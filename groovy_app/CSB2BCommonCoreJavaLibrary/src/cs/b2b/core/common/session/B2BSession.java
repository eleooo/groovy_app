package cs.b2b.core.common.session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.simplejavamail.email.Email;

public class B2BSession {

	public String sessionId;
	public String sessionStatus;
	public long sessionStart = -1;
	public long sessionEnd = -1;
	
	private Hashtable<String, String> hashPromoteProperty;
	
	//20170523, Alpaca cross process context
	private Map<String, Object> crossProcessContextMap;
	private List<Email> emails;
	
	public B2BSession(String _sessionId) {
		this.sessionId = _sessionId;
		this.sessionStart = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).getTimeInMillis();
		this.sessionStatus = "running";
	}
	
	public void setSessionId(String _sessionId) {
		this.sessionId = _sessionId;
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
	
	public String getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(String sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public long getSessionStart() {
		return sessionStart;
	}

	public void setSessionStart(long sessionStart) {
		this.sessionStart = sessionStart;
	}

	public long getSessionEnd() {
		return sessionEnd;
	}

	public void setSessionEnd(long sessionEnd) {
		this.sessionEnd = sessionEnd;
	}

	public void putSessionVal(String key, String val) {
		if (hashPromoteProperty==null)
			hashPromoteProperty = new Hashtable<String, String>();
		if (key==null || val==null || key.trim().length()==0 || val.length()==0)
			return;
		
		hashPromoteProperty.put(key, val);
	}
	
	public String getSessionVal(String key) {
		String ret = "";
		if (key!=null && hashPromoteProperty!=null && hashPromoteProperty.containsKey(key))
			ret = hashPromoteProperty.get(key);
		
		return ret;
	}
	
	public void removeSessionKey(String key) {
		if (key!=null && hashPromoteProperty!=null && hashPromoteProperty.containsKey(key))
			hashPromoteProperty.remove(key);
	}
	
	public Map<String, Object> getCrossProcessContextMap() {
		return crossProcessContextMap;
	}

	public void setCrossProcessContextMap(Map<String, Object> crossProcessContextMap) {
		this.crossProcessContextMap = crossProcessContextMap;
	}

	public List<Email> getEmails() {
		return emails;
	}

	public void addEmail(Email email) {
		if (email==null) {
			return;
		}
		if (this.emails==null) {
			this.emails = new ArrayList<Email>();
		}
		this.emails.add(email);
	}

	public void cleanSession() {
		if (hashPromoteProperty!=null)
			hashPromoteProperty.clear();
	}
}
