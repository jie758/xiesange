package com.xiesange.baseweb.wechat.pojo;

public class Signature {
	private long timestamp;
	private String random;
	private String signature;
	private String appId;
	
	public Signature(String appId,long timestamp,String random,String signature){
		this.appId = appId;
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

	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
}
