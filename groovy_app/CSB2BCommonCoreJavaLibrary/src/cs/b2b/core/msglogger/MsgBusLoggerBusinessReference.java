package cs.b2b.core.msglogger;

import java.sql.Connection;
import java.sql.PreparedStatement;

import cs.b2b.core.common.util.StringUtil;

public class MsgBusLoggerBusinessReference {

	static String sqlInsert = "insert into oll_business_references (interchange_id,business_reference_type_code,business_reference_value) values (?, ?, ?) ";

	public boolean InsertBusinessProfiles(Connection dbconn,
			String sInterchangeID, String sBusiness_Reference_Type_Code,
			String sBusiness_Reference_Value) throws Exception {
		if (StringUtil.isEmpty(sInterchangeID)
				|| StringUtil.isEmpty(sBusiness_Reference_Type_Code)
				|| StringUtil.isEmpty(sBusiness_Reference_Value))
			return false;

		// id, code, value
		PreparedStatement pst = null;
		boolean ret = false;
		try {
			pst = dbconn.prepareCall(sqlInsert);
			pst.setString(1, sInterchangeID);
			pst.setString(2, sBusiness_Reference_Type_Code);
			pst.setString(3, sBusiness_Reference_Value);
			ret = pst.execute();
		} finally {
			if (pst != null)
				pst.close();
		}
		return ret;
	}
}
