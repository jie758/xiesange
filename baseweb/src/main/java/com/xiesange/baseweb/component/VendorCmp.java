package com.xiesange.baseweb.component;

import java.util.Date;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseConsDefine.VendorConfigType;
import com.xiesange.core.util.DateUtil;
import com.xiesange.gen.dbentity.vendor.VendorConfig;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
/**
 * 第三方平台的一些配置参数，比如微信token，融云token等
 * 这些参数不用每次都向第三方平台去获取，因为都存在有效期，只有过了有效期才需要去获取刷新
 * @author Wilson 
 * @date 下午1:16:30
 */
public class VendorCmp {
	/**
	 * 获取配置记录
	 * @param typeEnum
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:16:20
	 */
	public static VendorConfig getConfig(BaseConsDefine.VendorConfigType typeEnum) throws Exception{
		VendorConfig configEntity = DBHelper.getDao().querySingle(
				VendorConfig.class, 
				new DBCondition(VendorConfig.JField.type,typeEnum.name())
		);
		
		return configEntity;
	}
		
	public static VendorConfig updateConfig(VendorConfigType typeEnum,VendorConfig configEntity,String value,int expireIn) throws Exception{
		Date expireTime = null;
		if(expireIn > 0){
			//预留给20s，因为网络交互也有时间，不能把时间卡的太死
			expireTime = DateUtil.offsetSecond(DateUtil.now(),expireIn-20);
		}else{
			//小于0表示是永久有效的
			expireTime = ETUtil.getPermanentDate();
		}
		
		if(configEntity == null){
			//说明之前没有查询过，新插入
			configEntity = new VendorConfig();
			configEntity.setType(typeEnum.name());
			configEntity.setValue(value);
			configEntity.setExpireTime(expireTime);
			
			DBHelper.getDao().insert(configEntity);
		}else{
			long id = configEntity.getId();
			//已经过期了,更新失效时间
			configEntity = new VendorConfig();
			configEntity.setExpireTime(expireTime);
			configEntity.setValue(value);
			DBHelper.getDao().updateById(configEntity,id);
		}
		
		return configEntity;
	}
	
}
