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
	var element = $("body").omBorderLayout({
		spacing : 3,
		panels:[{id:"north-panel", header:false, region:"north", height:120},
				{id:"center-panel", header:false, region:"center"},
				{id:"west-panel", resizable:true, collapsible:true, title:"数据库表一览", region:"west", width:200, expandToTop:true}]
	});

	$("#mytree2").omTree({
		dataSource : ${dbInfo},
		onClick : function(nodeData, event){ },
		onDblClick : function(nodeData, event){
			if (nodeData.hasChildren) {
				// 父结点，取得表一览
				$.ajax({
					url: '/dbm/ajax/gettbllist.do?catalog=' + nodeData.text,
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
					$("#tblname").text(nodeData.text),
					$("#grid").omGrid({
						limit: 25,
						height: 'fit',
						width : 'fit',
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

	$('#aButton').omButton({});
	$('#aButton').click(function () {
		document.forms[0].submit();
	});
});
</script>
</head>

<body>

<div id="west-panel">
<div style="height:20px;padding-left:5px;line-height:20px;background-color:#99FFCC;">当前选择：[<span id="tblname"></span>]</div>
	<ul id="mytree2"></ul>
</div>
<div id="north-panel" style="border-bottom-style:none">
<form method="post" id="man002form" action="/dbm/logout.do">
	<input type="button" id="aButton" value="退出" style="width:60px"/>
</form>
</div>
<div id="center-panel" style="border:0px">
	<table id="grid"></table>
</div>

</body>
</html>