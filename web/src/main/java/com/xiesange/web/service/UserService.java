package com.xiesange.web.service;

import com.xiesange.baseweb.component.VCodeCmp;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.orm.DBHelper;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.define.ParamDefine;

@ETServiceAnno(name = "user", version = "")
/**
 * 用户服务类
 * @author Wilson 
 * @date 上午9:48:42
 */
public class UserService extends AbstractService {
	/**
	 * 修改当前登录用户个人资料。如果有涉及到手机，必须输入验证码 
	 * @param context
	 * 			name,
	 * 			mobile,
	 * 			vcode,
	 * 			address
	 * @return
	 * @throws Exception
	 */
	public ResponseBody modify(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		String name = reqbody.getString(ParamDefine.Common.name);
		String mobile = reqbody.getString(ParamDefine.Common.mobile);
		String vcode = reqbody.getString(ParamDefine.Common.vcode);
		String address = reqbody.getString(ParamDefine.User.address);
		
		User userEntity = context.getAccessUser();
		if(NullUtil.isNotEmpty(name)){
			userEntity.setName(name);
		}
		if(NullUtil.isNotEmpty(address)){
			userEntity.setAddress(address);
		}
		if(NullUtil.isNotEmpty(mobile)){
			VCodeCmp.checkMobileVCode(vcode, mobile);
			userEntity.setMobile(mobile);
		}
		
		if(DBHelper.isModified(userEntity)){
			dao().updateById(userEntity, userEntity.getId());
		}
		
		return null;
	}
}
