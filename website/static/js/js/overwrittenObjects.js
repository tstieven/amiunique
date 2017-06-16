function testOverwrittenObjects(){
    var screenTest;
    try{
        screenTest = Object.getOwnPropertyDescriptor(Object.getPrototypeOf(screen), "width").get.toString();
    } catch(e){
        screenTest = ERROR;
    }
    var canvasTest;
    try{
        canvasTest = Object.getOwnPropertyDescriptor(window.HTMLCanvasElement.prototype, "toDataURL").value.toString();
    } catch(e){
        canvasTest = ERROR;
    }

    var dateTest;
    try{
        dateTest = Object.getOwnPropertyDescriptor(Date.prototype, "getTimezoneOffset").value.toString();
    } catch(e) {
        dateTest = ERROR;
    }

    // We separe with weird characters in case they use dash in their overwritten functions
    return screenTest+"~~~"+canvasTest+"~~~"+dateTest;
}

fp.overwrittenObjects = testOverwrittenObjects();