import com.mchange.v2.c3p0.ComboPooledDataSource;
import sun.awt.OSInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class C3P0Util {
    private static ComboPooledDataSource sqliteDS = null;
    private static ComboPooledDataSource mysqlDS = null;

    public static void init() {
        if (OSInfo.getOSType() == OSInfo.OSType.LINUX)
            sqliteDS = new ComboPooledDataSource("LinuxSQLiteDB");
        else
            sqliteDS = new ComboPooledDataSource("WindowsSQLiteDB");
        mysqlDS = new ComboPooledDataSource("");
    }

    public static DataSource getSQLiteDS() {
        return sqliteDS;
    }

    public static DataSource getMysqlDS() {
        return mysqlDS;
    }

    public static Connection getSQLiteConn() {
        Connection conne = null;
        try {
            conne = sqliteDS.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conne;
    }


    public static Connection getMysqlConn() {
        Connection conne = null;
        try {
            conne = mysqlDS.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conne;
    }

    public static void closeAll(ResultSet rs, Statement st, Connection conne) {
        try {
            if (null != rs) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (null != st) {
                st.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (conne != null) {
                conne.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeDataSource() {
        sqliteDS.close();
        mysqlDS.close();
    }

}
