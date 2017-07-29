package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.List
import java.util.Map;

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.edi.xml.si.CS.SIBillOfLading
import cs.b2b.core.mapping.bean.edi.xml.si.CS.SIBillOfLadingDistribution
import cs.b2b.core.mapping.bean.edi.xml.si.CS.ShippingInstructions
import cs.b2b.core.mapping.bean.edi.custom.iocm.IT0192;
import cs.b2b.core.mapping.bean.edi.xml.si.CS.BLInformation
import cs.b2b.core.mapping.bean.edi.xml.si.CS.Location
import cs.b2b.core.mapping.bean.edi.xml.si.CS.VesselVoyageInformation
import cs.b2b.core.mapping.bean.edi.xml.si.CS.PartyLocation
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.core.common.xmlvalidation.ValidateXML

public class CUS_SIXML_CS2SIXML_CSCUSSIXML {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil()
	cs.b2b.core.mapping.util.MappingUtil_SI_I_Common siUtil = new cs.b2b.core.mapping.util.MappingUtil_SI_I_Common(util)
	
	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	Connection conn = null
	
	// List of EDI Enum
	// ExtCode : IntCode
	List<String> certClauseTypeList = ["01", "02", "03", "04", "11", "20", "22", "24", "25", "34", "CC"]
	
	Map<String, String> extPartyTypeMap = [	"CA" : "CAR",
											"SH" : "SHP",
											"CN" : "CGN",
											"FW" : "FWD",
											"BR" : "BRK",
											"N1" : "NPT",
											"N2" : "ANP", 
											"BP" : "BPT", 
											'HI' : "SIR",
											"RP" : "WPA"	]
	
	Map<String, String> extBLDistributedRoleTypeMap = [	"CN"  : "CGN",
														"FW"  : "FWD",
														"N1"  : "NPT",
														"N2"  : "ANP",
														"SH"  : "SHP",
														"SI"  : "SIR"]
	
	Map<String, String> extRefMap = [	"SO" : "SO",
										"ON" : "PO",
										"FF" : "FR",
										"IV" : "INV",
										"CT" : "CTR",
										"CR" : "CR",
										"SI" : "SID",
										"BM" : "BL",
										"BN" : "BKG",
										"FI" : "FID",
										"RF" : "EXPR",
										"CG" : "CGO",
										"GN" : "GRN",
										"TN" : "ITN",
										"EX" : "EX",
										"PR" : "QUOT",
										"ZZ" : "OTH"	]
	
	Map<String, String> extRefDescMap = [	"SO" : "Shipper order number",
											"ON" : "Purchase order number",
											"FF" : "Forwarder's/Agent's reference",
											"IV" : "Invoice number",
											"CT" : "Contract Number",
											"CR" : "Customer Reference Number",
											"SI" : "Shipper's Identifying Number For Shipment (SID)",
											"BM" : "Bill of Lading Number",
											"BN" : "Booking Number",
											"FI" : "File Identifier",
											"RF" : "Export Reference Number",
											"CG" : "Consignee’s order number",
											"GN" : "Government Reference Number",
											"TN" : "Transaction Reference Number",
											"EX" : "Export Permit Number",
											"PR" : "Quotation Number",
											"ZZ" : "Others (Mutually Defined)"	]
	
	Map<String, String> extLengthUnitMap = ["C" : "C", "N" : "N", "F" : "F", "M" : "M"]
	Map<String, String> extWeightUnitMap = ["KGS" : "KGS", "LBS" : "LBS", "TON" : "TON"]
	Map<String, String> extVolumeUnitMap = ["CBF" : "CBF", "CBM" : "CBM"]
	Map<String, String> extTemperatureUnitMap = ["C" : "C", "F" : "F"]
	Map<String, String> extVentilationUnitMap = ["degrees" : "degrees", "cbfPerMin" : "cbfPerMin", "cbmPerHour" : "cbmPerHour", "percentage" : "percentage"]
	Map<String, String> extPaymentOptionMap = ["COLLECT" : "COLLECT", "MIXED" : "MIXED", "PREPAID" : "PREPAID"]
	Map<String, String> extBLTransmissionMap = ["ONLINE" : "ONLINE", "EDI" : "EDI", "ELECTRIONICALLY" : "EMAIL", "FAX" : "FAX", "HARDCOPY" : "HARDCOPY", "OTHER" : "OTHER"]
	Map<String, String> extDocTypeMap = ["OBL" : "OBL", "BC" : "CBL", "SWB" : "SWB"]
	Map<String, String> extFreightTypeMap = ['FreightedCollect':'FC', 'FreightedPrepaid':'FP', 'FreightedMixed':'FPC', 'NonFreighted':'NF']
	
	static final String SRC_FMT_ID = "SIXML"
	static final String TRG_FMT_ID = "CS2XML"
	static final String CCC_CONVERTTYPEID = "CustCode"
	static final String CONTAINER_CONVERTTYPEID = "ContainerType"
	static final String PACKAGE_CONVERTTYPEID = "CargoPackageType"
	static final String ISNEEDREPLYPARTYEMAIL_CONVERTTYPEID = "isNeedReplyPartyEmail"
	
	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMddHHmmss = "yyyyMMddHHmmss"
	def yyyyMMddHHmm = "yyyyMMddHHmm"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = "HHmm"

	def currentSystemDt = null
	
	private List prepValidation(def current_Body, def currentTransactionIndex) {
		SIBillOfLading currentSIBillOfLading = current_Body
		
		List preErrors = new ArrayList()
		
		if (util.notEmpty(currentSIBillOfLading.BLDetails?.UserReferences?.References)) {
			if (util.isEmpty(currentSIBillOfLading.BLDetails?.UserReferences?.References?.find{it.ReferenceType == "BN"})) {
				preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.UserReferences.References with ReferenceType = "BN"')
			}
		}
		
		if (util.notEmpty(currentSIBillOfLading.BLDetails?.LegalParties?.Party)) {
			currentSIBillOfLading.BLDetails?.LegalParties?.Party?.eachWithIndex { currentParty, currentParty_Index -> 
				if (util.isEmpty(currentParty.PartyName) && util.isEmpty(currentParty.CompanyID)){
					preErrors.add('At least either of PartyName or CompanyID should be provided for SIBillOfLading['+currentTransactionIndex+'].BLDetails.LegalParties.Party['+currentParty_Index+']')
				}
				if (util.isEmpty(currentParty.PartyLocation.CountryCode) && util.isEmpty(currentParty.PartyLocation.CountryName)){
					preErrors.add('At least either of CountryCode or CountryName should be provided for SIBillOfLading['+currentTransactionIndex+'].BLDetails.LegalParties.Party['+currentParty_Index+'].PartyLocation')
				}
				def contact = currentParty.ContactPerson.Phone
				if ((util.notEmpty(contact.AreaCode) || util.notEmpty(contact.CountryCode)) && util.isEmpty(contact.Number)){
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.LegalParties.Party['+currentParty_Index+'].ContactPerson.Phone.Number as AreaCode/CountryCode presents')
				}
				if (util.notEmpty(contact.CountryCode) && util.isEmpty(contact.Number)){
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.LegalParties.Party['+currentParty_Index+'].ContactPerson.Phone.CountryCode as Number presents')
				}
				contact = currentParty.ContactPerson.Fax
				if ((util.notEmpty(contact.AreaCode) || util.notEmpty(contact.CountryCode)) && util.isEmpty(contact.Number)){
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.LegalParties.Party['+currentParty_Index+'].ContactPerson.Fax.Number as AreaCode/CountryCode presents')
				}
				if (util.notEmpty(contact.CountryCode) && util.isEmpty(contact.Number)){
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.LegalParties.Party['+currentParty_Index+'].ContactPerson.Fax.CountryCode as Number presents')
				}
			}
			
			if (util.isEmpty(currentSIBillOfLading.BLDetails?.LegalParties?.Party?.find{it.PartyType == "SH"})) {
				preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.LegalParties.Party with PartyType = "SH"')
			}
			if (util.isEmpty(currentSIBillOfLading.BLDetails?.LegalParties?.Party?.find{it.PartyType == "HI"})) {
				preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.LegalParties.Party with PartyType = "HI"')
			}
		}
		
		if (util.notEmpty(currentSIBillOfLading.BLDetails?.RouteInformation)) {
			currentSIBillOfLading.BLDetails?.RouteInformation.Location.eachWithIndex{currentLocation, currentLocation_Index -> 
				if (util.isEmpty(currentLocation.LocationDetails.CountryCode) && util.isEmpty(currentLocation.LocationDetails.CountryName)){
					preErrors.add('At least either of CountryCode or CountryName should be provided for SIBillOfLading['+currentTransactionIndex+'].BLDetails.RouteInformation.Location['+currentLocation_Index+'].LocationDetails')
				}
			}
			if (util.isEmpty(currentSIBillOfLading.BLDetails?.RouteInformation?.Location?.find{it.FunctionCode == "POL"})) {
				preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.RouteInformation.Location with FunctionCode = "POL"')
			}
			if (util.isEmpty(currentSIBillOfLading.BLDetails?.RouteInformation?.Location?.find{it.PartyType == "POD"})) {
				preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.RouteInformation.Location with FunctionCode = "POD"')
			}
		}
		
		if (util.notEmpty(currentSIBillOfLading.BLDetails?.EquipmentInformation)) {
			currentSIBillOfLading.BLDetails?.EquipmentInformation?.Containers.eachWithIndex {currentContainer, currentContainer_Index ->
				if (util.notEmpty(currentContainer.ReeferSettings?.Ventilation) && util.isEmpty(currentContainer.ReeferSettings?.Ventilation?.attr_Units)) {
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.EquipmentInformation.Containers['+currentContainer_Index+'].ReeferSettings.Ventilation as Ventilation@Units presents')
				}
				else if (util.notEmpty(currentContainer.ReeferSettings?.Ventilation?.attr_Units) && util.isEmpty(currentContainer.ReeferSettings?.Ventilation)) {
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.EquipmentInformation.Containers['+currentContainer_Index+'].ReeferSettings.Ventilation@Units as Ventilation presents')
				}
				def contact = currentContainer.ReeferSettings?.EmergencyContact?.Phone
				if ((util.notEmpty(contact.AreaCode) || util.notEmpty(contact.CountryCode)) && util.isEmpty(contact.Number)){
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.EquipmentInformation.Containers['+currentContainer_Index+'].ReeferSettings.EmergencyContact.Phone.Number as AreaCode/CountryCode presents')
				}
				if (util.notEmpty(contact.CountryCode) && util.isEmpty(contact.Number)){
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.EquipmentInformation.Containers['+currentContainer_Index+'].ReeferSettings.EmergencyContact.Phone.CountryCode as Number presents')
				}
				contact = currentContainer.ReeferSettings?.EmergencyContact?.Fax
				if ((util.notEmpty(contact.AreaCode) || util.notEmpty(contact.CountryCode)) && util.isEmpty(contact.Number)){
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.EquipmentInformation.Containers['+currentContainer_Index+'].ReeferSettings.EmergencyContact.Fax.Number as AreaCode/CountryCode presents')
				}
				if (util.notEmpty(contact.CountryCode) && util.isEmpty(contact.Number)){
					preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.EquipmentInformation.Containers['+currentContainer_Index+'].ReeferSettings.EmergencyContact.Fax.CountryCode as Number presents')
				}
			}
			if (util.notEmpty(currentSIBillOfLading.BLDetails?.EquipmentInformation?.Containers?.find{it.Weight.findAll{it.attr_Qualifier == "VGM" && it.attr_Units == "TON"}?.size() > 0})) {
				preErrors.add('Invalid SIBillOfLading['+currentTransactionIndex+'].BLDetails.EquipmentInformation.Containers with Weight@Units = "TON" as Weight@Qualifier == "VGM"')
			}
			if (util.notEmpty(currentSIBillOfLading.BLDetails?.EquipmentInformation?.Containers?.find{it.Weight.findAll{it.attr_Qualifier == "VGM" && it.attr_Units == "TON"}?.size() > 0})) {
				preErrors.add('Invalid SIBillOfLading['+currentTransactionIndex+'].BLDetails.EquipmentInformation.Containers with Weight@Units = "TON" as Weight@Qualifier == "VGM"')
			}
		}
		
		if (util.notEmpty(currentSIBillOfLading.BLDetails?.CargoInformation)) {
			currentSIBillOfLading.BLDetails?.CargoInformation?.CargoItems.eachWithIndex {currentCargoItems, currentCargoItems_Index ->
				currentCargoItems.DangerousCargo.DangerousCargoInfo.eachWithIndex {currentDGCargo, currentDGCargo_Index ->
					if (util.isEmpty(currentDGCargo.HazardousMaterial.IMCOClass) && currentDGCargo.HazardousMaterial.DGRegulationString == "IMD") {
						preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.CargoInformation.CargoItems['+currentCargoItems_Index+'].DangerousCargo.DangerousCargoInfo.HazardousMaterial.IMCOClass as DGRegulationString = "IMD"')
					}
					currentDGCargo.EmergencyContact.eachWithIndex {currentContact, currentContact_Index ->
						def contact = currentContact.Phone
						if ((util.notEmpty(contact.AreaCode) || util.notEmpty(contact.CountryCode)) && util.isEmpty(contact.Number)){
							preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.CargoInformation.CargoItems['+currentCargoItems_Index+'].DangerousCargo.DangerousCargoInfo.EmergencyContact.Phone.Number as AreaCode/CountryCode presents')
						}
						if (util.notEmpty(contact.CountryCode) && util.isEmpty(contact.Number)){
							preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.CargoInformation.CargoItems['+currentCargoItems_Index+'].DangerousCargo.DangerousCargoInfo.EmergencyContact.Phone.CountryCode as Number presents')
						}
						contact = currentContact.Fax
						if ((util.notEmpty(contact.AreaCode) || util.notEmpty(contact.CountryCode)) && util.isEmpty(contact.Number)){
							preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.CargoInformation.CargoItems['+currentCargoItems_Index+'].DangerousCargo.DangerousCargoInfo.EmergencyContact.Fax.Number as AreaCode/CountryCode presents')
						}
						if (util.notEmpty(contact.CountryCode) && util.isEmpty(contact.Number)){
							preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.CargoInformation.CargoItems['+currentCargoItems_Index+'].DangerousCargo.DangerousCargoInfo.EmergencyContact.Fax.CountryCode as Number presents')
						}
					}// End-EmergencyContact
				}// End-DangerousCargoInfo
				currentCargoItems.AwkwardCargo.AwkwardCargoInfo.eachWithIndex {currentAWCargo, currentAWCargo_Index ->
					currentAWCargo.EmergencyContact.eachWithIndex {currentContact, currentContact_Index ->
						def contact = currentContact.Phone
						if ((util.notEmpty(contact.AreaCode) || util.notEmpty(contact.CountryCode)) && util.isEmpty(contact.Number)){
							preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.CargoInformation.CargoItems['+currentCargoItems_Index+'].DangerousCargo.DangerousCargoInfo.EmergencyContact.Phone.Number as AreaCode/CountryCode presents')
						}
						if (util.notEmpty(contact.CountryCode) && util.isEmpty(contact.Number)){
							preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.CargoInformation.CargoItems['+currentCargoItems_Index+'].DangerousCargo.DangerousCargoInfo.EmergencyContact.Phone.CountryCode as Number presents')
						}
						contact = currentContact.Fax
						if ((util.notEmpty(contact.AreaCode) || util.notEmpty(contact.CountryCode)) && util.isEmpty(contact.Number)){
							preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.CargoInformation.CargoItems['+currentCargoItems_Index+'].DangerousCargo.DangerousCargoInfo.EmergencyContact.Fax.Number as AreaCode/CountryCode presents')
						}
						if (util.notEmpty(contact.CountryCode) && util.isEmpty(contact.Number)){
							preErrors.add('Missing mandatory SIBillOfLading['+currentTransactionIndex+'].BLDetails.CargoInformation.CargoItems['+currentCargoItems_Index+'].DangerousCargo.DangerousCargoInfo.EmergencyContact.Fax.CountryCode as Number presents')
						}
					}// End-EmergencyContact
				}// End-AwkwardCargoInfo
			}// End-CargoItems
		}
		
		
		
		if (preErrors.size() == 0){
			preErrors.add("")
		}
		return preErrors
	}
	
	public void buildBizKey(MarkupBuilder bizKeyXml, def output, def inputEDI, def txnErrorKeys){
		def shippingInstruction = new XmlSlurper().parseText(output)
		ShippingInstructions input = inputEDI
		
		input.SIBillOfLading?.eachWithIndex { current_Body, current_BodyIndex ->
			bizKeyXml.'ns0:Transaction'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
				'ns0:ControlNumberInfo' {
					'ns0:Interchange' input.InterchangeControlHeader?.ControlNumber
					'ns0:Group' 
					'ns0:Transaction' current_Body.GeneralInfo?.TransactionInfo?.BatchNumber
				}
				// TransactionRefNumber
				if (util.notEmpty(current_Body.GeneralInfo?.SIReferenceNumber)){
					'ns0:BizKey' {	'ns0:Type' 'TransactionRefNumber'
									'ns0:Value' current_Body.GeneralInfo?.SIReferenceNumber}
				}
				// Message Reference Number (Reference Number)
				'ns0:BizKey' {	'ns0:Type' 'MSRN'
								'ns0:Value' MSG_REQ_ID	}
				
				// If no error in pre-validation
				if (txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.size() == 0) {
					// insert Biz-key by generated CS2XML which is common across different SI msg format
					def bizKey = siUtil.buildBizKey(bizKeyXml, shippingInstruction.Body[current_BodyIndex], current_BodyIndex, txnErrorKeys[current_BodyIndex], TP_ID, conn)

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

					if (util.notEmpty(shippingInstruction.Body[current_BodyIndex].GeneralInformation?.SCAC)){
						'ns0:CarrierId' util.getCarrierID(shippingInstruction.Body[current_BodyIndex].GeneralInformation?.SCAC.text(), conn)
					}
				}// End-if-no-error-in-prevalidation
				
				'ns0:CTEventTypeId'
				
				// map pre-validation error
				String errMsg = ''
				txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.eachWithIndex{ stringError, errorIndex ->
					errMsg = errMsg +  ' [Error ' + (errorIndex + 1) + '] : ' + stringError
					if (errorIndex != 0)
						errMsg = errMsg + '\n'
				}
				
				if (txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.size() > 0) {
					'ns1:AppErrorReport'('xmlns:ns1': 'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
						'ns1:Status' 'E'
						'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'    //max length is 20, exceed will missing error bizkey
						'ns1:Msg' errMsg.take(1000)
						'ns1:Severity' '5'
					}
				}
			}// End-Transaction
		}// End-Loop-input-Group_UNH
	}
	
	public void generateBody(MarkupBuilder outXml, def current_Body, def current_BodyIndex, def currentSystemDt, def transactionDT, def action, def owner) {
		SIBillOfLading current_SIBillOfLading = current_Body
		String SCAC_CDE = current_SIBillOfLading.GeneralInfo?.SCAC
		
		SimpleDateFormat sdf=new SimpleDateFormat(xmlDateTimeFormat)
		SimpleDateFormat sdf1=new SimpleDateFormat(yyyyMMddHHmmss)
		
		outXml.'Body'{
			'TransactionInformation'{
				'ns0:MessageID' MSG_REQ_ID + ',' + UUID.randomUUID()
				'ns0:InterchangeTransactionID' current_SIBillOfLading?.GeneralInfo?.TransactionInfo?.BatchNumber
			} //End-TransactionInformation
			
			'EventInformation'{
				'ns0:EventCode' 'si_submit'
				'ns0:EventDT'{
					'ns0:GMT' currentSystemDt.format(xmlDateTimeFormat) + '+08:00'
					'ns0:LocDT'
				}
			} //End-EventInformation
			
			'GeneralInformation'{
				'CustSIReferenceNumber' current_SIBillOfLading.GeneralInfo?.SIReferenceNumber
				if (util.notEmpty(action)){
					'ActionType' action
				}
				'SCAC' SCAC_CDE
				'Owner' owner
				'FMCNumber' current_SIBillOfLading.BLDetails?.LegalParties?.Party?.find{it.PartyType == "FW"}?.FMCNumber
				current_SIBillOfLading.BLDetails?.BookingInfo?.BookingNumber.each{ currentBN ->
					'BookingNumber' currentBN
				}
				
				def danger_exists = (current_SIBillOfLading.BLDetails?.CargoInformation?.CargoItems?.findAll{util.notEmpty(it.DangerousCargo)}?.size() > 0)
				def reefer_exists = (current_SIBillOfLading.BLDetails?.EquipmentInformation?.Containers?.findAll{util.notEmpty(it.ReeferSettings)}?.size() > 0)
				def awkward_exists = (current_SIBillOfLading.BLDetails?.CargoInformation?.CargoItems?.findAll{util.notEmpty(it.AwkwardCargo)}?.size() > 0)
				
				if(danger_exists &&	!awkward_exists && !reefer_exists){
					'ShipmentCargoType' 'DG'
				}
				else if(!danger_exists && !awkward_exists && reefer_exists){
					'ShipmentCargoType' 'RF'
				}
				else if(!danger_exists && awkward_exists && !reefer_exists){
					'ShipmentCargoType' 'AW'
				}
				else if(danger_exists && !awkward_exists && reefer_exists){
					'ShipmentCargoType' 'RD'
				}
				else if(danger_exists && awkward_exists && !reefer_exists){
					'ShipmentCargoType' 'AD'
				}
				else{
					'ShipmentCargoType' 'GC'
				}
				
				if (util.notEmpty(current_SIBillOfLading.BLDetails?.RouteInformation?.VesselVoyageInformation?.TrafficMode)){
					'ShipmentTrafficMode'{
						'ns0:OutBound' current_SIBillOfLading.BLDetails?.RouteInformation?.VesselVoyageInformation?.TrafficMode?.OutBound
						'ns0:InBound' current_SIBillOfLading.BLDetails?.RouteInformation?.VesselVoyageInformation?.TrafficMode?.InBound
						'TrafficModeDescription' current_SIBillOfLading.BLDetails?.RouteInformation?.VesselVoyageInformation?.TrafficMode?.Description	
					}// End-ShipmentTrafficMode
				}
				'IsOnlineBL' 'false'
				'CustomerTransactionDT'{
					'ns0:GMT'
					if (util.notEmpty(transactionDT)){
						def date = new SimpleDateFormat(yyyyMMddHHmmss).parse(transactionDT)
						def timezone = "HKT"
						if (util.notEmpty(current_SIBillOfLading?.GeneralInfo?.TransactionInfo?.DateCreated?.attr_TimeZone)){
							timezone = current_SIBillOfLading?.GeneralInfo?.TransactionInfo?.DateCreated?.attr_TimeZone
						}
						'ns0:LocDT' ('TimeZone': timezone, 'CSTimeZone': timezone, date.format(xmlDateTimeFormat) + '+08:00')
					}
				}
			} //End-GeneralInformation

			current_SIBillOfLading?.BLDetails?.LegalParties?.Party?.each{currentParty ->
				def CSPartyType = extPartyTypeMap[currentParty.PartyType]
				if (util.notEmpty(CSPartyType)){
					'Party'{
						'ns0:PartyType' CSPartyType
						'ns0:PartyName' currentParty?.PartyName
						'ns0:CSCompanyID' currentParty?.CompanyID
						
						String CCC_ExtCde = currentParty.CarrierCustomerCode
						if (util.notEmpty(CCC_ExtCde)){
							String CCC_Code_Conversion = util.getConversionWithScacByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, CCC_CONVERTTYPEID, CCC_ExtCde, SCAC_CDE, conn)
							'ns0:CarrierCustomerCode' CCC_Code_Conversion?CCC_Code_Conversion:CCC_ExtCde
						}
						
						String triggerEmail = util.getEDICdeRef(TP_ID, ISNEEDREPLYPARTYEMAIL_CONVERTTYPEID, DIR_ID, SRC_FMT_ID, 'isNeedReplyPartyEmail', 'isNeedReplyPartyEmail', CSPartyType, conn)
						if (util.notEmpty(triggerEmail)){
							'ns0:isNeedReplyPartyEmail' 'true'
						}
						
						if(currentParty?.ContactPerson){
							'ns0:Contact'{
								'ns0:FirstName' currentParty?.ContactPerson.FirstName
								'ns0:LastName' currentParty?.ContactPerson.LastName
								if(currentParty?.ContactPerson?.Phone){
									'ns0:ContactPhone'{
										'ns0:CountryCode' currentParty?.ContactPerson?.Phone?.CountryCode
										'ns0:AreaCode' currentParty?.ContactPerson?.Phone?.AreaCode
										'ns0:Number' currentParty?.ContactPerson?.Phone?.Number
									}
								}
								if(currentParty?.ContactPerson?.Fax){
									'ns0:ContactFax'{
										'ns0:CountryCode' currentParty?.ContactPerson?.Fax?.CountryCode
										'ns0:AreaCode' currentParty?.ContactPerson?.Fax?.AreaCode
										'ns0:Number' currentParty?.ContactPerson?.Fax?.Number
									}
								}
								'ns0:ContactEmailAddress' currentParty?.ContactPerson?.Email
							}
						}
						int lastIdx = 0
						
						if(currentParty?.PartyLocation){
							'ns0:Address'{
								'ns0:City' currentParty?.PartyLocation?.City
								'ns0:County' currentParty?.PartyLocation?.County
								'ns0:State' currentParty?.PartyLocation?.StateProvince?currentParty?.PartyLocation?.StateProvince:currentParty?.PartyLocation?.StateProvinceCode
								'ns0:Country' currentParty?.PartyLocation?.CountryName?currentParty?.PartyLocation?.CountryName:currentParty?.PartyLocation?.CountryCode
								if(currentParty?.PartyLocation?.LocationCode){
									'ns0:LocationCode'{
										'ns0:MutuallyDefinedCode' currentParty?.PartyLocation?.LocationCode?.MutuallyDefinedCode
										'ns0:UNLocationCode' currentParty?.PartyLocation?.LocationCode?.UNLocationCode
										if (util.notEmpty(currentParty?.PartyLocation?.LocationCode?.SchedKDCode?.attr_Type)){
											'ns0:SchedKDType' currentParty?.PartyLocation?.LocationCode?.SchedKDCode?.attr_Type
										}
										'ns0:SchedKDCode' currentParty?.PartyLocation?.LocationCode?.SchedKDCode
									}
								}
								'ns0:PostalCode' currentParty?.PartyLocation?.PostalCode
								if(util.notEmpty(currentParty?.PartyLocation?.Address?.AddressLines)){
									'ns0:AddressLines'{
										lastIdx = currentParty?.PartyLocation?.Address?.AddressLines?.size() > 4?4:currentParty?.PartyLocation?.Address?.AddressLines?.size()
										currentParty?.PartyLocation?.Address?.AddressLines?.subList(0, lastIdx)?.each{ currentAddressLine ->
											if (util.notEmpty(currentAddressLine)){
												'ns0:AddressLine' currentAddressLine
											}
										}
									}
								}
							} //End-Party-Address
						}
						List<String> partyTextList = new ArrayList()
						if(util.notEmpty(currentParty?.PartyName)){
							partyTextList.add(currentParty?.PartyName)
						}
						partyTextList.addAll(currentParty?.PartyLocation?.Address?.AddressLines.subList(0, lastIdx))
						String partyText = ""
						partyTextList.each {currentE ->
							if (util.notEmpty(partyText)){
								partyText = util.notEmpty(currentE)?partyText + "\n" + currentE:partyText+"\n"
							}
							else{
								partyText = util.notEmpty(currentE)?currentE:"\n"
							}
						}
						'PartyText' partyText
						'EORINumber' currentParty?.EORINumber
					} //End Party
				}
			} //End-LegalParties.Party Looping
			
			def mappedSID = false
			current_SIBillOfLading?.BLDetails?.UserReferences?.References.each { currentReference ->
				if (util.notEmpty(extRefMap[currentReference?.ReferenceType])){
					'ExternalReference'{
						'ns0:CSReferenceType' extRefMap[currentReference?.ReferenceType]
						if (currentReference?.ReferenceType == "SI"){
							'ns0:ReferenceNumber' current_SIBillOfLading.GeneralInfo?.SIReferenceNumber
							mappedSID = true
						}
						else {
							'ns0:ReferenceNumber' currentReference?.ReferenceNumber
						}
						if (util.notEmpty(currentReference?.ReferenceDescription)){
							'ns0:ReferenceDescription' currentReference?.ReferenceDescription
						}
						else {
							'ns0:ReferenceDescription' extRefDescMap[currentReference?.ReferenceType]
						}
						'EDIReferenceCode' currentReference?.ReferenceType
					}// End-ExternalReference
				}
			}// End-UserReferences Looping
			
			if (! mappedSID){
				'ExternalReference'{
					'ns0:CSReferenceType' extRefMap['SI']
					'ns0:ReferenceNumber' current_SIBillOfLading.GeneralInfo?.SIReferenceNumber
					'ns0:ReferenceDescription' extRefDescMap['SI']
				}
			}
			
			current_SIBillOfLading.BLDetails?.EquipmentInformation?.Containers.eachWithIndex{currentContainer, currentContainer_index ->
				'Container'{
					'AssociatedBookingNumber' currentContainer.AssociatedBookingNumber
					'ContainerNumber' currentContainer.ContainerNumber
					'ContainerCheckDigit' currentContainer?.ContainerNumber.attr_CheckDigit
					
					def ContainerType_ExtCde = currentContainer.containerType
					def ContainerType_IntCde = util.getConversionWithScacByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, CONTAINER_CONVERTTYPEID, ContainerType_ExtCde, SCAC_CDE, conn)
					'CarrCntrSizeType' ContainerType_IntCde?ContainerType_IntCde:ContainerType_ExtCde
					
					def currentContainer_GWT = currentContainer.Weight?.findAll{it.attr_Qualifier == "GROSS"}
					if (currentContainer_GWT?.size() > 0){
						'GrossWeight'{
							'ns0:Weight' currentContainer_GWT[0]
							'ns0:WeightUnit' extWeightUnitMap[currentContainer_GWT[0].attr_Units]
						}
					}
					
					def currentContainer_NWT = currentContainer.Weight?.findAll{it.attr_Qualifier == "NET"}
					if (currentContainer_NWT?.size() > 0){
						'NetWeight'{
							'ns0:Weight' currentContainer_NWT[0]
							'ns0:WeightUnit' extWeightUnitMap[currentContainer_NWT[0].attr_Units]
						}
					}

					if (util.notEmpty(currentContainer.Volume)){
						'ContainerVolume'{
							'ns0:Volume' currentContainer.Volume
							'ns0:VolumeUnit' extVolumeUnitMap[currentContainer.Volume.attr_Units]
						}
					}
					
					if(util.notEmpty(currentContainer.TrafficMode)){
						'TrafficMode'{
								'ns0:OutBound' currentContainer.TrafficMode?.OutBound
								'ns0:InBound' currentContainer.TrafficMode?.InBound
						}
					}
					
					currentContainer.SealNumber?.findAll{it.attr_Type == "CA" || it.attr_Type == "SH"}.each{currentSealNumber ->
						'Seal'{
							'ns0:SealType' currentSealNumber.attr_Type
							'ns0:SealNumber' currentSealNumber
							if (util.notEmpty(currentSealNumber.attr_Name)){
								'SealTypeName' currentSealNumber.attr_Name
							}
							else {
								switch (currentSealNumber.attr_Type){
									case "CA":
										'SealTypeName' "Carrier"
										break
									case "SH":
										'SealTypeName' "Shipper"
										break
								}
							}
						}
					}// End-SealNumber Looping
					
					'Indicators'{
						if(currentContainer.Indicators?.SOCIndicator=='1'){
							'SOCIndicator' 'true'
						}else{
							'SOCIndicator' 'false'
						}
						if(currentContainer.Indicators?.PerishableGoods=='1'){
							'PerishableGoods' 'true'
						}else{
							'PerishableGoods' 'false'
						}
						if(currentContainer.Indicators?.DangerousGoods=='1'){
							'DangerousGoods' 'true'
						}else{
							'DangerousGoods' 'false'
						}
						if(currentContainer.Indicators?.PersonalEffect=='1'){
							'PersonalEffect' 'true'
						}else{
							'PersonalEffect' 'false'
						}
						if(currentContainer.Indicators?.Timber=='1'){
							'Timber' 'true'
						}else{
							'Timber' 'false'
						}
						if(currentContainer.Indicators?.Flammable=='1'){
							'Flammable' 'true'
						}else{
							'Flammable' 'false'
						}
						if(currentContainer.Indicators?.Fumigation=='1'){
							'Fumigation' 'true'
						}else{
							'Fumigation' 'false'
						}
						if(currentContainer.Indicators?.MultipleBL=='1'){
							'MultipleBL' 'true'
						}else{
							'MultipleBL' 'false'
						}
						if(currentContainer.Indicators?.LoadEmptyStatus=='1'){
							'LoadEmptyStatus' 'true'
						}else{
							'LoadEmptyStatus' 'false'
						}
					}
					
					if (util.notEmpty(current_SIBillOfLading.BLDetails?.RouteInformation?.VesselVoyageInformation?.Haulage)){
						'Haulage'{
							switch (current_SIBillOfLading.BLDetails?.RouteInformation?.VesselVoyageInformation?.Haulage){
								case "Door-to-Door": 
									'ns0:OutBound' "C"
									'ns0:InBound' "C"
									break;
								case "Door-to-Pier":
									'ns0:OutBound' "C"
									'ns0:InBound' "M"
									break;
								case "Pier-to-Door":
									'ns0:OutBound' "M"
									'ns0:InBound' "C"
									break;
								case "Pier-to-Pier":
									'ns0:OutBound' "M"
									'ns0:InBound' "M"
									break;
							}
						}// End-Haulage
					}

					'ContainerSequenceID' 'EDI'+ currentSystemDt.format(yyyyMMddHHmmss)+ String.format("%03d",(currentContainer_index + 1))
					
					if(util.notEmpty(currentContainer.ReeferSettings)){
						'ReeferCargoSpec'{
							if (currentContainer.ReeferSettings?.attr_AtmosphereType == "CA"){
								'ns0:AtmosphereType' "CA"
							}
							'ns0:Temperature'{
								'ns0:Temperature' currentContainer.ReeferSettings?.Temperature
								'ns0:TemperatureUnit' extTemperatureUnitMap[currentContainer.ReeferSettings?.Temperature?.attr_Units]
							}
	
							'ns0:Ventilation'{
								'ns0:Ventilation' currentContainer.ReeferSettings?.Ventilation
								'ns0:VentilationUnit'  extVentilationUnitMap[currentContainer.ReeferSettings?.Ventilation?.attr_Units]
							}
							'ns0:GensetType' currentContainer.ReeferSettings?.attr_GenSetType
							String remarkLine = ""
							currentContainer.ReeferSettings?.Remarks?.RemarksLines?.each{ currentRemarksLine ->
								remarkLine = remarkLine + currentRemarksLine
							}
							
							def remarkLines = currentContainer.ReeferSettings?.Remarks?.RemarksLines
							if (remarkLines?.size() > 0){
								'ns0:Remarks' util.substring(remarkLines.join(" "), 1, 4000)
							}
							
							'ns0:CO2' currentContainer.ReeferSettings?.attr_CO2
							'ns0:O2' currentContainer.ReeferSettings?.attr_O2
							'ns0:VentSettingCode' currentContainer.ReeferSettings?.attr_VentSettingCode
							'ns0:DehumidityPercentage' currentContainer.ReeferSettings?.attr_DehumidityInd
							'ns0:SensitiveCargoDesc' currentContainer.ReeferSettings?.SensitiveCargoDesc
							'ns0:IsPreCoolingReq' currentContainer.ReeferSettings?.attr_PreCooling
							
							if (util.notEmpty(currentContainer.ReeferSettings?.EmergencyContact)){
								def emergencyContact = currentContainer.ReeferSettings?.EmergencyContact
								'ns0:EmergencyContact'{
									'ns0:FirstName' emergencyContact.FirstName
									'ns0:LastName' emergencyContact.LastName
									if(util.notEmpty(emergencyContact.Phone)){
										'ns0:ContactPhone'{
											'ns0:CountryCode' emergencyContact.Phone?.CountryCode
											'ns0:AreaCode' emergencyContact.Phone?.AreaCode
											'ns0:Number' emergencyContact.Phone?.Number
										}
									}
									if(util.notEmpty(emergencyContact.Fax)){
										'ns0:ContactFax'{
											'ns0:CountryCode' emergencyContact.Fax?.CountryCode
											'ns0:AreaCode' emergencyContact.Fax?.AreaCode
											'ns0:Number' emergencyContact.Fax?.Number
										}
									}
									if(util.notEmpty(emergencyContact.Email)){
										'ns0:ContactEmailAddress' emergencyContact.Email
									}
									if(util.notEmpty(emergencyContact.Type)){
										'ns0:Type' emergencyContact.Type?.toUpperCase()
									}
								}// End-EmergencyContact
							}
						}// End-ReeferCargoSpec
					}
					
					def currentContainer_VWT = currentContainer.Weight?.findAll{it.attr_Qualifier == "VGM"}
					if (currentContainer_VWT?.size() > 0){
						'VGMWeight'{
							'ns0:Weight' currentContainer_VWT[0]
							'ns0:WeightUnit' extWeightUnitMap[currentContainer_VWT[0].attr_Units]
						}// End-VGMWeight
					}
					
					if (util.notEmpty(currentContainer.Indicators?.VGMMethod)){
						'VGMMethod' currentContainer.Indicators?.VGMMethod
					}
					
					def currentContainer_party = null
					def weightingParty = current_SIBillOfLading.BLDetails?.LegalParties?.Party?.findAll{it.PartyType == "RP"}
					currentContainer_party = weightingParty.find{it.ContainerNumber == currentContainer.ContainerNumber && it.ContainerNumber.attr_CheckDigit == currentContainer.ContainerNumber.attr_CheckDigit}
					if (util.isEmpty(currentContainer_party)){
						currentContainer_party = weightingParty[0]
					}
					
					if (util.notEmpty(currentContainer_party)){
						'WeightPartyName' currentContainer_party.PartyName
						'WeightPartyAuthorizedPerson' currentContainer_party.Signature
						
						if(currentContainer_party?.ContactPerson){
							'WeightPartyContactInfo'{
								'ns0:FirstName' currentContainer_party?.ContactPerson.FirstName
								'ns0:LastName' currentContainer_party?.ContactPerson.LastName
								if(currentContainer_party?.ContactPerson?.Phone){
									'ns0:ContactPhone'{
										'ns0:CountryCode' currentContainer_party?.ContactPerson?.Phone?.CountryCode
										'ns0:AreaCode' currentContainer_party?.ContactPerson?.Phone?.AreaCode
										'ns0:Number' currentContainer_party?.ContactPerson?.Phone?.Number
									}
								}
								if(currentContainer_party?.ContactPerson?.Fax){
									'ns0:ContactFax'{
										'ns0:CountryCode' currentContainer_party?.ContactPerson?.Fax?.CountryCode
										'ns0:AreaCode' currentContainer_party?.ContactPerson?.Fax?.AreaCode
										'ns0:Number' currentContainer_party?.ContactPerson?.Fax?.Number
									}
								}
								if(currentContainer_party?.ContactPerson?.Email){
									'ns0:ContactEmailAddress' currentContainer_party?.ContactPerson?.Email
								}
							} //End-WeightPartyContactInfo
						}
						
						if(currentContainer_party?.PartyLocation){
							'WeightPartyAddress'{
								'ns0:City' currentContainer_party?.PartyLocation?.City
								'ns0:County' currentContainer_party?.PartyLocation?.County
								'ns0:State' currentContainer_party?.PartyLocation?.StateProvince?currentContainer_party?.PartyLocation?.StateProvince:currentContainer_party?.PartyLocation?.StateProvinceCode
								'ns0:Country' currentContainer_party?.PartyLocation?.CountryName?currentContainer_party?.PartyLocation?.CountryName:currentContainer_party?.PartyLocation?.CountryCode
								if(currentContainer_party?.PartyLocation?.LocationCode){
									'ns0:LocationCode'{
										'ns0:MutuallyDefinedCode' currentContainer_party?.PartyLocation?.LocationCode?.MutuallyDefinedCode
										'ns0:UNLocationCode' currentContainer_party?.PartyLocation?.LocationCode?.UNLocationCode
										if (util.notEmpty(currentContainer_party?.PartyLocation?.LocationCode?.SchedKDCode?.attr_Type)){
											'ns0:SchedKDType' currentContainer_party?.PartyLocation?.LocationCode?.SchedKDCode?.attr_Type
										}
										'ns0:SchedKDCode' currentContainer_party?.PartyLocation?.LocationCode?.SchedKDCode
									}// End-LocationCode
								}
								'ns0:PostalCode' currentContainer_party?.PartyLocation?.PostalCode
								if(util.notEmpty(currentContainer_party?.PartyLocation?.Address)){
									'ns0:AddressLines'{
										currentContainer_party?.PartyLocation?.Address?.AddressLines?.each{ currentAddressLine ->
											if (util.notEmpty(currentAddressLine)){
												'ns0:AddressLine' currentAddressLine
											}
										}
									}// End-AddressLines
								}
							} //End-Party-Address
						}
					}// End-Container-Weighting Party
				}// End-Container
			} //End-Container Looping
			
			//cargo	->start
			current_SIBillOfLading.BLDetails?.CargoInformation?.CargoItems?.each{currentCargoItem->
				'Cargo'{
					'CargoInfo'{
						'CargoNature' 	currentCargoItem.CargoNature
						'CargoDescription' currentCargoItem?.CargoDescription?.DescriptionLine.join("\n")
						
						'Packaging'{
							def Package_ExtCde = currentCargoItem.Package?.attr_Type
							def Package_IntCde = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, PACKAGE_CONVERTTYPEID, Package_ExtCde, conn)
							'ns0:PackageType' Package_IntCde?Package_IntCde:Package_ExtCde
							'ns0:PackageQty' currentCargoItem.Package
							'ns0:PackageDesc' currentCargoItem.PackageDescription
							'ns0:PackageMaterial' currentCargoItem.PackageMaterial
						}
						
						def currentCargo_GWT = currentCargoItem.Weight?.findAll{it.attr_Qualifier == "GROSS"}
						if (currentCargo_GWT?.size() > 0){
							'GrossWeight'{
								'ns0:Weight' currentCargo_GWT[0]
								'ns0:WeightUnit' extWeightUnitMap[currentCargo_GWT[0].attr_Units]
							}
						}
						
						def currentCargo_NWT = currentCargoItem.Weight?.findAll{it.attr_Qualifier == "NET"}
						if (currentCargo_NWT?.size() > 0){
							'NetWeight'{
								'ns0:Weight' currentCargo_NWT[0]
								'ns0:WeightUnit' extWeightUnitMap[currentCargo_NWT[0].attr_Units]
							}
						}
						
						if (util.notEmpty(currentCargoItem.Volume)){
							'Volume'{
								'ns0:Volume' currentCargoItem.Volume
								'ns0:VolumeUnit' extVolumeUnitMap[currentCargoItem.Volume.attr_Units]
							}
						}
						
						currentCargoItem?.MarksAndNumbers?.MarksAndNumbersLine.eachWithIndex {currentMarksAndNumber, currentMarksAndNumber_index ->
							'MarksAndNumbers'{
								'ns0:SeqNumber' currentMarksAndNumber_index + 1
								'ns0:MarksAndNumbersLine' currentMarksAndNumber
							}
						}

						'CargoRemarks' currentCargoItem.Remarks

						currentCargoItem.HarmonizedTariffSchedule.eachWithIndex { currentHS, currentHS_index->
							'HarmonizedTariffSchedule'{
								'SeqNumber' currentHS_index + 1
								'HarmonizedCode' currentHS
							}
						}
						if (currentCargoItem.CargoDetails?.size() > 0){
							'AssociatedBookingNumber' currentCargoItem.CargoDetails[0].AssociatedBookingNumber
						}
					}

					currentCargoItem.DangerousCargo?.DangerousCargoInfo.each {currentDangerousCargoInfo->
						'DGCargoSpec'{
							'ns0:DGRegulator' 	currentDangerousCargoInfo.HazardousMaterial?.DGRegulationString
							'ns0:IMDGPage'   	currentDangerousCargoInfo.HazardousMaterial?.IMCOPage
							'ns0:IMOClass'       currentDangerousCargoInfo.HazardousMaterial?.IMCOClass
							'ns0:UNNumber' 		currentDangerousCargoInfo.HazardousMaterial?.UNNumber
							'ns0:TechnicalName'  currentDangerousCargoInfo.hazardousMaterial?.TechnicalShippingName
							'ns0:ProperShippingName' currentDangerousCargoInfo.HazardousMaterial?.ProperShippingName
							'ns0:PackageGroup'{
								'ns0:Code' 	currentDangerousCargoInfo.HazardousMaterial?.Package?.PackagingGroupCode
								
								'ns0:InnerPackageDescription'{
									def Package_ExtCde = currentDangerousCargoInfo.HazardousMaterial?.Package?.InnerPackage?.attr_Type
									def Package_IntCde = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, PACKAGE_CONVERTTYPEID, Package_ExtCde, conn)
									'ns0:PackageType' Package_IntCde?Package_IntCde:Package_ExtCde
									'ns0:PackageQty' currentDangerousCargoInfo.HazardousMaterial?.Package?.InnerPackage
									'ns0:PackageDesc'
									'ns0:PackageMaterial'
								}
								'ns0:OuterPackageDescription'{
									def Package_ExtCde = currentDangerousCargoInfo.HazardousMaterial?.Package?.OuterPackage?.attr_Type
									def Package_IntCde = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, PACKAGE_CONVERTTYPEID, Package_ExtCde, conn)
									'ns0:PackageType' Package_IntCde?Package_IntCde:Package_ExtCde
									'ns0:PackageQty' currentDangerousCargoInfo.HazardousMaterial?.Package?.OuterPackage
									'ns0:PackageDesc'
									'ns0:PackageMaterial'
								}
							}
							'ns0:MFAGNumber' currentDangerousCargoInfo.HazardousMaterial?.MFAGTableNumber
							'ns0:EMSNumber' ''
							'ns0:PSAClass' ''
							'ns0:ApprovalCode' ''
							
							def currentDGCargo_GWT = currentDangerousCargoInfo.HazardousMaterial?.Weight?.findAll{it.attr_Qualifier == "GROSS"}
							if (currentDGCargo_GWT?.size() > 0){
								'ns0:GrossWeight'{
									'ns0:Weight' currentDGCargo_GWT[0]
									'ns0:WeightUnit' extWeightUnitMap[currentDGCargo_GWT[0].attr_Units]
								}
							}
							
							def currentDGCargo_NWT = currentDangerousCargoInfo.HazardousMaterial?.Weight?.findAll{it.attr_Qualifier == "NET"}
							if (currentDGCargo_NWT?.size() > 0){
								'ns0:NetWeight'{
									'ns0:Weight' currentDGCargo_NWT[0]
									'ns0:WeightUnit' extWeightUnitMap[currentDGCargo_NWT[0].attr_Units]
								}
							}
							
							def currentDGCargo_NExplosiveWT = currentDangerousCargoInfo.HazardousMaterial?.Weight?.findAll{it.attr_Qualifier == "NET Explosive"}
							if (currentDGCargo_NExplosiveWT?.size() > 0){
								'ns0:NetExplosiveWeight'{
									'ns0:Weight' currentDGCargo_NExplosiveWT[0]
									'ns0:WeightUnit' extWeightUnitMap[currentDGCargo_NExplosiveWT[0].attr_Units]
								}
							}
							
							'ns0:FlashPoint'{
								'ns0:Temperature' currentDangerousCargoInfo.HazardousMaterial?.FlashPoint
								'ns0:TemperatureUnit' extTemperatureUnitMap[currentDangerousCargoInfo.HazardousMaterial?.FlashPoint?.attr_Units]
							}
							'ns0:ElevatedTemperature'{
								'ns0:Temperature' currentDangerousCargoInfo.HazardousMaterial?.DGElevationTemperature
								'ns0:TemperatureUnit' extTemperatureUnitMap[ currentDangerousCargoInfo.HazardousMaterial?.DGElevationTemperature?.attr_Units]
							}
							'ns0:isLimitedQuantity'	''
							'ns0:IsInhalationHazardous'  currentDangerousCargoInfo.HazardousMaterial?.Indicators_hazar?.isInhalationHazardous
							'ns0:IsReportableQuantity' 	 currentDangerousCargoInfo.HazardousMaterial?.Indicators_hazar?.ReportableQuantityIndicator
							'ns0:IsEmptyUnclean' 		currentDangerousCargoInfo.HazardousMaterial?.Indicators_hazar?.isEmptyUnclean
							'ns0:isMarinePollutant' ''
							'ns0:State' ''
							'ns0:Label' ''
							'ns0:Remarks' currentDangerousCargoInfo.HazardousMaterial?.Remarks?.RemarksLines?.join(" ")

							currentDangerousCargoInfo.EmergencyContact.each {currentEmergencyContact->
								'ns0:EmergencyContact'{
									'ns0:FirstName' currentEmergencyContact.FirstName
									'ns0:LastName' currentEmergencyContact.LastName
									if (util.notEmpty(currentEmergencyContact.Phone)){
										'ns0:ContactPhone'{
											'ns0:CountryCode' currentEmergencyContact.Phone?.CountryCode
											'ns0:AreaCode' 	currentEmergencyContact.Phone?.AreaCode
											'ns0:Number' 	currentEmergencyContact.Phone?.Number
										}
									}
									if (util.notEmpty(currentEmergencyContact.Fax)){
										'ns0:ContactFax'{
											'ns0:CountryCode' currentEmergencyContact.Fax?.CountryCode
											'ns0:AreaCode' 	currentEmergencyContact.Fax?.AreaCode
											'ns0:Number' 	currentEmergencyContact.Fax?.Number
										}
									}
									'ns0:ContactEmailAddress' currentEmergencyContact.Email
									'ns0:Type' currentEmergencyContact.Type.toUpperCase()
								}
							}
							'HazardousClassificationCode' currentDangerousCargoInfo.HazardousMaterial?.hazardousClassificationCode
							'HazardousReference' 	currentDangerousCargoInfo.HazardousMaterial?.HazardousReference
							'MaterialDescription'  currentDangerousCargoInfo.HazardousMaterial?.MaterialDescription
							'MaterialClassification' currentDangerousCargoInfo.HazardousMaterial?.MaterialClassification
							'EmergencyScheduleReference' currentDangerousCargoInfo.HazardousMaterial?.EmergencyScheduleReference
							'EmergencyResponseCode'  currentDangerousCargoInfo.HazardousMaterial?.EmergencyResponseCode
							'EmergencySchedulePageNumber' currentDangerousCargoInfo.HazardousMaterial?.EmergencySchedulePageNumber
							'LimitedQuantityDeclaration'  currentDangerousCargoInfo.HazardousMaterial?.Indicators_hazar?.limitedQuantityDeclaration
							'MarinePollutantInformation' currentDangerousCargoInfo.HazardousMaterial?.Indicators_hazar?.marinePollutantInformation
							'MedicalFirstAidGuidePageNumber' currentDangerousCargoInfo.HazardousMaterial?.MFAGPageNumber
							'IsResidue' currentDangerousCargoInfo.HazardousMaterial?.Indicators_hazar?.isResidue
							'IsShipsideDelivery' currentDangerousCargoInfo.HazardousMaterial?.Indicators_hazar?.isShipsideDelivery
						}
					}

					currentCargoItem.AwkwardCargo?.AwkwardCargoInfo.each {currentAwkwardCargoInfo->
						'AWCargoSpec'{
							'ns0:Height'{
								'ns0:Length' currentAwkwardCargoInfo.AwkwardCargoDetails?.Height
								'ns0:LengthUnit' extLengthUnitMap[currentAwkwardCargoInfo.AwkwardCargoDetails?.Height?.attr_Units]
							}
							'ns0:Length'{
								'ns0:Length'  currentAwkwardCargoInfo.AwkwardCargoDetails?.Length
								'ns0:LengthUnit'  extLengthUnitMap[currentAwkwardCargoInfo.AwkwardCargoDetails?.Length?.attr_Units]
							}
							'ns0:Width'{
								'ns0:Length' currentAwkwardCargoInfo.AwkwardCargoDetails?.Width
								'ns0:LengthUnit' extLengthUnitMap[currentAwkwardCargoInfo.AwkwardCargoDetails?.Width?.attr_Units]
							}
							'ns0:GrossWeight'{
								'ns0:Weight' 
								'ns0:WeightUnit' 
							}
							'ns0:IsShipsideDelivery'
							'ns0:Remarks' currentAwkwardCargoInfo.AwkwardCargoDetails?.Remarks?.RemarksLines.join(" ")

							currentAwkwardCargoInfo.EmergencyContact.each {currentEmergencyContact->
								'ns0:EmergencyContact'{
									'ns0:FirstName' currentEmergencyContact.FirstName
									'ns0:LastName'  currentEmergencyContact.LastName
									if (util.notEmpty(currentEmergencyContact.Phone)){
										'ns0:ContactPhone'{
											'ns0:CountryCode' currentEmergencyContact.Phone?.CountryCode
											'ns0:AreaCode' currentEmergencyContact.Phone?.AreaCode
											'ns0:Number' currentEmergencyContact.Phone?.Number
										}
									}
									if (util.notEmpty(currentEmergencyContact.Fax)){
										'ns0:ContactFax'{
											'ns0:CountryCode' currentEmergencyContact.Fax.CountryCode
											'ns0:AreaCode' currentEmergencyContact.Fax.AreaCode
											'ns0:Number' currentEmergencyContact.Fax.Number
										}
									}
									'ns0:ContactEmailAddress' currentEmergencyContact.Email
									'ns0:Type' currentEmergencyContact.Type
								}
							}
						}
					}
					
					currentCargoItem.CargoDetails?.each {currentCargoDetails->
						'ContainerLoadPlan'{
							'Package'{
								def Package_ExtCde = currentCargoDetails.Package?.attr_Type
								def Package_IntCde = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, PACKAGE_CONVERTTYPEID, Package_ExtCde, conn)
								'ns0:PackageType' Package_IntCde?Package_IntCde:Package_ExtCde
								'ns0:PackageQty' currentCargoDetails.Package
								'ns0:PackageDesc' currentCargoDetails.PackageDescription
								'ns0:PackageMaterial' currentCargoDetails.PackageMaterial
							}
							
							def currentCargoDetail_GWT = currentCargoDetails.Weight?.findAll{it.attr_Qualifier == "GROSS"}
							if (currentCargoDetail_GWT?.size() > 0){
								'GrossWeight'{
									'ns0:Weight' currentCargoDetail_GWT[0]
									'ns0:WeightUnit' extWeightUnitMap[currentCargoDetail_GWT[0].attr_Units]
								}
							}
							
							def currentCargoDetail_NWT = currentCargoDetails.Weight?.findAll{it.attr_Qualifier == "NET"}
							if (currentCargoDetail_NWT?.size() > 0){
								'NetWeight'{
									'ns0:Weight' currentCargoDetail_NWT[0]
									'ns0:WeightUnit' extWeightUnitMap[currentCargoDetail_NWT[0].attr_Units]
								}
							}
							if (util.notEmpty(currentCargoDetails.Volume)){
								'Volume'{
									'ns0:Volume' currentCargoDetails.Volume
									'ns0:VolumeUnit' extVolumeUnitMap[currentCargoDetails.Volume?.attr_Units]
								}
							}
							
							def currentCargo_linkingCTN_index = current_SIBillOfLading.BLDetails?.EquipmentInformation?.Containers?.findIndexOf{it.ContainerNumber?.ContainerNumber == currentCargoDetails.ContainerNumber?.ContainerNumber && it.ContainerNumber?.attr_CheckDigit == currentCargoDetails.ContainerNumber?.attr_CheckDigit}
							'ContainerSequenceID' 'EDI' + currentSystemDt.format(yyyyMMddHHmmss)+ String.format("%03d",(currentCargo_linkingCTN_index + 1))
						}
					}
				}
			}

			BLInformation BLInformation = current_SIBillOfLading?.SummaryDetails?.BLInformation
			'BLInfo'{
				'BLNumber' BLInformation?.BLNumber
				if (util.notEmpty(extPaymentOptionMap[BLInformation.PaymentStatus])){
					'PaymentOptionForOceanFreight' extPaymentOptionMap[BLInformation.PaymentStatus]
				}
				if (util.notEmpty(BLInformation?.DraftInformation)){
					'IsDraftRequired' 'true'
					'DraftBLDistribution'{
						if (util.notEmpty(extBLDistributedRoleTypeMap[BLInformation?.DraftInformation?.Recipient?.attr_Type])){
							'BLDistributedRole' extBLDistributedRoleTypeMap[BLInformation?.DraftInformation?.Recipient?.attr_Type]
						}
						if (util.notEmpty(extBLTransmissionMap[BLInformation?.DraftInformation?.ModeOfTransmission])){
							'TransmissionMode' extBLTransmissionMap[BLInformation?.DraftInformation?.ModeOfTransmission]
						}
						if(BLInformation?.DraftInformation?.FaxNumber){
							'Fax'{
								'ns0:CountryCode' BLInformation?.DraftInformation?.FaxNumber?.CountryCode
								'ns0:AreaCode'  BLInformation?.DraftInformation?.FaxNumber?.AreaCode
								'ns0:Number'  BLInformation?.DraftInformation?.FaxNumber?.Number
							}
						}
					}
				}
				else {
					'IsDraftRequired' 'false'
				}
				
				'FinalCopyBLDistribution'{
					BLInformation?.SIDistribution?.SIBillOfLadingDistribution?.each{currentSIBillOfLadingDistribution ->
						if (util.notEmpty(extDocTypeMap[currentSIBillOfLadingDistribution.attr_DocType])){
							'DistributionDetails'{
								'ns0:DocumentType' extDocTypeMap[currentSIBillOfLadingDistribution.attr_DocType]
								if (util.notEmpty(extFreightTypeMap[currentSIBillOfLadingDistribution.attr_FreightType])){
									'ns0:FreightType' extFreightTypeMap[currentSIBillOfLadingDistribution.attr_FreightType]
								}
								'ns0:NumberOfCopies' currentSIBillOfLadingDistribution.NumberOfCopies
								if (util.notEmpty(extBLDistributedRoleTypeMap[currentSIBillOfLadingDistribution.PartyRole])){
									'ns0:BLDistributedRole' extBLDistributedRoleTypeMap[currentSIBillOfLadingDistribution.PartyRole]
								}
							}
						}
					}
					current_SIBillOfLading?.SummaryDetails?.PaperWork?.RequestedDocuments?.each{currentRequestedDocuments ->
						if (util.notEmpty(extDocTypeMap[currentRequestedDocuments.attr_DocumentType])){
							'DistributionDetails'{
								'ns0:DocumentType' extDocTypeMap[currentRequestedDocuments.attr_DocumentType]
								if (util.notEmpty(extFreightTypeMap[currentRequestedDocuments.attr_FreightType])){
									'ns0:FreightType' extFreightTypeMap[currentRequestedDocuments.attr_FreightType]
								}
								'ns0:NumberOfCopies' currentRequestedDocuments.NoOfCopies
								'ns0:BLDistributedRole' 'SIR'
							}
						}
					}
					if (util.notEmpty(extBLTransmissionMap[BLInformation?.OriginalBL?.ModeOfTransmission])){
						'BLTransmissionMode' extBLTransmissionMap[BLInformation?.OriginalBL?.ModeOfTransmission]
					}
					'BLOtherTransmissionInfo' BLInformation?.OriginalBL?.OtherInformation
				}
				'SummaryBLCargoDesc' BLInformation?.BLCargoDescription
				current_SIBillOfLading.SummaryDetails?.Certifications?.CertificationClauseText?.each{currentCertificationClauseText ->
					'SICertClause'{
						if (certClauseTypeList.contains(currentCertificationClauseText.attr_Code)){
							'ns0:CertificationClauseType' currentCertificationClauseText.attr_Code
						}
						'ns0:CertificationClauseText' currentCertificationClauseText
					}
				}
				if (util.notEmpty(extDocTypeMap[BLInformation?.attr_BLType])){
					'BLType' extDocTypeMap[BLInformation?.attr_BLType]
				}
				if (util.notEmpty(extFreightTypeMap[BLInformation?.attr_FreightType])){
					'BLFreightType' extFreightTypeMap[BLInformation?.attr_FreightType]
				}
			} //End-BLInfo
			
			Location locPOR = current_SIBillOfLading?.BLDetails?.RouteInformation?.Location?.find{it.FunctionCode == 'POR'}
			Location locPOL = current_SIBillOfLading?.BLDetails?.RouteInformation?.Location?.find{it.FunctionCode == 'POL'}
			Location locPOD = current_SIBillOfLading?.BLDetails?.RouteInformation?.Location?.find{it.FunctionCode == 'POD'}
			Location locFND = current_SIBillOfLading?.BLDetails?.RouteInformation?.Location?.find{it.FunctionCode== 'FND'}

			'Route'{
				if (util.notEmpty(locPOR)){
					'POR'{
						'ns0:LocationName' locPOR?.LocationName
						'ns0:CityDetails'{
							'ns0:City' locPOR?.LocationDetails?.City
							'ns0:County' locPOR?.LocationDetails?.County
							'ns0:State' locPOR?.LocationDetails?.StateProvince?locPOR?.LocationDetails?.StateProvince:locPOR?.LocationDetails?.StateProvinceCode
							'ns0:Country' locPOR?.LocationDetails?.CountryName?locPOR?.LocationDetails?.CountryName:locPOR?.LocationDetails?.CountryCode
							if(util.notEmpty(locPOR?.LocationDetails?.LocationCode)){
								'ns0:LocationCode'{
									'ns0:MutuallyDefinedCode' locPOR?.LocationDetails?.LocationCode?.MutuallyDefinedCode
									'ns0:UNLocationCode' locPOR?.LocationDetails?.LocationCode?.UNLocationCode
									if (util.notEmpty(locPOR?.LocationDetails?.LocationCode?.SchedKDCode?.attr_Type)){
										'ns0:SchedKDType' locPOR?.LocationDetails?.LocationCode?.SchedKDCode?.attr_Type
									}
									'ns0:SchedKDCode' locPOR?.LocationDetails?.LocationCode?.SchedKDCode
								}
							}
						}
						'BLText' locPOR?.LocationName
					}
				}
				if (util.notEmpty(locFND)){
					'FND'{
						'ns0:LocationName' locFND?.LocationName
						'ns0:CityDetails'{
							'ns0:City' locFND?.LocationDetails?.City
							'ns0:County' locFND?.LocationDetails?.County
							'ns0:State' locFND?.LocationDetails?.StateProvince?locFND?.LocationDetails?.StateProvince:locFND?.LocationDetails?.StateProvinceCode
							'ns0:Country' locFND?.LocationDetails?.CountryName?locFND?.LocationDetails?.CountryName:locFND?.LocationDetails?.CountryCode
							if(util.notEmpty(locFND?.LocationDetails?.LocationCode)){
								'ns0:LocationCode'{
									'ns0:MutuallyDefinedCode' locFND?.LocationDetails?.LocationCode?.MutuallyDefinedCode
									'ns0:UNLocationCode' locFND?.LocationDetails?.LocationCode?.UNLocationCode
									if (util.notEmpty(locFND?.LocationDetails?.LocationCode?.SchedKDCode?.attr_Type)){
										'ns0:SchedKDType' locFND?.LocationDetails?.LocationCode?.SchedKDCode?.attr_Type
									}
									'ns0:SchedKDCode' locFND?.LocationDetails?.LocationCode?.SchedKDCode
								}
							}
						}
						'BLText' locFND?.LocationName
					}
				}
				
				if (util.notEmpty(locPOL)){
					'FirstPOL'{
						'Port'{
							'ns0:PortName' locPOL?.LocationName
							'ns0:City' locPOL?.LocationDetails?.City
							'ns0:County' locPOL?.LocationDetails?.County
							'ns0:State' locPOL?.LocationDetails?.StateProvince?locPOL?.LocationDetails?.StateProvince:locPOL?.LocationDetails?.StateProvinceCode
							if(locPOL?.LocationDetails?.LocationCode){
								'ns0:LocationCode'{
									'ns0:MutuallyDefinedCode' locPOL?.LocationDetails?.LocationCode?.MutuallyDefinedCode
									'ns0:UNLocationCode' locPOL?.LocationDetails?.LocationCode?.UNLocationCode
									if (util.notEmpty(locPOL?.LocationDetails?.LocationCode?.SchedKDCode?.attr_Type)){
										'ns0:SchedKDType' locPOL?.LocationDetails?.LocationCode?.SchedKDCode?.attr_Type
									}
									'ns0:SchedKDCode' locPOL?.LocationDetails?.LocationCode?.SchedKDCode
								}
							}
							'ns0:Country' locPOL?.LocationDetails?.CountryName?locPOL?.LocationDetails?.CountryName:locPOL?.LocationDetails?.CountryCode
						}
						'BLText' locPOL?.LocationName
					}
				}

				if (util.notEmpty(locPOD)){
					'LastPOD'{
						'Port'{
							'ns0:PortName' locPOD?.LocationName
							'ns0:City' locPOD?.LocationDetails?.City
							'ns0:County' locPOD?.LocationDetails?.County
							'ns0:State' locPOD?.LocationDetails?.StateProvince?locPOD?.LocationDetails?.StateProvince:locPOD?.LocationDetails?.StateProvinceCode
							if(locPOD?.LocationDetails?.LocationCode){
								'ns0:LocationCode'{
									'ns0:MutuallyDefinedCode' locPOD?.LocationDetails?.LocationCode?.MutuallyDefinedCode
									'ns0:UNLocationCode' locPOD?.LocationDetails?.LocationCode?.UNLocationCode
									if (util.notEmpty(locPOD?.LocationDetails?.LocationCode?.SchedKDCode?.attr_Type)){
										'ns0:SchedKDType' locPOD?.LocationDetails?.LocationCode?.SchedKDCode?.attr_Type
									}
									'ns0:SchedKDCode' locPOD?.LocationDetails?.LocationCode?.SchedKDCode
								}
							}
							'ns0:Country' locPOD?.LocationDetails?.CountryName?locPOD?.LocationDetails?.CountryName:locPOD?.LocationDetails?.CountryCode
						}
						'BLText' locPOD?.LocationName
					}
				}
				
				VesselVoyageInformation vesselVoyageInformation = current_SIBillOfLading?.BLDetails?.RouteInformation?.VesselVoyageInformation
				if(vesselVoyageInformation){
					'SVVD'{
						'ns0:Service' vesselVoyageInformation?.ServiceName?.attr_Code						
						if(vesselVoyageInformation?.VesselInformation){
							'ns0:Vessel' vesselVoyageInformation?.VesselInformation?.VesselCode
							'ns0:VesselName' vesselVoyageInformation?.VesselInformation?.VesselName
						}
						if(vesselVoyageInformation?.VoyageNumberDirection?.length() == 4){
							'ns0:Voyage' util.substring(vesselVoyageInformation?.VoyageNumberDirection, 1, 3)
							'ns0:Direction' util.substring(vesselVoyageInformation?.VoyageNumberDirection, 4, 1)
						}else{
							'ns0:Voyage' vesselVoyageInformation?.VoyageNumberDirection
						}
						'ns0:LloydsNumber' vesselVoyageInformation?.VesselInformation?.VesselCode.attr_LloydsCode
						'ns0:CallSign' vesselVoyageInformation?.VesselInformation?.VesselCode.attr_CallSign
						'ns0:CallNumber'
						'ns0:VesselNationality' vesselVoyageInformation?.VesselInformation?.VesselRegistrationCountry
						if (vesselVoyageInformation?.External?.VesselCode?.attr_Type == "C" | vesselVoyageInformation?.External?.VesselCode?.attr_Type == "L")
							'ExternalVesselType' vesselVoyageInformation?.External?.VesselCode?.attr_Type
						'ExternalVesselCode' vesselVoyageInformation?.External?.VesselCode
						'ExternalVesselNumber' vesselVoyageInformation?.External?.VoyageNumber
						'VesselVoyageText' vesselVoyageInformation?.VesselVoyageText
					}
					'PreCarriage' vesselVoyageInformation?.PreCarriage
				}// End-SVVD
			}// End-Route
			
			current_SIBillOfLading?.SummaryDetails?.BLInformation?.SpecialInstructions?.SpecialHandling.each {currentSpecialHandling->
				'SpecialHandling' {
					if (util.notEmpty(currentSpecialHandling.attr_Code)){
						'ns0:Code' currentSpecialHandling.attr_Code
					}
					'ns0:Description' currentSpecialHandling
				}// End-SpecialHandling
			}
			
			current_SIBillOfLading?.SummaryDetails?.Charges.each{ currentCharge ->
				'ChargeInformation'{
					'ChargeDetails'{
						'ChargeCode' currentCharge.attr_Code
						'ChargeType' currentCharge.attr_Type
						'ChargeCategory' currentCharge.attr_Category
						'ChargeItem' currentCharge.attr_SubCategory
						if (util.notEmpty(currentCharge.PayableAt_charge)){
							'PayableAt' currentCharge.PayableAt_charge
						}
					}// End-ChargeDetails
					
					if (util.notEmpty(extPartyTypeMap[currentCharge.PaidBy?.PartyType])){
						'PayableBy'{
							'ns0:PartyType' extPartyTypeMap[currentCharge.PaidBy?.PartyType]
							'ns0:PartyName'currentCharge.PaidBy?.PartyName
							'ns0:CSCompanyID'currentCharge.PaidBy?.CompanyID
							
							String CCC_ExtCde =currentCharge.PaidBy?.CarrierCustomerCode
							if (util.notEmpty(CCC_ExtCde)){
								String CCC_Code_Conversion = util.getConversionWithScacByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, CCC_CONVERTTYPEID, CCC_ExtCde, SCAC_CDE, conn)
								'ns0:CarrierCustomerCode' CCC_Code_Conversion?CCC_Code_Conversion:CCC_ExtCde
							}
							if (util.notEmpty(currentCharge.PaidBy?.ContactPerson)){
								'ns0:Contact'{
									'ns0:FirstName'currentCharge.PaidBy?.ContactPerson?.FirstName
									'ns0:LastName' currentCharge.PaidBy?.ContactPerson?.LastName
									if(currentCharge.PaidBy?.ContactPerson?.Phone){
										'ns0:ContactPhone'{
											'ns0:CountryCode' currentCharge.PaidBy?.ContactPerson?.Phone?.CountryCode
											'ns0:AreaCode' currentCharge.PaidBy?.ContactPerson?.Phone?.AreaCode
											'ns0:Number' currentCharge.PaidBy?.ContactPerson?.Phone?.Number
										}
									}
									if(currentCharge.PaidBy?.ContactPerson?.Fax){
										'ns0:ContactFax'{
											'ns0:CountryCode' currentCharge.PaidBy?.ContactPerson?.Fax?.CountryCode
											'ns0:AreaCode' currentCharge.PaidBy?.ContactPerson?.Fax?.AreaCode
											'ns0:Number' currentCharge.PaidBy?.ContactPerson?.Fax?.Number
										}
									}
									'ns0:ContactEmailAddress' currentCharge.PaidBy?.ContactPerson?.Email
								}// End-Contact
							}
							if (util.notEmpty(currentCharge.PaidBy?.PartyLocation)){
								'ns0:Address'{
									'ns0:City' currentCharge.PaidBy?.PartyLocation?.City
									'ns0:County' currentCharge.PaidBy?.PartyLocation?.County
									'ns0:State' currentCharge.PaidBy?.PartyLocation?.StateProvince?currentCharge.PaidBy?.PartyLocation?.StateProvince:currentCharge.PaidBy?.PartyLocation?.StateProvinceCode
									'ns0:Country' currentCharge.PaidBy?.PartyLocation?.CountryName?currentCharge.PaidBy?.PartyLocation?.CountryName:currentCharge.PaidBy?.PartyLocation?.CountryCode
									if (util.notEmpty(currentCharge.PaidBy?.PartyLocation?.LocationCode)){
										'ns0:LocationCode'{
											'ns0:MutuallyDefinedCode' currentCharge.PaidBy?.PartyLocation?.LocationCode?.MutuallyDefinedCode
											'ns0:UNLocationCode' currentCharge.PaidBy?.PartyLocation?.LocationCode?.UNLocationCode
											if (util.notEmpty(currentCharge.PaidBy?.PartyLocation?.LocationCode?.SchedKDCode?.attr_Type)){
												'ns0:SchedKDType'  currentCharge.PaidBy?.PartyLocation?.LocationCode?.SchedKDCode?.attr_Type
											}
											'ns0:SchedKDCode'  currentCharge.PaidBy?.PartyLocation?.LocationCode?.SchedKDCode
										}
									}
									'ns0:PostalCode' currentCharge.PaidBy?.PartyLocation?.PostalCode
									'ns0:AddressLines'{
										'ns0:AddressLine' currentCharge.PaidBy?.PartyLocation?.Street
									}
								}// End-Address
							}
						}// End-PayableBy
					}
				}// End-ChargeInformation
			}
			
			'Remarks'{
				current_SIBillOfLading?.SummaryDetails?.BLInformation?.Remarks?.RemarksLines?.each{ currentRemarksLines-> 
					'ns0:RemarkLine' currentRemarksLines
				}
			}
			
			'EDIDynamicStructure'{
				'NewStructure'{
					'ParentStructureName' "FREETEXT"
					'NewElementName' SCAC_CDE
					'NewElementValue' "M+N"
				}// End-EDIDynamicStructure-NewStructure
				
				current_SIBillOfLading?.BLDetails?.CargoInformation?.CargoItems?.each { currentCargoItems ->
					'NewStructure'{
						'ParentStructureName' "ShippingInstruction/Body/Cargo/CargoInfo"
						'NewElementName' "IsCargoInfo"
						'NewElementValue' "Y"
					}// End-EDIDynamicStructure-NewStructure
				}
			}// End-EDIDynamicStructure
		}
	}
	
	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn){
		
		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		inputXmlBody = util.removeBOM(inputXmlBody)
		inputXmlBody = siUtil.replaceEUChars(inputXmlBody)

		/**
		 * Part II: get app mapping runtime parameters
		 */
		this.conn = conn
		appSessionId = util.getRuntimeParameter("B2BSessionID", runtimeParams)
		sourceFileName = util.getRuntimeParameter("B2B_OriginalSourceFileName", runtimeParams)
		//pmt info
		TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams)
		MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams)
		DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams)
		MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams)

		/**
		 * Part III: read xml and prepare output xml
		 */
		//Parse the xmlBody to JavaBean
		XmlBeanParser xmlBeanParser = new XmlBeanParser()
		ShippingInstructions shippingInstructions = xmlBeanParser.xmlParser(inputXmlBody, ShippingInstructions.class)

		def writer = new StringWriter()
		def outXml = new MarkupBuilder(writer) //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		
		def bizKeyWriter = new StringWriter()
		def bizKeyXml = new MarkupBuilder(bizKeyWriter)
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		
		def processAckWriter = new StringWriter()
		def processAckXml = new MarkupBuilder(processAckWriter)
		processAckXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def currentSystemDt = new Date()
		
		/**
		 * Part IV: mapping script start from here
		 */
		//create Start root node to Xml
		def namespace = ['xmlns':"http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0':"http://www.cargosmart.com/common"]
		def ShippingInstruction = outXml.createNode('ShippingInstruction',namespace)
		
		def bizKeyRoot = bizKeyXml.createNode('root')
		def processAckRoot = processAckXml.createNode('root')
		
		def headerGenerated = false
		def bodies = shippingInstructions.SIBillOfLading
		
		List txnErrorKeys = new ArrayList()
		// do inbound XML schema validation
		txnErrorKeys = siUtil.xmlXSDValidation('CUS-SI-CS-XML', inputXmlBody)
		
		bodies.eachWithIndex { current_Body, current_BodyIndex ->
			def action = current_Body.GeneralInfo?.ActionType
			def owner = current_Body.GeneralInfo?.OwnedBy
			def transactionDT = current_Body.GeneralInfo?.TransactionInfo?.DateCreated.toString()
			
			List preErrors = prepValidation(current_Body, current_BodyIndex)
			// map Header once only
			if (!headerGenerated){
				siUtil.generateHeader(outXml, current_Body, current_BodyIndex, currentSystemDt, action, DIR_ID, MSG_TYPE_ID, TP_ID, MSG_REQ_ID)
				headerGenerated = true
			}
			if (preErrors?.findAll{it != ""}?.size() == 0){
				generateBody(outXml, current_Body, current_BodyIndex, currentSystemDt, transactionDT, action, owner)
			}
			else {
				outXml.'Body'{}
			}
			txnErrorKeys[current_BodyIndex].addAll(preErrors)
		}
		
		outXml.nodeCompleted(null,ShippingInstruction)
		String si = writer?.toString()
		
		// do outBound XML schema validation & post-validation
		List xmlErrors = new ArrayList()
		
		xmlErrors = siUtil.xmlXSDValidation('CS2-CUS-SIXML', si, txnErrorKeys, siUtil.&postValidation)
		
		xmlErrors.eachWithIndex { current_Error, current_ErrorIndex ->
			txnErrorKeys[current_ErrorIndex].addAll(current_Error)
		}
		
		// map Bizkey
		buildBizKey(bizKeyXml, si, shippingInstructions, txnErrorKeys)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)

		writer.close()
		bizKeyWriter.close()
		
		//println si
		println bizKeyWriter.toString()
		 
		// promote bizkey
		siUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter)
		def BizKey = new XmlSlurper().parseText(bizKeyWriter?.toString())

		boolean hasGoodSI = false
		BizKey.children().each { currentTransaction ->
			if (!(util.notEmpty(currentTransaction.AppErrorReport))) {
				hasGoodSI = true
			}
		}
		
		def nodeOutput = new XmlParser().parseText(si)
		
		if (hasGoodSI) {
			List removeBodies = new LinkedList()
			
			BizKey.children().eachWithIndex { currentTransaction, currentTransactionIndex ->
				def currentBody = nodeOutput.children().findAll {
						it.name().toString().contains('Body')
					}?.get(currentTransactionIndex)
				if ((util.notEmpty(currentTransaction.AppErrorReport))) {
					removeBodies.add(currentBody)
				}
				else{
					if (util.notEmpty(currentBody.GeneralInformation?.SCAC)){
						processAckXml = siUtil.prepareACK(TP_ID, MSG_REQ_ID, DIR_ID, MSG_TYPE_ID, currentBody, currentTransaction, processAckXml, conn)
					}
				}
			}
			
			processAckXml.nodeCompleted(null,processAckRoot)
			processAckWriter.close()
			//println processAckWriter.toString()
			
			siUtil.promoteAckUploadToSession(appSessionId, processAckWriter)

			removeBodies.each { currentBody ->
				nodeOutput.remove(currentBody)
			}
			
			String cleanedOutputXml = util.cleanXml(XmlUtil.serialize(nodeOutput))

			return cleanedOutputXml
		}
		else{
			// prepare Node version of ShippingInstruction
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
