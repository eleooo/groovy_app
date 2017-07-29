package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.bean.bl.Container
import cs.b2b.core.mapping.bean.bl.FND
import cs.b2b.core.mapping.bean.bl.FirstPOL
import cs.b2b.core.mapping.bean.bl.FreightCharge
import cs.b2b.core.mapping.bean.bl.FreightChargeCNTR;
import cs.b2b.core.mapping.bean.bl.LastPOD
import cs.b2b.core.mapping.bean.bl.OceanLeg
import cs.b2b.core.mapping.bean.bl.POR
import cs.b2b.core.mapping.bean.bl.Party
import cs.b2b.core.mapping.util.XmlBeanParser


/**
* @author ZHONGWE
* @pattern after TP=HDYOW
*/
public class CUS_CS2BLXML_310_AVONELN {

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
	public void generateBody(Body current_Body, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml, Connection conn) {

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '310'
				'E329_02' '-999'
			}
			///////////////////   B3-start
			'B3' {
				
				if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLNumber)){
					'E76_02' current_Body?.GeneralInformation?.BLNumber
				}
				if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLNumber)){
					'E145_03' current_Body?.GeneralInformation?.BLNumber?.trim()
				}
				if(current_Body?.FreightCharge){
					if(current_Body?.FreightCharge[0]?.ChargeType == blUtil.COLLECT){
						'E146_04' 'CC'
					}
					else if(current_Body?.FreightCharge[0]?.ChargeType == blUtil.PREPAID){
						'E146_04' 'PP'
					}
				}
				
				SimpleDateFormat soutfmt = new SimpleDateFormat(xmlDateTimeFormat)
				
				if(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT){
					'E373_06' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
				}else if(current_Body?.GeneralInformation?.BLOnboardDT?.GMT){
					'E373_06' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT, xmlDateTimeFormat, yyyyMMdd)
				}
				
				sum = new BigDecimal(0);
				filteredFreightCharge?.each{current_FreightCharge ->
					if(current_FreightCharge?.TotalAmtInPmtCurrency){
						sum = sum + new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString())
					}
				}
				'E193_07' (sum.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();
				'E140_11' current_Body?.GeneralInformation?.SCACCode
			}
			
			///////////////////   B2A-start
			Map<String, String> eventDescriptionMap = ['NEW':'00','UPDATE':'05']
			'B2A' {
				if(eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)){
					'E353_01' eventDescriptionMap.get(current_Body?.EventInformation?.EventDescription)
				}
			}
			
			
			///////////////////   N9-start
			def N9PositionIndex = 0
			//Loop by BLNum ONCE
			if(N9PositionIndex < 15){
				N9PositionIndex = N9PositionIndex + 1
				'N9' {
					'E128_01' '2I'
					'E127_02' current_Body?.GeneralInformation?.BLNumber
				}
			}
			
			///////////////////   V1-start
			OceanLeg firstOceanLeg = null
			OceanLeg lastOceanLeg = null
			if(current_Body?.Route?.OceanLeg?.size() > 0){
				firstOceanLeg = current_Body?.Route?.OceanLeg[0]
				List<OceanLeg> OceanLegs = new ArrayList<OceanLeg>()
				current_Body?.Route?.OceanLeg?.eachWithIndex{current_OceanLeg, legIndex->
					if(StringUtil.isNotEmpty(current_OceanLeg?.SVVD?.Discharge?.VesselName) && legIndex > 0){
						OceanLegs.add(current_OceanLeg)
					}
				}
				if(current_Body?.Route?.OceanLeg?.size() > 1 && OceanLegs.size() > 0 ){
					lastOceanLeg = OceanLegs[-1]
				}
			}
			if(firstOceanLeg){
				'V1' {
					if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber){
						'E597_01' firstOceanLeg?.SVVD?.Loading?.LloydsNumber
					}else if(firstOceanLeg?.SVVD?.Loading?.CallSign){
						'E597_01' firstOceanLeg?.SVVD?.Loading?.CallSign
					}
					if(firstOceanLeg?.SVVD?.Loading?.VesselName){
						'E182_02' firstOceanLeg?.SVVD?.Loading?.VesselName
					}
				}
			}
			if(lastOceanLeg){
				'V1' {
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber){
						'E597_01' lastOceanLeg?.SVVD?.Discharge?.LloydsNumber
					}else if(lastOceanLeg?.SVVD?.Discharge?.CallSign){
						'E597_01' lastOceanLeg?.SVVD?.Discharge?.CallSign
					}
					if(lastOceanLeg?.SVVD?.Discharge?.VesselName){
						'E182_02' lastOceanLeg?.SVVD?.Discharge?.VesselName
					}
				}
			}
			
			///////////////////   C3-start
			'C3'{
				'E100_01' 'USD'
			}
			
			'Loop_N1' {    
				'N1' {
					'E98_01' 'CA'
					if(current_Body?.GeneralInformation?.SCACCode){
						'E93_02' current_Body?.GeneralInformation?.SCACCode
					}
				}
			}
			
			
			//R4  -->strat
			LastPOD POD =  current_Body?.Route?.LastPOD
			def oceanLegSize= current_Body?.Route?.OceanLeg?.size()
			
			//POD
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					if(POD){
						if(POD?.Port?.locationCode?.UNLocationCode){
							'E309_02' 'UN'
							'E310_03' POD?.Port?.locationCode?.UNLocationCode
						}else if(POD?.Port?.LocationCode?.SchedKDCode){
							'E309_02' POD?.Port?.LocationCode?.SchedKDType
							'E310_03' POD?.Port?.LocationCode?.SchedKDCode
						}
						
						Map PODCSCountryCodeConvert = ['CA':'CAN','US':'USA']
						
						if(POD?.Port?.CSCountryCode && PODCSCountryCodeConvert.get(POD?.Port?.CSCountryCode)){
							'E26_05' PODCSCountryCodeConvert.get(POD?.Port?.CSCountryCode)
						}else{
							'E26_05' POD?.Port?.CSCountryCode
						}
					}
				}
				if(oceanLegSize > 0){
					if(current_Body?.Route?.OceanLeg[-1]?.ArrivalDT?.find{it?.attr_Indicator=='E' || it?.attr_Indicator=='A'}?.LocDT){
						'DTM' {
							SimpleDateFormat soutfmt = new SimpleDateFormat(xmlDateTimeFormat)
								'E374_01' '003'
							'E373_02' util.substring(util.convertDateTime(soutfmt.format(new Date()), xmlDateTimeFormat, 'yyyyMMddHHmm'),1,8)
							'E337_03' util.substring(util.convertDateTime(soutfmt.format(new Date()), xmlDateTimeFormat, 'yyyyMMddHHmm'),9,4)
						}
					}
				}
				
			}
			
			///////////////////   LX-start
			associateContainerAndCargo?.eachWithIndex{current_Container, current_cargoList, Container_index->
				List<Container> containerGroup = current_Body.Container.findAll{it?.ContainerNumber == current_Container?.ContainerNumber && it?.ContainerCheckDigit == current_Container?.ContainerCheckDigit && it?.CS1ContainerSizeType == current_Container?.CS1ContainerSizeType}
				'Loop_LX' {
					'LX' {
						 'E554_01' Container_index +1
					}
					if(util.substring(containerGroup[0]?.ContainerNumber, 5, 5)){
						'Loop_N7' {
							if(util.substring(current_Container?.ContainerNumber, 5, 10)){
								'N7' {
									'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
									'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10).concat(util.substring(current_Container?.ContainerCheckDigit,1,1))
									
								}
								
								containerGroup?.each{ currentSubContainer ->
									currentSubContainer.Seal?.each{current_Seal ->
										if(current_Seal?.SealNumber){
											'M7' {
												'E225_01' current_Seal?.SealNumber?.trim()
											}
										}
									}
								}
								
								filteredFreightCharge?.eachWithIndex{current_FreightCharge, FreightChargeIndex->
									
									if(current_FreightCharge?.ChargeType == blUtil.COLLECT || current_FreightCharge?.ChargeType == blUtil.PREPAID){
										if(current_FreightCharge?.TotalAmtInPmtCurrency){
											'Loop_L1' {
												'L1' {
													'E213_01' FreightChargeIndex + 1
													
													def charge = null
													if(current_FreightCharge?.TotalAmtInPmtCurrency){
														charge = multiplyWithoutOriginalAccuray(current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency,"100",2)
													}
													
													def calculateMethod = current_FreightCharge?.CalculateMethod
													if(calculateMethod?.startsWith("B")){
														calculateMethod = "B%"
													}
													
													List rateValueQualifierList = ['GROSS CARGO WEIGHT', 'CONTAINER','MEASUREMENT','PACKAGE','COUNT','NET CARGO WEIGHT','B%','LUMP SUM']
													
													if(current_Body?.Container?.size() <=0 && current_Body?.Cargo?.size() > 0){
														'E58_04' charge
													}else if(rateValueQualifierList.contains(calculateMethod?.toUpperCase())){
														'E58_04' blUtil.replaceZeroAfterPoint(String.format("%.13f", charge.toDouble()/current_Body?.container?.size()))
													}else{
														'E58_04' charge
													}
													
													if(current_FreightCharge?.ChargeType == blUtil.PREPAID){
														if(current_FreightCharge?.TotalAmtInPmtCurrency){
															'E117_06' multiplyWithoutOriginalAccuray(current_FreightCharge?.TotalAmtInPmtCurrency?.TotalAmtInPmtCurrency, "100", 2)
														}
													}
													
													Map<String, String> chargeTypeExtCDE = blUtil.getB2BCdeConversion(current_FreightCharge?.ChargeCode, TP_ID, 'ChargeType', conn)
													if(StringUtil.isNotEmpty(current_FreightCharge?.ChargeCode) && StringUtil.isNotEmpty(chargeTypeExtCDE.get("EXT_CDE")) ){
														'E150_08' chargeTypeExtCDE.get("EXT_CDE")
													}else{
														'E150_08' 'MSC'
													}
													
													if(StringUtil.isNotEmpty(current_FreightCharge?.ChargeCode) && StringUtil.isNotEmpty(chargeTypeExtCDE.get("REMARKS")) ){
														'E276_12' chargeTypeExtCDE.get("REMARKS")
													}
													
													if(current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency){
														'E100_20' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			///////////////////   L3-start
			'L3' {
				sum = new BigDecimal(0);
				filteredFreightCharge?.each{current_FreightCharge ->
					if(current_FreightCharge?.TotalAmtInPmtCurrency){
						sum = sum + new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString())
					}
				}
				'E60_03' blUtil.replaceZeroAfterPoint((sum.setScale(2,BigDecimal.ROUND_HALF_UP)).toString())
				'E122_04' 'AA'
			}
			
			'SE' {
				'E96_01' '-999'
				'E329_02' '-999'
			}
		}



	}


	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn)  {

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
		def headerMsgDT = util.convertDateTime(bl.Header?.MsgDT?.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		def txnErrorKeys = []

		//Start mapping

		bl?.Body?.eachWithIndex{current_Body, current_BodyIndex ->

			List<FreightCharge> filteredFreightCharge = current_Body.FreightCharge?.clone()
			List<FreightChargeCNTR> filteredFreightChargeCNTR = current_Body.FreightChargeCNTR?.clone()

			//OceanLegReorder
			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)

			//filterFreightCharge
			blUtil.filterByChargeType(filteredFreightCharge, filteredFreightChargeCNTR, blUtil.PREPAID)

			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			if(errorKeyList.isEmpty()){
				//mapping
				generateBody(current_Body,  associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml ,conn)
				println(writer.toString())
			}

			//posp checking
			if(errorKeyList.isEmpty()){
				pospValidation(writer?.toString(), errorKeyList, current_Body )
			}

			blUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%18s', current_Body?.TransactionInformation?.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			blUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, bl.Header?.MsgDT?.GMT, bl.Header.InterchangeMessageID, TP_ID, conn)
			println(errorKeyList)
			println(bizKeyWriter.toString())
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

		writer.close();
		return result;
	}

	//AVONELN
	
	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
		blUtil.amendments_C(current_Body, false, null, errorKeyList)
		blUtil.checkSealNumberLength(current_Body, 15, true, null, errorKeyList)
		blUtil.missingBLNumber(current_Body, true, "", errorKeyList)
		blUtil.n7CntrrNumOrM7SealNumMissing(current_Body, true, null, errorKeyList)
		return errorKeyList;
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList , cs.b2b.core.mapping.bean.bl.Body current_Body){

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")

		blUtil.checkL0Max(node,120, true, "L0 is over than maximum", errorKeyList)
//		blUtil.checkL106Length(node, 9, true, null, errorKeyList)
		blUtil.checkL1max(node, 20, true, null, errorKeyList)
		blUtil.checkN7L1(node, true, "No L1 output to customer", errorKeyList)
		
	}
	
	private String multiplyWithoutOriginalAccuray(String multiplyed, String multiplying, Integer oriAccuray){
		def accuray = 0;
		if(multiplyed?.indexOf('.')>0){
			accuray = (multiplyed?.length() - multiplyed?.indexOf('.')-1) - oriAccuray;
		}
		accuray = accuray<0?0:accuray;
		return new BigDecimal(multiplyed).multiply(new BigDecimal(multiplying)).setScale(accuray,BigDecimal.ROUND_HALF_UP).toString()
	}
}

