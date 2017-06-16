function getDoNotTrack() {
	if(navigator.doNotTrack) {
		return navigator.doNotTrack;
	} else if (navigator.msDoNotTrack) {
		return navigator.msDoNotTrack;
	} else if (window.doNotTrack) {
		return window.doNotTrack;
	}
	return "unknown";
}

fp.doNotTrack = getDoNotTrack();