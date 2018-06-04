package com.xiesange.baseweb.notify;

public class NotifyReseponse {
	private String code;
	private String message;
	
	public NotifyReseponse(){};
	
	public NotifyReseponse(String code,String message){
		this.code = code;
		this.message = message;
	};
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
