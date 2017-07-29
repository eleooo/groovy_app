package cs.b2b.core.common.classloader;

import java.util.Hashtable;

import cs.b2b.core.common.util.Base64Util;

public class CommonClassesLoader extends ClassLoader{

	@SuppressWarnings("rawtypes")
	static Hashtable<String, Class> hashCommonClass = new Hashtable<String, Class>();
	static Hashtable<String, String> hashCommonClassTimeStamp = new Hashtable<String, String>();
	static Hashtable<String, String> hashCommonClassStringDefine = new Hashtable<String, String>();
	
	/**
	 * if class no exists then load into jvm
	 *    after that, no reload if class file timestamp is same
	 *    
	 *  Attention: it only works for if className start with package name cs.b2b.common.dynamicload.*
	 *     
	 * @param classFileFullName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClassDef(String className, String classTs, String classDefineInBase64) throws Exception {
		if (hashCommonClass.containsKey(className)) {
			Class clsInHash = hashCommonClass.get(className);
			String classTsInHash = hashCommonClassTimeStamp.get(className);
			
			if (classTs!=null && classTsInHash!=null && classTsInHash.equals(classTs)) {
				return clsInHash;
			}
		}
		
		hashCommonClassStringDefine.put(className, classDefineInBase64);
		
		CommonClassesLoader classLoader = new CommonClassesLoader(CommonClassesLoader.class.getClassLoader());
		Class rclass = classLoader.loadClass(className);
		
		hashCommonClass.put(className, rclass);
		hashCommonClassTimeStamp.put(className, classTs);
		
		return rclass;
	}
	
    public CommonClassesLoader(ClassLoader parent) {
        super(parent);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Class loadClass(String className) throws ClassNotFoundException {
    	
    	if(! className.startsWith("cs.b2b.core.mapping.util."))
    		return super.loadClass(className);
    	
    	String classDefineStrBase64 = hashCommonClassStringDefine.get(className);
		if (classDefineStrBase64==null || classDefineStrBase64.length()==0)
			throw new ClassNotFoundException("Class define string is not found. ");
		
    	try {
    		byte[] classData = Base64Util.getBytesFromBASE64(classDefineStrBase64);
    		if (classData!=null && classData.length>0) {
        		return defineClass(className, classData, 0, classData.length);
        	}
        } catch (Exception e) {
            throw new ClassNotFoundException("Exception in CommonClassLoader.loadClass: "+className+", exception: "+e.getMessage() + ". ");
        }
    	return null;
    }
}
