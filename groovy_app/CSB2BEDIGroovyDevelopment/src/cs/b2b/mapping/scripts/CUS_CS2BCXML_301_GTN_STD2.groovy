package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.DecimalFormat
import java.util.List;
import java.util.Map

import cs.b2b.core.mapping.bean.bc.Address;
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.Contact;
import cs.b2b.core.mapping.bean.bc.ContainerNumbers;
import cs.b2b.core.mapping.bean.bc.EmptyPickup
import cs.b2b.core.mapping.bean.bc.ExternalReference
import cs.b2b.core.mapping.bean.bc.Facility;
import cs.b2b.core.mapping.bean.bc.MvmtDT;
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.core.mapping.util.XmlBeanParser


/**
 * For TP_ID: LIFUNGGTN
 * 
 * @author LIANGDA
 * @author YINEM 
 * B2BSCR20170606004028A http://i2isd/sites/csisa/Lists/Workplan/DispForm.aspx?ID=30361   
 *
 */

public class CUS_CS2BCXML_301_GTN_STD2 {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_BC_O_Common bcUtil = new cs.b2b.core.mapping.util.MappingUtil_BC_O_Common(util);

	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	
	def tpBizAgreementDisplayName = null
	
	Connection conn = null

	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = 'HHmm'

	def currentSystemDt = null

	//for STD1, this status add one more "Pending" : "B"
	Map<String, String> bookingStatusMap = ["Confirmed":"A", "Cancelled":"D", "Pending":"B", "Declined":"D"]

	public void generateBody(Body current_Body, MarkupBuilder outXml) {
		if (current_Body==null) {
			return
		}
		
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '301'
				'E329_02' '-999'
			}
			'B1' {
				'E140_01' current_Body.GeneralInformation?.SCACCode
								
				def IDNum = ""
				if (! current_Body.GeneralInformation?.CarrierBookingNumber?.trim()?.endsWith("0")) {
					IDNum = "BLANK"
				} else {
					List<ExternalReference> SIDs = current_Body.ExternalReference?.findAll(){it?.ReferenceDescription && it?.ReferenceDescription?.toUpperCase()?.indexOf("SYSTEM REFERENCE NUMBER")>=0}
					if (SIDs && SIDs.size()>0 && SIDs[-1] && SIDs[-1].ReferenceNumber != "EDI Booking Confirmation") {
						String ref = SIDs[-1].ReferenceNumber
						IDNum = util.substring(ref, 1, 30)
					} else {
						IDNum = "BLANK"
					}
				}
				'E145_02' IDNum
				
				'E373_03' util.convertDateTime(current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
				
				def BookingStatus = bookingStatusMap.get(current_Body.GeneralInformation?.BookingStatus)
				if (! BookingStatus) {
					BookingStatus = "X"
				}
				'E558_04' BookingStatus
			}
			
			'Y3' {
				def defCarBkgNum = ''
				if (current_Body.GeneralInformation?.SCACCode && current_Body?.GeneralInformation?.CarrierBookingNumber) {
					defCarBkgNum = current_Body.GeneralInformation?.CarrierBookingNumber
				} else if (current_Body.GeneralInformation?.BookingStatus == "Declined") {
					defCarBkgNum = "DECLINE"
				}
				'E13_01' defCarBkgNum
				
				'E140_02' current_Body.GeneralInformation?.SCACCode
				
				if (current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT) {
					'E373_03' util.convertXmlDateTime(current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT, yyyyMMdd)
				}
				if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT) {
					'E373_04'  util.convertXmlDateTime(current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT, yyyyMMdd)
				}
				if (current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT) {
					'E373_07'  util.convertXmlDateTime(current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT, yyyyMMdd)
				}
				if (current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT) {
					'E337_08'   util.convertXmlDateTime(current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT, HHmm)
				}
				
				def ob = ""
				def ib = ""
				if (current_Body.Route?.Haulage?.OutBound == 'C') {
					ob = 'D'
				} else if (current_Body.Route?.Haulage?.OutBound == 'M') {
					ob = 'P'
				}
				if (current_Body.Route?.Haulage?.InBound == 'C') {
					ib = 'D'
				} else if (current_Body.Route?.Haulage?.InBound == 'M') {
					ib = 'P'
				}
				if (ob && ib) {
					'E375_10' ob + ib
				}
			}
			
			//Loop_Y4
			Map<String, Y4Data> defEmptyPickupGroups = [:]
			current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup?.eachWithIndex { current_EmptyPickup, idx ->
				def defScacCode = current_Body.GeneralInformation?.SCACCode
				def defConvType = ''
				if (current_EmptyPickup.ISOSizeType && defScacCode && tpBizAgreementDisplayName) {
					defConvType = util.getConversionByTpIdDirFmtScac(tpBizAgreementDisplayName, "O", "X.12", "ContainerType", defScacCode, current_EmptyPickup.ISOSizeType, conn)
				}
				Y4Data y4 = new Y4Data()
				y4.E373_03 = util.convertXmlDateTime(current_EmptyPickup.MvmtDT?.LocDT?.LocDT, yyyyMMdd)
				y4.numberOfContainer = current_EmptyPickup.NumberOfContainers
				y4.E24_06 = defConvType
				if (current_EmptyPickup.Address?.LocationCode?.UNLocationCode) {
					y4.E309_08 = 'UN'
					y4.E310_09 = util.substring(current_EmptyPickup.Address?.LocationCode?.UNLocationCode, 1, 30)
				} else if (current_EmptyPickup.Facility?.FacilityCode) {
					y4.E309_08 = 'ZZ'
					y4.E310_09 = util.substring(current_EmptyPickup.Facility?.FacilityCode, 1, 5)
				} else if (current_EmptyPickup.Address?.City) {
					y4.E309_08 = 'CI'
					y4.E310_09 = util.substring(current_EmptyPickup.Address?.City, 1, 30)
				}
				
				if (y4.numberOfContainer) {
					String keyStr = y4.toString()
					if (defEmptyPickupGroups.containsKey(keyStr)) {
						Y4Data existsData = defEmptyPickupGroups.get(keyStr)
						existsData.numberOfContainer = Integer.valueOf(existsData.numberOfContainer) + Integer.valueOf(y4.numberOfContainer)
					} else {
						defEmptyPickupGroups.put(keyStr, y4)
					}
				}
			}
			
			int countOfLoopEmptyContainer = 0
			defEmptyPickupGroups.each { current_EmptyCntrKey, current_EmptyPickup ->
				countOfLoopEmptyContainer ++
				//only the 9 loops
				if (countOfLoopEmptyContainer > 9) {
					return
				}
				'Loop_Y4' {
					'Y4' {
						'E13_01' current_Body.GeneralInformation?.CarrierBookingNumber
						'E373_03' current_EmptyPickup.E373_03
						'E95_05' current_EmptyPickup.numberOfContainer
						
						'E24_06' current_EmptyPickup.E24_06
						
						'E309_08' current_EmptyPickup.E309_08
						'E310_09' current_EmptyPickup.E310_09
						
						def defOb = current_Body.GeneralInformation?.TrafficMode?.OutBound
						def defIb = current_Body.GeneralInformation?.TrafficMode?.InBound
						def def56 = ''
						if(defOb == 'FCL' && defIb == 'FCL') {
							def56 = 'HH'
						} else if(defOb == 'FCL' && defIb == 'LCL') {
							def56 = 'HP'
						} else if(defOb == 'LCL' && defIb == 'FCL') {
							def56 = 'PH'
						}  else if(defOb == 'LCL' && defIb == 'LCL') {
							def56 = 'PP'
						}
						'E56_10' def56
					}
					
					//W09
					ReeferCargoSpec reeferCargo = current_Body.Cargo?.find{it?.ReeferCargoSpec && it?.ReeferCargoSpec?.size()>0}?.ReeferCargoSpec?.first()
					
					if (reeferCargo) {
						'W09' {
							'E40_01' 'CZ'
							'E408_02' bcUtil.getGTNTemperature(reeferCargo.Temperature?.Temperature)
							
							def defTempUint = ''
							if (reeferCargo.Temperature?.TemperatureUnit == 'C') {
								defTempUint = 'CE'
							} else if (reeferCargo.Temperature?.TemperatureUnit == 'F') {
								defTempUint = 'FA'
							}
							'E355_03' defTempUint
							
							'E488_08' reeferCargo.DehumidityPercentage
							'E380_09' reeferCargo.Ventilation?.Ventilation
						}
					}
				}
			}
			
			
			//// N9
			//LoopReferenceAndFilter
			if (current_Body.GeneralInformation?.CarrierBookingNumber) {
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(current_Body.GeneralInformation?.CarrierBookingNumber, 1, 30)
				}
				// different with STD1, no output BM segment here
//				'N9' {
//					'E128_01' 'BM'
//					'E127_02' util.substring(current_Body.GeneralInformation?.CarrierBookingNumber, 1, 30)
//				}
			}
			
			Map<String,String> referenceTypeMap = ['BL':'BL', 'CR':'CR', 'FR':'FN', 'ICR':'CR', 'INV':'SO', 'PO':'CG', 'SR':'SI']
			current_Body.ExternalReference?.each { current_ExternalReference ->
				if (current_ExternalReference.CSReferenceType && current_ExternalReference.ReferenceNumber) {
					if (referenceTypeMap.get(current_ExternalReference.CSReferenceType)) {
						'N9' {
							'E128_01' referenceTypeMap.get(current_ExternalReference.CSReferenceType)
							'E127_02' util.substring(current_ExternalReference.ReferenceNumber, 1, 30)
						}
					}
					if (current_ExternalReference.ReferenceDescription == 'Customer Reference Number' &&  current_ExternalReference.CSReferenceType != 'CR') {
						'N9' {
							'E128_01' 'CR'
							'E127_02' util.substring(current_ExternalReference.ReferenceNumber, 1, 30)
						}
					}
					if (current_ExternalReference.ReferenceDescription?.toUpperCase() == 'PURCHASE ORDER NUMBER') {
						'N9' {
							'E128_01' 'PO'
							'E127_02' util.substring(current_ExternalReference.ReferenceNumber, 1, 30)
						}
					}
				}
			}
			
			current_Body.CarrierRate?.each { current_CarrierRate ->
				if (current_CarrierRate?.CSCarrierRateType && current_CarrierRate?.CarrierRateNumber) {
					if (current_CarrierRate.CSCarrierRateType == 'SC') {
						'N9' {
							'E128_01' 'KL'
							'E127_02' util.substring(current_CarrierRate.CarrierRateNumber, 1, 30)
						}
					}
					if (current_CarrierRate.CSCarrierRateType == 'TARIF') {
						'N9' {
							'E128_01' 'OU'
							'E127_02' util.substring(current_CarrierRate.CarrierRateNumber, 1, 30)
						}
					}
				}
			}
			
			def defPartyShp = current_Body.Party?.find{it.PartyType == 'SHP'}
			if (defPartyShp && defPartyShp.CarrierCustomerCode) {
				'N9' {
					'E128_01' '4F'
					'E127_02' util.substring(defPartyShp.CarrierCustomerCode, 1, 30)
				}
			}
			
			def defSvvd = ''
			if (current_Body.Route?.OceanLeg && current_Body.Route?.OceanLeg?.size()>0) {
				defSvvd = current_Body.Route?.OceanLeg[0]?.SVVD?.Discharge?.Service
			}
			if (defSvvd) {
				'N9' {
					'E128_01' 'RU'
					'E127_02' util.substring(defSvvd, 1, 30)
				}
			}
			
			if (! current_Body.GeneralInformation?.CarrierBookingNumber?.endsWith("0")) {
				def defSysExtRef = current_Body.ExternalReference?.find{it.ReferenceDescription?.toUpperCase() == 'SYSTEM REFERENCE NUMBER'}
				def val = ""
				if (defSysExtRef) {
					val = defSysExtRef.ReferenceNumber
				} else {
					val = current_Body.GeneralInformation?.CarrierBookingNumber
				}
				'N9' {
					'E128_01' 'SS'
					'E127_02' util.substring(val, 1, 30)
				}
			}
			
			//def defPartyCcp = current_Body.Party?.find{it.PartyType == 'CCP'}
			current_Body.Party?.findAll{it.PartyType == 'CCP' && it.CarrierCustomerCode}?.each { current_CCP ->
				'N9' {
					'E128_01' '4F'
					'E127_02' util.substring(current_CCP.CarrierCustomerCode, 1, 30)
				}
			}
			
			//// end of LoopReferenceAndFilter
			
			
			Map N1Map = ['BPT':'R6', 'CGN':'CN', 'FWD':'FW', 'SHP':'SH']
			Map countryMap = util.getCountryMap(conn)
			
			current_Body.Party?.each { current_Party ->
				if (N1Map.get(current_Party.PartyType) && current_Party.PartyName) {
					//looptype=loop_N1
					'Loop_N1' {
						'N1' {
							'E98_01' N1Map.get(current_Party.PartyType)
							'E93_02' util.substring(current_Party.PartyName?.trim(), 1, 60)?.trim()
							if (current_Party.CarrierCustomerCode) {
								'E66_03' '25'
							}
							'E67_04' current_Party.CarrierCustomerCode?.trim()
							'E706_05' ''
							'E98_06' ''
						}
						'N2' {
							'E93_01' ''
							'E93_02' ''
						}
						if (current_Party.Address?.AddressLines) {
							def addressLineToSplit = ''
							for (i in 0..4) {
								if(current_Party.Address?.AddressLines?.AddressLine[i]) {
									addressLineToSplit = addressLineToSplit + current_Party.Address?.AddressLines?.AddressLine[i]?.trim()
									if (i!=4) {
										addressLineToSplit = addressLineToSplit + " "
									}
								}
							}
							//loop1
							'N3' {
								'E166_01' util.substring(addressLineToSplit, 1, 55)?.trim()
								if (util.substring(addressLineToSplit, 56, 55)) {
									'E166_02' util.substring(addressLineToSplit, 56, 55)?.trim()
								}
							}
							//loop2
							if (addressLineToSplit.length() > 111) {
								'N3' {
									'E166_01' util.substring(addressLineToSplit, 111, 55)?.trim()
									if (addressLineToSplit.length() > 166) {
										'E166_02' util.substring(addressLineToSplit, 166, 55)?.trim()
									}
								}
							}
						}
						
						'N4' {
							'E19_01' util.substring(current_Party.Address?.City, 1, 30)
							//cannot use common city query as the state_cde index use in below sql 
							'E156_02' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Party.Address?.State, current_Party.Address?.Country, conn)
							'E116_03' current_Party.Address?.PostalCode
							if (current_Party.Address?.Country?.length() < 4) {
								'E26_04' current_Party.Address?.Country
							} else {
								'E26_04' countryMap.get(current_Party.Address?.Country?.toUpperCase())
							}
							'E309_05' ''
							'E310_06' ''
						}
						
						def defFirstName = (current_Party.Contact?.FirstName==null?'':current_Party.Contact?.FirstName.trim())
						def defLastName = (current_Party.Contact?.LastName==null?'':current_Party.Contact?.LastName.trim())
						def defEmail = (current_Party.Contact?.ContactEmailAddress==null?'':current_Party.Contact?.ContactEmailAddress.trim())
						def defContact = ''
						if (defFirstName || defLastName) {
							defContact = util.substring(defFirstName + ' ' + defLastName, 1, 60)?.trim()
						} else if (defEmail.indexOf('@')>=0) {
							defContact = util.substring(defEmail, 1 , defEmail.indexOf('@'))?.trim()
						}
						
						def defContactType = 'IC'
						if (current_Party.PartyType == 'SHP') {
							defContactType = 'SH'
						}
						if (defEmail) {
							'G61' {
								'E366_01' defContactType
								'E93_02' defContact
								'E365_03' 'EM'
								'E364_04' util.substring(defEmail, 1, 80)?.trim()
								'E443_05' ''
							}
						}
						if (current_Party.Contact?.ContactPhone?.Number) {
							'G61' {
								'E366_01' defContactType
								'E93_02' defContact
								'E365_03' 'TE'
								'E364_04' util.substring(current_Party.Contact?.ContactPhone?.Number, 1, 80)?.trim()
								'E443_05' ''
							}
						}
					}
				}
			}
			
			
			//loopType = 'POR'
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if (current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDType
					}
					if (current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
					} else if (current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode
					}
					'E114_04' util.substring(current_Body.Route?.POR?.CityDetails?.City, 1, 24)
					'E26_05' current_Body.Route?.POR?.CSStandardCity?.CSCountryCode
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body.Route?.POR?.CityDetails?.State, current_Body.Route?.POR?.CSStandardCity?.CSCountryCode, conn)
				}
				if (util.isXmlDateTimeLaterThanNow(current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT)) {
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertXmlDateTime(current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT, HHmm)
					}
				}
			}
			
			//loopType = 'POL'
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if (current_Body.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (current_Body.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body.Route?.FirstPOL?.Port?.LocationCode?.SchedKDType
					}
					if (current_Body.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E310_03' current_Body.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode
					} else if (current_Body.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode) {
						'E310_03' current_Body.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode
					}
					'E114_04' util.substring(current_Body.Route?.FirstPOL?.Port?.City, 1, 24)
					'E26_05' current_Body.Route?.FirstPOL?.Port?.CSCountryCode
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body.Route?.FirstPOL?.Port?.State, current_Body.Route?.FirstPOL?.Port?.CSCountryCode, conn)
				}
				if (util.isXmlDateTimeLaterThanNow(current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT)) {
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertXmlDateTime(current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT, HHmm)
					}
				}
				def defDocDt = current_Body.DocumentInformation?.RequiredDocument?.find{it?.DocumentType=='Shipping Instruction/BL Master' && it?.DueDT?.LocDT?.LocDT}?.DueDT?.LocDT?.LocDT
				if (defDocDt) {
					//POL_2
					'DTM' {
						'E374_01' '649'
						'E373_02' util.convertXmlDateTime(defDocDt, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(defDocDt, HHmm)
					}
				}
			}
			
			//loopType = 'POD'
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if (current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (current_Body.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body.Route?.LastPOD?.Port?.LocationCode?.SchedKDType
					}
					if (current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
						'E310_03' current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode
					} else if (current_Body.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) {
						'E310_03' current_Body.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode
					}
					'E114_04' util.substring(current_Body.Route?.LastPOD?.Port?.City, 1, 24)
					'E26_05' current_Body.Route?.LastPOD?.Port?.CSCountryCode
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body.Route?.LastPOD?.Port?.State, current_Body.Route?.LastPOD?.Port?.CSCountryCode, conn)
				}
				if (util.isXmlDateTimeLaterThanNow(current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT)) {
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertXmlDateTime(current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT, HHmm)
					}
				}
			}
			
			//loopType = 'FND'
			'Loop_R4' {
				'R4' {
					'E115_01' 'E'
					if (current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDType
					}
					if (current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
					} else if (current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode
					}
					'E114_04' util.substring(current_Body?.Route?.FND?.CityDetails?.City, 1, 24)
					'E26_05' current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body.Route?.FND?.CityDetails?.State, current_Body.Route?.FND?.CSStandardCity?.CSCountryCode, conn)
				}
				def defFndDt = ''
				if (current_Body.Route?.ArrivalAtFinalHub && current_Body.Route?.ArrivalAtFinalHub?.size()>0) {
					defFndDt = current_Body.Route?.ArrivalAtFinalHub[0]?.LocDT?.LocDT
				}
				if (! defFndDt) {
					defFndDt = current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT
				}
				if (defFndDt && util.isXmlDateTimeLaterThanNow(defFndDt)) {
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertXmlDateTime(defFndDt, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(defFndDt, HHmm)
					}
				}
			}
			
			//loopType = 'Trans'
			if (current_Body.Route?.OceanLeg && current_Body.Route.OceanLeg.size()>1 && current_Body.Route.OceanLeg[-1].POL?.Port?.City) {
				'Loop_R4' {
					'R4' {
						'E115_01' 'Y'
						if (current_Body.Route?.OceanLeg[-1]?.POL?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (current_Body.Route?.OceanLeg[-1]?.POL?.Port?.LocationCode?.SchedKDCode) {
							'E309_02' current_Body.Route?.OceanLeg[-1]?.POL?.Port?.LocationCode?.SchedKDType
						}
						if (current_Body.Route?.OceanLeg[-1]?.POL?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' current_Body.Route?.OceanLeg[-1]?.POL?.Port?.LocationCode?.UNLocationCode
						} else if (current_Body.Route?.OceanLeg[-1]?.POL?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' current_Body.Route?.OceanLeg[-1]?.POL?.Port?.LocationCode?.SchedKDCode
						}
						'E114_04' util.substring(current_Body.Route?.OceanLeg[-1]?.POL?.Port?.City, 1, 24)
						'E26_05' current_Body.Route?.OceanLeg[-1]?.POL?.Port?.CSCountryCode
						'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body.Route?.OceanLeg[-1]?.POL?.Port?.State, current_Body.Route?.OceanLeg[-1]?.POL?.Port?.CSCountryCode, conn)
					}
					def defDt = ''
					if (current_Body.Route?.OceanLeg[-1]?.DepartureDT && current_Body.Route?.OceanLeg[-1]?.DepartureDT?.size()>0 && current_Body.Route?.OceanLeg[-1]?.DepartureDT[0]?.LocDT?.LocDT) {
						defDt = current_Body.Route?.OceanLeg[-1]?.DepartureDT[0]?.LocDT?.LocDT
					}
					if (defDt && util.isXmlDateTimeLaterThanNow(defDt)) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertXmlDateTime(defDt, yyyyMMdd)
							'E337_03' util.convertXmlDateTime(defDt, HHmm)
						}
					}
				}
			}
			
			current_Body.Cargo?.eachWithIndex{ current_Cargo, index ->
				'Loop_LX' {
					'LX' {
						'E554_01' index + 1
					}
					if (current_Body.GeneralInformation?.BookingStatus == 'Cancelled' || current_Body.GeneralInformation?.BookingStatus == 'Rejected') {
						'K1' {
							'E61_01' 'BOOKING REQUEST CANCELLED'
							'E61_02' ''
						}
					} else if (current_Body.GeneralInformation?.BookingStatus == 'Pending' && index==0) {
						String remarkLine = current_Body.RemarkLines?.find{it.attr_RemarkType=='EDI Bkg Confirmation Remark'}
						if (remarkLine) {
							for (int i = 0; i*60 < remarkLine.length(); i++ ) {
								'K1' {
									'E61_01' util.substring(remarkLine, i*60 + 1, 30).trim()
									'E61_02' util.substring(remarkLine, i*60 + 31, 30).trim()
								}
							}
						}
					}
					'L0' {
						'E213_01' index + 1
						if (current_Cargo.GrossWeight?.Weight) {
							'E81_04' util.substring(current_Cargo.GrossWeight?.Weight?.replace("+", ""), 1, 14)
							'E187_05' 'G'
						}
						if (current_Cargo.Volume?.Volume) {
							if (current_Cargo.Volume.VolumeUnit?.toUpperCase()=='CBM' || current_Cargo.Volume.VolumeUnit?.toUpperCase()=='CBF') {
								'E183_06' current_Cargo.Volume.Volume
							}
							if (current_Cargo.Volume.VolumeUnit?.toUpperCase()=='CBM') {
								'E184_07' 'X'
							} else if(current_Cargo.Volume.VolumeUnit?.toUpperCase()=='CBF') {
								'E184_07' 'E'
							}
						}
						def defPkgType = current_Cargo.Packaging?.PackageType
						if (defPkgType) {
							if (current_Cargo.Packaging?.PackageQty && Double.valueOf(current_Cargo.Packaging?.PackageQty) > 0) {
								'E80_08' current_Cargo.Packaging?.PackageQty
								
								def defPackageTypeConvert = util.getEDICdeReffromIntCde(TP_ID, 'BookingConfirmation', DIR_ID, 'BCCS2X', 'BookingConfirmation_Loop@LX_L0', 'L009', defPkgType, conn)
								'E211_09' defPackageTypeConvert
							}
						}
						'E458_10' ''
						if (current_Cargo.GrossWeight?.WeightUnit?.toUpperCase()?.startsWith("K")) {
							'E188_11' 'K'
						} else if (current_Cargo.GrossWeight?.WeightUnit?.toUpperCase()?.startsWith("L")) {
							'E188_11' 'L'
						}
						'E56_12' ''
						'E380_13' ''
						'E211_14' ''
						'E1073_15' ''
					}
					'L5' {
						'E213_01' ''
						if (current_Cargo.cargoDescription) {
							'E79_02' util.substring(current_Cargo.cargoDescription, 1, 50)
						}
						'E22_03' ''
						'E23_04' ''
						'E103_05' ''
						'E87_06' ''
						'E88_07' ''
						'E23_08' ''
						'E22_09' ''
						'E595_10' ''
					}
					
					DecimalFormat decimalFormatter = new DecimalFormat("#.####")
					DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")
					
					if (current_Cargo.AWCargoSpec && current_Cargo.AWCargoSpec?.size()>0) {
						'L4' {
							if (current_Cargo.AWCargoSpec[0]?.Length?.Length) {
								Double  lenth = Double.parseDouble(current_Cargo.AWCargoSpec[0]?.Length?.Length)
								if (current_Cargo.AWCargoSpec[0]?.Length?.LengthUnit?.toUpperCase()?.startsWith('F')) {
									'E82_01' decimalFormatter.format(lenth)
								} else if (current_Cargo.AWCargoSpec[0]?.Length?.LengthUnit?.toUpperCase()?.startsWith('M')) {
									//lenth.multiply(v03048).setScale(4, BigDecimal.ROUND_HALF_UP)
									'E82_01' decimalFormatter.format(lenth * 0.3048)
								} else {
									'E82_01' decimalFormatter.format(lenth)
								}
							}
							if (current_Cargo.AWCargoSpec[0]?.Width?.Length) {
								Double  lenth = Double.parseDouble(current_Cargo.AWCargoSpec[0]?.Width?.Length)
								if (current_Cargo.AWCargoSpec[0]?.Width?.LengthUnit?.toUpperCase()?.startsWith('F')) {
									'E189_02' decimalFormatter.format(lenth)
								} else if (current_Cargo.AWCargoSpec[0]?.Width?.LengthUnit?.toUpperCase()?.startsWith('M')) {
									'E189_02' decimalFormatter.format(lenth * 0.3048)
								} else {
									'E189_02' decimalFormatter.format(lenth)
								}
							}
							if (current_Cargo.AWCargoSpec[0]?.Height?.Length) {
								Double  lenth = Double.parseDouble(current_Cargo.AWCargoSpec[0]?.Height?.Length)
								if (current_Cargo.AWCargoSpec[0]?.Height?.LengthUnit?.toUpperCase()?.startsWith('F')) {
									'E65_03' decimalFormatter.format(lenth)
								} else if (current_Cargo.AWCargoSpec[0]?.Height?.LengthUnit?.toUpperCase()?.startsWith('M')) {
									'E65_03' decimalFormatter.format(lenth * 0.3048)
								} else {
									'E65_03' decimalFormatter.format(lenth)
								}
							}
							if (current_Cargo.AWCargoSpec[0]?.Height?.LengthUnit == 'F' && current_Cargo.AWCargoSpec[0]?.Width?.LengthUnit == 'F' && current_Cargo.AWCargoSpec[0]?.Length?.LengthUnit == 'F') {
								'E90_04' 'F'
							} else {
								'E90_04' 'M'
							}
							'E380_05' ''
							'E1271_06' ''
						}
					}
			
					current_Cargo.DGCargoSpec?.eachWithIndex { current_DGCargoSpec, current_DGCargoSpecIndex ->
						//only loop max 10 times
						if (current_DGCargoSpecIndex > 9) {
							return
						}
						'Loop_H1' {
							'H1' {
								'E62_01' current_DGCargoSpec.UNNumber
								'E209_02' util.substring(current_DGCargoSpec.IMOClass, 1, 4)
								'E208_03' current_DGCargoSpec.DGRegulator=='IMD'?'I':''
								'E64_04' util.substring(current_DGCargoSpec.ProperShippingName?.trim(), 1, 30)?.trim()
								'E63_05' util.substring(current_DGCargoSpec.EmergencyContact[0]?.FirstName?.trim(), 1, 24)?.trim()
								'E200_06' ''
								'E77_07' current_DGCargoSpec.FlashPoint?.Temperature
								if (current_DGCargoSpec.FlashPoint?.TemperatureUnit=='C') {
									'E355_08' 'CE'
								} else if (current_DGCargoSpec.FlashPoint?.TemperatureUnit=='F') {
									'E355_08' 'FA'
								}
								//MapDGPackageGroupCode
								if (current_DGCargoSpec.PackageGroup?.Code?.toUpperCase() in ['I', 'II', 'III']) {
									'E254_09' current_DGCargoSpec.PackageGroup?.Code
								}
							}
							def defName = current_DGCargoSpec.TechnicalName?.trim()
							def defNameS1 = util.substring(defName, 1, 30)
							if (defNameS1) {
								'H2' {
									if (defNameS1.length()==1) {
										'E64_01' defNameS1 + "\\"
									} else {
										'E64_01' defNameS1
									}
									'E274_02' util.substring(defName, 31, 30)
								}
							}
							def defName61 = util.substring(defName, 61, 30)
							if (defName61) {
								'H2' {
									if (defName61.length()==1) {
										'E64_01' defName61 + "\\"
									} else {
										'E64_01' defName61
									}
									'E274_02' util.substring(defName, 91, 30)
								}
							}
							def defName121 = util.substring(defName, 121, 30)
							if (defName121) {
								'H2' {
									if (defName121.length()==1) {
										'E64_01' defName121 + "\\"
									} else {
										'E64_01' defName121
									}
									'E274_02' util.substring(defName, 151, 30)
								}
							}
							def defProperShippingName = current_DGCargoSpec.ProperShippingName?.trim()
							def defProperShippingNameS1 = util.substring(defProperShippingName, 31, 30)
							if (defProperShippingNameS1) {
								'H2' {
									if (defProperShippingNameS1.length()==1) {
										'E64_01' defProperShippingNameS1 + "\\"
									} else {
										'E64_01' defProperShippingNameS1
									}
									'E274_02' util.substring(defProperShippingName, 61, 20)
								}
							}
						}
					}
					//end of Loop H1
				}
			}
			
			
			if (current_Body.Route?.OceanLeg && current_Body.Route?.OceanLeg?.size()>0) {
				def def597 = util.substring(current_Body.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber, 1, 8)
				if (! def597) {
					def597 = util.substring(current_Body.Route?.OceanLeg[0]?.SVVD?.Loading?.Vessel, 1, 8)
				}
				
				def defE55 = ''
				def defExtVoyage = current_Body.Route?.OceanLeg[0]?.LoadingExtVoyage
				defExtVoyage = (defExtVoyage==null?"":defExtVoyage)
				if (defExtVoyage) {
					defE55 = util.substring(defExtVoyage, 1, 10)
				} else if (current_Body.Route?.OceanLeg[0]?.SVVD?.Loading?.Direction) {
					def defVoyage = current_Body.Route?.OceanLeg[0]?.SVVD?.Loading?.Voyage
					defVoyage = defVoyage==null?"":defVoyage
					defE55 = util.substring(defVoyage + current_Body.Route?.OceanLeg[0]?.SVVD?.Loading?.Direction, 1, 10)
				}
				
				if (def597 || defE55) {
					'V1' {
						'E597_01' def597
						if (current_Body.Route?.OceanLeg[0].SVVD?.Loading?.VesselName) {
							'E182_02' util.substring(current_Body.Route?.OceanLeg[0]?.SVVD?.Loading?.VesselName, 1, 28)
						} else if (current_Body.Route?.OceanLeg[0].SVVD?.Discharge?.VesselName) {
							'E182_02' util.substring(current_Body.Route?.OceanLeg[0]?.SVVD?.Discharge?.VesselName, 1, 28)
						}
						def defCountryCde = ''
						if (current_Body.Route?.OceanLeg[0]?.LoadingVesselNationalityCode) {
							 defCountryCde = util.substring(current_Body.Route?.OceanLeg[0]?.LoadingVesselNationalityCode, 1, 3)
						} else if (current_Body.Route?.OceanLeg[0]?.SVVD?.Loading?.VesselNationality) {
							defCountryCde = util.getCS2MasterCity4CountryCodeByCountryName(current_Body.Route?.OceanLeg[0]?.SVVD?.Loading?.VesselNationality, conn)
						}
						'E26_03' defCountryCde
						'E55_04' defE55
						'E140_05' ''
						'E249_06' ''
						'E854_07' ''
						'E897_08' ''
						'E91_09' ''
					}
				}
			}
			
			if (current_Body.Route?.OceanLeg?.size()>1) {
				def def597 = util.substring(current_Body.Route?.OceanLeg[-1]?.SVVD?.Loading?.LloydsNumber, 1, 8)
				if (! def597) {
					def597 = util.substring(current_Body.Route?.OceanLeg[-1]?.SVVD?.Loading?.Vessel, 1, 8)
				}
				
				def outputE55 = ''
				def devVoyage = current_Body.Route?.OceanLeg[-1]?.LoadingExtVoyage
				devVoyage = (devVoyage==null?"":devVoyage.trim())
				if (devVoyage) {
					outputE55 = util.substring(devVoyage, 1, 10)
				} else if (current_Body.Route?.OceanLeg[-1]?.SVVD?.Loading?.Direction) {
					def defLoadingVoyage = current_Body.Route?.OceanLeg[-1]?.SVVD?.Loading?.Voyage
					if (defLoadingVoyage==null) {
						defLoadingVoyage = ""
					}
					outputE55 = util.substring(defLoadingVoyage + current_Body.Route?.OceanLeg[-1]?.SVVD?.Loading?.Direction, 1, 10)
				}
				
				if (def597 || outputE55) {
					'V1' {
						'E597_01' def597
						if (current_Body.Route?.OceanLeg[-1]?.SVVD?.Loading?.VesselName) {
							'E182_02' util.substring(current_Body.Route?.OceanLeg[-1]?.SVVD?.Loading?.VesselName, 1, 28)
						} else if (current_Body.Route?.OceanLeg[-1]?.SVVD?.Discharge?.VesselName) {
							'E182_02' util.substring(current_Body.Route?.OceanLeg[-1]?.SVVD?.Discharge?.VesselName, 1, 28)
						}
						
						def defCountryCde = ''
						if (current_Body.Route?.OceanLeg[-1]?.DischargeVesselNationalityCode) {
							defCountryCde = util.substring(current_Body.Route?.OceanLeg[-1]?.DischargeVesselNationalityCode, 1, 3)
						} else if (current_Body.Route?.OceanLeg[-1]?.SVVD?.Discharge?.VesselNationality) {
						   defCountryCde = util.getCS2MasterCity4CountryCodeByCountryName(current_Body.Route?.OceanLeg[-1]?.SVVD?.Discharge?.VesselNationality, conn)
						}
						'E26_03' defCountryCde
						'E55_04' outputE55
						'E140_05' ''
						'E249_06' ''
						'E854_07' ''
						'E897_08' ''
						'E91_09' ''
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
		 * Part II: get mapping runtime parameters
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
		//Parse the xmlBody to JavaBean
		XmlBeanParser parser = new XmlBeanParser()
		BookingConfirm bc = parser.xmlParser(inputXmlBody, BookingConfirm.class)

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
		def T301 = outXml.createNode('T301')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.

		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()
		def headerMsgDT = util.convertDateTime(bc.Header?.MsgDT?.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		def txnErrorKeys = []

		//Start mapping

		tpBizAgreementDisplayName = util.getBizAgreementDisplayName(TP_ID, MSG_TYPE_ID, conn)
		
		bc?.Body?.eachWithIndex{ current_Body, current_BodyIndex ->

			//associate container and cargo
			//			Map<cs.b2b.core.mapping.bean.bc.Container, List<cs.b2b.core.mapping.bean.bc.Cargo>> associateContainerAndCargo = bcUtil.associateContainerAndCargo(current_Body)

			//prep checking
			//			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)
			List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()

			//mapping
			if(current_Body?.GeneralInformation?.BookingStatus=='Declined') {
				//Declined mapping
				generateBody4RejectCase(current_Body, outXml)
			} else {
				generateBody(current_Body, outXml)
			}

			// posp checking
			if(errorKeyList.isEmpty()){
				pospValidation(current_Body, writer?.toString(), errorKeyList)
			}

			bcUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			bcUtil.buildBizKey(bizKeyXml, bc.Header, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, TP_ID, conn)

			txnErrorKeys.add(errorKeyList)
		}


		//End root node
		outXml.nodeCompleted(null,T301)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		bcUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		bcUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		bcUtil.promoteHeaderIntChgMsgId(appSessionId, bc.Header?.InterchangeMessageID);
		if (bc.Body && bc.Body.size()>0) {
			bcUtil.promoteScacCode(appSessionId, bc.Body[0].GeneralInformation?.SCACCode);
		}
		
		String result = '';
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			result = writer?.toString();
			result = util.cleanXml(result)
			println ('------ end with body ------ '+result.length())
		}

		writer.close();
		
		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bc.Body current_Body, int current_BodyIndex) {

		return [];
	}
	
	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList) {
		
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T301>")

		//B104StatusChecking
		bcUtil.checkB104Status(node, false, "MessageFunction are not Confirmed or Cancelled or Pending or Declined", errorKeyList)
		
		//R4YChecking
		bcUtil.checkR4Y(current_Body, node, true, null, errorKeyList)
		
		//Invalid ISOSizeType
		bcUtil.checkIsValidEmptyPickupISOSizeType(current_Body, tpBizAgreementDisplayName, true, null, errorKeyList, conn)

	}
	
	void generateBody4RejectCase(Body current_Body, MarkupBuilder outXml) {
		//GTNEXUS, only for Declined status
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '301'
				'E329_02' '-999'
			}
			'B1' {
				'E140_01' current_Body?.GeneralInformation?.SCACCode
				def def145 = ''
				def reference50 = current_Body?.ExternalReference?.find{it.CSReferenceType=='OTH' && it.referenceDescription=='50'}?.ReferenceNumber
				def assoReference50 = current_Body?.AssociatedExternalReference?.find{it.CSReferenceType=='OTH' && it.EDIReferenceType=='50'}?.ReferenceNumber
				//a.	ExternalReference/ReferenceNumber where CSReferenceType="OTH" and referenceDescription="50"
				if(reference50){
					def145 = reference50
				}
				//b.	AssociatedExternalReference/ReferenceNumber where CSReferenceType="OTH" and EDIReferenceType="50"
				else if(assoReference50){
					def145 = assoReference50
				}
				//c.	GeneralInformation/BookingStatusRemarks only if | exists  use the first element				
				else if (current_Body?.GeneralInformation?.BookingStatusRemarks?.indexOf('|') >= 0) {
					def145 = util.substring(current_Body?.GeneralInformation?.BookingStatusRemarks, 1, current_Body?.GeneralInformation?.BookingStatusRemarks.indexOf('|'))?.trim()
				}
				//d.	GeneralInformation/CarrierBookingNumber
				if(util.isEmpty(def145)) {
					def145 = current_Body.GeneralInformation.CarrierBookingNumber
				}
				'E145_02' util.substring(def145?.trim(), 1, 30)

				'E373_03' util.convertXmlDateTime(current_Body.GeneralInformation?.BookingStatusDT?.LocDT, yyyyMMdd)
				'E558_04' 'D'
			}
			'Y3' { 'E13_01' 'DECLINE' }
			if(current_Body?.GeneralInformation?.CarrierBookingNumber){
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(current_Body.GeneralInformation.CarrierBookingNumber, 1, 30)
				}
			}			
			'Loop_R4' {
				'R4' { 'E115_01' 'L' }
			}
			'Loop_LX' {
				'LX' { 'E554_01' '1' }
				def remarks = current_Body?.GeneralInformation?.BookingStatusRemarks
				if (remarks?.indexOf('|') >= 0) {					
					remarks = remarks.substring(remarks.indexOf('|')+1)
					if (remarks?.indexOf('|') >= 0) {
						remarks = remarks.substring(0, remarks.indexOf('|'))  //2ND element if | exists
					}
				}
				if (remarks) {
					remarks = remarks.trim()			
					'K1' {
						'E61_01' util.substring(remarks, 1, 30)
						'E61_02' util.substring(remarks, 31, 30)
					}					
				}
				else{
					'K1' {
						'E61_01' 'BOOKING REQUEST CANCELLED'
					}
				}
			}
			
			'SE' {
				'E96_01' '-999'
				'E329_02' '-999'
			}
		}
	}
	
	class Y4Data {
		String E373_03 = ""
		//container number
		String numberOfContainer = ""
		String E24_06 = ""
		String E309_08 = ""
		String E310_09 = ""
		
		String toString() {
			return "E373_03: "+E373_03+", E24_06: "+E24_06+", E309_08: "+E309_08+", E310_09: "+E310_09
		}
	}
}

