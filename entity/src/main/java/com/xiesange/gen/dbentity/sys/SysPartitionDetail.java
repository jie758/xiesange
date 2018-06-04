package com.xiesange.gen.dbentity.sys;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "SYS_PARTITION_DETAIL",primaryKey="ID",indexes="")
public class SysPartitionDetail extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long partitionId;//对应的分表定义记录ID，sys_partition.id
    private Integer mode;//分表模式，1-取模，2-按年月
    private String fieldName;//针对哪个字段
    private String value;//如果是取模，则填模值；如果是按年月则填年月格式
    private String memo;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(SysPartitionDetail.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setPartitionId(Long partitionId){
	    this.partitionId = partitionId;
		super._setFieldValue(SysPartitionDetail.JField.partitionId, partitionId);
    }
    public Long getPartitionId(){
	    return this.partitionId;
    }
	
    public void setMode(Integer mode){
	    this.mode = mode;
		super._setFieldValue(SysPartitionDetail.JField.mode, mode);
    }
    public Integer getMode(){
	    return this.mode;
    }
	
    public void setFieldName(String fieldName){
	    this.fieldName = fieldName;
		super._setFieldValue(SysPartitionDetail.JField.fieldName, fieldName);
    }
    public String getFieldName(){
	    return this.fieldName;
    }
	
    public void setValue(String value){
	    this.value = value;
		super._setFieldValue(SysPartitionDetail.JField.value, value);
    }
    public String getValue(){
	    return this.value;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(SysPartitionDetail.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(SysPartitionDetail.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(SysPartitionDetail.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(SysPartitionDetail.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        partitionId("PARTITION_ID","BIGINT",19,Long.class,false),
        mode("MODE","INT",10,Integer.class,false),
        fieldName("FIELD_NAME","VARCHAR",64,String.class,false),
        value("VALUE","VARCHAR",256,String.class,false),
        memo("MEMO","VARCHAR",1000,String.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,false),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,false),
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
            return "SYS_PARTITION_DETAIL";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.sys.SysPartitionDetail.class;
        }
    }
	

}