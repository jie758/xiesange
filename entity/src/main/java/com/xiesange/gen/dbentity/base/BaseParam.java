package com.xiesange.gen.dbentity.base;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "BASE_PARAM",primaryKey="ID",indexes="CODE(CODE)")
public class BaseParam extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String code;
    private String name;
    private String value;
    private Short appUsed;
    private Short webUsed;
    private String memo;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(BaseParam.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(BaseParam.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(BaseParam.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setValue(String value){
	    this.value = value;
		super._setFieldValue(BaseParam.JField.value, value);
    }
    public String getValue(){
	    return this.value;
    }
	
    public void setAppUsed(Short appUsed){
	    this.appUsed = appUsed;
		super._setFieldValue(BaseParam.JField.appUsed, appUsed);
    }
    public Short getAppUsed(){
	    return this.appUsed;
    }
	
    public void setWebUsed(Short webUsed){
	    this.webUsed = webUsed;
		super._setFieldValue(BaseParam.JField.webUsed, webUsed);
    }
    public Short getWebUsed(){
	    return this.webUsed;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(BaseParam.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(BaseParam.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(BaseParam.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(BaseParam.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        code("CODE","VARCHAR",64,String.class,false),
        name("NAME","VARCHAR",64,String.class,false),
        value("VALUE","VARCHAR",1000,String.class,false),
        appUsed("APP_USED","TINYINT",3,Short.class,true),
        webUsed("WEB_USED","TINYINT",3,Short.class,true),
        memo("MEMO","VARCHAR",1000,String.class,true),
        createTime("CREATE_TIME","DATETIME",null,java.util.Date.class,false),
        updateTime("UPDATE_TIME","DATETIME",null,java.util.Date.class,false),
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
            return "BASE_PARAM";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.base.BaseParam.class;
        }
    }
	

}