package cs.b2b.core.common.session;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.simplejavamail.email.Email;

public class B2BRuntimeSession {

	private static Hashtable<String, B2BSession> hashB2BEDISession = new Hashtable<String, B2BSession>();
	
	public static Hashtable<String, B2BSession> getSessionData() {
		return hashB2BEDISession;
	}
	
	/**
	 * session handling, add session key-value
	 * @param sessionId
	 * @param key
	 * @param value
	 * @return
	 */
	public static String addSessionValue(String sessionId, String key, String value) {
		if (sessionId==null || key==null || value==null)
			return "false";
		B2BSession os = null;
		if (!hashB2BEDISession.containsKey(sessionId)) {
			os = new B2BSession(sessionId);
			hashB2BEDISession.put(sessionId, os);
		} else {
			os = hashB2BEDISession.get(sessionId);
		}
		os.putSessionVal(key, value);
		return "true";
	}
	
	public static void addSessionCrossProcessContext(String sessionId, Map<String, Object> crossProcessContextMap) {
		if (sessionId==null || sessionId.trim().length()==0 || crossProcessContextMap==null)
			return;
		B2BSession os = null;
		if (!hashB2BEDISession.containsKey(sessionId)) {
			os = new B2BSession(sessionId);
			hashB2BEDISession.put(sessionId, os);
		} else {
			os = hashB2BEDISession.get(sessionId);
		}
		os.setCrossProcessContextMap(crossProcessContextMap);
		return;
	}
	
	public static Map<String, Object> getCrossProcessContextMap(String sessionId) {
		B2BSession os = hashB2BEDISession.get(sessionId);
		if (os!=null) {
			return os.getCrossProcessContextMap();
		}
		return null;
	}
	
	public static List<Email> getEmails(String sessionId) {
		B2BSession os = hashB2BEDISession.get(sessionId);
		if (os!=null) {
			return os.getEmails();
		}
		return null;
	}
	
	public static void addEmail(String sessionId, Email email) {
		if (email==null) {
			return;
		}
		B2BSession os = hashB2BEDISession.get(sessionId);
		if (os!=null) {
			os.addEmail(email);
		}
	}
	
	public static boolean containsSession(String sessionId) {
		return hashB2BEDISession.containsKey(sessionId);
	}
	
	public static String initSession(String sessionId) {
		if (sessionId==null)
			return "false";
		if (!hashB2BEDISession.containsKey(sessionId)) {
			B2BSession os = new B2BSession(sessionId);
			hashB2BEDISession.put(sessionId, os);
		}
		return "true";
	}
	
	public static String getSessionValue(String sessionId, String key) {
		if (sessionId==null || key==null)
			return "";
		if (!hashB2BEDISession.containsKey(sessionId)) {
			return "";
		} else {
			B2BSession os = hashB2BEDISession.get(sessionId);
			return os.getSessionVal(key);
		}
	}
	
	public static String cleanSessionById(String sessionId) {
		if (sessionId==null)
			return "false";
		if (!hashB2BEDISession.containsKey(sessionId)) {
			return "false";
		} else {
			B2BSession os = hashB2BEDISession.get(sessionId);
			os.setSessionEnd(Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).getTimeInMillis());
			os.setCrossProcessContextMap(null);
			os.setSessionStatus("finished");
			os.cleanSession();
			hashB2BEDISession.remove(sessionId);
			return "true";
		}
	}
}
