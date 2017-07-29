package cs.b2b.core.mapping.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author RENGA
 *  20161223 - ready for CS2XML CT, SO
 *  20160105 - ready for edi and cs2xml
 *  20170206 - bug fix for un-formatting xml and no child element
 *  20170207 - performance fine tune
 *  20170424 - remove xml comment and bug fix for empty tag in MultiElement
 *  20170517 - translate &apos;
 *  20170605 - fix array "null" string issue
 *  20170710 - enable to output string with space only
 */

public class XmlBeanParser {

	private static final String NULL = "null";
	private static final String COMMA = ",";
	private static final String JSON_DELIMITER = ":";
	private static final String ATTR_FLAG = "attr_";
	private static final String OBJ_START = "{";
	private static final String OBJ_END = "}";
	private static final String ARRAY_START = "[";
	private static final String ARRAY_END = "]";
	private static final String LOOP_SUFFIX = "";
	private static final String LOOP_FLAG = "Loop";
	private static final String LOOP_FLAG2 = "Group";
	private static final String SINGLE_QUOTES = "'";
	
	private Set<String> MultiElementList = null; 
	private String MultiElementListSearchIndex = null;
	private int elementLevel = 0;
	
	Stack<String> stack = new Stack<String>();

	@SuppressWarnings("unchecked")
	public <T> T xmlParser(String xml, Class<T> t) throws Exception {

		// transfer \ and ' in xml data and remove xml comment
		xml = xml.replaceAll("(?s)<\\!\\-\\-.+?\\-\\->", "").replace("\\", "\\\\").replace(SINGLE_QUOTES, "\\'").replace("&apos;","\\'");

		StringBuffer sb = new StringBuffer();
		Gson gson = new GsonBuilder().create();
		
		// get multiple element list
		MultiElementList = (Set<String>) t.getDeclaredField("MultiElementList").get(null);

		Iterator<String> it = MultiElementList.iterator();
		int tmp = 0;
		while(it.hasNext()){
			tmp = StringUtils.countMatches(it.next(), ".");
			elementLevel = tmp > elementLevel ? tmp : elementLevel;
		}
		
		if(elementLevel >= 2 ){
			MultiElementListSearchIndex = StringUtils.join(MultiElementList, ",");
		}
		// parse xml to dom
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		Document document = builder.parse(inputStream);

		// convert xml to json
		xmlToJson(document.getDocumentElement(), sb, true);
		String xmlJson = sb.toString().replace(COMMA + OBJ_END, OBJ_END).replace(COMMA + ARRAY_END, ARRAY_END);

		// System.out.println(xmlJson);
		// convert json to obj
		T obj = (T) gson.fromJson(xmlJson, t);

		return obj;
	}

	
	public void xmlToJson(org.w3c.dom.Node node, StringBuffer sb, boolean firstCall) throws Exception {
//		Set<String> MultiElementList = (Set<String>) T.getDeclaredField("MultiElementList").get(null);
		String rNodeName = removeNamespacePrefix(node.getNodeName());
		stack.push(rNodeName);
		if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
			if(isMultiElement(node, rNodeName)){
				if (isLoopElementStart(node)) {
					if((rNodeName.startsWith(LOOP_FLAG) || rNodeName.startsWith(LOOP_FLAG2)) && rNodeName.indexOf("_") > 0){
						sb.append(SINGLE_QUOTES + rNodeName + SINGLE_QUOTES + JSON_DELIMITER + ARRAY_START + OBJ_START);
					}else{
						sb.append(SINGLE_QUOTES + rNodeName + LOOP_SUFFIX + SINGLE_QUOTES + JSON_DELIMITER + ARRAY_START + OBJ_START);
					}
				}else{
					sb.append(OBJ_START);
				}
			} else {
				if (firstCall) {
					sb.append(OBJ_START);
				} else {
					sb.append(SINGLE_QUOTES + rNodeName + SINGLE_QUOTES + JSON_DELIMITER + OBJ_START);
				}
			}
			attrHandler(node, sb);
		}

		NodeList allNodes = node.getChildNodes();
		int size = allNodes.getLength();
		if (size > 0) {
			for (int j = 0; j < size; j++) {
				org.w3c.dom.Node childNode = allNodes.item(j);
				String childNodeName = removeNamespacePrefix(childNode.getNodeName());
				
				if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					
					if (childNode.getChildNodes().getLength() == 1 && childNode.getFirstChild().getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
						stack.push(childNodeName);
						if (hasAttr(childNode)) {
							if(isMultiElement(childNode, childNodeName)){
								if(isLoopElementStart(childNode)){
									sb.append(SINGLE_QUOTES + childNodeName + SINGLE_QUOTES + JSON_DELIMITER + ARRAY_START + OBJ_START);
									attrHandler(childNode, sb);
									sb.append(SINGLE_QUOTES + childNodeName + SINGLE_QUOTES + JSON_DELIMITER + SINGLE_QUOTES + childNode.getTextContent() + SINGLE_QUOTES);
									sb.append(OBJ_END + COMMA);
								}else{
									sb.append(OBJ_START);
									attrHandler(childNode, sb);
									sb.append(SINGLE_QUOTES + childNodeName + SINGLE_QUOTES + JSON_DELIMITER + SINGLE_QUOTES + childNode.getTextContent() + SINGLE_QUOTES);
									sb.append(OBJ_END + COMMA);
								}
								
								if (isLoopElementEnd(childNode)) {
									sb.append(ARRAY_END + COMMA);
								}
							}else{
								sb.append(SINGLE_QUOTES + childNodeName + SINGLE_QUOTES + JSON_DELIMITER + OBJ_START);
	
								attrHandler(childNode, sb);
	
								sb.append(SINGLE_QUOTES + childNodeName + SINGLE_QUOTES + JSON_DELIMITER + SINGLE_QUOTES + childNode.getTextContent() + SINGLE_QUOTES);
								sb.append(OBJ_END + COMMA);
							}
						} else {
							String content = null;
							if (childNode.getTextContent()!=null && childNode.getTextContent().length()>0) {
								content =  SINGLE_QUOTES + childNode.getTextContent()  + SINGLE_QUOTES;
							}
							
							if(isMultiElement(childNode, childNodeName)){
								if(isLoopElementStart(childNode)){
									sb.append(SINGLE_QUOTES + childNodeName + SINGLE_QUOTES + JSON_DELIMITER + ARRAY_START + content  +  COMMA );
								}else{
									sb.append(content  +  COMMA );
								}

								if (isLoopElementEnd(childNode)) {
									sb.append(ARRAY_END + COMMA);
								}
							} else {
								sb.append(SINGLE_QUOTES + childNodeName + SINGLE_QUOTES + JSON_DELIMITER + content + COMMA);
							}

						}
						stack.pop();
					} else { 
						if(childNode.getChildNodes().getLength() > 0) {
							xmlToJson(childNode, sb, false);
						}else{
							if(isMultiElement(childNode, childNodeName)){
								if(isLoopElementStart(childNode)){
									sb.append(SINGLE_QUOTES + childNodeName + SINGLE_QUOTES + JSON_DELIMITER + ARRAY_START + NULL  +  COMMA );
								}else{
									sb.append( NULL  +  COMMA );
								}

								if (isLoopElementEnd(childNode)) {
									sb.append(ARRAY_END + COMMA);
								}
							}
						}
					}
				}
				
			}

			if(isMultiElement(node, rNodeName) && isLoopElementEnd(node)){
				sb.append(OBJ_END + ARRAY_END + COMMA);
			} else {
				if (firstCall) {
					sb.append(OBJ_END);
				} else {
					sb.append(OBJ_END + COMMA);
				}
			}
		}
		stack.pop();
	}

	private boolean isMultiElement(org.w3c.dom.Node node, String rNodeName) {
		
		boolean result = false;
		
		String fisrtParentNodeName = node.getParentNode().getNodeName();
		String currentNodeName = rNodeName;
				
		fisrtParentNodeName = removeNamespacePrefix(fisrtParentNodeName);
		currentNodeName = removeNamespacePrefix(currentNodeName);

		if(elementLevel < 2){
			//Multi Element level is shallow, check current Node Name and parent node name directly.
			result = MultiElementList.contains(currentNodeName) || MultiElementList.contains(fisrtParentNodeName + "." + currentNodeName);
		}else{
			//Multi Element level is deep
			result = MultiElementListSearchIndex.indexOf(currentNodeName + ",") > 0;
			
			if(result == true){
				for(int i = 0 ; i <= elementLevel; i++){
					if(i < stack.size()){
						result = MultiElementList.contains(StringUtils.join(stack.subList(stack.size() - 1 - i, stack.size()),"."));
						if(result == true){
							break;
						}
					}else{
						break;
					}
				}
			}
		}
			
		return result;
	}
	
	private String removeNamespacePrefix(String nodeName){
		return nodeName.replaceFirst(".*:", "");
	}
	

	private boolean isLoopElementEnd(org.w3c.dom.Node node) {

		boolean result = false;

		if (node.getNextSibling() != null) {
			if(node.getNextSibling().getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
				result = node.getNextSibling().getNodeName() != node.getNodeName();
			}else{
				if (node.getNextSibling().getNextSibling() != null) {
					result = node.getNextSibling().getNextSibling().getNodeName() != node.getNodeName();
				} else {
					result = true;
				}
			}
		}else{
			result = true;
		}
		return result;
	}

	private boolean isLoopElementStart(org.w3c.dom.Node node) {

		boolean result = false;

		if (node.getPreviousSibling() != null) {
			if(node.getPreviousSibling().getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
				result = node.getPreviousSibling().getNodeName() != node.getNodeName();
			}else{
				if (node.getPreviousSibling().getPreviousSibling() != null) {
					result = node.getPreviousSibling().getPreviousSibling().getNodeName() != node.getNodeName();
				} else {
					result = true;
				}
			}
		}else{
			result = true;
		}
		return result;

	}

	private void attrHandler(org.w3c.dom.Node node, StringBuffer sb) {
		if (node.hasAttributes()) {
			Node curNode = null;
			for (int i = 0; i < node.getAttributes().getLength(); i++) {
				curNode = node.getAttributes().item(i);
				String attrName = curNode.getNodeName();
				String attrValue = curNode.getTextContent();
				if (!(attrName.startsWith("xmlns") || attrName.startsWith("xsi"))) {
					sb.append(SINGLE_QUOTES + ATTR_FLAG + removeNamespacePrefix(attrName) + SINGLE_QUOTES + JSON_DELIMITER + SINGLE_QUOTES + attrValue + SINGLE_QUOTES + COMMA);
				}
			}
		}
	}

	private boolean hasAttr(org.w3c.dom.Node node) {

		boolean result = false;

		if (node.hasAttributes()) {
			Node curNode = null;
			for (int i = 0; i < node.getAttributes().getLength(); i++) {
				curNode = node.getAttributes().item(i);
				String attrName = curNode.getNodeName();
				if (!(attrName.startsWith("xmlns") || attrName.startsWith("xsi"))) {
					result = true;
					break;
				}
			}
		}

		return result;
	}

	public static void main(String[] args) throws Exception {

		// String json = LocalFileUtil.readBigFile(".\\input\\CT_json.js");
		//
		// Gson gson = new Gson();
		//
		//// Header header = gson.fromJson(json, Header.class);
		// ContainerMovement ct = gson.fromJson(json, ContainerMovement.class);
		//
		// String actualResult = gson.toJson(ct);
		//
		// System.out.println(actualResult);
		// System.out.println(json.equals(actualResult));

		// String xslName = "ct.xml"
		// String xslName = "DSGOODS_CS190_CS140_CS160.xml"
		//
		// File file = new File("""input/${xslName}""");
		// String xml = file.text

		// XmlBeanParser xmlBeanParser = new XmlBeanParser();
		// ContainerMovement ct = xmlBeanParser.xmlParser(xml,
		// ContainerMovement.class)
		//
		// Gson gson = new GsonBuilder().registerTypeAdapter(LocDT.class, new
		// LocDTAdapter()).create();
		// println gson.toJson(ct)
	}

}
