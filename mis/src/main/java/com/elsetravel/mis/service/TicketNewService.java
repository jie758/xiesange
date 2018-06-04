package com.elsetravel.mis.service;

import java.util.List;

import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.component.ETEditorCmp;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.request.UploadRequestBody.UploadFile;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.ticket.TicketMain;
import com.elsetravel.gen.dbentity.ticket.TicketMainIntro;
import com.elsetravel.mis.component.TicketCmp;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.DBHelper;
/**
 * 
 * @author Wilson Wu
 * @date 2015年8月14日
 *
 */

@ETServiceAnno(name="ticket_new",version="")
public class TicketNewService extends AbstractService{
	/**
	 * 新增/修改主票。如果是新增的话，此时增加的主票状态是草稿。如果是修改的话，那保持修改前的状态不变
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
		boolean isNew = (ticktMainId == null);
		
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
			ticktMainId = DBHelper.getDao().getSequence(TicketMain.class);
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
		if(NullUtil.isNotEmpty(detailIntro)){
			ticketMain.setDetailIntro(detailIntro);
		}
		if(NullUtil.isNotEmpty(expenseIntro)){
			ticketMain.setExpenseIntro(expenseIntro);
		}
		if(NullUtil.isNotEmpty(otherIntro)){
			ticketMain.setOtherIntro(otherIntro);
		}	
		if(costTime != null){
			ticketMain.setCostTime(Float.valueOf(costTime*60).longValue());//转成整数存储，避免后续浮点数的运算
		}
		if(tripCost != null){
			ticketMain.setTripCost(tripCost);
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
	
}
