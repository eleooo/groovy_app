package b2b;

import groovy.util.IndentPrinter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.groovy.syntax.Types;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
 
/**
 * @author RENGA
 *
 */
public class DomToGroovy {
 
    protected IndentPrinter out;
    protected boolean inMixed = false;
    protected String qt = "'";
    protected Collection<String> keywords = Types.getKeywords();
 
    private static String stylesheetAttr = "";
    
    private List<String> stack = new LinkedList<String>();
    
    public DomToGroovy(PrintWriter out) {
        this(new IndentPrinter(out));
    }
 
    // TODO allow string quoting delimiter to be specified, e.g. ' vs "
    public DomToGroovy(IndentPrinter out) {
        this.out = out;
    }
 
    public void print(Document document) {
    	stack.add("inXml.");
        printChildren(document, new HashMap());
    }
 
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: DomToGroovy infilename [outfilename]");
            System.exit(1);
        }
        Document document = null;
        try {
            document = parse(args[0]);
        } catch (Exception e) {
            System.out.println("Unable to parse input file '" + args[0] + "': " + e.getMessage());
            System.exit(1);
        }
        PrintWriter writer = null;
        if (args.length < 2) {
            writer = new PrintWriter(System.out);
        } else {
            try {
                writer = new PrintWriter(new FileWriter(new File(args[1])));
            } catch (IOException e) {
                System.out.println("Unable to create output file '" + args[1] + "': " + e.getMessage());
                System.exit(1);
            }
        }
        DomToGroovy converter = new DomToGroovy(writer);
        converter.out.incrementIndent();
        writer.println("#!/bin/groovy");
        writer.println();
        writer.println("// generated from " + args[0]);
        writer.println("System.out << new groovy.xml.StreamingMarkupBuilder().bind {");
        converter.print(document);
        writer.println("}");
        writer.close();
    }
 
    // Implementation methods
    //-------------------------------------------------------------------------
 
    protected static Document parse(final String fileName) throws Exception {
        return parse(new File(fileName));
    }
 
    public static Document parse(final File file) throws Exception {
        return parse(new BufferedReader(new FileReader(file)));
    }
 
    public static Document parse(final Reader input) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(input));
    }
 
    public static Document parse(final InputStream input) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(input));
    }
 
    protected void print(Node node, Map namespaces, boolean endWithComma) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE :
                printElement((Element) node, namespaces, endWithComma);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE :
                printPI((ProcessingInstruction) node, endWithComma);
                break;
            case Node.TEXT_NODE :
                printText((Text) node, endWithComma);
                break;
            case Node.COMMENT_NODE :
                printComment((Comment) node, endWithComma);
                break;
        }
    }
 
    protected void printElement(Element element, Map namespaces, boolean endWithComma) {
        namespaces = defineNamespaces(element, namespaces);
 
        element.normalize();
        printIndent();
 
        String prefix = element.getPrefix();
        boolean hasPrefix = prefix != null && prefix.length() > 0;
        String localName = getLocalName(element);
        boolean isKeyword = checkEscaping(localName);
      
        boolean hasAttributes = false;
        
        if(isElement(localName)){
        	print("'" + element.getAttribute("name") + "'");
        }else if(isVariable(localName)){
        	print("def " + element.getAttribute("name") + " = " +  printValueWithPrefix(element.getAttribute("select")));
        }else if(isForEachGroup(localName)){
        	String loopName = DomToGroovyHelper.replaceUselessChar(element.getAttribute("select"));
         	
         	String currentLoopName = "current_"+ loopName.replace(".", "_").replaceAll("\\[.*\\]", "");
         	
         	if(loopName.contains("'..'")){
         		currentLoopName = "current_"+ loopName.replace("'..'.", "").replace(".", "_").replaceAll("\\[.*\\]", "");
         	}
         	
         	printQuoted(localName);
         	printAttributes(element);
         	print(" { "+ currentLoopName +" ->");
         	
         	stack.add(currentLoopName + ".");
         	
        }else if(isForEach(localName)){
        	String loopName = DomToGroovyHelper.replaceUselessChar(element.getAttribute("select"));
        	
        	String currentLoopName = "current_"+ loopName.replace(".", "_").replaceAll("\\[.*\\]", "");
        	
        	if(loopName.contains("'..'")){
        		currentLoopName = "current_"+ loopName.replace("'..'.", "").replace(".", "_").replaceAll("\\[.*\\]", "");
        	}
        	
        	print(stack.get(stack.size()-1) + loopName + ".each { "+ currentLoopName +" ->");
        	stack.add(currentLoopName + ".");
        }else if(isIfOrWhen(localName)){
//        	String loopName = DomToGroovyHelper.replaceUselessChar(element.getAttribute("test"));
        	String loopName = printValueWithPrefix(element.getAttribute("test"));
            print("if(" + loopName + ")");
        }else if(isOtherwise(localName)){
            print("else");
        }else if(isChoose(localName)){
        	//skip
        }
        else if(isStylesheet(localName)){
        	stylesheetAttr = getStylesheetAttributes(element);
        }
        else{
//        	if (isKeyword || hasPrefix) {
        		print(qt);
//        	}
            if (hasPrefix) {
              print(prefix);
              print(".");
            }
            print(localName);
          
//            if (isKeyword || hasPrefix){ 
            	print(qt);
//            }
            if(!"".equals(stylesheetAttr)){
            	print(stylesheetAttr);
            	stylesheetAttr = "";
            }
   
          hasAttributes = printAttributes(element);
        }
        
 
        NodeList list = element.getChildNodes();
        int length = list.getLength();
        if (length == 0) {
        	printEnd("", endWithComma);
        } else {
            Node node = list.item(0);
            if (length == 1 && node instanceof Text) {
                Text textNode = (Text) node;
                String text = getTextNodeData(textNode);
//                if (hasAttributes) print(", ");
                print(" ");
                printQuoted(text);
                printEnd("", endWithComma);
            } else if (mixedContent(list)) {
                println("{");
                out.incrementIndent();
                boolean oldInMixed = inMixed;
                inMixed = true;
                for (node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                    print(node, namespaces, false);
                }
                inMixed = oldInMixed;
                out.decrementIndent();
                printIndent();
                printEnd("}", endWithComma);
            } else {
            	if(list != null && length > 1 && ("xsl:text".equals(list.item(1).getNodeName()) || "text".equals(list.item(1).getNodeName()))){
            		print(" '" + list.item(1).getTextContent() + "'");
            		printEnd("", endWithComma);
            	}else if(list != null && length > 1 && ("xsl:value-of".equals(list.item(1).getNodeName()) || "value-of".equals(list.item(1).getNodeName()))){
            		String value = ((Element)list.item(1)).getAttribute("select");
            		print(printValueWithPrefix(value));
            		printEnd("", endWithComma);
            	}else if(list != null && length > 1 && ("xsl:attribute".equals(list.item(1).getNodeName()) || "attribute".equals(list.item(1).getNodeName()))){
            		Element attr = null;
            		String attrName = "";
            		String attrValue = "";
            		String value = "";
            		print("(");
            		
            		for(int i = 1 ;i < list.getLength(); i++){
            			if("xsl:attribute".equals(list.item(i).getNodeName()) || "attribute".equals(list.item(i).getNodeName())){
            				if(i > 1){
            					print(",");
            				}
	            			attr = (Element)list.item(i);
	            			attrName = attr.getAttribute("name");
	            			if(attr.getChildNodes().item(1).getNodeName().contains("text")){
	            				attrValue = attr.getChildNodes().item(1).getTextContent();
	            			}else if (attr.getChildNodes().item(1).getNodeName().contains("value-of")){
	            				attrValue = ((Element)attr.getChildNodes().item(1)).getAttribute("select");
	            			}
	            			
	            			print(qt);
	                		print(attrName);
	                		print(qt);
	                		print(":");
	                		print(printValueWithPrefix(attrValue));
            			}else if("xsl:text".equals(list.item(i).getNodeName()) || "text".equals(list.item(i).getNodeName())){
            				value = " '" + list.item(i).getTextContent() + "'";
            			}else if("xsl:value-of".equals(list.item(i).getNodeName()) || "value-of".equals(list.item(i).getNodeName())){
            				value = printValueWithPrefix(((Element)list.item(i)).getAttribute("select"));
            			}
            		}
            		print(")");
            		print(value);
                	printEnd("", endWithComma);
                	
            	}else{
            		if(isForEach(localName) || isForEachGroup(localName)){
            			println("");
            		}else if(!isChoose(localName) && !isStylesheet(localName)){
            			println("{");
            		}
            		
	                out.incrementIndent();
	                printChildren(element, namespaces);
	                out.decrementIndent();
	                printIndent();
	                
	                if(isForEach(localName)|| isForEachGroup(localName)){
	                	stack.remove(stack.size()-1);
	                }
	                if(!isChoose(localName) && !isStylesheet(localName)){
	                	printEnd("}", endWithComma);
	                }
            	}
            }
        }
    }

	private String printValueWithPrefix(String value) {
		String result = "";
		if(value.contains("$") || (value.startsWith("'") && value.endsWith("'")) || value.contains("current-dateTime")){
			result = " " + DomToGroovyHelper.replaceUselessChar(value);
//		}else if(value.contains("))") || value.contains("..")){
		}else if(value.contains("))")){
			result = " " +  DomToGroovyHelper.replaceUselessChar(value);
			System.out.println("Not Handled Value:" + result);
		}else if(value.startsWith("..")){
//			result = " " + stack.get(stack.size()-2).replace(".", "") + DomToGroovyHelper.replaceUselessChar(value);
			result = " " + stack.get(stack.size()-1) + DomToGroovyHelper.replaceUselessChar(value);
		}else{
			result = " "+ stack.get(stack.size()-1)  + DomToGroovyHelper.replaceUselessChar(value);
		}
		return result;
	}

	private boolean isIfOrWhen(String localName) {
		return "xsl:if".equals(localName) || "xsl:when".equals(localName);
	}
	
	private boolean isChoose(String localName) {
		return "xsl:choose".equals(localName);
	}
	
	private boolean isOtherwise(String localName) {
		return "xsl:otherwise".equals(localName);
	}


	private boolean isVariable(String localName) {
		return "xsl:variable".equals(localName);
	}
	
	private boolean isElement(String localName) {
		return "xsl:element".equals(localName);
	}
	
	private boolean isStylesheet(String localName) {
		return "xsl:stylesheet".equals(localName);
	}

	private boolean isForEach(String localName) {
		return "xsl:for-each".equals(localName);
	}
	
	private boolean isForEachGroup(String localName) {
		return "xsl:for-each-group".equals(localName);
	}

    protected void printQuoted(String text) {
        if (text.indexOf("\n") != -1) {
            print("'''");
            print(text);
            print("'''");
        } else {
            print(qt);
            print(escapeQuote(text));
            print(qt);
        }
    }
 
    protected void printPI(ProcessingInstruction instruction, boolean endWithComma) {
        printIndent();
        print("mkp.pi(" + qt);
        print(instruction.getTarget());
        print(qt + ", " + qt);
        print(instruction.getData());
        printEnd(qt + ");", endWithComma);
    }
 
    protected void printComment(Comment comment, boolean endWithComma) {
        String text = comment.getData().trim();
        if (text.length() >0) {
            printIndent();
            print("/* ");
            print(text);
            printEnd(" */", endWithComma);
        }
    }
 
    protected void printText(Text node, boolean endWithComma) {
        String text = getTextNodeData(node);
        if (text.length() > 0) {
            printIndent();
            if (inMixed) print("mkp.yield ");
            printQuoted(text);
            printEnd("", endWithComma);
        }
    }
 
    protected String escapeQuote(String text) {
        return text.replaceAll("\\\\", "\\\\\\\\").replaceAll(qt, "\\\\" + qt);
    }
 
    protected Map defineNamespaces(Element element, Map namespaces) {
        Map answer = null;
        String prefix = element.getPrefix();
        if (prefix != null && prefix.length() > 0 && !namespaces.containsKey(prefix)) {
            answer = new HashMap(namespaces);
            defineNamespace(answer, prefix, element.getNamespaceURI());
        }
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            Attr attribute = (Attr) attributes.item(i);
            prefix = attribute.getPrefix();
            if (prefix != null && prefix.length() > 0 && !namespaces.containsKey(prefix)) {
                if (answer == null) {
                    answer = new HashMap(namespaces);
                }
                defineNamespace(answer, prefix, attribute.getNamespaceURI());
            }
        }
        return (answer != null) ? answer : namespaces;
    }
 
    protected void defineNamespace(Map namespaces, String prefix, String uri) {
        namespaces.put(prefix, uri);
        if (!prefix.equals("xmlns") && !prefix.equals("xml")) {
            printIndent();
            print("mkp.declareNamespace(");
            print(prefix);
            print(":" + qt);
            print(uri);
            println(qt + ")");
        }
    }
 
    protected boolean printAttributes(Element element) {
        boolean hasAttribute = false;
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        if (length > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < length; i++) {
                printAttributeWithPrefix((Attr) attributes.item(i), buffer);
            }
            for (int i = 0; i < length; i++) {
                hasAttribute = printAttributeWithoutPrefix((Attr) attributes.item(i), hasAttribute);
            }
            if (buffer.length() > 0) {
                if (hasAttribute) {
                    print(", ");
                }
                print(buffer.toString());
                hasAttribute = true;
            }
        }
        return hasAttribute;
    }
    
    protected String getStylesheetAttributes(Element element) {
        boolean hasAttribute = false;
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        StringBuffer stringBuffer = new StringBuffer();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
            	Attr attribute = (Attr) attributes.item(i);
            	String localName = getLocalName(attribute);
            	if(!("exclude-result-prefixes".equals(localName) || "version".equals(localName) || "xmlns:OLLMappingLib".equals(localName) || "xmlns:tib".equals(localName))){
	                if (!hasAttribute) {
	                    hasAttribute = true;
	                    stringBuffer.append("(");
	                } else {
	                    stringBuffer.append(", ");
	                }
                    stringBuffer.append(qt);
                    stringBuffer.append(localName);
                    stringBuffer.append(qt);
                    stringBuffer.append(":");
                    stringBuffer.append(qt);
                    stringBuffer.append(getAttributeValue(attribute));
                    stringBuffer.append(qt);
                }
            }
            stringBuffer.append(")");
        }
        
        return stringBuffer.toString();
    }
 
    protected void printAttributeWithPrefix(Attr attribute, StringBuffer buffer) {
        String prefix = attribute.getPrefix();
        if (prefix != null && prefix.length() > 0 && !prefix.equals("xmlns")) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(qt);
            buffer.append(prefix);
            buffer.append(":");
            buffer.append(getLocalName(attribute));
            buffer.append(qt + ":" + qt);
            buffer.append(escapeQuote(getAttributeValue(attribute)));
            buffer.append(qt);
        }
    }
 
    protected String getAttributeValue(Attr attribute) {
        return attribute.getValue();
    }
 
    protected boolean printAttributeWithoutPrefix(Attr attribute, boolean hasAttribute) {
        String prefix = attribute.getPrefix();
        if (prefix == null || prefix.length() == 0) {
            if (!hasAttribute) {
                hasAttribute = true;
            } else {
                print(", ");
            }
            String localName = getLocalName(attribute);
            boolean needsEscaping = checkEscaping(localName);
            if (needsEscaping) print(qt);
            print(localName);
            if (needsEscaping) print(qt);
            print(":");
            printQuoted(getAttributeValue(attribute));
        }
        return hasAttribute;
    }
 
    protected boolean checkEscaping(String localName) {
        return keywords.contains(localName) || localName.contains("-") || localName.contains(":") || localName.contains(".");
    }
 
    protected String getTextNodeData(Text node) {
        return node.getData().trim();
    }
 
    protected boolean mixedContent(NodeList list) {
        boolean hasText = false;
        boolean hasElement = false;
        for (int i = 0, size = list.getLength(); i < size; i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                hasElement = true;
            } else if (node instanceof Text) {
                String text = getTextNodeData((Text) node);
                if (text.length() > 0) {
                    hasText = true;
                }
            }
            if (hasText && hasElement) break;
        }
        return hasText && hasElement;
    }
 
    protected void printChildren(Node parent, Map namespaces) {
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            print(node, namespaces, false);
        }
    }
 
    protected String getLocalName(Node node) {
        String answer = node.getLocalName();
        if (answer == null) {
            answer = node.getNodeName();
        }
        return answer.trim();
    }
 
    protected void printEnd(String text, boolean endWithComma) {
        if (endWithComma) {
            print(text);
            println(",");
        } else {
            println(text);
        }
    }
 
    protected void println(String text) {
        out.println(text);
    }
 
    protected void print(String text) {
        out.print(text);
    }
 
    protected void printIndent() {
        out.printIndent();
    }
    
}