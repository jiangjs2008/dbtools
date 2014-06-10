<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看表</title>
<script type="text/javascript" src="/dbm/js/jquery-1.6.4.min.js"></script>
<script type="text/javascript" src="/dbm/js/om-ui.js"></script>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-default.css">
<link rel="stylesheet" type="text/css" href="/dbm/css/main.css">
<script type="text/javascript">

$(document).ready(function() {

	$("#tblgrid").omGrid({
		height: 'fit',
		width : 'fit',
		limit : 0,
        colModel : [ {header : '列名', name : 'colname', width : 200}, 
                     {header : '类型名', name : 'type', width : 100}, 
                     {header : '大小', name : 'size', width : 100}, 
                     {header : '主键', name : 'pk', width : 100}, 
                     {header : '为空', name : 'nullable', width : 100},
                     {header : '注释', name : 'remark', width : 200} ]
	});

	$("#tblgrid").omGrid('setData', "/dbm/ajax/tblinfo.do?tblname=${tblname}&t=" + parseInt(Math.random()*100000));
});
</script>
</head>

<body>
<table id="tblgrid"></table>
</body>
</html>