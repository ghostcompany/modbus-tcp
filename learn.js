var net = require('net');
var server = net.createServer(function (socket) {
  socket.write(new Buffer(10));
}).on('error', function (err) {
  throw err;
});

server.listen( {
  host: 'localhost',
  port: 9000
}, function(){
  console.log('opened server on', server.address());
});
server.on('error',function(e){
  console.log(e);
});
