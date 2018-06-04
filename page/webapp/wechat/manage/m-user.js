var page_count = 10;
var $indexTitlebar = null;
var $modifyTitlebar = null;
var loading = new PageLoading();
$(function(){
	loading.show();
	
	//分页点击
	initPagination($(".next-page"),page_count,function(index){
		loadUserList(index);
	});
	
	loadUserList(0,true,function(){
		initPage();//有token则直接显示列表
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

function loadUserList(startIndex,isFirst,callback){
	if(startIndex == 0){
		//从第一条开始，那说明不是翻页加载，需要把原数据清空
		$(".user-list").empty();
	}
	
	ajax.request({
		url : _base_url+"/web/manage/queryUserList.do",
		need_progressbar : false,
		params:{
			page_index:startIndex,
			page_count:page_count,
			need_oauth:isFirst
		},
		success : function(header,body){
			var user_list = body.userList;
			updatePagination($('.next-page'),user_list && user_list.length,$(".titlebar"));
			
			if(user_list == null || user_list.length == 0)
				return;
			
			var $tempDiv = $("#_user_item_temp");
			var $listDiv = $(".user-list");
			
			
			
			for(var i=0;i<user_list.length;i++){
				var userEntity = user_list[i];
				var $newOrderItem = $tempDiv.clone().appendTo($listDiv);
				$newOrderItem.data("entity",userEntity);
				
				$newOrderItem.attr("id",userEntity.id);
				
				$newOrderItem.find(".user-item-name").text(userEntity.name);
				$newOrderItem.find(".user-item-mobile").text(userEntity.mobile);
				if(userEntity.device){
					$newOrderItem.find(".user-item-device").text("("+userEntity.device+")");
				}
				$newOrderItem.find(".user-item-addr").text(userEntity.address);
				
				$newOrderItem.find(".user-item-ordercount").text("共消费:"+userEntity.orderCount+"次,");
				$newOrderItem.find(".user-item-ordersum").text("￥"+userEntity.orderSum);
				
				$newOrderItem.find(".user-item-createtime").text(userEntity.createTime.split(" ")[0]);
				$newOrderItem.find(".user-item-activetime").text("最近活跃:"+userEntity.activeTime);
				if(userEntity.lastOrderTime){
					$newOrderItem.find(".user-item-lasttime").text("近期下单:"+userEntity.lastOrderTime);
				}
				$newOrderItem.show();
			}
			
			widget.bindTouchClick($listDiv.find(".user-item-bttn"),function(e){
				var $bttn = $(e.target);
				if($bttn.hasClass("oper")){
					var $item = $bttn.parents(".user-item");
					var user = $item.data("entity");
					
					widget.popupMenu(null,"bottom top",[{
						text : "查看订单",
						handler : function(){
							common.gotoPage("../manage/m-order-list.html?user_id="+user.id);
						}
					},{
						text : "查看优惠券",
						handler : function(){
							common.gotoPage("../manage/m-coupon.html?user_id="+user.id);
						}
					}],{need_titlebar:false,remove_when_close:true});
					
					//
				}else if($bttn.hasClass("modify")){
					showModifyPage($bttn);
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


//修改资料
function showModifyPage($bttn){
	var $modifypage = $(".user-modify-page");
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
			commitModify();
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
	var $item = $bttn.parents(".user-item");
	var user = $item.data("entity");
	$modifypage.data("entity",user);
	
	showEntityValue(user,$modifypage.find("[entityKey]"));//
	
	widget.slide($modifypage,"left right",400);
}

function commitModify(){
	var $modifypage = $(".user-modify-page");
	var user = $modifypage.data("entity");
	var entity = buildEntityValue($modifypage.find("[entityKey]"));
	entity.user_id = user.id;
	ajax.request({
		url : _base_url+"/web/manage/modifyUser.do",
		need_progressbar : $indexTitlebar,
		params : entity,
		success : function(header,body){
			message.successHide("操作成功",$indexTitlebar);
			$modifypage.hide();
			loadUserList();
		}
	});
}


