package com.xiesange.baseweb.wechat;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.log4j.Logger;

import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.component.VendorCmp;
import com.xiesange.baseweb.define.BaseConsDefine.VendorConfigType;
import com.xiesange.baseweb.wechat.define.WechatDefine;
import com.xiesange.baseweb.wechat.define.WechatParamDefine;
import com.xiesange.baseweb.wechat.pojo.Menu;
import com.xiesange.baseweb.wechat.pojo.OAuthToken;
import com.xiesange.baseweb.wechat.pojo.Signature;
import com.xiesange.baseweb.wechat.pojo.TemplateMessageParam;
import com.xiesange.baseweb.wechat.pojo.TemplateMessageReq;
import com.xiesange.baseweb.wechat.pojo.TicketInfo;
import com.xiesange.baseweb.wechat.pojo.UnifiedorderReq;
import com.xiesange.baseweb.wechat.pojo.UnifiedorderResp;
import com.xiesange.baseweb.wechat.pojo.UserInfo;
import com.xiesange.baseweb.wechat.pojo.WechatAccessToken;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.HttpUtil;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.RandomUtil;
import com.xiesange.core.xml.XStreamHolder;
import com.xiesange.gen.dbentity.vendor.VendorConfig;
import com.xiesange.gen.dbentity.vendor.VendorWxOauth;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;

public class WechatCmp {
	private static Logger logger = Logger.getLogger(WechatCmp.class);
	
		
	
	/**
	 * 获取普通的accessToken
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午9:55:30
	 */
	public static String getAccessToken() throws Exception{
		//return "KMaOT3kil-Gt3wIjFVbtjNz4kTG59rnO-8mbWc5jzhdMbobZOEFGg-KF4fP8MgWy49qyP-rFuyUnQ-ZDiQxeUDhucNTsvRB8QqnlOJQRmamhoDkFlLfNKmQ1MSUF7bbQOFPfACAWNX";
		VendorConfig configEntity = VendorCmp.getConfig(VendorConfigType.wx_access_token);
		if(configEntity != null && configEntity.getExpireTime().after(DateUtil.now())){
			//如果当前accessToken还没有过期，则直接取用
			return configEntity.getValue();
		}else{
			KeyValueHolder params = new KeyValueHolder();
			params.addParam(WechatParamDefine.GET_ACCESSTOKEN.grant_type.name(), "client_credential");
			params.addParam(WechatParamDefine.COMMON.appid.name(), WechatDefine.APP_ID);
			params.addParam(WechatParamDefine.COMMON.secret.name(), WechatDefine.SECRET);
			
			String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_ACCESSTOKEN, params));
			
			WechatAccessToken token = JsonUtil.json2Obj(respJson, WechatAccessToken.class);
			String tokenStr = token.getAccess_token();
			
			VendorCmp.updateConfig(VendorConfigType.wx_access_token, configEntity, tokenStr,token.getExpires_in());
			
			return tokenStr;
		}
	}
	
	public static OAuthToken accessOAuthToken(String code) throws Exception{
		KeyValueHolder params = new KeyValueHolder();
		params.addParam(WechatParamDefine.GET_OAUTHTOKEN.grant_type.name(), "authorization_code");
		params.addParam(WechatParamDefine.COMMON.appid.name(), WechatDefine.APP_ID);
		params.addParam(WechatParamDefine.COMMON.secret.name(), WechatDefine.SECRET);
		params.addParam(WechatParamDefine.GET_OAUTHTOKEN.code.name(), code);
		
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_OAUTH_TOKEN, params));
	
		logger.debug("accessOAuthToken json : "+respJson);
		
		OAuthToken token = JsonUtil.json2Obj(respJson, OAuthToken.class);
		return token;
	}
	
	/**
	 * 根据oauthcode查询对应的用户信息
	 * 先从本地数据库表中查找，因为查询过一次会存储下来；如果查不到或者记录已过期则重新去微信服务器查找
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static VendorWxOauth getOAuthInfo(String code) throws Exception{
		VendorWxOauth tokenEntity = DBHelper.getDao().querySingle(
				VendorWxOauth.class, 
				new DBCondition(VendorWxOauth.JField.code,code)
		);
		
		if(tokenEntity != null && tokenEntity.getExpireTime().after(DateUtil.now())){
			//如果当前还没有过期，则直接取用
			return tokenEntity;
		}
		
		OAuthToken accessToken = WechatCmp.accessOAuthToken(code);
		
		if(accessToken == null || NullUtil.isEmpty(accessToken.getAccess_token())){
			return null;
		}
		
		Date expireTime = DateUtil.offsetSecond(DateUtil.now(),accessToken.getExpires_in());
		
		if(tokenEntity == null){
			tokenEntity = new VendorWxOauth();
			tokenEntity.setCode(code);
			tokenEntity.setOpenid(accessToken.getOpenid());
			tokenEntity.setToken(accessToken.getAccess_token());
			tokenEntity.setExpireTime(expireTime);
			tokenEntity.setScope(accessToken.getScope().equalsIgnoreCase("snsapi_userinfo")?(short)2:(short)1);
			DBHelper.getDao().insert(tokenEntity);
		}else{
			//说是在数据中有但是已过期，需要更新code值和过期时间
			long id = tokenEntity.getId();
			tokenEntity.setToken(accessToken.getAccess_token());
			tokenEntity.setExpireTime(expireTime);
			DBHelper.getDao().updateById(tokenEntity,id);
		}
		
		return tokenEntity;
	}
	
	/*public static VendorWxOauth queryOAuthInfoByToken(String oauthToken) throws Exception{
		VendorWxOauth tokenEntity = DBHelper.getDao().querySingle(
				VendorWxOauth.class, 
				new DBCondition(VendorWxOauth.JField.token,oauthToken)
		);
		
		if(tokenEntity != null && tokenEntity.getExpireTime().after(DateUtil.now())){
			//如果当前还没有过期，则直接取用
			return tokenEntity;
		}
		return null;
	}*/
	
	/*
	public static OAuthToken getOAuthAccessToken(String code) throws Exception{
		VendorConfigType type = BaseConsDefine.VendorConfigType.wx_oauth_access_token;
		VendorConfig configEntity = VendorCmp.getConfig(type);
		if(configEntity != null && configEntity.getExpireTime().after(DateUtil.now())){
			//如果当前accessToken还没有过期，则直接取用
			return new OAuthToken(configEntity.getValue(),configEntity.getOpenid());
		}else{
			KeyValueHolder params = new KeyValueHolder();
			params.addParam(WechatParamDefine.GET_OAUTHTOKEN.grant_type.name(), "authorization_code");
			params.addParam(WechatParamDefine.COMMON.appid.name(), WechatDefine.APP_ID);
			params.addParam(WechatParamDefine.COMMON.secret.name(), WechatDefine.SECRET);
			params.addParam(WechatParamDefine.GET_OAUTHTOKEN.code.name(), code);
			
			String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_OAUTH_TOKEN, params));
		
			logger.debug("accessOAuthToken json : "+respJson);
			
			OAuthToken token = JsonUtil.json2Obj(respJson, OAuthToken.class);
			
			VendorCmp.updateConfigWithOpenid(type,configEntity,token.getAccess_token(),token.getExpires_in(),token.getOpenid());
			return token;
		}
	}
	*/
	
	public static void createMenu(Menu menu) throws Exception{
		String accessToken = getAccessToken();
		
		String json = JsonUtil.obj2Json(menu);
		System.out.println("request json:"+json);
		String respJson = HttpUtil.execute(HttpUtil.createJsonHttpPost(WechatDefine.URL_CREATE_MENU+"?access_token="+accessToken,json));
		System.out.println(respJson);
	}
	
	/**
	 * 获取用户信息。该用户必须关注了公众号才能获取到相应信息
	 * @param openId
	 * @throws Exception
	 */
	public static UserInfo getUser(String openId) throws Exception{
		KeyValueHolder params = new KeyValueHolder();
		params.addParam(WechatParamDefine.COMMON.access_token.name(), getAccessToken());
		params.addParam(WechatParamDefine.GET_USER.openid.name(), openId);
		params.addParam(WechatParamDefine.GET_USER.lang.name(), "zh_CN");
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_USER, params));
		logger.debug("----getUser:"+respJson);
		//System.out.println("getUser : "+respJson);
		return JsonUtil.json2Obj(respJson, UserInfo.class);
	}
	
	/**
	 * 获取用户信息。无需关注公众号，是通过显示授权的方来获取
	 * @param oAuthToken
	 * @param openId
	 * @return
	 * @throws Exception
	 */
	public static UserInfo getUserByOAuth(String oAuthToken,String openId) throws Exception{
		KeyValueHolder params = new KeyValueHolder();
		params.addParam(WechatParamDefine.COMMON.access_token.name(), oAuthToken);
		params.addParam(WechatParamDefine.COMMON.openid.name(), openId);
		
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_USERINFO, params));
		
		logger.debug("accessUserInfo:"+respJson);
		
		UserInfo userInfo = JsonUtil.json2Obj(respJson, UserInfo.class);
		
		return userInfo;
	}
	
	public static void getAllMaterial() throws Exception{
		KeyValueHolder params = new KeyValueHolder();
		params.addParam(WechatParamDefine.COMMON.access_token.name(), getAccessToken());
		params.addParam(WechatParamDefine.GET_MATERIAL.type.name(), WechatDefine.MESSAGE_TYPE.image.name());
		params.addParam(WechatParamDefine.GET_MATERIAL.count.name(), "20");
		params.addParam(WechatParamDefine.GET_MATERIAL.offset.name(), "0");
		
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_ALL_MATERIAL, params));
	
		System.out.println(respJson);
	}
	
	
	
	
	
	
	public static Signature buildSignature(String url) throws Exception{
		long timestamp = System.currentTimeMillis()/1000;
		String noncestr = RandomUtil.getString(16);
		String ticket = getJsTicket();
		StringBuffer str1 = new StringBuffer("jsapi_ticket=").append(ticket)
							.append("&noncestr=").append(noncestr)
							.append("&timestamp=").append(timestamp)
							.append("&url=").append(url);
		String signature = DigestUtils.shaHex(str1.toString());
		logger.debug("=======str1 : "+str1);
		//logger.debug("=======signature : "+signature);
		return new Signature(WechatDefine.APP_ID,timestamp,noncestr,signature);
	}
	
	public static String getJsTicket() throws Exception{
		VendorConfig configEntity = VendorCmp.getConfig(VendorConfigType.wx_jsapi_ticket);
		if(configEntity != null && configEntity.getExpireTime().after(DateUtil.now())){
			//如果当前jsticket还没有过期，则直接取用
			return configEntity.getValue();
		}else{
			KeyValueHolder params = new KeyValueHolder();
			params.addParam(WechatParamDefine.COMMON.access_token.name(), getAccessToken());
			params.addParam(WechatParamDefine.COMMON.type.name(), "jsapi");
			String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_TICKET, params));
			logger.debug("getJsTicket:"+respJson);
			TicketInfo ticketInfo = JsonUtil.json2Obj(respJson, TicketInfo.class);
			
			String value = ticketInfo.getTicket();
			
			VendorCmp.updateConfig(VendorConfigType.wx_jsapi_ticket, configEntity, value,ticketInfo.getExpires_in());
			
			return value;
		}
		
	}
	
	
	/**
	 * 获取OAuth流程的accessToken。
	 * 先根据code从表里取，取不到再到微信服务器上去取
	 * @param code,用户授权同意后，微信侧自动生成的code
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午9:55:30
	 */
	/*public static VendorWxOauthToken getOAuthAccessToken(String code) throws Exception{
		VendorWxOauthToken tokenEntity = DBHelper.getDao().querySingle(
				VendorWxOauthToken.class, 
				new DBCondition(VendorWxOauthToken.JField.code,code)
		);
		
		if(tokenEntity != null && tokenEntity.getExpireTime().after(DateUtil.now())){
			//如果当前还没有过期，则直接取用
			return tokenEntity;
		}
		
		OAuthToken accessToken = WechatComponent.accessOAuthToken(code);
		
		Date expireTime = DateUtil.offsetSecond(DateUtil.now(),Integer.parseInt(accessToken.getExpires_in())-20);
		
		if(tokenEntity == null){
			tokenEntity = new VendorWxOauthToken();
			tokenEntity.setCode(code);
			tokenEntity.setOpenid(accessToken.getOpenid());
			tokenEntity.setToken(accessToken.getAccess_token());
			tokenEntity.setExpireTime(expireTime);
			DBHelper.getDao().insert(tokenEntity);
		}else{
			//说是在数据中有但是已过期，需要更新code值和过期时间
			long id = tokenEntity.getId();
			tokenEntity.setToken(accessToken.getAccess_token());
			tokenEntity.setExpireTime(expireTime);
			DBHelper.getDao().updateById(tokenEntity,id);
		}
		
		return tokenEntity;
	}*/
	
	public static UnifiedorderResp sendUnifiedorderReq(String orderCode,long fee,String openid,String callbackUrl) throws Exception{
		UnifiedorderReq req = new UnifiedorderReq();
		req.setAppid(WechatDefine.APP_ID);
		req.setMch_id(WechatDefine.MCH_ID);
		req.setTrade_type("JSAPI");
		req.setNonce_str(RandomUtil.getString(32));
		req.setBody("订单编号"+orderCode);
		
		req.setSpbill_create_ip("115.29.241.212");
		req.setNotify_url(callbackUrl);
		req.setOut_trade_no(orderCode);
		req.setTotal_fee(String.valueOf(fee));
		req.setOpenid(openid);
		
		
		req.setSign(CCP.buildSignature(req.buildSignatureParams(), "&key="+WechatDefine.SIGN_KEY));
		
		XStreamHolder holder = new XStreamHolder();
		String xml = holder.parse2Xml(req);
		
		HttpPost post = HttpUtil.createXmlHttpPost(WechatDefine.URL_SEND_UNIFIEDORDER, xml);
		xml = HttpUtil.execute(post);
		logger.debug("sendUnifiedorderReq resp : "+xml);
		holder.setClassAlias(UnifiedorderResp.class, "xml");
		UnifiedorderResp resp = (UnifiedorderResp)holder.parseFromXml(xml);
		return resp;
	}
	
	
	public static void sendTemplateMessage(String toUser,String tempId,String url,TemplateMessageParam params) throws Exception{
		TemplateMessageReq req = new TemplateMessageReq(toUser,tempId,url,params.getData());
		String sendUrl = WechatDefine.URL_SEND_TEMP_MESSSGE+"?access_token="+getAccessToken();
		String json = JsonUtil.obj2Json(req);
		//logger.debug("sendTemplateMessage response:"+json);
		//System.out.println("json:"+json);
		String respJson = HttpUtil.execute(HttpUtil.createJsonHttpPost(sendUrl, json));
		logger.debug("sendTemplateMessage response:"+respJson);
		//System.out.println(respJson);
	}
	
	/*public static void getBatchUsers(){
		KeyValueHolder params = new KeyValueHolder();
		params.addParam(WechatParamDefine.COMMON.access_token.name(), oAuthToken);
		params.addParam(WechatParamDefine.COMMON.openid.name(), openId);
		
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_BATCHUSER, params));
		
		logger.debug("accessUserInfo:"+respJson);
		
		UserInfo userInfo = JsonUtil.json2Obj(respJson, UserInfo.class);
	}*/
	public static void main(String[] args) throws Exception {
		UserInfo user = getUser("og1OTwge1nWcSJNTFZEDNKCiik3w");
		LogUtil.dump("xxxxxx", user);
	}
}
