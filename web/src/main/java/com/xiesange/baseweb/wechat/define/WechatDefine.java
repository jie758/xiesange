package com.xiesange.baseweb.wechat.define;

import com.xiesange.core.enumdefine.IParamEnum;

public class WechatDefine {
	public static final String WECHAT_APP_ID = "wx32b6780d93247483";
	public static final String WECHAT_SECRET = "7f1cfda415cb695c82253114c42a6f85";
	
	public static final String MSG_TYPE_EVENT = "event";//事件消息
	public static final String MSG_TYPE_TEXT = "text";//文本消息
	
	
	public static final int REPLY_TYPE_TEXT = 1;//回复文本
	public static final int REPLY_TYPE_IMAGE = 2;//回复图片
	public static final int REPLY_TYPE_ARTICLE = 3;//图文消息
	
	public static final String TEMPMSG_PAY_SUCCESS = "FkDevLr1v5f0F0g97vI7BTX7sZIv7odlkT-cB-DaRkA";
	public static final String TEMPMSG_COUPON_EXPIRE = "YE9KvyRHdca98Xn3w93w2TuSUDgylxv9-CE3Jxtf11U";
	public static final String TEMPMSG_COUPON_ALLOCATE = "YE9KvyRHdca98Xn3w93w2TuSUDgylxv9-CE3Jxtf11U";
	public static final String TEMPMSG_PAY_REMIND = "tj7RaO1X0UW6yRu14rqc6lYs209oU3KCIaSnZ_zk7lA";
	
	
	public static enum VendorConfigType {
		wx_access_token,
		wx_jsapi_ticket,
		rong_token,
		wxid,
		wxsecret
	}

	
	public static final String TOKEN = "elsetravel";
	
	public static final String MCH_ID = "1321696201";//来自商户平台
	public static final String SIGN_KEY = "elsetravel2015development1234567";//来自商户API菜单
	
	public static final String URL_GET_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token";
	public static final String URL_CREATE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/create";
	public static final String URL_GET_USER = "https://api.weixin.qq.com/cgi-bin/user/info";
	public static final String URL_GET_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	public static final String URL_GET_ALL_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material";
	public static final String URL_GET_OAUTH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static final String URL_GET_USERINFO = "https://api.weixin.qq.com/sns/userinfo";
	public static final String URL_SEND_UNIFIEDORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	public static final String URL_SEND_QUERYORDER = "https://api.mch.weixin.qq.com/pay/orderquery";
	public static final String URL_GET_USERLIST = "https://api.weixin.qq.com/cgi-bin/user/get";
	public static final String URL_CREATE_QRCODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
	public static final String URL_ADD_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/add_material";
	public static final String URL_SEND_TEMP_MESSSGE = "https://api.weixin.qq.com/cgi-bin/message/template/send";
	
	
	
	public static enum MESSAGE_TYPE implements IParamEnum{
		text,
		image,
		location,
		link,
		voice,
		event
	}
	
	public static enum BUTTON_TYPE implements IParamEnum{
		click,
		view
	}
	
	public static enum EVENT_TYPE implements IParamEnum{
		SUBSCRIBE,
		UNSUBSCRIBE,
		CLICK
	}
	
	public static enum AUTH_TYPE implements IParamEnum{
		snsapi_base,
		snsapi_userinfo
	}
	
	public static enum MESSAGE_COMMON implements IParamEnum{
		first,
		keyword1,
		keyword2,
		remark
	}
	
	/*public static enum TOKEN_TYPE implements IParamEnum{
		access_token,
		jsapi_ticket
	}*/
	
}
