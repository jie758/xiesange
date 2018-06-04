package com.elsetravel.mis.test.frame;

import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.web.servlet.ModelAndView;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.IAccessToken;
import com.elsetravel.baseweb.RequestContext;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.define.BaseErrorDefine;
import com.elsetravel.baseweb.pojo.UrlNode;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.RequestHeader;
import com.elsetravel.baseweb.request.RequestParam;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.ServiceProxy;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.exception.IException;
import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.LogUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.util.SpringUtil;
import com.elsetravel.gen.dbentity.sys.SysLogin;
import com.elsetravel.gen.dbentity.sys.SysSn;
import com.elsetravel.mis.component.MisCmp;
import com.elsetravel.mis.request.MisAccessToken;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.DBHelper;

public class TestFrame {
	protected static Logger logger = LogUtil.getLogger(TestFrame.class);
	/**
	 * 
	 * @param url,在web.url.xml里配置的url串
	 * @param header
	 * @param body
	 * @author Wilson 
	 * @date 上午10:25:09
	 */
	public static void doTest(String url,RequestHeader header,RequestBody body) throws Exception{
		logger.info("****** enter test frame : "+url);
		
		if(NullUtil.isEmpty(header.getApp_lang())){
			header.setApp_lang("en");
		}
		
		UrlNode urlNode = null;
		RequestContext context = null;
		IException ex = null;
		Object result = null;
		try{
			//Thread.sleep(3000);
			//检查请求的url的合法性
			urlNode = checkUrl(url);
			
			//解析请求参数
			RequestParam requestParam = new RequestParam(header,body,null);//parseRequestParameter(request,urlNode.isEncrypt());
			
			//检查访问权限
			IAccessToken accessToken = checkAuth(urlNode,requestParam);
			
			//创建上下文对象
			context = createRequestContext();
			if(urlNode.isNeedSn()){
				context.getSn(true);
			}
			context.setRequestHeader(requestParam.getHeader());
			context.setRequestBody(requestParam.getBody());
			context.setInput(requestParam.getInput());
			context.setAccessToken(accessToken);
			ETUtil.setRequestContext(context);
			
			
			
			
			//调用业务服务
			String[] urlEls = urlNode.getUrlElements();
			ServiceProxy srvProxy = (ServiceProxy)SpringUtil.getBeanByName("serviceProxy");
			result = srvProxy.callService(null,urlEls[1], urlEls[2], context);
			
		}catch(Throwable e){
			if(e instanceof InvocationTargetException){
				e = ((InvocationTargetException)e).getTargetException();
			}
			logger.error(e,e);
			ex = RequestUtil.parse2ETException(e);
			if(context == null){
				context = createRequestContext();
				context.setRequestHeader(new RequestHeader());
			}
			Assert.fail(ex.getDisplayMessage(context.getRequestHeader().getApp_lang()));//单元测试失败
		}
		
		//解析输入值
		if(ex != null){
			result = parseErrorOutput(ex, context);
		}else{
			result = parseOutput(result,context);
		}
		
		if(urlNode != null && urlNode.isNeedSn()){
			//记录流水号
			insertSysSn(ex,context,url);
		}
		
		
		logger.info("****** exit test frame : "+url);
	}
	
	private static UrlNode checkUrl(String url){
		UrlNode urlNode = RequestUtil.getUrlNode(url);
		if(urlNode == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_REQUESET_INVALID,url);
		}
		return urlNode;
	}
	
	private static RequestContext createRequestContext() throws Exception{
		RequestContext context = new MisRequestContext(null,null);
		return context;
	};
	
	private static MisAccessToken buildAccessToken(String token,RequestParam reqParam) throws Exception {
		if(NullUtil.isEmpty(token)){
			return null;
		}
		MisAccessToken accessToken = MisCmp.queryMisAccessToken(token,BaseConstantDefine.SYS_TYPE_MIS);
		
		if(accessToken == null || accessToken.getLoginInfo() == null || accessToken.getUserInfo() == null){
			return null;
		}
		return accessToken;
	};
	
	private static IAccessToken checkAuth(UrlNode urlNode,RequestParam reqParam) throws Exception{
		//检查权限
		String token = reqParam.getHeader().getToken();
		IAccessToken accessToken = NullUtil.isEmpty(token)?null:buildAccessToken(token,reqParam);
		if(urlNode.isNeedAuth() && accessToken == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_AUTHLIMIT);
		}
		return accessToken;
	}
	
	private static Object parseOutput(Object result,RequestContext context/*SysSn sn ,HttpServletRequest request,HttpServletResponse response*/) throws Exception{
		if(result instanceof ModelAndView){
			//如果业务侧返回的是ModelAndView对象，说明是显示界面，则直接返回
			return (ModelAndView)result;
		}else{
			String output = ETUtil.outputResponse(context,(ResponseBody)result,null);//否则表示是ajax请求，需要在response流中进行输出json应答串
			logger.debug("output json : "+output);
			context.setOutput(output);
			return null;
		}
	}
	private static ModelAndView parseErrorOutput(IException ex,RequestContext context) throws Exception{
		String requestMethod = context.getRequest()==null?null:context.getRequest().getMethod();//post还是get
		
		if(requestMethod != null && requestMethod.equalsIgnoreCase("get")){
			//get表示是url直接访问的模式,没权限就直接跳转到登录界面
			//CaiUtil.redirect("/"+request.getContextPath()+"/login/showError.do", request, response);
			/*ModelAndView result = new ModelAndView("message");
			result.addObject("message", ex.getMessage());*/
			//context.getResponse().setContentType("text/html;charset=utf-8");
			//context.getResponse().setCharacterEncoding("utf-8");
			RequestHeader header = context.getRequestHeader();
			String message = URLEncoder.encode(ex.getDisplayMessage(header==null?null:header.getApp_lang()),"UTF-8");
			RequestUtil.redirect(RequestUtil.getHost(context.getRequest(),true)+"/message.html?"+message, context.getRequest(), context.getResponse());
			return null;
		}else{
			//其余都是ajax请求了，需要构造错误信息返回
			String output = ETUtil.outputResponse(context,null,ex);
			logger.debug("output json : "+output);
			return null;
		}
	}
	
	/**
     * 创建请求流水记录
     * @author wuyujie Nov 19, 2014 3:42:46 PM
     * @param request
     * @param requestHead
     * @return
     * @throws Exception
     */
	protected static SysSn insertSysSn(IException ex,RequestContext context,String url) throws Exception{
		RequestHeader header = context.getRequestHeader();
		if(header == null)
			return null;
    	IAccessToken accessToken = context.getAccessToken();
    	SysLogin loginInfo = accessToken == null ? null : accessToken.getLoginInfo();
    	
    	SysSn entity = new SysSn();
    	entity.setId(context.getSn(true));
    	entity.setLoginId(loginInfo==null?null:loginInfo.getId());
    	entity.setCreateTime(context.getRequestDate());
    	entity.setUrl(url);
    	entity.setUserId(accessToken == null ? null : accessToken.getAccessUserId());
    	entity.setDeviceType(header.getDevice_type());
    	entity.setAgentType(header.getAgent_type());
    	entity.setAppVersion(header.getApp_version());
    	entity.setServerIp(null);
    	entity.setLang(header.getApp_lang());
    	entity.setIsWifi(header.getWifi());
    	String input = context.getInput();
    	if(input != null && input.length() > 500){
    		input = input.substring(0, 500)+"...";
    	}
		entity.setInput(input);
		entity.setSysType((short)1);
		entity.setChannel(RequestUtil.getRequestChannel(header));
		
		entity.setUpdateTime(DateUtil.now());//应答时间
		entity.setCost((System.currentTimeMillis()-context.getRequestDate().getTime()));//耗时单位ms
    	
    	
    	if(ex != null){
    		entity.setRespCode(String.valueOf(ex.getErrorCode()));
    		entity.setRespMsg(ex.getMessage());
    	}else{
    		entity.setRespCode("0");
    		if(NullUtil.isNotEmpty(context.getOutput()) && context.getOutput().length() > 1000){
    			context.setOutput(context.getOutput().substring(0,990)+"...");
    		}
    		entity.setOutput(context.getOutput());
    	}
    	DBHelper.getDao().insert(entity);
    	return entity;
    }
}
