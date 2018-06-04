package com.xiesange.gen.dbentity.promotion;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "PROMOTION",primaryKey="ID",indexes="")
public class Promotion extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long productId;
    private Short type;
    private Integer conditionAmount;
    private Long conditionSum;
    private Integer conditionCusts;
    private Short actionType;
    private String value;
    private String creatorValue;
    private Integer validity;
    private Short status;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(Promotion.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setProductId(Long productId){
	    this.productId = productId;
		super._setFieldValue(Promotion.JField.productId, productId);
    }
    public Long getProductId(){
	    return this.productId;
    }
	
    public void setType(Short type){
	    this.type = type;
		super._setFieldValue(Promotion.JField.type, type);
    }
    public Short getType(){
	    return this.type;
    }
	
    public void setConditionAmount(Integer conditionAmount){
	    this.conditionAmount = conditionAmount;
		super._setFieldValue(Promotion.JField.conditionAmount, conditionAmount);
    }
    public Integer getConditionAmount(){
	    return this.conditionAmount;
    }
	
    public void setConditionSum(Long conditionSum){
	    this.conditionSum = conditionSum;
		super._setFieldValue(Promotion.JField.conditionSum, conditionSum);
    }
    public Long getConditionSum(){
	    return this.conditionSum;
    }
	
    public void setConditionCusts(Integer conditionCusts){
	    this.conditionCusts = conditionCusts;
		super._setFieldValue(Promotion.JField.conditionCusts, conditionCusts);
    }
    public Integer getConditionCusts(){
	    return this.conditionCusts;
    }
	
    public void setActionType(Short actionType){
	    this.actionType = actionType;
		super._setFieldValue(Promotion.JField.actionType, actionType);
    }
    public Short getActionType(){
	    return this.actionType;
    }
	
    public void setValue(String value){
	    this.value = value;
		super._setFieldValue(Promotion.JField.value, value);
    }
    public String getValue(){
	    return this.value;
    }
	
    public void setCreatorValue(String creatorValue){
	    this.creatorValue = creatorValue;
		super._setFieldValue(Promotion.JField.creatorValue, creatorValue);
    }
    public String getCreatorValue(){
	    return this.creatorValue;
    }
	
    public void setValidity(Integer validity){
	    this.validity = validity;
		super._setFieldValue(Promotion.JField.validity, validity);
    }
    public Integer getValidity(){
	    return this.validity;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(Promotion.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(Promotion.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(Promotion.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        productId("PRODUCT_ID","BIGINT",19,Long.class,true),
        type("TYPE","TINYINT",3,Short.class,true),
        conditionAmount("CONDITION_AMOUNT","INT",10,Integer.class,true),
        conditionSum("CONDITION_SUM","BIGINT",19,Long.class,true),
        conditionCusts("CONDITION_CUSTS","INT",10,Integer.class,true),
        actionType("ACTION_TYPE","TINYINT",3,Short.class,true),
        value("VALUE","VARCHAR",50,String.class,true),
        creatorValue("CREATOR_VALUE","VARCHAR",50,String.class,true),
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
            return "PROMOTION";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.promotion.Promotion.class;
        }
    }
	

}