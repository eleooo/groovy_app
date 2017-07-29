package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection

/**
 * @author Tracy
 * DDHCTOPOCEAN  pattern after TOPOCEAN CT initialize on 20170623
 */
public class CUS_CS2CTXML_315_DDHCTOPOCEAN {

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
	def SCAC = null // get SCAC from first Body



	public void generateBody(Body current_Body, MarkupBuilder outXml) {


		//CT special fields
		def vCS1Event = current_Body?.Event?.CS1Event?.trim()  //1.0 use trim
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		
		def vCS1EventCodeConversion = util.getCdeConversionFromIntCde('DDHCTOPOCEAN', 'CT', 'O','<QUERY-NULL-SCAC>',null, 'EventStatusCode', vCS1Event, conn)
		def vCS1EventCodeConversionWithSCAC=util.getConversionWithScac('DDHCTOPOCEAN', 'CT', 'O', 'EventStatusCode', vCS1Event, SCAC, conn)
		
		def shipDir =  null 		//get the first 5 char, in case duplicate event missing direction.
		if(util.getConversion('DDHCTOPOCEAN','CT', 'O','EventDirection',vCS1EventFirst5, conn)){
			shipDir = util.getConversion('DDHCTOPOCEAN','CT', 'O','EventDirection',vCS1EventFirst5, conn)
		}else if(util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)){
			shipDir =util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)
		}

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '-999'
			}
			'B4' {
				//B403: MapEventStatusCodeByScacFirst
				if(vCS1EventCodeConversionWithSCAC){
					'E157_03' vCS1EventCodeConversionWithSCAC
				}
				else{
					'E157_03' vCS1EventCodeConversion.get("EXT_CDE")
				}

				'E373_04' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)

				if(current_Body?.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body?.Container?.ContainerNumber,1,4)
				}

				if(current_Body.Container?.ContainerNumber?.length()>=5){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber,5,current_Body?.Container?.ContainerNumber?.length())
				}

				if(util.isEmpty(current_Body?.Container?.ContainerStatus)){
					if(['CS010', 'CS210'].contains(vCS1Event)){
						'E578_09'  'E'
					}else {
						'E578_09' 'L'
					}
				}else{
				'E578_09' util.substring(current_Body?.Container?.ContainerStatus?.trim(),1,1)
				}

				
				//B410: MapEquipmentTypeFromCarrCntrSizeType, direct map
				if(current_Body?.Container?.CarrCntrSizeType?.trim()){
					'E24_10' current_Body?.Container?.CarrCntrSizeType?.trim()
				}

				def B412=null
				if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode?.trim()){ //B411 MapLocationIdentifierFromEventUnCode sequence=1
					'E310_11' current_Body?.Event?.Location?.LocationCode?.UNLocationCode?.trim()
					B412='UN'
				}
				else if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode?.trim() && current_Body?.Event?.Location?.LocationCode?.SchedKDType?.trim()){ //B411 MapLocationIdentifierFromEventSchedKD sequence=2
					'E310_11' util.substring(current_Body?.Event?.Location?.LocationCode?.SchedKDCode?.trim(),1,30)
					B412=current_Body?.Event?.Location?.LocationCode?.SchedKDType?.trim()
				}else if(current_Body?.Event?.Location?.CityDetails?.City?.trim())  //B411 MapLocationIdentifierFromEventCity  sequence=3
				{
					'E310_11' util.substring(current_Body?.Event?.Location?.CityDetails?.City?.trim(),1,30)
					B412='CI'
				}else if(current_Body?.Event?.Location?.LocationName?.trim()){  //B411 MapLocationIdentifierFromLocationName sequence=4
					'E310_11' util.substring(current_Body?.Event?.Location?.LocationName?.trim(),1,30)
					B412='CI'
				}

				if(B412){
					'E309_12'  B412
				}

				if(util.isNotEmpty(current_Body?.Container?.ContainerCheckDigit)){
					'E761_13' util.substring(current_Body?.Container?.ContainerCheckDigit,1,1)
				}

			}
		
//===============================================N9 Start=======================================================
			//--------------------------BookingGeneralInfo - MapReferenceIndentification-------------------------------------
			//-----------Customization: N9RemoveDuplicateSegment-----

			def existsBLNumber = true

			if(current_Body?.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it?.BLNumber)}?.size()<=0){
				existsBLNumber = false
			}
			//Customization :  if BLNumber doesn't exist, then get the carrier booking number
			if(!existsBLNumber && current_Body?.BookingGeneralInfo?.find { StringUtil.isNotEmpty(it?.CarrierBookingNumber) }?.CarrierBookingNumber)  {
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(current_Body?.BookingGeneralInfo?.find { StringUtil.isNotEmpty(it?.CarrierBookingNumber) }?.CarrierBookingNumber, 1, 30)
				}
			}
			current_Body?.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it?.CarrierBookingNumber)}?.groupBy{it?.CarrierBookingNumber}?.each {bkNumber,current_BKGeneralInfo ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(bkNumber,1,30)
				}

			}

			if(existsBLNumber){
				current_Body?.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it?.BLNumber)}?.groupBy {it?.BLNumber}?.each {blNumber,current_blGeneralInfo->
					'N9' {
						'E128_01' 'BM'
						'E127_02' util.substring(blNumber,1,30)
					}
				}
			}

			//------------------------Container---MapReferenceIndentificationCheckDigit-----------------------------------------------------------
			current_Body?.Container?.each { current_Container ->
				def N902 = ctUtil.emptyToString(current_Container?.ContainerNumber?.trim()) + ctUtil.emptyToString(current_Container?.ContainerCheckDigit?.trim())
				if (util.isNotEmpty(N902)) {
					'N9' {
						'E128_01' 'EQ'
						'E127_02' util.substring(N902, 1, 30)
					}
				}
			}
				
			//-----------------------------ExternalReference-------------------------------------------------------
			def externalReferenceTypeMap = ['FR':'FN','INV':'IN','PO':'PO','SC':'E8','SID':'SI','SO':'SO','SR':'SR']
			def individualN9Ref = []
			current_Body?.ExternalReference?.each {current_ExtReference ->
				if(externalReferenceTypeMap.containsKey(current_ExtReference.CSReferenceType) && !individualN9Ref.contains(current_ExtReference?.CSReferenceType?.trim()+util.substring(current_ExtReference?.ReferenceNumber?.trim(),1,50))){
					'N9' {
						'E128_01' externalReferenceTypeMap.get(current_ExtReference?.CSReferenceType)
						'E127_02' util.substring(current_ExtReference?.ReferenceNumber?.trim(),1,50)  //1.0 did substring first 50 chars
						individualN9Ref.add(current_ExtReference?.CSReferenceType?.trim()+util.substring(current_ExtReference?.ReferenceNumber?.trim(),1,50))
					}
				}
			}

			//-----------------Party-------------------------------------------------------
			//Seq1: MapIndetificationWherePartyTypeIn   substring(tib:trim($Start/pfx10:group/pfx:Body-ContainerMovement/pfx:Party-CntrMvmtCusBodyType[1]/ns1:CarrierCustomerCode), 1, 30)
			//Seq2: MapIndetificationWherePartyTypeNotIn substring(tib:trim($Start/pfx10:group/pfx:Body-ContainerMovement/pfx:Party-CntrMvmtCusBodyType[1]/ns1:CarrierCustomerCode), 1, 30)
			def  N9PartyTypeMap = ['CGN': '4E','SHP':'4F']
			def individualN9Party = []
			current_Body?.Party?.each {current_Party ->
				//LoopByPartyNoLevelRtnM: when Party has no ParyLevel then do mapping
				if(current_Party?.PartyLevel?.trim()=='' || util.isEmpty(current_Party?.PartyLevel)){
					if(util.isNotEmpty(current_Party?.CarrierCustomerCode?.trim())){
						def N901Party=null
						def N902Party=null
						if(N9PartyTypeMap.containsKey(current_Party?.PartyType?.trim())&& !individualN9Party.contains(current_Party?.PartyType?.trim()+util.substring(current_Party?.CarrierCustomerCode?.trim(), 1, 30))){
							N901Party= N9PartyTypeMap.get(current_Party?.PartyType?.trim())
							N902Party=util.substring(current_Party?.CarrierCustomerCode?.trim(),1,30)
							individualN9Party.add(current_Party?.PartyType?.trim()+util.substring(current_Party?.CarrierCustomerCode?.trim(), 1, 30))
						}
						else if(!N9PartyTypeMap.containsKey(current_Party?.PartyType?.trim())&& !individualN9Party.contains(util.substring(current_Party?.CarrierCustomerCode?.trim(), 1, 30))){
							N901Party='AAO'
							N902Party=util.substring(current_Party?.CarrierCustomerCode?.trim(),1,30)
							individualN9Party.add(util.substring(current_Party?.CarrierCustomerCode?.trim(), 1, 30))
						}
						
						'N9' {
							'E128_01' N901Party
							'E127_02' N902Party
						}
					}
				}
			}
		

		
			//---------------------------Scac--------------------------------------------------------------
			if(util.isNotEmpty(SCAC)){
				'N9' {
					'E128_01' 'SCA'
					'E127_02' SCAC
				}
			}
//===============================================N9 OVER=======================================================


//===============================================Q2 START=======================================================
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body.Route?.OceanLeg){
				firstOceanLeg = current_Body.Route.OceanLeg[0]
				lastOceanLeg = current_Body.Route.OceanLeg[-1]
			}
        
			if (shipDir == 'I') {
				'Q2' {

					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E597_01' util?.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)
					}

					if (lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode) {
						'E26_02' lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode
					}
					if (lastOceanLeg?.SVVD?.Discharge?.Voyage) {  //seq1: MapVoyageNumberFromLastDischargeVoyageWithDirection
						'E55_09' util.substring(lastOceanLeg?.SVVD?.Discharge?.Voyage?.trim() + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction?.trim() : ""),1,10)
					} else if (lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage) {
						'E55_09' util.substring(lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage?.trim(),1,10)
					}
					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E897_12' 'L'
					}

					if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
						'E182_13' lastOceanLeg?.SVVD?.Discharge?.VesselName?.trim()
					}
				}
			}else if (shipDir == 'O') {
				'Q2' {
					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)
					}

					if (firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode) {
						'E26_02' firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode
					}

					if (firstOceanLeg?.SVVD?.Loading?.Voyage) {
						'E55_09' util.substring(firstOceanLeg?.SVVD?.Loading?.Voyage + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction : ""),1,10)
					} else if (firstOceanLeg?.SVVD?.Loading?.ExternalVoyage) {
						'E55_09' util.substring(firstOceanLeg?.SVVD?.Loading?.ExternalVoyage?.trim(),1,10)
					}

					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E897_12' 'L'
					}

					if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' firstOceanLeg?.SVVD?.Loading?.VesselName?.trim()
					}
				}
			}

//===============================================Q2 OVER========================================================


//===============================================R4 START=======================================================
//--------------------------------------POR--------------------------------
			POR por = current_Body?.Route?.POR
			def R402R=null
			def R403R=null
			def R404R=null

			if (por?.CityDetails?.LocationCode?.UNLocationCode) {
				R402R= 'UN'
				R403R= por?.CityDetails?.LocationCode?.UNLocationCode?.trim()
			}else if(util.isNotEmpty(por?.CityDetails?.LocationCode?.SchedKDCode)){
				if(util.isNotEmpty(por?.CityDetails?.LocationCode?.SchedKDType)){
					R402R= por?.CityDetails?.LocationCode?.SchedKDType?.trim()
				}
				R403R= por?.CityDetails?.LocationCode?.SchedKDCode?.trim()
			}
			if (por?.CityDetails?.City) {
				R404R= util.substring(por?.CityDetails?.City?.trim(),1,24).toUpperCase() //Customization - R404UpperCase
			}

			if(R403R || R404R){
				'Loop_R4' {

					'R4' {
						'E115_01' 'R'
						'E309_02' R402R
						'E310_03' R403R
						'E114_04' R404R
						if (util.isNotEmpty(por?.CSStandardCity?.CSCountryCode) && por?.CSStandardCity?.CSCountryCode?.length()<=2) {
							'E26_05' por?.CSStandardCity?.CSCountryCode?.trim()
						}
						if (por?.CSStandardCity?.CSStateCode) {
							'E156_08' util.substring(por?.CSStandardCity?.CSStateCode,1,2).padRight(2)
						}
					}

					LocDT porDTM = null
					def isAct = false
					if(current_Body?.Route?.CargoReceiptDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						porDTM = current_Body?.Route?.CargoReceiptDT?.find{it?.attr_Indicator=='A'}?.LocDT
						isAct =true
					} else if(current_Body?.Route?.FullPickupDT?.find{it?.attr_Indicator=='E'}?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound=='C' ){
						porDTM = current_Body?.Route?.FullPickupDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}else if(current_Body?.Route?.FullReturnCutoffDT?.find{it?.attr_Indicator=='A'}?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound=='M'){
						porDTM = current_Body?.Route?.FullReturnCutoffDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}

					if(porDTM){
						'DTM'{
							if(isAct){
								'E374_01' '140'
							}else{
								'E374_01' '139'
							}

							'E373_02' util.convertDateTime(porDTM, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(porDTM, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
//--------------------------------------POL--------------------------------
			FirstPOL firstPOL=current_Body?.Route?.FirstPOL
			def R402L=null
			def R403L=null
			def R404L=null

			if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
				R402L='UN'
				R403L= firstPOL?.Port?.LocationCode?.UNLocationCode?.trim()
			}else if(util.isNotEmpty(firstPOL?.Port?.LocationCode?.SchedKDCode)){
				if(util.isNotEmpty(firstPOL?.Port?.LocationCode?.SchedKDType)){
					R402L= firstPOL?.Port?.LocationCode?.SchedKDType?.trim()
				}
				R403L= firstPOL?.Port?.LocationCode?.SchedKDCode?.trim()
			}

			if (firstPOL?.Port?.PortName) {
				R404L= util.substring(firstPOL?.Port?.PortName?.trim(),1,24).toUpperCase() //Customization - R404UpperCase
			}else if(firstPOL?.Port?.City){
				R404L= util.substring(firstPOL?.Port?.City?.trim(),1,24).toUpperCase() //Customization - R404UpperCase
			}
			if(R403L || R404L){
				'Loop_R4' {
					'R4' {
						'E115_01' 'L'
						'E309_02' R402L
						'E310_03' R403L
						'E114_04' R404L
						if (util.isNotEmpty(firstPOL?.Port?.CSCountryCode) && firstPOL?.Port?.CSCountryCode?.length()<=2) {
							'E26_05' firstPOL?.Port?.CSCountryCode?.trim()
						}

						if (util.isNotEmpty(firstPOL?.CSStateCode)) {
							'E156_08' util.substring(firstPOL?.CSStateCode?.trim(),1,2).padRight(2)
						}
					}
					LocDT polDTM = null
					def isAct = false
					if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						isAct = true
						polDTM = firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						polDTM =firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}

					if(polDTM){
						'DTM'{
							if(isAct){
								'E374_01' '140'
							}else{
								'E374_01' '139'
							}

							'E373_02' util.convertDateTime(polDTM, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDTM, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
//--------------------------------------POD--------------------------------
			LastPOD lastPod=current_Body?.Route?.LastPOD
			def R402D=null
			def R403D=null
			def R404D=null

			if (lastPod?.Port?.LocationCode?.UNLocationCode) {
				R402D= 'UN'
				R403D= lastPod?.Port?.LocationCode?.UNLocationCode?.trim()
			}else if(util.isNotEmpty(lastPod?.Port?.LocationCode?.SchedKDCode) ){
				if(util.isNotEmpty(lastPod?.Port?.LocationCode?.SchedKDType)){
					R402D= lastPod?.Port?.LocationCode?.SchedKDType?.trim()
				}
				R403D= lastPod?.Port?.LocationCode?.SchedKDCode?.trim()
			}

			if (lastPod?.Port?.PortName) {
				R404D= util.substring(lastPod?.Port?.PortName?.trim(),1,24).toUpperCase() //Customization - R404UpperCase
			}else if(lastPod?.Port?.City){

				R404D= util.substring(lastPod?.Port?.City?.trim(),1,24).toUpperCase() //Customization - R404UpperCase
			}
			if(R403D||R404D){
				'Loop_R4' {
					'R4' {
						'E115_01' 'D'
						'E309_02' R402D
						'E310_03' R403D
						'E114_04' R404D
						if (util.isNotEmpty(lastPod?.Port?.CSCountryCode) && lastPod?.Port?.CSCountryCode?.length()<=2) {
							'E26_05' lastPod?.Port?.CSCountryCode?.trim()
						}
						if (util.isNotEmpty(lastPod?.CSStateCode)) {
							'E156_08' util.substring(lastPod?.CSStateCode?.trim()?.padRight(2),1,2)   //MapStateCodeFromCSStateCode
						}
					}

					LocDT podDTM = null
					def isAct = false
					if(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						isAct = true
						podDTM = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						podDTM =lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}

					if(podDTM){
						'DTM'{
							if(isAct){
								'E374_01' '140'
							}else{
								'E374_01' '139'
							}

							'E373_02' util.convertDateTime(podDTM, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podDTM, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
//--------------------------------------FND--------------------------------
			FND fnd=current_Body?.Route?.FND
			def R402E=null
			def R403E=null
			def R404E=null

			if (fnd?.CityDetails?.LocationCode?.UNLocationCode) {
				R402E='UN'
				R403E=fnd?.CityDetails?.LocationCode?.UNLocationCode?.trim()
			}else if(util.isNotEmpty(fnd?.CityDetails?.LocationCode?.SchedKDCode)){
				if( util.isNotEmpty(fnd?.CityDetails?.LocationCode?.SchedKDType)){
					R402E=fnd?.CityDetails?.LocationCode?.SchedKDType?.trim()
				}
				R403E=fnd?.CityDetails?.LocationCode?.SchedKDCode?.trim()
			}
			if (fnd?.CityDetails?.City) {
				R404E= util.substring(fnd?.CityDetails?.City?.trim(),1,24).toUpperCase() //Customization - R404UpperCase
			}
			if(R403E || R404E){
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						'E309_02' R402E
						'E310_03' R403E
						'E114_04'R404E

						if (util.isNotEmpty(fnd?.CSStandardCity?.CSCountryCode) && fnd?.CSStandardCity?.CSCountryCode?.length()<=2) {
							'E26_05' fnd?.CSStandardCity?.CSCountryCode?.trim()
						}
						if (fnd?.CSStandardCity?.CSStateCode) {
							'E156_08' util.substring(fnd?.CSStandardCity?.CSStateCode?.trim(),1,2).padRight(2)
						}
					}

					def ActCargoDeliveryDT=current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='A'}?.LocDT
					def EventLocDT=current_Body?.Event?.EventDT?.LocDT
					def ActFNDArrivalDT=current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator=='A'}?.LocDT
					def isActDate=false
					def FndDTM0203=null

					if(util.isNotEmpty(ActCargoDeliveryDT) && util.isNotEmpty(EventLocDT) && (util.convertDateTime(ActCargoDeliveryDT,xmlDateTimeFormat,yyyyMMdd) <= util.convertDateTime(EventLocDT,xmlDateTimeFormat,yyyyMMdd))){
						isActDate=true
						FndDTM0203=ActCargoDeliveryDT //1	MapQualifierFromActCargoDeliveryDtLocLtEventDtLoc	140
					}else if(util.isNotEmpty(ActFNDArrivalDT) && util.isNotEmpty(EventLocDT) && (util.convertDateTime(ActFNDArrivalDT,xmlDateTimeFormat,yyyyMMdd) <= util.convertDateTime(EventLocDT,xmlDateTimeFormat,yyyyMMdd))){
						isActDate=true
						FndDTM0203=ActFNDArrivalDT  //2	MapQualifierFromActArrivalDtFndLocLtEventDtLoc	140
					}else if(lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator=='A'}?.LocDT)
					{
						isActDate=true
						FndDTM0203=lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator=='A'}?.LocDT //3	MapQualifierFromActArrivalDtLastPodLoc	140
					}else if(current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='E'}?.LocDT)
					{
						isActDate=false
						FndDTM0203=current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='E'}?.LocDT //4	MapQualifierFromEstCargoDeliveryDtLoc	139
					}else if(current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator=='E'}?.LocDT)
					{
						isActDate=false
						FndDTM0203=current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator=='E'}?.LocDT  //5	MapQualifierFromEstArrivalDtFndLoc	139
					}else if(lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator=='E'}?.LocDT)
					{
						isActDate=false
						FndDTM0203=lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator=='E'}?.LocDT  //6	MapQualifierFromLastVoyageEstArrivalDtLoc	139
					}

					if(FndDTM0203){
						'DTM'{
							if (isActDate) {
								'E374_01' '140'
							} else {
								'E374_01' '139'
							}
							'E373_02' util.convertDateTime(FndDTM0203, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(FndDTM0203, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
//===============================================R4 OVER========================================================

			'SE' {
				//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
				'E96_01' '-999'
				'E329_02' '-999'
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
		//special logic for get scac from first body
		SCAC = ct.Body[0]?.GeneralInformation?.SCAC

		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
//
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->


			def eventCS2Cde = current_Body?.Event?.CS1Event?.trim() //1.0 use trim
			def eventExtCde = util.getConversion('DDHCTOPOCEAN', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

			if (errorKeyList.size() == 0) {
				//pass validateBeforeExecution
				//main mapping

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

		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID);
		if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);

		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = util.cleanXml(writer?.toString())
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()

		return result;
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		//MissingStatusCode
		ctUtil.missingEventStatusCode(eventCS2Cde,eventExtCde,false,'-Missing Status Code.',errorKeyList)
		//MissingEventDtLoc, ES,
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body?.Event?.EventDT?.LocDT, true, '-Missing Status Event Date', errorKeyList)
		//TODO: FilterNon1stPolIngate  ,isErr:No, CS008, 'CS008 Ingate Event not at POL.'
		ctUtil.filterNon1sstPolIngate(eventCS2Cde,'CS008',current_Body?.Event,current_Body?.Route?.firstPOL,false,null,errorKeyList)
		//FilterCS260WhenIBIntermodal
		ctUtil.filterIBIntermodal(eventCS2Cde,'CS260',current_Body?.Route?.Inbound_intermodal_indicator,false," - Filter CS260 because contains I/B intermodal.",errorKeyList)

		return errorKeyList;
	}

	public boolean pospValidation() {
		//posp expception: MissingB403 , due to this prep checking 'MissingStatusCode' for B403, so no need to add POSP checking for B403

	}
}