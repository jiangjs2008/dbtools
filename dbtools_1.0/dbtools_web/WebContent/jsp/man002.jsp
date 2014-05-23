<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看表</title>
<script type="text/javascript" src="/dbm/js/jquery-1.6.4.min.js"></script>
<script type="text/javascript" src="/dbm/js/om-ui.js"></script>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-ui.css">
<style>
	html, body{ width: 100%; height: 100%; padding: 0; margin: 0;}
</style>
<script type="text/javascript">
$(document).ready(function() {
	var element = $('body').omBorderLayout({
		panels:[{id:"center-panel", header:false, region:"center"},
				{id:"west-panel", resizable:true, collapsible:true, title:"数据库表一览", region:"west", width:150}]
	});
	$("#mytree2").omTree({
	    dataSource : citydata,
	    onSelect: function(nodedata) {
	    	if (!nodedata.children && nodedata.text) {
	    		//避免在IE浏览器下出现中文乱码
	    		var url = encodeURI("griddata.do?method=filter&city="+nodedata.text);
	    		$("#grid").omGrid("setData", url);
	    	} else {
	    		$("#grid").omGrid("setData", "griddata.do?method=fast");
	    	}
	    }
	});

	$('#grid').omGrid({
	    dataSource : 'griddata.do?method=fast',
	    limit: 14,
	    height: 'fit',
	    width : 'fit',
	    title : '标题',
	    colModel : [ {header : 'ID', name : 'id', width : 80, align : 'center'}, 
	                 {header : '地区', name : 'city', width : 100, align : 'left'}, 
	                 {header : '地址', name : 'address', align : 'left', width : 'autoExpand'} ]
	});
	$(window).resize(function() {
	    $('#grid').omGrid("resize");
	});
});
</script>
</head>

<body>
<div id="center-panel">
    <table id="grid"></table>
</div>
<div id="west-panel">
    <ul id="mytree2"></ul>
</div>
</body>
</html>