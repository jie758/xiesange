package com.xiesange.baseweb.wechat.pojo;

import java.util.List;

public class ArticleMessage extends Message {
	private int ArticleCount;
	private List<Article> Articles;
	
	public ArticleMessage(){
		this.setMsgType("news");
	}
	
	public int getArticleCount() {
		return ArticleCount;
	}
	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}
	public List<Article> getArticles() {
		return Articles;
	}
	public void setArticles(List<Article> articles) {
		Articles = articles;
	}
	
}
