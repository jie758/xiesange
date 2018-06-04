package com.xiesange.baseweb.exception;

import com.xiesange.baseweb.define.IErrorCodeEnum;
import com.xiesange.core.exception.IXSGException;



public class XSGException extends RuntimeException implements IXSGException{
	private static final long serialVersionUID = 6055725172187118732L;
	private IErrorCodeEnum errorEnum;
	private Object[] errorMsgParams;
	
	public XSGException(Throwable e){
		super(e);
	};
	public XSGException(IErrorCodeEnum errorEnum,Object...errorMsgParams){
		this.errorEnum = errorEnum;
		this.errorMsgParams = errorMsgParams;
	};
	public String getMessage(){
		return ErrorManager.parseMessage(errorEnum.getCode(),errorMsgParams);
	}
	
	public String getLocalizedMessage(){
		return new StringBuffer("[").append(this.getErrorCode())
				.append("]")
				.append(":")
				.append(getMessage()).toString();
	}
	@Override
	public long getErrorCode() {
		return errorEnum.getCode();
	}
	
	
}
