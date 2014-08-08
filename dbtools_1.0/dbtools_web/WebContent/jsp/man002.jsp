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
<script type="text/javascript">
var nowDate = new Date();
$(document).ready(function() {
	var element = $("body").omBorderLayout({
		spacing : 3,
		panels:[{id:"north-panel", header:false, region:"north", height:180},
				{id:"center-panel", header:false, region:"center"},
				{id:"west-panel", resizable:true, collapsible:true, title:"数据库表一览", region:"west", width:250, expandToTop:true}]
	});

	$("#mytree2").omTree({
		dataSource : ${dbInfo},
		onExpand: function(nodeData) {
			if (nodeData.isCatalog) {
				// 父结点，取得表一览
				if (!nodeData.isQuery) {
					// 未查询过
					$.ajax({
						url: '/dbm/ajax/gettbllist.do?catalog=' + nodeData.text + '&t=' + nowDate.getTime(),
						dataType: 'json',
						success: function(data){
							nodeData.isQuery = true;
							$("#mytree2").omTree("insert", data, nodeData);
						}
					});
				}
			} else {
				// 查询指定表的数据
				var tblName = encodeURIComponent(nodeData.text);
				//首先从服务器端获取表头数据，再初始化数据表
				$.getJSON("/dbm/ajax/gridcol.do?tblname=" + tblName + '&t=' + nowDate.getTime(), function(data) {
					$("#tblname").text(nodeData.text),
					$("#grid").omGrid({
						limit: 100,
						height: 600,
						width : 'fit',
						editMode:"insert",
						dataSource: "/dbm/ajax/griddata.do?tblname=" + tblName + "&t=" + nowDate.getTime(),
						colModel : data
					});
				});
			}
		},
		onDblClick : function(nodeData, event){
			if (!nodeData.isCatalog) {
				// 查询指定表的数据
				var tblName = encodeURIComponent(nodeData.text);
				//首先从服务器端获取表头数据，再初始化数据表
				$.getJSON("/dbm/ajax/gridcol.do?tblname=" + tblName + '&t=' + nowDate.getTime(), function(data) {
					$("#tblname").text(nodeData.text),
					$("#grid").omGrid({
						limit: 100,
						height: 'fit',
						width : 'fit',
						dataSource: "/dbm/ajax/griddata.do?tblname=" + tblName + "&t=" + nowDate.getTime(),
						colModel : data,
						onAfterEdit:function(rowIndex , rowData){
					         alert("您刚刚编辑的记录索引为:" + rowIndex);
					    }
					});
				});
			}
		},
        onRightClick : function(nodedata, e){
        	// 通过节点的text属性来判断是否响应右键菜单，也可以自行添加特殊属性来判断
        	if (!nodedata.hasChildren) {
        		// 右键选中并显示菜单
        		$('#mytree2').omTree('select', nodedata);
          	    $('#menu').omMenu('show', e);
        	}
        	e.preventDefault(); 
        }

	});

    $('#menu').omMenu({
    	contextMenu : true,
        dataSource : [{id:'001', label:'Table Info'},
                      {id:'002', label:'Index Info'}
                     ],
        onSelect : function(item){
        	var node = $("#mytree2").omTree("getSelected");
        	var tblName = encodeURIComponent(node.text);
        	
        	$("#menu").omMenu("hide");
        	if (item.id == "001") {
        		window.showModalDialog("/dbm/biz/inf001.do?tblname=" + tblName + "&t=" + nowDate.getTime(), null, 'dialogWidth:850px;dialogHeight:450px;center:yes;toolbar:no; menubar:no; scrollbars:no;scroll:no');

        	} else if (item.id  == "002") {
      
        	}
        }
    });

	$(window).resize(function() {
		$('#grid').omGrid("resize");
	});

	$('#aButton').omButton({});
	$('#aButton').click(function () {
		// 退出
		document.forms[0].submit();
	});

	$('#bButton').omButton({});
	$('#bButton').click(function () {
		var sqlScript = encodeURIComponent($("#sqlscript").val());

		if (sqlScript.length > 0) {

			var target = $('#mytree2').omTree('getSelected');
			if (target != null) {
				$('#mytree2').omTree('unselect', target);
			}
			$("#tblname").text(''),

			$.getJSON("/dbm/ajax/sqlscript.do?sqlscript=" + sqlScript + '&t=' + nowDate.getTime(), function(data) {
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
		}
	});

});
</script>
</head>

<body>

<div id="west-panel">
	<div style="height:20px;padding-left:5px;line-height:20px;background-color:#99FFCC;white-space: nowrap">当前选择：[<span id="tblname"></span>]</div>
	<ul id="mytree2"></ul>
	<div id="menu"></div>
</div>
<div id="north-panel" style="border-bottom-style:none">
<form method="post" id="man002form" action="/dbm/logout.do">
	<div style="float:left;height:132px"><textarea id="sqlscript" name="sqlscript" cols="140" style="height:100%"></textarea></div>
<div style="float:left;">
	<ul style="list-style-type:none" ><li style="height:40px"><input type="button" id="aButton" value="退出" style="width:60px"/></li>
	<li style="height:40px"><input type="button" id="bButton" value="执行SQL" style="width:60px;"/></li>
	<li style="height:30px"><input type="button" id="cButton" value="确认更新" style="width:60px;"/></li></ul></div>
</form>
</div>
<div id="center-panel" style="border:0px">
	<table id="grid"></table>
</div>

</body>
</html>