$(document).ready(function(e) {
	if(common.isPC()){
		$("body").addClass("pc-body")
	}
	initPage();
});
var startDate;
var endDate;
var startMonth;
var endMonth;
var fightingList;
var huifei;
var shuil
var orderBy;
var currentYear = null;
var isOnline = null;

function initPage(){
	startMonth = common.getUrlParams("start_month");
	endMonth = common.getUrlParams("end_month");
	isOnline = common.getUrlParams("is_online")==1;
	if(!isOnline){
		var dates = initPeriodArea(startMonth,endMonth);
		startDate = dates[0];
		endDate = dates[1];
	}else{
		startDate = common.getUrlParams("start_date");
		endDate = common.getUrlParams("end_date");
	}
	if(isOnline){
		$(".date-table").css("display","block");
		initOnlineAreaClick();
	}else{
		$(".period-table").css("display","block");
		initOfflineAreaClick();
	}
	orderBy = common.getUrlParams("order_by") || "sum";
	$(".order-by").children(".item."+orderBy).addClass("common-bg");
	
	if(!startDate || !endDate){
		return;
	}
	
	loadData(orderBy);
	
	var $statChart = $(".stat-total-chart");
	var $orderBys = $(".stat-list .order-by").children(".item");
	widget.bindTouchClick($orderBys,function(e){
		$orderBys.removeClass("common-bg");
		$(e.target).toggleClass("common-bg");
		
		var newOrderBy = $(e.target).attr("order-by");
		if(isOnline){
			common.gotoPage("stat.html?is_online=1&start_date="+startDate+"&end_date="+endDate+"&order_by="+newOrderBy);
		}else{
			common.gotoPage("stat.html?start_month="+startMonth+"&end_month="+endMonth+"&order_by="+newOrderBy);
		}
		
		orderBy = newOrderBy;
	});
	
	widget.bindTouchClick($(".qrcode"),function(e){
		$('.qrcode-img').qrcode({
			//render: "table", //table鏂瑰紡 
		    width: $('.qrcode-img').width(), //瀹藉害 
		    height:$('.qrcode-img').height(), //楂樺害 
		    text: window.location.href //浠绘剰鍐呭 
		});
		$('.qrcode-img').show();
	});
}

function initRealtimeHeaderFix(){
	if(orderBy == "realtime"){
		var $realtimeHeadertr = $(".realtime-table.content").find("tr").first();
		var trbottom = $realtimeHeadertr.offset().top+$realtimeHeadertr.height();
		var $fixtable = $(".realtime-table.fixed");
		$(window).scroll(function(e){
			var scrollTop = $(window).scrollTop();
			if(scrollTop > trbottom){
				$fixtable.show();
			}else{
				$fixtable.hide();
			}
		});
		$(".realtime-table.fixed").width($(".realtime-table.content").width());
	}
}

function initOnlineAreaClick(){
	var $datetable = $(".date-table");
	var $prevBttn = $datetable.find(".prev-date");
	var $nextBttn = $datetable.find(".next-date");
	var $starDate = $datetable.find(".start-date");
	var $endDate = $datetable.find(".end-date");
	
	$starDate.val(startDate);
	$endDate.val(endDate);
	
	widget.bindTouchClick($prevBttn,function(e){
		var $input = $(e.target).parent().next().children().eq(0);
		var date = $input.val().toDate("-");
		startDate = date.offsetDate(-1).toStr("yyyy-MM-dd");
		$input.val(startDate);
	});
	widget.bindTouchClick($nextBttn,function(e){
		var $input = $(e.target).parent().prev().children().eq(0);
		var date = $input.val().toDate("-");
		endDate = date.offsetDate(1).toStr("yyyy-MM-dd");
		$input.val(endDate);
	});
	
	
	
	widget.bindTouchClick($(".query"),function(e){
		startDate = $starDate.val();
		endDate = $endDate.val();
		redirect();
	});
	
	widget.bindTouchClick($(".date-select"),function(e){
		widget.popupMenu(null,"bottom top",[{
			text:"最近3天",
			handler:function(){
				var date = new Date();
				startDate = date.offsetDate(-2).toStr("yyyy-MM-dd");
				endDate = date.toStr("yyyy-MM-dd")
				redirect();
			}
		},{
			text:"最近7天",
			handler:function(){
				var date = new Date();
				startDate = date.offsetDate(-6).toStr("yyyy-MM-dd");
				endDate = date.toStr("yyyy-MM-dd")
				redirect();
			}
		},{
			text:"最近1月",
			handler:function(){
				var date = new Date();
				startDate = date.offsetDate(-29).toStr("yyyy-MM-dd");
				endDate = date.toStr("yyyy-MM-dd")
				redirect();
			}
		},{
			text:"最近3月",
			handler:function(){
				var date = new Date();
				startDate = date.offsetDate(-90).toStr("yyyy-MM-dd");
				endDate = date.toStr("yyyy-MM-dd")
				redirect();
			}
		}],{
			need_titlebar:"选择区间"
		});
	});
	
}

function redirect(){
	var url = null;
	if(isOnline){
		url = "stat.html?order_by="+orderBy+"&is_online=1&start_date="+startDate+"&end_date="+endDate;
	}else{
		url = "stat.html?order_by="+orderBy+"&is_online=0&start_month="+startMonth+"&end_month="+endMonth;
	}
	common.gotoPage(url);
}

function initOfflineAreaClick(startMonth,endMonth){
	var $periods = $(".period-item");
	widget.bindTouchClick($periods,function(e){
		$(e.target).toggleClass("common-bg");
		var deselected = !$(e.target).hasClass("common-bg");
		var selectMonth = $(e.target).attr("start_month");
		
		var $choosed = $(".period-item.common-bg");
		var startMonth = $choosed.first().attr("start_month");
		var endMonth = $choosed.last().attr("start_month");
		
		if(deselected && (selectMonth < startMonth || selectMonth > endMonth)){
			;//鍒氬ソ涓ょ鐨勫彇娑堥�夋嫨
		}else{
			for(var k=0;k<$periods.length;k++){
				var month = $periods.eq(k).attr("start_month");
				if(deselected && month <= selectMonth){
					$periods.eq(k).removeClass("common-bg");
				}else if(month > startMonth && month < endMonth){
					$periods.eq(k).addClass("common-bg");
				}
			}
		}
		
		
		$choosed = $(".period-item.common-bg");
		var startMonth = null;
		var endMonth = null;
		var url = "stat.html";
		if($choosed.length == 0){
			startMonth = null;
			startMonth = null;
		}else{
			startMonth = parseInt($choosed.first().attr("start_month"));
			endMonth = parseInt($choosed.last().attr("start_month"))+1;
			
			startMonth = startMonth < 10 ? "0"+startMonth : startMonth;
			endMonth = endMonth < 10 ? "0"+endMonth : endMonth;
			
			startMonth = currentYear+"-"+startMonth;
			endMonth = currentYear+"-"+endMonth;
			
			url += "?order_by="+orderBy+"&start_month="+startMonth+"&end_month="+endMonth;
		}
		common.gotoPage(url);
	});
}
function initPeriodArea(startMonth,endMonth){
	var $periods = $(".period-item");
	var currentYears = new Date().toStr("yyyy-MM").split("-");
	currentYear = currentYears[0];
	
	if(!startMonth){
		return [];
		
	}
	
	if(startMonth){
		var dates = startMonth ? startMonth.split("-") : currentYears;
		var startYear = parseInt(dates[0]);
		startMonth = parseInt(dates[1]);
		
		dates = endMonth.split("-");
		var endYear = parseInt(dates[0]);
		var endMonth = parseInt(dates[1]);
		
		
		
		for(var k=0;k<$periods.length;k++){
			var month = $periods.eq(k).attr("start_month");
			if(month >= startMonth && month <= endMonth){
				$periods.eq(k).addClass("common-bg");
			}
		}
	}
	endMonth+=1;
	startMonth = startMonth < 10 ? "0"+startMonth : startMonth;
	endMonth = endMonth < 10 ? "0"+endMonth : endMonth;	
	
	return [startYear+'-'+startMonth+"-01",endYear+'-'+endMonth+"-01"];
}

function loadData(orderBy){
	var url = orderBy=='realtime' || orderBy=='daily' ?"/web/shanghui/queryFightingList.do"
			:"/web/shanghui/queryAward.do"
	ajax.request({
		url : _base_url+url,
		need_progressbar : false,
		params : {
			start_date:startDate,
			end_date:endDate,
			is_online : isOnline?1:0
			//group_by : orderBy=='daily' ? 'daily' : 'realtime'
		},
		success : function(header,body){
			fightingList = body.fightingList;
			if(orderBy=='realtime' || orderBy=='daily'){
				drawRealtime(fightingList,orderBy);
			}else{
				huifei = body.huifei;
				shui = body.shui;
				initStat(body.fightingList,orderBy);
				initAward(body.awardList,body.fightingList);
				
				$('.huifei-sum').text(huifei);
				$('.huifei').hide();
			}
			
			
		}
	});
}
function initStat(fightingList,orderBy){
	$(".realtime-list").hide();
	var $statChart = $(".stat-total-chart");
	$statChart.show();
	
	$statChart.width("100%");
	$statChart.height(300);
	
	var data = [];
	for(var key in membernames){
		if(key == "shui" || key == "huifei"){
			continue;
		}
		if(membernames[key].status === 0){
			continue;
		}
		var fting = common.matchItem(key,fightingList,"member");
		var sum = (fting && fting.sum) || 0;
		var count = (fting && fting.count) || 0;
		var victoryCount = (fting && fting.victoryCount) || 0;
		var ngtvCount = (fting && fting.negativeCount) || 0;
		data.push(fting || {member:key});
	}
	data = data.sort(function(a,b){
		if(orderBy == "sum"){
			return (a.sum || 0) - (b.sum || 0) ;
		}else if(orderBy == "count"){
			var result = (a.count || 0) - (b.count || 0);
			//娆℃暟鐩稿悓锛屽垯鑳滅巼楂樻帓鍓嶉潰
			return  result == 0 ? (a.victoryCount || 0) - (b.victoryCount || 0) : result;
		}else if(orderBy == "victory"){
			return (a.victoryCount || 0) - (b.victoryCount || 0) ;
		}
	});
	
	var nameLabels = [];
	var victoryDatas = [];
	var negativeDatas = [];
	var countDatas = [];
	var sumDatas = [];
	
	
	var seriesData = [];
	
	if(orderBy == "sum"){
		//sumDatas.push({y:huifei,color:"#dddddd"});
		//nameLabels.push(membernames.huifei.name);
		seriesData.push({
			data: sumDatas,
            dataLabels: {
                enabled: true
            }
		});
	}else{
		seriesData.push({
			data: negativeDatas,
            dataLabels: {
                enabled: true
            }
		});
		seriesData.push({
			data: victoryDatas,
            dataLabels: {
                enabled: true
            }
		});
	}
	
	for(var i=0;i<data.length;i++){
		var d = data[i];
		var membInfo = membernames[d.member];
		nameLabels.push(membInfo.name);
		
		if(orderBy == "sum"){
			sumDatas.push({y:d.sum,color:membInfo.color});
		}else if(orderBy == "count"){
			victoryDatas.push({y:d.victoryCount,color:"#FF0000"});
			negativeDatas.push({y:d.negativeCount,color:"#00FF00"});
		}else if(orderBy == "victory"){
			victoryDatas.push({y:d.victoryCount,color:"#FF0000"});
		}
	}
	
    Highcharts.chart($statChart.get(0), {
          chart: {
              type: 'bar'
          },
          title: {
              text: startDate+" ~ "+endDate,
              style:{
                  "font-size":"1rem"
              }
          },
          subtitle: {
              text: null
          },
          xAxis: [{
              categories: nameLabels,
              reversed: false,
              labels: {
                  step: 1
              }
          }],
          yAxis: {
              title: {
                  text: orderBy == "sum" ? "閲戦(鍏�)" : "娆℃暟"
              },
              stackLabels: {
                  enabled: orderBy == "count",
                  style: {
                      fontWeight: 'bold',
                      color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                  }
              }
          },
          
          legend: {
          	enabled: false
          },
          plotOptions: {
        	  series: {
                  stacking: 'normal',
                  pointWidth: 13 //鏌卞瓙鐨勫搴﹀�� 鍗曚綅涓簆x
              },
              bar: {
                  cursor: 'pointer',
                  events: {
                      click: function(e) {
                    	  if(isOnline){
                    		  return;
                    	  }
                    	  var point = e.point;
                    	  var member = getMemberByName(point.category);
                    	  common.gotoPage("stat-member.html?member="+member+"&start_month="+(startMonth||'')+"&end_month="+(endMonth||''));
                      }
                  }
              }
          },
          series: seriesData
      });
	
};




function initAward(awardList,ftingList){
	var $awardList = $(".award-list");
	
	for(var i=0;i<awardList.length;i++){
		if(i > 0){
			$("<div>").addClass("separator_h").appendTo($awardList).css("margin-bottom","20px");
		}
		var award = awardList[i];
		var $tempDiv = $("#award_template").clone().appendTo($awardList);
		
		$tempDiv.find(".name-line .name").text(award.name);
		if(award.quota){
			$tempDiv.find(".name-line .quota").text("(鍓�"+award.quota+"鍚�)");
		}
		$tempDiv.find(".name-line .reward").text("濂栭噾锛�"+award.sums.join("/")+"鍏�");
		
		$tempDiv.find(".desc-line").text(award.desc);
		$tempDiv.find(".condition-line").text(award.condition);
		
		var $memlist = $tempDiv.find(".member-list");
		if(!award.members){
			$memlist.text("鐗涗汉杩樻湭鍑虹幇").css("color","#FF0000");
		}else{
			var membs = [];
			var values = [];
			
			var countValues = [];
			var ngtvCountValues = [];
			var sumValues = [];
			
			for(var k=0;k<award.members.length;k++){
				var memAward = award.members[k];
				var member = memAward.member;
				var memname = membernames[member] && membernames[member].name;
				if(memname == null){
					continue;
				}
				var fting = common.matchItem(member,ftingList,"member");
				
				var $memDiv = $("#member_template").clone().appendTo($memlist);
				
				$memDiv.find(".name").text(memname+"锛�");
				$memDiv.find(".reward").text(memAward.sum+"鍏�");
				if(memAward.sum == 0){
					$memDiv.removeClass("xsg-font").addClass("gray-font");
				}
				
				$memDiv.attr("id","mem");
				$memDiv.show();
				
				
				membs.push(memname);
				//if(award.orderBy == "count"){
				//countValues.push(fting.count);
				var yvalue = null;
				if(award.name == "鐗虹壊濂�" || award.name == "鍙戣储濂�"){
					yvalue = fting.sum;
				}else{
					yvalue = fting.count;
				}
				values.push({
					y:yvalue,color:memAward.sum==0?"#b3b3b3":null
				});
				
				//}else{
					//values.push(fting.sum);
				//}
			}
			
			var $chart = $tempDiv.find('.stat-chart');
			$chart.show();
			$chart.height(100+13*award.members.length);
			//缁樺埗鍥捐〃
			Highcharts.chart($chart.get(0), {
		          chart: {
		              type: 'bar'
		          },
		          title: {
		              text: null
		          },
		          subtitle: {
		              text: null,
		          },
		          xAxis: [{
		              categories: membs,
		              reversed: true,
		              labels: {
		                  step: 1
		              }
		          }],
		          yAxis: {
		              title: {
		                  text: award.orderBy == "count" ? "娆℃暟" : "閲戦(鍏�)",
		                  style:{
		                      "font-size":"0.9rem"
		                  }
		              }
		          },
		          legend: {
		            	enabled: false
		          },
		          plotOptions: {
		              bar: {
		                  dataLabels: {
		                      enabled: true
		                  }
		              },
		              series: {
		            	  pointWidth: 10 //鏌卞瓙鐨勫搴﹀�� 鍗曚綅涓簆x
		              }
		          },

		          series: [{
		              data: values
		          }]
		      });
		}
		$tempDiv.attr("id","award");
		$tempDiv.show();
		
	}
}
function drawRealtime(fightingList,orderBy){
	$(".stat-total-chart").hide();
	
	$(".realtime-list").show();
	var $realtime = $(".realtime-list table");
	
	var $memberchoose = $(".member-choose");
	if($memberchoose.children().length == 0){
		var index = 1;
		for(var key in membernames){
			var membInfo = membernames[key];
			if(!membInfo.short || membInfo.status === 0){
				continue;
			}
			var $span = $("<div>").appendTo($memberchoose).attr("member",key);
			$span.text(membInfo.name.substr(0,2)).addClass("item");
			if(index < 10 && membInfo.realtime !== 0){
				$span.addClass("common-bg");
			}
			index++;
		}
		
		var $items = $memberchoose.children();
		widget.bindTouchClick($items,function(e){
			$(e.target).toggleClass("common-bg");
			drawRealtimeTableData(fightingList,orderBy);
		});
	}
	
	drawRealtimeTableData(fightingList,orderBy);
	
}

function groupByDaily(fightingList){
	if(fightingList == null || fightingList.length == 0){
		return null;
	}
	var scores = [];
	for(var i=0;i<fightingList.length;i++){
		var ft = fightingList[i];
		var memberScores = ft.members;
		var date = ft.date;
		var lastFt = scores.length == 0 ? null : scores[scores.length-1];
		if(lastFt == null || lastFt.date != date){
			lastFt = {date:date,members:{}};
			scores.push(lastFt);
		}
		for(var key in memberScores){
			if(lastFt.members[key] === undefined){
				lastFt.members[key] = memberScores[key];
			}else{
				lastFt.members[key] = lastFt.members[key]+memberScores[key];
			}
		}
		
	}
	//console.log(scores);
	return scores;
}

function drawRealtimeTableData(fightingList,orderBy){
	if(orderBy == 'daily'){
		fightingList = groupByDaily(fightingList);
	}
	var $realtimeTable = $(".realtime-table.content");
	var $realtimeTableFixed = $(".realtime-table.fixed");
	$realtimeTable.empty();
	$realtimeTableFixed.empty();
	
	var $members = $(".member-choose").children(".common-bg");
	
	var $headeTr = $("<tr>").appendTo($realtimeTable);
	var $td = $("<td>").appendTo($headeTr).html("&nbsp;").width("35px");
	var leftTdWidth = ($realtimeTable.width() - 35)/$members.length;
	
	
	var $headeTrFix = $("<tr>").appendTo($realtimeTableFixed);
	$("<td>").appendTo($headeTrFix).html("&nbsp;").width("35px");
	
	for(var i=0;i<$members.length;i++){
		var $memb = $members.eq(i);
		var memb = $memb.attr("member");
		
		var $td = $("<td>").appendTo($headeTr).addClass("realtime-head-td");
		$td.text(membernames[memb].name.substr(0,2));
		
		var $td = $("<td>").appendTo($headeTrFix).addClass("realtime-head-td");
		$td.text(membernames[memb].name.substr(0,2));
	}
	
	//var $bodyTr = 
	if(fightingList == null || fightingList.length == 0){
		return;
	}
	var colorNeg = {
		"1":"#00FF00",
		"2":"#04d404",
		"3" : "#016701"
	}
	var colorPosi = {
		"1":"#fd3232",
		"2":"#ce2206",
		"3" : "#941703"
	}
	for(var i=0;i<fightingList.length;i++){
		var ft = fightingList[i];
		var $tr = $("<tr>").appendTo($realtimeTable);
		$tr.data("fighting",ft);
		$tr.attr("fid",ft.id)
		var memberScores = ft.members;
		var dates = ft.date.split("-");
		var timeStr = dates[2]+"/"+dates[1];
		if(orderBy != 'daily'){
			var times = ft.createTime.split(" ")[1].split(":");
			timeStr += "<BR/>"+times[0]+":"+times[1];
		}
		var $datetd = $("<td>").appendTo($tr).html(timeStr).addClass("gray-font");
		$datetd.css("font-size","0.8rem");
		for(var k=0;k<$members.length;k++){
			var $memb = $members.eq(k);
			var memb = $memb.attr("member");
			var score = memberScores[memb];
			
			var $td = $("<td>").appendTo($tr).addClass("realtime-score-td");
			
			if(score === undefined){
				$td.html("&nbsp;");
			}else{
				$td.text(score);
				if(score <= -1000){
					$td.css("font-size","0.8rem");
				}
			}
			var color = null;
			if(score < 0){
				if(score >= -500){
					color = colorNeg["1"];
				}else if(score < -500 && score >= -1000){
					color = colorNeg["2"];
				}else if(score < -1000){
					color = colorNeg["3"];
				}
				$td.css({
					"background-color":color,
					"color":"#000",
				});
			}else if(score > 0){
				if(score <= 500){
					color = colorPosi["1"];
				}else if(score > 500 && score <= 1000){
					color = colorPosi["2"];
				}else if(score > 500){
					color = colorPosi["3"];
				}
				$td.css({
					"background-color":color,
					"color":"#FFF",
				});
			}
		}
		
		widget.bindTouchClick($datetd,function(e){
			var $tr = $(e.currentTarget).parent();
			if(orderBy == 'daily'){
				var ft = $tr.data("fighting");
				common.gotoPage("stat.html?is_online=1&start_date="+ft.date+"&end_date="+ft.date+"&order_by=realtime");
			}else{
				common.gotoPage("score.html?sid="+$tr.attr("fid"));
			}
			
		});
	}
	initRealtimeHeaderFix();
}
