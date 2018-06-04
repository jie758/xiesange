package com.xiesange.web.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.exception.XSGException;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.activity.ActivityQingyuan;
import com.xiesange.gen.dbentity.shanghui.Shanghui;
import com.xiesange.gen.dbentity.shanghui.ShanghuiRound;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.field.summary.CountQueryField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.define.ParamDefine;
@ETServiceAnno(name="shanghui",version="")
public class ShanghuiService extends AbstractService {
	private static Map<String,String> Member_Mobiles = ClassUtil.newMap();
	private static List<AwardDefine> awardDefineList = ClassUtil.newList();
	static{
		Member_Mobiles.put("wuyj", "13588830404");
		Member_Mobiles.put("al", "18968083721");
		Member_Mobiles.put("zy", "13588106410");
		Member_Mobiles.put("wuxs", "13989896095");
		Member_Mobiles.put("zengxr", "18858100860");
		Member_Mobiles.put("lixp", "18867101173");
		Member_Mobiles.put("zhouyl", "18867101875");
		Member_Mobiles.put("lixf", "13484004200");
		Member_Mobiles.put("fan", "15168466488");
		Member_Mobiles.put("xuedl", "17706773050");
		
		//设置奖项
		awardDefineList.add(new AwardDefine("勤劳奖",3,new int[]{500,300,200},"授予积极参加活动的会员","战斗次数不少于4次，取次数最高的前3名。如并列，胜率高优先。",
				"count >= 4","count victoryCount",false));
		awardDefineList.add(new AwardDefine("5连胜奖",null,400,"授予当前赛季技术高超的连胜会员","连续5次取得胜绩，当场发放奖金",
				"count = -1 && negativeCount = -1","count",true));
		awardDefineList.add(new AwardDefine("牺牲奖",null,300,"授予为商会做出杰出贡献的会员","战斗次数不少于4次，累计战绩在-3000（含）元以上",
				"count >= 4 && sum <= -3000","-sum"));
		awardDefineList.add(new AwardDefine("发财奖",null,200,"授予当前赛季战绩最高的会员","战斗次数不少于4次，累计战绩在3000（含）元以上",
				"count >= 4 && sum >= 3000","sum"));
		
		/*awardDefineList.add(new AwardDefine("全胜奖",null,500,"授予当前赛季技术高超的全胜会员","战斗次数不少于6次，且无败绩",
				"count >= 6 && negativeCount = 0","count"));*/
		awardDefineList.add(new AwardDefine("阳光奖",null,100,"授予未得奖失落的会员","战斗次数不少于3次(如当月基金不够，则按次数、胜率高者优先分配)",
				"count >= 0","count"));
		
		
		
	}
	/**
	 * 创建
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody create(WebRequestContext context) throws Exception{
		String membersStr = context.getRequestBody().getString(ParamDefine.Shanghui.members);
		Integer price = context.getRequestBody().getInt(ParamDefine.Shanghui.price);
		String type = context.getRequestBody().getString(ParamDefine.Shanghui.mj_type);
		Short isOnline = context.getRequestBody().getShort(ParamDefine.Shanghui.is_online);
		
		
		
		
		Shanghui sh = new Shanghui();
		sh.setPrice(price);
		sh.setType(type);
		sh.setMembers(membersStr);
		sh.setDate(DateUtil.now());
		sh.setIsOnline(isOnline);
		
		
		if(isOnline == 1){
			String scoresStr = context.getRequestBody().getString(ParamDefine.Shanghui.scores);
			String[] scores = scoresStr.split(",");
			String[] members = membersStr.split(",");
			StringBuffer membScores = new StringBuffer();
			for(int i=0;i<scores.length;i++){
				membScores.append(",").append(members[i]).append(":").append(scores[i]);
			}
			sh.setScore(membScores.substring(1));
			sh.setShui(0);
			sh.setSum(0);
			sh.setFinishTime(context.getRequestDate());
			sh.setStatus((short)99);
			dao().insert(sh);
			
			ShanghuiRound round = new ShanghuiRound();
			round.setShanghuiId(sh.getId());
			round.setScore(sh.getScore()+",shui:0");
			dao().insert(round);
		}else{
			dao().insert(sh);
		}
		
		return new ResponseBody("newid",sh.getId());
		
	}
	
	/*public ResponseBody importData(WebRequestContext context) throws Exception{
		List<Map> items = context.getRequestBody().getList(ParamDefine.Shanghui.items,Map.class);
		
		List<ShanghuiS> list = ClassUtil.newList();
		for(Map item : items){
			String date = (String)item.get("date");
			Integer price = (Integer)item.get("price");
			JSONObject members = (JSONObject)item.get("members");
			Iterator<String> it = members.keySet().iterator();
			StringBuffer sb = new StringBuffer();
			StringBuffer sb_member = new StringBuffer();
			while(it.hasNext()){
				String key = it.next();
				String val = members.getString(key);
				sb.append(",").append(key+":"+val);
				
				if(!key.equals("shui")){
					sb_member.append(",").append(key);
				}
			}
			System.out.println(sb.substring(1));
			
			Shanghui sh = new Shanghui();
			sh.setDate(DateUtil.str2Date(date, DateUtil.DATE_FORMAT_EN_B_YYYYMMDD));
			sh.setMembers(sb_member.substring(1));
			sh.setPrice(price==null?5:price);
			sh.setSum(0L);
			dao().insert(sh);
			
			ShanghuiRecord record = new ShanghuiRecord();
			record.setScore(sb.substring(1));
			record.setShanghuiId(sh.getId());
			list.add(record);
		}
		
		dao().insertBatch(list);
		
		return null;
		
	}*/
	
	public ResponseBody finish(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		long sid = reqbody.getLong(ParamDefine.Shanghui.shanghui_id);
		Integer sum = reqbody.getInt(ParamDefine.Shanghui.sum,0);
		
		List<ShanghuiRound> roundList = dao().query(ShanghuiRound.class,new DBCondition(ShanghuiRound.JField.shanghuiId,sid));
		Map<String,Integer> scoreMap = ClassUtil.newMap();
		for(ShanghuiRound round : roundList){
			String[] scores = round.getScore().split(",");
			for(String item : scores){
				String[] items = item.split(":");
				Integer score = Integer.parseInt(items[1]);
				if(scoreMap.get(items[0]) == null){
					scoreMap.put(items[0],score);
				}else{
					scoreMap.put(items[0],scoreMap.get(items[0])+score);
				}
			}
		}
		
		
		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String,Integer>> it = scoreMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,Integer> scoreentry = it.next();
			String mem = scoreentry.getKey();
			if(mem.equals("shui")){
				continue;
			}
			int score = (Integer)scoreentry.getValue();
			sb.append(",").append(mem).append(":").append(score);
		}
		
		Shanghui update = new Shanghui();
		update.setScore(sb.substring(1));
		update.setShui(scoreMap.get("shui"));
		update.setFinishTime(DateUtil.now());
		update.setSum(sum);
		update.setStatus((short)99);
		
		dao().updateById(update, sid);
		return null;
		
	}
	
	public ResponseBody commitQingyuan(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		String code = reqbody.getString(ParamDefine.Qingyuan.code);
		String building = reqbody.getString(ParamDefine.Qingyuan.building);
		String room = reqbody.getString(ParamDefine.Qingyuan.room);
		String unit = reqbody.getString(ParamDefine.Qingyuan.unit);
		String sign = reqbody.getString(ParamDefine.Qingyuan.signature);
		
		ActivityQingyuan qy = dao().querySingle(ActivityQingyuan.class, 
				new DBCondition(ActivityQingyuan.JField.building,building),
				new DBCondition(ActivityQingyuan.JField.unit,unit),
				new DBCondition(ActivityQingyuan.JField.room,room));
		
		if(qy != null){
			dao().deleteById(ActivityQingyuan.class,qy.getId());
		}
		qy = new ActivityQingyuan();
		qy.setCode(code);
		qy.setBuilding(building);
		qy.setUnit(unit);
		qy.setSignup(sign);
		qy.setRoom(room);
		dao().insert(qy);
		
		return null;
		
	}
	
	public ResponseBody queryQingyuanList(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		String code = reqbody.getString(ParamDefine.Qingyuan.code);
		
		List<ActivityQingyuan> list = dao().query(new QueryStatement(ActivityQingyuan.class,new DBCondition(ActivityQingyuan.JField.code,code))
					.appendQueryField(ActivityQingyuan.JField.building,ActivityQingyuan.JField.unit,ActivityQingyuan.JField.room)
					.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
					.appendOrderFieldDesc(ActivityQingyuan.JField.createTime));
		
		List<ActivityQingyuan> statlist = dao().query(new QueryStatement(ActivityQingyuan.class,new DBCondition(ActivityQingyuan.JField.code,code))
				.appendQueryField(ActivityQingyuan.JField.building,CountQueryField.getInstance("totalcount"))
				.appendGroupField(ActivityQingyuan.JField.building)
		);

		
		return new ResponseBody("list",list).add("statList", statlist);
		
	}
	
	/**
	 * 记录一局
	 * @param context
	 * 			shanghui_id,
	 * 			grade
	 * @return
	 * @throws Exception
	 */
	public ResponseBody commitRound(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		long sid = reqbody.getLong(ParamDefine.Shanghui.shanghui_id);
		String grade = reqbody.getString(ParamDefine.Shanghui.grade);
		
		ShanghuiRound round = new ShanghuiRound();
		round.setShanghuiId(sid);
		round.setScore(grade);
		dao().insert(round);
		
		/*List<ShanghuiRecord> recordList = dao().query(ShanghuiRecord.class,new DBCondition(ShanghuiRecord.JField.shanghuiId,sid));
		Map<String,Integer> scoreMap = ClassUtil.newMap();
		int shui = 0;
		for(ShanghuiRecord record : recordList){
			String[] mems = record.getScore().split(",");
			for(String mem : mems){
				String[] scores = mem.split(":");
				String name = scores[0];
				int score = Integer.valueOf(scores[1]);
				if(name.equals("shui")){
					shui += score;
					continue;
				}
				
				if(scoreMap.get(name) == null){
					scoreMap.put(name, 0);
				}
				scoreMap.put(name, scoreMap.get(name)+score);
				
			}
		}
		
		Iterator<Entry<String,Integer>> it = scoreMap.entrySet().iterator();
		StringBuffer notifyContent = new StringBuffer("【四龙会】");
		List<String> mobileList = ClassUtil.newList();
		while(it.hasNext()){
			Entry<String,Integer> entry = it.next();
			System.out.println(entry.getKey()+"="+entry.getValue());
			notifyContent.append(entry.getKey()).append(":").append(entry.getValue()).append(" , ");
			if(Member_Mobiles.get(entry.getKey()) != null){
				mobileList.add(Member_Mobiles.get(entry.getKey()));
			}
		}
		notifyContent.append("台:").append(shui);
		notifyContent.append(" 。更多详情，请登录官方网站查看。 ");*/
		
		//TaskManager.execute(new SmsNotifyTaskBean(mobileList,notifyContent.toString()));
		
		return null;
	}
	
	/**
	 * 查询某次战斗的所有局数
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryRoundList(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		long sid = reqbody.getLong(ParamDefine.Shanghui.shanghui_id);
		
		Shanghui sh = dao().queryById(Shanghui.class, sid);
		List<ShanghuiRound> roundList = dao().query(ShanghuiRound.class, new DBCondition(ShanghuiRound.JField.shanghuiId,sid));
		
		//Map<String,Map<String,Integer>> roundMap = new LinkedMap();
		
		if(NullUtil.isNotEmpty(roundList)){
			for(int i=0;i<roundList.size();i++){
				ShanghuiRound round = roundList.get(i);
				long diff = 0;
				//计算两个回合之间的耗时
				if(i == 0){
					diff = round.getCreateTime().getTime() - sh.getCreateTime().getTime();
				}else{
					diff = round.getCreateTime().getTime() - roundList.get(i-1).getCreateTime().getTime();
				}
				round.addAttribute("cost", diff/1000/60);
				
				String[] grades = round.getScore().split(",");
				Map<String,Integer> map = ClassUtil.newMap();
				for(String str : grades){
					String[] items = str.split(":");
					map.put(items[0], Integer.valueOf(items[1]));
				}
				round.addAttribute("score", map);
				round.setScore(null);
				ETUtil.clearDBEntityExtraAttr(round,ShanghuiRound.JField.createTime);
			}
		}
		return new ResponseBody("roundList",roundList)
						.add("shanghui", sh);
	}
	
	
	public ResponseBody queryList(WebRequestContext context) throws Exception{
		List<Shanghui> fightingList = dao().query(new QueryStatement(Shanghui.class)
				.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
				.appendOrderFieldDesc(Shanghui.JField.createTime));
		
		return new ResponseBody("fightingList",fightingList);
	}
	
	public ResponseBody removeRound(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		long roundId = reqbody.getLong(ParamDefine.Shanghui.round_id);
		
		dao().deleteById(ShanghuiRound.class, roundId);
		
		return null;
	}
	
	public ResponseBody remove(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		long sid = reqbody.getLong(ParamDefine.Shanghui.shanghui_id);
		
		dao().deleteById(Shanghui.class, sid);
		dao().delete(ShanghuiRound.class, new DBCondition(ShanghuiRound.JField.shanghuiId,sid));
		
		return null;
	}
	
	/**
	 * 查询某个赛季获奖情况
	 * @param context
	 * 			start_date,
	 * 			end_date
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryAward(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		Date startDate = reqbody.getDate(ParamDefine.Shanghui.start_date);
		Date endDate = reqbody.getDate(ParamDefine.Shanghui.end_date);
		Short isOnline = context.getRequestBody().getShort(ParamDefine.Shanghui.is_online);
		if(isOnline == null){
			isOnline = 0;
		}
		
		List<DBCondition> condList = ClassUtil.newList();
		condList.add(new DBCondition(Shanghui.JField.date,startDate,DBOperator.GREAT_EQUALS));
		
		if(endDate != null){
			condList.add(new DBCondition(Shanghui.JField.date,endDate,DBOperator.LESS_EQUALS));
		}
		condList.add(new DBCondition(Shanghui.JField.status,(short)99));
		
		Map<String,Fighting> memScores = queryStat(startDate,endDate,true,isOnline);
		
		int shui = 0;
		int huifei = 0;
		
		List<Fighting> ftList = null;
		if(NullUtil.isNotEmpty(memScores)){
			shui = memScores.get("shui").getSum();
			huifei = memScores.get("huifei").getSum();
			memScores.remove("shui");
			memScores.remove("huifei");
			
			Iterator<Fighting> it = memScores.values().iterator();
			ftList = ClassUtil.newList();
			while(it.hasNext()){
				Fighting ft = it.next();
				ftList.add(ft);
			}
		}
		List<Award> rewardList = ClassUtil.newList();

		Set<String> membSet = ClassUtil.newSet();
		for(AwardDefine awardDefine : awardDefineList){
			if(isOnline != awardDefine.isOnline){
				continue;
			}
			boolean isYGJ = awardDefine.getName().equals("阳光奖");
			Award award = new Award(awardDefine);
			rewardList.add(award);
			List<Fighting> matchList = matchAward(ftList,awardDefine);
			if(NullUtil.isEmpty(matchList)){
				continue;
			}
			int[] sums = awardDefine.getSums();
			if(isYGJ){
				int count = 3;
				for(int i=0;i<matchList.size();i++){
					Fighting ft = matchList.get(i);
					if(!membSet.contains(ft.getMember())){
						award.addMember(ft.getMember(), ft.getCount()>=count ? sums[0] : 0);
					}
				}
			}else{
				for(int i=0;i<matchList.size();i++){
					membSet.add(matchList.get(i).getMember());
					award.addMember(matchList.get(i).getMember(), i<sums.length ? sums[i] : sums[sums.length-1]);
				}
			}
			
		}
		return new ResponseBody("awardList",rewardList)
					.add("fightingList", ftList)
					.add("shui", shui)
					.add("huifei", huifei);
	}
	
	/*public ResponseBody queryFightingStat(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		Date startDate = reqbody.getDate(ParamDefine.Shanghui.start_date);
		Date endDate = reqbody.getDate(ParamDefine.Shanghui.end_date);
		
		
		List<DBCondition> condList = ClassUtil.newList();
		condList.add(new DBCondition(Shanghui.JField.date,startDate,DBOperator.GREAT_EQUALS));
		
		if(endDate != null){
			condList.add(new DBCondition(Shanghui.JField.date,endDate,DBOperator.LESS_EQUALS));
		}
		
		Map<String,Fighting> memScores = queryStat(startDate,endDate,true);
		
		int shui = 0;
		int huifei = 0;
		if(NullUtil.isNotEmpty(memScores)){
			shui = memScores.get("shui").getSum();
			huifei = memScores.get("huifei").getSum();
			memScores.remove("shui");
			memScores.remove("huifei");
		}
		
		return new ResponseBody("memberScores",memScores)
					.add("shui", shui)
					.add("huifei", huifei);
	}*/
	
	
	public ResponseBody queryFightingList(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody(); 
		Date startDate = reqbody.getDate(ParamDefine.Shanghui.start_date);
		Date endDate = reqbody.getDate(ParamDefine.Shanghui.end_date);
		String member = reqbody.getString(ParamDefine.Shanghui.member);
		Short isOnline = reqbody.getShort(ParamDefine.Shanghui.is_online);
		Short isOrderByDateDesc = reqbody.getShort(ParamDefine.Shanghui.is_order_by_date_desc);
		if(isOnline == null){
			isOnline = 0;
		}
		if(isOrderByDateDesc == null){
			isOrderByDateDesc = 1;
		}
		//String groupBy = reqbody.getString(ParamDefine.Shanghui.group_by);
		List<DBCondition> condList = ClassUtil.newList();
		condList.add(new DBCondition(Shanghui.JField.date,startDate,DBOperator.GREAT_EQUALS));
		if(endDate != null){
			condList.add(new DBCondition(Shanghui.JField.date,endDate,DBOperator.LESS_EQUALS));
		}
		condList.add(new DBCondition(Shanghui.JField.isOnline,isOnline));
		condList.add(new DBCondition(Shanghui.JField.status,(short)99));
		
		QueryStatement qs = new QueryStatement(Shanghui.class, 
				condList.toArray(new DBCondition[condList.size()]));
		if(isOrderByDateDesc == 1){
			qs.appendOrderFieldDesc(Shanghui.JField.date,Shanghui.JField.createTime);
		}else{
			qs.appendOrderField(Shanghui.JField.date,Shanghui.JField.createTime);
		}
		/*if(groupBy.equals("daily")){
			qs.appendGroupField(fields)
		}*/

		
		List<Shanghui> shanghuiList = dao().query(qs);
		
		if(NullUtil.isEmpty(shanghuiList)){
			return null;
		}
		
		for(Shanghui fighting : shanghuiList){
			if(NullUtil.isEmpty(fighting.getScore())){
				continue;
			}
			String[] members = fighting.getScore().split(",");
			Map<String,Integer> scores = ClassUtil.newMap();
			for(String mem : members){
				String[] items = mem.split(":");
				if(NullUtil.isNotEmpty(member) && !items[0].equalsIgnoreCase(member)){
					continue;
				}
				
				scores.put(items[0], Integer.valueOf(items[1])*fighting.getPrice());
			}
			if(NullUtil.isEmpty(member)){
				scores.put("shui", fighting.getShui());
			}
			fighting.addAttribute("members", scores);
			fighting.setScore(null);
		}
		
		return new ResponseBody("fightingList",shanghuiList);
	}
	
	
	
	private static Map<String,Fighting> queryStat(Date startDate,Date endDate,boolean needShui,short isOnline) throws Exception{
		List<DBCondition> condList = ClassUtil.newList();
		condList.add(new DBCondition(Shanghui.JField.date,startDate,DBOperator.GREAT_EQUALS));
		
		if(endDate != null){
			condList.add(new DBCondition(Shanghui.JField.date,endDate,DBOperator.LESS_EQUALS));
		}
		
		condList.add(new DBCondition(Shanghui.JField.isOnline,isOnline));
		
		List<Shanghui> shanghuiList = DBHelper.getDao().query(new QueryStatement(Shanghui.class, 
				condList.toArray(new DBCondition[condList.size()]))
					.appendOrderField(Shanghui.JField.date)
		);
		
		if(NullUtil.isEmpty(shanghuiList)){
			return null;
		}
		Map<String,Fighting> memScores = ClassUtil.newMap();
		Integer shui = 0;
		Integer huifei = 0;
		for(Shanghui fighting : shanghuiList){
			if(NullUtil.isEmpty(fighting.getScore())){
				continue;
			}
			Integer price = fighting.getPrice();
			String[] members = fighting.getScore().split(",");
			for(String memStr : members){
				String[] items = memStr.split(":");
				String mem = items[0];
				Integer sum = Integer.valueOf(items[1])*price;
				Fighting fting = memScores.get(mem); 
				if(fting == null){
					fting = new Fighting();
					fting.setCount(0);
					fting.setMember(mem);
					fting.setNegativeCount(0);
					fting.setVictoryCount(0);
					fting.setSum(0);
					memScores.put(mem, fting);
				}
				fting.setCount(fting.getCount()+1);
				fting.setSum(fting.getSum()+sum);
				if(sum < 0){
					fting.setNegativeCount(fting.getNegativeCount()+1);
				}else{
					fting.setVictoryCount(fting.getVictoryCount()+1);
				}
			}
			if(fighting.getShui() != null){
				shui += fighting.getShui() * price;
			}
			if(fighting.getSum() != null){
				huifei += fighting.getSum();
			}
		}
		if(needShui){
			memScores.put("shui", new Fighting("shui",shui));
			memScores.put("huifei", new Fighting("huifei",huifei));
		}
		return memScores;
	}
	
	
	private static boolean matchExpression(int val1,int val2,String oper){
		if(oper.equals("<")){
			return val1 < val2;
		}else if(oper.equals("<=")){
			return val1 <= val2;
		}else if(oper.equals(">")){
			return val1 > val2;
		}else if(oper.equals(">=")){
			return val1 >= val2;
		}else if(oper.equals("=")){
			return val1 == val2;
		}
		return false;
	}
	
	private static String parseMemberFighting(Fighting ft){
		StringBuffer sb = new StringBuffer();
		sb.append("出战:").append(ft.getCount()).append("次;")
			.append(ft.getVictoryCount()).append("胜,")
			.append(ft.getNegativeCount()).append("负;");
		sb.append("战绩:").append(ft.getSum()).append("元");
		return sb.toString();
	}
	
	public static List<Fighting> matchAward(List<Fighting> dataList,final AwardDefine awardDefine){
		if(NullUtil.isEmpty(dataList)){
			return null;
		}
		List<Fighting> ftingList = ClassUtil.newList();
		for(Fighting ft : dataList){
			String[] exps = awardDefine.getExpression().split(" && ");
			
			boolean isMatch = true;
			for(String exp : exps){
				String[] conds = exp.split(" ");
				int condValue = Integer.valueOf(conds[2]);
				
				if(conds[0].equals("sum")){
					int sum = ft.getSum();
					if(!matchExpression(sum,condValue,conds[1])){
						isMatch = false;
						break;
					};
					
				}else if(conds[0].equals("negativeCount")){
					int negCount = ft.getNegativeCount();
					if(!matchExpression(negCount,condValue,conds[1])){
						isMatch = false;
						break;
					};
					
				}else if(conds[0].equals("count")){
					int count = ft.getCount();
					if(!matchExpression(count,condValue,conds[1])){
						isMatch = false;
						break;
					};
				}
			}
			if(!isMatch){
				continue;
			}
			ftingList.add(ft);
		}
		
		if(NullUtil.isEmpty(ftingList)){
			return null;
		}
		
		if(NullUtil.isNotEmpty(awardDefine.getOrderBy())){
			Collections.sort(ftingList, new Comparator<Fighting>(){
				@Override
				public int compare(Fighting o1, Fighting o2) {
					//-开头表示从低到高
					String[] orderbys = awardDefine.getOrderBy().split(" ");
					int result = 0;
					for(String order : orderbys){
						boolean isAsc = order.startsWith("-");
						String orderBy = isAsc ? order.substring(1):order;
						
						if(orderBy.equals("count")){
							result = o1.getCount() - o2.getCount();
						}else if(orderBy.equals("sum")){
							result = o1.getSum() - o2.getSum();
						}else if(orderBy.equals("victoryCount")){
							result = o1.getVictoryCount() - o2.getVictoryCount();
						}else if(orderBy.equals("negativeCount")){
							result = o1.getNegativeCount() - o2.getNegativeCount();
						}
						result = isAsc ? result : -1*result;
						if(result != 0){
							break;
						}
					}
					return result;
				}
			});
		}
		
		Integer quota = awardDefine.getQuota();
		if(quota != null && NullUtil.isNotEmpty(ftingList)){
			ftingList = ftingList.subList(0, quota<ftingList.size()?quota:ftingList.size());
		}
		
		return ftingList;
	}
	
	
	private static class Fighting{
		private String member;
		private Integer sum;
		private Integer count;
		private Integer victoryCount;
		private Integer negativeCount;
		
		
		public Fighting(){}
		
		public Fighting(String mem,Integer sum){
			this.member = mem;
			this.sum = sum;
		}
		
		public String getMember() {
			return member;
		}
		public void setMember(String member) {
			this.member = member;
		}
		public Integer getSum() {
			return sum;
		}
		public void setSum(Integer sum) {
			this.sum = sum;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
		public Integer getVictoryCount() {
			return victoryCount;
		}
		public void setVictoryCount(Integer victoryCount) {
			this.victoryCount = victoryCount;
		}
		public Integer getNegativeCount() {
			return negativeCount;
		}
		public void setNegativeCount(Integer negativeCount) {
			this.negativeCount = negativeCount;
		}
	}
	
	
	private static class AwardDefine{
		private String name;
		private Integer quota;
		private int[] sums;
		private String desc;
		private String condition;
		private String expression;
		private String orderBy;
		private short isOnline;
		
		public AwardDefine(String name,Integer quota,int sum,String desc,String condition,String expression,String orderBy){
			this(name,quota,new int[]{sum},desc,condition,expression,orderBy,false);
		}
		public AwardDefine(String name,Integer quota,int sum,String desc,String condition,String expression,String orderBy,boolean isOnline){
			this(name,quota,new int[]{sum},desc,condition,expression,orderBy,isOnline);
		}
		
		public AwardDefine(String name,Integer quota,int[] sums,String desc,String condition,String expression,String orderBy,boolean isOnline){
			this.name = name;
			this.quota = quota;
			this.sums = sums;
			this.desc = desc;
			this.condition = condition;
			this.expression = expression;
			this.orderBy = orderBy;
			this.isOnline = isOnline?(short)1:(short)0;
		}

		public String getName() {
			return name;
		}

		public String getDesc() {
			return desc;
		}

		public String getCondition() {
			return condition;
		}

		public Integer getQuota() {
			return quota;
		}

		public int[] getSums() {
			return sums;
		}

		public String getExpression() {
			return expression;
		}

		public String getOrderBy() {
			return orderBy;
		}
	}
	
	
	private static class Award{
		private String name;
		private Integer quota;
		private int[] sums;
		private String desc;
		private String condition;
		private String orderBy;
		private List<AwardMember> members;
		
		public Award(AwardDefine awardDefine){
			this.name = awardDefine.getName();
			this.quota = awardDefine.getQuota();
			this.sums = awardDefine.getSums();
			this.desc = awardDefine.getDesc();
			this.condition = awardDefine.getCondition();
			this.orderBy = awardDefine.getOrderBy().startsWith("-")?
							awardDefine.getOrderBy().substring(1)
							:awardDefine.getOrderBy();
		}
		public void addMember(String member,Integer sum){
			if(members == null){
				members = ClassUtil.newList();
			}
			members.add(new AwardMember(member,sum));
		}
		public String getName() {
			return name;
		}
		public Integer getQuota() {
			return quota;
		}
		public int[] getSums() {
			return sums;
		}
		public String getDesc() {
			return desc;
		}
		public String getCondition() {
			return condition;
		}
		public List<AwardMember> getMembers() {
			return members;
		}
		public String getOrderBy() {
			return orderBy;
		}
		
		
	}
	
	private static class AwardMember{
		private String member;
		private Integer sum;
		
		public AwardMember(String member,Integer sum){
			this.member = member;
			this.sum = sum;
		}

		public String getMember() {
			return member;
		}

		public Integer getSum() {
			return sum;
		}

	}
	
	
}
