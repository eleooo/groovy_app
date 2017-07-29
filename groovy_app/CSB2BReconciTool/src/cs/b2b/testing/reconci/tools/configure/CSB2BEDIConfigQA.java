package cs.b2b.testing.reconci.tools.configure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import cs.b2b.testing.reconci.tools.demo.DemoGroovy_Common_QA;
import cs.b2b.testing.reconci.tools.model.PMTRecordModel;

public class CSB2BEDIConfigQA {
	// Folder
	public static String folderPath;
	public static String testInputFileNamePath;

	// mappingScript config
	public static String rootPath;
	public static String InputDataFolder;
	public static String ActualCompleteFolder;
	public static String ActualFailFolder;
	public static String ExpectedFailFolder;
	public static String ExpectedUnknowErrorFolder;
	public static String ExpectedCompleteFolder;
	
	public static String mappingrootPath;
	public static String pmtMappingScriptFile;
	public static String mappingUtilScriptFile;
	public static String javaBeanCommonScriptFile;
	public static String javaBeanMessageTypeScriptFile;
	public static String mappingUtilMessageTypeCommonScriptFile;
	public static String definitionFilePath;
	public static String TP_ID;
	public static String MSG_TYPE_ID;
	public static String DIR_ID;
	public static String expect;

	// log
	public static String logPath;
	public static String groovyErrorLog;
	public static String belugaErrorLog;

	// compare use
	
	//public static String groovyOutputFolder;
	public static String modelPath;
	public static String defaultExtensionName;
	//public static String groovyExceptionFolder;
	//public static String groovyErrorFolder;
	//public static String groovyObsoleteFolder;
	public static String ModelFileFolder;
	// format
	public static String inputFormat;
	public static String outputFormat;
	// filename match
	public static int startFileNameIndex;
	public static int endFileNameIndex;
	// delimiter
	public static String recordDelimiter;
	public static String elementDelimiter;
	public static String subElementDelimiter;
	public static String escapeCharacter;
	// edi ignoreField
	public static String xmlIngoreProperties;
	public static String ediIngoreProperties;
	public static ArrayList<String> ignoredList = null;
	public static ArrayList<String> specialReplaceList = null;
	public static ArrayList<String> specialRemoveList = null;
	
	public static String DBSID;
	public static String DBIP;
	
	private static Logger logger = Logger.getLogger(CSB2BEDIConfigQA.class.getName());
	
//	public static void main(String[] args) throws Exception {
//		initalization();
//		System.out.println(ediIngoreProperties);
//	}

	public static void initalization(PMTRecordModel pmtrecordModel) throws Exception {
		
		//rootPath = "D:/B2BEDIWorkingFolder"+"/"+pmtrecordModel.getPmtPath();	
		rootPath = pmtrecordModel.getPmtPath();	
		TP_ID = pmtrecordModel.getTpId();
		MSG_TYPE_ID = pmtrecordModel.getMsg_type();
		DIR_ID = pmtrecordModel.getDir();
		InputDataFolder = rootPath + "/InputData";
		ExpectedCompleteFolder = rootPath+"/ExpectedComplete";

		ActualCompleteFolder=rootPath + "/ActualComplete";
		ActualFailFolder=rootPath + "/ActualFail";
		ExpectedFailFolder=rootPath + "/ExpectedFail";
		ExpectedUnknowErrorFolder=rootPath + "/ExpectedUnknowError";
		ModelFileFolder = "../CSB2BReconciTool/WorkingConfig/ModelFile";
		
		inputFormat = pmtrecordModel.getInputFormat();
		outputFormat = pmtrecordModel.getOutputFormat();
		// read mapping config
		System.out.println("rootPath: "+rootPath);
		System.out.println("TP_ID: "+TP_ID + " msg_type:"+MSG_TYPE_ID+" dir_id: "+DIR_ID);
		File F=new File("../CSB2BReconciTool/WorkingConfig/MappingConfig");
		System.out.println(MSG_TYPE_ID+"_"+DIR_ID+"_"+TP_ID+"_"+"mapping.properties");
		File f1 = new File("../CSB2BReconciTool/WorkingConfig/MappingConfig/"+MSG_TYPE_ID+"_"+DIR_ID+"_"+TP_ID+"_"+"mapping.properties");
		Configuration mappingScriptProperties = new PropertiesConfiguration(f1);
						
		mappingrootPath = mappingScriptProperties.getProperty("mappingScript.mappingrootPath").toString();
		javaBeanCommonScriptFile = mappingrootPath+mappingScriptProperties.getProperty("mappingScript.javaBeanCommonScriptFile").toString();
		javaBeanMessageTypeScriptFile = mappingrootPath+mappingScriptProperties.getProperty("mappingScript.javaBeanMessageTypeScriptFile").toString();
		mappingUtilScriptFile = mappingrootPath+mappingScriptProperties.getProperty("mappingScript.mappingUtilScriptFile").toString();
		mappingUtilMessageTypeCommonScriptFile = mappingrootPath+mappingrootPath+mappingScriptProperties.getProperty("mappingScript.mappingUtilMessageTypeCommonScriptFile").toString();
		pmtMappingScriptFile = mappingrootPath+mappingScriptProperties.getProperty("mappingScript.pmtMappingScriptFile").toString();
		definitionFilePath = mappingrootPath+mappingScriptProperties.getProperty("mappingScript.definitionFilePath").toString();
					
		expect = mappingScriptProperties.getProperty("mappingScript.expect").toString();
		
		DBSID = mappingScriptProperties.getProperty("DBSID").toString();
		DBIP = mappingScriptProperties.getProperty("DBIP").toString();
		// read compare config
		File f2 = new File("../CSB2BReconciTool/src/compare.properties");
		Configuration compareProperties = new PropertiesConfiguration(f2);
		modelPath = compareProperties.getProperty("compare.modelPath").toString();
		defaultExtensionName = compareProperties.getProperty("compare.defaultExtensionName").toString();
		startFileNameIndex = Integer.parseInt(compareProperties.getProperty("compare.startFileNameIndex").toString());
		endFileNameIndex = Integer.parseInt(compareProperties.getProperty("compare.endFileNameIndex").toString());
		subElementDelimiter = compareProperties.getProperty("compare.subElementDelimiter").toString();
		escapeCharacter = compareProperties.getProperty("compare.escapeCharacter").toString();
		if (compareProperties.getProperty("compare.xmlIngoreProperties") != null) {
			xmlIngoreProperties = compareProperties.getProperty("compare.xmlIngoreProperties").toString();
		} 
		if (compareProperties.getProperty("compare.ediIngoreProperties") != null) {
			ediIngoreProperties = compareProperties.getProperty("compare.ediIngoreProperties").toString();
		}

		File acf =new File(ActualCompleteFolder);
		File af =new File(ActualFailFolder);

		if (!acf.exists()) {
			acf.mkdirs();
		} 
		if (!af.exists()) {
			af.mkdirs();
			
		} 
		
	}
	
	public static void initFileFormat(String inputFileName){
		
		File df = new File(InputDataFolder);
		File bof = new File(ExpectedCompleteFolder);
		BufferedReader br;

			File df1 = new File(InputDataFolder+"/"+inputFileName);
			try {
				br = new BufferedReader(new FileReader(df1));
				String r = br.readLine();

				if (r.contains("xml")) {
					inputFormat = "XML";
					br.close();
				} else if (r.contains("ISA") || r.contains("UNB") || r.contains("UNA") || r.contains("ST")) {
					inputFormat = "EDI";
					br.close();
					/*if (r.contains("ISA")) {
						elementDelimiter = String.valueOf(r.charAt(3));
						recordDelimiter = String.valueOf(r.charAt(r.length() - 1));
					}else if(r.contains("ST")){
						elementDelimiter = String.valueOf(r.charAt(2));
						recordDelimiter = String.valueOf(r.charAt(r.length() - 1));
					}
					else if (r.contains("UNB")) {
						elementDelimiter = String.valueOf(r.charAt(3));
						recordDelimiter = String.valueOf(r.charAt(r.length() - 1));
					} else if (r.contains("UNA")) {
						elementDelimiter = String.valueOf(r.charAt(3));
						recordDelimiter = String.valueOf(r.charAt(r.length() - 1));
					}*/

				} else {
					inputFormat = "UIF";
					br.close();
				}
			} catch (Exception e) {
				logger.error(">>> initFileFormat - Unknow exception, plesae contact DEV to check, with your log.");
				logger.error("CSB2BEDIConfigQA.initFileFormat1", e);
				System.exit(-1);
			}
			if (bof.isDirectory()) {
				File of=new File(bof.list()[0]);
				File bof1=null;
				if(of.getName().equals("readme.txt")){
					bof1 = new File(ExpectedCompleteFolder + "/" + bof.list()[1]);
				}else{
					bof1 = new File(ExpectedCompleteFolder + "/" + bof.list()[0]);
				}
				
				try {
					br = new BufferedReader(new FileReader(bof1));
					String r = br.readLine();
					 Configuration config = null;
					if (r.contains("xml")) {
						outputFormat = "XML";
						config = new PropertiesConfiguration(CSB2BEDIConfigQA.xmlIngoreProperties);
						ignoredList = (ArrayList) config.getList("xml.ingoreCompareField");
						specialReplaceList = (ArrayList) config.getList("xml.specialReplace");
						specialRemoveList = (ArrayList) config.getList("xml."+CSB2BEDIConfigQA.MSG_TYPE_ID+".specialRemove");
						br.close();
					} else if (r.contains("ISA") || r.contains("UNB") || r.contains("UNA") || r.contains("ST")) {
						outputFormat = "EDI";
						 config = new PropertiesConfiguration(CSB2BEDIConfigQA.ediIngoreProperties);
						 ignoredList = (ArrayList) config.getList("edi.ingoreCompareField");
						 specialReplaceList = (ArrayList) config.getList("edi.specialReplace");
						 specialRemoveList = (ArrayList) config.getList("edi."+CSB2BEDIConfigQA.MSG_TYPE_ID+".specialRemove");
						br.close();
						if (r.contains("ISA")) {
							elementDelimiter = String.valueOf(r.charAt(3));
							recordDelimiter = String.valueOf(r.charAt(r.length() - 1));
						}else if(r.contains("ST")){
							elementDelimiter = String.valueOf(r.charAt(2));
							recordDelimiter = String.valueOf(r.charAt(r.length() - 1));
						} else if (r.contains("UNB")) {
							elementDelimiter = String.valueOf(r.charAt(3));
							recordDelimiter = String.valueOf(r.charAt(r.length() - 1));
						} else if (r.contains("UNA")) {
							elementDelimiter = String.valueOf(r.charAt(3));
							recordDelimiter = String.valueOf(r.charAt(r.length() - 1));
						}
					} else {
						outputFormat = "UIF";
						br.close();
					}
				} catch (Exception e) {
					logger.error(">>> initFileFormat2 - Unknow exception, plesae contact DEV to check, with your log.");
					logger.error("CSB2BEDIConfigQA.initFileFormat2", e);
					System.exit(-1);
				}
			}
		
		
	}
}
