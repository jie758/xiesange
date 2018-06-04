package com.xiesange.gen.dbentity.notify;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "NOTIFY_VCODE",primaryKey="ID",indexes="CODE(CODE)")
public class NotifyVcode extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String code;
    private String target;
    private java.util.Date expireTime;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(NotifyVcode.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(NotifyVcode.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setTarget(String target){
	    this.target = target;
		super._setFieldValue(NotifyVcode.JField.target, target);
    }
    public String getTarget(){
	    return this.target;
    }
	
    public void setExpireTime(java.util.Date expireTime){
	    this.expireTime = expireTime;
		super._setFieldValue(NotifyVcode.JField.expireTime, expireTime);
    }
    public java.util.Date getExpireTime(){
	    return this.expireTime;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(NotifyVcode.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(NotifyVcode.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        code("CODE","VARCHAR",16,String.class,true),
        target("TARGET","VARCHAR",64,String.class,true),
        expireTime("EXPIRE_TIME","DATETIME",19,java.util.Date.class,true),
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
            return "NOTIFY_VCODE";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.notify.NotifyVcode.class;
        }
    }
	

}