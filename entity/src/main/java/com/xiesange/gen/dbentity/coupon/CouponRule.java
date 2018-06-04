package com.xiesange.gen.dbentity.coupon;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "COUPON_RULE",primaryKey="ID",indexes="")
public class CouponRule extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String name;
    private String redeemCode;
    private Short triggerType;
    private Short type;
    private Long value;
    private Long premise;
    private Long productId;
    private Integer validity;
    private Short status;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(CouponRule.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(CouponRule.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setRedeemCode(String redeemCode){
	    this.redeemCode = redeemCode;
		super._setFieldValue(CouponRule.JField.redeemCode, redeemCode);
    }
    public String getRedeemCode(){
	    return this.redeemCode;
    }
	
    public void setTriggerType(Short triggerType){
	    this.triggerType = triggerType;
		super._setFieldValue(CouponRule.JField.triggerType, triggerType);
    }
    public Short getTriggerType(){
	    return this.triggerType;
    }
	
    public void setType(Short type){
	    this.type = type;
		super._setFieldValue(CouponRule.JField.type, type);
    }
    public Short getType(){
	    return this.type;
    }
	
    public void setValue(Long value){
	    this.value = value;
		super._setFieldValue(CouponRule.JField.value, value);
    }
    public Long getValue(){
	    return this.value;
    }
	
    public void setPremise(Long premise){
	    this.premise = premise;
		super._setFieldValue(CouponRule.JField.premise, premise);
    }
    public Long getPremise(){
	    return this.premise;
    }
	
    public void setProductId(Long productId){
	    this.productId = productId;
		super._setFieldValue(CouponRule.JField.productId, productId);
    }
    public Long getProductId(){
	    return this.productId;
    }
	
    public void setValidity(Integer validity){
	    this.validity = validity;
		super._setFieldValue(CouponRule.JField.validity, validity);
    }
    public Integer getValidity(){
	    return this.validity;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(CouponRule.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(CouponRule.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(CouponRule.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        name("NAME","VARCHAR",32,String.class,true),
        redeemCode("REDEEM_CODE","VARCHAR",32,String.class,true),
        triggerType("TRIGGER_TYPE","SMALLINT",5,Short.class,true),
        type("TYPE","TINYINT",3,Short.class,true),
        value("VALUE","BIGINT",19,Long.class,true),
        premise("PREMISE","BIGINT",19,Long.class,true),
        productId("PRODUCT_ID","BIGINT",19,Long.class,true),
        validity("VALIDITY","INT",10,Integer.class,true),
        status("STATUS","TINYINT",3,Short.class,true),
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
            return "COUPON_RULE";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.coupon.CouponRule.class;
        }
    }
	

}