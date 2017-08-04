package cs.b2b.mapping.e2e.demo;

import cs.b2b.core.common.classloader.GroovyClassDefinition;
import cs.b2b.core.common.classloader.GroovyScriptHelper;
import cs.b2b.mapping.e2e.util.ConnectionForTester;
import cs.b2b.mapping.e2e.util.DemoBase;
import cs.b2b.mapping.e2e.util.LocalFileUtil;
import groovy.lang.GroovyObject;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.LinkedHashMap;

public class DemoGroovy_Xml2Xml extends DemoBase {

	//1. CS2 xml JavaBean script
	String beanCommonFilePath = "./src/cs/b2b/core/mapping/bean/MessageBeanCS2XmlCommon.groovy";
	String ctBeanFilePah = "./src/cs/b2b/core/mapping/bean/bl/BillOfLading.groovy";
//	String ctBeanFilePah = "./src/cs/b2b/core/mapping/bean/bc/BookingConfirm.groovy";
	
	//2, Mapping Util library script file, if not use it, then not need to provide here
	String mappingUtilScriptFileNamePath = "./src/cs/b2b/core/mapping/util/MappingUtil.groovy";
	String mappingMsgTypeCommonScriptFilePath = "./src/cs/b2b/core/mapping/util/MappingUtil_BL_O_Common.groovy";
//	String mappingMsgTypeCommonScriptFilePath = "./src/cs/b2b/core/mapping/util/MappingUtil_BC_O_Common.groovy";
	
	//3, PMT groovy script file
//	static String ediMappingGroovyScriptFileNamePath = "./src/cs/b2b/mapping/scripts/CUS_CS2BLXML_XML_WGLL.groovy";
	static String ediMappingGroovyScriptFileNamePath = "./src/cs/b2b/mapping/scripts/CUS_CS2BLXML_XML_EXXONELITE.groovy";
//	static String ediMappingGroovyScriptFileNamePath = "./src/cs/b2b/mapping/scripts/CUS_CS2BCXML_XML_OOCLLOGISTICS.groovy";
		
	//4, provide your test file here
//	static String testInputFileNamePath = "./demo/CT_DSGOOGS/in_DSGOODS_CS020.xml";
//	static String testInputFileNamePath = "Y:\\\\1170-SDV-EDI2011072609273072-71.BL.X.12.CS.HJSC.SDV.BLUPDATEO.2011072609235009.uif.xml";
//	static String testInputFileNamePath = "\\\\yinem-2-w7\\SVN\\RegressionKukirMig\\Auto\\BL\\XML\\11-ACNI-17-Preparation_In_Progress-EDI2014042210241328-39.xml";
	static String testInputFileNamePath = "D:\\1_B2BEDI_Revamp\\BL\\OUT_XML\\EXXONELITE\\InputData\\EDI2017011303342874-44.in";


	public void demo() throws Exception {
		Connection conn = null;
		GroovyClassDefinition groovyDefClass = null;
		try {
			long start = Calendar.getInstance().getTimeInMillis();
			
			//20161222 david
			LinkedHashMap<String, String> scripts = new LinkedHashMap<String, String>();
			scripts.put(new File(beanCommonFilePath).getName(), LocalFileUtil.readBigFile(beanCommonFilePath));
			scripts.put(new File(ctBeanFilePah).getName(), LocalFileUtil.readBigFile(ctBeanFilePah));
			scripts.put(new File(mappingUtilScriptFileNamePath).getName(), LocalFileUtil.readBigFile(mappingUtilScriptFileNamePath));
			scripts.put(new File(mappingMsgTypeCommonScriptFilePath).getName(), LocalFileUtil.readBigFile(mappingMsgTypeCommonScriptFilePath));
			scripts.put(new File(ediMappingGroovyScriptFileNamePath).getName(), LocalFileUtil.readBigFile(ediMappingGroovyScriptFileNamePath));
			
			groovyDefClass = GroovyScriptHelper.getClassDef(scripts);
			
			GroovyObject instance = groovyDefClass.getInstance();
		
			//if you need connection to query, then you can test in DEV env first. For local support connection testing, please contact developer.
			ConnectionForTester testDBConn = new ConnectionForTester();
			conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
			String appSessionId = System.currentTimeMillis() + "";
			String[] runtimeParameters = new String[]{
					"B2BSessionID="+appSessionId,
					"OriginalSourceFileName="+(new File(testInputFileNamePath)).getName(), 
					"MSG_REQ_ID=TEST-MSG-REQ-ID-0011", 
					"TP_ID=OOCLLOGISTICS",
					"MSG_TYPE_ID=BL",
					"DIR_ID=O"};
			
			Object[] invokeParams = new Object[] { LocalFileUtil.readBigFile(testInputFileNamePath), runtimeParameters, conn };
			
			String output = instance==null?null:(String) instance.invokeMethod("mapping", invokeParams);
		
			long end = Calendar.getInstance().getTimeInMillis();

			println(">>"+(new File(ediMappingGroovyScriptFileNamePath)).getName()+", cost: "+(end-start)+" ms, result:");

			if(output.equals("")){
				System.out.println(cs.b2b.core.common.session.B2BRuntimeSession.getSessionValue(appSessionId, "PROMOTE_SESSION_BIZKEY"));
			}else {
				println(formatXml(output));
			}
//			String appSchemaZipBase64 = LocalFileUtil
//					.readBigFileContentDirectlyToBase64("./demo/schema/CS2-Schema-Files-Zip.zip");
//			
//			ValidateXML vm = new ValidateXML();
//			
//			String result1 = vm.freshAppSchemaToLocal("20181209205513", appSchemaZipBase64);
//			System.out.println("result1: "+result1);
//			
//			String result = vm.xmlValidation("CS2-CAR-BLXML", output);
//
//			System.out.println("-> " + result);
			
		} finally {
			//important, clean up to avoid memory leak
			if (groovyDefClass!=null) {
				groovyDefClass.close();
			}
		}
	}
	
}
