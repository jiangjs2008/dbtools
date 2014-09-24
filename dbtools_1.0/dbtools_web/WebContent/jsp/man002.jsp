<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>查看表</title>
<script type="text/javascript" src="/dbm/js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="/dbm/js/operamasks-ui.js"></script>
<script type="text/javascript" src="/dbm/js/main.js"></script>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-default.css">
<script type="text/javascript">
dataLimit = ${dataLimit};
$(window).resize(function(){
	$("#west-panel").height($(window).height() - 27);
	$("#sqlscript").width($(window).width() - 262);
});

$(document).ready(function() {
	$("#west-panel").height($(window).height() - 27);
	$("#sqlscript").width($(window).width() - 262);

	$("body").omBorderLayout({
		spacing : 3, fit: true,
		panels:[{id:"north-panel", header:false, region:"north", height:220 },
				{id:"center-panel", header:false, region:"center" },
				{id:"west-panel", title:"数据库表一览", region:"west", width:250, expandToTop:true, expandToBottom:true, resizable:true }]
	});
	$("#north-panel").omBorderLayout({
		spacing : 0, fit: true,
		panels:[{id:"north-panel2", header:false, region:"north", height:180},
				{id:"center-panel2", header:false, region:"center"}]
	});

	$("#mytree2").omTree({
		dataSource : ${dbInfo},
		onExpand: onExpandNode,
		onDblClick : onDblClickNode,
		onRightClick : onRightClickNode,
		onClick: function(nodeData, event) {
			$("#menu").omMenu("hide");
			$("#menu2").omMenu("hide");
		}
	});

	$('#menu').omMenu({
		contextMenu : true, minWidth : 130,
		dataSource : [{id:'001', label:'查看表定义', icon:'css/icons/t1.png'},
					  {id:'002', label:'查看索引定义', icon:'css/icons/t2.png'}],
		onSelect : onSelectNode
	});
	$('#menu2').omMenu({
		contextMenu : true, minWidth : 130,
		dataSource : [{id:'003', label:'刷新', icon:'css/icons/reload.png'}],
		onSelect : onSelectGroup
	});
	// 左键点击页面隐藏菜单
	$("body").bind("click", function(){
		$("#menu").omMenu("hide");
		$("#menu2").omMenu("hide");
	});

	$(window).resize(function() {
		$('#grid').omGrid("resize");
	});

	$('#aButton').omButton({ onClick : execScript });
	$('#bButton').omButton({ onClick : sqlHistory });
	$('#cButton').omButton({ icons : {left:"css/icons/closed.png"}, onClick:logout });
});
</script>
</head>

<body>
<div id="west-panel">
	<ul id="mytree2"></ul>
</div>
<div id="north-panel" style="border-style:none;background-color:#d2e3ec">
	<div id="north-panel2" style="border-style:none;overflow:hidden;background-color:#d2e3ec">
		<textarea id="sqlscript" name="sqlscript" style="height:174px;color:#686868;background-color:#F0F0F0;border-bottom-color:#F0F0F0" onclick="optionsSql()" onFocus="if(value=='请在此输入SQL执行脚本：'){value='';this.style.color='#000'}" onBlur="if(!value){value='请在此输入SQL执行脚本：';this.style.color='#686868'}">请在此输入SQL执行脚本：</textarea>
	</div>
	<div id="center-panel2" style="border-style:none;background-color:#d2e3ec;padding-top:7px;border-top:1px solid #86A3C4">
		<div id="ptbdiv" style="float:left;">
			<span style="height:20px;padding-left:5px;line-height:20px;white-space: nowrap">当前选择：[<span id="tblname"></span>]</span>
			<input type="button" id="zButton" value="确认更新" style="width:60px;"/></div>
		<div style="float:right;padding-right:15px;">
			<a id="aButton" style="width:60px;">执行SQL</a>&nbsp;&nbsp;&nbsp;
			<a id="bButton" style="width:60px;">历史纪录</a>&nbsp;&nbsp;&nbsp;
			<a id="cButton" style="width:50px">退出</a></div>
	</div>
</div>
<div id="center-panel" style="border-style:none;overflow:hidden">
	<table id="grid" style="table-layout:fixed"></table>
</div>
<div id="menu"></div><div id="menu2"></div>
<div id="dialog">
	<table id="grid2"></table>
</div>
<form method="post" id="man002form" action="/dbm/logout.do"></form>
</body>
</html>