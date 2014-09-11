<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看表定义</title>
<link rel="stylesheet" type="text/css" href="/dbm/css/default/easyui.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/icon.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/main.css">
<script type="text/javascript" src="/dbm/js/jquery.min.js"></script>
<script type="text/javascript" src="/dbm/js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="/dbm/js/main.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var rdata = ${rowobj};
	$('#favrid').datagrid('loadData', rdata);
});
</script>
</head>

<body>
<table id="favrid" class="easyui-datagrid" data-options="rownumbers:true,singleSelect:true" border="0">
    <thead>
        <tr>
            <th data-options="field:'colname',width:200,halign:'center'">列名</th>
            <th data-options="field:'type',width:80,halign:'center'">类型名</th>
            <th data-options="field:'size',width:70,halign:'center'">大小</th>
            <th data-options="field:'pk',width:30,align:'center'">主键</th>
            <th data-options="field:'nullable',width:30,align:'center'">为空</th>
            <th data-options="field:'remark',width:200,halign:'center'">注释</th>
        </tr>
    </thead>
</table>
</body>
</html>