package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

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

/**
 * @author Ingram
 * EUROFRETDSG CT initialize on 20170106 
 */
public class CUS_CS2CTXML_315_EUROFRETDSG {

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
	
	public String getExtReferenceCode(String TP_ID, String SRC_FMT_ID, String TRG_FMT_ID, String DIR_ID, String CONVERT_TYPE_ID, String SEG_ID,String SEG_NUM, String INT_CDE,Connection conn) throws Exception {
		if (conn == null)
		return "";
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select Ext_Cde from b2b_edi_cde_ref where TP_ID=? AND SRC_FMT_ID =? AND TRG_FMT_ID =? AND DIR_ID =? AND CONVERT_TYPE_ID =? AND SEG_ID =? AND SEG_NUM=? AND INT_CDE=?";
		
		try {
		pre = conn.prepareStatement(sql);
		pre.setMaxRows(util.getDBRowLimit());
		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
		pre.setString(1, TP_ID);
		pre.setString(2, SRC_FMT_ID);
		pre.setString(3, TRG_FMT_ID);
		pre.setString(4, DIR_ID);
		pre.setString(5, CONVERT_TYPE_ID);
		pre.setString(6, SEG_ID);
		pre.setString(7, SEG_NUM);
		pre.setString(8, INT_CDE);
	
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
		return ret
		}
	public String getShipDir(String TP_ID, String Seg_ID, String DIR_ID, String convertTypeId, String Int_Cde,String Seg_Num,  Connection conn) throws Exception {
		if (conn == null)
		return "";
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_edi_cde_ref where tp_id=? and dir_id =? and  CONVERT_TYPE_ID =? and seg_id=? and seg_num=? and int_cde =?";
		
		try {
		pre = conn.prepareStatement(sql);
		pre.setMaxRows(util.getDBRowLimit());
		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
		pre.setString(1, TP_ID);
		pre.setString(2, DIR_ID);
		pre.setString(3, convertTypeId);
		pre.setString(4, Seg_ID);
		pre.setString(5, Seg_Num);
		pre.setString(6, Int_Cde);
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
	public String getOceanCarrierID(String SCAC, Connection conn) throws Exception {
		if (conn == null)
		return "";
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select id from b2b_ocean_carrier where scac = ?";
		
		try {
		pre = conn.prepareStatement(sql);
		pre.setMaxRows(util.getDBRowLimit());
		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
		pre.setString(1, SCAC);
		
	
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
		return ret
		}
	public String getSCACCDE(String convert_type_id, String Ext_Cde,Connection conn) throws Exception {
		if (conn == null)
		return "";
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select scac_cde from b2b_cde_conversion where convert_type_id = ? and ext_cde = ?";
		
		try {
		pre = conn.prepareStatement(sql);
		pre.setMaxRows(util.getDBRowLimit());
		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
		pre.setString(1, convert_type_id);
		pre.setString(2, Ext_Cde);
		
	
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
		return ret
		}
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('EUROFRETDSG', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
//		def shipDir2 =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
		def shipDir=getShipDir('EUROFRETDSG','Q2','O','ShipDirection',vCS1EventFirst5,'EventStatusCode',conn)
		int OceanLegNum = current_Body.Route.OceanLeg.size();
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {

				if((vCS1Event=='CS060'||vCS1Event=='CS090')&&current_Body.GeneralInformation?.TransportMode?.trim().toUpperCase()=='FEEDER'){
					'E157_03' 'AP'
				}else if(vCS1Event=='CS050'&&current_Body.GeneralInformation?.TransportMode?.trim().toUpperCase()=='RAIL'){
					'E157_03' 'RL'
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
			def EQs = []
			current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber?.trim()}.each{BLNumber, BLNumberGroup ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' BLNumber
					
				}
			}
			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each {CarrierBookingNumber, CarrierBookingNumberGroup ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' CarrierBookingNumber
				}
			}
			if ((current_Body.Container?.ContainerNumber) && !EQs.contains(current_Body.Container?.ContainerNumber + current_Body.Container?.ContainerCheckDigit)) {
				EQs.add(current_Body.Container?.ContainerNumber)
				'N9' {
					'E128_01' 'EQ'
					'E127_02' current_Body.Container?.ContainerNumber
				}
			}

			def SCAC=current_Body.GeneralInformation.SCAC
			def SCACnumber=getOceanCarrierID(SCAC,conn)
			def SCACCED=getSCACCDE('CS1CarrierID',SCACnumber,conn)
			'N9'{
				'E128_01' 'SCA'
				'E127_02' SCACCED
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
//
//			//R4
			POR POR =  current_Body.Route?.POR
			FirstPOL firstPOL =  current_Body.Route?.FirstPOL
			LastPOD lastPOD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND
			Event Event = current_Body?.Event
//
//			//EventLoc
			'Loop_R4' {
				def unLocationCode = current_Body.Event?.Location?.LocationCode?.UNLocationCode
				def eventLocationName = current_Body.Event?.Location?.LocationName
				def eventCountryCode = current_Body.Event?.Location?.CSStandardCity?.CSCountryCode
				def eventStateCode=current_Body.Event?.Location?.CityDetails?.State
				//Map event Loc "R4"
				def R401 = '5'
				def R402 = (unLocationCode==null||unLocationCode.trim().equals(""))?current_Body.Event?.Location?.LocationCode?.SchedKDType:"UN"
				def R403 = (unLocationCode==null||unLocationCode.trim().equals(""))?current_Body.Event?.Location?.LocationCode?.SchedKDCode:unLocationCode
				def R404 = (eventLocationName==null||eventLocationName.trim().equals(""))?current_Body.Event?.Location?.CityDetails?.City:eventLocationName
				def R405=null
				if(eventCountryCode&&eventCountryCode!=""){ R405 = eventCountryCode.trim()}
				def R408 = getProvinceCode(unLocationCode,eventCountryCode,conn)
				//Map event Loc DT "DTM"
				'R4' {
					'E115_01' R401
					if(R402!=null && !R402?.trim()?.equals("")) 'E309_02' R402
					if(R403!=null && !R403?.trim()?.equals("")) 'E310_03' R403
					if(R404!=null && !R404?.trim()?.equals("")) 'E114_04' util.substring(R404,1,24).trim()
					if(R405!=null && !R405?.trim()?.equals("")) 'E26_05'  R405
					if(R408!=null && !R408.equals(""))   'E156_08'  R408
				}
				'DTM' {
					LocDT eventDT = current_Body.Event.EventDT?.LocDT
					'E374_01' '140'
					if(eventDT!=null&&!eventDT?.equals("")){
						'E373_02' util.convertDateTime(eventDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(eventDT, xmlDateTimeFormat, HHmm)
					}
				}
			}
//			//
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
				if (current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT) {
					porDTM = current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				} else if (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C') {
					porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT
				} else if (current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M') {
					porDTM = current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT
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
					if (lastPOD.Port?.CSCountryCode?.length() <= 2){
						'E26_05' lastPOD.Port?.CSCountryCode
					}
					if (lastPOD.CSStateCode) {
						'E156_08' util.substring(lastPOD.CSStateCode,1,2)
					}
				}

				LocDT locActDTM = null
				LocDT locEstDTM = null
				def isAct = false
				def isEst = false


				if(lastOceanLeg.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					locActDTM = lastOceanLeg.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}
				if(lastOceanLeg.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT){
					locEstDTM = lastOceanLeg.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
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
					if (firstPOL.Port?.CSCountryCode?.length() <= 2){
						'E26_05' firstPOL.Port?.CSCountryCode
					}
					if (firstPOL.CSStateCode) {
						'E156_08' firstPOL.CSStateCode
					}
				}

				LocDT locActDTM = null
				LocDT locEstDTM = null
				def isAct = false
				def isEst = false

				if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					locActDTM = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}
				if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT){
					locEstDTM = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
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
					if (FND.CSStandardCity?.CSCountryCode?.length() <= 2){
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
					if(current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT){
						AstC = current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT
						isAstC = true
					}
					LocDT EstA = null
					def firstBKG_M = current_Body?.BookingGeneralInfo[0]

					if(util.substring(firstBKG_M.Haulage?.InBound?.trim(),1,1) == 'M'){
						if(current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator =='E'}?.LocDT){
							EstA = current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator =='E'}?.LocDT
							isEstA = true
						}else if(current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator =='E'}?.LocDT && current_Body.GeneralInformation?.SCAC?.trim() !='OOLU'){
							EstA = current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator =='E'}?.LocDT
							isEstA = true
						}

					}

					LocDT EstC = null
					if(util.substring(firstBKG_M.Haulage?.InBound?.trim(),1,1) == 'C'){
						if(current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT){
							EstC = current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT
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
		generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)
		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = getDuplicatedPairs('EUROFRETDSG', conn)
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex -> 
			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('EUROFRETDSG', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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
		generateEDITail(outXml, outputBodyCount)

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
//				
				ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
				EventNotSub(eventCS2Cde, eventExtCde, false, null, errorKeyList)
//				CS160NotInTruck(eventCS2Cde, eventExtCde,current_Body.GeneralInformation?.TransportMode, true, null, errorKeyList)
				ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
				
//		
//				//N9 EQ BN
				missingContainerNumber(eventCS2Cde,eventExtCde,current_Body,true, null, errorKeyList)
				missingBookingNumber(eventCS2Cde, current_Body.BookingGeneralInfo,true, null, errorKeyList)
//				ctUtil.missingContainerNumber2(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
//				//R4 5
				missingEventCountryCode(eventCS2Cde, current_Body.Event, current_Body.Route?.FND, true, null, errorKeyList)
				missingEventLocationName(eventCS2Cde, current_Body.Event,current_Body.Route?.FND, true, null, errorKeyList)
//				missingEventLocationCode(eventCS2Cde, current_Body.Event?.Location?.LocationCode ,current_Body.Event, false, null, errorKeyList)
//				//B4 D
				missingPODCountryCode(eventCS2Cde, current_Body.Route?.lastPOD, true, null, errorKeyList)
				missingPODLocationCode(eventCS2Cde, current_Body.Route?.LastPOD?.Port?.LocationCode, true, null, errorKeyList)
				missingPODLocationName(eventCS2Cde, current_Body.Route?.LastPOD, true, null, errorKeyList)
//		
//				//B4 E
				missingFNDCountryCode(eventCS2Cde, current_Body.Route?.FND, true, null, errorKeyList)
				missingFNDLocationCode(eventCS2Cde, current_Body.Route?.FND?.CityDetails?.LocationCode, true, null, errorKeyList)
				missingFNDLocationName(eventCS2Cde, current_Body.Route?.FND, true, null, errorKeyList)
//		
//				//B4 L
				missingPOLCountryCode(eventCS2Cde, current_Body.Route?.FirstPOL, true, null, errorKeyList)
				missingPOLLocationCode(eventCS2Cde, current_Body.Route?.FirstPOL?.Port?.LocationCode, true, null, errorKeyList)
				missingPOLLocationName(eventCS2Cde, current_Body.Route?.FirstPOL, true, null, errorKeyList)
//		
//				//B4 R
				def flag
				if (current_Body.Route.POR.CSStandardCity?.CSCountryCode?.length() <= 2){
					flag=current_Body.Route.POR.CSStandardCity.CSCountryCode
				}
				missingPORCountryCode(eventCS2Cde, current_Body.Route?.POR, flag, true, null, errorKeyList)
				missingPORLocationCode(eventCS2Cde, current_Body.Route?.POR?.CityDetails?.LocationCode, true, null, errorKeyList)
				missingPORLocationName(eventCS2Cde, current_Body.Route?.POR,true, null, errorKeyList)
		
				// obsolete cases
				//B4 03_157
//				ctUtil.missingEventStatusCode3(eventCS2Cde, eventExtCde, false, null, errorKeyList)
				ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route.Inbound_intermodal_indicator, false, null, errorKeyList)


		return errorKeyList;
	}
    
	private static final String YES = 'YES'
	private static final String NO = 'NO'
	private static final String C = 'C'
	private static final String ERROR_SUPPORT = 'ES'
	private static final String ERROR_COMPLETE = 'EC'
	
	public void missingContainerNumber(String eventCS2Cde, String eventExtCde, Body current_Body,boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		def ErrorCode=null
		if((eventCS2Cde=='CS060'||eventCS2Cde=='CS090')&&current_Body.GeneralInformation?.TransportMode?.trim().toUpperCase()=='FEEDER'){
			ErrorCode='AP'
		}else if(eventCS2Cde=='CS050'&&current_Body.GeneralInformation?.TransportMode?.trim().toUpperCase()=='RAIL'){
			ErrorCode='RL'
		}else{
			ErrorCode=eventExtCde
		}
		if (!current_Body.Container?.ContainerNumber) {
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg: ErrorCode + ' - MISSING_CTN_NO']
			errorKeyList.add(errorKey)
		}
	}
	public void missingBookingNumber(String eventCS2Cde, List<cs.b2b.core.mapping.bean.ct.BookingGeneralInfo> BookingGeneralInfo, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it?.CarrierBookingNumber)}.size == 0){
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Booking Number']
			errorKeyList.add(errorKey);
		}
	}
	public void EventNotSub(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(eventExtCde)&&eventExtCde=="XX") {
			errorKey = [TYPE: isError? ERROR_SUPPORT : C, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg: eventCS2Cde + ' - EVT_NOT_SUB']
			errorKeyList.add(errorKey)
		}
	}
	

    public void missingEventCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Event.Location?.CSStandardCity?.CSContinentCode)&&(eventCS2Cde!='CS180' ||eventCS2Cde=='CS180' && StringUtil.isEmpty(FND?.CSStandardCity?.CSCountryCode))){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Country Code']
			errorKeyList.add(errorKey)
		}
	}
	public void missingEventLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Event.Location?.LocationName)&&StringUtil.isEmpty(Event.Location?.CityDetails.City)&&(eventCS2Cde!='CS180' ||eventCS2Cde=='CS180' && StringUtil.isEmpty(FND?.CityDetails?.City))){
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Location Name']
			errorKeyList.add(errorKey)
		}
	}
//	public 	void missingEventLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
//		Map<String,String> errorKey = null
//		if (StringUtil.isEmpty(LocationCode?.UNLocationCode?.toString()) && StringUtil.isEmpty(LocationCode?.SchedKDCode?.toString())) {
//			if(!(Event.CS1Event.toString()).equals("CS180") || Event.CS1Event.toString().equals("CS180") && StringUtil.isEmpty(LocationCode.UNLocationCode.toString()) && StringUtil.isEmpty(LocationCode.SchedKDCode)){
//				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Location Code for']
//				errorKeyList.add(errorKey)
//			}
//		}
//	}
	void missingPODCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LastPOD?.Port?.CSCountryCode)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPODLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)) {
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingPODLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LastPOD?.Port?.PortName)){
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + ' -Missing POD Location Name']
			errorKeyList.add(errorKey)
		}
	}
	void missingFNDCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CSStandardCity?.CSContinentCode)){
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingFNDLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode?.UNLocationCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingFNDLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Name']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FirstPOL?.Port?.CSCountryCode)){
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FirstPOL?.Port?.PortName)){
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Name']
			errorKeyList.add(errorKey)
		}
	}


	void missingPORCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR,String flag, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(POR?.CSStandardCity?.CSCountryCode)&&StringUtil.isEmpty(flag)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPORLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingPORLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(POR?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_COMPLETE : ERROR_SUPPORT, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Name']
			errorKeyList.add(errorKey)
		}
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
			'I07_08' 'GTNEXUS        '
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
			'E124_03' 'GTNEXUS'
			'E373_04' currentSystemDt.format(yyyyMMdd)
			'E337_05' currentSystemDt.format(HHmm)
			'E28_06' '###edi_Group_Ctrl_Number###'
			'E455_07' 'X'
			'E480_08' '004010'
		}

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
			if(rs!=null) rs.close()
			if(ps!=null)   ps.close()
		}

		return result==null?"":result
	}
	public Map<String,String> getDuplicatedPairs(String tp_id, Connection conn){
		Map<String,String> result = [:]
		if (conn == null)
			return result;

		String key = null;
		String value = null;
		PreparedStatement pre = null;
		ResultSet resultSet = null;
		String sql = "select substr(int_cde,0,5),EXT_CDE from b2b_cde_conversion where tp_id = ? and convert_type_id = 'EventStatusCode' and msg_type_id = 'CT' and substr(int_cde,7) = ext_cde order by int_cde";
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10000);
			pre.setQueryTimeout(10);
			pre.setString(1, tp_id);
			resultSet = pre.executeQuery();

			while(resultSet.next()) {
				key = resultSet.getString(1);
				value = resultSet.getString(2);
				result.put(key,value)
			}
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (pre != null)
				pre.close();
		}
		return result;
	}





}










