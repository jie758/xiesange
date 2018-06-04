var $indexTitlebar;
var purchaseId;
var purchase;
var purchaseItems;
var loading = new PageLoading();
$(function(){
	purchaseId = common.getUrlParams("purchase_id");
	loading.show();
	loadData(function(){
		initPage();
		loading.hide();
		$(".index-page").show();
	});
});
function initPage(){
	$indexTitlebar = $(".titlebar.index");
	
	//右上角按钮，过滤+菜单
	initTitlebarBttns($indexTitlebar,[{
		iconfont : 'menu',
		menu_items : getMenus()
	}]);
	
	$(".purchase-code .code").text("采购单编号："+purchase.id);
	$(".express-fee input").val(purchase.expressFee);
	initStat();
	initOrderList();
	
	//右下角操作按钮
	widget.bindTouchClick($(".footbar-bttn-ok.purchase"),function(){
		message.confirmSlide("确定提交?",function(){
			commitPurchasePrice();
		});
	});
}


function loadData(callback){
	ajax.request({
		url : _base_url+"/web/purchase/queryDetail.do",
		need_progressbar : false,
		params : {
			need_oauth:true,
			purchase_id:purchaseId
			
		},
		success : function(header,body){
			purchase = body.purchase;
			purchaseItems = body.purchaseItemList;
			
			callback && callback.apply();
		},
		complete : function(){
		}
	});
}

/**
 * 初始化统计栏
 * @param purchase
 * @param purchaseItemList
 */
function initStat(){
	if(purchaseItems == null || purchaseItems.length == 0){
		return;
	}
	var $statDiv = $(".order-statline");
	$statDiv.find(".totalcount").text("共"+purchaseItems.length+"笔订单")
	
	var priceMap = {};
	if(purchase.prices){
		var priceArr = purchase.prices.split("|");
		for(var i=0;i<priceArr.length;i++){
			var priceitems = priceArr[i].split(":");
			priceMap[priceitems[0]] = priceitems[1];
		}
	}
	var stat = {};
	var prodArr = [];
	for(var i=0;i<purchaseItems.length;i++){
		var pitem = purchaseItems[i];
		var order = pitem.order;
		var oitems = order.items;
		for(var k=0;k<oitems.length;k++){
			var oitem = oitems[k];
			var prod = oitem.product;
			var totalAmount = stat[prod.id] || 0;
			if(stat[prod.id] === undefined){
				prodArr.push(prod);
			}
			stat[prod.id] = totalAmount+oitem.amount;
		}
	}
	var $prodlist= $statDiv.find(".prodlist");
	var $tempDiv = $("#_stat_temp");
	
	for(var i=0;i<prodArr.length;i++){
		var prod = prodArr[i];
		var totalAmount = stat[prod.id];
		
		
		var $statitem = $tempDiv.clone().appendTo($prodlist);;
		var entity = $.extend({amount:totalAmount},prod);
		$statitem.data("entity",entity);
		
		ProductSet.init($statitem,entity,{size:40});
		if(priceMap){
			$statitem.find(".costprice-input").val(priceMap[prod.id]);
		}
	}
	
	$prodlist.find(".costprice-input").blur(function(e){
		var $input = $(e.target);
		var price = $input.val() || 0;
		var $statitem = $input.parents(".prod-stat-item");
		var entity = $statitem.data("entity");
		$statitem.find(".prod-totalsum").text("￥"+entity.amount*parseFloat(price));
		refreshTotalCostSum();
	});
	
	$statDiv.find(".express-fee input").blur(function(e){
		refreshTotalCostSum();
	});
	if(priceMap){
		refreshTotalCostSum();
	}
}

function refreshTotalCostSum(){
	var $inputs = $(".order-statline").find(".costprice-input");
	var totalSum = 0;
	for(var i=0;i<$inputs.length;i++){
		var $input = $inputs.eq(i);
		var $statitem = $input.parents(".prod-stat-item");
		var entity = $statitem.data("entity");
		totalSum +=  entity.amount*parseFloat($input.val() || 0);
	}
	
	var expresfee = $(".order-statline").find(".express-fee input").val();
	if(expresfee){
		totalSum += parseFloat(expresfee);
	}
	$(".order-statline").find(".totalsum").text("采购总价：￥"+totalSum);
}

function initOrderList(){
	var $listDiv = $(".order-list");
	var $tempDiv = $("#_order_item_temp");
	if(purchaseItems == null || purchaseItems.length == 0){
		return;
	}
	
	for(var i=0;i<purchaseItems.length;i++){
		var pitem = purchaseItems[i];
		var orderEntity = pitem.order;
		var isGroupbuy = orderEntity.buyType == 2;
		var $newDiv = $tempDiv.clone().appendTo($listDiv);
		$newDiv.data("entity",orderEntity);
		$newDiv.attr("id",orderEntity.id);
		
		if(isGroupbuy){
			var promotion = promotionList && common.matchItem(orderEntity.promotionId,promotionList,'id');
			if(promotion){
				$newDiv.find(".groupbuy-info").show();
				$newDiv.find(".groupbuy-info .promotion-item .content").text(buildPromotionText(promotion));
			}
		}else{
			$newDiv.find(".groupbuy-info").remove();
		}
		if(orderEntity.deliveryDate){
			$newDiv.find(".order-deliveryday").text(orderEntity.deliveryDate+"发货");
		}
		$newDiv.find(".order-code").text("订单编号:"+orderEntity.id);
		$newDiv.find(".order-index").text("序号"+(i+1));
		
		$newDiv.find(".order-linkman-name").text(orderEntity.name);
		$newDiv.find(".order-linkman-mobile").text(orderEntity.mobile);
		$newDiv.find(".order-createtime").text(orderEntity.createTime);
		$newDiv.find(".order-buyer-addr").text(orderEntity.address);
		
		
		var isPriceNotSure = initContent($newDiv,orderEntity);
		
		var $orderSum = $newDiv.find(".order-sum");
		var $origSum = $newDiv.find(".order-origsum");
		var $orderPaySum = $newDiv.find(".order-paysum");
		
		var sum = orderEntity.sum;
		var origSum = orderEntity.origSum;
		var expressfee = orderEntity.expressFee;
		
		$orderSum.text("￥"+orderEntity.sum.toFixed(2));
		if(sum != origSum){
			$origSum.text("￥"+origSum.toFixed(2));
		}
		$orderPaySum.text("￥"+(sum+expressfee).toFixed(2));
		
		if(orderEntity.coupon){
			$newDiv.find(".order-coupon").text("("+buildCouponText(orderEntity.coupon)+")");
		}
		$newDiv.find(".order-expressfee").text("运费:"+orderEntity.expressFee.toFixed(2));
		
		$newDiv.find(".order-deliverybar .deliveryno").text("快递单号:"+(orderEntity.deliveryNo || ""));
		
		/*var statusObj = refreshStatusInfo($newDiv,orderEntity.status,orderEntity.deliveryDate);
		orderEntity.statusText = statusObj.text;
		if(orderEntity.status == 3 && orderEntity.deliveryNo){
			$newDiv.find(".order-deliverybar").show();
			//queryDeliveryRoute(orderEntity.deliveryNo,$newDiv);
		}else{
			if(!orderEntity.deliveryNo){
				$newDiv.find(".order-deliverybar .deliveryno").remove();
			}
			$newDiv.find(".order-deliverybar .right-part").remove();
		}
		
		
		if(statusObj.items){
			var $rightBttn = $newDiv.find(".list-item-bttn.right");
			$rightBttn.show();
			if(statusObj.items.length == 1){
				var handlerItem = handler_menus[statusObj.items[0]].apply(null,[orderEntity,$rightBttn]);
				$rightBttn.text(handlerItem.text);
			}
		}*/
		
		//顶栏箭头点击事件
		/*widget.bindTouchClick($newDiv.find(".order-titleline"),function(e){
			var $bttn = $(e.target);
			if(!enableBttn($bttn)){
				return;
			}
			var orderEntity = $bttn.parents('.order-item').data('entity');
			
			if(orderEntity.buyType==2){
				var createOrderId = orderEntity.groupbuyId==-1 ? orderEntity.id : orderEntity.groupbuyId;
				common.gotoPage("../order/pay-groupbuy.html?order_id="+createOrderId);
			}else{
				common.gotoPage("../order/pay.html?order_id="+orderEntity.id);
			}
		});*/
		
		/*if(orderEntity.bargainCount > 0){
			var $barginDiv = $("<div class='bargain-bar promotion-block'>").appendTo($newDiv);
			initPromotionList($barginDiv,[{type:'3',text:orderEntity.bargainCount+"人参与砍价"}]);
			var $arrow = $("<span class='arrow xsg-fontset gray-font'>"+fontset.arrow+"</span>").appendTo($barginDiv.find(".item"));
			widget.bindTouchClick($barginDiv,function(e){
				var $item = $(e.currentTarget).parents(".order-item");
				var order = $item.data("entity");
				common.gotoPage("../welfare/bargain.html?order_id="+order.id);
			});
		}*/
		
		$newDiv.show();
	}
	
	
	
	/*widget.bindTouchClick($(".order-list .promotion-item"),function(e){
		var $this = $(e.target);
		var $item = $this.parents('.order-item');
		var entity = $item.data("entity");
		var createOrderId = entity.groupbuyId==-1 ? entity.id : entity.groupbuyId;
		common.gotoPage('../order/pay-groupbuy.html?order_id='+createOrderId);
	});*/
	
	//右部按钮点击事件
	/*widget.bindTouchClick($(".order-list .list-item-bttn.right"),function(e){
		var $bttn = $(e.target);
		if(!enableBttn($bttn)){
			return;
		}
		var statusObj = $bttn.data("statusObj");
		var menuItems = statusObj.items;
		if(menuItems == null || menuItems.length == 0){
			return;
		}
		var orderEntity = $bttn.parents('.order-item').data('entity');
		
		if(menuItems.length == 1){
			var handlerItem = handler_menus[menuItems[0]].apply(null,[orderEntity,$bttn]);
			handlerItem.handler && handlerItem.handler.apply(null,[orderEntity,$bttn]);
			return;
		}
		
		var popupMenus = [];
		for(var i=0;i<menuItems.length;i++){
			if(menuItems[i] == "-"){
				popupMenus.push("-");
				continue;
			}
			var popupItem = handler_menus[menuItems[i]].apply(null,[orderEntity,$bttn]);
			popupMenus.push(popupItem);
		}
		
		widget.popupMenu(null,"bottom top",popupMenus,{need_titlebar:false,remove_when_close:true});
	});*/
	
}


function initContent($newItemDiv,orderEntity){
	var isGroupbuy = orderEntity.buyType == 2;
	var $tempDiv = $("#_order_content_item_temp");
	var $contentList = $newItemDiv.find(".order-content");
	
	if(orderEntity.items == null || orderEntity.items.length == 0){
		var $newDiv = $tempDiv.clone().appendTo($contentList);
		$newDiv.attr("id",'_blank');
		$newDiv.show();
		$newDiv.text("(无订单明细)");
		return;
	}
	
	var $newDiv = null;
	var itemEntity = null;
	var prodEntity = null;
	var priceNotSure = false;
	for(var i=0;i<orderEntity.items.length;i++){
		itemEntity = orderEntity.items[i];
		
		$newDiv = $tempDiv.clone().appendTo($contentList);
		$newDiv.attr("id",'orderitem_'+itemEntity.id);
		if(isGroupbuy){
			$newDiv.find(".ordersum").hide();
		}
		prodEntity = itemEntity.product;
		prodEntity.price = itemEntity.price;
		prodEntity.sum = itemEntity.sum;
		prodEntity.amount = itemEntity.amount;
		
		ProductSet.init($newDiv,prodEntity,{size:40});
		$newDiv.show();
	}
	
	return priceNotSure;
}

function commitPurchasePrice(){
	var $inputs = $(".order-statline").find(".costprice-input");
	var priceArr = [];
	for(var i=0;i<$inputs.length;i++){
		var $input = $inputs.eq(i);
		var $statitem = $input.parents(".prod-stat-item");
		var entity = $statitem.data("entity");
		if($input.val().length == 0){
			message.errorHide("请输入"+entity.name+"的采购价格",$(".index-page .titlebar"));
			return;
		}
		priceArr.push(entity.id+":"+$input.val());
	}
	ajax.request({
		url : _base_url+"/web/purchase/commitPurchasePrice.do",
		need_progressbar : $indexTitlebar,
		params : {
			purchase_id : purchaseId,
			express_fee : $(".order-statline .express-fee input").val(),
			purchase_price : priceArr.join("|")
		},
		success : function(header,body){
			message.successHide("操作成功",$indexTitlebar);
			//common.gotoPage("../manage/m-purchase.html",1200);
		}
	});
}

