function getBuildId(){
	if(navigator.buildID){
		return navigator.buildID;
	}
	return "unknown";
} 

fp.buildID = getBuildId();