package com.elsetravel.mis.request;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.elsetravel.baseweb.RequestContext;
import com.elsetravel.baseweb.request.ExternalResponseBody;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.RequestHeader;
import com.elsetravel.baseweb.request.RequestParam;
import com.elsetravel.core.util.LogUtil;
import com.elsetravel.core.util.NullUtil;

/**
 * 用于外部调入的请求。这部分请求由于客观原因是无法当前框架里的请求格式和应答格式来交互的。
 * 所以这里增加了一个controller，把原HttpRequest和HttpResponse传到业务方法中个，让各个方法自己做处理
 * @author Wilson 
 * @date 下午6:01:23
 */
@Controller
@RequestMapping("/external")
public class ExternalController extends WebController{
	protected Logger logger = LogUtil.getLogger(this.getClass());

	@Override
	public RequestParam parseRequestParameter(HttpServletRequest request,boolean isEncrypt) throws Exception {
		logger.debug("________enter : ExternalController");
		/*BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null){
            sb.append(line);
        }
        
        logger.debug("request params : "+sb.toString());*/
		RequestHeader header = new RequestHeader();
		header.setApp_lang("zh-CN");
		header.setToken(null);
        
		RequestBody body = new RequestBody();
		//body.put("xml", sb.toString());
		
		return new RequestParam(header,body,null);
	}
	
	protected void checkVersion(RequestParam reqParam) throws Exception{
		return;//外部请求不做版本检测
	}
	
	protected void checkSignature(RequestParam requestParam,String signKey){
		return;//外部请求不做签名检测
	}
	
	protected Object parseOutput(Object response,RequestContext context) throws Exception{
		ExternalResponseBody extResponse = (ExternalResponseBody)response;
		if(NullUtil.isNotEmpty(extResponse.getType())){
			context.getResponse().setContentType(extResponse.getType());
		}
		logger.debug("__________Access-Control-Allow-Origin : *");
		
		context.getResponse().addHeader("Access-Control-Allow-Origin", "*");//可以跨域访问
		context.getResponse().getWriter().print(extResponse.getValue());
		context.getResponse().getWriter().flush();
		context.getResponse().getWriter().close();
		logger.debug("__________success output : "+extResponse.getValue());
		return null;
	}
	
}
