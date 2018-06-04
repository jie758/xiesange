package com.xiesange.gen.dbentity.user;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "USER_COUPON",primaryKey="ID",indexes="")
public class UserCoupon extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long userId;
    private Long ruleId;
    private Short type;
    private Long value;
    private Long premise;
    private Short src;
    private Short isUsed;
    private java.util.Date consumeTime;
    private java.util.Date expireTime;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(UserCoupon.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(UserCoupon.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setRuleId(Long ruleId){
	    this.ruleId = ruleId;
		super._setFieldValue(UserCoupon.JField.ruleId, ruleId);
    }
    public Long getRuleId(){
	    return this.ruleId;
    }
	
    public void setType(Short type){
	    this.type = type;
		super._setFieldValue(UserCoupon.JField.type, type);
    }
    public Short getType(){
	    return this.type;
    }
	
    public void setValue(Long value){
	    this.value = value;
		super._setFieldValue(UserCoupon.JField.value, value);
    }
    public Long getValue(){
	    return this.value;
    }
	
    public void setPremise(Long premise){
	    this.premise = premise;
		super._setFieldValue(UserCoupon.JField.premise, premise);
    }
    public Long getPremise(){
	    return this.premise;
    }
	
    public void setSrc(Short src){
	    this.src = src;
		super._setFieldValue(UserCoupon.JField.src, src);
    }
    public Short getSrc(){
	    return this.src;
    }
	
    public void setIsUsed(Short isUsed){
	    this.isUsed = isUsed;
		super._setFieldValue(UserCoupon.JField.isUsed, isUsed);
    }
    public Short getIsUsed(){
	    return this.isUsed;
    }
	
    public void setConsumeTime(java.util.Date consumeTime){
	    this.consumeTime = consumeTime;
		super._setFieldValue(UserCoupon.JField.consumeTime, consumeTime);
    }
    public java.util.Date getConsumeTime(){
	    return this.consumeTime;
    }
	
    public void setExpireTime(java.util.Date expireTime){
	    this.expireTime = expireTime;
		super._setFieldValue(UserCoupon.JField.expireTime, expireTime);
    }
    public java.util.Date getExpireTime(){
	    return this.expireTime;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(UserCoupon.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(UserCoupon.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        userId("USER_ID","BIGINT",19,Long.class,true),
        ruleId("RULE_ID","BIGINT",19,Long.class,true),
        type("TYPE","TINYINT",3,Short.class,true),
        value("VALUE","BIGINT",19,Long.class,true),
        premise("PREMISE","BIGINT",19,Long.class,true),
        src("SRC","TINYINT",3,Short.class,true),
        isUsed("IS_USED","TINYINT",3,Short.class,true),
        consumeTime("CONSUME_TIME","DATETIME",19,java.util.Date.class,true),
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
            return "USER_COUPON";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.user.UserCoupon.class;
        }
    }
	

}