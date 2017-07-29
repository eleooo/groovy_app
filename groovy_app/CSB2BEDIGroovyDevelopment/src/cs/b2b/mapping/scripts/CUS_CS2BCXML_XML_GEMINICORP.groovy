package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.text.DecimalFormat

import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.bc.AssociatedExternalReference
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.Header
import cs.b2b.core.mapping.bean.bc.Party
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * xml std. mapping from BCXML_StdNew, copy from CUS_CS2BCXML_XML_DUMMYBCXML.groovy
 *       -- difference:
 *       -- 1, References, one more loop from the 1st AssociatedExternalReference SID
 *       -- 2, Reference, ExternalReference?.each, add special condition expression
 *       -- 3, output file name
 * 
 * @author liangda init groovy migration
 *
 * @change B2BSCR20170626004090a - Remove Approval Code Mapping in B2B BC XML Std , http://i2isd/sites/csisa/Lists/Workplan/DispForm.aspx?ID=31360
 *
 */
public class CUS_CS2BCXML_XML_GEMINICORP {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil()
	cs.b2b.core.mapping.util.MappingUtil_BC_O_Common bcUtil = new cs.b2b.core.mapping.util.MappingUtil_BC_O_Common(util)

	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	Connection conn = null

	java.util.Date currentDate = new java.util.Date()
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = 'HHmm'
	def yyyyMMddHHmmss = 'yyyyMMddHHmmss'
	
	def currentSystemDt = null
	DecimalFormat decimalFormatter = new DecimalFormat("#.####")
	DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")

	Map bookingStatusMap = ['Confirmed':'COF','Pending':'PEN','Wait Listed':'PEN','Cancelled':'CAN','No Show':'NS','Declined':'DEC','Rejected':'DEC', 'Update':'UPD']
	
	Map<String,String> referenceTypeMap = ['CR':'CR', 'CTR':'CT', 'FR':'FN', 'INV':'IK', 'PO':'PO', 'SC':'CT', 'SID':'SI', 'TARIF':'TS']
	
	
	public void generateBody(Body current_Body, Header bcHeader, MarkupBuilder outXml) {
		outXml.'Confirmation' {
			'GeneralInfo' {
				'TransactionInfo' {
					if (bcHeader.ControlNumber) {
						'BatchNumber' bcHeader.ControlNumber
					} else {
						//get control number from db
						def xmlCtrlNum = util.getXMLControlNumber(TP_ID, 'BC', 'BCXML', 'BKGSUPUIFO', conn)
						'BatchNumber' xmlCtrlNum
					}
					'MessageSender' 'CARGOSMART'
					'MessageRecipient' bcHeader.ReceiverID
					'MessageID' 'BCXML'
					'DateCreated' 'TimeZone':'HKT', currentDate.format("yyyyMMddHHmmss")
					'Version' '7.4'
				}
				'ActionType' bookingStatusMap.get(current_Body.GeneralInformation?.BookingStatus)
				'BookingNumber' current_Body.GeneralInformation?.CarrierBookingNumber
				'BookingOffice' current_Body.GeneralInformation?.BookingOffice
				'SCAC' current_Body.GeneralInformation?.SCACCode
				if (current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.LocDT) {
					'StatusDate' 'TimeZone':current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.LocDT, 'yyyyMMddHHmmss')
				} else if (current_Body.GeneralInformation?.BookingStatusDT?.GMT) {
					'StatusDate' 'TimeZone':current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body.GeneralInformation?.BookingStatusDT?.GMT, 'yyyyMMddHHmmss')
				} else {
					'StatusDate' 'TimeZone':'HKT' , currentDate.format("yyyyMMddHHmmss")
				}
				'TrafficMode' {
					'OutBound' current_Body.GeneralInformation?.TrafficMode?.OutBound
					'InBound' current_Body.GeneralInformation?.TrafficMode?.InBound
				}
			}
			
			'ShipmentDetails' {
				//References
				'UserReferences' {
					//ExternalReference
					//if ZZ missing
					def refCSBkg = current_Body.ExternalReference.find{it.CSReferenceType == 'CSBKG'}
					if (refCSBkg==null && current_Body.GeneralInformation?.CSBookingRefNumber) {
						'References' {
							'ReferenceType' 'ZZ'
							'ReferenceNumber' current_Body.GeneralInformation?.CSBookingRefNumber
							'ReferenceDescription' 'CS Reference Number'
						}
					}
					
					//sepcial, add SID in loop
					AssociatedExternalReference assExtRefThe1st = null
					if (current_Body.AssociatedExternalReference?.size()>0) {
						assExtRefThe1st = current_Body.AssociatedExternalReference?.get(0)
					}
					
					if (assExtRefThe1st?.CSReferenceType == 'SID') {
						HashMap<String, String> convertType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null,  'XML', 'ReferenceType', 'SID', conn)
						'References' {
							'ReferenceType' convertType?.get('EXT_CDE')
							'ReferenceNumber' util.substring(assExtRefThe1st?.ReferenceNumber, 1, 35)
							'ReferenceDescription' util.substring(assExtRefThe1st?.ReferenceDescription, 1, 60)
						}
					}
					
					//CarrierRate
					current_Body.CarrierRate?.each { current_CarrierRate ->
						if (current_CarrierRate.CSCarrierRateType && current_CarrierRate.CarrierRateNumber) {
							HashMap<String, String> defType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null,  'XML', 'ReferenceType', current_CarrierRate.CSCarrierRateType, conn)
							if (defType && defType.get("EXT_CDE")) {
								'References' {
									'ReferenceType' defType.get("EXT_CDE")
									'ReferenceNumber' util.substring(current_CarrierRate.CarrierRateNumber?.trim(), 1, 35)
									//&quot; &#xA;else &#xA;   $varCarRefType
									def refDesc = current_CarrierRate.CSCarrierRateType
									if (refDesc=='SC') {
										refDesc = 'Service Contract Number'
									} else if (refDesc=='QUOT') {
										refDesc = 'Quotation Number'
									} else if (refDesc=='TARIF') {
										refDesc = 'Tariff Item Number'
									} else if (refDesc=='CSA') {
										refDesc = 'Customer Service Agreement'
									} else if (refDesc=='CSO') {
										refDesc = 'Carrier Rate Agreement'
									}
									'ReferenceDescription' refDesc 
								}
							}
						}
					}
					
					current_Body.ExternalReference?.each { current_ExternalReference ->
						if (current_ExternalReference.ReferenceNumber && current_ExternalReference.CSReferenceType) {
							HashMap<String, String> defType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null,  'XML', 'ReferenceType', current_ExternalReference.CSReferenceType, conn)
							if (defType?.get("EXT_CDE")) {
								//special filter 
								//($refType != BC/Body-BookingConfirm/pfx:AssociatedExternalReference-Body-BookingConfirm[1]/ns4:CSReferenceType-ExternalReferenceType or 
								//  not( exists($Start/root/ns6:MainInfo/ns6:BC/pfx:Body-BookingConfirm/pfx:AssociatedExternalReference-Body-BookingConfirm[1]/ns4:CSReferenceType-ExternalReferenceType)))
								if (assExtRefThe1st?.CSReferenceType==null || current_ExternalReference?.CSReferenceType != assExtRefThe1st.CSReferenceType) {
									'References' {
										'ReferenceType' defType.get("EXT_CDE")
										'ReferenceNumber' util.substring(current_ExternalReference.ReferenceNumber?.trim(), 1, 35)
										'ReferenceDescription' util.substring(current_ExternalReference.ReferenceDescription?.trim(), 1, 60)
									}
								}
							}
						}
					}
				}
				//end of References
				
				// Party
				'LegalParties' {
					//Carrier Party
					'Party' {
						'PartyType' 'CA'
						if (current_Body.GeneralInformation?.SCACCode=='OOLU') {
							'PartyName' 'Orient Overseas Container Line Ltd.'
							'CarrierCustomerCode' 'OOLU'
							'PartyLocation' {
								'Address' {
									'AddressLines' 'Orient Overseas Container Line Ltd.'
								}
							}
						} else {
							'CarrierCustomerCode' current_Body.GeneralInformation?.SCACCode
						}
					}
					
					//Party list
					def partyTypes = ['FWD':'FW', 'BPT':'BP', 'ANP':'AP', 'NPT': 'NP', 'SHP':'SH', 'CGN':'CN']
					List<Party> parties = current_Body.Party?.findAll(){it.PartyType && partyTypes.containsKey(it.PartyType)}
					if (parties && parties.size()>0) {
						parties.each { party ->
							'Party' {
								'PartyType' partyTypes.get(party.PartyType)
								'PartyName' util.substring(party.PartyName?.trim(), 1, 70)
								'CarrierCustomerCode' party.CarrierCustomerCode
								'PartyLocation' {
									'Address' {
										if (party.Address?.AddressLines?.AddressLine?.size()>0 && party.Address?.AddressLines?.AddressLine[0]) {
											'AddressLines' party.Address?.AddressLines?.AddressLine[0].trim()
										}
										if (party.Address?.AddressLines?.AddressLine?.size()>1 && party.Address?.AddressLines?.AddressLine[1]) {
											'AddressLines' party.Address?.AddressLines?.AddressLine[1].trim()
										}
										if (party.Address?.AddressLines?.AddressLine?.size()>2 && party.Address?.AddressLines?.AddressLine[2]) {
											'AddressLines' party.Address?.AddressLines?.AddressLine[2].trim()
										}
									}
									'City' party.Address?.City
									'County' party.Address?.County
									'StateProvince' party.Address?.State
									if (party.Address?.Country) {
										if (party.Address?.Country.length()<4) {
											'CountryCode' party.Address?.Country
											def cntryName = util.getCS2Country4CountryNameByCountryCode(party.Address?.Country, conn)
											if (cntryName) {
												'CountryName' cntryName
											}
										} else {
											def cntryCde = util.getCS2Country4CountryCodeByCountryName(party.Address?.Country, conn)
											if (cntryCde) {
												'CountryCode' cntryCde
											}
											'CountryName' party.Address?.Country
										}
									}
									'PostalCode' party.Address?.PostalCode
								}
								
								if (party.Contact?.FirstName || party.Contact?.LastName || party.Contact?.ContactPhone?.Number || party.Contact?.ContactEmailAddress) {
									'ContactPerson' {
										if (party.Contact?.FirstName) {
											'FirstName' util.substring(party.Contact?.FirstName, 1, 70)
										}
										if (party.Contact?.LastName) {
											'LastName' util.substring(party.Contact?.LastName, 1, 35)
										}
										if (party.Contact?.ContactPhone?.Number) {
											'Phone' {
												if (party.Contact?.ContactPhone?.CountryCode) {
													'CountryCode' util.substring(party.Contact?.ContactPhone?.CountryCode, 1, 3)
												}
												if (party.Contact?.ContactPhone?.AreaCode) {
													'AreaCode' util.substring(party.Contact?.ContactPhone?.AreaCode, 1, 5)
												}
												def defNum = util.substring(party.Contact?.ContactPhone?.Number.trim(), 1, 22)
												defNum = util.removeUnreableChar(defNum).trim()
												'Number' defNum
											}
										}
										if (party.Contact?.ContactFax?.Number) {
											'Fax' {
												if (party.Contact?.ContactFax?.CountryCode) {
													'CountryCode' util.substring(party.Contact?.ContactFax?.CountryCode, 1, 3)
												}
												if (party.Contact?.ContactFax?.AreaCode) {
													'AreaCode' util.substring(party.Contact?.ContactFax?.AreaCode, 1, 5)
												}
												if (party.Contact?.ContactFax?.CountryCode) {
													'Number' util.substring(party.Contact?.ContactFax?.Number, 1, 22)
												}
											}
										}
										if (party.Contact?.ContactEmailAddress) {
											'Email' util.substring(party.Contact?.ContactEmailAddress.trim(), 1, 60)
										}
									}
								}
							}
						}
					}
				}
				//end of party
				
				
				'RouteInformation' {
					'EventDate' {
						if (current_Body.GeneralInformation?.SICutOffDT?.LocDT?.LocDT) {
							'DocumentCutOffDate' 'TimeZone':current_Body.GeneralInformation?.SICutOffDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body.GeneralInformation?.SICutOffDT?.LocDT?.LocDT, yyyyMMddHHmmss)
						}
						if (current_Body.Route?.ReqDeliveryDT?.LocDT?.LocDT) {
							'ReqDeliveryDate' 'TimeZone':current_Body.Route?.ReqDeliveryDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body.Route?.ReqDeliveryDT?.LocDT?.LocDT, yyyyMMddHHmmss)
						}
						if (current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT) {
							'FullReturnCutoff' 'TimeZone':current_Body.Route?.FullReturnCutoff?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT, yyyyMMddHHmmss)
						}
						if (current_Body.Route?.ArrivalAtFinalHub && current_Body.Route?.ArrivalAtFinalHub.size()>0 && current_Body.Route?.ArrivalAtFinalHub?.get(0)?.LocDT?.LocDT) {
							'ArrivalAtFinalHub' 'TimeZone':current_Body.Route?.ArrivalAtFinalHub?.get(0).LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body.Route?.ArrivalAtFinalHub?.get(0)?.LocDT?.LocDT, yyyyMMddHHmmss)
						}
					}
					
					//Haulage
					'Haulage' {
						if (current_Body.Route?.Haulage?.OutBound) {
							'Outbound' current_Body.Route?.Haulage?.OutBound
						} else {
							'Outbound' 'C'
						}
						if (current_Body.Route?.Haulage?.InBound) {
							'Inbound' current_Body.Route?.Haulage?.InBound
						} else {
							'Inbound' 'C'
						}
					}
					
					// LocationDetails - POR
					if (current_Body.Route?.POR?.Facility?.FacilityName || current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'Location' {
							'FunctionCode' 'POR'
							if (current_Body.Route?.POR?.Facility?.FacilityName) {
								'LocationName' util.substring(current_Body.Route?.POR?.Facility?.FacilityName, 1, 35).trim()
							}
							'LocationDetails' {
								if (current_Body.Route?.POR?.CityDetails?.City) {
									'City' current_Body.Route?.POR?.CityDetails?.City?.trim()
								}
								if (current_Body.Route?.POR?.CityDetails?.County) {
									'County' current_Body.Route?.POR?.CityDetails?.County?.trim()
								}
								if (current_Body.Route?.POR?.CityDetails?.State) {
									'StateProvince' current_Body.Route?.POR?.CityDetails?.State?.trim()
								}
								
								if (current_Body.Route?.POR?.CSStandardCity?.CSCountryCode && current_Body.Route?.POR?.CSStandardCity?.CSCountryCode?.length()<4) {
									'CountryCode' current_Body.Route?.POR?.CSStandardCity?.CSCountryCode
								} else if (current_Body.Route?.POR?.CityDetails?.Country && current_Body.Route?.POR?.CityDetails?.Country?.length()>=4) {
									def cntryCde = util.getCS2Country4CountryCodeByCountryName(current_Body.Route?.POR?.CityDetails?.Country, conn)
									if (cntryCde) {
										'CountryCode' cntryCde
									}
								}
								if (current_Body.Route?.POR?.CityDetails?.Country && current_Body.Route?.POR?.CityDetails?.Country?.length()>=4) {
									'CountryName' current_Body.Route?.POR?.CityDetails?.Country
								} else if (current_Body.Route?.POR?.CSStandardCity?.CSCountryCode && current_Body.Route?.POR?.CSStandardCity?.CSCountryCode?.length()<4) {
									def cntryName = util.getCS2Country4CountryNameByCountryCode(current_Body.Route?.POR?.CSStandardCity?.CSCountryCode, conn)
									if (cntryName) {
										'CountryName' cntryName
									}
								}
								'LocationCode' {
									if (current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
										'UNLocationCode' current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
									}
									if (current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode && current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDType) {
										'SchedKDCode' 'Type':current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDType?.trim() , current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode?.trim()
									}
								}
							}
						}
					}
					
					// LocationDetails - FND
					if (current_Body.Route?.FND?.Facility?.FacilityName || current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'Location' {
							'FunctionCode' 'FND'
							if (current_Body.Route?.FND?.Facility?.FacilityName) {
								'LocationName' util.substring(current_Body.Route?.FND?.Facility?.FacilityName, 1, 35).trim()
							}
							'LocationDetails' {
								if (current_Body.Route?.FND?.CityDetails?.City) {
									'City' current_Body.Route?.FND?.CityDetails?.City?.trim()
								}
								if (current_Body.Route?.FND?.CityDetails?.County) {
									'County' current_Body.Route?.FND?.CityDetails?.County?.trim()
								}
								if (current_Body.Route?.FND?.CityDetails?.State) {
									'StateProvince' current_Body.Route?.FND?.CityDetails?.State?.trim()
								}
								if (current_Body.Route?.FND?.CSStandardCity?.CSCountryCode && current_Body.Route?.FND?.CSStandardCity?.CSCountryCode?.length()<4) {
									'CountryCode' current_Body.Route?.FND?.CSStandardCity?.CSCountryCode
								} else if (current_Body.Route?.FND?.CityDetails?.Country && current_Body.Route?.FND?.CityDetails?.Country?.length()>=4) {
									def cntryCde = util.getCS2Country4CountryCodeByCountryName(current_Body.Route?.FND?.CityDetails?.Country, conn)
									if (cntryCde) {
										'CountryCode' cntryCde
									}
								}
								if (current_Body.Route?.FND?.CityDetails?.Country && current_Body.Route?.FND?.CityDetails?.Country?.length()>=4) {
									'CountryName' current_Body.Route?.FND?.CityDetails?.Country
								} else if (current_Body.Route?.FND?.CSStandardCity?.CSCountryCode && current_Body.Route?.FND?.CSStandardCity?.CSCountryCode?.length()<4) {
									def cntryName = util.getCS2Country4CountryNameByCountryCode(current_Body.Route?.FND?.CSStandardCity?.CSCountryCode, conn)
									if (cntryName) {
										'CountryName' cntryName
									}
								}
								'LocationCode' {
									if (current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
										'UNLocationCode' current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
									}
									if (current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode && current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDType) {
										'SchedKDCode' 'Type':current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDType?.trim() , current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode?.trim()
									}
								}
							}
						}
					}
					
					//OceanLeg - POL POD
					current_Body.Route?.OceanLeg?.each { current_OceanLeg ->
						//POL
						if (current_OceanLeg?.POL?.Port?.PortName || current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode) {
							'Intermodal' {
								'FunctionCode' 'POL'
								'SequenceNumber' current_OceanLeg.LegSeq
								if (current_OceanLeg?.POL?.Port?.PortName) {
									'LocationName' util.substring(current_OceanLeg?.POL?.Port?.PortName, 1, 35).trim()
								}
								if (current_OceanLeg?.POL?.Facility?.FacilityCode) {
									'FacilityCode' util.substring(current_OceanLeg?.POL?.Facility?.FacilityCode?.trim(), 1, 5)
								}
								if (current_OceanLeg?.POL?.Facility?.FacilityName) {
									'FacilityName' util.substring(current_OceanLeg?.POL?.Facility?.FacilityName?.trim(), 1, 35)
								}
								'LocationDetails' {
									if (current_OceanLeg?.POL?.Port?.City) {
										'City' current_OceanLeg?.POL?.Port?.City
									}
									if (current_OceanLeg?.POL?.Port?.County) {
										'County' current_OceanLeg?.POL?.Port?.County
									}
									if (current_OceanLeg?.POL?.Port?.State) {
										'StateProvince' current_OceanLeg?.POL?.Port?.State
									}
									if (current_OceanLeg?.POL?.Port?.CSCountryCode) {
										if (current_OceanLeg?.POL?.Port?.CSCountryCode.length()<4) {
											'CountryCode' current_OceanLeg?.POL?.Port?.CSCountryCode
											def cntryName = util.getCS2Country4CountryNameByCountryCode(current_OceanLeg?.POL?.Port?.CSCountryCode, conn)
											if (cntryName) {
												'CountryName' cntryName
											}
										} else {
											def cntryCde = util.getCS2Country4CountryCodeByCountryName(current_OceanLeg?.POL?.Port?.CSCountryCode, conn)
											if (cntryCde) {
												'CountryCode' cntryCde
											}
											'CountryName' current_OceanLeg?.POL?.Port?.CSCountryCode
										}
									}
									'LocationCode' {
										if (current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode) {
											'UNLocationCode' current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode
										}
										if (current_OceanLeg?.POL?.Port?.LocationCode?.SchedKDCode && current_OceanLeg?.POL?.Port?.LocationCode?.SchedKDType) {
											'SchedKDCode' 'Type':current_OceanLeg?.POL?.Port?.LocationCode?.SchedKDType?.trim() , current_OceanLeg?.POL?.Port?.LocationCode?.SchedKDCode?.trim()
										}
									}
								}
								'EventDate' {
									LocDT actualDepartDt = current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
									LocDT estimateDepartDt = current_OceanLeg?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT
									if (actualDepartDt) {
										def tz = actualDepartDt.attr_TimeZone
										if (util.isEmpty(tz)) {
											if (current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT == actualDepartDt.LocDT &&
												current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.attr_TimeZone) {
												tz = current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.attr_TimeZone
											}
										}
										'Departure' 'EstActIndicator':'1', 'TimeZone':tz, util.convertXmlDateTime(actualDepartDt.LocDT, yyyyMMddHHmmss)
									} else if (estimateDepartDt) {
										def tz = estimateDepartDt.attr_TimeZone
										if (util.isEmpty(tz)) {
											if (current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT == estimateDepartDt.LocDT &&
												current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.attr_TimeZone) {
												tz = current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.attr_TimeZone
											}
										}
										'Departure' 'EstActIndicator':'0', 'TimeZone':tz, util.convertXmlDateTime(estimateDepartDt.LocDT, yyyyMMddHHmmss)
									}
								}
									
								if (current_OceanLeg.SVVD?.Loading?.Vessel || current_OceanLeg.SVVD?.Loading?.LloydsNumber || current_OceanLeg.SVVD?.Loading?.Service || current_OceanLeg.SVVD?.Loading?.VesselNationality || current_OceanLeg.SVVD?.Loading?.VesselName || current_OceanLeg.SVVD?.Loading?.Voyage) {
									'VesselVoyageInformation' {
										'VoyageEvent' 'Loading'
										if (current_OceanLeg.SVVD?.Loading?.Service) {
											'ServiceName' 'Code':util.substring(current_OceanLeg.SVVD?.Loading?.Service, 1, 5)
										}
										if (current_OceanLeg.LoadingExtVoyage) {
											'VoyageNumberDirection' current_OceanLeg.LoadingExtVoyage
										} else if (current_OceanLeg.SVVD?.Loading?.Voyage) {
											def vdir = current_OceanLeg.SVVD?.Loading?.Direction
											if (vdir != null) {
												vdir = vdir.trim()
											} else {
												vdir = ''
											}
											'VoyageNumberDirection' current_OceanLeg.SVVD?.Loading?.Voyage?.trim() + vdir
										}
										def defCallSign = current_OceanLeg.SVVD?.Loading?.CallSign
										if (defCallSign) {
											defCallSign = util.substring(defCallSign, 1, 10)
										}
										def defLloynum = current_OceanLeg.SVVD?.Loading?.LloydsNumber
										if (defLloynum) {
											defLloynum = util.substring(defLloynum, 1, 20)
										}
										'VesselInformation' {
											if (current_OceanLeg.SVVD?.Loading?.Vessel) {
												'VesselCode' 'CallSign': defCallSign, 'LloydsCode': defLloynum, util.substring(current_OceanLeg.SVVD?.Loading?.Vessel?.trim(), 1, 5)
											}
											if (current_OceanLeg.SVVD?.Loading?.VesselName) {
												'VesselName' util.substring(current_OceanLeg.SVVD?.Loading?.VesselName, 1, 30)
											}
											if (current_OceanLeg.SVVD?.Loading?.VesselNationality) {
												'VesselRegistrationCountry' current_OceanLeg.SVVD?.Loading?.VesselNationality
											}
										}
									}
								}
							}
						}
						
						//POD
						// copy from 1.0 logic, use POL for checking : if (current_OceanLeg?.POL?.Port?.PortName || current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode) {
						if (current_OceanLeg?.POL?.Port?.PortName || current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode) {
							'Intermodal' {
								'FunctionCode' 'POD'
								'SequenceNumber' current_OceanLeg.LegSeq
								if (current_OceanLeg?.POD?.Port?.PortName) {
									'LocationName' util.substring(current_OceanLeg?.POD?.Port?.PortName, 1, 35).trim()
								}
								if (current_OceanLeg?.POD?.Facility?.FacilityCode) {
									'FacilityCode' util.substring(current_OceanLeg?.POD?.Facility?.FacilityCode?.trim(), 1, 5)
								}
								if (current_OceanLeg?.POD?.Facility?.FacilityName) {
									'FacilityName' util.substring(current_OceanLeg?.POD?.Facility?.FacilityName?.trim(), 1, 35)
								}
								'LocationDetails' {
									if (current_OceanLeg?.POD?.Port?.City) {
										'City' current_OceanLeg?.POD?.Port?.City
									}
									if (current_OceanLeg?.POD?.Port?.County) {
										'County' current_OceanLeg?.POD?.Port?.County
									}
									if (current_OceanLeg?.POD?.Port?.State) {
										'StateProvince' current_OceanLeg?.POD?.Port?.State
									}
									if (current_OceanLeg?.POD?.Port?.CSCountryCode) {
										if (current_OceanLeg?.POD?.Port?.CSCountryCode.length()<4) {
											'CountryCode' current_OceanLeg?.POD?.Port?.CSCountryCode
										} else {
											'CountryCode' util.getCS2Country4CountryCodeByCountryName(current_OceanLeg?.POD?.Port?.CSCountryCode, conn)
										}
									}
									if (current_OceanLeg?.POD?.Port?.Country && current_OceanLeg?.POD?.Port?.Country?.length()>=4) {
										'CountryName' current_OceanLeg?.POD?.Port?.Country
									} else if (current_OceanLeg?.POD?.Port?.CSCountryCode) {
										'CountryName' util.getCS2Country4CountryNameByCountryCode(current_OceanLeg?.POD?.Port?.CSCountryCode, conn)
									}
									'LocationCode' {
										if (current_OceanLeg?.POD?.Port?.LocationCode?.UNLocationCode) {
											'UNLocationCode' current_OceanLeg?.POD?.Port?.LocationCode?.UNLocationCode
										}
										if (current_OceanLeg?.POD?.Port?.LocationCode?.SchedKDCode && current_OceanLeg?.POD?.Port?.LocationCode?.SchedKDType) {
											'SchedKDCode' 'Type':current_OceanLeg?.POD?.Port?.LocationCode?.SchedKDType?.trim() , current_OceanLeg?.POD?.Port?.LocationCode?.SchedKDCode?.trim()
										}
									}
								}
								'EventDate' {
									LocDT actualArrivalDt = current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT
									LocDT estimateArrivalDt = current_OceanLeg?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT
									if (actualArrivalDt) {
										def tz = actualArrivalDt.attr_TimeZone
										if (util.isEmpty(tz)) {
											if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT == actualArrivalDt.LocDT &&
												current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.attr_TimeZone) {
												tz = current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.attr_TimeZone
											}
										}
										'Arrival' 'EstActIndicator':'1', 'TimeZone':tz, util.convertXmlDateTime(actualArrivalDt.LocDT, yyyyMMddHHmmss)
									} else if (estimateArrivalDt) {
										def tz = estimateArrivalDt.attr_TimeZone
										if (util.isEmpty(tz)) {
											if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT == estimateArrivalDt.LocDT &&
												current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.attr_TimeZone) {
												tz = current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.attr_TimeZone
											}
										}
										'Arrival' 'EstActIndicator':'0', 'TimeZone':tz, util.convertXmlDateTime(estimateArrivalDt.LocDT, yyyyMMddHHmmss)
									}
								}
							}
						}
						//end of POD
					}
					//end of OceanLeg - POL POD
				}
				//end of RouteInformation
				
				
				//EquipmentInformation
				'EquipmentInformation' {
					current_Body.ContainerGroup?.Container?.each { current_Container ->
						'Containers' {
							if (current_Container.CarrCntrSizeType) {
								def defCntr = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, '', 'XML', 'ContainerType', current_Container.CarrCntrSizeType, conn)
								if (defCntr) {
									'ContainerType' defCntr.get('EXT_CDE')
								} else {
									'ContainerType' current_Container.CarrCntrSizeType
								}
							}
							if (current_Container.ContainerNumber) {
								'ContainerNumber' 'CheckDigit':current_Container.ContainerCheckDigit, current_Container.ContainerNumber
							}
							if (current_Container.IsSOC) {
								if (current_Container.IsSOC?.toUpperCase()=='FALSE' || current_Container.IsSOC=='0') {
									'SOCIndicator' '0'
								} else {
									'SOCIndicator' '1'
								}
							}
							if (current_Container.Seal?.size()>0 && current_Container.Seal?.get(0)?.SealNumber) {
								'SealNumber' 'Type':current_Container.Seal[0]?.SealType, current_Container.Seal[0]?.SealNumber
							}
							if (current_Container.GrossWeight?.Weight && current_Container.GrossWeight?.WeightUnit) {
								def weightUnit = current_Container.GrossWeight?.WeightUnit
								'Weight' 'Qualifier':'GROSS', 'Units':weightUnit, current_Container.GrossWeight?.Weight
							}
							def defOutbound = current_Container.TrafficMode?.OutBound
							if (util.isEmpty(defOutbound)) {
								defOutbound = current_Body.GeneralInformation?.TrafficMode?.OutBound
							}
							def defInbound = current_Container.TrafficMode?.InBound
							if (util.isEmpty(defInbound)) {
								defInbound = current_Body.GeneralInformation?.TrafficMode?.InBound
							}
							if (defOutbound || defInbound) {
								'TrafficMode' {
									'OutBound' defOutbound
									'InBound' defInbound
								}
							}
							
							//refer setting
							ReeferCargoSpec cargoReefer = current_Body.Cargo?.find{it.CurrentContainerNumber == current_Container.ContainerNumber && it.ReeferCargoSpec && it.ReeferCargoSpec?.size()>0}?.ReeferCargoSpec?.get(0)
							if (cargoReefer == null && current_Body.Cargo && current_Body.Cargo.size()>0 && current_Body.Cargo?.get(0)?.ReeferCargoSpec && current_Body.Cargo?.get(0)?.ReeferCargoSpec?.size()>0) {
								cargoReefer = current_Body.Cargo?.get(0)?.ReeferCargoSpec?.get(0)
							}
							if (cargoReefer) {
								def defCO2 = formatQty(cargoReefer.CO2)
								def defO2 = formatQty(cargoReefer.O2)
								def defPreCooling = ''
								if (cargoReefer.IsPreCoolingReq) {
									if (cargoReefer.IsPreCoolingReq=='true' || cargoReefer.IsPreCoolingReq=='1') {
										defPreCooling = '1'
									} else {
										defPreCooling = '0'
									}
								}
								def defROIndicator = ''
								if (cargoReefer.IsReeferOperational) {
									if (cargoReefer.IsReeferOperational=='true' || cargoReefer.IsReeferOperational=='1') {
										defROIndicator = '1'
									} else {
										defROIndicator = '0'
									}
								}
								def defCAIndicator = ''
								if (cargoReefer.IsControlledAtmosphere) {
									if (cargoReefer.IsControlledAtmosphere=='true' || cargoReefer.IsControlledAtmosphere=='1') {
										defCAIndicator = '1'
									} else {
										defCAIndicator = '0'
									}
								}
								def defHumidity = cargoReefer.DehumidityPercentage
								if (defHumidity != '0') {
									defHumidity = formatQty(defHumidity)
								}
								'ReeferSettings' ('AtmosphereType':cargoReefer.AtmosphereType, 'CAIndicator':defCAIndicator, 'CO2':defCO2, 'GenSetType':cargoReefer.GensetType, 'Humidity':defHumidity, 'O2':defO2, 'PreCooling':defPreCooling, 'ROIndicator':defROIndicator, 'VentSettingCode':cargoReefer.VentSettingCode) {
									if (cargoReefer.Temperature?.Temperature && cargoReefer.Temperature?.TemperatureUnit) {
										'Temperature' 'Units':cargoReefer.Temperature?.TemperatureUnit, cargoReefer.Temperature?.Temperature
									}
									if (cargoReefer.Ventilation?.Ventilation) {
										'Ventilation' 'Units':cargoReefer.Ventilation?.VentilationUnit, cargoReefer.Ventilation?.Ventilation
									}
									if (cargoReefer.SensitiveCargoDesc) {
										'SensitiveCargoDesc' util.substring(cargoReefer.SensitiveCargoDesc.trim(), 1, 20)
									}
									if (cargoReefer.Remarks) {
										'Remarks' {
											'RemarksLines' util.substring(cargoReefer.Remarks, 1, 240)
										}
									}
									cargoReefer.EmergencyContact?.each { current_EmergencyContact ->
										'EmergencyContact' {
											if (current_EmergencyContact.FirstName) {
												'FirstName' util.substring(current_EmergencyContact.FirstName, 1, 70)
											}
											if (current_EmergencyContact.LastName) {
												'LastName' util.substring(current_EmergencyContact.LastName, 1, 35)
											}
											'Phone' {
												if (current_EmergencyContact.ContactPhone?.CountryCode) {
													'CountryCode' util.substring(current_EmergencyContact.ContactPhone?.CountryCode, 1, 3)
												}
												if (current_EmergencyContact.ContactPhone?.AreaCode) {
													'AreaCode' util.substring(current_EmergencyContact.ContactPhone?.AreaCode, 1, 5)
												}
												if (current_EmergencyContact.ContactPhone?.Number) {
													def defNum = util.removeUnreableChar(current_EmergencyContact.ContactPhone?.Number.trim()).trim()
													'Number' util.substring(defNum, 1, 22)
												}
											}
											'Fax' {
												if (current_EmergencyContact.ContactFax?.CountryCode) {
													'CountryCode' util.substring(current_EmergencyContact.ContactFax?.CountryCode, 1, 3)
												}
												if (current_EmergencyContact.ContactFax?.AreaCode) {
													'AreaCode' util.substring(current_EmergencyContact.ContactFax?.AreaCode, 1, 5)
												}
												if (current_EmergencyContact.ContactFax?.Number) {
													def defNum = util.removeUnreableChar(current_EmergencyContact.ContactFax?.Number.trim()).trim()
													'Number' util.substring(defNum, 1, 22)
												}
											}
											if (current_EmergencyContact.ContactEmailAddress) {
												'Email' util.substring(current_EmergencyContact.ContactEmailAddress, 1, 60)
											}
										}
									}
								}
							}
						}
						//end of Containers
					}
					//end of loop Container
					
					
					//ContainerMovement
					'ContainerMovement' {
						//EmptyPickup
						current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup.each { current_EmptyPickup ->
							def cntrType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, '', 'XML', 'ContainerType', current_EmptyPickup.ISOSizeType, conn)
							if (cntrType==null || cntrType.get("EXT_CDE")==null) {
								cntrType = current_EmptyPickup.ISOSizeType
							} else {
								cntrType = cntrType.get("EXT_CDE")
							}
							'EmptyPickup' ('Number':current_EmptyPickup.NumberOfContainers, 'Type':cntrType) {
								'LocationDetails' {
									def defFacilityName = ''
									if (current_EmptyPickup.Facility?.FacilityName) {
										defFacilityName = util.substring(current_EmptyPickup.Facility?.FacilityName.trim(), 1, 35)?.trim()
									}
									if (current_EmptyPickup.Address?.LocationCode?.UNLocationCode || current_EmptyPickup.Address?.LocationCode?.SchedKDCode) {
										'LocationCode' {
											if (current_EmptyPickup.Address?.LocationCode?.UNLocationCode) {
												'UNLocationCode' current_EmptyPickup.Address?.LocationCode?.UNLocationCode.trim()
											}
											if (current_EmptyPickup.Address?.LocationCode?.SchedKDCode) {
												'SchedKDCode' 'Type':current_EmptyPickup.Address?.LocationCode?.SchedKDType.trim(), current_EmptyPickup.Address?.LocationCode?.SchedKDCode.trim()
											}
										}
									}
									'Address' {
										def defAdd1 = ''
										if (current_EmptyPickup.Address?.AddressLines?.AddressLine?.size()>0 && current_EmptyPickup.Address?.AddressLines?.AddressLine[0]) {
											defAdd1 = util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[0].trim(), 1, 55).trim()
										}
										if (defAdd1 && defAdd1 != defFacilityName) {
											'AddressLines' defAdd1
										}
										if (current_EmptyPickup.Address?.AddressLines?.AddressLine?.size()>1 && current_EmptyPickup.Address?.AddressLines?.AddressLine[1]) {
											'AddressLines' util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[1].trim(), 1, 55).trim()
										}
										if (current_EmptyPickup.Address?.AddressLines?.AddressLine?.size()>2 && current_EmptyPickup.Address?.AddressLines?.AddressLine[2]) {
											'AddressLines' util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[2].trim(), 1, 55).trim()
										}
										if (current_EmptyPickup.Address?.AddressLines?.AddressLine?.size()>3 && current_EmptyPickup.Address?.AddressLines?.AddressLine[3]) {
											'AddressLines' util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[3].trim(), 1, 55).trim()
										}
										if (current_EmptyPickup.Address?.AddressLines?.AddressLine?.size()>4 && current_EmptyPickup.Address?.AddressLines?.AddressLine[4]) {
											'AddressLines' util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[4].trim(), 1, 55).trim()
										}
									}
									//end of Address
									if (current_EmptyPickup.Address?.City) {
										'CityName' current_EmptyPickup.Address?.City.trim()
									}
									if (current_EmptyPickup.Address?.State) {
										'StateProvince' current_EmptyPickup.Address?.State.trim()
									}
									if (current_EmptyPickup.Address?.County) {
										'County' current_EmptyPickup.Address?.County.trim()
									}
									if (current_EmptyPickup.Address?.Country) {
										if (current_EmptyPickup.Address?.Country?.length()>=4) {
											def cntryCode = util.getCS2Country4CountryCodeByCountryName(current_EmptyPickup.Address?.Country, conn);
											if (cntryCode) {
												'CountryCode' cntryCode
											}
											'CountryName' current_EmptyPickup.Address?.Country
										} else {
											'CountryCode' current_EmptyPickup.Address?.Country
											def cntryName = util.getCS2Country4CountryNameByCountryCode(current_EmptyPickup.Address?.Country, conn)
											if (cntryName) {
												'CountryName' cntryName
											}
										}
									}
									if (current_EmptyPickup.Address?.PostalCode) {
										'PostalCode' current_EmptyPickup.Address?.PostalCode.trim()
									}
								}
								if (current_EmptyPickup.MvmtDT?.LocDT) {
									'EventDate' 'TimeZone':'LOCAL', util.convertXmlDateTime(current_EmptyPickup.MvmtDT?.LocDT, yyyyMMddHHmmss)
								}
							}
						}
						// end of EmptyPickup
						
						//EmptyReturn
						current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyReturn.each { current_EmptyReturn ->
							def cntrType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, '', 'XML', 'ContainerType', current_EmptyReturn.ISOSizeType, conn)
							if (cntrType && cntrType.get("EXT_CDE")) {
								cntrType = cntrType.get("EXT_CDE")
							} else {
								cntrType = current_EmptyReturn.ISOSizeType
							}
							'EmptyReturn' ('Number':current_EmptyReturn.NumberOfContainers, 'Type':cntrType) {
								'LocationDetails' {
									def defFacilityName = ''
									if (current_EmptyReturn.Facility?.FacilityName) {
										defFacilityName = util.substring(current_EmptyReturn.Facility?.FacilityName.trim(), 1, 35)?.trim()
									}
									'LocationCode' {
										if (current_EmptyReturn.Address?.LocationCode?.UNLocationCode) {
											'UNLocationCode' current_EmptyReturn.Address?.LocationCode?.UNLocationCode.trim()
										}
										if (current_EmptyReturn.Address?.LocationCode?.SchedKDCode) {
											'SchedKDCode' 'Type':current_EmptyReturn.Address?.LocationCode?.SchedKDType.trim(), current_EmptyReturn.Address?.LocationCode?.SchedKDCode.trim()
										}
									}
									'Address' {
										def defAdd1 = ''
										if (current_EmptyReturn.Address?.AddressLines?.AddressLine?.size()>0 && current_EmptyReturn.Address?.AddressLines?.AddressLine[0]) {
											defAdd1 = util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[0].trim(), 1, 55).trim()
										}
										if (defAdd1 && defAdd1 != defFacilityName) {
											'AddressLines' defAdd1
										}
										if (current_EmptyReturn.Address?.AddressLines?.AddressLine?.size()>1 && current_EmptyReturn.Address?.AddressLines?.AddressLine[1]) {
											'AddressLines' util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[1].trim(), 1, 55).trim()
										}
										if (current_EmptyReturn.Address?.AddressLines?.AddressLine?.size()>2 && current_EmptyReturn.Address?.AddressLines?.AddressLine[2]) {
											'AddressLines' util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[2].trim(), 1, 55)
										}
										if (current_EmptyReturn.Address?.AddressLines?.AddressLine?.size()>3 && current_EmptyReturn.Address?.AddressLines?.AddressLine[3]) {
											'AddressLines' util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[3].trim(), 1, 55).trim()
										}
										if (current_EmptyReturn.Address?.AddressLines?.AddressLine?.size()>4 && current_EmptyReturn.Address?.AddressLines?.AddressLine[4]) {
											'AddressLines' util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[4].trim(), 1, 55).trim()
										}
									}
									//end of Address
									if (current_EmptyReturn.Address?.City) {
										'CityName' current_EmptyReturn.Address?.City.trim()
									}
									if (current_EmptyReturn.Address?.State) {
										'StateProvince' current_EmptyReturn.Address?.State.trim()
									}
									if (current_EmptyReturn.Address?.City) {
										'County' current_EmptyReturn.Address?.City.trim()
									}
									if (current_EmptyReturn.Address?.Country) {
										if (current_EmptyReturn.Address?.Country?.length()>=4) {
											def cntryCode = util.getCS2Country4CountryCodeByCountryName(current_EmptyReturn.Address?.Country, conn);
											if (cntryCode) {
												'CountryCode' cntryCode
											}
											'CountryName' current_EmptyReturn.Address?.Country
										} else {
											'CountryCode' current_EmptyReturn.Address?.Country
											def cntryName = util.getCS2Country4CountryNameByCountryCode(current_EmptyReturn.Address?.Country, conn)
											if (cntryName) {
												'CountryName' cntryName
											}
										}
									}
									if (current_EmptyReturn.Address?.PostalCode) {
										'PostalCode' current_EmptyReturn.Address?.PostalCode.trim()
									}
								}
								if (current_EmptyReturn.MvmtDT?.GMT) {
									'EventDate' 'TimeZone':'LOCAL', util.convertXmlDateTime(current_EmptyReturn.MvmtDT?.GMT, yyyyMMddHHmmss)
								} else if (current_EmptyReturn.MvmtDT?.LocDT) {
									'EventDate' 'TimeZone':'LOCAL', util.convertXmlDateTime(current_EmptyReturn.MvmtDT?.LocDT, yyyyMMddHHmmss)
								}
							}
						}
						//end of EmptyReturn
						
						// FullReturn
						current_Body.ContainerGroup?.ContainerFlowInstruction?.FullReturn.each { current_FullReturn ->
							def cntrType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, '', 'XML', 'ContainerType', current_FullReturn.ISOSizeType, conn)
							if (cntrType==null || cntrType.get("EXT_CDE")==null) {
								cntrType = current_FullReturn.ISOSizeType
							} else {
								cntrType = cntrType.get("EXT_CDE")
							}
							'FullReturn' ('Number':current_FullReturn.NumberOfContainers, 'Type':cntrType) {
								'LocationDetails' {
									def defFacilityName = ''
									if (current_FullReturn.Facility?.FacilityName) {
										defFacilityName = util.substring(current_FullReturn.Facility?.FacilityName.trim(), 1, 35)?.trim()
									}
									'LocationCode' {
										if (current_FullReturn.Address?.LocationCode?.UNLocationCode) {
											'UNLocationCode' current_FullReturn.Address?.LocationCode?.UNLocationCode.trim()
										}
										if (current_FullReturn.Address?.LocationCode?.SchedKDCode) {
											'SchedKDCode' 'Type':current_FullReturn.Address?.LocationCode?.SchedKDType.trim(), current_FullReturn.Address?.LocationCode?.SchedKDCode.trim()
										}
									}
									'Address' {
										def defAdd1 = ''
										if (current_FullReturn.Address?.AddressLines?.AddressLine?.size()>0 && current_FullReturn.Address?.AddressLines?.AddressLine[0]) {
											defAdd1 = util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[0].trim(), 1, 55).trim()
										}
										if (defAdd1 && defAdd1 != defFacilityName) {
											'AddressLines' defAdd1
										}
										if (current_FullReturn.Address?.AddressLines?.AddressLine?.size()>1 && current_FullReturn.Address?.AddressLines?.AddressLine[1]) {
											'AddressLines' util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[1].trim(), 1, 55).trim()
										}
										if (current_FullReturn.Address?.AddressLines?.AddressLine?.size()>2 && current_FullReturn.Address?.AddressLines?.AddressLine[2]) {
											'AddressLines' util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[2].trim(), 1, 55).trim()
										}
										if (current_FullReturn.Address?.AddressLines?.AddressLine?.size()>3 && current_FullReturn.Address?.AddressLines?.AddressLine[3]) {
											'AddressLines' util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[3].trim(), 1, 55).trim()
										}
										if (current_FullReturn.Address?.AddressLines?.AddressLine?.size()>4 && current_FullReturn.Address?.AddressLines?.AddressLine[4]) {
											'AddressLines' util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[4].trim(), 1, 55).trim()
										}
									}
									//end of Address
									if (current_FullReturn.Address?.City) {
										'CityName' current_FullReturn.Address?.City.trim()
									}
									if (current_FullReturn.Address?.State) {
										'StateProvince' current_FullReturn.Address?.State.trim()
									}
									if (current_FullReturn.Address?.County) {
										'County' current_FullReturn.Address?.County.trim()
									}
									if (current_FullReturn.Address?.Country) {
										if (current_FullReturn.Address?.Country?.length()>=4) {
											def cntryCode = util.getCS2Country4CountryCodeByCountryName(current_FullReturn.Address?.Country, conn);
											if (cntryCode) {
												'CountryCode' cntryCode
											}
											'CountryName' current_FullReturn.Address?.Country
										} else {
											'CountryCode' current_FullReturn.Address?.Country
											def cntryName = util.getCS2Country4CountryNameByCountryCode(current_FullReturn.Address?.Country, conn)
											if (cntryName) {
												'CountryName' cntryName
											}
										}
									}
									if (current_FullReturn.Address?.PostalCode) {
										'PostalCode' current_FullReturn.Address?.PostalCode.trim()
									}
								}
								if (current_FullReturn.MvmtDT?.LocDT?.LocDT) {
									'EventDate' 'TimeZone':'LOCAL', util.convertXmlDateTime(current_FullReturn.MvmtDT?.LocDT.LocDT, yyyyMMddHHmmss)
								}
							}
						}
						// end of FullReturn	
					}
					//end of ContainerMovement
				}
				//end of EquipmentInformation
					
				//CargoInformation
				if (current_Body.Cargo) {
					'CargoInformation' {
						current_Body.Cargo?.each { current_Cargo ->
							'CargoDetails' {
								'CargoNature' current_Cargo.CargoNature
								if (current_Cargo.NetWeight?.Weight && Double.parseDouble(current_Cargo.NetWeight?.Weight) > 0 && current_Cargo.NetWeight?.WeightUnit) {
									def weightUnit = current_Cargo.NetWeight?.WeightUnit
									'Weight' 'Qualifier':'NET', 'Units':weightUnit, current_Cargo.NetWeight?.Weight
								}
								if (current_Cargo.GrossWeight?.Weight && Double.parseDouble(current_Cargo.GrossWeight?.Weight) > 0 && current_Cargo.GrossWeight?.WeightUnit) {
									def weightUnit = current_Cargo.GrossWeight?.WeightUnit
									'Weight' 'Qualifier':'GROSS', 'Units':weightUnit, current_Cargo.GrossWeight?.Weight
								}
								if (current_Cargo.Volume?.Volume && Double.parseDouble(current_Cargo.Volume?.Volume) != 0) {
									def volumeUnit = current_Cargo.Volume?.VolumeUnit
									if (util.isEmpty(volumeUnit) || volumeUnit=='CFT') {
										volumeUnit = 'CBF'
									}
									'Volume' 'Units':volumeUnit, current_Cargo.Volume?.Volume
								}
								if (current_Cargo.Packaging?.PackageQty || current_Cargo.Packaging?.PackageType) {
									def packageQty = current_Cargo.Packaging?.PackageQty
									if (util.isEmpty(packageQty)) {
										packageQty = "0"
									}
									def ptype = current_Cargo.Packaging?.PackageType
									if (ptype) {
										ptype = ptype.trim()
										HashMap<String, String> pkgTypeConvert = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null, 'XML', 'PackageUnit', ptype, conn)
										if (pkgTypeConvert && pkgTypeConvert.get("EXT_CDE")) {
											ptype = pkgTypeConvert.get("EXT_CDE").trim();
										}
									}
									'Package' 'Type':ptype, packageQty
								}
								if (current_Cargo.CurrentContainerNumber) {
									'ContainerNumber' 'CheckDigit':current_Cargo.CurrentContainerCheckDigit, current_Cargo.CurrentContainerNumber
								}
								if (current_Cargo.CargoDescription) {
									'CargoDescription' {
										'DescriptionLine' util.substring(current_Cargo.CargoDescription.trim(), 1, 60)
										if (current_Cargo.CargoDescription.trim().length()>60) {
											'DescriptionLine' util.substring(current_Cargo.CargoDescription, 61, 60)
										}
									}
								}
								if (current_Cargo.MarksAndNumbers && current_Cargo.MarksAndNumbers.size()>0) {
									'MarksAndNumbers' {
										current_Cargo.MarksAndNumbers.each { current_MarksAndNumbers ->
											if (util.isNotEmpty(current_MarksAndNumbers?.MarksAndNumbersLine)) {
												'MarksAndNumbersLine' current_MarksAndNumbers.MarksAndNumbersLine
											}
										}
									}
								}
								//DangerousCargo
								'DangerousCargo' {
									current_Cargo.DGCargoSpec?.each { current_DGCargoSpec ->
										'DangerousCargoInfo' {
											'HazardousMaterial' {
												if (current_DGCargoSpec.DGRegulator) {
													'DGRegulator' current_DGCargoSpec.DGRegulator.trim()
												}
												if (current_DGCargoSpec.IMOClass) {
													'IMCOClass' current_DGCargoSpec.IMOClass.trim()
												}
												if (current_DGCargoSpec.IMDGPage) {
													'IMDGPage' current_DGCargoSpec.IMDGPage.trim()
												}
												if (current_DGCargoSpec.UNNumber) {
													'UNNumber' current_DGCargoSpec.UNNumber.trim()
												}
												if (current_DGCargoSpec.TechnicalName) {
													'TechnicalShippingName' current_DGCargoSpec.TechnicalName.trim()
												}
												if (current_DGCargoSpec.ProperShippingName) {
													'ProperShippingName' current_DGCargoSpec.ProperShippingName.trim()
												}
												if (current_DGCargoSpec.EMSNumber) {
													'EMSNumber' current_DGCargoSpec.EMSNumber.trim()
												}
												if (current_DGCargoSpec.PSAClass) {
													'PSAClass' current_DGCargoSpec.PSAClass.trim()
												}
												if (current_DGCargoSpec.MFAGNumber) {
													'MFAGPageNumber' current_DGCargoSpec.MFAGNumber.trim()
												}
												if (current_DGCargoSpec.FlashPoint?.Temperature && Double.parseDouble(current_DGCargoSpec.FlashPoint?.Temperature) != 0 && current_DGCargoSpec.FlashPoint?.TemperatureUnit) {
													'FlashPoint' 'Units':current_DGCargoSpec.FlashPoint?.TemperatureUnit, current_DGCargoSpec.FlashPoint?.Temperature.trim()
												}
												if (current_DGCargoSpec.ElevatedTemperature?.Temperature && (Double.parseDouble(current_DGCargoSpec.ElevatedTemperature?.Temperature)!=0 || current_DGCargoSpec.ElevatedTemperature?.TemperatureUnit)) {
													'DGElevationTemperature' 'Units':current_DGCargoSpec.ElevatedTemperature?.TemperatureUnit, current_DGCargoSpec.ElevatedTemperature?.Temperature.trim()
												}
												if (current_DGCargoSpec.State) {
													'State' current_DGCargoSpec.State.trim()
												}
//												if (current_DGCargoSpec.ApprovalCode) {
//													'ApprovalCode' util.substring(current_DGCargoSpec.ApprovalCode.trim(), 1, 20)
//												}
												
												'Label' getArrayValue(current_DGCargoSpec.Label, 0)
												'Label' getArrayValue(current_DGCargoSpec.Label, 1)
												'Label' getArrayValue(current_DGCargoSpec.Label, 2)
												'Label' getArrayValue(current_DGCargoSpec.Label, 3)
												
												if (current_DGCargoSpec.GrossWeight?.Weight && (Double.parseDouble(current_DGCargoSpec.GrossWeight?.Weight)!=0 || current_DGCargoSpec.GrossWeight?.WeightUnit)) {
													def weightUnit = current_DGCargoSpec.GrossWeight?.WeightUnit
													'Weight' 'Qualifier':'GROSS', 'Units':weightUnit, current_DGCargoSpec.GrossWeight?.Weight
												}
												if (current_DGCargoSpec.NetWeight?.Weight && (Double.parseDouble(current_DGCargoSpec.NetWeight?.Weight)!=0 || current_DGCargoSpec.NetWeight?.WeightUnit)) {
													def weightUnit = current_DGCargoSpec.NetWeight.WeightUnit
													'Weight' 'Qualifier':'NET', 'Units':weightUnit, current_DGCargoSpec.NetWeight?.Weight
												}
												if (current_DGCargoSpec.NetExplosiveWeight?.Weight && (Double.parseDouble(current_DGCargoSpec.NetExplosiveWeight?.Weight)!=0 || current_DGCargoSpec.NetExplosiveWeight?.WeightUnit)) {
													'NetExplosiveWeight' 'Units':current_DGCargoSpec.NetExplosiveWeight?.WeightUnit, current_DGCargoSpec.NetExplosiveWeight?.Weight
												}
												'Indicators' {
													if (current_DGCargoSpec.isMarinePollutant=='false' || current_DGCargoSpec.isMarinePollutant=='0') {
														'isMarinePollutant' '0'
													} else {
														'isMarinePollutant' '1'
													}
													if (current_DGCargoSpec.isInhalationHazardous=='false' || current_DGCargoSpec.isInhalationHazardous=='0') {
														'isInhalationHazardous' '0'
													} else {
														'isInhalationHazardous' '1'
													}
													if (current_DGCargoSpec.isLimitedQuantity=='false' || current_DGCargoSpec.isLimitedQuantity=='0') {
														'isLimitedQuantity' '0'
													} else {
														'isLimitedQuantity' '1'
													}
													if (current_DGCargoSpec.isReportableQuantity=='false' || current_DGCargoSpec.isReportableQuantity=='0') {
														'isReportableQuantity' '0'
													} else {
														'isReportableQuantity' '1'
													}
													if (current_DGCargoSpec.IsEmptyUnclean=='false' || current_DGCargoSpec.IsEmptyUnclean=='0') {
														'isEmptyUnclean' '0'
													} else {
														'isEmptyUnclean' '1'
													}
												}
												'Package' {
													def pCode = current_DGCargoSpec.PackageGroup?.Code
													if (pCode) {
														if (pCode=='1') {
															pCode = 'I'
														} else if (pCode=='2') {
															pCode = 'II'
														} else if (pCode=='3') {
															pCode = 'III'
														}
														'PackagingGroupCode' pCode
													}
													if (current_DGCargoSpec.PackageGroup?.InnerPackageDescription?.PackageDesc) {
														def pkgType = current_DGCargoSpec.PackageGroup?.InnerPackageDescription?.PackageType?.trim()
														HashMap<String, String> pkgTypeConvert = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null, 'XML', 'PackageUnit', pkgType, conn)
														if (pkgTypeConvert && pkgTypeConvert.get("EXT_CDE")) {
															pkgType = pkgTypeConvert.get("EXT_CDE").trim();
														}
														'InnerPackaging' 'Type':pkgType, current_DGCargoSpec.PackageGroup.InnerPackageDescription?.PackageDesc.trim()
													}
													if (current_DGCargoSpec.PackageGroup?.OuterPackageDescription?.PackageDesc || (current_DGCargoSpec.PackageGroup?.OuterPackageDescription?.PackageType && (pCode || current_DGCargoSpec.PackageGroup?.InnerPackageDescription?.PackageDesc))) {
														def pkgType = current_DGCargoSpec.PackageGroup?.OuterPackageDescription?.PackageType?.trim()
														HashMap<String, String> pkgTypeConvert = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null, 'XML', 'PackageUnit', pkgType, conn)
														if (pkgTypeConvert && pkgTypeConvert.get("EXT_CDE")) {
															pkgType = pkgTypeConvert.get("EXT_CDE").trim();
														}
														'OuterPackaging' 'Type':pkgType, current_DGCargoSpec.PackageGroup?.OuterPackageDescription?.PackageDesc?.trim()
													}
												}
												'Remarks' {
													if (current_DGCargoSpec.Remarks) {
														'RemarksLines' current_DGCargoSpec.Remarks.trim()
													}
												}
											}
											//end of HazardousMaterial
											
											//EmergencyContact
											current_DGCargoSpec.EmergencyContact?.each { current_EmergencyContact ->
												'EmergencyContact' {
													if (current_EmergencyContact.FirstName) {
														'FirstName' util.substring(current_EmergencyContact.FirstName, 1, 70)
													}
													if (current_EmergencyContact.LastName) {
														'LastName' util.substring(current_EmergencyContact.LastName, 1, 35)
													}
													'Phone' {
														if (current_EmergencyContact.ContactPhone?.CountryCode) {
															'CountryCode' util.substring(current_EmergencyContact.ContactPhone?.CountryCode, 1, 3)
														}
														if (current_EmergencyContact.ContactPhone?.AreaCode) {
															'AreaCode' util.substring(current_EmergencyContact.ContactPhone?.AreaCode, 1, 5)
														}
														if (current_EmergencyContact.ContactPhone?.Number) {
															def defNum = util.removeUnreableChar(current_EmergencyContact.ContactPhone?.Number.trim()).trim()
															'Number' util.substring(defNum, 1, 22)
														}
													}
													'Fax' {
														if (current_EmergencyContact.ContactFax?.CountryCode) {
															'CountryCode' util.substring(current_EmergencyContact.ContactFax?.CountryCode, 1, 3)
														}
														if (current_EmergencyContact.ContactFax?.AreaCode) {
															'AreaCode' util.substring(current_EmergencyContact.ContactFax?.AreaCode, 1, 5)
														}
														if (current_EmergencyContact.ContactFax?.Number) {
															def defNum = util.removeUnreableChar(current_EmergencyContact.ContactFax?.Number.trim()).trim()
															'Number' util.substring(defNum, 1, 22)
														}
													}
													if (current_EmergencyContact.ContactEmailAddress) {
														'Email' util.substring(current_EmergencyContact.ContactEmailAddress, 1, 60)
													}
												}
											}
											//end of EmergencyContact
										}
									}
								}
								//end of DangerousCargo
								
								//AwkwardCargo
								'AwkwardCargo' {
									current_Cargo.AWCargoSpec?.each { current_AWCargoSpec ->
										if (current_AWCargoSpec?.Height?.Length && current_AWCargoSpec?.Height?.Length!='0' && current_AWCargoSpec?.Length?.Length && current_AWCargoSpec?.Length?.Length!='0' && current_AWCargoSpec?.Width?.Length && current_AWCargoSpec?.Width?.Length!='0') {
											'AwkwardCargoInfo' {
												'AwkwardCargoDetails' {
													'Height' 'Units': current_AWCargoSpec.Height?.LengthUnit, current_AWCargoSpec.Height?.Length
													'Width' 'Units': current_AWCargoSpec.Width?.LengthUnit, current_AWCargoSpec.Width?.Length
													'Length' 'Units': current_AWCargoSpec.Length?.LengthUnit, current_AWCargoSpec.Length?.Length
													if (current_AWCargoSpec.Remarks) {
														'Remarks' {
															'RemarksLines' util.substring(current_AWCargoSpec.Remarks, 1, 240)
														}
													}
												}
												current_AWCargoSpec.EmergencyContact?.each { current_EmergencyContact ->
													'EmergencyContact' {
														if (current_EmergencyContact.FirstName) {
															'FirstName' util.substring(current_EmergencyContact.FirstName, 1, 70)
														}
														if (current_EmergencyContact.LastName) {
															'LastName' util.substring(current_EmergencyContact.LastName, 1, 35)
														}
														'Phone' {
															if (current_EmergencyContact.ContactPhone?.CountryCode) {
																'CountryCode' util.substring(current_EmergencyContact.ContactPhone?.CountryCode, 1, 3)
															}
															if (current_EmergencyContact.ContactPhone?.AreaCode) {
																'AreaCode' util.substring(current_EmergencyContact.ContactPhone?.AreaCode, 1, 5)
															}
															if (current_EmergencyContact.ContactPhone?.Number) {
																def defNum = util.removeUnreableChar(current_EmergencyContact.ContactPhone?.Number.trim()).trim()
																'Number' util.substring(defNum, 1, 22)
															}
														}
														'Fax' {
															if (current_EmergencyContact.ContactFax?.CountryCode) {
																'CountryCode' util.substring(current_EmergencyContact.ContactFax?.CountryCode, 1, 3)
															}
															if (current_EmergencyContact.ContactFax?.AreaCode) {
																'AreaCode' util.substring(current_EmergencyContact.ContactFax?.AreaCode, 1, 5)
															}
															if (current_EmergencyContact.ContactFax?.Number) {
																def defNum = util.removeUnreableChar(current_EmergencyContact.ContactFax?.Number.trim()).trim()
																'Number' util.substring(defNum, 1, 22)
															}
														}
														if (current_EmergencyContact.ContactEmailAddress) {
															'Email' util.substring(current_EmergencyContact.ContactEmailAddress, 1, 60)
														}
													}
												}
											}
										}
									}
									//end of AwkwardCargo
								}
							}
							//end of CargoInformation
						}
					}
				}
				//end of CargoInformation
			}
			//end of MessageBody
			
			//summary part
			'SummaryDetails' {
				def remarkLines = current_Body.RemarkLines?.findAll{it.attr_RemarkType=='Booking' && util.isNotEmpty(it.RemarkLines)}.join(" ")
				if (util.isEmpty(remarkLines)) {
					if (current_Body.Remarks) {
						remarkLines = current_Body.Remarks.trim()
					}
				} else {
					remarkLines = remarkLines.trim()
				}
				
				'OtherRemarks' {
					if (remarkLines) {
						'RemarksLines' util.substring(remarkLines, 1, 100).trim()
						if (util.substring(remarkLines, 101, 100)) {
							'RemarksLines' util.substring(remarkLines, 101, 100).trim()
						}
						if (util.substring(remarkLines, 201, 100)) {
							'RemarksLines' util.substring(remarkLines, 201, 100).trim()
						}
						if (util.substring(remarkLines, 301, 100)) {
							'RemarksLines' util.substring(remarkLines, 301, 100).trim()
						}
						if (util.substring(remarkLines, 401, 100)) {
							'RemarksLines' util.substring(remarkLines, 401, 100).trim()
						}
					}
				}
			}
		}
		//end of loop BC
	}


	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		//inputXmlBody = util.removeBOM(inputXmlBody)

		//default BC pre-process, remove special unreable char
		//inputXmlBody = util.removeUnreableChar(inputXmlBody)
		
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
		BookingConfirm bc = parser.xmlParser(inputXmlBody, BookingConfirm.class)


		def writer = new StringWriter()
		//def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false))
		def outXml = new MarkupBuilder(writer)
		outXml.omitEmptyAttributes = true
		outXml.omitNullAttributes = true
		outXml.useDoubleQuotes = true
		
		outXml.mkp.xmlDeclaration(version: "1.0")
		
		//writer.append("\r\n")
		
		def bizKeyWriter = new StringWriter();
		def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeyWriter), "", false))
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def csuploadWriter = new StringWriter();
		def csuploadXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(csuploadWriter), "", false))
		csuploadXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")


		/**
		 * Part IV: mapping script start from here
		 */

		//create root node
		def XMLBooking = outXml.createNode('Booking')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.

		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()
		def headerMsgDT = ''
		if (bc.Header?.MsgDT?.LocDT?.LocDT) {
			headerMsgDT = util.convertXmlDateTime(bc.Header?.MsgDT?.LocDT?.LocDT, yyyyMMddHHmmss)
		} else if (bc.Header?.MsgDT?.GMT) {
			headerMsgDT = util.convertXmlDateTime(bc.Header?.MsgDT?.GMT, yyyyMMddHHmmss)
		}
		def txnErrorKeys = []

		//Start mapping
		bc?.Body?.eachWithIndex{current_Body, current_BodyIndex ->
			if (current_BodyIndex>0) {
				//currently only map the 1st BC, as CS2 always output 1 txn in one file
				return
			}
			//prep checking
			List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
			prepValidation(current_Body, current_BodyIndex, errorKeyList)
			
			//mapping
			if (errorKeyList.isEmpty()) {
				generateBody(current_Body, bc.Header, outXml)
			}
			
			// posp checking
			if (errorKeyList.isEmpty()) {
				//scheam validation
				String validateStr = util.cleanXml(writer.toString() + "\r\n</Booking>")
				pospValidation(current_Body, validateStr, errorKeyList)
			}

			bcUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			bcUtil.buildBizKey(bizKeyXml, bc.Header, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, TP_ID, conn)

			txnErrorKeys.add(errorKeyList)
		}

		//End root node
		outXml.nodeCompleted(null, XMLBooking)
		
		bizKeyXml.nodeCompleted(null, bizKeyRoot)
		
		csuploadXml.nodeCompleted(null, csuploadRoot)

		bcUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		bcUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		bcUtil.promoteHeaderIntChgMsgId(appSessionId, bc.Header?.InterchangeMessageID);
		if (bc.Body && bc.Body.size()>0 && bc.Body[0]) {
			bcUtil.promoteScacCode(appSessionId, bc.Body[0].GeneralInformation?.SCACCode);
		}
		
		String result = '';
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			result = writer?.toString();
			result = util.cleanXml(result)
			//result = result.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\"?>\r\n");
			
			//output file name
			String msgReqId = MSG_REQ_ID
			if (msgReqId==null) {
				msgReqId = ''
			}
			if (msgReqId.indexOf("FWK2-")>=0) {
				msgReqId = msgReqId.replace("FWK2-", "EDI");
			}
			
			String outputFileName = TP_ID + "_BCXML_" + MSG_REQ_ID + ".xml"
			bcUtil.promoteOutputFileNameToSession(appSessionId, outputFileName)
		}

		writer.close();
		
		return result;
	}

	
	void prepValidation(Body current_Body, int bodyLoopIndex, List<Map<String,String>> errorKeyList) {
		List<String> filterStatusList = new ArrayList<String>()
		filterStatusList.add("PreBooking")
		filterStatusList.add("Terminated")
		
		def varBkgStatus = current_Body.GeneralInformation?.BookingStatus
		//1. check booking confirm status
		bcUtil.filterBCStatus(varBkgStatus, filterStatusList, false, null, errorKeyList)
		
		//2. check booking number
		bcUtil.checkCarrierBookingNumber(current_Body, true, null, errorKeyList)
	}
	
	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList) {
		//scheam validation
//		println '-------------------'
//		println 'outputXml: '+outputXml
//		println '-------------------'
		
		bcUtil.customerSchemaValidation(outputXml, 'CUS-BCXML-OLL_NEWSTD', errorKeyList)
		
	}
	
	String formatQty(def defStr) {
		String ret = defStr
		if (util.isEmpty(ret)) {
			ret = ''
		} else {
			if (Double.parseDouble(defStr)==0) {
				ret = ''
			} else {
				int maxLoopCount = ret.length()
				for(int i=0; i<maxLoopCount && ret.startsWith("0"); i++) {
					ret = ret.substring(1)
				}
			}
		}
		return ret
	}
	
	String getArrayValue(def arrayData, int pos) {
		String ret = ''
		if (pos>=0 && arrayData && arrayData.size()>pos) {
			if (arrayData[pos]) {
				ret = arrayData[pos].trim()
				if (ret=='null') {
					ret = ''
				}
			}
		}
		return ret
	}
}
