package xmlToGroovy

import org.w3c.dom.Document
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class XmlToBeanSplit2 {

	static Set<String> elementFlag = new HashSet<String>();
	static Set<String> elementAttrFlag = new HashSet<String>();
	static Set<String> multiElementList = [];

	static Map<String,Set<String>> AdapterFlag = new HashMap<String,Set<String>>()
	
	static void main(String[] str){

//		String xslName = "cs2xmlCT.xml"
//		String xslName = "blCS2xml.xml"
//		String xslName = "BR.xml"
//		String xslName = "so.xml"
//		String xslName = "ct.xml"
//		String xslName = "bl.xml"
//		String xslName = "bc.xml"
//		String xslName = "customizedVGM.xml"
//		String xslName = "OAxml"
//		String xslName = "SS_PT2PT.xsd"
//		String xslName = "siCS2xml.xml"
//		String xslName = "BR.xml"
//		String xslName = "ack_fa.xml"
//		String xslName = "in.xml"
//		String xslName = "ack.xml"
//		String xslName = "siCS2xml.xml"
		String xslName = "EXX.xml"


		File file = new File("""input/${xslName}""");
		String xsl = file.text

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

		InputStream inputStream = new ByteArrayInputStream(xsl.bytes)
		Document document = builder.parse(inputStream)

		StringBuffer pmtSb = new StringBuffer()
		StringBuffer commonSb = new StringBuffer()

		pmtSb.append("import java.io.Serializable;\n");
		pmtSb.append("import java.util.ArrayList;\n")
		pmtSb.append("import java.util.List;\n\n")
		//println pmtSb.toString()
		nodeTranserse(document.getDocumentElement(),pmtSb,commonSb);
		
		println pmtSb.toString()
	//	println commonSb.toString()

		println '之前'+multiElementList
		for(i in 0..<multiElementList.size()){
			String temp=multiElementList[0]
			multiElementList.remove(temp)
			multiElementList.add('"'+temp+'"')
		}

		println '之后'+multiElementList

		println AdapterFlag
	}


	public static void nodeTranserse(org.w3c.dom.Node node, StringBuffer pmtSb, StringBuffer commonSb){
		
		Map<String,Integer> map = new HashMap<String, Integer>();
		//childNode flag
		Set<Boolean> flag = new HashSet<Boolean>();

		//childNode attributes
		Set<String> attr_Set = new HashSet<String>();

		boolean attr_flag=false
		String rNodeName=node.getNodeName();
		
		NodeList allNodes=node.getChildNodes();
		int size=allNodes.getLength();

		if(!elementFlag.contains(rNodeName)){
			elementFlag.add(rNodeName)
			//println 'node.getNodeType() '+node.getNodeType()
			//add class Name and attrbuit
			if(node.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
				if(rNodeName.contains("cs:")){
					if(node?.attributes?.length>0){
						commonSb.append("@JsonAdapter("+rNodeName.replace("cs:","")+"Adapter.class)\n")
						attr_flag=true
					}
					commonSb.append("class " + rNodeName.replace("cs:","") + " implements Serializable {\n")
					
					if(node.hasAttributes()){
						node.getAttributes().each { curNode ->
							for(i in 0..<curNode.getLength()){
								String attrName =  curNode.item(i).getNodeName()
								if(!['xsi:schemaLocation','xmlns','xmlns:cs','xmlns:xsi'].contains(attrName)){
									commonSb.append("\tString attr_"+attrName+";\n ")
									attr_Set.add("attr_"+attrName)
								}
							}
						}
					}

				}
				else{
					if(node?.attributes?.length>0){
						pmtSb.append("@JsonAdapter("+rNodeName+"Adapter.class)\n")
						attr_flag=true
					}
					pmtSb.append("class " + rNodeName + " implements Serializable {\n")
					
					if(node.hasAttributes()){
						node.getAttributes().each { curNode ->
							for(i in 0..<curNode.getLength()){
								String attrName =  curNode.item(i).getNodeName()
								if(!['xsi:schemaLocation','xmlns','xmlns:cs','xmlns:xsi'].contains(attrName)){
									pmtSb.append("\tString attr_"+attrName+";\n ")
									attr_Set.add("attr_"+attrName)
								}
							}
						}

						if(attr_Set!=null){
							AdapterFlag.put(rNodeName.replace("cs:",""),attr_Set)
						}
					}
				}
			
			}
			//println 'pmtSb '+pmtSb
			//add childNode
			if(size>0){

				//add multiElementList
				for(int i=0;i<size;i++){
					org.w3c.dom.Node childNode=allNodes.item(i);
					if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
						String childNodeName = childNode.getNodeName()
						//if exist childNodeName,it say that is List
						if(map.containsKey(childNodeName)){
							map.put(childNodeName, map.get(childNodeName) + 1)
						}else{
							map.put(childNodeName, 0)
						}
					}
				}
				//println 'map '+map
				
				StringBuffer tmpSB = new StringBuffer();
				//add childNode
				for(int j=0;j<size;j++){
					org.w3c.dom.Node childNode=allNodes.item(j);
					
					if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
						String childNodeName = childNode.getNodeName()

						//println 'childNodeName '+childNodeName
						//if childNode have ChildNode and do not have attributes
						if(childNode.getChildNodes().getLength()   && !childNode.hasAttributes()){
							if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
								flag.add(childNodeName)
								if(childNode.parentNode.getNodeName().contains("cs:")){
									commonSb.append('\tList<String>'  + ' ' + childNodeName.replace("cs:","") + ' = new ArrayList<String>();\n')
								}else{
									pmtSb.append('\tList<String>'  + ' ' + childNodeName.replace("cs:","") + ' = new ArrayList<String>();\n')
								}
							}else if(!flag.contains(childNodeName)){
								if(childNode.parentNode.getNodeName().contains("cs:")){
									if(childNodeName.contains("cs:")){
										commonSb.append('\tString'  + ' ' + childNodeName.replace("cs:","") + ';\n')
									}else{
										commonSb.append('\tString'  + ' ' + childNodeName + ';\n')
									}
								}else{
									if(childNodeName.contains("cs:")){
										pmtSb.append('\tString'  + ' ' + childNodeName.replace("cs:","") + ';\n')
									}else{
										pmtSb.append('\tString'  + ' ' + childNodeName + ';\n')
									}
								}
							}
						}
						else{
							//add AttrName is ChildNodeName
							if(!elementAttrFlag.contains(childNodeName)){
								elementAttrFlag.add(childNodeName)
								if(childNode.getChildNodes().getLength() <=1 && childNode.hasAttributes()){
									//@JsonAdapter(TotalAmtInAdditionalPmtCurrencyAdapter.class)
									if(childNode.attributes.length>0){
										tmpSB.append("@JsonAdapter("+childNodeName+"Adapter.class)\n")
										attr_flag=true
									}
									tmpSB.append("class " + childNodeName.replace("cs:","") + " implements Serializable {\n")
									
									childNode.getAttributes().each { curNode ->
										for(i in 0..<curNode.getLength()){
											String attrName =  "attr_" + curNode.item(i).getNodeName()
											tmpSB.append("\tString "+attrName+";\n ")
										}
									}
									
//									if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
//										flag.add(childNodeName)
//										tmpSB.append('\tList<String>'  + ' ' + childNodeName.replace("cs:","") + ' = new ArrayList<String>();\n')
//									}else if(!flag.contains(childNodeName)){
										tmpSB.append('\tString'  + ' ' + childNodeName.replace("cs:","") + ';\n')
										tmpSB.append('\n')
										tmpSB.append('\t@Override\n')
										tmpSB.append('\tpublic String toString() {\n')
										tmpSB.append('\t\treturn this.' + childNodeName.replace("cs:","") + ';\n')
										tmpSB.append('\t}\n')
//									}
									tmpSB.append("}\n\n")
								}
							}
						
							if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
								flag.add(childNodeName)
								
								if(childNode.parentNode.getNodeName().contains("cs:")){
									if(childNodeName.contains("cs:")){
										commonSb.append('\tList<' + childNodeName.replace("cs:","")  + '> ' + childNodeName.replace("cs:","") + ' = new ArrayList<cs.b2b.core.mapping.bean.'+childNodeName.replace("cs:","")+'>();\n')
									}else{
										commonSb.append('\tList<' + childNodeName.replace("cs:","")  + '> ' + childNodeName.replace("cs:","") + ' = new ArrayList<'+childNodeName.replace("cs:","")+'>();\n')
									}
								}else{
									if(childNodeName.contains("cs:")){
										pmtSb.append('\tList<cs.b2b.core.mapping.bean.' + childNodeName.replace("cs:","")  + '> ' + childNodeName.replace("cs:","") + ' = new ArrayList<cs.b2b.core.mapping.bean.'+childNodeName.replace("cs:","")+'>();\n')
									}else{
										pmtSb.append('\tList<' + childNodeName.replace("cs:","")  + '> ' + childNodeName.replace("cs:","") + ' = new ArrayList<'+childNodeName.replace("cs:","")+'>();\n')
									}
								}
							}else if(!flag.contains(childNodeName)){
								if(childNode.parentNode.getNodeName().contains("cs:")){
									if(childNodeName.contains("cs:")){
										commonSb.append('\t' + childNodeName.replace("cs:","")  + ' ' + childNodeName.replace("cs:","") + ';\n')
									}else{
										commonSb.append('\t' + childNodeName  + ' ' + childNodeName + ';\n')
									}
								}else{
									if(childNodeName.contains("cs:")){
										pmtSb.append('\tcs.b2b.core.mapping.bean.' + childNodeName.replace("cs:","")  + ' ' + childNodeName.replace("cs:","") + ';\n')
									}else{
										pmtSb.append('\t' + childNodeName  + ' ' + childNodeName + ';\n')
									}
								}
							}
						}
					}
				}
//				System.out.println("}");
//				System.out.println("");
				if(rNodeName.contains("cs:")){
					commonSb.append("}\n\n")
					commonSb.append(tmpSB.toString())
				}else{
					pmtSb.append("}\n\n")
					pmtSb.append(tmpSB.toString())
				}
			}
			
			flag = new HashSet<Boolean>();
			
			for(int j=0;j<size;j++){
				org.w3c.dom.Node childNode=allNodes.item(j);
				String childNodeName = childNode.getNodeName()
				if(childNode.getChildNodes().getLength() > 1 && !flag.contains(childNodeName)){
					flag.add(childNodeName);
					nodeTranserse(childNode, pmtSb, commonSb);
				}

			}

		}
	//	println 'map2 '+map
		if(map.findAll{it.value > 0}){
			multiElementList.addAll(map.findAll{it.value > 0}.keySet())
		}


		if(attr_Set!=null){
			AdapterFlag.put(rNodeName.replace("cs:",""),attr_Set)
		}
	}
}
