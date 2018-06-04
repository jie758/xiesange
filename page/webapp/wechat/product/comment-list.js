var loading = new PageLoading();
var prodId;
$(function(){
	loading.show();
	initPage();
});

function initPage(){
	var paramObj = common.getUrlParams();
	prodId = paramObj.product_id;
	loadDate(0,true,function(){
		$(".index-page").show();
		loading.hide();
		
		initTitlebarBttns($(".index-page .titlebar"),{
			iconfont : 'menu',
			menu_items : getMenus()
		});
	});
}

function loadDate(startIndex,isFirst,callback){
	ajax.request({
		url : _base_url+"/web/product/queryCommentList.do",
		need_progressbar : $(".index-page .titlebar"),
		params : {
			page_index:startIndex,
			page_count:page_count,
			product_id : prodId,
			need_oauth:isFirst
		},
		success : function(header,body){
			var list = body.commentList;
			initCommentList(list);
			callback && callback.apply();
		}
	});
}

function initCommentList(commentList){
	var $tempItem = $("#_comment_item_temp");
	var $commentList = $(".index-page .prod-comment-list");
	for(var i=0;i<commentList.length;i++){
		var entity = commentList[i];
		var userEntity = entity.user;
		if(userEntity == null){
			continue;
		}
		var $item = $tempItem.clone().appendTo($commentList);
		$item.attr("user_id",userEntity.id);
		$item.attr("id","comment_"+entity.id);
		$item.find(".name").text(userEntity?userEntity.name:"匿名用户");
		$item.find(".mobile").text(userEntity?userEntity.mobile:"");
		$item.find(".order-time").text(entity.createTime.split(" ")[0]);//只显示到日期
		$item.find(".content").text(entity.comment);
		
		//$item.find(".order-amount").text("购买:2斤");
		
		var $pic = $item.find(".header-pic");
		var pic = (userEntity && userEntity.pic) || logoUrl;
		if(pic){
			$pic.css({
				"background":"url("+pic+") no-repeat center center",
				"background-size":"100% 100%"
			});
		}
		
		initStars($item,entity);
		
		initTags($item,entity);
		
		$item.show();
		
		createSplitline().appendTo($commentList);
	}
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