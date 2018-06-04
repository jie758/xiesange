package com.xiesange.web.service;

import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.VCodeCmp;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.gen.dbentity.user.UserCoupon;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.CouponCmp;
import com.xiesange.web.define.ConsDefine.COUPON_EVENT;
import com.xiesange.web.define.ConsDefine.COUPON_TRIGGERTYPE;
import com.xiesange.web.define.ParamDefine;

@ETServiceAnno(name = "coupon", version = "")
/**
 * 优惠券相关服务类
 * @author Wilson 
 * @date 上午9:48:42
 */
public class CouponService extends AbstractService {
	/**
	 * 登录用户主动领取优惠券
	 * @param context
	 * 			mobile,
	 * 			vcode,
	 * 			redeem_code
	 * @return
	 * @throws Exception
	 */
	public ResponseBody demand(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Common.mobile);
		
		String mobile = reqbody.getString(ParamDefine.Common.mobile);
		String vcode = reqbody.getString(ParamDefine.Common.vcode);
		String redeemCode = reqbody.getString(ParamDefine.Coupon.redeem_code);
		
		User loginUser = context.getAccessUser();
		if(!mobile.equals(loginUser.getMobile())){
			//如果传入的号码与当前用户资料里的号码不一样，需要验证
			RequestUtil.checkEmptyParams(reqbody, ParamDefine.Common.vcode);
			VCodeCmp.checkMobileVCode(vcode, mobile);
			if(NullUtil.isEmpty(loginUser.getMobile())){
				loginUser.setMobile(mobile);
				dao().updateById(loginUser, loginUser.getId());
			}
		}
		long userId = context.getAccessUserId();
		List<UserCoupon> couponList = ClassUtil.newList();
		List<UserCoupon> newCustCoupons = CouponCmp.generateCoupons(userId,COUPON_EVENT.NEW_CUST.name(),COUPON_TRIGGERTYPE.SYS);
		if(NullUtil.isNotEmpty(newCustCoupons)){
			for(UserCoupon cp : newCustCoupons){
				ETUtil.clearDBEntityExtraAttr(cp);
				CouponCmp.transfer(cp);
			}
			couponList.addAll(newCustCoupons);
		}
		List<UserCoupon> newCouponList = CouponCmp.generateCoupons(userId,redeemCode,COUPON_TRIGGERTYPE.CUST);
		if(NullUtil.isNotEmpty(newCouponList)){
			for(UserCoupon cp : newCouponList){
				ETUtil.clearDBEntityExtraAttr(cp);
				CouponCmp.transfer(cp);
			}
			couponList.addAll(newCouponList);
		}
		
		return new ResponseBody("couponList",couponList);
	}
	
	/**
	 * 查询当前用户优惠券列表
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryList(WebRequestContext context) throws Exception{
		List<UserCoupon> couponList = CouponCmp.queryListByUserId(context.getAccessUserId(), false);
		if(NullUtil.isEmpty(couponList)){
			return null;
		}
		for(UserCoupon coupon : couponList){
			CouponCmp.transfer(coupon);
			ETUtil.clearDBEntityExtraAttr(coupon,UserCoupon.JField.createTime);
		}
		
		
		return new ResponseBody("couponList",couponList);
	}
	
}
