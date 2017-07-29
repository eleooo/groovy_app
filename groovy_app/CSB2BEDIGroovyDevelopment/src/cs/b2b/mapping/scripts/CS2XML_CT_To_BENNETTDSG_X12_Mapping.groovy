

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
public class CS2XML_CT_To_BENNETTDSG_X12_Mapping {
	
	
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
	String getSCACTpId(String scac, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select tp_id from b2b_scac_tp_map  where SCAC = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(1000);
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
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('BENNETTDSG', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
		def scacTpId = getSCACTpId(current_Body.GeneralInformation.SCAC, conn)
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {



				'E157_03' vCS1EventCodeConversion

				'E373_04' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, yyyyMMdd)
				//
				'E161_05' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, HHmm)

				if(current_Body.Container?.ContainerNumber){
					def containerNumber = current_Body.Container?.ContainerNumber
					'E206_07' containerNumber.substring(0,4)
					'E207_08' containerNumber.substring(4,containerNumber.length())
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
					
				if(current_Body.Container?.ContainerCheckDigit){
					'E761_13' current_Body.Container.ContainerCheckDigit
				}
			}

			//Loop N9
			//for avoid duplicated N9
			current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber}.each { blNumber, current_BookingGeneralInfoGroup ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(blNumber,1,35)
				}
			}

			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber}.each { bookingNumber, current_BookingGeneralInfoGroup ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(bookingNumber,1,30)
				}
			}

			if ((current_Body.Container?.ContainerNumber || current_Body.Container?.ContainerCheckDigit)) {
				String eq = ''

				if(current_Body.Container?.ContainerNumber){
					eq = util.substring(current_Body.Container.ContainerNumber,1,10)
					if(current_Body.Container?.ContainerCheckDigit){
						eq = eq + current_Body.Container?.ContainerCheckDigit
					}
				}else{
					eq = current_Body.Container?.ContainerCheckDigit
				}
				'N9' {
					'E128_01' 'EQ'
					'E127_02' util.substring(eq,1,10)
				}
			}

			def carrierId  = getCarrierID(current_Body.GeneralInformation.SCAC,conn)

			def scacCode = getScacCode(carrierId,conn)

			'N9' {
				'E128_01' 'SCA'
				if(!scacCode.equals("")){
					'E127_02' scacCode
				}
			}


			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body.Route.OceanLeg){
				firstOceanLeg = current_Body.Route.OceanLeg[0]
				lastOceanLeg = current_Body.Route.OceanLeg[-1]
			}
			if(shipDir=='I'){
				'Q2' {

					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E597_01' util.substring(lastOceanLeg.SVVD.Discharge.LloydsNumber, 1, 7)
					}

					if (lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction) {
						'E55_09' lastOceanLeg.SVVD.Discharge.Voyage + lastOceanLeg.SVVD.Discharge.Direction
					}
					'E897_12' 'L'
					if (lastOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' lastOceanLeg.SVVD.Discharge.VesselName
					}
				}
			}else if(shipDir == 'O'){
				'Q2' {

					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E597_01' util.substring(firstOceanLeg.SVVD.Loading.LloydsNumber, 1, 7)
					}

					if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
						'E55_09' firstOceanLeg?.SVVD.Loading.Voyage + firstOceanLeg?.SVVD.Loading.Direction
					}
					'E897_12' 'L'
					if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' firstOceanLeg.SVVD.Loading.VesselName
					}
				}
			}

			//R4


			'Loop_R4' {
				def unLocationCode = current_Body.Event.Location.LocationCode.UNLocationCode
				def eventLocationName = current_Body.Event.Location.LocationName
				def eventCountryCode = current_Body.Event.Location.CSStandardCity.CSCountryCode
				//Map event Loc "R4"
				def R401 = '5'
				def R402 = unLocationCode==null||unLocationCode.equals("")?current_Body.Event.Location.LocationCode?.SchedKDType:"UN"
				def R403 = unLocationCode==null||unLocationCode.equals("")?current_Body.Event.Location.LocationCode?.SchedKDCode:unLocationCode
				def R404 = (eventLocationName==null||eventLocationName.equals(""))?current_Body.Event.Location.CityDetails.City:eventLocationName
				def R405 = eventCountryCode

				def R408 = getProvinceCode(unLocationCode,eventCountryCode,conn)

				'R4' {
					'E115_01' R401
					if(R402!=null && !R402.equals("")) 'E309_02' R402
					if(R403!=null && !R403.equals("")) 'E310_03' R403
					if(R404!=null && !R404.equals("")) 'E114_04' util.substring(R404,1,24)
					if(R405!=null && !R405.equals("")) 'E26_05'  R405
					if(R408!=null && !R408.equals(""))   'E156_08'  R408
				}
				def eventDT = current_Body.Event.EventDT?.LocDT
				if(eventDT!=null&&!eventDT.equals("")){
					'DTM' {

						'E374_01' '140'
						'E373_02' util.convertDateTime(eventDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(eventDT, xmlDateTimeFormat, HHmm)
					}
				}
			}

			/*
			 POR
			 */

			'Loop_R4' {
				POR POR = current_Body.Route.POR
				def routeOriginUnLocationCode = POR?.CityDetails.LocationCode?.UNLocationCode
				def routeOriginCountryCode =  POR.CSStandardCity?.CSCountryCode
				def routeOriginProvinceStateCode = POR.CityDetails?.State
				def routeOriginScheduleKDCodeType = POR.CityDetails.LocationCode?.SchedKDType
				def routeOriginScheduleKDCode = POR.CityDetails.LocationCode?.SchedKDCode
				def routeOriginCity = POR.CityDetails?.City

				def R401 = 'R'
				def R402 = routeOriginUnLocationCode==null||routeOriginUnLocationCode.equals("")?routeOriginScheduleKDCodeType:"UN"
				def R403 = routeOriginUnLocationCode==null||routeOriginUnLocationCode.equals("")?routeOriginScheduleKDCode:routeOriginUnLocationCode
				def R404 = routeOriginCity
				def R405 = routeOriginCountryCode
				def R408 = util.substring(POR?.CSStandardCity?.CSStateCode,1,2)

				'R4' {
					'E115_01' R401
					if(R402!=null&&!R402.equals(""))'E309_02' R402
					if(R403!=null&&!R403.equals(""))'E310_03' R403
					if(R404!=null&&!R404.equals("")) 'E114_04' util.substring(R404,1,24)
					if(R405!=null&&!R405.equals("")&&R405.length()<=2) 'E26_05'  R405
					if(R408!=null&&!R408.equals("")) 'E156_08'  R408
				}

				def routeActCargoReceiptDateTime = current_Body.Route.CargoReceiptDT.find{it.attr_Indicator=='A'}?.LocDT
				def bkgInfoOBHaulageIndicator =  current_Body.BookingGeneralInfo[0].Haulage?.OutBound
				def routeEstFullPickupDateTime = current_Body.Route.FullPickupDT.find{it.attr_Indicator=="E"}?.LocDT
				def routeFullReturnCutoffDateTime = current_Body.Route.FullReturnCutoffDT.find{it.attr_Indicator=='A'}?.LocDT
				if(routeActCargoReceiptDateTime!=null&&!routeActCargoReceiptDateTime.equals("")){
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(routeActCargoReceiptDateTime, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(routeActCargoReceiptDateTime, xmlDateTimeFormat, HHmm)
					}
				}else if(bkgInfoOBHaulageIndicator.equals("C") && routeEstFullPickupDateTime!=null && routeEstFullPickupDateTime.equals("")){
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertDateTime(routeEstFullPickupDateTime, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(routeEstFullPickupDateTime, xmlDateTimeFormat, HHmm)
					}
				}else if(bkgInfoOBHaulageIndicator.equals("M") && routeFullReturnCutoffDateTime!=null && !routeFullReturnCutoffDateTime.equals("")){
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertDateTime(routeFullReturnCutoffDateTime, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(routeFullReturnCutoffDateTime, xmlDateTimeFormat, HHmm)
					}
				}else{
				}
			}
			/*
			 POD
			 */

			'Loop_R4' {
				def lastPOD = current_Body.Route.LastPOD
				def lastPODUNLocationCode = lastPOD.Port.LocationCode?.UNLocationCode
				def lastPODScheduleKDCodeType = lastPOD.Port.LocationCode?.SchedKDType
				def lastPODScheduleKDCode = lastPOD.Port.LocationCode?.SchedKDCode
				def lastPODPortName = lastPOD.Port?.PortName
				def lastPODCityName = lastPOD.Port?.City
				def lastPODCountryCode =lastPOD.Port?.CSCountryCode
				'R4' {
					def R401 = 'D'
					def R402 = lastPODUNLocationCode==null || lastPODUNLocationCode.equals("") ? lastPODScheduleKDCodeType : "UN"
					def R403 = lastPODUNLocationCode==null||lastPODUNLocationCode.equals("") ? lastPODScheduleKDCode : lastPODUNLocationCode
					def R404 = lastPODPortName == null || lastPODPortName?.equals("") ? lastPODCityName : lastPODPortName
					def R405 = lastPODCountryCode
					def R408 = util.substring(lastPOD?.CSStateCode,1,2)
					'E115_01' R401

					if (R402!=null && !R402.equals("")){
						'E309_02' R402
					}

					if (R403!=null && !R403.equals("")){
						'E310_03' R403
					}

					if (R404!=null && !R404.equals("")){
						'E114_04' util.substring(R404,1,24)
					}

					if (R405!=null && !R405.equals("") && R405.length()<=2){
						'E26_05'  R405
					}
					if(R408!=null && !R408.equals("")){
						'E156_08'  R408
					}
				}

				if(lastOceanLeg){
					def podArrivalDTA = lastOceanLeg.POD.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT
					def podArrivalDTE = lastOceanLeg.POD.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT
					if(podArrivalDTA && !podArrivalDTA.equals("")) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDTA,xmlDateTimeFormat,yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDTA,xmlDateTimeFormat,HHmm)
						}
					}else if(podArrivalDTE && !podArrivalDTE.equals("")){
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDTE,xmlDateTimeFormat,yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDTE,xmlDateTimeFormat,HHmm)
						}
					}
				}

			}

			/*
			 POL
			 */

			'Loop_R4' {

				def firstPOL =  current_Body.Route.FirstPOL
				def firstPOLUNLocationCode = firstPOL.Port.LocationCode.UNLocationCode
				def firstPOLScheduleKDCodeType = firstPOL.Port.LocationCode.SchedKDType
				def firstPOLScheduleKDCode = firstPOL.Port.LocationCode.SchedKDCode
				def firstPOLPortName = firstPOL.Port.PortName
				def firstPOLCityName = firstPOL.Port.City
				def firstPOLCountryCode = firstPOL.Port.CSCountryCode
				'R4' {
					def R401 = 'L'
					def R402 = firstPOLUNLocationCode==null || firstPOLUNLocationCode.equals("") ? firstPOLScheduleKDCodeType : "UN"
					def R403 = firstPOLUNLocationCode==null||firstPOLUNLocationCode.equals("") ?  firstPOLScheduleKDCode: firstPOLUNLocationCode
					def R404 = firstPOLPortName == null || firstPOLPortName?.equals("") ? firstPOLCityName : firstPOLPortName
					def R405 = firstPOLCountryCode
					def R408 = util.substring(firstPOL?.CSStateCode,1,2)
					'E115_01' R401

					if (R402!=null && !R402.equals("")){
						'E309_02' R402
					}

					if (R403!=null && !R403.equals("")){
						'E310_03' R403
					}

					if (R404!=null && !R404.equals("")){
						'E114_04' util.substring(R404,1,24)
					}

					if (R405!=null && !R405.equals("") && R405.length()<=2){
						'E26_05'  R405
					}
					if(R408!=null && !R408.equals("") ){
						'E156_08'  R408
					}

				}

				def polDepartureDtA = firstOceanLeg.POL.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT
				def polDepartureDtE = firstOceanLeg.POL.DepartureDT.find{it.attr_Indicator == 'E'}?.LocDT
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

			/*
			 FND
			 */
			'Loop_R4' {

				def FND = current_Body.Route.FND
				def FNDUNLocationCode = FND.CityDetails.LocationCode?.UNLocationCode
				def FNDScheduleKDCodeType = FND.CityDetails.LocationCode?.SchedKDType
				def FNDScheduleKDCode = FND.CityDetails.LocationCode?.SchedKDCode
				def FNDCountryCode =FND.CSStandardCity?.CSCountryCode
				def FNDCity = FND.CityDetails?.City

				def R401 = 'E'
				def R402 = FNDUNLocationCode==null || FNDUNLocationCode.equals("") ? FNDScheduleKDCodeType : "UN"
				def R403 = FNDUNLocationCode==null||FNDUNLocationCode.equals("") ? FNDScheduleKDCode : FNDUNLocationCode
				def R404 = FNDCity
				def R405 = FNDCountryCode
				def R408 = util.substring(FND?.CSStandardCity?.CSStateCode,1,2)
				'R4' {

					'E115_01' R401

					if (R402!=null && !R402.equals("")){
						'E309_02' R402
					}else {
						'E309_02' 'CI'
					}

					if (R403!=null && !R403.equals("")){
						'E310_03' R403
					}

					if (R404!=null && !R404.equals("")){
						'E114_04' util.substring(R404,1,24)
					}

					if (R405!=null && !R405.equals("") && R405.length()<=2){
						'E26_05'  R405
					}
					if (R408!=null && !R408.equals("")){
						'E156_08' R408
					}

				}


				def actCargoDeliveryDateTime = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator=='A'}?.LocDT
				def estArrivalAtFinalHubDateTime = current_Body.Route.ArrivalAtFinalHub.find{it.attr_Indicator=='E'}?.LocDT
				def estCargoDeliveryDateTime = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator=='E'}?.LocDT
				if(actCargoDeliveryDateTime && !actCargoDeliveryDateTime.equals("")){
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(actCargoDeliveryDateTime, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(actCargoDeliveryDateTime, xmlDateTimeFormat, HHmm)
					}
				}else if(estArrivalAtFinalHubDateTime!=null && !estArrivalAtFinalHubDateTime.equals('')){
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertDateTime(estArrivalAtFinalHubDateTime, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(estArrivalAtFinalHubDateTime, xmlDateTimeFormat, HHmm)
					}
				}else if(estCargoDeliveryDateTime!=null && !estCargoDeliveryDateTime.equals('')){
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertDateTime(estCargoDeliveryDateTime, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(estCargoDeliveryDateTime, xmlDateTimeFormat, HHmm)
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
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs("BENNETTDSG", conn)
		println(TP_ID)
		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

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

		//build GE, IEA
//		generateEDITail(outXml, outputBodyCount)

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

		//error cases
//				ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
//				ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event.EventDT.LocDT, true, null, errorKeyList)
//				ctUtil.missingBookingNumber(eventCS2Cde, current_Body.BookingGeneralInfo[0],true, null, errorKeyList)
//				ctUtil.missingContainerNumber(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
//				ctUtil.missingEventCountryCode(eventCS2Cde, current_Body.Event.Location.CSStandardCity, current_Body.Event, current_Body.Route.FND.CityDetails.LocationCode, true, null, errorKeyList)
//				ctUtil.missingEventLocationCode(eventCS2Cde, current_Body.Event.Location.LocationCode, current_Body.Event, true, null, errorKeyList)
//				ctUtil.missingEventLocationName(eventCS2Cde, current_Body.Event.Location, current_Body.Event.Location.CityDetails, current_Body.Event, current_Body.Route.FND.CityDetails.LocationCode, true, null, errorKeyList)
//				ctUtil.missingPODCountryCode(eventCS2Cde, current_Body.Route.lastPOD.Port, true, null, errorKeyList)
//				ctUtil.missingPODLocationCode(eventCS2Cde, current_Body.Route.LastPOD.Port.LocationCode, true, null, errorKeyList)
//				ctUtil.missingPODLocationName(eventCS2Cde, current_Body.Route.LastPOD.Port, true, null, errorKeyList)
//				ctUtil.missingFNDCountryCode(eventCS2Cde, current_Body.Route.FND.CSStandardCity, true, null, errorKeyList)
//				ctUtil.missingFNDLocationCode(eventCS2Cde, current_Body.Route.FND.CityDetails.LocationCode, true, null, errorKeyList)
//				ctUtil.missingFNDLocationName(eventCS2Cde, current_Body.Route.FND.CityDetails, true, null, errorKeyList)
//				ctUtil.missingPOLCountryCode(eventCS2Cde, current_Body.Route.FirstPOL.Port, true, null, errorKeyList)
//				ctUtil.missingPOLLocationCode(eventCS2Cde, current_Body.Route.FirstPOL.Port.LocationCode, true, null, errorKeyList)
//				ctUtil.missingPOLLocationName(eventCS2Cde, current_Body.Route.FirstPOL.Port, true, null, errorKeyList)
//				ctUtil.missingPORCountryCode(eventCS2Cde, current_Body.Route.POR[0].CSStandardCity, true, null, errorKeyList)
//				ctUtil.missingPORLocationCode(eventCS2Cde, current_Body.Route.POR[0].CityDetails.LocationCode, true, null, errorKeyList)
//				ctUtil.missingPORLocationName(eventCS2Cde, current_Body.Route.POR[0].CityDetails, true, null, errorKeyList)
		
				// obsolete cases
//				ctUtil.filterEventStatusCode(eventCS2Cde, 'CS130', false, null, errorKeyList)
//				ctUtil.filterEventStatusCode(eventCS2Cde, 'CS140', false, null, errorKeyList)
//				ctUtil.missingBlNumber(eventCS2Cde, current_Body.BLGeneralInfo, false, null, errorKeyList)
//				ctUtil.missingConsigneeCode(eventCS2Cde, current_Body.Party, false, null ,errorKeyList)
//				ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route.Inbound_intermodal_indicator, false, null, errorKeyList)

		
//		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
//		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event.EventDT.LocDT, true, null, errorKeyList)
//		ctUtil.missingBookingNumber(eventCS2Cde, current_Body.BookingGeneralInfo[0],true, null, errorKeyList)
//		ctUtil.missingContainerNumber(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
//		ctUtil.missingEventCountryCode(eventCS2Cde, current_Body.Event.Location.CSStandardCity, current_Body.Event, current_Body.Route.FND.CityDetails.LocationCode, true, null, errorKeyList)
//		ctUtil.missingEventLocationCode(eventCS2Cde, current_Body.Event.Location.LocationCode, current_Body.Event, true, null, errorKeyList)
//		ctUtil.missingEventLocationName(eventCS2Cde, current_Body.Event.Location, current_Body.Event.Location.CityDetails, current_Body.Event, current_Body.Route.FND.CityDetails.LocationCode, true, null, errorKeyList)
//		ctUtil.missingPODCountryCode(eventCS2Cde, current_Body.Route.lastPOD.Port, true, null, errorKeyList)
//		ctUtil.missingPODLocationCode(eventCS2Cde, current_Body.Route.LastPOD.Port.LocationCode, true, null, errorKeyList)
//		ctUtil.missingPODLocationName(eventCS2Cde, current_Body.Route.LastPOD.Port, true, null, errorKeyList)
//		ctUtil.missingFNDCountryCode(eventCS2Cde, current_Body.Route.FND.CSStandardCity, true, null, errorKeyList)
//		ctUtil.missingFNDLocationCode(eventCS2Cde, current_Body.Route.FND.CityDetails.LocationCode, true, null, errorKeyList)
//		ctUtil.missingFNDLocationName(eventCS2Cde, current_Body.Route.FND.CityDetails, true, null, errorKeyList)
//		ctUtil.missingPOLCountryCode(eventCS2Cde, current_Body.Route.FirstPOL.Port, true, null, errorKeyList)
//		ctUtil.missingPOLLocationCode(eventCS2Cde, current_Body.Route.FirstPOL.Port.LocationCode, true, null, errorKeyList)
//		ctUtil.missingPOLLocationName(eventCS2Cde, current_Body.Route.FirstPOL.Port, true, null, errorKeyList)
//		ctUtil.missingPORCountryCode(eventCS2Cde, current_Body.Route.POR.CSStandardCity, true, null, errorKeyList)
//		ctUtil.missingPORLocationCode(eventCS2Cde, current_Body.Route.POR.CityDetails.LocationCode, true, null, errorKeyList)
//		ctUtil.missingPORLocationName(eventCS2Cde, current_Body.Route.POR.CityDetails, true, null, errorKeyList)
		
		
		
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event.EventDT.LocDT, true, null, errorKeyList)
//		missingBookingNumber(eventCS2Cde, current_Body.BookingGeneralInfo[0],true, null, errorKeyList)
		ctUtil.missingContainerNumber(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
//		missingEventCountryCode(eventCS2Cde, current_Body.Event.Location.CSStandardCity, current_Body.Event, current_Body.Route.FND.CityDetails.LocationCode, true, null, errorKeyList)
//		missingEventLocationCode(eventCS2Cde, current_Body.Event.Location.LocationCode, current_Body.Event, true, null, errorKeyList)
//		missingEventLocationName(eventCS2Cde, current_Body.Event.Location, current_Body.Event.Location.CityDetails, current_Body.Event, current_Body.Route.FND.CityDetails.LocationCode, true, null, errorKeyList)
//		missingPODCountryCode(eventCS2Cde, current_Body.Route.lastPOD.Port, true, null, errorKeyList)
//		missingPODLocationCode(eventCS2Cde, current_Body.Route.LastPOD.Port.LocationCode, true, null, errorKeyList)
//		missingPODLocationName(eventCS2Cde, current_Body.Route.LastPOD.Port, true, null, errorKeyList)
//		missingFNDCountryCode(eventCS2Cde, current_Body.Route.FND.CSStandardCity, true, null, errorKeyList)
//		missingFNDLocationCode(eventCS2Cde, current_Body.Route.FND.CityDetails.LocationCode, true, null, errorKeyList)
//		missingFNDLocationName(eventCS2Cde, current_Body.Route.FND.CityDetails, true, null, errorKeyList)
//		missingPOLCountryCode(eventCS2Cde, current_Body.Route.FirstPOL.Port, true, null, errorKeyList)
//		missingPOLLocationCode(eventCS2Cde, current_Body.Route.FirstPOL.Port.LocationCode, true, null, errorKeyList)
//		missingPOLLocationName(eventCS2Cde, current_Body.Route.FirstPOL.Port, true, null, errorKeyList)
//		missingPORCountryCode(eventCS2Cde, current_Body.Route.POR.CSStandardCity, true, null, errorKeyList)
//		missingPORLocationCode(eventCS2Cde, current_Body.Route.POR.CityDetails.LocationCode, true, null, errorKeyList)
//		missingPORLocationName(eventCS2Cde, current_Body.Route.POR.CityDetails, true, null, errorKeyList)
//		
		
		
		return errorKeyList;
	}
	
	
	void missingBookingNumber(String eventCS2Cde, BookingGeneralInfo BookingGeneralInfo, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(BookingGeneralInfo?.CarrierBookingNumber?.toString())) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Booking Number']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingEventCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity,cs.b2b.core.mapping.bean.ct.Event Event, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(CSStandardCity?.CSCountryCode?.toString())) {
			if(!(Event.CS1Event.toString()).equals("CS180") || Event.CS1Event.toString().equals("CS180") && StringUtil.isEmpty(LocationCode.UNLocationCode.toString())){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Country Code']
				errorKeyList.add(errorKey)
			}
		}
	}
	
	
	void missingEventLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LocationCode?.UNLocationCode?.toString()) && StringUtil.isEmpty(LocationCode?.SchedKDCode?.toString())) {
			if(!(Event.CS1Event.toString()).equals("CS180") || Event.CS1Event.toString().equals("CS180") && StringUtil.isEmpty(LocationCode.UNLocationCode.toString()) && StringUtil.isEmpty(LocationCode.SchedKDCode.toString())){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Location Code for']
				errorKeyList.add(errorKey)
			}
		}
	}
	
	void missingEventLocationName(String eventCS2Cde, Location Location, cs.b2b.core.mapping.bean.ct.CityDetails CityDetails, cs.b2b.core.mapping.bean.ct.Event Event, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(Location?.LocationName?.toString()) && StringUtil.isEmpty(Location?.CityDetails?.City?.toString())) {
			if(!(Event.CS1Event.toString()).equals("CS180") || Event.CS1Event.toString().equals("CS180") && StringUtil.isEmpty(LocationCode.UNLocationCode.toString())){
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Location Name']
				errorKeyList.add(errorKey)
			}
		}
	}
	
	
	void missingPODCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.Port Port, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(Port?.CSCountryCode?.toString())) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Country Code']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingPODLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LocationCode?.UNLocationCode?.toString()) && StringUtil.isEmpty(LocationCode?.SchedKDCode?.toString())) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Location Code for']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingPODLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.Port Port, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Port.PortName.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + ' -Missing POD Location Name']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingFNDCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(CSStandardCity.CSCountryCode.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Country Code']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingFNDLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode.UNLocationCode.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Code for']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingFNDLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.CityDetails CityDetails, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(CityDetails.City.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Name']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingPOLCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.Port Port, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Port.CSCountryCode.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Country Code']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingPOLLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode.UNLocationCode.toString()) && StringUtil.isEmpty(LocationCode.SchedKDType.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Code for']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingPOLLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.Port Port, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Port.PortName.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Name']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingPORCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(CSStandardCity.CSCountryCode.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Country Code']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingPORLocationCode(String eventCS2Cde, LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode.UNLocationCode.toString()) && StringUtil.isEmpty(LocationCode.SchedKDCode.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Code for']
			errorKeyList.add(errorKey)
		}
	}
	
	
	void missingPORLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.CityDetails CityDetails, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(CityDetails.City.toString())){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Name']
			errorKeyList.add(errorKey)
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public boolean pospValidation() {

	}
	String getCarrierID(String scac, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select c.id from b2b_ocean_carrier c where c.scac = ?";

		try {
			pre = conn.prepareStatement(sql);
			//		pre.setMaxRows(getDBRowLimit());
			//		pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setMaxRows(1000);
			pre.setQueryTimeout(10);
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
	String getScacCode(String carrierId,Connection conn){
		if(conn==null) return ""
		if(carrierId==null||carrierId.equals('')) return ""
		PreparedStatement pre = null
		ResultSet rs = null
		String sql = "select scac_cde from b2b_cde_conversion where convert_type_id = 'CS1CarrierID' and ext_cde = ?"
		String scacCode = ""
		try{
			pre = conn.prepareStatement(sql)
			pre.setMaxRows(1000);
			pre.setQueryTimeout(10);
			pre.setString(1, carrierId);
			rs = pre.executeQuery();
			if(rs.next()){
				scacCode = rs.getString(1)
			}
		}finally{
			if(rs!=null)	rs.close()
			if(pre!=null) 	pre.close()
		}
		return scacCode
	}

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

	private void generateEDIHeader(MarkupBuilder outXml, def appSessionId, ContainerMovement ContainerMovement, def currentSystemDt) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID);
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC);

		outXml.'ISA' {
			'I01_01' '00'
			'I02_02' '          '
			'I03_03' '00'
			'I04_04' '          '
			'I05_05' 'ZZ'
			'I06_06' 'CARGOSMART     '
			'I05_07' 'ZZ'
			'I07_08' 'APLUNET        '
			'I08_09' currentSystemDt.format("yyMMdd")
			'I09_10' currentSystemDt.format(HHmm)
			'I10_11' 'U'
			'I11_12' '00401'
			'I12_13' '###edi_Isa_Ctrl_Number###'
			'I13_14' '0'
			'I14_15' 'P'
			'I15_16' '>'
		}
		outXml.'GS' {
			'E479_01' 'QO'
			'E142_02' 'CARGOSMART'
			'E124_03' 'APLUNET'
			'E373_04' currentSystemDt.format(yyyyMMdd)
			'E337_05' currentSystemDt.format(HHmm)
			'E28_06' '###edi_Group_Ctrl_Number###'
			'E455_07' 'X'
			'E480_08' '004010'
		}

	}

	private void generateEDITail(MarkupBuilder outXml, def outputBodyCount) {
		outXml.'GE' {
			'E97_01' outputBodyCount
			'E28_02' '###edi_Group_Ctrl_Number###'
		}
		outXml.'IEA' {
			'I16_01' '1'
			'I12_02' '###edi_Isa_Ctrl_Number###'
		}
	}

}






