function get(url) {
  // Return a new promise.
  return new Promise(function(resolve, reject) {
    // Do the usual XHR stuff
    var req = new XMLHttpRequest();
    req.open('GET', url);

    req.onload = function() {
      // This is called even on 404 etc
      // so check the status
      if (req.status == 200) {
        // Resolve the promise with the response text
        resolve(req.response);
      }
      else {
        // Otherwise reject with the status text
        // which will hopefully be a meaningful error
        reject(Error(req.statusText));
      }
    };

    // Handle network errors
    req.onerror = function(e) {
      reject(Error("Network Error"));
    };

    // Make the request
    req.send();
  });
}

function getHTTPHeaders(url){
  return new Promise(function(resolve, reject){
    get(url).then(function(response) {
        httpHeaders = JSON.parse(response);
        headersProperties = Object.getOwnPropertyNames(httpHeaders);
        res = [];
        headersProperties.forEach(function(prop){
            res.push(prop+";;"+httpHeaders[prop]);
        });
        resolve(res.join("~~~"));
      }, function(error) {
        reject(error);
      })
  });
}


var p2 = new Promise(function(resolve, reject){
    getHTTPHeaders("/headers").then(function(val){
        fp.httpHeaders = val;
        return resolve(fp);
    });
});
promiseArray.push(p2);