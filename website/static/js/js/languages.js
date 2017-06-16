function getLanguages(){
	if(navigator.languages){
		return navigator.languages.join("~~");
	}
	return "unknown";
}

fp.languages = getLanguages();