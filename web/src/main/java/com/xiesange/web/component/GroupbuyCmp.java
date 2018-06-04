package com.xiesange.web.component;

import java.util.List;
import java.util.Set;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.groupbuy.Groupbuy;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.sql.DBOrCondition;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.define.ConsDefine;
import com.xiesange.web.define.ErrorDefine;


public class GroupbuyCmp {
	public static Groupbuy checkGroupbuy(long groupbuyId) throws Exception{
		Groupbuy gb = DBHelper.getDao().queryById(Groupbuy.class, groupbuyId);
		if(gb == null || gb.getStatus() != 99){
			throw ETUtil.buildException(ErrorDefine.GROUPBUY_RULE_EXPIRED);
		}
		return gb;
	}
	
	public static List<Orders> checkGroupbuyOrders(long createOrderId,boolean needItems) throws Exception{
		List<Orders> orders = DBHelper.getDao().query(new QueryStatement(Orders.class, 
				new DBOrCondition(
					new DBCondition(Orders.JField.id,createOrderId),
					new DBCondition(Orders.JField.groupbuyId,createOrderId)
				)).appendOrderField(Orders.JField.id)
		);
		
		
		if(NullUtil.isEmpty(orders)){
			throw ETUtil.buildException(ErrorDefine.ORDER_NOTEXIST);
		}
		
		Set<Long> orderIds = ETUtil.buildEntityIdList(orders, Orders.JField.id);
		List<OrdersItem> items = null;
		if(needItems){
			items = DBHelper.getDao().query(new QueryStatement(OrdersItem.class, 
				new DBCondition(OrdersItem.JField.orderId,orderIds,DBOperator.IN))
					.appendQueryField(OrdersItem.JField.id,
							OrdersItem.JField.productId,
							OrdersItem.JField.orderId,
							OrdersItem.JField.amount,
							OrdersItem.JField.price)
			);
		}
		
		Orders createOrder = null;//找到发起人这个订单
		for(Orders order : orders){
			if(order.getId() == createOrderId){
				createOrder = order;
			}
			if(NullUtil.isNotEmpty(items)){
				for(OrdersItem item : items){
					if(order.getId().longValue() == item.getOrderId()){
						List<OrdersItem> orderitems = ClassUtil.newList();
						orderitems.add(item);
						order.addAttribute("items", orderitems);
						break;
					}
				}
			}
		}
		
		if(createOrder.getGroupbuyId() != -1
				|| createOrder.getBuyType() != ConsDefine.ORDER_BUYTYPE.TOGETHERBUY.value()){
			throw ETUtil.buildException(ErrorDefine.ORDER_NOTEXIST);
		}
		
		return orders;
	}
	
	/*public static Orders createGroupbuyOrder(Promotion promotion,String mobile,String name,String address,int amount) throws Exception{
		long groupbuyPrice = Long.valueOf(promotion.getValue());
		
		Orders orderEntity = buildOrder(mobile,name,address);
		orderEntity.setPromotionId(promotion.getId());
		orderEntity.setExpressFee(0L);
		orderEntity.setOrigSum(groupbuyPrice*amount);
		orderEntity.setSum(orderEntity.getOrigSum());
		orderEntity.setExpireTime(DateUtil.offsetDate(DateUtil.now(),promotion.getValidity()));
		DBHelper.getDao().insert(orderEntity);
		return orderEntity;
	}*/
	
	/*public static OrdersItem createGroupbuyItem(long groupbuyPrice,long orderId,int amount,long productId) throws Exception{
		OrdersItem orderItem = new OrdersItem();
		orderItem.setOrderId(orderId);
		orderItem.setUserId(ETUtil.getRequestContext().getAccessUserId());
		orderItem.setPrice(groupbuyPrice);
		orderItem.setAmount(amount);
		orderItem.setSum(groupbuyPrice*amount);
		orderItem.setProductId(productId);
		DBHelper.getDao().insert(orderItem);
		
		return orderItem;
	}*/
	
	
	/*public static Orders follow(Orders order,OrdersItem item,int amount,String name,String mobile) throws Exception{
		//WebRequestContext context = (WebRequestContext)ETUtil.getRequestContext();
		Orders newOrder = buildOrder(mobile,name,null);
		newOrder.setPromotionId(order.getPromotionId());
		newOrder.setExpireTime(order.getExpireTime());
		newOrder.setGroupbuyId(order.getId());
		DBHelper.getDao().insert(newOrder);
		
		createGroupbuyItem(item.getPrice(),newOrder.getId(),amount,item.getProductId());
		
		return newOrder;
	}*/
	
	
	public static Orders buildOrder(long groupbuyId,String mobile,String name,String address) throws Exception{
		long orderId = DBHelper.getDao().getSequence(Orders.class);
		Orders orderEntity = new Orders();
		orderEntity.setGroupbuyId(groupbuyId);
		orderEntity.setId(orderId);
		orderEntity.setBuyType(ConsDefine.ORDER_BUYTYPE.TOGETHERBUY.value());
		orderEntity.setUserId(ETUtil.getRequestContext().getAccessUserId());
		orderEntity.setCode(OrderCmp.generateOrderCode(orderId));
		if(mobile != null){
			orderEntity.setMobile(mobile);
		}
		if(address != null){
			orderEntity.setAddress(address);
		}
		if(name != null){
			orderEntity.setName(name);
		}
		orderEntity.setChannel(RequestUtil.getRequestChannel(ETUtil.getRequestContext().getRequestHeader()));
		orderEntity.setStatus((short)0);
		orderEntity.setOrigSum(0L);
		orderEntity.setSum(0L);
		orderEntity.setExpressSum(0L);
		return orderEntity;
	}
	
	public static OrdersItem getOrdersItem(Orders order){
		return ((List<OrdersItem>)order.getAttr("items")).get(0);
	}
}
