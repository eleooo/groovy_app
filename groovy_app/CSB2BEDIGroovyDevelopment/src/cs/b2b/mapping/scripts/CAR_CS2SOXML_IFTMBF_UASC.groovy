package cs.b2b.mapping.scripts

import cs.b2b.core.mapping.bean.so.ShippingOrder
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import javax.xml.bind.DatatypeConverter
import java.sql.Connection

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat

class CAR_CS2SOXML_IFTMBF_UASC {
	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

	List pre_validation(ShippingOrder shippingOrder) {

	}

	List post_validation(ShippingOrder iftmbf) {

	}

	Node mapBizKey(ShippingOrder shippingOrder) {

	}

	def mapIFTMBF(ShippingOrder shippingOrder, String TP_ID, Connection conn) {
		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		outXml.'IFTMBF' {
			shippingOrder?.Order.each { currentBody ->
				'Group_UNH' {
					'UNH' {
						'E0062_01' '<replacement_need>'
						'S009_02' {
							'E0065_01' '<replacement_need>'
							'E0052_02' '<replacement_need>'
							'E0054_03' '<replacement_need>'
							'E0051_04' '<replacement_need>'
							'E0057_05' ''
						}
						'E0068_03' ''
						'S010_04' {
							'E0070_01' ''
							'E0073_02' ''
						}
					}
					'BGM' {
						'C002_01' {
							'E1001_01' '220'
							'E1131_02' ''
							'E3055_03' ''
							'E1000_04' 'ShippingOrder'
						}
						'C106_02' {
							'E1004_01' currentBody.GeneralInformation?.CSSORefNumber
							'E1056_02' ''
							'E1060_03' ''
						}
						switch (currentBody.GeneralInformation.ActionType) {
							case 'UPD':
								'E1225_03' '5'
								break
							default:
								'E1225_03' '9'
						}
						'E4343_04' ''
					}
					'CTA' {
						'E3139_01' ''
						'C056_02' {
							'E3413_01' ''
							'E3412_02' ''
						}
					}
					'COM' {
						'C076_01' {
							'E3148_01' ''
							'E3155_02' ''
						}
					}
					'DTM' {
						'C507_01' {
							'E2005_01' '137'
						    'E2380_02' convertXMLDateTime(currentBody.GeneralInformation.Requested.RequestedDT.LocDT, 'yyyyMMddHHss')
							'E2379_03' '203'
						}
					}
					'TSR' {
						'C536_01' {
							String outBound = currentBody.GeneralInformation?.Haulage?.OutBound
							String inBound = currentBody.GeneralInformation?.Haulage?.InBound
							if (outBound == 'C' && inBound == 'C') 'E4065_01' '27'
							if (outBound == 'C' && inBound == 'M') 'E4065_01' '28'
							if (outBound == 'M' && inBound == 'C') 'E4065_01' '29'
							if (outBound == 'M' && inBound == 'M') 'E4065_01' '30'
							'E1131_02' ''
							'E3055_03' ''
						}
						'C233_02' {
							String outBound = currentBody.GeneralInformation.ShipmentTrafficMode?.OutBound
							String inBound = currentBody.GeneralInformation.ShipmentTrafficMode?.InBound
							if (outBound == 'FCL' && inBound == 'FCL') 'E7273_01' '2'
							if (outBound == 'LCL' && inBound == 'LCL') 'E7273_01' '3'
							'E1131_02' ''
							'E3055_03' ''
							'E7273_04' ''
							'E1131_05' ''
							'E3055_06' ''
						}
						'C537_03' {
							'E4219_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
						'C703_04' {
							'E7085_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
					}
					'MOA' {
						'C516_01' {
							'E5025_01' ''
							'E5004_02' ''
							'E6345_03' ''
							'E6343_04' ''
							'E4405_05' ''
						}
					}
					currentBody?.Remarks.each { currentRemarks ->
						List lines = splitString(currentRemarks, 512)
						if (lines != null) {
							lines.each { line ->
								'FTX' {
									'E4451_01' currentRemarks.attr_RemarkType
									'E4453_02' ''
									'C107_03' {
										'E4441_01' ''
										'E1131_02' ''
										'E3055_03' ''
									}
									'C108_04' {
										'E4440_01' line
										'E4440_02' ''
										'E4440_03' ''
										'E4440_04' ''
										'E4440_05' ''
									}
									'E3453_05' ''
									'E4447_06' ''
								}

							}
						}
					}
					if (util.notEmpty(currentBody.GeneralInformation?.TotalGrossWeight?.Weight)) {
						'CNT' {
							'C270_01' {
								'E6069_01' '7'
								'E6066_02' currentBody.GeneralInformation.TotalGrossWeight.Weight
								'E6411_03' currentBody.GeneralInformation?.TotalGrossWeight.WeightUnit
							}
						}

					}
					if (util.notEmpty(currentBody.GeneralInformation?.TotalNumberOfPackage)) {
						'CNT' {
							'C270_01' {
								'E6069_01' '11'
								'E6066_02' currentBody.GeneralInformation.TotalNumberOfPackage
								'E6411_03' ''
							}
						}
					}
					if (util.notEmpty(currentBody.GeneralInformation?.TotalConsignment?.Volume)) {
						'CNT' {
							'C270_01' {
								'E6069_01' '7'
								'E6066_02' currentBody.GeneralInformation.TotalConsignment.Volume
								'E6411_03' currentBody.GeneralInformation.TotalConsignment?.VolumeUnit
							}
						}

					}
					if (util.notEmpty(currentBody.GeneralInformation?.TotalNumberOfEquipment)) {
						'CNT' {
							'C270_01' {
								'E6069_01' '11'
								'E6066_02' currentBody.GeneralInformation.TotalNumberOfEquipment
								'E6411_03' ''
							}
						}
					}
					'GDS' {
						'C703_01' {
							'E7085_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
					}
					currentBody.GeneralInformation.PlaceOfPayment.each { currentPlaceOfPayment ->
						'Group1_LOC' {
							'LOC' {
								'E3227_01' '57'
								'C517_02' {
									'E3225_01' currentPlaceOfPayment?.LocationCode?.UNLocationCode
									'E1131_02' ''
									'E3055_03' '6'
									'E3224_04' currentPlaceOfPayment?.LocationName
								}
								'C519_03' {
									'E3223_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3222_04' ''
								}
								'C553_04' {
									'E3233_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3232_04' ''
								}
								'E5479_05' ''
							}
							'DTM' {
								'C507_01' {
									'E2005_01' ''
									'E2380_02' ''
									'E2379_03' ''
								}
							}
						}
					}
					currentBody.GeneralInformation.BillOfLadingReleaseOffice.each { currentBillOfLadingReleaseOffice ->
						'Group1_LOC' {
							'LOC' {
								'E3227_01' '73'
								'C517_02' {
									'E3225_01' currentBillOfLadingReleaseOffice?.LocationCode?.UNLocationCode
									'E1131_02' ''
									'E3055_03' '6'
									'E3224_04' currentBillOfLadingReleaseOffice?.LocationName
								}
								'C519_03' {
									'E3223_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3222_04' ''
								}
								'C553_04' {
									'E3233_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3232_04' ''
								}
								'E5479_05' ''
							}
							if (util.notEmpty(currentBody.GeneralInformation?.IssuranceOfBLDT?.LocDT)) {
								'DTM' {
									'C507_01' {
										'E2005_01' '95'
										//todo convert dateTime
										'E2380_02' convertXMLDateTime(currentBody.GeneralInformation.IssuranceOfBLDT.LocDT, 'yyyyMMddHHss')
										'E2379_03' ''
									}
								}
							}
						}

					}
					'Group2_TOD' {
						'TOD' {
							'E4055_01' ''
							'E4215_02' ''
							'C100_03' {
								'E4053_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E4052_04' ''
								'E4052_05' ''
							}
						}
						'LOC' {
							'E3227_01' ''
							'C517_02' {
								'E3225_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3224_04' ''
							}
							'C519_03' {
								'E3223_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3222_04' ''
							}
							'C553_04' {
								'E3233_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3232_04' ''
							}
							'E5479_05' ''
						}
					}
					currentBody.ExternalReference.each { currentExternalReference ->
						'Group3_RFF' {
							String referenceType = getExternalReferenceType(TP_ID, currentExternalReference.CSReferenceType, conn)
							'RFF' {
								'C506_01' {
									'E1153_01' referenceType
									'E1154_02' currentExternalReference.ReferenceNumber
									'E1156_03' ''
									'E4000_04' ''
									'E1060_05' ''
								}
							}
							if (referenceType == 'LC') {
								if (util.notEmpty(currentBody.GeneralInformation?.ExpiryDateOfLetterOfCredit?.LocDT)) {
									'DTM' {
										'C507_01' {
											'E2005_01' '36'
											'E2380_02' convertXMLDateTime(currentBody.GeneralInformation?.ExpiryDateOfLetterOfCredit?.LocDT, 'yyyyMMdd')
											'E2379_03' '102'
										}
									}
								}
							}
							if (util.notEmpty(currentBody.GeneralInformation?.IssuanceDateOfLetterOfCredit?.LocDT)) {
								'DTM' {
									'C507_01' {
										'E2005_01' '182'
										'E2380_02' convertXMLDateTime(currentBody.GeneralInformation.IssuanceDateOfLetterOfCredit.LocDT, 'yyyyMMdd')
										'E2379_03' '102'
									}
								}
							}
						}
					}
					'Group4_GOR' {
						'GOR' {
							'E8323_01' ''
							'C232_02' {
								'E9415_01' ''
								'E9411_02' ''
								'E9417_03' ''
								'E9353_04' ''
							}
							'C232_03' {
								'E9415_01' ''
								'E9411_02' ''
								'E9417_03' ''
								'E9353_04' ''
							}
							'C232_04' {
								'E9415_01' ''
								'E9411_02' ''
								'E9417_03' ''
								'E9353_04' ''
							}
							'C232_05' {
								'E9415_01' ''
								'E9411_02' ''
								'E9417_03' ''
								'E9353_04' ''
							}
						}
						'FTX' {
							'E4451_01' ''
							'E4453_02' ''
							'C107_03' {
								'E4441_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
							'C108_04' {
								'E4440_01' ''
								'E4440_02' ''
								'E4440_03' ''
								'E4440_04' ''
								'E4440_05' ''
							}
							'E3453_05' ''
							'E4447_06' ''
						}
						'Group5_DOC' {
							'DOC' {
								'C002_01' {
									'E1001_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E1000_04' ''
								}
								'C503_02' {
									'E1004_01' ''
									'E1373_02' ''
									'E1366_03' ''
									'E3453_04' ''
									'E1056_05' ''
									'E1060_06' ''
								}
								'E3153_03' ''
								'E1220_04' ''
								'E1218_05' ''
							}
							'DTM' {
								'C507_01' {
									'E2005_01' ''
									'E2380_02' ''
									'E2379_03' ''
								}
							}
						}
					}
					'Group6_TCC' {
						'TCC' {
							'C200_01' {
								'E8023_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E8022_04' ''
								'E4237_05' ''
								'E7140_06' ''
							}
							'C203_02' {
								'E5243_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E5242_04' ''
								'E5275_05' ''
								'E1131_06' ''
								'E3055_07' ''
								'E5275_08' ''
								'E1131_09' ''
								'E3055_10' ''
							}
							'C528_03' {
								'E7357_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
							'C554_04' {
								'E5243_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
						}
						'LOC' {
							'E3227_01' ''
							'C517_02' {
								'E3225_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3224_04' ''
							}
							'C519_03' {
								'E3223_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3222_04' ''
							}
							'C553_04' {
								'E3233_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3232_04' ''
							}
							'E5479_05' ''
						}
						'FTX' {
							'E4451_01' ''
							'E4453_02' ''
							'C107_03' {
								'E4441_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
							'C108_04' {
								'E4440_01' ''
								'E4440_02' ''
								'E4440_03' ''
								'E4440_04' ''
								'E4440_05' ''
							}
							'E3453_05' ''
							'E4447_06' ''
						}
						'CUX' {
							'C504_01' {
								'E6347_01' ''
								'E6345_02' ''
								'E6343_03' ''
								'E6348_04' ''
							}
							'C504_02' {
								'E6347_01' ''
								'E6345_02' ''
								'E6343_03' ''
								'E6348_04' ''
							}
							'E5402_03' ''
							'E6341_04' ''
						}
						'PRI' {
							'C509_01' {
								'E5125_01' ''
								'E5118_02' ''
								'E5375_03' ''
								'E5387_04' ''
								'E5284_05' ''
								'E6411_06' ''
							}
							'E5213_02' ''
						}
						'EQN' {
							'C523_01' {
								'E6350_01' ''
								'E6353_02' ''
							}
						}
						'PCD' {
							'C501_01' {
								'E5245_01' ''
								'E5482_02' ''
								'E5249_03' ''
								'E1131_04' ''
								'E3055_05' ''
							}
						}
						'MOA' {
							'C516_01' {
								'E5025_01' ''
								'E5004_02' ''
								'E6345_03' ''
								'E6343_04' ''
								'E4405_05' ''
							}
						}
						'QTY' {
							'C186_01' {
								'E6063_01' ''
								'E6060_02' ''
								'E6411_03' ''
							}
						}
					}
					currentBody?.Route.OceanLeg.eachWithIndex { currentOceanLeg, currentOceanLegIndex ->
						'Group7_TDT' {
							'TDT' {
								'E8051_01' '20'
								'E8028_02' currentOceanLeg.SVVD?.Voyage
								'C220_03' {
									'E8067_01' '1'
									'E8066_02' ''
								}
								'C228_04' {
									'E8179_01' ''
									'E8178_02' ''
								}
								'C040_05' {
									'E3127_01' 'UASC'
									'E1131_02' '172'
									'E3055_03' ''
									'E3128_04' ''
								}
								'E8101_06' ''
								'C401_07' {
									'E8457_01' ''
									'E8459_02' ''
									'E7130_03' ''
								}
								'C222_08' {
									'E8213_01' currentOceanLeg.SVVD?.LloydsNumber
									'E1131_02' ''
									'E3055_03' '11'
									'E8212_04' currentOceanLeg.SVVD?.VesselName
									'E8453_05' ''
								}
								'E8281_09' ''
							}
							'DTM' {
								'C507_01' {
									'E2005_01' ''
									'E2380_02' ''
									'E2379_03' ''
								}
							}
							'TSR' {
								'C536_01' {
									'E4065_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
								'C233_02' {
									'E7273_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E7273_04' ''
									'E1131_05' ''
									'E3055_06' ''
								}
								'C537_03' {
									'E4219_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
								'C703_04' {
									'E7085_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
							}
							if (currentOceanLegIndex == 0) {
								def POR = currentBody?.Route.POR
								'Group8_LOC' {
									'LOC' {
										'E3227_01' '88'
										'C517_02' {
											'E3225_01' POR.CityDetails?.LocationCode?.UNLocationCode
											'E1131_02' ''
											'E3055_03' '6'
											'E3224_04' POR?.LocationName
										}
										'C519_03' {
											//todo
											'E3223_01' POR.CityDetails.City.take(25)
											'E1131_02' ''
											'E3055_03' ''
											'E3222_04' ''
										}
										'C553_04' {
											'E3233_01' ''
											'E1131_02' ''
											'E3055_03' ''
											'E3232_04' ''
										}
										'E5479_05' ''
									}
									'DTM' {
										'C507_01' {
											'E2005_01' ''
											'E2380_02' ''
											'E2379_03' ''
										}
									}
								}
							}

							def currentPOL = currentOceanLeg?.POL
							if (util.notEmpty(currentPOL)) {
								'Group8_LOC' {
									'LOC' {
										'E3227_01' '9'
										'C517_02' {
											'E3225_01' currentPOL.Port?.LocationCode?.UNLocationCode
											'E1131_02' ''
											'E3055_03' '6'
											'E3224_04' currentPOL.Port?.PortName
										}
										'C519_03' {
											'E3223_01' currentPOL.Port?.City.take(25)
											'E1131_02' ''
											'E3055_03' ''
											'E3222_04' ''
										}
										'C553_04' {
											'E3233_01' ''
											'E1131_02' ''
											'E3055_03' ''
											'E3232_04' ''
										}
										'E5479_05' ''
									}
									if (util.notEmpty(currentOceanLeg.ETD?.LocDT)) {
										'DTM' {
											'C507_01' {
												'E2005_01' '133'
												'E2380_02' convertXMLDateTime(currentOceanLeg.ETD.LocDT, 'yyyyMMdd')
												'E2379_03' '102'
											}
										}
									}
								}
							}
							def currentPOD = currentOceanLeg?.POD
							if (util.notEmpty(currentPOD)) {
								'Group8_LOC' {
									'LOC' {
										'E3227_01' '11'
										'C517_02' {
											'E3225_01' currentPOD.Port?.LocationCode?.UNLocationCode
											'E1131_02' ''
											'E3055_03' '6'
											'E3224_04' currentPOD.Port?.PortName
										}
										'C519_03' {
											'E3223_01' currentPOD.Port?.City.take(25)
											'E1131_02' ''
											'E3055_03' ''
											'E3222_04' ''
										}
										'C553_04' {
											'E3233_01' ''
											'E1131_02' ''
											'E3055_03' ''
											'E3232_04' ''
										}
										'E5479_05' ''
									}
									'DTM' {
										'C507_01' {
											'E2005_01' ''
											'E2380_02' ''
											'E2379_03' ''
										}
									}
								}
							}
							//check if it is the last ocean leg
							if (currentOceanLegIndex == currentBody.Route.findAll { it.OceanLeg }.size() - 1) {
								def FND = currentBody.Route.FND
								'Group8_LOC' {
									'LOC' {
										'E3227_01' '7'
										'C517_02' {
											'E3225_01' FND.CityDetails?.LocationCode?.UNLocationCode
											'E3055_03' '6'
											'E3224_04' FND?.LocationName
										}
										'C519_03' {
											'E3223_01' FND.CityDetails?.City.take(25)
										}
									}
								}
								'Group8_LOC' {
									'LOC' {
										'E3227_01' '7'
										'C517_02' {
											'E3225_01' FND.CityDetails?.LocationCode?.UNLocationCode
											'E1131_02' ''
											'E3055_03' '6'
											'E3224_04' FND?.LocationName
										}
										'C519_03' {
											'E3223_01' FND.CityDetails?.City.take(25)
											'E1131_02' ''
											'E3055_03' ''
											'E3222_04' ''
										}
										'C553_04' {
											'E3233_01' ''
											'E1131_02' ''
											'E3055_03' ''
											'E3232_04' ''
										}
										'E5479_05' ''
									}
									'DTM' {
										'C507_01' {
											'E2005_01' ''
											'E2380_02' ''
											'E2379_03' ''
										}
									}
								}

							}
							if (util.notEmpty(currentBody.GeneralInformation?.OriginalLocation)) {
								def origin = currentBody.GeneralInformation.OriginalLocation
								'Group8_LOC' {
									'LOC' {
										'E3227_01' '106'
										'C517_02' {
											'E3225_01' origin?.LocationCode?.UNLocationCode
											'E1131_02' ''
											'E3055_03' '6'
											'E3224_04' origin?.LocationName
										}
										'C519_03' {
											'E3223_01' origin.City?.take(25)
											'E1131_02' ''
											'E3055_03' ''
											'E3222_04' ''
										}
										'C553_04' {
											'E3233_01' ''
											'E1131_02' ''
											'E3055_03' ''
											'E3232_04' ''
										}
										'E5479_05' ''
									}
									'DTM' {
										'C507_01' {
											'E2005_01' ''
											'E2380_02' ''
											'E2379_03' ''
										}
									}
								}
							}
							'Group9_RFF' {
								'RFF' {
									'C506_01' {
										'E1153_01' ''
										'E1154_02' ''
										'E1156_03' ''
										'E4000_04' ''
										'E1060_05' ''
									}
								}
								'DTM' {
									'C507_01' {
										'E2005_01' ''
										'E2380_02' ''
										'E2379_03' ''
									}
								}
							}
						}
					}
					currentBody.Party.each { currentParty ->
						'Group10_NAD' {
							// convert partyType
							String currentPartyType = currentParty.PartyType
							'NAD' {
								//todo convert partytype
								'E3035_01' getExternalPartyType('UASC', currentPartyType, conn)
								'C082_02' {
									if (util.notEmpty(currentParty?.CarrierCustomerCode)) {
										'E3039_01' currentParty.CarrierCustomerCode
									} else {
										//todo convert cscompayid
										'E3039_01' getCCC('UASC', currentParty?.CSCompanyID, conn)
									}
									'E1131_02' '160'
									'E3055_03' '80'
								}
								'C058_03' {
									'E3124_01' ''
									'E3124_02' ''
									'E3124_03' ''
									'E3124_04' ''
									'E3124_05' ''
								}
								List lines = splitString(currentParty?.PartyName, 35)
								Iterator lineIterator = lines.iterator()
								if (lineIterator.hasNext()) {
									'C080_04' {
										'E3036_01' lineIterator.next()
										if (lineIterator.hasNext()) {
											'E3036_02' lineIterator.next()
										}
										'E3036_03' ''
										'E3036_04' ''
										'E3036_05' ''
										'E3045_06' ''
									}
								}
								List addressLines = new LinkedList()
								currentParty?.Address?.AddressLines?.AddressLine.each {
									lines = splitString(it, 35)
									if (lines != null) {
										addressLines.addAll(lines)
									}
								}
								lineIterator = addressLines.iterator()
								if (lineIterator.hasNext()) {
									'C059_05' {
										'E3042_01' lineIterator.next()
										if (lineIterator.hasNext()) {
											'E3042_02' lineIterator.next()
										}
										if (lineIterator.hasNext()) {
											'E3042_03' lineIterator.next()
										}
										if (lineIterator.hasNext()) {
											'E3042_04' lineIterator.next()
										}
									}
								}
								'E3164_06' ''
								'C819_07' {
									'E3229_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3228_04' ''
								}
								'E3251_08' ''
								'E3207_09' ''
							}
							'LOC' {
								'E3227_01' ''
								'C517_02' {
									'E3225_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3224_04' ''
								}
								'C519_03' {
									'E3223_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3222_04' ''
								}
								'C553_04' {
									'E3233_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3232_04' ''
								}
								'E5479_05' ''
							}
							if (util.notEmpty(currentParty?.Contact?.FirstName) || util.notEmpty(currentParty?.Contact?.LastName)) {
								'Group11_CTA' {
									'CTA' {
										'E3139_01' 'IC'
										'C056_02' {
											String contactName = (currentParty.Contact?.FirstName + '' + currentParty.Contact?.LastName).trim()
											'E3413_01' contactName
											'E3412_02' ''
										}
									}
									if (util.notEmpty(currentParty.Contact?.ContactPhone)) {
										'COM' {
											'C076_01' {
												'E3148_01' constructContactNumber(currentParty.Contact.ContactPhone?.CountryCode, currentParty.Contact.ContactPhone?.AreaCode, currentParty.Contact.ContactPhone?.Number)
												'E3155_02' 'TE'
											}
										}

									}
									if (util.notEmpty(currentParty.Contact?.ContactFax)) {
										'COM' {
											'C076_01' {
												'E3148_01' constructContactNumber(currentParty.Contact.ContactFax?.CountryCode + ' ' + currentParty.Contact.ContactFax?.AreaCode + ' ' + currentParty.Contact.ContactFax?.Number)
												'E3155_02' 'FX'
											}
										}

									}
									if (util.notEmpty(currentParty.Contact?.ContactEmailAddress)) {
										'COM' {
											'C076_01' {
												'E3148_01' currentParty.Contact.ContactEmailAddress
												'E3155_02' 'EM'
											}
										}
									}
								}
							}
							if (currentPartyType == 'HI') {
								currentParty?.DocumentMessageDetails.each { currentDocumenMessageDetails ->
									'Group12_DOC' {
										'DOC' {
											'C002_01' {
												'E1001_01' currentDocumenMessageDetails?.DocumentName
												'E1131_02' ''
												'E3055_03' ''
												'E1000_04' currentDocumenMessageDetails?.DocumentName
											}
											'C503_02' {
												'E1004_01' ''
												'E1373_02' currentDocumenMessageDetails?.DocumentStatusCode
												'E1366_03' ''
												'E3453_04' ''
												'E1056_05' ''
												'E1060_06' ''
											}
											'E3153_03' ''
											if (currentDocumenMessageDetails.DocumentName != '714') {
												'E1220_04' currentDocumenMessageDetails?.NumberOfCopiesOfDocumentRequired
											}
											'E1218_05' ''
										}
										'DTM' {
											'C507_01' {
												'E2005_01' ''
												'E2380_02' ''
												'E2379_03' ''
											}
										}
									}
								}
							}
							'Group13_RFF' {
								'RFF' {
									'C506_01' {
										'E1153_01' ''
										'E1154_02' ''
										'E1156_03' ''
										'E4000_04' ''
										'E1060_05' ''
									}
								}
								'DTM' {
									'C507_01' {
										'E2005_01' ''
										'E2380_02' ''
										'E2379_03' ''
									}
								}
							}
							currentBody.GeneralInformation?.ChargePaymentInstruction.each { currentChargePaymentInstruction ->
								'Group14_CPI' {
									'CPI' {
										'C229_01' {
											'E5237_01' currentChargePaymentInstruction?.ChargeCategoryCode
											'E1131_02' ''
											'E3055_03' currentChargePaymentInstruction?.PrepaidCollectIndicator
										}
										'C231_02' {
											'E4215_01' ''
											'E1131_02' ''
											'E3055_03' ''
										}
										'E4237_03' ''
									}
									'RFF' {
										'C506_01' {
											'E1153_01' ''
											'E1154_02' ''
											'E1156_03' ''
											'E4000_04' ''
											'E1060_05' ''
										}
									}
									'CUX' {
										'C504_01' {
											'E6347_01' ''
											'E6345_02' ''
											'E6343_03' ''
											'E6348_04' ''
										}
										'C504_02' {
											'E6347_01' ''
											'E6345_02' ''
											'E6343_03' ''
											'E6348_04' ''
										}
										'E5402_03' ''
										'E6341_04' ''
									}
									'LOC' {
										'E3227_01' ''
										'C517_02' {
											'E3225_01' ''
											'E1131_02' ''
											'E3055_03' ''
											'E3224_04' ''
										}
										'C519_03' {
											'E3223_01' ''
											'E1131_02' ''
											'E3055_03' ''
											'E3222_04' ''
										}
										'C553_04' {
											'E3233_01' ''
											'E1131_02' ''
											'E3055_03' ''
											'E3232_04' ''
										}
										'E5479_05' ''
									}
									'MOA' {
										'C516_01' {
											'E5025_01' ''
											'E5004_02' ''
											'E6345_03' ''
											'E6343_04' ''
											'E4405_05' ''
										}
									}
								}
							}
							'Group15_TSR' {
								'TSR' {
									'C536_01' {
										'E4065_01' ''
										'E1131_02' ''
										'E3055_03' ''
									}
									'C233_02' {
										'E7273_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E7273_04' ''
										'E1131_05' ''
										'E3055_06' ''
									}
									'C537_03' {
										'E4219_01' ''
										'E1131_02' ''
										'E3055_03' ''
									}
									'C703_04' {
										'E7085_01' ''
										'E1131_02' ''
										'E3055_03' ''
									}
								}
								'RFF' {
									'C506_01' {
										'E1153_01' ''
										'E1154_02' ''
										'E1156_03' ''
										'E4000_04' ''
										'E1060_05' ''
									}
								}
								'LOC' {
									'E3227_01' ''
									'C517_02' {
										'E3225_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E3224_04' ''
									}
									'C519_03' {
										'E3223_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E3222_04' ''
									}
									'C553_04' {
										'E3233_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E3232_04' ''
									}
									'E5479_05' ''
								}
								'TPL' {
									'C222_01' {
										'E8213_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E8212_04' ''
										'E8453_05' ''
									}
								}
								'FTX' {
									'E4451_01' ''
									'E4453_02' ''
									'C107_03' {
										'E4441_01' ''
										'E1131_02' ''
										'E3055_03' ''
									}
									'C108_04' {
										'E4440_01' ''
										'E4440_02' ''
										'E4440_03' ''
										'E4440_04' ''
										'E4440_05' ''
									}
									'E3453_05' ''
									'E4447_06' ''
								}
							}
						}
					}
					currentBody.Cargo.each { currentCargo ->
						'Group16_GID' {
							'GID' {
								'E1496_01' currentCargo.CargoInfo.ItemNumber
								'C213_02' {
									'E7224_01' currentCargo.CargoInfo?.Packaging.PackageQty
									'E7065_02' ''
									'E1131_03' ''
									'E3055_04' ''
									'E7064_05' ''
									'E7233_06' ''
								}
								'C213_03' {
									'E7224_01' getExternalPackageCode(TP_ID, currentCargo.CargoInfo?.Packaging?.PackageType, conn)
									'E7065_02' ''
									'E1131_03' ''
									'E3055_04' '6'
									//todo
									'E7064_05' currentCargo.CargoInfo?.Packaging?.PackageDesc.take(35)
									'E7233_06' ''
								}
								'C213_04' {
									'E7224_01' ''
									'E7065_02' ''
									'E1131_03' ''
									'E3055_04' ''
									'E7064_05' ''
									'E7233_06' ''
								}
								'C213_05' {
									'E7224_01' ''
									'E7065_02' ''
									'E1131_03' ''
									'E3055_04' ''
									'E7064_05' ''
									'E7233_06' ''
								}
								'C213_06' {
									'E7224_01' ''
									'E7065_02' ''
									'E1131_03' ''
									'E3055_04' ''
									'E7064_05' ''
									'E7233_06' ''
								}
							}
							'HAN' {
								'C524_01' {
									'E4079_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E4078_04' ''
								}
								'C218_02' {
									'E7419_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E7418_04' ''
								}
							}
							'TMP' {
								'E6245_01' ''
								'C239_02' {
									'E6246_01' ''
									'E6411_02' ''
								}
							}
							'RNG' {
								'E6167_01' ''
								'C280_02' {
									'E6411_01' ''
									'E6162_02' ''
									'E6152_03' ''
								}
							}
							'TMD' {
								'C219_01' {
									'E8335_01' ''
									'E8334_02' ''
								}
								'E8332_02' ''
								'E8341_03' ''
							}
							'LOC' {
								'E3227_01' ''
								'C517_02' {
									'E3225_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3224_04' ''
								}
								'C519_03' {
									'E3223_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3222_04' ''
								}
								'C553_04' {
									'E3233_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3232_04' ''
								}
								'E5479_05' ''
							}
							'MOA' {
								'C516_01' {
									'E5025_01' ''
									'E5004_02' ''
									'E6345_03' ''
									'E6343_04' ''
									'E4405_05' ''
								}
							}
							currentCargo.CargoInfo?.HarmonizedTariffSchedule.each { currentSchedule ->
								'PIA' {
									'E4347_01' '5'
									'C212_02' {
										'E7140_01' currentSchedule
										'E7143_02' 'HS'
										'E1131_03' ''
										'E3055_04' ''
									}
									'C212_03' {
										'E7140_01' ''
										'E7143_02' ''
										'E1131_03' ''
										'E3055_04' ''
									}
									'C212_04' {
										'E7140_01' ''
										'E7143_02' ''
										'E1131_03' ''
										'E3055_04' ''
									}
									'C212_05' {
										'E7140_01' ''
										'E7143_02' ''
										'E1131_03' ''
										'E3055_04' ''
									}
									'C212_06' {
										'E7140_01' ''
										'E7143_02' ''
										'E1131_03' ''
										'E3055_04' ''
									}
								}
							}

							if (currentCargo.CargoInfo?.CargoDescription){
								List lines = splitString(currentCargo.CargoInfo?.CargoDescription, 512)
								lines.each { line ->
									'FTX' {
										'E4451_01' 'AAA'
										'E4453_02' ''
										'C107_03' {
											'E4441_01' ''
											'E1131_02' ''
											'E3055_03' ''
										}
										'C108_04' {
											'E4440_01' line
											'E4440_02' ''
											'E4440_03' ''
											'E4440_04' ''
											'E4440_05' ''
										}
										'E3453_05' ''
										'E4447_06' ''
									}
								}
							}
							'PCD' {
								'C501_01' {
									'E5245_01' ''
									'E5482_02' ''
									'E5249_03' ''
									'E1131_04' ''
									'E3055_05' ''
								}
							}
							'Group17_NAD' {
								'NAD' {
									'E3035_01' ''
									'C082_02' {
										'E3039_01' ''
										'E1131_02' ''
										'E3055_03' ''
									}
									'C058_03' {
										'E3124_01' ''
										'E3124_02' ''
										'E3124_03' ''
										'E3124_04' ''
										'E3124_05' ''
									}
									'C080_04' {
										'E3036_01' ''
										'E3036_02' ''
										'E3036_03' ''
										'E3036_04' ''
										'E3036_05' ''
										'E3045_06' ''
									}
									'C059_05' {
										'E3042_01' ''
										'E3042_02' ''
										'E3042_03' ''
										'E3042_04' ''
									}
									'E3164_06' ''
									'C819_07' {
										'E3229_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E3228_04' ''
									}
									'E3251_08' ''
									'E3207_09' ''
								}
								'DTM' {
									'C507_01' {
										'E2005_01' ''
										'E2380_02' ''
										'E2379_03' ''
									}
								}
							}
							'GDS' {
								'C703_01' {
									'E7085_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
							}
							if (util.notEmpty(currentCargo.CargoInfo?.Volume?.Volume)) {
								'Group18_MEA' {
									'MEA' {
										'E6311_01' 'VOL'
										'C502_02' {
											'E6313_01' 'AAW'
											'E6321_02' ''
											'E6155_03' ''
											'E6154_04' ''
										}
										'C174_03' {
											'E6411_01' currentCargo.CargoInfo.Volume?.VolumeUnit
											'E6314_02' currentCargo.CargoInfo.Volume.Volume
											'E6162_03' ''
											'E6152_04' ''
											'E6432_05' ''
										}
										'E7383_04' ''
									}
									'EQN' {
										'C523_01' {
											'E6350_01' ''
											'E6353_02' ''
										}
									}
								}
							}
							if (util.notEmpty(currentCargo.CargoInfo?.GrossWeight?.Weight)) {
								'Group18_MEA' {
									'MEA' {
										'E6311_01' 'WT'
										'C502_02' {
											'E6313_01' 'G'
											'E6321_02' ''
											'E6155_03' ''
											'E6154_04' ''
										}
										'C174_03' {
											switch (currentCargo.CargoInfo.GrossWeight?.WeightUnit) {
												case 'KGS':
													'E6411_01' 'KGM'
													break
												case 'LBS':
													'E6411_01' 'LBS'
													break
												case 'TON':
													'E6411_01' 'TON'
													break
											}
											'E6314_02' currentCargo.CargoInfo.GrossWeight.Weight
											'E6162_03' ''
											'E6152_04' ''
											'E6432_05' ''
										}
										'E7383_04' ''
									}
									'EQN' {
										'C523_01' {
											'E6350_01' ''
											'E6353_02' ''
										}
									}
								}
							}
							'Group19_DIM' {
								'DIM' {
									'E6145_01' ''
									'C211_02' {
										'E6411_01' ''
										'E6168_02' ''
										'E6140_03' ''
										'E6008_04' ''
									}
								}
								'EQN' {
									'C523_01' {
										'E6350_01' ''
										'E6353_02' ''
									}
								}
							}
							'Group20_RFF' {
								'RFF' {
									'C506_01' {
										'E1153_01' ''
										'E1154_02' ''
										'E1156_03' ''
										'E4000_04' ''
										'E1060_05' ''
									}
								}
								'DTM' {
									'C507_01' {
										'E2005_01' ''
										'E2380_02' ''
										'E2379_03' ''
									}
								}
							}
							// update md for pci
							currentCargo.CargoInfo.SOMarksAndNumbers.each { currentMarksAndNumbers ->
								'Group21_PCI' {
									List lines = new LinkedList()
									currentMarksAndNumbers.MarksAndNumbersLine.each { currentOriginalLine ->
										lines.addAll(splitString(currentOriginalLine, 35))
										Iterator linesIterator = lines.iterator()
									}
									Iterator linesIterator = lines.iterator()
									if (linesIterator.hasNext()) {
										'PCI' {
											'E4233_01' ''
											'C210_02' {
												'E7102_01' linesIterator.next()
												if (linesIterator.hasNext()) {
													'E7102_02' linesIterator.next()
												}
												if (linesIterator.hasNext()) {
													'E7102_03' linesIterator.next()
												}
												if (linesIterator.hasNext()) {
													'E7102_04' linesIterator.next()
												}
												if (linesIterator.hasNext()) {
													'E7102_05' linesIterator.next()
												}
												if (linesIterator.hasNext()) {
													'E7102_06' linesIterator.next()
												}
												if (linesIterator.hasNext()) {
													'E7102_07' linesIterator.next()
												}
												if (linesIterator.hasNext()) {
													'E7102_08' linesIterator.next()
												}
												if (linesIterator.hasNext()) {
													'E7102_09' linesIterator.next()
												}
												if (linesIterator.hasNext()) {
													'E7102_10' linesIterator.next()
												}
											}
											'E8275_03' ''
											'C827_04' {
												'E7511_01' ''
												'E1131_02' ''
												'E3055_03' ''
											}
										}
									}
									'RFF' {
										'C506_01' {
											'E1153_01' ''
											'E1154_02' ''
											'E1156_03' ''
											'E4000_04' ''
											'E1060_05' ''
										}
									}
									'DTM' {
										'C507_01' {
											'E2005_01' ''
											'E2380_02' ''
											'E2379_03' ''
										}
									}
									'GIN' {
										'E7405_01' ''
										'C208_02' {
											'E7402_01' ''
											'E7402_02' ''
										}
										'C208_03' {
											'E7402_01' ''
											'E7402_02' ''
										}
										'C208_04' {
											'E7402_01' ''
											'E7402_02' ''
										}
										'C208_05' {
											'E7402_01' ''
											'E7402_02' ''
										}
										'C208_06' {
											'E7402_01' ''
											'E7402_02' ''
										}
									}


								}
							}
							'Group22_DOC' {
								'DOC' {
									'C002_01' {
										'E1001_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E1000_04' ''
									}
									'C503_02' {
										'E1004_01' ''
										'E1373_02' ''
										'E1366_03' ''
										'E3453_04' ''
										'E1056_05' ''
										'E1060_06' ''
									}
									'E3153_03' ''
									'E1220_04' ''
									'E1218_05' ''
								}
								'DTM' {
									'C507_01' {
										'E2005_01' ''
										'E2380_02' ''
										'E2379_03' ''
									}
								}
							}
							'Group23_TPL' {
								'TPL' {
									'C222_01' {
										'E8213_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E8212_04' ''
										'E8453_05' ''
									}
								}
								'Group24_MEA' {
									'MEA' {
										'E6311_01' ''
										'C502_02' {
											'E6313_01' ''
											'E6321_02' ''
											'E6155_03' ''
											'E6154_04' ''
										}
										'C174_03' {
											'E6411_01' ''
											'E6314_02' ''
											'E6162_03' ''
											'E6152_04' ''
											'E6432_05' ''
										}
										'E7383_04' ''
									}
									'EQN' {
										'C523_01' {
											'E6350_01' ''
											'E6353_02' ''
										}
									}
								}
							}
							'Group25_SGP' {
								'SGP' {
									'C237_01' {
										'E8260_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E3207_04' ''
									}
									'E7224_02' ''
								}
								'Group26_MEA' {
									'MEA' {
										'E6311_01' ''
										'C502_02' {
											'E6313_01' ''
											'E6321_02' ''
											'E6155_03' ''
											'E6154_04' ''
										}
										'C174_03' {
											'E6411_01' ''
											'E6314_02' ''
											'E6162_03' ''
											'E6152_04' ''
											'E6432_05' ''
										}
										'E7383_04' ''
									}
									'EQN' {
										'C523_01' {
											'E6350_01' ''
											'E6353_02' ''
										}
									}
								}
							}
							currentCargo?.DGCargoSpec.each { currentDGCargoSpec ->
								'Group27_DGS' {
									'DGS' {
										'E8273_01' 'IMD'
										'C205_02' {
											'E8351_01' currentDGCargoSpec?.IMOClass
											'E8078_02' currentDGCargoSpec?.IMDGPage
											'E8092_03' ''
										}
										'C234_03' {
											'E7124_01' currentDGCargoSpec?.UNNumber
											'E7088_02' ''
										}
										'C223_04' {
											'E7106_01' currentDGCargoSpec?.FlashPoint?.Temperature
											switch (currentDGCargoSpec?.FlashPoint?.TemperatureUnit) {
												case 'C':
													'E6411_02' 'CEH'
													break
												case 'F':
													'E6411_02' 'FAH'
													break
											}
										}
										'E8339_05' getExternalPackageCode(TP_ID, currentDGCargoSpec?.PackageGroup?.Code, conn)
										'E8364_06' ''
										'E8410_07' ''
										'E8126_08' ''
										'C235_09' {
											'E8158_01' ''
											'E8186_02' ''
										}
										'C236_10' {
											'E8246_01' ''
											'E8246_02' ''
											'E8246_03' ''
										}
										'E8255_11' ''
										'E8325_12' ''
										'E8211_13' ''
									}
									List lines = new LinkedList()
									currentDGCargoSpec?.FreeText.each { currentFreeText ->
										currentFreeText?.TextLiteral.each { currentTextLiteral ->
											lines.addAll(splitString(currentTextLiteral, 512))
										}
									}
									lines.each { line ->
										'FTX' {
											'E4451_01' 'AAC'
											'E4453_02' ''
											'C107_03' {
												'E4441_01' ''
												'E1131_02' ''
												'E3055_03' ''
											}
											'C108_04' {
												'E4440_01' line
												'E4440_02' ''
												'E4440_03' ''
												'E4440_04' ''
												'E4440_05' ''
											}
											'E3453_05' ''
											'E4447_06' ''
										}
									}
									currentDGCargoSpec?.EmergencyContact.each { currentContact ->
										'Group28_CTA' {
											'CTA' {
												'E3139_01' 'HG'
												'C056_02' {
													'E3413_01' ''
													String contact_name = (currentContact?.FirstName + ' ' + currentContact?.LastName).trim().take(35)
													'E3412_02' contact_name
												}
											}

											'COM' {
												'C076_01' {
													'E3148_01' constructContactNumber(currentContact?.ContactPhone?.CountryCode, currentContact?.ContactPhone?.AreaCode, currentContact?.ContactPhone?.Number)
													'E3155_02' 'TE'
												}
											}
										}
									}
									'Group29_MEA' {
										'MEA' {
											'E6311_01' ''
											'C502_02' {
												'E6313_01' ''
												'E6321_02' ''
												'E6155_03' ''
												'E6154_04' ''
											}
											'C174_03' {
												'E6411_01' ''
												'E6314_02' ''
												'E6162_03' ''
												'E6152_04' ''
												'E6432_05' ''
											}
											'E7383_04' ''
										}
										'EQN' {
											'C523_01' {
												'E6350_01' ''
												'E6353_02' ''
											}
										}
									}
									'Group30_SGP' {
										'SGP' {
											'C237_01' {
												'E8260_01' ''
												'E1131_02' ''
												'E3055_03' ''
												'E3207_04' ''
											}
											'E7224_02' ''
										}
										'Group31_MEA' {
											'MEA' {
												'E6311_01' ''
												'C502_02' {
													'E6313_01' ''
													'E6321_02' ''
													'E6155_03' ''
													'E6154_04' ''
												}
												'C174_03' {
													'E6411_01' ''
													'E6314_02' ''
													'E6162_03' ''
													'E6152_04' ''
													'E6432_05' ''
												}
												'E7383_04' ''
											}
											'EQN' {
												'C523_01' {
													'E6350_01' ''
													'E6353_02' ''
												}
											}
										}
									}
								}
							}
						}
					}
					currentBody?.Container.each { currentContainer ->
						'Group32_EQD' {
							'EQD' {
								'E8053_01' 'CN'
								'C237_02' {
									'E8260_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E3207_04' ''
								}
								'C224_03' {
									'E8155_01' getExternalConatinerSize(TP_ID, currentContainer?.CSContainerSizeType, conn)
									'E1131_02' ''
									'E3055_03' ''
									String freeText = ''
									currentContainer?.FreeText.each {currentFreeText->
										currentFreeText?.TextLiteral.each { currentTextLiteral ->
											freeText = freeText + ' ' + currentTextLiteral
										}
									}
									'E8154_04' freeText.trim().take(35)
								}
								switch (currentContainer?.IsSOC) {
									case 'true':
										'E8077_04' '1'
										break
									case 'false':
										'E8077_04' '2'
										break
								}
								'E8249_05' ''
								'E8169_06' ''
							}
							'EQN' {
								'C523_01' {
									'E6350_01' currentContainer?.Quantity
									'E6353_02' ''
								}
							}
							'TMD' {
								'C219_01' {
									'E8335_01' ''
									'E8334_02' ''
								}
								'E8332_02' ''
								'E8341_03' ''
							}
							if (util.notEmpty(currentContainer?.ReeferCargoSpec?.Ventilation?.Ventilation)) {
								'MEA' {
									'E6311_01' 'AAE'
									'C502_02' {
										'E6313_01' 'AAS'
										'E6321_02' ''
										'E6155_03' ''
										'E6154_04' ''
									}
									'C174_03' {
										'E6411_01' currentContainer.ReeferCargoSpec.Ventilation?.VentilationUnit
										'E6314_02' currentContainer.ReeferCargoSpec.Ventilation.Ventilation
										'E6162_03' ''
										'E6152_04' ''
										'E6432_05' ''
									}
									'E7383_04' ''
								}
							}
							if (util.notEmpty(currentContainer?.GrossVolume?.Volume)) {
								'MEA' {
									'E6311_01' 'AAE'
									'C502_02' {
										'E6313_01' 'AAW'
										'E6321_02' ''
										'E6155_03' ''
										'E6154_04' ''
									}
									'C174_03' {
										'E6411_01' currentContainer.GrossVolume?.VolumeUnit
										'E6314_02' currentContainer.GrossVolume.VolumeUnit
										'E6162_03' ''
										'E6152_04' ''
										'E6432_05' ''
									}
									'E7383_04' ''
								}
							}
							if (util.notEmpty(currentContainer?.GrossWeight?.Weight)) {
								'MEA' {
									'E6311_01' 'WT'
									'C502_02' {
										'E6313_01' 'WT'
										'E6321_02' ''
										'E6155_03' ''
										'E6154_04' ''
									}
									'C174_03' {
										'E6411_01' currentContainer.GrossWeight?.WeightUnit
										'E6314_02' currentContainer.GrossWeight.Weight
										'E6162_03' ''
										'E6152_04' ''
										'E6432_05' ''
									}
									'E7383_04' ''
								}
							}
							'DIM' {
								'E6145_01' ''
								'C211_02' {
									'E6411_01' ''
									'E6168_02' ''
									'E6140_03' ''
									'E6008_04' ''
								}
							}
							'TPL' {
								'C222_01' {
									'E8213_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E8212_04' ''
									'E8453_05' ''
								}
							}
							'HAN' {
								'C524_01' {
									'E4079_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E4078_04' ''
								}
								'C218_02' {
									'E7419_01' ''
									'E1131_02' ''
									'E3055_03' ''
									'E7418_04' ''
								}
							}
							if (util.notEmpty(currentContainer?.ReeferCargoSpec?.Temperature?.Temperature)) {
								'TMP' {
									'E6245_01' '2'
									'C239_02' {
										'E6246_01' currentContainer.ReeferCargoSpec.Temperature.Temperature
										switch (currentContainer.ReeferCargoSpec.Temperature?.TemperatureUnit) {
											case 'C':
												'E6411_02' 'CEL'
												break
											case 'F':
												'E6411_02' 'FAH'
												break
										}
									}
								}
							}
							'FTX' {
								'E4451_01' ''
								'E4453_02' ''
								'C107_03' {
									'E4441_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
								'C108_04' {
									'E4440_01' ''
									'E4440_02' ''
									'E4440_03' ''
									'E4440_04' ''
									'E4440_05' ''
								}
								'E3453_05' ''
								'E4447_06' ''
							}
							'RFF' {
								'C506_01' {
									'E1153_01' ''
									'E1154_02' ''
									'E1156_03' ''
									'E4000_04' ''
									'E1060_05' ''
								}
							}
							'Group33_NAD' {
								'NAD' {
									'E3035_01' ''
									'C082_02' {
										'E3039_01' ''
										'E1131_02' ''
										'E3055_03' ''
									}
									'C058_03' {
										'E3124_01' ''
										'E3124_02' ''
										'E3124_03' ''
										'E3124_04' ''
										'E3124_05' ''
									}
									'C080_04' {
										'E3036_01' ''
										'E3036_02' ''
										'E3036_03' ''
										'E3036_04' ''
										'E3036_05' ''
										'E3045_06' ''
									}
									'C059_05' {
										'E3042_01' ''
										'E3042_02' ''
										'E3042_03' ''
										'E3042_04' ''
									}
									'E3164_06' ''
									'C819_07' {
										'E3229_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E3228_04' ''
									}
									'E3251_08' ''
									'E3207_09' ''
								}
								'DTM' {
									'C507_01' {
										'E2005_01' ''
										'E2380_02' ''
										'E2379_03' ''
									}
								}
								'Group34_CTA' {
									'CTA' {
										'E3139_01' ''
										'C056_02' {
											'E3413_01' ''
											'E3412_02' ''
										}
									}
									'COM' {
										'C076_01' {
											'E3148_01' ''
											'E3155_02' ''
										}
									}
								}
							}
							'Group35_DGS' {
								'DGS' {
									'E8273_01' ''
									'C205_02' {
										'E8351_01' ''
										'E8078_02' ''
										'E8092_03' ''
									}
									'C234_03' {
										'E7124_01' ''
										'E7088_02' ''
									}
									'C223_04' {
										'E7106_01' ''
										'E6411_02' ''
									}
									'E8339_05' ''
									'E8364_06' ''
									'E8410_07' ''
									'E8126_08' ''
									'C235_09' {
										'E8158_01' ''
										'E8186_02' ''
									}
									'C236_10' {
										'E8246_01' ''
										'E8246_02' ''
										'E8246_03' ''
									}
									'E8255_11' ''
									'E8325_12' ''
									'E8211_13' ''
								}
								'FTX' {
									'E4451_01' ''
									'E4453_02' ''
									'C107_03' {
										'E4441_01' ''
										'E1131_02' ''
										'E3055_03' ''
									}
									'C108_04' {
										'E4440_01' ''
										'E4440_02' ''
										'E4440_03' ''
										'E4440_04' ''
										'E4440_05' ''
									}
									'E3453_05' ''
									'E4447_06' ''
								}
								'Group36_CTA' {
									'CTA' {
										'E3139_01' ''
										'C056_02' {
											'E3413_01' ''
											'E3412_02' ''
										}
									}
									'COM' {
										'C076_01' {
											'E3148_01' ''
											'E3155_02' ''
										}
									}
								}
							}
						}
					}
					'UNT' {
						'E0074_01' '<replacement_need>'
						'E0062_02' '<replacement_need>'
					}
				}
			}
		}
		return writer.toString()
	}


	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

		cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

		/**
		 * Part I: prep handling here,
		 */
		//inputXmlBody = util.removeBOM(inputXmlBody)

		/**
		 * Part II: get OLL mapping runtime parameters
		 */
		def appSessionId = util.getRuntimeParameter("AppSessionID", runtimeParams);
		def sourceFileName = util.getRuntimeParameter("OriginalSourceFileName", runtimeParams);
		def TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
		def MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
		def DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
		def MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

		/**
		 * Part III: read xml and prepare output xml
		 */
		XmlBeanParser parser = new XmlBeanParser()
		ShippingOrder shippingOrder = parser.xmlParser(cleanXml(inputXmlBody), ShippingOrder.class)

		////below remarks lines is a jaxb demo, not use now
		//ContainerMovement containerMovement = JAXB.unmarshal(new StringReader(inputXmlBody), ContainerMovement.class)

		/**
		 * Part IV: mapping script start from here
		 */
//		List pre_result = pre_validation(shippingOrder)
//		Node bizKey = mapBizKey(shippingOrder)
		def iftmbf = cleanXml(mapIFTMBF(shippingOrder, TP_ID, conn))
//		def iftmbf = mapIFTMBF(shippingOrder, TP_ID, conn)
//		List pos_result = post_validation(iftmbf)

	}

	String cleanXml(String xml) {
		Node root = new XmlParser().parseText(xml)
		cleanNode(root)
		XmlUtil.serialize(root)
	}

	boolean cleanNode(Node node) {
		node.attributes().with { a ->
			a.findAll { !it.value }.each { a.remove(it.key) }
		}
		node.children().with { kids ->
			kids.findAll { it instanceof Node ? !cleanNode(it) : false }
					.each { kids.remove(it) }
		}
		node.attributes() || node.children() || node.text()
	}

	/**
	 * split the inputstring into lines with the given maxCharPerLine
	 * @param inputString
	 * @param maxCharPerLine
	 * @return
	 */

	List splitString(def inputObject, int maxCharPerLine) {
		String inputString = inputObject.toString()
		if (inputString == '') {
			return null
		}

		if (maxCharPerLine < 1) {
			return null
		}
		List lines = new LinkedList()
		while (util.notEmpty(inputString)) {
			if (inputString.length() > maxCharPerLine) {
				int splitPosistion = inputString.take(maxCharPerLine).toString().lastIndexOf(" ")
				if (splitPosistion != -1) {
					lines.add(inputString.take(splitPosistion))
					inputString = inputString.substring(splitPosistion + 1)
				} else {
					lines.add(inputString.take(maxCharPerLine))
				}
			} else {
				lines.add(inputString)
				inputString = ''
			}
		}
		lines
	}

	/**
	 * return the external reference type  for a given TP_ID , message type and interal reference type or the interal reference type if no result found
	 * @param TP_ID
	 * @param msgType
	 * @param internalCode
	 * @param conn
	 * @return external code or the inputted internal Code if externalCode not found
	 */
	//TP Specific
	String getExternalReferenceType(String TP_ID, String internalCode, Connection conn) {
		String ret = util.getEDICdeRef(TP_ID, 'Group3_ReferenceType', 'O', 'CS2XML', 'RFF', '01_1154', internalCode, conn)
		if (ret == '') {
			return internalCode
		} else {
			return ret
		}
	}

	/**
	 * return the external package code for a given TP_ID and internal package code or the internal package code if no result found
	 * @param TP_ID
	 * @param internalCode
	 * @param conn
	 * @return the externalPackagCode or if not found the internalCode inputed
	 */
	String getExternalPackageCode(String TP_ID, String internalCode, Connection conn) {
		getConversionByExtCdeWithDefault(TP_ID, 'PackageUnit', internalCode, internalCode, conn)
	}

	/**
	 * return the external container size type  for a given TP_ID and internal container size type or the internal container size type if no result found
	 * @param TP_ID
	 * @param internalCode
	 * @param conn
	 * @return external container size code or if not found the internalCode provided
	 */
	String getExternalConatinerSize(String TP_ID, String internalCode, Connection conn) {
		getConversionByExtCdeWithDefault(TP_ID, 'ContainerType', internalCode, internalCode, conn)
	}

	/**
	 *  return the CustomerCarrierCode for a given TP_ID and CSCompanyID
	 * @param TP_ID
	 * @param CSCompanyID
	 * @param conn
	 * @return the CustomerCarrierCode if found
	 */
	String getCCC(String TP_ID, String CSCompanyID, Connection conn) {
		getConversionByExtCde(TP_ID, 'CarrierCustomerCode', CSCompanyID, conn)
	}

	/**
	 * return the external party type  for a given TP_ID , message type and interal party type or the interal party type if no result found
	 * @param TP_ID
	 * @param internalCode
	 * @param conn
	 * @return the external Party Type or if not found the internalCode provided
	 */
	//TP Specific
	String getExternalPartyType(String TP_ID, String internalCode, Connection conn) {
		String ret = util.getEDICdeRef(TP_ID, 'Group10_NameAndAddress', 'O', 'CS2XML', 'NAD', '01_3035', internalCode, conn)
		if (ret == '') {
			return internalCode
		} else {
			return ret
		}
	}

	/**
	 * return the internal reference type for a given TP_ID , message type and external reference type or the external reference type if no result found
	 * @param TP_ID
	 * @param externalCode
	 * @param conn
	 * @return the internal reference type or if not found the externalcode provided
	 */
	String getInternalReferenceType(String TP_ID, String externalCode, Connection conn) {
		String ret = util.getEDICdeReffromIntCde(TP_ID, 'Group3_ReferenceType', 'O', 'CS2XML', 'RFF', '01_1154', externalCode, conn)
		if (ret == '') {
			return internalCode
		} else {
			return ret
		}
	}

	/**
	 *
	 * @param TP_ID
	 * @param externalcode
	 * @param conn
	 * @return
	 */
	String getInternalPackageType(String TP_ID, String externalcode, Connection conn) {
		getConversionByIntCdeWithDefault(TP_ID, 'PackageUnit', externalcode, externalcode, conn)
	}

	//fork from mappingUtil with modification
	String getConversionByExtCdeWithDefault(String TP_ID, String convertTypeId, String fromValue, String defaultValue, Connection conn) throws Exception {
		String ret = getConversionByExtCde(TP_ID, convertTypeId, fromValue, conn);
		if (ret == null || ret.length() == 0) {
			ret = defaultValue;
		}
		return ret;
	}

	//fork from mappingUtil with modification
	String getConversionByExtCde(String TP_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select int_cde from b2b_cde_conversion where tp_id=? and convert_type_id=? and ext_cde=?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, convertTypeId);
			pre.setString(3, fromValue);
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

	//fork from mappingUtil.getConversionWithDefault with modification
	String getConversionByIntCdeWithDefault(String TP_ID, String convertTypeId, String fromValue, String defaultValue, Connection conn) throws Exception {
		String ret = getConversionByExtCde(TP_ID, convertTypeId, fromValue, conn);
		if (ret == null || ret.length() == 0) {
			ret = defaultValue;
		}
		return ret;
	}
	//fork from mappingUtil.getConversion with modification
	String getConversionByIntCde(String TP_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where tp_id=? and convert_type_id=? and int_cde=?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, convertTypeId);
			pre.setString(3, fromValue);
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

	/**
	 * convert the date time in the xml with the outFormat
	 * for edifact
	 * 103 = 'yyyyMMdd'
	 * 203 = 'yyyyMMddHHss'
	 * @param xmlDateTime
	 * @param outFormat the output format, please refer to https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
	 * @return date time with the outFormat
	 */
	String convertXMLDateTime(def xmlDateTime, String outFormat) {
		Calendar dateTime = DatatypeConverter.parseDateTime(xmlDateTime.toString())
		SimpleDateFormat sfmt = new SimpleDateFormat(outFormat);
		sfmt.format(dateTime.getTime())
	}

	String constructContactNumber(String countryCode, String areaCode, String number) {
		String ret
		ret = ret + countryCode
		if (areaCode != null && areaCode != '') {
			ret = ret + '-' + areaCode
		}
		if (number != null && number != '') {
			ret = ret + '-' + number
		}
		ret
	}
}
