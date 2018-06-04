$(document).ready(function(e) {
	if(common.isPC()){
		$("body").addClass("pc-body")
	}
	initPage();
});

var startIndex = 0;
function initPage(){
	initRecordList();
	
	widget.bindTouchClick($(".loadmore .loadmore-bttn"),function(e){
		initRecordList();
	});
}

function initRecordList(){
	ajax.request({
		url : _base_url+"/web/shanghui/queryList.do",
		need_progressbar : false,
		params : {
			page_index:startIndex,
			page_count:20
		},
		success : function(header,body){
			var list = body.fightingList;
			if(list == null || list.length == 0){
				message.successHide("已无更多数据");
				return;
			}
			startIndex += list.length;	
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
				$div.text(rec.date+" - "+rec.price+"元  - "+names.join(","));
				
				widget.bindTouchClick($div,function(e){
					common.gotoPage("score.html?sid="+$(e.target).data("sid"));
				});
			}
		}
	});
}