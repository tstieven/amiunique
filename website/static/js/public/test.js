var ERROR = "error"
var jsonVal={};
    function generateFingerprint(){
        var fp = {}
        return new Promise(function(resolve, reject) {
            var promiseArray = [];
function getAdBlock(){
	var ads = document.createElement("div");
	ads.innerHTML = "&nbsp;";
	ads.className = "adsbox";
	var result = false;
	try {
		// body may not exist, that's why we need try/catch
		document.body.appendChild(ads);
		result = document.getElementsByClassName("adsbox")[0].offsetHeight === 0;
		document.body.removeChild(ads);
	} catch (e) {
		result = false;
	}
	return result;
}

fp.adBlock = getAdBlock();
 
 
 
function getLanguage(){
	return navigator.language || navigator.userLanguage || navigator.browserLanguage || navigator.systemLanguage || "";
}

fp.languageHttp = getLanguage();
 
 
 
function getUserAgent(){
	return navigator.userAgent;
}

fp.userAgentHttp = getUserAgent();   return Promise.all(promiseArray).then(function () {
    return resolve(fp);
});
})
; }
            function map(obj, iterator, context) {
    var results = [];
    if (obj == null) {
        return results;
    }
    if (this.nativeMap && obj.map === this.nativeMap) {
        return obj.map(iterator, context);
    }
    this.each(obj, function (value, index, list) {
        results[results.length] = iterator.call(context, value, index, list);
    });
    return results;
}
function each(obj, iterator, context) {
    if (obj === null) {
        return;
    }
    if (this.nativeForEach && obj.forEach === this.nativeForEach) {
        obj.forEach(iterator, context);
    } else if (obj.length === +obj.length) {
        for (var i = 0, l = obj.length; i < l; i++) {
            if (iterator.call(context, obj[i], i, obj) === {}) {
                return;
            }
        }
    } else {
        for (var key in obj) {
            if (obj.hasOwnProperty(key)) {
                if (iterator.call(context, obj[key], key, obj) === {}) {
                    return;
                }
            }
        }
    }
}
generateFingerprint().then(function (val) {
 jsonVal = val;
    console.log(val);});

//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImFkQmxvY2suanMiLCJsYW5ndWFnZS5qcyIsInVzZXJBZ2VudC5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOzs7O0FDaEJBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7Ozs7QUNKQTtBQUNBO0FBQ0E7QUFDQTtBQUNBIiwiZmlsZSI6InRlc3QuanMiLCJzb3VyY2VzQ29udGVudCI6WyJmdW5jdGlvbiBnZXRBZEJsb2NrKCl7XG5cdHZhciBhZHMgPSBkb2N1bWVudC5jcmVhdGVFbGVtZW50KFwiZGl2XCIpO1xuXHRhZHMuaW5uZXJIVE1MID0gXCImbmJzcDtcIjtcblx0YWRzLmNsYXNzTmFtZSA9IFwiYWRzYm94XCI7XG5cdHZhciByZXN1bHQgPSBmYWxzZTtcblx0dHJ5IHtcblx0XHQvLyBib2R5IG1heSBub3QgZXhpc3QsIHRoYXQncyB3aHkgd2UgbmVlZCB0cnkvY2F0Y2hcblx0XHRkb2N1bWVudC5ib2R5LmFwcGVuZENoaWxkKGFkcyk7XG5cdFx0cmVzdWx0ID0gZG9jdW1lbnQuZ2V0RWxlbWVudHNCeUNsYXNzTmFtZShcImFkc2JveFwiKVswXS5vZmZzZXRIZWlnaHQgPT09IDA7XG5cdFx0ZG9jdW1lbnQuYm9keS5yZW1vdmVDaGlsZChhZHMpO1xuXHR9IGNhdGNoIChlKSB7XG5cdFx0cmVzdWx0ID0gZmFsc2U7XG5cdH1cblx0cmV0dXJuIHJlc3VsdDtcbn1cblxuZnAuYWRCbG9jayA9IGdldEFkQmxvY2soKTsiLCJmdW5jdGlvbiBnZXRMYW5ndWFnZSgpe1xuXHRyZXR1cm4gbmF2aWdhdG9yLmxhbmd1YWdlIHx8IG5hdmlnYXRvci51c2VyTGFuZ3VhZ2UgfHwgbmF2aWdhdG9yLmJyb3dzZXJMYW5ndWFnZSB8fCBuYXZpZ2F0b3Iuc3lzdGVtTGFuZ3VhZ2UgfHwgXCJcIjtcbn1cblxuZnAubGFuZ3VhZ2VIdHRwID0gZ2V0TGFuZ3VhZ2UoKTsiLCJmdW5jdGlvbiBnZXRVc2VyQWdlbnQoKXtcblx0cmV0dXJuIG5hdmlnYXRvci51c2VyQWdlbnQ7XG59XG5cbmZwLnVzZXJBZ2VudEh0dHAgPSBnZXRVc2VyQWdlbnQoKTsiXX0=
