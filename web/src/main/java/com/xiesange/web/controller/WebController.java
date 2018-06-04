package com.xiesange.web.controller;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xiesange.baseweb.IAccessToken;
import com.xiesange.baseweb.RequestContext;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.pojo.UrlNode;
import com.xiesange.baseweb.request.AbstractController;
import com.xiesange.baseweb.request.RequestHeader;
import com.xiesange.baseweb.request.RequestParam;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.baseweb.wechat.pojo.Signature;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.orm.statement.update.UpdateStatement;
import com.xiesange.web.CustAccessToken;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.AuthCmp;
import com.xiesange.web.component.BaseDataCmp;
import com.xiesange.web.component.WebCmp;
import com.xiesange.web.pojo.AuthInfo;
@Controller
@RequestMapping("/web")
public class WebController extends AbstractController{
	
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
		CustAccessToken accessToken = WebCmp.queryCustAccessToken(token,BaseConsDefine.SYS_TYPE.XIESANGE);
		
		if(accessToken == null || accessToken.getLoginInfo() == null || accessToken.getUserInfo() == null){
			return null;
		}
		
		//更新当前用户的版本号以及最新活跃时间，不调用DBHelper，因为不用插入历史记录，提高性能
		User updateUser = new User();
		updateUser.setActiveTime(DateUtil.now());
		if(!reqParam.getHeader().getDevice_type().equalsIgnoreCase(accessToken.getUserInfo().getDevice())){
			updateUser.setDevice(reqParam.getHeader().getDevice_type());
		}
		
		//ETUtil.dealUpdateEntity(updateUser);
		new UpdateStatement(updateUser,accessToken.getUserInfo().getId()).execute();
		
		return accessToken;
	}


	@Override
	protected BaseConsDefine.SYS_TYPE getSysType() {
		return BaseConsDefine.SYS_TYPE.XIESANGE;
	};
	
	
	public IAccessToken checkAuth(UrlNode urlNode,RequestParam reqParam) throws Exception{
		CustAccessToken accessToken = null;
		if(NullUtil.isNotEmpty(reqParam.getHeader().getWx_oauth_code())){
			//如果有微信oauthcode，则自动认证身份
			accessToken = AuthCmp.loginByOAuthCode(reqParam.getHeader().getWx_oauth_code());
		}else{
			accessToken = (CustAccessToken)super.checkAuth(urlNode, reqParam);
		}
		if(accessToken == null){
			return null;
		}
		/*User user = accessToken.getUserInfo();
		UserInfo wxUser = WechatCmp.getUser(user.getWechat());
		if(NullUtil.isEmpty(user.getPic())){
			if(NullUtil.isNotEmpty(wxUser.getHeadimgurl())){
				user.setPic(wxUser.getHeadimgurl());
				DBHelper.getDao().updateById(new FieldUpdateExpression[]{
						new FieldUpdateExpression(User.JField.pic,wxUser.getHeadimgurl())}, user.getId());
			}
		}
		user.setIsSubscribe(wxUser.getSubscribe());*/
		return accessToken;
	}
	
	protected void checkSignature(RequestParam requestParam,String signKey) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		//如果有oauth_code值，那么肯定会重新校验身份信息，肯定会重新生成token，所以client传过来的skey肯定就不对了，所以不用做签名校验
		if(NullUtil.isEmpty(requestParam.getHeader().getWx_oauth_code())){
			super.checkSignature(requestParam, signKey);
		}
	}
	
	
	/**
	 * 可以根据前端参数，在公用侧添加一些应答信息。比如基础数据、微信签名
	 */
	protected Object parseOutput(Object result,RequestContext context) throws Exception{
		ResponseBody responseBody = (ResponseBody)result;
		RequestHeader header = context.getRequestHeader();
		
		//添加基础数据
		if(header.getBaseparam_flag() != null || header.getEnum_flag() != null){
			Long baseparamFlag = header.getBaseparam_flag()==null?-1:header.getBaseparam_flag();
			Long enumFlag = header.getEnum_flag()==null?-1:header.getEnum_flag();
			BaseDataCmp.checkBaseData(baseparamFlag, enumFlag, responseBody);
		}
		//添加微信签命信息
		if(NullUtil.isNotEmpty(header.getWx_signature_url())){
			Signature signature = WechatCmp.buildSignature(header.getWx_signature_url());
			responseBody.add("wxSignature", signature);
		}
		//添加身份认证
		if(NullUtil.isNotEmpty(header.getWx_oauth_code())){
			//微信oauth认证过来，则处理身份问题
			CustAccessToken accessToken = (CustAccessToken)context.getAccessToken();
			AuthInfo authInfo = new AuthInfo(
					accessToken.getUserInfo(),
					accessToken.getLoginInfo().getToken(),
					accessToken.getLoginInfo().getSignKey()
			);
			responseBody.add("auth", authInfo);
		}
		
		return super.parseOutput(result, context);
		
	}
	
}
