var net = require('net');
var server = net.createServer(function (socket) {
	socket.on('data',function(so,id){
		console.log(socket.remotePort);
            var mbap    = so.slice(0, 0 + 7),
                len     = mbap.readUInt16BE(4);
                request = { 
                    trans_id: mbap.readUInt16BE(0),
                    protocol_ver: mbap.readUInt16BE(2),
                    unit_id: mbap.readUInt8(6) 
                }; 
            // 2. extract pdu
            var pdu = so.slice(7, 7 + len - 1);
            // emit data event and let the 
            // listener handle the pdu
            console.log({ request : request, pdu : pdu});
			var funcCode = pdu.slice(0 , 1).readUInt8(0);
			var startAddr = pdu.slice(1 , 3).readUInt16BE(0);
			var quantity = pdu.slice(3, 5).readUInt16BE(0);
			console.log(so);
			if( funcCode == 4 ){
				var resp =  new Buffer([0x00, (request.trans_id).toString(16), 0x00, 0x00, 0x00, 0x05, 0x00,0x04,0x02, 0x12, 0x34]);
				console.log(resp);
				socket.write(resp);
			}
	});
}).on('error', function (err) {
  throw err;
});

server.listen( {
  host: 'localhost',
  port: 502
}, function(){
  console.log('opened server on', server.address());
});
server.on('error',function(e){
  console.log(e);
});
server.on('data',function(e){
  console.log(e);
});
