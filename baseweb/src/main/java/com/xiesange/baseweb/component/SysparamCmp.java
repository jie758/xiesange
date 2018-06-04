package com.xiesange.baseweb.component;


import java.util.Date;
import java.util.List;

import com.xiesange.baseweb.cache.CacheManager;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.base.BaseParam;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
/**
 * 系统配置项API。可以获取配置在base_configuration中各个配置项值。
 * @author wuyujie Dec 12, 2014 9:43:42 PM
 *
 */
public class SysparamCmp {
	private static final DBCondition WEB_USED_COND = new DBCondition(BaseParam.JField.webUsed,1);
	private static final DBCondition APP_USED_COND = new DBCondition(BaseParam.JField.appUsed,1);
	
	
	public static interface ISysParam{
		public String name();
	}
	
	private static final Class<BaseParam> CLZ = BaseParam.class;
	
	public static List<BaseParam> getAll(){
		return CacheManager.getCacheHouse(CLZ).getAll();
	}
	
	public static List<BaseParam> getAllAppUsed() throws Exception{
		return CacheManager.getCacheHouse(CLZ).getList(APP_USED_COND);
	}
	public static List<BaseParam> getAllWebUsed() throws Exception{
		return CacheManager.getCacheHouse(CLZ).getList(WEB_USED_COND);
	}
	
	public static String get(ISysParam enumCode) throws Exception{
		BaseParam entity = (BaseParam)CacheManager.getCacheHouse(CLZ).getSingle(new DBCondition(BaseParam.JField.code,enumCode.name()));
		if(entity == null)
			return null;
		return entity.getValue();
	}
	
	/**
	 * 模糊查找所有符合的配置项.返回一个二维数组，第一维是匹配到的所有配置项列表，第二维只有两个元素，是每个配置的编码和配置值
	 * @author wuyujie Dec 12, 2014 9:57:37 PM
	 * @param code，传入"xxx%"，那么就会用code lie 'xxx%'来查找
	 * @return
	 * @throws Exception
	 */
	public static String[][] like(ISysParam enumCode) throws Exception{
		List<BaseParam> entityList = DBHelper.getDao().query(CLZ, new DBCondition(BaseParam.JField.code,enumCode.name(),DBOperator.LIKE));
		if(NullUtil.isEmpty(entityList))
			return null;
		
		String[][] result = new String[entityList.size()][2];
		for(int i=0;i<entityList.size();i++){
			BaseParam entity = entityList.get(i);
			result[i] = new String[]{entity.getCode(),entity.getValue()};
		}
		
		return result;
	}
	public static Short getShort(ISysParam enumCode) throws Exception{
		String value = get(enumCode);
		if(NullUtil.isEmpty(value))
			return null;
		return Short.valueOf(value);
	}
	public static Integer getInt(ISysParam enumCode) throws Exception{
		String value = get(enumCode);
		if(NullUtil.isEmpty(value))
			return null;
		return Integer.valueOf(value);
	}
	public static Long getLong(ISysParam enumCode) throws Exception{
		String value = get(enumCode);
		if(NullUtil.isEmpty(value))
			return null;
		return Long.valueOf(value);
	}
	public static Float getFloat(ISysParam enumCode) throws Exception{
		String value = get(enumCode);
		if(NullUtil.isEmpty(value))
			return null;
		return Float.valueOf(value);
	}
	public static Double getDouble(ISysParam enumCode) throws Exception{
		String value = get(enumCode);
		if(NullUtil.isEmpty(value))
			return null;
		return Double.valueOf(value);
	}
	public static Boolean getBoolean(ISysParam enumCode) throws Exception{
		String value = get(enumCode);
		if(NullUtil.isEmpty(value))
			return null;
		if(value.equals("1") || value.equals("true")){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 返回默认格式的不带时间精度的日期。默认格式为:yyyy-mm-dd
	 * @author wuyujie Dec 12, 2014 9:37:09 PM
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static Date getDate(ISysParam enumCode) throws Exception{
		return getDate(enumCode, DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
	}
	/**
	 * 返回指定格式的不带时间精度的日期。默认格式为:yyyy-mm-dd
	 * @author wuyujie Dec 12, 2014 9:37:09 PM
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static Date getDate(ISysParam enumCode,String format) throws Exception{
		String value = get(enumCode);
		if(NullUtil.isEmpty(value))
			return null;
		return DateUtil.str2Date(value, format);
	}
	
	/**
	 * 返回默认格式的带时间精度的日期。默认格式为:yyyy-mm-dd hh:MM:ss
	 * @author wuyujie Dec 12, 2014 9:37:09 PM
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static Date getDateTime(ISysParam enumCode) throws Exception{
		return getDate(enumCode, DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS);
	}
	/**
	 * 返回指定格式的带时间精度的日期。默认格式为:yyyy-mm-dd hh:MM:ss
	 * @author wuyujie Dec 12, 2014 9:37:09 PM
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static Date getDateTime(ISysParam enumCode,String format) throws Exception{
		String value = get(enumCode);
		if(NullUtil.isEmpty(value))
			return null;
		return DateUtil.str2Date(value, format);
	}
	
	/**
	 * 返回字符串数组，默认配置值是以","分隔
	 * @author wuyujie Dec 12, 2014 9:40:56 PM
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static String[] getArray(ISysParam enumCode) throws Exception{
		return getArray(enumCode,",");
		
	}
	/**
	 * 返回字符串数组，可以指定分隔符
	 * @author wuyujie Dec 12, 2014 9:40:56 PM
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static String[] getArray(ISysParam enumCode,String splitChar) throws Exception{
		String value = get(enumCode);
		if(NullUtil.isEmpty(value))
			return null;
		return value.split(splitChar);
	}
	
	/**
	 * 返回字符串数组，默认配置值是以","分隔
	 * @author wuyujie Dec 12, 2014 9:40:56 PM
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static int[] getIntArray(ISysParam enumCode) throws Exception{
		return getIntArray(enumCode,",");
	}
	/**
	 * 返回字符串数组，可以指定分隔符
	 * @author wuyujie Dec 12, 2014 9:40:56 PM
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static int[] getIntArray(ISysParam enumCode,String splitChar) throws Exception{
		String[] arr =  getArray(enumCode);
		if(NullUtil.isEmpty(arr))
			return null;
		int[] resultArr = new int[arr.length];
		for(int i=0;i<arr.length;i++){
			resultArr[i] = Integer.valueOf(arr[i]);
		}
		return resultArr;
	}
}
