package com.xiesange.baseweb.notify;


public class NotifyDefine {
	public static enum CodeDefine implements INotifyCodeDefine{
		sys_notify,
		vcode,
		order_payed,//订单支付完成
		order_adjust_price,//调价
		//commit_order_4sys,
		order_remind_pay,//支付提醒
		//remind_fillpay,//支付提醒
		order_remind_comment,//评价提醒
		//allocate_coupons,//赠送优惠券
		//delivery_depart,
		///delivery_arrive,
		//groupbuy_close,
		//groupbuy_arrival,
		//order_modify,
		//sys_commit_order,
		//send_delivery_contact
	}
	
	public static enum Common{
		name,
		create_time,
		create_date
	}
	
	public static enum Delivery_Depart{
		city,
		order_code,
		order_time,
		address
	}
	public static enum Delivery_Arrive{
		time,
		node,
		order_code,
		delivery_no
	}
	
	public static enum Groupbuy_Close{
		order_id,
		groupbuy_name,
		creator_name,
		number,
		product_name
	}
	
	public static enum Groupbuy_Arrival{
		order_id,
		groupbuy_name,
		creator_name,
		creator_mobile,
		product_name,
		address
	}
}
