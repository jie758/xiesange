package com.xiesange.web.service;

import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.BaseDataCmp;
import com.xiesange.web.define.ParamDefine;

/**
 * 基础数据服务
 * @author Wilson
 *
 */
@ETServiceAnno(name="base",version="")
public class BaseDataService extends AbstractService{
	/**
	 * 查询基础数据
	 * @param context
	 * 			sysparam_flag,标签，包括个人标签和旅票标签
	 * 			enum_flag,枚举值
	 * 			country_flag,国家
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午1:41:22
	 */
	public ResponseBody queryBaseData(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		Long baseparam_flag = reqbody.getLong(ParamDefine.Cache.baseparam);
		Long enum_flag = reqbody.getLong(ParamDefine.Cache.enums);
		
		ResponseBody respBody = new ResponseBody();
		
		BaseDataCmp.checkBaseData(baseparam_flag, enum_flag, respBody);
		
		/*//sysparam
		BaseDataHolder<Map<String,String>> sysparam_cacheData = param_flag != null && param_flag == -1 ? 
							null:BaseDataCmp.getParamMap(param_flag,RequestUtil.isFromApp(context.getRequestHeader()));
		BaseDataCmp.appendBaseData(respBody,sysparam_cacheData,"baseparam","baseparamFlag");
		
		//enum
		BaseDataHolder<List<Map<String,Object>>> enum_cacheData = enum_flag != null && enum_flag == -1 ? 
				null : BaseDataCmp.getEnumList(enum_flag);
		BaseDataCmp.appendBaseData(respBody,enum_cacheData,"enum","enumFlag");
		*/
		return respBody;
	}
	
	
	/*public ResponseBody queryArticle(WebRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Common.code
		);
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		BaseArticle arti = dao().querySingle(BaseArticle.class, new DBCondition(BaseArticle.JField.code,code));
		return new ResponseBody("article",arti);
	}*/
	
	
}
