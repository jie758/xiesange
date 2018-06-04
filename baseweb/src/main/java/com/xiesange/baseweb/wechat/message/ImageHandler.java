package com.xiesange.baseweb.wechat.message;

import com.xiesange.baseweb.wechat.WechatNode;
import com.xiesange.baseweb.wechat.pojo.Message;
import com.xiesange.core.util.DateUtil;

public class ImageHandler implements IWechatHandler{
	public Message service(WechatNode node){
		Message message = new Message();
		
		message.setContent("您的订单图片已收到，我们会尽快下单，谢谢您的支持!");
		message.setToUserName(node.getFromUserName());  
        message.setFromUserName(node.getToUserName());  
        message.setCreateTime(DateUtil.now14());  
        message.setMsgType("text");
		
		return message;
	}
}
