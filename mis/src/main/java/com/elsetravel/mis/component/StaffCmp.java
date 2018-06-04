package com.elsetravel.mis.component;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.mis.MisStaff;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.orm.DBHelper;

public class StaffCmp {
	public static MisStaff queryByAccount(String account) throws Exception{
		return DBHelper.getDao().querySingle(MisStaff.class, MisStaff.JField.account, account);
	}
	
	public static MisStaff queryById(long staffId) throws Exception{
		return DBHelper.getDao().queryById(MisStaff.class, staffId);
	}
	
	/**
	 * 根据id检查员工是否存在
	 * @author Wilson 
	 * @date 下午8:13:11
	 * @param
	 */
	public static MisStaff checkStaffExist(long staffId) throws Exception{
		MisStaff staff = DBHelper.getDao().queryById(MisStaff.class, staffId);
		if(staff == null){
			throw ETUtil.buildException(ErrorDefine.STAFF_NOTEXIST);
		}
		return staff;
	}
	
	
	/**
	 *  根据sex枚举值获取出名称
	 * @param sex
	 * @return
	 * @author Wilson Wu
	 * @date 2015年9月25日
	 */
	public static String getSexName(Short sex){
		return sex == null ? null : (sex == BaseConstantDefine.USER_SEX_MALE) ? "男": (sex == BaseConstantDefine.USER_SEX_FEMALE) ? "女" : "其他";
	}
	/**
	 *  根据sex名称获取出枚举值
	 * @param sex
	 * @return
	 * @author Wilson Wu
	 * @date 2015年9月25日
	 */
	public static Short getSexEnum(String sexName){
		if(NullUtil.isEmpty(sexName))
			return null;
		else if(sexName.equals("男")){
			return BaseConstantDefine.USER_SEX_MALE;
		}else if(sexName.equals("女")){
			return BaseConstantDefine.USER_SEX_FEMALE;
		}else{
			return BaseConstantDefine.USER_SEX_OTHER;
		}
	}
}
