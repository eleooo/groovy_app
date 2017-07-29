package cs.b2b.mapping.unittest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.LinkedHashMap;

import org.junit.Test;

import cs.b2b.core.common.classloader.GroovyClassDefinition;
import cs.b2b.core.common.classloader.GroovyScriptHelper;
import cs.b2b.mapping.e2e.util.ConnectionForTester;
import cs.b2b.mapping.e2e.util.LocalFileUtil;
import cs.b2b.mapping.e2e.util.TestBase;
import groovy.lang.GroovyObject;

public class DSGOODS_CT_Mapping extends TestBase {

	@Test
	public void test() throws Exception {
		
		String beanCommonFilePath = "./src/cs/b2b/core/mapping/bean/MessageBeanCS2XmlCommon.groovy";
		String ctBeanFilePah = "./src/cs/b2b/core/mapping/bean/ct/ContainerMovement.groovy";
		
		String mappingUtilScriptFileNamePath = "./src/cs/b2b/core/mapping/util/MappingUtil.groovy";
		String mappingMsgTypeCommonScriptFilePath = "./src/cs/b2b/core/mapping/util/MappingUtil_CT_O_Common.groovy";
		String ediMappingGroovyScriptFileNamePath = "./src/cs/b2b/mapping/scripts/CUS_CS2CTXML_315_DSGOODS.groovy";
		
		String testInputFileNamePath = "./demo/CT_DSGOOGS/CE_EDI2017021510125208-47.in.xml";
		
		ConnectionForTester testDBConn = new ConnectionForTester();
		Connection conn = null;
		
		File testInputFile = new File(testInputFileNamePath);
		String testInputFileBody = LocalFileUtil.readBigFile(testInputFile.getAbsolutePath());
		
		File mappingEDIGroovyFile = new File(ediMappingGroovyScriptFileNamePath);
		String mappingGroovyScriptBody = LocalFileUtil.readBigFile(mappingEDIGroovyFile.getAbsolutePath());
		
		//get mapping util script here
		String mappingUtilScriptBody = LocalFileUtil.readBigFile(mappingUtilScriptFileNamePath);
		
		String[] runtimeParameters = new String[]{"AppSessionID="+System.currentTimeMillis(), "OriginalSourceFileName="+testInputFile.getName(), "SendPortID=N/A", "PortProperty=N/A", "MSG_REQ_ID=EDI2016081507050979-10", "TP_ID=DSGOODS", "MSG_TYPE_ID=CT", "DIR_ID=O"};
		conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
		Object[] invokeParams = new Object[] { testInputFileBody, runtimeParameters, conn };
		
		GroovyClassDefinition groovyDefClass = null;
		String output = null;
		try {
			long start = Calendar.getInstance().getTimeInMillis();
			
			//20161222 david
			LinkedHashMap<String, String> scripts = new LinkedHashMap<String, String>();
			scripts.put(new File(beanCommonFilePath).getName(), LocalFileUtil.readBigFile(beanCommonFilePath));
			scripts.put(new File(ctBeanFilePah).getName(), LocalFileUtil.readBigFile(ctBeanFilePah));
			//put general common script
			scripts.put(new File(mappingUtilScriptFileNamePath).getName(), mappingUtilScriptBody);
			scripts.put(new File(mappingMsgTypeCommonScriptFilePath).getName(), LocalFileUtil.readBigFile(mappingMsgTypeCommonScriptFilePath));
			
			//put pmt script
			scripts.put(mappingEDIGroovyFile.getName(), mappingGroovyScriptBody);
			
			groovyDefClass = GroovyScriptHelper.getClassDef(scripts);
			GroovyObject instance = groovyDefClass.getInstance();
		
			output = instance==null?null:(String) instance.invokeMethod("mapping", invokeParams);
		
			long end = Calendar.getInstance().getTimeInMillis();
			println(output);
			println(">>"+mappingEDIGroovyFile.getName()+", cost: "+(end-start)+" ms, result:");
			//println(formatXml(output));
			
		} finally {
			//important, clean up to avoid memory leak
			if (groovyDefClass!=null) {
				groovyDefClass.close();
			}
		}
		assertTrue(output!=null && output.length()>100);
	}

}
