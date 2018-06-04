package com.xiesange.baseweb.request;

import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.IAccessToken;
import com.xiesange.baseweb.RequestContext;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.component.SysparamCmp;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.define.SysparamDefine;
import com.xiesange.baseweb.pojo.UrlNode;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.exception.IXSGException;
import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.SpringUtil;
import com.xiesange.gen.dbentity.sys.SysLogin;
import com.xiesange.gen.dbentity.sys.SysSn;
import com.xiesange.orm.DBHelper;

public abstract class AbstractController{
	protected Logger logger = LogUtil.getLogger(this.getClass());
	
	public static void main(String[] args) {
		String savePath = "image/ticket/10020264/cover.${size}.png?t=1448356342113";
		savePath = savePath.replace(".${size}.", ".");
		System.out.println(savePath);
		/*Map<String,Object> paramMap = new HashMap<String,Object>();
		
		paramMap.put(BaseParamDefine.RequestHeader.token.name(), "1CHzhViEkufsPHq7MvaTpWzv0c07g1PU");
		paramMap.put(BaseParamDefine.RequestHeader.page_index.name(), "0");
		paramMap.put(BaseParamDefine.RequestHeader.page_count.name(), "20");
		paramMap.put(BaseParamDefine.RequestHeader.app_version.name(), "2.0.1");
		paramMap.put("code", "03194d7b920a8d2f57463e021749125T");
		paramMap.put("token", "7W8IZVfGCm7iHjDZO83UISfYsFh7jThQ");
		String paramStr = ETUtil.createSortedUrlStr(paramMap);
		System.out.println(paramStr);
		System.out.println(EncryptUtil.MD5.encode(paramStr,"XqYPwBX2isN2fDkxRjUd4sSR0gEbOi79"));*/
		//System.out.println(EncryptUtil.MD5.encode(paramStr,"hjsh3djdk4ehjeje23dlsdf"));
	}
	
	@RequestMapping(value = "/**",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView acceptRequest(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		logger.info("****** enter invoke controller : "+request.getServletPath());
		//Thread.sleep(3000);
		UrlNode urlNode = null;
		RequestContext context = null;
		IXSGException ex = null;
		Object result = null;
		try{
			//Thread.sleep(3000);
			//检查请求的url的合法性
			urlNode = checkUrl(request);
			
			//解析请求参数
			RequestParam requestParam = parseRequestParameter(request,urlNode.isEncrypt());
			
			//检查版本支持性
			checkVersion(requestParam,urlNode);
			
			//创建上下文对象
			context = createRequestContext(request,response,requestParam);
			if(urlNode.isNeedSn()){
				context.getSn(true);
			}
			context.setRequestHeader(requestParam.getHeader());
			context.setRequestBody(requestParam.getBody());
			context.setInput(requestParam.getInput());
			ETUtil.setRequestContext(context);
			
			//检查访问权限
			IAccessToken accessToken = checkAuth(urlNode,requestParam);
			context.setAccessToken(accessToken);
			
			
			//校验签名
			if(accessToken != null){
				checkSignature(requestParam,accessToken.getLoginInfo().getSignKey());
			}
			
			
			
			//调用业务服务
			String[] urlEls = urlNode.getUrlElements();
			ServiceProxy srvProxy = (ServiceProxy)SpringUtil.getBeanByName("serviceProxy");
			result = srvProxy.callService(this,urlEls[1], urlEls[2], context);
			
		}catch(Throwable e){
			if(e instanceof InvocationTargetException){
				e = ((InvocationTargetException)e).getTargetException();
			}
			logger.error(e,e);
			ex = RequestUtil.parse2ETException(e);
			if(context == null){
				context = new RequestContext(request, response);
				context.setRequestHeader(new RequestHeader());
			}
		}
		
		//解析输入值
		if(ex != null){
			result = parseErrorOutput(ex, context);
		}else{
			result = parseOutput(result,context);
		}
		
		if(urlNode != null && urlNode.isNeedSn()){
			//记录流水号
			try{
				insertSysSn(ex,context);
			}catch(Exception e){
				logger.error(e, e);
			}
		}
		logger.info("****** exit controller : "+request.getServletPath());
		return (ModelAndView)result;
	}
	
	//#################################################
	protected abstract RequestContext createRequestContext(HttpServletRequest request,HttpServletResponse response,RequestParam requestParam) throws Exception;
	protected abstract RequestParam parseRequestParameter(HttpServletRequest request,boolean isEncrypt) throws Exception;
	protected abstract IAccessToken buildAccessToken(String token,RequestParam reqParam) throws Exception;
	protected abstract BaseConsDefine.SYS_TYPE getSysType();
	
	
	private UrlNode checkUrl(HttpServletRequest request){
		String servletPath = request.getServletPath();
		UrlNode urlNode = RequestUtil.getUrlNode(servletPath.substring(1));
		if(urlNode == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_REQUESET_INVALID,servletPath);
		}
		return urlNode;
	}
	
	protected void checkSignature(RequestParam requestParam,String signKey) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(NullUtil.isEmpty(requestParam.getHeader().getToken()))
			return;
		CCP.checkSignature(requestParam.getHeader(), requestParam.getBody(),signKey);
	}
	
	/*public RequestContext createRequestContext(HttpServletRequest request,boolean needSn,HttpServletResponse response,RequestParam requestParam) throws Exception{
		RequestContext context = SessionUtil.createRequestContext(request, response);
		
		context.setRequestHeader(requestParam.getHeader());
		context.setRequestBody(requestParam.getBody());
		context.setInput(requestParam.getInput());
		
		ETUtil.setRequestContext(context);
		if(needSn){
			context.getSn(true);//必须在外层去获取sequence，如果在serviceProxy内层事务里获取的话，那么内层抛错后会导致sequence的更新也回滚了
		}
		return context;
	};*/
	
	protected void checkVersion(RequestParam reqParam,UrlNode urlNode) throws Exception{
		if(!urlNode.isNeedVersion())
			return;
		if(!RequestUtil.isFromApp(reqParam.getHeader())){
			return;//如果不是app里的请求都不做版本检查
		}
		String[] supportedVersions = SysparamCmp.getArray(SysparamDefine.SUPPORT_VERSION, ";");
		String requestVersion = reqParam.getHeader().getApp_version();
		//logger.debug("___________supportedVersions:"+CommonUtil.join(supportedVersions,';'));
		//logger.debug("____________requestVersion:"+requestVersion);
		if(NullUtil.isNotEmpty(supportedVersions) && !CommonUtil.isIn(requestVersion, supportedVersions)){
			//如果当前系统参数里配置了可支持的版本号，但是请求的版本又不在可支持的版本号列表里，则需要报错
			throw ETUtil.buildException(BaseErrorDefine.SYS_VERSION_NOTSUPPORT);
		}
	}
	
	public IAccessToken checkAuth(UrlNode urlNode,RequestParam reqParam) throws Exception{
		//检查权限
		String token = reqParam.getHeader().getToken();
		IAccessToken accessToken = NullUtil.isEmpty(token)?null:buildAccessToken(token,reqParam);
		if(urlNode.isNeedAuth() && accessToken == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_AUTHLIMIT);
		}
		return accessToken;
	}
	
	/**
     * 创建请求流水记录
     * @author wuyujie Nov 19, 2014 3:42:46 PM
     * @param request
     * @param requestHead
     * @return
     * @throws Exception
     */
	public SysSn insertSysSn(IXSGException ex,RequestContext context) throws Exception{
		RequestHeader header = context.getRequestHeader();
		if(header == null)
			return null;
    	IAccessToken accessToken = context.getAccessToken();
    	SysLogin loginInfo = accessToken == null ? null : accessToken.getLoginInfo();
    	
    	SysSn entity = new SysSn();
    	entity.setId(context.getSn(true));
    	entity.setLoginId(loginInfo==null?null:loginInfo.getId());
    	entity.setCreateTime(context.getRequestDate());
    	entity.setUrl(context.getRequest().getServletPath());
    	entity.setUserId(accessToken == null ? null : accessToken.getAccessUserId());
    	entity.setDeviceType(header.getDevice_type());
    	entity.setAgentType(header.getAgent_type());
    	entity.setAppVersion(header.getApp_version());
    	entity.setServerIp(RequestUtil.getRequestIP(context.getRequest()));
    	entity.setLang(header.getApp_lang());
    	entity.setIsWifi(header.getWifi());
    	String input = context.getInput();
    	if(input != null && input.length() > 500){
    		input = input.substring(0, 500)+"...";
    	}
		entity.setInput(input);
		entity.setSysType(this.getSysType().value());
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
	
	

	protected Object parseOutput(Object result,RequestContext context) throws Exception{
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
	protected ModelAndView parseErrorOutput(IXSGException ex,RequestContext context) throws Exception{
		String requestMethod = context.getRequest().getMethod();//post还是get
		
		if(requestMethod.equalsIgnoreCase("get")){
			//get表示是url直接访问的模式,没权限就直接跳转到登录界面
			//CaiUtil.redirect("/"+request.getContextPath()+"/login/showError.do", request, response);
			/*ModelAndView result = new ModelAndView("message");
			result.addObject("message", ex.getMessage());*/
			//context.getResponse().setContentType("text/html;charset=utf-8");
			//context.getResponse().setCharacterEncoding("utf-8");
			String message = URLEncoder.encode(ex.getMessage(),"UTF-8");
			RequestUtil.redirect(RequestUtil.getHost(context.getRequest(),true)+"/message.html?"+message, context.getRequest(), context.getResponse());
			return null;
		}else{
			//其余都是ajax请求了，需要构造错误信息返回
			String output = ETUtil.outputResponse(context,null,ex);
			logger.debug("output json : "+output);
			return null;
		}
	}
	
}
