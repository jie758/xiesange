package com.xiesange.baseweb.wechat.pojo;

import java.util.Map;

import com.xiesange.core.util.ClassUtil;

public class TemplateMessageReq {
	private String touser;
	private String template_id;
	private String url;
	private String topcolor;
	private Map<String,Map<String,String>> data = ClassUtil.newMap();
	
	public TemplateMessageReq(String touser,String tempId,String url,Map<String,Map<String,String>> data){
		this.touser = touser;
		this.template_id = tempId;
		this.url = url;
		this.data = data;
	}
	
	/*public void addData(String key,String val){
		addData(key,val,null);
	}
	public void addData(String key,String val,String color){
		TemplateMessageParam param = new TemplateMessageParam(val,color);
		data.put(key, param);
	}*/


	public String getTouser() {
		return touser;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public String getUrl() {
		return url;
	}

	public String getTopcolor() {
		return topcolor;
	}

	public Map<String,Map<String,String>> getData() {
		return data;
	}




	/*private class TemplateMessageParam{
		private String value;
		private String color;
		
		public TemplateMessageParam(String value,String color){
			this.value = value;
			this.color = color;
		}

		public String getValue() {
			return value;
		}

		public String getColor() {
			return color;
		}
	}*/
}
