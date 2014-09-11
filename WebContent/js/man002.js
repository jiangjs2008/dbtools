
var deployType = null;

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
					$('#griddiv').show();
					$('#griddiv2').hide();
					$('#grid').datagrid({
						toolbar: '#ptb',
						method: 'get',
						pagination: true,
						pageSize: 100,
						pageList: [100],
						fit:true, fitColumns: false,
						rownumbers:true,
						onLoadError: function() { $('select.pagination-page-list').hide(); hideMask(); },
						pageNumber: 1,
						url: "/dbm/ajax/griddata.do?t=" + parseInt(Math.random()*100000),
						queryParams: { tblname: tblName1 },
						onClickCell: onClickCell,
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
			}
		});
	});
});

var editIndex = undefined;
function endEditing() {
    if (editIndex == undefined){return true}
    if ($('#grid').datagrid('validateRow', editIndex)) {
        $('#grid').datagrid('endEdit', editIndex);
        editIndex = undefined;
        return true;
    } else {
        return false;
    }
}

function onClickCell(index, field) {
    if (endEditing()) {
        $('#grid').datagrid('selectRow', index)
                  .datagrid('editCell', {index:index,field:field});
        editIndex = index;
    }
}


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


function execSQL() {
	var sqlScript = $("#sqlscript").val().trim();
	if (sqlScript == '请在此输入SQL执行脚本：') {
		return;
	}

	// 判断SQL类型
	var idx = sqlScript.indexOf(' ');
	if (idx == -1) {
		$.messager.alert('注意', 'SQL语句不正确, 请重新输入.', 'warning');
		return;
	}
	var sqlType = sqlScript.substring(0, idx).toLocaleLowerCase();
	sqlScript = encodeURIComponent(sqlScript);

	if ('select' != sqlType) {
		// 如果不是查询语句则需要授权
		$.messager.confirm('确认', '确定要执行操作？<div style="color:red;font-weight:bold;">此操作不可恢复。</div>请再次确认：', function(r) {
			if (r) {
				$.messager.confirm('确认', '<div style="color:red;font-weight:bold;">再考虑一下！</div>', function(r) {
					if (r) {
						innerExecSQL(sqlScript, 2);
					}
				});
			}
		});

	} else {
		innerExecSQL(sqlScript, 1);
	}
}

function innerExecSQL(sqlScript, sqlType) {

	$.messager.progress({ 
        title: 'Please waiting', 
        text: '正在执行SQL脚本......' 
    });
	$("#ptbdiv").hide();

	if (sqlType == 1) {
		$('#griddiv').hide();
		$('#griddiv2').show();
		$('#grid2').datagrid({
			toolbar: '#ptb',
			method: 'get',
			pagination: true,
			pageSize: 100,
			pageList: [100],
			fit:true, fitColumns: false,
			rownumbers:true,
			onLoadError: function() { $('select.pagination-page-list').hide(); hideMask(); },
			pageNumber: 1,
			url: "/dbm/ajax/sqlscript.do?sqlscript=" + sqlScript + '&t=' + parseInt(Math.random()*100000),
			onLoadSuccess: function(_5a8, data) {
				$('select.pagination-page-list').hide();
				hideMask();
				//$('div.datagrid-view').hide();
				if (_5a8.ecd != undefined) {
					showerror(_5a8.ecd);
				}
			}
		});

	} else {
		$('#griddiv').show();
		$('#griddiv2').hide();
		$('#grid').datagrid('load', { total:0, rows:[] });
		$('#grid').datagrid({ columns: [[]] });

		$.getJSON("/dbm/ajax/sqlscript.do?sqlscript=" + sqlScript + '&t=' + parseInt(Math.random()*100000), function(data) {
			 if (data.ecd == '1') {
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
				} else if (data.ecd == '3') {
					$.messager.alert('注意', '执行SQL语句时发生错误，请确认你的SQL语句，并查看LOG文件', 'warning');
				}
			}
		});
	}
}


function resetFunc(deployType) {
	if (deployType == '1') {
		// 权限控制
		$("#ptbdiv").remove();
	} else if (deployType == '0') {
		// 设置单元格为可编辑
	    $.extend($.fn.datagrid.methods, {
	        editCell: function(jq,param) {
	            return jq.each(function() {
	                var opts = $(this).datagrid('options');
	                var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
	                for (var i=0; i<fields.length; i++) {
	                    var col = $(this).datagrid('getColumnOption', fields[i]);
	                    col.editor1 = col.editor;
	                    if (fields[i] != param.field) {
	                        col.editor = null;
	                    }
	                }
	                $(this).datagrid('beginEdit', param.index);
	                for (var i=0; i<fields.length; i++) {
	                    var col = $(this).datagrid('getColumnOption', fields[i]);
	                    col.editor = col.editor1;
	                }
	            });
	        }
	    });
	}
}

function showMsg() {

}