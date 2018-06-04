var page_count = 10;
var $indexTitlebar = null;
var $modifyTitlebar = null;

var loading = new PageLoading();
$(function(){
	loading.show();
	loadProdList(function(){
		initPage();
		loading.hide();
		$(".index-page").show();
	});
});
function initPage(){
	$indexTitlebar = $(".titlebar.indexpage");
	//右上角菜单按钮点击
	initTitlebarBttns($indexTitlebar,{
		iconfont : 'menu',
		menu_items : getMenus()
	});
}

function loadProdList(callback){
	ajax.request({
		url : _base_url+"/web/manage/queryProductList.do",
		need_progressbar : false,
		success : function(header,body){
			prod_list = body.productList;
			
			var $tempDiv = $("#_prod_item_temp");
			var $listDiv = $(".prod-list");
			$listDiv.empty();
			
			for(var i=0;i<prod_list.length;i++){
				var prodEntity = prod_list[i];
				var unit = prodEntity.unit;
				var $newOrderItem = $tempDiv.clone().appendTo($listDiv);
				$newOrderItem.data("entity",prodEntity);
				$newOrderItem.find(".prod-item-pic").css({
					"background-image":"url("+prodEntity.pic+")",
					"background-position" : "center center",
					"background-size" : "100% 100%"
				});
				$newOrderItem.attr("id",prodEntity.id);
				$newOrderItem.find(".prod-item-name").text(prodEntity.name);
				$newOrderItem.find(".prod-item-spec").text('规格:'+prodEntity.spec);
				
				$newOrderItem.find(".prod-item-price").text("￥"+prodEntity.price+"/"+unit);
				$newOrderItem.find(".prod-item-costprice").text("￥"+prodEntity.costPrice+"/"+unit);
				
				$newOrderItem.find(".prod-item-summary").text(prodEntity.summary);
				//$newOrderItem.find(".prod-item-bttn-oper").text(prodEntity.status===1?"下架 ":"上架");
				
				var $switchBttn = $newOrderItem.find(".prod-item-bttn.switch");
				refreshProdStatus($switchBttn,prodEntity);
				
				$newOrderItem.show();
				
			}
			
			widget.bindTouchClick($listDiv.find(".prod-item-bttn"),function(e){
				var $bttn = $(e.target);
				if($bttn.hasClass("switch")){
					switchProd($bttn);
				}else if($bttn.hasClass("modify")){
					showModifyProd($bttn);
				}
				e.stopPropagation();
				e.preventDefault();
			});
			
			callback && callback.apply();
		},
		error : function(header,body){
			if(header.error_code == 200000){
				//说明权限过期了
				gotoLogin();
				return false;
			}
		},
		complete : function(){
			
		}
	});
}

//切换产品状态
function switchProd($bttn){
	var $item = $bttn.parents(".prod-item");
	var prod = $item.data("entity");
	var isOnline = prod.status == 99;
	message.confirmSlide(isOnline?"确认下架?":"确认上架?",function(){
		ajax.request({
			url : _base_url+"/web/manage/modifyProduct.do",
			need_progressbar : $indexTitlebar,
			params : {
				product_id:prod.id,
				status : isOnline ? "0":"99"
			},
			success : function(header,body){
				message.successHide("操作成功！",$indexTitlebar);
				prod.status = prod.status==99 ? 0 : 99;
				
				refreshProdStatus($bttn,prod);
			}
		});
	});
}

//修改产品资料
function showModifyProd($bttn){
	var $modifypage = $(".prod-modify-page");
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
			message.confirmSlide("确定执行操作?",function(){
				commitModifyProd();
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
		$modifypage.attr("is_init",1);
	}
	var $item = $bttn.parents(".prod-item");
	var prod = $item.data("entity");
	$modifypage.data("entity",prod);
	
	showEntityValue(prod,$modifypage.find("[entityKey]"));//
	
	widget.slide($modifypage,"left right",400);
}

function commitModifyProd(){
	var $modifypage = $(".prod-modify-page");
	var prod = $modifypage.data("entity");
	var entity = buildEntityValue($modifypage.find("[entityKey]"));
	entity.product_id = prod.id;
	ajax.request({
		url : _base_url+"/web/manage/modifyProduct.do",
		need_progressbar : $indexTitlebar,
		params : entity,
		success : function(header,body){
			message.successHide("操作成功",$indexTitlebar);
			$modifypage.hide();
			loadProdList();
		}
	});
}
function refreshProdStatus($bttn,prodEntity){
	$bttn.text(prodEntity.status==99?"下架 ":"上架");
	var $table = $bttn.parents(".prod-item").children("table");
	if(prodEntity.status==99){
		$bttn.removeClass("offline common-bg");
		$table.removeClass("gray-filter");
	}else{
		$bttn.addClass("offline common-bg");
		$table.addClass("gray-filter");
	}
}


