package com.xiesange.baseweb.notify.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.core.notify.NotifyResponse;
import com.xiesange.core.notify.sms.YunpianUtil;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.gen.dbentity.notify.NotifyRecord;
import com.xiesange.orm.DBHelper;

public class SmsNotifyTaskBean implements Runnable{
	private static Logger logger = LogUtil.getLogger(SmsNotifyTaskBean.class);
	
	private List<String> targets;//手机号
	private String content;
	
	public SmsNotifyTaskBean(String target,String content){
		this.targets = ClassUtil.newList();
		this.targets.add(target);
		this.content = content;
	}
	
	public SmsNotifyTaskBean(List<String> targetList,String content){
		this.targets = targetList;
		this.content = content;
	}
	
	@Override
	public void run() {
		try {
			NotifyResponse response = YunpianUtil.sendV1Batch(targets, content);
			List<NotifyRecord> recordList = new ArrayList<NotifyRecord>();
			long batchId = DBHelper.getDao().getSequence(NotifyRecord.class);
			for(int i=0;i<targets.size();i++){
				NotifyRecord record = new NotifyRecord();
				record.setContent(content);
				record.setTarget(targets.get(i));
				record.setChannel(BaseConsDefine.NOTIFY_CHANNEL.SMS.value());
				record.setIsRead((short)0);
				
				if(i == 0){
					record.setId(batchId);//第一条记录就用batchId
					//因为是批量，应答code和message都是相同的，不用每条都记录，减少存储空间，所以只记录在第一条里
					record.setRespCode(response.getCode());
					record.setRespMsg(response.getMessage());
				}
				record.setBatchNo(String.valueOf(batchId));
				recordList.add(record);
			}
			DBHelper.getDao().insertBatch(recordList);
		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	
}
