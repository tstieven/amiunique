function getPlugins(){
	var plugins = [];
	for(var i = 0, l = navigator.plugins.length; i < l; i++) {
		plugins.push(navigator.plugins[i]);
	}
	return this.map(plugins, function (p) {
		var mimeTypes = this.map(p, function(mt){
			return [mt.type, mt.suffixes].join("~");
		}).join(",");
		return [p.name, p.description, p.filename, mimeTypes].join("::");
	}, this);
}

fp.plugins = getPlugins().join(";;;");