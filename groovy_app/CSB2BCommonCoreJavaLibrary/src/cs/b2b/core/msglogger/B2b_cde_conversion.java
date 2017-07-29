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

public class B2b_cde_conversion {

	public static boolean InsertB2b_cde_conversion(Connection dbconn,
			String convertTypeID, String dirID, String msgType, String tpID,
			String int_Cde, String ext_Cde) {
		if (StringUtil.isEmpty(convertTypeID) || StringUtil.isEmpty(dirID)
				|| StringUtil.isEmpty(msgType) || StringUtil.isEmpty(tpID))
			return false;

		// id, code, value
		PreparedStatement pst = null;
		boolean ret = false;

		String sqlInsert = "insert into b2b_cde_conversion (convert_type_id,dir_id,msg_type_id,msg_fmt_id,tp_id,scac_cde,int_cde,ext_cde,remarks,create_ts,update_ts,updated_by,ref_table,active_flag) values (?,?,?,'',?,' ',?,?,' ',new_time(sysdate,'GMT','PST'),new_time(sysdate,'GMT','PST'),'system',' ','T')";

		try {
			pst = dbconn.prepareCall(sqlInsert);
			pst.setString(1, convertTypeID);
			pst.setString(2, dirID);
			pst.setString(3, msgType);
			pst.setString(4, tpID);
			pst.setString(5, int_Cde);
			pst.setString(6, ext_Cde);

			ret = pst.execute();

			ret = true;

		} catch (Exception ex) {
			return false;
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
				}
			}
		}
		return ret;
	}

	public static String getB2b_cde_conversionByInt_CDE(Connection dbconn,
			String convertTypeID, String dirID, String msgType, String tpID,
			String int_Cde) {

		PreparedStatement pre = null;
		ResultSet result = null;

		String str_ExtCde = "";
		try {

			if (StringUtil.isEmpty(tpID) || StringUtil.isEmpty(convertTypeID)
					|| StringUtil.isEmpty(dirID) || StringUtil.isEmpty(msgType))
				return "";
			else {

				String sql = "select ext_cde from b2b_cde_conversion con where con.tp_id=? and con.msg_type_id=? and con.convert_type_id=? and con.dir_id=? and con.int_cde=?";

				pre = dbconn.prepareStatement(sql);
				pre.setString(1, tpID);
				pre.setString(2, msgType);
				pre.setString(3, convertTypeID);
				pre.setString(4, dirID);
				pre.setString(5, int_Cde);

				result = pre.executeQuery();

				if (result != null) {
					result.next();
				}
				str_ExtCde = result.getString("ext_cde");
				return str_ExtCde;
			}

		} catch (Exception ex) {
			return "";
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
				}
			}
			if (pre != null) {
				try {
					pre.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	public static String getB2b_cde_conversionByExt_CDE(Connection dbconn,
			String convertTypeID, String dirID, String msgType, String tpID,
			String ext_Cde) {

		PreparedStatement pre = null;
		ResultSet result = null;
		String str_IntCde = "";

		try {

			if (StringUtil.isEmpty(tpID) || StringUtil.isEmpty(convertTypeID)
					|| StringUtil.isEmpty(dirID) || StringUtil.isEmpty(msgType))
				return "";
			else {

				String sql = "select int_cde from b2b_cde_conversion con where con.tp_id=? and con.msg_type_id=? and con.convert_type_id=? and con.dir_id=? and con.ext_cde=?";

				pre = dbconn.prepareStatement(sql);
				pre.setString(1, tpID);
				pre.setString(2, msgType);
				pre.setString(3, convertTypeID);
				pre.setString(4, dirID);
				pre.setString(5, ext_Cde);

				result = pre.executeQuery();

				if (result != null) {
					result.next();
				}
				str_IntCde = result.getString("int_cde");
				return str_IntCde;
			}
		} catch (Exception ex) {
			return "";
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
				}
			}
			if (pre != null) {
				try {
					pre.close();
				} catch (SQLException e) {
				}
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
	 * 
	 * 
	 * boolean
	 * b=InsertB2b_cde_conversion(con,"OLLTestIndicator-EDITestIndicator"
	 * ,"O","POSTADV","AGE_GROUP","Jackson","JacksonExt"); String
	 * c=getB2b_cde_conversionByInt_CDE
	 * (con,"OLLTestIndicator-EDITestIndicator","O"
	 * ,"POSTADV","AGE_GROUP","Jackson"); String
	 * d=getB2b_cde_conversionByExt_CDE
	 * (con,"OLLTestIndicator-EDITestIndicator","O"
	 * ,"POSTADV","AGE_GROUP","JacksonExt");
	 * 
	 * System.out.println("Insert Result:" + b);
	 * System.out.println("Query Result:" + c);
	 * System.out.println("Query Result:" + d);
	 * 
	 * if (con != null) { con.close(); }
	 * 
	 * }
	 */

}
