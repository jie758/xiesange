package com.xiesange.web.task;

import com.xiesange.baseweb.wechat.WechatNode;
import com.xiesange.core.util.LogUtil;
import com.xiesange.gen.dbentity.wx.WxFans;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;

public class WxUnSubscribeTask implements Runnable {
	private WechatNode node;
	public WxUnSubscribeTask(WechatNode node){
		this.node = node;
	}
	@Override
	public void run() {
		try {
			DBHelper.getDao().delete(WxFans.class, new DBCondition(WxFans.JField.openid,node.getFromUserName()));
		} catch (Exception e) {
			LogUtil.getLogger(WxUnSubscribeTask.class).error(e,e);
		}

		
	}

}
