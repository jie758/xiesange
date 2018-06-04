package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.im.IMCmp;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.JsonUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.topic.Topic;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.gen.dbentity.user.UserFeedback;
import com.elsetravel.mis.component.UserCmp;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;

/**
 * 基础数据服务
 * @author Wilson
 *
 */
@ETServiceAnno(name="message",version="")
public class MessageService extends AbstractService{
	
	/**
	 * 查询用户反馈意见列表，按照提交提交从进到远排列
	 * @param context
	 * 			user_id,如果传了则按照提交的用户id进行过滤
	 * 			status,如果传了则按照意见处理状态进行过滤，0待处理，1已处理
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午7:37:22
	 */
	public ResponseBody queryFeedbackList(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		Long userId = reqbody.getLong(ParamDefine.User.user_id);
		Short status = reqbody.getShort(ParamDefine.Common.status);
		
		List<DBCondition> conds = new ArrayList<DBCondition>();
		if(userId != null){
			conds.add(new DBCondition(UserFeedback.JField.userId,userId));
		}
		if(status != null){
			conds.add(new DBCondition(UserFeedback.JField.status,status));
		}
		
		DBCondition[] condArr = conds.toArray(new DBCondition[conds.size()]);
		
		List<UserFeedback> feedbackList = dao().query(
			new QueryStatement(UserFeedback.class,condArr)
				.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
				.appendOrderFieldDesc(Topic.JField.createTime)
		);
		
		if(NullUtil.isEmpty(feedbackList)){
			return null;
		}
		
		long count = dao().queryCount(UserFeedback.class, condArr);
		
		Set<Long> userIds = ETUtil.buildEntityIdList(feedbackList, UserFeedback.JField.userId);
		
		List<User> userList = dao().query(
			new QueryStatement(User.class,new DBCondition(User.JField.id,userIds,DBOperator.IN))
								.appendQueryField(User.JField.id,User.JField.nickname)
		);
		if(NullUtil.isNotEmpty(userList)){
			for(UserFeedback feedback : feedbackList){
				for(User user : userList){
					if(user.getId().longValue() == feedback.getUserId()){
						feedback.addAttribute("userNickname", user.getNickname());
						break;
					}
				}
				ETUtil.clearDBEntityExtraAttr(feedback);
			}
		}
		return new ResponseBody("feedback_list",feedbackList).addTotalCount(count);
	}
	
	/**
	 * 把某条意见反馈置为"已处理"状态
	 * @param context
	 * 			feedback_id，必填
	 * @return
	 * @throws Exception
	 */
	public ResponseBody dealFeedback(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Feedback.feedback_id);
		Long feedbackId = reqbody.getLong(ParamDefine.Feedback.feedback_id);
		
		UserFeedback feedback = new UserFeedback();
		feedback.setStatus(BaseConstantDefine.STATUS_EFFECTIVE);//已处理
		dao().updateById(feedback, feedbackId);
		return null;
	}
	
	/**
	 * 查询聊天历史记录
	 * @param context,
	 * 			date,string，格式为yyyymmddhh（精确到小时）或者yyymmdd(精确到某天)
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:29:49
	 */
	public ResponseBody queryChatHistory(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		String dateStr = reqbody.getString(ParamDefine.Message.date);
		
		List<String> chatList = IMCmp.getChatHistory(dateStr);
		if(NullUtil.isEmpty(chatList))
			return null;
		
		Set<Long> userIds = new HashSet<Long>();
		Map<String,Object> messageMap = null;
		Long fromUserId = null;
		Long toUserId = null;
		
		List<Map<?,?>> resultList = new ArrayList<Map<?,?>>();
		for(String chat : chatList){
			logger.debug("______"+chat);
			chat = chat.substring(chat.indexOf("{"));
			messageMap = JsonUtil.json2Map(chat);
			if((Integer)messageMap.get("targetType") != 1)
				continue;//1是普通二人会话,6是系统通知,这里只解析type=1
			
			fromUserId = Long.parseLong((String)messageMap.get("fromUserId"));
			toUserId = Long.parseLong((String)messageMap.get("targetId"));

			userIds.add(fromUserId);
			userIds.add(toUserId);
			
			String content = ((JSONObject)messageMap.get("content")).getString("content");
		
			Map<String,Object> chatMap = new HashMap<String,Object>();
			chatMap.put("fromUser", fromUserId);
			chatMap.put("toUser", toUserId);
			chatMap.put("content", content);
			chatMap.put("time", messageMap.get("dateTime"));
			resultList.add(chatMap);
		}
		
		List<User> userList = NullUtil.isEmpty(userIds) ? null : dao().queryByIds(User.class, userIds);
		if(NullUtil.isNotEmpty(userList)){
			String fromUserName = null;
			String toUserName = null;
			for(Map map : resultList){
				fromUserId = (Long)map.get("fromUser");
				toUserId = (Long)map.get("toUser");
				
				fromUserName = null;
				toUserName = null;
				
				for(User user : userList){
					if(fromUserId != null && fromUserId.longValue() == user.getId()){
						fromUserName = user.getNickname();
					}else if(toUserId != null && toUserId.longValue() == user.getId()){
						toUserName = user.getNickname();
					}
					if(fromUserName != null && toUserName != null){
						break;
					}
				}
				map.put("fromUser",fromUserName);
				map.put("toUser",toUserName);
			}
		}
		return new ResponseBody("result",resultList);
	}
	
	/**
	 * 获取当前登录用户（员工）在融云端的token。该员工必须配置了app_user_id信息
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午9:51:07
	 */
	public ResponseBody getRongToken(MisRequestContext context) throws Exception{
		Long appUserId = context.getAccessUser().getAppUserId();//当前登录员工在app侧的userId
		if(appUserId == null){
			throw ETUtil.buildInvalidOperException("当前登录员工尚未填写App端用户ID，请前往菜单[员工设置]里进行配置");
		}
		User userEntity = UserCmp.queryUserById(appUserId);
		if(userEntity == null){
			throw ETUtil.buildInvalidOperException("当前登录员工配置的App端用户ID对应的用户并不存在，请前往菜单[员工设置]里进行修改");
		}
		
		String rongId = IMCmp.getRongToken(userEntity.getId(), userEntity.getNickname(), userEntity.getProfilePic());
		return new ResponseBody("rongId",rongId);
	}
}
