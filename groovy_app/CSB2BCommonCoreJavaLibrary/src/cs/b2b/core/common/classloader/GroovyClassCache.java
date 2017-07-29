package cs.b2b.core.common.classloader;

import java.util.ArrayList;
import java.util.List;

public class GroovyClassCache {
	public String name = null;
	public String versionInfo = null;
	public long loadTimestamp = 0;
	public int callCount = 0;
	public String scriptBody = null;
	public List<GroovyClassCacheItem> listItem = new ArrayList<GroovyClassCacheItem>();
}
