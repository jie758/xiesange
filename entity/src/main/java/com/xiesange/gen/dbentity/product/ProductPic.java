package com.xiesange.gen.dbentity.product;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "PRODUCT_PIC",primaryKey="ID",indexes="")
public class ProductPic extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long productId;
    private String pic;
    private String text;
    private Integer width;
    private Integer height;
    private Integer rate;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(ProductPic.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setProductId(Long productId){
	    this.productId = productId;
		super._setFieldValue(ProductPic.JField.productId, productId);
    }
    public Long getProductId(){
	    return this.productId;
    }
	
    public void setPic(String pic){
	    this.pic = pic;
		super._setFieldValue(ProductPic.JField.pic, pic);
    }
    public String getPic(){
	    return this.pic;
    }
	
    public void setText(String text){
	    this.text = text;
		super._setFieldValue(ProductPic.JField.text, text);
    }
    public String getText(){
	    return this.text;
    }
	
    public void setWidth(Integer width){
	    this.width = width;
		super._setFieldValue(ProductPic.JField.width, width);
    }
    public Integer getWidth(){
	    return this.width;
    }
	
    public void setHeight(Integer height){
	    this.height = height;
		super._setFieldValue(ProductPic.JField.height, height);
    }
    public Integer getHeight(){
	    return this.height;
    }
	
    public void setRate(Integer rate){
	    this.rate = rate;
		super._setFieldValue(ProductPic.JField.rate, rate);
    }
    public Integer getRate(){
	    return this.rate;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(ProductPic.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(ProductPic.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(ProductPic.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        productId("PRODUCT_ID","BIGINT",19,Long.class,true),
        pic("PIC","VARCHAR",512,String.class,true),
        text("TEXT","TEXT",65535,String.class,true),
        width("WIDTH","INT",10,Integer.class,true),
        height("HEIGHT","INT",10,Integer.class,true),
        rate("RATE","INT",10,Integer.class,true),
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
            return "PRODUCT_PIC";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.product.ProductPic.class;
        }
    }
	

}