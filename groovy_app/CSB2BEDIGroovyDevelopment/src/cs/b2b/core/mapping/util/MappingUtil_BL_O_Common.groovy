package cs.b2b.core.mapping.util

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.*
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat
/**
 * @author RENGA
 *
 */
class MappingUtil_BL_O_Common {

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
	
	static final String COLLECT = "0"
	
	static final String PREPAID = "1"
	
	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";
	def yyyyMMdd = 'yyyyMMdd';
	
	private cs.b2b.core.mapping.util.MappingUtil util;
	
	public MappingUtil_BL_O_Common(){
	
	}
	
	public String replaceZeroAfterPoint(String str){
		if(str?.indexOf('.') > -1){
			str = str?.replaceAll('0+?$', '' )?.replaceAll('[.]$', '' )
		}
		return  str
	}

	public MappingUtil_BL_O_Common(cs.b2b.core.mapping.util.MappingUtil util){
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
	 * @param csuploadXml
	 * @param csuploadErrorKey
	 * @param Interchange
	 * @param MSG_REQ_ID
	 * @return MarkupBuilder
	 */
	public void buildCsupload(MarkupBuilder csuploadXml, List csuploadErrorKey,String Interchange, String MSG_REQ_ID) {
		
		def currentSystemDt = new Date()
		def appErrorReportError = csuploadErrorKey.find{it?.IS_ERROR == YES}
		def appErrorReportObsolete = csuploadErrorKey.find{it?.IS_ERROR == NO}

		csuploadXml.'ns0:CSUploadKeyTrack'('xmlns:ns0':'http://www.tibco.com/schemas/message-processing/Schemas/System/CSUploadKeyTrack.xsd') {
			'ns0:ErrorID' 'SHMT_TO_TP'
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
				'ns0:Type' 'EVENTCODE'
				'ns0:Value' Interchange
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
	public void buildBizKey(MarkupBuilder bizKeyXml, cs.b2b.core.mapping.bean.bl.Body current_Body,int current_BodyIndex, def errorKeyList, def headerMsgDT, def headerInterchangeId, String TP_ID, Connection conn) {
		
		def scacTpId = util.getSCACTpIDFromScacTpMap(current_Body?.GeneralInformation?.SCACCode, conn)
		
		def appErrorReportError = errorKeyList.find{it?.IS_ERROR == YES}
		def appErrorReportObsolete = errorKeyList.find{it?.IS_ERROR == NO}

		def bizKey = []
		
		def extRefKey = []
		def containerKey = []
		def bkBizKey = []
		def crnBizKey = []
		def partyKey = []

		current_Body?.GeneralInformation?.CarrierBookingNumber?.each { current_CarrierBookingNumber ->
			if (StringUtil.isNotEmpty(current_CarrierBookingNumber)) {
				bkBizKey.add(['BK_NUM':current_CarrierBookingNumber])
			}
		}
		
		
		current_Body?.ExternalReference?.each {current_ExternalReference ->
			
			if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "SO" && current_ExternalReference.ReferenceDescription?.toUpperCase()?.trim() == "SHIPPERS ORDER NUMBER" ){
				extRefKey.add(["SO":current_ExternalReference.getCSReferenceType() + current_ExternalReference.ReferenceNumber + current_ExternalReference.ReferenceDescription])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "BL"){
				extRefKey.add(["BL_NUM":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "SR"){
				extRefKey.add(["SR":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "PO"){
				extRefKey.add(["PRN":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "FR"){
				extRefKey.add(["FR":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "SID"){
				extRefKey.add(["SID":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "INV"){
				extRefKey.add(["INV":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "MVID"){
				extRefKey.add(["VT":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "CR"){
				extRefKey.add(["CRN":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "CTR"){
				extRefKey.add(["CT":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "FM"){
				extRefKey.add(["FM":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "CBL"){
				extRefKey.add(["CBL":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "EXPR"){
				extRefKey.add(["ERN":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "CGR"){
				extRefKey.add(["AT":current_ExternalReference.ReferenceNumber])
			}else if(current_ExternalReference?.CSReferenceType?.toUpperCase()?.trim() == "UC"){
				extRefKey.add(["UC":current_ExternalReference.ReferenceNumber])
			}
			
		}
		
		current_Body.Container.each {current_Container ->
			if(StringUtil.isNotEmpty(current_Container.ContainerNumber)){
				containerKey.add(["CNTR_NUM":current_Container.ContainerNumber])
			}
		}
		
		current_Body.Party.each { current_Party ->
			if(current_Party.PartyType.trim() == "SHP"){
				partyKey.add(["SHIPPER":current_Party.PartyName])
			}else if(current_Party.PartyType.trim() == "CGN"){
				partyKey.add(["CONSIGNEE":current_Party.PartyName])
			}else if(current_Party.PartyType.trim() == "BRK"){
				partyKey.add(["BOOKING_PARTY":current_Party.PartyName])
			}else if(current_Party.PartyType.trim() == "FWD"){
				partyKey.add(["FORWARDER":current_Party.PartyName])
			}else if(current_Party.PartyType.trim() == "NPT" || current_Party.PartyType.trim() == "ANP"){
				partyKey.add(["NOTIFY_PARTY":current_Party.PartyName])
			}
			
			crnBizKey.add(['CRN':current_Party?.CarrierCustomerCode])
		}
		
		
		bizKey.addAll(bkBizKey)
		bizKey.addAll(extRefKey)
		bizKey.addAll(containerKey)
		bizKey.addAll(partyKey)
		bizKey.addAll(crnBizKey)
		
		bizKey.add(["CARRIER":current_Body.GeneralInformation.SCACCode])
		
		if(StringUtil.isNotEmpty(current_Body.GeneralInformation.BLNumber)){
			bizKey.add(["BL_NUM":current_Body.GeneralInformation.BLNumber])
		}
		
		bizKey.add(["RTPID":TP_ID])
		bizKey.add(["STPID":scacTpId])
		bizKey.add(["LKP":current_Body.GeneralInformation.SCACCode + ',' + (current_Body.GeneralInformation.BLNumber ? current_Body.GeneralInformation.BLNumber : "")?.trim()])

		bizKeyXml.'ns0:Transaction'('xmlns:ns0':'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
			'ns0:ControlNumberInfo' {
				'ns0:Interchange' headerMsgDT
				'ns0:Group' headerInterchangeId
				'ns0:Transaction' current_BodyIndex + 1
			}
			//filter duplicate items
			def addItems = []
			bizKey.each { currentBizKeyMap ->
				currentBizKeyMap.each { key, value ->
					String val = util.substring(value, 1, 100)
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


			'ns0:CarrierId' util.getOceanCarrierInfo(current_Body?.GeneralInformation?.SCACCode, conn)?.get("ID")

			if (errorKeyList.size != 0) {
				'ns1:AppErrorReport'('xmlns:ns1':'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
					if (appErrorReportObsolete != null) {
						'ns1:Status' OBSOLETE
					} else {
						'ns1:Status' ERROR
					}
					'ns1:MsgCode' 'B2B-APP-250'	//max length is 20, exceed will missing error bizkey
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
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizKeyWriter?.toString())
	}
	
	public void promoteHeaderIntChgMsgId(String appSessionId, cs.b2b.core.mapping.bean.bl.Body body){
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', body.TransactionInformation.MessageID)
	}
	
	public void promoteScacCode(String appSessionId, cs.b2b.core.mapping.bean.bl.Body body){
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', body.GeneralInformation.SCACCode)
	}

	public void promoteOutputFileNameToSession(String appSessionId, String outputFileName) {
		if (util.isNotEmpty(outputFileName)) {
			cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_TransportOutputFileName', outputFileName)
		}
	}
	
	/**
	 * @param current_Body
	 * @return
	 */
	boolean isLCL(cs.b2b.core.mapping.bean.bl.Body current_Body){
		return current_Body?.Container?.size() == 0 && current_Body?.Cargo?.size() > 0
	}
	
	Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo(cs.b2b.core.mapping.bean.bl.Body current_Body){
		
		Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> map = [:];

		if(isLCL(current_Body)){
			for(int i = 0; i * 120 < current_Body.Cargo.size() ; i++ ){
				int beginIndex = i * 120
				int endIndex = (i + 1) * 120
				if(endIndex > current_Body.Cargo.size()){
					endIndex = current_Body.Cargo.size()
				}
				map.put(new cs.b2b.core.mapping.bean.bl.Container(ContainerNumber: "Dummy${i+1}"), current_Body.Cargo[beginIndex..<endIndex])
			}
		}else{
			current_Body.Container.each{currentContainer ->
				List<cs.b2b.core.mapping.bean.bl.Cargo> cargos = new ArrayList<cs.b2b.core.mapping.bean.bl.Cargo>();
				if(current_Body.Cargo.find{it.CurrentContainerNumber == currentContainer.ContainerNumber && it.CurrentContainerCheckDigit == currentContainer.ContainerCheckDigit}){
					cargos.addAll(current_Body.Cargo.findAll{it.CurrentContainerNumber == currentContainer.ContainerNumber && it.CurrentContainerCheckDigit == currentContainer.ContainerCheckDigit})
				}
				map.put(currentContainer, cargos)
			}
		}
		
		return map;
	}
	
	//charge filter start
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @RuleName FilterByApprovedForCustomer
	 */
	void filterByApprovedForCustomer(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(freightCharge.IsApprovedForCustomer == "false"){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(freightChargeCNTR.IsApprovedForCustomer == "false"){
					iterator.remove();
				}
			}
		}
	}
	
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @param acceptChargeTypeList
	 * @RuleName FilterByChargeCodeAccept
	 */
	void filterByChargeCodeAccept(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, List<String> acceptChargeTypeList){
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(!acceptChargeTypeList.contains(freightCharge.ChargeCode?.toUpperCase())){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(!acceptChargeTypeList.contains(freightChargeCNTR.ChargeCode?.toUpperCase())){
					iterator.remove();
				}
			}
		}
	}
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @param blockChargeTypeList
	 * @RuleName FilterByChargeCodeBlock
	 */
	void filterByChargeCodeBlock(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, List<String> blockChargeTypeList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(blockChargeTypeList.contains(freightCharge.ChargeCode)){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(blockChargeTypeList.contains(freightChargeCNTR.ChargeCode)){
					iterator.remove();
				}
			}
		}
	}
	
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @param filterType
	 * @RuleName FilterByChargeType
	 */
	void filterByChargeType(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, String filterType){
	
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(filterType == freightCharge.ChargeType){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(filterType == freightChargeCNTR.ChargeType){
					iterator.remove();
				}
			}
		}
			
	}
	
	
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @RuleName FilterByContainerLevel
	 */
	void filterByContainerLevel(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(freightCharge?.CalculateMethod?.startsWith("2") || freightCharge?.CalculateMethod?.startsWith("4") || "CONTAINER" == freightCharge?.CalculateMethod?.toUpperCase()){
					iterator.remove();
				}
			}
		}
	}
	
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @param acceptContainerTypeList
	 * @RuleName FilterByContainerType
	 */
	void filterByContainerType(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, List<String> acceptContainerTypeList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(!acceptContainerTypeList.contains(freightCharge.CalculateMethod)){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(freightChargeCNTR?.OOCLCntrSizeType){
					if(!acceptContainerTypeList.contains(freightChargeCNTR.OOCLCntrSizeType)){
						iterator.remove();
					}
				}else if(!acceptContainerTypeList.contains(freightChargeCNTR.CalculateMethod)){
					iterator.remove();
				}
			}
		}
	}
	
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @RuleName FilterByFNDCSCountryCde
	 */
	void filterByFNDCSCountryCde(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, String fndCountryCode, List<String> blockCountryCodeList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(blockCountryCodeList.contains(fndCountryCode) && PREPAID == freightCharge.ChargeType){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(blockCountryCodeList.contains(fndCountryCode) && PREPAID == freightChargeCNTR.ChargeType){
					iterator.remove();
				}
			}
		}
	}
	
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @RuleName FilterByNegativeCollect
	 */
	void filterByNegativeCollect(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if((freightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toBigDecimal() < 0) && COLLECT == freightCharge.ChargeType){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if((freightChargeCNTR?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toBigDecimal() < 0) && COLLECT == freightChargeCNTR.ChargeType){
					iterator.remove();
				}
			}
		}
	}

	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @RuleName FilterByPORCSCountryCde
	 */
	void filterByPORCSCountryCde(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, String porCountryCode, List<String> blockCountryCodeList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(blockCountryCodeList.contains(porCountryCode?.trim()) && COLLECT == freightCharge.ChargeType){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(blockCountryCodeList.contains(porCountryCode?.trim()) && COLLECT == freightChargeCNTR.ChargeType){
					iterator.remove();
				}
			}
		}
	}
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @param acceptCurrencyList
	 * @param blockCurrencyList
	 * @RuleName FilterByTotalAmountCurrency
	 */
	void filterByTotalAmountCurrency(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, List<String> acceptCurrencyList, List<String> blockCurrencyList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(!acceptCurrencyList?.contains(freightCharge.TotalAmtInPmtCurrency?.attr_Currency) || blockCurrencyList?.contains(freightCharge.TotalAmtInPmtCurrency?.attr_Currency)){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(!acceptCurrencyList?.contains(freightChargeCNTR.TotalAmtInPmtCurrency?.attr_Currency) || blockCurrencyList?.contains(freightChargeCNTR.TotalAmtInPmtCurrency?.attr_Currency)){
					iterator.remove();
				}
			}
		}
	}
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @RuleName FilterByTotalAmtInPmtCurrencyIfNegative
	 */
	void filterByTotalAmtInPmtCurrencyIfNegative(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(freightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toBigDecimal() < 0){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(freightChargeCNTR?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toBigDecimal() < 0){
					iterator.remove();
				}
			}
		}
	}
	
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @param porCountry
	 * @param firstPolCountry
	 * @param lastPodCountry
	 * @param fndCountry
	 * @param acceptChargeCodeList
	 * @param acceptCountryCodeList
	 * @RuleName FilterCATERPILLARGTN
	 */
	void filterCATERPILLARGTN(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, String porCountry, String firstPolCountry, String lastPodCountry, String fndCountry, List<String> acceptChargeCodeList, List<String> acceptCountryCodeList){
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(((acceptCountryCodeList.contains(porCountry?.toUpperCase()) || acceptCountryCodeList.contains(firstPolCountry?.toUpperCase())) && freightCharge.ChargeType == PREPAID)){
				}else if(((acceptCountryCodeList.contains(lastPodCountry?.toUpperCase()) || acceptCountryCodeList.contains(fndCountry?.toUpperCase())) && acceptChargeCodeList?.contains(freightCharge.ChargeCode) && freightCharge.ChargeType == COLLECT)){
				}else{
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(((acceptCountryCodeList.contains(porCountry?.toUpperCase()) || acceptCountryCodeList.contains(firstPolCountry?.toUpperCase())) && freightChargeCNTR.ChargeType == PREPAID)){
				}else if(((acceptCountryCodeList.contains(lastPodCountry?.toUpperCase()) || acceptCountryCodeList.contains(fndCountry?.toUpperCase())) && acceptChargeCodeList?.contains(freightChargeCNTR.ChargeCode) && freightChargeCNTR.ChargeType == COLLECT)){
				}else{
					iterator.remove();
				}
			}
		}
	}
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @param firstPolCountry
	 * @param fndCountry
	 * @param validCountry
	 * @RuleName FilterCUMMINSCASS
	 */
	void filterCUMMINSCASS(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, String firstPolCountry, String fndCountry, String validCountry){

		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(validCountry?.trim()?.toUpperCase() == firstPolCountry?.toUpperCase()){
					if(freightCharge.ChargeType == '0'){
						iterator.remove();
					}
				}
				else if(validCountry?.trim()?.toUpperCase() == fndCountry?.toUpperCase()){
					if(freightCharge.ChargeType == '1'){
						iterator.remove();
					}
				}else{
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(validCountry?.trim()?.toUpperCase() == firstPolCountry?.toUpperCase()){
					if(freightChargeCNTR.ChargeType == COLLECT){
						iterator.remove();
					}
				}
				else if(validCountry?.trim()?.toUpperCase() == fndCountry?.toUpperCase()){
					if(freightChargeCNTR.ChargeType == PREPAID){
						iterator.remove();
					}
				}else{
					iterator.remove();
				}
			}
		}
	}
	
	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @param porCountry
	 * @param firstPolCountry
	 * @param fndCountry
	 * @RuleName FilterHONEYWELL
	 */
	void filterHONEYWELL(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList, String porCountry, String firstPolCountry, String fndCountry){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(fndCountry?.toUpperCase() == 'US' || fndCountry?.toUpperCase() == 'CA'){
					if(freightCharge?.ChargeType == '1'){
						iterator.remove();
					}
				}else if(porCountry?.toUpperCase() == 'US' || firstPolCountry?.toUpperCase() == 'US'){
					if(freightCharge?.ChargeType == '0'){
						iterator.remove();
					}
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(fndCountry?.toUpperCase() == 'US' || fndCountry?.toUpperCase() == 'CA'){
					if(freightChargeCNTR?.ChargeType == '1'){
						iterator.remove();
					}
				}else if(porCountry?.toUpperCase() == 'US' || firstPolCountry?.toUpperCase() == 'US'){
					if(freightChargeCNTR?.ChargeType == '0'){
						iterator.remove();
					}
				}
			}
		}
	}

	
	/**
	 * @param freightChargeList
	 * @param freightChargeCNTRList
	 * @RuleName FilterByTotalAmtInPmtCurrencyIfZero
	 */
	void filterByTotalAmtInPmtCurrencyIfZero(List<FreightCharge> freightChargeList, List<FreightChargeCNTR> freightChargeCNTRList){
		
		if(freightChargeList){
			Iterator<FreightCharge> iterator = freightChargeList.iterator();
			while(iterator.hasNext()){
				FreightCharge freightCharge = iterator.next();
				if(StringUtil.isEmpty(freightCharge.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency) || freightCharge.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency == "0"){
					iterator.remove();
				}
			}
		}
		
		if(freightChargeCNTRList){
			Iterator<FreightChargeCNTR> iterator = freightChargeCNTRList.iterator();
			while(iterator.hasNext()){
				FreightChargeCNTR freightChargeCNTR = iterator.next();
				if(StringUtil.isEmpty(freightChargeCNTR.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency) || freightChargeCNTR.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency == "0"){
					iterator.remove();
				}
			}
		}
	}
	
	//charge filter end
	
	//prep checking start
	

	/**
	 * @param current_Body
	 * @param maxLength
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckSealNumberLength
	 * @Tested True tested by file in HDYOW
	 * 
	 */
	void checkSealNumberLength(cs.b2b.core.mapping.bean.bl.Body current_Body, int maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		current_Body.Container.each {currentContainer ->
			if (currentContainer?.Seal.findAll{it?.SealNumber?.length() > maxLength}.size() > 0) {
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Not accepted the length of SealNumber more than " + maxLength]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
	/**
	 * 
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName Amendments_C
	 * @Tested True tested by file in BESTBUYD2L
	 */
	void amendments_C(cs.b2b.core.mapping.bean.bl.Body current_Body,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String, String> errorKey = null;
		
		if(current_Body?.EventInformation?.EventDescription?.trim() != 'NEW'){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Amendments will be sent"]
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
	 * @RuleName BLIssueDateCheck
	 * @author XIEMI
	 * @Tested True tested by file in VANPORTUSBANK
	 */
	void bLIssueDateCheck(cs.b2b.core.mapping.bean.bl.Body current_Body,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String, String> errorKey = null;
		SimpleDateFormat sdfXmlFmt = new SimpleDateFormat(xmlDateTimeFormat);
		if(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.LocDT){
			Date locDT = sdfXmlFmt.parse(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.LocDT);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(locDT);
			calendar.add(Calendar.DATE,60);

			if((new Date()).after(calendar.getTime())){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BL was issued more than 60 days"]
				errorKeyList.add(errorKey)
				return
			}
		}
	}

	
	
	/**
	 *
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName bLReadyOnly
	 * @author XIEMI
	 * @Tested True tested by file in CDLGTN
	 */
	void bLReadyOnly(cs.b2b.core.mapping.bean.bl.Body current_Body,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String, String> errorKey = null;
		
		current_Body?.BLStatus?.each { current_BLStatus ->
			if(current_BLStatus?.attr_Type == "BL_STATUS" && current_BLStatus?.Status!="BL Ready"){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Status is not BL-Ready."]
				errorKeyList.add(errorKey)
				return
			}
		}
			
		
	}

	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName cC_Not_USD
	 * @author XIEMI
	 * @Tested True tested by file in ONMUSBANK
	 */
	void cC_Not_USD(cs.b2b.core.mapping.bean.bl.Body current_Body,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String, String> errorKey = null;
		
		if(current_Body?.FreightCharge?.find{it?.ChargeType == COLLECT && it?.TotalAmtInPmtCurrency!=null && it?.TotalAmtInPmtCurrency?.attr_Currency!= null && it?.TotalAmtInPmtCurrency?.attr_Currency?.trim() != 'USD'}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Collect Charge currency is not USD"]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 * @param current_Body
	 * @param chargeType -- what chargeType need to be checked
	 * @param checkingCurrency -- what currency need to be checked
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName Currency_Check in 310 clp app
	 * @author RENGA
	 * @Tested True tested by file in SARECYCLING
	 */
	void checkCurrency(cs.b2b.core.mapping.bean.bl.Body current_Body, String chargeType, String checkingCurrency, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String, String> errorKey = null;

		if(current_Body?.FreightCharge?.find{it?.ChargeType == chargeType && it?.TotalAmtInPmtCurrency?.attr_Currency?.trim() != checkingCurrency &&
				StringUtil.isNotEmpty(it?.TotalAmtInPmtCurrency?.toString()) && it?.TotalAmtInPmtCurrency?.toString() != "0" }){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Currency in ${chargeType == PREPAID ? "prepaid" : "collect"} charge is not ${checkingCurrency} ."]
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
	 * @RuleName CheckRouteLocationHONEYWELL
	 * @author XIEMI
	 */
	void checkRouteLocationHONEYWELL(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String, String> errorKey = null;
		
		if(!((current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode == 'CA' || current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode == 'US') || 
			(current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode == 'US' || current_Body?.Route?.FirstPOL?.Port?.CSCountryCode == "US"))){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "For shipment when condition (FND is in US or Canada) or POR/POL = US not fullfilled."]
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
	 * @RuleName CollectCharge1NegativeFilter
	 * @Tested True tested by file in CASSOCEANFRT
	 */
	void collectCharge1NegativeFilter(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String, String> errorKey = null;
		def expectedTotalAmtInPmtCurrency = current_Body?.FreightCharge?.find{ it?.ChargeType == COLLECT && StringUtil.isNotEmpty(it?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)}
		if( current_Body?.FreightCharge?.findAll{it?.ChargeType == COLLECT}?.size() == 1  && expectedTotalAmtInPmtCurrency && expectedTotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toBigDecimal()<0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Only 1 Collect Charge and it is negative."]
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
	 * @RuleName CollectCurrencyNotUSDCAD
	 * @author XIEMI
	 */
	void collectCurrencyNotUSDCAD(cs.b2b.core.mapping.bean.bl.Body current_Body, List<FreightChargeCNTR> freightChargeCNTRList, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String, String> errorKey = null;
		if(freightChargeCNTRList == null || freightChargeCNTRList?.size() == 0 || freightChargeCNTRList?.findAll{it?.ChargeType == COLLECT && (it?.ChargeAmount?.attr_Currency == "USD" || it?.ChargeAmount?.attr_Currency == "CAD")}?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Collect Charge with USD or CAD"]
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
	 * @RuleName Currency_Check_Collect
	 * @author XIEMI
	 * @Tested True tested by file in LFSS
	 */
	void currency_Check_Collect(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		List<FreightCharge> freightCharges = current_Body?.FreightCharge?.findAll{it?.ChargeType == COLLECT}
		
		freightCharges?.each{current_FreightCharge->
			if(current_FreightCharge?.TotalAmtInPmtCurrency!=null && current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency!=null && current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency !='CAD'){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Collect Charge with USD or CAD"]
				errorKeyList.add(errorKey)
				return
			}
		}
		
	}
	
	/**
	 * 
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName Currency_Check_Collect
	 * @author XIEMI
	 * @Tested True tested by file in FRIESLANDGTN
	 */
	void filterBLTypeMEMO(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(current_Body?.GeneralInformation?.BLType?.toUpperCase()?.equals("MEMO")){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Filter MEMO in BL Type"]
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
	 * @RuleName FilterByBLType
	 * @author XIEMI
	 */
	void filterByBLType(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		current_Body?.Party?.each {current_Party ->
			if(current_Party?.PartyName){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : ""]
				errorKeyList.add(errorKey)
				return
			}
			
		}
	}
	
	
	/**
	 * 
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param ext_cdeList
	 * @RuleName FilterByNoRegisteredSapID
	 * @author XIEMI
	 * @Tested True tested by file in LANDSENDGTN
	 */
	void filterByNoRegisteredSapID(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, List<String> ext_cdeList){
		Map<String,String> errorKey = null
		if(!current_Body?.Party?.find{ext_cdeList?.contains(it?.CarrierCustomerCode)}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Non-Legal Party Sap ids"]
			errorKeyList.add(errorKey)
			return
		}
		
	}
	
	/**
	 * 
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param ext_cdeList
	 * @RuleName FilterByPartyName
	 * @author XIEMI
	 * @Tested True tested by file in SEARSGTN
	 */
	void filterByPartyName(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, List<String> ext_cdeList){
		Map<String,String> errorKey = null
		current_Body?.Party?.each{current_Party ->
			if(ext_cdeList?.contains(current_Party?.PartyName)){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Land's End shipment not sent to Sears"]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
	/**
	 * 
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName FilterByPartyNameIfBECTON
	 * @author XIEMI
	 */
	void FilterByPartyNameIfBECTON(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		current_Body?.Party?.each { current_Party ->
			if(current_Party?.PartyName?.contains("BECTON DICKINSON")){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Becton Dickinson shipment filtered"]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
	/**
	 * 
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName FilterByPartyNameIfUSG
	 * @author XIEMI
	 */
	void filterByPartyNameIfUSG(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		current_Body?.Party?.each { current_Party ->
			if(current_Party?.PartyType?.equals("SHP") || current_Party?.PartyType?.equals("CGN") || current_Party?.PartyType?.equals("FWD")){
				if(current_Party?.PartyName?.equals("USG")){
					errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "USG shipment filtered"]
					errorKeyList.add(errorKey)
					return
				}
			}
		}
	}
	
	
	
	/**
	 *
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param conn
	 * @param msgRequestId
	 * @RuleName FilterBySapID
	 * @author XIEMI
	 */
	void filterBySapID(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, List<String> ext_cdeList){
		
		Map<String,String> errorKey = null
		current_Body?.Party?.each{current_Party ->
			if(ext_cdeList?.contains(current_Party?.CarrierCustomerCode)){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Land's End shipment not sent to Sears"]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
	
	
	/**
	* @param current_Body
	* @param isError
	* @param errorMsg
	* @param errorKeyList
	* @RuleName FilterCollectChargeByPOR
	* @author XIEMI
	* @Test true in file HONEYWELLDSG
	*/
	void filterCollectChargeByPOR(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, List<String> porCountryList, String porCSCountryCode, String chargeLevelValue){
		Map<String,String> errorKey = null
		String fndCSCountryCode = StringUtil.isNotEmpty(current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode) ? current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode : ''
		porCountryList?.each { current_porCountry ->
			if(current_porCountry?.trim() == porCSCountryCode?.trim()){
				if(current_Body.FreightChargeCNTR.findAll {it?.ChargeType == PREPAID}.size() == 0 && chargeLevelValue == "ContainerLvOnly"){
					errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Collect Charge when POR is not US or Canada."+'POR - '+porCSCountryCode+' and FND - '+fndCSCountryCode]
					errorKeyList.add(errorKey)
					return
				}else if(current_Body.FreightCharge.findAll {it?.ChargeType == PREPAID}.size() == 0 && chargeLevelValue == "ShipmentLv"){
					errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Collect Charge when POR is not US or Canada."+'POR - '+porCSCountryCode+' and FND - '+fndCSCountryCode]
					errorKeyList.add(errorKey)
					return
				}
			}
		}
	}
	

	
	/**
	 * 
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName FilterDELinTxnAction
	 * @author XIEMI
	 * @Tested True tested by file in HDYOW
	 */
	void filterDELinTxnAction(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(current_Body?.TransactionInformation?.Action?.equals("DEL")){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Filter DEL status in Transaction/MessageFunction"]
			errorKeyList.add(errorKey)
			return
		}
	}
	

	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param FNDCountryList
	 * @param value
	 * @RuleName FilterPrepaidChargeByFND
	 * @Test true in file HONEYWELLDSG
	 */
	void  filterPrepaidChargeByFND(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg,
	   List<Map<String,String>> errorKeyList, List<String> FNDCountryList, String fndCSCountryCode, String chargeLevelValue){
		Map<String,String> errorKey = null
		String porCSCountryCode = StringUtil.isNotEmpty(current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode) ? current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode : ''
		FNDCountryList?.each { current_FNDCountry ->
			if(current_FNDCountry?.trim() == fndCSCountryCode?.trim()){
				if(current_Body.FreightCharge.findAll {it?.ChargeType == COLLECT}?.size() == 0 && chargeLevelValue == "ShipmentLv"){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Prepaid Charge when FND is not US or Canada.POR - "+porCSCountryCode+' and FND - '+fndCSCountryCode]
				errorKeyList.add(errorKey)
				return
			}else if(current_Body.FreightChargeCNTR.findAll {it?.ChargeType == COLLECT}.size() == 0 && chargeLevelValue == "ContainerLvOnly"){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Prepaid Charge when FND is not US or Canada.POR - "+porCSCountryCode+' and FND - '+fndCSCountryCode]
				errorKeyList.add(errorKey)
				return
				}
			}
		}
	}
	

	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName FNDCountryChecker
	 * @Tested True tested by file in ACUHASBRO
	 */
	void fNDCountryChecker(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, List<String> acceptedList){
		Map<String,String> errorKey = null
		//acceptedList contains "UNITED STATES" or "CANADA" or "US" or "CA"
		def country = current_Body?.Route?.FND?.CityDetails?.Country?.toUpperCase();
		
		if(!acceptedList?.contains(country)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "is not US or CA"]
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
	 * @param chargeCodewithExCdeMap
	 * @author XIEMI
	 */
	void freightChargeCodeChecker(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, Map<String,String> chargeCodewithExCdeMap){
		Map<String,String> errorKey = null
		List<String> unKnowChargeCodeList = []
		current_Body?.FreightCharge?.findAll{ it?.ChargeType ==COLLECT}?.each{ current_FreightCharge->
			def intCde = util.substring(current_FreightCharge?.ChargeCode,1,3)?.toUpperCase()
			if(chargeCodewithExCdeMap.get(intCde) == '999' || StringUtil.isEmpty(chargeCodewithExCdeMap.get(intCde))){
				if("${current_FreightCharge?.ChargeCode?.trim()?:''}${current_FreightCharge?.CalculateMethod?.trim()?:''}"!=""){
					unKnowChargeCodeList?.add(current_FreightCharge?.ChargeCode)
				}
			}
		}
		def unknowChargeCodeResultMsg = ""
		if(unKnowChargeCodeList?.size()>0){
			unKnowChargeCodeList?.each{ cu_Msg->
				cu_Msg = cu_Msg==null?'':cu_Msg
				unknowChargeCodeResultMsg = unknowChargeCodeResultMsg + cu_Msg + ','
			}
			unknowChargeCodeResultMsg = unknowChargeCodeResultMsg?.substring(0,unknowChargeCodeResultMsg?.length()-1) + ' is/are'
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : unknowChargeCodeResultMsg?.trim() + " Unknown Charge code found(L1_08)"]
			errorKeyList.add(errorKey)
			return
		}
	}
	

	 //InvalidCollectCharge_WALGREENSCASS: no errorMsg

	 //InvalidFNDCountry: no errorMsg

	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName InvalidMultiCargoVolUnit
	  * @author XIEMI
	  * @Tested True tested by file in ONMUSBANK
	  */
	 void invalidMultiCargoVolUnit(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null

		 current_Body.Cargo?.findAll{StringUtil.isNotEmpty(it?.Volume?.Volume) && it?.Volume?.Volume != '0' && it?.Volume?.Volume!='NaN'}?.each { current_Cargo ->
			def count1 = current_Body?.Cargo?.findAll{ it?.ContainerNumber?.trim() == current_Cargo?.ContainerNumber?.trim() && it?.ContainerCheckDigit?.trim() == current_Cargo?.ContainerCheckDigit &&
													   StringUtil.isNotEmpty(it?.Volume?.Volume) && it?.Volume?.Volume !='0' && it?.Volume?.Volume !='NaN'}?.size()
			def count2 = current_Body?.Cargo?.findAll{ it?.ContainerNumber?.trim() == current_Cargo?.ContainerNumber?.trim() && it?.ContainerCheckDigit?.trim() == current_Cargo?.ContainerCheckDigit &&
													   it?.Volume?.VolumeUnit?.trim() == current_Cargo?.Volume?.VolumeUnit?.trim() &&
													   StringUtil.isNotEmpty(it?.Volume?.Volume) && it?.Volume?.Volume !='0' && it?.Volume?.Volume !='NaN'}?.size()
			if(count1!=count2){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "is providing different Volume Unit in Cargo."]
				errorKeyList.add(errorKey)
				return
			}
		 }
	 }
	
	
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName InvalidMultiCargoWeightUnit
	  *@author XIEMI
	  * @Tested True tested by file in ONMUSBANK
	  */
	 void invalidMultiCargoWeightUnit(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null

		 current_Body.Cargo?.findAll{StringUtil.isNotEmpty(it?.GrossWeight?.Weight) && it?.GrossWeight?.Weight != '0'}?.each { current_Cargo ->
			def count1 = current_Body?.Cargo?.findAll{ it?.ContainerNumber?.trim() == current_Cargo?.ContainerNumber?.trim() && it?.ContainerCheckDigit?.trim() == current_Cargo?.ContainerCheckDigit &&
												   StringUtil.isNotEmpty(it?.GrossWeight?.Weight) && it?.GrossWeight?.Weight !='0'}?.size()
			def count2 = current_Body?.Cargo?.findAll{ it?.ContainerNumber?.trim() == current_Cargo?.ContainerNumber?.trim() && it?.ContainerCheckDigit?.trim() == current_Cargo?.ContainerCheckDigit &&
												   it?.GrossWeight?.WeightUnit?.trim() == current_Cargo?.GrossWeight?.WeightUnit?.trim() &&
												   StringUtil.isNotEmpty(it?.GrossWeight?.Weight) && it?.GrossWeight?.Weight !='0'}?.size()
			if(count1!=count2){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "is providing different Weight Unit in Cargo."]
				errorKeyList.add(errorKey)
				return
			}
		 }
	 }
	
	
//	 InvalidPOLFNDCUMMINSCASS: no errorMsg
	
		
		 
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @param expectedCarrierCustomerCodeList
	  * @param findPartyType
	  * @Tested True tested by file in RAYONIERUSBANK
	  */
	 void invalidShipperCCC(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, List<String> expectedCarrierCustomerCodeList, String findPartyType){
		 Map<String,String> errorKey = null
		 Party findParty = current_Body?.Party?.find{it?.PartyType== findPartyType}
		 if(!expectedCarrierCustomerCodeList.contains(findParty?.CarrierCustomerCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Shipper's CCC Invalid"]
			errorKeyList.add(errorKey)
			return
		}
	 }
		 
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName L3Checking
	  * @author XIEMI
	  * @Tested True tested by file in DIAGEOGTN
	  */
	void l3Checking(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		def sum1 = BigDecimal.ZERO
		def sum2 = BigDecimal.ZERO
		def sum3 = BigDecimal.ZERO
		def sum4 = BigDecimal.ZERO
		current_Body?.Cargo?.each{current_Cargo ->
			if(current_Cargo?.GCPackageUnit?.size() > 0){
				sum1 = sum1 + new BigDecimal(current_Cargo?.GCPackageUnit[0]?.PackageUnitQuantity ? current_Cargo?.GCPackageUnit[0]?.PackageUnitQuantity: '0')
			}
			if(current_Cargo?.ReeferCargoSpec?.size() > 0 && current_Cargo?.ReeferCargoSpec[0]?.RFPackageUnit?.size() > 0){
				sum2 = sum2 + new BigDecimal(current_Cargo?.ReeferCargoSpec[0]?.RFPackageUnit[0]?.PackageUnitQuantity ? current_Cargo?.ReeferCargoSpec[0]?.RFPackageUnit[0]?.PackageUnitQuantity: '0')
			}
			if(current_Cargo?.DGCargoSpec?.size() > 0 && current_Cargo?.DGCargoSpec[0]?.DGPackageUnit?.size() > 0){
				sum3 = sum3 + new BigDecimal(current_Cargo?.DGCargoSpec[0]?.DGPackageUnit[0]?.PackageUnitQuantity ? current_Cargo?.DGCargoSpec[0]?.DGPackageUnit[0]?.PackageUnitQuantity: '0')
			}
			sum4 = sum4 + new BigDecimal(current_Cargo?.Packaging?.PackageQty? current_Cargo?.Packaging?.PackageQty: '0')
		}
		if( sum1 == 0 && sum2 == 0 && sum3 == 0 && sum4 == 0 &&
		current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.trim()?.equals("0") &&
		current_Body?.GeneralInformation?.BLNetWeight?.Weight?.trim()?.equals("0") &&
		current_Body?.GeneralInformation?.BLVolume?.Volume?.trim()?.equals("0") ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Summary Loop cant be created. Minimum info are not available"]
			errorKeyList.add(errorKey)
			return
		}
	}

		 			
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissFNDUNCode
	 * @author XIEMI
	 * @Tested True tested by file in PIER1
	 */
	void missFNDUNCode(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(StringUtil.isEmpty(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode?.trim())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing FND UN location code for " + "${current_Body?.Route?.FND?.CityDetails?.City?:""}+${current_Body?.Route?.FND?.CityDetails?.Country?:""}"]
			errorKeyList.add(errorKey)
			return
		}
	}
		 
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingBLNumber
	 * @author XIEMI
	 */
	void missingBLNumber(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(!util.notEmpty(current_Body?.GeneralInformation?.BLNumber)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Bill of Lading Number / B3_02_76"]
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
	 * @param ext_CdeList
	 * @RuleName MissingChargeByContainerTypePOLOUSBANK
	 * @author XIEMI
	 * @Tested True tested by file in POLOUSBANK
	 */
	 void missingChargeByContainerTypePOLOUSBANK(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, List<String> excpectedCulculateMethodList, String prepaid){
		 Map<String,String> errorKey = null
		 
		 def isException = true
		 current_Body?.FreightChargeCNTR?.findAll{ it?.ChargeType == prepaid}?.each{current_FreightCharge ->
			 if(excpectedCulculateMethodList?.contains(current_FreightCharge?.CalculateMethod)){
				 isException = false
			 }
		 }
		 if(isException){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Loop LX cannot be generated."]
			 errorKeyList.add(errorKey)
			 return
		 }
	 }
	
	
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingChargePVH
	  * @author XIEMI
	  */
//	 void missingChargePVH(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, 
//		 List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR){
	 void missingChargePVH(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null

		if(current_Body?.FreightCharge?.find{ current_FreightCharge ->
			(current_FreightCharge?.ChargeType?.equals("1") &&  current_FreightCharge?.TotalAmtInPmtCurrency)} &&
		   !current_Body?.FreightCharge?.find{ current_FreightCharge ->
			(current_FreightCharge?.ChargeType?.equals("0") &&  current_FreightCharge?.TotalAmtInPmtCurrency)}){
		   
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Transaction has only prepaid amount."]
			errorKeyList.add(errorKey)
			return
		}
	 }
	
	 
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingChargeTOPOCEAN
	  * @author XIEMI
	  */
	 void missingChargeTOPOCEAN(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(current_Body.FreightCharge?.findAll{it?.ChargeType == PREPAID}?.find {it?.TotalAmtInPmtCurrency?.toString()} && current_Body.FreightCharge?.findAll{it?.ChargeType == COLLECT}?.findAll {it?.TotalAmtInPmtCurrency?.toString()}?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Transaction has only prepaid amount."]
			errorKeyList.add(errorKey)
			return
		}
	 }
	
//	 MissingChargeType: no errorMsg
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingCntrrNum
	 * @author XIEMI
	 * @Tested True tested by file in CDLGTN
	 */
	void missingCntrrNum(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(current_Body?.Container?.find{ current_Container ->
			( current_Container?.ContainerNumber == null  )&& ( ! util.notEmpty(current_Container?.ContainerNumber))}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing container number."]
			errorKeyList.add(errorKey)
			return
		}
	}
	 
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingCollectChargeCode
	  * @author XIEMI
	  * @Tested True tested by file in BESTBUYD2L
	  */
	 void missingCollectChargeCode(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		 
		 if(current_Body?.FreightCharge?.find{ current_FreightCharge ->
			 current_FreightCharge?.ChargeType?.equals("0") && !util.notEmpty(current_FreightCharge?.ChargeCode)}){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Charge type is missing"]
			 errorKeyList.add(errorKey)
			 return
		 }
	 }
	
	 
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingCollectExchangeRateforNonUSD
	  * @author XIEMI
	  */
	 void missingCollectExchangeRateforNonUSD(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		 
		 if( current_Body?.FreightCharge?.find{ current_FreightCharge ->
			 current_FreightCharge?.ChargeType?.equals("0") &&
			 current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency != "USD" &&
			 util.notEmpty( current_FreightCharge?.TotalAmtInPmtCurrency) &&
			 current_FreightCharge?.TotalAmtInPmtCurrency != "0" &&
			 !util.notEmpty( current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.trim())}){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Can not find correct exchange rate for not-USD collect charge conversion"]
			 errorKeyList.add(errorKey)
			 return
		 }
	 }
	
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingDeliveryDate
	 * @Tested True tested by file in TRANSAMERICAN
	 */
	void missingDeliveryDate(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(current_Body?.Route?.ArrivalAtFinalHub?.size()==0 || current_Body?.Route?.ArrivalAtFinalHub?.find{ current_ArrivalAtFinalHub ->
			((current_ArrivalAtFinalHub?.attr_Indicator?.equals("A") && !util.notEmpty(current_ArrivalAtFinalHub?.LocDT?.LocDT))||
					(current_ArrivalAtFinalHub?.attr_Indicator?.equals("A") && current_ArrivalAtFinalHub?.LocDT?.LocDT?.equals("0")) ||
					!(current_ArrivalAtFinalHub?.attr_Indicator?.equals("A") && current_ArrivalAtFinalHub?.LocDT?.LocDT))  && //
					( ( current_ArrivalAtFinalHub?.attr_Indicator?.equals("E") && !util.notEmpty(current_ArrivalAtFinalHub?.LocDT?.LocDT) )||
					( current_ArrivalAtFinalHub?.attr_Indicator?.equals("E") && current_ArrivalAtFinalHub?.LocDT?.LocDT?.equals("0") ) ||
					!( current_ArrivalAtFinalHub?.attr_Indicator?.equals("E") && current_ArrivalAtFinalHub?.LocDT?.LocDT))}){

			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Delivery Date / B3_09_32"]
			errorKeyList.add(errorKey)
			return
		}
		 
	 }
	 
	 
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingDGUNNumber
	  * @author XIEMI
	  */
	void missingDGUNNumber(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	 Map<String,String> errorKey = null

	 if( current_Body?.Cargo?.find{ current_Cargo ->
		 current_Cargo?.DGCargoSpec?.find(){ current_DGCargoSpec ->
			 !util.notEmpty(current_DGCargoSpec?.UNNumber?.trim())
		 }
	 } ) {
		 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "H101 is mandatory."]
		 errorKeyList.add(errorKey)
		 return
	 }
	}
	 
	 
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingFNDKDCode
	  * @author XIEMI
	  */
	 void missingFNDKDCode(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		 
		 if(!util.notEmpty(current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode?.trim()))
		  {
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing FND KD Code - "]
			 errorKeyList.add(errorKey)
			 return
		 }
	 }
	
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingL1_2
	  * @author XIEMI
	  * @Tested True tested by file in GCLOGISTICSGTN
	  */
	 void missingL1_2(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		 
		 if(current_Body?.FreightCharge?.findAll{it?.ChargeCode !="DEM" && it?.ChargeCode !="DET"}?.size()==0){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Freight Charge without DEM,DET"]
			 errorKeyList.add(errorKey)
			 return
		 }
	 }
	
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingL3_11_80
	  * @author XIEMI
	  * @Tested True tested by file in ONMUSBANK
	  */
	 void MissingL3_11_80(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		def L3_08isNull = true
		
		if(current_Body?.Cargo?.find{it?.Packaging?.PackageQty!=null && it?.Packaging?.PackageQty?.toBigDecimal()!=0}){
			L3_08isNull = false
		}else if(current_Body?.Cargo?.find{it?.ReeferCargoSpec?.find{it?.RFPackageUnit?.find{it?.PackageSeqNumber == '1' && it?.PackageUnitQuantity!=null && it?.PackageUnitQuantity?.toBigDecimal()!=0}}}){
			L3_08isNull = false
		}else if(current_Body?.Cargo?.find{it?.DGCargoSpec?.find{it?.DGPackageUnit?.find{ it?.PackageSeqNumber == '1' && it?.PackageUnitQuantity!=null && it?.PackageUnitQuantity?.toBigDecimal()!=0}}}){
			L3_08isNull = false
		}else if(current_Body?.Cargo?.find{it?.AWCargoSpec?.find{it?.AWPackageUnit?.find{ it?.PackageSeqNumber == '1' && it?.PackageUnitQuantity!=null && it?.PackageUnitQuantity?.toBigDecimal()!=0}}}){
			L3_08isNull = false
		}else if(StringUtil.isNotEmpty(current_Body?.Cargo[0]?.Packaging?.PackageQty) && current_Body?.Cargo[0]?.Packaging?.PackageQty?.toBigDecimal()!=0){
			L3_08isNull = false
		}
		
		if(L3_08isNull){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Mandatory L3."]
			errorKeyList.add(errorKey)
			return
		}
	
	 }
	
	 
	 
	 
	
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MissingOceanFreight
	  * @author XIEMI
	  */
	 void missingOceanFreight(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		 
		 
		PayByInformation payByInformation = current_Body?.FreightChargeCNTR?.find {it?.ChargePrintLable == "Ocean Freight"}?.PayByInformation
		String carrierCustomerCode = '';
		current_Body?.Party?.findAll{StringUtil.isNotEmpty(it?.CarrierCustomerCode)}?.each {current_party ->
			carrierCustomerCode = carrierCustomerCode + current_party?.CarrierCustomerCode;
		}
		if(StringUtil.isEmpty(payByInformation?.PayerName) || StringUtil.isEmpty(payByInformation?.CarrierCustomerCode) || carrierCustomerCode?.indexOf(payByInformation?.CarrierCustomerCode) < 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "party who pay the Ocean Freight should be provided"]
			errorKeyList.add(errorKey)
			return
		}
	}


	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingPODKDCode
	 * @author XIEMI
	 */
	void MissingPODKDCode(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(!util.notEmpty(current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode?.trim())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing POD KD Code - "]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingPOLKDCode
	 * @author XIEMI
	 */
	void missingPOLKDCode(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(!util.notEmpty(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode?.trim())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing POL KD Code - "]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingPORKDCode
	 * @author XIEMI
	 */
	void missingPORKDCode(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(!util.notEmpty(current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode?.trim())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing POR KD Code - "]
			errorKeyList.add(errorKey)
			return
		}
	}


	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissPODUNCode
	 * @author XIEMI
	 * @Tested True tested by file in PIER1
	 */
	void MissPODUNCode(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(!util.notEmpty(current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode?.trim())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing POD UN location code for " + "${current_Body?.Route?.LastPOD?.Port?.City?:""}+${current_Body?.Route?.LastPOD?.Port?.Country?:""}"]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissPOLUNCode
	 * @author XIEMI
	 * @Tested True tested by file in PIER1
	 */
	void missPOLUNCode(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(!util.notEmpty(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode?.trim())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing POL UN location code for " + "${current_Body?.Route?.FirstPOL?.Port?.City?:""}+${current_Body?.Route?.FirstPOL?.Port?.Country?:""}"]
			errorKeyList.add(errorKey)
			return
		}
	}

//	 MissPORUNCode: no errorMsg
	 
//	 MoreThanOneContainerInInput: no errorMsg
	 
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName MsFunDelete
	  * @author XIEMI
	  * @Tested True tested by file in CASSOCEANFRT
	  */
	 void msFunDelete(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		
		 if(current_Body?.TransactionInformation?.Action?.trim()?.toUpperCase()?.equals("DEL")){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Gen Info Message Function must not be DELETE"]
			 errorKeyList.add(errorKey)
			 return
		 }
	 }
	
	
		 
	 /**
	  * @param current_Body
	  * @param isError
	  * @param errorMsg
	  * @param errorKeyList
	  * @RuleName N7CntrrNumMissing
	  * @author XIEMI
	  * @Tested True tested by file in CASSOCEANFRT
	  */
	void n7CntrrNumMissing(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		 if( current_Body?.Container?.find { current_Container ->
			 !util.notEmpty(current_Container?.ContainerNumber) || !(current_Container?.ContainerNumber)}){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing container number"]
			 errorKeyList.add(errorKey)
			 return
		 }
	}
		 
		 
	/**
	 *
	 * @param current_Body
	 * @param maxLength
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param recordList
	 * @RuleName FilterByBLStatus
	 * @author XIEMI
	 */

	 void filterByBLStatus(cs.b2b.core.mapping.bean.bl.Body current_Body, int maxLength, boolean isError, String errorMsg,
		  List<Map<String,String>> errorKeyList, List<Map<String, String>> recordList){
		  Map<String,String> errorKey = null

		  def Status = current_Body?.BLStatus?.get(0)?.attr_Type?.equals("BL_STATUS") && current_Body?.BLStatus?.get(0)?.Status?.toUpperCase()

		  recordList.find { current_Record ->
			  def tempMap = current_Record;
			  if( tempMap.get('SEG_NUM').equals("Exception") &&  tempMap.get('CONFIG_TYPE').equals(LOOP_CTRL) ){
				  if(tempMap.get('ATTRIBUTE_KEY').contains("FilterStatus") ) {
					  tempMap.get('ATTRIBUTE_VALUE').toUpperCase().equals(Status){
						  errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : " status is NOT accepted"]
						  errorKeyList.add(errorKey)
						  return
					  }
				  }
			  }

		  }

	 }
		 

	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName N7CntrrNumOrM7SealNumMissing
	 * @author HEZE
	 * @Tested True tested by file in AVONELN
	 */
	void n7CntrrNumOrM7SealNumMissing(cs.b2b.core.mapping.bean.bl.Body current_Body,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		 
		 if(current_Body?.Container?.size() <= 0 || current_Body?.Container?.findAll{ StringUtil.isEmpty(it?.ContainerNumber) || it?.ContainerNumber == ""}?.size>0 ||
			 current_Body?.Container?.find{it?.Seal?.size()==0}!=null || current_Body?.Container?.findAll{it?.Seal?.findAll{ it?.SealNumber==""||StringUtil.isEmpty(it?.SealNumber)}}){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Both Container Number and Seal Number are required"]
			 errorKeyList.add(errorKey)
			 return
		 }
	}
			
			
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @param ActualDepartDate
	 * @param EstimateDepartDate
	 * @RuleName NoAmendmentsAfter4Day
	 * @author HEZE
	 * @Test true HONEYWELLUOP
	 */
	void noAmendmentsAfter4Day(Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		String BLChangeDT = util.convertDateTime(current_Body?.GeneralInformation?.BLChangeDT?.LocDT?.toString(), xmlDateTimeFormat, yyyyMMdd);
		if(current_Body?.Route?.OceanLeg?.size() > 0){
			ArrayList<cs.b2b.core.mapping.bean.DepartureDT> departureDTs = current_Body?.Route?.OceanLeg?.get(0)?.DepartureDT;
			String actualDepartDate = null;
			String estimateDepartDates = null;
			if(departureDTs.find {it?.attr_Indicator == 'A' && StringUtil.isNotEmpty(it?.LocDT?.toString())}){
				actualDepartDate = util.convertDateTime(departureDTs?.find {it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
			}
			if(departureDTs.find {it?.attr_Indicator == 'E' && StringUtil.isNotEmpty(it?.LocDT?.toString())}){
				estimateDepartDates = util.convertDateTime(departureDTs?.find {it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
			}

			if(current_Body?.EventInformation?.EventDescription?.trim() == 'UPDATE' && StringUtil.isNotEmpty(actualDepartDate?.trim())){
				if(BLChangeDT?.toBigDecimal()- actualDepartDate?.toBigInteger() > 4){
					errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Amendments are not accepted 4 days after original 310"]
					errorKeyList.add(errorKey);
					return
				}
			}else if(current_Body?.EventInformation?.EventDescription?.trim() == 'UPDATE' && StringUtil.isNotEmpty(estimateDepartDates?.trim())){
				if(BLChangeDT?.toBigDecimal() - estimateDepartDates?.toBigDecimal() > 4){
					errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Amendments are not accepted 4 days after original 310"]
					errorKeyList.add(errorKey);
					return
				}
			}
		}

	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NoAmendSent
	 * @author HEZE
	 */
	void noAmendSent(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		
		String upperCase_Action = null;
		if(null != current_Body && null != current_Body.getTransactionInformation() && null != current_Body.getTransactionInformation().getAction()){
			upperCase_Action = current_Body.getTransactionInformation().getAction().toUpperCase();
		}
		if("UPD".equals( upperCase_Action )){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No amendments can be sent"]
			errorKeyList.add(errorKey);
		}
		return;
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NoBLOnboardDTGMTandETD
	 * @author HEZE
     * @Tested True tested by file in EXPEDITOR
	 */
	void noBLOnboardDTGMTandETD( boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, cs.b2b.core.mapping.bean.bl.Body current_Body ){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		boolean flag_1 = false;
		boolean flag_2 = false;
		if( null!=current_Body?.Route?.OceanLeg && current_Body?.Route?.OceanLeg?.size()>0 ){
			Iterator<cs.b2b.core.mapping.bean.bl.OceanLeg> Iterator_OceanLeg = current_Body?.Route?.OceanLeg?.iterator()
			while(Iterator_OceanLeg.hasNext()){
				cs.b2b.core.mapping.bean.bl.OceanLeg current_OceanLeg = Iterator_OceanLeg.next()
						if(current_OceanLeg!=null && current_OceanLeg?.DepartureDT!=null && current_OceanLeg?.DepartureDT?.size()>0){
							Iterator<cs.b2b.core.mapping.bean.DepartureDT> it =current_OceanLeg.getDepartureDT().iterator();
							while( it.hasNext() ){
								cs.b2b.core.mapping.bean.DepartureDT departureDT =it.next();
								if( null != departureDT ){
									if( (departureDT?.attr_Indicator!=null && departureDT?.attr_Indicator=='E' && departureDT?.LocDT?.toString()!=null && departureDT?.LocDT?.toString()?.length()==0) ||
										(departureDT?.attr_Indicator!=null && departureDT?.attr_Indicator!='E') ||
										(departureDT?.attr_Indicator==null)  ){
										flag_1 = true;
									}else{
										flag_1 = false;
									}
									if(flag_1 == false){
										break;
									}
								}
							}
						}
						else{
							flag_1 = true;
						}
							break;
			}
		}else{
			flag_1 = true;
		}
		if( (current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.toString()!=null && current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.toString()?.length()==0) ||
			(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.toString()==null) ){
			flag_2 = true;
		}
		if( flag_1 && flag_2 ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BLOnboardDT and ETD are Missing"]
			errorKeyList.add(errorKey);
		}
		return;
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NoChargeCodeConvertedForPrepaidCharge
	 * @author HEZE
	 */
	void noChargeCodeConvertedForPrepaidCharge(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError,	String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		int count_FreightCharge = 0;
		if(null != current_Body && null != current_Body.getFreightCharge()){
			count_FreightCharge = current_Body.getFreightCharge().size(); 
		}
		if(0 == count_FreightCharge){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Prepaid Charge"]
			errorKeyList.add(errorKey);
		}
		return;
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NoL3
	 * @author WEIR
	 * @Tested True tested by file in HDYOW
	 */
	void noL3(boolean isError,String errorMsg,List<Map<String,String>> errorKeyList,cs.b2b.core.mapping.bean.bl.Body current_Body){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		if( current_Body?.Cargo?.find{StringUtil.isNotEmpty(it?.Packaging?.PackageQty) && it?.Packaging?.PackageQty != '0'}== null  && current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.equals("0")
			&& current_Body?.GeneralInformation?.BLNetWeight?.Weight?.equals("0") && current_Body?.GeneralInformation?.BLVolume?.Volume?.equals("0")){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Sum of Cargo package Quantity is Zero"]
			errorKeyList.add(errorKey);
		}
		return ;
	}
	
	
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NonCAinFND
	 * @Tested True tested by file in LFSS
	 */
	void nonCAinFND(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		if(current_Body?.Route?.FND?.CSStandardCity!= null && current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode!=null && current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode != 'CA'){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Must be inbound to Canada"]
			errorKeyList.add(errorKey);
		}
		return;
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NonCollectCharges
	 * @author HEZE
	 * @Tested True tested by file in CDLGTN
	 */
	void nonCollectCharges(boolean isError,String errorMsg,List<Map<String,String>> errorKeyList,cs.b2b.core.mapping.bean.bl.Body current_Body){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		boolean flag = false;
		int count_ChargeType = 0;
		if(null != current_Body && null != current_Body.getFreightCharge() && current_Body.getFreightCharge().size() > 0 ){
			Iterator<FreightCharge> it = current_Body.getFreightCharge().iterator();
			while( it.hasNext() ){
				if("0".equals( it.next().getChargeType() ) ){
					++count_ChargeType;
					break;
				}
			}
		}
		if( 0 == count_ChargeType ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Only collect charges are allowed"]
			errorKeyList.add(errorKey);
		}
		return;
	}
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param countryCode
	 * @RuleName nonSpecifiedPOL
	 * @Tested True tested by file in RAYONIERUSBANK
	 */
	void nonSpecifiedPOL(cs.b2b.core.mapping.bean.bl.Body current_Body,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList,String countryCode){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		
		if(current_Body?.Route?.FirstPOL?.Port?.CSCountryCode != countryCode){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Send only US-export shipments"]
			errorKeyList.add(errorKey);
		}
		return;
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NonUSCAinFND
	 * @author WEIR
	 * @Tested True tested by file in HDPOIN
	 */
	void nonUSCAinFND(boolean isError,String errorMsg,List<Map<String,String>> errorKeyList,cs.b2b.core.mapping.bean.bl.Body current_Body){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		List<String> appectedCountryName = ["CA","US"];
		if(StringUtil.isNotEmpty(current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode) && !appectedCountryName.contains( current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode?.toUpperCase())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Must be inbound to US or Canada"]
			errorKeyList.add(errorKey);
		}
		return;
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NonUSCAinPOD
	 * @author HEZE
	 * @Tested True tested by file in AMERICANSTD
	 */
	void nonUSCAinPOD( boolean isError, String errorMsg, List<Map<String,String>> errorKeyList,cs.b2b.core.mapping.bean.bl.Body current_Body){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		List<String> appectedCountryName = ["CA","US"];
	
		if(StringUtil.isNotEmpty(current_Body?.getRoute()?.getLastPOD()?.getPort()?.getCSCountryCode()) &&
			!appectedCountryName.contains( current_Body?.getRoute()?.getLastPOD()?.getPort()?.getCSCountryCode()?.toUpperCase())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Non US or CA shipments"]
			errorKeyList.add(errorKey);
		}
		return;
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NonUSCAinPORandFND
	 *
	 * @author HEZE
     * @Test true in file HONEYWELLDSG
	 */
	void nonUSCAinPORandFND(Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = new HashMap<String,String>();
		String fndCSCountryCode = StringUtil.isNotEmpty(current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode) ? current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode : ''
		String porCSCountryCode = StringUtil.isNotEmpty(current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode) ? current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode : ''
		
		if((fndCSCountryCode != 'US' && fndCSCountryCode != 'CA') && (porCSCountryCode != 'US' && porCSCountryCode != 'CA') && (fndCSCountryCode || porCSCountryCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "POR and FND are not US or Canada."+'POR - '+porCSCountryCode+' and FND - '+fndCSCountryCode]
			errorKeyList.add(errorKey);
			return
		}
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NonUSCAMXinPOD
	 * @author HEZE
	 */
	void nonUSCAMXinPOD(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = new HashMap<String,String>();
		boolean CSCountryCode_equal_to_US = true;
		boolean CSCountryCode_equal_to_CA = true;
		boolean CSCountryCode_equal_to_MX = true;
		if(null != current_Body && null != current_Body.getRoute() && null != current_Body.getRoute().getLastPOD() && null != current_Body.getRoute().getLastPOD().getPort()  && 
			null != current_Body.getRoute().getLastPOD().getPort().getCSCountryCode() ){
			String  CSCountryCode = current_Body.getRoute().getLastPOD().getPort().getCSCountryCode();
			if( !"US".equals(CSCountryCode) ){
				CSCountryCode_equal_to_US = false;
			}
			if(!"CA".equals(CSCountryCode) ){
				CSCountryCode_equal_to_CA = false;
			}
			if( !"MX".equals(CSCountryCode) ){
				CSCountryCode_equal_to_MX = false;
			}
		}
		if( (!CSCountryCode_equal_to_US) && (!CSCountryCode_equal_to_CA) && (!CSCountryCode_equal_to_MX) ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Not Import shipment to NA"]
			errorKeyList.add(errorKey);
		}
		return;
	}
	
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NonUSinFND
	 * @author WEIR
	 * @Tested True tested by file in HDYOW
	 */
	void nonUSinFND(boolean isError,String errorMsg,List<Map<String,String>> errorKeyList,cs.b2b.core.mapping.bean.bl.Body current_Body){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		if(current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode){
			def country = current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode?.toUpperCase();
			List<String> US = ["US"];
			if( !US.contains(country) ){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "FND not US"]
				errorKeyList.add(errorKey)
			}
			return ;
		}
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName NonUSinPOD
	 * @author HEZE
	 * @Tested True tested by file in WSIGTN
	 */
	void NonUSinPOD(boolean isError,String errorMsg,List<Map<String,String>> errorKeyList,cs.b2b.core.mapping.bean.bl.Body current_Body){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		
		boolean CSCountryCode_equal_to_US = true;
		if(null != current_Body && null != current_Body.getRoute() && null != current_Body.getRoute().getLastPOD() && null != current_Body.getRoute().getLastPOD().getPort() && null != current_Body.getRoute().getLastPOD().getPort().getCSCountryCode()){
			String CSCountryCode = current_Body.getRoute().getLastPOD().getPort().getCSCountryCode();
			if(! "US".equals( CSCountryCode )){
				CSCountryCode_equal_to_US = false;
			}
		} 
		if(false == CSCountryCode_equal_to_US){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "POD country is not US"]
			errorKeyList.add(errorKey)
		}
		return ;
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @author HEZE
	 * @Tested True tested by BL_common_Util_Unittest method: test_PartyTypeMissing()
	 */
	void partyTypeMissing(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		Boolean exists_PartyType_where  = false;
		if( null != current_Body && null != current_Body.getParty() ){
			List<Party> partys = current_Body.getParty();
			if( null != partys && partys.size() > 0 ){
				Iterator<Party> it = partys.iterator();
				while( it.hasNext() ){
					Party party = it.next();
					if( null!=party && null!=party?.getPartyName()){
						if( party?.getPartyName()?.length() == 0 ){
							exists_PartyType_where = true;
							break;
						}
					}
				}
			}
		}
		if( exists_PartyType_where ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Party name qualifier is missing"]
			errorKeyList.add(errorKey);
		}
		return ;
	}
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName NonUSinPOR
	 * @author HEZE
	 */
	void NonUSinPOR(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		if( "US" != current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Not US export shipment"]
			errorKeyList.add(errorKey)
		}
		return ;
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @param resultSetList (the key of the map,the column Name of the table,upper-case)
	 * @RuleName PORCityCountryFilter
	 * @author HEZE
	 * @Tested True tested by file in CASSOCEANFRTEU
	 */
	void pORCityCountryFilter(boolean isError,String errorMsg,List<Map<String,String>> errorKeyList,cs.b2b.core.mapping.bean.bl.Body current_Body,List<Map<String,String>> resultSetList){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		def result = true;
		if(null != resultSetList && resultSetList.size() > 0){
			resultSetList?.each{ cu_map ->
				if(cu_map?.get(current_Body?.Route?.POR?.CityDetails?.City?.toUpperCase()) == current_Body?.Route?.POR?.CityDetails?.Country?.toUpperCase()){
					result = false
				}
			}
		}
		if(result || StringUtil.isEmpty(current_Body?.Route?.POR?.CityDetails?.City) || StringUtil.isEmpty(current_Body?.Route?.POR?.CityDetails?.Country)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "POR City or Country no expected"]
			errorKeyList.add(errorKey)
		}
		return
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName PrePaid
	 * @author HEZE
     * @Tested True tested by file in EMERSONEXPORT
	 */
	void prePaid( cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		Boolean boolean_exists_FreightCharge_where = false ;
		if( null != current_Body && null != current_Body.getFreightCharge()  ){
		 	List<FreightCharge> FreightCharge_list = current_Body.getFreightCharge();
			Iterator<FreightCharge> it = FreightCharge_list.iterator();
			while ( it.hasNext() ) {
				FreightCharge freightcharge = it.next();
				if( "1".equals( freightcharge.getChargeType() )  ){
					boolean_exists_FreightCharge_where = true ;
				}
			}
		}
		
		if( !boolean_exists_FreightCharge_where ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Prepaid Charge"]
			errorKeyList.add(errorKey)
		}
		return
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName PrepaidCharge1NegativeFilter
	 * @author HEZE
	 * @Test true by CUMMINSCASS
	 */
	void prepaidCharge1NegativeFilter(cs.b2b.core.mapping.bean.bl.Body current_Body,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		Map<String, String> errorKey = null;
		def expectedTotalAmtInPmtCurrency = current_Body?.FreightCharge?.find{ it?.ChargeType == PREPAID && StringUtil.isNotEmpty(it?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)}
		if( current_Body?.FreightCharge?.findAll{it?.ChargeType == PREPAID}?.size() == 1  && expectedTotalAmtInPmtCurrency && expectedTotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toBigDecimal()<0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Only 1 Prepaid Charge and it is negative."]
			errorKeyList.add(errorKey)
			return
		}
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName SendStatusNull
	 * @author HEZE
	 * @Tested True tested by file in CASSOCEANFRTEU
	 */
	void sendStatusNull(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = new HashMap<String,String>();
		boolean flag_1 , flag_2;
		flag_1 = false;
		flag_2 = false;
		if(null != current_Body && null != current_Body.getEventInformation() && null != current_Body.getEventInformation().getEventDescription() ){
			String eventDescriptionStr = current_Body.getEventInformation().getEventDescription();
			if("" .equals(eventDescriptionStr)){
				flag_1 = true;
			}
		}else{// 02
			flag_2 = true;
		}
		if(  flag_1 || flag_2 ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "no Send Status of EDI in file"]
			errorKeyList.add(errorKey)
		}
		return
	}
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName SendStatusOfEDI
	 * @Tested True tested by file in TRANSAMERICAN
	 */
	void sendStatusOfEDI(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError,String errorMsg,List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null
		String tmp_EventDescription =  current_Body?.EventInformation?.EventDescription;
		if(StringUtil.isEmpty(tmp_EventDescription) || (StringUtil.isNotEmpty(tmp_EventDescription) && tmp_EventDescription != 'UPDATE' && tmp_EventDescription != 'NEW' )){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Send Status of EDI"]
			errorKeyList.add(errorKey)
		}
		return
	}
	
		
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_Body
	 * @RuleName SendStatusOfEDIChecker
	 * @author HEZE
	 * @Tested True tested by file in AMERICANSTD
	 */
	void sendStatusOfEDIChecker(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		String eventDescriptionStr = '';
		if( null != current_Body && null != current_Body.getEventInformation() && null != current_Body.getEventInformation().getEventDescription() ){
			eventDescriptionStr = current_Body.getEventInformation().getEventDescription();
		}
		if(eventDescriptionStr != null && 'NEW' != eventDescriptionStr ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Amendments is send"]
					errorKeyList.add(errorKey)
		}
		
		return
	}
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param maxlength
	 * @RuleName TooLongConcatAddressLineCityStateCountry
	 * @author HEZE
	 * @Tested True tested by file in PIER1
	 */
	void tooLongConcatAddressLineCityStateCountry(cs.b2b.core.mapping.bean.bl.Body current_Body,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList,int maxlength){

		Map<String,String> errorKey = null
		current_Body?.Party?.each{ current_Party->
			def city = ""
			def state = ""
			def postalCode = ""
			def country = ""
			def cu_PartyAddress = current_Party?.Address
			if(cu_PartyAddress?.City){
				city = cu_PartyAddress?.City
			}
			if(cu_PartyAddress?.State){
				state = cu_PartyAddress?.State
			}
			if(cu_PartyAddress?.PostalCode){
				postalCode = cu_PartyAddress?.PostalCode
			}
			if(cu_PartyAddress?.Country){
				country = cu_PartyAddress?.Country
			}
			def vAddressLines = ''
			current_Party?.Address?.AddressLines?.AddressLine?.each{current_addressLine ->
				vAddressLines = vAddressLines + ' ' + current_addressLine
			}
			
			def contactAddress = "${vAddressLines} ${city} ${state} ${postalCode} ${country}"
			
			List<String> vAddressArray = SplitTextWithConnector(contactAddress?.trim(),55)
			
			if( contactAddress?.length()  > maxlength ||  vAddressArray?.size() > 4){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "After Concat AddressLine, City, State, Postal Code, Country is over than " + maxlength +" " + contactAddress]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	/**
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param extCde
	 * @RuleName VeriifyBySapIDKNPRODNY
	 * @author HEZE
	 */
	void veriifyBySapIDKNPRODNY( boolean isError, String errorMsg, List<Map<String,String>> errorKeyList, String extCde ){
		Map<String,String> errorKey = null
		if( null != extCde  && "Expected".equals(extCde) ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Invalid KNN shipment"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param expectedCarrierCustomerCodeList
	 * @param findPartyType
	 * @Tested True tested by file in LAMPSPLUS
	 */
	void veriifyBySapIDLAMPSPLUS(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError,String errorMsg,List<Map<String,String>> errorKeyList,List<String> expectedCarrierCustomerCodeList, String findPartyType){
	
		Map<String,String> errorKey = null
		current_Body?.Party?.findAll{it?.PartyType == findPartyType}?.each{ current_Party->
			if(!expectedCarrierCustomerCodeList.contains(current_Party?.CarrierCustomerCode)){
				errorKey = [ TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Invalid Lamps Plus shipment"]
				errorKeyList.add(errorKey)
				return
			}
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * check container missing or not
	 */
	void missingContainer(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(current_Body?.Container == null || current_Body?.Container?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Container."]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param checkingIndex if null, then checking all container if exists one missing number, else, check to specified one
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * check container number missing or not
	 */
	void missingContainerNumber(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, Integer checkingIndex, String errorMsg,  List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(checkingIndex == null){
			if(current_Body?.Container?.findAll{StringUtil.isEmpty(it.ContainerNumber)}?.size() > 0){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Container Number."]
				errorKeyList.add(errorKey)
			}
		}else{
			if(StringUtil.isEmpty(current_Body?.Container[checkingIndex]?.ContainerNumber)){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Container[$checkingIndex] Number."]
				errorKeyList.add(errorKey)
			}
		}
	}

	/**
	 * @param freightChargeCNTRList
	 * @param isError
	 * @param currentContainerNumber
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 */
	void missingFreightChargeCNTRWithCurrentContainerNumber(List<FreightChargeCNTR> freightChargeCNTRList, String currentContainerNumber, boolean isError, String errorMsg,  List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(freightChargeCNTRList.findAll{it.ContainerNumber == currentContainerNumber}?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Container[${currentContainerNumber}] does not have their belonging Freight Charge."]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckCarrierBookingNumber - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkCarrierBookingNumber(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		//not(exists(  $ParseNormolizedCS2XML/root/ns1:Body-BillOfLading/ns1:GeneralInformation-Body-BillOfLading/ns1:CarrierBookingNumber-BLInformationType  ))
		Map<String,String> errorKey = null
		if(current_Body.GeneralInformation.CarrierBookingNumber == null || current_Body.GeneralInformation.CarrierBookingNumber?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing CarrierBookingNumber,(BookingInformation mandatoty and loopbyBookingNumber)"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckCertificationClauseTextLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkCertificationClauseTextLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.BLCertClause?.size() > 0 && current_Body.BLCertClause?.findAll{it?.CertificationClauseText?.length() > maxLength}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of CertificationClauseText over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckContainerTrafficModeInboundLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkContainerTrafficModeInboundLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.Container?.size() > 0 && current_Body.Container?.findAll{it?.TrafficMode?.InBound?.length() > maxLength}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of Container TrafficMode Inbound over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckContainerTrafficModeOutboundLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkContainerTrafficModeOutboundLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.Container?.size() > 0 && current_Body.Container?.findAll{it?.TrafficMode?.OutBound?.length() > maxLength}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of Container TrafficMode Outbound over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckDangerousCargoIMDGPageLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkDangerousCargoIMDGPageLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.Cargo?.findAll{it.DGCargoSpec?.size() > 0}?.size() > 0 && current_Body.Cargo?.findAll{it?.DGCargoSpec?.findAll{it.IMDGPage?.length() > maxLength}?.size() > 0}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of IMOPage over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckDangerousCargoIMOClassLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkDangerousCargoIMOClassLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.Cargo?.findAll{it.DGCargoSpec?.size() > 0}?.size() > 0 && current_Body.Cargo?.findAll{it?.DGCargoSpec?.findAll{it.IMOClass?.length() > maxLength}?.size() > 0}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of IMOClass over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckOceanLegDischargeVesselNameLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkOceanLegDischargeVesselNameLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.Route?.OceanLeg?.size() > 0 && current_Body.Route?.OceanLeg?.findAll{it?.SVVD?.Discharge?.VesselName?.length() > maxLength}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of DischargeVesselName over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckOceanLegLoadingVesselNameLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkOceanLegLoadingVesselNameLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.Route?.OceanLeg?.size() > 0 && current_Body.Route?.OceanLeg?.findAll{it?.SVVD?.Loading?.VesselName?.length() > maxLength}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of LoadingVesselName over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckReeferCargoSensitiveCargoDescLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkReeferCargoSensitiveCargoDescLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.Cargo?.findAll{it.ReeferCargoSpec?.size() > 0}?.size() > 0 && current_Body.Cargo?.findAll{it?.ReeferCargoSpec?.findAll{it.SensitiveCargoDesc?.length() > maxLength}?.size() > 0}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of ReeferCargoSensitiveCargoDesc is over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckStopOffPickupDetailsFacilityNameLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkStopOffPickupDetailsFacilityNameLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.Route?.StopOff?.size() > 0 && current_Body.Route?.StopOff?.findAll{it?.PickupDetails?.Facility?.FacilityName?.length() > maxLength}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of StopOffPickupDetailsFacilityName is over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 * @param current_Body
	 * @param isError
	 * @param maxLength
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckStopOffReturnDetailsFacilityNameLength - BLXML
	 * @Tested test by file in OOCLLOGISTICS
	 */
	void checkStopOffReturnDetailsFacilityNameLength(cs.b2b.core.mapping.bean.bl.Body current_Body, Integer maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body.Route?.StopOff?.size() > 0 && current_Body.Route?.StopOff?.findAll{it?.ReturnDetails?.Facility?.FacilityName?.length() > maxLength}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of StopOffPickupDetailsFacilityName is over than ${maxLength}"]
			errorKeyList.add(errorKey)
		}
	}
	
	/**
	 * @param current_Body
	 * @param isError
	 * @param blockedBLStatusList
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule CheckBLStatus - BLXML
	 * @Tested test by file in WGLL
	 */
	void checkBLStatus(cs.b2b.core.mapping.bean.bl.Body current_Body, List<String> blockedBLStatusList, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(blockedBLStatusList.contains(current_Body.BLStatus?.find{it?.attr_Type == 'BL_STATUS'}?.Status)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "${current_Body.BLStatus?.find{it?.attr_Type == 'BL_STATUS'}?.Status} status NOT accepted"]
			errorKeyList.add(errorKey)
		}
	}

	/**
	 *
	 * @param current_Body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @author RENGA
	 * @Rule InvalidCollectCharge_WALGREENSCASS
	 */
	void invalidCollectCharge_WALGREENSCASS(cs.b2b.core.mapping.bean.bl.Body current_Body, List<String> blockChargeCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body?.FreightCharge?.findAll{(!it?.CalculateMethod?.startsWith("2")) && (!it?.CalculateMethod?.startsWith("4")) &&
				(!it?.CalculateMethod?.startsWith("C")) && it?.ChargeType == COLLECT && blockChargeCode?.contains(it?.ChargeCode)}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Walgreens via CASS BL# ${current_Body?.GeneralInformation?.BLNumber} with non-containerized collect charges"]
			errorKeyList.add(errorKey)

			//TODO send mail
		}
	}

	//prep checking end

		
	//posp checking start
	 /**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName B3Checker
	 * @Tested True tested by BL_common_Util_Unittest method:test_checkB3()
	 */
	void checkB3(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 
		 Map<String,String> errorKey = null
		 
		 if(StringUtil.isEmpty(root?.Loop_ST?.B3?.E145_03?.text())){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No B303 -  (SID) is missing."]
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
	 * @RuleName CADUSD
	 * @Tested True tested by file in EMERSONEXPORT
	 */
	void checkCADUSD(cs.b2b.core.mapping.bean.bl.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 
		 Map<String,String> errorKey = null
		 if((['CA','US'].contains(root?.Loop_ST?.Loop_R4?.find{it.R4.E115_01.text() == 'L'}?.R4?.E26_05?.text()) && current_Body?.FreightCharge?.findAll{it?.ChargeType == PREPAID &&
			 it?.TotalAmtInPmtCurrency?.attr_Currency!=null && !['CAD','USD'].contains(it?.TotalAmtInPmtCurrency?.attr_Currency) }?.size() > 0) ||
			 (current_Body?.FreightCharge?.findAll{it?.ChargeType == PREPAID && it?.TotalAmtInPmtCurrency?.attr_Currency == 'CAD'}?.size() > 0 && current_Body?.FreightCharge?.findAll{it?.ChargeType == PREPAID && it?.TotalAmtInPmtCurrency?.attr_Currency == 'USD'}?.size() > 0)){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Charges can only be CAD or USD, and will error if prepaid currencies are both CAD and USD."]
			 errorKeyList.add(errorKey)
			 return
		 }
	 }
	 
	 /**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested True tested by BL_common_Util_Unittest method: checkChargeCode999()
	 */
	void checkChargeCode999(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_L1?.findAll{it.L1.E150_08?.text() == '999'}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "ChargeCode Can Not be converted."]
			errorKeyList.add(errorKey)
			return
		}
	 }
	 
	 /**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
     * @Tested True tested by BL_common_Util_Unittest method: test_ChargeCodeNotConverted()
	 */
	void checkChargeCodeNotConverted(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_L1?.findAll{it?.L1?.E150_08?.size()>0 && it?.L1?.E150_08?.text()?.startsWith("NaN")}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Charge Code unconverted."]
			errorKeyList.add(errorKey)
			return
		}
	 }
	 
	 /**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested True tested by file in CATERPILLARGTN
	 */
	void checkChargeNotUSDCATERPILLARGTN(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_L1?.findAll{StringUtil.isNotEmpty(it?.C3?.E100_01?.text()) && it?.C3?.E100_01?.text() != "USD"}?.size() > 0 ||
			root?.Loop_ST?.Loop_LX?.findAll{it?.Loop_N7?.find{it?.Loop_L1?.find{StringUtil.isNotEmpty(it?.C3?.E100_01?.text()) && it?.C3?.E100_01?.text() != "USD"}}}?.size() > 0 ||
			root?.Loop_ST?.Loop_LX?.findAll{it?.Loop_L0?.find{it?.Loop_L1?.find{StringUtil.isNotEmpty(it?.C3?.E100_01?.text()) && it?.C3?.E100_01?.text() != "USD"}}}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Charge currency should only be USD."]
			errorKeyList.add(errorKey)
			return
		}
	 }
	 
	 /**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested True tested by file in BESTBUYD2L
	 */
	void checkCollectPayment(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.B3?.E193_07?.text() == "0"){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Only collect charge will be sent."]
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
	 * @RuleName CurrencyCheckingForUSCA
	 * @Tested True tested by file in VANPORTUSBANK
	 */
	void checkCurrencyCheckingForUSCA(cs.b2b.core.mapping.bean.bl.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	 
		Map<String,String> errorKey = null
	
		String chargeType = ""

		if(root?.Loop_ST?.Loop_R4?.find{it?.R4?.E115_01?.text() == 'D' && (it?.R4?.E26_05?.text() == 'US' || it?.R4?.E26_05?.text() == 'CA')}){
			chargeType = COLLECT
		}else if(root?.Loop_ST?.Loop_R4?.find{it?.R4?.E115_01?.text() == 'L' && (it?.R4?.E26_05?.text() == 'US' || it?.R4?.E26_05?.text() == 'CA')}){
			chargeType = PREPAID
		}

		if(chargeType != "" && 	(current_Body.FreightChargeCNTR.find{it.ChargeType == chargeType && it.TotalAmtInPmtCurrency?.attr_Currency != 'USD'} ||
				current_Body.FreightCharge.find{it.ChargeType == chargeType && it.TotalAmtInPmtCurrency?.attr_Currency != 'USD' && it.CalculateMethod == "Lump Sum"})){
			if(chargeType == '0'){
                errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg :  "Collect Charges for Import, All show flag currency should be USD."]
            }else{
                errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg :  "Prepaid Charges for Export, All show flag currency should be USD."]

            }
			errorKeyList.add(errorKey)
			return
		}
	}
 
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested True tested by BL_common_Util_Unittest method: testH1_FlashPoint()
	 */
	void checkH1_FlashPoint(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	 
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.findAll{it.Loop_L0.find{it.Loop_H1.find{it.H1.E77_07.text()?.replace("+", "")?.replace("-", "")?.length() > 3}}}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Flashpoint's length max.3"]
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
	 */
	void checkInvalidL117_KOHLS(cs.b2b.core.mapping.bean.bl.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_L1?.find{it.L1.E220_17.text() != '1'}?.size() > 0 ||
			root?.Loop_ST?.Loop_LX?.find{it.Loop_L0.find{it.Loop_L1?.find{it.L1.E220_17.text() != '1'}}}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Below BL number found Pro Rate charges BL number:" + current_Body.GeneralInformation.BLNumber]
			errorKeyList.add(errorKey)
			return
		}
			
		//Send email if error?  
	}
	
	/**
	 * @param root -- ediXml's root node
	 * @param maxCount
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L0max
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkL0Max(Node root, int maxCount, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		root?.Loop_ST?.Loop_LX?.each{currentLX ->
			if(currentLX.Loop_L0?.size() > maxCount){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L0 Max is  " + maxCount]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L102_L103_M
	 * @Tested True tested by file in CATERPILLARCASS
	 */
	void checkL102_L103_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_L1?.find{StringUtil.isEmpty(it.L1.E60_02.text()) || StringUtil.isEmpty(it.L1.E122_03.text())}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L102 and L103 are mandatory."]
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
	 * @RuleName L108_Missed_UnAccepted
	 * @Tested True tested by file in BESTBUYD2L
	 */
	void checkL108_Missed_UnAccepted(cs.b2b.core.mapping.bean.bl.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null

		if(root?.Loop_ST?.Loop_L1?.findAll{StringUtil.isEmpty(it.L1.E150_08.text()) || it.L1.E150_08.text() == '999'}?.size() > 0 ||
		current_Body.FreightCharge.findAll{it.ChargeType == COLLECT && StringUtil.isEmpty(it.ChargeCode)}?.size() > 0){

			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Charge code is missing."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param maxCount
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L1max
	 * @Tested True tested by file in ACUHASBRO
	 */
	void checkL1max(Node root, int maxCount, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.findAll{it?.Loop_N7?.find{it?.Loop_L1?.size() > maxCount}}?.size() > 0 ||
			root?.Loop_ST?.Loop_LX?.findAll{it?.Loop_L0?.find{it?.Loop_L1?.size() > maxCount}}?.size() > 0 || 
			root?.Loop_ST?.Loop_L1?.size() > maxCount){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L1 segment more than " + maxCount]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root -- ediXml's root node
	 * @param maxLength
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L1_02_MaxLen, checkL102L106L307, L102L106L307Checker-ACUCUBE, L102L106L307Checker-GTN
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkL102Length(Node root, int maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_L1?.find{it.L1?.E60_02?.text()?.replace(".","")?.length() > maxLength}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L102's length max." + maxLength]
			errorKeyList.add(errorKey)
			return
		}
		
		if(root?.Loop_ST?.Loop_LX?.find{it?.Loop_N7?.find{it?.Loop_L1?.find{it?.L1?.E60_02?.text()?.replace(".","")?.length() > maxLength}} != null}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L102's length max is " + maxLength]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root -- ediXml's root node
	 * @param maxLength
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L1_02_MaxLen   //  this method the field compared will include the point
	 * @Tested True tested by file in DUMMY310BLc
	 */
	void checkL102LengthWithPoint(Node root, int maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_L1?.find{it.L1?.E60_02?.text()?.length() > maxLength}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L102's length max." + maxLength]
			errorKeyList.add(errorKey)
			return
		}

		if(root?.Loop_ST?.Loop_LX?.find{it?.Loop_N7?.find{it?.Loop_L1?.find{it?.L1?.E60_02?.text()?.length() > maxLength}} != null}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L102's length max is " + maxLength]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param maxLength
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L1_04_MaxLen
	 * @Tested True tested by file in DUMMY310BLc
	 */
	void checkL104Length(Node root, int maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_L1?.find{it.L1?.E58_04?.text()?.replace(".","")?.length() > maxLength}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L104's length max is " + maxLength]
			errorKeyList.add(errorKey)
			return
		}

		if(root?.Loop_ST?.Loop_LX?.find{it?.Loop_N7?.find{it?.Loop_L1?.find{it?.L1?.E58_04?.text()?.replace(".","")?.length() > maxLength}} != null}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L104's length max is " + maxLength]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root -- ediXml's root node
	 * @param maxLength
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L1_06_MaxLen, checkL102L106L307, L102L106L307Checker-ACUCUBE, L102L106L307Checker-GTN
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkL106Length(Node root, int maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null

		if(root?.Loop_ST?.Loop_L1?.find{it.L1?.E117_06?.text()?.replace(".","")?.length() > maxLength}){
			String errorData = ""
			root?.Loop_ST?.Loop_L1?.findAll{it.L1?.E117_06?.text()?.replace(".","")?.length() > maxLength}.each {
				errorData = errorData + it.L1?.E117_06?.text() + ", "
			}
			errorData = errorData.substring(0, errorData.length() - 2)

			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L106's length max." + maxLength + " [Invalid Prepaid Charge Amt - " + errorData + "]"]
			errorKeyList.add(errorKey)
			return
		}

		if(root?.Loop_ST?.Loop_LX?.find{it?.Loop_N7?.find{it?.Loop_L1?.find{it?.L1?.E117_06?.text()?.replace(".","")?.length() > maxLength}} != null}){
			String errorData = ""
			root?.Loop_ST?.Loop_LX?.findAll{it?.Loop_N7?.findAll{it?.Loop_L1?.findAll{it?.L1?.E117_06?.text()?.replace(".","")?.length() > maxLength}} != null}.each { current_Loop_LX ->
				current_Loop_LX?.Loop_N7?.each{ current_Loop_N7 ->
					current_Loop_N7?.Loop_L1?.each{ current_Loop_L1 ->
						if(current_Loop_L1?.L1?.E117_06?.text()?.replace(".","")?.length() > maxLength){
							errorData = errorData + current_Loop_L1?.L1?.E117_06?.text()+ ", "
						}
					}
				}

			}
			errorData = errorData.substring(0, errorData.length() - 2)
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L106's length max." + maxLength + " [Invalid CNTR Prepaid Charge Amt - " + errorData + "]"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root -- ediXml's root node
	 * @param maxLength
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L1_06_MaxLen
	 * @Tested True tested by file in CANONDSG
	 */
	void checkL106LengthWithPoint(Node root, int maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null

		if(root?.Loop_ST?.Loop_L1?.find{it.L1?.E117_06?.text()?.length() > maxLength}){
			String errorData = ""
			root?.Loop_ST?.Loop_L1?.findAll{it.L1?.E117_06?.text()?.length() > maxLength}?.each {
				errorData = errorData + it.L1?.E117_06?.text() + ", "
			}
			errorData = errorData.substring(0, errorData.length() - 2)

			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L106's length max." + maxLength + " [Invalid Prepaid Charge Amt - " + errorData + "]"]
			errorKeyList.add(errorKey)
			return
		}

		if(root?.Loop_ST?.Loop_LX?.find{it?.Loop_N7?.find{it?.Loop_L1?.find{it?.L1?.E117_06?.text()?.length() > maxLength}} != null}){
			String errorData = ""
			root?.Loop_ST?.Loop_LX?.findAll{it?.Loop_N7?.findAll{it?.Loop_L1?.findAll{it?.L1?.E117_06?.text()?.length() > maxLength}} != null}?.each { current_Loop_LX ->
				current_Loop_LX?.Loop_N7?.each{ current_Loop_N7 ->
					current_Loop_N7?.Loop_L1?.each{ current_Loop_L1 ->
						if(current_Loop_L1?.L1?.E117_06?.text()?.length() > maxLength){
							errorData = errorData + current_Loop_L1?.L1?.E117_06?.text()+ ", "
						}
					}
				}

			}
			errorData = errorData.substring(0, errorData.length() - 2)
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L106's length max is " + maxLength + " [Invalid CNTR Prepaid Charge Amt - " + errorData + "]"]
			errorKeyList.add(errorKey)
			return
		}
	}


	/**
	 * @param root -- ediXml's root node
	 * @param maxLength
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L3_07_MaxLen, checkL102L106L307, L102L106L307Checker-ACUCUBE, L102L106L307Checker-GTN
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkL307Length(Node root, int maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.L3?.E117_07?.text()?.length() > maxLength){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L307's length max." + maxLength]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root -- ediXml's root node
	 * @param maxLength
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName  L102L106L307Checker-GTN
	 */
	void checkL102L106L307Checker_GTN(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.B3?.E140_11?.text() != 'OOLU'){
			if(root?.Loop_ST?.Loop_L1?.find{it?.L1?.E60_02?.text()?.length() > 9 || it?.L1?.E117_06?.text()?.length() > 12} || root?.Loop_ST?.L3?.E117_07?.text()?.length() > 9){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of L307,L102,L106 is over."]
				errorKeyList.add(errorKey)
				return
			}
			
			if(root?.Loop_ST?.Loop_LX?.Loop_N7?.Loop_L1?.find{ it?.L1?.E60_02?.text()?.length() > 9 || it?.L1?.E117_06?.text()?.length() > 9 || t?.L1?.E120_07?.text()?.length() > 9}){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of L307,L102,L106 is over."]
				errorKeyList.add(errorKey)
				return
			}
			
			if(root?.Loop_ST?.Loop_LX?.Loop_L0?.Loop_L1?.find{ it?.L1?.E60_02?.text()?.length() > 9 || it?.L1?.E117_06?.text()?.length() > 9 || it?.L1?.E120_07?.text()?.length() > 9}){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of L307,L102,L106 is over."]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
	/**
	 * @param root
	 * @param maxCount
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @param current_body
	 * @param tmp_CarrierCustomerCode_list
	 */
	void checkMissingFWDRefNum_EXPEDITOR(Node root, int maxCount, boolean isError, String errorMsg,
	List<Map<String,String>> errorKeyList,cs.b2b.core.mapping.bean.bl.Body current_body,List<String> tmp_CarrierCustomerCode_list){
		//TODO email not done
		Map<String,String> errorKey = null;
		if(root?.Loop_ST?.B3?.E76_02?.size()>0 ){
			String BLNumber_number = current_body?.GeneralInformation?.BLNumber
			String POL_PortName_param = current_body?.Route?.FirstPOL?.Port?.PortName
			boolean ForwarderReferenceNum_isExistsFWDnum = (root?.Loop_ST?.N9?.find{it?.E128_01?.size()>0 && it?.E128_01?.text()=='FN'}!=null) ;
			List<String> tmp_PartyType_list = ['NPT','ANP']
			int SkipValidation_occurs = 0;
			int NonNPTANPHas_occurs = 0;
			current_body?.Party?.each {
				String tmp_CCC_CarrierCustomerCode = null;
				String tmp_Valid = null;
				if(it?.PartyType!=null && tmp_PartyType_list.contains(it?.PartyType)){
					String tmp_TPID;
					tmp_CCC_CarrierCustomerCode = it?.CarrierCustomerCode
					if(tmp_CarrierCustomerCode_list.contains(tmp_CCC_CarrierCustomerCode)){
						++SkipValidation_occurs;
					}
				}else if(it?.PartyType!=null && !tmp_PartyType_list.contains(it?.PartyType)){
					if(tmp_CarrierCustomerCode_list.contains(tmp_CCC_CarrierCustomerCode)){
						++NonNPTANPHas_occurs;
					}
				}
			}
			if(ForwarderReferenceNum_isExistsFWDnum == false){
				if(NonNPTANPHas_occurs==0 && SkipValidation_occurs>0){
				}else{
					errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Forwarder Reference Number"]
					errorKeyList.add(errorKey)
					return
				}
			}
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingL1andL1_1, MissingPer_Container_L1andL1_1
	 * @Tested True tested by BL_common_Util_Unittest method: checkMissingL1andL1_1()
	 */
	void checkMissingL1andL1_1(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_L1?.size() == 0 && root?.Loop_ST?.Loop_LX?.Loop_L0?.Loop_L1?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either L1.1 nor L1.2 are empty."]
			errorKeyList.add(errorKey)
			return
		}
		
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingL1_2
	 * @Tested True tested by file in HDPOIN
	 */
	void checkMissingL1_2(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_L1?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No L1_2."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param maxCount
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingL1_2CATERPILLARGTN
	 * @Tested True tested by file in CATERPILLARGTN
	 */
	void checkMissingL1_2CATERPILLARGTN(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_L1?.size() == 0  && root?.Loop_ST?.Loop_LX?.Loop_L0?.Loop_L1?.size() == 0 && root?.Loop_ST?.Loop_LX?.Loop_N7?.Loop_L1?.size() == 0 &&
			root?.Loop_ST?.findAll{it?.Loop_R4?.find{(it?.R4?.E115_01?.text() == 'D' || it?.R4?.E115_01?.text() == 'E' || it?.R4?.E115_01?.text() == 'R' || it?.R4?.E115_01?.text() == 'L') && it?.R4?.E26_05?.text() == 'US'}}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No LoopL1.2 in EDI to customer."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingL1_2_CUMMINSCASS
	 * @Test true by CUMMINSCASS
	 *
	 */
	void checkMissingL1_2_CUMMINSCASS(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_L1?.size() == 0 && root?.Loop_ST?.find{it?.Loop_R4?.find{(it?.R4?.E115_01?.text() == 'L') && (it?.R4?.E26_05 && it?.R4?.E26_05?.text() != 'US')}}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "POL is US and No PrePaid Charge send or FND is US and No Collect Charge."]
			errorKeyList.add(errorKey)
			return
		}else if(root?.Loop_ST?.Loop_L1?.size() == 0 && root?.Loop_ST?.find{it?.Loop_R4?.find{(it?.R4?.E115_01?.text() == 'E') && (it?.R4?.E26_05 && it?.R4?.E26_05?.text() != 'US')}}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "POL is US and No PrePaid Charge send or FND is US and No Collect Charge."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingL3_11_80
	 * @Tested True tested by BL_common_Util_Unittest method: checkMissingL3_11_80()
	 */
	void checkMissingL3_11_80(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if((StringUtil.isEmpty(root?.Loop_ST?.L3?.E80_11?.text()))){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Mandatory L3."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingLoopLXLoopL0L0
     * @Tested True tested by BL_common_Util_Unittest method: checkMissingLoopLXLoopL0L0()
	 */
	void checkMissingLoopLXLoopL0L0(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.Loop_L0?.L0?.size() == 0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "LoopLX/LoopL0/L0 is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingLoopN1N1
	 * @Tested True tested by BL_common_Util_Unittest method: checkMissingLoopN1N1()
	 */
	void checkMissingLoopN1N1(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_N1?.N1?.size() == 0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "LoopN1/N1 is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingLX
	 * @Tested True by file in SDV
	 */
	void checkMissingLX(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_LX?.size() == 0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Loop LX cannot be genertaed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingN7
	 * @Tested True tested by BL_common_Util_Unittest method: checkMissingN7()
	 */
	void checkMissingN7(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.Loop_N7?.N7?.size() == 0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "LoopLX/LoopN7/N7 is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingN9CR_Honeywell
	 * @Tested True tested by BL_common_Util_Unittest method: checkMissingN9CR_Honeywell()
	 */
	void checkMissingN9CR_Honeywell(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(root?.Loop_ST?.N9.findAll{it?.E128_01?.text() == 'CR'}?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Cannot find IOR by SAPID: " + root?.Loop_ST?.N9.find{it.E127_01.text() == 'TJ'}?.E369_03?.text()]
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
	 * @RuleName MissingPrepaid4ImportOrCollect4Export
	 * @Tested True tested by file in VANPORTUSBANK
	 */
	void checkMissingPrepaid4ImportOrCollect4Export(cs.b2b.core.mapping.bean.bl.Body current_Body, Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		String chargeType = ""
		if(root?.Loop_ST?.Loop_R4?.find{it?.R4?.E115_01?.text() == 'D' && it?.R4?.E26_05?.text() && (it?.R4?.E26_05?.text() == 'US' || it?.R4?.E26_05?.text() == 'CA')}){
			chargeType = COLLECT
		}else if(root?.Loop_ST?.Loop_R4?.find{it?.R4?.E115_01?.text() == 'L' && it?.R4?.E26_05?.text() && (it?.R4?.E26_05?.text() == 'US' || it?.R4?.E26_05?.text() == 'CA')}){
			chargeType = PREPAID
		}


		if(chargeType != "" && 	(current_Body?.FreightChargeCNTR?.findAll{it?.ChargeType == chargeType}?.size() == 0)){
			if(current_Body?.FreightCharge?.findAll {(it?.ChargeType == chargeType) && (it?.CalculateMethod?.equals("Lump Sum"))}?.size() == 0){
				if(chargeType == '0'){
                    errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Import miss charges "+"(Collect charges for Import, Prepaid charges for Export)"]
                }else{
                    errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Export miss charges "+"(Collect charges for Import, Prepaid charges for Export)"]
                }

				errorKeyList.add(errorKey)
				return
			}
		}

	}

	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissingR4DTM
	 * @Tested True tested by BL_common_Util_Unittest method: checkMissingR4DTM()
	 */
	void checkMissingR4DTM(Node root,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.DTM?.size() == 0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "LoopR4/DTM is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MISSING_SAP_IOR
	 * @Tested True tested by file in VFCGTN
	 */
	void checkMISSING_SAP_IOR(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.N9?.find{it?.E127_02?.text() == 'ERROR'}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Cannot find IOR by SAPID: " + root?.Loop_ST?.N9?.find{it.E128_01.text() == 'TJ'}?.E369_03?.text()]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName MissLoopLXLoopL0L5
	 * @Tested True tested by BL_common_Util_Unittest method: checkMissLoopLXLoopL0L5()
	 */
	void checkMissLoopLXLoopL0L5(Node root,  boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.LoopL0?.L5?.size() == 0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "LoopLX/LoopL0/L5 is mandatory."]
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
	 * @RuleName N722Checker
	 * @Tested True tested by file in CATERPILLARGTN
	 */
	void checkN722(cs.b2b.core.mapping.bean.bl.Body current_Body, Node root,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(current_Body?.Container?.size() > 0 && root?.Loop_ST?.Loop_LX?.findAll{it?.Loop_N7?.find{StringUtil.isEmpty(it?.N7?.E24_22?.text())}}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No Container Size type match."]
			errorKeyList.add(errorKey)
			return
		}
		
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N7L1Checker
	 * @Tested True tested by file in AVONELN
	 */
	void checkN7L1(Node root,  boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.Loop_N7?.size() == 0 || root?.Loop_ST?.Loop_LX?.Loop_N7?.Loop_L1?.L1?.size() ==0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No L1 output to customer."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName NoAmendSent
	 * @Tested True tested by file in VANPORTUSBANK
	 */
	void checkNoAmendSent(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.B2A?.E353_01?.text() != '00'){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "No amendments can be sent."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName NonTotalAmtCollectSum
	 * @Tested True tested by BL_common_Util_Unittest method: testNonTotalAmtCollectSum()
	 */
	void checkNonTotalAmtCollectSum(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(StringUtil.isEmpty(root?.Loop_ST?.B3?.E193_07?.text())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Net Amount Due / B3_07_193."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName NonUSInbound
	 * @Tested True tested by BL_common_Util_Unittest method:testcheckNonUSInbound()
	 */
	void checkNonUSInbound(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.findAll{it.R4.E115_01.text() == 'E' && StringUtil.isNotEmpty(it.R4.E26_05?.text()) && it.R4.E26_05.text() != 'US'}?.size() > 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Non US Inbound shimpent.."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName NoPaymentProvide
	 * @Tested True tested by file in CHHLODEGTN
	 */
	void checkNoPaymentProvide(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
	
		if(root?.Loop_ST?.Loop_L1?.L1?.size() == 0 && root?.Loop_ST?.Loop_LX?.Loop_L0?.L1?.size() == 0 && root?.Loop_ST?.Loop_LX?.Loop_N7?.L1?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "no collect charges."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName OnlyCollectCharge
	 * @Tested True tested by file in ONMUSBANK
	 */
	void checkOnlyCollectCharge(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		int numPrepaidPayment = 0
		int numCollectPayment = 0
		def loop_L1 = null
		if(root?.Loop_ST?.Loop_L1?.size() != 0){
			loop_L1 = root?.Loop_ST?.Loop_L1
		}else{
			loop_L1 = root?.Loop_ST?.Loop_LX?.Loop_L0?.Loop_L1
		}
		numPrepaidPayment = loop_L1?.findAll{it.L1.E16_11.text() == 'P'}?.size()
		numCollectPayment = loop_L1?.findAll{it.L1.E16_11.text() == 'C' || it.L1.E16_11.text() == 'E'}?.size()
		if(numCollectPayment == 0 || (root?.Loop_ST?.B3?.E193_07?.text() ? root?.Loop_ST?.B3?.E193_07?.text() : 0)?.toBigDecimal() <= 0 || root?.Loop_ST?.B3?.E146_04?.text() != 'CC'){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Only collect charge will be sent."]
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
	 * @RuleName OnlyprepaidCharge
	 * @Tested True tested by file in EASTMAN
	 */
	void checkOnlyprepaidCharge(cs.b2b.core.mapping.bean.bl.Body current_Body, Node root,  boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if((root?.Loop_ST?.B3?.E193_07?.text() ? root?.Loop_ST?.B3?.E193_07?.text() : 0)?.toBigDecimal() == 0 || current_Body.FreightCharge.find{it.ChargeType == PREPAID} == null ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Prepaid Charge or  Net Amount charges 0."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName POLPODNotUSCA
	 * @Tested True tested by file in VANPORTUSBANK
	 */
	void checkPOLPODNotUSCA(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{it.R4.E115_01.text() == 'D' && (it.R4.E26_05.text() == 'US' || it.R4.E26_05.text() == 'CA')} == null && root?.Loop_ST?.Loop_R4?.find{it.R4.E115_01.text() == 'L' && (it.R4.E26_05.text() == 'US' || it.R4.E26_05.text() == 'CA')} == null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Obsolete, it is not Import/Export of US/CA."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName R401NonUsAndCa
	 * @Tested True tested by file in CASSOCEANFRT
	 */
	void checkR401NonUsAndCa(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{ it?.R4?.E115_01?.size()>0 && it?.R4?.E115_01?.text()=='E' && (it?.R4?.E26_05?.size()>0 && it?.R4?.E26_05?.text() != 'US' && it?.R4?.E26_05?.text() != 'CA')} != null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Destination Country Code must be US or CA."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName USCAOnly
	 * @Tested true tested by EMERSONEXPORT
	 */
	void checkUSCAOnly(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_R4?.find{it.R4.E115_01.text() == 'L' && it?.R4?.E26_05 == null}!=null || root?.Loop_ST?.Loop_R4?.find{it.R4.E115_01.text() == 'L' && (StringUtil.isEmpty(it.R4.E26_05.text()) || (it.R4.E26_05.text() != 'US' && it.R4.E26_05.text() != 'CA'))} != null ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "POL not CA and US."]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName POLNotUSCA type 1
	 * @Tested true tested by IPPULP
	 */
	void checkPOLNotUSCA(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_R4?.find{it.R4.E115_01.text() == 'L' && (StringUtil.isNotEmpty(it.R4.E26_05.text()) && (it.R4.E26_05.text() != 'US' && it.R4.E26_05.text() != 'CA'))} != null ){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "POL is not US or CA"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName VesselInfo
	 * @Tested True tested by BL_common_Util_Unittest method: checkVesselInfo()
	 */
	void checkVesselInfo(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.V1?.size() == 0 || root?.Loop_ST?.V1?.findAll{StringUtil.isEmpty(it?.E182_02?.text()) && StringUtil.isEmpty(it?.E140_05?.text()) && StringUtil.isEmpty(it?.E55_04?.text())}?.size() == 0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Missing Vessel Information (Name, VoyageNumber or SCAC)."]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CheckLocationcodeForPOLPOD
	 */
	void checkLocationcodeForPOLPOD(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null

		if(root?.Group_UNH?.Group8_TDT[0]?.Group9_LOC?.find{ (it?.LOC?.E3227_01?.text() == '9' || it?.LOC?.E3227_01?.text() == '11') && StringUtil.isEmpty(it?.LOC?.C517_02?.E3225_01?.text())}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "UNLocationcode/SchedKDCode is  mandatory for POLPOD"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName checkL102L106L307Checker_ACUCUBE
	 * @Tested True tested by file in HDYOW
	 */
	void checkL102L106L307Checker_ACUCUBE(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_L1?.find{it.L1?.E60_02?.text()?.length() > 12} || root?.Loop_ST?.Loop_L1?.find{it.L1?.E117_06?.text()?.length() > 12} || root?.Loop_ST?.L3?.E117_07?.text()?.length() > 12){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of L307,L102,L106 is over."]
			errorKeyList.add(errorKey)
			return
		}
		return
	}
	

	
	//posp checking end
	
	//EDISyntax checking start
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName B2A_M
	 * @Tested True tested by file in CDLGTN
	 */
	void checkB2A_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(StringUtil.isEmpty(root?.Loop_ST?.B2A?.E353_01?.text())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "B2A.01.353 is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName B309_B310_P
	 * @Tested True tested by BL_common_Util_Unittest method: testB309_B310_P()
	 */
	void checkB309_B310_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if((StringUtil.isEmpty(root?.Loop_ST?.B3?.E32_09?.text()) && StringUtil.isNotEmpty(root?.Loop_ST?.B3?.E374_10?.text())) ||
			(StringUtil.isNotEmpty(root?.Loop_ST?.B3?.E32_09?.text()) && StringUtil.isEmpty(root?.Loop_ST?.B3?.E374_10?.text()))){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either B309 B310 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName B312_M
	 * @Tested True tested by file in PIER1
	 */
	void checkB312_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(StringUtil.isEmpty(root?.Loop_ST?.B3?.E373_12?.text())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment B312 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName C301_M
	 * @Tested True tested by file in HDYOW
	 */
	void checkC301_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_L1?.size() > 0 && root?.Loop_ST?.Loop_L1?.find{StringUtil.isEmpty(it?.C3?.text())}){    // prod this has no question
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "C301 is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName C803_C802_R
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkC803_C802_R(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_C8?.find{StringUtil.isEmpty(it.C8?.E246_02?.text()) && StringUtil.isEmpty(it.C8?.E247_03?.text())}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "At least one of C802,C803 required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName DTM02_DTM03_DTM_05_R
	 * @Tested True tested by BL_common_Util_Unittest method:testDTM02_DTM03_DTM_05_R
	 */
	void checkDTM02_DTM03_DTM_05_R(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{it.DTM?.E373_02  && it.DTM?.E373_02?.text()?.equals('')&& StringUtil.isEmpty(it.DTM?.E337_03?.text()) && StringUtil.isEmpty(it.DTM?.E1250_05?.text())}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "At least one of DTM02,DTM03,DTM05 required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName DTM04_DTM03_C
	 * @Tested True tested by BL_common_Util_Unittest method: testDTM04_DTM03_C()
	 */
	void checkDTM04_DTM03_C(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{StringUtil.isEmpty(it.DTM?.E337_03?.text()) && StringUtil.isNotEmpty(it.DTM?.E623_04?.text())}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "If DTM04 is present, DTM03 is required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName G6103_G6104_P
	 * @Tested True tested by BL_common_Util_Unittest method: testG6103_G6104_P()
	 */
	void checkG6103_G6104_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.G61?.find{(StringUtil.isEmpty(it.E365_03?.text()) && StringUtil.isNotEmpty(it.E364_04?.text())) || (StringUtil.isNotEmpty(it.E365_03?.text()) && StringUtil.isEmpty(it.E364_04?.text()))}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either G6103, G6104 present other are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName H101_M
	 * @Tested True tested by file in CANONDSG
	 */
	void checkH101_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{it.Loop_H1.find{StringUtil.isEmpty(it.H1.E62_01.text())}}}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "H101 is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName H107_H108_P
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkH107_H108_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{it.Loop_H1.find{(StringUtil.isEmpty(it.H1.E77_07.text()) && StringUtil.isNotEmpty(it.H1.E355_08.text())) || (StringUtil.isNotEmpty(it.H1.E77_07.text()) && StringUtil.isEmpty(it.H1.E355_08.text()))}}} ||
			root?.Loop_ST?.Loop_LX?.find{it.Loop_L0.find{it.Loop_H1.find{(StringUtil.isEmpty(it.H1.E77_07.text()) && StringUtil.isNotEmpty(it.H1.E355_08.text())) || (StringUtil.isNotEmpty(it.H1.E77_07.text()) && StringUtil.isEmpty(it.H1.E355_08.text()))}}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either H107 H108 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L004_L005_L011_P
	 * @Tested True tested by BL_common_Util_Unittest method: checkL004_L005_L011_P()
	 */
	void checkL004_L005_L011_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_L0.find{StringUtil.isEmpty(it.L0.E81_04.text()) && StringUtil.isEmpty(it.L0.E187_05.text()) && StringUtil.isEmpty(it.L0.E188_11.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either L004 and L005 and L011 are exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L004_L005_P
	 * @Tested True tested by BL_common_Util_Unittest method: testL004_L005_P()
	 */
	void checkL004_L005_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_L0.find{(StringUtil.isEmpty(it.L0.E81_04.text()) && StringUtil.isNotEmpty(it.L0.E187_05.text())) || (StringUtil.isNotEmpty(it.L0.E81_04.text()) && StringUtil.isEmpty(it.L0.E187_05.text()))}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either L004 and L005 and L011 are exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L006_L007_P
	 * @Tested True tested by BL_common_Util_Unittest method: testL006_L007_P
	 */
	void checkL006_L007_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if((StringUtil.isEmpty(root?.Loop_ST?.B3?.E32_09?.text()) && StringUtil.isNotEmpty(root?.Loop_ST?.B3?.E374_10?.text())) || (StringUtil.isNotEmpty(root?.Loop_ST?.B3?.E32_09?.text()) && StringUtil.isEmpty(root?.Loop_ST?.B3?.E374_10?.text()))){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either L006 L007 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 *  @RuleName L008_L009_P
	 *  @Tested True tested by BL_common_Util_Unittest method: testL008_L009_P()
	 */
	void checkL008_L009_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_L0.find{(StringUtil.isEmpty(it.L0.E80_08.text()) && StringUtil.isNotEmpty(it.L0.E211_09.text())) || (StringUtil.isNotEmpty(it.L0.E80_08.text()) && StringUtil.isEmpty(it.L0.E211_09.text()))}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either L008 L009 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L011_L004_C
	 * @Tested True tested by BL_common_Util_Unittest method: testL011_L004_C()
	 */
	void checkL011_L004_C(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_L0.find{(StringUtil.isEmpty(it.L0.E81_04.text()) && StringUtil.isNotEmpty(it.L0.E188_11.text())) || (StringUtil.isNotEmpty(it.L0.E81_04.text()) && StringUtil.isEmpty(it.L0.E188_11.text()))}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either L011 L004 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L102_L103_P
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkL102_L103_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{it.Loop_L1.find{(StringUtil.isEmpty(it.L1.E60_02.text()) && StringUtil.isNotEmpty(it.L1.E122_03.text())) || (StringUtil.isNotEmpty(it.L1.E60_02.text()) && StringUtil.isEmpty(it.L1.E122_03.text()))}}} != null ||
			root?.Loop_ST?.Loop_L1?.find{(StringUtil.isEmpty(it.L1.E60_02.text()) && StringUtil.isNotEmpty(it.L1.E122_03.text())) || (StringUtil.isNotEmpty(it.L1.E60_02.text()) && StringUtil.isEmpty(it.L1.E122_03.text()))} != null){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either L102 L103 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L104_L105_L106_R
	 * @Tested True tested by BL_common_Util_Unittest method: testL104_L105_L106_R()
	 */
	void checkL104_L105_L106_R(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{it.Loop_L1.find{StringUtil.isEmpty(it.L1.E58_04.text()) && StringUtil.isEmpty(it.L1.E191_05.text()) && StringUtil.isEmpty(it.L1.E117_06.text())}}} ||
			root?.Loop_ST?.Loop_L1?.find{StringUtil.isEmpty(it.L1.E58_04.text()) && StringUtil.isEmpty(it.L1.E191_05.text()) && StringUtil.isEmpty(it.L1.E117_06.text())}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "At least one of L104,L105,L106 required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L108_M
	 * @Tested True tested by file in PIER1
	 */
	void checkL108_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_L1?.find{StringUtil.isEmpty(it.L1.E150_08.text())}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment L108 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L117_L118_P
	 */
	void checkL117_L118_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_L1?.find{StringUtil.isEmpty(it.L1.E220_17.text()) && StringUtil.isNotEmpty(it.L1.E221_18.text())}!=null ||
		   root?.Loop_ST?.Loop_L1?.find{StringUtil.isNotEmpty(it.L1.E220_17.text()) && StringUtil.isEmpty(it.L1.E221_18.text())}!=null ||
		   root?.Loop_ST?.Loop_LX?.Loop_N7?.Loop_L1?.L1?.find{StringUtil.isNotEmpty(it.E220_17.text()) && StringUtil.isEmpty(it.E221_18.text())}!=null ||
		   root?.Loop_ST?.Loop_LX?.Loop_N7?.Loop_L1?.L1?.find{StringUtil.isEmpty(it.E220_17.text()) && StringUtil.isNotEmpty(it.E221_18.text())}!=null ||
		   root?.Loop_ST?.Loop_LX?.Loop_L0?.Loop_L1?.L1?.find{StringUtil.isNotEmpty(it.E220_17.text()) && StringUtil.isEmpty(it.E221_18.text())}!=null ||
		   root?.Loop_ST?.Loop_LX?.Loop_L0?.Loop_L1?.L1?.find{StringUtil.isEmpty(it.E220_17.text()) && StringUtil.isNotEmpty(it.E221_18.text())}!=null ) {
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment L108 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L301_L302_P
	 * @Tested True tested by BL_common_Util_Unittest method: testL301_L302_P()
	 */
	void checkL301_L302_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if((StringUtil.isEmpty(root?.Loop_ST?.L3?.E81_01?.text()) && StringUtil.isNotEmpty(root?.Loop_ST?.L3?.E187_02?.text())) || (StringUtil.isNotEmpty(root?.Loop_ST?.L3?.E81_01?.text()) && StringUtil.isEmpty(root?.Loop_ST?.L3?.E187_02?.text()))){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either L301 L302 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L301_M
	 * @Tested True tested by BL_common_Util_Unittest method: checkL301_M()
	 */
	void checkL301_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if((StringUtil.isEmpty(root?.Loop_ST?.L3?.E81_01?.text()))){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment L301 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L309_L310_P
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkL309_L310_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if((StringUtil.isEmpty(root?.Loop_ST?.L3?.E183_09?.text()) && StringUtil.isNotEmpty(root?.Loop_ST?.L3?.E184_10?.text())) || (StringUtil.isNotEmpty(root?.Loop_ST?.L3?.E183_09?.text()) && StringUtil.isEmpty(root?.Loop_ST?.L3?.E184_10?.text()))){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either L309 L310 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L312_L301_C
	 * @Tested True tested by file in BESTBUYD2L
	 */
	void checkL312_L301_C(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(StringUtil.isEmpty(root?.Loop_ST?.L3?.E81_01?.text()) && StringUtil.isNotEmpty(root?.Loop_ST?.L3?.E188_12?.text())){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "If L312 is present, L301 is required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L3_M
	 * @Tested True tested by BL_common_Util_Unittest method: testL3_M()
	 */
	void checkL3_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(root?.Loop_ST?.L3 == null || root?.Loop_ST?.L3?.size() == 0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "L3 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L507_L506_C
	 * @Tested True tested by BL_common_Util_Unittest method: testL507_L506_C
	 */
	void checkL507_L506_C(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_L0.find{StringUtil.isEmpty(it.L5.E87_06.text()) && StringUtil.isNotEmpty(it.L5.E88_07.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "If L507 is present, L506 is required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N101_N102_M
	 * @Tested True tested by file in HDYOW
	 */
	void checkN101_N102_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_N1?.find{(StringUtil.isEmpty(it.N1.E98_01.text()) && StringUtil.isNotEmpty(it.N1.E93_02.text())) || (StringUtil.isNotEmpty(it.N1.E98_01.text()) && StringUtil.isEmpty(it.N1.E93_02.text()))}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "N101 and N102 are mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName CLP
	 * @Tested True tested by BL_common_Util_Unittest method: checkN101_M()
	 */
	void checkN101_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null

		if(root?.Loop_ST?.Loop_N1?.find{StringUtil.isEmpty(it.N1.E98_01.text())} != null){

			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "N101 are mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N102_M
	 * @Tested True tested by file in PIER1
	 */
	void checkN102_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null

		if(root?.Loop_ST?.Loop_N1?.find{StringUtil.isEmpty(it.N1.E93_02.text())} != null){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "N102 are mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N102_N103_R
	 * @Tested True tested by file in HOMEDECORATOR
	 */
	void checkN102_N103_R(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_N1?.find{StringUtil.isEmpty(it.N1.E93_02.text()) && StringUtil.isEmpty(it.N1.E66_03.text())}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "At least one of N102, N103 is required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N103_N104_P
	 * @Tested True tested by BL_common_Util_Unittest method: testN103_N104_P()
	 */
	void checkN103_N104_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_N1?.find{(StringUtil.isEmpty(it.N1.E66_03.text()) && StringUtil.isNotEmpty(it.N1.E67_04.text())) || (StringUtil.isNotEmpty(it.N1.E66_03.text()) && StringUtil.isEmpty(it.N1.E67_04.text()))}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either N103 N104 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N1N3_M
	 * @Tested True tested by file in PIER1
	 */
	void checkN1N3_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_N1?.findAll{StringUtil.isEmpty(it.N3.text())}?.size() > 0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment N3 under N1 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N406_N405_C
	 * @Tested True tested by BL_common_Util_Unittest method: testN406_N405_C()
	 */
	void checkN406_N405_C(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_N1?.find{StringUtil.isEmpty(it.N4.E309_05.text()) && StringUtil.isNotEmpty(it.N4.E310_06.text())}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "If N406 is present, then all of N405 are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N701_M
	 * @Tested True tested by BL_common_Util_Unittest method: checkN701_M()
	 */
	void checkN701_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{StringUtil.isEmpty(it.N7.E206_01.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment N701 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N702_M
	 * @Tested True tested by file in TNT
	 */
	void checkN702_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it?.Loop_N7?.find{StringUtil.isEmpty(it?.N7?.E207_02?.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "N702 is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N703_M
	 * @Tested True tested by file in PIER1
	 */
	void checkN703_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{StringUtil.isEmpty(it.N7.E81_03.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment N703 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N703_N704_P
	 * @Tested True tested by file in PIER1
	 */
	void checkN703_N704_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{(StringUtil.isEmpty(it.N7.E81_03.text()) && StringUtil.isNotEmpty(it.N7.E187_04.text())) || (StringUtil.isNotEmpty(it.N7.E81_03.text()) && StringUtil.isEmpty(it.N7.E187_04.text()))}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either N703 N704 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N708_M
	 * @Tested True tested by BL_common_Util_Unittest method: checkN708_M()
	 */
	void checkN708_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{StringUtil.isEmpty(it.N7.E183_08.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment N708 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N708_N709_P
	 * @Tested True tested by file in TNT
	 */
	void checkN708_N709_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{(StringUtil.isEmpty(it.N7.E183_08.text()) && StringUtil.isNotEmpty(it.N7.E184_09.text())) || (StringUtil.isNotEmpty(it.N7.E183_08.text()) && StringUtil.isEmpty(it.N7.E184_09.text()))}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either N708 N709 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N709_M
	 * @Tested True tested by BL_common_Util_Unittest method: checkN709_M()
	 */
	void checkN709_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{StringUtil.isEmpty(it.N7.E184_09.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment N709 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N718_M
	 * @Tested True tested by file in PIER1
	 */
	void checkN718_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{StringUtil.isEmpty(it.N7.E761_18.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment N718 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N722_M
	 * @Tested True tested by file in FRIESLANDGTN
	 */
	void checkN722_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{StringUtil.isEmpty(it.N7.E24_22.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment N722 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N7M7_M
	 * @Tested True tested by file in PIER1
	 */
	void checkN7M7_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{StringUtil.isEmpty(it?.M7?.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment M7 under N7 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N901_N902_M
	 * @Tested True tested by BL_common_Util_Unittest method: testN901_N902_M()
	 */
	void checkN901_N902_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if((StringUtil.isEmpty(root?.Loop_ST?.N9?.E128_01?.text()) && StringUtil.isNotEmpty(root?.Loop_ST?.N9?.E127_02?.text())) ||
			(StringUtil.isNotEmpty(root?.Loop_ST?.N9?.E128_01?.text()) && StringUtil.isEmpty(root?.Loop_ST?.N9?.E127_02?.text()))){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "N901 and N902 are mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName N902_N903_R
	 * @Tested True tested by BL_common_Util_Unittest method: testN902_N903_R()
	 */
	void checkN902_N903_R(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(StringUtil.isEmpty(root?.Loop_ST?.N9?.E127_02?.text()) && StringUtil.isEmpty(root?.Loop_ST?.N9?.E369_03?.text())){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "At least one of N902, N903 is required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName QTY01_M
	 * @Test True
	 */
	void checkQTY01_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
	
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.Loop_LX?.find{it?.Loop_N7?.find{StringUtil.isEmpty(it?.QTY?.toString())}}){
			if(root?.Loop_ST?.Loop_LX?.find{it?.Loop_N7?.find{StringUtil.isEmpty(it?.QTY?.E673_01?.text())}} ){
				
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "QTY_01 is mandatory."]
				errorKeyList.add(errorKey)
				return
			}
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName QTY01_QTY02_R
	 * @Tested True tested by  KODAKNNR
	 */
	void checkQTY01_QTY02_R(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){

		Map<String,String> errorKey = null

		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{ it.QTY && StringUtil.isEmpty(it.QTY.E673_01.text()) && StringUtil.isEmpty(it.QTY.E380_02.text())}}){

			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "At least one of QTY01,QTY02 is required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName QTY02_QTY04_C
	 */
	void checkQTY02_QTY04_C(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{it?.QTY && StringUtil.isEmpty(it.QTY.E380_02.text()) && StringUtil.isEmpty(it.QTY.E61_04.text())}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "At least one of QTY02 or QTY04 is required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName QTY_M
	 * @Tested True tested by file in PIER1
	 */
	void checkQTY_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.Loop_N7?.findAll{StringUtil.isEmpty(it.QTY.text())}?.size()>0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment QTY under N7 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName R402_R403_P
	 * @Tested True tested by BL_common_Util_Unittest method: testR402_R403_P()
	 */
	void checkR402_R403_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{(StringUtil.isEmpty(it.R4.E309_02.text()) && StringUtil.isNotEmpty(it.R4.E310_03.text())) || (StringUtil.isNotEmpty(it.R4.E309_02.text()) && StringUtil.isEmpty(it.R4.E310_03.text()))}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either R402 R403 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName R403_M
	 * @Tested True tested by BL_common_Util_Unittest method: checkR403_M()
	 */
	void checkR403_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{StringUtil.isEmpty(it.R4.E310_03.text())}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "R403 is mandatory."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName R404_M
	 * @Tested True tested by file in PIER1
	 */
	void checkR404_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{StringUtil.isEmpty(it.R4.E114_04.text())}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment R404 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName R405_M
	 * @Tested True tested by file in PIER1
	 */
	void checkR405_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{StringUtil.isEmpty(it.R4.E26_05.text())}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment R405 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName R4DTM_M
	 * @Tested True tested by file in GEORGIAPACIFIC
	 */
	void checkR4DTM_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_R4?.find{it.DTM?.E374_01?.text()?.equals('') && it.DTM?.E373_02?.text()?.equals('')}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "ATA/ETA can not be null"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName V101_V102_R
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkV101_V102_R(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.V1?.find{StringUtil.isEmpty(it.E597_01.text()) && StringUtil.isEmpty(it.E182_02.text())} != null){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "At least one of V101, V102 is required"]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName V102_M
	 * @Tested True tested by file in WSIGTN
	 */
	void checkV102_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.V1?.find{StringUtil.isEmpty(it?.E182_02?.text())}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment V102 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName V104_M
	 * @Tested True tested by file in FRIESLANDGTN
	 */
	void checkV104_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		if(root?.Loop_ST?.V1?.find{StringUtil.isEmpty(it?.E55_04?.text())}){

			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment V104 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName V108_V101_C
	 * @Tested True tested by BL_common_Util_Unittest method: testV108_V101_C()
	 */
	void checkV108_V101_C(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(root?.Loop_ST?.V1?.E597_01?.text()) && StringUtil.isNotEmpty(root?.Loop_ST?.V1?.E897_08?.text())){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "If V108 is present, V101 is required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName V1_M
	 * @Tested True tested by file in PIER1
	 */
	void checkV1_M(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.V1?.size() == 0){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Segment V1 is Missed."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName W0902_W0903_P
	 * @Tested True tested by file in DUMMY310BLb
	 */
	void checkW0902_W0903_P(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
		if(root?.Loop_ST?.Loop_LX?.find{it.Loop_N7.find{(StringUtil.isEmpty(it.W09.E408_02.text()) && StringUtil.isNotEmpty(it.W09.E355_03.text())) || (StringUtil.isNotEmpty(it.W09.E408_02.text()) && StringUtil.isEmpty(it.W09.E355_03.text()))}}){
			
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "Either W0902 W0903 exists then all are required."]
			errorKeyList.add(errorKey)
			return
		}
	}
	
	/**
	 *
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName L102L106L307Checker
	 * @Tested true by tp_id = GEORGIAPACIFIC
	 */
	 void checkL102L106L307Checker(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		 Map<String,String> errorKey = null
		
		 if(root?.Loop_ST?.Loop_L1.find{it?.L1?.E60_02?.text()?.length() > 9 || it?.L1?.E117_06?.text()?.length() > 12} || root?.Loop_ST?.L3?.E117_07?.text()?.length() > 9){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of L307,L102,L106 is over."]
			 errorKeyList.add(errorKey)
			 return
		 }
		 
		 if(root?.Loop_ST?.Loop_LX?.Loop_N7?.Loop_L1?.find{ it?.L1?.E60_02?.text()?.length() > 9 || it?.L1?.E117_06?.text()?.length() > 9}){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of L307,L102,L106 is over."]
			 errorKeyList.add(errorKey)
			 return
		 }
		 
		 if(root?.Loop_ST?.Loop_LX?.Loop_L0?.Loop_L1?.find{ it?.L1?.E60_02?.text()?.length() > 12 || it?.L1?.E117_06?.text()?.length() > 9}){
			 errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "The Length of L307,L102,L106 is over."]
			 errorKeyList.add(errorKey)
			 return
		 }
		 
	 }
	
	//EDISyntax checking end
	
	void OceanLegReorder(List<OceanLeg>  OceanLeg){
		if(OceanLeg?.find{StringUtil.isNotEmpty(it.LegSeq)}){
			OceanLeg.sort{a,b ->
				return a.LegSeq.toBigInteger().compareTo(b.LegSeq.toInteger())
			}
		}
	}
	/**
	 * SplitTextWithConnector
	 * @param currentCargoDescription
	 * @param maxLengthOutElement
	 * @param prefix
	 * @return
	 */
	List<String> SplitTextWithConnector(String currentCargoDescription,int maxLengthOutElement, String prefix){
		List<String> CargoDescription = new ArrayList<String>()
		def prefixLength = prefix?.length()
		def  CargoDescriptionSize = currentCargoDescription?.length()
		if(CargoDescriptionSize > maxLengthOutElement - prefixLength + 1){
			while(currentCargoDescription?.length() + prefixLength > maxLengthOutElement ){
				def tempTxt = util.substring(currentCargoDescription?.trim(), 1, maxLengthOutElement-prefixLength)
				if(tempTxt.lastIndexOf(' ') > 0 && currentCargoDescription.charAt(maxLengthOutElement - prefixLength) == ' '){
					CargoDescription.add(prefix + tempTxt?.trim())
					currentCargoDescription =util.substring(currentCargoDescription?.trim(), maxLengthOutElement- prefixLength + 1, CargoDescriptionSize - maxLengthOutElement- prefixLength)
				}else if(tempTxt.lastIndexOf(' ') > 0){
					CargoDescription.add(prefix + util.substring(tempTxt, 1, tempTxt.lastIndexOf(' ')+1)?.trim())
					currentCargoDescription =  util.substring(currentCargoDescription?.trim(), tempTxt.lastIndexOf(' ') + 2, CargoDescriptionSize - tempTxt.lastIndexOf(' ')-1)
				}else{
					CargoDescription.add(prefix +tempTxt?.trim())
					currentCargoDescription = util.substring(currentCargoDescription?.trim(), maxLengthOutElement- prefixLength + 1, CargoDescriptionSize - maxLengthOutElement- prefixLength)
				}
			}
			if(currentCargoDescription?.length() > 0){
				CargoDescription.add(prefix +currentCargoDescription)
			}
		}else{
			CargoDescription = [prefix + currentCargoDescription]
		}
		return CargoDescription
	}

	/**
	 * SplitTextWithConnector
	 * @param currentCargoDescription
	 * @param maxLengthOutElement
	 * @return
	 */
	List<String> SplitTextWithConnector(String currentCargoDescription,int maxLengthOutElement){
		List<String> CargoDescription = new ArrayList<String>()
			def  CargoDescriptionSize = currentCargoDescription?.length()
			if(CargoDescriptionSize > maxLengthOutElement){
				while(currentCargoDescription?.length() > maxLengthOutElement){
					def tempTxt = util.substring(currentCargoDescription?.trim(), 1, maxLengthOutElement)
					if(tempTxt.lastIndexOf(' ') > 0 && currentCargoDescription.charAt(maxLengthOutElement) == ' '){
						CargoDescription.add(tempTxt?.trim())
						currentCargoDescription = util.substring(currentCargoDescription?.trim(), maxLengthOutElement + 1, CargoDescriptionSize - maxLengthOutElement)
					}else if(tempTxt.lastIndexOf(' ') > 0){
						CargoDescription.add(util.substring(tempTxt, 1, tempTxt.lastIndexOf(' ')+1)?.trim())
						currentCargoDescription =  util.substring(currentCargoDescription?.trim(), tempTxt.lastIndexOf(' ') + 2, CargoDescriptionSize - tempTxt.lastIndexOf(' ')-1)
					}else{
						CargoDescription.add(tempTxt?.trim())
						currentCargoDescription = util.substring(currentCargoDescription?.trim(), maxLengthOutElement + 1, CargoDescriptionSize - maxLengthOutElement)
					}
				}
				if(currentCargoDescription?.length() > 0){
					CargoDescription.add(currentCargoDescription)
				}
			}else{
				CargoDescription = [currentCargoDescription]
			}
			return CargoDescription
	}

	public Map<String,String> getB2BCdeConversion(String int_CDE, String TP_ID, String convertType, Connection conn) throws Exception {
		if (conn == null)
			return "";

		HashMap<String, String> retMap = new HashMap<String, String>();
		if (int_CDE==null) {
			return retMap
		}

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select EXT_CDE, REMARKS from b2b_cde_conversion WHERE (INT_CDE = ? or REMARKS = ?) and DIR_ID = 'O' and MSG_TYPE_ID = 'BL' and TP_ID = ? and CONVERT_TYPE_ID = ?";
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, int_CDE);
			pre.setString(2, int_CDE);
			pre.setString(3, TP_ID);
			pre.setString(4, convertType);

			result = pre.executeQuery();
			if (result.next()) {
				retMap.put("EXT_CDE", (result.getString("EXT_CDE")==null?"":result.getString("EXT_CDE").trim()));
				retMap.put("REMARKS", (result.getString("REMARKS")==null?"":result.getString("REMARKS").trim()));
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return retMap
	}

//  Jake eceplos
//	void missingBGM_C002_1001(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
//
//		Map<String,String> errorKey = null
//
//		if(StringUtil.isEmpty(root?.Group_UNH?.BGM?.C002_01?.E1001_01?.text())){
//			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BGM C002 1001 is missing."]
//			errorKeyList.add(errorKey)
//			return
//		}
//	}

	/**
	 *
	 * @param body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void missingGrp12_NAD_C082_3039(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		//$Start/root/Flows/ConvertType[type="ExceptionHandling"]/Config[SegmentId="Grp12-NAD" and SegmentField="C082-3039"]/ProcessId = "1" and
		//exists($Start/root/pfx:Body-BillOfLading/pfx:Party-Body-BillOfLading[tib:trim(ns3:PartyType)="SHP" and tib:trim(ns3:CarrierCustomerCode)="" ])
		//current_Body?.Party?.PartyType=="SHP" && current_Body?.Party?.CarrierCustomerCode==""
		if(current_Body?.Party?.findAll{it?.PartyType=="SHP" && it?.CarrierCustomerCode?.trim()==""}?.size()>0){
			//$_globalVariables/ns1:GlobalVariables/AppErrorReport/Action, "
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BlPartyType is SHP and BlPartyCode is null."]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 *
	 * @param body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void missingGrp12_NAD_Grp12_NAD(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		//exists($Start/root/pfx:Body-BillOfLading/pfx:Party-Body-BillOfLading[ns3:PartyType="CGN"]) =false() and
		//exists($Start/root/pfx:Body-BillOfLading/pfx:Party-Body-BillOfLading[ns3:PartyType="SHP"])=false()
		//current_Body?.Party?.PartyType!="CGN" && current_Body?.Party?.PartyType!="SHP"
		if(current_Body?.Party?.any{it?.PartyType!="CGN" && it?.PartyType!="SHP"}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BlParty without PartyType 'SHP' and 'CGN'"]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 *
	 * @param body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void missingGrp12_NAD_3035_1(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		//exists($Start/root/pfx:Body-BillOfLading/pfx:Party-Body-BillOfLading[ns3:PartyType!="SHP" and ns3:PartyType!="CGN"  and ns3:PartyType!="FWD"  and ns3:PartyType!="NPT"   and ns3:PartyType!="ANP"])
		Map<String,String> mapPartyType=['SHP':'SH','CGN':'CN','FWD':'FW','NPT':'NP','ANP':'AP']
		if(current_Body?.Party?.findAll{mapPartyType.get(it?.PartyType)==null}?.size()>0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BlPartyType is neither 'SH', 'CN', 'FW', 'NP', 'AP'."]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 *
	 * @param body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void missingGrp12_NAD_3035_2(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		//exists($Start/root/pfx:Body-BillOfLading/pfx:Party-Body-BillOfLading[ns3:PartyType!="SHP" and ns3:PartyType!="CGN"  and ns3:PartyType!="FWD"  and ns3:PartyType!="NPT"
		//and ns3:PartyType!="ANP" and ns3:PartyType!="BRK"  and ns3:PartyType!="CCP"  and ns3:PartyType!="CTP"])
		Map<String,String> mapPartyType=['SHP':'SH','CGN':'CN','FWD':'FW','NPT':'NP','ANP':'AP','BRK':'BR','CCP':'CP','CTP':'CT']
		if(current_Body?.Party?.findAll{ mapPartyType.get(it.PartyType)==null}?.size()>0){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BlPartyType is neither 'SH', 'CN', 'FW', 'NP', 'AP'"]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 *
	 * @param body
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @Tested true
	 */
	void missingGrp12_NAD_C080_3036(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		//exists($Start/root/pfx:Body-BillOfLading/pfx:Party-Body-BillOfLading[tib:trim(ns3:PartyName)=""])
		if(current_Body?.Party?.find{it.PartyName?.trim()==""}){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BlPartyName is null"]
			errorKeyList.add(errorKey)
			return
		}
	}

	/**
	 * @param root
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 * @RuleName BGM C002 1001 is missing
	 * @Tested true test by unit test
	 */
	void missingGrp18_GID_1496(Node root, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		//exists($getGroup18/group/pfx7:L-GID_70/pfx7:S-GID_70[tib:trim(pfx7:E-1496_70_01)=""])=true()
		Map<String,String> errorKey = null
		println root?.Group_UNH?.Group18_GID?.GID?.E1496_01?.text()
		if(root?.Group_UNH?.Group18_GID?.find{StringUtil.isEmpty(it?.GID?.E1496_01?.text()?.trim())} != null){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg ? errorMsg : "BGM C002 1001 is missing."]
			errorKeyList.add(errorKey)
			return
		}

	}



}
