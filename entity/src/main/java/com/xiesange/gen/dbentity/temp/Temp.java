package com.xiesange.gen.dbentity.temp;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "TEMP",primaryKey="",indexes="")
public class Temp extends DBEntity implements net.sf.cglib.proxy.Factory{
    private String nickname;
    private String pic;

    public void setNickname(String nickname){
	    this.nickname = nickname;
		super._setFieldValue(Temp.JField.nickname, nickname);
    }
    public String getNickname(){
	    return this.nickname;
    }
	
    public void setPic(String pic){
	    this.pic = pic;
		super._setFieldValue(Temp.JField.pic, pic);
    }
    public String getPic(){
	    return this.pic;
    }
	


public enum JField implements BaseJField{
        nickname("NICKNAME","VARCHAR",64,String.class,true),
        pic("PIC","VARCHAR",512,String.class,true);
        
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
            return "TEMP";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.temp.Temp.class;
        }
    }



@Override
public Long getId() {
	// TODO Auto-generated method stub
	return null;
}
@Override
public void setId(Long id) {
	// TODO Auto-generated method stub
	
}
	

}