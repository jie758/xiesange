$(document).ready(function(e) {
	if(common.isPC()){
		$("body").addClass("pc-body")
	}
	initPage();
});

function initPage(){
	
	var isOnline = $("#is_online").prop("checked");
	if(isOnline){
		$(".bttn").text("提交战绩");
		showOnlinePage();
	}else{
		showOfflinePage();
	}
	
	widget.bindTouchClick($(".bttn"),function(e){
		var isOnline = $("#is_online").prop("checked");
		if(isOnline){
			createOnline();
		}else{
			create();
		}
		
	});
	
	widget.bindTouchClick($(".viewall"),function(e){
		common.gotoPage("record-list.html");
	});
	
	widget.bindTouchClick($(".stat"),function(e){
		var $target = $(e.target);
		var isonline = $target.hasClass("online");
		gotoStatPage(isonline);
	});
	
	var $mjtype = $(".mj-type");
	widget.bindTouchClick($mjtype,function(e){
		$mjtype.removeClass("common-bg");
		$(e.target).addClass("common-bg");
		var defaultPrice = $(e.target).attr("default-price");
		$('.price').val(defaultPrice);
	});
	
	var $online = $("#is_online");
	$online.change(function(e){
		var isOnline = $("#is_online").prop("checked");
		if(isOnline){
			showOnlinePage();
		}else{
			showOfflinePage();
		}
	})
	
	var hasPwd = common.session("has_pwd");
	if(hasPwd == 1){
		showMain();
	}else{
		showMain();
		//initPwd();
	}
	
}

function showOnlinePage(){
	var $personList = $(".person-list");
	$personList.empty();
	var $table = $("<table class='person-table' border='0' cellspacing='1' cellpadding='1'>").appendTo($personList);
	var index = 0;
	var $tr = null;
	for(var key in membernames){
		if(key == "shui" || key == "huifei"){
			continue;
		}
		var p = membernames[key];
		if(p.status === 0){
			continue;
		}
		if(index % 2 == 0){
			$tr = $("<tr>").appendTo($table);
		}
		var $td_name = $("<td>").appendTo($tr);
		var $img = $("<img class='online-img'>").appendTo($td_name).attr("src",p.headimg);
		var $name = $("<div class='online-nickname'>").appendTo($td_name).text(p.nickname).css("font-size","0.9rem");
		
		var $td_input = $("<td width='45px'>").appendTo($tr);
		
		p.code = key;
		
		var $input = $("<input class='item-input score-input'>").appendTo($td_input);
		$input.data("entity",p);
		
		$input.blur(function(){
			checkScore()
		});
		$input.focus(function(e){
			autoCalcScore($(e.target));
		});
		index++;
	}
}

function autoCalcScore($input){
	if($input.val().length > 0){
		return;
	}
	var $inputs = $(".person-table .score-input");
	var totalScore = 0;
	var scoreCount = 0;
	for(var i=0;i<$inputs.length;i++){
		if($inputs.eq(i).val().length > 0){
			var score = parseInt($inputs.eq(i).val());
			totalScore += score;
			scoreCount++;
		}
	}
	if(scoreCount == 3){
		$input.val(totalScore*-1);
		return;
	}
}
function checkScore(){
	var $inputs = $(".person-table .score-input");
	var scores = [];
	var totalScore = 0;
	for(var i=0;i<$inputs.length;i++){
		if($inputs.eq(i).val().length > 0){
			var score = parseInt($inputs.eq(i).val());
			scores.push(score);
			totalScore += score
		}
	}
	if(scores.length > 4 || (scores.length == 4 && totalScore !=0)){
		message.errorHide("数据不平");
		return;
	}
}

function showOfflinePage(){
	var $personList = $(".person-list");
	$personList.empty();
	for(var key in membernames){
		if(key == "shui" || key == "huifei"){
			continue;
		}
		var p = membernames[key];
		if(p.status === 0){
			continue;
		}
		p.code = key;
		var $div = $("<div class='person-item'>").text(p.name).appendTo($personList);
		var $img = $("<img class='offline-img'>").appendTo($div).attr("src",p.headimg);
		
		$div.data("entity",p);
		widget.bindTouchClick($div,function(e){
			$(e.target).toggleClass("common-bg");
		});
	}
}

function gotoStatPage(isonline,orderBy){
	orderBy = orderBy || "sum";
	var now = new Date().toStr("yyyy-MM-dd");
	var currentYears = now.split("-");
	var startMonth=parseInt(currentYears[1]);
	var endMonth = null;
	if(startMonth % 2 == 0){
		endMonth = startMonth;
		startMonth = startMonth-1;
	}else{
		endMonth = startMonth+1;
	}
	startMonth = startMonth < 10 ? "0"+startMonth : startMonth;
	endMonth = endMonth < 10 ? "0"+endMonth : endMonth;
	startMonth = currentYears[0]+"-"+startMonth;
	endMonth = currentYears[0]+"-"+endMonth;
	if(isonline){
		common.gotoPage("stat.html?is_online=1&start_date="+now+"&end_date="+now+"&order_by="+orderBy);
	}else{
		common.gotoPage("stat.html?start_month="+startMonth+"&end_month="+endMonth);
	}
}

function create(){
	var $personList = $(".person-list");
	var $fightpersons = $personList.find(".common-bg");
	if($fightpersons.length != 4){
		message.errorHide("请先选择4位战斗人员");
		return false;
	}
	
	if(!checkCreate()){
		return;
	}
	
	$(".bttn").addClass("disable-bttn");
	var persons = [];
	for(var i=0;i<$fightpersons.length;i++){
		var p = $fightpersons.eq(i).data("entity");
		persons.push(p.code);
	}
	ajax.request({
		url : _base_url+"/web/shanghui/create.do",
		need_progressbar : false,
		params : {
			price : $(".price").val(),
			members:persons.join(","),
			mj_type : $(".mj-type.common-bg").attr("type"),
			is_online : $("#is_online").prop("checked") ? 1 : 0
		},
		success : function(header,body){
			common.gotoPage("score.html?sid="+body.newid);
		},
		complete : function(){
			$(".bttn").removeClass("disable-bttn");
		}
	});
}

function createOnline(){
	if(!checkCreate()){
		return;
	}
	
	var $inputlist = $(".person-list .item-input");
	var scores = [];
	var membs = [];
	var totalScore = 0;
	for(var i=0;i<$inputlist.length;i++){
		var $input = $inputlist.eq(i);
		if($input.val()){
			var p = $input.data("entity");
			scores.push($input.val());
			membs.push(p.code);
			totalScore += parseInt($input.val());
		}
	}
	if(membs.length != 4){
		message.errorHide("请先选择4位战斗人员，并直接输入战绩");
		return false;
	}
	if(totalScore != 0){
		message.errorHide("数据不平");
		return false;
	}
	
	$(".bttn").addClass("disable-bttn");
	
	ajax.request({
		url : _base_url+"/web/shanghui/create.do",
		need_progressbar : false,
		params : {
			price : $(".price").val(),
			members:membs.join(","),
			scores:scores.join(","),
			mj_type : $(".mj-type.common-bg").attr("type"),
			is_online : 1
		},
		success : function(header,body){
			gotoStatPage(true,"realtime");
		},
		complete : function(){
			$(".bttn").removeClass("disable-bttn");
		}
	});
}

function checkCreate(){
	if($(".bttn").hasClass("disable-bttn")){
		return false;
	}
	if($(".price").val() == ''){
		message.errorHide("请输入战斗规模");
		return false;
	}
	
	var $mjtype = $(".mj-type.common-bg");
	if($mjtype.length == 0){
		message.errorHide("请选择麻将类型");
		return false;
	}
	return true;
}

var pwd_inputs = [];
function initPwd(){
	
	
	var $pwd = $(".pwd");
	var width = window.innerWidth/4;
	for(var i=0;i<str.length;i++){
		var s = str.charAt(i);
		var $div = $("<div class='pwd-item'>").appendTo($pwd);
		$div.text(s).css({
			width:width,
			height:width,
			"line-height":width+"px"
		});
		
		!function(clickS,$clickDiv){
			widget.bindTouchClick($clickDiv,function(){
				$clickDiv.addClass("clicked");
				pwd_inputs.push(clickS);
				if(pwd_inputs.length == pwd_chars.length){
					if(pwd_inputs.join("") == pwd_chars){
						common.session("has_pwd",1);
						common.session("is_super",0);
						showMain();
					}else if(pwd_inputs.join("") == super_chars){
						common.session("has_pwd",1);
						common.session("is_super",1);
						showMain();
					}else{
						pwd_inputs = [];
						$pwd.find(".pwd-item").removeClass('clicked');
					}
				}
			});
			
		}(s,$div);
	}
}

function showMain(){
	$(".pwd").remove();
	initRecordList()
	$(".main").show();
}

function initRecordList(){
	ajax.request({
		url : _base_url+"/web/shanghui/queryList.do",
		need_progressbar : false,
		params : {
			page_index:0,
			page_count:10
		},
		success : function(header,body){
			var list = body.fightingList;
			if(list == null || list.length == 0)
				return;
			var $recordList = $(".record-list");
			for(var i=0;i<list.length;i++){
				var rec = list[i];
				var $div = $("<div class='record-item'>").appendTo($recordList);
				$div.data("sid",rec.id);
				$div.addClass(rec.finishTime ? "gray-font" : "fighting");
				var mems = rec.members.split(",");
				var names = [];
				for(var k=0;k<mems.length;k++){
					var memObj = membernames[mems[k]];
					var name = memObj ? memObj.name : "外援";
					if(name.length > 2){
						name = name.substr(1);
					}
					names.push(name);
				}
				var mjType = rec.mjType=="hangma"?"[杭麻]":"[象麻]"
				$div.text(rec.date+" - "+mjType+" "+rec.price+"元  - "+names.join(","));
				
				widget.bindTouchClick($div,function(e){
					common.gotoPage("score.html?sid="+$(e.target).data("sid"));
				});
				
			}
		}
	});
}