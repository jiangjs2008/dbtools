
var nowDate = new Date();

$(document).ready(function() {
	$("#favrid").combobox({
		url: '/dbm/ajax/getdblist.do?t=' + nowDate.getTime(),
		method: 'get',
		valueField: 'favrid',
		textField: 'name',
		panelWidth: 450,
		panelHeight: 'auto',
		formatter: formatItem,
		onSelect: function(param){
			$.getJSON("/dbm/ajax/getdblogininfo.do?favrid=" + param.favrid + '&t=' + nowDate.getTime(), function(data) {
				if (data.status == 'ok') {
					$("#account").val(data.account);
					$("#password").val(data.password);
				}
			});
		}
	});
	$("#favrid").combobox("setValue", "请选择数据库：");
});

function formatItem(row) {
	var s = '<div style="font-size:16px;height:20px;line-height:20px"><div style="float:left">' + row.name + '</div><div style="float:right;color:#888;margin-right:2px">' + row.description + '</div></div>';
	return s;
}

function submitForm() {
	var account = $("#account").val();
	if (account) {
		account = base64encode(account);
	}
	var password = $("#password").val();
	if (password) {
		password = base64encode(password);
	}
	var aurl = "/dbm/ajax/login.do?favrid=" + $("#favrid").combobox('getValue');
	aurl += "&user=" + account + "&pwd=" + password;
	$.getJSON(aurl + '&t=' + nowDate.getTime(), function(data) {
		if (data.ecd == 'ok') {
			location.href = '/dbm/jsp/man002.jsp';
		} else {
			showerror(data.ecd);
		}
	});
}
