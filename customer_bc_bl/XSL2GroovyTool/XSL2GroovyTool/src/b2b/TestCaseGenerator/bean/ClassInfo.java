package b2b.TestCaseGenerator.bean;

import java.util.HashMap;
import java.util.Map;

public class ClassInfo {

	private String packageName;
	private String groovyScriptName;
	private String groovyPath;
	private Map<String,Integer> methodInfoMap;
	
	public ClassInfo() {
		
		packageName = "";
		groovyPath = "";
		groovyScriptName = "";
		methodInfoMap = new HashMap<String, Integer>();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getGroovyScriptName() {
		return groovyScriptName;
	}

	public void setGroovyScriptName(String groovyScriptName) {
		this.groovyScriptName = groovyScriptName;
	}

	public String getGroovyPath() {
		return groovyPath;
	}

	public void setGroovyPath(String groovyPath) {
		this.groovyPath = groovyPath;
	}

	public Map<String, Integer> getMethodInfoMap() {
		return methodInfoMap;
	}

	public void setMethodInfoMap(Map<String, Integer> methodInfoMap) {
		this.methodInfoMap = methodInfoMap;
	}
	
}
