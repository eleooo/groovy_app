package cs.b2b.mapping.e2e.demo;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.LinkedHashMap;

import cs.b2b.beluga.common.fileparser.UIFFileParser;
import cs.b2b.core.common.classloader.GroovyClassDefinition;
import cs.b2b.core.common.classloader.GroovyScriptHelper;
import cs.b2b.mapping.e2e.util.BelugaOceanHelper;
import cs.b2b.mapping.e2e.util.ConnectionForTester;
import cs.b2b.mapping.e2e.util.DemoBase;
import cs.b2b.mapping.e2e.util.LocalFileUtil;
import groovy.lang.GroovyObject;

public class MyDemoGroovy_Xml2EDI2 extends DemoBase {

	//1, provide your test file here
	static String testInputFileNamePath = "./demo/CT_DSGOOGS/in_DSGOODS_CS020.xml";
	
	//2, all mapping scripts
	//2.1, mapping JavaBean common script
	static String javaBeanCommonScriptFile = "./src/cs/b2b/core/mapping/bean/MessageBeanCS2XmlCommon.groovy";
	//2.2, mapping JavaBean message type script
	static String javaBeanMessageTypeScriptFile = "./src/cs/b2b/core/mapping/bean/ct/MessageBeanCS2XmlCT.groovy";
	//2.3, Mapping Util library script file, if not use it, then not need to provide here
	static String mappingUtilScriptFile = "./src/cs/b2b/core/mapping/util/MappingUtil.groovy";
	//2.4, message type common groovy script
	static String mappingUtilMessageTypeCommonScriptFile = "./src/cs/b2b/core/mapping/util/MappingUtil_CT_O_Common.groovy";
	//2.5, pmt groovy script
	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_CS2CTXML_315_DSGOODS.groovy";
	
	//Beluga 1.0 definition for EDI 850 AgeGroup
	static String definitionFileForOutputXML = "./demo/IG_Definition/CUS_4010_315_CS.xml";
	
	
	public void demo() throws Exception {
		//db connection if you need to use
		ConnectionForTester testDBConn = new ConnectionForTester();
		Connection conn = null;
		
		String[] runtimeParameters = new String[]{
				"AppSessionID="+System.currentTimeMillis(), 
				"OriginalSourceFileName="+new File(testInputFileNamePath).getName(), 
				"SendPortID=N/A", 
				"PortProperty=N/A", 
				"MSG_REQ_ID=EDI-TEST-001",
				//for BelugaOcean EDI header handling, use PMT info in table table B2B_EDI_TP_LBC_SETTING
				"TP_ID=DSGOODS", 
				"MSG_TYPE_ID=CT", 
				"DIR_ID=O"};
		
		//Mapping Part
		GroovyClassDefinition groovyDefClass = null;
		try {
			String testInputFileBody = LocalFileUtil.readBigFile(testInputFileNamePath);
			
			conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
			Object[] invokeParams = new Object[] { testInputFileBody, runtimeParameters, conn };
			
			long start = Calendar.getInstance().getTimeInMillis();
			
			//20161222 david
			LinkedHashMap<String, String> scripts = new LinkedHashMap<String, String>();
			//1, put message type javabean common script
			scripts.put(new File(javaBeanCommonScriptFile).getName(), LocalFileUtil.readBigFile(javaBeanCommonScriptFile));
			//2, put message type javabean message script
			scripts.put(new File(javaBeanMessageTypeScriptFile).getName(), LocalFileUtil.readBigFile(javaBeanMessageTypeScriptFile));
			//3, put general mapping util script
			scripts.put(new File(mappingUtilScriptFile).getName(), LocalFileUtil.readBigFile(mappingUtilScriptFile));
			//4, put message type common script
			scripts.put(new File(mappingUtilMessageTypeCommonScriptFile).getName(), LocalFileUtil.readBigFile(mappingUtilMessageTypeCommonScriptFile));
			//5, put pmt mapping script
			scripts.put(new File(pmtMappingScriptFile).getName(), LocalFileUtil.readBigFile(pmtMappingScriptFile));
			
			groovyDefClass = GroovyScriptHelper.getClassDef(scripts);
			
			GroovyObject instance = groovyDefClass.getInstance();
		
			String output = instance==null?null:(String) instance.invokeMethod("mapping", invokeParams);
		
			long end = Calendar.getInstance().getTimeInMillis();
			
			if (definitionFileForOutputXML!=null && definitionFileForOutputXML.length()>0) {
				String definitionXml = LocalFileUtil.readBigFile(definitionFileForOutputXML);
				
				//20170109, david, add belugaocean handle edi header part
				BelugaOceanHelper belugaHelper = new BelugaOceanHelper();
				belugaHelper.getBelugaOceanDefinitionSettingStr(runtimeParameters, conn);
				
				UIFFileParser parser = new UIFFileParser();
				output = parser.parseXML2EDIWithConfig(output, definitionXml, belugaHelper.getBelugaOceanConfigSettingStr());
				
				//20170109, david, add beluga handle edi control number
				output = belugaHelper.setEDICtrlNumber(output, conn);
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
