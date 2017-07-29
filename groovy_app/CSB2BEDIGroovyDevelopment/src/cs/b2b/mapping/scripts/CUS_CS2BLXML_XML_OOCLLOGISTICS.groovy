package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.common.xmlvalidation.ValidateXML
import cs.b2b.core.mapping.bean.bl.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection

/**
 * @author RENGA
 * @change CSEDI6636 http://i2isd/sites/csisa/Lists/Workplan/DispForm.aspx?ID=29864
 */
public class CUS_CS2BLXML_XML_OOCLLOGISTICS {

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

	List<String> volumeUnitList = ['CBM','CBF']
	List<String> ventilationUnitList = ['degrees', 'cbfPerMin', 'cbmPerHour']
	List<String> sealTypeList = ['CA', 'CN', 'CU', 'FW', 'HS', 'MC', 'NP', 'QS', 'RR', 'SH', 'TM', 'VS', 'ZZ']

	public void generateBody(Body current_Body, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {
//		outXml.'InterchangeControlHeader'{
//			'ControlNumber' ''
//			'SenderId' ''
//			'ReceiverId' ''
//			'DateTime' ''
//			'ControlVersion' ''
//			'UsageIndicator' ''
//			'MessageSessionId' ''
//		}
		outXml.'BLBillOfLading'{
			'GeneralInfo'{
				'TransactionInfo'{
					'BatchNumber' util.getXMLControlNumber(TP_ID,'BL','XML','BLUPDATEO',conn)
					'MessageSender' 'CARGOSMART'
					'MessageRecipient' TP_ID
					'MessageID' 'BLXML'
					'DateCreated' ('TimeZone':'HKT', currentSystemDt.format("yyyyMMddHHmmss"))
					'FileName' ''
					'DataSource' ''
					'Version' '10.0'
				}
				Map<String, String> actionTypeMap = ['NEW':'ADD']
				'ActionType' actionTypeMap.get(current_Body?.TransactionInformation?.Action) ? actionTypeMap.get(current_Body?.TransactionInformation?.Action) : current_Body?.TransactionInformation?.Action
				'BLNumber' current_Body?.GeneralInformation?.BLNumber
				'SCAC' current_Body?.GeneralInformation?.SCACCode

				if(filteredFreightCharge?.size() != 0 && filteredFreightCharge?.findAll{it.ChargeType == blUtil.COLLECT}?.size() == filteredFreightCharge?.size()){
					'PaymentStatus' 'COLLECT'
				}else if(filteredFreightCharge?.size() != 0 && filteredFreightCharge?.findAll{it.ChargeType == blUtil.PREPAID}?.size() == filteredFreightCharge?.size()){
					'PaymentStatus' 'PREPAID'
				}else{
					'PaymentStatus' 'MIXED'
				}
			}
			'BLDetails'{
				'BookingInfo'{
					current_Body?.GeneralInformation?.CarrierBookingNumber?.each {current_BookingNumber ->
						'BookingNumber' util.substring(current_BookingNumber, 1, 35)
					}
				}
				Map<String, String> refTypeMap = ['BKG':'BN', 'BL':'BM', 'CR':'CR', 'CTR':'CT', 'EXPR':'RF',
												  'FID':'FI', 'FR':'FN', 'INV':'IK','PO':'PO',  'SID':'SI']
				'UserReferences'{

					current_Body?.GeneralInformation?.CarrierBookingNumber?.each {current_BookingNumber ->
						'References'{
							'ReferenceType' 'BN'
							'ReferenceNumber' util.substring(current_BookingNumber, 1, 35)
							'ReferenceDescription' 'BOOKING NUMBER'
						}
					}

					if(current_Body?.GeneralInformation?.BLNumber){
						'References'{
							'ReferenceType' 'BM'
							'ReferenceNumber' util.substring(current_Body?.GeneralInformation?.BLNumber, 1, 35)
							'ReferenceDescription' 'BILL OF LADING NUMBER'
						}
					}

					current_Body?.ExternalReference?.each {current_ExternalReference ->
						if(refTypeMap.get(current_ExternalReference?.CSReferenceType) && current_ExternalReference?.ReferenceNumber){
							'References'{
								'ReferenceType' refTypeMap.get(current_ExternalReference?.CSReferenceType)
								'ReferenceNumber' util.substring(current_ExternalReference?.ReferenceNumber, 1, 35)
								if(current_ExternalReference?.ReferenceDescription){
									'ReferenceDescription' util.substring(current_ExternalReference?.ReferenceDescription, 1, 60)
								}
							}
						}
					}
				}
				'LegalParties'{
					Map<String,String> partyTypeMap =  ['SHP':'SH', 'CGN':'CN', 'BRK':'BR', 'NPT':'NP', 'FWD':'FW']
					current_Body?.Party?.each {current_Party ->
						if(partyTypeMap.get(current_Party?.PartyType)) {
							'Party' {
								'PartyType' partyTypeMap.get(current_Party?.PartyType)
								'PartyName' util.substring(current_Party?.PartyName,1,70)
								'CarrierCustomerCode' util.substring(current_Party?.CarrierCustomerCode, 1, 14)
								'PartyLocation' {
									if(current_Party?.Address?.AddressLines?.AddressLine?.size() > 0){
										'Address' {
											current_Party?.Address?.AddressLines?.AddressLine?.each {current_AddressLine ->
												'AddressLines' util.substring(current_AddressLine, 1, 240)
											}
										}
									}
									'Street' ''
									'City' current_Party?.Address?.City
									'County' current_Party?.Address?.County
									'StateProvinceCode' ''
									'StateProvince' current_Party?.Address?.State
									'CountryCode' util.substring(current_Party?.Address?.Country, 1, 3)
									'CountryName' util.getCS2MasterCity4CountryCodeByCountryName(current_Party?.Address?.Country, conn)
									'LocationCode' {
										'MutuallyDefinedCode' ''
										'UNLocationCode' current_Party?.Address?.LocationCode?.UNLocationCode
										if(current_Party?.Address?.LocationCode?.SchedKDCode){
											'SchedKDCode' ('Type':(current_Party?.Address?.LocationCode?.SchedKDType == 'K'? 'K':'D'), current_Party?.Address?.LocationCode?.SchedKDCode)
										}
									}
									'PostalCode' util.substring(current_Party?.Address?.PostalCode, 1, 10)
								}
								'ContactPerson' {
									'FirstName' current_Party?.Contact?.FirstName
									'LastName' current_Party?.Contact?.LastName
									'Phone' {
										'CountryCode' util.substring(current_Party?.Contact?.ContactPhone?.CountryCode, 1, 3)
										'AreaCode' current_Party?.Contact?.ContactPhone?.AreaCode
										'Number' current_Party?.Contact?.ContactPhone?.Number
									}
									'Fax' {
										'CountryCode' util.substring(current_Party?.Contact?.ContactFax?.CountryCode, 1, 3)
										'AreaCode' current_Party?.Contact?.ContactFax?.AreaCode
										'Number' current_Party?.Contact?.ContactFax?.Number
									}
									'Email' current_Party?.Contact?.ContactEmailAddress
								}
							}
						}
					}
				}
				'RouteInformation'{
					'FullReturnCutoff' util.convertXmlDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, 'yyyyMMddHHmmss')
					if(current_Body?.Route?.ArrivalAtFinalHub){
						'ArrivalAtFinalHub' util.convertXmlDateTime(current_Body?.Route?.ArrivalAtFinalHub[0]?.LocDT, 'yyyyMMddHHmmss')
					}
					'Haulage'{
						'Outbound' current_Body?.Route?.Haulage?.OutBound
						'Inbound' current_Body?.Route?.Haulage?.InBound
					}

					POR por = current_Body?.Route?.POR
					FND fnd = current_Body?.Route?.FND
					FirstPOL pol = current_Body?.Route?.FirstPOL
					LastPOD pod = current_Body?.Route?.LastPOD

					if(por) {
						'Location' {
							'FunctionCode' 'POR'
							'FacilityCode' por?.Facility?.FacilityCode
							'FacilityName' util.substring(por?.Facility?.FacilityName, 1 , 35)
//							'LocationName' ''
							'LocationDetails' {
//								'Address' {
//									'AddressLines' ''
//								}
//								'Street' ''
								'City' por?.CityDetails?.City
								'County' por?.CityDetails?.County
//								'StateProvinceCode' ''
								'StateProvince' por?.CityDetails?.State
								'CountryCode' util.substring(por?.CSStandardCity?.CSCountryCode, 1, 3)
								'CountryName' por?.CityDetails?.Country
								'LocationCode' {
//									'MutuallyDefinedCode' ''
									'UNLocationCode' por?.CityDetails?.LocationCode?.UNLocationCode
									if(por?.CityDetails?.LocationCode?.SchedKDCode){
										'SchedKDCode' ('Type':(por?.CityDetails?.LocationCode?.SchedKDType == 'K'? 'K':'D'), por?.CityDetails?.LocationCode?.SchedKDCode)
									}
								}
//								'PostalCode' ''
							}
						}
					}

					if(fnd) {
						'Location' {
							'FunctionCode' 'FND'
							'FacilityCode' fnd?.Facility?.FacilityCode
							'FacilityName' util.substring(fnd?.Facility?.FacilityName, 1, 35)
//							'LocationName' ''
							'LocationDetails' {
//								'Address' {
//									'AddressLines' ''
//								}
//								'Street' ''
								'City' fnd?.CityDetails?.City
								'County' fnd?.CityDetails?.County
//								'StateProvinceCode' ''
								'StateProvince' fnd?.CityDetails?.State
								'CountryCode' util.substring(fnd?.CSStandardCity?.CSCountryCode, 1, 3)
								'CountryName' fnd?.CityDetails?.Country
								'LocationCode' {
//									'MutuallyDefinedCode' ''
									'UNLocationCode' fnd?.CityDetails?.LocationCode?.UNLocationCode
									if(fnd?.CityDetails?.LocationCode?.SchedKDCode) {
										'SchedKDCode'('Type': (fnd?.CityDetails?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), fnd?.CityDetails?.LocationCode?.SchedKDCode)
									}
								}
//								'PostalCode' ''
							}
						}
					}

					if(pol){
						'Location' {
							'FunctionCode' 'POL'
							'FacilityCode' pol?.Facility?.FacilityCode
							'FacilityName' util.substring(pol?.Facility?.FacilityName, 1, 35)
//							'LocationName' ''
							'LocationDetails' {
//								'Address' {
//									'AddressLines' ''
//								}
//								'Street' ''
								'City' pol?.Port?.City
								'County' pol?.Port?.County
								'StateProvinceCode' ''
								'StateProvince' pol?.Port?.State
								'CountryCode' util.substring(pol?.Port?.CSCountryCode, 1, 3)
								'CountryName' pol?.Port?.Country
								'LocationCode' {
//									'MutuallyDefinedCode' ''
									'UNLocationCode' pol?.Port?.LocationCode?.UNLocationCode
									if(pol?.Port?.LocationCode?.SchedKDCode) {
										'SchedKDCode'('Type': (pol?.Port?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), pol?.Port?.LocationCode?.SchedKDCode)
									}
								}
//								'PostalCode' ''
							}
							'EventDate' {
								if(current_Body?.Route?.DepartureDT?.LocDT) {
									Date departureDTDate = util.sdfXmlFmt.parse(current_Body?.Route?.DepartureDT?.LocDT?.LocDT);
									'Departure' ('TimeZone': current_Body?.Route?.DepartureDT?.LocDT?.attr_TimeZone, 'EstActIndicator': (departureDTDate.after(currentSystemDt) ? '1' : '0'), util.convertXmlDateTime(current_Body?.Route?.DepartureDT?.LocDT, "yyyyMMddHHmmss"))
								}
							}

							OceanLeg firstOceanLeg = current_Body?.Route?.OceanLeg[0]

							if(firstOceanLeg?.SVVD?.Loading){
								'VesselVoyageInformation' {
									'VoyageEvent' 'Loading'
									'ServiceName' ('Code': util.substring(firstOceanLeg?.SVVD?.Loading?.Service, 1, 4))
									'VoyageNumberDirection' util.substring(((firstOceanLeg?.SVVD?.Loading?.Voyage ? firstOceanLeg?.SVVD?.Loading?.Voyage : '') + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction : '')), 1, 22)
									'VesselInformation' {
										'VesselCode' firstOceanLeg?.SVVD?.Loading?.Vessel
										'VesselName' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName, 1, 30)
										'VesselRegistrationCountry' firstOceanLeg?.SVVD?.Loading?.VesselNationality
									}
								}
							}
						}
					}

					if(pod){
						'Location' {
							'FunctionCode' 'POD'
							'FacilityCode' pod?.Facility?.FacilityCode
							'FacilityName' util.substring(pod?.Facility?.FacilityName, 1, 35)
//							'LocationName' ''
							'LocationDetails' {
//								'Address' {
//									'AddressLines' ''
//								}
//								'Street' ''
								'City' pod?.Port?.City
								'County' pod?.Port?.County
//								'StateProvinceCode' ''
								'StateProvince' pod?.Port?.State
								'CountryCode' util.substring(pod?.Port?.CSCountryCode, 1, 3)
								'CountryName' pod?.Port?.Country
								'LocationCode' {
//									'MutuallyDefinedCode' ''
									'UNLocationCode' pod?.Port?.LocationCode?.UNLocationCode
									if (pod?.Port?.LocationCode?.SchedKDCode) {
										'SchedKDCode'('Type': (pod?.Port?.LocationCode?.SchedKDType == 'K' ? 'K' : 'D'), pod?.Port?.LocationCode?.SchedKDCode)
									}
								}
//								'PostalCode' ''
							}
							'EventDate' {
								if(current_Body?.Route?.ArrivalDT?.LocDT){
									Date arrivalDTDate = util.sdfXmlFmt.parse(current_Body?.Route?.ArrivalDT?.LocDT?.LocDT);
									'Arrival' ('TimeZone': current_Body?.Route?.ArrivalDT?.LocDT?.attr_TimeZone, 'EstActIndicator': (arrivalDTDate.after(currentSystemDt) ? '1' : '0'), util.convertXmlDateTime(current_Body?.Route?.ArrivalDT?.LocDT, "yyyyMMddHHmmss"))
								}
							}

							OceanLeg lastOceanLeg = null
							if(current_Body?.Route?.OceanLeg?.size() > 0){
								lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
							}
							if(lastOceanLeg?.SVVD?.Discharge){
								'VesselVoyageInformation' {
									'VoyageEvent' 'Discharge'
									'ServiceName' ('Code': util.substring(lastOceanLeg?.SVVD?.Discharge?.Service, 1, 4))
									'VoyageNumberDirection' util.substring(((lastOceanLeg?.SVVD?.Discharge?.Voyage ? lastOceanLeg?.SVVD?.Discharge?.Voyage : '') + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction  : '')),1,22)
									'VesselInformation' {
										'VesselCode' lastOceanLeg?.SVVD?.Discharge?.Vessel
										'VesselName' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName, 1, 30)
										'VesselRegistrationCountry' lastOceanLeg?.SVVD?.Discharge?.VesselNationality
									}
								}
							}
						}
					}
				}
				'EquipmentInformation'{
					current_Body?.Container?.each {current_Container ->
						'Containers' {
							'SOCIndicator' current_Container?.IsSOC == 'true' ? '1' : '0'
							Map<String, String> containerTypeMap = util.getCdeConversionFromIntCde(TP_ID,'BL','O', current_Body.GeneralInformation.SCACCode, 'XML', 'ContainerType', current_Container?.CS1ContainerSizeType, conn)
							if(containerTypeMap.get("EXT_CDE")){
								'ContainerType' containerTypeMap.get("EXT_CDE")
							}else{
								'ContainerType' current_Container?.CS1ContainerSizeType
							}
							'ContainerNumber' ('CheckDigit':current_Container?.ContainerCheckDigit, util.substring(current_Container?.ContainerNumber, 1, 10))
							current_Container?.Seal?.each {current_Seal ->
								if(sealTypeList.contains(current_Seal?.SealType)){
									'SealNumber' ('Type':current_Seal?.SealType, util.substring(current_Seal?.SealNumber, 1, 15))
								}
							}
							if(current_Container?.GrossWeight?.WeightUnit && current_Container?.GrossWeight?.Weight){
								'Weight' ('Qualifier':'Gross','Units':current_Container?.GrossWeight?.WeightUnit,current_Container?.GrossWeight?.Weight)
							}
							if(current_Container?.PieceCount?.PieceCountUnit && current_Container?.PieceCount?.PieceCount){
								'PieceCount' ('Units': current_Container?.PieceCount?.PieceCountUnit, current_Container?.PieceCount?.PieceCount)
							}
							if(current_Body?.GeneralInformation?.TrafficMode){
								'TrafficMode' {
									'OutBound' current_Body?.GeneralInformation?.TrafficMode?.OutBound
									'InBound' current_Body?.GeneralInformation?.TrafficMode?.InBound
								}
							}
							'RateAndCharges' {
//								'Charges' {
//									'ChargeTypeCode' ''
//									'FreightRate' ''
//									'PrepaidAmount' ''
//									'CollectAmount' ''
//									'PayableAt' ''
//									'PayableBy' {
//										'CarrierCustomerCode' ''
//										'ContactPerson' {
//											'FirstName' ''
//											'LastName' ''
//											'Phone' {
//												'CountryCode' ''
//												'AreaCode' ''
//												'Number' ''
//											}
//											'Fax' {
//												'CountryCode' ''
//												'AreaCode' ''
//												'Number' ''
//											}
//											'Email' ''
//										}
//										'Address' {
//											'AddressLines' ''
//										}
//										'CityName' ''
//										'County' ''
//										'StateProvinceCode' ''
//										'StateProvince' ''
//										'CountryCode' ''
//										'CountryName' ''
//										'PostalCode' ''
//									}
//								}
							}
							'ReeferSetting' {
								current_Body?.Cargo?.findAll{it?.CurrentContainerNumber == current_Container?.ContainerNumber && it?.CurrentContainerCheckDigit == current_Container?.ContainerCheckDigit}?.each {current_Container_AssoCargo ->
									current_Container_AssoCargo?.ReeferCargoSpec?.each {current_ReeferCargo ->
										'ReeferCargoInfo' {
											'ReeferSettings' ('AtmosphereType' : current_ReeferCargo?.AtmosphereType,
													'GenSetType' : current_ReeferCargo?.GensetType,
													'VentSettingCode' : current_ReeferCargo?.VentSettingCode,
													'CO2' : current_ReeferCargo?.CO2 != '0' ? current_ReeferCargo?.CO2 : '',
													'O2' : current_ReeferCargo?.O2 != '0' ? current_ReeferCargo?.O2 : '',
													'Humidity' : current_ReeferCargo?.DehumidityPercentage,
													'PreCooling' : current_ReeferCargo?.IsPreCoolingReq == 'true' ? '1' : '0' ,
													'CAIndicator' : current_ReeferCargo?.IsControlledAtmosphere == 'true' ? '1' : '0',
													'ROIndicator' : current_ReeferCargo?.IsReeferOperational == 'true' ? '1' : '0') {
												'Temperature' ('Units':current_ReeferCargo?.Temperature?.TemperatureUnit, current_ReeferCargo?.Temperature?.Temperature)
												if(ventilationUnitList.contains(current_ReeferCargo?.Ventilation?.VentilationUnit) && current_ReeferCargo?.Ventilation?.Ventilation){
													'Ventilation' ('Units':current_ReeferCargo?.Ventilation?.VentilationUnit, current_ReeferCargo?.Ventilation?.Ventilation)
												}
												'SensitiveCargoDesc' current_ReeferCargo?.SensitiveCargoDesc
//												'Remarks' {
//													'RemarksLines' ''
//												}
											}
											current_ReeferCargo?.EmergencyContact?.each {current_EmergencyContact ->
												'EmergencyContactDetails' {
													'FirstName' current_EmergencyContact?.FirstName
													'LastName' current_EmergencyContact?.LastName
													'Phone' {
														'CountryCode' util.substring(current_EmergencyContact?.ContactPhone?.CountryCode, 1, 3)
														'AreaCode' current_EmergencyContact?.ContactPhone?.AreaCode
														'Number' current_EmergencyContact?.ContactPhone?.Number
													}
													'Fax' {
														'CountryCode' util.substring(current_EmergencyContact?.ContactFax?.CountryCode, 1, 3)
														'AreaCode' current_EmergencyContact?.ContactFax?.AreaCode
														'Number' current_EmergencyContact?.ContactFax?.Number
													}
													'Email' current_EmergencyContact?.ContactEmailAddress
												}
											}
										}
									}
								}

							}

							Map<String, String> dndTypeMap = ['Demurrage':'DEM', 'Detention':'DET', 'Det/Dem':'COMBINED']

							DnD current_DnD = current_Body?.DnD?.find{it.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it.ContainerNumber == current_Container?.ContainerNumber && dndTypeMap.get(it?.attr_Type)}

							if(current_DnD){
								'DetentionDemurrage' {
									'Type' dndTypeMap.get(current_DnD?.attr_Type)
									'EventDate' {
										if(current_DnD?.attr_Type == 'Demurrage'){
											if(current_DnD?.FreeTimeStartDT?.LocDT){
												'FreeDemurrageStartDate' ('TimeZone':current_DnD?.FreeTimeStartDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_DnD?.FreeTimeStartDT?.LocDT, 'yyyyMMddHHmmss'))
											}else if(current_DnD?.FreeTimeStartDT?.GMT){
												'FreeDemurrageStartDate' ('TimeZone':'GMT', util.convertXmlDateTime(current_DnD?.FreeTimeStartDT?.GMT, 'yyyyMMddHHmmss'))
											}
										}
										if((current_DnD?.attr_Type == 'DET' || current_DnD?.attr_Type == 'Det/Dem') && current_DnD?.FreeTimeStartDT){
											if(current_DnD?.FreeTimeStartDT?.LocDT){
												'FreeDetentionStartDate' ('TimeZone':current_DnD?.FreeTimeStartDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_DnD?.FreeTimeStartDT?.LocDT, 'yyyyMMddHHmmss'))
											}else if(current_DnD?.FreeTimeStartDT?.GMT){
												'FreeDetentionStartDate' ('TimeZone':'GMT', util.convertXmlDateTime(current_DnD?.FreeTimeStartDT?.GMT, 'yyyyMMddHHmmss'))
											}
										}

									}
									if(current_DnD?.FreeTimeType && current_DnD?.FreeTime){
										'FreeTime' ('Type':current_DnD?.FreeTimeType, current_DnD?.FreeTime)
									}
								}
							}
						}
					}
				}
				'CargoInformation'{
					current_Body?.Cargo?.each { current_Cargo ->
						'CargoItems' {
							'CargoNature' current_Cargo?.CargoNature
							'CommodityCode' ''
							if(current_Cargo?.GrossWeight?.WeightUnit && current_Cargo?.GrossWeight?.Weight) {
								'Weight' ('Qualifier':'Gross', 'Units':current_Cargo?.GrossWeight?.WeightUnit ? current_Cargo?.GrossWeight?.WeightUnit : '', current_Cargo?.GrossWeight?.Weight)
							}
							if(volumeUnitList.contains(current_Cargo?.Volume?.VolumeUnit) && current_Cargo?.Volume?.Volume){
								'Volume' ('Units':current_Cargo?.Volume?.VolumeUnit, current_Cargo?.Volume?.Volume)
							}
							String packageQty = ''
							if(current_Cargo?.CargoNature == 'GC'){
								if(current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1'}?.PackageUnitQuantity){
									packageQty = current_Cargo?.GCPackageUnit?.find{it.PackageSeqNumber == '1'}?.PackageUnitQuantity
								}else if (current_Cargo?.Packaging?.PackageQty){
									packageQty = current_Cargo?.Packaging?.PackageQty
								}
							}else if(current_Cargo?.CargoNature == 'RF'){
								packageQty = current_Cargo?.ReeferCargoSpec?.find{it.RFPackageUnit.find{it.PackageSeqNumber == '1'}}?.RFPackageUnit?.get(0)?.PackageUnitQuantity
							}else if(current_Cargo?.CargoNature == 'DG'){
								packageQty = current_Cargo?.DGCargoSpec?.find{it.DGPackageUnit.find{it.PackageSeqNumber == '1'}}?.DGPackageUnit?.get(0)?.PackageUnitQuantity
							}else if(current_Cargo?.CargoNature == 'AW'){
								packageQty = current_Cargo?.AWCargoSpec?.find{it.AWPackageUnit.find{it.PackageSeqNumber == '1'}}?.AWPackageUnit?.get(0)?.PackageUnitQuantity
							}else if(current_Cargo?.CargoNature == 'AD'){
								if(current_Cargo?.AWCargoSpec?.find{it.AWPackageUnit.find{it.PackageSeqNumber == '1'}}?.AWPackageUnit?.get(0)?.PackageUnitQuantity){
									packageQty = current_Cargo?.AWCargoSpec?.find{it.AWPackageUnit.find{it.PackageSeqNumber == '1'}}?.AWPackageUnit?.get(0)?.PackageUnitQuantity
								}else if(current_Cargo?.DGCargoSpec?.find{it.DGPackageUnit.find{it.PackageSeqNumber == '1'}}?.DGPackageUnit?.get(0)?.PackageUnitQuantity){
									packageQty = current_Cargo?.DGCargoSpec?.find{it.DGPackageUnit.find{it.PackageSeqNumber == '1'}}?.DGPackageUnit?.get(0)?.PackageUnitQuantity
								}
							}else if(current_Cargo?.CargoNature == 'RD'){
								if(current_Cargo?.ReeferCargoSpec?.find{it.RFPackageUnit.find{it.PackageSeqNumber == '1'}}?.RFPackageUnit?.get(0)?.PackageUnitQuantity){
									packageQty = current_Cargo?.ReeferCargoSpec?.find{it.RFPackageUnit.find{it.PackageSeqNumber == '1'}}?.RFPackageUnit?.get(0)?.PackageUnitQuantity
								}else if(current_Cargo?.DGCargoSpec?.find{it.DGPackageUnit.find{it.PackageSeqNumber == '1'}}?.DGPackageUnit?.get(0)?.PackageUnitQuantity){
									packageQty = current_Cargo?.DGCargoSpec?.find{it.DGPackageUnit.find{it.PackageSeqNumber == '1'}}?.DGPackageUnit?.get(0)?.PackageUnitQuantity
								}
							}
							'Package' ('Type':current_Cargo?.Packaging?.PackageType, packageQty)
							'ContainerNumber' ('CheckDigit':current_Cargo?.CurrentContainerCheckDigit, current_Cargo?.CurrentContainerNumber)
							if(current_Cargo?.CargoDescription){
								'CargoDescription' {
									for(int i = 0 ; i * 60 < current_Cargo?.CargoDescription?.length(); i++){
										'DescriptionLine' util?.substring(current_Cargo?.CargoDescription, i * 60 + 1 , 60)
									}
								}
							}
							if(current_Cargo?.MarksAndNumbers?.size() > 0){
								'MarksAndNumbers' {
									current_Cargo?.MarksAndNumbers?.each {current_MarksAndNumbers ->
										'MarksAndNumbersLine' util.substring(current_MarksAndNumbers.MarksAndNumbersLine, 1, 240)
									}
								}
							}
							if(current_Cargo?.DGCargoSpec && current_Cargo?.DGCargoSpec?.size() > 0){
								'DangerousCargo' {
									current_Cargo?.DGCargoSpec?.each {current_DGCargo ->
										'DangerousCargoInfo' {
											'HazardousMaterial' {
												'DGRegulator' current_DGCargo?.DGRegulator
												'IMCOClass' util.substring(current_DGCargo?.IMOClass, 1, 5)
												'IMDGPage' util.substring(current_DGCargo?.IMDGPage, 1, 5)
												'UNNumber' current_DGCargo?.UNNumber
												'TechnicalShippingName' current_DGCargo?.TechnicalName
												'ProperShippingName' current_DGCargo?.ProperShippingName
												'EMSNumber' current_DGCargo?.EMSNumber
												'PSAClass' current_DGCargo?.PSAClass
												'MFAGPageNumber' current_DGCargo?.MFAGNumber
												if(current_DGCargo?.FlashPoint?.TemperatureUnit && current_DGCargo?.FlashPoint?.Temperature){
													'FlashPoint' ('Units':current_DGCargo?.FlashPoint?.TemperatureUnit, current_DGCargo?.FlashPoint?.Temperature)
												}else{
													'FlashPoint' ('Units':'C', '999.99')
												}
												if(StringUtil.isNotEmpty(current_DGCargo?.ElevatedTemperature?.TemperatureUnit) && StringUtil.isNotEmpty(current_DGCargo?.ElevatedTemperature?.Temperature)){
													'DGElevationTemperature' ('Units':current_DGCargo?.ElevatedTemperature?.TemperatureUnit, current_DGCargo?.ElevatedTemperature?.Temperature)
												}
												'State' current_DGCargo?.State
//												'ApprovalCode' util.substring(current_DGCargo?.ApprovalCode, 1, 20)	//removed from B2BSCR20170626004090d
												current_DGCargo?.Label?.eachWithIndex {current_Label, index ->
													if(index < 4){
														'Label' current_Label
													}
												}
												if(current_DGCargo?.GrossWeight?.WeightUnit && current_DGCargo?.GrossWeight?.Weight) {
													'Weight'('Qualifier': 'Gross', 'Units': current_DGCargo?.GrossWeight?.WeightUnit, current_DGCargo?.GrossWeight?.Weight)
												}
												if(StringUtil.isNotEmpty(current_DGCargo?.NetExplosiveWeight?.WeightUnit) && StringUtil.isNotEmpty(current_DGCargo?.NetExplosiveWeight?.Weight)){
													'NetExplosiveWeight' ('Units':current_DGCargo?.NetExplosiveWeight?.WeightUnit, current_DGCargo?.NetExplosiveWeight?.Weight)
												}
												'Indicators' {
													'isMarinePollutant' current_DGCargo?.isMarinePollutant == 'true' ? '1' : '0'
													'isInhalationHazardous' current_DGCargo?.IsInhalationHazardous == 'true' ? '1' : '0'
													'isLimitedQuantity' current_DGCargo?.isLimitedQuantity == 'true' ? '1' : '0'
													'isReportableQuantity' current_DGCargo?.IsReportableQuantity == 'true' ? '1' : '0'
													'isEmptyUnclean' current_DGCargo?.IsEmptyUnclean == 'true' ? '1' : '0'
												}
												'Package' {
													'PackagingGroupCode' current_DGCargo?.PackageGroup?.Code
													'InnerPackageDescription' ('Type':current_DGCargo?.PackageGroup?.InnerPackageDescription?.PackageType,current_DGCargo?.PackageGroup?.InnerPackageDescription?.PackageDesc)
													'OuterPackageDescription' ('Type':current_DGCargo?.PackageGroup?.OuterPackageDescription?.PackageType,current_DGCargo?.PackageGroup?.OuterPackageDescription?.PackageDesc)
												}
												if(current_DGCargo?.Remarks){
													'Remarks' {
														for(int i = 0; i * 100 < current_DGCargo?.Remarks?.length(); i++ ){
															'RemarksLines' util.substring(current_DGCargo?.Remarks, i * 100 + 1 , 100)
														}
													}
												}
											}
											current_DGCargo?.EmergencyContact?.each {current_EmergencyContact ->
												'EmergencyContactDetails' {
													'FirstName' current_EmergencyContact?.FirstName
													'LastName' current_EmergencyContact?.LastName
													'Phone' {
														'CountryCode' util.substring(current_EmergencyContact?.ContactPhone?.CountryCode, 1, 3)
														'AreaCode' current_EmergencyContact?.ContactPhone?.AreaCode
														'Number' current_EmergencyContact?.ContactPhone?.Number
													}
													'Fax' {
														'CountryCode' util.substring(current_EmergencyContact?.ContactFax?.CountryCode, 1, 3)
														'AreaCode' current_EmergencyContact?.ContactFax?.AreaCode
														'Number' current_EmergencyContact?.ContactFax?.Number
													}
													'Email' current_EmergencyContact?.ContactEmailAddress
												}
											}
										}
									}
								}
							}
							if(current_Cargo?.AWCargoSpec && current_Cargo?.AWCargoSpec?.size() > 0){
								'AwkwardCargo' {
									current_Cargo?.AWCargoSpec?.each {current_AWCargo ->
										'AwkwardCargoInfo' {
											'AwkwardCargoDetails' {
												'Height' ('Units':current_AWCargo?.Height?.LengthUnit, current_AWCargo?.Height?.Length)
												'Width' ('Units':current_AWCargo?.Width?.LengthUnit, current_AWCargo?.Width?.Length)
												'Length' ('Units':current_AWCargo?.Length?.LengthUnit, current_AWCargo?.Length?.Length)
												if(current_AWCargo?.Remarks){
													'Remarks' {
														for(int i = 0; i * 100 < current_AWCargo?.Remarks?.length(); i++ ){
															'RemarksLines' util.substring(current_AWCargo?.Remarks, i * 100 + 1 , 100)
														}
													}
												}
											}
											current_AWCargo?.EmergencyContact?.each {current_EmergencyContact ->
												'EmergencyContactDetails' {
													'FirstName' current_EmergencyContact?.FirstName
													'LastName' current_EmergencyContact?.LastName
													'Phone' {
														'CountryCode' util.substring(current_EmergencyContact?.ContactPhone?.CountryCode, 1 ,3)
														'AreaCode' current_EmergencyContact?.ContactPhone?.AreaCode
														'Number' current_EmergencyContact?.ContactPhone?.Number
													}
													'Fax' {
														'CountryCode' util.substring(current_EmergencyContact?.ContactFax?.CountryCode, 1, 3)
														'AreaCode' current_EmergencyContact?.ContactFax?.AreaCode
														'Number' current_EmergencyContact?.ContactFax?.Number
													}
													'Email' current_EmergencyContact?.ContactEmailAddress
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			'SummaryDetails' {
				'BLInformation' ('BillType':current_Body?.GeneralInformation?.BLType){
					'BLNumber' current_Body?.GeneralInformation?.BLNumber
					'BLStatus' current_Body?.BLStatus[0]?.Status
					'BLIssuanceDateTime' ('TimeZone':current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT, 'yyyyMMddHHmmss'))
					'BLReceiptDateTime' ('TimeZone':current_Body?.GeneralInformation?.BLReceiptDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT, 'yyyyMMddHHmmss'))
					'BLCreationDateTime' ('TimeZone':current_Body?.GeneralInformation?.BLCreationDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.GeneralInformation?.BLCreationDT?.LocDT, 'yyyyMMddHHmmss'))
					'BLUpdateDateTime' ('TimeZone':current_Body?.GeneralInformation?.BLChangeDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.GeneralInformation?.BLChangeDT?.LocDT, 'yyyyMMddHHmmss'))
					'BLOnBoardDateTime' ('TimeZone':current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT, 'yyyyMMddHHmmss'))
					'BLCapturingOffice' ('PhoneNumber':current_Body?.GeneralInformation?.CaptureOfficePhoneNumber, current_Body?.GeneralInformation?.CaptureOffice)
					'PaymentStatus'{
						'BLPaymentOffice' ('PhoneNumber':current_Body?.GeneralInformation?.ContactOfficeCode, current_Body?.GeneralInformation?.BLPaymentOffice)
						'PaymentReceiptDateTime' ('TimeZone':current_Body?.GeneralInformation?.PaymentReceiptDT?.LocDT?.attr_TimeZone, util.convertXmlDateTime(current_Body?.GeneralInformation?.PaymentReceiptDT?.LocDT, 'yyyyMMddHHmmss'))
					}
					'CargoType' current_Body?.GeneralInformation?.ShipmentCargoType
					'CargoControlOffice' current_Body?.GeneralInformation?.CargoControlOffice
					'TrafficMode'{
						'OutBound' current_Body?.GeneralInformation?.TrafficMode?.OutBound
						'InBound' current_Body?.GeneralInformation?.TrafficMode?.InBound
					}
					if(current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit && current_Body?.GeneralInformation?.BLGrossWeight?.Weight){
						'Weight' ('Qualifier':'Gross','Units':current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit , current_Body?.GeneralInformation?.BLGrossWeight?.Weight)
					}
					if(volumeUnitList.contains(current_Body?.GeneralInformation?.BLVolume?.VolumeUnit) && current_Body?.GeneralInformation?.BLVolume?.Volume){
						'Volume' ('Units':current_Body?.GeneralInformation?.BLVolume?.VolumeUnit, current_Body?.GeneralInformation?.BLVolume?.Volume)
					}
				}
				current_Body?.BLCertClause?.each {current_BLCertClause ->
					if(current_BLCertClause?.CertificationClauseType && current_BLCertClause?.CertificationClauseText){
						'Certifications'{
							'Code' current_BLCertClause?.CertificationClauseType
							'ClauseText' util.substring(current_BLCertClause?.CertificationClauseText, 1, 280)
						}
					}
				}
				'Charges'{
					filteredFreightCharge?.each {current_FreightCharge ->
						'Charges'{
							'ChargeTypeCode' current_FreightCharge?.ChargeCode
							'FreightRate' current_FreightCharge?.FreightRate
							if(current_FreightCharge?.ChargeType == blUtil?.PREPAID){
								'PrepaidAmount' ('Currency':current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency, 'ExchangeRate':current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate, current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)
							}
							if(current_FreightCharge?.ChargeType == blUtil?.COLLECT) {
								'CollectAmount' ('Currency':current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency, 'ExchangeRate':current_FreightCharge?.TotalAmtInPmtCurrency?.attr_ExchangeRate, current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency)
							}
							'PayableAt' current_FreightCharge?.PayableElseWhere == 'true' ? '0' : '1'
							'PayableBy'{
								'CarrierCustomerCode' current_FreightCharge?.PayByInformation?.CarrierCustomerCode
								'ContactPerson'{
									'FirstName' current_FreightCharge?.PayByInformation?.PayerName
//									'LastName' ''
//									'Phone'{
//										'CountryCode' ''
//										'AreaCode' ''
//										'Number' ''
//									}
//									'Fax'{
//										'CountryCode' ''
//										'AreaCode' ''
//										'Number' ''
//									}
//									'Email' ''
								}
//								'Address'{
//									'AddressLines' ''
//								}
								'CityName' current_FreightCharge?.PayByInformation?.CityDetails?.City
								'County' current_FreightCharge?.PayByInformation?.CityDetails?.County
								'StateProvinceCode' ''
								'StateProvince' current_FreightCharge?.PayByInformation?.CityDetails?.State
								'CountryCode' ''
								'CountryName' current_FreightCharge?.PayByInformation?.CityDetails?.Country
								'PostalCode' ''
							}
						}
					}
				}
				'Remarks' {
					for (int i = 0; i * 100 < current_Body?.Remarks?.length(); i++) {
						'RemarksLines' util.substring(current_Body?.Remarks, i * 100 + 1, 100)
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
		outXml.omitEmptyAttributes = true
		outXml.omitNullAttributes = true

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
		def blXml = outXml.createNode('BillOfLading')
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
			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)

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
		println util.cleanXml(writer?.toString())
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			result = util.cleanXml(writer?.toString())
			//promote XML output file name to transportation, xml format need this function
			//please change the file name pattern base on B2B_EDI_FILENAME setting
			long fileSeqNumber = util.getFileSequenceNumber(TP_ID, 'BC', 'BCXML', 'BKGSUPUIFO', conn)
			String fileSeqStr = '01'
			if (fileSeqNumber>=0) {
				fileSeqStr = fileSeqNumber + ""
				if (fileSeqStr.length()>2) {
					fileSeqStr = fileSeqStr.substring(fileSeqStr.length()-2)
				} else if (fileSeqStr.length()==1) {
					fileSeqStr = "0" + fileSeqStr
				}
			}
			//output file name
			String outputFileName = "OOCLLOGISTICS_BL_" + currentSystemDt.format(yyyyMMddHHmmss) + fileSeqStr + ".xml"
			blUtil.promoteOutputFileNameToSession(appSessionId, outputFileName)
		}

		writer.close();
		
		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){

		List<Map<String,String>> errorList = []

		blUtil.checkCarrierBookingNumber(current_Body, true, null, errorList)
		blUtil.checkCertificationClauseTextLength(current_Body, 280, true, null, errorList)
		blUtil.checkContainerTrafficModeInboundLength(current_Body, 3, true, null, errorList)
		blUtil.checkContainerTrafficModeOutboundLength(current_Body, 3, true, null, errorList)
		blUtil.checkDangerousCargoIMDGPageLength(current_Body, 5, true, null, errorList)
		blUtil.checkDangerousCargoIMOClassLength(current_Body, 5, true, null, errorList)
		blUtil.checkOceanLegDischargeVesselNameLength(current_Body, 30, true, null, errorList)
		blUtil.checkOceanLegLoadingVesselNameLength(current_Body, 30, true, null, errorList)
		blUtil.checkReeferCargoSensitiveCargoDescLength(current_Body, 20, true, null, errorList)
		blUtil.checkStopOffPickupDetailsFacilityNameLength(current_Body, 35, true, null, errorList)
		blUtil.checkStopOffReturnDetailsFacilityNameLength(current_Body, 35, true, null, errorList)

		return errorList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){

		cs.b2b.core.common.xmlvalidation.ValidateXML validator = new ValidateXML()

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</BillOfLading>")
		util.cleanNode(node)

		String validationResult = validator.xmlValidation('CUS-BLXML-10.0', XmlUtil.serialize(node))
		if (validationResult.contains('Validation Failure.')){
			errorKeyList.add([TYPE: 'ES', IS_ERROR: 'YES', VALUE: validationResult])
		}
	}
}


