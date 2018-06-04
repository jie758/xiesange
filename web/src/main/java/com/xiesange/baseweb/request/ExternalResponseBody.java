package com.xiesange.baseweb.request;

/**
 * 返回对象
 * @Description
 * @author wuyj
 * @Date 2013-11-18
 */
public class ExternalResponseBody extends ResponseBody
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private String value;
	public ExternalResponseBody(String value){
		this(value,null);
    }
    public ExternalResponseBody(String value,String type){
    	this.type = type;
    	this.value = value;
    }
    
    public String getType(){
    	return type;
    }
    
    public String getValue(){
    	return value;
    }
}
