package cs.b2b.core.common.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

	public Connection getConnection(String dbip, String port, String sid, String user, String psw) throws Exception {
		Connection conn=null;
		String url = "jdbc:oracle:thin:@"+dbip+":"+port+":"+sid;
		String className="oracle.jdbc.driver.OracleDriver";
		Class.forName(className);
		conn=DriverManager.getConnection(url, user, psw);
		return conn;
	}
}
