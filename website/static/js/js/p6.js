function getOSMq(){
    return new Promise(function(resolve, reject){

        function runMQ(){
            var divTest = document.createElement("div");
            var body = document.getElementsByTagName("body")[0];
            body.appendChild(divTest);

            var macP = document.createElement("p");
            macP.setAttribute("id", "testmac1");
            var winxpP = document.createElement("p");
            winxpP.setAttribute("id", "testwinxp");
            var winvisP = document.createElement("p");
            winvisP.setAttribute("id", "testwinvis");
            var win7P = document.createElement("p");
            win7P.setAttribute("id", "testwin7");
            var win8P = document.createElement("p");
            win8P.setAttribute("id", "testwin8");

            divTest.appendChild(macP);
            divTest.appendChild(winxpP);
            divTest.appendChild(winvisP);
            divTest.appendChild(win7P);
            divTest.appendChild(win8P);

            var queryMatchedColor = "red";
            var res = [];

            if(macP.style.color == queryMatchedColor){
                res.push("true");
            }else{
                res.push("false");
            }

            if(winxpP.style.color == queryMatchedColor){
                res.push("true");
            }else{
                res.push("false");
            }

            if(winvisP.style.color == queryMatchedColor){
                res.push("true");
            }else{
                res.push("false");
            }

            if(win7P.style.color == queryMatchedColor){
                res.push("true");
            }else{
                res.push("false");
            }

            if(win8P.style.color == queryMatchedColor){
                res.push("true");
            }else{
                res.push("false");
            }
            return res;
      }

      if(document.readyState == "complete"){
            return resolve(runMQ().join(";"));
      } else{
        document.addEventListener("DOMContentLoaded", function(event){
            return resolve(runMQ().join(";"));
        });
      }
   });
}


var osMediaqueries = "";
      var p6 = new Promise(function(resolve, reject){
          getOSMq().then(function(val){
              fp.osMediaqueries = val;
              return resolve();
          });
      });
promiseArray.push(p6);