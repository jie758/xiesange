package com.elsetravel.mis.service;

import java.util.ArrayList;

import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.notify.NotifyCmp;
import com.elsetravel.baseweb.notify.NotifyDefine;
import com.elsetravel.baseweb.notify.NotifyTargetHolder;
import com.elsetravel.baseweb.pojo.MerchantHolder;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.request.UploadRequestBody.UploadFile;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.ParamHolder;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.EncryptUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.util.PinyinUtil;
import com.elsetravel.gen.dbentity.mch.MchAgency;
import com.elsetravel.gen.dbentity.sys.SysLogin;
import com.elsetravel.gen.dbentity.ticket.Ticket;
import com.elsetravel.gen.dbentity.ticket.TicketMain;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.gen.dbentity.user.UserBalanceDetail;
import com.elsetravel.mis.component.MerchantCmp;
import com.elsetravel.mis.component.TicketCmp;
import com.elsetravel.mis.component.UserCmp;
import com.elsetravel.mis.component.VCodeCmp;
import com.elsetravel.mis.define.ConstantDefine;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.FieldPair;
import com.elsetravel.orm.pojo.JoinQueryData;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.field.JoinPart;
import com.elsetravel.orm.statement.query.QueryStatement;
@ETServiceAnno(name="agency",version="")
/**
 * 地接社服务类
 * @author Wilson 
 * @date 下午12:35:01
 */
public class AgencyService extends AbstractService {
	
	
	
	/**
	 * 查询商户列表，支持过滤查询，支持分页。
	 * @param context
	 * 			nickname,模糊搜索
	 * 			linkman,模糊搜索
	 * 			status,0驳回，1待审核，99已审核
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:26:51
	 */
	public ResponseBody queryList(MisRequestContext context) throws Exception{
		String nickname = context.getRequestBody().getString(ParamDefine.User.nickname);
		String linkman = context.getRequestBody().getString(ParamDefine.Merchant.linkman);
		String email = context.getRequestBody().getString(ParamDefine.User.email);
		Short status = context.getRequestBody().getShort(ParamDefine.Common.status);
		List<DBCondition> condList = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(nickname)){
			condList.add(new DBCondition(User.JField.nickname,"%"+nickname+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(email)){
			condList.add(new DBCondition(User.JField.email,"%"+email+"%",DBOperator.LIKE));
		}
		if(NullUtil.isNotEmpty(linkman)){
			condList.add(new DBCondition(MchAgency.JField.linkman,"%"+linkman+"%",DBOperator.LIKE));
		}
		
		if(status != null){
			condList.add(new DBCondition(MchAgency.JField.status,status));
		}
		DBCondition[] conds = condList.toArray(new DBCondition[condList.size()]);
		QueryStatement st = new QueryStatement(User.class,conds);
		JoinPart joinPart = new JoinPart(MchAgency.class, new FieldPair(MchAgency.JField.id,User.JField.id));
		st.appendJoin(joinPart);
		st.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()));
		List<JoinQueryData> resultList = dao().queryJoin(st);
		if(NullUtil.isEmpty(resultList))
			return null;
		
		long totalCount = dao().queryJoinCount(User.class,new JoinPart[]{joinPart} ,conds);
		
		List<MchAgency> mchList = new ArrayList<MchAgency>(); 
		for(JoinQueryData data : resultList){
			mchList.add(MerchantCmp.merge(data.getResult(User.class), data.getResult(MchAgency.class)));
		}
		
		return new ResponseBody("merchantList",mchList).addTotalCount(totalCount);
	}
	
	/**
	 * 查询某个商户详细资料信息
	 * @param context
	 * 			merchant_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:42:16
	 */
	public ResponseBody queryDetail(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Merchant.merchant_id);
		long mchId = reqbody.getLong(ParamDefine.Merchant.merchant_id);
		MerchantHolder<MchAgency> mchHolder = MerchantCmp.queryMerchantHolder(mchId,MchAgency.class);
		if(mchHolder == null)
			return null;
		
		MchAgency mch = MerchantCmp.merge(mchHolder.getUser(),mchHolder.getMerchant());
		
		return new ResponseBody("merchant",mch);
	}
	
	/**
	 * 查询某个商户的旅票列表
	 * @param context
	 * 			merchant_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:19:05
	 */
	public ResponseBody queryTicketList(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Merchant.merchant_id);
		long mchId = reqbody.getLong(ParamDefine.Merchant.merchant_id);
		
		MerchantCmp.checkMerchantExist(mchId);
		
		JoinPart joinPart = new JoinPart(TicketMain.class,new FieldPair(TicketMain.JField.id,Ticket.JField.ticketMainId));
		DBCondition cond = new DBCondition(Ticket.JField.userId,mchId);
		QueryStatement st = new QueryStatement(Ticket.class,cond);
		st.appendJoin(joinPart)
		  .appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
		  .appendQueryField(TicketMain.JField.name,Ticket.JField.id,Ticket.JField.price,Ticket.JField.dates,Ticket.JField.times,Ticket.JField.status);
		
		List<JoinQueryData> queryList = dao().queryJoin(st);
		if(NullUtil.isEmpty(queryList))
			return null;
		
		long totalCount = dao().queryJoinCount(Ticket.class, new JoinPart[]{joinPart}, cond);
		
		List<Ticket> ticketList = new ArrayList<Ticket>();
		Ticket tempTicket = null;
		for(JoinQueryData queryData : queryList){
			tempTicket = queryData.getResult(Ticket.class);
			TicketCmp.transferTicketInfo(tempTicket);
			tempTicket.addAttribute("name", queryData.getResult(TicketMain.class).getName());
			ticketList.add(tempTicket);
		}
		
		return new ResponseBody("ticketList",ticketList).addTotalCount(totalCount);
	
	}
	
	/**
	 * 新增或者修改商户资料.
	 * 
	 * @param context
	 * 			merchant_id,为空表示新增商户，不为空表示修改
	 * 			nickname,
	 * 			email,
	 * 			profile_pic,
	 * 			country_code,
	 * 			city_code,
	 * 
	 * 			name,
	 * 			tags,
	 * 			languages,
	 * 			memo,
	 * 			phone,
	 * 			linkman,
	 * 			identity_type,证件类型
	 * 			identity_pic,blob,证件照
	 * 			website,
	 * 			website2,
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:40:22
	 */
	public ResponseBody save(MisRequestContext context) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		
		Long merchantId = reqbody.getLong(ParamDefine.Merchant.merchant_id);
		String nickname = reqbody.getString(ParamDefine.User.nickname);
		String countryCode = reqbody.getString(ParamDefine.User.country_code);
		String cityCode = reqbody.getString(ParamDefine.User.city_code);
		String email = reqbody.getString(ParamDefine.User.email);
		UploadFile picFile = reqbody.getUploadFile(ParamDefine.User.profile_pic.name());
		
		boolean isNew = merchantId == null;
		
		if(isNew){
			merchantId = dao().getSequence(User.class);
		}
		
		//处理user表信息
		User user = new User();
		if(NullUtil.isNotEmpty(nickname)){
			user.setNickname(nickname);
			user.setPy(PinyinUtil.getFullSpell(nickname));
			user.setPyHeader(PinyinUtil.getFirstSpell(nickname));
		}
		if(NullUtil.isNotEmpty(email)){
			user.setEmail(email);
			user.setIsEmailChecked((short)1);
		}
		if(NullUtil.isNotEmpty(countryCode)){
			user.setCountryCode(countryCode);
		}
		if(NullUtil.isNotEmpty(cityCode)){
			user.setCityCode(cityCode);
		}
		if(picFile != null){
			String picPath = UserCmp.buildProfilePicPath(merchantId,picFile.getExtendName());
			picPath = CCP.uploadImage(picFile, picPath,true);
			user.setProfilePic(picPath+"?t="+System.currentTimeMillis());//加一个t时间戳，防止前端从缓存导致无法更新
		}
		if(isNew){
			user.setId(merchantId);
			user.setType(BaseConstantDefine.USER_TYPE_MERCHANT);
			user.setSubType(BaseConstantDefine.USER_SUBTYPE_MCH_AGENCY);
			user.setSrc(BaseConstantDefine.USER_SRC_MIS);
			user.setSts((short)1);
			dao().insert(user);
		}else if(DBHelper.isModified(user)){
			dao().updateById(user, merchantId);
		}
		
		
		//处理merchant表
		MchAgency merchant = new MchAgency();
		String phone = reqbody.getString(ParamDefine.Merchant.phone);
		String linkman = reqbody.getString(ParamDefine.Merchant.linkman);
		String website = reqbody.getString(ParamDefine.Merchant.website);
		String website2 = reqbody.getString(ParamDefine.Merchant.website2);
		String name = reqbody.getString(ParamDefine.Common.name);
		String tags = reqbody.getString(ParamDefine.Person.tags);
		String languages = reqbody.getString(ParamDefine.Person.languages);
		String memo = reqbody.getString(ParamDefine.Common.memo);
		Short idType = reqbody.getShort(ParamDefine.Merchant.identity_type);
		//上传证件照
		UploadFile identityPic = reqbody.getUploadFile(ParamDefine.Merchant.identity_pic.name());
		String identityPath = null;
		if(identityPic != null){
			identityPath = CommonUtil.join("image/user/",merchantId,"/merchant_identity_pic.",identityPic.getExtendName());
			identityPath = CCP.uploadImage(identityPic,identityPath,true);
		}
				
		
		if(NullUtil.isNotEmpty(name)){
			merchant.setName(name);
			merchant.setPy(PinyinUtil.getFullSpell(name));
			merchant.setPyHeader(PinyinUtil.getFirstSpell(name));
		}
		if(tags != null){
			merchant.setTags(tags);
		}
		if(languages != null){
			merchant.setLanguages(languages);
		}
		if(memo != null){
			merchant.setIntro(memo);
		}
		if(phone != null){
			merchant.setPhone(phone);
		}
		if(linkman != null){
			merchant.setLinkman(linkman);
		}
		if(website != null){
			merchant.setWebsite(website);
		}	
		if(website2 != null){
			merchant.setWebsite2(website2);
		}
		if(idType != null){
			merchant.setIdentityType(idType);
		}
		if(NullUtil.isNotEmpty(identityPath)){
			merchant.setIdentityPic(identityPath);
		}
		
		
		if(isNew){
			merchant.setId(merchantId);
			merchant.setSts((short)1);
			merchant.setStatus(ConstantDefine.MERCHANT_APPLY_APPROVED);
			merchant.setApproveTime(context.getRequestDate());
			merchant.setApproverId(context.getAccessUserId());
			dao().insert(merchant);
		}else if(DBHelper.isModified(merchant)){
			dao().updateById(merchant, merchantId);
		}
		
		return new ResponseBody("newid",merchantId);
	}
	
	/**
	 * 移除商户
	 * @param context
	 * 			mechant_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:32:52
	 */
	public ResponseBody remove(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Merchant.merchant_id);
		long merchantId = reqbody.getLong(ParamDefine.Merchant.merchant_id);
		
		MerchantCmp.checkMerchantExist(merchantId);
		
		//删除用户表记录
		dao().deleteById(User.class, merchantId);
		//删除商户表记录
		dao().deleteById(MchAgency.class, merchantId);
		//删除旅票记录
		dao().delete(Ticket.class, new DBCondition(Ticket.JField.userId,merchantId));
		//删除资金明细记录
		dao().delete(UserBalanceDetail.class, new DBCondition(UserBalanceDetail.JField.userId,merchantId));
		//删除登录痕迹
		dao().delete(SysLogin.class, new DBCondition(SysLogin.JField.userId,merchantId));
		
		return null;
	}
	
	/**
	 * 商户入驻审核通过
	 * @param context
	 * 			merchant_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:17:56
	 */
	public ResponseBody approve(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Merchant.merchant_id);
		long merchantId = reqbody.getLong(ParamDefine.Merchant.merchant_id);
		
		MchAgency merchant = MerchantCmp.checkMerchantExist(merchantId);
		
		merchant.setStatus(ConstantDefine.MERCHANT_APPLY_APPROVED);
		merchant.setApproveMemo(null);//有可能之前是驳回的，有驳回原因
		merchant.setApproverId(context.getAccessUserId());
		merchant.setApproveTime(context.getRequestDate());
		dao().updateById(merchant, merchantId);
		
		User user = UserCmp.queryUserById(merchantId);
		user.setIsEmailChecked((short)1);
		dao().updateById(user, merchantId);
		
		//发送邮件
		NotifyCmp.sendTemplate(
				NotifyDefine.CodeDefine.mch_regist_approved, 
				new ParamHolder(NotifyDefine.Common.nickname.name(),user.getNickname())
						.addParam(NotifyDefine.MchRegApproved.url.name(), ETUtil.OFFICIAL_WEBSITE),
				new NotifyTargetHolder().addEmail(user.getEmail())
		);
		
		return null;
	}
	
	/**
	 * 商户入驻审核驳回
	 * @param context
	 * 			merchant_id,
	 * 			reason,驳回原因
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:18:03
	 */
	public ResponseBody reject(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Merchant.merchant_id);
		long merchantId = reqbody.getLong(ParamDefine.Merchant.merchant_id);
		String reason = reqbody.getString(ParamDefine.Approve.reason);
		
		MchAgency merchant = MerchantCmp.checkMerchantExist(merchantId);
		
		merchant.setStatus(ConstantDefine.MERCHANT_APPLY_NONE);
		merchant.setApproveMemo(reason);
		merchant.setApproverId(context.getAccessUserId());
		merchant.setApproveTime(context.getRequestDate());
		dao().updateById(merchant, merchantId);
		
		User user = UserCmp.queryUserById(merchantId);
		String email = user.getEmail();
		
		//生成一个随机验证码，需要附到email发送的url上
		String vQuerycode = VCodeCmp.generateVCode(email, ConstantDefine.VCODE_TYPE_MCH_QUERY, 6, "24h");
		String vRegcode = VCodeCmp.generateVCode(email, ConstantDefine.VCODE_TYPE_REGISTER, 6, "24h");
		
		String urlParam = EncryptUtil.Base64.encode("type=2&email="+email+"&vcode_query="+vQuerycode+"&vcode_regist="+vRegcode);
		
		//发送提醒
		NotifyCmp.sendTemplate(
				NotifyDefine.CodeDefine.mch_regist_rejected, 
				new ParamHolder(NotifyDefine.Common.nickname.name(),user.getNickname())
						.addParam(NotifyDefine.MchRegRejected.url.name(), ETUtil.buildHostUrl(context, "/merchant-enter.html?"+urlParam))
						.addParam(NotifyDefine.MchRegRejected.reason.name(), reason),
				new NotifyTargetHolder().addEmail(email)
		);
		
		
		return null;
	}
}
