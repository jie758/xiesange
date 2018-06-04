package com.elsetravel.mis.component;

import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.pojo.MerchantHolder;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.mch.MchAgency;
import com.elsetravel.gen.dbentity.mch.MchHotel;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.orm.DBEntity;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.FieldPair;
import com.elsetravel.orm.pojo.JoinQueryData;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.statement.query.QueryStatement;

/**
 * 商户相关组件
 * @author Wilson 
 * @date 上午10:09:56
 */
public class MerchantCmp {
	/**
	 * 根据某个商户的user对象，把具体商户表里的信息也查询出来，并合并，最终返回一个完整的商户对象
	 * @param user,必须有id和sub_type字段，其它信息可以不要
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:11:18
	 */
	public static <T extends DBEntity>T merge(User user) throws Exception{
		T mch = queryById(user.getId(),user.getSubType());
		
		return merge(user,mch);
	}
	
	/**
	 * 根据商户的user对象和具体商户对象，进行合并，最终都合并到商户具体对象上，并返回该完整的商户对象
	 * @param user
	 * @param merchant
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:13:57
	 */
	public static <T extends DBEntity>T merge(User user,T merchant) throws Exception{
		if(user == null || merchant == null)
			return null;
		if(merchant instanceof MchAgency){
			transferAgency((MchAgency)merchant);
		}else if(merchant instanceof MchHotel){
			transferHotel((MchHotel)merchant);
		}
		UserCmp.transferUser(user);
		UserCmp.appendAttrsFromUser(merchant,user);
		
		return merchant;
	}
	
	/**
	 * 检查商户具体记录是否存在，不存在则抛出异常
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:14:31
	 */
	public static <T extends DBEntity>T checkMerchantExist(long userId) throws Exception{
		T mch = queryById(userId);
		if(mch == null){
			throw ETUtil.buildException(ErrorDefine.MERCHANT_NOTEXIST);
		}
		return mch;
	}
	
	/**
	 * 根据商户id和具体商户类，用join查询出商户具体信息，包括User和商户具体对象
	 * @param userId
	 * @param mchClass
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:15:14
	 */
	public static <T extends DBEntity>MerchantHolder<T> queryMerchantHolder(long userId,Class<T> mchClass) throws Exception{
		QueryStatement st = new QueryStatement(User.class,new DBCondition(User.JField.id,userId));
		st.appendRange(0, 1);
		st.appendJoin(mchClass, new FieldPair(DBHelper.getIdJField(mchClass),User.JField.id));
		
		List<JoinQueryData> resultList = (List<JoinQueryData>)st.execute();
		if(NullUtil.isEmpty(resultList))
			return null;
		User user = resultList.get(0).getResult(User.class);
		T merchant = resultList.get(0).getResult(mchClass);
		
		return new MerchantHolder<T>(user,merchant);
	}
	
	
	/**
	 * 查询商户资料,只查询Merchant信息，不查询user信息
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:11:54
	 */
	public static <T extends DBEntity>T queryById(long userId) throws Exception{
		User user = UserCmp.queryUserById(userId,User.JField.subType);
		if(user == null)
			return null;
		if(user.getSubType() == null){
			throw ETUtil.buildInvalidOperException();
		}
		return queryById(userId,user.getSubType());
	}
	public static <T extends DBEntity>T queryById(long userId,short subType) throws Exception{
		if(subType == BaseConstantDefine.USER_SUBTYPE_MCH_AGENCY){
			return (T)queryAgencyById(userId);
		}else if(subType == BaseConstantDefine.USER_SUBTYPE_MCH_HOTEL){
			return (T)queryHotelById(userId);
		}else{
			throw ETUtil.buildInvalidOperException();
		}
	}
	
	public static MchAgency queryAgencyById(long userId) throws Exception{
		return DBHelper.getDao().queryById(MchAgency.class,userId);
	}
	
	public static MchHotel queryHotelById(long userId) throws Exception{
		return DBHelper.getDao().queryById(MchHotel.class,userId);
	}
	
	/**
	 * 查询完整的商户资料，包括merchant表和user表信息
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:17:24
	 */
	/*public static <T extends DBEntity>MerchantHolder<T> queryMerchantHolder(long userId) throws Exception{
		User user = UserCmp.queryUserById(userId);
		T merchant = queryById(userId);
		
		return new MerchantHolder<T>(user,merchant);
	}*/
	
	/*public static MerchantHolder queryMerchantHolder(String email) throws Exception{
		QueryStatement st = new QueryStatement(User.class,new DBCondition(User.JField.email,email));
		st.appendJoin(Merchant.class, new FieldPair(Merchant.JField.id,User.JField.id))
		  .appendRange(0, 1);
		
		List<JoinQueryData> resultList = (List<JoinQueryData>)st.execute();
		if(NullUtil.isEmpty(resultList))
			return null;
		User user = resultList.get(0).getResult(User.class);
		Merchant merchant = resultList.get(0).getResult(Merchant.class);
		
		return new MerchantHolder(user,merchant);
	}*/
	
	/*public static MerchantHolder queryMerchantHolder(User user) throws Exception{
		Merchant merchant = queryById(user.getId());
		return new MerchantHolder(user,merchant);
	}*/
	
	private static <T extends DBEntity>void transferAgency(MchAgency agency) throws Exception{
		//证件照
		if(NullUtil.isNotEmpty(agency.getIdentityPic())){
			agency.setIdentityPic(ETUtil.buildPicUrl(agency.getIdentityPic()));
		}
	}
	private static <T extends DBEntity>void transferHotel(MchHotel hotel) throws Exception{
	}
}
