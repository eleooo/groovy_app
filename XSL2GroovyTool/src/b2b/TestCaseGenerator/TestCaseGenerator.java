package b2b.TestCaseGenerator;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import b2b.TestCaseGenerator.bean.ClassInfo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;

public class TestCaseGenerator {

	public static String Template_Folder = "template/testCase";
	public static String Template_Name = "testCase.ftl";
	public static String Test_Case_Suffix = "Test.groovy";

	public void generate(String groovyPath) throws Exception{
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);   
        cfg.setEncoding(Locale.getDefault(), "UTF-8");   
        cfg.setDirectoryForTemplateLoading(new File(Template_Folder));   
        Template temp = cfg.getTemplate(Template_Name); 
  
        ClassInfo classInfo = getClassInfo(groovyPath);
  
        Map<Object,Object> root = new HashMap<Object,Object>();   
        root.put("classInfo", classInfo);
        
        FileWriter w = new FileWriter(new File(classInfo.getGroovyPath() + "/" + classInfo.getGroovyScriptName() + Test_Case_Suffix));
//        java.io.StringWriter w =new StringWriter();  
        
        temp.process(root, w);   
        w.close();
	}
	
	private ClassInfo getClassInfo(String groovyPath) throws Exception{
		ClassInfo classInfo = new ClassInfo();
		
		File file = new File(groovyPath);
		
		if(file.exists()){
		
			classInfo.setGroovyScriptName(file.getName().replace(".groovy", ""));
			classInfo.setGroovyPath(file.getParent());
			
			GroovyCodeSource gc = new GroovyCodeSource(new File(groovyPath));
			GroovyClassLoader classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
			Class groovyClass = classLoader.parseClass(gc);
			GroovyObject instance = (GroovyObject) groovyClass.newInstance();
			
			for(Method method : instance.getClass().getMethods()){
				String declaringClass = method.getDeclaringClass().getName();
				String methodName = method.getName();
				if(declaringClass.contains(file.getName().replace(".groovy", "")) && !methodName.contains("$")){
					if(methodName != "main" && methodName != "run"){
//						System.out.println(methodName + " " + method.getParameterTypes().length);
						classInfo.getMethodInfoMap().put(methodName, method.getParameterTypes().length);
					}
				}
			}
		}else{
			throw new Exception("Groovy Script "+ groovyPath +" is not existing");
		}
		
		return classInfo;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Start test case generator");
		TestCaseGenerator generator = new TestCaseGenerator();
		generator.generate("output\\AgeGroup_850_to_OLL_POXML_Map_.groovy");
		System.out.println("End test case generator");
	}
}
