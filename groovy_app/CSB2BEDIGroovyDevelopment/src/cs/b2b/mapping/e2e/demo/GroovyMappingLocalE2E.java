package cs.b2b.mapping.e2e.demo;

import cs.b2b.mapping.e2e.util.DemoBase;

public class GroovyMappingLocalE2E extends DemoBase {
	
	public static byte[] mapUtilBytes = null;
	public static String mapUtilName = null;
	
	public static void main(String[] args) {
		println("E2E Start.");
		
		try {
			//1, Demo for XML to XML mapping
//			println("------------- 1, XML to XML Start ---------------");
//			DemoGroovy_Xml2Xml x2x = new DemoGroovy_Xml2Xml();
//			x2x.demo();
//			println("------------- 1, XML to XML Finished ---------------");
			
//			//2, Demo for EDI to XML mapping
//			println("------------- 2, EDI to XML Start ---------------");
//			DemoGroovy_EDI2Xml e2x = new DemoGroovy_EDI2Xml();
//			e2x.demo();
//			println("------------- 2, EDI to XML Finished ---------------");
			
			//3, Demo for XML to EDI
//			println("------------- 3, XML to EDI Start ---------------");
			DemoGroovy_Xml2EDI x2e = new DemoGroovy_Xml2EDI();
			x2e.demo();
//			println("------------- 3, XML to EDI Finished ---------------");
			
		} catch (Exception e) {
			Throwable t = e.getCause()==null?e:e.getCause();
			while(t.getCause()!=null) {
				t = t.getCause();
			}
			t.printStackTrace();
		}
		
		println("E2E Finished.");
	}
}
