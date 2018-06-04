package com.xiesange.web.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.baseweb.wechat.define.WechatDefine;
import com.xiesange.baseweb.wechat.pojo.WxTempMessageParam;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.coupon.CouponRule;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.gen.dbentity.user.UserCoupon;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.sql.DBOrCondition;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.define.ConsDefine;
import com.xiesange.web.define.ConsDefine.COUPON_TRIGGERTYPE;
import com.xiesange.web.define.ErrorDefine;

public class CouponCmp {
	/**
	 * 根据id查询某张优惠券
	 * @param couponId
	 * @param jfs
	 * @return
	 * @throws Exception
	 */
	public static UserCoupon queryById(long couponId,BaseJField...jfs) throws Exception{
		UserCoupon coupon = DBHelper.getDao().queryById(UserCoupon.class, couponId,jfs);
		return coupon;
	}
	
	public static UserCoupon checkById(long couponId,BaseJField...jfs) throws Exception{
		UserCoupon coupon = queryById(couponId,jfs);
		if(coupon == null || coupon.getExpireTime().before(DateUtil.now())){
			throw ETUtil.buildException(ErrorDefine.ORDER_COUPON_EXPIRED);
		}
		return coupon;
	}
	
	/**
	 * 获取某个用户的优惠券。
	 * @param userId
	 * @param onlyValid，true只获取有效期内；false所有都获取
	 * @return
	 * @throws Exception
	 */
	public static List<UserCoupon> queryListByUserId(long userId,boolean onlyValid) throws Exception{
		//查出该用户的优惠券，未使用且未过期
		List<DBCondition> conds = new ArrayList<DBCondition>();
		conds.add(new DBCondition(UserCoupon.JField.userId,userId));
		if(onlyValid){
			//有效的，那需要没用过，且也没过期
			conds.add(new DBCondition(UserCoupon.JField.isUsed,0));
			conds.add(new DBCondition(UserCoupon.JField.expireTime,DateUtil.now(),DBOperator.GREAT_EQUALS));
		}
		
		QueryStatement st = new QueryStatement(UserCoupon.class,conds.toArray(new DBCondition[conds.size()]));
		st.appendOrderFieldDesc(UserCoupon.JField.createTime);//按照领取时间降序排列
		
		List<UserCoupon> couponList = DBHelper.getDao().query(st);
		
		if(NullUtil.isEmpty(couponList))
			return null;
		
		return couponList;
	}
	
	public static boolean canUse(UserCoupon coupon,Orders order){
		if(coupon.getPremise() != null && order.getOrigSum() < coupon.getPremise()){
			return false;
		}
		return true;
	}
	
	public static void transfer(UserCoupon coupon) throws Exception{
		if(coupon.getType() != null && coupon.getType() == ConsDefine.COUPON_TYPE.CASH.value()){
			coupon.addAttribute("value", ETUtil.parseFen2YuanStr(coupon.getValue(),false));
			coupon.setValue(null);
		}
		if(coupon.getExpireTime() != null){
			if(coupon.getExpireTime().before(DateUtil.offsetDate(DateUtil.now(), 1))){
				//判断是否过期，因为是到某天23:59:59，所以直接判断是否小于当前往后一天
				coupon.addAttribute("isExpired", 1);
			}
			
			coupon.addAttribute("expireTime", DateUtil.date2Str(coupon.getExpireTime(), DateUtil.DATE_FORMAT_EN_B_YYYYMMDD));
			coupon.setExpireTime(null);
		}
	}
	
	/**
	 * 计算应用某张优惠券后最终价格
	 * @param coupon
	 * @param totalSum
	 * @return
	 * @throws Exception
	 */
	public static long apply(UserCoupon coupon,long sum) throws Exception{
		if(coupon == null)
			return sum;
		if(coupon.getType() == ConsDefine.COUPON_TYPE.CASH.value()){
			//现金券
			sum = sum - coupon.getValue();
			/*if(result < 0){
				result = 0;
			}*/
			//return result;
		}else if(coupon.getType() == ConsDefine.COUPON_TYPE.PERCENT.value()){
			//折扣券
			sum = sum * coupon.getValue()/100;
		}
		if(sum < 0){
			sum = 0;
		}
		//orderEntity.setSum(totalSum);
		return sum;
	}
	
	/**
	 * 消费某张优惠券
	 * @param coupon
	 * @throws Exception
	 */
	public static void consumeCoupon(UserCoupon coupon) throws Exception{
		coupon.setIsUsed((short)1);
		coupon.setConsumeTime(DateUtil.now());
		DBHelper.getDao().updateById(coupon, coupon.getId());
	}
	
	/**
	 * 获取某个用户，根据兑换码计算出兑换规则，注意，已经兑换过的优惠规则不再重复兑换：
	 * 1、查询出通用兑换规则，即兑换码为空
	 * 2、查询出用户输入的兑换码所对应的优惠规则
	 * @param redeemCode
	 * @throws Exception 
	 */
	public static List<CouponRule> matchConponRules(long userId,String redeemCode,COUPON_TRIGGERTYPE triggerType) throws Exception{
		return DBHelper.getDao().query(CouponRule.class, 
			NullUtil.isEmpty(redeemCode) ? 
				new DBCondition(CouponRule.JField.redeemCode,0)
				:new DBOrCondition(
						new DBCondition(CouponRule.JField.redeemCode,0),
						new DBCondition(CouponRule.JField.redeemCode,redeemCode)
			),
			new DBCondition(CouponRule.JField.triggerType,triggerType.value()),
			new DBCondition(CouponRule.JField.status,1),
			new DBCondition(CouponRule.JField.id,
					new QueryStatement(UserCoupon.class,new DBCondition(UserCoupon.JField.userId,userId))
							.appendQueryField(UserCoupon.JField.ruleId),
					DBOperator.NOT_IN)
		);
	}
	
	public static List<UserCoupon> generateCoupons(long userId,String redeemCode,COUPON_TRIGGERTYPE triggerType) throws Exception{
		List<CouponRule> rules = CouponCmp.matchConponRules(userId,redeemCode,triggerType);
		if(NullUtil.isEmpty(rules)){
			return null;
		}
		List<UserCoupon> newCouponList = ClassUtil.newList();
		UserCoupon coupon = null;
		for(CouponRule rule : rules){
			coupon = new UserCoupon();
			coupon.setUserId(userId);
			coupon.setType(rule.getType());//类型
			coupon.setValue(rule.getValue());//优惠值
			coupon.setPremise(rule.getPremise());//使用条件
			Date expireDate = DateUtil.offsetDate(DateUtil.now(), rule.getValidity());
			expireDate = DateUtil.getDayEnd(expireDate);//到23:59:59结束
			coupon.setExpireTime(expireDate);
			coupon.setSrc(ConsDefine.COUPON_EVENT.DEMAND.value());
			coupon.setIsUsed((short)0);
			coupon.setRuleId(rule.getId());
			newCouponList.add(coupon);
			
		}
		DBHelper.getDao().insertBatch(newCouponList);
		
		return newCouponList;
	}
	
	public static String buildCouponText(UserCoupon coupon){
		String str = null;
		if(coupon.getType() == ConsDefine.COUPON_TYPE.PERCENT.value()){
			//折扣券
			str = (coupon.getValue()/10)+"折券";
		}else if(coupon.getType() == ConsDefine.COUPON_TYPE.CASH.value()){
			//现金券
			str = ETUtil.parseFen2YuanStr(coupon.getValue(),false)+"元券";
		}
		return str;
	}
	
	public static void sendCouponNotify(User user,UserCoupon coupon) throws Exception{
		//发送提醒
		/*NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.allocate_coupons, 
				new KeyValueHolder("name",NullUtil.isEmpty(user.getName())?"小伙伴":user.getName())
						.addParam("coupon", buildCouponText(coupon)),
				new NotifyTargetHolder().addMobile(user.getMobile())
		);*/
	}
	
	public static void appendCouponEventName(List<UserCoupon> couponList) throws Exception{
		if(NullUtil.isEmpty(couponList))
			return;
		Set<Long> ruleIds = ETUtil.buildEntityIdList(couponList, UserCoupon.JField.ruleId);
		if(NullUtil.isEmpty(ruleIds))
			return;
		List<CouponRule> ruleList = DBHelper.getDao().queryByIds(CouponRule.class, ruleIds);
		long ruleId = 0L;
		for(UserCoupon coupon : couponList){
			ruleId = coupon.getRuleId();
			if(ruleId == -1){
				coupon.addAttribute("eventName", "人工赠送");
				continue;
			}
			if(NullUtil.isNotEmpty(ruleList)){
				for(CouponRule rule : ruleList){
					if(ruleId == rule.getId()){
						coupon.addAttribute("eventName", rule.getName());
						break;
					}
				}
			}
		}
	}
	
	public static void sendExpireWxNotify(String openid,UserCoupon coupon) throws Exception{
		String tempid = WechatDefine.TEMPMSG_COUPON_EXPIRE;
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx32b6780d93247483&redirect_uri=http://www.xiesange.com/wechat/product/product.html?fromwx=1&response_type=code&scope=snsapi_base&state=1&connect_redirect=1#wechat_redirect";
		String couponText = CouponCmp.buildCouponText(coupon);
		
		WxTempMessageParam param = new WxTempMessageParam(WechatDefine.MESSAGE_COMMON.first.name(),"您有一张优惠券即将到期\r\n");
		param.addParam(WechatDefine.MESSAGE_COMMON.keyword1.name(), couponText);
		param.addParam(WechatDefine.MESSAGE_COMMON.keyword2.name(), DateUtil.date2Str(coupon.getExpireTime(), DateUtil.DATE_FORMAT_EN_B_YYYYMMDD)+"到期","#FF0000");
		param.addParam(WechatDefine.MESSAGE_COMMON.remark.name(), "\r\n蟹三哥,提供地道的东海野生梭子蟹，让海洋与家不再遥远~");
		//System.out.println("---------touser:"+toUser);
		//LogUtil.dump("---------toUser:", toUser);
		WechatCmp.sendTemplateMessage(openid, tempid, url, param);
	}
	
	public static void sendAllocateWxNotify(String openid,UserCoupon coupon) throws Exception{
		/*String tempid = WechatDefine.TEMPMSG_ID_COUPON_EXPIRE;
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx32b6780d93247483&redirect_uri=http://www.xiesange.com/wechat/product/product.html?fromwx=1&response_type=code&scope=snsapi_base&state=1&connect_redirect=1#wechat_redirect";
		String couponText = CouponCmp.buildCouponText(coupon);
		
		TemplateMessageParam param = new TemplateMessageParam(WechatDefine.MESSAGE_COMMON.first,"您有一张优惠券即将到期\r\n");
		param.addParam(WechatDefine.MESSAGE_COMMON.keyword1, couponText);
		param.addParam(WechatDefine.MESSAGE_COMMON.keyword2, DateUtil.date2Str(coupon.getExpireTime(), DateUtil.DATE_FORMAT_EN_B_YYYYMMDD)+"到期","#FF0000");
		param.addParam(WechatDefine.MESSAGE_COMMON.remark, "\r\n蟹三哥,提供地道的东海野生梭子蟹，让海洋与家不再遥远~");
		//System.out.println("---------touser:"+toUser);
		//LogUtil.dump("---------toUser:", toUser);
		WechatComponent.sendTemplateMessage(openid, tempid, url, param);*/
	}
}
