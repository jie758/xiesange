package com.xiesange.gen.dbentity.activity;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "ACTIVITY_JOIN",primaryKey="ID",indexes="")
public class ActivityJoin extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long activityId;
    private Short type;
    private Long userId;
    private String openid;
    private String mobile;
    private String name;
    private String ext;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(ActivityJoin.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setActivityId(Long activityId){
	    this.activityId = activityId;
		super._setFieldValue(ActivityJoin.JField.activityId, activityId);
    }
    public Long getActivityId(){
	    return this.activityId;
    }
	
    public void setType(Short type){
	    this.type = type;
		super._setFieldValue(ActivityJoin.JField.type, type);
    }
    public Short getType(){
	    return this.type;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(ActivityJoin.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setOpenid(String openid){
	    this.openid = openid;
		super._setFieldValue(ActivityJoin.JField.openid, openid);
    }
    public String getOpenid(){
	    return this.openid;
    }
	
    public void setMobile(String mobile){
	    this.mobile = mobile;
		super._setFieldValue(ActivityJoin.JField.mobile, mobile);
    }
    public String getMobile(){
	    return this.mobile;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(ActivityJoin.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setExt(String ext){
	    this.ext = ext;
		super._setFieldValue(ActivityJoin.JField.ext, ext);
    }
    public String getExt(){
	    return this.ext;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(ActivityJoin.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(ActivityJoin.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        activityId("ACTIVITY_ID","BIGINT",19,Long.class,true),
        type("TYPE","TINYINT",3,Short.class,true),
        userId("USER_ID","BIGINT",19,Long.class,true),
        openid("OPENID","VARCHAR",50,String.class,true),
        mobile("MOBILE","VARCHAR",16,String.class,true),
        name("NAME","TEXT",65535,String.class,true),
        ext("EXT","VARCHAR",128,String.class,true),
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
            return "ACTIVITY_JOIN";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.activity.ActivityJoin.class;
        }
    }
	

}