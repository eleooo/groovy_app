package cs.b2b.core.mapping.util

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import java.sql.PreparedStatement
import java.sql.ResultSet

import java.sql.Connection
import java.util.Map;

import cs.b2b.core.common.xmlvalidation.ValidateXML

class MappingUtil_SI_I_Common {

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

    private static final String CCC_CONVERTTYPEID = "CustCode"

    private static final String CONTAINER_CONVERTTYPEID = "ContainerType"

    private static final String PACKAGE_CONVERTTYPEID = "CargoPackageType"

    private static final String INVAILD_CONTAINER_TYPE_ERROR = 'Container size type <EXT_CDE> is not recognized for code conversion. Please seek advice from customer for the corresponding code description.'

    private static final String INVAILD_PACKAGE_TYPE_ERROR = 'Package type <EXT_CDE> is not recognized for code conversion. Please seek advice from customer for the corresponding code description.'

    private static final String INVAILD_SICCC_TYPE_ERROR = 'SI Submitter OfficeCode/CCC is not recognized for code conversion. Please seek advice from customer for the new office code, name and address, to proceed LOA.'

    private static final String ISNEEDREPLYPARTYEMAIL_CONVERTTYPEID = "isNeedReplyPartyEmail"

    private static final Map<String, String> extEuCharMap = [	"°" : "o",
																"º" : "o",
																"À" : 'A',
																"Á" : "A",
																"Ã" : "A",
																"Ç" : "C",
																"É" : "E",
																"Ò" : "O",
																"Ó" : "O",
																"Ù" : "U",
																"Ú" : "U",
																"Ü" : "U",
																"à" : "a",
																"á" : "a",
																"ã" : "a",
																"ç" : "c",
																"é" : "e",
																"ò" : "o",
																"ó" : "o",
																"ù" : "u",
																"ú" : "u",
																"ü" : "u",
																"Ú" : "U",
																"Í" : "I",
																"í" : "i"	]
	
	private cs.b2b.core.mapping.util.MappingUtil util
	
	public MappingUtil_SI_I_Common(){
	
	}
	
	public MappingUtil_SI_I_Common(cs.b2b.core.mapping.util.MappingUtil util){
		this.util = util
	}
	
	public int getDBTimeOutInSeconds() {
		int DB_TIMEOUT_IN_SECCOND = 10
		return DB_TIMEOUT_IN_SECCOND
	}
	
	public void promoteBizKeyToSession(String appSessionId, StringWriter bizKeyWriter) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizKeyWriter?.toString())
	}
	
	public void promoteAckUploadToSession(String appSessionId, StringWriter processAckWriter) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_ACKUPLOAD', processAckWriter?.toString())
	}
	
	public MarkupBuilder prepareACK(def tpId, def msgReqId, def dirId, def msgTypeId, def current_Body, def currentTransaction, def processAckXml, Connection conn) {
		def SCAC = current_Body.GeneralInformation?.SCAC.text()
		def insertSCAC = util.InsertSCAC(msgReqId, dirId, 'OSCAC', SCAC, conn)
		
		def carrierId = getChannelTpId(tpId, msgTypeId, SCAC, conn)
		def uuidId = current_Body.TransactionInformation?.'ns0:MessageID'.text()
		def interchangeCtlNum = currentTransaction.ControlNumberInfo?.Interchange.text()
		def groupCtlNum = currentTransaction.ControlNumberInfo?.Group.text()
		def transactionCtlNum = currentTransaction.ControlNumberInfo?.Transaction.text()
		
		def insertMonLog = util.SetMonEDIControlNo(tpId, carrierId, msgTypeId, 'EDIFACT', interchangeCtlNum,groupCtlNum, transactionCtlNum, uuidId, msgReqId, conn)
		processAckXml = buildProcessAck(processAckXml, tpId, msgReqId, carrierId, uuidId, interchangeCtlNum, groupCtlNum, transactionCtlNum, currentTransaction)
		
		return processAckXml
	}
	
	public String replaceEUChars(String str) {
		String instr = str;
		extEuCharMap.each{k,v-> 
			instr = instr.replaceAll(k, v)
		}
		return instr
	}
	
	public String getChannelTpId(String TP_ID, String MSG_TYPE, String SCAC, Connection conn) throws Exception {
		if (conn == null)
			return "";
	
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select channel_tp_id from tp_integration_asso aa where aa.sender_tp_id=? and aa.message_type=? and aa.receiver_scac_code=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10);
			pre.setQueryTimeout(1);
			pre.setString(1, TP_ID);
			pre.setString(2, MSG_TYPE);
			pre.setString(3, SCAC);

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
	
	public MarkupBuilder buildProcessAck (MarkupBuilder processAckXml, def tpId, def msgReqId, def carrierId, def uuidId, def interchangeCtlNum, def groupCtlNum, def transactionCtlNum, def currentTransaction){
		processAckXml.'ns0:ProcessAckTrack'('xmlns:ns0': 'http://www.tibco.com/schemas/APP_COMMON/Schemas/EDI/Schema.xsd') {
			'ns0:AckTypeId' 1
			'ns0:SenderId' tpId
			'ns0:ReceiverId' carrierId
			'ns0:MsgReqId' msgReqId
			'ns0:CS2MsgId' uuidId
			'ns0:IntchgCtlNo' interchangeCtlNum
			'ns0:FGCtlNo' groupCtlNum
			'ns0:TxnCtlNo' transactionCtlNum
			currentTransaction.BizKey?.each{ currentBizKey ->
				'ns0:AckInfoKey'{
					'ns0:Type' currentBizKey.Type.text()
					'ns0:Value'currentBizKey.Value.text()
				}
			}
		}// End-Transaction
		return processAckXml
	}
	
	public List<Object> buildBizKey(MarkupBuilder bizKeyXml, def current_Body,int current_BodyIndex, def errorKeyList, String tpId, String msgReqId, Connection conn) {
		def bizKey = []
		
		// Message Reference Number (Reference Number)
		bizKey.add(["MSRN" : msgReqId])
		
		//L1STPID & L1RTPID
		//Get the correct MON_TYPE from DB
		String montype = util.getMonTypeFromE2EMon(tpId, msgReqId ,conn)
		
		if (util.notEmpty(montype)){
			HashMap<String, String> bizKeyObject = []
			bizKeyObject.put(montype,msgReqId)
			bizKey.add(bizKeyObject)
			bizKey.add(["L1STPID" : tpId])
			bizKey.add(["L1RTPID" : tpId])
		}
		
		//For Txn without error
		if (errorKeyList.findAll{it != ""}?.size() == 0){
			// LKP & L3P
			if (util.notEmpty(current_Body.TransactionInformation.MessageID)){
				bizKey.add(["LKP" : current_Body.TransactionInformation.MessageID.text()])
				bizKey.add(["L3P" : current_Body.TransactionInformation.MessageID.text()])
			}
			
			// STPID
			bizKey.add(["STPID" : tpId])
			//L3STPID
			bizKey.add(["L3STPID" : tpId])
			
			// RTPID & L3RTPID
			if (util.notEmpty(current_Body.GeneralInformation.SCAC)){
				bizKey.add(["RTPID" : util.getCarrierTpId(tpId, 'SI', current_Body.GeneralInformation.SCAC.text(), conn)])
				bizKey.add(["L3RTPID" : util.getCarrierTpId(tpId, 'SI', current_Body.GeneralInformation.SCAC.text(), conn)])
			}
			
			// Action
			if (util.notEmpty(current_Body.GeneralInformation.ActionType)){
				bizKey.add(["ACTION" : current_Body.GeneralInformation.ActionType])
			}
				
			// SI Reference Number (Shipment Number)
			if (util.notEmpty(current_Body.GeneralInformation.CustSIReferenceNumber)){
				bizKey.add(["SI_REF_NUM" : current_Body.GeneralInformation.CustSIReferenceNumber])
			}
				
			
			// Carrier (Shipment Number)
			if (util.notEmpty(current_Body.GeneralInformation.SCAC)){
				bizKey.add(["CARRIER" : current_Body.GeneralInformation.SCAC])
			}
				
			// Container (Shipment Number)
			current_Body.Container.each{ currentContainer->
				if (util.notEmpty(currentContainer)){
					bizKey.add(["CNTR_NUM" : currentContainer.ContainerNumber])
				}
			}
			// Booking Number (Shipment Number)
			current_Body.GeneralInformation.BookingNumber.each{ currentBookingNumber->
				bizKey.add(["BK_NUM" : currentBookingNumber])
			}
			// BL Number (Shipment Number)
			if (util.notEmpty(current_Body.BLInfo.BLNumber)){
				bizKey.add(["BL_NUM" : current_Body.BLInfo.BLNumber])
			}
			// Shippers, Consignee, (Also) Notify Party, Forwarder (Legal Parties)
			current_Body.Party.each{ currentParty->
				switch (currentParty.PartyType){
					case 'SIR':
						bizKey.add(["REQUESTOR_CCC" : currentParty.CarrierCustomerCode])
						break
					case 'SHP':
						bizKey.add(["SHIPPER_CCC" : currentParty.CarrierCustomerCode])
						break
					case 'CGN':
						bizKey.add(["CONSIGNEE_CCC" : currentParty.CarrierCustomerCode])
						break
					case 'FWD':
						bizKey.add(["FORWARDER_CCC" : currentParty.CarrierCustomerCode])
						break
					case 'NPT':
						bizKey.add(["NOTIFY_PARTY_CCC" : currentParty.CarrierCustomerCode])
						break
					case 'ANP':
						bizKey.add(["2ND_NOTIFY PARTY_CCC" : currentParty.CarrierCustomerCode])
						break
					}
			}
			// External Reference (Reference Number)
			current_Body.ExternalReference.each{ currentExternalReference->
				if (util.notEmpty(currentExternalReference.ReferenceNumber)){
					switch (currentExternalReference.CSReferenceType){
						case 'BL':
							bizKey.add(["BLN" : currentExternalReference.ReferenceNumber])
							break
						case 'BKG':
							bizKey.add(["BKG" : currentExternalReference.ReferenceNumber])
							break
						case 'CGO':
							bizKey.add(["CG" : currentExternalReference.ReferenceNumber])
							break
						case 'CR':
							bizKey.add(["CRN" : currentExternalReference.ReferenceNumber])
							break
						case 'CTR':
							bizKey.add(["CT" : currentExternalReference.ReferenceNumber])
							break
						case 'EX':
							bizKey.add(["EX" : currentExternalReference.ReferenceNumber])
							break
						case 'FR':
							bizKey.add(["FR" : currentExternalReference.ReferenceNumber])
							break
						case 'FID':
							bizKey.add(["FI" : currentExternalReference.ReferenceNumber])
							break
						case 'INV':
							bizKey.add(["INV" : currentExternalReference.ReferenceNumber])
							break
						case 'PO':
							bizKey.add(["PO" : currentExternalReference.ReferenceNumber])
							break
						case 'EXPR':
							bizKey.add(["ERN" : currentExternalReference.ReferenceNumber])
							break
						case 'SID':
							bizKey.add(["SID" : currentExternalReference.ReferenceNumber])
							break
						case 'SO':
							bizKey.add(["SO" : currentExternalReference.ReferenceNumber])
							break
						case 'ITN':
							bizKey.add(["TN" : currentExternalReference.ReferenceNumber])
							break
						case 'BH':
							bizKey.add(["HBL" : currentExternalReference.ReferenceNumber])
							break
					}
				}
			}
		}
		
		def addItems = []
		bizKey.each{ currentBizKeyMap ->
			if (!addItems.contains(currentBizKeyMap)){
				addItems.add(currentBizKeyMap)
			}
		}
		println addItems.toString()
		return addItems
	}

	public void generateHeader(MarkupBuilder outXml, def current_Body, def current_BodyIndex, def currentSystemDt, def action, def dirId, def msgTypeId, def tpId, def msgReqId) {
		outXml.'Header'{
			'ns0:ControlNumber'
			'ns0:MsgDT'{
				'ns0:GMT'
				'ns0:LocDT' ('TimeZone': 'HKT', 'CSTimeZone': 'HKT', currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss") + '+08:00')
			}
			'ns0:MsgDirection' dirId
			'ns0:MsgType' msgTypeId
			'ns0:SenderID' tpId
			'ns0:ReceiverID' 'CARGOSMART'
			if (util.notEmpty(action)){
				'ns0:Action' action
			}
			'ns0:Version' '2.0'
			'ns0:InterchangeMessageID' msgReqId
			'ns0:DataSource' "B2B"
		} //End-Header
	}
	
	public List xmlXSDValidation(def sendMessageType, def xml, def txnError = [], def extraValidation = null) {
		List AppErrors = new ArrayList()
		cs.b2b.core.common.xmlvalidation.ValidateXML validator = new ValidateXML()

		def nodeMsg = new XmlParser().parseText(xml)
		Node msgHeader = nodeMsg.clone()
		List msgBodies= new LinkedList()
		msgHeader.children().eachWithIndex {kid , index->
			if (index !=0){
				msgBodies.add(kid)
			}
		}
		msgBodies.eachWithIndex {kid , index->
			msgHeader.remove(kid)
		}
		
		msgBodies.eachWithIndex { currentBody, current_BodyIndex ->
			List bodyErrors = new ArrayList()
			
			if (txnError[current_BodyIndex]?.findAll{it != ""}?.size() == 0 || txnError == []) {
				Node currentMsg = msgHeader.clone()
				currentMsg.append(currentBody)

				String validationResult = validator.xmlValidation(sendMessageType, XmlUtil.serialize(currentMsg))
				List extraErrors = new ArrayList()
				if (extraValidation){
					extraErrors = extraValidation(currentBody, current_BodyIndex)
				}
				
				if (extraErrors.size() > 0){
					extraErrors.each{ error ->
						bodyErrors.add(error)
					}
				}
				
				if (validationResult.contains('Validation Failure.')){
					bodyErrors.add(validationResult)
				}
			}
			
			if (bodyErrors.size() == 0){
				bodyErrors.add("")
			}
			AppErrors.add(bodyErrors)
		}
		
		return AppErrors
	}
	
	public List postValidation(def currentBody, def current_BodyIndex) {
		List postErrors = new ArrayList()
		
		if (!(util.notEmpty(currentBody.GeneralInformation.TotalGrossWeight."ns0:Weight".text())) && (util.notEmpty(currentBody.GeneralInformation.TotalGrossWeight."ns0:WeightUnit".text()))) {
			postErrors.add('Missing Body['+current_BodyIndex+'].GeneralInformation.TotalGrossWeight.Weight as with WeightUnit')
		}
		if ((util.notEmpty(currentBody.GeneralInformation.TotalGrossWeight."ns0:Weight".text())) && !(util.notEmpty(currentBody.GeneralInformation.TotalGrossWeight."ns0:WeightUnit".text()))) {
			postErrors.add('Missing Body['+current_BodyIndex+'].GeneralInformation.TotalGrossWeight.WeightUnit but with Weight')
		}
		if (!(util.notEmpty(currentBody.GeneralInformation.TotalConsignment."ns0:Volume".text())) && (util.notEmpty(currentBody.GeneralInformation.TotalConsignment."ns0:VolumeUnit".text()))) {
			postErrors.add('Missing Body['+current_BodyIndex+'].GeneralInformation.TotalConsignment.Volume as with VolumeUnit')
		}
		if ((util.notEmpty(currentBody.GeneralInformation.TotalConsignment."ns0:Volume".text())) && !(util.notEmpty(currentBody.GeneralInformation.TotalConsignment."ns0:VolumeUnit".text()))) {
			postErrors.add('Missing Body['+current_BodyIndex+'].GeneralInformation.TotalConsignment.VolumeUnit but with Volume')
		}
		
		currentBody.Container.eachWithIndex { currentContainer, currentContainerIndex ->
			if (!(util.notEmpty(currentContainer.GrossWeight."ns0:Weight".text())) && (util.notEmpty(currentContainer.GrossWeight."ns0:WeightUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].GrossWeight.Weight but with WeightUnit')
			}
			if ((util.notEmpty(currentContainer.GrossWeight."ns0:Weight".text())) && !(util.notEmpty(currentContainer.GrossWeight."ns0:WeightUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].GrossWeight.WeightUnit but with Weight')
			}
			if (!(util.notEmpty(currentContainer.TareWeight."ns0:Weight".text())) && (util.notEmpty(currentContainer.TareWeight."ns0:WeightUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].TareWeight.Weight but with WeightUnit')
			}
			if ((util.notEmpty(currentContainer.TareWeight."ns0:Weight".text())) && !(util.notEmpty(currentContainer.TareWeight."ns0:WeightUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].TareWeight.WeightUnit but with Weight')
			}
			if (!(util.notEmpty(currentContainer.ContainerVolume."ns0:Volume".text())) && (util.notEmpty(currentContainer.ContainerVolume."ns0:VolumeUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].ContainerVolume.Volume but with VolumeUnit')
			}
			if ((util.notEmpty(currentContainer.ContainerVolume."ns0:Volume".text())) && !(util.notEmpty(currentContainer.ContainerVolume."ns0:VolumeUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].ContainerVolume.VolumeUnit but with Volume')
			}
			
			if (!(util.notEmpty(currentContainer.VGMWeight."ns0:Weight".text())) && (util.notEmpty(currentContainer.VGMWeight."ns0:WeightUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].VGMWeight.Weight but with WeightUnit')
			}
			if ((util.notEmpty(currentContainer.VGMWeight."ns0:Weight".text())) && !(util.notEmpty(currentContainer.VGMWeight."ns0:WeightUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].VGMWeight.WeightUnit but with Weight')
			}
			currentContainer.ReeferCargoSpec.eachWithIndex { currentReeferCargoSpec, currentReeferCargoSpecIndex ->
				if (!(util.notEmpty(currentReeferCargoSpec."ns0:Temperature"."ns0:Temperature".text())) && (util.notEmpty(currentReeferCargoSpec."ns0:Temperature"."ns0:TemperatureUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].ReeferCargoSpec['+currentReeferCargoSpecIndex+'].Temperature.Temperature but with TemperatureUnit')
				}
				if ((util.notEmpty(currentReeferCargoSpec."ns0:Temperature"."ns0:Temperature".text())) && !(util.notEmpty(currentReeferCargoSpec."ns0:Temperature"."ns0:TemperatureUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].ReeferCargoSpec['+currentReeferCargoSpecIndex+'].Temperature.TemperatureUnit but with Temperature')
				}
				if (!(util.notEmpty(currentReeferCargoSpec."ns0:Ventilation"."ns0:Ventilation".text())) && (util.notEmpty(currentReeferCargoSpec."ns0:Ventilation"."ns0:VentilationUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].ReeferCargoSpec['+currentReeferCargoSpecIndex+'].Ventilation.Ventilation but with VentilationUnit')
				}
				if ((util.notEmpty(currentReeferCargoSpec."ns0:Ventilation"."ns0:Ventilation".text())) && !(util.notEmpty(currentReeferCargoSpec."ns0:Ventilation"."ns0:VentilationUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Container['+currentContainerIndex+'].ReeferCargoSpec['+currentReeferCargoSpecIndex+'].Ventilation.VentilationUnit but with Ventilation')
				}
			}
		}
		
		currentBody.Cargo.eachWithIndex { currentCargo, currentCargoIndex ->
			if (!(util.notEmpty(currentCargo.CargoInfo.GrossWeight."ns0:Weight".text())) && (util.notEmpty(currentCargo.CargoInfo.GrossWeight."ns0:WeightUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].CargoInfo.GrossWeight.Weight but with WeightUnit')
			}
			if ((util.notEmpty(currentCargo.CargoInfo.GrossWeight."ns0:Weight".text())) && !(util.notEmpty(currentCargo.CargoInfo.GrossWeight."ns0:WeightUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].CargoInfo.GrossWeight.WeightUnit but with Weight')
			}
			if (!(util.notEmpty(currentCargo.CargoInfo.Volume."ns0:Volume".text())) && (util.notEmpty(currentCargo.CargoInfo.Volume."ns0:VolumeUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].CargoInfo.Volume but with VolumeUnit')
			}
			if ((util.notEmpty(currentCargo.CargoInfo.Volume."ns0:Volume".text())) && !(util.notEmpty(currentCargo.CargoInfo.Volume."ns0:VolumeUnit".text()))) {
				postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].CargoInfo.VolumeUnit but with Volume')
			}
			currentCargo.ContainerLoadPlan.eachWithIndex { currentContainerLoadPlan, currentContainerLoadPlanIndex ->
				if (!(util.notEmpty(currentContainerLoadPlan.GrossWeight."ns0:Weight".text())) && (util.notEmpty(currentContainerLoadPlan.GrossWeight."ns0:WeightUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].ContainerLoadPlan['+currentContainerLoadPlanIndex+'].GrossWeight.Weight but with WeightUnit')
				}
				if ((util.notEmpty(currentContainerLoadPlan.GrossWeight."ns0:Weight".text())) && !(util.notEmpty(currentContainerLoadPlan.GrossWeight."ns0:WeightUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].ContainerLoadPlan['+currentContainerLoadPlanIndex+'].GrossWeight.WeightUnit but with Weight')
				}
				
				if (!(util.notEmpty(currentContainerLoadPlan.Volume."ns0:Volume".text())) && (util.notEmpty(currentContainerLoadPlan.Volume."ns0:VolumeUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].ContainerLoadPlan['+currentContainerLoadPlanIndex+'].Volume.Volume but with VolumeUnit')
				}
				if ((util.notEmpty(currentContainerLoadPlan.Volume."ns0:Volume".text())) && !(util.notEmpty(currentContainerLoadPlan.Volume."ns0:VolumeUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].ContainerLoadPlan['+currentContainerLoadPlanIndex+'].Volume.VolumeUnit but with Volume')
				}
			}
			currentCargo.DGCargoSpec.eachWithIndex { currentDGCargoSpec, currentDGCargoSpecIndex ->
				if (!(util.notEmpty(currentDGCargoSpec."ns0:FlashPoint"."ns0:Temperature".text())) && (util.notEmpty(currentDGCargoSpec."ns0:FlashPoint"."ns0:TemperatureUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].DGCargoSpec['+currentDGCargoSpecIndex+'].FlashPoint.Temperature but with TemperatureUnit')
				}
				if ((util.notEmpty(currentDGCargoSpec."ns0:FlashPoint"."ns0:Temperature".text())) && !(util.notEmpty(currentDGCargoSpec."ns0:FlashPoint"."ns0:TemperatureUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].DGCargoSpec['+currentDGCargoSpecIndex+'].FlashPoint.TemperatureUnit but with Temperature')
				}
			}
			currentCargo.AWCargoSpec.eachWithIndex { currentAWCargoSpec, currentAWCargoSpecIndex ->
				if (!(util.notEmpty(currentAWCargoSpec."ns0:Height"."ns0:Length".text())) && (util.notEmpty(currentAWCargoSpec."ns0:Height"."ns0:LengthUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].AWCargoSpec['+currentAWCargoSpecIndex+'].Height.Length but with LengthUnit')
				}
				if ((util.notEmpty(currentAWCargoSpec."ns0:Height"."ns0:Length".text())) && !(util.notEmpty(currentAWCargoSpec."ns0:Height"."ns0:LengthUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].AWCargoSpec['+currentAWCargoSpecIndex+'].Height.LengthUnit but with Length')
				}
				if (!(util.notEmpty(currentAWCargoSpec."ns0:Length"."ns0:Length".text())) && (util.notEmpty(currentAWCargoSpec."ns0:Length"."ns0:LengthUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].AWCargoSpec['+currentAWCargoSpecIndex+'].Length.Length but with LengthUnit')
				}
				if ((util.notEmpty(currentAWCargoSpec."ns0:Length"."ns0:Length".text())) && !(util.notEmpty(currentAWCargoSpec."ns0:Length"."ns0:LengthUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].AWCargoSpec['+currentAWCargoSpecIndex+'].Length.LengthUnit but with Length')
				}
				if (!(util.notEmpty(currentAWCargoSpec."ns0:Width"."ns0:Length".text())) && (util.notEmpty(currentAWCargoSpec."ns0:Width"."ns0:LengthUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].AWCargoSpec['+currentAWCargoSpecIndex+'].Width.Length but with LengthUnit')
				}
				if ((util.notEmpty(currentAWCargoSpec."ns0:Width"."ns0:Length".text())) && !(util.notEmpty(currentAWCargoSpec."ns0:Width"."ns0:LengthUnit".text()))) {
					postErrors.add('Missing Body['+current_BodyIndex+'].Cargo['+currentCargoIndex+'].AWCargoSpec['+currentAWCargoSpecIndex+'].Width.LengthUnit but with Length')
				}
			}
		}
		
		return postErrors
	}
}
