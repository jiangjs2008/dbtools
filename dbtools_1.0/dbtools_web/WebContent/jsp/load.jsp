<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>异步加载表头</title>
<script type="text/javascript" src="/dbm/js/jquery-1.6.4.min.js"></script>
<script type="text/javascript" src="/dbm/js/om-ui.js"></script>
<link rel="stylesheet" type="text/css" href="/dbm/css/om-ui.css">

<!-- view_source_begin -->
<script type="text/javascript">
    $(document).ready(function(){
    	var col;
    	//首先从服务器端获取表头数据，再初始化数据表
    	$.getJSON("/dbm/ajax/gridcol.do?tblname=FeaturePhoneOrderTemp", function(data) {
    		col = data;
    		$("#grid").omGrid({
        		dataSource: "/dbm/ajax/griddata.do?tblname=FeaturePhoneOrderTemp",
                height : 600,
                limit : 10,
                colModel : data
        	});
    	});
    });
</script>
<!-- view_source_end -->
</head>

<body>
<!-- view_source_begin -->
<table id="grid"></table>
<!-- view_source_end -->
</body>
</html>