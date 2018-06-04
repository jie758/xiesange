package com.elsetravel.mis.request;

import com.elsetravel.baseweb.IAccessToken;
import com.elsetravel.gen.dbentity.mis.MisStaff;
import com.elsetravel.gen.dbentity.sys.SysLogin;

/**
 * WEB系统中的通行令牌，登录成功后就会存到session里
 * 所有访问WEB系统的交易，必须在session中存在一份通行令牌，且传入的token值要和session中的令牌对象里的值一致才能继续访问，否则会被拒绝
 * @author wuyujie Nov 19, 2014 3:58:02 PM
 *
 */
public class MisAccessToken implements IAccessToken{
	private SysLogin loginInfo;//登录信息
	private MisStaff staffInfo;//登录的用户信息
	
	public MisAccessToken(){
	}
	
	public MisAccessToken(SysLogin loginInfo,MisStaff userInfo){
		this.loginInfo = loginInfo;
		this.staffInfo = userInfo;
	}
	
	public SysLogin getLoginInfo() {
		return loginInfo;
	}
	public void setLoginInfo(SysLogin loginInfo) {
		this.loginInfo = loginInfo;
	}
	public MisStaff getUserInfo() {
		return staffInfo;
	}
	public void setUserInfo(MisStaff userInfo) {
		this.staffInfo = userInfo;
	}

	@Override
	public Long getAccessUserId() {
		return staffInfo == null ? null : staffInfo.getId();
	}
	
}
