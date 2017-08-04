package cs.b2b.mapping.scripts

import cs.b2b.core.common.xmlvalidation.ValidateXML
import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.bean.bl.FreightCharge
import cs.b2b.core.mapping.bean.bl.FreightChargeCNTR
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection

/**
 * @author JENNY
 */
/**
 * @author RENGA
 * mig initialized
 */
public class CUS_CS2BLXML_XML_EXXONELITE {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_BL_O_Common blUtil = new cs.b2b.core.mapping.util.MappingUtil_BL_O_Common(util);

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
	def yyyyMMddHHmmss = 'yyyyMMddHHmmss'

	def currentSystemDt = null

	BigDecimal sum = null
	BigDecimal hundred = new BigDecimal ('100');
	BigDecimal thousand = new BigDecimal ('1000');

	BigDecimal rateLBS = new BigDecimal('0.4536')
	BigDecimal rateTon = new BigDecimal('1000')
	BigDecimal rateCBF = new BigDecimal('0.0283')

	List<String> ventilationUnitList = ['degrees', 'cbfPerMin', 'cbmPerHour']
	List<String> sealTypeList = ['CA', 'CN', 'CU', 'FW', 'HS', 'MC', 'NP', 'QS', 'RR', 'SH', 'TM', 'VS', 'ZZ']
	Map<String, String> cargoTypeMap = ['AD':'Awkward Dangerous','AW':'Awkward','DG':'Dangerous','GC':'General','RD':'Reefer Dangerous','RF':'Reefer']

	public void generateBody(Body current_Body, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {

		outXml.'Header' {
			'MessageType'('MessageVersion': '13', 'BL')
			'DocumentIdentifier' '1'
			'DateTime'('DateType': 'Document', currentSystemDt.format("yyyyMMddHHmmss"))
			'Parties' {
				'PartnerInformation'('PartnerRole': 'Sender') {
					'PartnerIdentifier'('Agency': 'AssignedBySender', 'CargoSmart')
//					'PartnerName' ' '
//					'ContactInformation' {
//						'ContactName' ''
//						'CommunicationValue' ''
//					}
//					'AddressInformation' {
//						'AddressLine' ''
//					}
//					'ChargeDetails' {
//						'ChargeCategory' ''
//						'ChargeInvoice' ''
//						'ChargeLocation' {
//							'LocationCode' ''
//							'LocationName' ''
//							'LocationState' ''
//							'LocationCountry' ''
//							'LocationCountryCode' ''
//						}
//						'ChargeAmount' ''
//					}
				}
				'PartnerInformation'('PartnerRole': 'Recipient') {
					'PartnerIdentifier'('Agency': 'AssignedByRecipient', (String)TP_ID)
					'PartnerName' 'ELITE INTERNATIONAL LOGISTICS (S) P'
					'ContactInformation' {
						'ContactName'('ContactType': 'Informational', 'XOM BL')
						'CommunicationValue'('CommunicationType ': 'Telephone', '(65) 6471 7588 ')
						'CommunicationValue'('CommunicationType ': 'Fax', '(65) 6473 8991 ')
						if (current_Body?.ExternalReference[0]?.CSReferenceType) {
							'CommunicationValue'('CommunicationType ': 'Email', current_Body?.ExternalReference[0]?.CSReferenceType)
						}

					}
//					'AddressInformation' {
//						'AddressLine' ''
//					}
//					'ChargeDetails' {
//						'ChargeCategory' ''
//						'ChargeInvoice' ''
//						'ChargeLocation' {
//							'LocationCode' ''
//							'LocationName' ''
//							'LocationState' ''
//							'LocationCountry' ''
//							'LocationCountryCode' ''
//						}
//						'ChargeAmount' ''
//					}
				}
			}
		}
		outXml.'MessageBody' {
			'MessageProperties' {
				'ShipmentID' {
					'ShipmentIdentifier'('MessageStatus': 'Draft','MessageIdentifier': 'BillOfLading',  current_Body?.GeneralInformation?.BLNumber)
					'DocumentVersion' '1'
				}
				'DateTime'('DateType': 'Message', util.convertXmlDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT, "yyyyMMddhhmm"))
//				'ShipmentDeclaredValue' ''
//				'LetterOfCreditDetails' {
//					'LetterOfCreditNumber' ''
//					'DateTime' ''
//					'LCIssueDateFreeText' ''
//					'LCExpiryDateFreeText' ''
//				}
//				'ExportLicenseDetails' {
//					'ExportLicenseNumber' ''
//					'DateTime' ''
//				}
//				'BlLocations' {
//					'Location' {
//						'LocationCode' ''
//						'LocationName' ''
//						'LocationState' ''
//						'LocationCountry' ''
//						'LocationCountryCode' ''
//						'DateTime' ''
//					}
//				}
				current_Body?.CarrierRate?.each { current_carrierRate ->
					if (current_carrierRate?.CSCarrierRateType?.equals('SC')) {
						'ReferenceInformation'('ReferenceType': 'ContractNumber', current_carrierRate?.CarrierRateNumber)
					}
				}
				Map<String, String> referenceDescriptionMap = ['B/L Reference Number'                            : 'BillOfLadingNumber',
															   'Booking Number'                                  : 'BookingNumber',
															   'Consignee\'s Reference'                          : 'ConsigneeOrderNumber',
															   'Contract Number'                                 : 'ContractNumber',
															   'Forwarder Reference'                             : 'FreightForwarderReference',
															   'House B/L'                                       : 'HouseBill',
															   'Invoice Number'                                  : 'InvoiceNumber',
															   'Purchase Order Number'                           : 'PurchaseOrderNumber',
															   'Service Contract Number'                         : 'ContractNumber',
															   'Shipping Instruction Reference'                  : 'ShipmentID',
															   'Shippers Identity For Shipment (SID)'            : 'ShipperIdentifyingNumber',
															   'Shipper\'s Identifying Number for Shipment (SID)': 'CustomerShipmentIdentifier']
				current_Body?.ExternalReference?.each { current_externalReference ->
					if (referenceDescriptionMap.get(current_externalReference?.ReferenceDescription))
						'ReferenceInformation'('ReferenceType': referenceDescriptionMap.get(current_externalReference?.ReferenceDescription), current_externalReference?.ReferenceNumber)
				}
				if(current_Body?.Remarks || current_Body?.Cargo[0]?.Packaging?.PackageDesc && false){
					'Instructions' {
						'ShipmentComments' ('CommentType':'General',current_Body?.Remarks)
						'ShipmentComments' ('CommentType':'BlClause',current_Body?.Cargo[0]?.Packaging?.PackageDesc)
					}
				}

				def packageQtySum = 0;
				current_Body?.Cargo?.Packaging?.each { current_packging ->
					if(current_packging?.PackageQty)
						packageQtySum += current_packging?.PackageQty?.toInteger()
				}
				'ControlTotal' {
					'NumberOfEquipment' current_Body?.Container?.size()
					'NumberOfPackages' packageQtySum
					'GrossWeight' ''
					'GrossVolume' ''
				}
				def movementType;
				if (current_Body?.Route?.Haulage?.InBound == 'C' && current_Body?.Route?.Haulage?.OutBound == 'C') {
					movementType = 'DoorToDoor'
				} else if (current_Body?.Route?.Haulage?.InBound == 'M' && current_Body?.Route?.Haulage?.OutBound == 'C') {
					movementType = 'DoorToPort'
				} else if (current_Body?.Route?.Haulage?.InBound == 'C' && current_Body?.Route?.Haulage?.OutBound == 'M') {
					movementType = 'PortToDoor'
				} else {
					movementType = 'PortToPort'
				}

				'HaulageDetails'('MovementType': movementType, 'ServiceType': current_Body?.Container?.size() > 0 ? 'FullLoad' : 'LessThanFullLoad')

				if (movementType == 'DoorToDoor' || movementType == 'DoorToPort') {
					'TransportationDetails'('TransportMode': 'Maritime', 'TransportStage': 'PreCarriage') {
						'ConveyanceInformation' {
							'ConveyanceName' ''//trim()
							'VoyageTripNumber' util.substring(current_Body?.Route?.POR?.Facility?.FacilityName,1,17)
//							'CarrierSCAC' ''
//							'TransportIdentification' ''
//							'TransportMeans' ''
//							'TransportNationality' ''
						}
						'Location'('LocationType': 'PlaceOfReceipt') {
							if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode)
								'LocationCode'('nullAttr':'Agency','Agency': current_Body?.Route?.POR?.CityDetails?.LocationCode?.MutuallyDefinedCode, current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode)
							'LocationName' current_Body?.Route?.POR?.CityDetails?.City
							'LocationState' current_Body?.Route?.POR?.CityDetails?.State
							'LocationCountry' current_Body?.Route?.POR?.CityDetails?.Country
//							'LocationCountryCode' ''
//							'DateTime' ''
						}
						'Location'('LocationType': 'PlaceOfDelivery') {
							if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode)
								'LocationCode'('nullAttr':'Agency','Agency': current_Body?.Route?.FirstPOL?.Port?.LocationCode?.MutuallyDefinedCode, current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode)
							'LocationName' current_Body?.Route?.FirstPOL?.Port?.City
							'LocationState' current_Body?.Route?.FirstPOL?.Port?.State
							'LocationCountry' current_Body?.Route?.FirstPOL?.Port?.Country
//							'LocationCountryCode' ''
//							'DateTime' ''
						}
					}
				}

				Map<String, String> voyagaTripNumberMap = ['E': 'East', 'W': 'West', 'N': 'North']
				'TransportationDetails'('TransportMode': 'Maritime', 'TransportStage': 'Main') {
					'ConveyanceInformation' {
						'ConveyanceName' current_Body?.Route?.OceanLeg[0]?.SVVD?.Discharge?.VesselName//?:
						'VoyageTripNumber' "${current_Body?.Route?.OceanLeg[0]?.SVVD?.Discharge?.Voyage ?:''}${voyagaTripNumberMap.get(current_Body?.Route?.OceanLeg[0]?.SVVD?.Discharge?.Direction)?:'South'}"
						'CarrierSCAC' current_Body?.GeneralInformation?.SCACCode
						if (current_Body?.Route?.OceanLeg[0]?.SVVD?.Discharge?.LloydsNumber)
							'TransportIdentification'('TransportIdentificationType': 'LloydsCode', current_Body?.Route?.OceanLeg[0]?.SVVD?.Discharge?.LloydsNumber)
						'TransportMeans'('TransportMeansType': 'OceanVessel', 'OCEAN VESSEL')
//						'TransportNationality' ''
					}
					'Location'('LocationType': 'PortOfLoading') {
						'LocationCode'('nullAttr':'Agency','Agency': current_Body?.Route?.OceanLeg[0]?.POL?.Port?.LocationCode?.MutuallyDefinedCode?:"", current_Body?.Route?.OceanLeg[0]?.POL?.Port?.LocationCode?.UNLocationCode)
						'LocationName' current_Body?.Route?.OceanLeg[0]?.POL?.Port?.City?:''
						'LocationState' current_Body?.Route?.OceanLeg[0]?.POL?.Port?.State?:''
						'LocationCountry' current_Body?.Route?.OceanLeg[0]?.POL?.Port?.Country?:''
//						'LocationCountryCode' ''
//						'DateTime' ''
					}
					'Location'('LocationType': 'PortOfDischarge') {
						'LocationCode'('nullAttr':'Agency','Agency': current_Body?.Route?.OceanLeg[-1]?.POD?.Port?.LocationCode?.MutuallyDefinedCode?:'', current_Body?.Route?.OceanLeg[-1]?.POD?.Port?.LocationCode?.UNLocationCode)
						'LocationName' current_Body?.Route?.OceanLeg[-1]?.POD?.Port?.City
						'LocationState' current_Body?.Route?.OceanLeg[-1]?.POD?.Port?.State
						'LocationCountry' current_Body?.Route?.OceanLeg[-1]?.POD?.Port?.Country
//						'LocationCountryCode' ''
//						'DateTime' ''
					}
				}

				if (movementType == 'DoorToDoor' || movementType == 'PortToDoor') {
					'TransportationDetails'('TransportMode': 'Maritime', 'TransportStage': 'OnCarriage') {
						'ConveyanceInformation' {
//							'ConveyanceName' ''
							//delete .trim()
							'VoyageTripNumber' util.substring(current_Body?.Route?.FND?.Facility?.FacilityName,1,17)
//							'CarrierSCAC' ''
//							'TransportIdentification' ''
//							'TransportMeans' ''
//							'TransportNationality' ''
						}
						'Location'('LocationType': 'PlaceOfReceipt') {
							if(current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode)
								'LocationCode'('nullAttr':'Agency','Agency': current_Body?.Route?.LastPOD?.Port?.LocationCode?.MutuallyDefinedCode, current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode)
							'LocationName' current_Body?.Route?.LastPOD?.Port?.City
							'LocationState' current_Body?.Route?.LastPOD?.Port?.State
							'LocationCountry' current_Body?.Route?.LastPOD?.Port?.Country
//							'LocationCountryCode' ''
//							'DateTime' ''
						}
						'Location'('LocationType': 'PlaceOfDelivery') {
							if(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode)
								'LocationCode'('nullAttr':'Agency','Agency': current_Body?.Route?.FND?.CityDetails?.LocationCode?.MutuallyDefinedCode, current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode)
							'LocationName' current_Body?.Route?.FND?.CityDetails?.City
							'LocationState' current_Body?.Route?.FND?.CityDetails?.State
							'LocationCountry' current_Body?.Route?.FND?.CityDetails?.Country
//							'LocationCountryCode' ''
//							'DateTime' ''
						}
					}
				}

				'Parties' {
					Map<String, String> partnerRoleMap = ['ANP': 'NotifyParty', 'SHP': 'Shipper', 'FWD': 'FreightForwarder', 'CGN': 'Consignee', 'NPT': 'NotifyParty']
					//def partnerRole
					current_Body?.Party?.each { current_party ->
						if (partnerRoleMap.get(current_party?.PartyType)) {
							//println "parties"
//								if(current_party?.PartyType=='ANP'){
//									partnerRole='NotifyParty'
//								}else if(current_party?.PartyType=='SHP'){
//									partnerRole='Shipper'
//								}else if(current_party?.PartyType=='FWD'){
//									partnerRole='FreightForwarder'
//								}else if(current_party?.PartyType=='CGN'){
//									partnerRole='Consignee'
//								}else if(current_party?.PartyType=='NPT'){
//									partnerRole='NotifyParty'
//								}else if(current_party?.PartyType=='CCP'){
//									partnerRole='NotifyParty'
//									break
//								}else{
//									partnerRole='ContractParty'
//									break
//								}
							'PartnerInformation'('PartnerRole': partnerRoleMap.get(current_party?.PartyType)) {
								if(current_party?.CarrierCustomerCode)
									'PartnerIdentifier'('Agency': 'AssignedBySender', current_party?.CarrierCustomerCode)
								'PartnerName' util.substring(current_party?.PartyName, 1, 35)
								'PartnerName' util.substring(current_party?.PartyName, 36, 35)
								if ("${current_party?.Contact?.FirstName ?: ''}${current_party?.Contact?.LastName ?: ''}" && (current_party?.Contact?.ContactPhone?.Number || current_party?.Contact?.ContactEmailAddress)) {
									if (current_party?.Contact?.ContactPhone?.Number) {
										'ContactInformation' {
											'ContactName'('ContactType': 'Informational', "${current_party?.Contact?.FirstName ?:''}${current_party?.Contact?.LastName?:''}")
											'CommunicationValue'('CommunicationType': 'Telephone', "${current_party?.Contact?.ContactPhone?.CountryCode?:''}-${current_party?.Contact?.ContactPhone?.AreaCode?:''}-${current_party?.Contact?.ContactPhone?.Number}")
										}
									}
									if (current_party?.Contact?.ContactEmailAddress) {
										'ContactInformation' {
											'ContactName'('ContactType': 'Informational', "${current_party?.Contact?.FirstName ?: ''}${current_party?.Contact?.LastName ?: ''}")
											'CommunicationValue'('CommunicationType': 'Email', current_party?.Contact?.ContactEmailAddress)
										}
									}
								}
								if (current_party?.Address) {
									'AddressInformation' {
										if(current_party?.Address?.AddressLines){
											'AddressLine' util.substring("${current_party?.Address?.AddressLines?.AddressLine[0] ?: ''}${current_party?.Address?.AddressLines?.AddressLine[1] ?: ''}${current_party?.Address?.AddressLines?.AddressLine[2] ?: ''}", 1, 35)
											'AddressLine' util.substring("${current_party?.Address?.AddressLines?.AddressLine[0] ?: ''}${current_party?.Address?.AddressLines?.AddressLine[1] ?: ''}${current_party?.Address?.AddressLines?.AddressLine[2] ?: ''}", 36, 35)
											'AddressLine' util.substring("${current_party?.Address?.AddressLines?.AddressLine[0] ?: ''}${current_party?.Address?.AddressLines?.AddressLine[1] ?: ''}${current_party?.Address?.AddressLines?.AddressLine[2] ?: ''}", 71, 35)
											'AddressLine' util.substring("${current_party?.Address?.AddressLines?.AddressLine[0] ?: ''}${current_party?.Address?.AddressLines?.AddressLine[1] ?: ''}${current_party?.Address?.AddressLines?.AddressLine[2] ?: ''}", 106, 35)
										}
										 //'AddressLine' util.substring(current_party?.Address?.AddressLines?.AddressLine[0]?:''+(current_party?.Address?.AddressLines?.AddressLine[1]?:'')+(current_party?.Address?.AddressLines?.AddressLine[2]?:''),1,35)?:''
										}
								}
								if (current_party?.PartyType == 'SHP') {
									Map<String, String> chargeTypeMap = ['BAF': 'BasicFreight', 'DHC': 'DestinationHaulageCharges', 'DPC': 'DestinationPortCharges', 'OHC': 'OriginHaulageCharges', 'OPC': 'OriginPortCharges']

									current_Body?.FreightCharge?.each{current_FreightCharge->
										if (current_FreightCharge?.PayByInformation?.CarrierCustomerCode == current_party?.CarrierCustomerCode) {
											'ChargeDetails' {
												'ChargeCategory'('PrepaidorCollectIndicator': current_FreightCharge?.ChargeType=='1'?'Prepaid':'Collect', 'ChargeType': chargeTypeMap.get(current_FreightCharge?.ChargeCode) ?: 'Other', current_FreightCharge?.ChargeCode)
//												'ChargeInvoice' ''
//												'ChargeLocation' {
//													'LocationCode' ''
//													'LocationName' ''
//													'LocationState' ''
//													'LocationCountry' ''
//													'LocationCountryCode' ''
//												}
												if (current_FreightCharge?.ChargeAmount && current_FreightCharge?.ChargeAmount != "0")
													'ChargeAmount'('Currency': current_FreightCharge?.ChargeAmount?.attr_Currency, current_FreightCharge?.ChargeAmount)
											}
										}
									}
								}
							}
						}
					}
				}
			}
			'MessageDetails' {
				current_Body?.Container?.each { current_Container ->
					'EquipmentDetails' {
						'LineNumber' current_Container?.DisplaySequenceNumber
						'EquipmentIdentifier'('EquipmentSupplier': 'Carrier', current_Container?.ContainerNumber)
						'EquipmentType' {
							if (current_Container?.CS1ContainerSizeType) {
								'EquipmentTypeCode'('NonActiveReefer': 'Y', current_Container?.CS1ContainerSizeType)
							}
//							'EquipmentDescription' ''
						}
						'EquipmentGrossWeight'('UOM': 'KGM', blUtil.replaceZeroAfterPoint(current_Container?.GrossWeight?.Weight))
//						'EquipmentGrossVolume' ''
//						'EquipmentTemperature' ''
//						'EquipmentNonActive' ''
//						'EquipmentAirFlow' ''
						if(current_Container?.BLSeal?.SealNumber)
							'EquipmentSeal'('SealingParty': 'Carrier', current_Container?.BLSeal?.SealNumber)
//						'EquipmentComments' ''
//						'EquipmentReferenceInformation' ''
//						'EquipmentHazardousGoods' {
//							'IMOClassCode' ''
//							'IMDGPageNumber' ''
//							'UNDGNumber' ''
//							'FlashpointTemperature' ''
//							'EMSNumber' ''
//							'HazardousGoodsComments' ''
//							'EmergencyResponseContact' {
//								'ContactInformation' {
//									'ContactName' ''
//									'CommunicationValue' ''
//								}
//							}
//						}
					}
				}
				current_Body?.Cargo?.eachWithIndex { current_Cargo, current_Index ->
					'GoodsDetails' {
						'LineNumber' current_Index + 1
						'PackageDetail'('Level': 'Outer') {
							'NumberOfPackages' current_Cargo?.Packaging?.PackageQty
							'PackageTypeCode' current_Cargo?.Packaging?.PackageType
							//println current_Cargo?.Packaging?.PackageDesc?.indexOf("Instruction")
							//EDI2017010419304837-16.in
							'PackageTypeDescription' util.substring(current_Cargo?.Packaging?.PackageDesc ==null||current_Cargo?.Packaging?.PackageDesc?.indexOf("Instruction")==-1 ? null : current_Cargo?.Packaging?.PackageDesc?.split("Instruction")[0], 1, 35)
							//'PackageTypeDescription' util.substring(subStringBefore(current_Cargo?.Packaging?.PackageDesc, "Instruction"), 1, 35)
						}
						'PackageDetailComments'('CommentType': 'GoodsDescription', current_Cargo?.CargoDescription)
						'ProductId' ''
						if(current_Cargo?.Volume?.Volume)
							'PackageDetailGrossVolume'('UOM': 'FTQ', blUtil.replaceZeroAfterPoint(current_Cargo?.Volume?.Volume))
						if(current_Cargo?.GrossWeight?.Weight)
							'PackageDetailGrossWeight'('UOM': 'KGM', blUtil.replaceZeroAfterPoint(current_Cargo?.GrossWeight?.Weight))
//						'DetailsReferenceInformation' ''
//						'ExportLicenseDetails' {
//							'ExportLicenseNumber' ''
//							'DateTime' ''
//						}
//						'PackageMarks' {
//							'Marks' ''
//						}
//						'SplitGoodsDetails' {
//							'EquipmentIdentifier' ''
//							'SplitGoodsNumberOfPackages' ''
//							'SplitGoodsGrossVolume' ''
//							'SplitGoodsGrossWeight' ''
//						}
						if (current_Cargo?.DGCargoSpec) {
							'HazardousGoods' {
								'IMOClassCode' current_Cargo?.DGCargoSpec[0]?.IMOClass
								'IMDGPageNumber' ''
								'UNDGNumber' current_Cargo?.DGCargoSpec[0]?.UNNumber
								if(current_Cargo?.DGCargoSpec[0]?.FlashPoint?.Temperature)//new BigDecimal("100.000").stripTrailingZeros().toPlainString()  current_Cargo?.DGCargoSpec[0]?.FlashPoint?.Temperature
									'FlashpointTemperature'('UOM': current_Cargo?.DGCargoSpec[0]?.FlashPoint?.TemperatureUnit == "C" ? 'CEL' : 'FAH', new BigDecimal(current_Cargo?.DGCargoSpec[0]?.FlashPoint?.Temperature).toPlainString())
								'EMSNumber' util.substring(current_Cargo?.DGCargoSpec[0].EMSNumber, 1, 6)
								if (current_Cargo?.DGCargoSpec[0]?.Remarks)
									'HazardousGoodsComments'('CommentType': 'General', current_Cargo?.DGCargoSpec[0]?.Remarks)
								'EmergencyResponseContact' {
									'ContactInformation'() {
										'ContactName'('ContactType': current_Cargo?.DGCargoSpec[0]?.EmergencyContact[0]?.Type ? 'Informational' : 'Emergency', "${current_Cargo?.DGCargoSpec[0]?.EmergencyContact[0]?.FirstName ?: ''}${current_Cargo?.DGCargoSpec[0]?.EmergencyContact[0]?.LastName ?: ''}")
										'CommunicationValue'('CommunicationType': 'Telephone', current_Cargo?.DGCargoSpec[0]?.EmergencyContact[0]?.ContactPhone?.Number)
									}
								}
							}
						}

					}
				}

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
		BillOfLading bl = parser.xmlParser(inputXmlBody, BillOfLading.class)


		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		//try to change true to False
//		outXml.omitEmptyAttributes = true
//		outXml.omitNullAttributes = true

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
		def blXml = outXml.createNode('Message')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.

		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()
		def txnErrorKeys = []

		//Start mapping

		bl?.Body?.eachWithIndex{current_Body, current_BodyIndex ->

			List<FreightCharge> filteredFreightCharge = current_Body.FreightCharge?.clone()
			List<FreightChargeCNTR> filteredFreightChargeCNTR = current_Body.FreightChargeCNTR?.clone()
			//OceanLegReorder
//			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)

			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)
			
			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			//mapping
			if(errorKeyList.isEmpty()){
				generateBody(current_Body,  associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml)
			}
			//posp checking
			if(errorKeyList.isEmpty()){
				println "aa"
				pospValidation(writer?.toString(), errorKeyList)
			}

			blUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%18s', current_Body?.TransactionInformation?.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			blUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, bl.Header?.MsgDT?.GMT, bl.Header.InterchangeMessageID, TP_ID, conn)

			txnErrorKeys.add(errorKeyList)
		}

		//End root node
		outXml.nodeCompleted(null,blXml)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		blUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		blUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		blUtil.promoteHeaderIntChgMsgId(appSessionId, bl?.Body[0]);
		blUtil.promoteScacCode(appSessionId, bl?.Body[0]);

		String result = '';

		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			result = util.cleanXml(writer?.toString(), true)
			//result = util.cleanXml(writer?.toString(), true,true)
//			result = writer?.toString()
			//promote XML output file name to transportation, xml format need this function
			//please change the file name pattern base on B2B_EDI_FILENAME setting
//			long fileSeqNumber = util.getFileSequenceNumber(TP_ID, 'BC', 'BCXML', 'BKGSUPUIFO', conn)
//			String fileSeqStr = '01'
//			if (fileSeqNumber>=0) {
//				fileSeqStr = fileSeqNumber + ""
//				if (fileSeqStr.length()>2) {
//					fileSeqStr = fileSeqStr.substring(fileSeqStr.length()-2)
//				} else if (fileSeqStr.length()==1) {
//					fileSeqStr = "0" + fileSeqStr
//				}
//			}
			//output file name
			String outputFileName = "EXXONELITE_BL_" + currentSystemDt.format(yyyyMMddHHmmss) + ".xml"
			blUtil.promoteOutputFileNameToSession(appSessionId, outputFileName)

			//posp handle
			result = result.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\"?>")
		}

		writer.close();

		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){

		List<Map<String,String>> errorList = []

//		blUtil.checkBLStatus(current_Body, ['Preparation In Progress'], false, null, errorList)
//		blUtil.checkCarrierBookingNumber(current_Body, true, null, errorList)
//		blUtil.checkCertificationClauseTextLength(current_Body, 280, true, null, errorList)
//		blUtil.checkContainerTrafficModeInboundLength(current_Body, 3, true, null, errorList)
//		blUtil.checkContainerTrafficModeOutboundLength(current_Body, 3, true, null, errorList)
//		blUtil.checkDangerousCargoIMDGPageLength(current_Body, 5, true, null, errorList)
//		blUtil.checkDangerousCargoIMOClassLength(current_Body, 5, true, null, errorList)
//		blUtil.checkOceanLegDischargeVesselNameLength(current_Body, 30, true, null, errorList)
//		blUtil.checkOceanLegLoadingVesselNameLength(current_Body, 30, true, null, errorList)
//		blUtil.checkReeferCargoSensitiveCargoDescLength(current_Body, 20, true, null, errorList)
//		blUtil.checkStopOffPickupDetailsFacilityNameLength(current_Body, 35, true, null, errorList)
//		blUtil.checkStopOffReturnDetailsFacilityNameLength(current_Body, 35, true, null, errorList)
		//false is O ,true is E
//		CheckAmendent(current_Body, false, null, errorList)
//		CheckContainerNumber(current_Body, true, null, errorList)
//		CheckContainerType(current_Body, true, null, errorList)
		return errorList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){

		cs.b2b.core.common.xmlvalidation.ValidateXML validator = new ValidateXML()

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</Message>")
		util.cleanNode(node, true)
		//util.cleanNode(node, true,true)
		String validationResult = validator.xmlValidation('CUS-BLXML-EXXONELITE', XmlUtil.serialize(node))
		if (validationResult.contains('Validation Failure.')){
			errorKeyList.add([TYPE: 'ES', IS_ERROR: 'YES', VALUE: validationResult])
		}
	}

//	String subStringBefore(String str, String separator){
//		if((str) && separator != null) {
//			if(separator.length() == 0) {
//				return "";
//			} else {
//				int pos = str.indexOf(separator);
//				return pos == -1?null:str.substring(0, pos);
//			}
//		} else {
//			return str;
//		}
//	}
	void CheckAmendent(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(current_Body?.TransactionInformation?.Action!="NEW"){
			errorKey = [TYPE: isError? blUtil.ERROR_SUPPORT : blUtil.ERROR_COMPLETE, IS_ERROR: isError? blUtil.YES : blUtil.NO, VALUE: errorMsg ? errorMsg : "Amendent NOT accepted"]
			errorKeyList.add(errorKey)
		}
	}

	void CheckContainerNumber(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		current_Body?.Container?.each {current_Container ->
			if(current_Container?.ContainerNumber?.trim()=="" || current_Container?.ContainerNumber==null){
				errorKey = [TYPE: isError? blUtil.ERROR_SUPPORT : blUtil.ERROR_COMPLETE, IS_ERROR: isError? blUtil.YES : blUtil.NO, VALUE: errorMsg ? errorMsg : "Missing Container Number"]
				errorKeyList.add(errorKey)
			}

		}

	}
	void CheckContainerType(cs.b2b.core.mapping.bean.bl.Body current_Body, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		current_Body?.Container?.each {current_Container ->
			if(current_Container?.CSContainerSizeType?.trim()=="" || current_Container?.CSContainerSizeType==null){
				errorKey = [TYPE: isError? blUtil.ERROR_SUPPORT : blUtil.ERROR_COMPLETE, IS_ERROR: isError? blUtil.YES : blUtil.NO, VALUE: errorMsg ? errorMsg : "Missing Container Size Type"]
				errorKeyList.add(errorKey)
			}

		}

	}
}


