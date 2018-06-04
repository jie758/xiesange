package com.xiesange.gen.dbentity.notify;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "NOTIFY_TASK",primaryKey="ID",indexes="")
public class NotifyTask extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String target;
    private Short channel;
    private String title;
    private String content;
    private Short isRead;
    private String batchNo;
    private String respCode;
    private String respMsg;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(NotifyTask.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setTarget(String target){
	    this.target = target;
		super._setFieldValue(NotifyTask.JField.target, target);
    }
    public String getTarget(){
	    return this.target;
    }
	
    public void setChannel(Short channel){
	    this.channel = channel;
		super._setFieldValue(NotifyTask.JField.channel, channel);
    }
    public Short getChannel(){
	    return this.channel;
    }
	
    public void setTitle(String title){
	    this.title = title;
		super._setFieldValue(NotifyTask.JField.title, title);
    }
    public String getTitle(){
	    return this.title;
    }
	
    public void setContent(String content){
	    this.content = content;
		super._setFieldValue(NotifyTask.JField.content, content);
    }
    public String getContent(){
	    return this.content;
    }
	
    public void setIsRead(Short isRead){
	    this.isRead = isRead;
		super._setFieldValue(NotifyTask.JField.isRead, isRead);
    }
    public Short getIsRead(){
	    return this.isRead;
    }
	
    public void setBatchNo(String batchNo){
	    this.batchNo = batchNo;
		super._setFieldValue(NotifyTask.JField.batchNo, batchNo);
    }
    public String getBatchNo(){
	    return this.batchNo;
    }
	
    public void setRespCode(String respCode){
	    this.respCode = respCode;
		super._setFieldValue(NotifyTask.JField.respCode, respCode);
    }
    public String getRespCode(){
	    return this.respCode;
    }
	
    public void setRespMsg(String respMsg){
	    this.respMsg = respMsg;
		super._setFieldValue(NotifyTask.JField.respMsg, respMsg);
    }
    public String getRespMsg(){
	    return this.respMsg;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(NotifyTask.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(NotifyTask.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        target("TARGET","VARCHAR",3000,String.class,true),
        channel("CHANNEL","TINYINT",3,Short.class,true),
        title("TITLE","VARCHAR",256,String.class,true),
        content("CONTENT","VARCHAR",2000,String.class,true),
        isRead("IS_READ","TINYINT",3,Short.class,true),
        batchNo("BATCH_NO","VARCHAR",50,String.class,true),
        respCode("RESP_CODE","VARCHAR",50,String.class,true),
        respMsg("RESP_MSG","VARCHAR",1000,String.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,true),
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
            return "NOTIFY_TASK";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.notify.NotifyTask.class;
        }
    }
	

}