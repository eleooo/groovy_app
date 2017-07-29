package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List;
import java.util.Map;

import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser


/**
 * @author DENGJA
 * @pattern after TP=FMIC
 */
public class CUS_CS2CTXML_315_OECL002280 {
	
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
	def yyyyMMddHHmm="yyyyMMddHHmm"

	def currentSystemDt = null
	
	
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
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('OECL002280', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
//		def shipDir2 =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
		def shipDir = util.getEDICdeReffromIntCde('OECL002280', 'ShipDirection', 'O', 'INTCTXML', 'Q2', 'EventStatusCode', vCS1EventFirst5, conn)

		def B406 = null
		def B411 = null
		def B412 = null
		
		def vCity = util.substring(current_Body.Event?.Location?.CityDetails?.City,1,60).trim()
		def vState = (util.substring(current_Body.Event?.Location?.CityDetails?.State,1,2)).trim().toUpperCase()
		def vCSCountryCode = util.substring(current_Body.Event?.Location?.CSStandardCity?.CSCountryCode,1,2)
		
		Map vUNLocCode_map =ctUtil.getUNLocCodeFromLocationInfo(vCity, vState, vCSCountryCode, conn)
		Map vUNLocCode2_map =ctUtil.getUNLocCodeFromCityInfo(vCity, vCSCountryCode, conn)
		
		
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {

				def defaultB406 = current_Body.Event?.Location?.LocationCode?.UNLocationCode
				def defaultB411 = ''
				def defaultB412 = 'UN'
				def EventCityName=current_Body.Event?.Location?.CityDetails?.City
				def EventStateCode=(current_Body.Event?.Location?.CityDetails?.State)
				if(EventStateCode!="")
				{
					EventStateCode=util.substring(EventStateCode,1,2).toUpperCase();
				}
				   
				def EventCountryCode=current_Body.Event?.Location?.CSStandardCity?.CSCountryCode
				String[] LocationList= new String[5]
				String[] LocationList2= new String[5]
				if(EventStateCode){
					getLocationList(EventCityName,EventStateCode,EventCountryCode,conn,LocationList)
					if((LocationList[0]==""||LocationList[0]==null))
					{
						getLocationListWithoutStateCode(EventCityName,EventCountryCode,conn,LocationList)
					}
				}
				else {
					getLocationListWithoutStateCode(EventCityName,EventCountryCode,conn,LocationList)
				}


				'E157_03' vCS1EventCodeConversion

				'E373_04' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, yyyyMMdd)
//
				'E161_05' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, HHmm)
				
				if(current_Body.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
				}
				
				if(current_Body.Container?.ContainerNumber){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5,current_Body?.Container?.ContainerNumber?.length())
				}
				
				if (current_Body.Container?.ContainerStatus?.trim() == 'Empty') {
					'E578_09' 'E'
				} else if (current_Body.Container?.ContainerStatus?.trim() == 'Laden') {
					'E578_09' 'L'
				} else if (((current_Body.Event?.CS1Event?.trim() == 'CS210')||(current_Body.Event?.CS1Event?.trim() == 'CS010'))) {
				    'E578_09' 'E'
				} else if(((current_Body.Event?.CS1Event?.trim() != 'CS210')&&(current_Body.Event?.CS1Event?.trim() != 'CS010'))){
				    'E578_09' 'L'
				}
				
				if(current_Body.Container?.CarrCntrSizeType){
					'E24_10' util.substring(current_Body.Container?.CarrCntrSizeType, 1, 4)
				}
				
				
				
				if(vUNLocCode_map['UNLocCode'] || vUNLocCode_map['SchedKDCde']){
					if(vUNLocCode_map['UNLocCode']){
						'E310_11' vUNLocCode_map['UNLocCode']
					}else if(!vUNLocCode_map['UNLocCode'] && vUNLocCode_map['SchedKDCde']){
						'E310_11' vUNLocCode_map['SchedKDCde']
					}else if(vCity){
						'E310_11' vCity
					}else if(util.substring(current_Body.Event?.Location?.LocationName,1,35)){
						'E310_11' util.substring(current_Body.Event?.Location?.LocationName,1,35)
					}
				}else if(vUNLocCode2_map['UNLocCode'] || vUNLocCode2_map['SchedKDCde']){
					if(vUNLocCode2_map['UNLocCode']){
						'E310_11' vUNLocCode2_map['UNLocCode']
					}else if(!vUNLocCode2_map['UNLocCode'] && vUNLocCode2_map['SchedKDCde']){
						'E310_11' vUNLocCode2_map['SchedKDCde']
					}else if(vCity){
						'E310_11' vCity
					}else if(util.substring(current_Body.Event?.Location?.LocationName,1,35)){
						'E310_11' util.substring(current_Body.Event?.Location?.LocationName,1,35)
					}
				}else if(!vUNLocCode_map['UNLocCode'] && !vUNLocCode_map['SchedKDCde'] && !vUNLocCode2_map['UNLocCode'] && !vUNLocCode2_map['SchedKDCde']){
					if(vCity){
						'E310_11' vCity
					}else if(util.substring(current_Body.Event?.Location?.LocationName,1,35)){
						'E310_11' util.substring(current_Body.Event?.Location?.LocationName,1,35)
					}
				}

				if(vUNLocCode_map['UNLocCode'] || vUNLocCode_map['SchedKDCde']){
					if(vUNLocCode_map['UNLocCode']){
						'E309_12' 'UN'
					}else if(vUNLocCode_map['SchedKDCde']=='K' || vUNLocCode_map['SchedKDCde']=='D'){
						'E309_12' vUNLocCode_map['SchedKDCde']
					}else if(vCity){
						'E309_12' 'CI'
					}else if(util.substring(current_Body.Event?.Location?.LocationName,1,35)){
						'E309_12' 'CI'
					}
				}else if(vUNLocCode2_map['UNLocCode'] || vUNLocCode2_map['SchedKDCde']){
					if(vUNLocCode2_map['UNLocCode']){
						'E309_12' 'UN'
					}else if(vUNLocCode_map['SchedKDCde']=='K' || vUNLocCode_map['SchedKDCde']!='D'){
						'E309_12' vUNLocCode2_map['SchedKDCde']
					}else if(vCity){
						'E309_12' 'CI'
					}else if(util.substring(current_Body.Event?.Location?.LocationName,1,35)){
						'E309_12' 'CI'
					}
				}else if(!vUNLocCode_map['UNLocCode'] && !vUNLocCode_map['SchedKDCde'] && !vUNLocCode2_map['UNLocCode'] && !vUNLocCode2_map['SchedKDCde']){
					if(vCity){
						'E309_12' 'CI'
					}else if(util.substring(current_Body.Event?.Location?.LocationName,1,35)){
						'E309_12' 'CI'
					}
				}
			
				'E761_13'  util.substring(current_Body.Container?.ContainerCheckDigit, 1,2)
				
			}
			//Loop N9
			//for avoid duplicated N9
			current_Body?.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each { bookingNumber, current_BookingGeneralInfoGroup ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(bookingNumber,1,30)
				}
			}
			current_Body?.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber.trim()}.each { blNumber, current_BookingGeneralInfoGroup ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(blNumber,1,30)
				}
			}
			
			
			/*
			current_Body.ExternalReference?.findAll{StringUtil.isNotEmpty(it.CSReferenceType)}.groupBy{it.CSReferenceType?.trim()}.each { CSReferenceType, current_CSReferenceTypeGroup ->
				def ExtReferenceCode=getExtReferenceCode('OECL002280','INTCTXML','315','O','ExtReferenceCode','N9','01-128',CSReferenceType,conn)
				if(ExtReferenceCode!="")
					'N9' {
						'E128_01' ExtReferenceCode
						'E127_02' util.substring(current_CSReferenceTypeGroup.get(0).ReferenceNumber,1,30)
						}
			}
			*/
			
			current_Body.ExternalReference.each {current_Ex ->
				
				
				if(current_Ex?.CSReferenceType=='INV' && current_Ex?.ReferenceNumber)
				{
					
					'N9' {
						
						'E128_01' 'IN'
						'E127_02' util.substring(current_Ex?.ReferenceNumber,1,30)
						
						}
					
				}
				else if(current_Ex?.CSReferenceType=='PO' && current_Ex?.ReferenceNumber)
				{
					'N9' {
						'E128_01' 'PO'
						'E127_02' util.substring(current_Ex?.ReferenceNumber,1,30)
						}
					
				}
				else if(current_Ex?.CSReferenceType=='SC' && current_Ex?.ReferenceNumber)
				{
					
					'N9' {
						'E128_01' 'E8'
						'E127_02' util.substring(current_Ex?.ReferenceNumber,1,30)
						}
					
				}
				else if(current_Ex?.CSReferenceType=='SID' && current_Ex?.ReferenceNumber)
				{
					'N9' {
						'E128_01' 'SI'
						'E127_02' util.substring(current_Ex?.ReferenceNumber,1,30)
						}
					
				}
				else if(current_Ex?.CSReferenceType=='SO' && current_Ex?.ReferenceNumber)
				{
					'N9' {
						'E128_01' 'SO'
						'E127_02' util.substring(current_Ex?.ReferenceNumber,1,30)
						}
					
				}
				else if(current_Ex?.CSReferenceType=='SR' && current_Ex?.ReferenceNumber)
				{
					'N9' {
						'E128_01' 'SR'
						'E127_02' util.substring(current_Ex?.ReferenceNumber,1,30)
						}
					
				}
				
			}
			
			
			
			//Fixed b2b bug, for error N9*4F*
			def cgnParty_4F_BL=current_Body.Party?.findAll{it?.PartyType=='CGN' && (it?.PartyLevel=='BL')}
			
			if(cgnParty_4F_BL && cgnParty_4F_BL[0]?.CarrierCustomerCode){
					'N9' {
						'E128_01' '4F'
						'E127_02' cgnParty_4F_BL[0]?.CarrierCustomerCode
						//println(cgnParty_4F_BL[0]?.CarrierCustomerCode)
					}
			}
			else
			{
				def cgnParty_4F_EmptyFillter=current_Body.Party?.findAll{it?.PartyType=='CGN' && ((!(it?.PartyLevel)) || it?.PartyLevel=='')}
				if(cgnParty_4F_EmptyFillter && cgnParty_4F_EmptyFillter[0]?.CarrierCustomerCode){
						'N9' {
							'E128_01' '4F'
							'E127_02' cgnParty_4F_EmptyFillter[0]?.CarrierCustomerCode
							//println(cgnParty_4F_EmptyFillter[0]?.CarrierCustomerCode)
						}
				}
			}
			
			
			
			
			
//			def SCAC=current_Body.GeneralInformation.SCAC
//			def SCACnumber=getOceanCarrierID(SCAC,conn)
//			def SCACCED=getSCACCDE('CS1CarrierID',SCACnumber,conn)
//			//println(SCACCED)
//			'N9'{
//				'E128_01' 'SCA'
//				'E127_02' SCACCED
//			}
			if(firstScac){
				'N9'{
					'E128_01' 'SCA'
					'E127_02' firstScac
				}
			}

//			OceanLeg firstOceanLeg = null
//			OceanLeg lastOceanLeg = null
//			if(current_Body.Route?.OceanLeg){
//				firstOceanLeg = current_Body.Route.OceanLeg[0]
//				lastOceanLeg = current_Body.Route.OceanLeg[-1]
//			}
			
			
			def oceanLeg_map = []
			current_Body.Route?.OceanLeg?.each{current_OceanLeg ->
				
				def OBVoyageNumber =''
				if(current_OceanLeg?.SVVD?.Loading?.Voyage || current_OceanLeg?.SVVD?.Loading?.Direction){
					OBVoyageNumber = current_OceanLeg?.SVVD?.Loading?.Voyage + current_OceanLeg?.SVVD?.Loading?.Direction
				}
				 
				if(current_OceanLeg?.SVVD?.Loading?.Vessel !='' && OBVoyageNumber != '' && current_OceanLeg?.SVVD?.Loading?.VesselName != ''){
					oceanLeg_map.add(current_OceanLeg)
				}
			}
			
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			
//			def lastOceanLeg_map = []
//			//def firstOceanLeg_map = []
//			
//			lastOceanLeg_map = current_Body.Route?.OceanLeg?.findAll{it?.SVVD}
//			firstOceanLeg = current_Body.Route?.OceanLeg[0]
			
			if(oceanLeg_map.size() > 0){
				lastOceanLeg = oceanLeg_map[-1]
				firstOceanLeg = oceanLeg_map[0]
			}else{
				lastOceanLeg = current_Body.Route?.OceanLeg[-1]
				firstOceanLeg = current_Body.Route?.OceanLeg[0]
			}
			
			
			def CSSTDQ2ExtCDE=ctUtil.getCodeConversionbyCovertType('CT','O','CSSTDQ2',vCS1EventFirst5,conn)
			if(CSSTDQ2ExtCDE){
				if (shipDir == 'I') {
					'Q2' {
						if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
							'E597_01' util?.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)
						}
						if (lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode) {
							'E26_02' lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode
						}
						LocDT polDepartDtA = lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
						LocDT polDepartDtE = lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT
						
						if (lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction) {
							'E55_09' ((lastOceanLeg?.SVVD?.Discharge?.Voyage ? lastOceanLeg?.SVVD?.Discharge?.Voyage:"") + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction:""))
						}
						
						OceanLeg lastL_map
						if(oceanLeg_map.size() > 0){
							lastL_map = oceanLeg_map[-1]
						}
						
						
						if(lastL_map?.SVVD?.Discharge?.LloydsNumber)
						{
							'E897_12' 'L'
						}
						if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
							'E182_13' lastOceanLeg?.SVVD?.Discharge?.VesselName
						}
					}
				} else if (shipDir == 'O') {
					'Q2' {
						if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
							'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)
						}
						if (firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode) {
							'E26_02' firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode
						}
						LocDT polDepartureDtA = lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
						LocDT polDepartureDtE = lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT
	
						
						if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
							'E55_09' ((firstOceanLeg?.SVVD?.Loading?.Voyage ? firstOceanLeg?.SVVD?.Loading?.Voyage:"") + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction:""))
						}
//						if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber)
//						{
//							'E897_12' 'L'
//						}
						OceanLeg firstL_map
						if(oceanLeg_map.size() > 0){
							firstL_map = oceanLeg_map[0]
						}
						
						
						if(firstL_map?.SVVD?.Discharge?.LloydsNumber)
						{
							'E897_12' 'L'
						}
//						def firstL_map = []
//						firstL_map = current_Body.Route?.OceanLeg?.findAll{it?.SVVD?.Discharge?.LloydsNumber}
//						if(firstL_map.size() > 0)
//						{
//							'E897_12' 'L'
//						}
						if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
							'E182_13' firstOceanLeg?.SVVD?.Loading?.VesselName
						}
					}
				}
			}
			
			
			//R4
			POR POR =  current_Body.Route?.POR
			FirstPOL firstPOL =  current_Body.Route?.FirstPOL
			LastPOD lastPOD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND		
			
			
			if(POR){
				'Loop_R4' {
					'R4' {
						'E115_01' 'R'
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType
						}
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.SchedKDCode
						}
						if (POR?.CityDetails?.City) {
							'E114_04' util?.substring(POR?.CityDetails?.City, 1, 24).trim()
						}
						if (POR?.CSStandardCity?.CSCountryCode?.length() <= 2){
							'E26_05' POR?.CSStandardCity?.CSCountryCode
						}
						if (POR?.CSStandardCity?.CSStateCode) {
							'E156_08' POR?.CSStandardCity?.CSStateCode
						}
					}
	
					LocDT porDTM = null
					def isAct = false
					if (current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString()) {
						porDTM = current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT
						isAct = true
					} else if  (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT?.toString() && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C') {
						porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT
					} else if (current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString() && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M') {
						porDTM = current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT
					}
					if (porDTM?.toString()) {
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
			}
			
			if(firstPOL){
				'Loop_R4' {
					'R4' {
						'E115_01' 'L'
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E309_02' firstPOL?.Port?.LocationCode?.SchedKDType
						}
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.UNLocationCode
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.SchedKDCode
						}
						if (firstPOL?.Port?.PortName) {
							'E114_04'  util.substring(firstPOL?.Port?.PortName, 1, 24)
						} else if (firstPOL?.Port?.City) {
							'E114_04'  util.substring(firstPOL?.Port?.City, 1, 24)
						}
						
						if (firstPOL?.Port?.CSCountryCode?.length() <= 2) {
							'E26_05' firstPOL?.Port?.CSCountryCode
						}
						if (firstPOL?.CSStateCode) {
							'E156_08' firstPOL?.CSStateCode
						}
					}
					LocDT polDepartureDtA = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT polDepartureDtE = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
					if (polDepartureDtA?.toString()) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, HHmm)
							
						}
					} else if (polDepartureDtE?.toString()) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, HHmm)
							
						}
					}
				}
			}
			def R402D = null
			def R403D = null
			def R404D = null
			def R405D = null
			def R406D = null
			def R408D = null

			if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
				R402D = 'UN'
			} else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
				R402D = lastPOD?.Port?.LocationCode?.SchedKDType
			}
			if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
				R403D = lastPOD?.Port?.LocationCode?.UNLocationCode
			} else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
				R403D = lastPOD?.Port?.LocationCode?.SchedKDCode
			}
			if (lastPOD?.Port?.PortName) {
				R404D = util?.substring(lastPOD?.Port?.PortName, 1, 24)
			} else if (lastPOD?.Port?.City) {
				R404D = util?.substring(lastPOD?.Port?.City, 1, 24)
			}
			if (lastPOD?.Port?.CSCountryCode?.length() <= 2) {
				R405D = lastPOD?.Port?.CSCountryCode
			}

			if (lastPOD?.CSStateCode) {
				R408D = lastPOD?.CSStateCode
			}
			
			
//			LocDT podArrivalDtA
//			LocDT podArrivalDtE
//			current_Body?.Route?.OceanLeg?.findAll {
//				!(StringUtil.isEmpty(it.SVVD?.Loading?.Vessel)
//				&& (StringUtil.isEmpty(it.SVVD?.Loading?.Voyage) && StringUtil.isEmpty(it.SVVD?.Loading?.Direction))
//				&& StringUtil.isEmpty(it.SVVD?.Loading?.VesselName))}.each{current_OceanLeg->
//				if(current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT ||
//					current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT) {
//					podArrivalDtA = current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
//					podArrivalDtE = current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
//					
//				}
//			}
				
			
			def oceanLeg = null
			List<OceanLeg> OceanLegs = current_Body?.Route?.OceanLeg?.findAll {
				  !(StringUtil.isEmpty(it.SVVD?.Loading?.Vessel)
								&& (StringUtil.isEmpty(it.SVVD?.Loading?.Voyage) && StringUtil.isEmpty(it.SVVD?.Loading?.Direction))
								&& StringUtil.isEmpty(it.SVVD?.Loading?.VesselName))}
			if(OceanLegs.size() != 0){
				  oceanLeg = OceanLegs[-1]
			}

						
			LocDT podArrivalDtA = oceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
			LocDT podArrivalDtE = oceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
				
				'Loop_R4' {
					'R4' {
						'E115_01' 'D'
						if (R402D) {
							'E309_02' R402D
						}
						if (R403D) {
							'E310_03' R403D
						}
						if (R404D) {
							'E114_04' R404D
						}
						if (R405D) {
							'E26_05' R405D
						}
						if (R408D) {
							'E156_08' R408D
						}
					}
						
					if (podArrivalDtA?.toString()) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, HHmm)
						}
					} else if (podArrivalDtE?.toString()) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, HHmm)
						}
					}
				}

			
//				'Loop_R4' {
//					'R4' {
//						'E115_01' 'D'
//						if (R402D) {
//							'E309_02' R402D
//						}
//						if (R403D) {
//							'E310_03' R403D
//						}
//						if (R404D) {
//							'E114_04' R404D
//						}
//						if (R405D) {
//							'E26_05' R405D
//						}
//						if (R408D) {
//							'E156_08' R408D
//						}
//					}
//					def OBVoyageNumber=null
//					if (lastOceanLeg?.SVVD?.Loading?.Voyage || lastOceanLeg?.SVVD?.Loading?.Direction) {
//						OBVoyageNumber= ((lastOceanLeg?.SVVD?.Loading?.Voyage ? lastOceanLeg?.SVVD?.Loading?.Voyage:"") + (lastOceanLeg?.SVVD?.Loading?.Direction ? lastOceanLeg?.SVVD?.Loading?.Direction:""))
//					}
//					if((lastOceanLeg?.SVVD?.Loading?.Vessel)||(lastOceanLeg?.SVVD?.Loading?.VesselName)||(lastOceanLeg?.SVVD?.Loading?.Vessel)||OBVoyageNumber)
//					{
//						podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT
//						podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT
//						if (podArrivalDtA?.toString()) {
//							'DTM' {
//								'E374_01' '140'
//								'E373_02' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
//								'E337_03' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, HHmm)
//							}
//						} else if (podArrivalDtE?.toString()) {
//							'DTM' {
//								'E374_01' '139'
//								'E373_02' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
//								'E337_03' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, HHmm)
//							}
//						}
//					}
//					
//					
//					
//					
//					
//				}
			
//
			def R402E = null
			def R403E = null
			def R404E = null
			def R405E = null
			def R408E = null
			
			if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
				R402E = 'UN'
			} else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
				R402E = FND?.CityDetails?.LocationCode?.SchedKDType
			}
			if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
				R403E = FND?.CityDetails?.LocationCode?.UNLocationCode
			} else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
				R403E = FND?.CityDetails?.LocationCode?.SchedKDCode
			}
			if (FND?.CityDetails?.City) {
				R404E = util?.substring(FND?.CityDetails?.City, 1, 24)
			}
			if (FND?.CSStandardCity?.CSContinentCode?.length() <= 2) {
				R405E = FND?.CSStandardCity?.CSCountryCode
			}
			if (FND?.CSStandardCity?.CSStateCode) {
				R408E = FND?.CSStandardCity?.CSStateCode
			}

			     if(FND) {
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						if (R402E) {
							'E309_02' R402E
						}
						if (R403E) {
							'E310_03' R403E
						}
						if (R404E) {
							'E114_04' R404E
						}
						if (R405E) {
							'E26_05' R405E
						}
						if (R408E) {
							'E156_08' R408E
						}
					}
					LocDT fndDatetime = null
					def isAct = false
					def ActArrivalAtFinalHubDateTime=null
					def ActCargoDeliveryDateTime=null
					def ActCargoDeliveryDateTimeAsLong=null
					def ActArrivalAtFinalHubDateTimeAsLong=null
					def EventDateTime=util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					
					if(current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT)
					{
						ActArrivalAtFinalHubDateTime=current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT
						
					}
					else if(current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT)
					{
						ActArrivalAtFinalHubDateTime=current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					}
					if (current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString())
                    {
						ActCargoDeliveryDateTime = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'A'}?.LocDT
					}
					
					if(ActCargoDeliveryDateTime){
						ActCargoDeliveryDateTimeAsLong=util.convertDateTime(ActCargoDeliveryDateTime, xmlDateTimeFormat, yyyyMMdd)
					}
					if(ActArrivalAtFinalHubDateTime)
					{
						ActArrivalAtFinalHubDateTimeAsLong=util.convertDateTime(ActArrivalAtFinalHubDateTime, xmlDateTimeFormat, yyyyMMdd)
					}
					
					//tibco logic below
					if (ActCargoDeliveryDateTime&&(ActCargoDeliveryDateTimeAsLong as long)>=(EventDateTime as long)) 
					{
							fndDatetime=ActCargoDeliveryDateTime
							isAct = true
					}else if(ActArrivalAtFinalHubDateTime&&(ActArrivalAtFinalHubDateTimeAsLong as long)>=(EventDateTime as long))
					{   
						    fndDatetime=ActArrivalAtFinalHubDateTime
							isAct = true
					}else if (current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT?.toString()) {
						fndDatetime = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'E'}?.LocDT
						isAct = false
					} else if (current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT?.toString()) {
						fndDatetime = current_Body.Route.ArrivalAtFinalHub.find{it.attr_Indicator == 'E'}?.LocDT
						isAct = false
					} else if (current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT?.toString()) {
						fndDatetime = current_Body.Route.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
						isAct = false
					}
					else if (current_Body?.Route?.OceanLeg[-1]?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT?.toString()) {
						fndDatetime = current_Body?.Route?.OceanLeg[-1]?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
						isAct = false
					}else if (current_Body?.Route?.OceanLeg[-1]?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString()) {
						fndDatetime = current_Body?.Route?.OceanLeg[-1]?.POD.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
						isAct = true
					}
					if (fndDatetime?.toString()) {
						'DTM' {
							if (isAct) {
								'E374_01' '140'
							} else {
								'E374_01' '139'
							}
							'E373_02' util.convertDateTime(fndDatetime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(fndDatetime, xmlDateTimeFormat, HHmm)
							
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
		def txnErrorKeys = []
		
		//duplication -- CT special logic
//		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
//		println(duplicatedPairs)
//		
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

		// Modified by Louis , global variable : firstScac
		if (ct.Body.size() > 0) {
			firstScac = ct.Body[0]?.GeneralInformation?.SCAC
		}
		
		//start body looping
		ct.Body.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('OECL002280', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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

		//End root node
		outXml.nodeCompleted(null,T315)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		//promote csupload and bizkey to session
		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID);
		if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);

		
		
		
		
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

//
//	void missingEventStatusCodeCS220(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
//		Map<String,String> errorKey = null
//		if (StringUtil.isEmpty(eventExtCde)) {
//			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg]
//			errorKeyList.add(errorKey)
//		}
//	}
	

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
		// for this TP=OECL002280, it has two CSUpload cases, as below
		//for no EventCodeConversion case, error
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		
		//For CS220, it is invalid, Obsolete it
		if(eventCS2Cde=='CS220')
		{
			ctUtil.missingEventStatusCode(eventCS2Cde, '', false, 'No UIF Output - CS220', errorKeyList)
		}
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
		
//		// - Missing Container Number
//		ctUtil.missingContainerNumber(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
		//for this TP=, no seg_num='ALLOW_CS260_INTERMODAL' in DB, but exist in tibco, so CSUpload "filterIBIntermodal"
		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route?.Inbound_intermodal_indicator, false, null, errorKeyList)
		
		return errorKeyList;
	}


	public boolean pospValidation() {

	}

	
	
	
	public String getLocationList(String CITY_NME, String STATE_CDE, String CNTRY_CDE,Connection conn,String[] LocationList) throws Exception {
		if (conn == null)
		return "";
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		if (CITY_NME==null||STATE_CDE==null||CNTRY_CDE==null||CITY_NME.equals("")||STATE_CDE.equals("")||CNTRY_CDE.equals("")) return ""
		String sql = "select distinct UN_LOCN_CDE, SCHED_TYPE, SCHED_KD_CDE from CSS_CITY_LIST where CITY_NME =?  and  STATE_CDE =? and cntry_cde = '${CNTRY_CDE}'";
		try {
		pre = conn.prepareStatement(sql);
		pre.setMaxRows(100);
		pre.setQueryTimeout(10);
		pre.setString(1, CITY_NME);
		pre.setString(2, STATE_CDE);
		
		
		result = pre.executeQuery();
			if (result.next()) {
				LocationList[0] = result.getString(1);
				LocationList[1] = result.getString(2);
				LocationList[2] = result.getString(3);
			}
		} finally {
		if (result != null)
		result.close();
		if (pre != null)
		pre.close();
		}
		
		}
//
	public String getLocationListWithoutStateCode(String CITY_NME, String CNTRY_CDE,Connection conn,String[] LocationList) throws Exception {
		if (conn == null)
		return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select distinct UN_LOCN_CDE, SCHED_TYPE, SCHED_KD_CDE ,CNTRY_CDE from CSS_CITY_LIST where CITY_NME=? and cntry_cde = '${CNTRY_CDE}' ";
		try {
		pre = conn.prepareStatement(sql);
		pre.setMaxRows(100);
		pre.setQueryTimeout(10);
		pre.setString(1, CITY_NME);


		result = pre.executeQuery();
			if (result.next()) {

				LocationList[0] = result.getString(1);
				LocationList[1] = result.getString(2);
				LocationList[2] = result.getString(3);
			}

		} finally {
		if (result != null)
		result.close();
		if (pre != null)
		pre.close();
		}

		}
//	public String getShipDir(String TP_ID, String Seg_ID, String DIR_ID, String convertTypeId, String Int_Cde,String Seg_Num,  Connection conn) throws Exception {
//		if (conn == null)
//		return "";
//
//		String ret = "";
//		PreparedStatement pre = null;
//		ResultSet result = null;
//		String sql = "select ext_cde from b2b_edi_cde_ref where tp_id=? and dir_id =? and  CONVERT_TYPE_ID =? and seg_id=? and seg_num=? and int_cde =?";
//
//		try {
//		pre = conn.prepareStatement(sql);
//		pre.setMaxRows(100);
//		pre.setQueryTimeout(10);
//		pre.setString(1, TP_ID);
//		pre.setString(2, DIR_ID);
//		pre.setString(3, convertTypeId);
//		pre.setString(4, Seg_ID);
//		pre.setString(5, Seg_Num);
//		pre.setString(6, Int_Cde);
//		result = pre.executeQuery();
//
//		if (result.next()) {
//		ret = result.getString(1);
//		}
//		} finally {
//		if (result != null)
//		result.close();
//		if (pre != null)
//		pre.close();
//		}
//		return ret;
//		}
//
//	public String getExtReferenceCode(String TP_ID, String SRC_FMT_ID, String TRG_FMT_ID, String DIR_ID, String CONVERT_TYPE_ID, String SEG_ID,String SEG_NUM, String INT_CDE,Connection conn) throws Exception {
//		if (conn == null)
//		return "";
//
//		String ret = "";
//		PreparedStatement pre = null;
//		ResultSet result = null;
//		String sql = "select Ext_Cde from b2b_edi_cde_ref where TP_ID=? AND SRC_FMT_ID =? AND TRG_FMT_ID =? AND DIR_ID =? AND CONVERT_TYPE_ID =? AND SEG_ID =? AND SEG_NUM=? AND INT_CDE=?";
//
//		try {
//		pre = conn.prepareStatement(sql);
//		pre.setMaxRows(util.getDBRowLimit());
//		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
//		pre.setString(1, TP_ID);
//		pre.setString(2, SRC_FMT_ID);
//		pre.setString(3, TRG_FMT_ID);
//		pre.setString(4, DIR_ID);
//		pre.setString(5, CONVERT_TYPE_ID);
//		pre.setString(6, SEG_ID);
//		pre.setString(7, SEG_NUM);
//		pre.setString(8, INT_CDE);
//
//		result = pre.executeQuery();
//			if (result.next()) {
//
//				ret = result.getString(1);
//			}
//		} finally {
//		if (result != null)
//		result.close();
//		if (pre != null)
//		pre.close();
//		}
//		return ret
//		}
//
//	public String getIxPartyTypeExtCode(String CONVERT_TYPE_ID, String MSG_TYPE_ID, String DIR_ID, String INT_CDE, Connection conn) throws Exception {
//		if (conn == null)
//		return "";
//
//		String ret = "";
//		PreparedStatement pre = null;
//		ResultSet result = null;
//		String sql = "select Ext_Cde from B2B_CDE_CONVERSION where CONVERT_TYPE_ID=? AND MSG_TYPE_ID=? and DIR_ID=? AND INT_CDE=?";
//
//		try {
//		pre = conn.prepareStatement(sql);
//		pre.setMaxRows(util.getDBRowLimit());
//		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
//		pre.setString(1, CONVERT_TYPE_ID);
//		pre.setString(2, MSG_TYPE_ID);
//		pre.setString(3, DIR_ID);
//		pre.setString(4, INT_CDE);
//
//
//
//		result = pre.executeQuery();
//			if (result.next()) {
//
//				ret = result.getString(1);
//			}
//		} finally {
//		if (result != null)
//		result.close();
//		if (pre != null)
//		pre.close();
//		}
//		return ret
//		}
//
//	public String getLegalPartyCodeTypeExtCDE(String TP_ID, String SRC_FMT_ID, String TRG_FMT_ID, String DIR_ID, String CONVERT_TYPE_ID, String INT_CDE,Connection conn) throws Exception {
//		if (conn == null)
//		return "";
//
//		String ret = "";
//		PreparedStatement pre = null;
//		ResultSet result = null;
//		String sql = "select Ext_Cde from b2b_edi_cde_ref where TP_ID=? AND SRC_FMT_ID =? AND TRG_FMT_ID =? AND DIR_ID =? AND CONVERT_TYPE_ID =? AND INT_CDE=?";
//
//		try {
//		pre = conn.prepareStatement(sql);
//		pre.setMaxRows(util.getDBRowLimit());
//		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
//		pre.setString(1, TP_ID);
//		pre.setString(2, SRC_FMT_ID);
//		pre.setString(3, TRG_FMT_ID);
//		pre.setString(4, DIR_ID);
//		pre.setString(5, CONVERT_TYPE_ID);
//		pre.setString(6, INT_CDE);
//
//		result = pre.executeQuery();
//			if (result.next()) {
//
//				ret = result.getString(1);
//			}
//		} finally {
//		if (result != null)
//		result.close();
//		if (pre != null)
//		pre.close();
//		}
//		return ret
//		}

//	getOceanCarrierID
//	public String getOceanCarrierID(String SCAC, Connection conn) throws Exception {
//		if (conn == null)
//		return "";
//		
//		String ret = "";
//		PreparedStatement pre = null;
//		ResultSet result = null;
//		String sql = "select id from b2b_ocean_carrier where scac = ?";
//		
//		try {
//		pre = conn.prepareStatement(sql);
//		pre.setMaxRows(util.getDBRowLimit());
//		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
//		pre.setString(1, SCAC);
//		
//	
//		result = pre.executeQuery();
//			if (result.next()) {
//				
//				ret = result.getString(1);
//			}
//		} finally {
//		if (result != null)
//		result.close();
//		if (pre != null)
//		pre.close();
//		}
//		return ret
//		}
//	public String getSCACCDE(String convert_type_id, String Ext_Cde,Connection conn) throws Exception {
//		if (conn == null)
//		return "";
//		
//		String ret = "";
//		PreparedStatement pre = null;
//		ResultSet result = null;
//		String sql = "select scac_cde from b2b_cde_conversion where convert_type_id = ? and ext_cde = ?";
//		
//		try {
//		pre = conn.prepareStatement(sql);
//		pre.setMaxRows(util.getDBRowLimit());
//		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
//		pre.setString(1, convert_type_id);
//		pre.setString(2, Ext_Cde);
//		
//	
//		result = pre.executeQuery();
//			if (result.next()) {
//				
//				ret = result.getString(1);
//			}
//		} finally {
//		if (result != null)
//		result.close();
//		if (pre != null)
//		pre.close();
//		}
//		return ret
//		}
//	public String getCSSTD12ExtCDE(String msg_type_id, String dir_id,String CONVERT_TYPE_ID,String int_cde,  Connection conn) throws Exception {
//		if (conn == null)
//		return "";
//
//		String ret;
//		PreparedStatement pre = null;
//		ResultSet result = null;
//		String sql = "select ext_cde from b2b_cde_conversion where msg_type_id=? and dir_id=? and CONVERT_TYPE_ID=? and int_cde =? ";
//
//		try {
//		pre = conn.prepareStatement(sql);
//		pre.setMaxRows(util.getDBRowLimit());
//		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
//		pre.setString(1, msg_type_id);
//		pre.setString(2, dir_id);
//		pre.setString(3, CONVERT_TYPE_ID);
//		pre.setString(4, int_cde);
//
//
//		result = pre.executeQuery();
//
//		if (result.next()) {
//		ret=result.getString(1);
//		}
//		} finally {
//		if (result != null)
//		result.close();
//		if (pre != null)
//		pre.close();
//		}
//		return ret;
//		}
//
//	public String getEventStatusQuery(String INT_CDE, String CONVERT_TYPE_ID, String DIR_ID, String MSG_TYPE_ID, String TP_ID, Connection conn) throws Exception {
//		if (conn == null)
//			return "";
//
//		String ret = "";
//		PreparedStatement pre = null;
//		ResultSet result = null
//		String sql = "SELECT EXT_CDE FROM B2B_CDE_CONVERSION WHERE INT_CDE = ? AND CONVERT_TYPE_ID = ? AND DIR_ID = ? AND MSG_TYPE_ID = ? AND MSG_FMT_ID is null AND TP_ID = ? AND SCAC_CDE is null";
//
//		try {
//			pre = conn.prepareStatement(sql);
//			pre.setMaxRows(util.getDBRowLimit());
//			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
//
//			pre.setString(1, INT_CDE);
//			pre.setString(2, CONVERT_TYPE_ID);
//			pre.setString(3, DIR_ID);
//			pre.setString(4, MSG_TYPE_ID);
//			pre.setString(5, TP_ID);
//
//			result = pre.executeQuery();
//			if (result.next()) {
//				ret = result.getString(1);
//			}
//		} finally {
//			if (result != null)
//				result.close();
//			if (pre != null)
//				pre.close();
//			}
//		return ret
//	}
	

	
}






