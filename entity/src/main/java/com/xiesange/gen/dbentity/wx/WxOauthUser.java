package com.xiesange.gen.dbentity.wx;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "WX_OAUTH_USER",primaryKey="ID",indexes="")
public class WxOauthUser extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String code;
    private String token;
    private String openid;
    private Short scope;
    private java.util.Date expireTime;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(WxOauthUser.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(WxOauthUser.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setToken(String token){
	    this.token = token;
		super._setFieldValue(WxOauthUser.JField.token, token);
    }
    public String getToken(){
	    return this.token;
    }
	
    public void setOpenid(String openid){
	    this.openid = openid;
		super._setFieldValue(WxOauthUser.JField.openid, openid);
    }
    public String getOpenid(){
	    return this.openid;
    }
	
    public void setScope(Short scope){
	    this.scope = scope;
		super._setFieldValue(WxOauthUser.JField.scope, scope);
    }
    public Short getScope(){
	    return this.scope;
    }
	
    public void setExpireTime(java.util.Date expireTime){
	    this.expireTime = expireTime;
		super._setFieldValue(WxOauthUser.JField.expireTime, expireTime);
    }
    public java.util.Date getExpireTime(){
	    return this.expireTime;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(WxOauthUser.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(WxOauthUser.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(WxOauthUser.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        code("CODE","VARCHAR",32,String.class,true),
        token("TOKEN","VARCHAR",256,String.class,true),
        openid("OPENID","VARCHAR",256,String.class,true),
        scope("SCOPE","TINYINT",3,Short.class,true),
        expireTime("EXPIRE_TIME","DATETIME",19,java.util.Date.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,true),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,true),
        sn("SN","BIGINT",19,Long.class,true);
        
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
            return "WX_OAUTH_USER";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.wx.WxOauthUser.class;
        }
    }
	

}