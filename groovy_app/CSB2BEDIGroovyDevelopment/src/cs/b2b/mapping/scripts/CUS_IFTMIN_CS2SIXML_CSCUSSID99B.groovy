package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.HashMap

import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.EDI_IFTMIN
import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.Group37_EQD417
import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.Group_UNH13
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * IG		: SI CS Std IFTMIN D99B
 * Version	: 8.0
 */
public class CUS_IFTMIN_CS2SIXML_CSCUSSID99B {
	
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
	
	Map<String, String> extDocTypeMap = ["705" : "OBL", "707" : "CBL", "710" : "SWB", "714" : "HBL"]
	
	Map<String, String> extCarrierRateMap = [	"CT" : "SC"  ]
	
	// Excode : Incode
	Map<String, String> extRefMap = [	"BM" : "BL",
										"BN" : "BKG",
										"CG" : "CGO",
										"CR" : "CR",
										"CT" : "CTR",
										"EX" : "EX",
										"FF" : "FR",
										"FI" : "FID",
										"IV" : "INV",
										"ON" : "PO",
										"PR" : "PR",
										"RF" : "EXPR",
										"SI" : "SID",
										"SO" : "SO",
										"TN" : "ITN",
										"ZZ" : "OTH",
										"BH" : "BH" ]
	Map<String, String> extRefDescMap = [	"BM" : "Bill of lading number",
											"BN" : "Booking reference number",
											"CG" : "Consignee's order number",
											"CR" : "Customer reference number",
											"CT" : "Contract number",
											"EX" : "Export licence number",
											"FF" : "Freight forwarder's reference number",
											"FI" : "File line identifier",
											"IV" : "Invoice number",
											"ON" : "Order number (purchase)",
											"PR" : "Price quote number",
											"RF" : "Export reference number",
											"SI" : "SID (Shipper's identifying number for shipment)",
											"SO" : "Shipping Order Number",
											"TN" : "Transaction reference number",
											"ZZ" : "Mutually Defined",
											"BH" : "House bill of lading number"]
	Map<String, String> extGoodsItemRefMap = [	"ABW" : "SKU",
												"AFG" : "TARIF",
												"IX"  : "IX",
												"ON"  : "PO",
												"TN"  : "ITN"]
	Map<String, String> extGoodsItemRefDescMap = [	"ABW" : "Stock keeping unit number",
													"AFG" : "Tariff number",
													"IX"  : "Item Number",
													"ON"  : "Order number (purchase)",
													"TN"  : "Transaction reference number"]
	Map<String, String> extPartyTypeMap = [	"CA"  : "CAR",
											"CN"  : "CGN",
											"FW"  : "FWD",
											"N1"  : "NPT",
											"N2"  : "ANP",
											"SH"  : "SHP",
											"SI"  : "SIR",
											"WPA" : "WPA"]
	Map<String, String> extBLDistributedRoleTypeMap = [	"CN"  : "CGN",
														"FW"  : "FWD",
														"N1"  : "NPT",
														"N2"  : "ANP",
														"SH"  : "SHP",
														"SI"  : "SIR"]
	
	Map<String, String> extBLTransmissionMap = ["CS" : "ONLINE", "EI" : "EDI", "EM" : "EMAIL", "FX" : "FAX", "MA" : "HARDCOPY", "ZZ" : "OTHER"]
	Map<String, String> extPaymentOptionMap = ["C" : "COLLECT", "M" : "MIXED", "P" : "PREPAID"]
	Map<String, String> extChargeCategoryMap = ["4" : "Basic freight"]
	
	Map<String, String> extTemperatureUnitMap = ["CEL" : "C", "FAH" : "F"]
	Map<String, String> extVentilationUnitMap =  ["CBM" : "cbmPerHour", "CBF" : "cbfPerMin"]
	Map<String, String> extWeightUnitMap = ["KGM" : "KGS", "LBS" : "LBS", "TON" : "TON"]
	Map<String, String> extVolumeUnitMap = ["CBF" : "CBF", "CBM" : "CBM"]
	
	static final String SRC_FMT_ID = "IFTMIN"
	static final String TRG_FMT_ID = "CS2XML"

	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMddHHmmss = "yyyyMMddHHmmss"
	def yyyyMMddHHmm = "yyyyMMddHHmm"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = "HHmm"
	
	private List prepValidation(def current_Body, def currentTransactionIndex) {
		Group_UNH13 currentTransaction = current_Body
		
		List AppErrors = new ArrayList()
		// BGM
		if ((currentTransaction.BGM?.C002_01?.E1001_01 != "340") && (util.notEmpty(currentTransaction.BGM?.C002_01?.E1001_01))) {
			AppErrors.add('Invalid UNH['+currentTransactionIndex+'].BGM.C002_01.E1001_01')
		}
		// Map "9" as BGM03 when no input, but invalid input will flow error
		if ((currentTransaction.BGM?.E1225_03 != "5" && currentTransaction.BGM?.E1225_03 != "9" && util.notEmpty(currentTransaction.BGM?.E1225_03))) {
			AppErrors.add('Invalid UNH['+currentTransactionIndex+'].BGM.E1225_03')
		}
		//FTX
		if (util.notEmpty(currentTransaction.FTX)){
			currentTransaction.FTX?.eachWithIndex{ currentFTX, currentFTXIndex ->
				if (!(util.notEmpty(currentFTX.C108_04)) && (util.notEmpty(currentFTX.C107_03))) {
					AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].FTX['+currentFTXIndex+'].C108_04 as C107_03 presents')
				}
			}
		}
		//CNT
		if (util.notEmpty(currentTransaction.CNT)){
			currentTransaction.CNT?.eachWithIndex{ currentCNT, currentCNTIndex ->
				if (!(util.notEmpty(currentCNT.C270_01?.E6411_03)) && (currentCNT.C270_01?.E6069_01 == "7" || currentCNT.C270_01?.E6069_01 == "15")) {
					AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].CNT['+currentCNTIndex+'].C270_01.E6411_03 as E6069_01 is '+currentCNT.C270_01?.E6069_01)
				}
				if(util.isEmpty(extWeightUnitMap[currentCNT.C270_01?.E6411_03]) && (currentCNT.C270_01?.E6069_01 == "7")){
					AppErrors.add('Invalid UNH['+currentTransactionIndex+'].CNT['+currentCNTIndex+'].C270_01.E6411_03 for E6069_01 is '+currentCNT.C270_01?.E6069_01)
				}
				if(util.isEmpty(extVolumeUnitMap[currentCNT.C270_01?.E6411_03]) && (currentCNT.C270_01?.E6069_01 == "15")){
					AppErrors.add('Invalid UNH['+currentTransactionIndex+'].CNT['+currentCNTIndex+'].C270_01.E6411_03 for E6069_01 is '+currentCNT.C270_01?.E6069_01)
				}
			}
		}
		//Group1_LOC
		if (util.notEmpty(currentTransaction.Group1_LOC)){
			currentTransaction.Group1_LOC?.eachWithIndex{ currentGroup1LOC, currentGroup1LOCIndex ->
				if (!(util.notEmpty(currentGroup1LOC.DTM)) && (currentGroup1LOC.LOC?.E3227_01 == "73")) {
					AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group1_LOC['+currentGroup1LOCIndex+'].DTM for LOC.E3227_01 = "73"')
				}
			}
		}
		
		//Group3_RFF
		Boolean isRFFBNProvided = false
		if (util.notEmpty(currentTransaction.Group3_RFF)) {
			currentTransaction.Group3_RFF?.eachWithIndex { currentGroup3RFF, currentGroup3RFFIndex ->
				if (currentGroup3RFF.RFF?.C506_01?.E1153_01 == "BN" ){
					isRFFBNProvided = true
				}
			}
			if (!isRFFBNProvided){
				AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group3_RFF.RFF.C506_01.E1153_01 = "BN"')
			}
		}
		
		//Group8_TDT
		Boolean isPOLProvided = false
		Boolean isPODProvided = false
		if (util.notEmpty(currentTransaction.Group8_TDT)){
			currentTransaction.Group8_TDT?.eachWithIndex { currentGroup8TDT, currentGroup8TDTIndex ->
				currentGroup8TDT.Group9_LOC?.eachWithIndex { currentGroup9LOC, currentGroup9LOCIndex ->
					if (!(util.notEmpty(currentGroup9LOC.LOC?.C517_02?.E3225_01)) && (currentGroup9LOC.LOC?.E3227_01 == "9" || currentGroup9LOC.LOC?.E3227_01 == "11")) {
						AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group8_TDT['+currentGroup8TDTIndex+'].Group9_LOC['+currentGroup9LOCIndex+'].LOC.C517_02.E3225_01 as E3227_01 = "9"|"11"')
					}
					if (currentGroup9LOC.LOC?.E3227_01 == "9"){
						isPOLProvided = true
					}
					if (currentGroup9LOC.LOC?.E3227_01 == "11"){
						isPODProvided = true
					}
				}
				
			}
			if (!isPOLProvided){
				AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group8_TDT.Group9_LOC.LOC.E3227_01 = "9"')
			}
			if (!isPODProvided){
				AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group8_TDT.Group9_LOC.LOC.E3227_01 = "11"')
			}
		}
		
		//Group11_NAD
		if (util.notEmpty(currentTransaction.Group11_NAD)){
			if (!(util.notEmpty(currentTransaction.Group11_NAD?.find{it.NAD?.E3035_01 == "CA"}))) {
				AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group11_NAD for E3035_01 = "CA" (Carrier)')
			}
			else if (!(util.notEmpty(currentTransaction.Group11_NAD?.find{it.NAD?.E3035_01 == "CA"}.NAD?.C082_02?.E3039_01))) {
				AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group11_NAD.NAD.C082_02.E3039_01 where NAD.E3035_01 = "CA"')
			}
			if (!(util.notEmpty(currentTransaction.Group11_NAD?.find{it.NAD?.E3035_01 == "CN"}))) {
				AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group11_NAD for E3035_01 = "CN" (Consignee)')
			}
			if (!(util.notEmpty(currentTransaction.Group11_NAD?.find{it.NAD?.E3035_01 == "SH"}))) {
				AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group11_NAD for E3035_01 = "SH" (Shipper)')
			}
			if (!(util.notEmpty(currentTransaction.Group11_NAD?.find{it.NAD?.E3035_01 == "SI"}))) {
				AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group11_NAD for E3035_01 = "SI" (SI Requestor)')
			}
			
			currentTransaction.Group11_NAD?.eachWithIndex { currentGroup11NAD, currentGroup11NADIndex ->
				if (!(util.notEmpty(currentGroup11NAD.NAD?.C082_02)) && !(util.notEmpty(currentGroup11NAD.NAD?.C080_04)) && !(util.notEmpty(currentGroup11NAD.NAD?.C059_05)) && !(util.notEmpty(currentGroup11NAD.NAD?.E3164_06)) && !(util.notEmpty(currentGroup11NAD.NAD?.C819_07)) && !(util.notEmpty(currentGroup11NAD.NAD?.E3251_08)) && !(util.notEmpty(currentGroup11NAD.NAD?.E3207_09))) {
					AppErrors.add('UNH['+currentTransactionIndex+'].Group11_NAD['+currentGroup11NADIndex+'] - At least one of NAD020 or (NAD040~NAD090) is required')
				}
				if (util.notEmpty(currentGroup11NAD.Group12_CTA)) {
					currentGroup11NAD.Group12_CTA?.eachWithIndex { currentGroup12CTA, currentGroup12CTAIndex ->
						if (currentGroup11NAD.NAD?.E3035_01 == "WPA" && currentGroup12CTA.findAll{it.CTA?.E3139_01 == "RP"}?.size() == 0){
							AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group11_NAD[NAD_01 = "WPA"].Group12_CTA['+currentGroup12CTAIndex+'].CTA where CTA_01 = "RP"')
						}
					}
				}
				else {
					if (currentGroup11NAD.NAD?.E3035_01 == "WPA"){
						AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group11_NAD[NAD_01 = "WPA"].Group12_CTA')
					}
				}
				
				boolean billOfLading = false
				boolean seaWaybill = false
				boolean houseBillOfLading = false
				if (util.notEmpty(currentGroup11NAD.Group13_DOC)) {
					currentGroup11NAD.Group13_DOC?.eachWithIndex { currentGroup13DOC, currentGroup13DOCIndex ->
						if (currentGroup13DOC.DOC?.C002_01?.E1001_01 == "705"){
							billOfLading = true
						}
						if (currentGroup13DOC.DOC?.C002_01?.E1001_01 == "710"){
							seaWaybill = true
						}
						if (currentGroup13DOC.DOC?.C002_01?.E1001_01 == "714"){
							houseBillOfLading = true
						}
					}
					if ((billOfLading && seaWaybill)||(billOfLading && houseBillOfLading)||(seaWaybill && houseBillOfLading)){
						AppErrors.add('Multiple BL type involved - UNH['+currentTransactionIndex+'].Group11_NAD['+currentGroup11NADIndex+']')
					}
				}
			}
		}

		//Group18_GID
		if (util.notEmpty(currentTransaction.Group18_GID)){
			currentTransaction.Group18_GID?.eachWithIndex { currentGroup18GID, currentGroup18GIDIndex ->
				//Group20_MEA
				if (util.notEmpty(currentGroup18GID.Group20_MEA)) {
					currentGroup18GID.Group20_MEA?.eachWithIndex { currentGroup20MEA, currentGroup20MEAIndex ->
						if (!(util.notEmpty(extWeightUnitMap[currentGroup20MEA.MEA?.C174_03?.E6411_01])) && (currentGroup20MEA.MEA?.E6311_01 == "WT")) {
							AppErrors.add('Invalid UNH['+currentTransactionIndex+'].Group18_GID['+currentGroup18GIDIndex+'].Group20_MEA['+currentGroup20MEAIndex+'].MEA.C174_03.E6411_01 as E6311_01 = "WT"')
						}
						if (!(util.notEmpty(extVolumeUnitMap[currentGroup20MEA.MEA?.C174_03?.E6411_01])) && (currentGroup20MEA.MEA?.E6311_01 == "VOL")) {
							AppErrors.add('Invalid UNH['+currentTransactionIndex+'].Group18_GID['+currentGroup18GIDIndex+'].Group20_MEA['+currentGroup20MEAIndex+'].MEA.C174_03.E6411_01 as E6311_01 = "VOL"')
						}
					}
				}
				
				//Group29_SGP
				if (util.notEmpty(currentGroup18GID.Group29_SGP)) {
					currentGroup18GID.Group29_SGP?.eachWithIndex { currentGroup29SGP, currentGroup29SGPIndex ->
						if (util.notEmpty(currentGroup29SGP.Group30_MEA)) {
							currentGroup29SGP.Group30_MEA?.eachWithIndex { currentGroup30MEA, currentGroup30MEAIndex ->
								if (!(util.notEmpty(extWeightUnitMap[currentGroup30MEA.MEA?.C174_03?.E6411_01])) && (currentGroup30MEA.MEA?.E6311_01 == "WT")) {
									AppErrors.add('Invalid UNH['+currentTransactionIndex+'].Group18_GID['+currentGroup18GIDIndex+'].Group29_SGP['+currentGroup29SGPIndex+'].Group30_MEA['+currentGroup30MEAIndex+'].MEA.C174_03.E6411_01 as E6311_01 = "WT"')
								}
								if (!(util.notEmpty(extVolumeUnitMap[currentGroup30MEA.MEA?.C174_03?.E6411_01])) && (currentGroup30MEA.MEA?.E6311_01 == "VOL")) {
									AppErrors.add('Invalid UNH['+currentTransactionIndex+'].Group18_GID['+currentGroup18GIDIndex+'].Group29_SGP['+currentGroup29SGPIndex+'].Group30_MEA['+currentGroup30MEAIndex+'].MEA.C174_03.E6411_01 as E6311_01 = "VOL"')
								}
							}
						}
					}
				}
			}
		}// End-Group18_GID
		
		//Group37_EQD
		if (util.notEmpty(currentTransaction.Group37_EQD)){
			currentTransaction.Group37_EQD?.eachWithIndex { currentGroup37EQD, currentGroup37EQDIndex ->
				if (util.notEmpty(currentGroup37EQD.MEA)) {
					currentGroup37EQD.MEA?.eachWithIndex { currentGroup37EQDMEA, currentGroup37EQDMEAIndex ->
						if (!(util.notEmpty(extWeightUnitMap[currentGroup37EQDMEA.C174_03?.E6411_01])) && (currentGroup37EQDMEA.E6311_01 == "WT")) {
							AppErrors.add('Invalid UNH['+currentTransactionIndex+'].Group37_EQD['+currentGroup37EQDIndex+'].MEA['+currentGroup37EQDMEAIndex+'].C174_03.E6411_01 as E6311_01 = "WT"')
						}
						if (!(util.notEmpty(extVolumeUnitMap[currentGroup37EQDMEA.C174_03?.E6411_01])) && (currentGroup37EQDMEA.E6311_01 == "VOL")) {
							AppErrors.add('Invalid UNH['+currentTransactionIndex+'].Group37_EQD['+currentGroup37EQDIndex+'].MEA['+currentGroup37EQDMEAIndex+'].C174_03.E6411_01 as E6311_01 = "VOL"')
						}
						if ((currentGroup37EQDMEA.C174_03?.E6411_01 == "TON") && (currentGroup37EQDMEA.E6311_01 == "WT") && (currentGroup37EQDMEA.C502_02?.E6313_01 == "VGM")) {
							AppErrors.add('Invalid UNH['+currentTransactionIndex+'].Group37_EQD['+currentGroup37EQDIndex+'].MEA['+currentGroup37EQDMEAIndex+'].C174_03.E6411_01 = "TON" as E6311_01 = "WT" & C502_02.E6313_01 = "VGM"')
						}
						if ((currentGroup37EQDMEA.E6311_01 != "WT") && (currentGroup37EQDMEA.C502_02?.E6313_01 == "VGM")) {
							AppErrors.add('Invalid UNH['+currentTransactionIndex+'].Group37_EQD['+currentGroup37EQDIndex+'].MEA['+currentGroup37EQDMEAIndex+'].E6311_01 as C502_02.E6313_01 = "VGM"')
						}
						if (!(util.notEmpty(extVentilationUnitMap[currentGroup37EQDMEA.C174_03?.E6411_01])) && (currentGroup37EQDMEA.E6311_01 == "AAE") && (currentGroup37EQDMEA.C502_02?.E6313_01 == "AAS")) {
							AppErrors.add('Invalid UNH['+currentTransactionIndex+'].Group37_EQD['+currentGroup37EQDIndex+'].MEA['+currentGroup37EQDMEAIndex+'].C174_03.E6411_01 as E6311_01 = "AAE" and C502_02.E6313_01 == "AAS"')
						}
					}
				}

				
				if (util.notEmpty(currentGroup37EQD.Group39_NAD)) {
					currentGroup37EQD.Group39_NAD?.eachWithIndex { currentGroup39NAD, currentGroup39NADIndex ->					
						if (!(util.notEmpty(currentGroup39NAD.NAD?.C082_02)) && !(util.notEmpty(currentGroup39NAD.NAD?.C080_04)) && !(util.notEmpty(currentGroup39NAD.NAD?.C059_05)) && !(util.notEmpty(currentGroup39NAD.NAD?.E3164_06)) && !(util.notEmpty(currentGroup39NAD.NAD?.C819_07)) && !(util.notEmpty(currentGroup39NAD.NAD?.E3251_08)) && !(util.notEmpty(currentGroup39NAD.NAD?.E3207_09))) {
							AppErrors.add('UNH['+currentTransactionIndex+'].Group37_EQD['+currentGroup37EQDIndex+'].Group39_NAD['+currentGroup39NADIndex+'] - At least one of NAD020 or (NAD040~NAD090) is required')
						}
						//Group40_CTA
						if (util.notEmpty(currentGroup39NAD.Group40_CTA)) {
							currentGroup39NAD.Group40_CTA?.eachWithIndex { currentGroup40CTA, currentGroup40CTAIndex ->
								if (currentGroup39NAD.NAD?.E3035_01 == "WPA" && currentGroup40CTA.findAll{it.CTA?.E3139_01 == "RP"}?.size() == 0){
									AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group39_NAD[NAD_01 = "WPA"].Group12_CTA['+currentGroup40CTAIndex+'].CTA where CTA_01 = "RP"')
								}
							}
						}// End-Group40_CTA
						else {
							if (currentGroup39NAD.NAD?.E3035_01 == "WPA"){
								AppErrors.add('Missing mandatory UNH['+currentTransactionIndex+'].Group37_EQD['+currentGroup37EQDIndex+'].Group39_NAD[NAD_01 = "WPA"].Group40_CTA')
							}
						}
					}
				}// End-Group39_NAD
			}
		}// End-Group37_EQD

		//VGM
		//If WPA appears in Group 11
		if (currentTransaction.Group11_NAD?.findAll{it.NAD?.E3035_01 == "WPA"}?.size() > 0) {
			if (currentTransaction.Group37_EQD?.findAll{it.Group39_NAD?.findAll{it.NAD?.E3035_01 == "WPA"}}?.size() > 0){
				//If WPA appears in Group 39 at the same time
				AppErrors.add('Weighting party provided in both Group 11 & Group 39 which is not allowed.')
			}
		}
		//else if WPA appears in Group 39
		else if (currentTransaction.Group37_EQD?.findAll{it.Group39_NAD?.findAll{it.NAD?.E3035_01 == "WPA"}}?.size() > 0){
			//if not each EQD has provided weighting party
			if (currentTransaction.Group37_EQD?.findAll{it.Group39_NAD?.findAll{it.NAD?.E3035_01 == "WPA"}}?.size() != currentTransaction.Group37_EQD?.size()){
				AppErrors.add('Missing madatary weighting party Group39_NAD.')
			}
		}
		
		if (AppErrors.size() == 0){
			AppErrors.add("")
		}
		
		return AppErrors
	}
	
	public void generateBody(MarkupBuilder outXml, def current_Body, def current_BodyIndex, def currentSystemDt, def transactionDT, def action, def owner, def preError) {
		Group_UNH13 currentTransaction = current_Body
		String SCAC_CDE = currentTransaction.Group11_NAD?.find{it.NAD?.E3035_01 == "CA"}?.NAD?.C082_02?.E3039_01
		outXml.'Body'{
			'TransactionInformation'{
				'ns0:MessageID' MSG_REQ_ID + ',' + UUID.randomUUID()
				'ns0:InterchangeTransactionID' currentTransaction.UNH?.E0062_01
			} //End-TransactionInformation
			
			'EventInformation'{
				'ns0:EventCode' 'si_submit'
				'ns0:EventDT'{
					'ns0:GMT' currentSystemDt.format(xmlDateTimeFormat) + '+08:00'
					'ns0:LocDT'
				}
			} //End-EventInformation
			
			'GeneralInformation'{
				'CSSIReferenceNumber'
				'CustSIReferenceNumber' currentTransaction.BGM?.C106_02?.E1004_01
				if (util.notEmpty(action)){
					'ActionType' action
				}
				'SCAC' SCAC_CDE
				'SPCompanyID'
				'Owner' owner
				'SIVersionNumber'
				if (currentTransaction.Group11_NAD?.NAD?.find{it.E3035_01 == "CN"}?.C080_04?.E3036_01 == "TO ORDER"){
					'IsToOrder' 'true'
				}
				else{
					'IsToOrder' 'false'
				}
				'FMCNumber'
				currentTransaction.Group3_RFF?.findAll{it.RFF?.C506_01?.E1153_01 == "BN"}?.each{ currentBN ->
					'BookingNumber' currentBN.RFF?.C506_01?.E1154_02
				}
				def GID_DGS_exists = (currentTransaction.Group18_GID?.findAll{util.notEmpty(it.Group32_DGS)}?.size() > 0)
				def EQD_TMP_exists = (currentTransaction.Group37_EQD?.findAll{util.notEmpty(it.TMP)}?.size() > 0)
				if (GID_DGS_exists && EQD_TMP_exists){
					'ShipmentCargoType' 'RD'
				}
				else if (GID_DGS_exists){
					'ShipmentCargoType' 'DG'
				}
				else if (EQD_TMP_exists){
					'ShipmentCargoType' 'RF'
				}
				else{
					'ShipmentCargoType' 'GC'
				}
				if (util.notEmpty(currentTransaction.TSR)){
					'ShipmentTrafficMode'{
						switch(currentTransaction.TSR[0]?.C233_02?.E7273_01){
							case '2':
								'ns0:OutBound' 'FCL'
								'ns0:InBound' 'FCL'
								'TrafficModeDescription' 'FCL/FCL'
								break
							case '3':
								'ns0:OutBound' 'LCL'
								'ns0:InBound' 'LCL'
								'TrafficModeDescription' 'LCL/LCL'
								break
							case '82':
								'ns0:OutBound' 'FCL'
								'ns0:InBound' 'LCL'
								'TrafficModeDescription' 'FCL/LCL'
								break
							case '83':
								'ns0:OutBound' 'LCL'
								'ns0:InBound' 'FCL'
								'TrafficModeDescription' 'LCL/FCL'
								break
						}
					}
				}
				'IsOnlineBL' 'false'
				'SIRequestDescription'
				'CustomerTransactionDT'{
					'ns0:GMT'
					if (util.notEmpty(transactionDT)){
						def date = new SimpleDateFormat("yyyyMMddHHmm").parse(transactionDT)
						'ns0:LocDT' ('TimeZone': 'HKT', 'CSTimeZone': 'HKT', date.format(xmlDateTimeFormat) + '+08:00')
					}
				}
				'LastModifiedCSUserID'
				if (util.notEmpty(currentTransaction.Group1_LOC)){
					def placeOfPayment = currentTransaction.Group1_LOC?.findAll{it.LOC?.E3227_01 == "57"}
					if (placeOfPayment?.size() > 0){
						'PlaceOfPayment'{
							def UN_LOCN_CDE = ""
							if (util.notEmpty(placeOfPayment[0].LOC?.C517_02?.find{it.E3055_03 == "6"}?.E3225_01)){
								UN_LOCN_CDE = placeOfPayment[0].LOC?.C517_02?.find{it.E3055_03 == "6"}?.E3225_01
							}
							Map<String, String> cs2MasterCity = util.getCS2MasterCityByUNLocCityType(UN_LOCN_CDE, 'S', conn)
							'ns0:City' placeOfPayment[0].LOC?.C517_02?.E3224_04?placeOfPayment[0].LOC?.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
							'ns0:County' cs2MasterCity["COUNTY_NME"]
							'ns0:State' cs2MasterCity["STATE_NME"]?cs2MasterCity["STATE_NME"]:cs2MasterCity["STATE_CDE"]
							'ns0:Country' cs2MasterCity["CNTRY_NME"]?cs2MasterCity["CNTRY_NME"]:cs2MasterCity["CNTRY_CDE"]
							if (util.notEmpty(placeOfPayment[0].LOC?.C517_02?.E3225_01) && util.notEmpty(placeOfPayment[0].LOC?.C517_02?.E3055_03))
							'ns0:LocationCode' {
								'ns0:MutuallyDefinedCode' placeOfPayment[0].LOC?.C517_02?.find{it.E3055_03 == "ZZZ"}?.E3225_01
								'ns0:UNLocationCode' UN_LOCN_CDE
								'ns0:SchedKDType'
								'ns0:SchedKDCode'
							}
							'LocationName' placeOfPayment[0].LOC?.C517_02?.E3224_04?placeOfPayment[0].LOC?.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
							'OfficeCode'
						}// End-PlaceOfPayment
					}// End-if Group1_LOC+57
				}// End-if Group1_LOC
				
				def CNT_GW = currentTransaction.CNT?.findAll{it.C270_01?.E6069_01 == "7"}
				if (CNT_GW?.size > 0){
					'TotalGrossWeight'{
						'ns0:Weight' CNT_GW[0].C270_01?.E6066_02
						if (util.notEmpty(extWeightUnitMap[CNT_GW[0].C270_01?.E6411_03])){
							'ns0:WeightUnit' extWeightUnitMap[CNT_GW[0].C270_01?.E6411_03]
						}
					}
				}
				
				def CNT_VOL = currentTransaction.CNT?.findAll{it.C270_01?.E6069_01 == "15"}
				if (CNT_VOL?.size > 0){
					'TotalConsignment'{
						'ns0:Volume' CNT_VOL[0].C270_01?.E6066_02
						if (util.notEmpty(extVolumeUnitMap[CNT_VOL[0].C270_01?.E6411_03])){
							'ns0:VolumeUnit' extVolumeUnitMap[CNT_VOL[0].C270_01?.E6411_03]
						}
					}
				}
				def CNT_EQD = currentTransaction.CNT?.findAll{it.C270_01?.E6069_01 == "16"}
				if (CNT_EQD?.size() > 0){
					'TotalNumberOfEquipment' CNT_EQD[0].C270_01?.E6066_02
				}
				
				def CNT_PCK = currentTransaction.CNT?.findAll{it.C270_01?.E6069_01 == "11"}
				if (CNT_PCK?.size() > 0){
					'TotalNumberOfPackage'{
						'ns0:PackageType'
						'ns0:PackageQty' CNT_PCK[0].C270_01?.E6066_02
						'ns0:PackageDesc'
						'ns0:PackageMaterial'
					}
				}
			} //End-GeneralInformation
			
			currentTransaction.Group11_NAD?.each{ currentNAD ->
				def CSPartyType = extPartyTypeMap[currentNAD.NAD?.E3035_01]
				if (util.notEmpty(CSPartyType)){
					'Party'{
						'ns0:PartyType' CSPartyType
						if (util.notEmpty(currentNAD.NAD?.C080_04)){
							'ns0:PartyName' currentNAD.NAD?.C080_04?.E3036_01
						}
							
						String CCC_ExtCde = currentNAD.NAD?.C082_02?.E3039_01
						if (util.notEmpty(CCC_ExtCde)){
							String CCC_IntCde = util.getConversionWithScacByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, siUtil.CCC_CONVERTTYPEID, CCC_ExtCde, SCAC_CDE, conn)
                            def invaild_SICCCError = siUtil.INVAILD_SICCC_TYPE_ERROR
                            if (util.notEmpty(CCC_IntCde)){
								'ns0:CarrierCustomerCode' CCC_IntCde
							} else if (CSPartyType == "SIR"){
								'ns0:CarrierCustomerCode' preError.add(invaild_SICCCError)
							} else {
                                'ns0:CarrierCustomerCode' CCC_ExtCde
                            }
						}
						
						String triggerEmail = util.getEDICdeRef(TP_ID, siUtil.ISNEEDREPLYPARTYEMAIL_CONVERTTYPEID, DIR_ID, SRC_FMT_ID, 'isNeedReplyPartyEmail', 'isNeedReplyPartyEmail', CSPartyType, conn)
						if (util.notEmpty(triggerEmail)){
							'ns0:isNeedReplyPartyEmail' 'true'
						}
						
						def currentNAD_CTA = currentNAD.Group12_CTA?.findAll{it.CTA?.E3139_01 == "IC"}
						if (currentNAD_CTA?.size() > 0){
							'ns0:Contact' {
								int name_Cut = currentNAD_CTA[0].CTA?.C056_02?.E3412_02?.indexOf(",")
								if (name_Cut != -1){
									'ns0:FirstName' util.substring(currentNAD_CTA[0].CTA?.C056_02?.E3412_02, 1, name_Cut)
									'ns0:LastName' util.substring(currentNAD_CTA[0].CTA?.C056_02?.E3412_02, name_Cut+2, currentNAD_CTA[0].CTA?.C056_02?.E3412_02?.length())
								}else{
									'ns0:FirstName' currentNAD_CTA[0].CTA?.C056_02?.E3412_02
								}
								
								def currentNAD_COM_TE = currentNAD_CTA[0].COM?.findAll{it.C076_01.E3155_02 == "TE"}
								def currentNAD_COM_FX = currentNAD_CTA[0].COM?.findAll{it.C076_01.E3155_02 == "FX"}
								def currentNAD_COM_EM = currentNAD_CTA[0].COM?.findAll{it.C076_01.E3155_02 == "EM"}
								
								if (currentNAD_COM_TE?.size() > 0){
									'ns0:ContactPhone'{
										'ns0:CountryCode'
										'ns0:AreaCode'
										'ns0:Number' util.substring(currentNAD_COM_TE[0].C076_01?.E3148_01, 1, 30)
									}
								}
								if (currentNAD_COM_FX?.size() > 0){
									'ns0:ContactFax'{
										'ns0:CountryCode'
										'ns0:AreaCode'
										'ns0:Number' util.substring(currentNAD_COM_FX[0].C076_01?.E3148_01, 1, 30)
									}
								}
								if (currentNAD_COM_EM?.size() > 0){
									'ns0:ContactEmailAddress' currentNAD_COM_EM[0].C076_01?.E3148_01
								}
							} //End-Party-Contact
						}
						//}
						List<String> currentNAD05 = new ArrayList()
						if (util.notEmpty(currentNAD.NAD?.C059_05)){
							currentNAD05.add(currentNAD.NAD?.C059_05?.E3042_01)
							currentNAD05.add(currentNAD.NAD?.C059_05?.E3042_02)
							currentNAD05.add(currentNAD.NAD?.C059_05?.E3042_03)
							currentNAD05.add(currentNAD.NAD?.C059_05?.E3042_04)
							
							while (util.isEmpty(currentNAD05[-1])){
                                currentNAD05.remove(currentNAD05.size()-1)
                            }
						}
						
						if (currentNAD.NAD?.C059_05 || currentNAD.NAD?.E3164_06 || currentNAD.NAD?.C819_07 || currentNAD.NAD?.E3251_08 || currentNAD.NAD?.E3207_09){
							'ns0:Address'{
								'ns0:City' currentNAD.NAD?.E3164_06
								'ns0:County'
								'ns0:State' currentNAD.NAD?.C819_07?.E3229_01
								'ns0:Country' currentNAD.NAD?.E3207_09
								'ns0:LocationCode'{
									'ns0:MutuallyDefinedCode'
									'ns0:UNLocationCode'
									'ns0:SchedKDType'
									'ns0:SchedKDCode'
								}
								'ns0:PostalCode' util.substring(currentNAD.NAD?.E3251_08, 1, 12)
								if (util.notEmpty(currentNAD.NAD?.C059_05)){
									'ns0:AddressLines'{
										currentNAD05.each{ currentNAD05_e ->
											if (util.notEmpty(currentNAD05_e)){
												'ns0:AddressLine' currentNAD05_e
											}
										}
									}
								}
							} //End-Party-Address
						}
						
						def currentNAD_04 = currentNAD.NAD?.C080_04?.E3036_01
						List<String> partyTextList = new ArrayList()
						if (util.notEmpty(currentNAD_04)){
							partyTextList.add(currentNAD_04)
						}
						partyTextList.addAll(currentNAD05)
						String partyText = ""
						partyTextList.each {currentE ->
							if (util.notEmpty(partyText)){
								partyText = util.notEmpty(currentE)?partyText + "\n" + currentE:partyText + "\n"
							}
							else{
								partyText = util.notEmpty(currentE)?currentE:"\n"
							}
						}

						'PartyText' partyText
						'EDIPartyType' currentNAD.NAD?.E3035_01
					} //End-Party
				}
			} //End-Group11_NAD Looping
			
			def mappedSID = false
			currentTransaction.Group3_RFF?.each{ currentRFF ->
				if (util.notEmpty(extRefMap[currentRFF.RFF?.C506_01?.E1153_01])){
					'ExternalReference'{
						'ns0:CSReferenceType' extRefMap[currentRFF.RFF?.C506_01?.E1153_01]
						if (currentRFF.RFF?.C506_01?.E1153_01 == "SI"){
							'ns0:ReferenceNumber' currentTransaction.BGM?.C106_02?.E1004_01
							mappedSID = true
						}
						else {
							'ns0:ReferenceNumber' currentRFF.RFF?.C506_01?.E1154_02
						}
						if (util.notEmpty(extRefDescMap[currentRFF.RFF?.C506_01?.E1153_01])){
							'ns0:ReferenceDescription' extRefDescMap[currentRFF.RFF?.C506_01?.E1153_01]
						}
						'EDIReferenceCode' currentRFF.RFF?.C506_01?.E1153_01
					}// End-ExternalReference
				}
			} //End-Group3_RFF Looping
			if (! mappedSID){
				'ExternalReference'{
					'ns0:CSReferenceType' extRefMap['SI']
					'ns0:ReferenceNumber' currentTransaction.BGM?.C106_02?.E1004_01
					'ns0:ReferenceDescription' extRefDescMap['SI']
				}
			}
			
			currentTransaction.Group37_EQD?.eachWithIndex{ currentEQD, currentEQD_index ->
				'Container'{
					'AssociatedBookingNumber' currentTransaction.Group3_RFF?.find{it.RFF?.C506_01?.E1153_01 == "BN"}?.RFF?.C506_01?.E1154_02
					def currentContainerNumber = currentEQD.EQD?.C237_02?.E8260_01
					if (util.notEmpty(currentContainerNumber)){
						'ContainerNumber' util.substring(currentContainerNumber, 1, currentContainerNumber.length()-1)
						'ContainerCheckDigit' currentContainerNumber.charAt(currentContainerNumber.length()-1)
					}
					def ContainerType_ExtCde = currentEQD.EQD?.C224_03?.E8155_01
					if (util.notEmpty(ContainerType_ExtCde)){
						def ContainerType_IntCde = util.getConversionWithScacByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, siUtil.CONTAINER_CONVERTTYPEID, ContainerType_ExtCde, SCAC_CDE, conn)
						def invaildContainerError = siUtil.INVAILD_CONTAINER_TYPE_ERROR.replace("<EXT_CDE>", ContainerType_ExtCde)
						'CarrCntrSizeType' ContainerType_IntCde?ContainerType_IntCde:preError.add(invaildContainerError)
					}
					'CSContainerSizeType'
					def currentEQD_MEA_GWT = currentEQD.MEA?.findAll{it.E6311_01 == "WT" && it.C502_02?.E6313_01 == "G"}
					if (currentEQD_MEA_GWT?.size() > 0){
						'GrossWeight'{
							'ns0:Weight' currentEQD_MEA_GWT[0].C174_03?.E6314_02
							if (util.notEmpty(extWeightUnitMap[currentEQD_MEA_GWT[0].C174_03?.E6411_01])){
								'ns0:WeightUnit' extWeightUnitMap[currentEQD_MEA_GWT[0].C174_03?.E6411_01]
							}
							'WeightText'
						}
					}
					
					def currentEQD_MEA_TWT = currentEQD.MEA?.findAll{it.E6311_01 == "WT" && it.C502_02?.E6313_01 == "T"}
					if (currentEQD_MEA_TWT?.size() > 0){
						'TareWeight'{
							'ns0:Weight' currentEQD_MEA_TWT[0].C174_03?.E6314_02
							if (util.notEmpty(extWeightUnitMap[currentEQD_MEA_TWT[0].C174_03?.E6411_01])){
								'ns0:WeightUnit' extWeightUnitMap[currentEQD_MEA_TWT[0].C174_03?.E6411_01]
							}
						}
					}
					
					def currentEQD_MEA_VOL = currentEQD.MEA?.findAll{it.E6311_01 == "VOL" && it.C502_02?.E6313_01 == "AAW"}
					if (currentEQD_MEA_VOL?.size() > 0){
						'ContainerVolume'{
							'ns0:Volume' currentEQD_MEA_VOL[0].C174_03?.E6314_02
							if (util.notEmpty(extVolumeUnitMap[currentEQD_MEA_VOL[0].C174_03?.E6411_01])){
								'ns0:VolumeUnit' extVolumeUnitMap[currentEQD_MEA_VOL[0].C174_03?.E6411_01]
							}
						}
					}
					
					'Remarks'
					currentEQD.SEL?.findAll{it.C215_02?.E9303_01 == "CA" || it.C215_02?.E9303_01 == "SH"}?.each{currentEQD_SEL ->
						'Seal'{
							'ns0:SealType' currentEQD_SEL.C215_02?.E9303_01
							'ns0:SealNumber' currentEQD_SEL.E9308_01
							switch (currentEQD_SEL.C215_02?.E9303_01){
								case "CA":
									'SealTypeName' "Carrier"
									break
								case "SH":
									'SealTypeName' "Shipper"
									break
							}
						}
					}
					
					'Indicators'{
						'SOCIndicator' currentEQD.EQD?.E8077_04 == "1"?true:false
						'PerishableGoods' 'false'
						'DangerousGoods' 'false'
						'PersonalEffect' 'false'
						'Timber' 'false'
						'Flammable' 'false'
						'Fumigation' 'false'
						'MultipleBL' 'false'
						'LoadEmptyStatus' 'false'
					}// End-Indicators
					
					if (currentTransaction.TSR?.size() > 0){
						'Haulage'{
							if (currentTransaction.TSR[0].C536_01?.E4065_01 == "27"||currentTransaction.TSR[0].C536_01?.E4065_01 =="28"){
								'ns0:OutBound' 'C'
							}
							else if (currentTransaction.TSR[0].C536_01?.E4065_01 == "29"||currentTransaction.TSR[0].C536_01?.E4065_01 =="30"){
								'ns0:OutBound' 'M'
							}
							else{
								'ns0:OutBound' 'C'
							}
							if (currentTransaction.TSR[0].C536_01?.E4065_01 == "27"||currentTransaction.TSR[0].C536_01?.E4065_01 =="29"){
								'ns0:InBound' 'C'
							}
							else if (currentTransaction.TSR[0].C536_01?.E4065_01 == "28"||currentTransaction.TSR[0].C536_01?.E4065_01 =="30"){
								'ns0:InBound' 'M'
							}
							else{
								'ns0:InBound' 'C'
							}
						}
					}
					'ContainerSequenceID' 'EDI' + currentSystemDt.format(yyyyMMddHHmmss)+ String.format("%03d",(currentEQD_index + 1))
					if (currentEQD.TMP?.E6245_01 == "2"){
						'ReeferCargoSpec'{
							'ns0:Temperature'{
								'ns0:Temperature' currentEQD.TMP?.C239_02?.E6246_01?.replaceAll("^0+", "")
								if (util.notEmpty(extTemperatureUnitMap[currentEQD.TMP?.C239_02?.E6411_02])){
									'ns0:TemperatureUnit' extTemperatureUnitMap[currentEQD.TMP?.C239_02?.E6411_02]
								}
							}// End-ReeferCargoSpec-Temperature

							def currentEQD_MEA_VEN = currentEQD.MEA?.findAll{it.E6311_01 == "AAE" && it.C502_02?.E6313_01 == "AAS"}
							if (currentEQD_MEA_VEN?.size() > 0){
								'ns0:Ventilation'{
									'ns0:Ventilation' currentEQD_MEA_VEN[0]?.C174_03?.E6314_02
									if(util.notEmpty(extVentilationUnitMap[currentEQD_MEA_VEN[0]?.C174_03?.E6411_01])) {
										'ns0:VentilationUnit' extVentilationUnitMap[currentEQD_MEA_VEN[0]?.C174_03?.E6411_01]
									}
								}
							}//End-ReeferCargoSpec-Ventilation
						}// End-ReeferCargoSpec
					}
					
					def currentEQD_MEA_VGMWT = currentEQD.MEA?.findAll{it.E6311_01 == "WT" && it.C502_02?.E6313_01 == "VGM"}
					if (currentEQD_MEA_VGMWT?.size() > 0){
						'VGMWeight'{
							'ns0:Weight' currentEQD_MEA_VGMWT[0].C174_03?.E6314_02
							if (util.notEmpty(extWeightUnitMap[currentEQD_MEA_VGMWT[0].C174_03?.E6411_01])){
								'ns0:WeightUnit' extWeightUnitMap[currentEQD_MEA_VGMWT[0].C174_03?.E6411_01]
							}
						}// End-VGMWeight
					}
					def currentEQD_RFF_VGM = currentEQD.RFF?.findAll{it.C506_01?.E1153_01 == "ZZZ"}
					if (currentEQD_RFF_VGM?.size() > 0){
						if (util.notEmpty(currentEQD_RFF_VGM[0].C506_01?.E1154_02)){
							'VGMMethod' currentEQD_RFF_VGM[0].C506_01?.E1154_02
						}
					}
					
					def currentEQD_WPA = null
					def currentEQD_WPA_CTA = null
												
					if (currentEQD.findAll{it.Group39_NAD?.findAll{it.NAD?.E3035_01 == "WPA"}}?.size() > 0){
						currentEQD_WPA = currentEQD.Group39_NAD?.findAll{it.NAD?.E3035_01 == "WPA"}[0]
						currentEQD_WPA_CTA = currentEQD_WPA?.Group40_CTA?.findAll{it.CTA?.E3139_01 == "RP"}[0]
					}
					else if (currentTransaction.Group11_NAD?.findAll{it.NAD?.E3035_01 == "WPA"}?.size() > 0) {
						currentEQD_WPA = currentTransaction.Group11_NAD?.findAll{it.NAD?.E3035_01 == "WPA"}[0]
						currentEQD_WPA_CTA = currentEQD_WPA?.Group12_CTA?.findAll{it.CTA?.E3139_01 == "RP"}[0]
					}
					if (currentEQD_WPA){
						'WeightPartyName' currentEQD_WPA.NAD?.C080_04?.E3036_01
						if (util.notEmpty(currentEQD_WPA_CTA)){
							def currentEQD_WPA_CTA_02_02 = currentEQD_WPA_CTA.CTA?.C056_02?.E3412_02
							'WeightPartyAuthorizedPerson' currentEQD_WPA_CTA_02_02
							
							'WeightPartyContactInfo'{
								if (util.notEmpty(currentEQD_WPA_CTA_02_02)){
									int name_Cut = currentEQD_WPA_CTA_02_02.indexOf(",")
									if (name_Cut != -1){
										'ns0:FirstName' util.substring(currentEQD_WPA_CTA_02_02, 1, name_Cut)
										'ns0:LastName' util.substring(currentEQD_WPA_CTA_02_02, name_Cut+2, currentEQD_WPA_CTA_02_02.length())
									}else{
										'ns0:FirstName' currentEQD_WPA_CTA_02_02
									}
								}
								
								def currentNAD_COM_TE = currentEQD_WPA_CTA.COM?.findAll{it.C076_01?.E3155_02 == "TE"}
								def currentNAD_COM_FX = currentEQD_WPA_CTA.COM?.findAll{it.C076_01?.E3155_02 == "FX"}
								def currentNAD_COM_EM = currentEQD_WPA_CTA.COM?.findAll{it.C076_01?.E3155_02 == "EM"}
								
								if (currentNAD_COM_TE?.size() > 0){
									'ns0:ContactPhone'{
										'ns0:CountryCode'
										'ns0:AreaCode'
										'ns0:Number' util.substring(currentNAD_COM_TE[0]?.C076_01?.E3148_01, 1, 30)
									}
								}
								if (currentNAD_COM_FX?.size() > 0){
									'ns0:ContactFax'{
										'ns0:CountryCode'
										'ns0:AreaCode'
										'ns0:Number' util.substring(currentNAD_COM_FX[0]?.C076_01?.E3148_01, 1, 30)
									}
								}
								if (currentNAD_COM_EM?.size() > 0){
									'ns0:ContactEmailAddress' currentNAD_COM_EM[0]?.C076_01?.E3148_01
								}
							} //End-WeightPartyContactInfo
						}
						
						List<String> currentEQD_WPA05 = new ArrayList()
						if (util.notEmpty(currentEQD_WPA.NAD?.C059_05)){
							currentEQD_WPA05.add(currentEQD_WPA.NAD?.C059_05?.E3042_01)
							currentEQD_WPA05.add(currentEQD_WPA.NAD?.C059_05?.E3042_02)
							currentEQD_WPA05.add(currentEQD_WPA.NAD?.C059_05?.E3042_03)
							currentEQD_WPA05.add(currentEQD_WPA.NAD?.C059_05?.E3042_04)
							
							while (!util.notEmpty(currentEQD_WPA05[-1])){
								currentEQD_WPA05.remove(currentEQD_WPA05[-1])
							}
						}
						
						if (currentEQD_WPA.NAD?.C059_05 || currentEQD_WPA.NAD?.E3164_06 || currentEQD_WPA.NAD?.C819_07 || currentEQD_WPA.NAD?.E3251_08 || currentEQD_WPA.NAD?.E3207_09){
							'WeightPartyAddress'{
								'ns0:City' currentEQD_WPA.NAD?.E3164_06
								'ns0:County'
								'ns0:State' currentEQD_WPA.NAD?.C819_07?.E3229_01
								'ns0:LocationCode'
								'ns0:MutuallyDefinedCode'
								'ns0:UNLocationCode'
								'ns0:SchedKDType'
								'ns0:SchedKDCode'
								'ns0:Country' currentEQD_WPA.NAD?.E3207_09
								'ns0:PostalCode' util.substring(currentEQD_WPA.NAD?.E3251_08, 1, 12)
								if (util.notEmpty(currentEQD_WPA05)){
									'ns0:AddressLines'{
										currentEQD_WPA05.each{ currentEQD_WPA05_e ->
											if (util.notEmpty(currentEQD_WPA05_e)){
												'ns0:AddressLine' currentEQD_WPA05_e
											}
										}
									}
								}
							}// End-WeightPartyAddress
						}
					}
				}// End-Body-Container
			}// End-Group37_EQD Looping

			currentTransaction.Group18_GID?.eachWithIndex { currentGID, currentGID_index ->
				'Cargo'{
                    def invaildPackageError = ""
					'CargoInfo'{
						List<Group37_EQD417> EQD_linkingcurrentGID = new ArrayList()
						currentGID.Group29_SGP?.each{ currentGID_SGP ->
							EQD_linkingcurrentGID.addAll(currentTransaction.Group37_EQD?.findAll{it.EQD?.C237_02?.E8260_01 == currentGID_SGP.SGP?.C237_01?.E8260_01})
						}
						
						def currentGID_DGS_exists = util.notEmpty(currentGID.Group32_DGS)
						def EQD_linkingcurrentGID_TMP_exists = EQD_linkingcurrentGID.findAll{util.notEmpty(it.TMP)}?.size() > 0
						
						if (currentGID_DGS_exists && EQD_linkingcurrentGID_TMP_exists){
							'CargoNature' 'RD'
						}
						else if (currentGID_DGS_exists){
							'CargoNature' 'DG'
						}
						else if (EQD_linkingcurrentGID_TMP_exists){
							'CargoNature' 'RF'
						}
						else{
							'CargoNature' 'GC'
						}
						List<String> currentGID_FTXList = new ArrayList()
						currentGID.FTX?.findAll{it.E4451_01 == "AAA"}.each{ currentFTX ->
							currentGID_FTXList.add(currentFTX.C108_04?.E4440_01)
						}
						currentGID_FTXList.removeAll([null])
						'CargoDescription' currentGID_FTXList.join("\n")

						'Packaging'{
							def Package_ExtCde = currentGID.GID?.C213_02?.E7065_02
							def Package_IntCde = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, siUtil.PACKAGE_CONVERTTYPEID, Package_ExtCde, conn)
                            invaildPackageError = siUtil.INVAILD_PACKAGE_TYPE_ERROR.replace("<EXT_CDE>", Package_ExtCde)
                            'ns0:PackageType' Package_IntCde?Package_IntCde:preError.add(invaildPackageError)
							'ns0:PackageQty' currentGID.GID?.C213_02?.E7224_01
							'ns0:PackageDesc' currentGID.GID?.C213_02?.E7065_02
							'ns0:PackageMaterial'
						}
						def currentGID_MEA_GWT = currentGID.Group20_MEA?.findAll{it.MEA?.E6311_01 == 'WT' && it.MEA?.C502_02?.E6313_01 == 'G'}
						if (currentGID_MEA_GWT?.size() > 0){
							'GrossWeight'{
								'ns0:Weight' currentGID_MEA_GWT[0].MEA?.C174_03?.E6314_02
								if (util.notEmpty(extWeightUnitMap[currentGID_MEA_GWT[0].MEA?.C174_03?.E6411_01])){
									'ns0:WeightUnit' extWeightUnitMap[currentGID_MEA_GWT[0].MEA?.C174_03?.E6411_01]
								}
								'WeightText'
							}
						}
						
						def currentGID_MEA_VOL = currentGID.Group20_MEA?.findAll{it.MEA?.E6311_01 == 'VOL' && it.MEA?.C502_02?.E6313_01 == 'AAW'}
						if (currentGID_MEA_VOL?.size() > 0){
							'Volume'{
								'ns0:Volume' currentGID_MEA_VOL[0].MEA?.C174_03?.E6314_02
								if (util.notEmpty(extVolumeUnitMap[currentGID_MEA_VOL[0].MEA?.C174_03?.E6411_01])){
									'ns0:VolumeUnit' extVolumeUnitMap[currentGID_MEA_VOL[0].MEA?.C174_03?.E6411_01]
								}
							} // End-Volume
						}
						currentGID.Group23_PCI?.eachWithIndex{ currentGID_PCI, currentGID_PCI_index ->
							'MarksAndNumbers'{
								'ns0:SeqNumber' currentGID_PCI_index + 1
								'ns0:MarksAndNumbersLine' currentGID_PCI.PCI?.C210_02?.E7102_01
							}
						} //End-currentGID.Group23_PCI-Loop
						'CargoRemarks'
						
						currentGID.PIA?.findAll{it.C212_02?.E7143_02 == "HS" && it.C212_02?.E7140_01 != ""}?.eachWithIndex{ currentGID_PIA, currentGID_PIA_index ->
							'HarmonizedTariffSchedule'{
								'SeqNumber' currentGID_PIA_index + 1
								'HarmonizedCode' currentGID_PIA.C212_02?.E7140_01
							}
						} //End-currentGID.PIA-Loop
						'AssociatedBookingNumber' currentTransaction.Group3_RFF?.find{it.RFF?.C506_01?.E1153_01 == "BN"}?.RFF?.C506_01?.E1154_02
						
						def tempRFFType = ""
						def tempRFF = ""
						int tempRFFOrder = -1
						def waitFlag = true
						def currentGID_RFF_withOrder = currentGID.Group22_RFF?.findAll{util.notEmpty(it.RFF?.C506_01?.E1156_03)}
						def currentGID_RFF_withoutOrder = currentGID.Group22_RFF?.findAll{!util.notEmpty(it.RFF?.C506_01?.E1156_03)}
						
						if (util.notEmpty(currentGID_RFF_withOrder)){
							for (int i = 0; i < currentGID_RFF_withOrder?.size(); i++){
								def currentGID_RFF = currentGID_RFF_withOrder[i]
								// situation indicate that we can create ExternalReference
								// 1. Looping the last element i.e. no more RFF behind
								if (i == currentGID_RFF_withOrder?.size()-1){
									waitFlag = false
								}
								// 2. Non-first looping element and not with invalid line number
								else if (i != 0 && currentGID_RFF.RFF?.C506_01?.E1156_03?.toInteger() != (tempRFFOrder+1)){
									waitFlag = false
								}
								// 3. Non-first looping element and not with different ReferenceType
								else if (i != 0 && tempRFFType != currentGID_RFF.RFF?.C506_01?.E1153_01){
									waitFlag = false
								}
								
								if (!waitFlag){
									if (util.notEmpty(extGoodsItemRefMap[tempRFFType])){
										'ExternalReference'{
											'ns0:CSReferenceType' extGoodsItemRefMap[tempRFFType]
											'ns0:ReferenceNumber' util.substring(tempRFF, 1, 70)
											if (util.notEmpty(extGoodsItemRefDescMap[tempRFFType])){
												'ns0:ReferenceDescription' extGoodsItemRefDescMap[tempRFFType]
											}
											'EDIReferenceCode' tempRFFType
										}
									}
									// clean
									tempRFFType = ""
									tempRFF = ""
									tempRFFOrder = -1
									waitFlag = true
								}
								if (util.notEmpty(currentGID_RFF)){
									tempRFFType = currentGID_RFF.RFF?.C506_01?.E1153_01
									tempRFF = tempRFF + currentGID_RFF.RFF?.C506_01?.E1154_02
									tempRFFOrder = currentGID_RFF.RFF?.C506_01?.E1156_03?.toInteger()
								}
							}// End-currentGID_RFF_withOrder
						}
						
						currentGID_RFF_withoutOrder.each{ currentGID_RFF ->
							if (util.notEmpty(extGoodsItemRefMap[currentGID_RFF.RFF?.C506_01?.E1153_01]) && util.notEmpty(currentGID_RFF.RFF?.C506_01?.E1154_02)){
								'ExternalReference'{
									'ns0:CSReferenceType' extGoodsItemRefMap[currentGID_RFF.RFF?.C506_01?.E1153_01]
									'ns0:ReferenceNumber' currentGID_RFF.RFF?.C506_01?.E1154_02
									if (util.notEmpty(extGoodsItemRefDescMap[currentGID_RFF.RFF?.C506_01?.E1153_01])){
										'ns0:ReferenceDescription' extGoodsItemRefDescMap[currentGID_RFF.RFF?.C506_01?.E1153_01]
									}
									'EDIReferenceCode' currentGID_RFF.RFF?.C506_01?.E1153_01
								}
							}
						}// End-currentGID_RFF_withoutOrder
						if (util.notEmpty(currentGID.PIA?.find{it.C212_02?.E7143_02 == "CG" && it.C212_02?.E7140_01 != ""})){
							'CommodityGrouping' currentGID.PIA?.find{it.C212_02?.E7143_02 == "CG" && it.C212_02?.E7140_01 != ""}.C212_02?.E7140_01
						}
					}// End-CargoInfo
					
					currentGID.Group32_DGS?.each{ currentGID_DGS ->
						'DGCargoSpec'{
							'ns0:DGRegulator' currentGID_DGS.DGS?.E8273_01
							'ns0:IMDGPage' util.substring(currentGID_DGS.DGS?.C205_02?.E8078_02,1,5)
							'ns0:IMOClass' util.substring(currentGID_DGS.DGS?.C205_02?.E8351_01,1,5)
							'ns0:UNNumber' currentGID_DGS.DGS?.C234_03?.E7124_01
							
							List<String> currentGID_DGS_FTX = new ArrayList()
							currentGID_DGS.FTX?.findAll{it.E4451_01 == "AAD"}.each{ currentFTX ->
								currentGID_DGS_FTX.add(currentFTX.C108_04?.E4440_01)
								currentGID_DGS_FTX.add(currentFTX.C108_04?.E4440_02)
								currentGID_DGS_FTX.add(currentFTX.C108_04?.E4440_03)
								currentGID_DGS_FTX.add(currentFTX.C108_04?.E4440_04)
								currentGID_DGS_FTX.add(currentFTX.C108_04?.E4440_05)
							}
							currentGID_DGS_FTX.removeAll([null])
							
							if (currentGID_DGS_FTX.join().length() > 180)
								'ns0:TechnicalName' util.substring(currentGID_DGS_FTX.join(),1, 180)
							else 'ns0:TechnicalName' currentGID_DGS_FTX.join()
							'ns0:ProperShippingName'
							'ns0:PackageGroup'{
								'ns0:Code' currentGID_DGS.DGS?.E8339_05
								'ns0:InnerPackageDescription'{
									'ns0:PackageType'
									'ns0:PackageQty'
									'ns0:PackageDesc'
									'ns0:PackageMaterial'
								}
								'ns0:OuterPackageDescription'{
									'ns0:PackageType'
									'ns0:PackageQty'
									'ns0:PackageDesc'
									'ns0:PackageMaterial'
								}
							}
							'ns0:MFAGNumber'
							'ns0:EMSNumber'
							'ns0:PSAClass'
							'ns0:ApprovalCode'
							'ns0:GrossWeight'{
								'ns0:Weight'
								'ns0:WeightUnit'
							}
							'ns0:NetWeight'{
								'ns0:Weight'
								'ns0:WeightUnit'
							}
							'ns0:NetExplosiveWeight'{
								'ns0:Weight'
								'ns0:WeightUnit'
							}
							if (util.notEmpty(currentGID_DGS.DGS?.C223_04)){
								'ns0:FlashPoint'{
									def temp = currentGID_DGS.DGS?.C223_04?.E7106_01?.toInteger()
									'ns0:Temperature' temp
									if (util.notEmpty(extTemperatureUnitMap[currentGID_DGS.DGS?.C223_04?.E6411_02])){
										'ns0:TemperatureUnit' extTemperatureUnitMap[currentGID_DGS.DGS?.C223_04?.E6411_02]
									}
								}
							}
							'ns0:ElevatedTemperature'{
								'Temperature'
								'TemperatureUnit'
							}
							'ns0:IsLimitedQuantity'
							'ns0:IsInhalationHazardous'
							'ns0:IsReportableQuantity'
							'ns0:IsEmptyUnclean'
							'ns0:IsMarinePollutant'
							'ns0:State'
							'ns0:Label'
							'ns0:Remarks'
							currentGID_DGS.Group33_CTA?.findAll{it.CTA?.E3139_01 == "HG"}.each{ currentGID_DGS_CTA ->
								'ns0:EmergencyContact'{
									def currentGID_DGS_CTA_02_02 = currentGID_DGS_CTA.CTA?.C056_02?.E3412_02
									if (util.notEmpty(currentGID_DGS_CTA_02_02)){
										int name_Cut = currentGID_DGS_CTA_02_02.indexOf(",")
										if (name_Cut != -1){
											'ns0:FirstName' util.substring(currentGID_DGS_CTA_02_02, 1, name_Cut)
											'ns0:LastName' util.substring(currentGID_DGS_CTA_02_02, name_Cut+2, currentGID_DGS_CTA_02_02.length())
										}
										else{
											'ns0:FirstName' currentGID_DGS_CTA_02_02
										}
									}
									def currentGID_DGS_COM = currentGID_DGS_CTA.COM?.find{it.C076_01?.E3155_02 == 'TE'}
									'ns0:ContactPhone'{
										'ns0:CountryCode'
										'ns0:AreaCode'
										'ns0:Number' util.substring(currentGID_DGS_COM?.C076_01?.E3148_01, 1, 30)
									}
									'ns0:ContactFax'{
										'ns0:CountryCode'
										'ns0:AreaCode'
										'ns0:Number'
									}
									'ns0:ContactEmailAddress' 
									'ns0:Type'
								}
							}
							'HazardousClassificationCode'
							'HazardousReference'
							'MaterialDescription'
							'MaterialClassification'
							'EmergencyScheduleReference'
							'EmergencyResponseCode'
							'EmergencySchedulePageNumber'
							'LimitedQuantityDeclaration'
							'MarinePollutantInformation'
							'MedicalFirstAidGuidPageNumber'
							'IsResidue'
							'IsShipsideDelivery'
						}// End-DGCargoSpec
					}
					
					currentGID.Group21_DIM?.findAll{it.DIM?.E6145_01 == "9"}.each { currentGID_DIM ->
						'AWCargoSpec'{
							'ns0:Height'{
								'ns0:Length' currentGID_DIM.DIM?.C211_02?.E6008_04
								if (currentGID_DIM.DIM?.C211_02?.E6411_01 == "F"){
									'ns0:LengthUnit' "F"
								}
								else if (currentGID_DIM.DIM?.C211_02?.E6411_01 == "M"){
									'ns0:LengthUnit' "M"
								}
								
							}
							'ns0:Length'{
								'ns0:Length' currentGID_DIM.DIM?.C211_02?.E6168_02
								if (currentGID_DIM.DIM?.C211_02?.E6411_01 == "F"){
									'ns0:LengthUnit' "F"
								}
								else if (currentGID_DIM.DIM?.C211_02?.E6411_01 == "M"){
									'ns0:LengthUnit' "M"
								}
							}
							'ns0:Width'{
								'ns0:Length' currentGID_DIM.DIM?.C211_02?.E6140_03
								if (currentGID_DIM.DIM?.C211_02?.E6411_01 == "F"){
									'ns0:LengthUnit' "F"
								}
								else if (currentGID_DIM.DIM?.C211_02?.E6411_01 == "M"){
									'ns0:LengthUnit' "M"
								}
							}
						}
					}
					
					currentGID.Group29_SGP?.each{ currentGID_SGP ->
						'ContainerLoadPlan'{
							'Package'{
								def Package_ExtCde = currentGID.GID?.C213_02?.E7065_02
								def Package_IntCde = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, siUtil.PACKAGE_CONVERTTYPEID, Package_ExtCde, conn)
                                invaildPackageError = siUtil.INVAILD_PACKAGE_TYPE_ERROR.replace("<EXT_CDE>", Package_ExtCde)
                                'ns0:PackageType' Package_IntCde?Package_IntCde:preError.add(invaildPackageError)
								'ns0:PackageQty' currentGID_SGP.SGP?.E7224_02
								'ns0:PackageDesc'
								'ns0:PackageMaterial'
							}// End-Package
							
							def currentGID_SGP_MEA_GWT = currentGID_SGP.Group30_MEA?.findAll{it.MEA?.E6311_01 == 'WT' && it.MEA?.C502_02?.E6313_01 == 'G'}
							if (currentGID_SGP_MEA_GWT?.size() > 0){
								'GrossWeight'{
									'ns0:Weight' currentGID_SGP_MEA_GWT[0].MEA?.C174_03?.E6314_02
									if (util.notEmpty(extWeightUnitMap[currentGID_SGP_MEA_GWT[0].MEA?.C174_03?.E6411_01])){
										'ns0:WeightUnit' extWeightUnitMap[currentGID_SGP_MEA_GWT[0].MEA?.C174_03?.E6411_01]
									}
								} // End-GrossWeight
							}
							
							def currentGID_SGP_MEA_VOL = currentGID_SGP.Group30_MEA?.findAll{it.MEA?.E6311_01 == 'VOL' && it.MEA?.C502_02?.E6313_01 == 'AAW'}
							if (currentGID_SGP_MEA_VOL?.size() > 0){
								'Volume'{
									'ns0:Volume' currentGID_SGP_MEA_VOL[0].MEA?.C174_03?.E6314_02
									if (util.notEmpty(extVolumeUnitMap[currentGID_SGP_MEA_VOL[0].MEA?.C174_03?.E6411_01])){
										'ns0:VolumeUnit' extVolumeUnitMap[currentGID_SGP_MEA_VOL[0].MEA?.C174_03?.E6411_01]
									}
								}// End-Volume
							}
							'MarksAndNumber'{
								'ns0:SeqNumber'
								'ns0:MarksAndNumbersLine'
							}
							def currentGID_linkingEQD_index = currentTransaction.Group37_EQD?.findIndexOf{it.EQD?.C237_02?.E8260_01 == currentGID_SGP.SGP?.C237_01?.E8260_01}
							'ContainerSequenceID' 'EDI' + currentSystemDt.format(yyyyMMddHHmmss)+ String.format("%03d",(currentGID_linkingEQD_index + 1))
						}// End-ContainerLoadPlan
					}// End-looping currentGID_SGP
				}// End-Cargo
			}// End-Group18_GID Looping
			
			'BLInfo'{				
				'BLNumber' currentTransaction.Group3_RFF?.find{it.RFF?.C506_01?.E1153_01 == "BM"}?.RFF?.C506_01?.E1154_02
				//if (util.notEmpty(currentTransaction.Group1_LOC)){
				def LOC73 = currentTransaction.Group1_LOC?.findAll{it.LOC?.E3227_01 == "73" && util.notEmpty(it.DTM?.find{it.C507_01?.E2005_01 == "95"})}
				if (LOC73?.size() > 0){
					def LOC73_DTM = LOC73[0].DTM?.findAll{it.C507_01?.E2005_01 == "95"}
					if (util.notEmpty(LOC73_DTM[0])){
						'IssuranceOfBLDT'{
							'ns0:GMT' 
							def LOC73_DTM_format
							switch (LOC73_DTM[0].C507_01?.E2379_03){
								case "102":
									LOC73_DTM_format = yyyyMMdd
									break
								case "203":
									LOC73_DTM_format = yyyyMMddHHmm
									break
							}
							if (util.notEmpty(util.convertDateTime(LOC73_DTM[0].C507_01?.E2380_02, LOC73_DTM_format, xmlDateTimeFormat))){
								'ns0:LocDT' util.convertDateTime(LOC73_DTM[0].C507_01?.E2380_02, LOC73_DTM_format, xmlDateTimeFormat) + '+08:00'
							}
						}// End-IssuranceOfBLDT
					}
				}
				//}
				if (util.notEmpty(extPaymentOptionMap[currentTransaction.Group6_CPI?.CPI?.find{it.C229_01?.E5237_01 == "4"}?.E4237_03])){
					'PaymentOptionForOceanFreight' extPaymentOptionMap[currentTransaction.Group6_CPI?.CPI?.find{it.C229_01?.E5237_01 == "4"}?.E4237_03]
				}
				'IsDraftRequired' 'false'
				if (util.notEmpty(currentTransaction.Group11_NAD?.Group13_DOC)){
					'FinalCopyBLDistribution'{
						def firstTransmissionMode = ""
						currentTransaction.Group11_NAD?.each{ currentNAD ->
							currentNAD.Group13_DOC?.each{ currentDOC ->
								if (util.notEmpty(extDocTypeMap[currentDOC.DOC?.C002_01?.E1001_01])){
									'DistributionDetails'{
										'ns0:DocumentType' extDocTypeMap[currentDOC.DOC?.C002_01?.E1001_01]
										if (currentDOC.DOC?.C503_02?.E1373_02 == "26"){
											'ns0:FreightType' "NF"
										}
										else if (currentDOC.DOC?.C503_02?.E1373_02 == "27" && currentTransaction.Group6_CPI[0]?.CPI?.find{it.C229_01?.E5237_01 == "4"}?.E4237_03 == "C"){
											'ns0:FreightType' "FC"
										}
										else if (currentDOC.DOC?.C503_02?.E1373_02 == "27" && currentTransaction.Group6_CPI[0]?.CPI?.find{it.C229_01?.E5237_01 == "4"}?.E4237_03 == "P"){
												'ns0:FreightType' "FP"
										}
										else if (currentDOC.DOC?.C503_02?.E1373_02 == "27" && currentTransaction.Group6_CPI[0]?.CPI?.find{it.C229_01?.E5237_01 == "4"}?.E4237_03 == "M"){
											'ns0:FreightType' "FPC"
										}
										else if (currentDOC.DOC?.C503_02?.E1373_02 == "27"){
											'ns0:FreightType' "FC"
										}
										if (util.notEmpty(currentDOC.DOC?.E1220_04)){
											'ns0:NumberOfCopies' currentDOC.DOC?.E1220_04?.padLeft(3, "0")
										}
										if (firstTransmissionMode == ""){
											firstTransmissionMode = currentDOC.DOC?.E3153_03
										}
										if (util.notEmpty(extBLTransmissionMap[currentDOC.DOC?.E3153_03])){
											'ns0:TransmissionMode' extBLTransmissionMap[currentDOC.DOC?.E3153_03]
										}
										'ns0:OtherTransmissionInfo'
										if (util.notEmpty(extBLDistributedRoleTypeMap[currentNAD.NAD?.E3035_01])){
											'ns0:BLDistributedRole' extBLDistributedRoleTypeMap[currentNAD.NAD?.E3035_01]
										}
									}// End-DistributionDetails
								}
							}// End-NAD-DOC looping
						}// End-NAD looping
						'OtherHandlingInstruction'
						if (util.notEmpty(extBLTransmissionMap[firstTransmissionMode])){
							'BLTransmissionMode' extBLTransmissionMap[firstTransmissionMode]
						}
						'BLOtherTransmissionInfo'
					}// End-FinalCopyBLDistribution
				}
				'SummaryBLCargoDesc'
				'BLDefaultCurrencyCode'
				'DeclaredValueOfGoods'
	
				if (util.notEmpty(currentTransaction.Group1_LOC)){
					def blReleaseOffice = currentTransaction.Group1_LOC?.findAll{it.LOC?.E3227_01 == "73"}
					if (blReleaseOffice?.size() > 0){
						'BLReleaseOffice'{
							def UN_LOCN_CDE = ""
							if (util.notEmpty(blReleaseOffice[0].LOC?.C517_02?.find{it.E3055_03 == "6"}?.E3225_01)){
								UN_LOCN_CDE = blReleaseOffice[0].LOC?.C517_02?.find{it.E3055_03 == "6"}?.E3225_01
							}
							Map<String, String> cs2MasterCity = util.getCS2MasterCityByUNLocCityType(UN_LOCN_CDE, 'S', conn)
							'CountryCode' cs2MasterCity["CNTRY_CDE"]
							'City' blReleaseOffice[0].LOC?.C517_02?.E3224_04?blReleaseOffice[0].LOC?.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
							'County' cs2MasterCity["COUNTY_NME"]
							'State' cs2MasterCity["STATE_NME"]?cs2MasterCity["STATE_NME"]:cs2MasterCity["STATE_CDE"]
							'Country' cs2MasterCity["CNTRY_NME"]?cs2MasterCity["CNTRY_NME"]:cs2MasterCity["CNTRY_CDE"]
							'LocationName' blReleaseOffice[0].LOC?.C517_02?.E3224_04?blReleaseOffice[0].LOC?.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
							if (util.notEmpty(blReleaseOffice[0].LOC?.C517_02?.E3225_01) && util.notEmpty(blReleaseOffice[0].LOC?.C517_02?.E3055_03))
							'LocationCode' {
								'ns0:MutuallyDefinedCode' blReleaseOffice[0].LOC?.C517_02?.find{it.E3055_03 == "ZZZ"}?.E3225_01
								'ns0:UNLocationCode' UN_LOCN_CDE
								'ns0:SchedKDType'
								'ns0:SchedKDCode'
							}
							'OfficeCode'
						}// End-BLReleaseOffice
					}// End-if Group1_LOC+73
				}// End-if Group1_LOC
				
				currentTransaction.FTX?.findAll{it.E4451_01 == "BLC"}?.each{ currentFTX ->
					List<String> currentFTX04 = new ArrayList()
					
					currentFTX04.add(currentFTX.C108_04?.E4440_01)
					currentFTX04.add(currentFTX.C108_04?.E4440_02)
					currentFTX04.add(currentFTX.C108_04?.E4440_03)
					currentFTX04.add(currentFTX.C108_04?.E4440_04)
					currentFTX04.add(currentFTX.C108_04?.E4440_05)
					
					currentFTX04.removeAll([null])
					'SICertClause'{
						if (util.notEmpty(currentFTX.C107_03?.E4441_01) && certClauseTypeList.contains(currentFTX.C107_03?.E4441_01)){
							'ns0:CertificationClauseType' currentFTX.C107_03?.E4441_01
						}
						if (util.notEmpty(currentFTX04)){
							'ns0:CertificationClauseText' currentFTX04.join()
						}
					}// End-SICertClause
				}// End-FTX looping
				'RequestedBLDateType'
				def originOfGoods
				def validTDT = currentTransaction.Group8_TDT?.find{it.TDT?.E8051_01 == "20" && it.Group9_LOC?.findAll{it.LOC?.E3227_01 == "9"}}
				if (util.notEmpty(validTDT)){
					originOfGoods = validTDT.Group9_LOC?.findAll{it.LOC?.E3227_01 == "106"}[0]?.LOC
				}
				
				if (util.notEmpty(originOfGoods)){
					'OriginOfGoods'{
						def UN_LOCN_CDE = ""
						if (util.notEmpty(originOfGoods.C517_02?.find{it.E3055_03 == "6"}?.E3225_01)){
							UN_LOCN_CDE = originOfGoods.C517_02?.find{it.E3055_03 == "6"}?.E3225_01
						}
						Map<String, String> cs2MasterCity = util.getCS2MasterCityByUNLocCityType(UN_LOCN_CDE, 'S', conn)
						'ns0:City' originOfGoods.C517_02?.E3224_04?originOfGoods.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
						'ns0:County' cs2MasterCity["COUNTY_NME"]
						'ns0:State' cs2MasterCity["STATE_NME"]?cs2MasterCity["STATE_NME"]:cs2MasterCity["STATE_CDE"]
						'ns0:Country' cs2MasterCity["CNTRY_NME"]?cs2MasterCity["CNTRY_NME"]:cs2MasterCity["CNTRY_CDE"]
						if (util.notEmpty(originOfGoods.C517_02?.E3225_01) && util.notEmpty(originOfGoods.C517_02?.E3055_03))
						'ns0:LocationCode' {
							'ns0:MutuallyDefinedCode' originOfGoods.C517_02?.find{it.E3055_03 == "ZZZ"}?.E3225_01
							'ns0:UNLocationCode' UN_LOCN_CDE
							'ns0:SchedKDType'
							'ns0:SchedKDCode'
						}
						'LocationName' originOfGoods.C517_02?.E3224_04?originOfGoods.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
						'OriginText' originOfGoods.C517_02?.E3224_04
					}
				}// End-OriginOfGoods
				'BLRemarks'
				'OtherInformation'
				'BLCaptureOffice'
				'ThroughRateInd'
				'LoadThroughRateInd'
				'DischargingThroughRateInd'
				'MeasurementInd'
				'PLCIssue'
				'TotalPrepaidAmount'
				'TotalCollectAmount'
				
				def firstValidDOC = currentTransaction.Group11_NAD?.find{it.Group13_DOC?.size > 0}?.Group13_DOC.find{util.notEmpty(it.DOC?.C002_01?.E1001_01)}
				if (util.notEmpty(firstValidDOC)){
					'BLType' extDocTypeMap[firstValidDOC.DOC?.C002_01?.E1001_01]

					if (firstValidDOC.DOC?.C503_02?.E1373_02 == "26"){
						'BLFreightType' "NF"
					}
					else if (firstValidDOC.DOC?.C503_02?.E1373_02 == "27" && currentTransaction.Group6_CPI?.CPI?.find{it.C229_01?.E5237_01 == "4"}?.E4237_03 == "C"){
						'BLFreightType' "FC"
					}
					else if (firstValidDOC.DOC?.C503_02?.E1373_02 == "27" && currentTransaction.Group6_CPI?.CPI?.find{it.C229_01?.E5237_01 == "4"}?.E4237_03 == "P"){
						'BLFreightType' "FP"
					}
					else if (firstValidDOC.DOC?.C503_02?.E1373_02 == "27" && currentTransaction.Group6_CPI?.CPI?.find{it.C229_01?.E5237_01 == "4"}?.E4237_03 == "M"){
						'BLFreightType' "FPC"
					}
					else if (firstValidDOC.DOC?.C503_02?.E1373_02 == "27"){
						'BLFreightType' "FC"
					}
				}
			}//End-BLInfo
			
			// Discard TDT segment without LOC+9
			def validTDT = currentTransaction.Group8_TDT?.find{it.TDT?.E8051_01 == "20" && it.Group9_LOC?.findAll{it.LOC?.E3227_01 == "9"}}
			if (util.notEmpty(validTDT)){
				def locPOR = validTDT.Group9_LOC?.findAll{it.LOC?.E3227_01 == "88"}[0]?.LOC
				def locPOL = validTDT.Group9_LOC?.findAll{it.LOC?.E3227_01 == "9"}[0]?.LOC
				def locPOD = validTDT.Group9_LOC?.findAll{it.LOC?.E3227_01 == "11"}[0]?.LOC
				def locFND = validTDT.Group9_LOC?.findAll{it.LOC?.E3227_01 == "7"}[0]?.LOC
				
				'Route'{
					if (util.notEmpty(locPOR)){
						'POR'{
							def UN_LOCN_CDE = ""
							if (util.notEmpty(locPOR.C517_02?.find{it.E3055_03 == "6"}?.E3225_01)){
								UN_LOCN_CDE = locPOR.C517_02?.find{it.E3055_03 == "6"}?.E3225_01
							}
							Map<String, String> cs2MasterCity = util.getCS2MasterCityByUNLocCityType(UN_LOCN_CDE, 'S', conn)
							'ns0:LocationName' locPOR.C517_02?.E3224_04?locPOR.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
							'ns0:CityDetails'{
								'ns0:City' locPOR.C517_02?.E3224_04?locPOR.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
								'ns0:County' cs2MasterCity["COUNTY_NME"]
								'ns0:State' cs2MasterCity["STATE_NME"]?cs2MasterCity["STATE_NME"]:cs2MasterCity["STATE_CDE"]
								'ns0:Country' cs2MasterCity["CNTRY_NME"]?cs2MasterCity["CNTRY_NME"]:cs2MasterCity["CNTRY_CDE"]
								if (util.notEmpty(locPOR.C517_02?.E3225_01) && util.notEmpty(locPOR.C517_02?.E3055_03)){
									'ns0:LocationCode' {
										'ns0:MutuallyDefinedCode' locPOR.C517_02?.find{it.E3055_03 == "ZZZ"}?.E3225_01
										'ns0:UNLocationCode' UN_LOCN_CDE
										'ns0:SchedKDType'
										'ns0:SchedKDCode'
									}// End-POR-CityDetails-LocationCode
								}
							}// End-POR-CityDetails
							//-LOC_52/pfx:S-LOC_52/pfx:C-C517_52_02/pfx:E-3224_52_02.04)
							if (util.notEmpty(locPOR.C517_02?.E3224_04))
								'BLText' locPOR.C517_02?.E3224_04
						}// End-POR
					}// End-if-POR
					
					if (util.notEmpty(locFND)){
						'FND'{
							def UN_LOCN_CDE = ""
							if (util.notEmpty(locFND.C517_02?.find{it.E3055_03 == "6"}?.E3225_01)){
								UN_LOCN_CDE = locFND.C517_02?.find{it.E3055_03 == "6"}?.E3225_01
							}
							Map<String, String> cs2MasterCity = util.getCS2MasterCityByUNLocCityType(UN_LOCN_CDE, 'S', conn)
							'ns0:LocationName' locFND.C517_02?.E3224_04?locFND.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
							'ns0:CityDetails'{
								'ns0:City' locFND.C517_02?.E3224_04?locFND.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
								'ns0:County' cs2MasterCity["COUNTY_NME"]
								'ns0:State' cs2MasterCity["STATE_NME"]?cs2MasterCity["STATE_NME"]:cs2MasterCity["STATE_CDE"]
								'ns0:Country' cs2MasterCity["CNTRY_NME"]?cs2MasterCity["CNTRY_NME"]:cs2MasterCity["CNTRY_CDE"]
								if (util.notEmpty(locFND.C517_02?.E3225_01) && util.notEmpty(locFND.C517_02?.E3055_03)){
									'ns0:LocationCode' {
										'ns0:MutuallyDefinedCode' locFND.C517_02?.find{it.E3055_03 == "ZZZ"}?.E3225_01
										'ns0:UNLocationCode' UN_LOCN_CDE
										'ns0:SchedKDType'
										'ns0:SchedKDCode'
									}// End-POR-CityDetails-LocationCode
								}
							}// End-POR-CityDetails
							if (util.notEmpty(locFND.C517_02?.E3224_04))
								'BLText' locFND.C517_02?.E3224_04
						}// End-FND
					}// End-if-FND
					
					if (util.notEmpty(locPOL)){
						'FirstPOL'{
							'Port'{
								def UN_LOCN_CDE = ""
								if (util.notEmpty(locPOL.C517_02?.find{it.E3055_03 == "6"}?.E3225_01)){
									UN_LOCN_CDE = locPOL.C517_02?.find{it.E3055_03 == "6"}?.E3225_01
								}
								Map<String, String> cs2MasterCity = util.getCS2MasterCityByUNLocCityType(UN_LOCN_CDE, 'S', conn)
								'ns0:PortName' locPOL.C517_02?.E3224_04?locPOL.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
								'ns0:PortCode' ''
								'ns0:City' locPOL.C517_02?.E3224_04?locPOL.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
								'ns0:County' cs2MasterCity["COUNTY_NME"]
								'ns0:State' cs2MasterCity["STATE_NME"]?cs2MasterCity["STATE_NME"]:cs2MasterCity["STATE_CDE"]
								if (util.notEmpty(locPOL.C517_02?.E3225_01) && util.notEmpty(locPOL.C517_02?.E3055_03)){
									'ns0:LocationCode'{
										'ns0:MutuallyDefinedCode' locPOL.C517_02?.find{it.E3055_03 == "ZZZ"}?.E3225_01
										'ns0:UNLocationCode' UN_LOCN_CDE
										'ns0:SchedKDType'
										'ns0:SchedKDCode'
									}
								}
								'ns0:Country' cs2MasterCity["CNTRY_NME"]?cs2MasterCity["CNTRY_NME"]:cs2MasterCity["CNTRY_CDE"]
								'ns0:CSPortID'
								'ns0:CSCountryCode'
							}// End-FirstPOL-Port
							if (util.notEmpty(locPOL.C517_02?.E3224_04))
								'BLText' locPOL.C517_02?.E3224_04
						}// End-FirstPOL
					}// End-if-FirstPOL
					if (util.notEmpty(locPOD)){
						'LastPOD'{
							'Port'{
								def UN_LOCN_CDE = ""
								if (util.notEmpty(locPOD.C517_02?.find{it.E3055_03 == "6"}?.E3225_01)){
									UN_LOCN_CDE = locPOD.C517_02?.find{it.E3055_03 == "6"}?.E3225_01
								}
								Map<String, String> cs2MasterCity = util.getCS2MasterCityByUNLocCityType(UN_LOCN_CDE, 'S', conn)
								'ns0:PortName' locPOD.C517_02?.E3224_04?locPOD.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
								'ns0:PortCode' ''
								'ns0:City' locPOD.C517_02?.E3224_04?locPOD.C517_02?.E3224_04:cs2MasterCity["CITY_NME"]
								'ns0:County' cs2MasterCity["COUNTY_NME"]
								'ns0:State' cs2MasterCity["STATE_NME"]?cs2MasterCity["STATE_NME"]:cs2MasterCity["STATE_CDE"]
								if (util.notEmpty(locPOD.C517_02?.E3225_01) && util.notEmpty(locPOD.C517_02?.E3055_03)){
									'ns0:LocationCode'{
										'ns0:MutuallyDefinedCode' locPOD.C517_02?.find{it.E3055_03 == "ZZZ"}?.E3225_01
										'ns0:UNLocationCode' UN_LOCN_CDE
										'ns0:SchedKDType'
										'ns0:SchedKDCode'
									}
								}
								'ns0:Country' cs2MasterCity["CNTRY_NME"]?cs2MasterCity["CNTRY_NME"]:cs2MasterCity["CNTRY_CDE"]
								'ns0:CSPortID'
								'ns0:CSCountryCode'
							}// End-FirstPOL-Port
							if (util.notEmpty(locPOD.C517_02?.E3224_04))
								'BLText' locPOD.C517_02?.E3224_04
						}// End-LastPOD
					}// End-if-LastPOD
					
					'SVVD'{
						'ns0:Service'
						'ns0:Vessel'
						'ns0:VesselName' validTDT.TDT?.C222_08?.E8212_04
						'ns0:Voyage' validTDT.TDT?.E8028_02
						'ns0:Direction'
						'ns0:LloydsNumber' validTDT.TDT?.C222_08?.find {it.E1131_02 == 'L'}?.E8213_01
						'ns0:CallSign' util.substring(validTDT.TDT?.C222_08?.find {it.E1131_02 == '103'}?.E8213_01, 1, 10)
						'ns0:CallNumber'
						'ns0:VesselNationality'
						if (validTDT.TDT?.C222_08.E1131_02 == 'L'){
							'ExternalVesselType' 'L'
						}
						else if (validTDT.TDT?.C222_08.E1131_02 == '103'){
							'ExternalVesselType' 'C'
						}
						'ExternalVesselCode' util.substring(validTDT.TDT?.C222_08?.E8213_01, 1, 10)
						'ExternalVesselNumber' validTDT.TDT?.E8028_02
						'VesselVoyageText' validTDT.TDT?.C222_08?.E8212_04 + validTDT.TDT?.E8028_02
						'VoyageReference'
						'VesselFlag'
						'VesselOperator'
					}// End-SVVD
				}// End-Route
			}// End-validTDT-withLOC

			if (currentTransaction.Group6_CPI?.size() > 0){
				'ChargeInformation'{
					'ChargeDetails'{
						'ChargeCode'
						'ChargeCodeDescription'
						'ChargeType' extPaymentOptionMap[currentTransaction.Group6_CPI[0]?.CPI?.E4237_03]
						'ChargeCategory' extChargeCategoryMap[currentTransaction.Group6_CPI[0]?.CPI?.C229_01?.E5237_01]
						'ChargeItem'
						'ChargeItemDescription'
						'PayableAt'
						'PrepaidIndicator'
						'PrepaidBy'
						'PrepaidAt'
						'PrepaidCurrency'
						'PrepaidAmount'
						'CollectIndicator'
						'CollectBy'
						'CollectAt'
						'CollectCurrency'
						'CollectAmount'
						'FreightIndicator'
						'MautifactorRateDescription'
						'I_ECharge'
						'ChargeRatingMethod'
						'ChargeBasisIndicator'
						'ChargeBasis'
						'ChargeRate'
						'CurrencyCode'
						'CalculationMethod'
						'ExchangeRate'
						'ExchangeCurrency'
					}// End-ChargeInformation-ChargeDetails
				}// End-ChargeInformation
			}
			
			def validFTX = currentTransaction.FTX?.findAll{it.E4451_01 == "AAS"}
			if (validFTX?.size() > 0){
				'Remarks'{
					validFTX.each{ currentFTX ->
						List<String> FTX_AAS = new ArrayList()
						
						FTX_AAS.add(currentFTX.C108_04?.E4440_01)
						FTX_AAS.add(currentFTX.C108_04?.E4440_02)
						FTX_AAS.add(currentFTX.C108_04?.E4440_03)
						FTX_AAS.add(currentFTX.C108_04?.E4440_04)
						FTX_AAS.add(currentFTX.C108_04?.E4440_05)
	
						FTX_AAS.removeAll([null])
						if (FTX_AAS?.size() > 0){
							'ns0:RemarkLine' FTX_AAS.join(" ")
						}
					}
				}// End-Remarks
			}
			'EDIDynamicStructure'{
				'NewStructure'{
					'ParentStructureName' "FREETEXT"
					'NewElementName' SCAC_CDE
					'NewElementValue' "M+N"
				}// End-EDIDynamicStructure-NewStructure
				currentTransaction.Group18_GID?.eachWithIndex { currentGID, currentGID_index ->
					'NewStructure'{
						'ParentStructureName' "ShippingInstruction/Body/Cargo/CargoInfo"
						'NewElementName' "IsCargoInfo"
						'NewElementValue' "Y"
					}// End-EDIDynamicStructure-NewStructure
				}
			}// End-EDIDynamicStructure
		}// End-Body
	}
	
	public void buildBizKey(MarkupBuilder bizKeyXml, def output, def inputEDI, def txnErrorKeys){
		def shippingInstruction = new XmlSlurper().parseText(output)
		EDI_IFTMIN input = inputEDI
		
		input.Group_UNH?.eachWithIndex { current_Body, current_BodyIndex ->
			bizKeyXml.'ns0:Transaction'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
				'ns0:ControlNumberInfo' {
					'ns0:Interchange' input.UNB?.E0020_05
					'ns0:Group' input.UNG?.E0048_05
					'ns0:Transaction' current_Body.UNH?.E0062_01
				}
				// TransactionRefNumber
				if (util.notEmpty(current_Body.BGM?.C106_02?.E1004_01)){
					'ns0:BizKey' {	'ns0:Type' 'TransactionRefNumber'
									'ns0:Value' current_Body.BGM?.C106_02?.E1004_01	}
				}
				
				def bizKey = siUtil.buildBizKey(bizKeyXml, shippingInstruction.Body[current_BodyIndex], current_BodyIndex, txnErrorKeys[current_BodyIndex], TP_ID, MSG_REQ_ID, conn)
				
				// If no error in txn
				if (txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.size() == 0) {
					// insert Biz-key by generated CS2XML which is common across different SI msg format
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
	
	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
		
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
		EDI_IFTMIN iftmin = xmlBeanParser.xmlParser(inputXmlBody,EDI_IFTMIN.class)

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
		//create root node
		def siRoot = outXml.createNode('ShippingInstruction', ['xmlns':"http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0':"http://www.cargosmart.com/common"])
		def bizKeyRoot = bizKeyXml.createNode('root')
		def processAckRoot = processAckXml.createNode('root')
		
		def outputBodyCount = 0
		def headerGenerated = false
		List txnErrorKeys = new ArrayList()
		
		def bodies = iftmin.Group_UNH
		def transactionDT = ""
		if (util.notEmpty(iftmin.UNB?.S004_04)){
			transactionDT = util.substring(currentSystemDt.format("yyyy"), 1, 2) + iftmin.UNB?.S004_04?.E0017_01 + iftmin.UNB?.S004_04?.E0019_02
		}
		def action = ""
		if (iftmin.Group_UNH[0].BGM?.E1225_03 == "9"){
			action = 'NEW'
		}
		else if (iftmin.Group_UNH[0].BGM?.E1225_03 == "5"){
			action = 'UPD'
		}
		else if (!util.notEmpty(iftmin.Group_UNH[0].BGM?.E1225_03)){
			action = 'NEW'
		}
		def owner = iftmin.UNB?.S002_02?.E0004_01
		
		bodies.eachWithIndex { current_Body, current_BodyIndex ->
			// prep checking
			List preErrors = new ArrayList()
			preErrors = prepValidation(current_Body, current_BodyIndex)
			
			// map Header once only
			if (!headerGenerated){
				siUtil.generateHeader(outXml, current_Body, current_BodyIndex, currentSystemDt, action, DIR_ID, MSG_TYPE_ID, TP_ID, MSG_REQ_ID) 
				headerGenerated = true
			}
			// map Body
			if (preErrors?.findAll{it != ""}?.size() == 0){
				generateBody(outXml, current_Body, current_BodyIndex, currentSystemDt, transactionDT, action, owner, preErrors)
			}
			else {
				outXml.'Body'{}
			}
			txnErrorKeys.add(preErrors)
		}
		// close mapping of CS2XML
		outXml.nodeCompleted(null,siRoot)
		String si = writer?.toString()
		
		// do post-validation
		List postErrors = new ArrayList()
		postErrors = siUtil.xmlXSDValidation('CS2-CUS-SIXML', si, txnErrorKeys, siUtil.&postValidation)
		
		// add post-validation error to error set
		postErrors.eachWithIndex { current_Error, current_ErrorIndex ->
			txnErrorKeys[current_ErrorIndex].addAll(current_Error)
		}
		
		// map Bizkey
		buildBizKey(bizKeyXml, si, iftmin, txnErrorKeys)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		
		writer.close()
		bizKeyWriter.close()
		
		// println si
        // println bizKeyWriter.toString()
		 
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