package cs.b2b.core.common.util;

import java.util.ArrayList;
import java.util.List;

public class HandlerAdvanceResult {
	
	private static Object lock = new Object();
	
	public String uuidKey = null;
	private String exceptionDesc = null;
	private List<String> resultList = null;
	
	public String getException() {
		return (exceptionDesc==null?"":exceptionDesc.trim());
	}
	
	public void setException(String s) {
		exceptionDesc = s;
	}
	
	public boolean hasResult() {
		return (resultList==null?false:resultList.size()>0);
	}
	
	public void addResult(String str) {
		synchronized(lock) {
			if (resultList==null)
				resultList = new ArrayList<String>();
			resultList.add(str);
		}
	}
	
	public String getResult() {
		String ret = "";
		synchronized(lock) {
			if (resultList!=null && resultList.size()>0) {
				ret = resultList.get(0);
				resultList.remove(0);
			}
		}
		return ret;
	}
}
