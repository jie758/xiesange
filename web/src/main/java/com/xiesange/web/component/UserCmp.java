package com.xiesange.web.component;

import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.RequestContext;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.gen.dbentity.wx.WxOauthUser;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.define.ErrorDefine;

public class UserCmp{
	
	public static void transfer(User user) throws Exception{
		if(user == null)
			return;
		if(NullUtil.isNotEmpty(user.getPic())){
			user.setPic(ETUtil.buildPicUrl(user.getPic()));
		}
	}
	
	/**
	 * 根据手机号查询出用户实体。
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static User queryByMobile(String mobile,BaseJField...jfs) throws Exception{
		if(NullUtil.isEmpty(mobile))
			return null;
		QueryStatement st = new QueryStatement(User.class,new DBCondition(User.JField.mobile,mobile));
		st.appendRange(0, 1);
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
		}
		List<User> users = DBHelper.getDao().query(st);
		
		return NullUtil.isEmpty(users) ? null : users.get(0);
	}
	
	
	public static User queryById(long userId,BaseJField...jfs) throws Exception{
		User user = DBHelper.getDao().queryById(User.class,userId,jfs);
		return user;
	}
	
	
	public static User checkExistByUserId(long userId) throws Exception{
		User user = queryById(userId);
		if(user == null){
			throw ETUtil.buildException(ErrorDefine.USER_NOTEXIST);
		}
		return user;
	}
	
	public static User queryByOpenid(String openid,BaseJField...jfs) throws Exception{
		if(NullUtil.isEmpty(openid))
			return null;
		QueryStatement st = new QueryStatement(User.class,new DBCondition(User.JField.wechat,openid));
		st.appendRange(0, 1);
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
		}
		List<User> users = DBHelper.getDao().query(st);
		return NullUtil.isEmpty(users) ? null : users.get(0);
	}
	
	public static User queryByOAuthCode(String code) throws Exception{
		WxOauthUser accessToken = WechatCmp.getOAuthInfo(code);
		if(accessToken == null || NullUtil.isEmpty(accessToken.getOpenid())){
			throw ETUtil.buildException(BaseErrorDefine.SYS_OPEN_FROM_WX);
		}
		String openid = accessToken.getOpenid();
		User userEntity = UserCmp.queryByOpenid(openid, User.JField.id,User.JField.wechat,User.JField.mobile,User.JField.name,User.JField.address);
		
		return userEntity;
		//如果用户存在，则创建登录
		//SysLogin sysLogin = userEntity==null ? null : CCP.createLogin(userEntity.getId(),BaseConsDefine.SYS_TYPE.XIESANGE, context.getRequestChannel());
		
	}
	
	public static User createUser(RequestContext context,String name,String mobile,String address){
		User userEntity = new User();
		userEntity.setSrc(context.getRequestChannel());
		userEntity.setRole(BaseConsDefine.USER_ROLE.NORMAL.value());
		userEntity.setMobile(mobile);
		userEntity.setAddress(address);
		userEntity.setName(name);
		userEntity.setDevice(context.getRequestHeader().getDevice_type());
		userEntity.setOrderCount(0L);
		userEntity.setOrderSum(0L);
		userEntity.setActiveTime(context.getRequestDate());
		return userEntity;
	}
	
	public static boolean isAdmin(short role){
		return role == 99;
	}
}
