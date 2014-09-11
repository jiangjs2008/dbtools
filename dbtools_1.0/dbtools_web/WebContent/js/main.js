
String.prototype.trim = function() {
  var str = this,
  str = str.replace(/^\s\s*/, ''),
  ws = /\s/,
  i = str.length;
  while (ws.test(str.charAt(--i)));
  return str.slice(0, i + 1);
}

function showerror(errcode) {
	// 显示错误信息
	if ("1" == errcode) {
		 $.messager.alert("警告", '用户名或者密码错误');
	} else if("2" == errcode) {
		$.messager.alert("注意", "");
	} else if("3" == errcode) {
		$.messager.alert("注意", "");
	} else if("4" == errcode) {
		$.messager.alert("注意", "SQL语句执行时发生错误，请查看日志文件，或联系数据库管理员。");
	} else if("5" == errcode) {
		$.messager.alert("注意", "请选择数据库...");
	} else if("6" == errcode) {
		$.messager.alert("注意", "连接数据库时发生错误，请查看日志文件，或联系数据库管理员。");
	} else if("9" == errcode) {
		$.messager.alert("注意", "连接超时，请重新登录。");
	}
}
<<<<<<< .mine
=======


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
     	$('#grid').datagrid({
			toolbar: '#ptb',
			method: 'get',
			pagination: true,
			pageSize: 100,
			pageList: [100],
			fit:true, fitColumns: false,
			rownumbers:true,
			onLoadError: function() { $('select.pagination-page-list').hide(); hideMask(); },
			onClickCell: null,
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

	$('#grid').datagrid('load', { total:0, rows:[] });
	$('#grid').datagrid({ columns: [[]] });
	$('#grid').remove();

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


function showMsg() {

}

// 重新加载表一览
function reloadInfo() {

}

// 显示表定义信息
function getTblInfo() {
	var node = $('#mytree2').tree('getSelected');
	if (node == null) {
		return;
	}
	if (node.text) {
		window.open("/dbm/biz/inf001.do?tblname=" + node.text + "&t=" + parseInt(Math.random()*100000), null, 'top=200,left=300,width=770,height=300');
	} //window.showModalDialog
}

// 显示表的索引定义信息
function getIdxInfo() {
	var node = $('#mytree2').tree('getSelected');
	if (node == null) {
		return;
	}

		if (node.text) {
		$.getJSON("/dbm/biz/inf001.do?tblname=" + node.text + '&t=' + parseInt(Math.random()*100000), function(data) {
			 if (data.ecd == 0) {
			 	$('#tblgrid').html('');
			 	$('#tblinfo').window('open');
			 	var rowData = '';
			 	var allrow = data.rows;
		     	for (var i = 0; i < allrow.length; i++) {
					var row = allrow[i];
		     		rowData = rowData + "<tr><td align='center'>" + (i + 1) + "</td><td>" + row.colname + "</td><td>" + row.type + "</td><td>" + row.size 
		     			+ "</td><td align='center'>" + row.pk + "</td><td align='center'>" + row.nullable + "</td><td>" + row.remark + "</td></tr>";
		     	}
		     	$('#tblgrid').html(rowData);
			}
		});
	}
}
>>>>>>> .r228
