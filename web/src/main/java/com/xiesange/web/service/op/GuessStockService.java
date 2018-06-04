package com.xiesange.web.service.op;

import java.util.Date;
import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.op.Op;
import com.xiesange.gen.dbentity.op.OpSignup;
import com.xiesange.gen.dbentity.op.OpVote;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.OpCmp;
import com.xiesange.web.define.ErrorDefine;
import com.xiesange.web.define.OpParamDefine;
@ETServiceAnno(name="op_guess_stock",version="")
/**
 * G20海鲜大礼包
 * @author Think
 *
 */
public class GuessStockService extends AbstractService {
	private static final String OP_CODE = "guess_stock";
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
			OpParamDefine.Common.ext1,
			OpParamDefine.Common.name
		);
		
		String mobile = reqbody.getString(OpParamDefine.Common.mobile);
		String num = reqbody.getString(OpParamDefine.Common.ext1);//上证指数
		String name = reqbody.getString(OpParamDefine.Common.name);
		String openid = context.getAccessUser().getWechat();
		
		//提交时间为10:00~14:30
		String now = DateUtil.now_yyyy_mm_dd();
		Date startTime = DateUtil.str2Date(now+" 10:00:00");
		Date endTime =  DateUtil.str2Date(now+" 14:30:00");
		if(context.getRequestDate().before(startTime) || context.getRequestDate().after(endTime)){
			throw ETUtil.buildException(ErrorDefine.OP_SIGNUP_TIME, "10:00 ~ 14:30");
		}
		
		Op op = OpCmp.checkFinished(OP_CODE);
		
		//一个手机号或者一个微信号，一天只能报名一次
		OpSignup signupEntity = OpCmp.querySignupByMobileOrOpenid(op.getId(),mobile,openid);
		String createTime = signupEntity==null?null:DateUtil.date2Str(signupEntity.getCreateTime(), DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
		if(signupEntity != null && createTime.equals(now)){
			//已经报过名，不能重复报名
			throw ETUtil.buildException(ErrorDefine.OP_SIGNUP_DUPLICATE);
		}
		
		//OpCmp.checkSignupByMobileOrOpenid(op.getId(), mobile, openid);
		
		//如果投票者没有报名过，那么生成报名记录
		signupEntity = new OpSignup();
		signupEntity.setOpId(op.getId());
		signupEntity.setOpenid(openid);
		signupEntity.setNickname(name);
		signupEntity.setMobile(mobile);
		signupEntity.setExt1(num);
		signupEntity.setVoteCount(0);
		signupEntity.setViewCount(0);
		signupEntity.setSharingCount(0);
		dao().insert(signupEntity);
		
		return new ResponseBody("newid",signupEntity.getId());
	}
	
	/**
	 * 查询参赛者列表
	 * @param context
	 * 			signup_id
	 * 			openid
	 * @return
	 * @throws Exception
	 */
	public ResponseBody querySignupList(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		//long signupId = reqbody.getLong(OpParamDefine.G20.signup_id);reqbody.getLong(OpParamDefine.G20.signup_id);
		//String openId = reqbody.getString(OpParamDefine.Common.openid);
		
		Op op = OpCmp.queryOp(OP_CODE);
		
		
		Date begin = DateUtil.getDayBegin(context.getRequestDate());
		Date end = DateUtil.getDayEnd(context.getRequestDate());
		
		QueryStatement st = new QueryStatement(OpSignup.class, 
				new DBCondition(OpSignup.JField.opId,op.getId()),
				new DBCondition(OpSignup.JField.createTime,begin,DBOperator.GREAT_EQUALS),
				new DBCondition(OpSignup.JField.createTime,end,DBOperator.LESS_EQUALS));
		
		st.appendOrderFieldDesc(OpSignup.JField.createTime)
		  .appendQueryField(OpSignup.JField.nickname,OpSignup.JField.mobile,OpSignup.JField.ext1,OpSignup.JField.createTime);
		List<OpSignup> signupList = dao().query(st);
		
		if(NullUtil.isNotEmpty(signupList)){
			for(OpSignup signup : signupList){
				signup.setMobile(ETUtil.maskMobile(signup.getMobile()));
				signup.setNickname(ETUtil.maskName(signup.getNickname()));
				//vote.addAttribute("createTime", DateUtil.date2Str(vote.getCreateTime(), DateUtil.DATE_FORMAT_EN_B_YYYYMMDD));
				//vote.setCreateTime(null);
			}
		}
		
		return new ResponseBody("signupList",signupList);
	}
}
