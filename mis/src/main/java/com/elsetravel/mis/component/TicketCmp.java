package com.elsetravel.mis.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.component.CountryCmp;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.define.BaseErrorDefine;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.JsonUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.xml.BaseNode;
import com.elsetravel.gen.dbentity.base.BaseTag;
import com.elsetravel.gen.dbentity.orders.Orders;
import com.elsetravel.gen.dbentity.ticket.Ticket;
import com.elsetravel.gen.dbentity.ticket.TicketIndex;
import com.elsetravel.gen.dbentity.ticket.TicketMain;
import com.elsetravel.gen.dbentity.ticket.TicketMainIntro;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.mis.define.OrderStatusDefine;
import com.elsetravel.mis.pojo.TicketFullPojo;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.FieldUpdateExpression;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.field.summary.MinQueryField;
import com.elsetravel.orm.statement.query.QueryStatement;
import com.elsetravel.orm.statement.update.UpdateStatement;

public class TicketCmp {
	public static final String KEY_INTRO_IDS = "intro_ids";
	
	/**
	 * 根据ticket_id，查询完整信息
	 * @param ticketId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:14:34
	 */
	public static TicketFullPojo queryTicketFull(long ticketId) throws Exception{
		Ticket ticket = queryTicket(ticketId);
		if(ticket == null)
			return null;
		TicketMain main = queryMain(ticket.getTicketMainId());
		if(main == null)
			return null;
					
		return new TicketFullPojo(main,ticket);
	}
	
	/**
	 * 根据id查询出旅行票实体
	 * @param ticketId
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月21日
	 */
	public static Ticket queryTicket(long ticketId) throws Exception{
		Ticket ticket = DBHelper.getDao().queryById(Ticket.class,ticketId);
		return ticket;
	}
	
	
	/**
	 * 根据用户ID，检测导游是否存在。如果存在则返回Guider实体；如果不存在则抛出异常
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static Ticket checkTicketExist(long ticketId) throws Exception{
		Ticket ticket = queryTicket(ticketId);
		if(ticket == null){
			throw ETUtil.buildException(ErrorDefine.TICKET_NOTEXIST);
		}
		return ticket;
	}
	/**
	 * 根据用户ID，检测导游是否存在。如果存在则返回Guider实体；如果不存在则抛出异常
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static TicketMain checkTicketMainExist(long ticketMainId) throws Exception{
		TicketMain main = queryMain(ticketMainId);
		if(main == null){
			throw ETUtil.buildException(ErrorDefine.TICKET_NOTEXIST);
		}
		return main;
	}
	
	public static TicketMain queryMainByTicketId(long ticketId) throws Exception{
		TicketMain main = DBHelper.getDao().querySingle(TicketMain.class,
				new DBCondition(TicketMain.JField.id,
					new QueryStatement(Ticket.class,new DBCondition(Ticket.JField.id,ticketId))
						.appendQueryField(Ticket.JField.ticketMainId),
					DBOperator.IN
		));
		return main;
	}
	public static TicketMain queryMain(long mainId) throws Exception{
		return DBHelper.getDao().queryById(TicketMain.class,mainId);
	}
	
	/**
	 * 保存/发布旅票。包括修改和新增。
	 * @param context
	 * @param isPublish,true表示发布旅票
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:51:48
	 * @deprecated
	 */
	/*public static TicketFullPojo saveTicket(Ticket ticket,RequestContext context,boolean isPublish) throws Exception{
		boolean isNew = (ticket == null);
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		String name = reqbody.getString(ParamDefine.Common.name);
		Float costTime = reqbody.getFloat(ParamDefine.Ticket.cost_time);//前台传过来的单位是小时
		String countryCode = reqbody.getString(ParamDefine.Ticket.country_code);
		String cityCode = reqbody.getString(ParamDefine.Ticket.city_code);
		String latitude = reqbody.getString(ParamDefine.Ticket.latitude);
		String longtitude = reqbody.getString(ParamDefine.Ticket.longtitude);
		String placeName = reqbody.getString(ParamDefine.Ticket.place_name);
		String tags = reqbody.getString(ParamDefine.Ticket.tags);
		UploadFile picFile = reqbody.getUploadFile("pic");
		
		String timeList = reqbody.getString(ParamDefine.Ticket.times);
		String dateList = reqbody.getString(ParamDefine.Ticket.dates);
		Float price = reqbody.getFloat(ParamDefine.Ticket.price);//前台传过来的单位是元
		Long guiderId = reqbody.getLong(ParamDefine.Common.user_id);
		TicketMain ticketMain = null;
		if(isNew){
			ticket = new Ticket();
			ticketMain = new TicketMain();
			
			RequestUtil.checkEmptyParams(reqbody, ParamDefine.Common.user_id);
			
			ticketMain.setUserId(guiderId);
			ticketMain.setParticipable((short)1);//默认都是可领取
			ticketMain.setId(DBHelper.getDao().getSequence(TicketMain.class));//上传旅票背景需要用到id作为路径，所以先生成
			ticketMain.setMinPrice(0L);
			ticketMain.setFavCount(0L);
			ticketMain.setOrderCount(0L);
			ticketMain.setParticipateCount(0L);
			ticketMain.setStatus(BaseConstantDefine.TICKET_STATUS_EDIT);
			
			ticket.setUserId(context.getAccessUserId());
			ticket.setTicketMainId(ticketMain.getId());
			ticket.setStatus(BaseConstantDefine.TICKET_STATUS_EDIT);
		}else{
			ticket = TicketCmp.checkTicketExist(ticket.getId());
			ticketMain = TicketCmp.queryMain(ticket.getTicketMainId());
		}
		//设置main部分
		if(name != null){
			ticketMain.setName(name);
		}
			
		if(costTime != null){
			ticketMain.setCostTime(Float.valueOf(costTime*60).longValue());//转成整数存储，避免后续浮点数的运算
		}
		if(countryCode != null){
			ticketMain.setCountryCode(countryCode);
		}
		if(cityCode != null){
			ticketMain.setCityCode(cityCode);
		}
		if(latitude != null){
			ticketMain.setLatitude(latitude);
		}
		if(longtitude != null){
			ticketMain.setLongtitude(longtitude);
		}
		if(placeName != null){
			ticketMain.setPlaceName(placeName);
		}
		//标签
		if(tags != null){
			ticketMain.setTags(tags);
		}
		if(picFile != null){
			String picPath = CommonUtil.join("image/ticket/",ticketMain.getId(),"/cover.",picFile.getExtendName());
			picPath = CCP.uploadFile(picFile, picPath,true);
			ticketMain.setPic(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		//处理part部分
		if(price != null){
			ticket.setPrice(Float.valueOf(price*100).longValue());//转成单位分作为整数存储，避免后续浮点数的运算
		}
		if(timeList != null){
			ticket.setTimes(timeList);
		}
		if(dateList != null){
			ticket.setDates(dateList);
		}
		
		if(isPublish){
			checkForPublish(ticketMain);
			ticket.setStatus(BaseConstantDefine.TICKET_STATUS_PUBLISH);//审核中
			ticket.setPublishTime(DateUtil.now());//第一次发布的话记录发布时间
			
			ticketMain.setStatus(BaseConstantDefine.TICKET_STATUS_PUBLISH);//审核中
			ticketMain.setPublishTime(DateUtil.now());//第一次发布的话记录发布时间
			ticketMain.setParticipateCount(1L);//发布后认领数为1
			ticketMain.setMinPrice(ticket.getPrice());//发布后因为只有一张票，所以最低价就是当前票的价格
		}
		
		if(isNew){
			DBHelper.getDao().insert(ticketMain);
			DBHelper.getDao().insert(ticket);
		}else if(DBHelper.isModified(ticketMain)){
			DBHelper.getDao().updateById(ticketMain, ticketMain.getId());
			DBHelper.getDao().updateById(ticket, ticket.getId());
		}
		
		return new TicketFullPojo(ticketMain,ticket);
	}*/
	
	/**
	 * 检查发布时数据完整性。
	 * @param main，必传，必须是完整数据实体
	 * @param ticket，可空，如果不需要检查就不传（适用于客服发布的主票），如果要检查则传，但必须是完整数据实体
	 * @return
	 * @author Wilson 
	 * @date 上午10:20:17
	 */
	public static boolean checkForPublish(TicketMain main){
		//只有草稿状态的票券才能发布,否则需要报错
		if(main.getStatus() != BaseConstantDefine.STATUS_NONE){
			throw ETUtil.buildException(BaseErrorDefine.SYS_OPEARTION_INVALID);
		}
		if(main.getUserId() == null){
			throw ETUtil.buildException(ErrorDefine.TICKET_INFO_MISSING,"您还没有选择旅票归属的导游");
		}
		/*if(NullUtil.isEmpty(main.getPic())){
			throw ETUtil.buildException(ErrorDefine.TICKET_INFO_MISSING,"您还没有上传旅行票封面照片");
		}*/
		if(NullUtil.isEmpty(main.getName())){
			throw ETUtil.buildException(ErrorDefine.TICKET_INFO_MISSING,"您还没有填写旅行票名称");
		}
		if(main.getTripCost() == null){
			throw ETUtil.buildException(ErrorDefine.TICKET_INFO_MISSING,"您还没有填写旅行票时长类型");
		}
		if(NullUtil.isEmpty(main.getCountryCode())){
			throw ETUtil.buildException(ErrorDefine.TICKET_INFO_MISSING,"你还没有填写旅行票的国家/地区");
		}
		return true;
	}
	
	
	
	/*
	public static Ticket saveTicket(RequestContext context,boolean isPublish) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		
		//RequestUtil.checkEmptyParams(reqbody, ParamDefine.Ticket.ticket_id);
		
		Long ticketId = reqbody.getLong(ParamDefine.Ticket.ticket_id);
		Long guiderId = reqbody.getLong(ParamDefine.Guider.guider_id);
		Ticket ticket = null;
		if(ticketId == null){
			ticket = new Ticket();
			ticket.setUserId(guiderId);
			ticket.setStatus(BaseConstantDefine.STATUS_EDIT);
			ticketId = DBHelper.getDao().getSequence(Ticket.class);
		}else{
			ticket = DBHelper.getDao().queryById(Ticket.class, ticketId);
			//检查ticket的存在性
			if(ticket == null){
				throw ETUtil.buildException(ErrorDefine.TICKET_NOTEXIST);
			}
		}
		
		
		
		String name = reqbody.getString(ParamDefine.Common.name);
		Float price = reqbody.getFloat(ParamDefine.Ticket.price);//前台传过来的单位是元
		Float costTime = reqbody.getFloat(ParamDefine.Ticket.cost_time);//前台传过来的单位是小时
		String attrTags = reqbody.getString(ParamDefine.Ticket.attr_tags);
		Long catalogId = reqbody.getLong(ParamDefine.Ticket.catalog_id);
		String availableTimes = reqbody.getString(ParamDefine.Ticket.available_time_list);
		String disableDates = reqbody.getString(ParamDefine.Ticket.disable_date_list);
		String countryCode = reqbody.getString(ParamDefine.Ticket.country_code);
		String cityCode = reqbody.getString(ParamDefine.Ticket.city_code);
		String placeName = reqbody.getString(ParamDefine.Ticket.place_name);
		String latitude = reqbody.getString(ParamDefine.Ticket.latitude);
		String longtitude = reqbody.getString(ParamDefine.Ticket.longtitude);
		Short participable = 1;//目前全部都是可认领的。reqbody.getShort(ParamDefine.Ticket.participable);
		String timeList = reqbody.getString(ParamDefine.Ticket.time_list);
		String dateList = reqbody.getString(ParamDefine.Ticket.date_list);
		
		if(name != null){
			ticket.setName(name);
		}
		if(price != null){
			ticket.setPrice(Float.valueOf(price*100).longValue());//转成单位分作为整数存储，避免后续浮点数的运算
		}
		if(maxNumber != null){
			ticket.setMaxNumber(maxNumber);
		}
		
		if(costTime != null){
			ticket.setCostTime(Float.valueOf(costTime*60).longValue());//转成整数存储，避免后续浮点数的运算
		}
		if(catalogId != null){
			ticket.setCatalogId(catalogId);
		}
		
		if(countryCode != null){
			ticket.setCountryCode(countryCode);
		}
		if(cityCode != null){
			ticket.setCityCode(cityCode);
		}
		if(placeName != null){
			ticket.setPlaceName(placeName);
		}
		if(latitude != null){
			ticket.setLatitude(latitude);
		}
		if(longtitude != null){
			ticket.setLongtitude(longtitude);
		}
		
		List<Long> tagIds = null;
		if(attrTags != null){
			if(ticket.getId() != null){
				//老数据的话可能存在标签数据，先全部删除
				DBHelper.getDao().delete(TicketTag.class, new DBCondition(TicketTag.JField.ticketId,ticketId));
			}
			if(NullUtil.isNotEmpty(attrTags)){
				String[] tagArr = attrTags.split(",");
				List<TicketTag> tagList = new ArrayList<TicketTag>();
				tagIds = new ArrayList<Long>();
				for(String tagStr : tagArr){
					TicketTag tagEntity = new TicketTag();
					tagEntity.setTagId(Long.parseLong(tagStr));
					tagEntity.setTicketId(ticketId);
					tagList.add(tagEntity);
					tagIds.add(tagEntity.getTagId());
				}
				DBHelper.getDao().insertBatch(tagList);
			}
		}
		if(availableTimes != null){
			availableTimes = availableTimes.replace(" : ", ":");//前台传过来会有空格
			ticket.setAvailableTimes(availableTimes);
		}
		
		if(disableDates != null){
			ticket.setDisableDates(disableDates.toString());
		}
		
		if(timeList != null){
			ticket.setTimes(timeList);
		}
		if(dateList != null){
			ticket.setDates(dateList);
		}
		
		if(isPublish && checkForPublish(ticket)){
			ticket.setStatus(BaseConstantDefine.STATUS_COMMITTED);
		}
		
		//处理封面
		UploadFile coverFile = reqbody.getUploadFile(ParamDefine.Ticket.pic.name());
		if(coverFile != null){
			String picPath = CommonUtil.join("image/ticket/",ticketId,"/cover.",coverFile.getExtendName());
			picPath = CCP.uploadFile(coverFile, picPath,true);
			ticket.setPic(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		
		if(ticket.getId() == null){
			ticket.setId(ticketId);
			ticket.setParticipable(participable != null ? participable : (short)0);
			ticket.setParentTicketId(0L);//新创建的肯定是一级票，因此parentTicketId=0
			DBHelper.getDao().insert(ticket);//新增数据插入
		}else if(NullUtil.isNotEmpty(ticket._getSettedValue())){
			DBHelper.getDao().updateById(ticket, ticketId);
		}
		updateTicketIndex(ticket,tagIds);
		//处理图文明细,注意intro_list如果没有这个字段参数，则表示不做需改；如果传了这个字段参数，但是值里是一个空数组则表示全部删除
		List<TicketDetail> commitList = reqbody.getDBEntityList(ParamDefine.Ticket.intro_list, TicketDetail.class);
		long[] newIntroids = TicketCmp.dealIntroList(ticketId, reqbody, commitList);
		ticket.addAttribute(KEY_INTRO_IDS, newIntroids);
		return ticket;
	}*/
	
	
	
	/**
	 * 处理旅行票中的图文内容简介明细。内容简介是图文模式，可以有图片也可以有文本描述，每个都是一个细节item。
	 * 旅行票明细的处理方式是：每次保存的时候都要把原记录全部删除掉，再把新纪录按照顺序添加进去。因为前端涉及到排序，可以随意调整每个明细项目的位置，
	 * 如果按照正规方式单个单个的来处理非常的麻烦，所以索性把原先的先删除掉，再把最终的插入。
	 * 因此前台要按照顺序把明细项目组织好传送过来，有一个要点要注意，如果某个明细项没有修改过，那么实体中的value字段不要填值，系统会自动去把老记录中的value
	 * 取出来设置进去
	 * @param context
	 * 			ticket_id,修改的旅行票id
	 * 			detail_list,修改的明细内容实体，是一个数组，每个数组中的元素属性为：
	 * 				id,
	 * 				type,明细类型,pic/text
	 * 				value,明细值，如果是text的话就填文本内容，如果是pic的话就要和下一个参数图片二进制流里的name对应上
	 * 				is_title,是否是标题，0不是 ,1是
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月14日
	 */
	public static long[] dealIntroList(long ticketId,UploadRequestBody reqbody,List<TicketMainIntro> commitList) throws Exception{
		if(commitList == null){
			return null;//为null表示不做修改
		}
		if(commitList.size() == 0){
			clearIntroList(ticketId);//不为null但数组为空表示全部清空
			return null;
		}
		//先查出已经存在的明细
		List<TicketMainIntro> existList = DBHelper.getDao().query(TicketMainIntro.class, new DBCondition(TicketMainIntro.JField.ticketMainId,ticketId));
		long[] newids = DBHelper.getDao().getSequence(TicketMainIntro.class, commitList.size());
		List<TicketMainIntro> insertList = new ArrayList<TicketMainIntro>();
		for(int i=0;i<commitList.size();i++){
			TicketMainIntro commitDetail = commitList.get(i);
			Long id = commitDetail.getId();
			long newid = newids[i];
			TicketMainIntro insertItem = null;
			
			if(id != null && id > 0){
				//说明是老记录，先找到匹配的
				for(TicketMainIntro exist : existList){
					if(exist.getId() == id.longValue()){
						insertItem = exist;
						insertItem.setCreateTime(null);
						insertItem.setUpdateTime(null);
						insertItem.setSn(null);
						break;
					}
				}
				String value = commitDetail.getValue();
				String style = commitDetail.getStyle();
				if(value != null){
					insertItem.setValue(commitDetail.getValue());
				}
				if(style != null){
					insertItem.setStyle(style);
				}
			}else{
				//新插入的记录
				insertItem = commitDetail;
			}
			
			insertItem.setId(newid);
			insertItem.setTicketMainId(ticketId);
			
			/*//说明有修改
			if(value != null){
				if(EditorCmp.isPic(insertItem.getType())){
					//说明是图片修改
					UploadFile picFile = reqbody.getUploadFile(value);
					String picPath = CommonUtil.join("image/ticket/",ticketId,"/",newid,".",picFile.getExtendName());
					picPath = CCP.uploadFile(picFile, picPath,true);
					insertItem.setValue(picPath);
				}else if(EditorCmp.isText(insertItem.getType())){
					insertItem.setValue(commitDetail.getValue());
				}
			}*/
			
			insertList.add(insertItem);
			
		}
		//把老的都删除，再插入新的
		//DBHelper.getDao().delete(TicketDetail.class, new DBCondition(TicketDetail.JField.ticketId,ticketId));
		DBHelper.getDao().insertBatch(insertList);
		
		return newids;
	}
	
	public static void clearIntroList(long ticketId) throws Exception{
		DBHelper.getDao().delete(TicketMainIntro.class, new DBCondition(TicketMainIntro.JField.ticketMainId,ticketId));
	}
	
	/**
	 * 查询某张旅票被订购总数。支付了才算，包括退订申请中和推单申请中
	 * @param ticketId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午8:45:19
	 */
	public static long queryOrderCount(long ticketId) throws Exception{
		long orderCount = DBHelper.getDao().queryCount(Orders.class, 
			new DBCondition(Orders.JField.ticketId,ticketId),
			new DBCondition(Orders.JField.status,OrderStatusDefine.EDIT.getState(),DBOperator.GREAT)
		);
		return orderCount;
	}
	
	/**
	 * 查询某张旅票被收藏总数。
	 * @param ticketId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午8:45:19
	 */
	/*public static long queryFavCount(long ticketId) throws Exception{
		List<UserFavTicket> favTicketList = DBHelper.getDao().query(UserFavTicket.class, new DBCondition(UserFavTicket.JField.ticketId,ticketId));
		return NullUtil.isEmpty(favTicketList) ? 0 : favTicketList.size();
	}*/
	
	
	/**
	 * 对旅行票的相关信息进行转换,比如耗时转成小时，根据国家编码添加countryName等
	 * @param ticket
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月21日
	 */
	public static void transferTicketInfo(Ticket ticket) throws Exception{
		if(ticket == null)
			return;
		if(ticket.getPrice() != null){
			//这里存储的单位是分，需要转换成元
			ticket.addAttribute("price", ETUtil.parseFen2Yuan(ticket.getPrice()));
			ticket.setPrice(null);
		}
		if(ticket.getOrigPrice() != null){
			//这里存储的单位是分，需要转换成元
			ticket.addAttribute("origPrice", ETUtil.parseFen2Yuan(ticket.getOrigPrice()));
			ticket.setOrigPrice(null);
		}
		
		//如果times不为空，则需要截取成数组返回到前台
		if(NullUtil.isNotEmpty(ticket.getTimes())){
			ticket.addAttribute("times", ticket.getTimes().split(","));
		}
		ticket.setTimes(null);
		
		//如果dates不为空，则需要截取成数组返回到前台
		if(NullUtil.isNotEmpty(ticket.getDates())){
			ticket.addAttribute("dates", ticket.getDates().split(","));
			ticket.setDates(null);
		}
	}
	
	
	
	
	
	/**
	 * 转换旅票主体中的信息,,比如耗时转成小时，根据国家编码添加countryName等
	 * @param ticketset
	 * @param needTag
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:16:01
	 */
	public static void transferTicketMain(TicketMain ticketMain) throws Exception{
		if(ticketMain == null)
			return;
		
		//图片背景
		if(NullUtil.isNotEmpty(ticketMain.getPic())){
			ticketMain.setPic(ETUtil.buildPicUrl(ticketMain.getPic()));
		}
		
		if(ticketMain.getCostTime() != null){
			//这里存储的单位是分种，需要转换成小时
			ticketMain.addAttribute("costTime", ETUtil.parseMinute2Hour(ticketMain.getCostTime()));
			ticketMain.setCostTime(null);
		}
		
		//根据国家编码，添加国家名称
		if(NullUtil.isNotEmpty(ticketMain.getCountryCode())){
			BaseNode countryNode = CountryCmp.getCountryNode(ticketMain.getCountryCode());
			if(countryNode != null){
				ticketMain.addAttribute("countryName", countryNode.getAttribute("name"));
			}
		}
		//根据城市编码，添加城市名称
		if(NullUtil.isNotEmpty(ticketMain.getCityCode())){
			BaseNode cityNode = CountryCmp.getCityNode(ticketMain.getCountryCode(), ticketMain.getCityCode());
			if(cityNode != null){
				ticketMain.addAttribute("cityName", cityNode.getAttribute("name"));
			}
		}
	}
	
	
	/**
	 * 对旅行票的相关信息进行转换,比如耗时转成小时，根据国家编码添加countryName等
	 * @param ticket
	 * @param needAppendMain，转换的时候是否需要添加主票信息到当前子票实体上
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月21日
	 */
	public static void transferTicketInfo(Ticket ticket,boolean needAppendMain) throws Exception{
		TicketMain main = !needAppendMain ? null : TicketCmp.queryMain(ticket.getTicketMainId());
		if(main != null){
			TicketCmp.transferTicketMain(main);
		}	
		transferTicketInfo(ticket,main);
	}
	/**
	 * 对旅行票的相关信息进行转换,比如时间、价格等
	 * @param ticket
	 * @param ticketMain,如果不为空，则需要把主票资料添加到子票实体上。注意，该TicketMain实体必须已经调用了方法transferTicketMain();
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:45:45
	 */
	public static void transferTicketInfo(Ticket ticket,TicketMain ticketMain) throws Exception{
		if(ticket == null)
			return;
		if(ticket.getPrice() != null){
			//这里存储的单位是分，需要转换成元
			ticket.addAttribute("price", ETUtil.parseFen2Yuan(ticket.getPrice()));
			ticket.addAttribute("maxNumber", "1");
			ticket.setPrice(null);
		}
		if(ticket.getOrigPrice() != null){
			//这里存储的单位是分，需要转换成元
			ticket.addAttribute("origPrice", ETUtil.parseFen2Yuan(ticket.getOrigPrice()));
			ticket.setOrigPrice(null);
		}
		
		//如果times不为空，则需要截取成数组返回到前台
		if(NullUtil.isNotEmpty(ticket.getTimes())){
			ticket.addAttribute("times", ticket.getTimes().split(","));
			ticket.setTimes(null);
		}
		
		//如果dates不为空，则需要截取成数组返回到前台
		if(NullUtil.isNotEmpty(ticket.getDates())){
			ticket.addAttribute("dates", ticket.getDates().split(","));
			ticket.setDates(null);
		}
		
		//需要增加main信息
		if(ticketMain != null){
			ticket.addAttribute("name", ticketMain.getName());
			ticket.addAttribute("pic",ticketMain.getPic());
			ticket.addAttribute("costTime",ticketMain.getAttr("costTime"));
			ticket.addAttribute("tagTextList",ticketMain.getAttr("tagTextList"));
			ticket.addAttribute("placeName",ticketMain.getPlaceName());
		}
	}
	/**
	 * 一次性转换子旅票列表
	 * @param ticketList
	 * @param needAppendMain
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:53:17
	 */
	public static void transferTicketInfo(List<Ticket> ticketList,boolean needAppendMain) throws Exception{
		List<TicketMain> mainList = null;
		if(needAppendMain){
			Set<Long> mainIds = ETUtil.buildEntityIdList(ticketList, Ticket.JField.ticketMainId);
			mainList = DBHelper.getDao().query(new QueryStatement(TicketMain.class,
					new DBCondition(TicketMain.JField.id,mainIds,DBOperator.IN)));
			
		}
		TicketMain ticketMain = null;
		for(Ticket ticket : ticketList){
			if(NullUtil.isNotEmpty(mainList)){
				for(TicketMain main : mainList){
					if(main.getId().longValue() == ticket.getTicketMainId()){
						ticketMain = main;
						break;
					}
				}
			}
			if(ticketMain != null)
				TicketCmp.transferTicketMain(ticketMain);
			TicketCmp.transferTicketInfo(ticket, ticketMain);
			
		}
	}
	
	/**
	 * 获取票券类别名称
	 * @param catalogId
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月16日
	 */
	/*public static String getCatalogName(long catalogId) throws Exception{
		List<BaseTicketCatalog> catalogList = BaseDataCmp.queryTicketCatalogDefine();
		
		if(NullUtil.isEmpty(catalogList))
			return null;
		
		for(BaseTicketCatalog catalog : catalogList){
			if(catalog.getId() == catalogId){
				return catalog.getName();
			}
		}
		return null;
	}*/
	
	/**
	 *  更改了旅票信息后，也要同步更新旅票索引表里的信息
	 * @param main，主体信息，完整数据实体，且执行过transferTicketMain
	 * @param guider，如果新增索引的场景下，设置第一个导游名称
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:23:01
	 */
	public static void updateTicketIndex(TicketMain main,User guider) throws Exception{
		long ticketMainId = main.getId();
		TicketIndex index = DBHelper.getDao().queryById(TicketIndex.class,ticketMainId);
		boolean isNew = index == null;
		if(isNew){
			index = new TicketIndex();
			index.setName(main.getName());
		}else if(DBHelper.isModified(main, TicketMain.JField.name)){
			index.setName(main.getName());
		}
		
		if(isNew || DBHelper.isModified(main, TicketMain.JField.countryCode)
				|| DBHelper.isModified(main, TicketMain.JField.cityCode)
				|| DBHelper.isModified(main, TicketMain.JField.placeName)){
			String countryName = (String)main.getAttr("countryName");
			String cityName = (String)main.getAttr("cityName");
			StringBuffer sb_place = new StringBuffer();
			if(NullUtil.isNotEmpty(countryName))
				sb_place.append(" ").append(countryName);
			if(NullUtil.isNotEmpty(cityName))
				sb_place.append(" ").append(cityName);
			if(NullUtil.isNotEmpty(main.getPlaceName()))
				sb_place.append(" ").append(main.getPlaceName());
			
			if(sb_place.length() > 0)
				index.setPlace(sb_place.substring(1));
		}
		
		if(DBHelper.isModified(main, TicketMain.JField.tags)){
			List<String> tagTextList = (List<String>)main.getAttr("tagTextList");
			index.setTags(NullUtil.isEmpty(tagTextList)? null :
				CommonUtil.joinBySeperator(' ', tagTextList));
		}
		if(guider != null){
			index.setGuiderNames(JsonUtil.obj2Json(new String[]{String.valueOf(guider.getId()),guider.getNickname()}));
		}
		if(!DBHelper.isModified(index)){
			return;//ticket_index没有设置过值
		}
		
		if(index.getId() == null){
			index.setId(ticketMainId);
			DBHelper.getDao().insert(index);
		}else{
			DBHelper.getDao().updateById(index, ticketMainId);
		}
	}
	
	/**
	 * 重新获取一遍主票下的认领导游资料，并把所有导游的名称组织好，重新设置到索引记录中。
	 * 这个一般发生在导游下线的场景才会触发，下线后必须把当前导游在索引表中除名
	 * @param ticketMainId
	 * @param guider
	 * @param operType,操作类型，add/update/delete
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午10:34:20
	 */
	public static void updateTicketIndexGuiders(long ticketMainId,User guider,String operType) throws Exception{
		TicketIndex index = DBHelper.getDao().queryById(TicketIndex.class, ticketMainId);
		String guiderNames = index.getGuiderNames();
		List<String> names = NullUtil.isEmpty(guiderNames) ? null : JsonUtil.json2List(guiderNames, String.class);
		if(operType.equals("add")){
			if(names == null){
				names = new ArrayList<String>();
			}
			//用户id和用户昵称同时放入,id在前
			names.add(String.valueOf(guider.getId()));
			names.add(guider.getNickname());
		}else if(operType.equals("update")){
			if(NullUtil.isNotEmpty(names)){
				for(int i=0;i<names.size();i+=2){
					if(guider.getId().longValue() == Long.valueOf(names.get(i))){
						names.set(i+1,guider.getNickname());//找到对应id后，把后一个元素的昵称更新掉
						break;
					}
				}
			}
		}else if(operType.equals("delete")){
			if(NullUtil.isNotEmpty(names)){
				for(int i=0;i<names.size();i+=2){
					if(guider.getId().longValue() == Long.valueOf(names.get(i))){
						names.remove(i+1);//先移除后一个昵称
						names.remove(i);//再移除当前
						break;
					}
				}
			}
		}
		
		index.setGuiderNames(JsonUtil.obj2Json(names));//需要清空
		DBHelper.getDao().updateById(index,ticketMainId);
		
	}
	
	
	
	/**
	 * 更新索引表。主票中的name，city_code,country_code,place_name,tags更改要更新索引表
	 * @param main，必须存在id，且调用过了transferTicketMain方法
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:38:11
	 *//*
	public static void updateTicketIndex(TicketMain main) throws Exception{
		TicketIndex index = new TicketIndex();
		if(main._getSettedValue().containsKey(TicketMain.JField.name)){
			index.setName(main.getName());
		}
		if(main._getSettedValue().containsKey(TicketMain.JField.countryCode)
			|| main._getSettedValue().containsKey(TicketMain.JField.cityCode)
			|| main._getSettedValue().containsKey(TicketMain.JField.placeName)){
			//上述三个资料一个更改了就要全部更改
			String countryName = (String)main.getAttr("countryName");
			String cityName = (String)main.getAttr("cityName");
			
			StringBuffer sb_place = new StringBuffer();
			if(NullUtil.isNotEmpty(countryName))
				sb_place.append(" ").append(countryName);
			if(NullUtil.isNotEmpty(cityName))
				sb_place.append(" ").append(cityName);
			if(NullUtil.isNotEmpty(main.getPlaceName()))
				sb_place.append(" ").append(main.getPlaceName());
			
			index.setPlace(sb_place.substring(1));
		}
		if(main._getSettedValue().containsKey(TicketMain.JField.tags)){
			List<String> tagTextList = (List<String>)main.getAttr("tagTextList");
			if(NullUtil.isNotEmpty(tagTextList)){
				index.setTags(CommonUtil.joinBySeperator(' ', tagTextList));
			}else{
				index.setTags(null);
			}
		}
		
		if(DBHelper.isModified(index)){
			DBHelper.getDao().updateById(index, main.getId());
		}
	}*/
	
	/**
	 * 根据旅票中的标签ID串，构建成文本
	 * @param tagStr
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午10:25:13
	 */
	public static List<String> buildTagTextList(String tagStr) throws NumberFormatException, Exception{
		if(NullUtil.isEmpty(tagStr))
			return null;
		String[] tagStrArr = tagStr.split(",");
		List<String> tagTextList = new ArrayList<String>();
		for(String tagId : tagStrArr){
			BaseTag tagDefine = BaseDataCmp.queryTagDefine(Long.valueOf(tagId));
			if(tagDefine == null)
				continue;
			tagTextList.add(tagDefine.getName());
		}
		return tagTextList;
	}
	
	/**
	 * 更新整个旅票主体的最低价。
	 * 从所有认领票中找出最低价更新至ticket_main.min_price
	 * sql:update ticket_main set min_price = (select min(price) from ticket where ticket_main_id = mainId and status=99)
	 * 以下场景会涉及到数字变动：
	 * 1、新导游认领了旅票
	 * 2、导游下架了旅票
	 * 3、导游上架了旅票（可能下架后更新了单价）
	 * @param mainId
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:23:36
	 */
	public static void updateMinPrice(long mainId) throws Exception{
		QueryStatement minPriceQS = new QueryStatement(Ticket.class,
				new DBCondition(Ticket.JField.ticketMainId,mainId),
				new DBCondition(Ticket.JField.status,BaseConstantDefine.TICKET_STATUS_PUBLISH)
		).appendQueryField(new MinQueryField(Ticket.JField.price,"minPrice"));

		new UpdateStatement(new FieldUpdateExpression[]{
			new FieldUpdateExpression(TicketMain.JField.minPrice,minPriceQS)
			}, new DBCondition(TicketMain.JField.id,mainId)).execute();
		
	}
	/**
	 * 更新旅票的已认领的导游数。以下场景会涉及到数字的变动
	 * 1：导游新认领，+1
	 * 2、导游下线，-1
	 * 3、导游重新上线，+1
	 * @param mainId
	 * @param count
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午2:40:29
	 */
	public static void updateParticipateCount(long mainId,int count) throws Exception{
		CCP.updateFieldNum(TicketMain.JField.participateCount, count, new DBCondition(TicketMain.JField.id,mainId));
	}
}
