<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看表</title>
<link rel="stylesheet" type="text/css" href="/dbm/css/default/easyui.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/main.css">
<script type="text/javascript" src="/dbm/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="/dbm/js/jquery.easyui.min.js"></script>

</head>

<body>
<div class="easyui-layout" style="width:100%;height:100%;">
<div data-options="region:'west',split:true" title="West" style="width:100px;">
	<div style="height:20px;padding-left:5px;line-height:20px;background-color:#99FFCC;white-space: nowrap">当前选择：[<span id="tblname"></span>]</div>
	<ul id="mytree2" class="easyui-tree" url="/dbm/ajax/getcatalog.do"></ul>
	<div id="menu"></div>
</div>

<div data-options="region:'center'">
	<div class="easyui-layout" data-options="fit:true">
	    <div data-options="region:'north',border:false" style="height:140px">
			<div style="float:left;height:132px;padding-right:10px"><textarea id="sqlscript" name="sqlscript" cols="140" style="width:1000px;height:100%"></textarea></div>
			<div><form method="post" id="man002form" action="/dbm/logout.do">
				<ul style="list-style-type:none" >
				<li style="margin-top:25px"><input type="button" id="bButton" value="执行SQL" style="width:80px;"/></li>
				<li style="margin-top:25px"><input type="button" id="cButton" value="历史纪录" style="width:80px;"/></li></ul></form></div>
	    </div>
	    <div data-options="region:'center',border:false"><table id="grid"></table>
	    	<div>
	    	<div style="float:left;margin-left:60px"><form method="post" id="man002form" action="/dbm/logout.do">
				<input type="submit" id="aButton" value="退出" style="width:80px"/></form></div>
				<div style="float:right;margin-right:60px"><input type="button" id="cButton" value="确认更新" style="width:80px;"/></div></div>
		</div>
	</div>
</div>

</div>
</body>
</html>