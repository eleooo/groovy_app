package cs.b2b.core.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

/**
 * 
 * @author LIANGDA
 *
 * 20150926 added.
 */
public class UniqueKeyGenerator {

	public static SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyyMMddHHmmss");
	public static SimpleDateFormat sdfDateTimeMillionSec = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public static SimpleDateFormat sdfMsgSend = new SimpleDateFormat("yyMMdd.HHmmss.SSS");
	public static SimpleDateFormat sdfMsgSendFmt2 = new SimpleDateFormat("yyMMdd.HHmmssSSS");
	public static SimpleDateFormat sdfMsgSendFmt3 = new SimpleDateFormat("yyMMddHHmmssSSS");
	public static SimpleDateFormat sdfMsgSendFmt4 = new SimpleDateFormat("yyMMddHHmmss");
	
	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };

	public static String generateShortUUID() {
		return generateShortUUID(8);
	}
	
	public static String generateShortUUID(int length) {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < length; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}
	
	public synchronized String getMsgSendId() {
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
		StringBuffer outBuffer = new StringBuffer("MB");
		outBuffer.append(sdfMsgSend.format(now.getTime())).append(".");
		outBuffer.append(generateShortUUID(8));
		return outBuffer.toString();
	}
	
}
