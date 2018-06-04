package com.xiesange.baseweb.exception;

import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.core.exception.IXSGException;


public class SysErrorXSGException extends RuntimeException implements IXSGException {
	public SysErrorXSGException(String causedMessage) {
		super(causedMessage);
	}

	@Override
	public long getErrorCode() {
		return BaseErrorDefine.SYS_ERROR.getCode();
	}
	
	public String getMessage(){
		return ErrorManager.parseMessage(BaseErrorDefine.SYS_ERROR.getCode());
	}
}
