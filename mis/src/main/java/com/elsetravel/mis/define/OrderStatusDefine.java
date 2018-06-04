package com.elsetravel.mis.define;


/**
 * 订单状态定义
 * @author Think
 *
 */
public enum OrderStatusDefine{
	EDIT(0,"待支付"),//草稿状态
	PAYED(1,"已支付"),//已支付，待导游确认
	CONFIRMED(2,"导游已接单"),//导游已确认接单
	STARTING(3,"服务中"),//游程开始
	END(4,"待双方评价"),//游程结束
	VISITOR_COMMENTTED(5,"游客已评价"),//游客已评论，导游未评论
	GUIDER_COMMENTTED(6,"导游已评价"),//导游已评论，游客未评论
	CANCEL_APPLY(7,"退订审核中"),//游客申请退订的时候需要申请，待客服审核通过才能真正退订
	REJECT_APPLY(8,"推单审核中"),//导游拒单的时候需要申请，待客服审核通过才能真正取消
	COMPLETED(99,"已完成"),//双方都已评论,整个订单结束
	REJECTED(-1,"导游已推单"),
	CANCELED(-2,"游客已退订")
	;
	
	
	private short state;//订单状态枚举值
	private String text;//订单状态文本值
	
	/**
	 * 
	 * @param state，订单状态枚举值
	 * @param text,订单当前状态文本值
	 */
	private OrderStatusDefine(int state,String text){
		this.state = (short)state;
		this.text = text;
	}
	
	public short getState() {
		return state;
	}
	public String getText() {
		return text;
	}
}
