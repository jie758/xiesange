package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.component.ETEditorCmp;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.define.BaseErrorDefine;
import com.elsetravel.baseweb.notify.NotifyCmp;
import com.elsetravel.baseweb.notify.NotifyDefine;
import com.elsetravel.baseweb.notify.NotifyTargetHolder;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.request.UploadRequestBody.UploadFile;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.baseweb.util.VersionUtil;
import com.elsetravel.core.ParamHolder;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.orders.Orders;
import com.elsetravel.gen.dbentity.ticket.Ticket;
import com.elsetravel.gen.dbentity.ticket.TicketMain;
import com.elsetravel.gen.dbentity.ticket.TicketMainIntro;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.component.OrderCmp;
import com.elsetravel.mis.component.TicketCmp;
import com.elsetravel.mis.component.UserCmp;
import com.elsetravel.mis.define.ConstantDefine;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;
/**
 * 
 * @author Wilson Wu
 * @date 2015年8月14日
 *
 */

@ETServiceAnno(name="ticket",version="")
public class TicketService extends AbstractService{
	/**
	 * 获取一个ticket_main的id
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午1:12:52
	 */
	public ResponseBody getNewTicketMainId(MisRequestContext context) throws Exception {
		long newid = dao().getSequence(TicketMain.class);
		return new ResponseBody("newid",newid);
	}
	/**
	 * 新增/修改主票。如果是新增的话(id为空或者负数)，此时增加的主票状态是草稿。如果是修改的话，那保持修改前的状态不变
	 * @param context
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 上午9:56:26
	 */
	public ResponseBody saveTicketMain(MisRequestContext context) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		//RequestBody reqbody = context.getRequestBody();
		
		Long ticktMainId = reqbody.getLong(ParamDefine.Ticket.ticket_main_id);
		//Long guiderId = reqbody.getLong(ParamDefine.Ticket.guider_id);
		boolean isNew = (ticktMainId == null || ticktMainId < 0);
		
		String name = reqbody.getString(ParamDefine.Common.name);
		Long userId = reqbody.getLong(ParamDefine.User.user_id);
		if(userId == null){
			userId = reqbody.getLong(ParamDefine.Person.guider_id);//TODO 新版要去掉
		}
		Short canJoin = reqbody.getShort(ParamDefine.Ticket.can_join);
		Short tripCost = reqbody.getShort(ParamDefine.Ticket.trip_cost);
		UploadFile picFile = reqbody.getUploadFile(ParamDefine.Ticket.pic.name());
		String summary = reqbody.getString(ParamDefine.Ticket.summary);
		String detailIntro = reqbody.getString(ParamDefine.Ticket.detail_intro);
		String expenseIntro = reqbody.getString(ParamDefine.Ticket.expense_intro);
		String otherIntro = reqbody.getString(ParamDefine.Ticket.other_intro);
		
		Float costTime = reqbody.getFloat(ParamDefine.Ticket.cost_time);//前台传过来的单位是小时
		String countryCode = reqbody.getString(ParamDefine.Ticket.country_code);
		String cityCode = reqbody.getString(ParamDefine.Ticket.city_code);
		String latitude = reqbody.getString(ParamDefine.Ticket.latitude);
		String longtitude = reqbody.getString(ParamDefine.Ticket.longtitude);
		String placeName = reqbody.getString(ParamDefine.Ticket.place_name);
		String tags = reqbody.getString(ParamDefine.Ticket.tags);
		List<TicketMainIntro> introList = context.getRequestBody().getDBEntityList(ParamDefine.Ticket.intro_list, TicketMainIntro.class);
		
		TicketMain ticketMain = null;
		if(isNew){
			ticktMainId = ticktMainId != null ? -1*ticktMainId : DBHelper.getDao().getSequence(TicketMain.class);
			ticketMain = new TicketMain();
			
			RequestUtil.checkEmptyParams(reqbody, ParamDefine.Ticket.guider_id);
			ticketMain.setId(ticktMainId);//上传旅票背景需要用到id作为路径，所以先生成
			ticketMain.setUserId(userId);//负数表示客服发起的精品票
			ticketMain.setCanJoin(canJoin==null?1:canJoin);//默认都是可领取
			
			ticketMain.setMinPrice(0L);
			ticketMain.setFavCount(0L);
			ticketMain.setOrderCount(0L);
			ticketMain.setParticipateCount(0L);
			ticketMain.setStatus(BaseConstantDefine.TICKET_STATUS_EDIT);//新增的时候初始为草稿状态
		}else{
			ticketMain = TicketCmp.queryMain(ticktMainId);
		}
		
		if(NullUtil.isNotEmpty(name)){
			ticketMain.setName(name);
		}
		if(summary != null){
			ticketMain.setSummary(summary);
		}
			
		if(costTime != null){
			ticketMain.setCostTime(Float.valueOf(costTime*60).longValue());//转成整数存储，避免后续浮点数的运算
		}
		if(tripCost != null){
			ticketMain.setTripCost(tripCost);
			if(tripCost==ConstantDefine.TRIP_TYPE_HALFDAY){
				ticketMain.setCostTime(3*60L);
			}else if(tripCost==ConstantDefine.TRIP_TYPE_HALFDAY){
				ticketMain.setCostTime(8*60L);
			}
		}
		
		if(NullUtil.isNotEmpty(countryCode)){
			ticketMain.setCountryCode(countryCode);
		}
		if(NullUtil.isNotEmpty(cityCode)){
			ticketMain.setCityCode(cityCode);
		}
		if(NullUtil.isNotEmpty(latitude)){
			ticketMain.setLatitude(latitude);
		}
		if(NullUtil.isNotEmpty(longtitude)){
			ticketMain.setLongtitude(longtitude);
		}
		if(NullUtil.isNotEmpty(placeName)){
			ticketMain.setPlaceName(placeName);
		}
		if(NullUtil.isNotEmpty(detailIntro)){
			ticketMain.setDetailIntro(detailIntro);
		}
		if(NullUtil.isNotEmpty(expenseIntro)){
			ticketMain.setExpenseIntro(expenseIntro);
		}
		if(NullUtil.isNotEmpty(otherIntro)){
			ticketMain.setOtherIntro(otherIntro);
		}
		//标签
		if(tags != null){
			ticketMain.setTags(tags);
		}
		if(picFile != null){
			String picPath = CommonUtil.join("image/ticket/",ticketMain.getId(),"/cover.",picFile.getExtendName());
			CCP.uploadImage(picFile, picPath,true);
			ticketMain.setPic(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		if(NullUtil.isNotEmpty(introList)){
			ETEditorCmp.insertIntroList(TicketMainIntro.class,ticketMain.getId(),TicketMainIntro.JField.ticketMainId,introList);
		}
		
		if(isNew){
			DBHelper.getDao().insert(ticketMain);
		}else if(DBHelper.isModified(ticketMain)){
			DBHelper.getDao().updateById(ticketMain, ticketMain.getId());
			//如果是发布状态，那么修改过主票信息后要同步去修改索引表
			if(ticketMain.getStatus() == BaseConstantDefine.TICKET_STATUS_PUBLISH){
				TicketCmp.transferTicketMain(ticketMain);
				TicketCmp.updateTicketIndex(ticketMain, null);
			}
		}
		
		return new ResponseBody("newid",ticketMain.getId());
	}
	
	
	/**
	 * 发布主票信息。只能变更状态，不能修改其它资料
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:14:25
	 */
	public ResponseBody publishTicketMain(MisRequestContext context) throws Exception{
		
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Ticket.ticket_main_id);
		long ticketMainId = reqbody.getLong(ParamDefine.Ticket.ticket_main_id);
		TicketMain ticketMain = TicketCmp.checkTicketMainExist(ticketMainId);
		TicketCmp.checkForPublish(ticketMain);//只检查主数据完整性
		short origStatus = ticketMain.getStatus();
		if(origStatus != BaseConstantDefine.TICKET_STATUS_EDIT && 
				origStatus != BaseConstantDefine.TICKET_STATUS_OFFLINE){
			throw ETUtil.buildInvalidOperException();
		}
		//TicketFullPojo full = TicketCmp.saveTicket(ticket,context, true);
		ticketMain.setStatus(BaseConstantDefine.TICKET_STATUS_PUBLISH);//客服发布的直接置为发布状态
		ticketMain.setPublishTime(DateUtil.now());//第一次发布的话记录发布时间
		
		dao().updateById(ticketMain, ticketMainId);
		
		//创建索引
		TicketCmp.transferTicketMain(ticketMain);
		TicketCmp.updateTicketIndex(ticketMain, null);
		
		return null;
	}
	
	/**
	 * 修改旅票子票信息，支持新增和修改
	 * @param context
	 * 			ticket_id,票券id，修改的话一定要传入
	 * 			ticket_main_id,主票id
	 * 			price,价格，单位元
	 *			times,可预约的时间场次,每个元素都是时间格式，比如09:00，12:00，以逗号分隔
	 *			dates,不可预约的天数,每个元素都是时间格式，比如2012-01-12，以逗号分隔
     *
	 * @return
	 * 			ticket_id,返回当前新增的旅票id
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年8月14日
	 */
	public ResponseBody saveTicket(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		
		Long ticketId = reqbody.getLong(ParamDefine.Ticket.ticket_id);
		boolean isNew = ticketId == null;
		
		String timeList = reqbody.getString(ParamDefine.Ticket.times);
		String dateList = reqbody.getString(ParamDefine.Ticket.dates);
		Float price = reqbody.getFloat(ParamDefine.Ticket.price);//前台传过来的单位是元
		Long ticketMainId = reqbody.getLong(ParamDefine.Ticket.ticket_main_id);
		User guider = null;
		Ticket ticket = null;
		if(isNew){
			//新增相当于就是认领操作，则必须判断当前主票是否可以认领，是否是发布状态，是不是重复认领
			RequestUtil.checkEmptyParams(reqbody, 
				ParamDefine.Ticket.ticket_main_id,
				ParamDefine.Person.guider_id,
				ParamDefine.Ticket.times,
				ParamDefine.Ticket.dates,
				ParamDefine.Ticket.price
			);
			long guiderId = reqbody.getLong(ParamDefine.Person.guider_id);
			guider = UserCmp.checkExistByUserId(guiderId);
			
			TicketMain main = TicketCmp.checkTicketMainExist(ticketMainId);
			
			if(main.getCanJoin() == null || main.getCanJoin() == 0 || main.getStatus() != BaseConstantDefine.TICKET_STATUS_PUBLISH){
				//主票本身不可认领，或者没审核通过，不能被其他导游认领
				throw ETUtil.buildInvalidOperException();
			}
			
			Ticket userTicket = dao().querySingle(Ticket.class, 
					new DBCondition(Ticket.JField.ticketMainId,main.getId()),
					new DBCondition(Ticket.JField.userId,guiderId)
			);
			if(userTicket != null){
				throw ETUtil.buildInvalidOperException();
			}
			
			ticket = new Ticket();
			ticket.setUserId(guiderId);
			ticket.setTicketMainId(ticketMainId);
			ticket.setStatus(BaseConstantDefine.TICKET_STATUS_PUBLISH);
			ticket.setPublishTime(DateUtil.now());
		}else{
			ticket = TicketCmp.checkTicketExist(ticketId);
			ticketMainId = ticket.getTicketMainId();
		}
		
		if(price != null){
			ticket.setPrice(Float.valueOf(price*100).longValue());//转成单位分作为整数存储，避免后续浮点数的运算
		}
		if(timeList != null){
			ticket.setTimes(timeList);
		}
		if(dateList != null){
			ticket.setDates(dateList);
		}
		
		if(isNew){
			dao().insert(ticket);
		}else{
			dao().updateById(ticket, ticketId);
		}
		
		if(isNew || price != null){
			TicketCmp.updateMinPrice(ticketMainId);//更新当前主票里的最低价
			
			if(isNew){
				TicketCmp.updateParticipateCount(ticketMainId, 1);//更新当前主票所认领数
				TicketCmp.updateTicketIndexGuiders(ticketMainId,guider,"add");//索引表中把当前导游昵称添上，以供搜索
			}
		}
		
		/*TicketFullPojo ticketFull = TicketCmp.saveTicket(ticket, context, false);
		
		//发布后的修改操作也要更新索引
		if(ticketFull.getMain().getStatus() == BaseConstantDefine.TICKET_STATUS_PUBLISH){
			TicketCmp.transferTicketMain(ticketFull.getMain());
			TicketCmp.updateTicketIndex(ticketFull.getMain(),
					UserCmp.queryUserById(ticketFull.getMain().getUserId()));
		}*/
		
		
		
		return new ResponseBody("newid",ticket.getId());
		
		
		//return new ResponseBody("ticket_id",ticket.getId()).add("intro_ids", introids);
	}
	
	
	/**
	 * 下架某个子旅票。子旅票下架后，主旅票的状态是不受影响的，因为主旅票还存在其它认领导游
	 * @param context
	 * 			ticket_id,子旅票id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:26:38
	 */
	public ResponseBody offlineTicket(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Ticket.ticket_id
		);
		
		long ticketId = context.getRequestBody().getLong(ParamDefine.Ticket.ticket_id);
		Ticket ticket = TicketCmp.checkTicketExist(ticketId);
		
		if(ticket.getStatus() != BaseConstantDefine.TICKET_STATUS_PUBLISH){
			//只有在发布状态才能下线
			throw ETUtil.buildException(BaseErrorDefine.SYS_OPEARTION_INVALID);
		}
		//更新状态
		ticket.setStatus(BaseConstantDefine.TICKET_STATUS_OFFLINE);
		dao().updateById(ticket, ticketId);
		
		//更新主票中相关信息
		TicketCmp.updateMinPrice(ticket.getTicketMainId());//最低价
		TicketCmp.updateParticipateCount(ticket.getTicketMainId(), -1);
		
		User guider = UserCmp.queryUserById(ticket.getUserId());
		TicketCmp.updateTicketIndexGuiders(ticket.getTicketMainId(),guider,"delete");//重新更新索引表中导游名称，因为需要当前下架的导游去除掉，只能全量更新了
			
		return null;
	}
	
	/**
	 * 旅票上线。上线后该旅票又称为了发布状态
	 * 只有在下线状态才能上线。
	 * 上线后需要更新主票中认领数、最低价
	 * @param context
	 * 			ticket_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午11:23:43
	 */
	public ResponseBody onlineTicket(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Ticket.ticket_id
		);
		
		long ticketId = context.getRequestBody().getLong(ParamDefine.Ticket.ticket_id);
		Ticket ticket = TicketCmp.checkTicketExist(ticketId);
		
		if(ticket.getStatus() != BaseConstantDefine.TICKET_STATUS_OFFLINE){
			//只有在发布状态才能下线
			throw ETUtil.buildException(BaseErrorDefine.SYS_OPEARTION_INVALID);
		}
		
		//更新状态
		ticket.setStatus(BaseConstantDefine.TICKET_STATUS_PUBLISH);
		dao().updateById(ticket, ticketId);
		
		//更新主票中相关信息
		TicketCmp.updateMinPrice(ticket.getTicketMainId());//最低价
		TicketCmp.updateParticipateCount(ticket.getTicketMainId(), 1);
		
		User guider = UserCmp.queryUserById(ticket.getUserId());
		TicketCmp.updateTicketIndexGuiders(ticket.getTicketMainId(),guider,"add");
		return null;
	}
	
	
	/**
	 * 删除某张子旅票。
	 * 删除和下架的区别，下架后仍然会存在用户的旅票列表中，只是状态变了，还可以重新上架，但是删除后数据就会彻底没了。
	 * 注意:删除的时候需要判断当前是否还存在该子旅票未完成的订单，如果存在则不能删除。
	 * @param context
	 * 			ticket_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:30:38
	 */
	public ResponseBody removeTicket(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Ticket.ticket_id
		);
		
		long ticketId = context.getRequestBody().getLong(ParamDefine.Ticket.ticket_id);
		
		
		long orderCount = dao().queryCount(Orders.class, 
			new DBCondition(Orders.JField.ticketId,ticketId),
			new DBCondition(Orders.JField.status,OrderCmp.getUnFinishedStatus(BaseConstantDefine.USER_ROLE_GUIDER),DBOperator.IN)
		);
		if(orderCount > 0){
			//存在未完成订单，不允许删除
			throw ETUtil.buildException(ErrorDefine.TICKET_NOTALLOWED_REMOVE);
		}
		
		Ticket ticket = TicketCmp.checkTicketExist(ticketId);
		
		dao().deleteById(Ticket.class, ticketId);
		
		//更新主票中相关信息
		TicketCmp.updateMinPrice(ticket.getTicketMainId());//最低价
		TicketCmp.updateParticipateCount(ticket.getTicketMainId(), -1);
		
		User guider = UserCmp.queryUserById(ticket.getUserId());
		TicketCmp.updateTicketIndexGuiders(ticket.getTicketMainId(),guider,"delete");//重新更新索引表中导游名称，因为需要当前下架的导游去除掉，只能全量更新了
		
		return null;
	}
	
	/**
	 * 发布旅行票。和save方法逻辑基本一致。只是在save方法的基础上，还会把旅票的status改成发布状态
	 * @param context
	 * 			参数同save方法,其中ticket_id必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:51:16
	 */
	/*public ResponseBody publish(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Ticket.ticket_id);
		long ticketId = reqbody.getLong(ParamDefine.Ticket.ticket_id);
		Ticket ticket = TicketCmp.checkTicketExist(ticketId);
		
		TicketFullPojo full = TicketCmp.saveTicket(ticket,context, true);
		
		//创建索引
		TicketCmp.transferTicketMain(full.getMain());
		TicketCmp.updateTicketIndex(full.getMain(), UserCmp.queryUserById(full.getMain().getUserId()));
		
		return null;
	}*/
	
	/**
	 * 查询旅票图文明细
	 * @param context
	 * 			ticket_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:36:11
	 */
	public ResponseBody queryIntroList(MisRequestContext context) throws Exception {
		Thread.sleep(500);//TODO 临时方案，图文编辑有问题，新版后去掉
		RequestBody reqbody = (RequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Ticket.ticket_id);
		
		Long ticketId = reqbody.getLong(ParamDefine.Ticket.ticket_id);
		
		List<TicketMainIntro> detailList = dao().query(new QueryStatement(
				TicketMainIntro.class, 
				new DBCondition(TicketMainIntro.JField.ticketMainId,ticketId)
		).appendOrderField(TicketMainIntro.JField.id));
		
		
		ETEditorCmp.wrapIntroList(detailList);
		
		return new ResponseBody("result",detailList);
		
	}
	
	
	/**
	 * 查询旅票列表，需要分页查询。根据创建日期从近到远排列
	 * 注意需要过滤掉超级管理员(超级管理员的id<0)
	 * @param context
	 * 			name,旅票名称，模糊查询
	 * 			user_id,按照归属导游/商户查询
	 * 			ticket_main_id,按照主票id查询
	 * 			status,状态，草稿/审核/下线/已发布
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午8:34:49
	 */
	public ResponseBody queryList(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		Long userId = reqbody.getLong(ParamDefine.User.user_id);
		Long mainId = reqbody.getLong(ParamDefine.Ticket.ticket_main_id);
		Short status = reqbody.getShort(ParamDefine.Common.status);
		
		List<DBCondition> conds = new ArrayList<DBCondition>();
		
		if(mainId != null){
			conds.add(new DBCondition(Ticket.JField.ticketMainId,mainId));
		}
		if(userId != null){
			conds.add(new DBCondition(Ticket.JField.userId,userId));
		}
		if(status != null){
			conds.add(new DBCondition(Ticket.JField.status,status));
		}else{
			conds.add(new DBCondition(Ticket.JField.status,BaseConstantDefine.TICKET_STATUS_REMOVED,DBOperator.GREAT));
		}
		
		DBCondition[] condArr = conds.toArray(new DBCondition[conds.size()]);
		
		List<Ticket> ticketList = dao().query(
			new QueryStatement(Ticket.class,condArr)
				.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
				.appendOrderFieldDesc(Ticket.JField.createTime)
		);
		if(NullUtil.isEmpty(ticketList))
			return null;
		
		long count = dao().queryCount(Ticket.class, condArr);//记录总数
		
		/*Set<Long> mainIds = ETUtil.buildEntityIdList(ticketList, Ticket.JField.ticketMainId);
		List<TicketMain> mainList = DBHelper.getDao().query(new QueryStatement(TicketMain.class,
				new DBCondition(TicketMain.JField.id,mainIds,DBOperator.IN)));
		
		*/
		Set<Long> guiderIds = ETUtil.buildEntityIdList(ticketList, Ticket.JField.userId);
		List<User> guiderList = DBHelper.getDao().query(new QueryStatement(User.class,
				new DBCondition(User.JField.id,guiderIds,DBOperator.IN))
					.appendQueryField(User.JField.id,User.JField.nickname,User.JField.email)
		);
		
		TicketCmp.transferTicketInfo(ticketList, true);
		
		ResponseBody body = new ResponseBody();
		body.addTotalCount(count);
		if(VersionUtil.is2_0(context.getRequestHeader())){
			//TODO
			body.add("ticketList", ticketList).add("userList", guiderList);
		}else{
			body.add("ticket_list", ticketList).add("guider_list", guiderList);
		}
		
		return body;
	}
	/**
	 * 查询主票列表，需要分页显示
	 * @param context
	 * 			name,按照旅票名称模糊查询
	 * 			guider_id,按照旅票主体的创建者进行查询
	 * 			status,按照状态过滤，主体票只有草稿、审核、发布
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:18:17
	 */
	public ResponseBody queryMainList(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		String name = reqbody.getString(ParamDefine.Common.name);
		Long guiderId = reqbody.getLong(ParamDefine.User.user_id);
		Short status = reqbody.getShort(ParamDefine.Common.status);
		
		List<DBCondition> mainConds = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(name)){
			mainConds.add(new DBCondition(TicketMain.JField.name,"%"+name+"%",DBOperator.LIKE));
		}
		if(guiderId != null){
			mainConds.add(new DBCondition(TicketMain.JField.userId,guiderId));
		}
		if(status != null){
			mainConds.add(new DBCondition(TicketMain.JField.status,status));
		}
		
		DBCondition[] condArr = mainConds.toArray(new DBCondition[mainConds.size()]);
		
		List<TicketMain> mainList = dao().query(
			new QueryStatement(TicketMain.class,condArr)
					.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
					.appendOrderFieldDesc(Ticket.JField.createTime)
		);
		
		if(NullUtil.isEmpty(mainList))
			return null;
		Set<Long> userIdSet = new HashSet<Long>();
		for(TicketMain main : mainList){
			TicketCmp.transferTicketMain(main);
			userIdSet.add(main.getUserId());
		}
		
		List<User> userList = UserCmp.queryUserByIds(userIdSet, User.JField.id,User.JField.nickname);
		
		
		long totalCount = dao().queryCount(TicketMain.class,condArr);
		
		ResponseBody body = new ResponseBody();
		body.addTotalCount(totalCount).add("userList", userList);
		if(VersionUtil.is2_0(context.getRequestHeader())){
			body.add("ticketMainList", mainList);
		}else{
			body.add("ticket_main_list", mainList);
		}
		
		return body;
	}
	
	/**
	 * 查询主票详情信息
	 * @param context
	 * 			merchant_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午5:47:20
	 */
	public ResponseBody queryMainDetail(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Ticket.ticket_main_id
		);
		long ticketMainId = context.getRequestBody().getLong(ParamDefine.Ticket.ticket_main_id);
		
		TicketMain main = TicketCmp.queryMain(ticketMainId);
		
		User user = UserCmp.queryUserById(main.getUserId(),User.JField.id,User.JField.nickname);
		
		List<TicketMainIntro> introList = dao().query(new QueryStatement(
				TicketMainIntro.class, 
				new DBCondition(TicketMainIntro.JField.ticketMainId,ticketMainId)
		).appendOrderField(TicketMainIntro.JField.id));
		ETEditorCmp.wrapIntroList(introList);
		
		return new ResponseBody("ticketMain",main)
					.add("user", user)
					.add("introList", introList)
				;
		
	}
		
	/**
	 * 上传旅票图文编辑中的图片。一次性只能上传一张
	 * @param context
	 * 			ticket_main_id,必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:35:56
	 */
	public ResponseBody uploadIntroPic(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Ticket.ticket_main_id);
		long ticketMainId = context.getRequestBody().getLong(ParamDefine.Ticket.ticket_main_id);
		UploadRequestBody requestBody = (UploadRequestBody)context.getRequestBody();
		
		List<UploadFile> files = requestBody.getUploadFiles();
		
		if(NullUtil.isEmpty(files))
			return null;
		/*UploadFile file = files.get(0);
		String picPath = CommonUtil.join("/image/ticket/",ticketMainId,"/intro/",file.getFileName(),".",file.getExtendName());
		picPath = CCP.uploadImage(file, picPath,false);*/
		
		String saveFold = CommonUtil.join("image/ticket/",ticketMainId,"/intro");
		String picPath = ETEditorCmp.uploadPics(files, saveFold)[0];
		
		return new ResponseBody("picPath",picPath);
	}
	/**
	 * 保存图文编辑数据。图片的话需要前台先调用uploadIntroPics，把图片都上传好。这里只需要把图片url传过来即可。
	 * 注意，处理逻辑是在ticket_detail全量替换，即把老的数据先删除掉，把新的数据添加进去
	 * @param context
	 * 			ticket_id,必传
	 * 			intro_list，是一个数组，每个数组中的元素是图文明细中的一个条目，属性为：
	 * 				id,
	 * 				type,明细类型,image/text
	 * 				value,明细值，如果是text的话就填文本内容，如果是pic的话就要和下一个参数图片二进制流里的name对应上
	 * 				style,样式，尤其是text类型
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:36:22
	 */
	public ResponseBody saveIntroList(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Ticket.ticket_main_id);
		long ticketMainId = context.getRequestBody().getLong(ParamDefine.Ticket.ticket_main_id);
		//String introList = context.getRequestBody().getString(ParamDefine.Ticket.intro_list);
		List<TicketMainIntro> commitList = context.getRequestBody().getDBEntityList(ParamDefine.Ticket.intro_list, TicketMainIntro.class);
		List<TicketMainIntro> insertList = ETEditorCmp.insertIntroList(TicketMainIntro.class,ticketMainId,TicketMainIntro.JField.ticketMainId,commitList);
		
		List<Long> ids = new ArrayList<Long>();
		for(TicketMainIntro entity : insertList){
			ids.add(entity.getId());
		}
		
		return new ResponseBody("newids",ids);
	}
	
	/**
	 * 审核通过某张旅票的发布申请
	 * @param context
	 * 			ticket_main_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:18:16
	 */
	public ResponseBody approveTicketMain(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Ticket.ticket_main_id);
		long ticketMainId = context.getRequestBody().getLong(ParamDefine.Ticket.ticket_main_id);
		TicketMain ticketMain = TicketCmp.checkTicketMainExist(ticketMainId);
		
		if(ticketMain.getStatus() != BaseConstantDefine.TICKET_STATUS_APPROVING){
			//只有在认证申请审核状态下才能调用该接口
			throw ETUtil.buildInvalidOperException();
		}
		
		ticketMain.setStatus(BaseConstantDefine.TICKET_STATUS_PUBLISH);
		
		dao().updateById(ticketMain, ticketMainId);
		
		//子旅票也要更新成发布状态
		Ticket ticket = new Ticket();
		ticket.setStatus(BaseConstantDefine.TICKET_STATUS_PUBLISH);
		dao().update(ticket, new DBCondition(Ticket.JField.ticketMainId,ticketMainId));
		
		//发送通知，目标：导游
		User guider = UserCmp.queryUserById(ticketMain.getUserId(), User.JField.mobile,User.JField.id,User.JField.email);
		NotifyCmp.sendTemplate(
			NotifyDefine.CodeDefine.approve_ticket_publish, 
			new ParamHolder(NotifyDefine.ApproveTicketPublish.ticket_name.name(),ticketMain.getName()),
			new NotifyTargetHolder().addUser(guider)
		);
		
		return null;
	}
	/**
	 * 审核驳回某张旅票的发布申请
	 * @param context
	 * 			ticket_main_id,
	 * 			memo,驳回理由
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:18:16
	 */
	public ResponseBody rejectTicketMain(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Ticket.ticket_main_id);
		long ticketMainId = context.getRequestBody().getLong(ParamDefine.Ticket.ticket_main_id);
		TicketMain ticketMain = TicketCmp.checkTicketMainExist(ticketMainId);
		
		if(ticketMain.getStatus() != BaseConstantDefine.TICKET_STATUS_APPROVING){
			//只有在认证申请审核状态下才能调用该接口
			throw ETUtil.buildInvalidOperException();
		}
		ticketMain.setStatus(BaseConstantDefine.TICKET_STATUS_EDIT);//重置草稿状态
		
		dao().updateById(ticketMain, ticketMainId);
		
		//子旅票也要重置成草稿状态
		Ticket ticket = new Ticket();
		ticket.setStatus(BaseConstantDefine.TICKET_STATUS_EDIT);
		dao().update(ticket, new DBCondition(Ticket.JField.ticketMainId,ticketMainId));
		
		//发送通知，目标：导游
		String memo = context.getRequestBody().getString(ParamDefine.Common.memo);
		User guider = UserCmp.queryUserById(ticketMain.getUserId(), User.JField.mobile,User.JField.id,User.JField.email);
		NotifyCmp.sendTemplate(
			NotifyDefine.CodeDefine.reject_ticket_publish, 
			new ParamHolder(NotifyDefine.RejectTicketPublish.ticket_name.name(),ticketMain.getName())
					.addParam(NotifyDefine.RejectTicketPublish.reason.name(),memo),
			new NotifyTargetHolder().addUser(guider)
		);
		
		return null;
	}
	
	
	/**
	 * 删除旅票主体,该操作将会导致:
	 * 	需要删除索引表里信息;
	 * 	所有子票将会删除
	 * 主票删除的模式还没想好，暂时不提供接口
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:38:04
	 */
	/*public ResponseBody removeTicketMain(MisRequestContext context) throws Exception{
		
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, ParamDefine.Ticket.ticket_main_id);
		long ticketMainId = reqbody.getLong(ParamDefine.Ticket.ticket_main_id);
		
		dao().deleteById(TicketMain.class, ticketMainId);
		dao().deleteById(TicketIndex.class, ticketMainId);
		dao().delete(Ticket.class, new DBCondition(Ticket.JField.ticketMainId,ticketMainId));
		dao().delete(TicketMainIntro.class, new DBCondition(TicketMainIntro.JField.ticketMainId,ticketMainId));
		
		return null;
	}*/
	
	/**
	 * 导游认领某张旅行票。
	 * 认领后的旅票是直接发布状态的
	 * @param context
	 * 			ticket_main_id,认领的旅票主体id
	 * 			guider_id,认领的导游id
	 * 			price,新旅票的价格
	 * 			date_list,String,可服务的日期,每个元素都是星期几,比如"0,1,2,3,4",0表示周日
	 * 			time_list,array<String>,可预约的时间场次,每个元素都是时间格式，比如[09:00，12:00]
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午5:50:23
	 */
	/*public ResponseBody participateTicket(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(reqbody, 
				ParamDefine.Ticket.ticket_main_id,
				ParamDefine.Guider.guider_id,
				ParamDefine.Ticket.price,
				ParamDefine.Ticket.time_list,
				ParamDefine.Ticket.date_list
		);
		
		long mainId = reqbody.getLong(ParamDefine.Ticket.ticket_main_id);
		long guiderId = reqbody.getLong(ParamDefine.Ticket.ticket_main_id);
		TicketMain main = TicketCmp.checkTicketMainExist(mainId);
		
		if(main.getParticipable() == null || main.getParticipable() == 0 || main.getStatus() != BaseConstantDefine.TICKET_STATUS_PUBLISH){
			//主票本身不可认领，或者没审核通过，不能被其他导游认领
			throw ETUtil.buildInvalidOperException("this ticket can not be participated");
		}
		User guider = UserCmp.queryUserById(guiderId);
		if(guider == null){
			throw ETUtil.buildInvalidOperException("guider not exist");
		}
		
		//查询当前用户是否已经认领过该父级旅票
		Ticket userTicket = dao().querySingle(Ticket.class, 
				new DBCondition(Ticket.JField.ticketMainId,main.getId()),
				new DBCondition(Ticket.JField.userId,guiderId)
		);
		if(userTicket != null){
			throw ETUtil.buildInvalidOperException("you have already participated");
		}
		
		String timeList = reqbody.getString(ParamDefine.Ticket.time_list);
		String dateList = reqbody.getString(ParamDefine.Ticket.date_list);
		Float price = reqbody.getFloat(ParamDefine.Ticket.price);//前台传过来的单位是元
		
		Ticket pTicket = new Ticket();
		pTicket.setTicketMainId(mainId);
		pTicket.setUserId(guiderId);
		pTicket.setStatus(BaseConstantDefine.TICKET_STATUS_PUBLISH);//认领后就是发布状态
		pTicket.setPrice(Float.valueOf(price*100).longValue());
		pTicket.setDates(dateList);
		pTicket.setTimes(timeList);
		
		dao().insert(pTicket);
		
		
		TicketCmp.updateMinPrice(mainId);//更新当前主票里的最低价
		TicketCmp.updateParticipateCount(mainId, 1);//更新当前主票所认领数
		
		TicketCmp.updateTicketIndexGuiders(mainId,guider,"add");//索引表中把当前导游昵称添上，以供搜索
		
		return new ResponseBody("ticket_id",pTicket.getId());
		
	}*/
	
	
	
	
}
