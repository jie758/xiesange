$(document).ready(function(e) {
	if(common.isPC()){
		$("body").addClass("pc-body")
	}
	initPage();
});
var startDate;
var endDate;
var fightingList;
var huifei;
var shuil
//var orderBy;
var member;

var currentYear = null;
function initPage(){
	startMonth = common.getUrlParams("start_month");
	endMonth = common.getUrlParams("end_month");
	member = common.getUrlParams("member");
	var dates = initPeriodArea(startMonth,endMonth);
	startDate = dates[0];
	endDate = dates[1];
	
	initPeriodAreaClick();
	
	if(!startMonth || !endMonth){
		return;
	}
	
	loadData();
	
}
function initPeriodAreaClick(startMonth,endMonth){
	var $periods = $(".period-item");
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
		var startMonth = null;
		var endMonth = null;
		var url = "stat-member.html?member="+member;
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
			
			url += "&start_month="+startMonth+"&end_month="+endMonth;
		}
		common.gotoPage(url);
	});
}
function initPeriodArea(startMonth,endMonth){
	var $periods = $(".period-item");
	currentYear = new Date().toStr("yyyy-MM").split("-")[0];
	if(startMonth){
		var dates = startMonth ? startMonth.split("-") : new Date().toStr("yyyy-MM").split("-");
		var startYear = parseInt(dates[0]);
		var startMonth = parseInt(dates[1]);
		
		dates = endMonth.split("-");
		var endYear = parseInt(dates[0]);
		var endMonth = parseInt(dates[1]);
		
		for(var k=0;k<$periods.length;k++){
			var month = $periods.eq(k).attr("start_month");
			if(month >= startMonth && month <= endMonth){
				$periods.eq(k).addClass("common-bg");
			}
		}
		
		endMonth+=1;
		startMonth = startMonth < 10 ? "0"+startMonth : startMonth;
		endMonth = endMonth < 10 ? "0"+endMonth : endMonth;	
	}
	
	
	return [startYear+'-'+startMonth+"-01",endYear+'-'+endMonth+"-01"];
}

function loadData(){
	var url = "/web/shanghui/queryFightingList.do";
	ajax.request({
		url : _base_url+url,
		need_progressbar : false,
		params : {
			start_date:startDate,
			end_date:endDate,
			member : member
		},
		success : function(header,body){
			fightingList = body.fightingList;
			initStat(body.fightingList);
		}
	});
}
function initStat(fightingList){
	var $statChart = $(".stat-total-chart");
	$statChart.width("100%");
	$statChart.height(20*fightingList.length+50);
	
	var data = [];
	var nameLabels = [];
	if(fightingList){
		for(var i=0;i<fightingList.length;i++){
			var fting = fightingList[i];
			var memberObj = fting.members;
			var score = memberObj ? memberObj[member] : 0;
			data.push({y:score,color:score < 0 ? "#FF0000":"#00FF00",fid:fting.id});
			nameLabels.push(fting.date.toDate("-").toStr("MM/dd"));
		}
	}
	var seriesData = [{
		data: data,
        dataLabels: {
            enabled: true
        }
	}];
    Highcharts.chart($statChart.get(0), {
          chart: {
              type: 'bar'
          },
          title: {
              text: membernames[member].name+"实时战绩",
              style:{
                  "font-size":"1rem"
              }
          },
          subtitle: {
              text: startDate+" ~ "+endDate
          },
          xAxis: [{
              categories: nameLabels,
              reversed: true
          }],
          yAxis: {
              title: {
                  text: "金额(元)"
              }
          },
          legend: {
          	enabled: false
          },
          plotOptions: {
        	  series: {
                  stacking: 'normal',
                  pointWidth: 13 //柱子的宽度值 单位为px
              },
              bar: {
                  cursor: 'pointer',
                  events: {
                      click: function(e) {
                    	  var point = e.point;
                    	  var member = getMemberByName(point.category);
                    	  common.gotoPage("score.html?sid="+point.options.fid);
                      }
                  }
              }
          },
          series: seriesData
      });
	
};




