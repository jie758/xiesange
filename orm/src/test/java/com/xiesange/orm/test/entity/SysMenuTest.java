package com.xiesange.orm.test.entity;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.statement.field.BaseJField;

@DBTableAnno(name = "BASE_MENU",primaryKey="ID",indexes="Index 3(LAY_NO);PARENT_MENU_ID(PARENT_ID,NAME)")
public class SysMenuTest extends DBEntity{
    private Long id;
    private String name;
    private Long parentId;
    private String layStr;
    private Integer layNo;
    private String url;
    private Short isLeaf;
    private String memo;
    private Long tenantId;
    private Long sn;
    private Short isConfigable;
    private String icon;
    private String pinyin;
    private String pinyinHeader;
    private java.util.Date createDate;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(SysMenuTest.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(SysMenuTest.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setParentId(Long parentId){
	    this.parentId = parentId;
		super._setFieldValue(SysMenuTest.JField.parentId, parentId);
    }
    public Long getParentId(){
	    return this.parentId;
    }
	
    public void setLayStr(String layStr){
	    this.layStr = layStr;
		super._setFieldValue(SysMenuTest.JField.layStr, layStr);
    }
    public String getLayStr(){
	    return this.layStr;
    }
	
    public void setLayNo(Integer layNo){
	    this.layNo = layNo;
		super._setFieldValue(SysMenuTest.JField.layNo, layNo);
    }
    public Integer getLayNo(){
	    return this.layNo;
    }
	
    public void setUrl(String url){
	    this.url = url;
		super._setFieldValue(SysMenuTest.JField.url, url);
    }
    public String getUrl(){
	    return this.url;
    }
	
    public void setIsLeaf(Short isLeaf){
	    this.isLeaf = isLeaf;
		super._setFieldValue(SysMenuTest.JField.isLeaf, isLeaf);
    }
    public Short getIsLeaf(){
	    return this.isLeaf;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(SysMenuTest.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setTenantId(Long tenantId){
	    this.tenantId = tenantId;
		super._setFieldValue(SysMenuTest.JField.tenantId, tenantId);
    }
    public Long getTenantId(){
	    return this.tenantId;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(SysMenuTest.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	
    public void setIsConfigable(Short isConfigable){
	    this.isConfigable = isConfigable;
		super._setFieldValue(SysMenuTest.JField.isConfigable, isConfigable);
    }
    public Short getIsConfigable(){
	    return this.isConfigable;
    }
	
    public void setIcon(String icon){
	    this.icon = icon;
		super._setFieldValue(SysMenuTest.JField.icon, icon);
    }
    public String getIcon(){
	    return this.icon;
    }
	
    public void setPinyin(String pinyin){
	    this.pinyin = pinyin;
		super._setFieldValue(SysMenuTest.JField.pinyin, pinyin);
    }
    public String getPinyin(){
	    return this.pinyin;
    }
	
    public void setPinyinHeader(String pinyinHeader){
	    this.pinyinHeader = pinyinHeader;
		super._setFieldValue(SysMenuTest.JField.pinyinHeader, pinyinHeader);
    }
    public String getPinyinHeader(){
	    return this.pinyinHeader;
    }
	
    public void setCreateDate(java.util.Date createDate){
	    this.createDate = createDate;
		super._setFieldValue(SysMenuTest.JField.createDate, createDate);
    }
    public java.util.Date getCreateDate(){
	    return this.createDate;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        name("NAME","VARCHAR",64,String.class,false),
        parentId("PARENT_ID","BIGINT",19,Long.class,false),
        layStr("LAY_STR","VARCHAR",512,String.class,false),
        layNo("LAY_NO","INT",10,Integer.class,false),
        url("URL","VARCHAR",256,String.class,true),
        isLeaf("IS_LEAF","TINYINT",3,Short.class,false),
        memo("MEMO","VARCHAR",1000,String.class,true),
        tenantId("TENANT_ID","BIGINT",19,Long.class,false),
        sn("SN","BIGINT",19,Long.class,true),
        isConfigable("IS_CONFIGABLE","TINYINT",3,Short.class,true),
        icon("ICON","VARCHAR",128,String.class,true),
        pinyin("PINYIN","VARCHAR",256,String.class,true),
        pinyinHeader("PINYIN_HEADER","VARCHAR",256,String.class,true),
        createDate("CREATE_DATE","DATETIME",19,java.util.Date.class,false);
        
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
            return "BASE_MENU";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return SysMenuTest.class;
        }
    }
	

}