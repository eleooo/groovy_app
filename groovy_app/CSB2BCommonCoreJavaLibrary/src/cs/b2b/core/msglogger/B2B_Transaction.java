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

public class B2B_Transaction {

	static String sqlInsert = "insert into b2b_transaction(id,msg_req_id,updated_by,txn_sts_id,create_ts,update_ts) values (?,?,?,?,new_time(sysdate,'GMT','PST'),new_time(sysdate,'GMT','PST'))";

	public static boolean InsertB2bTransaction(Connection dbconn, String id,
			String msg_req_id, String updated_by, String txn_sts_id) {
		if (StringUtil.isEmpty(id) || StringUtil.isEmpty(msg_req_id)
				|| StringUtil.isEmpty(txn_sts_id))
			return false;

		// id, code, value
		PreparedStatement pst = null;
		boolean ret = false;
		try {
			pst = dbconn.prepareCall(sqlInsert);
			pst.setString(1, id);
			pst.setString(2, msg_req_id);
			pst.setString(3, updated_by);
			pst.setString(4, txn_sts_id);

			ret = pst.execute();

			ret = true;

		} catch (Exception ex) {
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

	// Get existed TransactionID from Msg_ReqesutID, for reprocess case
	public static String getTransactionIDByMsgRequestID(Connection dbconn,
			String msg_req_id) {
		String str_TransactonID = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		try {

			if (StringUtil.isEmpty(msg_req_id))
				return str_TransactonID;
			else {

				String sql = "select id transID  from b2b_transaction tran where tran.msg_req_id=?";

				pre = dbconn.prepareStatement(sql);
				pre.setString(1, msg_req_id);

				result = pre.executeQuery();

				if (result != null) {
					result.next();
				}

				str_TransactonID = result.getString("transID");

				return str_TransactonID;

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
	 * // TXN2015060105270638-055
	 * 
	 * SimpleDateFormat df_B2btransaction_id = new SimpleDateFormat(
	 * "yyyyMMddHHmmss-SSS"); String str_B2btransaction_id = "TXT" +
	 * df_B2btransaction_id.format(new Date());
	 * 
	 * System.out.println(str_B2btransaction_id);
	 * 
	 * boolean b = InsertB2bTransaction(con,
	 * "TXT20150807085351-543","EDI2015080504290566-68", "Jackson", "C");
	 * 
	 * // String trans =
	 * getTransactionIDByMsgRequestID(con,"EDI2015080504290566-68");
	 * System.out.println("TransID:" + b);
	 * 
	 * if (con != null) { con.close(); }
	 * 
	 * }
	 */

}
