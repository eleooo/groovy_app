package cs.b2b.testing.reconci.tools.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cs.b2b.beluga.common.fileparser.UIFFileParser;
import cs.b2b.testing.reconci.tools.configure.CSB2BEDIConfigQA;
import cs.b2b.testing.reconci.tools.handler.DifferenceErrorMsgHandlerQA;
import cs.b2b.testing.reconci.tools.handler.DifferenceHandlerQA;
import cs.b2b.testing.reconci.tools.model.RecordModelQA;
import cs.b2b.testing.reconci.tools.util.FileProcessorQA;
import cs.b2b.testing.reconci.tools.util.FunctionHelperQA;
import cs.b2b.testing.reconci.tools.util.LocalFileUtilQA;
import groovy.lang.GroovyObject;

public class MappingThread extends Thread {

	private static Logger logger = Logger.getLogger(MappingThread.class.getName());

	private static String thisClassName = MappingThread.class.getName().substring(MappingThread.class.getName().lastIndexOf(".") + 1,
			MappingThread.class.getName().length());
	
	RecordModelQA recordModel = null;
	GroovyObject instance = null;
	Connection conn = null;
	String threadId = "";
	int workSeq = 0;
	
	long startTime = 0;
	
	boolean isRunning = false;
	boolean isWorking = false;
	
	public String getThreadId() {
		return threadId;
	}
	
	public int getWorkingSeq() {
		return workSeq;
	}
	
	public boolean getWorkingStatus() {
		return isWorking;
	}
	
	public void stopService() {
		isRunning = false;
		logger.info("Thread ("+threadId+") stopping .....");
	}
	
	public MappingThread(String tid, Connection conn) {
		this.threadId = tid;
		this.conn = conn;
	}
	
	//, Connection conn
	public void setRunningParams(int workSeq, RecordModelQA data, GroovyObject instance) {
		this.isWorking = true;
		
		this.recordModel = data;
//		this.definitionFilePath = definitionFilePath;
		this.instance = instance;
		this.workSeq = workSeq;
		
		logger.info("Thread ("+threadId+") ("+workSeq+") init.");
	}
	
	public void closeDBConnection() {
		if (conn!=null) {
			try { conn.close(); } catch (Exception ex) {}
			logger.info("Thread ("+threadId+") db connection closed.");
		}
	}
	
	public long getStartTs() {
		return startTime;
	}
	
	public void run() {
		isRunning = true;
		while (isRunning) {
			
			if (! isWorking) {
				try {
					Thread.sleep(1000);
				} catch (Exception ex) {}
				continue;
			}
			if (recordModel==null || instance==null || workSeq==0) {
				try {
					Thread.sleep(1000);
				} catch (Exception ex) {}
				continue;
			}
			
			try {
				logger.info("Thread ("+threadId+") ("+workSeq+") Input file row: "+recordModel.getRow() + ", File name is: "+recordModel.getInputFile());
				
				startTime = System.currentTimeMillis();
				
				String inputFilePath = CSB2BEDIConfigQA.InputDataFolder+"//"+recordModel.getInputFile();
				File testInputFile = new File(inputFilePath);
				boolean fail = false;
				
				String testInputFileBody = LocalFileUtilQA.readBigFile(testInputFile.getAbsolutePath());
				
				String B2BSessionID = UUID.randomUUID().toString();
				String error = "";
				
				if (CSB2BEDIConfigQA.DIR_ID.toLowerCase().startsWith("i")&&!CSB2BEDIConfigQA.inputFormat.equals("XML")) {
					logger.info("Incoming EDI start to parse with definition");
					try {
						
							String definitionBody = LocalFileUtilQA.readBigFile(DemoGroovy_Common_QA.definitionFilePath);
							//logger.info("definitionBody :"+definitionBody);
							UIFFileParser parser = new UIFFileParser();
							testInputFileBody = parser.parseEDI2XML(testInputFileBody, definitionBody);
						
							String warnStartKey = "*******Parser Warning Info********";
							String warnEndKey = "*******Parser Warning End*******";
							String warningInfo = "";
							if (testInputFileBody.contains(warnStartKey)) {
								warningInfo = testInputFileBody.substring(testInputFileBody.indexOf(warnStartKey), testInputFileBody.indexOf(warnEndKey)+warnEndKey.length());
								testInputFileBody = testInputFileBody.substring(testInputFileBody.indexOf(warnEndKey)+warnEndKey.length());
							}
							if (warningInfo!=null && warningInfo.length()>0)
								System.out.println("Beluga Warning Info for CompleteWithError: "+warningInfo);
					} catch (Exception e) {
						logger.error("("+threadId+") meet Exception in parsing edi to xml : ", e);
							fail = true;
							error = DemoGroovy_Common_QA.getErrorInfoFromException(e);
							if (error!="" || !error.equals("")){
								DemoGroovy_Common_QA.errorCount++;
								/*LocalFileUtilQA
								.writeToFile(
										CSB2BEDIConfigQA.ActualFailFolder + "/"
												+ testInputFile.getName(),
												error, false);*/
							}
							writeErrorToDBExcel(error, testInputFile, recordModel);
							logger.warn("errorCount:" + DemoGroovy_Common_QA.errorCount);
					}
				}
				
				// *** start mapping *** 
				
				long cp1 = System.currentTimeMillis();
				String output = "";
				
				try {
					if (! fail) {
						String[] runtimeParameters = new String[]{"B2BSessionID="+B2BSessionID, "B2B_OriginalSourceFileName="+testInputFile.getName(), "SendPortID=N/A", "PortProperty=N/A", "MSG_REQ_ID=EDI-TEST-001", "TP_ID="+CSB2BEDIConfigQA.TP_ID, "MSG_TYPE_ID="+CSB2BEDIConfigQA.MSG_TYPE_ID, "DIR_ID="+CSB2BEDIConfigQA.DIR_ID};
						Object[] invokeParams = new Object[] { testInputFileBody, runtimeParameters, conn };
						
						logger.info(">>> ("+threadId+"), ("+workSeq+") Start Run groovy at :"+(new Date()));
						
						long lstart = System.currentTimeMillis();
						
						output = instance==null?null:(String) instance.invokeMethod("mapping", invokeParams);
						
						long between = (System.currentTimeMillis() - lstart);
						
						logger.info(">>> ("+threadId+"), ("+workSeq+") Mapping cost : "+between +" ms.");
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//logger.error("Exception groovy mapping Error:", e1);
					//e1.printStackTrace();
					fail = true;
					error = DemoGroovy_Common_QA.getErrorInfoFromException(e1);
					logger.error("("+threadId+") meet Exception in groovy mapping : " + error);
					if (error!=null || error.length()>0) {
						DemoGroovy_Common_QA.errorCount++;
						/*LocalFileUtilQA
						.writeToFile(
								CSB2BEDIConfigQA.ActualFailFolder + "/"
										+ testInputFile.getName(),
										error, false);*/
						
					}
					writeErrorToDBExcel(error, testInputFile, recordModel);
//					System.out.println("Set unexpected Error to output.");
		//			System.out.println("*********************"+error);

					//finished current work
//					this.recordModel = null;
//					this.instance = null;
//					this.workSeq = 0;
//					isWorking = false;
//					continue;
				}
				
				output = output==null?"":output;
				
				if (fail == false) {
					//get session value
					String sessionValue = cs.b2b.core.common.session.B2BRuntimeSession.getSessionValue(B2BSessionID,"PROMOTE_SESSION_BIZKEY");
					sessionValue = (sessionValue==null?"":sessionValue);
				//	logger.info("output:"+output);
					if (output.trim().length()==0) {
						if (sessionValue.contains("AppErrorReport")) {
							System.out.println("Set sessionValue to output.");
							output = sessionValue;
		//					exception+=1;
							DemoGroovy_Common_QA.exceptionCount+=1;
							writeErrorToDBExcel(output, testInputFile, recordModel);
						}	
					}else if(output.contains("AppErrorReport")){
						System.out.println("Output contains Error.");
		//				exception+=1;
						DemoGroovy_Common_QA.exceptionCount+=1;
						writeErrorToDBExcel(output,testInputFile,recordModel);
					} else {
						if(CSB2BEDIConfigQA.DIR_ID.toLowerCase().startsWith("o") && !CSB2BEDIConfigQA.outputFormat.equals("XML")) {
							logger.info("Outgoing convert xml to EDI.");
							if (DemoGroovy_Common_QA.definitionFilePath!=null && DemoGroovy_Common_QA.definitionFilePath.length()>0) {
								String definitionXml = LocalFileUtilQA.readBigFile(DemoGroovy_Common_QA.definitionFilePath);
								UIFFileParser parser = new UIFFileParser();
								try {
									output = parser.parseXML2EDI(output, definitionXml); 
									DemoGroovy_Common_QA.successCount++;	
									
								} catch (Exception e) {
									System.out.println("---------------\r\nException in XML-to-EDI parser, input data is : \r\n" + output + "\r\n---------------");
									
									DemoGroovy_Common_QA.failCount++;
									fail = true;
									
									String berr = DemoGroovy_Common_QA.getErrorInfoFromException(e);
									logger.error("("+threadId+") meet Exception in BO - X2E, file name: " + testInputFile.getName() + " : " + berr);
									
									StringWriter sw = new StringWriter();
									PrintWriter pw = new PrintWriter(sw);
									StringBuffer sb = new StringBuffer();
									e.printStackTrace(pw);
									BufferedReader br = new BufferedReader(new StringReader(sw.toString()));
									
									String b = br.readLine();
									sb.append(b + "\r\n");
									for (String line = br.readLine(); line != null; line = br.readLine()) {
										if (line.contains(thisClassName)) {
											sb.append(line + "\r\n");
										}
									}
									
									output = sb.toString();
									writeErrorToDBExcel(output, testInputFile, recordModel);
									logger.info("Set unexpected Error to output.");
									
									br.close();
									pw.close();
									sw.close();
								}
							}
							if (fail == false) {
								String outputFileName = testInputFile.getName();
								recordModel.setActualFile(outputFileName);
								recordModel.setActualStatus("C");
								recordModel.setActualFilePath(CSB2BEDIConfigQA.ActualCompleteFolder + "/"
														+ outputFileName);
								LocalFileUtilQA
										.writeToFile(
												recordModel.getActualFilePath(),
												output, false);
								logger.info("successCount:" + DemoGroovy_Common_QA.successCount);
							}																		
						} else {
							DemoGroovy_Common_QA.successCount++;		
							String outputFileName = testInputFile.getName();
									recordModel.setActualFile(outputFileName);
									recordModel.setActualStatus("C");
									recordModel.setActualFilePath(CSB2BEDIConfigQA.ActualCompleteFolder + "/"
															+ outputFileName);
									LocalFileUtilQA
											.writeToFile(
													recordModel.getActualFilePath(),
													output, false);
									logger.info("successCount:" + DemoGroovy_Common_QA.successCount);
						}
					}
				}
				
				long compareStart = System.currentTimeMillis();
				compareResult(recordModel);
				long compareEnd = System.currentTimeMillis();
				if ((compareEnd - compareStart) > 1000) {
					logger.info(">>> "+threadId+", ("+workSeq+") Compare time: "+(compareEnd - compareStart)+" ms. ");
				}
				logger.info(">>> "+threadId+", ("+workSeq+") Mapping & Compare total cost: "+ (compareEnd-cp1) + " ms.");
				Thread.sleep(200);
			} catch (Exception ex) {
				String exTrace = DemoGroovy_Common_QA.getErrorInfoFromException(ex);
				logger.error(">>> "+threadId+", ("+workSeq+") Fatal Error found in MappingThread: \r\n"+exTrace);
				
				logger.error("Please contact DEV to check, with the above log for troubleshoot.");
				System.exit(-1);
			} finally {
				this.recordModel = null;
				this.instance = null;
				this.workSeq = 0;
				
				isWorking = false;
			}
		}
		logger.info(">>>>> "+threadId+" finished works, stopped.");
	}
	
	public void writeErrorToDBExcel(String errorMsg,File testInputFile , RecordModelQA recordModel) {
		try {
			LocalFileUtilQA
			.writeToFile(
					CSB2BEDIConfigQA.ActualFailFolder + "/"
							+ testInputFile.getName(),
							errorMsg, false);
		} catch (Exception e) {
			logger.error(">>> writeErrorToDBExcel - Unknow fatal exception, plesae contact DEV to check, with your log.");
			logger.error("MappingThread.writeErrorToDBExcel", e);
			System.exit(-1);
		}
	
		recordModel.setActualFail(testInputFile.getName());
		if(errorMsg!=null&&errorMsg.trim().startsWith("<?xml"))
			FunctionHelperQA.prettyFormatXML2(CSB2BEDIConfigQA.ActualFailFolder + "/"
				+ testInputFile.getName());
	
		recordModel.setActualStatus("E");
		
	}
	
	
	//
	
	//File excel, 
		public void compareResult(RecordModelQA recordModel){
			logger.warn("-------------------Start to compare---------------");
//			openExcel(excel);
//			for (int j = 0; j < recordModelList.size(); j++) {								
//				RecordModelQA recordModel = (RecordModelQA) recordModelList
//						.get(j);
//				recordModel.setExcel(excel);
//				recordModel.setSheet(sheetQA);
				logger.info("Compare row "+recordModel.getRow()+",input file is "+recordModel.getInputFile());
				logger.info("Actual status:"+recordModel.getActualStatus());
				if(!StringUtils.isEmpty(recordModel.getActualStatus())){
					logger.info("Compare status......");
					if(recordModel.getActualStatus().equals(recordModel.getExpectedStatus()))
						recordModel.setStatusResult("Pass");
					else
						recordModel.setStatusResult("Fail");
					logger.warn("Status result :"+recordModel.getStatusResult());
				}
				if(!StringUtils.isEmpty(recordModel.getActualFile())&&!StringUtils.isEmpty(recordModel.getExpectedFile())){
					String FileResult=compareFile(recordModel);
					recordModel.setFileResult(FileResult);
					
				}

				if(!StringUtils.isEmpty(recordModel.getStatusResult())&&recordModel.getStatusResult().equals("Pass")){
					if(!StringUtils.isEmpty(recordModel.getActualFail())&&!StringUtils.isEmpty(recordModel.getExpectedFail())){
						logger.info("Compare Error result......");
						
						String expectedDBPath=CSB2BEDIConfigQA.ExpectedFailFolder+"/"+recordModel.getExpectedFail();
						String actualDBPath=CSB2BEDIConfigQA.ActualFailFolder +"/"+ recordModel.getActualFail();
						logger.info("expectedDBPath:"+expectedDBPath);
						logger.info("actualDBPath="+actualDBPath);

						compareErrorMsg(recordModel);
											
						logger.warn("DB result :"+recordModel.getDbresult());
					}			
				}else{
					logger.info("Status result is null or Pass , no need to compare Error msg.");
				}
				
//			}
//			closeExcel(excel);		
		}
		
		public void compareErrorMsg(RecordModelQA recordModel){
			File expectedDBPath=new File(CSB2BEDIConfigQA.ExpectedFailFolder+"/"+recordModel.getExpectedFail());
			File actualDBPath=new File(CSB2BEDIConfigQA.ActualFailFolder +"/"+ recordModel.getActualFail());
			String fileContentExp = FunctionHelperQA.readContent(expectedDBPath);
			if(fileContentExp!=null&&fileContentExp.trim().startsWith("<?xml"))
				FunctionHelperQA.prettyFormatXML(expectedDBPath.getAbsolutePath());
			String fileContentAct = FunctionHelperQA.readContent(actualDBPath);
			if(fileContentAct!=null&&fileContentAct.trim().startsWith("<?xml")){
				FunctionHelperQA.prettyFormatXML(actualDBPath.getAbsolutePath());
			}
			String expectedStr = FunctionHelperQA.readContent(expectedDBPath);
			String actualStr =FunctionHelperQA.readContent(actualDBPath);
			if (expectedStr.equals(actualStr)) {
				recordModel.setDbresult("Pass");
			}else{
				recordModel.setDbresult("Fail");				
				DifferenceErrorMsgHandlerQA differenceHandlerError = new DifferenceErrorMsgHandlerQA();
				if((expectedStr!=null&&fileContentExp.trim().startsWith("<?xml"))
						&&actualStr!=null&&actualStr.trim().startsWith("<?xml")){
				

					differenceHandlerError.logDifference(CSB2BEDIConfigQA.InputDataFolder+"/"+recordModel
									.getInputFile(), CSB2BEDIConfigQA.ExpectedFailFolder+"/"+recordModel.getExpectedFail(),
									CSB2BEDIConfigQA.ActualFailFolder +"/"+ recordModel.getActualFail(), 
									expectedStr,actualStr, recordModel.getExcel());
					
				}else{
					expectedStr = "Unexpected Error msg in expected Error file .Please check by manual .";
					actualStr="Unexpected Error msg in actual Error file .Please check by manual .";
					differenceHandlerError.logDifference(CSB2BEDIConfigQA.InputDataFolder+"/"+recordModel
							.getInputFile(), CSB2BEDIConfigQA.ExpectedFailFolder+"/"+recordModel.getExpectedFail(),
							CSB2BEDIConfigQA.ActualFailFolder +"/"+ recordModel.getActualFail(), 
							expectedStr,actualStr, recordModel.getExcel());
				}
			}
				
		}
		
		public String compareFile(RecordModelQA recordModel) {
			String FileResult=null;
			logger.info("Compare output file......");
			try {
				if(CSB2BEDIConfigQA.outputFormat.equals("XML")){
					FunctionHelperQA.prettyFormatXML2(recordModel.getExpectedFilePath());
					FunctionHelperQA.prettyFormatXML2(recordModel.getActualFilePath());
				}
				String expectedStr = FileProcessorQA.getComparableContent(
						new File(recordModel.getExpectedFilePath()),CSB2BEDIConfigQA.outputFormat);
				String actualStr = FileProcessorQA.getComparableContent(new File(
						recordModel.getActualFilePath()),
						CSB2BEDIConfigQA.outputFormat);
			//	logger.info("expectedStr:"+expectedStr);
			//	logger.info("actualStr:"+actualStr);
				if (expectedStr.equals(actualStr)) {
					//recordModel.setFileResult("Pass");
					FileResult="Pass";
					logger.warn("File result : Pass");
				} else {
					//recordModel.setFileResult("Fail");
					FileResult="Fail";
					logger.warn("File result : Fail");
					DifferenceHandlerQA differenceHandler = new DifferenceHandlerQA();

					differenceHandler.logDifference(CSB2BEDIConfigQA.InputDataFolder+"/"+recordModel
									.getInputFile(), recordModel
									.getExpectedFilePath(), recordModel
									.getActualFilePath(), expectedStr,
							actualStr, recordModel.getExcel());
				}
				
			} catch (IOException e) {
				logger.error(">>> compareFile - Unknow fatal exception, plesae contact DEV to check, with your log.");
				logger.error("MappintThread.compareFile", e);
				logger.error("recordModel: "+recordModel.getExpectedStatus()+" v.s. "+recordModel.getActualStatus()+", expected file: "+recordModel.getExpectedFile());
				System.exit(-1);
			}
			
			return FileResult;

		}
}
