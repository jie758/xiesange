package com.xiesange.web.service;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.component.VCodeCmp;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.sys.SysLogin;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.gen.dbentity.wx.WxOauthUser;
import com.xiesange.web.CustAccessToken;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.UserCmp;
import com.xiesange.web.define.ParamDefine;

@ETServiceAnno(name = "auth", version = "")
/**
 * 用户服务类
 * @author Wilson 
 * @date 上午9:48:42
 */
public class AuthService extends AbstractService {
	/**
	 * 通过手机号+验证码登录;
	 * 用户信息,如果是老用户则返回用户资料，如果是新用户返回null;
	 *  该接口都会自动触发登录操作，并返回登录token和skey
	 * 
	 * @param context
	 *            mobile,手机号，必填, 
	 *            vcode,验证码，必填
	 *            openid
	 * @return
	 * @throws Exception
	 * @author Wilson
	 * @date 下午1:19:04
	 */
	public ResponseBody loginByMobile(WebRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
				ParamDefine.Common.mobile, ParamDefine.Common.vcode);
		String mobile = reqbody.getString(ParamDefine.Common.mobile);
		String vcode = reqbody.getString(ParamDefine.Common.vcode);
		String openid = reqbody.getString(ParamDefine.Wechat.openid);

		VCodeCmp.checkMobileVCode(vcode, mobile);

		User userEntity = UserCmp.queryByMobile(mobile);
		if(!userEntity.getWechat().equals(openid)){
			userEntity.setWechat(openid);
			dao().updateById(userEntity, userEntity.getId());
		}
		
		// 老用户则登录
		SysLogin sysLogin = userEntity==null ? null : CCP.createLogin(userEntity.getId(),BaseConsDefine.SYS_TYPE.XIESANGE, context.getRequestChannel());

		return new ResponseBody("user", userEntity)
					.add("skey",sysLogin==null?null:sysLogin.getSignKey())
					.add("token",sysLogin==null?null:sysLogin.getToken());
	}
	
	
	/**
	 * 根据微信的oauth code来登录。
	 * 如果当前oauth code对应的用户不存在则会自动创建，并自动登录
	 * @param context
	 * 			oauth_code
	 * @return
	 * @throws Exception
	 */
	public ResponseBody loginByOAuthCode(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		String oauthCode = reqbody.getString(ParamDefine.Wechat.oauth_code);
		Short needCreateNew = reqbody.getShort(ParamDefine.Wechat.need_create_new);
		
		WxOauthUser accessToken = NullUtil.isEmpty(oauthCode) ? null : WechatCmp.getOAuthInfo(oauthCode);
		if(accessToken == null || NullUtil.isEmpty(accessToken.getOpenid())){
			throw ETUtil.buildException(BaseErrorDefine.SYS_OPEN_FROM_WX);
		}
		String openid = accessToken.getOpenid();
		User userEntity = UserCmp.queryByOpenid(openid, User.JField.id,User.JField.wechat,User.JField.mobile,User.JField.name,User.JField.address,User.JField.role);
		
		if(userEntity == null && needCreateNew == 1){
			userEntity = UserCmp.createUser(context, null,null,null);
			userEntity.setWechat(openid);
			dao().insert(userEntity);
			ETUtil.clearDBEntityExtraAttr(userEntity);
		}
		
		User returnUser = null;
		SysLogin sysLogin = null;
		if(userEntity != null){
			returnUser = new User();
			returnUser.setMobile(userEntity.getMobile());
			returnUser.setAddress(userEntity.getAddress());
			returnUser.setName(userEntity.getName());
			boolean isAdmin = userEntity.getRole()!=null && userEntity.getRole() == BaseConsDefine.USER_ROLE.ADMIN.value();
			returnUser.addAttribute("isAdmin",isAdmin?1:0);
			
			//创建登录
			sysLogin = CCP.createLogin(userEntity.getId(),BaseConsDefine.SYS_TYPE.XIESANGE, context.getRequestChannel());
		}
		
		ResponseBody respBody = new ResponseBody();
		
		
		respBody.add("user", returnUser)
				.add("openid",openid)
				.add("skey",sysLogin==null?null:sysLogin.getSignKey())
				.add("token",sysLogin==null?null:sysLogin.getToken())
				;
		
		return respBody;
				
	}
	
	public ResponseBody loginByToken(WebRequestContext context) throws Exception{
		CustAccessToken token = context.getAccessToken();
		SysLogin sysLogin = token.getLoginInfo();
		User userEntity = token.getUserInfo();
		
		User returnUser = null;
		if(userEntity != null){
			returnUser = new User();
			returnUser.setMobile(userEntity.getMobile());
			returnUser.setAddress(userEntity.getAddress());
			returnUser.setName(userEntity.getName());
			boolean isAdmin = userEntity.getRole()!=null && userEntity.getRole() == BaseConsDefine.USER_ROLE.ADMIN.value();
			returnUser.addAttribute("isAdmin",isAdmin?1:0);
			
		}
		
		return new ResponseBody("user",returnUser)
						.add("skey",sysLogin==null?null:sysLogin.getSignKey())
						.add("token",sysLogin==null?null:sysLogin.getToken())
		;

	}
	/**
	 * 根据oauth_code已经查询过一次所返回的token来登录。比loginByOAuthCode步骤少一步，更高效。
	 * 如果当前oauth code对应的用户不存在则会自动创建，并自动登录
	 * @param context
	 * 			oauth_token
	 * 			openid
	 * @return
	 * @throws Exception
	 */
	/*public ResponseBody loginByOAuthToken(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
				ParamDefine.Wechat.oauth_token,
				ParamDefine.Wechat.openid);
		String oAuthToken = reqbody.getString(ParamDefine.Wechat.oauth_token);
		String openid = reqbody.getString(ParamDefine.Wechat.openid);
		
		VendorWxOauth accessToken = WechatComponent.queryOAuthInfoByToken(oAuthToken);
		if(accessToken == null || !openid.equals(accessToken.getOpenid())){
			return null;
		}
		User userEntity = UserCmp.queryByOpenid(openid, User.JField.id,User.JField.wechat,User.JField.mobile,User.JField.nickname,User.JField.address);
		if(userEntity == null){
			userEntity = UserCmp.createUser(context, null,null,null);
			userEntity.setWechat(openid);
			dao().insert(userEntity);
		}
		//创建登录
		SysLogin sysLogin = CCP.createLogin(userEntity.getId(),BaseConsDefine.SYS_TYPE.XIESANGE, context.getRequestChannel());
		
		return new ResponseBody("user", userEntity)
						.add("skey",sysLogin.getSignKey())
						.add("token",sysLogin.getToken());
	}*/
	
	
}
