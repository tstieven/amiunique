function testCanvasValue(imgData){
  var r, g, b, a;

  var height = 60;
  var width = 400;
  var binarizedImg = new Array(height + 2);
  for(var i = 0; i < binarizedImg.length; i++){
    binarizedImg[i] = new Array(width + 2);
  }

  for(var j = 0; j < binarizedImg[0].length; j++){
    binarizedImg[0][j] = false;
    binarizedImg[binarizedImg.length - 1][j] = false;
  }

  for(var i = 0; i < binarizedImg.length; i++){
    binarizedImg[i][0] = false;
    binarizedImg[i][binarizedImg[0].length - 1] = false;
  }
  var binValue;
  nbZeroElts = 0;
  var xi = 1, yi = 1;
  for(var i = 0; i < imgData.length; i = i+4){
    r = imgData[i];
    g = imgData[i+1];
    b = imgData[i+2];
    a = imgData[i+3];
    if(r ==0 && g ==0 && b ==0 && a == 0){
        binValue = false;
        nbZeroElts++;
    } else{
        binValue = true;
    }
    if((yi)% width == 0){
        yi = 1;
        xi++;
    }else{
        yi++;
    }
    //probably a problem with offsets, check it later...
    binarizedImg[xi][yi] = binValue;
  }

  var isolatedCells = [];
  for(var i = 1; i < binarizedImg.length-1; i++){
    for(var j = 1; j < binarizedImg[0].length-1; j++){
      var isolated = true;
      if(binarizedImg[i][j] == true){
        if(!binarizedImg[i-1][j-1] && !binarizedImg[i-1][j] && !binarizedImg[i-1][j+1] &&
        !binarizedImg[i][j-1] && !binarizedImg[i][j+1] && !binarizedImg[i+1][j-1] && !binarizedImg[i+1][j] &&
        !binarizedImg[i+1][j+1]){
            //all cells are empty next to i,j
            isolatedCells.push([i-1, j-1]);
        }
      }
    }
  }

  return [nbZeroElts, isolatedCells.join("~~")].join(";;");
}

fp.canvasPixels = testCanvasValue(canvasObj.data);
