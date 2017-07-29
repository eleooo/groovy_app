package cs.b2b.core.common.session;

import java.util.Hashtable;

import cs.b2b.core.common.util.UniqueKeyGenerator;

public class B2BRuntimeHashData {

	private static String processName = "N/A";
	private static int processId = 0;
	private static String processEncodeToken = "";
	private static int processSequence = 0;
	
	private static Hashtable<String, String> hashStr = new Hashtable<String, String>();
	private static Hashtable<String, Integer> hashSequences = new Hashtable<String, Integer>();
	
	public static void setProcessName(String str) {
		processName = str;
	}
	
	public static String getProcessName() {
		return processName;
	}
			
	public static String getProcessEncodeToken() {
		return processEncodeToken;
	}

//	public static void setProcessEncodeToken(String _processEncodeToken) {
//		processEncodeToken = _processEncodeToken;
//	}

	public static void setProcessId(int sid) {
		processId = sid;
		
		String processToken = "";
		int tokenLen = 3;
		if (sid != 0) {
			//convert engine process id to 62 char string , 3 digit 238328, 4 digit 14776336
			int maxDigital = 62 * 62 * 62;
			if (tokenLen==4) {
				maxDigital *= 62;
			}
			int calInt = sid;
			if (calInt<0) {
				calInt = Math.abs(calInt);
			}
			while (calInt >= maxDigital) {
				calInt -= maxDigital;
			}
			
			StringBuilder sbProcessId = new StringBuilder();
			int remains = 0;
			do {
				remains = calInt % 62;
				sbProcessId.append(UniqueKeyGenerator.chars[remains]);
				calInt /= 62;
			} while (calInt > 0);
			
			processToken = sbProcessId.reverse().toString();
			while(processToken.length()<tokenLen) {
				processToken = "a"+processToken;
			}
			
			//decode process id
//			int len = processToken.length();
//			int finalValue = 0;
//			for (int i=0; i<processToken.length(); i++) {
//				String curs = processToken.substring(i, i+1);
//				int base = 0;
//				base = (int)Math.pow(62, len-i-1);
//				for (int k=0; k<UniqueKeyGenerator.chars.length; k++) {
//					if (UniqueKeyGenerator.chars[k].equals(curs)) {
//						base *= k;
//						break;
//					}
//				}
//				finalValue += base;
//			}
//			System.out.println("origi: "+engineProcessId);
//			System.out.println("recal: "+finalValue);
			
		} else {
			processToken = UniqueKeyGenerator.generateShortUUID(tokenLen);
		}
		processEncodeToken = processToken;
		
	}
	
	public static int getProcessId() {
		return processId;
	}
	
	public static synchronized int getProcessSequence() {
		processSequence++;
		if (processSequence > 2000000000) {
			processSequence = 1;
		}
		return processSequence;
	}
	
	public static synchronized int getSeqByKey(String key, int defaultValue) {
		if (hashSequences.containsKey(key)) {
			int retVal = (int)hashSequences.get(key);
			retVal ++;
			if (retVal > 2000000000) {
				retVal = 1;
			}
			hashSequences.put(key, new Integer(retVal));
			return retVal;
		} else {
			hashSequences.put(key, new Integer(defaultValue));
			return defaultValue;
		}
	}
	
	public static String getStringValueByKey(String key) {
		if (hashSequences.containsKey(key)) {
			return hashStr.get(key);
		}
		return "";
	}
	
	public static void setStringValueByKey(String key, String value) {
		hashStr.put(key, value);
	}
	
	public static void cleanHashStr() {
		hashStr.clear();
	}
	
	public static void cleanHashSeq() {
		hashSequences.clear();
	}
}
