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
* @pattern after TP=IPPULP
*/
public class CUS_CS2BLXML_310_SDV {

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
					'E76_02' current_Body?.GeneralInformation?.BLNumber?.trim()
				}

				'E145_03' 'N/A'

				def countOfPrepaidCharge = filteredFreightCharge?.findAll{it?.ChargeType==blUtil.PREPAID && it?.IsApprovedForCustomer?.trim() != '0'}?.size()
				def countOfCollectCharge = filteredFreightCharge?.findAll{it?.ChargeType==blUtil.COLLECT && it?.IsApprovedForCustomer?.trim() != '0'}?.size()

				if(filteredFreightCharge?.findAll{it?.IsApprovedForCustomer?.trim() != "false"}?.size() == 0){
					'E146_04' 'MX'
				}else if(countOfCollectCharge == 0 ){
					'E146_04' 'PP'
				}else if(countOfPrepaidCharge == 0){
					'E146_04' 'CC'
				}else{
					'E146_04' 'MX'
				}
				
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
						'E374_10' '140'
					}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E374_10' '139'
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
			Map<String, String> eventDescriptionMap = ['NEW':'00', 'UPDATE':'04']
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
						'E127_02' util.substring(current_BookingNumber?.trim() ,1 ,30)
					}
				}
			}
			
			//Loop by BLNum ONCE
			if(N9PositionIndex < 15){
				N9PositionIndex = N9PositionIndex + 1
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(current_Body?.GeneralInformation?.BLNumber?.trim(),1 ,30)
							
				}
			}
			
			def vCarrRatetTy= null
			def vItemCde = null
			Map<String, String> referenceCodeCS2Map = ['CTR':'CT','CIN':'IN','ICR':'CR','PO':'PO',
													   'QUOT':'Q1','RP':'RP','SC':'E8','SID':'SI','SR':'SR']
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
				if(current_Body?.Route?.OceanLeg?.size() > 1){
					lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
				}
			}
			if(firstOceanLeg){
				if(lastOceanLeg == null && (firstOceanLeg?.SVVD?.Discharge?.VesselName || firstOceanLeg?.SVVD?.Discharge?.LloydsNumber || firstOceanLeg?.SVVD?.Discharge?.CallSign)) {
					'V1' {
						if(firstOceanLeg?.SVVD?.Discharge?.LloydsNumber){
							'E597_01' util.substring(firstOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 8)
						}else if(firstOceanLeg?.SVVD?.Discharge?.CallSign){
							'E597_01' util.substring(firstOceanLeg?.SVVD?.Discharge?.CallSign, 1, 8)
						}

						'E182_02' util.substring(firstOceanLeg?.SVVD?.Discharge?.VesselName, 2, 27)	//maybe a bug

						if(firstOceanLeg?.SVVD?.Discharge?.Voyage){
							if(firstOceanLeg?.DischargeDirectionName){
								'E55_04' firstOceanLeg?.SVVD?.Discharge?.Voyage + firstOceanLeg?.DischargeDirectionName
							}else{
								'E55_04' firstOceanLeg?.SVVD?.Discharge?.Voyage
							}
						}
						'E140_05' current_Body?.GeneralInformation.SCACCode
						if(util.substring(firstOceanLeg?.SVVD?.Discharge?.LloydsNumber,1,8)){
							'E897_08' 'L'
						}else if(util.substring(firstOceanLeg?.SVVD?.Discharge?.CallSign,1,8)){
							'E897_08' 'C'
						}
					}
				}else{
					'V1' {
						if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
							'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 8)
						} else if (firstOceanLeg?.SVVD?.Loading?.CallSign) {
							'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.CallSign, 1, 8)
						}
						'E182_02' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName, 1, 28)

						if (firstOceanLeg?.SVVD?.Loading?.Voyage) {
							if (firstOceanLeg?.LoadingDirectionName) {
								'E55_04' firstOceanLeg?.SVVD?.Loading?.Voyage + firstOceanLeg?.LoadingDirectionName
							} else {
								'E55_04' firstOceanLeg?.SVVD?.Loading?.Voyage
							}
						}
						'E140_05' current_Body?.GeneralInformation.SCACCode
						if (util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 8)) {
							'E897_08' 'L'
						} else if (util.substring(firstOceanLeg?.SVVD?.Loading?.CallSign, 1, 8)) {
							'E897_08' 'C'
						}
					}
				}
			}

			if(lastOceanLeg && (lastOceanLeg?.SVVD?.Discharge?.VesselName && (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber || lastOceanLeg?.SVVD?.Discharge?.CallSign))){
				'V1' {
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber){
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 8)
					}else if(lastOceanLeg?.SVVD?.Discharge?.CallSign){
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.CallSign, 1, 8)
					}
					if(lastOceanLeg?.SVVD?.Discharge?.VesselName || lastOceanLeg?.SVVD?.Discharge?.LloydsNumber || lastOceanLeg?.SVVD?.Discharge?.CallSign){
						'E182_02' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName, 1, 28)
					}

					if(lastOceanLeg?.SVVD?.Discharge?.Voyage){
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
				if(StringUtil.isNotEmpty(current_Party?.Contact?.FirstName) || StringUtil.isNotEmpty(current_Party?.Contact?.LastName)){
					vname = util.substring((FirstName!=null? FirstName  :'' ) + ' ' + (LastName!=null? LastName :'' ) , 1 , 60)
				}
				def CountryCode = current_Party?.Contact?.ContactPhone?.CountryCode
				def AreaCode = current_Party?.Contact?.ContactPhone?.AreaCode
				def phoneNumber = current_Party?.Contact?.ContactPhone?.Number
				def vnumber = null

				if(phoneNumber){
					if(StringUtil.isNotEmpty(phoneNumber) && StringUtil.isEmpty(CountryCode) && StringUtil.isEmpty(AreaCode)) {
						vnumber = phoneNumber
					}else if(StringUtil.isNotEmpty(phoneNumber) && StringUtil.isNotEmpty(CountryCode) && StringUtil.isEmpty(AreaCode)){
						vnumber = CountryCode + '- -' + phoneNumber
					}else if(StringUtil.isNotEmpty(phoneNumber) && StringUtil.isNotEmpty(CountryCode) && StringUtil.isNotEmpty(AreaCode)){
						vnumber = CountryCode + '-' + AreaCode + '-' + phoneNumber
					}

					if(StringUtil.isNotEmpty(vnumber) && StringUtil.isNotEmpty(vname)){
						if(G61index < 3){
							'G61' {
								'E366_01' 'IC'
								'E93_02' vname?.trim()
								'E365_03' 'TE'
								'E364_04' vnumber
							}
						}
					}
					G61index = G61index + 1
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
						'DTM' {
							if(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
								'E374_01' '140'
								'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
								'E374_01' '139'
								'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
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
						'DTM' {
							if(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
								'E374_01' '140'
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
							}else if(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
								'E374_01' '139'
								'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
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
					'DTM' {
						if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator?.equals('A')}?.LocDT){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if((current_Body?.Route?.Haulage?.InBound == "M" || current_Body?.Route?.Haulage?.InBound == "C") && current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(oceanLegSize > 0 && current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(oceanLegSize > 0 && current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)

						}
					}
				}
			}

			'R2' {
				'E140_01' current_Body?.GeneralInformation?.SCACCode
				'E133_02' 'O'
				Map<String, String> trafficModeMap =  ['LCLLCL':'02', 'FCLFCL': '03', 'FCLLCL' :'04', 'LCLFCL' :'05']
				if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.OutBound) && StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.InBound)){
					'E56_12' trafficModeMap.get(current_Body?.GeneralInformation?.TrafficMode?.OutBound + current_Body?.GeneralInformation?.TrafficMode?.InBound)
				}

			}

			///////////////////   C8-start
			current_Body?.BLCertClause?.findAll{StringUtil.isNotEmpty(it?.CertificationClauseType) && StringUtil.isNotEmpty(it?.CertificationClauseText)}?.each{current_BLCertClause ->
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
			Map<String, String> weightUnitCodeMap = ["TON":"K","KGS":"K","LBS":"L"]
			Map<String, String> volumnUnitCodeMap = ["CBF":"E","CFT":"E","CBM":"X"]
			associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
				List<Container> containerGroup = current_Body.Container.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.CS1ContainerSizeType == current_Container?.CS1ContainerSizeType}
				'Loop_LX' {
					'LX' {
						 'E554_01' Container_index +1 
					}
					'Loop_N7' {
						if(util.substring(containerGroup[0]?.ContainerNumber, 5, 5)){
							if(util.substring(current_Container?.ContainerNumber, 5, 10)){
								'N7' {
									'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
									'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10)
									
									def varWeight1 = null
									def varWeight2 = null
									if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight)){
										varWeight1 = (current_Container?.GrossWeight?.Weight?.toBigDecimal()?.multiply(thousand)).setScale(2,BigDecimal.ROUND_FLOOR)
									}else if(StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
										varWeight1 = current_Container?.GrossWeight?.Weight?.toBigDecimal()?.setScale(2,BigDecimal.ROUND_FLOOR)
									}
									if(varWeight1){
										'E81_03' blUtil.replaceZeroAfterPoint(varWeight1?.toString())
										'E187_04' 'G'
									}
									if(varWeight1){
										'E188_17' weightUnitCodeMap.get(current_Container?.GrossWeight?.WeightUnit?.toUpperCase())
									}
									'E761_18' util.substring(current_Container?.ContainerCheckDigit, 1, 1)
									
									if(current_Container?.CS1ContainerSizeType){
										def ExtCDE = util.getConversionWithScac(TP_ID, 'BL', 'O', 'ContainerType', current_Container?.CS1ContainerSizeType,current_Body?.GeneralInformation?.SCACCode, conn)
										if(ExtCDE){
											'E24_22' ExtCDE
										}
									}
								}
							}
							
							//select * from b2b_cde_conversion t where t.convert_type_id = 'CTNRPIECECOUNTUNIT' and t.dir_id = 'O' and t.msg_type_id = 'BL' and t.msg_fmt_id = 'X.12' and t.tp_id = 'HDYOW'
							//DB has no result
							if(current_Container?.PieceCount?.PieceCount && current_Container?.PieceCount?.PieceCountUnit){
								'QTY' {
									'E673_01' '39'
									if(current_Container?.PieceCount?.PieceCount){
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
											
											Map<String,String> sealTypeMap = ['TM':'TO', 'OT':'ZZ']
											if(sealTypeMap.keySet().contains(current_Seal?.SealType)){
												'E98_05' sealTypeMap.get(current_Seal?.SealType)
											}else if(current_Seal?.SealType){
												'E98_05' current_Seal?.SealType
											}
										}
									}
								}
							}
							if(current_Body?.Cargo[Container_index]?.ReeferCargoSpec?.size() > 0){
								cs.b2b.core.mapping.bean.bl.ReeferCargoSpec current_ReeferCargoSpec = current_Body?.Cargo[Container_index]?.ReeferCargoSpec[current_Body?.Cargo[Container_index]?.ReeferCargoSpec?.size() -1]
								'W09' {
									'E40_01' 'CZ'

									'E408_02' util.substring(current_ReeferCargoSpec?.Temperature?.Temperature, 1, 4)?.replaceAll("\\.\$","")

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

//									if(StringUtil.isNotEmpty(current_ReeferCargoSpec?.DehumidityPercentage) && current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBFPERMIN'){
//										'E488_08' current_ReeferCargoSpec?.DehumidityPercentage
//									}
//									if(StringUtil.isNotEmpty(current_ReeferCargoSpec?.DehumidityPercentage) && current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBMPERHOUR'){
//										'E488_08' current_ReeferCargoSpec?.DehumidityPercentage
//									}

									if(current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBFPERMIN' || current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBMPERHOUR' ){
										'E380_09' current_ReeferCargoSpec?.Ventilation?.Ventilation
									}
								}
							}
						}else{
							if(blUtil.isLCL(current_Body)){
								if(current_Body?.Cargo[Container_index]?.ReeferCargoSpec?.size() > 0){
									cs.b2b.core.mapping.bean.bl.ReeferCargoSpec current_ReeferCargoSpec = current_Body?.Cargo[Container_index]?.ReeferCargoSpec[current_Body?.Cargo[Container_index]?.ReeferCargoSpec?.size() -1]
									'W09' {
										'E40_01' 'CZ'

										'E408_02' util.substring(current_ReeferCargoSpec?.Temperature?.Temperature, 1, 4)?.replaceAll("\\.\$","")

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
										if(current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBFPERMIN' || current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBMPERHOUR' ){
											'E380_09' current_ReeferCargoSpec?.Ventilation?.Ventilation
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
								
								def excdeByPackageType = util.getConversion(TP_ID, 'BL', 'O', 'PackageUnit', current_Cargo?.Packaging?.PackageType, conn)
								'E211_09' excdeByPackageType ? excdeByPackageType : 'UNT'

								if(vWeigth && vWeigth > BigDecimal.ZERO){
									'E188_11' weightUnitCodeMap.get(current_Cargo?.GrossWeight?.WeightUnit?.trim())
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
								if(CargoDescription[i] || util.substring(MarksAndNumbersList[i]?.MarksAndNumbersLine, 1, 50)){
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

							int h1Count = 0
							if(current_Cargo?.CargoNature == "DG" || current_Cargo?.CargoNature == "RD" || current_Cargo?.CargoNature == "AD") {
								current_Cargo?.DGCargoSpec?.each { current_DGCargoSpec ->
									if(h1Count < 10) {
										h1Count++
										'Loop_H1' {
											if (current_DGCargoSpec?.UNNumber) {
												'H1' {
													if (current_DGCargoSpec?.UNNumber) {
														'E62_01' current_DGCargoSpec?.UNNumber
													}
													'E209_02' util.substring(current_DGCargoSpec?.IMOClass, 1, 4)?.trim()
													if (current_DGCargoSpec?.DGRegulator == 'IMD') {
														'E208_03' 'I'
													}
													if (current_DGCargoSpec?.ProperShippingName) {
														'E64_04' util.substring(current_DGCargoSpec?.ProperShippingName, 1, 30)
													}
													if (current_Cargo?.CargoNature == "DG" && current_DGCargoSpec?.EmergencyContact[0]?.ContactPhone?.Number) {
														'E63_05' util.substring(current_DGCargoSpec?.EmergencyContact[0]?.ContactPhone?.Number, 1, 24)
													} else if (current_Cargo?.CargoNature == "RD" && current_Cargo?.ReeferCargoSpec[0]?.EmergencyContact?.size() > 0 && current_Cargo?.ReeferCargoSpec[0]?.EmergencyContact[0]?.ContactPhone?.Number) {
														'E63_05' util.substring(current_DGCargoSpec?.EmergencyContact[0]?.ContactPhone?.Number, 1, 24)
													} else if (current_Cargo?.CargoNature == "AD" && current_Cargo?.AWCargoSpec[0]?.EmergencyContact?.size() > 0 && current_Cargo?.AWCargoSpec[0]?.EmergencyContact[0]?.ContactPhone?.Number) {
														'E63_05' util.substring(current_DGCargoSpec?.EmergencyContact[0]?.ContactPhone?.Number, 1, 24)
													}
													if (StringUtil.isNotEmpty(current_DGCargoSpec?.IMDGPage)) {
														'E200_06' util.substring(current_DGCargoSpec?.IMDGPage, 1, 6)
													}
													if (StringUtil.isNotEmpty(current_DGCargoSpec?.FlashPoint?.Temperature)) {
														def temperature = util.substring(current_DGCargoSpec?.FlashPoint?.Temperature, 1, 4)
//											//Element 'Loop_LX[1]/Loop_L0[1]/Loop_H1[1]/H1/E77_07' value '-99.' length (4) over max-len required (3);
//											if(temperature?.length()==4 && temperature.charAt(3)=='.'){
//												'E77_07' util.substring(current_DGCargoSpec?.FlashPoint?.Temperature, 1, 3)
//											}else{
//												'E77_07' util.substring(current_DGCargoSpec?.FlashPoint?.Temperature, 1, 4)
//											}
														'E77_07' util.substring(current_DGCargoSpec?.FlashPoint?.Temperature, 1, 4)?.replaceAll("\\.\$", "")
													}
													if (current_DGCargoSpec?.FlashPoint?.TemperatureUnit == 'C') {
														'E355_08' 'CE'
													} else if (current_DGCargoSpec?.FlashPoint?.TemperatureUnit == 'F') {
														'E355_08' 'FA'
													}
													if (StringUtil.isNotEmpty(current_DGCargoSpec?.PackageGroup?.Code)) {
														'E254_09' current_DGCargoSpec?.PackageGroup?.Code
													}
												}
												if (current_DGCargoSpec?.TechnicalName) {
													for (int i = 0; i * 60 < current_DGCargoSpec?.TechnicalName?.length(); i++) {
														'H2' {
															if (util.substring(current_DGCargoSpec?.TechnicalName, i * 60 + 1, 30)?.trim()?.length() == 1) {
																'E64_01' util.substring(current_DGCargoSpec?.TechnicalName, i * 60 + 1, 30)?.trim() + " "
															} else {
																'E64_01' util.substring(current_DGCargoSpec?.TechnicalName, i * 60 + 1, 30)?.trim()
															}
															'E274_02' util.substring(current_DGCargoSpec?.TechnicalName, i * 60 + 31, 30)?.trim()
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

//					sum = new BigDecimal(0)
//					filteredFreightCharge?.findAll{it.ChargeType == blUtil.COLLECT}?.each{current_FreightCharge ->
//						if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
//							sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
//						}
//
//					}
//
//					if(sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)>0){
//						'E58_05' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
//					}

//					sum = new BigDecimal(0)
//					current_Body?.FreightCharge?.findAll{it?.ChargeType == blUtil.PREPAID && it?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toBigDecimal() > 0}?.each{current_FreightCharge ->
//						if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
//							sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
//						}
//					}
//					'E117_07' util.substring((sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString(), 1, 8);

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

							if(current_FreightCharge?.ChargeType == blUtil.PREPAID && current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency && current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency != "0"){
								'E117_06' util.substring(amountFormat(current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)?.toBigDecimal()?.toString(), 1, 9)
							}

							//Element 'Loop_L1[1]/L1/E150_08' value min-len required (1)
//							Map<String, String> chargeTypeExtCDE = blUtil.getB2BCdeConversion(current_FreightCharge?.ChargeCode, TP_ID, 'ChargeType', conn)
							Map<String, String> chargeTypeExtCDE = util.getCdeConversionFromIntCde(TP_ID,"BL","O",null,null,"ChargeType",util.substring(current_FreightCharge?.ChargeCode?.toUpperCase(),1,3), conn)
							if(StringUtil.isNotEmpty(current_FreightCharge?.ChargeCode) && StringUtil.isNotEmpty(chargeTypeExtCDE.get("EXT_CDE")) ){
								'E150_08' chargeTypeExtCDE.get("EXT_CDE")
							}

							if(current_FreightCharge?.ChargeType == '0'){
								'E16_11' 'C'
							}else{
								'E16_11' 'P'
							}

//							'E276_12' chargeTypeExtCDE.get("REMARKS") ? util.substring(chargeTypeExtCDE.get("REMARKS"), 1, 25) : '999'
						}
						'C3' {
							if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency){
								'E100_01' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency
								if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.length() > 10 && current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.indexOf(".") > -1){
									'E280_02' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.substring(0, current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate?.indexOf(".") + 1)
								}else{
									'E280_02' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate
								}
							}

						}
					}
				}
			}
			if(current_Body?.Remarks){
				List<String> Remarks = splitStringByLength(current_Body?.Remarks, 30)
				for(int i = 0 ; i < Remarks.size(); i = i+2){
					'K1' {
						'E61_01' Remarks[i]
						if(Remarks.size() > i + 1){
							'E61_02' Remarks[i+1]
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
//			blUtil.filterByChargeType(filteredFreightCharge, filteredFreightChargeCNTR, blUtil.COLLECT)
//			blUtil.filterByChargeCodeBlock(filteredFreightCharge, filteredFreightChargeCNTR, ['DET'])

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

//		blUtil.amendments_C(current_Body, false, "No amendments", errorKeyList);

		return errorKeyList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){


		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")

		blUtil.checkL0Max(node, 120, true , "L0 cannot support cargo more than 120" ,errorKeyList)
		blUtil.checkN101_M(node, true, "N101 and N102 are mandatory.", errorKeyList)
		blUtil.checkN102_M(node, true, "N101 and N102 are mandatory.", errorKeyList)
		blUtil.checkL3_M(node, false, null, errorKeyList)
		blUtil.checkMissingLX(node, true, "Missing BL-CONTAINER", errorKeyList)
//		blUtil.checkPOLNotUSCA(node, false, null, errorKeyList)

	}

	
	void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){
		
		
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")
		
		blUtil.checkB309_B310_P(node, true, null, errorKeyList)
//		blUtil.checkC301_M(node, true, null, errorKeyList)		//CLP app bug, it is impossible to missing C301
		blUtil.checkC803_C802_R(node, true, null, errorKeyList)
		blUtil.checkDTM02_DTM03_DTM_05_R(node, true, null, errorKeyList)
		blUtil.checkDTM04_DTM03_C(node, true, null, errorKeyList)
		blUtil.checkG6103_G6104_P(node, true, null, errorKeyList)
//		blUtil.checkMissingL1_2(node, false, "131At least one of L104,L105,L106 required.", errorKeyList)
//		blUtil.checkL104_L105_L106_R(node, false, "131At least one of L104,L105,L106 required.", errorKeyList)
		blUtil.checkL301_L302_P(node, true, null, errorKeyList)
		blUtil.checkL309_L310_P(node, true, null, errorKeyList)
		blUtil.checkL312_L301_C(node, true, null, errorKeyList)
		blUtil.checkN101_N102_M(node, true, null, errorKeyList)
		blUtil.checkN102_N103_R(node, true, null, errorKeyList)
		blUtil.checkN703_N704_P(node, true, null, errorKeyList)
		blUtil.checkN901_N902_M(node, true, null, errorKeyList)
		blUtil.checkN902_N903_R(node, true, null, errorKeyList)
		blUtil.checkQTY01_M(node, true, null, errorKeyList)
		blUtil.checkQTY01_QTY02_R(node, true, null, errorKeyList)
		blUtil.checkV101_V102_R(node, true, "At least one of V101, V102 is required.", errorKeyList)
		blUtil.checkH101_M(node, true, null, errorKeyList)
		blUtil.checkH107_H108_P(node, true, null, errorKeyList)
		blUtil.checkW0902_W0903_P(node, true, null, errorKeyList)

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

