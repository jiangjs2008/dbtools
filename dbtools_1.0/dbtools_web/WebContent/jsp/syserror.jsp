<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>系统异常</title>
<script type="text/javascript" src="/dbm/js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="/dbm/js/operamasks-ui.js"></script>
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
	location.href = '/dbm/index.html';
}
</script>
</head>

<body style="background-color: #E6E6E6">
<div id="tmpllist" style="margin:0 auto;padding-top:120px;text-align:center">
<table border="0" align="center">
<tbody>
	<tr>
		<td>系统异常，请联系客服。</td>
	</tr>
	<tr style="height:200px;">
		<td><input type="button" id="login" value="回到登陆画面" onclick="javascript:goLogin();"/></td>
	</tr>
</tbody>
</table>
</div>
</body>
</html>