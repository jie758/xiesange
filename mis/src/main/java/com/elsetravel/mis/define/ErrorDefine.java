package com.elsetravel.mis.define;

import com.elsetravel.baseweb.define.IErrorCodeEnum;

/**
 * 定义错误信息
 * 后台管理模块所有的业务错误都由3开头
 * @author Think
 *
 */
public enum ErrorDefine implements IErrorCodeEnum{
	//公用模块,3000开头
	CODE_DUPLICATE(300000),
	
	//登录模块,3100开头
	LOGIN_ACCOUNT_NOTEXIST(310001),
	LOGIN_PWD_NOT_MATCH(310002),
	
	//菜单模块,3101开头
	MENU_NOTEXIST(310101),
	MENU_PARENT_NOTEXIST(310102),
	
	//员工模块,3102开头
	STAFF_NOTEXIST(310201),
	
	//导游模块,3103开头
	GUIDER_NOTEXIST(310301),
	//订单模块,3104开头
	ORDER_NOTEXIST(310401),
	ORDER_STATUS_NOTALLOWED(310402),
	//票券模块，3105开头
	TICKET_NOTEXIST(310501),
	TICKET_INFO_MISSING(310502),
	TICKET_NOTALLOWED_REMOVE(310503),
	//专题模块，3106开头
	TOPIC_NOTEXIST(310601),
	TOPIC_INFO_MISSING(310602),
	//用户模块，3107开头
	USER_NOTEXIST(310701),
	//基础模块，3108
	CONFIG_NOTEXIST(310801),
	//商户模块,3109
	MERCHANT_NOTEXIST(310901),
	//达人分享会模块,3110
	SHARING_NOTEXIST(311001)
	;
	
	
	private String message;
	private long code;
	
	private ErrorDefine(long code){
		this.code = code;
		//this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public long getCode() {
		return code;
	}
}
