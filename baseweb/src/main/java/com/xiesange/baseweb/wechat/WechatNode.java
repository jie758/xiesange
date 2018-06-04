package com.xiesange.baseweb.wechat;

import com.xiesange.core.xml.BaseNode;

public class WechatNode extends BaseNode {

	public WechatNode(String tagName) {
		super(tagName);
	}
	
	public String getToUserName(){
		return this.getChildByTagName("ToUserName").getText();
	}
	
	public String getFromUserName(){
		return this.getChildByTagName("FromUserName").getText();
	}
	public String getCreateTime(){
		return this.getChildByTagName("CreateTime").getText();
	}

	public String getMsgType(){
		return this.getChildByTagName("MsgType").getText();
	}
	public String getEvent(){
		return this.getChildByTagName("Event").getText();
	}
}
