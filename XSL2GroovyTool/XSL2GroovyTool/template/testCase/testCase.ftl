<#if classInfo.packageName != "">
package ${classInfo.packageName};
</#if>

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class ${classInfo.groovyScriptName}Test {

	String groovyPath = "${classInfo.groovyPath}";
	String groovyScriptName = "${classInfo.groovyScriptName}.groovy";
	GroovyObject instance = null;	
	
	public AgeGroup_850_to_OLL_POXML_MapTest() throws Exception{
		GroovyCodeSource gc = new GroovyCodeSource(new File(groovyPath + groovyScriptName));
		GroovyClassLoader classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
		Class groovyClass = classLoader.parseClass(gc);
		instance = (GroovyObject) groovyClass.newInstance();
	}
	
<#list classInfo.methodInfoMap?keys as key>
	@Test
	public void test${key?cap_first}() {
	<#if key == "mapping">
		Object[] params = new Object[]{"<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>test</test>", null, null };
	<#else>
		Object[] params = new Object[${classInfo.methodInfoMap[key]}];
	</#if>
		Object obj = instance.invokeMethod("${key}",params);
		Assert.assertNotNull(obj);
	}
</#list>

}
