package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.text.SimpleDateFormat
/**
* @author XIEMI
* @pattern after TP=CASSOCEANFRTEU
*/
public class CUS_CS2BLXML_310_WALGREENSCASS {

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

	List<String> acceptChargeCode = ['OCE','OCEAN FREIGHT','DGC','ARD','ARB','BAF','COF','BUC']

	void generateBody(Body current_Body, Integer current_Container_index, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {

		Container current_Container = current_Body.Container[current_Container_index]
		String currentContainerNumber = current_Container?.ContainerNumber
		Map<String, String> containerSizeTypeMap = util.getCdeConversionFromIntCde(TP_ID, 'BL', 'O', current_Body?.GeneralInformation?.SCACCode, 'X.12','ContainerType', current_Container?.CS1ContainerSizeType, conn)
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '310'
				'E329_02' '-999'
			}
			///////////////////   B3-start
			'B3' {
				'E147_01' 'B'

				if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLNumber)){
					'E76_02' util.substring(current_Body?.GeneralInformation?.SCACCode + current_Body?.GeneralInformation?.BLNumber + (current_Container_index + 1).toString(), 1, 22)
					'E145_03' util.substring(current_Body?.GeneralInformation?.SCACCode + current_Body?.GeneralInformation?.BLNumber + (current_Container_index + 1).toString(), 1, 22)
				}

				'E146_04' 'CC'

				SimpleDateFormat soutfmt = new SimpleDateFormat(xmlDateTimeFormat)

				'E373_06' util.convertDateTime(soutfmt.format(new Date()), xmlDateTimeFormat, yyyyMMdd)

				sum = new BigDecimal(0);
				filteredFreightChargeCNTR?.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == it?.ContainerCheckDigit}?.each{current_FreightChargeCNTR ->
					if(current_FreightChargeCNTR?.TotalAmtInPmtCurrency){
						sum = sum + new BigDecimal(current_FreightChargeCNTR?.TotalAmtInPmtCurrency?.toString())?.setScale(2,BigDecimal.ROUND_HALF_UP )
					}
				}
				if(sum > 0){
					'E193_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString()
				}else{
					'E193_07' '0'
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
			}
			
			///////////////////   B2A-start
			Map<String, String> eventDescriptionMap = ['NEW':'00','UPDATE':'04']
			'B2A' {
				if(eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)){
					'E353_01' eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)
				}else{
					'E353_01' '01'
				}
				'E346_02' 'BL'
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
			Map<String, String> referenceCodeCS2Map = ['CTR':'CT','EXPR':'RF','FID':'FI','GEN':'GN','ICR':'CR','INV':'IK','OTH':'ZZ',
													   'PO':'PO','QUOT':'QN','SC':'E8','SCA':'SCA',
													   'SO':'SO','SR':'SR','TARIF':'TI','WARG':'QA','CR':'CR','SID':'SI']
			
			def systemRef = current_Body?.ExternalReference?.find{it?.ReferenceDescription=='System Reference Number'}?.ReferenceNumber
			
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
								'E127_02' util.substring(current_ExternalReference.ReferenceNumber,1,30)
							}
						}
					}
			}
			
			
			///////////////////   V1-start
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body?.Route?.OceanLeg?.size() > 0){
				firstOceanLeg = current_Body?.Route?.OceanLeg[0]
				lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
			}
			if(firstOceanLeg){
				'V1' {
					if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber){
						'E597_01' firstOceanLeg?.SVVD?.Loading?.LloydsNumber
					}else if(firstOceanLeg?.SVVD?.Loading?.CallSign){
						'E597_01' firstOceanLeg?.SVVD?.Loading?.CallSign
					}
					if(firstOceanLeg?.SVVD?.Loading?.VesselName){
						'E182_02' firstOceanLeg?.SVVD?.Loading?.VesselName
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
			if(current_Body?.Route?.OceanLeg?.size()>1){
				if(lastOceanLeg){
					'V1' {
						if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber){
							'E597_01' lastOceanLeg?.SVVD?.Discharge?.LloydsNumber
						}else if(lastOceanLeg?.SVVD?.Discharge?.CallSign){
							'E597_01' lastOceanLeg?.SVVD?.Discharge?.CallSign
						}
						if(lastOceanLeg?.SVVD?.Discharge?.VesselName){
							'E182_02' lastOceanLeg?.SVVD?.Discharge?.VesselName
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
			}
			
			///////////////////   Y2-start
			
			List<String> uniqueContainerY2_04Value = []
					
			if(current_Container?.CS1ContainerSizeType && StringUtil.isNotEmpty(containerSizeTypeMap.get("EXT_CDE"))){
//				if(!uniqueContainerY2_04Value?.contains(containerSizeTypeMap.get("EXT_CDE"))){
//					uniqueContainerY2_04Value?.add(containerSizeTypeMap.get("EXT_CDE"))
					'Y2'{
						'E95_01' '1'
						'E24_04' containerSizeTypeMap.get("EXT_CDE")
					}
//				}
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
						def vAddressArray = blUtil.SplitTextWithConnector(vAddressLines, 110)
						def N3MaxIndex = 1 //LoopConcatAddressLine
						vAddressArray?.each{ current_addressLines ->
							if(current_addressLines && N3MaxIndex <=2){
								N3MaxIndex++
								'N3' {
									'E166_01' util.substring(current_addressLines?.trim(), 1, 55)?.trim()
									if(current_addressLines.trim()?.length() >= 56){
										'E166_02' util.substring(current_addressLines?.trim(), 56, 55)?.trim()
									}
								}
							}
						}
						
						'N4' {
							if(current_Party?.PartyType=='SHP' || current_Party?.PartyType == 'CGN'){
								'E19_01' current_Party?.Address?.City?.toUpperCase()
							}else{
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
							
							if(current_Party?.Address?.PostalCode){
								'E116_03' current_Party?.Address?.PostalCode
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
			'Loop_R4' {
				'R4' {
					'E115_01' 'A'
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
						'E374_01' '370'
					}
					if(current_Body?.Route?.FullReturnCutoffDT?.LocDT){
						'E373_02' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, HHmm)
					}
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
						if(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '370'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '371'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
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
						if(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '370'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '371'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
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
						'E374_01' '370'
						'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
					}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E374_01' '371'
						'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
					}else if(oceanLegSize > 0 && current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
						'E374_01' '370'
						'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
					}else if(oceanLegSize > 0 && current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E374_01' '371'
						'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
					}
				}
			}
		
			///////////////////   C8-start
			current_Body?.BLCertClause?.each{current_BLCertClause ->
				if(StringUtil.isNotEmpty(current_BLCertClause?.CertificationClauseText)){
					'Loop_C8' {
						'C8' {
							if(current_BLCertClause?.CertificationClauseType){
								'E246_02' current_BLCertClause?.CertificationClauseType
							}
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
			}
			
			///////////////////   LX-start
//			associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
//				List<Container> containerGroup = current_Body.Container.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.CS1ContainerSizeType == current_Container?.CS1ContainerSizeType}
				List<Cargo> current_cargoList = current_Body?.Cargo?.findAll{it?.CurrentContainerNumber == current_Container?.ContainerNumber && it?.CurrentContainerCheckDigit == current_Container?.ContainerCheckDigit}
				'Loop_LX' {
					'LX' {
						 'E554_01' '1'
					}
					if(util.substring(current_Container?.ContainerNumber, 5, 5)){
						'Loop_N7' {
							if(util.substring(current_Container?.ContainerNumber, 5, 10)){
								'N7' {
									'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
									'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10).concat(util.substring(current_Container?.ContainerCheckDigit,1,1))
									
									def varWeight1 = null
									def varWeight2 = null
									if(current_Container?.GrossWeight?.WeightUnit == 'KGS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
										varWeight1 = current_Container?.GrossWeight?.Weight?.trim()
										'E81_03' varWeight1
									}else if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight)){
										varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight?.trim()).multiply(thousand).toString()
										if(varWeight1.indexOf('.') > 0){
											varWeight1 = blUtil.replaceZeroAfterPoint(varWeight1)
										}
										'E81_03' varWeight1
									}else if(current_Container?.GrossWeight?.WeightUnit == 'LBS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
										varWeight2 = current_Container?.GrossWeight?.Weight?.trim()
										'E81_03' blUtil.replaceZeroAfterPoint(varWeight2)
									}
									
									if(varWeight1){
										'E187_04' 'G'
									}else if(varWeight2){
										'E187_04' 'G'
									}
									
									if(varWeight1){
										'E188_17' 'K'
									}else if(varWeight2){
										'E188_17' 'L'
									}
									'E761_18' util.substring(current_Container?.ContainerCheckDigit, 1, 1)
									
									if(current_Container?.CS1ContainerSizeType){
										def ExtCDE = util.getConversionWithScac(TP_ID, 'BL', 'O', 'ContainerType', current_Container?.CS1ContainerSizeType,current_Body?.GeneralInformation?.SCACCode, conn)
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
											}else{
												'E355_01' 'UN'
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
							def NetWeightKGS = null
							def NetWeightLBS = null
							def NetWeightTON = null

							if(current_Cargo?.NetWeight?.WeightUnit?.trim() == 'KGS'){
								NetWeightKGS = current_Cargo?.NetWeight?.Weight?.trim()
								if(NetWeightKGS?.indexOf('.') > 0){
									NetWeightKGS = blUtil.replaceZeroAfterPoint(NetWeightKGS)
								}
								if(StringUtil.isNotEmpty(NetWeightKGS)){
									'E81_04' NetWeightKGS
								}
							}else if(current_Cargo?.NetWeight?.WeightUnit?.trim() == 'LBS'){
								NetWeightLBS = current_Cargo?.NetWeight?.Weight?.trim()
								if(NetWeightLBS?.indexOf('.') > 0){
									NetWeightLBS = blUtil.replaceZeroAfterPoint(NetWeightLBS)
								}
								if(StringUtil.isNotEmpty(NetWeightLBS)){
									'E81_04' NetWeightLBS
								}
							}else if(current_Cargo?.NetWeight?.WeightUnit?.trim() == 'TON'){
								if(current_Cargo?.NetWeight?.Weight){
									NetWeightTON = new BigDecimal(current_Cargo?.NetWeight?.Weight).multiply(thousand).toString()
								}
								if(NetWeightTON?.indexOf('.') > 0){
									NetWeightTON = blUtil.replaceZeroAfterPoint(NetWeightTON)
								}
								if(StringUtil.isNotEmpty(NetWeightTON)){
									'E81_04' NetWeightTON
								}
							}else if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'KGS'){
								groWeigthKGS =  current_Cargo?.GrossWeight?.Weight?.trim()
								if(groWeigthKGS?.indexOf('.') > 0){
									groWeigthKGS = blUtil.replaceZeroAfterPoint(groWeigthKGS)
								}
								if(StringUtil.isNotEmpty(groWeigthKGS)){
									'E81_04' groWeigthKGS
								}
							}else if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'LBS'){
								groWeigthLBS =  current_Cargo?.GrossWeight?.Weight?.trim()
								if(groWeigthLBS?.indexOf('.') > 0){
									groWeigthLBS = blUtil.replaceZeroAfterPoint(groWeigthLBS)
								}
								if(StringUtil.isNotEmpty(groWeigthLBS)){
									'E81_04' groWeigthLBS
								}
							}else if(current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'TON'){
								if(current_Cargo?.GrossWeight?.Weight){
									groWeigthTON = new BigDecimal(current_Cargo?.GrossWeight?.Weight).multiply(thousand).toString()
								}
								if(groWeigthTON?.indexOf('.') > 0){
									groWeigthTON = blUtil.replaceZeroAfterPoint(groWeigthTON)
								}
								if(StringUtil.isNotEmpty(groWeigthTON)){
									'E81_04' groWeigthTON
								}
							}

							if(NetWeightKGS || NetWeightLBS || NetWeightTON){
								'E187_05' 'N'
							}else if(groWeigthKGS || groWeigthLBS || groWeigthTON){
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

							if(StringUtil.isNotEmpty(current_Cargo?.Packaging?.PackageQty)){
								'E80_08' current_Cargo?.Packaging?.PackageQty?.trim()
								def excdeByPackageType = util.getConversion(TP_ID, 'BL', 'O', 'PackageUnit', current_Cargo?.Packaging?.PackageType, conn)
								if(StringUtil.isNotEmpty(current_Cargo?.Packaging?.PackageQty) && StringUtil.isNotEmpty(excdeByPackageType)){
									'E211_09' excdeByPackageType
								}else{
									'E211_09' 'UNT'
								}
							}

							if(NetWeightKGS  && NetWeightKGS){
								'E188_11' 'K'
							}else if(NetWeightTON  && NetWeightTON){
								'E188_11' 'K'
							}else if(NetWeightLBS  && NetWeightLBS){
								'E188_11' 'L'
							}else if(groWeigthKGS  && groWeigthKGS){
								'E188_11' 'K'
							}else if(groWeigthTON  && groWeigthTON){
								'E188_11' 'K'
							}else if(groWeigthLBS  && groWeigthLBS){
								'E188_11' 'L'
							}
						}

						//L5 LoopByCargoDescCASS
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
									'E22_03' 'A'
									'E23_04' 'Z'

									if(i < MarksAndNumbersList.size()){
										if(util.substring(MarksAndNumbersList[i].MarksAndNumbersLine, 1, 48)){
											'E87_06' util.substring(MarksAndNumbersList[i].MarksAndNumbersLine, 1, 48)?.trim()
										}
									}
								}
							}

						}

						current_Cargo?.DGCargoSpec?.each{current_DGCargoSpec ->
							if(current_DGCargoSpec?.UNNumber){
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
											if(current_Cargo?.CargoNature == 'DG'){
												'E63_05' util.substring(current_DGCargoSpec?.EmergencyContact[0]?.ContactPhone?.Number, 1, 24)
											}else if(current_Cargo?.CargoNature == 'RD'){
												'E63_05' util.substring(current_Cargo?.ReeferCargoSpec[0]?.EmergencyContact[0]?.ContactPhone?.Number, 1, 24)
											}else if(current_Cargo?.CargoNature == 'AD'){
												'E63_05' util.substring(current_Cargo?.AWCargoSpec[0]?.EmergencyContact[0]?.ContactPhone?.Number, 1, 24)
											}
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

									if(current_DGCargoSpec?.TechnicalName){
										def technicalName = current_DGCargoSpec?.TechnicalName
										int count = (technicalName.length() / 30.0);
										if( ! (count + "").endsWith(".0")){
											count = count + 1;
										}
										int numberOfLine = (int)count;
										def originalStr = technicalName;
										StringBuffer sb = new StringBuffer();

										def StringLineInLength30 = new String[numberOfLine];

										for(int i = 0; i < numberOfLine; i++){
											//Cut for each 55
											if(originalStr.length() > 30){
												 StringLineInLength30[i] = originalStr.substring(0, 30);
											 originalStr = originalStr.substring(30);
											}else{
												 StringLineInLength30[i] = originalStr.substring(0, originalStr.length());
												 originalStr = originalStr.substring(originalStr.length());
											}

										}

										String combineStr = "";
										for(int i = 0; i < StringLineInLength30.length; i++){
											if( i % 2 == 0){
												combineStr = combineStr + StringLineInLength30[i];
											}else{
												combineStr = combineStr + "|" + StringLineInLength30[i] + "END";
											}
										}
										StringLineInLength30 = combineStr.split("END");

										println(StringLineInLength30)

										StringLineInLength30?.each{ current_Str ->
											'H2'{
												if(current_Str?.indexOf('|')>0){
													'E64_01' current_Str?.substring(0, current_Str?.indexOf('|'))
												}else{
													'E64_01' current_Str
												}
												if(current_Str?.indexOf('|')>-1 && !current_Str?.endsWith("|")){
													'E274_02' current_Str?.substring(current_Str?.indexOf('|')+1,current_Str?.length())
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
//			}
			
			///////////////////   L3-start
			'L3' {
				def sumL3_01GrossWeigth = new BigDecimal(0)
				current_Body?.Cargo?.findAll{StringUtil.isNotEmpty(it?.GrossWeight?.Weight)}?.each{ current_Cargo ->
					
					def current_GrossWeight = current_Cargo?.GrossWeight
					
					if(current_GrossWeight?.WeightUnit == 'TON'){
						sumL3_01GrossWeigth = sumL3_01GrossWeigth + new BigDecimal(multiplyWithoutOriginalAccuray(current_GrossWeight?.Weight, '1000', 3))
					}else if(current_GrossWeight?.WeightUnit == 'LBS'){
						sumL3_01GrossWeigth = sumL3_01GrossWeigth + new BigDecimal(current_GrossWeight?.Weight)/2.2
					}else{
						sumL3_01GrossWeigth = sumL3_01GrossWeigth + new BigDecimal(current_GrossWeight?.Weight)
					}
				}
				def sumL3_01NetWeigth = new BigDecimal(0)
				current_Body?.Cargo?.findAll{StringUtil.isNotEmpty(it?.NetWeight?.Weight)}?.each{ current_Cargo ->
					
					def current_NetWeight = current_Cargo?.NetWeight
					
					if(current_NetWeight?.WeightUnit == 'TON'){
						sumL3_01NetWeigth = sumL3_01NetWeigth + new BigDecimal(multiplyWithoutOriginalAccuray(current_NetWeight?.Weight, '1000', 3))
					}else if(current_NetWeight?.WeightUnit == 'LBS'){
						sumL3_01NetWeigth = sumL3_01NetWeigth + new BigDecimal(current_NetWeight?.Weight)/2.2
					}else{
						sumL3_01NetWeigth = sumL3_01NetWeigth + new BigDecimal(current_NetWeight?.Weight)
					}
				}
				if(current_Body?.Cargo?.find{StringUtil.isNotEmpty(it?.NetWeight?.Weight)}){
					'E81_01' util.substring(sumL3_01NetWeigth?.toString(),1,10)
					'E187_02' 'N'
					
				}else if(current_Body?.Cargo?.find{StringUtil.isNotEmpty(it?.GrossWeight?.Weight)}){
					'E81_01' util.substring(sumL3_01GrossWeigth?.toString(),1,10)
					'E187_02' 'G'
				}
				
				
				sum = new BigDecimal(0)
				filteredFreightChargeCNTR?.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit}?.each{current_FreightChargeCNTR ->
					if(current_FreightChargeCNTR.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightChargeCNTR.TotalAmtInPmtCurrency?.toString())?.setScale(2, BigDecimal.ROUND_HALF_UP)
					}
					
				}
				if(sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)>0){
					'E58_05' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
				}else{
					'E58_05' '0'
				}

				//09

				sum = new BigDecimal(0)
				current_Body?.Cargo?.findAll{StringUtil?.isNotEmpty(it?.Volume?.Volume)}?.each { current_Cargo ->
					sum = sum + (new BigDecimal(current_Cargo?.Volume?.Volume).multiply(10000));
				}
				String tempSum = sum?.divide(10000)?.toString()
				tempSum = tempSum?.length() < 9 ? tempSum : tempSum?.substring(0, 9)

				sum = (new BigDecimal(tempSum)).setScale(2, BigDecimal.ROUND_HALF_UP) > 0? (new BigDecimal(tempSum)).setScale(2, BigDecimal.ROUND_HALF_UP) : 0
				if(sum?.toString()?.indexOf('.') > 0){
					sum = new BigDecimal(blUtil.replaceZeroAfterPoint(sum?.toString()))
				}
				'E183_09' sum

				def sumL3_10value = new BigDecimal(0)
				current_Body?.Cargo?.each{current_Cargo ->
					if(current_Cargo?.Volume?.Volume){
						sumL3_10value = sumL3_10value + new BigDecimal(current_Cargo?.Volume?.Volume).multiply(new BigDecimal(10000))
					}
				}
				sumL3_10value = sumL3_10value.divide(new BigDecimal(10000))
				if(current_Body?.Cargo?.find{it?.Volume?.VolumeUnit=='CBF'}!=null && sumL3_10value != 0){
					'E184_10' 'E'
				}else if(current_Body?.Cargo?.find{it?.Volume?.VolumeUnit=='CFT'}!=null && sumL3_10value != 0){
					'E184_10' 'E'
				}else if(current_Body?.Cargo?.find{it?.Volume?.VolumeUnit=='CBM'}!=null && sumL3_10value != 0){
					'E184_10' 'X'
				}else{
					'E184_10' 'X'
				}
				
				sum = new BigDecimal(0)
				current_Body?.Cargo?.each{current_Cargo ->
					if(current_Cargo?.Packaging?.PackageQty){
						sum = sum + new BigDecimal(current_Cargo?.Packaging?.PackageQty)
					}
				}
				'E80_11' util.substring(sum.toString(), 1, 7);
				
				if(current_Body?.Cargo?.find{StringUtil.isNotEmpty(it?.NetWeight?.WeightUnit) && (it?.NetWeight?.WeightUnit=='TON' || it?.NetWeight?.WeightUnit == 'KGS') }){
					'E188_12' 'K'
				}else if(current_Body?.Cargo?.find{StringUtil.isNotEmpty(it?.NetWeight?.WeightUnit) && it?.NetWeight?.WeightUnit=='LBS' } ){
					'E188_12' 'L'
				}else if(current_Body?.Cargo?.find{StringUtil.isNotEmpty(it?.GrossWeight?.WeightUnit) && (it?.GrossWeight?.WeightUnit=='TON' || it?.GrossWeight?.WeightUnit=='KGS') }){
					'E188_12' 'K'
				}else if(current_Body?.Cargo?.find{StringUtil.isNotEmpty(it?.GrossWeight?.WeightUnit) && it?.GrossWeight?.WeightUnit=='LBS'} ){
					'E188_12' 'L'
				}
				
			}
			
			///////////////////   Loop_L1-start
			def L101indexValue = 1
			filteredFreightChargeCNTR?.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit}?.eachWithIndex{current_FreightCharge, FreightChargeIndex->
				
			if(current_FreightCharge?.ChargeType == blUtil.COLLECT || current_FreightCharge?.ChargeType == blUtil.PREPAID){
					if(current_FreightCharge?.TotalAmtInPmtCurrency){
						'Loop_L1' {
							'L1' {
								'E213_01' L101indexValue++
								if(current_FreightCharge?.FreightRate){
									def freightRate = current_FreightCharge?.FreightRate
									if(freightRate?.indexOf(".")>0){
										freightRate = blUtil.replaceZeroAfterPoint(freightRate)
									}
									if(freightRate?.startsWith("0.")){
										freightRate = freightRate?.substring(1, freightRate?.length())
									}
									'E60_02' freightRate
								}
								
								def calculateMethod = current_FreightCharge?.CalculateMethod?.toUpperCase()
								List<String> calculateMethodList = ['CONTAINER','GROSS CARGO WEIGHT','NET CARGO WEIGHT','LUMP SUM','MEASUREMENT','PACKAGE']
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
									}else{
										'E122_03' 'LS'
									}
								}else{
									'E122_03' 'LS'
								}
								if(current_FreightCharge?.ChargeType == blUtil.COLLECT){
									if(current_FreightCharge?.TotalAmtInPmtCurrency){
										'E58_04' blUtil.replaceZeroAfterPoint(new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()).multiply(hundred).toString())
									}
								}
								if(current_FreightCharge?.ChargeType == blUtil.PREPAID){
									if(current_FreightCharge?.TotalAmtInPmtCurrency){
										'E117_06' multiplyWithoutOriginalAccuray(current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency, "100", 2)
									}
								}
								
								if(current_FreightCharge?.ChargeType == blUtil.COLLECT){
									'E16_11' 'C'
								}else{
									'E16_11' 'P'
								}
								
								Map<String, String> chargeTypeExtCDE = blUtil.getB2BCdeConversion(util.substring(current_FreightCharge?.ChargeCode,1,3)?.toUpperCase(), TP_ID, 'ChargeType', conn)
								if(StringUtil.isNotEmpty(current_FreightCharge?.ChargeCode) && StringUtil.isNotEmpty(chargeTypeExtCDE.get("REMARKS")) ){
									'E276_12' util.substring(chargeTypeExtCDE.get("REMARKS"),1,25)
								}
								
								if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency){
									'E100_20' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency
								}
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
		def headerMsgDT = util.convertDateTime(bl.Header?.MsgDT?.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		def txnErrorKeys = []

		List<String> bodyStringList = [];
		//Start mapping
		
		bl?.Body?.eachWithIndex{current_Body, current_BodyIndex ->
			
			//OceanLegReorder
			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)
			
			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)
			
			List<Map<String,String>> errorKeyList = []

			//container checking
			blUtil.missingContainer(current_Body, false, null, errorKeyList);

			if(errorKeyList.isEmpty()) {

				//split by Container
				current_Body?.Container?.eachWithIndex { current_Container, current_Container_index ->

					List<FreightCharge> filteredFreightCharge = current_Body.FreightCharge?.clone()
					List<FreightChargeCNTR> filteredFreightChargeCNTR = current_Body.FreightChargeCNTR?.clone()

					//filterFreightCharge
					filteredFreightChargeCNTR = filteredFreightChargeCNTR?.findAll {
						it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit
					}

					errorKeyList = []
					//prep checking
					prepValidation(current_Body, filteredFreightChargeCNTR, current_Container_index, errorKeyList)

					def bodyWriter = new StringWriter()
					def bodyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bodyWriter), "", false));
					def body = bodyXml.createNode("Body")

					blUtil.filterByChargeCodeAccept(filteredFreightCharge, filteredFreightChargeCNTR, acceptChargeCode)
					blUtil.filterByChargeType(filteredFreightCharge, filteredFreightChargeCNTR, blUtil.PREPAID)
					blUtil.filterByNegativeCollect(filteredFreightCharge, filteredFreightChargeCNTR)
					blUtil.filterByTotalAmountCurrency(filteredFreightCharge, filteredFreightChargeCNTR, ['CAD', 'USD'], null)

					//mapping
					if (errorKeyList.isEmpty()) {
						generateBody(current_Body, current_Container_index, associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, bodyXml)
						bodyXml.nodeCompleted(null, body)
					}
//					//EDI Syntax checking
//					if(errorKeyList.isEmpty()){
//						syntaxValidation(bodyWriter?.toString(), errorKeyList)
//					}
					//posp checking
					if (errorKeyList.isEmpty()) {
						pospValidation(bodyWriter?.toString(), errorKeyList, current_Body)
					}

					if (errorKeyList.isEmpty()) {
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

		blUtil.partyTypeMissing(current_Body, true, null, errorKeyList)
		blUtil.nonUSCAinPOD(false, "POD should be CA or US", errorKeyList, current_Body)
		blUtil.msFunDelete(current_Body, false, null, errorKeyList)
		blUtil.invalidCollectCharge_WALGREENSCASS(current_Body, acceptChargeCode , true, null, errorKeyList)
		blUtil.collectCurrencyNotUSDCAD(current_Body, freightChargeCNTRList, true, null, errorKeyList)
		blUtil.checkSealNumberLength(current_Body, 15, true, null, errorKeyList)
		blUtil.sendStatusOfEDIChecker(current_Body, false, "No Amendments is send", errorKeyList)
		blUtil.bLReadyOnly(current_Body, false, "Status is not BL-Ready", errorKeyList)
		return errorKeyList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList , cs.b2b.core.mapping.bean.bl.Body current_Body){

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml)

		blUtil.checkMissingL1_2(node,true, 'No Freight Charge', errorKeyList)
		blUtil.checkL1max(node, 20, true, "L1 segment more than 20.", errorKeyList)
		blUtil.checkL106LengthWithPoint(node, 9, true, null, errorKeyList)
		blUtil.checkL102_L103_M(node, true, null, errorKeyList)
		blUtil.checkL102L106L307Checker(node,true, null, errorKeyList)
		blUtil.checkL0Max(node,120, true, "L0 is over than maximum 120", errorKeyList)
		blUtil.checkH1_FlashPoint(node,true, null, errorKeyList)
	}

	private String multiplyWithoutOriginalAccuray(String multiplyed, String multiplying, Integer oriAccuray){// solve the accuray problem
		def accuray = 0;
		if(multiplyed?.indexOf('.')>0){
			accuray = (multiplyed?.length() - multiplyed?.indexOf('.')-1) - oriAccuray;
		}
		accuray = accuray<0?0:accuray;
		return new BigDecimal(multiplyed).multiply(new BigDecimal(multiplying)).setScale(accuray,BigDecimal.ROUND_HALF_UP).toString()
	}
}

