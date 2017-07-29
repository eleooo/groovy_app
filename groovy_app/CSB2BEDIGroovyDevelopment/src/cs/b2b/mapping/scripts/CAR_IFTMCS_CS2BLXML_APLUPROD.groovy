package cs.b2b.mapping.scripts

import cs.b2b.core.common.xmlvalidation.ValidateXML
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

//import cs.b2b.mapping.e2e.util.DemoBase
class CAR_IFTMCS_CS2BLXML_APLUPROD {

	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

		cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		inputXmlBody = util.removeBOM(inputXmlBody)

		/**
		 * Part II: get app mapping runtime parameters
		 */
		def appSessionId = util.getRuntimeParameter("B2BSessionID", runtimeParams);
		def sourceFileName = util.getRuntimeParameter("OriginalSourceFileName", runtimeParams);
		//pmt info
		def TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
		def MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
		def DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
		def MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

		/**
		 * Part III: read xml and prepare output xml
		 */
		//prepare inputEdi

//      Production
		def iftmcs = new XmlSlurper().parseText(inputXmlBody);

		//Coding
//		IFTMCS iftmcs = new IFTMCS();

		//prepare outXml
		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
		//new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		//prepare bizKeyXml
		def bizKeyWriter = new StringWriter();
		def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeyWriter), "", false));
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		/**
		 * Part IV: mapping script start from here
		 */

		outXml.'BillOfLading'('xmlns': 'http://www.cargosmart.com/billoflading', 'xmlns:cs': 'http://www.cargosmart.com/common', 'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance') {
			'Header' {
				'cs:ControlNumber' iftmcs.UNB.E0020_05
				'cs:MsgDT' {
					'cs:GMT' ''
					'cs:LocDT' ''
				}
				'cs:MsgDirection' 'I'
				'cs:MsgType' 'BL'
				'cs:SenderID' TP_ID
				'cs:ReceiverID' 'CARGOSMART'
				switch (iftmcs.Group_UNH?.BGM?.E1225_03) {
//                todo 'del'
					case '1':
						'cs:Action' 'DEL'
						break;
					case '5':
						'cs:Action' 'UPD'
						break;
					default:
						'cs:Action' 'NEW'
				}
//            todo version value
				'cs:Version' '10.1'
				'cs:InterchangeMessageID' MSG_REQ_ID
				'cs:FileName' sourceFileName
				'cs:DataSource' 'B2B'
				'cs:TargetCustomCountryCode' ''
				'cs:NVOProperty' {
					'cs:Name' ''
					'cs:Value' ''
				}
			}
			iftmcs.Group_UNH.each { currentTxn ->
				'Body' {
					'TransactionInformation' {
						'cs:MessageID' MSG_REQ_ID + ',' + UUID.randomUUID()
						'cs:GroupControlNumber' currentTxn.UNH.E0062_01
						'cs:InterchangeTransactionID' currentTxn.UNH.E0062_01
						switch (currentTxn.BGM?.E1225_03) {
//                 todo 'del'
							case '1':
								'Action' 'DEL'
								break
							case '5':
								'Action' 'UPD'
								break
							default:
								'Action' 'NEW'
						}
					}
					'EventInformation' {
						'cs:EventCode' 'bl_upload'
						'cs:EventDescription' ''

						'cs:EventDT' {
							String stringCurrentDateTime = Calendar.getInstance().format('yyyy-MM-dd HH:mm:ss').replace(' ', 'T')
							'cs:GMT' stringCurrentDateTime
							'cs:LocDT' stringCurrentDateTime
						}
					}
					'GeneralInformation' {
						'SPCompanyID' ''
						if (util.notEmpty(currentTxn?.Group11_NAD.NAD.find {
							it.E3035_01 == "CA"
						}?.C082_02?.E3039_01)) {
							'SCACCode' currentTxn?.Group11_NAD.NAD.find { it.E3035_01 == "CA" }?.C082_02?.E3039_01
						} else {
							'SCACCode' currentTxn?.Group8_TDT?.TDT?.C040_05?.E3127_01
						}
						'BLNumber' currentTxn.BGM?.C106_02?.E1004_01
						switch (currentTxn.BGM?.C002_01?.E1001_01) {
							case '705':
								'BLType' 'Original'
								break
							case '710':
								'BLType' 'Sea WayBill'
								break
						}
						//TODO CHECK mapping util version, CHECKED, version outdated, request update for mappingutil.groovy
						if (util.notEmpty(currentTxn?.Group18_GID?.Group30_DGS.DGS) && util.notEmpty(currentTxn?.Group35_EQD?.TMP)) {
							'ShipmentCargoType' 'RD'
						} else if (util.notEmpty(currentTxn?.Group18_GID?.Group30_DGS.DGS)) {
							'ShipmentCargoType' 'DG'
						} else if (util.notEmpty(currentTxn?.Group35_EQD?.TMP)) {
							'ShipmentCargoType' 'RF'
						} else {
							'ShipmentCargoType' 'GC'
						}
						'TrafficMode' {
							switch (currentTxn?.TSR?.C233_02.E7273_01) {
								case '2':
									'cs:OutBound' 'FCL'
									break
								case '3':
									'cs:OutBound' 'LCL'
									break
							}
							switch (currentTxn?.TSR?.C233_02?.E7273_04) {
								case '2':
									'cs:InBound' 'FCL'
									break
								case '3':
									'cs:InBound' 'LCL'
									break
							}
						}
						'CargoControlOffice' ''
						'ContactOfficeCode' ''
						'BLReceiptDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'BLIssueDT' {
							'cs:GMT' ''
							String stringDateTimeTobeConverted = currentTxn?.DTM.C507_01.find {
								it.E2005_01 == '182'
							}?.E2380_02
							String stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMdd", "yyyy-MM-dd HH:mm:ss")
							'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
						}
						'BLCreationDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'BLChangeDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'BLOnboardDT' {
							'cs:GMT' ''
							String stringDateTimeTobeConverted = currentTxn?.DTM.C507_01.find {
								it.E2005_01 == '342'
							}?.E2380_02
							String stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMdd", "yyyy-MM-dd HH:mm:ss")
							'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
						}
						'OriginalBLReleaseOffice' ''
						'BLPaymentOffice' ''
						'BLPaymentOfficeCode' ''
						'BLPaymentOfficeUNLoc' ''
						'CaptureOffice' ''
						'CaptureOfficeName' ''
						'CaptureOfficeUNLoc' ''
						'CaptureOfficePhoneNumber' ''
						'PaymentReceiptDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'OriginOfGoods' ''
						'CarrierBookingNumber' currentTxn?.Group3_RFF.RFF.C506_01.find { it.E1153_01 == 'BN' }?.E1154_02
						'Role' ''
						'SharedBy' ''
						'BLGrossWeight' {
							'cs:Weight' currentTxn?.CNT.C270_01.find { it.E6069_01 == '7' }.E6066_02
							switch (currentTxn?.CNT.C270_01.find { it.E6069_01 == '7' }?.E6411_03) {
								case 'KGM':
									'cs:WeightUnit' 'KGS'
									break
								case 'LBS':
									'cs:WeightUnit' 'LBS'
									break
								case 'TON':
									'cs:WeightUnit' 'TON'
									break
							}
						}
						'BLNetWeight' {
							'cs:Weight' ''
							'cs:WeightUnit' ''
						}
						'BLVolume' {
							'cs:Volume' currentTxn?.CNT.C270_01.find { it.E6069_01 == '15' }.E6066_02
							switch (currentTxn?.CNT.C270_01.find { it.E6069_01 == '15' }?.E6411_03) {
								case 'CBF':
									'cs:VolumeUnit' 'CBF'
									break
								case 'CBM':
									'cs:VolumeUnit' 'CBM'
									break
							}
						}
						if (currentTxn?.Group8_TDT.TDT.findAll() { it.E8051_01 == '20' }.size() != 0) {
							'BLSVVD' {
								currentTxn?.Group8_TDT.TDT.findAll() { it.E8051_01 == '20' }.first().with { firstTDT ->
									'cs:Service' ''
									'cs:Vessel' ''
									'cs:VesselName' firstTDT.C222_08?.E8212_04
									'cs:Voyage' firstTDT?.E8028_02
									'cs:Direction' ''
									'cs:LloydsNumber' firstTDT?.C222_08?.find { it?.E1131_02 == 'L' }?.E8213_01
									'cs:CallSign' firstTDT?.C222_08?.find { it?.E1131_02 == '103' }?.E8213_01
									'ExtVoyage' ''
								}
							}
						}

						'OutBoundROESVVD' {
							'cs:Service' ''
							'cs:Vessel' ''
							'cs:VesselName' ''
							'cs:Voyage' ''
							'cs:Direction' ''
							'cs:LloydsNumber' ''
							'cs:CallSign' ''
							'ROEPortOfLoad' ''
						}
						'InBoundROESVVD' {
							'cs:Service' ''
							'cs:Vessel' ''
							'cs:VesselName' ''
							'cs:Voyage' ''
							'cs:Direction' ''
							'cs:LloydsNumber' ''
							'cs:CallSign' ''
							'ROEPortOfDischarge' ''
						}
						'SIContactPerson' {
							'cs:FirstName' ''
							'cs:LastName' ''
							'cs:ContactPhone' {
								'cs:CountryCode' ''
								'cs:AreaCode' ''
								'cs:Number' ''
							}
							'cs:ContactFax' {
								'cs:CountryCode' ''
								'cs:AreaCode' ''
								'cs:Number' ''
							}
							'cs:ContactEmailAddress' ''
						}
						'TradeCode' ''
						'BookingOffice' ''
						'BookingOfficeName' ''
						'BookingOfficeUNLoc' ''
						'MovementRefNumber' ''
						'IsCSCustomer' ''
						'LabelActions' {
							'Seq' ''
							'Label' ''
							'ProposedAction' ''
							'ActionType' ''
							'TimeOfIssue' {
								'cs:GMT' ''
								'cs:LocDT' ''
							}
						}
						'PartnerID' ''
					}
					'BLStatus' "Type":"BL_STATUS", {
						'Status' "BL Ready"
						'StatusDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
					}
					currentTxn.Group11_NAD.each { currentParty ->
						'Party' {
							switch (currentParty.NAD.E3035_01) {
								case 'CA':
									'cs:PartyType' 'CAR'
									break
								case 'CN':
									'cs:PartyType' 'CGN'
									break
								case 'CZ':
									'cs:PartyType' 'SHP'
									break
								case 'FW':
									'cs:PartyType' 'FWD'
									break
								case 'N1':
									'cs:PartyType' 'NPT'
									break
								case 'N2':
									'cs:PartyType' 'ANP'
									break
								default:
									'cs:PartyType' 'OTH'
							}
							//todo space trim
							String partyName = currentParty.NAD?.C080_04.E3036_01.text() + ' ' + currentParty.NAD?.C080_04?.E3036_02.text()
							'cs:PartyName' partyName.trim()
							'cs:CSCompanyID' ''
							'cs:CarrierCustomerCode' currentParty.NAD?.C082_02.E3039_01
							'cs:isNeedReplyPartyEmail' ''
							if (util.notEmpty(currentParty?.Group12_CTA)) {
								currentParty.Group12_CTA.first().with { firstCTA ->
									'cs:Contact' {
										'cs:FirstName' firstCTA.CTA?.C056_02?.E3412_02
										'cs:LastName' ''
										'cs:ContactPhone' {
											'cs:CountryCode'
											'cs:AreaCode'
											'cs:Number' firstCTA?.COM?.C076_01?.find {
												it?.E3155_02 == 'TE'
											}?.E3148_01.text().take(30)
										}
										'cs:ContactFax' {
											'cs:CountryCode' ''
											'cs:AreaCode' ''
											'cs:Number' ''
										}
										'cs:ContactEmailAddress' firstCTA?.COM?.C076_01?.find {
											it?.E3155_02 == 'EM'
										}?.E3148_01
									}
								}
							}
							'cs:Address' {
								'cs:City' currentParty.NAD?.E3164_06
								'cs:County' ''
								'cs:State' currentParty.NAD?.C819_07?.E3229_01
								'cs:Country' currentParty.NAD?.E3207_09
								'cs:LocationCode' {
									'cs:MutuallyDefinedCode' ''
									'cs:UNLocationCode'
									'cs:SchedKDType' ''
									'cs:SchedKDCode' ''
								}
								'cs:PostalCode' currentParty.NAD?.E3251_08.text().take(12)
								'cs:AddressLines' {
									'cs:AddressLine' currentParty?.NAD?.C059_05?.E3042_01
									'cs:AddressLine' currentParty?.NAD?.C059_05?.E3042_02
									'cs:AddressLine' currentParty?.NAD?.C059_05?.E3042_03
									'cs:AddressLine' currentParty?.NAD?.C059_05?.E3042_04
								}

							}
							'cs:Reference' {
								'cs:Type' ''
								'cs:Number' ''
							}
							'PartyText' ''
							'EORINumber' ''
						}
					}

					currentTxn?.Group3_RFF.RFF?.C506_01?.each { currentRFF ->
						String stringReferenceTypeExtCde = currentRFF.E1153_01
						String stringReferenceTypeIntCde = util.getConversionByExtCdeWithDefault(TP_ID,MSG_TYPE_ID,DIR_ID,'ReferenceType',stringReferenceTypeExtCde,stringReferenceTypeExtCde,conn)
						// ALL CS code for CarrierRate
						switch (stringReferenceTypeIntCde){
							case ['CSO','TARIF','QUOT','SC','CSA']:
								'CarrierRate' {
									'cs:CSCarrierRateType' stringReferenceTypeIntCde
									'cs:CarrierRateNumber' currentRFF.E1154_02
								}
								break
							default:
								'ExternalReference' {
									'cs:CSReferenceType' stringReferenceTypeIntCde
									'cs:ReferenceNumber' currentRFF.E1154_02
								}
								break
						}
					}

//					currentTxn?.Group3_RFF.RFF?.C506_01?.findAll { it.E1153_01 == 'CT' }?.each { currentCarrierRate ->
//						'CarrierRate' {
//							'cs:CSCarrierRateType' 'SC'
//							'cs:CarrierRateNumber' currentCarrierRate.E1154_02
//						}
//					}
//
//					currentTxn?.Group3_RFF.RFF?.C506_01?.findAll { it.E1153_01 != 'CT' }?.each { currentExternalReference ->
//						switch (currentExternalReference.E1153_01) {
//							case 'BM':
//								'ExternalReference' {
//									'cs:CSReferenceType' 'BL'
//									'cs:ReferenceNumber' currentExternalReference?.E1154_02
//									'cs:ReferenceDescription' ''
//								}
//								break
//							case 'BN':
//								'ExternalReference' {
//									'cs:CSReferenceType' 'BKG'
//									'cs:ReferenceNumber' currentExternalReference?.E1154_02
//									'cs:ReferenceDescription' ''
//								}
//								break
//							case 'CR':
//								'ExternalReference' {
//									'cs:CSReferenceType' 'CR'
//									'cs:ReferenceNumber' currentExternalReference?.E1154_02
//									'cs:ReferenceDescription' ''
//								}
//								break
//							case 'FR':
//								'ExternalReference' {
//									'cs:CSReferenceType' 'FR'
//									'cs:ReferenceNumber' currentExternalReference?.E1154_02
//									'cs:ReferenceDescription' ''
//								}
//								break
//							case 'FI':
//								'ExternalReference' {
//									'cs:CSReferenceType' 'FID'
//									'cs:ReferenceNumber' currentExternalReference?.E1154_02
//									'cs:ReferenceDescription' ''
//								}
//								break
//							case 'IV':
//								'ExternalReference' {
//									'cs:CSReferenceType' 'INV'
//									'cs:ReferenceNumber' currentExternalReference?.E1154_02
//									'cs:ReferenceDescription' ''
//								}
//								break
//							case 'ON':
//								'ExternalReference' {
//									'cs:CSReferenceType' 'PO'
//									'cs:ReferenceNumber' currentExternalReference?.E1154_02
//									'cs:ReferenceDescription' ''
//								}
//								break
//							case 'RF':
//								'ExternalReference' {
//									'cs:CSReferenceType' 'EXPR'
//									'cs:ReferenceNumber' currentExternalReference?.E1154_02
//									'cs:ReferenceDescription' ''
//								}
//								break
//							case 'SI':
//								'ExternalReference' {
//									'cs:CSReferenceType' 'SID'
//									'cs:ReferenceNumber' currentExternalReference?.E1154_02
//									'cs:ReferenceDescription' ''
//								}
//								break
//						}
//					}
					'Route' {
						'Haulage' {
							'cs:OutBound' ''
							'cs:InBound' ''
						}
						'IsOBDropAndPoll' ''
						if (currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.size() != 0) {
							String stringDateTimeTobeConverted = currentTxn?.Group8_TDT.findAll {
								it.TDT.E8051_01 == '20'
							}.last()?.Group9_LOC.find { it.LOC.E3227_01 == '7' }?.DTM.C507_01.find {
								it.E2005_01 == '178' || it.E2005_01 == '132'
							}?.E2380_02
							String stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMddHHmm", "yyyy-MM-dd HH:mm:ss")
							switch (currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.last()?.Group9_LOC.find {
								it.LOC.E3227_01 == '7'
							}?.DTM?.C507_01.E2005_01) {
								case '178':
									'ArrivalAtFinalHub' 'Indicator': 'A', {
										'cs:GMT' ''
										'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
									}
									break
								case '132':
									'ArrivalAtFinalHub' 'Indicator': 'E', {
										'cs:GMT' ''
										'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
									}
									break
							}

							//start

							stringDateTimeTobeConverted = currentTxn?.Group8_TDT.findAll {
								it.TDT.E8051_01 == '20'
							}.last()?.Group9_LOC.find { it.LOC.E3227_01 == '7' }?.DTM.C507_01.find {
								it.E2005_01 == '178' || it.E2005_01 == '132'
							}?.E2380_02
							stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMddHHmm", "yyyy-MM-dd HH:mm:ss")
							switch (currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.last()?.Group9_LOC.find {
								it.LOC.E3227_01 == '7'
							}?.DTM?.C507_01.E2005_01) {
								case '178':
									'ArrivalAtFND' 'Indicator': 'A', {
										'cs:GMT' ''
										'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
									}
									break
								case '132':
									'ArrivalAtFND' 'Indicator': 'E', {
										'cs:GMT' ''
										'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
									}
									break
							}

							'FullReturnCutoffDT' {
								'cs:GMT' ''
								'cs:LocDT' ''
							}
							'DepartureDT' {
								'cs:GMT' ''
								stringDateTimeTobeConverted = currentTxn?.Group8_TDT.findAll {
									it.TDT.E8051_01 == '20'
								}.last()?.Group9_LOC.find { it.LOC.E3227_01 == '9' }?.DTM.C507_01.find {
									it.E2005_01 == '186' || it.E2005_01 == '133'
								}?.E2380_02
								stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMddHHmm", "yyyy-MM-dd HH:mm:ss")
								'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
							}
							'ArrivalDT' {
								'cs:GMT' ''
								stringDateTimeTobeConverted = currentTxn?.Group8_TDT.findAll {
									it.TDT.E8051_01 == '20'
								}.last()?.Group9_LOC.find { it.LOC.E3227_01 == '11' }?.DTM.C507_01.find {
									it.E2005_01 == '178' || it.E2005_01 == '132'
								}?.E2380_02
								stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMddHHmm", "yyyy-MM-dd HH:mm:ss")
								'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
							}
							if (currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.first()?.Group9_LOC.find {
								it.LOC.E3227_01 == '88'
							}) {
								currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.first()?.Group9_LOC.find {
									it.LOC.E3227_01 == '88'
								}.with { foundPOR ->
									'POR' {
										'cs:LocationName' foundPOR.LOC?.C517_02?.E3224_04.text().take(70)
										String stringUnLocationCode = foundPOR.LOC?.C517_02?.find {
											it?.E3055_03 == '6'
										}?.E3225_01
										Map mapLocationInfo = getLocationInfoFromUNLocCode(stringUnLocationCode, conn)
										'cs:CityDetails' {
											if (util.notEmpty(mapLocationInfo?.get('City'))) {
												'cs:City' mapLocationInfo?.get('City')
											} else {
												'cs:City' foundPOR.LOC?.C517_02?.E3224_04.text().take(60)
											}
											'cs:County' mapLocationInfo?.get('County')
											'cs:State' mapLocationInfo?.get('State')
											'cs:Country' mapLocationInfo?.get('Country')
											'cs:LocationCode' {
												'cs:MutuallyDefinedCode' ''
												'cs:UNLocationCode' stringUnLocationCode
												'cs:SchedKDType' ''
												'cs:SchedKDCode' ''
											}
										}
										'cs:Facility' {
											'cs:FacilityCode' ''
											'cs:FacilityName' ''
										}
										'cs:CSStandardCity' {
											'cs:CSParentCityID' ''
											'cs:CSStateCode' ''
											'cs:CSCountryCode' ''
											'cs:CSContinentCode' ''
										}
									}
								}
							}
							if (currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.last()?.Group9_LOC.find {
								it.LOC.E3227_01 == '7'
							}) {
								currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.last()?.Group9_LOC.find {
									it.LOC.E3227_01 == '7'
								}.with { foundFND ->
									'FND' {
										'cs:LocationName' foundFND.LOC?.C517_02?.E3224_04.text().take(70)
										String stringUnLocationCode = foundFND.LOC?.C517_02.find {
											it?.E3055_03 == '6'
										}?.E3225_01
										Map mapLocationInfo = getLocationInfoFromUNLocCode(stringUnLocationCode, conn)
										'cs:CityDetails' {
											if (util.notEmpty(mapLocationInfo?.get('City'))) {
												'cs:City' mapLocationInfo?.get('City')
											} else {
												'cs:City' foundFND.LOC?.C517_02?.E3224_04.text().take(60)
											}
											'cs:County' mapLocationInfo?.get('County')
											'cs:State' mapLocationInfo?.get('State')
											'cs:Country' mapLocationInfo?.get('Country')
											'cs:LocationCode' {
												'cs:MutuallyDefinedCode' ''
												'cs:UNLocationCode' stringUnLocationCode
												'cs:SchedKDType' ''
												'cs:SchedKDCode' ''
											}
										}
										'cs:Facility' {
											'cs:FacilityCode' ''
											'cs:FacilityName' ''
										}
										'cs:CSStandardCity' {
											'cs:CSParentCityID' ''
											'cs:CSStateCode' ''
											'cs:CSCountryCode' ''
											'cs:CSContinentCode' ''
										}
									}
								}
							}
							if (currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.first()) {
								currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.first().with { firstTDT ->
									firstTDT?.Group9_LOC.find { it.LOC.E3227_01 == '9' }.with { Group9_LOC ->
										String stringUnLocationCode = Group9_LOC.LOC?.C517_02.find {
											it?.E3055_03 == '6'
										}?.E3225_01
										Map mapLocationInfo = getLocationInfoFromUNLocCode(stringUnLocationCode, conn)
										Map mapPortInfo = getPortInfoFromUNLocCode(stringUnLocationCode, conn)
										String stringPortName
										if (util.notEmpty(mapPortInfo?.get('PortName'))) {
											stringPortName = mapPortInfo?.get('PortName')
										} else {
											stringPortName = Group9_LOC.LOC?.C517_02?.E3224_04.text().take(70)
										}
										if (util.notEmpty(stringPortName)) {
											'FirstPOL' {
												'cs:Port' {
													'cs:PortName' stringPortName
													'cs:PortCode' ''
													'cs:City' mapLocationInfo?.get('City')
													'cs:County' mapLocationInfo?.get('County')
													'cs:State' mapLocationInfo?.get('State')
													'cs:LocationCode' {
														'cs:MutuallyDefinedCode' ''
														'cs:UNLocationCode' stringUnLocationCode
														'cs:SchedKDType' mapPortInfo?.get('SchedKDType')
														'cs:SchedKDCode' mapPortInfo?.get('SchedKDCode')
													}
													'cs:Country' mapLocationInfo?.get('Country')
													'cs:CSPortID' mapPortInfo?.get('CSPortID')
													'cs:CSCountryCode' mapLocationInfo?.get('CSCountryCode')
												}
												'cs:Facility' {
													'cs:FacilityCode' ''
													'cs:FacilityName' ''
												}

												'OutboundSVVD' {
													'cs:Service' ''
													'cs:Vessel' ''
													'cs:VesselName' firstTDT.TDT?.C222_08?.E8212_04
													'cs:Voyage' firstTDT.TDT?.E8028_02
													'cs:Direction'
													'cs:LloydsNumber' firstTDT.TDT?.C222_08.find {
														it?.E1131_02 == 'L'
													}?.E8213_01
													'cs:CallSign' firstTDT.TDT?.C222_08.find {
														it?.E1131_02 == '103'
													}?.E8213_01
													'cs:CallNumber' ''
													'cs:VesselNationality' ''
													'DirectionName' ''
													'VesselNationalityCode' ''
												}
												'DepartureDT' {
													'cs:GMT' ''
													stringDateTimeTobeConverted = Group9_LOC?.DTM?.C507_01?.E2380_02
													stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMddHHmm", "yyyy-MM-dd HH:mm:ss")
													'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
												}
												'CSStateCode' ''
												'CSParentCityID' ''

											}
										}
									}
								}
							}
							if (currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.last()) {
								currentTxn?.Group8_TDT.findAll { it.TDT.E8051_01 == '20' }.last().with { lastTDT ->
									lastTDT?.Group9_LOC.find { it.LOC.E3227_01 == '11' }.with { Group9_LOC ->
										String stringUnLocationCode = Group9_LOC.LOC?.C517_02.find {
											it?.E3055_03 == '6'
										}?.E3225_01
										Map mapLocationInfo = getLocationInfoFromUNLocCode(stringUnLocationCode, conn)
										Map mapPortInfo = getPortInfoFromUNLocCode(stringUnLocationCode, conn)
										String stringPortName
										if (util.notEmpty(mapPortInfo?.get('PortName'))) {
											stringPortName = mapPortInfo?.get('PortName')
										} else {
											stringPortName = Group9_LOC.LOC?.C517_02?.E3224_04.text().take(70)
										}
										if (util.notEmpty(stringPortName)) {
											'LastPOD' {
												'cs:Port' {
													'cs:PortName' stringPortName
													'cs:PortCode' ''
													'cs:City' mapLocationInfo?.get('City')
													'cs:County' mapLocationInfo?.get('County')
													'cs:State' mapLocationInfo?.get('State')
													'cs:LocationCode' {
														'cs:MutuallyDefinedCode' ''
														'cs:UNLocationCode' stringUnLocationCode
														'cs:SchedKDType' mapPortInfo?.get('SchedKDType')
														'cs:SchedKDCode' mapPortInfo?.get('SchedKDCode')
													}
													'cs:Country' mapLocationInfo?.get('Country')
													'cs:CSPortID' mapPortInfo?.get('CSPortID')
													'cs:CSCountryCode' mapLocationInfo?.get('CSCountryCode')
												}
												'cs:Facility' {
													'cs:FacilityCode' ''
													'cs:FacilityName' ''
												}
												'InboundSVVD' {
													'cs:Service' ''
													'cs:Vessel' ''
													'cs:VesselName' lastTDT.TDT?.C222_08?.E8212_04
													'cs:Voyage' lastTDT.TDT?.E8028_02
													'cs:Direction' ''
													'cs:LloydsNumber' lastTDT.TDT?.C222_08.find {
														it?.E1131_02 == 'L'
													}?.E8213_01
													'cs:CallSign' lastTDT.TDT?.C222_08?.find {
														it.E1131_02 == '103'
													}?.E8213_01
													'cs:CallNumber' ''
													'cs:VesselNationality' ''
													'DirectionName' ''
													'VesselNationalityCode' ''
												}
												'ArrivalDT' {
													'cs:GMT' ''
													stringDateTimeTobeConverted = Group9_LOC?.DTM?.C507_01?.E2380_02
													stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMddHHmm", "yyyy-MM-dd HH:mm:ss")
													'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
												}
												'CSStateCode' ''
												'CSParentCityID' ''
											}
										}
									}
								}
							}
						}

						currentTxn?.Group8_TDT.eachWithIndex { currentOceanLeg, currentOceanLegIndex ->
							'OceanLeg' {
								'cs:LegSeq' currentOceanLegIndex + 1
								currentOceanLeg.Group9_LOC.LOC.find { it.E3227_01 == '9' }.with { polLOC ->
									String stringUnLocationCode = polLOC.C517_02.find { it.E3055_03 == '6' }.E3225_01
									Map mapLocationInfo = getLocationInfoFromUNLocCode(stringUnLocationCode, conn)
									Map mapPortInfo = getPortInfoFromUNLocCode(stringUnLocationCode, conn)
									'cs:POL' {
										'cs:Port' {
											if (util.notEmpty(mapPortInfo?.get('PortName'))) {
												'cs:PortName' mapPortInfo?.get('PortName')
											} else {
												'cs:PortName' polLOC.LOC?.C517_02?.E3224_04.text().take(70)
											}
											'cs:PortCode' ''
											'cs:City' mapLocationInfo?.get('City')
											'cs:County' mapLocationInfo?.get('County')
											'cs:State' mapLocationInfo?.get('State')
											'cs:LocationCode' {
												'cs:MutuallyDefinedCode' ''
												'cs:UNLocationCode' stringUnLocationCode
												'cs:SchedKDType' mapPortInfo?.get('SchedKDType')
												'cs:SchedKDCode' mapPortInfo?.get('SchedKDCode')
											}
											'cs:Country' mapLocationInfo?.get('Country')
											'cs:CSPortID' mapPortInfo?.get('CSPortID')
											'cs:CSCountryCode' mapLocationInfo?.get('CSCountryCode')
										}
										'cs:Facility' {
											'cs:FacilityCode' ''
											'cs:FacilityName' ''
										}
									}
								}
								currentOceanLeg.Group9_LOC.LOC.find { it.E3227_01 == '11' }.with { podLOC ->
									String stringUnLocationCode = podLOC.C517_02.find { it.E3055_03 == '6' }.E3225_01
									Map mapLocationInfo = getLocationInfoFromUNLocCode(stringUnLocationCode, conn)
									Map mapPortInfo = getPortInfoFromUNLocCode(stringUnLocationCode, conn)
									'cs:POD' {
										'cs:Port' {
											if (util.notEmpty(mapPortInfo?.get('PortName'))) {
												'cs:PortName' mapPortInfo?.get('PortName')
											} else {
												'cs:PortName' podLOC.LOC?.C517_02?.E3224_04.text().take(70)
											}
											'cs:PortCode' ''
											'cs:City' mapLocationInfo?.get('City')
											'cs:County' mapLocationInfo?.get('County')
											'cs:State' mapLocationInfo?.get('State')
											'cs:LocationCode' {
												'cs:MutuallyDefinedCode' ''
												'cs:UNLocationCode' stringUnLocationCode
												'cs:SchedKDType' mapPortInfo?.get('SchedKDType')
												'cs:SchedKDCode' mapPortInfo?.get('SchedKDCode')
											}
											'cs:Country' mapLocationInfo?.get('Country')
											'cs:CSPortID' mapPortInfo?.get('CSPortID')
											'cs:CSCountryCode' mapLocationInfo?.get('CSCountryCode')
										}
										'cs:Facility' {
											'cs:FacilityCode' ''
											'cs:FacilityName' ''
										}
									}
								}
								'cs:SVVD' {
									'cs:Loading' {
										'cs:Service' ''
										'cs:Vessel' ''
										'cs:VesselName' currentOceanLeg.TDT.C222_08.E8212_04
										'cs:Voyage' currentOceanLeg.TDT.E8028_02
										'cs:Direction' ''
										'cs:LloydsNumber' currentOceanLeg.TDT.C222_08.find {
											it.E1131_02 == 'L'
										}.E8213_01
										'cs:CallSign' currentOceanLeg.TDT.C222_08.find { it.E1131_02 == '103' }.E8213_01
										'cs:CallNumber' ''
										'cs:VesselNationality' ''
									}
									'cs:Discharge' {
										'cs:Service' ''
										'cs:Vessel' ''
										'cs:VesselName' currentOceanLeg.TDT.C222_08.E8212_04
										'cs:Voyage' currentOceanLeg.TDT.E8028_02
										'cs:Direction' ''
										'cs:LloydsNumber' currentOceanLeg.TDT.C222_08.find {
											it.E1131_02 == 'L'
										}.E8213_01
										'cs:CallSign' currentOceanLeg.TDT.C222_08.find { it.E1131_02 == '103' }.E8213_01
										'cs:CallNumber' ''
										'cs:VesselNationality' ''
									}
								}

								String stringDateTimeTobeConverted = currentOceanLeg.Group9_LOC.find {
									it.LOC.E3227_01 == '9'
								}.DTM?.C507_01.find { it.E2005_01 == '133' || it.E2005_01 == '186' }?.E2380_02
								String stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMddHHmm", "yyyy-MM-dd HH:mm:ss")
								switch (currentOceanLeg.Group9_LOC.find {
									it.LOC.E3227_01 == '9'
								}.DTM.C507_01.E2005_01) {
									case '133':
										'cs:DepartureDT' 'Indicator': 'E', {
											'cs:GMT' ''
											'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
										}
										break
									case '186':
										'cs:DepartureDT' 'Indicator': 'A', {
											'cs:GMT' ''
											'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
										}
										break
								}
								stringDateTimeTobeConverted = currentOceanLeg.Group9_LOC.find {
									it.LOC.E3227_01 == '11'
								}.DTM?.C507_01.find { it.E2005_01 == '132' || it.E2005_01 == '178' }?.E2380_02
								stringDateTimeConverted = util.convertDateTime(stringDateTimeTobeConverted, "yyyyMMddHHmm", "yyyy-MM-dd HH:mm:ss")
								switch (currentOceanLeg.Group9_LOC.find {
									it.LOC.E3227_01 == '11'
								}.DTM.C507_01.E2005_01) {
									case '132':
										'cs:ArrivalDT' 'Indicator': 'E', {
											'cs:GMT' ''
											'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
										}
										break
									case '178':
										'cs:ArrivalDT' 'Indicator': 'A', {
											'cs:GMT' ''
											'cs:LocDT' stringDateTimeConverted.replace(' ', 'T')
										}
										break
								}
								'CarrierExtractDT' {
									'cs:GMT' ''
									'cs:LocDT' ''
								}
								'LoadingDirectionName' ''
								'LoadingVesselNationalityCode' ''
								'DischargeDirectionName' ''
								'DischargeVesselNationalityCode' ''
								'POLCSStateCode' ''
								'PODCSStateCode' ''
								'POLCSParentCityID' ''
								'PODCSParentCityID' ''
								'LoadingExtVoyage' ''
								'DischargeExtVoyage' ''
							}
						}

						'StopOff' {
							'cs:SequenceNumber' ''
							'cs:PickupDetails' {
								'cs:City' {
									'cs:City' ''
									'cs:County' ''
									'cs:State' ''
									'cs:Country' ''
									'cs:LocationCode' {
										'cs:MutuallyDefinedCode' ''
										'cs:UNLocationCode' ''
										'cs:SchedKDType' ''
										'cs:SchedKDCode' ''
									}
								}
								'cs:Facility' {
									'cs:FacilityCode' ''
									'cs:FacilityName' ''
								}
							}
							'cs:ReturnDetails' {
								'cs:City' {
									'cs:City' ''
									'cs:County' ''
									'cs:State' ''
									'cs:Country' ''
									'cs:LocationCode' {
										'cs:MutuallyDefinedCode' ''
										'cs:UNLocationCode' ''
										'cs:SchedKDType' ''
										'cs:SchedKDCode' ''
									}
								}
								'cs:Facility' {
									'cs:FacilityCode' ''
									'cs:FacilityName' ''
								}
							}
							'cs:STOPOFF_LEG_UUID' ''
						}
						'ActualCargoReceiptDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'CSS_BL_RTE_INFO_UUID' ''
						'Inbound_intermodal_Indicator' ''
						'Outbound_intermodal_Indicator' ''
					}
					//1_start
					currentTxn.Group35_EQD.each { currentContainer ->
						'Container' {
							'cs:ContainerNumber' currentContainer.EQD?.C237_02?.E8260_01.text().take(10)
							if (currentContainer.EQD?.C237_02?.E8260_01.text().length() >= 11)
								'cs:ContainerCheckDigit' currentContainer.EQD?.C237_02?.E8260_01.text().substring(10, 10 + 1) //from the containerNUmber[10], get 1 character
							String stringCarrContainerSize = currentContainer.EQD?.C224_03?.E8155_01
							String stringCSContainerSizeType = util.getConversionByExtCdeWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'ContainerType', stringCarrContainerSize, stringCarrContainerSize, conn)
							'cs:CSContainerSizeType' stringCSContainerSizeType
							'cs:CarrCntrSizeType' stringCarrContainerSize
							switch (currentContainer.EQD?.E8077_04) {
								case '1':
									'cs:IsSOC' '1'
									break
								case '2':
									'cs:IsSOC' '0'
									break
							}
							'cs:GrossWeight' {
								'cs:Weight' currentContainer?.MEA.find { it.E6311_01 == 'WT' && it?.C502_02?.E6313_01 == 'G' }?.C174_03.E6314_02
								switch (currentContainer?.MEA.find { it.E6311_01 == 'WT' && it?.C502_02?.E6313_01 == 'G' }?.C174_03.E6411_01) {
									case 'KGM':
										'cs:WeightUnit' 'KGS'
										break
									case 'LBS':
										'cs:WeightUnit' 'LBS'
										break
									case 'TON':
										'cs:WeightUnit' 'TON'
										break
								}
							}
							currentContainer?.SEL.each { currentSEL ->
								switch (currentSEL?.C215_02?.E9303_01) {
									case ['CA', 'SH']:
										'cs:Seal' {
											'cs:SealType' currentSEL?.C215_02?.E9303_01
											'cs:SealNumber' currentSEL?.E9308_01
										}
										break
								}
							}
						}
						'ContainerFlowInstruction' {
							'EmptyReturn' {
								'NumberOfContainers' ''
								'ISOSizeType' ''
								'CSContainerSizeType' ''
								'ContainerNumbers' {
									'ContainerNumber' ''
									'CheckDigit' ''
								}
								'Facility' {
									'cs:FacilityCode' ''
									'cs:FacilityName' ''
								}
								'MvmtDT' {
									'cs:GMT' ''
									'cs:LocDT' ''
								}
								'Address' {
									'cs:City' ''
									'cs:County' ''
									'cs:State' ''
									'cs:Country' ''
									'cs:LocationCode' {
										'cs:MutuallyDefinedCode' ''
										'cs:UNLocationCode' ''
										'cs:SchedKDType' ''
										'cs:SchedKDCode' ''
									}
									'cs:PostalCode' ''
									'cs:AddressLines' {
										'cs:AddressLine' ''
									}
								}
								'Contact' {
									'cs:FirstName' ''
									'cs:LastName' ''
									'cs:ContactPhone' {
										'cs:CountryCode' ''
										'cs:AreaCode' ''
										'cs:Number' ''
									}
									'cs:ContactFax' {
										'cs:CountryCode' ''
										'cs:AreaCode' ''
										'cs:Number' ''
									}
									'cs:ContactEmailAddress' ''
								}
								'CSS_BL_RTE_INFO_UUID' ''
							}
						}
					}
					currentTxn.Group18_GID.each { currentCargo ->
						String stringCurrentCargoNature //Used for GCCargoUnit
						'Cargo' {
							if (util.notEmpty(currentCargo?.Group27_SGP)) {
								if (util.notEmpty(currentCargo?.Group30_DGS.DGS) && util.notEmpty(currentTxn?.Group35_EQD?.find {
									it.EQD?.C237_02?.E8260_01 == currentCargo.Group27_SGP.SGP.C237_01?.E8260_01
								}.TMP)) {
									stringCurrentCargoNature = 'RD'
									'cs:CargoNature' 'RD'
								} else if (util.notEmpty(currentCargo?.Group30_DGS.DGS)) {
									stringCurrentCargoNature = 'DG'
									'cs:CargoNature' 'DG'
								} else if (util.notEmpty(currentTxn?.Group35_EQD?.find {
									it.EQD?.C237_02?.E8260_01 == currentCargo.Group27_SGP.SGP.C237_01?.E8260_01
								}.TMP)) {
									stringCurrentCargoNature = 'RF'
									'cs:CargoNature' 'RF'
								} else {
									stringCurrentCargoNature = 'GC'
									'cs:CargoNature' 'GC'
								}
							} else {
								if (util.notEmpty(currentCargo?.Group30_DGS.DGS) && util.notEmpty(currentTxn?.Group35_EQD?.TMP)) {
									stringCurrentCargoNature = 'RD'
									'cs:CargoNature' 'RD'
								} else if (util.notEmpty(currentCargo?.Group30_DGS.DGS)) {
									stringCurrentCargoNature = 'DG'
									'cs:CargoNature' 'DG'
								} else if (util.notEmpty(currentTxn?.Group35_EQD?.TMP)) {
									stringCurrentCargoNature = 'RF'
									'cs:CargoNature' 'RF'
								} else {
									stringCurrentCargoNature = 'GC'
									'cs:CargoNature' 'GC'
								}
							}


							'cs:CargoDescription' currentCargo?.FTX.findAll { it.E4451_01 == 'AAA' }?.C108_04?.join(' ')
							'cs:Packaging' {
								String stringCarrPackageType = currentCargo.GID?.C213_02?.E7065_02
								String stringCSPackageType = util.getConversionByExtCdeWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'PackageUnit', stringCarrPackageType, stringCarrPackageType, conn)
								'cs:PackageType' stringCSPackageType
								'cs:PackageQty' currentCargo.GID?.C213_02?.E7224_01
//							    'cs:PackageDesc' ''
//							    'cs:PackageMaterial' ''
							}
							'cs:GrossWeight' {
								'cs:Weight' currentCargo?.Group20_MEA.MEA?.find { it.E6311_01 == 'WT' && it.C502_02.E6313_01 == 'G' }?.C174_03?.E6314_02
								switch (currentCargo?.Group20_MEA.MEA?.find { it.E6311_01 == 'WT' && it.C502_02.E6313_01 == 'G' }?.C174_03.E6411_01) {
									case 'KGM':
										'cs:WeightUnit' 'KGS'
										break
									case 'LBS':
										'cs:WeightUnit' 'LBS'
										break
									case 'TON':
										'cs:WeightUnit' 'TON'
										break
								}
							}
							'cs:NetWeight' {
								'cs:Weight' ''
								'cs:WeightUnit' ''
							}
							'cs:Volume' {
								'cs:Volume' currentCargo?.Group20_MEA.MEA?.find { it.E6311_01 == 'VOL' && it?.C502_02?.E6313_01 == 'AAW' }?.C174_03?.E6314_02
								switch (currentCargo?.Group20_MEA.MEA?.find { it.E6311_01 == 'VOL' && it?.C502_02?.E6313_01 == 'AAW' }?.C174_03.E6411_01) {
									case 'CBF':
										'cs:VolumeUnit' 'CBF'
										break
									case 'CBM':
										'cs:VolumeUnit' 'CBM'
										break
								}
							}
							currentCargo.Group23_PCI.eachWithIndex { currentPCI, currentPCIIndex ->
								'cs:MarksAndNumbers' {
									'cs:SeqNumber' currentPCIIndex + 1
									'cs:MarksAndNumbersLine' currentPCI.PCI.C210_02.findAll().join(" ").take(240)
									'cs:CGO_MRK_NUM_ID' ''
								}
							}
//						    'DisplaySequenceNumber' ''
//						    'ContainerNumber' ''
//						    'ContainerCheckDigit' ''
							String stringCurrentContainerNumber = currentCargo?.Group27_SGP.SGP?.C237_01?.E8260_01
							'CurrentContainerNumber' stringCurrentContainerNumber.take(10)
							if (stringCurrentContainerNumber.length() >= 11)
								'CurrentContainerCheckDigit' stringCurrentContainerNumber.substring(10, 10 + 1)
							'TrafficModel' {
								switch (currentTxn?.TSR?.C233_02.E7273_01) {
									case '2':
										'cs:OutBound' 'FCL'
										break
									case '3':
										'cs:OutBound' 'LCL'
										break
								}
								switch (currentTxn?.TSR?.C233_02?.E7273_04) {
									case '2':
										'cs:InBound' 'FCL'
										break
									case '3':
										'cs:InBound' 'LCL'
										break
								}
							}
							'CgoPkgName' ''
							'CgoPkgCodeCount' ''
							'CustomsRefNumber' ''
							'CustomsRefType' ''
							'CustomsRefDT' {
								'cs:GMT' ''
								'cs:LocDT' ''
							}
							'CustomsClearanceLocCode' ''
							'CustomsClearanceLocCity' ''
							'CustomsClearanceLocType' ''
							'CustomsClearanceLocDesc' ''
							'Seal' {
								'cs:SealType' ''
								'cs:SealNumber' ''
							}
							'ArticleNumber' ''
							'CommodityDescription' ''
							'BLCargoDescription' ''

							if (stringCurrentCargoNature == 'GC')
								'GCPackageUnit' {
									'cs:PackageSeqNumber' '1'
									'cs:PackageUnitQuantity' currentCargo.GID?.C213_02?.E7224_01
									'cs:PackageUnitDescription' ''
								}
							if (currentTxn.Group35_EQD.find {
								it.EQD?.C237_02?.E8260_01 == stringCurrentContainerNumber
							}) {
								currentTxn.Group35_EQD.find {
									it.EQD?.C237_02?.E8260_01 == stringCurrentContainerNumber
								}.with { foundContainerGroup ->
									'ReeferCargoSpec' {
										'cs:AtmosphereType' ''
										'cs:Temperature' {
											'cs:Temperature' foundContainerGroup.TMP?.C239_02?.E6246_01
											switch (foundContainerGroup.TMP?.C239_02?.E6411_02) {
												case 'CEL':
													'cs:TemperatureUnit' 'C'
													break
												case 'FAH':
													'cs:TemperatureUnit' 'F'
													break
											}
										}
										'cs:Ventilation' {
											'cs:Ventilation' ''
											'cs:VentilationUnit' ''
										}
										'cs:GensetType' ''
										'cs:Remarks' ''
										'cs:CO2' ''
										'cs:O2' ''
										'cs:VentSettingCode' ''
										'cs:DehumidityPercentage' ''
										'cs:SensitiveCargoDesc' ''
										'cs:IsPreCoolingReq'
										'cs:IsControlledAtmosphere'
										'cs:IsReeferOperational'
										'cs:EmergencyContact' {
											'cs:FirstName' ''
											'cs:LastName' ''
											'cs:ContactPhone' {
												'cs:CountryCode' ''
												'cs:AreaCode' ''
												'cs:Number' ''
											}
											'cs:ContactFax' {
												'cs:CountryCode' ''
												'cs:AreaCode' ''
												'cs:Number' ''
											}
											'cs:ContactEmailAddress' ''
											'cs:Type' ''
											'cs:EM_CTACT_UUID' ''
										}
										'cs:RF_CGO_INFO_UUID' ''
										if (stringCurrentCargoNature == 'RF' || stringCurrentCargoNature == 'RD')
											'RFPackageUnit' {
												'cs:PackageSeqNumber' '1'
												'cs:PackageUnitQuantity' currentCargo.GID?.C213_02?.E7224_01
												'cs:PackageUnitDescription' ''
											}
									}
								}
							}
							'AWCargoSpec' {
								'cs:Height' {
									'cs:Length' ''
									'cs:LengthUnit' ''
								}
								'cs:Length' {
									'cs:Length' ''
									'cs:LengthUnit' ''
								}
								'cs:Width' {
									'cs:Length' ''
									'cs:LengthUnit' ''
								}
								'cs:GrossWeight' {
									'cs:Weight' ''
									'cs:WeightUnit' ''
								}
								'cs:IsShipsideDelivery' ''
								'cs:Remarks' ''
								'cs:EmergencyContact' {
									'cs:FirstName' ''
									'cs:LastName' ''
									'cs:ContactPhone' {
										'cs:CountryCode' ''
										'cs:AreaCode' ''
										'cs:Number' ''
									}
									'cs:ContactFax' {
										'cs:CountryCode' ''
										'cs:AreaCode' ''
										'cs:Number' ''
									}
									'cs:ContactEmailAddress' ''
									'cs:Type' ''
									'cs:EM_CTACT_UUID' ''
								}
								'cs:AW_CGO_INFO_UUID' ''
								'AWPackageUnit' {
									'cs:PackageSeqNumber' ''
									'cs:PackageUnitQuantity' ''
									'cs:PackageUnitDescription' ''
								}
							}
							if (currentCargo?.Group30_DGS) currentCargo.Group30_DGS.with { currentDGCargo ->
								'DGCargoSpec' {
									'cs:DGRegulator' ''
									'cs:IMDGPage' currentDGCargo.DGS?.C205_02.E8351_01
									'cs:IMOClass' currentDGCargo.DGS?.C205_02?.E8078_02
									'cs:UNNumber' currentDGCargo.DGS?.C234_03?.E7124_01
									'cs:TechnicalName' currentDGCargo?.FTX.find {
										it.E4451_01 == 'AAD'
									}?.C108_04.E4440_01.text().take(180)
									'cs:ChinaDGNumber' ''
									'cs:ProperShippingName' currentDGCargo?.FTX.find {
										it.E4451_01 == 'ACB'
									}?.C108_04.E4440_01.text().take(80)
									'cs:PackageGroup' {
										'cs:Code' currentDGCargo.DGS?.E8339_05
										'cs:InnerPackageDescription' {
											'cs:PackageType' ''
											'cs:PackageQty' ''
											'cs:PackageDesc' ''
											'cs:PackageMaterial' ''
										}
										'cs:OuterPackageDescription' {
											'cs:PackageType' ''
											'cs:PackageQty' ''
											'cs:PackageDesc' ''
											'cs:PackageMaterial' ''
										}
									}
									'cs:MFAGNumber' ''
									'cs:EMSNumber' ''
									'cs:PSAClass' ''
									'cs:ApprovalCode' ''
									'cs:GrossWeight' {
										'cs:Weight' ''
										'cs:WeightUnit' ''
									}
									'cs:NetWeight' {
										'cs:Weight' ''
										'cs:WeightUnit' ''
									}
									'cs:NetExplosiveWeight' {
										'cs:Weight' ''
										'cs:WeightUnit' ''
									}
									'cs:FlashPoint' {
										'cs:Temperature' currentDGCargo.DGS?.C223_04?.E7106_01
										switch (currentDGCargo.DGS?.C223_04?.E6411_02) {
											case 'CEL':
												'cs:TemperatureUnit' 'C'
												break
											case 'FAH':
												'cs:TemperatureUnit' 'F'
												break
										}
									}
									'cs:ElevatedTemperature' {
										'cs:Temperature' ''
										'cs:TemperatureUnit' ''
									}
									'cs:isLimitedQuantity' ''
									'cs:IsInhalationHazardous' ''
									'cs:IsReportableQuantity' ''
									'cs:IsEmptyUnclean' ''
									'cs:isMarinePollutant' ''
									'cs:State' ''
									'cs:Label' ''
									String stringRemarks = currentDGCargo?.FTX.findAll {
										it.E4451_01 == 'AAC'
									}?.C108_04?.E4440_01.join(" ")
									if (stringRemarks.length() > 4000) {
										'cs:Remarks' stringRemarks.substring(0, 0 + 4000)
										//max 4000 char in cs2xml schema
									} else {
										'cs:Remarks' stringRemarks
									}
									currentDGCargo?.Group31_CTA.findAll {
										it.CTA?.E3139_01 == 'HG'
									}.each { currentDGCargoCTA ->
										'cs:EmergencyContact' {
											'cs:FirstName' currentDGCargoCTA.CTA?.C056_02?.E3412_02
											'cs:LastName' ''
											if (currentDGCargoCTA.COM.C076_01.find { it?.E3155_02 == 'TE' }?.E3148_01 != '') {
												'cs:ContactPhone' {
													'cs:CountryCode'
													'cs:AreaCode'
													'cs:Number' currentDGCargoCTA.COM.C076_01.find { it?.E3155_02 == 'TE' }?.E3148_01.text().take(30)
												}
											}
											'cs:ContactFax' {
												'cs:CountryCode' ''
												'cs:AreaCode' ''
												'cs:Number' ''
											}
											'cs:ContactEmailAddress' ''
											'cs:Type' ''
											'cs:EM_CTACT_UUID' ''
										}
									}
									'cs:DG_CGO_INFO_UUID' ''
									if (stringCurrentCargoNature == 'RD' || stringCurrentCargoNature == 'DG') {
										'DGPackageUnit' {
											'cs:PackageSeqNumber' '1'
											'cs:PackageUnitQuantity' currentCargo.GID?.C213_02?.E7224_01
											'cs:PackageUnitDescription' ''
										}
									}
								}
							}
							'ExternalReference' {
								'cs:CSReferenceType' ''
								'cs:ReferenceNumber' ''
								'cs:ReferenceDescription' ''
							}
							'HarmonizedTariffSchedule' ''
							'CSS_BL_CGO_UUID' ''
						}
					}
					'Appointment' {
						'cs:AppointmentDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'cs:Address' {
							'cs:City' ''
							'cs:County' ''
							'cs:State' ''
							'cs:Country' ''
							'cs:LocationCode' {
								'cs:MutuallyDefinedCode' ''
								'cs:UNLocationCode' ''
								'cs:SchedKDType' ''
								'cs:SchedKDCode' ''
							}
							'cs:PostalCode' ''
							'cs:AddressLines' {
								'cs:AddressLine' ''
							}
							'cs:Street' ''
						}
						'cs:Company' ''
						'cs:Contact' {
							'cs:FirstName' ''
							'cs:LastName' ''
							'cs:ContactPhone' {
								'cs:CountryCode' ''
								'cs:AreaCode' ''
								'cs:Number' ''
							}
							'cs:ContactFax' {
								'cs:CountryCode' ''
								'cs:AreaCode' ''
								'cs:Number' ''
							}
							'cs:ContactEmailAddress' ''
						}
						'cs:Sequence' ''
						'cs:Type' ''
						'cs:ActualArrivalDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'cs:ActualDepartureDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'cs:TransportLegCarrier' ''
						'cs:Container' {
							'cs:ContainerNumber' ''
							'cs:ContainerCheckDigit' ''
							'cs:CSContainerSizeType' ''
							'cs:CarrCntrSizeType' ''
						}
						'cs:CSS_RTE_INFO_UUID' ''
					}
					'BLCertClause' {
						'cs:CertificationClauseType' ''
						'cs:CertificationClauseText' ''
					}
					'DnD' {
						'ContainerNumber' ''
						'ContainerCheckDigit' ''
						'FreeTimeStartDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'FreeTimeEndDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'FreeTime' ''
						'FreeTimeType' ''
						'CSS_BL_CNTR_UUID' ''
						'ClockEndDT' {
							'cs:GMT' ''
							'cs:LocDT' ''
						}
						'IsCombo' ''
					}
					currentTxn.Group7_TCC.each { currentTCC ->
						'FreightCharge' {
							'DisplaySeqNumber' ''
							switch (currentTCC.TCC?.C200_01?.E4237_05) {
								case 'C':
									'ChargeType' '0'
									break
								case 'P':
									'ChargeType' '1'
									break
							}
							'UnBill' ''
							'PayableElseWhere' ''
							'ChargeCode' currentTCC.TCC?.C200_01?.E8023_01
							'ChargePrintLable' ''
							'Basis' ''
							'FreightRate' currentTCC.TCC?.C203_02?.E5242_04
							'CalculateMethod' ''
							String stringCurrency = currentTCC?.MOA?.C516_01?.E6345_03
							'ChargeAmount' 'Currency': stringCurrency, currentTCC?.MOA?.C516_01?.E5004_02
							'BillingAmount' ''
							'ChargeNetAmount' ''
							'PaymentCurrency' ''
							'InvoiceNumber' ''
							'ControlOfficeCode' ''
							'CollectionOfficeCode' ''
							'TotalAmount' 'Currency': stringCurrency, currentTCC?.MOA?.C516_01?.E5004_02
							'TotalAmtInPmtCurrency'
							'SVVD' {
								'cs:Service' ''
								'cs:Vessel' ''
								'cs:VesselName' ''
								'cs:Voyage' ''
								'cs:Direction' ''
								'cs:LloydsNumber' ''
								'cs:CallSign' ''
							}
							'ExchRateToEurope' ''
							'OBCustomsUNVoyRef' ''
							'IBCustomsUNVoyRef' ''
							'OBVoyRef' ''
							'IBVoyRef' ''
							'IsApprovedForCustomer' ''
							'ChargeDesc' currentTCC.TCC?.C200_01?.E8022_04
							'PayByInformation' {
								'PayerName' ''
								'CarrierCustomerCode' ''
								'CityDetails' {
									'cs:City' ''
									'cs:County' ''
									'cs:State' ''
									'cs:Country' ''
									'cs:LocationCode' {
										'cs:MutuallyDefinedCode' ''
										'cs:UNLocationCode' ''
										'cs:SchedKDType' ''
										'cs:SchedKDCode' ''
									}
								}
							}
							'RatePercentage' ''
							'TotalAmtInAdditionalPmtCurrency' ''
						}
					}
					currentTxn?.Group35_EQD?.Group36_TCC.each { currentContainerTCC ->
						'FreightChargeCNTR' {
							'DisplaySeqNumber' ''
							switch (currentContainerTCC.TCC?.C200_01?.E4237_05) {
								case 'C':
									'ChargeType' '0'
									break
								case 'P':
									'ChargeType' '1'
									break
							}
							'UnBill' ''
							'PayableElseWhere' ''
							'ChargeCode' currentContainerTCC.TCC?.C200_01?.E8023_01
							'ChargePrintLable' ''
							'Basis' ''
							'FreightRate' currentContainerTCC.TCC?.C203_02?.E5242_04
							'CalculateMethod' ''
							String stringCurrency = currentContainerTCC?.MOA?.C516_01?.E6345_03
							'ChargeAmount' 'Currency': stringCurrency, currentContainerTCC?.MOA?.C516_01?.E5004_02
							'BillingAmount' ''
							'PaymentCurrency' ''
							'InvoiceNumber' ''
							'ControlOfficeCode' ''
							'CollectionOfficeCode' ''
							'TotalAmount' 'Currency': stringCurrency, currentContainerTCC?.MOA?.C516_01?.E5004_02
							'TotalAmtInPmtCurrency' ''
							'SVVD' {
								'cs:Service' ''
								'cs:Vessel' ''
								'cs:VesselName' ''
								'cs:Voyage' ''
								'cs:Direction' ''
								'cs:LloydsNumber' ''
								'cs:CallSign' ''
							}
							'ExchRateToEurope' ''
							'OBCustomsUNVoyRef' ''
							'IBCustomsUNVoyRef' ''
							'OBVoyRef' ''
							'IBVoyRef' ''
							'IsApprovedForCustomer' ''
							'ChargeDesc' currentContainerTCC.TCC?.C200_01?.E8022_04
							'PayByInformation' {
								'PayerName' ''
								'CarrierCustomerCode' ''
								'CityDetails' {
									'cs:City' ''
									'cs:County' ''
									'cs:State' ''
									'cs:Country' ''
									'cs:LocationCode' {
										'cs:MutuallyDefinedCode' ''
										'cs:UNLocationCode' ''
										'cs:SchedKDType' ''
										'cs:SchedKDCode' ''
									}
								}
							}
							'RatePercentage' ''
							'TotalAmtInAdditionalPmtCurrency' ''
							'ContainerNumber' ''
							'ContainerCheckDigit' ''
							'CSContainerSizeType' ''
							'CarrCntrSizeType' ''
							'OOCLCntrSizeType' ''
						}
					}
					'RailInformation' {
						'RailCarrier' ''
						'RailPickupNumber' ''
						'ContainerNumber' ''
						'ContainerCheckDigit' ''
					}
					'Remarks' ''
				}
			}
		}

		//clean the outputXml
		//convert the object to xml string
		String cleanedOutputXml = cleanXml(writer.toString())
		def billOfLading = new XmlSlurper().parseText(cleanedOutputXml)

		// prepare schema validator
		cs.b2b.core.common.xmlvalidation.ValidateXML validator = new ValidateXML()
		//prepare Node version of billOfLading
		Node nodeBillOfBading = new XmlParser().parseText(cleanedOutputXml)
//		prepare msg with header
		Node msgWithHeader = nodeBillOfBading.clone()
		List kids= new LinkedList()
		msgWithHeader.children().eachWithIndex {kid , index->
			if (index !=0){
				kids.add(kid)
			}
		}
		kids.eachWithIndex {kid , index->
				msgWithHeader.remove(kid)
		}
		//build bizkey
		bizKeyXml.'root' {
			iftmcs.Group_UNH.eachWithIndex { currentTxn, currentTxnIndex ->
				'ns0:Transaction'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
					'ns0:ControlNumberInfo' {
						'ns0:Interchange' iftmcs.UNB.E0020_05
						'ns0:Group' currentTxnIndex+1
						'ns0:Transaction' currentTxn.UNH.E0062_01
					}
					//map
					'ns0:BizKey' {
						'ns0:Type' 'LKP'
						'ns0:Value' currentTxn?.Group11_NAD.NAD.find {
							it.E3035_01 == "CA"
						}?.C082_02?.E3039_01.text() + ',' + currentTxn.BGM?.C106_02?.E1004_01.text()
					}
					'ns0:BizKey' {
						'ns0:Type' 'STPID'
						'ns0:Value' TP_ID
					}
					'ns0:BizKey' {
						'ns0:Type' 'RTPID'
						'ns0:Value' 'CARGOSMART'
					}
					currentTxn.Group3_RFF.each { currentGroup3 ->
						switch (currentGroup3.RFF.C506_01.E1153_01) {
							case 'CT':
								'ns0:BizKey' {
									'ns0:Type' 'CT'
									'ns0:Value' currentGroup3.RFF.C506_01.E1154_02
								}
								break
							case 'IV':
								'ns0:BizKey' {
									'ns0:Type' 'INV'
									'ns0:Value' currentGroup3.RFF.C506_01.E1154_02
								}
								break
							case 'CR':
								'ns0:BizKey' {
									'ns0:Type' 'CRN'
									'ns0:Value' currentGroup3.RFF.C506_01.E1154_02
								}
								break
							case 'FF':
								'ns0:BizKey' {
									'ns0:Type' 'FR'
									'ns0:Value' currentGroup3.RFF.C506_01.E1154_02
								}
								break
							case 'BM':
								'ns0:BizKey' {
									'ns0:Type' 'BL_NUM'
									'ns0:Value' currentGroup3.RFF.C506_01.E1154_02
								}
								break
							case 'SI':
								'ns0:BizKey' {
									'ns0:Type' 'SID'
									'ns0:Value' currentGroup3.RFF.C506_01.E1154_02
								}
								break
							case 'BN':
								'ns0:BizKey' {
									'ns0:Type' 'BK_NUM'
									'ns0:Value' currentGroup3.RFF.C506_01.E1154_02
								}
								break
							case 'ON':
								'ns0:BizKey' {
									'ns0:Type' 'PRN'
									'ns0:Value' currentGroup3.RFF.C506_01.E1154_02
								}
								break
						}
					}

					currentTxn?.Group35_EQD.each { currentContainer ->
						'ns0:BizKey' {
							'ns0:Type' 'CNTR_NUM'
							'ns0:Value' currentContainer.EQD.C237_02.E8260_01.text().take(10)
						}
					}

					currentTxn?.Group11_NAD.each { currentParty ->
						switch (currentParty.NAD.E3035_01) {
							case 'CN':
								'ns0:BizKey' {
									'ns0:Type' 'CONSIGNEE_CCC'
									'ns0:Value' currentParty.NAD?.C082_02.E3039_01
								}
								break
							case 'FW':
								'ns0:BizKey' {
									'ns0:Type' 'FORWARDER_CCC'
									'ns0:Value' currentParty.NAD?.C082_02.E3039_01
								}
								break
							case 'CZ':
								'ns0:BizKey' {
									'ns0:Type' 'SHIPPER_CCC'
									'ns0:Value' currentParty.NAD?.C082_02.E3039_01
								}
								break
							case 'N1':
								'ns0:BizKey' {
									'ns0:Type' 'NOTIFY_PARTY_CCC'
									'ns0:Value' currentParty.NAD?.C082_02.E3039_01
								}
								break
							case 'N2':
								'ns0:BizKey' {
									'ns0:Type' '2ND_NOTIFY PARTY_CCC'
									'ns0:Value' currentParty.NAD?.C082_02.E3039_01
								}
								break
						}
					}
					String SCAC = currentTxn.Group11_NAD.NAD.find {it.E3035_01 == 'CA'}?.C082_02?.E3039_01
					'ns0:CarrierId' getCarrierID(SCAC, conn)
//					'ns0:CarrierId' getCarrierID(TP_ID, conn)

//					//check existance of POL and POD in every ocean leg
//					if (util.notEmpty(currentTxn?.Group8_TDT)) {
//						currentTxn.Group8_TDT.each { currentTDT ->
//							//POL
//							if (currentTDT.find { it?.Group9_LOC.LOC?.E3227_01 == '9' } == null) {
//								'ns1:AppErrorReport'('xmlns:ns1': 'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
//									'ns1:Status' 'E'
//									'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'
//									//max length is 20, exceed will missing error bizkey
//									'ns1:Msg' 'Missing POL'
//									'ns1:Severity' '4'
//								}
//							}
//							//POD
//							if (currentTDT.find { it?.Group9_LOC.LOC?.E3227_01 == '11' } == null) {
//								'ns1:AppErrorReport'('xmlns:ns1': 'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
//									'ns1:Status' 'E'
//									'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'
//									//max length is 20, exceed will missing error bizkey
//									'ns1:Msg' 'Missing POD'
//									'ns1:Severity' '4'
//								}
//							}
//						}
//					}
					//check if BL number exists
					LinkedList AppErrors = new LinkedList()
					if (!(util.notEmpty(billOfLading.Body[currentTxnIndex].GeneralInformation.BLNumber))) {
						AppErrors.add('Missing BL Number(BGM.C106_02.E1004_01)')

					}
					//scheam validation
					Node currentBody = nodeBillOfBading.children().get(currentTxnIndex+1)
					Node currentMsg = msgWithHeader.clone()
					currentMsg.append(currentBody)
					String validationResult = validator.xmlValidation('CS2-CAR-BLXML', XmlUtil.serialize(currentMsg))
					if (validationResult.contains('Validation Failure.')){
						AppErrors.add(validationResult)
					}

//					concat all error msg
					String errMsg = ''
					AppErrors.eachWithIndex{ stringError, errorIndex ->
						errMsg = errMsg +  ' [Error' + (errorIndex + 1) + '] : ' + stringError + '\n'
					}

					if (AppErrors.size() !=0) {
						'ns1:AppErrorReport'('xmlns:ns1': 'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
							'ns1:Status' 'E'
							'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'    //max length is 20, exceed will missing error bizkey
							'ns1:Msg' errMsg.take(1000)
							'ns1:Severity' '5'
						}
					}
				}
			}
		}
		//promte bizkey
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizKeyWriter?.toString());

//		return cleanedOutputXml
		//prepare outputxml
		def BizKey = new XmlSlurper().parseText(bizKeyWriter?.toString())

		boolean hasGoodBL = false
		BizKey.children().each { currentTransaction ->
			if (!(util.notEmpty(currentTransaction.AppErrorReport))) {
				hasGoodBL = true
			}
		}

		if (hasGoodBL) {
			List removeBodies = new LinkedList()
			def nodeOutput = new XmlParser().parseText(cleanedOutputXml)
			BizKey.children().eachWithIndex { currentTransaction, currentTransactionIndex ->
				if ((util.notEmpty(currentTransaction.AppErrorReport))) {
					removeBodies.add(nodeOutput.children().findAll {
						it.name().toString() == '{http://www.cargosmart.com/billoflading}Body'
					}.get(currentTransactionIndex))
				}
			}

			removeBodies.each { currentBody ->
				nodeOutput.remove(currentBody)
			}
			//return output
			return XmlUtil.serialize(nodeOutput)
		} else {
//			println("return null")
			return null
		}
	}

	public Map getLocationInfoFromUNLocCode(String unLocCode, Connection conn) throws Exception {
		if (conn == null)
			return null;

		cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
		Map ret = new HashMap()
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select CITY_NME, COUNTY, STATE, CNTRY_NME, CNTRY_CDE from CSS_CITY_LIST where UN_LOCN_CDE =?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, unLocCode);
			result = pre.executeQuery();

			if (result.next()) {
				ret.put('City', result.getString(1))
				ret.put('County', result.getString(2))
				ret.put('State', result.getString(3))
				ret.put('Country', result.getString(4))
				ret.put('CSCountryCode', result.getString(5).trim())
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	public Map getPortInfoFromUNLocCode(String unLocCode, Connection conn) throws Exception {
		if (conn == null)
			return null;

		cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
		Map ret = new HashMap()
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select PORT_ID,Port_Nme, Sched_Kd_Cde, SCHED_TYPE from css_port_list where Un_Locn_Cde =?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, unLocCode);
			result = pre.executeQuery();

			if (result.next()) {
				ret.put('CSPortID', result.getString(1))
				ret.put('PortName', result.getString(2))
				ret.put('SchedKDCode', result.getString(3))
				ret.put('SchedKDType', result.getString(4))
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	String getCarrierID(String scac, Connection conn){
		if (conn == null)
			return "";
		if (scac == null || scac.trim() == '')
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select c.id from b2b_ocean_carrier c where c.scac = ?";

		cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

		try {
			pre = conn.prepareStatement(sql);
		    pre.setMaxRows(util.getDBRowLimit());
		    pre.setQueryTimeout(util.getDBTimeOutInSeconds());
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

	String cleanXml(String xml){
		Node root = new XmlParser().parseText(xml)
		cleanNode(root)
		XmlUtil.serialize(root)
	}
	boolean cleanNode( Node node ) {
		node.attributes().with { a ->
			a.findAll { !it.value }.each { a.remove( it.key ) }
		}
		node.children().with { kids ->
			kids.findAll { it instanceof Node ? !cleanNode( it ) : false }
					.each { kids.remove( it ) }
		}
		node.attributes() || node.children() || node.text()
	}
}