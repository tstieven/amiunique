function getScreenResolution(){
	return [screen.width, screen.height].join(",");
}

fp.screenResolution = getScreenResolution();