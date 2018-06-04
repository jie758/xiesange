package com.xiesange.web;

import com.xiesange.baseweb.IAccessToken;
import com.xiesange.gen.dbentity.sys.SysLogin;
import com.xiesange.gen.dbentity.user.User;

/**
 * WEB系统中的通行令牌，登录成功后就会存到session里
 * 所有访问WEB系统的交易，必须在session中存在一份通行令牌，且传入的token值要和session中的令牌对象里的值一致才能继续访问，否则会被拒绝
 * @author wuyujie Nov 19, 2014 3:58:02 PM
 *
 */
public class CustAccessToken implements IAccessToken{
	private SysLogin loginInfo;//登录信息
	private User userInfo;//登录的用户信息
	
	public CustAccessToken(){
	}
	
	public CustAccessToken(SysLogin loginInfo,User userInfo){
		this.loginInfo = loginInfo;
		this.userInfo = userInfo;
	}
	
	public SysLogin getLoginInfo() {
		return loginInfo;
	}
	public void setLoginInfo(SysLogin loginInfo) {
		this.loginInfo = loginInfo;
	}
	public User getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(User userInfo) {
		this.userInfo = userInfo;
	}

	public Long getAccessUserId() {
		return userInfo == null ? null : userInfo.getId();
	}
	
}
