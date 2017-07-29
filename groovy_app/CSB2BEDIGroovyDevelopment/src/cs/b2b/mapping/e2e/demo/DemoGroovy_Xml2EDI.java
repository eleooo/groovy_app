package cs.b2b.mapping.e2e.demo;

import cs.b2b.beluga.api.BelugaApiUtil;
import cs.b2b.beluga.api.EDIProcessResult;
import cs.b2b.beluga.common.fileparser.UIFFileParser;
import cs.b2b.core.common.classloader.GroovyClassDefinition;
import cs.b2b.core.common.classloader.GroovyScriptHelper;
import cs.b2b.mapping.e2e.util.*;
import groovy.lang.GroovyObject;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

public class DemoGroovy_Xml2EDI extends DemoBase {

	//1, provide your test file here
//	static String testInputFileNamePath = "Z:\\1-PANTOS-1-pair.xml";
	static String testInputFileNamePath = "c:\\EDI2017072009341468-39_1.in";
//	static String testInputFileNamePath = "Z:\\490-EASTMANCHEMICAL-baseline_3OceanLeg.xml";
//	static String testInputFileNamePath = "C:\\Users\\RENGA\\Desktop\\833-MILESTONE-2-BL_CA1366973-B2A01=04_UPDATE.xml";

	///CSB2BEDIGroovyDevelopment/demo/CT_DSGOOGS/in_DSGOODS_CS210_DnD.xml

	//2, all mapping scripts
	//2.1, mapping JavaBean common script
	static String javaBeanCommonScriptFile = "./src/cs/b2b/core/mapping/bean/MessageBeanCS2XmlCommon.groovy";
	//2.2, mapping JavaBean message type script
//	static String javaBeanMessageTypeScriptFile = "./src/cs/b2b/core/mapping/bean/br/BookingRequest.groovy";
	static String javaBeanMessageTypeScriptFile = "./src/cs/b2b/core/mapping/bean/bl/BillOfLading.groovy";
//	static String javaBeanMessageTypeScriptFile = "./src/cs/b2b/core/mapping/bean/bc/BookingConfirm.groovy";
	//2.3, Mapping Util library script file, if not use it, then not need to provide here
	static String mappingUtilScriptFile = "./src/cs/b2b/core/mapping/util/MappingUtil.groovy";
	//2.4, message type common groovy script
	static String mappingUtilMessageTypeCommonScriptFile = "./src/cs/b2b/core/mapping/util/MappingUtil_BL_O_Common.groovy";
//	static String mappingUtilMessageTypeCommonScriptFile = "./src/cs/b2b/core/mapping/util/MappingUtil_BC_O_Common.groovy";
	//2.5, pmt groovy script
//	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2BLXML_310_DUMMY310BLb.groovy";
//	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2BCXML_301_EASTMANCHEMICAL.groovy";
//	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2BCXML_301_IMERYS.groovy";
//	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2BLXML_310_AMAZON.groovy";
//	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2BLXML_310_LLBEANUSBANK.groovy";
//	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CAR_CS2BRXML_IFTMBF_CMACGM.groovy";
//	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2BLXML_310_OFCDEPOT.groovy";
//	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2BLXML_310_SARECYCLING.groovy";
//static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2BLXML_310_CARDINALHEALTHAMBER.groovy";
static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2BLXML_D96B_INFODISBVFNL1014430.groovy";

//	static String definitionFile = "./IG_Definition/CUS_4010_310_CS.xml";
	static String definitionFile = "./IG_Definition/CUS_D96B_IFTMCS_CS.xml";
//	static String definitionFile = "./IG_Definition/CUS_4010_301_CS.xml";

	public void demo() throws Exception {

		String appSessionId = System.currentTimeMillis() + "";

		String[] runtimeParameters = new String[]{
				"B2BSessionID="+appSessionId,
				"OriginalSourceFileName="+new File(testInputFileNamePath).getName(),
				"MSG_REQ_ID=EDI-TEST-001-DUMMY",
				//for BelugaOcean EDI setting, use PMT info in table table B2B_EDI_TP_LBC_SETTING
				"TP_ID=DUMMYBLD99Bb",
				"MSG_TYPE_ID=BL",
//				"MSG_TYPE_ID=BC",
				"DIR_ID=O",
				"MSG_FMT_ID=X.12"
		};

		//Mapping Part
		GroovyClassDefinition groovyDefClass = null;
		//db connection if you need to use
		Connection conn = null;

		try {
			long start = Calendar.getInstance().getTimeInMillis();

			//20161222 david
			LinkedHashMap<String, String> scripts = new LinkedHashMap<String, String>();
			//1, put message type javabean common script
			String cs2CommonBean = "cs.b2b.core.mapping.bean."+(new File(javaBeanCommonScriptFile).getName());
			scripts.put(cs2CommonBean, LocalFileUtil.readBigFile(javaBeanCommonScriptFile));
			//2, put message type javabean message script
			scripts.put(new File(javaBeanMessageTypeScriptFile).getName(), LocalFileUtil.readBigFile(javaBeanMessageTypeScriptFile));
			//3, put general mapping util script
			scripts.put(new File(mappingUtilScriptFile).getName(), LocalFileUtil.readBigFile(mappingUtilScriptFile));
			//4, put message type common script
			String pmtMappingUtil =new File(mappingUtilMessageTypeCommonScriptFile).getName();
//			pmtMappingUtil = "cs.b2b.core.mapping.util.MappingUtil_CT_O_Common.groovy";
			scripts.put(pmtMappingUtil, LocalFileUtil.readBigFile(mappingUtilMessageTypeCommonScriptFile));
			//5, put pmt mapping script
			String pmtScriptName = new File(pmtMappingScriptFile).getName();
			scripts.put(pmtScriptName, LocalFileUtil.readBigFile(pmtMappingScriptFile));

			groovyDefClass = GroovyScriptHelper.getClassDef(scripts);

			GroovyObject instance = groovyDefClass==null?null:groovyDefClass.getInstance();


			String testInputFileBody = LocalFileUtil.readBigFile(testInputFileNamePath);
			ConnectionForTester testDBConn = new ConnectionForTester();
			conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
			Object[] invokeParams = new Object[] { testInputFileBody, runtimeParameters, conn };

			String output = instance==null?null:(String) instance.invokeMethod("mapping", invokeParams);

			if (output==null || output.trim().length()==0) {
				System.out.println(cs.b2b.core.common.session.B2BRuntimeSession.getSessionValue(appSessionId, "PROMOTE_SESSION_BIZKEY"));
				throw new Exception("Mapping output is empty.");
			}

			long end = Calendar.getInstance().getTimeInMillis();

			//20170301 - definition updated to get from db setting - b2b_edi_beluga_cfg
			BelugaOceanHelper bhelper = new BelugaOceanHelper();
//			bhelper.getBelugaOceanDefinitionSettingStr(runtimeParameters, conn);
//			String definitionBody = bhelper.getDefinitionBody();
			String definitionBody = LocalFileUtil.readBigFile(definitionFile);
			if (! CommonUtil.isEmpty(definitionBody)) {
				UIFFileParser parser = new UIFFileParser();
				EDIProcessResult ediResult = parser.xml2edi(output, definitionBody, bhelper.getBelugaOceanConfigSettingStr());

				output = ediResult.outputString==null?"":ediResult.outputString.toString();

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
//				if (output!=null && output.length()>0) {
//					output = bhelper.setEDICtrlNumber(output, conn);
//				}
			}

			println(">>"+new File(pmtMappingScriptFile).getName()+", cost: "+(end-start)+" ms, result:");
			println(output);

		} finally {
			if (conn!=null) {
				try { conn.close(); } catch (Exception e) {}
			}
			//important, clean up to avoid memory leak
			if (groovyDefClass!=null) {
				groovyDefClass.close();
			}
		}
	}


}
