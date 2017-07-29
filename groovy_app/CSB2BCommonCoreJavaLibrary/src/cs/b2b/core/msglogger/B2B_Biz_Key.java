package cs.b2b.core.msglogger;

import java.sql.Connection;
// import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//import java.text.SimpleDateFormat;
//import java.util.Date;

import cs.b2b.core.common.util.StringUtil;

public class B2B_Biz_Key {

	static String sqlInsert = "insert into b2b_biz_key(transaction_id,biz_key_type_id,biz_key,create_ts,update_ts,updated_by) values (?,?,?,new_time(sysdate,'GMT','PST'),new_time(sysdate,'GMT','PST'),?)";

	public static boolean InsertB2bBizKey(Connection dbconn,
			String transaction_id, String biz_key_type_id, String biz_key,
			String update_by) {
		if (StringUtil.isEmpty(transaction_id)
				|| StringUtil.isEmpty(biz_key_type_id))
			return false;

		if (biz_key != null && biz_key.length() > 100) {
			biz_key = biz_key.substring(0, 100);
		}

		// id, code, value
		PreparedStatement pst = null;
		boolean ret = false;
		try {
			pst = dbconn.prepareCall(sqlInsert);
			pst.setString(1, transaction_id);
			pst.setString(2, biz_key_type_id);
			pst.setString(3, biz_key);
			pst.setString(4, update_by);

			ret = pst.execute();

			ret = true;

		} catch (Exception ex) {
			// For Unique/BizKey empty case,just ignore, not insert
			return false;
		} finally {
			if (pst != null)
				try {
					pst.close();
				} catch (SQLException e) {
				}
		}
		return ret;
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
	 * boolean b = InsertB2bBizKey(con, "TXT20150806204900-528", "po_num", "",
	 * "Jackson");
	 * 
	 * if (con != null) { con.close(); }
	 * 
	 * }
	 */

}
