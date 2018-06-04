package com.xiesange.baseweb.exception;

import java.text.MessageFormat;
import java.util.Properties;

import com.xiesange.core.util.FileUtil;
import com.xiesange.core.util.NullUtil;

public class ErrorManager {
	private static Properties errorProp = null;
	
	public static void init(String classPath) throws Exception{
		errorProp = FileUtil.loadProperties("classpath:"+classPath);
	}
	
	public static String getMessage(long errorCode){
		if(NullUtil.isEmpty(errorProp))
			return null;
		return errorProp.getProperty(String.valueOf(errorCode));
	}
	
	public static String parseMessage(long errorCode,Object... params){
		String text = getMessage(errorCode);
		if(NullUtil.isNotEmpty(text)){
			text = MessageFormat.format(text, params);
		}
		return text;
	}
}
