package com.xiesange.baseweb.notify.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.core.notify.mail.EMailInfo;
import com.xiesange.core.notify.mail.EMailUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.gen.dbentity.notify.NotifyRecord;
import com.xiesange.orm.DBHelper;

public class EmailNotifyTaskBean implements Runnable{
	private static Logger logger = LogUtil.getLogger(EmailNotifyTaskBean.class);
	
	private EMailInfo emailInfo;
	private boolean needRecord;
	/*public EmailNotifyTaskBean(String target,String title,String content,EmailAttachment... attachs){
		emailInfo = new EMailInfo(title,content);
		emailInfo.addTo(target);
		if(NullUtil.isNotEmpty(attachs)){
			for(EmailAttachment attach : attachs){
				emailInfo.addAttach(attach);
			}
		}
		
	}
	
	public EmailNotifyTaskBean(List<String> targets,String title,String content,EmailAttachment... attachs){
		emailInfo = new EMailInfo(title,content);
		for(String target : targets){
			emailInfo.addTo(target);
		}
		if(NullUtil.isNotEmpty(attachs)){
			for(EmailAttachment attach : attachs){
				emailInfo.addAttach(attach);
			}
		}
	}*/
	
	public EmailNotifyTaskBean(EMailInfo emailInfo,boolean needRecord){
		this.emailInfo = emailInfo;
		this.needRecord = needRecord;
	}
	
	@Override
	public void run() {
		logger.debug("begin to run "+EmailNotifyTaskBean.class.getName()+"...");
		try {
			
			EMailUtil.sendHTML(emailInfo);
			
			if(this.needRecord){
				List<NotifyRecord> messageList = new ArrayList<NotifyRecord>();
				long batchId = DBHelper.getDao().getSequence(NotifyRecord.class);
				String content = null;
				for(int i=0;i<emailInfo.getAllTargetList().size();i++){
					String email = emailInfo.getAllTargetList().get(i).getAddress();
					NotifyRecord message = new NotifyRecord();
					content = emailInfo.getHtmlContent();
					if(content.length() > 1900){
						content = content.substring(0, 1900);
					}
					message.setContent(content);
					message.setTitle(emailInfo.getSubject());
					message.setTarget(email);
					message.setChannel(BaseConsDefine.NOTIFY_CHANNEL.WECHAT.value());
					message.setIsRead((short)0);
					
					if(i == 0){
						message.setId(batchId);//第一条记录就用batchId
					}
					message.setBatchNo(String.valueOf(batchId));
					messageList.add(message);
				}
				DBHelper.getDao().insertBatch(messageList);
			}
			
		} catch (Exception e) {
			logger.error(e, e);
		}
		logger.debug("finish "+EmailNotifyTaskBean.class.getName()+"...");
	}
	
	
}
