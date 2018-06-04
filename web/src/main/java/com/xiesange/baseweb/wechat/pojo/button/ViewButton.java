package com.xiesange.baseweb.wechat.pojo.button;

import com.xiesange.baseweb.wechat.define.WechatDefine;


public class ViewButton extends Button {
	private String url;
	
	public ViewButton(String name){
		super(name);
		this.setType(WechatDefine.BUTTON_TYPE.view.name());
	}
	
	public ViewButton(String name,String url){
		super(name);
		this.url = url;
		this.setType(WechatDefine.BUTTON_TYPE.view.name());
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
