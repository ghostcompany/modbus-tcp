var modbus = require('jsmodbus');
var mssql = require('mssql'); 
var host = 'localhost';
var port = 502;

var modbusClient = modbus.client.tcp.complete({ 
    'host'              : host, 
    'port'              : port,
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

mssql.connect(config).then(function() {
    modbusClient.connect();
    modbusClient.on('connect', function () {
        setInterval(function(){
            modbusClient.readInputRegisters(17, 18).then(function (resp) {
                console.log(resp.register[0]);
                updateData(resp.register[0]);
            }).fail(function(errx){
                console.log(errx);
            });
        },2000);
    });
    modbusClient.on('error', function (err) {
        console.log(err);
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
