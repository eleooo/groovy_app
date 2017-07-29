package cs.b2b.core.common.classloader;

import java.util.Hashtable;

import cs.b2b.core.common.util.Base64Util;

public class HandlerClassesLoader extends ClassLoader{

	static String classPackagePreffix = "cs.b2b.handler.";
	
	@SuppressWarnings("rawtypes")
	static Hashtable<String, Class> hashClass = new Hashtable<String, Class>();
	static Hashtable<String, String> hashClassTimeStamp = new Hashtable<String, String>();
	static Hashtable<String, String> hashClassStringDefine = new Hashtable<String, String>();
	
	/**
	 * if class not exists then load
	 *    after that, no reload if class file timestamp is same
	 * @param classFileFullName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClassDef(String className, String classTs, String classDefineInBase64) throws Exception {
		if (hashClass.containsKey(className)) {
			Class clsInHash = hashClass.get(className);
			String classTsInHash = hashClassTimeStamp.get(className);
			
			if (classTs!=null && classTsInHash!=null && classTsInHash.equals(classTs)) {
				return clsInHash;
			}
		}
		hashClassStringDefine.put(className, classDefineInBase64);
		
		HandlerClassesLoader classLoader = new HandlerClassesLoader(HandlerClassesLoader.class.getClassLoader());
		Class rclass = classLoader.loadClass(className);
		hashClass.put(className, rclass);
		hashClassTimeStamp.put(className, classTs);
		
		return rclass;
	}
	
    public HandlerClassesLoader(ClassLoader parent) {
        super(parent);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Class loadClass(String className) throws ClassNotFoundException {
    	
    	if(! className.endsWith(".class"))
    		return super.loadClass(className);
    	
    	String classNameFullName = null;
    	if (className.endsWith(".class")) {
    		classNameFullName = classPackagePreffix + className.replace(".class", "");
		}
		try {
    		String classDefineStrBase64 = hashClassStringDefine.get(className);
    		if (classDefineStrBase64==null || classDefineStrBase64.length()==0)
    			throw new Exception("Class define string is not found.");
    		
    		byte[] classData = Base64Util.getBytesFromBASE64(classDefineStrBase64);
    		if (classData!=null && classData.length>0) {
        		return defineClass(classNameFullName, classData, 0, classData.length);
        	}
        } catch (Exception e) {
            throw new ClassNotFoundException("Exception in Handler.loadClass: "+className+", e: "+e.getMessage());
        }
    	return null;
    }
}
