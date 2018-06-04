package com.xiesange.baseweb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiesange.baseweb.component.SysparamCmp;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.define.IErrorCodeEnum;
import com.xiesange.baseweb.define.RequestHeaderField;
import com.xiesange.baseweb.define.SysparamDefine;
import com.xiesange.baseweb.exception.XSGException;
import com.xiesange.baseweb.request.RequestHeader;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.request.ResponseHeader;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.exception.IException;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.PinyinUtil;
import com.xiesange.gen.dbentity.base.BaseEnum;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.query.QueryStatement;
/**
 * ElseTravelUtil的简写，放置有关ElseTravel业务逻辑相关的工具类
 * @author Think
 *
 */
public class ETUtil {
	public static final String OFFICIAL_WEBSITE = "http://www.elsetravel.com";
	public static Date PERMANENT_DATE = null;
	private static Pattern TEXT_PARAM_PATTERN = Pattern.compile("\\$\\{(\\w+)\\}");//解析${xxx}
	private static DecimalFormat FEN_2_YUAN_FORMAT = new java.text.DecimalFormat("#.00");
	
	
	private static ThreadLocal<RequestContext> TL_CONTEXT = new ThreadLocal<RequestContext>();
	
	public static void setRequestContext(RequestContext context){
		TL_CONTEXT.set(context);
	}
	public static RequestContext getRequestContext(){
		return TL_CONTEXT.get();
	}
	public static void clearRequestContext(){
		TL_CONTEXT.remove();
	}
	
	/**
     * 处理新插入实体的一些特殊字段值
     * @author wuyujie Nov 19, 2014 2:33:48 PM
     * @param entity
     * @throws Exception
     */
    public static void dealInsertEntity(DBEntity entity) throws Exception{
    	//如果ID值为空，则取sequence赋值
    	if(entity.getId() == null || entity.getId() <=0){
    		entity.setId(DBHelper.getDao().getSequence(entity.getClass()));
    	}
	    
	    //如果createDate为空，则取当前时间赋值
    	Date now = DateUtil.now();
	    BaseJField jf_createDate = DBHelper.getCreateTimeJField(entity.getClass(),false);
	    if(jf_createDate != null && DBHelper.getEntityValue(entity, jf_createDate) == null){
	    	DBHelper.setEntityValue(entity, jf_createDate, now);
	    }
	    
	  	//如果modifyDate为空，则取当前时间赋值
	    BaseJField jf_modifyDate = DBHelper.getUpdateTimeJField(entity.getClass(),false);
	    if(jf_modifyDate != null && DBHelper.getEntityValue(entity, jf_modifyDate) == null){
	    	DBHelper.setEntityValue(entity, jf_modifyDate, now);
	    }
	    
	    //如果sts为空，则插入的时候设置为1
	    BaseJField jf_sts = DBHelper.getStsJField(entity.getClass(),false);
	    if(jf_sts != null && DBHelper.getEntityValue(entity, jf_sts) == null){
	    	DBHelper.setEntityValue(entity, jf_sts, BaseConsDefine.STS.EFFECT.value());
	    }
	    
	    //设置sn,关联流水记录
	    RequestContext context = getRequestContext();
	    BaseJField jf_sn = DBHelper.getSnJField(entity.getClass(),false);
	    if(jf_sn != null && DBHelper.getEntityValue(entity, jf_sn) == null){
	    	Long sn = context==null?null:context.getSn(true);
	    	DBHelper.setEntityValue(entity, jf_sn, sn==null?-1:sn);
	    }
	    
	    //处理拼音
	    dealPinyin(entity, false);
    }
    
    
    public static <T extends DBEntity>QueryStatement createQueryStatement(Class<T> dbClass,DBCondition...conds) throws Exception{
    	BaseJField stsField = DBHelper.getStsJField(dbClass, false);
		if(stsField != null){
			if(NullUtil.isEmpty(conds)){
				return new QueryStatement(dbClass,new DBCondition(stsField,BaseConsDefine.STS.EFFECT.value()));
			}else{
				DBCondition[] finalConds = new DBCondition[conds.length+1];
				boolean includeSts = false;
				for(int i=0;i<conds.length;i++){
					finalConds[i] = conds[i];
					if(finalConds[i] == null)
						continue;
					if(finalConds[i].getJField() != null && finalConds[i].getJField().getName().equalsIgnoreCase("STS")){
						includeSts = true;//如果业务侧已经传了STS条件，则这里不再添加STS
					}
				}
				if(!includeSts){
					finalConds[conds.length] = new DBCondition(stsField,BaseConsDefine.STS.EFFECT.value());
				}
				return new QueryStatement(dbClass,finalConds);
			}
		}else{
			return new QueryStatement(dbClass,conds);
		}
    }
    /**
     * 处理更新实体时一些特殊字段值
     * @param entity
     * @throws Exception
     */
    public static void dealUpdateEntity(DBEntity entity) throws Exception{
    	//如果updateTime没被业务侧特殊赋值，则默认用当前时间作为更新时间
	    BaseJField jf_modifyDate = DBHelper.getUpdateTimeJField(entity.getClass(),false);
	    if(jf_modifyDate != null && !entity._getSettedValue().containsKey(jf_modifyDate)){
	    	DBHelper.setEntityValue(entity, jf_modifyDate, DateUtil.now());
	    }
	    
	    
	    //设置sn,关联流水记录
	    BaseJField jf_sn = DBHelper.getSnJField(entity.getClass(),false);
	    if(jf_sn != null && !entity._getSettedValue().containsKey(jf_sn)){
	    	RequestContext context = getRequestContext();
	    	Long sn = null;
	    	if(context == null){
	    		sn = -1L;
	    	}else{
	    		sn = context.getSn(false);
	    	}
	    	DBHelper.setEntityValue(entity, jf_sn, sn);
	    }
	    
    	//处理拼音
	    dealPinyin(entity, true);
    }
    
    /**
	 * 如果实体中pinyin或者pinyin_header，需要把拼音解析出来存进去，至于解析哪个字段的值默认是name字段，也可以在配置文件中指定其他字段，但只能指定一个字段
	 * @author wuyujie Dec 11, 2014 1:41:40 PM
	 * @param entity
	 * @param isUpdte,是否是更新操作。更新的情况下，只有拼音源值字段做了修改才需要同步更新拼音字段
	 * @throws Exception
	 */
	private static void dealPinyin(DBEntity entity,boolean isUpdate) throws Exception{
		Class<? extends DBEntity> entityClass = entity.getClass();
		BaseJField pinyinField = DBHelper.getPYJField(entityClass,false);
	    BaseJField pinyinHeaderField = DBHelper.getPYHeaderJField(entityClass,false);
	    if(pinyinField == null && pinyinHeaderField == null)
	    	return;//没有拼音字段就不处理了
	    
	    String py = (String)DBHelper.getEntityValue(entity, pinyinField);
	    String pyHeader = (String)DBHelper.getEntityValue(entity, pinyinHeaderField);
		
	    if(py != null && pyHeader != null){
	    	return;//py和py_header字段都有值了，说明业务侧赋值了，那么这里不需要做自动处理了
	    }
	    
		String pinyinFieldStr = null;//DBTableConfigBean.getPinyinSourceField(entityClass);
		if(NullUtil.isEmpty(pinyinFieldStr)){
			pinyinFieldStr = "name";//如果没有配置拼音源值字段，默认就取用name
		}
		BaseJField pinyinValueField = DBHelper.getJField(entityClass, pinyinFieldStr,false);
		if(pinyinValueField == null)
			return;
		
		if(isUpdate && !entity._getSettedValue().containsKey(pinyinValueField)){
			return;//更新的情况下，只有拼音源值字段做了修改才需要同步更新拼音字段
		}
		
		String pinyinFieldValue = (String)DBHelper.getEntityValue(entity, pinyinValueField);
		
		if(py == null && pinyinField != null && pinyinFieldValue != null)
			DBHelper.setEntityValue(entity,pinyinField, PinyinUtil.getFullSpell(pinyinFieldValue));
		
		if(pyHeader != null && pinyinHeaderField != null && pinyinFieldValue != null)
			DBHelper.setEntityValue(entity,pinyinHeaderField, PinyinUtil.getFirstSpell(pinyinFieldValue));
	}
	
	/**
	 * 构建200002错误，不合法的操作，因为非常常用，所以单独提取出来
	 * @return
	 * @author Wilson Wu
	 * @date 2015年9月10日
	 */
	public static XSGException buildInvalidOperException(){
		return buildException(BaseErrorDefine.SYS_OPEARTION_INVALID);
	}
	public static XSGException buildInvalidOperException(String message){
		return buildException(BaseErrorDefine.SYS_OPEARTION_INVALID,message);
	}
	public static XSGException buildAuthException(){
		throw ETUtil.buildException(BaseErrorDefine.SYS_AUTHLIMIT);
	}
	
	public static XSGException buildException(IErrorCodeEnum errorEnum){
		return new XSGException(errorEnum);
	}
	public static XSGException buildException(IErrorCodeEnum errorEnum,Object... params){
		return new XSGException(errorEnum,params);
	}
	/*public static String getSnipptLangResourceValue(ILangResourceEnum langEnum,String lang){
		return LangResourceHolder.getSnipptResourceValue(String.valueOf(langEnum.getCode()), lang);
	}*/
	/*public static BusiException buildException(String message){
		return new BusiException(BaseErrorDefine.SYS_ERROR,message);
	}*/
	/*public static String parsePatternText(String text,Object... params){
		if(NullUtil.isEmpty(params)){
			return text;
		}
		return 
	}*/
	
    
    public static String outputResponse(RequestContext context,ResponseBody responseBody,IException e) throws Exception{
		ResponseHeader responesHeader = RequestUtil.buildResponseHeader(e,context.getRequestHeader().getApp_lang());
		if(e != null)
			responesHeader.setSn(context.getSn(false));//需要把流水号返回到前端，这样提示报错的时候把流水号也一并显示,方便后端定位问题
		
		String output = RequestUtil.createReponseJson(responesHeader, responseBody);
		if(context.getResponse() != null){
			context.getResponse().addHeader("Access-Control-Allow-Origin", "*");//可以跨域访问
			context.getResponse().getWriter().print(output);
			context.getResponse().getWriter().flush();
			context.getResponse().getWriter().close();
		}
		return output;
	}
    
    /**
     * 返回分页查询的时候，如果前端没有传每页记录数，要查询一个默认记录数
     * @return
     */
    public static int getDefaultPageCount(){
    	return 20;
    }
    
    
    
    /**
     * 把一个数据中存储的pic的url拼接成完整的url
     * @param savePath
     * @return
     * @throws Exception
     */
    /*public static String buildPicUrl(String savePath) throws Exception{
    	return buildPicUrl(savePath,"large");
    }*/
    
    public static String buildPicUrl(String savePath) throws Exception{
    	if(NullUtil.isEmpty(savePath)){
    		return null;
    	}
    	
    	if(savePath.startsWith("http://") || savePath.startsWith("https://")){
    		return savePath;//如果本身已经是http开头了则不用再拼装了
    	}
    	
    	String urlBasePath = SysparamCmp.get(SysparamDefine.RESOURCE_URL_PATH)+"/";
    	String url = urlBasePath+savePath;
    	
    	return url;
    }
    
    
    /**
     * 构建url跳转的时候，url后面跟着的参数串
     * @param bodyParam
     * @param token
     * @return
     */
    public static String buildUrlParam(Map<String,Object> bodyParam,String token){
    	StringBuffer url = new StringBuffer();
    	if(NullUtil.isNotEmpty(bodyParam)){
    		Iterator<Entry<String,Object>> it = bodyParam.entrySet().iterator();
    		Object val = null;
			String key = null;
    		while(it.hasNext()){
    			Entry<String,Object> entry = it.next();
    			key = entry.getKey();
    			val = entry.getValue();
    			
    			if(url.length() > 0){
    				url.append("&");
    			}
    			url.append(key).append("=").append(val);
    		}
    	}
    	
    	if(NullUtil.isNotEmpty(token)){
			if(url.length() > 0){
				url.append("&");
			}
			url.append("token="+token);
		}
    	return url.toString();
    }
    
    
    
    /**
     * 清除DBEntity中多余的信息。DBEntity中的有些字段对于前端来说大部分场景下是没用的，比如sn,update_time等，
     * 为了缩减报文大小，把这些字段清空这样就不会在json序列化的时候被包含进去。
     * 以下属性会被清除：
     * sn
     * create_time,
     * update_time,
     * sts
     * py,
     * py_header
     * @param entity
     * @param excludeFields,默认清除上述字段，也可以指定哪些字段不清除
     * @author Wilson Wu
     * @throws Exception 
     * @date 2015年9月21日
     */
    public static void clearDBEntityExtraAttr(DBEntity entity,BaseJField...excludeFields) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, Exception{
    	if(entity == null)
    		return;
    	BaseJField[] clearfields = new BaseJField[]{
    			DBHelper.getCreateTimeJField(entity.getClass(), false),
    			DBHelper.getUpdateTimeJField(entity.getClass(), false),
    			DBHelper.getStsJField(entity.getClass(), false),
    			DBHelper.getSnJField(entity.getClass(), false),
    			DBHelper.getPYJField(entity.getClass(), false),
    			DBHelper.getPYHeaderJField(entity.getClass(), false)
    	};
    	
    	for(BaseJField j : clearfields){
    		if(j == null || CommonUtil.isIn(j, excludeFields))
    			continue;
        	DBHelper.setEntityValue(entity,j , null);
    	}
    	
    }
    public static <T extends DBEntity>void clearDBEntityExtraAttr(List<T> entityList,BaseJField...excludeFields) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, Exception{
    	if(NullUtil.isEmpty(entityList))
    		return;
    	for(T entity : entityList){
    		clearDBEntityExtraAttr(entity,excludeFields);
    	}
    }
    
    public static void clearEnumEntityExtraAttr(List<BaseEnum> entityList) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, Exception{
    	if(NullUtil.isEmpty(entityList))
    		return;
    	for(BaseEnum entity : entityList){
    		clearDBEntityExtraAttr(entity);
    		//entity.setParentId(null);
    		//entity.setIsLeaf(null);
    		entity.setMemo(null);
    	}
    }
    /**
     * 构建分页信息。前台如果指定了page_index、page_count就用前台的值；如果没指定就用默认的
     * @param header
     * @return
     * @author Wilson Wu
     * @date 2015年9月24日
     */
    public static int[] buildPageInfo(RequestHeader header){
    	Integer pageIndex = header.getPage_index();
		Integer pageCount = header.getPage_count();
		if(pageIndex == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_PARAM_ISNULL,RequestHeaderField.page_index.name());
		}
		if(pageCount == null)
			pageCount = ETUtil.getDefaultPageCount();
		
		return new int[]{pageIndex,pageCount};
    }
    
    /**
     * 元转成分
     * @param yuan
     * @return
     * @author Wilson Wu
     * @date 2015年9月24日
     */
    public static Long parseYuan2Fen(Float yuan){
    	return yuan == null ? null : new BigDecimal(yuan.toString()).multiply(new BigDecimal(100)).longValue();
    	//return yuan == null ? null : Float.valueOf(yuan*100).longValue();
    }
    
    /**
     * 分转成元
     * @param fen
     * @return
     * @author Wilson Wu
     * @date 2015年9月24日
     */
    public static String parseFen2YuanStr(Long fen,boolean needPrecision){
    	 Double result =  fen == null ? null : fen/100.0;//Double.valueOf().floatValue();
    	 if(result == null)
    		 return null;
    	 
    	 String yuan = FEN_2_YUAN_FORMAT.format(result);
    	 if(yuan.startsWith(".")){
    		 yuan = "0"+yuan;
    	 }
    	 if(!needPrecision && yuan.endsWith(".00")){
    		 yuan = yuan.replace(".00", "");
    	 }
    	 return yuan;
    }
    
    public static Float parseFen2Yuan(Long fen){
    	Float result =  fen == null ? null : Double.valueOf(fen/100.0).floatValue();
    	return result;
   }
    
    
    /**
     * 小时转成分钟
     * @param hour
     * @author Wilson Wu
     * @date 2015年9月24日
     */
    public static Long parseHour2Minute(Float hour){
    	return hour == null ? null : Float.valueOf(hour*60).longValue();
    }
    /**
     * 分钟转成小时
     * @param minute
     * @return
     * @author Wilson Wu
     * @date 2015年9月24日
     */
    public static Float parseMinute2Hour(Long minute){
    	return minute == null ? null : Double.valueOf(minute/60.0).floatValue();
    }
    
    
    /**
     * 从一个数据库实体列表中，把指定字段值(只能是Long型的id值)提取出来，组成一个列表返回
     * @param entityList
     * @param idField
     * @return
     * @throws Exception
     * @author Wilson 
     * @date 下午7:56:58
     */
    public static <T extends DBEntity>Set<Long> buildEntityIdList(List<T> entityList,BaseJField idField) throws Exception{
    	if(NullUtil.isEmpty(entityList)){
    		return null;
    	}
    	Set<Long> ids = new HashSet<Long>();
    	Long idVal = null;
    	for(T entity : entityList){
    		idVal = idField.getName().equalsIgnoreCase("ID") ? entity.getId() : (Long)DBHelper.getEntityValue(entity, idField);
    		if(idVal == null)
    			continue;
    		ids.add(idVal);
    	}
    	return ids;
    }
    
    public static void gotoMessagePage(String message,HttpServletRequest request,HttpServletResponse response) throws Exception{
    	message = URLEncoder.encode(message,"UTF-8");
		RequestUtil.redirect(RequestUtil.getHost(request,true)+"/message.html?"+message, request, response);
    	//return new ModelAndView("message").addObject("message", message);
    }
    
    public static Date getPermanentDate() throws Exception{
    	if(PERMANENT_DATE == null){
    		PERMANENT_DATE = DateUtil.str2Date("2099-12-30",DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
		}
    	return PERMANENT_DATE;
    }
    
    /**
     * 解析带有参数变量的文本。比如"恭喜您${name},注册成为其乐导游。"，通过传入的参数可以解析成“恭喜您Jack,注册成为其乐导游。”
     * @param text,带有参数变量的文本串，其中参数需要以${xxx}形式呈现，xxx只能是字母
     * @param params，传入的变量对应的值，会把文本中的对应变量都替换掉
     * @return
     * @author Wilson 
     * @date 上午10:49:08
     */
    public static String parseTextExpression(String text,KeyValueHolder params){
    	Matcher matcher = TEXT_PARAM_PATTERN.matcher(text);
		StringBuffer sb = new StringBuffer(128);
		String paramCode = null;
		String paramValue = null;
		while(matcher.find()){
			paramCode = matcher.group(1);
			paramValue = params.getParamValue(paramCode);
			if(paramValue == null)
				paramValue = "";
			matcher.appendReplacement(sb, paramValue);
		}
		matcher.appendTail(sb);
    	return sb.toString();
    }
    
    /**
	 * 按照字典排序，组织成url参数串
	 * 比如Map中有参数：
	 * name=bill;
	 * age=30;
	 * sex=1
	 * 
	 * 那么需要把键名按照字典排序后，最终获得url串：
	 * age=30&name=bill&sex=1
	 * @param params,需要字典排序的原始参数map对象
	 * @return,标准的url参数串，比如name=bill&age=30,只是参数名是经过了字典排序的
	 * @author Wilson 
	 * @date 下午1:33:43
	 */
    public static String createSortedUrlStr(Map<String, ?> params) {
		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		int index = 0;
		for (int i = 0; i < keys.size(); ++i) {
			String key = (String) keys.get(i);
			String value = params.get(key)== null ? null : String.valueOf(params.get(key));
			if (value == null)
				continue;
			if(index > 0)
				content.append("&");
			content.append(key).append("=").append(value);
			++index;
		}
		return content.toString();
	}
	/**
	 * 只把value值进行排列的签名串
	 * @param params
	 * @param joinChar
	 * @return
	 * @author Wilson 
	 * @date 2016年7月5日
	 */
	public static String createSortedValueStr(Map<String, ?> params,String joinChar) {
		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		int index = 0;
		for (int i = 0; i < keys.size(); ++i) {
			String key = (String) keys.get(i);
			String value = params.get(key)== null ? null : String.valueOf(params.get(key));
			if (NullUtil.isEmpty(value))
				continue;
			if(index > 0)
				content.append(joinChar);
			content.append(value);
			++index;
		}
		return content.toString();
	}
	
	/**
	 * 构建完整的url
	 * @param context
	 * @param relativeUrl
	 * 			相对于主机ip的url，不需要/，本方法会自动把主机以及端口拼上。
	 * @return
	 * @author Wilson 
	 * @date 上午11:15:33
	 */
	public static String buildHostUrl(RequestContext context,String relativeUrl) {
		return RequestUtil.getHost(context.getRequest(),true)+relativeUrl;
		
	}
	
	/**
	 * 把id串转成long数组，其中id串里要用,分隔
	 * @param ids
	 * @return
	 * @author Wilson 
	 * @date 2016年7月9日
	 */
	public static List<Long> trans2LongArray(String idsStr){
		String[] idArr = idsStr.split(",");
		List<Long> idList = ClassUtil.newList();
		for(int i=0;i<idArr.length;i++){
			idList.add(Long.valueOf(idArr[i]));
		}
		return idList;
	}
	
	public static String maskMobile(String mobile){
		if(NullUtil.isEmpty(mobile)){
			return mobile;
		}
		String first = mobile.substring(0,3);
		String last = mobile.substring(mobile.length()-4);
		return first+"****"+last;
	}
	public static String maskName(String name){
		if(NullUtil.isEmpty(name) || name.length() == 1){
			return name;
		}
		String first = name.substring(0,1);
		String last = "";
		if(name.length() > 2){
			last = name.substring(name.length()-1);
		}
		
		return first+"*"+last;
	}
	public static void main(String[] args) {
		System.out.println(maskName("吴宇宇杰"));
	}
}
