/*
 * Decompiled with CFR 0_118.
 */
package pkg;

//import com.pertamina.tas.bc.BatchControl;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection conn = null;
    private PreparedStatement psAuth = null;
    private PreparedStatement psUpdate = null;
    private PreparedStatement psStart = null;
    private PreparedStatement psFinish = null;
    private PreparedStatement psTimbangan = null;
    private static DatabaseManager instance = new DatabaseManager();

    private DatabaseManager() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //String connectionUrl = "jdbc:sqlserver://HISTORIANSERVER\\WINCC;database=PertaminaTas;applicationName=Batch Control;autoReconnect=true";
			String connectionUrl = "jdbc:sqlserver://BYON-PC\\SQLEXPRESS;database=PertaminaTas;applicationName=Batch Control;autoReconnect=true";
            //this.conn = DriverManager.getConnection(connectionUrl, "abud", "bl4ckm4nt4@gmail.com");
			this.conn = DriverManager.getConnection(connectionUrl, "rifqi", "kediri123");
            this.psAuth = this.conn.prepareStatement("exec autorisasiPin ?, ?");
            this.psStart = this.conn.prepareStatement("exec startFilling ?");
            this.psFinish = this.conn.prepareStatement("exec stopFilling ?, ?, ?, ?");
            this.psUpdate = this.conn.prepareStatement("update FillingPoint set preset = ?, realValue = ?, status = ? where idFillingPoint = ?");
            this.psTimbangan = this.conn.prepareStatement("update JembatanTimbang set nilai = ? where idJembatanTimbang = ?");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static DatabaseManager getInstance() {
        return instance;
    }
/*
    public void update(BatchControl bc) {
        try {
            this.psUpdate.setInt(1, (int)bc.getPreset());
            this.psUpdate.setInt(2, (int)bc.getBatchRealAmount());
            int stat = 0;
            if (bc.getArmStatus() == 3 || bc.getArmStatus() == 4) {
                stat = 1;
            } else if (bc.getArmStatus() == 22) {
                stat = 2;
            } else if (bc.getArmStatus() == 31) {
                stat = 3;
            } else if (bc.getArmStatus() == 39 || bc.getArmStatus() == 40) {
                stat = 4;
            }
            this.psUpdate.setInt(3, stat);
            this.psUpdate.setInt(4, bc.getSlaveId());
            this.psUpdate.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
	*/

    public int authenticate(int slaveID, int sn) {
        int hasil = 0;
        if (sn > 0) {
            try {
                this.psAuth.setInt(1, slaveID);
                this.psAuth.setInt(2, sn);
                this.psAuth.execute();
                ResultSet rs = null;
                while ((rs = this.psAuth.getResultSet()) == null) {
                    this.psAuth.getMoreResults();
                }
                if (rs.next()) {
                    hasil = rs.getInt(1);
                }
                rs.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return hasil;
    }

    public void finishLoading(int slaveID, float qty, float density, float temp) {
        try {
            this.psFinish.setInt(1, slaveID);
            this.psFinish.setInt(2, (int)qty);
            this.psFinish.setFloat(3, temp);
            this.psFinish.setFloat(4, density);
            this.psFinish.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startLoading(int slaveID) {
        try {
            this.psStart.setInt(1, slaveID);
            this.psStart.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTimbangan(int wt, int pk) {
        try {
            this.psTimbangan.setInt(1, wt);
            this.psTimbangan.setInt(2, pk);
            this.psTimbangan.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

