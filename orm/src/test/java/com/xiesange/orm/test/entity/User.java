package com.xiesange.orm.test.entity;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "USER",primaryKey="ID",indexes="EMAIL(EMAIL);MOBILE(MOBILE);WECHAT(WECHAT)")
public class User extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String mobile;
    private String email;
    private String wechat;
    private String bankCard;
    private String password;
    private String nickname;
    private Short sex;
    private java.util.Date birthday;
    private String profilePic;
    private Short type;
    private Short sts;
    private Long sn;
    private java.util.Date createTime;
    private java.util.Date updateTime;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(User.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setMobile(String mobile){
	    this.mobile = mobile;
		super._setFieldValue(User.JField.mobile, mobile);
    }
    public String getMobile(){
	    return this.mobile;
    }
	
    public void setEmail(String email){
	    this.email = email;
		super._setFieldValue(User.JField.email, email);
    }
    public String getEmail(){
	    return this.email;
    }
	
    public void setWechat(String wechat){
	    this.wechat = wechat;
		super._setFieldValue(User.JField.wechat, wechat);
    }
    public String getWechat(){
	    return this.wechat;
    }
	
    public void setBankCard(String bankCard){
	    this.bankCard = bankCard;
		super._setFieldValue(User.JField.bankCard, bankCard);
    }
    public String getBankCard(){
	    return this.bankCard;
    }
	
    public void setPassword(String password){
	    this.password = password;
		super._setFieldValue(User.JField.password, password);
    }
    public String getPassword(){
	    return this.password;
    }
	
    public void setNickname(String nickname){
	    this.nickname = nickname;
		super._setFieldValue(User.JField.nickname, nickname);
    }
    public String getNickname(){
	    return this.nickname;
    }
	
    public void setSex(Short sex){
	    this.sex = sex;
		super._setFieldValue(User.JField.sex, sex);
    }
    public Short getSex(){
	    return this.sex;
    }
	
    public void setBirthday(java.util.Date birthday){
	    this.birthday = birthday;
		super._setFieldValue(User.JField.birthday, birthday);
    }
    public java.util.Date getBirthday(){
	    return this.birthday;
    }
	
    public void setProfilePic(String profilePic){
	    this.profilePic = profilePic;
		super._setFieldValue(User.JField.profilePic, profilePic);
    }
    public String getProfilePic(){
	    return this.profilePic;
    }
	
    public void setType(Short type){
	    this.type = type;
		super._setFieldValue(User.JField.type, type);
    }
    public Short getType(){
	    return this.type;
    }
	
    public void setSts(Short sts){
	    this.sts = sts;
		super._setFieldValue(User.JField.sts, sts);
    }
    public Short getSts(){
	    return this.sts;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(User.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(User.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(User.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        mobile("MOBILE","VARCHAR",16,String.class,true),
        email("EMAIL","VARCHAR",64,String.class,true),
        wechat("WECHAT","VARCHAR",64,String.class,true),
        bankCard("BANK_CARD","VARCHAR",32,String.class,true),
        password("PASSWORD","VARCHAR",128,String.class,true),
        nickname("NICKNAME","VARCHAR",128,String.class,false),
        sex("SEX","TINYINT",3,Short.class,true),
        birthday("BIRTHDAY","DATE",10,java.util.Date.class,true),
        profilePic("PROFILE_PIC","VARCHAR",256,String.class,true),
        type("TYPE","TINYINT",3,Short.class,false),
        sts("STS","TINYINT",3,Short.class,false),
        sn("SN","BIGINT",19,Long.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,false),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,false);
        
        private String colName;
        private String colTypeName;
        private Integer length;
		private Class<?> javaType;
		private boolean nullable;
		
        private JField(String colName,String colTypeName,Integer length,Class<?> javaType,boolean nullable){
            this.colName = colName;
            this.colTypeName = colTypeName;
            this.length = length;
			this.javaType = javaType;
			this.nullable = nullable;
    	}
            
    	@Override
    	public String getName()
    	{
            return this.name();
    	}
    
        @Override
        public String getColName()
        {
            return this.colName;
        }
        @Override
        public String getColTypeName()
        {
            return this.colTypeName;
        }
        @Override
        public Integer getLength()
        {
            return length;
        }
        @Override
        public boolean getNullable()
        {
            return this.nullable;
        }
		@Override
        public String getTableName()
        {
            return "USER";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.orm.test.entity.User.class;
        }
    }
	

}