package com.xiesange.gen.dbentity.wx;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "WX_FANS",primaryKey="ID",indexes="")
public class WxFans extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String openid;
    private String unionId;
    private String nickname;
    private String pic;
    private Short sex;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(WxFans.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setOpenid(String openid){
	    this.openid = openid;
		super._setFieldValue(WxFans.JField.openid, openid);
    }
    public String getOpenid(){
	    return this.openid;
    }
	
    public void setUnionId(String unionId){
	    this.unionId = unionId;
		super._setFieldValue(WxFans.JField.unionId, unionId);
    }
    public String getUnionId(){
	    return this.unionId;
    }
	
    public void setNickname(String nickname){
	    this.nickname = nickname;
		super._setFieldValue(WxFans.JField.nickname, nickname);
    }
    public String getNickname(){
	    return this.nickname;
    }
	
    public void setPic(String pic){
	    this.pic = pic;
		super._setFieldValue(WxFans.JField.pic, pic);
    }
    public String getPic(){
	    return this.pic;
    }
	
    public void setSex(Short sex){
	    this.sex = sex;
		super._setFieldValue(WxFans.JField.sex, sex);
    }
    public Short getSex(){
	    return this.sex;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(WxFans.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(WxFans.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(WxFans.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        openid("OPENID","VARCHAR",256,String.class,true),
        unionId("UNION_ID","VARCHAR",256,String.class,true),
        nickname("NICKNAME","TEXT",65535,String.class,true),
        pic("PIC","VARCHAR",256,String.class,true),
        sex("SEX","TINYINT",3,Short.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,true),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,true),
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
            return "WX_FANS";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.wx.WxFans.class;
        }
    }
	

}