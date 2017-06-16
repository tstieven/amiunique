function getNavigatorCpuClass(){
	if(navigator.cpuClass){
		return navigator.cpuClass;
	}
	return "unknown";
}

fp.cpuClass = getNavigatorCpuClass();