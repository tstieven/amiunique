 function getAvailableScreenResolution(){
	if(screen.availWidth && screen.availHeight) {
		return [screen.availWidth, screen.availHeight].join(",");
	}
	return "unknown";
}

fp.availableScreenResolution = getAvailableScreenResolution();