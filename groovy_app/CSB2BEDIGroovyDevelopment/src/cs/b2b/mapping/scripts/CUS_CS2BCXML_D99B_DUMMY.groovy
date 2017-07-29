package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.math.RoundingMode
import java.sql.Connection
import java.text.DecimalFormat

import cs.b2b.core.mapping.bean.EmergencyContact
import cs.b2b.core.mapping.bean.bc.Appointment
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.Cargo
import cs.b2b.core.mapping.bean.bc.Container
import cs.b2b.core.mapping.bean.bc.DGCargoSpec
import cs.b2b.core.mapping.bean.bc.Party
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.core.mapping.bean.bc.RemarkLines
import cs.b2b.core.mapping.bean.bc.RequiredDocument
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * from tibco 1.0 DUMMYBCD99B
 * @author LIANGDA
 *
 */

public class CUS_CS2BCXML_D99B_DUMMY {

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
	def yyyyMMddHHmmss = 'yyyyMMddHHmmss'
	def yyyyMMddHHmm = "yyyyMMddHHmm"
	def yyyyMMdd = "yyyyMMdd"
	def HHmmss = 'HHmmss'
	
	DecimalFormat decimalFormatter = new DecimalFormat("#.####")
	DecimalFormat decimalFmt3Digital = new DecimalFormat("#.###")
	DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")
	
	def msgFmtId = "EDIFACT"
	DecimalFormat fmtDecmail3digital = (new DecimalFormat("0.000"))
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {
		
		def currentBcScac = current_Body.GeneralInformation?.SCACCode
		
		outXml.'Group_UNH' {
			'UNH' {
				'E0062_01' '-999'
				'S009_02' {
					'E0065_01' 'IFTMBC'
					'E0052_02' 'D'
					'E0054_03' '99B'
					'E0051_04' 'UN'
					'E0057_05' ''
					'E0110_06' ''
					'E0113_07' ''
				}
				'E0068_03' ''
				'S010_04' {
					'E0070_01' ''
					'E0073_02' ''
				}
			}
			
			'BGM' {
				'C002_01' {
					'E1001_01' '770'
					'E1131_02' ''
					'E3055_03' ''
					'E1000_04' ''
				}
				'C106_02' {
					'E1004_01' util.substring(current_Body.ExternalReference.find { it.CSReferenceType?.toUpperCase() == 'SID'}?.ReferenceNumber?.trim(), 1, 35)
					'E1056_02' ''
					'E1060_03' ''
				}
				def statusMap = ['Confirmed':'9', 'Cancelled':'1', 'Declined':'5', 'No Show':'NS', 'Pending':'PE', 'Rejected':'48']
				'E1225_03' statusMap.get(current_Body.GeneralInformation?.BookingStatus)
				'E4343_04' ''
			}
			
			if (current_Body.GeneralInformation?.BookingStatus == 'Confirmed') {
				if (current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.LocDT || current_Body.GeneralInformation?.BookingStatusDT?.GMT) {
					if (current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.LocDT) {
						'DTM' {
							'C507_01' {
								'E2005_01' '55'
								'E2380_02' util.convertXmlDateTime(current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.LocDT, yyyyMMddHHmm);
								'E2379_03' '203'
							}
						}
					} else {
						'DTM' {
							'C507_01' {
								'E2005_01' '55'
								'E2380_02' util.convertXmlDateTime(current_Body.GeneralInformation?.BookingStatusDT?.GMT, yyyyMMddHHmm);
								'E2379_03' '203'
							}
						}
					}
				}
			}
			
			if (current_Body.GeneralInformation?.SCACCode != 'HRZD') {
				RequiredDocument defDate = current_Body.DocumentInformation?.RequiredDocument?.find{it.DocumentType?.toUpperCase() == 'Shipping Instruction/BL Master'.toUpperCase()}
				if (defDate && defDate.DueDT?.LocDT) {
					'DTM' {
						'C507_01' {
							'E2005_01' '407'
							'E2380_02' util.convertXmlDateTime(defDate.DueDT?.LocDT, yyyyMMddHHmm);
							'E2379_03' '203'
						}
					}
				}
			}

			if (current_Body.GeneralInformation?.SCACCode == 'HRZD' && current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT) {
				'DTM' {
					'C507_01' {
						'E2005_01' '407'
						'E2380_02' util.convertXmlDateTime(current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT, yyyyMMddHHmm);
						'E2379_03' '203'
					}
				}
			}
			
			if (current_Body.GeneralInformation?.VGMCutOffDT?.LocDT?.LocDT) {
				'DTM' {
					'C507_01' {
						'E2005_01' '225'
						'E2380_02' util.convertXmlDateTime(current_Body.GeneralInformation?.VGMCutOffDT?.LocDT?.LocDT, yyyyMMddHHmm);
						'E2379_03' '203'
					}
				}
			}
			
			if (current_Body.Route) {
				'TSR' {
					'C536_01' {
						//Body/Route/Haulage/OutBound
						def haulage = current_Body.Route.Haulage?.OutBound
						if (util.isEmpty(haulage)) {
							haulage = 'C'
						}
						if (current_Body.Route.Haulage?.InBound) {
							haulage = haulage + current_Body.Route.Haulage?.InBound
						} else {
							haulage = haulage + 'C'
						}
						def mapHaulage = ['CC':'27', 'CM':'28', 'MC':'29', 'MM':'30']
						'E4065_01' mapHaulage.get(haulage)
						'E1131_02' ''
						'E3055_03' ''
					}
					'C233_02' {
						if (current_Body.GeneralInformation?.TrafficMode?.OutBound == 'FCL' && current_Body.GeneralInformation?.TrafficMode?.InBound == 'FCL') {
							'E7273_01' '2'
						} else if (current_Body.GeneralInformation?.TrafficMode?.OutBound == 'LCL' && current_Body.GeneralInformation?.TrafficMode?.InBound == 'LCL') {
							'E7273_01' '3'
						}
					}
				}
			}
			
			//FTX
			if (current_Body.GeneralInformation?.BookingStatus != 'Cancelled') {
				List<RemarkLines> remarks = current_Body.RemarkLines?.findAll(){it?.attr_RemarkType == 'Booking'}
				if (remarks && remarks.size()>0) {
					def remarksContent = ''
					for(RemarkLines rl : remarks) {
						if (rl && rl.RemarkLines) {
							if (remarksContent) {
								remarksContent = remarksContent + ' ' + rl.RemarkLines
							} else {
								remarksContent = rl.RemarkLines
							}
						}
					}
					remarksContent = remarksContent.toUpperCase()
					'FTX' {
						'E4451_01' 'AAI'
						'C108_04' {
							if (util.substring(remarksContent, 1, 70)?.trim()) {
								'E4440_01' util.trimRightSpace(util.substring(remarksContent, 1, 70))
							}
							if (util.substring(remarksContent, 71, 70)?.trim()) {
								'E4440_02' util.trimRightSpace(util.substring(remarksContent, 71, 70))
							}
							if (util.substring(remarksContent, 141, 70)?.trim()) {
								'E4440_03' util.trimRightSpace(util.substring(remarksContent, 141, 70))
							}
							if (util.substring(remarksContent, 211, 70)?.trim()) {
								'E4440_04' util.trimRightSpace(util.substring(remarksContent, 211, 70))
							}
							if (util.substring(remarksContent, 281, 70)?.trim()) {
								'E4440_05' util.trimRightSpace(util.substring(remarksContent, 281, 70))
							}
						}
					}
				}
			}
			if (current_Body.GeneralInformation?.BookingStatus == 'Cancelled') {
				'FTX' {
					'E4451_01' 'AAI'
					'C108_04' {
						'E4440_01' 'Carrier has cancelled booking on request of customer.'
					}
				}
			}
			
			//Group_2
			//note: clp setting : 'INVOICE  NUMBER'  -- 2 spaces need to check
			def extRefTypes = ['BN':'BN', 'CONTRACT NUMBER':'CT', 'CUSTOMER REFERENCE NUMBER':'CR', 'FORWARDER REFERENCE NUMBER':'FN', 'INVOICE  NUMBER':'IV', 'MUTUALLY DEFINED REFERENCE':'ZZ', 'PURCHASE ORDER':'PO', 'SID (SHIPPER\'S IDENTIFYING NUMBER FOR SHIPMENT)':'SI', 'TARIFF NUMBER':'AFG']
			current_Body.ExternalReference?.each { current_ExtRef ->
				if (current_ExtRef.ReferenceDescription && current_ExtRef.ReferenceNumber) {
					def convertVal = extRefTypes.get(current_ExtRef.ReferenceDescription?.toUpperCase())
					if (convertVal) {
						'Group2_RFF' {
							'RFF' {
								'C506_01' {
									'E1153_01' convertVal
									'E1154_02' util.substring(current_ExtRef.ReferenceNumber.trim(), 1, 35)
								}
							}
						}
					}
				}
			}
			
			current_Body.CarrierRate?.each { current_CarRate ->
				if (current_CarRate.CSCarrierRateType && current_CarRate.CarrierRateNumber) {
					def convertVal = extRefTypes.get(current_CarRate.CSCarrierRateType.toUpperCase())
					if (convertVal) {
						'Group2_RFF' {
							'RFF' {
								'C506_01' {
									'E1153_01' convertVal
									'E1154_02' util.substring(current_CarRate.CarrierRateNumber.trim(), 1, 35)
								}
							}
						}
					}
				}
			}
			
			if (current_Body.GeneralInformation?.CarrierBookingNumber) {
				'Group2_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' 'BN'
							'E1154_02' util.substring(current_Body.GeneralInformation?.CarrierBookingNumber.trim(), 1, 35)
						}
					}
				}
			}
			
			boolean isNotFoundBL = true;
			current_Body.GeneralInformation?.BLNumber?.each { current_BLNum ->
				if (current_BLNum) {
					isNotFoundBL = false
					'Group2_RFF' {
						'RFF' {
							'C506_01' {
								'E1153_01' 'BM'
								'E1154_02' util.substring(current_BLNum, 1, 35)
							}
						}
					}
				}
			}
			if (isNotFoundBL) {
				//if no BLNumber found
				current_Body.ExternalReference?.each { current_ExtRef ->
					//B2BSCR20170315003788 - DUMMYBCD99B RFF+BM enhancement
					if (current_ExtRef.CSReferenceType == 'BL' && current_ExtRef.ReferenceNumber) {
						'Group2_RFF' {
							'RFF' {
								'C506_01' {
									'E1153_01' 'BM'
									'E1154_02' util.substring(current_ExtRef.ReferenceNumber.trim(), 1, 35)
								}
							}
						}
					}
				}
			}
			
			
			//Group_3 Route
			current_Body.Route?.OceanLeg?.eachWithIndex { current_OceanLeg , current_Index ->
				'Group3_TDT' {
					'TDT' {
						if (current_Body.Route?.OceanLeg?.size() == 1) {
							'E8051_01' '20'
						} else if (current_Body.Route?.OceanLeg?.size() > 1 && current_Index == 0) {
							'E8051_01' '20'
						} else if (current_Body.Route?.OceanLeg?.size() > 1 && current_Index > 0) {
							'E8051_01' '10'
						}
						if (current_OceanLeg.SVVD?.Loading?.Voyage) {
							String defDir = (current_OceanLeg.SVVD?.Loading?.Direction==null?"":current_OceanLeg.SVVD?.Loading?.Direction?.trim())
							'E8028_02' util.substring(current_OceanLeg.SVVD?.Loading?.Voyage?.trim() + defDir, 1, 17)
						}
						'C220_03' {
							'E8067_01' '1'
							'E8066_02' ''
						}
						'C228_04' {
							'E8179_01' ''
							'E8178_02' ''
						}
						'C040_05' {
							if (current_Body.GeneralInformation?.SCACCode) {
								'E3127_01' current_Body.GeneralInformation?.SCACCode?.trim()
							}
							'E1131_02' ''
							'E3055_03' ''
							'E3128_04' ''
						}
						'C222_08' {
							if (current_OceanLeg.SVVD?.Loading?.LloydsNumber) {
								'E8213_01' util.substring(current_OceanLeg.SVVD?.Loading?.LloydsNumber.trim(), 1, 20)
								'E1131_02' 'L'
							} else if (current_OceanLeg.SVVD?.Loading?.CallSign) {
								'E8213_01' util.substring(current_OceanLeg.SVVD?.Loading?.CallSign.trim(), 1, 20)
								'E1131_02' '103'
							}
							'E3055_03' ''
							if (current_OceanLeg.SVVD?.Loading?.VesselName) {
								'E8212_04' util.substring(current_OceanLeg.SVVD?.Loading?.VesselName?.trim(), 1, 35)
							}
							'E8453_05' ''
						}
						'E8281_09' ''
					}
					
					//get the minimum oceanleg sequence
					def legSeqMin = current_Body.Route?.OceanLeg?.min {it==null || it.LegSeq==null?0:Integer.parseInt(it.LegSeq) }?.LegSeq
					//get the maximum oceanleg sequence
					def legSeqMax = current_Body.Route?.OceanLeg?.max {it==null || it.LegSeq==null?0:Integer.parseInt(it.LegSeq) }?.LegSeq
					
					// POR for the 1st OceanLeg
					if (current_OceanLeg.LegSeq == legSeqMin) {
						// Group_3/Group_4 - POR
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '88'
								'C517_02' {
									def portVal = current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
									if (util.isEmpty(portVal)) {
										portVal = current_Body.Route?.POR?.CSStandardCity?.CSParentCityID
									}
									'E3225_01' util.substring(portVal, 1, 25)
									'E1131_02' ''
									'E3055_03' '6'
									if (current_Body.Route?.POR?.CityDetails?.City) {
										'E3224_04' util.substring(current_Body.Route.POR?.CityDetails?.City?.trim(), 1, 256)
									}
								}
							}
							if (current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT) {
								'DTM' {
									'C507_01' {
										'E2005_01' '180'
										'E2380_02' util.convertXmlDateTime(current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT, yyyyMMdd)
										'E2379_03' '102'
									}
								}
							}
						}
					}
					//end of the min OceanLeg
					
					// Group_3/Group_4 - POL
					if (current_OceanLeg.POL) {
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '9'
								'C517_02' {
									def portVal = current_OceanLeg.POL?.Port?.LocationCode?.UNLocationCode
									if (util.isEmpty(portVal)) {
										portVal = current_OceanLeg.POL?.Port?.CSPortID
									}
									if (portVal) {
										'E3225_01' util.substring(portVal, 1, 25)
									}
									'E1131_02' ''
									'E3055_03' '6'
									if (current_OceanLeg.POL?.Port?.City) {
										'E3224_04' util.substring(current_OceanLeg.POL?.Port?.City?.trim(), 1, 256)
									}
								}
							}
							if (current_OceanLeg.DepartureDT?.size()>0 && current_OceanLeg.DepartureDT[0]?.LocDT?.LocDT) {
								'DTM' {
									'C507_01' {
										'E2005_01' '133'
										'E2380_02' util.convertXmlDateTime(current_OceanLeg.DepartureDT[0]?.LocDT?.LocDT, yyyyMMdd)
										'E2379_03' '102'
									}
								}
							}
						}
					}
					
					// Group_3/Group_4 - POD
					if (current_OceanLeg.POD) {
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '11'
								'C517_02' {
									def portVal = current_OceanLeg.POD?.Port?.LocationCode?.UNLocationCode
									if (util.isEmpty(portVal)) {
										portVal = current_OceanLeg.POD?.Port?.CSPortID
									}
									if (portVal) {
										'E3225_01' util.substring(portVal, 1, 25)
									}
									'E1131_02' ''
									'E3055_03' '6'
									if (current_OceanLeg.POD?.Port?.City) {
										'E3224_04' util.substring(current_OceanLeg.POD?.Port?.City?.trim(), 1, 256)
									}
								}
							}
							if (current_OceanLeg.ArrivalDT?.size()>0 && current_OceanLeg.ArrivalDT[0]?.LocDT?.LocDT) {
								'DTM' {
									'C507_01' {
										'E2005_01' '132'
										'E2380_02' util.convertXmlDateTime(current_OceanLeg.ArrivalDT[0]?.LocDT?.LocDT, yyyyMMdd)
										'E2379_03' '102'
									}
								}
							}
						}
					}
					
					// FND for the last OceanLeg
					if (current_OceanLeg.LegSeq == legSeqMax) {
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '7'
								'C517_02' {
									def portVal = current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
									if (util.isEmpty(portVal)) {
										portVal = current_Body.Route?.FND?.CSStandardCity?.CSParentCityID
									}
									'E3225_01' util.substring(portVal, 1, 25)
									'E1131_02' ''
									'E3055_03' '6'
									if (current_Body.Route?.FND?.CityDetails?.City) {
										'E3224_04' util.substring(current_Body.Route.FND?.CityDetails?.City?.trim(), 1, 256)
									}
								}
							}
							if (current_Body.Route?.ReqDeliveryDT?.LocDT?.LocDT) {
								'DTM' {
									'C507_01' {
										'E2005_01' '17'
										'E2380_02' util.convertXmlDateTime(current_Body.Route?.ReqDeliveryDT?.LocDT?.LocDT, yyyyMMdd)
										'E2379_03' '102'
									}
								}
							}
						}
					}
					//end of the max OceanLeg - FND
				}
				//end of Group3_TDT
			}
			//end Group_3
			
			
			// Group_6
			
			//CAR - CA
			'Group6_NAD' {
				'NAD' {
					'E3035_01' 'CA'
					'C082_02' {
						'E3039_01' current_Body.GeneralInformation?.SCACCode
					}
					'C059_05' {
						if (current_Body.GeneralInformation?.SCACCode == 'OOLU') {
							'E3042_01' 'Orient Overseas Container Line Ltd.'
						} else if (current_Body.GeneralInformation?.SCACCode == 'YMLU') {
							'E3042_01' 'Yangming Marine Transport Corp.'
						} else {
							Map<String, String> carName = util.getOceanCarrierInfo(current_Body.GeneralInformation?.SCACCode, conn)
							if (carName?.get('NAME')) {
								'E3042_01' carName.get('NAME')
							}
						}
					}
				}
			}
			
			Map partyTypeMap = ['BPT':'BK', 'CGN':'CN', 'FWD':'FW', 'SHP':'CZ']
			
			List<Party> parties = current_Body.Party.findAll(){it.PartyType in partyTypeMap.keySet()}
			parties.each { current_Party ->
				'Group6_NAD' {
					'NAD' {
						def defPartyType = partyTypeMap.get(current_Party.PartyType)
						'E3035_01' defPartyType

						'C082_02' {
							'E3039_01' current_Party.CarrierCustomerCode
							'E1131_02' ''
							'E3055_03' ''
						}
						
						if (current_Party.PartyName) {
							'C080_04' {
								'E3036_01' util.substring(current_Party.PartyName, 1, 35)?.trim()
								if (util.substring(current_Party.PartyName, 36, 35)) {
									'E3036_02' util.substring(current_Party.PartyName, 36, 35)?.trim()
								}
								'E3036_03' ''
								'E3036_04' ''
								'E3036_05' ''
								'E3045_06' ''
							}
						}
						
						String addressLine = ''
						current_Party.Address?.AddressLines?.AddressLine.each { current_AddressLine ->
							if (current_AddressLine) {
								if (addressLine.length()>0) {
									addressLine += " "
								}
								addressLine += current_AddressLine
							}
						}
						if (addressLine) {
							'C059_05' {
								'E3042_01' util.substring(addressLine, 1, 35).trim()
								if (addressLine.length()>35) {
									'E3042_02' util.substring(addressLine, 36, 35).trim()
								}
								if (addressLine.length()>70) {
									'E3042_03' util.substring(addressLine, 71, 35).trim()
								}
								if (addressLine.length()>105) {
									'E3042_04' util.substring(addressLine, 106, 35).trim()
								}
							}
						}
						if (current_Party?.Address?.City) {
							'E3164_06' util.substring(current_Party?.Address?.City, 1, 35)?.trim()
						}
						'C819_07' {
							HashMap<String, String> cityResult = util.getCS2MasterCityByStateNameCityNameCntryCde(current_Party.Address?.State, current_Party.Address?.City, current_Party.Address?.Country, conn)
							if (cityResult && cityResult.size()>0) {
								'E3229_01' cityResult.get('STATE_CDE')
							}
							'E1131_02' ''
							'E3055_03' ''
							'E3228_04' ''
						}
						if (current_Party?.Address?.PostalCode) {
							'E3251_08' util.substring(current_Party?.Address?.PostalCode, 1, 17)?.trim()
						}
						'E3207_09' util.substring(current_Party?.Address?.Country, 1, 3)
					}
					
					//requires both CTA and COM are exists together, not output Group_7 if not CTA or COM
					if ((current_Party.Contact?.FirstName || current_Party.Contact?.LastName) && (current_Party.Contact?.ContactPhone?.Number || current_Party.Contact?.ContactEmailAddress)) {
						'Group7_CTA' {
							def name = current_Party.Contact?.FirstName==null?'':current_Party.Contact?.FirstName.trim()
							if (current_Party.Contact?.LastName) {
								name = name + ' ' + current_Party.Contact?.LastName
								name = name.trim()
							}
							if (name) {
								'CTA' {
									'E3139_01' 'IC'
									'C056_02' {
										'E3413_01' ''
										'E3412_02' util.substring(name, 1, 35)?.trim()
									}
								}
							}
							if (current_Party.Contact?.ContactPhone?.Number) {
								'COM' {
									'C076_01' {
										'E3148_01' current_Party.Contact?.ContactPhone?.Number.toUpperCase().trim()
										'E3155_02' 'TE'
									}
								}
							} else if (current_Party.Contact?.ContactEmailAddress) {
								'COM' {
									'C076_01' {
										'E3148_01' current_Party.Contact?.ContactEmailAddress.toUpperCase().trim()
										'E3155_02' 'EM'
									}
								}
							}
						}
					}
					//end of Group7_CTA
				}
			}
			//end of Group_6 Party
			
			
			//Group_9
			current_Body.Cargo?.eachWithIndex { cargo, currentIndex ->
				'Group9_GID' {
					'GID' {
						'E1496_01' (currentIndex+1)
						if (cargo.Packaging?.PackageQty) {
							'C213_02' {
								'E7224_01' cargo.Packaging?.PackageQty
								//get code conversion of package type
								def pkgTypeMap = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null, msgFmtId, 'PackageUnit', cargo.Packaging?.PackageType, conn)
								def pkgTypeVal = pkgTypeMap?.get('EXT_CDE')
								if (util.isEmpty(pkgTypeVal)) {
									pkgTypeVal = cargo.Packaging?.PackageType
								}
								'E7065_02' pkgTypeVal
							}
						}
					}
					if (util.isNotEmpty(cargo.CargoDescription)) {
						String desc = cargo.CargoDescription
						if (desc) {
							for (int i=70; desc.length() < 70; i++) {
								desc += ' '
							}
						}
						if (cargo.Packaging?.PackageDesc) {
							if (desc) {
								desc += ' ' + cargo.Packaging?.PackageDesc
							} else {
								desc = cargo.Packaging?.PackageDesc
							}
						}
						desc = desc.replace("\n", " ")
						desc = desc.replace("\r", " ")
						desc = desc.toUpperCase()
						
						'FTX' {
							'E4451_01' 'AAA'
							'E4453_02' ''
							'C108_04' {
								'E4440_01' util.substring(desc, 1, 70)?.trim()
								def str1 = util.substring(desc, 71, 70)
								if (str1) {
									'E4440_02' util.trimRightSpace(str1)
								}
								def str2 = util.substring(desc, 141, 70)
								if (str2) {
									'E4440_03' util.trimRightSpace(str2)
								}
								def str3 = util.substring(desc, 211, 70)
								if (str3) {
									'E4440_04' util.trimRightSpace(str3)
								}
								def str4 = util.substring(desc, 281, 70)
								if (str4) {
									'E4440_05' util.trimRightSpace(str4)
								}
							}
						}
					}
					
					//Gross Weight
					if (cargo.GrossWeight?.Weight && Double.parseDouble(cargo.GrossWeight?.Weight)!=0 && cargo.GrossWeight?.WeightUnit) {
						'Group11_MEA' {
							'MEA' {
								'E6311_01' 'WT'
								'C502_02' {
									'E6313_01' 'G'
								}
								'C174_03' {
									if (cargo.GrossWeight?.WeightUnit?.toUpperCase() == 'KGS') {
										'E6411_01' 'KGM'
									} else if (cargo.GrossWeight?.WeightUnit?.toUpperCase() == 'KG') {
										'E6411_01' 'KGM'
									} else {
										'E6411_01' cargo.GrossWeight?.WeightUnit
									}
									def defVal = fmtDecmail3digital.format(Double.parseDouble(cargo.GrossWeight?.Weight))
									'E6314_02' defVal
								}
							}
						}
					}
					
					//Volume
					if (cargo.Volume?.Volume && Double.parseDouble(cargo.Volume?.Volume)!=0 && cargo.Volume?.VolumeUnit) {
						'Group11_MEA' {
							'MEA' {
								'E6311_01' 'VOL'
								'C502_02' {
									'E6313_01' 'AAW'
								}
								'C174_03' {
									if (cargo.Volume?.VolumeUnit?.toUpperCase() == 'CFT') {
										'E6411_01' 'CBM'
									} else {
										'E6411_01' cargo.Volume?.VolumeUnit
									}
									def defVal = fmtDecmail3digital.format(Double.parseDouble(cargo.Volume?.Volume))
									'E6314_02' defVal
								}
							}
						}
					}
					
					//Net Weight
					if (cargo.NetWeight?.Weight && Double.parseDouble(cargo.NetWeight?.Weight)!=0 && cargo.NetWeight?.WeightUnit) {
						'Group11_MEA' {
							'MEA' {
								'E6311_01' 'WT'
								'C502_02' {
									'E6313_01' 'N'
								}
								'C174_03' {
									if (cargo.NetWeight?.WeightUnit?.toUpperCase() == 'KGS') {
										'E6411_01' 'KGM'
									} else if (cargo.NetWeight?.WeightUnit?.toUpperCase() == 'KG') {
										'E6411_01' 'KGM'
									} else {
										'E6411_01' cargo.NetWeight?.WeightUnit
									}
									def defVal = fmtDecmail3digital.format(Double.parseDouble(cargo.NetWeight?.Weight))
									'E6314_02' defVal
								}
							}
						}
					}
					
					//group_12 - AWCargo
					cargo.AWCargoSpec?.each { current_AwCargoSpec ->
						'Group12_DIM' {
							'DIM' {
								'E6145_01' '2'
								'C211_02' {
									if (current_AwCargoSpec?.Height?.LengthUnit == 'F' && current_AwCargoSpec?.Length?.LengthUnit == 'F' && current_AwCargoSpec?.Width?.LengthUnit == 'F') {
										'E6411_01' 'FT'
									} else {
										'E6411_01' 'MT'
									}
									if (current_AwCargoSpec?.Length?.Length) {
										if (current_AwCargoSpec.Length?.LengthUnit=='F' && (current_AwCargoSpec.Height?.LengthUnit!='F' || current_AwCargoSpec.Width?.LengthUnit!='F')) {
											'E6168_02' decimalFmt3Digital.format(Double.parseDouble(current_AwCargoSpec.Length?.Length) * 0.3048)
										} else if (current_AwCargoSpec.Length?.Length?.indexOf('.')>=0) {
											'E6168_02' decimalFmt3Digital.format(Double.parseDouble(current_AwCargoSpec.Length?.Length))
										} else {
											'E6168_02' current_AwCargoSpec.Length?.Length
										}
									}
									if (current_AwCargoSpec?.Width?.Length) {
										if (current_AwCargoSpec.Width?.LengthUnit=='F' && (current_AwCargoSpec.Length?.LengthUnit!='F' || current_AwCargoSpec.Height?.LengthUnit!='F')) {
											'E6140_03' decimalFmt3Digital.format(Double.parseDouble(current_AwCargoSpec.Width?.Length) * 0.3048)
										} else if (current_AwCargoSpec.Width?.Length?.indexOf('.')>=0) {
											'E6140_03' decimalFmt3Digital.format(Double.parseDouble(current_AwCargoSpec.Width?.Length))
										} else {
											'E6140_03' current_AwCargoSpec.Width?.Length
										}
									}
									if (current_AwCargoSpec?.Height?.Length) {
										if (current_AwCargoSpec.Height?.LengthUnit=='F' && (current_AwCargoSpec.Length?.LengthUnit!='F' || current_AwCargoSpec.Width?.LengthUnit!='F')) {
											'E6008_04' decimalFmt3Digital.format(Double.parseDouble(current_AwCargoSpec.Height?.Length) * 0.3048)
										} else if (current_AwCargoSpec.Height?.Length.indexOf('.')>=0) {
											'E6008_04' decimalFmt3Digital.format(Double.parseDouble(current_AwCargoSpec.Height?.Length))
										} else {
											'E6008_04' current_AwCargoSpec.Height?.Length
										}
									}
								}
							}
						}
					}
					//end of group_12
					
					//Group_15 DG Cargo
					cargo.findAll{it.CargoNature == 'DG' || it.CargoNature == 'RD'}?.each { current_Cargo ->
						current_Cargo.DGCargoSpec?.each { current_DGCargoSpec ->
							DGCargoSpec dgCargoSpec = (DGCargoSpec)current_DGCargoSpec
							'Group15_DGS' {
								'DGS' {
									'E8273_01' 'IMD'
									'C205_02' {
										'E8351_01' ''
										'E8078_02' ''
										'E8092_03' ''
									}
									'C234_03' {
										'E7124_01' dgCargoSpec.UNNumber
										'E7088_02' ''
									}
									if (dgCargoSpec.FlashPoint?.Temperature) {
										'C223_04' {
											def defVal = dgCargoSpec.FlashPoint?.Temperature
											if (dgCargoSpec.FlashPoint?.Temperature.startsWith("-")) {
												defVal = dgCargoSpec.FlashPoint?.Temperature.substring(1)
												//enhance -- defVal.length()<2 changed to defVal.length()<3
												int totalLen = 3
												if (defVal.contains(".")) {
													totalLen = 4
												}
												for (int i=0; i<3 && defVal.length()<totalLen; i++) {
													defVal = "0"+defVal
												}
											} else {
												for (int i=0; i<3 && defVal.length()<3; i++) {
													defVal = "0"+defVal
												}
											}
											if (defVal.startsWith(".")) {
												defVal = "0" + defVal
											}
											if (dgCargoSpec.FlashPoint?.Temperature.startsWith("-")) {
												defVal = "-"+defVal
											}
											'E7106_01' defVal
											if (dgCargoSpec.FlashPoint?.TemperatureUnit?.toUpperCase() == 'C') {
												'E6411_02' 'CEL'
											} else if (dgCargoSpec.FlashPoint?.TemperatureUnit?.toUpperCase() == 'F') {
												'E6411_02' 'FAH'
											}
										}
									}
									if (dgCargoSpec.PackageGroup?.Code) {
										if (dgCargoSpec.PackageGroup?.Code == 'I') {
											'E8339_05' '1'
										} else if (dgCargoSpec.PackageGroup?.Code == 'II') {
											'E8339_05' '2'
										} else if (dgCargoSpec.PackageGroup?.Code == 'III') {
											'E8339_05' '3'
										} else {
											'E8339_05' util.substring(dgCargoSpec.PackageGroup?.Code, 1, 3)
										}
									}
									'E8364_06' util.substring(dgCargoSpec.EMSNumber, 1, 6)
									'E8410_07' util.substring(dgCargoSpec.MFAGNumber, 1, 4)
								}
								if (dgCargoSpec.TechnicalName) {
									'FTX' {
										'E4451_01' 'AAD'
										'C108_04' {
											'E4440_01' util.substring(dgCargoSpec.TechnicalName, 1, 70)?.trim()
											'E4440_02' util.substring(dgCargoSpec.TechnicalName, 71, 70)?.trim()
											'E4440_03' util.substring(dgCargoSpec.TechnicalName, 141, 70)?.trim()
											'E4440_04' util.substring(dgCargoSpec.TechnicalName, 211, 70)?.trim()
											'E4440_05' util.substring(dgCargoSpec.TechnicalName, 281, 70)?.trim()
										}
									}
								}
								if (dgCargoSpec.ProperShippingName) {
									'FTX' {
										'E4451_01' 'ACB'
										'C108_04' {
											'E4440_01' util.substring(dgCargoSpec.ProperShippingName, 1, 70).trim()
											'E4440_02' util.substring(dgCargoSpec.ProperShippingName, 71, 70)?.trim()
											'E4440_03' util.substring(dgCargoSpec.ProperShippingName, 141, 70)?.trim()
											'E4440_04' util.substring(dgCargoSpec.ProperShippingName, 211, 70)?.trim()
											'E4440_05' util.substring(dgCargoSpec.ProperShippingName, 281, 70)?.trim()
										}
									}
								}
								if (dgCargoSpec.Remarks) {
									'FTX' {
										'E4451_01' 'AAC'
										'C108_04' {
											'E4440_01' util.substring(dgCargoSpec.Remarks, 1, 70).trim()
											'E4440_02' util.substring(dgCargoSpec.Remarks, 71, 70)?.trim()
											'E4440_03' util.substring(dgCargoSpec.Remarks, 141, 70)?.trim()
											'E4440_04' util.substring(dgCargoSpec.Remarks, 211, 70)?.trim()
											'E4440_05' util.substring(dgCargoSpec.Remarks, 281, 70)?.trim()
										}
									}
								}
								
								List<EmergencyContact> contacts = dgCargoSpec.EmergencyContact?.findAll(){it.FirstName || it.LastName}
								contacts.each { current_Contact ->
									'Group16_CTA' {
										'CTA' {
											'E3139_01' 'HE'
											'C056_02' {
												'E3413_01' ''
												def defVal = current_Contact.FirstName==null?'':current_Contact.FirstName.trim()
												if (current_Contact.LastName) {
													defVal = (defVal + ' ' + current_Contact.LastName).trim()
												}
												'E3412_02' defVal
											}
										}
										if (current_Contact.ContactPhone?.Number) {
											'COM' {
												'C076_01' {
													'E3148_01' current_Contact.ContactPhone?.Number.trim()
													'E3155_02' 'TE'
												}
											}
										}
									}
								}
								// end of Group16_CTA
							}
						}
						//end loop of DGCargoSpec
					}
					//end of group_15
				}
			}
			
			//Group_18
			Map<String, Double> cntrTotalWeights = new HashMap<String, Double>()
			def weightUnitConvert = ['KGS':1, 'LBS':0.45, 'TON':1000]
			List<Container> containers = current_Body.ContainerGroup?.Container?.findAll{it!=null && it.CarrCntrSizeType!=null}
			containers.each { current_Cntr ->
				Map defCntrSizeType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null, msgFmtId, 'ContainerType', current_Cntr.CarrCntrSizeType, conn)
				if (defCntrSizeType?.get('EXT_CDE')) {
					current_Cntr.CarrCntrSizeType = defCntrSizeType.get('EXT_CDE')
				}
				if (current_Cntr.GrossWeight?.Weight && current_Cntr.GrossWeight?.WeightUnit && weightUnitConvert.get(current_Cntr.GrossWeight?.WeightUnit)) {
					double valToKGM = Double.parseDouble(current_Cntr.GrossWeight?.Weight) * weightUnitConvert.get(current_Cntr.GrossWeight?.WeightUnit)
					if (cntrTotalWeights.containsKey(current_Cntr.CarrCntrSizeType)) {
						cntrTotalWeights.put(current_Cntr.CarrCntrSizeType, valToKGM + cntrTotalWeights.get(current_Cntr.CarrCntrSizeType))
					} else {
						cntrTotalWeights.put(current_Cntr.CarrCntrSizeType, valToKGM)
					}
				}
			}
			
			containers?.groupBy{it.CarrCntrSizeType}.each { current_CarrCntrSizeType, current_ContainerGroup ->
				'Group18_EQD' {
					Container current_1stCntr = current_ContainerGroup.getAt(0)
					'EQD' {
						'E8053_01' 'CN'
						'C237_02' {
							def cntrNum = current_1stCntr.ContainerNumber==null?'':current_1stCntr.ContainerNumber?.trim()
							if (current_1stCntr.ContainerCheckDigit) {
								cntrNum += current_1stCntr.ContainerCheckDigit?.trim()
							}
							'E8260_01' cntrNum
						}
						'C224_03' {
							//current_CarrCntrSizeType is get code_conversion query by CarrCntrSizeType, use CarrCntrSizeType if not db record found
							'E8155_01' current_CarrCntrSizeType
							'E1131_02' ''
							'E3055_03' ''
							'E8154_04' ''
						}
						if (current_1stCntr.IsSOC?.toLowerCase() == 'true' || current_1stCntr.IsSOC == '1') {
							'E8077_04' '1'
						} else if (current_1stCntr.IsSOC?.toLowerCase() == 'false' || current_1stCntr.IsSOC == '0') {
							'E8077_04' '2'
						}
						'E8249_05' ''
						'E8169_06' ''
					}
					'EQN' {
						'C523_01' {
							'E6350_01' current_ContainerGroup.size()
							'E6353_02' ''
						}
					}
					'MEA' {
						'E6311_01' 'WT'
						'C502_02' {
							'E6313_01' 'G'
						}
						'C174_03' {
							'E6411_01' 'KGM'
							//container group's weight
							double val = 0
							if (cntrTotalWeights.get(current_CarrCntrSizeType)) {
								val = cntrTotalWeights.get(current_CarrCntrSizeType)
							}
							'E6314_02' fmtDecmail3digital.format(val)
						}
					}
					
					//if ReeferCargo found
					def currentGroupAllCntrNumbers = []
					current_ContainerGroup.each { current_CntrInLoop ->
						def cntrNum = current_CntrInLoop.ContainerNumber==null?'':current_CntrInLoop.ContainerNumber?.trim()
						if (! currentGroupAllCntrNumbers.contains(cntrNum)) { 
							currentGroupAllCntrNumbers.add(cntrNum)
						}
					}
					
					Cargo cntrCargoReefer = current_Body.Cargo?.find{it.ReeferCargoSpec?.size()>0 && it.CurrentContainerNumber in currentGroupAllCntrNumbers}
					ReeferCargoSpec reeferSpec = null
					if (cntrCargoReefer) {
						reeferSpec = cntrCargoReefer.ReeferCargoSpec?.get(0)
					} else if (current_Body.Cargo?.size()>0 && current_Body.Cargo?.get(0)?.ReeferCargoSpec?.size()>0) {
						reeferSpec = current_Body.Cargo?.get(0)?.ReeferCargoSpec?.get(0)
					}
					if (reeferSpec) {
						'MEA' {
							'E6311_01' 'AAE'
							'C502_02' {
								'E6313_01' 'AAS'
							}
							if (reeferSpec?.Ventilation?.VentilationUnit == 'cbfPerMin' || reeferSpec.Ventilation?.VentilationUnit == 'cbmPerHour') {
								'C174_03' {
									if (reeferSpec.Ventilation?.VentilationUnit == 'cbfPerMin') {
										'E6411_01' 'CBF'
									} else if (reeferSpec.Ventilation?.VentilationUnit == 'cbmPerHour') {
										'E6411_01' 'CBM'
									}
									//container group's weight
									if (reeferSpec.Ventilation?.Ventilation) {
										'E6314_02' fmtDecmail3digital.format(Double.parseDouble(reeferSpec.Ventilation?.Ventilation))
									}
								}
							}
						}
						if (reeferSpec.Temperature?.Temperature && reeferSpec.Temperature?.TemperatureUnit) {
							String outputVal = getDecimalFormat(reeferSpec.Temperature?.Temperature)
							'TMP' {
								'E6245_01' '2'
								'C239_02' {
									'E6246_01' outputVal
									if (reeferSpec.Temperature?.TemperatureUnit == 'C') {
										'E6411_02' 'CEL'
									} else if (reeferSpec.Temperature?.TemperatureUnit == 'F') {
										'E6411_02' 'FAH'
									}
								}
							}
						}
					}
					//end of Reefer Spec MEA & TMP
					
					
					def defCS1CntrSizeType = current_1stCntr.CS1ContainerSizeType
					def defCSCntrSizeType = current_1stCntr.CSContainerSizeType
					
					//Group_19_NAD limit to output max 9 loops
					int totalGroup19_NAD_Count = 0;
					
					//empty pickup
					current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup?.findAll(){it.Facility?.FacilityName}?.each { current_EmptyPickup ->
						boolean isNeedThisCntr = current_EmptyPickup.ISOSizeType && current_EmptyPickup.ISOSizeType == defCS1CntrSizeType
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_EmptyPickup.ISOSizeType) && current_EmptyPickup.CSContainerSizeType && current_EmptyPickup.CSContainerSizeType == defCSCntrSizeType
						}
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_EmptyPickup.ISOSizeType) && util.isEmpty(current_EmptyPickup.CSContainerSizeType)
						}
						if (isNeedThisCntr && totalGroup19_NAD_Count<9) {
							totalGroup19_NAD_Count ++
							
							'Group19_NAD' {
								'NAD' {
									'E3035_01' 'CK'
									'C058_03' {
										'E3124_01' util.substring(current_EmptyPickup.Contact?.FirstName, 1, 35)
										'E3124_02' util.substring(current_EmptyPickup.Contact?.LastName, 1, 35)
										'E3124_03' util.substring(current_EmptyPickup.Contact?.ContactPhone?.CountryCode, 1, 35)
										'E3124_04' util.substring(current_EmptyPickup.Contact?.ContactPhone?.AreaCode, 1, 35)
										'E3124_05' util.substring(current_EmptyPickup.Contact?.ContactPhone?.Number, 1, 35)
									}
									if (current_EmptyPickup.Facility?.FacilityName) {
										'C080_04' {
											'E3036_01' util.substring(current_EmptyPickup.Facility?.FacilityName.trim(), 1, 35)?.trim()
										}
									}
									if (current_EmptyPickup.Address?.AddressLines?.AddressLine) {
										'C059_05' {
											def addStr = current_EmptyPickup.Address?.AddressLines?.AddressLine[0]
											if (current_EmptyPickup.Address?.AddressLines?.AddressLine.size()>1 && current_EmptyPickup.Address?.AddressLines?.AddressLine[1]) {
												addStr += current_EmptyPickup.Address?.AddressLines?.AddressLine[1]
											}
											if (current_EmptyPickup.Address?.AddressLines?.AddressLine.size()>2 && current_EmptyPickup.Address?.AddressLines?.AddressLine[2]) {
												addStr += current_EmptyPickup.Address?.AddressLines?.AddressLine[2]
											}
											
											if (util.substring(addStr, 1, 35)?.trim()) {
												'E3042_01' util.substring(addStr, 1, 35)?.trim()
											}
											if (util.substring(addStr, 36, 35)?.trim()) {
												'E3042_02' util.substring(addStr, 36, 35)?.trim()
											}
											if (util.substring(addStr, 71, 35)?.trim()) {
												'E3042_03' util.substring(addStr, 71, 35)?.trim()
											}
										}
									}
									'E3164_06' util.substring(current_EmptyPickup.Address?.City, 1, 35)?.trim()
									//'C819_07' {
									//	'E3229_01' util.substring(current_EmptyPickup.Address?.State, 1, 9)?.trim()
									//}
									'E3251_08' current_EmptyPickup.Address?.PostalCode?.trim()
									'E3207_09' util.substring(current_EmptyPickup.Address?.LocationCode?.UNLocationCode, 1, 2)?.trim()
								}
								if (current_EmptyPickup.MvmtDT?.LocDT?.LocDT) {
									'DTM' {
										'C507_01' {
											'E2005_01' '201'
											'E2380_02' util.convertXmlDateTime(current_EmptyPickup.MvmtDT?.LocDT?.LocDT, yyyyMMddHHmm)
											'E2379_03' '203'
										}
									}
								}
							}
						}
					}
					//empty pickup
					
					//full return
					current_Body.ContainerGroup?.ContainerFlowInstruction?.FullReturn?.findAll(){it.Facility?.FacilityName}?.each { current_FullReturn ->
						boolean isNeedThisCntr = current_FullReturn.ISOSizeType && current_FullReturn.ISOSizeType == defCS1CntrSizeType
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_FullReturn.ISOSizeType) && current_FullReturn.CSContainerSizeType && current_FullReturn.CSContainerSizeType == defCSCntrSizeType
						}
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_FullReturn.ISOSizeType) && util.isEmpty(current_FullReturn.CSContainerSizeType)
						}
						if (isNeedThisCntr && totalGroup19_NAD_Count<9) {
							totalGroup19_NAD_Count ++
							
							'Group19_NAD' {
								'NAD' {
									'E3035_01' 'CU'
									'C058_03' {
										'E3124_01' util.substring(current_FullReturn.Contact?.FirstName, 1, 35)
										'E3124_02' util.substring(current_FullReturn.Contact?.LastName, 1, 35)
										'E3124_03' util.substring(current_FullReturn.Contact?.ContactPhone?.CountryCode, 1, 35)
										'E3124_04' util.substring(current_FullReturn.Contact?.ContactPhone?.AreaCode, 1, 35)
										'E3124_05' util.substring(current_FullReturn.Contact?.ContactPhone?.Number, 1, 35)
									}
									if (current_FullReturn.Facility?.FacilityName) {
										'C080_04' {
											'E3036_01' util.substring(current_FullReturn.Facility?.FacilityName.trim(), 1, 35)?.trim()
										}
									}
									if (current_FullReturn.Address?.AddressLines?.AddressLine) {
										'C059_05' {
											def addStr = current_FullReturn.Address?.AddressLines?.AddressLine[0]
											if (current_FullReturn.Address?.AddressLines?.AddressLine.size()>1 && current_FullReturn.Address?.AddressLines?.AddressLine[1]) {
												addStr += current_FullReturn.Address?.AddressLines?.AddressLine[1]
											}
											if (current_FullReturn.Address?.AddressLines?.AddressLine.size()>2 && current_FullReturn.Address?.AddressLines?.AddressLine[2]) {
												addStr += current_FullReturn.Address?.AddressLines?.AddressLine[2]
											}
											
											if (util.substring(addStr, 1, 35)?.trim()) {
												'E3042_01' util.substring(addStr, 1, 35)?.trim()
											}
											if (util.substring(addStr, 36, 35)?.trim()) {
												'E3042_02' util.substring(addStr, 36, 35)?.trim()
											}
											if (util.substring(addStr, 71, 35)?.trim()) {
												'E3042_03' util.substring(addStr, 71, 35)?.trim()
											}
										}
									}
									'E3164_06' util.substring(current_FullReturn.Address?.City, 1, 35)?.trim()
									//'C819_07' {
									//	'E3229_01' util.substring(current_FullReturn.Address?.State, 1, 9)?.trim()
									//}
									'E3251_08' current_FullReturn.Address?.PostalCode?.trim()
									'E3207_09' util.substring(current_FullReturn.Address?.LocationCode?.UNLocationCode, 1, 2)?.trim()
								}
								if (current_FullReturn.MvmtDT?.LocDT?.LocDT) {
									'DTM' {
										'C507_01' {
											'E2005_01' '116'
											'E2380_02' util.convertXmlDateTime(current_FullReturn.MvmtDT?.LocDT?.LocDT, yyyyMMddHHmm)
											'E2379_03' '203'
										}
									}
								}
							}
						}
					}
					//full return
					
					//Appointment
					List<Appointment> apps = new ArrayList<Appointment>() 
					current_Body.Appointment?.findAll{it.Type == 'PICKUP' || it.Type == 'DELIVERY'}?.each { current_Appointment ->
						String cntrNum = current_Appointment.Container?.ContainerNumber
						if (cntrNum==null) {
							cntrNum = ''
						} else {
							cntrNum = cntrNum.trim()
						}
						String curLoopCntrNum = current_1stCntr.ContainerNumber==null?'':current_1stCntr.ContainerNumber?.trim()
						if (cntrNum == curLoopCntrNum) {
							apps.add(current_Appointment)
						}
					}
					apps?.each { current_Appointment ->
						if (totalGroup19_NAD_Count<9 && (current_Appointment.AppointmentDT?.LocDT?.LocDT || current_Appointment.Contact?.FirstName)) {
							totalGroup19_NAD_Count ++
							
							'Group19_NAD' {
								'NAD' {
									if (current_Appointment.Type == 'PICKUP') {
										'E3035_01' 'SF'
									} else if (current_Appointment.Type == 'DELIVERY') {
										'E3035_01' 'ST'
									}
									if (current_Appointment.Contact?.FirstName) {
										'C058_03' {
											'E3124_01' util.substring(current_Appointment.Contact?.FirstName, 1, 35)
											'E3124_02' util.substring(current_Appointment.Contact?.LastName, 1, 35)
											'E3124_03' util.substring(current_Appointment.Contact?.ContactPhone?.CountryCode, 1, 35)
											'E3124_04' util.substring(current_Appointment.Contact?.ContactPhone?.AreaCode, 1, 35)
											'E3124_05' util.substring(current_Appointment.Contact?.ContactPhone?.Number, 1, 35)
										}
									}
									if (current_Appointment.Address?.AddressLines?.AddressLine) {
										'C059_05' {
											def addStr = current_Appointment.Address?.AddressLines?.AddressLine[0]
											if (current_Appointment.Address?.AddressLines?.AddressLine.size()>1 && current_Appointment.Address?.AddressLines?.AddressLine[1]) {
												addStr += current_Appointment.Address?.AddressLines?.AddressLine[1]
											}
											if (current_Appointment.Address?.AddressLines?.AddressLine.size()>2 && current_Appointment.Address?.AddressLines?.AddressLine[2]) {
												addStr += current_Appointment.Address?.AddressLines?.AddressLine[2]
											}
											
											if (util.substring(addStr, 1, 35)?.trim()) {
												'E3042_01' util.substring(addStr, 1, 35)?.trim()
											}
											if (util.substring(addStr, 36, 35)?.trim()) {
												'E3042_02' util.substring(addStr, 36, 35)?.trim()
											}
											if (util.substring(addStr, 71, 35)?.trim()) {
												'E3042_03' util.substring(addStr, 71, 35)?.trim()
											}
										}
									}
									'E3164_06' util.substring(current_Appointment.Address?.City, 1, 35)?.trim()
									//'C819_07' {
									//	'E3229_01' util.substring(current_Appointment.Address?.State, 1, 9)?.trim()
									//}
									'E3251_08' current_Appointment.Address?.PostalCode?.trim()
									'E3207_09' util.substring(current_Appointment.Address?.LocationCode?.UNLocationCode, 1, 2)?.trim()
								}
								if (current_Appointment.AppointmentDT?.LocDT?.LocDT) {
									'DTM' {
										'C507_01' {
											if (current_Appointment.Type == 'PICKUP') {
												'E2005_01' '201'
											} else if (current_Appointment.Type == 'DELIVERY') {
												'E2005_01' '17'
											}
											'E2380_02' util.convertXmlDateTime(current_Appointment.AppointmentDT?.LocDT?.LocDT, yyyyMMddHHmm)
											'E2379_03' '203'
										}
									}
								}
							}
						}
					}
					//end of Appointment
				}
			}
		
			//end of txn
			'UNT' {
				'E0074_01' '-999'
				'E0062_02' '-999'
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
		def IFTMBC = outXml.createNode('IFTMBC')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.

		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def headerMsgDT = ''
		if (bc.Header?.MsgDT?.LocDT?.LocDT) {
			headerMsgDT = util.convertXmlDateTime(bc.Header?.MsgDT?.LocDT?.LocDT, yyyyMMddHHmmss)
		} else if (bc.Header?.MsgDT?.GMT) {
			headerMsgDT = util.convertXmlDateTime(bc.Header?.MsgDT?.GMT, yyyyMMddHHmmss)
		} else {
			headerMsgDT = currentDate.format(yyyyMMddHHmmss)
		}
		def txnErrorKeys = []

		//Start mapping

		bc?.Body?.eachWithIndex{current_Body, current_BodyIndex ->
			//prep checking
			List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
			// prep validation
			prepValidation(current_Body, current_BodyIndex, errorKeyList)
			
			if(current_Body?.GeneralInformation?.BookingStatus=='Declined'||current_Body?.GeneralInformation?.BookingStatus=='Rejected') {
				//reject / declined mapping
				generateBody4RejectCase(current_Body, outXml)
			} else {
				//normal mapping
				generateBody(current_Body, outXml)
			}

			// posp checking
			if (errorKeyList.isEmpty()) {
				pospValidation(current_Body, writer?.toString(), errorKeyList)
			}

			bcUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			bcUtil.buildBizKey(bizKeyXml, bc.Header, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, TP_ID, conn)

			txnErrorKeys.add(errorKeyList)
		}

		//End root node
		outXml.nodeCompleted(null, IFTMBC)
		bizKeyXml.nodeCompleted(null, bizKeyRoot)
		csuploadXml.nodeCompleted(null, csuploadRoot)

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

	void prepValidation(Body current_Body, int bodyLoopIndex, List<Map<String,String>> errorKeyList) {
		
		//valid BC status allow to send
		List<String> allowStatusList = new ArrayList<String>()
		allowStatusList.add("Confirmed")
		allowStatusList.add("Cancelled")
		allowStatusList.add("Declined")
		allowStatusList.add("Rejected")
		
		def varBkgStatus = current_Body.GeneralInformation?.BookingStatus
		
		//1. check booking confirm status
		bcUtil.checkBCStatus(varBkgStatus, allowStatusList, false, "Filter by BookingStatus: ", errorKeyList)
		
		//2. carrier booking number is mandatory
		bcUtil.checkCarrierBookingNumber(current_Body, true, "Missing mandatory Carrier Booking Number.", errorKeyList)
		
		//3. check if OOLU reject bc has Shipper Ref or not
		bcUtil.checkShipperRefNonNullForOOLUReject(current_Body, true, null, errorKeyList)
		
		//4. CheckExternalRefCSBKGForRejected
		bcUtil.checkExternalRefCSBKGForRejected(current_Body, false, null, errorKeyList)
	}
	
	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList) {

//		XmlParser xmlParser = new XmlParser();
//		Node node = xmlParser.parseText(outputXml + "</IFTMBC>")

//		bcUtil.checkB102Mandatory(current_Body, node, true, null, errorKeyList)
//		bcUtil.checkN101Mandatory(current_Body, node, true, null, errorKeyList)
//		bcUtil.checkY3_01Mandatory(node, true, null, errorKeyList)

	}
	
	void generateBody4RejectCase(Body current_Body, MarkupBuilder outXml) {
		//rejected bc should be extracted to a method
		outXml.'Group_UNH' {
			'UNH' {
				'E0062_01' '-999'
				'S009_02' {
					'E0065_01' 'IFTMBC'
					'E0052_02' 'D'
					'E0054_03' '99B'
					'E0051_04' 'UN'
					'E0057_05' '2.0'
					'E0110_06' ''
					'E0113_07' ''
				}
				'E0068_03' ''
				'S010_04' {
					'E0070_01' ''
					'E0073_02' ''
				}
			}
			
			'BGM' {
				'C002_01' {
					'E1001_01' '770'
					'E1131_02' ''
					'E3055_03' ''
					'E1000_04' ''
				}
				'C106_02' {
					'E1004_01' currentDate.format(yyyyMMddHHmmss)
					'E1056_02' ''
					'E1060_03' ''
				}
				'E1225_03' '1'
				'E4343_04' ''
			}
			
			'CTA' {
				'E3139_01' 'CW'
				'C056_02' {
					'E3413_01' ''
					'E3412_02' 'Hong Kong Office'
				}
			}
			
			'COM' {
				'C076_01' {
					'E3148_01' '+85228333888'
					'E3155_02' 'TE'
				}
			}
			
			'DTM' {
				'C507_01' {
					'E2005_01' '137'
					if (current_Body.EventInformation?.EventDT?.LocDT?.LocDT) {
						'E2380_02' util.convertXmlDateTime(current_Body.EventInformation?.EventDT?.LocDT?.LocDT, yyyyMMddHHmm);
					} else if (current_Body.EventInformation?.EventDT?.GMT) {
						'E2380_02' util.convertXmlDateTime(current_Body.EventInformation?.EventDT?.GMT, yyyyMMddHHmm);
					} else {
						'E2380_02' currentDate.format(yyyyMMddHHmm)
					}
					'E2379_03' '203'
				}
			}
			
			'FTX' {
				'E4451_01' 'AAI'
				'C108_04' {
					if (current_Body.GeneralInformation?.BookingStatusRemarks) {
						'E4440_01' util.substring(current_Body.GeneralInformation?.BookingStatusRemarks, 1, 512)?.trim()
					} else {
						'E4440_01' 'Carrier has cancelled booking on request of customer.'
					}
				}
			}
			
			def currentBcScac = current_Body.GeneralInformation?.SCACCode
			'Group2_RFF' {
				'RFF' {
					'C506_01' {
						'E1153_01' 'BN'
						if (currentBcScac == 'OOLU' && current_Body.GeneralInformation?.CarrierBookingNumber) {
							'E1154_02' current_Body.GeneralInformation?.CarrierBookingNumber
						} else {
							'E1154_02' current_Body.GeneralInformation?.CSBookingRefNumber
						}
					}
				}
			}
			
			if (currentBcScac == 'OOLU' && current_Body.GeneralInformation?.CarrierBookingNumber) {
				'Group2_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' 'BM'
							if (current_Body.GeneralInformation?.CarrierBookingNumber?.trim()?.startsWith('OOLU')) {
								'E1154_02' current_Body.GeneralInformation?.CarrierBookingNumber?.trim()
							} else {
								'E1154_02' 'OOLU' + current_Body.GeneralInformation?.CarrierBookingNumber
							}
						}
					}
				}
			}
			
			if (currentBcScac == 'OOLU' && current_Body.GeneralInformation?.BookingStatusRemarks) {
				'Group2_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' 'FF'
							def defRemark = current_Body.GeneralInformation?.BookingStatusRemarks
							if (defRemark.indexOf('|')>0) {
								defRemark = defRemark.substring(0, defRemark.indexOf('|'))?.trim()
							}
							if (util.isEmpty(defRemark)) {
								defRemark = current_Body.GeneralInformation?.BookingStatusRemarks
							}
							'E1154_02' defRemark
						}
					}
				}
			}
			
			def bizkeyVal = bcUtil.bizkeyCSBkRefNumQuery('FR', current_Body.GeneralInformation?.CSBookingRefNumber, conn)
			if (bizkeyVal) {
				'Group2_RFF' {
					'RFF' {
						'C506_01' {
							if (currentBcScac == 'OOLU') {
								'E1153_01' 'SI'
							} else {
								'E1153_01' 'FF'
							}
							'E1154_02' bizkeyVal
						}
					}
				}
			}
			
			//Group_6
			//CAR - CA
			'Group6_NAD' {
				'NAD' {
					'E3035_01' 'CA'
					if (currentBcScac) {
						'C082_02' {
							'E3039_01' currentBcScac
							'E1131_02' '160'
							'E3055_03' '86'
						}
						'C058_03' {
							if (currentBcScac=='OOLU') {
								'E3124_01' 'Orient Overseas Container Line Ltd.'
							} else {
								Map<String, String> scacInfo = util.getOceanCarrierInfo(currentBcScac, conn)
								if (scacInfo && scacInfo.get('NAME')) {
									'E3124_01' scacInfo.get('NAME')
								} else {
									'E3124_01' currentBcScac
								}
							}
						}
					}
				}
			}
			
			//end of txn
			'UNT' {
				'E0074_01' '-999'
				'E0062_02' '-999'
			}
		}
	}
	
	
	//copy from tibco for DUMMYBCD99B
	String getDecimalFormat(String INPUT) {
		String OUTPUT = "";
		String numbers = "";
		String digitals = "";
							
		if (INPUT.startsWith("-")) {
			OUTPUT = "-";
			INPUT = INPUT.substring(1);
		}
		if (INPUT.indexOf(".")>=0) {
			digitals = INPUT.substring(INPUT.indexOf(".")+1);
			numbers = INPUT.substring(0, INPUT.indexOf("."));
			if (digitals.length()>=1) {
				digitals = digitals.substring(0, 1);
			}
		} else {
			numbers = INPUT;
		}
		if (numbers.length()>=3) {
			OUTPUT += numbers;
		} else if (numbers.length()==2 && digitals.length()==1) {
			OUTPUT += numbers + "." + digitals;
		} else if (numbers.length()==2 && digitals.length()==0) {
			OUTPUT += "0" + numbers;
		} else if (numbers.length()==1 && digitals.length()==1) {
			OUTPUT += "0" + numbers + "." + digitals;
		} else if (numbers.length()==1 && digitals.length()==0) {
			OUTPUT +=  "00" + numbers;
		} else if (numbers.length()==0 && digitals.length()==1) {
			OUTPUT += "00." + digitals;
		} else if (numbers.length()==0 && digitals.length()==0) {
			OUTPUT += "000";
		}
		return OUTPUT;
	}
}
