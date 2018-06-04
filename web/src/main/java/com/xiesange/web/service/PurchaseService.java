package com.xiesange.web.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.notify.NotifyCmp;
import com.xiesange.baseweb.notify.NotifyDefine;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.purchase.Purchase;
import com.xiesange.gen.dbentity.purchase.PurchaseItem;
import com.xiesange.gen.dbentity.sys.SysLogin;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.statement.update.UpdateStatement;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.OrderCmp;
import com.xiesange.web.component.ProdCmp;
import com.xiesange.web.component.PurchaseCmp;
import com.xiesange.web.define.OrderStatusDefine;
import com.xiesange.web.define.ParamDefine;
@ETServiceAnno(name="purchase",version="")
/**
 * 采购单服务类
 * @author Wilson 
 * @date 上午9:48:42
 */
public class PurchaseService extends AbstractService {
	/*public ResponseBody queryPackingOrderList(WebRequestContext context) throws Exception{
		List<Orders> orderList = dao().query(
			new QueryStatement(Orders.class, 
					new DBOrCondition(
						new DBCondition(Orders.JField.status,2),
						new DBCondition(
								new DBCondition(Orders.JField.status,3),
								new DBCondition(Orders.JField.deliveryDate,DateUtil.now()))
					))
					.appendQueryField(
							Orders.JField.id,
							Orders.JField.name,
							Orders.JField.deliveryFee,
							Orders.JField.deliveryNo,
							Orders.JField.deliveryWeight)
		);
		if(NullUtil.isEmpty(orderList)){
			return null;//throw new Exception("没有可采购的订单");
		}
		
		for(Orders order : orderList){
			OrderCmp.transfer(order);
		}
		
		return new ResponseBody("orderList",orderList);
	}*/
	
	
	/**
	 * 发送快递联系单，发送到快递侧
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody sendDeliveryContact(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody,
			ParamDefine.Purchase.purchase_id
		);
		long pid = reqbody.getLong(ParamDefine.Purchase.purchase_id);
		
		
		
		List<Orders> orderList = dao().query(Orders.class, new DBCondition(Orders.JField.status,2));
		if(NullUtil.isEmpty(orderList)){
			throw new Exception("没有可采购的订单");
		}
		
		//插入一条有效期为3天的token
		//SysLogin sysLogin = CCP.createLogin(-1L,BaseConsDefine.SYS_TYPE.XIESANGE, context.getRequestChannel());
		
		String url = context.getHost()+"/wechat/out/delivery-contact.html?purchase_id="+pid;
		
		/*NotifyCmp.sendTemplate(
				NotifyDefine.CodeDefine.send_delivery_contact, 
				new KeyValueHolder("url",url),
				null
		);*/
		return null;
	}
	
	public ResponseBody queryDeliveryContact(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(reqbody,
			ParamDefine.Purchase.purchase_id
		);
		long pid = reqbody.getLong(ParamDefine.Purchase.purchase_id);
		
		List<PurchaseItem> pitemList = dao().query(PurchaseItem.class, 
				new DBCondition(PurchaseItem.JField.purchaseId,pid));
		
		if(NullUtil.isNotEmpty(pitemList)){
			for(PurchaseItem item : pitemList){
				PurchaseCmp.transferItem(item);
			}
		}
		
		List<Orders> orderList = PurchaseCmp.appendOrders(pitemList,true);
		return new ResponseBody("orderList",orderList);
	}
	
	
	/**
	 * 提交快递数据
	 * @param context
	 * 			delivery_info
	 * @return
	 * @throws Exception
	 */
	public ResponseBody commitDeliveryData(WebRequestContext context) throws Exception{
		String balance = context.getRequestBody().getString(ParamDefine.Purchase.express_balance);
		List<Orders> orders  = context.getRequestBody().getDBEntityList(ParamDefine.Purchase.express_info, Orders.class);
		List<UpdateStatement> updateStList = ClassUtil.newList();
		
		Date now = DateUtil.now();
		for(Orders order : orders){
			Orders updateValue = new Orders();
			updateValue.setExpressDate(now);
			updateValue.setExpressNo(order.getExpressNo());
			updateValue.setExpressCost(order.getExpressCost());
			updateValue.setExpressWeight(order.getExpressWeight());
			updateValue.setStatus(OrderStatusDefine.CUST.DELIVERYING.state());
			UpdateStatement up = new UpdateStatement(updateValue,order.getId());
			updateStList.add(up);
		}
		
		dao().updateBatch(updateStList.toArray(new UpdateStatement[updateStList.size()]));
		
		return null;
	}
	
	public ResponseBody commitPurchasePrice(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody,
			ParamDefine.Purchase.purchase_id,
			ParamDefine.Purchase.purchase_price,
			ParamDefine.Purchase.express_fee
		);
		long pid = reqbody.getLong(ParamDefine.Purchase.purchase_id);
		Float expressFee = reqbody.getFloat(ParamDefine.Purchase.express_fee);
		String price = reqbody.getString(ParamDefine.Purchase.purchase_price);
		String[] priceArr = price.split("\\|");
		
		List<PurchaseItem> pitemList = dao().query(PurchaseItem.class, 
				new DBCondition(PurchaseItem.JField.purchaseId,pid));
		
		if(NullUtil.isEmpty(pitemList)){
			return null;
		}
		
		List<Orders> orderList = PurchaseCmp.appendOrders(pitemList,true);
		
		if(NullUtil.isEmpty(orderList)){
			return null;
		}

		
		List<OrdersItem> orderItems = OrderCmp.appendItems(orderList, false,false);
		if(NullUtil.isEmpty(orderItems)){
			return null;
		}
		
		List<UpdateStatement> updateSt = ClassUtil.newList();
		for(String pstr : priceArr){
			String[] priceitemArr = pstr.split(":");
			long prodId = Long.valueOf(priceitemArr[0]);
			float prodPrice = Float.valueOf(priceitemArr[1]);
			
			for(OrdersItem oitem : orderItems){
				if(oitem.getProductId() == prodId){
					oitem.setCostPrice(ETUtil.parseYuan2Fen(prodPrice));
					updateSt.add(new UpdateStatement(oitem,oitem.getId()));
				}
			}
		}
		
		dao().updateBatch(updateSt.toArray(new UpdateStatement[updateSt.size()]));
		
		Purchase pentity = new Purchase();
		pentity.setPrices(price);
		if(expressFee != null){
			pentity.setExpressFee(ETUtil.parseYuan2Fen(expressFee));
		}
		dao().updateById(pentity, pid);
		return null;
	}
	
	/**
	 * 生成采购订单
	 * @param context
	 * 			order_ids,需要在本次生成采购订单的销售订单ID
	 * @return
	 * @throws Exception
	 */
	public ResponseBody create(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		List<Orders> orderList = dao().query(Orders.class, 
				new DBCondition(Orders.JField.status,OrderStatusDefine.CUST.PACKING.state()));
		
		if(orderList == null){
			throw new Exception("没有可采购的订单");
		}
		List<OrdersItem> orderItems = OrderCmp.appendItems(orderList, false, true);
		long totalOrderSum = 0;
		long totalOrderExpressFee = 0;
		for(Orders order : orderList){
			totalOrderSum += order.getSum();
			totalOrderExpressFee += order.getExpressSum();
			List<OrdersItem> items = OrderCmp.appendItems(order);
			if(NullUtil.isNotEmpty(items)){
				orderItems.addAll(items);
			}
		}
		
		Purchase purchase = new Purchase();
		purchase.setOrderCount(orderList.size());
		purchase.setOrderExpressFee(totalOrderExpressFee);
		purchase.setOrderSum(totalOrderSum);
		purchase.setExpressFee(0L);
		purchase.setSum(0L);
		purchase.setStatus((short)0);//0表示还未生成发货单，1表示已经生成发货单
		dao().insert(purchase);
		
		List<PurchaseItem> items = ClassUtil.newList();
		for(Orders order : orderList){
			PurchaseItem item = new PurchaseItem();
			item.setPurchaseId(purchase.getId());
			item.setExpressFee(0L);
			item.setSum(0L);
			
			item.setOrderId(order.getId());
			item.setUserId(order.getUserId());
			item.setOrderSum(order.getSum());
			item.setOrderExpressFee(order.getExpressSum());
			item.addAttribute("order", order);
			items.add(item);
		}
		dao().insertBatch(items);
		
		//发送采购订单
		/*try{
			OrderCmp.appendProducts(orderItems, true);
			PurchaseCmp.sendPurchaseEmail(items);
		}catch(Exception e){
			logger.error(e,e);
		}*/
		//PurchaseCmp.sendDeliveryEmail(items);
		return null;
	}
	
	/**
	 * 删除某个采购单
	 * @param context
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody remove(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Purchase.purchase_id
		);
		
		long purchaseId = reqbody.getLong(ParamDefine.Purchase.purchase_id);
		dao().deleteById(Purchase.class, purchaseId);
		dao().delete(PurchaseItem.class, new DBCondition(PurchaseItem.JField.purchaseId,purchaseId));
		
		return null;
	}*/
	
	/**
	 * 查询采购单列表
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryList(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());//非管理员无法进行该操作
		RequestBody reqbody = context.getRequestBody();
		List<DBCondition> condList = ClassUtil.newList();
		
		Short status = reqbody.getShort(ParamDefine.Order.status);
		
		if(status != null){
			condList.add(new DBCondition(Purchase.JField.status,status));
		}
		
		DBCondition[] condArr = NullUtil.isEmpty(condList) ? null:
			condList.toArray(new DBCondition[condList.size()]);
		
		QueryStatement st = new QueryStatement(Purchase.class,condArr);
		st.appendOrderFieldDesc(Purchase.JField.createTime);
		st.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()));
		
		List<Purchase> purchaseList = dao().query(st);
		if(NullUtil.isEmpty(purchaseList)){
			return null;
		}
		
		for(Purchase purchase : purchaseList){
			PurchaseCmp.transfer(purchase);
		}
		/*Set<Long> purchaseIds = ETUtil.buildEntityIdList(purchaseList, Purchase.JField.id);
		
		List<PurchaseItem> purchaseItems = dao().query(PurchaseItem.class, new DBCondition(PurchaseItem.JField.purchaseId,purchaseIds,DBOperator.IN));
		
		for(Purchase purchase : purchaseList){
			int count = 0;
			for(PurchaseItem pitem : purchaseItems){
				if(pitem.getPurchaseId().longValue() == purchase.getId()){
					count++;
				}
			}
			PurchaseCmp.transfer(purchase);
			purchase.addAttribute("itemCount", count);
		}*/
		
		//List<PurchaseItem> itemList = PurchaseCmp.appendItems(purchaseList,true);
		
		//PurchaseCmp.appendOrders(itemList,true);
		
		return new ResponseBody("purchaseList",purchaseList);
	}
	
	public ResponseBody queryDetail(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody,
			ParamDefine.Purchase.purchase_id
		);
		long pid = reqbody.getLong(ParamDefine.Purchase.purchase_id);
		Purchase pEntity = dao().queryById(Purchase.class,pid);
		
		List<PurchaseItem> pitemList = dao().query(PurchaseItem.class, 
				new DBCondition(PurchaseItem.JField.purchaseId,pid));
		
		PurchaseCmp.transfer(pEntity);
		if(NullUtil.isNotEmpty(pitemList)){
			for(PurchaseItem item : pitemList){
				PurchaseCmp.transferItem(item);
			}
		}
		
		List<Orders> orderList = PurchaseCmp.appendOrders(pitemList,true);
		if(NullUtil.isNotEmpty(orderList)){
			List<OrdersItem> orderItems = OrderCmp.appendItems(orderList, false,false);
			if(NullUtil.isNotEmpty(orderItems)){
				for(OrdersItem item : orderItems){
					OrderCmp.transferItem(item, false);
				}
				
				List<Product> prodList = OrderCmp.appendProducts(orderItems, false,
						Product.JField.id,
						Product.JField.name,
						Product.JField.pname,
						Product.JField.spec,
						Product.JField.pic,
						Product.JField.unit);
				
				for(Product prod : prodList){
					if(NullUtil.isNotEmpty(prod.getPname())){
						prod.setName(prod.getPname());
					}
					ProdCmp.transfer(prod, false);
				}
			}
		}
		
		
		return new ResponseBody("purchase",pEntity)
						.add("purchaseItemList", pitemList);
	}
	
	
	
	
	/**
	 * 采购单状态更新为已接单。同时采购订单对应的销售订单更新成"打包中"
	 * @param context
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody packing(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Purchase.purchase_id
		);
		
		long purchaseId = reqbody.getLong(ParamDefine.Purchase.purchase_id);
	
		Purchase p = new Purchase();
		p.setStatus((short)1);
		dao().updateById(p, purchaseId);
		
		//同时把采购单下所有订单改为打包中
		Orders orderValue = new Orders();
		orderValue.setStatus(OrderStatusDefine.CUST.PACKING.state());
		dao().update(orderValue, new DBCondition(Orders.JField.id,
				new QueryStatement(PurchaseItem.class,new DBCondition(PurchaseItem.JField.purchaseId,purchaseId))
					.appendQueryField(PurchaseItem.JField.orderId),
				DBOperator.IN));
		
		
		return null;
	}*/
	
	/**
	 * 采购单状态更新为已发货。
	 * 1\采购订单对应的销售订单更新成"配送中"
	 * 2\向订单里的所有客户发送发货通知
	 * @param context
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody delivering(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Purchase.purchase_id
		);
		
		long purchaseId = reqbody.getLong(ParamDefine.Purchase.purchase_id);
	
		Purchase p = new Purchase();
		p.setStatus((short)2);
		dao().updateById(p, purchaseId);
		
		//同时把采购单下所有销售订单改为已发货
		Orders orderValue = new Orders();
		orderValue.setStatus(OrderStatusDefine.CUST.DELIVERYING.state());
		dao().update(orderValue, new DBCondition(Orders.JField.id,
				new QueryStatement(PurchaseItem.class,new DBCondition(PurchaseItem.JField.purchaseId,purchaseId))
					.appendQueryField(PurchaseItem.JField.orderId),
				DBOperator.IN));
		
		List<PurchaseItem> itemList = dao().query(PurchaseItem.class, new DBCondition(PurchaseItem.JField.purchaseId,purchaseId));
		Set<Long> userIdSet = ETUtil.buildEntityIdList(itemList, PurchaseItem.JField.userId);
		List<User> userList = dao().queryByIds(
				User.class, 
				userIdSet, 
				User.JField.id,
				User.JField.name,
				User.JField.mobile);
		
		for(PurchaseItem item : itemList){
			for(User user : userList){
				if(user.getId().longValue() == item.getUserId()){
					NotifyCmp.sendTemplate(
							NotifyDefine.CodeDefine.delivery_depart, 
							new KeyValueHolder(Common.name.name(),user.getName())
									.addParam(Delivery_Depart.city.name(), "杭州"),
							new NotifyTargetHolder().addMobile(user.getMobile()));
					break;
				}
			}
		}
		
		return null;
	}*/
	
	/**
	 * 查询快递路由信息
	 * @param context
	 * 			delivery_no
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody queryDeliveryRoute(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Purchase.delivery_no
		);
		String deliveryNo = reqbody.getString(ParamDefine.Purchase.delivery_no);
		List<SpeedaRoute> routerList = PurchaseCmp.queryDeliveryRoute(deliveryNo);
		return new ResponseBody("routerList",routerList);
	}*/
	/**
	 * 根据快递侧提供的信息，设置快递单、费用、重量等信息
	 * @param context
	 * 			delivery_info,json串
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody setDeliveryInfo(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Purchase.purchase_id,
			ParamDefine.Purchase.delivery_info
		);
		long purchaseId = reqbody.getLong(ParamDefine.Purchase.purchase_id);
		String deliveryInfo = reqbody.getString(ParamDefine.Purchase.delivery_info);
		
		List<DeliveryInfo> infolist = JsonUtil.json2List(deliveryInfo, DeliveryInfo.class);
		if(NullUtil.isEmpty(infolist)){
			return null;
		}
		//更新销售订单信息
		List<UpdateStatement> updateStList = ClassUtil.newList();
		for(DeliveryInfo info : infolist){
			Orders update = new Orders();
			update.setPurchaseId(purchaseId);
			update.setDeliveryNo(info.getDelivery_no());
			update.setDeliveryDate(context.getRequestDate());
			updateStList.add(new UpdateStatement(update,info.getOrder_id()));
		}
		dao().updateBatch(updateStList.toArray(new UpdateStatement[updateStList.size()]));
		
		//更新采购订单
		updateStList = ClassUtil.newList();
		for(DeliveryInfo info : infolist){
			PurchaseItem update = new PurchaseItem();
			update.setExpressFee(ETUtil.parseYuan2Fen(info.getFee()));
			update.setWeight(info.getWeight());
			updateStList.add(new UpdateStatement(update,
					new DBCondition(PurchaseItem.JField.purchaseId,purchaseId),
					new DBCondition(PurchaseItem.JField.orderId,info.getOrder_id())));
		}
		dao().updateBatch(updateStList.toArray(new UpdateStatement[updateStList.size()]));
		
		return null;
	}*/
	
	
	/*public ResponseBody sendDeliveryNotify(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Purchase.delivery_notify
		);
		String deliveryNotify = reqbody.getString(ParamDefine.Purchase.delivery_notify);
		
		List<DeliveryNotify> notifyList = JsonUtil.json2List(deliveryNotify, DeliveryNotify.class);
		if(NullUtil.isEmpty(notifyList)){
			return null;
		}
		
		Set<Long> userIdSet = ClassUtil.newSet();
		Set<Long> orderIdSet = ClassUtil.newSet();
		Set<Long> pitemIdSet = ClassUtil.newSet();
		for(DeliveryNotify notify : notifyList){
			userIdSet.add(notify.getUser_id());
			orderIdSet.add(notify.getOrder_id());
			pitemIdSet.add(notify.getPurchase_item_id());
		}
		List<User> userList = dao().queryByIds(
				User.class, 
				userIdSet, 
				User.JField.id,
				User.JField.name,
				User.JField.mobile,
				User.JField.wechat);
		List<Orders> orderList = dao().queryByIds(Orders.class, orderIdSet);
		List<PurchaseItem> pitems = dao().queryByIds(PurchaseItem.class, pitemIdSet);
		for(DeliveryNotify notify : notifyList){
			User user = null;
			Orders order = null;
			PurchaseItem pitem = null;
			for(Orders o : orderList){
				if(o.getId().longValue() == notify.getOrder_id()){
					order = o;
					break;
				}
			}
			for(PurchaseItem pi : pitems){
				if(pi.getId().longValue() == notify.getPurchase_item_id()){
					pitem = pi;
					break;
				}
			}
			for(User u : userList){
				if(u.getId().longValue() == notify.getUser_id()){
					user = u;
					break;
				}
			}
			
			if(pitem.getLastNotifyTime() == null){
				//说明是第一次发送提醒，那么是出发提醒
				NotifyCmp.sendTemplate(
						NotifyDefine.CodeDefine.delivery_depart, 
						new KeyValueHolder(Common.name.name(),user.getName())
								.addParam(Delivery_Depart.city.name(), "杭州")
								.addParam(Delivery_Depart.order_code.name(), order.getId())
								.addParam(Delivery_Depart.order_time.name(), order.getCreateTime())
								.addParam(Delivery_Depart.address.name(), order.getAddress()),
						new NotifyTargetHolder().addMobile(user.getMobile())
								.addOpenid(user.getWechat())
				);
			}else{
				//没有点值表示发货
				String time = notify.getDelivery_time().split(" ")[1];
				NotifyCmp.sendTemplate(
						NotifyDefine.CodeDefine.delivery_arrive, 
						new KeyValueHolder(Delivery_Arrive.time.name(),time)
								.addParam(Delivery_Arrive.node.name(), notify.getDelivery_node())
								.addParam(Common.name.name(), user.getName())
								.addParam(Delivery_Arrive.order_code.name(),order.getId())
								.addParam(Delivery_Arrive.delivery_no.name(),order.getDeliveryNo())
								.addParam(Common.name.name(), user.getName()), 
						new NotifyTargetHolder().addMobile(user.getMobile())
									.addOpenid(user.getWechat())
				);
			}
		}
		
		//更新采购单最新发送通知日期
		PurchaseItem update = new PurchaseItem();
		update.setLastNotifyTime(context.getRequestDate());
		dao().updateByIds(update, pitemIdSet);
		return null;
	}*/
	
	/**
	 * 采购完成。把采购更新成已完成。把采购单对应的订单都更新成已完成
	 * @param context
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody complete(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Purchase.purchase_id
		);
		
		long purchaseId = reqbody.getLong(ParamDefine.Purchase.purchase_id);
		Purchase pEntity = dao().queryById(Purchase.class, purchaseId);
		
		pEntity.setStatus(BaseConsDefine.STATUS.EFFECTIVE.value());
		dao().updateById(pEntity, purchaseId);
		
		//同时把采购单下所有订单改为已完成
		Orders orderValue = new Orders();
		orderValue.setStatus(BaseConsDefine.STATUS.EFFECTIVE.value());
		dao().update(orderValue, new DBCondition(Orders.JField.id,
				new QueryStatement(PurchaseItem.class,new DBCondition(PurchaseItem.JField.purchaseId,purchaseId))
					.appendQueryField(PurchaseItem.JField.orderId),
				DBOperator.IN));
		
		return null;
	}*/
	
	
	
}
