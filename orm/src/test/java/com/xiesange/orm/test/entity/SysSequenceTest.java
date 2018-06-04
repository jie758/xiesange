package com.xiesange.orm.test.entity;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "SYS_SEQUENCE",primaryKey="ID",indexes="CODE(TABLE_KEY)")
public class SysSequenceTest extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;//序列自增长
    private String tableKey;
    private Long value;
    private java.util.Date updateTime;
    private java.util.Date createTime;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(SysSequenceTest.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setTableKey(String tableKey){
	    this.tableKey = tableKey;
		super._setFieldValue(SysSequenceTest.JField.tableKey, tableKey);
    }
    public String getTableKey(){
	    return this.tableKey;
    }
	
    public void setValue(Long value){
	    this.value = value;
		super._setFieldValue(SysSequenceTest.JField.value, value);
    }
    public Long getValue(){
	    return this.value;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(SysSequenceTest.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(SysSequenceTest.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        tableKey("TABLE_KEY","VARCHAR",64,String.class,false),
        value("VALUE","BIGINT",19,Long.class,false),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,true);
        
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
            return "SYS_SEQUENCE";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.orm.test.entity.SysSequenceTest.class;
        }
    }
	

}