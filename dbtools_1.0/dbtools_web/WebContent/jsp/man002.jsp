<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看表</title>
<script type="text/javascript" src="/dbm/js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="/dbm/js/operamasks-ui.js"></script>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-default.css">
<script type="text/javascript">

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
		onExpand: function(nodeData) {
			if (nodeData.isCatalog) {
				// 父结点，取得表一览
				if (!nodeData.isQuery) {
					// 未查询过
					$.ajax({
						url: '/dbm/ajax/gettbllist.do?catalog=' + nodeData.text + '&t=' + parseInt(Math.random()*100000),
						dataType: 'json',
						success: function(data){
							nodeData.isQuery = true;
							$("#mytree2").omTree("insert", data, nodeData);
						}
					});
				}
			}
		},
		onDblClick : function(nodeData, event){
			if (!nodeData.isCatalog) {
				// 查询指定表的数据
				var tblName = encodeURIComponent(nodeData.text);
				//首先从服务器端获取表头数据，再初始化数据表

					$("#tblname").text(nodeData.text),
					$("#grid").omGrid({
						limit: ${dataLimit},
						height: 'fit',
						width : 'fit',
						editMode:"all",
						autoColModel: true,
						autoFit: true,
						dataSource: "/dbm/ajax/griddata.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000),
						onAfterEdit:function(rowIndex , rowData){
							 alert("您刚刚编辑的记录索引为:" + rowIndex);
						}
					});

			}
		},
		onRightClick : function(nodedata, e) {
			$("#menu").omMenu("hide");
			$("#menu2").omMenu("hide");

			// 通过节点的text属性来判断是否响应右键菜单，也可以自行添加特殊属性来判断
				// 右键选中并显示菜单
			if (nodedata.hasChildren) {
				// 显示[刷新]
				$('#mytree2').omTree('select', nodedata);
				$('#menu2').omMenu('show', e);
			} else {
				$('#mytree2').omTree('select', nodedata);
				$('#menu').omMenu('show', e);
			}
			e.preventDefault();
		}
	});

	$('#menu').omMenu({
		contextMenu : true, minWidth : 130,
		dataSource : [{id:'001', label:'查看表定义', icon:'css/icons/t1.png'},
					  {id:'002', label:'查看索引定义', icon:'css/icons/t2.png'}],
		onSelect : function(item){
			var node = $("#mytree2").omTree("getSelected");
			var tblName = encodeURIComponent(node.text);

			$("#menu").omMenu("hide");
			if (item.id == "001") {
				//window.showModalDialog("/dbm/biz/inf001.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000), null, 'dialogWidth:850px;dialogHeight:450px;center:yes;toolbar:no; menubar:no; scrollbars:no;scroll:no');
	             $('#grid2').omGrid({
	                 dataSource : "/dbm/ajax/biz/inf001.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000),
	                 width : 'auto',
	                 height: 400,
					limit : 0,
					autoFit: false,
			        colModel : [ {header : '列名', name : 'colname', width : 200},
			                     {header : '类型名', name : 'type', width : 110},
			                     {header : '大小', name : 'size', width : 60},
			                     {header : '主键', name : 'pk', width : 30},
			                     {header : '为空', name : 'nullable', width : 30},
			                     {header : '注释', name : 'remark', width : 250} ]
	             });
	             $( "#dialog").omDialog({
	     			width : 950,
	     			height: 450,
	     			modal: true,
	     			resizable:false
	     		});

			} else if (item.id  == "002") {

			}
		}
	});
	$('#menu2').omMenu({
		contextMenu : true, minWidth : 130,
		dataSource : [{id:'003', label:'刷新', icon:'css/icons/reload.png'}],
		onSelect : function(item){
			var node = $("#mytree2").omTree("getSelected");
			var tblName = encodeURIComponent(node.text);

			//window.showModalDialog("/dbm/biz/inf001.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000), null, 'dialogWidth:850px;dialogHeight:450px;center:yes;toolbar:no; menubar:no; scrollbars:no;scroll:no');

		}
	});
	// 左键点击页面隐藏菜单
	$("body").bind("click", function(){
		$("#menu").omMenu("hide");
		$("#menu2").omMenu("hide");
	});

	$(window).resize(function() {
		$('#grid').omGrid("resize");
	});

	$('#bButton').omButton({});
	$('#bButton').click(function () {

	});
	$('#cButton').omButton({icons : {left:"css/icons/closed.png"}});
	$('#cButton').click(function () {
		// 退出
		document.forms[0].submit();
	});

	$('#aButton').omButton({});
	$('#aButton').click(function () {
		var sqlScript = $.trim($("#sqlscript").val());
		if (sqlScript == '请在此输入SQL执行脚本：') {
			return;
		}

		// 判断SQL类型
		var idx = sqlScript.indexOf(' ');
		if (idx == -1) {
			$.omMessageBox.alert({
				title: '注意',
				content: 'SQL语句不正确, 请重新输入.'
			});
			return;
		}
		var sqlType = sqlScript.substring(0, idx).toLocaleLowerCase();
		sqlScript = encodeURIComponent(sqlScript);

		var target = $('#mytree2').omTree('getSelected');
		if (target != null) {
			$('#mytree2').omTree('unselect', target);
		}
		$("#tblname").text('');

		if ('select' != sqlType) {

			$.getJSON("/dbm/ajax/sqlscript.do?sqlscript=" + sqlScript + '&t=' + parseInt(Math.random()*100000), function(data) {
				if (data.ecd == '1') {
				   $.omMessageBox.waiting({
					   content: 'SQL语句执行成功！'
				   });
				   setTimeout("$.omMessageBox.waiting('close');", 1000);

				} else if (data.ecd == '2') {
				   $.omMessageBox.alert({
					   content: 'SQL语句执行失败，请再次确认你的SQL语句'
				   });

				} else if (data.ecd == '3') {
				   $.omMessageBox.alert({
					   content: '执行SQL语句时发生错误，请再次确认你的SQL语句，并可查看LOG文件<br>' + data.msg
				   });

				}
			});
		} else {

			$("#grid").omGrid({
				limit: ${dataLimit},
				height: 'fit',
				width : 'fit',
				autoColModel: true,
				dataSource: "/dbm/ajax/sqlscript.do?sqlscript=" + sqlScript + '&t=' + parseInt(Math.random()*100000)
			});

		}
	});
});

function showMsg() {

}

</script>
</head>

<body>

<div id="west-panel">
	<ul id="mytree2"></ul>
</div>
<div id="north-panel" style="border-style:none;background-color:#d2e3ec">
	<div id="north-panel2" style="border-style:none;overflow:hidden;background-color:#d2e3ec">
		<textarea id="sqlscript" name="sqlscript" style="height:174px;color:#686868;background-color:#F0F0F0;border-bottom-color:#F0F0F0" onselect="showMsg()" onFocus="if(value=='请在此输入SQL执行脚本：'){value='';this.style.color='#000'}" onBlur="if(!value){value='请在此输入SQL执行脚本：';this.style.color='#686868'}">请在此输入SQL执行脚本：</textarea>
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
<div id="dialog" title="布局组件和弹出窗口组合">
    <table id="grid2"></table>
</div>

<form method="post" id="man002form" action="/dbm/logout.do"></form>
</body>
</html>