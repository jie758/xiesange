package com.xiesange.gen.dbentity.base;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "BASE_ENUM",primaryKey="ID",indexes="")
public class BaseEnum extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String code;
    private String name;
    private Long typeId;
    private String ext;
    private String memo;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(BaseEnum.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(BaseEnum.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(BaseEnum.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setTypeId(Long typeId){
	    this.typeId = typeId;
		super._setFieldValue(BaseEnum.JField.typeId, typeId);
    }
    public Long getTypeId(){
	    return this.typeId;
    }
	
    public void setExt(String ext){
	    this.ext = ext;
		super._setFieldValue(BaseEnum.JField.ext, ext);
    }
    public String getExt(){
	    return this.ext;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(BaseEnum.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(BaseEnum.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(BaseEnum.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(BaseEnum.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        code("CODE","VARCHAR",32,String.class,true),
        name("NAME","VARCHAR",64,String.class,true),
        typeId("TYPE_ID","BIGINT",19,Long.class,true),
        ext("EXT","VARCHAR",1000,String.class,true),
        memo("MEMO","VARCHAR",1000,String.class,true),
        createTime("CREATE_TIME","DATETIME",null,java.util.Date.class,true),
        updateTime("UPDATE_TIME","DATETIME",null,java.util.Date.class,true),
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
            return "BASE_ENUM";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.base.BaseEnum.class;
        }
    }
	

}