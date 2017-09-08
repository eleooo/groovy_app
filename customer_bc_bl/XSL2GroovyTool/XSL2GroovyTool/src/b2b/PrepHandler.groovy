package b2b

import org.dom4j.Document;
import org.dom4j.DocumentHelper
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter

/**
 * @author RENGA
 *
 */
class PrepHandler {

	//remove useless element and template
	public static String xslPrepHandler(String xsl){

		//remove header
//		xsl = xsl.substring(xsl.indexOf("\n")+1);
		xsl = xsl.replaceAll('<\\?xml[\\w\\s=".-]*\\?>',"");
		//remove xsl:param
		xsl = xsl.replace('<xsl:param name="CdeConversionData"/>',"");
		//remove xsl:output
		xsl = xsl.replace('<xsl:output method="xml"/>',"");
		//remove comment
		xsl = xsl.replaceAll('(?s)<!--.+?-->',"");
		
		//handle template
		String templateKeyWord = '<xsl:template'
		String templateKeyWordEnd = '</xsl:template>'
		String callTemplateKeyWord = '<xsl:call-template name='
		String callTemplateKeyWordEnd = '</xsl:call-template>'
		String stytleKeyWord = '</xsl:stylesheet>'
		
		String[] templates  = xsl.split(templateKeyWord)

		String result = templates[0];
				
		String body =  templates[2].substring(templates[2].indexOf('>') + 1).replace(templateKeyWordEnd,"").replace(stytleKeyWord,"")
		
		Map map = new HashMap<String, String>();
		for(int i = 3; i < templates.length; i++){
			//key = "template name", value = template content
			map.put(templates[i].substring(0,templates[i].indexOf('>')).replace("name=","").trim(),templates[i].substring(templates[i].indexOf('>') + 1).replace("</xsl:template>","").replace(stytleKeyWord,""))
		}
		
		String[] callTemplate = body.split(callTemplateKeyWord);
		
		for(int i = 1 ; i < callTemplate.length ; i++){
			//find the target tag of call-template
			String tag = callTemplateKeyWord + callTemplate[i].substring(0,callTemplate[i].indexOf('>')+1)
			//get template name
			String templateName = tag.replace(callTemplateKeyWord,'').replace('>','').replace('/','')
			
			String template =  map.get(templateName);
			if(!tag.endsWith("/>")){
				//the case of call-template included params
				template = template.replaceAll('<xsl:param name=["\\w/>]*',"");	//remove param
				int begin = body.indexOf(tag)
				int end = body.indexOf(callTemplateKeyWordEnd, begin)
				tag = body.substring(begin,end) + callTemplateKeyWordEnd
				String[] params = tag.split('<xsl:with-param')
				for(int j = 1; j<params.length; j++){
					//change param to variable
					template = '<xsl:variable ' + params[j].replace(callTemplateKeyWordEnd,'').trim() + '\n' + template 
				}
			}
			
			//replace the call-template with template
			body = body.replace(tag,template)
		}
		
		result += body += stytleKeyWord;
		
		return result;
	}
	
	
	public static String formatXml(String xmlStr){
		Document doc = DocumentHelper.parseText(xmlStr);

		StringWriter writer = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
  
		XMLWriter xmlwriter = new XMLWriter(writer, format);
		xmlwriter.write(doc); 
        return writer.toString();  
	}
	
	static void main(String[] args) {
		String xslName = "AgeGroup_850_to_OLL_POXML_Map.xml";

		File file = new File("""input/${xslName}""");
		String xsl = file.text

		xsl = PrepHandler.xslPrepHandler(xsl)
		println PrepHandler.formatXml(xsl)
	

	}
	
}
