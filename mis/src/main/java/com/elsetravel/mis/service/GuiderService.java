package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.im.IMCmp;
import com.elsetravel.baseweb.notify.NotifyCmp;
import com.elsetravel.baseweb.notify.NotifyDefine;
import com.elsetravel.baseweb.notify.NotifyTargetHolder;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.ParamHolder;
import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.guider.GuiderWithdrawApply;
import com.elsetravel.gen.dbentity.person.Person;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.gen.dbentity.user.UserBalanceDetail;
import com.elsetravel.mis.component.PersonCmp;
import com.elsetravel.mis.component.UserCmp;
import com.elsetravel.mis.define.ConstantDefine;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.FieldPair;
import com.elsetravel.orm.pojo.JoinQueryData;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;
@ETServiceAnno(name="guider",version="")
public class GuiderService extends AbstractService{
	/**
	 * 根据条件查询导游列表
	 * @param context
	 * 			nickname,根据姓名做模糊查询
	 * 			email,根据邮箱做模糊查询
	 * 			status,根据导游状态查询
	 * 			
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:10:43
	 */
	public ResponseBody queryList(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		String nickname = reqbody.getString(ParamDefine.User.nickname);
		String email = reqbody.getString(ParamDefine.User.email);
		Short status = reqbody.getShort(ParamDefine.Common.status);
		
		
		List<DBCondition> userConds = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(email)){
			userConds.add(new DBCondition(User.JField.email,"%"+email+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(nickname)){
			userConds.add(new DBCondition(User.JField.nickname,"%"+nickname+"%",DBOperator.LIKE));
		}
		List<DBCondition> guiderConds = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(userConds)){
			guiderConds.add(new DBCondition(Person.JField.id,new QueryStatement(
					User.class,
					userConds.toArray(new DBCondition[userConds.size()])
			).appendQueryField(User.JField.id),DBOperator.IN));
		}
		//加上状态条件
		if(status != null){
			guiderConds.add(new DBCondition(Person.JField.status,status));
		}else{
			guiderConds.add(new DBCondition(Person.JField.status,
					new Short[]{ConstantDefine.GUIDER_APPLY_APPROVING,ConstantDefine.GUIDER_APPLY_APPROVED},
					DBOperator.IN
			));
		}
		
		DBCondition[] condArr = guiderConds.toArray(new DBCondition[guiderConds.size()]);
		List<JoinQueryData> joinList = dao().queryJoin(
				new QueryStatement(Person.class,condArr)
					.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
					.appendOrderFieldDesc(User.JField.createTime)
					.appendJoin(User.class,new FieldPair(User.JField.id,Person.JField.id))
		);
		if(NullUtil.isEmpty(joinList))
			return null;
		
		Person person = null;
		User user = null;
		List<Person> guiderList = new ArrayList<Person>();
		for(JoinQueryData joinData : joinList){
			person = joinData.getResult(Person.class);
			user = joinData.getResult(User.class);
			//ETUtil.clearDBEntityExtraAttr(guider);
			guiderList.add(PersonCmp.merge(user, person));
			//UserCmp.transferUser(guider,true);
			//UserCmp.clearSensitiveInfo(guider);
		}
		
		long count = dao().queryCount(Person.class, condArr);
		
		return new ResponseBody("result",guiderList).addTotalCount(count);
	}
	
	/**
	 * 认证申请通过
	 * @param context
	 * 			guider_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:18:16
	 */
	public ResponseBody approveApply(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Person.guider_id);
		Long guiderId = context.getRequestBody().getLong(ParamDefine.Person.guider_id);
		
		User user = UserCmp.checkExistByUserId(guiderId);
		Person guider = PersonCmp.checkExistById(guiderId);
		
		if(guider.getStatus() != ConstantDefine.GUIDER_APPLY_APPROVING){
			//只有在认证申请审核状态下才能调用该接口
			throw ETUtil.buildInvalidOperException();
		}
		
		guider.setStatus(ConstantDefine.GUIDER_APPLY_APPROVED);
		
		dao().updateById(guider, guiderId);
		
		//发送命令通知，刷新导游app界面，需要放出导游特有的菜单功能
		IMCmp.sendCmdMessage(guider.getId(), IMCmp.CmdDefine.approve_guider);
		
		//发送通知，目标：导游
		NotifyCmp.sendTemplate(
			NotifyDefine.CodeDefine.approve_guider, 
			null,
			new NotifyTargetHolder().addUser(user)
		);
		
		return null;
	}
	/**
	 * 认证申请驳回
	 * @param context
	 * 			guider_id
	 * 			memo,可以填写驳回的相关备注
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:18:29
	 */
	public ResponseBody rejectApply(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Person.guider_id);
		Long guiderId = context.getRequestBody().getLong(ParamDefine.Person.guider_id);
		String memo = context.getRequestBody().getString(ParamDefine.Common.memo);
		
		User user = UserCmp.checkExistByUserId(guiderId);
		Person guider = PersonCmp.checkExistById(guiderId);
		
		if(guider.getStatus() != ConstantDefine.GUIDER_APPLY_APPROVING){
			//只有在认证申请审核状态下才能调用该接口
			throw ETUtil.buildInvalidOperException();
		}
		
		guider.setStatus(ConstantDefine.GUIDER_APPLY_NONE);//回到未认证状态
		dao().updateById(guider, guiderId);
		
		
		//发送通知，目标：导游
		NotifyCmp.sendTemplate(
			NotifyDefine.CodeDefine.reject_guider, 
			new ParamHolder(NotifyDefine.RejectGuider.reason.name(),memo),
			new NotifyTargetHolder().addUser(user)
		);
		
		return null;
	}
	
	/**
	 * 查询提现申请列表数据 
	 * @param context
	 * 			name,导游姓名，模糊查询
	 * 			email，导游email，模糊查询
	 * 			isApproved,申请状态，0-未审批，1-已审批
	 * 			
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:43:38
	 */
	public ResponseBody queryWithdrawList(MisRequestContext context) throws Exception{
		String nickname = context.getRequestBody().getString(ParamDefine.User.nickname);
		String email = context.getRequestBody().getString(ParamDefine.User.email);
		Short isApproved = context.getRequestBody().getShort(ParamDefine.GuiderWithdrawApply.is_approved);
		
		List<DBCondition> userconds = new ArrayList<DBCondition>();
		//List<DBCondition> guiderconds = new ArrayList<DBCondition>();
		
		if(NullUtil.isNotEmpty(email)){
			userconds.add(new DBCondition(User.JField.email,"%"+email+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(nickname)){
			userconds.add(new DBCondition(User.JField.nickname,"%"+nickname+"%",DBOperator.LIKE));
		}

		List<DBCondition> finalConds = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(userconds)){
			finalConds.add(new DBCondition(GuiderWithdrawApply.JField.userId,new QueryStatement(
					User.class,
					userconds.toArray(new DBCondition[userconds.size()])
			).appendQueryField(User.JField.id),DBOperator.IN));
		}
		
		if(isApproved != null){
			finalConds.add(new DBCondition(GuiderWithdrawApply.JField.isApproved,isApproved));
		}
		
		DBCondition[] finalCondArr = finalConds.toArray(new DBCondition[finalConds.size()]);
		List<GuiderWithdrawApply> applyList = dao().query(
			new QueryStatement(GuiderWithdrawApply.class,finalCondArr)
					.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
					.appendOrderFieldDesc(GuiderWithdrawApply.JField.createTime)
		);
		
		if(NullUtil.isEmpty(applyList)){
			return null;
		}
		
		for(GuiderWithdrawApply withdraw : applyList){
			withdraw.addAttribute("applyTime", withdraw.getCreateTime());
			withdraw.addAttribute("approveTime", withdraw.getUpdateTime());
			withdraw.addAttribute("sum", ETUtil.parseFen2Yuan(withdraw.getSum()));
			withdraw.setCreateTime(null);
			withdraw.setUpdateTime(null);
			withdraw.setSum(null);
		}
		//查询出导游列表
		Set<Long> guiderIds = ETUtil.buildEntityIdList(applyList,GuiderWithdrawApply.JField.userId);
		List<User> guiderlist = dao().query(User.class, new DBCondition(User.JField.id,guiderIds,DBOperator.IN));
		for(User guider : guiderlist){
			UserCmp.transferUser(guider);
		}
		
		long totalCount = dao().queryCount(GuiderWithdrawApply.class,finalCondArr);
		return new ResponseBody("withdraw_list",applyList)
					.add("guider_list", guiderlist)
					.addTotalCount(totalCount);
	}
	
	/**
	 * 提现申请通过
	 * @param context
	 * 			withdraw_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:18:16
	 */
	public ResponseBody approveWithdraw(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.GuiderWithdrawApply.withdraw_id);
		Long withdrawId = context.getRequestBody().getLong(ParamDefine.GuiderWithdrawApply.withdraw_id);
		
		GuiderWithdrawApply apply = dao().queryById(GuiderWithdrawApply.class, withdrawId);
		if(apply == null || apply.getIsApproved() == 1){
			return null;
		}
		apply.setIsApproved((short)1);
		
		dao().update(apply, 
			new DBCondition(GuiderWithdrawApply.JField.id,withdrawId),
			new DBCondition(GuiderWithdrawApply.JField.isApproved,0)
		);
		
		//创建余额变动明细记录
		UserBalanceDetail balance = new UserBalanceDetail();
		//提现的交易号组成规则是：提现申请日期_提现记录ID
		balance.setCode(DateUtil.formatDate(apply.getCreateTime(), DateUtil.DATE_FORMAT_YYYYMMDD)+"_"+apply.getId());
		balance.setName("提现");
		balance.setSum(-1*apply.getSum());//提现变动是负数
		balance.setUserId(apply.getUserId());//申请人
		balance.setType(ConstantDefine.BALANCEDETAIL_TYPE_WITHDRAW);
		dao().insert(balance);
		
		
		//发送通知，目标：导游
		User guider = UserCmp.queryUserById(apply.getUserId(), User.JField.mobile,User.JField.id,User.JField.email);
		NotifyCmp.sendTemplate(
			NotifyDefine.CodeDefine.approve_withdraw, 
			new ParamHolder(NotifyDefine.ApproveWithdraw.sum.name(),ETUtil.parseFen2YuanStr(apply.getSum().longValue())),
			new NotifyTargetHolder().addUser(guider)
		);
		return null;
	}
	
	
	
}
