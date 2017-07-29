package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.math.RoundingMode
import java.sql.Connection
import java.text.DecimalFormat

import cs.b2b.core.mapping.bean.EmergencyContact
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.Cargo
import cs.b2b.core.mapping.bean.bc.Container
import cs.b2b.core.mapping.bean.bc.DGCargoSpec
import cs.b2b.core.mapping.bean.bc.OceanLeg
import cs.b2b.core.mapping.bean.bc.Party
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.core.mapping.bean.bc.RemarkLines
import cs.b2b.core.mapping.util.XmlBeanParser


public class CUS_CS2BCXML_D99B_RIEGE {

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
	
	def currentSystemDt = null
	DecimalFormat decimalFormatter = new DecimalFormat("#.####")
	DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")
	
	def msgFmtId = "EDIFACT"
	
	
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
					def defRef = current_Body.ExternalReference.find { it.ReferenceDescription && it.ReferenceDescription.toUpperCase() == 'SHIPPER REFERENCE'}?.ReferenceNumber
					if (util.isEmpty(defRef)) {
						defRef = current_Body.ExternalReference.find { it.ReferenceDescription && it.ReferenceDescription.toUpperCase() == 'FORWARDER REFERENCE'}?.ReferenceNumber
						if (util.isEmpty(defRef)) {
							defRef = "N/A"
						}
					}
					'E1004_01' util.substring(defRef.trim(), 1, 35)
					'E1056_02' ''
					'E1060_03' ''
				} 
				'E1225_03' '6'
				def statusMap = ['Confirmed':'AP', 'Cancelled':'RE', 'Declined':'RE', 'No Show':'RE']
				'E4343_04' statusMap.get(current_Body.GeneralInformation?.BookingStatus)
			}
			
			'CTA' {
				'E3139_01' 'CW'
				'C056_02' {
					'E3413_01' ''
					'E3412_02' 'CargoSmart Support'
				}
			}
			
			'COM' {
				'C076_01' {
					'E3148_01' '14083257600'
					'E3155_02' 'TE'
				}
			}
			
			
			def vEventDt = '';
			if (current_Body.EventInformation?.EventDT?.LocDT?.LocDT) {
				vEventDt = current_Body.EventInformation?.EventDT?.LocDT?.LocDT
			} else {
				vEventDt = current_Body.EventInformation?.EventDT?.GMT;
			}
			if (vEventDt) {
				'DTM' {
					'C507_01' {
						'E2005_01' '137'
						'E2380_02' util.convertXmlDateTime(vEventDt, yyyyMMddHHmm);
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
						'E7273_01' '2'
					}
				}
			}
			
			//if exists(Body/RemarkLines[@RemarkType = "Booking"])
			List<RemarkLines> remarks = current_Body.RemarkLines.findAll(){it.attr_RemarkType == 'Booking'}
			if (remarks) {
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
				'FTX' {
					'E4451_01' 'AAI'
					'C108_04' {
						if (util.substring(remarksContent, 1, 512)?.trim()) {
							'E4440_01' util.substring(remarksContent, 1, 512)
						}
						if (util.substring(remarksContent, 513, 512)?.trim()) {
							'E4440_02' util.substring(remarksContent, 513, 512)
						}
					}
				}
			}
			
			//Group_2
			current_Body.ExternalReference?.each { current_ExtRef -> 
				if (current_ExtRef.ReferenceDescription && current_ExtRef.ReferenceNumber) {
					def convertVal = util.getEDICdeReffromIntCde(TP_ID, 'EXTERNAL_REF', 'O', 'BCCS2X', 'Group2_RFF_C506', '1153', current_ExtRef.ReferenceDescription.toUpperCase(), conn)
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
			
			current_Body.CarrierRate?.each { current_CarRate ->
				if (current_CarRate.CSCarrierRateType && current_CarRate.CarrierRateNumber) {
					def convertVal = util.getEDICdeReffromIntCde(TP_ID, 'EXTERNAL_REF', 'O', 'BCCS2X', 'Group2_RFF_C506', '1153', current_CarRate.CSCarrierRateType.toUpperCase(), conn)
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
			
			
			//Group_3 Route
			OceanLeg oceanLeg = current_Body.Route?.OceanLeg?.find(){it.SVVD?.Loading?.Voyage || it.SVVD?.Loading?.VesselName}
			
			if (oceanLeg) {
				'Group3_TDT' {
					'TDT' {
						'E8051_01' '20'
						if (oceanLeg.SVVD?.Loading?.Voyage) {
							String defDir = (oceanLeg.LoadingDirectionName==null?"":oceanLeg.LoadingDirectionName.trim())
							'E8028_02' util.substring(oceanLeg.SVVD?.Loading?.Voyage?.trim()?.toUpperCase() + defDir.toUpperCase(), 1, 17)
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
							'E1131_02' '172'
							'E3055_03' ''
							'E3128_04' ''
						}
						'C222_08' {
							if (oceanLeg.SVVD?.Loading?.LloydsNumber) {
								'E8213_01' util.substring(oceanLeg.SVVD?.Loading?.LloydsNumber.trim(), 1, 20)
							}
							'E1131_02' ''
							'E3055_03' '11'
							if (oceanLeg.SVVD?.Loading?.VesselName) {
								'E8212_04' util.substring(oceanLeg.SVVD?.Loading?.VesselName?.trim(), 1, 35)
							}
							'E8453_05' ''
						}
						'E8281_09' ''
					}
					
					// Group_3/Group_4 - FND
					if (current_Body.Route?.FND?.CityDetails?.City || current_Body.Route?.FND?.CityDetails?.State || current_Body.Route?.FND?.CityDetails?.Country) {
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '7'
								'C517_02' {
									def portVal = current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
									if (util.isEmpty(portVal)) {
										portVal = current_Body.Route?.FND?.CSStandardCity?.CSParentCityID
									}
									if (portVal) {
										'E3225_01' util.substring(portVal, 1, 25)
									}
									'E1131_02' '139'
									if (current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
										'E3055_03' '6'
									} else if (current_Body.Route?.FND?.CSStandardCity?.CSParentCityID) {
										'E3055_03' 'ZZZ'
									}
									if (current_Body.Route?.FND?.CityDetails?.City) {
										'E3224_04' util.substring(current_Body.Route?.FND?.CityDetails?.City.trim(), 1, 256)
									}
								}
							}
							
							def defFinalAt = current_Body.Route?.ArrivalAtFinalHub?.find(){it.attr_Indicator == 'A'}?.LocDT?.LocDT
							def defFinalEt = current_Body.Route?.ArrivalAtFinalHub?.find(){it.attr_Indicator == 'E'}?.LocDT?.LocDT
							def defFinalLast = ''
							if (current_Body.Route?.ArrivalAtFinalHub) {
								defFinalLast = current_Body.Route?.ArrivalAtFinalHub[-1]?.LocDT?.LocDT
							}
							def defLasPodDt = current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT
							if (defFinalAt || defFinalEt || defFinalLast || defLasPodDt) {
								'DTM' {
									'C507_01' {
										'E2005_01' '132'
										if (defFinalAt) {
											'E2380_02' util.convertXmlDateTime(defFinalAt, yyyyMMddHHmm)
										} else if (defFinalEt) {
											'E2380_02' util.convertXmlDateTime(defFinalEt, yyyyMMddHHmm)
										} else if (defFinalLast) {
											'E2380_02' util.convertXmlDateTime(defFinalLast, yyyyMMddHHmm)
										} else if (defLasPodDt) {
											'E2380_02' util.convertXmlDateTime(defLasPodDt, yyyyMMddHHmm)
										}
										'E2379_03' '203'
									}
								}
							}
						}
					}
					
					// Group_3/Group_4 - FirstPOL
					if (current_Body.Route?.FirstPOL?.Port?.City || current_Body.Route?.FirstPOL?.Port?.State || current_Body.Route?.FirstPOL?.Port?.Country) {
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '9'
								'C517_02' {
									def portVal = current_Body.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode
									if (util.isEmpty(portVal)) {
										portVal = current_Body.Route?.FirstPOL?.Port?.CSPortID
									}
									if (portVal) {
										'E3225_01' util.substring(portVal, 1, 25)
									}
									'E1131_02' '139'
									if (current_Body.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
										'E3055_03' '6'
									} else if (current_Body.Route?.FirstPOL?.Port?.CSPortID) {
										'E3055_03' 'ZZZ'
									}
									if (current_Body.Route?.FirstPOL?.Port?.PortName) {
										'E3224_04' util.substring(current_Body.Route.FirstPOL.Port.PortName.trim(), 1, 256)
									}
								}
							}
							//Body/Route/FirstPOL/DepartureDT/LocDT
							if (current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT) {
								'DTM' {
									'C507_01' {
										'E2005_01' '133'
										'E2380_02' util.convertXmlDateTime(current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT, yyyyMMddHHmm)
										'E2379_03' '203'
									}
								}
							}
						}
					}
					
					// Group_3/Group_4 - LastPOD
					if (current_Body.Route?.LastPOD?.Port?.City || current_Body.Route?.LastPOD?.Port?.State || current_Body.Route?.LastPOD?.Port?.Country) {
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '11'
								'C517_02' {
									def portVal = current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode
									if (! portVal) {
										portVal = current_Body.Route?.LastPOD?.Port?.CSPortID
									}
									if (portVal) {
										'E3225_01' util.substring(portVal, 1, 25)
									}
									'E1131_02' '139'
									if (current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
										'E3055_03' '6'
									} else if (current_Body.Route?.LastPOD?.Port?.CSPortID) {
										'E3055_03' 'ZZZ'
									}
									if (current_Body.Route?.LastPOD?.Port?.PortName) {
										'E3224_04' util.substring(current_Body.Route.LastPOD.Port.PortName.trim(), 1, 256)
									}
								}
							}
							//Body/Route/LastPOD/ArrivalDT/LocDT
							if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT) {
								'DTM' {
									'C507_01' {
										'E2005_01' '132'
										'E2380_02' util.convertXmlDateTime(current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT, yyyyMMddHHmm)
										'E2379_03' '203'
									}
								}
							}
						}
					}
					
					// Group_3/Group_4 - POR
					if (current_Body.Route?.POR?.CityDetails?.City || current_Body.Route?.POR?.CityDetails?.State || current_Body.Route?.POR?.CityDetails?.Country) {
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '88'
								'C517_02' {
									def portVal = current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
									if (! portVal) {
										portVal = current_Body.Route?.POR?.CSStandardCity?.CSParentCityID
									}
									if (portVal) {
										'E3225_01' util.substring(portVal, 1, 25)
									}
									'E1131_02' '139'
									if (current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
										'E3055_03' '6'
									} else if (current_Body.Route?.POR?.CSStandardCity?.CSParentCityID) {
										'E3055_03' 'ZZZ'
									}
									if (current_Body.Route?.POR?.CityDetails?.City) {
										'E3224_04' util.substring(current_Body.Route.POR?.CityDetails?.City.trim(), 1, 256)
									}
								}
							}
							
							def defInbound = current_Body.Route?.Haulage?.InBound
							def defFullReturnCutoff = current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT
							def defDeliveryDt = current_Body.Route?.ReqDeliveryDT?.LocDT?.LocDT
							
							if ((util.isEmpty(defInbound) || defInbound=='C')) {
								if (defFullReturnCutoff) {
									'DTM' {
										'C507_01' {
											'E2005_01' '63'
											'E2380_02' util.convertXmlDateTime(defFullReturnCutoff, yyyyMMddHHmm)
											'E2379_03' '203'
										}
									}
								} else if (defDeliveryDt) {
									'DTM' {
										'C507_01' {
											'E2005_01' '133'
											'E2380_02' util.convertXmlDateTime(defDeliveryDt, yyyyMMddHHmm)
											'E2379_03' '203'
										}
									}
								}
							}
						}
					}
				}
			}
			//end Group_3
			
			// Group_6
			Map partyTypeMap = ['BPT':'ZZZ', 'BRK':'BR', 'CAR':'CA', 'CGN':'CN', 'FWD':'FW', 'SHP':'CZ']
			
			List<Party> parties = current_Body.Party.findAll(){it.PartyType in partyTypeMap.keySet()}
			parties.each { current_Party ->
				'Group6_NAD' {
					'NAD' {
						def partyType = partyTypeMap.get(current_Party.PartyType)
						'E3035_01' partyType

						if (current_Party.CarrierCustomerCode) {
							Map<String, String> cccMap = util.getCdeConversionFromIntCde(TP_ID,"BC","O",current_Body.GeneralInformation.SCACCode,'EDIFACT','CarrierCustCode',current_Party.CarrierCustomerCode, conn)
							'C082_02' {
								if(cccMap.get("EXT_CDE") && current_Party.PartyType != 'CAR') {
									'E3039_01' cccMap.get("EXT_CDE")
								}else{
									'E3039_01' current_Party.CarrierCustomerCode
								}
								'E1131_02' '160'
								'E3055_03' '87'
							}
						}
						
						if (current_Party.PartyName) {
							'C080_04' {
								'E3036_01' util.substring(current_Party.PartyName, 1, 35).trim()
								if (util.substring(current_Party.PartyName, 36, 35)) {
									'E3036_02' util.substring(current_Party.PartyName, 36, 35).trim()
								}
								if (util.substring(current_Party.PartyName, 71, 35)) {
									'E3036_03' util.substring(current_Party.PartyName, 71, 35).trim()
								}
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
						if (partyType!='CA' && addressLine) {
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
							'E3164_06' util.substring(current_Party?.Address?.City, 1, 35).trim()
						}
						'C819_07' {
							HashMap<String, String> cityResult = util.getCS2MasterCityByStateNameCityNameCntryCde(current_Party.Address?.State, current_Party.Address?.City, current_Party.Address?.Country, conn)
							if (cityResult && cityResult.size()>0) {
								'E3229_01' cityResult.get('STATE_CDE')
							}
							'E1131_02' ''
							'E3055_03' ''
							if (current_Party.Address?.Country) {
								'E3228_04' current_Party.Address?.Country
							}
						}
						if (current_Party?.Address?.PostalCode) {
							'E3251_08' util.substring(current_Party?.Address?.PostalCode, 1, 17).trim()
						}
					}
					if (current_Party.Contact?.FirstName || current_Party.Contact?.LastName) {
						'Group7_CTA' {
							'CTA' {
								'E3139_01' 'IC'
								'C056_02' {
									def name = current_Party.Contact?.FirstName==null?'':current_Party.Contact?.FirstName.trim()
									if (current_Party.Contact?.LastName) {
										name = name + ' ' + current_Party.Contact?.LastName
										name = name.trim()
									}
									'E3413_01' util.substring(name, 1, 17)
									'E3412_02' ''
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
				}
			}
			
			def mapSCAC = ['COSU':'COSCO CONTAINER LINES', 'MOLU':'Mitsui OSK Lines', 'OOLU':'Orient Overseas Container Line Ltd.']
			//CAR - CA
			'Group6_NAD' {
				'NAD' {
					'E3035_01' 'CA'
					if (current_Body.GeneralInformation?.SCACCode) {
						'C082_02' {
							'E3039_01' current_Body.GeneralInformation?.SCACCode
							'E1131_02' '160'
							'E3055_03' '87'
						}
					}
					'C080_04' {
						'E3036_01' mapSCAC.get(current_Body.GeneralInformation?.SCACCode)
					}
					'C059_05' {
						'E3042_01' ''
					}
				}
			}
			//end of Group_6 Party
			
			//Group_9
			List<Cargo> cargos = current_Body.Cargo
			cargos.eachWithIndex { cargo, currentIndex ->
				'Group9_GID' {
					'GID' {
						'E1496_01' (currentIndex+1)
						if (cargo.Packaging?.PackageQty) {
							'C213_02' {
								'E7224_01' cargo.Packaging?.PackageQty
								'E7065_02' 'PCS'
							}
						}
					}
					ReeferCargoSpec refSpec = cargo.ReeferCargoSpec?.find() {it.Temperature?.Temperature && it.Temperature?.TemperatureUnit}
					if (refSpec) {
						'TMP' {
							'E6245_01' '1'
							
							Double dval = Double.parseDouble(refSpec.Temperature?.Temperature)
							String outputVal = refSpec.Temperature?.Temperature
							if (Math.abs(dval)>100) {
								DecimalFormat df = new DecimalFormat("0");
								df.setRoundingMode(RoundingMode.HALF_UP);
								outputVal = df.format(dval);
							} else if (Math.abs(dval)>10) {
								if (outputVal.indexOf(".")>=0) {
									DecimalFormat df = new DecimalFormat("0.0");
									df.setRoundingMode(RoundingMode.HALF_UP);
									outputVal = df.format(dval);
								}
							}
							outputVal = getDecimalFormat(outputVal)
							'C239_02' {
								'E6246_01' outputVal
								if (refSpec.Temperature?.TemperatureUnit == 'C') {
									'E6411_02' 'CEL'
								} else if (refSpec.Temperature?.TemperatureUnit == 'F') {
									'E6411_02' 'FAH'
								}
							}
						}
					}
					def desc = ''
					if (cargo.Packaging?.PackageDesc) {
						desc = cargo.Packaging?.PackageDesc
					} else if (cargo.CargoDescription) {
						desc = cargo.CargoDescription
					}
					if (desc) {
						desc = desc.replace("\n", " ")
						desc = desc.replace("\r", " ")
						'FTX' {
							'E4451_01' 'AAA'
							'E4453_02' ''
							'C108_04' {
								'E4440_01' util.substring(desc, 1, 512)
								if (util.substring(desc, 513, 512)) {
									'E4440_02' util.substring(desc, 513, 512)
								}
							}
						}
					}
					if (cargo.GrossWeight?.Weight && Double.parseDouble(cargo.GrossWeight?.Weight)!=0 && cargo.GrossWeight?.WeightUnit) {
						'Group11_MEA' {
							'MEA' {
								'E6311_01' 'AAE'
								'C502_02' {
									'E6313_01' 'WT'
								}
								'C174_03' {
									if (cargo.GrossWeight?.WeightUnit?.toUpperCase() == 'KGS') {
										'E6411_01' 'KGM'
									} else if (cargo.GrossWeight?.WeightUnit?.toUpperCase() == 'LBS') {
										'E6411_01' 'LBR'
									} else {
										'E6411_01' cargo.GrossWeight?.WeightUnit
									}
									def defVal = (new DecimalFormat("0.000")).format(Double.parseDouble(cargo.GrossWeight?.Weight))
									'E6314_02' defVal
								}
							}
						}
					}
					
					List<DGCargoSpec> dgss = cargo.DGCargoSpec?.findAll(){it.DGRegulator?.trim().toUpperCase() == 'IMD'}
					dgss.each { dgCargoSpec ->
						'Group15_DGS' {
							'DGS' {
								'E8273_01' 'IMD'
								if (dgCargoSpec.IMOClass || dgCargoSpec.IMDGPage) {
									'C205_02' {
										if (dgCargoSpec.IMOClass) {
											'E8351_01' util.substring(dgCargoSpec.IMOClass, 1, 7)
										}
										if (dgCargoSpec.IMDGPage) {
											'E8078_02' util.substring(dgCargoSpec.IMDGPage, 1, 7)
										}
										'E8092_03' ''
									}
								}
								if (dgCargoSpec.UNNumber) {
									'C234_03' {
										'E7124_01' dgCargoSpec.UNNumber
										'E7088_02' ''
									}
								}
								if (dgCargoSpec.FlashPoint?.Temperature && dgCargoSpec.FlashPoint?.TemperatureUnit) {
									'C223_04' {
										def defVal = dgCargoSpec.FlashPoint?.Temperature
										if (dgCargoSpec.FlashPoint?.Temperature.startsWith("-")) {
											defVal = dgCargoSpec.FlashPoint?.Temperature.substring(1)
											for (int i=0; i<3 && defVal.length()<2; i++) {
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
									'E8339_05' util.substring(dgCargoSpec.PackageGroup?.Code, 1, 3)
								}
							}
							if (dgCargoSpec.ProperShippingName) {
								'FTX' {
									'E4451_01' 'AAD'
									'C108_04' {
										'E4440_01' util.substring(dgCargoSpec.ProperShippingName, 1, 512).trim()
									}
								}
							}
							if (dgCargoSpec.TechnicalName) {
								'FTX' {
									'E4451_01' 'AAD'
									'C108_04' {
										'E4440_01' util.substring(dgCargoSpec.TechnicalName, 1, 512).trim()
									}
								}
							}
							if (dgCargoSpec.Remarks) {
								'FTX' {
									'E4451_01' 'AAC'
									'C108_04' {
										'E4440_01' util.substring(dgCargoSpec.Remarks, 1, 512).trim()
									}
								}
							}
							dgCargoSpec.EmergencyContact?.each { current_EmgContact ->
								
							}
							List<EmergencyContact> contacts = dgCargoSpec.EmergencyContact?.findAll(){it.FirstName || it.LastName}
							contacts.each { current_Contact ->
								'Group16_CTA' {
									'CTA' {
										'E3139_01' 'HG'
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
												'E3155_02' 'HG'
											}
										}
									} else {
										current_Body.Party?.findAll(){it.Contact?.ContactPhone?.Number}?.each { curParty ->
											'COM' {
												'C076_01' {
													'E3148_01' curParty.Contact?.ContactPhone?.Number.trim()
													'E3155_02' 'HG'
												}
											}
										}
									}
								}
							}
						}
					}
					//end of group_15
				}
			}
			
			//Group_18
			List<Container> containers = current_Body.ContainerGroup?.Container?.findAll{it!=null && it.CarrCntrSizeType!=null}
			containers?.groupBy{it.CarrCntrSizeType}.each { current_CarrCntrSizeType, current_ContainerGroup ->
				'Group18_EQD' {
					'EQD' {
						'E8053_01' 'CN'
						'C224_03' {
							HashMap<String, String> defCntrSizeType = null
							if (currentBcScac) {
								defCntrSizeType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, currentBcScac, msgFmtId, 'ContainerType', current_CarrCntrSizeType, conn)
							}
							if (defCntrSizeType && defCntrSizeType.size()>0) {
								'E8155_01' defCntrSizeType.get("EXT_CDE")
							}
							'E1131_02' '102'
							'E3055_03' '5'
							'E8154_04' ''
						}
						'E8077_04' ''
						'E8249_05' ''
						'E8169_06' ''
					}
					'EQN' {
						'C523_01' {
							'E6350_01' current_ContainerGroup.size()
							'E6353_02' ''
						}
					}
					
					Container cntr = current_ContainerGroup.getAt(0)
					def defCS1CntrSizeType = cntr.CS1ContainerSizeType
					def defCSCntrSizeType = cntr.CSContainerSizeType
					
					//empty pickup
					current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup?.findAll(){it.Facility?.FacilityName}?.each { current_EmptyPickup ->
						boolean isNeedThisCntr = current_EmptyPickup.ISOSizeType && current_EmptyPickup.ISOSizeType == defCS1CntrSizeType
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_EmptyPickup.ISOSizeType) && current_EmptyPickup.CSContainerSizeType && current_EmptyPickup.CSContainerSizeType == defCSCntrSizeType
						}
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_EmptyPickup.ISOSizeType) && util.isEmpty(current_EmptyPickup.CSContainerSizeType)
						}
						if (isNeedThisCntr) {
							'Group19_NAD' {
								'NAD' {
									'E3035_01' 'CK'
									if (current_EmptyPickup.Facility?.FacilityCode) {
										'C082_02' {
											'E3039_01' current_EmptyPickup.Facility?.FacilityCode.trim()
											'E1131_02' '160'
											'E3055_03' '87'
										}
									}
									if (current_EmptyPickup.Facility?.FacilityName) {
										'C080_04' {
											'E3036_01' util.substring(current_EmptyPickup.Facility?.FacilityName.trim(), 1, 35).trim()
										}
									}
									if (current_EmptyPickup.Address?.AddressLines?.AddressLine) {
										'C059_05' {
											if (util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[0], 1, 35).trim()) {
												'E3042_01' util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[0], 1, 35).trim()
											}
											if (current_EmptyPickup.Address?.AddressLines?.AddressLine.size()>1 && util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[1], 1, 35).trim()) {
												'E3042_02' util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[1], 1, 35).trim()
											}
											if (current_EmptyPickup.Address?.AddressLines?.AddressLine.size()>2 && util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[2], 1, 35).trim()) {
												'E3042_03' util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[2], 1, 35).trim()
											}
											if (current_EmptyPickup.Address?.AddressLines?.AddressLine.size()>3 && util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[3], 1, 35).trim()) {
												'E3042_04' util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[3], 1, 35).trim()
											}
										}
										if (current_EmptyPickup.Address?.AddressLines?.AddressLine.size()>4 && util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[4], 1, 35).trim()) {
											'E3164_06' util.substring(current_EmptyPickup.Address?.AddressLines?.AddressLine[4], 1, 35).trim()
										}
									}
								}
								if (current_EmptyPickup.MvmtDT?.LocDT?.LocDT) {
									'DTM' {
										'C507_01' {
											'E2005_01' '181'
											'E2380_02' util.convertXmlDateTime(current_EmptyPickup.MvmtDT?.LocDT?.LocDT, yyyyMMddHHmmss)
											'E2379_03' '203'
										}
									}
								}
							}
						}
					}
					//empty pickup
					
					//empty return
					current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyReturn?.findAll(){it.Facility?.FacilityName}?.each { current_EmptyReturn ->
						boolean isNeedThisCntr = current_EmptyReturn.ISOSizeType && current_EmptyReturn.ISOSizeType == defCS1CntrSizeType
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_EmptyReturn.ISOSizeType) && current_EmptyReturn.CSContainerSizeType && current_EmptyReturn.CSContainerSizeType == defCSCntrSizeType
						}
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_EmptyReturn.ISOSizeType) && util.isEmpty(current_EmptyReturn.CSContainerSizeType)
						}
						if (isNeedThisCntr) {
							'Group19_NAD' {
								'NAD' {
									'E3035_01' 'SF'
									if (current_EmptyReturn.Facility?.FacilityCode) {
										'C082_02' {
											'E3039_01' current_EmptyReturn.Facility?.FacilityCode.trim()
											'E1131_02' '160'
											'E3055_03' '87'
										}
									}
									if (current_EmptyReturn.Facility?.FacilityName) {
										'C080_04' {
											'E3036_01' util.substring(current_EmptyReturn.Facility?.FacilityName.trim(), 1, 35).trim()
										}
									}
									if (current_EmptyReturn.Address?.AddressLines?.AddressLine) {
										'C059_05' {
											if (util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[0], 1, 35).trim()) {
												'E3042_01' util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[0], 1, 35).trim()
											}
											if (current_EmptyReturn.Address?.AddressLines?.AddressLine.size()>1 && util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[1], 1, 35).trim()) {
												'E3042_02' util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[1], 1, 35).trim()
											}
											if (current_EmptyReturn.Address?.AddressLines?.AddressLine.size()>2 && util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[2], 1, 35).trim()) {
												'E3042_03' util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[2], 1, 35).trim()
											}
											if (current_EmptyReturn.Address?.AddressLines?.AddressLine.size()>3 && util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[3], 1, 35).trim()) {
												'E3042_04' util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[3], 1, 35).trim()
											}
										}
										if (current_EmptyReturn.Address?.AddressLines?.AddressLine.size()>4 && util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[4], 1, 35).trim()) {
											'E3164_06' util.substring(current_EmptyReturn.Address?.AddressLines?.AddressLine[4], 1, 35).trim()
										}
									}
								}
								if (current_EmptyReturn.MvmtDT?.LocDT?.LocDT) {
									'DTM' {
										'C507_01' {
											'E2005_01' '181'
											'E2380_02' util.convertXmlDateTime(current_EmptyReturn.MvmtDT?.LocDT?.LocDT, yyyyMMddHHmmss)
											'E2379_03' '203'
										}
									}
								}
							}
						}
					}
					//empty return
					
					//full return
					current_Body.ContainerGroup?.ContainerFlowInstruction?.FullReturn?.findAll(){it.Facility?.FacilityName}?.each { current_FullReturn ->
						boolean isNeedThisCntr = current_FullReturn.ISOSizeType && current_FullReturn.ISOSizeType == defCS1CntrSizeType
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_FullReturn.ISOSizeType) && current_FullReturn.CSContainerSizeType && current_FullReturn.CSContainerSizeType == defCSCntrSizeType
						}
						if (! isNeedThisCntr) {
							isNeedThisCntr = util.isEmpty(current_FullReturn.ISOSizeType) && util.isEmpty(current_FullReturn.CSContainerSizeType)
						}
						if (isNeedThisCntr) {
							'Group19_NAD' {
								'NAD' {
									'E3035_01' 'TR'
									if (current_FullReturn.Facility?.FacilityCode) {
										'C082_02' {
											'E3039_01' current_FullReturn.Facility?.FacilityCode.trim()
											'E1131_02' '160'
											'E3055_03' '87'
										}
									}
									if (current_FullReturn.Facility?.FacilityName) {
										'C080_04' {
											'E3036_01' util.substring(current_FullReturn.Facility?.FacilityName.trim(), 1, 35).trim()
										}
									}
									if (current_FullReturn.Address?.AddressLines?.AddressLine) {
										'C059_05' {
											if (util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[0], 1, 35).trim()) {
												'E3042_01' util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[0], 1, 35).trim()
											}
											if (current_FullReturn.Address?.AddressLines?.AddressLine.size()>1 && util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[1], 1, 35).trim()) {
												'E3042_02' util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[1], 1, 35).trim()
											}
											if (current_FullReturn.Address?.AddressLines?.AddressLine.size()>2 && util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[2], 1, 35).trim()) {
												'E3042_03' util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[2], 1, 35).trim()
											}
											if (current_FullReturn.Address?.AddressLines?.AddressLine.size()>3 && util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[3], 1, 35).trim()) {
												'E3042_04' util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[3], 1, 35).trim()
											}
										}
										if (current_FullReturn.Address?.AddressLines?.AddressLine.size()>4 && util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[4], 1, 35).trim()) {
											'E3164_06' util.substring(current_FullReturn.Address?.AddressLines?.AddressLine[4], 1, 35).trim()
										}
									}
								}
								if (current_FullReturn.MvmtDT?.LocDT?.LocDT) {
									'DTM' {
										'C507_01' {
											'E2005_01' '181'
											'E2380_02' util.convertXmlDateTime(current_FullReturn.MvmtDT?.LocDT?.LocDT, yyyyMMddHHmmss)
											'E2379_03' '203'
										}
									}
								}
							}
						}
					}
					//empty return
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
		currentSystemDt = new Date()
		def headerMsgDT = util.convertDateTime(bc.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		def txnErrorKeys = []

		//Start mapping

		bc?.Body?.eachWithIndex{current_Body, current_BodyIndex ->
			//prep checking
			List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
			// prep validation
			prepValidation(current_Body, current_BodyIndex, errorKeyList)
			
			//mapping
			generateBody(current_Body, outXml)

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
		
	}
	
	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList) {

//		XmlParser xmlParser = new XmlParser();
//		Node node = xmlParser.parseText(outputXml + "</IFTMBC>")

//		bcUtil.checkB102Mandatory(current_Body, node, true, null, errorKeyList)
//		bcUtil.checkN101Mandatory(current_Body, node, true, null, errorKeyList)
//		bcUtil.checkY3_01Mandatory(node, true, null, errorKeyList)

	}
	
	//copy from tibco
	String getDecimalFormat(String INPUT) {
		String OUTPUT = INPUT;
		if (INPUT.startsWith("-")) {
			String signal = INPUT.substring(0,1);
			String remain = INPUT.substring(1);
			
			if (remain.contains(".")) {
				if (remain.length()==2) {
					OUTPUT = signal.concat("00").concat(remain);
				} else if (remain.length()==3) {
					OUTPUT = signal.concat("0").concat(remain);
				} else if (remain.length()==4) {
					OUTPUT = signal.concat(remain);
				} else if (remain.length()>4) {
					OUTPUT = signal.concat(remain.substring(0,4));
				}
			} else {
				if (remain.length()==1) {
					OUTPUT = signal.concat("00").concat(remain);
				} else if (remain.length()==2) {
					OUTPUT = signal.concat("0").concat(remain);
				} else if (remain.length()==3) {
					OUTPUT = signal.concat(remain);
				} else if (remain.length()>3) {
					OUTPUT = signal.concat(remain.substring(0,3));
				}
			}
		} else {
			if (INPUT.contains(".")) {
				if (INPUT.length()==2) {
					OUTPUT = "00".concat(INPUT);
				} else if (INPUT.length()==3) {
					OUTPUT = "0".concat(INPUT);
				} else if (INPUT.length()==4) {
					OUTPUT = INPUT;
				} else if (INPUT.length()>4) {
					OUTPUT = INPUT.substring(1,4);
				}
			} else {
				if (INPUT.length()==1) {
					OUTPUT = "00".concat(INPUT);
				} else if (INPUT.length()==2) {
					OUTPUT = "0".concat(INPUT);
				} else if (INPUT.length()==3) {
					OUTPUT = INPUT;
				} else if (INPUT.length()>3) {
					OUTPUT = INPUT.substring(0,3);
				}
			}
		}
		return OUTPUT;
	}
}
