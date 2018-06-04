var loading = new PageLoading();
$(function(){
	loading.show();
	loadPurchaseList(0,true,function(){
		initPage();
		loading.hide();
		$(".index-page").show();
	});
	
});
function initPage(){
	var $page = $(".index-page");
	var $titlebar = $page.find('.titlebar');
	//右上角菜单按钮点击
	initTitlebarBttns($titlebar,[{
		iconfont : 'menu',
		menu_items : getMenus()
	}]);
	
	/*//分页点击
	initPagination($(".index-page .next-page"),page_count,function(index){
		loadOrderList(index);
	});*/
}

function loadPurchaseList(startIndex,isFirst,callback){
	if(startIndex == 0){
		//从第一条开始，那说明不是翻页加载，需要把原数据清空
		$(".order-list").empty();
		$(".next-page").data("page_index",0);
	}
	ajax.request({
		url : _base_url+"/web/purchase/queryList.do",
		need_progressbar : message.showLoading,
		params : {
			page_index:startIndex,
			page_count:page_count,
			need_oauth:isFirst
		},
		success : function(header,body){
			initPurchaseList(body.purchaseList);
			callback && callback.apply();
			//updatePagination($('.index-page .next-page'),body.orderList && body.orderList.length,$(".index-page .titlebar"));
			//refreshTotalbar();
		},
		complete : function(){
			
		}
	});
	
	
}

function initPurchaseList(purchaseList){
	if(purchaseList == null || purchaseList.length == 0){
		//$("<div>").appendTo($listDiv).text("无数据");
		return;
	}
	
	var $listDiv = $(".purchase-list");
	for(var i=0;i<purchaseList.length;i++){
		initPurchaseItem(purchaseList[i],$listDiv,'delivery');
	}
	
	//按钮点击事件
	widget.bindTouchClick($(".list-item-bttn.oper"),function(e){
		var $bttn = $(e.target);
		if(!enableBttn($bttn)){
			return;
		}
		var entity = $bttn.parents('.purchase-item').data('entity');
		var handler = $bttn.data("handler");
		handler && handler.apply(null,[entity,$bttn]);
	});
	
	//顶栏箭头点击事件
	widget.bindTouchClick($(".purchase-titleline"),function(e){
		var $bttn = $(e.target);
		var purchaseEntity = $bttn.parents('.purchase-item').data('entity');
		
		common.gotoPage("../manage/m-purchase.html?purchase_id="+purchaseEntity.id);
	});
}

function initPurchaseItem(entity,$container,type){
	var $tempDiv = $("#_purchase_item_temp");
	var $newDiv = $tempDiv.clone().appendTo($container);
	$newDiv.data("entity",entity);
	$newDiv.attr("id",entity.id);
	$newDiv.show();
	
	$newDiv.find(".purchase-code").text("采购单编号:"+entity.id);
	$newDiv.find(".purchase-createtime").text(entity.createTime);
	$newDiv.find(".purchase-ordercount").text("订单数:"+(entity.orderCount || 0));
	
	$newDiv.find(".purchase-sum").text("￥"+entity.sum.toFixed(2));
	$newDiv.find(".purchase-ordersum").text("￥"+entity.orderSum.toFixed(2));
	
	$newDiv.find(".purchase-expressfee").text("运费:"+entity.orderExpressFee.toFixed(2)+"元");
	
	refreshStatusbar(entity,$newDiv);
	
	//initContent($newDiv,entity,entity.items,true);
}

function initContent($newItemDiv,entity,items,needDeliveryRoute){
	var $tempDiv = $("#_purchase_content_item_temp");
	var $contentList = $newItemDiv.find(".purchase-content");
	if($contentList.length == 0){
		$contentList = $newItemDiv;
	}
	if(items.length == 0){
		var $newDiv = $tempDiv.clone().appendTo($contentList);
		$newDiv.attr("id",'_blank');
		$newDiv.show();
		$newDiv.text("(无采购明细)");
		return;
	}
	var $newDiv = null;
	var itemEntity = null;
	var orderEntity = null;
	for(var i=0;i<items.length;i++){
		itemEntity = items[i];
		orderEntity = itemEntity.order;
		if(orderEntity == null){
			continue;
		}
		if(i > 0){
			createSplitline().appendTo($contentList);
		}
		$newDiv = $tempDiv.clone().appendTo($contentList);
		$newDiv.data("entity",itemEntity);
		$newDiv.attr("id",'purchaseitem_'+itemEntity.id);
		
		var $name = $newDiv.find(".purchase-item-name");
		var $mobile = $newDiv.find(".purchase-item-mobile");
		$name.text(orderEntity.name);
		$mobile.text(orderEntity.mobile);
		$newDiv.find(".purchase-item-address").text(orderEntity.address);
		$newDiv.find(".purchase-item-sum").text("￥"+orderEntity.sum.toFixed(2));
		$newDiv.find(".purchase-item-ordersum").text("￥"+orderEntity.sum.toFixed(2));
		
		if(orderEntity.buyType == 2){
			var togBuyText = "["+orderEntity.productName+"] 共"+orderEntity.totalCust+"人,"+orderEntity.totalAmount+"斤";
			var $promBlock = $newDiv.find(".promotion-block");
			initPromotionList($promBlock,[{type:5,text:togBuyText}]);
			$("<span class='viewdetail'>查看</span>").appendTo($promBlock.find(".item"));
			//$newDiv.find(".groupbuy-info .content").text("["+orderEntity.productName+"] 共"+orderEntity.totalCust+"人,"+orderEntity.totalAmount+"斤");
			$promBlock.show();
		}
		
		var $deliveryInfo = $newDiv.find(".purchase-item-deliveryinfo");
		
		$deliveryInfo.find(".deliveryno").text("快递单:"+(orderEntity.deliveryNo || ""));
		/*$deliveryInfo.find(".loading").addClass("loading-icon");
		$deliveryInfo.find(".right-part").show();*/
		
		if(entity.status == 2 && orderEntity.deliveryNo && needDeliveryRoute){
			$newDiv.find(".loading").addClass("loading-icon");
			$newDiv.find(".right-part").show();
			queryDeliveryRoute(orderEntity.deliveryNo,$newDiv);
		}else{
			$deliveryInfo.find(".right-part").remove();
		}
		if(i == items.length-1){
			$newDiv.css("margin-bottom","0px");
		}
		$newDiv.show();
	}
	
	widget.bindTouchClick($contentList.find(".promotion-block"),function(e){
		var $this = $(e.target);
		var $item = $this.parents('.purchase-content-item');
		var entity = $item.data("entity");
		var createOrderId = entity.order.id;
		common.gotoPage('../order/pay-groupbuy.html?order_id='+createOrderId);
	});
}

function updatePurchaseStatus(purchase,$bttn){
	var $titlebar = $('.index-page .titlebar');
	ajax.request({
		url : _base_url+"/web/purchase/packing.do",
		need_progressbar : $titlebar,
		params : {
			purchase_id : purchase.id
		},
		success : function(header,body){
			purchase.status = 1;
			message.successHide("操作成功");
			refreshStatusbar(purchase,$bttn.parents('.purchase-item'));
		}
	});
}
function updateDeliveryStatus(purchase,$bttn){
	var $titlebar = $('.index-page .titlebar');
	ajax.request({
		url : _base_url+"/web/purchase/delivering.do",
		need_progressbar : $titlebar,
		params : {
			purchase_id : purchase.id
		},
		success : function(header,body){
			purchase.status = 2;
			message.successHide("操作成功");
			refreshStatusbar(purchase,$bttn.parents('.purchase-item'));
		}
	});
}

function updateCompleteStatus(purchase,$bttn){
	var $titlebar = $('.index-page .titlebar');
	ajax.request({
		url : _base_url+"/web/purchase/complete.do",
		need_progressbar : $titlebar,
		params : {
			purchase_id : purchase.id
		},
		success : function(header,body){
			purchase.status = 99;
			message.successHide("操作成功");
			refreshStatusbar(purchase,$bttn.parents('.purchase-item'));
		}
	});
}

function refreshStatusbar(entity,$item){
	var $status = $item.find(".list-item-bttn.status");
	var $oper = $item.find(".list-item-bttn.oper");
	var handler = function(entity,$bttn){
		widget.popupMenu(null,"bottom top",[{
			text : "更新状态",
			handler : function(){
				showUpdateStatus(entity,$bttn);
			}
		},{
			text : "物流提醒",
			handler : function(){
				showDeliveryNotifyPage(entity,$bttn.parents(".purchase-item"));
			}
		},"-",{
			text : "删除",
			style:{
				color:"#FF0000"
			},
			handler : function(){
				message.confirmSlide("确定删除采购单"+entity.id+"?",function(){
					removePurchase(entity,$bttn.parents(".purchase-item"));
				});
				
			}
		}],{need_titlebar:false,remove_when_close:true});
	};
	
	
	if(entity.status == 0){
		$status.addClass("money-font");
		$status.text("未采购");
	}else if(entity.status == 1){
		$status.removeClass("money-font");
		$status.addClass("highlight-font");
		$status.text("已采购");
	}else if(entity.status == 2){
		$status.removeClass("highlight-font");
		$status.addClass("highlight-font");
		$status.text("已发货");
	}else if(entity.status == 99){
		$status.removeClass("highlight-font");
		$status.addClass("gray-font");
		$status.text("已完成");
		$oper.text("");
	}
	$oper.data("handler",handler);
}

function showUpdateStatus(entity,$bttn){
	widget.popupMenu(null,"bottom top",[{
		text : "打包",
		handler:function(){
			updatePurchaseStatus(entity,$bttn);
		}
	},{
		text : "发货",
		handler:function(){
			updateDeliveryStatus(entity,$bttn);
		}
	},{
		text : "完成",
		handler:function(){
			updateCompleteStatus(entity,$bttn);
		}
	}],{need_titlebar:"选择状态",remove_when_close:true});
}

function showDeliveryInfoInput(purchase){
	var $page = $(".deliveryinput-page");
	if($page.attr("is_init") != 1){
		//右上角菜单关闭按钮
		initTitlebarBttns($page.find(".titlebar"),{
			iconfont : 'close',
			handler:function(){
				$page.hide();
			}
		});
		
		//确定按钮
		widget.bindTouchClick($page.find(".footbar-bttn-ok"),function(){
			var deliveryArr = parseDeliveryInput($page);
			commitDeliveryInput(purchase,deliveryArr,function(){
				message.successHide("操作成功",$page.find(".titlebar"));
				$page.hide();
			});
		});
		
		//解析按钮
		/*CheckboxSet.init($page.find(".footbar-bttn-cancel"),{
			text : "解析",
			handler : function(checked){
				switchInput(purchase,$page,$(this));
				if(checked){
					$nextBttn.removeClass('disable-bttn');
				}else{
					$nextBttn.addClass('disable-bttn');
				}
			}
		});*/
		/*$page.find(".cbx-parse").change(function(){
			switchInput(purchase,$page,$(this));
		});*/
		
		$page.attr("is_init",1);
	}
	var $input = $page.find(".delivery-input");
	var $deliveryParse = $page.find(".delivery-parse");
	$input.text("");
	$input.show();
	$deliveryParse.hide();
	$page.find(".cbx-parse").attr("checked",false);
	widget.slide($page,"left right",400);
}

function queryDeliveryRoute(deliveryNo,$itemDiv){
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
			$itemDiv.find(".purchase-item-deliveryinfo .loading").remove();
			var $showall = $itemDiv.find(".purchase-item-deliveryinfo .showall");
			$showall.text("查看全部");
			$showall.attr("is_loading",0);
			$showall.removeClass("gray-font");
			var rows = responseData.rows;
			if(rows == null || rows.length == 0)
				return;
			var routeList = rows[0].routes;
			if(routeList == null || routeList.length == 0)
				return;
			$itemDiv.data("routeList",routeList);
			var $deliveryListDiv = $itemDiv.find(".purchase-delivery-list");
			$deliveryListDiv.data('routeList',routeList);
			var $newDeliveryDiv = insertRoute(routeList[routeList.length-1],true,$deliveryListDiv);
			$newDeliveryDiv.addClass("highlight-font");
			$newDeliveryDiv.find(".flag").addClass("latest");
			
			widget.bindTouchClick($showall,function(e){
				var $this = $(e.target);
				if($this.attr("is_expand") == 1){
					//已经展开了，点击缩合
					$deliveryListDiv.find(".purchase-delivery.gray-font").hide();
					$this.attr("is_expand",0);
					return;
				}else if($this.attr("is_expand") == 0){
					//已经缩合了，点击展开
					$deliveryListDiv.find(".purchase-delivery.gray-font").show();
					$this.attr("is_expand",1);
				}else{
					//第一次则新初始化
					for(var i=routeList.length-2;i>0;i--){
						var $routeDiv = insertRoute(routeList[i],false,$deliveryListDiv);
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


function insertRoute(route,needMobile,$listDiv){
	var $newDiv = $("#_delivery_route_temp").clone().appendTo($listDiv);
	//var $deliveryDiv = $itemDiv.find(".purchase-delivery");
	$newDiv.attr("id","");
	$newDiv.find(".time").text(route.updateAt);
	$newDiv.find(".node").text(route.actNode);
	$newDiv.find(".state").text(route.state);
	$newDiv.find(".name").text(route.updateByName+"("+route.mobile+")");
	$newDiv.find(".dial").attr("href","tel:"+route.mobile);
	$newDiv.data("route",route);
	
	if(needMobile){
		$newDiv.find(".dial").attr("href","tel:"+speeda_phone);
	}else{
		$newDiv.find(".dial").remove();
	}
	
	$newDiv.show();
	return $newDiv;
}

function parseDeliveryInput($page){
	var $trs = $page.find(".delivery-input").find("tr");
	var deliveryArr = [];
	for(var i=0;i<$trs.length;i++){
		var $tr = $trs.eq(i);
		var $tds = $tr.find("td");
		if($tds.first().text().trim().length == 0){
			continue;
		}
		deliveryArr.push({
			order_id : $tds.first().text(),
			delivery_no : $tds.eq(2).text(),
			weight : $tds.eq(3).text(),
			fee : $tds.eq(4).text()
		});
	}
	return deliveryArr;
}

function commitDeliveryInput(purchase,deliveryArr,callback){
	ajax.request({
		url : _base_url+"/web/purchase/setDeliveryInfo.do",
		need_progressbar : true,
		params : {
			purchase_id : purchase.id,
			delivery_info : JSON.stringify(deliveryArr)
		},
		success : function(header,body){
			callback.apply();
		}
	});
}

function switchInput(purchase,$page,$checkbox){
	var checked = $checkbox.is(":checked");
	var $input = $page.find(".delivery-input");
	var $deliveryParse = $page.find(".delivery-parse");
	if(checked){
		//显示解析后的数据
		$input.hide();
		$deliveryParse.empty();
		var deliveryArr = parseDeliveryInput($page);
		if(deliveryArr == null || deliveryArr.length == 0){
			return;
		}
		initContent($deliveryParse,purchase,purchase.items,false);
		var $deliverylines = $deliveryParse.find(".purchase-item-deliveryinfo");
		for(var i=0;i<$deliverylines.length;i++){
			var $line = $deliverylines.eq(i);
			var p = purchase.items[i];
			var orderId = p.order.id;
			for(k=0;k<deliveryArr.length;k++){
				if(deliveryArr[k].order_id == orderId){
					$line.find(".deliveryno").text("快递单号:"+deliveryArr[k].delivery_no);
					var $right = $("<div class='delivery-parse-feeweight'>").appendTo($line);
					$("<span class='fee money-font'>￥"+deliveryArr[k].fee+"</span>").appendTo($right);
					$("<span class='weight gray-font'>"+deliveryArr[k].weight+"斤</span>").appendTo($right);
					break;
				}
			}
		}
		$deliveryParse.show();
	}else{
		//显示输入框
		$deliveryParse.hide();
		$input.show();
	}
}

function showDeliveryNotifyPage(purchase,$purchaseItem){
	var $page = $(".index-page");
	$page.data("purchase_item_div",$purchaseItem);
	
	$(".deliverynotify-page").children().clone().appendTo($page);
	
	//右上角菜单关闭按钮
	initTitlebarBttns($page.find(".titlebar.notify"),{
		iconfont : 'close',
		handler:function(){
			closeDeliveryNotifyPage($purchaseItem);
		}
	});
	
	//确定按钮
	widget.bindTouchClick($page.find(".footbar.notify .footbar-bttn-ok"),function(){
		message.confirmSlide("确定发送提醒?",function(){
			commitNotify($purchaseItem);
		});
		
	});
	
	//全选按钮
	CheckboxSet.init($page.find(".footbar.notify .footbar-bttn-cancel"),{
		text : "全选",
		checked : true,
		handler : function(checked){
			if(checked){
				$purchaseItem.find(".checkbox").html(fontset.check);
			}else{
				$purchaseItem.find(".checkbox").html("");
			}
			
		}
	});
	
	createNotifyList(purchase,$purchaseItem);
	
}
function createNotifyList(purchase,$purchaseItem){
	var $items = $purchaseItem.find(".purchase-content-item");
	for(var i=0;i<$items.length;i++){
		var $item = $items.eq(i);
		var entity = $item.data("entity");
		var $notifydiv = $("<div class='delivery-notify-line'>").prependTo($item);
		//var $titleline = $item.find(".purchase-content-titleline");
		
		var $set = CheckboxSet.init($notifydiv,{
			text : "选择",
			checked:true
		});
		$set.css("float","left");
		var lasttime = entity.lastNotifyTime;
		if(lasttime){
			var $lasttime = $("<div class='delivery-notify-time gray-font'>").appendTo($notifydiv);
			$lasttime.text(lasttime+"已发送");
		}
		
		/*var $checkbox = $("<input class='delivery-notify-check' checked type='checkbox'>").appendTo($notifydiv);
		$checkbox.attr("id","checkbox_"+entity.id);
		var $label = $("<label for='"+$checkbox.attr("id")+"'>选择</label>").appendTo($notifydiv)
		var lasttime = entity.lastNotifyTime;
		if(lasttime){
			var $lasttime = $("<div class='delivery-notify-time gray-font'>").appendTo($notifydiv);
			$lasttime.text(lasttime+"已发送");
		}*/
	}
	
}

function closeDeliveryNotifyPage($purchaseItem){
	$purchaseItem.find(".delivery-notify-line").remove();
	$(".index-page .titlebar.notify").remove();
	$(".index-page .footbar.notify").remove();
}

function commitNotify($purchaseItem){
	var $checks = $purchaseItem.find(".checkbox.checked");
	var $notifytitlebar = $(".deliverynotify-page .titlebar");
	if($checks == null || $checks.length == 0){
		message.errorHide("没有可发送通知的用户",$notifytitlebar);
		return;
	}
	var notifyArr = [];
	for(var i=0;i<$checks.length;i++){
		var $check = $checks.eq(i);
		var $contentItem = $check.parents(".purchase-content-item");
		var entity = $contentItem.data("entity");
		var routeList = $contentItem.data("routeList");
		var notifyObj = {
			user_id : entity.order.userId,
			purchase_item_id : entity.id,
			order_id : entity.orderId
		}
		if(routeList != null && routeList.length > 0){
			var latestNode = routeList[routeList.length-1];
			notifyObj.delivery_time = latestNode.updateAt;
			notifyObj.delivery_node = latestNode.actNode;
		}
		
		notifyArr.push(notifyObj);
		
	}
	
	ajax.request({
		url : _base_url+"/web/purchase/sendDeliveryNotify.do",
		need_progressbar : $notifytitlebar,
		params : {
			delivery_notify : JSON.stringify(notifyArr)
		},
		success : function(header,body){
			closeDeliveryNotifyPage($purchaseItem);
			message.successHide("操作成功",$(".index-page .titlebar"));
		}
	});
}

function removePurchase(purchase,$item){
	var $title = $(".index-page .titlebar");
	ajax.request({
		url : _base_url+"/web/purchase/remove.do",
		need_progressbar : $title,
		params : {
			purchase_id : purchase.id
		},
		success : function(header,body){
			message.successHide("操作成功",$title);
			$item.remove();
			
		}
	});
}