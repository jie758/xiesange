var $indexTitlebar;
var orderId = null;
var orderEntity = null;
var isAdjustPrice = null;
var loading = new PageLoading();
$(function(){
	loading.show();
	orderId = common.getUrlParams("order_id");
	isAdjustPrice = common.getUrlParams("is_adjust_price") == 1;
	$indexTitlebar = $(".index-page .titlebar");
	loadData(function(){
		initPage();
		loading.hide();
		$(".index-page").show();
	});
});

function loadData(){
	ajax.request({
		url : _base_url+"/web/order/queryDetail.do",
		need_progressbar : false,
		params : {
			order_id:orderId,
			//need_promotion_list:10,
			need_oauth:true/*,
			need_wx_signature:true*/
		},
		success : function(header,body){
			orderEntity = body.order;
			initPage();
		},
		complete : function(){
			loading.hide();
			$(".index-page").show();
		}
	});
}

function initPage(){
	var $modifypage = $(".index-page");
	//取消按钮
	widget.bindTouchClick($modifypage.find(".footbar-bttn-cancel"),function(e){
		history.go(-1); //后退1页
	});
	//确定修改按钮
	widget.bindTouchClick($modifypage.find(".footbar-bttn-ok"),function(e){
		message.confirmSlide("确定修改订单?",function(){
			commitModifyOrder();
		});
		e.stopPropagation();
	});
	//右上角菜单关闭按钮
	initTitlebarBttns($indexTitlebar,{
		iconfont : 'menu',
		menu_items : getMenus()
	});
	
	//状态点击事件
	if(!isAdjustPrice){
		widget.bindTouchClick($modifypage.find(".item-input-value.status"),function(e){
			var $clickitem = $(e.currentTarget);
			showStatusPopupmenu(function(newStatus,statusObj){
				orderEntity.status = newStatus;
				orderEntity.statusText = statusObj.text;
				//$clickitem.text(getStatusInfo(entity,newStatus).text);
				//$clickitem.data("newValue",newStatus);
				var $statusItem = $clickitem.find(".status");
				showEntityValue(orderEntity,$statusItem);
				$statusItem.data("newValue",newStatus);
				//updateStatus($clickitem,entity,status);
			});
			e.stopPropagation();
		});
	}
		
	var orderStatus = getOrderStatus(orderEntity);
	orderEntity.statusText = orderStatus.text;
	orderEntity.profit = orderEntity.sum||0+orderEntity.expressSum||0-orderEntity.cost||0-orderEntity.expressCost||0;
	showEntityValue(orderEntity,$modifypage.find("[entityKey]"));
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
	
	initOrderItems();
}

function commitModifyOrder(){
	var $modifypage = $(".index-page");
	var order = $modifypage.data("entity");
	var entity = buildEntityValue($modifypage.find("[entityKey]"));
	if(common.isEmpty(entity)){
		message.errorHide("未做任何修改",$indexTitlebar);
		return;
	}
	entity.order_id = orderEntity.id;
	entity.is_adust_price = isAdjustPrice?1:0;
	ajax.request({
		url : _base_url+"/web/manage/modifyOrder.do",
		need_progressbar : $indexTitlebar,
		params : entity,
		success : function(header,body){
			message.successHide("操作成功",$indexTitlebar);
		}
	});
}

function initOrderItems(){
	var $listDiv = $(".prod-item-list");
	var orderItems = orderEntity.items;
	if(orderItems == null || orderItems.lenght == 0){
		return;
	}
	
	var $tempDiv = $("#_proditem_temp");
	var $newDiv = null;
	var itemEntity = null;
	var prodEntity = null;
	var orderSum = 0;
	for(var i=0;i<orderItems.length;i++){
		if(i > 0){
			var $sepDiv = $("<div>").appendTo($listDiv);
			createSplitline().appendTo($sepDiv);
		}
		itemEntity = orderItems[i];
		prodEntity = itemEntity.product;
		
		$newDiv = $tempDiv.clone().appendTo($listDiv);
		$newDiv.attr("id","orderitem_"+itemEntity.id);
		prodEntity.price = itemEntity.price || prodEntity.price;
		prodEntity.amount = itemEntity.amount;
		ProductSet.init($newDiv,prodEntity,{size:50});
		
		//$newDiv.find(".payitem-amount").text(itemEntity.amount+prodEntity.unit);
		//$newDiv.find(".payitem-sum").text("￥"+itemEntity.sum.toFixed(2));
		orderSum += itemEntity.price * itemEntity.amount;
	}
	return orderSum;
}

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

