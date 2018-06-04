package com.xiesange.baseweb.wechat.message;

import com.xiesange.baseweb.wechat.WechatNode;
import com.xiesange.baseweb.wechat.pojo.Message;
import com.xiesange.core.util.DateUtil;

public class TextHandler implements IWechatHandler{
	public Message service(WechatNode node){
		Message message = new Message();
		
		message.setContent("谢谢您输入的文字!");
		message.setToUserName(node.getFromUserName());  
        message.setFromUserName(node.getToUserName());  
        message.setCreateTime(DateUtil.now_yyyymmddhhmmss());  
        message.setMsgType("text");
		
		return message;
	}
}
