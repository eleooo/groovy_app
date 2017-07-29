package cs.b2b.mapping.e2e.demo;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.LinkedHashMap;

import cs.b2b.beluga.common.fileparser.UIFFileParser;
import cs.b2b.core.common.classloader.GroovyClassDefinition;
import cs.b2b.core.common.classloader.GroovyScriptHelper;
import cs.b2b.mapping.e2e.util.ConnectionForTester;
import cs.b2b.mapping.e2e.util.DemoBase;
import cs.b2b.mapping.e2e.util.LocalFileUtil;
import groovy.lang.GroovyObject;

public class MyDemoGroovy_EDI2Xml2 extends DemoBase {

	//1, provide your test file here
	static String testInputFileNamePath = "./demo/SI_DUMMY_D99B/big.txt";
	
	//2, testing groovy script file
	static String ediMappingGroovyScriptFileNamePath = "./src/cs/b2b/mapping/scripts/CUS_IFTMIN_CS2SIXML_DUMMY.groovy";
	
	static String messageTypeCommonScriptFileNamePath = "";//"./src/cs/b2b/core/mapping/util/MappingUtil_CT_O_Common.groovy";
	
	//3, Mapping Util library script file, if not use it, then not need to provide here
	static String mappingUtilScriptFileNamePath = "./src/cs/b2b/core/mapping/util/MappingUtil.groovy";
		
	//Beluga 1.0 definition for EDI 850 AgeGroup
	static String definitionFilePath = "./demo/SI_DUMMY_D99B/CUS_D99B_IFTMIN_CS.xml";
	
	
	public void demo() throws Exception {
		//db connection if you need to use
		ConnectionForTester testDBConn = new ConnectionForTester();
		Connection conn = null;
		
		File testInputFile = new File(testInputFileNamePath);
		String testInputFileBody = LocalFileUtil.readBigFile(testInputFile.getAbsolutePath());
		
		File mappingEDIGroovyFile = new File(ediMappingGroovyScriptFileNamePath);
		String mappingGroovyScriptBody = LocalFileUtil.readBigFile(mappingEDIGroovyFile.getAbsolutePath());
		
		//get mapping util script here
		String mappingUtilScriptBody = LocalFileUtil.readBigFile(mappingUtilScriptFileNamePath);
		
		//EDI Parsing Part
		String definitionBody = LocalFileUtil.readBigFile(definitionFilePath);
		UIFFileParser parser = new UIFFileParser();
		testInputFileBody = parser.parseEDI2XML(testInputFileBody, definitionBody);
		
		LocalFileUtil util = new LocalFileUtil();
		util.writeToFile("big.xml", testInputFileBody, false);
		
		String warnStartKey = "*******Parser Warning Info********";
		String warnEndKey = "*******Parser Warning End*******";
		String warningInfo = "";
		if (testInputFileBody.contains(warnStartKey)) {
			warningInfo = testInputFileBody.substring(testInputFileBody.indexOf(warnStartKey), testInputFileBody.indexOf(warnEndKey)+warnEndKey.length());
			testInputFileBody = testInputFileBody.substring(testInputFileBody.indexOf(warnEndKey)+warnEndKey.length());
		}
		if (warningInfo!=null && warningInfo.length()>0)
			System.out.println("Beluga Warning Info for CompleteWithError: "+warningInfo);
		
		String[] runtimeParameters = new String[]{"AppSessionID="+System.currentTimeMillis(), "OriginalSourceFileName="+testInputFile.getName(), "SendPortID=N/A", "PortProperty=N/A", "MSG_REQ_ID=EDI-TEST-001", "TP_ID=NA", "MSG_TYPE_ID=NA", "DIR_ID=NA"};
		
		//Mapping Part
		GroovyClassDefinition groovyDefClass = null;
		try {
			conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
			Object[] invokeParams = new Object[] { testInputFileBody, runtimeParameters, conn };
			
			long start = Calendar.getInstance().getTimeInMillis();
			
			//20161222 david
			LinkedHashMap<String, String> scripts = new LinkedHashMap<String, String>();
			//put general common script
			scripts.put(new File(mappingUtilScriptFileNamePath).getName(), mappingUtilScriptBody);
			//put pmt script
			scripts.put(mappingEDIGroovyFile.getName(), mappingGroovyScriptBody);
			
			
			groovyDefClass = GroovyScriptHelper.getClassDef(scripts);
			GroovyObject instance = groovyDefClass.getInstance();
		
			String output = instance==null?null:(String) instance.invokeMethod("mapping", invokeParams);
		
			long end = Calendar.getInstance().getTimeInMillis();
		
			println(">>"+mappingEDIGroovyFile.getName()+", cost: "+(end-start)+" ms, result:");
			println(formatXml(output));
			
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
