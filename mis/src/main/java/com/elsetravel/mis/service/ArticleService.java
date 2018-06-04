package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.ETEditorCmp;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.FileUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.base.BaseArticle;
import com.elsetravel.mis.component.BaseDataCmp;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;
@ETServiceAnno(name="article",version="")
public class ArticleService extends AbstractService {
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
	public ResponseBody queryList(MisRequestContext context) throws Exception{
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		
		List<DBCondition> conds = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(code)){
			conds.add(new DBCondition(BaseArticle.JField.code,"%"+code+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(name)){
			conds.add(new DBCondition(BaseArticle.JField.name,"%"+code+"%",DBOperator.LIKE));
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
		
		return new ResponseBody("article_list",artList).addTotalCount(totalCount);
	}
	
	/**
	 * 查询谋篇文章具体内容
	 * @param context
	 * 			article_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:39:09
	 */
	public ResponseBody queryContent(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Article.article_id);
		
		long articleId = context.getRequestBody().getLong(ParamDefine.Article.article_id);
		
		BaseArticle art = dao().queryById(BaseArticle.class, articleId);
		if(art == null)
			return null;
		return new ResponseBody("content",art.getContent());
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
	public ResponseBody save(MisRequestContext context) throws Exception{
		Long articleId = context.getRequestBody().getLong(ParamDefine.Article.article_id);
		
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		String content = context.getRequestBody().getString(ParamDefine.Article.content);

		BaseArticle article = null;
		boolean isNew = articleId == null;
		if(isNew){
			RequestUtil.checkEmptyParams(context.getRequestBody(), 
					ParamDefine.Common.code,
					ParamDefine.Common.name,
					ParamDefine.Article.content
			);
			if(BaseDataCmp.queryArticleByCode(code) != null){
				//新增的场景下如果编码重复需要报错
				throw ETUtil.buildException(ErrorDefine.CODE_DUPLICATE);
			}
			article = new BaseArticle();
			article.setIsSystem((short)0);//通过前端添加的都不是系统性文章
		}else{
			
			article = BaseDataCmp.queryArticle(articleId);
			if(article == null){
				throw ETUtil.buildInvalidOperException();
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
		
		if(isNew){
			DBHelper.getDao().insert(article);
		}else if(DBHelper.isModified(article)){
			DBHelper.getDao().updateById(article, article.getId());
		}
		
		return new ResponseBody("articleId",article.getId());
	}
	
	
	/**
	 * 上传文章图文编辑中的图片。一次性只能上传一张
	 * @param context
	 * 			article_id,必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:35:56
	 */
	public ResponseBody uploadPic(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Article.article_id
		);
		long articleId = context.getRequestBody().getLong(ParamDefine.Article.article_id);
		String savePath = CommonUtil.join("/image/article/",articleId);
		
		UploadRequestBody requestBody = (UploadRequestBody)context.getRequestBody();
		
		String[] savePaths = ETEditorCmp.uploadPics(requestBody.getUploadFiles(), savePath);
		
		if(NullUtil.isEmpty(savePaths))
			return null;
		String picPath = savePaths[0];
		
		return new ResponseBody("picUrl", ETUtil.buildPicUrl(picPath));
		
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
	public ResponseBody remove(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Article.article_id
		);
		long articleId = context.getRequestBody().getLong(ParamDefine.Article.article_id);
		
		BaseArticle arti = BaseDataCmp.queryArticle(articleId);
		if(arti == null)
			return null;
		if(arti.getIsSystem() == 1){
			//系统型不能删除
			throw ETUtil.buildInvalidOperException();
		}
		
		//删除数据库记录
		dao().deleteById(BaseArticle.class, articleId);
		
		//删除图片
		FileUtil.delFolder(ETUtil.buildPicPath(CommonUtil.join("/image/article/",articleId)));
		
		return null;
	}
}
