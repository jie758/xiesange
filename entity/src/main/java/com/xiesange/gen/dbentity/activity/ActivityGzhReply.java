package com.xiesange.gen.dbentity.activity;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "ACTIVITY_GZH_REPLY",primaryKey="ID",indexes="")
public class ActivityGzhReply extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String keyword;
    private Short replyType;
    private String replyValue;
    private String replyMediaId;
    private String qrcodeUrl;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(ActivityGzhReply.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setKeyword(String keyword){
	    this.keyword = keyword;
		super._setFieldValue(ActivityGzhReply.JField.keyword, keyword);
    }
    public String getKeyword(){
	    return this.keyword;
    }
	
    public void setReplyType(Short replyType){
	    this.replyType = replyType;
		super._setFieldValue(ActivityGzhReply.JField.replyType, replyType);
    }
    public Short getReplyType(){
	    return this.replyType;
    }
	
    public void setReplyValue(String replyValue){
	    this.replyValue = replyValue;
		super._setFieldValue(ActivityGzhReply.JField.replyValue, replyValue);
    }
    public String getReplyValue(){
	    return this.replyValue;
    }
	
    public void setReplyMediaId(String replyMediaId){
	    this.replyMediaId = replyMediaId;
		super._setFieldValue(ActivityGzhReply.JField.replyMediaId, replyMediaId);
    }
    public String getReplyMediaId(){
	    return this.replyMediaId;
    }
	
    public void setQrcodeUrl(String qrcodeUrl){
	    this.qrcodeUrl = qrcodeUrl;
		super._setFieldValue(ActivityGzhReply.JField.qrcodeUrl, qrcodeUrl);
    }
    public String getQrcodeUrl(){
	    return this.qrcodeUrl;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(ActivityGzhReply.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(ActivityGzhReply.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        keyword("KEYWORD","VARCHAR",50,String.class,true),
        replyType("REPLY_TYPE","TINYINT",3,Short.class,true),
        replyValue("REPLY_VALUE","TEXT",65535,String.class,true),
        replyMediaId("REPLY_MEDIA_ID","VARCHAR",128,String.class,true),
        qrcodeUrl("QRCODE_URL","VARCHAR",1000,String.class,true),
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
            return "ACTIVITY_GZH_REPLY";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.activity.ActivityGzhReply.class;
        }
    }
	

}