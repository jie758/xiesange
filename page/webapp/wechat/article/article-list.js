var loading = new PageLoading();
var articleList;
$(function(){
	loading.show();
	initPage();
});

function initPage(){
	//右上角菜单按钮点击
	initTitlebarBttns($(".index-page .titlebar"),[{
		iconfont : 'add',
		handler : function(){
			common.gotoPage("../article/article-edit.html");
		}
	},{
		iconfont : 'menu',
		menu_items : getMenus()
	}]);
	
	ajax.request({
		url : _base_url+"/web/article/queryList.do",
		need_progressbar : false,
		params : {
			page_index:0
		},
		success : function(header,body){
			articleList = body.articleList;
			initArticleList();
		},
		complete : function(){
			loading.hide();
			$(".index-page").show();
		}
	});
}

function initArticleList(){
	if(articleList==null || articleList.length ==0){
		return;
	}
	var $list = $('.article-list');
	for(var i=0;i<articleList.length;i++){
		if(i > 0){
			createSplitline().appendTo($list);
		}
		var art = articleList[i];
		var $item = $("<div class='article-item'><span class='name'></span><span class='date'></span></div>").appendTo($list);
		$item.find(".name").text(art.name);
		$item.find(".date").text(art.createTime);
		$item.data("entity",art);
		$item.attr("id",art.id);
	}
	
	widget.bindTouchClick($list.children(),function(e){
		var $item = $(this);
		if($item == null || $item.length == 0){
			return;
		}
		var article = $item.data("entity");
		common.gotoPage("article-edit.html?article_code="+article.code);
	});
}