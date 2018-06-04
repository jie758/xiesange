package com.xiesange.core.notify.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.EmailAttachment;

import com.xiesange.core.notify.target.EmailNotifyTarget;
import com.xiesange.core.util.NullUtil;

public class EMailInfo {
	private String subject;//邮件主题
	private String htmlContent;//邮件内容，支持HTML格式
	
	private List<EmailNotifyTarget> toList;//邮件接收者
	private List<EmailNotifyTarget> ccList;//邮件抄送者
	private List<EmailAttachment> attachs;//附件
	
	public EMailInfo(String subject,String content){
		this.subject = subject;
		this.htmlContent = content;
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public List<EmailNotifyTarget> getToList() {
		return toList;
	}
	public void setToList(List<EmailNotifyTarget> toList) {
		this.toList = toList;
	}
	
	public List<EmailNotifyTarget> getCcList() {
		return ccList;
	}
	public void setCcList(List<EmailNotifyTarget> ccList) {
		this.ccList = ccList;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	public List<EmailAttachment> getAttachs() {
		return attachs;
	}
	public void setAttachs(List<EmailAttachment> attachs) {
		this.attachs = attachs;
	}
	public void addAttach(EmailAttachment attach) {
		if(this.attachs == null){
			this.attachs = new ArrayList<EmailAttachment>();
		}
		this.attachs.add(attach);
	}
	public void addAttach(String path){
		String fileName = path.substring(path.lastIndexOf(File.separator)+1);
		addAttach(path,fileName);
	}
	public void addAttach(String path,String name){
		EmailAttachment attach = new EmailAttachment();
	    attach.setPath(path);
	    attach.setDisposition(EmailAttachment.ATTACHMENT);
	    if(NullUtil.isNotEmpty(name)){
	    	try{
	    		attach.setName(MimeUtility.encodeText(name));
	    	}catch(Exception e){
	    		attach.setName(name);
	    	}
	    	
	    }
	    //
	    addAttach(attach);
	}
	
	public void addTo(EmailNotifyTarget to) {
		if(this.toList == null){
			this.toList = new ArrayList<EmailNotifyTarget>();
		}
		this.toList.add(to);
	}
	public void addTo(String address) {
		addTo(address,null);
	}
	public void addTo(List<String> addressList) {
		for(String address : addressList){
			addTo(address,null);
		}
	}
	public void addTo(String address,String name) {
		if(this.toList == null){
			this.toList = new ArrayList<EmailNotifyTarget>();
		}
		this.toList.add(new EmailNotifyTarget(address,name));
	}
	
	public void addCc(EmailNotifyTarget cc) {
		if(this.ccList == null){
			this.ccList = new ArrayList<EmailNotifyTarget>();
		}
		this.ccList.add(cc);
	}
	public void addCc(String address) {
		addCc(address,null);
	}
	public void addCc(String address,String name) {
		if(this.ccList == null){
			this.ccList = new ArrayList<EmailNotifyTarget>();
		}
		this.ccList.add(new EmailNotifyTarget(address,name));
	}
	
	public List<EmailNotifyTarget> getAllTargetList(){
		List<EmailNotifyTarget> targetList = new ArrayList<EmailNotifyTarget>();
		if(this.toList != null){
			targetList.addAll(this.toList);
		}
		if(this.ccList != null){
			targetList.addAll(this.ccList);
		}
		return targetList.size() == 0 ? null : targetList;
	}
}
