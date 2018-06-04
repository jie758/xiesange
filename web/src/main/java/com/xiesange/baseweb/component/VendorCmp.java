package com.xiesange.baseweb.component;

import java.util.Date;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.wechat.define.WechatDefine.VendorConfigType;
import com.xiesange.core.util.DateUtil;
import com.xiesange.gen.dbentity.wx.WxConfig;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
/**
 * 第三方平台的一些配置参数，比如微信token，融云token等
 * 这些参数不用每次都向第三方平台去获取，因为都存在有效期，只有过了有效期才需要去获取刷新
 * @author Wilson 
 * @date 下午1:16:30
 */
public class VendorCmp {
	
	
}
