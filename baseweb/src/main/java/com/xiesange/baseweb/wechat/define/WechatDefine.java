package com.xiesange.baseweb.wechat.define;

import com.xiesange.core.enumdefine.IParamEnum;

public class WechatDefine {
	/*public static final String APP_ID = "wx16a7e9439daf4285";
	public static final String SECRET = "3420716bfc56c369ab0f6295263be9e2";*/
	
	public static final String APP_ID = "wx32b6780d93247483";
	public static final String SECRET = "1a845d33f00c78a76d1fbe7c751c6e5b";
	public static final String TOKEN = "xiesange137";
	
	public static final String MCH_ID = "1285324401";
	public static final String SIGN_KEY = "6ba74da9a3506f16d0e3e2ff2ac00450";
	
	public static final String URL_GET_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token";
	public static final String URL_CREATE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/create";
	public static final String URL_GET_USER = "https://api.weixin.qq.com/cgi-bin/user/info";
	public static final String URL_GET_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	public static final String URL_GET_ALL_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material";
	public static final String URL_GET_OAUTH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static final String URL_GET_USERINFO = "https://api.weixin.qq.com/sns/userinfo";
	public static final String URL_SEND_UNIFIEDORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	public static final String URL_SEND_QUERYORDER = "https://api.mch.weixin.qq.com/pay/orderquery";
	public static final String URL_SEND_TEMP_MESSSGE = "https://api.weixin.qq.com/cgi-bin/message/template/send";
	public static final String URL_GET_BATCHUSER = "https://api.weixin.qq.com/cgi-bin/user/info/batchget";
	
	public static final String TEMPMSG_PAY_SUCCESS = "FkDevLr1v5f0F0g97vI7BTX7sZIv7odlkT-cB-DaRkA";
	public static final String TEMPMSG_COUPON_EXPIRE = "YE9KvyRHdca98Xn3w93w2TuSUDgylxv9-CE3Jxtf11U";
	public static final String TEMPMSG_COUPON_ALLOCATE = "YE9KvyRHdca98Xn3w93w2TuSUDgylxv9-CE3Jxtf11U";
	public static final String TEMPMSG_PAY_REMIND = "tj7RaO1X0UW6yRu14rqc6lYs209oU3KCIaSnZ_zk7lA";
	
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
	
	
	public static enum ORDER_PAY implements IParamEnum{
		keyword3
	}
	
	/*public static enum TOKEN_TYPE implements IParamEnum{
		access_token,
		jsapi_ticket
	}*/
	
}
