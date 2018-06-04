package com.xiesange.web.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.RequestContext;
import com.xiesange.baseweb.cache.CacheManager;
import com.xiesange.baseweb.component.SysparamCmp;
import com.xiesange.baseweb.pojo.BaseDataHolder;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.base.BaseEnum;
import com.xiesange.gen.dbentity.base.BaseParam;
import com.xiesange.orm.DBHelper;
import com.xiesange.web.define.ParamDefine;
/**
 * 基础数据组件
 * @author Wilson Wu
 * @date 2015年9月16日
 *
 */
public class BaseDataCmp {
	private static Logger logger = LogUtil.getLogger(BaseDataCmp.class);
	private static Map<String,Long> CACHE_FLAG ;
	
	static{
		initCacheFlag();
	}
	
	private static void initCacheFlag(){
		CACHE_FLAG = new HashMap<String,Long>();
		long curTime = System.currentTimeMillis();
		
		ParamDefine.Cache[] keys = ParamDefine.Cache.values();
		for(ParamDefine.Cache key : keys){
			CACHE_FLAG.put(key.name(), curTime);
		}
	}
	
	public static long getCacheFlag(ParamDefine.Cache key){
		return CACHE_FLAG.get(key.name());
	}
	public static void updateCacheFlag(ParamDefine.Cache key){
		CACHE_FLAG.put(key.name(), System.currentTimeMillis());
		logger.debug(".....update new flag["+key.name()+"] : "+CACHE_FLAG.get(key.name()));
	}
	
	
	public static void checkBaseData(Long baseparamFlag,Long enumFlag,ResponseBody responseBody) throws Exception{
		boolean isFromApp = RequestUtil.isFromApp(ETUtil.getRequestContext().getRequestHeader());
		//sysparam
		BaseDataHolder<Map<String,String>> sysparam_cacheData = baseparamFlag != null && baseparamFlag == -1 ? 
									null:BaseDataCmp.getParamMap(baseparamFlag,isFromApp);
		BaseDataCmp.appendBaseData(responseBody,sysparam_cacheData,"baseparam","baseparamFlag");
		
		//
	
		//enum
		BaseDataHolder<List<Map<String,Object>>> enum_cacheData = enumFlag != null && enumFlag == -1 ? 
						null : BaseDataCmp.getEnumList(enumFlag);
		BaseDataCmp.appendBaseData(responseBody,enum_cacheData,"enum","enumFlag");
		
	}
	
	public static void appendBaseData(ResponseBody respBody,BaseDataHolder<?> baseDataHolder,String dataKey,String flagKey){
		if(baseDataHolder != null){
			respBody.add(dataKey, baseDataHolder.getCacheData());
			respBody.add(flagKey, baseDataHolder.getCacheFlag());
		}
		
	}
	
	
	public static BaseDataHolder<List<Map<String,Object>>> getEnumList(Long reqCacheFlag) throws Exception{
		long currentCacheFlag = getCacheFlag(ParamDefine.Cache.enums);
		if(reqCacheFlag != null && reqCacheFlag == currentCacheFlag){
			//如果请求过来的数据标识有值，且和当前服务端的数据标识值一致，那么说明服务端没有更改过数据，所以不需要返回数据到客户端
			return new BaseDataHolder<List<Map<String,Object>>>(currentCacheFlag,null);
		}
		
		List<BaseEnum> enumitems = DBHelper.getDao().queryAll(BaseEnum.class);
		List<Map<String,Object>> enumTypes = new ArrayList<Map<String,Object>>();
		String code = null;
		for(BaseEnum typeEntity : enumitems){
			if(typeEntity.getTypeId() != -1){
				continue;
			}
			code = typeEntity.getCode();
			Map<String,Object> typeMap = new HashMap<String,Object>();
			typeMap.put("code", code);
			typeMap.put("text", typeEntity.getName());
			//typeMap.put("id", typeEntity.getId());
			List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
			
			//找到该枚举类型下的所有条目,放入itemList中
			for(BaseEnum itemEntity : enumitems){
				if(itemEntity.getTypeId().longValue() == typeEntity.getId()){
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
	
	public static BaseDataHolder<Map<String,String>> getParamMap(Long reqCacheFlag,boolean isFromApp) throws Exception{
		long currentCacheFlag = getCacheFlag(ParamDefine.Cache.baseparam);
		if(reqCacheFlag != null && reqCacheFlag == currentCacheFlag){
			//如果请求过来的数据标识有值，且和当前服务端的数据标识值一致，那么说明服务端没有更改过数据，所以不需要返回数据到客户端
			return new BaseDataHolder<Map<String,String>>(currentCacheFlag,null);
		}
		
		//如果请求过来的数据标识有值，且和当前服务端的数据标识值一致，那么说明服务端没有更改过数据，所以不需要返回数据到客户端
		List<BaseParam> paramList = isFromApp ? SysparamCmp.getAllAppUsed() : SysparamCmp.getAllWebUsed();
		Map<String,String> dataMap = new HashMap<String,String>();
		for(BaseParam param : paramList){
			dataMap.put(param.getCode().toLowerCase(), param.getValue());
		}
		
		return new BaseDataHolder<Map<String,String>>(currentCacheFlag,dataMap);//及时没更新数据，也要把flag回传，用于区分到底是没数据还是数据改为空了
	}
	
	public static void refreshParam(boolean isFromApp) throws Exception{
		CacheManager.refreshCache(BaseParam.class);
		BaseDataCmp.updateCacheFlag(ParamDefine.Cache.baseparam);
		
		List<BaseParam> paramList = CacheManager.getCacheHouse(BaseParam.class).getAll();
		if(NullUtil.isNotEmpty(paramList)){
			StringBuffer sb = new StringBuffer();
			for(BaseParam param : paramList){
				sb.append("【")
					.append(param.getCode())
					.append("】").append("    ")
					.append("【")
					.append(param.getName())
					.append("】 ").append("    ")
					.append("【")
					.append(param.getValue())
					.append("】")
					.append("\n\r");
			}
			logger.debug("___________New Sysparam : \r\n"+sb);
		}
	}
	public static void refreshEnum() throws Exception{
		CacheManager.refreshCache(BaseEnum.class);
	}
	
	
}
