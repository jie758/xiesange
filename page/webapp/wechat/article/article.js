var loading = new PageLoading($(".index-page"));
var articleCode;
var articleEntity;
$(function(){
	articleCode = common.getUrlParams("article_code");
	
	loading.show();
	initPage();
});

function initPage(){
	//文章内容
	ajax.request({
		url : _base_url+"/web/article/queryDetailByCode.do",
		need_progressbar : false,
		params : {
			article_code:articleCode,
			need_wx_signature:true
		},
		success : function(header,body){
			articleEntity = body.article;
			$("title").html(articleEntity.name);
			initArticle();
			
			initWxConfig(body.wxSignature,{
				url : buildTransitionUrl(_base_host+"/wechat/article/article.html?article_code="+articleCode),
				img : articleEntity.sharePic,
				timeline_desc : articleEntity.shareTimelineDesc,
				message_title : articleEntity.shareMessageTitle,
				message_desc : articleEntity.shareMessageDesc
			});
		},
		complete : function(){
			loading.hide();
		}
	});
}



function initArticle(){
	if(!articleEntity)
		return;
	$(".article-content").html(articleEntity.content);
}

function loaded(){
	loading.hide();
}