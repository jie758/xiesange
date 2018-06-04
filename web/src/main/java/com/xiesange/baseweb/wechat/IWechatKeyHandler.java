package com.xiesange.baseweb.wechat;

import com.xiesange.baseweb.wechat.pojo.Message;

public interface IWechatKeyHandler {
	public Message service(WechatNode node,int gzhType) throws Exception;
}
