package com.xiesange.baseweb.notify;

import java.util.Date;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.notify.NotifyDefine.CodeDefine;
import com.xiesange.baseweb.notify.task.EmailNotifyTaskBean;
import com.xiesange.baseweb.notify.task.SmsNotifyTaskBean;
import com.xiesange.baseweb.notify.task.WechatNotifyTaskBean;
import com.xiesange.baseweb.wechat.pojo.TemplateMessageParam;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.notify.mail.EMailInfo;
import com.xiesange.core.notify.mail.EMailUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.notify.NotifyRecord;
import com.xiesange.gen.dbentity.notify.NotifyTemplate;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;

/**
 * 通知相关的组件
 * @author Wilson Wu
 * @date 2015年9月24日
 *
 */
public class NotifyCmp {
	private static Logger logger = LogUtil.getLogger(NotifyCmp.class);
    
    public static void main(String[] args) throws Exception {
    	String title = "你还，这是测试邮件";
		String content = "Hello，World!<BR/>你还，这是测试邮件";
		
		//content = "<style>p{display:inline}.ee_i{line-height:25px}</style>"+content;
		
		EMailInfo mailInfo = new EMailInfo(title,content);
		//mailInfo.setHtmlContent(EmailHtmlSkin.getInstance(content).buildHtml());
		//mailInfo.setSubject(title);
		mailInfo.addTo("wilson@elsetravel.com");
		
		EMailUtil.sendHTML(mailInfo);
		//sendEmail(mailInfo);
	}
    /**
     * 判断某个通知编码是否合法
     * @param code
     * @return
     * @author Wilson 
     * @date 下午4:46:57
     */
    public static boolean isValidTemplateCode(String code){
    	CodeDefine[] types = NotifyDefine.CodeDefine.values();
    	if(NullUtil.isNotEmpty(types)){
    		for(CodeDefine codeDefine : types){
    			if(codeDefine.name().equals(code)){
    				return true;
    			}
    		}
    	}
    	return false; 
    }
    /**
     * 根据模板编码发送该通知相关的所有渠道的通知。
     * 比如有个code=regist的通知模板配置了3个渠道，那么需要发送这3个渠道的通知
     * @param templateCode
     * @param params
     * @author Wilson 
     * @throws Exception 
     * @date 下午7:13:03
     */
    /*public static void sendTemplate(CodeDefine templateCodeDefine,ParamHolder params) throws Exception{
    	sendTemplate(templateCodeDefine,params,null);
    }
    public static void sendTemplate(CodeDefine templateCodeDefine,NotifyTargetHolder targetHolder) throws Exception{
    	sendTemplate(templateCodeDefine,null,targetHolder);
    }*/
    public static void sendTemplate(CodeDefine templateCodeDefine,KeyValueHolder params,NotifyTargetHolder targetHolder) throws Exception{
    	//通知不影响到整体流程
    	try{
	    	List<NotifyTemplate> tempList = getTemplateList(templateCodeDefine.name());
	    	if(NullUtil.isEmpty(tempList))
	    		return;
	    	if(params == null){
	    		params = new KeyValueHolder();
	    	}
	    	Date now = DateUtil.now();
	    	params.addParam(NotifyDefine.Common.create_time.name(), DateUtil.date2Str(now, DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS));
	    	params.addParam(NotifyDefine.Common.create_date.name(), DateUtil.date2Str(now, DateUtil.DATE_FORMAT_EN_B_YYYYMMDD));
	    	
	    	//List<NotifyRecord> allRecordList = new ArrayList<NotifyRecord>();
	    	
	    	//List<NotifyRecord> recordList = null;
	    	for(NotifyTemplate temp : tempList){
	    		short channel = temp.getChannel();
	    		if(NullUtil.isNotEmpty(temp.getTarget())){
	    			//如果是通知到客服，那么需要根据配置的客服账号去获取通知信息
	    			targetHolder = buildNotifyTarget(channel,temp.getTarget());
	    		}
	    		if(targetHolder == null)
	    			continue;
	    		
	    		if(isBySms(channel)){
	    			sendTemplateBySms(temp,params,targetHolder.getMobileList(),targetHolder.getZoneList());
	    		}else if(isByEmail(channel)){
	    			sendTemplateByEmail(temp,params,targetHolder.getEmailList());
	    		}else if(isByApp(channel)){
	    			;//recordList = sendTemplateByApp(temp,params,targetHolder.getUserIdList());
	    		}else if(isByWechat(channel)){
	    			sendTemplateByWechat(temp,params,targetHolder.getOpenidList());
	    		}
	    		/*
	    		if(NullUtil.isNotEmpty(recordList)){
	    			allRecordList.addAll(recordList);
	    		}*/
	    	}
	    	
	    	/*if(allRecordList.size() > 0){
	    		DBHelper.getDao().insertBatch(allRecordList);
	    	}*/
    	}catch(Exception e){
    		logger.error(e,e);
    	}
    }
    
    /*public static NotifyTemplate getEmailTemplate(String templateCode) throws Exception{
    	return DBHelper.getDao().querySingle(NotifyTemplate.class, 
    			new DBCondition(NotifyTemplate.JField.code,templateCode),
    			new DBCondition(NotifyTemplate.JField.channel,BaseConstantDefine.NOTIFY_CHANNEL_EMAIL)
    	);
    }*/
    
    /**
     * 获取出某个模板信息。
     * 注意target字段会做转换。如果是客服通知，那么会根据渠道把客户账号转换成对应具体的通知地址
     * 比如target="wuyj,doujia",如果channel=email,那么target会转换成"wuyj@elsetravel.com,doujia@elsetravel.com"
     * @param templateCode
     * @return
     * @throws Exception
     * @author Wilson 
     * @date 上午10:49:08
     */
    public static List<NotifyTemplate> getTemplateList(String templateCode) throws Exception{
    	List<NotifyTemplate> tempList = DBHelper.getDao().query(NotifyTemplate.class, 
    			new DBCondition(NotifyTemplate.JField.code,templateCode),
    			new DBCondition(NotifyTemplate.JField.status,BaseConsDefine.STATUS.EFFECTIVE.value())
    	);
    	if(NullUtil.isEmpty(tempList))
    		return null;
    	return tempList;
    	/*Set<String> staffAcct = new HashSet<String>();
    	for(NotifyTemplate temp : tempList){
    		if(temp.getTargetType() != BaseConstantDefine.NOTIFY_TARGETTYPE_ET){
    			continue;
    		}
    		
    		staffAcct.addAll(temp.getTarget().split(","));
    	}*/
    }
		
	/**
	 * 有系统账号发送的邮件
	 * @param email,接收方邮件地址
	 * @param title,邮件标题
	 * @param content,邮件内容,可以是html格式
	 * @throws EmailException
	 * @author Wilson Wu
	 * @date 2015年9月24日
	 */
	private static void sendTemplateBySms(NotifyTemplate tempEntity,KeyValueHolder params,List<String> targetMobileList,List<Integer> targetZoneList) throws Exception{
		String content = params==null ? tempEntity.getContent() : ETUtil.parseTextExpression(tempEntity.getContent(), params);
		TaskManager.execute(new SmsNotifyTaskBean(targetMobileList,content));
	}
	
	private static void sendTemplateByWechat(NotifyTemplate tempEntity,KeyValueHolder params,List<String> targetOpenidList) throws Exception{
		String[] items = tempEntity.getContent().split("\\|");
		TemplateMessageParam tempparam = new TemplateMessageParam();
		for(String item : items){
			String[] parts = item.split("=");
			String value = ETUtil.parseTextExpression(parts[1], params);
			value = value.replaceAll("\\\\r", "\r").replaceAll("\\\\n", "\n");
			String color = parts[0].equalsIgnoreCase("first") ? "#FD9603" : null;
			tempparam.addParam(parts[0], value,color);
		}
		String wxUrl = tempEntity.getWxTempUrl();
		wxUrl = ETUtil.parseTextExpression(wxUrl, params);
		TaskManager.execute(new WechatNotifyTaskBean(targetOpenidList,tempEntity.getWxTempId(),wxUrl,tempparam));
	}
    /*private static List<NotifyRecord> sendTemplateByApp(NotifyTemplate tempEntity,KeyValueHolder params,List<Long> targetUserIdList) throws Exception{
    	if(NullUtil.isEmpty(targetUserIdList))
			return null;
		String title = params==null ? tempEntity.getTitle() : ETUtil.parseTextExpression(tempEntity.getTitle(), params);
		String content = params==null ? tempEntity.getContent() : ETUtil.parseTextExpression(tempEntity.getContent(), params);
		
		List<NotifyRecord> messageList = new ArrayList<NotifyRecord>();
		long batchId = DBHelper.getDao().getSequence(NotifyRecord.class);
		for(int i=0;i<targetUserIdList.size();i++){
			Long targetUserId = targetUserIdList.get(i);
    		NotifyRecord record = new NotifyRecord();
    		record.setContent(content);
    		record.setTitle(title);
    		record.setTargetUserId(targetUserId);
    		record.setTarget(String.valueOf(targetUserId));
    		record.setChannel(BaseConsDefine.NOTIFY_CHANNEL.APP.value());
    		record.setIsRead((short)0);
			if(i == 0){
				record.setId(batchId);//第一条记录就用batchId
			}
			messageList.add(record);
    	}
		
		//发送系统消息，通知
		IMCmp.sendSysMessage(targetUserIdList, content);
		
		//发送命令消息，在每个收到通知的用户系统消息列表界面里即时插入这些消息
		Map<String,Object> cmdData = new HashMap<String,Object>();
		cmdData.put("messageList", new NotifyRecord[]{messageList.get(0)});
		IMCmp.sendCmdMessage(targetUserIdList, IMCmp.CmdDefine.send_sys_message,cmdData);
		
		return messageList;
    	
    }*/
	private static List<NotifyRecord> sendTemplateByEmail(NotifyTemplate tempEntity,KeyValueHolder params,List<String> targetEmailList) throws Exception{
		if(NullUtil.isEmpty(targetEmailList))
			return null;
		String title = params==null ? tempEntity.getTitle() : ETUtil.parseTextExpression(tempEntity.getTitle(), params);
		String content = params==null ? tempEntity.getContent() : ETUtil.parseTextExpression(tempEntity.getContent(), params);
		EMailInfo mailInfo = new EMailInfo(title,content);
		mailInfo.addTo(targetEmailList);
		
		try{
			TaskManager.execute(new EmailNotifyTaskBean(mailInfo,true));
			//EMailUtil.sendHTML(mailInfo);
		}catch(Exception e){
			logger.error(e, e);
		}
		return null;
	}
	
	/*public static void sendEmail(MailInfo mailInfo) throws Exception{
		logger.debug("...begin to send email :  "+mailInfo.getSubject());
		List<EmailNotifyTarget> targetList = mailInfo.getAllTargetList();//接收和抄送的都获取出来
		if(NullUtil.isEmpty(targetList))
			return;
		
		String result = null;
		try{
			MailUtil.sendHTML(getDefaultSender(),mailInfo);
			logger.debug("...success to send email");
		}catch(Exception e){
			result = e.getLocalizedMessage();
			logger.error("fail to send email...",e);
		}
		
		//记录通知发送记录
		List<NotifyRecord> recordList = new ArrayList<NotifyRecord>();
		int batchCount = targetList.size();
		String batchNo = batchCount > 1 ? ETUtil.getRequestContext().getSn(true)+String.valueOf(System.currentTimeMillis()) : null;
		for(EmailNotifyTarget target : targetList){
			NotifyRecord record = new NotifyRecord();
			record.setSender(getDefaultSender().getUser());
			record.setTarget(target.getAddress());
			record.setType(BaseConstantDefine.NOTIFY_CHANNEL_EMAIL);
			record.setResult(result == null ? "0" : result);
			record.setContent(mailInfo.getHtmlContent());
			
			record.setBatchCount(batchCount);
			record.setBatchNo(batchNo);
			recordList.add(record);
		}
		//DBHelper.getDao().insertBatch(recordList);
		
	}*/
	public static boolean isByEmail(short channel){
		return channel == BaseConsDefine.NOTIFY_CHANNEL.EMAIL.value();
	}
	public static boolean isBySms(short channel){
		return channel == BaseConsDefine.NOTIFY_CHANNEL.SMS.value();
	}
	public static boolean isByApp(short channel){
		return channel == BaseConsDefine.NOTIFY_CHANNEL.APP.value();
	}
	public static boolean isByWechat(short channel){
		return channel == BaseConsDefine.NOTIFY_CHANNEL.WECHAT.value();
	}
	
	
	
	private static NotifyTargetHolder buildNotifyTarget(short channel,String target) throws Exception{
		if(NullUtil.isEmpty(target)){
			return null;
			
		}
		String[] targetArr = target.split(",");
		//List<MisStaff> staffList = CCP.getCustServiceStaffList(target.split(","));
		if(NullUtil.isEmpty(targetArr)){
			return null;
		}
		NotifyTargetHolder holder = new NotifyTargetHolder();
		for(String targetItem : targetArr){
			if(isByEmail(channel)){
				holder.addEmail(targetItem);
			}else if(isBySms(channel)){
				holder.addMobile(targetItem);
			}else if(isByApp(channel)){
				holder.addUserId(Long.valueOf(targetItem));
			}else if(isByWechat(channel)){
				holder.addOpenid(targetItem);
			}
		}
		return holder;
	}
	
	//发送系统消息，对应提醒编码sys_notify
	public static void sendSysNotify(String title,String content) throws Exception{
		NotifyCmp.sendTemplate(
				NotifyDefine.CodeDefine.sys_notify, 
				new KeyValueHolder("title",title)
						.addParam("content",content),
				null);
	}
}
