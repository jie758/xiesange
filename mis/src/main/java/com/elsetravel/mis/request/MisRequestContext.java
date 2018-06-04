package com.elsetravel.mis.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elsetravel.baseweb.RequestContext;
import com.elsetravel.gen.dbentity.mis.MisStaff;

public class MisRequestContext extends RequestContext {
	public MisRequestContext(HttpServletRequest request,HttpServletResponse response) {
		super(request, response);
	}

		
	public MisStaff getAccessUser(){
		MisAccessToken token = getAccessToken();
		return token==null?null:token.getUserInfo();
	}
	
	public MisAccessToken getAccessToken() {
		return (MisAccessToken)accessToken;
	}
}
