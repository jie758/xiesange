package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.mis.MisStaff;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.gen.dbentity.user.UserSharing;
import com.elsetravel.mis.component.SharingCmp;
import com.elsetravel.mis.component.StaffCmp;
import com.elsetravel.mis.component.UserCmp;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;
/**
 * 达人分享会
 * @author Wilson 
 * @date 下午1:44:07
 */
@ETServiceAnno(name="sharing",version="")
public class SharingService extends AbstractService {
	public ResponseBody getNewId(MisRequestContext context) throws Exception{
		long newid = dao().getSequence(UserSharing.class);
		return new ResponseBody("newid",newid);
	}
	
	/**
	 * 新建或者修改分享会
	 * id为空或者负数表示新建。
	 * @param context
	 * 			sharing_id,
	 * 			name,
	 * 			cover_pic,
	 * 			discussion_id,必填，讨论组id
	 * 			start_time,开始时间，格式yyyy-mm-dd hh:mm:ss
	 * 			user_id,达人id,
	 * 			detail_intro,达人分享会详情，html串
	 * 			host_staff_id,主持人员工id
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午2:34:40
	 */
	public ResponseBody save(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		Long sharingId = reqbody.getLong(ParamDefine.Sharing.sharing_id);
		boolean isNew = sharingId==null || sharingId < 0;
		if(isNew){
			//新增的时候这些字段必填
			RequestUtil.checkEmptyParams(reqbody, 
					ParamDefine.Common.name,
					ParamDefine.Sharing.start_time,
					ParamDefine.Sharing.cover_pic,
					ParamDefine.User.user_id,
					ParamDefine.Sharing.host_staff_id
			);
		}
		String name = reqbody.getString(ParamDefine.Common.name);
		String startTime = reqbody.getString(ParamDefine.Sharing.start_time);
		String detailIntro = reqbody.getString(ParamDefine.Sharing.detail_intro);
		String coverPic = reqbody.getString(ParamDefine.Sharing.cover_pic);
		Long userId = reqbody.getLong(ParamDefine.User.user_id);
		Long hostStaffId = reqbody.getLong(ParamDefine.Sharing.host_staff_id);
		
		UserSharing sharingEntity = null;
		if(!isNew){
			sharingEntity = SharingCmp.checkExist(sharingId);
		}else{
			sharingEntity = new UserSharing();
			sharingEntity.setStatus(BaseConstantDefine.STATUS_NONE);//默认是草稿状态
			if(sharingId < 0){
				sharingEntity.setId(-1*sharingId);
			}
		}
		if(userId != null){
			sharingEntity.setUserId(userId);
		}
		if(hostStaffId != null){
			sharingEntity.setHostStaffId(hostStaffId);
		}
		if(NullUtil.isNotEmpty(name)){
			sharingEntity.setName(name);
		}
		if(NullUtil.isNotEmpty(startTime)){
			sharingEntity.setStartTime(DateUtil.parseDate(startTime,DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS));
		}
		if(NullUtil.isNotEmpty(coverPic)){
			sharingEntity.setCoverPic(coverPic);
		}
		if(NullUtil.isNotEmpty(detailIntro)){
			sharingEntity.setDetailIntro(detailIntro);
		}
		
		if(isNew){
			dao().insert(sharingEntity);
		}else{
			dao().updateById(sharingEntity, sharingId);
		}
		return null;
	}
	
	/**
	 * 发布分享会。
	 * 发布时需要先创建讨论组，创建成功需要把讨论组id传过来
	 * @param context
	 * 			sharing_id,
	 * 			discussion_id,string,讨论组id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:04:57
	 */
	public ResponseBody publish(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
				ParamDefine.Sharing.sharing_id,
				ParamDefine.Sharing.discussion_id
		);
		String discussionId = reqbody.getString(ParamDefine.Sharing.discussion_id);
		long sharingId = reqbody.getLong(ParamDefine.Sharing.sharing_id);
		
		UserSharing sharingEntity = SharingCmp.checkExist(sharingId);
		if(sharingEntity.getStatus() == BaseConstantDefine.STATUS_EFFECTIVE){
			throw ETUtil.buildInvalidOperException("该分享会已经是发布状态");
		}
		if(NullUtil.isEmpty(sharingEntity.getName())){
			throw ETUtil.buildInvalidOperException("请填写分享会名称");
		}
		if(NullUtil.isEmpty(sharingEntity.getCoverPic())){
			throw ETUtil.buildInvalidOperException("请填写分享会封面图片");
		}
		if(NullUtil.isEmpty(sharingEntity.getDetailIntro())){
			throw ETUtil.buildInvalidOperException("请填写分享会图文明细");
		}
		if(sharingEntity.getUserId() == null){
			throw ETUtil.buildInvalidOperException("请填写分享会达人");
		}
		if(sharingEntity.getStartTime() == null){
			throw ETUtil.buildInvalidOperException("请填写分享会开始时间");
		}

		sharingEntity.setDiscussionId(discussionId);
		sharingEntity.setStatus(BaseConstantDefine.STATUS_EFFECTIVE);//上线状态
		
		dao().updateById(sharingEntity, sharingId);
		return null;
	}
	
	/**
	 * 达人分享会下线。下线后该分享会就不能再参与
	 * @param context
	 * 			sharing_id,必填 
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:55:39
	 */
	public ResponseBody offline(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Sharing.sharing_id);
		long sharingId = reqbody.getLong(ParamDefine.Sharing.sharing_id);
		
		UserSharing sharingEntity = SharingCmp.checkExist(sharingId);
		if(sharingEntity.getStatus() == BaseConstantDefine.STATUS_NONE)
			return null;
		sharingEntity.setStatus(BaseConstantDefine.STATUS_NONE);
		dao().updateById(sharingEntity, sharingId);
		return null;
	}
	
	/**
	 * 删除达人分享会,可以删除多个，
	 * @param context
	 * 			sharing_ids，多个的话用英文逗号分隔
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:24:02
	 */
	public ResponseBody remove(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Sharing.sharing_ids);
		String sharingIds = reqbody.getString(ParamDefine.Sharing.sharing_ids);
		String[] idArr = sharingIds.split(",");
		
		if(NullUtil.isEmpty(idArr)){
			return null;
		}
		dao().delete(UserSharing.class,
			new DBCondition(UserSharing.JField.id,idArr,DBOperator.IN)
		);
		
		return null;
	}
	
	/**
	 * 查询某个分享会详情页
	 * @param context
	 * 			
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:30:02
	 */
	public ResponseBody queryDetail(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		long sharingId = reqbody.getLong(ParamDefine.Sharing.sharing_id);
		
		UserSharing sharingEntity = SharingCmp.checkExist(sharingId);
		
		SharingCmp.transfer(sharingEntity);
		
		MisStaff hostStaff = StaffCmp.queryById(sharingEntity.getHostStaffId());
		hostStaff.setPassword(null);//防止泄露密码
		
		User guider = UserCmp.queryUserById(sharingEntity.getUserId(), User.JField.id,User.JField.nickname);
		
		return new ResponseBody("sharing",sharingEntity)
					.add("host", hostStaff)
					.add("guider", guider);
	}
	
	/**
	 * 查询分享列表,按start_time由近到远排列
	 * @param context
	 * 			status,99上线状态，0下线状态，不传则查全部
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:43:20
	 */
	public ResponseBody queryList(MisRequestContext context) throws Exception{
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		Short status = context.getRequestBody().getShort(ParamDefine.Common.status);
		List<DBCondition> conds = new ArrayList<DBCondition>();
		
		if(NullUtil.isNotEmpty(name)){
			conds.add(new DBCondition(UserSharing.JField.name,"%"+name+"%",DBOperator.LIKE));
		}
			
		if(status != null){
			conds.add(new DBCondition(UserSharing.JField.status,status));
		}
		DBCondition[] condArr = conds.toArray(new DBCondition[conds.size()]);
		List<UserSharing> sharingList = dao().query(
			new QueryStatement(UserSharing.class,condArr)
				.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
				.appendOrderFieldDesc(UserSharing.JField.startTime)
		);
		if(NullUtil.isEmpty(sharingList))
			return null;
		
		Set<Long> guiderIds = new HashSet<Long>();
		Set<Long> staffIds = new HashSet<Long>();
		for(UserSharing sharing : sharingList){
			guiderIds.add(sharing.getUserId());
			staffIds.add(sharing.getHostStaffId());
			sharing.setCoverPic(ETUtil.buildPicUrl(sharing.getCoverPic()));
		}
		
		List<User> guiderList = UserCmp.queryUserByIds(guiderIds, User.JField.id,User.JField.nickname);
		List<User> hostList = dao().query(
				new QueryStatement(MisStaff.class,new DBCondition(MisStaff.JField.id,staffIds,DBOperator.IN))
						.appendQueryField(MisStaff.JField.id,MisStaff.JField.name,MisStaff.JField.appUserId)
		);
		
		
		long count = dao().queryCount(UserSharing.class, condArr);//记录总数
		ResponseBody body = new ResponseBody("sharingList",sharingList)
									.add("guiderList", guiderList)
									.add("hostList", hostList)
									.addTotalCount(count);
		return body;
	}
	
}
