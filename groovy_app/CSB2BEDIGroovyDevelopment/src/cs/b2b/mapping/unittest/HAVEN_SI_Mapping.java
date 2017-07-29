package cs.b2b.mapping.unittest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.LinkedHashMap;

import org.junit.Test;

import cs.b2b.beluga.common.fileparser.UIFFileParser;
import cs.b2b.core.common.classloader.GroovyClassDefinition;
import cs.b2b.core.common.classloader.GroovyScriptHelper;
import cs.b2b.mapping.e2e.util.ConnectionForTester;
import cs.b2b.mapping.e2e.util.LocalFileUtil;
import cs.b2b.mapping.e2e.util.TestBase;
import groovy.lang.GroovyObject;

public class HAVEN_SI_Mapping extends TestBase {

	@Test
	public void test() throws Exception {
		
		String testInputFileNamePath = "./demo/SI_DUMMY_D99B/2_baseline-newIG.txt";
		String ediMappingGroovyScriptFileNamePath = "./src/cs/b2b/mapping/scripts/CUS_IFTMIN_CS2SIXML_CSCUSSID99B.groovy";
		String mappingUtilScriptFileNamePath = "./src/cs/b2b/core/mapping/util/MappingUtil.groovy";
		String mappingMsgUtilScriptFileNamePath = "./src/cs/b2b/core/mapping/util/MappingUtil_SI_I_Common.groovy";
		String definitionFilePath = "./IG_Definition/CUS_D99B_IFTMIN_CS.xml";
		
		ConnectionForTester testDBConn = new ConnectionForTester();
		Connection conn = null;
		
		File testInputFile = new File(testInputFileNamePath);
		String testInputFileBody = LocalFileUtil.readBigFile(testInputFile.getAbsolutePath());
		
		File mappingEDIGroovyFile = new File(ediMappingGroovyScriptFileNamePath);
		String mappingGroovyScriptBody = LocalFileUtil.readBigFile(mappingEDIGroovyFile.getAbsolutePath());
		
		//get mapping util script here
		String mappingUtilScriptBody = LocalFileUtil.readBigFile(mappingUtilScriptFileNamePath);
		String mappingMsgUtilScriptBody = LocalFileUtil.readBigFile(mappingMsgUtilScriptFileNamePath);
		
		//EDI Parsing Part
		String definitionBody = LocalFileUtil.readBigFile(definitionFilePath);
		UIFFileParser parser = new UIFFileParser();
		testInputFileBody = parser.parseEDI2XML(testInputFileBody, definitionBody);
		
		String[] runtimeParameters = new String[]{"AppSessionID="+System.currentTimeMillis(), "OriginalSourceFileName="+testInputFile.getName(), "SendPortID=N/A", "PortProperty=N/A", "MSG_REQ_ID=EDI2017031006110320-47", "TP_ID=HAVEN", "MSG_TYPE_ID=SI", "DIR_ID=I"};
		conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
		Object[] invokeParams = new Object[] { testInputFileBody, runtimeParameters, conn };
		
		GroovyClassDefinition groovyDefClass = null;
		String output = null;
		try {
			long start = Calendar.getInstance().getTimeInMillis();
			
			//20161222 david
			LinkedHashMap<String, String> scripts = new LinkedHashMap<String, String>();
			//put general common script
			scripts.put(new File(mappingUtilScriptFileNamePath).getName(), mappingUtilScriptBody);
			//put msg common script
			scripts.put(new File(mappingUtilScriptFileNamePath).getName(), mappingMsgUtilScriptBody);
			//put pmt script
			scripts.put(mappingEDIGroovyFile.getName(), mappingGroovyScriptBody);
			
			groovyDefClass = GroovyScriptHelper.getClassDef(scripts);
			GroovyObject instance = groovyDefClass.getInstance();
		
			output = instance==null?null:(String) instance.invokeMethod("mapping", invokeParams);
		
			long end = Calendar.getInstance().getTimeInMillis();
			println(output);
			println(">>"+mappingEDIGroovyFile.getName()+", cost: "+(end-start)+" ms, result:");
			println(formatXml(output));
			
		} finally {
			//important, clean up to avoid memory leak
			if (groovyDefClass!=null) {
				groovyDefClass.close();
			}
		}
		assertTrue(output!=null && output.length()>100);
	}

}
