<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>用户验证</title>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-default.css">
<script type="text/javascript" src="/dbm/js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="/dbm/js/operamasks-ui.js"></script>
<script type="text/javascript" src="/dbm/js/base64.js"></script>
<script type="text/javascript" src="/dbm/js/main.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var errcnt = '${errlimit}';
	if (errcnt == '1') {
		var tmplListUl = $('#tmpllist');
		tmplListUl.empty();
		tmplListUl.append("再见！");
	}
});
function goLogin() {
	var checkInput = $("#username").val();
	if (!checkInput) {
		$.omMessageBox.alert({ content : '用户名不能为空。' });
		return;
	}
	var password = $("#password").val();
	if (!password) {
		$.omMessageBox.alert({ content : '密码不能为空。' });
		return;
	}
	$('#username').val(base64encode(checkInput));
	$('#password').val(base64encode(password));
	document.forms[0].submit();
}
</script>
</head>

<body onload="javascript:showerror('${ecd}');">
<form method="post" action="/dbm/index.do">
<table style="margin: 0 auto;padding-top:100px">
	<tr>
		<td style="width:80px">用户名</td>
		<td><input type="text" id="username" name="username" style="width:200px" maxlength="20"></td>
	</tr>
	<tr>
		<td>密码</td>
		<td><input type="password" id="password" name="password" style="width:200px" maxlength="20" ></td>
	</tr>
	<tr>
		<td colspan="2" align="center" style="height:40px"><input type="button" value="确定" onclick="javascript:goLogin();"></td>
	</tr>
</table>
</form>
</body>
</html>
