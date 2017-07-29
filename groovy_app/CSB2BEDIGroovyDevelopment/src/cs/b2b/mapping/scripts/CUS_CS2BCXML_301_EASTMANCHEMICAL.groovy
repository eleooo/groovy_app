package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.text.DecimalFormat

import cs.b2b.core.mapping.bean.bc.AWCargoSpec
import cs.b2b.core.mapping.bean.bc.Appointment
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.EmptyPickup
import cs.b2b.core.mapping.bean.bc.Facility
import cs.b2b.core.mapping.bean.bc.FullReturn
import cs.b2b.core.mapping.bean.bc.OceanLeg
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * @author RENGA
 * @change CSNAEDI6631 http://i2isd/sites/csisa/Lists/Workplan/DispForm.aspx?ID=29840
 */
public class CUS_CS2BCXML_301_EASTMANCHEMICAL {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_BC_O_Common bcUtil = new cs.b2b.core.mapping.util.MappingUtil_BC_O_Common(util);

	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	Connection conn = null

	java.util.Date currentDate = new java.util.Date()
	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = 'HHmm'

	DecimalFormat decimalFormatter = new DecimalFormat("#.####")
	DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")
	
	def currentSystemDt = null
	
	Map<String, String> bookingStatusMap = ["Confirmed":"A", "Pending":"B", "Wait Listed":"B", "Cancelled":"D", "No Show":"D", "Declined":"R", "Rejected":"R"]

//	public static void main(String[] args) {
//		try {
//			String basicTestingFileName = "./demo/BC_EASTMANCHEMICAL/testing_files/487-EASTMANCHEMICAL-baseline.xml"
//			XmlBeanParser parser = new XmlBeanParser()
//			String inputXmlBody = LocalFileUtil.readBigFileContentDirectly(basicTestingFileName)
//			BookingConfirm bcfile = parser.xmlParser(inputXmlBody, BookingConfirm.class)
//			Body body = bcfile.Body.get(0)
//			
//			StringWriter writer = new StringWriter()
//			MarkupBuilder markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false))
//			
//			CUS_CS2BCXML_301_EASTMANCHEMICAL cls = new CUS_CS2BCXML_301_EASTMANCHEMICAL()
//			
//			ConnectionForTester testDBConn = new ConnectionForTester();
//			cls.conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
//			cls.TP_ID = "EASTMANCHEMICAL"
//			cls.MSG_TYPE_ID = "BC"
//			cls.DIR_ID = "O"
//			cls.MSG_REQ_ID = "msg-id-12312312345"
//			String ret = cls.generateBody(body, markupBuilder)
//			println ret
//		} catch (Exception ex) {
//			ex.printStackTrace()
//		}
//	}
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '301'
				'E329_02' '-999'
			}
			'B1' {
				'E140_01' current_Body?.GeneralInformation?.SCACCode
				
				def defSIDs = current_Body?.ExternalReference?.findAll{it.CSReferenceType == "SID"}
				def defSRs = current_Body?.AssociatedExternalReference?.findAll{it.CSReferenceType == "SR"}
				def defLastIDNum = ''
				if (!defSIDs.isEmpty()) {
					defLastIDNum = defSIDs[-1].ReferenceNumber
				} else if (!defSRs.isEmpty()) {
					defLastIDNum = defSRs[-1].ReferenceNumber
				}
				if (defLastIDNum) {
					'E145_02' util.substring(defLastIDNum.trim(), 1, 30)
				}
				'E373_03' util.convertDateTime(current_Body?.GeneralInformation?.BookingStatusDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
				'E558_04' bookingStatusMap.get(current_Body?.GeneralInformation?.BookingStatus)
				
			}
			
			'Y3' {
				def defCarBkgNum = ''
				if (current_Body?.GeneralInformation?.BookingStatus == "Declined") {
					defCarBkgNum = "DECLINE"
				}else if(current_Body?.GeneralInformation?.SCACCode && current_Body?.GeneralInformation?.CarrierBookingNumber) {
					defCarBkgNum = current_Body.GeneralInformation.CarrierBookingNumber
				}
				'E13_01' defCarBkgNum
				
				'E140_02' current_Body?.GeneralInformation?.SCACCode
				
				if(current_Body?.Route?.FirstPOL?.DepartureDT?.LocDT) {
					'E373_03' util.convertDateTime(current_Body.Route.FirstPOL.DepartureDT.LocDT, xmlDateTimeFormat, yyyyMMdd)
				}
				if(current_Body?.Route?.LastPOD?.ArrivalDT?.LocDT) {
					'E373_04' util.convertDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT, xmlDateTimeFormat, yyyyMMdd)
				}				
				if(current_Body?.Route?.FullReturnCutoff?.LocDT) {
					'E373_07' util.convertDateTime(current_Body.Route.FullReturnCutoff.LocDT, xmlDateTimeFormat, yyyyMMdd)
				}
				if(current_Body?.Route?.FullReturnCutoff?.LocDT) {
					'E337_08' util.convertDateTime(current_Body.Route.FullReturnCutoff.LocDT, xmlDateTimeFormat, HHmm)
				}
				
				def ob = ""
				def ib = ""
				if(current_Body?.Route?.Haulage?.OutBound == 'C') {
					ob = 'D'
				} else if (current_Body?.Route?.Haulage?.OutBound == 'M') {
					ob = 'P'
				}
				if(current_Body?.Route?.Haulage?.InBound == 'C') {
					ib = 'D'
				} else if (current_Body?.Route?.Haulage?.InBound == 'M') {
					ib = 'P'
				}
				if (ob && ib) {
					'E375_10' ob + ib
				}
			}
			
			//Y4 Loop
			//get looping of empty container
			List<EmptyPickup> emptyCntrList = current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup?.findAll{it.ISOSizeType && it.ISOSizeType?.trim() != ''}
			if (emptyCntrList==null || emptyCntrList.isEmpty()) {
				//if empty, then build emptyContainer from container loop
				def groupCntr = current_Body.ContainerGroup?.Container?.groupBy(){it.CarrCntrSizeType}
				groupCntr.each() { current_LoopCntr ->
					EmptyPickup ep = new EmptyPickup()
					ep.ISOSizeType = current_LoopCntr.key
					if (current_LoopCntr.key) {
						ep.NumberOfContainers = current_LoopCntr.value.size()
					} else {
						ep.NumberOfContainers = 0
					}
					ep.Facility = new Facility()
					ep.Facility.FacilityCode = 'dummy'
					ep.Facility.FacilityName = 'dummy'
					emptyCntrList.add(ep)
				}
			}
			
			emptyCntrList.eachWithIndex { current_EmptyPickup, idx ->
				//only the 9 loops
				if (idx > 9 || current_Body?.GeneralInformation?.BookingStatus == "Declined") {
					return
				}
				'Loop_Y4' {					
					'Y4' {						
						'E13_01' current_Body.GeneralInformation?.CarrierBookingNumber						
						'E373_03' util.convertDateTime(current_EmptyPickup.MvmtDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E95_05' current_EmptyPickup.NumberOfContainers
						
						def defConvType = ''
						if (current_EmptyPickup.ISOSizeType) {
							defConvType = util.getConversionByTpIdMsgTypeDirFmtScac(TP_ID, MSG_TYPE_ID, DIR_ID, "X.12", "ContainerType", "ALL", current_EmptyPickup.ISOSizeType, conn)
							if (util.isEmpty(defConvType)) {
								defConvType = current_EmptyPickup.ISOSizeType
							}
						}
						'E24_06' defConvType
						
						def defOb = ''
						def defIb = ''
						if (current_Body.ContainerGroup?.Container && current_Body.ContainerGroup?.Container.size()>0) {
							defOb = current_Body.ContainerGroup?.Container?.get(0)?.TrafficMode?.OutBound
							defIb = current_Body.ContainerGroup?.Container?.get(0)?.TrafficMode?.InBound 
						}
						def def56 = ''
						if(defOb == 'FCL' && defIb == 'FCL') {
							def56 = '03'
						} else if(defOb == 'FCL' && defIb == 'LCL') {
							def56 = '04'
						} else if(defOb == 'LCL' && defIb == 'FCL') {
							def56 = '05'
						}  else if(defOb == 'LCL' && defIb == 'LCL') {
							def56 = '02'
						}
						'E56_10' def56
					}
					
					//W09
					current_Body.Cargo.each { current_Cargo ->
						current_Cargo.ReeferCargoSpec.each { current_ReeferCargoSpec ->
							
						}
					}
					ReeferCargoSpec reeferCargoSpec = current_Body.Cargo?.find() {it.ReeferCargoSpec && it.ReeferCargoSpec.size()>0}?.ReeferCargoSpec?.get(0)
					if (reeferCargoSpec) {
						'W09' {
							'E40_01' 'CZ'
							if (reeferCargoSpec.Temperature?.Temperature) {
								BigDecimal  temperature = new BigDecimal(reeferCargoSpec.Temperature?.Temperature)
								'E408_02' temperature.setScale(0, BigDecimal.ROUND_HALF_UP)
							}
							
							def defTempUint = ''
							if(reeferCargoSpec.Temperature?.TemperatureUnit == 'C'){
								defTempUint = 'CE'
							} else if (reeferCargoSpec.Temperature?.TemperatureUnit == 'F') {
								defTempUint = 'FA'
							}
							'E355_03' defTempUint
							
							'E488_08' reeferCargoSpec.DehumidityPercentage
							'E380_09' reeferCargoSpec.Ventilation?.Ventilation
						}
					}
				}
			}
			
			//// N9
			//LoopCarrierBookingNumberConvertByDB
			if (current_Body.GeneralInformation?.CarrierBookingNumber) {
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(current_Body.GeneralInformation?.CarrierBookingNumber, 1, 30)
				}
			}
			
			//LoopExternalReferenceByDB
			Map<String,String> referenceTypeMap = ['BL':'BL','CT':'CT','FF':'ZZ']
			current_Body?.ExternalReference.each { current_ExternalReference ->
				if(referenceTypeMap.get(current_ExternalReference.CSReferenceType) && current_ExternalReference.ReferenceNumber) {
					'N9' {
						'E128_01' referenceTypeMap.get(current_ExternalReference.CSReferenceType)
						'E127_02' util.substring(current_ExternalReference.ReferenceNumber, 1, 30)
					}
				}
			}
			
			//LoopSVVDServiceSVC
			if (current_Body.Route?.FirstPOL?.OutboundSVVD?.Service) {
				'N9' {
					'E128_01' 'SVC'
					'E127_02' util.substring(current_Body.Route?.FirstPOL?.OutboundSVVD?.Service, 1, 30)
				}
			}
			
			//// N1
			//// LoopPartyAndFilter
			Map defN1Map = ['BPT':'BK', 'SHP':'SH', 'CGN':'CN', 'FWD':'FW']
			//MapPartyTypeConversionByDBLoop
			current_Body.Party.each { current_Party ->
				if (current_Party.PartyName && defN1Map.get(current_Party.PartyType)) {
					'Loop_N1' {
						'N1' {
							//MapPartyTypeConversionByDBLoop
							'E98_01' defN1Map.get(current_Party.PartyType)
							//MapPartyNameLoop
							'E93_02' util.substring(current_Party.PartyName, 1, 60)
							'E66_03' (current_Party.CarrierCustomerCode?"25":"")
							'E67_04' util.substring(current_Party.CarrierCustomerCode, 1, 60)
							'E706_05' ''
							'E98_06' ''
						}
						
						def defAddress = ''
						current_Party.Address?.AddressLines?.AddressLine.each { current_AddressLine ->
							if (current_AddressLine) {
								if (defAddress) {
									defAddress = defAddress + " " + current_AddressLine.trim()
								} else {
									defAddress = current_AddressLine.trim()
								}
							}
						}
						if (defAddress) {
							'N3' {
								//MapPartyAddress
								'E166_01' util.substring(defAddress, 1, 55)?.trim()
								//MapPartyAddressLeft
								'E166_02' util.substring(defAddress, 56, 55)?.trim()
							}
							if (defAddress.length() > 110) {
								'N3' {
									//MapPartyAddress
									'E166_01' util.substring(defAddress, 111, 55)?.trim()
									//MapPartyAddressLeft
									'E166_02' util.substring(defAddress, 166, 55)?.trim()
								}
							}
						}
						'N4' {
							'E19_01' util.substring(current_Party.Address?.City, 1, 30)
							'E156_02' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Party.Address?.State, current_Party.Address?.Country, conn)
							'E116_03' current_Party.Address?.PostalCode?.trim()
							if (current_Party.Address?.Country && current_Party.Address?.Country.length()<4) {
								'E26_04' current_Party.Address?.Country
							} else {
								'E26_04' util.getCS2MasterCity4CountryCodeByCountryName(current_Party.Address?.Country, conn)
							}
							'E309_05' ''
							'E310_06' ''
						}
						if (current_Party.Contact?.ContactEmailAddress || current_Party.Contact?.ContactPhone?.Number) {
							def defEName = ''
							if (current_Party.Contact?.FirstName || current_Party.Contact?.LastName) {
								defEName = (current_Party.Contact?.FirstName ? current_Party.Contact.FirstName.trim()+" ":"")
								defEName = defEName + (current_Party.Contact?.LastName ? current_Party.Contact.LastName.trim():"")
							} else if (current_Party.Contact?.ContactEmailAddress) {
								defEName = current_Party.Contact.ContactEmailAddress
								if (defEName.indexOf("@")>0) {
									defEName = defEName.substring(0, defEName.indexOf("@"))
								}
							}
							def defType = defN1Map.get(current_Party.PartyType)
							if (defType == 'BK') {
								defType = 'BP'
							}
							if (defEName) {
								if (current_Party.Contact?.ContactEmailAddress) {
									'G61' {
										'E366_01' defType
										'E93_02' util.substring(defEName, 1, 60)?.trim()
										'E365_03' 'EM'
										'E364_04' util.substring(current_Party.Contact?.ContactEmailAddress, 1, 80)?.trim()
										'E443_05' ''
									}
								}
								if (current_Party.Contact?.ContactPhone?.Number) {
									'G61' {
										'E366_01' defType
										if (defEName) {
											'E93_02' util.substring(defEName, 1, 60)?.trim()
										}
										'E365_03' 'TE'
										'E364_04' util.substring(current_Party.Contact?.ContactPhone?.Number, 1, 80)?.trim()
										'E443_05' ''
									}
								}
							}
						}
					}
				}
			}
			
			//// LoopAppointment1stDELIVERYand1stPICKUP
			List<Appointment> defAppointments = []
			def defAppointmentDelivery = current_Body.Appointment.find{it.Type == 'DELIVERY'}
			if (defAppointmentDelivery) {
				defAppointments.add(defAppointmentDelivery)
			}
			def defAppointmentPickup = current_Body.Appointment.find{it.Type == 'PICKUP'}
			if (defAppointmentPickup) {
				defAppointments.add(defAppointmentPickup)
			}			
			defAppointments.each { current_Appointment ->
				'Loop_N1' {
					'N1' {
						if (current_Appointment.Type == 'DELIVERY') {
							'E98_01' 'ST'
						} else if (current_Appointment.Type == 'PICKUP') {
							'E98_01' 'SF'
						} 
						'E93_02' util.substring(current_Appointment.Type, 1, 60)
					}
					
					def defAddress = ''
					current_Appointment.Address?.AddressLines?.AddressLine.each { current_AddressLine ->
						if (current_AddressLine) {
							if (defAddress) {
								defAddress = defAddress + " " + current_AddressLine.trim()
							} else {
								defAddress = current_AddressLine.trim()
							}
						}
					}
					if (defAddress) {
						'N3' {
							//MapPartyAddress
							'E166_01' util.substring(defAddress, 1, 55)?.trim()
							//MapPartyAddressLeft
							'E166_02' util.substring(defAddress, 56, 55)?.trim()
						}
						if (defAddress.length() > 110) {
							'N3' {
								//MapPartyAddress
								'E166_01' util.substring(defAddress, 111, 55)?.trim()
								//MapPartyAddressLeft
								'E166_02' util.substring(defAddress, 166, 55)?.trim()
							}
						}
					}
					
					'N4' {
						'E19_01' util.substring(current_Appointment.Address?.City, 1, 30)
						'E156_02' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Appointment.Address?.State, current_Appointment.Address?.Country, conn)
						if (current_Appointment.Address?.PostalCode) {
							'E116_03' current_Appointment.Address?.PostalCode?.trim()
						}
						if (current_Appointment.Address?.Country) {
							if (current_Appointment.Address?.Country.length()<4) {
								'E26_04' current_Appointment.Address?.Country
							} else {
								'E26_04' util.getCS2MasterCity4CountryCodeByCountryName(current_Appointment.Address?.Country, conn)
							}
						}
					}
					
					if (current_Appointment.Contact?.ContactEmailAddress || current_Appointment.Contact?.ContactPhone?.Number) {
						def defEName = ''
						if (current_Appointment.Contact?.FirstName || current_Appointment.Contact?.LastName) {
							defEName = (current_Appointment.Contact?.FirstName ? current_Appointment.Contact.FirstName.trim()+" ":"")
							defEName = defEName + (current_Appointment.Contact?.LastName ? current_Appointment.Contact.LastName.trim():"")
						} else if (current_Appointment.Contact?.ContactEmailAddress) {
							defEName = current_Appointment.Contact.ContactEmailAddress
							if (defEName.indexOf("@")>0) {
								defEName = defEName.substring(0, defEName.indexOf("@"))
							}
						}
						
						if (defEName) {
							if (current_Appointment.Contact?.ContactPhone?.Number) {
								'G61' {
									'E366_01' 'IC'
									'E93_02' util.substring(defEName, 1, 60)?.trim()
									'E365_03' 'TE'
									'E364_04' util.substring(current_Appointment.Contact?.ContactPhone?.Number, 1, 80)?.trim()
									'E443_05' ''
								}
							}
							if (current_Appointment.Contact?.ContactEmailAddress) {
								'G61' {
									'E366_01' 'IC'
									'E93_02' util.substring(defEName, 1, 60)?.trim()
									'E365_03' 'EM'
									'E364_04' util.substring(current_Appointment.Contact?.ContactEmailAddress, 1, 80)?.trim()
									'E443_05' ''
								}
							}
						}
					}
				}
			}
			

			//Loop1stEmptyPickUp
			//Body-BookingConfirm/pfx:ContainerGroup-Body-BookingConfirm/pfx:ContainerFlowInstruction-ContainerGroup-Body-BookingConfirm/pfx:EmptyPickup-ContainerFlowInstruction-ContainerGroup-Body-BookingConfirm[position()<=1 and pfx:Facility-BookingCntrMvmtType/ns:FacilityName-SimpleFacilityType!='']
			//Empty Pickup
			EmptyPickup defN1EmptyPickup = null
			if (current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup && current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup.size()>0) {
				defN1EmptyPickup = current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup?.get(0)
			}
			if (defN1EmptyPickup && defN1EmptyPickup.Facility?.FacilityName && defN1EmptyPickup.Address?.AddressLines?.AddressLine?.size()>0) {
				'Loop_N1' {
					'N1' {
						'E98_01' 'CL'
						if (defN1EmptyPickup.Address?.AddressLines?.AddressLine) {
							'E93_02' util.substring(defN1EmptyPickup.Address?.AddressLines?.AddressLine[0], 1, 60)?.trim()
						}
					}
					if (defN1EmptyPickup.Address?.AddressLines?.AddressLine?.size()>1) {
						'N3' {
							'E166_01' util.substring(defN1EmptyPickup.Address?.AddressLines?.AddressLine[1], 1, 55)?.trim()
							if (defN1EmptyPickup.Address?.AddressLines?.AddressLine?.size()>2) {
								'E166_02' util.substring(defN1EmptyPickup.Address?.AddressLines?.AddressLine[2], 1, 55)?.trim()
							}
						}
					}
					if (defN1EmptyPickup.Address?.AddressLines?.AddressLine?.size()>3) {
						'N3' {
							'E166_01' util.substring(defN1EmptyPickup.Address?.AddressLines?.AddressLine[3], 1, 55)?.trim()
							if (defN1EmptyPickup.Address?.AddressLines?.AddressLine?.size()>4) {
								'E166_02' util.substring(defN1EmptyPickup.Address?.AddressLines?.AddressLine[4], 1, 55)?.trim()
							}
						}
					}
					'N4' {
						'E19_01' util.substring(defN1EmptyPickup.Address?.City, 1, 30)
						if (defN1EmptyPickup.Address?.State) {
							def defCity = util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(defN1EmptyPickup.Address?.State, defN1EmptyPickup.Address?.Country, conn)
							if (defCity) {
								'E156_02' defCity
							} else {
								'E156_02' util.substring(defN1EmptyPickup.Address?.State.trim().toUpperCase(), 1, 2)
							}
						}
						'E116_03' defN1EmptyPickup.Address?.PostalCode?.trim()
						if (defN1EmptyPickup.Address?.Country) {
							if (defN1EmptyPickup.Address?.Country.length()<4) {
								'E26_04' defN1EmptyPickup.Address?.Country
							} else {
								'E26_04' util.getCS2MasterCity4CountryCodeByCountryName(defN1EmptyPickup.Address?.Country, conn)
							}
						}
					}
				}
			}
			
			//Full Return
			//Loop1stFullReturn
			//Body-BookingConfirm/pfx:ContainerGroup-Body-BookingConfirm/pfx:ContainerFlowInstruction-ContainerGroup-Body-BookingConfirm/pfx:FullReturn-ContainerFlowInstruction-ContainerGroup-Body-BookingConfirm
			
			FullReturn defN1FullReturn = null
			if (current_Body.ContainerGroup?.ContainerFlowInstruction?.FullReturn && current_Body.ContainerGroup?.ContainerFlowInstruction?.FullReturn.size()>0) {
				defN1FullReturn = current_Body.ContainerGroup?.ContainerFlowInstruction?.FullReturn?.get(0)
			}
			if (defN1FullReturn && defN1FullReturn.Facility?.FacilityName && defN1FullReturn.Address?.AddressLines?.AddressLine?.size()>0) {
				'Loop_N1' {
					'N1' {
						'E98_01' 'TR'
						if (defN1FullReturn.Address?.AddressLines?.AddressLine) {
							'E93_02' util.substring(defN1FullReturn.Address?.AddressLines?.AddressLine[0], 1, 60)
						}
					}
					if (defN1FullReturn.Address?.AddressLines?.AddressLine?.size()>1) {
						'N3' {
							'E166_01' util.substring(defN1FullReturn.Address?.AddressLines?.AddressLine[1], 1, 55)?.trim()
							if (defN1FullReturn.Address?.AddressLines?.AddressLine?.size()>2) {
								'E166_02' util.substring(defN1FullReturn.Address?.AddressLines?.AddressLine[2], 1, 55)?.trim()
							}
						}
					}
					if (defN1FullReturn.Address?.AddressLines?.AddressLine?.size()>3) {
						'N3' {
							'E166_01' util.substring(defN1FullReturn.Address?.AddressLines?.AddressLine[3], 1, 55)?.trim()
							if (defN1FullReturn.Address?.AddressLines?.AddressLine?.size()>4) {
								'E166_02' util.substring(defN1FullReturn.Address?.AddressLines?.AddressLine[4], 1, 55)?.trim()
							}
						}
					}
					'N4' {
						'E19_01' util.substring(defN1FullReturn.Address?.City, 1, 30)
						if (defN1FullReturn.Address?.State) {
							def defCity = util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(defN1FullReturn.Address?.State, defN1FullReturn.Address?.Country, conn)
							if (defCity) {
								'E156_02' defCity
							} else {
								'E156_02' util.substring(defN1FullReturn.Address?.State.trim().toUpperCase(), 1, 2)
							}
						}
						'E116_03' defN1FullReturn.Address?.PostalCode?.trim()
						if (defN1FullReturn.Address?.Country) {
							if (defN1FullReturn.Address?.Country.length()<4) {
								'E26_04' defN1FullReturn.Address?.Country
							} else {
								'E26_04' util.getCS2MasterCity4CountryCodeByCountryName(defN1FullReturn.Address?.Country, conn)
							}
						}
					}
				}
			}
			//Loop_N1 end
			
			//Loop R4
			//loopType = 'POR'
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body.Route.POR.CityDetails.LocationCode.SchedKDType
					}
					if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' current_Body.Route.POR.CityDetails.LocationCode.UNLocationCode
					} else if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' current_Body.Route.POR.CityDetails.LocationCode.SchedKDCode
					}
					if (current_Body?.Route?.POR?.CityDetails?.City) {
						'E114_04' util.substring(current_Body?.Route?.POR?.CityDetails?.City.trim(), 1, 24)
					}
					if (current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode) {
						'E26_05' current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode
					}
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.POR?.CityDetails?.State, current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode, conn)
				}
				if (current_Body?.Route?.FullReturnCutoff?.GMT) {
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(current_Body.Route.FullReturnCutoff.GMT);
						'E374_01' date.after(currentDate) ? '139' : '140'
						'E373_02' util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.GMT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.GMT, HHmm)
					}
				}
			}
			
			//each OceanLeg create a pair of POL POD
			boolean SIAndVGMCutOffDTOutputFlag = true
			current_Body.Route?.OceanLeg?.eachWithIndex { current_OceanLeg, current_Index ->

				if(StringUtil.isEmpty(current_OceanLeg?.SVVD?.CarriageType) || current_OceanLeg?.SVVD?.CarriageType == 'MA'){ //CSNAEDI6631

					//OceanLeg - POL
					'Loop_R4' {
						'R4' {
							'E115_01' 'L'
							if (current_OceanLeg.POL?.Port?.LocationCode?.UNLocationCode ) {
								'E309_02' 'UN'
							} else if (current_OceanLeg.POL?.Port?.LocationCode?.SchedKDCode) {
								'E309_02' current_OceanLeg.POL?.Port?.LocationCode?.SchedKDType
							}
							if (current_OceanLeg.POL?.Port?.LocationCode?.UNLocationCode) {
								'E310_03' current_OceanLeg.POL?.Port?.LocationCode?.UNLocationCode
							} else if (current_OceanLeg.POL?.Port?.LocationCode?.SchedKDCode) {
								'E310_03' current_OceanLeg.POL?.Port?.LocationCode?.SchedKDCode
							}
							if (current_OceanLeg.POL?.Port?.City) {
								'E114_04' util.substring(current_OceanLeg.POL?.Port?.City.trim(), 1, 24)
							}
							if (current_OceanLeg.POL?.Port?.CSCountryCode) {
								'E26_05' current_OceanLeg.POL?.Port?.CSCountryCode
							}
							'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_OceanLeg.POL?.Port?.State, current_OceanLeg.POL?.Port?.CSCountryCode, conn)
						}

						if (current_OceanLeg.DepartureDT && current_OceanLeg.DepartureDT?.get(0)) {
							def defFirstPolDt = current_OceanLeg.DepartureDT[0].GMT
							if (defFirstPolDt) {
								'DTM' {
									java.util.Date date = util.sdfXmlFmt.parse(defFirstPolDt);
									'E374_01' date.after(currentDate) ? '139' : '140'
									'E373_02' util.convertXmlDateTime(defFirstPolDt, yyyyMMdd)
									'E337_03' util.convertXmlDateTime(defFirstPolDt, HHmm)
								}
							}
						}

						if (SIAndVGMCutOffDTOutputFlag) {	//CSNAEDI6631
							SIAndVGMCutOffDTOutputFlag = false	//only output SICutOffDT and VGMCutOffDT in R4*L
							def dt649 = current_Body.DocumentInformation?.RequiredDocument?.find(){it.DocumentType?.trim() == "Shipping Instruction/BL Master" && it.DueDT?.GMT}?.DueDT?.GMT
							if (util.isEmpty(dt649)) {
								dt649 = current_Body.GeneralInformation?.SICutOffDT?.GMT
							}
							if (dt649) {
								'DTM' {
									'E374_01' '649'
									'E373_02' util.convertXmlDateTime(dt649, yyyyMMdd)
									'E337_03' util.convertXmlDateTime(dt649, HHmm)
								}
							}

							if (current_Body.GeneralInformation?.VGMCutOffDT) {
								def dVgmCutoffGmt = current_Body.GeneralInformation?.VGMCutOffDT?.GMT
								if (dVgmCutoffGmt) {
									'DTM' {
										'E374_01' 'AAG'
										'E373_02' util.convertXmlDateTime(dVgmCutoffGmt, yyyyMMdd)
										'E337_03' util.convertXmlDateTime(dVgmCutoffGmt, HHmm)
									}
								} else {
									def dVgmCutoffLoc = current_Body.GeneralInformation?.VGMCutOffDT?.LocDT?.LocDT
									if (dVgmCutoffLoc) {
										'DTM' {
											'E374_01' 'AAG'
											'E373_02' util.convertXmlDateTime(dVgmCutoffLoc, yyyyMMdd)
											'E337_03' util.convertXmlDateTime(dVgmCutoffLoc, HHmm)
										}
									}
								}
							}
						}
					}
				
					//OceanLeg - POD
					'Loop_R4' {
						'R4' {
							'E115_01' 'D'
							if (current_OceanLeg.POD?.Port?.LocationCode?.UNLocationCode ) {
								'E309_02' 'UN'
							} else if (current_OceanLeg.POD?.Port?.LocationCode?.SchedKDCode) {
								'E309_02' current_OceanLeg.POD?.Port?.LocationCode?.SchedKDType
							}
							if (current_OceanLeg.POD?.Port?.LocationCode?.UNLocationCode) {
								'E310_03' current_OceanLeg.POD?.Port?.LocationCode?.UNLocationCode
							} else if (current_OceanLeg.POD?.Port?.LocationCode?.SchedKDCode) {
								'E310_03' current_OceanLeg.POD?.Port?.LocationCode?.SchedKDCode
							}
							if (current_OceanLeg.POD?.Port?.City) {
								'E114_04' util.substring(current_OceanLeg.POD?.Port?.City.trim(), 1, 24)
							}
							if (current_OceanLeg.POD?.Port?.CSCountryCode) {
								'E26_05' current_OceanLeg.POD?.Port?.CSCountryCode
							}
							'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_OceanLeg.POD?.Port?.State, current_OceanLeg.POD?.Port?.CSCountryCode, conn)
						}

						if (current_OceanLeg.ArrivalDT && current_OceanLeg.ArrivalDT?.get(0)) {
							def defArrivalDt = current_OceanLeg.ArrivalDT[0].GMT
							if (defArrivalDt) {
								'DTM' {
									java.util.Date date = util.sdfXmlFmt.parse(defArrivalDt);
									'E374_01' date.after(currentDate) ? '139' : '140'
									'E373_02' util.convertXmlDateTime(defArrivalDt, yyyyMMdd)
									'E337_03' util.convertXmlDateTime(defArrivalDt, HHmm)
								}
							}
						}
					}
				}
			}
			
			//loopType = 'FND'
			'Loop_R4' {
				'R4' {
					'E115_01' 'E'
					if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body.Route.FND.CityDetails.LocationCode.SchedKDType
					}
					if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' current_Body.Route.FND.CityDetails.LocationCode.UNLocationCode
					} else if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' current_Body.Route.FND.CityDetails.LocationCode.SchedKDCode
					}
					if (current_Body?.Route?.FND?.CityDetails?.City) {
						'E114_04' util.substring(current_Body?.Route?.FND?.CityDetails?.City.trim(), 1, 24)
					}
					if (current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode) {
						'E26_05' current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode
					}
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.FND?.CityDetails?.State, current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode, conn)
				}
				def fndDt = ''
				if (current_Body?.Route?.ArrivalAtFinalHub && current_Body?.Route?.ArrivalAtFinalHub?.get(0)) {
					fndDt = current_Body?.Route?.ArrivalAtFinalHub[0].GMT
				}
				if (util.isEmpty(fndDt)) {
					fndDt = current_Body?.Route?.LastPOD?.ArrivalDT?.GMT
				}
				if (fndDt) {
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(fndDt);
						'E374_01' date.after(currentDate) ? '139' : '140'
						'E373_02' util.convertXmlDateTime(fndDt, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(fndDt, HHmm)
					}
				}
			}
			//end of R4 Loop
			
			//Cargo - LX1
			current_Body.Cargo?.eachWithIndex{ current_Cargo, current_Index->
				'Loop_LX' {
					'LX' {
						'E554_01' current_Index + 1 
					}
					if (current_Index == 0) {
						def remarkLines = []
						current_Body.RemarkLines?.each { current_Remarks ->
							if (current_Remarks?.RemarkLines) {
								remarkLines.add(util.substring(current_Remarks.RemarkLines, 1, 60).trim())
								if (current_Remarks.RemarkLines.length()>60) {
									remarkLines.add(util.substring(current_Remarks.RemarkLines, 61, 60).trim())
								}
								if (current_Remarks.RemarkLines.length()>120) {
									remarkLines.add(util.substring(current_Remarks.RemarkLines, 121, 60).trim())
								}
								if (current_Remarks.RemarkLines.length()>180) {
									remarkLines.add(util.substring(current_Remarks.RemarkLines, 181, 60).trim())
								}
								if (current_Remarks.RemarkLines.length()>240) {
									remarkLines.add(util.substring(current_Remarks.RemarkLines, 241, 60).trim())
								}
								if (current_Remarks.RemarkLines.length()>300) {
									remarkLines.add(util.substring(current_Remarks.RemarkLines, 301, 60).trim())
								}
								if (current_Remarks.RemarkLines.length()>360) {
									remarkLines.add(util.substring(current_Remarks.RemarkLines, 361, 60).trim())
								}
								if (current_Remarks.RemarkLines.length()>420) {
									remarkLines.add(util.substring(current_Remarks.RemarkLines, 421, 60).trim())
								}
								if (current_Remarks.RemarkLines.length()>480) {
									remarkLines.add(util.substring(current_Remarks.RemarkLines, 481, 60).trim())
								}
								if (current_Remarks.RemarkLines.length()>540) {
									remarkLines.add(util.substring(current_Remarks.RemarkLines, 541, 60).trim())
								}
							}
						}
						remarkLines.eachWithIndex { remark, rIndex ->
							//only loop max 10 times
							if (rIndex>9) {
								return
							}
							'K1' {
								'E61_01' util.substring(remark, 1, 30).trim()
								if (remark.length() > 30) {
									'E61_02' util.substring(remark, 31, 30).trim()
								}
							}
						}
					}
					
					'L0' {
						'E213_01' current_Index + 1
						if (current_Cargo.GrossWeight?.Weight?.replace('+','')) {
							'E81_04' util.substring(current_Cargo.GrossWeight?.Weight?.replace('+',''), 1, 14).trim()
							'E187_05' 'G'
						}
						if (current_Cargo.Volume?.Volume) {
							if (current_Cargo.Volume?.VolumeUnit?.toUpperCase()=='CBM' || current_Cargo.Volume?.VolumeUnit?.toUpperCase()=='CBF') {
								'E183_06' current_Cargo.Volume?.Volume
							}
							if (current_Cargo.Volume?.VolumeUnit?.toUpperCase()=='CBM') {
								'E184_07' 'X'
							} else if(current_Cargo.Volume?.VolumeUnit?.toUpperCase()=='CBF') {
								'E184_07' 'E'
							}
						}
						
						if (current_Cargo.Packaging?.PackageType) {
							def defPackageConvert = util.getEDICdeReffromIntCde(TP_ID, 'BookingConfirmation', DIR_ID, 'BCCS2X', 'BookingConfirmation_Loop@LX_L0', 'L009', current_Cargo.Packaging?.PackageType, conn)
							if((defPackageConvert || current_Cargo.Packaging?.PackageType) && current_Cargo.Packaging?.PackageQty && Integer.parseInt(current_Cargo.Packaging.PackageQty)>0) {
								'E80_08' current_Cargo.Packaging.PackageQty
							}
							if (defPackageConvert) {
								'E211_09' defPackageConvert
							}
						}
						'E458_10' ''
						if (current_Cargo.GrossWeight?.WeightUnit) {
							def defType = util.substring(current_Cargo.GrossWeight?.WeightUnit?.toUpperCase(), 1, 1)
							if (defType == 'K' || defType == 'L') {
								'E188_11' defType
							}
						}
						'E56_12' ''
						'E380_13' ''
						'E211_14' ''
						'E1073_15' ''
					}
					'L5' {
						'E213_01' ''
						if (current_Cargo?.CargoDescription) {
							'E79_02' util.substring(current_Cargo?.CargoDescription.trim(), 1, 50)
						}
						'E22_03' ''
						'E23_04' ''
						'E103_05' ''
						if (current_Cargo?.MarksAndNumbers && current_Cargo?.MarksAndNumbers?.get(0)?.MarksAndNumbersLine) {
							'E87_06' util.substring(current_Cargo?.MarksAndNumbers[0]?.MarksAndNumbersLine?.trim(), 1, 48)
						}
						'E88_07' ''
						'E23_08' ''
						'E22_09' ''
						'E595_10' ''
					}
					
					if (current_Cargo.AWCargoSpec && current_Cargo.AWCargoSpec?.get(0)) {
						AWCargoSpec currentAWCargo = current_Cargo.AWCargoSpec?.get(0)
						'L4' {
							if (currentAWCargo.Length?.Length) {
								Double  lenth = Double.parseDouble(currentAWCargo.Length.Length)
								if (currentAWCargo.Length?.LengthUnit == 'F') {
									'E82_01' decimalFormatter.format(lenth)
								} else if (currentAWCargo.Length?.LengthUnit=='M') {
									//lenth.multiply(v03048).setScale(4, BigDecimal.ROUND_HALF_UP)
									'E82_01' decimalFormatter.format(lenth * 0.3048)
								} else {
									'E82_01' decimalFormatter.format(lenth)
								}
							} else {
								'E82_01' ''
							}
							if (currentAWCargo.Width?.Length) {
								Double  lenth = Double.parseDouble(currentAWCargo.Width.Length)
								if (currentAWCargo.Width.LengthUnit=='F') {
									'E189_02' decimalFormatter.format(lenth)
								} else if (currentAWCargo.Width?.LengthUnit=='M') {
									'E189_02' decimalFormatter.format(lenth * 0.3048)
								} else {
									'E189_02' decimalFormatter.format(lenth)
								}
							} else {
								'E189_02' ''
							}
							if (currentAWCargo.Height?.Length) {
								Double  lenth = Double.parseDouble(currentAWCargo.Height.Length)
								if (currentAWCargo.Height.LengthUnit=='F') {
									'E65_03' decimalFormatter.format(lenth)
								} else if(currentAWCargo.Height.LengthUnit=='M') {
									'E65_03' decimalFormatter.format(lenth * 0.3048)
								} else {
									'E65_03' decimalFormatter.format(lenth)
								}
							} else {
								'E65_03' ''
							}
							if (currentAWCargo.Height?.LengthUnit=='F' && currentAWCargo.Width?.LengthUnit == 'F' && currentAWCargo.Length?.LengthUnit == 'F') {
								'E90_04' 'F'
							} else {
								'E90_04' 'M'
							}
							'E380_05' ''
							'E1271_06' ''
						}
					}
					
					'L1' {
						'E213_01' ''
						'E60_02' ''
						'E122_03' ''
						'E58_04' ''
						'E191_05' ''
						'E117_06' ''
						'E120_07' ''
						'E150_08' ''
						'E121_09' ''
						'E39_10' ''
						'E16_11' ''
						'E276_12' ''
						'E257_13' ''
						'E74_14' ''
						'E122_15' ''
						'E372_16' ''
						'E220_17' ''
						'E221_18' ''
						'E954_19' ''
						'E100_20' ''
						'E610_21' ''
					}
					
					current_Cargo.DGCargoSpec.each{ current_DGCargoSpec ->
						'Loop_H1' {
							'H1' {
								'E62_01' current_DGCargoSpec.UNNumber
								'E209_02' util.substring(current_DGCargoSpec.IMOClass, 1, 4)
								'E208_03' current_DGCargoSpec.DGRegulator=='IMD'?'I':''
								'E64_04' util.substring(current_DGCargoSpec.ProperShippingName?.trim(), 1, 30)?.trim()
								if (current_DGCargoSpec.EmergencyContact && current_DGCargoSpec.EmergencyContact.size()>0 && current_DGCargoSpec.EmergencyContact?.get(0)?.FirstName) {
									'E63_05' util.substring(current_DGCargoSpec.EmergencyContact?.get(0)?.FirstName?.trim(), 1, 24)?.trim()
								}
								'E200_06' ''
								'E77_07' current_DGCargoSpec.FlashPoint?.Temperature
								if (current_DGCargoSpec.FlashPoint?.TemperatureUnit=='C') {
									'E355_08' 'CE'
								} else if (current_DGCargoSpec.FlashPoint?.TemperatureUnit=='F') {
									'E355_08' 'FA'
								}
								//MapDGPackageGroupCode
								if (current_DGCargoSpec.PackageGroup?.Code in ['I', 'II', 'III']) {
									'E254_09' current_DGCargoSpec.PackageGroup?.Code
								}
							}
							def defName = current_DGCargoSpec.TechnicalName?.trim()
							def defNameS1 = util.substring(defName, 1, 30)
							if (defNameS1) {
								'H2' {
									if (defNameS1.length()==1) {
										'E64_01' defNameS1 + " "
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
										'E64_01' defName61 + " "
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
										'E64_01' defName121 + " "
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
										'E64_01' defProperShippingNameS1 + " "
									} else {
										'E64_01' defProperShippingNameS1
									}
									'E274_02' util.substring(defProperShippingName, 61, 20)
								}
							}
						}
					}
				}
			}
			//end of Cargo Loop - LX1
			// V1 Loop
			Map dirMap = ['E':'East', 'S':'South', 'W':'West', 'N':'North']
			OceanLeg the1stOceanLeg = null
			if (current_Body?.Route?.OceanLeg && current_Body?.Route?.OceanLeg?.size()>0) {
				the1stOceanLeg = current_Body.Route?.OceanLeg?.get(0)
			}
			if (the1stOceanLeg) {
				def def597 = util.substring(the1stOceanLeg.SVVD?.Loading?.LloydsNumber, 1, 20)
				if (! def597) {
					def597 = util.substring(the1stOceanLeg.SVVD?.Loading?.Vessel, 1, 8)
				}
				def defVoyage = the1stOceanLeg.SVVD?.Loading?.Voyage
				defVoyage = (defVoyage==null?"":defVoyage)
				if(! the1stOceanLeg.SVVD?.Loading?.Direction) {
					defVoyage = util.substring(defVoyage, 1, 10)
				} else if (dirMap.get(the1stOceanLeg.SVVD?.Loading?.Direction)) {
					defVoyage = util.substring(defVoyage + dirMap.get(the1stOceanLeg.SVVD?.Loading?.Direction), 1, 10)
				}
				
				if (def597 || defVoyage) {
					'V1' {
						'E597_01' def597
						if (the1stOceanLeg.SVVD?.Loading?.VesselName) {
							'E182_02' util.substring(the1stOceanLeg.SVVD?.Loading?.VesselName, 1, 28)
						} else if (the1stOceanLeg.SVVD?.Discharge?.VesselName) {
							'E182_02' util.substring(the1stOceanLeg.SVVD?.Discharge?.VesselName, 1, 28)
						}
						if (the1stOceanLeg.LoadingVesselNationalityCode) {
							'E26_03' the1stOceanLeg.LoadingVesselNationalityCode.trim()
						} else if (the1stOceanLeg.SVVD?.Loading?.VesselNationality) {
							'E26_03' util.getCS2MasterCity4CountryCodeByCountryName(the1stOceanLeg.SVVD?.Loading?.VesselNationality, conn)
						}
						'E55_04' defVoyage
						'E140_05' ''
						'E249_06' ''
						'E854_07' ''
						if (the1stOceanLeg.SVVD?.Loading?.LloydsNumber) {
							'E897_08' 'L'
						} else if (! the1stOceanLeg.SVVD?.Loading?.LloydsNumber && the1stOceanLeg.SVVD?.Loading?.Vessel) {
							'E897_08' 'Z'
						} else if (! the1stOceanLeg.SVVD?.Loading?.LloydsNumber && ! the1stOceanLeg.SVVD?.Loading?.Vessel) {
							'E897_08' ''
						}
						'E91_09' ''
					}
				}
			}
			
			if (current_Body?.Route?.OceanLeg?.size()>1) {
				OceanLeg theLastOceanLeg = current_Body.Route?.OceanLeg[-1]
				def def597 = util.substring(theLastOceanLeg.SVVD?.Loading?.LloydsNumber, 1, 20)
				if (! def597) {
					def597 = util.substring(theLastOceanLeg.SVVD?.Loading?.Vessel, 1, 8)
				}
				def devVoyage = theLastOceanLeg.SVVD?.Discharge?.Voyage
				devVoyage = (devVoyage==null?"":devVoyage.trim())
				if(! theLastOceanLeg.SVVD?.Discharge?.Direction){
					devVoyage = util.substring(devVoyage, 1, 10)
				} else if(dirMap.get(theLastOceanLeg.SVVD?.Discharge?.Direction)){
					devVoyage =  util.substring(devVoyage + dirMap.get(theLastOceanLeg.SVVD?.Discharge?.Direction), 1, 10)
				}
				
				if (def597 || devVoyage) {
					'V1' {
						'E597_01' def597
						if (theLastOceanLeg.SVVD?.Loading?.VesselName) {
							'E182_02' util.substring(theLastOceanLeg.SVVD?.Loading?.VesselName, 1, 28)
						} else if (theLastOceanLeg.SVVD?.Discharge?.VesselName) {
							'E182_02' util.substring(theLastOceanLeg.SVVD?.Discharge?.VesselName, 1, 28)
						}
						if (theLastOceanLeg.DischargeVesselNationalityCode) {
							'E26_03' theLastOceanLeg.DischargeVesselNationalityCode.trim()
						} else if (theLastOceanLeg.SVVD?.Discharge?.VesselNationality) {
							'E26_03' util.getCS2MasterCity4CountryCodeByCountryName(theLastOceanLeg.SVVD?.Discharge?.VesselNationality, conn)
						}
						'E55_04' devVoyage
						'E140_05' ''
						'E249_06' ''
						'E854_07' ''
						if (theLastOceanLeg.SVVD?.Discharge?.LloydsNumber) {
							'E897_08' 'L'
						} else if (! theLastOceanLeg.SVVD?.Discharge?.LloydsNumber && theLastOceanLeg.SVVD?.Discharge?.Vessel) {
							'E897_08' 'Z'
						} else if (! theLastOceanLeg.SVVD?.Discharge?.LloydsNumber && ! theLastOceanLeg.SVVD?.Discharge?.Vessel) {
							'E897_08' ''
						}
						'E91_09' ''
					}
				}
			}
			//end of Loop V1
			
			//txn mapping end
			'SE' {
				'E96_01' '-999'
				'E329_02' '-999'
			}
		}
		//Loop_ST end
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
		def headerMsgDT = util.convertDateTime(bc.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		def txnErrorKeys = []

		//Start mapping

		bc?.Body?.eachWithIndex{current_Body, current_BodyIndex ->

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			//mapping
			if (errorKeyList.isEmpty()) {
				generateBody(current_Body, outXml)
			}
			
			//posp checking
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
		}
		writer.close();
		
		return result;
		
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bc.Body current_Body, int current_BodyIndex){

		return [];
	}
	
	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList){
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T301>")
		
		//validation rules
		bcUtil.checkY3_01Mandatory(node, true, null, errorKeyList)
		bcUtil.checkB102Mandatory(current_Body, node, true, null, errorKeyList)
	}

}


