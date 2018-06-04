package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.component.ETEditorCmp;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.define.BaseErrorDefine;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.request.UploadRequestBody.UploadFile;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.topic.Topic;
import com.elsetravel.gen.dbentity.topic.TopicComment;
import com.elsetravel.gen.dbentity.topic.TopicDetail;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.gen.dbentity.user.UserFavTopic;
import com.elsetravel.mis.component.TopicCmp;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.field.summary.CountQueryField;
import com.elsetravel.orm.statement.query.QueryStatement;
/**
 * 专题服务类
 * @author Wilson Wu
 * @date 2015年8月14日
 *
 */

@ETServiceAnno(name="topic",version="")
public class CopyOfTopicService extends AbstractService{
	/**
	 * 修改专题信息，支持新增和修改，该方法需要用upload，且二进制流中包含了专题封面和图文明细中的图片 
	 * @param context
	 * 			topic_id,专题id，修改的话一定要传入
	 * 			name,专题名称
	 * 			summary,专题摘要
	 * 			user_id,专题作者
	 *			intro_list,图文明细对象列表
     *
	 * @return
	 * 			topic_id,返回当前新增的专题id
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年8月14日
	 */
	public ResponseBody save(MisRequestContext context) throws Exception {
		Topic topic = TopicCmp.saveTopic(context, false);
		long[] introids = (long[])topic.getAttr(TopicCmp.KEY_INTRO_IDS);
		return new ResponseBody("topic_id",topic.getId()).add("intro_ids", introids);
	}
	
	/**
	 * 发布旅行票。和save方法逻辑基本一致。只是在save方法的基础上，还会把旅票的status改成发布状态
	 * @param context
	 * 			参数同save方法,其中ticket_id必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:51:16
	 */
	public ResponseBody publish(MisRequestContext context) throws Exception {
		Topic topic = TopicCmp.saveTopic(context, true);
		return new ResponseBody("topic_id",topic.getId());
	}
	
	/**
	 * 查询专题图文明细
	 * @param context
	 * 			ticket_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:36:11
	 */
	public ResponseBody queryIntroList(MisRequestContext context) throws Exception {
		RequestBody reqbody = (RequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Topic.topic_id);
		
		Long topicId = reqbody.getLong(ParamDefine.Topic.topic_id);
		
		List<TopicDetail> detailList = dao().query(new QueryStatement(
				TopicDetail.class, 
				new DBCondition(TopicDetail.JField.topicId,topicId)
		).appendOrderField(TopicDetail.JField.id));
		
		ETEditorCmp.wrapIntroList(detailList);
		
		return new ResponseBody("result",detailList);
		
	}
	
	
	/**
	 * 查询专题列表，按照时间从近到远排序
	 * @param context
	 * 			user_id,发起人,不传则不按照此条件过滤
	 * 			name,专题名称，不传则不按照此条件过滤
	 * 			status,状态，已发布或者未发布,不传则不按照此条件过滤
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月24日
	 */
	public ResponseBody queryList(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		
		String name = reqbody.getString(ParamDefine.Common.name);
		Long userId = reqbody.getLong(ParamDefine.User.user_id);
		Short status = reqbody.getShort(ParamDefine.Common.status);
		
		List<DBCondition> conds = new ArrayList<DBCondition>();
		if(userId != null){
			conds.add(new DBCondition(Topic.JField.userId,userId));
		}
		if(NullUtil.isNotEmpty(name)){
			conds.add(new DBCondition(Topic.JField.name,"%"+name+"%",DBOperator.LIKE));
		}
		if(status != null){
			conds.add(new DBCondition(Topic.JField.status,status));
		}
		
		DBCondition[] condArr = conds.toArray(new DBCondition[conds.size()]);
		
		List<Topic> topicList = dao().query(
			new QueryStatement(Topic.class,condArr)
				.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
				.appendOrderFieldDesc(Topic.JField.createTime)
		);
		
		if(NullUtil.isEmpty(topicList)){
			return null;
		}
		
		long count = dao().queryCount(Topic.class, condArr);
		
		Set<Long> topicIds = ETUtil.buildEntityIdList(topicList, Topic.JField.id);
		//查询出每个专题的被收藏数：select count(*),topic_id from user_fav_topic where topic_id in (xxx) group by topic_id
		List<UserFavTopic> favList = dao().query(
			new QueryStatement(UserFavTopic.class,new DBCondition(UserFavTopic.JField.topicId,topicIds,DBOperator.IN))
					.appendGroupField(UserFavTopic.JField.topicId)
					.appendQueryField(UserFavTopic.JField.topicId,CountQueryField.getInstance())
		);
		Set<Long> authorIds = ETUtil.buildEntityIdList(topicList, Topic.JField.userId);
		List<User> authorList = dao().query(
			new QueryStatement(User.class,new DBCondition(User.JField.id,authorIds,DBOperator.IN))
								.appendQueryField(User.JField.id,User.JField.nickname)
		);
		for(Topic topic : topicList){
			topic.addAttribute("favCount", 0);
			topic.setPic(ETUtil.buildPicUrl(topic.getPic()));
			topic.setTopPic(ETUtil.buildPicUrl(topic.getTopPic()));
			topic.setBottomPic(ETUtil.buildPicUrl(topic.getBottomPic()));
			if(NullUtil.isNotEmpty(favList)){
				for(UserFavTopic fav : favList){
					if(fav.getTopicId().longValue() == topic.getId()){
						topic.addAttribute("favCount", fav.getAttr(CountQueryField.getInstance().getColName()));
						break;
					}
				}
			}
			ETUtil.clearDBEntityExtraAttr(topic);
		}
		return new ResponseBody("result",topicList).addTotalCount(count).add("user_list", authorList);
		
	}
	
	/**
	 * 查询某个专题的评论列表。置顶的排列在前面，剩余再按创建日期从近到远排列
	 * @param context
	 * 			topic_id,必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午5:43:47
	 */
	public ResponseBody queryCommentList(MisRequestContext context) throws Exception {
		RequestBody reqbody = (RequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Topic.topic_id);
		
		Long topicId = reqbody.getLong(ParamDefine.Topic.topic_id);
		
		DBCondition cond = new DBCondition(TopicComment.JField.topicId,topicId);
		
		int[] pageInfo = ETUtil.buildPageInfo(context.getRequestHeader());
		//先按照is_stick排序，is_stick=1排在前面，再按照stick_time降序排（非置顶评论该字段为null）,再按照createTime降序排
		List<TopicComment> list = dao().query(
			new QueryStatement(TopicComment.class,cond)
						.appendOrderFieldDesc(TopicComment.JField.isStick,TopicComment.JField.stickTime,TopicComment.JField.createTime)
						.appendRange(pageInfo)
		);
		if(NullUtil.isEmpty(list)){
			return null;
		}
		List<User> userlist = dao().queryByIds(User.class, ETUtil.buildEntityIdList(list, TopicComment.JField.userId));
		ETUtil.clearDBEntityExtraAttr(userlist);
		
		long count = dao().queryCount(TopicComment.class, cond);
		
		return new ResponseBody("comment_list",list).add("user_list", userlist).addTotalCount(count);
	}
	
	/**
	 * 把某条评论置顶。
	 * 该条评论的is_stick会更新成1，stick_time会更新当前时间。
	 * @param context
	 * 			comment_id,必填
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:36:45
	 */
	public ResponseBody stickComment(MisRequestContext context) throws Exception {
		RequestBody reqbody = (RequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Topic.comment_id);
		
		Long commentId = reqbody.getLong(ParamDefine.Topic.comment_id);
		
		TopicComment comment = new TopicComment();
		comment.setStickTime(DateUtil.now());
		comment.setIsStick((short)1);
		dao().updateById(comment, commentId);
		
		return null;
	}
	
	/**
	 * 取消某条评论的置顶。
	 * 该条评论的is_stick会更新成0，stick_time会更新成null。
	 * @param context
	 * 			comment_id,必填
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:39:46
	 */
	public ResponseBody cancelStickComment(MisRequestContext context) throws Exception {
		RequestBody reqbody = (RequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Topic.comment_id);
		
		Long commentId = reqbody.getLong(ParamDefine.Topic.comment_id);
		
		TopicComment comment = new TopicComment();
		comment.setStickTime(null);
		comment.setIsStick((short)0);
		dao().updateById(comment, commentId);
		
		return null;
	}
	
	
	/**
	 * 专题撤销，只有发布状态的专题才能撤销，且撤销后专题变为草稿状态
	 * @param context
	 * 			topic_id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody cancel(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Topic.topic_id);
		
		Long topicId = reqbody.getLong(ParamDefine.Topic.topic_id);
		
		Topic topic = TopicCmp.checkTopicExist(topicId);
		
		//只有发布状态的专题才能撤销,否则需要报错
		if(topic.getStatus() != BaseConstantDefine.STATUS_EFFECTIVE){
			throw ETUtil.buildException(BaseErrorDefine.SYS_OPEARTION_INVALID);
		}
		
		
		topic.setStatus(BaseConstantDefine.STATUS_NONE);
		dao().updateById(topic, topicId);
		return null;
	}
	
	
	/**
	 * 删除专题。
	 * @param context
	 * 			topic_id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody remove(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Topic.topic_id);
		
		Long topicId = reqbody.getLong(ParamDefine.Topic.topic_id);
		dao().deleteById(Topic.class, topicId);
		return null;
	}
	
	
	/**
	 * 上传旅票图文编辑中的图片。一次性只能上传一张
	 * @param context
	 * 			ticket_id,必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:35:56
	 */
	public ResponseBody uploadIntroPics(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Topic.topic_id);
		long topicId = context.getRequestBody().getLong(ParamDefine.Topic.topic_id);
		
		UploadRequestBody requestBody = (UploadRequestBody)context.getRequestBody();
		
		List<UploadFile> files = requestBody.getUploadFiles();
		
		if(NullUtil.isEmpty(files))
			return null;
		UploadFile file = files.get(0);
		String picPath = CommonUtil.join("/image/topic/",topicId,"/intro/",file.getFileName(),".",file.getExtendName());
		picPath = CCP.uploadImage(file, picPath,false);
		
		return new ResponseBody("picPath",picPath);
	}
	/**
	 * 保存图文编辑数据。图片的话需要前台先调用uploadIntroPics，把图片都上传好。这里只需要把图片url传过来即可。
	 * 注意，处理逻辑是在ticket_detail全量替换，即把老的数据先删除掉，把新的数据添加进去
	 * @param context
	 * 			ticket_id,必传
	 * 			intro_list，是一个数组，每个数组中的元素是图文明细中的一个条目，属性为：
	 * 				id,
	 * 				type,明细类型,image/text
	 * 				value,明细值，如果是text的话就填文本内容，如果是pic的话就要和下一个参数图片二进制流里的name对应上
	 * 				style,样式，尤其是text类型
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:36:22
	 */
	public ResponseBody saveIntroList(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Topic.topic_id);
		long topicId = context.getRequestBody().getLong(ParamDefine.Topic.topic_id);
		//String introList = context.getRequestBody().getString(ParamDefine.Ticket.intro_list);
		List<TopicDetail> commitList = context.getRequestBody().getDBEntityList(ParamDefine.Topic.intro_list, TopicDetail.class);
		List<TopicDetail> insertList = ETEditorCmp.insertIntroList(TopicDetail.class, topicId, TopicDetail.JField.topicId, commitList);
		
		List<Long> ids = new ArrayList<Long>();
		for(TopicDetail entity : insertList){
			ids.add(entity.getId());
		}
		return new ResponseBody("newids",ids);
	}
	
}
