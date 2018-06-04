package com.xiesange.gen.dbentity.shanghui;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "SHANGHUI",primaryKey="ID",indexes="")
public class Shanghui extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private java.util.Date date;
    private Integer price;
    private String type;
    private Short isOnline;
    private String members;
    private String score;
    private Integer shui;
    private Integer sum;
    private Long onlineRoomid;
    private Short status;
    private java.util.Date finishTime;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(Shanghui.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setDate(java.util.Date date){
	    this.date = date;
		super._setFieldValue(Shanghui.JField.date, date);
    }
    public java.util.Date getDate(){
	    return this.date;
    }
	
    public void setPrice(Integer price){
	    this.price = price;
		super._setFieldValue(Shanghui.JField.price, price);
    }
    public Integer getPrice(){
	    return this.price;
    }
	
    public void setType(String type){
	    this.type = type;
		super._setFieldValue(Shanghui.JField.type, type);
    }
    public String getType(){
	    return this.type;
    }
	
    public void setIsOnline(Short isOnline){
	    this.isOnline = isOnline;
		super._setFieldValue(Shanghui.JField.isOnline, isOnline);
    }
    public Short getIsOnline(){
	    return this.isOnline;
    }
	
    public void setMembers(String members){
	    this.members = members;
		super._setFieldValue(Shanghui.JField.members, members);
    }
    public String getMembers(){
	    return this.members;
    }
	
    public void setScore(String score){
	    this.score = score;
		super._setFieldValue(Shanghui.JField.score, score);
    }
    public String getScore(){
	    return this.score;
    }
	
    public void setShui(Integer shui){
	    this.shui = shui;
		super._setFieldValue(Shanghui.JField.shui, shui);
    }
    public Integer getShui(){
	    return this.shui;
    }
	
    public void setSum(Integer sum){
	    this.sum = sum;
		super._setFieldValue(Shanghui.JField.sum, sum);
    }
    public Integer getSum(){
	    return this.sum;
    }
	
    public void setOnlineRoomid(Long onlineRoomid){
	    this.onlineRoomid = onlineRoomid;
		super._setFieldValue(Shanghui.JField.onlineRoomid, onlineRoomid);
    }
    public Long getOnlineRoomid(){
	    return this.onlineRoomid;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(Shanghui.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setFinishTime(java.util.Date finishTime){
	    this.finishTime = finishTime;
		super._setFieldValue(Shanghui.JField.finishTime, finishTime);
    }
    public java.util.Date getFinishTime(){
	    return this.finishTime;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(Shanghui.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(Shanghui.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        date("DATE","DATE",10,java.util.Date.class,true),
        price("PRICE","INT",10,Integer.class,true),
        type("TYPE","VARCHAR",50,String.class,true),
        isOnline("IS_ONLINE","TINYINT",3,Short.class,true),
        members("MEMBERS","VARCHAR",64,String.class,true),
        score("SCORE","VARCHAR",64,String.class,true),
        shui("SHUI","INT",10,Integer.class,true),
        sum("SUM","INT",10,Integer.class,true),
        onlineRoomid("ONLINE_ROOMID","BIGINT",19,Long.class,true),
        status("STATUS","TINYINT",3,Short.class,true),
        finishTime("FINISH_TIME","DATETIME",19,java.util.Date.class,true),
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
            return "SHANGHUI";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.shanghui.Shanghui.class;
        }
    }
	

}