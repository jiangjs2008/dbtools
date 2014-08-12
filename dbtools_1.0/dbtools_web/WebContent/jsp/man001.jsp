<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择数据库</title>
<link rel="stylesheet" type="text/css" href="/dbm/css/default/easyui.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/main.css">
<script type="text/javascript" src="/dbm/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="/dbm/js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="/dbm/js/base64.js"></script>
<script type="text/javascript">
var nowDate = new Date();
$(document).ready(function() {
	$("#favrid").combobox({
		url: '/dbm/ajax/getdblist.do?t=' + nowDate.getTime(),
		method: 'get',
		valueField: 'favrid',
		textField: 'name',
		panelWidth: 450,
		panelHeight: 'auto',
		formatter: formatItem,
		onSelect: function(param){
			$.getJSON("/dbm/ajax/getdblogininfo.do?favrid=" + param.favrid + '&t=' + nowDate.getTime(), function(data) {
				if (data.status == 'ok') {
					$("#account").val(data.account);
					$("#password").val(data.password);
				}
			});
		}
	});
});

function formatItem(row) {
	var s = '<div style="font-size:16px;height:20px;line-height:20px"><div style="float:left">' + row.name + '</div><div style="float:right;color:#888;margin-right:2px">' + row.description + '</div></div>';
	return s;
}

function submitForm() {
	var account = $("#account").val();
	if (account) {
		account = base64encode(account);
	}
	var password = $("#password").val();
	if (password) {
		password = base64encode(password);
	}
	var aurl = "/dbm/login.do?favrid=" + $("#favrid").combobox('getValue');
	aurl += "&user=" + account + "&pwd=" + password;
	$.getJSON(aurl + '&t=' + nowDate.getTime(), function(data) {
		if (data.errcode == 'ok') {
			location.href = '/dbm/jsp/man002.jsp';
		} else {
			showerror(data.errcode);
		}
	});
}

function showerror(errcode) {
	// 显示错误信息
	if ("1" == errcode) {
		 $.messager.alert('用户名或者密码错误');
	} else if("2" == errcode) {
		alert("登录超时，请重新登录！");
	} else if("3" == errcode) {
		alert("您的账号在别处登录。如非本人操作，请注意账号安全。");
	} else if("4" == errcode) {
		alert("您的账号存在登录异常，请重新登录。");
	} else if("5" == errcode) {
		alert("请选择数据库...");
	} else if("6" == errcode) {
		alert("连接数据库时发生错误，请查看日志文件，或联系数据库管理员。");
	} else if("999" == errcode) {
		alert("连接访问错误，请重新登录。");
	}
}
</script>
</head>

<body>
<div style="padding-top:150px">
<form method="post" id="man001form" action="">
<table cellspacing="0" cellpadding="0" border="0" style="height:200px" align="center">
	<tr>
		<td>请选择数据库：</td>
		<td align="left"><input class="easyui-combobox" style="width:250px;height:25px;line-height:25px" id="favrid" name="favrid" ></td>
	</tr>
	<tr>
		<td>用户：</td>
		<td align="left"><input type="text" id="account" name="user" style="width:150px;line-height:20px;height:20px"/></td>
	</tr>
	<tr>
		<td>密码：</td>
		<td align="left"><input type="password" id="password" name="pwd" style="width:150px;line-height:20px;height:20px"/></td>
	</tr>
	<tr>
		<td align="center" colspan="2"><a href="javascript:void(0)" class="easyui-linkbutton" style="width:60px;" onclick="submitForm()">确定</a></td>
	</tr>
</table>
</form>
</div>
<script type="text/javascript">
var browser=navigator.appName
var b_version=navigator.appVersion
var version=parseFloat(b_version)
document.write("浏览器名称："+ browser)
document.write("<br />")
document.write("浏览器版本："+ version)
</script>
</body>
</html>