package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.bean.bl.Cargo;
import cs.b2b.core.mapping.bean.bl.Container
import cs.b2b.core.mapping.bean.bl.FND
import cs.b2b.core.mapping.bean.bl.FirstPOL
import cs.b2b.core.mapping.bean.bl.FreightCharge
import cs.b2b.core.mapping.bean.bl.FreightChargeCNTR;
import cs.b2b.core.mapping.bean.bl.LastPOD
import cs.b2b.core.mapping.bean.bl.OceanLeg
import cs.b2b.core.mapping.bean.bl.POR
import cs.b2b.core.mapping.bean.bl.Party
import cs.b2b.core.mapping.util.XmlBeanParser


/**
* @author ZHONGWE
* @pattern after TP=ONMUSBANK
*/
public class CUS_CS2BLXML_310_POLOUSBANK {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_BL_O_Common blUtil = new cs.b2b.core.mapping.util.MappingUtil_BL_O_Common(util);

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

	BigDecimal sum = null
	BigDecimal hundred = new BigDecimal (100);
	BigDecimal thousand = new BigDecimal (1000);
	public void generateBody(Body current_Body, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml, Connection conn) {
		
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '310'
				'E329_02' '-999'
			}
			///////////////////   B3-start
			'B3' {
				if(current_Body?.GeneralInformation?.BLType?.toUpperCase() == 'SEA WAYBILL'){
					'E147_01' '2'
				}else{
					'E147_01' 'B'
				}
				
				if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLNumber)){
					'E76_02' current_Body?.GeneralInformation?.BLNumber
				}
				if(current_Body?.GeneralInformation?.BLNumber){
					'E145_03' current_Body?.GeneralInformation?.SCACCode?.concat(current_Body?.GeneralInformation?.BLNumber)
				}
				
				
				def countOfPrepaidCharge = filteredFreightCharge?.findAll{it?.ChargeType==blUtil.PREPAID}?.size()
				def countOfCollectCharge = filteredFreightCharge?.findAll{it?.ChargeType==blUtil.COLLECT}?.size()
				
				if(countOfPrepaidCharge == 0 && countOfCollectCharge > 0){
					'E146_04' 'CC'
				}else if(countOfPrepaidCharge > 0 && countOfCollectCharge == 0){
					'E146_04' 'PP'
				}else if(countOfPrepaidCharge >0 && countOfCollectCharge>0){
					'E146_04' 'MX'
				}
				
				SimpleDateFormat soutfmt = new SimpleDateFormat(xmlDateTimeFormat)
				
				'E373_06' util.convertDateTime(soutfmt.format(new Date()), xmlDateTimeFormat, yyyyMMdd)
				
				sum = new BigDecimal(0);
				filteredFreightCharge?.each{current_FreightCharge ->
					if(current_FreightCharge?.TotalAmtInPmtCurrency){
						sum = sum + new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString())
					}
				}
				if(sum>=0){
					'E193_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString()
				}else{
					'E193_07' '0'
				}
				if(current_Body?.EventInformation?.EventDescription?.toUpperCase() == 'UPDATE'){
					'E202_08' 'CO'
				}else{
					'E202_08' ''
				}
				
				def alocDT = null
				def elocDT = null
				if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT){
					'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '140'
				}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
					'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '139'
				}
				
				'E140_11' current_Body?.GeneralInformation?.SCACCode
				if(current_Body?.GeneralInformation?.BLIssueDT?.LocDT){
					'E373_12' util.convertDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
				}
				if(current_Body?.Route?.Haulage?.InBound && current_Body?.Route?.Haulage?.OutBound){
					if(current_Body?.Route?.Haulage?.OutBound == 'C' && current_Body?.Route?.Haulage?.InBound == 'C'){
						'E375_13' 'DD'
					}else if(current_Body?.Route?.Haulage?.OutBound == 'C' && current_Body?.Route?.Haulage?.InBound == 'M'){
						'E375_13' 'DP'
					}else if(current_Body?.Route?.Haulage?.OutBound == 'M' && current_Body?.Route?.Haulage?.InBound == 'M'){
						'E375_13' 'PP'
					}else if(current_Body?.Route?.Haulage?.OutBound == 'M' && current_Body?.Route?.Haulage?.InBound == 'C'){
						'E375_13' 'PD'
					}
				}
				
			}
			
			///////////////////   B2A-start
			Map<String, String> eventDescriptionMap = ['NEW':'00','UPDATE':'04']
			'B2A' {
				if(eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)){
					'E353_01' eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)
				}else{
					'E353_01' '01'
				}
			}
			
			
			///////////////////   N9-start
			//Loop by BookingNumber
			def N9PositionIndex = 0
			current_Body?.GeneralInformation?.CarrierBookingNumber?.each{current_BookingNumber ->
				if(N9PositionIndex < 15){
					N9PositionIndex = N9PositionIndex + 1
					'N9' {
						'E128_01' 'BN'
						'E127_02' current_BookingNumber
					}
				}
			}
			
			//Loop by BLNum ONCE
			if(N9PositionIndex < 15){
				N9PositionIndex = N9PositionIndex + 1
				'N9' {
					'E128_01' 'BM'
					'E127_02' current_Body?.GeneralInformation?.BLNumber
							
				}
			}
			
			//Loop by Body/CarrierRate
			def vCarrRatetTy= null
			def vItemCde = null
			Map<String, String> referenceCodeCS2Map = null
			
			def systemRef = current_Body?.ExternalReference?.find{it?.ReferenceDescription=='System Reference Number'}?.ReferenceNumber
			
			if(referenceCodeCS2Map != null){
				current_Body?.CarrierRate?.each{current_CarrierRate ->
				
						if(current_CarrierRate?.CSCarrierRateType ){
							// get the current looping type
							vCarrRatetTy = current_CarrierRate?.CSCarrierRateType
						}else{
							vCarrRatetTy = null
						}
						if (vCarrRatetTy){
							vItemCde = referenceCodeCS2Map.get(vCarrRatetTy)
						}else{
							vItemCde = null
						}
						if(vItemCde){
							if(N9PositionIndex < 15){
								N9PositionIndex = N9PositionIndex + 1
								'N9' {
									'E128_01' vItemCde
									'E127_02' current_CarrierRate.CarrierRateNumber
								}
							}
						}
				}
				
				//Loop by Body/ExternalReference, split per 30 char
				current_Body?.ExternalReference?.each{current_ExternalReference ->
					
						if(current_ExternalReference?.CSReferenceType ){
							// get the current looping type
							vCarrRatetTy = current_ExternalReference?.CSReferenceType
						}
						if (vCarrRatetTy){
							vItemCde = referenceCodeCS2Map.get(vCarrRatetTy)
						}
						if(vItemCde ){
							if(N9PositionIndex < 15){
								N9PositionIndex = N9PositionIndex + 1
								'N9' {
									'E128_01' vItemCde
									'E127_02' current_ExternalReference.ReferenceNumber
								}
							}
						}
				}
			}
			
			///////////////////   V1-start
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body?.Route?.OceanLeg?.size() > 0){
				firstOceanLeg = current_Body?.Route?.OceanLeg[0]
				List<OceanLeg> OceanLegs = new ArrayList<OceanLeg>()
				current_Body?.Route?.OceanLeg?.eachWithIndex{current_OceanLeg, legIndex->
					if(StringUtil.isNotEmpty(current_OceanLeg?.SVVD?.Discharge?.VesselName) && legIndex > 0){
						OceanLegs.add(current_OceanLeg)
					}
				}
				if(current_Body?.Route?.OceanLeg?.size() > 1 && OceanLegs.size() > 0 ){
					lastOceanLeg = OceanLegs[-1]
				}
			}
			if(firstOceanLeg){
				'V1' {
					if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber && firstOceanLeg?.SVVD?.Loading?.LloydsNumber?.length()<=8){
						'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber,1,8)
					}
					if(firstOceanLeg?.SVVD?.Loading?.VesselName){
						'E182_02' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName,1,28)
					}
					
					if(firstOceanLeg?.SVVD?.Loading?.Voyage && firstOceanLeg?.SVVD?.Loading?.Direction){
						if(firstOceanLeg?.LoadingDirectionName){
							'E55_04' firstOceanLeg?.SVVD?.Loading?.Voyage  + firstOceanLeg?.LoadingDirectionName
						}else{
							'E55_04' firstOceanLeg?.SVVD?.Loading?.Voyage
						}
					}
					'E140_05' current_Body?.GeneralInformation.SCACCode
					if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber && firstOceanLeg?.SVVD?.Loading?.LloydsNumber?.length()<=8){
						'E897_08' 'L'
					}else if(firstOceanLeg?.SVVD?.Loading?.CallSign && firstOceanLeg?.SVVD?.Loading?.CallSign?.length()<=8
							 && (!firstOceanLeg?.SVVD?.Loading?.LloydsNumber || StringUtil.isEmpty(firstOceanLeg?.SVVD?.Loading?.LloydsNumber))){
						'E897_08' 'C'
					}
				}
			}
			if(lastOceanLeg){
				'V1' {
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber && lastOceanLeg?.SVVD?.Discharge?.LloydsNumber?.length() <= 8){
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber,1,8)
					}
					if(lastOceanLeg?.SVVD?.Discharge?.VesselName){
						'E182_02' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName,1,28)
					}
					
					if(lastOceanLeg?.SVVD?.Discharge?.Voyage && lastOceanLeg?.SVVD?.Discharge?.Direction){
						if(lastOceanLeg?.DischargeDirectionName){
							'E55_04' lastOceanLeg?.SVVD?.Discharge?.Voyage + lastOceanLeg?.DischargeDirectionName
						}else{
							'E55_04' lastOceanLeg?.SVVD?.Discharge?.Voyage
						}
						
					}
					'E140_05' current_Body?.GeneralInformation.SCACCode
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber && lastOceanLeg?.SVVD?.Discharge?.LloydsNumber?.length() <= 8){
						'E897_08' 'L'
					}else if(lastOceanLeg?.SVVD?.Discharge?.CallSign && lastOceanLeg?.SVVD?.Discharge?.CallSign?.length() <= 8
							&& (!lastOceanLeg?.SVVD?.Discharge?.LloydsNumber || StringUtil.isEmpty(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber))){
						'E897_08' 'C'
					}
				}
			}
			
			///////////////////   C3-start
			if(filteredFreightCharge[0]?.TotalAmtInPmtCurrency?.attr_Currency){
				'C3'{
					'E100_01' filteredFreightCharge[0]?.TotalAmtInPmtCurrency?.attr_Currency
				}
			}
			
			///////////////////   N1-start
			'Loop_N1' {    //N1 LoopPartyPOLOUSBANK
				'N1' {
					'E98_01' 'SE'
					'E93_02' 'ORIENTOVERSEASCONTAINERLINELTD'
					'E66_03' '2'
					'E67_04' 'OOLU'
					
				}
			}
			
			'Loop_N1' {    //N1 LoopPartyPOLOUSBANK
				'N1' {
					'E98_01' 'BY'
					'E93_02' 'Polo Ralph Lauren'
					'E66_03' '92'
					'E67_04' 'PRLUSD'
					
				}
			}
			
			Map<String, String> partyTypeMap = ['CGN':'ST','SHP':'SF']
			current_Body?.Party?.each{current_Party ->
				if(partyTypeMap.get(current_Party?.PartyType)){
					'Loop_N1' {
						'N1' {
							'E98_01' partyTypeMap.get(current_Party?.PartyType)
							'E93_02' util.substring(current_Party?.PartyName,1, 60)
						}
						
						def vAddressLines = ''
						current_Party?.Address?.AddressLines?.AddressLine?.each{current_addressLine ->
							vAddressLines = vAddressLines + ' ' + current_addressLine
						}
						'N3' {
							if(vAddressLines){
								'E166_01' util.substring(vAddressLines?.trim(), 1, 55)?.trim()
							}
							if(vAddressLines.trim()?.length() >= 56){
								'E166_02' util.substring(vAddressLines?.trim(), 56, 55)?.trim()
							}
						}
						
						'N4' {
							if(current_Party?.PartyType == 'SHP'){
								'E19_01' current_Body?.Route?.POR?.CityDetails?.City?.toUpperCase()
							}else if(current_Party?.PartyType == 'CGN'){
								'E19_01' current_Body?.Route?.FND?.CityDetails?.City?.toUpperCase()
							}else {
								'E19_01' current_Party?.Address?.City
							}
							
							
							def city = current_Party?.Address?.City?.toUpperCase()
							def state = current_Party?.Address?.State?.toUpperCase()
							def country = current_Party?.Address?.Country?.toUpperCase()
							
							if(current_Party?.PartyType == 'SHP'){
								city = current_Body?.Route?.POR?.CityDetails?.City
								state = current_Body?.Route?.POR?.CityDetails?.State
								country = current_Body?.Route?.POR?.CityDetails?.Country
							}else if(current_Party?.PartyType == 'CGN'){
								city = current_Body?.Route?.FND?.CityDetails?.City
								state = current_Body?.Route?.FND?.CityDetails?.State
								country = current_Body?.Route?.FND?.CityDetails?.Country
							}
							
							Map<String, String> vResult = null
							if(current_Party?.Address?.State){
								vResult = util.getCS2MasterCityByStateNameCityNameCntryCde(state, city, country, conn)
							}
							Map<String, String> vResult1 = null
							if(StringUtil.isEmpty(vResult?.get('STATE_CDE')) ){
								vResult1 = util.getCS2MasterCityByStateNameCityName(state, city, conn)
							}
							if(vResult?.get('STATE_CDE')){
								'E156_02' vResult?.get('STATE_CDE')
							}else if(vResult1?.get('STATE_CDE')){
								'E156_02' vResult1?.get('STATE_CDE')
							}else{
								'E156_02' 'ZZ'
							}
							
							if(!['SHP','CGN']?.contains(current_Party?.PartyType)){
								'E116_03' current_Party?.Address?.PostalCode
							}
							
							if(current_Party?.PartyType == 'SHP'){
								'E26_04' current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode
							}else if(current_Party?.PartyType == 'CGN'){
								'E26_04' current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode
							}else{
								'E26_04' current_Party?.Address?.Country
							}
							
							if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode && current_Party?.PartyType == 'SHP'){
								'E309_05' 'UN'
								'E310_06' current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
							}else if(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode && current_Party?.PartyType == 'CGN'){
								'E309_05' 'UN'
								'E310_06' current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
							}else if(current_Party?.Address?.LocationCode?.UNLocationCode && !['SHP','CGN']?.contains(current_Party?.PartyType)){
								'E309_05' 'UN'
								'E310_06' current_Party?.Address?.LocationCode?.UNLocationCode
							}else if(current_Party?.PartyType == 'SHP' && current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDType && current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode){
								'E309_05' current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDType
								'E310_06' current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
							}else if(current_Party?.PartyType == 'CGN' && current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDType && current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode){
								'E309_05' current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDType
								'E310_06' current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
							}else if(!['SHP','CGN']?.contains(current_Party?.PartyType) && current_Party?.Address?.LocationCode?.SchedKDType && current_Party?.Address?.LocationCode?.SchedKDCode){
								'E309_05' current_Party?.Address?.LocationCode?.SchedKDType
								'E310_06' current_Party?.Address?.LocationCode?.UNLocationCode
							}
						}
					}
				}
			}
			
			//R4  -->strat
			POR POR =  current_Body?.Route?.POR
			FirstPOL POL =  current_Body?.Route?.FirstPOL
			LastPOD POD =  current_Body?.Route?.LastPOD
			FND FND =  current_Body?.Route?.FND
			def oceanLegSize= current_Body?.Route?.OceanLeg?.size()
			
			//POR
			'Loop_R4' {
				'R4' {
					'E115_01' 'O'
					if(POR){
						if(POR?.CityDetails?.LocationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode
						}else if(POR?.CityDetails?.LocationCode?.SchedKDCode){
							'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType
							'E310_03' POR?.CityDetails?.LocationCode?.SchedKDCode
						}
						if(POR?.CityDetails?.City){
							'E114_04' util.substring(POR?.CityDetails?.City, 1, 24)?.toUpperCase()
						}
						if(POR?.CSStandardCity?.CSCountryCode){
							'E26_05' POR?.CSStandardCity?.CSCountryCode
						}
						if(POR?.CSStandardCity?.CSStateCode){
							'E156_08' POR?.CSStandardCity?.CSStateCode
						}
					}
				}
				'DTM' {
					if(current_Body?.Route?.FullReturnCutoffDT?.LocDT){
						'E374_01' '140'
					}
					if(current_Body?.Route?.FullReturnCutoffDT?.LocDT){
						'E373_02' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, HHmm)
					}
				}
			}
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if(POL){
						if(POL?.Port?.locationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' POL?.Port?.locationCode?.UNLocationCode
						}else if(POL?.Port?.LocationCode?.SchedKDCode){
							'E309_02' POL?.Port?.LocationCode?.SchedKDType
							'E310_03' POL?.Port?.LocationCode?.SchedKDCode
						}
						if(POL?.Port?.PortName){
							'E114_04' util.substring(POL?.Port?.PortName, 1, 24)?.toUpperCase()
						}else if(POL?.Port?.City){
							'E114_04' util.substring(POL?.Port?.City, 1, 24)?.toUpperCase()
						}

						if(POL?.Port?.CSCountryCode){
							'E26_05' POL?.Port?.CSCountryCode
						}
						if(POL?.CSStateCode){
							'E156_08' POL?.CSStateCode
						}
					}
				}
				if(oceanLegSize > 0){
					'DTM' {
						if(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
			//POD
			
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if(POD){
						if(POD?.Port?.locationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' POD?.Port?.locationCode?.UNLocationCode
						}else if(POD?.Port?.LocationCode?.SchedKDCode){
							'E309_02' POD?.Port?.LocationCode?.SchedKDType
							'E310_03' POD?.Port?.LocationCode?.SchedKDCode
						}
						if(POD?.Port?.PortName){
							'E114_04' util.substring(POD?.Port?.PortName, 1, 24)?.toUpperCase()
						}else if(POD?.Port?.City){
							'E114_04' util.substring(POD?.Port?.City, 1, 24)?.toUpperCase()
						}
						if(POD?.Port?.CSCountryCode){
							'E26_05' POD?.Port?.CSCountryCode
						}
						if(POD?.CSStateCode){
							'E156_08' POD?.CSStateCode
						}
					}
				}
				if(oceanLegSize > 0){
					'DTM' {
						if(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
						}
					}
				}
				
			}
			//FND
			
			'Loop_R4' {
				'R4' {
					'E115_01' 'E'
					if(FND){
						if(FND?.CityDetails?.LocationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' FND?.CityDetails?.LocationCode?.UNLocationCode
						}else if(FND?.CityDetails?.LocationCode?.SchedKDCode){
							'E309_02' FND?.CityDetails?.LocationCode?.SchedKDType
							'E310_03' FND?.CityDetails?.LocationCode?.SchedKDCode
						}
						if(FND?.CityDetails?.City){
							'E114_04' util.substring(FND?.CityDetails?.City, 1, 24)?.toUpperCase()
						}
						if(FND?.CSStandardCity?.CSCountryCode){
							'E26_05' FND?.CSStandardCity?.CSCountryCode
						}
						if(FND?.CSStandardCity?.CSStateCode){
							'E156_08' FND?.CSStandardCity?.CSStateCode
						}
					}
				}
				'DTM' {
					if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator?.equals('A')}?.LocDT){
						'E374_01' '140'
						'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
					}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E374_01' '139'
						'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
					}else if(oceanLegSize > 0 && current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
						'E374_01' '140'
						'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
					}else if(oceanLegSize > 0 && current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E374_01' '139'
						'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
					}
				}
			}
		
			///////////////////   LX-start
			def reIndex = 0   //LoopFreightChargeCNTRRtnLPOLO
			associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
				
				List<FreightChargeCNTR> expectedfilteredFreightChargeCNTR = []
				//to do this in order to sort by the chargeCode       tibco process : LX LoopFreightChargeCNTRRtnLPOLO   ReorderFreightCharge
				filteredFreightChargeCNTR?.findAll{ it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.ChargeCode?.toUpperCase() == 'OCEAN FREIGHT'}?.each{cu_fc->
					expectedfilteredFreightChargeCNTR.add(cu_fc)
				}
				filteredFreightChargeCNTR?.findAll{ it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.ChargeCode?.toUpperCase() == 'OCE'}?.each{cu_fc->
					expectedfilteredFreightChargeCNTR.add(cu_fc)
				}
				filteredFreightChargeCNTR?.findAll{ it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.ChargeCode?.toUpperCase() == 'BUC'}?.each{cu_fc->
					expectedfilteredFreightChargeCNTR.add(cu_fc)
				}
				filteredFreightChargeCNTR?.findAll{ it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && !['OCEAN FREIGHT','OCE','BUC']?.contains(it?.ChargeCode?.toUpperCase())}?.each{cu_fc->
					expectedfilteredFreightChargeCNTR.add(cu_fc)
				}
				
				expectedfilteredFreightChargeCNTR?.eachWithIndex{ current_FreightChargeCNTR , freightChargeIndex ->
					reIndex++
					'Loop_LX' {
						'LX' {
							 'E554_01' reIndex   //LoopFreightChargeCNTRRtnLPOLO
						}
						if(freightChargeIndex==0){   //LoopFreightChargeCNTRRtnLPOLO
							'Loop_N7' { 
								if(util.substring(current_Container?.ContainerNumber, 5, 10)){
									'N7' {
										'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
										'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10)
										
										sum = new BigDecimal(0)
										current_Body?.Cargo?.findAll{ it?.CurrentContainerNumber == current_Container?.ContainerNumber}?.each{ cu_cargo->
											if(cu_cargo?.GrossWeight?.Weight && cu_cargo?.GrossWeight?.Weight!='' && cu_cargo?.GrossWeight?.Weight!='NaN'){
												sum = sum + new BigDecimal(cu_cargo?.GrossWeight?.Weight)
											}
										}
										
										if(sum > 0 && current_Body?.Cargo?.find{it?.CurrentContainerNumber == current_Container?.ContainerNumber}?.GrossWeight?.WeightUnit == 'TON' ){
											'E81_03' blUtil.replaceZeroAfterPoint(new BigDecimal(multiplyWithoutOriginalAccuray(sum?.toString(), '1000', 3)).setScale(2, BigDecimal.ROUND_DOWN)?.toString())
											'E187_04' 'G'
										}else if(sum > 0){
											'E81_03' blUtil.replaceZeroAfterPoint(sum?.setScale(2, BigDecimal.ROUND_DOWN)?.toString())
											'E187_04' 'G'
										}
										
										def varWeight1 = null
										def varWeight2 = null
										if(current_Container?.GrossWeight?.WeightUnit == 'KGS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
											varWeight1 = current_Container?.GrossWeight?.Weight?.trim()
										}else if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight)){
											varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight?.trim()).multiply(thousand).toString()
											if(varWeight1.indexOf('.') > 0){
												varWeight1 = blUtil.replaceZeroAfterPoint(varWeight1)
											}
										}else if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'LBS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight)){
											varWeight2 = current_Container?.GrossWeight?.Weight?.trim()
											if(varWeight2.indexOf('.') > 0){
												varWeight2 = blUtil.replaceZeroAfterPoint(varWeight2)
											}
										}
										
										if(varWeight1){
											'E188_17' 'K'
										}else if(varWeight2){
											'E188_17' 'L'
										}
										
										'E761_18' util.substring(current_Container?.ContainerCheckDigit,1,1)
										
										if(current_Container?.CS1ContainerSizeType || current_FreightChargeCNTR?.CalculateMethod && current_FreightChargeCNTR?.CalculateMethod?.length()<=4 && current_FreightChargeCNTR?.CalculateMethod?.length()>0){
											def fromValue = current_Container?.CS1ContainerSizeType
											if(current_FreightChargeCNTR?.CalculateMethod && current_FreightChargeCNTR?.CalculateMethod?.length()<=4 && current_FreightChargeCNTR?.CalculateMethod?.length()>0){
												fromValue = current_FreightChargeCNTR?.CalculateMethod
											}
											def ExtCDE = util.getConversionWithScac(TP_ID, 'BL', 'O', 'ContainerType', fromValue ,current_Body?.GeneralInformation?.SCACCode, conn)
											if(ExtCDE){
												'E24_22' ExtCDE
											}
										}
									}
									
									if(current_Container?.PieceCount?.PieceCount){
										'QTY' {
											'E673_01' '39'
											if(current_Container?.PieceCount?.PieceCount){
												'E380_02' current_Container?.PieceCount?.PieceCount
											}
											'C001_03' {
												if(current_Container?.PieceCount?.PieceCountUnit){
													'E355_01' current_Container?.PieceCount?.PieceCountUnit?.trim()
												}
											}
										}
									}
									
									current_Container.Seal?.each{current_Seal ->
										if(current_Seal?.SealNumber){
											'M7' {
												'E225_01' current_Seal?.SealNumber?.trim()
												if(current_Seal?.SealType == 'OT'){
													'E98_05' 'ZZ'
												}else{
													'E98_05' current_Seal?.SealType
												}
											}
										}
									}
									
									def w09occurNums = 1
									current_Body?.Cargo?.findAll{it?.CurrentContainerNumber == current_Container?.ContainerNumber && it?.CurrentContainerCheckDigit == current_Container?.ContainerCheckDigit}[0]?.each{ //LoopOnceRtnLONMUSBANK
										if(current_cargoList[0]?.ReeferCargoSpec?.size() > 0 && w09occurNums <=5){
											cs.b2b.core.mapping.bean.bl.ReeferCargoSpec current_ReeferCargoSpec = current_cargoList[0]?.ReeferCargoSpec[current_cargoList[0]?.ReeferCargoSpec?.size() -1]
											w09occurNums++
											'W09' {
												'E40_01' 'CZ'
												
												def temperature = current_ReeferCargoSpec?.Temperature?.Temperature
												def signal = null
												if(temperature?.contains(".")){
													def temperatureAfterPoint = temperature?.substring(temperature?.indexOf(".")+1, temperature?.length())
													if(temperature.startsWith("-")){
														temperature = temperature.substring(temperature?.indexOf("-")+1, temperature.length())
														signal = "-"
													}
													def temperatureBeforePoint = temperature.substring(0,temperature?.indexOf("."))
													def remainTempPoint = 4-temperatureBeforePoint?.length()>temperatureAfterPoint?.length()?temperatureAfterPoint:temperatureAfterPoint?.substring(0,4-temperatureBeforePoint.length())
													if(remainTempPoint !=""){
														if(signal.equals("-")){
															temperatureBeforePoint = "-".concat(temperatureBeforePoint)
														}
														'E408_02' blUtil.replaceZeroAfterPoint(temperatureBeforePoint?.concat(".")?.concat(remainTempPoint))
													}else{
														if(signal.equals("-")){
															temperatureBeforePoint = "-".concat(temperatureBeforePoint)
														}
														'E408_02' temperatureBeforePoint
													}
												}else{
													'E408_02' current_ReeferCargoSpec?.Temperature?.Temperature
												}
												
												if(current_ReeferCargoSpec?.Temperature?.Temperature){
													if(current_ReeferCargoSpec?.Temperature?.TemperatureUnit == 'C'){
														'E355_03' 'CE'
													}else if(current_ReeferCargoSpec?.Temperature?.TemperatureUnit == 'F'){
														'E355_03' 'FA'
													}
													
												}
												Map<String, String> ventilationMap =  ['25': 'A', '50':'B','75' :'C','100':'D','0':'E']
												def ventailation = ventilationMap.get(current_ReeferCargoSpec?.Ventilation?.Ventilation)
												if(current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'DEGREES' && StringUtil.isNotEmpty(ventailation)){
													'E1122_07' ventailation?.trim()
												}
											}
										}
									}
	
								}
							}
						}
					
						
					///////////////////   Loop_L0-start
					current_cargoList?.eachWithIndex{current_Cargo, cargoIndex->
						
							if(cargoIndex == 0){    //LoopFreightChargeCNTRRtnLPOLO
								'Loop_L0' {
									'L0' {
										'E213_01' reIndex   //LoopFreightChargeCNTRRtnLPOLO
										
										def sumMapTotalVolume = new BigDecimal(0)
										current_cargoList?.each{ cargo->
											if(cargo?.Volume?.Volume!="" && cargo?.Volume?.Volume!='NaN' && cargo?.Volume?.VolumeUnit!=null && ['CBF','CFT','CBM']?.contains(cargo?.Volume?.VolumeUnit)){
												if(cargo?.Volume?.Volume){
													sumMapTotalVolume = sumMapTotalVolume + new BigDecimal(cargo?.Volume?.Volume)
												}
											}
										}
										
										if(sumMapTotalVolume?.toString()?.startsWith("0.")){
											'E220_02' blUtil.replaceZeroAfterPoint(sumMapTotalVolume?.toString()?.substring(1,sumMapTotalVolume?.toString()?.length()))
										}else{
											'E220_02' blUtil.replaceZeroAfterPoint(sumMapTotalVolume?.toString())
										}
										
										'E221_03' 'VM'
										
										sum = new BigDecimal(0)
										current_cargoList?.findAll{StringUtil.isNotEmpty(it?.GrossWeight?.Weight) && it?.GrossWeight?.Weight!='NaN'}?.each{ cargo ->
											
											def current_GrossWeight = cargo?.GrossWeight
											
											if(current_GrossWeight?.WeightUnit == 'TON'){
												sum = sum + new BigDecimal(multiplyWithoutOriginalAccuray(current_GrossWeight?.Weight, '1000', 3))
											}else if(['KGS','LBS']?.contains(current_GrossWeight?.WeightUnit)){
												sum = sum + new BigDecimal(current_GrossWeight?.Weight)
											}
										}
										if(sum>0){
											'E81_04' blUtil.replaceZeroAfterPoint(sum?.toString())
										}
										
										if(current_Body?.Cargo?.find{StringUtil.isNotEmpty(it?.GrossWeight?.Weight)} && sum > 0){
											'E187_05' 'G'
										}
										
										if(sumMapTotalVolume?.toString()?.startsWith("0.")){
											'E183_06' blUtil.replaceZeroAfterPoint(sumMapTotalVolume?.toString()?.substring(1,sumMapTotalVolume?.toString()?.length()))
										}else{
											'E183_06' blUtil.replaceZeroAfterPoint(sumMapTotalVolume?.toString())
										}
										
										if(current_cargoList?.find{StringUtil.isNotEmpty(it?.Volume?.Volume)}){
											if(current_cargoList?.find{['CBF','CFT']?.contains(it?.Volume?.VolumeUnit)}){
												'E184_07' 'E'
											}else if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CBM' }){
												'E184_07' 'X'
											}
										}
										
										if(StringUtil.isNotEmpty(current_Cargo?.Packaging?.PackageQty)){
											sum = new BigDecimal(0)
											current_cargoList?.each{ cargo ->
												
												if(cargo?.Packaging?.PackageQty){
													sum = sum + new BigDecimal(cargo?.Packaging?.PackageQty)
												}
											}
											'E80_08' sum?.toString()
										}
										
										def excdeByPackageType = util.getConversion(TP_ID, 'BL', 'O', 'PackageUnit', current_Cargo?.Packaging?.PackageType, conn)
										if(StringUtil.isNotEmpty(current_Cargo?.Packaging?.PackageType) && StringUtil.isNotEmpty(current_Cargo?.Packaging?.PackageQty) && excdeByPackageType !=""){
											'E211_09' excdeByPackageType
										}else{
											'E211_09' 'UNT'
										}
										
										def groWeigthKGS = null
										def groWeigthLBS = null
										def groWeigthTON = null
										
										if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'KGS'){
											groWeigthKGS =  current_Cargo?.GrossWeight?.Weight?.trim()
											if(groWeigthKGS?.indexOf('.') > 0){
												groWeigthKGS = blUtil.replaceZeroAfterPoint(groWeigthKGS)
											}
										}
										if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'LBS'){
											groWeigthLBS =  current_Cargo?.GrossWeight?.Weight?.trim()
											if(groWeigthLBS?.indexOf('.') > 0){
												groWeigthLBS = blUtil.replaceZeroAfterPoint(groWeigthLBS)
											}
										}
										if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'TON'){
											groWeigthTON = new BigDecimal(current_Cargo?.GrossWeight?.Weight).multiply(thousand).toString()
											if(groWeigthTON?.indexOf('.') > 0){
												groWeigthTON = blUtil.replaceZeroAfterPoint(groWeigthTON)
											}
										}
										if(groWeigthKGS){
											'E188_11' 'K'
										}else if(groWeigthLBS){
											'E188_11' 'L'
										}else if(groWeigthTON){
											'E188_11' 'K'
										}
									}
									
									//split current_Cargo?.CargoDescription
									
									def StringLine = current_Cargo?.CargoDescription?.trim()
									double count = (StringLine.length() / 50.0);
									if( ! (count + "").endsWith(".0")){
										count = count + 1;
									}
									int numberOfLine = (int)count;
									String originalStr = StringLine;
									def StringLineInLength50 = new String[numberOfLine];
									
									for(int i = 0; i < numberOfLine; i++){
										//Cut for each 55
										if(originalStr.length() > 50){
											 StringLineInLength50[i] = originalStr.substring(0, 50);
										 originalStr = originalStr.substring(50);
										}else{
											 StringLineInLength50[i] = originalStr.substring(0, originalStr.length());
											 originalStr = originalStr.substring(originalStr.length());
										}
									
									}
									List<String> cargoDescription = []
									cargoDescription.add(StringLineInLength50[0])
									
									List<cs.b2b.core.mapping.bean.MarksAndNumbers>  MarksAndNumbersList = current_Cargo?.MarksAndNumbers
									def numOfLoop = null
									if(cargoDescription.size() > 0){
										numOfLoop = cargoDescription.size() + 1
		
									}else{
										numOfLoop = MarksAndNumbersList.size()
									}
									for(int i = 0; i < numOfLoop; i++){
										if(cargoDescription[i]==null){
											cargoDescription[i] = "CONTAINER#${current_Cargo?.CurrentContainerNumber?.trim()}${current_Cargo?.CurrentContainerCheckDigit?.trim()}"
										}
										if(reIndex == 1){  //Customization	Customization POLOUSBANK_Reording   Loop_L5
											if(StringUtil.isNotEmpty(cargoDescription[i]) && (cargoDescription[i]?.contains("CONTAINER#") || Container_index==0) ){
												'L5' {
													if(reIndex == 1){   //LoopFreightChargeCNTRRtnLPOLO
														if(Container_index == 0){ //LoopByCargoDescOnly
															'E213_01' i + 1
															}else{
															'E213_01' Container_index + 1
														}
													}else{
														'E213_01' reIndex
													}
													'E79_02' util.substring(cargoDescription[i],1,50)
												}
											}
										}else{   //Customization	Customization POLOUSBANK_Reording  Loop_L5
											if(StringUtil.isNotEmpty(cargoDescription[i]) && (cargoDescription[i]?.contains("CONTAINER#") || Container_index==0) ){
												if( i == numOfLoop-1){
													'L5' {
														if(reIndex == 1){  //LoopFreightChargeCNTRRtnLPOLO
															if(Container_index == 0){   //LoopByCargoDescOnly
																'E213_01' i + 1
															}else{
																'E213_01' Container_index + 1
															}
														}else{
															'E213_01' reIndex
														}
														'E79_02' util.substring(cargoDescription[i],1,50)
													}
												}
											}
										}
		
									}
									
									
									// LX-L0-L1 start
									List<FreightChargeCNTR> filterFreightChargeCNTRs = current_FreightChargeCNTR?.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit}
									filterFreightChargeCNTRs?.eachWithIndex{ freightChargeCNTR, current_index ->
											if(freightChargeCNTR?.ChargeType == blUtil.COLLECT || freightChargeCNTR?.ChargeType == blUtil.PREPAID){
												if(freightChargeCNTR?.TotalAmtInPmtCurrency){
													'Loop_L1'{
														'L1'{
															'E213_01' reIndex  //LoopFreightChargeCNTRRtnLPOLO
															'E60_02' freightChargeCNTR?.TotalAmtInPmtCurrency?.toString()
															
															def calculateMethod = freightChargeCNTR?.CalculateMethod?.toUpperCase()
															List<String> otherVar = ['PA','LS','VM','WM','PU','CS']
															if(calculateMethod!=null&&calculateMethod!=""){
																if(calculateMethod?.toUpperCase() == 'CONTAINER' || calculateMethod.charAt(0) == '2' || calculateMethod.charAt(0) == '4'){
																	'E122_03' 'PA'
																}else if(calculateMethod?.toUpperCase() == 'GROSS CARGO WEIGHT'){
																	'E122_03' 'WM'
																}else if(calculateMethod?.toUpperCase() == 'NET CARGO WEIGHT'){
																	'E122_03' 'WM'
																}else if(calculateMethod?.toUpperCase() == 'LUMP SUM'){
																	'E122_03' 'LS'
																}else if(calculateMethod?.toUpperCase() == 'MEASUREMENT'){
																	'E122_03' 'VM'
																}else if(calculateMethod?.toUpperCase() == 'PACKAGE'){
																	'E122_03' 'PU'
																}else if(calculateMethod.charAt(0) == 'B'){
																	'E122_03' 'CS'
																}else if(calculateMethod?.trim() == 'PA'){
																	'E122_03' calculateMethod
																}else if(otherVar.contains(calculateMethod?.trim())){
																	'E122_03' calculateMethod
																}
															}
															
															if(freightChargeCNTR?.ChargeType == blUtil.COLLECT){
																if(freightChargeCNTR?.TotalAmtInPmtCurrency){
																	'E58_04' blUtil.replaceZeroAfterPoint(new BigDecimal(freightChargeCNTR?.TotalAmtInPmtCurrency?.toString()).multiply(hundred).toString())
																}
															}
															
															if(freightChargeCNTR?.ChargeType == blUtil.PREPAID){
																if(freightChargeCNTR?.TotalAmtInPmtCurrency){
																	'E117_06' multiplyWithoutOriginalAccuray(freightChargeCNTR?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency, "100", 2)
																}
															}
															
															Map<String, String> chargeTypeExtCDE = blUtil.getB2BCdeConversion(freightChargeCNTR?.ChargeCode, TP_ID, 'ChargeType', conn)
															if(StringUtil.isNotEmpty(freightChargeCNTR?.ChargeCode) && StringUtil.isNotEmpty(chargeTypeExtCDE.get("EXT_CDE")) ){
																'E150_08' chargeTypeExtCDE.get("EXT_CDE")
															}else{
																'E150_08' freightChargeCNTR?.ChargeCode
															}
															
															if(freightChargeCNTR?.ChargeType == blUtil.COLLECT){
																'E16_11' 'C'
															}else{
																'E16_11' 'P'
															}
															
															chargeTypeExtCDE = blUtil.getB2BCdeConversion(util.substring(freightChargeCNTR?.ChargeCode,1,3)?.toUpperCase(), TP_ID, 'ChargeType', conn)
															if(StringUtil.isNotEmpty(freightChargeCNTR?.ChargeCode) && StringUtil.isNotEmpty(chargeTypeExtCDE.get("REMARKS")) ){
																'E276_12' util.substring(chargeTypeExtCDE.get("REMARKS"),1,25)
															}else if(StringUtil.isNotEmpty(freightChargeCNTR?.ChargeDesc)){
																'E276_12' util.substring(freightChargeCNTR?.ChargeDesc?.trim(),1,25)
															}
															
															'E220_17' '1'
															'E221_18' 'NR'
														}
													}
												}
											}
									}
								}
							}
						}
						
					}
					
				}
			}
			
			///////////////////   L3-start
			'L3' {
				if(util.isDecimal(current_Body?.GeneralInformation?.BLGrossWeight?.Weight) && current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.toBigDecimal() > 0){
					def vWeight = current_Body?.GeneralInformation?.BLGrossWeight?.Weight
					if(current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.indexOf('.') > 0){
						  vWeight = blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLGrossWeight?.Weight)
					}
					'E81_01'  util.substring(vWeight,1,10)
					'E187_02' 'G'
				}else if(util.isDecimal(current_Body?.GeneralInformation?.BLNetWeight?.Weight) && current_Body?.GeneralInformation?.BLNetWeight?.Weight?.toBigDecimal() > 0){
					def vWeight = current_Body?.GeneralInformation?.BLNetWeight?.Weight
					if(current_Body?.GeneralInformation?.BLNetWeight?.Weight?.indexOf('.') > 0){
						  vWeight = blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLNetWeight?.Weight)
					}
					'E81_01'  util.substring(vWeight,1,10)
					'E187_02' 'N'
				}
				
				if( current_Body?.GeneralInformation?.BLVolume?.Volume && new BigDecimal(current_Body?.GeneralInformation?.BLVolume?.Volume) > 0){
					'E183_09' current_Body?.GeneralInformation?.BLVolume?.Volume
					if(current_Body?.GeneralInformation?.BLVolume?.VolumeUnit == 'CBF'){
						'E184_10' 'E'
					}else if(current_Body?.GeneralInformation?.BLVolume?.VolumeUnit == 'CFT'){
						'E184_10' 'E'
					}else if(current_Body?.GeneralInformation?.BLVolume?.VolumeUnit == 'CBM'){
						'E184_10' 'X'
					}
				}
				
				sum = new BigDecimal(0)
				current_Body?.Cargo?.each{current_Cargo ->
					if(current_Cargo?.Packaging?.PackageQty){
						sum = sum + new BigDecimal(current_Cargo?.Packaging?.PackageQty)
					}
				}
				'E80_11' util.substring(sum.toString(), 1, 7);
				if(util.isDecimal(current_Body?.GeneralInformation?.BLGrossWeight?.Weight) && current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.toBigDecimal() > 0){
					if(current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit == 'TON'){
						'E188_12' 'K'
					}else if(current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit == 'KGS'){
						'E188_12' 'K'
					}else if(current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit == 'LBS'){
						'E188_12' 'L'
					}
				}else if(util.isDecimal(current_Body?.GeneralInformation?.BLNetWeight?.Weight) && current_Body?.GeneralInformation?.BLNetWeight?.Weight?.toBigDecimal() > 0){
					if(current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit == 'TON'){
						'E188_12' 'K'
					}else if(current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit == 'KGS'){
						'E188_12' 'K'
					}else if(current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit == 'LBS'){
						'E188_12' 'L'
					}
				}
				
			}
			
			if(current_Body?.Remarks){
				List<String> Remarks = blUtil.SplitTextWithConnector(current_Body?.Remarks, 30)
				Remarks?.each{current_Remarks ->
					'K1' {
					'E61_01' current_Remarks
					'E61_02' current_Remarks
					}
				}
			}
			
			'SE' {
				'E96_01' '-999'
				'E329_02' '-999'
			}
		}



	}


	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn)  {
			
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

		//Parse the xmlBody to JavaBean
		XmlBeanParser parser = new XmlBeanParser()
		BillOfLading bl = parser.xmlParser(inputXmlBody, BillOfLading.class)
		
		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
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
		def T310 = outXml.createNode('T310')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.

		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()
		def headerMsgDT = util.convertDateTime(bl.Header?.MsgDT?.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		def txnErrorKeys = []

		//Start mapping
		
		bl?.Body?.eachWithIndex{current_Body, current_BodyIndex ->
			
			List<FreightCharge> filteredFreightCharge = current_Body.FreightCharge?.clone()
			List<FreightChargeCNTR> filteredFreightChargeCNTR = current_Body.FreightChargeCNTR?.clone()
			
			//OceanLegReorder
			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)
			
			//filterFreightCharge
			blUtil.filterByChargeType(filteredFreightCharge, filteredFreightChargeCNTR, blUtil.PREPAID)
			List<String> expectedCalculateList = getContainerTypeByTP_ID(TP_ID, conn)
			
			blUtil.filterByContainerType(filteredFreightCharge, filteredFreightChargeCNTR, expectedCalculateList)
			
			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)
			
			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex , expectedCalculateList)
			//mapping
			generateBody(current_Body,  associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml ,conn)
			
			//posp checking
			if(errorKeyList.isEmpty()){
				pospValidation(writer?.toString(), errorKeyList, current_Body )
			}
			
			//EDI Syntax checking
			if(errorKeyList.isEmpty()){
				syntaxValidation(writer?.toString(), errorKeyList)
			}
			
			blUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%18s', current_Body?.TransactionInformation?.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			blUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, bl.Header?.MsgDT?.GMT, bl.Header.InterchangeMessageID, TP_ID, conn)
			txnErrorKeys.add(errorKeyList)
		}


		//End root node
		outXml.nodeCompleted(null,T310)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		blUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		blUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		blUtil.promoteHeaderIntChgMsgId(appSessionId, bl?.Body[0]);
		blUtil.promoteScacCode(appSessionId, bl?.Body[0]);
		String result = '';
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			result = util.cleanXml(writer?.toString());
		}

		writer.close();
		return result;
	}

	//POLOUSBANK
	
	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex , List<String> expectedCalculateList){
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
		blUtil.cC_Not_USD(current_Body, true, "Collect charges currency not USD", errorKeyList)
		blUtil.bLReadyOnly(current_Body, false, "Status is not BL-Ready", errorKeyList)
		blUtil.checkSealNumberLength(current_Body, 15, true, null, errorKeyList)
		blUtil.invalidMultiCargoVolUnit(current_Body, true, null, errorKeyList)
		blUtil.invalidMultiCargoWeightUnit(current_Body, true, null, errorKeyList)
		blUtil.noL3(true, null, errorKeyList, current_Body)
		blUtil.MissingL3_11_80(current_Body, true, null, errorKeyList)
		blUtil.missingChargeByContainerTypePOLOUSBANK(current_Body, false, null, errorKeyList, expectedCalculateList, blUtil.COLLECT)  
		
		return errorKeyList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList , cs.b2b.core.mapping.bean.bl.Body current_Body){

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")

		blUtil.checkL1max(node, 20, true, "L1 segment more than 20.", errorKeyList)
		blUtil.checkOnlyCollectCharge(node, true, "Only collect charge will be sent", errorKeyList)
		blUtil.checkL0Max(node,120, true, "L0 Max is 120", errorKeyList)
		blUtil.checkL102L106L307Checker(node,true, null, errorKeyList)
		blUtil.checkL106LengthWithPoint(node, 9, true, null, errorKeyList)
	}

	
	void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){
		
		
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")
		
		blUtil.checkB309_B310_P(node, true, null, errorKeyList)
		blUtil.checkC301_M(node, true, null, errorKeyList)
		blUtil.checkW0902_W0903_P(node, true, null, errorKeyList)
		blUtil.checkV101_V102_R(node, true, "At least one of V101, V102 is required.", errorKeyList)
		blUtil.checkQTY01_QTY02_R(node, true, null, errorKeyList)
		blUtil.checkQTY01_M(node, true, null, errorKeyList)
		blUtil.checkN902_N903_R(node, true, null, errorKeyList)
		blUtil.checkN901_N902_M(node, true, null, errorKeyList)
		blUtil.checkN703_N704_P(node, true, null, errorKeyList)
		blUtil.checkN102_N103_R(node, true, null, errorKeyList)
		blUtil.checkN101_N102_M(node, true, null, errorKeyList)
		blUtil.checkL312_L301_C(node, true, null, errorKeyList)
		blUtil.checkL309_L310_P(node, true, null, errorKeyList)
		blUtil.checkL301_L302_P(node, true, null, errorKeyList)
		blUtil.checkC803_C802_R(node, true, null, errorKeyList)
		blUtil.checkDTM02_DTM03_DTM_05_R(node, true, null, errorKeyList)
		blUtil.checkDTM04_DTM03_C(node, true, null, errorKeyList)
		blUtil.checkG6103_G6104_P(node, true, null, errorKeyList)
		blUtil.checkH101_M(node, true, null, errorKeyList)
		blUtil.checkH107_H108_P(node, true, null, errorKeyList)
		blUtil.checkL006_L007_P(node, true, null, errorKeyList)
		
	}
	
	private String multiplyWithoutOriginalAccuray(String multiplyed, String multiplying, Integer oriAccuray){
		def accuray = 0;
		if(multiplyed?.indexOf('.')>0){
			accuray = (multiplyed?.length() - multiplyed?.indexOf('.')-1) - oriAccuray;
		}
		accuray = accuray<0?0:accuray;
		return new BigDecimal(multiplyed).multiply(new BigDecimal(multiplying)).setScale(accuray,BigDecimal.ROUND_HALF_UP).toString()
	}
	
	public List<String> getContainerTypeByTP_ID(String TP_ID,Connection conn) throws Exception {
		if (conn == null)
			return null;

		List<String> retList = new ArrayList<String>();
		if (TP_ID==null) {
			return retList
		}

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select INT_CDE from b2b_cde_conversion c where c.tp_id= ? and DIR_ID = 'O' and MSG_TYPE_ID = 'BL' and c.convert_type_id='ContainerType'";
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);

			result = pre.executeQuery();
			while(result.next()) {
				retList.add(result.getString(1));
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return retList
	}
	
}

