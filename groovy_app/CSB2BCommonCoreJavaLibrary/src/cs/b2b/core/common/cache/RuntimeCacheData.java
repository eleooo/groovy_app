package cs.b2b.core.common.cache;

import java.util.Hashtable;

public class RuntimeCacheData {
	
	private static long cacheCheckInterval = 0;
	private static Hashtable<String, CacheItem> runtimeCaches = new Hashtable<String, CacheItem>();
	
	private static long last10minsCheckTs = 0;
	
	public static void updateCacheData(String scriptName, String value) {
		CacheItem _ci = null;
		if (runtimeCaches.containsKey(scriptName)) {
			_ci = runtimeCaches.get(scriptName);
		} else {
			_ci = new CacheItem();
			runtimeCaches.put(scriptName, _ci);
		}
		_ci.value = value;
		if (cacheCheckInterval>0) {
			_ci.isOverTs = false;
		} else {
			_ci.isOverTs = true;
		}
		_ci.lastUpdatePoint = System.currentTimeMillis();
	}
	
	public static CacheItem getCacheData(String scriptName) {
		CacheItem _ci = null;
		if (runtimeCaches.containsKey(scriptName)) {
			_ci = runtimeCaches.get(scriptName);
			if ((!_ci.isOverTs) && ((System.currentTimeMillis() - _ci.lastUpdatePoint) > cacheCheckInterval)) {
				_ci.isOverTs = true;
			}
		}
		return _ci;
	}
	
	public static String setCacheInterval(String interval) {
		String result = "";
		if (interval==null || interval.trim().length()==0) {
			if (cacheCheckInterval != 0) {
				result = "Cache Check Interval Updated from "+cacheCheckInterval+" to 0. ";
			}
			cacheCheckInterval = 0;
		} else {
			try {
				long _lnew = Long.parseLong(interval);
				if (_lnew != cacheCheckInterval) {
					result = "Cache Check Interval Updated from "+cacheCheckInterval+" to "+_lnew+". ";
				}
				cacheCheckInterval = _lnew;
			} catch (Exception ex) {
				cacheCheckInterval = 600000;
				result = "Cache Check Interval Updated to "+cacheCheckInterval+" as input setting is not valid number. ";
			}
		}
		return result;
	}
	
	public static boolean isOver10mins() {
		if ((System.currentTimeMillis() - last10minsCheckTs) > 600000) {
			last10minsCheckTs = System.currentTimeMillis();
			return true;
		} else {
			return false;
		}
	}
}
