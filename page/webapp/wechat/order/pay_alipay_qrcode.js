$(function(){
	initIndex();
});

function initIndex(){
	var params = common.getUrlParams();
	var orderId = params.order_id;
	if(!orderId){
		message.alert("请以正确的姿势打开~")
		return;
	}
	
	$(".paycode").text("订单编号:"+params.order_id);
	$(".linkman").text("收件人:"+decodeURI(params.linkman));
	$(".paysum").text("订单金额:"+params.pay_sum);
	
	/*widget.bindTouchClick($(".copy"),function(){
		alert();
	});*/
}