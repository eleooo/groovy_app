package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection

/**
 * @authore RENGA
 * @pattern after DUMMY310BLb
 */
public class CUS_CS2BLXML_310_OFCDEPOT {

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
	BigDecimal hundred = new BigDecimal ('100');
	BigDecimal thousand = new BigDecimal ('1000');
	Map<String, String> weightUnitMap = ['TON':'K', 'KGS':'K', 'LBS':'L']
	Map<String, String> volumeUnitMap = ['CBF':'E', 'CFT':'E', 'CBM':'X']
	public void generateBody(Body current_Body, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '310'
				'E329_02' '-999'
			}
			BigDecimal sumCharge = BigDecimal.ZERO
			filteredFreightCharge?.each{current_FreightCharge ->
				if(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()){
					sumCharge = sumCharge + current_FreightCharge?.TotalAmtInPmtCurrency?.toString()?.toBigDecimal()
				}
			}

			'B3' {
				if(current_Body?.GeneralInformation?.BLNumber){
					//Map B302=B303=BLNumber
					'E76_02' util.substring(current_Body?.GeneralInformation?.BLNumber, 1, 10)
					'E145_03' util.substring(current_Body?.GeneralInformation?.BLNumber, 1, 30)
				}
				'E146_04' 'CC'
				'E373_06' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT, xmlDateTimeFormat, yyyyMMdd)
				//Map only collect charges and the total collect charge amount to L305=B307 with B304=“CC”.
				'E193_07' (sumCharge.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString();

				'E140_11' current_Body?.GeneralInformation?.SCACCode
				if(current_Body?.GeneralInformation?.BLIssueDT?.LocDT){
					'E373_12' util.convertDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
				}
			}

			//Map N9 as per CL - only N9*BM
			'N9' {
				'E128_01' 'BM'
				'E127_02' util.substring(current_Body?.GeneralInformation?.BLNumber, 1, 35)
			}
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
					'E597_01' current_Body?.GeneralInformation?.SCACCode
					if(firstOceanLeg?.SVVD?.Loading?.VesselName){
						'E182_02' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName,1,28)
					}
				}
			}

			//Map only 1 N1 for N1*SH (PartyType=SHP)
			Map<String, String> partyTypeMap = ['SHP':'SH']
			current_Body?.Party?.each{current_Party ->
				if(partyTypeMap.get(current_Party?.PartyType)){
					'Loop_N1' {
						'N1' {
							'E98_01' partyTypeMap.get(current_Party?.PartyType)
							if(current_Party?.PartyName){
								'E93_02' util.substring(current_Party?.PartyName,1, 60)
							}
						}
						def vAddressLines = ''
						current_Party?.Address?.AddressLines?.AddressLine?.each{current_addressLine ->
							vAddressLines = vAddressLines + ' ' + current_addressLine
						}
						'N3' {
							if(util.substring(vAddressLines?.trim(), 1, 35)){
								'E166_01' util.substring(vAddressLines?.trim(), 1, 35)?.trim()
							}
							if(vAddressLines.trim()?.length() >= 36){
								'E166_02' util.substring(vAddressLines?.trim(), 36, 35)?.trim()
							}
						}
						'N4' {
							'E19_01' util.substring(current_Party?.Address?.City, 1, 30)
							Map<String, String> vResult = null
							Map<String, String> vResult1 = null
							Map<String, String> vResult2 = null
							if(current_Party?.Address?.State){
								vResult = util.getCS2MasterCityByStateNameCityNameCntryCde(current_Party?.Address?.State, current_Party?.Address?.City, current_Party?.Address?.Country, conn)
							}
							if(StringUtil.isEmpty(vResult?.get('STATE_CDE')) ){
								vResult1 = util.getCS2MasterCityByStateNameCityName(current_Party?.Address?.State, current_Party?.Address?.City, conn)
							}
							if(StringUtil.isEmpty(vResult?.get('STATE_CDE')) && StringUtil.isEmpty(vResult1?.get('STATE_CDE')) ){
								vResult2 = util.getCS2MasterCityByStateName(current_Party?.Address?.State, conn)
							}
							if(vResult?.get('STATE_CDE')){
								'E156_02' util.substring(vResult?.get('STATE_CDE'),1, 2)
							}else if(vResult1?.get('STATE_CDE')){
								'E156_02' util.substring(vResult1?.get('STATE_CDE'),1, 2)
							}else if(vResult2?.get('STATE_CDE')){
								'E156_02' util.substring(vResult2?.get('STATE_CDE'),1, 2)
							}
							'E116_03' util.substring(current_Party?.Address?.PostalCode, 1, 12)
							String countryCode = util.substring(util.getCS2MasterCity4CountryCodeByCountryName(current_Party?.Address?.Country, conn),1,2)
							if(StringUtil.isEmpty(countryCode)){
								countryCode = util.substring(current_Party?.Address?.Country, 1, 2)
							}
							'E26_04' countryCode
						}
					}
				}
			}

				//R4  -->strat
//			Map only 1 R4 for R4*L (FirstPOL) having
//			o	R402=OR
//			o	R403=LocationCode.ScheKDCode
//			o	R404=LocationCode.UNLocationCode

			FirstPOL POL =  current_Body?.Route?.FirstPOL
			//POL
			if(POL){
				'Loop_R4' {
					'R4' {
						'E115_01' 'L'
						'E309_02' 'OR'
						'E310_03' POL?.Port?.locationCode?.SchedKDCode
						'E114_04' POL?.Port?.locationCode?.UNLocationCode
					}
				}
			}


//			Map only 1 LX containing
//			o	All N7 containers
//			o	With only 1 L0 having total weight (L004=L301), volume (L006=L309)
//			o	Set L008=count (N7), L009=CNT
//			Include/append the ContainerCheckDigit into N702
//			Convert N711 (leave a default), N722 (use original as default) ~ CL subject for editing
			int N7Count = 0
			BigDecimal sumWight = null
			String wightUnit = null
			BigDecimal sumVolume = null
			String volumeUnit = null
			'Loop_LX' {
				'LX' {
					'E554_01' '1'
				}
				current_Body?.Container?.each {current_Container ->
					'Loop_N7' {
						if(util.substring(current_Container?.ContainerNumber, 5, 10)){
							'N7' {
								'E206_01' util.substring(current_Container?.ContainerNumber, 1, 4)
								//Include/append the ContainerCheckDigit into N702
								'E207_02' util.substring(current_Container?.ContainerNumber, 5, 10) + (current_Container?.ContainerCheckDigit ? current_Container?.ContainerCheckDigit : '')
								//Convert N711 (map first 2-char as default), N722 (use original as default)
								Map<String, String> resultMap = util.getCdeConversionFromIntCde(TP_ID, 'BL', 'O', null,  'X.12', 'ContainerType', current_Container?.CS1ContainerSizeType, conn)
								if(StringUtil.isNotEmpty(resultMap.get("EXT_CDE"))) {
									if (resultMap.get("EXT_CDE").indexOf("_") > -1) {
										'E40_11' resultMap.get("EXT_CDE").split("_")[1]
										'E24_22' resultMap.get("EXT_CDE").split("_")[0]
									}else{
										'E40_11' util.substring(resultMap.get("EXT_CDE"),1,2)
										'E24_22' resultMap.get("EXT_CDE")
									}
								}else{
									'E40_11' util.substring(current_Container?.CS1ContainerSizeType, 1,2)
									'E24_22' current_Container?.CS1ContainerSizeType
								}
							}
							if(current_Container?.GrossWeight?.Weight){
								if(wightUnit == null){
									wightUnit = current_Container?.GrossWeight?.WeightUnit
								}
								if(sumWight == null){
									sumWight = current_Container?.GrossWeight?.Weight?.toBigDecimal()
								}else{
									sumWight = sumWight + current_Container?.GrossWeight?.Weight?.toBigDecimal()
								}
							}
							N7Count++
						}
					}
				}

				current_Body?.Cargo?.each {current_Cargo ->
					if(current_Cargo?.Volume){
						if(volumeUnit == null){
							volumeUnit = current_Cargo?.Volume?.VolumeUnit
						}
						if(sumVolume == null){
							sumVolume = current_Cargo?.Volume?.Volume?.toBigDecimal()
						}else{
							sumVolume = sumVolume + current_Cargo?.Volume?.Volume?.toBigDecimal()
						}
					}
				}

				'Loop_L0' {
					'L0' {
						'E213_01' '1'
						if(sumWight){
							'E81_04' sumWight?.toString()
							'E187_05' 'G'
						}
						if(sumVolume){
							'E183_06' sumVolume?.toString()
							'E184_07' volumeUnitMap.get(volumeUnit)
						}
						'E80_08' N7Count
						'E211_09' 'CNT'
						if(sumWight && wightUnit) {
							'E188_11' weightUnitMap.get(wightUnit)
						}
					}
				}
			}

			'L3' {
				if(sumWight) {
					'E81_01' sumWight?.toString()
					'E187_02' 'G'
				}
				'E58_05' (sumCharge.multiply(hundred).setScale(0,BigDecimal.ROUND_HALF_UP)).toString()
				if(sumVolume){
					'E183_09' sumVolume?.toString()
					'E184_10' volumeUnitMap.get(volumeUnit)
				}
				if(sumWight && wightUnit){
					'E188_12' weightUnitMap.get(wightUnit)
				}

			}
			filteredFreightCharge?.eachWithIndex{current_FreightCharge, FreightChargeIndex->
				'Loop_L1' {
					'L1' {
						BigDecimal total = BigDecimal.ZERO
						if(current_FreightCharge?.TotalAmtInPmtCurrency){
							total = new BigDecimal(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()).multiply(hundred)
						}
						'E58_04' blUtil.replaceZeroAfterPoint(total?.toString())

						//select EXT_CDE from b2b_cde_conversion WHERE (INT_CDE = ? or REMARKS = ?) and DIR_ID = 'O' and MSG_TYPE_ID = 'BL' and TP_ID = ? and CONVERT_TYPE_ID = 'ChargeType'
						Map<String, String> chargeTypeExtCDE = blUtil.getB2BCdeConversion(util.substring(current_FreightCharge?.ChargeCode, 1, 3)?.toUpperCase(), TP_ID, 'ChargeType', conn)
						if(chargeTypeExtCDE.get("EXT_CDE")){
							'E150_08' chargeTypeExtCDE.get("EXT_CDE")
						}else{
							'E150_08' current_FreightCharge?.ChargeCode
						}

						'E276_12' util.substring(current_FreightCharge?.ChargePrintLable?.trim(), 1, 25)
					}
				}
			}
			if(current_Body?.Remarks){
				List<String> Remarks = blUtil.SplitTextWithConnector(current_Body?.Remarks, 30)
				Remarks?.each{current_Remarks ->
					'K1' {
						'E61_01' current_Remarks
						'E61_02' current_Remarks
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

			List<FreightCharge> filteredFreightCharge = current_Body.FreightCharge?.clone()
			List<FreightChargeCNTR> filteredFreightChargeCNTR = current_Body.FreightChargeCNTR?.clone()
			//OceanLegReorder
			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)
			//ChargeFilter
			blUtil.filterByTotalAmtInPmtCurrencyIfZero(filteredFreightCharge, filteredFreightChargeCNTR)

			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)
			
			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			//mapping
			if(errorKeyList.isEmpty()){
				generateBody(current_Body,  associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml)
			}
			//posp checking
			if(errorKeyList.isEmpty()){
				pospValidation(writer?.toString(), errorKeyList)
			}
			
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
		writer.close();
		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){

		List<Map<String, String>> errorList = []

		//Send only US-bound shipments. Obsolete the transaction if Last POD Country is not US.
		blUtil.NonUSinPOD(false,null,errorList,current_Body)

		return errorList
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")

		blUtil.checkL0Max(node, 120, true, null, errorKeyList)
		blUtil.checkL102Length(node, 9, true, null, errorKeyList)
		blUtil.checkL106Length(node, 12, true, null, errorKeyList)
		blUtil.checkL307Length(node, 12, true, null, errorKeyList)

	}


	void syntaxValidation(String outputXml, List<Map<String,String>> errorKeyList){

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</T310>")
		
		blUtil.checkC803_C802_R(node, true, null, errorKeyList)
		blUtil.checkG6103_G6104_P(node, true, null, errorKeyList)
		blUtil.checkH107_H108_P(node, true, null, errorKeyList)
		blUtil.checkL004_L005_P(node, true, null, errorKeyList)
		blUtil.checkL006_L007_P(node, true, null, errorKeyList)
		blUtil.checkL008_L009_P(node, true, null, errorKeyList)
		blUtil.checkL011_L004_C(node, true, null, errorKeyList)
		blUtil.checkL102_L103_P(node, true, null, errorKeyList)
		blUtil.checkL104_L105_L106_R(node, true, null, errorKeyList)
		blUtil.checkL301_L302_P(node, true, null, errorKeyList)
		blUtil.checkL309_L310_P(node, true, null, errorKeyList)
		blUtil.checkL312_L301_C(node, true, null, errorKeyList)
		blUtil.checkN406_N405_C(node, true, null, errorKeyList)
		blUtil.checkR402_R403_P(node, true, null, errorKeyList)
		blUtil.checkV101_V102_R(node, true, null, errorKeyList)
		blUtil.checkV108_V101_C(node, true, null, errorKeyList)
		blUtil.checkW0902_W0903_P(node, true, null, errorKeyList)
		
	}
}

