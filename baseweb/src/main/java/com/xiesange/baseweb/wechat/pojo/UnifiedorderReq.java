package com.xiesange.baseweb.wechat.pojo;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.xiesange.baseweb.ETUtil;
import com.xiesange.core.util.EncryptUtil;
import com.xiesange.core.util.HttpUtil;
import com.xiesange.core.xml.XStreamHolder;
@XStreamAlias("xml")
public class UnifiedorderReq {
	private String appid;
	private String mch_id;
	private String nonce_str;
	private String sign;
	private String body;
	private String openid;
	
	private String out_trade_no;
	private String total_fee;
	private String spbill_create_ip;
	private String notify_url;
	private String trade_type;
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
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}
	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	public Map<String,Object> buildSignatureParams(){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appid",this.getAppid());
		paramMap.put("mch_id",this.getMch_id());
		paramMap.put("nonce_str",this.getNonce_str());
		paramMap.put("body",this.getBody());
		paramMap.put("out_trade_no",this.getOut_trade_no());
		paramMap.put("total_fee",this.getTotal_fee());
		paramMap.put("spbill_create_ip",this.getSpbill_create_ip());
		paramMap.put("notify_url",this.getNotify_url());
		paramMap.put("trade_type",this.getTrade_type());
		paramMap.put("openid",this.getOpenid());
		return paramMap; 
	}
	
	public static void main(String[] args) throws Exception {
		UnifiedorderReq req = new UnifiedorderReq();
		req.setAppid("wx0b03d9f8cc96b472");
		req.setMch_id("1285324401");
		req.setNonce_str("234567687");
		req.setBody("旅票-10001");
		req.setOut_trade_no("201603261434");
		
		req.setTotal_fee("10");
		req.setSpbill_create_ip("115.29.241.212");
		req.setNotify_url("http://www.elsetravel.com/elsetravel/wspay/order/completePayByWechat.do");
		req.setTrade_type("JSAPI");
		req.setOpenid("o5V6SvzmAVfHKdo45L3AcSDJ-b08");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();

		paramMap.put("appid",req.getAppid());
		paramMap.put("mch_id",req.getMch_id());
		paramMap.put("nonce_str",req.getNonce_str());
		paramMap.put("body",req.getBody());
		paramMap.put("out_trade_no",req.getOut_trade_no());
		paramMap.put("total_fee",req.getTotal_fee());
		paramMap.put("spbill_create_ip",req.getSpbill_create_ip());
		paramMap.put("notify_url",req.getNotify_url());
		paramMap.put("trade_type",req.getTrade_type());
		paramMap.put("openid",req.getOpenid());
		
		String url = ETUtil.createSortedUrlStr(paramMap);
		System.out.println("sorted :"+url);
		url = EncryptUtil.MD5.encode(url, "&key=6ba74da9a3506f16d0e3e2ff2ac00450");
		System.out.println("md5 :"+url);
		req.setSign(url.toUpperCase());
		
		XStreamHolder holder = new XStreamHolder();
		String xml = holder.parse2Xml(req);
		System.out.println(xml);
		
		HttpPost post = HttpUtil.createXmlHttpPost("https://api.mch.weixin.qq.com/pay/unifiedorder", xml);
		String str = HttpUtil.execute(post);
		System.out.println("__________str:"+str);
		
		holder.setClassAlias(UnifiedorderResp.class, "xml");
		UnifiedorderResp resp = (UnifiedorderResp)holder.parseFromXml(str);
		System.out.println("__________str:"+str);
		
	}
	
}
