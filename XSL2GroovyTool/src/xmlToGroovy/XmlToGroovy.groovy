package xmlToGroovy

import org.w3c.dom.Document
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class XmlToGroovy {

	static Map<String,Integer> map = [:];
	
	static void main(String[] str){

//		String xslName = "cs2xmlCT.xml"
//		String xslName = "blCS2xml.xml"
//		String xslName = "siCS2xml.xml"
//		String xslName = "ct.xml"
//		String xslName = "CS2_ICS_ENS.xml"
//		String xslName = "blXml.xml"
//		String xslName = "blInttra.xml"
//		String xslName = "inCar.xml"
	//	String xslName = "ackTemplate.xml"
		String xslName = "EXX.xml"

		File file = new File("""input/${xslName}""");
		String xsl = file.text

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

		InputStream inputStream = new ByteArrayInputStream(xsl.bytes)
		Document document    = builder.parse(inputStream)


		nodeTranserse(document.getDocumentElement(),0);
		
		
	}


	public static void nodeTranserse(org.w3c.dom.Node node,int level){
		String rNodeName=node.getNodeName();
		level = level + 1
		if(node.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
			printTag(level)
			System.out.print("'" + rNodeName + "'" + "{");
		}

		NodeList allNodes=node.getChildNodes();
		int size=allNodes.getLength();
		if(size>0){
			for(int j=0;j<size;j++){
				org.w3c.dom.Node childNode=allNodes.item(j);

				if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
					if(childNode.getChildNodes().length <= 1){
						printTag(level+1)
						print "'" + childNode.getNodeName() + "' ''" ;
					}else{
						nodeTranserse(childNode,level);
					}
				}
				
			}
			printTag(level)
			System.out.print("}");

		}
	}
	
	static void printTag(int level){
		for(i in 1..level){
			if( i == 1){
				println '\t'
			}else{
				print '\t'
			}
		}
	}
}


