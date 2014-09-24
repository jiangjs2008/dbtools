<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<script type="text/javascript" src="/dbm/js/main.js"></script>
<script type="text/javascript">
function gotosubmit() {
	var value = getCookie("sessionid");
	if (value.length == 0) {
		value = "${session.getId()}";
		setCookie("sessionid", value);
	}

	document.forms[0].submit();
}
</script>
</head>

<body onload="javascript:gotosubmit();">
<form method="post" id="man001form" action="/dbm/jsp/man001.jsp">
</form>
</body>
</html>