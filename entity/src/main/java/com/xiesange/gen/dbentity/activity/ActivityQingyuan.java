package com.xiesange.gen.dbentity.activity;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "ACTIVITY_QINGYUAN",primaryKey="ID",indexes="TYPE(CODE)")
public class ActivityQingyuan extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String code;
    private String building;
    private String unit;
    private String room;
    private String signup;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(ActivityQingyuan.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(ActivityQingyuan.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setBuilding(String building){
	    this.building = building;
		super._setFieldValue(ActivityQingyuan.JField.building, building);
    }
    public String getBuilding(){
	    return this.building;
    }
	
    public void setUnit(String unit){
	    this.unit = unit;
		super._setFieldValue(ActivityQingyuan.JField.unit, unit);
    }
    public String getUnit(){
	    return this.unit;
    }
	
    public void setRoom(String room){
	    this.room = room;
		super._setFieldValue(ActivityQingyuan.JField.room, room);
    }
    public String getRoom(){
	    return this.room;
    }
	
    public void setSignup(String signup){
	    this.signup = signup;
		super._setFieldValue(ActivityQingyuan.JField.signup, signup);
    }
    public String getSignup(){
	    return this.signup;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(ActivityQingyuan.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(ActivityQingyuan.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        code("CODE","VARCHAR",50,String.class,true),
        building("BUILDING","VARCHAR",50,String.class,true),
        unit("UNIT","VARCHAR",50,String.class,true),
        room("ROOM","VARCHAR",50,String.class,true),
        signup("SIGNUP","VARCHAR",2000,String.class,true),
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
            return "ACTIVITY_QINGYUAN";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.activity.ActivityQingyuan.class;
        }
    }
	

}