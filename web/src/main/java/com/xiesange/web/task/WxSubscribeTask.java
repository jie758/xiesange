package com.xiesange.web.task;

import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.baseweb.wechat.WechatNode;
import com.xiesange.baseweb.wechat.pojo.WXUserInfo;
import com.xiesange.core.util.LogUtil;
import com.xiesange.gen.dbentity.wx.WxFans;
import com.xiesange.orm.DBHelper;

public class WxSubscribeTask implements Runnable {
	private WechatNode node;
	public WxSubscribeTask(WechatNode node){
		this.node = node;
	}
	@Override
	public void run() {
		try {
			WXUserInfo wxUser = WechatCmp.getFans(node.getFromUserName());
			
			WxFans fans = new WxFans();
			fans.setNickname(wxUser.getNickname());
			fans.setOpenid(wxUser.getOpenid());
			fans.setPic(wxUser.getHeadimgurl());
			fans.setSex(wxUser.getSex());
			fans.setUnionId(wxUser.getUnionid());
			DBHelper.getDao().insert(fans);
		} catch (Exception e) {
			LogUtil.getLogger(WxSubscribeTask.class).error(e,e);
		}

		
	}

}
