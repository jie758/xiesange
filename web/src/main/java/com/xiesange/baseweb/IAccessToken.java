package com.xiesange.baseweb;

import com.xiesange.gen.dbentity.sys.SysLogin;

public interface IAccessToken {
	public SysLogin getLoginInfo();
	
	public Long getAccessUserId();
}
