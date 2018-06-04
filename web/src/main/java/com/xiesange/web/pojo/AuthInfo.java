package com.xiesange.web.pojo;

import com.xiesange.baseweb.wechat.pojo.Signature;
import com.xiesange.gen.dbentity.user.User;

public class AuthInfo {
	private User user;
	private String token;
	private String skey;
	private Signature wxSignature;
	
	public AuthInfo(User user,String token,String skey){
		this.user = user;
		this.token = token;
		this.skey = skey;
	}
	public User getUser() {
		return user;
	}
	public String getToken() {
		return token;
	}
	public String getSkey() {
		return skey;
	}
	public Signature getWxSignature() {
		return wxSignature;
	}
	
}
