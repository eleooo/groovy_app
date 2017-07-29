package cs.b2b.testing.reconci.tools.demo;

import cs.b2b.testing.reconci.tools.configure.CSB2BEDIConfigQA;
import cs.b2b.testing.reconci.tools.model.PMTRecordModel;
import cs.b2b.testing.reconci.tools.util.FileProcessorQA;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;


public class GroovyMappingLocalE2EQA {
	private static Logger logger = Logger.getLogger(GroovyMappingLocalE2EQA.class.getName());
	public static byte[] mapUtilBytes = null;
	public static String mapUtilName = null;
	private static XSSFWorkbook wb = null;
	private static FileOutputStream fileOut = null;
	private static XSSFSheet sheet = null;
	private static InputStream fis = null;
	
	static int workingThreadNumber = 30;

	/**
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		logger.info("E2E Start, pre-setting workingThreadNumber asking: "+workingThreadNumber);
		
		long tsStart = System.currentTimeMillis();
		try {
			//System.out.println(System.getProperty("user.dir"));
			//Read runing TP info from ./WorkingConfig/config.txt
			File f = new File("../CSB2BReconciTool/WorkingConfig/config.txt");
			String workFolder=FileProcessorQA.getWorkFolder(f);
			logger.info("workFolder: "+workFolder);
			File workF=new File(workFolder);
			if(workF.exists()&&workF.isDirectory()){
				File f1 = new File(workFolder+"/pmt_config.txt");
				ArrayList<String> runList = FileProcessorQA.getRunTPList(f1);
				if(runList!=null){
					for (int i=0;i<runList.size();i++) {
						String runLine = runList.get(i);
						if (runLine==null || runLine.trim().length()==0 || runLine.startsWith("##")) {
							continue;
						}
						logger.info("==================\r\nTesting with line : " + runLine+"\r\n====================");
						PMTRecordModel pmtrecordModel = new PMTRecordModel(runLine);
						CSB2BEDIConfigQA.initalization(pmtrecordModel);
						String excelPath=CSB2BEDIConfigQA.rootPath+"/"+CSB2BEDIConfigQA.TP_ID+".xlsx";
						File excel = new File(excelPath);
						logger.info("inputFormat : " + CSB2BEDIConfigQA.inputFormat);
						logger.info("outputFormat : " + CSB2BEDIConfigQA.outputFormat);
						File dataFolder= new File(CSB2BEDIConfigQA.rootPath);
				
						if(dataFolder.exists()&&dataFolder.isDirectory()){
							if(!excel.exists()){
								logger.info("Prepare test caseexcel first" );
								if(prepareTestExcel(excel)){
									DemoGroovy_Common_QA demoQA = new DemoGroovy_Common_QA(workingThreadNumber);
									demoQA.demo(excel);
								}else{
									logger.info("Some expected not found ,please check ." );
								}
							} else {
								DemoGroovy_Common_QA demoQA = new DemoGroovy_Common_QA(workingThreadNumber);
								demoQA.demo(excel);
							}
						} else {
							logger.error("Error: Not found "+CSB2BEDIConfigQA.rootPath);
						}
					}
				}
			}else{
				logger.error("Not found workFolder!! Please check your ./WorkingConfig/config.txt .");
			}
			
			long tsFinish = System.currentTimeMillis();
			
			logger.info("E2E Finished. Total cost: "+(tsFinish-tsStart)+" ms.");
			
		} catch (Exception e) {
			Throwable t = e.getCause() == null ? e : e.getCause();
			while (t.getCause() != null) {
				t = t.getCause();
			}
			t.printStackTrace();
			
			logger.error(">>> GroovyMappingLocalE2EQA.Main - Unknow exception, plesae contact DEV to check, with your log.");
			logger.error("Unexpected error and exit, please check ............");
			
		}
	}
	
	public static boolean prepareTestExcel(File excel){
		boolean flag=true;
		File modelF=new File(CSB2BEDIConfigQA.ModelFileFolder+"/Auto-Log-Model.xlsx");
		if(modelF.exists()){
			try {
				FileUtils.copyFile(modelF, excel);
				logger.info("Satrt to fill test data to excel.");
				File inputF= new File(CSB2BEDIConfigQA.InputDataFolder);
				openExcel(excel);
				int rowC=sheet.getLastRowNum();
				logger.info(rowC);
				if(rowC>1){
					for(int n=rowC;n>0;n--)
					{
						logger.info("n="+n);
						sheet.removeRow(sheet.getRow(n));
					}
				}
				closeExcel(excel);
				File[] inputFiles=inputF.listFiles();  
				openExcel(excel);
				int row=0;
				logger.info("inputFiles.length="+inputFiles.length);
				for(int i=0;i<inputFiles.length;i++){
					File f=inputFiles[i];
					if(f.exists()&&f.isFile()&&!f.getName().equals("readme.txt")){
						row=1+i;
						sheet.createRow(row);
						XSSFCell runflag = sheet.getRow(row).createCell(0);
						runflag.setCellValue("t");
						/*XSSFCell InputPath = sheet.getRow(row).createCell(1);
						InputPath.setCellValue("/InputData");*/
						XSSFCell InputFile = sheet.getRow(row).createCell(1);
						InputFile.setCellValue(f.getName());
						XSSFCell expectedStatus = sheet.getRow(row).createCell(2);
						XSSFCell expectedFile = sheet.getRow(row).createCell(3);
						XSSFCell expectedFail = sheet.getRow(row).createCell(4);
						File of=new File(CSB2BEDIConfigQA.ExpectedCompleteFolder+"/"+f.getName());
						File ff=new File(CSB2BEDIConfigQA.ExpectedFailFolder+"/"+f.getName());
						File uf=new File(CSB2BEDIConfigQA.ExpectedUnknowErrorFolder+"/"+f.getName());

						if(of.exists()){
							expectedFile.setCellValue(f.getName());
							expectedStatus.setCellValue("C");
						//	flag=true;
						}else if(ff.exists()){
							expectedFail.setCellValue(f.getName());
							expectedStatus.setCellValue("E");
						//	flag=true;
						}else if(uf.exists()){
						//	expectedFail.setCellValue(f.getName());
							expectedStatus.setCellValue("E");
							
						}else{
							logger.info("Not found expected for "+f.getName());
							flag=false;
							expectedStatus.setCellValue("E");
						}
					}
				}
				closeExcel(excel);
				
			} catch (IOException e) {
				logger.error(">>> GroovyMappingLocalE2EQA.prepareTestExcel - Unexpected fatal exception, plesae contact DEV to check, with your log.");
				logger.error("GroovyMappingLocalE2EQA.prepareTestExcel", e);
				System.exit(-1);
			}
		}
		
		return flag;
	}
	
	private static void openExcel(File excel) {
		try {
			fis = new FileInputStream(excel);
			wb = new XSSFWorkbook(fis);

			sheet = wb.getSheetAt(0);
			// logger.info("Excel is opened !");
		} catch (Exception e) {
			logger.error(">>> GroovyMappingLocalE2EQA.openExcel - Unexpected fatal exception, plesae contact DEV to check, with your log.");
			logger.error("GroovyMappingLocalE2EQA.openExcel", e);
			System.exit(-1);
		}
	}

	private static void closeExcel(File excel) {
		try {
			fileOut = new FileOutputStream(excel);
			wb.write(fileOut);
			fileOut.close();
			// logger.info("Excel is closed !");
		} catch (FileNotFoundException e) {
			logger.error("Encounter exception : " + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("Encounter exception : " + e.getMessage(), e);
		}
	}
}
