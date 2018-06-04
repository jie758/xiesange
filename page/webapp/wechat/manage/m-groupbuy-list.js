var loading = new PageLoading();
var prodId;
$(function(){
	loading.show();
	initPage();
});

function initPage(){
	//var paramObj = common.getUrlParams();
	initTitlebarBttns($(".index-page .titlebar"),[{
		iconfont : 'add',
		handler : function(){
			common.gotoPage("../manage/m-groupbuy-detail.html");
		}
	},{
		iconfont : 'menu',
		menu_items : getMenus()
	}]);
	
	loadDate(0,function(){
		$(".index-page").show();
		loading.hide();
	});
	
	//分页点击
	initPagination($(".next-page"),page_count,function(index){
		loadDate(index);
	});
}

function loadDate(startIndex,callback){
	ajax.request({
		url : _base_url+"/web/manage/queryGroupbuyList.do",
		need_progressbar : $(".index-page .titlebar"),
		params : {
			page_index:startIndex,
			page_count:page_count
		},
		success : function(header,body){
			var list = body.groupbuyList;
			list && initGroupbuyList(list);
			
			updatePagination($('.next-page'),list && list.length);
			
			callback && callback.apply();
		}
	});
}

function initGroupbuyList(list){
	var $tempItem = $("#_item_temp");
	var $list = $(".index-page .groupbuy-list");
	for(var i=0;i<list.length;i++){
		var entity = list[i];
		
		var $item = $tempItem.clone().appendTo($list);
		$item.data("entity",entity);
		$item.attr("id","comment_"+entity.id);
		$item.find(".create-time").text(entity.createTime);
		$item.find(".status").text(entity.status==99?"开团中":"已结束");
		if(entity.status==99){
			$item.find(".status").addClass('xsg-font');
		}
		$item.find(".content").text(entity.intro);
		
		$item.show();
		
		createSplitline().appendTo($list);
	}
	
	widget.bindTouchClick($list,function(e){
		var $item = $(e.target).parents(".groupbuy-item");
		var gbid = $item.data("entity").id;
		common.gotoPage("m-groupbuy-detail.html?groupbuy_id="+gbid);
	});
	
	
}

function initStars($item,cmtEntity){
	var grade = cmtEntity.grade || 5;
	var $gradeContainer = $item.find(".grade-container");
	for(var k=0;k<5;k++){
		$("<span class='xsg-fontset star'>").appendTo($gradeContainer).html(k<grade?iconfontset.star:iconfontset.star_empty)
	}
}

function initTags($item,cmtEntity){
	var tags = cmtEntity.tags;
	if(tags == null || tags.length == 0){
		return;
	}
	var $taglist = $item.find(".tag-content");
	for(var i=0;i<tags.length;i++){
		var $tag = $("<span class='tag'>").appendTo($taglist);
		$tag.text(tags[i]);
	}
	
	$item.find(".tag-area").show();
}