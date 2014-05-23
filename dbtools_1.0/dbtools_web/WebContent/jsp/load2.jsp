<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>异步加载表头</title>
<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="../js/jquery.handsontable.full.js"></script>
<link rel="stylesheet" type="text/css" href="../css/jquery.handsontable.full.css">

<!-- view_source_begin -->
<script type="text/javascript">
    $(document).ready(function(){

    	$.getJSON("../ajax/alldata.do?tblname=FeaturePhoneOrderTemp", function(data) {

			$("#dataTable").handsontable({
				data: data.gridata,
				colHeaders : data.coldata,
				rowHeaders : true
			});
		});

    });
</script>
<!-- view_source_end -->
</head>

<body>
<!-- view_source_begin -->
<div id="dataTable" style="width:1000px;height:600px;"></div>
<!-- view_source_end -->
</body>
</html>