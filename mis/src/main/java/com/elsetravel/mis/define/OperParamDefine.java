package com.elsetravel.mis.define;

import com.elsetravel.core.IParamEnum;



/**
 * 定义运营需求的请求参数
 * @Description
 * @author wuyj
 * @Date 2015-11-4
 */
public class OperParamDefine
{
	public static enum Common implements IParamEnum{
		code,
		wechat_openid,
		profile_pic,
		name,
		mobile,
		email
	}
	public static enum Vangogh implements IParamEnum{
		intro,
		signup_id,
		vote_count
	}
}
