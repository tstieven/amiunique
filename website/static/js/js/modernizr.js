function testModernizr(){
  var propertiesVec = [];
  var modernizrProperties = Object.getOwnPropertyNames(Modernizr);
  modernizrProperties.forEach(function(prop){
    if(typeof Modernizr[prop] == "boolean"){
        propertiesVec.push(prop+"-"+Modernizr[prop].toString());
    }
  });
  return propertiesVec.join(";");
}

fp.modernizr = testModernizr();