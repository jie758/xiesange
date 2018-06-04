package com.xiesange.gen.dbentity.mis;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "MIS_STAFF",primaryKey="ID",indexes="EMAIL(EMAIL);MOBILE(ACCOUNT)")
public class MisStaff extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String account;
    private String password;
    private String name;
    private Short sex;
    private java.util.Date birthday;
    private String email;
    private String mobile;
    private Long appUserId;
    private String wechat;
    private String profilePic;
    private Short sts;
    private Long sn;
    private java.util.Date createTime;
    private java.util.Date updateTime;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(MisStaff.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setAccount(String account){
	    this.account = account;
		super._setFieldValue(MisStaff.JField.account, account);
    }
    public String getAccount(){
	    return this.account;
    }
	
    public void setPassword(String password){
	    this.password = password;
		super._setFieldValue(MisStaff.JField.password, password);
    }
    public String getPassword(){
	    return this.password;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(MisStaff.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setSex(Short sex){
	    this.sex = sex;
		super._setFieldValue(MisStaff.JField.sex, sex);
    }
    public Short getSex(){
	    return this.sex;
    }
	
    public void setBirthday(java.util.Date birthday){
	    this.birthday = birthday;
		super._setFieldValue(MisStaff.JField.birthday, birthday);
    }
    public java.util.Date getBirthday(){
	    return this.birthday;
    }
	
    public void setEmail(String email){
	    this.email = email;
		super._setFieldValue(MisStaff.JField.email, email);
    }
    public String getEmail(){
	    return this.email;
    }
	
    public void setMobile(String mobile){
	    this.mobile = mobile;
		super._setFieldValue(MisStaff.JField.mobile, mobile);
    }
    public String getMobile(){
	    return this.mobile;
    }
	
    public void setAppUserId(Long appUserId){
	    this.appUserId = appUserId;
		super._setFieldValue(MisStaff.JField.appUserId, appUserId);
    }
    public Long getAppUserId(){
	    return this.appUserId;
    }
	
    public void setWechat(String wechat){
	    this.wechat = wechat;
		super._setFieldValue(MisStaff.JField.wechat, wechat);
    }
    public String getWechat(){
	    return this.wechat;
    }
	
    public void setProfilePic(String profilePic){
	    this.profilePic = profilePic;
		super._setFieldValue(MisStaff.JField.profilePic, profilePic);
    }
    public String getProfilePic(){
	    return this.profilePic;
    }
	
    public void setSts(Short sts){
	    this.sts = sts;
		super._setFieldValue(MisStaff.JField.sts, sts);
    }
    public Short getSts(){
	    return this.sts;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(MisStaff.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(MisStaff.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(MisStaff.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        account("ACCOUNT","VARCHAR",32,String.class,true),
        password("PASSWORD","VARCHAR",64,String.class,true),
        name("NAME","VARCHAR",32,String.class,true),
        sex("SEX","TINYINT",3,Short.class,true),
        birthday("BIRTHDAY","DATE",10,java.util.Date.class,true),
        email("EMAIL","VARCHAR",64,String.class,true),
        mobile("MOBILE","VARCHAR",16,String.class,true),
        appUserId("APP_USER_ID","BIGINT",19,Long.class,true),
        wechat("WECHAT","VARCHAR",64,String.class,true),
        profilePic("PROFILE_PIC","VARCHAR",256,String.class,true),
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
            return "MIS_STAFF";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.mis.MisStaff.class;
        }
    }
	

}