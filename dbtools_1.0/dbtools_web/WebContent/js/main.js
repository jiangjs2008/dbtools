
function getCookie(c_name) {
	if (document.cookie.length > 0) {
		c_start = document.cookie.indexOf(c_name + "=");
		if (c_start != -1) { 
			c_start = c_start + c_name.length + 1 
			c_end = document.cookie.indexOf(";", c_start)
			if (c_end == -1) c_end=document.cookie.length
			return unescape(document.cookie.substring(c_start,c_end))
		} 
	}
	return "";
}

function setCookie(c_name, value, expiredays) {
	var exdate = new Date()
	exdate.setDate(exdate.getDate() + expiredays)
	document.cookie = c_name + "=" + escape(value) + ((expiredays==null) ? "" : ";expires=" + exdate.toGMTString())
}

function man001Submit() {
	var checkInput = $("#account1").val();
	if (checkInput) {
		$("#account").val(base64encode(checkInput));
	}
	var checkInput = $("#password1").val();
	if (checkInput) {
		$("#password").val(base64encode(checkInput));
	}
	$('#favrid').val($('#combo1').omCombo('value'));
	document.forms[0].submit();
}

function showerror() {
	// 显示错误信息
	var value = "${errcode}";
	if ("1" == value) {
		 $.omMessageBox.alert({content:'用户名或者密码错误'});
	} else if("2" == value) {
		alert("登录超时，请重新登录！");
	} else if("3" == value) {
		alert("您的账号在别处登录。如非本人操作，请注意账号安全。");
	} else if("4" == value) {
		alert("您的账号存在登录异常，请重新登录。");
	} else if("5" == value) {
		alert("请选择数据库...");
	} else if("999" == value) {
		alert("连接访问错误，请重新登录。");
	}
}

function initCombo(data) {
	$('#combo1').omCombo({
		dataSource : data,
		editable : false,
		valueField : 'favrid',
		optionField : function(data, index) {
			return '<div style="font-size:15px;width:350px;margin-right:15px"><div style="float:left">' + data.name + '</div><div style="float:right">' + data.description + '</div></div>';
		},
		inputField : function(data, index) {
			return data.name;
		},
		onValueChange:function(target, newValue, oldValue, event) {
			$.getJSON("/dbm/ajax/getdblogininfo.do?favrid=" + newValue, function(data) {
				if (data.status == 'ok') {
					$("#account1").val(data.account);
					$("#password1").val(data.password);
				}
			});
		}
	});
}


var dataLimit = -1;
function execScript() {
	var sqlScript = ""; 
	if (optSqlStr.length > 0) {
		sqlScript = optSqlStr;
		optSqlStr = "";
	} else {
		sqlScript = $.trim($("#sqlscript").val());
	}
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

	var clientid = getCookie("clientid");
	var sqlType = sqlScript.substring(0, idx).toLocaleLowerCase();
	sqlScript = encodeURIComponent(encodeURIComponent(sqlScript));

	// 先清空现有数据
	var target = $('#mytree2').omTree('getSelected');
	if (target != null) {
		$('#mytree2').omTree('unselect', target);
	}
	$("#tblname").text('');

	if ('select' != sqlType) {
		$("#grid").omGrid({
			autoColModel: true,
			dataSource : "/dbm/ajax/sqlscript.do?sqlscript=empty&t=" + parseInt(Math.random()*100000)
		});
		$.getJSON("/dbm/ajax/sqlscript.do?sqlscript=" + sqlScript + "&clientid=" + clientid + '&t=' + parseInt(Math.random()*100000), function(data) { showErrMsg(data); });
	} else {

		$("#grid").omGrid({
			limit: dataLimit,
			height: 'fit',
			width : 'fit',
			autoColModel: true,
			dataSource : "/dbm/ajax/sqlscript.do?sqlscript=" + sqlScript + "&clientid=" + clientid + '&t=' + parseInt(Math.random()*100000),
			preProcess: function(data) {
				showErrMsg(data);
				return data;
			}
		});
	}
}

function showErrMsg(data) {
	// 显示错误信息
	// 0:成功, 1:更新成功, 2:更新失败(数据没有被更新), 3:查询失败(注意不是查询结果为0件), 4:执行时发生异常, 5:输入参数不对, 9:系统异常
	if (data.ecd == '1') {
		$.omMessageBox.waiting({ content: 'SQL语句执行成功！' });
		setTimeout("$.omMessageBox.waiting('close');", 800);
	} else if (data.ecd == '2') { $.omMessageBox.alert({ content: '数据没有被更新，请再次确认你的SQL语句' });
	} else if (data.ecd == '3') { $.omMessageBox.alert({ content: '查询数据时发生错误，请再次确认你的SQL语句，并可查看LOG文件' });
	} else if (data.ecd == '4') { $.omMessageBox.alert({ content: '执行SQL语句时发生错误，请再次确认你的SQL语句，并可查看LOG文件<br>' + data.emsg });
	} else if (data.ecd == '9') { $.omMessageBox.alert({ content: '系统异常，请查看LOG文件<br>' + data.emsg });
	
	}
}

var optSqlStr = "";
function optionsSql() {
	var userSelection;
	if (window.getSelection) { //现代浏览器
		userSelection = window.getSelection();
		optSqlStr = userSelection.toString();
	} else if (document.selection) { //IE浏览器 考虑到Opera，应该放在后面
		userSelection = document.selection.createRange();
		optSqlStr = userSelection.text;
	}
}

function logout() {
	// 退出
	document.forms[0].submit();
}

function sqlHistory() {

}


function onExpandNode(nodeData) {
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
}

function onDblClickNode(nodeData, event) {
	if (!nodeData.isCatalog) {
		// 查询指定表的数据
		var tblName = encodeURIComponent(nodeData.text);
		//首先从服务器端获取表头数据，再初始化数据表

		$("#tblname").text(nodeData.text),
		$("#grid").omGrid({
			limit: dataLimit,
			height: 'fit',
			width : 'fit',
			editMode:"all",
			autoColModel: true,
			dataSource: "/dbm/ajax/griddata.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000),
			onAfterEdit:function(rowIndex , rowData){
				 //alert("您刚刚编辑的记录索引为:" + rowIndex);
			},
			preProcess: function(data) {
				showErrMsg(data);
				return data;
			}
		});
	}
}

function onRightClickNode(nodedata, e) {
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

function onSelectNode(item) {
	var node = $("#mytree2").omTree("getSelected");
	var tblName = encodeURIComponent(node.text);

	$("#menu").omMenu("hide");
	if (item.id == "001") {
		//window.showModalDialog("/dbm/biz/inf001.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000), null, 'dialogWidth:850px;dialogHeight:450px;center:yes;toolbar:no; menubar:no; scrollbars:no;scroll:no');
		$('#grid2').omGrid({
			dataSource : "/dbm/ajax/biz/inf001.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000),
			width : 'auto',
			height: '305',
			limit : 0,
			colModel : [ {header : '列名', name : 'colname', width : 200},
						 {header : '类型名', name : 'type', width : 110},
						 {header : '大小', name : 'size', width : 60},
						 {header : '主键', name : 'pk', width : 30, align : 'center' },
						 {header : '可为空', name : 'nullable', width : 35, align : 'center' },
						 {header : '自增加', name : 'autoinc', width : 35, align : 'center' },
						 {header : '默认值', name : 'colvalue', width : 50},
						 {header : '注释', name : 'remark', width : 250} ]
		 });
		 $( "#dialog").omDialog({
			width : 962,
			height: 350,
			modal: true,
			resizable:false,
			title: '表定义 [' + node.text + "] - " + node.remarks
		});

	} else if (item.id  == "002") {

	}
}


function onSelectGroup(item) {
	var node = $("#mytree2").omTree("getSelected");
	var tblName = encodeURIComponent(node.text);

	//window.showModalDialog("/dbm/biz/inf001.do?tblname=" + tblName + "&t=" + parseInt(Math.random()*100000), null, 'dialogWidth:850px;dialogHeight:450px;center:yes;toolbar:no; menubar:no; scrollbars:no;scroll:no');

}
