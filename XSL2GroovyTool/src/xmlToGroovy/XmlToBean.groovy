package xmlToGroovy

import java.awt.Container
import java.io.Serializable;
import java.util.HashMap.EntrySet;

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.codehaus.groovy.tools.xml.DomToGroovy;
import org.w3c.dom.Document
import org.w3c.dom.NodeList;
import org.w3c.dom.Text

class XmlToBean {

	static Set<String> elementFlag = new HashSet<String>();
	static Set<String> elementAttrFlag = new HashSet<String>();
	static Set<String> multiElementList = [];
	
	static void main(String[] str){

//		String xslName = "cs2xmlCT.xml"
//		String xslName = "blCS2xml.xml"
//		String xslName = "BR.xml"
//		String xslName = "so.xml"
		String xslName = "ct.xml"
//		String xslName = "bl.xml"
//		String xslName = "bc.xml"
//		String xslName = "customizedVGM.xml"

		File file = new File("""input/${xslName}""");
		String xsl = file.text

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

		InputStream inputStream = new ByteArrayInputStream(xsl.bytes)
		Document document = builder.parse(inputStream)

		StringBuffer sb = new StringBuffer()
		
		sb.append("import java.io.Serializable;\n");
		sb.append("import java.util.ArrayList;\n")
		sb.append("import java.util.List;\n\n")
		
		nodeTranserse(document.getDocumentElement(),sb);
		
		println sb.toString()
		
		println multiElementList
	}


	public static void nodeTranserse(org.w3c.dom.Node node, StringBuffer sb){
		
		Map<String,Integer> map = new HashMap<String, Integer>(); 
		Set<Boolean> flag = new HashSet<Boolean>();
		
		
//		String rNodeName=node.getNodeName().replace("cs:","");
		String rNodeName=node.getNodeName();
		
		NodeList allNodes=node.getChildNodes();
		int size=allNodes.getLength();
		
		if(!elementFlag.contains(rNodeName)){
			elementFlag.add(rNodeName)
			if(node.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
//				System.out.println("class " + rNodeName + "{");
				sb.append("class " + rNodeName + " implements Serializable {\n")
				if(node.hasAttributes()){
					node.getAttributes().each { curNode ->
						for(i in 0..<curNode.getLength()){
//							String attrName =  curNode.item(i).getNodeName().replace("cs:","")
							String attrName =  curNode.item(i).getNodeName()
//							println curNode.item(i).getTextContent()
							if(!['xsi:schemaLocation','xmlns','xmlns:cs','xmlns:xsi'].contains(attrName)){
								sb.append("\tString attr_"+attrName+";\n ")
							}
						}
					}
				}
			}
			
			if(size>0){
				
				for(int i=0;i<size;i++){
					org.w3c.dom.Node childNode=allNodes.item(i);
	
					if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
//						String childNodeName = childNode.getNodeName().replace("cs:","")
						String childNodeName = childNode.getNodeName()
						if(map.containsKey(childNodeName)){
							map.put(childNodeName, map.get(childNodeName) + 1)
						}else{
							map.put(childNodeName, 0)
						}
					}
				}
				
				StringBuffer tmpSB = new StringBuffer();
				for(int j=0;j<size;j++){
					org.w3c.dom.Node childNode=allNodes.item(j);
					
					if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
//						String childNodeName = childNode.getNodeName().replace("cs:","")
						String childNodeName = childNode.getNodeName()
						if(childNode.getChildNodes().getLength() <=1 && !childNode.hasAttributes()){
							if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
								flag.add(childNodeName)
//								println '\t public List<String>'  + ' ' + childNodeName + ' = new ArrayList<String>();';
								sb.append('\tList<String>'  + ' ' + childNodeName + ' = new ArrayList<String>();\n')
							}else if(!flag.contains(childNodeName)){
//								println '\t public String'  + ' ' + childNodeName + ';';
								sb.append('\tString'  + ' ' + childNodeName + ';\n')
							}
						} else{
							
							if(!elementAttrFlag.contains(childNodeName)){
								elementAttrFlag.add(childNodeName)
								if(childNode.getChildNodes().getLength() <=1 && childNode.hasAttributes()){
									tmpSB.append("class " + childNodeName + " implements Serializable {\n")
									
									childNode.getAttributes().each { curNode ->
										for(i in 0..<curNode.getLength()){
//											String attrName =  "attr_" + curNode.item(i).getNodeName().replace("cs:","")
											String attrName =  "attr_" + curNode.item(i).getNodeName()
				//							println curNode.item(i).getTextContent()
											tmpSB.append("\tString "+attrName+";\n ")
										}
									}
									
									if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
										flag.add(childNodeName)
										tmpSB.append('\tList<String>'  + ' ' + childNodeName + ' = new ArrayList<String>();\n')
									}else if(!flag.contains(childNodeName)){
										tmpSB.append('\tString'  + ' ' + childNodeName + ';\n')
										tmpSB.append('\n')
										tmpSB.append('\t@Override\n')
										tmpSB.append('\tpublic String toString() {\n')
										tmpSB.append('\t\treturn this.' + childNodeName + ';\n')
										tmpSB.append('\t}\n')
									}
									tmpSB.append("}\n\n")
								}
							}
						
							if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
								flag.add(childNodeName)
								
//								println '\t public List<' + childNodeName  + '> ' + childNodeName + ' = new ArrayList<'+childNodeName+'>();';
								sb.append('\tList<' + childNodeName  + '> ' + childNodeName + 'Loop = new ArrayList<'+childNodeName+'>();\n')
							}else if(!flag.contains(childNodeName)){
//								println '\t public ' + childNodeName  + ' ' + childNodeName + ' = new '+ childNodeName +'();';
								sb.append('\t' + childNodeName  + ' ' + childNodeName + ' = new '+ childNodeName +'();\n')
							}
						}
					}
				}
//				System.out.println("}");
//				System.out.println("");
				sb.append("}\n\n")
				sb.append(tmpSB.toString())
			}
			
			flag = new HashSet<Boolean>();
			
			for(int j=0;j<size;j++){
				org.w3c.dom.Node childNode=allNodes.item(j);
//				String childNodeName = childNode.getNodeName().replace("cs:","")
				String childNodeName = childNode.getNodeName()
				if(childNode.getChildNodes().getLength() > 1 && !flag.contains(childNodeName)){
					flag.add(childNodeName);
					nodeTranserse(childNode, sb);
				}

			}

		}
		
		if(map.findAll{it.value > 0}){
			multiElementList.addAll(map.findAll{it.value > 0}.keySet())
		}
		
	}
}
