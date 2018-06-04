package com.xiesange.baseweb.wechat.pojo;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class QueryOrderReq {
	private String appid;
	private String mch_id;
	private String nonce_str;
	private String sign;
	private String out_trade_no;
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	
	public Map<String,Object> buildSignatureParams(){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appid",this.getAppid());
		paramMap.put("mch_id",this.getMch_id());
		paramMap.put("nonce_str",this.getNonce_str());
		paramMap.put("out_trade_no",this.getOut_trade_no());
		return paramMap; 
	}
	
}
