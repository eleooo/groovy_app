package cs.b2b.core.mapping.util

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import java.util.List
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.Map
import cs.b2b.core.common.xmlvalidation.ValidateXML



class MappingUtil_BR_I_Common {
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
	
	private cs.b2b.core.mapping.util.MappingUtil util
	
	public MappingUtil_BR_I_Common(){
	}
	
	public MappingUtil_BR_I_Common(cs.b2b.core.mapping.util.MappingUtil util){
		this.util = util
	}
	
	
	
	public int getDBTimeOutInSeconds() {
		int DB_TIMEOUT_IN_SECCOND = 10
		return DB_TIMEOUT_IN_SECCOND
	}
	
	public void promoteBizKeyToSession(String appSessionId, StringWriter bizKeyWriter) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizKeyWriter?.toString())
	}
	
	private void buildBizKeyXML(MarkupBuilder bizKeyXml, def current_Body, def current_Bodyindex, def MSG_REQ_ID, def action, def errorKeyList, String TP_ID, String interchange, Connection conn) {
		
		def bookingRequest = new  XmlSlurper().parseText(current_Body)
		def bizKey = []
		// LKP
		if (util.notEmpty(bookingRequest.Request[current_Bodyindex].TransactionInformation.MessageID)){
			bizKey.add(["LKP" : bookingRequest.Request[current_Bodyindex].TransactionInformation.MessageID.text()])
		}
		
		// STPID
		bizKey.add(["STPID" : TP_ID])

		// RTPID
		if (util.notEmpty(bookingRequest.Request[current_Bodyindex].GeneralInformation.SCAC)){
			bizKey.add(["RTPID" : util.getCarrierTpId(TP_ID, 'BR', bookingRequest.Request[current_Bodyindex].GeneralInformation.SCAC.text(), conn)])
		}
/*			
		// Action
		if (util.notEmpty(action)){
			bizKey.add(["ACTION" : action])
		}
*/
		// Shippers, Consignee, (Also) Notify Party, Forwarder (Legal Parties)
		bookingRequest.Request[current_Bodyindex].Party.each{ currentParty->
			switch (currentParty.PartyType){
				case 'BPT':
					bizKey.add(["BOOKING_PARTY" : currentParty.PartyName])
					break
				case 'SHP':
					bizKey.add(["SHIPPER" : currentParty.PartyName])
					break
				case 'FWD':
					bizKey.add(["FORWARDER" : currentParty.PartyName])
					break
				case 'CGN':
					bizKey.add(["CONSIGNEE" : currentParty.PartyName])
					break
				case 'NPT':
					bizKey.add(["NOTIFY_PARTY" : currentParty.PartyName])
					break
				case 'CAR':
					bizKey.add(["CARRIER" : currentParty.PartyName])
					break
				case 'ANP':
					bizKey.add(["ANOTHER NOTIFY PARTY" : currentParty.PartyName])
					break
				}
		}
		// External Reference (Reference Number)
		bookingRequest.Request[current_Bodyindex].ExternalReference.each{ currentExternalReference->
			if (util.notEmpty(currentExternalReference.ReferenceNumber)){
				switch (currentExternalReference.CSReferenceType){
					case 'AR':
						bizKey.add(["AR" : currentExternalReference.ReferenceNumber])
						break
					case 'BKG':
						bizKey.add(["BK_NUM" : currentExternalReference.ReferenceNumber])
						break
					case 'BKG':
						bizKey.add(["BKG" : currentExternalReference.ReferenceNumber])
						break
					case 'BL':
						bizKey.add(["BLN" : currentExternalReference.ReferenceNumber])
						bizKey.add(["BL_NUM" : currentExternalReference.ReferenceNumber])
						break
					case 'BRF':
						bizKey.add(["BR" : currentExternalReference.ReferenceNumber])
						break
					case 'CBL':
						bizKey.add(["CBL" : currentExternalReference.ReferenceNumber])
						break
					case 'CGO':
						bizKey.add(["CG" : currentExternalReference.ReferenceNumber])
						break
					case 'CR':
						bizKey.add(["CRN" : currentExternalReference.ReferenceNumber])
						break
					case 'ECN':
						bizKey.add(["ECN" : currentExternalReference.ReferenceNumber])
						break
					case 'EX':
						bizKey.add(["EX" : currentExternalReference.ReferenceNumber])
						break
					case 'EXPR':
						bizKey.add(["EXPR" : currentExternalReference.ReferenceNumber])
						break
					case 'FM':
						bizKey.add(["FM" : currentExternalReference.ReferenceNumber])
						break
					case 'FR':
						bizKey.add(["FR" : currentExternalReference.ReferenceNumber])
						break
					case 'GRN':
						bizKey.add(["GN" : currentExternalReference.ReferenceNumber])
						break
					case 'INV':
						bizKey.add(["INV" : currentExternalReference.ReferenceNumber])
						break
					case 'LEC':
						bizKey.add(["LC" : currentExternalReference.ReferenceNumber])
						break
					case 'MB':
						bizKey.add(["BKG" : currentExternalReference.ReferenceNumber])
						bizKey.add(["BK_NUM" : currentExternalReference.ReferenceNumber])
						break
					case 'MVID':
						bizKey.add(["VT" : currentExternalReference.ReferenceNumber])
						break
					case 'PO':
						bizKey.add(["PRN" : currentExternalReference.ReferenceNumber])
						break
					case 'SC':
						bizKey.add(["CT" : currentExternalReference.ReferenceNumber])
						break
					case 'SID':
						bizKey.add(["SID" : currentExternalReference.ReferenceNumber])
						break
					case 'SO':
						bizKey.add(["SO" : currentExternalReference.ReferenceNumber])
						break
					case 'SR':
						bizKey.add(["SR" : currentExternalReference.ReferenceNumber])
						break
					case 'TARIF':
						bizKey.add(["TIN" : currentExternalReference.ReferenceNumber])
						break
					case 'TN':
						bizKey.add(["TN" : currentExternalReference.ReferenceNumber])
						break
					case 'UC':
						bizKey.add(["UC" : currentExternalReference.ReferenceNumber])
						break
				}
			}
		}
		
		// Carrier Rate Reference (Reference Number)
		if (util.notEmpty(bookingRequest.Request[current_Bodyindex].CarrierRate.CarrierRateNumber)){
			switch (bookingRequest.Request[current_Bodyindex].CarrierRate.CSCarrierRateType){
				case 'SC':
					bizKey.add(["CT" : bookingRequest.Request[current_Bodyindex].CarrierRate.CarrierRateNumber])
					break
				case 'SR':
					bizKey.add(["SR" : bookingRequest.Request[current_Bodyindex].CarrierRate.CarrierRateNumber])
					break
				case 'TARIF':
					bizKey.add(["TIN" : bookingRequest.Request[current_Bodyindex].CarrierRate.CarrierRateNumber])
					break
			}
		}
		
		def appErrorReportError = errorKeyList.find{it?.IS_ERROR == YES}
		def appErrorReportObsolete = errorKeyList.find{it?.IS_ERROR == NO}
		
		bizKeyXml.'ns0:Transaction'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
			'ns0:ControlNumberInfo' {
				'ns0:Interchange' interchange
				'ns0:Group' ''
				'ns0:Transaction' bookingRequest.Request[current_Bodyindex].TransactionInformation?.MessageID
			}
			
			bizKey.each{ currentKey ->
				currentKey.each { key, value->
					'ns0:BizKey' {
						'ns0:Type' key
						'ns0:Value' value
					}
				}
			}

			//'ns0:CarrierId'

			if (errorKeyList.size != 0) {
				'ns1:AppErrorReport'('xmlns:ns1':'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
					if (appErrorReportError != null) {
						'ns1:Status' ERROR
					} else {
						'ns1:Status' OBSOLETE
					}
					'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'	//max length is 20, exceed will missing error bizkey
					if (appErrorReportError != null) {
						'ns1:Msg' appErrorReportError?.VALUE
					} else {
						'ns1:Msg' appErrorReportObsolete?.VALUE
					}
					'ns1:Severity' '5'
				}
			}
		}
	}


	public List<Object> buildBizKey(MarkupBuilder bizKeyXml, def current_Body,int current_BodyIndex, def errorKeyList, String TP_ID, Connection conn) {
		def bizKey = []
		// LKP & L3P
		cs.b2b.core.mapping.util.MappingUtil commUtil = new cs.b2b.core.mapping.util.MappingUtil()
		
		if (commUtil.notEmpty(current_Body.TransactionInformation?.MessageID)){
			bizKey.add(["LKP" : current_Body.TransactionInformation?.MessageID])
		}
		
		// STPID
		bizKey.add(["STPID" : TP_ID])

		// RTPID
		def RTPID = commUtil.getCarrierTpId(TP_ID, 'BR', current_Body.GeneralInformation?.SCAC.text(), conn) 
		if (commUtil.notEmpty(RTPID)){
			bizKey.add(["RTPID" : RTPID])
		}
		
		// Action
		if (commUtil.notEmpty(current_Body.GeneralInformation?.ActionType)){
			bizKey.add(["ACTION" : current_Body.GeneralInformation?.ActionType])
		}
		
		current_Body.Party.each{ currentParty->
			if (commUtil.notEmpty(currentParty.PartyName)) {
				switch (currentParty.PartyType){
					case 'BPT':
						bizKey.add(["BOOKING_PARTY" : currentParty.PartyName])
						break
					case 'SHP':
						bizKey.add(["SHIPPER" : currentParty.PartyName])
						break
					case 'FWD':
						bizKey.add(["FORWARDER" : currentParty.PartyName])
						break
					case 'CGN':
						bizKey.add(["CONSIGNEE" : currentParty.PartyName])
						break
					case 'NPT':
						bizKey.add(["NOTIFY_PARTY" : currentParty.PartyName])
						break
					case 'CAR':
						bizKey.add(["CARRIER" : currentParty.PartyName])
						break
					case 'ANP':
						bizKey.add(["ANOTHER NOTIFY PARTY" : currentParty.PartyName])
						break
					}
			}
			
		}
		// External Reference (Reference Number)
		current_Body.ExternalReference.each{ currentExternalReference->
			if (commUtil.notEmpty(currentExternalReference.ReferenceNumber)){
				switch (currentExternalReference.CSReferenceType){
					case 'AR':
						bizKey.add(["AR" : currentExternalReference.ReferenceNumber])
						break
					case 'BKG':
						bizKey.add(["BK_NUM" : currentExternalReference.ReferenceNumber])
						break
					case 'BKG':
						bizKey.add(["BKG" : currentExternalReference.ReferenceNumber])
						break
					case 'BL':
						bizKey.add(["BLN" : currentExternalReference.ReferenceNumber])
						bizKey.add(["BL_NUM" : currentExternalReference.ReferenceNumber])
						break
					case 'BRF':
						bizKey.add(["BR" : currentExternalReference.ReferenceNumber])
						break
					case 'CBL':
						bizKey.add(["CBL" : currentExternalReference.ReferenceNumber])
						break
					case 'CGO':
						bizKey.add(["CG" : currentExternalReference.ReferenceNumber])
						break
					case 'CR':
						bizKey.add(["CRN" : currentExternalReference.ReferenceNumber])
						break
					case 'ECN':
						bizKey.add(["ECN" : currentExternalReference.ReferenceNumber])
						break
					case 'EX':
						bizKey.add(["EX" : currentExternalReference.ReferenceNumber])
						break
					case 'EXPR':
						bizKey.add(["EXPR" : currentExternalReference.ReferenceNumber])
						break
					case 'FM':
						bizKey.add(["FM" : currentExternalReference.ReferenceNumber])
						break
					case 'FR':
						bizKey.add(["FR" : currentExternalReference.ReferenceNumber])
						break
					case 'GRN':
						bizKey.add(["GN" : currentExternalReference.ReferenceNumber])
						break
					case 'INV':
						bizKey.add(["INV" : currentExternalReference.ReferenceNumber])
						break
					case 'LEC':
						bizKey.add(["LC" : currentExternalReference.ReferenceNumber])
						break
					case 'MB':
						bizKey.add(["BKG" : currentExternalReference.ReferenceNumber])
						bizKey.add(["BK_NUM" : currentExternalReference.ReferenceNumber])
						break
					case 'MVID':
						bizKey.add(["VT" : currentExternalReference.ReferenceNumber])
						break
					case 'PO':
						bizKey.add(["PRN" : currentExternalReference.ReferenceNumber])
						break
					case 'SC':
						bizKey.add(["CT" : currentExternalReference.ReferenceNumber])
						break
					case 'SID':
						bizKey.add(["SID" : currentExternalReference.ReferenceNumber])
						break
					case 'SO':
						bizKey.add(["SO" : currentExternalReference.ReferenceNumber])
						break
					case 'SR':
						bizKey.add(["SR" : currentExternalReference.ReferenceNumber])
						break
					case 'TARIF':
						bizKey.add(["TIN" : currentExternalReference.ReferenceNumber])
						break
					case 'TN':
						bizKey.add(["TN" : currentExternalReference.ReferenceNumber])
						break
					case 'UC':
						bizKey.add(["UC" : currentExternalReference.ReferenceNumber])
						break
				}
			}
		}
		
		// Carrier Rate Reference (Reference Number)
		if (commUtil.notEmpty(current_Body.CarrierRate.CarrierRateNumber)){
			switch (current_Body.CarrierRate.CSCarrierRateType){
				case 'SC':
					bizKey.add(["CT" : current_Body.CarrierRate.CarrierRateNumber])
					break
				case 'SR':
					bizKey.add(["SR" : current_Body.CarrierRate.CarrierRateNumber])
					break
				case 'TARIF':
					bizKey.add(["TIN" : current_Body.CarrierRate.CarrierRateNumber])
					break
			}
		}

		def addItems = []
		bizKey.each{ currentBizKeyMap ->
			if (!addItems.contains(currentBizKeyMap)){
				addItems.add(currentBizKeyMap)
			}
		}
		
		return addItems
	}
	public void generateHeader(MarkupBuilder outXml, def current_Body, def current_BodyIndex, def currentSystemDt, def dirId, def msgTypeId, def tpId, def msgReqId,def controlNum) {
		outXml.'ns0:Header'{
			'ns1:ControlNumber' controlNum //UNH14.E0062_01
			'ns1:MsgDT'{
				'ns1:GMT'
				'ns1:LocDT' ('TimeZone': 'HKT', 'CSTimeZone': 'HKT', currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss") + '+08:00')
			}
			'ns1:MsgDirection' dirId
			'ns1:MsgType' msgTypeId
			'ns1:SenderID' tpId
			'ns1:ReceiverID' 'CARGOSMART'
			'ns1:Action' 'NEW'
			'ns1:Version' '4.5'
			'ns1:InterchangeMessageID' msgReqId
			'ns1:DataSource' "B2B"
		} //End-Header
	}

	
	public boolean checkTpIntegrationAsso(String TP_ID, String MSG_TYPE, String SCAC, Connection conn) throws Exception {
		if (conn == null)
			return "";
	
		boolean ret = false;
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select count(1) from tp_integration_asso aa where aa.sender_tp_id=? and aa.message_type=? and aa.receiver_scac_code=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10);
			pre.setQueryTimeout(10);
			pre.setString(1, TP_ID);
			pre.setString(2, MSG_TYPE);
			pre.setString(3, SCAC);

			result = pre.executeQuery();
	
			if (result.next()) {
				ret = true;
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	public boolean checkSCAC(String SCAC, Connection conn) throws Exception {
		if (conn == null)
			return "";
	
		boolean ret = false;
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where convert_type_id='CS1CarrierID' and int_cde=? and dir_id='O'";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10);
			pre.setQueryTimeout(10);
			pre.setString(1, SCAC);
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = true;
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	public String getContainerType(String TP_ID, String SCAC, String ext_cde, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
		
		if (TP_ID==null || SCAC==null || ext_cde==null) {
			return ""
		}
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select int_cde from b2b_cde_conversion where convert_type_id='ContainerType' and msg_type_id='BR' and tp_id=? and SCAC_CDE=? and ext_cde=? and dir_id='I'";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10);
			pre.setQueryTimeout(10);
			pre.setString(1, TP_ID);
			pre.setString(2, SCAC);
			pre.setString(3, ext_cde);
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
	
	public String getPackageUnit(String TP_ID, String ext_cde, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
		
		if (TP_ID==null || ext_cde==null) {
			return ""
		}
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select int_cde from b2b_cde_conversion where convert_type_id='PackageUnit' and msg_type_id='BR' and tp_id=? and ext_cde=? and dir_id='I'";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10);
			pre.setQueryTimeout(10);
			pre.setString(1, TP_ID);
			pre.setString(2, ext_cde);
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

	public List postValidation(def input, def output, def txnErrorKeys) {
		List AppErrors = new ArrayList()
		
		def nodeBookingRequest = new XmlSlurper().parseText(output)
		
		// prepare schema validator
		cs.b2b.core.common.xmlvalidation.ValidateXML validator = new ValidateXML()
		// prepare Node version of ShippingInstruction
		Node msgWithHeader = nodeBookingRequest.clone()
		List kids= new LinkedList()
		//remove the <Body>
		msgWithHeader.children().eachWithIndex {kid , index->
			if (index !=0){
				kids.add(kid)
			}
		}
		kids.eachWithIndex {kid , index->
			msgWithHeader.remove(kid)
		}
		
		input.Request.eachWithIndex { current_Body, current_BodyIndex ->
			if (!(txnErrorKeys[current_BodyIndex])){
				// do Post-validation on Body (
				Node currentBody = nodeBookingRequest.children().get(current_BodyIndex+1)
				Node currentMsg = msgWithHeader.clone()
				// prepare a complete transaction with one header & one body
				currentMsg.append(currentBody)
				String validationResult = validator.xmlValidation('CS2-CUS-BRXML', XmlUtil.serialize(currentMsg))
				if (validationResult.contains('Validation Failure.')){
					AppErrors.add(validationResult)
				} 
			}
			// for empty Body i.e. pre-validation fails
			else AppErrors.add("")
		}
		return AppErrors
	}
	
	public String removeEmptyTag(String Input) {
		Node root = new XmlParser().parseText(Input);
		cleanNode(root);
		XmlUtil.serialize(root);
	}

	public boolean cleanNode(Node node) {
		node.attributes().with {
			a ->
				a.findAll { !it.value }.each {
					a.remove(it.key)
				}
		}
		node.children().with {
			kids ->
				kids.findAll { it instanceof Node ? !cleanNode(it) : false }.each {

					kids.remove(it)
				}
		}
		node.attributes() || node.children() || node.text()
	}
	
	public List<Map<String,String>> postValidation1(def output, def txnErrorKeys) {
		
		List<Map<String,String>> AppErrors = new ArrayList<Map<String,String>>()
		
		def content  = removeEmptyTag(output)
		def nodeBookingRequest = new XmlParser().parseText(content)
		//def nodeBookingRequest = new XmlSlurper().parseText(output)
		
		// prepare schema validator
		cs.b2b.core.common.xmlvalidation.ValidateXML validator = new ValidateXML()
		// prepare Node version of BR
		Node msgHeader = nodeBookingRequest.clone()
		List msgBodies= new LinkedList()
		//remove the <Body>
		msgHeader.children().eachWithIndex {kid , index->
			if (index !=0){
				msgBodies.add(kid)
			}
		}
		msgBodies.eachWithIndex {kid , index->
			msgHeader.remove(kid)
		}
		
		msgBodies.eachWithIndex { currentBody, current_BodyIndex ->
			if (!(txnErrorKeys[current_BodyIndex])){
				// do Post-validation on Body (
				//Node currentBody = nodeShippingInstruction.children().get(current_BodyIndex+1)
				Node currentMsg = msgHeader.clone()
				// prepare a complete transaction with one header & one body
				currentMsg.append(currentBody)

				String validationResult = validator.xmlValidation('CS2-CUS-BRXML', XmlUtil.serialize(currentMsg))
				if (validationResult.contains('Validation Failure.')){
					Map<String,String> errorKey = null
					errorKey = errorKey = [CAT: 'POSP', TYPE: 'E' , VALUE: validationResult]
					AppErrors.add(errorKey)
				}
			}
			// for empty Body i.e. pre-validation fails
			else AppErrors.add("")
		}
		return AppErrors
	}
}
