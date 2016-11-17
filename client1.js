var net = require('net');

var c = createConnection(9000, '127.0.0.1');

function createConnection(port, server) {
    c = net.connect(port, server);

    console.log('new connection');

    c.on('error', function (error) {
      setTimeout(function(){
        console.log('error, trying again');
        //c = createConnection(port, server);
        c = c.connect(port, server);
      }, 1000);
    });
    
    c.on('data', function(message){
      console.log(message);
    });

    return c;
}
/*
client.on('data', function (data) {
  console.log(data.toString());
  client.end();
});
client.on('end', function () {
  console.log('disconnected from server');
});
client.on('error', function (err) {
  console.log('trying again');
  //client = net.createConnection( 9000 , '127.0.0.1');
});
*/
