package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.text.DecimalFormat
import java.text.NumberFormat

import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.OceanLeg
import cs.b2b.core.mapping.bean.bc.Party
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.core.mapping.bean.bc.RemarkLines
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * @author RENGA
 * @change SHAEDI235 http://i2isd/sites/csisa/Lists/Workplan/DispForm.aspx?ID=29906
 * @change B2BSCR20170703004122 http://i2isd/sites/csisa/Lists/Workplan/DispForm.aspx?ID=31457
 * @change SHAEDI235-1 http://i2isd/sites/csisa/Lists/Workplan/DispForm.aspx?ID=31456
 */
public class CUS_CS2BCXML_D96B_EIR_EASIPASS {

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
	def yyyyMMddHHmmss = 'yyyyMMddHHmmss'
	def yyyyMMddHHmm = "yyyyMMddHHmm"
	def yyyyMMdd = "yyyyMMdd"
	def HHmmss = 'HHmmss'
	
	def currentSystemDt = null
	DecimalFormat decimalFormatter = new DecimalFormat("#.####")
	DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")
	
	def msgFmtId = "EDIFACT"
	
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		Map<String, String> unLocationCodeMap = ['INHZA':'INHZR', 'INPPV':'INPAV', 'IRBND':'IRBN1', 'JOAQJ':'JOAQB', 'MTMAR':'MTMLA', 'SADMM':'SADM1', 'SARUH':'SADM2']
		def currentBcScac = current_Body.GeneralInformation?.SCACCode
		
		outXml.'Group_UNH' {
			'UNH' {
				'E0062_01' '-999'
				'S009_02' {
					'E0065_01' 'IFTMBC'
					'E0052_02' 'D'
					'E0054_03' '96B'
					'E0051_04' 'UN'
					'E0057_05' '1.3'
					'E0110_06' ''
					'E0113_07' ''
				}
				'E0068_03' ''
				'S010_04' {
					'E0070_01' ''
					'E0073_02' ''
				}
			}
			
			'BGM' {
				'C002_01' {
					'E1001_01' '770'
					'E1131_02' ''
					'E3055_03' ''
					'E1000_04' ''
				}
				'C106_02' {
					'E1004_01' util.substring(current_Body.GeneralInformation?.CarrierBookingNumber, 1, 35)
					'E1056_02' ''
					'E1060_03' ''
				}
				def statusMap = ['Confirmed':'9', 'Cancelled':'1', 'PreBooking':'PR', 'No Show':'NS', 'Wait Listed':'WL']
				def bcSts = current_Body.GeneralInformation?.BookingStatus
				if (statusMap.get(bcSts)) {
					'E1225_03' statusMap.get(bcSts)
				} else if (bcSts == 'Pending') {
					if (current_Body.GeneralInformation?.BookingStatusRemarks?.toUpperCase() == 'SHIPMENT WAIT-LISTED') {
						'E1225_03' 'WL'
					} else {
						'E1225_03' 'PE'
					}
				}
				'E4343_04' ''
			}
			
			def vModifiedDt = '';
			if (current_Body.GeneralInformation?.LastModifiedDT?.LocDT?.LocDT) {
				vModifiedDt = current_Body.GeneralInformation?.LastModifiedDT?.LocDT?.LocDT
			} else {
				vModifiedDt = current_Body.GeneralInformation?.LastModifiedDT?.GMT
			}
			if (vModifiedDt) {
				'DTM' {
					'C507_01' {
						'E2005_01' '324'
						'E2380_02' util.convertXmlDateTime(vModifiedDt, yyyyMMddHHmmss);
						'E2379_03' '204'
					}
				}
			}
			if (current_Body.GeneralInformation?.BookingStatus=='Confirmed') {
				def vBkgStsDt = '';
				if (current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.LocDT) {
					vBkgStsDt = current_Body.GeneralInformation?.BookingStatusDT?.LocDT?.LocDT
				} else {
					vBkgStsDt = current_Body.GeneralInformation?.BookingStatusDT?.GMT
				}
				if (vBkgStsDt) {
					'DTM' {
						'C507_01' {
							'E2005_01' '55'
							'E2380_02' util.convertXmlDateTime(vBkgStsDt, yyyyMMddHHmmss);
							'E2379_03' '204'
						}
					}
				}
			}
			

			'TSR' {
				'C536_01' {
					//Body/Route/Haulage/OutBound
					def haulage = current_Body.Route?.Haulage?.OutBound
					if (util.isEmpty(haulage)) {
						haulage = 'C'
					}
					if (current_Body.Route?.Haulage?.InBound) {
						haulage = haulage + current_Body.Route.Haulage?.InBound
					} else {
						haulage = haulage + 'C'
					}
					def mapHaulage = ['CC':'27', 'CM':'28', 'MC':'29', 'MM':'30']
					'E4065_01' mapHaulage.get(haulage)
					'E1131_02' ''
					'E3055_03' ''
				}
				def vInb = current_Body.GeneralInformation?.TrafficMode?.InBound
				def vOutb = current_Body.GeneralInformation?.TrafficMode?.OutBound
				'C233_02' {
					if (vOutb=='FCL' && vInb=='FCL') {
						'E7273_01' '2'
					} else if (vOutb=='LCL' && vInb=='LCL') {
						'E7273_01' '3'
					} else if (vOutb=='FCL' && vInb=='LCL') {
						'E7273_01' '4'
					} else if (vOutb=='LCL' && vInb=='FCL') {
						'E7273_01' '5'
					} 
				}
			}
			
			def defMapFtxType = ['Bill Of Lading Instruction':'AAS', 'Booking':'AAI', 'Consignment Reference':'ICN', 'Customs':'AAH', 'EDI Bkg Confirmation Remark':'ABP']  //SHAEDI235-1
			
			//if exists(Body/RemarkLines[@RemarkType = "Booking"])
			List<RemarkLines> remarks = current_Body.RemarkLines.findAll(){it?.attr_RemarkType && it.attr_RemarkType in defMapFtxType.keySet()}
			remarks?.each { current_RemarksLines ->
				if (current_RemarksLines.RemarkLines) {
					'FTX' {
						'E4451_01' defMapFtxType.get(current_RemarksLines.attr_RemarkType)
						'C108_04' {
							if (util.substring(current_RemarksLines.RemarkLines, 1, 70)?.trim()) {
								'E4440_01' util.substring(current_RemarksLines.RemarkLines, 1, 70)
							}
							if (util.substring(current_RemarksLines.RemarkLines, 71, 70)?.trim()) {
								'E4440_02' util.substring(current_RemarksLines.RemarkLines, 71, 70)
							}
							if (util.substring(current_RemarksLines.RemarkLines, 141, 70)?.trim()) {
								'E4440_03' util.substring(current_RemarksLines.RemarkLines, 141, 70)
							}
							if (util.substring(current_RemarksLines.RemarkLines, 211, 70)?.trim()) {
								'E4440_04' util.substring(current_RemarksLines.RemarkLines, 211, 70)
							}
							if (util.substring(current_RemarksLines.RemarkLines, 281, 70)?.trim()) {
								'E4440_05' util.substring(current_RemarksLines.RemarkLines, 281, 70)
							}
						}
					}
					if (current_RemarksLines.RemarkLines?.length()>350) {
						'FTX' {
							'E4451_01' defMapFtxType.get(current_RemarksLines.attr_RemarkType)
							'C108_04' {
								if (util.substring(current_RemarksLines.RemarkLines, 351, 70)?.trim()) {
									'E4440_01' util.substring(current_RemarksLines.RemarkLines, 351, 70)
								}
								if (util.substring(current_RemarksLines.RemarkLines, 421, 70)?.trim()) {
									'E4440_02' util.substring(current_RemarksLines.RemarkLines, 421, 70)
								}
								if (util.substring(current_RemarksLines.RemarkLines, 491, 70)?.trim()) {
									'E4440_03' util.substring(current_RemarksLines.RemarkLines, 491, 70)
								}
								if (util.substring(current_RemarksLines.RemarkLines, 561, 70)?.trim()) {
									'E4440_04' util.substring(current_RemarksLines.RemarkLines, 561, 70)
								}
								if (util.substring(current_RemarksLines.RemarkLines, 631, 70)?.trim()) {
									'E4440_05' util.substring(current_RemarksLines.RemarkLines, 631, 70)
								}
							}
						}
					}
				}
			}

			//SHAEDI235 start, B2BSCR20170703004122 rollback
			if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode == "CNSHA"){
				OceanLeg firstOceanLegPODNotCN = current_Body?.Route?.OceanLeg?.find{it?.POD?.Port?.Country?.toUpperCase() != "CHINA"}	//SHAEDI235-1
				String intBlockCode = null
				String extLocationCode = null
				if(firstOceanLegPODNotCN){
					if (firstOceanLegPODNotCN?.BlockCode?.UserDischargeBlock){
						intBlockCode = firstOceanLegPODNotCN?.BlockCode?.UserDischargeBlock
					}else if(firstOceanLegPODNotCN?.BlockCode?.OverflowDischargeBlock){
						intBlockCode = firstOceanLegPODNotCN?.BlockCode?.OverflowDischargeBlock
					}else if(firstOceanLegPODNotCN?.BlockCode?.StandardDischargeBlock){
						intBlockCode = firstOceanLegPODNotCN?.BlockCode?.StandardDischargeBlock
					}

					if(unLocationCodeMap.get(firstOceanLegPODNotCN?.POD?.Port?.LocationCode?.UNLocationCode)){
						extLocationCode = unLocationCodeMap.get(firstOceanLegPODNotCN?.POD?.Port?.LocationCode?.UNLocationCode)
					}else{
						extLocationCode = firstOceanLegPODNotCN?.POD?.Port?.LocationCode?.UNLocationCode
					}
				}

				if(extLocationCode){
					'FTX' {
						'E4451_01' 'SIN'
						'C108_04' {
							if (intBlockCode == "7" || StringUtil.isEmpty(intBlockCode)) {
								'E4440_01' extLocationCode
							}else if (StringUtil.isNotEmpty(intBlockCode)){
								'E4440_01' util.substring(extLocationCode, 1 ,4) + intBlockCode
							}
						}
					}
				}
			}
			//SHAEDI235 end
			
			def mapRefTypes = 
			['BOOKING NUMBER':'BN', 
			'CONSIGNEE\'S ORDER NUMBER':'CG', 
			'CUSTOMER REFRENCE NUMBER':'CR', 
			'CONTRACT NUMBER':'CT', 
			'FORWARDER REFERENCE':'FN', 
			'INVOICE NUMBER':'IN', 
			'PURCHASE ORDER NUMBER':'PO', 
			'SERVICE CONTRACT NUMBER':'SC', 
			'SHIPPER\'S IDENTIFYING NUMBER FOR SHIPMENT (SID)':'SI', 
			'SHIPPING ORDER NUMBER':'SO', 
			'SHIPPER REFERENCE':'SR']
			
			//Group_2
			current_Body.ExternalReference?.each { current_ExtRef ->
				if (current_ExtRef.ReferenceDescription && current_ExtRef.ReferenceNumber && current_ExtRef.ReferenceDescription.toUpperCase().trim() in mapRefTypes.keySet()) {
					'Group2_RFF' {
						'RFF' {
							'C506_01' {
								def defType = mapRefTypes.get(current_ExtRef.ReferenceDescription.toUpperCase().trim())
								'E1153_01' defType
								if (defType=='BN' && current_Body.GeneralInformation?.SCACCode == 'OOLU') {
									if (current_ExtRef.ReferenceNumber?.trim().startsWith('OOLU')) {
										'E1154_02' util.substring(current_ExtRef.ReferenceNumber?.trim(), 1, 35)
									} else {
										'E1154_02' util.substring('OOLU' + current_ExtRef.ReferenceNumber?.trim(), 1, 35)
									}
								} else {
									'E1154_02' util.substring(current_ExtRef.ReferenceNumber?.trim(), 1, 35)
								}
							}
						}
					}
				}
			}
			
			String refBkgNum = current_Body.GeneralInformation?.CarrierBookingNumber
			def extRefBkg = current_Body.ExternalReference?.find(){it.ReferenceDescription?.toUpperCase() == 'BOOKING NUMBER' && it.ReferenceNumber == refBkgNum}
			if (extRefBkg == null && refBkgNum) {
				'Group2_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' 'BN'
							if (current_Body.GeneralInformation?.SCACCode == 'OOLU' && !refBkgNum.startsWith('OOLU')) {
								refBkgNum = 'OOLU' + refBkgNum
							}
							'E1154_02' util.substring(refBkgNum, 1, 35)
						}
					}
				}
			}
						
			//get the minimum oceanleg sequence
			def legSeqMin = current_Body.Route?.OceanLeg?.min {it==null || it.LegSeq==null?0:Integer.parseInt(it.LegSeq) }?.LegSeq
			def legSeqMax = current_Body.Route?.OceanLeg?.max {it==null || it.LegSeq==null?0:Integer.parseInt(it.LegSeq) }?.LegSeq
			//get the maximum oceanleg sequence
			
			//Group_3 Route
			current_Body.Route?.OceanLeg?.eachWithIndex { current_OceanLeg, current_Index ->
				'Group3_TDT' {
					'TDT' {
						switch (current_OceanLeg.LegSeq) {
							case '1' : 'E8051_01' '20'; break;
							case '2' : 'E8051_01' '22'; break;
							case '3' : 'E8051_01' '23'; break;
							case '4' : 'E8051_01' 'Z1'; break;
							case '5' : 'E8051_01' 'Z2'; break;
						}
						'E8028_02' util.substring(current_OceanLeg.LoadingExtVoyage?.trim(), 1, 17)
						'C220_03' {
							'E8067_01' '1'
							'E8066_02' ''
						}
						'C228_04' {
							'E8179_01' ''
							'E8178_02' ''
						}
						'C040_05' {
							'E3127_01' current_Body.GeneralInformation?.SCACCode?.trim()
							'E1131_02' '172'
							'E3055_03' '182'
							'E3128_04' ''
						}
						'C222_08' {
							if (current_OceanLeg.SVVD?.Loading?.LloydsNumber) {
								'E8213_01' util.substring(current_OceanLeg.SVVD?.Loading?.LloydsNumber.trim(), 1, 20)
								'E1131_02' 'ZZ1'
							} else if (current_OceanLeg.SVVD?.Loading?.CallSign) {
								'E8213_01' util.substring(current_OceanLeg.SVVD?.Loading?.CallSign.trim(), 1, 20)
								'E1131_02' '103'
							} else if (current_OceanLeg.SVVD?.Loading?.Vessel) {
								'E8213_01' util.substring(current_OceanLeg.SVVD?.Loading?.Vessel.trim(), 1, 20)
								'E1131_02' 'ZZZ'
							}
							'E3055_03' ''
							'E8212_04' util.substring(current_OceanLeg.SVVD?.Loading?.VesselName?.trim(), 1, 35)
							'E8453_05' ''
						}
						'E8281_09' ''
					}
					
					// POR for the 1st OceanLeg
					if (current_OceanLeg.LegSeq == legSeqMin) {
						// Group_3/Group_4 - POR
						//if (current_Body.Route?.POR?.CityDetails?.City || current_Body.Route?.POR?.CityDetails?.State || current_Body.Route?.POR?.CityDetails?.Country) {
							'Group4_LOC' {
								'LOC' {
									'E3227_01' '88'
									'C517_02' {
										'E3225_01' util.substring((unLocationCodeMap.get(current_Body.Route?.POR?.CityDetails?.LocationCode?.MutuallyDefinedCode) ? unLocationCodeMap.get(current_Body.Route?.POR?.CityDetails?.LocationCode?.MutuallyDefinedCode) : current_Body.Route?.POR?.CityDetails?.LocationCode?.MutuallyDefinedCode), 1, 25)	//SHAEDI235
										'E1131_02' '145'
										'E3055_03' '6'
										'E3224_04' current_Body.Route?.POR?.CityDetails?.City?.trim()
									}
									'C519_03' {
										'E3223_01' ''
										'E1131_02' '163'
										'E3055_03' ''
										'E3222_04' current_Body.Route?.POR?.CityDetails?.State?.trim()
									}
									'C553_04' {
										'E3233_01' ''
										'E1131_02' '162'
										'E3055_03' ''
										'E3232_04' current_Body.Route?.POR?.CityDetails?.Country?.trim()
									}
								}
								
								def defPorDt = ''
								if ((current_Body.Route?.POR?.CityDetails?.City == current_Body.Route?.FirstPOL?.Port?.City) ||
									(current_Body.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode == current_Body.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode)) {
									defPorDt = current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT
								} else {
									defPorDt = current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT
								}
								if (defPorDt) {
									'DTM' {
										'C507_01' {
											'E2005_01' '180'
											'E2380_02' util.convertXmlDateTime(defPorDt, yyyyMMddHHmm)
											'E2379_03' '203'
										}
									}
								}
							}
						//}
					}
					
					// Group_3/Group_4 - POL
					if (current_OceanLeg.POL?.Port?.PortName) {
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '9'
								'C517_02' {
									'E3225_01' util.substring((unLocationCodeMap.get(current_OceanLeg.POL?.Port?.LocationCode?.UNLocationCode) ? unLocationCodeMap.get(current_OceanLeg.POL?.Port?.LocationCode?.UNLocationCode) : current_OceanLeg.POL?.Port?.LocationCode?.UNLocationCode), 1, 25) //SHAEDI235
									'E1131_02' '139'
									'E3055_03' '6'
									'E3224_04' current_OceanLeg.POL?.Port?.City?.trim()
								}
							}
							
							def defDt = current_OceanLeg.DepartureDT?.find(){it?.attr_Indicator=='A'}?.LocDT?.LocDT
							if (defDt==null) {
								defDt = current_OceanLeg.DepartureDT?.find(){it?.attr_Indicator=='E'}?.LocDT?.LocDT
							}
							if (defDt==null && current_OceanLeg.DepartureDT && current_OceanLeg.DepartureDT.size()>0) {
								defDt = current_OceanLeg.DepartureDT[0]?.LocDT?.LocDT
							}
							if (defDt) {
								'DTM' {
									'C507_01' {
										'E2005_01' '133'
										'E2380_02' util.convertXmlDateTime(defDt, yyyyMMddHHmm)
										'E2379_03' '203'
									}
								}
							}
						}
					}
					
					// Group_3/Group_4 - POD
					if (current_OceanLeg.POD?.Port?.PortName) {
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '11'
								'C517_02' {
									'E3225_01' util.substring((unLocationCodeMap.get(current_OceanLeg.POD?.Port?.LocationCode?.UNLocationCode) ? unLocationCodeMap.get(current_OceanLeg.POD?.Port?.LocationCode?.UNLocationCode) : current_OceanLeg.POD?.Port?.LocationCode?.UNLocationCode), 1, 25)	//SHAEDI235
									'E1131_02' '139'
									'E3055_03' '6'
									'E3224_04' current_OceanLeg.POD?.Port?.City?.trim()
								}
							}
							def defDt = current_OceanLeg.ArrivalDT?.find(){it?.attr_Indicator=='A'}?.LocDT?.LocDT
							if (defDt==null) {
								defDt = current_OceanLeg.ArrivalDT?.find(){it?.attr_Indicator=='E'}?.LocDT?.LocDT
							}
							if (defDt==null && current_OceanLeg.ArrivalDT && current_OceanLeg.ArrivalDT.size()>0) {
								defDt = current_OceanLeg.ArrivalDT[0]?.LocDT?.LocDT
							}
							if (defDt) {
								'DTM' {
									'C507_01' {
										'E2005_01' '132'
										'E2380_02' util.convertXmlDateTime(defDt, yyyyMMddHHmm)
										'E2379_03' '203'
									}
								}
							}
						}
					}
					
					// Group_3/Group_4 - FND
					if (current_OceanLeg.LegSeq == legSeqMax) {
						//if (current_Body.Route?.FND?.CityDetails?.City || current_Body.Route?.FND?.CityDetails?.State || current_Body.Route?.FND?.CityDetails?.Country) {
							'Group4_LOC' {
								'LOC' {
									'E3227_01' '7'
									'C517_02' {
										'E3225_01' util.substring((unLocationCodeMap.get(current_Body.Route?.FND?.CityDetails?.LocationCode?.MutuallyDefinedCode) ? unLocationCodeMap.get(current_Body.Route?.FND?.CityDetails?.LocationCode?.MutuallyDefinedCode) : current_Body.Route?.FND?.CityDetails?.LocationCode?.MutuallyDefinedCode) , 1, 25)	//SHAEDI235
										'E1131_02' '145'
										'E3055_03' '6'
										'E3224_04' current_Body.Route?.FND?.CityDetails?.City?.trim()
									}
									'C519_03' {
										'E3223_01' ''
										'E1131_02' '163'
										'E3055_03' ''
										'E3222_04' current_Body.Route?.FND?.CityDetails?.State?.trim()
									}
									'C553_04' {
										'E3233_01' ''
										'E1131_02' '162'
										'E3055_03' ''
										'E3232_04' current_Body.Route?.FND?.CityDetails?.Country?.trim()
									}
								}
								
								def defPorDt = current_Body.Route?.ReqDeliveryDT?.LocDT?.LocDT
								if (defPorDt) {
									'DTM' {
										'C507_01' {
											'E2005_01' '17'
											'E2380_02' util.convertXmlDateTime(defPorDt, yyyyMMddHHmm)
											'E2379_03' '203'
										}
									}
								}
							}
						//}
					}
					//end of FND
				}
			}
			//end Group_3
		
			//Group_6			
			//CAR - CA
			'Group6_NAD' {
				'NAD' {
					'E3035_01' 'CA'
					if (current_Body.GeneralInformation?.SCACCode) {
						'C082_02' {
							'E3039_01' current_Body.GeneralInformation?.SCACCode
							'E1131_02' '160'
							'E3055_03' ''
						}
					}
					'C080_04' {
						'E3036_01' ''
					}
					'C059_05' {
						'E3042_01' ''
					}
				}
			}
			
			
			Map partyTypeMap = ['BPT':'BK', 'SHP':'CZ', 'CGN':'CN', 'FWD':'FW']
			
			List<Party> parties = new ArrayList<Party>()
			parties.addAll(current_Body.Party.findAll(){it?.PartyType == 'BPT'})
			parties.addAll(current_Body.Party.findAll(){it?.PartyType == 'SHP'})
			parties.addAll(current_Body.Party.findAll(){it?.PartyType == 'CGN'})
			parties.addAll(current_Body.Party.findAll(){it?.PartyType == 'FWD'})
			
			parties.each { current_Party ->
				def partyType = partyTypeMap.get(current_Party.PartyType)
				'Group6_NAD' {
					'NAD' {
						'E3035_01' partyType
						if (current_Party.CarrierCustomerCode) {
							'C082_02' {
								'E3039_01' current_Party.CarrierCustomerCode
								'E1131_02' '160'
								'E3055_03' ''
							}
						}
						
						if (current_Party.PartyName) {
							'C080_04' {
								'E3036_01' util.substring(current_Party.PartyName, 1, 35)?.trim()
								if (util.substring(current_Party.PartyName, 36, 35)) {
									'E3036_02' util.substring(current_Party.PartyName, 36, 35)?.trim()
								}
								'E3036_03' ''
								'E3036_04' ''
								'E3036_05' ''
								'E3045_06' ''
							}
						}
						
						String addressLine = current_Party.Address?.AddressLines?.AddressLine?.findAll{util.isNotEmpty(it)}?.join(" ")
						if (addressLine) {
							'C059_05' {
								'E3042_01' util.substring(addressLine, 1, 35)
								if (addressLine.length()>35) {
									'E3042_02' util.substring(addressLine, 36, 35)
								}
								if (addressLine.length()>70) {
									'E3042_03' util.substring(addressLine, 71, 35)
								}
								'E3042_04' ''
							}
						}
						if (current_Party?.Address?.City) {
							'E3164_06' util.substring(current_Party?.Address?.City, 1, 35)
						}
						if (current_Party?.Address?.State && current_Party?.Address?.State?.length()>2 && current_Party?.Address?.City) {
							'E3229_07' util.substring(current_Party?.Address?.State, 1, 9)
						}
						def postalCode = current_Party?.Address?.PostalCode
						if (postalCode && postalCode.replace("-", "").length()>0) {
							'E3251_08' util.substring(postalCode.replace("-", ""), 1, 9)
						}
						if (current_Party?.Address?.Country) {
							if (current_Party?.Address?.Country?.length()<=2) {
								'E3207_09' current_Party?.Address?.Country?.trim()
							} else {
								'E3207_09' util.getCS2Country4CountryCodeByCountryName(current_Party?.Address?.Country, conn)
							}
						}
					}
					if (current_Party.Contact?.FirstName || current_Party.Contact?.LastName) {
						'Group7_CTA' {
							'CTA' {
								'E3139_01' partyType
								'C056_02' {
									def name = current_Party.Contact?.FirstName==null?'':current_Party.Contact?.FirstName.trim()
									if (current_Party.Contact?.LastName) {
										name = name + ' ' + current_Party.Contact?.LastName
										name = name.trim()
									}
									'E3413_01' ''
									'E3412_02' name
								}
							}
							if (current_Party.Contact?.ContactPhone?.Number) {
								'COM' {
									'C076_01' {
										'E3148_01' current_Party.Contact?.ContactPhone?.Number.trim()
										'E3155_02' 'TE'
									}
								}
							}
							if (current_Party.Contact?.ContactEmailAddress) {
								'COM' {
									'C076_01' {
										'E3148_01' current_Party.Contact?.ContactEmailAddress.trim()
										'E3155_02' 'EM'
									}
								}
							}
						}
					}
				}
			}
			//end of Group_6 Party
			
			//Group_9 
			current_Body.Cargo?.eachWithIndex { cargo, currentIndex ->
				'Group9_GID' {
					'GID' {
						'E1496_01' (currentIndex+1)
						'C213_02' {
							if (cargo.Packaging?.PackageQty) {
								'E7224_01' cargo.Packaging?.PackageQty
							} else {
								'E7224_01' '0'
							}
							if (cargo.Packaging?.PackageType) {
								'E7065_02' cargo.Packaging?.PackageType?.toUpperCase()
							}
							'E1131_03' ''
							'E3055_04' '5'
							'E7064_05' ''
						}
					}
					List<ReeferCargoSpec> allRefSpecs = cargo.ReeferCargoSpec?.findAll() {it.Temperature?.Temperature && it.Temperature?.TemperatureUnit}
					ReeferCargoSpec refSpec = null
					if (allRefSpecs && allRefSpecs.size()>0) {
						refSpec = allRefSpecs[-1]
						'TMP' {
							'E6245_01' '1'
							'C239_02' {
								'E6246_01' getReeferTemp(refSpec.Temperature?.Temperature)
								if (refSpec.Temperature?.TemperatureUnit?.toUpperCase() == 'C') {
									'E6411_02' 'CEL'
								} else if (refSpec.Temperature?.TemperatureUnit?.toUpperCase() == 'F') {
									'E6411_02' 'FAH'
								}
							}
						}
					}
					if (cargo.CargoDescription) {
						def desc = cargo.CargoDescription?.replace("\n", " ")
						desc = desc.replace("\r", " ")
						'FTX' {
							'E4451_01' 'AAA'
							'E4453_02' ''
							'C108_04' {
								'E4440_01' util.substring(desc, 1, 70)
								if (util.substring(desc, 71, 70)) {
									'E4440_02' util.substring(desc, 71, 70)
								}
								if (util.substring(desc, 141, 70)) {
									'E4440_03' util.substring(desc, 141, 70)
								}
								if (util.substring(desc, 211, 70)) {
									'E4440_04' util.substring(desc, 211, 70)
								}
								if (util.substring(desc, 281, 70)) {
									'E4440_05' util.substring(desc, 281, 70)
								}
							}
						}
					}
					
					cargo.ReeferCargoSpec?.each { current_ReeferCargoSpec ->
						if (current_ReeferCargoSpec.GensetType) {
							'FTX' {
								'E4451_01' 'GES'
								'C108_04' {
									'E4440_01' current_ReeferCargoSpec.GensetType?.trim()
								}
							}
						}
						if (current_ReeferCargoSpec.DehumidityPercentage) {
							'FTX' {
								'E4451_01' 'DEH'
								'C108_04' {
									'E4440_01' bcUtil.addLeading0(current_ReeferCargoSpec.DehumidityPercentage, 3)
								}
							}
						}
						if (current_ReeferCargoSpec.O2) {
							'FTX' {
								'E4451_01' 'O2'
								'C108_04' {
									'E4440_01' current_ReeferCargoSpec.O2?.trim()
								}
							}
						}
						if (current_ReeferCargoSpec.CO2) {
							'FTX' {
								'E4451_01' 'CO2'
								'C108_04' {
									'E4440_01' current_ReeferCargoSpec.CO2?.trim()
								}
							}
						}
						if (current_ReeferCargoSpec.Ventilation?.VentilationUnit) {
							'FTX' {
								'E4451_01' 'VET'
								'C108_04' {
									def vunit = current_ReeferCargoSpec.Ventilation?.VentilationUnit
									if (vunit==null) {
										vunit = ''
									}
									'E4440_01' bcUtil.addLeading0(current_ReeferCargoSpec.Ventilation?.Ventilation, 3) + ' ' + vunit.trim()
								}
							}
						}
					}
					
					if (cargo.CargoNature) {
						'GDS' {
							'C703_01' {
								def defNature = ''
								switch (cargo.CargoNature) {
									case 'AW': 
										defNature = '10'
										break
									case 'DG': 
										defNature = '11'
										break
									case 'GC': 
										defNature = '12'
										break
									case 'RF': 
										defNature = '14'
										break
									case 'RD': 
										defNature = 'ZZ2'
										break
									case 'AD': 
										defNature = 'ZZ1'
										break
									default: 
										defNature = '12'
										break
								}
								'E7085_01' defNature
							}
						}
					}
					
					if (cargo.GrossWeight?.Weight && cargo.GrossWeight?.WeightUnit) {
						'Group11_MEA' {
							'MEA' {
								'E6311_01' 'WT'
								'C502_02' {
									'E6313_01' 'G'
								}
								'C174_03' {
									if (cargo.GrossWeight?.WeightUnit?.toUpperCase() == 'KGS') {
										'E6411_01' 'KGM'
									} else {
										'E6411_01' cargo.GrossWeight?.WeightUnit
									}
									def defVal = (new DecimalFormat(".000")).format(Double.parseDouble(cargo.GrossWeight?.Weight))
									'E6314_02' defVal
								}
							}
						}
					}
					if (cargo.NetWeight?.Weight && cargo.NetWeight?.WeightUnit) {
						'Group11_MEA' {
							'MEA' {
								'E6311_01' 'WT'
								'C502_02' {
									'E6313_01' 'N'
								}
								'C174_03' {
									if (cargo.NetWeight?.WeightUnit?.toUpperCase() == 'KGS') {
										'E6411_01' 'KGM'
									} else {
										'E6411_01' cargo.NetWeight?.WeightUnit
									}
									def defVal = (new DecimalFormat(".000")).format(Double.parseDouble(cargo.NetWeight?.Weight))
									'E6314_02' defVal
								}
							}
						}
					}
					if (cargo.Volume?.Volume && cargo.Volume?.VolumeUnit) {
						'Group11_MEA' {
							'MEA' {
								'E6311_01' 'VOL'
								'C502_02' {
									'E6313_01' 'AAW'
								}
								'C174_03' {
									DecimalFormat df = new DecimalFormat(".000")
									if (cargo.Volume?.VolumeUnit?.toUpperCase() == 'CFT') {
										'E6411_01' 'CBM'
										def defVal = df.format(Double.parseDouble(cargo.Volume?.Volume) * 0.0283168466)
										'E6314_02' defVal
									} else {
										'E6411_01' cargo.Volume?.VolumeUnit
										'E6314_02' df.format(Double.parseDouble(cargo.Volume?.Volume))
									}
								}
							}
						}
					}
					
					cargo.AWCargoSpec.each { awCargoSpec ->
						'Group12_DIM' {
							'DIM' {
								'E6145_01' '2'
								'C211_02' {
									if (awCargoSpec.Height?.LengthUnit=='F' && awCargoSpec.Length?.LengthUnit=='F' && awCargoSpec.Width?.LengthUnit=='F') {
										'E6411_01' 'FT'
									} else {
										'E6411_01' 'MT'
									}
									DecimalFormat df = new DecimalFormat("0.000")
									if (awCargoSpec.Length?.Length) {
										if (awCargoSpec.Length?.LengthUnit=='F' && (awCargoSpec.Height?.LengthUnit!='F' || awCargoSpec.Width?.LengthUnit!='F')) {
											'E6168_02' df.format(Double.parseDouble(awCargoSpec.Length?.Length) * 0.3048)
										} else if (awCargoSpec.Length?.Length?.indexOf('.')>=0) {
											'E6168_02' Double.parseDouble(awCargoSpec.Length?.Length)
										} else {
											'E6168_02' awCargoSpec.Length?.Length
										}
									}
									if (awCargoSpec.Width?.Length) {
										if (awCargoSpec.Width?.LengthUnit=='F' && (awCargoSpec.Length?.LengthUnit!='F' || awCargoSpec.Height?.LengthUnit!='F')) {
											'E6140_03' df.format(Double.parseDouble(awCargoSpec.Width?.Length) * 0.3048)
										} else if (awCargoSpec.Width?.Length?.indexOf('.')>=0) {
											'E6140_03' Double.parseDouble(awCargoSpec.Width?.Length)
										} else {
											'E6140_03' awCargoSpec.Width?.Length
										}
									}
									if (awCargoSpec.Height?.Length) {
										if (awCargoSpec.Height?.LengthUnit=='F' && (awCargoSpec.Length?.LengthUnit!='F' || awCargoSpec.Width?.LengthUnit!='F')) {
											'E6008_04' df.format(Double.parseDouble(awCargoSpec.Height?.Length) * 0.3048)
										} else if (awCargoSpec.Height?.Length.indexOf('.')>=0) {
											'E6008_04' Double.parseDouble(awCargoSpec.Height?.Length)
										} else {
											'E6008_04' awCargoSpec.Height?.Length
										}
									}
								}
							}
						}
					}
				}
			}
			//end of Group_9
			
			
			//Group_18
			current_Body.ContainerGroup?.Container?.each { current_Cntr ->
				'Group18_EQD' {
					'EQD' {
						'E8053_01' 'CN'
						'C237_02' {
							'E8260_01' util.substring(bcUtil.concat(current_Cntr.ContainerNumber, current_Cntr.ContainerCheckDigit), 1, 17)
							'E1131_02' ''
							'E3055_03' ''
							'E3207_04' ''
						}
						'C224_03' {
							HashMap<String, String> defCntrSizeType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null, msgFmtId, 'CNTRSIZETYPEBC', current_Cntr.CarrCntrSizeType, conn)
							if (defCntrSizeType && defCntrSizeType.size()>0) {
								'E8155_01' defCntrSizeType.get("EXT_CDE")
							} else {
								'E8155_01' current_Cntr.CarrCntrSizeType
							}
							'E1131_02' ''
							'E3055_03' ''
							'E8154_04' ''
						}
						if (current_Cntr.IsSOC?.toLowerCase()=='true' || current_Cntr.IsSOC=='1') {
							'E8077_04' '1'
						} else if (current_Cntr.IsSOC?.toLowerCase()=='false' || current_Cntr.IsSOC=='0') {
							'E8077_04' '2'
						}
						'E8249_05' ''
						'E8169_06' '4'
					}
					'EQN' {
						'C523_01' {
							'E6350_01' '1'
							'E6353_02' ''
						}
					}
				}
			}
			//end of container loop
			
			//empty pickup loop
			current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup?.findAll(){it?.Facility?.FacilityName}?.each { current_EmptyPickup ->
				'Group18_EQD' {
					'EQD' {
						'E8053_01' 'CN'
						'C237_02' {
							'E8260_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E3207_04' ''
						}
						'C224_03' {
							HashMap<String, String> defCntrSizeType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null, msgFmtId, 'CNTRSIZETYPEBC', current_EmptyPickup.ISOSizeType, conn)
							if (defCntrSizeType && defCntrSizeType.size()>0) {
								'E8155_01' defCntrSizeType.get("EXT_CDE")
							} else {
								'E8155_01' current_EmptyPickup.ISOSizeType
							}
							'E1131_02' ''
							'E3055_03' ''
							'E8154_04' ''
						}
						'E8077_04' ''
						'E8249_05' ''
						'E8169_06' '4'
					}
					'EQN' {
						'C523_01' {
							'E6350_01' current_EmptyPickup.NumberOfContainers
							'E6353_02' ''
						}
					}
					
					//group 19
					'Group19_NAD' {
						'NAD' {
							'E3035_01' 'CK'
							if (current_EmptyPickup.Facility?.FacilityCode) {
								'C082_02' {
									'E3039_01' current_EmptyPickup.Facility?.FacilityCode.trim()
									'E1131_02' ''
									'E3055_03' ''
								}
							}
							if (current_EmptyPickup.Facility?.FacilityName) {
								'C080_04' {
									'E3036_01' util.substring(current_EmptyPickup.Facility?.FacilityName.trim(), 1, 35).trim()
									if (util.substring(current_EmptyPickup.Facility?.FacilityName.trim(), 36, 35)) {
										'E3036_02' util.substring(current_EmptyPickup.Facility?.FacilityName.trim(), 36, 35).trim()
									}
								}
							}
							if (current_EmptyPickup.Address?.AddressLines?.AddressLine && current_EmptyPickup.Address?.AddressLines?.AddressLine?.size()>0) {
								def addline = current_EmptyPickup.Address?.AddressLines?.AddressLine[0]
								if (current_EmptyPickup.Address?.AddressLines?.AddressLine.size()>1) {
									addline += ' ' + current_EmptyPickup.Address?.AddressLines?.AddressLine[1]
								}
								if (current_EmptyPickup.Address?.AddressLines?.AddressLine.size()>2) {
									addline += ' ' + current_EmptyPickup.Address?.AddressLines?.AddressLine[2]
								}
								'C059_05' {
									if (util.substring(addline, 1, 35)) {
										'E3042_01' util.substring(addline, 1, 35)
									}
									if (util.substring(addline, 36, 35)) {
										'E3042_02' util.substring(addline, 36, 35)
									}
									if (util.substring(addline, 71, 35)) {
										'E3042_03' util.substring(addline, 71, 35)
									}
								}
							}
						}
						if (current_EmptyPickup.MvmtDT?.LocDT?.LocDT) {
							'DTM' {
								'C507_01' {
									'E2005_01' '201'
									'E2380_02' util.convertXmlDateTime(current_EmptyPickup.MvmtDT?.LocDT?.LocDT, yyyyMMdd)
									'E2379_03' '102'
								}
							}
						}
					}
					//end of Group19_NAD
				}
				//end of group18_EQD
			}
			//end of empty pickup
			
			//full return loop
			current_Body.ContainerGroup?.ContainerFlowInstruction?.FullReturn?.findAll(){it?.Facility?.FacilityName}?.each { current_FullReturn ->
				'Group18_EQD' {
					'EQD' {
						'E8053_01' 'CN'
						'C237_02' {
							'E8260_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E3207_04' ''
						}
						'C224_03' {
							HashMap<String, String> defCntrSizeType = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null, msgFmtId, 'CNTRSIZETYPEBC', current_FullReturn.ISOSizeType, conn)
							if (defCntrSizeType && defCntrSizeType.size()>0) {
								'E8155_01' defCntrSizeType.get("EXT_CDE")
							} else {
								'E8155_01' current_FullReturn.ISOSizeType
							}
							'E1131_02' ''
							'E3055_03' ''
							'E8154_04' ''
						}
						'E8077_04' ''
						'E8249_05' ''
						'E8169_06' '4'
					}
					'EQN' {
						'C523_01' {
							'E6350_01' current_FullReturn.NumberOfContainers
							'E6353_02' ''
						}
					}
					
					//group 19
					'Group19_NAD' {
						'NAD' {
							'E3035_01' 'CU'
							if (current_FullReturn.Facility?.FacilityCode) {
								'C082_02' {
									'E3039_01' current_FullReturn.Facility?.FacilityCode.trim()
									'E1131_02' ''
									'E3055_03' ''
								}
							}
							if (current_FullReturn.Facility?.FacilityName) {
								'C080_04' {
									'E3036_01' util.substring(current_FullReturn.Facility?.FacilityName.trim(), 1, 35).trim()
									if (util.substring(current_FullReturn.Facility?.FacilityName.trim(), 36, 35)) {
										'E3036_02' util.substring(current_FullReturn.Facility?.FacilityName.trim(), 36, 35).trim()
									}
								}
							}
							if (current_FullReturn.Address?.AddressLines?.AddressLine && current_FullReturn.Address?.AddressLines?.AddressLine?.size()>0) {
								def addline = current_FullReturn.Address?.AddressLines?.AddressLine[0]
								if (current_FullReturn.Address?.AddressLines?.AddressLine.size()>1) {
									addline += ' ' + current_FullReturn.Address?.AddressLines?.AddressLine[1]
								}
								if (current_FullReturn.Address?.AddressLines?.AddressLine.size()>2) {
									addline += ' ' + current_FullReturn.Address?.AddressLines?.AddressLine[2]
								}
								'C059_05' {
									if (util.substring(addline, 1, 35)) {
										'E3042_01' util.substring(addline, 1, 35)
									}
									if (util.substring(addline, 36, 35)) {
										'E3042_02' util.substring(addline, 36, 35)
									}
									if (util.substring(addline, 71, 35)) {
										'E3042_03' util.substring(addline, 71, 35)
									}
								}
							}
						}
						if (current_FullReturn.MvmtDT?.LocDT?.LocDT) {
							'DTM' {
								'C507_01' {
									'E2005_01' '116'
									'E2380_02' util.convertXmlDateTime(current_FullReturn.MvmtDT?.LocDT?.LocDT, yyyyMMdd)
									'E2379_03' '102'
								}
							}
						}
					}
					//end of Group19_NAD
				}
				//end of group18_EQD
			}
			
			//end of txn
			'UNT' {
				'E0074_01' '-999'
				'E0062_02' '-999'
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
		def IFTMBC = outXml.createNode('IFTMBC')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.

		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()
		def headerMsgDT = util.convertDateTime(bc.Header?.MsgDT?.LocDT?.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		def txnErrorKeys = []

		//Start mapping

		bc?.Body?.eachWithIndex{current_Body, current_BodyIndex ->
			//prep checking
			List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
			// prep validation
			prepValidation(current_Body, current_BodyIndex, errorKeyList)
			
			if(current_Body?.GeneralInformation?.BookingStatus=='Declined'||current_Body?.GeneralInformation?.BookingStatus=='Rejected') {
				generateBody4RejectCase(current_Body, outXml)
			} else {
				//mapping
				generateBody(current_Body, outXml)
			}
			
			// posp checking
			if (errorKeyList.isEmpty()) {
				pospValidation(current_Body, writer?.toString(), errorKeyList)
			}

			bcUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			bcUtil.buildBizKey(bizKeyXml, bc.Header, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, TP_ID, conn)

			txnErrorKeys.add(errorKeyList)
		}

		//End root node
		outXml.nodeCompleted(null, IFTMBC)
		bizKeyXml.nodeCompleted(null, bizKeyRoot)
		csuploadXml.nodeCompleted(null, csuploadRoot)

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

	void prepValidation(Body current_Body, int bodyLoopIndex, List<Map<String,String>> errorKeyList) {
		
		//IsMissBkgNum
		bcUtil.checkCarrierBookingNumberForAllStatus(current_Body, true, 'Missing Shipment Booking Number.', errorKeyList)
		
		//IsMissSCACCode
		bcUtil.checkSCACCode(current_Body, true, 'Missing Carrier SCACCode.', errorKeyList)
		
		//CheckContainerSizeTypeAlertISA
		bcUtil.checkContainerSizeTypeAlertISA(TP_ID, MSG_TYPE_ID, DIR_ID, null, msgFmtId, 'CNTRSIZETYPEBC', conn, current_Body, true, null, errorKeyList)

		//SHAEDI235 SHAEDI235-1
		if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode == "CNSHA"){
			bcUtil.checkFirstOceanLegPODUnlocation(current_Body, true, null, errorKeyList);
		}
	}
	
	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList) {
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</IFTMBC>")

		//CheckGRP2
		bcUtil.checkGRP2(current_Body, node, true, null, errorKeyList)
		
		bcUtil.checkGRP2_RFF_C506_1153_1154(current_Body, node, true, null, errorKeyList)

	}
	
	void generateBody4RejectCase(Body current_Body, MarkupBuilder outXml) {
		//rejected bc should be extracted to a method
		
		def currentBcScac = current_Body.GeneralInformation?.SCACCode
		def currentBcBookingStatus = current_Body.GeneralInformation?.BookingStatus
		def currentBcRemarks = current_Body.GeneralInformation?.BookingStatusRemarks
		String ooluBkgNum = ''
		if (currentBcRemarks?.indexOf('|')>0) {
			String[] splits = currentBcRemarks.split('\\|')
			if (splits.length>=2) {
				ooluBkgNum = splits[1]
			}
		}
		
		outXml.'Group_UNH' {
			'UNH' {
				'E0062_01' '-999'
				'S009_02' {
					'E0065_01' 'IFTMBC'
					'E0052_02' 'D'
					'E0054_03' '96B'
					'E0051_04' 'UN'
					'E0057_05' '1.3'
					'E0110_06' ''
					'E0113_07' ''
				}
				'E0068_03' ''
				'S010_04' {
					'E0070_01' ''
					'E0073_02' ''
				}
			}
			
			'BGM' {
				'C002_01' {
					'E1001_01' '770'
					'E1131_02' ''
					'E3055_03' ''
					'E1000_04' ''
				}
				'C106_02' {
					'E1004_01' ''
					'E1056_02' ''
					'E1060_03' ''
				}
				'E1225_03' '5'
				'E4343_04' 'RE'
			}
			
			'CTA' {
				'E3139_01' 'IC'
				'C056_02' {
					'E3413_01' ''
					'E3412_02' 'HKGCSU'
				}
			}
			
			'COM' {
				'C076_01' {
					'E3148_01' '25066666'
					'E3155_02' 'TE'
				}
			}
			
			if (currentBcScac=='OOLU') {
				if (ooluBkgNum) {
					def vbkgnum = ''
					if (ooluBkgNum.indexOf('+')>=0) {
						vbkgnum = ooluBkgNum.substring(ooluBkgNum.indexOf('+')+1)
						if (util.isEmpty(vbkgnum)) {
							vbkgnum = ooluBkgNum.substring(0, ooluBkgNum.indexOf('+')).trim()
						}
					}
					if (util.isEmpty(vbkgnum)) {
						vbkgnum = ooluBkgNum.trim()
					}
					if (vbkgnum) {
						'FTX' {
							'E4451_01' 'AAI'
							'C108_04' {
								'E4440_01' util.substring(vbkgnum, 1, 70)
							}
						}
					}
				}
			} else {
				if (util.substring(currentBcRemarks, 1, 70)) {
					'FTX' {
						'E4451_01' 'AAI'
						'C108_04' {
							'E4440_01' util.substring(currentBcRemarks, 1, 70)
						}
					}
				}
			}
			
			'Group1_LOC' {
				'LOC' {
					'E3227_01' '90'
				}
			}
			
			'Group2_RFF' {
				'RFF' {
					'C506_01' {
						'E1153_01' 'BN'
						if (currentBcScac=='OOLU' && current_Body.GeneralInformation?.CarrierBookingNumber) {
							'E1154_02' current_Body.GeneralInformation?.CarrierBookingNumber
						} else {
							'E1154_02' current_Body.GeneralInformation?.CSBookingRefNumber
						}
					}
				}
			}
			
			if (currentBcScac=='OOLU' && currentBcRemarks) {
				'Group2_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' 'FF'
							def defRemark = ''
							if (currentBcRemarks.indexOf('|')>0) {
								defRemark = currentBcRemarks.substring(0, currentBcRemarks.indexOf('|'))?.trim()
							}
							if (util.isEmpty(defRemark)) {
								defRemark = currentBcRemarks
							}
							'E1154_02' defRemark
						}
					}
				}
			}
			
			if (currentBcScac!='OOLU') {
				def bizkeyVal = bcUtil.bizkeyCSBkRefNumQuery('FR', current_Body.GeneralInformation?.CSBookingRefNumber, conn)
				if (bizkeyVal) {
					'Group2_RFF' {
						'RFF' {
							'C506_01' {
								'E1153_01' 'FF'
								'E1154_02' bizkeyVal
							}
						}
					}
				}
			}
			
			//Group_6
			//CAR - CA
			'Group6_NAD' {
				'NAD' {
					'E3035_01' 'CA'
					if (currentBcScac) {
						'C082_02' {
							'E3039_01' currentBcScac
							'E1131_02' '160'
							'E3055_03' 'ZZZ'
						}
						'C080_04' {
							if (currentBcScac=='OOLU') {
								'E3036_01' 'Orient Overseas Container Line Ltd'
							} else {
								Map<String, String> scacInfo = util.getOceanCarrierInfo(currentBcScac, conn)
								if (scacInfo && scacInfo.get('NAME')) {
									'E3036_01' scacInfo.get('NAME')
								} else {
									'E3036_01' currentBcScac
								}
							}
						}
					}
				}
			}
			
			//end of txn
			'UNT' {
				'E0074_01' '-999'
				'E0062_02' '-999'
			}
		}
	}
	
	
	//copy from tibco
	private String getReeferTemp(String numberString) {
		boolean isPositive = true;
		
		//If input string is null or empty, return empty
		NumberFormat formatter = new DecimalFormat("00.00");
		BigDecimal bd = null;
		String resultStr = "";
						  
		if(numberString == null || numberString.trim().length()==0){
			return "";
		}
		//Check positive or negative
		if(numberString.trim().startsWith("-")){
			isPositive = false;
		}
					
		//Case positve
		if (isPositive) {
			bd = new BigDecimal(numberString);
			double value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
						
			//Case 00.0 -> 000
			if (value == 0d) {
				resultStr = "000";
			} else if( value >= 100){
				//Case value > 100 such as 00168.65 -> 169
				formatter = new DecimalFormat("000");
				resultStr = formatter.format(value);
			} else if( value >= 10){
				//Case value >= 10 and < 100 , +0018.50 -> 18.5
				formatter = new DecimalFormat("00.0");
				resultStr = formatter.format(value);
				//if it is integer 18.0
				if (resultStr.endsWith(".0")) {
					formatter = new DecimalFormat("000");
					resultStr = formatter.format(new Double(resultStr));
				}
			} else {
				bd = new BigDecimal(numberString);
				value = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				formatter = new DecimalFormat("0.00");
				resultStr = formatter.format(new Double(value));
				//if it is integer +0000.65 -> 0.65
				if (resultStr.endsWith(".00")) {
					formatter = new DecimalFormat("000");
					resultStr = formatter.format(new Double(resultStr));
				} else if(resultStr.endsWith("0")) {
					formatter = new DecimalFormat("00.0");
					resultStr = formatter.format(new Double(resultStr));
				}
			}
		} else {
			bd = new BigDecimal(numberString);
			double value = bd.doubleValue();
						
			//Case -0000.00 -> -000
			if (value == 0d) {
				resultStr = "-000";
			} else if( value <= -100) {
				//Case value <= -100 such as -0189.56 -> -190
				formatter = new DecimalFormat("000");
				resultStr = formatter.format(new Double(value));
			} else if( value <= -10) {
				//Case value >= -10 and < -100 , -0018.50 -> -18.5
				value = bd.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
				formatter = new DecimalFormat("00.0");
				resultStr = formatter.format(new Double(value));
				//if it is integer 18.0
				if (resultStr.endsWith(".0")) {
					formatter = new DecimalFormat("000");
					resultStr = formatter.format(new Double(resultStr));
				}
			} else {
				formatter = new DecimalFormat("0.00");
				resultStr = formatter.format(new Double(value));
				//if it is integer -0009.70 -> -09.7
				if (resultStr.endsWith(".00")) {
					formatter = new DecimalFormat("000");
					resultStr = formatter.format(new Double(resultStr));
				} else if (resultStr.endsWith("0")) {
					formatter = new DecimalFormat("00.0");
					resultStr = formatter.format(new Double(resultStr));
				}
			}
		}
		return resultStr;
	}
}
