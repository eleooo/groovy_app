package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import org.apache.commons.lang3.SerializationUtils

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List
import java.util.Map;

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

class CUS_CS2CTXML_315_HDSUPPLYDAMCO {
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
		def vCS1EventCodeConversion = util.getConversion('HDSUPPLYDAMCO', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  util.getConversionWithoutTP('CT', 'O', 'HDSUPPLYDAMCO', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
		//		def currentSTCtrlNum = '###edi_Group_Ctrl_Number###' + String.format("%04d", 1)

		int OceanLegNum = current_Body.Route.OceanLeg.size();
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {
				'E152_01' 'ZZZ'
				
				if ( (vCS1Event == 'CS160' || vCS1Event == 'CS195') && current_Body?.GeneralInformation?.TransportMode?.toUpperCase() == 'RAIL' ) {
					def int_cde = vCS1Event + 'RAIL'
					'E157_03' util.getConversion('HDSUPPLYDAMCO', 'CT', 'O', 'EventStatusCode', int_cde, conn)
				} else {
					'E157_03' vCS1EventCodeConversion
				}


				'E373_04' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, HHmm)
				if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode){
					'E159_06' current_Body.Event?.Location?.LocationCode?.SchedKDCode
				}else if(current_Body.Event?.Location?.LocationCode?.UNLocationCode){
					'E159_06' current_Body.Event?.Location?.LocationCode?.UNLocationCode
				}
//				else {
//					'E159_06' ''
//				}

				if(current_Body.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)?.trim()
				}


				if(util.substring(current_Body.Container?.ContainerNumber, 5, 6)){
//					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, current_Body.Container?.ContainerNumber?.length())
					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, 6)
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
				if(current_Body.Event?.Location?.LocationCode?.SchedKDCode){
					'E310_11' current_Body.Event?.Location?.LocationCode?.SchedKDCode
				}else if(current_Body.Event?.Location?.LocationCode?.UNLocationCode){
					'E310_11' current_Body.Event?.Location?.LocationCode?.UNLocationCode
				}
//				else {
//					'E310_11' ''
//				}
				if(current_Body.Event?.Location?.LocationCode?.SchedKDCode&&current_Body?.Event?.Location?.LocationCode?.SchedKDType=='K'){
					'E309_12' 'K'
				}else if(current_Body.Event?.Location?.LocationCode?.SchedKDCode&&current_Body?.Event?.Location?.LocationCode?.SchedKDType=='D'){
					'E309_12' 'D'
				}else if(current_Body.Event?.Location?.LocationCode?.UNLocationCode){
					'E309_12' 'UN'
				}



			}
			//Loop N9
			//for avoid duplicated N9
			//			def BMs = []
			//			def BNs = []
			//def EQs = []


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

			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body.Route?.OceanLeg){
				firstOceanLeg = current_Body.Route.OceanLeg[0]
				lastOceanLeg = current_Body.Route.OceanLeg[-1]
			}
		
			def CSSTDQ2ExtCDE=ctUtil.getCodeConversionbyCovertType('CT','O','CSSTDQ2',vCS1Event,conn)
			if(CSSTDQ2ExtCDE){
				if (shipDir == 'I') {
					'Q2' {
						if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
							'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)?.trim()
						}
						if (lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction) {
							'E55_09' lastOceanLeg?.SVVD?.Discharge?.Voyage + lastOceanLeg?.SVVD?.Discharge?.Direction
						}
						
						//					'E127_11' current_Body.GeneralInformation.SCAC.text()
						'E897_12' 'L'
						if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
							'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName?.trim(), 1, 28)?.trim()
						}
					}
				} else if (shipDir == 'O') {
					'Q2' {
						if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
							'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)?.trim()
						}

						if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
							'E55_09' firstOceanLeg?.SVVD?.Loading?.Voyage + firstOceanLeg?.SVVD?.Loading?.Direction
						}

						'E897_12' 'L'
						if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
							'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName?.trim(), 1, 28)?.trim()
						}
					}
				}

			}

			//R4
			POR POR =  current_Body.Route?.POR
			FirstPOL firstPOL =  current_Body.Route?.FirstPOL
			LastPOD lastPOD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND
			Event Event = current_Body?.Event


			//
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if (POR.CityDetails?.LocationCode?.SchedKDCode) {
						if (POR.CityDetails?.LocationCode?.SchedKDType) {
							'E309_02' POR.CityDetails?.LocationCode?.SchedKDType
						}						
					}else if (POR.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}
					if (POR.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' POR.CityDetails?.LocationCode?.SchedKDCode
					}else if (POR.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' POR.CityDetails?.LocationCode?.UNLocationCode
					}
					if (POR.CityDetails?.City) {
						'E114_04' util.substring(POR.CityDetails?.City, 1, 24)?.trim()
					}
					if (POR.CSStandardCity?.CSCountryCode && POR.CSStandardCity?.CSCountryCode?.length() >= 2){
						'E26_05' POR.CSStandardCity?.CSCountryCode
					}
					if (POR.CSStandardCity?.CSStateCode) {
						'E156_08' util.substring(POR.CSStandardCity?.CSStateCode, 1, 2)?.trim()
					}
				}

				LocDT porDTM = null
				def isAct = false
				if (current_Body.Route.CargoReceiptDT.find{it.attr_Indicator == 'A'}?.LocDT) {
					porDTM = current_Body.Route.CargoReceiptDT.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				} else if (current_Body.Route.FullPickupDT.find{it.attr_Indicator == 'E'}?.LocDT && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C') {
					porDTM = current_Body.Route.FullPickupDT.find{it.attr_Indicator == 'E'}?.LocDT
				} else if (current_Body.Route.FullReturnCutoffDT.find{it.attr_Indicator == 'A'}?.LocDT && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M') {
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
					'E115_01' 'L'
					if (firstPOL.Port?.LocationCode?.SchedKDCode) {
						if (firstPOL.Port?.LocationCode?.SchedKDType) {
							'E309_02' firstPOL.Port?.LocationCode?.SchedKDType
						}						
					} else if (firstPOL.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}
					if (firstPOL.Port?.LocationCode?.SchedKDCode) {
						'E310_03' util.substring(firstPOL.Port?.LocationCode?.SchedKDCode,1,5)?.trim()
					}else if (firstPOL.Port?.LocationCode?.UNLocationCode) {
						'E310_03' util.substring(firstPOL.Port?.LocationCode?.UNLocationCode,1,5)?.trim()
					}
					if (firstPOL.Port?.PortName) {
						'E114_04' util.substring(firstPOL.Port?.PortName, 1, 24)?.trim()
					}else if(firstPOL.Port?.City){
						'E114_04' util.substring(firstPOL.Port?.City,1,24)?.trim()
					}
					if (firstPOL.Port?.CSCountryCode && firstPOL.Port?.CSCountryCode?.length() >= 2){
						'E26_05' firstPOL.Port?.CSCountryCode
					}
					if (firstPOL.CSStateCode) {
						'E156_08' util.substring(firstPOL.CSStateCode, 1, 2)?.trim()
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
					'E115_01' 'D'
					if (lastPOD.Port?.LocationCode?.SchedKDCode) {
						if (lastPOD.Port?.LocationCode?.SchedKDType) {
							'E309_02' util.substring(lastPOD.Port?.LocationCode?.SchedKDType,1,2)?.trim()
						}
					}else if (lastPOD.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}
					if (lastPOD.Port?.LocationCode?.SchedKDCode) {
						'E310_03' util.substring(lastPOD.Port?.LocationCode?.SchedKDCode,1,5)?.trim()
					}else if (lastPOD.Port?.LocationCode?.UNLocationCode) {
						'E310_03' util.substring(lastPOD.Port?.LocationCode?.UNLocationCode,1,5)?.trim()
					}
					if (lastPOD.Port?.PortName) {
						'E114_04' util.substring(lastPOD.Port?.PortName, 1, 24)?.trim()
					}else if(lastPOD.Port?.City){
						'E114_04' util.substring(lastPOD.Port?.City,1,24)?.trim()
					}
					if (lastPOD.Port?.CSCountryCode && lastPOD.Port?.CSCountryCode?.length() >= 2){
						'E26_05' lastPOD.Port?.CSCountryCode?.trim()
					}
					if (lastPOD.CSStateCode) {
						'E156_08' util.substring(lastPOD.CSStateCode,1,2)?.trim()
					}
				}

				List oceanLegList = current_Body?.Route?.OceanLeg.findAll{! (StringUtil.isEmpty(it?.SVVD?.Loading?.Vessel) && StringUtil.isEmpty(it?.SVVD?.Loading?.Voyage) && StringUtil.isEmpty(it?.SVVD?.Loading?.VesselName)) }
				if (oceanLegList.size() > 0) {
					OceanLeg last_OceanLeg = oceanLegList[-1]
					if (last_OceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(last_OceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(last_OceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}
					} else if (last_OceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(last_OceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(last_OceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
						}
					}
				}
				
				

			}



			'Loop_R4' {
				'R4' {
					'E115_01' 'M'
					if (FND.CityDetails?.LocationCode?.SchedKDCode) {
						if (FND.CityDetails?.LocationCode?.SchedKDType) {
							'E309_02' util.substring(FND.CityDetails?.LocationCode?.SchedKDType,1,2)?.trim()
						}						
					}else if (FND.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}
					
					if (FND.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' util.substring(FND.CityDetails?.LocationCode?.SchedKDCode,1,5)?.trim()
					}else if (FND.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' util.substring(FND.CityDetails?.LocationCode?.UNLocationCode,1,5)?.trim()
					}
					if(FND.CityDetails?.City){
						'E114_04' util.substring(FND.CityDetails?.City,1,24)?.trim()
					}
					if (FND.CSStandardCity?.CSCountryCode && FND.CSStandardCity?.CSCountryCode?.length() >= 2){
						'E26_05' util.substring(FND.CSStandardCity?.CSCountryCode,1,2)?.trim()
					}
					if (FND.CSStandardCity?.CSStateCode) {
						'E156_08' util.substring(FND.CSStandardCity?.CSStateCode,1,2)?.trim()
					}
				}

				def isAct = false;
				def datetime;

				def eventDateTime = current_Body?.Event?.EventDT?.LocDT
//					println 'eventDateTime  ' + eventDateTime
				//1
				def actCargoDeliveryDateTime = current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT
//					println 'actCargoDeliveryDateTime  \t\t\t' + actCargoDeliveryDateTime
				//2
				def actArrivalAtFinalHubDateTime = null
				//In App 1 MapTransaction
				if (current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT) {
					actArrivalAtFinalHubDateTime = current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT
				} else if (current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT && current_Body.GeneralInformation?.SCAC != 'OOLU') {
					actArrivalAtFinalHubDateTime = current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
				}
//					println 'actArrivalAtFinalHubDateTime  \t\t\t' + actArrivalAtFinalHubDateTime?.toString() {};
				//4
				def estCargoDeliveryDateTime = current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT
//					println 'estCargoDeliveryDateTime  \t\t\t'  + estCargoDeliveryDateTime
				//5
				//In App 1 MapTransaction
				def estArrivalAtFinalHubDateTime = null
				if (current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT) {
					estArrivalAtFinalHubDateTime = current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT
				} else if (current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT && current_Body.GeneralInformation?.SCAC != 'OOLU') {
					estArrivalAtFinalHubDateTime = current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
				}
//					println 'estArrivalAtFinalHubDateTime  \t\t\t' + estArrivalAtFinalHubDateTime
				//8
				def estArrivalDate = current_Body?.Route?.OceanLeg[-1]?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
//					println 'estArrivalDate  \t\t\t' + estArrivalDate
				//9
				def actArrivalDate = null
				actArrivalDate = current_Body?.Route?.OceanLeg[-1]?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
//					println 'actArrivalDate  \t\t\t' + actArrivalDate


				if ( actCargoDeliveryDateTime && util.convertDateTime(eventDateTime, xmlDateTimeFormat, yyyyMMdd) <= util.convertDateTime(actCargoDeliveryDateTime, xmlDateTimeFormat, yyyyMMdd)) {
					isAct = true;
					datetime = actCargoDeliveryDateTime
				} else if (actArrivalAtFinalHubDateTime && util.convertDateTime(eventDateTime, xmlDateTimeFormat, yyyyMMdd) <= util.convertDateTime(actArrivalAtFinalHubDateTime, xmlDateTimeFormat, yyyyMMdd)) {
					isAct = true;
					datetime = actArrivalAtFinalHubDateTime
				} else if (estCargoDeliveryDateTime) {
					isAct = false;
					datetime = estCargoDeliveryDateTime
				} else if (estArrivalAtFinalHubDateTime) {
					isAct = false;
					datetime = estArrivalAtFinalHubDateTime
				} else if (estArrivalDate) {
					isAct = false;
					datetime = estArrivalDate
				} else if (actArrivalDate) {
					isAct = true;
					datetime = actArrivalDate
				} else {
					isAct = false
				}

//					println 'datetime  \t\t\t' + datetime

				//Jackson fix, to avoid empty DTM and lead to light bc validation error
				def R4_M_DTM=util.convertDateTime(datetime, xmlDateTimeFormat, yyyyMMdd)
				if(R4_M_DTM && R4_M_DTM?.length()>0)
				{
					if (isAct) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(datetime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(datetime, xmlDateTimeFormat, HHmm)
						}
					} else {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(datetime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(datetime, xmlDateTimeFormat, HHmm)
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
		def headerMsgDT = util.convertDateTime(ct.Header?.MsgDT?.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		//long ediIsaCtrlNumber = ctrlNos[0]
		//long ediGroupCtrlNum = ctrlNos[1]
		def txnErrorKeys = []

		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs('HDSUPPLYDAMCO', conn)
		//println(duplicatedPairs)

//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body?.Event?.CS1Event
			def eventExtCde = util.getConversion('HDSUPPLYDAMCO', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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
		ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID)
		if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC)
		
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
		//B4 03_157
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body?.Event?.EventDT?.LocDT, true, null, errorKeyList)
		// pInt_Cde Choose
		def tmp_eventExtCde
		if (['CS160', 'CS195'].contains(eventCS2Cde) && current_Body?.GeneralInformation?.TransportMode?.toUpperCase()  == 'RAIL') {
			tmp_eventExtCde = new cs.b2b.core.mapping.util.MappingUtil().getConversion('HDSUPPLYDAMCO', 'CT', 'O', 'EventStatusCode', eventCS2Cde + current_Body?.GeneralInformation?.TransportMode.toUpperCase(), conn)
			ctUtil.missingEventStatusCode(eventCS2Cde, tmp_eventExtCde, true, null, errorKeyList)
		} else {
			ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		}
		
		// N9
		ctUtil.missingBookingNumber(eventCS2Cde, current_Body?.BookingGeneralInfo, true, null, errorKeyList)
		ctUtil.missingContainerNumber(eventCS2Cde, current_Body?.Container, true, null, errorKeyList)

		//CSUpload	R4
		//R4 5
		missingEventLocationCodeFor(eventCS2Cde, current_Body, true, null, errorKeyList)
		missingEventLocationName(eventCS2Cde, current_Body, true, null, errorKeyList)
		
		//Missing Location Name
		missingPORLocationName(eventCS2Cde, current_Body?.Route?.POR, true, null, errorKeyList)
		missingPODLocationName(eventCS2Cde, current_Body?.Route?.LastPOD, true, null, errorKeyList)
		missingPOLLocationName(eventCS2Cde, current_Body?.Route?.FirstPOL, true, null, errorKeyList)
		missingFNDLocationName(eventCS2Cde, current_Body?.Route?.FND, true, null, errorKeyList)

		//Missing Location Qualifier
		missingPORLocationQualValue(eventCS2Cde, current_Body?.Route?.POR, true, null, errorKeyList)
		missingPODLocationQualValue(eventCS2Cde, current_Body?.Route?.LastPOD, true, null, errorKeyList)
		missingPOLLocationQualValue(eventCS2Cde, current_Body?.Route?.FirstPOL, true, null, errorKeyList)
		missingFNDLocationQualValue(eventCS2Cde, current_Body?.Route?.FND, true, null, errorKeyList)

		//Missing Country Code
		ctUtil.missingPORCountryCode(eventCS2Cde, current_Body?.Route?.POR, current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode, true, null, errorKeyList)
		ctUtil.missingPODCountryCode(eventCS2Cde, current_Body?.Route?.LastPOD, true, null, errorKeyList)
		ctUtil.missingPOLCountryCode(eventCS2Cde, current_Body?.Route?.FirstPOL, true, null, errorKeyList)
		missingFNDCountryCode(eventCS2Cde, current_Body?.Route?.FND, true, null, errorKeyList)

		//CS260
		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route.Inbound_intermodal_indicator, false, null, errorKeyList)

		return errorKeyList;
	}

	
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



	//	CSUpload	O	R4	5	1
	void missingEventLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Body current_Body, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if ( StringUtil.isEmpty(current_Body?.event?.Location?.LocationName?.trim()) && StringUtil.isEmpty(current_Body?.event?.Location?.CityDetails?.City?.trim())
		&& ( eventCS2Cde != 'CS180' ||  eventCS2Cde == 'CS180' && StringUtil.isEmpty(current_Body?.Route?.FND?.CityDetails?.City?.trim()) ) ) {
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing Event Location Name']
			errorKeyList.add(errorKey)
		}
	}

	void missingEventLocationCodeFor(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Body current_Body, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		def FND = current_Body?.Route?.FND
		if ( StringUtil.isEmpty(current_Body?.Event?.Location?.LocationCode?.UNLocationCode?.trim()) && StringUtil.isEmpty(current_Body?.Event?.Location?.LocationCode?.SchedKDCode?.trim()) && ( eventCS2Cde != 'CS180' || (eventCS2Cde == 'CS180' &&  
			StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.UNLocationCode?.trim()) && StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDCode?.trim())) ) ) {
			String tail = current_Body?.Event?.Location?.CityDetails?.City + '/' + current_Body?.Event?.Location?.CityDetails?.State + '/' + current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? errorMsg + tail : '- Missing Event Location Code for' + tail]
			errorKeyList.add(errorKey)
		}
	}

	//	CSUpload	O	R4	R	1
	void missingPORLocationName(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(POR?.CityDetails?.City?.trim())) {
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Name']
			errorKeyList.add(errorKey)
		}
	}

	//	CSUpload	O	R4	E	1
	void missingFNDLocationName(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(FND?.CityDetails?.City)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Name']
			errorKeyList.add(errorKey)
		}
	}

	//	CSUpload	O	R4	L	1
	void missingPOLLocationName(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.FirstPOL firstPOL, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(firstPOL?.Port?.PortName) && StringUtil.isEmpty(firstPOL?.Port?.City)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Name']
			errorKeyList.add(errorKey)
		}
	}

	//	CSUpload	O	R4	D	1
	void missingPODLocationName(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.LastPOD lastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(lastPOD?.Port?.PortName) && StringUtil.isEmpty(lastPOD?.Port?.City)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POD Location Name']
			errorKeyList.add(errorKey)
		}
	}

	void missingFNDCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CSStandardCity?.CSCountryCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Country Code']
			errorKeyList.add(errorKey)
		}
	}

	// missing Loc Qual Valu
	void missingPORLocationQualValue(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}
	void missingPODLocationQualValue(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POD Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}
	void missingPOLLocationQualValue(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}
	void missingFNDLocationQualValue(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDType)&&StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}



	public boolean pospValidation() {

	}


}













