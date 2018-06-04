package com.xiesange.baseweb.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.define.RequestHeaderField;
import com.xiesange.baseweb.exception.SysErrorXSGException;
import com.xiesange.baseweb.pojo.UrlNode;
import com.xiesange.baseweb.request.ExternalResponseBody;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.RequestHeader;
import com.xiesange.baseweb.request.RequestParam;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.request.ResponseHeader;
import com.xiesange.baseweb.request.ResponseParam;
import com.xiesange.core.enumdefine.IParamEnum;
import com.xiesange.core.enumdefine.IShortConsEnum;
import com.xiesange.core.exception.IException;
import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.EncryptUtil;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.xml.UniversalXmlConverter;
import com.xiesange.core.xml.XStreamHolder;
/**
 * 跟每次请求相关的工具类
 * @author Think
 *
 */
public class RequestUtil {
	private static Logger logger = LogUtil.getLogger(RequestUtil.class);
	private static final String[] PATH_URLS = {"/url/web.url.xml"};
	
	private static final ExternalResponseBody WECHAT_PAY_RESPONSE = new ExternalResponseBody("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>","text/xml");
	
	private static Map<String,UrlNode> ALL_URL_NODE_MAP = new HashMap<String,UrlNode>();
	
	public static void initUrls() throws Exception{
		XStreamHolder holder = new XStreamHolder("root", UrlNode.class);
		holder.registerConverter(new UniversalXmlConverter(UrlNode.class));

		for(String path : PATH_URLS){
			UrlNode root = (UrlNode) holder.parseFromStream(RequestUtil.class.getResourceAsStream(path));
			List<UrlNode> urlNodeList = root.getChildren();
			for(UrlNode urlNode : urlNodeList){
				ALL_URL_NODE_MAP.put(urlNode.getUrl(), urlNode);
				logger.debug("[access url] : "+urlNode.getUrl());
			}
		}
	}
	
	
	public static UrlNode getUrlNode(String url){
		return ALL_URL_NODE_MAP.get(url);
	}
	
	
	/**
     * 检查请求参数中，如果指定的参数前台没传或者传过来的值为空，则抛出异常 
     * @param body
     * @param params
     */
    public static void checkEmptyParams(RequestBody body,IParamEnum...params){
    	if(NullUtil.isEmpty(params))
    		return;
    	for(IParamEnum paramEnum : params){
    		if(NullUtil.isEmpty(body.getString(paramEnum))){
    			throw ETUtil.buildException(BaseErrorDefine.SYS_PARAM_ISNULL,paramEnum.name());
    		}
    	}
    }
    public static void checkParamEnumValue(RequestBody body,IParamEnum param,IShortConsEnum[] expectecValues){
    	Short val = body.getShort(param);
    	if(val == null)
    		return;
    	
    	boolean isIn = false;
    	for(IShortConsEnum enumitem : expectecValues){
    		if(enumitem.value() == val){
    			isIn = true;
    			break;
    		}
    	}
    	if(!isIn){
    		throw ETUtil.buildException(BaseErrorDefine.SYS_PARAM_INVALID,param.name());
    	}
    	
    }
	
	/**
	 * 根据request传过来的参数，把相关参数分别填充到requestHeader或者requestBody里
	 * @param request
	 * @param header
	 * 			token,device_type,agent_type,page_index,page_count这些参数会填充到requestHeader中
	 * @param body
	 */
    public static RequestParam createRequestParam(HttpServletRequest request,boolean isEncrypt){
    	RequestHeader header = new RequestHeader();
		RequestBody body = new RequestBody();
		
		
		StringBuffer all_param_sb = new StringBuffer();
		StringBuffer input_param_sb = new StringBuffer();//需要加入input字段的参数，有些公用参数在sys_sn本身有字段记录就不需要在加入sys_sn.input中了
		String key = null;
		String val = null;
		if(!isEncrypt){
	    	Enumeration<String> em = request.getParameterNames();
			while(em.hasMoreElements()){
				key = em.nextElement();
				val = request.getParameter(key);
				fillRequestParamStr(header,body,key,val,all_param_sb,input_param_sb);
			}
		}else{
			String encryptParam = request.getQueryString();
			if(NullUtil.isNotEmpty(encryptParam)){
				encryptParam = EncryptUtil.Base64.decode(encryptParam);
				String[] paramArr = encryptParam.split("&");
				String[] paramItem = null;
				for(String str : paramArr){
		    		paramItem = str.split("=");
		    		key = paramItem[0];
					val = paramItem.length == 1 ? null : paramItem[1];
		    		
					fillRequestParamStr(header,body,key,val,all_param_sb,input_param_sb);
		    	}
			}
		}
		String input = all_param_sb.toString();
    	logger.info("parameter:"+input);
    	
    	/*if(NullUtil.isEmpty(header.getApp_lang())){
    		header.setApp_lang("zh-CN");//默认中文
    	}*/
    	return new RequestParam(header,body,input_param_sb.toString());
    }
	
    public static void fillRequestParamStr(RequestHeader header,RequestBody body,String key,String val,StringBuffer allParams,StringBuffer inputParams){
    	boolean needAppendInput = false;
    	if(key.equals(RequestHeaderField.token.name())){
			header.setToken(val);
			needAppendInput = true;
		}else if(key.equals(RequestHeaderField.page_index.name())){
			header.setPage_index(Integer.valueOf(val));
			needAppendInput = true;
		}else if(key.equals(RequestHeaderField.page_count.name())){
			header.setPage_count(Integer.valueOf(val));
			needAppendInput = true;
		}else if(key.equals(RequestHeaderField.device_type.name())){
			header.setDevice_type(val);
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.agent_type.name())){
			header.setAgent_type(val);
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.app_version.name())){
			header.setApp_version(val);
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.app_lang.name())){
			header.setApp_lang(val);
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.wifi.name())){
			header.setWifi(NullUtil.isEmpty(val)?null:Short.valueOf(val));
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.device_width.name())){
			header.setDevice_width(val);
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.device_height.name())){
			header.setDevice_height(val);
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.client_ip.name())){
			header.setClient_ip(val);
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.client_uid.name())){
			header.setClient_uid(val);
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.wx_signature_url.name())){
			header.setWx_signature_url(val);
			needAppendInput = false;
		}/*else if(key.equals(RequestHeaderField.check_auth.name())){
			header.setCheck_auth(Short.valueOf(val));
			needAppendInput = false;
		}*/else if(key.equals(RequestHeaderField.enum_flag.name())){
			header.setEnum_flag(Long.valueOf(val));
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.baseparam_flag.name())){
			header.setBaseparam_flag(Long.valueOf(val));
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.wx_oauth_code.name())){
			header.setWx_oauth_code(val);
			needAppendInput = false;
		}else if(key.equals(RequestHeaderField.sign.name())){
			header.setSign(val);
			needAppendInput = false;
		}else{
			//其它的说明是业务参数，放入body
			body.put(key, val);
			needAppendInput = true;
		}
		if(needAppendInput){
			if(inputParams.length() > 0){
				inputParams.append("&");
			}
			inputParams.append(key).append("=").append(val);
		}
		
		if(allParams.length() > 0){
			allParams.append("&");
		}
		
		allParams.append(key).append("=").append(val);
    }
    
	/**
     * redirect到登录页面，url发生变化
     * @author wuyj 2013-11-7
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public static void redirect(String url,ServletRequest request, ServletResponse response) throws ServletException, IOException{
        ((HttpServletResponse)response).sendRedirect(url);
    }
    
    public static String getHost(HttpServletRequest request,boolean needPort){
    	StringBuffer url_sb = new StringBuffer(128).append(request.getScheme()).append("://").append(request.getServerName());
		if(needPort && request.getServerPort() != 80){
			url_sb.append(":").append(request.getServerPort());
		}
		return url_sb.toString();
    }
    
    
    public static String getRequestIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (!checkIP(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!checkIP(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!checkIP(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    private static boolean checkIP(String ip) {
        if (ip == null || ip.length() == 0 || "unkown".equalsIgnoreCase(ip)
                || ip.split(".").length != 4) {
            return false;
        }
        return true;
    }
    
    
    
    
    public static String createReponseJson(ResponseHeader responseHeader,ResponseBody responseBody){
    	//构建应答对象
    	String output = null;
    	ResponseParam responseParam = null;
    	try {
    		responseParam = new ResponseParam(responseHeader,responseBody);
			output = JsonUtil.obj2Json(responseParam);
		} catch (Exception e) {
			// 这里出错，一般都是responseHead或者responseBody里的内容json化出错，所以把body内容移除掉，只序列化header，防止出错后前台收不到任何信息
			logger.error(e, e);
			responseParam = new ResponseParam(buildResponseHeader(parse2ETException(e),responseHeader.getLang()),null);
			output = JsonUtil.obj2Json(responseParam);
		}
		return output;
    }
	
	
	public static ResponseHeader buildResponseHeader(IException ex,String lang) {
    	ResponseHeader respHeader = new ResponseHeader();
    	respHeader.setResponse_date(DateUtil.now());
    	respHeader.setLang(lang);
    	
    	if(ex != null){
    		respHeader.setError_code(ex.getErrorCode());
    		respHeader.setError_message(ex.getMessage());
	    	//如果是sys_error类型异常，那么返回给客户端要把提示语改成sys_error中定义的message，而不要返回原生的异常信息，因为原生的提示太专业化(比如NullpointException)，对用户不友好
	    	/*if(ex.getErrorCode() == BaseErrorDefine.SYS_ERROR.getCode()){
	    		respHeader.setError_message(ex.getMessage());
	    	}else{
	    		respHeader.setError_message(ex.getMessage());
	    	}*/
    	}else{
    		respHeader.setError_code(0);
    	}
		return respHeader;
	}
	
	/**
	 * 把一个异常转换成BaseExeption。
	 * 如果这个异常本身就是BaseException则原样返回；
	 * 如果非BaseException，那就只能是系统程序异常抛出，比如空指针，数组越界等，这种情况要构建一个SYS_ERROR类型的BaseExeption
	 *
	 * @param e
	 * @return
	 */
	public static IException parse2ETException(Throwable e) {
		e = CommonUtil.getCaused(e);
		IException ex = null;
		if(e instanceof IException){
			ex = (IException)e;
		}else{
			//系统性抛出的异常，错误信息规则是：异常类名:异常信息
			String message = e.getClass().getName();
			if(NullUtil.isNotEmpty(e.getMessage())){
				message = message+":"+e.getMessage();
			}
			ex = new SysErrorXSGException(message);
		}

		return ex;
	}
	
	public static boolean isFromWechat(RequestHeader header){
		return NullUtil.isNotEmpty(header.getAgent_type()) && header.getAgent_type().indexOf("wechat") > -1;
	}
	public static boolean isFromWeb(RequestHeader header){
		return NullUtil.isNotEmpty(header.getAgent_type()) && header.getAgent_type().indexOf("browser") > -1;
	}
	public static boolean isFromApp(RequestHeader header){
    	return header.getAgent_type() != null && header.getAgent_type().startsWith("app");
    }
	
	public static short getRequestChannel(RequestHeader header){
    	if(isFromWeb(header)){
    		return BaseConsDefine.CHANNEL.WEBSITE.value();
    	}else if(isFromApp(header)){
    		return BaseConsDefine.CHANNEL.APP.value();
    	}else if(isFromWechat(header)){
    		return BaseConsDefine.CHANNEL.WECHAT.value();
    	}
    	return -1;
    }
	
	public static ExternalResponseBody getWechatPaySuccessResponse(){
		return WECHAT_PAY_RESPONSE;
	}
	
	public static boolean checkVersionSupport(){
		return true;
	}
}
