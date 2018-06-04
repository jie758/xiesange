package com.xiesange.web.test;

import java.util.List;

import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.RandomUtil;
import com.xiesange.gen.dbentity.temp.Temp;
import com.xiesange.gen.dbentity.trip.TripComment;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.orm.DBHelper;
import com.xiesange.web.test.frame.TestCmp;

public class GenUser {
	public static final String PATH_CONFIG = "/config.xml";
	public static final String PATH_CACHE = "/cache.xml";

	public static void main(String[] args) throws Exception {
		TestCmp.init114();
		
		List<Temp> comList = DBHelper.getDao().queryAll(Temp.class);
		
		if(NullUtil.isEmpty(comList)){
			return;
		}
		
		for(int i=0;i<comList.size();i++){
			Temp c = comList.get(i);
			User userEntity = new User();
			userEntity.setSrc((short)-1);
			userEntity.setRole(BaseConsDefine.USER_ROLE.NORMAL.value());
			//userEntity.setMobile(mobile);
			//userEntity.setAddress(address);
			userEntity.setName(c.getNickname());
			userEntity.setPic(c.getPic());
			userEntity.setDevice(i%2==0 ? "iphone":"android");
			userEntity.setOrderCount(RandomUtil.getRangeLong(1, 5));
			userEntity.setOrderSum(userEntity.getOrderCount()*RandomUtil.getRangeLong(100, 150));
			DBHelper.getDao().insert(userEntity);
		}
		
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
