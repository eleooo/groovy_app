package cs.b2b.core.common.file;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import cs.b2b.core.common.session.B2BRuntimeSession;
import cs.b2b.core.msglogger.MsgBusLogger;

public class FileNameMacro {

	//private static String B2BMSGBUS_NameSpace = "http://B2BMSGBUS.StandardPromotedProperties_2dot01";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01 = "FirstMsgHeader_DataHdr_ToRef01";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02 = "FirstMsgHeader_DataHdr_ToRef02";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03 = "FirstMsgHeader_DataHdr_ToRef03";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04 = "FirstMsgHeader_DataHdr_ToRef04";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05 = "FirstMsgHeader_DataHdr_ToRef05";

	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01_FileMacro = "%FirstMsgHeader_DataHdr_ToRef01%";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02_FileMacro = "%FirstMsgHeader_DataHdr_ToRef02%";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03_FileMacro = "%FirstMsgHeader_DataHdr_ToRef03%";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04_FileMacro = "%FirstMsgHeader_DataHdr_ToRef04%";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05_FileMacro = "%FirstMsgHeader_DataHdr_ToRef05%";

	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01_FileMacro_With_Namespace = "%B2BMSGBUS.StdProp.FirstMsgHeader_DataHdr_ToRef01%";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02_FileMacro_With_Namespace = "%B2BMSGBUS.StdProp.FirstMsgHeader_DataHdr_ToRef02%";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03_FileMacro_With_Namespace = "%B2BMSGBUS.StdProp.FirstMsgHeader_DataHdr_ToRef03%";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04_FileMacro_With_Namespace = "%B2BMSGBUS.StdProp.FirstMsgHeader_DataHdr_ToRef04%";
	private static String B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05_FileMacro_With_Namespace = "%B2BMSGBUS.StdProp.FirstMsgHeader_DataHdr_ToRef05%";

	private static String B2BMSGBUS_FILE_MACRO_SUFFIX = "_SEQ";
	//private static String genericExcelPropertyPromotionNS = "http://www.oocllogistics.com/GenericExcelPropertyPromotion.ExcelColumnPropertySchema";

	public String ApplyCustomMacro(Connection dbconn, String sendPortName, String msgReqId,
			String originalFileName, String sMacro, String sessionId) throws Exception {
		if (sMacro == null || sMacro.trim().length() == 0)
			throw new Exception("[ApplyCustomMacro] input sMarco is empty.");

		String ret = null;
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());

		// list of replacement macro
		String interchangeID = msgReqId;
		String sourceFileName = originalFileName;
		String sourceFileNameNoExt = originalFileName;
		String sourceFileExt = "";

		if (originalFileName.contains(".")) {
			sourceFileNameNoExt = originalFileName.substring(0,
					originalFileName.lastIndexOf("."));
			sourceFileExt = originalFileName.substring(originalFileName
					.lastIndexOf(".") + 1);
		}

		String yyyy = "" + now.get(Calendar.YEAR);
		String yy = "" + now.get(Calendar.YEAR);
		yy = yy.substring(yy.length() - 2, yy.length());
		String mm = "" + (now.get(Calendar.MONTH) + 1);
		mm = (mm.length() > 1 ? mm : "0" + mm);
		String dd = "" + (now.get(Calendar.DAY_OF_MONTH));
		dd = (dd.length() > 1 ? dd : "0" + dd);
		String hh24 = "" + now.get(Calendar.HOUR_OF_DAY); // current hour in 24
															// hours format
		hh24 = (hh24.length() > 1 ? hh24 : "0" + hh24);
		String mi = "" + (now.get(Calendar.MINUTE)); // minute
		mi = (mi.length() > 1 ? mi : "0" + mi);
		String ss = "" + (now.get(Calendar.SECOND));
		ss = (ss.length() > 1 ? ss : "0" + ss);
		//String seq = null; // for next release
		String millionSeconds = ""+now.get(Calendar.MILLISECOND);
		if (millionSeconds.length()==1) {
			millionSeconds = "00"+millionSeconds;
		} else if (millionSeconds.length()==2) {
			millionSeconds = "0"+millionSeconds;
		}
		
		// replacement
		String outString = sMacro;

		outString = ApplyFileNotExtReplacementImpl(sourceFileNameNoExt,
				outString);
		outString = outString.replace("%SourceFileName%", sourceFileName);
		outString = outString.replace("%YYYY%", yyyy);
		outString = outString.replace("%YY%", yy);
		outString = outString.replace("%MM%", mm);
		outString = outString.replace("%DD%", dd);
		outString = outString.replace("%HH24%", hh24);
		outString = outString.replace("%MI%", mi);
		outString = outString.replace("%SS%", ss);
		outString = outString.replace("%MILLIONSEC%", millionSeconds);
		outString = outString.replace("%InterchangeID%", interchangeID);

		outString = outString.replace("%Ext%", sourceFileExt);

		if (outString.indexOf("%MessageBusUniqieFileNameNoExt%") >= 0) {
			outString = outString.replace("%MessageBusUniqieFileNameNoExt%",
					getMessageBusUniqieNumber());
		}

		if (outString.indexOf("%MessageBusUniqueID%") >= 0) {
			outString = outString.replace("%MessageBusUniqueID%",
					getMessageBusUniqieNumber());
		}

		// replace B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef0[n]_FileMacro
		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01_FileMacro) >= 0) {
			// inmsg.Context.Read(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01,
			// B2BMSGBUS_NameSpace) as String;
			String strMsgHeaderDataToRef01 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01);
			if (strMsgHeaderDataToRef01 != null
					&& strMsgHeaderDataToRef01.length() > 0) {
				outString = outString.replace(
						B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01_FileMacro,
						strMsgHeaderDataToRef01);
			}
		}

		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02_FileMacro) >= 0) {
			// inmsg.Context.Read(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02,
			// B2BMSGBUS_NameSpace) as String;
			String strMsgHeaderDataToRef02 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02);
			if (strMsgHeaderDataToRef02 != null
					&& strMsgHeaderDataToRef02.length() > 0) {
				outString = outString.replace(
						B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02_FileMacro,
						strMsgHeaderDataToRef02);
			}
		}

		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03_FileMacro) >= 0) {
			String strMsgHeaderDataToRef03 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03);
			if (strMsgHeaderDataToRef03 != null
					&& strMsgHeaderDataToRef03.length() > 0) {
				outString = outString.replace(
						B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03_FileMacro,
						strMsgHeaderDataToRef03);
			}
		}

		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04_FileMacro) >= 0) {
			String strMsgHeaderDataToRef04 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04);
			if (strMsgHeaderDataToRef04 != null
					&& strMsgHeaderDataToRef04.length() > 0) {
				outString = outString.replace(
						B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04_FileMacro,
						strMsgHeaderDataToRef04);
			}
		}
		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05_FileMacro) >= 0) {
			String strMsgHeaderDataToRef05 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05);
			if (strMsgHeaderDataToRef05 != null
					&& strMsgHeaderDataToRef05.length() > 0) {
				outString = outString.replace(
						B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05_FileMacro,
						strMsgHeaderDataToRef05);
			}
		}

		// * replace
		// B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef0[1]_FileMacro_With_Namespace
		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01_FileMacro_With_Namespace) >= 0) {
			String strMsgHeaderDataToRef01 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01);
			if (strMsgHeaderDataToRef01 != null
					&& strMsgHeaderDataToRef01.length() > 0) {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01_FileMacro_With_Namespace,
								strMsgHeaderDataToRef01);
			} else {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef01_FileMacro_With_Namespace,
								"");
			}
		}
		// * replace
		// B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef0[2]_FileMacro_With_Namespace
		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02_FileMacro_With_Namespace) >= 0) {
			String strMsgHeaderDataToRef02 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02);
			if (strMsgHeaderDataToRef02 != null
					&& strMsgHeaderDataToRef02.length() > 0) {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02_FileMacro_With_Namespace,
								strMsgHeaderDataToRef02);
			} else {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef02_FileMacro_With_Namespace,
								"");
			}
		}
		// * replace
		// B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef0[3]_FileMacro_With_Namespace
		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03_FileMacro_With_Namespace) >= 0) {
			String strMsgHeaderDataToRef03 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03);
			if (strMsgHeaderDataToRef03 != null
					&& strMsgHeaderDataToRef03.length() > 0) {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03_FileMacro_With_Namespace,
								strMsgHeaderDataToRef03);
			} else {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef03_FileMacro_With_Namespace,
								"");
			}
		}
		// * replace
		// B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef0[4]_FileMacro_With_Namespace
		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04_FileMacro_With_Namespace) >= 0) {
			String strMsgHeaderDataToRef04 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04);
			if (strMsgHeaderDataToRef04 != null
					&& strMsgHeaderDataToRef04.length() > 0) {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04_FileMacro_With_Namespace,
								strMsgHeaderDataToRef04);
			} else {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef04_FileMacro_With_Namespace,
								"");
			}
		}
		// * replace
		// B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef0[5]_FileMacro_With_Namespace
		if (outString
				.indexOf(B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05_FileMacro_With_Namespace) >= 0) {
			String strMsgHeaderDataToRef05 = B2BRuntimeSession.getSessionValue(
					sessionId, B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05);
			if (strMsgHeaderDataToRef05 != null
					&& strMsgHeaderDataToRef05.length() > 0) {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05_FileMacro_With_Namespace,
								strMsgHeaderDataToRef05);
			} else {
				outString = outString
						.replace(
								B2BMSGBUS_FirstMsgHeader_DataHdr_ToRef05_FileMacro_With_Namespace,
								"");
			}
		}

		// set processing datetime for %REc...%
		if (outString.indexOf("%Recv") >= 0) {
			outString = ApplyCustomMacroRecvDatetime(outString, sessionId);
		}
		// set port sequence for "..._SEQ"
		if (outString.indexOf("_SEQ%") >= 0) {
			outString = ApplySequenceMarco(dbconn, outString, sendPortName);
		}
		// set seq_value with specified seq_id
		outString = ApplySequenceAdvanceMacro(dbconn, outString);

		// excel column promote
		String[] excelPropertyNameList = { "ExcelColumn_1", "ExcelColumn_2",
				"ExcelColumn_3", "ExcelColumn_4", "ExcelColumn_5",
				"ExcelColumn_6", "ExcelColumn_7", "ExcelColumn_8",
				"ExcelColumn_9", "ExcelColumn_10", "ExcelColumn_11",
				"ExcelColumn_12", "ExcelColumn_13", "ExcelColumn_14",
				"ExcelColumn_15", "ExcelColumn_16", "ExcelColumn_17",
				"ExcelColumn_18", "ExcelColumn_19", "ExcelColumn_20",
				"ExcelColumn_21", "ExcelColumn_22", "ExcelColumn_23",
				"ExcelColumn_24", "ExcelColumn_25", "ExcelColumn_26" };

		if (outString.contains("%ExcelColumn_")) {
			for (String excelColumnName : excelPropertyNameList) {
				String excelColumnFileMacro = "%" + excelColumnName + "%";
				if (outString.indexOf(excelColumnFileMacro) > 0) {
					String excelColumnPropertyValue = B2BRuntimeSession
							.getSessionValue(sessionId, excelColumnName);
					if (excelColumnPropertyValue != null
							&& excelColumnPropertyValue.trim().length() > 0) {
						outString = outString.replace(excelColumnFileMacro,
								excelColumnPropertyValue);
					} else {
						outString = outString.replace(excelColumnFileMacro, "");
					}
				}
			}
		}

		String commonFileNameReplaceTag = "Output_FileName_ReplacePart";
		String commonFileNameReplaceMacro = "%" + commonFileNameReplaceTag
				+ "%";
		if (outString.indexOf(commonFileNameReplaceMacro) >= 0) {
			String commonPipelineFileNamePropertyValue = B2BRuntimeSession
					.getSessionValue(sessionId, commonFileNameReplaceTag);
			if (commonPipelineFileNamePropertyValue == null) {
				commonPipelineFileNamePropertyValue = "";
			}
			outString = outString.replace(commonFileNameReplaceMacro,
					commonPipelineFileNamePropertyValue.trim());
		}
		ret = outString;
		return ret;
	}

	protected String ApplyFileNotExtReplacementImpl(String sourceFileNameNoExt,
			String outString) throws Exception {
		String ret = null;

		outString = outString.replace("%SourceFileNameNoExt%",
				sourceFileNameNoExt);

		String tagOfFileNotExtBegin = "%SourceFileNameNoExt[";
		String tagOfFileNotExtEnd = "]%";

		int intFileNotExtBeginTag = outString.indexOf(tagOfFileNotExtBegin);
		int intFileNotExtEndTag = outString.indexOf(tagOfFileNotExtEnd);
		if (intFileNotExtBeginTag >= 0
				&& intFileNotExtBeginTag < intFileNotExtEndTag) {
			String keyOfFileNotExtTag = outString.substring(
					intFileNotExtBeginTag, intFileNotExtEndTag
							+ tagOfFileNotExtEnd.length());

			int intReqFromIndexPosition = keyOfFileNotExtTag.indexOf("[") + 1;
			int intReqLengthEndPosition = keyOfFileNotExtTag.indexOf("]");
			String strCutStringSetting = keyOfFileNotExtTag.substring(
					intReqFromIndexPosition, intReqLengthEndPosition);
			if (strCutStringSetting.indexOf(",") < 0) {
				throw new Exception(
						"Tag of %SourceFileNameNoExt[N,N]%, wrong setting [require ','], the marco is: "
								+ outString);
			}
			int intCutStringSettingCommaPosition = strCutStringSetting
					.indexOf(",");
			String reqFromIndex = strCutStringSetting.substring(0,
					intCutStringSettingCommaPosition);
			String reqLength = strCutStringSetting
					.substring(intCutStringSettingCommaPosition + 1);

			int cutBeginIndex = 0;
			int cutLen = 0;
			try {
				cutBeginIndex = Integer.parseInt(reqFromIndex);
				cutLen = Integer.parseInt(reqLength);
			} catch (Exception e) {
				throw new Exception(
						"Tag of %SourceFileNameNoExt[N,N]%, wrong setting, the marco is: "
								+ outString);
			}
			if (cutBeginIndex >= sourceFileNameNoExt.length()) {
				throw new Exception(
						"Tag of %SourceFileNameNoExt[N,N]%, begin index is larger than length of sourceFileNameNoExt, sourceFileNameNoExt: "
								+ sourceFileNameNoExt
								+ ", cutBeginIndex: "
								+ cutBeginIndex);
			}
			if (cutBeginIndex < 0 || cutLen < 0) {
				throw new Exception(
						"Tag of %SourceFileNameNoExt[N,N]%, wrong setting, the marco is: "
								+ outString);
			}

			if (cutLen > (sourceFileNameNoExt.length() - cutBeginIndex)
					|| cutLen == 0) {
				cutLen = sourceFileNameNoExt.length() - cutBeginIndex;
			}
			String replaceString = sourceFileNameNoExt.substring(cutBeginIndex,
					cutBeginIndex + cutLen);

			outString = outString.replace(keyOfFileNotExtTag, replaceString);
		}
		ret = outString;
		return ret;
	}

	protected String ApplyCustomMacroRecvDatetime(String inputString,
			String sessionId) throws Exception {
		if (inputString == null || sessionId == null) {
			return inputString;
		}
		String outString = inputString;
		String msgRecTs = B2BRuntimeSession.getSessionValue(sessionId,
				"MsgReceiveDateTime");
		if (msgRecTs != null && msgRecTs.length() > 0) {
			// format: yyyy-MM-dd HH:mm:ss
			int _1stmidline = msgRecTs.indexOf("-");
			int _lastmidline = msgRecTs.lastIndexOf("-");
			int _1stmidmi = msgRecTs.indexOf(":");
			int _lastmidmi = msgRecTs.lastIndexOf(":");
			if (_1stmidline < 0 || _lastmidline < 0 || _1stmidmi < 0
					|| _lastmidmi < 0)
				throw new Exception("Invalid msg receive datetime, required format: yyyy-MM-dd HH:mm:ss, value: "+ msgRecTs);
			outString = outString.replace("%RecvYYYY%",
					msgRecTs.substring(0, _1stmidline));
			outString = outString.replace("%RecvYY%",
					msgRecTs.substring(2, _1stmidline));
			outString = outString.replace("%RecvMM%",
					msgRecTs.substring(_1stmidline + 1, _lastmidline));
			outString = outString.replace("%RecvDD%",
					msgRecTs.substring(_lastmidline + 1, _lastmidline + 3));
			outString = outString.replace("%RecvHH24%",
					msgRecTs.substring(_1stmidmi - 2, _1stmidmi));
			outString = outString.replace("%RecvMI%",
					msgRecTs.substring(_1stmidmi + 1, _lastmidmi));
			outString = outString.replace("%RecvSS%",
					msgRecTs.substring(_lastmidmi + 1));
		}
		return checkSpecialCharForFileName(outString);
	}

	public String checkSpecialCharForFileName(String OUTPUT_FILE_NAME) {
		String rplChr = "";
//		if (OUTPUT_FILE_NAME.contains("%"))
//			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("%", rplChr);
		if (OUTPUT_FILE_NAME.contains("/"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("/", rplChr);
		if (OUTPUT_FILE_NAME.contains("\\"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("\\", rplChr);
		if (OUTPUT_FILE_NAME.contains("*"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("*", rplChr);
		if (OUTPUT_FILE_NAME.contains(":"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace(":", rplChr);
		if (OUTPUT_FILE_NAME.contains("?"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("?", rplChr);
		if (OUTPUT_FILE_NAME.contains("$"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("$", rplChr);
		if (OUTPUT_FILE_NAME.contains("&"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("&", rplChr);
		if (OUTPUT_FILE_NAME.contains("'"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("'", rplChr);
		if (OUTPUT_FILE_NAME.contains("\""))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("\"", rplChr);
		if (OUTPUT_FILE_NAME.contains("<"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace("<", rplChr);
		if (OUTPUT_FILE_NAME.contains(">"))
			OUTPUT_FILE_NAME = OUTPUT_FILE_NAME.replace(">", rplChr);
		return OUTPUT_FILE_NAME;
	}
	
	public String ApplySequenceMarco(Connection dbconn, String inputString, String sendPortName) throws Exception {
		if (inputString == null || inputString.trim().length() == 0) {
			return inputString;
		}

		String retString = inputString.trim();
		// get the sequence setting macro, and then analysis 'start from',
		// 'maximum' setting from macro
		String originalMacroSetting = "";
		String macroSetting = "";
		long settingStartFromValue = 0;
		long settingMaximumValue = 0;
		String errorDescription = null;

		if (retString.indexOf("_SEQ%") >= 0) {
			try {
				// %000001_999999_SEQ%
				// get out the macro setting
				int finishPosition = retString.indexOf("_SEQ%") + 4;
				macroSetting = retString.substring(0, finishPosition);
				int lastPercentage = macroSetting.lastIndexOf("%") + 1;
				macroSetting = macroSetting.substring(lastPercentage);
				originalMacroSetting = "%" + macroSetting + "%";
				// get 'start from' and maximum
				int _firstSplit = macroSetting.indexOf("_");
				String strStartFrom = macroSetting.substring(0, _firstSplit);
				String _temMaxSetting = macroSetting.substring(_firstSplit + 1);
				String strMaximum = _temMaxSetting.substring(0,
						_temMaxSetting.indexOf("_"));
				settingStartFromValue = Long.parseLong(strStartFrom);
				settingMaximumValue = Long.parseLong(strMaximum);

				// logic checking: 1) startFrom < Maximum
				if (settingStartFromValue >= settingMaximumValue) {
					errorDescription = "File sequence macro setting error, start from value shall be less than maximum value, sendPortName: "
							+ sendPortName
							+ ", setting: "
							+ originalMacroSetting;
				} else {
					// get port sequence data from util.
					MsgBusLogger log = new MsgBusLogger();
					String portSeqID = sendPortName
							+ B2BMSGBUS_FILE_MACRO_SUFFIX; // + "_SEQ"
					long currentValue = log
							.GetSequenceNextValWithDefaultAndMaximum(
									dbconn, portSeqID, settingStartFromValue, settingMaximumValue);
					if (currentValue < settingStartFromValue
							|| currentValue > settingMaximumValue) {
						errorDescription = "File Name Sequence is out of scope, sendPortName: "
								+ sendPortName
								+ ", setting: "
								+ originalMacroSetting
								+ ", db sequence value: " + currentValue;
						currentValue = settingStartFromValue;
					}
					// replace file macro sequence
					String updateValue = "" + currentValue;
					// formatting sequence value to require leading digital
					if (updateValue.length() < strStartFrom.length()) {
						updateValue = strStartFrom.substring(0,
								strStartFrom.length() - updateValue.length())
								+ updateValue;
					}
					retString = retString.replace(originalMacroSetting, updateValue);
				}
			} catch (Exception e) {
				errorDescription = "Analysis file sequence macro error, sendPortName: "
						+ sendPortName
						+ ", setting: "
						+ originalMacroSetting
						+ ", Exception: " + e.getMessage();
			}

			if (errorDescription != null && errorDescription.length() > 0) {
				throw new Exception("Port File Sequence Macro Exception: \r\n"
						+ errorDescription);
			}
		}
		return retString;
	}

	static SimpleDateFormat msgBusUniqueSdf = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };

	public static String generateShortUUID() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}

	public synchronized static String getMessageBusUniqieNumber() {
		String output = "MB" + msgBusUniqueSdf.format(new Date());
		// 4 digital random str
		String rval = generateShortUUID();
		return output + rval;
	}

	public String ApplySequenceAdvanceMacro(Connection conn, String inputString) throws Exception {
		if (inputString == null || inputString.trim().length() == 0) {
			return inputString;
		}

		MsgBusLogger logger = new MsgBusLogger();
		// get iseq's sequence id
		String fullSeqIDMacro = "";
		String sequenceID = "";

		String ISEQ_TAG = "%ISEQ[";
		String ISEQ_END_TAG = "]%";
		// replace ISEQ
		while (inputString.indexOf(ISEQ_TAG) >= 0
				&& inputString.indexOf(ISEQ_END_TAG) > 0) {
			int intIseqBegin = inputString.indexOf(ISEQ_TAG) + ISEQ_TAG.length();
			int intIseqEnd = inputString.indexOf(ISEQ_END_TAG, intIseqBegin);
			if (intIseqEnd < intIseqBegin) {
				break;
			}
			sequenceID = inputString.substring(intIseqBegin, intIseqEnd);
			fullSeqIDMacro = inputString.substring(
					intIseqBegin - ISEQ_TAG.length(), intIseqEnd + ISEQ_END_TAG.length());

			long seqVal = logger.GetSequenceNextValWithDefault(conn, sequenceID, 0);
			inputString = inputString
					.replace(fullSeqIDMacro, ""+seqVal);
		}

		String SEQI_TAG = "%SEQI[";
		String SEQI_END_TAG = "]%";
		// replace SEQI
		while (inputString.indexOf(SEQI_TAG) >= 0
				&& inputString.indexOf(SEQI_END_TAG) > 0) {
			int intIseqBegin = inputString.indexOf(SEQI_TAG) + SEQI_TAG.length();
			int intIseqEnd = inputString.indexOf(SEQI_END_TAG, intIseqBegin);
			if (intIseqEnd < intIseqBegin) {
				break;
			}
			sequenceID = inputString.substring(intIseqBegin, intIseqEnd);
			fullSeqIDMacro = inputString.substring(
					intIseqBegin - SEQI_TAG.length(), intIseqEnd + SEQI_END_TAG.length());

			long seqVal = logger.GetSequenceNextValWithDefault(conn, sequenceID, 1);
			// SEQI is return current value and then db add 1.
			seqVal -= 1;
			inputString = inputString.replace(fullSeqIDMacro, ""+seqVal);
		}

		String SEQ_TAG = "%SEQ[";
		String SEQ_END_TAG = "]%";
		// replace SEQ - current value
		while (inputString.indexOf(SEQ_TAG) >= 0
				&& inputString.indexOf(SEQ_END_TAG) > 0) {
			int intIseqBegin = inputString.indexOf(SEQ_TAG) + SEQ_TAG.length();
			int intIseqEnd = inputString.indexOf(SEQ_END_TAG, intIseqBegin);
			if (intIseqEnd < intIseqBegin) {
				break;
			}
			sequenceID = inputString.substring(intIseqBegin, intIseqEnd);
			fullSeqIDMacro = inputString.substring(
					intIseqBegin - SEQ_TAG.length(), intIseqEnd + SEQ_END_TAG.length());

			// if the seqId is not exist, then return is -1
			long seqVal = logger.GetSequenceCurrentVal(conn, sequenceID);
			inputString = inputString.replace(fullSeqIDMacro, ""+seqVal);
		}

		return inputString;
	}

}
