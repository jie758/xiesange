package com.xiesange.gen.dbentity.base;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "BASE_ARTICLE",primaryKey="ID",indexes="")
public class BaseArticle extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String code;
    private String name;
    private String content;
    private String shareMessageTitle;
    private String shareMessageDesc;
    private String shareTimelineDesc;
    private String sharePic;
    private Short isSystem;
    private Integer viewCount;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(BaseArticle.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(BaseArticle.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(BaseArticle.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setContent(String content){
	    this.content = content;
		super._setFieldValue(BaseArticle.JField.content, content);
    }
    public String getContent(){
	    return this.content;
    }
	
    public void setShareMessageTitle(String shareMessageTitle){
	    this.shareMessageTitle = shareMessageTitle;
		super._setFieldValue(BaseArticle.JField.shareMessageTitle, shareMessageTitle);
    }
    public String getShareMessageTitle(){
	    return this.shareMessageTitle;
    }
	
    public void setShareMessageDesc(String shareMessageDesc){
	    this.shareMessageDesc = shareMessageDesc;
		super._setFieldValue(BaseArticle.JField.shareMessageDesc, shareMessageDesc);
    }
    public String getShareMessageDesc(){
	    return this.shareMessageDesc;
    }
	
    public void setShareTimelineDesc(String shareTimelineDesc){
	    this.shareTimelineDesc = shareTimelineDesc;
		super._setFieldValue(BaseArticle.JField.shareTimelineDesc, shareTimelineDesc);
    }
    public String getShareTimelineDesc(){
	    return this.shareTimelineDesc;
    }
	
    public void setSharePic(String sharePic){
	    this.sharePic = sharePic;
		super._setFieldValue(BaseArticle.JField.sharePic, sharePic);
    }
    public String getSharePic(){
	    return this.sharePic;
    }
	
    public void setIsSystem(Short isSystem){
	    this.isSystem = isSystem;
		super._setFieldValue(BaseArticle.JField.isSystem, isSystem);
    }
    public Short getIsSystem(){
	    return this.isSystem;
    }
	
    public void setViewCount(Integer viewCount){
	    this.viewCount = viewCount;
		super._setFieldValue(BaseArticle.JField.viewCount, viewCount);
    }
    public Integer getViewCount(){
	    return this.viewCount;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(BaseArticle.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(BaseArticle.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(BaseArticle.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        code("CODE","VARCHAR",64,String.class,false),
        name("NAME","VARCHAR",128,String.class,true),
        content("CONTENT","TEXT",65535,String.class,true),
        shareMessageTitle("SHARE_MESSAGE_TITLE","VARCHAR",256,String.class,true),
        shareMessageDesc("SHARE_MESSAGE_DESC","VARCHAR",512,String.class,true),
        shareTimelineDesc("SHARE_TIMELINE_DESC","VARCHAR",512,String.class,true),
        sharePic("SHARE_PIC","VARCHAR",1000,String.class,true),
        isSystem("IS_SYSTEM","TINYINT",3,Short.class,true),
        viewCount("VIEW_COUNT","INT",10,Integer.class,true),
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
            return "BASE_ARTICLE";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.base.BaseArticle.class;
        }
    }
	

}