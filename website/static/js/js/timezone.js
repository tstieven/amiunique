function getTimezone(){
	return new Date().getTimezoneOffset();
}

fp.timezone = getTimezone();