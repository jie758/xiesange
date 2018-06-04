package com.xiesange.baseweb.notify;

import java.util.Date;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.notify.NotifyDefine.CodeDefine;
import com.xiesange.baseweb.notify.task.EmailNotifyTaskBean;
import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.baseweb.wechat.pojo.WxTempMessageParam;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.notify.mail.EMailInfo;
import com.xiesange.core.notify.mail.EMailUtil;
import com.xiesange.core.notify.sms.YunpianUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.mis.MisStaff;
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
    public static void sendTemplate(INotifyCodeDefine templateCodeDefine,KeyValueHolder params,NotifyTargetHolder targetHolder) throws Exception{
    	List<NotifyTemplate> tempList = getTemplateList(templateCodeDefine.name());
    	logger.debug("match notify template count:"+(NullUtil.isEmpty(tempList)?0:tempList.size()));
    	if(NullUtil.isEmpty(tempList))
    		return;
    	if(params == null){
    		params = new KeyValueHolder();
    	}
    	Date now = DateUtil.now();
    	params.addParam(NotifyDefine.Common.create_time.name(), DateUtil.date2Str(now, DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS));
    	params.addParam(NotifyDefine.Common.create_date.name(), DateUtil.date2Str(now, DateUtil.DATE_FORMAT_EN_B_YYYYMMDD));
    	
    	for(NotifyTemplate temp : tempList){
    		short channel = temp.getChannel();
    		if(NullUtil.isNotEmpty(temp.getTarget())){
    			//如果是通知到客服，那么需要根据配置的客服账号去获取通知信息
    			targetHolder = buildNotifyTarget(channel,temp.getTarget(),targetHolder);
    		}
    		if(targetHolder == null)
    			continue;
    		if(isByEmail(channel)){
    			if(NullUtil.isNotEmpty(targetHolder.getEmailList())){
    				sendTemplateByEmail(temp,params,targetHolder.getEmailList());
    			}
    		}else if(isByApp(channel)){
    			/*if(NullUtil.isNotEmpty(targetHolder.getUserIdList())){
    				sendTemplateByApp(temp,params,targetHolder.getUserIdList());
    			}*/
    		}else if(isBySms(channel)){
    			if(NullUtil.isNotEmpty(targetHolder.getMobileList())){
	    			for(int k=0;k<targetHolder.getMobileList().size();k++){
	    				String m = targetHolder.getMobileList().get(k);
	    				int zone = targetHolder.getZoneList().get(k);
	    				
	    				targetHolder.getMobileList().set(k, zone==86?m:zone+m);
	    			}	
	    			sendTemplateBySms(temp,params,targetHolder.getMobileList());
    			}
    		}else if(isByWechat(channel)){
    			if(NullUtil.isNotEmpty(targetHolder.getWechatList())){
    				sendTemplateByWechat(temp,params,targetHolder.getWechatList());
    			}
    		}
    	}
    }
    
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
	private static void sendTemplateBySms(NotifyTemplate tempEntity,KeyValueHolder params,List<String> targetMobileList) throws Exception{
		String content = params==null ? tempEntity.getContent() : ETUtil.parseTextExpression(tempEntity.getContent(), params);
		//TaskManager.execute(new SmsNotifyTaskBean(targetMobileList,content));
		
		YunpianUtil.sendV1Batch(targetMobileList, content);
		
		//return recordList;
	}
    
	private static void sendTemplateByEmail(NotifyTemplate tempEntity,KeyValueHolder params,List<String> targetEmailList) throws Exception{
		if(NullUtil.isEmpty(targetEmailList))
			return;
		String title = params==null ? tempEntity.getTitle() : ETUtil.parseTextExpression(tempEntity.getTitle(), params);
		String content = params==null ? tempEntity.getContent() : ETUtil.parseTextExpression(tempEntity.getContent(), params);
		
		/*String tempStr = SysparamCmp.get(SysParamDefine.EMAIL_TEMPLATE);
		content = ETUtil.parseTextExpression(tempStr, new KeyValueHolder("content",content));
		*/
		EMailInfo mailInfo = new EMailInfo(title,content);
		mailInfo.addTo(targetEmailList);
		
		try{
			TaskManager.execute(new EmailNotifyTaskBean(mailInfo,true));
			//EMailUtil.sendHTML(mailInfo);
		}catch(Exception e){
			logger.error(e, e);
		}
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
	
	
	private static void sendTemplateByWechat(NotifyTemplate tempEntity,KeyValueHolder params,List<String> targetOpenidList) throws Exception{
		String[] items = tempEntity.getContent().split("\\|");
		WxTempMessageParam tempparam = new WxTempMessageParam();
		for(String item : items){
			String[] parts = item.split("=");
			String value = ETUtil.parseTextExpression(parts[1], params);
			value = value.replaceAll("\\\\r", "\r").replaceAll("\\\\n", "\n");
			String color = parts[0].equalsIgnoreCase("first") ? "#FD9603" : null;
			tempparam.addParam(parts[0], value,color);
		}
		String wxUrl = tempEntity.getWxTempUrl();
		wxUrl = ETUtil.parseTextExpression(wxUrl, params);
		//wxUrl = WechatUtil.buildOAuthUrl(wxUrl, (short)1);
		logger.info("______________wxUrl:"+wxUrl);
		//TaskManager.execute(new WechatNotifyTaskBean(targetOpenidList,tempEntity.getWxTempId(),wxUrl,tempparam));
		for(String openid : targetOpenidList){
			WechatCmp.sendTemplateMessage(openid, tempEntity.getWxTempId(), wxUrl, tempparam);
		}
		
	}
	
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
	
	
	
	private static NotifyTargetHolder buildNotifyTarget(short channel,String target,NotifyTargetHolder targetHolder) throws Exception{
		if(NullUtil.isEmpty(target)){
			return null;
		}
		List<MisStaff> staffList = CCP.getCustServiceStaffList(target.split(","));		
		if(NullUtil.isEmpty(staffList)){
			return targetHolder;
		}
		if(targetHolder == null){
			targetHolder = new NotifyTargetHolder();
		}
		for(MisStaff staff : staffList){
			if(isByEmail(channel)){
				if(NullUtil.isNotEmpty(staff.getEmail())){
					targetHolder.addEmail(staff.getEmail());
				}
			}else if(isBySms(channel)){
				if(NullUtil.isNotEmpty(staff.getMobile())){
					targetHolder.addMobile(staff.getMobile());
				}
			}else if(isByApp(channel)){
				if(staff.getAppUserId() != null){
					targetHolder.addUserId(staff.getAppUserId());
				}
			}else if(isByWechat(channel)){
				if(NullUtil.isNotEmpty(staff.getWechat())){
					targetHolder.addWechat(staff.getWechat());
				}
			}
		}
		return targetHolder;
	}
	
	/**
	 * 针对客服发送提醒。所有客服消息用的是同一个通知模板，只是内容用变量替换。
	 * @param title
	 * @param content
	 * @throws Exception
	 * @author Wilson 
	 * @date 2016年12月5日
	 */
	public static void sendSysNotify(INotifyCodeDefine notifyCode,String content) throws Exception{
		//LogUtil.getLogger(NotifyCmp.class).debug("xxxxxxxxxxxxxxxx");
		NotifyCmp.sendTemplate(
				notifyCode, 
				new KeyValueHolder("content",content),
				null);
	}
}
