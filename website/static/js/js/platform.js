function getNavigatorPlatform(){
	if(navigator.platform) {
		return navigator.platform;
	}
	return "unknown";
}

fp.platform = getNavigatorPlatform();