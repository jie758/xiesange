package com.xiesange.baseweb.notify;

import java.util.ArrayList;
import java.util.List;

import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.mis.MisStaff;
import com.xiesange.gen.dbentity.user.User;

public class NotifyTargetHolder {
	private static final int CHINA_ZONE = 86;
	private List<String> emailList;
	private List<String> mobileList;
	private List<Integer> zoneList;
	private List<Long> userIdList;
	private List<String> openidList;
	
	public NotifyTargetHolder(){
	}
	/*public NotifyTargetHolder(List<MisStaff> custStaffList){
		this.addUserList(custStaffList);
	}
	public NotifyTargetHolder(MisStaff user){
		this.addUser(user);
	}
	public NotifyTargetHolder(String email){
		this.addEmail(email);
	}*/
	
	public NotifyTargetHolder addEmail(String email){
		if(emailList == null){
			emailList = new ArrayList<String>();
		}
		emailList.add(email);
		return this;
	}
	public NotifyTargetHolder addOpenid(String openid){
		if(openidList == null){
			openidList = new ArrayList<String>();
		}
		openidList.add(openid);
		return this;
	}
	public NotifyTargetHolder addMobile(String mobile){
		return addMobile(mobile,CHINA_ZONE);//添加中国号码
	}
	public NotifyTargetHolder addMobile(String mobile,Integer zone){
		if(mobileList == null){
			mobileList = new ArrayList<String>();
		}
		mobileList.add(mobile);
		
		if(zoneList == null){
			zoneList = new ArrayList<Integer>();
		}
		zoneList.add(zone);
		return this;
	}
	public NotifyTargetHolder addUserId(long userId){
		if(userIdList == null){
			userIdList = new ArrayList<Long>();
		}
		userIdList.add(userId);
		return this;
	}
	public List<String> getEmailList() {
		return emailList;
	}
	public void setEmailList(List<String> emailList) {
		this.emailList = emailList;
	}
	public List<String> getMobileList() {
		return mobileList;
	}
	public List<Integer> getZoneList() {
		return zoneList;
	}
	public List<String> getOpenidList() {
		return openidList;
	}
	public void setMobileList(List<String> mobileList) {
		this.mobileList = mobileList;
	}
	public List<Long> getUserIdList() {
		return userIdList;
	}
	public void setUserIdList(List<Long> userIdList) {
		this.userIdList = userIdList;
	}
	
	public NotifyTargetHolder addStaff(MisStaff staff){
		if(NullUtil.isNotEmpty(staff.getEmail()))
			this.addEmail(staff.getEmail());
		if(staff.getAppUserId() != null)
			this.addUserId(staff.getAppUserId());
		if(NullUtil.isNotEmpty(staff.getMobile()))
			this.addMobile(staff.getMobile());
		return this;
	}
	public NotifyTargetHolder addUser(User user){
		this.addUserId(user.getId());
		/*if(NullUtil.isNotEmpty(user.getEmail()))
			this.addEmail(user.getEmail());*/
		if(NullUtil.isNotEmpty(user.getMobile()))
			this.addMobile(user.getMobile());
		if(NullUtil.isNotEmpty(user.getWechat()))
			this.addOpenid(user.getWechat());
		return this;
	}
	public NotifyTargetHolder addStaffList(List<MisStaff> staffList){
		for(MisStaff staff : staffList){
			addStaff(staff);
		}
		return this;
	}
	public NotifyTargetHolder addUserList(List<User> userList){
		for(User user : userList){
			addUser(user);
		}
		return this;
	}
	
	public NotifyTargetHolder addWechat(String wechat){
		if(openidList == null){
			openidList = new ArrayList<String>();
		}
		openidList.add(wechat);
		return this;
	}

	public List<String> getWechatList() {
		return openidList;
	}
}
