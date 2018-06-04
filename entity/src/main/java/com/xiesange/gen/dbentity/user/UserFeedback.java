package com.xiesange.gen.dbentity.user;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "USER_FEEDBACK",primaryKey="ID",indexes="USER_ID(USER_ID)")
public class UserFeedback extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long userId;
    private String content;
    private Short status;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(UserFeedback.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(UserFeedback.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setContent(String content){
	    this.content = content;
		super._setFieldValue(UserFeedback.JField.content, content);
    }
    public String getContent(){
	    return this.content;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(UserFeedback.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(UserFeedback.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(UserFeedback.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(UserFeedback.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        userId("USER_ID","BIGINT",19,Long.class,true),
        content("CONTENT","VARCHAR",1000,String.class,true),
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
            return "USER_FEEDBACK";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.user.UserFeedback.class;
        }
    }
	

}