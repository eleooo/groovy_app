package cs.b2b.core.msglogger;

import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;

import cs.b2b.core.common.util.StringUtil;

public class B2b_edi_cross_process_data {

	public static boolean InsertB2b_edi_cross_process_data(Connection dbconn,
			String data_type_id, String key, String value, String msg_req_id,
			String update_by) {
		if (StringUtil.isEmpty(msg_req_id) || StringUtil.isEmpty(data_type_id)) {
			return false;
		}

		String sqlInsert = "insert into b2b_edi_cross_process_data(data_type_id,key,value,msg_req_id,create_ts,update_ts,update_by) values (?,?,?,?,new_time(sysdate,'GMT','PST'),new_time(sysdate,'GMT','PST'),?)";

		// id, code, value
		PreparedStatement pst = null;
		boolean ret = false;
		try {
			pst = dbconn.prepareCall(sqlInsert);
			pst.setString(1, data_type_id);
			pst.setString(2, key);
			pst.setString(3, value);
			pst.setString(4, msg_req_id);
			pst.setString(5, update_by);

			ret = pst.execute();

			ret = true;

		} catch (Exception ex) {
			// ex.printStackTrace();
			return false;
		}

		finally {
			if (pst != null)
				try {
					pst.close();
				} catch (SQLException e) {

				}
		}
		return ret;
	}

	public static int UpdateB2b_edi_cross_process_dataByMsgIDAndKeyValue(
			Connection dbconn, String msg_req_id, String data_type_id,
			String key, String value) {

		PreparedStatement pre = null;
		int result = 0;

		try {

			if (StringUtil.isEmpty(msg_req_id)
					|| StringUtil.isEmpty(data_type_id)
					|| StringUtil.isEmpty(key))
				return 0;
			else {

				String sql = "update b2b_edi_cross_process_data da set value=? where da.data_type_id=? and da.msg_req_id=? and da.key=?";

				pre = dbconn.prepareStatement(sql);
				pre.setString(1, value);
				pre.setString(2, data_type_id);
				pre.setString(3, msg_req_id);
				pre.setString(4, key);

				result = pre.executeUpdate();

				return result;

			}

		}

		catch (Exception ex) {

			return 0;
		}

		finally {
			if (pre != null)
				try {
					pre.close();
				} catch (SQLException e) {

				}
		}

	}

	public static String GetB2b_edi_cross_process_dataByMsgIDAndKey(
			Connection dbconn, String msg_req_id, String data_type_id,
			String key) {

		PreparedStatement pre = null;
		ResultSet result = null;

		String str_value = "";

		try {

			if (StringUtil.isEmpty(msg_req_id)
					|| StringUtil.isEmpty(data_type_id)
					|| StringUtil.isEmpty(key))
				return "";
			else {

				String sql = "select value from b2b_edi_cross_process_data da where da.msg_req_id=? and da.data_type_id=? and da.key=?";

				pre = dbconn.prepareStatement(sql);
				pre.setString(1, msg_req_id);
				pre.setString(2, data_type_id);
				pre.setString(3, key);

				result = pre.executeQuery();

				if (result != null) {
					result.next();
					str_value = result.getString("value");
				}

				return str_value;

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

	public static String GetMessageRequestIDByKeyValue(Connection dbconn,
			String data_type_id, String key, String value) {

		PreparedStatement pre = null;
		ResultSet result = null;

		String str_MessageRequestID = "";

		try {

			if (StringUtil.isEmpty(data_type_id) || StringUtil.isEmpty(key))
				return "";
			else {

				String sql = " select data_type_id,key,value,msg_req_id,create_ts,update_ts,update_by from b2b_edi_cross_process_data da where da.data_type_id=? and da.key=? and da.value=?";

				pre = dbconn.prepareStatement(sql);
				pre.setString(1, data_type_id);
				pre.setString(2, key);
				pre.setString(3, value);

				result = pre.executeQuery();

				if (result != null) {
					result.next();
					str_MessageRequestID = result.getString("msg_req_id");
				}

				return str_MessageRequestID;

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
	 * 
	 * String cross_data_type ="TARGET_210_997_CROSS_DATA";
	 * 
	 * boolean bInsert = InsertB2b_edi_cross_process_data(con, cross_data_type,
	 * "invoice_num1", "1234", "EDI2010051710282245-67", "Jackson");
	 * 
	 * int i_Update=0; if(!bInsert) {
	 * i_Update=UpdateB2b_edi_cross_process_dataByMsgIDAndKeyValue
	 * (con,"EDI2010051710282245-67",cross_data_type,"invoice_num1","abcde"); }
	 * 
	 * System.out.println(bInsert); System.out.println(i_Update);
	 * 
	 * 
	 * String
	 * msgID=GetMessageRequestIDByKeyValue(con,cross_data_type,"invoice_num1"
	 * ,"abcde"); System.out.println(msgID);
	 * 
	 * String
	 * sValue=GetB2b_edi_cross_process_dataByMsgIDAndKey(con,msgID,cross_data_type
	 * ,"invoice_num1"); System.out.println(sValue);
	 * 
	 * 
	 * if (con != null) { con.close(); }
	 * 
	 * }
	 */

}
