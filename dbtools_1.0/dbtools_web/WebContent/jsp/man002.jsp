<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看表</title>
<script type="text/javascript" src="/dbm/js/jquery-1.6.4.min.js"></script>
<script type="text/javascript" src="/dbm/js/om-ui.js"></script>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-default.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/main.css">
<script type="text/javascript">

$(document).ready(function() {
	var element = $('body').omBorderLayout({
		panels:[{id:"center-panel", header:false, region:"center"},
				{id:"west-panel", resizable:true, collapsible:true, title:"数据库表一览", region:"west", width:"25%"}]
	});


	$("#mytree2").omTree({
		dataSource : ${dbInfo},
		onClick : function(nodeData, event){ },
		onDblClick : function(nodeData, event){
			if (nodeData.hasChildren) {
				// 父结点，取得表一览
				$.ajax({
					url: '/dbm/ajax/gettbllist.do?catalog=' + nodeData.text,
					method: 'POST',
					dataType: 'json',
					success: function(data){
						$("#mytree2").omTree("insert", data, nodeData);
					}
				});
			} else {
				// 查询指定表的数据
				//var url = encodeURI("griddata.do?method=filter&city="+nodedata.text);
				//$("#grid").omGrid("setData", url);

				//首先从服务器端获取表头数据，再初始化数据表
				$.getJSON("/dbm/ajax/gridcol.do?tblname=" + nodeData.text, function(data) {
					$("#grid").omGrid({
							limit: 25,
		height: 'fit',
		width : 'fit',
					title : '表名: ' + nodeData.text,
						dataSource: "/dbm/ajax/griddata.do?tblname=" + nodeData.text,
						colModel : data
					});
				});
			}
		}
	});

	$(window).resize(function() {
		$('#grid').omGrid("resize");
	});
});
</script>
</head>

<body>
<div id="center-panel" style="border:0px">
	<table id="grid"></table>
</div>
<div id="west-panel">
	<ul id="mytree2"></ul>
</div>
</body>
</html>