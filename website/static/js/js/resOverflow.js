function generateStackOverflow(){
    var depth = 0;
    var errorMessage;
    var errorName;
    function inc(){
        try{
            depth++;
            inc();
        }catch(e){
        errorMessage = e.message;
        errorName = e.name;
        }
    }

    inc();
	return [depth, errorName, errorMessage].join(";;;");
}     

fp.resOverflow = generateStackOverflow();