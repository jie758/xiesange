package com.xiesange.gen.dbentity.op;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "OP_VOTE",primaryKey="ID",indexes="")
public class OpVote extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long opId;
    private Long signupId;
    private String openid;
    private String mobile;
    private String nickname;
    private Short sex;
    private String pic;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(OpVote.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setOpId(Long opId){
	    this.opId = opId;
		super._setFieldValue(OpVote.JField.opId, opId);
    }
    public Long getOpId(){
	    return this.opId;
    }
	
    public void setSignupId(Long signupId){
	    this.signupId = signupId;
		super._setFieldValue(OpVote.JField.signupId, signupId);
    }
    public Long getSignupId(){
	    return this.signupId;
    }
	
    public void setOpenid(String openid){
	    this.openid = openid;
		super._setFieldValue(OpVote.JField.openid, openid);
    }
    public String getOpenid(){
	    return this.openid;
    }
	
    public void setMobile(String mobile){
	    this.mobile = mobile;
		super._setFieldValue(OpVote.JField.mobile, mobile);
    }
    public String getMobile(){
	    return this.mobile;
    }
	
    public void setNickname(String nickname){
	    this.nickname = nickname;
		super._setFieldValue(OpVote.JField.nickname, nickname);
    }
    public String getNickname(){
	    return this.nickname;
    }
	
    public void setSex(Short sex){
	    this.sex = sex;
		super._setFieldValue(OpVote.JField.sex, sex);
    }
    public Short getSex(){
	    return this.sex;
    }
	
    public void setPic(String pic){
	    this.pic = pic;
		super._setFieldValue(OpVote.JField.pic, pic);
    }
    public String getPic(){
	    return this.pic;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(OpVote.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(OpVote.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        opId("OP_ID","BIGINT",19,Long.class,true),
        signupId("SIGNUP_ID","BIGINT",19,Long.class,true),
        openid("OPENID","VARCHAR",50,String.class,true),
        mobile("MOBILE","VARCHAR",16,String.class,true),
        nickname("NICKNAME","TEXT",65535,String.class,true),
        sex("SEX","TINYINT",3,Short.class,true),
        pic("PIC","VARCHAR",256,String.class,true),
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
            return "OP_VOTE";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.op.OpVote.class;
        }
    }
	

}