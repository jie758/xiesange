package com.xiesange.baseweb.wechat.pojo;


public class WXUserInfo extends BaseReseponse{
	private String openid;
	private Short subscribe;
	private String nickname;
	private Short sex;
	private String language;
	private String city;
	private String province;
	private String country;
	private String headimgurl;
	private String unionid;
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
		public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Short getSex() {
		return sex;
	}
	public void setSex(Short sex) {
		this.sex = sex;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getHeadimgurl() {
		return headimgurl;
	}
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public Short getSubscribe() {
		return subscribe;
	}
	public void setSubscribe(Short subscribe) {
		this.subscribe = subscribe;
	}
	
	
	/*public String getSexName(){
		return UserCoreCmp.getSexName(sex);
	}*/
	
}
