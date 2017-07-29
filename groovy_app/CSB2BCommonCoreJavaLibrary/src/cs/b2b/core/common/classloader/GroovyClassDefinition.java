package cs.b2b.core.common.classloader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.GroovyClassValue;

import groovy.lang.GroovyObject;

public class GroovyClassDefinition {
	protected String name = null;
	protected String groovyScript = null;
	protected String versionInfo = null;
	
	@SuppressWarnings("rawtypes")
	protected Class mainClass = null;
//	public List<GroovyClassItem> listItem = new ArrayList<GroovyClassItem>();
	
	//keep class redefine with class bytes in cache
	@SuppressWarnings("rawtypes")
	private List<Class> listClassInUse = new ArrayList<Class>();
	
	public void addClass(Class<?> cls) {
		listClassInUse.add(cls);
	}
	
	public GroovyObject getInstance() throws InstantiationException, IllegalAccessException {
		return (GroovyObject)mainClass.newInstance();
	}
	
	public void close() throws Exception {
		mainClass = null;
		for (Class<?> cls : listClassInUse) {
			clearClassInfoInGroovyCache(cls);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void clearClassInfoInGroovyCache(Class<?> type) throws Exception {
		if (type==null)
			return;
		Field globalClassValue = ClassInfo.class.getDeclaredField("globalClassValue");
		globalClassValue.setAccessible(true);
		@SuppressWarnings("rawtypes")
		GroovyClassValue classValueBean = (GroovyClassValue) globalClassValue.get(null);
	    classValueBean.remove(type);
	}
}
