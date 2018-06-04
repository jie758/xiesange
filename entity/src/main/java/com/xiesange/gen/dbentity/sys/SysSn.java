package com.xiesange.gen.dbentity.sys;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "SYS_SN",primaryKey="ID",indexes="")
public class SysSn extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long userId;
    private String url;
    private Short sysType;
    private Short channel;
    private Short isWifi;
    private Long loginId;
    private String deviceType;
    private String agentType;
    private String appVersion;
    private String lang;
    private String input;
    private String output;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private String respCode;
    private String respMsg;
    private String serverIp;
    private Long cost;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(SysSn.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(SysSn.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setUrl(String url){
	    this.url = url;
		super._setFieldValue(SysSn.JField.url, url);
    }
    public String getUrl(){
	    return this.url;
    }
	
    public void setSysType(Short sysType){
	    this.sysType = sysType;
		super._setFieldValue(SysSn.JField.sysType, sysType);
    }
    public Short getSysType(){
	    return this.sysType;
    }
	
    public void setChannel(Short channel){
	    this.channel = channel;
		super._setFieldValue(SysSn.JField.channel, channel);
    }
    public Short getChannel(){
	    return this.channel;
    }
	
    public void setIsWifi(Short isWifi){
	    this.isWifi = isWifi;
		super._setFieldValue(SysSn.JField.isWifi, isWifi);
    }
    public Short getIsWifi(){
	    return this.isWifi;
    }
	
    public void setLoginId(Long loginId){
	    this.loginId = loginId;
		super._setFieldValue(SysSn.JField.loginId, loginId);
    }
    public Long getLoginId(){
	    return this.loginId;
    }
	
    public void setDeviceType(String deviceType){
	    this.deviceType = deviceType;
		super._setFieldValue(SysSn.JField.deviceType, deviceType);
    }
    public String getDeviceType(){
	    return this.deviceType;
    }
	
    public void setAgentType(String agentType){
	    this.agentType = agentType;
		super._setFieldValue(SysSn.JField.agentType, agentType);
    }
    public String getAgentType(){
	    return this.agentType;
    }
	
    public void setAppVersion(String appVersion){
	    this.appVersion = appVersion;
		super._setFieldValue(SysSn.JField.appVersion, appVersion);
    }
    public String getAppVersion(){
	    return this.appVersion;
    }
	
    public void setLang(String lang){
	    this.lang = lang;
		super._setFieldValue(SysSn.JField.lang, lang);
    }
    public String getLang(){
	    return this.lang;
    }
	
    public void setInput(String input){
	    this.input = input;
		super._setFieldValue(SysSn.JField.input, input);
    }
    public String getInput(){
	    return this.input;
    }
	
    public void setOutput(String output){
	    this.output = output;
		super._setFieldValue(SysSn.JField.output, output);
    }
    public String getOutput(){
	    return this.output;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(SysSn.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(SysSn.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setRespCode(String respCode){
	    this.respCode = respCode;
		super._setFieldValue(SysSn.JField.respCode, respCode);
    }
    public String getRespCode(){
	    return this.respCode;
    }
	
    public void setRespMsg(String respMsg){
	    this.respMsg = respMsg;
		super._setFieldValue(SysSn.JField.respMsg, respMsg);
    }
    public String getRespMsg(){
	    return this.respMsg;
    }
	
    public void setServerIp(String serverIp){
	    this.serverIp = serverIp;
		super._setFieldValue(SysSn.JField.serverIp, serverIp);
    }
    public String getServerIp(){
	    return this.serverIp;
    }
	
    public void setCost(Long cost){
	    this.cost = cost;
		super._setFieldValue(SysSn.JField.cost, cost);
    }
    public Long getCost(){
	    return this.cost;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        userId("USER_ID","BIGINT",19,Long.class,true),
        url("URL","VARCHAR",256,String.class,false),
        sysType("SYS_TYPE","TINYINT",3,Short.class,false),
        channel("CHANNEL","TINYINT",3,Short.class,true),
        isWifi("IS_WIFI","TINYINT",3,Short.class,true),
        loginId("LOGIN_ID","BIGINT",19,Long.class,true),
        deviceType("DEVICE_TYPE","VARCHAR",64,String.class,true),
        agentType("AGENT_TYPE","VARCHAR",64,String.class,true),
        appVersion("APP_VERSION","VARCHAR",16,String.class,true),
        lang("LANG","VARCHAR",16,String.class,true),
        input("INPUT","VARCHAR",1000,String.class,true),
        output("OUTPUT","VARCHAR",1000,String.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,false),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,false),
        respCode("RESP_CODE","VARCHAR",32,String.class,true),
        respMsg("RESP_MSG","VARCHAR",1000,String.class,true),
        serverIp("SERVER_IP","VARCHAR",50,String.class,true),
        cost("COST","BIGINT",19,Long.class,true);
        
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
            return "SYS_SN";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.sys.SysSn.class;
        }
    }
	

}