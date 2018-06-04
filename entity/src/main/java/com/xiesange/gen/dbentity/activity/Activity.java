package com.xiesange.gen.dbentity.activity;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "ACTIVITY",primaryKey="ID",indexes="TYPE(CODE)")
public class Activity extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Short type;
    private String code;
    private Long name;
    private String bannerPic;
    private Long signupCount;
    private Long voteCount;
    private Long viewCount;
    private java.util.Date expireDate;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(Activity.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setType(Short type){
	    this.type = type;
		super._setFieldValue(Activity.JField.type, type);
    }
    public Short getType(){
	    return this.type;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(Activity.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setName(Long name){
	    this.name = name;
		super._setFieldValue(Activity.JField.name, name);
    }
    public Long getName(){
	    return this.name;
    }
	
    public void setBannerPic(String bannerPic){
	    this.bannerPic = bannerPic;
		super._setFieldValue(Activity.JField.bannerPic, bannerPic);
    }
    public String getBannerPic(){
	    return this.bannerPic;
    }
	
    public void setSignupCount(Long signupCount){
	    this.signupCount = signupCount;
		super._setFieldValue(Activity.JField.signupCount, signupCount);
    }
    public Long getSignupCount(){
	    return this.signupCount;
    }
	
    public void setVoteCount(Long voteCount){
	    this.voteCount = voteCount;
		super._setFieldValue(Activity.JField.voteCount, voteCount);
    }
    public Long getVoteCount(){
	    return this.voteCount;
    }
	
    public void setViewCount(Long viewCount){
	    this.viewCount = viewCount;
		super._setFieldValue(Activity.JField.viewCount, viewCount);
    }
    public Long getViewCount(){
	    return this.viewCount;
    }
	
    public void setExpireDate(java.util.Date expireDate){
	    this.expireDate = expireDate;
		super._setFieldValue(Activity.JField.expireDate, expireDate);
    }
    public java.util.Date getExpireDate(){
	    return this.expireDate;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(Activity.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(Activity.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        type("TYPE","TINYINT",3,Short.class,true),
        code("CODE","VARCHAR",50,String.class,true),
        name("NAME","BIGINT",19,Long.class,true),
        bannerPic("BANNER_PIC","VARCHAR",128,String.class,true),
        signupCount("SIGNUP_COUNT","BIGINT",19,Long.class,true),
        voteCount("VOTE_COUNT","BIGINT",19,Long.class,true),
        viewCount("VIEW_COUNT","BIGINT",19,Long.class,true),
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
            return "ACTIVITY";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.activity.Activity.class;
        }
    }
	

}