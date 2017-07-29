package cs.b2b.mapping.scripts

import groovy.util.Node;
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.util.List;
import java.util.Map;

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.MarksAndNumbers
import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.bean.bl.Container
import cs.b2b.core.mapping.bean.bl.FND
import cs.b2b.core.mapping.bean.bl.FirstPOL
import cs.b2b.core.mapping.bean.bl.FreightCharge
import cs.b2b.core.mapping.bean.bl.FreightChargeCNTR
import cs.b2b.core.mapping.bean.bl.LastPOD
import cs.b2b.core.mapping.bean.bl.OceanLeg
import cs.b2b.core.mapping.bean.bl.POR
import cs.b2b.core.mapping.bean.bl.ReeferCargoSpec
import cs.b2b.core.mapping.util.XmlBeanParser

 /**
 * @author XIEMI
 * @pattern after TP=DUMMY310BLb
 */

public class CUS_CS2BLXML_310_GEORGIAPACIFIC {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_BL_O_Common blUtil = new cs.b2b.core.mapping.util.MappingUtil_BL_O_Common(util);  //single
//	cs.b2b.core.mapping.util.MappingUtil_BL_O_Common blUtil = new cs.b2b.core.mapping.util.MappingUtil_BL_O_Common();  //batch
	

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
	public void generateBody(Body current_Body, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '310'
				'E329_02' '-999'  //auto add
			}
			'B3' {
				if(current_Body?.GeneralInformation?.BLType?.toUpperCase() == 'ORIGINAL'){
					'E147_01' 'B'
				}else if(current_Body?.GeneralInformation?.BLType?.toUpperCase() == 'SEA WAYBILL'){
					'E147_01' '2'
				}
				
				if(current_Body?.GeneralInformation?.BLNumber){
					'E76_02' current_Body?.GeneralInformation?.BLNumber  //MORE SPACE
				}
				
				def vsize = current_Body?.ExternalReference?.findAll{it.CSReferenceType == 'SID'}?.size()
				if( vsize > 0){
					def ReferenceNumber = current_Body?.ExternalReference?.findAll{it.CSReferenceType == 'SID'}[vsize-1].ReferenceNumber
					'E145_03' ReferenceNumber.length() < 30 ? ReferenceNumber : ReferenceNumber.substring(0,30);
				}
				def v_prepaid_charge = filteredFreightCharge?.findAll{it.ChargeType == '1'}?.size()
				def v_collect_charge = filteredFreightCharge?.findAll{it.ChargeType == '0'}?.size()

				if(v_prepaid_charge == 0 && v_collect_charge > 0){
					'E146_04' 'CC'
				}else if(v_prepaid_charge > 0 && v_collect_charge == 0){
					'E146_04' 'PO'
				}else if(v_prepaid_charge > 0 && v_collect_charge > 0){
					'E146_04' 'MX'
				}else{
					'E146_04' 'MX'
				}
				
				
				
				if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.LocDT?.trim())){
					'E373_06' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.LocDT?.trim(), xmlDateTimeFormat, yyyyMMdd)
				}else if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLOnboardDT?.GMT?.trim())){
					'E373_06' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT?.trim(), xmlDateTimeFormat, yyyyMMdd)
				}else if(StringUtil.isNotEmpty(current_Body?.Route?.DepartureDT?.LocDT?.LocDT?.trim())){
					'E373_06' util.convertDateTime(current_Body?.Route?.DepartureDT?.LocDT?.LocDT?.trim(), xmlDateTimeFormat, yyyyMMdd)
				}

				sum = new BigDecimal(0);
				
				//new fix
				List<FreightCharge> filteredFreightCharge0 = filteredFreightCharge?.findAll{current_FreightCharge -> current_FreightCharge?.ChargeType?.equals("0")};
				List<FreightCharge> filteredFreightCharge1 = filteredFreightCharge?.findAll{current_FreightCharge -> current_FreightCharge?.ChargeType?.equals("1")};
				if(filteredFreightCharge0.size() > 0){
					filteredFreightCharge0.each{ current_FreightCharge->
					sum = sum + current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toBigDecimal()
					}
					if(sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP) > 0){
						'E193_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
					}else{
						'E193_07' '0'  //maybe error
					}
					//
				}else{
					'E193_07' '0'
				}
				
	
				
				def alocDT = null
				def elocDT = null
				current_Body?.Route?.OceanLeg?.find{alocDT = it.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT}
				current_Body?.Route?.OceanLeg?.find{elocDT = it.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT}
				if(current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT){
					'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '140'
				}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT){
					'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '139'
				}else if(alocDT){
					'E32_09' util.convertDateTime(alocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '139'
				}else if(elocDT){
					'E32_09' util.convertDateTime(elocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '140'
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
				
			}  //end B3 right
			
			//begin C3
			
			
			
			Map<String, String> actionMap = ['NEW':'00' ,'UPDATE':'53']
			if(actionMap.get(current_Body?.EventInformation?.EventDescription)){
				'B2A' {
						'E353_01' actionMap.get(current_Body?.EventInformation?.EventDescription)
						'E346_02' 'BL'
					}
			}
			if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.LocDT)){
				'Y6' {
					'E313_01' 'CA'
					'E151_02' current_Body?.GeneralInformation?.SCACCode
					'E275_03' util.convertDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
				}
			}
			
			
			
			//My N9
			//Loop by BookingNumber
			int countNum = 1;
			current_Body?.GeneralInformation?.CarrierBookingNumber?.each{current_BookingNumber ->
				if(countNum <= 15){
					countNum = countNum +1
					'N9' {
						'E128_01' 'BN'
						'E127_02' current_BookingNumber?.length() < 30? current_BookingNumber : current_BookingNumber.substring(0,30)
						'E369_03' 'BOOKING NUMBER'
					}
				}
			}  //fixed
			
			//Loop by Body/ExternalReference, split per 30 char
			def vCarrRatetTy= null
			def vItemCde = null
			Map<String, String> referenceCodeCS2Map = ['CTR':'CT','FR':'FN','SID':'SI','SR':'SI']  //execute sql to get your map data
			Map<String, String> mapRemarks = ['CTR':'CONTRACT NUMBER', 'FR':'FORWARDER REFERENCE', 'SID':"Shipper's Identifying Number for Shipment (SID)", 'SR':'SHIPPER REFERENCE']
			
			current_Body?.ExternalReference?.each{current_ExternalReference ->
				
				if(current_ExternalReference?.CSReferenceType ){
					// get the current looping type
					vCarrRatetTy = current_ExternalReference?.CSReferenceType
				}
				if (vCarrRatetTy){
					vItemCde = referenceCodeCS2Map.get(vCarrRatetTy)
				}
				if(vItemCde ){
					if(countNum <= 15){
						countNum = countNum +1
						'N9' {
							'E128_01' vItemCde
							'E127_02' current_ExternalReference?.ReferenceNumber?.length() < 30? current_ExternalReference?.ReferenceNumber : current_ExternalReference?.ReferenceNumber?.substring(0,30)
							if(StringUtil.isNotEmpty(mapRemarks?.get(vCarrRatetTy))){
								'E369_03' mapRemarks?.get(vCarrRatetTy).length() < 41 ? mapRemarks.get(vCarrRatetTy) : mapRemarks?.get(vCarrRatetTy)?.substring(0,41).toUpperCase()
							}
							
						}
					}
				}
			}
			
			//Loop by Body/CarrierRate
			
			
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
						if(countNum <= 15){
							countNum = countNum +1
							'N9' {
							'E128_01' vItemCde
							'E127_02' current_CarrierRate?.CarrierRateNumber?.length() < 30 ? current_CarrierRate?.CarrierRateNumber : current_CarrierRate?.CarrierRateNumber?.substring(0,30)
							'E369_03' mapRemarks?.get(vCarrRatetTy)
							}
						}
					}
					
			}
			
			//my: loop by Body/Party
			Map<String, String> mapPartyType = ['CGN':'4F', 'FWD':'4F', 'SHP':'4F']
			current_Body?.Party?.each {current_Party ->
				if(StringUtil.isNotEmpty(mapPartyType?.get(current_Party.PartyType)) && StringUtil.isNotEmpty(current_Party.CarrierCustomerCode)){
					if(countNum <= 15){
						countNum = countNum +1
						'N9'{
							'E128_01' mapPartyType?.get(current_Party.PartyType)
							'E127_02' current_Party.CarrierCustomerCode
						}
					}
				}
			}
			
			
			
			
			
			OceanLeg lastOceanLeg = null
			if(current_Body?.Route?.OceanLeg){       //first, find all that match your condition
				List<OceanLeg> OceanLegs = current_Body?.Route?.OceanLeg?.findAll{StringUtil.isNotEmpty(it?.SVVD?.Discharge?.VesselName)}
				if(OceanLegs.size() > 0){
					lastOceanLeg = OceanLegs[-1]   //the lase one
				}
			}
			if(lastOceanLeg){
				'V1' {
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber){
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber,1,8)
					}else if(lastOceanLeg?.SVVD?.Discharge?.CallSign){
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.CallSign,1,8)
					}
					if(lastOceanLeg?.SVVD?.Discharge?.VesselName){
						'E182_02' lastOceanLeg?.SVVD?.Discharge?.VesselName
					}
					if(StringUtil.isNotEmpty(lastOceanLeg?.SVVD?.Discharge?.Vessel)){
						'E26_03' lastOceanLeg?.SVVD?.Discharge?.Vessel
					}
					
					if(lastOceanLeg?.SVVD?.Discharge?.Voyage && lastOceanLeg?.SVVD?.Discharge?.Direction){
						'E55_04'  lastOceanLeg?.SVVD?.Discharge?.Voyage?.concat(lastOceanLeg?.DischargeDirectionName==null?"":lastOceanLeg?.DischargeDirectionName)
					}
					if(util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber,1,8)){
						'E897_08' 'L'
					}else if(util.substring(lastOceanLeg?.SVVD?.Discharge?.CallSign,1,8)){
						'E897_08' 'C'
					}
				}
			}
			
			FreightCharge freightCharge1 = null;
			FreightCharge freightCharge0 = null;
			
			//KATE fixed
			
			if(current_Body?.FreightCharge?.find{it?.ChargeType==blUtil.PREPAID || it?.ChargeType==blUtil.COLLECT || StringUtil.isNotEmpty(it?.TotalAmtInPmtCurrency?.attr_Currency)}){
				'C3'{
					def vChargeType1 = current_Body?.FreightCharge?.find{it?.ChargeType==blUtil.PREPAID && it?.TotalAmtInPmtCurrency?.attr_Currency != null}?.TotalAmtInPmtCurrency?.attr_Currency
					def vChargeType0 =  current_Body?.FreightCharge?.find{it?.ChargeType==blUtil.COLLECT && it?.TotalAmtInPmtCurrency?.attr_Currency != null}?.TotalAmtInPmtCurrency?.attr_Currency
					if(vChargeType1){
						'E100_01' vChargeType1
					}else if(vChargeType0){
						'E100_01' vChargeType0
					}
					if(vChargeType0){
						'E100_03' vChargeType0
					}else if(vChargeType1){
						'E100_03' vChargeType1
					}
				}
			}
			
			
			int y2Count = 0;
			current_Body?.Container?.groupBy {it?.CS1ContainerSizeType}?.each{currentContainerSizeType, currentContainerGroup->
				if(y2Count < 10){
					y2Count = y2Count + 1
					if(currentContainerSizeType){
						'Y2' {
						'E95_01' currentContainerGroup.size()
						'E24_04' currentContainerSizeType
						}
					}
				}
				
			}
			
				
			//MIKE XIE
			
			
			Map<String, String> partyTypeMap = ['CAR':'CA','CGN':'CN','FWD':'FW','NPT':'N1','ANP':'N2', 'SHP':'SH'] //MY: aready search
			current_Body?.Party?.each{current_Party ->
				if(partyTypeMap.get(current_Party?.PartyType)){
					'Loop_N1' {
						'N1' {
								'E98_01' partyTypeMap.get(current_Party?.PartyType)
							if(current_Party?.PartyName){
								'E93_02' util.substring(current_Party?.PartyName,1, 60)
							}
							if(current_Party?.CarrierCustomerCode){
								'E66_03' '25'
							}
							if(current_Party?.CarrierCustomerCode){
								'E67_04' current_Party?.CarrierCustomerCode
							}
						}
						def vAddressLines = ''
						current_Party?.Address?.AddressLines?.AddressLine?.each{current_addressLine ->
							vAddressLines = vAddressLines + ' ' + current_addressLine
						}
						
						//fixed
						def temp = current_Party?.Address
						def address = vAddressLines?.trim().concat(' ').concat(
						StringUtil.isEmpty(temp?.City?.trim()) ? '' : temp?.City?.trim()).concat(' ').concat(
						StringUtil.isEmpty(temp?.County?.trim()) ? '' : temp?.County?.trim()).concat(' ').concat(
						StringUtil.isEmpty(temp?.State?.trim()) ? '' : temp?.State?.trim()).concat(' ').concat(
						StringUtil.isEmpty(temp?.PostalCode?.trim()) ? '' : temp?.PostalCode?.trim()).concat(' ').concat(
						StringUtil.isEmpty(temp?.Country?.trim()) ? '' : temp?.Country?.trim())
						if(address.trim()){  //fixed
							'N3' {
								'E166_01' address.length() < 55 ? address.trim() : address.substring(0, 55).trim()
								if(address.trim()?.length() >= 56){
									'E166_02' util.substring(address?.trim(), 56, 55).trim()
								}
							}
						}
					}
				}
			}
		
			//R4  -->strat finished
			POR POR =  current_Body?.Route?.POR
			FirstPOL POL =  current_Body?.Route?.FirstPOL
			LastPOD POD =  current_Body?.Route?.LastPOD
			FND FND =  current_Body?.Route?.FND
			def oceanLegSize= current_Body?.Route?.OceanLeg?.size()
			//POR
			//if(POR){
				'Loop_R4' {
					'R4' { //here
						'E115_01' 'R'
						if(POR?.CityDetails?.LocationCode?.UNLocationCode){
							'E309_02' 'UN'
						}else if(StringUtil.isNotEmpty(POR?.CityDetails?.LocationCode?.SchedKDCode) && StringUtil.isNotEmpty(POR?.CityDetails?.LocationCode?.SchedKDType)){
							'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType
						}
						if(POR?.CityDetails?.LocationCode?.UNLocationCode){
							'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode
						}
						else if(StringUtil.isNotEmpty(POR?.CityDetails?.LocationCode?.SchedKDCode)){
							'E310_03' POR?.CityDetails?.LocationCode?.SchedKDCode
						}
						
						if(POR?.CityDetails?.City){
							'E114_04' util.substring(POR?.CityDetails?.City, 1, 24)
						}
						if(POR?.CSStandardCity?.CSCountryCode){
							'E26_05' POR?.CSStandardCity?.CSCountryCode
						}
						if(POR?.CSStandardCity?.CSStateCode){
							'E156_08' POR?.CSStandardCity?.CSStateCode
						}
					}
					
					'DTM' {
						//here here
						if(StringUtil.isNotEmpty(current_Body?.Route?.ActualCargoReceiptDT?.LocDT?.LocDT)){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body?.Route?.ActualCargoReceiptDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.ActualCargoReceiptDT?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(StringUtil.isNotEmpty(current_Body?.Route?.FullReturnCutoffDT?.LocDT?.LocDT)){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(StringUtil.isNotEmpty(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it.attr_Indicator?.equals("A")}?.LocDT?.LocDT)){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it.attr_Indicator?.equals("A")}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it.attr_Indicator?.equals("A")}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(StringUtil.isNotEmpty(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it.attr_Indicator?.equals("E")}?.LocDT?.LocDT)){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it.attr_Indicator?.equals("E")}?.LocDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it.attr_Indicator?.equals("E")}?.LocDT, xmlDateTimeFormat, HHmm)
						}
						
						
					}
				}
			//}
			//POL
			//if(POL){
				'Loop_R4' {
					'R4' {//here
						'E115_01' 'L'
						if(POL?.Port?.locationCode?.UNLocationCode){
							'E309_02' 'UN'
						}else if(StringUtil.isNotEmpty(POL?.Port?.locationCode?.SchedKDCode) && StringUtil.isNotEmpty(POL?.Port?.LocationCode?.SchedKDType)){
							'E309_02' POL?.Port?.LocationCode?.SchedKDType
						}
						if(POL?.Port?.locationCode?.UNLocationCode){
							'E310_03' POL?.Port?.locationCode?.UNLocationCode
						}
						else if(StringUtil.isNotEmpty(POL?.Port?.locationCode?.SchedKDCode)){
							'E310_03' POL?.Port?.locationCode?.SchedKDCode
						}
						
						if(POL?.Port?.PortName){
							'E114_04' util.substring(POL?.Port?.PortName, 1, 24)
						}else if(POL?.Port?.City){
							'E114_04' util.substring(POL?.Port?.City, 1, 24)
						}
						
						if(POL?.Port?.CSCountryCode){
							'E26_05' POL?.Port?.CSCountryCode
						}
						if(POL?.CSStateCode){
							'E156_08' POL?.CSStateCode
						}
					}
					'DTM' {
						
						if(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
						}
					}
				}
			//}
			//POD
			//if(POD){
				'Loop_R4' {
					'R4' { //here
						'E115_01' 'D'
						if(POD?.Port?.locationCode?.UNLocationCode){
							'E309_02' 'UN'
						}else if(StringUtil.isNotEmpty(current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) && StringUtil.isNotEmpty(current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDType)){
							'E309_02' current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDType
						}
						if(POD?.Port?.locationCode?.UNLocationCode){
							'E310_03' POD?.Port?.locationCode?.UNLocationCode
						}
						else if(StringUtil.isNotEmpty(POD?.Port?.locationCode?.SchedKDCode)){
							'E310_03' POD?.Port?.locationCode?.SchedKDCode
						}
						
						
						if(POD?.Port?.PortName){
							'E114_04' util.substring(POD?.Port?.PortName, 1, 24)
						}else if(POD?.Port?.City){
							'E114_04' util.substring(POD?.Port?.City, 1, 24)
						}
						if(POD?.Port?.CSCountryCode){
							'E26_05' POD?.Port?.CSCountryCode
						}
						if(POD?.CSStateCode){
							'E156_08' POD?.CSStateCode
						}
					}
					if(oceanLegSize > 0){
						'DTM' {
							if(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT){
								'E374_01' '140'
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT){
								'E374_01' '139'
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body.Route.OceanLeg?.get(0)?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT){
								'E374_01' '139'   //here
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body.Route.OceanLeg?.get(0)?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT){
								'E374_01' '140'  //FIXED
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
							}
						}
					}
				}
			//}
			//FND
			//if(FND){
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						//here
						if(FND?.CityDetails?.LocationCode?.UNLocationCode){
							'E309_02' 'UN'
						}else if(StringUtil.isNotEmpty(FND?.CityDetails?.LocationCode?.SchedKDCode) && StringUtil.isNotEmpty(current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDType)){
							'E309_02' current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDType
						}
						if(FND?.CityDetails?.LocationCode?.UNLocationCode){
							'E310_03' FND?.CityDetails?.LocationCode?.UNLocationCode
						}else if(StringUtil.isNotEmpty(FND?.CityDetails?.LocationCode?.SchedKDCode)){
							'E310_03' FND?.CityDetails?.LocationCode?.SchedKDCode
						}
						
						if(FND?.CityDetails?.City){
							'E114_04' util.substring(FND?.CityDetails?.City, 1, 24)
						}
						if(FND?.CSStandardCity?.CSCountryCode){
							'E26_05' FND?.CSStandardCity?.CSCountryCode
						}
						if(FND?.CSStandardCity?.CSStateCode){
							'E156_08' FND?.CSStandardCity?.CSStateCode
						}
					}
					'DTM' {
						if(oceanLegSize > 0){
							if(current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator.equals('A')}?.LocDT){    //find , you can use it.
								'E374_01' '140'
								'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub.find{it.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub.find{it.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT){
								'E374_01' '139'
								'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub.find{it.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub.find{it.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT){
								'E374_01' '140'
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT){
								'E374_01' '139'
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body?.Route.OceanLeg.get(0)?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT){
								'E374_01' '139'  //here
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.ArrivalDT.find{it.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.ArrivalDT.find{it.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body?.Route.OceanLeg.get(0)?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT){
								'E374_01' '140'  //here fixed
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.ArrivalDT.find{it.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.ArrivalDT.find{it.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, HHmm)
							}
						}
						
					}
				}
			//}
		

			current_Body?.BLCertClause?.each{current_BLCertClause ->
				'Loop_C8' {
					'C8' {
						if(current_BLCertClause?.CertificationClauseType){
							'E246_02' current_BLCertClause?.CertificationClauseType
						}
						if(current_BLCertClause?.CertificationClauseText){
							'E247_03'  util.substring(current_BLCertClause?.CertificationClauseText, 1, 60)
						}
						
					}
				}
			}
			associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
				List<Container> containerGroup = current_Body.Container.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.CS1ContainerSizeType == current_Container?.CS1ContainerSizeType}
				
				'Loop_LX' {
					'LX' { 'E554_01' Container_index +1 }
					//start N7
					if(util.substring(containerGroup[0]?.ContainerNumber, 5, 5)){
						'Loop_N7' {
							if(util.substring(current_Container?.ContainerNumber, 5, 10)){
								'N7' {
										'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
										'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10)
									def varWeight1 = null
									def varWeight2 = null
									if(current_Container?.GrossWeight?.WeightUnit == 'KGS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
										varWeight1 = current_Container?.GrossWeight?.Weight?.trim()
										'E81_03' varWeight1
									}else if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
										varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight?.trim())?.multiply(thousand)?.toString()
										if(varWeight1.indexOf('.') > 0){
											varWeight1 = blUtil.replaceZeroAfterPoint(varWeight1)
										}
										'E81_03' varWeight1
									}
											
									if(varWeight1){
										'E187_04' 'G'
									}
									if(varWeight1){
										'E188_17' 'K'
									}else if(varWeight2){
										'E188_17' 'L'
									}
									'E761_18' util.substring(current_Container?.ContainerCheckDigit, 1, 1)
									if(current_Container?.CS1ContainerSizeType){
										def ExtCDE = util.getConversionWithScac(TP_ID, 'BL', 'O', 'ContainerType', current_Container?.CS1ContainerSizeType, current_Body?.GeneralInformation?.SCACCode, conn)
										if(ExtCDE){
											'E24_22' ExtCDE
										}
		
									}
									
								}
							//end N7
							}
							//fixed here
							if(current_Container?.PieceCount?.PieceCount){
								'QTY' {
									'E673_01' '39'
									'E380_02' current_Container?.PieceCount?.PieceCount
									// begin fix
									'C001_03' {
										if(current_Container?.PieceCount?.PieceCountUnit){
											String ext_cde = util.getConversionWithScac(TP_ID, 'BL', 'O', 'QuantityType', current_Container?.PieceCount?.PieceCountUnit, 'ALL', conn)
											if(ext_cde){
												'E355_01' ext_cde
											}else{
												'E355_01' 'UN'
											}
										}else{
											'E355_01' 'UN'
										}

									}
								}
							}
							

					
							
							
							//fixed
							containerGroup?.each{ currentSubContainer ->
								currentSubContainer.Seal?.each{current_Seal ->
									if(util.substring(current_Seal?.SealNumber, 1, 15)){
										'M7' {
											'E225_01' util.substring(current_Seal?.SealNumber?.trim(), 1, 15)
											if(current_Seal?.SealType){
												'E98_05' current_Seal?.SealType
											}
											
										}
									}
								}
							}
							
							
							if(current_cargoList[0]?.ReeferCargoSpec?.size() > 0){
								ReeferCargoSpec current_ReeferCargoSpec = current_cargoList[0]?.ReeferCargoSpec[current_cargoList[0]?.ReeferCargoSpec?.size() -1]
								'W09' {
									'E40_01' 'CZ'
									//eg. in:-12.036 out:-12.03
									def temperature = current_ReeferCargoSpec?.Temperature?.Temperature
									def signal = null
									if(temperature?.startsWith('-')){
										signal = '-'
										temperature = temperature.replace('-' ,'')
									}
									def tempBeforePoint = null
									def tempAfterPoint = null
									def pointIndex = temperature?.indexOf('.')
									if(pointIndex > 0 && pointIndex <= 3){
										tempBeforePoint = util.substring(temperature, 1, pointIndex)
										tempAfterPoint = util.substring(temperature, pointIndex + 2, 4 - tempBeforePoint.length())
										if(signal){
											temperature = blUtil.replaceZeroAfterPoint(signal + tempBeforePoint + '.' + tempAfterPoint)
										}else{
											temperature = blUtil.replaceZeroAfterPoint((tempBeforePoint + '.' + tempAfterPoint))
										}
									}else if(pointIndex > 0 && pointIndex > 3){
										tempBeforePoint = util.substring(temperature, 1, 4)
										if(signal){
											temperature = blUtil.replaceZeroAfterPoint(signal + tempBeforePoint)
										}else{
											temperature = blUtil.replaceZeroAfterPoint(tempBeforePoint)
										}
									}else if(signal){
										temperature = signal + temperature
									}

									'E408_02' temperature
									
									if(current_ReeferCargoSpec?.Temperature?.Temperature){
										if(current_ReeferCargoSpec?.Temperature?.TemperatureUnit == 'C'){
											'E355_03' 'CE'
										}else if(current_ReeferCargoSpec?.Temperature?.TemperatureUnit == 'F'){
											'E355_03' 'FA'
										}
										
									}
									
									Map<String, String> ventilationMap =  ['25': 'A', '50':'B','75' :'C','100':'D','0':'E']
									if(current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'DEGREES' && ventilationMap.get(current_ReeferCargoSpec?.Ventilation?.Ventilation?.trim())){
										'E1122_07' ventilationMap.get(current_ReeferCargoSpec?.Ventilation?.Ventilation?.trim())
									}

									
									'E488_08' current_ReeferCargoSpec?.DehumidityPercentage
									if(current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBFPERMIN' || current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBMPERHOUR' ){
										'E380_09' current_ReeferCargoSpec?.Ventilation?.Ventilation
									}
									
								}
							}
						
							
							//after filter
							int current_DGCargoSpec_Num = 1;
							current_cargoList?.each {current_Cargo ->  //maybe the outside loop is unused
								current_Cargo?.DGCargoSpec?.each {current_DGCargoSpec ->
									if(current_DGCargoSpec?.UNNumber && current_DGCargoSpec_Num <= 10){
										current_DGCargoSpec_Num = current_DGCargoSpec_Num + 1
										'Loop_H1'{
											'H1' {
												if(current_DGCargoSpec?.UNNumber){
													'E62_01' current_DGCargoSpec?.UNNumber
												}
													
												if(current_DGCargoSpec?.IMOClass){
													'E209_02' current_DGCargoSpec?.IMOClass?.length() < 4 ? current_DGCargoSpec?.IMOClass : current_DGCargoSpec?.IMOClass?.substring(0,4)
												}
												if(current_DGCargoSpec?.DGRegulator=="IMD"){
													'E208_03' 'I'
												}
																							
												
												if(current_DGCargoSpec?.ProperShippingName){
													'E64_04' current_DGCargoSpec?.ProperShippingName?.length() < 30 ? current_DGCargoSpec?.ProperShippingName : current_DGCargoSpec?.ProperShippingName?.substring(0,30)
												}
												def firstLast = (current_DGCargoSpec?.EmergencyContact[0]?.FirstName?:"").concat(" ").concat((current_DGCargoSpec?.EmergencyContact[0]?.LastName?:""))
														
												'E63_05' firstLast.length() < 24 ? firstLast.trim() : firstLast?.substring(0,24)
												if(current_DGCargoSpec?.FlashPoint){
													def temperature = current_DGCargoSpec?.FlashPoint?.Temperature?.length() < 3 ? current_DGCargoSpec?.FlashPoint?.Temperature : current_DGCargoSpec?.FlashPoint?.Temperature?.substring(0,3)
													
													if(StringUtil.isNotEmpty(temperature) && temperature?.contains('.')){
														temperature = temperature?.substring(0, temperature.indexOf("."))  //out of index
													}
													'E77_07' temperature
												}
												Map<String, String> map = ['C':'CE' ,'F':'FA']
												if(map.get(current_DGCargoSpec?.FlashPoint?.TemperatureUnit)){   //fixed
													'E355_08' map.get(current_DGCargoSpec?.FlashPoint?.TemperatureUnit)
												}
												
												
												if(current_DGCargoSpec?.PackageGroup?.Code){
													'E254_09' current_DGCargoSpec?.PackageGroup?.Code
												}
												
												
											} //end H1
											
											//maybe wrong
												List<String> technicalNameList = blUtil.SplitTextWithConnector(current_DGCargoSpec?.TechnicalName, 60,'')
												if(technicalNameList?.size() > 0){
													technicalNameList?.each{current_technical ->
														if(current_technical!=null && current_technical!='null'){
															'H2' {
																if(util.substring(current_technical, 1, 30)?.length() == 1){
																	'E64_01' util.substring(current_technical, 1, 30)+'\\'
																}else{
																	'E64_01' util.substring(current_technical, 1, 30)
																}
																if(util.substring(current_technical, 31, 30)?.trim()){
																	'E274_02' util.substring(current_technical, 31, 30)?.trim()
																}
																
															}
														}
													}
														
												}
	
										}
									}
								}
							}
							
						}   //end loop N7
					}
					
				//current_Body?.Cargo?.eachWithIndex{current_Cargo, cargoIndex->
				current_cargoList?.eachWithIndex{current_Cargo, cargoIndex->
						'Loop_L0' {
							'L0' {
								'E213_01' cargoIndex + 1
								def NetWeightTON = null
								def NetWeightLBS = null
								def NetWeightKGS = null
								def GrossWeightTON = null
								def GrossWeightLBS = null
								def GrossWeightKGS = null

								if(current_Cargo?.NetWeight?.WeightUnit == 'TON' ){
								if(current_Cargo?.NetWeight?.Weight){
									NetWeightTON = new BigDecimal(current_Cargo?.NetWeight?.Weight).multiply(thousand).toString()
									if(NetWeightTON?.indexOf('.') > 0){
											NetWeightTON = blUtil.replaceZeroAfterPoint(NetWeightTON)
									}
									}else{
									NetWeightTON = 'NaN'
									}
									'E81_04' NetWeightTON
								}else if(current_Cargo?.NetWeight?.WeightUnit == 'LBS' ){
									NetWeightLBS = current_Cargo?.NetWeight?.Weight
									'E81_04' NetWeightLBS==null ?'NaN' :NetWeightLBS
								}else if(current_Cargo?.NetWeight?.WeightUnit == 'KGS'){
									NetWeightKGS = current_Cargo?.NetWeight?.Weight
									'E81_04' NetWeightKGS==null ?'NaN' :NetWeightKGS
								}else if(current_Cargo?.GrossWeight?.WeightUnit == 'LBS' && StringUtil.isNotEmpty(current_Cargo?.GrossWeight?.Weight)){
									GrossWeightLBS = current_Cargo?.GrossWeight?.Weight
									'E81_04' GrossWeightLBS
								}else if(current_Cargo?.GrossWeight?.WeightUnit == 'KGS' && StringUtil.isNotEmpty(current_Cargo?.GrossWeight?.Weight)){
									GrossWeightKGS = current_Cargo?.GrossWeight?.Weight
									'E81_04' GrossWeightKGS
								}else if(current_Cargo?.GrossWeight?.WeightUnit == 'TON' ){
									if(current_Cargo?.GrossWeight?.Weight){
										GrossWeightTON = new BigDecimal(current_Cargo?.GrossWeight?.Weight).multiply(thousand).toString()
										//if(GrossWeightTON?.indexOf('.') > 0){  //fixed
											GrossWeightTON = blUtil.replaceZeroAfterPoint(GrossWeightTON)
											'E81_04' GrossWeightTON
										//}
									}else{
										GrossWeightTON = ''
										}
									
									//REMEMBER
									
								}

								
								if(NetWeightTON || NetWeightLBS || NetWeightKGS){
									'E187_05' 'N'
								}else if(GrossWeightTON || GrossWeightLBS || GrossWeightKGS){
									'E187_05' 'G'
								}
								
								//7
								def CBFVolume = null
								def CBMVolume = null
								def CFTVolume = null
								if(StringUtil.isNotEmpty(current_Cargo?.Volume?.Volume)){
									if(current_Cargo?.Volume?.VolumeUnit == 'CBF'){
										if(current_Cargo?.Volume?.Volume?.startsWith('0.')){
											def v = current_Cargo?.Volume?.Volume?.indexOf('.')
											CBFVolume =  util.substring(current_Cargo?.Volume?.Volume, v+1, current_Cargo?.Volume?.Volume?.length() - v)
										}else {
											CBFVolume =  current_Cargo?.Volume?.Volume
										}
										'E183_06' CBFVolume
										if(CBFVolume){
											'E184_07' 'X'
										}
									}else if( current_Cargo?.Volume?.VolumeUnit == 'CBM' ){
										if(current_Cargo?.Volume?.Volume?.startsWith('0.')){
											def v = current_Cargo?.Volume?.Volume?.indexOf('.')
											CBMVolume = util.substring(current_Cargo?.Volume?.Volume, v+1, current_Cargo?.Volume?.Volume?.length() - v)
										}else {
											CBMVolume = current_Cargo?.Volume?.Volume
										}
										'E183_06' CBMVolume
										if(CBMVolume){
											'E184_07' 'E'
										}
									}else if(current_Cargo?.Volume?.VolumeUnit == 'CFT'){
										if(current_Cargo?.Volume?.Volume?.startsWith('0.')){
											def v = current_Cargo?.Volume?.Volume?.indexOf('.')
											CFTVolume = util.substring(current_Cargo?.Volume?.Volume, v+1, current_Cargo?.Volume?.Volume?.length() - v)
										}else {
											CFTVolume = current_Cargo?.Volume?.Volume
										}
										'E183_06' CFTVolume
										if(CFTVolume){
											'E184_07' 'X'
										}
									}
								}
								
								//end 7
								
								if(current_Cargo?.Packaging?.PackageQty){
									'E80_08' current_Cargo?.Packaging?.PackageQty?.trim()
								}else{
									'E80_08' '0'
								}
								
								//fixing
								String ext_cde = util.getConversion(TP_ID, 'BL', 'O', 'PackageUnit', current_Cargo?.Packaging?.PackageType, conn)
								if(ext_cde && current_Cargo?.Packaging?.PackageQty){
									'E211_09' ext_cde
								}else{
									'E211_09' 'UNT'
								}
								
								if(NetWeightKGS){
									'E188_11' 'K'
								}else if(NetWeightLBS){
									'E188_11' 'L'
								}else if(NetWeightTON){
									'E188_11' 'K'
								}else if(GrossWeightKGS){
									'E188_11' 'K'
								}else if(GrossWeightLBS){
									'E188_11' 'L'
								}else if(GrossWeightTON){
									'E188_11' 'K'
								}
							} //end L0
							//split current_Cargo?.CargoDescription
							List<String> CargoDescription = blUtil.SplitTextWithConnector(current_Cargo?.CargoDescription, 50)
							List<MarksAndNumbers>  MarksAndNumbersList = current_Cargo?.MarksAndNumbers
							def numOfLoop = null
							if(CargoDescription.size() > MarksAndNumbersList.size()){
								numOfLoop = CargoDescription.size()

							}else{
								numOfLoop = MarksAndNumbersList.size()
							}
							for(int i = 0; i < numOfLoop; i++){
								if(CargoDescription[i] || MarksAndNumbersList[i]?.MarksAndNumbersLine){
									'L5' {
										'E213_01' i + 1
										if(i < CargoDescription.size()){
											if(CargoDescription[i]){
												'E79_02' CargoDescription[i].trim()
											}
										}
//										if(i < MarksAndNumbersList.size()){
										if(MarksAndNumbersList[i]?.MarksAndNumbersLine){
										'E87_06' MarksAndNumbersList[i].MarksAndNumbersLine.length() < 49 ? MarksAndNumbersList[i]?.MarksAndNumbersLine?.trim() : util.substring(MarksAndNumbersList[i].MarksAndNumbersLine, 1, 48)
										
										}
										
//										if(MarksAndNumbersList[i]?.SeqNumber == (i+1).toString()){
											if(MarksAndNumbersList[i]?.MarksAndNumbersLine){
												'E88_07' 'L'  //fixed
											}
//										}
										
										
//										}
										
									}
								}
							}
						}
					}
					
				}
			}
			
			'L3' {
			
				if(util.isDecimal(current_Body?.GeneralInformation?.BLGrossWeight?.Weight) && current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.toBigDecimal() > 0){
					def vWeight = current_Body?.GeneralInformation?.BLGrossWeight?.Weight
					if(current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.indexOf('.') > 0){
						  vWeight = blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLGrossWeight?.Weight)
					}
					'E81_01'  vWeight
					'E187_02' 'G'
				}else if(util.isDecimal(current_Body?.GeneralInformation?.BLNetWeight?.Weight) && current_Body?.GeneralInformation?.BLNetWeight?.Weight?.toBigDecimal() > 0){
					def vWeight = current_Body?.GeneralInformation?.BLNetWeight?.Weight
					if(current_Body?.GeneralInformation?.BLNetWeight?.Weight?.indexOf('.') > 0){
						  vWeight = blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLNetWeight?.Weight)
					}
					'E81_01'  vWeight
					'E187_02' 'N'
				}
				
				
				sum = new BigDecimal(0)
				filteredFreightCharge?.findAll{it.ChargeType == '0'}.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
					}
					
				}
				if(sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP) > 0){
					'E58_05' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString()
				}
				
				sum = new BigDecimal(0)
				current_Body?.FreightCharge?.findAll{it.ChargeType == '1'}?.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
					}
				}
				
				String E117 = (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
				'E117_07' E117
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
			
			
			//
			filteredFreightCharge?.eachWithIndex{current_FreightCharge, FreightChargeIndex->
				'Loop_L1' {
					'L1' { //here, L1 means L1_2
						'E213_01' FreightChargeIndex + 1
						if(current_FreightCharge?.ChargeType?.equals(blUtil.COLLECT)){
							String TotalAmtInPmtCurrency = current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency;
							
							if(TotalAmtInPmtCurrency.length() == 10 || TotalAmtInPmtCurrency.length() == 12){
								'E60_02' TotalAmtInPmtCurrency.substring(0,9)
							}else if(TotalAmtInPmtCurrency.length() == 11){
								'E60_02' TotalAmtInPmtCurrency.substring(0,8)
							}else{
								'E60_02' TotalAmtInPmtCurrency
							}
						}else if(current_FreightCharge?.ChargeType?.equals(blUtil.PREPAID)){
							def TotalAmtInPmtCurrency = current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency;
							if(TotalAmtInPmtCurrency.length() == 10 || TotalAmtInPmtCurrency.length() == 12){
								'E60_02' TotalAmtInPmtCurrency.substring(0,9)
							}else if(TotalAmtInPmtCurrency.length() == 11){
								'E60_02' TotalAmtInPmtCurrency.substring(0,8)
							}else{
								'E60_02' TotalAmtInPmtCurrency
							}
						}
						
						
						
						String var = current_FreightCharge?.CalculateMethod
						if(var?.toUpperCase()?.equals("CONTAINER") || var?.substring(0,1).equals("2") || var?.substring(0,1).equals("4")){
							'E122_03' 'PA'
						}else if(var?.toUpperCase().equals("GROSS CARGO WEIGHT")){
							'E122_03' 'WM'
						}else if(var?.toUpperCase().equals("NET CARGO WEIGHT")){
							'E122_03' 'WM'
						}else if(var?.toUpperCase().equals("LUMP SUM")){
							'E122_03' 'LS'
						}else if(var?.toUpperCase().equals("MEASUREMENT")){
							'E122_03' 'VM'
						}else if(var?.toUpperCase().equals("PACKAGE")){
							'E122_03' 'PU'
						}else if(var?.substring(0,1).equals("B")){
							'E122_03' 'CS'
						}else if(var?.trim().equals("PA")){
							'E122_03' var
						}else if(var?.trim().equals("LS") || var?.trim().equals("VM") || var?.trim().equals("WM") || var?.trim().equals("PU") || var?.trim().equals("CS")){
							'E122_03' var
						}else{
//							'E122_03' ''  //fixed 5/3
						}
						
						
						if(current_FreightCharge?.ChargeType == '0'){
							if(current_FreightCharge?.TotalAmtInPmtCurrency){
								def total = new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency.toString()).multiply(hundred).setScale(2,BigDecimal.ROUND_HALF_UP).toString()
								if(total?.indexOf('.')>0){
									total = total?.replaceAll('0+?$', '' )?.replaceAll('[.]$', '' )
								}
								'E58_04' total
							}
						}else{
							'E58_04' '0'
						}
						if(current_FreightCharge?.ChargeType?.equals(blUtil.PREPAID)){
							if(current_FreightCharge?.TotalAmtInPmtCurrency){
								def total =  new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()).multiply(hundred).toString()
								if(total?.indexOf('.')>0){
									total = blUtil.replaceZeroAfterPoint(total)
								}
								'E117_06' total
							}
						}else{
							'E117_06' '0'
						}
						
						Map<String, String> chargeTypeExtCDE = blUtil.getB2BCdeConversion(current_FreightCharge?.ChargeCode, TP_ID, 'ChargeType', conn)
						if(StringUtil.isNotEmpty(chargeTypeExtCDE.get("EXT_CDE"))){
							'E150_08' chargeTypeExtCDE.get("EXT_CDE")
						}else{
							'E150_08' '999'
						}
						if(current_FreightCharge?.ChargeType == '0'){
							'E16_11' 'E'
						}else{
							'E16_11' 'P'
						}

						Map<String, String> chargeTypeExtCDE2 = blUtil.getB2BCdeConversion(current_FreightCharge?.ChargeCode?.length() < 3 ? current_FreightCharge?.ChargeCode : current_FreightCharge?.ChargeCode?.substring(0,3)?.toUpperCase(), TP_ID, 'ChargeType', conn)
						if(chargeTypeExtCDE2.get("REMARKS")){
							'E276_12' util.substring(chargeTypeExtCDE2.get("REMARKS"), 1, 25).trim()
						}else{
							'E276_12' '999'
						}
						
						//fixing: writter by Gawing
						def out_value
						BigDecimal vCharge = current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toString()?.toBigDecimal()
						BigDecimal vExchangeRate = current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.toBigDecimal()
						BigDecimal vFreightRate = current_FreightCharge?.FreightRate?.toBigDecimal();
						BigDecimal vDivNum = new BigDecimal(1)
						if(vFreightRate != null && vFreightRate?.doubleValue() != 0 && vExchangeRate?.doubleValue() > 0) {
							vCharge = vCharge?.divide(vDivNum, 15, BigDecimal.ROUND_FLOOR)
							vCharge = vCharge?.divide(vFreightRate, 15, BigDecimal.ROUND_FLOOR)   //ERROR
							vCharge = vCharge?.divide(vExchangeRate, 15, BigDecimal.ROUND_FLOOR)
							out_value = "" + vCharge?.toBigDecimal()
						} else if(vFreightRate != null &&  vFreightRate?.doubleValue() != 0&& vExchangeRate?.doubleValue() == 0) {
							vCharge = vCharge?.divide(vDivNum, 15, BigDecimal.ROUND_FLOOR)
							vCharge = vCharge?.divide(vFreightRate, 15, BigDecimal.ROUND_FLOOR)
							out_value = "" + vCharge?.toBigDecimal()
						}else{
							out_value = ""
						}
						if(StringUtil.isNotEmpty(out_value)){
							'E220_17' blUtil.replaceZeroAfterPoint(util.substring(out_value.toBigDecimal().setScale(4, BigDecimal.ROUND_HALF_UP).toString(),1,11))
						}else{
							'E220_17' '0'
						}
						//18
						//fix here;

						Map<String, String> map = ['LUMP SUM':'FR']
						if(current_FreightCharge?.ChargeType?.equals("0")){
							if(StringUtil.isNotEmpty(current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)){
								'E221_18' StringUtil.isNotEmpty(map.get(current_FreightCharge?.CalculateMethod?.toUpperCase())) ? map.get(current_FreightCharge?.CalculateMethod?.toUpperCase()): 'NR'
							}
						}else if(current_FreightCharge?.ChargeType?.equals("1")){
							if(StringUtil.isNotEmpty(current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)){
								'E221_18' StringUtil.isNotEmpty(map.get(current_FreightCharge?.CalculateMethod?.toUpperCase())) ? map.get(current_FreightCharge?.CalculateMethod?.toUpperCase()): 'NR'
							}
						}


						
					}//end L1

					
					if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency){
						'C3' {  //here , C3 means C3_1
							'E100_01' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency
						}
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
		def txnErrorKeys = []

	//Start mapping

		bl?.Body?.eachWithIndex{current_Body, current_BodyIndex ->

			List<FreightCharge> filteredFreightCharge = current_Body.FreightCharge?.clone()
			List<FreightChargeCNTR> filteredFreightChargeCNTR = current_Body.FreightChargeCNTR?.clone()
			//OceanLegReorder
			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)
			//ChargeFilter
			blUtil.filterByTotalAmtInPmtCurrencyIfZero(filteredFreightCharge, filteredFreightChargeCNTR)

			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)
			
			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			//mapping
			if(errorKeyList.isEmpty()){
				generateBody(current_Body,  associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml)
			}
			//EDI Syntax checking
			if(errorKeyList.isEmpty()){
				syntaxValidation(writer?.toString(), errorKeyList)
			}
			
			//posp checking
			if(errorKeyList.isEmpty()){
				pospValidation(writer?.toString(), errorKeyList)
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
			result = writer?.toString();
		}

		writer.close();
		
		return result;
	}

	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		//parameter: cs.b2b.core.mapping.bean.bl.Body current_Body, int maxLength, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList
		blUtil.filterBLTypeMEMO(current_Body, false, null, errorKeyList)
		blUtil.checkSealNumberLength(current_Body, 15, true, null, errorKeyList)
		blUtil.filterDELinTxnAction(current_Body,false, null, errorKeyList)
		return errorKeyList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){


		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")
		//parameters: Node root, int maxCount, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList
		blUtil.checkL1max(node, 20, true, "L1 segment more than 20.", errorKeyList)
		blUtil.checkL102L106L307Checker(node, true, null, errorKeyList)  //
		blUtil.checkH1_FlashPoint(node, true, null, errorKeyList)
		
		
	}

	
	void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){
		
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")
		
		blUtil.checkB309_B310_P(node, true, null, errorKeyList)
		blUtil.checkC803_C802_R(node, true, null, errorKeyList)
		blUtil.checkW0902_W0903_P(node, true, null, errorKeyList)
		blUtil.checkV108_V101_C(node, true, null, errorKeyList)
		blUtil.checkV104_M(node, true, null, errorKeyList)  //Segment V104 is Missed
		blUtil.checkV102_M(node, true, null, errorKeyList)
		blUtil.checkR4DTM_M(node, true, "ATA/ETA can not be null", errorKeyList)  //ATA/ETA can not be null ---
		blUtil.checkR402_R403_P(node, true, null, errorKeyList)
		blUtil.checkDTM02_DTM03_DTM_05_R(node, true, null, errorKeyList)
		blUtil.checkDTM04_DTM03_C(node, true, null, errorKeyList)
		blUtil.checkH107_H108_P(node, true, null, errorKeyList)
		blUtil.checkL006_L007_P(node, true, null, errorKeyList)
		blUtil.checkL008_L009_P(node, true, null, errorKeyList)
		blUtil.checkL011_L004_C(node, true, null, errorKeyList)
		blUtil.checkL102_L103_P(node, true, null, errorKeyList)
		blUtil.checkL104_L105_L106_R(node, true, null, errorKeyList)
		blUtil.checkL117_L118_P(node, true, null, errorKeyList)
		blUtil.checkL301_L302_P(node, true, null, errorKeyList)
		blUtil.checkL309_L310_P(node, true, null, errorKeyList)
		blUtil.checkL312_L301_C(node, true, null, errorKeyList)
		blUtil.checkL3_M(node, true, null, errorKeyList)
		blUtil.checkL507_L506_C(node, true, null, errorKeyList)
		blUtil.checkN102_N103_R(node, true, null, errorKeyList)  //At least one of N102, N103 is required.
		blUtil.checkN103_N104_P(node, true, null, errorKeyList)
		blUtil.checkN702_M(node, true, null, errorKeyList)
		blUtil.checkN703_N704_P(node, true, null, errorKeyList)
		blUtil.checkN722_M(node, true, null, errorKeyList)    //Segment N722 is Missed
		blUtil.checkN902_N903_R(node, true, null, errorKeyList)

	}

}

