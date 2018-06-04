package com.xiesange.baseweb.request;

import java.util.Date;

public class ResponseHeader {
	private Long sn;//交易流水号
	private Date response_date;//应答时间
	private long error_code;//应答码
    private String error_message;//应答信息
    private String lang;//应答所采用的语言
    private String token;//通行令牌
    private String reward_medal_name;
    private String reward_medal_url;
    
    //private Object body;//应答内容
	public Date getResponse_date() {
		return response_date;
	}
	public Long getSn() {
		return sn;
	}
	public void setSn(Long sn) {
		this.sn = sn;
	}
	public void setResponse_date(Date response_date) {
		this.response_date = response_date;
	}
	public long getError_code() {
		return error_code;
	}
	public void setError_code(long error_code) {
		this.error_code = error_code;
	}
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getReward_medal_name() {
		return reward_medal_name;
	}
	public void setReward_medal_name(String reward_medal_name) {
		this.reward_medal_name = reward_medal_name;
	}
	public String getReward_medal_url() {
		return reward_medal_url;
	}
	public void setReward_medal_url(String reward_medal_url) {
		this.reward_medal_url = reward_medal_url;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
}
