package com.xiesange.web.define;

import com.xiesange.core.enumdefine.IShortConsEnum;



/**
 * 定义常量值
 * @Description
 * @author wuyj
 * @Date 2013-11-4
 */
public class ConsDefine
{
	//活动类型
	public static enum ACTIVITY_TYPE implements IShortConsEnum{
		ORDER_BARGAIN{public short value(){return 1;}}//订单砍价
	}
	
	//优惠券类型
	public static enum COUPON_TYPE implements IShortConsEnum{
		PERCENT{public short value(){return 1;}},//折扣券，百分比
		CASH{public short value(){return 2;}}//抵价券
		;
	}
	
	//优惠券来源
	public static enum COUPON_EVENT implements IShortConsEnum{
		DEMAND{public short value(){return 1;}},//主动微信索取
		NEW_CUST{public short value(){return 2;}},//新用户
		ORDER{public short value(){return 3;}},//下单
		PAYED{public short value(){return 4;}},//支付成功
		RECOMMEND{public short value(){return 5;}},//推荐成功
		SYS{public short value(){return 99;}}//系统赠送
		;
	}
	
	//触发场景
	public static enum COUPON_TRIGGERTYPE implements IShortConsEnum{
		CUST{public short value(){return 1;}},//面向用户索取
		SYS{public short value(){return 2;}},//面向系统
		;
	}
	
	
	public static enum ORDER_BUYTYPE implements IShortConsEnum{
		SINGLE{public short value(){return 1;}},//单购
		TOGETHERBUY{public short value(){return 2;}},//拼单
		GROUPBUY{public short value(){return 3;}},//团购,
		PREORDER{public short value(){return 4;}}//预订
		;
	}
	
	public static enum PROMOTION_TYPE implements IShortConsEnum{
		FIRST{public short value(){return 1;}},//首次下单
		REACH{public short value(){return 2;}},//满
		BARGAIN{public short value(){return 3;}},//砍价
		GROUP{public short value(){return 4;}},//团购
		TOGETHER{public short value(){return 5;}},//拼单
		
		//GROUP_CREATOR{public short value(){return 4;}},//团购发起者
		
		;
	}
	
	
	public static enum PROMOTION_ACTION implements IShortConsEnum{
		REDUCE{public short value(){return 1;}},//减
		DISCOUNT{public short value(){return 2;}},//折
		MORE{public short value(){return 3;}},//送
		FIXPRICE{public short value(){return 4;}},//优惠单价
		;
	}
	
}
