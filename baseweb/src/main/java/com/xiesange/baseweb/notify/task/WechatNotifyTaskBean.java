package com.xiesange.baseweb.notify.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.baseweb.wechat.pojo.TemplateMessageParam;
import com.xiesange.core.util.LogUtil;
import com.xiesange.gen.dbentity.notify.NotifyRecord;
import com.xiesange.orm.DBHelper;

public class WechatNotifyTaskBean implements Runnable{
	private static Logger logger = LogUtil.getLogger(WechatNotifyTaskBean.class);
	
	private List<String> targets;//openid列表
	private String url;//点击后的跳转url
	private String tempid;//模板id
	private TemplateMessageParam params;
	
	public WechatNotifyTaskBean(List<String> openidList,String tempid,String url,TemplateMessageParam params){
		this.targets = openidList;
		this.url = url;
		this.tempid = tempid;
		this.params = params;
	}
	
	@Override
	public void run() {
		try {
			List<NotifyRecord> recordList = new ArrayList<NotifyRecord>();
			long batchId = DBHelper.getDao().getSequence(NotifyRecord.class);
			
			for(int i=0;i<targets.size();i++){
				String openid = targets.get(i);
				logger.debug("---------openid:"+openid);
				try {
					WechatCmp.sendTemplateMessage(openid, tempid, url, params);
					
					NotifyRecord record = new NotifyRecord();
					record.setContent(params.buildText());
					record.setTarget(openid);
					record.setChannel(BaseConsDefine.NOTIFY_CHANNEL.SMS.value());
					record.setIsRead((short)0);
					record.setBatchNo(String.valueOf(batchId));
					
					if(i == 0){
						record.setId(batchId);//第一条记录就用batchId
					}
					
					recordList.add(record);
				}catch(Exception e){
					logger.error(e, e);
				}
				
				
			}
			
			DBHelper.getDao().insertBatch(recordList);
		} catch (Exception e) {
			logger.error(e, e);
		}
	}
}
