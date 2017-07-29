package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.DecimalFormat
import java.util.List;
import java.util.Map;

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.EmptyPickup
import cs.b2b.core.mapping.bean.bc.ExternalReference
import cs.b2b.core.mapping.bean.bc.Facility
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.core.mapping.util.XmlBeanParser


/**
 * @author YINEM
 * @pattern after TP = DUMMYBC301
 */
public class CUS_CS2BCXML_301_KODAKEXPORT {

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

	Map bookingStatusMap = ['Confirmed':'A']

	Map<String,String> referenceTypeMap = [:]

	public void generateBody(Body current_Body, MarkupBuilder outXml) {
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '301'
				'E329_02' '-999'
			}
			'B1' {
				'E140_01' current_Body.GeneralInformation?.SCACCode

				List<ExternalReference> RefNumList = current_Body?.ExternalReference?.findAll{it.CSReferenceType=='SID'}
				if (RefNumList.size() > 0 && RefNumList[-1].ReferenceNumber) {
					'E145_02' util.substring(RefNumList[-1].ReferenceNumber?.trim(), 1, 30)
				} else {
					'E145_02' '0000000000'
				}
				'E373_03' util.convertXmlDateTime(current_Body?.GeneralInformation?.BookingStatusDT?.LocDT, yyyyMMdd)
				'E558_04' bookingStatusMap.get(current_Body?.GeneralInformation?.BookingStatus)
			}
			'Y3' {
				if (current_Body.GeneralInformation?.SCACCode && current_Body?.GeneralInformation?.CarrierBookingNumber) {
					'E13_01' current_Body.GeneralInformation.CarrierBookingNumber
				}
				'E140_02' current_Body.GeneralInformation?.SCACCode
				if (current_Body.Route?.FirstPOL?.DepartureDT?.LocDT?.LocDT) {
					'E373_03' util.convertXmlDateTime(current_Body.Route.FirstPOL.DepartureDT.LocDT.LocDT, yyyyMMdd)
				}
				if (current_Body.Route?.LastPOD?.ArrivalDT?.LocDT?.LocDT) {
					'E373_04'  util.convertXmlDateTime(current_Body.Route.LastPOD.ArrivalDT.LocDT.LocDT, yyyyMMdd)
				}
				if (current_Body?.Route?.FullReturnCutoff?.LocDT?.LocDT) {
					'E373_07'  util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT.LocDT, yyyyMMdd)
				}
				if (current_Body?.Route?.FullReturnCutoff?.LocDT?.LocDT) {
					'E337_08'   util.convertXmlDateTime(current_Body.Route.FullReturnCutoff.LocDT.LocDT, HHmm)
				}
			}

			//Y4 Loop
			//loop for 1st of EmptyPickup only
			//TIBCO always loop once, follow it.
			//			if(current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup){
			def emptyCntrList = current_Body.ContainerGroup?.ContainerFlowInstruction?.EmptyPickup
			'Loop_Y4' {
				'Y4' {
					if (emptyCntrList) {
						def emptyCntr = emptyCntrList[0]
						'E373_03' util.convertXmlDateTime(emptyCntr.MvmtDT?.LocDT, yyyyMMdd)
						'E95_05' emptyCntr.NumberOfContainers

						def defConvType = ''
						if (emptyCntr.ISOSizeType) {
							defConvType = util.getConversionByTpIdMsgTypeDirFmtScac(TP_ID, MSG_TYPE_ID, DIR_ID, "X.12", "ContainerType", current_Body.GeneralInformation?.SCACCode, emptyCntr.ISOSizeType, conn)
							if (! defConvType) {
								defConvType = emptyCntr.ISOSizeType
							}
						}
						'E24_06' defConvType
					}
					'E140_07' current_Body.GeneralInformation?.SCACCode
				}
			}
			//			}
			// W09 not used
			// N9 not used
			// N1 not used

			//loopType = 'POR'
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode){
						'E309_02' current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDType
						'E310_03' current_Body.Route.POR.CityDetails.LocationCode.SchedKDCode
					}
					else if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' current_Body.Route.POR.CityDetails.LocationCode.UNLocationCode
					}

					'E114_04' util.substring(current_Body?.Route?.POR?.CityDetails?.City, 1, 24)
				}
			}
			//loopType = 'POL'
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDType
						'E310_03' current_Body.Route.FirstPOL.Port.LocationCode.SchedKDCode
					} else if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' current_Body.Route.FirstPOL.Port.LocationCode.UNLocationCode
					}
					'E114_04' util.substring(current_Body?.Route?.FirstPOL?.Port?.City, 1, 24)
				}
			}
			//loopType = 'POD'
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDType
						'E310_03' current_Body.Route.LastPOD.Port.LocationCode.SchedKDCode
					} else if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' current_Body.Route.LastPOD.Port.LocationCode.UNLocationCode
					}
					'E114_04' util.substring(current_Body?.Route?.LastPOD?.Port?.City, 1, 24)
				}
			}
			//loopType = 'FND'
			'Loop_R4' {
				'R4' {
					'E115_01' 'E'
					if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode) {
						'E309_02' current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDType
						'E310_03' current_Body.Route.FND.CityDetails.LocationCode.SchedKDCode
					} else if (current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
						'E310_03' current_Body.Route.FND.CityDetails.LocationCode.UNLocationCode
					}
					'E114_04' util.substring(current_Body?.Route?.FND?.CityDetails?.City, 1, 24)
				}
			}
			current_Body?.Cargo?.eachWithIndex{ current_Cargo, index->
				'Loop_LX' {
					'LX' { 'E554_01' index+1 }
					'L0' {
						'E213_01' index+1
						if(current_Cargo.GrossWeight?.Weight && current_Cargo.GrossWeight?.Weight!='0'&& current_Cargo.GrossWeight?.WeightUnit){
							'E81_04' util.substring(current_Cargo.GrossWeight?.Weight?.replaceAll('\\+',''), 1, 14)
							'E187_05' 'G'
						}
						if (current_Cargo.Volume?.Volume) {
							if (current_Cargo.Volume?.VolumeUnit?.toUpperCase()=='CBM' || current_Cargo.Volume?.VolumeUnit?.toUpperCase()=='CBF') {
								'E183_06' current_Cargo.Volume?.Volume
							}
							if (current_Cargo.Volume?.VolumeUnit?.toUpperCase()=='CBM') {
								'E184_07' 'X'
							} else if(current_Cargo.Volume?.VolumeUnit?.toUpperCase()=='CBF') {
								'E184_07' 'E'
							}
						}

						if (current_Cargo.Packaging?.PackageType && current_Cargo.Packaging?.PackageQty && Integer.parseInt(current_Cargo.Packaging?.PackageQty)>0) {
							'E80_08' current_Cargo.Packaging.PackageQty
							def defPackageConvert = util.getConversionByTpIdMsgTypeDirFmtScac(TP_ID, 'BC', DIR_ID, 'X.12', 'PackageUnit',current_Body.GeneralInformation?.SCACCode, current_Cargo.Packaging?.PackageType.trim(), conn)
							if (defPackageConvert) {
								'E211_09' defPackageConvert
							}else
								'E211_09' current_Cargo.Packaging.PackageType
						}
						'E458_10' ''
						if(current_Cargo.GrossWeight?.WeightUnit){
							if(current_Cargo.GrossWeight.WeightUnit.substring(0,1)=='K')
								'E188_11' 'K'
							else if (current_Cargo.GrossWeight.WeightUnit.substring(0,1)=='L')
								'E188_11' 'L'
						}
						def defOb = current_Body?.GeneralInformation?.TrafficMode?.OutBound
						def defIb = current_Body?.GeneralInformation?.TrafficMode?.InBound
						def def56 = ''
						if(defOb == 'FCL' && defIb == 'FCL') {
							def56 = 'HH'
						} else if(defOb == 'FCL' && defIb == 'LCL') {
							def56 = 'HD'
						} else if(defOb == 'LCL' && defIb == 'FCL') {
							def56 = 'DH'
						}  else if(defOb == 'LCL' && defIb == 'LCL') {
							def56 = 'DD'
						}
						'E56_12' def56

						'E380_13' ''
						'E211_14' ''
						'E1073_15' ''
					}
					'L5' {
						'E213_01' index+1
						'E79_02' util.substring(current_Cargo?.CargoDescription, 1, 50)
						'E22_03' ''
						'E23_04' ''
						'E103_05' ''
						'E87_06' ''
						'E88_07' ''
						'E23_08' ''
						'E22_09' ''
						'E595_10' ''
					}
					// L4 is not used
					// L1 is not used
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
					// H1 is not used
				}
			}

			//			Map dirMap = ['E':'East', 'S':'South', 'W':'West', 'N':'North']
			if(current_Body?.Route?.OceanLeg && current_Body?.Route?.OceanLeg[0]) {
				def def597 = util.substring(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.LloydsNumber, 1, 20)
				if (! def597) {
					def597 = util.substring(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Vessel, 1, 8)
				}
				def defVoyage = current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Voyage
				def defDir = current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.Direction
				defVoyage = (defVoyage==null?"":defVoyage)
				defDir = (defDir==null?"":defDir)
				def def55 = ''
				if(current_Body?.Route?.OceanLeg[0].LoadingExtVoyage){
					def55 = util.substring(current_Body.Route.OceanLeg[0].LoadingExtVoyage,1,10)
				}
				else
					def55 = util.substring(defVoyage + defDir, 1, 10)
				if (def597 || def55) {
					'V1' {
						if (def597) {
							'E597_01' def597
						}
						if (current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.VesselName) {
							'E182_02' util.substring(current_Body?.Route?.OceanLeg[0].SVVD?.Loading?.VesselName, 1, 28)
						}
						'E26_03' ''
						'E55_04' def55
						'E140_05' current_Body.GeneralInformation?.SCACCode
						'E249_06' ''
						'E854_07' ''
						if (current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber) {
							'E897_08' 'L'
						} else if (!current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber&&current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.Vessel) {
							'E897_08' 'Z'
						} else if (!current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.LloydsNumber&&!current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.Vessel) {
							'E897_08' ''
						}
						'E91_09' ''
					}
				}
			}
			if (current_Body?.Route?.OceanLeg && current_Body?.Route?.OceanLeg?.size()>1) {
				def def597 = util.substring(current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.LloydsNumber, 1, 20)
				if (! def597) {
					def597 = util.substring(current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.Vessel, 1, 8)
				}
				def devVoyage = current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.Voyage
				def devDir = current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.Direction
				devVoyage = (devVoyage==null?"":devVoyage)
				devDir = (devDir==null?"":devDir)
				def def55 = ''
				if(current_Body?.Route?.OceanLeg[-1].LoadingExtVoyage){
					def55 =util.substring(current_Body.Route.OceanLeg[-1].LoadingExtVoyage,1,10)
				} else {
					def55 = util.substring(devVoyage + devDir, 1, 10)
				}
				if (def597 || def55) {
					'V1' {
						if (def597) {
							'E597_01' def597
						}
						if (current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.VesselName) {
							'E182_02' util.substring(current_Body?.Route?.OceanLeg[-1].SVVD?.Loading?.VesselName, 1, 28)
						}
						'E26_03' ''
						'E55_04' def55

						'E140_05' current_Body.GeneralInformation?.SCACCode
						'E249_06' ''
						'E854_07' ''
						if (current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.LloydsNumber) {
							'E897_08' 'L'
						} else if (!current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.LloydsNumber&&current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.Vessel) {
							'E897_08' 'Z'
						} else if (!current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.LloydsNumber&&!current_Body?.Route?.OceanLeg[-1]?.SVVD?.Discharge?.Vessel) {
							'E897_08' ''
						}
						'E91_09' ''
					}
				}
			}
			'V9' {
				'E304_01' ''
				'E106_02' ''
				'E373_03' ''
				'E337_04' ''
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

			//associate container and cargo
			//			Map<cs.b2b.core.mapping.bean.bc.Container, List<cs.b2b.core.mapping.bean.bc.Cargo>> associateContainerAndCargo = bcUtil.associateContainerAndCargo(current_Body)

			//prep checking
			List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
			// prep validation
			prepValidation(current_Body, current_BodyIndex, errorKeyList)


			//mapping
			//			if(current_Body?.GeneralInformation?.BookingStatus=='Declined'||current_Body?.GeneralInformation?.BookingStatus=='Rejected') {
			//				//Declined / Reject mapping
			//				generateBody4RejectCase(current_Body, outXml)
			//			} else {
			generateBody(current_Body, outXml)
			//			}

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


	void prepValidation(Body current_Body, int bodyLoopIndex, List<Map<String,String>> errorKeyList) {
		//valid BC status allow to send
		List<String> allowStatusList = new ArrayList<String>()
		allowStatusList.add("Confirmed")

		def varBkgStatus = current_Body.GeneralInformation?.BookingStatus

		//1. check booking confirm status
		bcUtil.checkBCStatus(varBkgStatus, allowStatusList, false, "Only accept Confirmed bookings, the status is: ", errorKeyList)

	}

	void pospValidation(Body current_Body, String outputXml, List<Map<String,String>> errorKeyList){


		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T301>")
		bcUtil.checkY3_01Mandatory(node, true, null, errorKeyList)

	}

}
