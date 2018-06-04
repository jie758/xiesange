package com.xiesange.baseweb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.APIConnectionException;
import com.pingplusplus.exception.APIException;
import com.pingplusplus.exception.AuthenticationException;
import com.pingplusplus.exception.ChannelException;
import com.pingplusplus.exception.InvalidRequestException;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Webhooks;
import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.SysparamCmp;
import com.xiesange.baseweb.define.BaseConsDefine.PAYCHANNEL;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.define.SysparamDefine;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.user.User;

public class PingppUtil {
	//private static String pubKeyPath = "C:\\Users\\Think\\Desktop\\test\\pingpp\\rsa_public_key.pem";
	//private static String eventPath = "E:\\workspace\\elsetravel\\elsetravel\\elsetravel-web\\src\\test\\resources\\webhooks_raw_post_data.json";
	//private static String signPath = "E:\\workspace\\elsetravel\\elsetravel\\elsetravel-web\\src\\test\\resources\\signature.txt";
	private static final String API_KEY = "sk_live_qzjfTKnPW9m9zj14i54u5eHG";
	private static final String API_ID = "app_1Wbjb10OSarPyzL4";
	
	public static enum PINGPP_CHANNEL{
		alipay(PAYCHANNEL.ALIPAY.value()),
		alipay_pc_direct(PAYCHANNEL.ALIPAY_PC.value()),
		wx(PAYCHANNEL.WECHAT.value()),
		wx_pub(PAYCHANNEL.WX_PUB.value()),
		wx_pub_qr(PAYCHANNEL.WX_PUB_QR.value());
		
		private short xsgVal;
		private PINGPP_CHANNEL(short xsgVal){
			this.xsgVal = xsgVal;
		}
		
		public short getXsgValue(){
			return xsgVal;
		}
	} 
	public static void main(String[] args) throws Exception {
		boolean aa = checkSignature(null,null);
		System.out.println(aa);
		/*Pingpp.apiKey = "sk_test_ibbTe5jLGCi5rzfH4OqPW9KC";

		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("order_no", "123456789");
		chargeParams.put("amount", 10);
		Map<String, String> app = new HashMap<String, String>();
		app.put("id", "app_1Gqj58ynP0mHeX1q");
		chargeParams.put("app", app);
		chargeParams.put("channel", "wx");
		chargeParams.put("currency", "cny");
		chargeParams.put("client_ip", "127.0.0.1");
		chargeParams.put("subject", "Your Subject");
		chargeParams.put("body", "Your Body");

		Charge chg = Charge.create(chargeParams);
		System.out.println(chg);*/
	}
	
	public static Map<String,Object> createExtra(Orders order,User payUser,PINGPP_CHANNEL pingppChannel){
		Map<String,Object> extra = ClassUtil.newMap();
		if(pingppChannel == PINGPP_CHANNEL.wx_pub){
			extra.put("open_id", payUser.getWechat());
		}else if(pingppChannel == PINGPP_CHANNEL.wx_pub_qr){
			extra.put("product_id", order.getId());
		}else if(pingppChannel == PINGPP_CHANNEL.alipay_pc_direct){
			extra.put("success_url", ETUtil.getRequestContext().getHost()+"/order_complete.html?order_id="+order.getId());
		}
		return extra;
	}
	
	public static PINGPP_CHANNEL trans2PingppChannel(short xsgChannel){
		PINGPP_CHANNEL[] enums = PINGPP_CHANNEL.values();
		for(PINGPP_CHANNEL pingpp : enums){
			if(pingpp.getXsgValue() == xsgChannel){
				return pingpp;
			}
		}
		throw ETUtil.buildInvalidOperException("不支持该支付方式");
	}
	
	public static Short trans2XSGChannel(String pingppChannel){
		PINGPP_CHANNEL[] enums = PINGPP_CHANNEL.values();
		for(PINGPP_CHANNEL pingpp : enums){
			if(pingpp.name().equals(pingppChannel)){
				return pingpp.xsgVal;
			}
		}
		return null;
	}
	
	/**
	 * 创建Charge对象
	 * @param orderId
	 * @param amount
	 * @return
	 * @throws AuthenticationException
	 * @throws InvalidRequestException
	 * @throws APIConnectionException
	 * @throws APIException
	 * @throws ChannelException
	 * @author Wilson 
	 * @date 2016年7月5日
	 */
	public static Charge createCharge(String orderNo,long paySum,String title,PINGPP_CHANNEL pingppChannel,Map<String,Object> extra,Map<String,String> metadata) throws Exception{
		//开始调用ping+创建支付charge
		Pingpp.apiKey = API_KEY;
		Pingpp.privateKeyPath = SysparamCmp.get(SysparamDefine.PINGPP_RSA_PRIVATE_KEY_PATH);
		
		if(title.length() > 32){
			title = title.substring(0, 31);
		}
		//long orderId = orderEntity.getId();
		//long paySum = orderEntity.getSum()+orderEntity.getExpressSum();
		/*if(ETUtil.getRequestContext().getAccessUserId() == 1){
			sum = 1;
		}*/
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("order_no",  orderNo);
		chargeParams.put("amount", paySum);//订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）
		Map<String, String> app = new HashMap<String, String>();
		app.put("id", API_ID);
		chargeParams.put("app", app);
		chargeParams.put("channel",pingppChannel.name());
		chargeParams.put("currency", "cny");
		chargeParams.put("client_ip",  "127.0.0.1");
		chargeParams.put("subject", title);
		chargeParams.put("body", title);
		if(NullUtil.isNotEmpty(extra)){
			chargeParams.put("extra", extra);
		}
		if(metadata == null){
			metadata = ClassUtil.newMap();
		}
		
		chargeParams.put("metadata", metadata);
		Charge charge = Charge.create(chargeParams);
		return charge;
	}
	
	
	public static String parseWebhookRequestStr(HttpServletRequest request) throws IOException{
		// 获得 http body 内容
        BufferedReader reader = request.getReader();
        StringBuffer buffer = new StringBuffer();
        String string;
        while ((string = reader.readLine()) != null) {
            buffer.append(string);
        }
        reader.close();
        
        return buffer.toString();
	}
	
	/**
	 * 支付成功，通过webhook回调过来解析参数成Charge对象
	 * @param request
	 * @return
	 * @throws IOException
	 * @author Wilson 
	 * @date 2016年7月5日
	 */
	public static Charge parseWebhookCharge(String reqString) throws IOException{
        Event event = Webhooks.eventParse(reqString);
        Charge charge = (Charge)event.getData().getObject();
        return charge;
	}
	
	
	
	/**
	 * 在ping++支付成功的webhook事件里验证签名，防止模拟攻击
	 * @param dataString
	 * @param signatureString
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 2016年8月3日
	 */
	public static boolean checkSignature(String dataString, String signatureString)
            throws Exception {
		//signatureString = FileUtil.getFileContent(signPath);//"lvNka5t2A+JVKOjRRPDMIiPW4dxi/VV8hcdAqWrwdvhbJRpG2VFDjMq1T6Q8fZh4St57din7/TAxzsFuMqBnWiwLYwPNMhr69iuwLMCYqEQXnrg3ORfSjSZmEKVrrhHhwypK0juxhgwR7B8swbJ4pAjM18KG2vtXHsGRQBKu+PFEqiI7vTC8PGLt294hKa0b6nKpmAmEd918n2aAeUfRMJpdqCbL8HDn1uSqgX00mFUhQYcNaA1MnOYgSkPATtvLRRZ0W6duxCEsZd/EORh/DWz9VncxS+sg0a05ZiBPNuSj6GxZbQ8yIvWgmaQ7526ODM8DrQ8B1z6tYIC59sNRhw==";
		//dataString = FileUtil.getFileContent(eventPath);
		
		byte[] signatureBytes = Base64.decodeBase64(signatureString);
        Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(getPubKey());
		signature.update(dataString.getBytes("UTF-8"));
		boolean result = signature.verify(signatureBytes);
		if(!result){
			throw ETUtil.buildException(BaseErrorDefine.SYS_INVALID_SIGNATURE);
		}
		LogUtil.getLogger(PingppUtil.class).debug("result : "+result);
		return result;
	}
	
	/**
	 * 获得公钥
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPubKey() throws Exception {
		String pubKeyString = SysparamCmp.get(SysparamDefine.PINGPP_WEBHOOK_RSA_PUBLIC_KEY);
        pubKeyString = pubKeyString.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
        //LogUtil.getLogger(PingppUtil.class).debug("--------public key : "+pubKeyString);
        
        byte[] keyBytes = Base64.decodeBase64(pubKeyString);

		// generate public key
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(spec);
		return publicKey;
	}
}
