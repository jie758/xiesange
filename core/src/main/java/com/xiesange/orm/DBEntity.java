package com.xiesange.orm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.statement.field.BaseJField;

public abstract class DBEntity implements Serializable,net.sf.cglib.proxy.Factory{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1476758716882148057L;
	private Map<BaseJField,Object> settedValue = null;
    private Map<String,Object> attributes;//可以添加一些自定义的属性，json话的时候会拆到DBEntity对象里一起返回到前台

    protected void _setFieldValue(BaseJField column,Object value){
    	if(settedValue == null){
    		settedValue = new HashMap<BaseJField,Object>();
    	}
        settedValue.put(column, value);
    }
    
    public Map<BaseJField,Object> _getSettedValue(){
        return settedValue;
    }
    
    public void addAttribute(String key,Object value){
    	if(attributes == null){
    		attributes = new HashMap<String,Object>();
    	}
    	attributes.put(key, value);
    }
    public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public Object getAttr(String key){
		return attributes == null ? null : attributes.get(key);
	}
	
	public <T extends DBEntity>T clone(BaseJField...jfs) throws Exception {
		T newEntity = (T)ClassUtil.instance(this.getClass());
		
		List<BaseJField> setjfs = null;
		if(NullUtil.isEmpty(jfs)){
			setjfs = DBHelper.getAllJFieldList(this.getClass());
		}else{
			setjfs = ClassUtil.newList();
			for(BaseJField jf : jfs){
				setjfs.add(jf);
			}
		}
				
        int length = setjfs.size();
        for(int i=0;i<length;i++){
            BaseJField jf = setjfs.get(i);
            DBHelper.setEntityValue(newEntity, jf, DBHelper.getEntityValue(this, jf));
        }
		
		return newEntity;
	}

	//获取ID值
    abstract public Long getId();
    //设置ID值
    abstract public void setId(Long id);
}
