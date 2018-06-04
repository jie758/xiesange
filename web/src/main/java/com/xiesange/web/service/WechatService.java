package com.xiesange.web.service;

import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;

@ETServiceAnno(name="wx",version="")
public class WechatService extends AbstractService{
	/**
	 * 微信oauth验证后获取用户信息
	 * @param context
	 * 			code,微信自动附加上的code
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午8:55:55
	 */
	/*public ResponseBody getUserInfo(WebRequestContext context) throws Exception{
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		if(NullUtil.isEmpty(code)){
			throw ETUtil.buildException(BaseErrorDefine.OPEN_FROM_WX);
		}
		
		VendorWxOauth accessToken = WechatCmp.getOAuthInfo(code);
		if(accessToken == null || NullUtil.isEmpty(accessToken.getOpenid())){
			throw ETUtil.buildException(BaseErrorDefine.OPEN_FROM_WX);
		}
		
		UserInfo userInfo = null;
		if(accessToken.getScope() == 2){
			//显示授权，可以拿到更具体的用户资料
			userInfo = WechatCmp.accessUserInfo(accessToken.getToken(), accessToken.getOpenid());
		}else{
			userInfo = new UserInfo();
			userInfo.setOpenid(accessToken.getOpenid());
		}
		
		return new ResponseBody("user",userInfo);
	}*/
	
	/**
	 * 获取微信配置相关信息
	 * @param context
	 * 			url,当前微信界面打开的url
	 * 			code，oAuthCode，如果有的话传入，这个有值则需要查询微信用户相关信息
	 * 			need_user,是否还需要查询出用户资料 
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody getSignature(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody,
				ParamDefine.Wechat.url);
		String url = reqbody.getString(ParamDefine.Wechat.url);
		Signature signature = WechatCmp.buildSignature(url);
		
		return new ResponseBody("signature",signature);
					.add("openid", accessToken.getOpenid())
					.add("oauthToken", accessToken.getToken())
	}*/
	
	
	/*public ResponseBody getSignature2(WebRequestContext context) throws Exception{
		String url = context.getRequestBody().getString(ParamDefine.Wechat.url);
		Signature signature = WechatComponent.buildSignature(url);
		
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		Short needUser = context.getRequestBody().getShort(ParamDefine.Wechat.need_user);
		String openid = null;
		User userEntity = null;
		SysLogin sysLogin = null;
		
		if(NullUtil.isNotEmpty(code)){
			VendorWxOauth accessToken = WechatComponent.getOAuthInfo(code);
			if(accessToken == null || NullUtil.isEmpty(accessToken.getOpenid())){
				throw ETUtil.buildException(BaseErrorDefine.OPEN_FROM_WX);
			}
			openid = accessToken.getOpenid();
		}
		
		
		if(openid != null && needUser != null && needUser == 1){
			userEntity = UserCmp.queryByOpenid(openid, User.JField.id,User.JField.wechat,User.JField.mobile,User.JField.name,User.JField.address);
			sysLogin = CCP.createLogin(userEntity.getId(),BaseConsDefine.SYS_TYPE.XIESANGE, context.getRequestChannel());
		}
		
		return new ResponseBody("signature",signature)
					.add("openid", openid)
					.add("user", userEntity)
					.add("token", sysLogin==null?null:sysLogin.getToken())
					.add("skey", sysLogin==null?null:sysLogin.getSignKey());
		//return new ResponseBody("signature",signature).add("appId", WechatDefine.APP_ID);
	}*/
	
	
	/*public ResponseBody getOAuthAccessToken(WebRequestContext context) throws Exception{
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		if(NullUtil.isEmpty(code)){
			return null;
		}
		OAuthToken accesToken = WechatComponent.accessOAuthToken(code);
		return new ResponseBody("result",accesToken == null ? null : accesToken.getAccess_token());
	}*/
}
