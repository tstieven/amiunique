function getMimeTypes(){
	var mimeTypes = [];
	for(var i = 0; i < navigator.mimeTypes.length; i++){
		var mt = navigator.mimeTypes[i];
		mimeTypes.push([mt.description, mt.type, mt.suffixes].join("~~"));
	}
	return mimeTypes.join(";;");
}

fp.mimeTypes = getMimeTypes();
