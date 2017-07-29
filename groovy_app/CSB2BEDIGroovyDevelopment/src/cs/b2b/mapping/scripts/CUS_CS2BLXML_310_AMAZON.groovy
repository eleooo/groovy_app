package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.GrossWeight
import cs.b2b.core.mapping.bean.bl.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection

/**
 * @author renga
 * @pattern after TP=DUMMY310BLc http://i2isd/sites/csisa/Lists/Workplan/DispForm.aspx?ID=29913
 */
public class CUS_CS2BLXML_310_AMAZON {

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
	public void generateBody(Body current_Body, Integer current_Container_index, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {

		Container current_Container = current_Body.Container[current_Container_index]
		String currentContainerNumber = current_Container?.ContainerNumber
		Map<String, String> containerSizeTypeMap = util.getCdeConversionFromIntCde(TP_ID, 'BL', 'O', null, 'X.12','ContainerType', current_Container?.CSContainerSizeType, conn)

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
					//Map B302=GeneralInformation.BLNumber + N incrementing per container/transaction
					'E76_02' util.substring(current_Body?.GeneralInformation?.BLNumber + (current_Container_index + 1).toString(), 1, 22)
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
				filteredFreightChargeCNTR.findAll{it.ContainerNumber == currentContainerNumber}?.each{current_FreightCharge ->
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
			int indexN9 = 0
			//Loop by BookingNumber
			current_Body?.GeneralInformation?.CarrierBookingNumber?.each{current_BookingNumber ->
				if(indexN9 < 15){
					indexN9++
					'N9' {
						'E128_01' 'BN'
						'E127_02' util.substring(current_BookingNumber, 1, 30)
					}
				}
			}
			//Loop by BLNum ONCE
			if( current_Body?.GeneralInformation?.SCACCode && current_Body?.GeneralInformation?.BLNumber && indexN9 < 15){
				indexN9++
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring((current_Body?.GeneralInformation?.SCACCode + current_Body?.GeneralInformation?.BLNumber), 1, 30)
				}
			}
			//Loop by Body/CarrierRate
			Map<String, String> carrierRateTypeMap = ['SC':'CT']
			current_Body?.CarrierRate?.each{current_CarrierRate ->
				if(carrierRateTypeMap.get(current_CarrierRate?.CSCarrierRateType) && indexN9 < 15){
					indexN9++
					'N9' {
						'E128_01' carrierRateTypeMap.get(current_CarrierRate?.CSCarrierRateType)
						'E127_02' util.substring(current_CarrierRate.CarrierRateNumber, 1, 30)
					}
				}
			}

			Map<String, String> referenceTypeMap = ['PO':'PO']
			current_Body?.ExternalReference?.each{current_ExternalReference ->
				if(referenceTypeMap.get(current_ExternalReference.CSReferenceType) && indexN9 < 15){
					indexN9++
					'N9' {
						'E128_01' referenceTypeMap.get(current_ExternalReference.CSReferenceType)
						'E127_02' util.substring(current_ExternalReference.ReferenceNumber, 1, 30)
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
							'E55_04' firstOceanLeg?.SVVD?.Loading?.Voyage  + util.substring(firstOceanLeg?.LoadingDirectionName, 1, 1)
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
							'E55_04' lastOceanLeg?.SVVD?.Discharge?.Voyage + util.substring(lastOceanLeg?.DischargeDirectionName, 1, 1)
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

			'Y2' {
				'E95_01' '1'
				'E78_02' ''
				'E56_03' ''
				if(containerSizeTypeMap.get("EXT_CDE")){
					'E24_04' containerSizeTypeMap.get("EXT_CDE")
				}else{
					'E24_04' current_Container?.CSContainerSizeType
				}
				'E91_05' ''
				'E177_06' ''
				'E140_07' ''
				'E464_08' ''
				'E465_09' ''
				'E466_10' ''
			}

			Map<String, String> partyTypeMap = ['CGN':'CN', 'SHP':'SH']
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
							if(current_Party?.PartyType == 'SHP'){
								'E67_04' 'OOLUOCEAN'
							}else{
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
			filteredFreightChargeCNTR?.find{it.ContainerNumber == currentContainerNumber && it.PayByInformation}?.each {current_FreightChargeCNTR ->
				'Loop_N1' {
					'N1' {
						'E98_01' 'BT'
						'E93_02' util.substring(current_FreightChargeCNTR.PayByInformation?.PayerName,1, 60)
						if(current_FreightChargeCNTR?.PayByInformation?.CarrierCustomerCode){
							'E66_03' '25'
							'E67_04' current_FreightChargeCNTR?.PayByInformation?.CarrierCustomerCode
						}
					}

					'N4' {
						//if(current_Party?.Address?.City){
						'E19_01' current_FreightChargeCNTR?.PayByInformation?.CityDetails?.City
						//}
						Map<String, String> vResult = null
						if(current_FreightChargeCNTR?.PayByInformation?.CityDetails?.State){
							vResult = util.getCS2MasterCityByStateNameCityNameCntryCde(current_FreightChargeCNTR?.PayByInformation?.CityDetails?.State, current_FreightChargeCNTR?.PayByInformation?.CityDetails?.City, current_FreightChargeCNTR?.PayByInformation?.CityDetails?.Country, conn)
						}
						Map<String, String> vResult1 = null
						Map<String, String> vResult2 = null
						if(StringUtil.isEmpty(vResult?.get('STATE_CDE')) ){
							vResult1 = util.getCS2MasterCityByStateNameCityName(current_FreightChargeCNTR?.PayByInformation?.CityDetails?.State, current_FreightChargeCNTR?.PayByInformation?.CityDetails?.City, conn)
						}
						if(StringUtil.isEmpty(vResult?.get('STATE_CDE')) && StringUtil.isEmpty(vResult1?.get('STATE_CDE')) ){
							vResult2 = util.getCS2MasterCityByStateName(current_FreightChargeCNTR?.PayByInformation?.CityDetails?.State, conn)
						}
						if(vResult?.get('STATE_CDE')){
							'E156_02' vResult?.get('STATE_CDE')
						}else if(vResult1?.get('STATE_CDE')){
							'E156_02' vResult1?.get('STATE_CDE')
						}else if(vResult2?.get('STATE_CDE')){
							'E156_02' vResult2?.get('STATE_CDE')
						}
						'E116_03' ''

						if(current_FreightChargeCNTR?.PayByInformation?.CityDetails?.Country){
							'E26_04' util.getCS2MasterCity4CountryCodeByCountryName(current_FreightChargeCNTR?.PayByInformation?.CityDetails?.Country, conn)
						}
						if(current_FreightChargeCNTR?.PayByInformation?.CityDetails?.LocationCode?.UNLocationCode){
							'E309_05' 'UN'
							'E310_06' current_FreightChargeCNTR?.PayByInformation?.CityDetails?.LocationCode?.UNLocationCode
						}

					}
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


			List<Cargo> current_cargoList = current_Body?.Cargo?.findAll{it.CurrentContainerNumber == current_Container?.ContainerNumber}
			BigDecimal sumVolume = BigDecimal.ZERO
			current_cargoList.each {current_Cargo ->
				if(current_Cargo?.Volume) {
					sumVolume = sumVolume + current_Cargo?.Volume?.Volume?.toBigDecimal()
				}
			}
			//Mapping only 1 LX for the specific ContainerNumber
			'Loop_LX' {
				'LX' {
					'E554_01' 1
				}
				if(util.substring(current_Container?.ContainerNumber, 5, 5)){
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

								'E183_08' blUtil.replaceZeroAfterPoint(sumVolume.setScale(3, BigDecimal.ROUND_HALF_UP).toString())
								if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CBF'}){
									'E184_09' 'E'
								}else if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CBM'}){
									'E184_09' 'X'
								}else if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CFT'}){
									'E184_09' 'E'
								}

								if(varWeight1){
									'E188_17' 'K'
								}else if(varWeight2){
									'E188_17' 'L'
								}

								'E761_18' util.substring(current_Container?.ContainerCheckDigit, 1, 1)
								if(current_Container?.CSContainerSizeType){
									if(containerSizeTypeMap.get("EXT_CDE")){
										'E24_22' containerSizeTypeMap.get("EXT_CDE")
									}else{
										'E24_22' current_Container?.CSContainerSizeType
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

						current_Container?.Seal?.each{current_Seal ->
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
								def pointIndex = temperature?.indexOf('.')
								def tempBeforePoint = null
								def tempAfterPoint = null
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

						//L1 will be mapped from FreightChargeCNTR corresponding to the ContainerNumber

						int actualFreightChargeIndex = 0;
						filteredFreightChargeCNTR?.eachWithIndex{current_FreightChargeCNTR, FreightChargeIndex->
							if((current_FreightChargeCNTR?.ContainerNumber?.trim() == current_Container?.ContainerNumber?.trim())){
								if(current_FreightChargeCNTR?.ChargeType == blUtil.COLLECT || current_FreightChargeCNTR?.ChargeType == blUtil.PREPAID){
									'Loop_L1' {
										'L1' {
											actualFreightChargeIndex++
											'E213_01' actualFreightChargeIndex
											def vfreightRate = null
											if(current_FreightChargeCNTR?.FreightRate){
												vfreightRate = (new BigDecimal(current_FreightChargeCNTR?.FreightRate).setScale(2, BigDecimal.ROUND_HALF_UP)).toString()
											}
											if(StringUtil.isNotEmpty(vfreightRate)) {
												'E60_02' vfreightRate
											}

											'E122_03' 'PA'

											if(current_FreightChargeCNTR?.ChargeType == blUtil.COLLECT){
												if(current_FreightChargeCNTR?.TotalAmtInPmtCurrency){
													def total = new BigDecimal(current_FreightChargeCNTR?.TotalAmtInPmtCurrency?.toString()).multiply(hundred).toString()
													if(total?.indexOf('.')>0){
														total = blUtil.replaceZeroAfterPoint(total)
													}
													'E58_04' total
												}
											}else{
												'E58_04' '0'
											}
											if(current_FreightChargeCNTR?.ChargeType == blUtil.PREPAID){
												if(current_FreightChargeCNTR?.TotalAmtInPmtCurrency){
													def total =  new BigDecimal(current_FreightChargeCNTR?.TotalAmtInPmtCurrency?.toString()).multiply(hundred).toString()
													if(total?.indexOf('.')>0){
														total = blUtil.replaceZeroAfterPoint(total)
													}
													'E117_06' total
												}
											}else{
												'E117_06' '0'
											}
											//select EXT_CDE from b2b_cde_conversion WHERE (INT_CDE = ? or REMARKS = ?) and DIR_ID = 'O' and MSG_TYPE_ID = 'BL' and TP_ID = ? and CONVERT_TYPE_ID = 'ChargeType'
											Map<String, String> chargeTypeExtCDE = blUtil.getB2BCdeConversion(util.substring(current_FreightChargeCNTR?.ChargeCode?.toUpperCase(), 1, 3), TP_ID, 'ChargeType', conn)
											if(chargeTypeExtCDE.get("EXT_CDE")){
												'E150_08' chargeTypeExtCDE.get("EXT_CDE")
											}else{
												'E150_08' util.substring(current_FreightChargeCNTR?.ChargeCode?.toUpperCase(), 1, 3)
											}
											if(current_FreightChargeCNTR?.ChargeType == blUtil.COLLECT){
												'E16_11' 'E'
											}else{
												'E16_11' 'P'
											}
											'E276_12' util.substring(current_FreightChargeCNTR?.ChargePrintLable, 1, 25)?.trim()
										}
										if(current_FreightChargeCNTR?.TotalAmtInPmtCurrency?.attr_Currency){
											'C3' {
												'E100_01' current_FreightChargeCNTR?.TotalAmtInPmtCurrency?.attr_Currency
												if(current_FreightChargeCNTR?.TotalAmtInPmtCurrency?.attr_ExchangeRate){
													sum = new  BigDecimal(current_FreightChargeCNTR?.TotalAmtInPmtCurrency?.attr_ExchangeRate)
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
			}

			'L3' {

				if(util.isDecimal(current_Container?.GrossWeight?.Weight) && current_Container?.GrossWeight?.Weight?.toBigDecimal() > 0){
					def varWeight1 = null
					def varWeight2 = null
					if(current_Container?.GrossWeight?.WeightUnit == 'KGS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
						varWeight1 = current_Container?.GrossWeight?.Weight?.trim()
						'E81_01' util.substring(varWeight1, 1, 10)
					}else if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
						varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight?.trim())?.multiply(thousand)?.toString()
						if(varWeight1.indexOf('.') > 0){
							varWeight1 = blUtil.replaceZeroAfterPoint(varWeight1)
						}
						'E81_01' util.substring(varWeight1, 1, 10)
					}else if(current_Container?.GrossWeight?.WeightUnit == 'LBS'){
						varWeight2 = current_Container?.GrossWeight?.Weight?.trim()
						'E81_01' util.substring(varWeight2, 1, 10)
					}
					'E187_02' 'G'
				}
				sum = BigDecimal.ZERO

				filteredFreightChargeCNTR?.findAll{it?.ChargeType == blUtil.COLLECT && it.ContainerNumber == currentContainerNumber}?.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
					}
				}
				'E58_05' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString()

				sum = BigDecimal.ZERO
				filteredFreightChargeCNTR?.findAll{it.ChargeType == blUtil.PREPAID && it.ContainerNumber == currentContainerNumber}?.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
					}
				}
//				'E117_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
				'E183_09' blUtil.replaceZeroAfterPoint(sumVolume.setScale(3, BigDecimal.ROUND_HALF_UP).toString())
				if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CBF'}){
					'E184_10' 'E'
				}else if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CBM'}){
					'E184_10' 'X'
				}else if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CFT'}){
					'E184_10' 'E'
				}

				sum = BigDecimal.ZERO
				current_cargoList?.each{current_Cargo ->
					if(current_Cargo?.Packaging?.PackageQty){
						sum = sum + new BigDecimal(current_Cargo?.Packaging?.PackageQty)
					}
				}
				'E80_11' util.substring(sum.toString(), 1, 7);
				Map<String, String> weightUnitMap = ['TON':'K', 'KGS':'K', 'LBS':'L']
				if(weightUnitMap.get(current_Container?.GrossWeight?.WeightUnit) && current_Container?.GrossWeight?.Weight?.toBigDecimal() != 0){
					'E188_12' weightUnitMap.get(current_Container?.GrossWeight?.WeightUnit)
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


//		def writer = new StringWriter()
//		MarkupBuilder outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
//		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		StringBuffer outXml = new StringBuffer()

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
//		def T310 = outXml.createNode('T310')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.


		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()
		def txnErrorKeys = []

		List<String> bodyStringList = [];

		//Start mapping
		bl?.Body?.eachWithIndex{current_Body, current_BodyIndex ->

			List<FreightCharge> filteredFreightCharge = current_Body.FreightCharge?.clone()
			List<FreightChargeCNTR> filteredFreightChargeCNTR = current_Body.FreightChargeCNTR?.clone()
			//OceanLegReorder
			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)
			//ChargeFilter

			blUtil.filterByChargeType(filteredFreightCharge, filteredFreightChargeCNTR, blUtil.PREPAID)
//			blUtil.filterByTotalAmtInPmtCurrencyIfZero(filteredFreightCharge, filteredFreightChargeCNTR)

			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)

			List<Map<String,String>> errorKeyList = []

			//container checking
			blUtil.missingContainer(current_Body, false, null, errorKeyList);

			if(errorKeyList.isEmpty()){

				//split by Container
				current_Body?.Container?.eachWithIndex {current_Container, current_Container_index ->

					def bodyWriter = new StringWriter()
					def bodyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bodyWriter), "", false));
					def body = bodyXml.createNode("Body")

					errorKeyList = []

					//prep checking
					prepValidation(current_Body, filteredFreightChargeCNTR, current_Container_index, errorKeyList)

					//mapping
					if(errorKeyList.isEmpty()){
						generateBody(current_Body, current_Container_index, associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, bodyXml)
						bodyXml.nodeCompleted(null, body)
					}

					//EDI Syntax checking
					if(errorKeyList.isEmpty()){
						syntaxValidation(bodyWriter?.toString(), errorKeyList)
					}
					//posp checking
					if(errorKeyList.isEmpty()){
						pospValidation(bodyWriter?.toString(), errorKeyList)
					}

					if(errorKeyList.isEmpty()){
						bodyStringList.add(bodyWriter?.toString())
					}

					blUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%18s', current_Body?.TransactionInformation?.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
					blUtil.buildBizKey(bizKeyXml, current_Body, current_Container_index, errorKeyList, bl.Header?.MsgDT?.GMT, bl.Header.InterchangeMessageID, TP_ID, conn)

					txnErrorKeys.add(errorKeyList)

					bodyWriter.close()
				}
			}else{
				blUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%18s', current_Body?.TransactionInformation?.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
				blUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, bl.Header?.MsgDT?.GMT, bl.Header.InterchangeMessageID, TP_ID, conn)
				txnErrorKeys.add(errorKeyList)
			}
		}

		//End root node
//		outXml.nodeCompleted(null,T310)

		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		blUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		blUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		blUtil.promoteHeaderIntChgMsgId(appSessionId, bl?.Body[0]);
		blUtil.promoteScacCode(appSessionId, bl?.Body[0]);

		String result = '';
		if (bodyStringList.size() > 0) {
			outXml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><T310>")
			bodyStringList.each {current_BodyString ->
				outXml.append(current_BodyString.replace("<Body>","").replace("</Body>",""))
			}
			outXml.append("</T310>")
			result = util.cleanXml(outXml?.toString());
		}

		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, List<FreightChargeCNTR> freightChargeCNTRList, int current_Container_Index, List<Map<String,String>> errorKeyList){
		//prep checking
		blUtil.missingContainerNumber(current_Body, false, current_Container_Index, null, errorKeyList)
		blUtil.missingFreightChargeCNTRWithCurrentContainerNumber(freightChargeCNTRList, current_Body.Container[current_Container_Index]?.ContainerNumber, false, null, errorKeyList)

	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml)

		blUtil.checkL0Max(node, 120, true, null, errorKeyList)
		blUtil.checkL102Length(node, 9, true, 'L102\'s length max.9', errorKeyList)
		blUtil.checkL106Length(node, 12, true, null, errorKeyList)  //L106's length max is 12
		blUtil.checkL307Length(node, 12, true, null, errorKeyList)  //L307's length max.
	}


	void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml)

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

