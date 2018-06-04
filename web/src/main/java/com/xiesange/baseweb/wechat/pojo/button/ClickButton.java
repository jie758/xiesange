package com.xiesange.baseweb.wechat.pojo.button;

import com.xiesange.baseweb.wechat.define.WechatDefine;


public class ClickButton extends Button {
	private String key;
	
	public ClickButton(String name){
		super(name);
		this.setType(WechatDefine.BUTTON_TYPE.click.name());
	}
	
	public ClickButton(String name,String key){
		super(name);
		this.key = key;
		this.setType(WechatDefine.BUTTON_TYPE.click.name());
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	
}
