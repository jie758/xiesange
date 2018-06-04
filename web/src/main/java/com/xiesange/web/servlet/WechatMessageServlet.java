package com.xiesange.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.notify.TaskManager;
import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.baseweb.wechat.WechatNode;
import com.xiesange.baseweb.wechat.define.WechatDefine;
import com.xiesange.baseweb.wechat.pojo.CustomArticleItem;
import com.xiesange.baseweb.wechat.pojo.Message;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.EncryptUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.xml.UniversalXmlConverter;
import com.xiesange.core.xml.XStreamHolder;
import com.xiesange.gen.dbentity.activity.ActivityGzhReply;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.web.define.ParamDefine.Article;
import com.xiesange.web.task.WxSubscribeTask;
import com.xiesange.web.task.WxUnSubscribeTask;

/**
 * 微信服务号接入servlet
 * 
 * @author wuyujie 2014年8月29日 下午10:14:15
 * 
 */
public class WechatMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_HANDLER = "classpath:/wechat/message_handler.properties";
	private XStreamHolder holder = new XStreamHolder("xml",WechatNode.class);
	private XStreamHolder replyHolder = null;

	protected static Logger logger = LogUtil.getLogger(WechatMessageServlet.class);
	
	//private Map<String,IWechatHandler> servcieMap = new HashMap<String,IWechatHandler>();
	//private static DefaultHandler default_handler = new DefaultHandler();
	
	
	public void init(ServletConfig config) throws ServletException {
		/*try{
			Properties p = FileUtil.loadProperties(MESSAGE_HANDLER);
			Iterator<Entry<Object,Object>> it = p.entrySet().iterator();
	    	String key = null;
	    	while(it.hasNext()){
	    		Entry<Object,Object> entry = it.next();
	    		key = (String)entry.getKey();
	    		servcieMap.put(key, (IWechatHandler)ClassUtil.instance((String)entry.getValue()));
	    		logger.info("......[wechat handler] "+key+" = "+entry.getValue());
	    	}
		}catch(Exception e){
			logger.error(e, e);
		}*/
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		logger.debug("----- enter WechatMessageServlet");
		
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
    		
    		String msgType = messageNode.getChildByTagName("MsgType").getText();
    		Message replyMessage = dealMessage(msgType,messageNode);
    		String output = null;
    		if(replyMessage != null){
	    		replyHolder = new XStreamHolder();
				replyHolder.setClassAlias(replyMessage.getClass(), "xml");
				replyHolder.setClassAlias(Article.class, "item");
				output = replyHolder.parse2Xml(replyMessage);
    		}
    		
    		
    		logger.debug("output : "+output);
    		PrintWriter out = response.getWriter();
    		out.write(output==null?"":output);
    		out.flush();
    		out.close();
		} catch (Exception e) {
			logger.error(e,e);
		}
        
        logger.debug("----- exit WechatMessageServlet");
	}
	
	
	private Message dealMessage(String msgType,WechatNode node) throws Exception{
		//logger.debug("----["+node.getFromUserName()+"]:"+node.getContent());
		
		Message messageReply = null;
		if(msgType.equalsIgnoreCase(WechatDefine.MSG_TYPE_EVENT)){
			messageReply = dealEvent(node);
		}else if(msgType.equalsIgnoreCase(WechatDefine.MSG_TYPE_TEXT)){
			messageReply = dealText(node);
		}
		
		if(messageReply != null){
			messageReply.setToUserName(node.getFromUserName());  
			messageReply.setFromUserName(node.getToUserName());  
			messageReply.setCreateTime(DateUtil.now_yyyymmddhhmmss());  
		}
        
        return null;
	}
	
	public static Message dealText(WechatNode node) throws Exception{
		Message messageReply = reply(node,node.getContent());
		
		if(messageReply == null){
			WechatCmp.sendCustomText(node.getFromUserName(), "有点尴尬,匹配不到对应的内容~确定没有打错字嘛？");
			//messageReply = new Message();
			//messageReply.setMsgType("text");
			//messageReply.setContent("有点尴尬,匹配不到对应的内容~确定没有打错字嘛？");
		}
		
		return messageReply;
	}
	public static Message dealEvent(WechatNode node) throws Exception{
		//事件
		String event = node.getEvent();
		Message message = null;
		String eventKey = node.getEventKey();
		if(event.equalsIgnoreCase("subscribe")){
			//关注
			if(NullUtil.isNotEmpty(eventKey)){
				eventKey = eventKey.split("qrscene_")[1];
			}
			subscribe(node);
			message = reply(node,NullUtil.isNotEmpty(eventKey) ? "#"+eventKey:"#subscribe");
		}else if(event.equalsIgnoreCase("unsubscribe")){
			unsubscribe(node);
		}else if(event.equalsIgnoreCase("scan")){
			//扫描
			message = reply(node,"#"+eventKey);
		}
		
		return message;
		
	}
	private static Message reply(WechatNode node,String key) throws Exception{
		Message messageReply = new Message();
		
		//查询出该关键字所有的回复配置
		List<ActivityGzhReply> replyList = DBHelper.getDao().query(
				ActivityGzhReply.class,
				new DBCondition(ActivityGzhReply.JField.keyword,key));
		
		if(NullUtil.isNotEmpty(replyList)){
			for(ActivityGzhReply reply : replyList){
				short replyType = reply.getReplyType();
				if(replyType == WechatDefine.REPLY_TYPE_TEXT){
					String respMessage = WechatCmp.parseTextReply(reply);
					WechatCmp.sendCustomText(node.getFromUserName(), respMessage);
				}else if(replyType == WechatDefine.REPLY_TYPE_IMAGE){
					String mediaId = WechatCmp.parseImageReply(reply);
					logger.debug("----------mediaId:"+mediaId);
					if(NullUtil.isNotEmpty(mediaId)){
						WechatCmp.sendCustomImage(node.getFromUserName(),mediaId);
					}
				}else if(replyType == WechatDefine.REPLY_TYPE_ARTICLE){
					List<CustomArticleItem> items = WechatCmp.parseArticleReply(reply);
					if(NullUtil.isNotEmpty(items)){
						WechatCmp.sendCustomArticle(node.getFromUserName(),items);
					}
				}
			}
		}
        
        return messageReply;
	}

	private static void subscribe(WechatNode node){
		TaskManager.execute(new WxSubscribeTask(node));
	}
	private static void unsubscribe(WechatNode node){
		TaskManager.execute(new WxUnSubscribeTask(node));
	}
	
	public static void main(String[] args) throws Exception {
		Message messageReply = new Message();
		messageReply.setToUserName("111");  
		messageReply.setFromUserName("2222");  
		messageReply.setCreateTime(DateUtil.now_yyyymmddhhmmss());  
		messageReply.setMsgType("image");
		messageReply.addImage("60NH1KIfGlT0-JBA133ZalWLQEQAbhksKDgya0kid-s");
		
		XStreamHolder replyHolder = new XStreamHolder();
		replyHolder.setClassAlias(messageReply.getClass(), "xml");
		replyHolder.setClassAlias(Article.class, "item");
		String output = replyHolder.parse2Xml(messageReply);
		System.out.println(output);
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
		
		
		List<String> keys = ClassUtil.newList();
		keys.add("qinglvxingqiufw");
		keys.add(timestamp);
		keys.add(nonce);
		//keys.add(echostr);
		Collections.sort(keys);
		
		String str = CommonUtil.join(keys);
		str = EncryptUtil.SHA1.encode(str);
		logger.debug("signature from wx : "+signature);
		logger.debug("signature from et : "+str);
		
		response.getWriter().print(echostr); 
	}
}
