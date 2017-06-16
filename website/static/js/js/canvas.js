function getCanvasFp() {
    try {
        canvas = document.createElement("canvas");
        canvas.height = 60;
        canvas.width = 400;
        canvasContext = canvas.getContext("2d");
        canvas.style.display = "inline";
        canvasContext.textBaseline = "alphabetic";
        canvasContext.fillStyle = "#f60";
        canvasContext.fillRect(125, 1, 62, 20);
        canvasContext.fillStyle = "#069";
        canvasContext.font = "11pt no-real-font-123";
        canvasContext.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03", 2, 15);
        canvasContext.fillStyle = "rgba(102, 204, 0, 0.7)";
        canvasContext.font = "18pt Arial";
        canvasContext.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03", 4, 45);
        canvasData = canvas.toDataURL();
    }
    catch(e){
        canvasData = "Not supported";
    }

    return {url: canvasData, data:canvasContext.getImageData(0, 0, canvas.width, canvas.height).data};
}

canvasObj = getCanvasFp();
fp.canvasJs = canvasObj.url;