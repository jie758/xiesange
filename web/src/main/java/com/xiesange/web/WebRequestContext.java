package com.xiesange.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiesange.baseweb.RequestContext;
import com.xiesange.gen.dbentity.user.User;

public class WebRequestContext extends RequestContext {
	public WebRequestContext(HttpServletRequest request,HttpServletResponse response) {
		super(request, response);
	}
	
	public CustAccessToken getAccessToken() {
		return (CustAccessToken)accessToken;
	}
	
	public User getAccessUser(){
		CustAccessToken token = getAccessToken();
		return token==null?null:token.getUserInfo();
	}

}
