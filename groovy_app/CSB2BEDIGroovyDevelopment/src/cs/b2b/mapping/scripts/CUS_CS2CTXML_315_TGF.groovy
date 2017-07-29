package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
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
 * @author LIYU
 * TGF CT initialize on 20170227
 * Reference WEYERHAEUSER and EFREIGHT
 */
public class CUS_CS2CTXML_315_TGF {

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

	def firstScac = null

	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		def vCS1Event = current_Body.Event?.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCarrCntrSizeType = current_Body.Container?.CarrCntrSizeType
		def vCity = util.substring(current_Body.Event?.Location?.CityDetails?.City,1,30)?.trim()
		def vState = (util.substring(current_Body.Event?.Location?.CityDetails?.State,1,2))?.trim()?.toUpperCase()
		def vCSCountryCode = util.substring(current_Body.Event?.Location?.CSStandardCity?.CSCountryCode,1,2)
		//		def shipDir=getShipDir('TGF','Q2','O','ShipDirection',vCS1EventFirst5,'EventStatusCode',conn)
		def shipDir = util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.

		def vCS1EventCodeConversion = util.getConversion('TGF', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		//def vCarrCntrSizeTypeCodeConversion = util.getConversionWithScac('TGF', 'CT', 'O', 'ContainerType', vCarrCntrSizeType, 'OOLU', conn)
		Map vUNLocCode_map =ctUtil.getUNLocCodeFromLocationInfo(vCity, vState, vCSCountryCode, conn)
		Map vUNLocCode2_map =ctUtil.getUNLocCodeFromCityInfo(vCity, vCSCountryCode, conn)

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '-999'
			}
			'B4' {
				if(vCS1EventCodeConversion){
					'E157_03' vCS1EventCodeConversion
				}

				'E373_04' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)

				if(current_Body.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)?.trim()
				}

				if(current_Body.Container?.ContainerNumber){
					//					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, current_Body.Container?.ContainerNumber?.length()).trim()
					if (util.substring(current_Body.Container?.ContainerNumber, 5, 6)?.trim())
						'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, 6)?.trim()
				}

				if (!current_Body.Container?.ContainerStatus&& (vCS1Event=='CS210'||vCS1Event=='CS010')) {
					'E578_09' 'E'
				}else if (!current_Body.Container?.ContainerStatus&& (vCS1Event!='CS210' && vCS1Event!='CS010')) {
					'E578_09' 'L'
				}else if(current_Body.Container?.ContainerStatus=='Empty'){
					'E578_09' 'E'
				}else if(current_Body.Container?.ContainerStatus == 'Laden'){
					'E578_09' 'L'
				}

				if(current_Body.Container?.CarrCntrSizeType){
					'E24_10' util.substring(current_Body.Container?.CarrCntrSizeType, 1, 4)?.trim()
				}

				def eventUNLoc = current_Body?.Event?.Location?.LocationCode?.UNLocationCode
				//Get UNLoc if exists
				if(eventUNLoc && eventUNLoc.length()>0)
				{
					'E310_11' eventUNLoc
					'E309_12' 'UN'
				}
				else
				{
					if(vUNLocCode_map['UNLocCode'] || vUNLocCode_map['SchedKDCde']){
						if(vUNLocCode_map['UNLocCode']){
							'E310_11' vUNLocCode_map['UNLocCode']
						}else if(!vUNLocCode_map['UNLocCode'] && vUNLocCode_map['SchedKDCde']){
							'E310_11' vUNLocCode_map['SchedKDCde']
						}else if(vCity){
							'E310_11' vCity
						}else if(util.substring(current_Body.Event?.Location?.LocationName,1,30)){
							'E310_11' util.substring(current_Body.Event?.Location?.LocationName,1,30)?.trim()
						}
					}else if(vUNLocCode2_map['UNLocCode'] || vUNLocCode2_map['SchedKDCde']){
						if(vUNLocCode2_map['UNLocCode']){
							'E310_11' vUNLocCode2_map['UNLocCode']
						}else if(!vUNLocCode2_map['UNLocCode'] && vUNLocCode2_map['SchedKDCde']){
							'E310_11' vUNLocCode2_map['SchedKDCde']
						}else if(vCity){
							'E310_11' vCity
						}else if(util.substring(current_Body.Event?.Location?.LocationName,1,30)){
							'E310_11' util.substring(current_Body.Event?.Location?.LocationName,1,30)?.trim()
						}
					}else if(!vUNLocCode_map['UNLocCode'] && !vUNLocCode_map['SchedKDCde'] && !vUNLocCode2_map['UNLocCode'] && !vUNLocCode2_map['SchedKDCde']){
						if(vCity){
							'E310_11' vCity
						}else if(util.substring(current_Body.Event?.Location?.LocationName,1,30)){
							'E310_11' util.substring(current_Body.Event?.Location?.LocationName,1,30)?.trim()
						}
					}

					if(vUNLocCode_map['UNLocCode'] || vUNLocCode_map['SchedKDCde']){
						if(vUNLocCode_map['UNLocCode']){
							'E309_12' 'UN'
						}else if(vUNLocCode_map['SchedKDCde']=='K' || vUNLocCode_map['SchedKDCde']=='D'){
							'E309_12' vUNLocCode_map['SchedKDCde']
						}else if(vCity){
							'E309_12' 'CI'
						}else if(util.substring(current_Body.Event?.Location?.LocationName,1,35)){
							'E309_12' 'CI'
						}
					}else if(vUNLocCode2_map['UNLocCode'] || vUNLocCode2_map['SchedKDCde']){
						if(vUNLocCode2_map['UNLocCode']){
							'E309_12' 'UN'
						}else if(vUNLocCode_map['SchedKDCde']=='K' || vUNLocCode_map['SchedKDCde']!='D'){
							'E309_12' vUNLocCode2_map['SchedKDCde']
						}else if(vCity){
							'E309_12' 'CI'
						}else if(util.substring(current_Body.Event?.Location?.LocationName,1,35)){
							'E309_12' 'CI'
						}
					}else if(!vUNLocCode_map['UNLocCode'] && !vUNLocCode_map['SchedKDCde'] && !vUNLocCode2_map['UNLocCode'] && !vUNLocCode2_map['SchedKDCde']){
						if(vCity){
							'E309_12' 'CI'
						}else if(util.substring(current_Body.Event?.Location?.LocationName,1,35)){
							'E309_12' 'CI'
						}
					}
				}
				
				
				'E761_13'  util.substring(current_Body.Container?.ContainerCheckDigit, 1,2)?.trim()
			}

			//Loop N9
			// BookingNumber
			//			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each {CarrierBookingNumber, CarrierBookingNumberGroup ->
			current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.each {BN ->
				'N9' {
					'E128_01' 'BN'
					'E127_02' util.substring(BN?.CarrierBookingNumber?.trim(), 1, 30)?.trim()
				}
			}


			//BLNumber
			//			current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber?.trim()}.each{BLNumber, BLNumberGroup ->
			current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.each{BL ->
				'N9' {
					'E128_01' 'BM'
					'E127_02' util.substring(BL?.BLNumber?.trim(), 1, 30)?.trim()

				}
			}


			//ExtReferenceCode
			def extRefMap = ['FN':'FN', 'INV':'IN', 'PO':'PO', 'SC':'E8', 'SID':'SI', 'SO':'SO', 'SR':'SR']
			current_Body.ExternalReference?.findAll{StringUtil.isNotEmpty(it.CSReferenceType) }?.each { ExternalReference ->
				//				def ExtReferenceCode=getExtReferenceCode('TGF','INTCTXML','315','O','ExtReferenceCode','N9','01-128',ExternalReference?.get(0).CSReferenceType,conn)
				def ExtReferenceCode = extRefMap.get(ExternalReference?.CSReferenceType)
				if(StringUtil.isNotEmpty(ExtReferenceCode))
					'N9' {
						'E128_01' ExtReferenceCode
						'E127_02' util.substring(ExternalReference?.ReferenceNumber,1,30)
					}
			}

			// CarrierScacCode
			if(firstScac){
				'N9'{
					'E128_01' 'SCA'
					'E127_02' firstScac
				}
			}

			//Q2
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body.Route?.OceanLeg){
				firstOceanLeg = current_Body.Route?.OceanLeg[0]
				lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
			}

			//			List oceanLegList = current_Body?.Route?.OceanLeg?.findAll{StringUtil.isNotEmpty(it?.SVVD?.Loading?.Vessel) || StringUtil.isNotEmpty(it?.SVVD?.Loading?.Voyage + it?.SVVD?.Loading?.Direction) || StringUtil.isNotEmpty(it?.SVVD?.Loading?.VesselName)}
			//			List oceanLegList = current_Body?.Route?.OceanLeg?.findAll{StringUtil.isNotEmpty(it?.SVVD?.Loading?.Vessel) || StringUtil.isNotEmpty((it?.SVVD?.Loading?.Voyage?:"") + (it?.SVVD?.Loading?.Direction ?:"")) || StringUtil.isNotEmpty(it?.SVVD?.Loading?.VesselName)}
			List oceanLegList = []
			for (OceanLeg oceanLeg : current_Body?.Route?.OceanLeg) {
				if (StringUtil.isNotEmpty(oceanLeg?.SVVD?.Loading?.Vessel) || StringUtil.isNotEmpty((oceanLeg?.SVVD?.Loading?.Voyage ?: "") + (oceanLeg?.SVVD?.Loading?.Direction ?:"")) || StringUtil.isNotEmpty(oceanLeg?.SVVD?.Loading?.VesselName)) {
					oceanLegList.add(oceanLeg)
				}
			}
			OceanLeg first_OceanLeg = null
			OceanLeg last_OceanLeg = null
			if (oceanLegList.size() > 0) {
				first_OceanLeg = oceanLegList[0]
				last_OceanLeg = oceanLegList[-1]
			}



			def CSSTDQ2ExtCDE=null
			CSSTDQ2ExtCDE=ctUtil.getCodeConversionbyCovertType('CT','O','CSSTDQ2',vCS1Event,conn)
			if(CSSTDQ2ExtCDE){
				if (shipDir == 'I') {

					'Q2' {
						if (last_OceanLeg?.SVVD?.Discharge?.LloydsNumber) {
							'E597_01' util?.substring(last_OceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)?.trim()
						}
						if (last_OceanLeg?.SVVD?.Discharge?.RegistrationCountryCode) {
							'E26_02' util?.substring(last_OceanLeg?.SVVD?.Discharge?.RegistrationCountryCode, 1, 2)?.trim()
						}
						//						LocDT polDepartDtA = last_OceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
						//						LocDT polDepartDtE = last_OceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT

						if (last_OceanLeg?.SVVD?.Discharge?.Voyage || last_OceanLeg?.SVVD?.Discharge?.Direction) {
							'E55_09' ((last_OceanLeg?.SVVD?.Discharge?.Voyage ? last_OceanLeg?.SVVD?.Discharge?.Voyage:"") + (last_OceanLeg?.SVVD?.Discharge?.Direction ? last_OceanLeg?.SVVD?.Discharge?.Direction:""))
						}
						if(last_OceanLeg?.SVVD?.Discharge?.LloydsNumber)
						{
							'E897_12' 'L'
						}
						if (last_OceanLeg?.SVVD?.Discharge?.VesselName) {
							'E182_13' util.substring(last_OceanLeg?.SVVD?.Discharge?.VesselName?.trim(), 1, 28)?.trim()
						}
					}
				} else if (shipDir == 'O') {
					'Q2' {
						if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
							'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)?.trim()
						}
						if (firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode) {
							'E26_02' firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode
						}
						//						LocDT polDepartureDtA = last_OceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
						//						LocDT polDepartureDtE = last_OceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT


						if (firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction) {
							'E55_09' ((firstOceanLeg?.SVVD?.Loading?.Voyage ? firstOceanLeg?.SVVD?.Loading?.Voyage:"") + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction:""))
						}
						if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber)
						{
							'E897_12' 'L'
						}
						if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
							'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName?.trim(), 1, 28)?.trim()
						}
					}
				}
			}


			//Loop R4
			POR POR =  current_Body.Route?.POR
			FirstPOL firstPOL =  current_Body.Route?.FirstPOL
			LastPOD lastPOD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND
			//POR
			// when R401-115 and (R403-310 or R404-114) is not empty, output R4 R
			def R403 = null
			def R404 = null
			if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
				R403 = POR?.CityDetails?.LocationCode?.UNLocationCode?.trim()
			} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
				R403 = POR?.CityDetails?.LocationCode?.SchedKDCode?.trim()
			}
			if (POR?.CityDetails?.City) {
				R404 = util?.substring(POR?.CityDetails?.City, 1, 24)?.trim()
			}

			if(POR && (R403 || R404)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'R'
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							if (POR?.CityDetails?.LocationCode?.SchedKDType) {
								'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType?.trim()
							}
						}
						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode?.trim()
						} else if (POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.SchedKDCode?.trim()
						}
						if (POR?.CityDetails?.City) {
							'E114_04' util?.substring(POR?.CityDetails?.City, 1, 24)?.trim()
						}
						if (POR?.CSStandardCity?.CSCountryCode && POR?.CSStandardCity?.CSCountryCode?.length() <= 2){
							'E26_05' POR?.CSStandardCity?.CSCountryCode?.trim()
						}
						if (POR?.CSStandardCity?.CSStateCode) {
							'E156_08' util.substring(POR?.CSStandardCity?.CSStateCode?.trim(), 1, 2).trim()
						}
					}

					LocDT porDTM = null
					def isAct = false
					if (current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString()) {
						porDTM = current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT
						isAct = true
					} else if  (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT?.toString() && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C') {
						porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT
					} else if (current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT?.toString() && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M') {
						porDTM = current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT
					}
					if (porDTM?.toString()) {
						'DTM' {
							if (isAct) {
								'E374_01' '140'
							} else {
								'E374_01' '139'
							}
							'E373_02' util.convertDateTime(porDTM, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(porDTM, xmlDateTimeFormat, HHmm)

						}
					}

				}
			}

			//POL
			R403 = null
			R404 = null
			if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
				R403 =  firstPOL?.Port?.LocationCode?.UNLocationCode?.trim()
			} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
				R403 = firstPOL?.Port?.LocationCode?.SchedKDCode?.trim()
			}
			if (firstPOL?.Port?.PortName) {
				R404 = util.substring(firstPOL?.Port?.PortName, 1, 24)?.trim()
			} else if (firstPOL?.Port?.City) {
				R404 = util.substring(firstPOL?.Port?.City, 1, 24)?.trim()
			}
			if(firstPOL && (R403 || R404)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'L'
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							if (firstPOL?.Port?.LocationCode?.SchedKDType) {
								'E309_02' firstPOL?.Port?.LocationCode?.SchedKDType?.trim()
							}
						}
						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.UNLocationCode?.trim()
						} else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' firstPOL?.Port?.LocationCode?.SchedKDCode?.trim()
						}
						if (firstPOL?.Port?.PortName) {
							'E114_04'  util.substring(firstPOL?.Port?.PortName, 1, 24)?.trim()
						} else if (firstPOL?.Port?.City) {
							'E114_04'  util.substring(firstPOL?.Port?.City, 1, 24)?.trim()
						}

						if (firstPOL?.Port?.CSCountryCode && firstPOL?.Port?.CSCountryCode?.length() <= 2) {
							'E26_05' firstPOL?.Port?.CSCountryCode?.trim()
						}
						if (firstPOL?.CSStateCode) {
							'E156_08'  util.substring(firstPOL?.CSStateCode?.trim(), 1, 2).trim()
						}
					}

					LocDT locActDTM = null
					LocDT locEstDTM = null
					def isAct = false
					def isEst = false

					if (current_Body.Route?.OceanLeg ) {
						first_OceanLeg = current_Body.Route?.OceanLeg[0]
					}

					if(first_OceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT){
						locActDTM = first_OceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
						isAct = true
					}
					if(first_OceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT){
						locEstDTM = first_OceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
						isEst = true
					}


					if (isAct) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(locActDTM, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(locActDTM, xmlDateTimeFormat, HHmm)
						}
					}
					else if (isEst) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(locEstDTM, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(locEstDTM, xmlDateTimeFormat, HHmm)
						}
					}


				}
			}

			//POD
			R403 = null
			R404 = null
			if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
				R403 = lastPOD?.Port?.LocationCode?.UNLocationCode
			}else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
				R403 = lastPOD?.Port?.LocationCode?.SchedKDCode
			}
			if (lastPOD?.Port?.PortName) {
				R404 = util?.substring(lastPOD?.Port?.PortName, 1, 24)?.trim()
			}else if(!lastPOD?.Port?.PortName && lastPOD?.Port?.City){
				R404 = util?.substring(lastPOD?.Port?.City, 1, 24)?.trim()
			}
			if(lastPOD && (R403 || R404)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'D'
						if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
							if (lastPOD?.Port?.LocationCode?.SchedKDType) {
								'E309_02' lastPOD?.Port?.LocationCode?.SchedKDType
							}
						}
						if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' lastPOD?.Port?.LocationCode?.UNLocationCode
						}else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' lastPOD?.Port?.LocationCode?.SchedKDCode
						}
						if (lastPOD?.Port?.PortName) {
							'E114_04' util?.substring(lastPOD?.Port?.PortName, 1, 24)?.trim()
						}else if(!lastPOD?.Port?.PortName && lastPOD?.Port?.City){
							'E114_04' util?.substring(lastPOD?.Port?.City, 1, 24)?.trim()
						}
						if (lastPOD?.Port?.CSCountryCode && lastPOD?.Port?.CSCountryCode?.length() <= 2){
							'E26_05' lastPOD?.Port?.CSCountryCode
						}
						if (lastPOD?.CSStateCode) {
							'E156_08' util.substring(lastPOD?.CSStateCode?.trim(), 1, 2).trim()
						}
					}


					oceanLegList = current_Body?.Route?.OceanLeg?.findAll{! (StringUtil.isEmpty(it?.SVVD?.Loading?.Vessel) && StringUtil.isEmpty(it?.SVVD?.Loading?.Voyage) && StringUtil.isEmpty(it?.SVVD?.Loading?.VesselName)) }
					if (oceanLegList.size() > 0) {
						last_OceanLeg = oceanLegList[-1]
						if (last_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT) {
							'DTM' {
								'E374_01' '140'
								'E373_02' util.convertDateTime(last_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(last_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
							}
						} else if (last_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT) {
							'DTM' {
								'E374_01' '139'
								'E373_02' util.convertDateTime(last_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(last_OceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
							}
						}
					}



				}
			}

			//FND
			R403 = null
			R404 = null
			if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
				R403 = FND?.CityDetails?.LocationCode?.UNLocationCode
			}else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
				R403 = FND?.CityDetails?.LocationCode?.SchedKDCode
			}
			if(FND?.CityDetails?.City){
				R404 = util.substring(FND?.CityDetails?.City,1,24)?.trim()
			}
			if(FND && (R403 || R404)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
							if (FND?.CityDetails?.LocationCode?.SchedKDType) {
								'E309_02' FND?.CityDetails?.LocationCode?.SchedKDType
							}
						}
						if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' FND?.CityDetails?.LocationCode?.UNLocationCode
						}else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' FND?.CityDetails?.LocationCode?.SchedKDCode
						}
						if(FND?.CityDetails?.City){
							'E114_04' util.substring(FND?.CityDetails?.City,1,24)?.trim()
						}
						if (FND?.CSStandardCity?.CSCountryCode?.length() <= 2){
							if (StringUtil.isNotEmpty(FND?.CSStandardCity?.CSCountryCode)) {
								'E26_05' FND?.CSStandardCity?.CSCountryCode
							}
						}
						if (FND?.CSStandardCity?.CSStateCode) {
							'E156_08' util.substring(FND?.CSStandardCity?.CSStateCode?.trim(), 1, 2).trim()
						}
					}



					def isAct = false;
					def datetime;

					def eventDateTime = current_Body?.Event?.EventDT?.LocDT
					//					println 'eventDateTime  ' + eventDateTime
					//1
					def actCargoDeliveryDateTime = current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT
					//					println 'actCargoDeliveryDateTime  \t\t\t' + actCargoDeliveryDateTime
					//2
					def actArrivalAtFinalHubDateTime = null
					//In App 1 MapTransaction
					if (current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT) {
						actArrivalAtFinalHubDateTime = current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT
					} else if (current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT && current_Body.GeneralInformation?.SCAC != 'OOLU') {
						actArrivalAtFinalHubDateTime = current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					}
					//					println 'actArrivalAtFinalHubDateTime  \t\t\t' + actArrivalAtFinalHubDateTime?.toString() {};
					//4
					def estCargoDeliveryDateTime = current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT
					//					println 'estCargoDeliveryDateTime  \t\t\t'  + estCargoDeliveryDateTime
					//5
					//In App 1 MapTransaction
					def estArrivalAtFinalHubDateTime = null
					if (current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT) {
						estArrivalAtFinalHubDateTime = current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT
					} else if (current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT && current_Body.GeneralInformation?.SCAC != 'OOLU') {
						estArrivalAtFinalHubDateTime = current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
					}
					//					println 'estArrivalAtFinalHubDateTime  \t\t\t' + estArrivalAtFinalHubDateTime
					//8
					def estArrivalDate = null
					if (current_Body?.Route?.OceanLeg?.size() > 0)
						estArrivalDate = current_Body?.Route?.OceanLeg[-1]?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
					//					println 'estArrivalDate  \t\t\t' + estArrivalDate
					//9
					def actArrivalDate = null
					if (current_Body?.Route?.OceanLeg?.size() > 0)
						actArrivalDate = current_Body?.Route?.OceanLeg[-1]?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					//					println 'actArrivalDate  \t\t\t' + actArrivalDate


					if ( actCargoDeliveryDateTime && util.convertDateTime(eventDateTime, xmlDateTimeFormat, yyyyMMdd) <= util.convertDateTime(actCargoDeliveryDateTime, xmlDateTimeFormat, yyyyMMdd)) {
						isAct = true;
						datetime = actCargoDeliveryDateTime
					} else if (actArrivalAtFinalHubDateTime && util.convertDateTime(eventDateTime, xmlDateTimeFormat, yyyyMMdd) <= util.convertDateTime(actArrivalAtFinalHubDateTime, xmlDateTimeFormat, yyyyMMdd)) {
						isAct = true;
						datetime = actArrivalAtFinalHubDateTime
					} else if (estCargoDeliveryDateTime) {
						isAct = false;
						datetime = estCargoDeliveryDateTime
					} else if (estArrivalAtFinalHubDateTime) {
						isAct = false;
						datetime = estArrivalAtFinalHubDateTime
					} else if (estArrivalDate) {
						isAct = false;
						datetime = estArrivalDate
					} else if (actArrivalDate) {
						isAct = true;
						datetime = actArrivalDate
					} else {
						isAct = false
					}

					//					println 'datetime  \t\t\t' + datetime

					//Jackson fix, to avoid empty DTM and lead to light bc validation error
					def R4_M_DTM=util.convertDateTime(datetime, xmlDateTimeFormat, yyyyMMdd)
					if(R4_M_DTM && R4_M_DTM?.length()>0)
					{
						if (isAct) {
							'DTM' {
								'E374_01' '140'
								'E373_02' util.convertDateTime(datetime, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(datetime, xmlDateTimeFormat, HHmm)
							}
						} else {
							'DTM' {
								'E374_01' '139'
								'E373_02' util.convertDateTime(datetime, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(datetime, xmlDateTimeFormat, HHmm)
							}
						}
					}



				}
			}


			'SE' {
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
		def headerMsgDT = util.convertDateTime(ct.Header?.MsgDT?.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		//long ediIsaCtrlNumber = ctrlNos[0]
		//long ediGroupCtrlNum = ctrlNos[1]
		def txnErrorKeys = []

		//duplication -- CT special logic
		//generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)

		//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		// Modified by Louis , global variable : firstScac
		if (bodies.size() > 0 && bodies[0].GeneralInformation?.SCAC) {
			firstScac = bodies[0].GeneralInformation?.SCAC
		}

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('TGF', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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
		//generateEDITail(outXml, outputBodyCount)
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
//			result = writer?.toString()
			result = util.cleanXml(writer?.toString())
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()

		//		println result
		return result;
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		// error cases
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		ctUtil.EventIsNotSubscribedByParnter(eventCS2Cde, eventExtCde, false, '', errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
		ctUtil.missingContainerNumber(eventCS2Cde, current_Body?.Container, true, null, errorKeyList)
		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route?.Inbound_intermodal_indicator, false, null, errorKeyList)

		return errorKeyList;
	}

	public boolean pospValidation() {

	}


}






