package cs.b2b.core.beluga.lightbc;

import java.lang.reflect.Method;

import cs.b2b.core.common.classloader.ParserClassLoader;

public class UIFXMLParser {

	@SuppressWarnings("rawtypes")
	public static String transformUIF2XML(String originalUif, String configLine, String jarUpdatedTs, String jarBase64String) throws Exception {
		Class clss = ParserClassLoader.getClassDef("cs.b2b.beluga.common.fileparser.UIFFileParser", jarUpdatedTs, jarBase64String);
		Class[] clsss = new Class[]{String.class, String.class};
	    @SuppressWarnings("unchecked")
		Method method = clss.getDeclaredMethod("parseEDI2XML", clsss);
	        
	    Object[] obj = new Object[]{originalUif, configLine};
	    return method.invoke(clss.newInstance(), obj).toString();
	}
	
	@SuppressWarnings("rawtypes")
	public String transformXML2UIF(String xml, String configLine, String jarUpdatedTs, String jarBase64String) throws Exception {
		Class clss = ParserClassLoader.getClassDef("cs.b2b.beluga.common.fileparser.UIFFileParser", jarUpdatedTs, jarBase64String);
		Class[] clsss = new Class[]{String.class, String.class};
	    @SuppressWarnings("unchecked")
		Method method = clss.getDeclaredMethod("parseXML2EDI", clsss);
	       
	    Object[] obj = new Object[]{xml, configLine};
	    return method.invoke(clss.newInstance(), obj).toString();
	}
}
