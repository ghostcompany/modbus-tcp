var modbus = require('jsmodbus');

// create a modbus client
var client = modbus.client.tcp.complete({ 
        'host'              : host, 
        'port'              : port,
        'autoReconnect'     : true,
        'reconnectTimeout'  : 1000,
        'timeout'           : 5000,
        'unitId'            : 0
    });

client.connect();

// reconnect with client.reconnect()

client.on('connect', function () {

    // make some calls

    client.readCoils(0, 13).then(function (resp) {
	// resp will look like { fc: 1, byteCount: 20, coils: [ values 0 - 13 ], payload: <Buffer> } 
        console.log(resp);

    }).fail(console.log);

    client.readDiscreteInputs(0, 13).then(function (resp) {

        // resp will look like { fc: 2, byteCount: 20, coils: [ values 0 - 13 ], payload: <Buffer> } 
        console.log(resp);

    }).fail(console.log);

    client.readHoldingRegisters(0, 10).then(function (resp) {

        // resp will look like { fc: 3, byteCount: 20, register: [ values 0 - 10 ], payload: <Buffer> }
        console.log(resp); 

    }).fail(console.log);

    client.readInputRegisters(0, 10).then(function (resp) {

        // resp will look like { fc: 4, byteCount: 20, register: [ values 0 - 10 ], payload: <Buffer> }
        console.log(resp);

    }).fail(console.log);

    client.writeSingleCoil(5, true).then(function (resp) {

        // resp will look like { fc: 5, byteCount: 4, outputAddress: 5, outputValue: true }
        console.log(resp);

    }).fail(console.log);

    client.writeSingleCoil(5, new Buffer(0x01)).then(function (resp) {

        // resp will look like { fc: 5, byteCount: 4, outputAddress: 5, outputValue: true }
        console.log(resp);

    }).fail(console.log);

    client.writeSingleRegister(13, 42).then(function (resp) {

        // resp will look like { fc: 6, byteCount: 4, registerAddress: 13, registerValue: 42 }
        console.log(resp);

    }).fail(console.log);

    client.writeSingleRegister(13, new Buffer([0x00,0x2A])).then(function (resp) {

        // resp will look like { fc: 6, byteCount: 4, registerAddress: 13, registerValue: 42 }
        console.log(resp);

    }).fail(console.log);

    client.writeMultipleCoils(3, [1, 0, 1, 0, 1, 1]).then(function (resp) {

        // resp will look like { fc: 15, startAddress: 3, quantity: 6 }
        console.log(resp); 

    }).fail(console.log);

    client.writeMultipleCoils(3, new Buffer([0x2B]), 6).then(function (resp) {

        // resp will look like { fc: 15, startAddress: 3, quantity: 6 }
        console.log(resp); 

    }).fail(console.log);

    client.writeMultipleRegisters(4, [1, 2, 3, 4]).then(function (resp) {

        // resp will look like { fc : 16, startAddress: 4, quantity: 4 }
        console.log(resp);

    }).fail(console.log);

});

client.on('error', function (err) {

    console.log(err);

})