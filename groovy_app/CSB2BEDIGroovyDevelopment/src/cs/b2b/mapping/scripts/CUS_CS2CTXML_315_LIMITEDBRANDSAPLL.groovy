package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser

/** 
 * LIMITEDBRANDSAPLL reference DSGOODS CT 
 * initialize on 20170112 
 */
public class CS2XML_CT_To_LIMITEDBRANDSAPLL_X12_Mapping {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_CT_O_Common ctUtil = new cs.b2b.core.mapping.util.MappingUtil_CT_O_Common(util);

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

	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event, 1, 5)
		def vCS1EventCodeConversion = util.getConversion('LIMITEDBRANDSAPLL', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir = util.getEDICdeReffromIntCde('LIMITEDBRANDSAPLL','ShipDirection', 'O', 'INTCTXML','Q2','EventStatusCode', vCS1EventFirst5,conn);
		def B406 = null
		def B411 = null
		def B412 = null
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {

				def defaultB406 = current_Body.Event?.Location?.LocationCode?.UNLocationCode
				def defaultB411 = ''
				def defaultB412 = 'UN'

				if (current_Body.Event.Location?.CityDetails?.City) {
					defaultB411 = current_Body?.Event?.Location?.CityDetails?.City?.trim().toUpperCase()
				} else if (current_Body.Event?.Location?.LocationName) {
					defaultB411 = current_Body?.Event?.Location?.LocationName?.trim().toUpperCase()
				} else if (current_Body.Event?.Location?.LocationCode?.UNLocationCode) {
					defaultB411 = current_Body.Event?.Location?.LocationCode?.UNLocationCode?.trim().toUpperCase()
				}
	
				B406 = defaultB406
				B411 = defaultB411
				B412 = defaultB412

				//APLL special logic
				if (['CS040'].contains(vCS1EventFirst5)) {
					if (current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						B406 = current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode	
						B412 = 'UN'
					} else if (current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode) {
						B406 = current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode
						B412 = current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDType
					}
					if (current_Body.Route?.POR?.CityDetails?.City) {
						B411 = util.substring(current_Body?.Route?.POR?.CityDetails.City?.toUpperCase(), 1, 24)
					}
				} else if (['CS160'].contains(vCS1EventFirst5)) {
					if (current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
						B406 = current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode
						B412 = 'UN'
					} else if (current_Body.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) {
						B406 = current_Body.Route.LastPOD.Port.LocationCode?.SchedKDCode
						B412 = current_Body.Route.LastPOD.Port.LocationCode?.SchedKDType
					}
					if (current_Body.Route?.LastPOD?.Port?.PortName) {
						B411 = util.substring(current_Body.Route.LastPOD.Port.PortName.toUpperCase(), 1, 24)
					} else if (current_Body.Route?.LastPOD?.Port?.City) {
						B411 = util.substring(current_Body.Route.LastPOD.Port.City.toUpperCase(), 1, 24)
					}
				}

				'E157_03' vCS1EventCodeConversion

		    	'E373_04' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
				'E161_05' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)

				if (B406) {
					'E159_06' String.format('%-5s', B406)
				}	
				if (current_Body.Container?.ContainerNumber) {
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
				}

				if(current_Body.Container?.ContainerNumber || current_Body.Container?.ContainerCheckDigit){
					'E207_08' (util.substring(current_Body.Container?.ContainerNumber, 5, 6) + (current_Body.Container.ContainerCheckDigit ? current_Body.Container.ContainerCheckDigit : ""))
				}

				if (current_Body.Container?.ContainerStatus?.trim() == 'Empty') {
					'E578_09' 'E'
				} else if (current_Body.Container?.ContainerStatus?.trim() == 'Laden') {
					'E578_09' 'L'
				} else if (current_Body.Event?.CS1Event?.trim() == 'CS210') {
					'E578_09' 'E'
				} else {
					'E578_09' 'L'
				}

				if (current_Body.Container?.CarrCntrSizeType) {
					'E24_10' util.substring(current_Body?.Container?.CarrCntrSizeType, 1, 4)
				}

				if (B411) {
					'E310_11' util.substring(B411, 1, 30)
				}

				if (B412) {
					'E309_12' B412
				}
			}
			//Loop N9
			//for avoid duplicated N9
			current_Body.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber.trim()}.each { blNumber, current_BookingGeneralInfoGroup ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(blNumber,1,30)
				}
			}

			current_Body.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it.CustomsReferenceNumber) && it.CustomsReferenceType == 'IT'}.groupBy{it.CustomsReferenceNumber?.trim()}.each { customerReferenceNumber, current_BLGeneralInfoGroup ->
				'N9' {
					'E128_01' 'IB'
					'E127_02' util.substring(customerReferenceNumber,1,30)
				}
			}

			current_Body.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each { bookingNumber, current_BookingGeneralInfoGroup ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(bookingNumber,1,30)
				}
			}

			if ((current_Body.Container?.ContainerNumber || current_Body.Container?.ContainerCheckDigit)) {
				String eq = ''

				if (current_Body.Container?.ContainerNumber) {
					eq = util.substring(current_Body?.Container?.ContainerNumber,1,10)
					if(current_Body.Container?.ContainerCheckDigit){
						eq = eq + current_Body.Container?.ContainerCheckDigit
					}
				} else {
					eq = current_Body.Container?.ContainerCheckDigit
				}
				'N9' {
					'E128_01' 'EQ'
					'E127_02' util.substring(eq,1,30)
				}
			}

			current_Body.Container?.Seal?.findAll{StringUtil.isNotEmpty(it.SealNumber) && it.SealNumber.length() >= 4}.groupBy{it.SealNumber?.trim()}.each { sealNumber, sealGroup ->
				'N9' {
					'E128_01' 'SN'
					'E127_02' util.substring(sealNumber,1,30)
				}
			}

			current_Body.ExternalReference?.findAll{it.CSReferenceType == 'PO' && StringUtil.isNotEmpty(it.ReferenceNumber)}.groupBy{it.ReferenceNumber?.trim()}.each{ currentPONumber, current_ExternalReferenceGroup ->
				'N9' {
					'E128_01' 'PO'
					'E127_02' util.substring(currentPONumber,1,30)
				}
			}

			Party partyCGN = current_Body.Party?.find{it.PartyType == 'CGN' && StringUtil.isEmpty(it.PartyLevel)}
			Party partyCGNWithPartyLevel = current_Body.Party?.find{it.PartyType == 'CGN' && StringUtil.isNotEmpty(it.PartyLevel)}

			if (partyCGN?.CarrierCustomerCode) {
				'N9' {
					'E128_01' 'IC'
					'E127_02' partyCGN?.CarrierCustomerCode?.trim()
					// this should be bug in tibco
					if (partyCGN?.CarrierCustomerCode?.trim()?.length() == 10) {					
						'E369_03' util.substring(partyCGN?.PartyName?.trim(), 1, 45)
					}
				}
			} else if (partyCGNWithPartyLevel?.CarrierCustomerCode) {
				'N9' {
					'E128_01' 'IC'
					'E127_02' util.substring(partyCGNWithPartyLevel?.CarrierCustomerCode?.trim(),1,30)
					// this should be bug in tibco
					if (partyCGNWithPartyLevel?.CarrierCustomerCode?.trim()?.length() == 10 && partyCGNWithPartyLevel?.PartyName) {
						//update substring at most 45.					
						'E369_03' util.substring(partyCGNWithPartyLevel?.PartyName?.trim(), 1, 45)
					}
				}
			}
			//
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if (current_Body.Route?.OceanLeg) {
				firstOceanLeg = current_Body?.Route?.OceanLeg[0]
				lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
			}
			if (shipDir == 'I') {
				'Q2' {
					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E597_01' util?.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)
					}
					if (lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode) {
						'E26_02' lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode
					}
					LocDT polDepartDtA = lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT

					LocDT polDepartDtE = lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT
					if (polDepartDtA?.toString()) {
						'E373_04' util?.convertDateTime(polDepartDtA, xmlDateTimeFormat, yyyyMMdd)
					} else if (polDepartDtE?.toString()) {
						'E373_04' util?.convertDateTime(polDepartDtE, xmlDateTimeFormat, yyyyMMdd)
					}
					LocDT podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT
					LocDT podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT
					if (podArrivalDtA?.toString()) {
						'E373_05' util?.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
					} else if (podArrivalDtE?.toString()) {
						'E373_05' util?.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
					}
					if (lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction) {
						'E55_09' ((lastOceanLeg?.SVVD?.Discharge?.Voyage ? lastOceanLeg?.SVVD?.Discharge?.Voyage:"") + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction:""))
					}
					'E127_11' current_Body.GeneralInformation?.SCAC
					'E897_12' 'L'
					if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
						'E182_13' lastOceanLeg?.SVVD?.Discharge?.VesselName
					}
				}
			} else if (shipDir == 'O') {
				'Q2' {
					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)
					}
					if (firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode) {
						'E26_02' firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode
					}
					LocDT polDepartureDtA = lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
					LocDT polDepartureDtE = lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT
					if (polDepartureDtA?.toString()) {
						'E373_04' util?.convertDateTime(polDepartureDtA, xmlDateTimeFormat, yyyyMMdd)
					} else if (polDepartureDtE?.toString()) {
						'E373_04' util?.convertDateTime(polDepartureDtE, xmlDateTimeFormat, yyyyMMdd)
					}

					LocDT podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT
					LocDT podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT
					if (podArrivalDtA?.toString()) {
						'E373_05' util?.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
					} else if (podArrivalDtE?.toString()) {
						'E373_05' util?.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
					}
					if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
						'E55_09' ((firstOceanLeg?.SVVD?.Loading?.Voyage ? firstOceanLeg?.SVVD?.Loading?.Voyage:"") + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction:""))
					}
					'E127_11' current_Body.GeneralInformation?.SCAC
					'E897_12' 'L'
					if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' firstOceanLeg?.SVVD?.Loading?.VesselName
					}
				}
			}

				//R4
			POR POR =  current_Body.Route?.POR
			FirstPOL firstPOL =  current_Body.Route?.FirstPOL
			LastPOD lastPOD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND
			if (POR) {
				'Loop_R4' {
					'R4' {
						'E115_01' 'R'
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType
						}
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.SchedKDCode
						}
						if (POR?.CityDetails?.City) {
							'E114_04' util?.substring(POR?.CityDetails?.City?.toUpperCase(), 1, 24)
						}
						if (POR?.CSStandardCity?.CSCountryCode?.length() <= 2){
							'E26_05' POR?.CSStandardCity?.CSCountryCode
						}
						if (POR?.CSStandardCity?.CSStateCode) {
							'E156_08' POR?.CSStandardCity?.CSStateCode
						}
					}

					LocDT porDTM = null
					def isAct = false
					if (current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString()) {
						porDTM = current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT
						isAct = true
					} else if (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT?.toString() && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C') {
						porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT
					} else if (current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString() && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M') {
						porDTM = current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT
					}
					if (porDTM?.toString()) {
						'DTM' {
							if (isAct) {
								'E374_01' '370'
							} else {
								'E374_01' '369'
							}
							'E373_02' util.convertDateTime(porDTM, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(porDTM, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}

				}
			}
			if (firstPOL) {
				'Loop_R4' {

					//APLL special logic
					def R404L = null
					if (['CS070'].contains(vCS1EventFirst5)) {
						R404L = B411
					} else {
						if (firstPOL?.Port?.PortName) {
							R404L = util.substring(firstPOL?.Port?.PortName?.toUpperCase(), 1, 24)
						} else if (firstPOL?.Port?.City) {
							R404L = util.substring(firstPOL?.Port?.City?.toUpperCase(), 1, 24)
						}
						if (R404L.contains('(')) {
							def temp = R404L.indexOf('(')
							R404L = util.substring(R404L, 1, temp)

							if (R404L.charAt(R404L.length()-1) == ' ') {
								R404L = util.substring(R404L, 1, temp-1)
							}
						}
					}

					'R4' {
						'E115_01' 'L'
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E309_02' firstPOL?.Port?.LocationCode?.SchedKDType
						}
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.UNLocationCode
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.SchedKDCode
						}
						if (R404L) {
							'E114_04' R404L
						}
						if (firstPOL?.Port?.CSCountryCode?.length() <= 2) {
							'E26_05' firstPOL?.Port?.CSCountryCode
						}
						if (firstPOL?.CSStateCode) {
							'E156_08' firstPOL?.CSStateCode
						}
					}
					LocDT polDepartureDtA = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT polDepartureDtE = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
					if (polDepartureDtA?.toString()) {
						'DTM' {
							'E374_01' '370'
							'E373_02' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					} else if (polDepartureDtE?.toString()) {
						'DTM' {
							'E374_01' '369'
							'E373_02' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, HHmm)
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
				R402D = lastPOD?.Port?.LocationCode?.SchedKDType
			}
			if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
				R403D = lastPOD?.Port?.LocationCode?.UNLocationCode
			} else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
				R403D = lastPOD?.Port?.LocationCode?.SchedKDCode
			}
			if (lastPOD?.Port?.PortName) {
				R404D = util?.substring(lastPOD?.Port?.PortName?.toUpperCase(), 1, 24)
			} else if (lastPOD?.Port?.City) {
				R404D = util?.substring(lastPOD?.Port?.City?.toUpperCase(), 1, 24)
			}
			if (lastPOD?.Port?.CSCountryCode?.length() <= 2) {
				R405D = lastPOD?.Port?.CSCountryCode
			}
			if (lastPOD?.Facility?.FacilityName) {
				R406D = util?.substring(lastPOD?.Facility?.FacilityName, 1, 30)?.trim()
			} else {
				R406D = lastPOD?.Port?.LocationCode?.UNLocationCode?.trim()
			}
			if (lastPOD?.CSStateCode) {
				R408D = lastPOD?.CSStateCode
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
						if (R408D) {
							'E156_08' R408D
						}
					}
					LocDT podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT
					if (podArrivalDtA?.toString()) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					} else if (podArrivalDtE?.toString()) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
					if (current_Body.BLGeneralInfo[0]?.CustomsReferenceNumber && current_Body.BLGeneralInfo[0]?.CustomsReferenceType == 'IT') {
						if (podArrivalDtA?.toString()) {
							'DTM' {
								'E374_01' '059'
								'E373_02' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, HHmm)
								'E623_04' 'LT'
							}
						} else if (podArrivalDtE?.toString()) {
							'DTM' {
								'E374_01' '059'
								'E373_02' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, HHmm)
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
				R402E = FND?.CityDetails?.LocationCode?.SchedKDType
			}
			if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
				R403E = FND?.CityDetails?.LocationCode?.UNLocationCode
			} else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
				R403E = FND?.CityDetails?.LocationCode?.SchedKDCode
			}
			if (FND?.CityDetails?.City) {
				R404E = util?.substring(FND?.CityDetails?.City?.toUpperCase(), 1, 24)
			}
			if (FND?.CSStandardCity?.CSContinentCode?.length() <= 2) {
				R405E = FND?.CSStandardCity?.CSCountryCode
			}
			if (FND?.CSStandardCity?.CSStateCode) {
				R408E = FND?.CSStandardCity?.CSStateCode
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
				if (B406 && current_Body.Event?.Location?.CSStandardCity?.CSCountryCode) {
					R405E = current_Body.Event.Location.CSStandardCity.CSCountryCode
				}
				if (B406 && current_Body.Event?.Location?.CityDetails?.State) {
					R408E = util.substring(current_Body.Event.Location.CityDetails.State, 1, 2)?.toUpperCase()
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
						'E373_02' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}
			} else if (FND) {
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
					LocDT fndDatetime = null
					def isAct = null
					if (current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString()) {
						fndDatetime = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'A'}?.LocDT
						isAct = true
					} else if (current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT?.toString()) {
						fndDatetime = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'E'}?.LocDT
						isAct = false
					} else if (current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT?.toString()) {
						fndDatetime = current_Body.Route.ArrivalAtFinalHub.find{it.attr_Indicator == 'A'}?.LocDT
						isAct = true
					} else if (FND?.ArrivalDT?.find{it.attr_Indicator[0] == 'A'}?.LocDT?.toString() && current_Body.GeneralInformation?.SCAC != 'OOLU') {
						fndDatetime = FND?.ArrivalDT.find{it.attr_Indicator[0] == 'A'}?.LocDT
						isAct = true
					} else if (current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT?.toString()) {
						fndDatetime = current_Body.Route.ArrivalAtFinalHub.find{it.attr_Indicator == 'E'}?.LocDT
						isAct = false
					} else if (FND?.ArrivalDT?.find{it.attr_Indicator[0] == 'E'}?.LocDT?.toString() && current_Body.GeneralInformation?.SCAC != 'OOLU') {
						fndDatetime = FND?.ArrivalDT.find{it.attr_Indicator[0] == 'E'}?.LocDT
						isAct = false
					}
					if (fndDatetime?.toString()) {
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
			if (['CS200'].contains(vCS1EventFirst5)) {
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
						'E373_02' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)
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
						if (R408D) {
							'E156_08' R408D
						}
					}
					LocDT podArrivalDtA = lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT podArrivalDtE = lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
					if (podArrivalDtA?.toString()) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtA, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					} else if (podArrivalDtE?.toString()) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDtE, xmlDateTimeFormat, HHmm)
							'E623_04' 'LT'
						}
					}
				}
			}
			'SE' {
				//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
				'E96_01' ' '
				'E329_02' '    '
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

		XmlBeanParser parser = new XmlBeanParser()
		ContainerMovement ct = parser.xmlParser(inputXmlBody, ContainerMovement.class)

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
		currentSystemDt = new Date()
		//def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
		def headerMsgDT = util.convertDateTime(ct.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		//long ediIsaCtrlNumber = ctrlNos[0]
		//long ediGroupCtrlNum = ctrlNos[1]
		def txnErrorKeys = []

		//duplication -- CT special logic
		Map<String,String> duplicatedPairs	= ctUtil.getDuplicatedPairs('LIMITEDBRANDSAPLL', conn)
		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)


		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)
		List<Body> temp

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('LIMITEDBRANDSAPLL', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

			if (errorKeyList.size() == 0) {
				//pass validateBeforeExecution
				//main mapping
				if (eventCS2Cde == 'CS160AM' && current_Body?.GeneralInformation.TransportMode != 'Truck') {
					return;
				}
				generateBody(current_Body, outXml)
				outputBodyCount++

			}

			//posp checking
			pospValidation()

			ctUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, eventExtCde, TP_ID, conn)

			txnErrorKeys.add(errorKeyList);
		}

		//EndWorkFlow


		//End root node
		outXml.nodeCompleted(null,T315)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		//		println bizKeyWriter.toString();
		//		println csuploadWriter.toString();

		//promote csupload and bizkey to session
		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);

		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = writer?.toString()
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()

		return result;
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		// error cases
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
		ctUtil.missingContainerNumber(eventCS2Cde, current_Body.Container, true, null, errorKeyList)

		// obsolete cases
		ctUtil.missingBlNumber(eventCS2Cde, current_Body.BLGeneralInfo, false, null, errorKeyList)
		ctUtil.missingConsigneeCode(eventCS2Cde, current_Body.Party, false, null ,errorKeyList)
		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route?.Inbound_intermodal_indicator, false, null, errorKeyList)

		return errorKeyList;
	}

	public boolean pospValidation() {

	}
}






