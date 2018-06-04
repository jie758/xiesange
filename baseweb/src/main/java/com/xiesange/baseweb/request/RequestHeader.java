package com.xiesange.baseweb.request;


public class RequestHeader {
	private String app_version;//版本号
	private String app_lang;//访问语言
	private String token;//登录令牌
    private String agent_type;//客户端类型,
    private String device_type;//设备类型
    private Short wifi;//是否在wifi环境下
    private Integer page_index;
    private Integer page_count;
    private String device_width;
    private String device_height;
    private String client_ip;
    private String client_uid;
    private String wx_signature_url;//有值，则表示需要返回微信的签名信息，用于微信端转发
    //private Short check_auth;
    private Long baseparam_flag;
	private Long enum_flag;
	private String wx_oauth_code;
    private String sign;
    
    
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getAgent_type() {
		return agent_type;
	}
	public void setAgent_type(String agent_type) {
		this.agent_type = agent_type;
	}
	public Integer getPage_index() {
		return page_index;
	}
	public void setPage_index(Integer page_index) {
		this.page_index = page_index;
	}
	public Integer getPage_count() {
		return page_count;
	}
	public void setPage_count(Integer page_count) {
		this.page_count = page_count;
	}
	public String getDevice_type() {
		return device_type;
	}
	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}
	public String getApp_version() {
		return app_version;
	}
	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}
	/*public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}*/
	
	public Short getWifi() {
		return wifi;
	}
	public void setWifi(Short wifi) {
		this.wifi = wifi;
	}
	public String getApp_lang() {
		return app_lang;
	}
	public void setApp_lang(String app_lang) {
		this.app_lang = app_lang;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getDevice_width() {
		return device_width;
	}
	public void setDevice_width(String device_width) {
		this.device_width = device_width;
	}
	public String getDevice_height() {
		return device_height;
	}
	public void setDevice_height(String device_height) {
		this.device_height = device_height;
	}
	public String getClient_ip() {
		return client_ip;
	}
	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}
	public String getClient_uid() {
		return client_uid;
	}
	public void setClient_uid(String client_uid) {
		this.client_uid = client_uid;
	}
	public String getWx_signature_url() {
		return wx_signature_url;
	}
	public void setWx_signature_url(String wx_signature_url) {
		this.wx_signature_url = wx_signature_url;
	}
	/*public Short getCheck_auth() {
		return check_auth;
	}
	public void setCheck_auth(Short check_auth) {
		this.check_auth = check_auth;
	}*/
	public Long getBaseparam_flag() {
		return baseparam_flag;
	}
	public void setBaseparam_flag(Long baseparam_flag) {
		this.baseparam_flag = baseparam_flag;
	}
	public Long getEnum_flag() {
		return enum_flag;
	}
	public void setEnum_flag(Long enum_flag) {
		this.enum_flag = enum_flag;
	}
	public String getWx_oauth_code() {
		return wx_oauth_code;
	}
	public void setWx_oauth_code(String wx_oauth_code) {
		this.wx_oauth_code = wx_oauth_code;
	}
	
}
