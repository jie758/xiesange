package com.xiesange.web.define;

import com.xiesange.baseweb.define.ILangResourceEnum;

public enum LangSnipptDefine implements ILangResourceEnum{
	//导游域
	GUIDER_INFO_LACKOF_NAME(1001L,null),
	GUIDER_INFO_LACKOF_PIC(1002L,null),
	//用户域
	USER_INFO_LACKOF_NICKNAME(2001L,null),
	USER_INFO_LACKOF_GENDER(2002L,null),
	USER_INFO_LACKOF_EMAIL(2003L,null),
	USER_INFO_LACKOF_EMAILBIND(2004L,null),
	USER_INFO_LACKOF_BIRTHDAY(2005L,null),
	USER_INFO_LACKOF_CITY(2006L,null),
	//订单域
	ORDER_INFO_LACKOF_DATE(3001L,null),
	ORDER_INFO_LACKOF_AMOUNT(3002L,null),
	
	//旅票域
	TICKET_INFO_LACKOF_PIC(4001L,null),
	TICKET_INFO_LACKOF_NAME(4002L,null),
	TICKET_INFO_LACKOF_PRICE(4003L,null),
	TICKET_INFO_LACKOF_COSTTIME(4004L,null),
	TICKET_INFO_LACKOF_MAXNUMBER(4005L,null),
	TICKET_INFO_LACKOF_CITY(4006L,null),
	TICKET_INFO_LACKOF_CATALOG(4007L,null),
	TICKET_INFO_LACKOF_TIMES(4008L,null),
	TICKET_INFO_LACKOF_DATES(4009L,null),
	TICKET_INFO_LACKOF_DETAIL(4010L,null),
	
	;
	
	private String message;
	private long code;
	
	private LangSnipptDefine(long code,String message){
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
