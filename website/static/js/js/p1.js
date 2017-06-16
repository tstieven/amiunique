function getIPs(callback){
	var ip_dups = {};
	//compatibility for firefox and chrome
	var RTCPeerConnection = window.RTCPeerConnection
			|| window.mozRTCPeerConnection
			|| window.webkitRTCPeerConnection;
	var useWebKit = !!window.webkitRTCPeerConnection;
	//minimal requirements for data connection
	var mediaConstraints = {
			optional: [{RtpDataChannels: true}]
	};
	var servers = {iceServers: [{urls: "stun:stun.services.mozilla.com"}]};
	//construct a new RTCPeerConnection
	var pc = new RTCPeerConnection(servers, mediaConstraints);
	function handleCandidate(candidate){
			//match just the IP address
			var ip_regex = /([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/
			var ip_addr = ip_regex.exec(candidate)[1];
			//remove duplicates
			if(ip_dups[ip_addr] === undefined)
					callback(ip_addr);
			ip_dups[ip_addr] = true;
	}
	//listen for candidate events
	pc.onicecandidate = function(ice){
			//skip non-candidate events
			if(ice.candidate)
					handleCandidate(ice.candidate.candidate);
	};
	//create a bogus data channel
	pc.createDataChannel("");
	//create an offer sdp
	pc.createOffer(function(result){
			//trigger the stun server request
			pc.setLocalDescription(result, function(){}, function(){});
	}, function(){});
	//wait for a while to let everything done
	setTimeout(function(){
			//read candidate info from local description
			var lines = pc.localDescription.sdp.split('\n');
      // console.log(lines);
			lines.forEach(function(line){
					if(line.indexOf('a=candidate:') === 0)
							handleCandidate(line);
			});
	}, 1000);
}

function getLocalIP(){
	return new Promise(function(resolve, reject){
		getIPs(function(ip){
			//local IPs
			if (ip.match(/^(192\.168\.|169\.254\.|10\.|172\.(1[6-9]|2\d|3[01]))/)){
					return resolve(ip);
			}
		});
	});
}

var p1 = new Promise(function(resolve, reject){
    getLocalIP().then(function(val){
        fp.localIP = val;
        return resolve(fp);
    });
});

      // TODO: add p1 later
      // Problem currently if no private address
//array.push(p1);