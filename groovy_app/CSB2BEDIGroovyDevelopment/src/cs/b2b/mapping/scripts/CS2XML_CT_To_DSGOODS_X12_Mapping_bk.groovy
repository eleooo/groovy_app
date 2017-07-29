import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * @author RENGA
 * DSGOODS CT initialize on 20161115 
 */
public class CS2XML_CT_To_DSGOODS_X12_Mapping_bk {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	def conn = null
	
	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = 'HHmm'

	private void generateBody(MarkupBuilder outXml, def current_Body, def current_BodyIndex, def currentSystemDt) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event.text()
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('DSGOODS', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.

		def currentSTCtrlNum = '###edi_ST_Ctrl_Number###' + String.format("%04d", current_BodyIndex+1)
		def B406 = null
		def B411 = null
		def B412 = null
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' currentSTCtrlNum
			}
			'B4' {

				def defaultB406 = current_Body.Event.Location.LocationCode.UNLocationCode.text()
				def defaultB411 = ''
				def defaultB412 = 'UN'
				if (current_Body.Event.Location?.CityDetails?.City?.text()?.trim() != '') {
					defaultB411 = current_Body.Event.Location.CityDetails.City.text().trim().toUpperCase()
				} else if (current_Body.Event?.Location?.LocationName?.text().trim() != '') {
					defaultB411 = current_Body.Event.Location.LocationName.text().trim().toUpperCase()
				} else if (current_Body.Event?.Location?.LocationCode?.UNLocationCode?.text().trim() != '') {
					defaultB411 = current_Body.Event.Location.LocationCode.UNLocationCode.text().trim().toUpperCase()
				}

				B406 = defaultB406
				B411 = defaultB411
				B412 = defaultB412

				//APLL special logic
				if (['CS020', 'CS040', 'CS310'].contains(vCS1EventFirst5)) {
					if (current_Body.Route.POR.CityDetails.LocationCode.UNLocationCode) {
						B406 = current_Body.Route.POR.CityDetails.LocationCode.UNLocationCode.text()
						B412 = 'UN'
					} else if (current_Body.Route.POR.CityDetails.LocationCode.SchedKDCode) {
						B406 = current_Body.Route.POR.CityDetails.LocationCode.SchedKDCode.text()
						B412 = current_Body.Route.POR.CityDetails.LocationCode.SchedKDType.text()
					}
					if (current_Body.Route.POR.CityDetails.City) {
						B411 = util.substring(current_Body.Route.POR.CityDetails.City?.text().toUpperCase(), 1, 24)
					}
				} else if (['CS160'].contains(vCS1EventFirst5)) {
					if (current_Body.Route.LastPOD.Port.LocationCode.UNLocationCode) {
						B406 = current_Body.Route.LastPOD.Port.LocationCode.UNLocationCode.text()
						B412 = 'UN'
					} else if (current_Body.Route.LastPOD.Port.LocationCode.SchedKDCode) {
						B406 = current_Body.Route.LastPOD.Port.LocationCode.SchedKDCode.text()
						B412 = current_Body.Route.LastPOD.Port.LocationCode.SchedKDType.text()
					}
					if (current_Body.Route.LastPOD.Port.PortName) {
						B411 = util.substring(current_Body.Route.LastPOD.Port.PortName.text().toUpperCase(), 1, 24)
					} else if (current_Body.Route.LastPOD.Port.City) {
						B411 = util.substring(current_Body.Route.LastPOD.Port.City.text().toUpperCase(), 1, 24)
					}
				}

				'E157_03' vCS1EventCodeConversion

				'E373_04' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), xmlDateTimeFormat, HHmm)

				if (B406){
					'E159_06' String.format('%-5s', B406)
				}
				
				if(current_Body.Container.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber?.text(), 1, 4)
				}
				
				if(current_Body.Container.ContainerNumber || current_Body.Container.ContainerCheckDigit){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber?.text(), 5, 6) + current_Body.Container.ContainerCheckDigit.text()
				}
				
				if (current_Body.Container?.ContainerStatus?.text().trim() == 'Empty') {
					'E578_09' 'E'
				} else if (current_Body.Container?.ContainerStatus?.text().trim() == 'Laden') {
					'E578_09' 'L'
				} else if (current_Body.Event?.CS1Event.text().trim() == 'CS210') {
					'E578_09' 'E'
				} else {
					'E578_09' 'L'
				}
				
				if(current_Body.Container.CarrCntrSizeType){
					'E24_10' util.substring(current_Body.Container?.CarrCntrSizeType?.text(), 1, 4)
				}
			
				if(B411){
					'E310_11' util.substring(B411, 1, 30)
				}
				
				if(B412){
					'E309_12' B412
				}
			}
			//Loop N9
			//for avoid duplicated N9
			def BMs = []
			def IBs = []
			def BNs = []
			def EQs = []
			def SNs = []
			def POs = []
			current_Body.BLGeneralInfo.each { current_BLGeneralInfo ->
				if (current_BLGeneralInfo.BLNumber && !BMs.contains(current_BLGeneralInfo.BLNumber.text()?.trim())) {
					BMs.add(current_BLGeneralInfo.BLNumber.text()?.trim())
					'N9' {
						'E128_01' 'BM'
						'E127_02' util.substring(current_BLGeneralInfo.BLNumber.text()?.trim(),1,30)
					}
				}
				if (current_BLGeneralInfo.CustomsReferenceType?.text()?.trim() == 'IT' && current_BLGeneralInfo.CustomsReferenceNumber && !IBs.contains(current_BLGeneralInfo.CustomsReferenceNumber.text()?.trim())) {
					IBs.add(current_BLGeneralInfo.CustomsReferenceNumber.text()?.trim())
					'N9' {
						'E128_01' 'IB'
						'E127_02' util.substring(current_BLGeneralInfo.CustomsReferenceNumber.text()?.trim(),1,30)
					}
				}
			}
			current_Body.BookingGeneralInfo.each { current_BookingGeneralInfo ->
				if (current_BookingGeneralInfo.CarrierBookingNumber && !BNs.contains(current_BookingGeneralInfo.CarrierBookingNumber.text()?.trim())) {
					BNs.add(current_BookingGeneralInfo.CarrierBookingNumber.text()?.trim())
					'N9' {
						'E128_01' 'BN'
						'E127_02' util.substring(current_BookingGeneralInfo.CarrierBookingNumber.text()?.trim(),1,30)
					}
				}
			}
			if ((current_Body.Container?.ContainerNumber || current_Body.Container?.ContainerCheckDigit) && !EQs.contains(current_Body.Container.ContainerNumber.text() + current_Body.Container?.ContainerCheckDigit.text())) {
				EQs.add(current_Body.Container.ContainerNumber.text() + current_Body.Container?.ContainerCheckDigit.text())
				'N9' {
					'E128_01' 'EQ'
					'E127_02' util.substring(util.substring(current_Body.Container.ContainerNumber.text(),1,10) + current_Body.Container?.ContainerCheckDigit.text(),1,30)
				}
			}
			current_Body.Container.Seal.each { current_Seal ->
				if (current_Seal.SealNumber.text() != '' && current_Seal.SealNumber?.text().trim().length() >= 4 && !SNs.contains(current_Seal.SealNumber?.text()?.trim())) {
					SNs.add(current_Seal.SealNumber?.text()?.trim())
					'N9' {
						'E128_01' 'SN'
						'E127_02' util.substring(current_Seal.SealNumber?.text()?.trim(),1,30)
					}
				}
			}
			current_Body.ExternalReference.findAll{it.CSReferenceType.text() == 'PO'}.each{ currentPO ->
				if (currentPO.ReferenceNumber && !POs.contains(currentPO.ReferenceNumber?.text())) {
					POs.add(currentPO.ReferenceNumber?.text())
					'N9' {
						'E128_01' 'PO'
						'E127_02' util.substring(currentPO.ReferenceNumber?.text(),1,30)
					}
				}
			}

			def partyCGN = current_Body.Party?.find{it.PartyType.text() == 'CGN' && it.PartyLevel.text() == ''}
			def partyCGNWithPartyLevel = current_Body.Party?.find{it.PartyType.text() == 'CGN' && it.PartyLevel.text() != ''}

			if (partyCGN?.CarrierCustomerCode) {
				'N9' {
					'E128_01' 'IC'
					'E127_02' util.substring(partyCGN?.CarrierCustomerCode?.text()?.trim(),1,30)
					// this should be bug in tibco
					if (partyCGN?.CarrierCustomerCode?.text().trim()?.length() == 10 && partyCGN?.PartyName) {
						'E369_03' partyCGN?.PartyName?.text()?.trim()
					}
				}
			} else if (partyCGNWithPartyLevel?.CarrierCustomerCode) {
				'N9' {
					'E128_01' 'IC'
					'E127_02' util.substring(partyCGNWithPartyLevel?.CarrierCustomerCode?.text()?.trim(),1,30)
					// this should be bug in tibco
					if (partyCGNWithPartyLevel?.CarrierCustomerCode?.text().trim()?.length() == 10) {
						'E369_03' partyCGNWithPartyLevel?.PartyName?.text()?.trim()
					}
				}
			}

			def firstOceanLeg = null
			def lastOceanLeg = null
			if(current_Body.Route.OceanLeg){
				firstOceanLeg = current_Body.Route.OceanLeg[0]
				lastOceanLeg = current_Body.Route.OceanLeg[-1]
			}
			if (shipDir == 'I') {
				'Q2' {
					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E597_01' util?.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber?.text(), 1, 7)
					}
					if (lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode) {
						'E26_02' lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode?.text()
					}
					def polDepartDtA = lastOceanLeg?.POL?.DepartureDT?.find{it?.@Indicator == 'A'}?.LocDT
					def polDepartDtE = lastOceanLeg?.POL?.DepartureDT?.find{it?.@Indicator == 'E'}?.LocDT
					if (polDepartDtA) {
						'E373_04' util?.convertDateTime(polDepartDtA?.text(), xmlDateTimeFormat, yyyyMMdd)
					} else if (polDepartDtE) {
						'E373_04' util?.convertDateTime(polDepartDtE?.text(), xmlDateTimeFormat, yyyyMMdd)
					}
					def podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT?.find{it?.@Indicator == 'A'}?.LocDT
					def podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT?.find{it?.@Indicator == 'E'}?.LocDT
					if (podArrivalDtA) {
						'E373_05' util?.convertDateTime(podArrivalDtA?.text(), xmlDateTimeFormat, yyyyMMdd)
					} else if (podArrivalDtE) {
						'E373_05' util?.convertDateTime(podArrivalDtE?.text(), xmlDateTimeFormat, yyyyMMdd)
					}
					if (lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction) {
						'E55_09' lastOceanLeg?.SVVD?.Discharge?.Voyage?.text() + lastOceanLeg?.SVVD?.Discharge?.Direction?.text()
					}
					'E127_11' current_Body?.GeneralInformation?.SCAC?.text()
					'E897_12' 'L'
					if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
						'E182_13' lastOceanLeg?.SVVD?.Discharge?.VesselName?.text()
					}
				}
			} else if (shipDir == 'O') {
				'Q2' {
					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber?.text(), 1, 7)
					}
					if (firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode) {
						'E26_02' firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode?.text()
					}
					def polDepartureDtA = lastOceanLeg?.POL?.DepartureDT?.find{it?.@Indicator == 'A'}?.LocDT
					def polDepartureDtE = lastOceanLeg?.POL?.DepartureDT?.find{it?.@Indicator == 'E'}?.LocDT
					if (polDepartureDtA) {
						'E373_04' util?.convertDateTime(polDepartureDtA?.text(), xmlDateTimeFormat, yyyyMMdd)
					} else if (polDepartureDtE) {
						'E373_04' util?.convertDateTime(polDepartureDtE?.text(), xmlDateTimeFormat, yyyyMMdd)
					}

					def podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT?.find{it?.@Indicator == 'A'}?.LocDT
					def podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT?.find{it?.@Indicator == 'E'}?.LocDT
					if (podArrivalDtA) {
						'E373_05' util?.convertDateTime(podArrivalDtA?.text(), xmlDateTimeFormat, yyyyMMdd)
					} else if (podArrivalDtE) {
						'E373_05' util?.convertDateTime(podArrivalDtE?.text(), xmlDateTimeFormat, yyyyMMdd)
					}
					if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
						'E55_09' firstOceanLeg?.SVVD?.Loading?.Voyage?.text() + firstOceanLeg?.SVVD?.Loading?.Direction?.text()
					}
					'E127_11' current_Body?.GeneralInformation?.SCAC?.text()
					'E897_12' 'L'
					if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' firstOceanLeg?.SVVD?.Loading?.VesselName?.text()
					}
				}
			}

			//R4
			def POR =  current_Body.Route.POR[0]
			def firstPOL =  current_Body.Route.FirstPOL
			def lastPOD =  current_Body.Route.LastPOD
			def FND =  current_Body.Route.FND[0]
			if(POR){
				'Loop_R4' {
					'R4' {
						'E115_01' 'R'
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType?.text()
						}
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode?.text()
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.SchedKDCode?.text()
						}
						if (POR?.CityDetails?.City) {
							'E114_04' util?.substring(POR?.CityDetails?.City?.text()?.toUpperCase(), 1, 24)
						}
						if (POR?.CSStandardCity?.CSCountryCode?.text()?.length() >= 2){
							'E26_05' POR?.CSStandardCity?.CSCountryCode?.text()
						}
						if (POR?.CSStandardCity?.CSStateCode) {
							'E156_08' POR?.CSStandardCity?.CSStateCode?.text()
						}
					}
	
					def porDTM = null
					def isAct = false
					if (current_Body.Route.CargoReceiptDT.find{it.@Indicator == 'A'}?.LocDT) {
						porDTM = current_Body.Route.CargoReceiptDT.find{it.@Indicator == 'A'}?.LocDT
						isAct = true
					} else if (current_Body.Route.FullPickupDT.find{it.@Indicator == 'E'}?.LocDT && current_Body.BookingGeneralInfo.Haulage.OutBound?.text() == 'C') {
						porDTM = current_Body.Route.FullPickupDT.find{it.@Indicator == 'E'}?.LocDT
					} else if (current_Body.Route.FullReturnCutoffDT.find{it.@Indicator == 'A'}?.LocDT && current_Body.BookingGeneralInfo.Haulage.OutBound?.text() == 'M') {
						porDTM = current_Body.Route.FullReturnCutoffDT.find{it.@Indicator == 'A'}?.LocDT
					}
					if (porDTM) {
						'DTM' {
							if (isAct) {
								'E374_01' '370'
							} else {
								'E374_01' '369'
							}
							'E373_02' util.convertDateTime(porDTM?.text(), xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(porDTM?.text(), xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
	
				}
			}
			if(firstPOL){
				'Loop_R4' {
	
					//APLL special logic
					def R404L = null
					if (['CS070'].contains(vCS1EventFirst5)) {
						R404L = B411
					} else {
						if (firstPOL?.Port?.PortName) {
							R404L = util?.substring(firstPOL?.Port?.PortName?.text()?.toUpperCase(), 1, 24)
						} else if (firstPOL?.Port?.City) {
							R404L = util?.substring(firstPOL?.Port?.City?.text()?.toUpperCase(), 1, 24)
						}
					}
	
					'R4' {
						'E115_01' 'L'
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E309_02' firstPOL?.Port?.LocationCode?.SchedKDType?.text()
						}
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.UNLocationCode?.text()
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.SchedKDCode?.text()
						}
						if (R404L) {
							'E114_04' R404L
						}
						if (firstPOL?.Port?.CSCountryCode?.text()?.length() >= 2) {
							'E26_05' firstPOL?.Port?.CSCountryCode?.text()
						}
						if (firstPOL?.CSStateCode) {
							'E156_08' firstPOL?.CSStateCode?.text()
						}
					}
					def polDepartureDtA = firstOceanLeg?.POL?.DepartureDT.find{it.@Indicator == 'A'}?.LocDT
					def polDepartureDtE = firstOceanLeg?.POL?.DepartureDT.find{it.@Indicator == 'E'}?.LocDT
					if (polDepartureDtA) {
						'DTM' {
							'E374_01' '370'
							'E373_02' util.convertDateTime(polDepartureDtA?.text(), xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtA?.text(), xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					} else if (polDepartureDtE) {
						'DTM' {
							'E374_01' '369'
							'E373_02' util.convertDateTime(polDepartureDtE?.text(), xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtE?.text(), xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
				}
			}
			def R402D = null
			def R403D = null
			def R404D = null
			def R405D = null
			def R406D = null
			def R408D = null

			if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
				R402D = 'UN'
			} else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
				R402D = lastPOD?.Port?.LocationCode?.SchedKDType?.text()
			}
			if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
				R403D = lastPOD?.Port?.LocationCode?.UNLocationCode?.text()
			} else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
				R403D = lastPOD?.Port?.LocationCode?.SchedKDCode?.text()
			}
			if (lastPOD?.Port?.PortName) {
				R404D = util?.substring(lastPOD?.Port?.PortName?.text()?.toUpperCase(), 1, 24)
			} else if (lastPOD?.Port?.City) {
				R404D = util?.substring(lastPOD?.Port?.City?.text()?.toUpperCase(), 1, 24)
			}
			if (lastPOD?.Port?.CSCountryCode?.text()?.length() >= 2) {
				R405D = lastPOD?.Port?.CSCountryCode?.text()
			}
			if (lastPOD?.Facility?.FacilityName) {
				R406D = util?.substring(lastPOD?.Facility?.FacilityName?.text(), 1, 30)?.trim()
			} else {
				R406D = lastPOD?.Port?.LocationCode?.UNLocationCode?.text()?.trim()
			}
			if (lastPOD?.CSStateCode) {
				R408D = lastPOD?.CSStateCode?.text()
			}

			if ((R403D || R404D) && !['CS160', 'CS120'].contains(vCS1EventFirst5)) {
				'Loop_R4' {
					'R4' {
						'E115_01' 'D'
						if (R402D) {
							'E309_02' R402D
						}
						if (R403D) {
							'E310_03' R403D
						}
						if (R404D) {
							'E114_04' R404D
						}
						if (R405D) {
							'E26_05' R405D
						}
						if (R406D) {
							'E174_06' R406D
						}
						if (R408D) {
							'E156_08' R408D
						}
					}
					def podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT.find{it.@Indicator == 'A'}?.LocDT
					def podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT.find{it.@Indicator == 'E'}?.LocDT
					if (podArrivalDtA) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDtA?.text(), xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtA?.text(), xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					} else if (podArrivalDtE) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDtE?.text(), xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtE?.text(), xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
					if (current_Body.BLGeneralInfo[0]?.CustomsReferenceNumber && current_Body.BLGeneralInfo[0]?.CustomsReferenceType?.text() == 'IT') {
						if (podArrivalDtA) {
							'DTM' {
								'E374_01' '059'
								'E373_02' util.convertDateTime(podArrivalDtA?.text(), xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(podArrivalDtA?.text(), xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						} else if (podArrivalDtE) {
							'DTM' {
								'E374_01' '059'
								'E373_02' util.convertDateTime(podArrivalDtE?.text(), xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(podArrivalDtE?.text(), xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						}
					}
				}
			}

			def R402E = null
			def R403E = null
			def R404E = null
			def R405E = null
			def R408E = null
			
			if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
				R402E = 'UN'
			} else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
				R402E = FND?.CityDetails?.LocationCode?.SchedKDType?.text()
			}
			if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
				R403E = FND?.CityDetails?.LocationCode?.UNLocationCode?.text()
			} else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
				R403E = FND?.CityDetails?.LocationCode?.SchedKDCode?.text()
			}
			if (FND?.CityDetails?.City) {
				R404E = util?.substring(FND?.CityDetails?.City?.text()?.toUpperCase(), 1, 24)
			}
			if (FND?.CSStandardCity?.CSContinentCode?.text()?.length() >= 2) {
				R405E = FND?.CSStandardCity?.CSCountryCode?.text()
			}
			if (FND?.CSStandardCity?.CSStateCode) {
				R408E = FND?.CSStandardCity?.CSStateCode?.text()
			}


			if (vCS1EventFirst5 == 'CS190') {

				if (B412) {
					R402E = B412
				}
				if (B406) {
					R403E = B406
				}
				if (B411) {
					R404E = B411
				}
				if (B406 && current_Body.Event.Location.CSStandardCity.CSCountryCode) {
					R405E = current_Body.Event.Location.CSStandardCity.CSCountryCode.text()
				}
				if (B406 && current_Body.Event.Location.CityDetails.State) {
					R408E = util.substring(current_Body.Event.Location.CityDetails.State.text(), 1, 2)?.toUpperCase()
				}
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						if (R402E) {
							'E309_02' R402E
						}
						if (R403E) {
							'E310_03' R403E
						}
						if (R404E) {
							'E114_04' R404E
						}
						if (R405E) {
							'E26_05' R405E
						}
						if (R408E) {
							'E156_08' R408E
						}
					}
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}
			} else if(FND) {
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						if (R402E) {
							'E309_02' R402E
						}
						if (R403E) {
							'E310_03' R403E
						}
						if (R404E) {
							'E114_04' R404E
						}
						if (R405E) {
							'E26_05' R405E
						}
						if (R408E) {
							'E156_08' R408E
						}
					}
					def fndDatetime = null
					def isAct = null
					if (current_Body.Route.CargoDeliveryDT.find{it.@Indicator == 'A'}?.LocDT?.text()) {
						fndDatetime = current_Body.Route.CargoDeliveryDT.find{it.@Indicator == 'A'}?.LocDT?.text()
						isAct = true
					} else if (current_Body.Route.CargoDeliveryDT.find{it.@Indicator == 'E'}?.LocDT?.text()) {
						fndDatetime = current_Body.Route.CargoDeliveryDT.find{it.@Indicator == 'E'}?.LocDT?.text()
						isAct = false
					} else if (current_Body.Route.ArrivalAtFinalHub.find{it.@Indicator == 'A'}?.LocDT?.text()) {
						fndDatetime = current_Body.Route.ArrivalAtFinalHub.find{it.@Indicator == 'A'}?.LocDT?.text()
						isAct = true
					} else if (FND?.ArrivalDT.find{it.@Indicator[0] == 'A'}?.LocDT?.text() && current_Body.GeneralInformation.SCAC.text() != 'OOLU') {
						fndDatetime = FND?.ArrivalDT.find{it.@Indicator[0] == 'A'}?.LocDT?.text()
						isAct = true
					} else if (current_Body.Route.ArrivalAtFinalHub.find{it.@Indicator == 'E'}?.LocDT?.text()) {
						fndDatetime = current_Body.Route.ArrivalAtFinalHub.find{it.@Indicator == 'E'}?.LocDT?.text()
						isAct = false
					} else if (FND?.ArrivalDT.find{it.@Indicator[0] == 'E'}?.LocDT?.text() && current_Body.GeneralInformation.SCAC.text() != 'OOLU') {
						fndDatetime = FND?.ArrivalDT.find{it.@Indicator[0] == 'E'}?.LocDT?.text()
						isAct = false
					}
					if (fndDatetime) {
						'DTM' {
							if (isAct) {
								'E374_01' '140'
							} else {
								'E374_01' '139'
							}
							'E373_02' util.convertDateTime(fndDatetime, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(fndDatetime, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
				}
			}
			//special logic
			if (['CS200', 'CS195'].contains(vCS1EventFirst5)) {
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						if (B412) {
							'E309_02' B412
						}
						if (B406) {
							'E310_03' B406
						}
						if (B411) {
							'E114_04' B411
						}
						if (B412 == 'UN') {
							'E26_05' util.substring(B406, 1, 2)
						}
					}
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}
			}
			if ((R403D || R404D) && ['CS160', 'CS120'].contains(vCS1EventFirst5)) {
				'Loop_R4' {
					'R4' {
						'E115_01' '1'
						if (R402D) {
							'E309_02' R402D
						}
						if (R403D) {
							'E310_03' R403D
						}
						if (R404D) {
							'E114_04' R404D
						}
						if (R405D) {
							'E26_05' R405D
						}
						if (R406D) {
							'E174_06' R406D
						}
						if (R408D) {
							'E156_08' R408D
						}
					}
					def podArrivalDtA = lastOceanLeg.POD.ArrivalDT.find{it.@Indicator == 'A'}?.LocDT
					def podArrivalDtE = lastOceanLeg.POD.ArrivalDT.find{it.@Indicator == 'E'}?.LocDT
					if (podArrivalDtA) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDtA.text(), xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtA.text(), xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					} else if (podArrivalDtE) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDtE.text(), xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtE.text(), xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
				}
			}
			if ((R403D || R404D) && vCS1EventFirst5 == 'CS277') {
				'Loop_R4' {
					'R4' {
						'E115_01' '1'
						if (R402D) {
							'E309_02' R402D
						}
						if (R403D) {
							'E310_03' R403D
						}
						if (R404D) {
							'E114_04' R404D
						}
						if (R405D) {
							'E26_05' R405D
						}
						if (R406D) {
							'E174_06' R406D
						}
						if (R408D) {
							'E156_08' R408D
						}
					}
					if (current_Body.Event.EventDT.LocDT) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
			'SE' {
				//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
				'E96_01' ' '
				'E329_02' currentSTCtrlNum
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
		def parser = new XmlParser()
		parser.setNamespaceAware(false);
		def ContainerMovement = parser.parseText(inputXmlBody);

		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
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
		def T315 = outXml.createNode('T315')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.


		//BeginWorkFlow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def outputBodyCount = 0
		def currentSystemDt = new Date()
		//def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
		def headerMsgDT = util.convertDateTime(ContainerMovement.Header.MsgDT.LocDT.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		def scacTpId = getSCACTpId(ContainerMovement.Body.GeneralInformation.SCAC[0].text(), conn)
		//long ediIsaCtrlNumber = ctrlNos[0]
		//long ediGroupCtrlNum = ctrlNos[1]
		def txnErrorKeys = []
		
		//build ISA, GS
		generateEDIHeader(outXml, appSessionId, ContainerMovement, currentSystemDt)

		//duplication -- CT special logic
//		def duplicatedPairs = ['CS190':'CS190D', 'CS170':'CS170RL', 'CS277':'CS277AG']
		def duplicatedPairs = getDuplicatedPairs(TP_ID)
		
		def blDupBodies = CTBLDuplication(ContainerMovement.Body)
		def bodies = CTEventDuplication(blDupBodies, duplicatedPairs)

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event.text()
			def eventExtCde = util.getConversion('DSGOODS', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

			//prep checking
			def errorKeys = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

			if (errorKeys.size == 0) {
				//pass validateBeforeExecution
				//main mapping
				generateBody(outXml, current_Body, outputBodyCount, currentSystemDt)
				outputBodyCount++
			}

			//posp checking
			pospValidation()

			buildCsupload(csuploadXml, errorKeys,String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID.text())?.replace(" ", "0"))
			buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeys, headerMsgDT, scacTpId, eventExtCde)

			txnErrorKeys.add(errorKeys);
		}

		//build GE, IEA
		generateEDITail(outXml, outputBodyCount)

		//EndWorkFlow

		//End root node
		outXml.nodeCompleted(null,T315)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

//			println bizKeyWriter.toString();
//			println csuploadWriter.toString();

		//promote csupload and bizkey to session
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizKeyWriter?.toString());
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_CSUPLOAD', csuploadWriter?.toString());

		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result

			//call get ctrl num
			def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
			long ediIsaCtrlNumber = ctrlNos[0]
			long ediGroupCtrlNum = ctrlNos[1]
			
			long ediSTCtrlNum = ediGroupCtrlNum
			
			if (ediSTCtrlNum > 99999) {
				ediSTCtrlNum = ediSTCtrlNum % 100000;
			}
			
			result = writer?.toString()?.replace('###edi_Isa_Ctrl_Number###',String.format("%09d", ediIsaCtrlNumber))?.replace('###edi_Group_Ctrl_Number###',(String)ediGroupCtrlNum).replace("###edi_ST_Ctrl_Number###",(String)ediSTCtrlNum);
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()

		return result;
	}

	private void generateEDIHeader(MarkupBuilder outXml, def appSessionId, Node ContainerMovement, def currentSystemDt) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID.text());
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC?.text());

		outXml.'ISA' {
			'I01_01' '00'
			'I02_02' '          '
			'I03_03' '00'
			'I04_04' '          '
			'I05_05' 'ZZ'
			'I06_06' 'CARGOSMART     '
			'I05_07' 'ZZ'
			'I07_08' 'APLUNET        '
			'I08_09' currentSystemDt.format("yyMMdd")
			'I09_10' currentSystemDt.format(HHmm)
			'I10_11' 'U'
			'I11_12' '00401'
			'I12_13' '###edi_Isa_Ctrl_Number###'
			'I13_14' '0'
			'I14_15' 'P'
			'I15_16' '>'
		}
		outXml.'GS' {
			'E479_01' 'QO'
			'E142_02' 'CARGOSMART'
			'E124_03' 'APLUNET'
			'E373_04' currentSystemDt.format(yyyyMMdd)
			'E337_05' currentSystemDt.format(HHmm)
			'E28_06' '###edi_Group_Ctrl_Number###'
			'E455_07' 'X'
			'E480_08' '004010'
		}

	}

	private def prepValidation(def current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> csuploadErrorKey = new ArrayList<Map<String,String>>();
		def errorKey = null

		// error cases
		if (!util.notEmpty(eventExtCde)) {
			errorKey = ['Type':'ES','Value':eventCS2Cde + ' - Missing Status Code','IsError':'YES']
			csuploadErrorKey.add(errorKey);
		}
		if (!util.notEmpty(current_Body.Event.EventDT.LocDT.text())) {
			errorKey = ['Type':'ES','Value':eventCS2Cde + ' - Missing Status Event Date','IsError':'YES']
			csuploadErrorKey.add(errorKey);
		}
		if (current_Body.Container.findAll{util.notEmpty(it?.ContainerNumber?.text())}.size == 0) {
			errorKey = ['Type':'EC','Value':eventCS2Cde + ' - Missing Container Number','IsError':'YES']
			csuploadErrorKey.add(errorKey);
		}

		//obsolete cases

		if (current_Body.Event.CS1Event.text() == 'CS130' || current_Body.Event.CS1Event.text() == 'CS140') {
			errorKey = ['Type':'C','Value':eventCS2Cde + ' - Event not subscribed by Customer','IsError':'NO']
			csuploadErrorKey.add(errorKey);
		}
		if (current_Body.BLGeneralInfo.findAll{util.notEmpty(it?.BLNumber?.text())}.size == 0) {
			errorKey = ['Type':'EC','Value':eventCS2Cde + ' - Missing BL Number','IsError':'NO']
			csuploadErrorKey.add(errorKey);
		}
		if (current_Body.Party.findAll{it.PartyType.text() == 'CGN' && util.notEmpty(it.CarrierCustomerCode.text())}.size == 0) {
			errorKey = ['Type':'C','Value':eventCS2Cde + ' - Missing Consignee Code','IsError':'NO']
			csuploadErrorKey.add(errorKey);
		}
		if (current_Body.Event.CS1Event.text() == 'CS260' && current_Body.Route.Inbound_intermodal_indicator.text() == '1') {
			errorKey = ['Type':'C','Value':eventCS2Cde + ' - Ignore (I/B intermodal)','IsError':'NO']
			csuploadErrorKey.add(errorKey);
		}

		return csuploadErrorKey;
	}

	private boolean pospValidation() {

	}

	private void generateEDITail(MarkupBuilder outXml, def outputBodyCount) {
		outXml.'GE' {
			'E97_01' outputBodyCount
			'E28_02' '###edi_Group_Ctrl_Number###'			
		}
		outXml.'IEA' {
			'I16_01' '1'
			'I12_02' '###edi_Isa_Ctrl_Number###'			
		}
	}

	private buildBizKey(MarkupBuilder bizKeyXml, def current_Body,int current_BodyIndex, def csuploadErrorKey, def Interchange, def scacTpId, def eventExtCde) {

		def appErrorReportError = csuploadErrorKey.find{it.IsError == 'YES'}
		def appErrorReportObsolete = csuploadErrorKey.find{it.IsError == 'NO'}

		def blBizKey = []
		def bkBizKey = []
		def crnBizKey = []
		def eventExtCdeBizKey = []
		def eventDateTimeBizKey = []
		def eventPortIdBizKey = []
		def eventPortNameBizKey = []
		def evnetCityIdBizKey = []
		def eventCityNameBizKey = []
		def evnetCntryCdeBizKey = []
		def partyType = ['ANP', 'BPT', 'CCP', 'CGN', 'FWD', 'NPT', 'OTH', 'SHP']

		current_Body.BLGeneralInfo.each { current_BLGeneralInfo ->
			if (util.notEmpty(current_BLGeneralInfo.BLNumber.text())) {
				blBizKey.add(['BL_NUM':current_BLGeneralInfo.BLNumber.text()])
			}
		}
		current_Body.BookingGeneralInfo.each { current_BookingGeneralInfo ->
			if (util.notEmpty(current_BookingGeneralInfo.CarrierBookingNumber.text())) {
				bkBizKey.add(['BK_NUM':current_BookingGeneralInfo.CarrierBookingNumber.text()])
			}
		}
		current_Body.Party.each { current_Party ->
			if (util.notEmpty(current_Party.CarrierCustomerCode.text()) && partyType.contains(current_Party.PartyType.text())) {
				crnBizKey.add(['CRN':current_Party.CarrierCustomerCode.text()])
			}
		}
		if (util.notEmpty(eventExtCde)) {
			eventExtCdeBizKey.add(['CT_Event_ext_cde':eventExtCde])
		}
		def eventInfo = getCS2EventInfo(current_Body.Event.Location.CityDetails.City.text(),current_Body.Event.Location.LocationCode.UNLocationCode.text(), current_Body.Event.Location.CSStandardCity.CSCountryCode.text(), conn)
		if (util.notEmpty(current_Body.Event.EventDT.LocDT.text())) {
			eventDateTimeBizKey = ['CT_Event_datetime':util.convertDateTime(current_Body.Event.EventDT.LocDT.text(),"yyyy-MM-dd'T'HH:mm:ss", 'yyyy-MM-dd HH:mm:ss')]
		}
		if (util.notEmpty(eventInfo.CT_Event_port_id)) {
			eventPortIdBizKey = ['CT_Event_port_id':eventInfo.CT_Event_port_id]
		}
		if (util.notEmpty(eventInfo.CT_Event_port_nme)) {
			eventPortNameBizKey = ['CT_Event_port_nme':eventInfo.CT_Event_port_nme]
		}
		if (util.notEmpty(eventInfo.CT_Event_city_id)) {
			evnetCityIdBizKey = ['CT_Event_city_id':eventInfo.CT_Event_city_id]
		}
		if (util.notEmpty(eventInfo.CT_Event_city_nme)) {
			eventCityNameBizKey = ['CT_Event_city_nme':eventInfo.CT_Event_city_nme]
		}
		if (util.notEmpty(eventInfo.CT_Event_cntry_cde)) {
			evnetCntryCdeBizKey = ['CT_Event_cntry_cde':eventInfo.CT_Event_cntry_cde]
		}
		
		def bizKey = [
					['CNTR_NUM': util.substring(current_Body.Container.ContainerNumber.text(),1,10)],
					['STPID':scacTpId],['RTPID':TP_ID],
					['LKP':current_Body.GeneralInformation.SCAC.text() + ',' + util.substring(current_Body.Container.ContainerNumber.text(),1,10)  +','+ current_Body.Event.CS1Event.text() + ','+ util.convertDateTime(current_Body.Event.EventDT.LocDT.text(),"yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')],
				]
		bizKey.addAll(blBizKey)
		bizKey.addAll(bkBizKey)
		bizKey.addAll(crnBizKey)
		bizKey.addAll(eventExtCdeBizKey)
		bizKey.addAll(eventDateTimeBizKey)
		bizKey.addAll(eventPortIdBizKey)
		bizKey.addAll(eventPortNameBizKey)
		bizKey.addAll(evnetCityIdBizKey)
		bizKey.addAll(eventCityNameBizKey)
		bizKey.addAll(evnetCntryCdeBizKey)

		

		bizKeyXml.'ns0:Transaction'('xmlns:ns0':'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
			'ns0:ControlNumberInfo' {
				'ns0:Interchange' Interchange
				'ns0:Group' String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID.text())?.replace(" ","0");
				'ns0:Transaction' current_BodyIndex + 1
			}
			bizKey.each{ currentBizKeyMap ->
				currentBizKeyMap.each{ key, value ->
					'ns0:BizKey'{
						'ns0:Type' key
						'ns0:Value' value
					}
				}
			}
			'ns0:CarrierId' getCarrierID(current_Body?.GeneralInformation?.SCAC?.text(), conn)
			'ns0:CTEventTypeId' util.substring(current_Body.Event.CS1Event.text(), 1, 5)

			if (csuploadErrorKey.size != 0) {
				'ns1:AppErrorReport'('xmlns:ns1':'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
					if (appErrorReportError != null) {
						'ns1:Status' 'E'
					} else {
						'ns1:Status' 'O'
					}
					'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'	//max length is 20, exceed will missing error bizkey
					if (appErrorReportError != null) {
						'ns1:Msg' appErrorReportError?.Value
					} else {
						'ns1:Msg' appErrorReportObsolete?.Value
					}
					'ns1:Severity' '5'
				}
			}
		}
	}

	private buildCsupload(MarkupBuilder csuploadXml, List csuploadErrorKey,def Interchange) {

		def currentSystemDt = new Date()

		csuploadXml.'ns0:CSUploadKeyTrack'('xmlns:ns0':'http://www.tibco.com/schemas/message-processing/Schemas/System/CSUploadKeyTrack.xsd') {
			'ns0:ErrorID' 'SEND_TO_TP'
			if (csuploadErrorKey.isEmpty()) {
				'ns0:CSUploadErrorKey'{
					'ns0:Type' 'C'
					'ns0:Value' MSG_REQ_ID + ".request"
					'ns0:IsError' 'NO'
				}
			} else {
				csuploadErrorKey.each{ currentErrorKey ->
					'ns0:CSUploadErrorKey' {
						'ns0:Type' currentErrorKey?.Type
						'ns0:Value' currentErrorKey?.Value
						'ns0:IsError' currentErrorKey?.IsError
					}
				}
			}
			'ns0:CSUploadInfoKey' {
				'ns0:Type' 'SHMTQUEUEID'
				'ns0:Value' Interchange
			}
			'ns0:CSUploadInfoKey' {
				'ns0:Type' 'ACTIVITYSTARTDT'
				'ns0:Value' currentSystemDt.format("yyyyMMddHHmmss")
			}
		}

	}

	String getSCACTpId(String scac, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select tp_id from b2b_scac_tp_map  where SCAC = ?";

		try {
			pre = conn.prepareStatement(sql);
			//		pre.setMaxRows(getDBRowLimit());
			//		pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setMaxRows(1000);
			pre.setQueryTimeout(10);
			pre.setString(1, scac);
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

	Map<String,String> getCS2EventInfo(String cityName, String unlocationCode, String countryCode,Connection conn) {

		if (conn == null)
			return [:];

		def ret = [:];
		PreparedStatement pre = null;
		ResultSet result = null;
		PreparedStatement pre2 = null;
		ResultSet result2 = null;
		String sql1 = "select STND_PORT_ID, PORT_NAME from cs2_port_list where  (UPPER(port_name) = UPPER(?) or UN_LOCATION_CODE =?) and port_type ='S' and rownum = 1";
		String sql2 = "select stnd_city_id, city_nme, cntry_nme,cntry_cde, un_locn_cde from cs2_master_city where (UN_LOCN_CDE = ? or (UPPER(city_nme) = UPPER(?) and CNTRY_CDE = ? )) and city_type = 'S' and rownum=1";
		try {
			pre = conn.prepareStatement(sql1);
			//		pre.setMaxRows(getDBRowLimit());
			//		pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setMaxRows(1000);
			pre.setQueryTimeout(10);
			pre.setString(1, cityName);
			pre.setString(2, unlocationCode);
			result = pre.executeQuery();

			if (result.next()) {
				ret.put('CT_Event_port_id', result.getString(1))
				ret.put('CT_Event_port_nme', result.getString(2))
			}

			pre2 = conn.prepareStatement(sql2);
			//		pre2.setMaxRows(getDBRowLimit());
			//		pre2.setQueryTimeout(getDBTimeOutInSeconds());
			pre2.setMaxRows(1000);
			pre2.setQueryTimeout(10);
			pre2.setString(1, unlocationCode);
			pre2.setString(2, cityName);
			pre2.setString(3, countryCode);
			result2 = pre2.executeQuery();

			if (result2.next()) {
				ret.put('CT_Event_city_id', result2.getString(1))
				ret.put('CT_Event_city_nme', result2.getString(2))
				ret.put('CT_Event_cntry_cde', result2.getString(4))
			}

		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
			if (result2 != null)
				result2.close();
			if (pre2 != null)
				pre2.close();
		}
		return ret;
	}

	String getCarrierID(String scac, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select c.id from b2b_ocean_carrier c where c.scac = ?";

		try {
			pre = conn.prepareStatement(sql);
			//		pre.setMaxRows(getDBRowLimit());
			//		pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setMaxRows(1000);
			pre.setQueryTimeout(10);
			pre.setString(1, scac);
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

	private def CTEventDuplication(def Body, def duplicatedPairs) {

		def bodies = [];

		Body.eachWithIndex { current_Body, current_BodyIndex ->
			bodies.add(current_Body)
			if (duplicatedPairs.get(current_Body.Event.CS1Event.text())) {
				def duplBody = current_Body.clone()
				duplBody.Event.CS1Event[0].setValue(duplicatedPairs.get(current_Body.Event.CS1Event.text()))
				bodies.add(duplBody)
			}
		}
		
		return bodies;
	}

	private def CTBLDuplication(def Body) {
		def bodies = [];

		Body.eachWithIndex { current_Body, current_BodyIndex ->
			def blCount = current_Body.BLGeneralInfo.size()
			if ( blCount > 1 ) {
				for ( i in 0..<blCount ) {
					def duplBody = current_Body.clone()
					for ( j in 0..<blCount ) {
						if ( i < j ) {
							duplBody.remove(duplBody.BLGeneralInfo[1])
						} else if ( i > j ) {
							duplBody.remove(duplBody.BLGeneralInfo[0])
						}
					}
					bodies.add(duplBody)
				}
			} else {
				bodies.add(current_Body)
			}

		}

		return bodies
	}

	private def getDuplicatedPairs(String tp_id){
		def result = [:]
		if (conn == null)
			return result;

		String key = null;
		String value = null;
		PreparedStatement pre = null;
		ResultSet resultSet = null;
		String sql = "select substr(int_cde,0,5),int_cde from b2b_cde_conversion where tp_id = ? and convert_type_id = 'EventStatusCode' and msg_type_id = 'CT' and substr(int_cde,6) = ext_cde order by int_cde";
	
		try {
			pre = conn.prepareStatement(sql);
			//		pre.setMaxRows(getDBRowLimit());
			//		pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setMaxRows(1000);
			pre.setQueryTimeout(10);
			pre.setString(1, tp_id);
			resultSet = pre.executeQuery();
	
			while(resultSet.next()) {
				key = resultSet.getString(1);
				value = resultSet.getString(2);
				result.put(key,value)
			}
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (pre != null)
				pre.close();
		}
		return result;
		
	}
	
}






