<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择数据库</title>
<script type="text/javascript">
var userAgent = navigator.userAgent;
if (userAgent.indexOf("MSIE") > 0) {
	location.href = '/dbm/notsupport.html';
}
</script>
<link rel="stylesheet" type="text/css" href="/dbm/css/default/easyui.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/main.css">
<script type="text/javascript" src="/dbm/js/jquery.min.js"></script>
<script type="text/javascript" src="/dbm/js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="/dbm/js/base64.js"></script>
<script type="text/javascript" src="/dbm/js/main.js"></script>
<<<<<<< .mine
<script type="text/javascript" src="/dbm/js/man001.js"></script>
=======
<script type="text/javascript">
var nowDate = new Date();
$(document).ready(function() {
	$("#favrid").combobox({
		url: '/dbm/ajax/getdblist.do?t=' + nowDate.getTime(),
		method: 'get',
		valueField: 'favrid',
		textField: 'name',
		panelWidth: 450,
		panelHeight: '200',
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
	$("#favrid").combobox("setValue", "请选择数据库：");
});

function formatItem(row) {
	var s = '<div style="font-size:16px;height:20px;line-height:20px;margin-right:3px"><div style="float:left">' + row.name + '</div><div style="float:right;color:#888">' + row.description + '</div></div>';
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
	var aurl = "/dbm/ajax/login.do?favrid=" + $("#favrid").combobox('getValue');
	aurl += "&user=" + account + "&pwd=" + password;
	$.getJSON(aurl + '&t=' + nowDate.getTime(), function(data) {
		if (data.ecd == 'ok') {
			location.href = '/dbm/jsp/man002.jsp';
		} else {
			showerror(data.ecd);
		}
	});
}

</script>
>>>>>>> .r228
</head>

<body>
<div style="padding-top:150px"></div>
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
<br/><br/>
<div id="pmsg" style="text-align:center;font-size:12px"></div>
<form method="post" id="man001form" action="/dbm/biz/man001.do"></form>
</body>
</html>