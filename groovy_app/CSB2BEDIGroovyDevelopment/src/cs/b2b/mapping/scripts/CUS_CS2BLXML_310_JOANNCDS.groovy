import groovy.xml.MarkupBuilder

import java.sql.Connection

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.bean.bl.Container
import cs.b2b.core.mapping.bean.bl.FND
import cs.b2b.core.mapping.bean.bl.FirstPOL
import cs.b2b.core.mapping.bean.bl.FreightCharge
import cs.b2b.core.mapping.bean.bl.FreightChargeCNTR
import cs.b2b.core.mapping.bean.bl.LastPOD
import cs.b2b.core.mapping.bean.bl.OceanLeg
import cs.b2b.core.mapping.bean.bl.POR
import cs.b2b.core.mapping.util.XmlBeanParser

public class CUS_CS2BLXML_310_JOANNCDS {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_BL_O_Common blUtil = new cs.b2b.core.mapping.util.MappingUtil_BL_O_Common(util);

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

	BigDecimal sum = null
	BigDecimal hundred = new BigDecimal (100);
	BigDecimal thousand = new BigDecimal (1000);
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '310'
				'E329_02' '-999'
			}
//			B3 segment
			'B3' {
				if (current_Body?.GeneralInformation?.BLType?.toUpperCase() == 'SEA WAYBILL'){
					'E147_01' '2'
				}else {
					'E147_01' 'B'
				}
				if(current_Body?.GeneralInformation?.BLNumber){
					'E76_02' current_Body?.GeneralInformation?.BLNumber
				}
				'E146_04' 'CC'
				'E373_06' new Date().format(yyyyMMdd)
				'E193_07' '0'
				if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT){
					'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '140'
				}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
					'E32_09' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E374_10' '139'
				}
				'E140_11' current_Body?.GeneralInformation?.SCACCode
			}
//			N9 segment
			//Loop by Body/CarrierRate
			def vCarrRatetTy= null
			def vItemCde = null
			Map<String, String> referenceCodeCS2Map = ['CR':'CR','CTR':'CT','EXPR':'RF','FID':'FI','GEN':'GN','ICR':'CR','INV':'IK','MB':'BN','PO':'PO','QUOT':'QN','SC':'CT','SCA':'SCA','SID':'SI','SR':'SR','TARIF':'TI','WARG':'QA']
			current_Body?.CarrierRate?.each{current_CarrierRate ->			
					if(current_CarrierRate?.CSCarrierRateType ){
						// get the current looping type
						vCarrRatetTy = current_CarrierRate?.CSCarrierRateType
					}
					if (vCarrRatetTy){
						vItemCde = referenceCodeCS2Map.get(vCarrRatetTy)
					}
					if(vItemCde){
						'N9' {
						'E128_01' vItemCde
						'E127_02' current_CarrierRate.CarrierRateNumber
						}
					}
			}
			//Loop by Body/ExternalReference
			current_Body?.ExternalReference?.each{current_ExternalReference ->				
					if(current_ExternalReference?.CSReferenceType ){
						// get the current looping type
						vCarrRatetTy = current_ExternalReference?.CSReferenceType
					}
					if (vCarrRatetTy){
						vItemCde = referenceCodeCS2Map.get(vCarrRatetTy)
					}
					if(vItemCde ){
						'N9' {
							'E128_01' vItemCde
							'E127_02' current_ExternalReference.ReferenceNumber
						}
					}
			}
//			V1 segment, always get from last oceanLeg
			OceanLeg lastOceanLeg = null
			if(current_Body?.Route?.OceanLeg?.size() > 0){
					lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
			}
			if(lastOceanLeg){
				'V1' {
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber?.trim()){
						'E597_01' lastOceanLeg?.SVVD?.Discharge?.LloydsNumber?.trim() //TIBCO no substring
					}else if(lastOceanLeg?.SVVD?.Discharge?.CallSign){
						'E597_01' lastOceanLeg?.SVVD?.Discharge?.CallSign?.trim() //TIBCO no substring
					}
					'E182_02' lastOceanLeg?.SVVD?.Discharge?.VesselName?.trim()
					'E26_03' lastOceanLeg?.POD?.Port?.CSCountryCode?.trim()
					if(lastOceanLeg?.SVVD?.Discharge?.Voyage && lastOceanLeg?.SVVD?.Discharge?.Direction){
						if(lastOceanLeg?.DischargeDirectionName){
							'E55_04' lastOceanLeg?.SVVD?.Discharge?.Voyage + lastOceanLeg?.DischargeDirectionName
						}else{
							'E55_04' lastOceanLeg?.SVVD?.Discharge?.Voyage
						}
						
					}
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber?.trim()){
						'E897_08' 'L'
					}else if(lastOceanLeg?.SVVD?.Discharge?.CallSign?.trim()){
						'E897_08' 'C'
					}
				}
			}			
			//R4  -->strat
			FirstPOL POL =  current_Body?.Route?.FirstPOL
			LastPOD POD =  current_Body?.Route?.LastPOD
			FND FND =  current_Body?.Route?.FND
			def oceanLegSize= current_Body?.Route?.OceanLeg?.size()
			//POL			
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if(POL){
						if(POL?.Port?.locationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' POL?.Port?.locationCode?.UNLocationCode
						}else if (POL?.Port?.LocationCode?.SchedKDCode){
							'E309_02' POL?.Port?.LocationCode?.SchedKDType
							'E310_03' POL?.Port?.LocationCode?.SchedKDCode
						}
						if(POL?.Port?.PortName){
							'E114_04' POL?.Port?.PortName?.trim().toUpperCase()
						}else if(POL?.Port?.City){
							'E114_04' POL?.Port?.City?.trim().toUpperCase()
						}
						if(POL?.Port?.CSCountryCode){
							'E26_05' POL?.Port?.CSCountryCode?.trim().toUpperCase()
						}
						if(POL?.CSStateCode){
							'E156_08' POL?.CSStateCode?.trim().toUpperCase()
						}
					}
				}
				if(oceanLegSize > 0){
					'DTM' {
						if(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[0]?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
			//POD			
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if(POD){
						if(POD?.Port?.locationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' POD?.Port?.locationCode?.UNLocationCode
						}else if (POD?.Port?.LocationCode?.SchedKDCode){
							'E309_02' POD?.Port?.LocationCode?.SchedKDType
							'E310_03' POD?.Port?.LocationCode?.SchedKDCode
						}
						if(POD?.Port?.PortName){
							'E114_04' POD?.Port?.PortName?.trim().toUpperCase()
						}else if(POD?.Port?.City){
							'E114_04' POD?.Port?.City?.trim().toUpperCase()
						}
						if(POD?.Port?.CSCountryCode){
							'E26_05' POD?.Port?.CSCountryCode?.trim().toUpperCase()
						}
						if(POD?.CSStateCode){
							'E156_08' POD?.CSStateCode?.trim().toUpperCase()
						}
					}
				}
				if(oceanLegSize > 0){
					'DTM' {
						if(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
							'E374_01' '140'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
						}else if(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
							'E374_01' '139'
							'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
						}
					}
				}				
			}
			//FND			
			'Loop_R4' {
				'R4' {
					'E115_01' 'N'
					if(FND){
						if(FND?.CityDetails?.LocationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' FND?.CityDetails?.LocationCode?.UNLocationCode
						}else if (FND?.CityDetails?.LocationCode?.SchedKDCode){
							'E309_02' FND?.CityDetails?.LocationCode?.SchedKDType
							'E310_03' FND?.CityDetails?.LocationCode?.SchedKDCode
						}
						if(FND?.CityDetails?.City){
							'E114_04' FND?.CityDetails?.City?.trim().toUpperCase()
						}
						if(FND?.CSStandardCity?.CSCountryCode){
							'E26_05' FND?.CSStandardCity?.CSCountryCode?.trim().toUpperCase()
						}
						if(FND?.CSStandardCity?.CSStateCode){
							'E156_08' FND?.CSStandardCity?.CSStateCode?.trim().toUpperCase()
						}
					}
				}
				'DTM' {
					if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator?.equals('A')}?.LocDT){
						'E374_01' '140'
						'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
					}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E374_01' '139'
						'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
					}else if(oceanLegSize > 0 && current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
						'E374_01' '140'
						'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, HHmm)
					}else if(oceanLegSize > 0 && current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
						'E374_01' '139'
						'E373_02' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(current_Body.Route.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, HHmm)
					}
				}
			}
//			C8 and C8C segment
			current_Body?.BLCertClause?.each{current_BLCertClause ->
				'Loop_C8' {
					'C8' {
						'E246_02' current_BLCertClause?.CertificationClauseType
						'E247_03'  util.substring(current_BLCertClause?.CertificationClauseText, 1, 60)
					}
					List<String> splitTextList= new ArrayList<String>()
					if(current_BLCertClause?.CertificationClauseText?.length() > 60){
						def v = util.substring(current_BLCertClause?.CertificationClauseText, 61, current_BLCertClause?.CertificationClauseText?.length() - 60)
						while(v.size() > 180){
							splitTextList.add(util.substring(v, 1, 180))
							v = util.substring(v, 181, v.size() - 180)
						}
						if(v.size() > 0){
							splitTextList.add(v)
						}
					}
					splitTextList?.each{current_text->
						'C8C' {
							if(util.substring(current_text, 1, 60)){
								'E247_01' util.substring(current_text, 1, 60)
							}
							if(current_text?.length() > 60){
								'E247_02' util.substring(current_text, 61, 60)
							}
							if(current_text?.length() > 120){
							'E247_03' util.substring(current_text, 121, 60)
							}
						}
					}					
				}
			}
			current_Body?.cargo.eachWithIndex { current_cago, cargo_index ->
				'Loop_LX' {
					'LX' {
						'E554_01' cargo_index +1
					}
				Container currentContainer = current_Body?.Container?.find{it?.ContainerNumber == current_cago?.CurrentContainerNumber}
				if(currentContainer){					
					'Loop_N7' {
						if(util.substring(currentContainer?.ContainerNumber, 5, 10)){
							'N7' {
								'E206_01' util.substring(currentContainer?.ContainerNumber, 1, 4)
								'E207_02' util.substring(currentContainer?.ContainerNumber, 5, 10)

								BigDecimal varWeight1 = BigDecimal.ZERO
								
								if(current_cago?.GrossWeight?.WeightUnit?.toUpperCase() == 'TON' && StringUtil.isNotEmpty(current_cago?.GrossWeight?.Weight?.trim())){
									varWeight1 = varWeight1.add(current_cago?.GrossWeight?.Weight?.trim())?.multiply(thousand)?.toBigDecimal()
								}else if(current_cago?.GrossWeight?.WeightUnit == 'LBS' && StringUtil.isNotEmpty(current_cago?.GrossWeight?.Weight?.trim())){
									varWeight1 = current_cago?.GrossWeight?.Weight?.trim().toBigDecimal().divide(2.2)
								}else if(current_cago?.GrossWeight?.Weight.trim().toBigDecimal() > BigDecimal.ZERO){
									varWeight1 = varWeight1.add(current_cago?.GrossWeight?.Weight?.trim().toBigDecimal())
								}
								if(varWeight1 > BigDecimal.ZERO){
									'E81_03'  blUtil.replaceZeroAfterPoint(varWeight1.setScale(4,BigDecimal.ROUND_DOWN).toString())
									'E187_04' 'G'
								}								
								BigDecimal sumVolume = BigDecimal.ZERO
								if(current_cago?.Volume?.Volume){
									sumVolume = sumVolume.add(current_cago?.Volume?.Volume?.toBigDecimal())
								}
								if(sumVolume > BigDecimal.ZERO){
									'E183_08' blUtil.replaceZeroAfterPoint(sumVolume.setScale(3,BigDecimal.ROUND_HALF_UP).toString())
								}else{
									'E183_08' '0'
								}
								if( current_cago?.Volume?.VolumeUnit == 'CBF'){
									'E184_09' 'E'
								}else if(current_cago?.Volume?.VolumeUnit == 'CBM'){
									'E184_09' 'X'
								}
								'E188_17' 'K'
//									if(current_Container?.ContainerCheckDigit != null && !util.isDecimal(util.substring(current_Container?.ContainerCheckDigit, 1, 1))){
//										throw new Exception("N718 is mapped from ContainerCheckDigit, it must be a number, but actual is " + util.substring(current_Container?.ContainerCheckDigit, 1, 1))
//									}
								if(currentContainer?.ContainerCheckDigit){
									'E761_18' util.substring(currentContainer?.ContainerCheckDigit, 1, 1)	//this field is a number type, will validate in BO
								}
								if(currentContainer?.CS1ContainerSizeType){
									Map<String, String> retMap = util.getCdeConversionFromIntCde(TP_ID, 'BL', 'O', null, 'X.12', 'ContainerType', currentContainer?.CS1ContainerSizeType, conn)
									if(retMap.get("EXT_CDE")){
										'E24_22' retMap.get("EXT_CDE")
									}
								}
							}
						}
						if(currentContainer?.PieceCount?.PieceCount && currentContainer?.PieceCount?.PieceCountUnit){
							'QTY' {
								'E673_01' '39'
								'E380_02' currentContainer?.PieceCount?.PieceCount?.trim()
								Map<String, String> pieceCountUnitMap = [:]
								'C001_03' {
									if(pieceCountUnitMap.get(currentContainer?.PieceCount?.PieceCountUnit)){
										'E355_01' pieceCountUnitMap.get(currentContainer?.PieceCount?.PieceCountUnit?.trim())
									}else{
										'E355_01' currentContainer?.PieceCount?.PieceCountUnit?.trim()
									}
								}
							}
						}
						currentContainer.Seal?.each{current_Seal ->
								if(current_Seal?.SealNumber){
									'M7' {
										'E225_01' current_Seal?.SealNumber?.trim()
									}
								}
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

		//Parse the xmlBody to JavaBean
		XmlBeanParser parser = new XmlBeanParser()
		BillOfLading bl = parser.xmlParser(inputXmlBody, BillOfLading.class)


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
		def T310 = outXml.createNode('T310')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.

		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()
		def txnErrorKeys = []

		//Start mapping

		bl?.Body?.eachWithIndex{current_Body, current_BodyIndex ->

//			List<FreightCharge> filteredFreightCharge = current_Body.FreightCharge?.clone()
//			List<FreightChargeCNTR> filteredFreightChargeCNTR = current_Body.FreightChargeCNTR?.clone()
			//OceanLegReorder
			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)
			//ChargeFilter
//			blUtil.filterByTotalAmtInPmtCurrencyIfZero(filteredFreightCharge, filteredFreightChargeCNTR)

			//associate container and cargo
//			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)
			
			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			//mapping
			if(errorKeyList.isEmpty()){
				generateBody(current_Body, outXml)
			}
			//posp checking
//			if(errorKeyList.isEmpty()){
//				pospValidation(writer?.toString(), errorKeyList)
//			}
			
			//EDI Syntax checking
			if(errorKeyList.isEmpty()){
				syntaxValidation(writer?.toString(), errorKeyList)
			}

			blUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%18s', current_Body?.TransactionInformation?.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			blUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, bl.Header?.MsgDT?.GMT, bl.Header.InterchangeMessageID, TP_ID, conn)

			txnErrorKeys.add(errorKeyList)
		}

		//End root node
		outXml.nodeCompleted(null,T310)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		blUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		blUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		blUtil.promoteHeaderIntChgMsgId(appSessionId, bl?.Body[0]);
		blUtil.promoteScacCode(appSessionId, bl?.Body[0]);
		
		String result = '';
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			result = util.cleanXml(writer?.toString());
		}
		//replaceAll * to |
//		result = result.replaceAll('\\*', '\\|');
		writer.close();
		
		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();		
		
		blUtil.bLReadyOnly(current_Body, false, "Status is not BL-Ready", errorKeyList)
		blUtil.missingCntrrNum(current_Body, false, null, errorKeyList)
		
		return errorKeyList;
	}

//	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){
//	}
	
		void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){	
		
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")
		
		blUtil.checkB309_B310_P(node, true, null, errorKeyList)
		blUtil.checkC803_C802_R(node, true, null, errorKeyList)
		blUtil.checkN703_N704_P(node, true, null, errorKeyList)		
		blUtil.checkN901_N902_M(node, true, null, errorKeyList)
		blUtil.checkN902_N903_R(node, true, null, errorKeyList)
		blUtil.checkQTY01_QTY02_R(node, true, null, errorKeyList)
		blUtil.checkV101_V102_R(node, true, null, errorKeyList)
		//no W9 mapping, so impossiable to hit such exception
//		blUtil.checkW0902_W0903_P(node, true, null, errorKeyList)			
	}
}

