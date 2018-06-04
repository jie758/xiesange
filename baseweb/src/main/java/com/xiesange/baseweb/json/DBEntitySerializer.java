package com.xiesange.baseweb.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.statement.field.BaseJField;
/**
 * 数据库实体DBEntity类序列化解析器。
 * 主要是判断DBEntity中有没有attribute，如果有那么要把这些attibute取出来跟属性平级的解析
 * 
 * @author wuyujie Jan 20, 2015 5:33:28 PM
 *
 */
public class DBEntitySerializer implements ObjectSerializer {
	private static Logger logger = LogUtil.getLogger(DBEntitySerializer.class);
	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType)
			throws IOException {
		DBEntity entity = (DBEntity)object;
		SerializeWriter out = serializer.getWriter();
		Map<String,Object> valueMap = new HashMap<String,Object>();
		List<BaseJField> allJfields = DBHelper.getAllJFieldList(entity.getClass());
		try {
			if(entity instanceof User){
				((User)entity).setPassword(null);//防止User中的密码泄露到前端，这里要清空
				//((User)entity).setPayPassword(null);//防止User中的密码泄露到前端，这里要清空
			}
			String fName = null;
			Object fValue = null;
			for(BaseJField jfield : allJfields){
				fName = jfield.getName();
				fValue = DBHelper.getEntityValue(entity, jfield);
				if(fValue == null)
					continue;
				//如果是数据库里Date类型，那么返回到前台的时候需要把时间精度给去掉
				if(jfield.getColTypeName().equals("DATE") && fValue != null){
					fValue = DateUtil.date2Str((Date)fValue, DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
				}
				valueMap.put(fName,fValue);
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		Map<String,Object> attrs = entity.getAttributes();
		if(NullUtil.isNotEmpty(attrs)){
			valueMap.putAll(attrs);
		}
		out.write(JsonUtil.obj2Json(valueMap));
	}

}
