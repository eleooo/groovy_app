package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List;

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.bean.bl.Cargo
import cs.b2b.core.mapping.bean.bl.Container
import cs.b2b.core.mapping.bean.bl.FND
import cs.b2b.core.mapping.bean.bl.FirstPOL
import cs.b2b.core.mapping.bean.bl.FreightCharge
import cs.b2b.core.mapping.bean.bl.FreightChargeCNTR
import cs.b2b.core.mapping.bean.bl.LastPOD
import cs.b2b.core.mapping.bean.bl.OceanLeg
import cs.b2b.core.mapping.bean.bl.POR
import cs.b2b.core.mapping.bean.bl.Party;
import cs.b2b.core.mapping.util.XmlBeanParser
/**
 * @author katerina
 * @pattern after TP=FRIESLANDGTN
 */

public class CUS_CS2BLXML_310_GTNEXUS_CDLGTN {

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
				if(current_Body?.GeneralInformation?.BLType?.toUpperCase() == 'SEA WAYBILL'){
					'E147_01' '2'
				}else{
					'E147_01' 'B'
				}
				if(current_Body?.GeneralInformation?.BLNumber){
					'E76_02' "${current_Body?.GeneralInformation?.SCACCode ? current_Body?.GeneralInformation?.SCACCode : '' }${current_Body?.GeneralInformation?.BLNumber}"
				}
				def v_prepaid_charge = current_Body?.FreightCharge?.findAll{it.ChargeType == blUtil.PREPAID && it?.TotalAmtInPmtCurrency?.attr_Currency == 'USD'}?.size()
				def v_collect_charge = filteredFreightCharge?.findAll{it.ChargeType == blUtil.COLLECT && it?.TotalAmtInPmtCurrency?.attr_Currency == 'USD'}?.size()
				if(v_prepaid_charge == 0 && v_collect_charge > 0){
					'E146_04' 'CC'
				}else if(v_prepaid_charge > 0 && v_collect_charge == 0){
					'E146_04' 'PO'
				}else if(v_prepaid_charge > 0 && v_collect_charge > 0){
					'E146_04' 'MX'
				}else{
					'E146_04' 'MX'
				}
				if(current_Body?.GeneralInformation?.BLOnboardDT?.GMT){
					'E373_06' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT, xmlDateTimeFormat, yyyyMMdd)
				}else if(current_Body?.Route?.DepartureDT?.LocDT){
					'E373_06' util.convertDateTime(current_Body?.Route?.DepartureDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
				}
				sum = BigDecimal.ZERO;
				filteredFreightCharge?.findAll{it.ChargeType == blUtil.COLLECT && it?.TotalAmtInPmtCurrency?.attr_Currency == 'USD'}?.each{current_FreightCharge ->
					if(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString())
					}
				}
				if(sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP) != 0){
					'E193_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
				}else{
					'E193_07' '0'
				}

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
			Map<String, String> actionMap = ['NEW':'00','UPD':'05']
			if(actionMap.get(current_Body?.TransactionInformation?.Action)){
				'B2A' {
					'E353_01' actionMap.get(current_Body?.TransactionInformation?.Action)
					'E346_02' 'BL'
				}
			}
			
				
			
			Map<String, String> referenceCodeCS2Map = ['CR':'CR', 'EXPR':'RF', 'FID':'FI', 'GEN':'GN', 'ICR':'CR', 'INV':'IK', 'OTH':'ZZ', 'PO':'PO', 'QUOT':'QN', 'SC':'CT', 'SCA':'SCA', 'SID':'SI', 'SO':'SO', 'SR':'SR', 'TARIF':'TI', 'WARG':'QA']
			Map<String, String> referenceRemarkCS2Map = ['CR':'CUSTOMER REFERENCE NUMBER', 'EXPR':'EXPORT_REFERENCE_NUMBER', 'FID':'FILE_IDENTIFIER', 'GEN':'Government Export Number',
			'ICR':'Inbound Customer Reference', 'INV':'INVOICE NUMBER', 'OTH':'OTHERS', 'PO':'PURCHASE ORDER NUMBER', 'QUOT':'Quotation Number', 'SC':'CONTRACT NUMBER', 'SCA':'STANDARD CARRIER ALPHA CODE',
			'SID':"Shipper's Identifying Number for Shipment (SID)", 'SO':'S/O Number', 'SR':'Shipper Reference', 'TARIF':'Tariff Item No.', 'WARG':'WARRANTY REGISTRATION NUMBER'
			]
			def countN9 = 0
			current_Body?.GeneralInformation?.CarrierBookingNumber?.each{current_BookingNumber ->
				for(int i = 0; i * 30 < current_BookingNumber.length(); i++ ){
					if(countN9 > 14){
						break
					}
					int startIndex = i * 30
					int endIndex = (i + 1) * 30 < current_BookingNumber.length() ? ((i + 1) * 30) : current_BookingNumber.length()
					'N9' {
						'E128_01' 'BN'
						'E127_02' current_BookingNumber[startIndex..<endIndex]
					}
					countN9++
				}
			}
			
			
			//GTN N9, split per 30 char
			for(int i = 0; i * 30 < current_Body?.GeneralInformation?.BLNumber?.length(); i++ ){
				if(countN9 > 14){
					break
				}
				int startIndex = i * 30
				int endIndex = (i + 1) * 30 < current_Body?.GeneralInformation?.BLNumber?.length() ? ((i + 1) * 30) : current_Body?.GeneralInformation?.BLNumber?.length()
				'N9' {
					'E128_01' 'BM'
					'E127_02' "${current_Body?.GeneralInformation?.SCACCode ? current_Body?.GeneralInformation?.SCACCode : ''}${current_Body?.GeneralInformation?.BLNumber[startIndex..<endIndex]}"
				}
				countN9++
			}
			
			current_Body?.ExternalReference?.each{current_ExternalReference ->
				if(referenceCodeCS2Map.get(current_ExternalReference?.CSReferenceType)){
					for(int i = 0; i * 30 < current_ExternalReference.ReferenceNumber.length(); i++ ) {
						if(countN9 > 14){
							break
						}
						int startIndex = i * 30
						int endIndex = (i + 1) * 30 < current_ExternalReference.ReferenceNumber.length() ? ((i + 1) * 30) : current_ExternalReference.ReferenceNumber.length()
						'N9' {
							'E128_01' referenceCodeCS2Map.get(current_ExternalReference?.CSReferenceType)
							'E127_02' current_ExternalReference.ReferenceNumber[startIndex..<endIndex]
						}
						countN9++
					}
				}
			}
			//Loop by Body/CarrierRate
			current_Body?.CarrierRate?.each{current_CarrierRate ->
				if(referenceCodeCS2Map.get(current_CarrierRate?.CSCarrierRateType)){
					for(int i = 0; i * 30 < current_CarrierRate.CarrierRateNumber.length(); i++ ){
						if(countN9 > 14){
							break
						}
						int startIndex = i * 30
						int endIndex = (i + 1) * 30 < current_CarrierRate.CarrierRateNumber.length() ? ((i + 1) * 30) : current_CarrierRate.CarrierRateNumber.length()
						'N9' {
							'E128_01' referenceCodeCS2Map.get(current_CarrierRate?.CSCarrierRateType)
							'E127_02' current_CarrierRate.CarrierRateNumber[startIndex..<endIndex]
						}
						countN9++
					}
				}
			}

			Map<String, String> partyTypeN9 = ["CCP":"4F"]
			current_Body?.Party?.each{current_Party ->
				if(partyTypeN9.get(current_Party?.PartyType)){
					for(int i = 0; i * 30 < current_Party?.CarrierCustomerCode?.length(); i++ ) {
						if(countN9 > 14){
							break
						}
						int startIndex = i * 30
						int endIndex = (i + 1) * 30 < current_Party?.CarrierCustomerCode?.length() ? ((i + 1) * 30) : current_Party?.CarrierCustomerCode?.length()
						'N9' {
							'E128_01' partyTypeN9.get(current_Party?.PartyType)
							'E127_02' current_Party?.CarrierCustomerCode[startIndex..<endIndex]
						}
						countN9++
					}
				}
			}

			//V1 has done by K
			OceanLeg lastOceanLeg = null
			List<OceanLeg> OceanLegs = current_Body?.Route?.OceanLeg?.findAll{StringUtil.isNotEmpty(it?.SVVD?.Discharge?.VesselName)}
			if(OceanLegs.size() > 0 ){
				lastOceanLeg = OceanLegs[-1]
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
			
			Map<String, String> partyTypeMap = ['ANP':'N2','CGN':'CN','FWD':'FW','NPT':'N1', 'SHP':'SH']
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

						if(util.substring(vAddressLines?.trim(), 1, 55)){
							'N3' {
								'E166_01' util.substring(vAddressLines?.trim(), 1, 55)?.trim()
								def stateCde = getCS2MasterCityByUNLoc(current_Party?.Address?.LocationCode?.UNLocationCode, conn)?.trim()
								'E166_02' "${current_Party?.Address?.City?.trim() ? current_Party?.Address?.City?.trim() : ''} ${stateCde ? stateCde : ''} ${current_Party?.Address?.PostalCode?.trim() ? current_Party?.Address?.PostalCode?.trim() : ''}"?.trim()
							}
						}
						if(current_Party?.Address?.Country){
							'N3' {
								'E166_01' current_Party?.Address?.Country
							}
						}
					}
				}
			}
			List<Party> vPartyList = current_Body?.Party?.findAll{StringUtil.isNotEmpty(it?.Contact?.FirstName?.trim()) && StringUtil.isNotEmpty(it?.Contact?.ContactPhone?.Number?.trim())}
			if(vPartyList?.size() > 3){
				vPartyList = vPartyList?.subList(0, 3)
			}
			//G61
			vPartyList?.each{current_Party ->
				def FirstName = current_Party?.Contact?.FirstName
				def LastName = current_Party?.Contact?.LastName
				def vname = util.substring((FirstName!=null? FirstName  :'' ) + ' ' + (LastName!=null? LastName :'' ) , 1 , 60)
				def CountryCode = current_Party?.Contact?.ContactPhone?.CountryCode!=null? current_Party?.Contact?.ContactPhone?.CountryCode : ''
				def AreaCode = current_Party?.Contact?.ContactPhone?.AreaCode!=null? current_Party?.Contact?.ContactPhone?.AreaCode : ''
				def vnumber = null
				if(current_Party?.Contact?.ContactPhone?.Number){
					vnumber = CountryCode+ '-' + AreaCode+ '-' +current_Party?.Contact?.ContactPhone?.Number
				}
				if(vname?.trim() && vnumber){
					'G61' {
						'E366_01' 'IC'
						'E93_02' vname?.trim()
						'E365_03' 'TE'
						'E364_04' vnumber
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
						}else if (POR?.CityDetails?.LocationCode?.SchedKDCode){
							if(POR?.CityDetails?.LocationCode?.SchedKDType){
								'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType
							}
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
			}
			//POL
			
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if(POL){
						if(POL?.Port?.locationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' POL?.Port?.locationCode?.UNLocationCode
						}else if (POL?.Port?.locationCode?.SchedKDCode){
							if(POL?.Port?.locationCode?.SchedKDType){
								'E309_02' POL?.Port?.locationCode?.SchedKDType
							}
							'E310_03' POL?.Port?.locationCode?.SchedKDCode
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
						}else if (POD?.Port?.locationCode?.SchedKDCode){
							if(POD?.Port?.locationCode?.SchedKDType){
								'E309_02' POD?.Port?.locationCode?.SchedKDType
							}
							'E310_03' POD?.Port?.locationCode?.SchedKDCode
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
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
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
						}else if (FND?.CityDetails?.LocationCode?.SchedKDCode){
							if(FND?.CityDetails?.LocationCode?.SchedKDType){
								'E309_02' FND?.CityDetails?.LocationCode?.SchedKDType
							}
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
			}
			current_Body?.BLCertClause?.findAll{StringUtil.isNotEmpty(it?.CertificationClauseText)}?.each{current_BLCertClause ->
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
			if(associateContainerAndCargo?.find{it?.value?.size() > 0}){
				associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
					List<Container> containerGroup = current_Body?.Container?.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.CS1ContainerSizeType == current_Container?.CS1ContainerSizeType}
					if(current_cargoList && current_cargoList?.size() > 0){
						'Loop_LX' {
							'LX' { 'E554_01' Container_index +1 }
							if(containerGroup?.size() > 0 && util.substring(containerGroup[0]?.ContainerNumber, 5, 5)){
								'Loop_N7' {

									if(util.substring(current_Container?.ContainerNumber, 5, 10)){
										'N7' {
											'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
											'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10)

											def varWeight1 = null
											if(current_Container?.GrossWeight?.WeightUnit == 'KGS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
												varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight?.trim()).setScale(2,BigDecimal.ROUND_HALF_UP).toString()
												'E81_03' blUtil.replaceZeroAfterPoint(varWeight1)
											}else if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
												varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight?.trim())?.multiply(thousand)?.setScale(2,BigDecimal.ROUND_HALF_UP)?.toString()
												'E81_03' blUtil.replaceZeroAfterPoint(varWeight1)
											}
											if(varWeight1){
												'E187_04' 'G'
											}

											//List<Cargo> cargoGroup = current_Body?.Cargo?.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit}
											BigDecimal sumVolume = BigDecimal.ZERO
											current_cargoList.each {
												if(it?.Volume?.Volume){
													sumVolume = sumVolume.add(it?.Volume?.Volume?.toBigDecimal())
												}
											}
											if(sumVolume > BigDecimal.ZERO){
												'E183_08' blUtil.replaceZeroAfterPoint(sumVolume.setScale(3,BigDecimal.ROUND_HALF_UP).toString())
												if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CBF'}){
													'E184_09' 'E'
												}else if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CBM'}){
													'E184_09' 'X'
												}else if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CFT'}){
													'E184_09' 'E'
												}
											}

											if(varWeight1){
												'E188_17' 'K'
											}
											Map InBoundOROutBoundMap = ['C':'D', 'M':'P']
											def vOutBound = InBoundOROutBoundMap.get(current_Body?.Route?.Haulage?.OutBound?.trim())
											def vInBound = InBoundOROutBoundMap.get(current_Body?.Route?.Haulage?.InBound?.trim())
											if("${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}" == 'PP' || "${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}" == 'DD'){
												'E56_19' "${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}"
											}

											if(current_Container?.CS1ContainerSizeType){
												Map<String, String> retMap = util.getCdeConversionFromIntCde('GTNEXUS', '', 'O', current_Body?.GeneralInformation?.SCACCode, 'X.12', 'ContainerType', current_Container?.CS1ContainerSizeType, conn)
												if(retMap.get("EXT_CDE")){
													'E24_22' retMap.get("EXT_CDE")
												}

											}

										}
									}
									if(current_Container?.PieceCount?.PieceCount){
										'QTY' {
											'E673_01' '39'
											'E380_02' current_Container?.PieceCount?.PieceCount
											'C001_03' {
												String pieceCountUnit = null
												if(current_Container?.PieceCount?.PieceCountUnit){
													pieceCountUnit = util.getConversionWithScac(TP_ID,'BL','O','QuantityType',current_Container?.PieceCount?.PieceCountUnit,'ALL', conn)
													if(pieceCountUnit){
														'E355_01' pieceCountUnit
													}else {
														'E355_01' 'UN'
													}
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
													if(current_Seal?.SealType?.trim() == 'TM'){
														'E98_05' 'TO'
													}else{
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
											//eg. in:-12.036 out:-12.03
											def temperature = util.substring(current_ReeferCargoSpec?.Temperature?.Temperature?.trim(), 1, 4)
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
										}
									}

								}
							}

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
										Map InBoundOROutBoundMap = ['C':'D', 'M':'P']
										def vOutBound = InBoundOROutBoundMap.get(current_Body?.Route?.Haulage?.OutBound?.trim())
										def vInBound = InBoundOROutBoundMap.get(current_Body?.Route?.Haulage?.InBound?.trim())
										if("${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}" == 'PP'){
											'E56_12' "${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}"
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
												List<String> technicalNameList = blUtil.SplitTextWithConnector(current_DGCargoSpec?.TechnicalName, 60)
												if(technicalNameList?.size() > 0){
													technicalNameList?.each{current_technical ->
														'H2' {
															if(util.substring(current_technical, 1, 30)?.trim()?.length() == 1){
																'E64_01' util.substring(current_technical, 1, 30)?.trim()+'\\'
															}else{
																'E64_01' util.substring(current_technical, 1, 30)?.trim()
															}
															if(util.substring(current_technical, 31, 30)?.trim()?.length() == 1){
																'E274_02' util.substring(current_technical, 31, 30)?.trim()+'\\'
															}else{
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

						}
					}

				}
			}else{
				associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
					List<Container> containerGroup = current_Body?.Container?.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.CS1ContainerSizeType == current_Container?.CS1ContainerSizeType}
					'Loop_LX' {
						'LX' { 'E554_01' Container_index +1 }
						if(containerGroup?.size() > 0 && util.substring(containerGroup[0]?.ContainerNumber, 5, 5)){
							'Loop_N7' {
								if(util.substring(current_Container?.ContainerNumber, 5, 10)){
									'N7' {
										'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
										'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10)

										def varWeight1 = null
										if(current_Container?.GrossWeight?.WeightUnit == 'KGS' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
											varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight?.trim()).setScale(2,BigDecimal.ROUND_HALF_UP).toString()
											'E81_03' blUtil.replaceZeroAfterPoint(varWeight1)
										}else if(current_Container?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight?.trim())){
											varWeight1 = new  BigDecimal(current_Container?.GrossWeight?.Weight?.trim())?.multiply(thousand)?.setScale(2,BigDecimal.ROUND_HALF_UP)?.toString()
											'E81_03' blUtil.replaceZeroAfterPoint(varWeight1)
										}
										if(varWeight1){
											'E187_04' 'G'
										}

										//List<Cargo> cargoGroup = current_Body?.Cargo?.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit}
										BigDecimal sumVolume = BigDecimal.ZERO
										current_cargoList?.each {
											if(it?.Volume?.Volume){
												sumVolume = sumVolume?.add(it?.Volume?.Volume?.toBigDecimal())
											}
										}
										if(sumVolume > BigDecimal.ZERO){
											'E183_08' blUtil.replaceZeroAfterPoint(sumVolume.setScale(3,BigDecimal.ROUND_HALF_UP).toString())
											if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CBF'}){
												'E184_09' 'E'
											}else if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CBM'}){
												'E184_09' 'X'
											}else if( current_cargoList?.find{it?.Volume?.VolumeUnit == 'CFT'}){
												'E184_09' 'E'
											}
										}

										if(varWeight1){
											'E188_17' 'K'
										}
										Map InBoundOROutBoundMap = ['C':'D', 'M':'P']
										def vOutBound = InBoundOROutBoundMap.get(current_Body?.Route?.Haulage?.OutBound?.trim())
										def vInBound = InBoundOROutBoundMap.get(current_Body?.Route?.Haulage?.InBound?.trim())
										if("${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}" == 'PP' || "${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}" == 'DD'){
											'E56_19' "${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}"
										}

										if(current_Container?.CS1ContainerSizeType){
											Map<String, String> retMap = util.getCdeConversionFromIntCde('GTNEXUS', '', 'O', current_Body?.GeneralInformation?.SCACCode, 'X.12', 'ContainerType', current_Container?.CS1ContainerSizeType, conn)
											if(retMap.get("EXT_CDE")){
												'E24_22' retMap.get("EXT_CDE")
											}

										}

									}
								}
								if(current_Container?.PieceCount?.PieceCount){
									'QTY' {
										'E673_01' '39'
										'E380_02' current_Container?.PieceCount?.PieceCount
										'C001_03' {
											String pieceCountUnit = null
											if(current_Container?.PieceCount?.PieceCountUnit){
												pieceCountUnit = util.getConversionWithScac(TP_ID,'BL','O','QuantityType',current_Container?.PieceCount?.PieceCountUnit,'ALL', conn)
												if(pieceCountUnit){
													'E355_01' pieceCountUnit
												}else {
													'E355_01' 'UN'
												}
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
												if(current_Seal?.SealType?.trim() == 'TM'){
													'E98_05' 'TO'
												}else{
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
										//eg. in:-12.036 out:-12.03
										def temperature = util.substring(current_ReeferCargoSpec?.Temperature?.Temperature?.trim(), 1, 4)
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
									}
								}

							}
						}

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
									Map InBoundOROutBoundMap = ['C':'D', 'M':'P']
									def vOutBound = InBoundOROutBoundMap.get(current_Body?.Route?.Haulage?.OutBound?.trim())
									def vInBound = InBoundOROutBoundMap.get(current_Body?.Route?.Haulage?.InBound?.trim())
									if("${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}" == 'PP'){
										'E56_12' "${vOutBound ? vOutBound : '' }${vInBound ? vInBound : ''}"
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
											List<String> technicalNameList = blUtil.SplitTextWithConnector(current_DGCargoSpec?.TechnicalName, 60)
											if(technicalNameList?.size() > 0){
												technicalNameList?.each{current_technical ->
													'H2' {
														if(util.substring(current_technical, 1, 30)?.trim()?.length() == 1){
															'E64_01' util.substring(current_technical, 1, 30)?.trim()+'\\'
														}else{
															'E64_01' util.substring(current_technical, 1, 30)?.trim()
														}
														if(util.substring(current_technical, 31, 30)?.trim()?.length() == 1){
															'E274_02' util.substring(current_technical, 31, 30)?.trim()+'\\'
														}else{
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

					}

				}
			}
			
			'L3' {
				sum = BigDecimal.ZERO
				current_Body?.Cargo?.findAll{StringUtil.isNotEmpty(it?.GrossWeight?.Weight)}?.each{currentCargo ->
					if(currentCargo?.GrossWeight?.WeightUnit == 'TON'){
						sum = sum + (new BigDecimal(currentCargo?.GrossWeight?.Weight))?.multiply(new BigDecimal(907.18474))
					}else if(currentCargo?.GrossWeight?.WeightUnit == 'LBS'){
						sum = sum + (new BigDecimal(currentCargo?.GrossWeight?.Weight))?.divide(new BigDecimal(2.2), 10, BigDecimal.ROUND_HALF_UP)
					}else{
						sum = sum + new BigDecimal(currentCargo?.GrossWeight?.Weight)
					}
				}
				if(current_Body?.Cargo?.findAll{StringUtil.isNotEmpty(it?.GrossWeight?.Weight)}?.size() > 0){
					'E81_01' util.substring(sum?.toString(), 1, 10)
					'E187_02' 'G'
				}
				sum = BigDecimal.ZERO
				filteredFreightCharge?.findAll{it.ChargeType == blUtil.COLLECT}?.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + new BigDecimal(current_FreightCharge.TotalAmtInPmtCurrency?.toString())
					}
					
				}
				if(sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP) > 0){
					'E58_05' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString()
				}else{
					'E58_05' '0'
				}
				
				sum = BigDecimal.ZERO
				current_Body?.FreightCharge?.findAll{it.ChargeType == blUtil.PREPAID}?.each{current_FreightCharge ->
					if(current_FreightCharge.TotalAmtInPmtCurrency?.toString()){
						sum = sum + current_FreightCharge.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency?.toBigDecimal()
					}
				}
				'E117_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
				
				sum = BigDecimal.ZERO
				current_Body?.Cargo?.each{current_Cargo ->
					if(current_Cargo?.Volume?.Volume){
						sum = sum + new BigDecimal(current_Cargo?.Volume?.Volume)?.multiply(new BigDecimal(10000))
					}
				}
				if(new BigDecimal(util.substring(sum?.divide(new BigDecimal(10000))?.toString(), 1, 9)) != 0){
					'E183_09' blUtil.replaceZeroAfterPoint(util.substring(sum?.divide(new BigDecimal(10000))?.toString(), 1, 9))
				}
				if(current_Body?.Cargo?.find{it.Volume?.VolumeUnit == 'CBF'}){
					'E184_10' 'E'
				}else if(current_Body?.Cargo?.find{it.Volume?.VolumeUnit == 'CFT'}){
					'E184_10' 'E'
				}else if(current_Body?.Cargo?.find{it.Volume?.VolumeUnit  == 'CBM'}){
					'E184_10' 'X'
				}
				
				sum = BigDecimal.ZERO
				current_Body?.Cargo?.each{current_Cargo ->
					if(current_Cargo?.Packaging?.PackageQty){
						sum = sum + new BigDecimal(current_Cargo?.Packaging?.PackageQty)
					}
				}
				'E80_11' util.substring(sum.toString(), 1, 7);
				if(current_Body?.Cargo?.size() > 0 && current_Body?.Cargo[0]?.NetWeight?.WeightUnit){
					if(current_Body?.Cargo[0]?.NetWeight?.WeightUnit == 'TON'){
						'E188_12' 'K'
					}else if(current_Body?.Cargo[0]?.NetWeight?.WeightUnit == 'KGS'){
						'E188_12' 'K'
					}else if(current_Body?.Cargo[0]?.NetWeight?.WeightUnit == 'LBS'){
						'E188_12' 'L'
					}
				}else if(current_Body?.Cargo?.size() > 0 && current_Body?.Cargo[0]?.GrossWeight?.WeightUnit){
					if(current_Body?.Cargo[0]?.GrossWeight?.WeightUnit == 'TON'){
						'E188_12' 'K'
					}else if(current_Body?.Cargo[0]?.GrossWeight?.WeightUnit == 'KGS'){
						'E188_12' 'K'
					}else if(current_Body?.Cargo[0]?.GrossWeight?.WeightUnit == 'LBS'){
						'E188_12' 'L'
					}
				}
				
			}
			filteredFreightCharge?.findAll{it?.ChargeType == blUtil.COLLECT || it?.ChargeType == blUtil.PREPAID && it?.TotalAmtInPmtCurrency}?.eachWithIndex{current_FreightCharge, FreightChargeIndex->
				'Loop_L1' {
					'L1' {
						'E213_01' FreightChargeIndex + 1
						if(current_FreightCharge?.FreightRate){
							def vfreightRate = new BigDecimal(current_FreightCharge?.FreightRate)?.abs()?.toString()
							if(vfreightRate?.startsWith('0.')){
								'E60_02' util?.substring(vfreightRate, 2, vfreightRate?.length() -1)
							}else{
								'E60_02' vfreightRate
							}
							
						}
						def CalculateMethod = current_FreightCharge?.CalculateMethod
						Map L1Map = ['CONTAINER':'PA','GROSS CARGO WEIGHT':'WM','NET CARGO WEIGHT':'WM',
							'LUMP SUM':'LS','MEASUREMENT':'VM','PACKAGE':'PU']
						if(util.substring(CalculateMethod, 1, 1) == '2' || util.substring(CalculateMethod, 1, 1) == '4'){
							'E122_03' 'PA'
						}else if(L1Map.get(CalculateMethod?.toUpperCase())){
							'E122_03' L1Map.get(CalculateMethod?.toUpperCase())
						}else if(L1Map.containsValue((CalculateMethod?.toUpperCase())) || (CalculateMethod?.toUpperCase()) == 'CS'){
							'E122_03' CalculateMethod?.toUpperCase()
						}else if(util.substring(CalculateMethod, 1, 1) == 'B'){
							'E122_03' 'CS'
						}
						
						if(current_FreightCharge?.ChargeType == blUtil.COLLECT){
							if(current_FreightCharge?.TotalAmtInPmtCurrency){
								def total = new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()).multiply(hundred).abs().toString()
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
								def total =  new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()).multiply(hundred).abs().toString()
								if(total?.indexOf('.')>0){
									total = blUtil.replaceZeroAfterPoint(total)
								}
								'E117_06' total
							}
						}
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
						Map<String, String> chargeTypeREMARKS = blUtil.getB2BCdeConversion(util.substring(current_FreightCharge?.ChargeCode, 1, 3)?.toUpperCase(), TP_ID, 'ChargeType', conn)
						if(chargeTypeREMARKS.get("REMARKS")){
							'E276_12' util.substring(chargeTypeREMARKS.get("REMARKS"), 1, 25)?.trim()
						}else{
							'E276_12' '999'
						}
						def out_value = ''
						BigDecimal vCharge = new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString())
						BigDecimal vExchangeRate = new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate)
						BigDecimal vFreightRate = new BigDecimal(current_FreightCharge?.FreightRate)
						BigDecimal vDivNum = new BigDecimal(1)
						
						if(vFreightRate.doubleValue() != 0 && vExchangeRate.doubleValue() > 0) {
							vCharge = vCharge?.divide(vDivNum, 15, BigDecimal.ROUND_FLOOR)
							vCharge = vCharge?.divide(vFreightRate, 15, BigDecimal.ROUND_FLOOR)
							vCharge = vCharge?.divide(vExchangeRate, 15, BigDecimal.ROUND_FLOOR)
							out_value = vCharge?.toBigDecimal()
						} else if(vFreightRate?.doubleValue() != 0&&vExchangeRate?.doubleValue() == 0) {
							vCharge = vCharge?.divide(vDivNum, 15, BigDecimal.ROUND_FLOOR)
							vCharge = vCharge?.divide(vFreightRate, 15, BigDecimal.ROUND_FLOOR)
							out_value = vCharge?.toBigDecimal()
						}
						if(out_value){
							'E220_17' util.substring(blUtil.replaceZeroAfterPoint(out_value?.toBigDecimal()?.setScale(4, BigDecimal.ROUND_HALF_UP)?.toString()),1,11)
						}
						Map calMethodMap = ['LUMP SUM':'FR']
						 if(calMethodMap.get(current_FreightCharge?.CalculateMethod?.toUpperCase())){
							 'E221_18' calMethodMap.get(current_FreightCharge?.CalculateMethod?.toUpperCase())
						 }else{
							 'E221_18' 'NR'
						 }
					}
					if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency){
						'C3' {
							'E100_01' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency
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
			List<String> blockChargeTypeList = ['DET']
			blUtil.filterByChargeCodeBlock(filteredFreightCharge, filteredFreightChargeCNTR, blockChargeTypeList)
			blUtil.filterByTotalAmtInPmtCurrencyIfZero(filteredFreightCharge, filteredFreightChargeCNTR)
			List<String> acceptCurrencyList = ['USD']
			blUtil.filterByTotalAmountCurrency(filteredFreightCharge, filteredFreightChargeCNTR, acceptCurrencyList, null)
			blUtil.filterByChargeType(filteredFreightCharge, filteredFreightChargeCNTR, blUtil.PREPAID)
			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)
			
			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			//mapping
			if(errorKeyList.isEmpty()){
				generateBody(current_Body,  associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml)
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
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
		blUtil.bLReadyOnly(current_Body, false, 'Status is not BL-Ready', errorKeyList)
		blUtil.filterBLTypeMEMO(current_Body,  false, null, errorKeyList)
		blUtil.checkSealNumberLength(current_Body, 15,  true, null, errorKeyList)
		blUtil.missingCntrrNum(current_Body, true, null, errorKeyList)
		blUtil.nonCollectCharges(true, 'No Collect Freight Charge', errorKeyList, current_Body)
		return errorKeyList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){


		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")

		blUtil.checkH1_FlashPoint(node,  true, null, errorKeyList)

	}

	
	void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){
		
		
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")
		
		blUtil.checkB2A_M(node, false, 'Delete Status', errorKeyList)
		blUtil.checkB309_B310_P(node, false, null,  errorKeyList)
		blUtil.checkW0902_W0903_P(node, true, null,  errorKeyList)
		blUtil.checkV108_V101_C(node, true, null,  errorKeyList)
		blUtil.checkV104_M(node, true, null,  errorKeyList)
		blUtil.checkV102_M(node, true, null,  errorKeyList)
		blUtil.checkR402_R403_P(node, true, null,  errorKeyList)
		blUtil.checkC803_C802_R(node, true, null,  errorKeyList)
		blUtil.checkDTM02_DTM03_DTM_05_R(node, true, null,  errorKeyList)
		blUtil.checkDTM04_DTM03_C(node, true, null,  errorKeyList)
		blUtil.checkH107_H108_P(node, true, null,  errorKeyList)
		blUtil.checkL006_L007_P(node, true, null,  errorKeyList)
		blUtil.checkL008_L009_P(node, true, null,  errorKeyList)
		blUtil.checkL011_L004_C(node, true, null,  errorKeyList)
		blUtil.checkL102_L103_P(node, true, 'L102 and L103 should be Pair.',  errorKeyList)
		blUtil.checkL104_L105_L106_R(node, true, null,  errorKeyList)
		blUtil.checkL301_L302_P(node, true, null,  errorKeyList)
		blUtil.checkL309_L310_P(node, true, null,  errorKeyList)
		blUtil.checkL312_L301_C(node, true, null,  errorKeyList)
		blUtil.checkL3_M(node, true, null,  errorKeyList)
		blUtil.checkL507_L506_C(node, true, null,  errorKeyList)
		blUtil.checkN102_N103_R(node, true, null,  errorKeyList)
		blUtil.checkN103_N104_P(node, true, null,  errorKeyList)
		blUtil.checkN702_M(node, true, null,  errorKeyList)
		blUtil.checkN703_N704_P(node, true, null,  errorKeyList)
		blUtil.checkN722_M(node, true, null,  errorKeyList)
		blUtil.checkN902_N903_R(node, true, null,  errorKeyList)
	}

	 String getCS2MasterCityByUNLoc(String UnLocnCde, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for city query. ");
		}
		if (UnLocnCde==null) {
			return ""
		}
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "SELECT STATE_CDE FROM CS2_MASTER_CITY WHERE UN_LOCN_CDE=? and STATE_CDE is not null";
		try {
			pre = conn.prepareStatement(sql);
			//only get the 1st record for this query
			pre.setMaxRows(1);
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, UnLocnCde.toUpperCase());
			result = pre.executeQuery();
	
			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}
	
	
}

