package com.xiesange.web.service;

import java.util.ArrayList;
import java.util.List;

import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.QiniuUtil;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.BaseDataCmp;
import com.xiesange.web.define.ParamDefine;
@ETServiceAnno(name="common",version="")
public class CommonService extends AbstractService {
	
	public ResponseBody refreshCache(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		Short need_baseparam = reqbody.getShort(ParamDefine.Cache.baseparam);
		Short need_enum = reqbody.getShort(ParamDefine.Cache.enums);
		
		StringBuffer sb = new StringBuffer();
		if(need_baseparam != null && need_baseparam == 1){
			BaseDataCmp.refreshParam(RequestUtil.isFromApp(context.getRequestHeader()));
			sb.append(",baseparam_flag=1");
		}
		if(need_enum != null && need_enum == 1){
			BaseDataCmp.refreshEnum();
			sb.append(",enum_flag=1");
		}
		return new ResponseBody("result",sb.length()==0?null:sb.substring(1));
	}
	
	
	/**
	 * 获取七牛上传token
	 * @param context
	 * 			keys,string,多个值用,分隔,如果要获取默认的key，请用'default',比如:'default,image/user/1001/main'
	 * @return
	 * 		按照keys的顺序，返回对应的token
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午5:09:14
	 */
	public ResponseBody getQiniuUptokens(WebRequestContext context) throws Exception{
		//RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Qiniu.keys);
		String keys = context.getRequestBody().getString(ParamDefine.Qiniu.keys);
		Short needCommon = context.getRequestBody().getShort(ParamDefine.Qiniu.need_common,(short)0);
		List<String> tokens = null;
		if(NullUtil.isNotEmpty(keys)){
			String[] keyArr = keys.split(",");
			tokens = new ArrayList<String>();
			for(String key : keyArr){
				tokens.add(QiniuUtil.getUpToken(key));
			}
		}
		return new ResponseBody("tokens",tokens).add("commonToken", needCommon==0?null:QiniuUtil.getUpToken());
	}
}
