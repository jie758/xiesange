package com.xiesange.baseweb.wechat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.wechat.message.EventHandler;
import com.xiesange.baseweb.wechat.message.IWechatHandler;
import com.xiesange.baseweb.wechat.message.ImageHandler;
import com.xiesange.baseweb.wechat.message.TextHandler;
import com.xiesange.baseweb.wechat.pojo.Message;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.xml.UniversalXmlConverter;
import com.xiesange.core.xml.XStreamHolder;

/**
 * 微信公众号接入servlet
 * 
 * @author wuyujie 2014年8月29日 下午10:14:15
 * 
 */
public class WechatMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private XStreamHolder holder = new XStreamHolder("xml",WechatNode.class);
	private XStreamHolder replyHolder = new XStreamHolder("xml",Message.class);

	protected static Logger logger = LogUtil.getLogger(WechatMessageServlet.class);
	
	private Map<String,IWechatHandler> servcieMap = new HashMap<String,IWechatHandler>();
	
	
	public void init(ServletConfig config) throws ServletException {
		servcieMap.put("text", new TextHandler());
		servcieMap.put("event", new EventHandler());
		servcieMap.put("image", new ImageHandler());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		/*logger.debug("+++++++++++++++doPost");
		
		StringBuffer sb = new StringBuffer(); 
        InputStream is = request.getInputStream();  
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");  
        BufferedReader br = new BufferedReader(isr);  
        String s = "";  
        while ((s = br.readLine()) != null) {  
            sb.append(s);  
        }  
        String xml = sb.toString(); //次即为接收到微信端发送过来的xml数据  
        try {
        	logger.debug("xml : \n"+xml);
    		holder.registerConverter(new UniversalXmlConverter(WechatNode.class));
    		WechatNode messageNode = (WechatNode)holder.parseFromXml(xml.toString());
    		
    		LogUtil.dump("xxxxxxxxxxx", messageNode);
    		
    		String msgType = messageNode.getChildByTagName("MsgType").getText();
    		Message replyMessage = dealMessage(msgType,messageNode);
    		
    		String output = replyHolder.parse2Xml(replyMessage);
    		logger.debug("output : "+output);
    		PrintWriter out = response.getWriter();
    		out.write(output);
    		out.flush();
    		out.close();
		} catch (Exception e) {
			logger.error(e,e);
		}*/
	}
	
	
	private Message dealMessage(String msgType,WechatNode node){
		/*// 发送方帐号（open_id）  
        String fromUserName = node.getChildByTagName("FromUserName").getText();  
        // 公众帐号  
        String toUserName = node.getChildByTagName("ToUserName").getText();  
        */
        
        Message replyMessage = servcieMap.get(msgType).service(node);
        
		/*String respContent = "请求处理异常，请稍候尝试！";
        if (msgType.equals(WXDefine.MESSAGE_TYPE.text.name())) {
        	// 文本消息
            respContent = "您发送的是文本消息！";
        }else if (msgType.equals(WXDefine.MESSAGE_TYPE.image.name())) {
        	// 图片消息  
            respContent = "您发送的是图片消息！";  
        }else if (msgType.equals(WXDefine.MESSAGE_TYPE.location.name())) {
        	// 地理位置消息  
            respContent = "您发送的是地理位置消息！";  
        }else if (msgType.equals(WXDefine.MESSAGE_TYPE.voice.name())) {  
        	// 音频消息  
            respContent = "您发送的是音频消息！"; 
        }else if(msgType.equals(WXDefine.MESSAGE_TYPE.event.name())){
        	// 事件类型  
            String eventType = node.getChildByTagName("Event").getText();  
            if (eventType.equals(WXDefine.EVENT_TYPE.SUBSCRIBE.name())) {  
            	// 订阅  
                respContent = "谢谢您的关注！";  
            }else if (eventType.equals(WXDefine.EVENT_TYPE.UNSUBSCRIBE.name())) {
            	// 取消订阅  
            }else if (eventType.equals(WXDefine.EVENT_TYPE.CLICK.name())) {  
                // 自定义菜单点击
            	String key = node.getChildByTagName("EventKey").getText();
            	respContent = "谢谢您的点击！"+key;
            }  
        }*/
        
        /*Message message = new Message();
        
        message.setToUserName(fromUserName);  
        message.setFromUserName(toUserName);  
        message.setCreateTime(DateUtil.now14());  
        message.setMsgType("text");
        message.setContent(respContent);*/
        // message.setMediaId(node.getChildByTagName("MediaId").getText());
        //textMessage.setFuncFlag(0);  
        
        return replyMessage;
	}
	
	

	
	public static void main(String[] args) throws Exception {
		StringBuffer xml = new StringBuffer("<xml>");
		xml.append("<ToUserName><![CDATA[xyz]]></ToUserName>");
		xml.append("<FromUserName><![CDATA[abc]]></FromUserName>");
		xml.append("<CreateTime>12345678</CreateTime>");
		xml.append("<MsgType><![CDATA[text]]></MsgType>");
		xml.append("<Content><![CDATA[你好]]></Content>");
		xml.append("</xml>");
		
		XStreamHolder holder = new XStreamHolder("xml",WechatNode.class);
		holder.registerConverter(new UniversalXmlConverter(WechatNode.class));
		WechatNode message = (WechatNode)holder.parseFromXml(xml.toString());
		
		LogUtil.dump("xxxxxxxxxxxx", message);
		
		String aa = message.getFromUserName();
		
		//LogUtil.dump("xxxxxxxxxxx", message);
		
		
		Message messageReply = new Message();
		messageReply.setContent("xxxxxxxx");
		messageReply.setFromUserName("wuyj");
		
		holder = new XStreamHolder("xml",Message.class);
		String output = holder.parse2Xml(messageReply);
		System.out.println("output : "+output);
		/*Map<String,String> a = new HashMap<String,String>();
		a.put("ToUserName", "wuyj");
		
		XStreamHolder holder2 = new XStreamHolder("xml2",HashMap.class);
		System.out.println(holder2.parse2Xml(a));*/
		
	}
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("+++++++++++++++doGet");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		if(NullUtil.isNotEmpty(request.getParameter("signature"))){
			validate(request,response);
		}else{
			doPost(request,response);
		}
		
		/*request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");*/
		
	}
	
	/**
	 * 服务器验证
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private static void validate(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String signature = request.getParameter("signature");
		logger.debug("微信加密签名" + signature);
		// 随机字符串
		String echostr = request.getParameter("echostr");
		logger.debug("随机字符串" + echostr);
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		logger.debug("时间戳" + timestamp);
		// 随机数
		String nonce = request.getParameter("nonce");
		logger.debug("随机数" + nonce);
		response.getWriter().print(echostr); 
	}
}
