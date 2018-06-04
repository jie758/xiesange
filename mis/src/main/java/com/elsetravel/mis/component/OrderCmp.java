package com.elsetravel.mis.component;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.gen.dbentity.orders.Orders;
import com.elsetravel.gen.dbentity.orders.OrdersOperation;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.mis.define.OrderStatusDefine;
import com.elsetravel.orm.DBHelper;

public class OrderCmp {
	public static Orders queryOrder(long orderId) throws Exception{
		return DBHelper.getDao().queryById(Orders.class, orderId);
		
	}
	
	/**
	 * 判断订单是否存在，如果不存在（包括status=0,已取消）则抛出异常；如果订单存在则返回该订单实体
	 * @param orderId
	 * @throws Exception 
	 */
	public static Orders checkOrderExist(long orderId) throws Exception{
		Orders orderEntity = queryOrder(orderId);
		if(orderEntity == null){
			throw ETUtil.buildException(ErrorDefine.ORDER_NOTEXIST);
		}
		return orderEntity;
	}
	
	
	/**
	 * 对订单的相关信息进行转换,比如把价格单位、金额单位转成元，状态转换成文本值
	 * @param ticket
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月21日
	 */
	public static void transfer(Orders order) throws Exception{
		if(order == null)
			return;
		
		if(order.getPrice() != null){
			//这里存储的单位是分，需要转换成元
			order.addAttribute("price", ETUtil.parseFen2Yuan(order.getPrice()));
			order.setPrice(null);
		}
		
		if(order.getSum() != null){
			//这里存储的单位是分，需要转换成元
			order.addAttribute("sum", ETUtil.parseFen2Yuan(order.getSum()));
			order.setSum(null);
		}
		
		if(order.getCostTime() != null){
			//这里存储的单位是分，需要转换成小时
			order.addAttribute("costTime", ETUtil.parseMinute2Hour(order.getCostTime()));
			order.setCostTime(null);
		}
		order.addAttribute("stateText", getOrderStateText(order.getStatus()));
	}
	
	public static String getOrderStateText(short orderStatus){
		OrderStatusDefine state = getOrderState(orderStatus);
		return state==null?null:state.getText();
	}
	public static OrderStatusDefine getOrderState(short orderStatus){
		OrderStatusDefine[] states = OrderStatusDefine.class.getEnumConstants();
		for(OrderStatusDefine state : states){
    		if(state.getState() == orderStatus)
    			return state;
    	}
		return null;
	}
	
	/**
	 * 订单状态更变操作。不仅会改变当前订单主表的status值，还会往订单状态操作表里插入一条记录
	 * @param order
	 * 			Orders对象，id必须要有值，其他属性可以根据业务需要是否要更新来设置
	 * @param newStatus，新状态值
	 * @param memo,状态更改备注，有些驳回操作是会有备注的
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:13:29
	 */
	public static void changeStatus(Orders order,short newStatus,String memo) throws Exception{
		order.setStatus(newStatus);
		DBHelper.getDao().updateById(order, order.getId());
		
		OrdersOperation oper = new OrdersOperation();
		oper.setOrderId(order.getId());
		oper.setStatus(newStatus);
		oper.setMemo(memo);
		
		DBHelper.getDao().insert(oper);
	}
	
	public static Short[] getUnFinishedStatus(short userRole){
		if(userRole == BaseConstantDefine.USER_ROLE_VISITOR){
			return new Short[]{
				OrderStatusDefine.EDIT.getState(),
				OrderStatusDefine.PAYED.getState(),
				OrderStatusDefine.CONFIRMED.getState(),
				OrderStatusDefine.STARTING.getState(),
				OrderStatusDefine.END.getState(),
				OrderStatusDefine.CANCEL_APPLY.getState(),
				OrderStatusDefine.REJECT_APPLY.getState()
			};
		}else if(userRole == BaseConstantDefine.USER_ROLE_GUIDER){
			//注意未支付的订单在导游侧是感知不到的，所以不需要列出来,且导游点击结束后就算订单完成了，导游无需评价
			return new Short[]{
				OrderStatusDefine.PAYED.getState(),
				OrderStatusDefine.CONFIRMED.getState(),
				OrderStatusDefine.STARTING.getState(),
				OrderStatusDefine.CANCEL_APPLY.getState(),
				OrderStatusDefine.REJECT_APPLY.getState()
			};
		}
		return null;
	}
}
