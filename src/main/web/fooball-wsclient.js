if (!window.WebSocket) {
  window.WebSocket = window.MozWebSocket;
}
var FOOBALL = FOOBALL || {};
  
FOOBALL.wsclient = {
  connectDefault : function(url, ta) {
  	return this.connect(
  			url, 
  			function(event) { ta.value = ta.value + event.data + '\n'; ta.scrollTop = ta.scrollHeight; },
  			function(event) { ta.value = ta.value + "Web Socket opened!\n"; ta.scrollTop = ta.scrollHeight; },
  			function(event) { ta.value = ta.value + "Web Socket closed\n"; ta.scrollTop = ta.scrollHeight; });
  },
  
  connect : function(url, onmessage, onopen, onclose) {
	  if (window.WebSocket) {
	    var socket = new WebSocket(url);
	    socket.onmessage = onmessage;
	    socket.onopen = onopen;
	    socket.onclose = onclose;
	    return socket;
	   } else { 
	    alert("Your browser does not support Web Sockets.");
	  }
  },
  
  send : function(socket, message) {
    if (!window.WebSocket) { alert("Your browser does not support Web Sockets"); return; }
    if (socket.readyState == WebSocket.OPEN) {
      socket.send(message);
    } else {
      alert("The socket is not open.");
    }
  }
};