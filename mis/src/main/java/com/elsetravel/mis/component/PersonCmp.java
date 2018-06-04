package com.elsetravel.mis.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.pojo.PersonHolder;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.base.BaseTag;
import com.elsetravel.gen.dbentity.person.Person;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.define.ConstantDefine;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;

/**
 * 个人用户相关组件
 * @author Wilson 
 * @date 上午10:09:56
 */
public class PersonCmp {
	public static Person merge(User user) throws Exception{
		return merge(user,PersonCmp.queryById(user.getId()));
	}
	public static Person merge(PersonHolder holder) throws Exception{
		return merge(holder.getUser(),holder.getPerson());
	}
	public static Person merge(User user,Person person) throws Exception{
		UserCmp.transferUser(user);
		PersonCmp.transfer(person);
		
		UserCmp.appendAttrsFromUser(person,user);
		
		return person;
	}
	
	
	/**
	 * 查询商户资料,只查询Merchant信息，不查询user信息
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:11:54
	 */
	public static Person queryById(long userId) throws Exception{
		Person person = DBHelper.getDao().queryById(Person.class,userId);
		return person;
	}
	
	public static Person checkExistById(long userId) throws Exception{
		Person person = queryById(userId);
		if(person == null){
			throw ETUtil.buildException(ErrorDefine.USER_NOTEXIST);
		}
		return person;
	}
	
	/**
	 * 查询完整的个人商户资料，包括person表和user表信息
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:17:24
	 */
	public static PersonHolder queryFull(long userId) throws Exception{
		User user = UserCmp.queryUserById(userId);
		Person person = queryById(userId);
		
		return new PersonHolder(user,person);
	}
	
	public static PersonHolder queryFull(User user) throws Exception{
		Person person = queryById(user.getId());
		return new PersonHolder(user,person);
	}
	
	public static List<PersonHolder> queryFullList(Set<Long> userIds) throws Exception{
		List<User> userList = DBHelper.getDao().query(User.class, new DBCondition(User.JField.id,userIds,DBOperator.IN));
		if(NullUtil.isEmpty(userList)){
			return null;
		}
		List<Person> personList = DBHelper.getDao().query(Person.class, new DBCondition(Person.JField.id,userIds,DBOperator.IN));
		
		List<PersonHolder> fullList = new ArrayList<PersonHolder>();
		for(User user : userList){
			for(Person person : personList){
				if(user.getId().longValue() == person.getId().longValue()){
					fullList.add(new PersonHolder(user,person));
					break;
				}
			}
		}
		return fullList;
	}
	
	
	public static void transfer(Person person) throws Exception{
		person.addAttribute("sexName", CCP.getSexName(person.getSex()));
		//证件照
		if(NullUtil.isNotEmpty(person.getIdentityPic1())){
			person.setIdentityPic1(ETUtil.buildPicUrl(person.getIdentityPic1()));
		}
		if(NullUtil.isNotEmpty(person.getIdentityPic2())){
			person.setIdentityPic2(ETUtil.buildPicUrl(person.getIdentityPic2()));
		}
		
		
		//转换服务语言
		if(NullUtil.isNotEmpty(person.getLanguages())){
			String[] langCodes = person.getLanguages().split(",");
			List<String> langTextList = new ArrayList<String>();
			StringBuffer text_sb = new StringBuffer();
			for(int i=0;i<langCodes.length;i++){
				String langCode = langCodes[i];
				String langName = CCP.getLangName(langCode);//BaseDataCmp.queryLangByCode(langCode);
				//LogUtil.getLogger(UserCmp.class).debug("xxxxxxxx:"+langCode+","+langName);
				if(NullUtil.isEmpty(langName))
					continue;
				if(text_sb.length() > 0){
					text_sb.append(",");
				}
				text_sb.append(langName);
				
				langTextList.add(langName);
			}
			
			person.addAttribute("languageCodeList", langCodes);
			person.addAttribute("languageTextList", langTextList);
			
			person.setLanguages(null);
		}else{
			person.setLanguages(null);
		}
		
		//转换标签
		if(NullUtil.isNotEmpty(person.getTags())){
			String[] tagIds = person.getTags().split(",");
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
			person.addAttribute("tags", tagIds);
			person.addAttribute("tagsText", text_sb);
			person.setTags(null);
		}else{
			person.setTags(null);
		}
	}
	
	public static boolean isGuider(Person person){
		return person != null && person.getStatus() != null && person.getStatus() == ConstantDefine.GUIDER_APPLY_APPROVED;
	}
	public static boolean isGuider(User user) throws Exception{
		if(user == null)
			return false;
		Person person = queryById(user.getId());
		return person != null && person.getStatus() != null && person.getStatus() == ConstantDefine.GUIDER_APPLY_APPROVED;
	}
	
	public static void clearSensitiveInfo(Person person){
		if(person == null)
			return;
		person.setBirthday(null);
		person.setBalance(null);
		person.setIdentityType(null);
		person.setIdentityPic1(null);
		person.setIdentityPic2(null);
	}
}
