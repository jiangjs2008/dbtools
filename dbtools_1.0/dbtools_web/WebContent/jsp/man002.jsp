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

function hideMask() {
	$.messager.progress('close');
}

$(document).ready(function() {
	$("#pmask").hide();
	$('#grid').datagrid({
		toolbar: '#ptb',
		pagination: true,
		pageList: [100],
		rownumbers:true,fitColumns:true,
		onLoadSuccess: function() { hideMask(); },
		onLoadError: function() { hideMask(); },
		onClickCell: onClickCell,
		loadFilter: function(data) {
			if (data.emsg){
				$('div.pagination-info').text(data.emsg);
			}
			return data;
		}
	});
	$('select.pagination-page-list').hide();

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
					$.messager.progress({ 
				        title: 'Please waiting', 
				        text: '正在努力获取数据中......' 
				    });
				    $("#ptbdiv").show();
					var tblName = encodeURIComponent(node.text);
					$.getJSON("/dbm/ajax/gridcol.do?tblname=" + tblName + '&t=' + parseInt(Math.random()*100000), function(coldata) {
						$("#tblname").text(node.text);
						// 打开新的表时必须刷新pageNumber，重置为1
						$('#grid').datagrid({
							pageNumber: 1,
							pageSize: 100,
							url: "/dbm/ajax/griddata.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000),
							columns: coldata
						});
						$('select.pagination-page-list').hide();
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
						$('#mytree2').tree('append', {
							parent: node.target,
							data: subdata2
						});
						hideMask();
					});
					node.hasdata = true;
				}
			}
		});
	});

	
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
                .datagrid('editCell', {index:index,field:field});
        editIndex = index;
    }
}

function execSQL() {
	var sqlScript = $("#sqlscript").val();
	if (sqlScript == '请在此输入SQL执行脚本：') {
		return;
	}
	sqlScript = encodeURIComponent(sqlScript);

	if (sqlScript.length > 0) {
		$.messager.progress({ 
	        title: 'Please waiting', 
	        text: '正在执行SQL脚本......' 
	    });
		$("#ptbdiv").hide();
		//$('#grid').datagrid({ data: [{}] });

		$.getJSON("/dbm/ajax/sqlscript.do?sqlscript=" + sqlScript + '&t=' + parseInt(Math.random()*100000), function(data) {
			if (data.ecd == '1') {
			  // $.omMessageBox.waiting({
		     //      content: 'SQL语句执行成功！'
		     //  });
		     //  setTimeout("$.omMessageBox.waiting('close');", 1000);
				if (data.gridata) {
					//var dispData = JSON.parse(data.gridata);
	     			$('#grid').datagrid({
						pageNumber: 1,
						pageSize: 100,
						columns: data.coldata
						//data: data.gridata.rows
					});
					$('#grid').datagrid('loadData', data.gridata);
				}

			} else if (data.ecd == '2') {
		     //  $.omMessageBox.alert({
		     //      content: 'SQL语句执行失败，请再次确认你的SQL语句'
		     //  });

			} else if (data.ecd == '3') {
		     //  $.omMessageBox.alert({
		     //      content: '执行SQL语句时发生错误，请再次确认你的SQL语句，并可查看LOG文件<br>' + data.msg
		     //  });

			}
			hideMask();
		});
	}
}

</script>
</head>

<body>
<div class="easyui-layout" style="width:100%;height:100%">
	<div data-options="region:'west',split:true" title="Database" style="width:250px">
		<ul id="mytree2" class="easyui-tree" data-options="lines:true"></ul>
	</div>
	<div data-options="region:'center'" style="width:99.5%">
	    <div data-options="region:'north',border:false" style="height:153px;border-bottom:2px solid #E6EEF8">
	        <textarea id="sqlscript" name="sqlscript" style="width:99.5%;height:150px;color:#999" onFocus="if(value=='请在此输入SQL执行脚本：'){value='';this.style.color='#000'}" onBlur="if(!value){value='请在此输入SQL执行脚本：';this.style.color='#999'}">请在此输入SQL执行脚本：</textarea>
	    </div>
	    <div data-options="region:'center',border:false">
	    	<table id="grid" class="easyui-datagrid" style="width:100%;height:607px"></table>
		</div>
	</div>
</div>
<form method="post" id="man002form" action="/dbm/logout.do"></form>
<div id="ptb" style="height:31px;">
	<div id="ptbdiv" style="float:left"><img src="/dbm/css/icons/shapes.png" style="margin-left:10px;margin-right:10px;margin-top:4px"/><span id="tblname" style="position:relative;font-size:14px;top:-5px"></span>
	<a href="#" class="easyui-linkbutton" style="margin-left:50px;margin-top:-12px" data-options="iconCls:'icon-add'">添加记录</a>
	<a href="#" class="easyui-linkbutton" style="margin-left:30px;margin-top:-12px" data-options="iconCls:'icon-remove'">删除记录</a>
	<a href="#" class="easyui-linkbutton" style="margin-left:30px;margin-top:-12px" data-options="iconCls:'icon-save'">保存修改</a></div>
	<div style="float:right"><a href="#" onclick="javascript:execSQL()" class="easyui-linkbutton" style="margin-right:30px;margin-top:2px">执行SQL</a>
	<a href="#" class="easyui-linkbutton" style="margin-right:30px;margin-top:2px">历史纪录</a>
	<a href="#" onclick="javascript:logout()" class="easyui-linkbutton" style="margin-right:30px;margin-top:2px" data-options="iconCls:'icon-closed'">退出</a></div>
</div>
</body>
</html>