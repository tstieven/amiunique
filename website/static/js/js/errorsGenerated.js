function generateErrors(){
    var errors = [];
    try{
        azeaze + 3;
    }catch(e){
        errors.push(e.message);
        errors.push(e.fileName);
        errors.push(e.lineNumber);
        errors.push(e.description);
        errors.push(e.number);
        errors.push(e.columnNumber);
        try{
            errors.push(e.toSource().toString());
        }catch(e){
            errors.push(undefined);
        }
    }

    try{
    var a = new WebSocket("itsgonnafail");
  }catch(e){
    errors.push(e.toString());
  }

    return errors.join("~~~");
}    

fp.errorsGenerated = generateErrors();