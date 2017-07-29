package cs.b2b.mapping.unittest

import cs.b2b.beluga.api.BelugaApiUtil
import cs.b2b.beluga.api.EDIProcessResult
import cs.b2b.beluga.api.EdiControlNumberReplaceResult
import cs.b2b.beluga.common.fileparser.UIFFileParser
import cs.b2b.mapping.e2e.util.BelugaOceanHelper
import cs.b2b.mapping.e2e.util.CommonUtil
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.e2e.util.LocalFileUtil
import cs.b2b.mapping.scripts.CAR_CS2BRXML_IFTMBF_CMACGM
import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import java.sql.Connection

/**
 * Created by CHEUNBE2 on 6/29/2017.
 */
class CAR_CS2BRXML_IFTMBF_CMACGM_Test {
	String[] runtimeParams
	Connection conn

	@Before
	void before(){

		List runtimeParamsList = new LinkedList()
		runtimeParamsList.add("B2BSessionID="+"B2BSessionID")
		runtimeParamsList.add("B2B_OriginalSourceFileName="+"B2B_OriginalSourceFileName")
		runtimeParamsList.add("B2B_SendPortID="+"B2B_SendPortID")
		runtimeParamsList.add("PortProperty="+"PortProperty")
		runtimeParamsList.add("MSG_REQ_ID="+"MSG_REQ_ID")
		runtimeParamsList.add("TP_ID="+"CMACGM")
		runtimeParamsList.add("MSG_TYPE_ID="+"CS2BR")
		runtimeParamsList.add("DIR_ID="+"O")
		runtimeParamsList.add("MSG_FMT_ID="+"EDIFACT")

		runtimeParams = runtimeParamsList.toArray()

		ConnectionForTester testDBConn = new ConnectionForTester();
		conn = testDBConn.getB2BEDIQA1_DEV_DBConn();

	}

	@Test
	void initTest(){
		String testInputFileNamePath = "D:/SVN/Regression/Auto/CS2BR/OUT_D99B/CMACGM/1-CS2BR-CMACGM.edi"
		String inputXmlBody = LocalFileUtil.readBigFile(testInputFileNamePath)
		String testExpecteFileNamePath ="D:/SVN/Regression/Auto/CS2BR/OUT_D99B/CMACGM/ExpectedFile/1-CS2BR-CMACGM.edi.edifact"
		String ouputXmlBody = LocalFileUtil.readBigFile(testExpecteFileNamePath)

		CAR_CS2BRXML_IFTMBF_CMACGM mapping = new CAR_CS2BRXML_IFTMBF_CMACGM()

		String mappingResult = mapping.mapping(inputXmlBody, runtimeParams, conn)

		BelugaOceanHelper bhelper = new BelugaOceanHelper();
		bhelper.getBelugaOceanDefinitionSettingStr(runtimeParams, conn);
		String definitionBody = bhelper.getDefinitionBody();
		if (! CommonUtil.isEmpty(definitionBody)) {
			UIFFileParser parser = new UIFFileParser();
			EDIProcessResult ediResult = parser.xml2edi(mappingResult, definitionBody, bhelper.getBelugaOceanConfigSettingStr());

			mappingResult = ediResult.outputString==null?"":ediResult.outputString.toString();

			BelugaApiUtil bapiutil = new BelugaApiUtil();
			String validationSummary = bapiutil.getErrorSummary(ediResult);
			List<String> validationInfos = bapiutil.getTxnWarning(ediResult);

			//20170207 update beluga to txn object mode
			if (validationSummary!=null && validationSummary.length()>0) {
				System.out.println("-----------------------------");
				System.out.println("Beluga Warning Info for Error Transaction: ");
				System.out.println("Summary: "+validationSummary);
				for(int i=0; validationInfos!=null && i<validationInfos.size(); i++) {
					System.out.println(validationInfos.get(i));
				}
				System.out.println("-----------------------------");
			}

			//20170109, david, add beluga handle edi control number
			if (mappingResult!=null && mappingResult.length()>0) {
				mappingResult = bhelper.setEDICtrlNumber(mappingResult, conn);
				long[] ctrls = bhelper.getEDIControlNumber('CARGOSMART', 'CMACGM', 'CS2BR', 'EDIFACT', conn);

				EdiControlNumberReplaceResult result = bapiutil.updateOutgoingEDIControlNumber(mappingResult, bhelper.getBelugaOceanConfigSettingStr(), ctrls[0], ctrls[1]);
				assertEquals(ouputXmlBody.replaceAll("'", "'\n"), result.outputEdiBody.replaceAll("'", "'\n"))
			}
		}
	}
}
