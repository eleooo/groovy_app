package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bc.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.text.DecimalFormat

/**
 * @author RENGA
 * @pattern Class3 initialization
 * @Workplan http://i2isd/sites/csisa/Lists/Workplan/DispForm.aspx?ID=29188
 */
public class CUS_CS2BCXML_301_LOWES {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil()
	cs.b2b.core.mapping.util.MappingUtil_BC_O_Common bcUtil = new cs.b2b.core.mapping.util.MappingUtil_BC_O_Common(util)

	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	Connection conn = null

	java.util.Date currentDate = new java.util.Date()
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = 'HHmm'
	
	def currentSystemDt = null
	DecimalFormat decimalFormatter = new DecimalFormat("#.####")
	DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")

	Map bookingStatusMap = ['Confirmed':'A','Cancelled':'D','No Show':'D','Declined':'R','Rejected':'R']
	Map<String, String> DCLocationCodeMap = ['USMVX':'955','USSVH':'960','USMVP':'961','USVLD':'962','USNWB':'964','USCYS':'965','USPER':'966','USFDY':'990','USVNY':'992','USSIM':'1419','USGYS':'1420','USPFE':'1421','USLON':'1436','USLGB':'1438','USLAX':'1438','USRFD':'1440','USPTO':'1449','CAMIL':'1469']
	Map<String,String> referenceTypeMap = ['SC':'E8']
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '301'
				'E329_02' '-999'
			}
			'B1' {
				'E140_01' current_Body.GeneralInformation?.SCACCode
				'E145_02' util.substring(current_Body?.ExternalReference?.find{it.CSReferenceType == 'SR'}?.ReferenceNumber?.trim(), 1, 30)
				'E373_03' util.convertXmlDateTime(current_Body?.GeneralInformation?.BookingStatusDT?.LocDT, yyyyMMdd)
				'E558_04' bookingStatusMap.get(current_Body?.GeneralInformation?.BookingStatus)
			}
			'Y3' {
				if (current_Body.GeneralInformation?.SCACCode && current_Body?.GeneralInformation?.CarrierBookingNumber) {
					'E13_01' util.substring(current_Body.GeneralInformation.CarrierBookingNumber,1,17)
				}
				'E140_02' current_Body.GeneralInformation?.SCACCode
				if (current_Body.Route?.OceanLeg?.size() > 1 && current_Body.Route?.OceanLeg[0]?.DepartureDT[0]?.LocDT) {
					//Map only when multiple-OceanLeg – that means Y307 is enough for single OceanLeg
					'E373_03' util.convertXmlDateTime(current_Body.Route?.OceanLeg[0]?.DepartureDT[0]?.LocDT, yyyyMMdd)
				}
				if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT) {
					'E373_04'  util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT.LocDT, yyyyMMdd)
				}else if (current_Body.Route?.OceanLeg?.size() > 0 && current_Body.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == "E"}?.LocDT) {
					'E373_04'  util.convertXmlDateTime(current_Body.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == "E"}?.LocDT, yyyyMMdd)
				}
				if (current_Body.Route?.OceanLeg?.size() > 0 && current_Body.Route?.OceanLeg[-1]?.DepartureDT[0]?.LocDT) {
					'E373_07'  util.convertXmlDateTime(current_Body.Route?.OceanLeg[-1]?.DepartureDT[0]?.LocDT, yyyyMMdd)
				}
				Map IOMAP = ['C':'D','M':'P']
				def ob = IOMAP.get(current_Body.Route?.Haulage?.OutBound)
				def ib = IOMAP.get(current_Body.Route?.Haulage?.InBound)
				if (ob && ib) {
					'E375_10' ob + ib
				}
			}

			//Y4 Loop
			//get looping of empty container
			List<EmptyPickup> emptyCntrList = current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup?.findAll{it.ISOSizeType && it.ISOSizeType?.trim() != ''}
			if (emptyCntrList==null || emptyCntrList.isEmpty()) {
				//if empty, then build emptyContainer from container loop
				def groupCntr = current_Body.ContainerGroup?.Container?.groupBy(){it.CarrCntrSizeType}
				groupCntr.each() { current_LoopCntr ->
					EmptyPickup ep = new EmptyPickup()
					ep.ISOSizeType = current_LoopCntr.key
					if (current_LoopCntr.key) {
						ep.NumberOfContainers = current_LoopCntr.value.size()
					} else {
						ep.NumberOfContainers = 0
					}
					ep.Facility = new Facility()
					ep.Facility.FacilityCode = 'dummy'
					ep.Facility.FacilityName = 'dummy'
					emptyCntrList.add(ep)
				}
			}

			emptyCntrList.eachWithIndex { current_EmptyPickup, idx ->
				//only the 10 loops
				if (idx > 9) {
					return
				}
				'Loop_Y4' {
					'Y4' {
						'E13_01' util.substring(current_Body.GeneralInformation?.CarrierBookingNumber,1,17)
						'E373_03' util.convertXmlDateTime(current_EmptyPickup.MvmtDT?.LocDT, yyyyMMdd)
						'E95_05' current_EmptyPickup.NumberOfContainers

						def defConvType = ''
						if (current_EmptyPickup.ISOSizeType) {
							defConvType = util.getConversionByTpIdMsgTypeDirFmtScac(TP_ID, MSG_TYPE_ID, DIR_ID, "X.12", "ContainerType", current_Body.GeneralInformation?.SCACCode, current_EmptyPickup.ISOSizeType, conn)
							if (! defConvType) {
								defConvType = current_EmptyPickup.ISOSizeType
							}
						}
						'E24_06' defConvType

						if (current_Body.ContainerGroup?.Container?.size()>0) {
							def defOb = current_Body.ContainerGroup.Container[0].TrafficMode?.OutBound
							def defIb = current_Body.ContainerGroup.Container[0].TrafficMode?.InBound
							def def56 = ''
							if(defOb == 'FCL' && defIb == 'FCL') {
								def56 = '03'
							} else if(defOb == 'FCL' && defIb == 'LCL') {
								def56 = '04'
							} else if(defOb == 'LCL' && defIb == 'FCL') {
								def56 = '05'
							}  else if(defOb == 'LCL' && defIb == 'LCL') {
								def56 = '02'
							}
							'E56_10' def56
						}
					}
				}
			}

			if(current_Body?.GeneralInformation?.CarrierBookingNumber) {
				'N9' {
					'E128_01' 'WY'
					'E127_02' current_Body.GeneralInformation.CarrierBookingNumber
				}
			}

			current_Body?.CarrierRate?.each { current_CarrierRate ->
				if(referenceTypeMap.get(current_CarrierRate?.CSCarrierRateType) && current_CarrierRate?.CarrierRateNumber) {
					'N9' {
						'E128_01' referenceTypeMap.get(current_CarrierRate?.CSCarrierRateType)
						'E127_02' current_CarrierRate.CarrierRateNumber
					}
				}
			}



			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}
					if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' current_Body.Route.POR.CityDetails.LocationCode.UNLocationCode
					} else if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode) {
						'E310_03' current_Body.Route.POR.CityDetails.LocationCode.SchedKDCode
					}
					'E114_04' util.substring(current_Body?.Route?.POR?.CityDetails?.City, 1, 24)
					if (current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode) {
						'E26_05' current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode
					}
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.POR?.CityDetails?.State, current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode, conn)
				}
				if (current_Body?.Route?.FullReturnCutoff?.LocDT) {
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(current_Body.Route.FullReturnCutoff.LocDT.LocDT);
						'E374_01' date.after(currentDate) ? '139' : '140'
						'E373_02' util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT, HHmm)
					}
				}
			}
			//loopType = 'POL'
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}
					if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E310_03' current_Body.Route.FirstPOL.Port.LocationCode.UNLocationCode
					} else if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode) {
						'E310_03' current_Body.Route.FirstPOL.Port.LocationCode.SchedKDCode
					}
					'E114_04' util.substring(current_Body?.Route?.FirstPOL?.Port?.City, 1, 24)
					if (current_Body?.Route?.FirstPOL?.Port?.CSCountryCode) {
						'E26_05' current_Body?.Route?.FirstPOL?.Port?.CSCountryCode
					}
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.FirstPOL?.Port?.State, current_Body?.Route?.FirstPOL?.Port?.CSCountryCode, conn)
				}

				def SICutOffDT = current_Body?.GeneralInformation?.SICutOffDT?.LocDT?.LocDT
				def DT649 = null
				def DocDT = current_Body?.DocumentInformation?.RequiredDocument?.find{it.DocumentType=='Shipping Instruction/BL Master' && it.DueDT?.LocDT?.LocDT}?.DueDT?.LocDT?.LocDT
				if (DocDT) {
					DT649 = DocDT
				} else if (SICutOffDT) {
					DT649 = SICutOffDT
				}
				if (current_Body?.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT) {
					//POL_1
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(current_Body?.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT);
						'E374_01' date.after(currentDate) ? '139':'140'
						'E373_02' util.convertXmlDateTime(current_Body.Route.FirstPOL.DepartureDT.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route.FirstPOL.DepartureDT.LocDT, HHmm)
					}
				}
				if (DT649) {
					//POL_2
					'DTM' {
						'E374_01' '649'
						'E373_02' util.convertXmlDateTime(DT649, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(DT649, HHmm)
					}
				}
				if (current_Body?.GeneralInformation?.VGMCutOffDT?.LocDT?.LocDT) {
					//POL_3
					'DTM' {
						'E374_01' 'AAG'
						'E373_02' util.convertXmlDateTime(current_Body.GeneralInformation.VGMCutOffDT.LocDT.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.GeneralInformation.VGMCutOffDT.LocDT.LocDT, HHmm)
					}
				}
			}
			//loopType = 'POD'
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}
					if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
						'E310_03' current_Body.Route.LastPOD.Port.LocationCode.UNLocationCode
					} else if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) {
						'E310_03' current_Body.Route.LastPOD.Port.LocationCode.SchedKDCode
					}
					'E114_04' util.substring(current_Body?.Route?.LastPOD?.Port?.City, 1, 24)
					'E26_05' current_Body?.Route?.LastPOD?.Port?.CSCountryCode
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.LastPOD?.Port?.State, current_Body?.Route?.LastPOD?.Port?.CSCountryCode, conn)
				}
				if (current_Body?.Route?.LastPOD?.ArrivalDT?.LocDT) {
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(current_Body?.Route?.LastPOD?.ArrivalDT?.LocDT.LocDT);
						'E374_01' date.after(currentDate) ? '139' : '140'
						'E373_02' util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT, HHmm)
					}
				}
			}
			//loopType = 'FND'
			'Loop_R4' {
				'R4' {
					'E115_01' 'E'
					if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
						if(DCLocationCodeMap.get(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode)){
							'E309_02' 'DC'
							'E310_03' DCLocationCodeMap.get(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode)
						}else{
							'E309_02' 'UN'
							'E310_03' current_Body.Route.FND.CityDetails.LocationCode.UNLocationCode
						}
					}
					'E114_04' util.substring(current_Body?.Route?.FND?.CityDetails?.City, 1, 24)
					'E26_05' current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode
					'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.FND?.CityDetails?.State, current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode, conn)
				}
				def v_fnd_dt=null
				if (current_Body.Route?.ArrivalAtFinalHub && current_Body.Route?.ArrivalAtFinalHub[0]?.LocDT?.LocDT) {
					v_fnd_dt = current_Body.Route.ArrivalAtFinalHub[0].LocDT.LocDT
				} else if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT) {
					v_fnd_dt =current_Body.Route.LastPOD.ArrivalDT.LocDT?.LocDT
				}
				if (v_fnd_dt) {
					'DTM' {
						java.util.Date date = util.sdfXmlFmt.parse(v_fnd_dt);
						'E374_01' date.after(currentDate) ? '139' : '140'
						'E373_02' util.convertXmlDateTime(v_fnd_dt, yyyyMMdd)
						'E337_03' util.convertXmlDateTime(v_fnd_dt, HHmm)
					}
				}
			}

			BigDecimal weight = BigDecimal.ZERO
			BigDecimal volume = BigDecimal.ZERO
			BigDecimal quantity = BigDecimal.ZERO

			BigDecimal rateKGS = new BigDecimal('2.20462262')
			BigDecimal rateTon = new BigDecimal('1000')
			BigDecimal rateCBM = new BigDecimal('35.3146667215')

			current_Body?.Cargo?.each {current_Cargo ->

				if(current_Cargo.GrossWeight?.Weight){
					if(current_Cargo?.GrossWeight?.WeightUnit == "KGS" || current_Cargo?.GrossWeight?.WeightUnit == "KGM" ){
						weight = weight + (current_Cargo.GrossWeight?.Weight?.replaceAll('\\+','')?.toBigDecimal() * rateKGS)
					}else if(current_Cargo?.GrossWeight?.WeightUnit == "LBS"){
						weight = weight + (current_Cargo.GrossWeight?.Weight?.replaceAll('\\+','')?.toBigDecimal())
					}else if(current_Cargo?.GrossWeight?.WeightUnit == "TON"){
						weight = weight + (current_Cargo.GrossWeight?.Weight?.replaceAll('\\+','')?.toBigDecimal() * rateTon * rateKGS)
					}
				}

				if(current_Cargo.Volume?.Volume){
					if(current_Cargo?.Volume?.VolumeUnit == "CBM"){
						volume = volume + (current_Cargo.Volume?.Volume?.toBigDecimal() * rateCBM)
					}else if(current_Cargo?.Volume?.VolumeUnit == "CBF"){
						volume = volume + current_Cargo.Volume?.Volume?.toBigDecimal()
					}
				}

				if(current_Cargo?.Packaging?.PackageQty){
					quantity = quantity + current_Cargo?.Packaging?.PackageQty?.toBigDecimal()
				}
			}

			'Loop_LX' {
				'LX' {
					'E554_01' '1'
				}
				'L0' {
					'E213_01' '1'
					'E81_04' weight != BigDecimal.ZERO ? weight?.setScale(3,BigDecimal.ROUND_HALF_UP)?.toString() : weight
					'E187_05' 'G'
					'E183_06' volume != BigDecimal.ZERO ? volume?.setScale(3,BigDecimal.ROUND_HALF_UP)?.toString() : weight
					'E184_07' 'E'
					'E80_08' quantity?.toString()
					Map<String,String> packageUnitMap = util.getCdeConversionFromIntCde(TP_ID,"BC","O",null,'X.12',"PackageUnit",current_Body?.Cargo[0]?.Packaging?.PackageType,conn)
					if(packageUnitMap.get("EXT_CDE")){
						'E211_09' packageUnitMap.get("EXT_CDE")
					}else{
						'E211_09' 'CTN'
					}
					'E458_10' ''
					'E188_11' 'L'
					'E56_12' ''
					'E380_13' ''
					'E211_14' ''
					'E1073_15' ''
				}
				'L5' {
					'E213_01' '1'
					'E79_02' util.substring(current_Body?.Cargo[0]?.CargoDescription, 1, 50)
					'E22_03' ''
					'E23_04' ''
					'E103_05' ''
					'E87_06' ''
					'E88_07' ''
					'E23_08' ''
					'E22_09' ''
					'E595_10' ''
				}
				'L1' {
					'E213_01' ''
					'E60_02' ''
					'E122_03' ''
					'E58_04' ''
					'E191_05' ''
					'E117_06' ''
					'E120_07' ''
					'E150_08' ''
					'E121_09' ''
					'E39_10' ''
					'E16_11' ''
					'E276_12' ''
					'E257_13' ''
					'E74_14' ''
					'E122_15' ''
					'E372_16' ''
					'E220_17' ''
					'E221_18' ''
					'E954_19' ''
					'E100_20' ''
					'E610_21' ''
				}
			}

			Map dirMap = ['E':'East', 'S':'South', 'W':'West', 'N':'North']
			if (current_Body?.Route?.OceanLeg?.size()>0) {
				if (current_Body?.Route?.OceanLeg[0].SVVD?.Loading && current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.VesselName) {
					'V1' {
						if (StringUtil.isNotEmpty(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.LloydsNumber)) {
							'E597_01' current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.LloydsNumber
						} else {
							'E597_01' '99999996'
						}
						'E182_02' util.substring(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.VesselName, 1, 28)
						'E26_03' ''
						def defVoyage = current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Voyage
						defVoyage = (defVoyage == null ? "" : defVoyage)
						if (!current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Direction) {
							'E55_04' util.substring(defVoyage, 1, 10)
						} else if (dirMap.get(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Direction)) {
							'E55_04' util.substring(defVoyage + dirMap.get(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Direction), 1, 10)
						}
						'E140_05' ''
						'E249_06' ''
						if (current_Body?.Route?.OceanLeg?.size() > 1) {
							'E854_07' 'SP'
						} else {
							'E854_07' 'GC'
						}
						if (current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber) {
							'E897_08' 'L'
						} else if (!current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber && current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.Vessel) {
							'E897_08' 'Z'
						} else if (!current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber && !current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.Vessel) {
							'E897_08' ''
						}
						'E91_09' ''
					}
				}
			}
			if(current_Body?.Route?.OceanLeg?.size()>1){
				if(current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge && current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.VesselName) {
					'V1' {
						if (StringUtil.isNotEmpty(current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.LloydsNumber)) {
							'E597_01' current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.LloydsNumber
						} else {
							'E597_01' '99999996'
						}
						'E182_02' util.substring(current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.VesselName, 1, 28)
						'E26_03' ''
						def devVoyage = current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.Voyage
						devVoyage = (devVoyage == null ? "" : devVoyage.trim())
						if (!current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.Direction) {
							'E55_04' util.substring(devVoyage, 1, 10)
						} else if (dirMap.get(current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.Direction)) {
							'E55_04' util.substring(devVoyage + dirMap.get(current_Body?.Route?.OceanLeg[-1].SVVD?.Discharge?.Direction), 1, 10)
						}
						'E140_05' ''
						'E249_06' ''
						'E854_07' 'GC'
						if (current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.LloydsNumber) {
							'E897_08' 'L'
						} else if (!current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.LloydsNumber && current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.Vessel) {
							'E897_08' 'Z'
						} else if (!current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.LloydsNumber && !current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.Vessel) {
							'E897_08' ''
						}
						'E91_09' ''
					}
				}
			}
			if(current_Body?.GeneralInformation?.SICutOffDT?.LocDT){
				'V9' {
					'E304_01' 'AAA'
					'E106_02' ''
					'E373_03' util.convertXmlDateTime(current_Body?.GeneralInformation?.SICutOffDT?.LocDT, yyyyMMdd)
					'E337_04' util.convertXmlDateTime(current_Body?.GeneralInformation?.SICutOffDT?.LocDT, HHmm)
					'E19_05' ''
					'E156_06' ''
					'E26_07' ''
					'E641_08' ''
					'E154_09' ''
					'E380_10' ''
					'E1274_11' ''
					'E61_12' ''
					'E623_13' ''
					'E380_14' ''
					'E154_15' ''
					'E86_16' ''
					'E86_17' ''
					'E86_18' ''
					'E81_19' ''
					'E82_20' ''
				}
			}
			if(current_Body?.Route?.FullReturnCutoff?.LocDT){
				'V9' {
					'E304_01' 'TRN'
					'E106_02' ''
					'E373_03' util.convertXmlDateTime(current_Body?.Route?.FullReturnCutoff?.LocDT, yyyyMMdd)
					'E337_04' util.convertXmlDateTime(current_Body?.Route?.FullReturnCutoff?.LocDT, HHmm)
					'E19_05' ''
					'E156_06' ''
					'E26_07' ''
					'E641_08' ''
					'E154_09' ''
					'E380_10' ''
					'E1274_11' ''
					'E61_12' ''
					'E623_13' ''
					'E380_14' ''
					'E154_15' ''
					'E86_16' ''
					'E86_17' ''
					'E86_18' ''
					'E81_19' ''
					'E82_20' ''
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
		inputXmlBody = util.removeBOM(inputXmlBody)

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
		BookingConfirm bc = parser.xmlParser(inputXmlBody, BookingConfirm.class)


		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
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
		def T301 = outXml.createNode('T301')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.

		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()
		def headerMsgDT = util.convertDateTime(bc.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		def txnErrorKeys = []

		//Start mapping

		bc?.Body?.eachWithIndex{current_Body, current_BodyIndex ->

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body)

			//mapping
			if(current_Body?.GeneralInformation?.BookingStatus=='Declined'||current_Body?.GeneralInformation?.BookingStatus=='Rejected') {
				//Declined / Reject mapping
				generateBody4RejectCase(current_Body, outXml)
			} else {
				generateBody(current_Body, outXml)
			}

			// posp checking
			if(errorKeyList.isEmpty()){
				pospValidation(current_Body, writer?.toString(), errorKeyList)
			}

			bcUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			bcUtil.buildBizKey(bizKeyXml, bc.Header, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, TP_ID, conn)

			txnErrorKeys.add(errorKeyList)
		}


		//End root node
		outXml.nodeCompleted(null,T301)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		bcUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		bcUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		bcUtil.promoteHeaderIntChgMsgId(appSessionId, bc.Header?.InterchangeMessageID);
		if (bc.Body && bc.Body.size()>0) {
			bcUtil.promoteScacCode(appSessionId, bc.Body[0].GeneralInformation?.SCACCode);
		}

		String result = '';
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			result = writer?.toString();
			result = util.cleanXml(result)
		}

		writer.close();

		return result;
	}


	List<Map<String,String>>  prepValidation(Body current_Body){

		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()

		bcUtil.checkBCStatus(current_Body?.GeneralInformation?.BookingStatus, ['Confirmed','Cancelled','No Show','Declined','Rejected'], false, null , errorKeyList)

		return errorKeyList
	}


	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList){


//		XmlParser xmlParser = new XmlParser();
//		Node node = xmlParser.parseText(outputXml + "</T301>")

	}

	void generateBody4RejectCase(Body current_Body, MarkupBuilder outXml) {
		//Gawing: rejected bc should be extracted to a method
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '301'
				'E329_02' '-999'
			}
			'B1' {
				'E140_01' current_Body?.GeneralInformation?.SCACCode
				def def145 = ''
				if (current_Body?.GeneralInformation?.BookingStatusRemarks?.indexOf('|') >= 0) {
					def145 = util.substring(current_Body?.GeneralInformation?.BookingStatusRemarks, 1, current_Body?.GeneralInformation?.BookingStatusRemarks.indexOf('|'))
				}
				if (util.isEmpty(def145)) {
					def145 = current_Body?.AssociatedExternalReference?.find{it.CSReferenceType=='OTH' && it.EDIReferenceType=='50'}?.ReferenceNumber
				}
				'E145_02' util.substring(def145?.trim(), 1, 30)

				'E373_03' util.convertXmlDateTime(current_Body.GeneralInformation?.BookingStatusDT?.LocDT, yyyyMMdd)
				'E558_04' 'R'
			}
			'Y3' {
				'E13_01' 'DECLINE'
			}
			if(current_Body?.GeneralInformation?.CarrierBookingNumber) {
				'N9' {
					'E128_01' 'WY'
					'E127_02' util.substring(current_Body.GeneralInformation.CarrierBookingNumber, 1, 30)
				}
			}
			if(current_Body?.Route) {
				'Loop_R4' {
					'R4' {
						'E115_01' 'R'
						if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}
						if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' current_Body.Route.POR.CityDetails.LocationCode.UNLocationCode
						} else if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' current_Body.Route.POR.CityDetails.LocationCode.SchedKDCode
						}
						'E114_04' util.substring(current_Body?.Route?.POR?.CityDetails?.City, 1, 24)
						if (current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode) {
							'E26_05' current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode
						}
						'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.POR?.CityDetails?.State, current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode, conn)
					}
					if (current_Body?.Route?.FullReturnCutoff?.LocDT) {
						'DTM' {
							java.util.Date date = util.sdfXmlFmt.parse(current_Body.Route.FullReturnCutoff.LocDT.LocDT);
							'E374_01' date.after(currentDate) ? '139' : '140'
							'E373_02' util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT, yyyyMMdd)
							'E337_03' util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT, HHmm)
						}
					}
				}
				//loopType = 'POL'
				'Loop_R4' {
					'R4' {
						'E115_01' 'L'
						if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}
						if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' current_Body.Route.FirstPOL.Port.LocationCode.UNLocationCode
						} else if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' current_Body.Route.FirstPOL.Port.LocationCode.SchedKDCode
						}
						'E114_04' util.substring(current_Body?.Route?.FirstPOL?.Port?.City, 1, 24)
						if (current_Body?.Route?.FirstPOL?.Port?.CSCountryCode) {
							'E26_05' current_Body?.Route?.FirstPOL?.Port?.CSCountryCode
						}
						'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.FirstPOL?.Port?.State, current_Body?.Route?.FirstPOL?.Port?.CSCountryCode, conn)
					}

					def SICutOffDT = current_Body?.GeneralInformation?.SICutOffDT?.LocDT?.LocDT
					def DT649 = null
					def DocDT = current_Body?.DocumentInformation?.RequiredDocument?.find {
						it.DocumentType == 'Shipping Instruction/BL Master' && it.DueDT?.LocDT?.LocDT
					}?.DueDT?.LocDT?.LocDT
					if (DocDT) {
						DT649 = DocDT
					} else if (SICutOffDT) {
						DT649 = SICutOffDT
					}
					if (current_Body?.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT) {
						//POL_1
						'DTM' {
							java.util.Date date = util.sdfXmlFmt.parse(current_Body?.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT);
							'E374_01' date.after(currentDate) ? '139' : '140'
							'E373_02' util.convertXmlDateTime(current_Body.Route.FirstPOL.DepartureDT.LocDT, yyyyMMdd)
							'E337_03' util.convertXmlDateTime(current_Body.Route.FirstPOL.DepartureDT.LocDT, HHmm)
						}
					}
					if (DT649) {
						//POL_2
						'DTM' {
							'E374_01' '649'
							'E373_02' util.convertXmlDateTime(DT649, yyyyMMdd)
							'E337_03' util.convertXmlDateTime(DT649, HHmm)
						}
					}
					if (current_Body?.GeneralInformation?.VGMCutOffDT?.LocDT?.LocDT) {
						//POL_3
						'DTM' {
							'E374_01' 'AAG'
							'E373_02' util.convertXmlDateTime(current_Body.GeneralInformation.VGMCutOffDT.LocDT.LocDT, yyyyMMdd)
							'E337_03' util.convertXmlDateTime(current_Body.GeneralInformation.VGMCutOffDT.LocDT.LocDT, HHmm)
						}
					}
				}
				//loopType = 'POD'
				'Loop_R4' {
					'R4' {
						'E115_01' 'D'
						if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}
						if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
							'E310_03' current_Body.Route.LastPOD.Port.LocationCode.UNLocationCode
						} else if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) {
							'E310_03' current_Body.Route.LastPOD.Port.LocationCode.SchedKDCode
						}
						'E114_04' util.substring(current_Body?.Route?.LastPOD?.Port?.City, 1, 24)
						'E26_05' current_Body?.Route?.LastPOD?.Port?.CSCountryCode
						'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.LastPOD?.Port?.State, current_Body?.Route?.LastPOD?.Port?.CSCountryCode, conn)
					}
					if (current_Body?.Route?.LastPOD?.ArrivalDT?.LocDT) {
						'DTM' {
							java.util.Date date = util.sdfXmlFmt.parse(current_Body?.Route?.LastPOD?.ArrivalDT?.LocDT.LocDT);
							'E374_01' date.after(currentDate) ? '139' : '140'
							'E373_02' util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT, yyyyMMdd)
							'E337_03' util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT, HHmm)
						}
					}
				}
				//loopType = 'FND'
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
							if (DCLocationCodeMap.get(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode)) {
								'E309_02' 'DC'
								'E310_03' DCLocationCodeMap.get(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode)
							} else {
								'E309_02' 'UN'
								'E310_03' current_Body.Route.FND.CityDetails.LocationCode.UNLocationCode
							}
						}
						'E114_04' util.substring(current_Body?.Route?.FND?.CityDetails?.City, 1, 24)
						'E26_05' current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode
						'E156_08' util.getCS2MasterCityStateCodeByStateCntryNoEmptyStateCde(current_Body?.Route?.FND?.CityDetails?.State, current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode, conn)
					}
					def v_fnd_dt = null
					if (current_Body.Route?.ArrivalAtFinalHub && current_Body.Route?.ArrivalAtFinalHub[0]?.LocDT?.LocDT) {
						v_fnd_dt = current_Body.Route.ArrivalAtFinalHub[0].LocDT.LocDT
					} else if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT) {
						v_fnd_dt = current_Body.Route.LastPOD.ArrivalDT.LocDT?.LocDT
					}
					if (v_fnd_dt) {
						'DTM' {
							java.util.Date date = util.sdfXmlFmt.parse(v_fnd_dt);
							'E374_01' date.after(currentDate) ? '139' : '140'
							'E373_02' util.convertXmlDateTime(v_fnd_dt, yyyyMMdd)
							'E337_03' util.convertXmlDateTime(v_fnd_dt, HHmm)
						}
					}
				}
			} else{
				'Loop_R4' {
					'R4' {
						'E115_01' 'L'
						'E309_02' 'UN'
						'E310_03' 'USNYC'
						'E114_04' 'NEW YORK USA'
					}
				}
			}
			'Loop_LX' {
				'LX' { 'E554_01' '1' }
				def remarks = ''
				if (current_Body?.GeneralInformation?.BookingStatusRemarks?.indexOf('|') >= 0) {
					remarks = current_Body?.GeneralInformation?.BookingStatusRemarks
					remarks = remarks.substring(remarks.indexOf('|')+1)
					if (remarks.indexOf('|') >= 0) {
						remarks = remarks.substring(0, remarks.indexOf('|'))
					}
				}
				if (remarks) {
					remarks = remarks.trim()
					'K1' {
						'E61_01' util.substring(remarks, 1, 30)
						'E61_02' util.substring(remarks, 31, 30)
					}
					if (remarks.length()>60) {
						'K1' {
							'E61_01' util.substring(remarks, 61, 30)
							if (remarks.length()>90) {
								'E61_02' util.substring(remarks, 91, 30)
							}
						}
					}
					if (remarks.length()>120) {
						'K1' {
							'E61_01' util.substring(remarks, 121, 30)
							if (remarks.length()>150) {
								'E61_02' util.substring(remarks, 151, 30)
							}
						}
					}
					if (remarks.length()>180) {
						'K1' {
							'E61_01' util.substring(remarks, 181, 30)
							if (remarks.length()>210) {
								'E61_02' util.substring(remarks, 211, 30)
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
}
