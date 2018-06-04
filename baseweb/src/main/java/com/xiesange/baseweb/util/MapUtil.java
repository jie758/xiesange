package com.xiesange.baseweb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiesange.baseweb.pojo.MapGeoCodeResult;
import com.xiesange.core.util.HttpUtil;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.PinyinUtil;

public class MapUtil {
	public static final String MBOX_MAP_URL = "https://api.mapbox.com/v4/geocode/mapbox.places/%s.json?types=%s&access_token=pk.eyJ1IjoidGlhbndhbmdsYW96aXd1IiwiYSI6ImNpZXVxMTdkbjBuMDlzcG0ydWQyOHV4NmgifQ._At56qBczUrYzaHSlBFOHQ";
	public static final String MBOX_TOKEN = "pk.eyJ1IjoidGlhbndhbmdsYW96aXd1IiwiYSI6ImNpZXVxMTdkbjBuMDlzcG0ydWQyOHV4NmgifQ._At56qBczUrYzaHSlBFOHQ";
	
	public static final String GOOGLE_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=true&language=%s&key=AIzaSyBhkk3iwYhNp1nZnGOdOgiS84XE43wKBZ4";
	
	
	public static void main(String[] args) throws Exception {
		queryGoogleLocationByKey("asdfasd","asdfasdf");
		
		
		//queryGoogleLocationByKey("zhejiang","zh-CN");
		
		//queryGoogleLocationByKey("hangzhou");
		//Map result = JsonUtil.json2Map(respJson);
		//LogUtil.dump("result", map);
		
		
		/*CloseableHttpClient httpClient = HttpUtil.HTTP_CLIENT;//new HttpClient(new MultiThreadedHttpConnectionManager());
		httpClient.getHostConfiguration().setProxy("85.25.109.152", 3128);
		Geocoder geocoder = new AdvancedGeoCoder(httpClient);GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress("Paris, France").setLanguage("en").getGeocoderRequest();
		GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);*/
	}
	
	public void getGeoCode(String address, boolean ssl) throws Exception {
	    // build url
	    StringBuilder url = new StringBuilder("http");
	    if ( ssl ) {
	        url.append("s");
	    }
	   
	    url.append("://maps.googleapis.com/maps/api/geocode/json?");
	   
	    if ( ssl ) {
	        url.append("key=");
	        url.append("AIzaSyBhkk3iwYhNp1nZnGOdOgiS84XE43wKBZ4");
	        url.append("&");
	    }
	    url.append("sensor=false&address=");
	    url.append( URLEncoder.encode(address) );
	   
	    // request url like: http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address) + "&sensor=false"
	    // do request
    	CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url.toString());

        // set common headers (may useless)
        request.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:31.0) Gecko/20100101 Firefox/31.0 Iceweasel/31.6.0");
        request.setHeader("Host", "maps.googleapis.com");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Encoding", "gzip, deflate");

    	CloseableHttpResponse response = httpclient.execute(request);
        HttpEntity entity = response.getEntity();

        // recover String response (for debug purposes)
        StringBuilder result = new StringBuilder();
    	BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            result.append(inputLine);
            result.append("\n");
        }
        return;
	}
	
	
	public static List<MapGeoCodeResult> queryGoogleLocationByKey(String key,String lang) throws Exception{
		key = PinyinUtil.getFullSpell(key);
		String url = String.format(GOOGLE_GEOCODE_URL, key,lang);
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(url, null));
		LogUtil.getLogger(MapUtil.class).debug("respJson:"+respJson);
		Map map = JsonUtil.json2Map(respJson);
		JSONArray results = (JSONArray)map.get("results");
		List<MapGeoCodeResult> geoList = new ArrayList<MapGeoCodeResult>();
		for(int i=0;i<results.size();i++){
			JSONObject item = (JSONObject)results.get(i);
			JSONArray components = item.getJSONArray("address_components");
			String longName = components.getJSONObject(0).getString("long_name");
			String formattedAddress = item.getString("formatted_address");
			JSONObject location = item.getJSONObject("geometry").getJSONObject("location");
			String lat = location.getString("lat");
			String lng = location.getString("lng");
			
			MapGeoCodeResult geoResult = new MapGeoCodeResult();
			geoResult.setText(longName);
			geoResult.setPlace_name(formattedAddress);
			geoResult.setCenter(new Double[]{Double.valueOf(lng),Double.valueOf(lat)});
			geoList.add(geoResult);
		}
		return geoList;
	}
	public static List<MapGeoCodeResult> queryLocationByKey(String key) throws Exception{
		String url = String.format(MBOX_MAP_URL, key,"place,address");
		String respJson = HttpUtil.execute(HttpUtil.createHttpGet(url, null));
		LogUtil.getLogger(MapUtil.class).debug("respJson:"+respJson);
		
		Map map = JsonUtil.json2Map(respJson);
		
		JSONArray features = (JSONArray)map.get("features");
		List<MapGeoCodeResult> aa = JsonUtil.json2List(features, MapGeoCodeResult.class);
		
		LogUtil.dump("xxxx", aa);
		
		return aa;
	}
	
	/**
	 * 根据经纬度反向解析地址，有时需要多尝试几次
	 * 注意:(摘自：http://code.google.com/intl/zh-CN/apis/maps/faq.html
	 * 提交的地址解析请求次数是否有限制？) 如果在 24 小时时段内收到来自一个 IP 地址超过 2500 个地址解析请求， 或从一个 IP
	 * 地址提交的地址解析请求速率过快，Google 地图 API 编码器将用 620 状态代码开始响应。 如果地址解析器的使用仍然过多，则从该 IP
	 * 地址对 Google 地图 API 地址解析器的访问可能被永久阻止。
	 * 
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 * @return
	 */
	public static String GetAddr(String latitude, String longitude) {
		String addr = "";

		// 也可以是http://maps.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s，不过解析出来的是英文地址
		// 密钥可以随便写一个key=abc
		// output=csv,也可以是xml或json，不过使用csv返回的数据最简洁方便解析
		String url = String.format(
				"http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s",
				latitude, longitude);
		System.out.println(url);
		URL myURL = null;
		URLConnection httpsConn = null;
		try {
			myURL = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		try {
			httpsConn = (URLConnection) myURL.openConnection();
			if (httpsConn != null) {
				InputStreamReader insr = new InputStreamReader(
						httpsConn.getInputStream(), "UTF-8");
				BufferedReader br = new BufferedReader(insr);
				String data = null;
				if ((data = br.readLine()) != null) {
					System.out.println(data);
					String[] retList = data.split(",");
					if (retList.length > 2 && ("200".equals(retList[0]))) {
						addr = retList[2];
						addr = addr.replace("\"", "");
					} else {
						addr = "";
					}
				}
				insr.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return addr;
	}

	public static String getCoordinate(String addr) {
		String addrs = "";
		String address = null;
		try {
			address = java.net.URLEncoder.encode(addr, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		;
		String output = "csv";
		String key = "abc";
		String url = String.format(
				"http://maps.google.com/maps/geo?q=%s&output=%s&key=%s",
				address, output, key);
		System.out.println(url);
		URL myURL = null;
		
		/*Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("US1.ISS.TF", "8989"));
		URLConnection conn = url.openConnection(proxy);*/
		
		
		URLConnection httpsConn = null;
		// 进行转码
		try {
			myURL = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		try {
			httpsConn = (URLConnection) myURL.openConnection();
			if (httpsConn != null) {
				InputStreamReader insr = new InputStreamReader(
						httpsConn.getInputStream(), "UTF-8");
				BufferedReader br = new BufferedReader(insr);
				String data = null;
				if ((data = br.readLine()) != null) {
					System.out.println(data);
					String[] retList = data.split(",");
					/*
					 * String latitude = retList[2]; String longitude =
					 * retList[3];
					 * 
					 * System.out.println("纬度"+ latitude);
					 * System.out.println("经度"+ longitude);
					 */

					if (retList.length > 2 && ("200".equals(retList[0]))) {
						addrs = retList[2];
						addrs = addr.replace("\"", "");
					} else {
						addrs = "";
					}
				}
				insr.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		LogUtil.getLogger(MapUtil.class).debug(addrs);
		return addrs;
	}
}
