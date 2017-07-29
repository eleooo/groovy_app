package cs.b2b.testing.reconci.tools.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cs.b2b.testing.reconci.tools.configure.CSB2BEDIConfigQA;
import cs.b2b.testing.reconci.tools.util.FunctionHelperQA;



public class DifferDBHandlerQA {
	private static Logger logger = Logger.getLogger(DifferDBHandlerQA.class.getName());
	private XSSFWorkbook diffDB;
	private static String excelDBcmplogPath = null;
	
	public boolean compareExcel(File autoexcel, String input, String expectedStatus, String actualStatus, String expectDBFile, String actDBFile) {
		boolean allresultsame = true;

		try {
			FileInputStream fos1 = new FileInputStream(new File(expectDBFile));
			FileInputStream fos2 = new FileInputStream(new File(actDBFile));

			//logger.info("Compare DB :" + expectDBFile + "," + actDBFile);
			
			XSSFWorkbook wbExpected = new XSSFWorkbook(fos1);
			XSSFWorkbook wbActual = new XSSFWorkbook(fos2);

			// // whether two xls have different sheet number
		//	allresultsame = compareSheetCountAndName(wbExpected, wbActual);
			int wbExpectedSheetCount = wbExpected.getNumberOfSheets();
			int wbActualSheetCount = wbActual.getNumberOfSheets();

			for (int i = 0; i < wbExpectedSheetCount; i++) {
				XSSFSheet expectedSheet = wbExpected.getSheetAt(i);
				String sheetName = expectedSheet.getSheetName();
				if(!sheetName.equals("b2b_transaction")){
					continue;
				}
				XSSFSheet actualSheet;

				
				if ((actualSheet = wbActual.getSheet(sheetName)) == null){
					logger
							.info("Sheet "
									+ sheetName
									+ " doesn't exist in actual DB file, will ignore comparing this sheet.");
				} else {
					if (!compareSheet(expectedSheet, actualSheet)) {
						allresultsame = false;
					}
				}

			}
			if(!allresultsame){
				XSSFRichTextString expdbmsg = new XSSFRichTextString();
				XSSFRichTextString actdbmsg = new XSSFRichTextString() ;
								
			//	logDBcmpDifference(autoexcel,input,expectedStatus,actualStatus);
				logDBcmpDifference(autoexcel,input,expectedStatus,actualStatus);
								
				for(int j=0;j<wbExpectedSheetCount;j++){
					XSSFSheet expectedSheet = wbExpected.getSheetAt(j);
					String sheetName = expectedSheet.getSheetName();
					XSSFSheet actualSheet;
					if(!sheetName.equals("b2b_transaction")){
						continue;
					}
					if ((actualSheet = wbActual.getSheet(sheetName)) == null) {
						
						expdbmsg.setString("Exist");
						actdbmsg.setString("Missing");
											
						writeDBcmpInfo(sheetName,expdbmsg,actdbmsg);
						
					} else {
						compareTable(expectedSheet, actualSheet);
					}
				}
				
				for(int j=0;j<wbActualSheetCount;j++){
					XSSFSheet actualSheet = wbActual.getSheetAt(j);
					String sheetName = actualSheet.getSheetName();
					XSSFSheet expectedSheet;
					if(!sheetName.equals("b2b_transaction")){
						continue;
					}
					if((expectedSheet = wbExpected.getSheet(sheetName)) == null){
						expdbmsg.setString("Missing");
						actdbmsg.setString("Exist");
						
						writeDBcmpInfo(sheetName,expdbmsg,actdbmsg);
					}
				}		
								
				
			}

		} catch (Exception e) {
			
			logger.error("autoexcel: "+autoexcel.getAbsolutePath());
			logger.error("expectedStatus: "+expectedStatus+", actualStatus: "+actualStatus);
			logger.error("expectDBFile: "+expectDBFile+", actDBFile: "+actDBFile);
			
			logger.error(">>> DifferDBHandlerQA.compareExcel - Unknow fatal exception, plesae contact DEV to check, with your log.");
			logger.error("DifferDBHandlerQA.compareExcel", e);
			System.exit(-1);
		}

		return allresultsame;

	}
	
//	private boolean compareSheetCountAndName(XSSFWorkbook wbExpected,
//			XSSFWorkbook wbActual) {
//		boolean same = true;
//		int wbExpectedSheetCount = wbExpected.getNumberOfSheets();
//		int wbActualSheetCount = wbActual.getNumberOfSheets();
//		if (wbExpectedSheetCount != wbActualSheetCount) {
//			logger.warn("Sheet total count is different!");
//			same = false;
//		}
//		for (int i = 0; i < wbExpectedSheetCount; i++) {
//			XSSFSheet expectedSheet = wbExpected.getSheetAt(i);
//			String sheetName = expectedSheet.getSheetName();
//			if (wbActual.getSheet(sheetName) == null) {
//				logger.warn("Sheet " + sheetName + " is missed in actual DB !");
//				same = false;
//			}
//		}
//
//		for (int i = 0; i < wbActualSheetCount; i++) {
//			XSSFSheet actualSheet = wbActual.getSheetAt(i);
//			String sheetName = actualSheet.getSheetName();
//			if (wbExpected.getSheet(sheetName) == null) {
//				logger.warn("Sheet " + sheetName + " is extra in actual DB !");
//				same = false;
//			}
//		}
//
//		return same;
//
//	}
	
	
	private boolean compareSheet(XSSFSheet expectedSheet, XSSFSheet actualSheet) {

		int expectedRowCount = expectedSheet.getLastRowNum();
		int actualRowCount = actualSheet.getLastRowNum();
		int compareRowCount = expectedRowCount > actualRowCount ? actualRowCount
				: expectedRowCount;
		String sheetName = expectedSheet.getSheetName();
		logger.warn("Compare sheet : " + sheetName);
		boolean sheetSame = true;
		if (expectedRowCount != actualRowCount) {
			logger.warn("Different, Sheet " + sheetName
					+ " row count is different, expected :" + expectedRowCount
					+ " ,actual :" + actualRowCount);
			sheetSame = false;
		}		
		ArrayList<String> ignoredColumnList = new ArrayList<String>();
		ignoredColumnList.add("ID");
		ignoredColumnList.add("MSG_REQ_ID");
		ignoredColumnList.add("INTERCHANGE_CTRL_NUM");
		ignoredColumnList.add("GROUP_CTRL_NUM");
		ignoredColumnList.add("TXN_CTRL_NUM");
		ignoredColumnList.add("INT_ERR_CDE");
		

			
		for (int j = 0; j <= compareRowCount; j++) {

			XSSFRow expectedRow = expectedSheet.getRow(j);
			XSSFRow actualRow = actualSheet.getRow(j);
			for (Iterator cit = expectedRow.cellIterator(); cit.hasNext();) {
				XSSFCell expectedCell = (XSSFCell) cit.next();
				int cellnum = expectedCell.getColumnIndex();
				String colname = getString(expectedSheet.getRow(0).getCell(
						(int) cellnum));	
				if (FunctionHelperQA
						.checkInColumnList(ignoredColumnList, colname)) {
					continue;
				}
				XSSFCell actualCell = actualRow.getCell((int) (cellnum));
				String cellvalueExpected = getString(expectedCell);
				String cellvalueActual = getString(actualCell);
				if (!cellvalueExpected.equals(cellvalueActual)) {
					sheetSame = false;
					logger.warn("Different,Sheet : " + sheetName
							+ ", Column name : " + colname + ",  Row : "
							+ (j + 1) + "   Expected value : "
							+ cellvalueExpected + ", Actual value : "
							+ cellvalueActual);
					
				}
			}
		}
		
		if (sheetSame) {
			logger.warn("Sheet " + sheetName + " is the same.");
		}
		return sheetSame;
	}

	private boolean compareTable(XSSFSheet expectedSheet, XSSFSheet actualSheet) {
		boolean Result=true;
		XSSFRichTextString expdbmsg = new XSSFRichTextString();
		XSSFRichTextString actdbmsg = new XSSFRichTextString();
		boolean flag=true;
		boolean allflag=true;
		boolean headerflag=true;
		int diffcount=0;
		
		List<Integer> faillist=new ArrayList<Integer>();
		int expectedRowCount = expectedSheet.getLastRowNum();
		int actualRowCount = actualSheet.getLastRowNum();
		int cmpRowCount = expectedRowCount>actualRowCount?expectedRowCount:actualRowCount;
	
		String sheetName = expectedSheet.getSheetName();
		int cmpColumnCount=0;
		while(!getString(expectedSheet.getRow(0).getCell(cmpColumnCount)).equals("")){
			if(!getString(expectedSheet.getRow(0).getCell(cmpColumnCount)).equals(getString(actualSheet.getRow(0).getCell(cmpColumnCount)))){
				headerflag=false;
				break;
			}
			cmpColumnCount++;
		}
	
		XSSFFont blackfont=diffDB.createFont();
		blackfont.setColor(XSSFFont.DEFAULT_FONT_COLOR);
		XSSFFont headerfont=diffDB.createFont();
		headerfont.setBold(true);
		
		if(headerflag){
			for(int i=1;i<=cmpRowCount;i++){
				flag=true;
				faillist.clear();
				if(expectedRowCount<i){
					expectedSheet.createRow(++expectedRowCount);

				}else if(actualRowCount<i){
					actualSheet.createRow(++actualRowCount);
				}				
				for(int j=0;j<cmpColumnCount;j++){
					
					if(!getString(expectedSheet.getRow(i).getCell(j)).equals(getString(actualSheet.getRow(i).getCell(j)))){
						if (sheetName.toLowerCase().equals("b2b_transaction")&&(getString(expectedSheet.getRow(0).getCell(j)).equals("INT_ERR_CDE")
							||getString(expectedSheet.getRow(0).getCell(j)).equals("ID")
							||getString(expectedSheet.getRow(0).getCell(j)).equals("MSG_REQ_ID")
							||getString(expectedSheet.getRow(0).getCell(j)).equals("INTERCHANGE_CTRL_NUM")
							||getString(expectedSheet.getRow(0).getCell(j)).equals("GROUP_CTRL_NUM")
							||getString(expectedSheet.getRow(0).getCell(j)).equals("TXN_CTRL_NUM"))) {									
							continue;
						}else{
							faillist.add(j);
							flag=false;
							allflag=false;
						}
					}	
				}
				if(!flag){
					diffcount++;
					if(diffcount==1){
						expdbmsg.append(diffcount+" :",headerfont);
						actdbmsg.append(diffcount+" :",headerfont);
					}else{
						expdbmsg.append(".\n"+diffcount+" :",headerfont);
						actdbmsg.append(".\n"+diffcount+" :",headerfont);
					}
					
					for(int j=0;j<cmpColumnCount;j++){
									
						if(j!=0){
							expdbmsg.append("; "+getString(expectedSheet.getRow(0).getCell(j))+" :",headerfont);
							actdbmsg.append("; "+getString(actualSheet.getRow(0).getCell(j))+" :",headerfont);	
						}else{
							expdbmsg.append(getString(expectedSheet.getRow(0).getCell(j))+" :",headerfont);	
							actdbmsg.append(getString(actualSheet.getRow(0).getCell(j))+" :",headerfont);
						}
						
						if(faillist.contains(j)){
													
							XSSFFont redfont=diffDB.createFont();
							redfont.setColor(XSSFFont.COLOR_RED);
							if(!getString(expectedSheet.getRow(i).getCell(j)).equals("")){
								expdbmsg.append(getString(expectedSheet.getRow(i).getCell(j)),redfont);
							}
							if(!getString(actualSheet.getRow(i).getCell(j)).equals("")){
								actdbmsg.append(getString(actualSheet.getRow(i).getCell(j)),redfont);
							}
								
						}else{
							if(!getString(expectedSheet.getRow(i).getCell(j)).equals("")){
								expdbmsg.append(getString(expectedSheet.getRow(i).getCell(j)),blackfont);
							}
							if(!getString(actualSheet.getRow(i).getCell(j)).equals("")){
								actdbmsg.append(getString(actualSheet.getRow(i).getCell(j)),blackfont);
							}
			
						}
						
					}
				}	
			}
			if(allflag){
				expdbmsg.setString("PASS");
				actdbmsg.setString("PASS");			
			}
			
		}else{
			int k=1;
			expdbmsg.append("The columns are different,  the expected column list",blackfont);
			expdbmsg.append(":\n"+getString(expectedSheet.getRow(0).getCell(0)),blackfont);
			while(!getString(expectedSheet.getRow(0).getCell(k)).equals("")){
				expdbmsg.append(";\n"+getString(expectedSheet.getRow(0).getCell(k)),blackfont);
				k++;
			}
			k=1;
			actdbmsg.append("The columns are different,  the actual column list",blackfont);
			actdbmsg.append(":\n"+getString(actualSheet.getRow(0).getCell(0)),blackfont);
			while(!getString(actualSheet.getRow(0).getCell(k)).equals("")){
				actdbmsg.append(";\n"+getString(actualSheet.getRow(0).getCell(k)),blackfont);
				k++;
			}
		}
			
		try {
			
			if(expdbmsg.equals("PASS") && actdbmsg.equals("PASS"))
				Result=true;
			else
				writeDBcmpInfo(sheetName, expdbmsg, actdbmsg);
		} catch (Exception e) {
			logger.error(">>> DifferDBHandlerQA.compareTable - Unknow fatal exception, plesae contact DEV to check, with your log.");
			logger.error("DifferDBHandlerQA.compareTable", e);
			System.exit(-1);
		}
		return Result;
	}
	
	public void logDBcmpDifference(File autoLogExcel,String infilename,String expstatus, String actstatus) {
		try{
			if(excelDBcmplogPath==null){
				String autoLogExcelName = autoLogExcel.getName().substring(0,autoLogExcel.getName().lastIndexOf("."));
				excelDBcmplogPath=copyExcelWithName(new File(CSB2BEDIConfigQA.ModelFileFolder+
						"/Difference-DBcmp-Log-Model.xlsm"),
						new File(autoLogExcel.getParent()),"Difference-DBcmp-Log-"+autoLogExcelName+"-");
				if (excelDBcmplogPath==null)
				{
					logger.info("Copy Excel DB Model Fail !");

				} else {
					
					diffDB=new XSSFWorkbook(new FileInputStream(new File(excelDBcmplogPath)));
					logger.info("Copy Excel DB Model successfully !");
				}
			}else{
				if(diffDB==null){
					diffDB=new XSSFWorkbook(new FileInputStream(new File(excelDBcmplogPath)));
				}
			}
			writeDBcmpHeader(infilename,expstatus, actstatus);
		} catch (Exception e) {
			logger.error(">>> DifferDBHandlerQA.logDBcmpDifference - Unknow fatal exception, plesae contact DEV to check, with your log.");
			logger.error("DifferDBHandlerQA.logDBcmpDifference", e);
			System.exit(-1);
		}
	}
	
	public String copyExcelWithName(File f, File dir, String filename) {

		if (!f.isFile()) {
			logger.warn("Can't find the difference model : "
					+ f.getAbsolutePath());
		} else {
			try {
				Date time = new Date();
				SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
				String stime = form.format(time);
				filename = filename + stime + ".xlsm";

				File fcopy = new File(dir, filename);
				fcopy.createNewFile();
				FileInputStream finpen = new FileInputStream(f);
				FileOutputStream foutpen = new FileOutputStream(fcopy);
				FileChannel fin = finpen.getChannel(), fou = foutpen
						.getChannel();

				ByteBuffer byb = ByteBuffer.allocate(1024);
				int i = 1;
				while (i != -1) {
					byb.clear();
					i = fin.read(byb);
					byb.flip();
					fou.write(byb);
				}
				fin.close();
				fou.close();
				finpen.close();
				foutpen.close();
				return  dir + "/" + filename;
			} catch (Exception e) {
				logger.error(">>> DifferDBHandlerQA.copyExcelWithName - Unknow fatal exception, plesae contact DEV to check, with your log.");
				logger.error("DifferDBHandlerQA.copyExcelWithName, filename: "+filename, e);
				System.exit(-1);
			}
		}
		return null;
	}
	
	private String getString(XSSFCell cell1) {
		String cell1value = "";
		if (cell1 != null) {
			switch (cell1.getCellType()) {

			case XSSFCell.CELL_TYPE_NUMERIC: {
				if (HSSFDateUtil.isCellDateFormatted(cell1)) {
					cell1value = cell1.getDateCellValue().toLocaleString();
				} else {

					Integer num = new Integer((int) cell1.getNumericCellValue());
					cell1value = String.valueOf(num);
				}
				break;
			}

			case XSSFCell.CELL_TYPE_STRING:

				cell1value = cell1.getStringCellValue().replaceAll("'", "''");
				break;

			default:
				cell1value = "";
			}
		} else {
			cell1value = "";
		}
		return cell1value;
	}
	
	public void writeDBcmpInfo(String sheetName, XSSFRichTextString expmsg,XSSFRichTextString actmsg) throws Exception {

		File excel = new File(excelDBcmplogPath);
		if (excel.renameTo(excel)) {
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excel));
			XSSFCellStyle style1 = workbook.createCellStyle();
			style1.setBorderBottom(XSSFCellStyle.BORDER_THIN); //
			style1.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //
			style1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			style1.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			style1.setBorderRight(XSSFCellStyle.BORDER_THIN);
			style1.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style1.setBorderTop(XSSFCellStyle.BORDER_THIN);
			style1.setTopBorderColor(IndexedColors.BLACK.getIndex());
			style1.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);//

			style1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			style1.setAlignment(XSSFCellStyle.ALIGN_LEFT); //
			style1.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP); 
			style1.setWrapText(true);
			XSSFSheet XSSFSheet = workbook.getSheetAt(0);
			int rownum = XSSFSheet.getLastRowNum();
			XSSFRow row = XSSFSheet.createRow(rownum + 1);
			// row.setRowStyle(style1);

			XSSFCell celltablename = row.createCell((int) 4);
			celltablename.setCellStyle(style1);
			celltablename.setCellType(XSSFCell.CELL_TYPE_STRING);
			XSSFCell celltableexpmsg = row.createCell((int) 5);
			celltableexpmsg.setCellStyle(style1);
			celltableexpmsg.setCellType(XSSFCell.CELL_TYPE_STRING);
			XSSFCell celltableactmsg = row.createCell((int) 6);
			celltableactmsg.setCellStyle(style1);
			celltableactmsg.setCellType(XSSFCell.CELL_TYPE_STRING);

			celltablename.setCellValue(sheetName);
			celltableexpmsg.setCellValue(expmsg);
			celltableactmsg.setCellValue(actmsg);
			
			FileOutputStream fOut = new FileOutputStream(excel);
			workbook.write(fOut);

			fOut.flush();
			fOut.close();
		}
	}
	
	public void writeDBcmpHeader(String infilename,
			String expstatus, String actstatus) throws Exception {

		File excel = new File(excelDBcmplogPath);
		
		if (excel.renameTo(excel)) {
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excel));
			XSSFCellStyle style1 = workbook.createCellStyle();
			style1.setBorderBottom(XSSFCellStyle.BORDER_THIN); //
			style1.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //
			style1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			style1.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			style1.setBorderRight(XSSFCellStyle.BORDER_THIN);
			style1.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style1.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
			style1.setTopBorderColor(IndexedColors.BLACK.getIndex());
			style1.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);//

			style1.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			style1.setAlignment(XSSFCellStyle.ALIGN_LEFT); //
			style1.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP); 
			style1.setWrapText(true);
			XSSFSheet XSSFSheet = workbook.getSheetAt(0);
			int rownum = XSSFSheet.getLastRowNum();
			XSSFRow row = XSSFSheet.createRow(rownum + 1);
			// row.setRowStyle(style1);
			XSSFCell celltpid = row.createCell((int) 0);
			celltpid.setCellType(XSSFCell.CELL_TYPE_STRING);
			celltpid.setCellStyle(style1);
			XSSFCell cellreqid = row.createCell((int) 1);
			cellreqid.setCellStyle(style1);
			cellreqid.setCellType(XSSFCell.CELL_TYPE_STRING);
			XSSFCell cellexpstatus = row.createCell((int) 2);
			cellexpstatus.setCellStyle(style1);
			cellexpstatus.setCellType(XSSFCell.CELL_TYPE_STRING);
			XSSFCell cellactstatus = row.createCell((int) 3);
			cellactstatus.setCellStyle(style1);
			cellactstatus.setCellType(XSSFCell.CELL_TYPE_STRING);
			XSSFCell celltablename = row.createCell((int) 4);
			celltablename.setCellStyle(style1);
			celltablename.setCellType(XSSFCell.CELL_TYPE_STRING);
			XSSFCell celltableexpmsg = row.createCell((int) 5);
			celltableexpmsg.setCellStyle(style1);
			celltableexpmsg.setCellType(XSSFCell.CELL_TYPE_STRING);
			XSSFCell celltableactmsg = row.createCell((int) 6);
			celltableactmsg.setCellStyle(style1);
			celltableactmsg.setCellType(XSSFCell.CELL_TYPE_STRING);
			XSSFCell cellactinputfile = row.createCell((int) 7);
			cellactinputfile.setCellStyle(style1);
			cellactinputfile.setCellType(XSSFCell.CELL_TYPE_STRING);
			
		//	celltpid.setCellValue(tpID);
		//	cellreqid.setCellValue(msgReqID);
			cellactinputfile.setCellValue(infilename);
			cellexpstatus.setCellValue(expstatus);
			cellactstatus.setCellValue(actstatus);
			celltablename.setCellValue("");
			celltableexpmsg.setCellValue("");
			celltableactmsg.setCellValue("");
			
			FileOutputStream fOut = new FileOutputStream(excel);
			workbook.write(fOut);

			fOut.flush();
			fOut.close();
		}
	}
	
}
