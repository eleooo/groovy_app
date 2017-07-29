package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.DecimalFormat

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.EmptyPickup
import cs.b2b.core.mapping.bean.bc.ExternalReference
import cs.b2b.core.mapping.bean.bc.Facility
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * 
 * @author YINEM
 * shared by CHALLENGERDSG, pattern after STD2
 */
public class CUS_CS2BCXML_301_DSG_STD3 {

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
	
	def currentSystemDt = null
	DecimalFormat decimalFormatter = new DecimalFormat("#.####")
	DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")

	Map bookingStatusMap = ['Confirmed':'A','Pending':'B','Wait Listed':'B','Cancelled':'D','No Show':'D','Declined':'R','Update':'U']
	
	Map<String,String> referenceTypeMap = ['BL':'BM','CR':'CR', 'CTR':'CT', 'FR':'FN', 'ICR':'CR', 'INV':'IN', 'PO':'PO', 'QUOT':'Q1', 'SC':'E8', 'SO':'SO', 'SR':'SI',]
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '301'
				'E329_02' '-999'
			}
			'B1' {
				'E140_01' current_Body.GeneralInformation?.SCACCode
				 
				List<ExternalReference> RefNumList = current_Body?.ExternalReference?.findAll{it.CSReferenceType=='SID'}
				if (RefNumList.size() > 0 && RefNumList[-1].ReferenceNumber) {
					'E145_02' util.substring(RefNumList[-1].ReferenceNumber?.trim(), 1, 30)
				} else {
					'E145_02' 'N/A'
				}
				'E373_03' util.convertXmlDateTime(current_Body?.GeneralInformation?.BookingStatusDT?.LocDT, yyyyMMdd)
				'E558_04' bookingStatusMap.get(current_Body?.GeneralInformation?.BookingStatus)
			}
			'Y3' {
				if (current_Body.GeneralInformation?.SCACCode && current_Body?.GeneralInformation?.CarrierBookingNumber) {
					'E13_01' current_Body.GeneralInformation.CarrierBookingNumber
				}
				else if(current_Body.GeneralInformation.BookingStatus=='Declined'){
					'E13_01' 'DECLINE'
				}
				'E140_02' current_Body.GeneralInformation?.SCACCode
				if (current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT) {
					'E373_03' util.convertXmlDateTime(current_Body.Route.FirstPOL.DepartureDT.LocDT.LocDT, yyyyMMdd)
				}
				if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT) {
					'E373_04'  util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT.LocDT, yyyyMMdd)
				}
				if (current_Body?.Route?.FullReturnCutoff?.LocDT?.LocDT) {
					'E373_07'  util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT.LocDT, yyyyMMdd)
				}
				if (current_Body?.Route?.FullReturnCutoff?.LocDT?.LocDT) {
					'E337_08'   util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT.LocDT, HHmm)
				}
				Map IOMAP = ['C':'D','M':'P']
				def ob = IOMAP.get(current_Body.Route?.Haulage?.OutBound)
				def ib = IOMAP.get(current_Body.Route?.Haulage?.InBound)
				if (ob && ib) {
					'E375_10' ob + ib
				}
			}
			
			//Y4 Loop
			//get looping of each EmptyPickup						
			current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup?.eachWithIndex { current_EmptyPickup, idx ->
				if (idx > 9) {
					return
				}
				'Loop_Y4' {
					'Y4' {
						'E13_01' current_Body.GeneralInformation?.CarrierBookingNumber
						'E373_03' util.convertXmlDateTime(current_EmptyPickup.MvmtDT?.LocDT, yyyyMMdd)
						'E95_05' current_EmptyPickup.NumberOfContainers
						
						def defConvType = ''
						if (current_EmptyPickup.ISOSizeType) {
							defConvType = util.getConversionByTpIdMsgTypeDirFmtScac(TP_ID, MSG_TYPE_ID, DIR_ID, "X.12", "ContainerType", current_Body.GeneralInformation?.SCACCode, current_EmptyPickup.ISOSizeType, conn)
							if (! defConvType) {
								defConvType = current_EmptyPickup.ISOSizeType
							}
						}
						'E24_06' defConvType
						
						def def309 = ''
						if(current_EmptyPickup.Address?.LocationCode?.UNLocationCode){
							def309 = 'UN'
						}
						else if(current_EmptyPickup.Facility?.FacilityCode){
							def309 = 'ZZ'
						}
						else if(current_EmptyPickup.Address?.City){
							def309 = 'CI'
						}						
						'E309_08' def309
						
						def defOb = current_Body?.GeneralInformation?.TrafficMode?.OutBound
						def defIb = current_Body?.GeneralInformation?.TrafficMode?.InBound
						def def56 = ''
						if(defOb && defIb){
							if(defOb == 'FCL' && defIb == 'FCL') {
								def56 = '03'
							} else if(defOb == 'FCL' && defIb == 'LCL') {
								def56 = '04'
							} else if(defOb == 'LCL' && defIb == 'FCL') {
								def56 = '05'
							}  else if(defOb == 'LCL' && defIb == 'LCL') {
								def56 = '02'
							}
						}
						'E56_10' def56
					}
					
					//W09
					ReeferCargoSpec defFirstReeferCargo = current_Body.Cargo.find(){it.ReeferCargoSpec}?.ReeferCargoSpec?.first()
					if (defFirstReeferCargo) {
						'W09' {
							'E40_01' 'CZ'
							if (defFirstReeferCargo.Temperature?.Temperature) {
								BigDecimal  temperature = new BigDecimal(defFirstReeferCargo.Temperature?.Temperature)
								'E408_02' temperature.setScale(0, BigDecimal.ROUND_HALF_UP)
							}
							
							def defTempUint = ''
							if (defFirstReeferCargo.Temperature?.TemperatureUnit == 'C') {
								defTempUint = 'CE'
							} else if (defFirstReeferCargo.Temperature?.TemperatureUnit == 'F') {
								defTempUint = 'FA'
							}
							'E355_03' defTempUint
							
							'E488_08' defFirstReeferCargo.DehumidityPercentage
							'E380_09' defFirstReeferCargo.Ventilation?.Ventilation
						}
					}
				}
			}
			
			current_Body?.CarrierRate.each { current_CarrierRate ->
				if(referenceTypeMap.get(current_CarrierRate.CSCarrierRateType)) {
					'N9' {
						'E128_01' util.substring(referenceTypeMap.get(current_CarrierRate.CSCarrierRateType), 1, 3)
						'E127_02' util.substring(current_CarrierRate.CarrierRateNumber, 1, 30)
					}
				}
			}
			current_Body?.ExternalReference.each { current_ExternalReference ->
				if (referenceTypeMap.get(current_ExternalReference.CSReferenceType)) {
					'N9' {
						'E128_01' util.substring(referenceTypeMap.get(current_ExternalReference.CSReferenceType), 1, 3)
						'E127_02' util.substring(current_ExternalReference.ReferenceNumber, 1, 30)
					}
				}
			}
			ExternalReference defCustRefCR = current_Body?.ExternalReference?.find(){it.ReferenceDescription == 'Customer Reference Number' &&  it.CSReferenceType != 'CR' && it.ReferenceNumber}
			if (defCustRefCR) {
				'N9' {
					'E128_01' 'CR'
					'E127_02' util.substring(defCustRefCR.ReferenceNumber, 1, 30)
				}
			}
			if(current_Body?.GeneralInformation?.CarrierBookingNumber) {
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(current_Body.GeneralInformation.CarrierBookingNumber, 1, 30)
				}
			}
			
			//LoopSYNFN
//			List<ExternalReference> RefNumList = current_Body?.ExternalReference?.findAll{it.ReferenceDescription?.toUpperCase()=='SYSTEM REFERENCE NUMBER'}
//			List<ExternalReference> RefNumListSR = current_Body?.ExternalReference?.findAll{it.CSReferenceType=='SR'}
//			def def127='BLANK'
//			if(RefNumList.size() > 0 && RefNumList[-1].ReferenceNumber){					
//					def127 = RefNumList[-1].ReferenceNumber
//				}
//			'N9' {
//					'E128_01' 'FN'
//					'E127_02' util.substring(def127.trim(), 1, 30)
//				}

			
			//LoopShipperReferenceWhenNotEqSYN
//			if(RefNumListSR.size()>0){
//				RefNumListSR.each {
//					def fn127 = it.ReferenceNumber
//					if(fn127 && fn127!=def127){						
//						'N9' {
//							'E128_01' 'SR'
//							'E127_02' util.substring(fn127.trim(),1,30)
//						}
//					}
//				}
//			}


			////////N1 start
			Map N1Map = ['BPT':'BK', 'CGN':'CN', 'FWD':'FW','SHP':'SH']
			Map countryMap = util.getCountryMap(conn)
			
			current_Body?.Party?.each { current_Party ->
				if (N1Map.get(current_Party.PartyType) && current_Party.PartyName) {
					//looptype=loop_N1
					'Loop_N1' {
						'N1' {
							'E98_01' N1Map.get(current_Party.PartyType)
							'E93_02' util.substring(current_Party.PartyName, 1, 60)?.trim()
							if (current_Party.CarrierCustomerCode) {
								'E66_03' '25'
							}
							if (current_Party.CarrierCustomerCode) {
								'E67_04' current_Party.CarrierCustomerCode.trim()
							}
							'E706_05' ''
							'E98_06' ''
						}
						'N2' {
							'E93_01' ''
							'E93_02' ''
						}
						if (current_Party.Address?.AddressLines?.AddressLine?.get(0)) {
							def addressLineToSplit = current_Party.Address?.AddressLines?.AddressLine[0]?.trim()
							def addressLineToCheck = current_Party.Address?.AddressLines?.AddressLine[0]?.trim()
							for (i in 1..4) {
								if(current_Party.Address?.AddressLines?.AddressLine[i]) {
									addressLineToCheck = addressLineToCheck + current_Party.Address.AddressLines.AddressLine[i].trim()
									addressLineToSplit = addressLineToSplit + " "  + current_Party.Address.AddressLines.AddressLine[i].trim()
								} else {
									addressLineToSplit = addressLineToSplit + " "
								}
							}
							//loop1
							'N3' {
								'E166_01' util.substring(addressLineToSplit, 1, 55)?.trim()
								if (addressLineToCheck.length() > 55) {
									'E166_02' util.substring(addressLineToSplit, 56, 55)?.trim()
								}
							}
							//loop2
							if (addressLineToCheck.length() > 111) {
								'N3' {
									'E166_01' util.substring(addressLineToSplit, 111, 55)?.trim()
									if (addressLineToCheck.length() > 166) {
										'E166_02' util.substring(addressLineToSplit, 166, 55)?.trim()
									}
								}
							}
						}
						'N4' {
							'E19_01' util.substring(current_Party.Address?.City, 1, 30)?.trim()
							//cannot use common city query as the state_cde index use in below sql 
							'E156_02' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Party.Address?.State, current_Party.Address?.Country, conn)
							'E116_03' current_Party.Address?.PostalCode?.trim()
							if (current_Party.Address?.Country?.length() < 4) {
								'E26_04' current_Party.Address?.Country?.trim()
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
						def defPartyType = ''
						if(current_Party.PartyType == 'BPT'){
							defPartyType = 'BP'
						}else
							defPartyType = N1Map.get(current_Party.PartyType)
						
						if (defEmail) {
							'G61' {
								'E366_01' defPartyType
								'E93_02' defContact
								'E365_03' 'EM'
								'E364_04' util.substring(defEmail, 1, 80)?.trim()
								'E443_05' ''
							}
						}
						if (current_Party.Contact?.ContactPhone?.Number) {
							'G61' {
								'E366_01' defPartyType
								'E93_02' defContact
								'E365_03' 'TE'
								'E364_04' util.substring(current_Party.Contact?.ContactPhone?.Number,1,80)?.trim()
								'E443_05' ''
							}
						}
					}
				}
			}			
			//////////N1 end
			
			//loopType = 'POR'
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'					
					if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' current_Body.Route.POR.CityDetails.LocationCode.UNLocationCode
					} else if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDType
						'E310_03' current_Body.Route.POR.CityDetails.LocationCode.SchedKDCode
					}
					'E114_04' util.substring(current_Body?.Route?.POR?.CityDetails?.City, 1, 24)
					if (current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode) {
						'E26_05' current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode
					}
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.POR?.CityDetails?.State, current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode, conn)
				}
				if (current_Body?.Route?.FullReturnCutoff?.LocDT) {
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(current_Body.Route.FullReturnCutoff.LocDT.LocDT);
						'E374_01' date.after(currentDate) ? '139' : '140'
						'E373_02' util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT, HHmm)
					}
				}
			}
			//loopType = 'POL'
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'					
					if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' current_Body.Route.FirstPOL.Port.LocationCode.UNLocationCode
					} else if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDType
						'E310_03' current_Body.Route.FirstPOL.Port.LocationCode.SchedKDCode
					}
					'E114_04' util.substring(current_Body?.Route?.FirstPOL?.Port?.City, 1, 24)
					if (current_Body?.Route?.FirstPOL?.Port?.CSCountryCode) {
						'E26_05' current_Body?.Route?.FirstPOL?.Port?.CSCountryCode
					}
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.FirstPOL?.Port?.State, current_Body?.Route?.FirstPOL?.Port?.CSCountryCode, conn)
				}
				
				if (current_Body?.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT) {
					//POL_1
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(current_Body?.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT);
						'E374_01' date.after(currentDate) ? '139':'140'
						'E373_02' util.convertXmlDateTime(current_Body.Route.FirstPOL.DepartureDT.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route.FirstPOL.DepartureDT.LocDT, HHmm)
					}
				}
			}
			//loopType = 'POD'
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'					
					if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' current_Body.Route.LastPOD.Port.LocationCode.UNLocationCode
					} else if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDType
						'E310_03' current_Body.Route.LastPOD.Port.LocationCode.SchedKDCode
					}
					'E114_04' util.substring(current_Body?.Route?.LastPOD?.Port?.City, 1, 24)
					'E26_05' current_Body?.Route?.LastPOD?.Port?.CSCountryCode
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.LastPOD?.Port?.State, current_Body?.Route?.LastPOD?.Port?.CSCountryCode, conn)
				}
				if (current_Body?.Route?.LastPOD?.ArrivalDT?.LocDT) {
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(current_Body?.Route?.LastPOD?.ArrivalDT?.LocDT.LocDT);
						'E374_01' date.after(currentDate) ? '139' : '140'
						'E373_02' util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT, HHmm)
					}
				}
			}
			//loopType = 'FND'
			'Loop_R4' {
				'R4' {
					'E115_01' 'E'					
					if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' current_Body.Route.FND.CityDetails.LocationCode.UNLocationCode
					} else if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDType
						'E310_03' current_Body.Route.FND.CityDetails.LocationCode.SchedKDCode
					}
					'E114_04' util.substring(current_Body?.Route?.FND?.CityDetails?.City, 1, 24)
					'E26_05' current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.FND?.CityDetails?.State, current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode, conn)
				}
				def v_fnd_dt=null
				if (current_Body.Route?.ArrivalAtFinalHub && current_Body.Route?.ArrivalAtFinalHub[0]?.LocDT?.LocDT) {
					v_fnd_dt = current_Body.Route.ArrivalAtFinalHub[0].LocDT.LocDT
				} else if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT) {
					v_fnd_dt =current_Body.Route.LastPOD.ArrivalDT.LocDT?.LocDT
				}
				if (v_fnd_dt) {
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(v_fnd_dt);
						'E374_01' date.after(currentDate) ? '139' : '140'
						'E373_02' util.convertXmlDateTime(v_fnd_dt, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(v_fnd_dt, HHmm)
					}
				}
			}
			current_Body?.Cargo?.eachWithIndex{ current_Cargo, index->
				'Loop_LX' {
					'LX' { 'E554_01' index+1 }
					'L0' {
						'E213_01' index+1
						'E81_04' util.substring(current_Cargo.GrossWeight?.Weight?.replaceAll('\\+',''), 1, 14)
						'E187_05' util.substring(current_Cargo.GrossWeight?.Weight?.replaceAll('\\+',''), 1, 14)?'G':''
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
						if(current_Cargo.Packaging?.PackageQty && Integer.parseInt(current_Cargo.Packaging.PackageQty)>0) {
							'E80_08' current_Cargo.Packaging.PackageQty
						}
						if (current_Cargo.Packaging?.PackageType) {
							def defPackageConvert = util.getEDICdeReffromIntCde(TP_ID, 'BookingConfirmation', DIR_ID, 'BCCS2X', 'BookingConfirmation_Loop@LX_L0', 'L009', current_Cargo.Packaging?.PackageType, conn)							
							if (defPackageConvert) {
								'E211_09' defPackageConvert
							}
						}
						'E458_10' ''
						if(current_Cargo.GrossWeight?.WeightUnit){
							if(current_Cargo.GrossWeight.WeightUnit.substring(0,1)=='K')
							'E188_11' 'K'
							else if (current_Cargo.GrossWeight.WeightUnit.substring(0,1)=='L')
							'E188_11' 'L'
						}
						'E56_12' ''
						'E380_13' ''
						'E211_14' ''
						'E1073_15' ''
					}
					'L5' {
						'E213_01' ''
						'E79_02' util.substring(current_Cargo?.CargoDescription, 1, 50)
						'E22_03' ''
						'E23_04' ''
						'E103_05' ''
						'E87_06' util.substring(current_Cargo?.MarksAndNumbers[0]?.MarksAndNumbersLine, 1, 48)
						'E88_07' ''
						'E23_08' ''
						'E22_09' ''
						'E595_10' ''
					}
					if (current_Cargo.AWCargoSpec) {
						'L4' {
							if (current_Cargo.AWCargoSpec[0]?.Length?.Length) {
								Double  lenth = Double.parseDouble(current_Cargo.AWCargoSpec[0].Length.Length)
								if (util.substring(current_Cargo.AWCargoSpec[0].Length.LengthUnit?.toUpperCase(),1,1)=='F') {
									'E82_01' decimalFormatter.format(lenth)
								} else if(util.substring(current_Cargo.AWCargoSpec[0].Length.LengthUnit?.toUpperCase(),1,1)=='M') {
									//lenth.multiply(v03048).setScale(4, BigDecimal.ROUND_HALF_UP)
									'E82_01' decimalFormatter.format(lenth * 0.3048)
								} else {
									'E82_01' decimalFormatter.format(lenth)
								}
							} else {
								'E82_01' ''
							}
							if (current_Cargo.AWCargoSpec[0]?.Width?.Length) {
								Double  lenth = Double.parseDouble(current_Cargo.AWCargoSpec[0].Width.Length)
								if(util.substring(current_Cargo.AWCargoSpec[0].Width.LengthUnit?.toUpperCase(),1,1)=='F'){
									'E189_02' decimalFormatter.format(lenth)
								}else if(util.substring(current_Cargo.AWCargoSpec[0].Width.LengthUnit?.toUpperCase(),1,1)=='M'){
									'E189_02' decimalFormatter.format(lenth * 0.3048)
								}else{
									'E189_02' decimalFormatter.format(lenth)
								}
							} else {
								'E189_02' ''
							}
							if (current_Cargo.AWCargoSpec[0]?.Height?.Length) {
								Double  lenth = Double.parseDouble(current_Cargo.AWCargoSpec[0].Height.Length)
								if (util.substring(current_Cargo.AWCargoSpec[0].Height.LengthUnit?.toUpperCase(),1,1)=='F'){
									'E65_03' decimalFormatter.format(lenth)
								} else if(util.substring(current_Cargo.AWCargoSpec[0].Height.LengthUnit?.toUpperCase(),1,1)=='M'){
									'E65_03' decimalFormatter.format(lenth * 0.3048)
								} else {
									'E65_03' decimalFormatter.format(lenth)
								}
							} else {
								'E65_03' ''
							}
							if(current_Cargo.AWCargoSpec[0]?.Height?.LengthUnit == 'F' && current_Cargo.AWCargoSpec[0]?.Width?.LengthUnit == 'F' && current_Cargo.AWCargoSpec[0]?.Length?.LengthUnit == 'F'){
								'E90_04' 'F'
							}else{
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
								'E63_05' util.substring(current_DGCargoSpec.EmergencyContact[0]?.FirstName?.trim(), 1, 24)?.trim()
								'E200_06' ''
								'E77_07' current_DGCargoSpec.FlashPoint?.Temperature
								if (current_DGCargoSpec.FlashPoint?.TemperatureUnit=='C') {
									'E355_08' 'CE'
								}
								if (current_DGCargoSpec.FlashPoint?.TemperatureUnit=='F') {
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
			
			Map dirMap = ['E':'East', 'S':'South', 'W':'West', 'N':'North']
			
			
			if (current_Body?.Route?.OceanLeg && current_Body?.Route?.OceanLeg[0]) {
				def def597 = util.substring(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.LloydsNumber, 1, 20)
				if (! def597) {
					def597 = util.substring(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Vessel, 1, 8)
				}
				def defVoyage = current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Voyage
				def def55 = ''
				defVoyage = (defVoyage==null?"":defVoyage)
				if(! current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Direction) {
					def55 = util.substring(defVoyage,1,10)
				} else if (dirMap.get(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Direction)) {
					def55 = util.substring(defVoyage + dirMap.get(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Direction), 1, 10)
				}
				if (def597 || def55) {
					'V1' {
						if (def597) {
							'E597_01' def597
						}
						if (current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.VesselName) {
							'E182_02' util.substring(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.VesselName, 1, 28)
						} else if (current_Body?.Route?.OceanLeg[0].SVVD?.Discharge?.VesselName) {
							'E182_02' util.substring(current_Body?.Route?.OceanLeg[0].SVVD?.Discharge?.VesselName, 1, 28)
						}

						'E26_03' ''

						'E55_04' def55
						'E140_05' current_Body?.GeneralInformation?.SCACCode
						'E249_06' ''
						'E854_07' ''
						if (current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber) {
							'E897_08' 'L'
						} else if (!current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber&&current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.Vessel) {
							'E897_08' 'Z'
						} else if (!current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber&&!current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.Vessel) {
							'E897_08' ''
						}
						'E91_09' ''
					}
				}
			}
			if (current_Body?.Route?.OceanLeg?.size()>1) {
				def def597 = util.substring(current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.LloydsNumber, 1, 20)
				if (! def597) {
					def597 = util.substring(current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.Vessel, 1, 8)
				}
				def def55 = ''
				def devVoyage = current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.Voyage
				devVoyage = (devVoyage==null?"":devVoyage.trim())
				if(! current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.Direction){
					def55 = util.substring(devVoyage, 1, 10)
				} else if(dirMap.get(current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.Direction)){
					def55 = util.substring(devVoyage + dirMap.get(current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.Direction), 1, 10)
				}
				if (def597 || def55) {
					'V1' {
						if (def597) {
							'E597_01' def597
						}
						if (current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.VesselName) {
							'E182_02' util.substring(current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.VesselName, 1, 28)
						} else if (current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.VesselName) {
							'E182_02' util.substring(current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.VesselName, 1, 28)
						}
						'E26_03' ''
						'E55_04' def55
						'E140_05' current_Body?.GeneralInformation?.SCACCode
						'E249_06' ''
						'E854_07' ''
						if (current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.LloydsNumber) {
							'E897_08' 'L'
						} else if (!current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.LloydsNumber&&current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.Vessel) {
							'E897_08' 'Z'
						} else if (!current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.LloydsNumber&&!current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.Vessel) {
							'E897_08' ''
						}
						'E91_09' ''
					}
				}
			}
			'V9' {
				'E304_01' ''
				'E106_02' ''
				'E373_03' ''
				'E337_04' ''
				'E19_05' ''
				'E156_06' ''
				'E26_07' ''
				'E641_08' ''
				'E154_09' ''
				'E380_10' ''
				'E1274_11' ''
				'E61_12' ''
				'E623_13' ''
				'E380_14' ''
				'E154_15' ''
				'E86_16' ''
				'E86_17' ''
				'E86_18' ''
				'E81_19' ''
				'E82_20' ''
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
		inputXmlBody = util.removeBOM(inputXmlBody)

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

			//associate container and cargo
			//			Map<cs.b2b.core.mapping.bean.bc.Container, List<cs.b2b.core.mapping.bean.bc.Cargo>> associateContainerAndCargo = bcUtil.associateContainerAndCargo(current_Body)
			
			List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
			
			//prep checking
//				prepValidation(current_Body, current_BodyIndex,errorKeyList)	

			//mapping			
//			if(current_Body?.GeneralInformation?.BookingStatus=='Declined'||current_Body?.GeneralInformation?.BookingStatus=='Rejected') {
//				//Declined / Reject mapping
//				generateBody4RejectCase(current_Body, outXml)
//			} else {
				generateBody(current_Body, outXml)
//			}
			
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
		}

		writer.close();
		
		return result;
	}
	
	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList){

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T301>")

		bcUtil.checkB102Mandatory(current_Body,node,true,null,errorKeyList)
		bcUtil.checkN1LoopCount(current_Body,node,true,null,errorKeyList)
		bcUtil.checkY3_01Mandatory(node, true, null, errorKeyList)			

	}
//	void prepValidation(Body current_Body, int bodyLoopIndex, List<Map<String,String>> errorKeyList){
//		bcUtil.checkSystemReferenceNoForRejectEnable(current_Body,false,null,errorKeyList)
//	}
	
 //HERBALIFEVIADSGRejectBC
//	void generateBody4RejectCase(Body current_Body, MarkupBuilder outXml) {
//		//Gawing: rejected bc should be extracted to a method
//		outXml.'Loop_ST' {
//			'ST' {
//				'E143_01' '301'
//				'E329_02' '-999'
//			}
//			'B1' {
//				'E140_01' current_Body?.GeneralInformation?.SCACCode
//				def def145 = ''
//				if (current_Body?.GeneralInformation?.BookingStatusRemarks?.indexOf('|') >= 0) {
//					def145 = util.substring(current_Body?.GeneralInformation?.BookingStatusRemarks, 1, current_Body?.GeneralInformation?.BookingStatusRemarks.indexOf('|'))
//				} 
//				'E145_02' util.substring(def145?.trim(), 1, 30)
//
//				'E373_03' util.convertXmlDateTime(current_Body.GeneralInformation?.BookingStatusDT?.LocDT, yyyyMMdd)
//				'E558_04' 'D'
//			}
//			'Y3' { 'E13_01' 'DECLINE' }
//
//			'Loop_R4' {
//				'R4' { 'E115_01' 'L' }
//			}
//			'Loop_LX' {
//				'LX' { 'E554_01' '1' }
//				def remarks = ''
//				if (current_Body?.GeneralInformation?.BookingStatusRemarks?.indexOf('|') >= 0) {
//					remarks = current_Body?.GeneralInformation?.BookingStatusRemarks
//					remarks = remarks.substring(remarks.indexOf('|')+1)
//					if (remarks.indexOf('|') >= 0) {
//						remarks = remarks.substring(0, remarks.indexOf('|')) //remarks = 2nd part of BookingStatusRemarks,split by "|"
//					}
//				}
//				if (remarks) {
//					remarks = remarks.trim()
//					if(remarks.indexOf('+') >= 0){
//						remarks = remarks.substring(remarks.indexOf('+')+1)
//						if (remarks.indexOf('+') >= 0) {
//							remarks = remarks.substring(0, remarks.indexOf('+')).trim() //remarks = 2nd part of BookingStatusRemarks,split by "+"
//						}
//					}
//					'K1' {
//						'E61_01' util.substring(remarks, 1, 30)
//						'E61_02' util.substring(remarks, 31, 30)
//					}
//					if (remarks.length()>60) {
//						'K1' {
//							'E61_01' util.substring(remarks, 61, 30)
//							if (remarks.length()>90) {
//								'E61_02' util.substring(remarks, 91, 30)
//							}
//						}
//					}
//				}
//			}
//			'SE' {
//				'E96_01' '-999'
//				'E329_02' '-999'
//			}
//		}
//	}
}
