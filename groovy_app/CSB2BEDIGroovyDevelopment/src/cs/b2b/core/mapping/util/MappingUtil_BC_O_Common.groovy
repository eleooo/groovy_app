package cs.b2b.core.mapping.util

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bc.Party

/**
 * @author LIANGDA
 *
 */
class MappingUtil_BC_O_Common {

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
	
	public MappingUtil_BC_O_Common(){
	
	}

	public MappingUtil_BC_O_Common(cs.b2b.core.mapping.util.MappingUtil util){
		this.util = util;
	}
	
	public int getDBRowLimit() {
		return util.getDBRowLimit()
	}

	public int getDBTimeOutInSeconds() {
		return util.getDBTimeOutInSeconds()
	}
	
	public String bizkeyCSBkRefNumQuery(String bizKeyTypeId, String csBkgNum, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}
	
		if (util.isEmpty(csBkgNum)) {
			return ""
		}
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select biz_key from b2b_biz_key where biz_key_type_id=? and transaction_id in (select transaction_id from b2b_biz_key where biz_key_type_id='CS_BK_REF_NUM' and biz_key = ?)";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, bizKeyTypeId);
			pre.setString(2, csBkgNum);
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
	
	String concat(def v1, def v2) {
		String val1 = v1==null?'':v1.toString().trim()
		String val2 = v2==null?'':v2.toString().trim()
		return val1 + val2
	}
	
	String addLeading0(String str, int len) {
		String ret = ''
		if (str!=null) {
			ret = str.trim()
			for (int i=0; ret.length()<len && i<len; i++) {
				ret = '0' + ret
			}
		}
		return ret
	}
	
	String removeLeading0(String str) {
		String ret = str
		if (ret!=null) {
			ret = ret.trim()
			int len = ret.length()
			for (int i=0; i<len && ret.startsWith('0'); i++) {
				ret = ret.substring(1)
			}
		}
		return ret
	}
	
	String getGTNTemperature(String temp) {
		if (temp==null || temp.trim().length()==0) {
			return ""
		}
		String ret = ""
		BigDecimal dd = null
		try {
			dd = new BigDecimal(temp)
			if (dd > -9999.5 && dd < 9999.5) {
				if (temp.indexOf(".") < 0) {
					ret = temp
				} else {
					if (dd > 1000) {
						ret = util.substring(temp, 1, 4)
					} else if (dd < -1000) {
						ret = util.substring(temp, 1, 5)
					} else if (dd > 0) {
						ret = util.substring(temp, 1, 5)
					} else if (dd < 0) {
						ret = util.substring(temp, 1, 6)
					} else {
						ret = "0"
					}
				}
			}
		} catch (Exception e) {
		}
		return ret
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

		csuploadXml.'ns0:CSUploadKeyTrack'('xmlns:ns0':'http://www.tibco.com/schemas/message-processing/Schemas/System/CSUploadKeyTrack.xsd') {
			'ns0:ErrorID' 'SEND_TO_TP'
			if (csuploadErrorKey.isEmpty()) {
				'ns0:CSUploadErrorKey'{
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
//				csuploadErrorKey.each{ currentErrorKey ->
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
	 * @param TP_ID
	 * @param conn
	 * @return MarkupBuilder
	 */
	public void buildBizKey(MarkupBuilder bizKeyXml, cs.b2b.core.mapping.bean.bc.Header current_Header, cs.b2b.core.mapping.bean.bc.Body current_Body, int current_BodyIndex, def errorKeyList, def headerMsgDT, String TP_ID, Connection conn) {
		
		def appErrorReportError = errorKeyList.find{it?.IS_ERROR == YES}
		def appErrorReportObsolete = errorKeyList.find{it?.IS_ERROR == NO}

		def bizKey = []
		def extRefKey = []
		def containerKey = []
		def bkBizKey = []
		def crnBizKey = []
		def partyKey = []

		if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.CarrierBookingNumber)){
			bkBizKey.add(['BK_NUM':current_Body?.GeneralInformation?.CarrierBookingNumber])
		}
		
		current_Body.ExternalReference.each { current_ExternalReference ->
			if (current_ExternalReference==null || current_ExternalReference.ReferenceDescription==null) {
				// bypass this element if null, return is bypass this element only
				return
			}
			if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Agent's Reference".toUpperCase()){
				extRefKey.add(["AR":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Consignee's Reference".toUpperCase()){
				extRefKey.add(["AT":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Bill Of Lading Reference Number".toUpperCase()){
				extRefKey.add(["BLN":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Booking Number".toUpperCase()){
				extRefKey.add(["BKG":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Customshouse Broker License Number".toUpperCase()){
				extRefKey.add(["CBL":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Contract Party Reference Number".toUpperCase()){
				extRefKey.add(["CPR":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Contract Number".toUpperCase()){
				extRefKey.add(["CT":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "CS Assigned Booking Reference No.".toUpperCase() &&
						!(current_Body?.GeneralInformation?.CSBookingRefNumber)){
				extRefKey.add(["CS_BK_REF_NUM":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Customer Reference Number".toUpperCase()){
				extRefKey.add(["CRN":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Export Reference Number".toUpperCase()){
				extRefKey.add(["ERN":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Federal Maritime Commission Forwarders Number".toUpperCase()){
				extRefKey.add(["FM":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Forwarder Reference".toUpperCase()){
				extRefKey.add(["FR":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Invoice Number".toUpperCase()){
				extRefKey.add(["INV":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Purchase Order Number".toUpperCase()){
				extRefKey.add(["PRN":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Shipper Reference".toUpperCase()) {
				extRefKey.add(["SR":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Shipper's Identifying Number for Shipment (SID)".toUpperCase()){
				extRefKey.add(["SID":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Shipping Order Number".toUpperCase()){
				extRefKey.add(["SO":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Unique Consignment Reference Number".toUpperCase()){
				extRefKey.add(["UC":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Motor Vehicle Identification Number".toUpperCase()){
				extRefKey.add(["VT":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Broker Reference".toUpperCase()){
				extRefKey.add(["AU":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Consignee's Order Number".toUpperCase()){
				extRefKey.add(["CG":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Export License Number".toUpperCase()){
				extRefKey.add(["EX":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Government Reference Number".toUpperCase()){
				extRefKey.add(["GN":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Letter of Credit Number".toUpperCase()){
				extRefKey.add(["LC":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference.ReferenceDescription?.toUpperCase().trim() == "Transaction Reference Number".toUpperCase()){
				extRefKey.add(["TN":current_ExternalReference.ReferenceNumber])
			}
			
		}
		
		def carBkgNum = (current_Body?.GeneralInformation?.CarrierBookingNumber==null?"":current_Body?.GeneralInformation?.CarrierBookingNumber.trim())
		def scac = (current_Body.GeneralInformation?.SCACCode ? current_Body?.GeneralInformation?.SCACCode?.trim() : "")
		
		current_Body.ContainerGroup?.Container.each{ current_Container ->
			if (StringUtil.isNotEmpty(current_Container?.ContainerNumber)) {
				containerKey.add(["CNTR_NUM":current_Container.ContainerNumber])
			}
		}
		
		current_Body.Party.each { current_Party ->
			if(current_Party?.PartyType?.trim() == "SHP"){
				partyKey.add(["SHIPPER":current_Party.PartyName])
			}else if(current_Party?.PartyType?.trim() == "CGN"){
				partyKey.add(["CONSIGNEE":current_Party.PartyName])
			}else if(current_Party?.PartyType?.trim() == "BPT"){
				partyKey.add(["BOOKING_PARTY":current_Party.PartyName])
			}else if(current_Party?.PartyType?.trim() == "FWD"){
				partyKey.add(["FORWARDER":current_Party.PartyName])
			}else if(current_Party?.PartyType?.trim() == "CAR"){
				partyKey.add(["CARRIER":current_Party.PartyName])
			}
		}
		
		current_Body?.Party.each{ cu_party ->
			if (cu_party?.CarrierCustomerCode) {
				partyKey.add(["CRN": cu_party.CarrierCustomerCode])
			}
		}
		
		if( current_Body.Party.find{ it.PartyType == "CAR"}==null && scac) {
			partyKey.add(["CARRIER": scac])
		}
		
		bizKey.addAll(bkBizKey)
		bizKey.addAll(extRefKey)
		bizKey.addAll(containerKey)
		bizKey.addAll(partyKey)
		bizKey.addAll(crnBizKey)
		
		def scacTpId = util.getSCACTpIDFromScacTpMap(current_Body?.GeneralInformation.SCACCode, conn)
		bizKey.add(["STPID": scacTpId])
		bizKey.add(["RTPID": current_Header?.ReceiverID])
		bizKey.add(["LKP": scac + ',' + carBkgNum])
		bizKey.add(["CS_BK_REF_NUM": current_Body?.GeneralInformation?.CSBookingRefNumber])
		bizKey.add(["L3PBKP":current_Body?.GeneralInformation?.CSBookingRefNumber?.trim()])
		
		def externalRef = current_Body?.ExternalReference?.find{ cu_ExternalRef -> cu_ExternalRef?.ReferenceDescription?.trim()?.equalsIgnoreCase("CS Reference Number") }
		if(externalRef != null && externalRef.ReferenceNumber){
			bizKey.add(["L3PBKP":externalRef.ReferenceNumber.trim()])
		}
		bizKey.add(["L3BKSID":TP_ID])
		bizKey.add(["L3BKRID":TP_ID])
		
		bizKeyXml.'ns0:Transaction'('xmlns:ns0':'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
			'ns0:ControlNumberInfo' {
				'ns0:Interchange' '1'
				//'bkg_confirm' - /ns0:BC/pfx2:Body-BookingConfirm/pfx2:EventInformation-Body-BookingConfirm/ns:EventCode-EventType
				'ns0:Group' current_Body.EventInformation?.EventCode
				'ns0:Transaction' current_BodyIndex + 1
			}
			
			//filter duplicate items
			def addItems = []
			bizKey.each { currentBizKeyMap ->
				currentBizKeyMap.each { key, value ->
					//util.substring(value, 1, 100)  -- no need substring here, kukri will cut it
					String val = value
					String _duplicateVal = key + ", value: " + val
					if (addItems.contains(_duplicateVal) || val==null || val.trim()=='') {
						//loop next item
						return
					} else {
						addItems.add(_duplicateVal)
					}
					'ns0:BizKey'{
						'ns0:Type' key
						'ns0:Value' val
					}
				}
			}
			addItems.clear()
			
			Map<String, String> mapCar = util.getOceanCarrierInfo(current_Body?.GeneralInformation?.SCACCode, conn)
			'ns0:CarrierId' mapCar.get("ID")

			if (errorKeyList.size != 0) {
				'ns1:AppErrorReport'('xmlns:ns1':'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
					if (appErrorReportObsolete != null) {
						'ns1:Status' OBSOLETE
					} else {
						'ns1:Status' ERROR
					}
					'ns1:MsgCode' 'B2B-APP-250'	//max length is 20, exceed will missing error bizkey, bw app is B2B-APP-250 , kukri is B2B-APP-MSG-GENERA
					if (appErrorReportObsolete != null) {
						'ns1:Msg' appErrorReportObsolete?.VALUE
					} else {
						'ns1:Msg' appErrorReportError?.VALUE
					}
					'ns1:Severity' '4'
				}
			}
		}
	}
	
	public void promoteCSUploadToSession(String appSessionId, StringWriter csuploadWriter) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_CSUPLOAD', csuploadWriter?.toString())
	}

	public void promoteBizKeyToSession(String appSessionId, StringWriter bizKeyWriter) {
		String bizkey = bizKeyWriter?.toString()
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizkey)
	}
	
	public void promoteHeaderIntChgMsgId(String appSessionId, String intMsgIdVal) {
		if (intMsgIdVal) {
			cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', intMsgIdVal)
		}
	}
	
	public void promoteScacCode(String appSessionId, String scacVal) {
		if (scacVal) {
			cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', scacVal)
		}
	}
	
	public void promoteOutputFileNameToSession(String appSessionId, String outputFileName) {
		if (util.isNotEmpty(outputFileName)) {
			cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_TransportOutputFileName', outputFileName)
		}
	}
	
	
	// *************************************************************
	//                        VALIDATION PART
	// *************************************************************
	// Prep validation part --- start
	void checkBCStatus(String bookingStatus, List<String> allowStatusList, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		if (bookingStatus==null || (!allowStatusList.contains(bookingStatus))) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg+bookingStatus : "Booking Confirm status is not valid to send out"]
			errorKeyList.add(errorKey)
				return
		}
	}
	
	void checkSealNumberLength(cs.b2b.core.mapping.bean.bc.Body current_Body, int maxReq, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		boolean isOverLen = false
		current_Body.ContainerGroup?.Container?.each { current_Container ->
			current_Container?.Seal?.each { current_Seal ->
				if (current_Seal!=null && current_Seal.SealNumber!=null && current_Seal.SealNumber.length()>maxReq) {
					isOverLen = true
					return
				}
			}
		}
		
		if (isOverLen) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Seal number max then "+maxReq]
			errorKeyList.add(errorKey)
				return
		}
	}
	
	void filterBCStatus(String bookingStatus, List<String> filterStatusList, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		if (bookingStatus==null || filterStatusList.contains(bookingStatus)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Booking Confirm status is not supported : " + bookingStatus]
			errorKeyList.add(errorKey)
				return
		}
	}
	
	void checkOnlyNewAllowSend(cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ) {
		Map<String,String> errorKey = null
		def cu_EventDes = current_Body.EventInformation?.EventDescription
		if (cu_EventDes) {
			if (cu_EventDes.toUpperCase() != 'NEW') {
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Only NEW BC can be sent out."]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
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
	
	void CheckBookingOfficeFiltering(String tpId, cs.b2b.core.mapping.bean.bc.Body current_Body, Connection conn, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		def bkgOffice = current_Body.GeneralInformation?.BookingOffice
		boolean hasNotConversion = true
		if (bkgOffice) {
			Map<String, String> bkgOfficeMap = util.getCdeConversionFromIntCde(tpId, 'BC', 'O', null, 'EDIFACT', 'BookingOffice', "UPPER-CASE:"+bkgOffice.toUpperCase(), conn)
			hasNotConversion = (bkgOfficeMap==null || bkgOfficeMap.size()==0)
		}
		bkgOffice = (bkgOffice==null?"":bkgOffice)
		Map<String,String> errorKey = null
		if (hasNotConversion) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: (errorMsg==null?"":errorMsg) + bkgOffice]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	void checkExternalReferenceShipperReference(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ) {
		Map<String,String> errorKey = null
		def varBkgStatus = current_Body.GeneralInformation?.BookingStatus
		def var_SRRef = current_Body?.ExternalReference.find{it.CSReferenceType && it.CSReferenceType=="SR" && it.ReferenceNumber}
		
		if (var_SRRef==null) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing External Reference: Shipper Reference."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	void CheckIsValidBookingParty(String tpId, cs.b2b.core.mapping.bean.bc.Body current_Body, Connection conn, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		boolean isNotFoundValidSAP = true
		List<Party> parties = current_Body.Party?.findAll{it.PartyType == "BPT"}
		
		parties.each { current_Party ->
			if (current_Party.CarrierCustomerCode) {
				//(String TP_ID, String convertTypeId, String DIR_ID, String SrcFmtId, String SegId, String SegNum, String fromValue, Connection conn)
				String extCde = util.getEDICdeReffromIntCde(tpId, 'CSUPLOAD', 'O', 'BCCS2X', 'SAP_CHECKING', 'SAPID', current_Party.CarrierCustomerCode, conn)
				if (extCde) {
					isNotFoundValidSAP = false
					return
				}
			}
		}
		
		Map<String,String> errorKey = null
		if (isNotFoundValidSAP) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Invalid Booking Party."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	// Prep validation part --- end
	
	
	//301-4010    prep checking start
	
	/**
	 * @param bizkeyList
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void bPTSAPIDChecking(List<Map<String, String>> bizkeyList, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		if(bizkeyList.size() < 1){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Booking Party's SAPID (<CarrierCustomerCode>) not in list."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param bizkeyList
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void checkBookingShipmentId(cs.b2b.core.mapping.bean.bc.Body current_Body,List<String> bizkeyList, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
	
		def cu_BookingStatus = current_Body?.GeneralInformation?.BookingStatus.toUpperCase()
		def cu_ReferenceNumber = current_Body?.ExternalReference.find{ cu_externalRef ->
			cu_externalRef?.ReferenceDescription.trim().toUpperCase() == 'SYSTEM REFERENCE NUMBER'}?.ReferenceNumber
		
		if( (  cu_BookingStatus == 'Rejected'.toUpperCase() || cu_BookingStatus == 'Declined'.toUpperCase() ) &&  ( StringUtil.isEmpty(cu_ReferenceNumber) && bizkeyList.get(0) == '' )){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No System Reference Number and 300 shipment id is not found"]
			errorKeyList.add(errorKey)
			return
		}	
		
	}
		
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void checkSystemReferenceNoForRejectEnable(cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		def srn = ''
		if(current_Body?.GeneralInformation?.BookingStatus=='Rejected' || current_Body?.GeneralInformation?.BookingStatus=='Declined'){
			if(current_Body?.GeneralInformation?.BookingStatusRemarks?.indexOf('|') >= 0){
			srn = util.substring(current_Body?.GeneralInformation?.BookingStatusRemarks, 1, current_Body?.GeneralInformation?.BookingStatusRemarks.indexOf('|'))?.trim()
			}
		}
		else{
			srn = current_Body?.ExternalReference.find{ cu_externalRef ->
			cu_externalRef.ReferenceDescription!=null && cu_externalRef.ReferenceDescription.trim().toUpperCase()=='SYSTEM REFERENCE NUMBER'}?.ReferenceNumber
		}
		
			if(util.isEmpty(srn)){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No System Reference Number"]
				errorKeyList.add(errorKey)
				return
			}
	}	
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void fWDPartyChecking(cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		def condition = current_Body?.Party.find{it.PartyType=='FWD'}
		
		if(  condition==null ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Forwarder Legal Party"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param bookingStatusList
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void genericBookingStatusChecking(List<String> bookingStatusList, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		if( bookingStatusList.get(0) == "" || bookingStatusList.get(0) == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Only accept Confirmed/Cancelled/Rejected/Pending bookings"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void gTNStatusChecking(cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
				
		def cu_BookingStatus = current_Body?.GeneralInformation?.BookingStatus.trim()
		
		if( cu_BookingStatus != "Confirmed" && cu_BookingStatus !="Cancelled" ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "MessageFunction are not Confirmed or Cancelled"]
					errorKeyList.add(errorKey)
					return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void nonOOCLSystemReferenceNumberChecking(cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def scaccode = current_Body?.GeneralInformation?.SCACCode	
		def externalRef = current_Body?.ExternalReference.find{ cu_externalref ->
			cu_externalref.ReferenceDescription.trim().toUpperCase() == "CS Reference Number".toUpperCase() && cu_externalref.ReferenceNumber != null}
		if( externalRef == null && scaccode != "OOLU"){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "For non OOCL shipment, absent of CS Reference Number"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void oOCLSystemReferenceNumberChecking(cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def scaccode = current_Body?.GeneralInformation?.SCACCode
		def externalRef = current_Body?.ExternalReference.find{ cu_externalref ->
			cu_externalref.ReferenceDescription.trim().toUpperCase() == "System Reference Number".toUpperCase() && cu_externalref.ReferenceNumber != null}
		if( externalRef == null && scaccode == "OOLU"){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "For OOCL shipment, absent of System Reference Number, meaning the booking channel is not from EDI."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param invalidSapIdCountList
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void partySAPIDChecking(cs.b2b.core.mapping.bean.bc.Body current_Body,List<String> invalidSapIdCountList,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
			
//		def count_Party = current_Body?.Party.size()
//		def count_error = 0
//		def tp_id = getTpID(msgRequestId, conn)
//		current_Body?.Party.each{ cu_Party ->
//			if(getInvalidSapID(tp_id, cu_Party.CarrierCustomerCode, conn)==0){
//				count_error = count_error +1
//			}
//		}
//		if(count_error == count_Party){
//			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Non-LANDSENDGTN Shipment"]
//			errorKeyList.add(errorKey)
//			return
//		}
		def count_Party = current_Body?.Party
		println count_Party.size()
		def count_error = 0
		for(String count : invalidSapIdCountList){
			if(count.equals("0")){
				count_error = count_error+1
			}
		}
		if(count_error == count_Party.size()){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Non-LANDSENDGTN Shipment"]
			errorKeyList.add(errorKey)
			return
		}
		
	}
	
	/**
	 * 
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void pendingStatusChecking(cs.b2b.core.mapping.bean.bc.Body current_Body ,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
				
		if(current_Body?.GeneralInformation?.BookingStatus.trim() == "Pending"){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BookingStatus is pending"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void pOCheckingAndSendEmailIfMissing(cs.b2b.core.mapping.bean.bc.Body current_Body ,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
			
		def externalRef = current_Body?.ExternalReference.find{ cu_extRef -> cu_extRef.CSReferenceType.trim().toUpperCase() == "PO" && cu_extRef.ReferenceNumber.trim() !="" }
			
		if( externalRef == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Purchase Order Number for BKG"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void rejectStatusChecking(cs.b2b.core.mapping.bean.bc.Body current_Body ,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
				
		def bookingStatus = current_Body?.GeneralInformation?.BookingStatus.toUpperCase()
		if( bookingStatus != "Rejected".toUpperCase() && bookingStatus != "Declined".toUpperCase()){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Only accept Rejected and Declined BookingStatus"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param invalidSapIdcountList
	 * @param invalidSapID_namecountList
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 */
	
	void sAPIDChecking(List<String> invalidSapIdcountList, List<String> invalidSapID_namecountList,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		for(int i=0;i<invalidSapIdcountList.size();i++){
			if(invalidSapIdcountList[i].equals("0") && invalidSapID_namecountList[i].equals("0")){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Non-MWVEI Shipment"]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
	/**
	 * @param scacInvalidList
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void sCACChecking(List<String> scacInvalidList, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		if( scacInvalidList.get(0) == "" || scacInvalidList.get(0) == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "SCAC not in list"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param invalidSapIdList
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void sHPSAPIDChecking(List<Map<String,String>> invalidSapIdList, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		if( invalidSapIdList == null || invalidSapIdList.size() < 1){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Invalid shipper CCC."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void specialGTNStatusChecking(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
				
		def bookingStatus = current_Body?.GeneralInformation?.BookingStatus.trim()
		if( bookingStatus !="Confirmed" && bookingStatus != "Cancelled" && bookingStatus != "Pending"){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "MessageFunction are not Confirmed or Cancelled or Pending"]
					errorKeyList.add(errorKey)
					return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void systemReferenceNumberM(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		def externalRef = current_Body?.ExternalReference?.find {it?.ReferenceDescription?.trim()?.toUpperCase() == "System Reference Number".toUpperCase() && it?.ReferenceNumber}
		if( externalRef == null){
			Map<String,String> errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No EDI Booking Confirmation in external reference."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	//IFTMBCD998  
	/**
	 * @param conversionDataMapList
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckBkgStatusConvertResponseTypeCode
	 */
	void checkBkgStatusConvertResponseTypeCode(List<Map<String,String>> conversionDataMapList, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		for(HashMap<String, String> cu_map : conversionDataMapList){
			if( cu_map.get("INT_CDE") == varBkgStatus && (cu_map.get("EXT_CDE")==""||cu_map.get("EXT_CDE")==null ) ){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No BkgStatus conversion to ResponseTypeCode in BGM4343"]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param ext_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckBookingOfficeFiltering
	 */
	void checkBookingOfficeFiltering(String isRejectedInt_cde, String para_ext_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		
		if(!isRejected && (para_ext_cde != null||para_ext_cde!="")){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Unknown booking office for BASF-Germany:"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckBookingStatus
	 */
	void checkBookingStatus(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		
		if((isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase())) ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Filter by BookingStatus"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckCarrierBookingNumber
	 */
	void checkCarrierBookingNumber(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ) {
		Map<String,String> errorKey = null
		
		if (util.isEmpty(current_Body?.GeneralInformation?.CarrierBookingNumber)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing CarrierBookingNumber or empty"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	void checkCarrierBookingNumberForNonCancelled(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ) {
		Map<String,String> errorKey = null
		
		if (util.isEmpty(current_Body?.GeneralInformation?.CarrierBookingNumber) && current_Body?.GeneralInformation?.BookingStatus!='Cancelled') {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing CarrierBookingNumber or empty for non Cancelled booking"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckCarrierBookingNumberForAllStatus
	 */
	void checkCarrierBookingNumberForAllStatus(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		if( current_Body?.GeneralInformation?.CarrierBookingNumber == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing mandatory Carrier Booking Number."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	void checkReeferVentilation(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		boolean isInvalid = false
		Map<String,String> errorKey = null
		current_Body.Cargo?.each { current_Cargo ->
			current_Cargo.ReeferCargoSpec?.each { current_ReeferCargo ->
				if (util.isNotEmpty(current_ReeferCargo.Ventilation?.Ventilation) && util.isEmpty(current_ReeferCargo.Ventilation?.VentilationUnit)) {
					isInvalid = true
				} else if (util.isEmpty(current_ReeferCargo.Ventilation?.Ventilation) && util.isNotEmpty(current_ReeferCargo.Ventilation?.VentilationUnit)) {
					isInvalid = true
				}
			}
		}
		if (isInvalid) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing ReeferCargoSpec Ventilation or VentilationUnit."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckExternalRefCSBKGForRejected
	 */
	void checkExternalRefCSBKGForRejected(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def varCSBExtRef = current_Body?.ExternalReference.find{it.CSReferenceType == "CSBKG"}
		
		if ((varBkgStatus.equals("Rejected")||varBkgStatus.equals("Declined")) && util.isEmpty(varCSBExtRef)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Reject, Declined BC has not external reference for CSBKG."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckExternalReferenceForwarderRef
	 */
	void checkExternalReferenceForwarderRef(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		
		if( !isRejected && current_Body?.ExternalReference.find{ cu_ExternalRef -> cu_ExternalRef.CSReferenceType.equals("FR") && cu_ExternalRef.ReferenceNumber!=null} == null ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Forwarder Reference."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName checkExternalReferenceForwarderRefForNonOOLU
	 */
	void CheckExternalReferenceForwarderRefForNonOOLU(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		
		def varFRExt = current_Body?.ExternalReference.find{ it.CSReferenceType.equals("FR")}
		
		if( !isRejected && (varFRExt != null && varFRExt.ReferenceNumber == null) && !current_Body?.GeneralInformation.SCACCode.equals("OOLU") ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Data not needed as it has not Forwarder Reference."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckExternalReferenceMultipleShiperReferenceForOOLU
	 */
	void checkExternalReferenceMultipleShiperReferenceForOOLU(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		
		def var_ShiperRef = current_Body?.ExternalReference.findAll{ cu_ExtRef -> cu_ExtRef.ReferenceDescription.trim().toUpperCase().equals("Shipper Reference".toUpperCase())}
		def var_fowardRef = current_Body?.ExternalReference.find{ it.ReferenceDescription.trim().toUpperCase().equals("Forwarder Reference".toUpperCase())}.ReferenceNumber
		def varSysRefNum =  current_Body?.ExternalReference.find{ it.ReferenceDescription.trim().toUpperCase().equals("SYSTEM REFERENCE NUMBER".toUpperCase())}.ReferenceNumber
		
		if( !isRejected && (varSysRefNum == null && var_ShiperRef?0:var_ShiperRef.size()>1 ) && !current_Body?.GeneralInformation.SCACCode.equals("OOLU") ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "There are multiple shiper reference"]
			errorKeyList.add(errorKey)
			return
		}else if(!isRejected && (var_fowardRef == null && var_ShiperRef?0:var_ShiperRef.size()>1 ) && !current_Body?.GeneralInformation.SCACCode.equals("OOLU") ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "There are multiple shiper reference"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckExternalReferenceSystemRefNumForOOLU
	 */
	void checkExternalReferenceSystemRefNumForOOLU(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		def var_sysRef = current_Body?.ExternalReference.find{ it.ReferenceDescription.equals("SYSTEM REFERENCE NUMBER")}
		
		if( !isRejected && current_Body?.GeneralInformation?.SCACCode == "OOLU" && (!var_sysRef?var_sysRef.ReferenceNumber:null) == null ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Data not needed as it has not SYSTEM REFERENCE NUMBER."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckExternalReferenceSystemRefNumOrShiperReferenceForOOLU
	 */
	void checkExternalReferenceSystemRefNumOrShiperReferenceForOOLU(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		def var_sysRef = current_Body?.ExternalReference.find{ it.ReferenceDescription.equals("SYSTEM REFERENCE NUMBER")}
		def var_ShiperReference = current_Body?.ExternalReference.find{ it.ReferenceDescription.trim().toUpperCase().equals("Shipper Reference".toUpperCase())}
		
		if( !isRejected && current_Body?.GeneralInformation?.SCACCode == "OOLU" && (!var_sysRef?var_sysRef.ReferenceNumber:null) == null && (!var_ShiperReference?var_ShiperReference.ReferenceNumber:null)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Data not needed as it has not system reference number or shiper reference"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	void checkExternalRefSystemRefNumForNonRejected(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def bkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def sysRef = current_Body?.ExternalReference?.find{ it?.ReferenceDescription?.toUpperCase() == "SYSTEM REFERENCE NUMBER"}
		
		if ((sysRef==null || util.isEmpty(sysRef.ReferenceNumber)) && bkgStatus!= "Rejected" && bkgStatus != "Declined") {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Data not needed as it has not SYSTEM REFERENCE NUMBER."]
			errorKeyList.add(errorKey)
			return
		}
	}	
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckExternalRefSystemRefNumIsEDIBookingAndSCACIsOOLU
	 */
	void checkExternalRefSystemRefNumIsEDIBookingAndSCACIsOOLU(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def var_sysRef = current_Body?.ExternalReference.find{ it.ReferenceDescription.equals("SYSTEM REFERENCE NUMBER")}
		
		if( (!var_sysRef?var_sysRef.ReferenceNumber:"") != "EDI BOOKING CONFIRMATION" && current_Body?.GeneralInformation?.SCACCode != "OOLU" ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Filter Non-EDI Booking Confirm or Non-OOLU data."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckExtRefForwarderReferenceForNotImportUK
	 */
	void checkExtRefForwarderReferenceForNotImportUK(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varExtRefForwarder = current_Body?.ExternalReference.find{ it.CSReferenceType=="FR"} 
		
		if( (!varExtRefForwarder?varExtRefForwarder.ReferenceNumber:null) == null && current_Body?.Route?.LastPOD?.Port?.Country.trim().toUpperCase() !="UNITED KINGDOM" ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Forwarder Reference is missing. "]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckFirstPOLand1stOceanLegDepartureDTLocDT
	 */
	void checkFirstPOLand1stOceanLegDepartureDTLocDT(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		
		if( !isRejected && current_Body?.Route?.FirstPOL?.DepartureDT?.LocDT == null && current_Body?.Route?.OceanLeg[0]?.DepartureDT[0]?.LocDT == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Departure Datetime for Port of Load"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckFullReturnCutoffDTLocDT
	 */
	void checkFullReturnCutoffDTLocDT(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		
		if( !isRejected && current_Body?.Route?.FullReturnCutoff?.LocDT == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Full Return Cutoff DT for Place of Origin"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckIsEDIBkg
	 */
	void checkIsEDIBkg(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varRef = current_Body?.ExternalReference.find{ it.CSReferenceType=="CSBKG"}
		
		if( (!varRef?varRef.ReferenceNumber:null) == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "IFTMBC Non-EDI Bkg."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckIsValidBookingParty
	 */
	void checkIsValidBookingParty(List<Map<String,String>> conversionDataMapList, String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		//TODO need review important!
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		def var_Party = current_Body?.Party.findAll{ cu_Party-> cu_Party.PartyType=="BPT"}.each{ cu_Party->
			for(HashMap<String, String> map : conversionDataMapList){
				if( map.get("INT_CDE") == cu_Party?.CarrierCustomerCode && (map.get("EXT_CDE")!=""||map.get("EXT_CDE")==null) ){
					errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "IFTMBC Non-EDI Bkg."]
					errorKeyList.add(errorKey)
					return
					
				}
			}
		}
	}
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckLastPODandLastOceanLegArrivalDTLocDT
	 */
	void checkLastPODandLastOceanLegArrivalDTLocDT(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		
		if( !isRejected && current_Body?.Route?.LastPOD?.ArrivalDT?.LocDT == null && current_Body?.Route?.OceanLeg[-1]?.ArrivalDT[0]?.LocDT == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Arrival Datetime for Port of Discharge"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param isRejectedInt_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckLastPODLastOceanLegArrivalDTandArrivalAtFinalHubLocDT
	 */
	void checkLastPODLastOceanLegArrivalDTandArrivalAtFinalHubLocDT(String isRejectedInt_cde, cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
				
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def isRejected = (isRejectedInt_cde != null || isRejectedInt_cde != "") && ( !isRejectedInt_cde.contains(varBkgStatus.toUpperCase()))
		
		if( !isRejected && current_Body?.Route?.LastPOD?.ArrivalDT?.LocDT == null && current_Body?.Route?.ArrivalAtFinalHub[0]?.LocDT == null && current_Body?.Route?.OceanLeg[-1]?.ArrivalDT[0]?.LocDT == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Datetime for Place of Delivery"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckOceanLegLoadingAndDischarging
	 */
	void checkOceanLegLoadingAndDischarging(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varOceanLeg = current_Body?.Route?.OceanLeg		
		
		if( varOceanLeg.size() == 0 && varOceanLeg.find{it.SVVD.Loading}==null && varOceanLeg.find{it.SVVD.Discharge} == null ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing mandatory OceanLeg and SVVD Loading Discharging."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckOnlyNewAllowSendForPendingStatus
	 */
	void checkOnlyNewAllowSendForPendingStatus(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		def varBkgStatus = !current_Body?.GeneralInformation?.BookingStatus?current_Body?.GeneralInformation?.BookingStatus.trim():""
		def varEventDes = current_Body?.EventInformation?.EventDescription
		
		if( varBkgStatus == "Pending" && varEventDes!=null && varEventDes != "NEW"){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Filter if Pending BC is not NEW."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * IsMissSCACCode
	 */
	void checkSCACCode(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		if(util.isEmpty(current_Body?.GeneralInformation?.SCACCode)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Unknow Carrier, SCACCode is missing."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/*
	 * checkContainerSizeTypeAlertISA
	 */
	void checkContainerSizeTypeAlertISA(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String SCAC, String MSG_FMT_ID, String CONVERT_TYPE_ID, Connection conn, cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		
		StringBuilder invalidStr = new StringBuilder()
		current_Body?.ContainerGroup?.Container?.each { current_Container ->
			if (util.isEmpty(current_Container.CarrCntrSizeType)) {
				//ignore empty car cntr
				//invalidStr.append("'', ")
			} else {
				def convertCntrSize = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, SCAC, MSG_FMT_ID, CONVERT_TYPE_ID, current_Container.CarrCntrSizeType, conn)
				if (convertCntrSize==null || convertCntrSize.size()==0) {
					invalidStr.append(current_Container.CarrCntrSizeType).append(", ")
				}
			}
		}
		String invStr = invalidStr.toString()
		if(invStr!=null && invStr.trim().length()>0) {
			if (invStr.length()>405) {
				invStr = invStr.substring(0, 405)
			}
			if (invStr.endsWith(", ")) {
				invStr = invStr.substring(0, invStr.length()-2)
			}
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Please check with ISA and IIS for the handling procedure of unrecognized container size type: "+invStr]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName checkShipperRefForOOLUReject
	 */
	void CheckShipperRefForOOLUReject(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		def varBkgStatus = !current_Body?.GeneralInformation?.BookingStatus?current_Body?.GeneralInformation?.BookingStatus.trim():""
		
		def in_var_1 = !current_Body?.GeneralInformation?.BookingStatusRemarks?current_Body?.GeneralInformation?.BookingStatusRemarks.trim():null
		def out_var_1 = ""
		def splitChar = "|"
		
		if (in_var_1!=null && in_var_1.indexOf(splitChar)>=0) {
			out_var_1 = in_var_1.substring(0, in_var_1.indexOf(splitChar));
		} else {
				out_var_1 = in_var_1;
		}
		if (out_var_1!=null) {
			out_var_1 = out_var_1.trim();
		}
		if( (varBkgStatus=="Declined"||varBkgStatus=="Rejected") && out_var_1.length()!=16 ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Reject BC need 16 chars RefNumber in BookingStatusRemarks."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	void checkShipperRefNonNullForOOLUReject(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		def varBkgStatus = current_Body?.GeneralInformation?.BookingStatus
		def varBkgStatusRemarks = current_Body?.GeneralInformation?.BookingStatusRemarks
		def extRefNum = ''
		if (varBkgStatusRemarks?.indexOf('|')>=0) {
			extRefNum = varBkgStatusRemarks.substring(0, varBkgStatusRemarks.indexOf('|'))
		}
		if ((varBkgStatus == "Declined" || varBkgStatus == "Rejected") && util.isEmpty(extRefNum)) {
			Map<String,String> errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Reject BC need RefNumber in BookingStatusRemarks."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckSPCompanyID
	 */
	void checkSPCompanyID(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		
		if( current_Body?.GeneralInformation?.SPCompanyID == null ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing SPCompanyID in GeneralInformation."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param ext_cde
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName FilterBookingStatus
	 */
	void filterBookingStatus(String ext_cde,cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
		def varBkgStatus = !current_Body?.GeneralInformation?.BookingStatus?current_Body?.GeneralInformation?.BookingStatus.trim():""
		
		if( (ext_cde != "" && ext_cde != null) && ext_cde.contains(varBkgStatus) ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Ignored Invalid Message Status: "]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName FilterIfFirstPOLIsNotUK
	 */
	void filterIfFirstPOLIsNotUK(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
			
		if(  current_Body?.Route?.FirstPOL?.Port?.Country.trim().toUpperCase() !="UNITED KINGDOM" ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Ignore transaction as it is Not Export from UK shipment."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName FilterIfLastPODIsUK
	 */
	void filterIfLastPODIsUK(cs.b2b.core.mapping.bean.bc.Body current_Body , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList ){
		Map<String,String> errorKey = null
			
		if(  current_Body?.Route?.LastPOD?.Port?.Country.trim().toUpperCase() == "UNITED KINGDOM" ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Ignore transaction as it is Import to UK shipment."]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @change SHAEDI235
	 * @change SHAEDI235-1
	 */
	void checkFirstOceanLegPODUnlocation(cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(current_Body?.Route?.OceanLeg?.find{it?.POD?.Port?.Country?.toUpperCase() != "CHINA"} != null && StringUtil.isEmpty(current_Body?.Route?.OceanLeg?.find{it?.POD?.Port?.Country?.toUpperCase() != "CHINA"}?.POD?.Port?.LocationCode?.UNLocationCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing POD UN Location Code."]
			errorKeyList.add(errorKey)
			return
		}

	}
	
	/**
	 * David : Urgent recovery for rollback to add this method, for EIR_EASIPASS BC urgent support case for 7/12/2017
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @change SHAEDI235
	 */
	void checkFirstOceanLengPODUnlocation(cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(StringUtil.isEmpty(current_Body?.Route?.OceanLeg?.find{it.LegSeq == '1'}?.POD?.Port?.LocationCode?.UNLocationCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing POD UN Location Code."]
			errorKeyList.add(errorKey)
			return
		}

	}
	
	
	//prep checking end
	
	//posp checking start
	
	/**
	 * @param current_Body
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName B102MandatoryChecking
	 */
	void checkB102Mandatory(cs.b2b.core.mapping.bean.bc.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		
		Map<String,String> errorKey = null
		
		if(StringUtil.isEmpty(root?.Loop_ST?.B1?.E145_02.text()) && current_Body.GeneralInformation.BookingStatus != "Rejected" && current_Body.GeneralInformation.BookingStatus != "Declined") {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Mandatory B1_02_145."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	void checkGRP2(cs.b2b.core.mapping.bean.bc.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		
		Map<String,String> errorKey = null
		
		if (root?.Group_UNH?.Group2_RFF==null || root?.Group_UNH?.Group2_RFF?.size()==0) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "GRP2.RFF is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	void checkGRP2_RFF_C506_1153_1154(cs.b2b.core.mapping.bean.bc.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		
		Map<String,String> errorKey = null
		
		def grp2Rff = root?.Group_UNH?.Group2_RFF?.find{util.isEmpty(it.RFF?.C506_01?.E1154_02) || util.isEmpty(it.RFF?.C506_01?.E1154_02)}
		if (grp2Rff!=null) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "GRP2.RFF.C506.1153 and 1154 are mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName B102MandatoryCheckingForDeclined
	 */
	void checkB102MandatoryForDeclined(cs.b2b.core.mapping.bean.bc.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(StringUtil.isEmpty(root?.Loop_ST?.B1?.E145_02.text()) && (current_Body.GeneralInformation.BookingStatus == "Rejected" || current_Body.GeneralInformation.BookingStatus == "Declined")){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Mandatory B1_02_145."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName B104StatusAChecking
	 */
	void checkB104StatusA(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.B1?.E558_04.text() != 'A'){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "B104 is required equals A."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName B104StatusChecking
	 */
	void checkB104Status(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if (root?.Loop_ST?.find(){it?.B1?.E558_04?.text() == 'X'} != null) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "B104 Status is not acceptable"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CRATENBARRELB104StatusChecking
	 */
	void checkCRATENBARRELB104Status(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		List<String> suppStatus = ['A','U','D']
		
		if(!suppStatus.contains(root?.Loop_ST?.B1?.E558_04.text())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BookingStatus is not Cancelled or Confirmed(New and Update)."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName FRExternalReference
	 * Should be prep checking?
	 */
	void checkFRExternalReference(cs.b2b.core.mapping.bean.bc.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(current_Body.ExternalReference.find{it.CSReferenceType == 'FR'} == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Forwarder Reference."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName IsConfirmedBooking
	 */
	void checkIsConfirmedBooking(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		this.checkB104StatusA(root, isError, "Non-Confirmed Booking.", errorKeyList)
	}
	
	/**
	 * @param current_Body
	 * @param containerListWithScacAsKey
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName isValidEmptyPickupISOSizeType
	 * should be prep checking?
	 */
	void checkIsValidEmptyPickupISOSizeType(cs.b2b.core.mapping.bean.bc.Body current_Body, String tpBizAgreementDisplayName, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, Connection conn) {
		Map<String,String> errorKey = null
		String invalidCntrSizeType = ''
		def defScacCode = current_Body?.GeneralInformation?.SCACCode
		def invalidSize = []
		current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup?.each{ current_EmptyPickup ->
			String isoType = current_EmptyPickup.ISOSizeType
			if (util.isNotEmpty(isoType)) {
				def defConvType = ''
				if (isoType && defScacCode && tpBizAgreementDisplayName) {
					defConvType = util.getConversionByTpIdDirFmtScac(tpBizAgreementDisplayName, "O", "X.12", "ContainerType", defScacCode, isoType, conn)
				}
				if (defConvType == null || defConvType?.trim()?.length()==0) {
					if (! invalidSize.contains(isoType)) {
						invalidSize.add(isoType)
						if (invalidCntrSizeType) {
							invalidCntrSizeType = invalidCntrSizeType + ", " + isoType
						} else {
							invalidCntrSizeType = isoType
						}
					}
				}
			}
		}
		invalidSize.clear()
		
		if (invalidCntrSizeType) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Invalid ISOSizeTypepls check " + invalidCntrSizeType ]
			errorKeyList.add(errorKey)
		}
	}
	
	/**
	 * @param current_Body
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N101MandatoryChecking
	 */
	void checkN101Mandatory(cs.b2b.core.mapping.bean.bc.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		def partyBK = root?.Loop_ST?.Loop_N1.find{it.N1.E98_01.text() == 'BK'}
		def partyCA = root?.Loop_ST?.Loop_N1.find{it.N1.E98_01.text() == 'CA'}
		if((partyBK==null || partyCA==null) && !(current_Body.GeneralInformation?.BookingStatus in ["Rejected", "Declined"])) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Mandatory parties BK and CA"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N1LoopCountChecking
	 */
	void checkN1LoopCount(cs.b2b.core.mapping.bean.bc.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_N1?.size() > 4 && current_Body.GeneralInformation.BookingStatus != "Rejected" && current_Body.GeneralInformation.BookingStatus != "Declined"){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "N1 loop count > 4"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N9EQChecking
	 */
	void checkN9EQ(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.N9.find{it.E128_01.text() == 'EQ'} == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing N9_01=EQ, Obsolete."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName R4EUNLocChecking
	 */
	void checkR4DUNLoc(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4.find{it.R4.E115_01.text() == 'D' && it.R4.E309_02.text() == 'UN' && StringUtil.isEmpty(it.R4.E310_03.text())} != null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "UNLOCODE of Place of Delivery is missing."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName R4LUNLocChecking
	 */
	void checkR4LUNLoc(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4.find{it.R4.E115_01.text() == 'L' && it.R4.E309_02.text() == 'UN' && StringUtil.isEmpty(it.R4.E310_03.text())} != null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "UNLOCODE of Place of Delivery is missing."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName R4YChecking
	 */
	void checkR4Y(cs.b2b.core.mapping.bean.bc.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{it.R4.E115_01?.text() == 'Y'} == null && current_Body.Route?.OceanLeg?.size() > 1 && current_Body.GeneralInformation?.BookingStatus != "Rejected" && current_Body.GeneralInformation?.BookingStatus != "Declined") {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "R4|Y is Missing"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName V105Checking
	 */
	void checkV105(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.V1.find{it.E140_05.text() != 'OOLU'} != null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Invalid carrier code."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName Y3_01MandatoryChecking
	 */
	void checkY3_01Mandatory(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(StringUtil.isEmpty(root?.Loop_ST?.Y3?.E13_01?.text())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Mandatory Y3_01_13."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName Y406Checking
	 */
	void checkY406(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_Y4.find{StringUitl.isEmpty(it.Y4.E24_06.text())} != null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Equipment Type is missing."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	
	//posp checking end
	
}
