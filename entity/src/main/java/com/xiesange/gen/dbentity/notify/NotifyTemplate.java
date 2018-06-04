package com.xiesange.gen.dbentity.notify;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "NOTIFY_TEMPLATE",primaryKey="ID",indexes="TARGET_USER_ID(CODE)")
public class NotifyTemplate extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String code;
    private String name;
    private String title;
    private String content;
    private Short channel;
    private String target;
    private Short targetType;
    private String wxTempId;
    private String wxTempUrl;
    private Short status;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(NotifyTemplate.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(NotifyTemplate.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(NotifyTemplate.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setTitle(String title){
	    this.title = title;
		super._setFieldValue(NotifyTemplate.JField.title, title);
    }
    public String getTitle(){
	    return this.title;
    }
	
    public void setContent(String content){
	    this.content = content;
		super._setFieldValue(NotifyTemplate.JField.content, content);
    }
    public String getContent(){
	    return this.content;
    }
	
    public void setChannel(Short channel){
	    this.channel = channel;
		super._setFieldValue(NotifyTemplate.JField.channel, channel);
    }
    public Short getChannel(){
	    return this.channel;
    }
	
    public void setTarget(String target){
	    this.target = target;
		super._setFieldValue(NotifyTemplate.JField.target, target);
    }
    public String getTarget(){
	    return this.target;
    }
	
    public void setTargetType(Short targetType){
	    this.targetType = targetType;
		super._setFieldValue(NotifyTemplate.JField.targetType, targetType);
    }
    public Short getTargetType(){
	    return this.targetType;
    }
	
    public void setWxTempId(String wxTempId){
	    this.wxTempId = wxTempId;
		super._setFieldValue(NotifyTemplate.JField.wxTempId, wxTempId);
    }
    public String getWxTempId(){
	    return this.wxTempId;
    }
	
    public void setWxTempUrl(String wxTempUrl){
	    this.wxTempUrl = wxTempUrl;
		super._setFieldValue(NotifyTemplate.JField.wxTempUrl, wxTempUrl);
    }
    public String getWxTempUrl(){
	    return this.wxTempUrl;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(NotifyTemplate.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(NotifyTemplate.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(NotifyTemplate.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(NotifyTemplate.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        code("CODE","VARCHAR",64,String.class,true),
        name("NAME","VARCHAR",128,String.class,true),
        title("TITLE","VARCHAR",128,String.class,true),
        content("CONTENT","VARCHAR",5000,String.class,true),
        channel("CHANNEL","TINYINT",3,Short.class,true),
        target("TARGET","VARCHAR",128,String.class,true),
        targetType("TARGET_TYPE","TINYINT",3,Short.class,true),
        wxTempId("WX_TEMP_ID","VARCHAR",256,String.class,true),
        wxTempUrl("WX_TEMP_URL","VARCHAR",1000,String.class,true),
        status("STATUS","TINYINT",3,Short.class,true),
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
            return "NOTIFY_TEMPLATE";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.notify.NotifyTemplate.class;
        }
    }
	

}