package com.elsetravel.mis.component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.ICallback;
import com.elsetravel.baseweb.component.CountryCmp;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.FileUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.xml.BaseNode;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.orm.DBEntity;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.field.BaseJField;
import com.elsetravel.orm.statement.query.QueryStatement;


public class UserCmp {
	public static void appendAttrsFromUser(DBEntity detail,User user) throws Exception{
		detail.addAttribute(User.JField.cityCode.name(), user.getCityCode());
		detail.addAttribute(User.JField.countryCode.name(), user.getCountryCode());
		detail.addAttribute(User.JField.email.name(), user.getEmail());
		detail.addAttribute(User.JField.isEmailChecked.name(), user.getIsEmailChecked());
		detail.addAttribute(User.JField.isMobileChecked.name(), user.getIsMobileChecked());
		detail.addAttribute(User.JField.mobile.name(), user.getMobile());
		detail.addAttribute(User.JField.nickname.name(), user.getNickname());
		detail.addAttribute(User.JField.profilePic.name(), user.getProfilePic());
		detail.addAttribute(User.JField.type.name(), user.getType());
		detail.addAttribute(User.JField.wechat.name(), user.getWechat());
		detail.addAttribute(User.JField.wechatUnionId.name(), user.getWechatUnionId());
		detail.addAttribute(User.JField.weibo.name(), user.getWeibo());
		detail.addAttribute(User.JField.zone.name(), user.getZone());
		
		detail.addAttribute("countryName", user.getAttr("countryName"));
		detail.addAttribute("cityName", user.getAttr("cityName"));
	}
	
	/**
	 * 根据手机号查询出用户实体。
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static User queryUserByMobile(String mobile) throws Exception{
		if(NullUtil.isEmpty(mobile))
			return null;
		
		User user = DBHelper.getDao().querySingle(User.class, new DBCondition(User.JField.mobile,mobile));
		//transerUserInfo(user);
		
		return user;
	}
	
	/**
	 * 根据Email查询出用户实体。查询的时候邮箱必须是已经绑定的
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static User queryUserByEmail(String email) throws Exception{
		if(NullUtil.isEmpty(email))
			return null;
		
		User user = DBHelper.getDao().querySingle(
				User.class, 
				new DBCondition(User.JField.email,email)/*,
				new DBCondition(User.JField.isEmailChecked,1)*/
		);
		//transerUserInfo(user);
		return user;
	}
	
	
	/**
	 * 根据用户ID，检测游客是否存在。如果存在则返回Visitor实体；如果不存在则抛出异常
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static User checkExistByUserId(long userId) throws Exception{
		User user = queryUserById(userId);
		if(user == null){
			throw ETUtil.buildException(ErrorDefine.USER_NOTEXIST);
		}
		return user;
	}
	
	/**
	 * 根据微信号查询出用户实体。
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static User queryUserByWechat(String wechat) throws Exception{
		if(NullUtil.isEmpty(wechat))
			return null;
		User user = DBHelper.getDao().querySingle(User.class, new DBCondition(User.JField.wechat,wechat));
		//transerUserInfo(user);
		return user;
	}
	
	/**
	 * 根据QQ号查询出用户实体。
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static User queryUserByQQ(String qq) throws Exception{
		if(NullUtil.isEmpty(qq))
			return null;
		User user = DBHelper.getDao().querySingle(User.class, new DBCondition(User.JField.qq,qq));
		//transerUserInfo(user);
		return user;
	}
	
	/**
	 * 根据微信号查询出用户实体。
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static User queryUserByWeibo(String weibo) throws Exception{
		if(NullUtil.isEmpty(weibo))
			return null;
		User user = DBHelper.getDao().querySingle(User.class, new DBCondition(User.JField.weibo,weibo));
		//transerUserInfo(user);
		return user;
	}
	
	public static User queryUserById(Long userId,BaseJField... jfs) throws Exception{
		if(userId == null)
			return null;
		
		User user = DBHelper.getDao().queryById(User.class,userId,jfs);
		//transerUserInfo(user);
		
		return user;
	}
	
	public static List<User> queryUserByIds(Set<Long> userIds,BaseJField... jfs) throws Exception{
		if(NullUtil.isEmpty(userIds))
			return null;
		QueryStatement st = new QueryStatement(User.class,new DBCondition(User.JField.id,userIds,DBOperator.IN));
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
		}
		return DBHelper.getDao().query(st);
	}
	
	
	/**
	 * 往一个数据库实体列表里加入对应用户信息。通常这个数据库实体有一个指向User.id的外键字段
	 * @param entityList
	 * @param userIdField
	 * @param callback
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:57:22
	 */
	public static <T extends DBEntity>void appendUserInfo(List<T> entityList,BaseJField userIdField,ICallback callback) throws Exception{
		if(NullUtil.isEmpty(entityList))
			return;
		Set<Long> userIds = new HashSet<Long>();
		Long entityUserId = null;
		for(T entity : entityList){
			entityUserId = (Long)DBHelper.getEntityValue(entity, userIdField);
			if(entityUserId == null){
				continue;
			}
			entity.addAttribute("@userId", entityUserId);//后面还要用到，为了性能考虑直接存储起来
			userIds.add(entityUserId);
		}
		List<User> userList = DBHelper.getDao().query(User.class, new DBCondition(User.JField.id,userIds,DBOperator.IN));
		
		User matchUser = null;
		for(T entity : entityList){
			matchUser = null;
			entityUserId = (Long)entity.getAttr("@userId");
			if(entityUserId != null){
				for(User user : userList){
					if(entityUserId.longValue() == user.getId()){
						matchUser = user;
						break;
					}
				}
			}
			callback.execute(entity,matchUser);
			entity.addAttribute("@userId",null);
		}
	}
	
	public static String buildProfilePicPath(long userId,String extendName){
		return CommonUtil.join("image/user/",userId,"/main.",extendName);
	}
	
	public static void transferUser(User user) throws Exception{
		if(user == null)
			return;
		String profilePic = user.getProfilePic();
		
		//如果pic存储的是相对路径(非http开头)，则要用加上完整的http域名前缀返回
		if(NullUtil.isEmpty(profilePic)){
			user.setProfilePic(ETUtil.buildPicUrl("image/system/profile_default.png"));
		}else if(NullUtil.isNotEmpty(profilePic) && !profilePic.startsWith("http")){
			UserCmp.transferProfilePic(user);
		}
		//根据国家编码，添加国家名称
		if(NullUtil.isNotEmpty(user.getCountryCode())){
			BaseNode countryNode = CountryCmp.getCountryNode(user.getCountryCode());
			if(countryNode != null){
				user.addAttribute("countryName", countryNode.getAttribute("name"));
			}
		}
		//根据城市编码，添加城市名称
		if(NullUtil.isNotEmpty(user.getCityCode())){
			BaseNode cityNode = CountryCmp.getCityNode(user.getCountryCode(), user.getCityCode());
			if(cityNode != null){
				user.addAttribute("cityName", cityNode.getAttribute("name"));
			}
		}
		//查看当前用户是否是认证通过的导游,guider表里有记录且status=GUIDER_APPLY_APPROVED表示审核通过
		/*if(needJudgeGuder){
			transferGuider(user);
		}*/
	}
	
	/*public static void transferGuider(User guider) throws Exception{
		guider.addAttribute("isGuider", isGuider(guider)?"1":"0");
		//转换服务语言
		if(NullUtil.isNotEmpty(guider.getLanguages())){
			String[] langCodes = guider.getLanguages().split(",");
			StringBuffer text_sb = new StringBuffer();
			for(String langCode : langCodes){
				String langName = CCP.getLangName(langCode);
				if(NullUtil.isEmpty(langName))
					continue;
				if(text_sb.length() > 0){
					text_sb.append(",");
				}
				text_sb.append(langName);
			}
			guider.addAttribute("languagesText", text_sb);
			guider.addAttribute("languages", langCodes);
			guider.setLanguages(null);
		}else{
			guider.setLanguages(null);
		}
		
		//转换标签
		if(NullUtil.isNotEmpty(guider.getTags())){
			String[] tagIds = guider.getTags().split(",");
			List<BaseTag> tagList = BaseDataCmp.queryTagDefineList(ConstantDefine.TAG_TYPE_GUIDER);
			StringBuffer text_sb = new StringBuffer();
			for(String id : tagIds){
				for(BaseTag tagDefine : tagList){
					if(tagDefine.getId() == Long.valueOf(id).longValue()){
						if(text_sb.length() > 0){
							text_sb.append(",");
						}
						text_sb.append(tagDefine.getName());
						break;
					}
				}
			}
			guider.addAttribute("tags", tagIds);
			guider.addAttribute("tagsText", text_sb);
			guider.setTags(null);
		}else{
			guider.setTags(null);
		}
		
		//转换余额
		if(guider.getBalance() != null){
			//这里存储的单位是分，需要转换成元
			guider.addAttribute("balance", ETUtil.parseFen2Yuan(guider.getBalance()));
			guider.setBalance(null);
		}
		
		//证件照
		if(NullUtil.isNotEmpty(guider.getIdentityPic1())){
			guider.setIdentityPic1(ETUtil.buildPicUrl(guider.getIdentityPic1(),"small"));
		}
		if(NullUtil.isNotEmpty(guider.getIdentityPic2())){
			guider.setIdentityPic2(ETUtil.buildPicUrl(guider.getIdentityPic2(),"small"));
		}
	}*/
	
	/*public static boolean isGuider(User user){
		return user.getStatus() != null && user.getStatus() == ConstantDefine.GUIDER_APPLY_APPROVED;
	}*/
	
	/**
	 * 
	 * @param user
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:14:57
	 */
    public static void transferProfilePic(User user) throws Exception{
    	String pic = user.getProfilePic();
    	
    	boolean isPicExist = false;
    	if(NullUtil.isEmpty(pic)){
    		isPicExist = false;//pic为空肯定不存在
    	}else if(pic.startsWith("http")){
    		isPicExist = true;//如果是http开头，则表示是第三方图片，默认都是存在
    	}else{
    		//本地存储的文件，判断该文件是否存在
    		pic = pic.replace("${size}", "small");
    		int dot = pic.lastIndexOf("?");
    		if(dot > -1){
    			//有?得把?去掉,否则文件路径会找不到
    			isPicExist = FileUtil.isFileExist(ETUtil.buildPicPath(pic.substring(0,dot)));
    		}else{
    			isPicExist = FileUtil.isFileExist(ETUtil.buildPicPath(pic));
    		}
    	}
    	
    	if(isPicExist){
    		user.setProfilePic(ETUtil.buildPicUrl(pic));
    	}else{
    		//如果不存在则用默认的图片
    		user.setProfilePic(ETUtil.buildPicUrl("image/system/profile_default.png"));
    	}
    }
    
    /**
	 * 清除游客敏感信息。主要是联系方式
	 * 
	 * @param guider
	 * @author Wilson 
	 * @date 下午7:32:45
	 */
	/*public static void clearSensitiveInfo(User user){
		if(user == null)
			return;
		user.setWeibo(null);
		user.setWechat(null);
		user.setBirthday(null);
		user.setEmail(null);
		user.setMobile(null);
		user.setQq(null);
		user.setPassword(null);
		user.setBalance(null);
		user.setIdentityType(null);
		user.setIdentityPic1(null);
		user.setIdentityPic2(null);
	}*/
}
