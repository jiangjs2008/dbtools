<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>系统异常</title>
<link rel="stylesheet" type="text/css" href="${w}/css/operamasks-ui.min.css">
<script type="text/javascript" src="${w}/js/jquery.min.js"></script>
<script type="text/javascript" src="${w}/js/operamasks-ui.min.js"></script>
<script type="text/javascript" src="${w}/js/sem.js"></script>
<script type="text/javascript">
	// web context path
	w = "${w}";
</script>
</head>

<body style="background-color: #E6E6E6;padding-top:120px;text-align:center">
<form method="post" id="loginForm" action="${w}/index.do">
	<table border="0" align="center">
	<tbody>
		<tr>
			<td>系统异常，请联系客服。</td>
		</tr>
		<tr style="height:100px;">
			<td><input type="submit" id="login" value="回到登陆画面" /></td>
		</tr>
	</tbody>
	</table>
</form>
</body>
</html>