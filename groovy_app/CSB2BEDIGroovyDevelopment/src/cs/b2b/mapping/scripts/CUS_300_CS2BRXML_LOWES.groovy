package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.HashMap
import java.util.List;
import java.util.Map;

import cs.b2b.core.mapping.bean.edi.x12.v5040.T300_LOWES.EDI_LOWES_T300
import cs.b2b.core.mapping.bean.edi.x12.v5040.T300_LOWES.Loop_ST3
import cs.b2b.core.mapping.bean.edi.x12.v5040.T300_LOWES.Loop_N116
import cs.b2b.core.mapping.bean.edi.x12.v5040.T300_LOWES.Loop_Y210
import cs.b2b.core.mapping.bean.edi.x12.v5040.T300_LOWES.R423
import cs.b2b.core.mapping.bean.edi.x12.v5040.T300_LOWES.V151
import cs.b2b.core.mapping.bean.edi.x12.v5040.T300_LOWES.Loop_LX29

import cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Booking_CSSTD;
import cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Request;
import cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Location
import cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Intermodal

import cs.b2b.core.common.xmlvalidation.ValidateXML
import cs.b2b.core.mapping.util.XmlBeanParser



/**
 * IG		: 300 5040 for LOWES
 */
public class CUS_300_CS2BRXML_LOWES {
	
	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil()
	cs.b2b.core.mapping.util.MappingUtil_BR_I_Common brUtil = new cs.b2b.core.mapping.util.MappingUtil_BR_I_Common()
	
	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	Connection conn = null
	
	// List of EDI Enum
	// ExtCode : IntCode
	Map<String, String> extActionType = [	"N" : "NEW",
											"U" : "UPD",
											"R" : "CANCEL",
									]
	
	Map<String, String> extRefMap = [	"E8" : "SC",
										"RU" : "OTH",
										"BN" : "BKG",
									]
	
	Map<String, String> extCarrierRateMap = [ "" : ""]
		
	Map<String, String> extPartyTypeMap = [	"CN"  : "CGN",
											"NP"  : "NPT",
											"SH"  : "SHP",
											]
		
	Map<String, String> extWeightUnitMap = ["L" : "LBS"]
	Map<String, String> extVolumeUnitMap = ["E" : "CBF"]
	
	Map<String, String> extOutboundHaulageMap = ["DD" : "M",
												"DP" : "M",
												"PD" : "C",
												"PP" : "C"
												]
	
	Map<String, String> extInboundHaulageMap = ["DD" : "M",
												"DP" : "C",
												"PD" : "M",
												"PP" : "C"
												]
	static final String SRC_FMT_ID = "300"
	static final String TRG_FMT_ID = "BRCS2X"
	static final String CCC_CONVERTTYPEID = "CarrierCustCode"
//	static final String CONTAINER_CONVERTTYPEID = "ContainerType"
//	static final String PACKAGE_CONVERTTYPEID = "PackageUnit"
//	static final String ISNEEDREPLYPARTYEMAIL_CONVERTTYPEID = "isNeedReplyPartyEmail"
	
	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMddHHmmss = "yyyyMMddHHmmss"
	def yyyyMMddHHmm = "yyyyMMddHHmm"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = "HHmm"
	
	private List<Map<String,String>> prepValidation(def TP_ID, def current_Body, def currentTransactionIndex, Connection conn) {
		Loop_ST3 currentTransaction = current_Body
		
		List<Map<String,String>> AppErrors = new ArrayList<Map<String,String>>() 
		
		//check tp_integration_asso
		def ReceiverID = util.getCarrierTpId(TP_ID, 'BR', currentTransaction.B1?.E140_01, conn)
		if (!util.notEmpty(ReceiverID)) {
			Map<String,String> errorKey = null
			errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Customer is not integrate with SCAC : '+ currentTransaction.B1?.E140_01]
			AppErrors.add(errorKey)
		}
		
		// check action type 
		if (extActionType[current_Body.B1?.E558_04] == ""){
			Map<String,String> errorKey = null
			errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Invalid Action Type :' + current_Body.B1?.E558_04]
			AppErrors.add(errorKey)
		}
		
		//Check CustomerBooking Reference
		if (!util.notEmpty(currentTransaction.B1?.E145_02)) {
			Map<String,String> errorKey = null
			errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Missing customer booking reference number B1_04']
			AppErrors.add(errorKey)
		}
		
		return AppErrors
	}
	
	public void generateBody(MarkupBuilder outXml, def current_Body, def current_BodyIndex, def currentSystemDt, def transactionDT, def preErrors) {
		Loop_ST3 currentTransaction = current_Body
		String SCAC_CDE = currentTransaction.B1?.E140_01
		SimpleDateFormat sdf=new SimpleDateFormat(xmlDateTimeFormat)
		outXml.'ns0:Request'{

			'ns0:TransactionInformation'{
				UUID idOne = UUID.randomUUID();
				'ns1:MessageID' MSG_REQ_ID+','+idOne
				'ns1:InterchangeTransactionID'  currentTransaction.ST?.E329_02
			}

			'ns0:EventInformation'{
				'ns1:EventCode' 'bkg_req'
				'ns1:EventDT'{
					'ns1:LocDT'( 'TimeZone' : 'HKT', 'CSTimeZone' : 'HKT', sdf.format(currentSystemDt)+'+08:00')
				}
			}

			'ns0:GeneralInformation'{
				'ns0:CSBookingRefNumber'
				currentTransaction.N9?.each { currentN9 -> 
					if(currentN9.E128_01=="BN" && currentN9.E127_02!="") {
						'ns0:CarrierBookingNumber' currentN9.E127_02
					}
				}
				
				'ns0:ActionType' extActionType[currentTransaction.B1?.E558_04]
				'ns0:SCAC' SCAC_CDE
//				'ns0:BookingOffice'{
//					'ns0:BookingRegion' 
//					'ns0:BookingOffice' 
//					'ns0:BookingOfficeName' 
//					'ns0:BLIssuingOffice' 
//					}

				'ns0:Requested'{
					'ns0:By' //TP_ID
					'ns0:RequestedDT'{
						'ns1:LocDT' ('TimeZone' : 'GMT',
						util.convertDateTime(currentTransaction.B1?.E373_03,yyyyMMdd,xmlDateTimeFormat)+"+08:00" )
					}
				}
//				'ns0:Amended'{
//					'ns0:By' 
//					'ns0:AmendedDT'{
//							'ns1:LocDT'
//					}
//				}

				'ns0:ShipmentCargoType' 'GC'

				'ns0:ShipmentTrafficMode'{
					'ns1:OutBound' 'FCL'
					'ns1:InBound' 'FCL'
				}
				
				if (current_Body.G61?.E365_03=="EM") {
					'ns0:NotificationEmailAddress' util.substring(currentTransaction.G61?.first().E364_04,1,60)
				}
				'ns0:isNeedReplyPartyEmail'
				'ns0:CustomerBookingReference' currentTransaction.B1?.E145_02
			}

			//party  ->begin
			currentTransaction.Loop_N1.each {currentParty ->
				Loop_N116 party = currentParty
				
				def curPartyType = extPartyTypeMap[party.N1?.E98_01] 
				if (curPartyType !="" && curPartyType != null) {
					'ns0:Party'{
						'ns1:PartyType' curPartyType
						'ns1:PartyName' party.N1?.E93_02
						
						if (currentParty.N1?.E67_04!="") {
							'ns1:CarrierCustomerCode' util.getConversionByExtCdeWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, CCC_CONVERTTYPEID, party.N1?.E67_04,party.N1?.E67_04, conn)
						}
						 
						'ns1:isNeedReplyPartyEmail'
						'ns1:Contact'{
							'ns1:FirstName'
							'ns1:LastName'
							'ns1:ContactPhone'{
								'ns1:CountryCode'
								'ns1:AreaCode'
								'ns1:Number'
							}
							'ns1:ContactFax'{
								'ns1:CountryCode'
								'ns1:AreaCode'
								'ns1:Number'
							}
							'ns1:ContactEmailAddress'
						}
						'ns1:Address'{
							'ns1:City' party.N4?.E19_01
							'ns1:County'
							'ns1:State' party.N4?.E156_02
							'ns1:Country' party.N4?.E26_04
							'ns1:LocationCode'{
								'ns1:MutuallyDefinedCode'
								'ns1:UNLocationCode'
								'ns1:SchedKDType'
								'ns1:SchedKDCode'
							}
							
							'ns1:PostalCode' party.N4?.E116_03
							'ns1:AddressLines' {
								'ns1:AddressLine' party.N3?.first().E166_01 
							}
						}
						
						'ns0:SalesOfficeCode'
						'ns0:EDIPartyType'
					}
					
					// Map Booking party from N1_01 = 'CN'
					if (party.N1?.E98_01 =="CN") {
						'ns0:Party'{
							'ns1:PartyType' 'BPT'
							'ns1:PartyName' party.N1?.E93_02
							
							if (party.N1?.E67_04!="") {
								'ns1:CarrierCustomerCode' util.getConversionByExtCdeWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, CCC_CONVERTTYPEID, party.N1?.E67_04,party.N1?.E67_04, conn)
							}
							 
							'ns1:isNeedReplyPartyEmail'
							'ns1:Contact'{
								'ns1:FirstName'
								'ns1:LastName'
								'ns1:ContactPhone'{
									'ns1:CountryCode'
									'ns1:AreaCode'
									'ns1:Number'
								}
								'ns1:ContactFax'{
									'ns1:CountryCode'
									'ns1:AreaCode'
									'ns1:Number'
								}
								'ns1:ContactEmailAddress'
							}
							'ns1:Address'{
								'ns1:City' party.N4?.E19_01
								'ns1:County'
								'ns1:State' party.N4?.E156_02
								'ns1:Country' party.N4?.E26_04
								'ns1:LocationCode'{
									'ns1:MutuallyDefinedCode'
									'ns1:UNLocationCode'
									'ns1:SchedKDType'
									'ns1:SchedKDCode'
								}
								'ns1:PostalCode' party.N4?.E116_03
								'ns1:AddressLines' {
									'ns1:AddressLine' party.N3?.E166_01
								}
							}
							
							'ns0:SalesOfficeCode'
							'ns0:EDIPartyType'
						}
					}
				}
			}
			
			//For Cancel booking if no booking party add one
			if (extActionType[currentTransaction.B1?.E558_04]=="CANCEL" && currentTransaction.Loop_N1?.find(it.N1?.E98_01 =="BP" ).size()==0) {
				'ns0:Party'{
					'ns1:PartyType' 'BPT'
					'ns1:PartyName' 'LG Sourcing, Inc.'
					'ns1:CarrierCustomerCode' '6756088000'
					'ns1:isNeedReplyPartyEmail'
					'ns1:Contact'{
						'ns1:FirstName'
						'ns1:LastName'
						'ns1:ContactPhone'{
							'ns1:CountryCode'
							'ns1:AreaCode'
							'ns1:Number'
						}
						'ns1:ContactFax'{
							'ns1:CountryCode'
							'ns1:AreaCode'
							'ns1:Number'
						}
						'ns1:ContactEmailAddress'
					}
					'ns1:Address'{
						'ns1:City' 
						'ns1:County'
						'ns1:State' 
						'ns1:Country' 'US'
						'ns1:LocationCode'{
							'ns1:MutuallyDefinedCode'
							'ns1:UNLocationCode'
							'ns1:SchedKDType'
							'ns1:SchedKDCode'
						}
						'ns1:PostalCode' 
						'ns1:AddressLines' {
							'ns1:AddressLine' '1605 Curtis Bridge Road, 1111, Wilkesboro, Wilkes, North Carolina, United States'
						}
					}
					
					'ns0:SalesOfficeCode'
					'ns0:EDIPartyType'
				}
			}
			//party  ->end

			//Carrier Rate References  ->begin
			currentTransaction.N9?.each { currentRef ->
				if (extCarrierRateMap[currentRef.E128_01]!="" && extCarrierRateMap[currentRef.E128_01]!=null) {
					'ns0:CarrierRateReference'{
						'ns1:CSCarrierRateType' extCarrierRateMap[currentRef.E128_01]
						'ns1:CarrierRateNumber'currentRef.E127_02
					}
				}
			}
			//External References  ->begin
			currentTransaction.N9?.each { currentRef ->
				if (extRefMap[currentRef.E128_01]!="" && extRefMap[currentRef.E128_01]!=null) {
					'ns0:ExternalReference'{
						'ns1:CSReferenceType' extRefMap[currentRef.E128_01]
						'ns1:ReferenceNumber' currentRef.E127_02
						'ns1:ReferenceDescription'
					}
				}
				
			}
			
			//extra External Reference
			if (currentTransaction.B1?.E145_02!="") {
				'ns0:ExternalReference'{
					'ns1:CSReferenceType' 'FR'
					'ns1:ReferenceNumber' currentTransaction.B1?.E145_02
					'ns1:ReferenceDescription'
				}
			}
			
			//References  ->end
			
			//Container ->begin
			currentTransaction.Loop_Y2.eachWithIndex { curContainer, containerIdx->
				Loop_Y210 container = curContainer
				
				'ns0:Container'{
					String containerType = brUtil.getContainerType(TP_ID, SCAC_CDE, container.Y2?.E24_04, conn)
					if (util.notEmpty(containerType) && container.Y2?.E95_01!="")
					{
						'ns0:CarrCntrSizeType' containerType
						'ns0:Quantity' container.Y2?.E95_01
					}
					def cargoWeight = 0
					currentTransaction.Loop_LX?.eachWithIndex { curCargo, curCargoIdx ->
						if (curCargo.N7?.E24_22 == container.Y2?.E24_04 && curCargo.N7?.E24_22!="") {
							cargoWeight = cargoWeight +  curCargo.L0.E81_04.toBigDecimal()
						}
						
					}
					if (cargoWeight!=0  && cargoWeight != null && extWeightUnitMap[currentTransaction.Loop_LX.first().L0.E188_11]!="") {
						'ns0:GrossWeight'{
							'ns1:Weight' cargoWeight.div(container.Y2?.E95_01.toInteger())
							'ns1:WeightUnit' extWeightUnitMap[currentTransaction.Loop_LX.first().L0?.E188_11]
						}
					}
					'ns0:NetWeight'{
						'ns1:Weight'
						'ns1:WeightUnit'
					}
					'ns0:IsSOC'
	
//					'ns0:OBDoor'{
//						'ns0:EmptyPickupDT'{
//							'ns1:LocDT' 
//						}
//	
//						'ns0:FullReturnDT'{
//							'ns1:LocDT'
//						}
//						'ns0:DoorAppointment'{
//							'ns1:AppointmentDT'{
//								'ns1:LocDT' 
//							}
//							'ns1:Address'{
//								'ns1:City'
//								'ns1:County'
//								'ns1:State'
//								'ns1:Country'
//								'ns1:LocationCode'{
//									'ns1:MutuallyDefinedCode'
//									'ns1:UNLocationCode'
//									'ns1:SchedKDType'
//									'ns1:SchedKDCode'
//								}
//								'ns1:PostalCode'
//								'ns1:AddressLines'{
//										//'ns1:AddressLine'
//								}
//								'ns1:Street'
//							}
//							'ns1:Company'
//							'ns1:Contact'{
//								'ns1:FirstName'
//								'ns1:LastName'
//								'ns1:ContactPhone'{
//									'ns1:CountryCode'
//									'ns1:AreaCode'
//									'ns1:Number'
//								}
//								'ns1:ContactFax'{
//									'ns1:CountryCode'
//									'ns1:AreaCode'
//									'ns1:Number'
//								}
//								'ns1:ContactEmailAddress'
//							}
//							'ns0:Quantity'
//						}
//					}
//					'ns0:IBDoor'{
//						'ns0:DestinationDT'{
//							'ns1:LocDT'
//						}
//					}
//	
					'ns0:Haulage'{
						
						'ns1:OutBound' extOutboundHaulageMap[currentTransaction.Y1?.E375_08]
						'ns1:InBound' extInboundHaulageMap[currentTransaction.Y1?.E375_08]
					}

					'ns0:Route'{
//						'ns0:IntendedDT'{
//							'ns0:From'{
//								'ns1:LocDT'
//							}
//							'ns0:To'{
//								'ns1:LocDT'
//							}
//							'ns0:IntendedRangeIndicator'
//						}
						
						R423 POR = currentTransaction.Loop_R4?.findAll{it.R4?.E115_01=="R"}.first().R4
						R423 FND = currentTransaction.Loop_R4?.findAll{it.R4?.E115_01=="E"}.last()?.R4
						R423 FirstPOL = currentTransaction.Loop_R4?.findAll{it.R4?.E115_01=="L"}.first()?.R4
						R423 LastPOD = currentTransaction.Loop_R4?.findAll{it.R4?.E115_01=="D"}.last()?.R4

						'ns0:POR'{
							'ns1:LocationName' POR.E114_04
							'ns1:CityDetails'{
								'ns1:City'
								'ns1:County'
								'ns1:State'
								'ns1:Country'
								'ns1:LocationCode'{
									'ns1:MutuallyDefinedCode'
									'ns1:UNLocationCode' POR.E310_03
									'ns1:SchedKDType'
									'ns1:SchedKDCode'
								}
							}
						}
						
						'ns0:FND'{
							'ns1:LocationName' FND.E114_04
							String FNDUNCde = null
							if (FND.E309_02=='DC') {
								FNDUNCde= util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, 'UNLocCdeConversion', FND.E310_03, conn)
							} else if (FND.E309_02=='UN') {
								FNDUNCde=FND.E310_03
							}
							'ns1:CityDetails'{
								'ns1:City'
								'ns1:County'
								'ns1:State'
								'ns1:Country'
								'ns1:LocationCode'{
									'ns1:MutuallyDefinedCode'
									'ns1:UNLocationCode' FNDUNCde
									'ns1:SchedKDType'
									'ns1:SchedKDCode'
								}
							}
						}
	
						'ns0:FirstPOL'{
							'ns1:Port'{
								'ns1:PortName' FirstPOL.E114_04
								'ns1:City'
								'ns1:County'
								'ns1:State'
								'ns1:LocationCode'{
									'ns1:MutuallyDefinedCode'
									'ns1:UNLocationCode' FirstPOL.E310_03
									'ns1:SchedKDType'
									'ns1:SchedKDCode'
									}
								'ns1:Country'
							}
							'ns0:LoadingPortVoyage'
							'ns0:LoadingPortVesselName'
						}
	
						'ns0:LastPOD'{
							'ns1:Port'{
								'ns1:PortName' LastPOD.E114_04
								'ns1:City'
								'ns1:County'
								'ns1:State'
								'ns1:LocationCode'{
									'ns1:MutuallyDefinedCode'
									'ns1:UNLocationCode' LastPOD.E310_03
									'ns1:SchedKDType'
									'ns1:SchedKDCode'
								}
								'ns1:Country'
							}
						}
						'ns0:LoadingPortLatestDepDT'{
									'ns1:LocDT' 
						}
						
						currentTransaction.V1?.eachWithIndex { currentV1, legIdx->
							R423 oceanLegPOL = currentTransaction.Loop_R4?.findAll{it.R4?.E115_01=="L"}[legIdx]?.R4
							R423 oceanLegPOD = currentTransaction.Loop_R4?.findAll{it.R4?.E115_01=="D"}[legIdx]?.R4
							V151 vesselInfo = currentV1
							
							'ns0:OceanLeg'{
								'ns0:LegSeq' legIdx+1
								'ns0:POL'{
									'ns1:Port'{
										'ns1:PortName' oceanLegPOL.E114_04
										'ns1:City'
										'ns1:County'
										'ns1:State'
										'ns1:LocationCode'{
											'ns1:MutuallyDefinedCode'
											'ns1:UNLocationCode' oceanLegPOL.E310_03
											'ns1:SchedKDType'
											'ns1:SchedKDCode'
										}
										'ns1:Country'
									}
								}
								'ns0:POD'{
									'ns1:Port'{
										'ns1:PortName' oceanLegPOD.E114_04
											'ns1:City'
											'ns1:County'
											'ns1:State'
											'ns1:LocationCode'{
												'ns1:MutuallyDefinedCode'
												'ns1:UNLocationCode' oceanLegPOD.E310_03
												'ns1:SchedKDType'
												'ns1:SchedKDCode'
											}
											'ns1:Country'
										}
									}
									'ns0:SVVD'{
										'ns1:Service'
										'ns1:Vessel'
										'ns1:VesselName' vesselInfo.E182_02
										'ns1:Voyage'
										'ns1:LloydsNumber'
										'ns1:CallSign'
										'ns0:ExternalVoyageNumber' vesselInfo.E55_04
									}
								}
							}
						
						}

					}
			}

			//(sum)  Cargo 	->begin
			currentTransaction.Loop_LX?.eachWithIndex {	currentCargo, cargoIdx ->
				Loop_LX29 cargo = currentCargo
				'ns0:Cargo'{
					'ns0:CargoInfo'{
						'ns1:CargoNature' 'GC'
						'ns1:CargoDescription' cargo.L5?.E79_02
						'ns1:Packaging'{
							'ns1:PackageType' brUtil.getPackageUnit(TP_ID, cargo.L0?.E211_09, conn)
							'ns1:PackageQty' cargo.L0?.E80_08
							'ns1:PackageMaterial'
						}
						'ns1:GrossWeight'{
							'ns1:Weight' cargo.L0?.E81_04
							'ns1:WeightUnit' extWeightUnitMap[cargo.L0?.E188_11]
						}
						'ns1:NetWeight'{
							'ns1:Weight'
							'ns1:WeightUnit'
						}
						'ns1:Volume'{
							'ns1:Volume' cargo.L0?.E183_06
							'ns1:VolumeUnit' extVolumeUnitMap[cargo.L0?.E184_07]
						}
						'ns0:Remarks'
						'ns0:HarmonizedTariffSchedule'
					}
					'ns0:ReeferCargoSpec'{
						'ns1:AtmosphereType'
						'ns1:Temperature'{
							'ns1:Temperature'
							'ns1:TemperatureUnit'
						}
						'ns1:Ventilation'{
							'ns1:Ventilation'
							'ns1:VentilationUnit'
						}
						'ns1:GensetType'
						'ns1:Remarks'
						'ns1:CO2'
						'ns1:O2'
						'ns1:VentSettingCode'
						'ns1:DehumidityPercentage'
						'ns1:SensitiveCargoDesc'
						'ns1:IsPreCoolingReq'
						'ns1:EmergencyContact'{
							'ns1:FirstName'
							'ns1:LastName'
							'ns1:ContactPhone'{
								'ns1:CountryCode'
								'ns1:AreaCode'
								'ns1:Number'
							}
							'ns1:ContactFax'{
								'ns1:CountryCode'
								'ns1:AreaCode'
								'ns1:Number'
							}
							'ns1:ContactEmailAddress'
							'ns1:Type'
						}
					}
					'ns0:DGCargoSpec'{
						'ns1:IMDGPage'
						'ns1:IMOClass'
						'ns1:UNNumber'
						'ns1:TechnicalName'
						'ns1:ProperShippingName'
						'ns1:PackageGroup'{
							'ns1:Code'
							'ns1:InnerPackageDescription'{
								'ns1:PackageType'
								'ns1:PackageQty'
							}
							'ns1:OuterPackageDescription'{
								'ns1:PackageType'
								'ns1:PackageQty'
							}
						}
						'ns1:MFAGNumber'
						'ns1:EMSNumber'
	
						'ns1:GrossWeight'{
							'ns1:Weight'
							'ns1:WeightUnit'
						}
						'ns1:NetWeight'{
							'ns1:Weight'
							'ns1:WeightUnit'
						}
						'ns1:FlashPoint'{
							'ns1:Temperature'
							'ns1:TemperatureUnit'
						}
						'ns1:ElevatedTemperature'{
							'ns1:Temperature'
							'ns1:TemperatureUnit'
						}
						'ns1:isLimitedQuantity'
						'ns1:IsInhalationHazardous'
						'ns1:IsReportableQuantity'
						'ns1:IsEmptyUnclean'
						'ns1:isMarinePollutant'
						'ns1:Remarks'
						'ns1:EmergencyContact'{
							'ns1:FirstName'
							'ns1:LastName'
							'ns1:ContactPhone'{
								'ns1:CountryCode'
								'ns1:AreaCode'
								'ns1:Number'
							}
							'ns1:ContactFax'{
								'ns1:CountryCode'
								'ns1:AreaCode'
								'ns1:Number'
							}
							'ns1:ContactEmailAddress'
							'ns1:Type'
						}
						'ns0:Description'
					}
					'ns0:AWCargoSpec'{
						'ns1:Height'{
							'ns1:Length'
							'ns1:LengthUnit'
						}
						'ns1:Length'{
							'ns1:Length'
							'ns1:LengthUnit'
						}
						'ns1:Width'{
							'ns1:Length'
							'ns1:LengthUnit'
						}
						'ns1:Remarks'
					}
					'ns0:TrafficMode'{
						'ns1:OutBound' 'FCL'
						'ns1:InBound' 'FCL'
					}
				}
			}
			
			'ns0:SpecialInstruction'{
				'ns0:SpecialHandling'{
					'ns1:Code'
					'ns1:Description'
				}
			}
			
			'ns0:Remarks' 
		}
	}


	public void buildBizKey(MarkupBuilder bizKeyXml, def output, def inputEDI, def txnErrorKeys){
		def bookingRequest = new XmlSlurper().parseText(output)
		EDI_LOWES_T300 input = inputEDI
		
		input.Loop_ST?.eachWithIndex { current_Body, current_BodyIndex ->
			bizKeyXml.'ns0:Transaction' ('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
				'ns0:ControlNumberInfo' {
					'ns0:Interchange' input.ISA?.I12_13
					'ns0:Group' input.GS?.E28_06
					'ns0:Transaction' current_Body.ST?.E329_02
				}
				
				// If no error in pre-validation
				if (txnErrorKeys[current_BodyIndex]?.findAll{it?.CAT == "PREP"}?.size() == 0) {
					def bizKey = brUtil.buildBizKey(bizKeyXml, bookingRequest.Request[current_BodyIndex], current_BodyIndex, txnErrorKeys[current_BodyIndex], TP_ID, conn)

					bizKey.each{ currentBizKeyMap ->
						currentBizKeyMap.each{ key, value ->
							String val = value
							if ( val==null || val.trim()=='') {
								//loop next item
								return
							}
							'ns0:BizKey'{
								'ns0:Type' key
								'ns0:Value' value
							}
						}
					}

					'ns0:CarrierId' //util.getCarrierID(bookingRequest.Request[current_BodyIndex].GeneralInformation?.SCAC.text(), conn)

				}// End-if-no-error-in-prevalidation
				
				'ns0:CTEventTypeId'
				
				// map validation error
				String Status = ''
				String errMsg = ''
				
				if (txnErrorKeys[current_BodyIndex]?.findAll{it.TYPE == "E" }?.size()>0) {
					Status = 'E'
					txnErrorKeys[current_BodyIndex]?.findAll{it.TYPE == "E"}?.VALUE?.eachWithIndex{ stringError, errorIndex ->
						errMsg = errMsg +  ' [Error ' + (errorIndex + 1) + '] : ' + stringError
						if (errorIndex != 0)
							errMsg = errMsg + ', '
					}
					
				} else if (txnErrorKeys[current_BodyIndex]?.findAll{it.TYPE == "O" }?.size()>0) {
					Status = 'O'
					txnErrorKeys[current_BodyIndex]?.findAll{it.TYPE == "O"}?.VALUE?.eachWithIndex{ stringError, errorIndex ->
						errMsg = errMsg +  ' [Error ' + (errorIndex + 1) + '] : ' + stringError
						if (errorIndex != 0)
							errMsg = errMsg + ', '
					}
				}
				
				
				
				if (txnErrorKeys[current_BodyIndex]?.findAll{it?.CAT != ""}?.size() > 0) {
					'ns1:AppErrorReport'('xmlns:ns1': 'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
						'ns1:Status' Status
						'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'    //max length is 20, exceed will missing error bizkey
						'ns1:Msg' errMsg.take(1000)
						'ns1:Severity' '5'
					}
				}
			}// End-Transaction
		}
	}
	
	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
		
		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		inputXmlBody = util.removeBOM(inputXmlBody)
		/**
		 * Part II: get app mapping runtime parameters
		 */
		this.conn = conn
		appSessionId = util.getRuntimeParameter("B2BSessionID", runtimeParams)
		sourceFileName = util.getRuntimeParameter("OriginalSourceFileName", runtimeParams)
		//pmt info
		TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams)
		MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams)
		DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams)
		MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams)
		
		/**
		 * Part III: read xml and prepare output xml
		 */

		XmlBeanParser xmlBeanParser = new XmlBeanParser()
		EDI_LOWES_T300 T300 = xmlBeanParser.xmlParser(inputXmlBody,EDI_LOWES_T300.class)

		def writer = new StringWriter()
		def outXml = new MarkupBuilder(writer) //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		
		def bizKeyWriter = new StringWriter()
		def bizKeyXml = new MarkupBuilder(bizKeyWriter)
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def currentSystemDt = new Date()
		
		/**
		 * Part IV: mapping script start from here
		 */
		//create root node
		def brRoot = outXml.createNode('ns0:BookingRequest', ['xmlns:ns0':"http://www.cargosmart.com/bookingrequest", 'xmlns:ns1' :"http://www.cargosmart.com/common"])
		def bizKeyRoot = bizKeyXml.createNode('root')
		
		def outputBodyCount = 0
		def headerGenerated = false
		List<Map<String,String>> txnErrorKeys = new ArrayList<Map<String,String>>()

		
		def bodies = T300.Loop_ST
		def transactionDT = ""
		
		transactionDT = util.substring(currentSystemDt.format("yyyy"), 1, 2)
	
		bodies.eachWithIndex { current_Body, current_BodyIndex ->
			// prep checking
			List<Map<String,String>> AppErrors = prepValidation(TP_ID, current_Body, current_BodyIndex, conn)
			
			// map Header once only
			if (!headerGenerated){
				
				brUtil.generateHeader(outXml, current_Body, outputBodyCount, currentSystemDt, DIR_ID, MSG_TYPE_ID, TP_ID, MSG_REQ_ID, current_Body.ST.E329_02) 
				headerGenerated = true
			}
			// map Body
			if (AppErrors.size()==0) {
				generateBody(outXml, current_Body, outputBodyCount, currentSystemDt, transactionDT, AppErrors)
			} else {
				outXml.'Request' {}
			}
			
			// add pre-validation error to error set
			txnErrorKeys.add(AppErrors)
		}
		
		// close mapping of CS2XML
		outXml.nodeCompleted(null,brRoot)
		String br = writer?.toString()
		
		//println(br)
		// do post-validation
		List<Map<String,String>> postErrors = brUtil.postValidation1(br, txnErrorKeys)
		// add post-validation error to error set
		bodies.eachWithIndex { current_Body, current_BodyIndex ->
			if (postErrors[current_BodyIndex])
				txnErrorKeys[current_BodyIndex].add(postErrors[current_BodyIndex])
		}
		
		// map Bizkey
		buildBizKey(bizKeyXml, br, T300, txnErrorKeys)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		
		String bizkeyXML = bizKeyWriter?.toString()
		println(bizkeyXML)
		
		
		writer.close()
		bizKeyWriter.close()
		
//		 println br
//		 println bizKeyWriter.toString()
		 
		// promote bizkey
		brUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter)
		def BizKey = new XmlSlurper().parseText(bizKeyWriter?.toString())

		boolean hasGoodBR = false
		BizKey.children().each { currentTransaction ->
			if (!(util.notEmpty(currentTransaction.AppErrorReport))) {
				hasGoodBR = true
			}
		}
	
		def nodeOutput = new XmlParser().parseText(br)
				
		if (hasGoodBR) {
			List removeBodies = new LinkedList()
			
			BizKey.children().eachWithIndex { currentTransaction, currentTransactionIndex ->
				def currentBody = nodeOutput.children().findAll {
						it.name().toString().contains('Request')
					}?.get(currentTransactionIndex)
				if ((util.notEmpty(currentTransaction.AppErrorReport))) {
					removeBodies.add(currentBody)
				}
			}
			
			removeBodies.each { currentBody ->
				nodeOutput.remove(currentBody)
			}
			
			String cleanedOutputXml = util.cleanXml(XmlUtil.serialize(nodeOutput))

			return cleanedOutputXml
		}
		else{
			// prepare Node version of BookingRequest
			Node emptyMsg = nodeOutput.clone()
			List kids= new LinkedList()
			//remove the <Header> and all <Body>
			emptyMsg.children().eachWithIndex {kid , index->
				kids.add(kid)
			}
			kids.eachWithIndex {kid , index->
					emptyMsg.remove(kid)
			}
			
			return XmlUtil.serialize(emptyMsg)
		}
	}
}
