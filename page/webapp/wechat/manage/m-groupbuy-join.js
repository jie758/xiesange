var loading = new PageLoading();
var prodId;
var groupbuyId;
$(function(){
	loading.show();
	initPage();
});

function initPage(){
	groupbuyId = common.getUrlParams("groupbuy_id");
	initTitlebarBttns($(".index-page .titlebar"),[{
		iconfont : 'menu',
		menu_items : getMenus()
	}]);
	
	loadDate(0,function(){
		$(".index-page").show();
		loading.hide();
	});
	
	widget.bindTouchClick($(".status-bttn"),function(e){
		var $this = $(e.target);
		if($this.hasClass("common-bog")){
			return;
		}
		$(".status-bttn").removeClass("common-bg");
		$this.addClass("common-bg");
		$(".groupbuy-join-list").empty();
		loadDate(0);
	});
}

function loadDate(startIndex,callback){
	ajax.request({
		url : _base_url+"/web/manage/queryGroupbuyJoin.do",
		need_progressbar : $(".index-page .titlebar"),
		params : {
			groupbuy_id : groupbuyId,
			need_payed : $(".status-bttn.payed").hasClass("common-bg")?1:0,
			need_unpay : $(".status-bttn.unpay").hasClass("common-bg")?1:0,
			page_index:startIndex,
			page_count:page_count
		},
		success : function(header,body){
			var joinList = body.joinList;
			var prodList = body.productList;
			initGroupbuyJoinList(joinList,prodList);
			initStat(body.statList,prodList,body.totalCustCount);
			
			updatePagination($('.next-page'),joinList);
			
			callback && callback.apply();
		}
	});
	if(startIndex == 0){
		//分页点击
		initPagination($(".next-page"),page_count,function(index){
			loadDate(index);
		});
	}
}
function initStat(statList,prodList,totalCustCount){
	var $statarea = $(".stat-area");
	var $prodlist = $statarea.find(".product-list");
	$prodlist.empty();
	var totalSum = 0
	for(var i=0;i<statList.length;i++){
		var stat = statList[i];
		var prod = common.matchItem(stat.productId,prodList,'id') || {};
		var total = stat.custCount+"人,"+stat.amount+"斤,"+stat.sum+"元 ";
		var $div = div($prodlist,"prod-stat").text(prod.name+(prod.spec && "("+prod.spec+")"));
		$("<span class='right-part'>").appendTo($div).text("共"+total);
		//totalCust += prod.cust;
		totalSum += stat.sum;
	}
	$statarea.find(".bottomline").text("共:"+totalCustCount+"人,"+totalSum.toFixed(2)+"元");
}

function initGroupbuyJoinList(list,productList){
	if(!list)return;
	var $tempItem = $("#_item_temp");
	var $list = $(".index-page .groupbuy-join-list");
	for(var i=0;i<list.length;i++){
		var entity = list[i];
		
		var $item = $tempItem.clone().appendTo($list);
		$item.data("entity",entity);
		$item.attr("id","item_"+entity.id);
		$item.find(".name").text(entity.name);
		$item.find(".mobile").text(entity.mobile);
		$item.find(".address").text(entity.address);
		$item.find(".status").text(entity.status==0?"未支付":"已支付");
		$item.find(".status").addClass(entity.status==0 ? 'money-font':'gray-font');
		$item.find(".content").text(entity.intro);
		
		initOrderProduct($item,entity.items,productList);
		
		$item.show();
		
		//createSplitline().appendTo($list);
	}
	
	widget.bindTouchClick($list,function(e){
		var $item = $(e.target).parents(".groupbuy-join-item");
		var gbid = $item.data("entity").id;
		common.gotoPage("../order/pay.html?order_id="+gbid);
	});
	
	
}

function initOrderProduct($item,items,productList){
	var $tempDiv = $("#_order_content_item_temp");
	var $contentList = $item.find(".content");
	var totalsum = 0;
	for(var i=0;i<items.length;i++){
		var itemEntity = items[i];
		var prodEntity = common.matchItem(itemEntity.productId,productList,'id') || {};
		$newDiv = $tempDiv.clone().appendTo($contentList);
		$newDiv.attr("id",'orderitem_'+itemEntity.id);
		
		$newDiv.find(".order-item-prodname").text(prodEntity.name+"("+prodEntity.spec+")：");
		$newDiv.find(".order-item-amount").text(itemEntity.amount);
		
		var $price = $newDiv.find(".order-item-prodprice");
		$price.text("￥"+itemEntity.price+"/"+prodEntity.unit);
		
		var $sum = $newDiv.find(".order-item-sum");
		$sum.text("￥"+itemEntity.sum);
		totalsum += itemEntity.sum;
		
		/*if(statObj[prodEntity.id] == null){
			statObj[prodEntity.id] = $.extend(prodEntity,{cust:0,amount:0,sum:0});
		}
		statObj[prodEntity.id].amount += itemEntity.amount;
		statObj[prodEntity.id].sum += itemEntity.sum;
		statObj[prodEntity.id].cust += 1;*/
		$newDiv.show();
	}
	$item.find(".totalsum").text("共:￥"+totalsum);
}

