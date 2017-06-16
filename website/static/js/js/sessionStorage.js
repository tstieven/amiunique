function getSessionStorage(){
	try {
			return !!window.sessionStorage;
		} catch(e) {
			return true; // SecurityError when referencing it means it exists
		}
}

fp.sessionStorage = getSessionStorage();