package xmlToGroovy;

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Document
import org.w3c.dom.NodeList;

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapterFactory;

public class XmlBeanParserGroovy {
	
	private static final String COMMA = ",";
	private static final String JSON_DELIMITER = ":";
	private static final String ATTR_FLAG = "attr_";
	private static final String OBJ_START = "{";
	private static final String OBJ_END = "}";
	private static final String ARRAY_START = "[";
	private static final String ARRAY_END = "]";
	private static final String LOOP_FLAG = "Loop";
	private static final String SINGLE_QUOTES = "'";
	
	public static void main(String[] args) throws Exception {
		
//		String json = LocalFileUtil.readBigFile(".\\input\\CT_json.js");
//		
//		Gson gson = new Gson();
//		
////		Header header = gson.fromJson(json, Header.class);
//		ContainerMovement ct = gson.fromJson(json, ContainerMovement.class);
//		
//		String actualResult = gson.toJson(ct);
//		
//		System.out.println(actualResult);
//		System.out.println(json.equals(actualResult)); 
		
//		String xslName = "ct.xml"
//		String xslName = "DSGOODS_CS190_CS140_CS160.xml"
//		
//		File file = new File("""input/${xslName}""");
//		String xml = file.text
//		
//		XmlBeanParserG xmlBeanParser = new XmlBeanParserG();
//		ContainerMovement ct = xmlBeanParser.xmlParser(xml, ContainerMovement.class)
//
//		Gson gson = new GsonBuilder().create();  
//		println gson.toJson(ct)
	}
	
	public <T> T xmlParser(String xml, Class<T> t){
		
		xml = xml.replace(SINGLE_QUOTES,"\\'")
		
		StringBuffer sb = new StringBuffer();
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create()
		
		//parse xml to dom
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		InputStream inputStream = new ByteArrayInputStream(xml.bytes)
		Document document = builder.parse(inputStream)

		//convert xml to json
		xmlToJson(document.getDocumentElement(), sb, true, t);
		String xmlJson = sb.toString().replace(",}","}").replace(",]","]")

		println xmlJson
				
		//convert json to obj
		T obj = gson.fromJson(xmlJson, t);
		
		return obj;
	}
	
	public static void xmlToJson(org.w3c.dom.Node node, StringBuffer sb, boolean firstCall, Class T){
		Set<String> MultiElementList = T.getDeclaredField("MultiElementList").get(null)
		String rNodeName=node.getNodeName().replaceFirst(".*:","")
		if(node.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
			if(MultiElementList.contains(rNodeName)){
				if(node?.previousSibling?.previousSibling?.getNodeName() != node.getNodeName()){
					sb.append(SINGLE_QUOTES + rNodeName + "Loop'" + ":[{")
				}else{
					sb.append(OBJ_START)
				}
			}else{
				if(firstCall){
					sb.append(OBJ_START)
				}else{
					sb.append(SINGLE_QUOTES + rNodeName + SINGLE_QUOTES + ":{")
				}
			}
			if(node.hasAttributes()){
				node.getAttributes().each { curNode ->
					for(i in 0..<curNode.getLength()){
						String attrName =  curNode.item(i).getNodeName()
						String attrValue =  curNode.item(i).getTextContent()
						if(!(attrName.startsWith('xmlns') || attrName.startsWith('xsi'))){
							sb.append("'attr_" + attrName.replaceFirst(".*:","") + "':'" + attrValue + "',")
						}
					}
				}
			}
		}

		NodeList allNodes=node.getChildNodes();
		int size=allNodes.getLength();
		if(size>0){
			for(int j=0;j<size;j++){
				org.w3c.dom.Node childNode=allNodes.item(j);
				String childNodeName = childNode.getNodeName().replaceFirst(".*:","");

				if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
					if(childNode.getChildNodes().length <= 1){
						
						Map<String,String> attrKeyValue = new HashMap<String, String>()
						
						childNode.getAttributes().each { curNode ->
							for(i in 0..<curNode.getLength()){
								String attrName =  curNode.item(i).getNodeName()
								String attrValue =  curNode.item(i).getTextContent()
								if(!(attrName.startsWith('xmlns') || attrName.startsWith('xsi'))){
									attrKeyValue.put(attrName.replaceFirst(".*:",""), attrValue)
								}
							}
						}
						
						if(attrKeyValue.size() > 0){
							sb.append(SINGLE_QUOTES + childNodeName + SINGLE_QUOTES + ":{");
							attrKeyValue.each { key, value ->
								sb.append("'attr_" + key + "':'" + value + "',")
							}
							sb.append(SINGLE_QUOTES + childNodeName + "':'"+ childNode.getTextContent()  +SINGLE_QUOTES )
							sb.append("},")
						} else {
						
							if(MultiElementList.contains(childNodeName)){
								if(childNode?.previousSibling?.previousSibling?.getNodeName() != childNode.getNodeName()){
									sb.append(SINGLE_QUOTES + childNodeName + "':['"+ childNode.getTextContent()  +"'," )
								}else{
									sb.append(SINGLE_QUOTES + childNode.getTextContent()  + "'," )
								}
								
								if(childNode?.nextSibling?.nextSibling?.getNodeName() != childNode.getNodeName()){
									sb.append("],")
								}
							}else{
								sb.append(SINGLE_QUOTES + childNodeName + "':'"+ childNode.getTextContent()  +"'," )
							}
							
						}
					}else{
						xmlToJson(childNode,sb,false,T);
					}
				}
				
			}
			
			if(MultiElementList.contains(rNodeName) && node?.nextSibling?.nextSibling?.getNodeName() != node.getNodeName()){
				sb.append(OBJ_END + ARRAY_END + COMMA)
			}else{
				if(firstCall){
					sb.append(OBJ_END)
				}else{
					sb.append(OBJ_END + COMMA)
				}
			}
			
		}
	}

}
