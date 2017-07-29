import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List;
import java.util.Map;

import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.LocationCode
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.BookingGeneralInfo
import cs.b2b.core.mapping.bean.ct.CityDetails
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.Event
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.Location
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * @author Kieren
 * BENNETTDSG CT initialize on 2017/1/16
 */
public class CUS_CS2CTXML_315_BENNETTDSG {


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

	def firstScac = null

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
		def vCS1Event = current_Body?.Event?.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('BENNETTDSG', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
		
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '-999'
			}
			'B4' {
				if ( (vCS1Event == 'CS060' || vCS1Event == 'CS090') && current_Body?.GeneralInformation?.TransportMode?.toUpperCase() == 'FEEDER')
					'E157_03' 'AP'
				else if (vCS1Event == 'CS050' && current_Body?.GeneralInformation?.TransportMode?.toUpperCase() == 'RAIL')
					'E157_03' 'RL'
				else
					'E157_03' vCS1EventCodeConversion

				'E373_04' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)

				if(current_Body.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)?.trim()
				}

				if (util.substring(current_Body.Container?.ContainerNumber, 5, 6)?.trim()) {
					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, 6)?.trim()
				}

				if (!current_Body.Container?.ContainerStatus&& (vCS1Event=='CS210'||vCS1Event=='CS010')) {
					'E578_09' 'E'
				}else if (!current_Body.Container?.ContainerStatus&& (vCS1Event!='CS210' && vCS1Event!='CS010')) {
					'E578_09' 'L'
				}else if(current_Body.Container?.ContainerStatus=='Empty'){
					'E578_09' 'E'
				}else if(current_Body.Container?.ContainerStatus == 'Laden'){
					'E578_09' 'L'
				}

				'E761_13'  util.substring(current_Body.Container?.ContainerCheckDigit, 1,2)?.trim()
			}


			//Loop N9
			current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.each{BL ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(BL?.BLNumber?.trim(), 1, 30)?.trim()

				}
			}

			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.each {BN ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(BN?.CarrierBookingNumber?.trim(), 1, 30)?.trim()
				}
			}

			if(current_Body.Container?.ContainerNumber){
				'N9' {
					'E128_01' 'EQ'
					'E127_02' util.substring(current_Body.Container?.ContainerNumber,1,10)
				}
			}

			// CarrierScacCode
			if(firstScac){
				'N9'{
					'E128_01' 'SCA'
					'E127_02' firstScac
				}
			}


			// Q2
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body?.Route?.OceanLeg){
				firstOceanLeg = current_Body?.Route?.OceanLeg[0]
				lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
			}

			if(shipDir=='I'){
				'Q2' {

					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E597_01' util?.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)?.trim()
					}

					if (lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction) {
						'E55_09' ((lastOceanLeg?.SVVD?.Discharge?.Voyage ? lastOceanLeg?.SVVD?.Discharge?.Voyage:"") + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction:""))
					}
					'E897_12' 'L'
					if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
						'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName?.trim(), 1, 28)?.trim()
					}
				}
			}else if(shipDir == 'O'){
				'Q2' {

					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)?.trim()
					}

					if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
						'E55_09' ((firstOceanLeg?.SVVD?.Loading?.Voyage ? firstOceanLeg?.SVVD?.Loading?.Voyage:"") + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction:""))
					}
					'E897_12' 'L'
					if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName?.trim(), 1, 28)?.trim()
					}
				}
			}

			//R4


			'Loop_R4' {
				def unLocationCode = current_Body?.Event?.Location?.LocationCode?.UNLocationCode
				def eventLocationName = current_Body?.Event?.Location?.LocationName
				def eventCountryCode = current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode
				//Map event Loc "R4"
				def R401 = '5'
				def R402 = unLocationCode==null||unLocationCode.equals("")?current_Body?.Event?.Location?.LocationCode?.SchedKDType:"UN"
				def R403 = unLocationCode==null||unLocationCode.equals("")?current_Body?.Event?.Location?.LocationCode?.SchedKDCode:unLocationCode
				def R404 = (eventLocationName==null||eventLocationName.equals(""))?current_Body?.Event?.Location?.CityDetails?.City:eventLocationName?.trim()
				def R405 = eventCountryCode

				

				if (R403 && R404) {
					'R4' {
						'E115_01' R401
						if(R402!=null && !R402?.equals("")) 'E309_02' R402
						if(R403!=null && !R403?.equals("")) 'E310_03' R403
						if(R404!=null && !R404?.equals("")) 'E114_04' util.substring(R404,1,24)?.trim()
						if(R405!=null && !R405?.equals("")) 'E26_05'  R405
						
					//As Tracy's email, Directly use CSStateCode value in CS2XML
					//	def R408 = getProvinceCode(unLocationCode,eventCountryCode,conn)
						def  R408 = current_Body?.Event?.Location?.CSStandardCity?.CSStateCode?.trim()
						 
						if(R408!=null && !R408?.equals(""))   'E156_08'  R408
					}
					def eventDT = current_Body?.Event?.EventDT?.LocDT
					if(eventDT!=null&&!eventDT.equals("")){
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(eventDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(eventDT, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}

			/*
			 POR
			 */

			'Loop_R4' {
				POR POR = current_Body?.Route?.POR
				def routeOriginUnLocationCode = POR?.CityDetails?.LocationCode?.UNLocationCode
				def routeOriginCountryCode =  POR?.CSStandardCity?.CSCountryCode
				def routeOriginProvinceStateCode = POR?.CityDetails?.State
				def routeOriginScheduleKDCodeType = POR?.CityDetails?.LocationCode?.SchedKDType
				def routeOriginScheduleKDCode = POR?.CityDetails?.LocationCode?.SchedKDCode
				def routeOriginCity = POR?.CityDetails?.City

				def R401 = 'R'
				def R402 = routeOriginUnLocationCode==null||routeOriginUnLocationCode?.equals("")?routeOriginScheduleKDCodeType:"UN"
				def R403 = routeOriginUnLocationCode==null||routeOriginUnLocationCode?.equals("")?routeOriginScheduleKDCode:routeOriginUnLocationCode
				def R404 = routeOriginCity?.trim()
				def R405 = routeOriginCountryCode
				def R408 = util.substring(POR?.CSStandardCity?.CSStateCode,1,2)

				if (R403 && R404) {
					'R4' {
						'E115_01' R401
						if(R402!=null&&!R402?.equals(""))'E309_02' R402
						if(R403!=null&&!R403?.equals(""))'E310_03' R403
						if(R404!=null&&!R404?.equals("")) 'E114_04' util.substring(R404,1,24)?.trim()
						if(R405!=null&&!R405?.equals("")&&R405?.length()<=2) 'E26_05'  R405
						if(R408!=null&&!R408?.equals("")) 'E156_08'  R408
					}

					def routeActCargoReceiptDateTime = current_Body?.Route?.CargoReceiptDT?.find{it.attr_Indicator=='A'}?.LocDT
					def bkgInfoOBHaulageIndicator =  current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound
					def routeEstFullPickupDateTime = current_Body?.Route?.FullPickupDT?.find{it.attr_Indicator=="E"}?.LocDT
					def routeFullReturnCutoffDateTime = current_Body?.Route?.FullReturnCutoffDT?.find{it.attr_Indicator=='A'}?.LocDT
					if(routeActCargoReceiptDateTime){
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(routeActCargoReceiptDateTime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(routeActCargoReceiptDateTime, xmlDateTimeFormat, HHmm)
						}
					}else if(bkgInfoOBHaulageIndicator?.equals("C") && routeEstFullPickupDateTime){
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(routeEstFullPickupDateTime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(routeEstFullPickupDateTime, xmlDateTimeFormat, HHmm)
						}
					}else if(bkgInfoOBHaulageIndicator?.equals("M") && routeFullReturnCutoffDateTime){
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(routeFullReturnCutoffDateTime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(routeFullReturnCutoffDateTime, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
			/*
			 POD
			 */

			'Loop_R4' {
				def lastPOD = current_Body?.Route?.LastPOD
				def lastPODUNLocationCode = lastPOD?.Port?.LocationCode?.UNLocationCode
				def lastPODScheduleKDCodeType = lastPOD?.Port?.LocationCode?.SchedKDType
				def lastPODScheduleKDCode = lastPOD?.Port?.LocationCode?.SchedKDCode
				def lastPODPortName = lastPOD?.Port?.PortName
				def lastPODCityName = lastPOD?.Port?.City
				def lastPODCountryCode =lastPOD?.Port?.CSCountryCode

				def R401 = 'D'
				def R402 = lastPODUNLocationCode==null || lastPODUNLocationCode?.equals("") ? lastPODScheduleKDCodeType : "UN"
				def R403 = lastPODUNLocationCode==null||lastPODUNLocationCode?.equals("") ? lastPODScheduleKDCode : lastPODUNLocationCode
				def R404 = lastPODPortName == null || lastPODPortName?.equals("") ? lastPODCityName?.trim() : lastPODPortName?.trim()
				def R405 = lastPODCountryCode
				def R408 = util.substring(lastPOD?.CSStateCode,1,2)
				if (R403 && R404 ) {
					'R4' {

						'E115_01' R401

						if (R402!=null && !R402?.equals("")){
							'E309_02' R402
						}

						if (R403!=null && !R403?.equals("")){
							'E310_03' R403
						}

						if (R404!=null && !R404?.equals("")){
							'E114_04' util.substring(R404,1,24)?.trim()
						}

						if (R405!=null && !R405?.equals("") && R405?.length()<=2){
							'E26_05'  R405
						}
						if(R408!=null && !R408?.equals("")){
							'E156_08'  R408
						}
					}

					if(lastOceanLeg){
						def podArrivalDTA = lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
						def podArrivalDTE = lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
						if(podArrivalDTA && !podArrivalDTA?.equals("")) {
							'DTM' {
								'E374_01' '140'
								'E373_02' util.convertDateTime(podArrivalDTA,xmlDateTimeFormat,yyyyMMdd)
								'E337_03' util.convertDateTime(podArrivalDTA,xmlDateTimeFormat,HHmm)
							}
						}else if(podArrivalDTE && !podArrivalDTE?.equals("")){
							'DTM' {
								'E374_01' '139'
								'E373_02' util.convertDateTime(podArrivalDTE,xmlDateTimeFormat,yyyyMMdd)
								'E337_03' util.convertDateTime(podArrivalDTE,xmlDateTimeFormat,HHmm)
							}
						}
					}
				}

			}

			/*
			 POL
			 */

			'Loop_R4' {

				def firstPOL =  current_Body?.Route?.FirstPOL
				def firstPOLUNLocationCode = firstPOL?.Port.LocationCode?.UNLocationCode
				def firstPOLScheduleKDCodeType = firstPOL?.Port.LocationCode?.SchedKDType
				def firstPOLScheduleKDCode = firstPOL?.Port?.LocationCode?.SchedKDCode
				def firstPOLPortName = firstPOL?.Port?.PortName
				def firstPOLCityName = firstPOL?.Port?.City
				def firstPOLCountryCode = firstPOL?.Port?.CSCountryCode

				def R401 = 'L'
				def R402 = firstPOLUNLocationCode==null || firstPOLUNLocationCode?.equals("") ? firstPOLScheduleKDCodeType : "UN"
				def R403 = firstPOLUNLocationCode==null||firstPOLUNLocationCode?.equals("") ?  firstPOLScheduleKDCode: firstPOLUNLocationCode
				def R404 = firstPOLPortName == null || firstPOLPortName?.equals("") ? firstPOLCityName?.trim() : firstPOLPortName?.trim()
				def R405 = firstPOLCountryCode
				def R408 = util.substring(firstPOL?.CSStateCode,1,2)
				if (R403 && R404) {
					'R4' {

						'E115_01' R401

						if (R402!=null && !R402?.equals("")){
							'E309_02' R402
						}

						if (R403!=null && !R403?.equals("")){
							'E310_03' R403
						}

						if (R404!=null && !R404?.equals("")){
							'E114_04' util.substring(R404,1,24)?.trim()
						}

						if (R405!=null && !R405?.equals("") && R405?.length()<=2){
							'E26_05'  R405
						}
						if(R408!=null && !R408?.equals("") ){
							'E156_08'  R408
						}

					}

					def polDepartureDtA = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					def polDepartureDtE = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
					if (polDepartureDtA) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, HHmm)

						}
					} else if (polDepartureDtE) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}

			/*
			 FND
			 */
			'Loop_R4' {

				def FND = current_Body?.Route?.FND
				def FNDUNLocationCode = FND?.CityDetails?.LocationCode?.UNLocationCode
				def FNDScheduleKDCodeType = FND?.CityDetails?.LocationCode?.SchedKDType
				def FNDScheduleKDCode = FND?.CityDetails?.LocationCode?.SchedKDCode
				def FNDCountryCode =FND?.CSStandardCity?.CSCountryCode
				def FNDCity = FND?.CityDetails?.City

				def R401 = 'E'
				def R402 = FNDUNLocationCode==null || FNDUNLocationCode?.equals("") ? FNDScheduleKDCodeType : "UN"
				def R403 = FNDUNLocationCode==null||FNDUNLocationCode?.equals("") ? FNDScheduleKDCode : FNDUNLocationCode
				def R404 = FNDCity?.trim()
				def R405 = FNDCountryCode
				def R408 = util.substring(FND?.CSStandardCity?.CSStateCode,1,2)
				if (R403 && R404) {
					'R4' {

						'E115_01' R401

						if (R402!=null && !R402?.equals("")){
							'E309_02' R402
						}else {
							'E309_02' 'CI'
						}

						if (R403!=null && !R403?.equals("")){
							'E310_03' R403
						}

						if (R404!=null && !R404?.equals("")){
							'E114_04' util.substring(R404,1,24)?.trim()
						}

						if (R405!=null && !R405?.equals("") && R405?.length()<=2){
							'E26_05'  R405
						}
						if (R408!=null && !R408?.equals("")){
							'E156_08' R408
						}

					}

					def actCargoDeliveryDateTime = null
					def estArrivalAtFinalHubDateTime = null
					def estCargoDeliveryDateTime = null
					// actCargoDeliveryDateTime
					actCargoDeliveryDateTime = current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='A'}?.LocDT
					// estArrivalAtFinalHubDateTime 
					// Louis, some logics come from the first APP
					if (current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator=='E'}?.LocDT)
						estArrivalAtFinalHubDateTime = current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator=='E'}?.LocDT
					else if (current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator=='E'}?.LocDT && current_Body?.GeneralInformation?.SCAC != 'OOLU')
						estArrivalAtFinalHubDateTime =  current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator=='E'}?.LocDT
					// estCargoDeliveryDateTime
					estCargoDeliveryDateTime = current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='E'}?.LocDT
					def inboundIndicator = current_Body?.BookingGeneralInfo[0]?.Haulage?.InBound?.toUpperCase()?.trim()
					if(actCargoDeliveryDateTime) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(actCargoDeliveryDateTime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(actCargoDeliveryDateTime, xmlDateTimeFormat, HHmm)
						}
					} else if (estArrivalAtFinalHubDateTime && inboundIndicator == 'M'){
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(estArrivalAtFinalHubDateTime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(estArrivalAtFinalHubDateTime, xmlDateTimeFormat, HHmm)
						}
					} else if (estCargoDeliveryDateTime && inboundIndicator == 'C'){
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(estCargoDeliveryDateTime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(estCargoDeliveryDateTime, xmlDateTimeFormat, HHmm)
						}
					}
				}
				
				
			}


			'SE' {
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


		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs("BENNETTDSG", conn)
		//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		// Modified by Louis , global variable : firstScac
		if (bodies.size() > 0 && bodies[0].GeneralInformation?.SCAC) {
			firstScac = bodies[0].GeneralInformation?.SCAC
		}

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('BENNETTDSG', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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

		//End root node
		outXml.nodeCompleted(null,T315)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		//		println bizKeyWriter.toString();
		//		println csuploadWriter.toString();

		//promote csupload and bizkey to session
		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID);
		if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);

		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			//			result = writer?.toString()
			result = util.cleanXml(writer?.toString())
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()

		return result;
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		// B4 03 04
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		ctUtil.EventIsNotSubscribedByParnter(eventCS2Cde, eventExtCde, false, null, errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body?.Event?.EventDT?.LocDT, true, null, errorKeyList)

		// N9
		ctUtil.missingBookingNumber(eventCS2Cde, current_Body?.BookingGeneralInfo,true, null, errorKeyList)
		ctUtil.missingContainerNumber(eventCS2Cde, current_Body?.Container, true, null, errorKeyList)

		// R4 order matters 
		missingEventCountryCode(eventCS2Cde, current_Body, true, null, errorKeyList)
		missingEventLocationCodeFor(eventCS2Cde, current_Body, true, null, errorKeyList)
		missingEventLocationName(eventCS2Cde, current_Body, true, null, errorKeyList)
		missingPORLocationCodeFor(eventCS2Cde, current_Body?.Route?.POR, true, null, errorKeyList)
		ctUtil.missingPORCountryCode(eventCS2Cde, current_Body?.Route?.POR, current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode, true, null, errorKeyList)
		ctUtil.missingPORLocationName(eventCS2Cde, current_Body?.Route?.POR, true, null, errorKeyList)
		ctUtil.missingPODCountryCode(eventCS2Cde, current_Body?.Route?.lastPOD, true, null, errorKeyList)
		missingPODLocationCodeFor(eventCS2Cde, current_Body?.Route?.LastPOD, true, null, errorKeyList)
		ctUtil.missingPODLocationName(eventCS2Cde, current_Body?.Route?.LastPOD, true, null, errorKeyList)
		ctUtil.missingPOLCountryCode(eventCS2Cde, current_Body?.Route?.FirstPOL, true, null, errorKeyList)
		missingPOLLocationCodeFor(eventCS2Cde, current_Body?.Route?.FirstPOL, true, null, errorKeyList)
		ctUtil.missingPOLLocationName(eventCS2Cde, current_Body?.Route?.FirstPOL, true, null, errorKeyList)
		missingFNDCountryCode(eventCS2Cde, current_Body?.Route?.FND, true, null, errorKeyList)
		missingFNDLocationCodeFor(eventCS2Cde, current_Body?.Route?.FND, true, null, errorKeyList)
		missingFNDLocationName(eventCS2Cde, current_Body?.Route?.FND, true, null, errorKeyList)

		// CS260
		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body?.Route?.Inbound_intermodal_indicator, false, null, errorKeyList)

		return errorKeyList;
	}

	
	void missingEventCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Body current_Body,  boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		def eventCountryCode = current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode
		def finalDestinationCountryCode = current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode
		if (StringUtil.isEmpty(eventCountryCode)) {
			if(eventCS2Cde != "CS180" || (eventCS2Cde == "CS180" && StringUtil.isEmpty(finalDestinationCountryCode))) {
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Country Code']
				errorKeyList.add(errorKey)
			}
		}
	}

	
	void missingEventLocationCodeFor(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Body current_Body, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		def EventLoc = current_Body?.Event?.Location?.LocationCode
		def FNDLoc = current_Body?.Route?.FND?.CityDetails?.LocationCode 
		def eventLocation = current_Body?.Event?.Location?.CityDetails?.City + '/' + util.substring(current_Body?.Event?.Location?.CityDetails?.State?.toUpperCase(), 1, 2) + '/' +  util.substring(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode, 1, 2)
		if (StringUtil.isEmpty(EventLoc?.UNLocationCode) && StringUtil.isEmpty(EventLoc?.SchedKDCode)) {
			if(eventCS2Cde != "CS180" || eventCS2Cde == "CS180" && StringUtil.isEmpty(FNDLoc?.UNLocationCode) && StringUtil.isEmpty(FNDLoc?.SchedKDCode.toString())){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg + eventLocation : eventCS2Cde + ' - Missing Event Location Code for ' + eventLocation]
				errorKeyList.add(errorKey)
			}
		}
	}
	
	
	void missingEventLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Body current_Body,  boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		def EventLoc = current_Body?.Event?.Location
		def FNDCity = current_Body?.Route?.FND?.CityDetails?.City
		if (StringUtil.isEmpty(EventLoc?.LocationName) && StringUtil.isEmpty(EventLoc?.CityDetails?.City)) {
			if(eventCS2Cde != "CS180" || eventCS2Cde == "CS180" && StringUtil.isEmpty(FNDCity)){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Location Name']
				errorKeyList.add(errorKey)
			}
		}
	}

	void missingFNDCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CSStandardCity?.CSCountryCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingFNDLocationCodeFor(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		def FNDLocation = FND?.CityDetails?.City + '/' + util.substring(FND?.CSStandardCity?.CSStateCode, 1, 2) + '/' + util.substring(FND?.CSStandardCity?.CSCountryCode, 1, 2)
		if(StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg + FNDLocation: eventCS2Cde + '- Missing FND Location Code for ' + FNDLocation]
			errorKeyList.add(errorKey)
		}
	}


	void missingFNDLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Name']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLLocationCodeFor(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		def FirstPOLLocation = FirstPOL?.Port?.City + '/' + FirstPOL?.CSStateCode + '/' + FirstPOL?.Port?.CSCountryCode
		if(StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg + FirstPOLLocation : eventCS2Cde + '- Missing POL Location Code for ' + FirstPOLLocation]
			errorKeyList.add(errorKey)
		}
	}


	void missingPORLocationCodeFor(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		def PORLocation = POR?.CityDetails?.City + '/' + util.substring(POR?.CSStandardCity?.CSStateCode, 1, 2) + '/' + util.substring(POR?.CSStandardCity?.CSCountryCode, 1, 2)
		if(StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg + PORLocation : eventCS2Cde + '- Missing POR Location Code for ' + PORLocation]
			errorKeyList.add(errorKey)
		}
	}
	
	void missingPODLocationCodeFor(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		def lastPODLocation = LastPOD?.Port?.City + '/' + LastPOD?.CSStateCode + '/' + LastPOD?.Port?.CSCountryCode
		if (StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.UNLocationCode) && StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.SchedKDCode)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg + lastPODLocation : eventCS2Cde + ' - Missing POD Location Code for ' + lastPODLocation]
			errorKeyList.add(errorKey)
		}
	}

	public boolean pospValidation() {

	}
    
	/* No need now
	String getProvinceCode(String eventLocationCodeUn,String eventCountryCode,Connection conn){
		if (conn==null) return ""
		if (eventLocationCodeUn==null||eventCountryCode==null||eventLocationCodeUn.equals("")||eventCountryCode.equals("")) return ""
		String sql = "select state_cde from css_city_list where un_locn_cde = ? and cntry_cde = '${eventCountryCode}'"
		PreparedStatement ps = null
		ResultSet rs = null
		String result = ""
		try{
			ps = conn.prepareStatement(sql)
			ps.setString(1,eventLocationCodeUn)
			//ps.setString(2,eventCountryCode)
			rs = ps.executeQuery()
			if(rs.next()) result = rs.getString(1)
		}finally{
			if(rs!=null)	rs.close()
			if(ps!=null)   ps.close()
		}

		return result==null?"":result
	}
	*/

}






