package com.xiesange.gen.dbentity.product;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "PRODUCT_ITEM",primaryKey="ID",indexes="")
public class ProductItem extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long productId;
    private Long itemId;
    private Integer amount;
    private String memo;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(ProductItem.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setProductId(Long productId){
	    this.productId = productId;
		super._setFieldValue(ProductItem.JField.productId, productId);
    }
    public Long getProductId(){
	    return this.productId;
    }
	
    public void setItemId(Long itemId){
	    this.itemId = itemId;
		super._setFieldValue(ProductItem.JField.itemId, itemId);
    }
    public Long getItemId(){
	    return this.itemId;
    }
	
    public void setAmount(Integer amount){
	    this.amount = amount;
		super._setFieldValue(ProductItem.JField.amount, amount);
    }
    public Integer getAmount(){
	    return this.amount;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(ProductItem.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(ProductItem.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(ProductItem.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(ProductItem.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        productId("PRODUCT_ID","BIGINT",19,Long.class,true),
        itemId("ITEM_ID","BIGINT",19,Long.class,true),
        amount("AMOUNT","INT",10,Integer.class,true),
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
            return "PRODUCT_ITEM";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.product.ProductItem.class;
        }
    }
	

}