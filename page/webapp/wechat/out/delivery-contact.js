var $indexTitlebar;
var purchaseOrderIds = [];

var loading = new PageLoading();
$(function(){
	loading.show();
	loadData(function(){
		initPage();
		loading.hide();
		$(".index-page").show();
	});
});
function initPage(){
	$indexTitlebar = $(".titlebar.index");
	
	//右下角操作按钮
	widget.bindTouchClick($(".delivery-commit"),function(){
		commitData();
	});
}


function loadData(callback){
	var token = common.getUrlParams("token");
	var skey = common.getUrlParams("skey");
	common.setAuth(token,skey);
	
	common.setSkey
	ajax.request({
		url : _base_url+"/web/purchase/queryDeliveryContact.do",
		need_progressbar : false,
		params : {
			purchase_id : common.getUrlParams("purchase_id")
		},
		success : function(header,body){
			initOrderList(body.orderList);
			
			callback && callback.apply();
		},
		complete : function(){
		}
	});
}

function initOrderList(orderList,prodList,promotionList){
	if(orderList == null || orderList.length == 0){
		return;
	}
	var $table = $(".delivery-table");
	
	for(var i=0;i<orderList.length;i++){
		var orderEntity = orderList[i];
		
		var $tr = $("<tr>").appendTo($table);
		$tr.data("entity",orderEntity);
		
		var $tdName = $("<td class='item-td'></td>").appendTo($tr).text(orderEntity.name);
		var $tdNo = $("<td class='item-td'><input style='width:90%' class='delivery-no item-input common-font'/></td>").appendTo($tr);
		var $tdWeight = $("<td class='item-td'><input class='weight item-input common-font'/></td>").appendTo($tr);
		var $tdFee = $("<td class='item-td'><input class='fee item-input common-font'/></td>").appendTo($tr);
		
		$tdNo.find("input").val(orderEntity.deliveryNo);
		$tdWeight.find("input").val(orderEntity.deliveryWeight);
		$tdFee.find("input").val(orderEntity.deliveryFee);
		
	}
}

function commitData(){
	var $trs = $(".delivery-table").find("tr");
	var arr = [];
	var $errorbar = $(".errorbar");
	for(var i=0;i<$trs.length;i++){
		var $tr = $trs.eq(i);
		var order = $tr.data("entity");
		if(order == null){
			continue;
		}
		var no = $tr.find(".delivery-no").val();
		var weight = $tr.find(".weight").val();
		var fee = $tr.find(".fee").val();
		if(no.length == 0){
			$errorbar.text(order.name+"行没有填写快递单号");
			return;
		}
		if(weight.length == 0){
			$errorbar.text(order.name+"行没有填写重量");
			return;
		}
		if(fee.length == 0){
			$errorbar.text(order.name+"行没有填写快运费");
			return;
		}
		arr.push({
			id : order.id,
			delivery_no : no,
			delivery_weight : weight,
			delivery_fee : fee*100
		});
	}
	var str = JSON.stringify(arr);
	var balance = $(".balance input").val();
	
	if(balance.length == 0){
		$errorbar.text("请填写账户余额");
		return;
	}
	message.confirm("确定提交?",function(){
		ajax.request({
			url : _base_url+"/web/purchase/commitDeliveryData.do",
			need_progressbar : true,
			params : {
				delivery_info : str,
				balance : balance*100
			},
			success : function(header,body){
				message.alert("提交成功!");
			}
		});
	});
	
}