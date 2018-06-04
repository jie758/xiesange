package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.request.UploadRequestBody.UploadFile;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.mis.MisStaff;
import com.elsetravel.mis.component.StaffCmp;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;
/**
 * 员工服务
 * @author Wilson 
 * @date 下午7:42:43
 */
@ETServiceAnno(name="staff",version="")
public class StaffService extends AbstractService{
	/**
	 * 添加新员工
	 * @author Wilson 
	 * @date 下午8:10:11
	 * @param
	 */
	public ResponseBody add(MisRequestContext context) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.Staff.name,
			ParamDefine.Staff.sex_name,
			ParamDefine.Staff.birthday,
			ParamDefine.Login.account,
			ParamDefine.Login.password
		);
		
		
		String name = reqbody.getString(ParamDefine.Staff.name);
		String account = reqbody.getString(ParamDefine.Login.account);
		String password = reqbody.getString(ParamDefine.Login.password);
		String sexName = reqbody.getString(ParamDefine.Staff.sex_name);
		String email = reqbody.getString(ParamDefine.Staff.email);
		String mobile = reqbody.getString(ParamDefine.Staff.mobile);
		
		Date birthday = reqbody.getDate(ParamDefine.Staff.birthday,DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
		
		MisStaff staffEntity = new MisStaff();
		long newid = dao().getSequence(MisStaff.class);
		staffEntity.setId(newid);
		staffEntity.setName(name);
		staffEntity.setAccount(account);
		staffEntity.setPassword(CCP.md5Password(password));
		staffEntity.setBirthday(birthday);
		staffEntity.setMobile(mobile);
		staffEntity.setEmail(email);
		staffEntity.setSex(CCP.getSexEnum(sexName));
		
		String picUrl = null;//头像的完整url
		if(NullUtil.isNotEmpty(reqbody.getUploadFiles())){
			UploadFile uploadFile = reqbody.getUploadFiles().get(0);//头像只会有一个
			//图片的存储路径,$ROOT_PATH/image/user/$userId/main.xxx,后缀由上传文件决定
			String picPath = CommonUtil.join("image/staff/",newid,"/profile.",uploadFile.getExtendName());
			picUrl = CCP.uploadImage(uploadFile, picPath,false);
			staffEntity.setProfilePic(picPath);
		}
		
		dao().insert(staffEntity);
		
		ResponseBody respBody = new ResponseBody("staffId",staffEntity.getId());
		if(picUrl != null){
			respBody.add("profilePic",picUrl);
		}
		
		return respBody;
		
	}
	
	/**
	 * 修改员工信息
	 * @author Wilson 
	 * @date 下午8:10:11
	 * @param
	 * @throws Exception 
	 */
	public ResponseBody modify(MisRequestContext context) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.Staff.staff_id
		);
		
		String name = reqbody.getString(ParamDefine.Staff.name);
		String account = reqbody.getString(ParamDefine.Login.account);
		String sexName = reqbody.getString(ParamDefine.Staff.sex_name);
		String email = reqbody.getString(ParamDefine.Staff.email);
		String mobile = reqbody.getString(ParamDefine.Staff.mobile);
		
		Date birthday = reqbody.getDate(ParamDefine.Staff.birthday,DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
		
		long staffId = reqbody.getLong(ParamDefine.Staff.staff_id);
		MisStaff staffEntity = StaffCmp.checkStaffExist(staffId);
		
		if(name != null){
			staffEntity.setName(name);
		}
		if(account != null){
			staffEntity.setAccount(account);
		}
		if(sexName != null){
			staffEntity.setSex(CCP.getSexEnum(sexName));
		}
		if(email != null){
			staffEntity.setEmail(email);
		}
		if(mobile != null){
			staffEntity.setMobile(mobile);
		}
		if(birthday != null){
			staffEntity.setBirthday(birthday);
		}
		
		
		String picUrl = null;//头像的完整url
		if(NullUtil.isNotEmpty(reqbody.getUploadFiles())){
			UploadFile uploadFile = reqbody.getUploadFiles().get(0);//头像只会有一个
			//图片的存储路径,$ROOT_PATH/image/user/$userId/main.xxx,后缀由上传文件决定
			String picPath = CommonUtil.join("image/staff/",staffId,"/profile.",uploadFile.getExtendName());
			picUrl = CCP.uploadImage(uploadFile, picPath,false);
			staffEntity.setProfilePic(picPath+"?t="+System.currentTimeMillis());//加一个t时间戳，防止前端从缓存导致无法更新
			
		}
		if(NullUtil.isNotEmpty(staffEntity._getSettedValue())){
			dao().updateById(staffEntity, staffId);
		}
		
		return picUrl==null ? null : new ResponseBody("result",picUrl);
	}
	
	/**
	 * 删除员工，一次只能删除一条
	 * @param context
	 * 			staff_id,需要删除的员工id
	 * @return
	 * @author Wilson 
	 * @date 下午8:34:49
	 */
	public ResponseBody remove(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.Staff.staff_id
		);
		
		long staffId = reqbody.getLong(ParamDefine.Staff.staff_id);
		dao().deleteById(MisStaff.class, staffId);
		
		return null;
	}
	
	/**
	 * 查询员工列表，需要分页查询。
	 * 注意需要过滤掉超级管理员(超级管理员的id<0)
	 * @param context
	 * 			name,按照姓名模糊查询
	 * 			account,按照账号模糊查询
	 * 			mobile,按照手机号模糊查询
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午8:34:49
	 */
	public ResponseBody queryList(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		String name = reqbody.getString(ParamDefine.Staff.name);
		String account = reqbody.getString(ParamDefine.Login.account);
		String mobile = reqbody.getString(ParamDefine.Staff.mobile);
		
		List<DBCondition> conds = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(name)){
			conds.add(new DBCondition(MisStaff.JField.name,"%"+name+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(account)){
			conds.add(new DBCondition(MisStaff.JField.account,"%"+account+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(mobile)){
			conds.add(new DBCondition(MisStaff.JField.mobile,"%"+mobile+"%",DBOperator.LIKE));
		}
		conds.add(new DBCondition(MisStaff.JField.id,0,DBOperator.GREAT));//过滤掉超级管理员
		
		DBCondition[] condArr = conds.toArray(new DBCondition[conds.size()]);
		
		List<MisStaff> staffList = dao().query(
			new QueryStatement(MisStaff.class,condArr)
				.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
		);
		if(NullUtil.isEmpty(staffList))
			return null;
		
		for(MisStaff staff : staffList){
			staff.setProfilePic(ETUtil.buildPicUrl(staff.getProfilePic()));
		}
		
		long count = dao().queryCount(MisStaff.class, condArr);
		
		ETUtil.clearDBEntityExtraAttr(staffList);
		
		return new ResponseBody("result",staffList).addTotalCount(count);
	}
}
