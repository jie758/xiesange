package com.xiesange.gen.dbentity.purchase;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "PURCHASE_ITEM",primaryKey="ID",indexes="")
public class PurchaseItem extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long purchaseId;
    private Long orderId;
    private Long userId;
    private Long orderSum;
    private Long orderExpressFee;
    private Long sum;
    private Long expressFee;
    private Integer weight;
    private java.util.Date lastNotifyTime;
    private String memo;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(PurchaseItem.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setPurchaseId(Long purchaseId){
	    this.purchaseId = purchaseId;
		super._setFieldValue(PurchaseItem.JField.purchaseId, purchaseId);
    }
    public Long getPurchaseId(){
	    return this.purchaseId;
    }
	
    public void setOrderId(Long orderId){
	    this.orderId = orderId;
		super._setFieldValue(PurchaseItem.JField.orderId, orderId);
    }
    public Long getOrderId(){
	    return this.orderId;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(PurchaseItem.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setOrderSum(Long orderSum){
	    this.orderSum = orderSum;
		super._setFieldValue(PurchaseItem.JField.orderSum, orderSum);
    }
    public Long getOrderSum(){
	    return this.orderSum;
    }
	
    public void setOrderExpressFee(Long orderExpressFee){
	    this.orderExpressFee = orderExpressFee;
		super._setFieldValue(PurchaseItem.JField.orderExpressFee, orderExpressFee);
    }
    public Long getOrderExpressFee(){
	    return this.orderExpressFee;
    }
	
    public void setSum(Long sum){
	    this.sum = sum;
		super._setFieldValue(PurchaseItem.JField.sum, sum);
    }
    public Long getSum(){
	    return this.sum;
    }
	
    public void setExpressFee(Long expressFee){
	    this.expressFee = expressFee;
		super._setFieldValue(PurchaseItem.JField.expressFee, expressFee);
    }
    public Long getExpressFee(){
	    return this.expressFee;
    }
	
    public void setWeight(Integer weight){
	    this.weight = weight;
		super._setFieldValue(PurchaseItem.JField.weight, weight);
    }
    public Integer getWeight(){
	    return this.weight;
    }
	
    public void setLastNotifyTime(java.util.Date lastNotifyTime){
	    this.lastNotifyTime = lastNotifyTime;
		super._setFieldValue(PurchaseItem.JField.lastNotifyTime, lastNotifyTime);
    }
    public java.util.Date getLastNotifyTime(){
	    return this.lastNotifyTime;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(PurchaseItem.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(PurchaseItem.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(PurchaseItem.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(PurchaseItem.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        purchaseId("PURCHASE_ID","BIGINT",19,Long.class,true),
        orderId("ORDER_ID","BIGINT",19,Long.class,true),
        userId("USER_ID","BIGINT",19,Long.class,true),
        orderSum("ORDER_SUM","BIGINT",19,Long.class,true),
        orderExpressFee("ORDER_EXPRESS_FEE","BIGINT",19,Long.class,true),
        sum("SUM","BIGINT",19,Long.class,true),
        expressFee("EXPRESS_FEE","BIGINT",19,Long.class,true),
        weight("WEIGHT","INT",10,Integer.class,true),
        lastNotifyTime("LAST_NOTIFY_TIME","DATETIME",19,java.util.Date.class,true),
        memo("MEMO","VARCHAR",1000,String.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,false),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,false),
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
            return "PURCHASE_ITEM";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.purchase.PurchaseItem.class;
        }
    }
	

}