$(document).ready(function(e) {
	initPage();
});
var startDate;
var endDate;
var fightingList;
function initPage(){
	startMonth = common.getUrlParams("start_month");
	endMonth = common.getUrlParams("end_month");;
	
	var dates = initPeriodArea(startMonth,endMonth);
	startDate = dates[0];
	endDate = dates[1];
	
	loadData();
	
	
	
	var $statChart = $(".stat-total-chart");
	var $orderBys = $(".stat-list .order-by").children();
	widget.bindTouchClick($orderBys,function(e){
		$orderBys.removeClass("common-bg");
		$(e.target).toggleClass("common-bg");
		
		initStat(fightingList,$(e.target).attr("order-by"));
	});
	
}

function initPeriodArea(startMonth,endMonth){
	var dates = startMonth ? startMonth.split("-") : new Date().toStr("yyyy-MM").split("-");
	var year = parseInt(dates[0]);
	var startMonth = parseInt(dates[1]);
	if(startMonth % 2 == 0){
		startMonth -= 1;
	}
	if(endMonth == null){
		endMonth = startMonth+1;
	}else{
		endMonth = parseInt(endMonth.split("-")[1]);
	}
	startMonth = startMonth < 10 ? "0"+startMonth : startMonth;
	endMonth = endMonth < 10 ? "0"+endMonth : endMonth;	
	
	var $periods = $(".period-item");
	
	for(var k=0;k<$periods.length;k++){
		var month = $periods.eq(k).attr("start_month");
		if(month >= startMonth && month <= endMonth){
			$periods.eq(k).addClass("common-bg");
		}
	}
	
	
	widget.bindTouchClick($periods,function(e){
		$(e.target).toggleClass("common-bg");
		var deselected = !$(e.target).hasClass("common-bg");
		var selectMonth = $(e.target).attr("start_month");
		
		var $choosed = $(".period-item.common-bg");
		var startMonth = $choosed.first().attr("start_month");
		var endMonth = $choosed.last().attr("start_month");
		
		if(deselected && (selectMonth < startMonth || selectMonth > endMonth)){
			;//刚好两端的取消选择
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
		startMonth = $choosed.first().attr("start_month");
		endMonth = parseInt($choosed.last().attr("start_month"))+1;
		endMonth = endMonth < 10 ? "0"+endMonth : endMonth;
		
		var startDate = year+"-"+startMonth;
		var endDate = year+"-"+endMonth;
		common.gotoPage("award.html?start_month="+startDate+"&end_month="+endDate);
		
	});
	
	
	return [year+'-'+startMonth+"-01",year+'-'+endMonth+"-31"];
}

function loadData(){
	ajax.request({
		url : _base_url+"/web/shanghui/queryAward.do",
		need_progressbar : false,
		params : {
			start_date:startDate,
			end_date:endDate
		},
		success : function(header,body){
			fightingList = body.fightingList;
			initStat(body.fightingList,"sum");
			initAward(body.awardList,body.fightingList);
		}
	});
}
function initStat(fightingList,orderBy){
	var $statChart = $(".stat-total-chart");
	$statChart.width("100%");
	$statChart.height(300);
	
	var data = [];
	for(var key in membernames){
		if(key == "shui" || key == "huifei"){
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
			//次数相同，则胜率高排前面
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
			victoryDatas.push({y:d.victoryCount,color:"#00FF00"});
			negativeDatas.push({y:d.negativeCount,color:"#FF0000"});
		}else if(orderBy == "victory"){
			victoryDatas.push({y:d.victoryCount,color:"#00FF00"});
		}
	}
	
    Highcharts.chart($statChart.get(0), {
          chart: {
              type: 'bar'
          },
          title: {
              text: null,
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
                  text: orderBy == "sum" ? "金额(元)" : "次数"
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
                  pointWidth: 13 //柱子的宽度值 单位为px
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
			$tempDiv.find(".name-line .quota").text("(前"+award.quota+"名)");
		}
		$tempDiv.find(".name-line .reward").text("奖金："+award.sums.join("/")+"元");
		
		$tempDiv.find(".desc-line").text(award.desc);
		$tempDiv.find(".condition-line").text(award.condition);
		
		var $memlist = $tempDiv.find(".member-list");
		if(!award.members){
			$memlist.text("牛人还未出现").css("color","#FF0000");
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
				
				$memDiv.find(".name").text(memname+"：");
				$memDiv.find(".reward").text(memAward.sum+"元");
				if(memAward.sum == 0){
					$memDiv.removeClass("xsg-font").addClass("gray-font");
				}
				
				$memDiv.attr("id","mem");
				$memDiv.show();
				
				
				membs.push(memname);
				//if(award.orderBy == "count"){
				//countValues.push(fting.count);
				values.push({
					y:fting.count,color:memAward.sum==0?"#b3b3b3":null
				});
				
				//}else{
					//values.push(fting.sum);
				//}
			}
			
			var $chart = $tempDiv.find('.stat-chart');
			$chart.show();
			$chart.height(100+13*award.members.length);
			//绘制图表
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
		                  text: award.orderBy == "count" ? "次数" : "金额(元)",
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
		            	  pointWidth: 10 //柱子的宽度值 单位为px
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