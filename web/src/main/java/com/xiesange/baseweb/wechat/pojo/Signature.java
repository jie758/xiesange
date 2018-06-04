package com.xiesange.baseweb.wechat.pojo;

public class Signature {
	private long timestamp;
	private String random;
	private String signature;
	private String appid;
	
	public Signature(String appid,long timestamp,String random,String signature){
		this.appid = appid;
		this.timestamp = timestamp;
		this.random = random;
		this.signature = signature;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getRandom() {
		return random;
	}
	public void setRandom(String random) {
		this.random = random;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}
}
