package com.xiesange.web.define;

import com.xiesange.baseweb.define.IErrorCodeEnum;

/**
 * 定义错误信息
 * @author Think
 *
 */
public enum ErrorDefine implements IErrorCodeEnum{
	COMMON_DUPLICATE_CODE(200000,"编码重复"),
	//登录模块,2100开头
	LOGIN_USER_NOT_EXIST(210001,"用户不存在或者不密码不正确"),
	LOGIN_PWD_NOT_MATCH(210002,"密码不正确"),
	//注册模块,2101开头
	REG_EMAIL_DUPLICATE(210101,"该邮箱已经被其它用户注册了"),
	REG_MOBILE_DUPLICATE(210102,"该手机号已经被其它用户注册了"),
	REG_WECHAT_DUPLICATE(210103,"该微信号已经被其它用户注册了"),
	REG_MOBILE_PWD_DUPLICATE(210104,""),
	
	//订单模块，2102开头
	ORDER_NOTEXIST(210201,"订单不存在 "),
	ORDER_STATUS_NOTALLOWED(210202,"订单状态异常， 不允许进行该操作"),
	ORDER_PRICE_CHANGED(210203,"产品价格有变动，请重新确认"),
	ORDER_NOT_ALLOWED_CANCEL(210204,"当前订单{0},无法取消"),
	ORDER_OFF(210205,"系统升级中，暂时不接受预订！如给您带来不便,敬请谅解。"),
	ORDER_HAS_PAYED(210206,"该订单已支付"),
	ORDER_COUPON_EXPIRED(210207,"该优惠券已过期"),
	ORDER_BARGAIN_REPEAT(210208,"您已经砍过价啦"),
	//运营模块,2103开头
	OP_EXPIRED(210301,"当前活动已结束"),
	OP_SIGNUP_DUPLICATE(210302,"您已参与此次活动的报名了"),
	OP_VOTE_DUPLICATE(210303,"您已为该用户投过票啦~"),
	OP_G20_CANNOT_VOTE(210304,"您是活动参与者，所以不能参与投票噢~赶紧邀请更多好友来助威吧~"),
	OP_SIGNUP_TIME(210305,"您是活动参与者，所以不能参与投票噢~赶紧邀请更多好友来助威吧~"),
	//用户模块,2104
	USER_NOTEXIST(210401,"用户不存在 "),
	//团购模块2105
	GROUPBUY_NOTEXIST(210501,"团购信息不存在~"),
	GROUPBUY_DUPLICATE_JOIN(210502,"您已经参与该团购~"),
	GROUPBUY_END(210503,"本次团购已经结束~"),
	GROUPBUY_RULE_EXPIRED(210504,"该活动团购方案已经下线"),
	GROUPBUY_NOTOPEN(210505,"该活动团购活动尚未生效"),
	GROUPBUY_NOT_MATCHED(210506,"当前尚未达到本次团购活动标准，请继续加油~"),
	;
	
	
	private String message;
	private long code;
	
	private ErrorDefine(long code,String message){
		this.code = code;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public long getCode() {
		return code;
	}
}
