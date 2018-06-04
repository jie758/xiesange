package com.xiesange.baseweb.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiesange.baseweb.RequestContext;


public class SessionUtil {
	private static final String KEY_LOGIN_TOKEN = "_LOGIN_TOKEN";
	//请求上下文对象的线程变量存放
	private static ThreadLocal<RequestContext> TL_REQUEST = new ThreadLocal<RequestContext>();
	
	public static RequestContext getRequestContext(){
		return TL_REQUEST.get();
	}
	
	/*public static RequestContext createRequestContext(HttpServletRequest req, HttpServletResponse resp){
		RequestContext context = new RequestContext(req,resp);
		return context;
	}*/
	public static void setRequestContext(RequestContext context){
		TL_REQUEST.set(context);
	}
	
	/*public static void setLoginToken(HttpSession session,AccessToken token){
		session.setAttribute(KEY_LOGIN_TOKEN, token);
	}
	public static AccessToken getLoginToken(HttpSession session){
		return (AccessToken)session.getAttribute(KEY_LOGIN_TOKEN);
	}*/
	
}
