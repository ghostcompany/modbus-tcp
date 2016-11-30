var modbus = require('jsmodbus');
var mssql = require('mssql'); 
var host = 'localhost';
var port1 = 502;
var port2 = 503;

var modbusClient1 = modbus.client.tcp.complete({ 
    'host'              : host, 
    'port'              : port1,
    'autoReconnect'     : true,
    'reconnectTimeout'  : 1000,
    'timeout'           : 5000,
    'unitId'            : 0
});

var modbusClient2 = modbus.client.tcp.complete({ 
    'host'              : host, 
    'port'              : port2,
    'autoReconnect'     : true,
    'reconnectTimeout'  : 1000,
    'timeout'           : 5000,
    'unitId'            : 0
});

var config = {
    user: 'rifqi',
    password: 'kediri123',
    server: 'BYON-PC\\SQLEXPRESS', // You can use 'localhost\\instance' to connect to named instance
    database: 'PertaminaTas',
};
var flagActive1 = false;
var flagActive2 = false;
var busy1 = false;
var busy2 = false;
var main = function(){
    if(flagActive1 &&  ( !(busy1) ) ){
        busy1 = true;
        modbusClient1.readInputRegisters(17, 18).then(function (resp) {
            console.log('client1',resp.register[0]);
            updateData(resp.register[0]);
            busy1 = false;
        }).fail(function(errx){
            console.log(errx);
            busy1 = false;
        });    
    }
    if(flagActive2 &&  ( !(busy2) ) ){
        busy2 = true;
        modbusClient2.readInputRegisters(17, 18).then(function (resp) {
            console.log('client2',resp.register[0]);
            updateData(resp.register[0]);
            busy2 = false;
        }).fail(function(errx){
            console.log('client2',errx);
            busy2 = false;
        });   
    }
};
var loop = function(){
    main();
    setTimeout(loop,2000);
}
mssql.connect(config).then(function() {
    modbusClient1.connect();
    modbusClient1.on('connect', function () {
        console.log('connect');
        loop();
        flagActive1 = true;
    });
    modbusClient1.on('error', function (err) {
        flagActive1 = false;
        console.log('client1',err);
    });
    modbusClient2.connect();
    modbusClient2.on('connect', function () {
        flagActive2 = true;
    });
    modbusClient2.on('error', function (err) {
        flagActive2 = false;
        console.log('client2',err);
    });
}).catch(function(err){
    console.log(err);
});
var updateData = function(data){
    var sql = "UPDATE [PertaminaTas].[dbo].[JembatanTimbang]";
    sql += " SET [nilai] = " + (data * 1);
    sql += " WHERE [kode] = 'WB01'"; 
    var request = new mssql.Request();
    request.query(sql).then(function(recordset) {
        // ... error checks
        console.dir(recordset);
    }).catch(function(err){
        console.log(err);
    });
};