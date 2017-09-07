package xmlToGroovy

import java.util.List
import java.util.Set

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Document
import org.w3c.dom.NodeList;
	

class XmlToBeanSplit {

	static Set<String> elementFlag = new HashSet<String>();
	static Set<String> elementAttrFlag = new HashSet<String>();
	static Set<String> multiElementList = [];
	static Set<String> commonElementList = [];				
	static Set<String> commonClassList = []					// 存放 common.groovy 下面存在的class set
	// messageSet 拿到的是基本类型的class名，但是特别的地方是如果class名与common下面的一样，但是数据结构不一样的话，会包含在这个set里面
	static Set<String> messageSet = []
	static Set<String> csSet = []
	static final def commpack = "cs.b2b.core.mapping.bean2"
	static final def commClass = "MessageBean_Common.groovy"
	static final String replacemultiElementList = "kieren"  // 替换掉
	static void main(String[] str){

//		String xslName = "cs2xmlCT.xml"
//		String xslName = "blCS2xml.xml"
//		String xslName = "BR.xml"
//		String xslName = "so.xml"
		String xslName = "ct.xml"
//		String xslName = "bl.xml"
//		String xslName = "bc.xml"

		File file = new File("""input/${xslName}""");
		String xsl = file.text

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

		InputStream inputStream = new ByteArrayInputStream(xsl.bytes)
		Document document = builder.parse(inputStream)
		
		findCommon(document,{
			set1,set2->
			messageSet = set1
			csSet = set2
		})

		
		StringBuffer sb = new StringBuffer()
		StringBuffer commonSb = new StringBuffer()
	
		sb.append("import java.io.Serializable;\n");
		sb.append("import java.util.ArrayList;\n")
		sb.append("import java.util.List;\n\n")
		
		commonSb.append("import java.io.Serializable;\n");
		commonSb.append("import java.util.ArrayList;\n")
		commonSb.append("import java.util.List;\n")
		
		commonSb.append("import java.io.IOException\n")
		commonSb.append("import com.google.gson.TypeAdapter\n")
		commonSb.append("import com.google.gson.annotations.JsonAdapter\n")
		commonSb.append("import com.google.gson.stream.JsonWriter;\n\n")
		commonSb.append("import com.google.gson.stream.JsonReader\n\n")
		commonSb.append(
"""/**
* @author RENGA
* @cs2xmlVersion 67.0
*/\n\n""")
		
//		commonSb.append("""
//	public class LocDTAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.LocDT> {
//	
//	@Override
//	public LocDT read(JsonReader jsonReader) throws IOException {
//		
//		LocDT locDT = new LocDT();
//
//		try{
//			jsonReader.beginObject();
//			jsonReader.setLenient(true)
//			while (jsonReader.hasNext()) {
//				switch (jsonReader.nextName()) {
//					case "attr_CSTimeZone":
//						locDT.attr_CSTimeZone = jsonReader.nextString();
//						break;
//					case "attr_TimeZone":
//						locDT.attr_TimeZone = jsonReader.nextString();
//						break;
//					case "LocDT":
//						locDT.LocDT = jsonReader.nextString();
//						break;
//				}
//			}
//			jsonReader.endObject()
//		}catch(IllegalStateException e){
//			locDT = new LocDT();
//			locDT.setLocDT(jsonReader.nextString());
//		}
//		
//		return locDT;
//	}
//	
//	@Override
//	public void write(JsonWriter jsonWriter, LocDT locDT) throws IOException {
//		// TODO Auto-generated method stub
//		println locDT
//	}
//}
//						""")
		
		commonClassList = CodeToClassUtil.readFileConfig(commpack,commClass)
		csSet.addAll(commonClassList)
		nodeTranserse(document.getDocumentElement(),sb,commonSb,true);
		commonClassList.addAll(csSet)
		CodeToClassUtil.writeClass(commonSb.toString(),commonClassList,commpack,commClass)
		CodeToClassUtil.writeClass(sb.toString(),"cs.b2b.core.mapping.bean.ct2","MessageBean_CT_O.groovy")		
		
		println multiElementList
	}
	
	private static Set<String> findDuplicate(def document){
		Set<String> set = []
		nodeRecursive(document.getDocumentElement(),set)
		Set<String> duplicate = []
		set.each{
			def str = ""
			if(!it.contains("cs:")){
				str+="cs:"+it
				if(set.contains(str)){
					duplicate.add(str)
				}
			}
			
		}
		return duplicate
	} 
	
	
	
	public static void findCommon(def document,def closure){
		def duplicateSet = findDuplicate(document)
		Set<String> set = []
		nodeRecursive(document.getDocumentElement(),set)
		Set<String> cs_set = []   //包含cs: 的所有node name set1
		Set<String> message_set = [] // 
		set.each{
			if(it.contains("cs:")) cs_set.add(it)  //set1
			else 	message_set.add(it)
		}
		
		duplicateSet.each{
			def s = it.replace("cs:","")
			message_set.remove(s)
		}
		Map<String,List<String>> sameClassMap = [:]
		
		findSameClassBetweenMessageAndCommon(document.getDocumentElement(),duplicateSet,new HashSet<String>(),sameClassMap)
		
		sameClassMap.each{
			k,v->
			if(!k.contains("cs:")){
				def cs_k="cs:"+k
				if(!sameClassMap.get(k).equals(sameClassMap.get(cs_k))){
					message_set.add(k)
				}
			}
		}

		closure(message_set,cs_set)
	}
	//duplicateSet like :[cs:CityDetails, cs:LocationCode, cs:CSStandardCity, cs:Packaging]
	private static void findSameClassBetweenMessageAndCommon(org.w3c.dom.Node node,Set<String> duplicateSet,Set<String> multiElementSet,Map<String,Set<String>> sameClassMap){
		if (multiElementSet.contains(node.getNodeName())) return
		else multiElementSet.add(node.getNodeName())
		if(node.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
			def nodeName = node.getNodeName()
			if(!nodeName.contains("cs:")){
				nodeName = "cs:"+nodeName
			}
		
			if(duplicateSet.contains(nodeName)){
				Set<String> list = sameClassMap.get(node.getNodeName())
				if(list==null){
					list = []
					sameClassMap.put(node.getNodeName(),list)
				}
				int size = node.getChildNodes().length
				for(int i=0;i<size;i++){
					org.w3c.dom.Node childNode= node.item(i)
					if(childNode.getNodeType()==1)
						sameClassMap.get(node.getNodeName()).add(childNode.getNodeName().replace("cs:",""))
				}
			}
			int size=node.getChildNodes().length
			for(int i=0;i<size;i++){
				org.w3c.dom.Node childNode= node.item(i)
				findSameClassBetweenMessageAndCommon(childNode,duplicateSet,multiElementSet,sameClassMap)
			}
			
		}
	}
	
	
	
	private static void nodeRecursive(org.w3c.dom.Node node,Set<String> set){
		if(node.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
			set.add(node.getNodeName())
			int size=node.getChildNodes().length;
			for(int i=0;i<size;i++){
				org.w3c.dom.Node childNode=node.item(i);
				if(childNode.getChildNodes().length>1){
					nodeRecursive(childNode,set)
				}else if(childNode.hasAttributes()){
					set.add(childNode.getNodeName())
				}
			}
		}
	}
	

	public static void nodeTranserse(org.w3c.dom.Node node, StringBuffer sb,StringBuffer commonSb,boolean isRoot){
		
		def tempStr;
		
		Map<String,Integer> map = new HashMap<String, Integer>(); 
		Set<Boolean> flag = new HashSet<Boolean>();
		
		if(commonClassList.contains(node.getNodeName())) return
				
		if(node.getNodeName().contains("cs:")&&!commonClassList.contains(node.getNodeName())){
			commonClassList.add(node.getNodeName())
		}
		
		if(csSet.contains(node.getNodeName())) {
			tempStr = commonSb
		}else if(messageSet.contains(node.getNodeName())){
			tempStr = sb
		}else  return
			
		String rNodeName=node.getNodeName().replace("cs:","");
		
		NodeList allNodes=node.getChildNodes();
		
		int size=allNodes.getLength();
		// elementFlag keep the node name if it's an element
		if(!elementFlag.contains(node.getNodeName())){
			elementFlag.add(node.getNodeName())
			if(node.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){ // if it is an element, then class.
				
				tempStr.append("class " + rNodeName + " implements Serializable {\n")
				if(isRoot){
					tempStr.append(replacemultiElementList)
				}
				
				if(node.hasAttributes()){							// if the element have the attribute like LOC_DT TIME_ZONE....
					node.getAttributes().each { curNode ->
						for(i in 0..<curNode.getLength()){
							String attrName =  curNode.item(i).getNodeName().replace("cs:","")
//							println curNode.item(i).getTextContent()
							if(!['xsi:schemaLocation','xmlns','xmlns:cs','xmlns:xsi'].contains(attrName)){
								tempStr.append("\tString attr_"+attrName+";\n ")
							}
						}
					}
				}
			}
			
			if(size>0){
				//  
				/*
				 * map add all the child nodes name.
				 * ex <Header><ControlNumber></ControlNumber><NVOProperty></NVOProperty><NVOProperty></NVOProperty></Header>
				 * then map will have {"ControlNumber":0,"NVOProperty":1}
				 * */
				for(int i=0;i<size;i++){
					org.w3c.dom.Node childNode=allNodes.item(i);
					if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
						String childNodeName = childNode.getNodeName().replace("cs:","")
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
						String childNodeName = childNode.getNodeName().replace("cs:","")
//						if(childNode.getNodeName().contains("cs:")) tempStr = commonSb
//						else tempStr = sb
						
						if(childNode.getChildNodes().getLength() <=1 && !childNode.hasAttributes()){
							if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
								flag.add(childNodeName)
//								
								tempStr.append('\tList<String>'  + ' ' + childNodeName + ' = new ArrayList<String>();\n')
							}else if(!flag.contains(childNodeName)){
								
								tempStr.append('\tString'  + ' ' + childNodeName + ';\n')
							}
						} else{
							
							if(!elementAttrFlag.contains(childNodeName)){
								elementAttrFlag.add(childNodeName)
								if(childNode.getChildNodes().getLength() <=1 && childNode.hasAttributes()){
									
//											if(childNode.getNodeName().contains("cs:")){
												if(!commonClassList.contains(childNode.getNodeName()&&childNode.getNodeName().contains('cs:'))){
													commonClassList.add(childNode.getNodeName())
													if(childNode.getNodeName()=="cs:LocDT")
														tmpSB.append("@JsonAdapter(LocDTAdapter.class)\n")
													tmpSB.append("class " + childNodeName + " implements Serializable {\n")
													
														childNode.getAttributes().each { curNode ->
															for(i in 0..<curNode.getLength()){
																String attrName =  "attr_" + curNode.item(i).getNodeName().replace("cs:","")
									//							println curNode.item(i).getTextContent()
																tmpSB.append("\tString "+attrName+";\n ")
												
															}
														}
														
														if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
															flag.add(childNodeName)
//															if(csSet.contains(childNode.getNodeName()))
//																childNodeName = commpack+childNodeName
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
//											}
//											
										
								}
							}
							def className = childNodeName
							if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
								flag.add(childNodeName)

									if((csSet.contains("cs:"+childNode.getNodeName())||csSet.contains(childNode.getNodeName()))&&tempStr!=commonSb){
										className = commpack+"."+childNodeName
									}
									
//								println '\t public List<' + childNodeName  + '> ' + childNodeName + ' = new ArrayList<'+childNodeName+'>();';
								tempStr.append('\tList<' + className  + '> ' + childNodeName + 'Loop = new ArrayList<'+className+'>();\n')
							}else if(!flag.contains(childNodeName)){
							
//								println '\t public ' + childNodeName  + ' ' + childNodeName + ' = new '+ childNodeName +'();';
								if((csSet.contains("cs:"+childNode.getNodeName())||csSet.contains(childNode.getNodeName()))&&tempStr!=commonSb){
										className = commpack+"."+childNodeName
								}
								
								tempStr.append('\t' + className  + ' ' + childNodeName + ' = new '+ className +'();\n')
							}
						}
					}
				}
//				System.out.println("}");
//				System.out.println("");
				tempStr.append("}\n\n")
				tempStr.append(tmpSB.toString())
			}
			
			flag = new HashSet<Boolean>();
			
			for(int j=0;j<size;j++){
				org.w3c.dom.Node childNode=allNodes.item(j);
				String childNodeName = childNode.getNodeName().replace("cs:","")
				if(childNode.getChildNodes().getLength() > 1 && !flag.contains(childNodeName)){
					flag.add(childNodeName);
					nodeTranserse(childNode, sb,commonSb,false);
				}

			}

		}
		
		if(map.findAll{it.value > 0}){
			multiElementList.addAll(map.findAll{it.value > 0}.keySet())
		}
		
		if(isRoot){
			StringBuffer str = new StringBuffer()
			str.append("\n")
			str.append("    public static final Set<String> MultiElementList=")
			str.append("[")
			multiElementList.each{
				
				str.append("\"").append(it).append("\"").append(",")
			}
			str.deleteCharAt(str.length()-1)
			str.append(']')
			str.append("\n\n")
			
			String s = tempStr.replaceAll(replacemultiElementList,str)
			sb.delete(0,sb.length())
			sb.append(s)
			
		}
	}
}

class CodeToClassUtil {
	
	private static def fileName;
	
	private static def parsePackageName(def packageName){	
	
//		if(!packageName.matches("[A-Za-z]+[.\\w+.]*[A-Za-z]+")){
//			 throw new RuntimeException("packageName invalid")
//		}
		
		def folders = packageName.split("\\.")
		def binPath = CodeToClassUtil.class.getClassLoader().getResource("").getPath()
		def srcPath = binPath.replace("bin","src").substring(0,binPath.length()-1)
		
		folders.each{
			srcPath+=("/"+it)
			mkdir(srcPath)
		}
		
		return (srcPath+"/")
	}
	
	
	public static void writeClass(def code,def packageName,def fileName){
		validateParam(code)
		validateParam(packageName)
		validateParam(fileName)
		validateExtension(fileName,["java","groovy"])
		def path = parsePackageName(packageName)+fileName
		def file = new File(path)
		def pack = "package "+packageName+";"+"\r\n"
		if(file.exists()){
			def old_code = file.getText()
			if(old_code.contains(code)){
				old_code = old_code.replace(code,"")
				code+=old_code
			}
		}
		code = pack+code
		file.write(code)
	}
	
	public static void writeClass(def code,Set<String> classNameSet,def packageName,def fileName){
		def binPath = CodeToClassUtil.class.getClassLoader().getResource("").getPath()
		def path = binPath.replace("/bin","")+"config/"
		def f = new File(path)
		if(!f.exists()){
			mkdir(path)
		}
		def folders = packageName.split ("\\.")
		folders.each{
			path+=it
		}
		f = new File(path)
		if(!f.exists()){
			mkdir(path)
		}
		path = path+"/"+fileName.split ("\\.")[0]+".txt"
		def file = new File(path)
		def s = new StringBuffer()
		classNameSet.each {
			if(!it.contains("cs:")) s.append("cs:"+it).append(",")
			else s.append(it).append(",")
		}
		file.write(s.toString())
		path = parsePackageName(packageName)+fileName
		
		file = new File(path)
		if(file.exists()){
			if(code!=null&&code.trim().length()!=0){
				if(code.contains("class")){
					int index = code.indexOf("class")
					if(index>0)
						code = code.substring(index,code.length()-1)
					file.append(code)
				}
			}
			
		}else{
			code = "package ${packageName} \n"+code
			file.write(code)
		}
	}
	
	
	public static Set<String> readFileConfig(def packageName,def fileName){

		def binPath = CodeToClassUtil.class.getClassLoader().getResource("").getPath()
		def path = binPath.replace("/bin","")+"config/"
		def folders = packageName.split ("\\.")	
		folders.each{
			path+=it
		}
		
		path = path+"/"+fileName.split ("\\.")[0]+".txt"
		
		def file = new File(path)
		Set<String> set = []
		if(file.exists()){
			def content = file.getText()
			content.split (",").each{
				set.add(it)
			}
		}
		
		return set		
	}
	
	private static def validateParam(def param){
		if(!param) throw new RuntimeException("param is null")
		
		if(param.getClass().getName()!=String.class.getName()){
			throw new RuntimeException("param is not a str")
		}
		if(param.trim().equals("")){
			throw new RuntimeException("param is length 0")
		} 
	}
	
	private static def validateExtension(String param,List<String> extension){
		def sb = new StringBuffer();
		def filename = param.split("\\.")
		def ext = filename[-1]
		if(!extension.contains(ext)){
			throw new RuntimeException("file extension invalid!")
		}
	}
	
	
	private static void mkdir (def path){
		File f = new File(path)
		if(!f.exists()){
			f.mkdir()
		}
	}
	
}

