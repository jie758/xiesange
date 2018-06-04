package com.xiesange.web.define;

import com.xiesange.core.enumdefine.IParamEnum;



/**
 * 定义请求参数枚举，业务侧使用RequestBody.getXXX(ParamDefine.Common.xxx)方式来获取
 * @Description
 * @author wuyj
 * @Date 2013-11-4
 */
public class OpParamDefine
{
	public static enum Common implements IParamEnum{
		oauth_code,
		mobile,
		name,
		address,
		nickname,
		vcode,
		openid,
		ext1,
		ext2,
		ext3
	}
	
	public static enum G20 implements IParamEnum{
		name,
		address,
		signup_id,
		vote_signup_id,
		vote_openid
	}
}
