package com.xiesange.gen.dbentity.shanghui;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "SHANGHUI_ROUND",primaryKey="ID",indexes="")
public class ShanghuiRound extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private Long shanghuiId;
    private String score;
    private java.util.Date createTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(ShanghuiRound.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setShanghuiId(Long shanghuiId){
	    this.shanghuiId = shanghuiId;
		super._setFieldValue(ShanghuiRound.JField.shanghuiId, shanghuiId);
    }
    public Long getShanghuiId(){
	    return this.shanghuiId;
    }
	
    public void setScore(String score){
	    this.score = score;
		super._setFieldValue(ShanghuiRound.JField.score, score);
    }
    public String getScore(){
	    return this.score;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(ShanghuiRound.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(ShanghuiRound.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        shanghuiId("SHANGHUI_ID","BIGINT",19,Long.class,false),
        score("SCORE","VARCHAR",128,String.class,true),
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
            return "SHANGHUI_ROUND";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.shanghui.ShanghuiRound.class;
        }
    }
	

}