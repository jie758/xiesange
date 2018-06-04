package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.pojo.CodeNamePojo;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.QiniuUtil;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.guider.GuiderWithdrawApply;
import com.elsetravel.gen.dbentity.mis.MisStaff;
import com.elsetravel.gen.dbentity.orders.Orders;
import com.elsetravel.gen.dbentity.person.Person;
import com.elsetravel.gen.dbentity.sys.SysLogin;
import com.elsetravel.gen.dbentity.ticket.TicketMain;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.component.StaffCmp;
import com.elsetravel.mis.define.ConstantDefine;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.mis.define.OrderStatusDefine;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.pojo.AutoMatchItem;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.FieldPair;
import com.elsetravel.orm.pojo.JoinQueryData;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.sql.DBOrCondition;
import com.elsetravel.orm.statement.field.BaseJField;
import com.elsetravel.orm.statement.query.QueryStatement;
@ETServiceAnno(name="common",version="")
public class CommonService extends AbstractService {
	/**
	 * 前台的用户登录输入手机和密码后的验证
	 * @param context
	 * 			account,登录的账号，这个账号可以是手机/邮箱
	 * 			password,密码，前台是md5形式加密过来
	 * @return
	 * @throws Exception
	 */
	public ResponseBody login(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody,ParamDefine.Login.account,ParamDefine.Login.password);
		
		String account = reqbody.getString(ParamDefine.Login.account);
		String password = reqbody.getString(ParamDefine.Login.password);
		
		MisStaff staffEntity = StaffCmp.queryByAccount(account);
		if(staffEntity == null){
			throw ETUtil.buildException(ErrorDefine.LOGIN_ACCOUNT_NOTEXIST);
		}
		
		if(!staffEntity.getPassword().equals(CCP.md5Password(password))){
			throw ETUtil.buildException(ErrorDefine.LOGIN_PWD_NOT_MATCH);
		}
		
		SysLogin login = CCP.buildLogin(staffEntity.getId(),BaseConstantDefine.SYS_TYPE_MIS,null,context.getRequestHeader());
		dao().insert(login);
		
		staffEntity.setProfilePic(ETUtil.buildPicUrl(staffEntity.getProfilePic()));
		//context.setAccessToken(new AccessToken(login,staffEntity));
		
		//context.getSysSn().setUserId(staffEntity.getId());
		//context.getSysSn().setLoginId(login.getId());
		
		return new ResponseBody("token",login.getToken()).add("staff", staffEntity);
	}
	
	/**
	 * 获取可刷新的缓存定义列表
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:31:48
	 */
	public ResponseBody getRefreshableCacheList(MisRequestContext context) throws Exception {
		List<CodeNamePojo> result = new ArrayList<CodeNamePojo>();
		result.add(new CodeNamePojo("banner","首页广告"));
		result.add(new CodeNamePojo("medal","勋章定义"));
		result.add(new CodeNamePojo("tag","标签定义"));
		result.add(new CodeNamePojo("config","系统参数"));
		result.add(new CodeNamePojo("enum","枚举值"));
		result.add(new CodeNamePojo("ticketcatalog","旅票类别"));
		result.add(new CodeNamePojo("rewardrule","奖励规则"));
		
		return new ResponseBody("result",result);
	}
	
	/**
	 * 快速匹配
	 * @param context
	 * 			type,固定枚举值:
	 * 					user:用户
	 * 					guider:导游，必须是认证通过的
	 * 					ticketMain : 按照主票名称，必须是已发布状态
	 * 			field,匹配的字段名称,默认是匹配name
	 * 			value,匹配值
	 * 			extra_condition,额外的过滤条件
	 * @return
	 * @author Wilson 
	 * @date 上午11:36:00
	 */
	public ResponseBody quickMatch(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		String type = reqbody.getString(ParamDefine.QuickMatch.type);
		String field = reqbody.getString(ParamDefine.QuickMatch.field);
		String value = reqbody.getString(ParamDefine.QuickMatch.value);
		
		List<DBCondition> conditions = new ArrayList<DBCondition>();
		
		
		List<AutoMatchItem> result = null;
		if(type.equals("guider")){
			BaseJField pyheaderJF = User.JField.pyHeader;
			BaseJField pyJF = User.JField.py;
			conditions.add(new DBCondition(
				new DBCondition(User.JField.nickname,"%"+value+"%",DBOperator.LIKE),
				//拼音的话都采用开始左匹配，否则吴宇杰(wuyujie),输个y都会被匹配出来
				new DBOrCondition(pyheaderJF,value+"%",DBOperator.LIKE),
				new DBOrCondition(pyJF,value+"%",DBOperator.LIKE)
			));
			
			conditions.add(new DBCondition(Person.JField.status,ConstantDefine.GUIDER_APPLY_APPROVED));
			
			DBCondition[] condArr = conditions.toArray(new DBCondition[conditions.size()]);
			List<JoinQueryData> joinList = dao().queryJoin(new QueryStatement(User.class,condArr)
					.appendQueryField(User.JField.id,User.JField.email,User.JField.nickname)
					.appendRange(0, 10)
					.appendJoin(Person.class, new FieldPair(Person.JField.id,User.JField.id))
			);
			
			
			if(NullUtil.isNotEmpty(joinList)){
				result = new ArrayList<AutoMatchItem>();
				String label = null;
				User user = null;
				for(JoinQueryData joinData : joinList){
					user = joinData.getResult(User.class);
					label = user.getNickname()+"("+user.getEmail()+")";
					result.add(new AutoMatchItem(user.getId(),label,user.getNickname()));
				}
			}
		}else if(type.equals("user")){
			BaseJField pyheaderJF = User.JField.pyHeader;
			BaseJField pyJF = User.JField.py;
			conditions.add(new DBCondition(User.JField.nickname,"%"+value+"%",DBOperator.LIKE));
			
			//拼音的话都采用开始左匹配，否则吴宇杰(wuyujie),输个y都会被匹配出来
			conditions.add(new DBOrCondition(pyheaderJF,value+"%",DBOperator.LIKE));
			conditions.add(new DBOrCondition(pyJF,value+"%",DBOperator.LIKE));
			
			List<User> list = dao().query(new QueryStatement(
					User.class, conditions.toArray(new DBCondition[conditions.size()])
			).appendQueryField(User.JField.id,User.JField.email,User.JField.nickname).appendRange(0, 10));
			
			
			if(NullUtil.isNotEmpty(list)){
				result = new ArrayList<AutoMatchItem>();
				String label = null;
				for(User user : list){
					label = user.getNickname()+"("+user.getEmail()+")";
					result.add(new AutoMatchItem(user.getId(),label,user.getNickname()));
				}
			}
		}else if(type.equals("ticketMain")){
			BaseJField pyheaderJF = TicketMain.JField.pyHeader;
			BaseJField pyJF = TicketMain.JField.py;
			conditions.add(new DBCondition(TicketMain.JField.name,"%"+value+"%",DBOperator.LIKE));
			//拼音的话都采用开始左匹配，否则吴宇杰(wuyujie),输个y都会被匹配出来
			conditions.add(new DBOrCondition(pyheaderJF,value+"%",DBOperator.LIKE));
			conditions.add(new DBOrCondition(pyJF,value+"%",DBOperator.LIKE));
			
			
			conditions.add(new DBCondition(
				new DBCondition(TicketMain.JField.name,"%"+value+"%",DBOperator.LIKE),
				//拼音的话都采用开始左匹配，否则吴宇杰(wuyujie),输个y都会被匹配出来
				new DBOrCondition(pyheaderJF,value+"%",DBOperator.LIKE),
				new DBOrCondition(pyJF,value+"%",DBOperator.LIKE)
			));
			
			conditions.add(new DBCondition(TicketMain.JField.status,BaseConstantDefine.TICKET_STATUS_PUBLISH));
			
			List<TicketMain> list = dao().query(new QueryStatement(
					TicketMain.class, conditions.toArray(new DBCondition[conditions.size()])
			).appendQueryField(TicketMain.JField.id,TicketMain.JField.name).appendRange(0, 10));
			
			
			if(NullUtil.isNotEmpty(list)){
				result = new ArrayList<AutoMatchItem>();
				String label = null;
				for(TicketMain item : list){
					label = "["+item.getId()+"]"+item.getName();
					result.add(new AutoMatchItem(item.getId(),label,item.getName()));
				}
			}
		}
		
		return new ResponseBody("result",result);
		
	}
	
	/**
	 * 查询待处理事项, 这些事项包括：
	 * 1、导游申请
	 * 2、提现申请
	 * 3、旅票发布申请
	 * 4、订单退订申请
	 * 只返回这些事项的待处理数量
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午10:09:46
	 */
	public ResponseBody queryTodoTaskList() throws Exception{
		//导游申请
		long guiderApply = dao().queryCount(Person.class, 
				new DBCondition(Person.JField.status,ConstantDefine.GUIDER_APPLY_APPROVING));
		//提现申请
		long withdrawApply = dao().queryCount(GuiderWithdrawApply.class, 
				new DBCondition(GuiderWithdrawApply.JField.isApproved,0));
		//旅票发布申请
		long ticketApply = dao().queryCount(TicketMain.class, 
				new DBCondition(TicketMain.JField.status,BaseConstantDefine.TICKET_STATUS_APPROVING));
		//订单退订申请
		long orderCancleApply = dao().queryCount(Orders.class, 
				new DBCondition(Orders.JField.status,OrderStatusDefine.CANCEL_APPLY.getState()));
		
		return new ResponseBody("guiderApplyCount",guiderApply)
					.add("withdrawApplyCount", withdrawApply)
					.add("orderCancleApplyCount", orderCancleApply)
					.add("ticketApplyCount", ticketApply);
		
	}
	
	/**
	 * 获取七牛上传token
	 * @param context
	 * 			keys,string,多个值用,分隔,如果要获取默认的key，请用'default',比如:'default,image/user/1001/main'
	 * @return
	 * 		按照keys的顺序，返回对应的token
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午5:09:14
	 */
	public ResponseBody getQiniuTokens(MisRequestContext context) throws Exception{
		//RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Qiniu.keys);
		String keys = context.getRequestBody().getString(ParamDefine.Qiniu.keys);
		List<String> tokens = null;
		if(NullUtil.isNotEmpty(keys)){
			String[] keyArr = keys.split(",");
			tokens = new ArrayList<String>();
			for(String key : keyArr){
				tokens.add(QiniuUtil.getUpToken(key));
			}
		}
		return new ResponseBody("tokens",tokens).add("defaultToken", QiniuUtil.getUpToken());
	}
}
