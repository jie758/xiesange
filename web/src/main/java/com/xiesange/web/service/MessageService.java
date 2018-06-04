package com.xiesange.web.service;

import com.xiesange.baseweb.component.VCodeCmp;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.define.ParamDefine;
@ETServiceAnno(name="msg",version="")
/**
 * 信息交互相关的服务类，包括短信、IM、邮件各种交互方式的功能
 * @author Wilson 
 * @date 上午9:48:42
 */
public class MessageService extends AbstractService {
	/**
	 * 发送注册时手机短信验证码。只支持单条。
	 * @param context
	 * 			mobile,手机号，必填
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:19:04
	 */
	public ResponseBody sendVCode(WebRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(),ParamDefine.Common.mobile);
		String mobile = context.getRequestBody().getString(ParamDefine.Common.mobile);
		
		VCodeCmp.sendVCode(mobile,86,"30m");
		
		return null;//new ResponseBody("vcode",vcode);
	}
	
	public ResponseBody checkVCode(WebRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(),
				ParamDefine.Common.mobile,
				ParamDefine.Common.vcode);
		String mobile = context.getRequestBody().getString(ParamDefine.Common.mobile);
		String vcode = context.getRequestBody().getString(ParamDefine.Common.vcode);
		
		VCodeCmp.checkMobileVCode(vcode, mobile);
		return null;//new ResponseBody("vcode",vcode);
	}
	
}
