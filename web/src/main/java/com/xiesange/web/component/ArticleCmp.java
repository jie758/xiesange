package com.xiesange.web.component;

import com.xiesange.gen.dbentity.base.BaseArticle;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;

public class ArticleCmp {
	public static BaseArticle queryArticleByCode(String code) throws Exception{
		return DBHelper.getDao().querySingle(BaseArticle.class, new DBCondition(BaseArticle.JField.code,code));
	}
	public static BaseArticle queryArticle(long articleId) throws Exception{
		return DBHelper.getDao().queryById(BaseArticle.class, articleId);
	}
}
