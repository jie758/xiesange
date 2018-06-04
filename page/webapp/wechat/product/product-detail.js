var productEntity;
var picList;
var picUrlList;
var loading = new PageLoading();
$(function(){
	loading.show();
	initPage();
});

function initPage(){
	var paramObj = common.getUrlParams();
	var prodId = paramObj.product_id;
	ajax.request({
		url : _base_url+"/web/product/queryDetail.do",
		need_progressbar : false,
		params : {
			product_id : prodId,
			need_oauth : true,
			need_wx_signature : true
		},
		success : function(header,body){
			initTitlebarBttns($(".index-page .titlebar"),{
				iconfont : 'menu',
				menu_items : getMenus()
			});
			
			
			productEntity = body.product;
			productEntity.promotionList = body.promotionList;
			picList = body.picList;
			
			picUrlList = [productEntity.pic];
			if(picList != null && picList.length > 0){
				for(var i=0;i<picList.length;i++){
					picUrlList.push(picList[i].pic);
				}
			}
			
			initProd();
			initDetailPics();
			initFootbar();
			
			showGZHQRTip(120);
			
			initWxConfig(body.wxSignature,{
				url : buildTransitionUrl(_base_host+"/wechat/product/product-detail.html?product_id="+productEntity.id),
				img : productEntity.pic,
				timeline_desc : "#快看!有"+productEntity.name+"# 每一天,都是吃海鲜的好日子 !"+productEntity.summary,
				message_title : "#快看!有"+productEntity.name+"# 每一天,都是吃海鲜的好日子",
				message_desc : productEntity.summary
			});
		},
		complete : function(){
			$(".index-page").show();
			loading.hide();
		}
	});
	showCustServiceIcon();
}

function initProd(){
	var $prodarea = $(".prod-item");
	var $pic = $prodarea.find(".prod-pic");
	ProductSet.init($prodarea,productEntity,{pic_preview:false});
	widget.bindTouchClick($pic,function(e){
		xsgwx.viewImages(picUrlList,productEntity.pic);
	});
	
	widget.bindTouchClick($prodarea.find(".comment"),function(e){
		common.gotoPage("comment-list.html?product_id="+productEntity.id);
		e.stopPropagation();
	});
}

function initDetailPics(){
	if(picList == null || picList.length == 0){
		return null;
	}
	var $picList = $(".prod-pic-list");
	for(var i=0;i<picList.length;i++){
		var picEntity = picList[i];
		var $divitem = $("<div class='pic-item'>").appendTo($picList);
		$divitem.data("entity",picEntity);
		var $img = $("<img>").appendTo($divitem);
		$img.attr("src",picEntity.pic).css("width","100%");
		if(!picEntity.text){
			var $text = $("<div>").appendTo($divitem);
			$text.text(picEntity.text);
		}
		
		widget.bindTouchClick($divitem,function(e){
			var $picItem = $(e.currentTarget);
			var picEntity = $picItem.data("entity");
			xsgwx.viewImages(picUrlList,picEntity.pic);
		});
	}
}

function initFootbar(){
	var $footbar = $(".footbar");
	var canEdit = common.getUrlParams("can_order");
	if(canEdit != "" && canEdit == 0){
		$footbar.remove();
		/*$footbar.text('返回').css({
			"text-align":"center"
		}).addClass("common-font");
		
		widget.bindTouchClick($footbar,function(){
			common.gotoPage("../op/1111/preorder.html");
		});*/
		
		/*$footbar.find(".footbar-bttn-left").remove();
		$footbar.find(".footbar-bttn-right").removeClass("right-part");
		
		$footbar.find(".ok").addClass("footbar-bttn-ok").width("auto");*/
		return;
	}
	
	var shoppingCart = getShoppingCart() || {};
	
	$footbar.find(".prod-amount-unit").text("斤");
	if(shoppingCart){
		$footbar.find(".prod-amount-input").val(shoppingCart[productEntity.id]);
	}
	ProductSet.initAmountBttn($footbar.find(".prod-amount-bttn"),function(isAdd,amount,$item,event){
		if(amount < 0){
			amount = 0;
		}
		event.stopPropagation();
		showTotalSum(amount);
	});
	
	widget.bindTouchClick($("body"),function(){
		$(".index-page .totalsum").hide();
	});
	
	widget.bindTouchClick($footbar.find(".cancel"),function(){
		common.gotoPage("../product/product-list.html");
	});
	widget.bindTouchClick($footbar.find(".ok"),function(){
		var $amountInput = $footbar.find(".prod-amount-input");
		if($amountInput.val() == ""){
			message.errorHide("请输入购买数量",$(".index-page .titlebar"));
			return;
		}
		
		var orderMap = getShoppingCart() || {};
		orderMap[productEntity.id] = $amountInput.val();
		addShoppingCart(orderMap);
		
		common.gotoPage("../product/product-list.html");
	});
}

function showTotalSum(amount){
	var $totalSum = $(".index-page .totalsum");
	if($totalSum == null || $totalSum.length == 0){
		$totalSum = $("<div class='totalsum money-font'>").appendTo($(".index-page"));
	}
	var sum = amount*productEntity.price;
	$totalSum.text("￥"+sum.toFixed(2));
	$totalSum.show();
}
