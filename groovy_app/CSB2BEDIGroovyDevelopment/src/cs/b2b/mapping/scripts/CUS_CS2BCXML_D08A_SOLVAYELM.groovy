package cs.b2b.mapping.scripts

import cs.b2b.core.mapping.bean.bc.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.text.DecimalFormat

public class CUS_CS2BCXML_D08A_SOLVAYELM {

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
	def HHmmss = 'HHmmss'
	
	def currentSystemDt = null
	DecimalFormat decimalFormatter = new DecimalFormat("#.####")
	DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")

	List<String> bcRejectStatus = ['Declined']
	
	def msgFmtId = "EDIFACT"
	def convertTypeID_VENDORCODE = "VENDORCODE"
	
	public void generateBody(Body current_Body, MarkupBuilder outXml) {
		
		boolean isRejectBC = bcRejectStatus.contains(current_Body.GeneralInformation?.BookingStatus)
		
		def currentBcScac = current_Body.GeneralInformation?.SCACCode
		
		def bcReference = ''
		def bcRejectRef = ''
		if (isRejectBC && current_Body.GeneralInformation?.BookingStatusRemarks) {
			int charPos = current_Body.GeneralInformation?.BookingStatusRemarks.indexOf("|")
			if (charPos > 0) {
				bcReference = current_Body.GeneralInformation?.BookingStatusRemarks.substring(0, charPos).trim()
				bcRejectRef = bcRejectRef
			}
			if (util.isEmpty(bcRejectRef)) {
				bcRejectRef = current_Body.GeneralInformation?.BookingStatusRemarks.trim()
			}
			if (bcRejectRef && bcRejectRef.indexOf("|")>0) {
				bcRejectRef = bcRejectRef.substring(0, bcRejectRef.indexOf("|"))
			}
		}
		if (util.isEmpty(bcReference)) {
			ExternalReference defRef = current_Body.ExternalReference.find { it.ReferenceDescription && it.ReferenceDescription.toUpperCase() == 'SHIPPER REFERENCE'}
			if (defRef==null) {
				defRef = current_Body.ExternalReference.find { it.ReferenceDescription && it.ReferenceDescription.toUpperCase() == 'BOOKING NUMBER'}
			}
			if (defRef && defRef.ReferenceNumber) {
				bcReference = defRef.ReferenceNumber.trim()
			}
		}
		
		outXml.'Group_UNH' {
			'UNH' {
				'E0062_01' '-999'
				'S009_02' {
					'E0065_01' 'IFTMBC'
					'E0052_02' 'D'
					'E0054_03' '08A'
					'E0051_04' 'UN'
					'E0057_05' ''
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
					if (bcReference) {
						'E1004_01' util.substring(bcReference, 1, 35)
					}
					'E1056_02' ''
					'E1060_03' ''
				} 
				'E1225_03' '9'
				if (isRejectBC) {
					'E4343_04' 'RE'
				} else {
					'E4343_04' 'AP'
				}
			}
			
			def vEventDt = '';
			if (current_Body.EventInformation?.EventDT?.LocDT?.LocDT) {
				vEventDt = current_Body.EventInformation?.EventDT?.LocDT?.LocDT
			} else {
				vEventDt = current_Body.EventInformation?.EventDT?.GMT;
			}
			if (vEventDt) {
				//the 1st DTM
				'DTM' {
					'C507_01' {
						'E2005_01' '137'
						'E2380_02' util.convertXmlDateTime(vEventDt, yyyyMMdd);
						'E2379_03' '203'
					}
				}
				//the 2nd DTM
				'DTM' {
					'C507_01' {
						'E2005_01' '137'
						'E2380_02' util.convertXmlDateTime(vEventDt, HHmmss);
						'E2379_03' '406'
					}
				}
				//the 3rd DTM
				'DTM' {
					'C507_01' {
						'E2005_01' '735'
						'E2380_02' util.convertXmlDateTime(vEventDt, yyyyMMdd);
						'E2379_03' '203'
					}
				}
				//the 4th DTM
				'DTM' {
					'C507_01' {
						'E2005_01' '735'
						'E2380_02' util.convertXmlDateTime(vEventDt, HHmmss);
						'E2379_03' '406'
					}
				}
			}
			
			//Group_2
			current_Body.GeneralInformation?.BLNumber?.each {current_BLNumber ->
				'Group2_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' 'BM'
							'E1154_02' util.substring(current_BLNumber?.trim(), 1, 35)
							'E1156_03' ''
							'E1056_04' ''
							'E1060_05' ''
						}
					}
					'DTM' {
						'C507_01' {
							'E2005_01' ''
							'E2380_02' ''
							'E2379_03' ''
						}
					}
				}
			}

			'Group2_RFF' {
				'RFF' {
					'C506_01' {
						'E1153_01' 'BN'
						'E1154_02' util.substring(current_Body.GeneralInformation?.CarrierBookingNumber?.trim(), 1, 35)
						'E1156_03' ''
						'E1056_04' ''
						'E1060_05' ''
					}
				}
				'DTM' {
					'C507_01' {
						'E2005_01' ''
						'E2380_02' ''
						'E2379_03' ''
					}
				}
			}
			Map<String, String> refTypeMap = ['EX':'EX','FR':'FF','PO':'ON','SC':'AGE','CGO':'ANT','SR':'SRN']
			if (isRejectBC && bcRejectRef) {
				'Group2_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' 'SI'
							'E1154_02' util.substring(bcRejectRef, 1, 35)?.trim()
						}
					}
				}
			} else {
				current_Body.ExternalReference?.each { current_Reference ->
					'Group2_RFF' {
						'RFF' {
							'C506_01' {
								'E1153_01' refTypeMap.get(current_Reference?.CSReferenceType)
								'E1154_02' util.substring(current_Reference?.ReferenceNumber, 1, 35)?.trim()
							}
						}
					}
				}
			}
			
			boolean isVendorCCCFound = false
			List<Party> defBPT_CCC = current_Body.Party?.findAll{it.PartyType == "BPT" && it.CarrierCustomerCode}
			defBPT_CCC?.each { current_BPT ->
				String extCde = util.getEDICdeReffromIntCde(TP_ID, 'VENDOR-SHIPMENT', 'O', 'BCCS2X', 'VENDOR_SAP', 'SAP_ID', current_BPT.CarrierCustomerCode, conn)
				if (extCde) {
					isVendorCCCFound = true
					return
				}
			}
			
			//Group_3 Route
			if (current_Body.route) {
				'Group3_TDT' {
					'TDT' {
						'E8051_01' '20'
						'E8028_02' ''
						'C220_03' {
							'E8067_01' ''
							'E8066_02' ''
						}
						'C001_04' {
							'E8179_01' '13'
							'E1131_02' ''
							'E3055_03' ''
							'E8178_04' ''
						}
						if (current_Body.Route.OceanLeg) {
							OceanLeg firstLeg = current_Body.Route.OceanLeg.first()
							if (firstLeg && firstLeg.SVVD?.Loading?.LloydsNumber || firstLeg.SVVD?.Loading?.VesselName) {
								'C222_08' {
									'E8213_01' util.substring(firstLeg.SVVD?.Loading?.LloydsNumber, 1, 20)?.trim()
									'E1131_02' ''
									'E3055_03' ''
									'E8212_04' util.substring(firstLeg.SVVD?.Loading?.VesselName, 1, 35)?.trim()
									'E8453_05' ''
								}
							}
						}
						'E8281_09' ''
					}
					
					//the 1st DTM - Format-dateTime("yyyyMMdd",Body/Route/FirstPOL/DepartureDT/LocDT)
					if (current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT) {
						'DTM' {
							'C507_01' {
								'E2005_01' '133'
								'E2380_02' util.convertXmlDateTime(current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT, yyyyMMdd)
								'E2379_03' '102'
							}
						}
					}
					//the 2nd DTM - Format-dateTime("yyyyMMdd",Body/Route/LastPOD/ArrivalDT/LocDT)
					if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT) {
						'DTM' {
							'C507_01' {
								'E2005_01' '132'
								'E2380_02' util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT.LocDT, yyyyMMdd)
								'E2379_03' '102'
							}
						}
					}
					//the 3rd DTM - Format-dateTime("yyyyMMdd",Body/Route/FullReturnCutoff/LocDT)
					if (current_Body.Route?.FullReturnCutoff?.LocDT?.LocDT) {
						'DTM' {
							'C507_01' {
								'E2005_01' '180'
								'E2380_02' util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT.LocDT, yyyyMMdd)
								'E2379_03' '102'
							}
						}
					}
					
					def defListReturnDT = []
					if (isVendorCCCFound) {
						current_Body.ContainerGroup?.ContainerFlowInstruction?.FullReturn?.each { current_FullReturn ->
							def facilityCode = current_FullReturn.Facility?.FacilityCode
							if (current_FullReturn.MvmtDT?.LocDT?.LocDT && facilityCode && currentBcScac) {
								Map<String, String> queryResult = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, currentBcScac, msgFmtId, convertTypeID_VENDORCODE, facilityCode, conn)
								//if it has value and remarks is RAIL RAMP
								if (queryResult.get("REMARKS")?.toUpperCase()=="RAIL RAMP") {
									def convertDt = util.convertXmlDateTime(current_FullReturn.MvmtDT?.LocDT?.LocDT, yyyyMMdd)
									if (! defListReturnDT.contains(convertDt)) {
										defListReturnDT.add(convertDt)
									}
								}
							}
						}
					}
					
//					defListReturnDT.each { current_ReturnDT ->
//						'DTM' {
//							'C507_01' {
//								'E2005_01' '199'
//								'E2380_02' current_ReturnDT
//								'E2379_03' '102'
//							}
//						}
//					}
//					defListReturnDT.clear()
					
					//TSR - if trim(Body/Route/Haulage/OutBound) != "" and trim(Body/Route/Haulage/InBound) !=""
					if (current_Body.Route?.Haulage?.OutBound && current_Body.Route?.Haulage?.InBound) {
						def inbound = current_Body.Route.Haulage.InBound.trim()
						def outbound = current_Body.Route.Haulage.OutBound.trim()
						'TSR' {
							'C536_01' {
								if (outbound == "C" && inbound == "C") {
									'E4065_01' '27'
								} else if (outbound == "C" && inbound == "M") {
									'E4065_01' '28'
								} else if (outbound == "M" && inbound == "C") {
									'E4065_01' '29'
								} else if (outbound == "M" && inbound == "M") {
									'E4065_01' '30'
								}
								'E1131_02' ''
								'E3055_03' ''
							}
						}
					}
					
					// Group_3/Group_4
					if (current_Body.Route?.FirstPOL?.Port?.City || current_Body.Route?.FirstPOL?.Port?.State || current_Body.Route?.FirstPOL?.Port?.Country) {
						//loopType="POL"
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '9'
								'C517_02' {
									//FirstPOL/Port/LocationCode/UnLocationCod or FirstPOL/Port/CSPortID
									def portVal = current_Body.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode
									if (! portVal) {
										portVal = current_Body.Route?.FirstPOL?.Port?.CSPortID
									}
									if (portVal) {
										'E3225_01' util.substring(portVal, 1, 25)
									}
									'E1131_02' ''
									'E3055_03' ''
									if (current_Body.Route?.FirstPOL?.Port?.PortName) {
										'E3224_04' util.substring(current_Body.Route.FirstPOL.Port.PortName.trim(), 1, 256)
									}
								}
							}
						}
					}
					if (current_Body.Route?.LastPOD?.Port?.City || current_Body.Route?.LastPOD?.Port?.State || current_Body.Route?.LastPOD?.Port?.Country) {
						//loopType="POL"
						'Group4_LOC' {
							'LOC' {
								'E3227_01' '12'
								'C517_02' {
									//LastPOD/Port/LocationCode/UnLocationCod or LastPOD/Port/CSPortID
									def portVal = current_Body.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode
									if (! portVal) {
										portVal = current_Body.Route?.LastPOD?.Port?.CSPortID
									}
									if (portVal) {
										'E3225_01' util.substring(portVal, 1, 25)
									}
									'E1131_02' ''
									'E3055_03' ''
									if (current_Body.Route?.LastPOD?.Port?.PortName) {
										'E3224_04' util.substring(current_Body.Route.LastPOD.Port.PortName.trim(), 1, 256)
									}
								}
							}
						}
					}
					
//					current_Body.Route?.OceanLeg?.each { current_OceanLeg ->
//						'Group5_RFF' {
//							'RFF' {
//								'C506_01' {
//									'E1153_01' 'VON'
//									//concat(substring(trim(SegmentLoop/OceanLeg/SVVD/Loading/Voyage), 1, 4), SegmentLoop/OceanLeg/SVVD/Loading/Direction)
//									if (current_OceanLeg.SVVD?.Loading?.Voyage) {
//										def direction = (current_OceanLeg.SVVD?.Loading?.Direction==null?"":current_OceanLeg.SVVD?.Loading?.Direction.trim())
//										'E1154_02' util.substring(current_OceanLeg.SVVD?.Loading?.Voyage, 1, 4) + direction
//									}
//									'E1156_03' ''
//									'E1056_04' ''
//									'E1060_05' ''
//								}
//							}
//						}
//					}
					//end looping OceanLeg
				}
			}
			//end Group_3
			
			// Group_6
			// RuleName: LoopPartyFromBookingOffice
			//SELECT EXT_CDE FROM B2B_CDE_CONVERSION WHERE CONVERT_TYPE_ID = 'BookingOffice' AND TP_ID = <current TP_ID> AND MSG_FMT_ID = 'EDIFACT' AND UPPER(INT_CDE)=upper(Body/GeneralInformation/BookingOffice)
			//String TP_ID, String MSG_TYPE_ID, String DIR_ID, String scacCode, String MSG_FMT_ID, String convertTypeId, String fromValue
			def bkgOffice = current_Body.GeneralInformation?.BookingOffice
			if (bkgOffice) {
				Map<String, String> bkgOfficeMap = util.getCdeConversionFromIntCde(TP_ID, null, null, null, 'EDIFACT', 'BookingOffice', "UPPER-CASE:"+bkgOffice.toUpperCase(), conn)
				def bkgOfficeVal = bkgOfficeMap.get("EXT_CDE")
				if (bkgOfficeVal && bkgOfficeVal.indexOf("`")>0) {
					def baCCC = bkgOfficeVal.substring(0, bkgOfficeVal.indexOf("`"))
					def baName = bkgOfficeVal.substring(bkgOfficeVal.indexOf("`")+1)
					baName = baName.replace("`", "")
					'Group6_NAD' {
						'NAD' {
							'E3035_01' 'BA'
							'C082_02' {
								'E3039_01' baCCC
							}
							if (baName) {
								'C080_04' {
									'E3036_01' util.substring(baName, 1, 35).trim()
									if (util.substring(baName, 36, 35)) {
										'E3036_02' util.substring(baName, 36, 35).trim()
									}
									'E3036_03' ''
									'E3036_04' ''
									'E3036_05' ''
									'E3045_06' ''
								}
							}
						}
					}
				}
			}
			
			Map partyTypeMap = ['SHP':'CZ', 'FWD':'FW']
			List<Party> partyShpFwds = current_Body.Party.findAll(){it.PartyType == 'SHP'}
			List<Party> partyFwds = current_Body.Party.findAll(){it.PartyType == 'FWD'}
			partyShpFwds.addAll(partyFwds)
			
			partyShpFwds.each { current_Party ->
				'Group6_NAD' {
					'NAD' {
						'E3035_01' partyTypeMap.get(current_Party.PartyType)
						
						def cusCCC = current_Party.CarrierCustomerCode
						if (cusCCC) {
							Map<String, String> bkgOfficeMap = util.getCdeConversionFromIntCde(TP_ID, null, null, null, 'EDIFACT', 'CarrierCustCode', cusCCC, conn)
							if (bkgOfficeMap.get("EXT_CDE")) {
								'C082_02' {
									'E3039_01' bkgOfficeMap.get("EXT_CDE")
								}
							}
						}
						
						if (current_Party.PartyName) {
							'C080_04' {
								'E3036_01' util.substring(current_Party.PartyName, 1, 35).trim()
								if (util.substring(current_Party.PartyName, 36, 35)) {
									'E3036_02' util.substring(current_Party.PartyName, 36, 35).trim()
								}
								'E3036_03' ''
								'E3036_04' ''
								'E3036_05' ''
								'E3045_06' ''
							}
						}
						
						String addressLine = ''
						current_Party.Address?.AddressLines?.AddressLine.each { current_AddressLine ->
							if (current_AddressLine) {
								if (addressLine.length()>0) {
									addressLine += " "
								}
								addressLine += current_AddressLine
							}
						}
						if (addressLine) {
							'C059_05' {
								'E3042_01' util.substring(addressLine, 1, 35).trim()
								if (addressLine.length()>35) {
									'E3042_02' util.substring(addressLine, 36, 35).trim()
								}
								if (addressLine.length()>70) {
									'E3042_03' util.substring(addressLine, 71, 35).trim()
								}
								if (addressLine.length()>105) {
									'E3042_04' util.substring(addressLine, 106, 35).trim()
								}
							}
						}
						if (current_Party?.Address?.City) {
							'E3164_06' util.substring(current_Party?.Address?.City, 1, 35).trim()
						}
						if (current_Party?.Address?.PostalCode) {
							'E3251_08' util.substring(current_Party?.Address?.PostalCode, 1, 17).trim()
						}
						if (current_Party?.Address?.Country) {
							'E3207_09' util.substring(current_Party?.Address?.Country, 1, 3).trim()
						}
					}
				}
			}
			
			//CAR - CA
			'Group6_NAD' {
				'NAD' {
					'E3035_01' 'CA'
					'C082_02' {
						'E3039_01' '300736'
					}
					'C080_04' {
						'E3036_01' 'ORIENT OVERSEAS CONTAINER LINE'
					}
				}
			}
			//MS
			'Group6_NAD' {
				'NAD' {
					'E3035_01' 'MS'
					'C082_02' {
						'E3039_01' '300736'
					}
					'C080_04' {
						'E3036_01' 'ORIENT OVERSEAS CONTAINER LINE'
					}
				}
			}
			
			//FullReturn 'TRK':'TR'
//			if (isVendorCCCFound) {
//				def duplicateFullReturnList = []
//				current_Body.ContainerGroup?.ContainerFlowInstruction?.FullReturn.each { current_FullReturn ->
//					def facilityCode = current_FullReturn.Facility?.FacilityCode
//					if (facilityCode && currentBcScac) {
//						Map<String, String> queryResult = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, currentBcScac, msgFmtId, convertTypeID_VENDORCODE, facilityCode, conn)
//						//if can be found in code conversion setup
//						String vendorCCC = queryResult.get("EXT_CDE")
//						if (vendorCCC && (! duplicateFullReturnList.contains(vendorCCC))) {
//							duplicateFullReturnList.add(vendorCCC)
//							'Group6_NAD' {
//								'NAD' {
//									'E3035_01' 'TR'
//									'C082_02' {
//										'E3039_01' vendorCCC
//									}
//									if (current_FullReturn.Facility?.FacilityName) {
//										'C080_04' {
//											'E3036_01' util.substring(current_FullReturn.Facility?.FacilityName, 1, 35).trim()
//										}
//									}
//									if (current_FullReturn.Address?.City) {
//										'E3164_06' util.substring(current_FullReturn.Address?.City, 1, 35).trim()
//									}
//								}
//							}
//						}
//					}
//				}
//				duplicateFullReturnList.clear()
//			}
			// end of FullReturn Party in Group_6
			
			//Group_18
			List<Container> cntrs = []
			Map cntrRemarks = [:]
			current_Body.ContainerGroup?.Container.each { current_Container ->
				if (current_Container.CarrCntrSizeType) {
					Map<String, String> queryResult = util.getCdeConversionFromIntCde(TP_ID, MSG_TYPE_ID, DIR_ID, null, msgFmtId, 'ContainerSizeType', current_Container.CarrCntrSizeType, conn)
					def convertCarCntrType = queryResult.get("EXT_CDE")
					def cntrInfo = queryResult.get("REMARKS")
					if (util.isEmpty(convertCarCntrType)) {
						convertCarCntrType = current_Container.CarrCntrSizeType
					}
					current_Container.CS1ContainerSizeType = convertCarCntrType
					if (cntrRemarks.get(convertCarCntrType)==null && cntrInfo) {
						cntrRemarks.put(convertCarCntrType, cntrInfo)
					}
					cntrs.add(current_Container)
				}
			}
			cntrs.groupBy{it.CS1ContainerSizeType}.each { current_ConvertCntrSizeType, current_ContainerGroup ->
				'Group18_EQD' {
					'EQD' {
						'E8053_01' 'CN'
						'C224_03' {
							'E8155_01' current_ConvertCntrSizeType
							'E1131_02' ''
							'E3055_03' '5'
							if (cntrRemarks.get(current_ConvertCntrSizeType)) {
								'E8154_04' util.substring(cntrRemarks.get(current_ConvertCntrSizeType), 1, 35).trim()
							}
						}
						'E8077_04' ''
						'E8249_05' ''
						'E8169_06' ''
						'E4233_07' ''
					}
					'EQN' {
						'C523_01' {
							'E6350_01' current_ContainerGroup.size()
							'E6353_02' ''
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
		def IFTMBC = outXml.createNode('IFTMBC')
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
			List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
			// prep validation
			prepValidation(current_Body, current_BodyIndex, errorKeyList)
			
			//mapping
			generateBody(current_Body, outXml)

			// posp checking
			if(errorKeyList.isEmpty()){
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
		//valid BC status allow to send
//		List<String> allowStatusList = new ArrayList<String>()
//		allowStatusList.add("Confirmed")
//		allowStatusList.add("Declined")
//
//		def varBkgStatus = current_Body.GeneralInformation?.BookingStatus
//
//		//1. check booking confirm status
//		bcUtil.checkBCStatus(varBkgStatus, allowStatusList, false, "Only allow Confirmed, Declined, Ignored Invalid Message Status: ", errorKeyList)
//
//		//2. Only NEW BC can be sent out.
//		bcUtil.checkOnlyNewAllowSend(current_Body, false, null, errorKeyList)
//
//		if (varBkgStatus != "Declined") {
//			//3. booking office
//			bcUtil.CheckBookingOfficeFiltering(TP_ID, current_Body, conn, false, "Unknown booking office for BASF-Germany: ", errorKeyList)
//
//			//4. Missing External Reference: Shipper Reference.
//			bcUtil.checkExternalReferenceShipperReference(current_Body, false, "Missing External Reference: Shipper Reference.", errorKeyList)
//
//			//5. Invalid Booking Party.
//			bcUtil.CheckIsValidBookingParty(TP_ID, current_Body, conn, false, "Invalid Booking Party.", errorKeyList)
//		}
		
	}
	
	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList) {

//		XmlParser xmlParser = new XmlParser();
//		Node node = xmlParser.parseText(outputXml + "</IFTMBC>")

//		bcUtil.checkB102Mandatory(current_Body, node, true, null, errorKeyList)
//		bcUtil.checkN101Mandatory(current_Body, node, true, null, errorKeyList)
//		bcUtil.checkY3_01Mandatory(node, true, null, errorKeyList)

	}
}
