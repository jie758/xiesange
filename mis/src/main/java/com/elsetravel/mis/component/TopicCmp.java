package com.elsetravel.mis.component;

import java.util.ArrayList;
import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.RequestContext;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.component.ETEditorCmp;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.define.BaseErrorDefine;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.request.UploadRequestBody.UploadFile;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.topic.Topic;
import com.elsetravel.gen.dbentity.topic.TopicDetail;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;

public class TopicCmp {
	public static final String KEY_INTRO_IDS = "intro_ids";
	
	/**
	 * 根据id查询出专题实体
	 * @param topic
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月21日
	 */
	public static Topic queryTopic(long topicId) throws Exception{
		Topic topic = DBHelper.getDao().queryById(Topic.class,topicId);
		return topic;
	}
	
	/**
	 * 根据用户ID，检测导游是否存在。如果存在则返回Guider实体；如果不存在则抛出异常
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static Topic checkTopicExist(long topicId) throws Exception{
		Topic topic = queryTopic(topicId);
		if(topic == null){
			throw ETUtil.buildException(ErrorDefine.TOPIC_NOTEXIST);
		}
		return topic;
	}
	
	/**
	 * 保存/发布专题。包括修改和新增。
	 * @param context
	 * @param isPublish,true表示发布旅票
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:51:48
	 */
	public static Topic saveTopic(RequestContext context,boolean isPublish) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		
		//RequestUtil.checkEmptyParams(reqbody, ParamDefine.Ticket.ticket_id);
		
		Long topicId = reqbody.getLong(ParamDefine.Topic.topic_id);
		Topic topic = null;
		if(topicId == null){
			topic = new Topic();
			topic.setStatus(BaseConstantDefine.STATUS_NONE);
			topicId = DBHelper.getDao().getSequence(Topic.class);
		}else{
			topic = DBHelper.getDao().queryById(Topic.class, topicId);
			//检查ticket的存在性
			if(topic == null){
				throw ETUtil.buildException(ErrorDefine.TICKET_NOTEXIST);
			}
		}
		
		
		String name = reqbody.getString(ParamDefine.Common.name);
		Long userId = reqbody.getLong(ParamDefine.User.user_id);//专题作者
		String summary = reqbody.getString(ParamDefine.Topic.summary);
		String countryCode = reqbody.getString(ParamDefine.Topic.country_code);
		String cityCode = reqbody.getString(ParamDefine.Topic.city_code);
		String placeName = reqbody.getString(ParamDefine.Topic.place_name);
		String latitude = reqbody.getString(ParamDefine.Topic.latitude);
		String longtitude = reqbody.getString(ParamDefine.Topic.longtitude);
		
		if(name != null){
			topic.setName(name);
		}
		if(userId != null){
			topic.setUserId(userId);
		}
		if(summary != null){
			topic.setSummary(summary);
		}
		
		if(countryCode != null){
			topic.setCountryCode(countryCode);
		}
		if(cityCode != null){
			topic.setCityCode(cityCode);
		}
		if(placeName != null){
			topic.setPlaceName(placeName);
		}
		if(latitude != null){
			topic.setLatitude(latitude);
		}
		if(longtitude != null){
			topic.setLongtitude(longtitude);
		}
		
		
		if(isPublish && checkForPublish(topic)){
			topic.setStatus(BaseConstantDefine.STATUS_EFFECTIVE);
		}
		
		//处理封面
		UploadFile coverFile = reqbody.getUploadFile(ParamDefine.Topic.pic.name());
		if(coverFile != null){
			String picPath = CommonUtil.join("image/topic/",topicId,"/cover.",coverFile.getExtendName());
			picPath = CCP.uploadImage(coverFile, picPath,true);
			topic.setPic(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		
		//处理详情里的顶部照片
		UploadFile topPicFile = reqbody.getUploadFile(ParamDefine.Topic.top_pic.name());
		if(topPicFile != null){
			String picPath = CommonUtil.join("image/topic/",topicId,"/top.",topPicFile.getExtendName());
			picPath = CCP.uploadImage(topPicFile, picPath,true);
			topic.setTopPic(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		//处理详情里的底部照片
		UploadFile bottomPicFile = reqbody.getUploadFile(ParamDefine.Topic.bottom_pic.name());
		if(bottomPicFile != null){
			String picPath = CommonUtil.join("image/topic/",topicId,"/bottom.",bottomPicFile.getExtendName());
			picPath = CCP.uploadImage(bottomPicFile, picPath,true);
			topic.setBottomPic(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		
		if(topic.getId() == null){
			topic.setId(topicId);
			DBHelper.getDao().insert(topic);//新增数据插入
		}else if(NullUtil.isNotEmpty(topic._getSettedValue())){
			DBHelper.getDao().updateById(topic, topicId);
		}
		
		//处理图文明细,注意intro_list如果没有这个字段参数，则表示不做需改；如果传了这个字段参数，但是值里是一个空数组则表示全部删除
		List<TopicDetail> commitList = reqbody.getDBEntityList(ParamDefine.Ticket.intro_list, TopicDetail.class);
		long[] newIntroids = dealIntroList(topicId, reqbody, commitList);
		topic.addAttribute(KEY_INTRO_IDS, newIntroids);
		return topic;
	}
	
	
	
	
	
	/**
	 * 处理专题中的图文内容简介明细。内容简介是图文模式，可以有图片也可以有文本描述，每个都是一个细节item。
	 * 专题明细的处理方式是：每次保存的时候都要把原记录全部删除掉，再把新纪录按照顺序添加进去。因为前端涉及到排序，可以随意调整每个明细项目的位置，
	 * 如果按照正规方式单个单个的来处理非常的麻烦，所以索性把原先的先删除掉，再把最终的插入。
	 * 因此前台要按照顺序把明细项目组织好传送过来，有一个要点要注意，如果某个明细项没有修改过，那么实体中的value字段不要填值，系统会自动去把老记录中的value
	 * 取出来设置进去
	 * @param context
	 * 			ticket_id,修改的专题id
	 * 			detail_list,修改的明细内容实体，是一个数组，每个数组中的元素属性为：
	 * 				id,
	 * 				type,明细类型,pic/text
	 * 				value,明细值，如果是text的话就填文本内容，如果是pic的话就要和下一个参数图片二进制流里的name对应上
	 * 				is_title,是否是标题，0不是 ,1是
	 * 			List<图片二进制流>，如果明细中有新增图片需要全部上传上来，而且名称要和entity.value对应上
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月14日
	 */
	public static long[] dealIntroList(long topicId,UploadRequestBody reqbody,List<TopicDetail> commitList) throws Exception{
		if(commitList == null){
			return null;//为null表示不做修改
		}
		if(commitList.size() == 0){
			clearIntroList(topicId);//不为null但数组为空表示全部清空
			return null;
		}
		
		
		//先查出已经存在的明细
		List<TopicDetail> existList = DBHelper.getDao().query(TopicDetail.class, new DBCondition(TopicDetail.JField.topicId,topicId));
		long[] newids = null;
		newids = DBHelper.getDao().getSequence(TopicDetail.class, commitList.size());
		List<TopicDetail> insertList = new ArrayList<TopicDetail>();
		for(int i=0;i<commitList.size();i++){
			TopicDetail commitDetail = commitList.get(i);
			Long id = commitDetail.getId();
			long newid = newids[i];
			String value = commitDetail.getValue();
			TopicDetail insertItem = null;
			
			if(id != null && id > 0){
				//说明是老记录，先找到匹配的
				for(TopicDetail exist : existList){
					if(exist.getId() == id.longValue()){
						insertItem = exist;
						insertItem.setCreateTime(null);
						insertItem.setUpdateTime(null);
						insertItem.setSn(null);
						break;
					}
				}
			}else{
				//新插入的记录
				insertItem = commitDetail;
			}
			
			insertItem.setId(newid);
			insertItem.setTopicId(topicId);
			
			//说明有修改
			if(value != null){
				if(ETEditorCmp.isPic(insertItem.getType())){
					//说明是图片修改
					UploadFile picFile = reqbody.getUploadFile(value);
					String picPath = CommonUtil.join("image/topic/",topicId,"/",newid,".",picFile.getExtendName());
					picPath = CCP.uploadImage(picFile, picPath,true);
					insertItem.setValue(picPath);
				}else if(ETEditorCmp.isText(insertItem.getType())){
					insertItem.setValue(commitDetail.getValue());
				}
			}
			
			insertList.add(insertItem);
			
		}
		//把老的都删除，再插入新的
		DBHelper.getDao().delete(TopicDetail.class, new DBCondition(TopicDetail.JField.topicId,topicId));
		DBHelper.getDao().insertBatch(insertList);
		return newids;
	}
	
	public static void clearIntroList(long topicId) throws Exception{
		DBHelper.getDao().delete(TopicDetail.class, new DBCondition(TopicDetail.JField.topicId,topicId));
	}
	
	
	public static boolean checkForPublish(Topic topic){
		//只有草稿状态的票券才能发布,否则需要报错
		if(topic.getStatus() != BaseConstantDefine.STATUS_NONE){
			throw ETUtil.buildException(BaseErrorDefine.SYS_OPEARTION_INVALID);
		}
		//检查票券信息完整性，如果有必要信息没有填完整需要报错
		if(topic.getUserId() == null){
			throw ETUtil.buildException(ErrorDefine.TOPIC_INFO_MISSING,"您还没有填写专题的作者");
		}
		if(NullUtil.isEmpty(topic.getPic())){
			throw ETUtil.buildException(ErrorDefine.TOPIC_INFO_MISSING,"您还没有上传专题封面照片");
		}
		if(NullUtil.isEmpty(topic.getName())){
			throw ETUtil.buildException(ErrorDefine.TOPIC_INFO_MISSING,"您还没有填写专题名称");
		}
		if(NullUtil.isEmpty(topic.getSummary())){
			throw ETUtil.buildException(ErrorDefine.TOPIC_INFO_MISSING,"您还没有填写专题摘要");
		}
		return true;
	}
}
