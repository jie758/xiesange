package com.elsetravel.mis.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.elsetravel.baseweb.IAccessToken;
import com.elsetravel.baseweb.RequestContext;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.request.AbstractController;
import com.elsetravel.baseweb.request.RequestParam;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.mis.component.MisCmp;
@Controller
@RequestMapping("/web")
public class WebController extends AbstractController{
	public RequestParam parseRequestParameter(HttpServletRequest request,boolean isEncrypt) throws Exception{
		return RequestUtil.createRequestParam(request,isEncrypt);
	}

	@Override
	public IAccessToken buildAccessToken(String token,RequestParam reqParam) throws Exception {
		if(NullUtil.isEmpty(token)){
			return null;
		}
		
		MisAccessToken accessToken = MisCmp.queryMisAccessToken(token,BaseConstantDefine.SYS_TYPE_MIS);
		
		if(accessToken == null || accessToken.getLoginInfo() == null || accessToken.getUserInfo() == null){
			return null;
		}
		
		return accessToken;
	};
	
	protected void checkVersion(RequestParam reqParam) throws Exception{
		return;//MIS系统不做版本检测
	}
	
	protected void checkSignature(RequestParam requestParam,String signKey){
		return;//MIS系统不做签名检测
	}
	
	@Override
	public RequestContext createRequestContext(HttpServletRequest request,
			HttpServletResponse response, RequestParam requestParam)
			throws Exception {
		return new MisRequestContext(request,response);
	}
	
	protected short getSysType() {
		return BaseConstantDefine.SYS_TYPE_MIS;
	};
}
