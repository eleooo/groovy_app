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
* @pattern after TP=HDYOW
*/
public class CUS_CS2BLXML_310_ACUHASBRO {

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
				
				def vsize = current_Body?.ExternalReference?.findAll{it.CSReferenceType == 'SID'}?.size()
				if( vsize > 0){
					'E145_03' util.substring(current_Body?.ExternalReference?.findAll{it.CSReferenceType == 'SID'}[vsize-1].ReferenceNumber, 1, 30)
				}else{
					'E145_03' 'N/A'
				}
				
				'E146_04' 'CC'
				
				SimpleDateFormat soutfmt = new SimpleDateFormat(xmlDateTimeFormat)
				
				'E373_06' util.convertDateTime(soutfmt.format(new Date()), xmlDateTimeFormat, yyyyMMdd)
				
				sum = new BigDecimal(0);
				filteredFreightCharge.each{current_FreightCharge ->
					if(current_FreightCharge?.TotalAmtInPmtCurrency){
						sum = sum + new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString())
					}
				}
				'E193_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
		
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
			Map<String, String> eventDescriptionMap = ['NEW':'00']
			'B2A' {
				if(eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)){
					'E353_01' eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)
				}else{
					'E353_01' '04'
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
			Map<String, String> referenceCodeCS2Map = ['CR':'CR','CTR':'CT','EXPR':'RF','FID':'FI','GEN':'GN','ICR':'CR','INV':'IK',
													   'OTH':'ZZ','PO':'PO','QUOT':'QN','SC':'E8','SCA':'SCA','SID':'SI','SO':'SO','SR':'SR','TARIF':'TI','WARG':'QA']
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
					if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber){
						'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 8)
					}else if(firstOceanLeg?.SVVD?.Loading?.CallSign){
						'E597_01' firstOceanLeg?.SVVD?.Loading?.CallSign
					}
					if(firstOceanLeg?.SVVD?.Loading?.VesselName){
						'E182_02' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName, 1, 35)
					}
					
					if(firstOceanLeg?.SVVD?.Loading?.Voyage && firstOceanLeg?.SVVD?.Loading?.Direction){
						if(firstOceanLeg?.LoadingDirectionName){
							'E55_04' firstOceanLeg?.SVVD?.Loading?.Voyage  + firstOceanLeg?.LoadingDirectionName
						}else{
							'E55_04' firstOceanLeg?.SVVD?.Loading?.Voyage 
						}
					}
					'E140_05' current_Body?.GeneralInformation.SCACCode
					if(util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber,1,8)){
						'E897_08' 'L'
					}else if(util.substring(firstOceanLeg?.SVVD?.Loading?.CallSign,1,8)){
						'E897_08' 'C'
					}
				}
			}
			if(lastOceanLeg){
				'V1' {
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber){
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 8)
					}else if(lastOceanLeg?.SVVD?.Discharge?.CallSign){
						'E597_01' lastOceanLeg?.SVVD?.Discharge?.CallSign
					}
					if(lastOceanLeg?.SVVD?.Discharge?.VesselName){
						'E182_02' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName, 1, 35)
					}
					
					if(lastOceanLeg?.SVVD?.Discharge?.Voyage && lastOceanLeg?.SVVD?.Discharge?.Direction){
						if(lastOceanLeg?.DischargeDirectionName){
							'E55_04' lastOceanLeg?.SVVD?.Discharge?.Voyage + lastOceanLeg?.DischargeDirectionName
						}else{
							'E55_04' lastOceanLeg?.SVVD?.Discharge?.Voyage
						}
						
					}
					'E140_05' current_Body?.GeneralInformation.SCACCode
					if(util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber,1,8)){
						'E897_08' 'L'
					}else if(util.substring(lastOceanLeg?.SVVD?.Discharge?.CallSign,1,8)){
						'E897_08' 'C'
					}
				}
			}
			
			///////////////////   N1-start
			Map<String, String> partyTypeMap = ['CGN':'CN','FWD':'FW','NPT':'N1','ANP':'N2', 'SHP':'SH']
			current_Body?.Party?.each{current_Party ->
				if(partyTypeMap.get(current_Party?.PartyType)){
					'Loop_N1' {
						'N1' {
							'E98_01' partyTypeMap.get(current_Party?.PartyType)
							if(current_Party?.PartyName){
								'E93_02' util.substring(current_Party?.PartyName,1, 60)
							}
							if(current_Party?.CarrierCustomerCode){
								'E66_03' '67'
							}
							if(current_Party?.CarrierCustomerCode){
								'E67_04' current_Party?.CarrierCustomerCode
							}
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
							if(StringUtil.isNotEmpty(current_Party?.Address?.City)){
								'E19_01' current_Party?.Address?.City
							}
							Map<String, String> vResult = null
							if(current_Party?.Address?.State){
								vResult = util.getCS2MasterCityByStateNameCityNameCntryCde(current_Party?.Address?.State, current_Party?.Address?.City, current_Party?.Address?.Country, conn)
							}
							Map<String, String> vResult1 = null
							Map<String, String> vResult2 = null
							if(StringUtil.isEmpty(vResult?.get('STATE_CDE')) ){
								vResult1 = util.getCS2MasterCityByStateNameCityName(current_Party?.Address?.State, current_Party?.Address?.City, conn)
							}
							if(StringUtil.isEmpty(vResult?.get('STATE_CDE')) && StringUtil.isEmpty(vResult1?.get('STATE_CDE')) ){
								vResult2 = util.getCS2MasterCityByStateName(current_Party?.Address?.State, conn)
						   	}
							if(vResult?.get('STATE_CDE')){
								'E156_02' vResult?.get('STATE_CDE')
							}else if(vResult1?.get('STATE_CDE')){
								'E156_02' vResult1?.get('STATE_CDE')
							}else if(vResult2?.get('STATE_CDE')){
								'E156_02' vResult2?.get('STATE_CDE')
							}
							if(vResult?.get('STATE_CDE') || vResult1?.get('STATE_CDE') || vResult2?.get('STATE_CDE')){
								if(current_Party?.Address?.PostalCode){
									'E116_03' current_Party?.Address?.PostalCode
								}
							}
							if(StringUtil.isNotEmpty(current_Party?.Address?.Country)){
								'E26_04' util.substring(current_Party?.Address?.Country, 1, 3)
							}
							
							if(current_Party?.Address?.LocationCode?.UNLocationCode){
								'E309_05' 'UN'
								'E310_06' current_Party?.Address?.LocationCode?.UNLocationCode
							}else if(current_Party?.Address?.LocationCode?.SchedKDType){
								'E309_05' current_Party?.Address?.LocationCode?.SchedKDType
								'E310_06' current_Party?.Address?.LocationCode?.SchedKDCode
							}
						}
					}
				}
			}
			
			
			///////////////////   G61-start
			def G61index = 0
			current_Body?.Party?.each{current_Party ->
				def FirstName = current_Party?.Contact?.FirstName
				def LastName = current_Party?.Contact?.LastName
				def vname = null
				if(StringUtil.isNotEmpty(current_Party?.Contact?.FirstName)){
					vname = util.substring((FirstName!=null? FirstName  :'' ) + ' ' + (LastName!=null? LastName :'' ) , 1 , 60)
				}
				def CountryCode = current_Party?.Contact?.ContactPhone?.CountryCode!=null? current_Party?.Contact?.ContactPhone?.CountryCode : ''
				def AreaCode = current_Party?.Contact?.ContactPhone?.AreaCode!=null? current_Party?.Contact?.ContactPhone?.AreaCode : ''
				def vnumber = null
				if(current_Party?.Contact?.ContactPhone?.Number){
					vnumber = CountryCode+ '-' + AreaCode+ '-' +current_Party?.Contact?.ContactPhone?.Number
				}
				
				if(StringUtil.isNotEmpty(vnumber) && StringUtil.isNotEmpty(vname)){
					if(G61index < 3){
						G61index = G61index + 1
						'G61' {
							'E366_01' 'IC'
							'E93_02' vname?.trim()
							'E365_03' 'TE'
							'E364_04' vnumber
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
					'E115_01' 'R'
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
						if(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it?.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
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
						if(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
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
					if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator.equals('A')}?.LocDT){
						'E374_01' '140'
						'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub.find{it?.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub.find{it?.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, HHmm)
					}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E374_01' '139'
						'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub.find{it?.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub.find{it?.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, HHmm)
					}else if(oceanLegSize > 0 && current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'A'}?.LocDT){
						'E374_01' '140'
						'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'A'}.LocDT, xmlDateTimeFormat, HHmm)
					}else if(oceanLegSize > 0 && current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E374_01' '139'
						'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT.find{it?.attr_Indicator == 'E'}.LocDT, xmlDateTimeFormat, HHmm)
					}
				}
			}
		
			
			///////////////////   R2-start
			'R2' {
				'E140_01' current_Body?.GeneralInformation?.SCACCode
				'E133_02' 'O'
				Map<String, String> trafficModeMap =  ['LCLLCL':'PP', 'FCLFCL': 'HH', 'FCLLCL' :'HP', 'LCLFCL' :'PH']
				if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.OutBound) && StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.InBound)){
					'E56_12' trafficModeMap.get(current_Body?.GeneralInformation?.TrafficMode?.OutBound + current_Body?.GeneralInformation?.TrafficMode?.InBound)
				}
				
			}
			
			///////////////////   C8-start
			current_Body?.BLCertClause?.each{current_BLCertClause ->
				'Loop_C8' {
					'C8' {
						if(current_BLCertClause?.CertificationClauseType){
							'E246_02' current_BLCertClause?.CertificationClauseType
						}
						if(StringUtil.isNotEmpty(current_BLCertClause?.CertificationClauseText)){
							'E247_03'  util.substring(current_BLCertClause?.CertificationClauseText, 1, 60)
						}
					}
					List<String> splitTextList= new ArrayList<String>()
					if(current_BLCertClause?.CertificationClauseText?.length() > 60){
						def v = util.substring(current_BLCertClause?.CertificationClauseText, 61, current_BLCertClause?.CertificationClauseText?.length() - 60)
						while(v.size() > 180){
							splitTextList.add(util.substring(v, 1, 180))
							v = util.substring(v, 181, v.size() - 180)
						}
						if(v.size() > 0){
							splitTextList.add(v)
						}
					}
					splitTextList?.each{current_text->
						'C8C' {
							if(util.substring(current_text, 1, 60)){
								'E247_01' util.substring(current_text, 1, 60)
							}
							if(current_text?.length() > 60){
								'E247_02' util.substring(current_text, 61, 60)
							}
							if(current_text?.length() > 120){
							'E247_03' util.substring(current_text, 121, 60)
							}
						}
					}
					
				}
			}	
			
			///////////////////   LX-start
			associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
				List<Container> containerGroup = current_Body.Container.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.CS1ContainerSizeType == current_Container?.CS1ContainerSizeType}
				'Loop_LX' {
					'LX' {
						 'E554_01' Container_index +1 
					}
					if(util.substring(containerGroup[0]?.ContainerNumber, 5, 5)){
						'Loop_N7' {
							if(util.substring(current_Container?.ContainerNumber, 5, 10)){
								'N7' {
									'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
									'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10)
									
									def varWeight1 = null
									def varWeight2 = null
									if(current_Container?.GrossWeight?.WeightUnit == 'KGS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight.trim())){
										varWeight1 = current_Container?.GrossWeight?.Weight.trim()
										'E81_03' varWeight1
									}else if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight)){
										varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight.trim()).multiply(thousand).toString()
										if(varWeight1.indexOf('.') > 0){
											varWeight1 = blUtil.replaceZeroAfterPoint(varWeight1)
										}
										'E81_03' varWeight1
									}
									if(varWeight1){
										'E187_04' 'G'
									}
									'E140_12' current_Body?.GeneralInformation?.SCACCode
											if(varWeight1){
												'E188_17' 'K'
											}
									'E761_18' util.substring(current_Container?.ContainerCheckDigit, 1, 1)
									
									Map<String,String> haulageMap = ['C':'D','M':'P']
									def haulageOutBound = haulageMap.get(current_Body?.Route?.Haulage?.OutBound)==null?"":haulageMap.get(current_Body?.Route?.Haulage?.OutBound)
									def haulageInBound = haulageMap.get(current_Body?.Route?.Haulage?.InBound)==null?"":haulageMap.get(current_Body?.Route?.Haulage?.InBound)
									if(StringUtil.isNotEmpty(haulageOutBound.concat(haulageInBound))){
										'E56_19' haulageOutBound.concat(haulageInBound)
									}
									
									if(current_Container?.CS1ContainerSizeType){
										def ExtCDE = util.getConversionWithScac(TP_ID, 'BL', 'O', 'ContainerType', current_Container?.CS1ContainerSizeType,current_Body?.GeneralInformation?.SCACCode, conn)
												if(ExtCDE){
													'E24_22' ExtCDE
												}
										
									}
									'E140_23' current_Body?.GeneralInformation?.SCACCode
											
								}
							}
							
							//select * from b2b_cde_conversion t where t.convert_type_id = 'CTNRPIECECOUNTUNIT' and t.dir_id = 'O' and t.msg_type_id = 'BL' and t.msg_fmt_id = 'X.12' and t.tp_id = ''
							//DB has no result
							if(current_Container?.PieceCount?.PieceCount){
								'QTY' {
									'E673_01' '39'
									if(current_Container?.PieceCount?.PieceCount){
										'E380_02' current_Container?.PieceCount?.PieceCount
									}
									'C001_03' {
										if(current_Container?.PieceCount?.PieceCountUnit){
											'E355_01' current_Container?.PieceCount?.PieceCountUnit.trim()
										}else{
											'E355_01' 'UN'
										}
										
									}
								}
							}
							
							containerGroup?.each{ currentSubContainer ->
								currentSubContainer.Seal?.each{current_Seal ->
									if(current_Seal?.SealNumber){
										'M7' {
											'E225_01' current_Seal?.SealNumber?.trim()
											
											Map<String,String> sealTypeMap = ['TM':'TO']
													if(sealTypeMap.keySet().contains(current_Seal?.SealType)){
														'E98_05' sealTypeMap.get(current_Seal?.SealType)
													}else if(current_Seal?.SealType){
														'E98_05' current_Seal?.SealType
													}
										}
									}
								}
							}
							
							if(current_cargoList[0]?.ReeferCargoSpec?.size() > 0){
								cs.b2b.core.mapping.bean.bl.ReeferCargoSpec current_ReeferCargoSpec = current_cargoList[0]?.ReeferCargoSpec[current_cargoList[0]?.ReeferCargoSpec?.size() -1]
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
									
									if(StringUtil.isNotEmpty(current_ReeferCargoSpec?.DehumidityPercentage) && current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBFPERMIN'){
										'E488_08' current_ReeferCargoSpec?.DehumidityPercentage
									}
									if(StringUtil.isNotEmpty(current_ReeferCargoSpec?.DehumidityPercentage) && current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBMPERHOUR'){
										'E488_08' current_ReeferCargoSpec?.DehumidityPercentage
									}
									
									if(current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBFPERMIN' || current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBMPERHOUR' ){
										'E380_09' current_ReeferCargoSpec?.Ventilation?.Ventilation
									}else{
										'E380_09' '0'
									}
									
								}
							}
						}
					}
				
					
				///////////////////   Loop_L0-start
				//current_Body?.Cargo?.eachWithIndex{current_Cargo, cargoIndex->
				current_cargoList?.eachWithIndex{current_Cargo, cargoIndex->
						'Loop_L0' {
							'L0' {
								'E213_01' cargoIndex + 1
								def groWeigthKGS = null
								def groWeigthLBS = null
								def groWeigthTON = null
								
								if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'KGS'){
									groWeigthKGS =  current_Cargo?.GrossWeight?.Weight?.trim()
									if(groWeigthKGS?.indexOf('.') > 0){
										groWeigthKGS = blUtil.replaceZeroAfterPoint(groWeigthKGS)
									}
									if(StringUtil.isNotEmpty(groWeigthKGS)){
										'E81_04' groWeigthKGS
									}
								}
								if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'LBS'){
									groWeigthLBS =  current_Cargo?.GrossWeight?.Weight?.trim()
									if(groWeigthLBS?.indexOf('.') > 0){
										groWeigthLBS = blUtil.replaceZeroAfterPoint(groWeigthLBS)
									}
									if(StringUtil.isNotEmpty(groWeigthLBS)){
										'E81_04' groWeigthLBS
									}
								}
								if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'TON'){
									groWeigthTON = new BigDecimal(current_Cargo?.GrossWeight?.Weight).multiply(thousand).toString()
									if(groWeigthTON?.indexOf('.') > 0){
										groWeigthTON = blUtil.replaceZeroAfterPoint(groWeigthTON)
									}
									if(StringUtil.isNotEmpty(groWeigthTON)){
										'E81_04' groWeigthTON
									}
								}
								
								if(groWeigthKGS || groWeigthLBS || groWeigthTON){
									'E187_05' 'G'
								}
								
								def CBFVolume = null
								def CBMVolume = null
								def CFTVolume = null
								if(StringUtil.isNotEmpty(current_Cargo?.Volume?.Volume)){
									if(current_Cargo?.Volume?.VolumeUnit == 'CBF'){
										if(current_Cargo?.Volume?.Volume.startsWith('0.')){
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
									}
								}
								
								if(StringUtil.isNotEmpty(current_Cargo?.Packaging?.PackageQty)){
									'E80_08' current_Cargo?.Packaging?.PackageQty?.trim()
								}
								
								def excdeByPackageType = util.getConversion(TP_ID, 'BL', 'O', 'PackageUnit', current_Cargo?.Packaging?.PackageType, conn)
								if(StringUtil.isNotEmpty(current_Cargo?.Packaging?.PackageType) && StringUtil.isNotEmpty(current_Cargo?.Packaging?.PackageQty) && excdeByPackageType !=""){
									'E211_09' excdeByPackageType
								}
								
								if(groWeigthKGS){
									'E188_11' 'K'
								}else if(groWeigthLBS){
									'E188_11' 'L'
								}else if(groWeigthTON){
									'E188_11' 'K'
								}
								
								Map<String, String> trafficModeMap =  ['LCLLCL':'PP', 'FCLFCL': 'HH', 'FCLLCL' :'HP', 'LCLFCL' :'PH']
								if( current_Container?.TrafficMode!=null && current_Container?.TrafficMode!="" ){
									'E56_12'  trafficModeMap.get(current_Container?.TrafficMode?.OutBound + current_Container?.TrafficMode?.InBound)
								}
								else if( current_Body?.GeneralInformation?.TrafficMode !=null ){
									'E56_12'  trafficModeMap.get(current_Body?.GeneralInformation?.TrafficMode?.OutBound + current_Body?.GeneralInformation?.TrafficMode?.InBound)
								}
							}
							
							//split current_Cargo?.CargoDescription
							
							List<String> CargoDescription = blUtil.SplitTextWithConnector(current_Cargo?.CargoDescription, 50)
							
							List<cs.b2b.core.mapping.bean.MarksAndNumbers>  MarksAndNumbersList = current_Cargo?.MarksAndNumbers
							def numOfLoop = null
							if(CargoDescription.size() > MarksAndNumbersList.size()){
								numOfLoop = CargoDescription.size()

							}else{
								numOfLoop = MarksAndNumbersList.size()
							}
							for(int i = 0; i < numOfLoop; i++){
								if(CargoDescription[i] || util.substring(MarksAndNumbersList[i]?.MarksAndNumbersLine, 1, 48)){
									'L5' {
										'E213_01' i + 1
										if(i < CargoDescription.size() && StringUtil.isNotEmpty(CargoDescription[i])){
											'E79_02' CargoDescription[i]?.trim()
										}
										if(i < MarksAndNumbersList.size()){
											if(util.substring(MarksAndNumbersList[i].MarksAndNumbersLine, 1, 48)){
												'E87_06' util.substring(MarksAndNumbersList[i].MarksAndNumbersLine, 1, 48)?.trim()
											}
										}
									}
								}

							}
							
							current_Cargo?.DGCargoSpec?.each{current_DGCargoSpec ->
								'Loop_H1' {
									if(current_DGCargoSpec?.UNNumber){
										'H1' {
											if(current_DGCargoSpec?.UNNumber){
												'E62_01' current_DGCargoSpec?.UNNumber
											}
											'E209_02' util.substring(current_DGCargoSpec?.IMOClass, 1, 4)
											if(current_DGCargoSpec?.DGRegulator == 'IMD'){
												'E208_03' 'I'
											}
											if(current_DGCargoSpec?.ProperShippingName){
												'E64_04' util.substring(current_DGCargoSpec?.ProperShippingName, 1, 30)
											}
											'E63_05' util.substring(current_DGCargoSpec?.EmergencyContact[0]?.FirstName, 1, 24)
											if(StringUtil.isNotEmpty(current_DGCargoSpec?.IMDGPage)){
												'E200_06' util.substring(current_DGCargoSpec?.IMDGPage, 1, 6)
											}
											if(StringUtil.isNotEmpty(current_DGCargoSpec?.FlashPoint?.Temperature)){
												def temperature = util.substring(current_DGCargoSpec?.FlashPoint?.Temperature, 1, 4)
											'E77_07' util.substring(current_DGCargoSpec?.FlashPoint?.Temperature, 1, 4)
											}
											if(current_DGCargoSpec?.FlashPoint?.TemperatureUnit == 'C'){
												'E355_08' 'CE'
											}else if(current_DGCargoSpec?.FlashPoint?.TemperatureUnit == 'F'){
												'E355_08' 'FA'
											}
											if(StringUtil.isNotEmpty(current_DGCargoSpec?.PackageGroup?.Code)){
												'E254_09' current_DGCargoSpec?.PackageGroup?.Code
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
				filteredFreightCharge?.findAll{it.ChargeType == blUtil.COLLECT}.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
					}
					
				}
				
				if(sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)>0){
					'E58_05' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
				}
				
				
				sum = new BigDecimal(0)
				current_Body?.FreightCharge?.findAll{it.ChargeType == blUtil.PREPAID}?.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
					}
				}
				'E117_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
				
				
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
			
			
			///////////////////   Loop_L1-start
			filteredFreightCharge?.eachWithIndex{current_FreightCharge, FreightChargeIndex->
				
				if(current_FreightCharge?.ChargeType == blUtil.COLLECT){
					if(current_FreightCharge?.TotalAmtInPmtCurrency){
						'Loop_L1' {
							'L1' {
								'E213_01' FreightChargeIndex + 1
								if(current_FreightCharge?.FreightRate){
									'E60_02' current_FreightCharge?.FreightRate
								}
								
								def calculateMethod = current_FreightCharge?.CalculateMethod?.toUpperCase()
								List<String> calculateMethodList = ['CONTAINER','GROSS CARGO WEIGHT','NET CARGO WEIGHT','LUMP SUM','MEASUREMENT','PACKAGE']
								List<String> otherVar = ['PA','LS','VM','WM','PU','CS']
								
								if(calculateMethod?.toUpperCase() == 'CONTAINER' || calculateMethod?.charAt(0) == '2' || calculateMethod?.charAt(0) == '4'){
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
								}else if(calculateMethod?.charAt(0) == 'B'){
									'E122_03' 'CS'
								}else if(otherVar.contains(calculateMethod?.trim())){
									'E122_03' calculateMethod
								}
								
								if(current_FreightCharge?.ChargeType == blUtil.COLLECT){
									if(current_FreightCharge?.TotalAmtInPmtCurrency){
										'E58_04' blUtil.replaceZeroAfterPoint(new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency.toString()).multiply(hundred).toString())
									}
								}
								if(current_FreightCharge?.ChargeType == blUtil.PREPAID){
									if(current_FreightCharge?.TotalAmtInPmtCurrency){
										'E117_06' new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency.toString()).multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP).toString()
									}
								}else{
									'E117_06' '0'
								}
								
								Map<String, String> chargeTypeExtCDE = blUtil.getB2BCdeConversion(current_FreightCharge?.ChargeCode, TP_ID, 'ChargeType', conn)
								if(StringUtil.isNotEmpty(current_FreightCharge?.ChargeCode) && StringUtil.isNotEmpty(chargeTypeExtCDE.get("EXT_CDE")) ){
									'E150_08' chargeTypeExtCDE.get("EXT_CDE")
								}
								
								if(current_FreightCharge?.ChargeType == blUtil.COLLECT){
									'E16_11' 'E'
								}else{
									'E16_11' 'P'
								}
							}
							'C3' {
								if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency){
									'E100_01' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency
								}
								
							}
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

			//ChargeFilter
			blUtil.filterByTotalAmtInPmtCurrencyIfZero(filteredFreightCharge, filteredFreightChargeCNTR)
			blUtil.filterByChargeType(filteredFreightCharge, filteredFreightChargeCNTR, blUtil.PREPAID)

			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			//mapping
			generateBody(current_Body,  associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml ,conn)

			//posp checking
			if(errorKeyList.isEmpty()){
				pospValidation(writer?.toString(), errorKeyList)
			}

			//EDI Syntax checking
			if(errorKeyList.isEmpty()){
				syntaxValidation(writer?.toString(), errorKeyList)
			}

			blUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
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


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
		blUtil.checkSealNumberLength(current_Body, 15, true, null, errorKeyList)
		blUtil.filterDELinTxnAction(current_Body, false, null, errorKeyList)
		blUtil.fNDCountryChecker(current_Body, true, "is not US or CA", errorKeyList, ["UNITED STATES","CA","CANADA","US"])
		blUtil.noL3(true, null, errorKeyList,current_Body)
		
		return errorKeyList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){


		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")

		blUtil.checkL1max(node, 20, true, "L1 segment more than 20.", errorKeyList)
		blUtil.checkL106Length(node, 9, true, null, errorKeyList)
		blUtil.checkL102L106L307Checker_ACUCUBE(node,true, null, errorKeyList)
		blUtil.checkH1_FlashPoint(node,true, null, errorKeyList)
	}

	
	void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){
		
		
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")
		
		blUtil.checkB309_B310_P(node, true, null, errorKeyList)
		blUtil.checkC301_M(node, true, null, errorKeyList)
		blUtil.checkW0902_W0903_P(node, true, null, errorKeyList)
		blUtil.checkV101_V102_R(node, true,"At least one of V101, V102 is required.", errorKeyList)
		blUtil.checkR402_R403_P(node, true, null, errorKeyList)
		blUtil.checkN101_N102_M(node, true, null, errorKeyList)   
		blUtil.checkN102_N103_R(node, true, null, errorKeyList)   
		blUtil.checkN703_N704_P(node, true, null, errorKeyList)
		blUtil.checkN406_N405_C(node, true, null, errorKeyList)
		blUtil.checkC803_C802_R(node, true, null, errorKeyList)
		blUtil.checkG6103_G6104_P(node, true, null, errorKeyList)
		blUtil.checkH101_M(node, true, null, errorKeyList)
		blUtil.checkH107_H108_P(node, true, null, errorKeyList)
		blUtil.checkL301_L302_P(node, true, null, errorKeyList)
		blUtil.checkL309_L310_P(node, true, null, errorKeyList)
		blUtil.checkL312_L301_C(node, true, null, errorKeyList)
		blUtil.checkN901_N902_M(node, true, null, errorKeyList)
		blUtil.checkN902_N903_R(node, true, null, errorKeyList)
	}
}

