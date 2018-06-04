package com.xiesange.web.test;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.client.methods.HttpGet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiesange.baseweb.component.SysparamCmp;
import com.xiesange.baseweb.define.SysparamDefine;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.HttpUtil;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.shanghui.Shanghui;
import com.xiesange.gen.dbentity.shanghui.ShanghuiRound;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.test.frame.TestCmp;

public class ShanghuiTest {
	public static Map members = null;
	public static void main(String[] args) throws Exception {
		TestCmp.initLocalhost();
		String key = "aa9790a890b9428aae3a4f68d94f43c7";
		String sessionId = "26B1CCBC92AA06247397223FF58B5286";
		String roomKey = "6df0e56efcb24c00a1a9a8a1c8120567";
		//key = "01f0345d1a004ce888093badf20c9c4f";
		String s = SysparamCmp.get(SysparamDefine.SHANGHUI_MEMBER);
		//System.out.println(s);
		members = JsonUtil.json2Map(s);
		updateOnlineNaliqu(key,sessionId,roomKey);
	}
	
	public static void updateOnlineNaliqu(String key,String sessionId,String roomKey) throws Exception{
		//String key = "e6bfc302965749f189371e6f04927e22";
		KeyValueHolder params = new KeyValueHolder("key",key);
		params.addParam("gametype", 1);
		HttpGet post = HttpUtil.createHttpGet("http://www.naliqu.net/hall/getskgexploit.do", params);
		post.addHeader("Cookie", "JSESSIONID="+sessionId);
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 MicroMessenger/6.5.2.501 NetType/WIFI WindowsWechat QBCore/3.43.373.400 QQBrowser/9.0.2524.400");
		post.addHeader("Referer", "http://www.naliqu.net/hall/27/index.html?key="+key+"&page=1&gametype=1&roomkey="+roomKey+"&maint=0");
		//post.addHeader("Hm_lvt_8b3f54474964aab606e5c07d77075185=1495372972");
		//post.addHeader("Hm_lpvt_8b3f54474964aab606e5c07d77075185=1495603396");
		//post.addHeader(header);
		
		String resp = HttpUtil.execute(post);
		System.out.println("resp==="+resp);
		//if(resp != null){return;}
		List<Map> list = JsonUtil.json2List(resp,Map.class);
		Set<Long> roomIds = ClassUtil.newSet();
		for(Map item : list){
			roomIds.add(Long.valueOf(item.get("roomid").toString()));
		}
		List<Shanghui> existList = DBHelper.getDao().query(new QueryStatement(
				Shanghui.class, new DBCondition(Shanghui.JField.onlineRoomid,roomIds,DBOperator.IN))
					.appendQueryField(Shanghui.JField.id,Shanghui.JField.onlineRoomid)
		);
		
		
		
		//int totalPoint = 0;
		List<Shanghui> shanghuiList = ClassUtil.newList();
		
		
		for(Map item : list){
			long roomid = Long.valueOf(item.get("roomid").toString());
			boolean isExist = false;
			if(NullUtil.isNotEmpty(existList)){
				for(Shanghui esh : existList){
					if(esh.getOnlineRoomid() == roomid){
						isExist = true;//该房间已经录入过了，所以本局战绩可以跳过
						break;
					}
				}
			}
			if(isExist){
				continue;
			}
			
			long ctime = Long.valueOf(item.get("ctime").toString());
			//String date = DateUtil.date2Str(new Date(ctime),DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
			
			
			Shanghui sh = new Shanghui();
			
			JSONArray plist = (JSONArray)item.get("prdcList");
			StringBuffer membs = new StringBuffer();
			StringBuffer scores = new StringBuffer();
			for(int i=0;i<plist.size();i++){
				JSONObject p = plist.getJSONObject(i);
				String nick = p.getString("nick");
				String point = p.getString("point");
				String memb = getMemberByNickname(nick);
				membs.append(",").append(memb);
				scores.append(",").append(memb+":"+point);
				
			}
			
			sh.setPrice(10);
			sh.setType("xiangma");
			sh.setMembers(membs.substring(1));
			sh.setScore(scores.substring(1));
			sh.setSum(0);
			sh.setShui(0);
			sh.setSn(-111L);
			sh.setDate(new Date(ctime));
			sh.setIsOnline((short)1);
			sh.setOnlineRoomid(roomid);
			sh.setStatus((short)99);
			
			shanghuiList.add(sh);
		}
		if(NullUtil.isNotEmpty(shanghuiList)){
			DBHelper.getDao().insertBatch(shanghuiList);
			
			List<ShanghuiRound> roundList = ClassUtil.newList();
			for(Shanghui sh : shanghuiList){
				ShanghuiRound round = new ShanghuiRound();
				round.setShanghuiId(sh.getId());
				round.setScore(sh.getScore()+",shui:0");
				roundList.add(round);
			}
			DBHelper.getDao().insertBatch(roundList);	
				
		}
		
		
		
		
		
		//System.out.println("total_point:"+point);
		//list = list.subList(0, 3);
		//LogUtil.dump("xxxxx", list);
	}
	
	public static String getMemberByNickname(String nickname){
		Iterator it = members.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry)it.next();
			Map memb = (Map)entry.getValue();
			if(memb.get("nickname") != null && nickname.equalsIgnoreCase(memb.get("nickname").toString())){
				return entry.getKey().toString();
			}
		}
		return null;
	}
}
