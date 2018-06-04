package com.elsetravel.mis.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.define.BaseErrorDefine;
import com.elsetravel.core.ParamHolder;
import com.elsetravel.core.notify.target.SmsNotifyTarget;
import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.HttpUtil;
import com.elsetravel.core.util.RandomUtil;
import com.elsetravel.gen.dbentity.notify.NotifyVcode;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;

/**
 * 验证码相关组件
 * @author Think
 *
 */
public class VCodeCmp {
	
	
	
	/**
	 * 查询出验证码
	 * @param vcode
	 * @param target，手机号码或者邮箱
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static NotifyVcode queryVCode(String vcode,Object target,short type) throws Exception{
		return DBHelper.getDao().querySingle(NotifyVcode.class, 
				new DBCondition(NotifyVcode.JField.code,vcode),
				new DBCondition(NotifyVcode.JField.target,target),
				new DBCondition(NotifyVcode.JField.type,type),
				new DBCondition(NotifyVcode.JField.expireTime,DateUtil.now(),DBOperator.GREAT_EQUALS)
		);
	}
	
	/**
	 * 发送验证码。并往notify_validate_code里存入记录。
	 * @param mobile
	 * @param type,验证码的类型不一样，比如有注册验证码、修改密码验证码等，验证的时候也要加上这个
	 * @param length,验证码长度
	 * @param expireIn,该验证码有效期，单位秒，传了60表示60秒后失效
	 * @return
	 * @throws Exception 
	 */
	public static String sendVCode(String mobile,short type,int length,int expireIn) throws Exception{
		String validateCode = RandomUtil.getNum(length);
		
		NotifyVcode vc = new NotifyVcode();
		Date currentDate = DateUtil.now();
		vc.setCode(validateCode);
		vc.setExpireTime(DateUtil.offsetSecond(currentDate, expireIn));//有效期
		vc.setCreateTime(currentDate);
		vc.setType(type);
		vc.setTarget(mobile);
		DBHelper.getDao().insert(vc);
		
		//要存入记录成功后才能发送验证码
		List<SmsNotifyTarget> targets = new ArrayList<SmsNotifyTarget>();
		targets.add(new SmsNotifyTarget(mobile));
		
		return validateCode;
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
	public static String generateVCode(Object target,short type,int length,String expireIn) throws Exception{
		String vcode = RandomUtil.getNum(length);
		
		//如果产生了一个新的验证码，那么要把之前同类型的全部删除
		DBHelper.getDao().delete(NotifyVcode.class, 
				new DBCondition(NotifyVcode.JField.target,target),
				new DBCondition(NotifyVcode.JField.type,type));
		
		NotifyVcode vc = new NotifyVcode();
		Date currentDate = DateUtil.now();
		vc.setCode(vcode);
		vc.setExpireTime(DateUtil.offset(currentDate, expireIn));//有效期
		vc.setCreateTime(currentDate);
		vc.setType(type);
		vc.setTarget(String.valueOf(target));
		DBHelper.getDao().insert(vc);
		
		return vcode;
	}
	
	/**
	 * 往邮箱里发送验证链接。该链接要包括当前email、验证码
	 * @param mobile
	 * @param type
	 * @param length
	 * @param expireIn
	 * @return
	 * @throws Exception
	 */
	/*public static String sendVEmail(String email,short type,int length,int expireIn) throws Exception{
		String validateCode = RandomUtil.getNum(length);
		
		NotifyVcode vc = new NotifyVcode();
		Date currentDate = DateUtil.now();
		vc.setCode(validateCode);
		vc.setExpireTime(DateUtil.offsetSecond(currentDate, expireIn));//有效期
		vc.setCreateTime(currentDate);
		vc.setType(type);
		vc.setTarget(email);
		DBHelper.getDao().insert(vc);
		
		//要存入记录成功后才能发送验证码
		List<SmsNotifyTarget> targets = new ArrayList<SmsNotifyTarget>();
		targets.add(new SmsNotifyTarget(mobile));
		
		return validateCode;
	}*/
	
	/**
	 * 验证某个验证码的有效性
	 * @param vcode,验证码
	 * @param target，手机号码或者邮箱
	 * @param type，验证码类型
	 * @return
	 * @throws Exception
	 */
	public static boolean validateVCode(String vcode,Object target,short type) throws Exception{
		return queryVCode(vcode,target,type) != null;
	}
	
	/**
	 * 检查某个验证码的有效性，和validateVCode逻辑一致，只是如果该校验码无效，会抛出错误。
	 * 验证过一次后该验证码就会失效了
	 * @param vcode,验证码
	 * @param target，手机号码或者邮箱
	 * @param type，验证码类型
	 * @return
	 * @throws Exception
	 */
	public static void checkVCode(String vcode,Object target,short type) throws Exception{
		NotifyVcode notifyV = queryVCode(vcode,target,type);
		if(notifyV == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_VCODE_INVALID);
		}
		
		DBHelper.getDao().deleteById(NotifyVcode.class,notifyV.getId());
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
	public static void expireVCode(String vcode,Object target,short type) throws Exception{
		/*NotifyVcode value = new NotifyVcode();
		value.setExpireTime(DateUtil.now());*/
		DBHelper.getDao().delete(NotifyVcode.class,
				new DBCondition(NotifyVcode.JField.code,vcode),
				new DBCondition(NotifyVcode.JField.target,target),
				new DBCondition(NotifyVcode.JField.type,type));
	}
	
	/**
	 * 向mob服务端校验短信码的正确性
	 * 
	 * @param mobile
	 * @param zone
	 * @param vcode
	 * @return
	 * @author Wilson
	 * @throws Exception
	 * @date 下午5:22:02
	 */
	public static boolean isValidVcodeFromMob(String mobile, short zone, String vcode)
			throws Exception {
		ParamHolder params = new ParamHolder("appkey",BaseConstantDefine.MOB_APPKEY);
		params.addParam("phone", mobile);
		params.addParam("zone", String.valueOf(zone));
		params.addParam("code", vcode);
		String resp = HttpUtil.execute(HttpUtil.createHttpPost(BaseConstantDefine.MOB_VERIFY_VCODE_URL, params));
		//有200应答码表示有效
		System.out.println("vcode result : " + resp);
		return resp.indexOf("\"status\":200") != -1;
		// /return true;
	}
	
	public static boolean sendVcodeFromMob(String mobile, short zone) throws Exception {
		ParamHolder params = new ParamHolder("appkey",BaseConstantDefine.MOB_APPKEY);
		params.addParam("phone", mobile);
		params.addParam("zone", String.valueOf(zone));
		
		String resp = HttpUtil.execute(HttpUtil.createHttpPost(BaseConstantDefine.MOB_SEND_VCODE_URL, params));
		//有200应答码表示有效
		System.out.println("vcode result : " + resp);
		return resp.indexOf("\"status\":200") != -1;
		// /return true;
	}
	
}
