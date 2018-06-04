package com.xiesange.baseweb.request;

import java.util.HashMap;
/**
 * 返回对象
 * @Description
 * @author wuyj
 * @Date 2013-11-18
 */
public class ResponseBody extends HashMap<String,Object>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ResponseBody(){
    }
    
    public ResponseBody(String key,Object value){
    	this.put(key, value);
    }
    
    public ResponseBody add(String key,Object value){
    	this.put(key, value);
    	return this;
    }
    public ResponseBody addTotalCount(long value){
    	this.put("totalCount", value);
    	return this;
    }
    
}
