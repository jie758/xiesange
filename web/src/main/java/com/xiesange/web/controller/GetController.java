package com.xiesange.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.xiesange.baseweb.IAccessToken;
import com.xiesange.baseweb.RequestContext;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.request.AbstractController;
import com.xiesange.baseweb.request.RequestParam;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.orm.statement.update.UpdateStatement;
import com.xiesange.web.CustAccessToken;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.WebCmp;
@Controller
@RequestMapping("/portal")
public class GetController extends AbstractController{
	
	@RequestMapping(value = "/**",method={RequestMethod.GET})
	public ModelAndView acceptRequest(HttpServletRequest request,HttpServletResponse response) throws Exception{
		return super.acceptRequest(request, response);
	}
	
	public RequestContext createRequestContext(HttpServletRequest request,HttpServletResponse response,RequestParam requestParam) throws Exception{
		WebRequestContext context = new WebRequestContext(request,response);
		/*if(needSn){
			context.getSn(true);//必须在外层去获取sequence，如果在serviceProxy内层事务里获取的话，那么内层抛错后会导致sequence的更新也回滚了
		}*/
		return context;
	};
	
	
	public RequestParam parseRequestParameter(HttpServletRequest request,boolean isEncrypt) throws Exception{
		return RequestUtil.createRequestParam(request,isEncrypt);
	}

	@Override
	public IAccessToken buildAccessToken(String token,RequestParam reqParam) throws Exception {
		if(NullUtil.isEmpty(token)){
			return null;
		}
		CustAccessToken accessToken = WebCmp.queryCustAccessToken(token,getSysType());
		
		if(accessToken == null || accessToken.getLoginInfo() == null || accessToken.getUserInfo() == null){
			return null;
		}
		
		//更新当前用户的版本号以及最新活跃时间，不调用DBHelper，因为不用插入历史记录，提高性能
		User updateUser = new User();
		//updateUser.setAppVersion(reqParam.getHeader().getApp_version());
		updateUser.setActiveTime(DateUtil.now());
		
		//ETUtil.dealUpdateEntity(updateUser);
		new UpdateStatement(updateUser,accessToken.getUserInfo().getId()).execute();
		
		return accessToken;
	}


	@Override
	protected BaseConsDefine.SYS_TYPE getSysType() {
		return BaseConsDefine.SYS_TYPE.XIESANGE;
	};
	
	
	
}
