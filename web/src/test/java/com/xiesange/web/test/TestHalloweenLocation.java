package com.xiesange.web.test;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.FileUtil;
import com.xiesange.gen.dbentity.base.BaseEnum;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.web.test.frame.TestCmp;

public class TestHalloweenLocation {
	public static final String PATH_CONFIG = "classpath:/halloween_location.properties";

	public static void main(String[] args) throws Exception {
		TestCmp.init114();
		
		DBHelper.getDao().delete(BaseEnum.class, new DBCondition(BaseEnum.JField.typeId,1));
		
		Properties p = FileUtil.loadProperties(PATH_CONFIG);
		
		Iterator<Entry<Object,Object>> it = p.entrySet().iterator();
    	String key = null;
    	List<BaseEnum> enumList = ClassUtil.newList();
    	while(it.hasNext()){
    		Entry<Object,Object> entry = it.next();
    		key = (String)entry.getKey();
    		
    		BaseEnum enumEntity = new BaseEnum();
    		enumEntity.setName(key);
    		enumEntity.setTypeId(1L);
    		enumList.add(enumEntity);
    	}
		
    	DBHelper.getDao().insertBatch(enumList);
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
