package cs.b2b.mapping.scripts

import java.sql.Connection;
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils
import cs.b2b.core.mapping.bean.ct.Body;
import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.bean.ct.Seal;
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder;

class CUS_CS2CTXML_315_BNZLBPI {

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

	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('BNZLBPI', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
		def shipDir =  util.getEDICdeReffromIntCde('BNZLBPI', 'ShipDirection', 'O', 'INTCTXML', 'Q2', 'EventStatusCode', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
		
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {

				'E157_03' vCS1EventCodeConversion

				//convert 2011 0816 20 59 00 to 20110816
				'E373_04' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

				//convert 2011 0816 20 59 00 to 20 59
				'E161_05' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)

				def B406 = util.substring(current_Body.Event?.Location?.LocationCode?.UNLocationCode,1,5)
				if(StringUtil.isEmpty(B406)){
					B406 = util.substring(current_Body.Event?.Location?.LocationCode?.SchedKDCode,1,5)
				}
				if(StringUtil.isEmpty(B406)){
					B406 = '9999'
				}

				if(StringUtil.isNotEmpty(B406)){
					'E159_06' B406
				}

				if(StringUtil.isNotEmpty(util.substring(current_Body.Container?.ContainerNumber?.trim(), 1, 4))){
					'E206_07'  util.substring(current_Body.Container?.ContainerNumber?.trim(), 1, 4)
				}

				if(StringUtil.isNotEmpty(util.substring(current_Body.Container?.ContainerNumber?.trim(), 5, 6))){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber?.trim(), 5, 6)
				}
				
				def vEventCodeCSInternalValue = vCS1Event.trim()
				def vContainerStatus = null
				switch (current_Body.Container?.ContainerStatus?.trim()){
					case "Empty" :
						vContainerStatus = "E"
						break
					case "Laden" :
						vContainerStatus = "L"
						break
				}

				if(StringUtil.isEmpty(vContainerStatus) && (vEventCodeCSInternalValue.equals("CS210") ||
				vEventCodeCSInternalValue.equals("CS010"))){
					'E578_09' 'E'
				}else if(StringUtil.isEmpty(vContainerStatus) && (!vEventCodeCSInternalValue.equals("CS210") ||
				!vEventCodeCSInternalValue.equals("CS010"))){
					'E578_09' 'L'
				}else if(!StringUtil.isEmpty(vContainerStatus)){
					'E578_09' vContainerStatus
				}

				if(StringUtil.isNotEmpty(util.substring(current_Body.Container?.CarrCntrSizeType, 1, 4))){
					'E24_10' util.substring(current_Body.Container?.CarrCntrSizeType, 1, 4)
				}

				def vFirstPOLUnLocCode = util.substring(current_Body.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode?.trim(),1,5)
				def vFirstPOLScheduleKDCode = util.substring(current_Body.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode?.trim(),1,5)
				def vFirstPOLCityName = util.substring(current_Body.Route?.FirstPOL?.Port?.City?.trim(),1,60)
				def vLastPODUnLocCode = util.substring(current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode?.trim(),1,5)
				def vLastPODScheduleKDCode = util.substring(current_Body.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode?.trim(),1,5)
				def vLastPODCityName = util.substring(current_Body.Route?.LastPOD?.Port?.City?.trim(),1,60)
				def vFinalDestinationUnLocCode = util.substring(current_Body.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode?.trim(),1,5)
				def vFinalDestinationScheduleKDCode = util.substring(current_Body.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode?.trim(),1,5)
				def vFinalDestinationCity = util.substring(current_Body.Route?.FND?.CityDetails?.City?.trim(),1,60)
				def vEventLocationCodeUN = util.substring(current_Body.Event?.Location?.LocationCode?.UNLocationCode?.trim(),1,5)
				def vEventLocationCodeScheduleKD = util.substring(current_Body.Event?.Location?.LocationCode?.SchedKDCode?.trim(),1,5)
				def vEventLocationName = util.substring(current_Body?.Event?.Location?.LocationName?.trim(),1,35)
				def vEventCityName = util.substring(current_Body.Event?.Location?.CityDetails?.City?.trim(),1,60)
				if(vEventCodeCSInternalValue.equals("CS020") ||
				vEventCodeCSInternalValue.equals("CS070")){
					if(StringUtil.isNotEmpty(vFirstPOLUnLocCode)){
						'E310_11' vFirstPOLUnLocCode
					}else if(StringUtil.isNotEmpty(vFirstPOLScheduleKDCode)){
						'E310_11' vFirstPOLScheduleKDCode
					}else if(StringUtil.isNotEmpty(vFirstPOLCityName)){
						'E310_11' vFirstPOLCityName
					}
				}else if(vEventCodeCSInternalValue.equals("CS120")){
					if(StringUtil.isNotEmpty(vLastPODUnLocCode)){
						'E310_11' vLastPODUnLocCode
					}else if(StringUtil.isNotEmpty(vLastPODScheduleKDCode)){
						'E310_11' vLastPODScheduleKDCode
					}else if(StringUtil.isNotEmpty(vLastPODCityName)){
						'E310_11' vLastPODCityName
					}
				}else if(vEventCodeCSInternalValue.equals("CS180") ||
				vEventCodeCSInternalValue.equals("CS190")){
					if(StringUtil.isNotEmpty(vFinalDestinationUnLocCode)){
						'E310_11' vFinalDestinationUnLocCode
					}else if(StringUtil.isNotEmpty(vFinalDestinationScheduleKDCode)){
						'E310_11' vFinalDestinationScheduleKDCode
					}else if(StringUtil.isNotEmpty(vFinalDestinationCity)){
						'E310_11' vFinalDestinationCity
					}
				}else if(StringUtil.isNotEmpty(vEventLocationCodeUN)){
					'E310_11' vEventLocationCodeUN
				}else if(StringUtil.isNotEmpty(vEventLocationCodeScheduleKD)){
					'E310_11' vEventLocationCodeScheduleKD
				}else if(StringUtil.isNotEmpty(vEventLocationName)){
					'E310_11' vEventLocationName
				}else if(StringUtil.isNotEmpty(vEventCityName)){
					'E310_11' vEventCityName
				}

				def vOriginScheduleKDCodeType = util.substring(current_Body.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode,1,5)
				def vLastPODScheduleKDCodeType = util.substring(current_Body.Route?.LastPOD?.Port?.LocationCode?.SchedKDType.trim(),1,2)
				def vScheduleKDType = util.substring(current_Body.Event?.Location?.LocationCode?.SchedKDType?.trim(),1,1)
				if(vEventCodeCSInternalValue.equals("CS020") ||
				vEventCodeCSInternalValue.equals("CS070")){
					if(StringUtil.isNotEmpty(vFirstPOLUnLocCode)){
						'E309_12' 'UN'
					}else if(StringUtil.isNotEmpty(vFirstPOLScheduleKDCode)){
						'E309_12' vOriginScheduleKDCodeType
					}else if(StringUtil.isNotEmpty(vFirstPOLCityName)){
						'E309_12' 'CI'
					}
				}else if(vEventCodeCSInternalValue.equals("CS120")){
					if(StringUtil.isNotEmpty(vLastPODUnLocCode)){
						'E309_12' 'UN'
					}
					else if(StringUtil.isNotEmpty(vLastPODScheduleKDCode)){
						'E309_12' vLastPODScheduleKDCodeType
					}else if(StringUtil.isNotEmpty(vFinalDestinationCity)){
						'E309_12' 'CI'
					}
				}else if(StringUtil.isNotEmpty(vEventLocationCodeUN)){
					'E309_12' 'UN'
				}else if(StringUtil.isNotEmpty(vEventLocationCodeScheduleKD)){
					'E309_12' vScheduleKDType
				}else if(StringUtil.isNotEmpty(vEventLocationName)){
					'E309_12' 'CI'
				}else if(StringUtil.isNotEmpty(vEventCityName)){
					'E309_12' 'CI'
				}

				if(StringUtil.isNotEmpty(current_Body.Container.ContainerCheckDigit)){
					'E761_13' current_Body.Container?.ContainerCheckDigit
				}

			}

			//N9
			
			//BookingNumber
			current_Body.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.each {currentBKG->
				'N9' {
					'E128_01' 'BN'
					'E127_02' currentBKG.CarrierBookingNumber.trim()
				}
			}

			//BLNumber
			current_Body.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it.BLNumber)}.each {currentBLG->
				'N9' {
						'E128_01' 'BM'
						'E127_02' current_Body.GeneralInformation?.SCAC?.concat(currentBLG.BLNumber.trim())
				}
			}

			//ContainerNumber
			if(current_Body.Container){
				def vCNumberCheckDigit = util.substring(current_Body.Container?.ContainerNumber?.trim(),1,10)
						.concat(current_Body.Container?.ContainerCheckDigit?.trim())
				if(StringUtil.isNotEmpty(vCNumberCheckDigit)){
					'N9' {
						'E128_01' 'EQ'
						'E127_02' vCNumberCheckDigit
					}
				}
			}
			
			//SealNumber
			current_Body.Container?.Seal?.findAll{StringUtil.isNotEmpty(it.SealNumber) && it.SealNumber.length() >= 4}.groupBy{it.SealNumber?.trim()}.each { sealNumber, sealGroup ->
				'N9' {
					'E128_01' 'SN'
					'E127_02' util.substring((sealNumber),1,12)
				}
			}

			//CarrierSCAC
			if(StringUtil.isNotEmpty(current_Body.GeneralInformation?.SCAC)){
				'N9' {
					'E128_01' this.getEDICdeReffromIntCde('BNZLBPI','N9_01','N9','SCACQualifier','N9','O',conn)
					'E127_02' current_Body.GeneralInformation?.SCAC?.trim()
				}
			}

			//Q2
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body.Route?.OceanLeg){
				firstOceanLeg = current_Body.Route.OceanLeg[0]
				lastOceanLeg = current_Body.Route.OceanLeg[-1]
			}

			
			if(shipDir.equals('I')){
				'Q2' {
					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
						'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)
					}

					if (lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode) {
						'E26_02' lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode
					}

					'E80_06' null

					def vIBExternalVoyageNumber = null
					def vIBVoyageNumber = util.substring(lastOceanLeg?.SVVD?.Discharge?.Voyage?.trim().concat(lastOceanLeg?.SVVD?.Discharge?.Direction?.trim()),1,17)
					if(StringUtil.isNotEmpty(vIBExternalVoyageNumber)){
						'E55_09' vIBExternalVoyageNumber
					}else {
						'E55_09' vIBVoyageNumber
					}

					'E128_10' this.getEDICdeReffromIntCde('BNZLBPI','10-128','Q2_Inbound','SCA','Q2','O',conn)

					'E127_11' current_Body.GeneralInformation?.SCAC?.trim()

					'E897_12' this.getEDICdeReffromIntCde('BNZLBPI','12-897','Q2_Inbound','L','Q2','O',conn)

					def vIBVesselName = util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName?.trim(),1,30)
					if(StringUtil.isNotEmpty(vIBVesselName)){
						'E182_13' vIBVesselName
					}

				}
			}else if(shipDir.equals('O')){
				'Q2' {
					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
						'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)
					}

					if (firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode){
						'E26_02' util?.substring(firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode.trim(), 1, 60)
					}

					'E80_06' null

					def vOBExternalVoyageNumber = null
					def vOBVoyageNumber = util.substring(firstOceanLeg?.SVVD?.Loading?.Voyage?.trim().concat(firstOceanLeg?.SVVD?.Loading?.Direction?.trim()),1,17)
					if(StringUtil.isNotEmpty(vOBExternalVoyageNumber)){
						'E55_09' vOBExternalVoyageNumber
					}else {
						'E55_09' vOBVoyageNumber
					}

					'E128_10' this.getEDICdeReffromIntCde('BNZLBPI','10-128','Q2_Outbound','SCA','Q2','O',conn)

					'E127_11' current_Body.GeneralInformation?.SCAC?.trim()

					'E897_12' this.getEDICdeReffromIntCde('BNZLBPI','12-897','Q2_Outbound','L','Q2','O',conn)

					def vVesselName = firstOceanLeg?.SVVD?.Loading?.VesselName?.trim()
					if(vVesselName){
						'E182_13' vVesselName
					}

				}
			}

			//R4
			POR POR =  current_Body.Route?.POR
			FirstPOL POL =  current_Body.Route?.FirstPOL
			LastPOD POD =  current_Body.Route?.LastPOD
			FND FND =  current_Body.Route?.FND
			if(POD){
				'Loop_R4' {
					'R4' {

						'E115_01' this.getEDICdeReffromIntCde('BNZLBPI','01-115','R4','POD','LocationCode','O',conn)

						def vLastPODUnLocCode = util.substring(POD.Port?.LocationCode?.UNLocationCode?.trim(),1,5)
						def vLastPODScheduleKDCode = util.substring(POD.Port?.LocationCode?.SchedKDCode?.trim(),1,5)
						def vLastPODScheduleKDCodeType = util.substring(POD.Port?.LocationCode?.SchedKDType?.trim(),1,2)
						def vLastPODCityName = util.substring(POD.Port?.City?.trim(),1,60)
						if(StringUtil.isNotEmpty(vLastPODUnLocCode)){
							'E309_02' 'UN'
						}else if(StringUtil.isNotEmpty(vLastPODScheduleKDCode)){
							'E309_02' vLastPODScheduleKDCodeType
						}else if(StringUtil.isNotEmpty(vLastPODCityName)){
							'E309_02' 'CI'
						}

						if(StringUtil.isNotEmpty(vLastPODUnLocCode)){
							'E310_03' vLastPODUnLocCode
						}else if(StringUtil.isNotEmpty(vLastPODScheduleKDCode)){
							'E310_03' vLastPODScheduleKDCode
						}else if(StringUtil.isNotEmpty(vLastPODCityName)){
							'E310_03' vLastPODCityName
						}

						def vLastPODPortName = util.substring(POD.Port?.PortName?.trim(),1,35)
						if(StringUtil.isNotEmpty(vLastPODPortName)){
							'E114_04' vLastPODPortName
						}else {
							'E114_04' vLastPODCityName
						}

						def vLastPODCountryCode = util.substring(POD.Port?.CSCountryCode?.trim(),1,2)
						if(StringUtil.isNotEmpty(vLastPODCountryCode)){
							'E26_05' vLastPODCountryCode
						}

						def vLastPODStateProvinceCode = util.substring(POD.CSStateCode?.trim(),1,2)
						if(StringUtil.isNotEmpty(vLastPODStateProvinceCode)){
							'E156_08' vLastPODStateProvinceCode
						}

					}

					'DTM' {

						if(lastOceanLeg){
							def vDTMQualifier = null
							def vDTM = null
							def isAct = false
							def vActArrivalDate = lastOceanLeg?.POD?.ArrivalDT?.findAll{it.attr_Indicator.equals("A")}[0]?.LocDT
							def vEstArrivalDate = lastOceanLeg?.POD?.ArrivalDT?.findAll{it.attr_Indicator.equals("E")}[0]?.LocDT
							if(vActArrivalDate && StringUtil.isNotEmpty(vActArrivalDate.toString())){
								isAct = true
							}

							if(isAct){
								vDTM = util.convertDateTime(vActArrivalDate, xmlDateTimeFormat,'yyyyMMddHHmmss')
								vDTMQualifier = '140'
							}else {
								if(vEstArrivalDate && StringUtil.isNotEmpty(vEstArrivalDate.toString())){
									vDTM = util.convertDateTime(vEstArrivalDate, xmlDateTimeFormat,'yyyyMMddHHmmss')
								}else {
									vDTM = '00000000000000'
								}
								vDTMQualifier = '139'
							}
							
							'E374_01' vDTMQualifier
							
							'E373_02' util.substring(vDTM,1,8)
							
							'E337_03' util.substring(vDTM,9,4)
							
							'E623_04' this.getEDICdeReffromIntCde('BNZLBPI','04-623','R4_DTM','TimeCode','DTMCode','O',conn)
						}

					}
				}
			}
			if(POL){
				'Loop_R4' {
					
					'R4' {
						
						'E115_01' this.getEDICdeReffromIntCde('BNZLBPI','01-115','R4','POL','LocationCode','O',conn)
						
						def vFirstPOLUnLocCode = util.substring(POL.Port?.LocationCode?.UNLocationCode?.trim(),1,5)
						def vFirstPOLScheduleKDCode = util.substring(POL.Port?.LocationCode?.SchedKDCode?.trim(),1,5)
						def vFirstPOLScheduleKDCodeType = util.substring(POL.Port?.LocationCode?.SchedKDType?.trim(),1,2)
						def vFirstPOLCityName = util.substring(POL.Port?.City?.trim(),1,60)
						if(StringUtil.isNotEmpty(vFirstPOLUnLocCode)){
							'E309_02' 'UN'
						}else if(StringUtil.isNotEmpty(vFirstPOLScheduleKDCode)){
							'E309_02' vFirstPOLScheduleKDCodeType
						}else if(StringUtil.isNotEmpty(vFirstPOLCityName)){
							'E309_02' 'CI'
						}
						
						if(StringUtil.isNotEmpty(vFirstPOLUnLocCode)){
							'E310_03' vFirstPOLUnLocCode
						}else if (StringUtil.isNotEmpty(vFirstPOLScheduleKDCode)){
							'310_03' vFirstPOLScheduleKDCode
						}else if (StringUtil.isNotEmpty(vFirstPOLCityName)){
							'310_03' vFirstPOLCityName
						}
						
						def vFirstPOLPortName = util.substring(POL.Port?.PortName?.trim(),1,35)
						if(StringUtil.isNotEmpty(vFirstPOLPortName)){
							'E114_04' vFirstPOLPortName
						}else {
							'E114_04' vFirstPOLCityName
						}
						
						def vFirstPOLCountryCode = util.substring(POL.Port?.CSCountryCode?.trim(),1,2)
						if(StringUtil.isNotEmpty(vFirstPOLCountryCode)){
							'E26_05' vFirstPOLCountryCode
						}
						
						def vFirstPOLStateProvinceCode = util.substring(POL.CSStateCode?.trim(),1,2)
						if(StringUtil.isNotEmpty(vFirstPOLStateProvinceCode)){
							'E156_08' vFirstPOLStateProvinceCode
						}
						
					}
					
					'DTM' {
						
						if(firstOceanLeg){
							def vDTMQualifier = null
							def vDTM = null
							def vActDepartureDate = firstOceanLeg?.POL?.DepartureDT?.findAll{it.attr_Indicator.equals("A")}[0]?.LocDT
							if(vActDepartureDate && StringUtil.isNotEmpty(vActDepartureDate.toString())){
								vActDepartureDate = util.convertDateTime(vActDepartureDate, xmlDateTimeFormat,'yyyyMMddHHmmss')
							}
							def vEstDepartureDate = firstOceanLeg?.POL?.DepartureDT?.findAll{it.attr_Indicator.equals("E")}[0]?.LocDT
							if(vEstDepartureDate && StringUtil.isNotEmpty(vEstDepartureDate.toString())){
								vEstDepartureDate = util.convertDateTime(vEstDepartureDate, xmlDateTimeFormat,'yyyyMMddHHmmss')
							}
							def vEventDateTime = current_Body.Event?.EventDT?.LocDT
							if(vEventDateTime && StringUtil.isNotEmpty(vEventDateTime.toString())){
								vEventDateTime = util.convertDateTime(vEventDateTime, xmlDateTimeFormat,'yyyyMMddHHmmss')
							}
							
							if(vActDepartureDate && StringUtil.isNotEmpty(vActDepartureDate.toString()) && vActDepartureDate as long != 0
								&&  (vActDepartureDate as long) <= (vEventDateTime as long)){
								vDTMQualifier = '140'
								vDTM = vActDepartureDate
							}else if(vActDepartureDate && StringUtil.isNotEmpty(vActDepartureDate.toString()) && vActDepartureDate as long != 0
								&&  (vActDepartureDate as long) > (vEventDateTime as long)){
								vDTMQualifier = '139'
								vDTM = vActDepartureDate
							}else if(vEstDepartureDate && StringUtil.isNotEmpty(vEstDepartureDate.toString()) && vEstDepartureDate as long != 0){
								vDTMQualifier = '139'
								vDTM = vEstDepartureDate
							}
							
							'E374_01' vDTMQualifier
							
							'E373_02' util.substring(vDTM,1,8)
							
							'E337_03' util.substring(vDTM,9,4)
							
							'E623_04' this.getEDICdeReffromIntCde('BNZLBPI','04-623','R4_DTM','TimeCode','DTMCode','O',conn)
							
						}
					}
				}
			}
			
			if(FND){
				'Loop_R4' {
					
					'R4' {
						
						'E115_01' this.getEDICdeReffromIntCde('BNZLBPI','01-115','R4','FND','LocationCode','O',conn)
						
						def vFinalDestinationUnLocCode = util.substring(FND.CityDetails?.LocationCode?.UNLocationCode?.trim(),1,5)
						def vFinalDestinationScheduleKDCode = util.substring(FND.CityDetails?.LocationCode?.SchedKDCode?.trim(),1,5)
						def vFinalDestinationScheduleKDCodeType = util.substring(FND.CityDetails?.LocationCode?.SchedKDCode?.trim(),1,2)
						def vFinalDestinationCity = util.substring(FND.CityDetails?.City?.trim(),1,60)
						if(StringUtil.isNotEmpty(vFinalDestinationUnLocCode)){
							'E309_02' 'UN'
						}else if(StringUtil.isNotEmpty(vFinalDestinationScheduleKDCode)){
							'E309_02' vFinalDestinationScheduleKDCodeType
						}else if(StringUtil.isNotEmpty(vFinalDestinationCity)){
							'E309_02' 'CI'
						}
						
						if(StringUtil.isNotEmpty(vFinalDestinationUnLocCode)){
							'E310_03' vFinalDestinationUnLocCode
						}else if(StringUtil.isNotEmpty(vFinalDestinationScheduleKDCode)){
							'E310_03' vFinalDestinationScheduleKDCode
						}else if(StringUtil.isNotEmpty(vFinalDestinationCity)){
							'E310_03' vFinalDestinationCity
						}
						
						if(StringUtil.isNotEmpty(vFinalDestinationCity)){
							'E114_04' util.substring(vFinalDestinationCity,1,24)
						}
						
						//26_05
						def vFinalDestinationCountryCode = util.substring(FND.CSStandardCity?.CSCountryCode?.trim(),1,2)
						if(StringUtil.isNotEmpty(vFinalDestinationCountryCode)){
							'E26_05' vFinalDestinationCountryCode
						}
						
						def vFinalDestinationProvinceStateCode = util.substring(FND.CSStandardCity?.CSStateCode?.trim(),1,2)
						if(StringUtil.isNotEmpty(vFinalDestinationProvinceStateCode)){
							'E156_08' vFinalDestinationProvinceStateCode
						}
					}
					
					'DTM' {
						
						if(lastOceanLeg){
							def vDTMQualifier = null
							def vDTM = null
							def vActCargoDeliveryDateTime = current_Body.Route?.CargoDeliveryDT?.findAll{it.attr_Indicator.equals("A")}[0]?.LocDT
							if(vActCargoDeliveryDateTime && StringUtil.isNotEmpty(vActCargoDeliveryDateTime.toString())){
								vActCargoDeliveryDateTime = util.convertDateTime(vActCargoDeliveryDateTime, xmlDateTimeFormat,'yyyyMMddHHmmss')
							}
							def vEstCargoDeliveryDateTime = current_Body.Route?.CargoDeliveryDT?.findAll{it.attr_Indicator.equals("E")}[0]?.LocDT
							if(vEstCargoDeliveryDateTime && StringUtil.isNotEmpty(vEstCargoDeliveryDateTime.toString())){
								vEstCargoDeliveryDateTime = util.convertDateTime(vEstCargoDeliveryDateTime, xmlDateTimeFormat,'yyyyMMddHHmmss')
							}
							def vActArrivalAtFinalHubDateTime = current_Body.Route?.ArrivalAtFinalHub?.findAll{it.attr_Indicator.equals("A")}[0]?.LocDT
							if(vActArrivalAtFinalHubDateTime && StringUtil.isNotEmpty(vActArrivalAtFinalHubDateTime.toString())){
								vActArrivalAtFinalHubDateTime = util.convertDateTime(vActArrivalAtFinalHubDateTime, xmlDateTimeFormat,'yyyyMMddHHmmss')
							}else{
								vActArrivalAtFinalHubDateTime = FND.ArrivalDT?.findAll{it.attr_Indicator.equals("A")}[0]?.LocDT
								if(vActArrivalAtFinalHubDateTime && StringUtil.isNotEmpty(vActArrivalAtFinalHubDateTime.toString())){
									vActArrivalAtFinalHubDateTime = util.convertDateTime(vActArrivalAtFinalHubDateTime, xmlDateTimeFormat,'yyyyMMddHHmmss')
								}
							}
							def vEstArrivalAtFinalHubDateTime = current_Body.Route?.ArrivalAtFinalHub?.findAll{it.attr_Indicator.equals("E")}[0]?.LocDT
							if(vEstArrivalAtFinalHubDateTime && StringUtil.isNotEmpty(vEstArrivalAtFinalHubDateTime.toString())){
								vEstArrivalAtFinalHubDateTime = util.convertDateTime(vEstArrivalAtFinalHubDateTime, xmlDateTimeFormat,'yyyyMMddHHmmss')
							}else{
								vEstArrivalAtFinalHubDateTime = FND.ArrivalDT?.findAll{it.attr_Indicator.equals("E")}[0]?.LocDT
								if(vEstArrivalAtFinalHubDateTime && StringUtil.isNotEmpty(vEstArrivalAtFinalHubDateTime.toString())){
									vEstArrivalAtFinalHubDateTime = util.convertDateTime(vEstArrivalAtFinalHubDateTime, xmlDateTimeFormat,'yyyyMMddHHmmss')
								}
							}
							def vActArrivalDate = lastOceanLeg.POD?.ArrivalDT?.findAll{it.attr_Indicator.equals("A")}[0]?.LocDT
							if(vActArrivalDate && StringUtil.isNotEmpty(vActArrivalDate.toString())){
								vActArrivalDate = util.convertDateTime(vActArrivalDate, xmlDateTimeFormat,'yyyyMMddHHmmss')
							}else {
								vActArrivalDate = '00000000000000'
							}
							def vEstArrivalDate = lastOceanLeg.POD?.ArrivalDT?.findAll{it.attr_Indicator.equals("E")}[0]?.LocDT
							if(vEstArrivalDate && StringUtil.isNotEmpty(vEstArrivalDate.toString())){
								vEstArrivalDate = util.convertDateTime(vEstArrivalDate, xmlDateTimeFormat,'yyyyMMddHHmmss')
							}else {
								vEstArrivalDate = '00000000000000'
							}
							
							if(vActCargoDeliveryDateTime && StringUtil.isNotEmpty(vActCargoDeliveryDateTime)
								&& (vActCargoDeliveryDateTime as long) != 0 ){
								vDTMQualifier = '140'
								vDTM = vActCargoDeliveryDateTime
							}else if(vEstCargoDeliveryDateTime && StringUtil.isNotEmpty(vEstCargoDeliveryDateTime)
								&& (vEstCargoDeliveryDateTime as long) != 0 ){
								vDTMQualifier = '139'
								vDTM = vEstCargoDeliveryDateTime
							}else if(vActArrivalAtFinalHubDateTime && StringUtil.isNotEmpty(vActArrivalAtFinalHubDateTime)
								&& (vActArrivalAtFinalHubDateTime as long) != 0 ){
								vDTMQualifier = '140'
								vDTM = vActArrivalAtFinalHubDateTime
							}else if(vEstArrivalAtFinalHubDateTime && StringUtil.isNotEmpty(vEstArrivalAtFinalHubDateTime)
								&& (vEstArrivalAtFinalHubDateTime as long) != 0 ){
								vDTMQualifier = '139'
								vDTM = vEstArrivalAtFinalHubDateTime
							}else if(vActArrivalDate && StringUtil.isNotEmpty(vActArrivalDate)
								&& (vActArrivalDate as long) != 0 ){
								vDTMQualifier = '140'
								vDTM = vActArrivalDate
							}else if(vEstArrivalDate && StringUtil.isNotEmpty(vEstArrivalDate)
								&& (vEstArrivalDate as long) != 0 ){
								vDTMQualifier = '139'
								vDTM = vEstArrivalDate
							}
							
							'E374_01' vDTMQualifier
							
							'E373_02' util.substring(vDTM,1,8)
							
							'E337_03' util.substring(vDTM,9,4)
							
							'E623_04' this.getEDICdeReffromIntCde('BNZLBPI','04-623','R4_DTM','TimeCode','DTMCode','O',conn)
							
						}
					}
				}
			}

			'SE' {
				//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
				'E96_01' ' '
				'E329_02' '    '
			}
		}

	}
	
	private void generateEDIHeader(MarkupBuilder outXml, def appSessionId, ContainerMovement ContainerMovement, def currentSystemDt) {
		//		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID.text());
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID);
		//		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC?.text());
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0].GeneralInformation.SCAC);

		outXml.'ISA' {
			'I01_01' '00'
			'I02_02' '          '
			'I03_03' '00'
			'I04_04' '          '
			'I05_05' 'ZZ'
			'I06_06' 'CARGOSMART     '
			'I05_07' 'ZZ'
			'I07_08' 'CARSONDSG      '
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
			'E124_03' 'CARSONDSG'
			'E373_04' currentSystemDt.format(yyyyMMdd)
			'E337_05' currentSystemDt.format(HHmm)
			'E28_06' '1'
			'E455_07' 'X'
			'E480_08' '004010'
		}

	}
	
	
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
		
//		generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)

		//DuplicateTransactionType4
		Map<String,String> duplicatedPairs = this.getDuplicatedPairs('BNZLBPI', 'Duplicate',conn)
		Map<String,String> duplicatedPairs2 = this.getDuplicatedPairs('BNZLBPI', 'Duplicate2',conn)
		List<Body> bodies = this.CTEventDuplication(ct.Body, duplicatedPairs,duplicatedPairs2)
		
		//duplication -- CT special logic
//		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('BNZLBPI', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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

//		generateEDITail(outXml, outputBodyCount)
		
		//End root node
		outXml.nodeCompleted(null,T315)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		//		println bizKeyWriter.toString();
		//		println csuploadWriter.toString();

		//promote csupload and bizkey to session
		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);

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
		
		//B403
		if(StringUtil.isEmpty(eventExtCde)||eventExtCde.equals('XX')){
			this.eventNotSubscribed(eventCS2Cde, eventExtCde, false, null, errorKeyList)
			this.missingStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
			this.missingEventLocationCode(eventCS2Cde, eventExtCde, current_Body.Event?.Location, true, null, errorKeyList)
		}
		
		//B404
		this.missingStatusEventDate(eventCS2Cde, eventExtCde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
		
		//N9
		this.missingBookingNumber(eventCS2Cde, eventExtCde, current_Body.BookingGeneralInfo, true, null, errorKeyList)
		this.missingEquipmentNumber(eventCS2Cde, eventExtCde, current_Body.Container, true, null, errorKeyList)
		
		//CheckInput
		this.missingStatusCode2(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		this.missingStatusEventDateAndTime(eventCS2Cde, eventExtCde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
		this.missingEventLocation(eventCS2Cde, eventExtCde, current_Body.Event?.Location, true, null, errorKeyList)
		this.eventNotForBUNZLBPI(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		
		return errorKeyList;
	}

	public boolean pospValidation() {

	}


	private String getEDICdeReffromIntCde(String tp_id, String seg_num, String seg_id, String int_cde, String convert_type_id, String dir_id, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "SELECT ext_cde FROM b2b_edi_cde_ref WHERE tp_id = ? AND seg_num = ? AND seg_id = ? AND int_cde = ? AND convert_type_id = ? AND dir_id = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, tp_id);
			pre.setString(2, seg_num);
			pre.setString(3, seg_id);
			pre.setString(4, int_cde);
			pre.setString(5, convert_type_id);
			pre.setString(6, dir_id);
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
	
	/**
	 * For Duplicate
	 * @param tp_id
	 * @param convert_type_id
	 * @param conn
	 * @return
	 */
	private Map<String,String> getDuplicatedPairs(String tp_id, String convert_type_id, Connection conn){
		Map<String,String> result = [:]
		if (conn == null)
			return result;

		String key = null;
		String value = null;
		PreparedStatement pre = null;
		ResultSet resultSet = null;
		String sql = "SELECT INT_CDE, EXT_CDE FROM b2b_edi_cde_ref WHERE TP_ID = ? AND SRC_FMT_ID = 'INTCTXML' AND TRG_FMT_ID = '315' AND DIR_ID = 'O' AND CONVERT_TYPE_ID = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(ctUtil.getDBRowLimit());
			pre.setQueryTimeout(ctUtil.getDBTimeOutInSeconds());
			pre.setString(1, tp_id);
			pre.setString(2, convert_type_id);
			resultSet = pre.executeQuery();

			while(resultSet.next()) {
				key = resultSet.getString(1);
				value = resultSet.getString(2);
				result.put(key,value)
			}
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (pre != null)
				pre.close();
		}
		return result;
	}
	
	private List<cs.b2b.core.mapping.bean.ct.Body> CTEventDuplication(List<cs.b2b.core.mapping.bean.ct.Body> Body, Map<String,String> duplicatedPairs, Map<String,String> duplicatedPairs2) {

		List<cs.b2b.core.mapping.bean.ct.Body> bodies = [];

		Body.eachWithIndex { current_Body, current_BodyIndex ->
			bodies.add(current_Body)

			if (duplicatedPairs.get(current_Body.Event.CS1Event)) {
				//deep clone
				cs.b2b.core.mapping.bean.ct.Body duplBody = (cs.b2b.core.mapping.bean.ct.Body)SerializationUtils.clone(current_Body)
				duplBody.Event.CS1Event = duplicatedPairs.get(current_Body.Event.CS1Event)
				bodies.add(duplBody)
			}
			if (duplicatedPairs2.get(current_Body.Event.CS1Event)) {
				//deep clone
				cs.b2b.core.mapping.bean.ct.Body duplBody = (cs.b2b.core.mapping.bean.ct.Body)SerializationUtils.clone(current_Body)
				duplBody.Event.CS1Event = duplicatedPairs2.get(current_Body.Event.CS1Event)
				bodies.add(duplBody)
			}
		}

		return bodies;
	}
	
	/**
	 * For CSUpload
	 * @param eventCS2Cde
	 * @param eventExtCde
	 * @param isError
	 * @param errorMsg
	 * @param errorKeyList
	 */
	void eventNotSubscribed(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isNotEmpty(eventCS2Cde)&&eventExtCde.equals('XX')) {
			errorKey = [TYPE: isError? ctUtil.OBSOLETE : ctUtil.COMPLETE, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg: eventCS2Cde + ' - Event is not subscribed by Parnter ! ']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingStatusCode(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(eventCS2Cde)) {
			errorKey = [TYPE: isError? ctUtil.ERROR_SUPPORT : ctUtil.ERROR_COMPLETE, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE: errorMsg != null? eventExtCde + errorMsg: eventExtCde + '  - Missing Status Code']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingEventLocationCode(String eventCS2Cde, String eventExtCde, cs.b2b.core.mapping.bean.ct.Location location, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(location?.LocationName)
			&&StringUtil.isEmpty(location?.CityDetails?.City)
			&&StringUtil.isEmpty(location?.LocationCode?.UNLocationCode)) {
			errorKey = [TYPE: isError? ctUtil.ERROR_COMPLETE : ctUtil.ERROR_SUPPORT, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE: errorMsg != null? eventExtCde + errorMsg: eventExtCde + ' - Missing Event Location Code ']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingStatusEventDate(String eventCS2Cde, String eventExtCde, cs.b2b.core.mapping.bean.LocDT locDT, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(locDT?.toString())) {
			errorKey = [TYPE: isError? ctUtil.ERROR_SUPPORT : ctUtil.ERROR_COMPLETE, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE: errorMsg != null? eventExtCde + errorMsg : eventExtCde + ' - Missing Status Event Date']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingBookingNumber(String eventCS2Cde, String eventExtCde, List<cs.b2b.core.mapping.bean.ct.BookingGeneralInfo> bookingGeneralInfo, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(bookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.size() == 0){
			errorKey = [TYPE: isError? ctUtil.ERROR_COMPLETE : ctUtil.ERROR_SUPPORT, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE:errorMsg != null? eventExtCde + errorMsg : eventExtCde + ' - Missing Booking Number']
			errorKeyList.add(errorKey);
		}
	}
	
	void missingEquipmentNumber(String eventCS2Cde, String eventExtCde, cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(container?.ContainerNumber)&&StringUtil.isEmpty(container?.ContainerCheckDigit)){
			errorKey = [TYPE: isError? ctUtil.ERROR_COMPLETE : ctUtil.ERROR_SUPPORT, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE:errorMsg != null? eventExtCde + errorMsg : eventExtCde + ' - Missing Equipment Number']
			errorKeyList.add(errorKey);
		}
	}
	
	void missingStatusCode2(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(eventExtCde)
			&&!eventCS2Cde.equals('CS140')
			&&!eventCS2Cde.equals('CS220')) {
			errorKey = [TYPE: isError? ctUtil.ERROR_SUPPORT : ctUtil.ERROR_COMPLETE, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg: eventCS2Cde + ' - Missing Status Code']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingStatusEventDateAndTime(String eventCS2Cde, String eventExtCde, cs.b2b.core.mapping.bean.LocDT LocDT, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LocDT?.toString())
			||(util.convertDateTime(LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss') as long) == 0) {
			errorKey = [TYPE: isError? ctUtil.ERROR_COMPLETE : ctUtil.ERROR_SUPPORT, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE: errorMsg != null? eventExtCde + errorMsg: eventExtCde + ' - Missing Status Event Date and Time']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingEventLocation(String eventCS2Cde, String eventExtCde, cs.b2b.core.mapping.bean.ct.Location location, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(location?.LocationName)
			&&StringUtil.isEmpty(location?.CityDetails?.City)
			&&StringUtil.isEmpty(location?.LocationCode?.UNLocationCode)
			&&StringUtil.isEmpty(location?.LocationCode?.SchedKDCode)) {
			errorKey = [TYPE: isError? ctUtil.ERROR_COMPLETE : ctUtil.ERROR_SUPPORT, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE: errorMsg != null? eventExtCde + errorMsg: eventExtCde + ' - Missing Event Location']
			errorKeyList.add(errorKey)
		}
	}
	
	void eventNotForBUNZLBPI(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(eventCS2Cde.equals('CS140')
			||eventCS2Cde.equals('CS220')) {
			errorKey = [TYPE: isError? ctUtil.ERROR_COMPLETE : ctUtil.ERROR_SUPPORT, IS_ERROR: isError? ctUtil.YES : ctUtil.NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg: eventCS2Cde + ' - Event not for BUNZLBPI']
			errorKeyList.add(errorKey)
		}
	}

}
