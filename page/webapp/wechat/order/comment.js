var loading = new PageLoading();
var orderEntity ;
var paySum;
$(function(){
	loading.show();
	initPage();
});
function initPage(){
	var orderId = common.getUrlParams("order_id");
	if(!orderId){
		message.alert("请以正确的姿势打开~")
		return;
	}
	
	ajax.request({
		url : _base_url+"/web/order/queryComment.do",
		need_progressbar : false,
		need_oauth:false,
		params : {
			order_id:orderId,
			need_oauth:true
		},
		success : function(header,body){
			//右上角菜单按钮点击
			initTitlebarBttns($(".index-page .titlebar"),{
				iconfont : 'menu',
				menu_items : getMenus()
			});
			orderEntity = body.order;
			paySum = orderEntity.sum + orderEntity.expressFee;
			initIndex();
		},
		complete : function(){
			loading.hide();
			$(".index-page").show();
		}
	});
	
}

function initIndex(){
	var $indexPage = $(".index-page");
	//订单内容
	initOrderItems();
	
	//星星点击
	widget.bindTouchClick($indexPage.find(".prod-starline .star"),function(e){
		var $this = $(e.target);
		var $sliblings = $this.parent().children();
		var isMatched = false;
		for(var i=0;i<$sliblings.length;i++){
			var $curitem = $sliblings.eq(i);
			if(isMatched){
				$curitem.html(iconfontset.star_empty);
				$curitem.removeClass("choosed");
			}else{
				$curitem.html(iconfontset.star);
				$curitem.addClass("choosed");
			}
			if($curitem.get(0) == $this.get(0)){
				isMatched = true;
			}
		}
	});
	
	//标签点击
	widget.bindTouchClick($indexPage.find(".comment-tag-list .tag"),function(e){
		var $this = $(e.target);
		$this.toggleClass("common-bg");
	});
	widget.bindTouchClick($indexPage.find(".footbar-bttn-ok"),function(e){
		message.confirmSlide("确定提交您的评价",function(){
			commitComment();
		});
	});
	
	
	
}



function initOrderItems(){
	var orderItems = orderEntity.items;
	var $listDiv = $(".pay-prodlist");
	if(orderItems == null || orderItems.lenght == 0){
		$listDiv.text("(无明细)");
		return;
	}
	
	var $tempDiv = $("#_payitem_temp");
	var $newDiv = null;
	var itemEntity = null;
	var prodEntity = null;
	var priceNotSure = false;
	for(var i=0;i<orderItems.length;i++){
		if(i > 0){
			var $sepDiv = $("<div>").appendTo($listDiv);
			createSplitline().appendTo($sepDiv);
		}
		itemEntity = orderItems[i];
		prodEntity = itemEntity.product;
		prodEntity.origPrice = prodEntity.origPrice || prodEntity.price;
		prodEntity.price = itemEntity.price;
		var isPriceNotSure = prodEntity.price < 0;
		if(isPriceNotSure){
			priceNotSure = true;
		}
		$newDiv = $tempDiv.clone().appendTo($listDiv);
		$newDiv.attr("id","orderitem_"+itemEntity.id);
		$newDiv.data("item",itemEntity);
		ProductSet.init($newDiv,prodEntity,{size:80});
		
		$newDiv.find(".payitem-amount").text(itemEntity.amount+prodEntity.unit);
		$newDiv.find(".payitem-sum").text("￥"+itemEntity.sum.toFixed(2));
	
		initTags($newDiv,prodEntity);
	}
	return priceNotSure;
}

function initTags($newDiv,prodEntity){
	var tags = prodEntity.commentTags;
	if(tags == null)
		return null;
	var $cmtList = $newDiv.find(".comment-tag-list");
	for(var i=0;i<tags.length;i++){
		var tagStr = tags[i];
		var $tag = $("<span class='tag'>").appendTo($cmtList);
		$tag.text(tagStr);
	}
}

function commitComment(){
	var $titlebar = $(".index-page .titlebar");
	var proditems = $(".index-page .pay-prodlist .prod-item");
	var datas = [];
	for(var i=0;i<proditems.length;i++){
		var $item = proditems.eq(i);
		var itemEntity = $item.data("item");
		var $stars = $item.find(".star.choosed");
		
		var data = {
			order_item_id : itemEntity.id,
			product_id : itemEntity.productId,
			grade : $stars.length,
			comment : $item.find(".comment-content").val()	
		}
		
		var $choosedTags = $item.find(".tag.common-bg");
		var tags = null;
		if($choosedTags != null && $choosedTags.length > 0){
			tags = [];
			for(var k=0;k<$choosedTags.length;k++){
				tags.push($choosedTags.eq(k).text());
			}
			data.tags = tags.join("|")
		}
		
		datas.push(data);
	}
	ajax.request({
		url : _base_url+"/web/order/comment.do",
		need_progressbar : $titlebar,
		params : {
			order_id:orderEntity.id,
			comment_items : JSON.stringify(datas)
		},
		success : function(header,body){
			message.successHide("操作成功，正在跳转...",$titlebar);
			common.gotoPage("../order/order-list.html",1200);
		}
	});
}
