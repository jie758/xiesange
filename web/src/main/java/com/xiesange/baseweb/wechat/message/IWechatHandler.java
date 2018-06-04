package com.xiesange.baseweb.wechat.message;

import com.xiesange.baseweb.wechat.WechatNode;
import com.xiesange.baseweb.wechat.pojo.Message;

public interface IWechatHandler {
	public Message service(WechatNode node);
}
