<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看表</title>
<link rel="stylesheet" type="text/css" href="/dbm/css/default/easyui.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/icon.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/main.css">
<script type="text/javascript" src="/dbm/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="/dbm/js/jquery.easyui.min.js"></script>
<script type="text/javascript">

function logout() {
	var rslt = false;
	$.messager.confirm('消息框', '确定要退出?', function(r) {
		if (r) {
			document.forms[0].submit();
			rslt = true;
		} else {
			rslt = false;
		}
	});
	return rslt;
}

</script>
</head>

<body>
<div class="easyui-layout" style="width:100%;height:100%;">
	<div data-options="region:'west',split:true" title="Database" style="width:200px;">
		<ul id="mytree2" class="easyui-tree" data-options="lines:true"></ul>
	</div>
	<div data-options="region:'center'" style="overflow-y:hidden">
	    <div data-options="region:'north',border:false" style="height:182px;border-bottom:2px solid #E6EEF8;">
	        <div id="tblname" class="easyui-layout easyui-panel" title="My Panel" data-options="iconCls:'icon-save',closable:true,fit:true,border:false">
	            <div data-options="region:'west',border:false" style="width:88%;overflow:hidden"><textarea id="sqlscript" name="sqlscript" style="width:98%;height:150px"></textarea></div>
	            <div data-options="region:'center',border:false" style="width:12%;margin-left:8px;overflow:hidden"><br/><br/>
					<input type="button" id="bButton" value="执行SQL" style="width:80px;"/><br/><br/><br/>
					<input type="button" id="cButton" value="历史纪录" style="width:80px;"/></div>
	        </div>
	    </div>
	    <div data-options="region:'center',border:false">
	    	<table id="grid" class="easyui-datagrid" style="width:800px;height:400px;"></table>
		</div>
	</div>
</div>
<form method="post" id="man002form" action="/dbm/logout.do"></form>
<script type="text/javascript">

var nowDate = new Date();

	$.getJSON("/dbm/ajax/getcatalog.do", function(subdata) {
		$('#mytree2').tree({
			data: subdata,
			onBeforeLoad:function(row,param){
				if (row) {
					$(this).tree('options').url = '....../?parentId=row.id'; // TODO-- 此处用法不明，没有这种处理的话会重复加载父节点
				}
			},
			onDblClick: function(node) {
				if (node.hassub) {
					// 点击的是父节点 
					$(this).tree('toggle', node.target);
				} else {
					// 点击的是表，显示该表数据
					var tblName = encodeURIComponent(node.text);
					$.getJSON("/dbm/ajax/gridcol.do?tblname=" + tblName + '&t=' + parseInt(Math.random()*100000), function(coldata) {
						$("#tblname").panel('setTitle', tblName);
						$('#grid').datagrid({
							url: "/dbm/ajax/griddata.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000),
							 columns: coldata,
							 width:function(){return document.body.clientWidth*0.9},
							 pageSize:50,rownumbers:true,fitColumns:true,singleSelect:true
						});
					});
				}
			},
			onExpand: function(node) {
				if (!node.hasdata) {
					// 如果没有加载子节点，则加载
					// append some nodes to the selected node
					//var selected = $('#mytree2').tree('getSelected');
					$.getJSON("/dbm/ajax/gettbllist.do?catalog=" + node.text + '&t=' + nowDate.getTime(), function(subdata2) {
						$('#mytree2').tree('append', {
							parent: node.target,
							data: subdata2
						});
					});
					node.hasdata = true;
				}
			}
		});
	});

	$('#tblname').panel({
		onBeforeClose:logout
	});

</script>
</body>
</html>