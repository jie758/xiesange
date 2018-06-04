package com.xiesange.gen.dbentity.orders;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "ORDERS_COMMENT",primaryKey="ID",indexes="")
public class OrdersComment extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private Long userId;
    private String comment;
    private String tags;
    private Integer grade;
    private Integer stickIndex;
    private Short status;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(OrdersComment.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setOrderId(Long orderId){
	    this.orderId = orderId;
		super._setFieldValue(OrdersComment.JField.orderId, orderId);
    }
    public Long getOrderId(){
	    return this.orderId;
    }
	
    public void setOrderItemId(Long orderItemId){
	    this.orderItemId = orderItemId;
		super._setFieldValue(OrdersComment.JField.orderItemId, orderItemId);
    }
    public Long getOrderItemId(){
	    return this.orderItemId;
    }
	
    public void setProductId(Long productId){
	    this.productId = productId;
		super._setFieldValue(OrdersComment.JField.productId, productId);
    }
    public Long getProductId(){
	    return this.productId;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(OrdersComment.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setComment(String comment){
	    this.comment = comment;
		super._setFieldValue(OrdersComment.JField.comment, comment);
    }
    public String getComment(){
	    return this.comment;
    }
	
    public void setTags(String tags){
	    this.tags = tags;
		super._setFieldValue(OrdersComment.JField.tags, tags);
    }
    public String getTags(){
	    return this.tags;
    }
	
    public void setGrade(Integer grade){
	    this.grade = grade;
		super._setFieldValue(OrdersComment.JField.grade, grade);
    }
    public Integer getGrade(){
	    return this.grade;
    }
	
    public void setStickIndex(Integer stickIndex){
	    this.stickIndex = stickIndex;
		super._setFieldValue(OrdersComment.JField.stickIndex, stickIndex);
    }
    public Integer getStickIndex(){
	    return this.stickIndex;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(OrdersComment.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(OrdersComment.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(OrdersComment.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        orderId("ORDER_ID","BIGINT",19,Long.class,true),
        orderItemId("ORDER_ITEM_ID","BIGINT",19,Long.class,true),
        productId("PRODUCT_ID","BIGINT",19,Long.class,true),
        userId("USER_ID","BIGINT",19,Long.class,true),
        comment("COMMENT","TEXT",65535,String.class,true),
        tags("TAGS","VARCHAR",1000,String.class,true),
        grade("GRADE","INT",10,Integer.class,true),
        stickIndex("STICK_INDEX","INT",10,Integer.class,true),
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
            return "ORDERS_COMMENT";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.orders.OrdersComment.class;
        }
    }
	

}