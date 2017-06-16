function getOscpu(){
	if(navigator.oscpu){
		return navigator.oscpu;
	}
	return "unknown";
}

fp.oscpu = getOscpu();