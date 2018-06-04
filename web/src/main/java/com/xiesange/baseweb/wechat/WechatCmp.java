package com.xiesange.baseweb.wechat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.log4j.Logger;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.wechat.define.WechatDefine;
import com.xiesange.baseweb.wechat.define.WechatDefine.VendorConfigType;
import com.xiesange.baseweb.wechat.define.WechatParamDefine;
import com.xiesange.baseweb.wechat.pojo.CustomArticleItem;
import com.xiesange.baseweb.wechat.pojo.MediaResponse;
import com.xiesange.baseweb.wechat.pojo.Menu;
import com.xiesange.baseweb.wechat.pojo.OAuthToken;
import com.xiesange.baseweb.wechat.pojo.Signature;
import com.xiesange.baseweb.wechat.pojo.TicketInfo;
import com.xiesange.baseweb.wechat.pojo.WXUserInfo;
import com.xiesange.baseweb.wechat.pojo.WechatAccessToken;
import com.xiesange.baseweb.wechat.pojo.WxTempMessageParam;
import com.xiesange.baseweb.wechat.pojo.WxTempMessageReq;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.HttpUtil;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.RandomUtil;
import com.xiesange.gen.dbentity.activity.ActivityGzhReply;
import com.xiesange.gen.dbentity.wx.WxConfig;
import com.xiesange.gen.dbentity.wx.WxOauthUser;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;

public class WechatCmp {
	private static Logger logger = Logger.getLogger(WechatCmp.class);
	
		
	
	/**
	 * 获取普通的accessToken
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午9:55:30
	 */
	public static String getAccessToken() throws Exception{
		//return "KMaOT3kil-Gt3wIjFVbtjNz4kTG59rnO-8mbWc5jzhdMbobZOEFGg-KF4fP8MgWy49qyP-rFuyUnQ-ZDiQxeUDhucNTsvRB8QqnlOJQRmamhoDkFlLfNKmQ1MSUF7bbQOFPfACAWNX";
		WxConfig configEntity = getConfig(VendorConfigType.wx_access_token);
		if(configEntity != null && configEntity.getExpireTime().after(DateUtil.now())){
			//如果当前accessToken还没有过期，则直接取用
			return configEntity.getValue();
		}else{
			KeyValueHolder params = new KeyValueHolder();
			params.addParam(WechatParamDefine.GET_ACCESSTOKEN.grant_type.name(), "client_credential");
			params.addParam(WechatParamDefine.COMMON.appid.name(), WechatDefine.WECHAT_APP_ID);
			params.addParam(WechatParamDefine.COMMON.secret.name(), WechatDefine.WECHAT_SECRET);
			
			String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_ACCESSTOKEN, params));
			
			WechatAccessToken token = JsonUtil.json2Obj(respJson, WechatAccessToken.class);
			String tokenStr = token.getAccess_token();
			
			updateConfig(VendorConfigType.wx_access_token, configEntity,tokenStr,token.getExpires_in());
			
			return tokenStr;
		}
	}
	
	public static OAuthToken accessOAuthToken(String code) throws Exception{
		KeyValueHolder params = new KeyValueHolder();
		params.addParam(WechatParamDefine.GET_OAUTHTOKEN.grant_type.name(), "authorization_code");
		params.addParam(WechatParamDefine.COMMON.appid.name(), WechatDefine.WECHAT_APP_ID);
		params.addParam(WechatParamDefine.COMMON.secret.name(), WechatDefine.WECHAT_SECRET);
		params.addParam(WechatParamDefine.GET_OAUTHTOKEN.code.name(), code);
		
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_OAUTH_TOKEN, params));
	
		logger.debug("accessOAuthToken json : "+respJson);
		
		OAuthToken token = JsonUtil.json2Obj(respJson, OAuthToken.class);
		return token;
	}
	
	/**
	 * 根据oauthcode查询对应的用户信息
	 * 先从本地数据库表中查找，因为查询过一次会存储下来；如果查不到或者记录已过期则重新去微信服务器查找
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static WxOauthUser getOAuthInfo(String code) throws Exception{
		WxOauthUser tokenEntity = DBHelper.getDao().querySingle(
				WxOauthUser.class, 
				new DBCondition(WxOauthUser.JField.code,code)
		);
		
		if(tokenEntity != null && tokenEntity.getExpireTime().after(DateUtil.now())){
			//如果当前还没有过期，则直接取用
			return tokenEntity;
		}
		
		OAuthToken accessToken = WechatCmp.accessOAuthToken(code);
		
		if(accessToken == null || NullUtil.isEmpty(accessToken.getAccess_token())){
			return null;
		}
		
		Date expireTime = DateUtil.offsetSecond(DateUtil.now(),Integer.parseInt(accessToken.getExpires_in()));
		
		if(tokenEntity == null){
			tokenEntity = new WxOauthUser();
			tokenEntity.setCode(code);
			tokenEntity.setOpenid(accessToken.getOpenid());
			tokenEntity.setToken(accessToken.getAccess_token());
			tokenEntity.setExpireTime(expireTime);
			tokenEntity.setScope(accessToken.getScope().equalsIgnoreCase("snsapi_userinfo")?(short)2:(short)1);
			DBHelper.getDao().insert(tokenEntity);
		}else{
			//说是在数据中有但是已过期，需要更新code值和过期时间
			long id = tokenEntity.getId();
			tokenEntity.setToken(accessToken.getAccess_token());
			tokenEntity.setExpireTime(expireTime);
			DBHelper.getDao().updateById(tokenEntity,id);
		}
		
		return tokenEntity;
	}
	
	
	public static void createMenu(Menu menu) throws Exception{
		String accessToken = getAccessToken();
		
		String json = JsonUtil.obj2Json(menu);
		System.out.println("request json:"+json);
		String respJson = HttpUtil.execute(HttpUtil.createJsonHttpPost(WechatDefine.URL_CREATE_MENU+"?access_token="+accessToken,json));
		System.out.println(respJson);
	}
	
	/**
	 * 获取粉丝用户信息。该用户必须关注了公众号才能获取到相应信息
	 * @param openId
	 * @throws Exception
	 */
	public static WXUserInfo getFans(String openId) throws Exception{
		KeyValueHolder params = new KeyValueHolder();
		params.addParam(WechatParamDefine.COMMON.access_token.name(), getAccessToken());
		params.addParam(WechatParamDefine.GET_USER.openid.name(), openId);
		params.addParam(WechatParamDefine.GET_USER.lang.name(), "zh_CN");
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_USER, params));
		logger.debug("----getUser:"+respJson);
		//System.out.println("getUser : "+respJson);
		return JsonUtil.json2Obj(respJson, WXUserInfo.class);
	}
	
	/**
	 * 获取用户信息。无需关注公众号，是通过显示授权的方来获取
	 * @param oAuthToken
	 * @param openId
	 * @return
	 * @throws Exception
	 */
	public static WXUserInfo getUserByOAuth(String oAuthToken,String openId) throws Exception{
		KeyValueHolder params = new KeyValueHolder();
		params.addParam(WechatParamDefine.COMMON.access_token.name(), oAuthToken);
		params.addParam(WechatParamDefine.COMMON.openid.name(), openId);
		
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_USERINFO, params));
		
		logger.debug("accessUserInfo:"+respJson);
		
		WXUserInfo userInfo = JsonUtil.json2Obj(respJson, WXUserInfo.class);
		
		return userInfo;
	}
	
	public static void getAllMaterial() throws Exception{
		KeyValueHolder params = new KeyValueHolder();
		params.addParam(WechatParamDefine.COMMON.access_token.name(), getAccessToken());
		params.addParam(WechatParamDefine.GET_MATERIAL.type.name(), WechatDefine.MESSAGE_TYPE.image.name());
		params.addParam(WechatParamDefine.GET_MATERIAL.count.name(), "20");
		params.addParam(WechatParamDefine.GET_MATERIAL.offset.name(), "0");
		
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_ALL_MATERIAL, params));
	
		System.out.println(respJson);
	}
	
	
	
	
	
	
	public static Signature buildSignature(String url) throws Exception{
		long timestamp = System.currentTimeMillis()/1000;
		String noncestr = RandomUtil.getString(16);
		String ticket = getJsTicket();
		StringBuffer str1 = new StringBuffer("jsapi_ticket=").append(ticket)
							.append("&noncestr=").append(noncestr)
							.append("&timestamp=").append(timestamp)
							.append("&url=").append(url);
		String signature = DigestUtils.shaHex(str1.toString());
		logger.debug("=======str1 : "+str1);
		//logger.debug("=======signature : "+signature);
		return new Signature(WechatDefine.WECHAT_APP_ID,timestamp,noncestr,signature);
	}
	
	public static String getJsTicket() throws Exception{
		WxConfig configEntity = getConfig(VendorConfigType.wx_jsapi_ticket);
		if(configEntity != null && configEntity.getExpireTime().after(DateUtil.now())){
			//如果当前jsticket还没有过期，则直接取用
			return configEntity.getValue();
		}else{
			KeyValueHolder params = new KeyValueHolder();
			params.addParam(WechatParamDefine.COMMON.access_token.name(), getAccessToken());
			params.addParam(WechatParamDefine.COMMON.type.name(), "jsapi");
			String respJson = HttpUtil.execute(HttpUtil.createHttpGet(WechatDefine.URL_GET_TICKET, params));
			logger.debug("getJsTicket:"+respJson);
			TicketInfo ticketInfo = JsonUtil.json2Obj(respJson, TicketInfo.class);
			
			String value = ticketInfo.getTicket();
			
			updateConfig(VendorConfigType.wx_jsapi_ticket, configEntity, value,ticketInfo.getExpires_in());
			
			return value;
		}
		
	}
	
	
	
	
	
	
	public static void sendTemplateMessage(String toUser,String tempId,String url,WxTempMessageParam params) throws Exception{
		WxTempMessageReq req = new WxTempMessageReq(toUser,tempId,url,params.getData());
		String sendUrl = WechatDefine.URL_SEND_TEMP_MESSSGE+"?access_token="+getAccessToken();
		String json = JsonUtil.obj2Json(req);
		//logger.debug("sendTemplateMessage response:"+json);
		//System.out.println("json:"+json);
		String respJson = HttpUtil.execute(HttpUtil.createJsonHttpPost(sendUrl, json));
		logger.debug("sendTemplateMessage response:"+respJson);
		//System.out.println(respJson);
	}
	
	
	
	/**
	 * 获取配置记录
	 * @param typeEnum
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:16:20
	 */
	/*public static VendorConfig getConfig(VendorConfigType typeEnum) throws Exception{
		return getConfig(typeEnum.name());
	}*/
	public static WxConfig getConfig(VendorConfigType typeEnum) throws Exception{
		WxConfig configEntity = DBHelper.getDao().querySingle(
				WxConfig.class, 
				new DBCondition(WxConfig.JField.type,typeEnum.name())
		);
		
		return configEntity;
	}
	
	public static WxConfig updateConfig(VendorConfigType typeEnum,WxConfig configEntity,String value,int expireIn) throws Exception{
		Date expireTime = null;
		if(expireIn > 0){
			//预留给20s，因为网络交互也有时间，不能把时间卡的太死
			expireTime = DateUtil.offsetSecond(DateUtil.now(),expireIn-20);
		}else{
			//小于0表示是永久有效的
			expireTime = ETUtil.getPermanentDate();
		}
		
		if(configEntity == null){
			//说明之前没有查询过，新插入
			configEntity = new WxConfig();
			configEntity.setType(typeEnum.name());
			configEntity.setValue(value);
			configEntity.setExpireTime(expireTime);
			
			DBHelper.getDao().insert(configEntity);
		}else{
			long id = configEntity.getId();
			//已经过期了,更新失效时间
			configEntity = new WxConfig();
			configEntity.setExpireTime(expireTime);
			configEntity.setValue(value);
			DBHelper.getDao().updateById(configEntity,id);
		}
		
		return configEntity;
	}
	
	
	/**
	 * 发送客服文本消息
	 * @param toUser
	 * @param text
	 * @throws Exception
	 * @author Wilson 
	 * @date 2017年3月24日
	 */
	public static void sendCustomText(String toUser,String text) throws Exception{
		String accessToken = WechatCmp.getAccessToken();
		Map<String,Object> map = ClassUtil.newMap();
		map.put("touser", toUser);
		map.put("msgtype", "text");
		Map<String,Object> textMap = ClassUtil.newMap();
		map.put("text", textMap);
		textMap.put("content", text);
		
		
		HttpPost post = HttpUtil.createJsonHttpPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+accessToken
				, JsonUtil.obj2Json(map));
		
		String resp = HttpUtil.execute(post);
		logger.debug("....." + resp);
	}
	/**
	 * 发送客服图片消息
	 * @param toUser
	 * @param mediaId
	 * @throws Exception
	 * @author Wilson 
	 * @date 2017年3月24日
	 */
	public static void sendCustomImage(String toUser,String mediaId) throws Exception{
		String accessToken = WechatCmp.getAccessToken();
		Map<String,Object> map = ClassUtil.newMap();
		map.put("touser", toUser);
		map.put("msgtype", "image");
		Map<String,Object> imageMap = ClassUtil.newMap();
		map.put("image", imageMap);
		imageMap.put("media_id", mediaId);
		
		HttpPost post = HttpUtil.createJsonHttpPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+accessToken
				, JsonUtil.obj2Json(map));
		
		String resp = HttpUtil.execute(post);
		logger.debug("....." + resp);
	}
	
	public static void sendCustomArticle(String toUser,List<CustomArticleItem> items) throws Exception{
		String accessToken = WechatCmp.getAccessToken();
		Map<String,Object> map = ClassUtil.newMap();
		map.put("touser", toUser);
		map.put("msgtype", "news");
		Map<String,Object> newsMap = ClassUtil.newMap();
		map.put("news", newsMap);
		
		
		List<Map> articleItems = ClassUtil.newList();
		newsMap.put("articles", articleItems);
		for(CustomArticleItem item : items){
			Map<String,Object> itemMap = ClassUtil.newMap();
			itemMap.put("title", item.getTitle());
			itemMap.put("description", item.getDesc());
			itemMap.put("url", item.getUrl());
			itemMap.put("picurl", item.getImage());
			
			articleItems.add(itemMap);
		}
		
		HttpPost post = HttpUtil.createJsonHttpPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+accessToken
				, JsonUtil.obj2Json(map));
		
		String resp = HttpUtil.execute(post);
		logger.debug("....." + resp);
	}
	
	
	public static String createQRCodeTicket(String key) throws Exception{
		String accessToken = WechatCmp.getAccessToken();
		Map<String,Object> map = ClassUtil.newMap();
		map.put("action_name", "QR_LIMIT_STR_SCENE");
		Map<String,Object> actionInfo = ClassUtil.newMap();
		map.put("action_info", actionInfo);
		Map<String,Object> scene = ClassUtil.newMap();
		actionInfo.put("scene", scene);
		scene.put("scene_str", key);
		
		HttpPost post = HttpUtil.createJsonHttpPost("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+accessToken
				, JsonUtil.obj2Json(map));
		
		String resp = HttpUtil.execute(post);
		Map respMap = JsonUtil.json2Map(resp);
		
		String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+respMap.get("ticket");
		logger.debug(".....url:"+url);
		//logger.debug("....." + respMap.get("ticket"));
		
		return respMap==null?null:url;
	}
	
	
	/**
	 * 这里说下，在上传视频素材的时候，微信说不超过20M，我试了下，超过10M调通的可能性都比较小，建议大家上传视频素材的大小小于10M比交好
	 * 
	 * @param accessToken
	 * @param file
	 *            上传的文件
	 * @param title
	 *            上传类型为video的参数
	 * @param introduction
	 *            上传类型为video的参数
	 * @throws IOException 
	 */
	public static MediaResponse uploadPermanentMedia(String imageUrl) throws Exception {
		//String imagePath = "https://img.yzcdn.cn/upload_files/2017/03/24/FpbK9qbZh-MgcMKc2PFqkVh-C0HN.jpg!580x580.jpg";
		URL url = new URL(imageUrl);//文件夹中的某个文件对应的URL
		
		HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
		long fileLength = urlConn.getContentLengthLong();
		
		DataInputStream in = new DataInputStream(url.openStream());
		String fileName = url.getFile();
		try{
			return uploadPermanentMedia(in,fileLength,fileName);
		}catch(Exception e){
			throw e;
		}finally{
			in.close();
		}
	}
	
	public static MediaResponse uploadPermanentMedia(File file) throws Exception {
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		
		String fileName = file.getName();
		//String suffix = fileName.substring(fileName.lastIndexOf("."),fileName.length());
		//String type = "image/"+suffix; // 我这里写死
		try{
			return uploadPermanentMedia(in,file.length(),fileName);
		}catch(Exception e){
			logger.error(e, e);
			in.close();
		}
		return null;
	}
	public static MediaResponse uploadPermanentMedia(DataInputStream imageInputStream,long fileLength,String fileName) throws Exception {
		logger.debug("-------begin to uploadPermanentMedia");
		String accessToken = WechatCmp.getAccessToken();
		try {
			// 这块是用来处理如果上传的类型是video的类型的
			//JSONObject j = new JSONObject();
			/*j.put("title", title);
			j.put("introduction", introduction);*/

			// 拼装请求地址
			URL url = new URL(WechatDefine.URL_ADD_MATERIAL+"?access_token="+accessToken);
			String result = null;
			String suffix = fileName.substring(fileName.lastIndexOf("."),fileName.length());
			String type = "image/"+suffix;
			/**
			 * 你们需要在这里根据文件后缀suffix将type的值设置成对应的mime类型的值
			 */
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); // post方式不能使用缓存
			// 设置请求头信息
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");

			// 设置边界,这里的boundary是http协议里面的分割符，不懂的可惜百度(http 协议
			// boundary)，这里boundary 可以是任意的值(111,2222)都行
			String BOUNDARY = "----------" + System.currentTimeMillis();
			con.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
			// 请求正文信息
			// 第一部分：

			StringBuilder sb = new StringBuilder();

			// 这块是post提交type的值也就是文件对应的mime类型值
			sb.append("--"); // 必须多两道线
								// 这里说明下，这两个横杠是http协议要求的，用来分隔提交的参数用的，不懂的可以看看http
								// 协议头
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"type\" \r\n\r\n"); // 这里是参数名，参数名和值之间要用两次
			sb.append(type + "\r\n"); // 参数的值

			/**
			 * 这里重点说明下，上面两个参数完全可以卸载url地址后面 就想我们平时url地址传参一样，
			 * http://api.weixin.qq.
			 * com/cgi-bin/material/add_material?access_token
			 * =##ACCESS_TOKEN##&type=""&description={} 这样，如果写成这样，上面的
			 * 那两个参数的代码就不用写了，不过media参数能否这样提交我没有试，感兴趣的可以试试
			 */

			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			// 这里是media参数相关的信息，这里是否能分开下我没有试，感兴趣的可以试试
			sb.append("Content-Disposition: form-data;name=\"media\";filename=\""
					+ fileName + "\";filelength=\"" + fileLength + "\" \r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			System.out.println(sb.toString());
			byte[] head = sb.toString().getBytes("utf-8");
			// 获得输出流
			OutputStream out = new DataOutputStream(con.getOutputStream());
			// 输出表头
			out.write(head);
			// 文件正文部分
			// 把文件已流文件的方式 推入到url中
			//String imagePath = "https://img.yzcdn.cn/upload_files/2017/03/24/FpbK9qbZh-MgcMKc2PFqkVh-C0HN.jpg!580x580.jpg";
			//URL imageUrl=new URL(imagePath);//文件夹中的某个文件对应的URL
			//DataInputStream is = new DataInputStream(imageUrl.openStream());
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = imageInputStream.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			//is.close();
			// 结尾部分，这里结尾表示整体的参数的结尾，结尾要用"--"作为结束，这些都是http协议的规定
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
			out.write(foot);
			out.flush();
			out.close();
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = null;
			// 定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
			
			logger.debug("-------finish to uploadPermanentMedia:\n\r"+result);
			
			MediaResponse response = JsonUtil.json2Obj(result, MediaResponse.class);
			// 使用JSON-lib解析返回结果
			/*JSONObject jsonObject = JSONObject.fromObject(result);
			if (jsonObject.has("media_id")) {
				System.out.println("media_id:"
						+ jsonObject.getString("media_id"));
			} else {
				System.out.println(jsonObject.toString());
			}
			System.out.println("json:" + jsonObject.toString());*/
			return response;
		} catch (IOException e) {
			logger.error(e, e);
		} finally {
		}
		return null;
	}
	
	
	public static String parseTextReply(ActivityGzhReply replyEntity) throws Exception{
		if(replyEntity.getReplyType() == WechatDefine.REPLY_TYPE_TEXT){
			//属性：text,url
			List<Map> lines = JsonUtil.json2List(replyEntity.getReplyValue(), Map.class);
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<lines.size();i++){
				Map line = lines.get(i);
				if(i > 0){
					sb.append("\r");
				}
				if(line == null){
					//表示空行
					continue;
				}
				String text = (String)line.get("text");
				String url = (String)line.get("url");
				if(NullUtil.isNotEmpty(url)){
					sb.append("<a href=\""+url+"\">");
				}
				sb.append(text);
				if(NullUtil.isNotEmpty(url)){
					sb.append("</a>");
				}
			}
			return sb.toString();
		}
		return null;
	}
	
	
	public static String parseImageReply(ActivityGzhReply replyEntity) throws Exception{
		Map map = JsonUtil.json2Map(replyEntity.getReplyValue());
		if(NullUtil.isNotEmpty(map)){
			return (String)map.get("media_id");
		}
		return null;
	}
	public static List<CustomArticleItem> parseArticleReply(ActivityGzhReply replyEntity) throws Exception{
		List<CustomArticleItem> items = JsonUtil.json2List(replyEntity.getReplyValue(),CustomArticleItem.class);
		
		return items;
	}
	
	public static void main(String[] args) throws Exception {
		createQRCodeTicket("testaaa");
	}
}
