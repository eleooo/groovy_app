package cs.b2b.core.msglogger;

import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.DriverManager;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;

import cs.b2b.core.common.util.StringUtil;

public class B2B_msg_send_log {

	public static String GetFileNameAndSendDTByMsgReqIDAndPortID(
			Connection dbconn, String message_request_id, String portID) {

		PreparedStatement pre = null;
		ResultSet result = null;

		String str_SendFileName = "";
		String sendDateTime = "";
		String str_Return = "";

		try {

			if (StringUtil.isEmpty(message_request_id)
					|| StringUtil.isEmpty(portID))
				return "";
			else {

				String sql = "select file_name,end_ts from b2b_edi_msg_send_log log where log.msg_req_id=? and log.port_id=?";

				pre = dbconn.prepareStatement(sql);
				pre.setString(1, message_request_id);
				pre.setString(2, portID);

				result = pre.executeQuery();

				if (result != null) {
					result.next();
					str_SendFileName = result.getString("file_name");
					sendDateTime = result.getString("end_ts");

					str_Return = str_SendFileName + "||" + sendDateTime;

				}

				return str_Return;

			}

		}

		catch (Exception ex) {

			return "";
		}

		finally {
			if (pre != null)
				try {
					pre.close();
				} catch (SQLException e) {

				}
		}

	}

	public static String GetFileNameAndSendDTByMsgReqID(Connection dbconn,
			String message_request_id) {

		PreparedStatement pre = null;
		ResultSet result = null;

		String str_SendFileName = "";
		// String sendDateTime = "";
		String str_Return = "";

		try {

			if (StringUtil.isEmpty(message_request_id))
				return "";
			else {

				String sql = "select file_name,end_ts from b2b_edi_msg_send_log log where log.msg_req_id=?";

				pre = dbconn.prepareStatement(sql);
				pre.setString(1, message_request_id);

				result = pre.executeQuery();

				if (result != null) {
					result.next();
					str_SendFileName = result.getString("file_name");

					str_Return = str_SendFileName;

				}

				return str_Return;

			}

		}

		catch (Exception ex) {

			return "";
		}

		finally {
			if (pre != null)
				try {
					pre.close();
				} catch (SQLException e) {

				}
		}

	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * 
	 * String driver = "oracle.jdbc.driver.OracleDriver"; String url =
	 * "jdbc:oracle:thin:@hln2071p:1521:olb2bdev"; String userName =
	 * "b2b_owner"; String password = "b2b_owner";
	 * 
	 * Connection con = null;
	 * 
	 * Class.forName(driver); con = DriverManager.getConnection(url, userName,
	 * password);
	 * 
	 * // TXN2015060105270638-055 String cross_data_type =
	 * "TARGET_210_997_CROSS_DATA";
	 * 
	 * String sFileName = GetFileNameAndSendDTByMsgReqIDAndPortID(con,
	 * "EDI2015081003380597-88", "SEND_14319187434152");
	 * 
	 * 
	 * System.out.println(sFileName);
	 * 
	 * // EDI2015070105565893-36 String
	 * s=GetFileNameAndSendDTByMsgReqID(con,"EDI2015070105565893-36");
	 * System.out.println(s);
	 * 
	 * if (con != null) { con.close(); }
	 * 
	 * }
	 */

}
