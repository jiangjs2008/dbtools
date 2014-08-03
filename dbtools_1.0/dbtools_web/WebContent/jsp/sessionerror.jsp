<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>提示</title>
<link rel="stylesheet" type="text/css" href="${w}/css/operamasks-ui.min.css">
<script type="text/javascript" src="${w}/js/jquery.min.js"></script>
<script type="text/javascript" src="${w}/js/operamasks-ui.min.js"></script>
<script type="text/javascript" src="${w}/js/sem.js"></script>
<script type="text/javascript">
	// web context path
	w = "${w}";

	$(document).ready(function() {
		setTimeout("location.href='${w}/index.html'", 5000);
	});
</script>
</head>

<body style="background-color: #E6E6E6;padding-top:150px;text-align:center">
您还未登陆到本系统，5秒钟后将自动切换至登陆画面 .........
</body>
</html>