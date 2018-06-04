package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.request.UploadRequestBody.UploadFile;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.util.PinyinUtil;
import com.elsetravel.gen.dbentity.mis.MisStaff;
import com.elsetravel.gen.dbentity.person.Person;
import com.elsetravel.gen.dbentity.reward.RewardMedal;
import com.elsetravel.gen.dbentity.reward.RewardPoint;
import com.elsetravel.gen.dbentity.reward.RewardRecord;
import com.elsetravel.gen.dbentity.sys.SysLogin;
import com.elsetravel.gen.dbentity.ticket.Ticket;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.gen.dbentity.user.UserBalanceDetail;
import com.elsetravel.gen.dbentity.user.UserFavTicket;
import com.elsetravel.gen.dbentity.user.UserFavTopic;
import com.elsetravel.mis.component.UserCmp;
import com.elsetravel.mis.define.ConstantDefine;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;
@ETServiceAnno(name="user",version="")
public class UserService extends AbstractService{
	/**
	 * 查询用户
	 * @param context
	 * 			nickname,昵称，模糊匹配
	 * 			email,邮箱,模糊匹配
	 * 			mobile,手机，模糊匹配
	 * 			sex,性别
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午11:27:06
	 */
	public ResponseBody queryList(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		String nickname = reqbody.getString(ParamDefine.User.nickname);
		String email = reqbody.getString(ParamDefine.User.email );
		String mobile = reqbody.getString(ParamDefine.User.mobile);
		Short sex = reqbody.getShort(ParamDefine.Person.sex);
		
		List<DBCondition> conds = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(nickname)){
			conds.add(new DBCondition(User.JField.nickname,"%"+nickname+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(email)){
			conds.add(new DBCondition(User.JField.email,"%"+email+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(mobile)){
			conds.add(new DBCondition(User.JField.mobile,"%"+mobile+"%",DBOperator.LIKE));
		}
		//TODO 
		/*if(NullUtil.isNotEmpty(sexName)){
			conds.add(new DBCondition(User.JField.sex,CCP.getSexEnum(sexName)));
		}*/
		conds.add(new DBCondition(MisStaff.JField.id,0,DBOperator.GREAT));//过滤掉超级管理员
		
		DBCondition[] condArr = conds.toArray(new DBCondition[conds.size()]); 
		List<User> userList = dao().query(
			new QueryStatement(User.class,condArr)
				.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
				.appendOrderFieldDesc(User.JField.createTime)
		);
		if(NullUtil.isEmpty(userList))
			return null;
		for(User user : userList){
			user.addAttribute(User.JField.createTime.getName(), user.getCreateTime());
			ETUtil.clearDBEntityExtraAttr(user);
			user.setProfilePic(ETUtil.buildPicUrl(user.getProfilePic()));
			if(user.getZone() != null){
				user.setMobile("+"+user.getZone()+" "+user.getMobile());
			}
		}
		
		long count = dao().queryCount(User.class, condArr);
		return new ResponseBody("result",userList).addTotalCount(count);
	}
	
	
	/**
	 * 添加新用户，这个动作由客服发起，添加一些僵尸用户，这些用户的roleType是特殊的类型
	 * @author Wilson 
	 * @date 下午8:10:11
	 * @param context
	 */
	public ResponseBody add(MisRequestContext context) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.User.nickname,
			ParamDefine.User.email,
			ParamDefine.Login.password,
			ParamDefine.User.type
		);
		
		
		String nickname = reqbody.getString(ParamDefine.User.nickname);
		String email = reqbody.getString(ParamDefine.User.email);
		String password = reqbody.getString(ParamDefine.Login.password);
		
		String mobile = reqbody.getString(ParamDefine.User.mobile);
		String countryCode = reqbody.getString(ParamDefine.User.country_code);
		String cityCode = reqbody.getString(ParamDefine.User.city_code);
		
		Short type = reqbody.getShort(ParamDefine.User.type);
		
		User userEntity = new User();
		long newid = dao().getSequence(User.class);
		userEntity.setId(newid);
		userEntity.setNickname(nickname);
		userEntity.setEmail(email);
		userEntity.setPassword(CCP.md5Password(password));
		userEntity.setMobile(mobile);
		userEntity.setCountryCode(countryCode);
		userEntity.setCityCode(cityCode);
		userEntity.setType(type);
		userEntity.setSrc(BaseConstantDefine.USER_SRC_MIS);
		
		
		
		
		if(NullUtil.isNotEmpty(nickname)){
			//设置拼音
			userEntity.setPy(PinyinUtil.getFullSpell(nickname));
			userEntity.setPyHeader(PinyinUtil.getFirstSpell(nickname));
		}
		String picPath = null;//头像的完整url
		if(NullUtil.isNotEmpty(reqbody.getUploadFiles())){
			UploadFile uploadFile = reqbody.getUploadFiles().get(0);//头像只会有一个
			//图片的存储路径,$ROOT_PATH/image/user/$userId/main.xxx,后缀由上传文件决定
			picPath = UserCmp.buildProfilePicPath(newid,uploadFile.getExtendName());
			picPath = CCP.uploadImage(uploadFile, picPath,true);
			userEntity.setProfilePic(picPath);
		}
		
		dao().insert(userEntity);
		
		String occupation = reqbody.getString(ParamDefine.Person.occupation);
		Short sex = reqbody.getShort(ParamDefine.Person.sex);
		Date birthday = reqbody.getDate(ParamDefine.Staff.birthday,DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
		String tags = reqbody.getString(ParamDefine.Person.tags);
		String langs = reqbody.getString(ParamDefine.Person.languages);
		
		Person person = new Person();
		person.setId(newid);
		person.setBirthday(birthday);
		person.setSex(sex);
		person.setTags(tags);
		person.setLanguages(langs);
		person.setOccupation(occupation);
		person.setStatus(ConstantDefine.GUIDER_APPLY_NONE);
		dao().insert(person);
		
		ResponseBody respBody = new ResponseBody("userId",userEntity.getId());
		if(picPath != null){
			respBody.add("profilePic",ETUtil.buildPicUrl(picPath));
		}
		
		return respBody;
		
	}
	
	
	/**
	 * 修改用户信息
	 * @author Wilson 
	 * @date 下午8:10:11
	 * @param
	 * @throws Exception 
	 */
	public ResponseBody modify(MisRequestContext context) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.User.user_id
		);
		
		String nickname = reqbody.getString(ParamDefine.User.nickname);
		String email = reqbody.getString(ParamDefine.User.email);
		String mobile = reqbody.getString(ParamDefine.User.mobile);
		
		String countryCode = reqbody.getString(ParamDefine.User.country_code);
		String cityCode = reqbody.getString(ParamDefine.User.city_code);
		
		long userId = reqbody.getLong(ParamDefine.User.user_id);
		User userEntity = UserCmp.checkExistByUserId(userId);
		if(nickname != null){
			userEntity.setNickname(nickname);
			//设置拼音
			userEntity.setPy(PinyinUtil.getFullSpell(nickname));
			userEntity.setPyHeader(PinyinUtil.getFirstSpell(nickname));
				
		}
		if(email != null){
			userEntity.setEmail(email);
			userEntity.setIsEmailChecked((short)1);//客服修改的就直接置为已认证
		}
		if(mobile != null){
			userEntity.setMobile(mobile);
			userEntity.setIsMobileChecked((short)1);//客服修改的就直接置为已认证
		}
		if(countryCode != null){
			userEntity.setCountryCode(countryCode);
		}
		if(cityCode != null){
			userEntity.setCityCode(cityCode);
		}

		String picPath = null;//头像的完整url
		if(NullUtil.isNotEmpty(reqbody.getUploadFiles())){
			UploadFile uploadFile = reqbody.getUploadFiles().get(0);//头像只会有一个
			picPath = UserCmp.buildProfilePicPath(userId, uploadFile.getExtendName());
			picPath = CCP.uploadImage(uploadFile, picPath,true);
			userEntity.setProfilePic(picPath+"?t="+System.currentTimeMillis());//加一个t时间戳，防止前端从缓存导致无法更新
		}
		
		if(DBHelper.isModified(userEntity)){
			dao().updateById(userEntity, userId);
		}
		
		
		Short sex = reqbody.getShort(ParamDefine.Person.sex);
		Date birthday = reqbody.getDate(ParamDefine.Person.birthday,DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
		String occupation = reqbody.getString(ParamDefine.Person.occupation);
		String tags = reqbody.getString(ParamDefine.Person.tags);
		String langs = reqbody.getString(ParamDefine.Person.languages);
		
		
		Person person = new Person();
		if(sex != null){
			person.setSex(sex);
		}
		if(birthday != null){
			person.setBirthday(birthday);
		}
		if(occupation != null){
			person.setOccupation(occupation);
		}
		if(tags != null){
			person.setTags(tags);
		}
		if(langs != null){
			person.setLanguages(langs);
		}
		if(DBHelper.isModified(person)){
			dao().updateById(person, userId);
		}
		
		return picPath==null ? null : new ResponseBody("result",ETUtil.buildPicUrl(picPath));
	}
	
	/**
	 * 删除个人用户，一次只能删除一条
	 * @param context
	 * 			user_id,需要删除的用户id
	 * @return
	 * @author Wilson 
	 * @date 下午8:34:49
	 */
	public ResponseBody remove(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.User.user_id
		);
		
		long userId = reqbody.getLong(ParamDefine.User.user_id);
		
		//删除用户表记录
		dao().deleteById(User.class, userId);
		//删除商户表记录
		dao().deleteById(Person.class, userId);
		//删除旅票记录
		dao().delete(Ticket.class, new DBCondition(Ticket.JField.userId,userId));
		//删除资金明细记录
		dao().delete(UserBalanceDetail.class, new DBCondition(UserBalanceDetail.JField.userId,userId));
		//删除登录痕迹
		dao().delete(SysLogin.class, new DBCondition(SysLogin.JField.userId,userId));
		//删除专题收藏
		dao().delete(UserFavTicket.class, new DBCondition(UserFavTicket.JField.userId,userId));
		//删除旅票收藏
		dao().delete(UserFavTopic.class, new DBCondition(UserFavTopic.JField.userId,userId));
		
		//删除赠送
		dao().delete(RewardMedal.class, new DBCondition(RewardMedal.JField.userId,userId));
		dao().delete(RewardPoint.class, new DBCondition(RewardPoint.JField.userId,userId));
		dao().delete(RewardRecord.class, new DBCondition(RewardRecord.JField.userId,userId));
		
		
		return null;
	}
	
	
	/**
	 * 把某个用户直接转成导游，跳过认证这个步骤。
	 * 如果是第一次转的话，会在guider表里新创建记录，后续再转的话相当于修改导游资料，不会再插入了
	 * @param context
	 * 			user_id,用户id，必填
	 * 			name，真实姓名
	 * 			tags,string,导游标签，多个值以英文逗号分隔
	 * 			languages,string,服务语言，多个值以英文逗号分隔
	 * 			
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:04:04
	 */
	public ResponseBody trans2Guider(MisRequestContext context) throws Exception{
		/*RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.User.user_id);
		
		Long userId = reqbody.getLong(ParamDefine.User.user_id);
		
		String name = reqbody.getString(ParamDefine.Common.name);
		String tags = reqbody.getString(ParamDefine.Guider.tags);
		String languages = reqbody.getString(ParamDefine.Guider.languages);
		
		User user = dao().queryById(User.class, userId);
		if(user == null){
			throw ETUtil.buildException(ErrorDefine.USER_NOTEXIST);
		}
		
		if(NullUtil.isNotEmpty(name)){
			user.setName(name);
		}
		if(tags != null){
			//可以修改成空值，所以用！=null判断
			user.setName(tags);
		}
		if(languages != null){
			//可以修改成空值，所以用！=null判断
			user.setLanguages(languages);
		}
		user.setStatus(ConstantDefine.GUIDER_APPLY_APPROVED);
		if(DBHelper.isModified(user)){
			dao().updateById(user, userId);
		}*/
		
		
		return null;
	}
}
