package com.xiesange.gen.dbentity.purchase;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "PURCHASE",primaryKey="ID",indexes="")
public class Purchase extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long sum;
    private String prices;
    private Long expressFee;
    private Integer orderCount;
    private Long orderSum;
    private Long orderExpressFee;
    private Short status;
    private String memo;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(Purchase.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setSum(Long sum){
	    this.sum = sum;
		super._setFieldValue(Purchase.JField.sum, sum);
    }
    public Long getSum(){
	    return this.sum;
    }
	
    public void setPrices(String prices){
	    this.prices = prices;
		super._setFieldValue(Purchase.JField.prices, prices);
    }
    public String getPrices(){
	    return this.prices;
    }
	
    public void setExpressFee(Long expressFee){
	    this.expressFee = expressFee;
		super._setFieldValue(Purchase.JField.expressFee, expressFee);
    }
    public Long getExpressFee(){
	    return this.expressFee;
    }
	
    public void setOrderCount(Integer orderCount){
	    this.orderCount = orderCount;
		super._setFieldValue(Purchase.JField.orderCount, orderCount);
    }
    public Integer getOrderCount(){
	    return this.orderCount;
    }
	
    public void setOrderSum(Long orderSum){
	    this.orderSum = orderSum;
		super._setFieldValue(Purchase.JField.orderSum, orderSum);
    }
    public Long getOrderSum(){
	    return this.orderSum;
    }
	
    public void setOrderExpressFee(Long orderExpressFee){
	    this.orderExpressFee = orderExpressFee;
		super._setFieldValue(Purchase.JField.orderExpressFee, orderExpressFee);
    }
    public Long getOrderExpressFee(){
	    return this.orderExpressFee;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(Purchase.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(Purchase.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(Purchase.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(Purchase.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        sum("SUM","BIGINT",19,Long.class,true),
        prices("PRICES","VARCHAR",2000,String.class,true),
        expressFee("EXPRESS_FEE","BIGINT",19,Long.class,true),
        orderCount("ORDER_COUNT","INT",10,Integer.class,true),
        orderSum("ORDER_SUM","BIGINT",19,Long.class,true),
        orderExpressFee("ORDER_EXPRESS_FEE","BIGINT",19,Long.class,true),
        status("STATUS","TINYINT",3,Short.class,false),
        memo("MEMO","VARCHAR",1000,String.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,false),
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
            return "PURCHASE";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.purchase.Purchase.class;
        }
    }
	

}