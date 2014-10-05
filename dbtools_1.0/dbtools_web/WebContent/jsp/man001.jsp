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

$(document).ready(function() {
	$.getJSON("/dbm/ajax/getdblist.do", initCombo);
	$('#aButton').omButton({onClick : man001Submit});
	$('#bButton').omButton({onClick : createOpid});

    $('#panel1').omPanel({
        width: '500px',
        height: 'auto',
        title: '数据库登录'
    });
    $('#panel2').omPanel({
        width: '500px',
        height: 'auto',
        title: '可选项--缓存设置',
        collapsed: true,
        collapsible: true
    });

	var value = getCookie('clientid');
	if (value == null || value == "") {
		$("#opid").val("");
		$("#opid1").val("");
	} else {
		$("#opid").val(value);
		$("#opid1").val(value);
	}
});
</script>
</head>

<body onload="javascript:showerror('${ecd}');" style="margin:0px auto;width:500px;padding-top:130px">
<div id="panel1">
<table cellspacing="0" cellpadding="0" border="0" style="width:100%;height:180px;padding-left:15px;font-size:14px" align="center">
	<tr>
		<td style="width:140px">请选择数据库：</td>
		<td ><input id="combo1" style="width:200px"/></td>
	</tr>
	<tr>
		<td>用户：</td>
		<td ><input type="text" id="account1" style="width:150px;line-height:20px;height:20px"/></td>
	</tr>
	<tr>
		<td>密码：</td>
		<td ><input type="password" id="password1" style="width:150px;line-height:20px;height:20px"/></td>
	</tr>
	<tr>
		<td align="center" colspan="2"><input type="button" id="aButton" value="确定" style="width:70px"/></td>
	</tr>
</table>
</div>
<div id="panel2">
请输入客户端识别编号：&nbsp;&nbsp;&nbsp;<input type="text" id="opid1" style="width:100px;line-height:20px;height:20px"/><br/><br/>
此编号由系统自动生成，用于判别客户身份，保存操作纪录。<br/>
此编号保存在客户端浏览器cookie中，清除浏览器缓存时将会被删除，因此请记牢你的编号。<br/>
忘记编号也没关系，只是以前的操作纪录不可查询了。<br/>
若是第一次访问，请点击右边的[创建编号]按钮，生成新的编号。&nbsp;&nbsp;&nbsp;
<input type="button" id="bButton" value="创建编号" style="width:80px"/><br/>
</div>
<form method="post" id="man001form" action="/dbm/login.do">
<input type="hidden" id="favrid" name="favrid"/>
<input type="hidden" id="account" name="user"/>
<input type="hidden" id="password" name="pwd"/>
<input type="hidden" id="opid" name="opid"/>
</form>
</body>
</html>