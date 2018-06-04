package com.xiesange.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.xiesange.core.enumdefine.KeyValueEntry;
import com.xiesange.core.enumdefine.KeyValueHolder;
/**
 * http请求工具类
 * @author wuyujie Jan 29, 2015 6:54:35 PM
 *
 */
public class HttpUtil {
	protected static Logger logger = LogUtil.getLogger(HttpUtil.class);
	public static final CloseableHttpClient  HTTP_CLIENT;
	static{
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		
		final HttpClientBuilder builder = HttpClients.custom();
		builder.setConnectionManager(cm);
		HTTP_CLIENT = builder.build();
		
	};
	
	
	public static HttpPost createHttpPost(String url,KeyValueHolder param) throws Exception{
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> postData = null;
		if(param != null && NullUtil.isNotEmpty(param.getParams())){
			List<KeyValueEntry> paramEntries = param.getParams();
			postData = new ArrayList<BasicNameValuePair>();
			for(KeyValueEntry entry : paramEntries){
				postData.add(new BasicNameValuePair(entry.getCode(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));
		}
		return httpPost;
	}
	
	public static HttpPost createHttpPost(String url,String paramStr) throws Exception{
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(paramStr, "UTF-8"));
		return httpPost;
	}
	
	public static HttpPost createXmlHttpPost(String url,String paramStr) throws Exception{
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(paramStr, "UTF-8"));
		httpPost.addHeader("Content-Type", "text/xml");
		return httpPost;
	}
	
	public static HttpPost createJsonHttpPost(String url,String json) throws Exception{
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = new StringEntity(json,"utf-8");//解决中文乱码问题  
		entity.setContentEncoding("UTF-8");  
		entity.setContentType("application/json");  
		httpPost.setEntity(entity);
		//httpPost.setConfig(REQUEST_CONFIG);
		return httpPost;
	}
	
	public static HttpGet createHttpGet(String url,KeyValueHolder param) throws Exception{
		if(param != null && NullUtil.isNotEmpty(param.getParams())){
			List<KeyValueEntry> paramEntries = param.getParams();
			StringBuffer param_sb = new StringBuffer();
			for(KeyValueEntry entry : paramEntries){
				if(param_sb.length() > 0){
					param_sb.append("&");
				}
				param_sb.append(entry.getCode()).append("=").append(entry.getValue());
			}
			url = url+"?"+param_sb.toString();
		}
		HttpGet httpGet = new HttpGet(url);
		//httpGet.setConfig(REQUEST_CONFIG);
		return httpGet;
	}
	
	
	public static String execute(HttpRequestBase httpRequest) throws Exception{
		logger.debug("....begin to request :"+httpRequest.getURI());
		CloseableHttpResponse response = HTTP_CLIENT.execute(httpRequest,HttpClientContext.create());
		String respJson = null;
		try{
			InputStream is = response.getEntity().getContent();
			respJson = CommonUtil.inputStream2String(is);
		}catch(Exception e){
			response.close();
		}
		logger.debug("....finish to request http. "+respJson);
		return respJson;
	}
	
	/*public static HttpResponse post(HttpPost httpPost) throws Exception{
		System.out.println("begin to post...");
		HttpResponse response = HTTP_CLIENT.execute(httpPost);
		System.out.println("finish to post...");
		//解析应答参数
		//HttpEntity httpEntity = response.getEntity();
		//String respJson= CommonUtil.inputStream2String(httpEntity.getContent());
		return response;
	}*/
	
	public static void main(String[] aa) throws Exception{
		
		
	}
	
	private static void send2() throws Exception{
		//0,2015012918244742897278251,0,1,0,提交成功
		ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
		
		
		postData.add(new BasicNameValuePair("name", "13588830404"));
		postData.add(new BasicNameValuePair("pwd", "2E1139811FB0B7785600692AD98E"));
		postData.add(new BasicNameValuePair("content", "【皇图科技】this is test form kw"));
		postData.add(new BasicNameValuePair("mobile", "13588830404"));
		postData.add(new BasicNameValuePair("type", "pt"));
		
		//System.out.println("request_body：\n"+headerBody);
		
		HttpPost httpPost = new HttpPost("http://sms.1xinxi.cn/asmx/smsservice.aspx");
		httpPost.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));
		
		/*DefaultHttpClient HTTP_CLIENT = new DefaultHttpClient();
		HttpResponse response = HTTP_CLIENT.execute(httpPost);
		HttpEntity httpEntity = response.getEntity();
		String respJson= CommonUtil.inputStream2String(httpEntity.getContent());
		System.out.println("resp:"+respJson);*/
	}
	
	
	public static String execute(String url) {
		String rawHtml = null;
		//构造HttpClient
		final HttpClientBuilder builder = HttpClients.custom();
		builder.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)");
		final CloseableHttpClient httpClient = builder.build();
		
		Builder custom = RequestConfig.custom();
		HttpHost proxy = new HttpHost("127.0.0.1",16823);
		custom.setProxy(proxy);
		RequestConfig requestConfig = custom.build();
		
		final HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		
		CloseableHttpResponse response = null;
		final long start = System.currentTimeMillis();
		try {
			//处理response
			response = httpClient.execute(httpGet);
			final HttpEntity entity = response.getEntity();
			if(entity!=null) {
                rawHtml = EntityUtils.toString(entity);  
                EntityUtils.consume(entity);  
			}
			//System.out.println("解析{}成功,耗时{}毫秒", url, (System.currentTimeMillis()-start));
		} catch (Exception e) {
			e.printStackTrace();
			//logger.info("解析{}失败,耗时{}毫秒", url, (System.currentTimeMillis()-start));
		} finally {
			try {
				if(response!=null)response.close();
				if(httpClient!=null)httpClient.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rawHtml;
	}
	
	public static String rsaSign(String content, String privateKey, String charset)
	  {
	    try {
	      PrivateKey priKey = getPrivateKeyFromPKCS8("RSA", new ByteArrayInputStream(privateKey.getBytes()));

	      Signature signature = Signature.getInstance("SHA1WithRSA");

	      signature.initSign(priKey);

	      if (NullUtil.isEmpty(charset))
	        signature.update(content.getBytes());
	      else {
	        signature.update(content.getBytes(charset));
	      }

	      byte[] signed = signature.sign();

	      return null;//new String(Base64.encodeBase64(signed));
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	  }

	  public static String rsaSign(Map<String, String> params, String privateKey, String charset) throws Exception
	  {
	    String signContent = getSignContent(params);

	    return rsaSign(signContent, privateKey, charset);
	  }
	  
	  
	  public static String getSignContent(Map<String, String> sortedParams)
	  {
	    StringBuffer content = new StringBuffer();
	    List keys = new ArrayList(sortedParams.keySet());
	    Collections.sort(keys);
	    int index = 0;
	    for (int i = 0; i < keys.size(); ++i) {
	      String key = (String)keys.get(i);
	      String value = (String)sortedParams.get(key);
	      content.append(((index == 0) ? "" : "&") + key + "=" + value);
	      ++index;
	    }
	    return content.toString();
	  }
	  
	  public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins)
			    throws Exception
	  {
	    if ((ins == null) || (NullUtil.isEmpty(algorithm))) {
	      return null;
	    }

	    KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
	    
	    ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
        byte[] buff = new byte[100];  
        int rc = 0;  
        while ((rc = ins.read(buff, 0, 100)) > 0) {  
            swapStream.write(buff, 0, rc);  
        }  
        byte[] encodedKey = swapStream.toByteArray();  
	    

	    //encodedKey = Base64.decodeBase64(encodedKey);

	    return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
	  }

}
