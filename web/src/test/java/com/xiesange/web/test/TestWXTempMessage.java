package com.xiesange.web.test;

import com.xiesange.baseweb.notify.NotifyCmp;
import com.xiesange.baseweb.notify.NotifyDefine;
import com.xiesange.baseweb.notify.NotifyTargetHolder;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.web.test.frame.TestCmp;

public class TestWXTempMessage {
	public static final String PATH_CONFIG = "/config.xml";
	public static final String PATH_CACHE = "/cache.xml";

	public static void main(String[] args) throws Exception {
		TestCmp.initLocalhost();
		
		NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.order_payed, 
				new KeyValueHolder("name","wuyujie")
						.addParam("order_code", "12345678")
						.addParam("order_sum", "100元"),
				new NotifyTargetHolder().addOpenid("og1OTwge1nWcSJNTFZEDNKCiik3w")
		);
		
		
		/*Date expireDate = DateUtil.offsetDate(DateUtil.now(),7);//7天之内过期的
		
		List<UserCoupon> couponList = DBHelper.getDao().query(UserCoupon.class,
				new DBCondition(UserCoupon.JField.isUsed,0),
				new DBCondition(UserCoupon.JField.expireTime,DateUtil.now(),DBOperator.GREAT_EQUALS),
				new DBCondition(UserCoupon.JField.expireTime,expireDate,DBOperator.LESS_EQUALS));
		if(NullUtil.isEmpty(couponList)){
			return;
		}
		Set<Long> userIds = ETUtil.buildEntityIdList(couponList, UserCoupon.JField.userId);
		List<User> userList = DBHelper.getDao().queryByIds(User.class, userIds, User.JField.id,User.JField.wechat);
		Set<Long> sended = ClassUtil.newSet();
		for(UserCoupon coupon : couponList){
			if(coupon.getUserId() != 1){
				continue;
			}
			if(sended.contains(coupon.getUserId())){
				continue;
			}
			for(User u : userList){
				if(u.getId().longValue() == coupon.getUserId()){
					sended.add(u.getId());
					if(NullUtil.isNotEmpty(u.getWechat())){
						CouponCmp.sendExpireWxNotify(u.getWechat(), coupon);
					}
					break;
				}
			}
		}*/
		//
		/*TemplateMessageReq req = new TemplateMessageReq(null,WechatDefine.TEMPMSG_ID_COUPON_EXPIRE,null);
		Map<String,TemplateMessageParam> data = ClassUtil.newMap();
		data.put(key, value);
		
		req.setTouser();
		req.setTemplate_id("YE9KvyRHdca98Xn3w93w2TuSUDgylxv9-CE3Jxtf11U");
		req.addData("first", "您有一张优惠券即将到期\r\n");
		req.addData("keyword1", "100");
		req.addData("keyword2", "2016-09-20到期","#FF0000");
		req.addData("remark", "\r\n蟹三哥,提供地道的东海野生梭子蟹，让海洋与家不再遥远~");
		WechatComponent.sendTemplateMessage(req);*/
	}
	
	/*private static void send(String toUser,UserCoupon coupon) throws Exception{
		String tempid = WechatDefine.TEMPMSG_ID_COUPON_EXPIRE;
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx32b6780d93247483&redirect_uri=http://www.xiesange.com/wechat/product/product.html?fromwx=1&response_type=code&scope=snsapi_base&state=1&connect_redirect=1#wechat_redirect";
		String couponText = CouponCmp.buildCouponText(coupon);
		
		TemplateMessageParam param = new TemplateMessageParam(WechatDefine.COUPON_EXPIRE.first,"您有一张优惠券即将到期\r\n");
		param.addParam(WechatDefine.COUPON_EXPIRE.keyword1, couponText);
		param.addParam(WechatDefine.COUPON_EXPIRE.keyword2, DateUtil.date2Str(coupon.getExpireTime(), DateUtil.DATE_FORMAT_EN_B_YYYYMMDD)+"到期","#FF0000");
		param.addParam(WechatDefine.COUPON_EXPIRE.remark, "\r\n蟹三哥,提供地道的东海野生梭子蟹，让海洋与家不再遥远~");
		//System.out.println("---------touser:"+toUser);
		//LogUtil.dump("---------toUser:", toUser);
		WechatComponent.sendTemplateMessage(toUser, tempid, url, param);
	}*/
}
