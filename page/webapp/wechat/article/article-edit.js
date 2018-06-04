var loading = new PageLoading($(".index-page"));
var articleId;
var articleCode;
var articleEntity;
var eteditor;
$(function(){
	//右上角菜单按钮点击
	initTitlebarBttns($(".index-page .titlebar"),[{
		iconfont : 'menu',
		menu_items : getMenus()
	}]);
	
	//articleId = common.getUrlParams("article_id");
	//if(!articleId){
	articleCode = common.getUrlParams("article_code");
	//}
	
	loading.show();
	loadData();
	
});

function loadData(){
	var isNew = articleCode == null;
	if(isNew){
		ajax.request({
			url : _base_url+"/web/article/getNewId.do",
			//need_progressbar : false,
			params : {
				//need_oauth:true
			},
			success : function(header,body){
				articleId = -1*body.newid;
				initPage();
			},
			complete : function(){
				loading.hide();
			}
		});
	}else{
		//文章内容
		ajax.request({
			url : _base_url+"/web/article/queryDetailByCode.do",
			need_progressbar : false,
			params : {
				article_code:articleCode,
				//need_oauth:true,
				need_wx_signature:true
			},
			success : function(header,body){
				articleEntity = body.article;
				articleId = articleEntity.id;
				initPage();
				
				//initEditor();
				//initArticle();
				
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
	
}

function initPage(){
	initEditor();
	initArticle();
	
	//按钮
	widget.bindTouchClick($(".index-page .footbar-bttn-ok"),function(){
		commit();
	});
}

function initArticle(){
	if(articleEntity){
		$(".article-info .name").val(articleEntity.name);
		$(".article-info .code").val(articleEntity.code);
		$(".messagetitle").val(articleEntity.shareMessageTitle);
		$(".messagedesc").val(articleEntity.shareMessageDesc);
		$(".timelinedesc").val(articleEntity.shareTimelineDesc);
		$(".share-pic img").attr("src",articleEntity.sharePic);
		eteditor.html(articleEntity.content);
	}
	
	var artId = articleId < 0 ? -1*articleId : articleId;
	xsg_qiniu.init(new QiniuJsSDK(),{
		browse_button : "uploadSharePic",
		domain : "resource.xiesange.com",
		path : 'image/article/'+artId+'/share_pic_'+common.unique(),
		events: {
            'FilesAdded': function(files) {
                xsg_progress.showProgressDialog(files, function(urls) {});
            },
            'BeforeUpload': function(file) {
            	xsg_progress.beginUpload(file.id);
            },
            'UploadProgress': function(file, percent, uploadedSize, speed) {
            	xsg_progress.updateProgress(file.id, percent, uploadedSize, speed);
            },
            'FileUploaded': function(file, url,key) {
            	xsg_progress.completeProgress(file.id, url);
                $('.share-pic img').attr('src', url);
                $('.share-pic img').attr('path', key);
            }
        }
	});
	
}

function initEditor(){
	$(".article-content").height(window.innerHeight - $(".titlebar").height()-$(".article-name").height());
	var artId = articleId < 0 ? -1*articleId : articleId;
	eteditor = new xsg_summernote({
		canvas : $(".article-content"),
		//html : articleEntity.content,
		height:400,
		padding_h : (window.innerWidth - 400)/2,
		qiniuOpt : {
			qiniuInstance : Qiniu,
			domain : "resource.xiesange.com",
			path : 'image/article/'+artId+'/*'
		},
		events : {
			complete:function(){
				loaded();
			}
		}
	});
}

function commit(){
	var $titlebar = $('.index-page .titlebar');
	var isNew = articleId == null;
	var code = $(".article-info .code").val();
	var name = $(".article-info .name").val();
	if(isNew){
		if(!code){
			message.errorHide("请输入编码",$titlebar);
			return;
		}
		if(!name){
			message.errorHide("请输入名称",$titlebar);
			return;
		}
	}
	ajax.request({
		url : _base_url+"/web/article/save.do",
		need_progressbar : $titlebar,
		params : {
			article_id:articleId,
			content : eteditor.html(),
			name : name,
			code : code,
			share_message_title:$(".messagetitle").val(),
			share_message_desc:$(".messagedesc").val(),
			share_timeline_desc:$(".timelinedesc").val(),
			share_pic:$(".share-pic img").attr("src"),
		},
		success : function(header,body){
			message.successHide("操作成功!正在刷新...",$titlebar);
			common.gotoPage("article-edit.html?article_code="+code,2000);
		},
		complete : function(){
		}
	});
}

function loaded(){
	loading.hide();
}