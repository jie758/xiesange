package com.xiesange.baseweb.pojo;

import java.util.ArrayList;
import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.core.util.CommonUtil;

public class EmailHtmlSkin {
	private static EmailHtmlSkin INSTANCE;
	
	private List<String> template_content_list;
	
	private EmailHtmlSkin(){};//不能由外部实例化
	
	public static EmailHtmlSkin getInstance() throws Exception{
		return getInstance(null);
	}
	public static EmailHtmlSkin getInstance(String content) throws Exception{
		if(INSTANCE == null){
			INSTANCE = new EmailHtmlSkin();
			
			int width = 46;
			String titlePic = ETUtil.buildPicUrl("image/system/notify_email/title_pic.png");
			String linePic = ETUtil.buildPicUrl("image/system/notify_email/sep_line.png");
			String bottomPic = ETUtil.buildPicUrl("image/system/notify_email/bottom_pic.png");
			
			String homepagePic = ETUtil.buildPicUrl("image/system/notify_email/homepage.png");
			String weiboPic = ETUtil.buildPicUrl("image/system/notify_email/weibo.png");
			String facebookPic = ETUtil.buildPicUrl("image/system/notify_email/facebook.png");
			String youkuPic = ETUtil.buildPicUrl("image/system/notify_email/youku.png");
			
			INSTANCE.template_content_list = new ArrayList<String>();
			StringBuffer sb_title = new StringBuffer(256);
			sb_title.append("<style>p{display:inline}.ee_i{line-height:25px}</style>")
					.append("<div style='width:800px;margin:0 auto'>")//整个邮件内容区固定为800px
			  		.append("	<img width='100%' src='"+titlePic+"'/>")
			  		.append("	<div style='margin-top:30px;margin-bottom:40px;'>");
			INSTANCE.template_content_list.add(sb_title.toString());
			
			INSTANCE.template_content_list.add(null);//第二个元素由业务侧设置具体内容html
			
			StringBuffer sb_bottom = new StringBuffer(256);
			sb_bottom.append("	</div>")
			  .append("	<div style='margin:0 auto;width:100%;'><img src='"+linePic+"' width='100%'/></div>")
			  .append("	<table style='margin:0 auto;margin-top:30px;margin-bottom:10px'><tr>")
			  .append("		<td width='53px'>").append("<a href='http://www.elsetravel.com'><img src='"+homepagePic+"' width='"+width+"'/></a>").append("</td>")
			  .append("		<td width='53px'>").append("<a href='http://weibo.com/elsetravel?is_hot=1#_rnd1452321263941'><img src='"+weiboPic+"' width='"+width+"'/></a>").append("</td>")
			  .append("		<td width='53px'>").append("<a href='https://www.facebook.com/elsetravel888/?fref=ts'><img src='"+facebookPic+"' width='"+width+"'/></a>").append("</td>")
			  .append("		<td width='53px'>").append("<a href='http://i.youku.com/u/UMzI0NTgxMzgzNg=='><img src='"+youkuPic+"' width='"+width+"'/></a>").append("</td>")
			  .append("	</tr></table>")
			  .append("	<div style='margin:0 auto;width:120px'><img src='"+bottomPic+"' width='120'/></div>")
			  .append("</div>");
			
			INSTANCE.template_content_list.add(sb_bottom.toString());
		}
		if(content != null)
			INSTANCE.template_content_list.set(1, content);
		
		return INSTANCE;
	}
	
	public String buildHtml(String content){
		if(content != null)
			template_content_list.set(1, content);
		return CommonUtil.join(template_content_list);
	} 
}
