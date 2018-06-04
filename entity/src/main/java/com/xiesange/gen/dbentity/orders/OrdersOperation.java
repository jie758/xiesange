package com.xiesange.gen.dbentity.orders;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "ORDERS_OPERATION",primaryKey="ID",indexes="ORDER_ID(ORDER_ID)")
public class OrdersOperation extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long orderId;
    private Short status;
    private String memo;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(OrdersOperation.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setOrderId(Long orderId){
	    this.orderId = orderId;
		super._setFieldValue(OrdersOperation.JField.orderId, orderId);
    }
    public Long getOrderId(){
	    return this.orderId;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(OrdersOperation.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(OrdersOperation.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(OrdersOperation.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(OrdersOperation.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        orderId("ORDER_ID","BIGINT",19,Long.class,true),
        status("STATUS","TINYINT",3,Short.class,true),
        memo("MEMO","VARCHAR",1000,String.class,true),
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
            return "ORDERS_OPERATION";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.orders.OrdersOperation.class;
        }
    }
	

}