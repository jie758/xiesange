package com.xiesange.web.service;

import java.util.ArrayList;
import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.base.BaseArticle;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.ArticleCmp;
import com.xiesange.web.define.ErrorDefine;
import com.xiesange.web.define.ParamDefine;
@ETServiceAnno(name="article",version="")
public class ArticleService extends AbstractService {
	/**
	 * 获取新id
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody getNewId(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());
		long newid = dao().getSequence(BaseArticle.class);
		return new ResponseBody("newid",newid);
	}
	
	/**
	 * 查询所有文章列表，这里返回的文章里不包含具体内容，为了报文大小考虑。返回的文章列表按照日期从近到远排列
	 * 需要查看文章内容，请调用queryContent接口
	 * @param context
	 * 			code,文章编码，模糊搜索
	 * 			name，文章名称，模糊搜索
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午6:05:12
	 */
	public ResponseBody queryList(WebRequestContext context) throws Exception{
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		
		List<DBCondition> conds = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(code)){
			conds.add(new DBCondition(BaseArticle.JField.code,"%"+code+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(name)){
			conds.add(new DBCondition(BaseArticle.JField.name,"%"+name+"%",DBOperator.LIKE));
		}
		
		DBCondition[] condArr = conds.toArray(new DBCondition[conds.size()]);
		List<BaseArticle> artList = dao().query(new QueryStatement(BaseArticle.class,condArr)
							.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
							.appendQueryField(BaseArticle.JField.id,
												BaseArticle.JField.code,
												BaseArticle.JField.name,
												BaseArticle.JField.isSystem,
												BaseArticle.JField.createTime
							)
							.appendOrderFieldDesc(BaseArticle.JField.createTime)
		);
		long totalCount = 0;
		if(NullUtil.isNotEmpty(artList)){
			totalCount = dao().queryCount(BaseArticle.class, condArr);
		}
		
		return new ResponseBody("articleList",artList).addTotalCount(totalCount);
	}
	
	/**
	 * 查询谋篇文章具体内容
	 * @param context
	 * 			article_id,
	 * 			code,
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:39:09
	 */
	public ResponseBody queryDetail(WebRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Article.article_id);
		
		long articleId = context.getRequestBody().getLong(ParamDefine.Article.article_id);
		
		BaseArticle art = dao().queryById(BaseArticle.class, articleId);
		if(art == null)
			return null;
		Short recordView = context.getRequestBody().getShort(ParamDefine.Article.record_view);
		if(recordView == null || recordView == 1){
			CCP.updateFieldNum(BaseArticle.JField.viewCount, 1,articleId);
		}
		return new ResponseBody("article",art);
	}
	
	public ResponseBody queryDetailByCode(WebRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Article.article_code
		);
		String code = context.getRequestBody().getString(ParamDefine.Article.article_code);
		BaseArticle arti = dao().querySingle(BaseArticle.class, new DBCondition(BaseArticle.JField.code,code));
		if(arti == null){
			return null;
		}
		Short recordView = context.getRequestBody().getShort(ParamDefine.Article.record_view);
		if(recordView == null || recordView == 1){
			CCP.updateFieldNum(BaseArticle.JField.viewCount, 1,arti.getId());
		}
		return new ResponseBody("article",arti);
	}
	
	
	/**
	 * 保存文章信息。这里只是保存住信息，不包括文章具体内容。
	 * 如要保存内容，请调用接口saveContent
	 * @param context
	 * 			article_id,如果有有值则表示修改，为空表示新增
	 * 			code,文章编码，保存后编码就不能修改
	 * 			name,文章名称
	 * 			content,文章内容
	 * content,文章内容，html
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:11:00
	 */
	public ResponseBody save(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());
		RequestBody reqbody = context.getRequestBody();
		Long articleId = reqbody.getLong(ParamDefine.Article.article_id);
		
		String code = reqbody.getString(ParamDefine.Common.code);
		String name = reqbody.getString(ParamDefine.Common.name);
		String content = reqbody.getString(ParamDefine.Article.content);
		String shareMessageTitle = reqbody.getString(ParamDefine.Article.share_message_title);
		String shareMessageDesc = reqbody.getString(ParamDefine.Article.share_message_desc);
		String shareTimelineDesc = reqbody.getString(ParamDefine.Article.share_timeline_desc);
		String sharePic = reqbody.getString(ParamDefine.Article.share_pic);
		
		BaseArticle article = null;
		boolean isNew = articleId == null || articleId < 0;
		if(isNew){
			RequestUtil.checkEmptyParams(context.getRequestBody(), 
					ParamDefine.Common.code,
					ParamDefine.Common.name,
					ParamDefine.Article.content
			);
			if(ArticleCmp.queryArticleByCode(code) != null){
				//新增的场景下如果编码重复需要报错
				throw ETUtil.buildException(ErrorDefine.COMMON_DUPLICATE_CODE);
			}
			article = new BaseArticle();
			if(articleId != null && articleId < 0){
				article.setId(-1*articleId);
			}
			article.setIsSystem((short)0);//通过前端添加的都不是系统性文章
		}else{
			/*article = BaseDataCmp.queryArticle(articleId);
			if(article == null){
				throw ETUtil.buildInvalidOperException();
			}*/
			if(NullUtil.isNotEmpty(code)){
				article = ArticleCmp.queryArticleByCode(code);
				if(article != null && article.getId() != articleId.longValue()){
					//修改的场景下如果编码重复需要报错
					throw ETUtil.buildException(ErrorDefine.COMMON_DUPLICATE_CODE);
				}
			}
			if(article == null){
				article = new BaseArticle();
				article.setId(articleId);
			}
		}
		//修改场景下只能修改名称
		if(code != null){
			article.setCode(code);
		}
		if(name != null){
			article.setName(name);
		}
		if(content != null){
			article.setContent(content);
		}
		if(shareMessageTitle != null){
			article.setShareMessageTitle(shareMessageTitle);
		}
		if(shareMessageDesc != null){
			article.setShareMessageDesc(shareMessageDesc);
		}
		if(shareTimelineDesc != null){
			article.setShareTimelineDesc(shareTimelineDesc);
		}
		if(sharePic != null){
			article.setSharePic(sharePic);
		}
		
		if(isNew){
			DBHelper.getDao().insert(article);
		}else if(DBHelper.isModified(article)){
			DBHelper.getDao().updateById(article, articleId);
		}
		
		return new ResponseBody("articleId",article.getId());
	}
	
	
	/**
	 * 删除某个文章。系统型文章不能删除。
	 * @param context
	 * 			article_id,必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:50:51
	 */
	public ResponseBody remove(WebRequestContext context) throws Exception{
		CCP.checkOperateAdmin(context.getAccessUser());
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Article.article_id
		);
		long articleId = context.getRequestBody().getLong(ParamDefine.Article.article_id);
		
		BaseArticle arti = ArticleCmp.queryArticle(articleId);
		if(arti == null)
			return null;
		if(arti.getIsSystem() == 1){
			//系统型不能删除
			throw ETUtil.buildInvalidOperException();
		}
		
		//删除数据库记录
		dao().deleteById(BaseArticle.class, articleId);
		
		//删除图片
		//FileUtil.delFolder(ETUtil.buildPicPath(CommonUtil.join("/image/article/",articleId)));
		
		return null;
	}
}
