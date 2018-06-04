package com.xiesange.gen.dbentity.op;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "OP",primaryKey="ID",indexes="")
public class Op extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String code;
    private String name;
    private Integer viewCount;
    private Integer sharingCount;
    private java.util.Date expireDate;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(Op.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(Op.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(Op.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setViewCount(Integer viewCount){
	    this.viewCount = viewCount;
		super._setFieldValue(Op.JField.viewCount, viewCount);
    }
    public Integer getViewCount(){
	    return this.viewCount;
    }
	
    public void setSharingCount(Integer sharingCount){
	    this.sharingCount = sharingCount;
		super._setFieldValue(Op.JField.sharingCount, sharingCount);
    }
    public Integer getSharingCount(){
	    return this.sharingCount;
    }
	
    public void setExpireDate(java.util.Date expireDate){
	    this.expireDate = expireDate;
		super._setFieldValue(Op.JField.expireDate, expireDate);
    }
    public java.util.Date getExpireDate(){
	    return this.expireDate;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(Op.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(Op.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        code("CODE","VARCHAR",16,String.class,true),
        name("NAME","VARCHAR",8,String.class,true),
        viewCount("VIEW_COUNT","INT",10,Integer.class,true),
        sharingCount("SHARING_COUNT","INT",10,Integer.class,true),
        expireDate("EXPIRE_DATE","DATETIME",19,java.util.Date.class,true),
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
            return "OP";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.op.Op.class;
        }
    }
	

}