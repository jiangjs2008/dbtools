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
	<div data-options="region:'west',split:true" title="Database" style="width:200px;">
		<div style="height:20px;padding-left:5px;line-height:20px;background-color:#99FFCC;white-space: nowrap">当前选择：[<span id="tblname"></span>]</div>
		<ul id="mytree2" class="easyui-tree" data-options="lines:true"></ul>
	</div>
	<div data-options="region:'center'">
	    <div data-options="region:'north',border:false" style="height:140px;border-bottom:2px solid #E6EEF8;">
	        <div class="easyui-layout" data-options="fit:true">
	            <div data-options="region:'west',border:false" style="width:1000px;overflow:hidden"><textarea id="sqlscript" name="sqlscript" style="width:98%;height:96%"></textarea></div>
	            <div data-options="region:'center',border:false" style="width:100%;margin-left:10px;overflow:hidden"><form method="post" id="man002form" action="/dbm/logout.do"><br/><br/>
					<input type="button" id="bButton" value="执行SQL" style="width:80px;"/><br/><br/><br/>
					<input type="button" id="cButton" value="历史纪录" style="width:80px;"/></form></div>
	        </div>
	    </div>
	    <div data-options="region:'center',border:false">
	    	<table id="grid"></table>
	    	<div style="float:left;margin-left:60px"><form method="post" id="man002form" action="/dbm/logout.do"><input type="submit" id="aButton" value="退出" style="width:80px"/></form></div>
			<div style="float:right;margin-right:60px"><input type="button" id="cButton" value="确认更新" style="width:80px;"/></div></div>
		</div>
	</div>
</div>
<script type="text/javascript">
var nowDate = new Date();

	$.getJSON("/dbm/ajax/getcatalog.do", function(subdata) {
		$('#mytree2').tree({
			data: subdata,
			onBeforeLoad:function(row,param){
				if (row) {
					$(this).tree('options').url = '....../?parentId=row.id'; // TODO-- 此处用法不明，没有这种处理的话会重复加载父节点
				}
			}
		});
	});

	$('#mytree2').tree({
		url:'/dbm/ajax/getcatalog.do',
		onDblClick: function(node) {
			if (node.hassaub) {
				// 点击的是父节点 
				$(this).tree('toggle', node.target);
			} else {
				// 点击的是表，显示该表数据
				var tblName = encodeURIComponent(nodeData.text);
				$.getJSON("/dbm/ajax/gridcol.do?tblname=" + tblName + '&t=' + parseInt(Math.random()*100000), function(data) {
					$("#tblname").text(nodeData.text),
					$("#grid").omGrid({
						limit: 100,
						height: 600,
						width : 'fit',
						editMode:"insert",
						dataSource: "/dbm/ajax/griddata.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000),
						colModel : data
					});
				});
			}
		},
		onExpand: function(node) {
			if (!node.hasdata) {
				// 如果没有加载子节点，则加载
				// append some nodes to the selected node
				//var selected = $('#mytree2').tree('getSelected');
				$.getJSON("/dbm/ajax/gettbllist.do?catalog=" + node.text + '&t=' + nowDate.getTime(), function(subdata) {
					$('#mytree2').tree('append', {
						parent: node.target,
						data: subdata
					});
				});
				node.hasdata = true;
			}
		}
	});

</script>
</body>
</html>