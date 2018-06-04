package com.xiesange.gen.dbentity.trip;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "TRIP_COMMENT",primaryKey="",indexes="")
public class TripComment extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long orderId;
    private Long tripId;
    private String pic;
    private String name;
    private String comment;
    private Integer grade;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(TripComment.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setOrderId(Long orderId){
	    this.orderId = orderId;
		super._setFieldValue(TripComment.JField.orderId, orderId);
    }
    public Long getOrderId(){
	    return this.orderId;
    }
	
    public void setTripId(Long tripId){
	    this.tripId = tripId;
		super._setFieldValue(TripComment.JField.tripId, tripId);
    }
    public Long getTripId(){
	    return this.tripId;
    }
	
    public void setPic(String pic){
	    this.pic = pic;
		super._setFieldValue(TripComment.JField.pic, pic);
    }
    public String getPic(){
	    return this.pic;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(TripComment.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setComment(String comment){
	    this.comment = comment;
		super._setFieldValue(TripComment.JField.comment, comment);
    }
    public String getComment(){
	    return this.comment;
    }
	
    public void setGrade(Integer grade){
	    this.grade = grade;
		super._setFieldValue(TripComment.JField.grade, grade);
    }
    public Integer getGrade(){
	    return this.grade;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(TripComment.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(TripComment.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,true),
        orderId("ORDER_ID","BIGINT",19,Long.class,true),
        tripId("TRIP_ID","BIGINT",19,Long.class,true),
        pic("PIC","VARCHAR",256,String.class,true),
        name("NAME","VARCHAR",50,String.class,true),
        comment("COMMENT","VARCHAR",500,String.class,true),
        grade("GRADE","INT",10,Integer.class,true),
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
            return "TRIP_COMMENT";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.trip.TripComment.class;
        }
    }
	

}