<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择数据库</title>
<script type="text/javascript" src="/dbm/js/jquery-1.6.4.min.js"></script>
<script type="text/javascript" src="/dbm/js/om-ui.js"></script>
<script type="text/javascript" src="/dbm/js/base64.js"></script>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-default.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/main.css">
<script type="text/javascript">
$(document).ready(function() {

	$('#combo1').omCombo({});
	showerror();

	$.getJSON("/dbm/ajax/getdblist.do", function(data) {
		$('#combo1').omCombo({
			dataSource : data,
			editable : false,
			valueField : 'favrid', 
			optionField : function(data, index) {
				return '<div style="float:left">' + data.name + '</div><div style="float:right">' + data.description + '</div>';
			},
			inputField : function(data, index) {
				return data.name;
			}
		});
	});
	$('#aButton').omButton({});
	$('#aButton').click(function onFormSubmitReg() {
		var checkInput = $("#account").val();
		if (checkInput) {
			$("#account").val(base64encode(checkInput));
		}
		var checkInput = $("#password").val();
		if (checkInput) {
			$("#password").val(base64encode(checkInput));
		}
		$('#favrid').val($('#combo1').omCombo('value'));
		document.forms[0].submit();
	});
});

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
</script>
<!-- view_source_end -->
</head>

<body>
<form method="post" id="man001form" action="/dbm/man001.do">
<input type="hidden" id="favrid" name="favrid"/>
<div style="padding-top:150px;overflow:visible">
<table cellspacing="0" cellpadding="0" border="0" style="height:200px" align="center">
	<tr>
		<td>请选择数据库：</td>
		<td align="left"><input id="combo1" style="width:250px;"/></td>
	</tr>
	<tr>
		<td>用户：</td>
		<td align="left"><input type="text" id="account" name="user" value="root" style="width:150px;line-height:20px;height:20px"/></td>
	</tr>
	<tr>
		<td>密码：</td>
		<td align="left"><input type="password" id="password" name="pwd" value="123456" style="width:150px;line-height:20px;height:20px"/></td>
	</tr>
	<tr>
		<td align="center" colspan="2"><input type="button" id="aButton" value="确定" style="width:60px;"/></td>
	</tr>
</table>
</div>
</form>
</body>
</html>