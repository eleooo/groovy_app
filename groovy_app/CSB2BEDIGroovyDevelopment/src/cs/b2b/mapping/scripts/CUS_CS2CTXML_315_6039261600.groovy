package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection

import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.Event
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * @author RENGA
 * DSGOODS CT initialize on 20161115 
 */
public class CUS_CS2CTXML_315_6039261600 {

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
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('6039261600', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
//		def currentSTCtrlNum = '###edi_Group_Ctrl_Number###' + String.format("%04d", 1)

		int OceanLegNum = current_Body.Route.OceanLeg.size();
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {

				if (['CS060'].contains(vCS1EventFirst5)) {
					if(OceanLegNum == 1){
						'E157_03' 'AE'
					}else{
						'E157_03' 'AP'
					}
				}else{
					'E157_03' vCS1EventCodeConversion
				}

				'E373_04' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, HHmm)

				if(current_Body.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
				}


				if(current_Body.Container?.ContainerNumber){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, current_Body.Container?.ContainerNumber.length())
				}


//				if (current_Body.Container?.ContainerStatus?.trim() == 'Empty') {
//					'E578_09' 'E'
//				} else if (current_Body.Container?.ContainerStatus?.trim() == 'Laden') {
//					'E578_09' 'L'
//				} else if (current_Body.Event?.CS1Event.trim() == 'CS210') {
//					'E578_09' 'E'
//				} else {
//					'E578_09' 'L'
//				}
				if (!current_Body.Container?.ContainerStatus&& (vCS1Event=='CS210'||vCS1Event=='CS010')) {
					'E578_09' 'E'
				}else if (!current_Body.Container?.ContainerStatus&& (vCS1Event!='CS210' && vCS1Event!='CS010')) {
					'E578_09' 'L'
				}else if(current_Body.Container?.ContainerStatus=='Empty'){
					'E578_09' 'E'
				}else if(current_Body.Container?.ContainerStatus == 'Laden'){
					'E578_09' 'L'
				}

				if(current_Body.Container?.ContainerCheckDigit){
					'E761_13' util.substring(current_Body.Container?.ContainerCheckDigit,1,2);
				}

			}
			//Loop N9
			//for avoid duplicated N9
//			def BMs = []
//			def BNs = []
			def EQs = []


//			current_Body.BLGeneralInfoLoop.each { current_BLGeneralInfo ->
//				if (current_BLGeneralInfo.BLNumber && !BMs.contains(current_BLGeneralInfo.BLNumber?.trim())) {
//					BMs.add(current_BLGeneralInfo.BLNumber?.trim())
//					'N9' {
//						'E128_01' 'BM'
//						'E127_02' current_BLGeneralInfo.BLNumber?.trim()
//					}
//				}
//
//			}
//			
			current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber?.trim()}.each{BLNumber, BLNumberGroup ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' BLNumber
				}
			}


//			current_Body.BookingGeneralInfoLoop.each { current_BookingGeneralInfo ->
//				if (current_BookingGeneralInfo.CarrierBookingNumber && !BNs.contains(current_BookingGeneralInfo.CarrierBookingNumber?.trim())) {
//					BNs.add(current_BookingGeneralInfo.CarrierBookingNumber?.trim())
//					'N9' {
//						'E128_01' 'BN'
//						'E127_02' current_BookingGeneralInfo.CarrierBookingNumber?.trim()
//					}
//				}
//			}
			
			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each {CarrierBookingNumber, CarrierBookingNumberGroup ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' CarrierBookingNumber
				}
			}
			
			if ((current_Body.Container?.ContainerNumber || current_Body.Container?.ContainerCheckDigit) && !EQs.contains(current_Body.Container?.ContainerNumber + current_Body.Container?.ContainerCheckDigit)) {
				EQs.add(current_Body.Container?.ContainerNumber + current_Body.Container?.ContainerCheckDigit)
				'N9' {
					'E128_01' 'EQ'
					'E127_02' current_Body.Container?.ContainerNumber + current_Body.Container?.ContainerCheckDigit
				}
			}
			
			
			
			def firstScac = current_Body.GeneralInformation?.SCAC
			if(firstScac != ''){
				'N9'{
					'E128_01' 'SCA'
					'E127_02' firstScac
				}
			}


			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body.Route.OceanLeg){
				firstOceanLeg = current_Body.Route.OceanLeg[0]
				lastOceanLeg = current_Body.Route.OceanLeg[-1]
			}
			
			
			if (shipDir == 'I') {
				'Q2' {
					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)
					}
					if (lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction) {
						'E55_09' lastOceanLeg?.SVVD?.Discharge?.Voyage + lastOceanLeg?.SVVD?.Discharge?.Direction
					}
					//					'E127_11' current_Body.GeneralInformation.SCAC.text()
					'E897_12' 'L'
					if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
						'E182_13' lastOceanLeg?.SVVD?.Discharge?.VesselName
					}
				}
			} else if (shipDir == 'O') {
				'Q2' {
					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)
					}

					if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
						'E55_09' firstOceanLeg?.SVVD?.Loading?.Voyage + firstOceanLeg?.SVVD?.Loading?.Direction
					}

					'E897_12' 'L'
					if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' firstOceanLeg?.SVVD?.Loading?.VesselName
					}
				}
			}

			//R4
			POR POR =  current_Body.Route?.POR
			FirstPOL firstPOL =  current_Body.Route?.FirstPOL
			LastPOD lastPOD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND
			Event Event = current_Body?.Event

			'Loop_R4' {
				'R4' {
					'E115_01' '5'
					if (util.notEmpty(Event.Location?.LocationCode?.UNLocationCode?.trim())) {
						'E309_02' 'UN'
					} else if (util.notEmpty(Event.Location?.LocationCode?.SchedKDCode?.trim())) {
						'E309_02' Event.Location?.LocationCode?.SchedKDType?.trim()
					}
					if (util.notEmpty(Event?.Location?.LocationCode?.UNLocationCode?.trim())) {
						'E310_03' Event.Location?.LocationCode?.UNLocationCode?.trim()
					} else if (Event.Location?.LocationCode?.SchedKDCode?.trim()) {
						'E310_03' Event.Location?.LocationCode?.SchedKDCode?.trim()
					}
					if (util.notEmpty(Event.Location?.LocationName?.trim())) {
						'E114_04' util.substring(Event.Location?.LocationName, 1, 24).trim()
					}else if(util.notEmpty(Event.Location?.CityDetails?.City?.trim())){
						'E114_04' util.substring(Event.Location?.CityDetails?.City,1,24).trim()
					}
					if (Event.Location?.CSStandardCity?.CSCountryCode?.length() >= 2){
						'E26_05' Event.Location?.CSStandardCity?.CSCountryCode?.trim()
					}
					if (Event.Location?.CityDetails?.State) {
						'E156_08'util.substring(Event.Location?.CityDetails?.State?.toUpperCase(),1,2)
					}
				}


				LocDT locDTM
				if (Event.EventDT?.LocDT) {
					locDTM = Event.EventDT?.LocDT
				}
				if (locDTM != null || locDTM != '') {
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(locDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(locDTM, xmlDateTimeFormat, HHmm)
						//						'E623_04' 'LT'
					}
				}

			}
			//
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if (POR.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (POR.CityDetails?.LocationCode?.SchedKDCode) {
						'E309_02' POR.CityDetails?.LocationCode?.SchedKDType
					}
					if (POR.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' POR.CityDetails?.LocationCode?.UNLocationCode
					} else if (POR.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' POR.CityDetails?.LocationCode?.SchedKDCode
					}
					if (POR.CityDetails?.City) {
						'E114_04' util.substring(POR.CityDetails?.City, 1, 24)
					}
					if (POR.CSStandardCity?.CSCountryCode?.length() >= 2){
						'E26_05' POR.CSStandardCity?.CSCountryCode
					}
					if (POR.CSStandardCity?.CSStateCode) {
						'E156_08' POR.CSStandardCity?.CSStateCode
					}
				}

				LocDT porDTM = null
				def isAct = false
				if (current_Body.Route.CargoReceiptDT.find{it.attr_Indicator == 'A'}?.LocDT) {
					porDTM = current_Body.Route.CargoReceiptDT.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				} else if (current_Body.Route.FullPickupDT.find{it.attr_Indicator == 'E'}?.LocDT && current_Body.BookingGeneralInfo[0].Haulage.OutBound == 'C') {
					porDTM = current_Body.Route.FullPickupDT.find{it.attr_Indicator == 'E'}?.LocDT
				} else if (current_Body.Route.FullReturnCutoffDT.find{it.attr_Indicator == 'A'}?.LocDT && current_Body.BookingGeneralInfo[0].Haulage.OutBound == 'M') {
					porDTM = current_Body.Route.FullReturnCutoffDT.find{it.attr_Indicator == 'A'}?.LocDT
				}
				if (porDTM) {
					'DTM' {
						if (isAct) {
							'E374_01' '140'
						} else {
							'E374_01' '139'
						}
						'E373_02' util.convertDateTime(porDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(porDTM, xmlDateTimeFormat, HHmm)
					}
				}

			}

			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if (lastPOD.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (lastPOD.Port?.LocationCode?.SchedKDCode) {
						'E309_02' util.substring(lastPOD.Port?.LocationCode?.SchedKDType,1,2)
					}
					if (lastPOD.Port?.LocationCode?.UNLocationCode) {
						'E310_03' util.substring(lastPOD.Port?.LocationCode?.UNLocationCode,1,5)
					} else if (lastPOD.Port?.LocationCode?.SchedKDCode) {
						'E310_03' util.substring(lastPOD.Port?.LocationCode?.SchedKDCode,1,5)
					}
					if (lastPOD.Port?.PortName) {
						'E114_04' util.substring(lastPOD.Port?.PortName, 1, 24)
					}else if(lastPOD.Port?.City){
						'E114_04' util.substring(lastPOD.Port?.City,1,24)
					}
					if (lastPOD.Port?.CSCountryCode?.length() >= 2){
						'E26_05' lastPOD.Port?.CSCountryCode
					}
					if (lastPOD?.CSStateCode) {
						'E156_08' util.substring(lastPOD?.CSStateCode,1,2)
					}
				}

				LocDT locActDTM = null
				LocDT locEstDTM = null
				def isAct = false
				def isEst = false


				if(lastOceanLeg.POD.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT){
					locActDTM = lastOceanLeg.POD.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}
				if(lastOceanLeg.POD.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT){
					locEstDTM = lastOceanLeg.POD.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT
					isEst = true
				}


				if (isAct) {
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(locActDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(locActDTM, xmlDateTimeFormat, HHmm)
					}
				}
				else if (isEst) {
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertDateTime(locEstDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(locEstDTM, xmlDateTimeFormat, HHmm)
					}
				}

			}



			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if (firstPOL.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (firstPOL.Port?.LocationCode?.SchedKDCode) {
						'E309_02' util.substring(firstPOL.Port?.LocationCode?.SchedKDType,1,2)
					}
					if (firstPOL.Port?.LocationCode?.UNLocationCode) {
						'E310_03' util.substring(firstPOL.Port?.LocationCode?.UNLocationCode,1,5)
					} else if (firstPOL.Port?.LocationCode?.SchedKDCode) {
						'E310_03' util.substring(firstPOL.Port?.LocationCode?.SchedKDCode,1,5)
					}
					if (firstPOL.Port?.PortName) {
						'E114_04' util.substring(firstPOL.Port?.PortName, 1, 24)
					}else if(firstPOL.Port?.City){
						'E114_04' util.substring(firstPOL.Port?.City,1,24)
					}
					if (firstPOL.Port?.CSCountryCode?.length() >= 2){
						'E26_05' firstPOL.Port?.CSCountryCode
					}
					if (firstPOL?.CSStateCode) {
						'E156_08' firstPOL?.CSStateCode
					}
				}

				LocDT locActDTM = null
				LocDT locEstDTM = null
				def isAct = false
				def isEst = false

				if(firstOceanLeg?.POL?.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT){
					locActDTM = firstOceanLeg?.POL.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}
				if(firstOceanLeg?.POL?.DepartureDT.find{it.attr_Indicator == 'E'}?.LocDT){
					locEstDTM = firstOceanLeg?.POL.DepartureDT.find{it.attr_Indicator == 'E'}?.LocDT
					isEst = true
				}


				if (isAct) {
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(locActDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(locActDTM, xmlDateTimeFormat, HHmm)
					}
				}
				else if (isEst) {
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertDateTime(locEstDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(locEstDTM, xmlDateTimeFormat, HHmm)
					}
				}

			}



			'Loop_R4' {
				'R4' {
					'E115_01' 'E'
					if (FND.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (FND.CityDetails?.LocationCode?.SchedKDCode) {
						'E309_02' util.substring(FND.CityDetails?.LocationCode?.SchedKDType,1,2)
					}else{
						'E309_02' 'CI'
					}
					if (FND.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' util.substring(FND.CityDetails?.LocationCode?.UNLocationCode,1,5)
					} else if (FND.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' util.substring(FND.CityDetails?.LocationCode?.SchedKDCode,1,5)
					}
					if(FND.CityDetails?.City){
						'E114_04' util.substring(FND.CityDetails?.City,1,24)
					}
					if (FND.CSStandardCity?.CSCountryCode?.length() >= 2){
						'E26_05' util.substring(FND.CSStandardCity?.CSCountryCode,1,2)
					}
					if (FND.CSStandardCity?.CSStateCode) {
						'E156_08' util.substring(FND.CSStandardCity?.CSStateCode,1,2)
					}
				}

				def isAstC = false
				def isEstA = false
				def isEstC = false
				if(lastOceanLeg){

					LocDT AstC = null
					if(current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'A'}?.LocDT){
						AstC = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'A'}?.LocDT
						isAstC = true
					}
					LocDT EstA = null
					def firstBKG_M = current_Body?.BookingGeneralInfo[0]

					if(util.substring(firstBKG_M.Haulage?.InBound?.trim(),1,1) == 'M'){
						if(current_Body.Route.ArrivalAtFinalHub.find{it.attr_Indicator =='E'}?.LocDT){
							EstA = current_Body.Route.ArrivalAtFinalHub.find{it.attr_Indicator =='E'}?.LocDT
							isEstA = true
						}else if(current_Body.Route.FND.ArrivalDT.find{it.attr_Indicator =='E'}?.LocDT && current_Body.GeneralInformation?.SCAC?.trim() !='OOLU'){
							EstA = current_Body.Route.FND.ArrivalDT.find{it.attr_Indicator =='E'}?.LocDT
							isEstA = true
						}

					}

					LocDT EstC = null
					if(util.substring(firstBKG_M.Haulage?.InBound?.trim(),1,1) == 'C'){
						if(current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'E'}?.LocDT){
							EstC = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'E'}?.LocDT
							isEstC = true
						}

					}


					if (isAstC) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(AstC, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(AstC, xmlDateTimeFormat, HHmm)
						}

					}else if(isEstA) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(EstA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(EstA, xmlDateTimeFormat, HHmm)
						}

					}else if(isEstC){
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(EstC, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(EstC, xmlDateTimeFormat, HHmm)
						}
					}

				}
			}
			'SE' {
				//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
				'E96_01' ' '
				'E329_02' '    ' 
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
		
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.BodyLoop)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

		//start body looping
		ct.Body.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('6039261600', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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

//		println bizKeyWriter.toString();
//		println csuploadWriter.toString();

		//promote csupload and bizkey to session
		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);

		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = writer?.toString()
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()

		return result;
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
		// error cases
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event.EventDT.LocDT, true, null, errorKeyList)
		ctUtil.missingContainerNumber(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
		
		// obsolete cases
//		ctUtil.filterEventStatusCode(eventCS2Cde, 'CS130', false, null, errorKeyList)
//		ctUtil.filterEventStatusCode(eventCS2Cde, 'CS140', false, null, errorKeyList)
//		ctUtil.missingBlNumber(eventCS2Cde, current_Body.BLGeneralInfoLoop, false, null, errorKeyList)
//		ctUtil.missingConsigneeCode(eventCS2Cde, current_Body.PartyLoop, false, null ,errorKeyList)
//		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route.Inbound_intermodal_indicator, false, null, errorKeyList)
		
		
//		ctUtil.missingBookingNumber(eventCS2Cde, current_Body.BookingGeneralInfoLoop, true, null, errorKeyList)
//		ctUtil.missingEventCountryCode(eventCS2Cde, current_Body, true, null, errorKeyList)
//		ctUtil.missingEventLocationCode(eventCS2Cde, current_Body, true, null, errorKeyList)
//		ctUtil.missingEventLocationName(eventCS2Cde, current_Body, true, null, errorKeyList)
//		ctUtil.missingPODCountryCode(eventCS2Cde, current_Body.Route.LastPOD, true, null , errorKeyList)
//		ctUtil.missingPODLocationCode(eventCS2Cde, current_Body.Route.LastPOD, true, null , errorKeyList)
//		ctUtil.missingFNDCountryCode(eventCS2Cde, current_Body.Route.FND, true, null , errorKeyList)
//		ctUtil.missingFNDLocationCode(eventCS2Cde, current_Body.Route.FND, true, null , errorKeyList)
//		ctUtil.missingFNDLocationName(eventCS2Cde, current_Body.Route.FND, true, null , errorKeyList)
//		ctUtil.missingPOLCountryCode(eventCS2Cde, current_Body.Route.FirstPOL, true, null , errorKeyList)
//		ctUtil.missingPOLLocationCode(eventCS2Cde, current_Body.Route.FirstPOL, true, null , errorKeyList)
//		ctUtil.missingPOLLocationName(eventCS2Cde, current_Body.Route.FirstPOL, true, null , errorKeyList)
//		ctUtil.missingPORCountryCode(eventCS2Cde, current_Body.Route.POR, true, null , errorKeyList)
//		ctUtil.missingPORLocationCode(eventCS2Cde, current_Body.Route.POR, true, null , errorKeyList)
//		ctUtil.missingPORLocationName(eventCS2Cde, current_Body.Route.POR, true, null , errorKeyList)
		return errorKeyList;
	}

	//add by Harry
	private void generateEDITail(MarkupBuilder outXml, def outputBodyCount) {
		outXml.'GE' {
			'E97_01' outputBodyCount
			'E28_02' '1'
		}
		outXml.'IEA' {
			'I16_01' '1'
			'I12_02' '1'
		}
	}
	
	
	public boolean pospValidation() {

	}
	
	private void generateEDIHeader(MarkupBuilder outXml, def appSessionId, ContainerMovement ContainerMovement, def currentSystemDt) {
//		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID.text());
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID);
//		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC?.text());
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0].GeneralInformation.SCAC);

		outXml.'ISA' {
			'I01_01' '00'
			'I02_02' '          '
			'I03_03' '00'
			'I04_04' '          '
			'I05_05' 'ZZ'
			'I06_06' 'CARGOSMART     '
			'I05_07' 'ZZ'
			'I07_08' '6039261600     '
			'I08_09' currentSystemDt.format("yyMMdd")
			'I09_10' currentSystemDt.format(HHmm)
			'I10_11' 'U'
			'I11_12' '00401'
			'I12_13' '1'
			'I13_14' '0'
			'I14_15' 'P'
			'I15_16' ':'
		}
		outXml.'GS' {
			'E479_01' 'QO'
			'E142_02' 'CARGOSMART'
			'E124_03' '6039261600'
			'E373_04' currentSystemDt.format(yyyyMMdd)
			'E337_05' currentSystemDt.format(HHmm)
			'E28_06' '1'
			'E455_07' 'X'
			'E480_08' '004010'
		}

	}

}






