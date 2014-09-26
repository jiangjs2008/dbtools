<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>选择数据库</title>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-default.css">
<script type="text/javascript" src="/dbm/js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="/dbm/js/operamasks-ui.js"></script>
<script type="text/javascript" src="/dbm/js/base64.js"></script>
<script type="text/javascript" src="/dbm/js/main.js"></script>
<script type="text/javascript">

var value = getCookie('clientid');
if (value == null || value == "") {
	value = "<%=session.getId()%>";
	setCookie('clientid', value, 365);
}

$(document).ready(function() {
	$.getJSON("/dbm/ajax/getdblist.do", initCombo);
	$('#aButton').omButton({onClick : man001Submit});
});
</script>
</head>

<body onload="javascript:showerror();">
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