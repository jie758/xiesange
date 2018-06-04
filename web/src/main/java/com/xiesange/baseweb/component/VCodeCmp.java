package com.xiesange.baseweb.component;

import java.util.Date;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.notify.NotifyCmp;
import com.xiesange.baseweb.notify.NotifyDefine;
import com.xiesange.baseweb.notify.NotifyTargetHolder;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.RandomUtil;
import com.xiesange.gen.dbentity.notify.NotifyVcode;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;

public class VCodeCmp {
	/**
	 * 发送手机验证码
	 * @param mobile
	 * @param zone
	 * @param type
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:19:15
	 */
	public static String sendVCode(String mobile,int zone,String expireIn) throws Exception{
		//生成验证码
		String vcode = VCodeCmp.generateVCode(zone+mobile, 4, expireIn);
		LogUtil.getLogger(VCodeCmp.class).debug("vcode : "+vcode);
		//发送手机验证码
		NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.vcode, 
				new KeyValueHolder("vcode",vcode), 
				new NotifyTargetHolder().addMobile(mobile, zone)
		);
		return vcode;
	}
	
	
	/**
	 * 检查某个短信验证码的有效性，和validateVCode逻辑一致，只是如果该校验码无效，会抛出错误。
	 * 验证过一次后该验证码就会失效了
	 * @param vcode,验证码
	 * @param target，手机号码或者邮箱
	 * @param type，验证码类型
	 * @return
	 * @throws Exception
	 */
	public static void checkMobileVCode(String vcode,String mobile) throws Exception{
		checkMobileVCode(vcode,mobile,(short)86);
	}	
	public static void checkMobileVCode(String vcode,String mobile,short zone) throws Exception{
		mobile = zone+mobile;
		NotifyVcode notifyV = queryVCode(vcode,mobile);
		//查询过一次后就要把当前验证码删除掉
		if(notifyV != null){
			DBHelper.getDao().deleteById(NotifyVcode.class,notifyV.getId());
		}else{
			expireVCode(vcode, mobile);
		}
		if(/*!vcode.equals("8888") && */notifyV == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_VCODE_INVALID);
		}
	}
	
	
	/**
	 * 查询出验证码
	 * @param vcode
	 * @param target，手机号码或者邮箱
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static NotifyVcode queryVCode(String vcode,Object target) throws Exception{
		return DBHelper.getDao().querySingle(NotifyVcode.class, 
				new DBCondition(NotifyVcode.JField.code,vcode),
				new DBCondition(NotifyVcode.JField.target,target),
				new DBCondition(NotifyVcode.JField.expireTime,DateUtil.now(),DBOperator.GREAT_EQUALS)
		);
	}
	
	
	/**
	 * 创建一个用户邮箱校验的校验码。需要往notify_vcode表里插入一条记录
	 * @param email，需要发送校验码的邮箱地址
	 * @param type，校验的类型
	 * @param length，校验码长度
	 * @param expireIn，校验码有效期，支持60s,1m,2h这种写法
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月12日
	 */
	public static String generateVCode(Object target,int length,String expireIn) throws Exception{
		String vcode = RandomUtil.getNum(length);
		
		//如果产生了一个新的验证码，那么要把之前同类型的全部删除
		DBHelper.getDao().delete(NotifyVcode.class,new DBCondition(NotifyVcode.JField.target,target));
		
		NotifyVcode vc = new NotifyVcode();
		Date currentDate = DateUtil.now();
		vc.setCode(vcode);
		vc.setExpireTime(DateUtil.offset(currentDate, expireIn));//有效期
		vc.setCreateTime(currentDate);
		vc.setTarget(String.valueOf(target));
		DBHelper.getDao().insert(vc);
		
		return vcode;
	}
	
	
	/**
	 * 验证某个验证码的有效性
	 * @param vcode,验证码
	 * @param target，手机号码或者邮箱
	 * @param type，验证码类型
	 * @return
	 * @throws Exception
	 */
	public static boolean isValidateVCode(String vcode,Object target) throws Exception{
		return queryVCode(vcode,target) != null;
	}
	
	/**
	 * 某个验证使用过以后就要置为无效，从当前notify_code表中删除。
	 * 因为验证码是一个随机数，所以在理论上是会出现重复的可能性，尽管概率很低，为了降低把其它相同的验证码也失效掉，所以还要加上对应手机号和类型
	 * 虽然理论上还是有可能，但几率低了很多
	 * @param vcode
	 * @param target,有可能是手机，也可能是邮箱
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static void expireVCode(String vcode,Object target) throws Exception{
		DBHelper.getDao().delete(NotifyVcode.class,
				new DBCondition(NotifyVcode.JField.code,vcode),
				new DBCondition(NotifyVcode.JField.target,target));
	}
}
