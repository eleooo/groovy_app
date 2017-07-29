package cs.b2b.core.mapping.util

import cs.b2b.core.mapping.bean.EventDT
import cs.b2b.core.mapping.bean.ct.BLGeneralInfo
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.Cargo
import cs.b2b.core.mapping.bean.ct.Container
import cs.b2b.core.mapping.bean.ct.Event
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.MileStones
import cs.b2b.core.mapping.bean.ct.Party


import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


import org.apache.commons.lang3.SerializationUtils
import cs.b2b.core.common.util.StringUtil;

import groovy.xml.MarkupBuilder;

class MappingUtil_CT_O_Common {

	private static final String COMPLETE = 'C'

	private static final String OBSOLETE = 'O'

	private static final String ERROR = 'E'

	private static final String YES = 'YES'

	private static final String NO = 'NO'

	private static final String TYPE = 'Type'

	private static final String ERROR_SUPPORT = 'ES'

	private static final String IS_ERROR = 'IsError'

	private static final String VALUE = 'Value'

	private static final String ERROR_COMPLETE = 'EC'

	private cs.b2b.core.mapping.util.MappingUtil util;

	public MappingUtil_CT_O_Common() {

	}

	public MappingUtil_CT_O_Common(cs.b2b.core.mapping.util.MappingUtil util) {
		this.util = util;
	}

	public int getDBRowLimit() {
		int DB_MAX_RETURN_ROW = 10000;
		return DB_MAX_RETURN_ROW;
	}

	public int getDBTimeOutInSeconds() {
		int DB_TIMEOUT_IN_SECCOND = 10;
		return DB_TIMEOUT_IN_SECCOND;
	}

	/**
	 * @param tp_id
	 * @param conn
	 * @return Map
	 * @author RENGA
	 * Get ct duplicated pairs from code conversion table
	 */
	public Map<String, String> getDuplicatedPairs(String tp_id, Connection conn) {
		Map<String, String> result = [:]
		if (conn == null)
			throw new Exception("Connection is not available for getDuplicatedPairs query.");


		String key = null;
		String value = null;
		PreparedStatement pre = null;
		ResultSet resultSet = null;
		String sql = "select substr(int_cde,0,5),int_cde from b2b_cde_conversion where tp_id = ? and convert_type_id = 'EventStatusCode' and msg_type_id = 'CT' and substr(int_cde,6) = ext_cde order by int_cde";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, tp_id);
			resultSet = pre.executeQuery();

			while (resultSet.next()) {
				key = resultSet.getString(1);
				value = resultSet.getString(2);
				result.put(key, value)
			}
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (pre != null)
				pre.close();
		}
		return result;
	}

	/**
	 * @param tp_id
	 * @param convert_type_id
	 * @param conn
	 * @return Map
	 * @author Tracy
	 * Get ct duplicated pairs from code conversion table with convert_type_id
	 */
	public Map<String, String> getDuplicatedPairsbyConvertType(String tp_id,String convert_type_id, Connection conn) {
		Map<String, String> result = [:]
		if (conn == null)
			throw new Exception("Connection is not available for getDuplicatedPairsbyConvertType query.");


		String key = null;
		String value = null;
		PreparedStatement pre = null;
		ResultSet resultSet = null;
		String sql = "select substr(int_cde,0,5),int_cde from b2b_cde_conversion where tp_id = ? and convert_type_id = ? and msg_type_id = 'CT' and substr(int_cde,6) = ext_cde order by int_cde";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, tp_id);
			pre.setString(2, convert_type_id);
			resultSet = pre.executeQuery();

			while (resultSet.next()) {
				key = resultSet.getString(1);
				value = resultSet.getString(2);
				result.put(key, value)
			}
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (pre != null)
				pre.close();
		}
		return result;
	}

	/**
	 * @param BodyLoop
	 * @param duplicatedPairs  Map<String, String>
	 * @return List < ct.Body >
	 * @author RENGA
	 * CT event duplication method
	 */
	public List<cs.b2b.core.mapping.bean.ct.Body> CTEventDuplication(List<cs.b2b.core.mapping.bean.ct.Body> Body,
																	 def duplicatedPairs) {

		List<cs.b2b.core.mapping.bean.ct.Body> bodies = [];

		Body.eachWithIndex { current_Body, current_BodyIndex ->
			bodies.add(current_Body)

			if (duplicatedPairs.get(current_Body.Event?.CS1Event)) {
				//deep clone
				cs.b2b.core.mapping.bean.ct.Body duplBody = (cs.b2b.core.mapping.bean.ct.Body) SerializationUtils.clone(current_Body)
				duplBody.Event.CS1Event = duplicatedPairs.get(current_Body.Event.CS1Event)
				bodies.add(duplBody)
			}
		}

		return bodies;
	}
	/**
	 * @param EventDuplicatebyConfitions
	 * @return List < ct.Body >
	 * @author Tracy
	 * Container.Haulage.InBound=”M” and GeneralInformation.SCAC=”OOLU”
	 */
	public List<cs.b2b.core.mapping.bean.ct.Body> CTEventDuplicationbyBodByConditions(List<cs.b2b.core.mapping.bean.ct.Body> Body, def duplicatedPairs){
		List<cs.b2b.core.mapping.bean.ct.Body> bodies = [];

		Body.eachWithIndex { current_Body, current_BodyIndex ->
			bodies.add(current_Body)
			if(current_Body?.Container?.Haulage?.InBound == 'M' && current_Body?.GeneralInformation?.SCAC=="OOLU" && current_Body.Event?.CS1Event){
				if (duplicatedPairs.get(current_Body.Event.CS1Event)) {
					//deep clone
					cs.b2b.core.mapping.bean.ct.Body duplBody = (cs.b2b.core.mapping.bean.ct.Body) SerializationUtils.clone(current_Body)
					duplBody.Event.CS1Event = duplicatedPairs.get(current_Body.Event.CS1Event)
					bodies.add(duplBody)
				}
			}
		}
		return bodies;
	}

	/**
     * BL dispatching, for multiple BLs in same CT body, split and dispatch BL into different CT body
	 * @param BodyLoop
	 * @return List < ct.Body >
	 * @author RENGA
	 * Bl duplication method
	 */
	public List<cs.b2b.core.mapping.bean.ct.Body> CTBLDuplication(List<cs.b2b.core.mapping.bean.ct.Body> Body) {
		List<cs.b2b.core.mapping.bean.ct.Body> bodies = [];

		List<cs.b2b.core.mapping.bean.ct.BLGeneralInfo> blGeneralInfos = [];

		Body.eachWithIndex { current_Body, current_BodyIndex ->

			def blCount = current_Body?.BLGeneralInfo?.size()

			if (blCount > 1) {
				blGeneralInfos = current_Body.BLGeneralInfo
				current_Body.BLGeneralInfo = []
				for (i in 0..<blCount) {
					//deep clone
					cs.b2b.core.mapping.bean.ct.Body duplBody = (cs.b2b.core.mapping.bean.ct.Body) SerializationUtils.clone(current_Body)

					duplBody.BLGeneralInfo.add(blGeneralInfos[i])

					bodies.add(duplBody)
				}
			} else {
				bodies.add(current_Body)
			}
		}

		return bodies
	}

	/**
	 * @param scac
	 * @param conn
	 * @return String
	 * Get carrier tp id, for bizKey
	 */
	String getSCACTpId(String scac, Connection conn) {
		if (conn == null)
			throw new Exception("Connection is not available for SCAC query.");

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select tp_id from b2b_scac_tp_map  where SCAC = ?";

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
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	//please use getConversionByTpIdDirFmtScac from MappingUtil to replccace this function
    public String ConvertContainerSizeType(String convertTypeID, String tp_id, String scac_cde, String msg_fmt_id, String int_cde, String dir_id, Connection conn) {
        if (conn == null)
            throw new Exception("Connection is not available for ConvertContainerSizeType query.");

        String ret = "";
        PreparedStatement pre = null;
        ResultSet result = null;
        String sql = "SELECT ext_cde FROM B2B_CDE_CONVERSION  WHERE convert_type_id = ?  AND tp_id = ? AND scac_cde = ? AND msg_fmt_id = ? AND Int_Cde = ? AND dir_id = ? ";

        try {
            pre = conn.prepareStatement(sql);
            pre.setMaxRows(1);
            pre.setQueryTimeout(10);
            pre.setString(1, convertTypeID);
            pre.setString(2, tp_id);
            pre.setString(3, scac_cde);
            pre.setString(4, msg_fmt_id);
            pre.setString(5, int_cde);
            pre.setString(6, dir_id);

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

	public String getFacilityCodeQuery(String int_cde, String convertType, String dirID, String msg_type_id, String interchangeID, String msg_fmt_id, Connection conn) {
		if (conn == null)
            throw new Exception("Connection is not available for getFacilityCodeQuery query.");

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where b2b_cde_conversion.int_cde = ? and b2b_cde_conversion.convert_type_id = ? and b2b_cde_conversion.dir_id = ? and b2b_cde_conversion.msg_type_id = ? and b2b_cde_conversion.tp_id = ? and b2b_cde_conversion.msg_fmt_id = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(1);
			pre.setQueryTimeout(10);
			pre.setString(1, int_cde);
			pre.setString(2, convertType);
			pre.setString(3, dirID);
			pre.setString(4, msg_type_id);
			pre.setString(5, interchangeID);
			pre.setString(6, msg_fmt_id);

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
	 * @param scac
	 * @param conn
	 * @return String
	 * Get SCAC ID,
	 */
	public String getCodeConversionbyConvertTypeSCAC(String convertTypeID,String scac, Connection conn) {
		if (conn == null)
            throw new Exception("Connection is not available for getCarrierID query.");

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select Ext_CDE from B2B_CDE_CONVERSION where convert_type_id = ?  and SCAC_CDE = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, convertTypeID);
			pre.setString(2, scac);
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
	 * @param scac
	 * @param conn
	 * @return String
	 * Get SCAC ID, for bizKey
	 */
	String getCarrierID(String scac, Connection conn) {
		if (conn == null)
			throw new Exception("Connection is not available for getCarrierID query.");

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
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	/**
	 * @param cityName
	 * @param unlocationCode
	 * @param countryCode
	 * @param conn
	 * @return Map
	 * Get CT Event port id, port name, city id, city name, country name, for bizkey
	 */
	Map<String, String> getCS2EventInfo(String cityName, String unlocationCode, String countryCode, Connection conn) {

		if (conn == null)
            throw new Exception("Connection is not available for getCS2EventInfo query.");

		def ret = [:];
		PreparedStatement pre = null;
		ResultSet result = null;
		PreparedStatement pre2 = null;
		PreparedStatement pre3 = null;
		PreparedStatement pre4 = null;
		ResultSet result2 = null;
		ResultSet result3 = null;
		ResultSet result4 = null;
		String sql1 = "select STND_PORT_ID, PORT_NAME from cs2_port_list where  UPPER(port_name) = UPPER(?)and port_type ='S' and rownum = 1";
		String sql4 = "select STND_PORT_ID, PORT_NAME from cs2_port_list where  UN_LOCATION_CODE =? and port_type ='S' and rownum = 1";
		String sql2 = "select stnd_city_id, city_nme, cntry_nme,cntry_cde, un_locn_cde from cs2_master_city where UN_LOCN_CDE = ? and city_type = 'S' and rownum=1";
		String sql3 = " select stnd_city_id, city_nme, cntry_nme,cntry_cde, un_locn_cde from cs2_master_city where UPPER(city_nme) = UPPER(?) and CNTRY_CDE = ? and city_type = 'S' and rownum=1";

		try {
			pre = conn.prepareStatement(sql1);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, cityName);

			result = pre.executeQuery();

			//port
			if (result.next()) {
				ret.put('CT_Event_port_id', result.getString(1))
				ret.put('CT_Event_port_nme', result.getString(2))
			}else{
				pre4 = conn.prepareStatement(sql4);
				pre4.setMaxRows(getDBRowLimit());
				pre4.setQueryTimeout(getDBTimeOutInSeconds());
				pre4.setString(1, unlocationCode);
				result4 = pre4.executeQuery();
				if(result4.next()){
					ret.put('CT_Event_port_id', result4.getString(1))
					ret.put('CT_Event_port_nme', result4.getString(2))
				}
			}

			pre2 = conn.prepareStatement(sql2);
			pre2.setMaxRows(getDBRowLimit());
			pre2.setQueryTimeout(getDBTimeOutInSeconds());
			pre2.setString(1, unlocationCode);
			result2 = pre2.executeQuery();

			// city
			if (result2.next()) {
				ret.put('CT_Event_city_id', result2.getString(1))
				ret.put('CT_Event_city_nme', result2.getString(2))
				ret.put('CT_Event_cntry_cde', result2.getString(4))
			}else{
				pre3 = conn.prepareStatement(sql3);
				pre3.setMaxRows(getDBRowLimit());
				pre3.setQueryTimeout(getDBTimeOutInSeconds());
				pre3.setString(1, cityName);
				pre3.setString(2, countryCode);
				result3 = pre3.executeQuery();
				if(result3.next()){
					ret.put('CT_Event_city_id', result3.getString(1))
					ret.put('CT_Event_city_nme', result3.getString(2))
					ret.put('CT_Event_cntry_cde', result3.getString(4))
				}
			}


		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
			if (result2 != null)
				result2.close();
			if (pre2 != null)
				pre2.close();
			if(pre3!=null)
				pre3.close();
			if(pre4!=null)
				pre4.close();
		}
		return ret;
	}

	//please don't use this function to get UN_LOCATION_CDE ,CSS_CITY_LIST should be abandoned.
	public Map<String ,String> getUNLocCodeFromLocationInfo(String city, String state, String CSCountryCode, Connection conn) throws Exception {
		if (conn == null)
            throw new Exception("Connection is not available for getUNLocCodeFromLocationInfo query.");

		Map ret = new HashMap()
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql ="select distinct UN_LOCN_CDE, SCHED_TYPE, SCHED_KD_CDE from CSS_CITY_LIST where UPPER(city_nme) = UPPER(?) and STATE_CDE =? and CNTRY_CDE = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(100);
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, city);
			pre.setString(2, state);
			pre.setString(3, CSCountryCode);

			result = pre.executeQuery();

			while (result.next()) {
				ret.put('UNLocCode', result.getString(1))
				ret.put('SchedType', result.getString(2))
				ret.put('SchedKDCde', result.getString(3))
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	//please don't use this function to get UN_LOCATION_CDE ,CSS_CITY_LIST should be abandoned.
	public Map<String ,String> getUNLocCodeFromCityInfo(String city, String CSCountryCode, Connection conn) throws Exception {
		if (conn == null)
            throw new Exception("Connection is not available for getUNLocCodeFromCityInfo query.");

		Map ret = new HashMap()
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql ="select distinct UN_LOCN_CDE, SCHED_TYPE, SCHED_KD_CDE from CSS_CITY_LIST where UPPER(city_nme) = UPPER(?) and CNTRY_CDE = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(100);
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, city);
			pre.setString(2, CSCountryCode);
			result = pre.executeQuery();

			if (result.next()) {
				ret.put('UNLocCode', result.getString(1))
				ret.put('SchedType', result.getString(2))
				ret.put('SchedKDCde', result.getString(3))
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}


	public String getCodeConversionbyCovertType(String msg_type_id, String dir_id,String CONVERT_TYPE_ID,String int_cde,  Connection conn) throws Exception {
		if (conn == null)
            throw new Exception("Connection is not available for getCodeConversionbyCovertType query.");

		String ret;
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where msg_type_id=? and dir_id=? and CONVERT_TYPE_ID=? and int_cde =? ";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(100);
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, msg_type_id);
			pre.setString(2, dir_id);
			pre.setString(3, CONVERT_TYPE_ID);
			pre.setString(4, int_cde);
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

	public Map<String ,String> getConversionByIntCdeandScacCde(String convertTypeId,String intCode,String scacCode, Connection conn) throws Exception {
		if (conn == null)
            throw new Exception("Connection is not available for getConversionByIntCdeandScacCde query.");

		Map ret = new HashMap()
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select tp_id, int_cde, ext_cde from b2b_cde_conversion where  convert_type_id=? and int_cde=? and scac_cde=?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(1);
			pre.setQueryTimeout(getDBTimeOutInSeconds());

			pre.setString(1, convertTypeId);
			pre.setString(2, intCode);
			pre.setString(3, scacCode);
			result = pre.executeQuery();

			if (result.next()) {
				ret.put('TP_ID',result.getString(1)) ;
				ret.put('INT_CDE',result.getString(2)) ;
				ret.put('EXT_CDE',result.getString(3)) ;
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	public static String getCS2MasterCitybyUnlocationCde(String unlocationCode, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		if (unlocationCode==null) {
			return ''
		}
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select CITY_NME from cs2_master_city  where un_locn_cde =?";
		String ret = '';
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(10);
			pre.setString(1, unlocationCode);

			result = pre.executeQuery();

			if (result.next()) {
				ret = (result.getString("CITY_NME")==null ? "" : result.getString("CITY_NME").trim())
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	public static String getCS2MasterCitybySKDcodeandType(String skdtype, String skdcode,Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		if (skdcode==null||skdtype==null) {
			return ''
		}
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "SELECT UN_LOCN_CDE FROM CS2_MASTER_CITY CMC WHERE CMC.SCHED_TYPE=? AND CMC.SCHED_KD_CDE=? and CMC.city_type='S'";
		String ret = '';
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(10);
			pre.setString(1, skdtype);
			pre.setString(2, skdcode);

			result = pre.executeQuery();

			if (result.next()) {
				ret = (result.getString("UN_LOCN_CDE")==null ? "" : result.getString("UN_LOCN_CDE").trim())
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	public String getCSEventCodebyExtCde(String ext_cde, Connection conn) throws Exception {
		if (conn == null)
            throw new Exception("Connection is not available for getCSEventCodebyExtCde query.");

		if (ext_cde==null) {
			return ''
		}
		String ret;
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select id from b2b_ct_event_type where ext_cde=? ";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(1);
			pre.setQueryTimeout(10);
			pre.setString(1, ext_cde);

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
	 * @param csuploadXml
	 * @param csuploadErrorKey
	 * @param Interchange
	 * @param MSG_REQ_ID
	 * @return MarkupBuilder
	 */
	public void buildCsupload(MarkupBuilder csuploadXml, List csuploadErrorKey, String Interchange, String MSG_REQ_ID) {

		def currentSystemDt = new Date()
		def appErrorReportError = csuploadErrorKey.find{it?.IS_ERROR == YES}
		def appErrorReportObsolete = csuploadErrorKey.find{it?.IS_ERROR == NO}
		csuploadXml.'ns0:CSUploadKeyTrack'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/CSUploadKeyTrack.xsd') {
			'ns0:ErrorID' 'SEND_TO_TP'
			if (csuploadErrorKey.isEmpty()) {
				'ns0:CSUploadErrorKey' {
					'ns0:Type' COMPLETE
					'ns0:Value' MSG_REQ_ID + ".request"
					'ns0:IsError' NO
				}
			} else {
				if (appErrorReportObsolete != null) {
					'ns0:CSUploadErrorKey' {
						'ns0:Type' appErrorReportObsolete?.TYPE
						'ns0:Value' appErrorReportObsolete?.VALUE
						'ns0:IsError' appErrorReportObsolete?.IS_ERROR
					}
				} else {
					'ns0:CSUploadErrorKey' {
						'ns0:Type' appErrorReportError?.TYPE
						'ns0:Value' appErrorReportError?.VALUE
						'ns0:IsError' appErrorReportError?.IS_ERROR
					}
				}
//				csuploadErrorKey.each { currentErrorKey ->
//					'ns0:CSUploadErrorKey' {
//						'ns0:Type' currentErrorKey?.TYPE
//						'ns0:Value' currentErrorKey?.VALUE
//						'ns0:IsError' currentErrorKey?.IS_ERROR
//					}
//				}
			}
			'ns0:CSUploadInfoKey' {
				'ns0:Type' 'SHMTQUEUEID'
				'ns0:Value' Interchange
			}
			'ns0:CSUploadInfoKey' {
				'ns0:Type' 'ACTIVITYSTARTDT'
				'ns0:Value' currentSystemDt.format("yyyyMMddHHmmss")
			}
		}

	}

	/**
	 * @param bizKeyXml
	 * @param current_Body
	 * @param current_BodyIndex
	 * @param errorKeyList
	 * @param Interchange
	 * @param eventExtCde
	 * @param TP_ID
	 * @param conn
	 * @return MarkupBuilder
	 */
	public void buildBizKey(MarkupBuilder bizKeyXml, cs.b2b.core.mapping.bean.ct.Body current_Body, int current_BodyIndex,
							def errorKeyList, def headerMsgDT, def eventExtCde, String TP_ID, Connection conn) {

		def scacTpId = getSCACTpId(current_Body?.GeneralInformation.SCAC, conn)

		def appErrorReportError = errorKeyList.find { it?.IS_ERROR == YES }
		def appErrorReportObsolete = errorKeyList.find { it?.IS_ERROR == NO }
		def blBizKey = []
		def bkBizKey = []
		def crnBizKey = []
		def eventExtCdeBizKey = []
		def eventDateTimeBizKey = []
		def eventPortIdBizKey = []
		def eventPortNameBizKey = []
		def evnetCityIdBizKey = []
		def eventCityNameBizKey = []
		def evnetCntryCdeBizKey = []
		def partyType = ['ANP', 'BPT', 'CCP', 'CGN', 'FWD', 'NPT', 'OTH', 'SHP']
		def externalReferenceType =  ['SO','SR','VT','SID','INV','PRN']
		def updatePartyBizkey=[]
		def externalReferenceBizkey = []
        def lkpBizkey =null

		Map<String,String> blMap= new HashMap<String,String>()
		current_Body.BLGeneralInfo.each { current_BLGeneralInfo ->
			if (StringUtil.isNotEmpty(current_BLGeneralInfo.BLNumber)&& !blMap.get(current_BLGeneralInfo?.BLNumber)) {
				blBizKey.add(['BL_NUM': current_BLGeneralInfo.BLNumber])
				blMap.put(current_BLGeneralInfo?.BLNumber,"BL_NUM")
			}
		}
		Map<String,String> bkMap= new HashMap<String,String>()
		current_Body.BookingGeneralInfo.each { current_BookingGeneralInfo ->
			if (StringUtil.isNotEmpty(current_BookingGeneralInfo.CarrierBookingNumber)&& !bkMap.get(current_BookingGeneralInfo?.CarrierBookingNumber)) {
				bkBizKey.add(['BK_NUM': current_BookingGeneralInfo.CarrierBookingNumber])
				bkMap.put(current_BookingGeneralInfo?.CarrierBookingNumber,"BK_NUM")
			}
		}
		Map<String,String> extRefMap= new HashMap<String,String>()
		current_Body.ExternalReference.findAll {it?.CSReferenceType in externalReferenceType}.each { current_extReference ->
			if (StringUtil.isNotEmpty(current_extReference?.ReferenceNumber) && !extRefMap.get(current_extReference?.ReferenceNumber)) {
				Map<String,String> tmpExtRef=[:]
				tmpExtRef.put(current_extReference.CSReferenceType, current_extReference?.ReferenceNumber)
				externalReferenceBizkey.add(tmpExtRef)
				extRefMap.put(current_extReference?.ReferenceNumber,current_extReference.CSReferenceType )
			}
		}
		Map<String,String> crnMap= new HashMap<String,String>()
		current_Body.Party.each { current_Party->
			if (StringUtil.isNotEmpty(current_Party?.CarrierCustomerCode)  && partyType.contains(current_Party?.PartyType) && !crnMap.get(util.substring(current_Party?.CarrierCustomerCode,1,10))) {
				crnBizKey.add(['CRN': util.substring(current_Party?.CarrierCustomerCode,1,10)])
				crnMap.put(util.substring(current_Party?.CarrierCustomerCode,1,10),"CRN")
			}
		}
		if (StringUtil.isNotEmpty(eventExtCde)) {
			eventExtCdeBizKey.add(['CT_Event_ext_cde': eventExtCde])
		}
		def eventInfo = getCS2EventInfo(current_Body?.Event?.Location?.CityDetails?.City, current_Body?.Event?.Location?.LocationCode?.UNLocationCode, current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode, conn)
		if (StringUtil.isNotEmpty(current_Body?.Event?.EventDT?.LocDT?.toString())) {
			eventDateTimeBizKey = ['CT_Event_datetime': util.convertDateTime(current_Body?.Event?.EventDT?.LocDT?.toString(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyy-MM-dd HH:mm:ss')]
		}else if(StringUtil.isNotEmpty(current_Body?.Event?.EventDT?.GMT?.toString())){
            eventDateTimeBizKey = ['CT_Event_datetime': util.convertDateTime(current_Body?.Event?.EventDT?.GMT?.toString(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyy-MM-dd HH:mm:ss')]
        }
		if (StringUtil.isNotEmpty(eventInfo.CT_Event_port_id)) {
			eventPortIdBizKey = ['CT_Event_port_id': eventInfo.CT_Event_port_id]
		}
		if (StringUtil.isNotEmpty(eventInfo.CT_Event_port_nme)) {
			eventPortNameBizKey = ['CT_Event_port_nme': eventInfo.CT_Event_port_nme]
		}
		if (StringUtil.isNotEmpty(eventInfo.CT_Event_city_id)) {
			evnetCityIdBizKey = ['CT_Event_city_id': eventInfo.CT_Event_city_id]
		}
		if (StringUtil.isNotEmpty(eventInfo.CT_Event_city_nme)) {
			eventCityNameBizKey = ['CT_Event_city_nme': eventInfo.CT_Event_city_nme]
		}
		if (StringUtil.isNotEmpty(eventInfo.CT_Event_cntry_cde)) {
			evnetCntryCdeBizKey = ['CT_Event_cntry_cde': eventInfo.CT_Event_cntry_cde]
		}

		if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.UpdateParty)){
			updatePartyBizkey = ['UPDATE_PARTY':current_Body?.GeneralInformation?.UpdateParty]
		}

        if(StringUtil.isNotEmpty(current_Body?.Event?.EventDT?.LocDT?.toString())){
            lkpBizkey = current_Body?.GeneralInformation?.SCAC+ ',' + util.substring(current_Body?.Container?.ContainerNumber, 1, 10) + ',' + (current_Body?.Event?.CS1Event ? current_Body?.Event?.CS1Event : "")+ ',' + util.convertXmlDateTime(current_Body?.Event?.EventDT?.LocDT?.toString(), 'yyyyMMddHHmmss')
        }else if(StringUtil.isNotEmpty(current_Body?.Event?.EventDT?.GMT?.toString())){
            lkpBizkey = current_Body?.GeneralInformation?.SCAC+ ',' + util.substring(current_Body?.Container?.ContainerNumber, 1, 10) + ',' + (current_Body?.Event?.CS1Event ? current_Body?.Event?.CS1Event : "")+ ',' +  util.convertXmlDateTime(current_Body?.Event?.EventDT?.GMT?.toString(), 'yyyyMMddHHmmss')
        }

		def bizKey = [
				['CNTR_NUM': util.substring(current_Body?.Container?.ContainerNumber, 1, 10)],
				['STPID': scacTpId], ['RTPID': TP_ID],
				['LKP': lkpBizkey],
		]
		bizKey.addAll(blBizKey)
		bizKey.addAll(bkBizKey)
		bizKey.addAll(externalReferenceBizkey)
		bizKey.addAll(crnBizKey)
		bizKey.addAll(eventExtCdeBizKey)
		bizKey.addAll(eventDateTimeBizKey)
		bizKey.addAll(eventPortIdBizKey)
		bizKey.addAll(eventPortNameBizKey)
		bizKey.addAll(evnetCityIdBizKey)
		bizKey.addAll(eventCityNameBizKey)
		bizKey.addAll(evnetCntryCdeBizKey)
		bizKey.addAll(updatePartyBizkey)



		bizKeyXml.'ns0:Transaction'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
			'ns0:ControlNumberInfo' {
				'ns0:Interchange' headerMsgDT
				'ns0:Group' String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0");
				'ns0:Transaction' current_BodyIndex + 1
			}



			bizKey.each { currentBizKeyMap ->
				currentBizKeyMap.each { key, value ->
					'ns0:BizKey' {
						'ns0:Type' key
						'ns0:Value' value
					}
				}
			}
			'ns0:CarrierId' getCarrierID(current_Body?.GeneralInformation?.SCAC, conn)
			'ns0:CTEventTypeId' util.substring(current_Body?.Event?.CS1Event, 1, 5)

			if (errorKeyList.size != 0) {
				'ns1:AppErrorReport'('xmlns:ns1': 'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
					if (appErrorReportObsolete != null) {
						'ns1:Status' OBSOLETE
					} else {
						'ns1:Status' ERROR
					}
					'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'    //max length is 20, exceed will missing error bizkey
					if (appErrorReportObsolete != null) {
						'ns1:Msg' appErrorReportObsolete?.VALUE
					} else {
						'ns1:Msg' appErrorReportError?.VALUE
					}
					'ns1:Severity' '5'
				}
			}
		}
	}
//bizkey for UIF OLL-EDICARR
	public void buildBizKeyforUIF(MarkupBuilder bizKeyXml, cs.b2b.core.mapping.bean.ct.Body current_Body, int current_BodyIndex,cs.b2b.core.mapping.bean.ct.Header header, def errorKeyList, def headerMsgDT, String TP_ID, Connection conn) {


		def appErrorReportError = errorKeyList.find { it?.IS_ERROR == YES }
		def appErrorReportObsolete = errorKeyList.find { it?.IS_ERROR == NO }

		def sender_id = header.SenderID ? header.SenderID : ''
		def recevier_id = null
		if(TP_ID=='OLL-EDICARR'){
			recevier_id = 'OLL-EDICARR'
		}else if(header.ReceiverID){
			recevier_id = header.ReceiverID
		}


		def cs_event_code = null
		if(TP_ID=='OLL-EDICARR'){
			cs_event_code = getCSEventCodebyExtCde(current_Body?.Event?.CarrEventCode,conn)
		}else if(StringUtil.isNotEmpty(current_Body?.Event?.CarrEventCode) && TP_ID!="OLL-EDICARR"){
			cs_event_code = current_Body?.Event?.CarrEventCode
		}


		def blBizKey = []
		def bkBizKey = []
		def crnBizKey = []


		Map<String,String> blMap= new HashMap<String,String>()
		current_Body.BLGeneralInfo.each { current_BLGeneralInfo ->
			if (StringUtil.isNotEmpty(current_BLGeneralInfo.BLNumber)&& !blMap.get(current_BLGeneralInfo?.BLNumber)) {
				blBizKey.add(['BL_NUM': current_BLGeneralInfo.BLNumber])
				blMap.put(current_BLGeneralInfo?.BLNumber,"BL_NUM")
			}
		}
		Map<String,String> bkMap= new HashMap<String,String>()
		current_Body.BookingGeneralInfo.each { current_BookingGeneralInfo ->
			if (StringUtil.isNotEmpty(current_BookingGeneralInfo.CarrierBookingNumber)&& !bkMap.get(current_BookingGeneralInfo?.CarrierBookingNumber)) {
				bkBizKey.add(['BK_NUM': current_BookingGeneralInfo.CarrierBookingNumber])
				bkMap.put(current_BookingGeneralInfo?.CarrierBookingNumber,"BK_NUM")
			}
		}
		Map<String,String> crnMap= new HashMap<String,String>()
		current_Body.Party.each { current_Party->
			if (StringUtil.isNotEmpty(current_Party?.CarrierCustomerCode) && !crnMap.get(current_Party?.CarrierCustomerCode)) {
				crnBizKey.add(['CRN': current_Party?.CarrierCustomerCode])
				crnMap.put(current_Party?.CarrierCustomerCode,"CRN")
			}
		}


		def containerinfo =null
		if(StringUtil.isNotEmpty(current_Body?.Container?.ContainerNumber)&& !StringUtil.isNotEmpty(current_Body?.Container?.ContainerCheckDigit)) {
			containerinfo = (current_Body?.Container?.ContainerNumber ? current_Body?.Container?.ContainerNumber : '') +(current_Body?.Container?.ContainerCheckDigit ? current_Body?.Container?.ContainerCheckDigit : '')
		}

		def bizKey = [
				['CNTR_NUM': containerinfo],
				['STPID': sender_id], ['RTPID': recevier_id],
				['LKP': current_Body?.TransactionInformation?.MessageID]
		]
		bizKey.addAll(blBizKey)
		bizKey.addAll(bkBizKey)
		bizKey.addAll(crnBizKey)




		bizKeyXml.'ns0:Transaction'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
			'ns0:ControlNumberInfo' {
				'ns0:Interchange' headerMsgDT
				'ns0:Group' String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0");
				'ns0:Transaction' current_BodyIndex + 1
			}



			bizKey.each { currentBizKeyMap ->
				currentBizKeyMap.each { key, value ->
					'ns0:BizKey' {
						'ns0:Type' key
						'ns0:Value' value
					}
				}
			}
			'ns0:CarrierId' getCarrierID(current_Body?.GeneralInformation?.SCAC, conn)
			'ns0:CTEventTypeId' cs_event_code

			if (errorKeyList.size != 0) {
				'ns1:AppErrorReport'('xmlns:ns1': 'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
					if (appErrorReportObsolete != null) {
						'ns1:Status' OBSOLETE
					} else {
						'ns1:Status' ERROR
					}
					'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'    //max length is 20, exceed will missing error bizkey
					if (appErrorReportObsolete != null) {
						'ns1:Msg' appErrorReportObsolete?.VALUE
					} else {
						'ns1:Msg' appErrorReportError?.VALUE
					}
					'ns1:Severity' '5'
				}
			}
		}
	}


	public void promoteCSUploadToSession(String appSessionId, StringWriter csuploadWriter) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_CSUPLOAD', csuploadWriter?.toString())
	}

	public void promoteBizKeyToSession(String appSessionId, StringWriter bizKeyWriter) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizKeyWriter?.toString())
	}

	public void promoteAssoInterchangeMessageIDToSession(String appSessionId, String InterchangeMessageID) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', InterchangeMessageID)
	}

	public void promoteAssoCarrierSCACToSession(String appSessionId, String CarrierSCAC) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', CarrierSCAC)
	}
	public void promoteOutputFileNameToSession(String appSessionId, String outputFileName) {
		if (util.isNotEmpty(outputFileName)) {
			cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_TransportOutputFileName', outputFileName)
		}
	}

	public String emptyToString(String in_string) {
		if (in_string != null && in_string.trim().length() != 0) {
			return in_string;
		} else {
			return '';
		}
	}


	// special handing for replace char ,GTN
	public  String ReplaceSpecialChar(String source){
		source = source.replaceAll("[#\$%&'()*,-./<>@\\\\^`~]*",'')
		return source
	}

	/**
	 * @param eventCS2Cde
	 * @param eventExtCde
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 */
	void missingEventStatusCode(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(eventExtCde)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Status Code']
			errorKeyList.add(errorKey)
		}
	}

	void missingEventStatusDate(String eventCS2Cde, cs.b2b.core.mapping.bean.LocDT LocDT, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(LocDT?.toString()) || LocDT?.toString().trim() == '0') {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Status Event Date']
			errorKeyList.add(errorKey)
		}
	}

	void missingContainerNumber(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (container.findAll { StringUtil.isNotEmpty(it?.ContainerNumber) }.size == 0) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Container Number']
			errorKeyList.add(errorKey);
		}
	}

	void filterEventStatusCode(String eventCS2Cde, String notSubscribedCode, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (eventCS2Cde.equals(notSubscribedCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Event not subscribed by Customer']
			errorKeyList.add(errorKey);
		}
	}

	void missingBlNumber(String eventCS2Cde, List<cs.b2b.core.mapping.bean.ct.BLGeneralInfo> blGeneralInfo, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (blGeneralInfo.findAll { StringUtil.isNotEmpty(it?.BLNumber) }.size == 0) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing BL Number']
			errorKeyList.add(errorKey);
		}
	}
	/** TP_ID :WGLL
	 * check BLNumber by each BLGeneralInfo*/
	void missingBLNumberbutexistsBLGeninfo(String eventCS2Cde, BLGeneralInfo blGeneralInfo, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (util.isEmpty(blGeneralInfo?.BLNumber)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing BL Number']
			errorKeyList.add(errorKey);
		}
	}

	void missingConsigneeCode(String eventCS2Cde, List<cs.b2b.core.mapping.bean.ct.Party> party, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (party.findAll {it.PartyType == 'CGN' && StringUtil.isNotEmpty(it.CarrierCustomerCode)}.size == 0) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Consignee Code']
			errorKeyList.add(errorKey);
		}
	}

	void filterIBIntermodal(String eventCS2Cde, String filterCode, String filterIBIntermodalIndicator, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (eventCS2Cde.equals(filterCode) && '1'.equals(filterIBIntermodalIndicator)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Ignore (I/B intermodal)']
			errorKeyList.add(errorKey);
		}
	}

	void missingEventCodeLoc(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(Event.Location?.LocationName) && StringUtil.isEmpty(Event.Location?.CityDetails?.City) && eventCS2Cde != 'CS180' && StringUtil.isEmpty(Event.Location?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(Event.Location?.LocationCode?.SchedKDCode) && StringUtil.isEmpty(Event.Location?.LocationCode?.SchedKDType)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Code and Location']
			errorKeyList.add(errorKey)
		}
	}

	void missingEventFNDCodeLoc(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(Event.Location?.LocationName) && StringUtil.isEmpty(Event.Location?.CityDetails?.City) && eventCS2Cde == 'CS180' && StringUtil.isEmpty(Event.Location?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(Event.Location?.LocationCode?.SchedKDCode) && StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDCode) && StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDType) && StringUtil.isEmpty(FND?.CityDetails?.City)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event and FND Location Code and Name']
			errorKeyList.add(errorKey)
		}
	}

	void missingPODCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(LastPOD?.Port?.CSCountryCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Country Code']
			errorKeyList.add(errorKey)
		}
	}

	void missingPODLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Location Code for']
			errorKeyList.add(errorKey)
		}
	}

	void missingPODLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(LastPOD?.Port?.PortName)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' -Missing POD Location Name']
			errorKeyList.add(errorKey)
		}
	}

	void missingPODLocationQual(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.UNLocationCode) && StringUtil.isNotEmpty(LastPOD?.Port?.LocationCode?.SchedKDCode) && StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.SchedKDType)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POD Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}

	void missingPOLCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(FirstPOL?.Port?.CSCountryCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Country Code']
			errorKeyList.add(errorKey)
		}
	}

	void missingPOLLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Code']
			errorKeyList.add(errorKey)
		}
	}

	void missingPOLLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(FirstPOL?.Port?.PortName)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Name']
			errorKeyList.add(errorKey)
		}
	}

	void missingPOLLocationQual(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.UNLocationCode) && StringUtil.isNotEmpty(FirstPOL?.Port?.LocationCode?.SchedKDCode) && StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.SchedKDType)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POL Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}

	void missingPORCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, String flag, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(POR?.CSStandardCity?.CSCountryCode) && StringUtil.isEmpty(flag)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Country Code']
			errorKeyList.add(errorKey)
		}
	}

	void missingPORLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(POR?.CityDetails?.City)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Name']
			errorKeyList.add(errorKey)
		}
	}

	void missingPORLocationQual(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.UNLocationCode) && StringUtil.isNotEmpty(POR?.CityDetails?.LocationCode?.SchedKDCode) && StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.SchedKDType)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POR Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}

	void EventNotSub(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(eventExtCde) || eventExtCde == "XX") {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - EVT_NOT_SUB']
			errorKeyList.add(errorKey)
		}
	}

	void missingEventLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(Event.Location?.LocationName) && StringUtil.isEmpty(Event.Location?.CityDetails?.City) && StringUtil.isEmpty(Event.Location?.LocationCode?.UNLocationCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing Event Location Code']
			errorKeyList.add(errorKey)
		}
	}

	void missingBookingNumber(String eventCS2Cde, List<cs.b2b.core.mapping.bean.ct.BookingGeneralInfo> BookingGeneralInfo, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(BookingGeneralInfo[0]?.CarrierBookingNumber)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing Booking Number']
			errorKeyList.add(errorKey)
		}
	}

	void missingEquipmentNumber(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (util.isEmpty(container?.ContainerNumber) || container?.ContainerNumber?.size() < 10) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Equipment Number']
			errorKeyList.add(errorKey)
		}
	}

	void missingCustomerCode(String eventCS2Cde, String CustomerCode, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (CustomerCode.equals('no')) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Customer Code']
			errorKeyList.add(errorKey)
		}
	}

	void EventIsNotSubscribedByParnter(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (eventExtCde.equals('XX')) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Event is not subscribed by Parnter ! ']
			errorKeyList.add(errorKey)
		}
	}

	void EventCodeCSInternalValueIsEmpty(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(eventCS2Cde)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventExtCde + errorMsg : eventCS2Cde + '  - Missing Status Code']
			errorKeyList.add(errorKey)
		}
	}

	void FailToConvertContainerSizeType(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? errorMsg + container?.CarrCntrSizeType : 'Fail to convert container size type=' + container?.CarrCntrSizeType]
		errorKeyList.add(errorKey)
	}

	void eventnotsubcribed(String eventCS2Cde, String ext_cde, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isNotEmpty(ext_cde) && ext_cde == 'XX') {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Event not subscribed by Partner']
			errorKeyList.add(errorKey)
		}
	}

	void missingEquipmentNumberWithOutLengthChecking(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (util.isEmpty(container.ContainerNumber)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Equipment Number']
			errorKeyList.add(errorKey)
		}
	}

	void missingBLNumberWithEventCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.BLGeneralInfo blgeneralinfo, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (util.isEmpty(blgeneralinfo?.BLNumber) && (['CS040'].contains(eventCS2Cde) || ['CS310'].contains(eventCS2Cde))) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing BL Number for' + eventCS2Cde]
			errorKeyList.add(errorKey)

		}

	}

	void failcontainersizetype(cs.b2b.core.mapping.bean.ct.Container container, String eventCS2Cde, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null

		errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' -Fail to convert container size type= ' + container?.CarrCntrSizeType]
		errorKeyList.add(errorKey)
	}

	void verifyTransportMode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.GeneralInformation generalInformation, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (generalInformation?.TransportMode?.trim()?.toUpperCase() != 'TRUCK') {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - TransportMode!=Truck']
			errorKeyList.add(errorKey)
		}
	}

	void missingEventUNLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (util.isEmpty(Event?.Location?.LocationCode?.UNLocationCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event UNLocationCode']
			errorKeyList.add(errorKey)
		}
	}

	void filterEventCodeNotinTruck(String eventCS2Cde, String filterCode, String TransportMode, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (eventCS2Cde.equals(filterCode) && TransportMode?.toUpperCase() != 'TRUCK') {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '  not in transport mode (TRUCK).']
			errorKeyList.add(errorKey)
		}
	}
	void missingEventStatusLocation(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null

		if (StringUtil.isEmpty(Event?.Location?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(Event?.Location?.LocationCode?.SchedKDCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Status Location for']
			errorKeyList.add(errorKey)
		}
	}

	void filterInboundintermodalindicatorByEvent(String eventCS2Cde, Body body, String filterIBIntermodal, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (eventCS2Cde.equals(body?.Event?.CS1Event?.trim()) && body?.Route?.Inbound_intermodal_indicator?.equals(filterIBIntermodal)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- Filter CSEvent because contains I/B intermodal.']
			errorKeyList.add(errorKey)
		}
	}

	void filterPartySentToCustomerByEvent(String eventCS2Cde,List<String> filterEventCode, List<Party> partyList, List<String> filterPartyName, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		def flag=false
		partyList?.each {current_Party ->
			if(filterPartyName.contains(current_Party?.PartyName?.toUpperCase()) && !current_Party?.PartyLevel){
				flag=true
			}
		}
		if (filterEventCode.contains(eventCS2Cde) && flag) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- Event not for customer']
			errorKeyList.add(errorKey)
		}
	}

	void filterContainerNumberMaxLength(String eventCS2Cde, Container container, int filterMaxLength, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (container?.ContainerNumber?.trim()?.length()>filterMaxLength) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- container number longer the max length'+filterMaxLength]
			errorKeyList.add(errorKey)
		}
	}

	//this function will never be used by 4010-RM GTN since 1.0 Exception checking bug, following MissingCarrierCustomerCodeWithPartyWithoutLevel is another function for 4010-CS APP Tp CARGOMSVCGTN
	void MissingCarrierCustomerCodeByPartyLevel(String eventCS2Cde, List<Party> party, String filterPartyLevel, List<String> filterPartyType, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		def flag=false
		def errorPartyType=""
		party?.findAll{!it?.PartyLevel || it?.PartyLevel?.trim()==filterPartyLevel}?.each {partyType ->
			if(filterPartyType.contains(partyType)){
				flag=true
				errorPartyType=partyType
			}
		}
		if (flag) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing customer code / party is'+errorPartyType]
			errorKeyList.add(errorKey)
		}
	}

	void MissingCarrierCustomerCodeWithoutPartyLevel(String eventCS2Cde, List<Party> party, String filterPartyLevel, List<String> filterPartyType, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		def flag=false
		def errorPartyType=""
		party?.findAll{it?.PartyLevel?.trim()==filterPartyLevel}?.each {partyType ->
			if(filterPartyType.contains(partyType?.PartyType)){
				flag=true
				errorPartyType=partyType?.PartyType
			}
		}
		if (flag) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing customer code / party is'+errorPartyType]
			errorKeyList.add(errorKey)
		}
	}

	void missingAllR4DTM(String eventCS2Cde, cs.b2b.core.mapping.bean.LocDT LocDT, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(LocDT?.toString()) || LocDT?.toString().trim() == '0') {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Status Event Date']
			errorKeyList.add(errorKey)
		}
	}

	void filterContainerNumberMinLengthForB40708Pair(String eventCS2Cde, Container container, int filterMinLength, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (container?.ContainerNumber?.trim()?.length()<filterMinLength) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '- B407/B408 not coexist.']
			errorKeyList.add(errorKey)
		}
	}
	void missingFNDCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(FND?.CSStandardCity?.CSCountryCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing FND Country Code']
			errorKeyList.add(errorKey)
		}
	}

	void missingEventLocationNameandCity(String eventCS2Cde, Event event, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(event?.Location?.LocationName) && StringUtil.isEmpty(event?.Location?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing event in both name and city.']
			errorKeyList.add(errorKey)
		}
	}

	void missingLocationCodeUNandKD(String eventCS2Cde,String UNLocationCode,String SchedKDCode,boolean isError,String errorMsg, List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null

		if(StringUtil.isEmpty(UNLocationCode) && StringUtil.isEmpty(SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing location in both UN and KD.']
			errorKeyList.add(errorKey)
		}
	}

	void missingCityDetailsCity(String eventCS2Cde,String city,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(city)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing City.']
			errorKeyList.add(errorKey)
		}
	}
	void missingEventCountryCode(String eventCS2Cde,Event event,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(event?.Location?.CSStandardCity?.CSCountryCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing event country code.']
			errorKeyList.add(errorKey)
		}
	}

	void filterNon1sstPolIngate(String eventCS2Cde,String filterEvent,Event event,cs.b2b.core.mapping.bean.ct.FirstPOL firstPOL,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(eventCS2Cde == filterEvent){
			if(event?.Location?.LocationCode?.UNLocationCode!= firstPOL?.Port?.LocationCode?.UNLocationCode){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Ingate Event not at POL.']
				errorKeyList.add(errorKey)

			}
		}
	}
	//tp: INFODISBVFNL1014430
	void missingEventLocation(String eventCS2Cde, Event event, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(event?.Location?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(event?.Location?.CityDetails?.City) ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Location']
			errorKeyList.add(errorKey)
		}
	}
	void missingFNDLocation(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.FND  fnd, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(fnd?.CityDetails?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(fnd?.CityDetails?.City) && StringUtil.isEmpty(fnd?.CSStandardCity?.CSCountryCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing FND UN Location Code for ']
			errorKeyList.add(errorKey)
		}
	}
	void missingPODLocation(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.LastPOD lastPOD,cs.b2b.core.mapping.bean.ct.OceanLeg oceanLeg, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(oceanLeg?.POD?.Port?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(lastPOD?.Port?.LocationCode?.UNLocationCode) && (StringUtil.isEmpty(lastPOD?.Port?.City)||StringUtil.isEmpty(oceanLeg?.POD?.Port?.PortCode))){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POD UN Location Code for ']
			errorKeyList.add(errorKey)
		}
	}


	void missingSCAC(String eventCS2Cde,Body current_Body,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(current_Body?.GeneralInformation?.SCAC)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing scac code']
			errorKeyList.add(errorKey)
		}
	}


	//missing legal party
	/**
	 * 	 reference CT_O_WGLL
	 * checking the party type in body is expected
	 * #1 provided the expected  list
	 * #2 get currentbody/Paryt/PartyType  and  intersect with expected list
	 * if result >1 , then exists  legal partytype
	 * def partyTypeMap = ['ANP', 'BPT', 'CGN', 'FWD', 'NPT', 'OTH', 'SHP', 'UK']
	 * List legalPartyType = current_Body?.Party?.PartyType?.intersect(partyTypeMap)
	 * */
	void missingLegalParty(String eventCS2Cde,List legalPartyType,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(legalPartyType.size()<1){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - No Valid LEGALPARTIES exists.']
			errorKeyList.add(errorKey)
		}
	}
	//doesn't test, if need, please do the unit test for it
	void missingCargo(String eventCS2Cde, Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body?.Cargo?.size()<1){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing cargo infromation.']
			errorKeyList.add(errorKey)
		}
	}
	//doesn't test, if need, please do the unit test for it
	void missingMileStonesEventCode(List mileStonesInvalidEvent, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(mileStonesInvalidEvent?.size()<1){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? '' + errorMsg : '' + ' - Cannot convert milestone event.']
			errorKeyList.add(errorKey)
		}
	}

	void missingEventDate(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.EventDT eventDT, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(eventDT?.LocDT?.LocDT) && StringUtil.isEmpty(eventDT?.GMT)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing event data time.']
			errorKeyList.add(errorKey)
		}
	}
     //TP ID : DELMONTE
	void filterPODbyCountryCode(String eventCS2Cde,List<String> filterCode,cs.b2b.core.mapping.bean.ct.Route route, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (route?.LastPOD?.Port?.CSCountryCode?.toUpperCase() in filterCode) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Last Pod Country must need to US.']
			errorKeyList.add(errorKey)
		}
	}
	//doesn't test, if tested, please add tp id in here
	void filterFNDbyCountryCode(String eventCS2Cde,List<String> filterCode,cs.b2b.core.mapping.bean.ct.Route route, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (route?.FND?.CSStandardCity?.CSCountryCode?.toUpperCase() in filterCode) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - FND Country must need to US.']
			errorKeyList.add(errorKey)
		}
	}

	//doesn't test, if need, please do the unit test for it
	void filterPODNonUsInbound(String eventCS2Cde,List<String> filterCode,cs.b2b.core.mapping.bean.ct.Route route, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (!(route?.LastPOD?.Port?.CSCountryCode?.toUpperCase() in filterCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Non US inbound (POD).']
			errorKeyList.add(errorKey)
		}
	}
// tp id :MONSTERCABLE
	void filterEventNotSentToCustomer(String eventCS2Cde,List<String> filterCustomerName,cs.b2b.core.mapping.bean.ct.Body body, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		def countParty = body?.Party?.findAll {util.isEmpty(it?.PartyLevel)}?.size()   //countParty=0, then no need to check it.

		if(countParty!=0 && body?.Party?.findAll {util.isEmpty(it?.PartyLevel) && !(filterCustomerName.contains(it?.PartyName))}?.size()==countParty){
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + '-Event not for customer '+filterCustomerName]
			errorKeyList.add(errorKey)
		}
	}
	//doesn't test, if need, please do the unit test for it
	void filterEventbyPODCountryCode(String eventCS2Cde,List<String> filterEvent,List<String> filterCountryCode,cs.b2b.core.mapping.bean.ct.Route route, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if((eventCS2Cde in filterEvent) && (route?.LastPOD?.Port?.CSCountryCode?.toUpperCase() in filterCountryCode)){
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Obsolete due to CSCountryCode.']
			errorKeyList.add(errorKey)
		}
	}

    //TP_ID PANALPINA
	void checkEventTriggeredTD(String eventCS2Cde,List<String> afterATDEvent,List<String> beforeATDorETDEvent,cs.b2b.core.mapping.bean.ct.Route route,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(route?.OceanLeg[0]?.POL?.DepartureDT?.find{it?.attr_Indicator =='A'}?.LocDT && (eventCS2Cde in afterATDEvent)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '- ETD change after Vessel Departure not needed.']
			errorKeyList.add(errorKey)
		}else if((eventCS2Cde in beforeATDorETDEvent) && util.isEmpty(route?.OceanLeg[0]?.POL?.DepartureDT?.find{it?.attr_Indicator =='A'}?.LocDT)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - ETA change before Vessel Departure not needed']
			errorKeyList.add(errorKey)
		}
	}

	//TP_ID PANALPINA please noted whether need to check is Inbound_intermodal_indicator empty or not
	void checkWithIBIntermodal(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.Route route,String filterCode,boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if(route?.Inbound_intermodal_indicator!=filterCode){
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Inbound_intermodal_indicator≠0']
			errorKeyList.add(errorKey)
		}
	}
	//TP_ID PANALPINA
	void checkWithIBHaulage(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.BookingGeneralInfo bookingGeneralInfo,String filterCode,boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if(bookingGeneralInfo?.Haulage?.InBound!=filterCode){
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - InBound Haulage≠C']
			errorKeyList.add(errorKey)
		}
	}
	//doesn't test, if need, please do the unit test for it
	void checkWithActivityLocation(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.FND fnd,String filterCode,boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if(fnd?.CSStandardCity?.CSCountryCode!=filterCode){
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Activity location ≠ US']
			errorKeyList.add(errorKey)
		}
	}

	//doesn't test, if need, please do the unit test for it
	void filterByCustomerNameforAPLL(String eventCS2Cde,String filterCustomerName,cs.b2b.core.mapping.bean.ct.Body body,boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null

		if(!body?.Party?.find {it?.PartyName?.toUpperCase()?.contains(filterCustomerName.toUpperCase())}){
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Non-APL shipment.']
			errorKeyList.add(errorKey)
		}
	}

	//doesn't test, if need, please do the unit test for it
	void filterCarrierCustomerCodeBackList(String eventCS2Cde,List<String> filterCarrCustomerCode,cs.b2b.core.mapping.bean.ct.Body body,boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if(body?.Party?.find {it?.CarrierCustomerCode in filterCarrCustomerCode}){
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - Filter transaction by CCC.']
			errorKeyList.add(errorKey)
		}
	}

	//doesn't test, if need, please do the unit test for it
	void filterCarrierCustomerCodeByPartyWithWhiteCCCList(String eventCS2Cde,List<String> whiteListforCarrCusCode,List<String> filterPartyType,List<String> filterCarrCusCode,cs.b2b.core.mapping.bean.ct.Body body,boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		boolean isException = false
		if(body?.Party?.find {it?.CarrierCustomerCode in whiteListforCarrCusCode}){
			isException = false
		}else if(body?.Party?.find{it?.PartyType in filterPartyType && it?.CarrierCustomerCode in filterCarrCusCode}){
			isException = false
		}else{
			isException = true
		}
		if(isException){
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - SAP ID is not allowed.']
			errorKeyList.add(errorKey)
		}

	}

	/**
	 *
	 * CT XML schema validation
	 * */

	void customerSchemaValidation(String outputXmlBody, String validateMsgSchemaType, List<Map<String,String>> errorKeyList ) {
		Map<String,String> errorKey = null
		// prepare schema validator
		cs.b2b.core.common.xmlvalidation.ValidateXML validator = new cs.b2b.core.common.xmlvalidation.ValidateXML()
		String validationResult = validator.xmlValidation(validateMsgSchemaType, outputXmlBody)
		if (validationResult.indexOf('Validation Failure.')>=0) {
			//println 'Validation schema failure:'
			//println '--> '+validationResult
			errorKey = [TYPE: ERROR_SUPPORT, IS_ERROR: YES, VALUE: validationResult]
			errorKeyList.add(errorKey)
			return
		}
	}


}
