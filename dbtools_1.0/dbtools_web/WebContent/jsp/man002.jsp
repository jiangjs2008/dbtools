<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看表</title>
<link rel="stylesheet" type="text/css" href="/dbm/css/default/easyui.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/icon.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/main.css">
<script type="text/javascript" src="/dbm/js/jquery.min.js"></script>
<script type="text/javascript" src="/dbm/js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="/dbm/js/main.js"></script>
<script type="text/javascript">

function hideMask() {
	$.messager.progress('close');
}

$(document).ready(function() {
	$("#pmask").hide();
	$("#ptbdiv").hide();
	var nowDate = new Date();

	$.getJSON("/dbm/ajax/getcatalog.do?t=" + nowDate.getTime(), function(subdata) {
		if (subdata.ecd) {
			location.href = '/dbm/index.html';
			return;
		}
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
					$.messager.progress({ 
				        title: 'Please waiting', 
				        text: '正在努力获取数据中......' 
				    });
				    $("#ptbdiv").show();
					var tblName1 = encodeURIComponent(node.text);

					$("#tblname").text(node.text);
					// 打开新的表时必须刷新pageNumber，重置为1
					$('#grid').datagrid({
						toolbar: '#ptb',
						method: 'get',
						pagination: true,
						pageSize: 100,
						pageList: [100],
						fit:true, fitColumns: false,
						rownumbers:true,
						onLoadError: function() { $('select.pagination-page-list').hide(); hideMask(); },
						onClickCell: onClickCell,
						pageNumber: 1,
						url: "/dbm/ajax/griddata.do?t=" + parseInt(Math.random()*100000),
						queryParams: { tblname: tblName1 },
						onLoadSuccess: function(_5a8, data) {
							$('select.pagination-page-list').hide();
							hideMask();
							if (_5a8.ecd != undefined) {
								showerror(_5a8.ecd);
							}
						}
					});
				}
			},
			onExpand: function(node) {
				if (!node.hasdata) {
					// 如果没有加载子节点，则加载
					$.messager.progress({ 
				        title: 'Please waiting', 
				        text: '正在努力获取数据中......' 
				    });
					// append some nodes to the selected node
					$.getJSON("/dbm/ajax/gettbllist.do?catalog=" + node.text + '&t=' + nowDate.getTime(), function(subdata2) {
						if (subdata2.ecd) {
							hideMask();
							$.messager.alert('注意', '操作过程中发生错误', 'warning');
						} else {
							$('#mytree2').tree('append', {
								parent: node.target,
								data: subdata2
							});
							hideMask();
						}
					});
					node.hasdata = true;
				}
			},
			onContextMenu: function(e, node){
				e.preventDefault();
				// select the node
				$('#mytree2').tree('select', node.target);
				// display context menu
				if (!!node.hassub) {
					$('#mm1').menu('show', {
						left: e.pageX,
						top: e.pageY
					});
				} else {
					$('#mm2').menu('show', {
						left: e.pageX,
						top: e.pageY
					});
				}
			}
		});
	});

	// 设置单元格为可编辑
    $.extend($.fn.datagrid.methods, {
        editCell: function(jq,param){
            return jq.each(function(){
                var opts = $(this).datagrid('options');
                var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
                for(var i=0; i<fields.length; i++){
                    var col = $(this).datagrid('getColumnOption', fields[i]);
                    col.editor1 = col.editor;
                    if (fields[i] != param.field){
                        col.editor = null;
                    }
                }
                $(this).datagrid('beginEdit', param.index);
                for(var i=0; i<fields.length; i++){
                    var col = $(this).datagrid('getColumnOption', fields[i]);
                    col.editor = col.editor1;
                }
            });
        }
    });
});

var editIndex = undefined;
function endEditing(){
    if (editIndex == undefined){return true}
    if ($('#grid').datagrid('validateRow', editIndex)){
        $('#grid').datagrid('endEdit', editIndex);
        editIndex = undefined;
        return true;
    } else {
        return false;
    }
}

function onClickCell(index, field){
    if (endEditing()){
        $('#grid').datagrid('selectRow', index)
                  .datagrid('editCell', {index:index, field:field});
        editIndex = index;
    }
}

</script>
</head>

<body>
<div class="easyui-layout" data-options="fit:true">
	<div id="l1" data-options="region:'west',split:true" title="Database" style="width:250px">
		<ul id="mytree2" class="easyui-tree" data-options="lines:true"></ul>
	</div>
	<div id="r1" data-options="region:'center'">
		<div class="easyui-layout" data-options="fit:true">
		    <div data-options="region:'north',border:false" style="height:155px;border-bottom:2px solid #E6EEF8">
		        <textarea id="sqlscript" name="sqlscript" style="width:99%;height:96%;color:#999" onselect="showMsg()" onFocus="if(value=='请在此输入SQL执行脚本：'){value='';this.style.color='#000'}" onBlur="if(!value){value='请在此输入SQL执行脚本：';this.style.color='#999'}">请在此输入SQL执行脚本：</textarea>
		    </div>
		    <div data-options="region:'center',border:false" >
<div id="ptb" style="height:31px;">
	<div id="ptbdiv" style="float:left"><img src="/dbm/css/icons/shapes.png" style="margin-left:10px;margin-right:10px;margin-top:4px"/><span id="tblname" style="position:relative;font-size:14px;top:-5px"></span>
	<a href="#" class="easyui-linkbutton" style="margin-left:50px;margin-top:-12px" data-options="iconCls:'icon-add'">添加记录</a>
	<a href="#" class="easyui-linkbutton" style="margin-left:30px;margin-top:-12px" data-options="iconCls:'icon-remove'">删除记录</a>
	<a href="#" class="easyui-linkbutton" style="margin-left:30px;margin-top:-12px" data-options="iconCls:'icon-save'">保存修改</a></div>
	<div style="float:right"><a href="#" onclick="javascript:execSQL()" class="easyui-linkbutton" style="margin-right:30px;margin-top:2px">执行SQL</a>
	<a href="#" class="easyui-linkbutton" style="margin-right:30px;margin-top:2px">历史纪录</a>
	<a href="#" onclick="javascript:logout()" class="easyui-linkbutton" style="margin-right:30px;margin-top:2px" data-options="iconCls:'icon-closed'">退出</a></div>
</div>
		    	<table id="grid" class="easyui-datagrid" border="0" ></table>
			</div>
		</div>
	</div>
</div>
<div id="mm1" class="easyui-menu" style="width:120px;">
	<div onclick="javascript:reloadInfo()" data-options="iconCls:'icon-reload'">刷新</div>
</div>
<div id="mm2" class="easyui-menu" style="width:120px;">
	<div onclick="javascript:getTblInfo()" data-options="iconCls:'icon-tbl'">表定义</div>
	<div onclick="javascript:getIdxInfo()" data-options="iconCls:'icon-idx'">索引定义</div>
</div>
<div id="idxinfo" class="easyui-window" style="width:600px;height:400px" title="索引定义" data-options="closed:true,collapsible:false,minimizable:false,maximizable:false">
    <table id="idxgrid" class="easyui-datagrid" border="0" ></table>
</div>
<form method="post" id="man002form" action="/dbm/logout.do"></form>
</body>
</html>