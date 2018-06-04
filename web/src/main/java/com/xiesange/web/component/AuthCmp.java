package com.xiesange.web.component;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.RequestContext;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.sys.SysLogin;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.gen.dbentity.wx.WxOauthUser;
import com.xiesange.orm.DBHelper;
import com.xiesange.web.CustAccessToken;

public class AuthCmp {
	/**
	 * 通过oauth_code登录
	 * @param oauthCode
	 * @return
	 * @throws Exception
	 */
	public static CustAccessToken loginByOAuthCode(String oauthCode) throws Exception{
		if(oauthCode == null){
			return null;
		}
		WxOauthUser oauthAccessToken = WechatCmp.getOAuthInfo(oauthCode);
		if(oauthAccessToken == null || NullUtil.isEmpty(oauthAccessToken.getOpenid())){
			throw ETUtil.buildException(BaseErrorDefine.SYS_OPEN_FROM_WX);
		}
		RequestContext context = ETUtil.getRequestContext();
		String openid = oauthAccessToken.getOpenid();
		User userEntity = UserCmp.queryByOpenid(openid, 
				User.JField.id,
				User.JField.wechat,
				User.JField.orderCount,
				User.JField.mobile,
				User.JField.name,
				User.JField.address,
				User.JField.isSubscribe,
				User.JField.role);
		
		if(userEntity == null){
			userEntity = UserCmp.createUser(context, null,null,null);
			userEntity.setWechat(openid);
			DBHelper.getDao().insert(userEntity);
		}
		
		
		boolean isAdmin = userEntity.getRole()!=null && userEntity.getRole() == BaseConsDefine.USER_ROLE.ADMIN.value();
		userEntity.addAttribute("isAdmin",isAdmin?1:0);
		
		//创建登录
		SysLogin sysLogin = CCP.createLogin(userEntity.getId(),BaseConsDefine.SYS_TYPE.XIESANGE, context.getRequestChannel());
		CustAccessToken accessToken = new CustAccessToken(sysLogin,userEntity);
		context.setAccessToken(accessToken);
		return accessToken;
	}
}
