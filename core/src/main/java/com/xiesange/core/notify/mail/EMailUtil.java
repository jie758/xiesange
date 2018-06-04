package com.xiesange.core.notify.mail;

import java.util.List;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import com.xiesange.core.notify.target.EmailNotifyTarget;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;

public class EMailUtil
{
    private static final Logger logger = LogUtil.getLogger(EMailUtil.class);
	private static final String EMAIL_HOST = "smtp.exmail.qq.com";
    private static final String SENDER_USER = "service@xiesange.com";
    private static final String SENDER_PASSWORD = "sangeApply1209%";
    private static final String SENDER_USERNAME = "蟹三哥";
    
	public static void main(String[] args) throws Exception
    {
		
		EMailInfo mailInfo = new EMailInfo("[TTT]这是测试邮件","<B>aaaaaaaaaa</B>");
		//mailInfo.setFrom(new EmailNotifyTarget("2712921938@qq.com","皇图科技","kingwant1209%"));
		mailInfo.addTo(new EmailNotifyTarget("apply@xiesange.com"));
		
		sendHTML(mailInfo);
		
		System.out.println(111111);
    }
	public static void sendHTML(String target,String title,String content) throws EmailException{
		EMailInfo mailInfo = new EMailInfo(title,content);
		mailInfo.addTo(target);
		sendHTML(mailInfo);
	}
    public static void sendHTML(EMailInfo mailInfo) throws EmailException{
    	HtmlEmail email = new HtmlEmail();
    	email.setHostName(EMAIL_HOST);
    	email.setAuthentication(SENDER_USER, SENDER_PASSWORD);
    	email.setFrom(SENDER_USER,SENDER_USERNAME);
    	email.setCharset("UTF-8"); //编码
    	
    	email.setSubject(mailInfo.getSubject());
        
        if(NullUtil.isNotEmpty(mailInfo.getToList())){
        	for(EmailNotifyTarget to : mailInfo.getToList()){
        		logger.debug("...mail target to : "+to.getAddress());
        		email.addTo(to.getAddress(),to.getName());
        	}
        }
        
        if(NullUtil.isNotEmpty(mailInfo.getCcList())){
        	for(EmailNotifyTarget cc : mailInfo.getCcList()){
        		logger.debug("...mail target cc : "+cc.getAddress());
        		email.addCc(cc.getAddress(),cc.getName());
        	}
        }
        
        if(NullUtil.isNotEmpty(mailInfo.getAttachs())){
        	for(EmailAttachment attach : mailInfo.getAttachs()){
        		email.attach(attach);
        	}
        }
        email.setCharset("UTF-8");
        email.setHtmlMsg(mailInfo.getHtmlContent()==null?"<div>无内容</div>":mailInfo.getHtmlContent());
        
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        String aa = email.send();
        logger.debug("...success to send email");
    }
    
    public static String buildTable(TableTitleRow titleRow, List<TableRecordRow> recordRows)
    {
        StringBuffer sb = new StringBuffer(
                "<table border='0' cellpadding='0' cellspacing='0' style='font-size:11pt;font-family:微软雅黑;border-top:1px solid #808080;border-left:1px solid #808080;'>");
        if (titleRow !=null && !NullUtil.isEmpty(titleRow.getCellValues()))
        {
            sb.append("<tr style='background-color:#87CEFA;height:30px'>");
            for (int i = 0; i < titleRow.getCellValues().size(); i++)
            {
                String text = titleRow.getCellValue(i);
                int width = titleRow.getCellWidth(i);
                sb.append("<td style='word-break:break-all;border-bottom:1px solid #808080;border-right:1px solid #808080;text-align:center;' width='"
                        + width + "'>");
                sb.append(text);
                sb.append("</td>");
            }
            sb.append("</tr>");
        }

        if (!NullUtil.isEmpty(recordRows))
        {
            for (int i = 0; i < recordRows.size(); i++)
            {
                List<String> values = recordRows.get(i).getCellValues();
                List<String> colors = recordRows.get(i).getCellColors();
                sb.append("<tr style='height:30px'>");
                for (int k = 0; k < values.size(); k++)
                {
                    String color = NullUtil.isEmpty(colors.get(k)) ? "" : "color:"+colors.get(k);
                    String text = NullUtil.isEmpty(values.get(k)) ? "&nbsp;" : values.get(k);
                    sb.append("<td style='"+color+";text-indent:2px;word-break:break-all;border-bottom:1px solid #808080;border-right:1px solid #808080;text-align:left;' width='"
                            + titleRow.getCellWidth(k) + "'>");
                    text = text.replaceAll("\n", "<br/>");
                    text = text.replaceAll("\\s", "&nbsp;&nbsp;");
                    sb.append(text);
                    sb.append("</td>");
                }
                sb.append("</tr>");
            }
        }
        sb.append("</table>");

        return sb.toString();
    }
}
