package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection

import cs.b2b.core.common.util.StringUtil
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
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * @author MikeXie
 * @pattern after TP=DUMMY310BLb
 */
public class CUS_CS2BLXML_310_DUMMY310BLc {

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
	public void generateBody(Body current_Body, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '310'
				'E329_02' '-999'
			}
			'B3' {
				if(current_Body?.GeneralInformation?.BLType?.toUpperCase() == 'ORIGINAL'){
					'E147_01' 'B'
				}else if(current_Body?.GeneralInformation?.BLType?.toUpperCase() == 'SEA WAYBILL'){
					'E147_01' '2'
				}
				if(current_Body?.GeneralInformation?.BLNumber){
					'E76_02' util.substring(current_Body?.GeneralInformation?.BLNumber, 1, 22)
				}
				def vsize = current_Body?.ExternalReference?.findAll{it.CSReferenceType == 'SID'}?.size()
				if( vsize > 0){
					'E145_03' current_Body?.ExternalReference?.findAll{it.CSReferenceType == 'SID'}[vsize-1].ReferenceNumber
				}


				int v_prepaid_charge = filteredFreightCharge?.findAll{it.ChargeType == blUtil.PREPAID}?.size() + filteredFreightChargeCNTR?.findAll {it?.ChargeType == blUtil.PREPAID}?.size()
				int v_collect_charge = filteredFreightCharge?.findAll{it.ChargeType == blUtil.COLLECT}?.size() + filteredFreightChargeCNTR?.findAll {it?.ChargeType == blUtil.COLLECT}?.size()

				if(v_prepaid_charge == 0 && v_collect_charge > 0){
					'E146_04' 'CC'
				}else if(v_prepaid_charge > 0 && v_collect_charge == 0){
					'E146_04' 'PP'
				}else if(v_prepaid_charge > 0 && v_collect_charge > 0){
					'E146_04' 'MX'
				}else{
					'E146_04' 'MX'
				}

				'E373_06' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT, xmlDateTimeFormat, yyyyMMdd)

				sum = BigDecimal.ZERO;
				filteredFreightChargeCNTR?.each{current_FreightCharge ->
					if(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString())
					}
				}
				'E193_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
				//'E193_07' '0'

				def alocDT = null
				def elocDT = null
				current_Body?.Route?.OceanLeg?.find{alocDT = it?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT}
				current_Body?.Route?.OceanLeg?.find{elocDT = it?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT}
				if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT){
					'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '140'
				}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
					'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '139'
				}else if(alocDT){
					'E32_09' util.convertDateTime(alocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '140'
				}else if(elocDT){
					'E32_09' util.convertDateTime(elocDT, xmlDateTimeFormat, yyyyMMdd)
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
			Map<String, String> actionMap = ['DEL':'01' ,'NEW':'00','UPD':'05']
			'B2A' {
				if(actionMap.get(current_Body?.TransactionInformation?.Action)){
					'E353_01' actionMap.get(current_Body?.TransactionInformation?.Action)
				}
			}
			//Loop by BookingNumber
			current_Body?.GeneralInformation?.CarrierBookingNumber?.each{current_BookingNumber ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' current_BookingNumber
				}
			}
			//Loop by BLNum ONCE
			'N9' {
				'E128_01' 'BM'
				'E127_02' current_Body?.GeneralInformation?.BLNumber

			}
			//Loop by Body/CarrierRate
			def vCarrRatetTy= null
			def vItemCde = null
			Map<String, String> referenceCodeCS2Map = [:]
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
					'N9' {
						'E128_01' vItemCde
						'E127_02' current_CarrierRate.CarrierRateNumber
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
					'N9' {
						'E128_01' vItemCde
						'E127_02' current_ExternalReference.ReferenceNumber
					}
				}
			}
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
						'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber,1,8)
					}else if(firstOceanLeg?.SVVD?.Loading?.CallSign){
						'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.CallSign,1,8)
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
					'E140_05' current_Body?.GeneralInformation?.SCACCode
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
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber,1,8)
					}else if(lastOceanLeg?.SVVD?.Discharge?.CallSign){
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.CallSign,1,8)
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
					'E140_05' current_Body?.GeneralInformation?.SCACCode
					if(util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber,1,8)){
						'E897_08' 'L'
					}else if(util.substring(lastOceanLeg?.SVVD?.Discharge?.CallSign,1,8)){
						'E897_08' 'C'
					}
				}
			}

			Map<String, String> partyTypeMap = ['CAR':'CA','CGN':'CN','FWD':'FW','NPT':'N1','ANP':'N2', 'SHP':'SH']
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
						'N3' {
							if(util.substring(vAddressLines?.trim(), 1, 55)){
								'E166_01' util.substring(vAddressLines?.trim(), 1, 55)?.trim()
							}
							if(vAddressLines.trim()?.length() >= 56){
								'E166_02' util.substring(vAddressLines?.trim(), 56, 55)?.trim()
							}
						}
						'N4' {
							//if(current_Party?.Address?.City){
							'E19_01' current_Party?.Address?.City
							//}
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
							if(current_Party?.Address?.Country){
								'E26_04' current_Party?.Address?.Country
							}
							if(current_Party?.Address?.LocationCode?.UNLocationCode){
								'E309_05' 'UN'
								'E310_06' current_Party?.Address?.LocationCode?.UNLocationCode
							}

						}
					}
				}
			}
			'Loop_N1' {
				'N1' {
					'E98_01' 'CA'
					'E93_02' current_Body?.GeneralInformation?.SCACCode

				}
			}
			current_Body?.Party?.each{current_Party ->
				def FirstName = current_Party?.Contact?.FirstName
				def LastName = current_Party?.Contact?.LastName
				def vname = util.substring((FirstName!=null? FirstName  :'' ) + ' ' + (LastName!=null? LastName :'' ) , 1 , 60)
				def CountryCode = current_Party?.Contact?.ContactPhone?.CountryCode!=null? current_Party?.Contact?.ContactPhone?.CountryCode : ''
				def AreaCode = current_Party?.Contact?.ContactPhone?.AreaCode!=null? current_Party?.Contact?.ContactPhone?.AreaCode : ''
				def vnumber = null
				if(current_Party?.Contact?.ContactPhone?.Number){
					vnumber = CountryCode+ '-' + AreaCode+ '-' +current_Party?.Contact?.ContactPhone?.Number
				}
				if(partyTypeMap.get(current_Party?.PartyType) && StringUtil.isNotEmpty(vname) && vnumber){
					'G61' {
						'E366_01' partyTypeMap.get(current_Party?.PartyType)
						'E93_02' vname?.trim()
						'E365_03' 'TE'
						'E364_04' vnumber
					}
				}
			}
			current_Body?.Party?.each{current_Party ->
				def FirstName = current_Party?.Contact?.FirstName
				def LastName = current_Party?.Contact?.LastName
				def vname = util.substring((FirstName!=null? FirstName  :'' ) + ' ' + (LastName!=null? LastName :'' ) , 1 , 60)
				if(partyTypeMap.get(current_Party?.PartyType) && StringUtil.isNotEmpty(vname) && current_Party?.Contact?.ContactEmailAddress?.trim()){
					'G61' {
						'E366_01' partyTypeMap.get(current_Party?.PartyType)
						'E93_02' vname?.trim()
						'E365_03' 'EM'
						'E364_04' current_Party?.Contact?.ContactEmailAddress?.trim()
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
					'E373_02' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E337_03' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, HHmm)
				}
			}
			//POL

			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if(POL){
						if(POL?.Port?.locationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' POL?.Port?.locationCode?.UNLocationCode
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

			'R2' {
				'E140_01' current_Body?.GeneralInformation?.SCACCode
				'E133_02' 'O'
				Map<String, String> trafficModeMap =  ['LCLLCL':'02', 'FCLFCL': '03', 'FCLLCL' :'04', 'LCLFCL' :'05']
				if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.OutBound) && StringUtil.isNotEmpty(current_Body?.GeneralInformation?.TrafficMode?.InBound)){
					'E56_12' trafficModeMap.get(current_Body?.GeneralInformation?.TrafficMode?.OutBound + current_Body?.GeneralInformation?.TrafficMode?.InBound)
				}

			}
			current_Body?.BLCertClause?.each{current_BLCertClause ->
				'Loop_C8' {
					'C8' {
						'E246_02' current_BLCertClause?.CertificationClauseType
						'E247_03'  util.substring(current_BLCertClause?.CertificationClauseText, 1, 60)
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
			associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
				List<Container> containerGroup = current_Body?.Container?.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.CS1ContainerSizeType == current_Container?.CS1ContainerSizeType}
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
									if(current_Container?.GrossWeight?.WeightUnit == 'KGS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
										varWeight1 = current_Container?.GrossWeight?.Weight?.trim()
										'E81_03' varWeight1
									}else if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
										varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight?.trim())?.multiply(thousand)?.toString()
										if(varWeight1.indexOf('.') > 0){
											varWeight1 = blUtil.replaceZeroAfterPoint(varWeight1)
										}
										'E81_03' varWeight1
									}else if(current_Container?.GrossWeight?.WeightUnit == 'LBS'){
										varWeight2 = current_Container?.GrossWeight?.Weight?.trim()
										'E81_03' varWeight2
									}
									if(varWeight1){
										'E187_04' 'G'
									}
									if(varWeight1){
										'E188_17' 'K'
									}else if(varWeight2){
										'E188_17' 'L'
									}
									if(current_Container?.ContainerCheckDigit != null && !util.isDecimal(util.substring(current_Container?.ContainerCheckDigit, 1, 1))){
										throw new Exception("N718 is mapped from ContainerCheckDigit, it must be a number, but actual is " + util.substring(current_Container?.ContainerCheckDigit, 1, 1))
									}
									'E761_18' util.substring(current_Container?.ContainerCheckDigit, 1, 1)	//this field is a number type, will validate in BO
									if(current_Container?.CS1ContainerSizeType){
										def ExtCDE = util.getConversionWithScac(TP_ID, 'BL', 'O', 'ContainerType', current_Container?.CS1ContainerSizeType,current_Body?.GeneralInformation?.SCACCode, conn)
										if(ExtCDE){
											'E24_22' ExtCDE
										}

									}

								}
							}
							if(current_Container?.PieceCount?.PieceCount){
								'QTY' {
									'E673_01' '39'
									'E380_02' current_Container?.PieceCount?.PieceCount
									Map<String, String> pieceCountUnitMap = [:]
									'C001_03' {
										if(pieceCountUnitMap.get(current_Container?.PieceCount?.PieceCountUnit)){
											'E355_01' pieceCountUnitMap.get(current_Container?.PieceCount?.PieceCountUnit?.trim())
										}else if(current_Container?.PieceCount?.PieceCountUnit){
											'E355_01' current_Container?.PieceCount?.PieceCountUnit?.trim()
										}

									}
								}
							}

							containerGroup?.each{ currentSubContainer ->
								currentSubContainer.Seal?.each{current_Seal ->
									if(util.substring(current_Seal?.SealNumber, 1, 15)){
										'M7' {
											'E225_01' util.substring(current_Seal?.SealNumber?.trim(), 1, 15)
											if(current_Seal?.SealNumber?.length() > 15){
												'E225_02' util.substring(current_Seal?.SealNumber, 16, 15)
											}

											if(current_Seal?.SealNumber?.length() > 30){
												'E225_03' util.substring(current_Seal?.SealNumber, 31, 15)
											}

											if(current_Seal?.SealType == 'CA' ){
												'E98_05' 'CA'
											}else if( current_Seal?.SealType == 'SH'){
												'E98_05' 'SH'
											}
										}
									}
								}
							}
							if(current_cargoList[0]?.ReeferCargoSpec?.size() > 0){
								cs.b2b.core.mapping.bean.bl.ReeferCargoSpec current_ReeferCargoSpec = current_cargoList[0]?.ReeferCargoSpec[current_cargoList[0]?.ReeferCargoSpec?.size() -1]
								'W09' {
									'E40_01' 'CZ'
									//eg. in:-12.036 out:-12.03
									def temperature = current_ReeferCargoSpec?.Temperature?.Temperature?.trim()
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
									if(current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'DEGREES'){
										'E1122_07' ventilationMap.get(current_ReeferCargoSpec?.Ventilation?.Ventilation?.trim())
									}

									if(current_ReeferCargoSpec?.DehumidityPercentage){
										'E488_08' current_ReeferCargoSpec?.DehumidityPercentage
									}
									if(current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBFPERMIN' || current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.toUpperCase() == 'CBMPERHOUR' ){
										'E380_09' current_ReeferCargoSpec?.Ventilation?.Ventilation
									}

								}
							}

							//begin -->mike 4 or 6 exists


							int actualFreightChargeIndex = 0;
							filteredFreightChargeCNTR?.eachWithIndex{current_FreightCharge, FreightChargeIndex->
								if((current_FreightCharge?.ContainerNumber?.trim() == current_Container?.ContainerNumber?.trim()) && (current_FreightCharge?.ContainerCheckDigit?.trim() == current_Container?.ContainerCheckDigit)){
									if(current_FreightCharge?.ChargeType == blUtil.COLLECT || current_FreightCharge?.ChargeType == blUtil.PREPAID){
										'Loop_L1' {
											'L1' {
												actualFreightChargeIndex++
												'E213_01' actualFreightChargeIndex
												def vfreightRate = null
												if(current_FreightCharge?.FreightRate){
													vfreightRate = (new BigDecimal(current_FreightCharge?.FreightRate).setScale(2, BigDecimal.ROUND_HALF_UP)).toString()
												}
												if(StringUtil.isNotEmpty(vfreightRate)) {
													'E60_02' vfreightRate
												}

												'E122_03' 'PA'

												if(current_FreightCharge?.ChargeType == blUtil.COLLECT){
													if(current_FreightCharge?.TotalAmtInPmtCurrency){
														def total = new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()).multiply(hundred).toString()
														if(total?.indexOf('.')>0){
															total = blUtil.replaceZeroAfterPoint(total)
														}
														'E58_04' total
													}
												}else{
													'E58_04' '0'
												}
												if(current_FreightCharge?.ChargeType == blUtil.PREPAID){
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
//here
												//select EXT_CDE from b2b_cde_conversion WHERE (INT_CDE = ? or REMARKS = ?) and DIR_ID = 'O' and MSG_TYPE_ID = 'BL' and TP_ID = ? and CONVERT_TYPE_ID = 'ChargeType'
												Map<String, String> chargeTypeExtCDE = blUtil.getB2BCdeConversion(current_FreightCharge?.ChargeCode, TP_ID, 'ChargeType', conn)
												if(chargeTypeExtCDE.get("EXT_CDE")){
													'E150_08' chargeTypeExtCDE.get("EXT_CDE")
												}else{
													'E150_08' '999'
												}
												if(current_FreightCharge?.ChargeType == blUtil.COLLECT){
													'E16_11' 'E'
												}else{
													'E16_11' 'P'
												}
												//select REMARKS from b2b_cde_conversion WHERE INT_CDE = ? and DIR_ID = 'O' and MSG_TYPE_ID = 'BL' and TP_ID = ? and CONVERT_TYPE_ID = 'ChargeType'
												Map<String, String> chargeTypeREMARKS = blUtil.getB2BCdeConversion(util.substring(current_FreightCharge?.ChargeCode, 1, 3)?.toUpperCase(), TP_ID, 'ChargeType', conn)
												if(chargeTypeREMARKS.get("REMARKS")){
													'E276_12' util.substring(chargeTypeExtCDE.get("REMARKS"), 1, 25)?.trim()
												}else{
													'E276_12' '999'
												}
											}
											if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency){
												'C3' {
													'E100_01' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency
													if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate){
														sum = new  BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate)
														'E280_02' (sum.setScale(4, BigDecimal.ROUND_HALF_UP)).toString();
													}
												}
											}

										}

									}

								}



							}

						}
					}
//TODO
					//L start..



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
								}else if(current_Cargo?.GrossWeight?.WeightUnit == 'LBS'){
									GrossWeightLBS = current_Cargo?.GrossWeight?.Weight
									'E81_04' GrossWeightLBS==null ?'' :GrossWeightLBS
								}else if(current_Cargo?.GrossWeight?.WeightUnit == 'KGS' ){
									GrossWeightKGS = current_Cargo?.GrossWeight?.Weight
									'E81_04' GrossWeightKGS==null ?'' :GrossWeightKGS
								}else if(current_Cargo?.GrossWeight?.WeightUnit == 'TON' ){
									if(current_Cargo?.GrossWeight?.Weight){
										GrossWeightTON = new BigDecimal(current_Cargo?.GrossWeight?.Weight).multiply(thousand).toString()
										if(GrossWeightTON?.indexOf('.') > 0){
											GrossWeightTON = blUtil.replaceZeroAfterPoint(GrossWeightTON)
										}
									}else{
										GrossWeightTON = ''
									}
									'E81_04' GrossWeightTON
								}

								if(NetWeightTON || NetWeightLBS || NetWeightKGS){
									'E187_05' 'N'
								}else if(GrossWeightTON || GrossWeightLBS || GrossWeightKGS){
									'E187_05' 'G'
								}
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
											'E184_07' 'E'
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
											'E184_07' 'X'
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
											'E184_07' 'E'
										}
									}
								}
								if(current_Cargo?.Packaging?.PackageQty){
									'E80_08' current_Cargo?.Packaging?.PackageQty?.trim()
								}else{
									'E80_08' '0'
								}

								if(current_Cargo?.Packaging?.PackageQty && util.getConversion(TP_ID, 'BL', 'O', 'PackageUnit', current_Cargo?.Packaging?.PackageType, conn)){
									'E211_09' util.getConversion(TP_ID, 'BL', 'O', 'PackageUnit', current_Cargo?.Packaging?.PackageType, conn)
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
										if(i < CargoDescription.size()){
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
											'E62_01' current_DGCargoSpec?.UNNumber
											'E209_02' util.substring(current_DGCargoSpec?.IMOClass, 1, 4)
											if(current_DGCargoSpec?.DGRegulator == 'IMD'){
												'E208_03' 'I'
											}
											'E64_04' util.substring(current_DGCargoSpec?.ProperShippingName, 1, 30)
											'E63_05' util.substring(current_DGCargoSpec?.EmergencyContact[0]?.ContactPhone?.Number, 1, 24)
											'E200_06' util.substring(current_DGCargoSpec?.IMDGPage, 1, 6)
											'E77_07' util.substring(current_DGCargoSpec?.FlashPoint?.Temperature, 1, 3)
											if(current_DGCargoSpec?.FlashPoint?.TemperatureUnit == 'C'){
												'E355_08' 'CE'
											}else if(current_DGCargoSpec?.FlashPoint?.TemperatureUnit == 'F'){
												'E355_08' 'FA'
											}

											'E254_09' current_DGCargoSpec?.PackageGroup?.Code
										}
										if(util.substring(current_DGCargoSpec?.ProperShippingName, 31, 30)){
											'H2' {
												'E64_01' util.substring(current_DGCargoSpec?.ProperShippingName, 31, 30)
												'E274_02' util.substring(current_DGCargoSpec?.TechnicalName, 31, 30)
											}
										}
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
					'E81_01'  util.substring(vWeight, 1, 10)
					'E187_02' 'G'
				}else if(util.isDecimal(current_Body?.GeneralInformation?.BLNetWeight?.Weight) && current_Body?.GeneralInformation?.BLNetWeight?.Weight?.toBigDecimal() > 0){
					def vWeight = current_Body?.GeneralInformation?.BLNetWeight?.Weight
					if(current_Body?.GeneralInformation?.BLNetWeight?.Weight?.indexOf('.') > 0){
						vWeight = blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLNetWeight?.Weight)
					}
					'E81_01'  util.substring(vWeight, 1, 10)
					'E187_02' 'N'
				}
				sum = BigDecimal.ZERO

				filteredFreightChargeCNTR?.findAll{it?.ChargeType == blUtil.COLLECT}?.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
					}

				}
				'E58_05' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString()

				sum = BigDecimal.ZERO
				filteredFreightChargeCNTR?.findAll{it.ChargeType == blUtil.PREPAID}?.each{current_FreightCharge ->
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

				sum = BigDecimal.ZERO
				current_Body?.Cargo?.each{current_Cargo ->
					if(current_Cargo?.Packaging?.PackageQty){
						sum = sum + new BigDecimal(current_Cargo?.Packaging?.PackageQty)
					}
				}
				'E80_11' util.substring(sum.toString(), 1, 7);
				Map<String, String> weightUnitMap = ['TON':'K', 'KGS':'K', 'LBS':'L']
				if(weightUnitMap.get(current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit) && util.isDecimal(current_Body?.GeneralInformation?.BLGrossWeight?.Weight) && current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.toBigDecimal() > 0){
					'E188_12' weightUnitMap.get(current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit)
				}else if(weightUnitMap.get(current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit) && util.isDecimal(current_Body?.GeneralInformation?.BLNetWeight?.Weight) && current_Body?.GeneralInformation?.BLNetWeight?.Weight?.toBigDecimal() > 0){
					'E188_12' weightUnitMap.get(current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit)
				}

			}




//
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
			blUtil.filterByContainerLevel(filteredFreightCharge, null)
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
			result = util.cleanXml(writer?.toString());
		}
		//replaceAll * to |
//		result = result.replaceAll('\\*', '\\|');
		writer.close();

		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){

		return [];
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){


		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")

		blUtil.checkL0Max(node, 120, true, null, errorKeyList)
		blUtil.checkL102Length(node, 9, true, 'L102\'s length max.9', errorKeyList)
		blUtil.checkL106Length(node, 12, true, null, errorKeyList)  //L106's length max is 12
		blUtil.checkL307Length(node, 12, true, null, errorKeyList)  //L307's length max.
	}


	void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){


		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")

		blUtil.checkC803_C802_R(node, true, 'At least one of C802,C803 required', errorKeyList)
		blUtil.checkG6103_G6104_P(node, true, null, errorKeyList)
		blUtil.checkW0902_W0903_P(node, true, null, errorKeyList)
		blUtil.checkV108_V101_C(node, true, null, errorKeyList)
		blUtil.checkV101_V102_R(node, true, 'At least one of V101, V102 is required', errorKeyList) //
		blUtil.checkH107_H108_P(node, true, null, errorKeyList)
		blUtil.checkL004_L005_P(node, true, null, errorKeyList)
		blUtil.checkL006_L007_P(node, true, null, errorKeyList)
		blUtil.checkL008_L009_P(node, true, null, errorKeyList)
		blUtil.checkL011_L004_C(node, true, null, errorKeyList)
		blUtil.checkL102_L103_P(node, true, 'Either L102 L103 exists then all are required.', errorKeyList)
		blUtil.checkL104_L105_L106_R(node, true, null, errorKeyList)
		blUtil.checkL301_L302_P(node, true, null, errorKeyList)
		blUtil.checkL309_L310_P(node, true, null, errorKeyList)
		blUtil.checkL312_L301_C(node, true, null, errorKeyList)
		blUtil.checkN406_N405_C(node, true, null, errorKeyList)
		blUtil.checkR402_R403_P(node, true, null, errorKeyList)
	}
}

