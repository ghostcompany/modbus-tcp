/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  net.wimpi.modbus.io.ModbusTCPTransaction
 *  net.wimpi.modbus.msg.ModbusRequest
 *  net.wimpi.modbus.msg.ModbusResponse
 *  net.wimpi.modbus.msg.ReadInputRegistersRequest
 *  net.wimpi.modbus.msg.ReadInputRegistersResponse
 *  net.wimpi.modbus.net.TCPMasterConnection
 */
//package ab;

import pkg.*;

import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;

public class Satorius {
    private static final long serialVersionUID = 8907188755122105165L;
    private DatabaseManager db = DatabaseManager.getInstance();
    private static Satorius instance = new Satorius();
    private TCPMasterConnection conn1 = null;
	private InetAddress addr = null;
    private Thread th = null;
    private boolean jalan = true;
	private boolean flagFirst = false;
    //private static String ip = "10.103.78.27";
    private static String ip = "127.0.0.1";

    private Satorius() {
    }

    private void connect() throws Exception {
        //byte[] b = new byte[]{10, 103, 78, 27};
        byte[] b = new byte[]{127, 0, 0, 1};
        String[] split = ip.split("\\.");
        if (split.length == 4) {
            try {
                byte b1 = Byte.parseByte(split[0]);
                byte b2 = Byte.parseByte(split[1]);
                byte b3 = Byte.parseByte(split[2]);
                byte b4 = Byte.parseByte(split[3]);
                b[0] = b1;
                b[1] = b2;
                b[2] = b3;
                b[3] = b4;
            }
            catch (Exception e) {
                System.out.println("Incorrect ip " + ip);
            }
        }
        System.out.println("Connecting to: " + b[0] + "." + b[1] + "." + b[2] + "." + b[3] + ".");
        InetAddress addr = InetAddress.getByAddress(b);
        this.conn1 = new TCPMasterConnection(addr);
        this.conn1.setPort(502);
        this.conn1.connect();
    }

    public static Satorius getInstance() {
        return instance;
    }

    private int bacaAlamat1(int alamat) throws Exception {
        int hasil = 0;
        ReadInputRegistersRequest req = new ReadInputRegistersRequest(alamat, 1);
        ModbusTCPTransaction trans = new ModbusTCPTransaction(this.conn1);
        trans.setRequest((ModbusRequest)req);
        trans.setCheckingValidity(true);
        trans.setRetries(10);
        trans.execute();
        ReadInputRegistersResponse res = (ReadInputRegistersResponse)trans.getResponse();
        hasil = res.getRegisterValue(0);
        return hasil;
    }

    private void read() {
        try {
            if (this.conn1 == null || !this.conn1.isConnected()) {
				System.out.println("baru");
				this.connect();
            }
        }
        catch (Exception e1) {
            e1.printStackTrace();
			System.out.println("errordisini");
        }
        try {
            int wt = this.bacaAlamat1(17);
            System.out.println(wt);
            this.db.setTimbangan(wt, 1);
			Satorius.this.flagFirst = true;
        }
        catch (Exception e) {
            e.printStackTrace();
			System.out.println("errordisana");
			try {
				this.connect();
			}
			catch (Exception e2) {
			}
        }
    }

    public void start() {
        if (this.th != null) {
            return;
        }
        this.th = new Thread(new Runnable(){

            @Override
            public void run() {
                while (Satorius.this.jalan) {
                    try {
                        Satorius.this.read();
                        Thread.sleep(1000);
                        continue;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.th.start();
    }

    public void stop() {
        this.jalan = false;
    }
	
    public static void main(String[] args) {
	System.out.println("hello javatpoint");
        if (args.length > 0) {
            ip = args[0];
        }
        Satorius sat = new Satorius();
        sat.start();
    }

}

