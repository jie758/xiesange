package com.xiesange.baseweb.wechat.define;

import com.xiesange.core.enumdefine.IParamEnum;

public class WechatParamDefine {
	public static enum COMMON implements IParamEnum{
		access_token,
		appid,
		secret,
		openid,
		type
		
	}
	public static enum GET_ACCESSTOKEN implements IParamEnum{
		grant_type
	}
	
	public static enum GET_USER implements IParamEnum{
		openid,
		lang
	}
	
	//获取素材
	public static enum GET_MATERIAL implements IParamEnum{
		access_token,
		type,
		offset,
		count
	}
	
	//获取OAuth认证token
	public static enum GET_OAUTHTOKEN implements IParamEnum{
		grant_type,
		code
	}
}
