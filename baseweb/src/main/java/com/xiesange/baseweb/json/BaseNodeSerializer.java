package com.xiesange.baseweb.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.xml.BaseNode;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.statement.field.BaseJField;
/**
 * 数据库实体DBEntity类序列化解析器。
 * 主要是判断DBEntity中有没有attribute，如果有那么要把这些attibute取出来跟属性平级的解析
 * 
 * @author wuyujie Jan 20, 2015 5:33:28 PM
 *
 */
public class BaseNodeSerializer implements ObjectSerializer {
	private static Logger logger = LogUtil.getLogger(BaseNodeSerializer.class);
	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType)
			throws IOException {
		BaseNode baseNode = (BaseNode)object;
		SerializeWriter out = serializer.getWriter();
		Map<String,Object> valueMap = new HashMap<String,Object>();
		valueMap.putAll(baseNode.getAttributes());
		//baseNode.getAttributes();
		valueMap.put("children", baseNode.getChildren());
		/*List<BaseJField> allJfields = DBHelper.getAllJFields(entity.getClass());
		try {
			for(BaseJField jfield : allJfields){
				valueMap.put(jfield.getName(), DBHelper.getEntityValue(entity, jfield));
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		Map<String,Object> attrs = entity.getAttributes();
		if(NullUtil.isNotEmpty(attrs)){
			valueMap.putAll(attrs);
		}*/
		out.write(JsonUtil.obj2Json(valueMap));
	}

}
