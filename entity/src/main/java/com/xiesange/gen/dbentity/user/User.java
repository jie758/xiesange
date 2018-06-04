package com.xiesange.gen.dbentity.user;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "USER",primaryKey="ID",indexes="MOBILE(MOBILE);WECHAT(WECHAT)")
public class User extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String name;
    private Short role;
    private Short src;
    private String mobile;
    private String address;
    private Long orderCount;
    private Long orderSum;
    private String device;
    private java.util.Date lastOrderTime;
    private String wechat;
    private String wechatUnionId;
    private Short isSubscribe;
    private String qq;
    private String weibo;
    private String password;
    private String pic;
    private String cityCode;
    private String pyHeader;
    private String py;
    private Short sts;
    private Long sn;
    private java.util.Date activeTime;
    private java.util.Date createTime;
    private java.util.Date updateTime;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(User.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(User.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setRole(Short role){
	    this.role = role;
		super._setFieldValue(User.JField.role, role);
    }
    public Short getRole(){
	    return this.role;
    }
	
    public void setSrc(Short src){
	    this.src = src;
		super._setFieldValue(User.JField.src, src);
    }
    public Short getSrc(){
	    return this.src;
    }
	
    public void setMobile(String mobile){
	    this.mobile = mobile;
		super._setFieldValue(User.JField.mobile, mobile);
    }
    public String getMobile(){
	    return this.mobile;
    }
	
    public void setAddress(String address){
	    this.address = address;
		super._setFieldValue(User.JField.address, address);
    }
    public String getAddress(){
	    return this.address;
    }
	
    public void setOrderCount(Long orderCount){
	    this.orderCount = orderCount;
		super._setFieldValue(User.JField.orderCount, orderCount);
    }
    public Long getOrderCount(){
	    return this.orderCount;
    }
	
    public void setOrderSum(Long orderSum){
	    this.orderSum = orderSum;
		super._setFieldValue(User.JField.orderSum, orderSum);
    }
    public Long getOrderSum(){
	    return this.orderSum;
    }
	
    public void setDevice(String device){
	    this.device = device;
		super._setFieldValue(User.JField.device, device);
    }
    public String getDevice(){
	    return this.device;
    }
	
    public void setLastOrderTime(java.util.Date lastOrderTime){
	    this.lastOrderTime = lastOrderTime;
		super._setFieldValue(User.JField.lastOrderTime, lastOrderTime);
    }
    public java.util.Date getLastOrderTime(){
	    return this.lastOrderTime;
    }
	
    public void setWechat(String wechat){
	    this.wechat = wechat;
		super._setFieldValue(User.JField.wechat, wechat);
    }
    public String getWechat(){
	    return this.wechat;
    }
	
    public void setWechatUnionId(String wechatUnionId){
	    this.wechatUnionId = wechatUnionId;
		super._setFieldValue(User.JField.wechatUnionId, wechatUnionId);
    }
    public String getWechatUnionId(){
	    return this.wechatUnionId;
    }
	
    public void setIsSubscribe(Short isSubscribe){
	    this.isSubscribe = isSubscribe;
		super._setFieldValue(User.JField.isSubscribe, isSubscribe);
    }
    public Short getIsSubscribe(){
	    return this.isSubscribe;
    }
	
    public void setQq(String qq){
	    this.qq = qq;
		super._setFieldValue(User.JField.qq, qq);
    }
    public String getQq(){
	    return this.qq;
    }
	
    public void setWeibo(String weibo){
	    this.weibo = weibo;
		super._setFieldValue(User.JField.weibo, weibo);
    }
    public String getWeibo(){
	    return this.weibo;
    }
	
    public void setPassword(String password){
	    this.password = password;
		super._setFieldValue(User.JField.password, password);
    }
    public String getPassword(){
	    return this.password;
    }
	
    public void setPic(String pic){
	    this.pic = pic;
		super._setFieldValue(User.JField.pic, pic);
    }
    public String getPic(){
	    return this.pic;
    }
	
    public void setCityCode(String cityCode){
	    this.cityCode = cityCode;
		super._setFieldValue(User.JField.cityCode, cityCode);
    }
    public String getCityCode(){
	    return this.cityCode;
    }
	
    public void setPyHeader(String pyHeader){
	    this.pyHeader = pyHeader;
		super._setFieldValue(User.JField.pyHeader, pyHeader);
    }
    public String getPyHeader(){
	    return this.pyHeader;
    }
	
    public void setPy(String py){
	    this.py = py;
		super._setFieldValue(User.JField.py, py);
    }
    public String getPy(){
	    return this.py;
    }
	
    public void setSts(Short sts){
	    this.sts = sts;
		super._setFieldValue(User.JField.sts, sts);
    }
    public Short getSts(){
	    return this.sts;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(User.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	
    public void setActiveTime(java.util.Date activeTime){
	    this.activeTime = activeTime;
		super._setFieldValue(User.JField.activeTime, activeTime);
    }
    public java.util.Date getActiveTime(){
	    return this.activeTime;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(User.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(User.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        name("NAME","TEXT",65535,String.class,true),
        role("ROLE","TINYINT",3,Short.class,true),
        src("SRC","TINYINT",3,Short.class,true),
        mobile("MOBILE","VARCHAR",16,String.class,true),
        address("ADDRESS","VARCHAR",512,String.class,true),
        orderCount("ORDER_COUNT","BIGINT",19,Long.class,true),
        orderSum("ORDER_SUM","BIGINT",19,Long.class,true),
        device("DEVICE","VARCHAR",50,String.class,true),
        lastOrderTime("LAST_ORDER_TIME","DATETIME",19,java.util.Date.class,true),
        wechat("WECHAT","VARCHAR",64,String.class,true),
        wechatUnionId("WECHAT_UNION_ID","VARCHAR",64,String.class,true),
        isSubscribe("IS_SUBSCRIBE","TINYINT",3,Short.class,true),
        qq("QQ","VARCHAR",64,String.class,true),
        weibo("WEIBO","VARCHAR",128,String.class,true),
        password("PASSWORD","VARCHAR",128,String.class,true),
        pic("PIC","VARCHAR",256,String.class,true),
        cityCode("CITY_CODE","VARCHAR",64,String.class,true),
        pyHeader("PY_HEADER","VARCHAR",128,String.class,true),
        py("PY","VARCHAR",128,String.class,true),
        sts("STS","TINYINT",3,Short.class,false),
        sn("SN","BIGINT",19,Long.class,true),
        activeTime("ACTIVE_TIME","DATETIME",19,java.util.Date.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,false),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,false);
        
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
            return "USER";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.user.User.class;
        }
    }
	

}