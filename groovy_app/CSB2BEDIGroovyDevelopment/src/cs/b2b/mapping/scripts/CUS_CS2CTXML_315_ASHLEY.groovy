package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * @author Amy
 * ASHLEY CT initialize on 20170622
 */
public class CUS_CS2CTXML_315_ASHLEY {

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
		def vCS1EventCodeConversion = util.getConversion('ASHLEY', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  null 		//get the first 5 char, in case duplicate event missing direction.
		if(util.getConversion('ASHLEY','CT', 'O','EventDirection',vCS1EventFirst5, conn)){ 
			shipDir = util.getConversion('ASHLEY','CT', 'O','EventDirection',vCS1EventFirst5, conn)
		}else if(util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)){
			shipDir =util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)
		}


		 def ContainerSizeType = util.getConversion('ASHLEY','CT','O','ContainerType',current_Body?.Container?.CarrCntrSizeType,conn)
		//def SCAC=current_Body?.GeneralInformation?.SCAC

		def eventUNLocationCode = current_Body?.Event?.Location?.LocationCode?.UNLocationCode
		def eventSKDCode = current_Body?.Event?.Location?.LocationCode?.SchedKDCode
		def eventSKDType = current_Body?.Event?.Location?.LocationCode?.SchedKDType
		def eventCity = current_Body?.Event?.Location?.CityDetails?.City

		//Customization
		def queryNonMovementDT = ctUtil.getConversionByIntCdeandScacCde('EventNonMovementDT',vCS1Event,SCAC,conn)

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '-999'
			}
			
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body.Route?.OceanLeg){
				firstOceanLeg = current_Body.Route.OceanLeg[0]
				lastOceanLeg = current_Body.Route.OceanLeg[-1]
			}
			
			'B4' {
				//-----------B403 special handling for CS060/CS090 to map AE on the last leg/POL-------pleaser see ERS#NATEDI1471d------------
				def spcialEventmap=['CS060':'AE','CS090':'AE']
				if(spcialEventmap.containsKey(vCS1Event) && current_Body?.Event?.Location?.CityDetails?.City && lastOceanLeg?.POL?.Port?.City){
					if(current_Body?.Event?.Location?.CityDetails?.City?.toUpperCase().trim().equals(lastOceanLeg?.POL?.Port?.City?.toUpperCase().trim())){
						'E157_03' spcialEventmap.get(vCS1Event)
					}else{
						'E157_03' vCS1EventCodeConversion
					}
				}
				else{
					'E157_03' vCS1EventCodeConversion
				}

				if(queryNonMovementDT.get('EXT_CDE')=='GMT'){
					//Customization -  NonMovementEventDtInGmt

					'E373_04' util.convertDateTime(current_Body?.Event?.EventDT?.GMT, xmlDateTimeFormat, yyyyMMdd)

					'E161_05' util.convertDateTime(current_Body?.Event?.EventDT?.GMT, xmlDateTimeFormat, HHmm)
				}else{

					'E373_04' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

					'E161_05' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)
				}

				if (util.isNotEmpty(current_Body?.Event?.Location?.LocationCode?.SchedKDCode)) {
					'E159_06'  current_Body?.Event?.Location?.LocationCode?.SchedKDCode
				}else if (util.isNotEmpty(current_Body?.Event?.Location?.LocationCode?.UNLocationCode)) {
					'E159_06'  current_Body?.Event?.Location?.LocationCode?.UNLocationCode
				}

				if(current_Body?.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body?.Container?.ContainerNumber,1,4)
				}

				if(current_Body.Container?.ContainerNumber?.length()>=5){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, 10)
				}


				if(current_Body?.Event?.CS1Event =='CS210' && util.isEmpty(current_Body?.Container?.ContainerStatus)){
					'E578_09'  'E'
				}else if(current_Body?.Event?.CS1Event !='CS210' && util.isEmpty(current_Body?.Container?.ContainerStatus)){
					'E578_09'  'L'
				}else if(current_Body?.Container?.ContainerStatus){
					'E578_09' util.substring(current_Body?.Container?.ContainerStatus,1,1)
				}else {
					'E578_09' 'L'
				}
                //B410, sequence1-MapEquipmentTypeFromTPCodeConv; sequence2-MapEquipmentTypeFromCarrCntrSizeType
				if(util.isNotEmpty(ContainerSizeType)){
					'E24_10' ContainerSizeType
				}else if(current_Body?.Container?.CarrCntrSizeType?.trim()){
					'E24_10' current_Body?.Container?.CarrCntrSizeType?.trim()
				}

				def B412=null
				if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode?.trim()){ //B411 MapLocationIdentifierFromEventSchedKD sequence=4
					'E310_11' util.substring(current_Body?.Event?.Location?.LocationCode?.SchedKDCode?.trim(),1,30)
					B412=current_Body?.Event?.Location?.LocationCode?.SchedKDType?.trim()
				}else if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode?.trim()){ //B411 MapLocationIdentifierFromEventUnCode sequence=1
					'E310_11' current_Body?.Event?.Location?.LocationCode?.UNLocationCode?.trim()
					B412='UN'
				}else if(current_Body?.Event?.Location?.CityDetails?.City?.trim()){  //B411 MapLocationIdentifierFromEventCity  sequence=2
					'E310_11' util.substring(current_Body?.Event?.Location?.CityDetails?.City?.trim(),1,30)
					B412='CI'
				}else if(current_Body?.Event?.Location?.LocationName?.trim()){  //B411 MapLocationIdentifierFromLocationName sequence=3
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
			def N9SegmentsLimitation=30
			//--------------------------BookingGeneralInfo - MapReferenceIndentificationWithScac-------------------------------------
			current_Body?.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it?.CarrierBookingNumber?.trim())}?.each{current_BookingGeneralInfo ->
				def currentBKnum=current_BookingGeneralInfo?.CarrierBookingNumber?.trim()
				def BKnumPrex=null

				if(currentBKnum.startsWith(SCAC)){
					BKnumPrex=currentBKnum
				}else{
					BKnumPrex=SCAC + currentBKnum
				}
				if(BKnumPrex && N9SegmentsLimitation>0){
					'N9' {
						'E128_01' 'BN'
						'E127_02' util.substring((BKnumPrex),1,30)
					}
					N9SegmentsLimitation--
				}
			}
            
			//-----------------------BLGeneralInfo---MapReferenceIndentification----------------------------------------------------------------------------
			current_Body?.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it?.BLNumber?.trim())}?.each{current_BLGeneralInfo ->
				if(current_BLGeneralInfo?.BLNumber?.trim() && N9SegmentsLimitation>0){
					'N9' {
						'E128_01' 'BM'
						'E127_02' util.substring(current_BLGeneralInfo?.BLNumber?.trim(),1,30)
					}
					N9SegmentsLimitation--
				}
			}

			//---------------------------Scac--------------------------------------------------------------
			if(util.isNotEmpty(SCAC) && N9SegmentsLimitation>0){
				'N9' {
					'E128_01' 'SCA'
					'E127_02' SCAC
				}
				N9SegmentsLimitation--
			}

			//-----------------SealNum-------------------------------------------------------
			current_Body.Container?.Seal?.findAll{StringUtil.isNotEmpty(it.SealNumber?.trim())}?.each{current_seal ->
				if(current_seal?.SealNumber?.trim() && N9SegmentsLimitation>0){
					'N9' {
						'E128_01' 'SN'
						'E127_02' util.substring(current_seal?.SealNumber?.trim(),1,30)
					}
					N9SegmentsLimitation--
				}
			}
			
			//-----------------------------ExternalReference-------------------------------------------------------
			def externalReferenceTypeMap = ['CGO':'CG','FR':'FN']
			current_Body?.ExternalReference?.each {current_ExtReference ->
				if(externalReferenceTypeMap.containsKey(current_ExtReference?.CSReferenceType) && N9SegmentsLimitation>0){
					'N9' {
						'E128_01' externalReferenceTypeMap.get(current_ExtReference?.CSReferenceType)
						'E127_02' util.substring(current_ExtReference?.ReferenceNumber,1,30)  //1.0 did substring first 50 chars, but CS STD IG, N902 max length is 30
					}
					N9SegmentsLimitation--
				}
			}

//===============================================N9 OVER=======================================================


//===============================================Q2 START===has T/S logic====================================================
			List<OceanLeg> oceanlegCheckedPod = []
			List<OceanLeg> oceanLegCheckedPol = []
			current_Body?.Route?.OceanLeg?.each {current_Oceanleg->
				//for T/S-I
				if(current_Oceanleg?.POD?.Port?.LocationCode?.UNLocationCode?.equals(eventUNLocationCode)){
					oceanlegCheckedPod.add(current_Oceanleg)
				}else if(current_Oceanleg?.POD?.Port?.City?.equals(eventCity)){
					oceanlegCheckedPod.add(current_Oceanleg)
				}
				// for T/S-O
				if(current_Oceanleg?.POL?.Port?.LocationCode?.UNLocationCode?.equals(eventUNLocationCode)){
					oceanLegCheckedPol.add(current_Oceanleg)
				}else if(current_Oceanleg?.POL?.Port?.City?.equals(eventCity)){
					oceanLegCheckedPol.add(current_Oceanleg)
				}
			}

			OceanLeg lastoceanLeg_TS_Inbound = null
			OceanLeg firstoceanLeg_TS_outbound = null
			if(oceanlegCheckedPod.size()>0){
				lastoceanLeg_TS_Inbound = oceanlegCheckedPod.get(oceanlegCheckedPod.size()-1)

			}

			if(oceanLegCheckedPol.size()>0){
				firstoceanLeg_TS_outbound = oceanLegCheckedPol.get(0)
			}




			// sum all cargo gross weight as KGS and volume as CBM

			def weightCalMap = ['LBS':'0.453597024']
			def volumeCalMap = ['CBF':'0.028316579']



			BigDecimal sumGrossWeigth = new BigDecimal (0)
			BigDecimal sumCargoVolume = new BigDecimal (0)



			current_Body?.Cargo?.each {current_Cargo->

				BigDecimal grossWeigth = new BigDecimal (0)
				BigDecimal cargoVolume = new BigDecimal (0)
				def weightUnit = current_Cargo?.GrossWeight?.WeightUnit
				def volumeUnit = current_Cargo?.Volume?.VolumeUnit

				// GrossWeight
				if(current_Cargo?.GrossWeight?.Weight){
					BigDecimal weight = new BigDecimal(current_Cargo?.GrossWeight?.Weight)

					if(weightCalMap.containsKey(current_Cargo?.GrossWeight?.WeightUnit))
					{
						grossWeigth = (weight * (new BigDecimal(weightUnit))).setScale(2,BigDecimal.ROUND_HALF_UP)
					}
					else if(weightUnit == 'KGS'){
						grossWeigth = new BigDecimal(weight.toString())
					}
				}
				sumGrossWeigth = sumGrossWeigth.add(grossWeigth)

                // Volume
				if(current_Cargo?.Volume?.Volume){

					BigDecimal volume = new BigDecimal(current_Cargo?.Volume?.Volume)

					if(volumeCalMap.containsKey(current_Cargo?.Volume?.VolumeUnit)){
						cargoVolume = (volume * (new BigDecimal(volumeCalMap.get(volumeUnit))).setScale(2,BigDecimal.ROUND_HALF_UP))
					}
					else if(volumeUnit == 'CBM'){
						cargoVolume = new BigDecimal(volume.toString())
					}
				}

				sumCargoVolume = sumCargoVolume.add(cargoVolume)

			}

			def Q207 = null
			def Q214 = null
			if(sumGrossWeigth!=0 && util.isNotEmpty(sumGrossWeigth)){
				Q207 = sumGrossWeigth   //Math.round(sumGrossWeigth*100)/100
			}
			if(sumCargoVolume!=0 && util.isNotEmpty(sumCargoVolume)){
				Q214 = sumCargoVolume   //Math.round(sumCargoVolume*100)/100

			}

			if (shipDir == 'I') {
				'Q2' {
					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E597_01' util?.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 8)
					}
					if(lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode?.trim()){
						'E26_02' util?.substring(lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode?.trim(),1,2)
					}
					//Q204=OceanLeg.POL.DepartureDT.LocDT ~ map date part only CCYYMMDD ~ map Indicator=A first else E
					//Q205=OceanLeg.POD.ArrivalDTLocDT ~ map date part only CCYYMMDD ~ map Indicator=A first else E
					def Q204=null
					def Q205=null
					if(lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						Q204=lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						Q204=lastOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}
					if(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						Q205=lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						Q205=lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}
					if(Q204){
						'E373_04' util.convertXmlDateTime(Q204, yyyyMMdd)
					}
					if(Q205){
						'E373_05' util.convertXmlDateTime(Q205, yyyyMMdd)
					}
					if(current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty){
						'E80_06' current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty
					}

					if(Q207){
						'E81_07' Q207
						'E187_08' 'G'
					}

					if (lastOceanLeg?.SVVD?.Discharge?.Voyage) {
						'E55_09' lastOceanLeg?.SVVD?.Discharge?.Voyage?.trim() + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction?.trim():"")
					}else if(lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage){
						'E55_09' lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage?.trim()
					}

					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E897_12' 'L'
					}


					if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
						'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName?.trim(),1,28)
					}

					if(Q214){
						'E183_14'  Q214
						'E184_15' 'X'
					}

					if(Q207){
						'E188_16' 'K'
					}

				}
			} else if(shipDir =='T/S-I'){
					'Q2' {
					if (lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.LloydsNumber) {
						'E597_01' util?.substring(lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.LloydsNumber, 1, 8)
					}
					if(lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.RegistrationCountryCode?.trim()){
						'E26_02' util?.substring(lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.RegistrationCountryCode?.trim(),1,2)
					}
					//Q204=OceanLeg.POL.DepartureDT.LocDT ~ map date part only CCYYMMDD ~ map Indicator=A first else E
					//Q205=OceanLeg.POD.ArrivalDTLocDT ~ map date part only CCYYMMDD ~ map Indicator=A first else E
					def Q204=null
					def Q205=null
					if(lastoceanLeg_TS_Inbound?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						Q204=lastoceanLeg_TS_Inbound?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(lastoceanLeg_TS_Inbound?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						Q204=lastoceanLeg_TS_Inbound?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}
					if(lastoceanLeg_TS_Inbound?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						Q205=lastoceanLeg_TS_Inbound?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(lastoceanLeg_TS_Inbound?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						Q205=lastoceanLeg_TS_Inbound?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}
					if(Q204){
						'E373_04' util.convertXmlDateTime(Q204, yyyyMMdd)
					}
					if(Q205){
						'E373_05' util.convertXmlDateTime(Q205, yyyyMMdd)
					}
					if(current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty){
						'E80_06' current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty
					}

					if(Q207){
						'E81_07' Q207
						'E187_08' 'G'
					}

					if (lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.Voyage) {
						'E55_09' lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.Voyage?.trim() + (lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.Direction ? lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.Direction?.trim():"")
					}else if(lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.ExternalVoyage){
						'E55_09' lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.ExternalVoyage?.trim()
					}
					if (lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.LloydsNumber) {
						'E897_12' 'L'
					}

					if (lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.VesselName) {
						'E182_13' util.substring(lastoceanLeg_TS_Inbound?.SVVD?.Discharge?.VesselName?.trim(),1,28)
					}

					if(Q214){
						'E183_14'  Q214
						'E184_15' 'X'
					}

					if(Q207){
						'E188_16' 'K'
					}

				}

			}else if (shipDir == 'O') {
				'Q2' {
					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 8)
					}
					if(firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode?.trim()){
						'E26_02' util?.substring(firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode?.trim(),1,2)
					}
					//Q204=OceanLeg.POL.DepartureDT.LocDT ~ map date part only CCYYMMDD ~ map Indicator=A first else E
					//Q205=OceanLeg.POD.ArrivalDTLocDT ~ map date part only CCYYMMDD ~ map Indicator=A first else E
					def Q204=null
					def Q205=null
					if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						Q204=firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						Q204=firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}
					if(firstOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						Q205=firstOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(firstOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						Q205=firstOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}
					if(Q204){
						'E373_04' util.convertXmlDateTime(Q204, yyyyMMdd)
					}
					if(Q205){
						'E373_05' util.convertXmlDateTime(Q205, yyyyMMdd)
					}
					if(current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty){
						'E80_06' current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty
					}

					if(Q207){
						'E81_07' Q207
						'E187_08' 'G'
					}

					if (firstOceanLeg?.SVVD?.Loading?.Voyage) {
						'E55_09' firstOceanLeg?.SVVD?.Loading?.Voyage?.trim() + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction?.trim():"")
					}else if(firstOceanLeg?.SVVD?.Loading?.ExternalVoyage){
						'E55_09' firstOceanLeg?.SVVD?.Loading?.ExternalVoyage?.trim()
					}
					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E897_12' 'L'
					}

					if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName?.trim(),1,28)
					}

					if(Q214){
						'E183_14'  Q214
						'E184_15' 'X'
					}

					if(Q207){
						'E188_16' 'K'
					}

				}
			}else if(shipDir =='T/S-O'){
				'Q2' {
					if (firstoceanLeg_TS_outbound?.SVVD?.Loading?.LloydsNumber) {
						'E597_01' util?.substring(firstoceanLeg_TS_outbound?.SVVD?.Loading?.LloydsNumber, 1, 8)
					}
					if(firstoceanLeg_TS_outbound?.SVVD?.Loading?.RegistrationCountryCode?.trim()){
						'E26_02' util?.substring(firstoceanLeg_TS_outbound?.SVVD?.Loading?.RegistrationCountryCode?.trim(),1,2)
					}
					//Q204=OceanLeg.POL.DepartureDT.LocDT ~ map date part only CCYYMMDD ~ map Indicator=A first else E
					//Q205=OceanLeg.POD.ArrivalDTLocDT ~ map date part only CCYYMMDD ~ map Indicator=A first else E
					def Q204=null
					def Q205=null
					if(firstoceanLeg_TS_outbound?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						Q204=firstoceanLeg_TS_outbound?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(firstoceanLeg_TS_outbound?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						Q204=firstoceanLeg_TS_outbound?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}
					if(firstoceanLeg_TS_outbound?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT){
						Q205=firstoceanLeg_TS_outbound?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT
					}else if(firstoceanLeg_TS_outbound?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT){
						Q205=firstoceanLeg_TS_outbound?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}
					if(Q204){
						'E373_04' util.convertXmlDateTime(Q204, yyyyMMdd)
					}
					if(Q205){
						'E373_05' util.convertXmlDateTime(Q205, yyyyMMdd)
					}
					if(current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty){
						'E80_06' current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty
					}

					if(Q207){
						'E81_07' Q207
						'E187_08' 'G'
					}

					if (firstoceanLeg_TS_outbound?.SVVD?.Loading?.Voyage) {
						'E55_09' firstoceanLeg_TS_outbound?.SVVD?.Loading?.Voyage?.trim() + (firstoceanLeg_TS_outbound?.SVVD?.Loading?.Direction ? firstoceanLeg_TS_outbound?.SVVD?.Loading?.Direction?.trim():"")
					}else if(firstoceanLeg_TS_outbound?.SVVD?.Loading?.ExternalVoyage){
						'E55_09' firstoceanLeg_TS_outbound?.SVVD?.Loading?.ExternalVoyage?.trim()
					}

					if (firstoceanLeg_TS_outbound?.SVVD?.Loading?.LloydsNumber) {
						'E897_12' 'L'
					}

					if (firstoceanLeg_TS_outbound?.SVVD?.Loading?.VesselName) {
						'E182_13' util.substring(firstoceanLeg_TS_outbound?.SVVD?.Loading?.VesselName?.trim(),1,28)

					}

					if(Q214){
						'E183_14'  Q214
						'E184_15' 'X'
					}

					if(Q207){
						'E188_16' 'K'
					}

				}

			}

//===============================================Q2 OVER========================================================


//===============================================R4 START=======================================================
			//-----------special logic for CS060 and CS090---------------------------------
			
			//tib:trim($Start/group/pfx5:T-315/pfx5:S-B4_2/pfx5:E-157_2_03) = "AR" or tib:trim($Start/group/pfx5:T-315/pfx5:S-B4_2/pfx5:E-157_2_03) = "UR"
			//---------------------------POR-----------------------------------------
			POR por = current_Body?.Route?.POR
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if(util.isNotEmpty(por?.CityDetails?.LocationCode?.SchedKDCode)){
						if(util.isNotEmpty(por?.CityDetails?.LocationCode?.SchedKDType)){
							'E309_02' por?.CityDetails?.LocationCode?.SchedKDType?.trim()
						}
						'E310_03' por?.CityDetails?.LocationCode?.SchedKDCode?.trim()
					}else if (por?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' por?.CityDetails?.LocationCode?.UNLocationCode?.trim()
					}
					if (por?.CityDetails?.City) {
						'E114_04' util.substring(por?.CityDetails?.City?.trim(),1,24)
					}
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
          //---------------------------POL-----------------------------------------
			FirstPOL firstPOL=current_Body?.Route?.FirstPOL
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if(util.isNotEmpty(firstPOL?.Port?.LocationCode?.SchedKDCode)){
						if(util.isNotEmpty(firstPOL?.Port?.LocationCode?.SchedKDType)){
							'E309_02' firstPOL?.Port?.LocationCode?.SchedKDType?.trim()
						}
						'E310_03' firstPOL?.Port?.LocationCode?.SchedKDCode?.trim()
					}else if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' firstPOL?.Port?.LocationCode?.UNLocationCode?.trim()
					}

					if (firstPOL?.Port?.PortName) {
						'E114_04' util.substring(firstPOL?.Port?.PortName?.trim(),1,24)
					}else if(firstPOL?.Port?.City){
						'E114_04' util.substring(firstPOL?.Port?.City?.trim(),1,24)
					}

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
			//---------------------------POD-----------------------------------------
			LastPOD lastPod=current_Body?.Route?.LastPOD
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if(util.isNotEmpty(lastPod?.Port?.LocationCode?.SchedKDCode) ){
						if(util.isNotEmpty(lastPod?.Port?.LocationCode?.SchedKDType)){
							'E309_02' lastPod?.Port?.LocationCode?.SchedKDType?.trim()
						}
						'E310_03' lastPod?.Port?.LocationCode?.SchedKDCode?.trim()
					}else if (lastPod?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' lastPod?.Port?.LocationCode?.UNLocationCode?.trim()
					}

					if (lastPod?.Port?.PortName) {
						'E114_04' util.substring(lastPod?.Port?.PortName?.trim(),1,24)
					}else if(lastPod?.Port?.City){

						'E114_04' util.substring(lastPod?.Port?.City?.trim(),1,24)
					}
					if (util.isNotEmpty(lastPod?.Port?.CSCountryCode) && lastPod?.Port?.CSCountryCode?.length()<=2) {
						'E26_05' lastPod?.Port?.CSCountryCode?.trim()
					}
					if (util.isNotEmpty(lastPod?.CSStateCode) && lastPod?.CSStateCode?.length()<=2) {
						'E156_08' lastPod?.CSStateCode?.trim()?.padRight(2)
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
			
			//---------------------------FND-----------------------------------------
			FND fnd=current_Body?.Route?.FND
			'Loop_R4' {
				'R4' {
					'E115_01' 'M'					
					if(util.isNotEmpty(fnd?.CityDetails?.LocationCode?.SchedKDCode)){
						if( util.isNotEmpty(fnd?.CityDetails?.LocationCode?.SchedKDType)){
							'E309_02' fnd?.CityDetails?.LocationCode?.SchedKDType?.trim()
						}
						'E310_03' fnd?.CityDetails?.LocationCode?.SchedKDCode?.trim()
					}else if (fnd?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' fnd?.CityDetails?.LocationCode?.UNLocationCode?.trim()
					}


					if (fnd?.CityDetails?.City) {
						'E114_04' util.substring(fnd?.CityDetails?.City?.trim(),1,24)
					}

					if (util.isNotEmpty(fnd?.CSStandardCity?.CSCountryCode) && fnd?.CSStandardCity?.CSCountryCode?.length()<=2) {
						'E26_05' fnd?.CSStandardCity?.CSCountryCode?.trim()
					}
					if (fnd?.CSStandardCity?.CSStateCode) {
						'E156_08' util.substring(fnd?.CSStandardCity?.CSStateCode?.trim(),1,2).padRight(2)
					}
				}

				LocDT fndDTM = null
				def isAct = false

				if(current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator=='A'}?.LocDT){
					fndDTM = current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator=='A'}?.LocDT
					isAct = true
				}else if(fnd?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT){
					fndDTM = fnd?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT
					isAct = true
				}else if(current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator =='E'}?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.InBound =='C'){
					fndDTM = current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator =='E'}?.LocDT
				}else if(fnd?.ArrivalDT?.find{it?.attr_Indicator =='E'}?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.InBound=='M'){
					fndDTM = fnd?.ArrivalDT?.find{it?.attr_Indicator =='E'}?.LocDT
				}

				if(fndDTM){
					'DTM'{
						if(isAct){
							'E374_01' '140'
						}else{
							'E374_01' '139'
						}
						'E373_02' util.convertDateTime(fndDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(fndDTM, xmlDateTimeFormat, HHmm)
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

        //Duplicate logic 2: duplicate by event code
        //2-1, duplication -- CT special logic, get event duplicate config in code conversion
        Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
        //2-2, duplication details
        List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->


			def eventCS2Cde = current_Body?.Event?.CS1Event?.trim() //1.0 use trim
			def eventExtCde = util.getConversion('ASHLEY', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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

		//		println bizKeyWriter.toString();
		//		println csuploadWriter.toString();

		//promote csupload and bizkey to session
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
		ctUtil.missingEventStatusCode(eventCS2Cde,eventExtCde,true,null,errorKeyList)

		//MissingCarrierBookingNumber
		ctUtil.missingBookingNumber(eventCS2Cde,current_Body?.BookingGeneralInfo,true,null,errorKeyList)

		//MissingContainerNumber
		ctUtil.missingContainerNumber(eventCS2Cde,current_Body?.Container,true,null,errorKeyList)

		//FilterCS260WhenIBIntermodal
		ctUtil.filterIBIntermodal(eventCS2Cde,'CS260',current_Body?.Route?.Inbound_intermodal_indicator,false," - Filter CS260 because contains I/B intermodal.",errorKeyList)
		//MissingEventLocationCodeUNAndKD
		String missingEventLocationCdeUNandKDmsg = util.substring('-City: '+(current_Body?.Event?.Location?.CityDetails?.City ? current_Body?.Event?.Location?.CityDetails?.City : '')+' -Country: '+(current_Body?.Event?.Location?.CityDetails?.Country ? current_Body?.Event?.Location?.CityDetails?.Country : '')+' - Missing event location in both UN and KD',1,100)
		ctUtil.missingLocationCodeUNandKD(eventCS2Cde,current_Body?.Event?.Location?.LocationCode?.UNLocationCode,current_Body?.Event?.Location?.LocationCode?.SchedKDCode,true,missingEventLocationCdeUNandKDmsg,errorKeyList)

		//MissingPodLocationCodesUNAndKD
		String missingPODLocationCdeUNandKDmsg = util.substring('-City: '+(current_Body?.Route?.LastPOD?.Port?.City ? current_Body?.Route?.LastPOD?.Port?.City  : '')+' -Country: '+(current_Body?.Route?.LastPOD?.Port?.Country ? current_Body?.Route?.LastPOD?.Port?.Country: '')+'-Missing POD Location Code UN and KD.',1,100)

		ctUtil.missingLocationCodeUNandKD(eventCS2Cde,current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode,current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode,true,missingPODLocationCdeUNandKDmsg,errorKeyList)
		//MissingEventLocationNameAndCity
		ctUtil.missingEventLocationNameandCity(eventCS2Cde,current_Body?.Event,true,null,errorKeyList)
		//MissingEventCountryCode
		String missingEventCountryCodemsg = "- Missing event country code."+(current_Body?.Event?.Location?.CityDetails?.City ? current_Body?.Event?.Location?.CityDetails?.City :'')+" "+(current_Body?.Event?.Location?.CityDetails?.Country ? current_Body?.Event?.Location?.CityDetails?.Country :'')

		ctUtil.missingEventCountryCode(eventCS2Cde,current_Body?.Event,true,missingEventCountryCodemsg,errorKeyList)
		//MissingPolLocationCodesUNAndKD
		String missingPOLLocationCdeUNandKDmsg = util.substring('-City: '+(current_Body?.Route?.FirstPOL?.Port?.City ? current_Body?.Route?.FirstPOL?.Port?.City  : '')+' -Country: '+(current_Body?.Route?.FirstPOL?.Port?.Country ? current_Body?.Route?.FirstPOL?.Port?.Country: '')+'-Missing POL Location Code UN and KD.',1,100)
//      
		ctUtil.missingLocationCodeUNandKD(eventCS2Cde,current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode,current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode,true,missingPOLLocationCdeUNandKDmsg,errorKeyList)
		//MissingPorCity
		ctUtil.missingCityDetailsCity(eventCS2Cde,current_Body?.Route?.POR?.CityDetails?.City,true,'- Missing POR City.',errorKeyList)
		//MissingFndCity
		ctUtil.missingCityDetailsCity(eventCS2Cde,current_Body?.Route?.FND?.CityDetails?.City,true,'- Missing FND City.',errorKeyList)
		//MissingPorCSCountryCode
		ctUtil.missingPORCountryCode(eventCS2Cde,current_Body?.Route?.POR,null,true,null,errorKeyList)
		//MissingPodCSCountryCode
		ctUtil.missingPODCountryCode(eventCS2Cde,current_Body?.Route?.LastPOD,true,null,errorKeyList)
		//MissingPolCSCountryCode
		ctUtil.missingPOLCountryCode(eventCS2Cde,current_Body?.Route?.FirstPOL,true,null,errorKeyList)

		//MissingFndCSCountryCode
		ctUtil.missingFNDCountryCode(eventCS2Cde,current_Body?.Route?.FND,true,null,errorKeyList)

		return errorKeyList;
	}

	public boolean pospValidation() {

	}
}






