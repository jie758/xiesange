package com.xiesange.web.define;


/**
 * 订单状态定义
 * @author Think
 *
 */
public class OrderStatusDefine{
	public enum CUST{
		CANCELED(-1,"已取消"),
		EDIT(0,"待支付"),//草稿状态
		ADJUST_PRICE(98,"待支付"),//草稿(调价)状态
		PAYED(1,"已支付"),//已支付
		GROUPBUY_CLOSE(11,"已截单"),//适用于团购，表示已截单
		PACKING(2,"打包中"),
		DELIVERYING(3,"配送中"),
		COMPLETED(99,"已完成")//整个订单结束
		;
		
		
		private short state;//订单状态枚举值
		private String text;//订单状态文本值
		
		private CUST(int state,String text){
			this.state = (short)state;
			this.text = text;
		}
		
		public short state() {
			return state;
		}
		public String text() {
			return text;
		}
	}
	
	
	
	
	
	
}
