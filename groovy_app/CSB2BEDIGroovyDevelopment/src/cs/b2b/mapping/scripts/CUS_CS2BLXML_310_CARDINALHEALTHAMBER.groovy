package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

/**
* @author RENGA
* @pattern after TP=SDV
*/
public class CUS_CS2BLXML_310_CARDINALHEALTHAMBER {

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

	BigDecimal rateLBS = new BigDecimal('0.4535')
	BigDecimal rateTon = new BigDecimal('1000')

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
					if (current_Body?.GeneralInformation?.BLNumber?.startsWith(current_Body?.GeneralInformation?.SCACCode)) {
						'E76_02' current_Body?.GeneralInformation?.BLNumber?.trim()
					} else {
						'E76_02' current_Body?.GeneralInformation?.SCACCode + current_Body?.GeneralInformation?.BLNumber?.trim()
					}
				}

				'E145_03' 'N/A'
				'E146_04' 'CC'

				SimpleDateFormat soutfmt = new SimpleDateFormat(xmlDateTimeFormat)
				'E373_06' util.convertDateTime(soutfmt.format(new Date()), xmlDateTimeFormat, yyyyMMdd)
				
				sum = new BigDecimal(0);
				filteredFreightCharge?.findAll{it?.ChargeType==blUtil.COLLECT && it?.TotalAmtInPmtCurrency}?.each{current_FreightCharge ->
					sum = sum + current_FreightCharge?.TotalAmtInPmtCurrency?.toString()?.toBigDecimal()
				}
				'E193_07' blUtil.replaceZeroAfterPoint((sum.setScale(2,BigDecimal.ROUND_HALF_UP) * hundred).toString())
		
				def alocDT = null
				def elocDT = null
				if(current_Body?.Route?.Haulage?.InBound == "M" || current_Body?.Route?.Haulage?.InBound == "C"){
					if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT){
						'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E374_10' '035'
					}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E374_10' '017'
					}
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
			Map<String, String> eventDescriptionMap = ['NEW':'00', 'UPDATE':'05']
			'B2A' {
				if(eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)){
					'E353_01' eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)
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
						'E127_02' util.substring(current_BookingNumber?.trim() ,1 ,30)
					}
				}
			}
			
			//Loop by BLNum ONCE
			if(N9PositionIndex < 15){
				N9PositionIndex = N9PositionIndex + 1
				'N9' {
					'E128_01' 'BM'
					if (current_Body?.GeneralInformation?.BLNumber?.startsWith(current_Body?.GeneralInformation?.SCACCode)) {
						'E127_02' util.substring(current_Body?.GeneralInformation?.BLNumber?.trim(),1 ,30)
					} else {
						'E127_02' util.substring(current_Body?.GeneralInformation?.SCACCode + current_Body?.GeneralInformation?.BLNumber?.trim(),1 ,30)
					}
				}
			}
			
			def vCarrRatetTy= null
			def vItemCde = null
			Map<String, String> referenceCodeCS2Map = ['SC':'CT','SID':'SI']
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
								'E127_02' util.substring(current_ExternalReference.ReferenceNumber?.toUpperCase()?.trim(),1 ,30)
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
					if(N9PositionIndex < 15){
						N9PositionIndex = N9PositionIndex + 1
						'N9' {
							'E128_01' vItemCde
							'E127_02' util.substring(current_CarrierRate.CarrierRateNumber?.trim()?.toUpperCase(),1 ,30)
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
			'V1' {
				if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber){
					'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 8)
				}else if(firstOceanLeg?.SVVD?.Loading?.CallSign){
					'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.CallSign, 1, 8)
				}
				if(firstOceanLeg?.SVVD?.Loading?.VesselName || firstOceanLeg?.SVVD?.Loading?.LloydsNumber || firstOceanLeg?.SVVD?.Loading?.CallSign){
					'E182_02' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName, 1, 28)
				}

				if(firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction){
					if(firstOceanLeg?.SVVD?.Loading?.Direction){
						'E55_04' util.substring((firstOceanLeg?.SVVD?.Loading?.Voyage + firstOceanLeg?.SVVD?.Loading?.Direction) , 1 , 4)
					}else{
						'E55_04' util.substring(firstOceanLeg?.SVVD?.Loading?.Voyage, 1, 4)
					}
				}
				'E140_05' current_Body?.GeneralInformation?.SCACCode
				if(util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber,1,8)){
					'E897_08' 'L'
				}else if(util.substring(firstOceanLeg?.SVVD?.Loading?.CallSign,1,8)){
					'E897_08' 'C'
				}
			}

			'C3' {
				'E100_01' 'USD'
			}

			///////////////////   N1-start
			Map<String, String> partyTypeMap = ['CGN':'CN','FWD':'FW','NPT':'N1','ANP':'N2', 'SHP':'SH']
			current_Body?.Party?.each{current_Party ->
				if(partyTypeMap.get(current_Party?.PartyType)){
					'Loop_N1' {
						'N1' {
							'E98_01' partyTypeMap.get(current_Party?.PartyType)
							if (current_Party?.PartyName) {
								'E93_02' util.substring(current_Party?.PartyName, 1, 60)?.trim()
							}
							if (current_Party?.CarrierCustomerCode) {
								'E66_03' '25'
							}
							if (current_Party?.CarrierCustomerCode) {
								'E67_04' current_Party?.CarrierCustomerCode
							}
						}
						def vAddressLines = ''
						current_Party?.Address?.AddressLines?.AddressLine?.each { current_addressLine ->
							vAddressLines = vAddressLines + " " + current_addressLine?.trim()
						}
						if (StringUtil.isNotEmpty(vAddressLines)) {
							'N3' {
								'E166_01' util.substring(vAddressLines?.trim(), 1, 55)?.trim()
								'E166_02' util.substring(vAddressLines?.trim(), 56, 55)?.trim()
							}
							if (vAddressLines?.length() > 110) {
								'N3' {
									'E166_01' util.substring(vAddressLines?.trim(), 111, 55)?.trim()
									'E166_02' util.substring(vAddressLines?.trim(), 166, 55)?.trim()
								}
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
			if(StringUtil.isNotEmpty(POR?.CityDetails?.LocationCode?.UNLocationCode) || StringUtil.isNotEmpty(POR?.CityDetails?.City) ||
					StringUtil.isNotEmpty(POR?.CityDetails?.LocationCode?.SchedKDType) || StringUtil.isNotEmpty(POR?.CityDetails?.LocationCode?.SchedKDCode)){
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
								'E156_08' util.substring(POR?.CSStandardCity?.CSStateCode,1,2)
							}
						}
					}
					if(current_Body?.Route?.FullReturnCutoffDT?.LocDT) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
				}
			}
			if(StringUtil.isNotEmpty(POL?.Port?.LocationCode?.UNLocationCode) || StringUtil.isNotEmpty(POL?.Port?.PortName) || StringUtil.isNotEmpty(POL?.Port?.City) ||
					StringUtil.isNotEmpty(POL?.Port?.LocationCode?.SchedKDType) || StringUtil.isNotEmpty(POL?.Port?.LocationCode?.SchedKDCode)){
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
								'E156_08'util.substring(POL?.CSStateCode,1,2)
							}
						}
					}
					if(oceanLegSize > 0){
						if(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
							'DTM' {
								'E374_01' '140'
								'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						}else if(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'DTM' {
								'E374_01' '139'
								'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						}
					}
				}
			}
			//POD
			if(StringUtil.isNotEmpty(POD?.Port?.LocationCode?.UNLocationCode) || StringUtil.isNotEmpty(POD?.Port?.PortName) || StringUtil.isNotEmpty(POD?.Port?.City) ||
					StringUtil.isNotEmpty(POD?.Port?.LocationCode?.SchedKDType) || StringUtil.isNotEmpty(POD?.Port?.LocationCode?.SchedKDCode)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'D'
						if(POD){
							if(POD?.Port?.locationCode?.UNLocationCode){
								'E309_02' 'UN'
								'E310_03' POD?.Port?.locationCode?.UNLocationCode
							}else if(POD?.Port?.LocationCode?.SchedKDCode){
								'E309_02' POL?.Port?.LocationCode?.SchedKDType	//should be tibco bug
								'E310_03' POL?.Port?.LocationCode?.SchedKDCode	//should be tibco bug
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
								'E156_08' util.substring(POD?.CSStateCode,1,2)
							}
						}
					}
					if(oceanLegSize > 0){
						if(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
							'DTM' {
								'E374_01' '140'
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						}else if(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'DTM' {
								'E374_01' '139'
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						}
					}

				}
			}
			//FND
			if(StringUtil.isNotEmpty(FND?.CityDetails?.LocationCode?.UNLocationCode) || StringUtil.isNotEmpty(FND?.CityDetails?.City) ||
					StringUtil.isNotEmpty(FND?.CityDetails?.LocationCode?.SchedKDType) || StringUtil.isNotEmpty(FND?.CityDetails?.LocationCode?.SchedKDCode)){
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
								'E156_08' util.substring(FND?.CSStandardCity?.CSStateCode,1,2)
							}
						}
					}

					if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator?.equals('A')}?.LocDT){
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}else if((current_Body?.Route?.Haulage?.InBound == "M" || current_Body?.Route?.Haulage?.InBound == "C") && current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}else if(oceanLegSize > 0 && current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}else if(oceanLegSize > 0 && current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
				}
			}

			'R2' {
				'E140_01' current_Body?.GeneralInformation?.SCACCode
				'E133_02' 'O'
				Map<String, String> trafficModeMap =  ['LCLLCL':'PH', 'FCLFCL': 'HH', 'FCLLCL' :'HP', 'LCLFCL' :'PP']
				if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.OutBound) && StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.InBound)){
					'E56_12' trafficModeMap.get(current_Body?.GeneralInformation?.TrafficMode?.OutBound + current_Body?.GeneralInformation?.TrafficMode?.InBound)
				}

			}

			///////////////////   LX-start
			Map<String, String> weightUnitCodeMap = ["TON":"K","KGS":"K","LBS":"L"]
			Map<String, String> volumnUnitCodeMap = ["CBF":"E","CFT":"E","CBM":"X"]
			associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
				List<Container> containerGroup = current_Body.Container.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.CS1ContainerSizeType == current_Container?.CS1ContainerSizeType}
				'Loop_LX' {
					'LX' {
						 'E554_01' Container_index +1 
					}
					'Loop_N7' {

						if(util.substring(current_Container?.ContainerNumber, 5, 10) || blUtil.isLCL(current_Body)){
							'N7' {
								if(!current_Container?.ContainerNumber?.startsWith("Dummy")) {
									'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
									'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10)
								}
								String varWeight1 = null
								def varWeight2 = null
								if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight)){
									varWeight1 = (current_Container?.GrossWeight?.Weight?.toBigDecimal() * thousand)?.toString()
								}else if(StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
									varWeight1 = current_Container?.GrossWeight?.Weight
								}
								if(varWeight1 && varWeight1 != "0" && weightUnitCodeMap.containsKey(current_Container?.GrossWeight?.WeightUnit)){
									'E81_03' util.substring(blUtil.replaceZeroAfterPoint(varWeight1), 1, 11)
									'E187_04' 'G'
								}

								'E140_12' current_Body?.GeneralInformation?.SCACCode
								if(varWeight1  && varWeight1 != "0"){
									'E188_17' weightUnitCodeMap.get(current_Container?.GrossWeight?.WeightUnit?.toUpperCase())
								}
								'E761_18' util.substring(current_Container?.ContainerCheckDigit, 1, 1)

								if(current_Container?.CS1ContainerSizeType){
									def ExtCDE = util.getConversionWithScac(TP_ID, 'BL', 'O', 'ContainerType', current_Container?.CS1ContainerSizeType,current_Body?.GeneralInformation?.SCACCode, conn)
									if(ExtCDE){
										'E24_22' ExtCDE
									}else{
										'E24_22' current_Container?.CS1ContainerSizeType
									}
								}
								'E140_23' current_Body?.GeneralInformation?.SCACCode
							}
						}
						if(util.substring(current_Container?.ContainerNumber, 5, 10) && !blUtil.isLCL(current_Body)){
							//select * from b2b_cde_conversion t where t.convert_type_id = 'CTNRPIECECOUNTUNIT' and t.dir_id = 'O' and t.msg_type_id = 'BL' and t.msg_fmt_id = 'X.12' and t.tp_id = 'HDYOW'
							//DB has no result
							if(current_Container?.PieceCount?.PieceCount && current_Container?.PieceCount?.PieceCountUnit){
								'QTY' {
									'E673_01' '39'
									if(current_Container?.PieceCount?.PieceCount != "0"){
										'E380_02' current_Container?.PieceCount?.PieceCount
									}
									'C001_03' {
										if(current_Container?.PieceCount?.PieceCountUnit){
											'E355_01' current_Container?.PieceCount?.PieceCountUnit?.trim()
										}else{
											'E355_01' 'UN'
										}
									}
								}
							}

							int sealNumberIndex = 0
							current_Container?.each{ currentSubContainer ->
								currentSubContainer.Seal?.each{current_Seal ->
									if(sealNumberIndex < 5 && current_Seal?.SealNumber && current_Seal?.SealNumber?.trim()?.length() >= 2 && current_Seal?.SealType?.length() >= 2){
										sealNumberIndex++
										'M7' {
											'E225_01' current_Seal?.SealNumber?.trim()
											
											Map<String,String> sealTypeMap = ['OT':'ZZ']
											if(sealTypeMap.keySet().contains(current_Seal?.SealType)){
												'E98_05' sealTypeMap.get(current_Seal?.SealType)
											}else if(current_Seal?.SealType){
												'E98_05' current_Seal?.SealType
											}
										}
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
								BigDecimal vWeigth = null

								if(current_Cargo?.GrossWeight?.WeightUnit && current_Cargo?.GrossWeight?.Weight?.toBigDecimal() > 0){
									vWeigth =  current_Cargo?.GrossWeight?.Weight?.toBigDecimal()
									if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'TON'){
										vWeigth = vWeigth?.multiply(rateTon)
									}
								}
								if(vWeigth && vWeigth > BigDecimal.ZERO){
									'E81_04' util.substring(blUtil.replaceZeroAfterPoint(vWeigth?.toString()),1,10)
									'E187_05' 'G'
								}

								def CBFVolume = null
								def CBMVolume = null
								def CFTVolume = null
								if(StringUtil.isNotEmpty(current_Cargo?.Volume?.Volume) && volumnUnitCodeMap.containsKey(current_Cargo?.Volume?.VolumeUnit)){
									'E183_06' util.substring(current_Cargo?.Volume?.Volume, 1, 8)?.replaceAll("\\.\$","")
									'E184_07' volumnUnitCodeMap.get(current_Cargo?.Volume?.VolumeUnit)
								}

								if(StringUtil.isNotEmpty(current_Cargo?.Packaging?.PackageType)){
									if(current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)} && current_Cargo?.CargoNature == 'GC'){
										'E80_08' util.substring(current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)}?.PackageUnitQuantity, 1, 7)
									}else if(current_Cargo?.ReeferCargoSpec?.find{it?.RFPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)}} && ['RF','RD'].contains(current_Cargo?.CargoNature)){
										'E80_08' util.substring(current_Cargo?.ReeferCargoSpec?.find{it?.RFPackageUnit?.find{it?.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it?.PackageUnitQuantity)}}?.RFPackageUnit?.get(0)?.PackageUnitQuantity, 1, 7)
									}else if(current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)} && ['RF','RD'].contains(current_Cargo?.CargoNature)){
										'E80_08' util.substring(current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)}?.PackageUnitQuantity, 1, 7)
									}else if(current_Cargo?.DGCargoSpec?.find{it?.DGPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)}} && ['DG','RD','AD'].contains(current_Cargo?.CargoNature)){
										'E80_08' util.substring(current_Cargo?.DGCargoSpec?.find{it?.DGPackageUnit?.find{it?.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it?.PackageUnitQuantity)}}?.DGPackageUnit?.get(0)?.PackageUnitQuantity, 1, 7)
									}else if(current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)} && ['DG','RD','AD'].contains(current_Cargo?.CargoNature)){
										'E80_08' util.substring(current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)}?.PackageUnitQuantity, 1, 7)
									}else if(current_Cargo?.AWCargoSpec?.find{it?.AWPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)}} && ['AW','AD'].contains(current_Cargo?.CargoNature)){
										'E80_08' util.substring(current_Cargo?.AWCargoSpec?.find{it?.AWPackageUnit?.find{it?.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it?.PackageUnitQuantity)}}?.AWPackageUnit?.get(0)?.PackageUnitQuantity, 1, 7)
									}else if(current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)} && ['AW','AD'].contains(current_Cargo?.CargoNature)){
										'E80_08' util.substring(current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1' && StringUtil.isNotEmpty(it.PackageUnitQuantity)}?.PackageUnitQuantity, 1, 7)
									}else if(current_Cargo?.Packaging?.PackageQty){
										'E80_08' util.substring(current_Cargo?.Packaging?.PackageQty, 1, 7)
									}
								}

								Map<String,String> packageTypeMap = util.getCdeConversionFromIntCde(TP_ID, 'BL', 'O', null, 'X.12', 'PackageUnit', current_Cargo?.Packaging?.PackageType, conn)
								'E211_09' packageTypeMap.get("EXT_CDE") ? packageTypeMap.get("EXT_CDE") : 'PCS'

								if(vWeigth && vWeigth > BigDecimal.ZERO){
									'E188_11' weightUnitCodeMap.get(current_Cargo?.GrossWeight?.WeightUnit?.trim())
								}
								Map<String, String> trafficModeMap =  ['LCLLCL':'PP', 'FCLFCL': 'HH', 'FCLLCL' :'HP', 'LCLFCL' :'PH']
								if(StringUtil.isNotEmpty(current_Container?.TrafficMode?.InBound) && StringUtil.isNotEmpty(current_Container?.TrafficMode?.OutBound)){
									'E56_12'  trafficModeMap.get(current_Container?.TrafficMode?.OutBound + current_Container?.TrafficMode?.InBound)
								}else if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.OutBound) && StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.InBound)){
									'E56_12'  trafficModeMap.get(current_Body?.GeneralInformation?.TrafficMode?.OutBound + current_Body?.GeneralInformation?.TrafficMode?.InBound)
								}
							}

							//split current_Cargo?.CargoDescription

	//							List<String> CargoDescription = blUtil.SplitTextWithConnector(current_Cargo?.CargoDescription, 50)
							List<String> CargoDescription = splitStringByLength(current_Cargo?.CargoDescription?.trim(), 50)

							List<cs.b2b.core.mapping.bean.MarksAndNumbers>  MarksAndNumbersList = current_Cargo?.MarksAndNumbers
							def numOfLoop = null
							if(CargoDescription.size() > MarksAndNumbersList.size()){
								numOfLoop = CargoDescription.size()

							}else{
								numOfLoop = MarksAndNumbersList.size()
							}
							for(int i = 0; i < numOfLoop; i++){
								if(CargoDescription[i]){
									'L5' {
										'E213_01' i + 1
										if(i < CargoDescription.size() && StringUtil.isNotEmpty(CargoDescription[i])){
											'E79_02' CargoDescription[i]?.trim()
										}
										if(i < MarksAndNumbersList.size()){
											if(util.substring(MarksAndNumbersList[i].MarksAndNumbersLine, 1, 48)){
												'E87_06' util.substring(MarksAndNumbersList[i].MarksAndNumbersLine, 1, 48)
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
			BigDecimal sumL311 = new BigDecimal(0)
			if(current_Body?.Cargo?.find{it?.CargoNature != "RF" && it.CargoNature != "DG" && it?.Packaging?.PackageQty}){
				current_Body?.Cargo?.findAll{it?.CargoNature != "RF" && it.CargoNature != "DG" && it?.Packaging?.PackageQty}?.each{current_Cargo ->
					sumL311 = sumL311 + current_Cargo?.Packaging?.PackageQty?.toBigDecimal()
				}
			}else if(current_Body?.Cargo?.find{it?.GCPackageUnit?.find{it?.PackageSeqNumber == "1" && StringUtil.isNotEmpty(it?.PackageUnitQuantity)}}){
				current_Body?.Cargo?.each{current_Cargo ->
					current_Cargo?.GCPackageUnit?.findAll{it?.PackageSeqNumber == "1" && StringUtil.isNotEmpty(it?.PackageUnitQuantity)}?.each {current_GCPackage ->
						sumL311 = sumL311 + current_GCPackage?.PackageUnitQuantity?.toBigDecimal()
					}
				}
			}
			if(current_Body?.Cargo?.find{it?.CargoNature == "RF" && it?.Packaging?.PackageQty}){
				current_Body?.Cargo?.findAll{it?.CargoNature == "RF" && it?.Packaging?.PackageQty}?.each{current_Cargo ->
					sumL311 = sumL311 + current_Cargo?.Packaging?.PackageQty?.toBigDecimal()
				}
			}
			if(current_Body?.Cargo?.find{it?.CargoNature == "DG" && it?.Packaging?.PackageQty}){
				current_Body?.Cargo?.findAll{it?.CargoNature == "DG" && it?.Packaging?.PackageQty}?.each{current_Cargo ->
					sumL311 = sumL311 + current_Cargo?.Packaging?.PackageQty?.toBigDecimal()
				}
			}

			'L3' {

				BigDecimal vGWeight = BigDecimal.ZERO
				BigDecimal vNWeight = BigDecimal.ZERO
				if (util.isDecimal(current_Body?.GeneralInformation?.BLGrossWeight?.Weight)) {
					vGWeight = vGWeight.add(current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.toBigDecimal())
				}
				if (util.isDecimal(current_Body?.GeneralInformation?.BLNetWeight?.Weight)) {
					vNWeight = vGWeight.add(current_Body?.GeneralInformation?.BLNetWeight?.Weight?.toBigDecimal())
				}

				if (vGWeight > 0) {
					'E81_01' blUtil.replaceZeroAfterPoint(vGWeight?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString())
					'E187_02' 'G'
				} else if (vNWeight > 0) {
					'E81_01' blUtil.replaceZeroAfterPoint(vNWeight?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString())
					'E187_02' 'N'
				}

				sum = new BigDecimal(0)
				filteredFreightCharge?.findAll{it?.ChargeType==blUtil.COLLECT && it?.TotalAmtInPmtCurrency}?.each{current_FreightCharge ->
					sum = sum + current_FreightCharge?.TotalAmtInPmtCurrency?.toString()?.toBigDecimal()
				}
//				if(sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)>0){
					'E58_05' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
//				}

				sum = new BigDecimal(0)
				current_Body?.FreightCharge?.findAll{it?.ChargeType == blUtil.PREPAID && it.ChargeCode != 'DET' && it?.TotalAmtInPmtCurrency}?.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
					}
				}
				'E117_07' util.substring((sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString(), 1, 8);

				if (current_Body?.GeneralInformation?.BLVolume?.Volume && new BigDecimal(current_Body?.GeneralInformation?.BLVolume?.Volume) > 0) {
					'E183_09' current_Body?.GeneralInformation?.BLVolume?.Volume
					if (current_Body?.GeneralInformation?.BLVolume?.VolumeUnit == 'CBF') {
						'E184_10' 'E'
					} else if (current_Body?.GeneralInformation?.BLVolume?.VolumeUnit == 'CFT') {
						'E184_10' 'E'
					} else if (current_Body?.GeneralInformation?.BLVolume?.VolumeUnit == 'CBM') {
						'E184_10' 'X'
					}
				}


				if (sumL311 > BigDecimal.ZERO) {
					'E80_11' util.substring(sumL311.toString(), 1, 7);
				}

				if (vGWeight || vNWeight){
					if (current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit) {
						if (current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit == 'TON') {
							'E188_12' 'K'
						} else if (current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit == 'KGS') {
							'E188_12' 'K'
						} else if (current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit == 'LBS') {
							'E188_12' 'L'
						}
					} else if (current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit) {
						if (current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit == 'TON') {
							'E188_12' 'K'
						} else if (current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit == 'KGS') {
							'E188_12' 'K'
						} else if (current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit == 'LBS') {
							'E188_12' 'L'
						}
					}
				}
			}

			
			///////////////////   Loop_L1-start
			filteredFreightCharge?.eachWithIndex{current_FreightCharge, FreightChargeIndex->
				if(FreightChargeIndex < 20){
					'Loop_L1' {
						'L1' {
							'E213_01' FreightChargeIndex + 1
							if(current_FreightCharge?.FreightRate && current_FreightCharge?.FreightRate != '0') {
								String freightRate = current_FreightCharge?.FreightRate
								if (freightRate?.indexOf(".") > -1) {
									NumberFormat formatter = new DecimalFormat("######.000")
									freightRate = blUtil.replaceZeroAfterPoint(formatter.format((freightRate?.toBigDecimal()))).replaceFirst("^0\\.", ".")
								}
								'E60_02' util.substring(freightRate?.replace("-",""), 1, 9)
							}

							Map<String, String> CalcMethodMap = ['2':'PA','4':'PA','B':'CS','CONTAINER':'PA','CS':'CS','GROSS CARGO WEIGHT':'WM',
																 'LS':'LS','LUMP SUM':'LS','MEASUREMENT':'VM','NET CARGO WEIGHT':'WM','PA':'PA',
																 'PACKAGE':'PU','PU':'PU','VM':'VM','WM':'WM']

							if(current_FreightCharge?.CalculateMethod?.startsWith("2") || current_FreightCharge?.CalculateMethod?.startsWith("4") ||
								current_FreightCharge?.CalculateMethod?.startsWith("B")){
								'E122_03' CalcMethodMap.get(util.substring(current_FreightCharge?.CalculateMethod, 1 , 1))
							}else{
								'E122_03' CalcMethodMap.get(current_FreightCharge?.CalculateMethod?.toUpperCase())
							}

							if(current_FreightCharge?.ChargeType == blUtil.COLLECT && current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency && current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency != "0"){
								'E58_04' amountFormat(current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)?.toBigDecimal()?.toString()
							}

							Map<String, String> chargeTypeExtCDE = util.getCdeConversionFromIntCde(TP_ID,"BL","O",null,null,"ChargeType",util.substring(current_FreightCharge?.ChargeCode?.toUpperCase(),1,3), conn)
							if(StringUtil.isNotEmpty(current_FreightCharge?.ChargeCode) && StringUtil.isNotEmpty(chargeTypeExtCDE.get("EXT_CDE")) ){
								'E150_08' chargeTypeExtCDE.get("EXT_CDE")
							}else{
								'E150_08' util.substring(current_FreightCharge?.ChargeCode?.toUpperCase(),1,3)
							}

							if(current_FreightCharge?.ChargeType == '0'){
								'E16_11' 'E'
							}else{
								'E16_11' 'P'
							}

							'E276_12' util.substring(current_FreightCharge?.ChargeDesc, 1, 25)
							'E220_17' current_FreightCharge?.Basis
							if(current_FreightCharge?.CalculateMethod?.toUpperCase() == "LUMP SUM"){
								'E221_18' 'FR'
							}else{
								'E221_18' 'NR'
							}
						}
						if(current_FreightCharge?.ChargeAmount?.attr_Currency){
							'C3' {
								'E100_01' current_FreightCharge?.ChargeAmount?.attr_Currency
								if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.indexOf(".") > -1){
									if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.startsWith("0.")){
										String exchangeRate = current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.substring(1)
										if(exchangeRate?.length() < 4) {
											'E280_02' exchangeRate + String.format("%0${(5 - exchangeRate.length())}d", 0)
										}else{
											'E280_02' exchangeRate
										}
									}else if (current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.length() < 4){
										String exchangeRate = current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate
										'E280_02' exchangeRate + String.format("%0${(5-exchangeRate.length())}d",0)
									}else{
										'E280_02' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate
									}
								}else{
									if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.length() >= 4){
										'E280_02' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate
									}else if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.length() == 1){
										'E280_02' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate + ".000"
									}else{
										'E280_02' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate + ".00"
									}
								}
								'E100_03' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency
							}
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
//				blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)

			//ChargeFilter
//			blUtil.filterByTotalAmtInPmtCurrencyIfZero(filteredFreightCharge, filteredFreightChargeCNTR)
//			blUtil.filterByTotalAmtInPmtCurrencyIfNegative(filteredFreightCharge, filteredFreightChargeCNTR)
			blUtil.filterByChargeType(filteredFreightCharge, filteredFreightChargeCNTR, blUtil.PREPAID)
			blUtil.filterByChargeCodeBlock(filteredFreightCharge, filteredFreightChargeCNTR, ['DET'])

			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			//mapping
			if(errorKeyList.isEmpty()) {
				generateBody(current_Body, associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml, conn)
			}

			//posp checking
			if(errorKeyList.isEmpty()){
				pospValidation(writer?.toString(), errorKeyList)
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


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		blUtil.sendStatusOfEDI(current_Body, true, null, errorKeyList)

		return errorKeyList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){


		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")

		blUtil.checkL0Max(node, 120, true , "L0 cannot support cargo more than 120" ,errorKeyList)
		blUtil.checkN101_M(node, true, "N101 and N102 are mandatory.", errorKeyList)
		blUtil.checkN102_M(node, true, "N101 and N102 are mandatory.", errorKeyList)
		blUtil.checkL3_M(node, false, null, errorKeyList)
		blUtil.checkMissingLX(node, true, "131Missing BL-CONTAINER", errorKeyList)
		blUtil.checkCollectPayment(node, false, "Zero total charge amount not allowed.", errorKeyList)
//		blUtil.checkPOLNotUSCA(node, false, null, errorKeyList)

	}

	
	void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){
		
		
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")
		
		blUtil.checkB309_B310_P(node, true, null, errorKeyList)
		blUtil.checkDTM02_DTM03_DTM_05_R(node, true, null, errorKeyList)
		blUtil.checkDTM04_DTM03_C(node, true, null, errorKeyList)
		blUtil.checkL004_L005_P(node, true, null, errorKeyList)
		blUtil.checkL006_L007_P(node, true, null ,errorKeyList)
		blUtil.checkL011_L004_C(node, true, null ,errorKeyList)
		blUtil.checkL102_L103_P(node, true, "131Either L102 L103 exists then all are required.", errorKeyList)
		blUtil.checkL104_L105_L106_R(node, true, "131At least one of L104,L105,L106 required.", errorKeyList)
		blUtil.checkL301_L302_P(node, true, null, errorKeyList)
		blUtil.checkL309_L310_P(node, true, null, errorKeyList)
		blUtil.checkL312_L301_C(node, true, null, errorKeyList)
		blUtil.checkN102_N103_R(node, true, null, errorKeyList)
		blUtil.checkN103_N104_P(node, true, null, errorKeyList)
		blUtil.checkN703_N704_P(node, true, "131Either N703 N704 exists then all are required.", errorKeyList)
		blUtil.checkL008_L009_P(node, true, "131Either L008 L009 exists then all are required.", errorKeyList)
		blUtil.checkQTY02_QTY04_C(node, true, "131At least one of QTY02 or QTY04 is required.", errorKeyList)
		blUtil.checkV101_V102_R(node, true, "131At least one of V101, V102 is required.", errorKeyList)
		blUtil.checkV108_V101_C(node, true, null, errorKeyList)
	}

	private List<String> splitStringByLength(String stringLine, int splitLength){

		List<String> arraylistOfStrings = []

		if(stringLine){
			double count = (stringLine.length() / splitLength);
			if( ! (count + "").endsWith(".0")){
				count = count + 1;
			}
			int numberOfLine = (int)count;
			String originalStr = stringLine;
			arraylistOfStrings = new String[numberOfLine];

			for(int i = 0; i < numberOfLine; i++){
				//Cut for each 55
				if(originalStr.length() > splitLength){
					arraylistOfStrings[i] = originalStr.substring(0, splitLength);
					originalStr = originalStr.substring(splitLength);
				}else{
					arraylistOfStrings[i] = originalStr.substring(0, originalStr.length());
					originalStr = originalStr.substring(originalStr.length());
				}
			}
		}

		return arraylistOfStrings
	}

	private amountFormat(String amount){
		String result = "";
		if (amount && !amount.equals("")) {
			amount = amount.replace("+","").replace("-","")
			if (amount.indexOf(".") >= 0) {
				result = amount.substring(amount.indexOf(".") + 1);
				if (result.length() == 2) {
					result = amount.replaceAll("[.]", "");
				} else if (result.length() == 1) {
					result = amount.replaceAll("[.]", "") + "0";
				}
			} else {
				result = amount + "00";
			}
		}
		return result
	}
}

