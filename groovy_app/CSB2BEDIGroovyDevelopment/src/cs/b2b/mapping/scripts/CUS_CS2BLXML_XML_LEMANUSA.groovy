package cs.b2b.mapping.scripts

import cs.b2b.core.common.xmlvalidation.ValidateXML
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.POD
import cs.b2b.core.mapping.bean.POL
import cs.b2b.core.mapping.bean.bl.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
/**
 * @author LINJA
 * @pattern after WGLL
 */
public class CUS_CS2BLXML_XML_LEMANUSA {

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
	def yyyyMMddHHmmss = 'yyyyMMddHHmmss'

	def currentSystemDt = null

	BigDecimal sum = null
	BigDecimal hundred = new BigDecimal ('100');
	BigDecimal thousand = new BigDecimal ('1000');

	BigDecimal rateLBS = new BigDecimal('0.4536')
	BigDecimal rateTon = new BigDecimal('1000')
	BigDecimal rateCBF = new BigDecimal('0.0283')

	List<String> ventilationUnitList = ['degrees', 'cbfPerMin', 'cbmPerHour']
	List<String> sealTypeList = ['CA', 'CN', 'CU', 'FW', 'HS', 'MC', 'NP', 'QS', 'RR', 'SH', 'TM', 'VS', 'ZZ']
	Map<String, String> cargoTypeMap = ['AD':'Awkward Dangerous','AW':'Awkward','DG':'Dangerous','GC':'General','RD':'Reefer Dangerous','RF':'Reefer']

	public void generateBody(Body current_Body, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {
//		outXml.'InterchangeControlHeader'{
//			'ControlNumber' ''
//			'SenderId' ''
//			'ReceiverId' ''
//			'DateTime' ''
//			'ControlVersion' ''
//			'UsageIndicator' ''
//			'MessageSessionId' ''
//		}
		outXml.'Update'{
			'BLMessageHeader'{
				'TransactionInfo'{
					'BatchNumber' '1'
					'MessageSender' 'CARGOSMART'
					'MessageRecipient' TP_ID
					'MessageID' 'BLXML'
					'DateCreated' ('TimeZone':'HKT', currentSystemDt.format("yyyyMMddHHmmss"))
//					'FileName' ''
//					'DataSource' ''
					'Version' '8.3'
				}
				Map<String, String> actionTypeMap = ['NEW':'ADD', 'DEL':'DELETE', 'UPD':'UPDATE']
				'Purpose' actionTypeMap.get(current_Body?.TransactionInformation?.Action)
				'SCAC' current_Body?.GeneralInformation?.SCACCode
				if(current_Body?.FreightCharge?.size()>0){
					if(current_Body?.FreightCharge?.findAll{it?.ChargeType == blUtil?.COLLECT}?.size()>0 && current_Body?.FreightCharge?.findAll{it?.ChargeType == blUtil?.PREPAID}?.size()>0){
						'PaymentOption' 'Mixed'
					}else if(current_Body?.FreightCharge?.findAll{it?.ChargeType == blUtil?.COLLECT}?.size()>0){
						'PaymentOption' 'Collect'
					}else{
						'PaymentOption' 'Prepaid'
					}
				}
				'BLNumber' current_Body?.GeneralInformation?.BLNumber
			}
			'BLDetails' {
				'BLInformation'('BillType': current_Body?.GeneralInformation?.BLType) {
					'BLNumber' current_Body?.GeneralInformation?.BLNumber
					'BLStatus' current_Body?.BLStatus[0]?.Status
//					'BLIssuanceDateTime'('null':'true','TimeZone': current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT, 'yyyyMMddHHmmss'))
					if(current_Body?.GeneralInformation?.BLIssueDT?.LocDT){
						'BLIssuanceDateTime'('null':'true','TimeZone':current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.attr_TimeZone?:' ', util.convertXmlDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT, 'yyyyMMddHHmmss'))
					}
					'BLReceiptDateTime' ('null':'true', 'TimeZone':(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT?.attr_TimeZone)?:' ', util.convertXmlDateTime(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT, 'yyyyMMddHHmmss'))

//					'BLCreationDateTime' ('null':'true',util.convertXmlDateTime(current_Body?.GeneralInformation?.BLCreationDT?.LocDT, 'yyyyMMddHHmmss'))
					'BLCreationDateTime' ('null':'true','TimeZone':current_Body?.GeneralInformation?.BLCreationDT?.LocDT?.attr_TimeZone?.trim()?:' ', util.convertXmlDateTime(current_Body?.GeneralInformation?.BLCreationDT?.LocDT, 'yyyyMMddHHmmss'))
					if(current_Body?.GeneralInformation?.BLChangeDT?.LocDT){
						'BLUpdateDateTime' ('null':'true','TimeZone':current_Body?.GeneralInformation?.BLChangeDT?.LocDT?.attr_TimeZone?.trim()?:' ', util.convertXmlDateTime(current_Body?.GeneralInformation?.BLChangeDT?.LocDT, 'yyyyMMddHHmmss'))
					}
					if(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.toString()?.trim()){
						'BLOnBoardDateTime' ('null':'true','TimeZone':(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.attr_TimeZone?.trim())?:' ', util.convertXmlDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT, 'yyyyMMddHHmmss'))
					} else if(current_Body?.GeneralInformation?.BLOnboardDT?.GMT?.trim()){
						'BLOnBoardDateTime' ('null':'true', 'TimeZone':current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.attr_TimeZone?.trim()?:' ',util.convertXmlDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT?.trim(), 'yyyyMMddHHmmss'))
					}else{
						'BLOnBoardDateTime' ('null':'true','TimeZone':' ',' ')
					}
//					if(current_Body?.GeneralInformation?.CaptureOffice){
						'BLCapturingOffice'('PhoneNumber': current_Body?.GeneralInformation?.CaptureOfficePhoneNumber?.trim(), current_Body?.GeneralInformation?.CaptureOffice?.trim())
//					}
					if(current_Body?.GeneralInformation?.BLPaymentOffice?.trim()){
						'Payment'{
							if(current_Body?.GeneralInformation?.BLPaymentOffice?.trim()) {
								'BLPaymentOffice'('PhoneNumber': current_Body?.GeneralInformation?.ContactOfficeCode?.trim()?current_Body?.GeneralInformation?.ContactOfficeCode?.trim():' ', current_Body?.GeneralInformation?.BLPaymentOffice?.trim())
							}
							if(current_Body?.GeneralInformation?.PaymentReceiptDT?.LocDT){
								'PaymentReceiptDateTime' ('TimeZone':current_Body?.GeneralInformation?.PaymentReceiptDT?.LocDT?.attr_TimeZone?.trim(), util.convertXmlDateTime(current_Body?.GeneralInformation?.PaymentReceiptDT?.LocDT?.toString()?.trim(), 'yyyyMMddHHmmss'))
							}
//							'CollectAmount' ''
////						'PrepaidAmount' ''
						}
					}
					'CargoType' cargoTypeMap.get(current_Body?.GeneralInformation?.ShipmentCargoType) ? cargoTypeMap.get(current_Body?.GeneralInformation?.ShipmentCargoType) : 'General'
					if(current_Body?.GeneralInformation?.CargoControlOffice){
						'CargoControlNumber' current_Body?.GeneralInformation?.CargoControlOffice
					}
					'TrafficMode' {
						'OutBound' current_Body?.GeneralInformation?.TrafficMode?.OutBound
						'InBound' current_Body?.GeneralInformation?.TrafficMode?.InBound
					}
					if (current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.trim() && current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.trim() != '0' && current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit?.trim() != '0') {
						'Weight'('Qualifier': 'Gross', 'Units': current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit?.trim() == 'KGS' ? 'KG' :current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit?.trim(), blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.toBigDecimal()?.toString()))
					}
					if(current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit && current_Body?.GeneralInformation?.BLNetWeight?.Weight && current_Body?.GeneralInformation?.BLGrossWeight?.Weight != '0'){
						'Weight'('Qualifier': 'Net', 'Units': current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit, blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLNetWeight?.Weight?.toBigDecimal()?.toString()))
					}
					if (current_Body?.GeneralInformation?.BLVolume?.VolumeUnit && current_Body?.GeneralInformation?.BLVolume?.Volume && current_Body?.GeneralInformation?.BLVolume?.Volume != '0') {
						'Volume'('Units': current_Body?.GeneralInformation?.BLVolume?.VolumeUnit == 'CFT' ? 'CBF' : current_Body?.GeneralInformation?.BLVolume?.VolumeUnit, current_Body?.GeneralInformation?.BLVolume?.Volume)
					}
				}
				'PartyInformation' {
					Map<String, String> partyTypeMap = ['CGN': 'CN', 'FWD': 'FW', 'NPT': 'NP', 'SHP': 'SH']
					current_Body?.Party?.each{ current_Party ->
						if(partyTypeMap.get(current_Party?.PartyType)){
							'Party' {
								'PartyType' partyTypeMap.get(current_Party?.PartyType)
								'PartyName' current_Party?.PartyName?.trim()
								if(current_Party?.CarrierCustomerCode?.trim()){
									'CarrierCustomerCode' current_Party?.CarrierCustomerCode?.trim()
								}
								'PartyLocation'('null':'true') {
									current_Party?.Address?.AddressLines?.each{current_AddressLines ->
										'Address' {
											'AddressLines' current_AddressLines?.AddressLine[0]
										}
									}
//								'Street' ''
									if(current_Party?.Address?.City){
										'City' current_Party?.Address?.City
									}
//								current_Party?.Address?.City?.each {current_City ->
//									'City' current_City
//								}
									if(current_Party?.Address?.County){
										'County' current_Party?.Address?.County
									}
//								current_Party?.Address?.County?.each {current_County ->
//									'County' current_County
//								}
//								'StateProvinceCode' ''
									if(current_Party?.Address?.State){
										'StateProvince' current_Party?.Address?.State
									}
									if(current_Party?.Address?.Country){
										'CountryCode' util.substring(current_Party?.Address?.Country, 1, 3)
									}
									if(current_Party?.Address?.Country){
										'CountryName' util.getCS2MasterCity4CountryNameByCountryCode(current_Party?.Address?.Country, conn)
									}
									if(current_Party?.Address?.LocationCode?.UNLocationCode || current_Party?.Address?.LocationCode?.SchedKDCode){
										'LocationCode'('null':'true') {
											'MutuallyDefinedCode' ' '
											'UNLocationCode' current_Party?.Address?.LocationCode?.UNLocationCode
											if (current_Party?.Address?.LocationCode?.SchedKDCode) {
												'SchedKDCode'('Type': (current_Party?.Address?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), current_Party?.Address?.LocationCode?.SchedKDCode)
											}
										}
									}
									if(current_Party?.Address?.PostalCode){
										'PostalCode' current_Party?.Address?.PostalCode
									}
								}
								if(current_Party?.Contact){
									'ContactPerson' {
										if(current_Party?.Contact?.FirstName) {
											'FirstName' current_Party?.Contact?.FirstName
										}
										if(current_Party?.Contact?.LastName) {
											'LastName' current_Party?.Contact?.LastName
										}

										'Phone' ('null':'true'){
											if (current_Party?.Contact?.ContactPhone?.CountryCode) {
												'CountryCode' util.substring(current_Party?.Contact?.ContactPhone?.CountryCode, 1, 3)
											}
											if (current_Party?.Contact?.ContactPhone?.AreaCode) {
												'AreaCode' current_Party?.Contact?.ContactPhone?.AreaCode
											}
											if (current_Party?.Contact?.ContactPhone?.Number) {
												'Number' current_Party?.Contact?.ContactPhone?.Number
											}
										}
										if(current_Party?.Contact?.ContactEmailAddress) {
											'Email' current_Party?.Contact?.ContactEmailAddress
										}
//									'Type' ''
									}
								}
							}
						}

					}
				}
				'ShipmentDetails' {
					'BookingInformation' {
						'BookingNumber' current_Body?.GeneralInformation?.CarrierBookingNumber[0]
					}
					'RouteInformation' {
						'EventDate' ('null':'true') {
//							'FullPickup' ''
							if(current_Body?.Route?.FullReturnCutoffDT?.LocDT){
								'FullReturnCutoff' ('TimeZone': current_Body?.Route?.FullReturnCutoffDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, 'yyyyMMddHHmmss'))
							}

							if(current_Body?.Route?.ArrivalAtFinalHub[0]?.LocDT){
								'ArrivalAtFinalHub' ('TimeZone': current_Body?.Route?.ArrivalAtFinalHub[0]?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.Route?.ArrivalAtFinalHub[0]?.LocDT, 'yyyyMMddHHmmss'))
							}
//							'Delivery' ''
						}
						'Haulage' {
							'Outbound' current_Body?.Route?.Haulage?.OutBound
							'Inbound' current_Body?.Route?.Haulage?.InBound
						}

						POR por = current_Body?.Route?.POR
						FND fnd = current_Body?.Route?.FND

						'LocationDetails' {
							'FunctionCode' 'POR'
							'LocationName'('null':'true', util.substring(por?.Facility?.FacilityName, 1, 60).trim())
							'LocationDetails' ('null':'true') {
//								'Address' {
//										'AddressLines' ''
//								}
//								'Street' ''
								if(por?.CityDetails?.City){
									'City' por?.CityDetails?.City?.trim()
								}
								if(por?.CityDetails?.County){
									'County' por?.CityDetails?.County?.trim()
								}
								if(por?.CSStandardCity?.CSStateCode){
									'StateProvinceCode' por?.CSStandardCity?.CSStateCode?.trim()
								}
								if(por?.CityDetails?.State){
									'StateProvince' por?.CityDetails?.State?.trim()
								}
								if(por?.CSStandardCity?.CSCountryCode){
									'CountryCode' por?.CSStandardCity?.CSCountryCode?.trim()
								}
								if(por?.CityDetails?.Country){
									'CountryName' por?.CityDetails?.Country?.trim()
								}
								'LocationCode' ('null':'true'){
//									'MutuallyDefinedCode' ''
//									'UNLocationCode' por?.CityDetails?.LocationCode?.UNLocationCode
									if(por?.CityDetails?.LocationCode?.UNLocationCode && por?.CityDetails?.LocationCode?.MutuallyDefinedCode?.size()<= 0){
										'UNLocationCode' por?.CityDetails?.LocationCode?.UNLocationCode
									}
//									'SchedKDCode'('Type': (por?.CityDetails?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), por?.CityDetails?.LocationCode?.SchedKDCode)
									if(por?.CityDetails?.LocationCode?.SchedKDCode){
										'SchedKDCode' ('Type':por?.CityDetails?.LocationCode?.SchedKDType, por?.CityDetails?.LocationCode?.SchedKDCode)
									}
								}

//								'PostalCode' ''
							}
//							'EventDate' {
//								'Arrival' ''
//								'Departure' ''
//							}
						}

						'LocationDetails' {
							'FunctionCode' 'FND'
							'LocationName' ('null':'true',util.substring(fnd?.Facility?.FacilityName, 1, 60).trim())
							'LocationDetails' ('null':'true'){
//								'Address' {
//									'AddressLines' ''
//								}
//								'Street' ''
								if(fnd?.CityDetails?.City){
									'City' fnd?.CityDetails?.City?.trim()
								}
//								'City' fnd?.CityDetails?.City
								if(fnd?.CityDetails?.County){
									'County' fnd?.CityDetails?.County?.trim()
								}
								if(fnd?.CSStandardCity?.CSStateCode){
									'StateProvinceCode' fnd?.CSStandardCity?.CSStateCode?.trim()
								}
								if(fnd?.CityDetails?.State){
									'StateProvince' fnd?.CityDetails?.State?.trim()
								}
								if(fnd?.CSStandardCity?.CSCountryCode){
									'CountryCode' fnd?.CSStandardCity?.CSCountryCode?.trim()
								}
								if(fnd?.CityDetails?.Country){
									'CountryName' fnd?.CityDetails?.Country?.trim()
								}
								'LocationCode'('null':'true'){
									if(fnd?.CityDetails?.LocationCode?.UNLocationCode && fnd?.CityDetails?.LocationCode?.MutuallyDefinedCode?.size()<=0){
										'UNLocationCode' fnd?.CityDetails?.LocationCode?.UNLocationCode
									}
									if(fnd?.CityDetails?.LocationCode?.SchedKDCode){
										'SchedKDCode' ('Type':fnd?.CityDetails?.LocationCode?.SchedKDType, fnd?.CityDetails?.LocationCode?.SchedKDCode)
									}
								}
//								'PostalCode' ''
							}
//							'EventDate' {
//								'Arrival' ''
//								'Departure' ''
//							}
						}

						current_Body?.Route?.OceanLeg?.eachWithIndex{ current_OceanLeg, current_OceanLeg_index ->

							POL pol = current_OceanLeg?.POL
							POD pod = current_OceanLeg?.POD
							Map<String,String> VDirectionMap = ['E':'East','W':'West','N':'North']

								'PortDetails' {
									'FunctionCode' 'POL'
									'LocationName' ('null':'true',pol?.Port?.PortName?.trim())
									'SequenceNumber' current_OceanLeg?.LegSeq?.trim()
									'LocationDetails' {
//										'Address' {
//											'AddressLines' ''
//										}
//										'Street' ''
										if(pol?.Port?.City?.trim()){
											'City' pol?.Port?.City?.trim()
										}
										if(pol?.Port?.County?.trim()){
											'County' pol?.Port?.County?.trim()
										}
//										'StateProvinceCode' ''
										if(pol?.Port?.State?.trim()){
											'StateProvince' pol?.Port?.State?.trim()
										}
										if(pol?.Port?.CSCountryCode?.trim()){
											'CountryCode' pol?.Port?.CSCountryCode?.trim()
										}
										if(pol?.Port?.Country?.trim()){
											'CountryName' pol?.Port?.Country?.trim()
										}
										'LocationCode' ('null':'true'){
//											'MutuallyDefinedCode' ''
											if(pol?.Port?.LocationCode?.UNLocationCode?.trim()){
												'UNLocationCode' pol?.Port?.LocationCode?.UNLocationCode?.trim()
											}
											if (pol?.Port?.LocationCode?.SchedKDCode) {
												'SchedKDCode'('Type': (pol?.Port?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), pol?.Port?.LocationCode?.SchedKDCode)
											}
										}
//										'PostalCode' ''
									}

									'EventDate'('null':'true') {
//										if(current_OceanLeg?.DepartureDT?.findAll{it?.attr_Indicator == 'A'}?.size()>0){
//											println (current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT?.attr_TimeZone)
//											'Departure'('TimeZone':  current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT?.attr_TimeZone?:current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT?.attr_TimeZone, 'EstActIndicator': '1', util.convertXmlDateTime(current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT?.toString()?.trim(), "yyyyMMddHHmmss"))
//										}else if(current_OceanLeg?.DepartureDT?.findAll {it?.attr_Indicator}?.size()>0){
//											'Departure'('TimeZone': current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT?.attr_TimeZone?:' ', 'EstActIndicator': '0', util.convertXmlDateTime(current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT?.toString()?.trim(), "yyyyMMddHHmmss"))
//										}
										LocDT actDepartureDTLocDT = current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
										LocDT estDepartureDTLocDT = current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT
										if(actDepartureDTLocDT){
											'Departure'('TimeZone': actDepartureDTLocDT?.attr_TimeZone ? actDepartureDTLocDT?.attr_TimeZone :estDepartureDTLocDT?.attr_TimeZone, 'EstActIndicator': '1', util.convertXmlDateTime(actDepartureDTLocDT?.LocDT, "yyyyMMddHHmmss"))
										}else if(estDepartureDTLocDT){
											'Departure'('TimeZone': estDepartureDTLocDT?.attr_TimeZone, 'EstActIndicator': '0', util.convertXmlDateTime(estDepartureDTLocDT?.LocDT, "yyyyMMddHHmmss"))
										}
									}
									'VesselVoyageInformation' {
										'VoyageEvent' 'Loading'
										if(current_OceanLeg?.SVVD?.Loading?.Service?.trim()){
											'ServiceName'('Code': current_OceanLeg?.SVVD?.Loading?.Service,' ')
										}
										'VoyageNumberDirection' "${current_OceanLeg?.SVVD?.Loading?.Voyage?.trim()?:' '}${VDirectionMap[current_OceanLeg?.SVVD?.Loading?.Direction]?:'South'}"


										'VesselInformation' {
											'VesselCode' ('null':'true', 'LloydsCode':current_OceanLeg?.SVVD?.Loading?.LloydsNumber?.trim()?:' ', 'CallSign':current_OceanLeg?.SVVD?.Loading?.CallSign?.trim()?:' ', current_OceanLeg?.SVVD?.Loading?.Vessel?.trim())
											'VesselName' ('null':'true',current_OceanLeg?.SVVD?.Loading?.VesselName?.trim())
										}
									}
								}

							if (pod) {
								'PortDetails' {
									'FunctionCode' 'POD'
									'LocationName' ('null':'true',pod?.Port?.PortName)
									'SequenceNumber' current_OceanLeg?.LegSeq
									'LocationDetails' {
//										'Address' {
//											'AddressLines' ''
//										}
//										'Street' ''
										if(pod?.Port?.City){
											'City' pod?.Port?.City
										}
										if(pod?.Port?.County){
											'County' pod?.Port?.County
										}
//										'StateProvinceCode' ''
										if(pod?.Port?.State){
											'StateProvince' pod?.Port?.State
										}
										if(pod?.Port?.CSCountryCode){
											'CountryCode' util.substring(pod?.Port?.CSCountryCode, 1, 3)
										}
										if(pod?.Port?.Country){
											'CountryName' pod?.Port?.Country
										}
										if(pod?.Port?.LocationCode?.UNLocationCode && pod?.Port?.LocationCode?.SchedKDCode) {
											'LocationCode' ('null':'true'){
//											'MutuallyDefinedCode' ''
												'UNLocationCode' pod?.Port?.LocationCode?.UNLocationCode
												if (pod?.Port?.LocationCode?.SchedKDCode) {
													'SchedKDCode'('Type': (pod?.Port?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), pod?.Port?.LocationCode?.SchedKDCode)
												}
											}
										}
//										'PostalCode' ''
									}

									'EventDate'('null':'true') {
//									if(current_OceanLeg?.ArrivalDT?.findAll{it.attr_Indicator == 'A'}?.size()>0){
//										'Arrival'('TimeZone': current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT?.attr_TimeZone?:current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT?.attr_TimeZone, 'EstActIndicator': '1' , util.convertXmlDateTime(current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, "yyyyMMddHHmmss"))
//									}else if(current_OceanLeg?.ArrivalDT?.findAll {it?.attr_Indicator == 'E'}?.size()>0){
//										'Arrival'('TimeZone': current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT?.attr_TimeZone?:' ', 'EstActIndicator': '0' , util.convertXmlDateTime(current_OceanLeg?.ArrivalDT?.find {it?.attr_Indicator =='E'}?.LocDT, "yyyyMMddHHmmss"))
//									}
										LocDT actArrivalDTLocDT = current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == "A"}?.LocDT
										LocDT estArrivalDTLocDT = current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == "E"}?.LocDT
										if(actArrivalDTLocDT){
											'Arrival'('TimeZone': actArrivalDTLocDT?.attr_TimeZone ?actArrivalDTLocDT?.attr_TimeZone :estArrivalDTLocDT?.attr_TimeZone, 'EstActIndicator': '1' , util.convertXmlDateTime(actArrivalDTLocDT, "yyyyMMddHHmmss"))
										}else if(estArrivalDTLocDT){
											'Arrival'('TimeZone': estArrivalDTLocDT?.attr_TimeZone, 'EstActIndicator': '0' , util.convertXmlDateTime(estArrivalDTLocDT, "yyyyMMddHHmmss"))
										}
									}


									'VesselVoyageInformation' {
										'VoyageEvent' 'Discharging'
										if(current_OceanLeg?.SVVD?.Discharge?.Service?.trim()){
											'ServiceName'('Code':current_OceanLeg?.SVVD?.Discharge?.Service?.trim())
										}
										'VoyageNumberDirection' "${current_OceanLeg?.SVVD?.Discharge?.Voyage?.trim()?:' '}${VDirectionMap[current_OceanLeg?.SVVD?.Discharge?.Direction]?:'South'}"

										'VesselInformation' {
											'VesselCode' ('null':'true', 'LloydsCode':current_OceanLeg?.SVVD?.Discharge?.LloydsNumber?.trim()?:' ', 'CallSign':current_OceanLeg?.SVVD?.Discharge?.CallSign?.trim()?:' ', current_OceanLeg?.SVVD?.Discharge?.Vessel)
											'VesselName' ('null':'true',current_OceanLeg?.SVVD?.Discharge?.VesselName?.trim())
										}
									}
								}
							}
						}
						current_Body?.Route?.StopOff?.eachWithIndex {current_StopOff, current_StopOff_index ->
							'StopOff' {
								'SequenceNumber' current_StopOff?.SequenceNumber
								'PickupDetails' {
									'FacilityCode' current_StopOff?.PickupDetails?.Facility?.FacilityCode
									'FacilityName' current_StopOff?.PickupDetails?.Facility?.FacilityName
//									'LocationName' ''
									'LocatoionDetails' {
//										'Address' {
//											'AddressLines' ''
//										}
//										'Street' ''
										'City' current_StopOff?.PickupDetails?.City?.City
										'County' current_StopOff?.PickupDetails?.City?.County
//										'StateProvinceCode' ''
										'StateProvince' current_StopOff?.PickupDetails?.City?.State
										String stopOffPickupCountryCode = "ZZ"
										if(current_StopOff?.PickupDetails?.City?.Country){
											String countryCode = util.getCS2MasterCity4CountryCodeByCountryName(current_StopOff?.PickupDetails?.City?.Country, conn)
											stopOffPickupCountryCode = countryCode ? countryCode : stopOffPickupCountryCode
										}
//										'CountryCode' stopOffPickupCountryCode
										'CountryName' current_StopOff?.PickupDetails?.City?.Country
										'LocationCode' ('null':'true'){
//											'MutuallyDefinedCode' ''
											'UNLocationCode' current_StopOff?.PickupDetails?.City?.LocationCode?.UNLocationCode
											'SchedKDCode' ('null':'true','Type':current_StopOff?.PickupDetails?.City?.LocationCode?.SchedKDType, current_StopOff?.PickupDetails?.City?.LocationCode?.SchedKDCode)
										}
//										'PostalCode' ''
									}
								}
								'ReturnDetails' {
									'FacilityCode' current_StopOff?.ReturnDetails?.Facility?.FacilityCode
									'FacilityName' current_StopOff?.ReturnDetails?.Facility?.FacilityName
//									'LocationName' ''
									'LocationDetails' {
										'City' ('null':'true',current_StopOff?.ReturnDetails?.City?.City)
										'County' ('null':'true', current_StopOff?.ReturnDetails?.City?.County)
										'StateProvince' current_StopOff?.ReturnDetails?.City?.State
										'CountryName' current_StopOff?.ReturnDetails?.City?.Country
										'LocationCode' ('null':'true'){
											'UNLocationCode' current_StopOff?.ReturnDetails?.City?.LocationCode?.UNLocationCode
											'SchedKDCode' ('Type':current_StopOff?.ReturnDetails?.City?.LocationCode?.SchedKDType, current_StopOff?.ReturnDetails?.City?.LocationCode?.SchedKDCode)
										}
									}
								}
							}
						}
					}
					'EquipmentInformation' {
						current_Body?.Container?.each { current_Container ->
							'Containers' {
								'IsSOC' current_Container?.IsSOC?.toUpperCase() == 'TRUE' ? '1' : '0'
								'ContainerType' current_Container?.CS1ContainerSizeType?.trim()
								'ContainerNumber'('CheckDigit': current_Container?.ContainerCheckDigit?.trim()?:' ', current_Container?.ContainerNumber?.trim())
								'SealNumber'('null':'true', 'Type': current_Container?.Seal[0]?.SealType?.trim(), current_Container?.Seal[0]?.SealNumber?.trim())
								if (current_Container?.GrossWeight?.Weight?.trim()) {
									'Weight'('Qualifier': 'Gross', 'Units': (current_Container?.GrossWeight?.WeightUnit == 'KGS')? 'KG' : current_Container?.GrossWeight?.WeightUnit?.trim(), blUtil.replaceZeroAfterPoint(current_Container?.GrossWeight?.Weight?.toBigDecimal()?.toString()))
								}
								'TrafficMode'{
									'OutBound' current_Container?.TrafficMode?.OutBound ?current_Container?.TrafficMode?.OutBound: current_Body?.GeneralInformation?.TrafficMode?.OutBound
									'InBound' current_Container?.TrafficMode?.InBound ?current_Container?.TrafficMode?.InBound: current_Body?.GeneralInformation?.TrafficMode?.InBound
								}
							}
						}
					}
					'CargoInformation' {
						current_Body?.Cargo?.each { current_Cargo ->
							'CargoGroup' {
								'CargoNature' cargoTypeMap.get(current_Cargo?.CargoNature) ? cargoTypeMap.get(current_Cargo?.CargoNature) : 'General'
//								'CommodityCode' ''
								if (current_Cargo?.NetWeight?.Weight?.trim() && current_Cargo?.NetWeight?.Weight?.trim() != '0') {
									'Weight'('Qualifier': 'Net', 'Units': (current_Cargo?.NetWeight?.WeightUnit?.trim() == 'KGS') ? 'KG' : current_Cargo?.NetWeight?.WeightUnit?.trim(), blUtil.replaceZeroAfterPoint(current_Cargo?.NetWeight?.Weight?.trim()))
								}
								if (current_Cargo?.GrossWeight?.Weight?.trim() && current_Cargo?.GrossWeight?.Weight?.trim() != '0') {
									'Weight'('Qualifier': 'Gross', 'Units': (current_Cargo?.GrossWeight?.WeightUnit?.trim() == 'KGS') ? 'KG' : current_Cargo?.GrossWeight?.WeightUnit?.trim(),blUtil.replaceZeroAfterPoint(current_Cargo?.GrossWeight?.Weight?.trim()))
								}
								if (current_Cargo?.Volume?.Volume?.trim() && current_Cargo?.Volume?.Volume?.trim() != '0') {
									'Volume'('Units': current_Cargo?.Volume?.VolumeUnit?.trim() == 'CFT' ? 'CBF' : current_Cargo?.Volume?.VolumeUnit?.trim(), current_Cargo?.Volume?.Volume?.trim())
								}
//								String packageQty = ''
//								if (current_Cargo?.CargoNature == 'GC') {
//									if (current_Cargo?.GCPackageUnit?.find {it.PackageSeqNumber == '1'}?.PackageUnitQuantity) {
//										packageQty = current_Cargo?.GCPackageUnit?.find {it.PackageSeqNumber == '1'}?.PackageUnitQuantity
//									} else if (current_Cargo?.Packaging?.PackageQty) {
//										packageQty = current_Cargo?.Packaging?.PackageQty
//									}
//								} else if (current_Cargo?.CargoNature == 'RF') {
//									packageQty = current_Cargo?.ReeferCargoSpec?.find {it.RFPackageUnit.find { it.PackageSeqNumber == '1' }}?.RFPackageUnit?.get(0)?.PackageUnitQuantity
//								} else if (current_Cargo?.CargoNature == 'DG') {
//									packageQty = current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity
//								} else if (current_Cargo?.CargoNature == 'AW') {
//									packageQty = current_Cargo?.AWCargoSpec?.find {it.AWPackageUnit.find { it.PackageSeqNumber == '1' }}?.AWPackageUnit?.get(0)?.PackageUnitQuantity
//								} else if (current_Cargo?.CargoNature == 'AD') {
//									if (current_Cargo?.AWCargoSpec?.find {it.AWPackageUnit.find { it.PackageSeqNumber == '1' }}?.AWPackageUnit?.get(0)?.PackageUnitQuantity) {
//										packageQty = current_Cargo?.AWCargoSpec?.find {it.AWPackageUnit.find { it.PackageSeqNumber == '1' }}?.AWPackageUnit?.get(0)?.PackageUnitQuantity
//									} else if (current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity) {
//										packageQty = current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity
//									}
//								} else if (current_Cargo?.CargoNature == 'RD') {
//									if (current_Cargo?.ReeferCargoSpec?.find {it.RFPackageUnit.find { it.PackageSeqNumber == '1' }}?.RFPackageUnit?.get(0)?.PackageUnitQuantity) {
//										packageQty = current_Cargo?.ReeferCargoSpec?.find {it.RFPackageUnit.find { it.PackageSeqNumber == '1' }}?.RFPackageUnit?.get(0)?.PackageUnitQuantity
//									} else if (current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity) {
//										packageQty = current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity
//									}
//								}
//								if(StringUtil.isEmpty(packageQty)){
//									packageQty = current_Cargo?.Packaging?.PackageQty
//								}
								'Package'('Type': current_Cargo?.Packaging?.PackageType?.trim(), current_Cargo?.Packaging?.packageQty?.trim())
								if(current_Cargo?.CurrentContainerNumber?.trim()){
									'ContainerNumber'('CheckDigit': current_Cargo?.CurrentContainerCheckDigit?.trim()?:' ', current_Cargo?.CurrentContainerNumber?.trim())
								}

								'CargoDescription' {
//									for (int i = 0; i * 60 < current_Cargo?.CargoDescription?.length(); i++) {
									'DescriptionLine' current_Cargo?.CargoDescription
//									}
								}
								'MarksAndNumbers'('null':'true') {
									current_Cargo?.MarksAndNumbers?.each { current_MarksAndNumbers ->
										'MarksAndNumbersLine' current_MarksAndNumbers.MarksAndNumbersLine
									}
								}

//								'RateAndCharges' {
//									'Charges' {
//										'ShowIndicator' ''
//										'ChargeTypeCode' ''
//										'FreightRate' ''
//										'PrepaidAmount' ''
//										'CollectAmount' ''
//										'PayableAt' ''
//										'PayableBy' {
//											'CarrierCustomerCode' ''
//											'ContactPerson' {
//												'FirstName' ''
//												'LastName' ''
//												'Phone' {
//													'CountryCode' ''
//													'AreaCode' ''
//													'Number' ''
//												}
//												'Fax' {
//													'CountryCode' ''
//													'AreaCode' ''
//													'Number' ''
//												}
//												'Email' ''
//												'Type' ''
//											}
//											'Address' {
//												'AddressLines' ''
//											}
//											'CityName' ''
//											'County' ''
//											'StateProvinceCode' ''
//											'StateProvince' ''
//											'CountryCode' ''
//											'CountryName' ''
//											'PostalCode' ''
//										}
//									}
//								}
								if (current_Cargo?.DGCargoSpec?.size() > 0) {
									'DangerousCargo' {
										current_Cargo?.DGCargoSpec?.each { current_DGCargo ->
											'DangerousCargoInfo' {
												'HazardousMaterial' {
													'DGRegulator' current_DGCargo?.DGRegulator?.trim()
													'IMCOClass' current_DGCargo?.IMOClass?.trim()
													'IMDGPage' current_DGCargo?.IMDGPage?.trim()
													'UNNumber' current_DGCargo?.UNNumber?.trim()
													'TechnicalShippingName' current_DGCargo?.TechnicalName?.trim()
													'ProperShippingName' current_DGCargo?.ProperShippingName?.trim()
													'EMSNumber' current_DGCargo?.EMSNumber?.trim()
													'PSAClass' current_DGCargo?.PSAClass?.trim()
													'MFAGPageNumber' current_DGCargo?.MFAGNumber?.trim()
													if (current_DGCargo?.FlashPoint?.Temperature?.trim()) {
														'FlashPoint'('Units': current_DGCargo?.FlashPoint?.TemperatureUnit?.trim(), current_DGCargo?.FlashPoint?.Temperature?.trim())
													} else {
														'FlashPoint'('Units': 'C', '999.99')
													}
//													'FlashPoint' ('null':'true','Units': current_DGCargo?.FlashPoint?.TemperatureUnit?current_DGCargo?.FlashPoint?.TemperatureUnit:'C',current_DGCargo?.FlashPoint?.Temperature?current_DGCargo?.FlashPoint?.Temperature:'999.99')
//													if (StringUtil.isNotEmpty(current_DGCargo?.ElevatedTemperature?.TemperatureUnit) && StringUtil.isNotEmpty(current_DGCargo?.ElevatedTemperature?.Temperature)) {
//														'DGElevationTemperature'('Units': current_DGCargo?.ElevatedTemperature?.TemperatureUnit, blUtil.replaceZeroAfterPoint(current_DGCargo?.ElevatedTemperature?.Temperature?.toBigDecimal()?.toString()))
//													}
													if(current_DGCargo?.ElevatedTemperature?.Temperature?.trim()){
														'DGElevationTemperature'('Units':current_DGCargo?.ElevatedTemperature?.TemperatureUnit?.trim(),current_DGCargo?.ElevatedTemperature?.Temperature?.trim())
													}
													'State' current_DGCargo?.State?.trim()
//													'ApprovalCode' util.substring(current_DGCargo?.ApprovalCode, 1, 20) //removed from B2BSCR20170626004090d
//													if(current_DGCargo?.Label?.size() > 0) {
//														current_DGCargo?.Label?.eachWithIndex { current_Label, index ->
//															if (index < 4) {
//																'Label'('null': 'true', current_Label)
//															}
//														}
//													}else{
//														'Label'('null': 'true')
//													}
													'Label' current_DGCargo?.Label[0]?.trim()
													if (current_DGCargo?.NetWeight?.Weight?.trim()) {
														'Weight'('Qualifier': 'Net', 'Units': (current_DGCargo?.NetWeight?.WeightUnit?.trim() == 'KGS')?'KG':current_DGCargo?.NetWeight?.WeightUnit?.trim(), blUtil.replaceZeroAfterPoint(current_DGCargo?.NetWeight?.Weight?.trim()))
													}
													if(current_DGCargo?.GrossWeight?.Weight){
														'Weight'('Qualifier': 'Gross', 'Units': (current_DGCargo?.GrossWeight?.WeightUnit?.trim() == 'KGS')?'KG':current_DGCargo?.GrossWeight?.WeightUnit?.trim(),blUtil.replaceZeroAfterPoint(current_DGCargo?.GrossWeight?.Weight?.trim()))
													}
													if (current_DGCargo?.NetExplosiveWeight?.Weight?.trim()) {
														'NetExplosiveWeight'('Units': current_DGCargo?.NetExplosiveWeight?.WeightUnit?.trim() == 'KGS' ? 'KG' : current_DGCargo?.NetExplosiveWeight?.WeightUnit?.trim(), blUtil.replaceZeroAfterPoint(current_DGCargo?.NetExplosiveWeight?.Weight?.trim()))
													}
													'Indicators' {
														'isMarinePollutant' current_DGCargo?.isMarinePollutant?.trim()?.toUpperCase() == 'TRUE' ? '1' : '0'
														'isInhalationHazardous' current_DGCargo?.IsInhalationHazardous?.trim()?.toUpperCase() == 'TRUE' ? '1' : '0'
														'isLimitedQuantity' current_DGCargo?.isLimitedQuantity?.trim()?.toUpperCase() == 'TRUE' ? '1' : '0'
														'isReportableQuantity' current_DGCargo?.isMarinePollutant?.trim()?.toUpperCase() == 'TRUE' ? '1' : '0'
														'isEmptyUnclean' current_DGCargo?.IsEmptyUnclean?.trim()?.toUpperCase() == 'TRUE' ? '1' : '0'
													}
													Map<String, String> numberRomanMap = ['1':'I' , '2':'II', '3':'III']
													'Package' ('null':'true'){
														if(util.substring(current_DGCargo?.PackageGroup?.Code?.trim(),1,3)) {
															'PackagingGroupCode' util.substring(current_DGCargo?.PackageGroup?.Code?.trim(),1,3)
														}
														'InnerPackageDescription'('Type': current_DGCargo?.PackageGroup?.InnerPackageDescription?.PackageType?.trim(), "${current_DGCargo?.PackageGroup?.InnerPackageDescription?.PackageType?.trim()}${current_DGCargo?.PackageGroup?.InnerPackageDescription?.PackageQty?.trim()}")
														'OuterPackageDescription'('Type': current_DGCargo?.PackageGroup?.OuterPackageDescription?.PackageType?.trim(), "${current_DGCargo?.PackageGroup?.OuterPackageDescription?.PackageType?.trim()}${current_DGCargo?.PackageGroup?.OuterPackageDescription?.PackageQty?.trim()}")

													}
													'Remarks' {
														'RemarksLines' current_DGCargo?.Remarks?.trim()
													}
												}
												'EmergencyContactDetails'('null':'true') {
													'FirstName' ('null':'true',current_DGCargo?.EmergencyContact[0]?.FirstName?.trim())
													'LastName' ('null':'true',current_DGCargo?.EmergencyContact[0]?.LastName?.trim())
													'Phone'('null':'true') {
														'Number' ('null':'true',current_DGCargo?.EmergencyContact[0]?.ContactPhone?.Number?.trim())
													}
													if(current_DGCargo?.EmergencyContact?.Type){
														'Type' ('null':'true',current_DGCargo?.EmergencyContact[0]?.Type)
													}
												}
											}
										}
									}
								}
								if(current_Cargo?.ReeferCargoSpec?.size() > 0) {
									Map<String, String> atmosphereTypeMap = ['AFAM':'AFAM', 'CA':'CA', 'MA':'MA', 'MAG':'MG', 'NP':'NP']
									Map<String, String> romanNumeralMap = ['I':'1', 'II':'2', 'III':'3']
									'ReeferCargo' {
										current_Cargo?.ReeferCargoSpec?.each {current_ReeferCargoSpec ->

											'ReeferCargoInfo' {
												'ReeferSettings' ('AtmosphereType':current_ReeferCargoSpec?.AtmosphereType?.trim(),'GenSetType':current_ReeferCargoSpec?.GensetType?.trim(),'CO2':current_ReeferCargoSpec?.CO2?.trim(), 'O2': current_ReeferCargoSpec?.O2?.trim(),
														'Humidity':current_ReeferCargoSpec?.DehumidityPercentage?.trim(), 'PreCooling': current_ReeferCargoSpec?.IsPreCoolingReq?.trim()?.toUpperCase() == "TRUE" ? '1' : '0' , 'CAIndicator':current_ReeferCargoSpec?.IsControlledAtmosphere?.trim()?.toUpperCase() == "TRUE" ? '1' :'0' ,
														'ROIndicator':current_ReeferCargoSpec?.IsReeferOperational?.trim()?.toUpperCase() == "TRUE" ? '1' : '0'){
													'Temperature' ('Units':current_ReeferCargoSpec?.Temperature?.TemperatureUnit?.trim(), current_ReeferCargoSpec?.Temperature?.Temperature?.trim())
													if(current_ReeferCargoSpec?.Ventilation?.Ventilation?.trim()){
														'Ventilation' ('Units':current_ReeferCargoSpec?.Ventilation?.VentilationUnit?.trim(), current_ReeferCargoSpec?.Ventilation?.Ventilation?.trim())
													}
													'SensitiveCargoDesc' ('null':'true',current_ReeferCargoSpec?.SensitiveCargoDesc?.trim())
//													'Remarks' {
//														'RemarksLines' ''
//													}
												}
												'EmergencyContactDetails' ('null':'true'){
													'FirstName' ('null':'true',current_ReeferCargoSpec?.EmergencyContact[0]?.FirstName?.trim())
													'LastName' ('null':'true',current_ReeferCargoSpec?.EmergencyContact[0]?.LastName?.trim())
													'Phone'('null':'true') {
														'Number'('null':'true', current_ReeferCargoSpec?.EmergencyContact[0]?.ContactPhone?.Number?.trim())
													}
													'Type'('null':'true',current_ReeferCargoSpec?.EmergencyContact[0]?.Type)

												}
											}
										}
									}
								}
								if (current_Cargo?.AWCargoSpec?.size() > 0) {
									'AwkwardCargo' {
										current_Cargo?.AWCargoSpec?.each { current_AWCargo ->
											'AwkwardCargoInfo' {
												'AwkwardCargoDetails' {
													'Height'('Units': current_AWCargo?.Height?.LengthUnit?.trim(), current_AWCargo?.Height?.Length?.trim())
													'Width'('Units': current_AWCargo?.Width?.LengthUnit?.trim(), current_AWCargo?.Width?.Length?.trim())
													'Length'('Units': current_AWCargo?.Length?.LengthUnit?.trim(), current_AWCargo?.Length?.Length?.trim())
												}
												'EmergencyContactDetails' ('null':'true'){
													'FirstName'('null':'true',current_AWCargo?.EmergencyContact[0]?.FirstName?.trim())
													'LastName' ('null':'true',current_AWCargo?.EmergencyContact[0]?.LastName?.trim())
													'Phone'('null':'true') {
														'Number' ('null':'true',current_AWCargo?.EmergencyContact[0]?.ContactPhone?.Number?.trim())
														}
														'Type' ('null':'true',current_AWCargo?.EmergencyContact[0]?.Type?.trim())

												}
											}
										}
									}
								}
							}
						}
					}
					Map<String, String> referenceTypeMap = ['FID':'FI', 'PO':'PO', 'SC':'E8', 'SIR':'SIR', 'SR':'SR','CSBKG':'CSBKG']
					Map<String, String> referenceNameMap = ['FID':'File Identifier', 'PO':'Purchase Order Number',
															'SC':'Service Contract Number', 'SIR':'Shipping Instruction Reference', 'SR':'Shipper Reference','CSBKG':'CS Reference Number']
					'ExternalReferences'('null':'true') {
						if(current_Body?.CarrierRate?.findAll {it?.CSCarrierRateType == 'SC'}?.size()>0){
							'References'{
								'ReferenceType' 'E8'
								'ReferenceName' 'Service Contract Number'
								'ReferenceNumber' util.substring(current_Body?.CarrierRate?.findAll{it?.CSCarrierRateType == 'SC'}[0]?.CarrierRateNumber, 1, 35)
							}
						}
						current_Body?.ExternalReference?.each {current_ExternalReference ->
							'References' {
								'ReferenceType' current_ExternalReference?.CSReferenceType
								if(current_ExternalReference?.ReferenceDescription){
									'ReferenceName' current_ExternalReference?.ReferenceDescription
								}
								'ReferenceNumber' util.substring(current_ExternalReference?.ReferenceNumber, 1, 35)
							}

						}
					}
				}
			}
			if(current_Body?.FreightCharge?.size()>0 || current_Body?.Remarks?.trim() || current_Body?.BLCertClause?.size()>0){
				'SummaryDetails' {
					'RateAndCharges' {
						current_Body?.FreightCharge?.each { current_FreightCharge ->
							'Charges'('Method': current_FreightCharge?.ChargeType?.trim()) {
								if (current_FreightCharge?.IsApprovedForCustomer?.trim()) {
									'ShowIndicator' current_FreightCharge?.IsApprovedForCustomer?.trim()?.toUpperCase() == "TRUE" ? "1" : "0"
								}
								if(current_FreightCharge?.ChargeCode?.trim()){
									'ChargeTypeCode' current_FreightCharge?.ChargeCode?.trim()
								}
								if (current_FreightCharge?.FreightRate?.trim()) {
									'FreightRate'('Qualifier': current_FreightCharge?.CalculateMethod?.trim(), current_FreightCharge?.FreightRate?.trim())
								}
								if (current_FreightCharge?.ChargeType?.trim() == blUtil?.PREPAID && current_FreightCharge?.TotalAmtInPmtCurrency?.toString()?.trim()) {
									'PrepaidAmount'('Currency': current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency, 'ExchangeRate': current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate, current_FreightCharge?.TotalAmtInPmtCurrency?.toString()?.trim())
								}
								else {
									'PrepaidAmount'('ExchangeRate':'0', '0')
								}
								if (current_FreightCharge?.ChargeType?.trim() == blUtil?.COLLECT && current_FreightCharge?.TotalAmtInPmtCurrency?.toString()?.trim()) {
									'CollectAmount'('Currency': current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency?.trim(), 'ExchangeRate': current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate, current_FreightCharge?.TotalAmtInPmtCurrency?.toString()?.trim())
								}
								else{
									'CollectAmount'('ExchangeRate': '0', '0')
								}
								'PayableAt' current_FreightCharge?.PayableElseWhere?.trim()?.toUpperCase() == 'TRUE' ? '1' : '0'
								'PayableBy' {
									if (current_FreightCharge?.PayByInformation?.CarrierCustomerCode?.trim()) {
										'CarrierCustomerCode' current_FreightCharge?.PayByInformation?.CarrierCustomerCode?.trim()
									}
									if (current_FreightCharge?.PayByInformation?.PayerName?.trim()) {
										'ContactPerson' {
											'FirstName' current_FreightCharge?.PayByInformation?.PayerName?.trim()
										}
									}
									'CityName' current_FreightCharge?.PayByInformation?.CityDetails?.City?.trim()
									if (current_FreightCharge?.PayByInformation?.CityDetails?.County?.trim()) {
										'County' current_FreightCharge?.PayByInformation?.CityDetails?.County?.trim()
									}
									if (current_FreightCharge?.PayByInformation?.CityDetails?.State?.trim()) {
										'StateProvince' current_FreightCharge?.PayByInformation?.CityDetails?.State?.trim()
									}
									if (current_FreightCharge?.PayByInformation?.CityDetails?.Country?.trim()) {
										'CountryCode' util.getCS2MasterCity4CountryCodeByCountryName(current_FreightCharge?.PayByInformation?.CityDetails?.Country, conn)
									}
									if(current_FreightCharge?.PayByInformation?.CityDetails?.Country?.trim()){
										'CountryName' current_FreightCharge?.PayByInformation?.CityDetails?.Country?.trim()
									}
								}
							}
						}
					}
					current_Body?.BLCertClause?.each { current_BLCertClause ->
						'Certification' {
							'Code' current_BLCertClause?.CertificationClauseType
							'ClauseText' current_BLCertClause?.CertificationClauseText
						}

					}
					if (current_Body?.Remarks) {
						'Remarks' {
							'RemarksLines' current_Body?.Remarks
						}
					}
					if(current_Body?.CarrierRate[0]?.CarrierRateNumber?.trim()){
						'RateReferences'{
							'References'{
								if(current_Body?.CarrierRate[0]?.CarrierRateNumber?.trim()){
									'ServiceOffering' util.substring(current_Body?.CarrierRate[0]?.CarrierRateNumber?.trim(),1,35)
								}
								if(util.substring(current_Body?.CarrierRate[0]?.CarrierRateNumber?.trim(),1,8)){
									'ServiceContractNumber' util.substring(current_Body?.CarrierRate[0]?.CarrierRateNumber?.trim(),1,8)
								}
							}
						}
					}
				}
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
		outXml.omitEmptyAttributes = true
		outXml.omitNullAttributes = true

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
		def blXml = outXml.createNode('BL')
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
//			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)

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

			blUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%18s', current_Body?.TransactionInformation?.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			blUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, bl.Header?.MsgDT?.GMT, bl.Header.InterchangeMessageID, TP_ID, conn)

			txnErrorKeys.add(errorKeyList)
		}

		//End root node
		outXml.nodeCompleted(null,blXml)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		blUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		blUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		blUtil.promoteHeaderIntChgMsgId(appSessionId, bl?.Body[0]);
		blUtil.promoteScacCode(appSessionId, bl?.Body[0]);

		String result = '';

		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			result = util.cleanXml(writer?.toString(), true)
			//promote XML output file name to transportation, xml format need this function
			//please change the file name pattern base on B2B_EDI_FILENAME setting
//			long fileSeqNumber = util.getFileSequenceNumber(TP_ID, 'BC', 'BCXML', 'BKGSUPUIFO', conn)
//			String fileSeqStr = '01'
//			if (fileSeqNumber>=0) {
//				fileSeqStr = fileSeqNumber + ""
//				if (fileSeqStr.length()>2) {
//					fileSeqStr = fileSeqStr.substring(fileSeqStr.length()-2)
//				} else if (fileSeqStr.length()==1) {
//					fileSeqStr = "0" + fileSeqStr
//				}
//			}
			//output file name
			String outputFileName = "LEMANUSA_BL_" + currentSystemDt.format(yyyyMMddHHmmss) + ".xml"
			blUtil.promoteOutputFileNameToSession(appSessionId, outputFileName)

			//posp handle
			result = result.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\"?>")
		}

		writer.close();

		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){

		List<Map<String,String>> errorList = []

		blUtil.checkBLStatus(current_Body, ['Preparation In Progress'], false, null, errorList)
		blUtil.checkCarrierBookingNumber(current_Body, true, null, errorList)
		blUtil.checkCertificationClauseTextLength(current_Body, 280, true, null, errorList)
		blUtil.checkContainerTrafficModeInboundLength(current_Body, 3, true, null, errorList)
		blUtil.checkContainerTrafficModeOutboundLength(current_Body, 3, true, null, errorList)
		blUtil.checkDangerousCargoIMDGPageLength(current_Body, 5, true, null, errorList)
		blUtil.checkDangerousCargoIMOClassLength(current_Body, 5, true, null, errorList)
		blUtil.checkOceanLegDischargeVesselNameLength(current_Body, 30, true, null, errorList)
		blUtil.checkOceanLegLoadingVesselNameLength(current_Body, 30, true, null, errorList)
		blUtil.checkReeferCargoSensitiveCargoDescLength(current_Body, 20, true, null, errorList)
		blUtil.checkStopOffPickupDetailsFacilityNameLength(current_Body, 35, true, null, errorList)
		blUtil.checkStopOffReturnDetailsFacilityNameLength(current_Body, 35, true, null, errorList)

		return errorList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){

		cs.b2b.core.common.xmlvalidation.ValidateXML validator = new ValidateXML()

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</BL>")
		util.cleanNode(node, true)

		String validationResult = validator.xmlValidation('CUS-BLXML', XmlUtil.serialize(node))
		if (validationResult.contains('Validation Failure.')){
			errorKeyList.add([TYPE: 'ES', IS_ERROR: 'YES', VALUE: validationResult])
		}
	}
}


