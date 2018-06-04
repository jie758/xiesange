package com.xiesange.baseweb;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiesange.baseweb.pojo.UrlNode;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.RequestHeader;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.gen.dbentity.sys.SysSn;
import com.xiesange.orm.DBHelper;

/**
 * 请求的上下文对象。每次新请求产生后会新创建一个，并贯穿整个请求处理流程.
 * 存放一些公用的信息
 * @author wuyujie Nov 19, 2014 5:05:43 PM
 *
 */
public class RequestContext {
	private String contextUrl;//http://ip:port/context组成的url
	private String host;//当前服务器主机地址
	private Date requestDate;//请求发生时间
	private UrlNode urlNode;//当前请求url在web.url.xml里对应的节点
	
	private HttpServletRequest request;//原始的request对象
	private HttpServletResponse response;//原始的response对象
	
	private RequestHeader requestHeader;//请求头对象
	private RequestBody requestBody;//请求体对象,存放请求参数
	
	private String input;//请求参数，url参数格式
	private String output;//输出参数,json格式
	
	
	//private SysSn sysSn;//流水记录对象
	private Long sn;//流水号，第一次获取的才去数据库查询,提高性能考虑，因为有可能有些业务既不需要插流水表，也不涉及到insert的操作
	protected IAccessToken accessToken;
	
	public RequestContext(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
		this.requestDate = DateUtil.now();
		
		if(request != null){
			host = request.getScheme()+"://"+request.getServerName();
			if(request.getServerPort() != 80){
				host+=":"+request.getServerPort();
			}
			//LogUtil.getLogger(RequestContext.class).debug("host:"+host);
			//LogUtil.getLogger(RequestContext.class).debug("contentPath:"+request.getContextPath());
			contextUrl = host+request.getContextPath()+"/";
		}
	}
	
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	/*public SysSn getSysSn() {
		return sysSn;
	}
	public void setSysSn(SysSn sysRequest) {
		this.sysSn = sysRequest;
	}*/


	public HttpServletRequest getRequest() {
		return request;
	}


	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}


	public HttpServletResponse getResponse() {
		return response;
	}


	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public RequestHeader getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(RequestHeader requestHeader) {
		this.requestHeader = requestHeader;
	}

	public RequestBody getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(RequestBody requestBody) {
		this.requestBody = requestBody;
	}

	public IAccessToken getAccessToken() {
		return accessToken;
	}
	public String getAccessTokenCode() {
		if(accessToken == null || accessToken.getLoginInfo() == null)
			return null;
		return accessToken.getLoginInfo().getToken();
	}

	public void setAccessToken(IAccessToken accessToken) {
		this.accessToken = accessToken;
	}
	
	public Long getAccessUserId(){
		return accessToken == null ? null : accessToken.getAccessUserId();
	}
	public Long getSn(boolean forceAccess) throws Exception{
		if(sn == null && forceAccess){
			sn = DBHelper.getDao().getSequence(SysSn.class);
		}
		return sn;
	}

	public String getContextUrl() {
		return contextUrl;
	}
	public void setContextUrl(String contextUrl) {
		this.contextUrl = contextUrl;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public UrlNode getUrlNode() {
		return urlNode;
	}

	public void setUrlNode(UrlNode urlNode) {
		this.urlNode = urlNode;
	}

	public String getHost() {
		return host;
	}
	
	public short getRequestChannel(){
		return RequestUtil.getRequestChannel(this.requestHeader);
	}
}
