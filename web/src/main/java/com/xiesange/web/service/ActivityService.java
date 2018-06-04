package com.xiesange.web.service;

import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.activity.Activity;
import com.xiesange.gen.dbentity.base.BaseArticle;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.define.ParamDefine;
@ETServiceAnno(name="activity",version="")
public class ActivityService extends AbstractService {
	/**
	 * 获取新id
	 * @param context
	 * 			activity_code,
	 * 			article_code
	 * @return
	 * @throws Exception
	 */
	public ResponseBody view(WebRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Activity.activity_code
		);
		String activityCode = context.getRequestBody().getString(ParamDefine.Activity.activity_code);
		String articleCode = context.getRequestBody().getString(ParamDefine.Article.article_code);
		BaseArticle arti = null;
		if(NullUtil.isNotEmpty(articleCode)){
			arti = dao().querySingle(BaseArticle.class, new DBCondition(BaseArticle.JField.code,articleCode));
		}
		
		CCP.updateFieldNum(Activity.JField.viewCount, 1, new DBCondition(Activity.JField.code,activityCode));
		
		return new ResponseBody("article",arti);
	}
	
	
}
