package cs.b2b.core.common.xmlvalidation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.util.XMLErrorHandler;

import cs.b2b.core.common.util.Base64Util;
import cs.b2b.core.common.util.LocalFileUtil;

public class ValidateXML {

	static String configFileName = "b2b_validation_config.txt";
	static String xsdRootPath = "C:/home/tibco/B2BEDI/appchema/";

	static long lastModifiedTimestampConfigFile = 0;
	static long lastConfigFileCheckPoint = 0;

	static Properties msgtypeProps = new Properties();

	static String VALIDATION_NO_NEED = "Validation No Need.";
	static String VALIDATION_SUCCESS = "Validation Success.";
	static String VALIDATION_FAIL = "Validation Failure.";
	static long lastFileCheckTs = -1;
	static SimpleDateFormat sdfTsFmt = new SimpleDateFormat("yyyyMMddHHmmss");
	static TimeZone tzGmt8 = TimeZone.getTimeZone("GMT+8");

	//if invoker help to provide schema full path or not, in 2.0, Alpaca will help to provide schema full path 
	static boolean isSchemaFileNamePropertyProvided = false;
	static Properties schemaProperties = null;
	
	/**
	 * only be called by Alpaca - kukri wrapper now, while kukri 1.5 will not call this method
	 * Alpaca pass schema properties to script to call validation 
	 * @param sprop
	 */
	public static void setSchemaPathProperty(Properties sprop) {
		if (sprop != null && sprop.size()>0) {
			isSchemaFileNamePropertyProvided = true;
			schemaProperties = sprop;
		}
	}
	
	public String freshAppSchemaToLocal(String schemaFileLastUpdatedTs,
			String schemaFilesZipBase64) {
		if (schemaFileLastUpdatedTs == null || schemaFileLastUpdatedTs.trim().length() == 0)
			return VALIDATION_FAIL
					+ " Cannot find schema zip file last updated timestamp for local refresh. ";
		if (schemaFilesZipBase64 == null
				|| schemaFilesZipBase64.trim().length() == 0)
			return VALIDATION_FAIL
					+ " Cannot find schema zip base64 string to refresh local. ";
		long dbLastUpdatedTs = 0;
		try {
			dbLastUpdatedTs = Long.parseLong(schemaFileLastUpdatedTs);
		} catch (Exception ex) {
			return VALIDATION_FAIL + " invalid schema file last updated timestamp: "+schemaFileLastUpdatedTs + ". ";
		}
		if (dbLastUpdatedTs==0) {
			return VALIDATION_FAIL + " invalid schema file last updated timestamp: 0. ";
		}
		
		File fconfig = new File(xsdRootPath + configFileName);
		FileOutputStream out = null;
		BufferedOutputStream bufferOut = null;
		BufferedInputStream binaryIn = null;
		ZipInputStream zipIn = null;
		try {
			//schemaFileLastUpdateTs format: yyyy-MM-dd'T'
			long localConfigTs = 0; 
			if (fconfig.exists()) {
				Calendar cfgCal = Calendar.getInstance(tzGmt8);
				cfgCal.setTimeInMillis(fconfig.lastModified());
				sdfTsFmt.setTimeZone(tzGmt8);
				localConfigTs = Long.parseLong(sdfTsFmt.format(cfgCal.getTime()));
			}
			
			//2 set of checking, in case the TZ impact to caused compare failure.
			if (localConfigTs < dbLastUpdatedTs && lastFileCheckTs != dbLastUpdatedTs) {
				// upload to local if not exists
				// refresh if version upgrade
				
				byte[] zipBytes = Base64Util
						.getBytesFromBASE64(schemaFilesZipBase64);
				zipIn = new ZipInputStream(new ByteArrayInputStream(zipBytes));
				binaryIn = new BufferedInputStream(zipIn);

				ZipEntry entry;
				while ((entry = zipIn.getNextEntry()) != null) {
					File fout = new File(xsdRootPath, entry.getName());
					if (entry.isDirectory()) {
						if (!fout.exists()) {
							fout.mkdirs();
						}
						continue;
					} else {
						if (!fout.exists()) {
							(new File(fout.getParent())).mkdirs();
						}
					}
					
					out = new FileOutputStream(fout);
					bufferOut = new BufferedOutputStream(out);
					int b;
					while ((b = binaryIn.read()) != -1) {
						bufferOut.write(b);
					}
					bufferOut.close();
					out.close();
				}
				
				lastFileCheckTs = dbLastUpdatedTs;
			}
		} catch (Exception e) {
			return VALIDATION_FAIL
					+ " Exception in local schema files refresh: "
					+ e.toString();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception ex) {
				}
			}
			if (bufferOut != null) {
				try {
					bufferOut.close();
				} catch (Exception ex) {
				}
			}
			if (binaryIn != null) {
				try {
					binaryIn.close();
				} catch (Exception ex) {
				}
			}
			if (zipIn != null) {
				try {
					zipIn.close();
				} catch (Exception ex) {
				}
			}
		}
		return "";
	}

	public String xmlValidation(String sendMessageType, String xmlBody) {
		if (isSchemaFileNamePropertyProvided) {
			//call by 2.0 Alpaca
			return xmlValidation2dot0AlpacaImpl(sendMessageType, xmlBody);
		} else {
			//call by 1.5 kukri app
			return xmlValidation1dot0Impl(sendMessageType, xmlBody);
		}
	}
	
	private String xmlValidation2dot0AlpacaImpl(String sendMessageType, String xmlBody) {
		if (sendMessageType == null || sendMessageType.trim().length() == 0 || schemaProperties==null) {
			return VALIDATION_NO_NEED;
		}
		try {
			sendMessageType = sendMessageType.trim();
			String trgXsdPath = schemaProperties.getProperty(sendMessageType);
			if (trgXsdPath == null || trgXsdPath.trim().length() == 0) {
				return VALIDATION_NO_NEED;
			}

			File trgXsdFile = new File(trgXsdPath);
			if (!trgXsdFile.exists()) {
				return VALIDATION_FAIL + " Cannot find out schema for: "
						+ trgXsdFile + ". ";
			}

			return validateXMLByXSDFilePath(trgXsdPath, xmlBody);
			
		} catch (Exception ex) {
			String errInfo = VALIDATION_FAIL + "\r\nException in "
					+ sendMessageType + " validation, details: "
					+ ex.toString() + ". ";
			return errInfo;
		}
	}
	
	private String xmlValidation1dot0Impl(String sendMessageType, String xmlBody) {
		if (sendMessageType == null || sendMessageType.trim().length() == 0) {
			return VALIDATION_NO_NEED;
		}
		try {
			// check interval = 10 mins
			if ((System.currentTimeMillis() - lastConfigFileCheckPoint) > 600000) {
				File fconfig = new File(xsdRootPath + configFileName);
				if (!fconfig.exists()) {
					return VALIDATION_FAIL
							+ " Cannot find out app schema validation config file: "
							+ fconfig + ". ";
				}
				if (fconfig.lastModified() != lastModifiedTimestampConfigFile) {
					// load config
					InputStream in = null;
					try {
						in = new FileInputStream(fconfig.getAbsolutePath());
						msgtypeProps.load(in);
					} catch (Exception ex) {
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (Exception exclose) {
							}
						}
					}
					lastConfigFileCheckPoint = System.currentTimeMillis();
				}
			}
			sendMessageType = sendMessageType.trim();
			String trgXsdPath = msgtypeProps.getProperty(sendMessageType);
			if (trgXsdPath == null || trgXsdPath.trim().length() == 0) {
				return VALIDATION_NO_NEED;
			}

			trgXsdPath = xsdRootPath + trgXsdPath;
			File trgXsdFile = new File(trgXsdPath);
			if (!trgXsdFile.exists()) {
				return VALIDATION_FAIL + " Cannot find out schema for: "
						+ trgXsdFile + ". ";
			}

			return validateXMLByXSDFilePath(trgXsdPath, xmlBody);
		} catch (Exception ex) {
			String errInfo = VALIDATION_FAIL + "\r\nException in "
					+ sendMessageType + " validation, details: "
					+ ex.toString() + ". ";
			return errInfo;
		}
	}

	/**
	 * XSD XML Schema validate xml
	 */
	private String validateXMLByXSDFilePath(String xsdFileName, String xmlBody)
			throws Exception {

		StringBuilder validateInfo = new StringBuilder();
		XMLErrorHandler errorHandler = new XMLErrorHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// Set need to validate XML
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		SAXParser parser = factory.newSAXParser();
		SAXReader xmlReader = new SAXReader();
		// get xml document
		Document xmlDocument = (Document) xmlReader
				.read(new ByteArrayInputStream(xmlBody
						.getBytes(getEncodingInFile(xmlBody))));
		// can find property in
		// [url]http://sax.sourceforge.net/?selected=get-set[/url]
		parser.setProperty(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				XMLConstants.W3C_XML_SCHEMA_NS_URI);
		parser.setProperty(
				"http://java.sun.com/xml/jaxp/properties/schemaSource", "file:"
						+ xsdFileName);

		// SAXValidator
		SAXValidator validator = new SAXValidator(parser.getXMLReader());
		// Can get error info from ErrorHandler
		validator.setErrorHandler(errorHandler);
		validator.validate(xmlDocument);

		if (errorHandler.getErrors() != null
				&& errorHandler.getErrors().hasContent()) {
			Element errele = errorHandler.getErrors();// .asXML().toString();
			for (int i = 0; errele.elements() != null
					&& i < errele.elements().size(); i++) {
				if (i == 0) {
					validateInfo.append(VALIDATION_FAIL).append(" \r\n");
				}
				Element e = (Element) errele.elements().get(i);
				validateInfo.append(i + 1).append("), ")
						.append(e.getTextTrim()).append(";; ");
			}
		} else {
			validateInfo.append(VALIDATION_SUCCESS);
		}

		return validateInfo.toString();
	}

	private String getEncodingInFile(String body) {
		String retCoding = "UTF-8";
		if (body != null && body.length() > 60) {
			String header = body.substring(0, 60);

			String codekey = "encoding=\"";
			int position = header.toLowerCase().indexOf(codekey);

			if (position > 0) {
				String encodingInFile = header.substring(position
						+ codekey.length());
				encodingInFile = encodingInFile.substring(0,
						encodingInFile.indexOf("\""));
				if (encodingInFile != null
						&& encodingInFile.trim().length() > 0) {
					retCoding = encodingInFile;
				}
			}
		}
		return retCoding;
	}
	
	
	public static void main(String[] args) {
		try {
			String sendMessageType = "bcxml1dot0";
			String xmlBody = LocalFileUtil.readBigFile("D:/1_B2BEDI_Revamp/BC/OUT_XML/OOCLLOGISTICS/schema_56.0.xml");
			
			//test 1.0 method
			ValidateXML validator1dot0 = new ValidateXML();
			String result1dot0 = validator1dot0.xmlValidation(sendMessageType, xmlBody);
			System.out.println("--------1dot0-1--------");
			System.out.println(result1dot0);
			System.out.println("-----------------");
			
			result1dot0 = validator1dot0.xmlValidation("CUS-BCXML-OLL_STD", xmlBody);
			System.out.println("--------1dot0-2--------");
			System.out.println(result1dot0);
			System.out.println("-----------------");
				
			
			Properties props = new Properties();
			props.setProperty("bcxml1dot0", "d:/home/tibco/B2BEDI/appchema/com/b2b/customer/bc/XML/CS-SYSINT-STD-IG-BC.XML.xsd");
			ValidateXML.setSchemaPathProperty(props);
			ValidateXML validator = new ValidateXML();
		
			String result = validator.xmlValidation(sendMessageType, xmlBody);
			
			//System.out.println(xmlBody);
			System.out.println("-----------------");
			System.out.println("--------1--------");
			System.out.println(result);
			System.out.println("-----------------");
			
			sendMessageType = "bcxml1dot0";
			xmlBody = LocalFileUtil.readBigFile("D:/1_B2BEDI_Revamp/BC/OUT_XML/OOCLLOGISTICS/schema_56.0_invalid.xml");
		
			result = validator.xmlValidation(sendMessageType, xmlBody);
			
			//System.out.println(xmlBody);
			System.out.println("-----------------");
			System.out.println("-------2---------");
			System.out.println(result);
			System.out.println("-----------------");
			
			ValidateXML.setSchemaPathProperty(null);
			xmlBody = LocalFileUtil.readBigFile("D:/1_B2BEDI_Revamp/BC/OUT_XML/OOCLLOGISTICS/schema_56.0_invalid.xml");
			result = validator.xmlValidation(sendMessageType, xmlBody);
			System.out.println("-------3---------");
			System.out.println(result);
			System.out.println("-----------------");
			
			
			ValidateXML.setSchemaPathProperty(new Properties());
			xmlBody = LocalFileUtil.readBigFile("D:/1_B2BEDI_Revamp/BC/OUT_XML/OOCLLOGISTICS/schema_56.0_invalid.xml");
			result = validator.xmlValidation(sendMessageType, xmlBody);
			System.out.println("-------4---------");
			System.out.println(result);
			System.out.println("-----------------");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}