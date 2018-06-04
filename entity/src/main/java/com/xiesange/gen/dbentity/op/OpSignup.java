package com.xiesange.gen.dbentity.op;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "OP_SIGNUP",primaryKey="ID",indexes="OPENID(OPENID)")
public class OpSignup extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long opId;
    private String openid;
    private String nickname;
    private Short sex;
    private String pic;
    private String mobile;
    private String address;
    private String ext1;
    private String ext2;
    private String ext3;
    private Integer voteCount;
    private Integer viewCount;
    private Integer sharingCount;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(OpSignup.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setOpId(Long opId){
	    this.opId = opId;
		super._setFieldValue(OpSignup.JField.opId, opId);
    }
    public Long getOpId(){
	    return this.opId;
    }
	
    public void setOpenid(String openid){
	    this.openid = openid;
		super._setFieldValue(OpSignup.JField.openid, openid);
    }
    public String getOpenid(){
	    return this.openid;
    }
	
    public void setNickname(String nickname){
	    this.nickname = nickname;
		super._setFieldValue(OpSignup.JField.nickname, nickname);
    }
    public String getNickname(){
	    return this.nickname;
    }
	
    public void setSex(Short sex){
	    this.sex = sex;
		super._setFieldValue(OpSignup.JField.sex, sex);
    }
    public Short getSex(){
	    return this.sex;
    }
	
    public void setPic(String pic){
	    this.pic = pic;
		super._setFieldValue(OpSignup.JField.pic, pic);
    }
    public String getPic(){
	    return this.pic;
    }
	
    public void setMobile(String mobile){
	    this.mobile = mobile;
		super._setFieldValue(OpSignup.JField.mobile, mobile);
    }
    public String getMobile(){
	    return this.mobile;
    }
	
    public void setAddress(String address){
	    this.address = address;
		super._setFieldValue(OpSignup.JField.address, address);
    }
    public String getAddress(){
	    return this.address;
    }
	
    public void setExt1(String ext1){
	    this.ext1 = ext1;
		super._setFieldValue(OpSignup.JField.ext1, ext1);
    }
    public String getExt1(){
	    return this.ext1;
    }
	
    public void setExt2(String ext2){
	    this.ext2 = ext2;
		super._setFieldValue(OpSignup.JField.ext2, ext2);
    }
    public String getExt2(){
	    return this.ext2;
    }
	
    public void setExt3(String ext3){
	    this.ext3 = ext3;
		super._setFieldValue(OpSignup.JField.ext3, ext3);
    }
    public String getExt3(){
	    return this.ext3;
    }
	
    public void setVoteCount(Integer voteCount){
	    this.voteCount = voteCount;
		super._setFieldValue(OpSignup.JField.voteCount, voteCount);
    }
    public Integer getVoteCount(){
	    return this.voteCount;
    }
	
    public void setViewCount(Integer viewCount){
	    this.viewCount = viewCount;
		super._setFieldValue(OpSignup.JField.viewCount, viewCount);
    }
    public Integer getViewCount(){
	    return this.viewCount;
    }
	
    public void setSharingCount(Integer sharingCount){
	    this.sharingCount = sharingCount;
		super._setFieldValue(OpSignup.JField.sharingCount, sharingCount);
    }
    public Integer getSharingCount(){
	    return this.sharingCount;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(OpSignup.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(OpSignup.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        opId("OP_ID","BIGINT",19,Long.class,false),
        openid("OPENID","VARCHAR",50,String.class,true),
        nickname("NICKNAME","TEXT",65535,String.class,true),
        sex("SEX","TINYINT",3,Short.class,true),
        pic("PIC","VARCHAR",256,String.class,true),
        mobile("MOBILE","VARCHAR",16,String.class,true),
        address("ADDRESS","VARCHAR",512,String.class,true),
        ext1("EXT1","VARCHAR",512,String.class,true),
        ext2("EXT2","VARCHAR",512,String.class,true),
        ext3("EXT3","VARCHAR",512,String.class,true),
        voteCount("VOTE_COUNT","INT",10,Integer.class,true),
        viewCount("VIEW_COUNT","INT",10,Integer.class,true),
        sharingCount("SHARING_COUNT","INT",10,Integer.class,true),
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
            return "OP_SIGNUP";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.op.OpSignup.class;
        }
    }
	

}