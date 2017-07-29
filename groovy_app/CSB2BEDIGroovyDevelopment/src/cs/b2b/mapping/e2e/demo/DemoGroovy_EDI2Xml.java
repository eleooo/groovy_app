package cs.b2b.mapping.e2e.demo;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import cs.b2b.beluga.api.BelugaApiUtil;
import cs.b2b.beluga.api.EDIProcessResult;
import cs.b2b.beluga.common.fileparser.UIFFileParser;
import cs.b2b.core.common.classloader.GroovyClassDefinition;
import cs.b2b.core.common.classloader.GroovyScriptHelper;
import cs.b2b.mapping.e2e.util.BelugaOceanHelper;
import cs.b2b.mapping.e2e.util.CommonUtil;
import cs.b2b.mapping.e2e.util.ConnectionForTester;
import cs.b2b.mapping.e2e.util.DemoBase;
import cs.b2b.mapping.e2e.util.LocalFileUtil;
import groovy.lang.GroovyObject;

public class DemoGroovy_EDI2Xml extends DemoBase {

	//1, provide your test file here
	//static String testInputFileNamePath = "D:\\_1\\___test\\heaven_si\\1-6-Looping.edi-20170411133535476-15";//"./demo/SI_DUMMY_D99B/1_OECLOGSHA_F000626707[1].TXT.MBR.txt";
	static String testInputFileNamePath = "./demo/SI_DUMMY_D99B/2_OECLOGSHA_baseline.xml_CMDU.edi";
	
	//2, all mapping scripts
	//2.1, EDI JavaBean script 
	static String javaBeanCommonScriptFile = "./src/cs/b2b/core/mapping/bean/edi/edifact/d99b/IFTMIN/EDI_IFTMIN.groovy";//"./src/cs/b2b/core/mapping/bean/edi/edifact/d99b/IFTMIN/EDI_IFTMIN.groovy";
	//2.2, Mapping Util library script file, if not use it, then not need to provide here
	static String mappingUtilScriptFile = "./src/cs/b2b/core/mapping/util/MappingUtil.groovy";
	//2.3, message type common groovy script
	static String mappingUtilMessageTypeCommonScriptFile = "./src/cs/b2b/core/mapping/util/MappingUtil_SI_I_Common.groovy";
	//2.4, pmt groovy script
	//static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_IFTMIN_CS2SIXML_HAVEN.groovy"; 
	static String pmtMappingScriptFile = "./src/cs/b2b/mapping/scripts/CUS_IFTMIN_CS2SIXML_INTTRACUSSID99B.groovy";
	
	/**
	 * Important !!!!!
	 * 
	 * As incoming EDI will translate to CS2XML, for CS2 xml schema validation, please get the zip file from git:
	 *     D:\git\b2b_cs_customers\common\applications\kukri\src\03_java_projects\CSB2BEDIGroovyDevelopment\cs2_schema_validation
	 *     
	 * and then unzip the file to : C:\home\tibco\B2BEDI\, and rename the CS2-Schema-Files-Zip to appchema
	 * the final folder is : C:\home\tibco\B2BEDI\appchema, and you can see the file : C:\home\tibco\B2BEDI\appchema\b2b_validation_config.txt
	 * then schema is ready for local validation now
	 *    
	 * if your project is locate at D driver, then please copy schema to D:\home\tibco\B2BEDI\appchema
	 * 
	 */
	
	public void demo() throws Exception {
		
		String[] runtimeParameters = new String[]{
				"AppSessionID="+System.currentTimeMillis(), 
				"OriginalSourceFileName="+new File(testInputFileNamePath).getName(),
				"MSG_REQ_ID=EDI-TEST-002-DUMMY",
				//for BelugaOcean EDI setting, use PMT info in table table B2B_EDI_TP_LBC_SETTING
				"TP_ID=OECLOGSHA", //OECLOGSHA HAVEN
				"MSG_TYPE_ID=SI", 
				"DIR_ID=I",
				"MSG_FMT_ID=EDIFACT"
		};
		
		//Mapping Part
		GroovyClassDefinition groovyDefClass = null;
		Connection conn = null;
		try {
			long start = Calendar.getInstance().getTimeInMillis();
			
			//20161222 david
			LinkedHashMap<String, String> scripts = new LinkedHashMap<String, String>();
			if (javaBeanCommonScriptFile!=null && javaBeanCommonScriptFile.length()>0) {
				File edibeanFile = new File(javaBeanCommonScriptFile);
				if (edibeanFile.exists()) {
					scripts.put(edibeanFile.getName(), LocalFileUtil.readBigFile(edibeanFile.getAbsolutePath()));
				}
			}
			scripts.put(new File(mappingUtilScriptFile).getName(), LocalFileUtil.readBigFile(mappingUtilScriptFile));
			if (mappingUtilMessageTypeCommonScriptFile!=null && mappingUtilMessageTypeCommonScriptFile.length()>0) {
				File typecommonFile = new File(mappingUtilMessageTypeCommonScriptFile);
				if (typecommonFile.exists()) {
					scripts.put(typecommonFile.getName(), LocalFileUtil.readBigFile(typecommonFile.getAbsolutePath()));
				}
			}
			scripts.put(new File(pmtMappingScriptFile).getName(), LocalFileUtil.readBigFile(pmtMappingScriptFile));
			
			groovyDefClass = GroovyScriptHelper.getClassDef(scripts);
			GroovyObject instance = groovyDefClass.getInstance();
			
			//after compile, start to works
			
			File testInputFile = new File(testInputFileNamePath);
			String testInputFileBody = LocalFileUtil.readBigFile(testInputFile.getAbsolutePath());
			
			//db connection if you need to use
			ConnectionForTester testDBConn = new ConnectionForTester();
			conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
			
			//20170301 - definition updated to get from db setting - b2b_edi_beluga_cfg
			BelugaOceanHelper bhelper = new BelugaOceanHelper();
			bhelper.getBelugaOceanDefinitionSettingStr(runtimeParameters, conn);
			String definitionBody = bhelper.getDefinitionBody();
			if (! CommonUtil.isEmpty(definitionBody)) {
				UIFFileParser parser = new UIFFileParser();
				EDIProcessResult ediResult = parser.edi2xml(testInputFileBody, definitionBody);
				testInputFileBody = ediResult.outputString==null?"":ediResult.outputString.toString();
				
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
				if (CommonUtil.isEmpty(testInputFileBody)) {
					return;
				}
			}
			
			println("testInputFileBody: "+testInputFileBody);
			
			Object[] invokeParams = new Object[] { testInputFileBody, runtimeParameters, conn };
			
			String output = instance==null?null:(String) instance.invokeMethod("mapping", invokeParams);
		
			long end = Calendar.getInstance().getTimeInMillis();
		
			println(">>"+(new File(pmtMappingScriptFile)).getName()+", cost: "+(end-start)+" ms, result:");
			//println(formatXml(output));
			println(output);
			println("---------finished----------");
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
