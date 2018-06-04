package com.xiesange.baseweb.wechat.pojo;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.xiesange.core.util.ClassUtil;

public class TemplateMessageParam {
	private Map<String,Map<String,String>> data = ClassUtil.newMap();
	
	/*private String key;
	private String value;
	private String color;*/
	public TemplateMessageParam(){}
	public TemplateMessageParam(String key,String value){
		this(key,value,null);
	}
	public TemplateMessageParam(String key,String value,String color){
		Map<String,String> val = ClassUtil.newMap();
		val.put("value", value);
		val.put("color", color);
		data.put(key, val);
		
	}
	
	public TemplateMessageParam addParam(String key,String value){
		return addParam(key,value,null);
	}
	
	public TemplateMessageParam addParam(String key,String value,String color){
		Map<String,String> val = ClassUtil.newMap();
		val.put("value", value);
		val.put("color", color);
		data.put(key, val);
		return this;
	}
	public Map<String, Map<String, String>> getData() {
		return data;
	}
	public void setData(Map<String, Map<String, String>> data) {
		this.data = data;
	}
	
	public String buildText(){
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String,Map<String,String>>> it = data.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,Map<String,String>> entry = it.next();
			sb.append("|").append(entry.getKey()).append("=").append(entry.getValue().get("value"));
		}
		return sb.substring(1);
	}
}
