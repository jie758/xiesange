package com.xiesange.gen.dbentity.sys;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "SYS_LOGIN",primaryKey="ID",indexes="TOKEN(TOKEN);USER_ID(USER_ID)")
public class SysLogin extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long userId;
    private Short sysType;
    private String token;
    private Short channel;
    private String signKey;
    private String clientIp;
    private String clientUid;
    private java.util.Date logoutTime;
    private java.util.Date createTime;
    private java.util.Date expireTime;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(SysLogin.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(SysLogin.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setSysType(Short sysType){
	    this.sysType = sysType;
		super._setFieldValue(SysLogin.JField.sysType, sysType);
    }
    public Short getSysType(){
	    return this.sysType;
    }
	
    public void setToken(String token){
	    this.token = token;
		super._setFieldValue(SysLogin.JField.token, token);
    }
    public String getToken(){
	    return this.token;
    }
	
    public void setChannel(Short channel){
	    this.channel = channel;
		super._setFieldValue(SysLogin.JField.channel, channel);
    }
    public Short getChannel(){
	    return this.channel;
    }
	
    public void setSignKey(String signKey){
	    this.signKey = signKey;
		super._setFieldValue(SysLogin.JField.signKey, signKey);
    }
    public String getSignKey(){
	    return this.signKey;
    }
	
    public void setClientIp(String clientIp){
	    this.clientIp = clientIp;
		super._setFieldValue(SysLogin.JField.clientIp, clientIp);
    }
    public String getClientIp(){
	    return this.clientIp;
    }
	
    public void setClientUid(String clientUid){
	    this.clientUid = clientUid;
		super._setFieldValue(SysLogin.JField.clientUid, clientUid);
    }
    public String getClientUid(){
	    return this.clientUid;
    }
	
    public void setLogoutTime(java.util.Date logoutTime){
	    this.logoutTime = logoutTime;
		super._setFieldValue(SysLogin.JField.logoutTime, logoutTime);
    }
    public java.util.Date getLogoutTime(){
	    return this.logoutTime;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(SysLogin.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setExpireTime(java.util.Date expireTime){
	    this.expireTime = expireTime;
		super._setFieldValue(SysLogin.JField.expireTime, expireTime);
    }
    public java.util.Date getExpireTime(){
	    return this.expireTime;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        userId("USER_ID","BIGINT",19,Long.class,false),
        sysType("SYS_TYPE","TINYINT",3,Short.class,true),
        token("TOKEN","VARCHAR",32,String.class,false),
        channel("CHANNEL","TINYINT",3,Short.class,true),
        signKey("SIGN_KEY","VARCHAR",64,String.class,true),
        clientIp("CLIENT_IP","VARCHAR",64,String.class,true),
        clientUid("CLIENT_UID","VARCHAR",64,String.class,true),
        logoutTime("LOGOUT_TIME","DATETIME",19,java.util.Date.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,false),
        expireTime("EXPIRE_TIME","DATETIME",19,java.util.Date.class,false);
        
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
            return "SYS_LOGIN";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.sys.SysLogin.class;
        }
    }
	

}