function getLanguage(){
	return navigator.language || navigator.userLanguage || navigator.browserLanguage || navigator.systemLanguage || "";
}

fp.languageHttp = getLanguage();