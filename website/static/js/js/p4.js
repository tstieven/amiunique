 function generateUnknownImageError(){
    return new Promise(function(resolve, reject){
        function getError(){
            var body = document.getElementsByTagName("body")[0];
            var image = document.createElement("img");
            image.src = "http://iloveponeydotcom32188.jg";
            image.setAttribute("id", "fakeimage");
            body.appendChild(image);
            image = document.getElementById("fakeimage");
            image.onerror = function (errorMsg, url, lineNumber) {
                 resolve([image.width, image.height].join(";"));
            }
        }
        if(document.readyState == "complete"){
              getError();
        }else{
          document.addEventListener("DOMContentLoaded", function(event){
            getError();
          });
      }
    });
}

 var p4 = new Promise(function(resolve, reject){
          generateUnknownImageError().then(function(val){
              fp.unknownImageError = val;
              return resolve(fp);
          });
      });
 promiseArray.push(p4);
