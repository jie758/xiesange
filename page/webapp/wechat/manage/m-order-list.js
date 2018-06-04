var page_count = 15;
var filterStatus = null;
var $indexTitlebar;
var userId = null;
var orderId = null;
var purchaseOrderIds = [];
var menus_filter = [{
	text :'全部',
	//icon : '../../resource/image/index.png',
	handler : function(){
		$(".order-list").empty();
		filterStatus = null;
		loadOrderList(0);
	}
},{
	text :'未支付',
	//icon : '../../resource/image/order.png',
	handler : function(){
		filterStatus = 0;
		loadOrderList(0,0);
	}
},{
	text :'已支付',
	//icon : '../../resource/image/order.png',
	handler : function(){
		filterStatus = 1;
		loadOrderList(0);
	}
},{
	text :'打包中',
	//icon : '../../resource/image/order.png',
	handler : function(){
		filterStatus = 2;
		loadOrderList(0);
	}
},{
	text :'配送中',
	//icon : '../../resource/image/order.png',
	handler : function(){
		filterStatus = 3;
		loadOrderList(0);
	}
},{
	text :'已完成',
	//icon : '../../resource/image/order.png',
	handler : function(){
		filterStatus = 99;
		loadOrderList(0);
	}
}
];

var loading = new PageLoading();
$(function(){
	loading.show();
	userId = common.getUrlParams("user_id");//从用户列表中过来，附带用户id，只查询该用户的订单
	orderId = common.getUrlParams("order_id");
	
	//分页点击
	initPagination($(".next-page"),page_count,function(index){
		loadOrderList(index);
	});
	
	loadOrderList(0,true,function(){
		initPage();
		loading.hide();
		$(".index-page").show();
	});
});
function initPage(){
	$indexTitlebar = $(".titlebar.index");
	
	
	
	//initDeliveryTimeline();
	
	//右上角按钮，过滤+菜单
	initTitlebarBttns($indexTitlebar,[{
		iconfont : 'filter',
		menu_items : menus_filter
	},{
		iconfont : 'menu',
		menu_items : getMenus()
	}]);
	
	//右下角操作按钮
	widget.bindTouchClick($(".footbar-bttn-ok.purchase"),function(){
		/*if(purchaseOrderIds.length == 0){
			message.errorHide("没有可采购的订单",$indexTitlebar);
			return;
		}*/
		
		message.confirmSlide("确定生成采购单?",function(){
			createPurchase();
		});
	});
}


function loadOrderList(startIndex,isFirst,callback){
	if(arguments.length == 0){
		startIndex = $(".next-page").data("page_index") || 0;
	}
	//dates = buildDeliveryDates();
	if(startIndex == 0){
		//从第一条开始，那说明不是翻页加载，需要把原数据清空
		$(".order-list").empty();
		$(".next-page").data("page_index",0);
	}
	ajax.request({
		url : _base_url+"/web/manage/queryOrderList.do",
		need_progressbar : false,
		params : {
			page_index:startIndex,
			page_count:page_count,
			status : filterStatus,
			user_id : userId,
			order_id : orderId,
			need_oauth:isFirst/*,
			need_wx_signature:true*/
		},
		success : function(header,body){
			initOrderList(body.orderList,body.productList,body.promotionList);
			
			$(".footbar-bttn-cancel.sum").text("共"+(body.totalCount||0)+"单");
			
			updatePagination($('.next-page'),body.orderList && body.orderList.length,$(".titlebar"));
			
			/*initWxConfig(body.wxSignature,{
				url : buildTransitionUrl(_base_host+"/wechat/order/order-list.html?fromwx=1"),
				img : logoUrl,
				timeline_desc : "#我的订单# 蟹三哥，地道的东海野生海鲜",
				message_title : "#我的订单#",
				message_desc : "蟹三哥，地道的东海野生海鲜"
			});
*/			
			callback && callback.apply();
		},
		complete : function(){
		}
	});
}

function initOrderList(orderList,prodList,promotionList){
	var $listDiv = $(".order-list");
	var $tempDiv = $("#_order_item_temp");
	if(orderList == null || orderList.length == 0){
		//$("<div>").appendTo($listDiv).text("无数据");
		return;
	}
	
	for(var i=0;i<orderList.length;i++){
		var orderEntity = orderList[i];
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
		
		$newDiv.find(".order-linkman-name").text(orderEntity.name);
		$newDiv.find(".order-linkman-mobile").text(orderEntity.mobile);
		$newDiv.find(".order-createtime").text(orderEntity.createTime);
		$newDiv.find(".order-buyer-addr").text(orderEntity.address);
		
		
		var isPriceNotSure = initContent($newDiv,orderEntity,prodList,promotionList);
		
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
		
		$newDiv.find(".order-deliverybar .deliveryno").text("快递单号:"+(orderEntity.deliveryNo || ""));
		
		var statusObj = refreshStatusInfo($newDiv,orderEntity.status,orderEntity.deliveryDate);
		orderEntity.statusText = statusObj.text;
		if(orderEntity.status == 3 && orderEntity.deliveryNo){
			$newDiv.find(".order-deliverybar").show();
			$newDiv.find(".order-deliverybar .right-part").hide();
			queryDeliveryRoute(orderEntity.deliveryNo,$newDiv);
		}else{
			if(!orderEntity.deliveryNo){
				$newDiv.find(".order-deliverybar .deliveryno").remove();
			}
			$newDiv.find(".order-deliverybar .right-part").remove();
		}
		
		
		var $leftBttn = $newDiv.find(".list-item-bttn.left");
		widget.bindTouchClick($leftBttn,function(e){
			var $clickitem = $(e.target);
			var orderEntity = $clickitem.parents('.order-item').data('entity');
			showStatusPopupmenu(function(newStatus,statusObj){
				ajax.request({
					url : _base_url+"/web/manage/modifyOrder.do",
					need_progressbar : $('.index-page .titlebar'),
					params : {
						order_id : orderEntity.id,
						status : newStatus
					},
					success : function(header,body){
						message.successHide("操作成功",$indexTitlebar);
						loadOrderList(0);
					}
				});
			});
		});
		
		if(statusObj.items){
			var $rightBttn = $newDiv.find(".list-item-bttn.right");
			$rightBttn.show();
			if(statusObj.items.length == 1){
				var handlerItem = handler_menus[statusObj.items[0]].apply(null,[orderEntity,$rightBttn]);
				$rightBttn.text(handlerItem.text);
			}
		}
		
		//顶栏箭头点击事件
		widget.bindTouchClick($newDiv.find(".order-titleline"),function(e){
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
		
		if(orderEntity.bargainCount > 0){
			var $barginDiv = $("<div class='bargain-bar promotion-block'>").appendTo($newDiv);
			initPromotionList($barginDiv,[{type:'3',text:orderEntity.bargainCount+"人参与砍价"}]);
			var $arrow = $("<span class='arrow xsg-fontset gray-font'>"+iconfontset.arrow+"</span>").appendTo($barginDiv.find(".item"));
			widget.bindTouchClick($barginDiv,function(e){
				var $item = $(e.currentTarget).parents(".order-item");
				var order = $item.data("entity");
				common.gotoPage("../welfare/bargain.html?order_id="+order.id);
			});
		}
		
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
	widget.bindTouchClick($(".order-list .list-item-bttn.right"),function(e){
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
		
		widget.popupMenu(null,"bottom top",popupMenus,{need_titlebar:"选择操作",remove_when_close:true});
	});
	
}

function refreshStatusInfo($item,status,deliveryDate){
	var entity = $item.data("entity");
	var statusObj = getStatusInfo(entity,status,deliveryDate);
	var $statusText = $item.find(".order-status");
	var $statusBttn = $item.find(".list-item-bttn.right");
	//状态文本
	$statusText.text(statusObj.text);
	if(statusObj.color){
		$statusText.css("color",statusObj.color);
	}
	$statusBttn.data('statusObj',statusObj);
	
	return statusObj;
}
function getStatusInfo(entity,status){
	var orderStatus = getOrderStatus(entity);
	if(status == -1){
		orderStatus.items = ["modify","remove"];
		return orderStatus;
	}
	
	if(status == 0 || status == 98){
		orderStatus.items = ["modify","remindPay","adjust","remove"];
		return orderStatus;
	}
	
	if(status == 1){
		var isGroup = entity.buyType==2;
		var isPreorder = entity.buyType==4;
		var items = ["modify"];
		if(isGroup){
			items.push("closeGroupbuy");
		}
		if(isPreorder){
			items.push("remindFillPay");
		}
		//items.push("purchase");
		/*if(items.length > 0){
			items.push("-");
		}*/
		
		items.push("remove");
		
		orderStatus.items = items;
		return orderStatus;
	}
	
	if(status == 11){
		orderStatus.items = ["modify","remove"];
		return orderStatus;
	}
	
	if(status == 2){
		orderStatus.items = ["modify","remove"];
		return orderStatus;
	}
	
	if(status == 3){
		orderStatus.items = ["modify","remindComment","remove"];
		return orderStatus;
	}
	
	if(status == 99){
		orderStatus.items = ["modify","remove"];
		return orderStatus;
	}
}

var handler_menus = {
	remindPay : function(orderEntity,$bttn){
		return {
			text : "支付提醒",
			handler : function(){
				message.confirmSlide("确定发送支付提醒?",function(){
					ajax.request({
						url : _base_url+"/web/manage/remindOrderPay.do",
						need_progressbar : $('.titlebar'),
						params : {
							order_id:orderEntity.id
						},
						success : function(header,body){
							message.successHide("操作成功",$(".titlebar"))
						}
					});
				});
			}
		}
	},
	remindFillPay : function(orderEntity,$bttn){
		return {
			text : "余款提醒",
			handler : function(){
				message.confirmSlide("确定发送补余款提醒?",function(){
					ajax.request({
						url : _base_url+"/web/manage/remindOrderPay.do",
						need_progressbar : $('.titlebar'),
						params : {
							order_id:orderEntity.id
						},
						success : function(header,body){
							message.successHide("操作成功",$(".titlebar"))
						}
					});
				});
			}
		}
	},
	remindComment : function(orderEntity,$bttn){
		return {
			text : "评价提醒",
			handler : function(){
				message.confirmSlide("确定发送评价提醒?",function(){
					ajax.request({
						url : _base_url+"/web/manage/remindOrderComment.do",
						need_progressbar : $('.titlebar'),
						params : {
							order_id:orderEntity.id
						},
						success : function(header,body){
							message.successHide("操作成功",$(".titlebar"))
						}
					});
				});
			}
		}
	},
	closeGroupbuy : function(orderEntity,$bttn){
		return {
			text : "立即截单",
			handler : function(){
				var createOrderId = orderEntity.groupbuyId==-1 ? orderEntity.id : orderEntity.groupbuyId;
				message.confirmSlide("确定截单?",function(){
					ajax.request({
						url : _base_url+"/web/manage/closeGroupbuy.do",
						need_progressbar : $indexTitlebar,
						params : {
							order_id:createOrderId
						},
						success : function(header,body){
							message.successHide("操作成功",$(".titlebar"));
							location.reload();
						}
					});
				});
			}
		}
	},
	adjust : function(orderEntity,$bttn){
		return {
			text : "调价",
			handler : function(){
				common.gotoPage("../manage/m-order-modify.html?is_adjust_price=1&order_id="+orderEntity.id);
			}
		}
	},
	modify : function(orderEntity,$bttn){
		return {
			text : "订单修改",
			handler : function(){
				common.gotoPage("../manage/m-order-modify.html?order_id="+orderEntity.id);
			}
		}
	},
	remove : function(orderEntity,$bttn){
		return {
			text : "删除",
			style:{
				color:"#FF0000"
			},
			handler : function(){
				message.confirmSlide("确定删除\""+orderEntity.name+"\"的订单?",function(){
					removeOrder(orderEntity,$bttn);
				});
			}
		}
	},
	purchase : function(orderEntity,$bttn){
		return {
			text : "加入采购单",
			handler : function(){
				for(var i=0;i<purchaseOrderIds.length;i++){
					if(purchaseOrderIds[i] == orderEntity.id){
						return;
					}
				}
				var $item = $bttn.parents(".order-item");
				$item.find(".order-status").html("已支付 - <span>待采购</span>");
				purchaseOrderIds.push(orderEntity.id);
			}
		}
	}
}

//修改产品资料，包括调价
function showModifyOrderPage(entity,isAdjustPrice){
	var $modifypage = $(".order-modify-page");
	var $sendNotifyTd = $modifypage.find(".item-input-value.sendNotify");
	$modifypage.data("is_adjust_price",isAdjustPrice || false);
	$modifypage.data("entity",entity);
	if($modifypage.attr("is_init") != 1){
		//初始化修改界面
		var $modifypageTitle = $modifypage.find(".titlebar");
		//取消按钮
		widget.bindTouchClick($modifypage.find(".footbar-bttn-cancel"),function(e){
			$modifypage.hide();
			e.stopPropagation();
		});
		//确定修改按钮
		widget.bindTouchClick($modifypage.find(".footbar-bttn-ok"),function(e){
			message.confirmSlide("确定修改订单?",function(){
				commitModifyOrder($modifypage.data("is_adjust_price"));
			});
			
			e.stopPropagation();
		});
		//右上角菜单关闭按钮
		initTitlebarBttns($modifypageTitle,{
			iconfont : 'close',
			handler:function(){
				$modifypage.hide();
			}
		});
		
		//状态点击事件
		widget.bindTouchClick($modifypage.find(".item-input-value.status"),function(e){
			var $clickitem = $(e.currentTarget);
			var orderEntity = $modifypage.data("entity");
			showStatusPopupmenu(function(newStatus,statusObj){
				entity.status = newStatus;
				entity.statusText = statusObj.text;
				//$clickitem.text(getStatusInfo(entity,newStatus).text);
				//$clickitem.data("newValue",newStatus);
				var $statusItem = $clickitem.find(".status");
				showEntityValue(entity,$statusItem);
				$statusItem.data("newValue",newStatus);
				//updateStatus($clickitem,entity,status);
			});
			e.stopPropagation();
		});
		
		//初始化发送修改提醒checkbox框
		CheckboxSet.init($sendNotifyTd,{
			text : "是否需要发送提醒",
			click_area : $sendNotifyTd
		});
		$modifypage.attr("is_init",1);
	}
	//CheckboxSet.select($sendNotifyTd.find(".checkbox-set"),false);
	
	
	showEntityValue(entity,$modifypage.find("[entityKey]"));
	//$modifypage.find(".item-input-value.status").text(entity.statusText);
	if(isAdjustPrice){
		$modifypage.find(".name").prop("readOnly",true).addClass("gray-bg").parent().addClass("gray-bg");
		$modifypage.find(".mobile").prop("readOnly",true).addClass("gray-bg").parent().addClass("gray-bg");
		$modifypage.find(".address").prop("readOnly",true).addClass("gray-bg").parent().addClass("gray-bg");
	}else{
		$modifypage.find(".name").prop("readOnly",false).removeClass("gray-bg").parent().removeClass("gray-bg");
		$modifypage.find(".mobile").prop("readOnly",false).removeClass("gray-bg").parent().removeClass("gray-bg");
		$modifypage.find(".address").prop("readOnly",false).removeClass("gray-bg").parent().removeClass("gray-bg");
	}
	widget.slide($modifypage,"left right",400);
}



function initContent($newItemDiv,orderEntity,prodList,promotionList){
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
		
		prodEntity = common.matchItem(itemEntity.productId,prodList,'id') || {};
		var name = prodEntity ? prodEntity.name : "[未知产品]";
		if(prodEntity && prodEntity.spec){
			name = name+"("+prodEntity.spec+")";
		}
		
		var isPriceNotSure = itemEntity.price < 0;
		if(isPriceNotSure){
			priceNotSure = true;
		}
		
		
		$newDiv.find(".order-item-prodname").text(name+"：");
		$newDiv.find(".order-item-amount").text(itemEntity.amount);
		
		var $price = $newDiv.find(".order-item-prodprice");
		$price.text(isPriceNotSure?"价格待定":"￥"+itemEntity.price+"/"+prodEntity.unit);
		isPriceNotSure ? $price.addClass("price-not-sure"):$price.removeClass("price-not-sure"); 
		
		var $sum = $newDiv.find(".order-item-sum");
		$sum.text(isPriceNotSure?"价格待定":"￥"+itemEntity.sum.toFixed(2));
		isPriceNotSure && $sum.addClass("money-font");
		
		
		$newDiv.show();
	}
	
	return priceNotSure;
}

function commitModifyOrder(isAdjustPrice){
	var $modifypage = $(".order-modify-page");
	var order = $modifypage.data("entity");
	var entity = buildEntityValue($modifypage.find("[entityKey]"));
	entity.order_id = order.id;
	entity.is_adust_price = isAdjustPrice?1:0;
	ajax.request({
		url : _base_url+"/web/manage/modifyOrder.do",
		need_progressbar : $modifypage.find('.titlebar'),
		params : entity,
		success : function(header,body){
			message.successHide("操作成功",$indexTitlebar);
			$modifypage.hide();
			loadOrderList(0);
		}
	});
}
function getDelayOrderIds(){
	var $delayBttns = $(".list-item-bttn.right.delay");
	var orderIds = [];
	for(var i=0;i<$delayBttns.length;i++){
		var $bttn = $delayBttns.eq(i);
		var orderEntity = $bttn.parents('.order-item').data('entity');
		orderIds.push(orderEntity.id);
	}
	return orderIds;
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
	$newDiv.find(".name").text(route.updateByName+"("+route.mobile+")");
	if(needmoblie){
		$newDiv.find(".dial").attr("href","tel:"+speeda_phone);
	}else{
		$newDiv.find(".dial").remove();
	}
	$newDiv.show();
	return $newDiv;
}

function removeOrder(orderEntity,$bttn){
	ajax.request({
		url : _base_url+"/web/manage/removeOrder.do",
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

function createPurchase(){
	var $indexTitlebar = $(".index-page .titlebar");
	ajax.request({
		url : _base_url+"/web/purchase/create.do",
		need_progressbar : $indexTitlebar,
		params : {
		},
		success : function(header,body){
			message.successHide("操作成功",$indexTitlebar);
			common.gotoPage("../manage/m-purchase-list.html",1200);
		}
	});
}

/*function updateStatus($clickitem,entity,newStatus){
	entity.status = newStatus;
	$clickitem.text(getStatusInfo(entity,newStatus).text);
	$clickitem.data("newValue",newStatus);
}*/

function showStatusPopupmenu(callback){
	var statusList = [-1,0,1,2,3,99];
	var menuitems = [];
	for(var i=0;i<statusList.length;i++){
		var statusObj = getOrderStatus({status:statusList[i]});
		!function(status,statusObj){
			menuitems.push({
				text : statusObj.text,
				handler:function(){
					callback && callback.apply(null,[status,statusObj]);
				}
			});
		}(statusList[i],statusObj);
		
	}
	
	widget.popupMenu(null,"bottom top",menuitems,{need_titlebar:"选择状态",remove_when_close:true});
}