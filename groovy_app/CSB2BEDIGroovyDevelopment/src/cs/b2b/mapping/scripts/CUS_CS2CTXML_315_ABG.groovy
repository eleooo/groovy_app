package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List;
import java.util.Map;

import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.Event
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * @author NICOLE
 * ABG CT initialize on 20161115 
 */
public class CUS_CS2CTXML_315_ABG {

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
	
	//Process Select SQL for main mapping and exception check
	public Map<String, String> sqlValue = new HashMap<String, String>();
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

	def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('ABG', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
//		def shipDir=getShipDir('SONYDSG','Q2','O','ShipDirection',vCS1EventFirst5,'EventStatusCode',conn)
//		def currentSTCtrlNum = '###edi_Group_Ctrl_Number###' + String.format("%04d", current_BodyIndex+1)
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {

				'E157_03' vCS1EventCodeConversion

				'E373_04' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)

				if(current_Body.Container?.ContainerNumber?.length()>0){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
				}


				if(current_Body.Container?.ContainerNumber?.length()>4){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, 6)
				}


				if (current_Body.Container?.ContainerStatus?.trim() == 'Empty') {
					'E578_09' 'E'
				} else if (current_Body.Container?.ContainerStatus?.trim() == 'Laden') {
					'E578_09' 'L'
				} else if (current_Body.Event?.CS1Event?.trim() == 'CS210') {
					'E578_09' 'E'
				} else {
					'E578_09' 'L'
				}

				if(current_Body.Container?.CarrCntrSizeType?.trim() != null && current_Body.Container?.CarrCntrSizeType?.trim() != ''){
					'E24_10' current_Body.Container?.CarrCntrSizeType
				}

				if(util.substring(current_Body.Event?.Location?.LocationCode?.UNLocationCode,1,5).trim() != ''
				&& util.substring(current_Body.Event?.Location?.LocationCode?.UNLocationCode,1,5).trim() != null){

					'E310_11' util.substring(current_Body.Event?.Location?.LocationCode?.UNLocationCode,1,5)

				}else if(util.substring(current_Body.Event?.Location?.LocationCode?.SchedKDCode,1,5).trim() != ''
				&& util.substring(current_Body.Event?.Location?.LocationCode?.SchedKDCode,1,5).trim() != null){

					'E310_11' util.substring(current_Body.Event?.Location?.LocationCode?.SchedKDCode,1,5)

				}else if(util.substring(current_Body.Event?.Location?.CityDetails?.City,1,30).trim() != null &&
				util.substring(current_Body.Event?.Location?.CityDetails?.City,1,30).trim() != ''){

					'E310_11' util.substring(current_Body.Event?.Location?.CityDetails?.City,1,30).trim()

				}else if(['CS180'].contains(vCS1EventFirst5) && current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode?.trim()!='' && current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode?.trim()!=null){

					'E310_11' util.substring(current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode?.trim(),1,5)

				}else if(['CS180'].contains(vCS1EventFirst5) && current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode?.trim()!='' && current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode?.trim()!=null){

					'E310_11' util.substring(current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode?.trim(),1,5)

				}else if(['CS180'].contains(vCS1EventFirst5) && current_Body.Route?.FND?.CityDetails?.City?.trim()!='' && current_Body.Route?.FND?.CityDetails?.City?.trim()!=null){

					'E310_11' util.substring(current_Body.Route?.FND?.CityDetails?.City?.trim(),1,30)


				}

				if(current_Body.Event?.Location?.LocationCode?.UNLocationCode?.trim() != '' && current_Body.Event?.Location?.LocationCode?.UNLocationCode?.trim() != null){
					'E309_12' 'UN'
				}else if(current_Body.Event?.Location?.LocationCode?.SchedKDCode?.trim() != '' && current_Body.Event?.Location?.LocationCode?.SchedKDCode?.trim() != null && current_Body.Event?.Location?.LocationCode?.SchedKDType?.trim()!='' && current_Body.Event?.Location?.LocationCode?.SchedKDType?.trim()!=null){
					'E309_12' util.substring(current_Body.Event?.Location?.LocationCode?.SchedKDType?.trim(),1,1)
				}else if(current_Body.Event?.Location?.CityDetails?.City?.trim()!='' && current_Body.Event?.Location?.CityDetails?.City?.trim()!=null && current_Body.Event?.Location?.LocationCode?.SchedKDCode?.length()<1){
					'E309_12' 'CI'
				}else if(['CS180'].contains(vCS1EventFirst5) && current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode?.trim()!='' && current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode?.trim()!=null){
					'E309_12' 'UN'
				}else if(['CS180'].contains(vCS1EventFirst5) && current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode?.trim()!='' && current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode?.trim()!=null && current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDType?.trim()!='' && current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDType?.trim()!=null){
					'E309_12' util.substring(current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDType?.trim(),1,2)
				}else if(['CS180'].contains(vCS1EventFirst5) && current_Body.Route?.FND?.CityDetails?.City?.trim()!='' && current_Body.Route?.FND?.CityDetails?.City?.trim()!=null && current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode?.length()<1){
					'E309_12' 'CI'
				
				}

				//In T2T will substring(1,2) while T2E will output substring(1,1)
				if(current_Body.Container?.ContainerCheckDigit){
					'E761_13' util.substring(current_Body.Container?.ContainerCheckDigit,1,1);
				}

			}
			//Loop N9
			//for avoid duplicated N9
			def BMs = []
			def BNs = []
			def EQs = []
			def FNs = []


//			current_Body.BLGeneralInfo.each { current_BLGeneralInfo ->
//				if (current_BLGeneralInfo.BLNumber && !BMs.contains(current_BLGeneralInfo.BLNumber?.trim())) {
//					BMs.add(current_BLGeneralInfo.BLNumber?.trim())
//					'N9' {
//						'E128_01' 'BM'
//						'E127_02' current_BLGeneralInfo.BLNumber?.trim()
//					}
//				}
//
//			}
			
			current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber?.trim()}.each{BLNumber, BLNumberGroup ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(BLNumber,1,30)
				}
			}


//			current_Body.BookingGeneralInfo.each { current_BookingGeneralInfo ->
//				if (current_BookingGeneralInfo.CarrierBookingNumber && !BNs.contains(current_BookingGeneralInfo.CarrierBookingNumber?.trim())) {
//					BNs.add(current_BookingGeneralInfo.CarrierBookingNumber?.trim())
//					'N9' {
//						'E128_01' 'BN'
//						'E127_02' current_BookingGeneralInfo.CarrierBookingNumber?.trim()
//					}
//				}
//			}
			
			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each {CarrierBookingNumber, CarrierBookingNumberGroup ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(CarrierBookingNumber,1,30)
				}
			}
			
			if (current_Body.Container?.RailPickupNumber && (util.substring(current_Body.Route?.FirstPOL?.Port?.CSCountryCode?.trim(),1,2) == 'CA' ) && !EQs.contains(current_Body.Container?.RailPickupNumber )) {
				EQs.add(current_Body.Container?.RailPickupNumber)
				'N9' {
					'E128_01' 'P8'
					'E127_02' util.substring(current_Body.Container?.RailPickupNumber,1,30)
					'E369_03' 'Canada shipment/cargo'
				}
			}


			current_Body.ExternalReference.findAll{it.CSReferenceType == 'FN'}.each{ currentPO ->
				if (!FNs.contains(currentPO.ReferenceNumber)) {
					FNs.add(currentPO.ReferenceNumber)
					'N9' {
						'E128_01' 'FN'
						'E127_02' util.substring(currentPO.ReferenceNumber,1,30)
					}
				}
			}


			def lastOceanLeg_map1 = []
			def lastOceanLeg_map = []
			def firstOceanLeg_map1 = []
			def firstOceanLeg_map2 = []
			current_Body.Route?.OceanLeg?.each{current_Oceanleg ->
				if(current_Oceanleg?.POD?.Port?.PortName?.trim().equals(current_Body.Event?.Location?.LocationName?.trim())
				&& current_Oceanleg?.POD?.Port?.City?.trim().equals(current_Body.Event?.Location?.CityDetails?.City?.trim())){
					lastOceanLeg_map1.add(current_Oceanleg)
				}


				if(current_Oceanleg?.SVVD?.Loading?.Vessel?.length()>0 || (current_Oceanleg?.SVVD?.Loading?.Voyage?.length()>0 || current_Oceanleg?.SVVD?.Loading?.Direction?.length()>0 )|| current_Oceanleg.SVVD?.Loading?.VesselName?.length()>0)
				{
					
					lastOceanLeg_map.add(current_Oceanleg)
				
				}

				if(current_Oceanleg?.POL?.Port?.PortName?.trim().equals(current_Body.Event?.Location?.LocationName?.trim())
				&& current_Oceanleg?.POL?.Port?.City?.trim().equals(current_Body.Event?.Location?.CityDetails?.City?.trim())){
					firstOceanLeg_map2.add(current_Oceanleg)
				}
				if(current_Oceanleg.LegSeq == '1'){
					firstOceanLeg_map1.add(current_Oceanleg)
				}

			}


			OceanLeg firstOceanLeg1 = firstOceanLeg_map1[0]
			OceanLeg firstOceanLeg2 = firstOceanLeg_map2[0]
			OceanLeg lastOceanLeg1
			OceanLeg lastOceanLeg
			OceanLeg IQ212FirstOceanLeg
			if(lastOceanLeg_map.size() > 0){
				lastOceanLeg = lastOceanLeg_map[-1]
				IQ212FirstOceanLeg = lastOceanLeg_map[0]
			}
			if(lastOceanLeg_map1.size() > 0){
				lastOceanLeg1 = lastOceanLeg_map1[-1]
			}
			if (shipDir == 'I') {
				if(['CS080', 'CS100'].contains(vCS1EventFirst5)){
					'Q2' {
						if (lastOceanLeg1?.SVVD?.Discharge?.LloydsNumber) {
							'E597_01' util.substring(lastOceanLeg1?.SVVD?.Discharge?.LloydsNumber, 1, 7)
						}
						if(lastOceanLeg1?.SVVD?.Discharge?.RegistrationCountryCode){
							'E26_02' util.substring(lastOceanLeg1?.SVVD?.Discharge?.RegistrationCountryCode?.trim(),1,60)
						}

						if (lastOceanLeg1?.SVVD?.Discharge?.Voyage || lastOceanLeg1?.SVVD?.Discharge?.Direction) {
							'E55_09' util.substring((lastOceanLeg1?.SVVD?.Discharge?.Voyage ? lastOceanLeg1?.SVVD?.Discharge?.Voyage : "") + (lastOceanLeg1?.SVVD?.Discharge?.Direction ? lastOceanLeg1?.SVVD?.Discharge?.Direction : ""),1,10)
						}
						'E128_10' 'SCA'
						if(IQ212FirstOceanLeg?.SVVD?.Discharge?.LloydsNumber){
						'E897_12' 'L'
						
						}
						if (lastOceanLeg1?.SVVD?.Discharge?.VesselName?.length()>0) {
							'E182_13' util.substring(lastOceanLeg1?.SVVD?.Discharge?.VesselName,1,28)
						}
					}
				}else{
					'Q2' {

						if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
							'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)
						}
						if(lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode){
							'E26_02' util.substring(lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode.trim(),1,60)
						}

						if (lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction) {
							'E55_09' util.substring((lastOceanLeg?.SVVD?.Discharge?.Voyage ? lastOceanLeg?.SVVD?.Discharge?.Voyage : "") + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction : ""),1,10)
						}
						'E128_10' 'SCA'
						if(IQ212FirstOceanLeg?.SVVD?.Discharge?.LloydsNumber){
						'E897_12' 'L'
						}
						if (lastOceanLeg?.SVVD?.Discharge?.VesselName?.length()>0) {
							'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName,1,28)
						}
					}
				}

			} else if (shipDir == 'O') {

				if(['CS090', 'CS110'].contains(vCS1EventFirst5)){
					'Q2' {
						if (firstOceanLeg2?.SVVD?.Loading?.LloydsNumber) {
							'E597_01' util.substring(firstOceanLeg2?.SVVD?.Loading?.LloydsNumber, 1, 7)
						}
						if(firstOceanLeg2?.SVVD?.Loading?.RegistrationCountryCode){
							'E26_02' util.substring(firstOceanLeg2?.SVVD?.Loading?.RegistrationCountryCode?.trim(),1,60)
						}

						if (firstOceanLeg2?.SVVD?.Loading?.Voyage || firstOceanLeg2?.SVVD?.Loading?.Direction) {
							'E55_09' util.substring((firstOceanLeg2?.SVVD?.Loading?.Voyage ? firstOceanLeg2?.SVVD?.Loading?.Voyage : "") + (firstOceanLeg2?.SVVD?.Loading?.Direction ? firstOceanLeg2?.SVVD?.Loading?.Direction : ""),1,10)
						}

						'E128_10' 'SCA'
						if(firstOceanLeg1?.SVVD?.Loading?.LloydsNumber){
						'E897_12' 'L'
						}
						if (firstOceanLeg2?.SVVD?.Loading?.VesselName?.length()>0) {
							'E182_13' util.substring(firstOceanLeg2?.SVVD?.Loading?.VesselName,1,28)
						}
					}
				}else{
					'Q2' {
						if (firstOceanLeg1?.SVVD?.Loading?.LloydsNumber) {
							'E597_01' util.substring(firstOceanLeg1?.SVVD?.Loading?.LloydsNumber, 1, 7)
						}
						if(firstOceanLeg1?.SVVD?.Loading?.RegistrationCountryCode){
							'E26_02' util.substring(firstOceanLeg1?.SVVD?.Loading?.RegistrationCountryCode?.trim(),1,60)
						}

						if (firstOceanLeg1?.SVVD?.Loading?.Voyage || firstOceanLeg1?.SVVD?.Loading?.Direction) {
							'E55_09' util.substring((firstOceanLeg1?.SVVD?.Loading?.Voyage ? firstOceanLeg1?.SVVD?.Loading?.Voyage :"") + (firstOceanLeg1?.SVVD?.Loading?.Direction ? firstOceanLeg1?.SVVD?.Loading?.Direction : ""),1,10)
						}

						'E128_10' 'SCA'
						if(firstOceanLeg1?.SVVD?.Loading?.LloydsNumber){
						'E897_12' 'L'
						}
						if (firstOceanLeg1?.SVVD?.Loading?.VesselName?.length()>0) {
							'E182_13' util.substring(firstOceanLeg1?.SVVD?.Loading?.VesselName,1,28)
						}
					}
				}
			}

			//R4
			FirstPOL firstPOL =  current_Body.Route?.FirstPOL
			OceanLeg firstOceanLeg = current_Body.Route?.OceanLeg[0]
			OceanLeg OceanLegMax
			if(current_Body.Route?.OceanLeg.size()>0){
				 OceanLegMax = current_Body.Route?.OceanLeg[-1]
			}
			LastPOD lastPOD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND
			POR POR =  current_Body.Route?.POR
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (POR?.CityDetails?.LocationCode?.SchedKDCode && POR?.CityDetails?.LocationCode?.SchedKDType) {
						'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType
					}else if(POR?.CityDetails?.City){
						'E309_02' 'CI'
					
					}
					if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode
					} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' POR?.CityDetails?.LocationCode?.SchedKDCode
					}else if(POR?.CityDetails?.City){
						'E310_03' POR?.CityDetails?.City
					}
					if (POR?.CityDetails?.City) {
						'E114_04' util.substring(POR?.CityDetails?.City, 1, 24)
					}
					//Tibco R4/EventPOR/Type8(>=2 was corrected by 2.0)
					if (POR?.CSStandardCity?.CSCountryCode?.length() >= 2 ){
						'E26_05' POR?.CSStandardCity?.CSCountryCode
					}else if(POR?.CityDetails?.Country?.length()>0){
						'E26_05' sqlValue.get("R4R05")
					}
					if (POR?.CSStandardCity?.CSStateCode) {
						'E156_08' util.substring(POR?.CSStandardCity?.CSStateCode,1,2)
					}
				}

				LocDT porDTM = null
				def isActF_A = false
				def isActF_E = false
				def isActC_A = false
				def isFullR_A = false
				def isFirst_A = false
				def isFirst_E = false

				if (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'A'}?.LocDT) {
					porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isActF_A = true
				} else if (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT) {
					porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT
					isActF_E = true
				} else if (current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT) {
					porDTM = current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isActC_A = true
				}else if(current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					porDTM = current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isFullR_A = true
				}else if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					porDTM = firstOceanLeg?.POL.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isFirst_A = true
				}else if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT){
					porDTM = firstOceanLeg?.POL.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
					isFirst_E = true
				}
				if (porDTM) {
					'DTM' {
						if (isActF_A || isActC_A || isFirst_A ){
							'E374_01' '140'
						} else {
							'E374_01' '139'
						}
						'E373_02' util.convertDateTime(porDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(porDTM, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}
			}

			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (firstPOL?.Port?.LocationCode?.SchedKDCode && firstPOL?.Port?.LocationCode?.SchedKDType) {
						'E309_02' util.substring(firstPOL?.Port?.LocationCode?.SchedKDType,1,2)
					
					}
					if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E310_03' util.substring(firstPOL?.Port?.LocationCode?.UNLocationCode,1,5)
					} else if (firstPOL.Port?.LocationCode?.SchedKDCode) {
						'E310_03' util.substring(firstPOL?.Port?.LocationCode?.SchedKDCode,1,5)
					}else if(firstPOL.Port?.City){
						'E310_03' util.substring(firstPOL?.Port?.City?.trim(),1,30)
					}
					if (firstPOL.Port?.PortName) {
						'E114_04' util.substring(firstPOL?.Port?.PortName, 1, 24)
					}
					if (firstPOL?.Port?.CSCountryCode?.length() >= 2){
						'E26_05' firstPOL?.Port?.CSCountryCode
					}
					if (firstPOL?.CSStateCode) {
						'E156_08' util.substring(firstPOL?.CSStateCode,1,2)
					}
				}

				LocDT locActDTM = null
				LocDT locEstDTM = null
				def isAct = false
				def isEst = false

				if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					locActDTM = firstOceanLeg?.POL?.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}
				if(firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT){
					locEstDTM = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
					isEst = true
				}


				if (isAct) {
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(locActDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(locActDTM, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}
				else if (isEst) {
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertDateTime(locEstDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(locEstDTM, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}

			}

            
			//POT only when there are more than one OceanLeg
			def OceanLegIndex=0
			def BeforeOceanLeg=null
			current_Body.Route?.OceanLeg.each {
				OceanLegIndex++
				if(OceanLegIndex>1 && (it?.POL?.Port?.LocationCode?.UNLocationCode || (it?.POL?.Port?.LocationCode?.SchedKDCode))){
					def TempOcean=it
								'Loop_R4' {
									'R4' {
										'E115_01' 'Y'
										
										if (TempOcean?.POL?.Port?.LocationCode?.UNLocationCode) {
											'E309_02' 'UN'
										} else if (!TempOcean?.POL?.Port?.LocationCode?.SchedKDCode && TempOcean?.POL?.Port?.LocationCode?.SchedKDType) {
											'E309_02' util.substring(TempOcean?.POL?.Port?.LocationCode?.SchedKDType,1,2)
										}
										if (TempOcean?.POL?.Port?.LocationCode?.UNLocationCode) {
											'E310_03' util.substring(TempOcean?.POL?.Port?.LocationCode?.UNLocationCode,1,5)
										} else if (TempOcean?.POL?.Port?.LocationCode?.SchedKDCode) {
											'E310_03' util.substring(TempOcean?.POL?.Port?.LocationCode?.SchedKDCode,1,5)
										}
										if (TempOcean?.POL?.Port?.City) {
											'E114_04' util.substring(TempOcean?.POL?.Port?.City, 1, 24)
										}
										if (TempOcean?.POL?.Port?.CSCountryCode){
											'E26_05' util.substring(TempOcean?.POL?.Port?.CSCountryCode,1,2)
										}
										if(TempOcean?.POL?.CSStateCode){
											'E156_08' util.substring(TempOcean?.POL?.CSStateCode,1,2)
										}
										
									
									
									
								}
									def isAct=false
									def formatDTM=null
									if(BeforeOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT)
									{
										isAct=true
										formatDTM=BeforeOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
									}
									else if(BeforeOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT)
									{
										isAct=false
										formatDTM=BeforeOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
									}
									if(formatDTM){
										'DTM' {
											if(isAct)
											{
												'E374_01' '140'
											}
											else{
												'E374_01' '139'
											}
											'E373_02' util.convertDateTime(formatDTM, xmlDateTimeFormat, yyyyMMdd)
											'E337_03' util.convertDateTime(formatDTM, xmlDateTimeFormat, HHmm)
											'E623_04' 'LT'
										}
										
									}
				              }
				            }
				BeforeOceanLeg=it
			               }
			

			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					} else if (lastPOD?.Port?.LocationCode?.SchedKDCode && lastPOD?.Port?.LocationCode?.SchedKDType) {
						'E309_02' util.substring(lastPOD?.Port?.LocationCode?.SchedKDType,1,2)
					}else if(lastPOD?.Port?.City){
						'E309_02' 'CI'
					}
					if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
						'E310_03' util.substring(lastPOD?.Port?.LocationCode?.UNLocationCode,1,5)
					} else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
						'E310_03' util.substring(lastPOD?.Port?.LocationCode?.SchedKDCode,1,5)
					}else if(lastPOD?.Port?.City){
						'E310_03' util.substring(lastPOD?.Port?.City?.trim(),1,30)
					}
					if (lastPOD?.Port?.PortName) {
						'E114_04' util.substring(lastPOD?.Port?.PortName, 1, 24)
					}
					if (lastPOD?.Port?.CSCountryCode?.length() >= 2){
						'E26_05' lastPOD?.Port?.CSCountryCode
					}
					if (lastPOD?.CSStateCode) {
						'E156_08' util.substring(lastPOD?.CSStateCode,1,2)
					}
				}

				LocDT locActDTM = null
				LocDT locEstDTM = null
				def isAct = false
				def isEst = false


				if(OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					locActDTM = OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}
				if(OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT){
					locEstDTM = OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
					isEst = true
				}


				if (isAct) {
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(locActDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(locActDTM, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}
				else if (isEst) {
					'DTM' {
						'E374_01' '139'
						'E373_02' util.convertDateTime(locEstDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(locEstDTM, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}

			}
			if((FND?.CityDetails?.LocationCode?.UNLocationCode || FND?.CityDetails?.LocationCode?.SchedKDCode || FND?.CityDetails?.City) || (FND?.CityDetails?.City && (FND?.CityDetails?.LocationCode?.UNLocationCode || FND?.CityDetails?.LocationCode?.SchedKDCode))){
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (FND?.CityDetails?.LocationCode?.SchedKDCode && FND?.CityDetails?.LocationCode?.SchedKDType) {
							'E309_02' util.substring(FND?.CityDetails?.LocationCode?.SchedKDType,1,2)
						}else if(FND?.CityDetails?.City && FND?.CityDetails?.LocationCode?.SchedKDCode?.length()<1){
							'E309_02' 'CI'
						
						}
					if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' util.substring(FND?.CityDetails?.LocationCode?.UNLocationCode,1,5)
					} else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' util.substring(FND?.CityDetails?.LocationCode?.SchedKDCode,1,5)
					}else if(FND?.CityDetails?.City){
						'E310_03'  util.substring(FND?.CityDetails?.City?.trim(),1,30)
					}
					if(FND?.CityDetails?.City && (FND?.CityDetails?.LocationCode?.UNLocationCode || FND?.CityDetails?.LocationCode?.SchedKDCode)){
						'E114_04' util.substring(FND?.CityDetails?.City,1,24)
					}
					if (FND?.CSStandardCity?.CSCountryCode?.length() >= 2){
						'E26_05' util.substring(FND?.CSStandardCity?.CSCountryCode,1,2)
					}
					if (FND?.CSStandardCity?.CSStateCode) {
						'E156_08' util.substring(FND?.CSStandardCity?.CSStateCode,1,2)
					}
				}

				def isActC = false
				def isEstC = false
				def isActA = false
				def isEstA = false
				def isActD = false
				def isEstD = false
				LocDT ActC = null
				LocDT EstC = null
				LocDT ActA = null
				LocDT EstA = null
				LocDT ActD = null
				LocDT EstD = null

				if(current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT){

					ActC = current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isActC = true

				}else if(current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT){

					EstC = current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT
					isEstC = true

				}else if(current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT){

					ActA = current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT
					isActA = true

				}else if(current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT && current_Body.GeneralInformation?.SCAC?.trim() != 'OOLU'){

					ActA = current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isActA = true

				}else if(current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT){

					EstA = current_Body.Route?.ArrivalAtFinalHub.find{it.attr_Indicator == 'E'}?.LocDT
					isEstA = true

				}else if(current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT && current_Body.GeneralInformation?.SCAC?.trim() != 'OOLU'){

					EstA = current_Body.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
					isEstA = true

				}else if(OceanLegMax){
					if(OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT){

						ActD = OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
						isActD = true

					}else if(OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT){

						EstD = OceanLegMax?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
						isEstD = true
					}
				}

				if(isActC){
					'DTM' {
						'E374_01' '140'
						'E373_02' util.convertDateTime(ActC, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(ActC, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}else if(isEstC){
					'DTM'{
						'E374_01' '139'
						'E373_02' util.convertDateTime(EstC, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(EstC, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}else if(isActA){
					'DTM'{
						'E374_01' '140'
						'E373_02' util.convertDateTime(ActA, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(ActA, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}else if(isEstA){
					'DTM'{
						'E374_01' '139'
						'E373_02' util.convertDateTime(EstA, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(EstA, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}else if(isActD){
					'DTM'{
						'E374_01' '140'
						'E373_02' util.convertDateTime(ActD, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(ActD, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
				}else if(isEstD){
					'DTM'{
						'E374_01' '139'
						'E373_02' util.convertDateTime(EstD, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(EstD, xmlDateTimeFormat, HHmm)
						'E623_04' 'LT'
					}
									}
//				else{
//					'DTM'{ 'E374_01' '139' }
//				}
			}
		}	
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
		
		//build ISA, GS
//		generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)
		
		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
				
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

		//start body looping
		ct.Body.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('ABG', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

			//Process Select SQL for main mapping and exception check
			String R4_R_05 = getCS2MasterCity(current_Body.Route?.POR?.CityDetails?.Country)
			if(R4_R_05.length()>0){
				sqlValue.put("R4R05",R4_R_05)
			}
			
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
		
		//build GE, IEA
//		generateEDITail(outXml, outputBodyCount)

		//End root node
		outXml.nodeCompleted(null,T315)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		println bizKeyWriter.toString();
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
		
		//				//R4 R Tibco R4/EventPOR/Type8
		def flag
		if (current_Body.Route?.POR?.CSStandardCity?.CSCountryCode?.length() >= 2){
			flag=current_Body.Route?.POR?.CSStandardCity?.CSCountryCode
		}else if(current_Body.Route?.POR?.CityDetails?.Country?.length()>0){
			flag= sqlValue.get("R4R05")
		}
		
		// error cases
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, '- Event not subscribed by Partner', errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
		//R4 5
		ctUtil.missingEventCodeLoc(eventCS2Cde,current_Body.Event, true, null, errorKeyList)
		ctUtil.missingEventFNDCodeLoc(eventCS2Cde, current_Body.Event,current_Body.Route?.FND, true, null, errorKeyList)
//				//B4 D
		ctUtil.missingPODCountryCode(eventCS2Cde, current_Body.Route?.lastPOD, true, null, errorKeyList)
		ctUtil.missingPODLocationCode(eventCS2Cde, current_Body.Route?.LastPOD?.Port?.LocationCode, true, null, errorKeyList)
		ctUtil.missingPODLocationName(eventCS2Cde, current_Body.Route?.LastPOD, true, null, errorKeyList)
		ctUtil.missingPODLocationQual(eventCS2Cde, current_Body.Route?.LastPOD, true, null, errorKeyList)
//				//B4 L
		ctUtil.missingPOLCountryCode(eventCS2Cde, current_Body.Route?.FirstPOL, true, null, errorKeyList)
		ctUtil.missingPOLLocationCode(eventCS2Cde, current_Body.Route?.FirstPOL?.Port?.LocationCode, true, null, errorKeyList)
		ctUtil.missingPOLLocationName(eventCS2Cde, current_Body.Route?.FirstPOL, true, null, errorKeyList)
		ctUtil.missingPOLLocationQual(eventCS2Cde, current_Body.Route?.FirstPOL, true, null, errorKeyList)
//				//R4 R
		ctUtil.missingPORCountryCode(eventCS2Cde, current_Body.Route?.POR, flag, true, null, errorKeyList)
		ctUtil.missingPORLocationName(eventCS2Cde, current_Body.Route?.POR,true, null, errorKeyList)
		ctUtil.missingPORLocationQual(eventCS2Cde, current_Body.Route?.POR, true, null, errorKeyList)
		
		// obsolete cases
		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route?.Inbound_intermodal_indicator, false, null, errorKeyList)
		ctUtil.EventNotSub(eventCS2Cde, eventExtCde, false, null, errorKeyList)

		return errorKeyList;
	}
	private static final String YES = 'YES'
	private static final String NO = 'NO'
	private static final String C = 'C'
	private static final String ERROR_SUPPORT = 'ES'
	private static final String ERROR_COMPLETE = 'EC'
	public void EventNotSub(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(eventExtCde)&&eventExtCde=="XX") {
			errorKey = [TYPE: isError? ERROR_SUPPORT : C, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg: eventCS2Cde + ' - EVT_NOT_SUB']
			errorKeyList.add(errorKey)
		}
	}

	public void missingEventCodeLoc(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Event.Location?.LocationName)&&StringUtil.isEmpty(Event.Location?.CityDetails?.City)&&eventCS2Cde!='CS180'&&StringUtil.isEmpty(Event.Location?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(Event.Location?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(Event.Location?.LocationCode?.SchedKDType))
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Code and Location']
			errorKeyList.add(errorKey)
		}
	}
	public void missingEventFNDCodeLoc(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Event.Location?.LocationName)&&StringUtil.isEmpty(Event.Location?.CityDetails?.City)&&eventCS2Cde=='CS180'&&StringUtil.isEmpty(Event.Location?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(Event.Location?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDType)&&StringUtil.isEmpty(FND?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event and FND Location Code and Name']
			errorKeyList.add(errorKey)
		}
	}
	void missingPODCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LastPOD?.Port?.CSCountryCode)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPODLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingPODLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LastPOD?.Port?.PortName)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + ' -Missing POD Location Name']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingPODLocationQual(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.UNLocationCode)&&StringUtil.isNotEmpty(LastPOD?.Port?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POD Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}
	void missingFNDCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CSStandardCity?.CSContinentCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingFNDLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode?.UNLocationCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingFNDLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Name']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FirstPOL?.Port?.CSCountryCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FirstPOL?.Port?.PortName)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Name']
			errorKeyList.add(errorKey)
		}
	}

	void missingPOLLocationQual(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.UNLocationCode)&&StringUtil.isNotEmpty(FirstPOL?.Port?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POL Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingPORCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR,String flag, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(POR?.CSStandardCity?.CSCountryCode)&&StringUtil.isEmpty(flag)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPORLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingPORLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(POR?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Name']
			errorKeyList.add(errorKey)
		}
	}
	void missingPORLocationQual(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.UNLocationCode)&&StringUtil.isNotEmpty(POR?.CityDetails?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POR Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}
	public boolean pospValidation() {

	}
	
	private void generateEDIHeader(MarkupBuilder outXml, def appSessionId, ContainerMovement ContainerMovement, def currentSystemDt) {
		//		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID.text());
				cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement?.Header?.InterchangeMessageID);
		//		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC?.text());
				cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC);
		
				outXml.'ISA' {
					'I01_01' '00'
					'I02_02' '          '
					'I03_03' '00'
					'I04_04' '          '
					'I05_05' 'ZZ'
					'I06_06' 'CARGOSMART     '
					'I05_07' 'ZZ'
					'I07_08' 'CARGOSMARTABG  '
					'I08_09' currentSystemDt.format("yyMMdd")
					'I09_10' currentSystemDt.format(HHmm)
					'I10_11' 'U'
					'I11_12' '00401'
					'I12_13' '1'
					'I13_14' '0'
					'I14_15' 'P'
					'I15_16' '>'
				}
				outXml.'GS' {
					'E479_01' 'QO'
					'E142_02' 'CARGOSMART'
					'E124_03' 'CARGOSMARTABG'
					'E373_04' currentSystemDt.format(yyyyMMdd)
					'E337_05' currentSystemDt.format(HHmm)
					'E28_06' '1'
					'E455_07' 'X'
					'E480_08' '004010'
				}
		
			}
	
	//add by Harry
	private void generateEDITail(MarkupBuilder outXml, def outputBodyCount) {
		outXml.'GE' {
			'E97_01' outputBodyCount
			'E28_02' '1'
		}
		outXml.'IEA' {
			'I16_01' '1'
			'I12_02' '1'
		}
	}
	
	public String getShipDir(String TP_ID, String Seg_ID, String DIR_ID, String convertTypeId, String Int_Cde,String Seg_Num,  Connection conn) throws Exception {
		if (conn == null)
		return "";
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_edi_cde_ref where tp_id=? and dir_id =? and  CONVERT_TYPE_ID =? and seg_id=? and seg_num=? and int_cde =?";
		
		try {
		pre = conn.prepareStatement(sql);
		pre.setMaxRows(util.getDBRowLimit());
		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
		pre.setString(1, TP_ID);
		pre.setString(2, DIR_ID);
		pre.setString(3, convertTypeId);
		pre.setString(4, Seg_ID);
		pre.setString(5, Seg_Num);
		pre.setString(6, Int_Cde);
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
	
	public String getCS2MasterCity(String City_Name){
		if (conn == null)
			return "";
			
			String ret = ""; 
			PreparedStatement pre = null;
			ResultSet result = null;
			String sql = "select distinct cntry_cde  from cs2_master_city where  UPPER(CNTRY_NME) =UPPER(?) ";
			
			try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(1);
			pre.setQueryTimeout(20);
			pre.setString(1, City_Name);
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

}






