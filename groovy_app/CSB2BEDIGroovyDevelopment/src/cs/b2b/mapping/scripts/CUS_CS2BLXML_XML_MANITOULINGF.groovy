package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
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
public class CUS_CS2BLXML_XML_MANITOULINGF {

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
					'BatchNumber' util.getXMLControlNumber(TP_ID,'BL','XML','BLUPDATEO',conn)
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
				if(filteredFreightCharge?.findAll{it.ChargeType == blUtil.COLLECT}?.size() == filteredFreightCharge?.size()){
					'PaymentOption' 'Collect'
				}else if(filteredFreightCharge?.findAll{it.ChargeType == blUtil.PREPAID}?.size() == filteredFreightCharge?.size()){
					'PaymentOption' 'Prepaid'
				}else{
					'PaymentOption' 'Mixed'
				}
				'BLNumber' current_Body?.GeneralInformation?.BLNumber
			}
			'BLDetails' {
				'BLInformation'('BillType': current_Body?.GeneralInformation?.BLType) {
					'BLNumber' current_Body?.GeneralInformation?.BLNumber
					'BLStatus' current_Body?.BLStatus[0]?.Status
					'BLIssuanceDateTime'('null':'true','TimeZone': current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT, 'yyyyMMddHHmmss'))
					if(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT){
						'BLReceiptDateTime' util.convertXmlDateTime(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT, 'yyyyMMddHHmmss')
					}
					'BLCreationDateTime' ('null':'true',util.convertXmlDateTime(current_Body?.GeneralInformation?.BLCreationDT?.LocDT, 'yyyyMMddHHmmss'))
					if(current_Body?.GeneralInformation?.BLChangeDT?.LocDT){
						'BLUpdateDateTime' util.convertXmlDateTime(current_Body?.GeneralInformation?.BLChangeDT?.LocDT, 'yyyyMMddHHmmss')
					}
					if(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT){
						'BLOnBoardDateTime' util.convertXmlDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT, 'yyyyMMddHHmmss')
					}
					if(current_Body?.GeneralInformation?.CaptureOffice){
						'BLCapturingOffice'('PhoneNumber': current_Body?.GeneralInformation?.CaptureOfficePhoneNumber, current_Body?.GeneralInformation?.CaptureOffice)
					}
					'Payment' {
						if(current_Body?.GeneralInformation?.BLPaymentOffice) {
							'BLPaymentOffice'('PhoneNumber': current_Body?.GeneralInformation?.ContactOfficeCode, current_Body?.GeneralInformation?.BLPaymentOffice)
						}
						if(current_Body?.GeneralInformation?.PaymentReceiptDT?.LocDT){
							'PaymentReceiptDateTime' util.convertXmlDateTime(current_Body?.GeneralInformation?.PaymentReceiptDT?.LocDT, 'yyyyMMddHHmmss')
						}
//						'CollectAmount' ''
//						'PrepaidAmount' ''
					}
					'CargoType' cargoTypeMap.get(current_Body?.GeneralInformation?.ShipmentCargoType) ? cargoTypeMap.get(current_Body?.GeneralInformation?.ShipmentCargoType) : 'General'
					if(current_Body?.GeneralInformation?.CargoControlOffice){
						'CargoControlNumber' current_Body?.GeneralInformation?.CargoControlOffice
					}
					'TrafficMode' {
						'OutBound' current_Body?.GeneralInformation?.TrafficMode?.OutBound
						'InBound' current_Body?.GeneralInformation?.TrafficMode?.InBound
					}
					if (current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit && current_Body?.GeneralInformation?.BLGrossWeight?.Weight) {
						'Weight'('Qualifier': 'Gross', 'Units': current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit, blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.toBigDecimal()?.toString()))
					}else if(current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit && current_Body?.GeneralInformation?.BLNetWeight?.Weight){
						'Weight'('Qualifier': 'Net', 'Units': current_Body?.GeneralInformation?.BLNetWeight?.WeightUnit, blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLNetWeight?.Weight?.toBigDecimal()?.toString()))
					}
					if (current_Body?.GeneralInformation?.BLVolume?.VolumeUnit && current_Body?.GeneralInformation?.BLVolume?.Volume?.toBigDecimal() > 0) {
						'Volume'('Units': current_Body?.GeneralInformation?.BLVolume?.VolumeUnit, blUtil.replaceZeroAfterPoint(current_Body?.GeneralInformation?.BLVolume?.Volume))
					}
				}
				'PartyInformation' {
					Map<String, String> partyTypeMap = ['ANP': 'AP', 'BRK': 'BR', 'CGN': 'CN', 'FWD': 'FW', 'NPT': 'NP', 'SHP': 'SH']
					current_Body?.Party?.each { current_Party ->
						if (partyTypeMap.get(current_Party?.PartyType)) {
							'Party' {
								'PartyType' partyTypeMap.get(current_Party?.PartyType)
								'PartyName' util.substring(current_Party?.PartyName, 1, 70)
								'CarrierCustomerCode' ('null':'true', util.substring(current_Party?.CarrierCustomerCode, 1, 14))
								'PartyLocation' {
									if (current_Party?.Address?.AddressLines?.AddressLine?.size() > 0) {
										'Address' {
											current_Party?.Address?.AddressLines?.AddressLine?.each { current_AddressLine ->
												'AddressLines' util.substring(current_AddressLine, 1, 240)
											}
										}
									}
//									'Street' ''
									if(current_Party?.Address?.City){
										'City' current_Party?.Address?.City
									}
									if(current_Party?.Address?.County){
										'County' current_Party?.Address?.County
									}
//									'StateProvinceCode' ''
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
										'LocationCode' {
											'MutuallyDefinedCode' ''
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
										if(current_Party?.Contact?.ContactPhone) {
											'Phone' {
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
										}
//										'Fax' {
//											'CountryCode' util.substring(current_Party?.Contact?.ContactFax?.CountryCode, 1, 3)
//											'AreaCode' current_Party?.Contact?.ContactFax?.AreaCode
//											'Number' current_Party?.Contact?.ContactFax?.Number
//										}
										if(current_Party?.Contact?.ContactEmailAddress) {
											'Email' current_Party?.Contact?.ContactEmailAddress
										}
//										'Type' ''
									}
								}
							}
						}
					}
				}
				'ShipmentDetails' {
					'BookingInformation' {
						current_Body?.GeneralInformation?.CarrierBookingNumber?.each { current_BookingNumber ->
							'BookingNumber' util.substring(current_BookingNumber, 1, 35)
						}
					}
					'RouteInformation' {
						'EventDate' ('null':'true') {
//							'FullPickup' ''
							if(current_Body?.Route?.FullReturnCutoffDT?.LocDT){
								'FullReturnCutoff' ('TimeZone': current_Body?.Route?.FullReturnCutoffDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, 'yyyyMMddHHmmss'))
							}
							LocDT arrivalAtFinalHubLocDT = current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == "A"}?.LocDT
							if(arrivalAtFinalHubLocDT){
								'ArrivalAtFinalHub' ('TimeZone': arrivalAtFinalHubLocDT?.attr_TimeZone, util.convertXmlDateTime(arrivalAtFinalHubLocDT, 'yyyyMMddHHmmss'))
							}
//							'Delivery' ''
						}
						'Haulage' {
							'Outbound' current_Body?.Route?.Haulage?.OutBound
							'Inbound' current_Body?.Route?.Haulage?.InBound
						}

						POR por = current_Body?.Route?.POR
						FND fnd = current_Body?.Route?.FND

						if (por) {
							'LocationDetails' {
								'FunctionCode' 'POR'
								'LocationName' util.substring(por?.Facility?.FacilityName, 1, 60)
								'LocationDetails' ('null':'true') {
//									'Address' {
//										'AddressLines' ''
//									}
//									'Street' ''
									if(por?.CityDetails?.City){
										'City' por?.CityDetails?.City
									}
									if(por?.CityDetails?.County){
										'County' por?.CityDetails?.County
									}
//									'StateProvinceCode' ''
									if(por?.CityDetails?.State){
										'StateProvince' por?.CityDetails?.State
									}
									if(por?.CSStandardCity?.CSCountryCode){
										'CountryCode' util.substring(por?.CSStandardCity?.CSCountryCode, 1, 3)
									}
									if(por?.CityDetails?.Country){
										'CountryName' por?.CityDetails?.Country
									}
									if(por?.CityDetails?.LocationCode?.UNLocationCode && por?.CityDetails?.LocationCode?.SchedKDCode){
										'LocationCode' {
//											'MutuallyDefinedCode' ''
											'UNLocationCode' por?.CityDetails?.LocationCode?.UNLocationCode
											'SchedKDCode'('Type': (por?.CityDetails?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), por?.CityDetails?.LocationCode?.SchedKDCode)
										}
									}
//									'PostalCode' ''
								}
//								'EventDate' {
//									'Arrival' ''
//									'Departure' ''
//								}
							}
						}

						if (fnd) {
							'LocationDetails' {
								'FunctionCode' 'FND'
								'LocationName' util.substring(fnd?.Facility?.FacilityName, 1, 60)
								'LocationDetails' ('null':'true'){
//									'Address' {
//										'AddressLines' ''
//									}
//									'Street' ''
									if(fnd?.CityDetails?.City){
										'City' fnd?.CityDetails?.City
									}
									if(fnd?.CityDetails?.County){
										'County' fnd?.CityDetails?.County
									}
//									'StateProvinceCode' ''
									if(fnd?.CityDetails?.State){
										'StateProvince' fnd?.CityDetails?.State
									}
									if(fnd?.CSStandardCity?.CSCountryCode){
										'CountryCode' util.substring(fnd?.CSStandardCity?.CSCountryCode, 1, 3)
									}
									if(fnd?.CityDetails?.Country){
										'CountryName' fnd?.CityDetails?.Country
									}
									if(fnd?.CityDetails?.LocationCode?.UNLocationCode && fnd?.CityDetails?.LocationCode?.SchedKDCode){
										'LocationCode' {
//											'MutuallyDefinedCode' ''
											'UNLocationCode' fnd?.CityDetails?.LocationCode?.UNLocationCode
											'SchedKDCode'('Type': (fnd?.CityDetails?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), fnd?.CityDetails?.LocationCode?.SchedKDCode)
										}
									}
//									'PostalCode' ''
								}
//								'EventDate' {
//									'Arrival' ''
//									'Departure' ''
//								}
							}
						}
						current_Body?.Route?.OceanLeg?.eachWithIndex{ current_OceanLeg, current_OceanLeg_index ->

							POL pol = current_OceanLeg?.POL
							POD pod = current_OceanLeg?.POD

							if (pol) {
								'PortDetails' {
									'FunctionCode' 'POL'
									'LocationName' pol?.Port?.PortName
									'SequenceNumber' current_OceanLeg?.LegSeq
									'LocationDetails' {
//										'Address' {
//											'AddressLines' ''
//										}
//										'Street' ''
										if(pol?.Port?.City){
											'City' pol?.Port?.City
										}
										if(pol?.Port?.County){
											'County' pol?.Port?.County
										}
//										'StateProvinceCode' ''
										if(pol?.Port?.State){
											'StateProvince' pol?.Port?.State
										}
										if(pol?.Port?.CSCountryCode){
											'CountryCode' util.substring(pol?.Port?.CSCountryCode, 1, 3)
										}
										if(pol?.Port?.Country){
											'CountryName' pol?.Port?.Country
										}
										if(pol?.Port?.LocationCode?.UNLocationCode && pol?.Port?.LocationCode?.SchedKDCode) {
											'LocationCode' {
//											'MutuallyDefinedCode' ''
												'UNLocationCode' pol?.Port?.LocationCode?.UNLocationCode
												if (pol?.Port?.LocationCode?.SchedKDCode) {
													'SchedKDCode'('Type': (pol?.Port?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), pol?.Port?.LocationCode?.SchedKDCode)
												}
											}
										}
//										'PostalCode' ''
									}
									if (current_OceanLeg?.DepartureDT?.size() > 0) {
										'EventDate' {
											LocDT actDepartureDTLocDT = current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
											LocDT estDepartureDTLocDT = current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT
											if(actDepartureDTLocDT){
												'Departure'('TimeZone': actDepartureDTLocDT?.attr_TimeZone ? actDepartureDTLocDT?.attr_TimeZone : estDepartureDTLocDT?.attr_TimeZone, 'EstActIndicator': '0', util.convertXmlDateTime(actDepartureDTLocDT?.LocDT, "yyyyMMddHHmmss"))
											}else if(estDepartureDTLocDT){
												'Departure'('TimeZone': estDepartureDTLocDT?.attr_TimeZone, 'EstActIndicator': '1', util.convertXmlDateTime(estDepartureDTLocDT?.LocDT, "yyyyMMddHHmmss"))
											}
										}
									}
//									if (current_OceanLeg?.SVVD?.Loading) {
										'VesselVoyageInformation' {
											'VoyageEvent' 'Loading'
											'ServiceName'('null':'true', 'Code': util.substring(current_OceanLeg?.SVVD?.Loading?.Service, 1, 4))
											if(current_OceanLeg?.SVVD?.Loading?.Voyage){
												'VoyageNumberDirection' util.substring((current_OceanLeg?.SVVD?.Loading?.Voyage + (current_OceanLeg?.LoadingDirectionName ? current_OceanLeg?.LoadingDirectionName : '')), 1, 22)
											}
											'VesselInformation' {
												'VesselCode' ('null':'true',  'LloydsCode':(current_OceanLeg?.SVVD?.Loading?.LloydsNumber)?:' ', 'CallSign':current_OceanLeg?.SVVD?.Loading?.CallSign?:' ', current_OceanLeg?.SVVD?.Loading?.Vessel)
												'VesselName' util.substring(current_OceanLeg?.SVVD?.Loading?.VesselName, 1, 30)
											}
										}
//									}
								}
							}

							if (pod) {
								'PortDetails' {
									'FunctionCode' 'POD'
									'LocationName' pod?.Port?.PortName
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
											'LocationCode' {
//											'MutuallyDefinedCode' ''
												'UNLocationCode' pod?.Port?.LocationCode?.UNLocationCode
												if (pod?.Port?.LocationCode?.SchedKDCode) {
													'SchedKDCode'('Type': (pod?.Port?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), pod?.Port?.LocationCode?.SchedKDCode)
												}
											}
										}
//										'PostalCode' ''
									}
									if (current_OceanLeg?.ArrivalDT?.size() > 0) {
										'EventDate' {
											LocDT actArrivalDTLocDT = current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == "A"}?.LocDT
											LocDT estArrivalDTLocDT = current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == "E"}?.LocDT
											if(actArrivalDTLocDT){
												'Arrival'( 'EstActIndicator': '0' , util.convertXmlDateTime(actArrivalDTLocDT, "yyyyMMddHHmmss"),'TimeZone': actArrivalDTLocDT?.attr_TimeZone ? actArrivalDTLocDT?.attr_TimeZone : estArrivalDTLocDT?.attr_TimeZone)
											}else if(estArrivalDTLocDT){
												'Arrival'('TimeZone': estArrivalDTLocDT?.attr_TimeZone, 'EstActIndicator': '1' , util.convertXmlDateTime(estArrivalDTLocDT, "yyyyMMddHHmmss"))
											}
										}

									}
//									if (current_OceanLeg?.SVVD?.Discharge) {
										'VesselVoyageInformation' {
											'VoyageEvent' 'Discharging'
											'ServiceName'('null':'true', 'Code': util.substring(current_OceanLeg?.SVVD?.Discharge?.Service, 1, 4))
											if(current_OceanLeg?.SVVD?.Discharge?.Voyage){
												'VoyageNumberDirection' util.substring((current_OceanLeg?.SVVD?.Discharge?.Voyage + (current_OceanLeg?.DischargeDirectionName ? current_OceanLeg?.DischargeDirectionName : '')), 1, 22)
											}
											'VesselInformation' {
												'VesselCode' ('null':'true', 'LloydsCode':(current_OceanLeg?.SVVD?.Discharge?.LloydsNumber)?:' ', 'CallSign':current_OceanLeg?.SVVD?.Discharge?.CallSign?:' ', current_OceanLeg?.SVVD?.Discharge?.Vessel)
												'VesselName' util.substring(current_OceanLeg?.SVVD?.Discharge?.VesselName, 1, 30)
											}
										}
//									}
								}
							}
						}
						current_Body?.Route?.StopOff?.eachWithIndex {current_StopOff, current_StopOff_index ->
							'StopOff' {
							'SequenceNumber' current_StopOff_index + 1
							'PickupDetails' {
								'FacilityCode' current_StopOff?.PickupDetails?.Facility?.FacilityCode
								'FacilityName' current_StopOff?.PickupDetails?.Facility?.FacilityName
								'LocationName' ''
								'LocatoionDetails' {
									'Address' {
										'AddressLines' ''
									}
									'Street' ''
									'City' current_StopOff?.PickupDetails?.City?.City
									'County' current_StopOff?.PickupDetails?.City?.County
									'StateProvinceCode' ''
									'StateProvince' current_StopOff?.PickupDetails?.City?.State
									String stopOffPickupCountryCode = "ZZ"
									if(current_StopOff?.PickupDetails?.City?.Country){
										String countryCode = util.getCS2MasterCity4CountryCodeByCountryName(current_StopOff?.PickupDetails?.City?.Country, conn)
										stopOffPickupCountryCode = countryCode ? countryCode : stopOffPickupCountryCode
									}
									'CountryCode' stopOffPickupCountryCode
									'CountryName' current_StopOff?.PickupDetails?.City?.Country
									'LocationCode' {
										'MutuallyDefinedCode' ''
										'UNLocationCode' current_StopOff?.PickupDetails?.City?.LocationCode?.UNLocationCode
										'SchedKDCode' ('null':'true','Type':current_StopOff?.PickupDetails?.City?.LocationCode?.SchedKDType, current_StopOff?.PickupDetails?.City?.LocationCode?.SchedKDCode)
									}
									'PostalCode' ''
								}
							}
							'ReturnDetails' {
								'FacilityCode' current_StopOff?.ReturnDetails?.Facility?.FacilityCode
								'FacilityName' current_StopOff?.ReturnDetails?.Facility?.FacilityName
								'LocationName' ''
								'LocationDetails' {
									'Address' {
										'AddressLines' ''
									}
									'Street' ''
									'City' ('null':'true',current_StopOff?.ReturnDetails?.City?.City)
									'County' ('null':'true', current_StopOff?.ReturnDetails?.City?.County)
									'StateProvinceCode' ''
									'StateProvince' current_StopOff?.ReturnDetails?.City?.State
									String stopOffReturnCountryCode = "ZZ"
									if(current_StopOff?.PickupDetails?.City?.Country){
										String countryCode = util.getCS2MasterCity4CountryCodeByCountryName(current_StopOff?.ReturnDetails?.City?.Country, conn)
										stopOffReturnCountryCode = countryCode ? countryCode : stopOffReturnCountryCode
									}
									'CountryCode' stopOffReturnCountryCode
									'CountryName' current_StopOff?.ReturnDetails?.City?.Country
									'LocationCode' {
										'MutuallyDefinedCode' ''
										'UNLocationCode' current_StopOff?.ReturnDetails?.City?.LocationCode?.UNLocationCode
										'SchedKDCode' ('Type':current_StopOff?.ReturnDetails?.City?.LocationCode?.SchedKDType, current_StopOff?.ReturnDetails?.City?.LocationCode?.SchedKDCode)
									}
									'PostalCode' ''
								}
							}
						}
						}
					}
					'EquipmentInformation' {
						current_Body?.Container?.each { current_Container ->
							'Containers' {
								if(current_Container?.IsSOC) {
									'IsSOC' current_Container?.IsSOC?.toUpperCase() == 'TRUE' ? '1' : '0'
								}
								Map<String, String> containerTypeMap = util.getCdeConversionFromIntCde(TP_ID, 'BL', 'O', current_Body.GeneralInformation.SCACCode, 'XML', 'ContainerType', current_Container?.CS1ContainerSizeType, conn)
								if (containerTypeMap.get("EXT_CDE")) {
									'ContainerType' containerTypeMap.get("EXT_CDE")
								} else {
									'ContainerType' current_Container?.CS1ContainerSizeType
								}
								'ContainerNumber'('CheckDigit': current_Container?.ContainerCheckDigit?:' ', util.substring(current_Container?.ContainerNumber, 1, 10))
								'SealNumber'('null':'true', 'type':current_Container?.Seal?.SealType,current_Container?.Seal?.SealNumber)
								if (current_Container?.GrossWeight?.WeightUnit && current_Container?.GrossWeight?.Weight) {
									'Weight'('Qualifier': 'Gross', 'Units': current_Container?.GrossWeight?.WeightUnit, blUtil.replaceZeroAfterPoint(current_Container?.GrossWeight?.Weight?.toBigDecimal()?.toString()))
								}
//								'Volume' ''
								if (current_Container?.PieceCount?.PieceCountUnit && current_Container?.PieceCount?.PieceCount) {
									'PieceCount'('Units': current_Container?.PieceCount?.PieceCountUnit?.trim(), blUtil.replaceZeroAfterPoint(current_Container?.PieceCount?.PieceCount))
								}
								if (current_Container?.TrafficMode) {
									'TrafficMode' {
										'OutBound' current_Container?.TrafficMode?.OutBound
										'InBound' current_Container?.TrafficMode?.InBound
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
							}
						}
					}
					'CargoInformation' {
						current_Body?.Cargo?.each { current_Cargo ->
							'CargoGroup' {
								'CargoNature' cargoTypeMap.get(current_Cargo?.CargoNature) ? cargoTypeMap.get(current_Cargo?.CargoNature) : 'General'
//								'CommodityCode' ''
								if (current_Cargo?.GrossWeight?.WeightUnit && current_Cargo?.GrossWeight?.Weight) {
									'Weight'('Qualifier': 'Gross', 'Units': current_Cargo?.GrossWeight?.WeightUnit, blUtil.replaceZeroAfterPoint(current_Cargo?.GrossWeight?.Weight?.toBigDecimal()?.toString()))
								}else if (current_Cargo?.NetWeight?.WeightUnit && current_Cargo?.NetWeight?.Weight) {
									'Weight'('Qualifier': 'Net', 'Units': current_Cargo?.NetWeight?.WeightUnit, blUtil.replaceZeroAfterPoint(current_Cargo?.NetWeight?.Weight?.toBigDecimal()?.toString()))
								}
								if (current_Cargo?.Volume?.VolumeUnit && current_Cargo?.Volume?.Volume) {
									'Volume'('Units': current_Cargo?.Volume?.VolumeUnit, blUtil.replaceZeroAfterPoint(current_Cargo?.Volume?.Volume))
								}
								String packageQty = ''
								if (current_Cargo?.CargoNature == 'GC') {
									if (current_Cargo?.GCPackageUnit?.find {it.PackageSeqNumber == '1'}?.PackageUnitQuantity) {
										packageQty = current_Cargo?.GCPackageUnit?.find {it.PackageSeqNumber == '1'}?.PackageUnitQuantity
									} else if (current_Cargo?.Packaging?.PackageQty) {
										packageQty = current_Cargo?.Packaging?.PackageQty
									}
								} else if (current_Cargo?.CargoNature == 'RF') {
									packageQty = current_Cargo?.ReeferCargoSpec?.find {it.RFPackageUnit.find { it.PackageSeqNumber == '1' }}?.RFPackageUnit?.get(0)?.PackageUnitQuantity
								} else if (current_Cargo?.CargoNature == 'DG') {
									packageQty = current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity
								} else if (current_Cargo?.CargoNature == 'AW') {
									packageQty = current_Cargo?.AWCargoSpec?.find {it.AWPackageUnit.find { it.PackageSeqNumber == '1' }}?.AWPackageUnit?.get(0)?.PackageUnitQuantity
								} else if (current_Cargo?.CargoNature == 'AD') {
									if (current_Cargo?.AWCargoSpec?.find {it.AWPackageUnit.find { it.PackageSeqNumber == '1' }}?.AWPackageUnit?.get(0)?.PackageUnitQuantity) {
										packageQty = current_Cargo?.AWCargoSpec?.find {it.AWPackageUnit.find { it.PackageSeqNumber == '1' }}?.AWPackageUnit?.get(0)?.PackageUnitQuantity
									} else if (current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity) {
										packageQty = current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity
									}
								} else if (current_Cargo?.CargoNature == 'RD') {
									if (current_Cargo?.ReeferCargoSpec?.find {it.RFPackageUnit.find { it.PackageSeqNumber == '1' }}?.RFPackageUnit?.get(0)?.PackageUnitQuantity) {
										packageQty = current_Cargo?.ReeferCargoSpec?.find {it.RFPackageUnit.find { it.PackageSeqNumber == '1' }}?.RFPackageUnit?.get(0)?.PackageUnitQuantity
									} else if (current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity) {
										packageQty = current_Cargo?.DGCargoSpec?.find {it.DGPackageUnit.find { it.PackageSeqNumber == '1' }}?.DGPackageUnit?.get(0)?.PackageUnitQuantity
									}
								}
								if(StringUtil.isEmpty(packageQty)){
									packageQty = current_Cargo?.Packaging?.PackageQty
								}
								'Package'('Type': current_Cargo?.Packaging?.PackageType, packageQty)
								'ContainerNumber'('CheckDigit': current_Cargo?.CurrentContainerCheckDigit?:' ', current_Cargo?.CurrentContainerNumber)
								if (current_Cargo?.CargoDescription) {
									'CargoDescription' {
//										for (int i = 0; i * 60 < current_Cargo?.CargoDescription?.length(); i++) {
										'DescriptionLine' util?.substring(current_Cargo?.CargoDescription, 1, 60)?.trim()
//										}
									}
								}
								if (current_Cargo?.MarksAndNumbers?.size() > 0) {
									'MarksAndNumbers' {
										current_Cargo?.MarksAndNumbers?.each { current_MarksAndNumbers ->
											'MarksAndNumbersLine' util.substring(current_MarksAndNumbers.MarksAndNumbersLine, 1, 240)
										}
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
								if (current_Cargo?.DGCargoSpec && current_Cargo?.DGCargoSpec?.size() > 0) {
									'DangerousCargo' {
										current_Cargo?.DGCargoSpec?.each { current_DGCargo ->
											'DangerousCargoInfo' {
												'HazardousMaterial' {
													'DGRegulator' current_DGCargo?.DGRegulator
													'IMCOClass' util.substring(current_DGCargo?.IMOClass, 1, 5)
													'IMDGPage' util.substring(current_DGCargo?.IMDGPage, 1, 5)
													'UNNumber' current_DGCargo?.UNNumber
													'TechnicalShippingName' ('null':'true', current_DGCargo?.TechnicalName)
													'ProperShippingName' ('null':'true', current_DGCargo?.ProperShippingName)
													'EMSNumber' ('null':'true', current_DGCargo?.EMSNumber)
													'PSAClass' ('null':'true', current_DGCargo?.PSAClass)
													'MFAGPageNumber' ('null':'true', current_DGCargo?.MFAGNumber)
													if (current_DGCargo?.FlashPoint?.TemperatureUnit && current_DGCargo?.FlashPoint?.Temperature) {
														'FlashPoint'('Units': current_DGCargo?.FlashPoint?.TemperatureUnit, blUtil.replaceZeroAfterPoint(current_DGCargo?.FlashPoint?.Temperature?.toBigDecimal()?.toString()))
													} else {
														'FlashPoint'('Units': 'C', '999.99')
													}
													if (StringUtil.isNotEmpty(current_DGCargo?.ElevatedTemperature?.TemperatureUnit) && StringUtil.isNotEmpty(current_DGCargo?.ElevatedTemperature?.Temperature)) {
														'DGElevationTemperature'('Units': current_DGCargo?.ElevatedTemperature?.TemperatureUnit, blUtil.replaceZeroAfterPoint(current_DGCargo?.ElevatedTemperature?.Temperature?.toBigDecimal()?.toString()))
													}
													'State' current_DGCargo?.State
//													'ApprovalCode' util.substring(current_DGCargo?.ApprovalCode, 1, 20) //removed from B2BSCR20170626004090d
													if(current_DGCargo?.Label?.size() > 0) {
														current_DGCargo?.Label?.eachWithIndex { current_Label, index ->
															if (index < 4) {
																'Label'('null': 'true', current_Label)
															}
														}
													}else{
														'Label'('null': 'true')
													}
													if (current_DGCargo?.GrossWeight?.Weight) {
														'Weight'('Qualifier': 'Gross', 'Units': current_DGCargo?.GrossWeight?.WeightUnit, blUtil.replaceZeroAfterPoint(current_DGCargo?.GrossWeight?.Weight?.toBigDecimal()?.toString()))
													}else if(current_DGCargo?.NetWeight?.Weight){
														'Weight'('Qualifier': 'Net', 'Units': current_DGCargo?.NetWeight?.WeightUnit, blUtil.replaceZeroAfterPoint(current_DGCargo?.NetWeight?.Weight?.toBigDecimal()?.toString()))
													}
													if (StringUtil.isNotEmpty(current_DGCargo?.NetExplosiveWeight?.WeightUnit) && StringUtil.isNotEmpty(current_DGCargo?.NetExplosiveWeight?.Weight)) {
														'NetExplosiveWeight'('Units': current_DGCargo?.NetExplosiveWeight?.WeightUnit, blUtil.replaceZeroAfterPoint(current_DGCargo?.NetExplosiveWeight?.Weight?.toBigDecimal()?.toString()))
													}
													'Indicators' {
														'isMarinePollutant' current_DGCargo?.isMarinePollutant ? (current_DGCargo?.isMarinePollutant?.toUpperCase() == 'TRUE' ? '0' : '1') : ''
														'isInhalationHazardous' current_DGCargo?.IsInhalationHazardous ? (current_DGCargo?.IsInhalationHazardous?.toUpperCase() == 'TRUE' ? '0' : '1') : ''
														'isLimitedQuantity' current_DGCargo?.isLimitedQuantity ? (current_DGCargo?.isLimitedQuantity?.toUpperCase() == 'TRUE' ? '0' : '1') : ''
														'isReportableQuantity' current_DGCargo?.IsReportableQuantity ? (current_DGCargo?.isMarinePollutant?.toUpperCase() == 'TRUE' ? '0' : '1') : ''
														'isEmptyUnclean' current_DGCargo?.IsEmptyUnclean ? (current_DGCargo?.IsEmptyUnclean?.toUpperCase() == 'TRUE' ? '0' : '1') : ''
													}
													Map<String, String> numberRomanMap = ['1':'I' , '2':'II', '3':'III']
													'Package' ('null':'true'){
														if(current_DGCargo?.PackageGroup?.Code) {
															'PackagingGroupCode' numberRomanMap.get(current_DGCargo?.PackageGroup?.Code) ? numberRomanMap.get(current_DGCargo?.PackageGroup?.Code) : current_DGCargo?.PackageGroup?.Code
														}
														if(current_DGCargo?.PackageGroup?.InnerPackageDescription?.PackageDesc){
															'InnerPackageDescription'('Type': current_DGCargo?.PackageGroup?.InnerPackageDescription?.PackageType, current_DGCargo?.PackageGroup?.InnerPackageDescription?.PackageDesc)
														}
														if(current_DGCargo?.PackageGroup?.OuterPackageDescription?.PackageDesc){
															'OuterPackageDescription'('Type': current_DGCargo?.PackageGroup?.OuterPackageDescription?.PackageType, current_DGCargo?.PackageGroup?.OuterPackageDescription?.PackageDesc)
														}
													}
													'Remarks' {
														if (current_DGCargo?.Remarks) {
															for (int i = 0; i * 100 < current_DGCargo?.Remarks?.length(); i++) {
																'RemarksLines' ('null':'true', util.substring(current_DGCargo?.Remarks, i * 100 + 1, 100))
															}
														}else{
															'RemarksLines' ('null':'true')
														}
													}
												}
												'EmergencyContactDetails' ('null':'true') {
													current_DGCargo?.EmergencyContact[0]?.each { current_EmergencyContact ->
														'FirstName' current_EmergencyContact?.FirstName
														'LastName' current_EmergencyContact?.LastName
														'Phone' {
//															'CountryCode' util.substring(current_EmergencyContact?.ContactPhone?.CountryCode, 1, 3)
//															'AreaCode' current_EmergencyContact?.ContactPhone?.AreaCode
															'Number' current_EmergencyContact?.ContactPhone?.Number
														}
//														'Fax' {
//															'CountryCode' util.substring(current_EmergencyContact?.ContactFax?.CountryCode, 1, 3)
//															'AreaCode' current_EmergencyContact?.ContactFax?.AreaCode
//															'Number' current_EmergencyContact?.ContactFax?.Number
//														}
//														'Email' current_EmergencyContact?.ContactEmailAddress
														'Type' current_EmergencyContact?.Type
													}
												}
											}
										}
									}
								}
								if(current_Cargo?.ReeferCargoSpec && current_Cargo?.ReeferCargoSpec?.size() > 0) {
									Map<String, String> atmosphereTypeMap = ['AFAM':'AFAM', 'CA':'CA', 'MA':'MA', 'MAG':'MG', 'NP':'NP']
									Map<String, String> romanNumeralMap = ['I':'1', 'II':'2', 'III':'3']
									'ReeferCargo' {
										current_Cargo?.ReeferCargoSpec?.each {current_ReeferCargoSpec ->

											'ReeferCargoInfo' {
												'ReeferSettings' ('AtmosphereType':atmosphereTypeMap.get(current_ReeferCargoSpec?.AtmosphereType),'GenSetType':current_ReeferCargoSpec?.GensetType,
														'CO2':romanNumeralMap.get(current_ReeferCargoSpec?.CO2) ? romanNumeralMap.get(current_ReeferCargoSpec?.CO2) : current_ReeferCargoSpec?.CO2?.toBigDecimal() > 0 && current_ReeferCargoSpec?.CO2?.toBigDecimal() < 100 ? blUtil.replaceZeroAfterPoint(current_ReeferCargoSpec?.CO2) : '',
														'O2': romanNumeralMap.get(current_ReeferCargoSpec?.O2) ? romanNumeralMap.get(current_ReeferCargoSpec?.O2) : current_ReeferCargoSpec?.O2?.toBigDecimal() > 0 && current_ReeferCargoSpec?.O2?.toBigDecimal() < 100 ? blUtil.replaceZeroAfterPoint(current_ReeferCargoSpec?.O2) : '',
														'Humidity':current_ReeferCargoSpec?.DehumidityPercentage,
														'PreCooling': current_ReeferCargoSpec?.DehumidityPercentage ? (current_ReeferCargoSpec?.IsPreCoolingReq?.toUpperCase() == "TRUE" ? '1' : current_ReeferCargoSpec?.IsPreCoolingReq?.toUpperCase() == "FALSE" ? '0' : '') : '' ,
														'CAIndicator':current_ReeferCargoSpec?.IsControlledAtmosphere?.toUpperCase() == "TRUE" ? '1' : current_ReeferCargoSpec?.IsControlledAtmosphere?.toUpperCase() == "FALSE" ? '0' : '',
														'ROIndicator':current_ReeferCargoSpec?.IsReeferOperational?.toUpperCase() == "TRUE" ? '1' : current_ReeferCargoSpec?.IsReeferOperational?.toUpperCase() == "FALSE" ? '0' : ''){
													'Temperature' ('Units':current_ReeferCargoSpec?.Temperature?.TemperatureUnit, blUtil.replaceZeroAfterPoint(current_ReeferCargoSpec?.Temperature?.Temperature?.toBigDecimal()?.toString()))
													if(current_ReeferCargoSpec?.Ventilation?.VentilationUnit && current_ReeferCargoSpec?.Ventilation?.Ventilation){
														'Ventilation' ('Units':current_ReeferCargoSpec?.Ventilation?.VentilationUnit, current_ReeferCargoSpec?.Ventilation?.Ventilation)
													}
													'SensitiveCargoDesc' current_ReeferCargoSpec?.SensitiveCargoDesc
//													'Remarks' {
//														'RemarksLines' ''
//													}
												}
												'EmergencyContactDetails' ('null':'true'){
													current_ReeferCargoSpec?.EmergencyContact[0]?.each { current_EmergencyContact ->
														'FirstName' current_EmergencyContact?.FirstName
														'LastName' current_EmergencyContact?.LastName
														'Phone' {
//															'CountryCode' util.substring(current_EmergencyContact?.ContactPhone?.CountryCode, 1, 3)
//															'AreaCode' current_EmergencyContact?.ContactPhone?.AreaCode
															'Number' current_EmergencyContact?.ContactPhone?.Number
														}
//														'Fax' {
//															'CountryCode' util.substring(current_EmergencyContact?.ContactFax?.CountryCode, 1, 3)
//															'AreaCode' current_EmergencyContact?.ContactFax?.AreaCode
//															'Number' current_EmergencyContact?.ContactFax?.Number
//														}
//														'Email' current_EmergencyContact?.ContactEmailAddress
														'Type' current_EmergencyContact?.Type
													}
												}
											}
										}
									}
								}
								if (current_Cargo?.AWCargoSpec && current_Cargo?.AWCargoSpec?.size() > 0) {
									'AwkwardCargo' {
										current_Cargo?.AWCargoSpec?.each { current_AWCargo ->
											'AwkwardCargoInfo' {
												'AwkwardCargoDetails' {
													'Height'('Units': current_AWCargo?.Height?.LengthUnit, blUtil.replaceZeroAfterPoint(current_AWCargo?.Height?.Length?.toBigDecimal()?.toString()))
													'Width'('Units': current_AWCargo?.Width?.LengthUnit, blUtil.replaceZeroAfterPoint(current_AWCargo?.Width?.Length?.toBigDecimal()?.toString()))
													'Length'('Units': current_AWCargo?.Length?.LengthUnit, blUtil.replaceZeroAfterPoint(current_AWCargo?.Length?.Length?.toBigDecimal()?.toString()))
//													if (current_AWCargo?.Remarks) {
//														'Remarks' {
//															for (int i = 0; i * 100 < current_AWCargo?.Remarks?.length(); i++) {
//																'RemarksLines' util.substring(current_AWCargo?.Remarks, i * 100 + 1, 100)
//															}
//														}
//													}
												}
												'EmergencyContactDetails' ('null':'true'){
													current_AWCargo?.EmergencyContact[0]?.each { current_EmergencyContact ->
														'FirstName' current_EmergencyContact?.FirstName
														'LastName' current_EmergencyContact?.LastName
														'Phone' {
//															'CountryCode' util.substring(current_EmergencyContact?.ContactPhone?.CountryCode, 1, 3)
//															'AreaCode' current_EmergencyContact?.ContactPhone?.AreaCode
															'Number' current_EmergencyContact?.ContactPhone?.Number
														}
//														'Fax' {
//															'CountryCode' util.substring(current_EmergencyContact?.ContactFax?.CountryCode, 1, 3)
//															'AreaCode' current_EmergencyContact?.ContactFax?.AreaCode
//															'Number' current_EmergencyContact?.ContactFax?.Number
//														}
//														'Email' current_EmergencyContact?.ContactEmailAddress
														'Type' current_EmergencyContact?.Type
													}
												}
											}
										}
									}
								}
							}
						}
					}
					Map<String, String> referenceTypeMap = ['FID':'FI', 'PO':'PO', 'QUOT':'Q1', 'SC':'E8', 'SIR':'SI', 'SR':'SR']
					Map<String, String> referenceNameMap = ['FID':'File Identifier', 'PO':'Purchase Order Number', 'QUOT':'Quotation Number',
															'SC':'Service Contract Number', 'SIR':'Shipping Instruction Reference', 'SR':'Shipper Reference']
//					'ExternalReferences' {
//						current_Body?.CarrierRate?.each {current_CarrierRate ->
//							if(referenceTypeMap.containsKey(current_CarrierRate?.CSCarrierRateType)){
//								'References' {
//									'ReferenceType' referenceTypeMap.get(current_CarrierRate?.CSCarrierRateType)
//									'ReferenceName' referenceNameMap.get(current_CarrierRate?.CSCarrierRateType)
//									'ReferenceNumber' util.substring(current_CarrierRate?.CarrierRateNumber, 1, 35)
//								}
//							}
//						}
//
//						current_Body?.ExternalReference?.each {current_ExternalReference ->
//							if(referenceTypeMap.containsKey(current_ExternalReference?.CSReferenceType)){
//								'References' {
//									'ReferenceType' referenceTypeMap.get(current_ExternalReference?.CSReferenceType)
//									'ReferenceName' referenceNameMap.get(current_ExternalReference?.CSReferenceType)
//									'ReferenceNumber' util.substring(current_ExternalReference?.ReferenceNumber, 1, 35)
//								}
//							}
//						}
//					}
				}
			}
			'SummaryDetails'{
				'RateAndCharges' ('null':'true'){
					filteredFreightCharge?.each {current_FreightCharge ->
						'Charges' ('Method':current_FreightCharge?.ChargeType){
							if(current_FreightCharge?.IsApprovedForCustomer) {
								'ShowIndicator' current_FreightCharge?.IsApprovedForCustomer?.toUpperCase() == "TRUE" ? "0" : "1"
							}
							'ChargeTypeCode' current_FreightCharge?.ChargeCode
							if(current_FreightCharge?.FreightRate) {
								'FreightRate'('Qualifier': current_FreightCharge?.CalculateMethod, current_FreightCharge?.FreightRate)
							}
							if(current_FreightCharge?.ChargeType == blUtil?.PREPAID && current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency){
								'PrepaidAmount' ('Currency':current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency, 'ExchangeRate':current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate, current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)
							}
							if(current_FreightCharge?.ChargeType == blUtil?.COLLECT && current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency) {
								'CollectAmount' ('Currency':current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency, 'ExchangeRate':current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate, current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)
							}
							if(current_FreightCharge?.PayableElseWhere) {
								'PayableAt' current_FreightCharge?.PayableElseWhere == 'true' ? '0' : '1'
							}
							'PayableBy' ('null':'true'){
								if(current_FreightCharge?.PayByInformation?.CarrierCustomerCode) {
									'CarrierCustomerCode' current_FreightCharge?.PayByInformation?.CarrierCustomerCode
								}
								if(current_FreightCharge?.PayByInformation?.PayerName) {
									'ContactPerson'{
										'FirstName' current_FreightCharge?.PayByInformation?.PayerName
									}
//									'LastName' ''
//									'Phone'{
//										'CountryCode' ''
//										'AreaCode' ''
//										'Number' ''
//									}
//									'Fax'{
//										'CountryCode' ''
//										'AreaCode' ''
//										'Number' ''
//									}
//									'Email' ''
//									'Type' ''
								}
//								'Address'{
//									'AddressLines' ''
//								}
								if(current_FreightCharge?.PayByInformation?.CityDetails?.City) {
									'CityName' current_FreightCharge?.PayByInformation?.CityDetails?.City
								}
								if(current_FreightCharge?.PayByInformation?.CityDetails?.County) {
									'County' current_FreightCharge?.PayByInformation?.CityDetails?.County
								}
								if(current_FreightCharge?.PayByInformation?.CityDetails?.State){
									'StateProvinceCode' util.getCS2MasterCityByStateName(current_FreightCharge?.PayByInformation?.CityDetails?.State,conn)?.get("STATE_CDE")
									'StateProvince' current_FreightCharge?.PayByInformation?.CityDetails?.State
								}
								if(current_FreightCharge?.PayByInformation?.CityDetails?.Country){
									'CountryCode' util.getCS2MasterCity4CountryCodeByCountryName(current_FreightCharge?.PayByInformation?.CityDetails?.Country, conn)
									'CountryName' current_FreightCharge?.PayByInformation?.CityDetails?.Country
								}
//								'PostalCode' ''
							}
						}
					}
				}
				current_Body?.BLCertClause?.each {current_BLCertClause ->
					if(current_BLCertClause?.CertificationClauseType && current_BLCertClause?.CertificationClauseText){
						'Certification'{
							'Code' current_BLCertClause?.CertificationClauseType
							'ClauseText' util.substring(current_BLCertClause?.CertificationClauseText, 1, 280)
						}
					}
				}
				if(current_Body?.Remarks?.size() > 0) {
					'Remarks' {
						for (int i = 0; i * 100 < current_Body?.Remarks?.length(); i++) {
							'RemarksLines' util.substring(current_Body?.Remarks, i * 100 + 1, 100)
						}
					}
				}
				'RateReferences'{
					current_Body?.CarrierRate?.each {current_CarrierRate ->
						'References'{
							'ServiceOffering' current_CarrierRate?.CSCarrierRateType == 'CSO' ? current_CarrierRate.CarrierRateNumber : ''
							'QuotationNumber' current_CarrierRate?.CSCarrierRateType == 'QUOT' ? current_CarrierRate.CarrierRateNumber : ''
							'ServiceContractNumber' current_CarrierRate?.CSCarrierRateType == 'SC' ? current_CarrierRate.CarrierRateNumber : ''
							'TariffItem' current_CarrierRate?.CSCarrierRateType == 'TARIF' ? current_CarrierRate.CarrierRateNumber : ''
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
			String outputFileName = "MANITOULINGF_BL_" + currentSystemDt.format(yyyyMMddHHmmss) + ".xml"
			blUtil.promoteOutputFileNameToSession(appSessionId, outputFileName)

			//posp handle
			result = result.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\"?>")
		}

		writer.close();

		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){

		List<Map<String,String>> errorList = []

//		blUtil.checkBLStatus(current_Body, ['Preparation In Progress'], false, null, errorList)
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


