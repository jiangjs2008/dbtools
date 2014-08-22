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
<script type="text/javascript">

function hideMask() {
	$.messager.progress('close');
}

$(document).ready(function() {
	$("#pmask").hide();
	$(window).resize(function() {
		$('#l1').layout('resize', {
			height: function() {return document.body.clientHeight;}
		});
		$('#r1').layout('resize', {
			width: function() {return document.body.clientWidth;}
		});
	});

	$('#grid').datagrid({
		toolbar: '#ptb',
		method: 'get',
		pagination: true,
		pageNumber: 1,
		pageSize: 100,
		pageList: [100],
		 fitColumns: false,
		 fit:true,
		rownumbers:true,fitColumns:true,
		onLoadSuccess: function() { recolsize(); hideMask(); },
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
					var tblName2 = encodeURIComponent(tblName1);
					$.getJSON("/dbm/ajax/gridcol.do?tblname=" + tblName2 + '&t=' + parseInt(Math.random()*100000), function(coldata) {
						$("#tblname").text(node.text);
						// 打开新的表时必须刷新pageNumber，重置为1
						$('#grid').datagrid({
							pagination: true,
							pageList: [100],
							pageNumber: 1,
							pageSize: 100,
							 fitColumns: false,
							url: "/dbm/ajax/griddata.do?t=" + parseInt(Math.random()*100000),
							queryParams: { tblname: tblName1 },
							columns: coldata,
							loadFilter: function(data) {
								if (data.emsg){
									$('div.pagination-info').text(data.emsg);
								}
								return data;
							}
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

	    $('#grid').datagrid({ url: null });
		$('#grid').datagrid('loadData', {total:0, rows:[]});
		$('#grid').datagrid({
			columns: [[]],
			loadFilter: function(data) {
				return data;
			}
		});
		$('select.pagination-page-list').hide();

		$.getJSON("/dbm/ajax/sqlscript.do?sqlscript=" + sqlScript + '&t=' + parseInt(Math.random()*100000), function(data) {
			 if (data.ecd == '1') {
				if (data.gridata) {
	     			$('#grid').datagrid({
						pageNumber: 1,
						pageSize: 100,
						url: null,
						columns: data.coldata
					});

					$('#grid').datagrid('loadData', data.gridata);
					$('select.pagination-page-list').hide();
				}
				hideMask();
				$.messager.show({
					title: '好消息',
					msg: 'SQL语句执行成功.',
					timeout: 5000,
					showType: 'slide'
				});

			} else {
				hideMask();
				if (data.ecd == '0') {
					$.messager.alert('注意', 'SQL语句不正确', 'warning');
				} else if (data.ecd == '2') {
					$.messager.alert('注意', '执行SQL语句时发生错误，没有更新成功', 'warning');
				} else if (data.ecd == '3') {
					$.messager.alert('注意', '执行SQL语句时发生错误，请确认你的SQL语句，并查看LOG文件', 'warning');
				}
			}
		});
	}
}

function recolsize() {
	//datagrid头部 table 的第一个tr 的td们，即columns的集合
	var headerTds = $(".datagrid-view2 .datagrid-header .datagrid-header-inner table tr:first-child").children();
	//datagrid主体 table 的第一个tr 的td们，即第一个数据行
	var bodyTds = $(".datagrid-view2 .datagrid-body table tr:first-child").children();
	var totalWidth = 0;
	//循环设置宽度
	bodyTds.each(function (i, obj) {
	  var headerTd = $(headerTds.get(i));
	  var bodyTd = $(bodyTds.get(i));
	  var headerTdWidth = headerTd.width();
	  var bodyTdWidth = bodyTd.width();

	  //如果头部列名宽度比主体数据宽度宽，则它们的宽度都设为头部的宽度。反之亦然
	  if (headerTdWidth > bodyTdWidth) {
	      headerTdWidth = headerTdWidth + 15;
	  } else {
	      headerTdWidth = bodyTdWidth + 15;
	  }
	  if (headerTdWidth > 800) { // TODO-- 此行代码不起作用，待调查
	  	headerTdWidth = 800;
	  }
	  bodyTd.width(headerTdWidth);
	  headerTd.width(headerTdWidth);
	  totalWidth += headerTdWidth;
	});
	var headerTable = $(".datagrid-view2 .datagrid-header .datagrid-header-inner table:first-child");
	var bodyTable = $(".datagrid-view2 .datagrid-body table:first-child");
	//循环完毕即能得到总得宽度设置到头部table和数据主体table中
	headerTable.width(totalWidth);
	bodyTable.width(totalWidth);
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
		    	<table id="grid" class="easyui-datagrid" border="0" ></table>
			</div>
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