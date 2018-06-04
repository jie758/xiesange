package com.xiesange.baseweb.component;

import java.util.ArrayList;
import java.util.List;

import com.xiesange.baseweb.cache.CacheHouse;
import com.xiesange.baseweb.cache.CacheManager;
import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.base.BaseEnum;
import com.xiesange.orm.sql.DBCondition;

/**
 * 枚举值组件
 * @author wuyujie Feb 6, 2015 10:33:25 AM
 *
 */
public class EnumCmp {
	
	/**
	 * 查询所有枚举类别
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午4:04:55
	 */
	public static List<BaseEnum> getAllEnumTypes() throws Exception{
		CacheHouse<BaseEnum> cacheHouse = CacheManager.getCacheHouse(BaseEnum.class);
		List<BaseEnum> enumTypeEntityList = cacheHouse.getList(
			new DBCondition(BaseEnum.JField.typeId,-1)
		);
		
		return enumTypeEntityList;
	}
	
	/**
	 * 查询指定枚举类别
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午4:04:55
	 */
	public static BaseEnum getEnumType(String type) throws Exception{
		CacheHouse<BaseEnum> cacheHouse = CacheManager.getCacheHouse(BaseEnum.class);
		BaseEnum enumTypeEntity = cacheHouse.getSingle(
			new DBCondition(BaseEnum.JField.code,type),
			new DBCondition(BaseEnum.JField.typeId,-1)
		);
		return enumTypeEntity;
	}
	
	/**
	 * 获取指定枚举类型的所有枚举条目列表
	 * @author wuyujie Feb 19, 2015 1:42:11 PM
	 * @param enumTypeCode
	 * @param mainTenantCond
	 * @return
	 * @throws Exception 
	 */
	public static List<BaseEnum> getEnumTypeItems(String enumTypeCode) throws Exception{
		CacheHouse<BaseEnum> cacheHouse = CacheManager.getCacheHouse(BaseEnum.class);
		BaseEnum enumTypeEntity = getEnumType(enumTypeCode);
		//LogUtil.dump("enumTypeEntity:", enumTypeEntity);
		if(enumTypeEntity == null)
			return null;
		
		List<BaseEnum> resultList = cacheHouse.getList(
			new DBCondition(BaseEnum.JField.typeId,enumTypeEntity.getId())
		);
		
		//LogUtil.dump("enumItemList", resultList);
		return resultList;
	}
	
	/**
	 * 获取指指定枚举类型的指定枚举条目对象
	 * @author wuyujie Feb 19, 2015 1:42:11 PM
	 * @param enumTypeCode
	 * @param mainTenantCond
	 * @return
	 */
	/*public static BaseEnum getEnumTypeItem(String enumTypeCode,String enumItemCode){
		CacheHouse<BaseEnum> cacheHouse = CacheManager.getCacheHouse(BaseEnum.class);
		BaseEnum enumTypeEntity = getEnumType(enumTypeCode);
		if(enumTypeEntity == null)
			return null;
		
		BaseEnum enumItem = cacheHouse.getSingle(
				new DBCondition(BaseEnum.JField.parentId,enumTypeEntity.getId()),
				new DBCondition(BaseEnum.JField.code,enumItemCode)
		);
		
		return enumItem;
	}*/
	
	public static BaseEnum getEnum(long enumId){
		CacheHouse<BaseEnum> cacheHouse = CacheManager.getCacheHouse(BaseEnum.class);
		return cacheHouse.getById(enumId);
	}
	
	/**
	 * 根据一个枚举值ID串，返回对应的枚举值名称列表
	 * @param enumIds
	 * @return
	 * @author Wilson 
	 * @date 2016年8月5日
	 */
	public static List<String> getEnumTextList(String enumIds) {
		if(NullUtil.isEmpty(enumIds))
			return null;
		String[] langIdArr = enumIds.split(",");
		BaseEnum enumEntity = null;
		List<String> list = new ArrayList<String>();
		for(String item : langIdArr){
			enumEntity = EnumCmp.getEnum(Long.valueOf(item));
			if(enumEntity == null)
				continue;
			list.add(enumEntity.getName());
		}
		return list;
	}
	
	public static String getEnumTextString(String enumIds,char separator) {
		List<String> textList = getEnumTextList(enumIds);
		if(NullUtil.isEmpty(textList))
			return null;
		return CommonUtil.joinBySeperator(separator, textList);
		/*String[] langIdArr = enumIds.split(",");
		StringBuilder builder = new StringBuilder();
		BaseEnum enumEntity = null;
		for(String item : langIdArr){
			enumEntity = EnumCmp.getEnum(Long.valueOf(item));
			if(enumEntity == null)
				continue;
			builder.append(separator).append(enumEntity.getName());
		}
		return builder.length() == 0 ? null : builder.substring(1);*/
	}
	
}
