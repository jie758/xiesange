package com.xiesange.gen.dbentity.wx;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "WX_CONFIG",primaryKey="ID",indexes="")
public class WxConfig extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String type;
    private String value;
    private java.util.Date expireTime;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(WxConfig.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setType(String type){
	    this.type = type;
		super._setFieldValue(WxConfig.JField.type, type);
    }
    public String getType(){
	    return this.type;
    }
	
    public void setValue(String value){
	    this.value = value;
		super._setFieldValue(WxConfig.JField.value, value);
    }
    public String getValue(){
	    return this.value;
    }
	
    public void setExpireTime(java.util.Date expireTime){
	    this.expireTime = expireTime;
		super._setFieldValue(WxConfig.JField.expireTime, expireTime);
    }
    public java.util.Date getExpireTime(){
	    return this.expireTime;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(WxConfig.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(WxConfig.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        type("TYPE","VARCHAR",32,String.class,true),
        value("VALUE","VARCHAR",256,String.class,true),
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
            return "WX_CONFIG";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.wx.WxConfig.class;
        }
    }
	

}