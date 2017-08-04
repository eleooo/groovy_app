package cs.b2b.core.mapping.util

import groovy.xml.XmlUtil

import java.sql.CallableStatement
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import java.text.SimpleDateFormat
import java.util.zip.ZipInputStream

class MappingUtil {
	
	String xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	SimpleDateFormat sdfXmlFmt = new SimpleDateFormat(xmlDateTimeFormat);
	
	public int getDBRowLimit() {
		int DB_MAX_RETURN_ROW = 10000;
		return DB_MAX_RETURN_ROW;
	}
	
	public int getDBTimeOutInSeconds() {
		int DB_TIMEOUT_IN_SECCOND = 10;
		return DB_TIMEOUT_IN_SECCOND;
	}
	
	public boolean isXmlDateTimeLaterThanNow(Object inputDt) throws Exception {
		if (inputDt==null) {
			return false
		}
		String inputStr = ""
		if (inputDt instanceof String) {
			inputStr = inputDt.toString()
		} else {
			throw new Exception("isXmlDateTimeLaterThanNow - pass invalid parameter: "+inputDt)
		}
		if (inputStr.trim().length()==0) {
			return false
		}
		Date date = sdfXmlFmt.parse(inputStr)
		Calendar calNow = Calendar.getInstance()
		return (date.getTime() > calNow.getTimeInMillis())
	}
	
	String trim(Object obj) {
		if (obj==null) {
			return ""
		}
		if (obj instanceof String) {
			return obj.trim()
		} else {
			return obj.toString().trim()
		}
	}
	
	public boolean isDecimal(String s) {
		if (s==null || s.trim().length()==0)
			return false;
		
		String rule = "(-|\\+)?([0-9]|\\.)+([0-9]+)?";
		
		if (s.matches(rule)) {
			if (s.indexOf(".")>=0 && s.indexOf(".")!=s.lastIndexOf("."))
				return false;
			else
				return true;
		} else {
			return false;
		}
	}
	
	public boolean isNumberic(String s) {
		if (s==null || s.trim().length()==0)
			return false;
		
		String rule = "(-|\\+)?([0-9]+)";
		
		if (s.matches(rule)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isInteger(String s, int radix) {
		if(s==null || s.trim().length()==0 || ((s.startsWith("-") || s.startsWith("+")) && s.length()>11) || ( !s.startsWith("-") && !s.startsWith("+") && s.length()>10))
			return false;
		
		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && (s.charAt(i) == '-' || s.charAt(i) == '+')) {
				if(s.length() == 1)
					return false;
				else
					continue;
			}
			if(Character.digit(s.charAt(i), radix) < 0)
				return false;
		}
		return true;
	}
	
	public String formatString(long val, String outputFormat) {
		String ret = val;
		if (outputFormat != null && outputFormat.length() > 0) {
			ret = String.format(outputFormat, val);
		}
		return ret;
	}
	
	public boolean notEmpty(Object data) {
		if (data == null)
			return false;
	
		if (data instanceof String) {
			return data.trim().length() > 0
		} else {
			return (data != null && data.toString().trim().length()>0)
		}
	}
	
	public boolean isNotEmpty(Object data) {
		return notEmpty(data)
	}
	
	public boolean isEmpty(Object data) {
		return (! notEmpty(data))
	}
	
	public String removeBOM(String str) {
		byte[] bs = str.getBytes();
		String instr = str;
		if (-17 == bs[0] && -69 == bs[1] && -65 == bs[2]) {
			instr = new String(bs, 3, bs.length - 3);
		} else if (-17 == bs[0] && -65 == bs[1] && -67 == bs[2]) {
			instr = new String(bs, 3, bs.length - 3);
		} else if (-17 == bs[0] && -69 == bs[1] && 63 == bs[2]) {
			instr = new String(bs, 3, bs.length - 3);
		} else if (63 == bs[0] && 63 == bs[1] && 63 == bs[2]) {
			instr = new String(bs, 3, bs.length - 3);
		}
		return instr;
	}
	
	public String getConversionWithDefault(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, String defaultValue, Connection conn) throws Exception {
		String ret = getConversion(TP_ID, MSG_TYPE_ID, DIR_ID, convertTypeId, fromValue, conn);
		if (ret == null || ret.length() == 0) {
			ret = defaultValue;
		}
		return ret;
	}
	
	public String getConversionByExtCdeWithDefault(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, String defaultValue, Connection conn) throws Exception {
		String ret = getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, convertTypeId, fromValue, conn);
		if (ret == null || ret.length() == 0) {
			ret = defaultValue;
		}
		return ret;
	}
	
	//////////////////////
	/**
	 * query b2b_cde_conversion by auto parameters, if parameter is not in query, then set to null
	 * e.g. when TP_ID is null, then will not use in query parameter
	 *
	 * when fromValue.startsWith("UPPER-CASE:"), will use Uppder(int_cde) mode to ignore upper-case
	 * @param TP_ID
	 * @param MSG_TYPE_ID
	 * @param DIR_ID
	 * @param scacCode  -- support space query, but space replace to macro: " " => "<space>".
	 * @param MSG_FMT_ID
	 * @param convertTypeId
	 * @param fromValue
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getCdeConversionFromIntCde(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String scacCode, String MSG_FMT_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
		boolean isUseIntCde = true
		return getCdeConversionImpl(TP_ID, MSG_TYPE_ID, DIR_ID, scacCode, MSG_FMT_ID, convertTypeId, fromValue, isUseIntCde, conn)
	}
	
	public Map<String, String> getCdeConversionByExtCde(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String scacCode, String MSG_FMT_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
		boolean isUseIntCde = false
		return getCdeConversionImpl(TP_ID, MSG_TYPE_ID, DIR_ID, scacCode, MSG_FMT_ID, convertTypeId, fromValue, isUseIntCde, conn)
	}
	
	private String queryNullScac = "<QUERY-NULL-SCAC>"
	private String queryNullOrASpaceScac = "<QUERY-NULL-OR-SPACE-SCAC>"
	
	private Map<String, String> getCdeConversionImpl(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String scacCode, String MSG_FMT_ID, String convertTypeId, String fromValue, boolean isUseIntCde, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB connection is not available for conversion query.")
		}
	
		Map ret = new HashMap<String, String>()
		
		boolean intCdeUpper = false;
		if (fromValue!=null && fromValue.startsWith("UPPER-CASE:")) {
			intCdeUpper = true;
			fromValue = fromValue.substring("UPPER-CASE:".length())
			fromValue = fromValue.toUpperCase()
		}
		if (isEmpty(fromValue)) {
			return ret
		}
		
		PreparedStatement pre = null
		ResultSet result = null
		//tp_id=? and dir_id=? and msg_fmt_id=? and convert_type_id=? and scac_cde=? and int_cde=?
		String sql = "select INT_CDE, EXT_CDE, REMARKS from b2b_cde_conversion where "
		String condition = ""
		if (isNotEmpty(TP_ID)) {
			condition += "tp_id=? and "
		}
		if (isNotEmpty(MSG_TYPE_ID)) {
			condition += "msg_type_id=? and "
		}
		if (isNotEmpty(DIR_ID)) {
			condition += "dir_id=? and "
		}
		if (isNotEmpty(scacCode)) {
			if (scacCode.equalsIgnoreCase(queryNullScac)) {
				condition += "scac_cde is null and "
			} else if (scacCode.equalsIgnoreCase(queryNullOrASpaceScac)) {
				condition += "(scac_cde is null or scac_cde=' ') and "
			} else {
				condition += "scac_cde=? and "
			}
		}
		if (isNotEmpty(MSG_FMT_ID)) {
			condition += "msg_fmt_id=? and "
		}
		if (isNotEmpty(convertTypeId)) {
			condition += "convert_type_id=? and "
		}
		if (condition.length()==0) {
			return ret
		} else {
			sql += condition
			if (isUseIntCde) {
				if (intCdeUpper) {
					sql += "upper(int_cde)=?"
				} else {
					sql += "int_cde=?"
				}
			} else {
				//use ext_cde for query
				if (intCdeUpper) {
					sql += "upper(ext_cde)=?"
				} else {
					sql += "ext_cde=?"
				}
			}
		}
		
		try {
			pre = conn.prepareStatement(sql)
			//only get the 1st record for conversion
			pre.setMaxRows(1)
			pre.setQueryTimeout(getDBTimeOutInSeconds())
			int colCount = 1
			if (isNotEmpty(TP_ID)) {
				pre.setString(colCount++, TP_ID)
			}
			if (isNotEmpty(MSG_TYPE_ID)) {
				pre.setString(colCount++, MSG_TYPE_ID)
			}
			if (isNotEmpty(DIR_ID)) {
				pre.setString(colCount++, DIR_ID)
			}
			if (isNotEmpty(scacCode)) {
				if (scacCode.equalsIgnoreCase(queryNullScac)) {
					//nothing to set
				} else if (scacCode.equalsIgnoreCase(queryNullOrASpaceScac)) {
					//nothing to set
				} else {
					if (scacCode.indexOf("<space>")>=0) {
						scacCode = scacCode.replace("<space>", " ")
					}
					pre.setString(colCount++, scacCode)
				}
			}
			if (isNotEmpty(MSG_FMT_ID)) {
				pre.setString(colCount++, MSG_FMT_ID)
			}
			if (isNotEmpty(convertTypeId)) {
				pre.setString(colCount++, convertTypeId)
			}
			pre.setString(colCount, fromValue)
			
			result = pre.executeQuery();
			if (result.next()) {
				ret.put("INT_CDE", result.getString("INT_CDE"))
				ret.put("EXT_CDE", result.getString("EXT_CDE"))
				ret.put("REMARKS", result.getString("REMARKS"))
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret
	}
	
	
	public String getConversion(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
	
		if (TP_ID==null || MSG_TYPE_ID==null || DIR_ID==null || convertTypeId==null || fromValue==null) {
			return ""
		}
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where tp_id=? and msg_type_id=? and dir_id=? and convert_type_id=? and int_cde=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, MSG_TYPE_ID);
			pre.setString(3, DIR_ID);
			pre.setString(4, convertTypeId);
			pre.setString(5, fromValue);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String getEDICrossProcessData(String dataTypeId, String key, String msgReqId, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
	
		if (isEmpty(dataTypeId) || isEmpty(key)) {
			return ""
		}
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select value from B2B_EDI_CROSS_PROCESS_DATA WHERE DATA_TYPE_ID = ? and KEY = ?";
	
		try {
			pre = conn.prepareStatement(sql);
			// only need get the 1st one
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, dataTypeId);
			pre.setString(2, key);
			result = pre.executeQuery();
			
			if (result.next()) {
				byte[] buffer = result.getBytes(1);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
				ZipInputStream zis = new ZipInputStream(bais);
				zis.getNextEntry();
				byte[] b = new byte[1024];
				for (int c = zis.read(b, 0, 1024); c != -1; c = zis.read(b, 0, 1024)) {
					baos.write(b, 0, c);
				}
				ret = new String(baos.toByteArray(), "UTF-8");
				baos.close()
				zis.close()
				bais.close()
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String getConversionByExtCde(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
	
		if (TP_ID==null || MSG_TYPE_ID==null || DIR_ID==null || convertTypeId==null || fromValue==null) {
			return ""
		}
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select int_cde from b2b_cde_conversion where tp_id=? and msg_type_id=? and dir_id=? and convert_type_id=? and ext_cde=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, MSG_TYPE_ID);
			pre.setString(3, DIR_ID);
			pre.setString(4, convertTypeId);
			pre.setString(5, fromValue);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String getConversionWithScac(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, String SCAC, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
	
		if (TP_ID==null || MSG_TYPE_ID==null || DIR_ID==null || convertTypeId==null || fromValue==null || SCAC==null) {
			return ""
		}
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where tp_id=? and msg_type_id=? and dir_id=? and convert_type_id=? and int_cde=? and scac_cde=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, MSG_TYPE_ID);
			pre.setString(3, DIR_ID);
			pre.setString(4, convertTypeId);
			pre.setString(5, fromValue);
			pre.setString(6, SCAC);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String getConversionWithScacByExtCde(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, String SCAC, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
	
		if (TP_ID==null || MSG_TYPE_ID==null || DIR_ID==null || convertTypeId==null || fromValue==null || SCAC==null) {
			return ""
		}
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select int_cde from b2b_cde_conversion where tp_id=? and msg_type_id=? and dir_id=? and convert_type_id=? and ext_cde=? and scac_cde=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, MSG_TYPE_ID);
			pre.setString(3, DIR_ID);
			pre.setString(4, convertTypeId);
			pre.setString(5, fromValue);
			pre.setString(6, SCAC);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String getConversionCommonWithScac(String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String SCAC, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
	
		if (MSG_TYPE_ID==null || DIR_ID==null || convertTypeId==null || SCAC==null) {
			return ''
		}
		
		String ret = '';
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select int_cde, ext_cde from b2b_cde_conversion where tp_id is null and msg_type_id=? and dir_id=? and convert_type_id=? and scac_cde=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, MSG_TYPE_ID);
			pre.setString(2, DIR_ID);
			pre.setString(3, convertTypeId);
			pre.setString(4, SCAC);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1) + ';' + result.getString(2);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	
	public String getConversionByConvertType(String convertTypeId, String fromValue, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
		
		if (convertTypeId==null || fromValue==null) {
			return null
		}
		
		String ret = null;
		PreparedStatement pre = null;
		ResultSet result = null;
	
		String sql = "select ext_cde from b2b_cde_conversion where convert_type_id=? and int_cde=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, convertTypeId);
			pre.setString(2, fromValue);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String getConversionWithoutTP(String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		if (MSG_TYPE_ID==null || DIR_ID==null || convertTypeId==null || fromValue==null) {
			return ""
		}
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where tp_id is null and msg_type_id=? and dir_id=? and convert_type_id=? and int_cde=?";
		
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, MSG_TYPE_ID);
			pre.setString(2, DIR_ID);
			pre.setString(3, convertTypeId);
			pre.setString(4, fromValue);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result!=null)
				result.close();
			if (pre!=null)
				pre.close();
		}
		return ret;
	}
	
	public String getConversionByTpIdDirFmtScac(String TP_ID, String DIR_ID, String MSG_FMT_ID, String convertTypeId, String scacCode, String fromValue, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB connection is not available for conversion query.");
		}
	
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where tp_id=? and dir_id=? and msg_fmt_id=? and convert_type_id=? and scac_cde=? and int_cde=?";
		
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for conversion
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, DIR_ID);
			pre.setString(3, MSG_FMT_ID);
			pre.setString(4, convertTypeId);
			pre.setString(5, scacCode);
			pre.setString(6, fromValue);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String getConversionByTpIdMsgTypeDirFmtScac(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String MSG_FMT_ID, String convertTypeId, String scacCode, String fromValue, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB connection is not available for conversion query.");
		}
	
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where tp_id=? and msg_type_id=? and dir_id=? and msg_fmt_id=? and convert_type_id=? and scac_cde=? and int_cde=?";
		
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for conversion
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, MSG_TYPE_ID);
			pre.setString(3, DIR_ID);
			pre.setString(4, MSG_FMT_ID);
			pre.setString(5, convertTypeId);
			pre.setString(6, scacCode);
			pre.setString(7, fromValue);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String getCarrierTpId(String SenderTpId, String MsgType, String Scac, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
		
		if (SenderTpId==null || MsgType==null || Scac==null) {
			return null;
		}
		
		String ret = null;
		PreparedStatement pre = null;
		ResultSet result = null;
	
		String sql = "select channel_tp_id from tp_integration_asso where sender_tp_id=? and message_type=? and receiver_scac_code=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, SenderTpId);
			pre.setString(2, MsgType);
			pre.setString(3, Scac);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	/**
	 * for B2B1.5 FA (IRA) usage, query L1P mon type
	 */
	public String getMonTypeFromE2EMon(String TP_ID, String msgReqId, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}

		if (msgReqId==null) {
			return null;
		}

		String ret = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		String sql = "select mon_type from b2b_e2e_pmt_cfg where sender_id=? and start_msg_type_id in (select msg_type_id from b2b_msg_req_detail where msg_req_id=?) " +
				"and substr(mon_type, 1, 2) = 'L1'";

		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st line
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, msgReqId);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String convertDateTime(def inputDateObj, String inputFormat, String outputFormat) throws Exception {
		String output = "";
		String inputDate = inputDateObj?.toString();
		if (inputDate != null && inputDate.trim().length() > 0 && inputFormat != null && inputFormat.trim().length() > 0
				&& outputFormat != null && outputFormat.trim().length() > 0) {
	
			SimpleDateFormat sfmt = new SimpleDateFormat(inputFormat);
			java.util.Date date = sfmt.parse(inputDate);
	
			SimpleDateFormat soutfmt = new SimpleDateFormat(outputFormat);
			output = soutfmt.format(date);
		}
		return output;
	}
	
	public String convertXmlDateTime(def inputDateObj, String outputFormat) throws Exception {
		String output = "";
		String inputDate = inputDateObj?.toString();
		if (inputDate && outputFormat) {
			java.util.Date date = sdfXmlFmt.parse(inputDate);
			SimpleDateFormat soutfmt = new SimpleDateFormat(outputFormat);
			output = soutfmt.format(date);
		}
		return output;
	}
	
	public long GetSequenceNextValWithDefault(String seqKey, long defaultVal, Connection conn) {
		//implement your sequence logic here
		//TODO
		return -1;
	}
	
	public String getRuntimeParameter(String name, String[] params) {
		String pn = name + "=";
		for (int i = 0; params != null && i < params.length; i++) {
			String tmp = params[i];
			if (tmp == null || tmp.length() == 0)
				continue;
			if (tmp.startsWith(pn)) {
				return tmp.substring(pn.length());
			}
		}
		return "";
	}
	
	public boolean SetMonEDIControlNo(String SenderId, String PartnerId, String MsgTypeId, String MsgFmtId, String EdiIntChgNo, String EdiGrpCtlNo, String EdiTxnCtlNo, String MessageId, String MsgReqId, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		
		boolean ret = false;
	
		PreparedStatement CheckMonPre = null;
		PreparedStatement CheckExistPre = null;
		PreparedStatement UpdateMon = null;
		PreparedStatement InsertMon = null;
	
		ResultSet CheckMonResult = null;
		ResultSet CheckExistResult = null;
		ResultSet UpdateMonResult = null;
		ResultSet InsertMonResult = null;
	
		String CheckMonSql = "select 1 from b2b_edi_filename where PARTNER_ID=? and MSG_TYPE=? and ACK_MON_FLAG IS NOT NULL";
		String CheckExistSql = "select CS2_MSG_ID from b2b_edi_ackmon_log where SENDER_ID=? and CS2_MSG_ID=?";
		String UpdateMonSql = "update b2b_edi_ackmon_log set IN_MSG_REQ_ID=?, IN_MSG_TYPE_ID=?, IN_MSG_FMT_ID=?, IN_INTCHG_CTLNO=?, IN_GROUP_CTLNO=?, IN_TXN_CTLNO=?, IN_MSG_CREATE_TS=SYS_EXTRACT_UTC(current_timestamp) where CS2_MSG_ID=?";
		String InsertMonSql = "insert into b2b_edi_ackmon_log (SENDER_ID, RECEIVER_ID, CS2_MSG_ID, IN_MSG_REQ_ID, IN_MSG_TYPE_ID, IN_MSG_FMT_ID, IN_INTCHG_CTLNO, IN_GROUP_CTLNO, IN_TXN_CTLNO, IN_MSG_CREATE_TS) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, SYS_EXTRACT_UTC(current_timestamp))";
	
		try {
	
			CheckMonPre = conn.prepareStatement(CheckMonSql);
			CheckMonPre.setMaxRows(getDBRowLimit());
			CheckMonPre.setQueryTimeout(getDBTimeOutInSeconds());
			CheckMonPre.setString(1, SenderId);
			CheckMonPre.setString(2, MsgTypeId);
			CheckMonResult = CheckMonPre.executeQuery();
			if (CheckMonResult != null) {
				CheckExistPre = conn.prepareStatement(CheckExistSql);
				CheckExistPre.setMaxRows(getDBRowLimit());
				CheckExistPre.setQueryTimeout(getDBTimeOutInSeconds());
				CheckExistPre.setString(1, SenderId);
				CheckExistPre.setString(2, MessageId);
				CheckExistResult = CheckExistPre.executeQuery();
	
				if (CheckExistResult.row > 0) {
					UpdateMon = conn.prepareStatement(UpdateMonSql);
					UpdateMon.setMaxRows(getDBRowLimit());
					UpdateMon.setQueryTimeout(getDBTimeOutInSeconds());
					UpdateMon.setString(1, MsgReqId);
					UpdateMon.setString(2, MsgTypeId);
					UpdateMon.setString(3, MsgFmtId);
					UpdateMon.setString(4, EdiIntChgNo);
					UpdateMon.setString(5, EdiGrpCtlNo);
					UpdateMon.setString(6, EdiTxnCtlNo);
					UpdateMon.setString(7, MessageId);
					if (UpdateMon.executeUpdate() > 0) {
						ret = true;
					} else {
	
						ret = false;
					}
				} else {
					InsertMon = conn.prepareStatement(InsertMonSql);
					InsertMon.setMaxRows(getDBRowLimit());
					InsertMon.setQueryTimeout(getDBTimeOutInSeconds());
					InsertMon.setString(1, SenderId);
					InsertMon.setString(2, PartnerId);
					InsertMon.setString(3, MessageId);
					InsertMon.setString(4, MsgReqId);
					InsertMon.setString(5, MsgTypeId);
					InsertMon.setString(6, MsgFmtId);
					InsertMon.setString(7, EdiIntChgNo);
					InsertMon.setString(8, EdiGrpCtlNo);
					InsertMon.setString(9, EdiTxnCtlNo);
					if (InsertMon.executeUpdate() > 0) {
						ret = true;
					} else {
						ret = false;
					}
				}
			}
		} finally {
			if (CheckMonResult != null)
				CheckMonResult.close();
			if (CheckExistResult != null)
				CheckExistResult.close();
			if (UpdateMonResult != null)
				UpdateMonResult.close();
			if (InsertMonResult != null)
				InsertMonResult.close();
		}
		return ret;
	}
	
	public boolean InsertSCAC(String MsgReqId, String DirId, String MsgReqDetailAssoType, String SCAC, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		
		boolean ret = false;
	
		PreparedStatement CheckExistSCACPre = null;
		PreparedStatement InsertSCACPre = null;
	
		ResultSet CheckExistSCACResult = null;
		ResultSet InsertSCACResult = null;
	
		String CheckExistSCACSql = "select 1 from b2b_msg_req_detail_asso where MSG_REQ_ID=? and DIR_ID=? and MSG_REQ_DETAIL_ASSO_TYPE_ID=? and MSG_REQ_DETAIL_ASSO=?";
		String InsertSCACSql = "insert into b2b_msg_req_detail_asso (DIR_ID,MSG_REQ_ID,MSG_REQ_DETAIL_ASSO_TYPE_ID,MSG_REQ_DETAIL_ASSO,CREATE_TS,UPDATE_TS,UPDATED_BY) VALUES (?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'HKEDI')";
	
		try {
	
			CheckExistSCACPre = conn.prepareStatement(CheckExistSCACSql);
			CheckExistSCACPre.setMaxRows(getDBRowLimit());
			CheckExistSCACPre.setQueryTimeout(getDBTimeOutInSeconds());
			CheckExistSCACPre.setString(1, MsgReqId);
			CheckExistSCACPre.setString(2, DirId);
			CheckExistSCACPre.setString(3, MsgReqDetailAssoType)
			CheckExistSCACPre.setString(4, SCAC)
			CheckExistSCACResult = CheckExistSCACPre.executeQuery();
	
			if (CheckExistSCACResult.row > 0) {
				ret = true;
			} else {
				InsertSCACPre = conn.prepareStatement(InsertSCACSql);
				InsertSCACPre.setMaxRows(getDBRowLimit());
				InsertSCACPre.setQueryTimeout(getDBTimeOutInSeconds());
				InsertSCACPre.setString(1, DirId);
				InsertSCACPre.setString(2, MsgReqId);
				InsertSCACPre.setString(3, MsgReqDetailAssoType);
				InsertSCACPre.setString(4, SCAC);
				if (InsertSCACPre.executeUpdate() > 0) {
					ret = true;
				} else {
					ret = false;
				}
			}
		} finally {
			if (CheckExistSCACResult != null)
				CheckExistSCACResult.close();
			if (InsertSCACResult != null)
				InsertSCACResult.close();
		}
		return ret;
	}
	
	public String getEDICdeRef(String TP_ID, String convertTypeId, String DIR_ID, String SrcFmtId, String SegId, String SegNum, String fromValue, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		if (TP_ID==null || convertTypeId==null || DIR_ID==null || SrcFmtId==null || SegId==null || SegNum==null || fromValue==null) {
			return ""
		}
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select int_cde from b2b_edi_cde_ref where tp_id=? and convert_type_id=? and dir_id=? and src_fmt_id=? and seg_id=? and seg_num=? and ext_cde=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, convertTypeId);
			pre.setString(3, DIR_ID);
			pre.setString(4, SrcFmtId);
			pre.setString(5, SegId);
			pre.setString(6, SegNum);
			pre.setString(7, fromValue);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public String substring(String oriStr,int beginIndex,int len){
		String str = "";
		if(oriStr != null){
			int strlen = oriStr.length();
			beginIndex = beginIndex -1;
			if(strlen <= beginIndex){
			}else if(strlen <= beginIndex+len){
				 str = oriStr.substring(beginIndex);
			}else{
				 str = oriStr.substring(beginIndex, beginIndex+len);
			}
		}
		return str;
	}
	
	public String trimRightSpace(String oriStr) {
		String str = oriStr
		if (str != null) {
			int maxLimit = str.length()
			for(int i=0; i<maxLimit; i++) {
				if (str.endsWith(' ')) {
					str = str.substring(0, str.length()-1)
				} else {
					break
				}
			}
		}
		return str;
	}
	
	/**
	 *
	 * @param vSenderID
	 * @param vPartnerId
	 * @param vMessageType
	 * @param vEDIOutMsgFormat
	 * @param conn
	 * @return long array, the 1st return is ediControlNumber, the 2nd is ediGroupControlNumber
	 *
	 */
	public long[] getEDIControlNumber(String vSenderID, String vPartnerId, String vMessageType, String vEDIOutMsgFormat, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		
		CallableStatement cStmt = null;
		long vEdiCtlNo = -1;
		long vEdiGroupCtlNo = -1;
		try {
			cStmt = conn.prepareCall("{call B2B_PLSQL.B2BEDI_CTLNO_PKG.GET_EDI_CTLNO (?, ?, ?, ?, ?, ?)}");
			cStmt.setQueryTimeout(getDBTimeOutInSeconds());
			cStmt.setString(1, vSenderID);
			cStmt.setString(2, vPartnerId);
			cStmt.setString(3, vMessageType);
			cStmt.setString(4, vEDIOutMsgFormat);
			
			cStmt.registerOutParameter(5, Types.BIGINT);
			cStmt.registerOutParameter(6, Types.BIGINT);
			
			cStmt.execute();
			vEdiCtlNo = cStmt.getLong(5);
			vEdiGroupCtlNo = cStmt.getLong(6);
		} finally {
			try { cStmt.close(); } catch (Exception ex) {}
		}
		
		def retLong = [vEdiCtlNo, vEdiGroupCtlNo];
		return retLong;
	}
	
	/**
	 * Outgoing Xml edi sequence function
	 * @param vPartnerId
	 * @param vMessageType
	 * @param vMessageFormat
	 * @param vInboundMessageType
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public long getXMLControlNumber(String vPartnerId, String vMessageType, String vMessageFormat, String vInboundMessageType, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		
		CallableStatement cStmt = null;
		long vXmlCtlNo = -1;
		try {
			cStmt = conn.prepareCall("{? = call B2B_PLSQL.B2BEDI_XML_PKG.GET_BATCH_NUMBER (?, ?, ?, ?)}");
			cStmt.setQueryTimeout(getDBTimeOutInSeconds());
			
			cStmt.registerOutParameter(1, Types.BIGINT);
			cStmt.setString(2, vPartnerId);
			cStmt.setString(3, vMessageType);
			cStmt.setString(4, vMessageFormat);
			cStmt.setString(5, vInboundMessageType);
			
			cStmt.execute();
			vXmlCtlNo = cStmt.getLong(1);
		} finally {
			try { cStmt.close(); } catch (Exception ex) {}
		}
		return vXmlCtlNo;
	}
	
	public long getFileSequenceNumber(String vPartnerId, String vMessageType, String vMessageFormat, String vInboundMessageType, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		
		CallableStatement cStmt = null;
		long vXmlFileSeq = -1;
		try {
			cStmt = conn.prepareCall("{? = call B2B_PLSQL.B2BEDI_XML_PKG.GET_FILE_SEQ_NUMBER (?, ?, ?, ?)}");
			cStmt.setQueryTimeout(getDBTimeOutInSeconds());
			
			cStmt.registerOutParameter(1, Types.BIGINT);
			cStmt.setString(2, vPartnerId);
			cStmt.setString(3, vMessageType);
			cStmt.setString(4, vMessageFormat);
			cStmt.setString(5, vInboundMessageType);
			
			cStmt.execute();
			vXmlFileSeq = cStmt.getLong(1);
		} finally {
			try { cStmt.close(); } catch (Exception ex) {}
		}
		return vXmlFileSeq;
	}
	
	public String getEDICdeReffromIntCde(String TP_ID, String convertTypeId, String DIR_ID, String SrcFmtId, String SegId, String SegNum, String fromValue, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		if (TP_ID==null || convertTypeId==null || DIR_ID==null || SrcFmtId==null || SegId==null || SegNum==null || fromValue==null) {
			return ""
		}
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_edi_cde_ref where tp_id=? and convert_type_id=? and dir_id=? and src_fmt_id=? and seg_id=? and seg_num=? and int_cde=?";
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, convertTypeId);
			pre.setString(3, DIR_ID);
			pre.setString(4, SrcFmtId);
			pre.setString(5, SegId);
			pre.setString(6, SegNum);
			pre.setString(7, fromValue);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	String getCarrierID(String scac, Connection conn) {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		if (isEmpty(scac)) {
			return "";
		}
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select c.id from b2b_ocean_carrier c where c.scac = ?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, scac);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	public Map getLocationInfoFromUNLocCode(String unLocCode, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
		
		Map ret = new HashMap()
		if (isEmpty(unLocCode)) {
			return ret;
		}
	
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select CITY_NME, COUNTY, STATE, CNTRY_NME, CNTRY_CDE from CSS_CITY_LIST where UN_LOCN_CDE =?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, unLocCode);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret.put('City', result.getString(1))
				ret.put('County', result.getString(2))
				ret.put('State', result.getString(3))
				ret.put('Country', result.getString(4))
				ret.put('CSCountryCode', (result.getString(5)==null?null:result.getString(5).trim()))
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	public Map getPortInfoFromUNLocCode(String unLocCode, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available. ");
		}
	
		Map ret = new HashMap()
		
		if (isEmpty(unLocCode)) {
			return ret;
		}
		
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select PORT_ID,Port_Nme, Sched_Kd_Cde, SCHED_TYPE from css_port_list where Un_Locn_Cde =?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, unLocCode);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret.put('CSPortID', result.getString(1))
				ret.put('PortName', result.getString(2))
				ret.put('SchedKDCode', result.getString(3))
				ret.put('SchedKDType', result.getString(4))
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	//by Ben
	String cleanXml(String xml) {
		Node root = new XmlParser().parseText(xml)
		cleanNode(root)
		XmlUtil.serialize(root)
	}
	
	boolean cleanNode( Node node ) {
		node.attributes().with { a ->
			a.findAll { !it.value }.each { a.remove( it.key ) }
		}
		node.children().with { kids ->
			kids.findAll { it instanceof Node ? !cleanNode( it ) : false }
					.each { kids.remove( it ) }
		}
		node.attributes() || node.children() || node.text()
	}

	/**
	 *
	 * @param node
	 * @param nullable
	 * @param nullattr
	 * @return boolean
	 * @author Jenny
	 * extend cleanNode(Node node), to support empty tag and empty attriute
	 */
//	String cleanXml(String xml, boolean nullable, boolean nullattr) {
//		Node root = new XmlParser().parseText(xml)
//		if(nullable && nullattr){
//			cleanNodeNullableNullattr(root)
//		}else if(nullable){
//			cleanNodeNullable (root)
//		}else {
//			cleanNode(root)
//		}
//		XmlUtil.serialize(root)
//	}

	/**
	 * @param node
	 * @param nillable
	 * @param nullattr
	 * @return boolean
	 * @author Jenny
	 * extend cleanNode(Node node), to support empty tag and empty attriute
	 */
//	boolean cleanNode( Node node, boolean nullable , boolean nullattr) {
//		if(nullable && nullattr){
//			return cleanNodeNullableNullattr (node)
//		}else if(nullable){
//			return cleanNodeNullable (node)
//		}else {
//			return cleanNode( node )
//		}
//	}


	/**
	 * @param node
	 * @param nillable
	 * @return boolean
	 * @author renga
	 * extend cleanXml(Node node), to support empty tag
	 */
	String cleanXml(String xml, boolean nullable ) {
		Node root = new XmlParser().parseText(xml)
		if(nullable){
			cleanNodeNullable (root)
		}else {
			cleanNode(root)
		}
		XmlUtil.serialize(root)
	}

	/**
	 * @param node
	 * @param nillable
	 * @return boolean
	 * @author renga
	 * extend cleanNode(Node node), to support empty tag
	 */
	boolean cleanNode( Node node, boolean nullable ) {
		if(nullable){
			return cleanNodeNullable (node)
		}else {
			return cleanNode( node )
		}
	}


	/**
	 * @param node
	 * @return boolean
	 * @author Jenny
	 * core function for supporting empty tag and empty attribute
	 */

//	boolean cleanNodeNullableNullattr(Node node){
//		if(node.attribute("null")?.toString()?.toBoolean()) {
//			node.attributes().remove("null")
//			if(node.children().size() > 0) {
//				if(node.attribute("nullAttr")){
//					def nullAttr=node.attribute("nullAttr")
//					node.attributes().remove("nullAttr")
//					node.attributes().with { a ->
//						a.findAll { it.key != nullAttr ? !it.value : false }.each { a.remove(it.key) } }
//				}else{
//					node.attributes().with { a ->
//						a.findAll {!it.value }.each {a.remove(it.key)} }
//				}
//				node.children().with { kids ->
//					kids.findAll { it instanceof Node ? !cleanNodeNullableNullattr(it) : false }
//							.each { kids.remove(it) }
//				}
//				return node.attributes() || node.children() || node.text()
//			}else{
//				if(node.attribute("nullAttr"))
//					node.attributes().remove("nullAttr")
//				return true
//			}
//		}else{
//			if(node.attribute("nullAttr")){
//				def nullAttr=node.attribute("nullAttr")
//				node.attributes().remove("nullAttr")
//				node.attributes().with { a ->
//					a.findAll { it.key != nullAttr ? !it.value : false }.each { a.remove(it.key) } }
//			}else{
//				node.attributes().with { a ->
//					a.findAll {!it.value }.each {a.remove(it.key)} }
//			}
//			node.children().with { kids ->
//				kids.findAll { it instanceof Node ? !cleanNodeNullableNullattr(it) : false }
//						.each { kids.remove(it) }
//			}
//			return node.attributes() || node.children() || node.text()
//		}
//	}

	/**
	 * @param node
	 * @return boolean
	 * @author renga
	 * core function for supporting empty tag
	 */

	boolean cleanNodeNullable(Node node){
		if(node.attribute("null")?.toString()?.toBoolean()) {
			node.attributes().remove("null")
			if(node.children().size() > 0) {
				if(node.attribute("nullAttr")){
					def nullAttr=node.attribute("nullAttr")
					node.attributes().remove("nullAttr")
					node.attributes().with { a ->
						a.findAll { it.key != nullAttr ? !it.value : false }.each { a.remove(it.key) } }
				}else{
					node.attributes().with { a ->
						a.findAll {!it.value }.each {a.remove(it.key)} }
				}
				node.children().with { kids ->
					kids.findAll { it instanceof Node ? !cleanNodeNullable(it) : false }
							.each { kids.remove(it) }
				}
				return node.attributes() || node.children() || node.text()
			}else{
				if(node.attribute("nullAttr"))
					node.attributes().remove("nullAttr")
				return true
			}
		}else{
			if(node.attribute("nullAttr")){
				def nullAttr=node.attribute("nullAttr")
				node.attributes().remove("nullAttr")
				node.attributes().with { a ->
					a.findAll { it.key != nullAttr ? !it.value : false }.each { a.remove(it.key) } }
			}else{
				node.attributes().with { a ->
					a.findAll {!it.value }.each {a.remove(it.key)} }
			}
			node.children().with { kids ->
				kids.findAll { it instanceof Node ? !cleanNodeNullable(it) : false }
						.each { kids.remove(it) }
			}
			return node.attributes() || node.children() || node.text()
		}
	}


//	boolean cleanNodeNullable(Node node){
//		if(node.attribute("null")?.toString()?.toBoolean()) {
//			node.attributes().remove("null")
//			if(node.children().size() > 0) {
//				node.attributes().with { a ->
//					a.findAll {!it.value  }.each { a.remove(it.key) }
//				}
//				node.children().with { kids ->
//					kids.findAll { it instanceof Node ? !cleanNodeNullable(it) : false }
//							.each { kids.remove(it) }
//				}
//				return node.attributes() || node.children() || node.text()
//			}else{
//				return true
//			}
//		}else{
//			node.attributes().with { a ->
//				a.findAll {!it.value }.each { a.remove(it.key)}
//			}
//			node.children().with { kids ->
//				kids.findAll { it instanceof Node ? !cleanNodeNullable(it) : false }
//						.each { kids.remove(it) }
//			}
//			return node.attributes() || node.children() || node.text()
//		}
//	}


	
	// Master City
	public HashMap<String, String> getCS2MasterCityByStateNameCityNameCntryCde(String StateName, String CityName, String CountryCode, Connection conn) throws Exception {
		HashMap<String, String> retMap = new HashMap<String, String>();
		if (isEmpty(StateName) || isEmpty(CityName) || isEmpty(CountryCode)) {
			return retMap;
		}
		return getCS2MasterCityByImpl(StateName, CityName, CountryCode, conn);
	}
	
	public HashMap<String, String> getCS2MasterCityByCityNameCntryCde(String CityName, String CountryCode, Connection conn) throws Exception {
		return getCS2MasterCityByImpl(null, CityName, CountryCode, conn);
	}
	
	/**
	 * if StateName is null, then no query with STATE_NME
	 * @param StateName
	 * @param CityName
	 * @param CountryCode
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private HashMap<String, String> getCS2MasterCityByImpl(String StateName, String CityName, String CountryCode, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city and country query. ");
		}
	
		HashMap<String, String> retMap = new HashMap<String, String>();
		if (isEmpty(CityName) || isEmpty(CountryCode)) {
			return retMap;
		}
		
		PreparedStatement pre = null;
		ResultSet result = null;
		
		String sql = "select CITY_NME, COUNTY_NME, STATE_CDE, STATE_NME, CNTRY_CDE, CNTRY_NME, UN_LOCN_CDE, CITY_TYPE, SCHED_TYPE, SCHED_KD_CDE from cs2_master_city where ";
		if (isNotEmpty(StateName)) {
			sql += "upper(STATE_NME) = ? and "
		}
		sql += "upper(city_nme) = ? and upper(cntry_cde) = ?";
		
		try {
			pre = conn.prepareStatement(sql);
			//this query only need get the 1st record only
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			int seqIndex = 1;
			if (StateName!=null) {
				pre.setString(seqIndex++, StateName.toUpperCase());
			}
			pre.setString(seqIndex++, CityName.toUpperCase());
			pre.setString(seqIndex++, CountryCode.toUpperCase());
			result = pre.executeQuery();
	
			if (result.next()) {
				retMap.put("CITY_NME", (result.getString("CITY_NME")==null?"":result.getString("CITY_NME").trim()));
				retMap.put("COUNTY_NME", (result.getString("COUNTY_NME")==null?"":result.getString("COUNTY_NME").trim()));
				retMap.put("STATE_CDE", (result.getString("STATE_CDE")==null?"":result.getString("STATE_CDE").trim()));
				retMap.put("STATE_NME", (result.getString("STATE_NME")==null?"":result.getString("STATE_NME").trim()));
				retMap.put("CNTRY_CDE", (result.getString("CNTRY_CDE")==null?"":result.getString("CNTRY_CDE").trim()));
				retMap.put("CNTRY_NME", (result.getString("CNTRY_NME")==null?"":result.getString("CNTRY_NME").trim()));
				retMap.put("UN_LOCN_CDE", (result.getString("UN_LOCN_CDE")==null?"":result.getString("UN_LOCN_CDE").trim()));
				retMap.put("CITY_TYPE", (result.getString("CITY_TYPE")==null?"":result.getString("CITY_TYPE").trim()));
				retMap.put("SCHED_TYPE", (result.getString("SCHED_TYPE")==null?"":result.getString("SCHED_TYPE").trim()));
				retMap.put("SCHED_KD_CDE", (result.getString("SCHED_KD_CDE")==null?"":result.getString("SCHED_KD_CDE").trim()));
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return retMap;
	}
	
	public HashMap<String, String> getCS2MasterCityByStateNameCityName(String StateName, String CityName, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for state and city query. ");
		}
		
		HashMap<String, String> retMap = new HashMap<String, String>();
		
		if (StateName==null || CityName==null) {
			return retMap;
		}
		
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select CITY_NME, COUNTY_NME, STATE_CDE, STATE_NME, CNTRY_CDE, CNTRY_NME, UN_LOCN_CDE, CITY_TYPE from cs2_master_city where upper(STATE_NME) = ? and upper(city_nme) = ?";
	
		try {
			pre = conn.prepareStatement(sql);
			//this query only need get the 1st record only
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, StateName.toUpperCase());
			pre.setString(2, CityName.toUpperCase());
			result = pre.executeQuery();
	
			if (result.next()) {
				retMap.put("CITY_NME", (result.getString("CITY_NME")==null?"":result.getString("CITY_NME").trim()));
				retMap.put("COUNTY_NME", (result.getString("COUNTY_NME")==null?"":result.getString("COUNTY_NME").trim()));
				retMap.put("STATE_CDE", (result.getString("STATE_CDE")==null?"":result.getString("STATE_CDE").trim()));
				retMap.put("STATE_NME", (result.getString("STATE_NME")==null?"":result.getString("STATE_NME").trim()));
				retMap.put("CNTRY_CDE", (result.getString("CNTRY_CDE")==null?"":result.getString("CNTRY_CDE").trim()));
				retMap.put("CNTRY_NME", (result.getString("CNTRY_NME")==null?"":result.getString("CNTRY_NME").trim()));
				retMap.put("UN_LOCN_CDE", (result.getString("UN_LOCN_CDE")==null?"":result.getString("UN_LOCN_CDE").trim()));
				retMap.put("CITY_TYPE", (result.getString("CITY_TYPE")==null?"":result.getString("CITY_TYPE").trim()));
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return retMap;
	}
	
	public HashMap<String, String> getCS2MasterCityByStateName(String StateName, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for state query. ");
		}
		HashMap<String, String> retMap = new HashMap<String, String>();
		if (StateName==null) {
			return retMap;
		}
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select CITY_NME, COUNTY_NME, STATE_CDE, STATE_NME, CNTRY_CDE, CNTRY_NME, UN_LOCN_CDE, CITY_TYPE from cs2_master_city where upper(STATE_NME) = ?";
	
		try {
			pre = conn.prepareStatement(sql);
			//this query only need get the 1st record only
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, StateName.toUpperCase());
			result = pre.executeQuery();
	
			if (result.next()) {
				retMap.put("CITY_NME", (result.getString("CITY_NME")==null?"":result.getString("CITY_NME").trim()));
				retMap.put("COUNTY_NME", (result.getString("COUNTY_NME")==null?"":result.getString("COUNTY_NME").trim()));
				retMap.put("STATE_CDE", (result.getString("STATE_CDE")==null?"":result.getString("STATE_CDE").trim()));
				retMap.put("STATE_NME", (result.getString("STATE_NME")==null?"":result.getString("STATE_NME").trim()));
				retMap.put("CNTRY_CDE", (result.getString("CNTRY_CDE")==null?"":result.getString("CNTRY_CDE").trim()));
				retMap.put("CNTRY_NME", (result.getString("CNTRY_NME")==null?"":result.getString("CNTRY_NME").trim()));
				retMap.put("UN_LOCN_CDE", (result.getString("UN_LOCN_CDE")==null?"":result.getString("UN_LOCN_CDE").trim()));
				retMap.put("CITY_TYPE", (result.getString("CITY_TYPE")==null?"":result.getString("CITY_TYPE").trim()));
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return retMap;
	}
	
	
	public HashMap<String, String> getCS2MasterCityByUNLocCityType(String UnLocnCde, String CityType, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		HashMap<String, String> retMap = new HashMap<String, String>();
		if (UnLocnCde==null || CityType==null) {
			return retMap;
		}
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select CITY_NME, COUNTY_NME, STATE_CDE, STATE_NME, CNTRY_CDE, CNTRY_NME, UN_LOCN_CDE, CITY_TYPE from cs2_master_city where upper(UN_LOCN_CDE)=? and CITY_TYPE=?";
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, UnLocnCde.toUpperCase());
			pre.setString(2, CityType.toUpperCase());
			result = pre.executeQuery();
	
			if (result.next()) {
				retMap.put("CITY_NME", (result.getString("CITY_NME")==null?"":result.getString("CITY_NME").trim()));
				retMap.put("COUNTY_NME", (result.getString("COUNTY_NME")==null?"":result.getString("COUNTY_NME").trim()));
				retMap.put("STATE_CDE", (result.getString("STATE_CDE")==null?"":result.getString("STATE_CDE").trim()));
				retMap.put("STATE_NME", (result.getString("STATE_NME")==null?"":result.getString("STATE_NME").trim()));
				retMap.put("CNTRY_CDE", (result.getString("CNTRY_CDE")==null?"":result.getString("CNTRY_CDE").trim()));
				retMap.put("CNTRY_NME", (result.getString("CNTRY_NME")==null?"":result.getString("CNTRY_NME").trim()));
				retMap.put("UN_LOCN_CDE", (result.getString("UN_LOCN_CDE")==null?"":result.getString("UN_LOCN_CDE").trim()));
				retMap.put("CITY_TYPE", (result.getString("CITY_TYPE")==null?"":result.getString("CITY_TYPE").trim()));
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return retMap;
	}
	
	public HashMap<String, String> getCS2MasterCityByStateCntryNoEmptyStateCde(String stateName, String countryName, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		HashMap<String, String> retMap = new HashMap<String, String>();
		if (stateName==null || countryName==null) {
			return retMap
		}
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select CITY_NME, COUNTY_NME, STATE_CDE, STATE_NME, CNTRY_CDE, CNTRY_NME, UN_LOCN_CDE, CITY_TYPE from cs2_master_city where upper(STATE_NME)=? and upper(CNTRY_CDE)=? and STATE_CDE != ' ' ";
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, stateName.toUpperCase());
			pre.setString(2, countryName.toUpperCase());
			result = pre.executeQuery();
	
			if (result.next()) {
				retMap.put("CITY_NME", (result.getString("CITY_NME")==null?"":result.getString("CITY_NME").trim()));
				retMap.put("COUNTY_NME", (result.getString("COUNTY_NME")==null?"":result.getString("COUNTY_NME").trim()));
				retMap.put("STATE_CDE", (result.getString("STATE_CDE")==null?"":result.getString("STATE_CDE").trim()));
				retMap.put("STATE_NME", (result.getString("STATE_NME")==null?"":result.getString("STATE_NME").trim()));
				retMap.put("CNTRY_CDE", (result.getString("CNTRY_CDE")==null?"":result.getString("CNTRY_CDE").trim()));
				retMap.put("CNTRY_NME", (result.getString("CNTRY_NME")==null?"":result.getString("CNTRY_NME").trim()));
				retMap.put("UN_LOCN_CDE", (result.getString("UN_LOCN_CDE")==null?"":result.getString("UN_LOCN_CDE").trim()));
				retMap.put("CITY_TYPE", (result.getString("CITY_TYPE")==null?"":result.getString("CITY_TYPE").trim()));
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return retMap;
	}
	
	/**
	 * Query state_cde value by state_cde index
	 * @param stateName
	 * @param countryName
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(String stateName, String countryName, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		if (stateName==null || countryName==null) {
			return ''
		}
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select distinct STATE_CDE from cs2_master_city where upper(STATE_NME)=? and upper(CNTRY_CDE)=? and STATE_CDE != ' ' ";
		String ret = '';
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, stateName.toUpperCase());
			pre.setString(2, countryName.toUpperCase());
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = (result.getString("STATE_CDE")==null?"":result.getString("STATE_CDE").trim())
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	//2017-05-27 enhance to cntry name
	public String getCS2MasterCity4CountryCodeByCountryName(String countryName, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		if (!countryName) {
			return ""
		}
		countryName = countryName.toUpperCase()
		if (countryName.indexOf("&AMP;")>=0) {
			countryName = countryName.replace("&AMP;", "AND")
		}
		
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select CNTRY_CDE from cs2_master_city where upper(CNTRY_NME)=?";
		String ret = "";
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, countryName);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = (result.getString("CNTRY_CDE")==null?"":result.getString("CNTRY_CDE").trim());
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	public String getCS2MasterCity4CountryNameByCountryCode(String countryCode, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		if (!countryCode) {
			return ""
		}
		countryCode = countryCode.toUpperCase()
		
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select CNTRY_NME from cs2_master_city where upper(CNTRY_CDE)=?";
		String ret = "";
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, countryCode);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = (result.getString("CNTRY_NME")==null?"":result.getString("CNTRY_NME").trim());
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	public String getCS2Country4CountryNameByCountryCode(String countryCode, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		if (!countryCode) {
			return ""
		}
		countryCode = countryCode.toUpperCase()
		
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select country_name from cs2_country where upper(country_cde)=?";
		String ret = "";
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, countryCode);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = (result.getString("country_name")==null?"":result.getString("country_name").trim());
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	public String getCS2Country4CountryCodeByCountryName(String countryName, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		if (!countryName) {
			return ""
		}
		countryName = countryName.trim()
		
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select country_cde from cs2_country where country_name=?";
		String ret = "";
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, countryName);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = (result.getString("country_cde")==null?"":result.getString("country_cde").trim());
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	String removeUnreableChar(String inMessageContent) {
		String outMessageContent = inMessageContent
		int intLen = inMessageContent.length()
		StringBuffer sb = new StringBuffer()
		for (int i = 0; i < intLen; i++) {
			char chr = inMessageContent.charAt(i)
			if ((0x00 <= chr && chr <= 0x09) ||
				(0x0B <= chr && chr <= 0x0C) ||
				(0x0E <= chr && chr <= 0x1F) ||
				(0x7F <= chr && chr <= 0xFF) ||
				 //(chr == '*') ||
				 (chr == '`') ) {
				 sb.append (' ')
			 } else {
				 sb.append (chr)
			 }
		 }
		 outMessageContent = sb.toString()
		 return outMessageContent
	}
	
	/**
	 * @param scac
	 * @param conn
	 * @return String
	 * Get carrier tp id, for bizKey
	 */
	String getBizAgreementDisplayName(String tpId, String msgTypeId, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB connection is not available for biz query.");
		}
		if (tpId==null || msgTypeId==null) {
			return ""
		}
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select partner_display_name from b2b_tp_biz_agreements where tp_id=? and msg_type_id=?";
	
		try {
			pre = conn.prepareStatement(sql);
			//this method, only need get the 1st record
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, tpId);
			pre.setString(2, msgTypeId);
			result = pre.executeQuery();
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	/**
	 * @param scac
	 * @param conn
	 * @return String
	 * Get Carrier ID, for bizKey
	 */
	Map<String, String> getOceanCarrierInfo(String scac, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
		
		Map<String, String> retMap = new HashMap<String, String>()
		if (isEmpty(scac))
			return retMap;
		
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ID, NAME, ICSS_ID, SCAC from b2b_ocean_carrier where scac = ?";

		try {
			pre = conn.prepareStatement(sql);
			//this method, only need the 1st record
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, scac);
			result = pre.executeQuery();

			if (result.next()) {
				retMap.put("ID", result.getString("ID"));
				retMap.put("NAME", result.getString("NAME"));
				retMap.put("ICSS_ID", result.getString("ICSS_ID"));
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return retMap;
	}
	
	/**
	 * @param scac
	 * @param conn
	 * @return String
	 * Get carrier tp id, for bizKey
	 */
	String getSCACTpIDFromScacTpMap(String scac, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
		String ret = "";
		if (isEmpty(scac)) {
			return ret;
		}
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select tp_id from b2b_scac_tp_map  where SCAC = ?";
		try {
			pre = conn.prepareStatement(sql);
			//only need the 1st record
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, scac);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	public Map<String, String> getCountryMap(Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for country query. ");
		}
		Map<String, String> ret = new HashMap<String, String>()
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql ="SELECT COUNTRY_CDE, UPPER(COUNTRY_NAME) FROM CS2_COUNTRY";
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			result = pre.executeQuery();
			while (result.next()) {
				if (isNotEmpty(result.getString(2)) && isNotEmpty(result.getString(1))) {
					ret.put(result.getString(2), result.getString(1))
				}
			}
		} finally {
			closeQueryResource(pre, result)
		}
		return ret;
	}
	
	void closeQueryResource(PreparedStatement pre, ResultSet result) {
		if (result != null) {
			try {
				result.close()
			} catch (Exception e) {}
		}
		if (pre != null) {
			try {
				pre.close()
			} catch (Exception e) {}
		}
	}
	
	
}

