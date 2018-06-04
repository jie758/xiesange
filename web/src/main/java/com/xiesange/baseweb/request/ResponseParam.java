package com.xiesange.baseweb.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



/**
 * 业务应答对象
 * @author wuyujie 2014年8月30日 上午10:33:24
 *
 */
public class ResponseParam
{
	private ResponseHeader header;
    private ResponseBody body;
    
    public static void main(String[] args) {
    	String respJson= "{\"body\":{\"result\":\"用户[]不存在!\"},\"header\":{\"errorCode\":0,\"responseDate\":\"2015-01-14 22:06:43\"}}";
		System.out.println("respJson:"+respJson);
		JSONObject jsonObj = JSON.parseObject(respJson);
		String body = jsonObj.getString("body");
		String header = jsonObj.getString("header");
		ResponseHeader respHeader = JSON.parseObject(header, ResponseHeader.class);
		ResponseBody respBody = JSON.parseObject(body, ResponseBody.class);
		
		
	}
    
    public ResponseParam(){
    }
    public ResponseParam(ResponseHeader header,ResponseBody body){
        this.header = header;
        this.body = body;
    }

    public ResponseHeader getHeader()
    {
        return header;
    }

    public ResponseBody getBody()
    {
        return body;
    }

	public void setHead(ResponseHeader header) {
		this.header = header;
	}

	public void setBody(ResponseBody body) {
		this.body = body;
	}
    
}
