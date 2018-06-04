package com.xiesange.gen.dbentity.orders;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "ORDERS_ITEM",primaryKey="ID",indexes="")
public class OrdersItem extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer amount;
    private Long price;
    private Long costPrice;
    private Long sum;
    private String memo;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(OrdersItem.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setOrderId(Long orderId){
	    this.orderId = orderId;
		super._setFieldValue(OrdersItem.JField.orderId, orderId);
    }
    public Long getOrderId(){
	    return this.orderId;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(OrdersItem.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setProductId(Long productId){
	    this.productId = productId;
		super._setFieldValue(OrdersItem.JField.productId, productId);
    }
    public Long getProductId(){
	    return this.productId;
    }
	
    public void setAmount(Integer amount){
	    this.amount = amount;
		super._setFieldValue(OrdersItem.JField.amount, amount);
    }
    public Integer getAmount(){
	    return this.amount;
    }
	
    public void setPrice(Long price){
	    this.price = price;
		super._setFieldValue(OrdersItem.JField.price, price);
    }
    public Long getPrice(){
	    return this.price;
    }
	
    public void setCostPrice(Long costPrice){
	    this.costPrice = costPrice;
		super._setFieldValue(OrdersItem.JField.costPrice, costPrice);
    }
    public Long getCostPrice(){
	    return this.costPrice;
    }
	
    public void setSum(Long sum){
	    this.sum = sum;
		super._setFieldValue(OrdersItem.JField.sum, sum);
    }
    public Long getSum(){
	    return this.sum;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(OrdersItem.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(OrdersItem.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(OrdersItem.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(OrdersItem.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        orderId("ORDER_ID","BIGINT",19,Long.class,true),
        userId("USER_ID","BIGINT",19,Long.class,true),
        productId("PRODUCT_ID","BIGINT",19,Long.class,true),
        amount("AMOUNT","INT",10,Integer.class,true),
        price("PRICE","BIGINT",19,Long.class,true),
        costPrice("COST_PRICE","BIGINT",19,Long.class,true),
        sum("SUM","BIGINT",19,Long.class,true),
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
            return "ORDERS_ITEM";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.orders.OrdersItem.class;
        }
    }
	

}