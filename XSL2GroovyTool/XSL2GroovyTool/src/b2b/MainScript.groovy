package b2b

println "XSL to Groovy Conversion start"

//String xslName = "AgeGroup_850_to_OLL_POXML_Map.xml";
//String xslName = "BlueLinx_850_1dot0_To_OLL_PO_XML_2dot01.xml";
//String xslName = "Carhartt_PO_1dot0_to_POXML_Map.xml"
//String xslName = "EchoStar_850_to_997_Map.xml"
//String xslName = "EchoStar_PO_UIF_2dot0_To_OLL_HTS_XML_2dot01_Map.xml"
String xslName = "CT_Mapping_DSGOODS_2.0.xml"

File file = new File("""input/${xslName}""");
String xsl = file.text

//remove header
//xsl = xsl.substring(xsl.indexOf("\n")+1);
//xsl = PrepHandler.xslPrepHandler(PrepHandler.formatXml(xsl))
//println xsl

//XSL to Groovy
B2BXSL2Groovy b2bXSL2Groovy = new B2BXSL2Groovy();
String groovy = b2bXSL2Groovy.process(xsl);

File header = new File("template/header.txt")
File footer = new File("template/footer.txt")

//output
File output = new File("output/" + xslName.replace(".xml",".groovy"));
output.write(header.text + PospHandler.postHandler(groovy) + footer.text)


println "XSL to Groovy Conversion done"









