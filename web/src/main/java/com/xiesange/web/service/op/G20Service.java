package com.xiesange.web.service.op;

import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.CCP;
import com.xiesange.baseweb.component.VCodeCmp;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.notify.mail.EMailUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.op.Op;
import com.xiesange.gen.dbentity.op.OpSignup;
import com.xiesange.gen.dbentity.op.OpVote;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.OpCmp;
import com.xiesange.web.define.ErrorDefine;
import com.xiesange.web.define.OpParamDefine;
@ETServiceAnno(name="op_g20",version="")
/**
 * G20海鲜大礼包
 * @author Think
 *
 */
public class G20Service extends AbstractService {
	private static final String OP_CODE_G20 = "G20";
	/**
	 * 报名G20海鲜大礼包
	 * @param context
	 * 			mobile,
	 * 			nickname,
	 * 			address,
	 * 			vcode
	 * @return
	 * @throws Exception
	 */
	public ResponseBody signup(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			OpParamDefine.Common.mobile,
			OpParamDefine.G20.address,
			OpParamDefine.Common.nickname,
			OpParamDefine.Common.vcode,
			OpParamDefine.Common.openid
		);
		
		String mobile = reqbody.getString(OpParamDefine.Common.mobile);
		String address = reqbody.getString(OpParamDefine.G20.address);
		String nickname = reqbody.getString(OpParamDefine.Common.nickname);
		String vcode = reqbody.getString(OpParamDefine.Common.vcode);
		String openid = reqbody.getString(OpParamDefine.Common.openid);
		//检验验证码
		VCodeCmp.checkMobileVCode(vcode, mobile);
		
		Op op = OpCmp.checkFinished(OP_CODE_G20);
		
		//一个手机号或者一个微信号只能报名一次
		OpCmp.checkSignupByMobileOrOpenid(op.getId(), mobile, openid);
		
		//如果投票者没有报名过，那么生成报名记录
		OpSignup signupEntity = new OpSignup();
		signupEntity.setOpId(op.getId());
		signupEntity.setOpenid(openid);
		signupEntity.setNickname(nickname);
		signupEntity.setMobile(mobile);
		signupEntity.setAddress(address);
		signupEntity.setVoteCount(0);
		signupEntity.setViewCount(0);
		signupEntity.setSharingCount(0);
		dao().insert(signupEntity);
		
		EMailUtil.sendHTML("wuyj@xiesange.com", "G20海鲜大礼包",nickname+","+mobile+","+address);
		
		return new ResponseBody("newid",signupEntity.getId());
	}
	
	/**
	 * 查询报名信息
	 * @param context
	 * 			signup_id,
	 * 			vote_openid
	 * @return
	 * @throws Exception
	 */
	public ResponseBody querySignup(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		Long signupId = reqbody.getLong(OpParamDefine.G20.signup_id);
		String openid = reqbody.getString(OpParamDefine.Common.openid);
		OpSignup signupEntity = null;
		
		Op op = OpCmp.queryOp(OP_CODE_G20);
		
		if(signupId != null){
			//qrcode界面的查询
			signupEntity = OpCmp.querySignup(signupId, OpSignup.JField.mobile,OpSignup.JField.nickname,OpSignup.JField.id);
		}else if(NullUtil.isNotEmpty(openid)){
			signupEntity = OpCmp.querySignupByMobileOrOpenid(op.getId(), null,openid);
			CCP.updateFieldNum(Op.JField.viewCount, 1, op.getId());
		}
		
		
		return new ResponseBody("signup",signupEntity);
		
	}
	
	/**
	 * 查询投票者的相关信息
	 * @param context
	 * 			signup_id,
	 * 			vote_openid,投票者openid
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryVoteSignup(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			OpParamDefine.Common.openid,
			OpParamDefine.G20.signup_id
		);
		Long signupId = reqbody.getLong(OpParamDefine.G20.signup_id);
		String openid = reqbody.getString(OpParamDefine.Common.openid);
		
		
		Op op = OpCmp.checkFinished(OP_CODE_G20);
		
		
		OpSignup signupEntity = OpCmp.querySignup(signupId, OpSignup.JField.openid,OpSignup.JField.mobile,OpSignup.JField.nickname,OpSignup.JField.id);
		boolean isSelf = signupEntity.getOpenid().equals(openid);//投票者是不是当前参赛者者本身
		
		OpSignup voteSignupEntity = isSelf ? signupEntity : OpCmp.querySignupByMobileOrOpenid(op.getId(), null, openid,OpSignup.JField.nickname,OpSignup.JField.id);
		
		CCP.updateFieldNum(OpSignup.JField.viewCount, 1, signupId);
		
		return new ResponseBody("signup",signupEntity)
						.add("voteSignup", voteSignupEntity)
					;
		
	}
	
	/**
	 * 投票。一个手机号或者一个微信号只能投一次
	 * 投票成功，投票者也会生成报名记录
	 * @param context
	 * 			mobile,
	 * 			nickname,
	 * 			address,
	 * 			vcode,
	 * 			signup_id,投票者所投的那位报名者id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody vote(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			OpParamDefine.Common.mobile,
			OpParamDefine.G20.address,
			OpParamDefine.Common.nickname,
			OpParamDefine.Common.vcode,
			OpParamDefine.Common.openid,
			OpParamDefine.G20.signup_id
		);
		
		String mobile = reqbody.getString(OpParamDefine.Common.mobile);
		String address = reqbody.getString(OpParamDefine.G20.address);
		String nickname = reqbody.getString(OpParamDefine.Common.nickname);
		String vcode = reqbody.getString(OpParamDefine.Common.vcode);
		String openid = reqbody.getString(OpParamDefine.Common.openid);
		Long voteSignupId = reqbody.getLong(OpParamDefine.G20.signup_id);//说明是投票类型的,这里是被投票者的报名记录
		//检验验证码
		VCodeCmp.checkMobileVCode(vcode, mobile);
		
		Op op = OpCmp.checkFinished(OP_CODE_G20);
		
		//一个手机号或者微信号对同一个用户只能投一票，投过了就要报错
		OpCmp.checkVoteByMobileOrOpenid(voteSignupId, mobile,openid);
		
		//报过名的用户不能参与投票
		OpSignup voteSignup = OpCmp.querySignupByMobileOrOpenid(op.getId(), mobile, openid, OpSignup.JField.id);
		if(voteSignup != null){
			throw ETUtil.buildException(ErrorDefine.OP_G20_CANNOT_VOTE);
		}
		
		OpVote voteEntity = new OpVote();
		voteEntity.setMobile(mobile);
		voteEntity.setNickname(nickname);
		voteEntity.setOpenid(openid);
		voteEntity.setSignupId(voteSignupId);
		voteEntity.setOpId(op.getId());
		dao().insert(voteEntity);
		
		CCP.updateFieldNum(OpSignup.JField.voteCount, 1,voteSignupId);//被投票人投票数+1
		
		//一个手机号或者一个微信号只能报名一次
		OpSignup signupEntity = OpCmp.querySignupByMobileOrOpenid(op.getId(), mobile,openid);
		if(signupEntity == null){
			//如果投票者没有报名过，那么生成报名记录
			signupEntity = new OpSignup();
			signupEntity.setOpId(op.getId());
			signupEntity.setOpenid(openid);
			signupEntity.setNickname(nickname);
			signupEntity.setMobile(mobile);
			signupEntity.setAddress(address);
			signupEntity.setVoteCount(0);
			signupEntity.setSharingCount(0);
			signupEntity.setViewCount(0);
			dao().insert(signupEntity);
		}
		EMailUtil.sendHTML("wuyj@xiesange.com", "G20海鲜大礼包",nickname+","+mobile+","+address);
		return new ResponseBody("newid",signupEntity.getId());
	}
	
	/**
	 * 没分享一次，对应的参与者分享数+1
	 * @param context
	 * 			signup_id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody share(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		/*RequestUtil.checkEmptyParams(context.getRequestBody(),
			OpParamDefine.G20.signup_id
		);*/
		Long signupId = reqbody.getLong(OpParamDefine.G20.signup_id);//说明是投票类型的,这里是被投票者的报名记录
		if(signupId == null){
			//分享到是主页
			Op op = OpCmp.queryOp(OP_CODE_G20);
			if(op == null)
				return null;
			CCP.updateFieldNum(Op.JField.sharingCount, 1,op.getId());//被投票人投票数+1
		}else{
			//分享到是某个参赛者拉票页
			CCP.updateFieldNum(OpSignup.JField.sharingCount, 1,signupId);//被投票人投票数+1
		}
		
		return null;
	}
	
	/**
	 * 查询某个参赛者投票列表
	 * @param context
	 * 			signup_id
	 * 			openid
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryVoteList(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			OpParamDefine.G20.signup_id,
			OpParamDefine.Common.openid
		);
		long signupId = reqbody.getLong(OpParamDefine.G20.signup_id);reqbody.getLong(OpParamDefine.G20.signup_id);
		String openId = reqbody.getString(OpParamDefine.Common.openid);
		QueryStatement st = new QueryStatement(OpVote.class, 
				new DBCondition(OpVote.JField.signupId,signupId),
				new DBCondition(OpVote.JField.openid,openId));
		
		st.appendOrderFieldDesc(OpVote.JField.createTime)
		  .appendQueryField(OpVote.JField.nickname,OpVote.JField.createTime);
		List<OpVote> voteList = dao().query(st);
		
		if(NullUtil.isNotEmpty(voteList)){
			for(OpVote vote : voteList){
				vote.addAttribute("createTime", DateUtil.date2Str(vote.getCreateTime(), DateUtil.DATE_FORMAT_EN_B_YYYYMMDD));
				vote.setCreateTime(null);
			}
		}
		
		return new ResponseBody("voteList",voteList);
	}
	
}
