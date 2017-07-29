package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils
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

public class CUS_CS2CTXML_315_EMERSONAPLL {

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
	
	// Special param for SpecialLogicOn315Type9
	//0 for common body (no special duplicate)
	//1 for CS190toOAandD_EU Duplicate
	//	(1).Change B4 E157_03 = 'D'
	//	(2).Change B4 E159_06 = R403E
	//	(3).Change B4 E310_11 = R403E
	//	(4).Change B4 E309_12 = R402E
	//	(5).If R4 E115_01 = 'E' Then cover currentR4 DTM E374_01 = '140' and E373_02 = B404 and E337_03 = B405
	//2 for CS160toOAandD_NATEDI319A35 Duplicate
	//	(1).Change B4 E157_03 = 'D'
	//	(2).Change B4 E159_06 = R403E
	//	(3).Change B4 E310_11 = R403E
	//	(4).Change B4 E309_12 = R402E
	//	(5).If R4 E115_01 = 'E' Then cover currentR4 DTM E374_01 = '140' and E373_02 = B404 and E337_03 = B405
	//3 for CS190toOAandD_US_M Duplicate 
	//	(1).Change B4 E157_03 = 'D'
	//	(2).Change B4 E159_06 = R403E
	//	(3).Change B4 E310_11 = R403E
	//	(4).Change B4 E309_12 = R402E
	//	(5).If R4 E115_01 = 'E' Then change currentR4 DTM E374_01 = '140'
	def specialDuplicate_flag = 0
	
	def list_EUCountry= ['AD', 'AL', 'AM', 'AN', 'AT', 'AZ', 'BA', 
		'BE', 'BF', 'BG', 'BY', 'CH', 'CS', 'CY', 'CZ', 'DE', 
		'DK', 'EE', 'EG', 'ES', 'FI', 'FJ', 'FK', 'FM', 'FO',
		'FR', 'GB', 'GE', 'GI', 'GR', 'HR', 'HU', 'IE', 'IS',
		'IT', 'LI', 'LT', 'LU', 'LV', 'MC', 'MD', 'MK', 'MT',
		'NL', 'NO', 'PL', 'PT', 'RO', 'RU', 'SE', 'SI', 'SK',
		'SM', 'SZ', 'UA', 'UG', 'YU']
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('EMERSONAPLL', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  util.getEDICdeReffromIntCde('EMERSONAPLL', 'ShipDirection', 'O', 'INTCTXML', 'Q2', 'EventStatusCode', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.

		def B404 = null
		def B405 = null
		def B406 = null
		def B411 = null
		def B412 = null
		
		def R402E = null
		def R403E = null
		
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {

				def defaultB406 = current_Body.Event?.Location?.LocationCode?.UNLocationCode
				def defaultB411 = ''
				def defaultB412 = 'UN'
				if(['CS070','CS080','CS110','CS120'].contains(vCS1EventFirst5)){
					if (current_Body.Event?.Location?.LocationName) {
						defaultB411 = util.substring(current_Body.Event.Location.LocationName.trim().toUpperCase(),1,30)
					}else if (current_Body.Event.Location?.CityDetails?.City) {
						defaultB411 = util.substring(current_Body.Event.Location.CityDetails.City.trim().toUpperCase(),1,30)
					}else if (current_Body.Event?.Location?.LocationCode?.UNLocationCode) {
						defaultB411 = current_Body.Event.Location.LocationCode.UNLocationCode.trim().toUpperCase()
					}
				}else {
					if (current_Body.Event.Location?.CityDetails?.City) {
						defaultB411 = util.substring(current_Body.Event.Location.CityDetails.City.trim().toUpperCase(),1,30)
					} else if (current_Body.Event?.Location?.LocationName) {
						defaultB411 = util.substring(current_Body.Event.Location.LocationName.trim().toUpperCase(),1,30)
					} else if (current_Body.Event?.Location?.LocationCode?.UNLocationCode) {
						defaultB411 = current_Body.Event.Location.LocationCode.UNLocationCode.trim().toUpperCase()
					}
				}  

				B404 = util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, yyyyMMdd)
				B405 = util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, HHmm)
				B406 = defaultB406
				B411 = defaultB411
				B412 = defaultB412

				//APLL special logic
				if (['CS160'].contains(vCS1EventFirst5)) {
					if (current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
						B406 = current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode
						B412 = 'UN'
					} else if (current_Body.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) {
						B406 = current_Body.Route.LastPOD.Port.LocationCode.SchedKDCode
						B412 = current_Body.Route.LastPOD.Port.LocationCode.SchedKDType
					}
					if (current_Body.Route?.LastPOD?.Port?.PortName) {
						B411 = util.substring(current_Body.Route.LastPOD.Port.PortName.toUpperCase(), 1, 24)
					} else if (current_Body.Route?.LastPOD?.Port?.City) {
						B411 = util.substring(current_Body.Route.LastPOD.Port.City.toUpperCase(), 1, 24)
					}
				}

				if(specialDuplicate_flag == 0){
					//common body
					'E157_03' vCS1EventCodeConversion
				}else if([1,2,3].contains(specialDuplicate_flag)){
					//special duplicate
					'E157_03' 'D'
				}
				
				'E373_04' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, HHmm)
				
				
				if (current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
					R403E = current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
				} else if (current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode) {
					R403E = current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode
				}
				if(specialDuplicate_flag == 0){
					//common body
					B406 = String.format('%-5s', B406)
				}else if([1,2,3].contains(specialDuplicate_flag)){
					//special duplicate
					B406 = R403E
				}
				
				if(B406){
					'E159_06' B406
				}
				
				if(current_Body.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
				}
				
				if(current_Body.Container?.ContainerNumber || current_Body.Container?.ContainerCheckDigit){
					'E207_08' (util.substring(current_Body.Container?.ContainerNumber, 5, 6) + (current_Body.Container.ContainerCheckDigit ? current_Body.Container.ContainerCheckDigit : ""))
				}
				
				if (current_Body.Container?.ContainerStatus?.trim() == 'Empty') {
					'E578_09' 'E'
				} else if (current_Body.Container?.ContainerStatus?.trim() == 'Laden') {
					'E578_09' 'L'
				} else if (current_Body.Event?.CS1Event?.trim() == 'CS210') {
					'E578_09' 'E'
				} else {
					'E578_09' 'L'
				}
				
				if(current_Body.Container?.CarrCntrSizeType){
					'E24_10' util.substring(current_Body.Container?.CarrCntrSizeType, 1, 4)
				}
			
				
				if(specialDuplicate_flag == 0){
					//common body
					B411 = util.substring(B411, 1, 30)
				}else if([1,2,3].contains(specialDuplicate_flag)){
					//special duplicate
					B411 = R403E
				}
				if(B411){
					'E310_11' B411
				}
				
				if (current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
					R402E = 'UN'
				} else if (current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode) {
					R402E = current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDType
				}
				
				if([1,2,3].contains(specialDuplicate_flag)){
					//special duplicate
					B412 = R402E
				}
				
				if(B412){
					'E309_12' B412
				}
				
			}
			//Loop N9
			//for avoid duplicated N9
			current_Body.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber.trim()}.each { blNumber, current_BookingGeneralInfoGroup ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(blNumber,1,30)
				}
			}
			
			current_Body.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it.CustomsReferenceNumber) && it.CustomsReferenceType == 'IT'}.groupBy{it.CustomsReferenceNumber?.trim()}.each { customerReferenceNumber, current_BLGeneralInfoGroup ->
				'N9' {
					'E128_01' 'IB'
					'E127_02' util.substring(customerReferenceNumber,1,30)
				}
			}
			
			current_Body.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each { bookingNumber, current_BookingGeneralInfoGroup ->
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
					'E127_02' util.substring(eq,1,30)
				}
			}
			
			if(current_Body.Container?.RailPickupNumber){
				'N9' {
					'E128_01' 'P8'
					'E127_02' current_Body.Container?.RailPickupNumber
				}
			}
			
			current_Body.Container?.Seal?.findAll{StringUtil.isNotEmpty(it.SealNumber) && it.SealNumber.length() >= 4}.groupBy{it.SealNumber?.trim()}.each { sealNumber, sealGroup ->
				'N9' {
					'E128_01' 'SN'
					'E127_02' util.substring(sealNumber,1,30)
				}
			}
			
			current_Body.ExternalReference?.findAll{it.CSReferenceType == 'PO' && StringUtil.isNotEmpty(it.ReferenceNumber)}.groupBy{it.ReferenceNumber?.trim()}.each{ currentPONumber, current_ExternalReferenceGroup ->
				'N9' {
					'E128_01' 'PO'
					'E127_02' util.substring(currentPONumber,1,30)
				}
			}

			Party partyCGN = current_Body.Party?.find{it.PartyType == 'CGN' && StringUtil.isEmpty(it.PartyLevel)}
			Party partyCGNWithPartyLevel = current_Body.Party?.find{it.PartyType == 'CGN' && StringUtil.isNotEmpty(it.PartyLevel)}

			if (partyCGN?.CarrierCustomerCode) {
				'N9' {
					'E128_01' 'IC'
					'E127_02' util.substring(partyCGN?.CarrierCustomerCode?.trim(),1,30)
					// this should be bug in tibco
					if (partyCGN?.CarrierCustomerCode?.trim()?.length() == 10 && partyCGN?.PartyName) {
						'E369_03' util.substring(partyCGN?.PartyName?.trim(),1,45)
					}
				}
			} else if (partyCGNWithPartyLevel?.CarrierCustomerCode) {
				'N9' {
					'E128_01' 'IC'
					'E127_02' util.substring(partyCGNWithPartyLevel?.CarrierCustomerCode?.trim(),1,30)
					// this should be bug in tibco
					if (partyCGNWithPartyLevel?.CarrierCustomerCode?.trim()?.length() == 10 && partyCGNWithPartyLevel?.PartyName) {
						'E369_03' util.substring(partyCGNWithPartyLevel?.PartyName?.trim(),1,45)
					}
				}
			}

			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body.Route?.OceanLeg){
				firstOceanLeg = current_Body.Route.OceanLeg[0]
				lastOceanLeg = current_Body.Route.OceanLeg[-1]
			}
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
					if (polDepartDtA?.toString()) {
						'E373_04' util?.convertDateTime(polDepartDtA, xmlDateTimeFormat, yyyyMMdd)
					} else if (polDepartDtE?.toString()) {
						'E373_04' util?.convertDateTime(polDepartDtE, xmlDateTimeFormat, yyyyMMdd)
					}
					LocDT podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT
					LocDT podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT
					if (podArrivalDtA?.toString()) {
						'E373_05' util?.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
					} else if (podArrivalDtE?.toString()) {
						'E373_05' util?.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
					}
					if (lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction) {
						'E55_09' ((lastOceanLeg?.SVVD?.Discharge?.Voyage ? lastOceanLeg?.SVVD?.Discharge?.Voyage:"") + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction:""))
					}
					'E127_11' current_Body.GeneralInformation?.SCAC
					'E897_12' 'L'
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
					if (polDepartureDtA?.toString()) {
						'E373_04' util?.convertDateTime(polDepartureDtA, xmlDateTimeFormat, yyyyMMdd)
					} else if (polDepartureDtE?.toString()) {
						'E373_04' util?.convertDateTime(polDepartureDtE, xmlDateTimeFormat, yyyyMMdd)
					}

					LocDT podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT
					LocDT podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT
					if (podArrivalDtA?.toString()) {
						'E373_05' util?.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
					} else if (podArrivalDtE?.toString()) {
						'E373_05' util?.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
					}
					if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
						'E55_09' ((firstOceanLeg?.SVVD?.Loading?.Voyage ? firstOceanLeg?.SVVD?.Loading?.Voyage:"") + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction:""))
					}
					'E127_11' current_Body.GeneralInformation?.SCAC
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
							'E114_04' util?.substring(POR?.CityDetails?.City?.toUpperCase(), 1, 24)
						}
						if (POR?.CSStandardCity?.CSCountryCode?.length() >= 2){
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
					} else if (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT?.toString() && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C') {
						porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT
					} else if (current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString() && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M') {
						porDTM = current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT
					}
					if (porDTM?.toString()) {
						'DTM' {
							if (isAct) {
								'E374_01' '370'
							} else {
								'E374_01' '369'
							}
							'E373_02' util.convertDateTime(porDTM, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(porDTM, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
	
				}
			}
			
			def R402L = null
			def R403L = null
			def R404L = null
			def R405L = null
			def R408L = null
			
			if(['CS070','CS080','CS110','CS120'].contains(vCS1EventFirst5)){
				
				if(StringUtil.isNotEmpty(firstOceanLeg?.POL?.Port?.LocationCode?.UNLocationCode)){
					R402L = 'UN'
				}else if(StringUtil.isNotEmpty(firstOceanLeg?.POL?.Port?.LocationCode?.SchedKDCode)){
					R402L = util.substring(firstOceanLeg?.POL?.Port?.LocationCode?.SchedKDType, 1, 1)
				}
				
				if(StringUtil.isNotEmpty(firstOceanLeg?.POL?.Port?.LocationCode?.UNLocationCode)){
					R403L = util.substring(firstOceanLeg?.POL?.Port?.LocationCode?.UNLocationCode,1,5)
				}else if(StringUtil.isNotEmpty(firstOceanLeg?.POL?.Port?.LocationCode?.SchedKDCode)){
					R403L = util.substring(firstOceanLeg?.POL?.Port?.LocationCode?.SchedKDCode,1,5)
				}
				
				if(StringUtil.isNotEmpty(firstOceanLeg?.POL?.Port?.PortName)){
					R404L = util.substring(firstOceanLeg?.POL?.Port?.PortName?.toUpperCase(),1,24)
				}else if(StringUtil.isNotEmpty(firstOceanLeg?.POL?.Port?.City)){
					R404L = util.substring(firstOceanLeg?.POL?.Port?.City?.toUpperCase(),1,24)
				}
				
				R405L = util.substring(firstOceanLeg?.POL?.Port?.CSCountryCode,1,2)
				
				if(StringUtil.isNotEmpty(firstOceanLeg?.POL?.CSStateCode)){
					R408L = util.substring(firstOceanLeg?.POL?.CSStateCode, 1, 2)
				}
			}else {
			
				if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
					R402L = 'UN'
				} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
					R402L = firstPOL?.Port?.LocationCode?.SchedKDType
				}
				
				if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
					R403L = firstPOL?.Port?.LocationCode?.UNLocationCode
				} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
					R403L = firstPOL?.Port?.LocationCode?.SchedKDCode
				}
				
				if (firstPOL?.Port?.PortName) {
					R404L = util?.substring(firstPOL?.Port?.PortName?.toUpperCase(), 1, 24)
				} else if (firstPOL?.Port?.City) {
					R404L = util?.substring(firstPOL?.Port?.City?.toUpperCase(), 1, 24)
				}
				
				R405L = util.substring(firstPOL?.Port?.CSCountryCode,1,2)
				
				if (firstPOL?.CSStateCode) {
					R408L = firstPOL?.CSStateCode
				}
			}
			
			if(firstPOL){
				'Loop_R4' {

					'R4' {
						'E115_01' 'L'
						if (R402L) {
							'E309_02' R402L
						}
						if (R403L) {
							'E310_03' R403L
						}
						if (R404L) {
							'E114_04' R404L
						}
						if (R405L) {
							'E26_05' R405L
						}
						if (R408L) {
							'E156_08' R408L
						}
					}
					LocDT polDepartureDtA = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT polDepartureDtE = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
					if (polDepartureDtA?.toString()) {
						'DTM' {
							'E374_01' '370'
							'E373_02' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					} else if (polDepartureDtE?.toString()) {
						'DTM' {
							'E374_01' '369'
							'E373_02' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
				}
			}
			def R402D = null
			def R403D = null
			def R404D = null
			def R405D = null
			def R408D = null

			if(['CS070','CS080','CS110','CS120'].contains(vCS1EventFirst5)){
				
				if(StringUtil.isNotEmpty(lastOceanLeg?.POD?.Port?.LocationCode?.UNLocationCode)){
					R402D = 'UN'
				}else if(StringUtil.isNotEmpty(lastOceanLeg?.POD?.Port?.LocationCode?.SchedKDCode)){
					R402D = util.substring(lastOceanLeg?.POD?.Port?.LocationCode?.SchedKDType, 1, 1)
				}
				
				if(StringUtil.isNotEmpty(lastOceanLeg?.POD?.Port?.LocationCode?.UNLocationCode)){
					R403D = util.substring(lastOceanLeg?.POD?.Port?.LocationCode?.UNLocationCode,1,5)
				}else if(StringUtil.isNotEmpty(lastOceanLeg?.POD?.Port?.LocationCode?.SchedKDCode)){
					R403D = util.substring(lastOceanLeg?.POD?.Port?.LocationCode?.SchedKDCode,1,5)
				}
				
				if(StringUtil.isNotEmpty(lastOceanLeg?.POD?.Port?.PortName)){
					R404D = util.substring(lastOceanLeg?.POD?.Port?.PortName?.toUpperCase(),1,24)
				}else if(StringUtil.isNotEmpty(lastOceanLeg?.POD?.Port?.City)){
					R404D = util.substring(lastOceanLeg?.POD?.Port?.City?.toUpperCase(),1,24)
				}
				
				R405D = util.substring(lastOceanLeg?.POD?.Port?.CSCountryCode,1,2)
				
				if(StringUtil.isNotEmpty(lastOceanLeg?.POD?.CSStateCode)){
					R408D = util.substring(lastOceanLeg?.POD?.CSStateCode, 1, 2)
				}
				
			}else {
			
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
					R404D = util?.substring(lastPOD?.Port?.PortName?.toUpperCase(), 1, 24)
				} else if (lastPOD?.Port?.City) {
					R404D = util?.substring(lastPOD?.Port?.City?.toUpperCase(), 1, 24)
				}
				
				R405D = util.substring(lastPOD?.Port?.CSCountryCode,1,2)
				
				if (lastPOD?.CSStateCode) {
					R408D = lastPOD?.CSStateCode
				}
				
			}
			
			if ((R403D || R404D) && !['CS160', 'CS120'].contains(vCS1EventFirst5)) {
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
					LocDT podArrivalDtA = null
					LocDT podArrivalDtE = null
					current_Body?.Route?.OceanLeg?.findAll{
						!(StringUtil.isEmpty(it.SVVD?.Loading?.Vessel)
						&& (StringUtil.isEmpty(it.SVVD?.Loading?.Voyage) && StringUtil.isEmpty(it.SVVD?.Loading?.Direction))
						&& StringUtil.isEmpty(it.SVVD?.Loading?.VesselName))}.each{current_OceanLeg->
						if(current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT ||
							current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT) {
							podArrivalDtA = current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
							podArrivalDtE = current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
						}
					}
					if (podArrivalDtA?.toString()) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
						//SpecialLogicOn315Type9 MapDTM059
						//Copy DTM and Change E374_01 to '059'
						if(current_Body.BLGeneralInfo?.find{StringUtil.isNotEmpty(it.CustomsReferenceNumber) && it.CustomsReferenceType == 'IT'}){
							'DTM' {
								'E374_01' '059'
								'E373_02' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						}
					} else if (podArrivalDtE?.toString()) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
						//SpecialLogicOn315Type9 MapDTM059
						//Copy DTM and Change E374_01 to '059'
						if(current_Body.BLGeneralInfo?.find{StringUtil.isNotEmpty(it.CustomsReferenceNumber) && it.CustomsReferenceType == 'IT'}){
							'DTM' {
								'E374_01' '059'
								'E373_02' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						}
					}
				}
			}

			def R404E = null
			def R405E = null
			def R408E = null
			
			if (FND?.CityDetails?.City) {
				R404E = util?.substring(FND?.CityDetails?.City?.toUpperCase(), 1, 24)
			}
			if (FND?.CSStandardCity?.CSContinentCode?.length() >= 2) {
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
					def isAct = null
					
					LocDT vCargoDateTime = current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT vArriDateTime = null
					if(current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT?.toString()){
						vArriDateTime = current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT
					}else if(current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString()
						&& !current_Body.GeneralInformation?.SCAC.equals('OOLU') ){
						vArriDateTime = current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					}

					def vEventDateTime = util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					def vActCargoDeliveryDateTime = util.convertDateTime(vCargoDateTime, xmlDateTimeFormat, yyyyMMdd)
					def vActArrivalAtFinalHubDateTime = util.convertDateTime(vArriDateTime, xmlDateTimeFormat, yyyyMMdd)

					if (vActCargoDeliveryDateTime && vEventDateTime <= vActCargoDeliveryDateTime) {
						isAct = true
						fndDatetime = vCargoDateTime
					} else if(vActArrivalAtFinalHubDateTime && vEventDateTime <= vActArrivalAtFinalHubDateTime){
						isAct = true
						fndDatetime = vArriDateTime
					} else if (current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT?.toString()) {
						fndDatetime = current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT
						isAct = false
					} else if (current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT?.toString()){
						isAct = false
						fndDatetime = current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT
					} else if (current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator='E'}?.LocDT?.toString()
						&& !current_Body.GeneralInformation?.SCAC.equals('OOLU')){
						isAct = false
						fndDatetime = current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator='E'}?.LocDT
					} else if (lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT.toString()) {
						fndDatetime = lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
						isAct = false
					} else if (lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT.toString()) {
						fndDatetime = lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
						isAct = true
					}
					if(specialDuplicate_flag == 0){
						//common body
						if (fndDatetime?.toString()) {
							'DTM' {
								if (isAct) {
									'E374_01' '140'
								} else {
									'E374_01' '139'
								}
								'E373_02' util.convertDateTime(fndDatetime, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(fndDatetime, xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						}
					}else if([1,2].contains(specialDuplicate_flag)){
						'DTM' {
							'E374_01' '140'
							if(B404){
								'E373_02' B404
							}
							if(B405){
								'E337_03' B405
							}
						}
					}else if([3].contains(specialDuplicate_flag)){
						if (fndDatetime?.toString()) {
							'DTM' {
								'E374_01' '140'
								'E373_02' util.convertDateTime(fndDatetime, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(fndDatetime, xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						}
					}
				}
			}
			if ((R403D || R404D) && ['CS160', 'CS120'].contains(vCS1EventFirst5)) {
				'Loop_R4' {
					'R4' {
						'E115_01' '1'
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
					LocDT podArrivalDtA = null
					LocDT podArrivalDtE = null
					current_Body?.Route?.OceanLeg?.findAll{
						!(StringUtil.isEmpty(it.SVVD?.Loading?.Vessel)
						&& (StringUtil.isEmpty(it.SVVD?.Loading?.Voyage) && StringUtil.isEmpty(it.SVVD?.Loading?.Direction))
						&& StringUtil.isEmpty(it.SVVD?.Loading?.VesselName))}.each{current_OceanLeg->
						if(current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT ||
							current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT) {
							podArrivalDtA = current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
							podArrivalDtE = current_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
						}
					}
					if (podArrivalDtA?.toString()) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					} else if (podArrivalDtE?.toString()) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
				}
			}
			//SpecialLogicOn315Type9 MapR4-l
			'Loop_R4' {
				'R4' {
					'E115_01' 'I'
					if (B412) {
						'E309_02' B412
					}
					
					def vEventLocationCodeUN = util.substring(current_Body?.Event?.Location?.LocationCode?.UNLocationCode, 1, 5)
					if (vEventLocationCodeUN) {
						'E310_03' vEventLocationCodeUN
					}
					if (B411) {
						'E114_04' B411
					}
					
					def vEventCountryCode = util.substring(current_Body.Event?.Location?.CSStandardCity?.CSCountryCode, 1, 2)
					if (vEventCountryCode) {
						'E26_05' vEventCountryCode
					}
				}
				'DTM' {
					'E374_01' '140'
					
					if(B404){
						'E373_02' B404
					}
					
					if(B405){
						'E337_03' B405
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
			'I07_08' 'CARSONDSG      '
			'I08_09' currentSystemDt.format("yyMMdd")
			'I09_10' currentSystemDt.format(HHmm)
			'I10_11' 'U'
			'I11_12' '00401'
			'I12_13' '1'
			'I13_14' '0'
			'I14_15' 'P'
			'I15_16' '>'
		}
		outXml.'GS' {
			'E479_01' 'QO'
			'E142_02' 'CARGOSMART'
			'E124_03' 'CARSONDSG'
			'E373_04' currentSystemDt.format(yyyyMMdd)
			'E337_05' currentSystemDt.format(HHmm)
			'E28_06' '1'
			'E455_07' 'X'
			'E480_08' '004010'
		}

	}
	
	
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
		
//		generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)
		
		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs('EMERSONAPLL', conn)
		
		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)
		
		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('EMERSONAPLL', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)
			
			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

			if (errorKeyList.size() == 0) {
				//pass validateBeforeExecution
				//main mapping
				generateBody(current_Body, outXml)
				outputBodyCount++
				
				//Special Duplicate For SpecialLogicOn315Type9
				specialDuplicate_flag = getSpecialDuplicateFlag(current_Body)
				if(specialDuplicate_flag != 0){
					generateBody(current_Body, outXml)
					outputBodyCount++
				}
				//reset the flag after special duplicate
				specialDuplicate_flag = 0
			}

			//posp checking
			pospValidation()

			ctUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, eventExtCde, TP_ID, conn)

			txnErrorKeys.add(errorKeyList);
		}

		//EndWorkFlow

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
			//Postprocess Replace GS
			result = writer?.toString().replaceAll("HO CHI MINH \\(CAT LAI\\)","HO CHI MINH").replaceAll("VNCLI","VNSGN")
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()
		
		return result
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
		// error cases
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
		ctUtil.missingContainerNumber(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
		
		// obsolete cases
		ctUtil.missingBlNumber(eventCS2Cde, current_Body.BLGeneralInfo, false, null, errorKeyList)
		ctUtil.missingConsigneeCode(eventCS2Cde, current_Body.Party, false, null ,errorKeyList)
		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route?.Inbound_intermodal_indicator, false, null, errorKeyList)
		
		return errorKeyList;
	}

	public boolean pospValidation() {

	}
	
	//For SpecialLogicOn315/Type9
	private int getSpecialDuplicateFlag(Body current_Body){
		def specialDuplicate_flag = 0
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vLastPODUnLocCode = util.substring(current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode,1,5)
		def vFinalDestinationUnLocCode = util.substring(current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode,1,5)
		def vLastPODCountryCode = util.substring(current_Body.Route?.LastPOD?.Port?.CSCountryCode,1,2)
		def vIBHaulageIndicator = util.substring(current_Body.BookingGeneralInfo[0]?.Haulage?.InBound,1,1)
		def vIBIntermodalIndicator = current_Body.Route?.Outbound_intermodal_indicator?.trim() ? 
				current_Body.Route?.Outbound_intermodal_indicator.trim() + current_Body.Route?.Outbound_intermodal_indicator.trim() : '00'
		vIBIntermodalIndicator = util.substring(vIBIntermodalIndicator,1,2)
		if(list_EUCountry.contains(vLastPODCountryCode) && ['CS190'].contains(vCS1EventFirst5)){
			//CS190toOAandD_EU Duplicate T315
			specialDuplicate_flag = 1
		}else if(['CS160','CS190'].contains(vCS1EventFirst5)
			&& ['USLGB','USLAX'].contains(vLastPODUnLocCode)
			&& ['USCNO','USRCU'].contains(vFinalDestinationUnLocCode)){
			//CS160toOAandD_NATEDI319A35
			specialDuplicate_flag = 2
		}else if(['CS190'].contains(vCS1EventFirst5)
			&& ['US'].contains(vLastPODCountryCode)
			&& ['M'].contains(vIBHaulageIndicator)){
			//CS190toOAandD_US_M
			specialDuplicate_flag = 3
		}
		return specialDuplicate_flag;
	}
	
}






