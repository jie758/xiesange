package com.xiesange.baseweb.wechat.message;

import com.xiesange.baseweb.wechat.WechatNode;
import com.xiesange.baseweb.wechat.pojo.Message;

public class EventHandler implements IWechatHandler{

	@Override
	public Message service(WechatNode node) {
		String event = node.getEvent();
		if(event.equals("subscribe")){
			//关注
			
		}
		
		return null;
	}

}
