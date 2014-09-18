<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择数据库</title>
<script type="text/javascript" src="/dbm/js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="/dbm/js/operamasks-ui.js"></script>
<script type="text/javascript" src="/dbm/js/base64.js"></script>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-default.css">
<script type="text/javascript">
$(document).ready(function() {

	showerror();

	$.getJSON("/dbm/ajax/getdblist.do", function(data) {
		$('#combo1').omCombo({
			dataSource : data,
			editable : false,
			valueField : 'favrid',
			optionField : function(data, index) {
				return '<div style="font-size:15px"><div style="float:left">' + data.name + '</div><div style="float:right">' + data.description + '</div></div>';
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
	});
	$('#aButton').omButton({});
	$('#aButton').click(function () {
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
<div style="padding-top:150px">
<table cellspacing="0" cellpadding="0" border="0" style="height:200px" align="center">
	<tr>
		<td>请选择数据库：</td>
		<td align="left"><input id="combo1" style="width:200px"/></td>
	</tr>
	<tr>
		<td>用户：</td>
		<td align="left"><input type="text" id="account1" style="width:150px;line-height:20px;height:20px"/></td>
	</tr>
	<tr>
		<td>密码：</td>
		<td align="left"><input type="password" id="password1" style="width:150px;line-height:20px;height:20px"/></td>
	</tr>
	<tr>
		<td align="center" colspan="2"><input type="button" id="aButton" value="确定" style="width:60px;"/></td>
	</tr>
</table>
</div>
<form method="post" id="man001form" action="/dbm/login.do">
<input type="hidden" id="favrid" name="favrid"/>
<input type="hidden" id="account" name="user"/>
<input type="hidden" id="password" name="pwd"/>
</form>
</body>
</html>