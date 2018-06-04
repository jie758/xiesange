$(document).ready(function(e) {
	if(common.isPC()){
		$("body").addClass("pc-body")
	}
	initPage();
});
var sid = null;
var price = null;
var shanghui = null;
function initPage(){
	sid = common.getUrlParams("sid");
	ajax.request({
		url : _base_url+"/web/shanghui/queryRoundList.do",
		need_progressbar : false,
		params : {
			shanghui_id:sid
		},
		success : function(header,body){
			var recList = body.recordList;
			shanghui = body.shanghui;
			var mems = shanghui.members.split(",");
			mems.push("shui");
			price = shanghui.price;
			initMembers(shanghui,mems);
			initRound(mems,body.roundList);
			
			refreshTotal(1);
			refreshTotal(2);
			if(shanghui.sum != null){
				$(".total-table .finish").removeClass("xsg-font");
				$(".total-table .finish").html("会费<br/>￥"+shanghui.sum);
			}
			console.log("sid:"+sid);
		}
	});
	widget.bindTouchClick($(".goto-index"),function(){
		common.gotoPage("index.html");
	});
	if(isSuper()){
		$(".remove-fighting").css("display","block");
		widget.bindTouchClick($(".remove-fighting"),function(){
			message.confirmSlide("删除后不可恢复，确认删除?",function(){
				ajax.request({
					url : _base_url+"/web/shanghui/remove.do",
					need_progressbar : false,
					params : {
						shanghui_id:sid,
					},
					success : function(header,body){
						common.gotoPage("index.html");
					}
				});
			});
		});
	}
	
	widget.bindTouchClick($(".finish"),function(){
		if(shanghui.sum > 0){
			return;
		}
		
		refreshTotal(2);
		if(shanghui.isOnline == 1){
			finish();
		}else{
			showSumInput();
		}
		
	});
	widget.bindTouchClick($(".found-sum .ok"),function(){
		if($(".found-sum input").val() === ""){
			message.errorHide("请输入会费");
			return;
		}
		finish();
	});
	
	$('.qrcode-img').qrcode({
	    width: 70, //宽度 
	    height:70, //高度 
	    text: window.location.href //任意内容 
	});
	
}

var $mask = null;
function showSumInput(){
	$mask = message.showMask(null,function(){
		message.hideMask($mask);
		$(".found-sum").hide();
		$(".found-sum input").blur();
	});
	$(".found-sum input").val("");
	$(".found-sum").show();
	$(".found-sum input").focus();
	var width = window.innerWidth;
	$(".found-sum").css({
		left:(width-$(".found-sum").width()-100)/2
	});
};



function initMembers(shanghui,members){
	var $table = $(".member-table");
	var isFinish = shanghui.sum != null;
	if(isFinish){
		$table.find("tr").eq(1).remove();
		$(".grade-table").css("margin-top","30px");
	}else{
		var $bttnSave = $table.find(".bttn.save");
		widget.bindTouchClick($bttnSave,function(){
			commitRound();
		});
	}
	var mjType = shanghui.mjType=="hangma"?"杭":"象" 
	$table.find(".price-td").text(mjType+"-"+shanghui.price+"元")
	var $memberTds = $table.find("tr").eq(0).find('td');
	var $inputs = isFinish ? null : $table.find("tr").eq(1).find('input');
	for(var i=0;i<members.length;i++){
		var name = membernames[members[i]].name;
		if(name.length > 2){
			name = name.substr(1);
		}
		$memberTds.eq(i+1).text(name);
		
		if($inputs != null){
			var $input = $inputs.eq(i);
			$input.attr("member",members[i]);
			$input.blur(function(e){
				var $this = $(e.target);
				var val = $this.val();
				///if(val === ''){
					autoCalc($this);
				//}
				
			});
			$input.keypress(function(e){
				var $this = $(e.target);
				//console.log("1-"+$this.val());
				if(e.keyCode == 13){
					$this.parent().next().find("input").select();
					autoCalc($this);
				}/*else if(e.keyCode == 126){
					$this.val("-");
					e.preventDefault();
				}
				$input.data("value",$this.val());*/
			});
			/*$input.keyup(function(e){
				var $this = $(e.target);
				console.log($this.data("value"));
				if(e.keyCode == 192){
					$this.val(-1*$this.val());
				}
				
			});*/
		}
	}
	
	
	
}
function initRound(members,roundList){
	if(roundList == null || roundList.length == 0){
		return;
	}
	var $table = $(".grade-table");
	for(var i=0;i<roundList.length;i++){
		var round = roundList[i];
		var $tr = $("<tr>").appendTo($table);
		$tr.data("rid",round.id);
		$tr.data("index",i+1);
		
		var $tdIndex = $("<td>").appendTo($tr).css("text-align","left");
		$tdIndex.text(" "+(i+1)+" "+round.cost+"分").css({
			"text-align":"left",
			"color":"#c8c8c8",
			"padding-left":"2px"
		});
		for(var k=0;k<members.length;k++){
			var mem = members[k];
			var grade = round.score[mem];
			var $td = $("<td>").appendTo($tr).text(grade);
			if(grade < 0){
				$td.css("color","#FF0000");
			}
		}
		!function($tr){
			widget.bindTouchClick($tdIndex,function(e){
				$table.find("td").removeClass("gray-bg");
				$tr.children().addClass("gray-bg");
				message.confirmSlide("删除第"+$tr.data("index")+"局记录?",function(){
					ajax.request({
						url : _base_url+"/web/shanghui/removeRound.do",
						need_progressbar : false,
						params : {
							round_id:$tr.data("rid"),
						},
						success : function(header,body){
							location.reload();
						}
					});
				});
			});
		}($tr)
		
	}
}



function refreshTotal(type){
	var totals = [0,0,0,0,0];
	var $table = $(".grade-table");
	var $trs = $table.find("tr");
	for(var i=0;i<$trs.length;i++){
		var $tds = $trs.eq(i).children();
		for(var k=1;k<$tds.length;k++){
			totals[k-1] += parseInt($tds.eq(k).text());
		}
	}
	
	var $tds_pian = $(".total-table tr").eq(0).children();
	var $tds_sum = $(".total-table tr").eq(1).children();
	for(var k=1;k<$tds_pian.length;k++){
		var totalgrade = totals[k-1];
		if(type == 1){
			$tds_pian.eq(k).text(totalgrade);
			if(totalgrade < 0){
				$tds_pian.eq(k).css("color","#FF0000");
			}
		}else if(type==2){
			$tds_sum.eq(k-1).text("￥"+totalgrade*price);
			if(totalgrade < 0){
				$tds_sum.eq(k-1).css("color","#FF0000");
			}
		}
	}
}

function autoCalc(){
	var $table = $(".member-table");
	var $inputs = $table.find(".item-input");
	var $blank = null;
	var sum = 0;
	for(var i=0;i<$inputs.length;i++){
		var $input = $inputs.eq(i);
		var grade = $input.val();
		if(grade === ''){
			if($blank == null){
				$blank = $input;
			}else{
				//alert("只有一个输入框为空，才能自动计算");
				return;
			}
		}else{
			sum += parseInt(grade);
		}
	}
	
	var $bttnSave = $(".bttn.save");
	if($blank != null){
		$bttnSave.removeClass("calc-error").text("保存");
		$blank.val(-1*sum);
	}else{
		if(sum != 0){
			$bttnSave.addClass("calc-error").text("有误");
		}else{
			$bttnSave.removeClass("calc-error").text("保存");
		}
		return;
	}
	
}

function finish(){
	ajax.request({
		url : _base_url+"/web/shanghui/finish.do",
		need_progressbar : false,
		params : {
			shanghui_id:sid,
			sum : $(".found-sum input").val()
		},
		success : function(header,body){
			if(shanghui.isOnline != 1){
				message.hideMask($mask);
				$(".found-sum").hide();
				$(".found-sum input").blur();
			}
			location.reload();
		}
	});
}

function commitRound(){
	var $inputs = $(".member-table .item-input");
	var gradeArr = [];
	var sum = 0;
	for(var i=0;i<$inputs.length;i++){
		var $input = $inputs.eq(i);
		var member = $input.attr("member");
		var grade = $input.val();
		if(grade === ''){
			message.errorHide("请输入"+membernames[member].name+"的战绩",null,null,{height:"29px","line-height":"29px"});
			return;
		}
		gradeArr.push(member+":"+grade);
		sum += parseInt(grade);
	}
	
	if(sum != 0){
		message.errorHide("数据不平",null,null,{height:"29px","line-height":"29px"});
		return;
	}
	
	ajax.request({
		url : _base_url+"/web/shanghui/commitRound.do",
		need_progressbar : false,
		params : {
			shanghui_id:sid,
			grade:gradeArr.join(",")
		},
		success : function(header,body){
			location.reload();
		}
	});
	
}
