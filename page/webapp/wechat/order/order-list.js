var loading = new PageLoading();
$(function(){
	loading.show();
	initPage();
});
function initPage(){
	ajax.request({
		url : _base_url+"/web/order/queryList.do",
		need_progressbar : false,
		params : {
			page_index:0,
			page_count:20,
			need_oauth:true
		},
		success : function(header,body){
			//右上角菜单按钮点击
			initTitlebarBttns($(".titlebar"),[{
				iconfont : 'menu',
				menu_items : getMenus()
			}]);
			
			initIndex(body.orderList,body.productList,body.promotionList);
			
			$(".order-list").show();
			$(".vcode-set").hide();
		},
		complete : function(){
			loading.hide();
			$(".index-page").show();
		}
	});
}

function initIndex(orderList,prodList,promotionList){
	var $listDiv = $(".order-list");
	var $tempDiv = $("#_order_item_temp");
	if(orderList != null && orderList.length > 0){
		for(var i=0;i<orderList.length;i++){
			var orderEntity = orderList[i];
			var isGroupbuy = false;//orderEntity.buyType == 2;
			var $newDiv = $tempDiv.clone().appendTo($listDiv);
			$newDiv.data("entity",orderEntity);
			$newDiv.attr("id",orderEntity.id);
			
			if(isGroupbuy){
				var promotion = promotionList && common.matchItem(orderEntity.promotionId,promotionList,'id');
				$newDiv.find(".groupbuy-info").show();
				$newDiv.find(".groupbuy-info .promotion-item .is-creator").text(orderEntity.groupbuyId==-1?"[发起]":"[参团]");
				$newDiv.find(".groupbuy-info .promotion-item .text").text(buildPromotionText(promotion));
				$newDiv.find(".ordersum").hide();
			}else{
				$newDiv.find(".groupbuy-info").remove();
			}
			
			$newDiv.find(".order-code").text("订单编号:"+orderEntity.id);
			
			$newDiv.find(".order-linkman-name").text(orderEntity.name);
			$newDiv.find(".order-linkman-mobile").text(orderEntity.mobile);
			$newDiv.find(".order-createtime").text(orderEntity.createTime);
			$newDiv.find(".order-buyer-addr").text(orderEntity.address);
			
			initContent($newDiv,orderEntity,prodList);
			var $orderSum = $newDiv.find(".order-sum");
			var $origSum = $newDiv.find(".order-origsum");
			var $orderPaySum = $newDiv.find(".order-paysum");
			
			var sum = orderEntity.sum;
			var origSum = orderEntity.origSum;
			var expressfee = orderEntity.expressSum;
			$orderSum.text("￥"+orderEntity.sum.toFixed(2));
			if(sum != origSum){
				$origSum.text("￥"+origSum.toFixed(2));
			}
			$orderPaySum.text("￥"+(sum+expressfee).toFixed(2));
			
			if(orderEntity.coupon){
				$newDiv.find(".order-coupon").text("("+buildCouponText(orderEntity.coupon)+")");
			}
			$newDiv.find(".order-expressfee").text("运费:"+orderEntity.expressSum.toFixed(2));
			$newDiv.find(".order-deliverybar .deliveryno").text("快递单号:"+orderEntity.deliveryNo);
			
			
			var statusObj = refreshStatusInfo($newDiv,orderEntity.status,orderEntity.deliveryDate);
			
			if(orderEntity.status == 3 && orderEntity.deliveryNo){
				$newDiv.find(".order-deliverybar").show();
				$newDiv.find(".order-deliverybar .right-part").hide();
				queryDeliveryRoute(orderEntity.deliveryNo,$newDiv);
			}else{
				$newDiv.find(".order-deliverybar").remove();
			}
			
			//console.log(statusObj.items.length);
			if(statusObj.items && statusObj.items.length > 0){
				var $rightBttn = $newDiv.find(".list-item-bttn.right");
				$rightBttn.text("操作...");
				$rightBttn.show();
				if(statusObj.items.length == 1){
					var handlerItem = handler_menus[statusObj.items[0]].apply(null,[orderEntity,$rightBttn]);
					$rightBttn.text(handlerItem.text);
				}
			}
			
			$newDiv.show();
		}
	}else{
		$("<div>").appendTo($listDiv).text("无数据");	
	}
	
	//顶栏箭头点击事件
	widget.bindTouchClick($(".order-list .order-titleline"),function(e){
		var $bttn = $(e.target);
		if(!enableBttn($bttn)){
			return;
		}
		var orderEntity = $bttn.parents('.order-item').data('entity');
		
		if(orderEntity.buyType==2){
			var createOrderId = orderEntity.groupbuyId==-1 ? orderEntity.id : orderEntity.groupbuyId;
			common.gotoPage("../order/pay.html?order_id="+orderEntity.id);
		}else{
			common.gotoPage("../order/pay.html?order_id="+orderEntity.id);
		}
	});
	
	//右部按钮点击事件
	widget.bindTouchClick($(".order-list .list-item-bttn.right"),function(e){
		var $bttn = $(e.target);
		if(!enableBttn($bttn)){
			return;
		}
		var orderEntity = $bttn.parents('.order-item').data('entity');
		
		var statusObj = $bttn.data("statusObj");
		var menuItems = statusObj.items;
		if(menuItems == null || menuItems.length == 0){
			return;
		}
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
		
		widget.popupMenu(null,"bottom top",popupMenus,{need_titlebar:"选择操作",remove_when_close:true});
	});
}

function refreshStatusInfo($item,status,deliveryDate){
	var entity = $item.data("entity");
	var statusObj = getStatus(entity,status,deliveryDate);
	var $statusText = $item.find(".order-status");
	var $statusBttn = $item.find(".list-item-bttn.right");
	//状态文本
	$statusText.text(statusObj.text);
	if(statusObj.color){
		$statusText.css("color",statusObj.color);
	}
	
	$statusBttn.data('statusObj',statusObj);
	return statusObj;
	
	/*//按钮
	if(statusObj.bttn){
		$statusBttn.text(statusObj.bttn);
	}else{
		$statusBttn.addClass("disable-bttn").hide();
	}
	//按钮事件
	if(statusObj.handler){
		$statusBttn.data('handler',statusObj.handler);
	}else{
		$statusBttn.data('handler',null);
	}*/
}

function getStatus(entity,status){
	var orderStatus = getOrderStatus(entity);
	if(status == -1){
		orderStatus.items = ["remove"];
		return orderStatus;
	}else if(status == 0 || status == 98){
		orderStatus.items = ["pay","remove"];
		return orderStatus;
	}else if(status == 1){
		var isGroup = entity.buyType==2;
		var isPreorder = entity.buyType==4;
		var isCreator = entity.groupbuyId == -1;
		var items = [];
		if(isGroup && isCreator){
			items.push("closeGroupbuy");
		}
		
		orderStatus.items = items;
		return orderStatus;
		
		
		/*return {
			text : isGroup ? '拼单中':isPreorder ? '已支付(预订)':'已支付',
			items : items,
			handler : function($bttn,orderEntity){
				if(orderEntity.buyType==2){
					var createOrderId = orderEntity.groupbuyId==-1 ? orderEntity.id : orderEntity.groupbuyId;
					common.gotoPage("../order/pay-groupbuy.html?order_id="+createOrderId);
				}else{
					common.gotoPage("../product/product-list.html?order_id="+orderEntity.id);
				}
			}
		}*/
	}else if(status == 11){
		//var isGroup = entity.buyType==2;
		//var isCreator = entity.groupbuyId == -1;
		
		orderStatus.items = ["notifyArrival","comment"];
		return orderStatus;
		
		/*return {
			text : '已截单',
			items : ["notifyArrival","comment"]
		}*/
	}else if(status == 2){
		orderStatus.items = ["comment"];
		return orderStatus;
		/*return {
			text : '打包中',
			items : ["comment"]
		}*/
	}else if(status == 3){
		orderStatus.items = ["comment"];
		return orderStatus;
		
		/*return {
			text : '配送中',
			items : ["comment"]
		}*/
	}else if(status == 99){
		orderStatus.items = ["onemore","remove"];
		return orderStatus;
		
		/*return {
			text : '已完成',
			color : '#b3b3b3',
			items : ["onemore","sep","remove"]
		}*/
	}
}

function initContent($newDiv,orderEntity,prodList){
	var isGroupbuy = orderEntity.buyType == 2;
	var $tempDiv = $("#_order_content_item_temp");
	var $contentList = $newDiv.find(".order-content");
	
	if(orderEntity.items.length == 0){
		var $newDiv = $tempDiv.clone().appendTo($contentList);
		$newDiv.attr("id","");
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
		$newDiv.attr("id","orderitem_"+itemEntity.id);
		
		prodEntity = common.matchItem(itemEntity.productId,prodList,'id');
		if(prodEntity == null){
			$newDiv.find(".order-item-prodname").text("产品已下架");
			continue;
		}
		var name = prodEntity.name;
		if(prodEntity.spec){
			name = name+"("+prodEntity.spec+")";
		}
		
		
		$newDiv.find(".order-item-prodname").text(name+"：");
		$newDiv.find(".order-item-amount").text(itemEntity.amount);
		
		var $price = $newDiv.find(".order-item-prodprice");
		$price.text("￥"+itemEntity.price+"/"+prodEntity.unit);
		
		$newDiv.find(".order-item-sum").text("￥"+itemEntity.sum.toFixed(2));
		
		//$newDiv.find(".order-item-sum").text("￥"+itemEntity.sum.toFixed(2));
		$newDiv.show();
	}
	return priceNotSure;
}

function queryDeliveryRoute(deliveryNo,$itemDiv){
	return;
	var time = new Date().getTime();
	var param = "&timestamp="+parseInt(time/1000)+"&number_id="+deliveryNo;
	$.ajax({
		dataType : "json",
		async : true,
		type : 'GET',
		url : speeda_url+param,
		//data : params,
		error : function(xmlHttpRequest, textStatus, errorThrown) {
		},
		success : function(responseData, textStatus, jqXHR) {
			$itemDiv.find(".order-deliverybar .loading").remove();
			var $showall = $itemDiv.find(".order-deliverybar .showall");
			$showall.text("查看全部");
			$showall.css("text-decoration","underline");
			$showall.attr("is_loading",0);
			$showall.removeClass("gray-font");
			var rows = responseData.rows;
			if(rows == null || rows.length == 0)
				return;
			var routeList = rows[0].routes;
			var $routeListDiv = $itemDiv.find(".routelist");
			$routeListDiv.data('routeList',routeList);
			var $newRouteDiv = insertRoute(routeList[routeList.length-1],true,$routeListDiv);
			$newRouteDiv.addClass("highlight-font");
			$newRouteDiv.find(".flag").addClass("latest");
			
			widget.bindTouchClick($showall,function(e){
				var $this = $(e.target);
				if($this.attr("is_expand") == 1){
					//已经展开了，点击缩合
					$routeListDiv.find(".route.gray-font").hide();
					$this.attr("is_expand",0);
					return;
				}else if($this.attr("is_expand") == 0){
					//已经缩合了，点击展开
					$routeListDiv.find(".route.gray-font").show();
					$this.attr("is_expand",1);
				}else{
					//第一次则新初始化
					for(var i=routeList.length-2;i>0;i--){
						var $routeDiv = insertRoute(routeList[i],false,$routeListDiv);
						$routeDiv.addClass("gray-font");
						//$routeDiv.find(".showall").remove();
					}
				}
				$this.attr("is_expand",1);
			});
		},
		complete : function(data, textStatus) {
		}
	});
}


function insertRoute(route,needmoblie,$listDiv){
	var $newDiv = $("#_delivery_route_temp").clone().appendTo($listDiv);
	//var $deliveryDiv = $itemDiv.find(".purchase-delivery");
	$newDiv.attr("id","");
	$newDiv.find(".time").text(route.updateAt);
	$newDiv.find(".node").text(route.actNode);
	$newDiv.find(".state").text(route.state);
	$newDiv.find(".name").text(route.updateByName);
	if(needmoblie){
		$newDiv.find(".dial").attr("href","tel:"+speeda_phone);
	}else{
		$newDiv.find(".dial").remove();
	}
	$newDiv.show();
	return $newDiv;
}

var handler_menus = {
	pay : function(orderEntity,$bttn){
		return {
			text : "立即支付",
			handler : function(){
				if(orderEntity.buyType == 2){
					//团购
					var createOrderId = orderEntity.groupbuyId==-1 ? orderEntity.id : orderEntity.groupbuyId;
					common.gotoPage("../order/pay-groupbuy.html?order_id="+createOrderId);
				}else{
					common.gotoPage("../order/pay.html?order_id="+orderEntity.id);
				}
			}
		}
	},
	onemore : function(orderEntity,$bttn){
		return {
			text : "再来一单",
			handler : function(){
				common.gotoPage("../product/product-list.html?order_id="+orderEntity.id);
			}
		}
	},
	notifyArrival : function(orderEntity,$bttn){
		return {
			text : "到货通知",
			handler : function(){
				message.confirm("请务必确认您的货品已经收到。确定向所有组团人员发送到货通知?",function(){
					sendArrivalNotify(orderEntity.id);
				});
			}
		}
	},
	closeGroupbuy : function(orderEntity,$bttn){
		return {
			text : "立即截单",
			handler : function(){
				message.confirm("截单后本次拼单将会结束并安排发货，拼单中未支付的小伙伴们将会被视为放弃。<BR/>确定截单？",function(){
					commitCloseGroupbuy(orderEntity.id);
				});
			}
		}
	},
	comment : function(orderEntity,$bttn){
		return {
			text : "收货评价",
			handler : function(){
				common.gotoPage("../order/comment.html?order_id="+orderEntity.id);
			}
		}
	},
	remove : function(orderEntity,$bttn){
		return {
			text : "删除订单",
			style:{
				color:"#FF0000"
			},
			handler : function(){
				message.confirmSlide("确定删除\""+orderEntity.code+"\"订单?",function(){
					removeOrder(orderEntity,$bttn);
				});
			}
		}
	}
}

function removeOrder(orderEntity,$bttn){
	ajax.request({
		url : _base_url+"/web/order/remove.do",
		need_progressbar : $('.titlebar'),
		params : {
			order_id:orderEntity.id
		},
		success : function(header,body){
			message.successHide("操作成功",$(".titlebar"));
			var $orderitem = $bttn.parents(".order-item");
			$orderitem.remove();
		}
	});
}

function sendArrivalNotify(orderId){
	var $titlebar = $(".titlebar");
	ajax.request({
		url : _base_url+"/web/groupbuy/sendArrivalNotify.do",
		params : {
			order_id : orderId
		},
		need_progressbar : $titlebar,
		success : function(header,body){
			message.successHide("操作成功",$titlebar);
		}
	});
}

function commitCloseGroupbuy(orderId){
	var $titlebar = $(".titlebar");
	ajax.request({
		url : _base_url+"/web/groupbuy/close.do",
		params : {
			order_id : orderId
		},
		need_progressbar : $titlebar,
		success : function(header,body){
			message.successHide("操作成功...",$titlebar);
		}
	});
}