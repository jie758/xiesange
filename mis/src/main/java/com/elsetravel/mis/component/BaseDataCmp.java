package com.elsetravel.mis.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.cache.CacheManager;
import com.elsetravel.baseweb.component.SysParamCmp;
import com.elsetravel.baseweb.config.bean.CountryConfigBean;
import com.elsetravel.baseweb.pojo.BaseDataHolder;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.util.VersionUtil;
import com.elsetravel.core.IParamEnum;
import com.elsetravel.core.util.LogUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.xml.BaseNode;
import com.elsetravel.gen.dbentity.base.BaseArticle;
import com.elsetravel.gen.dbentity.base.BaseConfig;
import com.elsetravel.gen.dbentity.base.BaseEnum;
import com.elsetravel.gen.dbentity.base.BaseTag;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;
/**
 * 基础数据组件
 * @author Wilson Wu
 * @date 2015年9月16日
 *
 */
public class BaseDataCmp {
	private static Logger logger = LogUtil.getLogger(BaseDataCmp.class);
	private static Map<String,Long> CACHE_FLAG ;
	public static enum CACHE_KEY implements IParamEnum{
		country_flag,
		sysparam_flag,
		enum_flag,
		sharing_flag,
		topic_flag,
		banner_flag,
	}
	static{
		initCacheFlag();
	}
	
	private static void initCacheFlag(){
		CACHE_FLAG = new HashMap<String,Long>();
		long curTime = System.currentTimeMillis();
		
		CACHE_KEY[] keys = CACHE_KEY.values();
		for(CACHE_KEY key : keys){
			CACHE_FLAG.put(key.name(), curTime);
		}
	}
	
	public static long getCacheFlag(CACHE_KEY key){
		return CACHE_FLAG.get(key.name());
	}
	public static void updateCacheFlag(CACHE_KEY key){
		CACHE_FLAG.put(key.name(), System.currentTimeMillis());
		logger.debug(".....update new flag["+key.name()+"] : "+CACHE_FLAG.get(key.name()));
	}
	
	
	public static BaseDataHolder<Map<String,String>> getSysParamMap(Long reqCacheFlag) throws Exception{
		long currentCacheFlag = getCacheFlag(CACHE_KEY.sysparam_flag);
		if(reqCacheFlag != null && reqCacheFlag == currentCacheFlag){
			//如果请求过来的数据标识有值，且和当前服务端的数据标识值一致，那么说明服务端没有更改过数据，所以不需要返回数据到客户端
			return null;
		}
		
		Map<String,String> dataMap = new HashMap<String,String>();
		List<BaseConfig> configList = SysParamCmp.getAll();
		for(BaseConfig config : configList){
			dataMap.put(config.getCode(),config.getValue());
		}
		return new BaseDataHolder<Map<String,String>>(currentCacheFlag,dataMap);
	}
	
	public static BaseDataHolder<List<Map<String,Object>>> getEnumList(Long reqCacheFlag) throws Exception{
		long currentCacheFlag = getCacheFlag(CACHE_KEY.enum_flag);
		if(reqCacheFlag != null && reqCacheFlag == currentCacheFlag){
			//如果请求过来的数据标识有值，且和当前服务端的数据标识值一致，那么说明服务端没有更改过数据，所以不需要返回数据到客户端
			return null;
		}
		
		List<BaseEnum> enumitems = DBHelper.getDao().queryAll(BaseEnum.class);
		List<Map<String,Object>> enumTypes = new ArrayList<Map<String,Object>>();
		for(BaseEnum typeEntity : enumitems){
			if(typeEntity.getIsLeaf() != 0){
				continue;
			}
			Map<String,Object> typeMap = new HashMap<String,Object>();
			typeMap.put("code", typeEntity.getCode());
			typeMap.put("text", typeEntity.getName());
			//typeMap.put("id", typeEntity.getId());
			List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
			
			//找到该枚举类型下的所有条目,放入itemList中
			for(BaseEnum itemEntity : enumitems){
				if(itemEntity.getIsLeaf() == 1 && itemEntity.getParentId().longValue() == typeEntity.getId()){
					Map<String,Object> itemMap = new HashMap<String,Object>();
					itemMap.put("ext", NullUtil.isEmpty(itemEntity.getCode()) ? null : itemEntity.getCode());
					itemMap.put("text", itemEntity.getName());
					itemMap.put("id", itemEntity.getId());
					itemList.add(itemMap);
				}
			}
			
			typeMap.put("items", itemList);
			enumTypes.add(typeMap);
		}
		
		return new BaseDataHolder<List<Map<String,Object>>>(currentCacheFlag,enumTypes);
	}
	
	public static BaseDataHolder<List<BaseNode>> getCountryList(Long reqCacheFlag) throws Exception{
		long currentCacheFlag = getCacheFlag(CACHE_KEY.country_flag);
		if(reqCacheFlag != null && reqCacheFlag == currentCacheFlag){
			//如果请求过来的数据标识有值，且和当前服务端的数据标识值一致，那么说明服务端没有更改过数据，所以不需要返回数据到客户端
			return null;
		}
		List<BaseNode> result = CountryConfigBean.getCountyList();
		return new BaseDataHolder<List<BaseNode>>(currentCacheFlag,result);
	}
	
	
	public static void appendBaseData(ResponseBody respBody,BaseDataHolder<?> baseDataHolder,String dataKey,String flagKey){
		if(baseDataHolder != null){
			respBody.add(dataKey, baseDataHolder.getCacheData());
			respBody.add(flagKey, baseDataHolder.getCacheFlag());
		}
	}
	
	
	/**
	 * 查询系统中的标签定义，标签有好几种，这里要根据传入的type来做区分 
	 * @param type
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月14日
	 */
	public static List<BaseTag> queryTagDefineList(short type) throws Exception{
		return CacheManager.getCacheHouse(BaseTag.class).getList(new DBCondition(BaseTag.JField.type,type));
	}
	/**
	 * 查询指定id的标签定义实体
	 * @param tagId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:10:26
	 */
	public static BaseTag queryTagDefine(long tagId) throws Exception{
		List<BaseTag> tagDefineList = CacheManager.getCacheHouse(BaseTag.class).getAll();
		if(NullUtil.isEmpty(tagDefineList))
			return null;
		for(BaseTag tag : tagDefineList){
			if(tag.getId().longValue() == tagId){
				return tag;
			}
		}
		return null;
			
	}
	
	/**
	 * 查询系统中定义的旅行票类别
	 * @param type
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月14日
	 */
	/*public static List<BaseTicketCatalog> queryTicketCatalogDefine() throws Exception{
		List<BaseTicketCatalog> catalogList = CacheManager.getCacheHouse(BaseTicketCatalog.class).getAll();
		for(BaseTicketCatalog catalog : catalogList){
			catalog.setPic(ETUtil.buildPicUrl(catalog.getPic(),"small"));
			ETUtil.clearDBEntityExtraAttr(catalog);
		}
		return catalogList;
	}*/
	
	/*public static List<BaseEnum> queryLangList() throws Exception{
		List<LangObj> items = new ArrayList<LangObj>();
		
		items.add(new LangObj("en","英语"));
		items.add(new LangObj("zh_cn","中文"));
		items.add(new LangObj("fr","法语"));
		
		return EnumCmp.getEnumTypeItems(EnumDefine.LANG);
	}
	
	public static BaseEnum queryLangByCode(String code) throws Exception{
		List<BaseEnum> items = queryLangList();
		for(BaseEnum lang : items){
			if(lang.getCode().equals(code)){
				return lang;
			}
		}
		return null;
	}*/
	
	/*public static List<String> getSearchKeyList() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, Exception{
		List<BaseEnum> list =  EnumCmp.getEnumTypeItems(EnumDefine.SEARCH_KEY);
		if(NullUtil.isEmpty(list))
			return null;
		List<String> items = new ArrayList<String>();
		for(BaseEnum enumEntity : list){
			ETUtil.clearDBEntityExtraAttr(enumEntity);
			items.add(enumEntity.getName());
		}
		return items;
	}*/
	
	/**
	 * 根据标签定义的id列表，返回对应的名称列表
	 * @param tagDefineIds
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:05:07
	 */
	public static List<String> buildTagNameList(List<Long> tagDefineIds) throws Exception{
		if(NullUtil.isEmpty(tagDefineIds))
			return null;
		List<String> names = new ArrayList<String>();
		for(Long id : tagDefineIds){
			BaseTag tagDefine = BaseDataCmp.queryTagDefine(id);
			if(tagDefine == null)
				continue;
			names.add(tagDefine.getName());
		}
		return names;
	}
	
	
	
	/**
	 * 查询某个类型的文章，只会返回一条
	 * @param type
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午10:40:44
	 */
	public static BaseArticle queryArticleByCode(String code) throws Exception{
		return DBHelper.getDao().querySingle(BaseArticle.class, new DBCondition(BaseArticle.JField.code,code));
	}
	public static BaseArticle queryArticle(long articleId) throws Exception{
		return DBHelper.getDao().queryById(BaseArticle.class, articleId);
	}
	
	
}
