function getIndexedDb(){
	try {
			return !!window.indexedDB;
		} catch(e) {
			return true; // SecurityError when referencing it means it exists
		}
}

fp.indexedDb = getIndexedDb();