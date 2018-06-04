package com.xiesange.web.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.notify.NotifyCmp;
import com.xiesange.baseweb.notify.NotifyDefine;
import com.xiesange.baseweb.notify.NotifyTargetHolder;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.activity.ActivityJoin;
import com.xiesange.gen.dbentity.groupbuy.Groupbuy;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.gen.dbentity.user.UserCoupon;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.sql.DBOrCondition;
import com.xiesange.orm.statement.field.summary.CountQueryField;
import com.xiesange.orm.statement.field.summary.IStatQueryField;
import com.xiesange.orm.statement.field.summary.SumQueryField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.CouponCmp;
import com.xiesange.web.component.GroupbuyCmp;
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
@ETServiceAnno(name="manage",version="")
/**
 * 管理服务类，适用于内部管理人员
 * @author Wilson 
 * @date 上午9:48:42
 */
public class ManageService extends AbstractService {
	/**
	 * 查询产品列表
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryProductList(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		List<Product> prodList = dao().queryAll(Product.class);
		if(NullUtil.isEmpty(prodList)){
			return null;
		}
		for(Product prod : prodList){
			ProdCmp.transfer(prod,true);
		}
		
		/*List<BaseParam> paramList = SysparamCmp.getAllWebUsed();
		Map<String,Object> paramMap = ClassUtil.newMap();
		for(BaseParam param : paramList){
			//只需返回到前台code和value
			paramMap.put(param.getCode().toLowerCase(), param.getValue());
		}
		*/
		return new ResponseBody("productList",prodList);//.add("params", paramMap);
	}
	
	/**
	 * 修改产品资料。包括产品状态
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody modifyProduct(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Product.product_id
		);
		long prodId = reqbody.getLong(ParamDefine.Product.product_id);
		Product prodEntity = dao().queryById(Product.class, prodId);
		
		String name = reqbody.getString(ParamDefine.Product.name);
		String summary = reqbody.getString(ParamDefine.Product.summary);
		Float price = reqbody.getFloat(ParamDefine.Product.price);
		Float costPrice = reqbody.getFloat(ParamDefine.Product.cost_price);
		String spec = reqbody.getString(ParamDefine.Product.spec);
		Short status = reqbody.getShort(ParamDefine.Product.status);
		
		Product update = new Product();
		if(name != null){
			update.setName(name);
		}
		if(summary != null){
			update.setSummary(summary);
		}
		if(price != null){
			update.setPrice(ETUtil.parseYuan2Fen(price));
		}
		if(costPrice != null){
			update.setCostPrice(ETUtil.parseYuan2Fen(costPrice));
		}
		if(spec != null){
			update.setSpec(spec);
		}
		if(status != null){
			//如果状态有变化，只能做切换，做个判断
			if(prodEntity.getStatus() == status){
				throw ETUtil.buildInvalidOperException();
			}
			update.setStatus(status);
		}
		dao().updateById(update, prodId);
		return null;
	}
	
	/**
	 * 查询订单。
	 * @param context
	 * 			status,订单状态
	 * 			user_id,对应的用户id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryOrderList(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		List<DBCondition> condList = ClassUtil.newList();
		
		Short status = reqbody.getShort(ParamDefine.Order.status);
		Long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		Long userId = reqbody.getLong(ParamDefine.User.user_id);
		

		if(orderId != null){
			condList.add(new DBCondition(Orders.JField.id,orderId));
		}
		
		if(userId != null){
			condList.add(new DBCondition(Orders.JField.userId,userId));
		}
		
		if(status != null){
			//如果是过滤“已支付”，要考虑拼单状况，拼单要只显示“截单”状态的
			if(status == OrderStatusDefine.CUST.PAYED.state()){
				condList.add(new DBOrCondition(
					new DBCondition(
							new DBCondition(Orders.JField.status,OrderStatusDefine.CUST.PAYED.state()),
							new DBCondition(Orders.JField.buyType,ConsDefine.ORDER_BUYTYPE.SINGLE.value())
					),
					new DBCondition(
						new DBCondition(Orders.JField.status,OrderStatusDefine.CUST.GROUPBUY_CLOSE.state()),
						new DBCondition(Orders.JField.buyType,ConsDefine.ORDER_BUYTYPE.TOGETHERBUY.value()),
						new DBCondition(Orders.JField.groupbuyId,-1)
					))
				);
			}else{
				condList.add(new DBCondition(Orders.JField.status,status));
			}
			
		}
		//condList.add(new DBCondition(Orders.JField.groupbuyId,-1));
		
		DBCondition[] condArr = NullUtil.isEmpty(condList) ? null:
			condList.toArray(new DBCondition[condList.size()]);
		
		QueryStatement st = new QueryStatement(Orders.class,condArr);
		st.appendOrderFieldDesc(Orders.JField.createTime);
		st.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()));
		
		List<Orders> orderList = dao().query(st);
		if(NullUtil.isEmpty(orderList)){
			return null;
		}
		
		Set<Long> promIds = ClassUtil.newSet();
		Set<Long> userIds = ClassUtil.newSet();
		Set<Long> orderIds = ClassUtil.newSet();
		for(Orders order : orderList){
			if(OrderCmp.isTogetherbuy(order.getBuyType())){
				promIds.add(order.getPromotionId());
			}
			userIds.add(order.getUserId());
			orderIds.add(order.getId());
		}
		
		List<ActivityJoin> bargainStat = dao().query(new QueryStatement(ActivityJoin.class,
				new DBCondition(ActivityJoin.JField.activityId,orderIds,DBOperator.IN),
				new DBCondition(ActivityJoin.JField.type,ConsDefine.ACTIVITY_TYPE.ORDER_BARGAIN.value()))
					.appendGroupField(ActivityJoin.JField.activityId)
					.appendQueryField(ActivityJoin.JField.activityId,CountQueryField.getInstance("totalCount"))
		);
		
		List<OrdersItem> items = OrderCmp.appendItems(orderList,true,false);
		List<Product> prodList = OrderCmp.appendProducts(items, false);
		List<Promotion> promList = NullUtil.isEmpty(promIds)?null:dao().queryByIds(Promotion.class, promIds);
		List<User> userList = dao().queryByIds(User.class, userIds,User.JField.id,User.JField.orderCount);
		if(NullUtil.isNotEmpty(promList)){
			for(Promotion promotion : promList){
				PromotionCmp.transfer(promotion);
			}
		}
		//添加优惠券对象
		OrderCmp.appendCoupons(orderList);
		
		List<Promotion> globalPromList = PromotionCmp.queryGlobalPromotions();
		//如果是未支付状态的，需要处理该订单的价格，因为要实时跟着产品价格变化
		
		for(Orders order : orderList){
			for(User user : userList){
				if(user.getId().longValue() == order.getUserId()){
					//logger.debug("xxxxxxxxxxxx:"+order.getId());
					order.addAttribute("user", user);
					break;
				}
			}
			
			if(NullUtil.isNotEmpty(bargainStat)){
				for(ActivityJoin bargin : bargainStat){
					if(bargin.getActivityId().longValue() == order.getId()){
						BigDecimal totalCount = (BigDecimal)bargin.getAttr("totalCount");
						order.addAttribute("bargainCount",totalCount==null?0:totalCount.longValue());
						break;
					}
				}
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
		
		//统计总额和总记录数
		List<BigDecimal> stats = dao().queryStat(Orders.class,
				new IStatQueryField[]{CountQueryField.getInstance("count")},condArr);
		long totalCount = 0L;
		if(NullUtil.isNotEmpty(stats)){
			totalCount = stats.get(0).longValue();
		}
		return new ResponseBody("orderList",orderList)
						.add("productList", prodList)
						.add("promotionList", promList)
						.add("totalCount", totalCount)
					;
	}
	
	/**
	 * 查询需要生成采购订单的订单列表 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody queryPurchaseOrderList(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		//RequestBody reqbody = context.getRequestBody();
		List<DBCondition> condList = ClassUtil.newList();
		condList.add(new DBCondition(Orders.JField.status,OrderStatusDefine.CUST.PAYED.state()));
		DBCondition[] condArr = condList.toArray(new DBCondition[condList.size()]);
		
		QueryStatement st = new QueryStatement(Orders.class,condArr);
		st.appendOrderField(Orders.JField.createTime);//先订购的优先排列
		st.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()));
		
		List<Orders> orderList = dao().query(st);
		if(NullUtil.isEmpty(orderList)){
			return null;
		}
		
		for(Orders order : orderList){
			OrderCmp.transfer(order);
			ETUtil.clearDBEntityExtraAttr(order,Orders.JField.createTime);
		}
		
		//添加优惠券对象
		OrderCmp.appendCoupons(orderList);
		
		//统计总额和总记录数
		List<BigDecimal> stats = dao().queryStat(Orders.class,
				new IStatQueryField[]{new CountQueryField("count"),new SumQueryField(Orders.JField.sum,"sum")},condArr);
		long totalCount = 0L;
		long totalSum = 0L;
		if(NullUtil.isNotEmpty(stats)){
			totalCount = stats.get(0).longValue();
			totalSum = stats.get(1)==null?0:stats.get(1).longValue();
		}
		
		return new ResponseBody("orderList",orderList)
					.add("totalCount", totalCount)
					.add("totalSum", ETUtil.parseFen2Yuan(totalSum));
	}
	
	*//**
	 * 生成采购联系单
	 * @param context
	 * 			delivery_dates,配送日期，以英文逗号分隔
	 * 			status,订单状态
	 * @return
	 * @throws Exception
	 *//*
	public ResponseBody createPurchase(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		
		List<DBCondition> condList = ClassUtil.newList();
		
		String delayOrderIds = reqbody.getString(ParamDefine.Purchase.delay_order_ids);
		
		condList.add(new DBCondition(Orders.JField.status,OrderStatusDefine.CUST.PAYED.state()));
		
		if(NullUtil.isNotEmpty(delayOrderIds)){
			condList.add(new DBCondition(Orders.JField.id,delayOrderIds.split(","),DBOperator.NOT_IN));
		}
		
		DBCondition[] condArr = NullUtil.isEmpty(condList) ? null:
			condList.toArray(new DBCondition[condList.size()]);
		
		List<Orders> orderList = dao().query(Orders.class, condArr);
		if(orderList == null){
			return null;
		}
		if(NullUtil.isNotEmpty(orderList)){
			List<OrdersItem> items = OrderCmp.appendItems(orderList,true);
			OrderCmp.appendProducts(items,true);
		}
		
		//发送采购订单
		OrderCmp.sendPurchase(orderList);
		
		//把符合条件的订单记录状态改为配送中
		//Orders update = new Orders();
		//update.setStatus(OrderStatusDefine.CUST.DELIVERYING.state());
		//dao().update(update, condArr);
		return null;
	}
	*/
	/**
	 * 查询采购单明细。采购只会保存最新一份
	 * @param context
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody queryOrderList4Purchase(WebRequestContext context) throws Exception{
		QueryStatement st = new QueryStatement(OrdersPurchase.class);
		JoinPart joinPart = new JoinPart(Orders.class, new FieldPair(OrdersPurchase.JField.orderId,Orders.JField.id));
		st.appendJoin(joinPart);
		st.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()));
		
		List<JoinQueryData> joinQueryData = dao().queryJoin(st);
		if(NullUtil.isEmpty(joinQueryData)){
			return null;
		}
		Date purchaseTime = null;
		List<Orders> orderList = ClassUtil.newList();
		for(JoinQueryData data : joinQueryData){
			if(purchaseTime == null){
				purchaseTime = data.getResult(OrdersPurchase.class).getCreateTime();
			}
			OrderCmp.transfer(data.getResult(Orders.class));
			orderList.add(data.getResult(Orders.class));
		}
		return new ResponseBody("orderList",orderList)
				.add("purchaseTime", purchaseTime);
	}*/
	
	
	/**
	 * 把发货清单中的订单更新成打包中状态
	 * @param context
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody updateOrderList2Purchase(WebRequestContext context) throws Exception{
		Orders orderEntity = new Orders();
		orderEntity.setStatus(OrderStatusDefine.CUST.PACKING.state());
		dao().update(orderEntity, 
				new DBCondition(Orders.JField.id,
						new QueryStatement(OrdersPurchase.class)
							.appendQueryField(OrdersPurchase.JField.orderId),
						DBOperator.IN),
				new DBCondition(Orders.JField.status,OrderStatusDefine.CUST.PAYED.state())
		);
		return null;
	}*/
	
	/**
	 * 生成快递联系单
	 * @param context
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody createDelivery(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		return null;
	}*/
	
	/**
	 * 查询用户列表
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryUserList(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		List<DBCondition> conds = ClassUtil.newList();
		conds.add(new DBCondition(User.JField.mobile,null,DBOperator.IS_NOT_NULL));
		
		QueryStatement st = new QueryStatement(User.class,conds.toArray(new DBCondition[conds.size()]))
					.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()));
		Short orderbyOrderCount = context.getRequestBody().getShort(ParamDefine.User.orderby_order_count);
		Short orderbyOrderSum = context.getRequestBody().getShort(ParamDefine.User.orderby_order_count);
		
		if(orderbyOrderCount != null){
			st.appendOrderFieldDesc(User.JField.orderCount);
		}else if(orderbyOrderSum != null){
			st.appendOrderFieldDesc(User.JField.orderSum);
		}else{
			st.appendOrderFieldDesc(User.JField.createTime);
		}
		
		List<User> userList = dao().query(st);
		if(NullUtil.isEmpty(userList)){
			return null;
		}
		
		for(User user : userList){
			user.addAttribute("orderSum", ETUtil.parseFen2Yuan(user.getOrderSum()));
			if(user.getLastOrderTime() != null)
				user.addAttribute("lastOrderTime", DateUtil.date2Str(user.getLastOrderTime(), DateUtil.DATE_FORMAT_EN_B_YYYYMMDD));
			user.setOrderSum(null);
			user.setLastOrderTime(null);
		}
		long count = dao().queryCount(User.class);
		
		return new ResponseBody("userList",userList).addTotalCount(count);
	}
	
	/**
	 * 修改用户资料
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody modifyUser(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.User.user_id
		);
		long userId = reqbody.getLong(ParamDefine.User.user_id);
		//User userEntity = dao().queryById(User.class, userId);
		
		String nickname = reqbody.getString(ParamDefine.Common.name);
		String mobile = reqbody.getString(ParamDefine.User.mobile);
		String address = reqbody.getString(ParamDefine.User.address);
		
		User update = new User();
		if(nickname != null){
			update.setName(nickname);
		}
		if(mobile != null){
			update.setMobile(mobile);
		}
		if(address != null){
			update.setAddress(address);
		}
		
		dao().updateById(update, userId);
		return null;
	}
	
	
	public ResponseBody modifyOrder(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id/*,
			ParamDefine.Order.need_notify*/
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		//OrderCmp.calcPaySum(orderEntity, items, globalPromList, couponEntity)
		//short needNotify = reqbody.getShort(ParamDefine.Order.need_notify);
		
		String name = reqbody.getString(ParamDefine.Common.name);
		String mobile = reqbody.getString(ParamDefine.User.mobile);
		String address = reqbody.getString(ParamDefine.User.address);
		Float sum = reqbody.getFloat(ParamDefine.Order.sum);
		Float expressSum = reqbody.getFloat(ParamDefine.Order.express_sum);
		Float expressCost = reqbody.getFloat(ParamDefine.Order.express_cost);
		
		Short isAdustPrice = reqbody.getShort(ParamDefine.Order.is_adust_price);
		Short status = reqbody.getShort(ParamDefine.Order.status);
		String deliveryNo = reqbody.getString(ParamDefine.User.delivery_no);
		Float cost = reqbody.getFloat(ParamDefine.Order.cost);
		
		boolean isAdjPrice = isAdustPrice != null && isAdustPrice == 1;
		Orders origOrderEntity = null;//
		if(isAdjPrice){
			origOrderEntity = dao().queryById(Orders.class, orderId,
					Orders.JField.id,Orders.JField.code,Orders.JField.userId,Orders.JField.origSum,Orders.JField.sum,Orders.JField.expressSum);
		}
		
		Orders update = new Orders();
		if(name != null){
			update.setName(name);
		}
		if(mobile != null){
			update.setMobile(mobile);
		}
		if(address != null){
			update.setAddress(address);
		}
		if(sum != null){
			update.setSum(ETUtil.parseYuan2Fen(sum));
		}
		if(cost != null){
			update.setCost(ETUtil.parseYuan2Fen(cost));
		}
		if(deliveryNo != null){
			update.setExpressNo(deliveryNo);
		}
		if(expressSum != null){
			update.setExpressSum(ETUtil.parseYuan2Fen(expressSum));
		}
		if(expressCost != null){
			update.setExpressCost(ETUtil.parseYuan2Fen(expressCost));
		}
		
		if(isAdjPrice){
			update.setStatus(OrderStatusDefine.CUST.ADJUST_PRICE.state());
		}else if(status != null){
			update.setStatus(status);
		}
		
		if(!DBHelper.isModified(update)){
			return null;
		}
		
		dao().updateById(update, orderId);
		
		if(isAdjPrice){
			//调价
			//LogUtil.dump("________origOrderEntity", origOrderEntity);
			long origPaySum = origOrderEntity.getOrigSum()+origOrderEntity.getExpressSum();
			//调价通知必须发到订购人，而不是收货人
			User userEntity = dao().queryById(User.class, origOrderEntity.getUserId());
			//String finalSum = ETUtil.parseFen2YuanStr(origOrderEntity.getSum()+origOrderEntity.getExpressSum(),false);
			Long adjustSum = sum == null ? origOrderEntity.getSum() : ETUtil.parseYuan2Fen(sum);
			Long adjustExpressSum = expressSum==null?origOrderEntity.getExpressSum():ETUtil.parseYuan2Fen(expressSum);
			
			NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.order_adjust_price, 
					new KeyValueHolder("order_code", origOrderEntity.getCode())
							.addParam("order_id", origOrderEntity.getId())
							.addParam("order_sum", ETUtil.parseFen2YuanStr(origPaySum,false))
							.addParam("final_order_sum", ETUtil.parseFen2YuanStr(adjustSum+adjustExpressSum,false))
					,
					new NotifyTargetHolder().addMobile(userEntity.getMobile())
							.addOpenid(userEntity.getWechat())
			);
		}
		
		return null;
	}
	
	public ResponseBody removeOrder(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		
		dao().deleteById(Orders.class, orderId);
		dao().delete(OrdersItem.class, new DBCondition(OrdersItem.JField.orderId,orderId));
		return null;
	}
	
	
	public ResponseBody remindOrderComment(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		Orders orderEntity = OrderCmp.checkExist(orderId, false);
		//已支付
		/*if (orderEntity.getStatus() == OrderStatusDefine.CUST.COMPLETED.state()
				&& orderEntity.getStatus() != OrderStatusDefine.CUST.ADJUST_PRICE.state()) {
			throw ETUtil.buildException(ErrorDefine.ORDER_HAS_PAYED);
		}*/
		User userEntity = UserCmp.queryById(orderEntity.getUserId(), User.JField.name,User.JField.mobile,User.JField.wechat);
		
		NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.order_remind_comment, 
				new KeyValueHolder("order_id",String.valueOf(orderEntity.getId()))
						.addParam("order_code", String.valueOf(orderEntity.getId()))
						.addParam("order_time", orderEntity.getCreateTime())
				,
				new NotifyTargetHolder().addMobile(userEntity.getMobile())
						.addOpenid(userEntity.getWechat())
		);
		
		return null;
	}
	
	/**
	 * 提醒订单支付，针对未支付订单。或者是补余款的场景
	 * @param context
	 * 			order_id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody remindOrderPay(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		Orders orderEntity = OrderCmp.checkExist(orderId, false);
		//已支付
		short status = orderEntity.getStatus();
		boolean canPay = (status == OrderStatusDefine.CUST.EDIT.state() 
				|| status == OrderStatusDefine.CUST.ADJUST_PRICE.state()
				|| OrderCmp.isPreorderBuy(orderEntity.getBuyType()));
		
		if (!canPay) {
			throw ETUtil.buildException(ErrorDefine.ORDER_HAS_PAYED);
		}
		User userEntity = UserCmp.queryById(orderEntity.getUserId(), User.JField.name,User.JField.mobile,User.JField.wechat);
		
		NotifyDefine.CodeDefine notifyCode = NotifyDefine.CodeDefine.order_remind_pay;
		/*if(OrderCmp.isPreorderBuy(orderEntity.getBuyType())){
			notifyCode = NotifyDefine.CodeDefine.remind_fillpay;
		}else{
			notifyCode = NotifyDefine.CodeDefine.remind_pay;
		}*/
		
		NotifyCmp.sendTemplate(notifyCode, 
				new KeyValueHolder("order_sum",ETUtil.parseFen2YuanStr(orderEntity.getSum()+orderEntity.getExpressSum(),false))
						.addParam("name", userEntity.getName())
						.addParam("order_id", orderEntity.getId())
						.addParam("order_code", String.valueOf(orderEntity.getId()))
						.addParam("order_time", orderEntity.getCreateTime())
				,
				new NotifyTargetHolder().addMobile(userEntity.getMobile())
						.addOpenid(userEntity.getWechat())
		);
		
		return null;
	}
	
	/**
	 * 赠送优惠券
	 * @param context
	 * 			user_id,
	 * 			coupon_type,
	 * 			coupon_value,
	 *			validity,有效期，单位天,如果是0，表示当天截止
	 * @return
	 * @throws Exception
	 */
	public ResponseBody allocateCoupons(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
				ParamDefine.User.user_id,
				ParamDefine.Coupon.coupon_type,
				ParamDefine.Coupon.coupon_value,
				ParamDefine.Coupon.expire_date
		);
		long userId = reqbody.getLong(ParamDefine.User.user_id);
		short couponType = reqbody.getShort(ParamDefine.Coupon.coupon_type);
		long couponValue = reqbody.getLong(ParamDefine.Coupon.coupon_value);
		Date expireDate = reqbody.getDate(ParamDefine.Coupon.expire_date,DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
		
		User user = UserCmp.checkExistByUserId(userId);
		
		UserCoupon coupon = new UserCoupon();
		coupon.setUserId(userId);
		coupon.setType(couponType);//类型
		coupon.setValue(couponValue);//优惠值
		//coupon.setPremise(rule.getPremise());//使用条件
		//Date now = DateUtil.now();
		/*Date expireDate = null;
		
		if(validity == 0){
			expireDate = now;
		}else if(validity == -1){
			expireDate = DateUtil.str2Date("2099-12-30", DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
		}else{
			expireDate = DateUtil.offsetDate(now, validity);
		}*/
		//expireDate = DateUtil.getDayEnd(expireDate);//到23:59:59结束
		coupon.setExpireTime(expireDate);
		coupon.setSrc(ConsDefine.COUPON_EVENT.SYS.value());
		coupon.setIsUsed((short)0);
		coupon.setRuleId(-1L);
		
		dao().insert(coupon);
		
		//通知客户赠送
		CouponCmp.sendCouponNotify(user, coupon);
		/*NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.allocate_coupons, 
				new KeyValueHolder("name",user.getName())
						.addParam("coupon", CouponCmp.buildCouponText(coupon)),
				new NotifyTargetHolder().addMobile(user.getMobile())
		);*/
		
		return null;
	}
	
	/**
	 * 查询某个用户的优惠券
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryUserCouponList(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
				ParamDefine.User.user_id
		);
		long userId = reqbody.getLong(ParamDefine.User.user_id);
		//查出该用户的优惠券，包括已过期的
		List<UserCoupon> couponList = CouponCmp.queryListByUserId(userId, false);
		if(NullUtil.isEmpty(couponList)){
			return null;
		}
		for(UserCoupon coupon : couponList){
			CouponCmp.transfer(coupon);
			ETUtil.clearDBEntityExtraAttr(coupon,UserCoupon.JField.createTime);
		}
		
		CouponCmp.appendCouponEventName(couponList);
		
		return new ResponseBody("couponList",couponList);
		
	}
	
	/**
	 * 截单。本次团购方案结束并生效。不再接受新成员报名，之前未支付的成员也将视为自动放弃
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody closeGroupbuy(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		//long userId = context.getAccessUserId();
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
		/*//只能创建者截单
		if(creatorOrder == null || creatorOrder.getUserId() != userId){
			throw ETUtil.buildException(BaseErrorDefine.SYS_NOT_ALLOWED);
		}*/
		
		Product prodEntity = ProdCmp.queryProduct(creatorItem.getProductId());
		creatorItem.addAttribute("product", prodEntity);
		
		Promotion promEntity = dao().queryById(Promotion.class,creatorOrder.getPromotionId());
		AbstractPromotionBean promBean = PromotionManager.getPromotionBean(promEntity);
		
		//boolean isMatch = promBean.isMatched(orderList);
		
		/*if(!isMatch){
			throw ETUtil.buildException(ErrorDefine.GROUPBUY_NOT_MATCHED);
		}*/
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
				target);*/
		
		OrderCmp.changeStatus(creatorOrder, OrderStatusDefine.CUST.GROUPBUY_CLOSE.state(), null);
		
		return null;
	} 
	
	/**
	 * 发布一个团购
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody publishGroupbuy(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody,
			ParamDefine.Groupbuy.product_info,
			ParamDefine.Groupbuy.intro
		);
		Long groupbuyId = reqbody.getLong(ParamDefine.Groupbuy.groupbuy_id);
		boolean isNew = groupbuyId == null;
		if(isNew){
			RequestUtil.checkEmptyParams(reqbody,
				ParamDefine.Groupbuy.product_info,
				ParamDefine.Groupbuy.intro
			);
		}
		String intro = reqbody.getString(ParamDefine.Groupbuy.intro);
		String productInfo = reqbody.getString(ParamDefine.Groupbuy.product_info);
		
		Groupbuy gb = new Groupbuy();
		if(productInfo != null){
			gb.setProductInfo(productInfo);
		}
		if(intro != null){
			gb.setIntro(intro);
		}
		if(isNew){
			gb.setStatus((short)99);
			gb.setUserId(context.getAccessUserId());
			dao().insert(gb);
			groupbuyId = gb.getId();
		}else if(DBHelper.isModified(gb)){
			dao().updateById(gb, groupbuyId);
		}
		
		
		return new ResponseBody("newid",groupbuyId);
	}
	
	
	/**
	 * 查询团购列表
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryGroupbuyList(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		/*RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Groupbuy.groupbuy_id
		);*/
		List<Groupbuy> list = dao().query(new QueryStatement(Groupbuy.class)
				.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))	
				.appendOrderFieldDesc(Groupbuy.JField.createTime));
				
		return new ResponseBody("groupbuyList",list);
	}
	
	
	public ResponseBody queryGroupbuyDetail(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		Long groupbuyId = reqbody.getLong(ParamDefine.Groupbuy.groupbuy_id);
		List<Product> prodList = dao().queryAll(Product.class);
		if(NullUtil.isEmpty(prodList)){
			return null;
		}
		for(Product prod : prodList){
			ProdCmp.transfer(prod,true);
		}
		
		Groupbuy groupbuy = null;
		if(groupbuyId != null){
			groupbuy = dao().queryById(Groupbuy.class, groupbuyId);
		}
		
		return new ResponseBody("productList",prodList)
					.add("groupbuy", groupbuy);
	}
	
	public ResponseBody queryGroupbuyJoin(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		long groupbuyId = reqbody.getLong(ParamDefine.Groupbuy.groupbuy_id);
		Short needPayed = reqbody.getShort(ParamDefine.Groupbuy.need_payed,(short)1);
		Short needUnpay = reqbody.getShort(ParamDefine.Groupbuy.need_unpay,(short)1);
		Groupbuy gb = dao().queryById(Groupbuy.class,groupbuyId);
		
		String[] els = gb.getProductInfo().split(",");
		Set<Long> productIds = ClassUtil.newSet();
		for(String el : els){
			String[] ells = el.split(":");
			productIds.add(Long.valueOf(ells[0]));
		}
		Short status = null;
		if(needPayed == 1 && needUnpay == 1){
			status = null;
		}else if(needPayed == 1){
			status = 1;
		}else if(needUnpay == 1){
			status = 0;
		}
		List<DBCondition> orderCondList = ClassUtil.newList();
		orderCondList.add(new DBCondition(Orders.JField.groupbuyId,groupbuyId));
		if(status != null){
			orderCondList.add(new DBCondition(Orders.JField.status,status));
		}
		DBCondition[] orderCondArr = orderCondList.toArray(new DBCondition[orderCondList.size()]);
		
		List<Orders> orderList = dao().query(new QueryStatement(Orders.class,orderCondArr)
					.appendOrderFieldDesc(Orders.JField.createTime)
					.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
					.appendQueryField(Orders.JField.id,
							Orders.JField.userId,
							Orders.JField.status,
							Orders.JField.name,
							Orders.JField.address,
							Orders.JField.mobile)
		);
		List<OrdersItem> itemList = OrderCmp.appendItems(orderList, false,false,
				OrdersItem.JField.id,
				OrdersItem.JField.amount,
				OrdersItem.JField.productId,
				OrdersItem.JField.sum,
				OrdersItem.JField.price);
		
		if(NullUtil.isNotEmpty(itemList)){
			for(OrdersItem item : itemList){
				OrderCmp.transferItem(item, false);
			}
		}
		
		List<Product> prodList = dao().queryByIds(Product.class,productIds,
				Product.JField.id,
				Product.JField.pic,
				Product.JField.name,
				Product.JField.unit,
				Product.JField.spec);
		if(NullUtil.isNotEmpty(prodList)){
			for(Product prod : prodList){
				ProdCmp.transfer(prod,true);
			}
		}
		
		//查询统计，金额，数量总计
		List<OrdersItem> statList = dao().query(new QueryStatement(OrdersItem.class,
				new DBCondition(OrdersItem.JField.orderId,
						new QueryStatement(Orders.class,orderCondArr).appendQueryField(Orders.JField.id),DBOperator.IN)
				)
					.appendGroupField(OrdersItem.JField.productId)
					.appendQueryField(
							OrdersItem.JField.productId,
							CountQueryField.getInstance("custCount"),
							new SumQueryField(OrdersItem.JField.amount),
							new SumQueryField(OrdersItem.JField.sum))
		);
		//统计总数
		List<BigDecimal> result = dao().queryStat(Orders.class,new IStatQueryField[]{
			CountQueryField.getInstance("custTotalCoun")
		} , orderCondArr);
		
		if(NullUtil.isNotEmpty(statList)){
			for(OrdersItem item : statList){
				OrderCmp.transferItem(item, false);
			}
		}
		
		return new ResponseBody("productList",prodList)
					.add("joinList", orderList)
					.add("statList", statList)
					.add("totalCustCount", result.get(0).intValue());
	}
	
	
	public static void main(String[] args) {
		
	}
}
