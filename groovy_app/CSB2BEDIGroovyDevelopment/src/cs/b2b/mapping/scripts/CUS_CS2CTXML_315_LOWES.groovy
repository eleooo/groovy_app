package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * @author NICOLE
 * ABG CT initialize on 20161115 
 */
public class CUS_CS2CTXML_315_LOWES {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_CT_O_Common ctUtil = new cs.b2b.core.mapping.util.MappingUtil_CT_O_Common(util);

	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	Connection conn = null
	
	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = 'HHmm'

	def currentSystemDt = null
	
	//Process Select SQL for main mapping and exception check
	public Map<String, String> sqlValue = new HashMap<String, String>();
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

	def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('LOWES', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '-999'
			}

//=====================================================B4 START=========================================================
			def B403=vCS1EventCodeConversion
			def specialB403EventCovertion =  ['CS060':'AE','CS070':'VD']
			def isFeeder = null
			if(current_Body?.Route?.OceanLeg && current_Body?.Route?.OceanLeg?.size()>0){
				isFeeder= util.getConversion('LOWES','CT','O','FeederType',current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.Service,conn)
			}

			if(isFeeder && specialB403EventCovertion.containsKey(vCS1Event)){
				B403 = specialB403EventCovertion.get(vCS1Event)
			}


			'B4' {
				'E152_01' 'CH'

				'E157_03' B403

				'E373_04' util.convertXmlDateTime(current_Body.Event?.EventDT?.LocDT, yyyyMMdd)

				'E161_05' util.convertXmlDateTime(current_Body.Event?.EventDT?.LocDT, HHmm)

				'E159_06' current_Body?.Event?.Location?.LocationCode?.UNLocationCode

				if(current_Body.Container?.ContainerNumber?.length()>4){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
					'E207_08' util.substring(util.substring(current_Body.Container?.ContainerNumber,5,6)+ctUtil.emptyToString(current_Body?.Container?.ContainerCheckDigit), 1,7)
				}


				if(current_Body.Container?.CarrCntrSizeType?.trim() != null && current_Body.Container?.CarrCntrSizeType?.trim() != ''){
					'E24_10' current_Body.Container?.CarrCntrSizeType
				}

				if(util.isNotEmpty(current_Body?.Event?.Location?.LocationName)){
					'E310_11' util.substring(current_Body?.Event?.Location?.LocationName?.trim(),1,30)
					'E309_12' 'UN'
				}



			}
//=====================================================B4 OVER==========================================================

			//Loop N9

			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each {CarrierBookingNumber, CarrierBookingNumberGroup ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(CarrierBookingNumber,1,30)
				}
			}

			current_Body?.Container?.Seal?.each {current_Seal ->
				if(util.isNotEmpty(current_Seal?.SealNumber)){
					'N9' {
						'E128_01' 'SN'
						'E127_02' util.substring(current_Seal?.SealNumber?.trim(),1,30)
					}
				}
			}

			current_Body?.BLGeneralInfo?.each {current_BLGeneralInfo ->
				if(util.isNotEmpty(current_BLGeneralInfo?.BLNumber)){
					'N9' {
						'E128_01' 'WY'
						'E127_02' util.substring(current_BLGeneralInfo?.BLNumber?.trim(),1,30)
					}
				}
			}

			def legalPartyType=[]
			current_Body?.Party?.each {current_Party ->
				if(legalPartyType.contains(current_Party?.PartyType) && util.isNotEmpty(current_Party?.CarrierCustomerCode)){
					'N9' {
						'E128_01' 'AAO'
						'E127_02' util.substring(current_Party?.CarrierCustomerCode?.trim(),1,30)
					}
				}
			}


//=====================================================N9 OVER==========================================================

//=====================================================Q2 START==========================================================

			OceanLeg lastOceanLeg=current_Body?.Route?.OceanLeg[-1]
			OceanLeg firstOceanLeg=current_Body?.Route?.OceanLeg[0]

			if(shipDir=='I'){
				'Q2' {
					if (util.isNotEmpty(lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage)) {
						'E55_09' util.substring(lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage?.trim(), 1, 10)
					}
					if (util.isNotEmpty(lastOceanLeg?.SVVD?.Discharge?.VesselName)) {
						'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName, 1, 28)
					}
				}
			}else if(shipDir=='O') {
				'Q2' {
					if (util.isNotEmpty(firstOceanLeg?.SVVD?.Discharge?.ExternalVoyage)) {
						'E55_09' util.substring(firstOceanLeg?.SVVD?.Discharge?.ExternalVoyage?.trim(), 1, 10)
					}
					if (util.isNotEmpty(firstOceanLeg?.SVVD?.Discharge?.VesselName)) {
						'E182_13' util.substring(firstOceanLeg?.SVVD?.Discharge?.VesselName, 1, 28)
					}
				}
			}

//=====================================================Q2 OVER==========================================================

//=====================================================R4 START=========================================================
			//R4
			FirstPOL firstPOL =  current_Body.Route?.FirstPOL
			LastPOD lastPOD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND
			POR POR =  current_Body.Route?.POR
			OceanLeg OceanLegMax
			if(current_Body.Route?.OceanLeg?.size()>0){
				OceanLegMax = current_Body.Route?.OceanLeg[-1]
			}
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode
					}
					if (POR?.LocationName) {
						'E114_04' util.substring(POR?.LocationName?.trim(), 1, 24)
					}
				}

				LocDT porDTM = null
				def isAct = false

				if (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'A'}?.LocDT) {
					porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				} else if (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT) {
					porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT
				} else if (current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT) {
					porDTM = current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}else if(current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					porDTM = current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}else if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					porDTM = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}else if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT){
					porDTM = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
				}
				if (porDTM) {
					'DTM' {
						if (isAct){
							'E374_01' '140'
						} else {
							'E374_01' '139'
						}
						'E373_02' util.convertXmlDateTime(porDTM, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(porDTM, HHmm)

					}
				}
			}

			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' util.substring(firstPOL?.Port?.LocationCode?.UNLocationCode,1,5)
					}
					if (firstPOL?.Port?.PortName) {
						'E114_04' util.substring(firstPOL?.Port?.PortName?.trim(), 1, 24)
					}
				}

				LocDT polDTM = null
				def isAct = false


				if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					polDTM = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				} else if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT){
					polDTM = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
				}

				if (polDTM) {
					'DTM' {
						if (isAct){
							'E374_01' '140'
						} else {
							'E374_01' '139'
						}
						'E373_02' util.convertXmlDateTime(polDTM, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(polDTM, HHmm)

					}
				}

			}


			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' util.substring(lastPOD?.Port?.LocationCode?.UNLocationCode,1,5)
					}

					if (lastPOD?.Port?.PortName) {
						'E114_04' util.substring(lastPOD?.Port?.PortName, 1, 24)
					}
				}

				LocDT podDTM = null
				def isAct = false



				if(OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					podDTM = OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				} else if(OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT){
					podDTM = OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT

				}
				if (podDTM) {
					'DTM' {
						if (isAct){
							'E374_01' '140'
						} else {
							'E374_01' '139'
						}
						'E373_02' util.convertXmlDateTime(podDTM, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(podDTM, HHmm)

					}
				}

			}

			Map<String,String> convertUNLCode = new HashMap<String,String>();
			convertUNLCode.put('USWHU','536')
			convertUNLCode.put('USMVX','955')
			convertUNLCode.put('USSVH','960')
			convertUNLCode.put('USMVP','961')
			convertUNLCode.put('USVLD','962')
			convertUNLCode.put('USNWB','964')
			convertUNLCode.put('USCYS','965')
			convertUNLCode.put('USPER','966')
			convertUNLCode.put('USWHU','972')
			convertUNLCode.put('USTVE','973')
			convertUNLCode.put('USTVE','989')
			convertUNLCode.put('USFDY','990')
			convertUNLCode.put('USVNY','992')
			convertUNLCode.put('USNWB','1418')
			convertUNLCode.put('USSIM','1419')
			convertUNLCode.put('USGYS','1420')
			convertUNLCode.put('USPFE','1421')
			convertUNLCode.put('USLON','1436')
			convertUNLCode.put('USLGB','1438')
			convertUNLCode.put('USLAX','1438')
			convertUNLCode.put('USRFD','1440')
			convertUNLCode.put('USCSF','1446')
			convertUNLCode.put('USPTO','1449')
			convertUNLCode.put('USLRD','1466')
			convertUNLCode.put('CAMIL','1469')
			convertUNLCode.put('USLRD','1471')
			convertUNLCode.put('USLRD','1472')
			convertUNLCode.put('USCSF','3106')

			'Loop_R4' {
				if(convertUNLCode.get(FND?.CityDetails?.LocationCode?.UNLocationCode?.trim())){
					'R4' {
						'E115_01' 'E'
						if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'DC'
							'E310_03' util.substring(convertUNLCode.get(FND?.CityDetails?.LocationCode?.UNLocationCode?.trim()),1,5)
						}
						if(FND?.LocationName){
							'E114_04' util.substring(FND?.LocationName?.trim(),1,24)
						}
					}

				}else{
					'R4' {
						'E115_01' 'E'
						if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
							'E310_03' util.substring(FND?.CityDetails?.LocationCode?.UNLocationCode,1,5)
						}
						if(FND?.LocationName){
							'E114_04' util.substring(FND?.LocationName?.trim(),1,24)
						}
					}
				}


				def isAct = false
				LocDT fndDTM =null
				if(current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT){

					fndDTM = current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true

				}else if(current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT){

					fndDTM = current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT

				}else if(current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT){

					fndDTM = current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true

				}else if(current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT && current_Body.GeneralInformation?.SCAC?.trim() != 'OOLU'){

					fndDTM = current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true

				}else if(current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT){

					fndDTM = current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT

				}else if(current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT && current_Body.GeneralInformation?.SCAC?.trim() != 'OOLU'){

					fndDTM = current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT

				}else if(OceanLegMax){
					if(OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT){
						fndDTM = OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
						isAct = true

					}else if(OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT){
						fndDTM = OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
					}
				}

				if (fndDTM) {
					'DTM' {
						if (isAct){
							'E374_01' '140'
						} else {
							'E374_01' '139'
						}
						'E373_02' util.convertXmlDateTime(fndDTM, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(fndDTM, HHmm)

					}
				}
			}

//=====================================================R4 OVER==========================================================



			'SE' {
				//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
				'E96_01' '-999'
				'E329_02' '-999'
			}
		}

	}

	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		//inputXmlBody = util.removeBOM(inputXmlBody)

		/**
		 * Part II: get OLL mapping runtime parameters
		 */
		//[]{"B2BSessionID="+B2BSessionID, "B2B_OriginalSourceFileName="+B2B_OriginalSourceFileName, "B2B_SendPortID="+B2B_SendPortID, "PortProperty="+PortProperty, "MSG_REQ_ID="+MSG_REQ_ID, "TP_ID="+TP_ID, "MSG_TYPE_ID="+MSG_TYPE_ID, "DIR_ID="+DIR_ID};
		this.conn = conn
		appSessionId = util.getRuntimeParameter("B2BSessionID", runtimeParams);
		sourceFileName = util.getRuntimeParameter("B2B_OriginalSourceFileName", runtimeParams);
		//pmt info
		TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
		MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
		DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
		MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

		/**
		 * Part III: read xml and prepare output xml
		 */
		//Important: the inputXml is xml root element
//		def parser = new XmlParser()
//		parser.setNamespaceAware(false);
//		def ContainerMovement = parser.parseText(inputXmlBody);
		
		XmlBeanParser parser = new XmlBeanParser()
		ContainerMovement ct = parser.xmlParser(inputXmlBody, ContainerMovement.class)
	
		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def bizKeyWriter = new StringWriter();
		def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeyWriter), "", false));
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def csuploadWriter = new StringWriter();
		def csuploadXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(csuploadWriter), "", false));
		csuploadXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")


		/**
		 * Part IV: mapping script start from here
		 */

		//create root node
		def T315 = outXml.createNode('T315')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.


		//BeginWorkFlow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def outputBodyCount = 0
		currentSystemDt = new Date()
		//def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
		def headerMsgDT = util.convertDateTime(ct.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		//long ediIsaCtrlNumber = ctrlNos[0]
		//long ediGroupCtrlNum = ctrlNos[1]
		def txnErrorKeys = []
		
		//build ISA, GS
//		generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)
		
		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
				
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('LOWES', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

			
			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

			if (errorKeyList.size() == 0) {
				//pass validateBeforeExecution
				//main mapping
				generateBody(current_Body, outXml)
				outputBodyCount++
			}

			//posp checking
			pospValidation()

			ctUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, eventExtCde, TP_ID, conn)

			txnErrorKeys.add(errorKeyList);
		}

		//EndWorkFlow
		
		//build GE, IEA
//		generateEDITail(outXml, outputBodyCount)

		//End root node
		outXml.nodeCompleted(null,T315)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)
//		println csuploadWriter.toString();

		//promote csupload and bizkey to session
		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID);
		if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);
		
		
		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = util.cleanXml(writer?.toString());
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()
		return result;

		
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

//		Obsolte Case
		if(['CS160','CS190'].contains(eventCS2Cde)){
			ctUtil.verifyTransportMode(eventCS2Cde, current_Body?.GeneralInformation, false, null, errorKeyList)
		}

//		Error Case
           ctUtil.missingEventUNLocationCode(eventCS2Cde, current_Body?.Event, true, null, errorKeyList)


		return errorKeyList;
	}

	public boolean pospValidation() {

	}


}






