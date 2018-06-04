package com.xiesange.gen.dbentity.product;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "PRODUCT",primaryKey="ID",indexes="CODE(NAME)")
public class Product extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String name;
    private String pname;
    private Long typeId;
    private String summary;
    private String spec;
    private Long price;
    private Long origPrice;
    private Long costPrice;
    private String unit;
    private String commentTags;
    private Integer premise;
    private String pic;
    private Integer orderCount;
    private Short status;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(Product.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(Product.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setPname(String pname){
	    this.pname = pname;
		super._setFieldValue(Product.JField.pname, pname);
    }
    public String getPname(){
	    return this.pname;
    }
	
    public void setTypeId(Long typeId){
	    this.typeId = typeId;
		super._setFieldValue(Product.JField.typeId, typeId);
    }
    public Long getTypeId(){
	    return this.typeId;
    }
	
    public void setSummary(String summary){
	    this.summary = summary;
		super._setFieldValue(Product.JField.summary, summary);
    }
    public String getSummary(){
	    return this.summary;
    }
	
    public void setSpec(String spec){
	    this.spec = spec;
		super._setFieldValue(Product.JField.spec, spec);
    }
    public String getSpec(){
	    return this.spec;
    }
	
    public void setPrice(Long price){
	    this.price = price;
		super._setFieldValue(Product.JField.price, price);
    }
    public Long getPrice(){
	    return this.price;
    }
	
    public void setOrigPrice(Long origPrice){
	    this.origPrice = origPrice;
		super._setFieldValue(Product.JField.origPrice, origPrice);
    }
    public Long getOrigPrice(){
	    return this.origPrice;
    }
	
    public void setCostPrice(Long costPrice){
	    this.costPrice = costPrice;
		super._setFieldValue(Product.JField.costPrice, costPrice);
    }
    public Long getCostPrice(){
	    return this.costPrice;
    }
	
    public void setUnit(String unit){
	    this.unit = unit;
		super._setFieldValue(Product.JField.unit, unit);
    }
    public String getUnit(){
	    return this.unit;
    }
	
    public void setCommentTags(String commentTags){
	    this.commentTags = commentTags;
		super._setFieldValue(Product.JField.commentTags, commentTags);
    }
    public String getCommentTags(){
	    return this.commentTags;
    }
	
    public void setPremise(Integer premise){
	    this.premise = premise;
		super._setFieldValue(Product.JField.premise, premise);
    }
    public Integer getPremise(){
	    return this.premise;
    }
	
    public void setPic(String pic){
	    this.pic = pic;
		super._setFieldValue(Product.JField.pic, pic);
    }
    public String getPic(){
	    return this.pic;
    }
	
    public void setOrderCount(Integer orderCount){
	    this.orderCount = orderCount;
		super._setFieldValue(Product.JField.orderCount, orderCount);
    }
    public Integer getOrderCount(){
	    return this.orderCount;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(Product.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(Product.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(Product.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(Product.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        name("NAME","VARCHAR",64,String.class,true),
        pname("PNAME","VARCHAR",64,String.class,true),
        typeId("TYPE_ID","BIGINT",19,Long.class,true),
        summary("SUMMARY","VARCHAR",128,String.class,true),
        spec("SPEC","VARCHAR",32,String.class,true),
        price("PRICE","BIGINT",19,Long.class,true),
        origPrice("ORIG_PRICE","BIGINT",19,Long.class,true),
        costPrice("COST_PRICE","BIGINT",19,Long.class,true),
        unit("UNIT","VARCHAR",16,String.class,true),
        commentTags("COMMENT_TAGS","VARCHAR",1000,String.class,true),
        premise("PREMISE","INT",10,Integer.class,true),
        pic("PIC","VARCHAR",512,String.class,true),
        orderCount("ORDER_COUNT","INT",10,Integer.class,true),
        status("STATUS","TINYINT",3,Short.class,true),
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
            return "PRODUCT";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.product.Product.class;
        }
    }
	

}