package com.elsetravel.mis.define;



/**
 * 定义常量值
 * @Description
 * @author wuyj
 * @Date 2013-11-4
 */
public class ConstantDefine{
	public static final short GUIDER_APPLY_NONE = 0;//未申请认证
	public static final short GUIDER_APPLY_APPROVING = 1;//认证申请中
	public static final short GUIDER_APPLY_APPROVED = 99;//审核通过，即已认证
	
	public static final short MERCHANT_APPLY_NONE = 0;//未申请认证
	public static final short MERCHANT_APPLY_APPROVING = 1;//认证审核中
	public static final short MERCHANT_APPLY_APPROVED = 99;//审核通过，即已认证
	
	//标签类型
	public static final short TAG_TYPE_TICKET = 1;
	public static final short TAG_TYPE_GUIDER = 2;
	
	public static final short APPROVE_REJECT = 0;//审核驳回
	public static final short APPROVE_PASS = 1;//审核通过
	
	//钱包明细
	public static final short BALANCEDETAIL_TYPE_ORDER = 1;//下单
	public static final short BALANCEDETAIL_TYPE_WITHDRAW = 2;//提现
	
	//验证码用途类型
	public static final short VCODE_TYPE_REGISTER = 1;
	public static final short VCODE_TYPE_CHANGEPWD = 2;
	public static final short VCODE_TYPE_BINDMOBILE = 3;
	public static final short VCODE_TYPE_BINDEMAIL = 4;
	public static final short VCODE_TYPE_ORDER = 5;
	public static final short VCODE_TYPE_MCH_QUERY = 6;
	
	//行程类型
	public static final short TRIP_TYPE_HALFDAY = 1;//半日游
	public static final short TRIP_TYPE_ONEDAY = 2;//一日游
	public static final short TRIP_TYPE_MANYDAY = 3;//多日游
}
