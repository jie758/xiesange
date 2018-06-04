package com.xiesange.web.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pingplusplus.model.Charge;
import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.component.VCodeCmp;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.notify.NotifyTargetHolder;
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
import com.xiesange.gen.dbentity.groupbuy.Groupbuy;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.field.summary.IStatQueryField;
import com.xiesange.orm.statement.field.summary.SumQueryField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.GroupbuyCmp;
import com.xiesange.web.component.OrderCmp;
import com.xiesange.web.component.ProdCmp;
import com.xiesange.web.component.PromotionCmp;
import com.xiesange.web.define.ConsDefine;
import com.xiesange.web.define.ErrorDefine;
import com.xiesange.web.define.OrderStatusDefine;
import com.xiesange.web.define.ParamDefine;
import com.xiesange.web.promotion.AbstractPromotionBean;
import com.xiesange.web.promotion.PromotionManager;
@ETServiceAnno(name="groupbuy",version="")
public class GroupbuyService extends AbstractService {
	/**
	 * 查询组团详情，包括对应的的产品团购规则
	 * @param context
	 * 			groupbuy_id，
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryDetail(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Groupbuy.groupbuy_id
		);
		long groupbuyId = reqbody.getLong(ParamDefine.Groupbuy.groupbuy_id);
		long userId = context.getAccessUserId();
		Groupbuy gb = dao().queryById(Groupbuy.class, groupbuyId);
		
		String[] prodInfo = gb.getProductInfo().split(",");
		Set<Long> prodIdSet = ClassUtil.newSet();
		for(String info : prodInfo){
			String[] items = info.split(":");
			prodIdSet.add(Long.valueOf(items[0]));
		}
		List<Product> prodList = dao().queryByIds(Product.class, prodIdSet,
				Product.JField.id,
				Product.JField.name,
				//Product.JField.origPrice,
				//Product.JField.price,
				Product.JField.spec,
				Product.JField.unit,
				Product.JField.summary,
				//Product.JField.premise,
				Product.JField.pic
		);
		for(Product prod : prodList){
			for(String info : prodInfo){
				String[] items = info.split(":");
				if(prod.getId().longValue() == Long.valueOf(items[0])){
					prod.addAttribute("price", items[1]);
					prod.setPrice(null);//单位元
					prod.setPic(ETUtil.buildPicUrl(prod.getPic()));
					//ProdCmp.transfer(prod, false);
					break;
				}
			}
		}
		gb.setProductInfo(null);
		
		Orders payedOrder= dao().querySingle(Orders.class, 
				new DBCondition(Orders.JField.userId,userId),
				new DBCondition(Orders.JField.groupbuyId,groupbuyId),
				new DBCondition(Orders.JField.status,OrderStatusDefine.CUST.PAYED.state())
		);
		
		
		return new ResponseBody("groupbuy",gb)
					.add("productList", prodList)
					.add("hasPayed", payedOrder != null ? 1 : 0)
					//.add("orderItemList", itemList)
					//.add("order", order)
					;
	}
	
	
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
			ParamDefine.Product.product_id,
			ParamDefine.Order.amount,
			ParamDefine.Order.address,
			ParamDefine.Common.mobile,
			ParamDefine.Common.name,
			ParamDefine.Groupbuy.promotion_id
		);
		String name = reqbody.getString(ParamDefine.Common.name);
		String mobile = reqbody.getString(ParamDefine.Common.mobile);
		String address = reqbody.getString(ParamDefine.Order.address);
		long productId = reqbody.getLong(ParamDefine.Product.product_id);
		long promotionId = reqbody.getLong(ParamDefine.Groupbuy.promotion_id);
		int amount = reqbody.getInt(ParamDefine.Order.amount);
		
		Promotion promotion = dao().queryById(Promotion.class,promotionId);
		if(promotion == null || promotion.getStatus() == 0){
			throw ETUtil.buildException(ErrorDefine.GROUPBUY_RULE_EXPIRED);
		}
		Product product = dao().queryById(Product.class,productId);
		long groupbuyPrice = Long.valueOf(promotion.getValue());
		
		//创建一条对应的订单记录
		Orders orderEntity = GroupbuyCmp.buildOrder(0,mobile,name,address);
		orderEntity.setPromotionId(promotion.getId());
		orderEntity.setExpressSum(0L);
		orderEntity.setOrigSum(groupbuyPrice*amount);
		orderEntity.setSum(orderEntity.getOrigSum());
		orderEntity.setExpireTime(DateUtil.offsetDate(DateUtil.now(),promotion.getValidity()));
		orderEntity.setGroupbuyId(-1L);
		DBHelper.getDao().insert(orderEntity);
		
		OrdersItem orderItem = new OrdersItem();
		orderItem.setOrderId(orderEntity.getId());
		orderItem.setUserId(orderEntity.getUserId());
		orderItem.setPrice(groupbuyPrice);
		orderItem.setAmount(amount);
		orderItem.setSum(groupbuyPrice*amount);
		orderItem.setProductId(productId);
		orderItem.setCostPrice(product.getCostPrice());
		DBHelper.getDao().insert(orderItem);
		
		
		
		//如果首次购买，那么需要把地址信息更新到该用户
		User loginUser = context.getAccessUser();
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
		
		return new ResponseBody("newid", orderEntity.getId());
	}
	
	
	
	/**
	 * 修改订单信息。如果有涉及到收件人手机，必须输入验证码 
	 * @param context
	 * 			order_id,需要修改的订单,非开单者只能修改自己的订购数量，不能修改寄送信息
	 * 			name,
	 * 			mobile,
	 * 			vcode,
	 * 			address,
	 * 			amount,订购的数量
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
		Integer amount = reqbody.getInt(ParamDefine.Order.amount);
		
		if(orderEntity.getGroupbuyId() == -1){
			//开团者才能修改寄送信息
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
		}
		
		
		if(amount != null){
			OrdersItem itemEntity = new OrdersItem();
			itemEntity.setAmount(amount);
			dao().update(itemEntity, 
					new DBCondition(OrdersItem.JField.orderId,orderId),
					new DBCondition(OrdersItem.JField.userId,context.getAccessUserId()));
		}
		
		return null;
	}
	
	/**
	 * 查询某个产品团购规则
	 * @param context
	 * 			product_id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryGroupbuyRule(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Product.product_id
		);
		
		long prodId = reqbody.getLong(ParamDefine.Product.product_id);
		Product prodEntity = dao().queryById(Product.class, prodId);
		ProdCmp.transfer(prodEntity,false);
		
		//查询团购促销规则
		Promotion prom = PromotionCmp.queryGroupbuyPromotion(ConsDefine.PROMOTION_TYPE.TOGETHER,prodId,true);
		ETUtil.clearDBEntityExtraAttr(prom);
		//int validity = prom.getValidity();
		//Date expireDate = DateUtil.offsetDate(context.getRequestDate(), validity);
		//ProdCmp.appendPics(prodEntity);
		
		//查询月销量
		Date now = DateUtil.now();
		Date firstDay = DateUtil.getDayBegin(DateUtil.getFirstDateOfMonth(now));
		Date nextMonthFirstDay = DateUtil.offsetMonth(firstDay, 1);
		List<BigDecimal> totalAmounts = dao().queryStat(OrdersItem.class, new IStatQueryField[]{new SumQueryField(OrdersItem.JField.amount,"totalAmount")}, 
				new DBCondition(OrdersItem.JField.productId,prodId),
				new DBCondition(OrdersItem.JField.createTime,firstDay,DBOperator.GREAT_EQUALS),
				new DBCondition(OrdersItem.JField.createTime,nextMonthFirstDay,DBOperator.LESS)
		);
		if(NullUtil.isNotEmpty(totalAmounts) && totalAmounts.get(0) != null){
			prodEntity.addAttribute("totalAmount", totalAmounts.get(0).longValue());
		}
		return new ResponseBody("product",prodEntity)
					.add("promotion", prom);
	}
	
	/**
	 * 新用户团购跟单
	 * @param context
	 * 			order_id,
	 * 			amount,
	 * 			name,
	 * 			mobile
	 * @return
	 * @throws Exception
	 */
	public ResponseBody follow(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Groupbuy.groupbuy_id,
			ParamDefine.Common.name,
			ParamDefine.Common.mobile,
			ParamDefine.Order.address
		);
		long groupbuyId = reqbody.getLong(ParamDefine.Groupbuy.groupbuy_id);
		//Orders creatorOrderEntity = OrderCmp.checkExist(orderId,true);
		String name = reqbody.getString(ParamDefine.Common.name);
		String mobile = reqbody.getString(ParamDefine.Common.mobile);
		String address = reqbody.getString(ParamDefine.Order.address);
		long userId = context.getAccessUserId();
		//查询团购单是否有效
		Groupbuy gb = GroupbuyCmp.checkGroupbuy(groupbuyId);
		
		//查询是否重复跟单
		/*Orders currentOrder = dao().querySingle(Orders.class, 
			new DBCondition(Orders.JField.groupbuyId,groupbuyId),
			new DBCondition(Orders.JField.userId,userId)
		);*/
		//存在且已支付，说明已经加入
		/*if(currentOrder != null && currentOrder.getStatus() == 1){
			throw ETUtil.buildException(ErrorDefine.GROUPBUY_DUPLICATE_JOIN);
		}*/
		
		long orderId = dao().getSequence(Orders.class);
		String[] prodInfo = gb.getProductInfo().split(",");
		List<OrdersItem> items = reqbody.getDBEntityList(ParamDefine.Order.items, OrdersItem.class);
		long totalSum = 0;
		for(OrdersItem item : items){
			for(String info : prodInfo){
				String[] stritems = info.split(":");
				if(item.getProductId().longValue() == Long.valueOf(stritems[0])){
					item.setPrice(ETUtil.parseYuan2Fen(Float.valueOf(stritems[1])));
					item.setSum(item.getAmount()*item.getPrice());
					item.setOrderId(orderId);
					totalSum += item.getSum();
					break;
				}
			}
		}
		
		/*if(currentOrder != null){
			//为了方便处理，修改状态下，把原有订单先删除，后续重新插入，但保持id不变
			dao().deleteById(Orders.class, orderId);
			dao().delete(OrdersItem.class, new DBCondition(OrdersItem.JField.orderId,currentOrder.getId()));
		}*/
		Orders newOrderEntity = GroupbuyCmp.buildOrder(groupbuyId,mobile,name,address);
		newOrderEntity.setId(orderId);
		newOrderEntity.setExpressSum(0L);
		newOrderEntity.setOrigSum(totalSum);
		newOrderEntity.setSum(totalSum);
		DBHelper.getDao().insert(newOrderEntity);
		DBHelper.getDao().insertBatch(items);
		
		User updateUser = new User();
		User loginUser = context.getAccessUser();
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
		
		return new ResponseBody("orderId",orderId);
	}
	
	/**
	 * 截单。本次团购方案结束并生效。不再接受新成员报名，之前未支付的成员也将视为自动放弃
	 * 1、只能由本次团购发起人截单
	 * 2、没有达到活动人数无法接单
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody close(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		long userId = context.getAccessUserId();
		List<Orders> orderList = GroupbuyCmp.checkGroupbuyOrders(orderId,true);
		
		Orders creatorOrder = null;
		OrdersItem creatorItem = null;
		Set<Long> userIds = ClassUtil.newSet();
		for(Orders order : orderList){
			userIds.add(order.getUserId());
			if(order.getGroupbuyId() == -1){
				creatorOrder = order;
				creatorItem = GroupbuyCmp.getOrdersItem(order);
			}
		}
		//只能创建者截单
		if(creatorOrder == null || creatorOrder.getUserId() != userId){
			throw ETUtil.buildException(BaseErrorDefine.SYS_NOT_ALLOWED);
		}
		
		Product prodEntity = ProdCmp.queryProduct(creatorItem.getProductId());
		creatorItem.addAttribute("product", prodEntity);
		
		Promotion promEntity = dao().queryById(Promotion.class,creatorOrder.getPromotionId());
		AbstractPromotionBean promBean = PromotionManager.getPromotionBean(promEntity);
		
		boolean isMatch = promBean.isMatched(orderList);
		
		if(!isMatch){
			throw ETUtil.buildException(ErrorDefine.GROUPBUY_NOT_MATCHED);
		}
		//发送团购成员通知
		List<User> userList = dao().queryByIds(User.class, userIds);
		NotifyTargetHolder target = new NotifyTargetHolder();
		for(Orders order : orderList){
			User user = null;
			for(User u : userList){
				if(u.getId().longValue() == order.getUserId()){
					user = u;
					break;
				}
			}
			if(order.getStatus() == OrderStatusDefine.CUST.PAYED.state()){
				target.addMobile(user.getMobile()).addOpenid(user.getWechat());
			}
		}
		
		/*NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.groupbuy_close,
				new KeyValueHolder(Groupbuy_Close.groupbuy_name.name(),promBean.buildText())
					.addParam(Groupbuy_Close.creator_name.name(),creatorOrder.getName())
					.addParam(Groupbuy_Close.number.name(), orderList.size())
					.addParam(Groupbuy_Close.product_name.name(), prodEntity.getName())
					.addParam(Groupbuy_Close.order_id.name(), creatorOrder.getId())
				,
				target);
		*/
		OrderCmp.changeStatus(creatorOrder, OrderStatusDefine.CUST.GROUPBUY_CLOSE.state(), null);
		
		return null;
	}
	
	/**
	 * 某个团购成员发起支付，创建支付元素
	 * @param context
	 * @return
	 * @throws Exception
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
		
		Orders orderEntity = OrderCmp.checkExist(orderId, true);
		CCP.checkOperateOwner(context, orderEntity.getUserId());
		
		if (orderEntity.getStatus() != OrderStatusDefine.CUST.EDIT.state()) {
			throw ETUtil.buildException(ErrorDefine.ORDER_HAS_PAYED);
		}
		
		if (orderEntity.getExpireTime().before(context.getRequestDate())) {
			throw ETUtil.buildException(ErrorDefine.GROUPBUY_NOTOPEN);
		}
		
		OrdersItem currentItem = ((List<OrdersItem>)orderEntity.getAttr("items")).get(0);
		
		//找到产品信息
		Product prodEntity = ProdCmp.queryProduct(currentItem.getProductId());
		if(prodEntity == null){
			throw ETUtil.buildInvalidOperException();
		}
		
		currentItem.addAttribute("product", prodEntity);
		
		List<Orders> orderList = ClassUtil.newList();
		orderList.add(orderEntity);
		
		PromotionCmp.applyPromotion(orderList,orderEntity.getPromotionId());
		
		long paySum = orderEntity.getSum()+orderEntity.getExpressSum();
		//查询团购促销规则
		if(paySum != sum){
			//如果和前台传过来的不匹配，说明价格已有变化，需要告知前台重新确认
			throw ETUtil.buildException(ErrorDefine.ORDER_PRICE_CHANGED);
		}
		
		dao().updateById(orderEntity, orderEntity.getId());
		dao().updateById(currentItem, currentItem.getId());
		
		//开始调用ping+创建支付charge
		String title = BaseConsDefine.APP_NAME + "-团购-" + orderEntity.getCode();
		PINGPP_CHANNEL pingppChannel = PingppUtil.trans2PingppChannel(payChannel);
		Map<String,Object> extra = PingppUtil.createExtra(orderEntity, context.getAccessUser(), pingppChannel);
		/*Map<String,String> metadata = ClassUtil.newMap();
		metadata.put("orderItemId", String.valueOf(currentItem.getId()));*/
		
		Map<String,String> metadata = ClassUtil.newMap();
		metadata.put("orderId", String.valueOf(orderId));
		metadata.put("sum", String.valueOf(orderEntity.getSum()));
		metadata.put("origSum", String.valueOf(orderEntity.getOrigSum()));
		metadata.put("expressSum", String.valueOf(orderEntity.getExpressSum()));
		metadata.put("couponId", String.valueOf(orderEntity.getCouponId()));
		/*if(itemCost.length() > 0){
			metadata.put("itemCost", itemCost.substring(1));
		}*/
		
		Charge chg = PingppUtil.createCharge(
				DateUtil.now_yyyymmddhhmmss()+orderId,
				orderEntity.getSum()+orderEntity.getExpressSum(),
				title,
				pingppChannel,
				extra,
				null);
		return new ResponseBody("charge", chg);
	}
	
	/**
	 * 查询组团详情，包括对应的的产品团购规则
	 * @param context
	 * 			order_id，开单者的orderId
	 * 			only_self,是否只查询出当前本身的订单明细条目
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody queryDetail(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long loginUserId = context.getAccessUserId();
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		Long onlySelf = reqbody.getLong(ParamDefine.Order.only_self);
		
		List<Orders> orderList = GroupbuyCmp.checkGroupbuyOrders(orderId,true);
		Orders creatorOrder = orderList.get(0);
		OrdersItem firstItem = GroupbuyCmp.getOrdersItem(creatorOrder);//(OrdersItem)creatorOrder.getAttr("item");
		boolean isCreator = creatorOrder.getUserId()==loginUserId;
		boolean isAdmin = UserCmp.isAdmin(context.getAccessUser().getRole());
		//查询评论
		long commentCount = dao().queryCount(OrdersComment.class, 
				new DBCondition(OrdersComment.JField.productId,firstItem.getProductId()),
				new DBCondition(OrdersComment.JField.status,1));
		
		//查询月销量
		Date now = DateUtil.now();
		Date firstDay = DateUtil.getDayBegin(DateUtil.getFirstDateOfMonth(now));
		Date nextMonthFirstDay = DateUtil.offsetMonth(firstDay, 1);
		
		List<BigDecimal> totalAmounts = dao().queryStat(OrdersItem.class, new IStatQueryField[]{new SumQueryField(OrdersItem.JField.amount,"totalAmount")}, 
				new DBCondition(OrdersItem.JField.productId,firstItem.getProductId()),
				new DBCondition(OrdersItem.JField.createTime,firstDay,DBOperator.GREAT_EQUALS),
				new DBCondition(OrdersItem.JField.createTime,nextMonthFirstDay,DBOperator.LESS)
		);
		
		Product prodEntity = ProdCmp.queryProduct(firstItem.getProductId(),
				Product.JField.id,
				Product.JField.name,
				Product.JField.origPrice,
				Product.JField.price,
				Product.JField.unit,
				Product.JField.pic,
				Product.JField.spec,
				Product.JField.summary);
		if(onlySelf == null || onlySelf != 1){
			//表示是支付界面，那么需要查询出图片,轮播图需要用
			ProdCmp.appendPics(prodEntity);
		}
		prodEntity.addAttribute("totalAmount", totalAmounts.get(0)==null?0:totalAmounts.get(0).longValue());
		prodEntity.addAttribute("commentCount", commentCount);
		firstItem.addAttribute("product", prodEntity);
		
		//查询团购促销规则
		Promotion prom = DBHelper.getDao().queryById(Promotion.class, creatorOrder.getPromotionId());
		AbstractPromotionBean promBean = PromotionManager.getPromotionBean(prom);
		//promBean.apply(orderList);
		if(promBean.isMatched(orderList)){
			prom.addAttribute("isMatched", 1);
		}
		
		//PromotionCmp.applyPromotion(orderList,prom);
		//PromotionCmp.transfer(prom);
		
		
		Orders selfOrder = null;
		for(Orders order : orderList){
			OrdersItem item = GroupbuyCmp.getOrdersItem(order);
			if(order.getUserId() == loginUserId){
				selfOrder = order;
				selfOrder.addAttribute("isSelf", 1);
			}else if(order.getGroupbuyId() != -1){
				order.setAddress(null);//非发起者条目，需要把地址移除
			}
			if(!isAdmin && !isCreator && order.getGroupbuyId() != -1){
				//如果当前查看用户非发起者，那么需要把姓名和电话都打上星号，处于隐私考虑
				order.setName(ETUtil.maskName(order.getName()));
				order.setMobile(ETUtil.maskMobile(order.getMobile()));
			}
			if(order.getId().longValue() == orderId){
				creatorOrder = order;
			}
			
			ETUtil.clearDBEntityExtraAttr(item);
			ETUtil.clearDBEntityExtraAttr(order);
			
			OrderCmp.transferItem(item, false);
			OrderCmp.transfer(order);
		}
		//非开单者查看的时候，如果开单者未支付表示该团购单未开团
		if(creatorOrder.getUserId() != loginUserId && creatorOrder.getStatus() == 0){
			throw ETUtil.buildException(ErrorDefine.GROUPBUY_NOTOPEN);
		}
		
		ProdCmp.transfer(prodEntity,false);
		ETUtil.clearDBEntityExtraAttr(prodEntity);
		
		boolean isOpen = (creatorOrder.getStatus() == OrderStatusDefine.CUST.EDIT.state()
					|| creatorOrder.getStatus() == OrderStatusDefine.CUST.PAYED.state());
		
		if(onlySelf != null && onlySelf == 1 && NullUtil.isNotEmpty(orderList)){
			orderList.clear();
			if(selfOrder != null){
				orderList.add(selfOrder);
			}
		}
		
		return new ResponseBody("orderList",orderList)
					.add("product", prodEntity)
					.add("promotion", prom)
					.add("isJoined", selfOrder==null?0:1)
					.add("isCreator",isCreator?1:0)
					.add("isClosed",isOpen?0:1 )
					.add("expireTime", creatorOrder.getExpireTime())
					;
	}*/
	
	/**
	 * 发送到货通知
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody sendArrivalNotify(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody,
			ParamDefine.Order.order_id
		);
		long loginUserId = context.getAccessUserId();
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		
		List<Orders> orderList = GroupbuyCmp.checkGroupbuyOrders(orderId,true);
		
		Orders loginUserOrder = null;//找到发起人这个订单
		Set<Long> userIds = ClassUtil.newSet();
		for(Orders orderEntity : orderList){
			if(orderEntity.getUserId() == loginUserId){
				loginUserOrder = orderEntity;
			}
			userIds.add(orderEntity.getUserId());
		}
		
		//非团购模式，或者不是团长，不能发送通知
		if(loginUserOrder == null 
				|| loginUserOrder.getBuyType() != ConsDefine.ORDER_BUYTYPE.TOGETHERBUY.value()
				|| loginUserOrder.getGroupbuyId() != -1){
			throw ETUtil.buildInvalidOperException();
		}
		
		short orderStatus = loginUserOrder.getStatus();
		//只有在截单、打包、配送状态才可以对团员发送
		if(orderStatus != OrderStatusDefine.CUST.GROUPBUY_CLOSE.state()
				&& orderStatus != OrderStatusDefine.CUST.PACKING.state()
				&& orderStatus != OrderStatusDefine.CUST.DELIVERYING.state()){
			throw ETUtil.buildAuthException();
		}
		
		List<User> userList = dao().queryByIds(User.class,userIds);
		if(NullUtil.isEmpty(userList)){
			return null;
		}
		List<OrdersItem> items = OrderCmp.appendItems(loginUserOrder);
		List<Product> products = OrderCmp.appendProducts(items,true);
		Product prodEntity = products.get(0);
		
		Promotion promEntity = dao().queryById(Promotion.class,loginUserOrder.getPromotionId());
		AbstractPromotionBean promBean = PromotionManager.getPromotionBean(promEntity);
		
		
		NotifyTargetHolder target = new NotifyTargetHolder();
		
		for(Orders order : orderList){
			User user = null;
			for(User u : userList){
				if(u.getId().longValue() == order.getUserId()){
					user = u;
					break;
				}
			}
			if(order.getStatus() == OrderStatusDefine.CUST.EDIT.state()){
				continue;
			}
			target.addMobile(user.getMobile()).addOpenid(user.getWechat());
		}
		
		/*NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.groupbuy_arrival,
			new KeyValueHolder(Groupbuy_Arrival.groupbuy_name.name(),promBean.buildText())
				.addParam(Groupbuy_Arrival.creator_name.name(),loginUserOrder.getName())
				.addParam(Groupbuy_Arrival.creator_mobile.name(), loginUserOrder.getMobile())
				.addParam(Groupbuy_Arrival.product_name.name(), prodEntity.getName())
				.addParam(Groupbuy_Arrival.address.name(), loginUserOrder.getAddress())
				.addParam(Groupbuy_Arrival.order_id.name(), loginUserOrder.getId())
			,
			target);*/
		
		return null;
	} 
}
