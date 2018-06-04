package com.xiesange.gen.dbentity.groupbuy;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "GROUPBUY",primaryKey="ID",indexes="")
public class Groupbuy extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long userId;
    private String productInfo;
    private String intro;
    private Short status;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(Groupbuy.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(Groupbuy.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setProductInfo(String productInfo){
	    this.productInfo = productInfo;
		super._setFieldValue(Groupbuy.JField.productInfo, productInfo);
    }
    public String getProductInfo(){
	    return this.productInfo;
    }
	
    public void setIntro(String intro){
	    this.intro = intro;
		super._setFieldValue(Groupbuy.JField.intro, intro);
    }
    public String getIntro(){
	    return this.intro;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(Groupbuy.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(Groupbuy.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(Groupbuy.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(Groupbuy.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        userId("USER_ID","BIGINT",19,Long.class,true),
        productInfo("PRODUCT_INFO","VARCHAR",1000,String.class,true),
        intro("INTRO","VARCHAR",64,String.class,true),
        status("STATUS","TINYINT",3,Short.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,true),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,true),
        sn("SN","BIGINT",19,Long.class,false);
        
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
            return "GROUPBUY";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.groupbuy.Groupbuy.class;
        }
    }
	

}