package b2b

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Document

/**
 * @author RENGA
 *
 */
class B2BXSL2Groovy {

	public String process(String xsl){
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
	
		InputStream inputStream = new ByteArrayInputStream(xsl.bytes)
		Document document    = builder.parse(inputStream)
		StringWriter output      = new StringWriter()
		DomToGroovy converter   = new DomToGroovy(new PrintWriter(output))
	
		converter.print(document)
		
		return  output.toString().trim();
	}
}
