package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List;
import java.util.Map;

import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser


/**
 * @author Neo Ke
 * @pattern ref TP=CVI
 */
public class CUS_CS2CTXML_315_AHM {

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
	
	def firstScac = null
	

	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('AHM', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		//		def shipDir2 =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
		//		def shipDir=getShipDir('AHM','Q2','O','ShipDirection',vCS1EventFirst5,'EventStatusCode',conn)
		def shipDir = util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
		def B406 = null
		def B411 = null
		def B412 = null
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

				if (util.substring(current_Body.Container?.ContainerNumber, 5, 6)?.trim()) {
					//					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5,current_Body.Container?.ContainerNumber?.length())
					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, 6)
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
				if((LocationList[0]!=""&&LocationList[0]!=null)||(LocationList[2]!=""&&LocationList[2]!=null)){
					if(LocationList[0]!=""&&LocationList[0]!=null)
					{
						'E310_11' util.substring(LocationList[0]?.trim(), 1, 30)?.trim()
					}
					else if(LocationList[2]!=""&&LocationList[2]!=null)
					{
						'E310_11' util.substring(LocationList[2]?.trim(), 1, 30)?.trim()
					}
					else if(current_Body.Event?.Location?.CityDetails?.City)
					{
						'E310_11' util.substring(current_Body.Event?.Location?.CityDetails?.City?.trim(), 1, 30)?.trim()
					}
					else if(current_Body.Event?.Location?.LocationName)
					{
						'E310_11' util.substring(current_Body.Event?.Location?.LocationName?.trim(), 1, 30)?.trim()
					}
				}
				else if(current_Body.Event?.Location?.CityDetails?.City)
				{
					'E310_11' util.substring(current_Body.Event?.Location?.CityDetails?.City?.trim(), 1, 30)?.trim()
				}
				else if(current_Body.Event?.Location?.LocationName)
				{
					'E310_11' util.substring(current_Body.Event?.Location?.LocationName?.trim(), 1, 30)?.trim()
				}



				if((LocationList[0]!=""&&LocationList[0]!=null)||(LocationList[2]!=""&&LocationList[2]!=null)){
					if(LocationList[0]!=""&&LocationList[0]!=null)
					{
						'E309_12' 'UN'
					}
					else if(LocationList[1]=="K"||LocationList[1]=="D")
					{
						'E309_12' LocationList[1]
					}
					else if(current_Body.Event?.Location?.CityDetails?.City||current_Body.Event?.Location?.LocationName)
					{
						'E309_12' 'CI'
					}
				}
				else if((current_Body.Event?.Location?.CityDetails?.City||current_Body.Event?.Location?.LocationName))
				{
					'E309_12' 'CI'
				}
				'E761_13'  util.substring(current_Body.Container?.ContainerCheckDigit, 1,2)

			}
			//Loop N9
			//for avoid duplicated N9
//			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each {CarrierBookingNumber, CarrierBookingNumberGroup ->
			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.each {BN ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(BN?.CarrierBookingNumber?.trim(), 1, 30)?.trim()
				}
			}

//			current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber?.trim()}.each{BLNumber, BLNumberGroup ->
			current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.each{BL ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(BL?.BLNumber?.trim(), 1, 30)?.trim()
					
				}
			}

			// ContainerNumber
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

			// Seal Number
//			current_Body?.Container?.Seal.each{
//				def sealNumber=it?.SealNumber
//				if(sealNumber){
//					'N9' {
//						'E128_01' 'SN'
//						'E127_02' sealNumber
//					}
//				}
//			}
			
			//SealNumber
			String flag=false
			def SNs = []

			String Invalid_char="# \$ % & ' ( ) * , - . / < > @ \\ ^ ` ~"
			current_Body?.Container?.Seal?.each { current_Seal ->
				if (current_Seal?.SealNumber &&current_Seal?.SealNumber.trim().length() >= 4) {
					SNs.add(current_Seal.SealNumber?.trim())
					if(current_Seal?.SealNumber.contains(Invalid_char)){
						flag=true
					}
					if(flag.equals("false")){
						'N9' {
							'E128_01' 'SN'
							'E127_02' util.substring(current_Seal.SealNumber,1,12).trim()
						}
					}
				}
			}

			// ExtReferenceCode
			List legalRefIntCde = ['FN', 'INV', 'IK', 'PO', 'SC', 'SID', 'SO', 'SR']
			Map extRefMap = ['FN':'FN', 'INV':'IK', 'PO':'PO', 'SC':'E8', 'SID':'SI', 'SO':'SO', 'SR':'SR']
			def list = current_Body.ExternalReference?.findAll{StringUtil.isNotEmpty(it.CSReferenceType) && legalRefIntCde.contains(it.CSReferenceType) }
			list?.each{ ExternalReference ->
				if (StringUtil.isNotEmpty(ExternalReference.ReferenceNumber)) {
					'N9' {
						'E128_01' extRefMap.get(ExternalReference?.CSReferenceType)
						'E127_02' util.substring(ExternalReference?.ReferenceNumber, 1, 30)
					}
				}
			}

			//			def scExternalReference=current_Body.ExternalReference?.findAll{it?.CSReferenceType=='SC'}
			//			if(scExternalReference){
			//				'N9' {
			//					'E128_01' 'E8'
			//					'E127_02' scExternalReference[0]?.ReferenceNumber
			//				}
			//			}

			// LegalPartyCode
			def legalPartyIntCde = ['ANP', 'BPT', 'CCP', 'CGN', 'FWD', 'NPT', 'OTH', 'SHP']
			HashMap legalPartyMap = ['ANP':'AN', 'BPT':'BP', 'CGN':'CN', 'CCP':'CP', 'FWD':'FW', 'NPT':'NP', 'ANP':'NP', 'SHP':'SH', 'OTH':'UK']
			list = current_Body.Party?.findAll{legalPartyIntCde.contains(it.PartyType) && it?.PartyType=='SHP' && it?.PartyLevel != 'BL' && it?.PartyLevel != 'BK'}
			list?.each{current_Party ->
				'N9' {
					'E128_01' 'ZA'
					'E127_02' util.substring(current_Party?.PartyName?.trim(),1, 30)?.trim()
				}
			}

			list = current_Body.Party?.findAll{legalPartyIntCde.contains(it.PartyType) && it?.PartyLevel != 'BL' && it?.PartyLevel != 'BK'}
			list?.each{current_Party ->
				String customerType = legalPartyMap.get(current_Party?.PartyType)
				'N9' {
					if (customerType == 'CN') {
						'E128_01' '4E'
					} else if (customerType == 'SH') {
						'E128_01' '4F'
					} else {
						'E128_01' 'AAO'
					}
					'E127_02' util.substring(current_Party?.CarrierCustomerCode,1,10)?.trim()
				}
			}
			//			def bptParty=current_Body.Party?.findAll{it?.PartyType=='SHP' && it?.PartyLevel!='BK' && it?.PartyLevel!='BL'}
			//			if(bptParty){
			//				'N9' {
			//					'E128_01' 'ZA'
			//					'E127_02' bptParty[0]?.PartyName
			//				}
			//			}
			//
			//			def cgnParty=current_Body.Party?.findAll{it?.PartyType=='CGN' && it?.PartyLevel!='BK' && it?.PartyLevel!='BL'}
			//			if(cgnParty){
			//				'N9' {
			//					'E128_01' '4E'
			//					'E127_02' cgnParty[0]?.CarrierCustomerCode
			//				}
			//			}
			//
			//			def bptParty_4F=current_Body.Party?.findAll{it?.PartyType=='BPT' && it?.PartyLevel!='BK' && it?.PartyLevel!='BL'}
			//			if(bptParty_4F){
			//				'N9' {
			//					'E128_01' '4F'
			//					'E127_02' bptParty_4F[0]?.CarrierCustomerCode
			//				}
			//			}
			//
			//
			//			List<String> aaoList = new ArrayList<String>();
			//
			//			current_Body.Party?.findAll{it.PartyLevel!='BL' && it.PartyLevel!='BK' && it?.PartyType!='BPT' && it?.PartyType!='CGN'}.each {
			//				def TempCarrierCustomerCode=it.CarrierCustomerCode
			//				if(TempCarrierCustomerCode)
			//					if(!aaoList.contains(TempCarrierCustomerCode))
			//				{
			//					aaoList.add(TempCarrierCustomerCode);
			//
			//					'N9' {
			//						'E128_01' 'AAO'
			//						'E127_02' TempCarrierCustomerCode
			//					}
			//				}
			//			}
			//
			//			if(aaoList)
			//			{
			//				aaoList.clear();
			//			}





			// SCAC
			'N9'{
				'E128_01' 'SCA'
				'E127_02' firstScac
			}

			//Q2
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body.Route?.OceanLeg){
				firstOceanLeg = current_Body.Route.OceanLeg[0]
				lastOceanLeg = current_Body.Route.OceanLeg[-1]
			}
		
			List oceanLegList = []
			for (OceanLeg oceanLeg : current_Body?.Route?.OceanLeg) {
				if (StringUtil.isNotEmpty(oceanLeg?.SVVD?.Loading?.Vessel) || StringUtil.isNotEmpty((oceanLeg?.SVVD?.Loading?.Voyage ?: "") + (oceanLeg?.SVVD?.Loading?.Direction ?:"")) || StringUtil.isNotEmpty(oceanLeg?.SVVD?.Loading?.VesselName)) {
					oceanLegList.add(oceanLeg)
				}
			}
			OceanLeg first_OceanLeg = null
			OceanLeg last_OceanLeg = null
			if (oceanLegList.size() > 0) {
				first_OceanLeg = oceanLegList[0]
				last_OceanLeg = oceanLegList[-1]
			}

			def CSSTDQ2ExtCDE=ctUtil.getCodeConversionbyCovertType('CT','O','CSSTDQ2',vCS1EventFirst5,conn)
			if(CSSTDQ2ExtCDE){
				if (shipDir == 'I') {
					'Q2' {
						if (last_OceanLeg?.SVVD?.Discharge?.LloydsNumber) {
							'E597_01' util?.substring(last_OceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)
						}
						if (last_OceanLeg?.SVVD?.Discharge?.RegistrationCountryCode) {
							'E26_02' last_OceanLeg?.SVVD?.Discharge?.RegistrationCountryCode
						}
						LocDT polDepartDtA = last_OceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
						LocDT polDepartDtE = last_OceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT

						if (last_OceanLeg?.SVVD?.Discharge?.Voyage || last_OceanLeg?.SVVD?.Discharge?.Direction) {
							'E55_09' ((last_OceanLeg?.SVVD?.Discharge?.Voyage ? last_OceanLeg?.SVVD?.Discharge?.Voyage:"") + (last_OceanLeg?.SVVD?.Discharge?.Direction ? last_OceanLeg?.SVVD?.Discharge?.Direction:""))
						}
						if(last_OceanLeg?.SVVD?.Discharge?.LloydsNumber)
						{
							'E897_12' 'L'
						}
						if (last_OceanLeg?.SVVD?.Discharge?.VesselName) {
							'E182_13' util.substring(last_OceanLeg?.SVVD?.Discharge?.VesselName, 1, 28)?.trim()
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
						LocDT polDepartureDtA = last_OceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
						LocDT polDepartureDtE = last_OceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT


						if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
							'E55_09' ((firstOceanLeg?.SVVD?.Loading?.Voyage ? firstOceanLeg?.SVVD?.Loading?.Voyage:"") + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction:""))
						}
						if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber)
						{
							'E897_12' 'L'
						}
						if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
							'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName, 1, 28)?.trim()
						}
					}

				}
			}

			// R4 copy from BCTTECH
			//Loop R4
			POR POR =  current_Body.Route?.POR
			FirstPOL firstPOL =  current_Body.Route?.FirstPOL
			LastPOD lastPOD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND
			//POR
			// when R401-115 and (R403-310 or R404-114) is not empty, output R4 R
			def R403 = null
			def R404 = null
			if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
				 R403 = POR?.CityDetails?.LocationCode?.UNLocationCode?.trim()
			} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
				 R403 = POR?.CityDetails?.LocationCode?.SchedKDCode?.trim()
			} 
			if (POR?.CityDetails?.City) {
				R404 = util?.substring(POR?.CityDetails?.City, 1, 24)?.trim()
			}
			
			if(POR && (R403 || R404)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'R'
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							if (POR?.CityDetails?.LocationCode?.SchedKDType) {
								'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType?.trim()
							}
						}
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode?.trim()
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.SchedKDCode?.trim()
						}
						if (POR?.CityDetails?.City) {
							'E114_04' util?.substring(POR?.CityDetails?.City, 1, 24)?.trim()
						}
						if (POR?.CSStandardCity?.CSCountryCode && POR?.CSStandardCity?.CSCountryCode?.length() <= 2){
							'E26_05' POR?.CSStandardCity?.CSCountryCode?.trim()
						}
						if (POR?.CSStandardCity?.CSStateCode) {
							'E156_08' util.substring(POR?.CSStandardCity?.CSStateCode?.trim(), 1, 2).trim()
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
			
			//POL
			R403 = null
			R404 = null
			if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
				R403 =  firstPOL?.Port?.LocationCode?.UNLocationCode?.trim()
			} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
				R403 = firstPOL?.Port?.LocationCode?.SchedKDCode?.trim()
			}
			if (firstPOL?.Port?.PortName) {
				R404 = util.substring(firstPOL?.Port?.PortName, 1, 24)?.trim()
			} else if (firstPOL?.Port?.City) {
				R404 = util.substring(firstPOL?.Port?.City, 1, 24)?.trim()
			}
			if(firstPOL && (R403 || R404)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'L'
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {							
							if (firstPOL?.Port?.LocationCode?.SchedKDType) {
								'E309_02' firstPOL?.Port?.LocationCode?.SchedKDType?.trim()
							}
						}
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.UNLocationCode?.trim()
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.SchedKDCode?.trim()
						}
						if (firstPOL?.Port?.PortName) {
							'E114_04'  util.substring(firstPOL?.Port?.PortName, 1, 24)?.trim()
						} else if (firstPOL?.Port?.City) {
							'E114_04'  util.substring(firstPOL?.Port?.City, 1, 24)?.trim()
						}

						if (firstPOL?.Port?.CSCountryCode && firstPOL?.Port?.CSCountryCode?.length() <= 2) {
							'E26_05' firstPOL?.Port?.CSCountryCode?.trim()
						}
						if (firstPOL?.CSStateCode) {
							'E156_08'  util.substring(firstPOL?.CSStateCode?.trim(), 1, 2).trim()
						}
					}

					LocDT locActDTM = null
					LocDT locEstDTM = null
					def isAct = false
					def isEst = false

					if (current_Body.Route?.OceanLeg ) {
						first_OceanLeg = current_Body.Route?.OceanLeg[0]
					}

					if(first_OceanLeg?.POL?.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT){
						locActDTM = first_OceanLeg?.POL.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT
						isAct = true
					}
					if(first_OceanLeg?.POL?.DepartureDT.find{it.attr_Indicator == 'E'}?.LocDT){
						locEstDTM = first_OceanLeg?.POL.DepartureDT.find{it.attr_Indicator == 'E'}?.LocDT
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
			}

			//POD
			R403 = null
			R404 = null
			if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
				R403 = lastPOD?.Port?.LocationCode?.UNLocationCode
			}else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
				R403 = lastPOD?.Port?.LocationCode?.SchedKDCode
			}
			if (lastPOD?.Port?.PortName) {
				R404 = util?.substring(lastPOD?.Port?.PortName, 1, 24)?.trim()
			}else if(!lastPOD?.Port?.PortName && lastPOD?.Port?.City){
				R404 = util?.substring(lastPOD?.Port?.City, 1, 24)?.trim()
			}
			if(lastPOD && (R403 || R404)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'D'
						if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
							if (lastPOD?.Port?.LocationCode?.SchedKDType) {
								'E309_02' lastPOD?.Port?.LocationCode?.SchedKDType
							}
						}
						if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' lastPOD?.Port?.LocationCode?.UNLocationCode
						}else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' lastPOD?.Port?.LocationCode?.SchedKDCode
						}
						if (lastPOD?.Port?.PortName) {
							'E114_04' util?.substring(lastPOD?.Port?.PortName, 1, 24)?.trim()
						}else if(!lastPOD?.Port?.PortName && lastPOD?.Port?.City){
							'E114_04' util?.substring(lastPOD?.Port?.City, 1, 24)?.trim()
						}
						if (lastPOD?.Port?.CSCountryCode && lastPOD?.Port?.CSCountryCode?.length() <= 2){
							'E26_05' lastPOD?.Port?.CSCountryCode
						}
						if (lastPOD?.CSStateCode) {
							'E156_08' util.substring(lastPOD?.CSStateCode?.trim(), 1, 2).trim() 
						}
					}

					oceanLegList = current_Body?.Route?.OceanLeg.findAll{! (StringUtil.isEmpty(it?.SVVD?.Loading?.Vessel) && StringUtil.isEmpty(it?.SVVD?.Loading?.Voyage) && StringUtil.isEmpty(it?.SVVD?.Loading?.VesselName)) }
					if (oceanLegList.size() > 0) {
						last_OceanLeg = oceanLegList[-1]
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
			}

			//FND
			R403 = null
			R404 = null
			if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
				R403 = FND?.CityDetails?.LocationCode?.UNLocationCode
			}else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
				R403 = FND?.CityDetails?.LocationCode?.SchedKDCode
			}
			if(FND?.CityDetails?.City){
				R404 = util.substring(FND?.CityDetails?.City,1,24)?.trim()
			}
			if(FND && (R403 || R404)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {							
							if (FND?.CityDetails?.LocationCode?.SchedKDType) {
								'E309_02' FND?.CityDetails?.LocationCode?.SchedKDType
							}
						}
						if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' FND?.CityDetails?.LocationCode?.UNLocationCode
						}else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' FND?.CityDetails?.LocationCode?.SchedKDCode
						}
						if(FND?.CityDetails?.City){
							'E114_04' util.substring(FND?.CityDetails?.City,1,24)?.trim()
						}
						if (FND?.CSStandardCity?.CSCountryCode?.length() <= 2){
							if (StringUtil.isNotEmpty(FND?.CSStandardCity?.CSCountryCode)) {
								'E26_05' FND?.CSStandardCity?.CSCountryCode
							}
						}
						if (FND?.CSStandardCity?.CSStateCode) {
							'E156_08' util.substring(FND?.CSStandardCity?.CSStateCode?.trim(), 1, 2).trim() 
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
					def estArrivalDate = null
					if (current_Body?.Route?.OceanLeg.size() > 0)
						estArrivalDate = current_Body?.Route?.OceanLeg[-1]?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
//					println 'estArrivalDate  \t\t\t' + estArrivalDate
					//9
					def actArrivalDate = null
					if (current_Body?.Route?.OceanLeg.size() > 0)
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

		//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		//		List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		
		// Modified by Louis , global variable : firstScac
		if (ct.Body?.size() > 0) {
			firstScac = ct.Body[0].GeneralInformation?.SCAC
		}
		
		//start body looping
		//		bodies.eachWithIndex { current_Body, current_BodyIndex ->
		ct.Body.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('AHM', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		// for this TP=AHM, it has four CSUpload cases, as below
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
		
		//B4 ext_cde == XX    pass ''
		eventNotSub(eventCS2Cde, eventExtCde, false, '', errorKeyList)
		
		// - Missing Booking Number
		//ctUtil.missingBookingNumber(eventCS2Cde, current_Body.BookingGeneralInfo,true, null, errorKeyList)
		// - Missing Container Number
		//ctUtil.missingContainerNumber(eventCS2Cde, current_Body.Container, true, null, errorKeyList)

		//for this TP=AHM, no seg_num='ALLOW_CS260_INTERMODAL' in DB, but exist in tibco, so CSUpload "filterIBIntermodal"
		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route?.Inbound_intermodal_indicator, false, null, errorKeyList)
		//FND - Non US/CA Inbound 
		nonUSCAInboundShipments(eventCS2Cde,current_Body?.Route?.FND,false, null,errorKeyList)

		return errorKeyList;
	}

	private static final String YES = 'YES'
	private static final String NO = 'NO'
	private static final String ERROR_SUPPORT = 'ES'
	private static final String ERROR_COMPLETE = 'EC'

	void nonUSCAInboundShipments(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.FND fnd, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (fnd?.CSStandardCity?.CSCountryCode!='US'&&fnd?.CSStandardCity?.CSCountryCode!='CA') {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg: eventCS2Cde + '- Non US/CA Inbound Shipments']
			errorKeyList.add(errorKey)
		}
	}


	void eventNotSub(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (eventExtCde=="XX") {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg: eventCS2Cde + ' - EVT_NOT_SUB']
			errorKeyList.add(errorKey)
		}
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
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
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

	public String getLocationListWithoutStateCode(String CITY_NME, String CNTRY_CDE,Connection conn,String[] LocationList) throws Exception {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		//String sql = "select distinct UN_LOCN_CDE, SCHED_TYPE, SCHED_KD_CDE ,CNTRY_CDE from CSS_CITY_LIST where CITY_NME=? and cntry_cde = '${CNTRY_CDE}' ";
		String sql = "select distinct UN_LOCN_CDE, SCHED_TYPE, SCHED_KD_CDE  from CSS_CITY_LIST where CITY_NME=? and cntry_cde = '${CNTRY_CDE}' ";
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
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

	
}






