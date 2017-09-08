package xmlToGroovy

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.codehaus.groovy.tools.xml.DomToGroovy;
import org.w3c.dom.Document
import org.w3c.dom.NodeList;
import org.w3c.dom.Text

class XmlToMD {

	static Map<String,Integer> map = [:];
	
	static void main(String[] str){

//		String xslName = "cs2xmlCT.xml"
//		String xslName = "blCS2xml.xml"
		String xslName = "blXml.xml"

		File file = new File("""input/${xslName}""");
		String xsl = file.text

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

		InputStream inputStream = new ByteArrayInputStream(xsl.bytes)
		Document document    = builder.parse(inputStream)


		nodeTranserse(document.getDocumentElement(),0);
	}


	public static void nodeTranserse(org.w3c.dom.Node node,int level){
		String rNodeName=node.getNodeName();
		
		printTag(level)
		level = level + 1
		if(node.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
			print rNodeName
		}

		NodeList allNodes=node.getChildNodes();
		int size=allNodes.getLength();
		if(size>0){
			for(int j=0;j<size;j++){
				org.w3c.dom.Node childNode=allNodes.item(j);

				if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
					if(childNode.getChildNodes().length <= 1){
						printTag(level)
						print  childNode.getNodeName() ;
					}else{
						
						nodeTranserse(childNode,level);
					}
				}
				
			}
//			printTag(level)

		}
	}
	
	static void printTag(int level){
		for(int i = 0; i<level; i++){
			if( i == 0){
				println  ''
			}else{
				print ','
			}
		}
	}
}


