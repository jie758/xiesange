package com.xiesange.web.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pingplusplus.model.Charge;
import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.component.VCodeCmp;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.PingppUtil;
import com.xiesange.baseweb.util.PingppUtil.PINGPP_CHANNEL;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersComment;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.gen.dbentity.user.UserCoupon;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.BargainCmp;
import com.xiesange.web.component.CouponCmp;
import com.xiesange.web.component.OrderCmp;
import com.xiesange.web.component.ProdCmp;
import com.xiesange.web.component.PromotionCmp;
import com.xiesange.web.component.UserCmp;
import com.xiesange.web.define.ConsDefine;
import com.xiesange.web.define.ErrorDefine;
import com.xiesange.web.define.OrderStatusDefine;
import com.xiesange.web.define.ParamDefine;
import com.xiesange.web.promotion.AbstractPromotionBean;
import com.xiesange.web.promotion.PromotionManager;
@ETServiceAnno(name="order",version="")
public class OrderService extends AbstractService {

	/**
	 * 下单前的确认，包含下面操作：
	 * 1、检查订单产品的价格变动。如果有变动则需要提示客户重新确认
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody create(WebRequestContext context) throws Exception{
		OrderCmp.checkSwitch();//检查当前开关是否开启
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.items,
			ParamDefine.Order.address,
			ParamDefine.Common.mobile,
			ParamDefine.Common.name
		);
		String name = reqbody.getString(ParamDefine.Common.name);
		String mobile = reqbody.getString(ParamDefine.Common.mobile);
		String address = reqbody.getString(ParamDefine.Order.address);
		List<OrdersItem> items = reqbody.getDBEntityList(ParamDefine.Order.items, OrdersItem.class);
		long totalSum = 0;
		//int totalAmount = 0;
		Product prodEntity = null;
		long orderId = dao().getSequence(Orders.class);
		/*if(sum == 0){
			throw ETUtil.buildInvalidOperException();
		}*/
		//因为前端传过来的price是元，所以可能会有小数点，这会导致数据结构里的long型的price解析出错，所以暂时用了一个_price作为额外属性，这里再转成分
		OrderCmp.appendProducts(items,true);//
		List<Product> prodList = ClassUtil.newList();
		for(OrdersItem orderItem : items){
			prodEntity = (Product)orderItem.getAttr("product");
			//prodList.add(prodEntity);
			
			if(prodEntity == null || orderItem.getAmount() == 0)
				continue;
			//这个price传过来是用户检测价格变动的。因为前端传过来的price是元，所以可能会有小数点，这会导致数据结构里的long型的price解析出错，所以暂时用了一个_price作为额外属性，这里再转成分
			orderItem.setPrice(ETUtil.parseYuan2Fen(Float.parseFloat(String.valueOf(orderItem.getAttr("_price")))));
			orderItem.setCostPrice(prodEntity.getCostPrice());
			orderItem.setSum(orderItem.getAmount() * orderItem.getPrice());
			
			totalSum += orderItem.getSum();
			//totalAmount += orderItem.getAmount();
			orderItem.setOrderId(orderId);
			orderItem.setUserId(context.getAccessUserId());
			
			Product prod = new Product();
			prod.setId(prodEntity.getId());
			prod.setPrice(prodEntity.getPrice());
			prodList.add(prod);
		}
		boolean priceChanged = OrderCmp.isPriceChanged(items);
		if(priceChanged && NullUtil.isNotEmpty(prodList)){
			for(Product prod : prodList){
				ProdCmp.transfer(prod,false);
			}
		}
		User loginUser = context.getAccessUser();
		Orders orderEntity = null;
		if(!priceChanged){
			//如果价格和后台一致，未变动则直接生成订单
			orderEntity = new Orders();
			orderEntity.setId(orderId);
			orderEntity.setBuyType(ConsDefine.ORDER_BUYTYPE.SINGLE.value());
			orderEntity.setUserId(context.getAccessUserId());
			orderEntity.setCode(OrderCmp.generateOrderCode(orderId));
			orderEntity.setMobile(mobile);
			orderEntity.setAddress(address);
			orderEntity.setName(name);
			orderEntity.setChannel(RequestUtil.getRequestChannel(context.getRequestHeader()));
			orderEntity.setStatus((short)0);
			orderEntity.setOrigSum(totalSum);
			orderEntity.setSum(totalSum);
			orderEntity.setExpressSum(0L);
			orderEntity.setGroupbuyId(-1L);
			
			dao().insert(orderEntity);
			dao().insertBatch(items);
			
			//如果首次购买，那么需要把地址信息更新到该用户
			User updateUser = new User();
			if(NullUtil.isEmpty(loginUser.getName())){
				updateUser.setName(name);
			}
			if(NullUtil.isEmpty(loginUser.getMobile())){
				updateUser.setMobile(mobile);
			}
			if(NullUtil.isEmpty(loginUser.getAddress())){
				updateUser.setAddress(address);
			}
			if(DBHelper.isModified(updateUser)){
				dao().updateById(updateUser, loginUser.getId());
			}
		}
		
		return new ResponseBody("priceChanged", priceChanged ? 1:0)
					.add("productList", prodList)
					.add("newid", orderId);
	}
	
	
	public ResponseBody preorder(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Product.product_id,
			ParamDefine.Order.amount,
			ParamDefine.Order.address,
			ParamDefine.Common.mobile,
			ParamDefine.Common.name/*,
			ParamDefine.Order.pay_channel*/
		);
		String name = reqbody.getString(ParamDefine.Common.name);
		String mobile = reqbody.getString(ParamDefine.Common.mobile);
		String address = reqbody.getString(ParamDefine.Order.address);
		//short payChannel = reqbody.getShort(ParamDefine.Order.pay_channel);
		long productId = reqbody.getLong(ParamDefine.Product.product_id);
		int amount = reqbody.getInt(ParamDefine.Order.amount);
		long orderSum = 10;//10元预订
		Product prodEntity = dao().queryById(Product.class, productId);
		long orderId = dao().getSequence(Orders.class);
		
		
		User loginUser = context.getAccessUser();
		Orders orderEntity = new Orders();
		orderEntity.setId(orderId);
		orderEntity.setBuyType(ConsDefine.ORDER_BUYTYPE.PREORDER.value());
		orderEntity.setUserId(context.getAccessUserId());
		orderEntity.setCode(OrderCmp.generateOrderCode(orderId));
		orderEntity.setMobile(mobile);
		orderEntity.setAddress(address);
		orderEntity.setName(name);
		orderEntity.setChannel(RequestUtil.getRequestChannel(context.getRequestHeader()));
		orderEntity.setStatus((short)0);
		orderEntity.setOrigSum(prodEntity.getPrice()*amount);
		orderEntity.setSum(orderSum);
		orderEntity.setExpressSum(0L);
		orderEntity.setGroupbuyId(-1L);
		dao().insert(orderEntity);
		
		OrdersItem orderItem = new OrdersItem();
		orderItem.setAmount(amount);
		orderItem.setOrderId(orderEntity.getId());
		orderItem.setProductId(productId);
		orderItem.setPrice(prodEntity.getPrice());
		orderItem.setAmount(amount);
		orderItem.setSum(prodEntity.getPrice()*amount);
		dao().insert(orderItem);
		
		//如果首次购买，那么需要把地址信息更新到该用户
		User updateUser = new User();
		if(NullUtil.isEmpty(loginUser.getName())){
			updateUser.setName(name);
		}
		if(NullUtil.isEmpty(loginUser.getMobile())){
			updateUser.setMobile(mobile);
		}
		if(NullUtil.isEmpty(loginUser.getAddress())){
			updateUser.setAddress(address);
		}
		if(DBHelper.isModified(updateUser)){
			dao().updateById(updateUser, loginUser.getId());
		}
		
		/*//开始调用ping+创建支付charge
		String title = BaseConsDefine.APP_NAME + "-" + orderEntity.getCode();
		
		PINGPP_CHANNEL pingppChannel = PingppUtil.trans2PingppChannel(payChannel);
		Map<String,Object> extra = PingppUtil.createExtra(orderEntity, context.getAccessUser(), pingppChannel);
		Map<String,String> metadata = ClassUtil.newMap();
		
		Charge chg = PingppUtil.createCharge(
				orderEntity,
				title,
				pingppChannel,
				extra,
				metadata);
		return new ResponseBody("charge", chg)
					.add("newid", orderEntity.getId());*/
		
		
		return new ResponseBody("newid",orderEntity.getId())
					.add("sum", ETUtil.parseFen2Yuan(orderSum));
	}
	
	
	/**
	 * 创建订单支付元素
	 * @param context
	 * 			order_id,订单id
	 * 			pay_channel,支付渠道
	 * 			coupon_id,优惠券id
	 * 			sum,前端计算出来的需要支付的实际金额，如果和后端计算出来，不一致则要提醒前端重新确认
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:19:04
	 */
	public ResponseBody createPayByPingpp(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id,
			ParamDefine.Order.pay_channel,
			ParamDefine.Order.sum
			
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id); 
		short payChannel = reqbody.getShort(ParamDefine.Order.pay_channel);
		float sumFloat = reqbody.getFloat(ParamDefine.Order.sum);
		long sum = ETUtil.parseYuan2Fen(sumFloat);
		Long couponId = reqbody.getLong(ParamDefine.Order.coupon_id); 
		Short needBargain = reqbody.getShort(ParamDefine.Order.need_bargain);
		
		Orders orderEntity = OrderCmp.checkExist(orderId, true);
		CCP.checkOperateOwner(context, orderEntity.getUserId());
		
		// 只有在草稿状态(即待支付状态)下才能支付
		short status = orderEntity.getStatus();
		boolean canPay = (status == OrderStatusDefine.CUST.EDIT.state() 
				|| status == OrderStatusDefine.CUST.ADJUST_PRICE.state()
				|| OrderCmp.isPreorderBuy(orderEntity.getBuyType()));
		if (!canPay) {
			throw ETUtil.buildException(ErrorDefine.ORDER_HAS_PAYED);
		}
		
		List<OrdersItem> items = (List<OrdersItem>)orderEntity.getAttr("items");
		OrderCmp.appendProducts(items, true);
		//OrderCmp.isPriceChanged(items);
		//OrderCmp.refreshSum(orderEntity, items);
		long paySum = 0L;
		//如果是未支付状态,需要计算各种优惠
		/*if(OrderCmp.isPreorderBuy(orderEntity.getBuyType())){
			long orderSum = orderEntity.getSum();//定金
			if(orderEntity.getStatus() == 0){
				//付定金
				paySum = orderSum;
			}else{
				//补余款
				orderEntity.setSum(orderEntity.getOrigSum());
				List<Promotion> globalPromList = PromotionCmp.queryGlobalPromotions();
				UserCoupon couponEntity = couponId==null?null:CouponCmp.checkById(couponId);
				paySum = OrderCmp.calcPaySum(orderEntity, items, globalPromList, couponEntity);
				paySum -= orderSum;
				orderEntity.setSum(paySum);
				paySum += orderEntity.getExpressSum();
			}
			
		}else if(OrderCmp.isTogetherbuy(orderEntity.getBuyType())){
			//如果是拼单,则应用拼单规则
			Promotion prom = DBHelper.getDao().queryById(Promotion.class, orderEntity.getPromotionId());
			List<Orders> orderList = ClassUtil.newList();
			orderList.add(orderEntity);
			
			AbstractPromotionBean promBean = PromotionManager.getPromotionBean(prom);
			promBean.apply(orderList);
			paySum = orderEntity.getSum();
		}else*/ if(needBargain != null && needBargain == 1){
			//应用砍价，砍价和其它活动互斥
			OrderCmp.refreshSum(orderEntity, items);
			paySum = BargainCmp.apply(orderId,orderEntity.getOrigSum());
			orderEntity.setSum(paySum);
			long expressFee = OrderCmp.calcExpressfee(paySum);
			orderEntity.setExpressSum(expressFee);
			
			paySum += expressFee;
		}else{
			List<Promotion> globalPromList = PromotionCmp.queryGlobalPromotions();
			UserCoupon couponEntity = couponId==null?null:CouponCmp.checkById(couponId);
			paySum = OrderCmp.calcPaySum(orderEntity, items, globalPromList, couponEntity);
			paySum += orderEntity.getExpressSum();
		}
		//paySum += orderEntity.getExpressFee();
		//logger.debug("from client : "+sum);
		
		if(paySum != sum){
			//如果和前台传过来的不匹配，说明价格已有变化，需要告知前台重新确认
			throw ETUtil.buildException(ErrorDefine.ORDER_PRICE_CHANGED);
		}
		
		//List<UpdateStatement> itemUpdateList = ClassUtil.newList();
		/*StringBuffer itemCost = new StringBuffer();
		List<OrdersItem> itemList = null;
		if(NullUtil.isNotEmpty(items)){
			//把每条明细的真实价格都记录下来
			itemList = ClassUtil.newList();
			for(OrdersItem item : items){
				Product prod = (Product)item.getAttr("product");
				item
				
				itemCost.append(",").append(item.getId()).append(":").append(prod.getCostPrice());
				OrdersItem itemUpdate = new OrdersItem();
				itemUpdate.setPrice(item.getPrice());
				itemUpdate.setCostPrice(prod.getCostPrice());
				itemUpdate.setSum(item.getSum());
				itemUpdateList.add(new UpdateStatement(itemUpdate,item.getId()));
			}
		}*/
		/*if(NullUtil.isNotEmpty(itemUpdateList)){
			dao().updateBatch(itemUpdateList.toArray(new UpdateStatement[itemUpdateList.size()]));
		}*/
		//开始调用ping+创建支付charge
		String title = BaseConsDefine.APP_NAME + "-" + orderEntity.getCode();
		
		PINGPP_CHANNEL pingppChannel = PingppUtil.trans2PingppChannel(payChannel);
		Map<String,Object> extra = PingppUtil.createExtra(orderEntity, context.getAccessUser(), pingppChannel);
		Map<String,String> metadata = ClassUtil.newMap();
		metadata.put("orderId", String.valueOf(orderId));
		metadata.put("sum", String.valueOf(orderEntity.getSum()));
		metadata.put("origSum", String.valueOf(orderEntity.getOrigSum()));
		metadata.put("expressSum", String.valueOf(orderEntity.getExpressSum()));
		metadata.put("couponId", orderEntity.getCouponId()==null?null:String.valueOf(orderEntity.getCouponId()));
		logger.debug(".............expressSum:"+orderEntity.getExpressSum());
		if(NullUtil.isNotEmpty(items)){
			//把每条明细的真实价格都记录下来
			List<OrdersItem> itemList = ClassUtil.newList();
			for(OrdersItem item : items){
				Product prod = (Product)item.getAttr("product");
				OrdersItem newitem = new OrdersItem();
				newitem.setId(newitem.getId());
				newitem.setPrice(prod.getPrice());
				newitem.setSum(sum);
				newitem.setCostPrice(prod.getCostPrice());
				
				itemList.add(newitem);
			}
		}
		
		/*if(itemCost.length() > 0){
			metadata.put("itemCost", itemCost.substring(1));
		}*/
		Charge chg = PingppUtil.createCharge(
				DateUtil.now_yyyymmddhhmmss()+orderId,
				paySum,
				title,
				pingppChannel,
				extra,
				metadata);
		return new ResponseBody("charge", chg);
		
		//return null;
	}
	
	
	/**
	 * 游客支付后该订单就处于已支付待导游确认状态 提交后，票券的单价、数量、时长和总金额都要记录到订单记录中
	 * 
	 * @param context
	 *            order_id,订单id pay_channel,支付渠道
	 * @return
	 * @throws Exception
	 */
	public ResponseBody completePayByPingpp(WebRequestContext context)
			throws Exception {
		HttpServletRequest request = context.getRequest();
		HttpServletResponse response = context.getResponse();
		request.setCharacterEncoding("UTF8");

		// 获取头部所有信息
		String pingpSign = request.getHeader("x-pingplusplus-signature");
		//logger.debug("=============x-pingplusplus-signature = "+pingpSign);

		String reqString = PingppUtil.parseWebhookRequestStr(request);
		//logger.debug("=============datastring = "+reqString);
		
		context.setInput(reqString);//可以记录到sys_sn表中
		
		PingppUtil.checkSignature(reqString, pingpSign);
		
		try{
			Charge charge = PingppUtil.parseWebhookCharge(reqString);
			Map<String,String> metaData = charge.getMetadata();
			/*logger.debug("---------orderId:"+orderId);
			logger.debug("---------expressFee:"+expressFee);
			logger.debug("---------couponId:"+couponId);*/
			
			OrderCmp.completeOrder(metaData,charge);
			
		}catch(Exception e){
			logger.error(e,e);
		}
		response.setStatus(200);

		return null;
	}
	
	/**
	 * 查询订单列表
	 * @param context
	 * 			status,状态
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryList(WebRequestContext context) throws Exception{
		long userId = context.getAccessUserId();
		Short status = context.getRequestBody().getShort(ParamDefine.Common.status);
		List<DBCondition> condList = ClassUtil.newList();
		if(status != null){
			condList.add(new DBCondition(Orders.JField.status,status));
		}
		condList.add(new DBCondition(Orders.JField.userId,userId));
		QueryStatement st = new QueryStatement(Orders.class,condList.toArray(new DBCondition[condList.size()]));
		st.appendOrderFieldDesc(Orders.JField.createTime);
		st.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()));
		
		List<Orders> orderList = dao().query(st);
		if(NullUtil.isEmpty(orderList)){
			return null;
		}
		
		List<OrdersItem> items = OrderCmp.appendItems(orderList,true,false);
		List<Product> prodList = OrderCmp.appendProducts(items, false);
		
		//添加优惠券对象
		OrderCmp.appendCoupons(orderList);
		
		//如果是未支付状态的，需要处理该订单的价格，因为要实时跟着产品价格变化
		Set<Long> promIds = ClassUtil.newSet();
		List<Promotion> globalPromList = PromotionCmp.queryGlobalPromotions();
		for(Orders order : orderList){
			if(OrderCmp.isTogetherbuy(order.getBuyType())){
				promIds.add(order.getPromotionId());
			}
			List<OrdersItem> orderitems = (List<OrdersItem>)order.getAttr("items");
			UserCoupon coupon = (UserCoupon)order.getAttr("coupon");
			long paySum = OrderCmp.calcPaySum(order, orderitems, globalPromList, coupon);
			order.setSum(paySum);
			
			ETUtil.clearDBEntityExtraAttr(order, Orders.JField.createTime);
			OrderCmp.transfer(order);
		}
		
		for(OrdersItem item : items){
			item.addAttribute("product", null);//把product清除掉，统一放在整个produtList列表中返回前台
			ETUtil.clearDBEntityExtraAttr(item);
			OrderCmp.transferItem(item,false);
		}
		
		List<Promotion> promList = NullUtil.isEmpty(promIds) ? null : dao().queryByIds(Promotion.class, promIds);
		if(NullUtil.isNotEmpty(promList)){
			for(Promotion promotion : promList){
				PromotionCmp.transfer(promotion);
			}
		}
		
		return new ResponseBody("orderList",orderList)
					.add("productList", prodList)
					.add("promotionList", promList);
	}
	
	/**
	 * 查询订单详情。适用于单购类型
	 * @param context
	 * 			order_id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryDetail(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		//Short needPromList = reqbody.getShort(ParamDefine.Order.need_promotion_list);
		Orders orderEntity = OrderCmp.checkExist(orderId,true);
		//只能当前用户操作,管理员除外
		if(!UserCmp.isAdmin(context.getAccessUser().getRole())){
			CCP.checkOperateOwner(context, orderEntity.getUserId());
		}
		boolean isEdit = orderEntity.getStatus() == OrderStatusDefine.CUST.EDIT.state();
		boolean isPreorder = OrderCmp.isPreorderBuy(orderEntity.getBuyType());
		
		long loginUserId = context.getAccessUserId();
		
		List<OrdersItem> items = (List<OrdersItem>)orderEntity.getAttr("items");
		OrderCmp.appendProducts(items, false);
		
		//计算整个订单金额
		OrderCmp.refreshSum(orderEntity, items);
		long paySum = orderEntity.getOrigSum();
		long finalSum = orderEntity.getSum();
		List<UserCoupon> couponList = null;
		List<Promotion> promotionList = null;
		if(isEdit || isPreorder){
			couponList = OrderCmp.queryCouponList(orderEntity);
		}else if(orderEntity.getCouponId() != null){
			UserCoupon coupon = CouponCmp.queryById(orderEntity.getCouponId());
			CouponCmp.transfer(coupon);
			ETUtil.clearDBEntityExtraAttr(coupon);
			couponList = ClassUtil.newList();
			couponList.add(coupon);
		}
		
		//优惠活动
		List<Promotion> globalPromList = PromotionCmp.queryGlobalPromotions();
		if(NullUtil.isNotEmpty(globalPromList)){
			paySum = OrderCmp.applyPromotion(globalPromList,orderEntity);
			if(isEdit){
				orderEntity.setSum(paySum);
			}
			//把符合条件的优惠活动过滤出来，传到前端
			promotionList = ClassUtil.newList();
			for(Promotion prom : globalPromList){
				Integer isMatched = (Integer)prom.getAttr("isMatched");
				if(isMatched != null && isMatched == 1){
					PromotionCmp.transfer(prom);
					promotionList.add(prom);
					ETUtil.clearDBEntityExtraAttr(prom);
				}
			}
		}
		
		for(OrdersItem item : items){
			Product prod = (Product)item.getAttr("product");
			
			prod.setSummary(null);
			prod.setOrderCount(null);
			//prod.setOrigPrice(null);
			prod.setPname(null);
			prod.setStatus(null);
			prod.setCommentTags(null);
			ProdCmp.transfer(prod, false);
			ETUtil.clearDBEntityExtraAttr(prod);
			
			
			OrderCmp.transferItem(item,false);
			ETUtil.clearDBEntityExtraAttr(item);
		}
		
		if(!isEdit){
			orderEntity.setSum(finalSum);
		}
		
		OrderCmp.transfer(orderEntity);
		ETUtil.clearDBEntityExtraAttr(orderEntity,Orders.JField.createTime);
		
		boolean isCreator = orderEntity.getUserId()==loginUserId;
		return new ResponseBody("order",orderEntity)
					.add("isCreator", isCreator?1:0)
					.add("promotionList", promotionList)
					.add("couponList", NullUtil.isEmpty(couponList)?null:couponList);
	}
	
	
	public ResponseBody queryComment(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		//Short needPromList = reqbody.getShort(ParamDefine.Order.need_promotion_list);
		Orders orderEntity = OrderCmp.checkExist(orderId,true);
		//只能当前用户操作
		CCP.checkOperateOwner(context, orderEntity.getUserId());
		
		List<OrdersItem> items = (List<OrdersItem>)orderEntity.getAttr("items");
		OrderCmp.appendProducts(items, false);
		
		for(OrdersItem item : items){
			Product prod = (Product)item.getAttr("product");
			
			prod.setSummary(null);
			prod.setOrderCount(null);
			//prod.setOrigPrice(null);
			prod.setPname(null);
			prod.setStatus(null);
			ProdCmp.transfer(prod, false);
			OrderCmp.transferItem(item,false);
			
			ETUtil.clearDBEntityExtraAttr(prod);
			ETUtil.clearDBEntityExtraAttr(item);
			
			String commentTagsStr = prod.getCommentTags();
			if(NullUtil.isNotEmpty(commentTagsStr)){
				String[] commentTags = commentTagsStr.split("\\|");
				prod.addAttribute("commentTags", commentTags);
				prod.setCommentTags(null);
			}
			
		}
		
		OrderCmp.transfer(orderEntity);
		ETUtil.clearDBEntityExtraAttr(orderEntity);
		
		
		return new ResponseBody("order",orderEntity);
	}
	
	
	/**
	 * 取消订单
	 * 配送前都可以取消
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody cancel(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		Orders orderEntity = OrderCmp.checkExist(orderId,false);
		
		CCP.checkOperateOwner(context, orderEntity.getUserId());
		
		//配送前都可以取消，配送之后不能取消
		if(orderEntity.getStatus() >= OrderStatusDefine.CUST.DELIVERYING.state()){
			throw ETUtil.buildException(ErrorDefine.ORDER_NOT_ALLOWED_CANCEL,"已在配送中");
		}
		
		orderEntity.setStatus(OrderStatusDefine.CUST.CANCELED.state());
		
		dao().updateById(orderEntity, orderId);
		
		
		return new ResponseBody("newStatus",OrderStatusDefine.CUST.CANCELED.state());
	} 
	
	public ResponseBody remove(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		Orders orderEntity = OrderCmp.checkExist(orderId,false);
		
		CCP.checkOperateOwner(context, orderEntity.getUserId());
		
		//只有未支付、调价、已完成几种状态才能删除
		short status = orderEntity.getStatus();
		if(status != OrderStatusDefine.CUST.EDIT.state() &&
				status != OrderStatusDefine.CUST.ADJUST_PRICE.state() && 
				status != OrderStatusDefine.CUST.COMPLETED.state()){
			throw ETUtil.buildInvalidOperException();
		}
		
		dao().deleteById(Orders.class, orderId);
		return null;
	}
	
	/**
	 * 修改订单信息。如果有涉及到收件人手机，必须输入验证码 
	 * @param context
	 * 			order_id,
	 * 			name,
	 * 			mobile,
	 * 			vcode,
	 * 			address,
	 * 			items
	 * @return
	 * @throws Exception
	 */
	public ResponseBody modify(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		Orders orderEntity = OrderCmp.checkExist(orderId,false);
		
		CCP.checkOperateOwner(context, orderEntity.getUserId());
		
		String name = reqbody.getString(ParamDefine.Common.name);
		String mobile = reqbody.getString(ParamDefine.Common.mobile);
		String vcode = reqbody.getString(ParamDefine.Common.vcode);
		String address = reqbody.getString(ParamDefine.User.address);
		List<OrdersItem> items = reqbody.getDBEntityList(ParamDefine.Order.items, OrdersItem.class);
		
		if(NullUtil.isNotEmpty(mobile)){
			VCodeCmp.checkMobileVCode(vcode, mobile);
			orderEntity.setMobile(mobile);
		}
		
		if(NullUtil.isNotEmpty(name)){
			orderEntity.setName(name);
		}
		if(NullUtil.isNotEmpty(address)){
			orderEntity.setAddress(address);
		}
		if(DBHelper.isModified(orderEntity)){
			dao().updateById(orderEntity, orderEntity.getId());
		}
		
		//处理明细
		if(NullUtil.isNotEmpty(items)){
			for(OrdersItem item : items){
				item.setOrderId(orderId);
			}
			dao().delete(OrdersItem.class, new DBCondition(OrdersItem.JField.orderId,orderId));
			dao().insertBatch(items);
		}
		
		return null;
	}
	
	public ResponseBody comment(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id,
			ParamDefine.Order.comment_items
		);
		long loginUserId = context.getAccessUserId();
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		Orders orderEntity = OrderCmp.checkExist(orderId,false);
		
		CCP.checkOperateOwner(context, orderEntity.getUserId());
		
		List<OrdersComment> cmtList = reqbody.getDBEntityList(ParamDefine.Order.comment_items, OrdersComment.class);
		
		for(OrdersComment cmt : cmtList){
			cmt.setUserId(loginUserId);
			cmt.setOrderId(orderId);
			cmt.setStatus((short)1);
		}
		dao().insertBatch(cmtList);
		
		orderEntity.setStatus(OrderStatusDefine.CUST.COMPLETED.state());
		dao().updateById(orderEntity,orderId);
		return null;
	}
	
}
