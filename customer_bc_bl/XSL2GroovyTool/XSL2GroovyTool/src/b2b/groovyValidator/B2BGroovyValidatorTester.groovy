package b2b.groovyValidator

class B2BGroovyValidatorTester {
	
	static void main(String[] argv){
		
		B2BGroovyValidator validator = new B2BGroovyValidator();
		
//		File file = new File("output\\AgeGroup_850_to_OLL_POXML_Map.groovy");
//		File file = new File("output\\AgeGroup_850_to_OLL_POXML_Map_.groovy");
//		File file = new File("output\\BlueLinx_850_1dot0_To_OLL_PO_XML_2dot01.groovy");
		File file = new File("output\\Carhartt_PO_1dot0_to_POXML_Map.groovy");
//		File file = new File("output\\HM_PO_1dot7_to_POXML_2dot01.groovy");
//		File file = new File("output\\RackRoomShoes_PO_UIF_to_OLL_POXML_2dot01_Map.groovy");
		
		println validator.validGroorvy(file.text);
		
	}
}
