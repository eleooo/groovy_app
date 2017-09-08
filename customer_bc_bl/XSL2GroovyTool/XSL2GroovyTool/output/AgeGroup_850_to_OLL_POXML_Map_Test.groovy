
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class AgeGroup_850_to_OLL_POXML_Map_Test {

	String groovyPath = "output";
	String groovyScriptName = "AgeGroup_850_to_OLL_POXML_Map_.groovy";
	GroovyObject instance = null;	
	
	public AgeGroup_850_to_OLL_POXML_MapTest() throws Exception{
		GroovyCodeSource gc = new GroovyCodeSource(new File(groovyPath + groovyScriptName));
		GroovyClassLoader classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
		Class groovyClass = classLoader.parseClass(gc);
		instance = (GroovyObject) groovyClass.newInstance();
	}
	
	@Test
	public void testGetConversion() {
		Object[] params = new Object[6];
		Object obj = instance.invokeMethod("getConversion",params);
		Assert.assertNotNull(obj);
	}
	@Test
	public void testGetRuntimeParameter() {
		Object[] params = new Object[2];
		Object obj = instance.invokeMethod("getRuntimeParameter",params);
		Assert.assertNotNull(obj);
	}
	@Test
	public void testNotEmpty() {
		Object[] params = new Object[1];
		Object obj = instance.invokeMethod("notEmpty",params);
		Assert.assertNotNull(obj);
	}
	@Test
	public void testMapping() {
		Object[] params = new Object[]{"<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>test</test>", null, null };
		Object obj = instance.invokeMethod("mapping",params);
		Assert.assertNotNull(obj);
	}
	@Test
	public void testConvertDateTime() {
		Object[] params = new Object[3];
		Object obj = instance.invokeMethod("convertDateTime",params);
		Assert.assertNotNull(obj);
	}
	@Test
	public void testGetConversionWithDefault() {
		Object[] params = new Object[7];
		Object obj = instance.invokeMethod("getConversionWithDefault",params);
		Assert.assertNotNull(obj);
	}

}
