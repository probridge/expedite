function openWin(url, name) {
	if (!url) return false;
	if (!name)
		name = "_blank";
	window
			.open(
					url,
					name,
					"toolbar=no, scrollbars=yes, location=no, menubar=no, directories=no, resizable=yes,"
							+ popup_params(1200, 800));
	return false;
}

function popup_params(width, height) {
	var a = typeof window.screenX != 'undefined' ? window.screenX
			: window.screenLeft;
	var i = typeof window.screenY != 'undefined' ? window.screenY
			: window.screenTop;
	var g = typeof window.outerWidth != 'undefined' ? window.outerWidth
			: document.documentElement.clientWidth;
	var f = typeof window.outerHeight != 'undefined' ? window.outerHeight
			: (document.documentElement.clientHeight - 22);
	var h = (a < 0) ? window.screen.width + a : a;
	var left = parseInt(h + ((g - width) / 2), 10);
	var top = parseInt(i + ((f - height) / 2.5), 10);
	return 'width=' + width + ',height=' + height + ',left=' + left + ',top='
			+ top;
}
function show_personal_info_dialog() {
	$('#personalInfoDialog').modal();
}
function show_export_dialog() {
	$('#exportFormDataDialog').modal();
}
