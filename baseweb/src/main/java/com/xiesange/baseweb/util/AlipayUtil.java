package com.xiesange.baseweb.util;

import java.util.HashMap;
import java.util.Map;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.pojo.AlipayResponse;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.EncryptUtil;
import com.xiesange.core.util.HttpUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.RandomUtil;
import com.xiesange.core.xml.BaseNode;
import com.xiesange.core.xml.UniversalXmlConverter;
import com.xiesange.core.xml.XStreamHolder;

public class AlipayUtil {
	public static final String URL_ALIPAY_GATEWAY = "https://mapi.alipay.com/gateway.do";
	public static final String PARTNER_ID = "2088021469473028";
	public static final String SIGN_TYPE = "MD5";
	public static final String CHARSET = "UTF-8";
	public static final String PRIVATE_KEY = "pnfahfnxl68t4wtdt9u962zbsqe3spx0";
	
	public static void main(String[] args) throws Exception {
		querySingleTrade("20160423_10020564");
		
		//transfer("2015123121001004530046591225");
	}
	
	
	/*public static String transfer(String tradeNo) throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("service", "refund_fastpay_by_platform_pwd");
		paramMap.put("partner", PARTNER_ID);
		paramMap.put("_input_charset", CHARSET);
		paramMap.put("seller_user_id", PARTNER_ID);
		paramMap.put("refund_date", DateUtil.now19());
		paramMap.put("batch_no", DateUtil.now14()+RandomUtil.getNum(6));
		paramMap.put("batch_num", "1");
		paramMap.put("notify_url", "http://114.215.199.197/elsetravel/wxpay/wx/getUserInfo.do");
		
		paramMap.put("detail_data", tradeNo+"^0.1^订单取消退款");

		String paramStr = ETUtil.createSortedUrlStr(paramMap) + PRIVATE_KEY;
		String sign = EncryptUtil.MD5.encode(paramStr);
		
		ParamHolder params = new ParamHolder(paramMap)
									.addParam("sign", sign)
									.addParam("sign_type", SIGN_TYPE);
		
		String respXml = HttpUtil.execute(HttpUtil.createHttpPost(URL_ALIPAY_GATEWAY, params));
		LogUtil.getLogger(AlipayUtil.class).debug(respXml);
		//System.out.println(respXml);
		
		return respXml;
	}*/
	
	/**
	 * 
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:38:57
	 */
	public static AlipayResponse querySingleTrade(String outTradeNo) throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("service", "single_trade_query");
		paramMap.put("partner", PARTNER_ID);
		paramMap.put("_input_charset", CHARSET);
		paramMap.put("out_trade_no", outTradeNo);

		String paramStr = ETUtil.createSortedUrlStr(paramMap) + PRIVATE_KEY;
		String sign = EncryptUtil.MD5.encode(paramStr);
		
		KeyValueHolder params = new KeyValueHolder(paramMap)
									.addParam("sign", sign)
									.addParam("sign_type", SIGN_TYPE);
		
		String respXml = HttpUtil.execute(HttpUtil.createHttpPost(URL_ALIPAY_GATEWAY, params));
		System.out.println(respXml);
		XStreamHolder holder = new XStreamHolder("alipay", BaseNode.class);
		holder.registerConverter(new UniversalXmlConverter(BaseNode.class));
		
		BaseNode alipayNode = (BaseNode)holder.parseFromXml(respXml);
		String successStr = alipayNode.getChildByTagName("is_success").getText();
		AlipayResponse resp = new AlipayResponse();
		resp.setSuccess(successStr.equals("T"));
		BaseNode respNode = alipayNode.getChildByTagName("response");
		if(respNode != null){
			BaseNode tradeNode = respNode.getChildByTagName("trade");
			
			String sumStr = tradeNode.getChildByTagName("price").getText();
			
			resp.setOrderCode(outTradeNo);
			resp.setSuccess(successStr.equals("T"));
			resp.setSum(Float.valueOf(Float.parseFloat(sumStr)*100).longValue());
			resp.setBuyerAccount(tradeNode.getChildByTagName("buyer_email").getText());
			resp.setTransactionCode(tradeNode.getChildByTagName("trade_no").getText());
		}
		return resp;
	}
	
	public static void checkSign(){
		
	};
}
