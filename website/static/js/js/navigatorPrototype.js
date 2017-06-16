function getNavigatorPrototype(){
	var obj = window.navigator;
	var protoNavigator = [];
	do Object.getOwnPropertyNames(obj).forEach(function(name) {
		protoNavigator.push(name);
	});
	while(obj = Object.getPrototypeOf(obj));

  var res;
  var finalProto = [];
  protoNavigator.forEach(function(prop){
    var objDesc = Object.getOwnPropertyDescriptor(Object.getPrototypeOf(navigator), prop);
    if(objDesc != undefined){
      if(objDesc.value != undefined){
          res = objDesc.value.toString();
      }else if(objDesc.get != undefined){
          res = objDesc.get.toString();
      }
    }else{
        res = "";
    }
    finalProto.push(prop+"~~~"+res);

  });
	return finalProto.join(";;;");
}

fp.navigatorPrototype = getNavigatorPrototype();