package com.xiesange.baseweb.component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.ICallback;
import com.xiesange.baseweb.RequestContext;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.define.RequestHeaderField;
import com.xiesange.baseweb.define.SysparamDefine;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.RequestHeader;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.EncryptUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.RandomUtil;
import com.xiesange.gen.dbentity.base.BaseEnum;
import com.xiesange.gen.dbentity.mis.MisStaff;
import com.xiesange.gen.dbentity.sys.SysLogin;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.FieldUpdateExpression;
import com.xiesange.orm.NativeValue;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.update.UpdateStatement;

/**
 * CommonCompent的简写，因为比较常用，所以用简写，方便调用
 * 
 * @author Think
 * 
 */
public class CCP {
	private static Logger logger = LogUtil.getLogger(CCP.class);
	private static final String PASSWORD_MD5_KEY = "_qXeXdseCvdfMn";// md5加密混淆key
	public static List<MisStaff> CUST_SERVICE_USER_LIST;

	/*
	 * public static void main(String[] args) {
	 * System.out.println(md5Password(EncryptUtil
	 * .MD5.encode("elsetravel123")));; }
	 */
	
	public static String buildSignature(Map<String,Object> paramMap,String skey) {
		String paramStr = ETUtil.createSortedUrlStr(paramMap);
		logger.debug("sorted params : "+paramStr+skey);
		paramStr = EncryptUtil.MD5.encode(paramStr, skey);
		return paramStr;
	}
	
	/**
	 * 校验请求签名
	 * 
	 * @param header
	 * @param body
	 * @author Wilson
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @date 下午3:18:48
	 */
	public static void checkSignature(RequestHeader header, RequestBody body,String skey) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(RequestUtil.isFromApp(header)){
			//app渠道过来的,非2.1版本不做校验
			return;
		}
		String sign = header.getSign();
		if (NullUtil.isEmpty(header.getToken()))
			return;//token为null，则不做签名校验
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (NullUtil.isNotEmpty(body))
			paramMap.putAll(body);
		
		RequestHeaderField[] fs = RequestHeaderField.values();
		for(RequestHeaderField f : fs){
			if(f == RequestHeaderField.sign){
				continue;
			}
			Object val = PropertyUtils.getProperty(header, f.name());
			if(val == null){
				continue;
			}
			paramMap.put(f.name(), val);
		}
		
		/*paramMap.put(RequestHeaderDefine.token.name(),header.getToken());
		paramMap.put(RequestHeaderDefine.page_index.name(),header.getPage_index());
		paramMap.put(RequestHeaderDefine.page_count.name(),header.getPage_count());
		paramMap.put(RequestHeaderDefine.app_version.name(),header.getApp_version());
		*/String paramStr = CCP.buildSignature(paramMap,skey);
		logger.debug("-----------sign str from app : " + sign);
		logger.debug("-----------sign str : " + paramStr);
		if (!paramStr.equals(sign)) {
			throw ETUtil.buildException(BaseErrorDefine.SYS_INVALID_SIGNATURE);
		}
	}
	/**
	 * 解密sign_key。不同渠道过来解密方式不一样
	 * @param decodeKey
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 上午10:26:31
	 */
	public static String decodeSignKey(String decodeKey,RequestHeader header) throws Exception {
		if(NullUtil.isEmpty(decodeKey))
			return null;
		//LogUtil.getLogger(CCP.class).debug("xxxxxxx:"+ETUtil.isFromApp(header));
		if(RequestUtil.isFromApp(header)){
			return EncryptUtil.RSA.decodeBase64(decodeKey);
		}else{
			return EncryptUtil.RSA.decode(decodeKey);
		}
	}
	/**
	 * 对前台通过md5加密后的密码串，再加入混淆key后再一次进行md5加密
	 * 
	 * @param md5Password
	 * @return
	 * @author Wilson Wu
	 * @date 2015年9月17日
	 */
	public static String md5Password(String password) {
		logger.debug("___________orig pwd : "+password);
		String newpwd = EncryptUtil.MD5.encode(password, PASSWORD_MD5_KEY);
		logger.debug("___________new pwd : "+newpwd);
		return newpwd;
	}

	/**
	 * 检查操作合法性。 在该方法里，目标用户id和当前token对应的用户id必须保持一致，否则就会被认为是非法操作，会抛异常
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static void checkOperateOwner(RequestContext context,
			long targetUserId) throws Exception {
		long tokenUserId = context.getAccessUserId();
		if (tokenUserId != targetUserId) {
			// 如果目标用户id和当前token对应用户id则认为是非法操作
			throw ETUtil.buildException(BaseErrorDefine.SYS_NOT_ALLOWED);
		}
	}
	
	public static void checkOperateAdmin(User loginUser) throws Exception {
		Short userRole = loginUser.getRole();
		if (userRole == null || userRole != BaseConsDefine.USER_ROLE.ADMIN.value()) {
			//非管理员身份无法进行该操作
			throw ETUtil.buildException(BaseErrorDefine.SYS_NOT_ALLOWED);
		}
	}

	/**
	 * 根据token到SysLogin表中查询为未过期的记录
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static SysLogin querySysLoginByToken(String token)
			throws Exception {
		return DBHelper.getDao().querySingle(
				SysLogin.class,
				new DBCondition(SysLogin.JField.token, token),
				new DBCondition(SysLogin.JField.expireTime, DateUtil.now(),DBOperator.GREAT_EQUALS));
	}
	

	/**
	 * 上传图片
	 * 
	 * @param uploadFile
	 *            ,
	 * @param saveRelativePath
	 *            ,存储路径，要相对于存储的主目录
	 * @param needSplit
	 *            ,boolean，是否需要把图片拆分成大小图
	 * @return，如果不需要切分，就直接返回saveRelativePath,如果需要切分就返回带有${size 标识的相对路径
	 * @throws Exception
	 */
	/*public static String uploadImage(UploadFile uploadImage,String saveRelativePath, boolean needSplit) throws Exception {
		needSplit = false;// 不切割图片
		if (needSplit) {
			// 要拆分成大小图,大小图的文件地址格式为：xxx.${size}.extName,即在扩展名的.前面加上${size}
			StringBuffer resultPath = new StringBuffer(saveRelativePath);
			int extDotIndex = saveRelativePath.lastIndexOf(".");
			resultPath.insert(extDotIndex + 1, "${size}.");
			saveRelativePath = resultPath.toString();
			// 原尺寸
			String largPath = ETUtil.buildPicPath(saveRelativePath.replace(
					".${size}.", ".large."));
			FileUtil.newFile(largPath, uploadImage.getInputStream());
			logger.debug("success to upload file :" + largPath);

			String smallPath = ETUtil.buildPicPath(saveRelativePath.replace(
					".${size}.", ".small."));
			ImageUtil.zoom(largPath, smallPath, 450);
			logger.debug("success to zoom image :" + smallPath);

		} else {
			// 直接存成原图
			String largePath = ETUtil.buildPicPath(saveRelativePath);
			FileUtil.newFile(largePath, uploadImage.getInputStream());
			// 上传到七牛云服务器
			QiniuUtil.uploadPic(largePath, saveRelativePath);
			logger.debug("success to upload file :" + largePath);

		}

		return saveRelativePath;
		// return saveRelativePath;
	}*/

	public static void main(String[] args) {
		String sb = EncryptUtil.MD5.encode("amount=1&mobile=1&name=123&ticket_id=20020408&token=N0JUbYc6GdJb6Xb3Y5Ht1i2MKCY2d6uB&vcode=111111&visit_time=2016-03-30 08:30&wechat=o5V6SvzmAVfHKdo45L3AcSDJ-b08&zone=12JMT4CBRXtmzXFrHjlJhUpxHsSR6mPg3");
		// String largPath = path.replace(".${size}.", ".large.");
		System.out.println(sb);
		
		
		
	}

	public static SysLogin createLogin(long userId, BaseConsDefine.SYS_TYPE sysType,short channel) throws Exception {
		//先删除相同渠道的登录token
		DBHelper.getDao().delete(
				SysLogin.class,
				new DBCondition(SysLogin.JField.userId, userId),
				new DBCondition(SysLogin.JField.channel,channel));
		
		SysLogin loginEntity = new SysLogin();
		loginEntity.setUserId(userId);
		loginEntity.setToken(RandomUtil.getString(32));
		loginEntity.setSignKey(RandomUtil.getString(10));
		loginEntity.setExpireTime(DateUtil.offsetDate(DateUtil.now(), SysparamCmp.getInt(SysparamDefine.TOKEN_VALIDITY)));// 有效期为在后台配置
		loginEntity.setSysType(sysType.value());
		loginEntity.setChannel(channel);
		DBHelper.getDao().insert(loginEntity);
		return loginEntity;
	}
	
	
	/**
	 * 根据sex名称获取出枚举值
	 * 
	 * @param sex
	 * @return
	 * @author Wilson Wu
	 * @date 2015年9月25日
	 */
	/*public static Short getSexEnum(String sexName) {
		if (NullUtil.isEmpty(sexName))
			return null;
		else if (sexName.equals("男")) {
			return BaseConstantDefine.USER_SEX_MALE;
		} else if (sexName.equals("女")) {
			return BaseConstantDefine.USER_SEX_FEMALE;
		} else {
			return BaseConstantDefine.USER_SEX_OTHER;
		}
	}*/

	/**
	 * 两个数据库实体中进行数据匹配。目前只支持long字段之间的匹配
	 * 
	 * @param srcEntityList
	 * @param srcField
	 * @param targetEntityList
	 * @param targetField
	 * @param callback
	 * @throws Exception
	 * @author Wilson
	 * @date 下午7:13:25
	 */
	public static <T extends DBEntity> void matchEntity(List<T> a) {

	}

	public static void matchEntity(List<? extends DBEntity> srcEntityList,
			BaseJField srcField, List<? extends DBEntity> targetEntityList,
			BaseJField targetField, ICallback callback) throws Exception {
		DBEntity targetMatched = null;
		for (DBEntity srcEntity : srcEntityList) {
			targetMatched = null;
			Long srcVal = (Long) DBHelper.getEntityValue(srcEntity, srcField);
			if (srcVal != null) {
				for (DBEntity targetEntity : targetEntityList) {
					Long targetVal = (Long) DBHelper.getEntityValue(
							targetEntity, targetField);
					if (srcVal.longValue() == targetVal.longValue()) {
						targetMatched = targetEntity;
						break;
					}
				}
			}
			callback.execute(srcEntity, targetMatched);
		}
	}

	/**
	 * 把某张表中的某个字段自身加减操作。适用于sql：update tableA set field1=field1+N where xxx=yyy,
	 * 其中N是可以指定的数字
	 * 
	 * @param jf
	 *            ,要更新的字段，对应field1
	 * @param newValue
	 *            ，需要更新的值，对应于N
	 * @param conds
	 *            ，条件，对应于xxx=yyy
	 * @throws Exception
	 * @author Wilson
	 * @date 下午4:31:51
	 * 示例:
	 * 示例:
	 * CCP.updateFieldNum(UserSharing.JField.scanCount,31,new DBCondition(UserSharing.JField.type,1),new DBCondition(UserSharing.JField.sex,2));
	 * 表示把UserSharing表中type=1且sex=2记录的scanCount字段增加31；
	 * 同等sql：update user_sharing set scan_count=scan_count+31 where type = 1 and sex=2
	 * 
	 * CCP.updateFieldNum(UserSharing.JField.scanCount,-10,new DBCondition(UserSharing.JField.type,1),new DBCondition(UserSharing.JField.sex,2));
	 * 表示把UserSharing表中id=1002记录的scanCount字段减-10；
	 * 同等sql：update user_sharing set scan_count=scan_count-10 where type = 1 and sex=2
	 */
	public static int updateFieldNum(BaseJField jf, long newValue,DBCondition... conds) throws Exception {
		StringBuffer valueSb = new StringBuffer(jf.getColName());
		if (newValue > 0) {
			valueSb.append("+").append(newValue);
		} else {
			valueSb.append("-").append(-1 * newValue);
		}
		FieldUpdateExpression[] updateValues = new FieldUpdateExpression[] { new FieldUpdateExpression(jf,
				new NativeValue(valueSb.toString())) };
		return (Integer)new UpdateStatement(updateValues, conds).execute();

	}
	/**
	 * 作用同updateFieldNum(jf,newValue,conditions)，只是这里以主键id去作为条件
	 * @param jf
	 * @param newValue
	 * @param id
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:04:06
	 * 示例:
	 * CCP.updateFieldNum(UserSharing.JField.scanCount,31,1002);
	 * 表示把UserSharing表中id=1002记录的scanCount字段增加31；
	 * 同等sql：update user_sharing set scan_count=scan_count+31 where id = 1002
	 * 
	 * CCP.updateFieldNum(UserSharing.JField.scanCount,-10,1002);
	 * 表示把UserSharing表中id=1002记录的scanCount字段减-10；
	 * 同等sql：update user_sharing set scan_count=scan_count-10 where id = 1002
	 */
	public static void updateFieldNum(BaseJField jf, long newValue,long id) throws Exception {
		StringBuffer valueSb = new StringBuffer(jf.getColName());
		if (newValue > 0) {
			valueSb.append("+").append(newValue);
		} else {
			valueSb.append("-").append(-1 * newValue);
		}
		FieldUpdateExpression[] updateValues = new FieldUpdateExpression[] { 
				new FieldUpdateExpression(jf,new NativeValue(valueSb.toString())) };
		new UpdateStatement(updateValues, id).execute();

	}
	
	public static void reloadStaffList() throws Exception{
		CUST_SERVICE_USER_LIST = new ArrayList<MisStaff>();
		List<MisStaff> staffList = DBHelper.getDao().queryAll(MisStaff.class);
		if(NullUtil.isNotEmpty(staffList)){
			CUST_SERVICE_USER_LIST.addAll(staffList);
		}
	}
	
	public static List<MisStaff> getCustServiceStaffList(String[] acctArr)
			throws Exception {
		if(CUST_SERVICE_USER_LIST == null){
			reloadStaffList();
		}
		if(NullUtil.isEmpty(CUST_SERVICE_USER_LIST)){
			return null;
		}
		List<MisStaff> staffList = new ArrayList<MisStaff>();
		for(String acct : acctArr){
			for(MisStaff staff : CUST_SERVICE_USER_LIST){
				if(staff.getAccount().equalsIgnoreCase(acct)){
					staffList.add(staff);
					break;
				}
			}
		}
		return staffList;
	}

	/**
	 * 根据语言编码获取到对应语言名称。
	 * 
	 * @param langCode
	 * @return
	 * @author Wilson
	 * @date 下午7:43:22
	 */
	public static String getLangName(long langEnumId) {
		BaseEnum enumItem = EnumCmp.getEnum(langEnumId);
		return enumItem == null ? null : enumItem.getName();
	}
}
