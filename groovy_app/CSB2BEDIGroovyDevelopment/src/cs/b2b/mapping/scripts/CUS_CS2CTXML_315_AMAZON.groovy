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
 * @author Tracy
 * AMAZON CT initialize on 20161115
 */
public class CUS_CS2CTXML_315_AMAZON {

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
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('AMAZON', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  null 		//get the first 5 char, in case duplicate event missing direction.
		if(util.getConversion('AMAZON','CT', 'O','EventDirection',vCS1EventFirst5, conn)){
			shipDir = util.getConversion('AMAZON','CT', 'O','EventDirection',vCS1EventFirst5, conn)
		}else if(util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)){
			shipDir =util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)
		}


		def ContainerSizeTypebySCAC=ctUtil.ConvertContainerSizeType('ContainerType','AMAZON',current_Body?.GeneralInformation?.SCAC,'X.12',current_Body?.Container?.CarrCntrSizeType,'O',conn)
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
				'E329_02' '    '
			}
			'B4' {


				'E157_03' vCS1EventCodeConversion

				if(queryNonMovementDT.get('EXT_CDE')=='GMT'){
					//Customization -  NonMovementEventDtInGmt

					'E373_04' util.convertDateTime(current_Body?.Event?.EventDT?.GMT, xmlDateTimeFormat, yyyyMMdd)

					'E161_05' util.convertDateTime(current_Body?.Event?.EventDT?.GMT, xmlDateTimeFormat, HHmm)
				}else{

					'E373_04' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

					'E161_05' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)
				}

				def B406andB411 = null
				def B412 = null
				if(eventUNLocationCode){
					B406andB411=eventUNLocationCode
					B412 ='UN'
				}

				if(B406andB411){
					'E159_06' B406andB411
				}

				if(current_Body?.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body?.Container?.ContainerNumber,1,4)
				}

				if(current_Body.Container?.ContainerNumber?.length()>=5){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber,5,current_Body?.Container?.ContainerNumber?.length())
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

				if(util.isNotEmpty(ContainerSizeTypebySCAC)){
					'E24_10' ContainerSizeTypebySCAC
				}else if(current_Body?.Container?.CarrCntrSizeType){
					'E24_10' current_Body?.Container?.CarrCntrSizeType
				}

				if(B406andB411){
					'E310_11' B406andB411
				}

				if(B412){
					'E309_12'  B412
				}

				if(util.isNotEmpty(current_Body?.Container?.ContainerCheckDigit)){
					'E761_13' current_Body?.Container?.ContainerCheckDigit
				}

			}
			//Loop N9
			//for avoid duplicated N9
			def N9SegmentsLimitation  = 30
			current_Body?.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it?.BLNumber)}?.each{current_BLGeneralInfo ->
				def blNumber = null
				def blNumPrex = util.substring(current_BLGeneralInfo.BLNumber,1,4)
				if(N9SegmentsLimitation>0){
					if(blNumPrex == SCAC){
						blNumber = current_BLGeneralInfo.BLNumber
					}else{
						blNumber = SCAC + current_BLGeneralInfo.BLNumber
					}
					'N9' {
						'E128_01' 'BM'
						'E127_02' util.substring(blNumber,1,30)
					}
					N9SegmentsLimitation--
				}
			}

			if(util.isNotEmpty(SCAC) && N9SegmentsLimitation>0){
				'N9' {
					'E128_01' 'SCA'
					'E127_02' SCAC
				}
				N9SegmentsLimitation--
			}
			def POs = []
			current_Body?.ExternalReference?.findAll{it?.CSReferenceType == 'PO'}?.each{ currentPO ->
				if (!POs.contains(currentPO.ReferenceNumber) && N9SegmentsLimitation>0) {
					POs.add(currentPO.ReferenceNumber)
					'N9' {
						'E128_01' 'PO'
						'E127_02' util.substring(currentPO.ReferenceNumber,1,30)
					}
					N9SegmentsLimitation--
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
					if(current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty){
						'E80_06' current_Body?.BookingGeneralInfo[0]?.Packaging?.PackageQty
					}

					if(Q207){
						'E81_07' Q207
						'E187_08' 'G'
					}


//					if(sumGrossWeigth!=0 && util.isNotEmpty(sumGrossWeigth)){
//						'E81_07' Math.round(sumGrossWeigth*100)/100
//						'E187_08' 'G'
//						'E188_16' 'K'
//					}

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
			POR por = current_Body?.Route?.POR
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if (por?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' por?.CityDetails?.LocationCode?.UNLocationCode?.trim()
					}else if(util.isNotEmpty(por?.CityDetails?.LocationCode?.SchedKDCode) && util.isNotEmpty(por?.CityDetails?.LocationCode?.SchedKDType)){
						'E309_02' por?.CityDetails?.LocationCode?.SchedKDType?.trim()
						'E310_03' por?.CityDetails?.LocationCode?.SchedKDCode?.trim()
					}
//					else{
//						'E309_02' "ZZ"
//						'E310_03' "XXXX"
//					}

					if (por?.CityDetails?.City) {
						'E114_04' util.substring(por?.CityDetails?.City?.trim(),1,24)?.toUpperCase()
					}
					if (util.isNotEmpty(por?.CSStandardCity?.CSCountryCode) && por?.CSStandardCity?.CSCountryCode?.length()<=2) {
						'E26_05' por?.CSStandardCity?.CSCountryCode?.trim()
					}
					if (por?.CSStandardCity?.CSStateCode) {
						'E156_08' util.substring(por?.CSStandardCity?.CSStateCode,1,2)
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
						'E623_04' 'LT'
					}
				}

			}

			FirstPOL firstPOL=current_Body?.Route?.FirstPOL
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' firstPOL?.Port?.LocationCode?.UNLocationCode?.trim()
					}else if(util.isNotEmpty(firstPOL?.Port?.LocationCode?.SchedKDCode) &&util.isNotEmpty(firstPOL?.Port?.LocationCode?.SchedKDType)){
						'E309_02' firstPOL?.Port?.LocationCode?.SchedKDType?.trim()
						'E310_03' firstPOL?.Port?.LocationCode?.SchedKDCode?.trim()
					}
//					else{
//						'E309_02' "ZZ"
//						'E310_03' "XXXX"
//					}

					if (firstPOL?.Port?.PortName) {
						'E114_04' util.substring(firstPOL?.Port?.PortName?.trim(),1,24).toUpperCase()
					}else if(firstPOL?.Port?.City){
						'E114_04' util.substring(firstPOL?.Port?.City?.trim(),1,24).toUpperCase()
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
						'E623_04' 'LT'
					}
				}

			}

			LastPOD lastPod=current_Body?.Route?.LastPOD
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if (lastPod?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' lastPod?.Port?.LocationCode?.UNLocationCode?.trim()
					}else if(util.isNotEmpty(lastPod?.Port?.LocationCode?.SchedKDCode) && util.isNotEmpty(lastPod?.Port?.LocationCode?.SchedKDType)){
						'E309_02' lastPod?.Port?.LocationCode?.SchedKDType?.trim()
						'E310_03' lastPod?.Port?.LocationCode?.SchedKDCode?.trim()
					}
//					else{
//						'E309_02' "ZZ"
//						'E310_03' "XXXX"
//					}

					if (lastPod?.Port?.PortName) {
						'E114_04' util.substring(lastPod?.Port?.PortName?.trim(),1,24).toUpperCase()
					}else if(lastPod?.Port?.City){

						'E114_04' util.substring(lastPod?.Port?.City?.trim(),1,24).toUpperCase()
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
						'E623_04' 'LT'
					}
				}


			}

			FND fnd=current_Body?.Route?.FND
			'Loop_R4' {
				'R4' {
					'E115_01' 'E'
					if (fnd?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' fnd?.CityDetails?.LocationCode?.UNLocationCode?.trim()
					}else if(util.isNotEmpty(fnd?.CityDetails?.LocationCode?.SchedKDCode) && util.isNotEmpty(fnd?.CityDetails?.LocationCode?.SchedKDType)){
						'E309_02' fnd?.CityDetails?.LocationCode?.SchedKDType?.trim()
						'E310_03' fnd?.CityDetails?.LocationCode?.SchedKDCode?.trim()
					}
//					else{
//						'E309_02' "ZZ"
//						'E310_03' "XXXX"
//					}

					if (fnd?.CityDetails?.City) {
						'E114_04' util.substring(fnd?.CityDetails?.City?.trim(),1,24).toUpperCase()
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
						'E623_04' 'LT'
					}

				}

			}

			'Loop_R4' {
				'R4'{
					'E115_01' '5'
					if (eventUNLocationCode) {
						'E309_02' 'UN'
						'E310_03' eventUNLocationCode?.trim()
					}else if(eventSKDCode){
						'E309_02' eventSKDType ? eventSKDType : ''
						'E310_03' eventSKDCode?.trim()
					}


					if (eventCity) {
						'E114_04' util.substring(eventCity?.trim(),1,24)?.toUpperCase()
					}else if(current_Body?.Event?.Location?.LocationName){
						'E114_04' util.substring(current_Body?.Event?.Location?.LocationName?.trim(),1,24).toUpperCase()
					}

					if (util.isNotEmpty(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode) && current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode?.length()<=2) {
						'E26_05' current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode?.trim()
					}
					if (current_Body?.Event?.Location?.CSStandardCity?.CSStateCode) {
						'E156_08' util.substring(current_Body?.Event?.Location?.CSStandardCity?.CSStateCode?.trim(),1,2).padRight(2)
					}
				}


			}

//===============================================R4 OVER========================================================

			'SE' {
				//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
				'E96_01' ' '
				'E329_02' '    '
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
//		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
//
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

		//start body looping
		ct.Body.eachWithIndex { current_Body, current_BodyIndex ->


			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('AMAZON', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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
			result = writer?.toString()
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
		String missingEventLocationCdeUNandKDmsg = util.substring('- City : '+(current_Body?.Event?.Location?.CityDetails?.City ? current_Body?.Event?.Location?.CityDetails?.City : '')+'- Country : '+(current_Body?.Event?.Location?.CityDetails?.Country ? current_Body?.Event?.Location?.CityDetails?.Country : '')+' - Missing event location in both UN and KD.',1,100)
		ctUtil.missingLocationCodeUNandKD(eventCS2Cde,current_Body?.Event?.Location?.LocationCode?.UNLocationCode,current_Body?.Event?.Location?.LocationCode?.SchedKDCode,true,missingEventLocationCdeUNandKDmsg,errorKeyList)

		//MissingPodLocationCodesUNAndKD
		String missingPODLocationCdeUNandKDmsg = util.substring('- City : '+(current_Body?.Route?.LastPOD?.Port?.City ? current_Body?.Route?.LastPOD?.Port?.City  : '')+'- Country : '+(current_Body?.Route?.LastPOD?.Port?.Country ? current_Body?.Route?.LastPOD?.Port?.Country: '')+' - Missing POD location in both UN and KD.',1,100)

		ctUtil.missingLocationCodeUNandKD(eventCS2Cde,current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode,current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode,true,missingPODLocationCdeUNandKDmsg,errorKeyList)
		//MissingEventLocationNameAndCity
		ctUtil.missingEventLocationNameandCity(eventCS2Cde,current_Body?.Event,true,null,errorKeyList)
		//MissingEventCountryCode
		String missingEventCountryCodemsg = "- Missing event country code."+(current_Body?.Event?.Location?.CityDetails?.City ? current_Body?.Event?.Location?.CityDetails?.City :'')+" "+(current_Body?.Event?.Location?.CityDetails?.Country ? current_Body?.Event?.Location?.CityDetails?.Country :'')

		ctUtil.missingEventCountryCode(eventCS2Cde,current_Body?.Event,true,missingEventCountryCodemsg,errorKeyList)
		//MissingPolLocationCodesUNAndKD
		String missingPOLLocationCdeUNandKDmsg = util.substring('- City : '+(current_Body?.Route?.FirstPOL?.Port?.City ? current_Body?.Route?.FirstPOL?.Port?.City  : '')+'- Country : '+(current_Body?.Route?.FirstPOL?.Port?.Country ? current_Body?.Route?.FirstPOL?.Port?.Country: '')+' - Missing POL location in both UN and KD.',1,100)

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
		//missing Event Locationcde
		ctUtil.missingEventUNLocationCode(eventCS2Cde,current_Body?.Event,true,null,errorKeyList)


		return errorKeyList;
	}



	public boolean pospValidation() {

	}

}






