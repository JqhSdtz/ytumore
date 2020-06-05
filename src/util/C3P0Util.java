package util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import sun.awt.OSInfo;

import java.sql.*;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

public class C3P0Util {
	private static ComboPooledDataSource sqliteDS = null;
	private static ComboPooledDataSource mysqlDS = null;

	public static void init(){
		if(sqliteDS != null || mysqlDS != null)
			return;
		if(OSInfo.getOSType() == OSInfo.OSType.LINUX) {
			sqliteDS = new ComboPooledDataSource("LinuxSQLiteDB");
			mysqlDS = new ComboPooledDataSource("LinuxMySQLDB");
		} else {
			sqliteDS = new ComboPooledDataSource("WindowsSQLiteDB");
			mysqlDS = new ComboPooledDataSource("WindowsMySQLDB");
		}
	}

	public static ComboPooledDataSource getSQLiteDS() {
		return sqliteDS;
	}

	public static ComboPooledDataSource getMysqlDS() {
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
		mysqlDS.setIdleConnectionTestPeriod(0);
		sqliteDS.setIdleConnectionTestPeriod(0);
		sqliteDS.close();
		mysqlDS.close();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {//立刻关闭可能会引起内存泄漏
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("DataSource has been closed!");
	}
	
}
