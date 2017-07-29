package cs.b2b.testing.reconci.tools.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cs.b2b.testing.reconci.tools.model.DBResultRecordModelQA;


public class DBResultHandlerQA {
	
	private static Logger logger = Logger.getLogger(DBResultHandlerQA.class.getName());
	
	private static XSSFWorkbook wbQA = null;
	private static FileOutputStream fileOutQA = null;
	private static XSSFSheet sheetQA = null;
	private static InputStream fisQA = null;
	
	 public static void exportErrorMsgToExcel(String output, File actualDB){
		 String xml=null;
		 boolean isxml=true;
		 if(output.startsWith("I AM EXCEPTION")){
				xml=output.substring(output.indexOf("<?xml version="),output.length());
				System.out.println("xml:"+xml);
			}else if(output.startsWith("<?xml"))
				xml=output; 
			else{
				System.out.println("Can't parse the error msg to XML.");
				isxml=false;								 
			}
		 ArrayList<DBResultRecordModelQA> recordModelList = new ArrayList<DBResultRecordModelQA>();
		 if(isxml){
				try {
					Document document = DocumentHelper.parseText(xml);
					Element root = document.getRootElement();
					List list = root.elements();
					 for(Iterator its =  list.iterator();its.hasNext();){  
						 DBResultRecordModelQA recordModel=new DBResultRecordModelQA();
						 Element chileEle = (Element)its.next();   
						 System.out.println(chileEle.getName());
						 List listTrans = chileEle.elements();
						 for(Iterator itT =  listTrans.iterator();itT.hasNext();){  
							 Element chileEleT = (Element)itT.next();   
							 if(chileEleT.getName().equals("CarrierId")){
								 recordModel.setOCEAN_CARRIER_ID(chileEleT.getText());
							 }else if(chileEleT.getName().equals("CTEventTypeId")){
								 recordModel.setCT_EVENT_TYPE_ID(chileEleT.getText());
							 }else if(chileEleT.getName().equals("AppErrorReport")){
								 List listError = chileEleT.elements();
								 for(Iterator ite =  listError.iterator();ite.hasNext();){  
									 Element chileElError = (Element)ite.next();   
									 if(chileElError.getName().equals("Status")){
										 recordModel.setTXN_STS_ID(chileElError.getText());
									 }else if(chileElError.getName().equals("MsgCode")){
										 recordModel.setINT_ERR_CDE(chileElError.getText());
									 }else if(chileElError.getName().equals("Msg")){
										 recordModel.setINT_ERR_MSG(chileElError.getText());
									 }else if(chileElError.getName().equals("Severity")){
										 recordModel.setSEVERITY(chileElError.getText());
									 }
									 
								 }
							 }
						 }
						
						 
						 recordModelList.add(recordModel);
					 }
				} catch (DocumentException e) {
					logger.error(">>> exportErrorMsgToExcel - Unknow fatal exception, plesae contact DEV to check, with your log.");
					logger.error("exportErrorMsgToExcel: "+actualDB.getAbsolutePath(), e);
					logger.error("output: \r\n"+output);
					System.exit(-1);
				} 
		 }else{
			 DBResultRecordModelQA recordModel=new DBResultRecordModelQA();
			 recordModel.setOCEAN_CARRIER_ID("###");
			 recordModel.setCT_EVENT_TYPE_ID("###");
			 recordModel.setTXN_STS_ID("E");
			 recordModel.setINT_ERR_CDE("###");
			 if(output.length()>1000)
				 recordModel.setINT_ERR_MSG(output.substring(0,1000));
			 else
				 recordModel.setINT_ERR_MSG(output);
			 recordModel.setSEVERITY("###");
			 recordModelList.add(recordModel);
		 }
		
			
			if(recordModelList!=null&&recordModelList.size()>0){
				openExcel(actualDB);
				int rowNum=sheetQA.getLastRowNum();
				for(int i=0;i<recordModelList.size();i++){
					DBResultRecordModelQA recordModel=new DBResultRecordModelQA();
					recordModel=recordModelList.get(i);
					XSSFRow row = sheetQA.createRow(rowNum+i+1);
					if(recordModel.getID()!=null)
						row.createCell(0).setCellValue(recordModel.getID());
					if(recordModel.getMSG_REQ_ID()!=null)
						row.createCell(1).setCellValue(recordModel.getMSG_REQ_ID());
					
					if(recordModel.getOCEAN_CARRIER_ID()!=null)
						row.createCell(2).setCellValue(recordModel.getOCEAN_CARRIER_ID());
					
					if(recordModel.getINTERCHANGE_CTRL_NUM()!=null)
						row.createCell(3).setCellValue(recordModel.getINTERCHANGE_CTRL_NUM());
					if(recordModel.getGROUP_CTRL_NUM()!=null)
						row.createCell(4).setCellValue(recordModel.getGROUP_CTRL_NUM());
					if(recordModel.getTXN_CTRL_NUM()!=null)
						row.createCell(5).setCellValue(recordModel.getTXN_CTRL_NUM());
					
					if(recordModel.getCT_EVENT_TYPE_ID()!=null)
						row.createCell(6).setCellValue(recordModel.getCT_EVENT_TYPE_ID());
					if(recordModel.getUPDATED_BY()!=null)
						row.createCell(7).setCellValue(recordModel.getUPDATED_BY());
					if(recordModel.getTXN_STS_ID()!=null)
						row.createCell(8).setCellValue(recordModel.getTXN_STS_ID());
					if(recordModel.getINT_ERR_CDE()!=null)
						row.createCell(9).setCellValue(recordModel.getINT_ERR_CDE());
					if(recordModel.getINT_ERR_MSG()!=null)
						row.createCell(10).setCellValue(recordModel.getINT_ERR_MSG());
					if(recordModel.getSEVERITY()!=null)
						row.createCell(11).setCellValue(recordModel.getSEVERITY());

				}
				closeExcel(actualDB);
			}
		 
	 }
	 
	 private static void openExcel(File excel) {
			try {
				fisQA = new FileInputStream(excel);
				wbQA = new XSSFWorkbook(fisQA);

				sheetQA = wbQA.getSheetAt(0);
				// logger.info("Excel is opened !");
			} catch (Exception e) {
				logger.error(">>> openExcel - Unknow fatal exception, plesae contact DEV to check, with your log.");
				logger.error("openExcel: "+excel.getAbsolutePath(), e);
				System.exit(-1);
			}
		}

		private static void closeExcel(File excel) {
			try {
				fileOutQA = new FileOutputStream(excel);
				wbQA.write(fileOutQA);
				fileOutQA.close();
				// logger.info("Excel is closed !");
			} catch (Exception e) {
				logger.error(">>> closeExcel - exception found.");
				logger.error("closeExcel: "+excel.getAbsolutePath(), e);
			}

		}
		

}
