package com.xiesange.web.component;

import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.VCodeCmp;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.op.Op;
import com.xiesange.gen.dbentity.op.OpSignup;
import com.xiesange.gen.dbentity.op.OpVote;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOrCondition;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.define.ErrorDefine;
import com.xiesange.web.define.OpParamDefine;

public class OpCmp {
	/**
	 * 创建运营主体记录
	 * @param code
	 * @param name
	 * @param expireTime
	 * @return
	 * @throws Exception
	 */
	public static Op createOp(String code,String name,String expireTime) throws Exception{
		Op op = new Op();
		op.setCode(code);
		op.setName(name);
		op.setExpireDate(DateUtil.str2Date(expireTime, DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS));
		op.setViewCount(0);
		return op;
	}
	
	public static Op queryOp(String code) throws Exception{
		return DBHelper.getDao().querySingle(Op.class, new DBCondition(Op.JField.code,code));
	}
	
	
	public static Op checkFinished(String code) throws Exception{
		Op op = queryOp(code);
		if(isFinished(op)){
			throw ETUtil.buildException(ErrorDefine.OP_EXPIRED);
		}
		return op;
	}
	
	public static void checkFinished(Op op){
		if(isFinished(op)){
			throw ETUtil.buildException(ErrorDefine.OP_EXPIRED);
		}
	}
	
	/**
	 * 当前活动是否结束报名期
	 * @param gradu
	 * @return
	 * @author Wilson 
	 * @date 下午12:01:17
	 */
	public static boolean isFinished(Op op){
		return op == null || op.getExpireDate().before(DateUtil.now());
	}
	
	
	/**
	 * 根据手机号查询某个运营活动下的报名记录
	 * @param opId
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public static OpSignup querySignupByMobileOrOpenid(long opId,String mobile,String openid,BaseJField...jfs) throws Exception{
		DBCondition cond = null;
		if(NullUtil.isNotEmpty(mobile) && NullUtil.isNotEmpty(openid)){
			cond = new DBOrCondition(
					new DBCondition(OpSignup.JField.mobile,mobile),
					new DBCondition(OpSignup.JField.openid,openid)
				);
		}else if(NullUtil.isNotEmpty(mobile) && NullUtil.isEmpty(openid)){
			cond = new DBCondition(OpSignup.JField.mobile,mobile);
		}else if(NullUtil.isEmpty(mobile) && NullUtil.isNotEmpty(openid)){
			cond = new DBCondition(OpSignup.JField.openid,openid);
		}
		QueryStatement st = new QueryStatement(OpSignup.class,
				new DBCondition(OpSignup.JField.opId,opId),
				cond
		);
		st.appendRange(0, 1).appendOrderFieldDesc(OpSignup.JField.createTime);
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
		}
		List<OpSignup> list = (List<OpSignup>)st.execute();
		return NullUtil.isEmpty(list) ? null : list.get(0);
	}
	
	public static OpSignup checkSignupByMobileOrOpenid(long opId,String mobile,String openid,BaseJField...jfs) throws Exception{
		OpSignup signupEntity = querySignupByMobileOrOpenid(opId,mobile,openid,jfs);
		
		if(signupEntity != null){
			//已经报过名，不能重复报名
			throw ETUtil.buildException(ErrorDefine.OP_SIGNUP_DUPLICATE);
		}
		return signupEntity;
	}
	
	public static OpSignup querySignup(long signupId,BaseJField...jfs) throws Exception{
		return DBHelper.getDao().queryById(OpSignup.class, signupId,jfs);
	}
	
	/**
	 * 一个手机号或者一个微信号只能投票一次
	 * @param signupId
	 * @param mobile
	 * @param openid
	 * @throws Exception
	 */
	public static OpVote queryVoteByMobileOrOpenid(long signupId,String mobile,String openid,BaseJField...jfs) throws Exception{
		
		DBCondition cond = null;
		if(NullUtil.isNotEmpty(mobile) && NullUtil.isNotEmpty(openid)){
			cond = new DBOrCondition(
					new DBCondition(OpVote.JField.mobile,mobile),
					new DBCondition(OpVote.JField.openid,openid)
				);
		}else if(NullUtil.isNotEmpty(mobile) && NullUtil.isEmpty(openid)){
			cond = new DBCondition(OpVote.JField.mobile,mobile);
		}else if(NullUtil.isEmpty(mobile) && NullUtil.isNotEmpty(openid)){
			cond = new DBCondition(OpVote.JField.openid,openid);
		}
		QueryStatement st = new QueryStatement(OpVote.class,
				new DBCondition(OpVote.JField.signupId,signupId),
				cond
		);
		st.appendRange(0, 1);
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
		}
		List<OpVote> list = (List<OpVote>)st.execute();
		return NullUtil.isEmpty(list) ? null : list.get(0);
		
	}
	
	public static OpVote checkVoteByMobileOrOpenid(long signupId,String mobile,String openid,BaseJField...jfs) throws Exception{
		OpVote vote = queryVoteByMobileOrOpenid(signupId, mobile, openid, jfs);
		
		if(vote != null){
			//已经投票过，不能重复投票
			throw ETUtil.buildException(ErrorDefine.OP_VOTE_DUPLICATE);
		}
		return vote;
	}
}
