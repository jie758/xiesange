package com.xiesange.orm;

import com.xiesange.core.exception.IException;
import com.xiesange.core.util.CommonUtil;

public class DBErrorException extends RuntimeException implements IException{
	private static final long serialVersionUID = 1L;
	private String sql;
	
	public DBErrorException(Exception e,String sql) {
		super(e);
		this.sql = sql;
	}
	
	public String getMessage(String lang){
		Throwable caused = CommonUtil.getCaused(this.getCause());
		return caused.getMessage()+" : "+sql;
	}

	@Override
	public long getErrorCode() {
		return 10004L;
	}
}
