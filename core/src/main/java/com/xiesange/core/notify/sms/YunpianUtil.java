package com.xiesange.core.notify.sms;

import java.util.List;

import org.apache.log4j.Logger;

import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.notify.NotifyResponse;
import com.xiesange.core.util.HttpUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;

public class YunpianUtil {
	//编码格式。发送编码格式统一用UTF-8
    private static final String ENCODING = "UTF-8";
    private static final String APPKEY = "58dce291f561e1cdb108b5a7d3f89066";
    private static final String SEND_URL_V1 = "https://sms.yunpian.com/v1/sms/send.json";
	
	public static final String SMS_SIGN = "【蟹三哥】";
	
	private static Logger logger = LogUtil.getLogger(YunpianUtil.class);
	
	public static void main(String[] args) throws Exception {
		/*sendByTemplate("6408",new String[]{"13588830404"},
				new String[]{"兵哥"});*/
		
		//sendVCode("13588830404");
		sendV1("13588830404",86,"您的验证码是"+1234+"。如非本人操作，请忽略本短信");
		//sendByTemplate(1370359L);
	}
	
	/**
	 * 发送单条短信，包括国内或者国外。国外号码必须以+zone，zone是国家区号
	 * @param mobile
	 * @param content，不包括签名，签名在本方法里自动加上，国外和国内的签名不一样
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:02:36
	 */
	public static NotifyResponse sendV1(String mobile,Integer zone,String content) throws Exception{
		if(!content.startsWith("【")){
			content = SMS_SIGN+content;
		}
		KeyValueHolder param = new KeyValueHolder("apikey", APPKEY);
		param.addParam("mobile", mobile);//手机号码，国外号码必须以+zone开头，zone是国家区号
		param.addParam("text", content);//短息内容
		String respJson = HttpUtil.execute(HttpUtil.createHttpPost(SEND_URL_V1,param));
		System.out.println("yunpian sms response : "+respJson);
		logger.debug("yunpian sms response : "+respJson);
		
		NotifyResponse resp = new NotifyResponse();
		if(respJson.indexOf("\"code\":0") > -1){
			resp.setCode("0");
		}else{
			resp.setCode("1");//表示错误
		}
		resp.setMessage(respJson);
		
		return resp;
	}
	
	/**
	 * 发送国内批量短信，最多1000个。仅支持国内号码发送批量短信
	 * @param mobileList
	 * @param zoneList,如果整个zoneList都为null，则表示是国内号码，86
	 * @param content
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:01:11
	 */
	public static NotifyResponse sendV1Batch(List<String> mobileList,String content) throws Exception{
		if(NullUtil.isEmpty(mobileList)){
			return null;
		}
		if(mobileList.size() == 1){
			return sendV1(mobileList.get(0),86,content);
		}
		StringBuffer mobiles = new StringBuffer();
		for(int i=0;i<mobileList.size();i++){
			mobiles.append(",").append(mobileList.get(i));//仅中国号码支持批量发送
		}
		if(!content.startsWith("【")){
			content = SMS_SIGN+content;//短息内容,需要加国内签名
		}
		KeyValueHolder param = new KeyValueHolder("apikey", APPKEY);
		param.addParam("mobile", mobiles.substring(1));
		param.addParam("text", content);
		String respStr = HttpUtil.execute(HttpUtil.createHttpPost(SEND_URL_V1,param));
		NotifyResponse resp = new NotifyResponse();
		if(respStr.indexOf("\"code\":0") > -1){
			resp.setCode("0");
		}else{
			resp.setCode("1");//表示错误
		}
		resp.setMessage(respStr);
		
		logger.debug("yunpian sms response : "+respStr);
		return resp;
	}
	
	
	/*public static void send(String mobile,String content) throws Exception{
		ParamHolder param = new ParamHolder("apikey", APPKEY);
		param.addParam("mobile", mobile);//短信平台用户名
		param.addParam("text", content);//短信平台密码
		param.addParam("encrypt", "tea");//固定值
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("mobile",param.getParamValue("mobile"));
		paramMap.put("text",param.getParamValue("text"));
		paramMap.put("encrypt",param.getParamValue("encrypt"));
		paramMap.put("apikey",param.getParamValue("apikey"));
		paramMap.put("api_secret",SECRET+SECRET+SECRET+SECRET);
		
		String str = ETUtil.createSortedValueStr(paramMap,",");
		LogUtil.getLogger(NeteaseUtil.class).debug(str);
		str = EncryptUtil.MD5.encode(str);
		LogUtil.getLogger(NeteaseUtil.class).debug(str);
		param.addParam("_sign", str);//短信必须要加上签名
		String respJson = HttpUtil.execute(HttpUtil.createHttpPost("https://sms.yunpian.com/v2/sms/single_send.json",param));
		
		System.out.println("send sms response："+respJson);
	}*/
	
	/*public static void sendByTemplate(long tempId) throws Exception{
		String tpl_value = URLEncoder.encode("#code#",ENCODING) +"="
	            + URLEncoder.encode("1234", ENCODING);
		
		
		KeyValueHolder param = new KeyValueHolder("apikey", "afcb7573e160f94af8e782b3274b66bf");
		param.addParam("mobile", "13588830404");//短信平台用户名
		param.addParam("tpl_id", String.valueOf(tempId));//模板id
		param.addParam("tpl_value", tpl_value);//模板值
		
		String secret = "d2c58593";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("mobile",param.getParamValue("mobile"));
		paramMap.put("text",param.getParamValue("text"));
		paramMap.put("encrypt",param.getParamValue("encrypt"));
		paramMap.put("apikey",param.getParamValue("apikey"));
		paramMap.put("api_secret",secret+secret+secret+secret);
		
		String str = ETUtil.createSortedValueStr(paramMap,",");
		LogUtil.getLogger(NeteaseUtil.class).debug(str);
		str = EncryptUtil.MD5.encode(str);
		LogUtil.getLogger(NeteaseUtil.class).debug(str);
		param.addParam("_sign", str);//短信必须要加上签名
		String respJson = HttpUtil.execute(HttpUtil.createHttpPost("https://sms.yunpian.com/v2/sms/single_send.json",param));
		
		System.out.println("send sms response："+respJson);
	}*/
}
