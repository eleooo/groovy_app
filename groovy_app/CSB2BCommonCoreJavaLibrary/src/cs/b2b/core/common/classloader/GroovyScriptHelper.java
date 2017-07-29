package cs.b2b.core.common.classloader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.GroovyClassValue;
import org.codehaus.groovy.tools.GroovyClass;

public class GroovyScriptHelper {
	
	//20160601 - new cache and compile implementation
	static Hashtable<String, GroovyClassCache> hashGroovyClassBytesCache = new Hashtable<String, GroovyClassCache>();
	
//	static String commonUtilScriptBodyInCache = ""; 
//	//this class requires locate in package cs.b2b.core.mapping.util
//	//  then this file name is cs.b2b.core.mapping.util.MappingUtil.groovy
//	//  and you also need add a empty class in this core jar, cs.b2b.core.mapping.util.MappingUtil.java, 
//	//    it's for groovy high level compile pass, but details implementation locate in MappingUtil.groovy
//	static final String CommonUtilGroovyClassName = "MappingUtil";
	
	/**
	 * if class not exists then load and build
	 *    if script is same in memory then not compile, return cache define class bytes.
	 * @param groovyScripts - LinkedHashMap - sequence required, put java bean calss first, then then MappingUtil, and then MsgType common script, PMT script put to the last one.
	 *      ** Important: the last one script compile, need pre-load previouse resource first as has dependency.
	 *      
	 *     //old version: String groovyName, String groovyScript, String commonUtilScriptBody
	 * @return
	 * @throws Exception
	 */
	public static GroovyClassDefinition getClassDef(LinkedHashMap<String, String> groovyScripts) throws Exception {
		List<String> loadKeys = new ArrayList<String>();
		
//		long ts = System.currentTimeMillis();
		
		for (String scriptName : groovyScripts.keySet()) {
			String scriptBody = groovyScripts.get(scriptName);
			
			boolean needReCompile = true; 
			//check if groovy script is change or not
			if (hashGroovyClassBytesCache.containsKey(scriptName)) {
				GroovyClassCache gcc = hashGroovyClassBytesCache.get(scriptName);
				//if exists, and the timestamp same as define time 
				if (gcc!=null && gcc.scriptBody != null && gcc.scriptBody.equals(scriptBody)) {
					//if not change, then get from cache and re-define calss from byte[] 
					needReCompile = false;
				}
			}
			
			if (needReCompile) {
				//preload compile class
				compileScript(scriptName, scriptBody, loadKeys);
			}
			loadKeys.add(scriptName);
		}
		
//		long te = System.currentTimeMillis();
//		System.out.println("compile: "+(te-ts));
		
		//generate groovy script main class for invoke
		return generateReDefineClass(loadKeys);
	}
	
	@SuppressWarnings("rawtypes")
	private static void compileScript(String groovyName, String groovyScript, List<String> preloadScripts) throws Exception {
		//compile the new version
		CompilationUnit compileUnit = new CompilationUnit();
		
		List<Class> preloadClasses = null;
		if (preloadScripts!=null && preloadScripts.size()>0) {
			preloadClasses = new ArrayList<Class>();
			//need preload the scripts to class loader to compile scripts
			//put common script calss for pmt class building
			for (String prename : preloadScripts) {
				GroovyClassCache _pregcc = hashGroovyClassBytesCache.get(prename);
				if (_pregcc!=null && _pregcc.listItem!=null) {
					for (GroovyClassCacheItem _gcci : _pregcc.listItem) {
						preloadClasses.add(compileUnit.getClassLoader().defineClass(_gcci.name, _gcci.cls));
					}
				}
			}
		}
		
	    compileUnit.addSource(groovyName, groovyScript);
	    compileUnit.compile(Phases.CLASS_GENERATION);
	    
	    if (preloadClasses!=null) {
	    	for(Class _cls : preloadClasses) {
	    		clearClassInfoInGroovyCache(_cls);
	    	}
	    }
	    
	    //** Important: if need recompile, generate new gcc cache
	    GroovyClassCache gcc = new GroovyClassCache();
	    gcc.scriptBody = groovyScript;
	    gcc.name = groovyName;
	    if (groovyName.endsWith(".groovy")) {
	    	//remove .groovy in the name end part
	    	gcc.name = groovyName.substring(0, groovyName.length()-7);
	    }
		if (gcc.name.indexOf(".")>0) {
			gcc.name = gcc.name.substring(gcc.name.lastIndexOf(".")+1);
		}
		hashGroovyClassBytesCache.put(groovyName, gcc);
		gcc.versionInfo = null;
		for (Object compileClass : compileUnit.getClasses()) {
	    	GroovyClass groovyClass = (GroovyClass) compileClass;
	    	GroovyClassCacheItem gci = new GroovyClassCacheItem();
	    	gci.name = groovyClass.getName();
	    	gci.cls = groovyClass.getBytes();
	    	gcc.listItem.add(gci);
    	}
	    gcc.loadTimestamp = System.currentTimeMillis();
	}
	
	private static GroovyClassDefinition generateReDefineClass(List<String> groovyNames) throws Exception {
		GroovyClassDefinition gcd = new GroovyClassDefinition();
		groovy.lang.GroovyClassLoader classLoader = new groovy.lang.GroovyClassLoader(Thread.currentThread().getContextClassLoader());
		
		for(int i=0; i<groovyNames.size(); i++) {
			String name = groovyNames.get(i);
			GroovyClassCache gcc = hashGroovyClassBytesCache.get(name);
			if (gcc==null || gcc.listItem==null || gcc.listItem.size()==0)
				throw new Exception("Cannot find GroovyCalssCache for: "+name+", please check. ");
			
			//load classes
			for(GroovyClassCacheItem gci : gcc.listItem) {
		    	//get real class from GroovyClassMain
		    	Class<?> cls = classLoader.defineClass(gci.name, gci.cls);
		    	gcd.addClass(cls);
		    	
		    	if (gci.name.equals(gcc.name)) {
		    		if (i==groovyNames.size()-1) {
		    			gcd.mainClass = cls;
		    		}
		    	} else if (gci.name.contains(".")) {
		    		//if groovy class contains package name
		    		if (gci.name.endsWith("."+gcc.name)) {
		    			if (i==groovyNames.size()-1) {
		    				gcd.mainClass = cls;
		    			}
		    		}
			   }
			}
		}
	    return gcd;
	}
	
	@SuppressWarnings("unchecked")
	private static void clearClassInfoInGroovyCache(Class<?> type) throws Exception {
		if (type==null)
			return;
		Field globalClassValue = ClassInfo.class.getDeclaredField("globalClassValue");
		globalClassValue.setAccessible(true);
		@SuppressWarnings("rawtypes")
		GroovyClassValue classValueBean = (GroovyClassValue) globalClassValue.get(null);
	    classValueBean.remove(type);
	}
}
