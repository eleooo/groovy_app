package cs.b2b.testing.reconci.tools.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class FunctionHelperQA {
	
	static Logger logger = Logger.getLogger(FunctionHelperQA.class.getName());
	
	public static String readContent(File file) {
		
		String body = null;
		try {
			body = LocalFileUtilQA.readBigFileContentDirectly(file.getAbsolutePath());
		} catch (Exception e) {
			logger.error(">>> readContent - Unknow fatal exception, plesae contact DEV to check, with your log.");
			logger.error("FunctionHelperQA.readContent", e);
			logger.error("file: "+file.getAbsolutePath());
			System.exit(1);
		}
		return body;
	}
	
	public static boolean checkInColumnList(List list, String key) {
		boolean inFlag = false;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).toString().toLowerCase().equals(
					key.toLowerCase().trim())) {
				inFlag = true;
				break;
			}
		}
		return inFlag;
	}
	
	public static boolean prettyFormatXML(String xmlFilePath) {
		/*File f1= new File(xmlFilePath);
		String fileContent = FunctionHelperQA.readContent(f1);
		fileContent = fileContent.replaceAll("\r\n", "");
		fileContent = fileContent.replaceAll("\n\r", "");
		fileContent = fileContent.replaceAll("\n", "");
		fileContent = fileContent.replaceAll("\r", "");
		logger.info(fileContent);*/
		try {
			FileReader fr;
			fr = new FileReader(new File(xmlFilePath));
			SAXReader reader = new SAXReader();

		//	Document document = reader.read(new ByteArrayInputStream(fileContent.getBytes()));
			Document document = reader.read(fr);
			fr.close();

			 OutputFormat format = OutputFormat.createCompactFormat(); // ����XML�ĵ������ʽ
             format.setEncoding("UTF-8"); // ����XML�ĵ��ı�������             

			XMLWriter output = new XMLWriter(new FileWriter(new File(
					xmlFilePath)), format);

			output.write(document);
			output.close();
			prettyFormatXML2(xmlFilePath);
		} catch (Exception e) {
			logger.error("FunctionHelperQA.prettyFormatXML", e);
			logger.error("xmlFilePath: "+xmlFilePath);
			return false;
		}
		return true;
	}
	
	public static boolean prettyFormatXML2(String xmlFilePath) {
		/*File f1= new File(xmlFilePath);
		String fileContent = FunctionHelperQA.readContent(f1);

		logger.info(fileContent);*/
		try {
			FileReader fr;
			fr = new FileReader(new File(xmlFilePath));
			SAXReader reader = new SAXReader();

		//	Document document = reader.read(new ByteArrayInputStream(fileContent.getBytes()));
			Document document = reader.read(fr);
			fr.close();

			 OutputFormat format = OutputFormat.createPrettyPrint(); // ����XML�ĵ������ʽ
             format.setEncoding("UTF-8"); // ����XML�ĵ��ı�������             

			XMLWriter output = new XMLWriter(new FileWriter(new File(
					xmlFilePath)), format);

			output.write(document);
			output.close();

		} catch (Exception e) {
			logger.error("FunctionHelperQA.prettyFormatXML2", e);
			logger.error("xmlFilePath2: "+xmlFilePath);
			return false;
		}
		return true;
	}
	
	public static boolean checkExcelAvailable(File excel) {
		if (!excel.renameTo(excel)) {
			logger.error("Auto-Log excel : " + excel.getAbsolutePath());
			logger
					.error("  Error! Please make sure your Auto-Log excel exists and it is closed !  ");

			return false;
		} else {
			return true;
		}

	}
	public static boolean backupFile(File file) {
		File backFile = new File(file.getParent()+"/Auto-Backup-" + file.getName());
		if (file.exists()) {
			try {
				FileUtils.copyFile(file, backFile);
				return true;
			} catch (Exception e) {
				logger.error("FunctionHelperQA.backupFile", e);
				logger.error("file: "+file.getAbsolutePath());
				return false;
			}
		} else {
			return false;
		}

	}
	
	public static boolean fileExist(int row, String filePath) {

		boolean existFlag = true;
		File file = new File(filePath);
		if ((!file.isFile()) || (!file.exists())) {
			logger.error("File doesn't exist: Row " + row
					+ " , file: " + filePath);

			existFlag = false;
			
		}		
		return existFlag;
	}
}
