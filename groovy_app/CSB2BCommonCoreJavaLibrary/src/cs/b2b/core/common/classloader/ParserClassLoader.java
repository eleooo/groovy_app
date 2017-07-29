package cs.b2b.core.common.classloader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import cs.b2b.core.common.util.Base64Util;
import cs.b2b.core.common.util.StringUtil;

public class ParserClassLoader extends ClassLoader {

	private static JarFileReader reader = null;
	private static String jarLastUpdatedTs = "";
	
	@SuppressWarnings("rawtypes")
	static Hashtable<String, Class> hashClass = new Hashtable<String, Class>();
	
	/**
	 * if class exist then load
	 *    after that, no reload if class has been deleted, and no reload if class file timestamp same, and no reload if (now() - last same checkpoint) less than 60 seconds
	 * @param classFileFullName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClassDef(String className, String jarUpdatedTs, String jarBase64String) throws Exception {
		if (StringUtil.isEmpty(className) || StringUtil.isEmpty(jarBase64String))
			throw new Exception("ClassName or Jar is empty.");
		
		if (reader==null || !hashClass.containsKey(className) || (!StringUtil.isEmpty(jarUpdatedTs) && !jarLastUpdatedTs.equals(jarUpdatedTs))) {
			InputStream inbyte = new ByteArrayInputStream(Base64Util.getBytesFromBASE64(jarBase64String));
			reader = new JarFileReader(inbyte);
			reader.readEntries();
			jarLastUpdatedTs = jarUpdatedTs;
			
			ParserClassLoader classLoader = new ParserClassLoader(ParserClassLoader.class.getClassLoader());
			hashClass.put(className, classLoader.loadClass(className));
		}
		return hashClass.get(className);
	}
	
    public ParserClassLoader(ClassLoader parent) {
        super(parent);
    }    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Class loadClass(String className) throws ClassNotFoundException {
    	if (!reader.containsCopy(className)) {
    		return super.loadClass(className);
    	}
    	
		try {
    		byte[] classData = reader.getCopy(className);
    		if (classData!=null && classData.length>0) {
        		return defineClass(className, classData, 0, classData.length);
        	}
        } catch (Exception e) {
            throw new ClassNotFoundException("Exception in Handler.loadClass: "+className+", e: "+e.getMessage());
        }
    	return null;
    }
}

