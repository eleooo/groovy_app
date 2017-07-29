package cs.b2b.core.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return (s==null || s.trim().length()==0);
	}
	
	public static boolean isNotEmpty(String s) {
		return (!(s==null || s.trim().length()==0));
	}
	
	public static String getErrStr(Exception e, String errInfo) {
		if (errInfo==null)
			errInfo = "";
		StringBuffer sberror = new StringBuffer(errInfo);
		StringWriter sw = new StringWriter();
		try {
			if (e.getMessage()!=null) {
				sberror.append(e.getMessage()).append("\r\n");
			}
			e.printStackTrace(new PrintWriter(sw));            
			sberror.append(sw.toString());
			sw.close();
		} catch (Exception ex1) {
			sberror.append("\r\n").append(e.getMessage());
		}
		return sberror.toString();
	}
	
	public static String getErrStr(Throwable te, String errInfo) {
		if (errInfo==null)
			errInfo = "";
		StringBuffer sberror = new StringBuffer(errInfo);
		StringWriter sw = new StringWriter();
		try {
			if (te.getMessage()!=null) {
				sberror.append(te.getMessage()).append("\r\n");
			}
			te.printStackTrace(new PrintWriter(sw));            
			sberror.append(sw.toString());
			sw.close();
		} catch (Exception ex1) {
			sberror.append("\r\n").append(te.getMessage());
		}
		return sberror.toString();
	}
	
	public static String getErrRootCauseStr(Throwable texp, String errInfo) {
		if (errInfo==null)
			errInfo = "";
		StringBuffer sberror = new StringBuffer(errInfo);
		sberror.append("; \r\n");
		StringWriter sw = new StringWriter();
		try {
			Throwable loge = texp;
			for (int li=0; li<20; li++) {
				if (loge!=null && loge.getCause()!=null) {
					loge = loge.getCause();
				}
				if (loge.getCause()==null)
					break;
			}
			loge.printStackTrace(new PrintWriter(sw));            
			sberror.append(sw.toString());
			sw.close();
		} catch (Exception exUnexpected) {
			sberror.append(exUnexpected.toString());
		}
		return sberror.toString();
	}
	
	public static String trimWrapper(String str, String wrapper) {
		if (str==null)
			return null;
		if (isEmpty(wrapper))
			return str;
		
		if (str.startsWith(wrapper) && str.endsWith(wrapper)) {
			str = str.substring(wrapper.length(), str.length()-wrapper.length());
		}
		return str;
	}	
	
	public static String splitCharEncode(String str) {
		if (str.equals("+") || str.equals("|") || str.equals("*"))
			return "\\"+str;
		else
			return str;
	}

	public static String removeHexChar(String str, String replacement) {
		if (str==null || str.trim().length()==0)
			return str;
		
		int intLen = str.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < intLen; i++){
			char chr = str.charAt(i);
			if ((chr <= 0x09) || (0x0B <= chr && chr <= 0x0C) || (0x0E <= chr && chr <= 0x1F) || (0x7F <= chr) ) {
				if(chr!='\t') {
					sb.append (replacement);
				}
			} else {
				sb.append (chr);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param inStr
	 * @param fieldFixedLength
	 * @param replaceHexToChar
	 * @return
	 */
    public static List<String> positionalString2Delimited(String inStr, int[] fieldFixedLength, boolean breplaceHexChar, String replaceHexToChar) {
		return positionalString2Delimited(inStr, fieldFixedLength, null, null, breplaceHexChar, replaceHexToChar, null);
	}
    
    public static List<String> positionalString2Delimited(String inStr, int[] fieldFixedLength, String tag, String wrapperChar, boolean breplaceHexChar, String replaceHexToChar, String dataToAppendAtEnd) {
		return positionalString2Delimited(inStr, fieldFixedLength, tag, wrapperChar, "\t", "\r\n", breplaceHexChar, replaceHexToChar, dataToAppendAtEnd);
	}
	
	public static List<String> positionalString2Delimited(String inStr, int[] fieldFixedLength, String tag, String wrapperChar, String fieldDelimiter, String recordDelimiter, boolean breplaceHexChar, String replaceHexToChar, String dataToAppendAtEnd) {
		if (inStr==null || inStr.trim().length()==0)
			return null;
		
		List<String> retList = new ArrayList<String>();
		
		//StringBuffer csvBuffer = new StringBuffer();
		if (inStr.contains(fieldDelimiter))
			inStr = inStr.replace(fieldDelimiter, "");
		
		if (recordDelimiter.equals("\r\n") && !inStr.contains(recordDelimiter) && inStr.contains("\n")) {
			recordDelimiter = "\n";
		}
		
		String[] contentLines = inStr.split(splitCharEncode(recordDelimiter));
		for(int icontent = 0; contentLines!=null && icontent<contentLines.length; icontent++) {
			String currentLine = contentLines[icontent];
			if (currentLine.trim().length()==0)
				continue;
			
			StringBuffer csvBuffer = new StringBuffer();
	        int position = 0;
	        int contentStrLen = currentLine.length();
	        for (int i = 0; i < fieldFixedLength.length; i++) {
	        	int currentReadEndPosition = position + fieldFixedLength[i];
	        	if (currentReadEndPosition > contentStrLen)
	        		currentReadEndPosition = contentStrLen;
	        	
	            String fieldValue = currentLine.substring(position, currentReadEndPosition);
	            position = currentReadEndPosition;
	            
	            if (i==0 && ! StringUtil.isEmpty(tag)) {
	            	if (! StringUtil.isEmpty(wrapperChar))
	            		csvBuffer.append(wrapperChar);
	            	csvBuffer.append(tag);
	            	if (! StringUtil.isEmpty(wrapperChar))
	            		csvBuffer.append(wrapperChar);
	            	csvBuffer.append(fieldDelimiter);
	            }
	            if (breplaceHexChar && replaceHexToChar!=null)
	            	fieldValue = StringUtil.removeHexChar(fieldValue, replaceHexToChar);
	            csvBuffer.append(fieldValue);
	            if (position<contentStrLen && i<fieldFixedLength.length-1) {
	            	//if still need add field.
	            	csvBuffer.append(fieldDelimiter);
	            } else {
	            	//add dataToAppendAtEnd
	            	if (StringUtil.isNotEmpty(dataToAppendAtEnd)) {
	            		csvBuffer.append(fieldDelimiter);
	            		if (! StringUtil.isEmpty(wrapperChar))
		            		csvBuffer.append(wrapperChar);
		            	csvBuffer.append(dataToAppendAtEnd);
		            	if (! StringUtil.isEmpty(wrapperChar))
		            		csvBuffer.append(wrapperChar);
	            	}
	            	retList.add(csvBuffer.toString());
	            	break;
	            }
	        }
		}
        return retList;
    }
	
	public static void main(String[] args) {
		String strResult = "";
		
		System.out.println("result: ---\r\n"+strResult+"\r\n---");
	}
}
