package xmlToGroovy

import org.w3c.dom.Document
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class XmlToBeanSplit2 {

	static Set<String> elementFlag = new HashSet<String>();
	static Set<String> elementAttrFlag = new HashSet<String>();
	static Set<String> multiElementList = [];
	//add multiElementList class
	static boolean NodeFlag=true
	//create FileName
	static String FileName

	//duplication name'
	static Set<String> ElementList = new HashSet<String>();

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
		String xslName = "EDI_CS_IFTMCS.xml"

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

		pmtSb.append("import com.google.gson.TypeAdapter;\n")
		pmtSb.append("import com.google.gson.annotations.JsonAdapter;\n")
		pmtSb.append("import com.google.gson.stream.JsonReader;\n")
		pmtSb.append("import com.google.gson.stream.JsonWriter;\n")
		//println pmtSb.toString()
		nodeTranserse(document.getDocumentElement(),pmtSb,commonSb);
	//	println AdapterFlag
		nodeAddAdapter(AdapterFlag,pmtSb)

		println '之前'+multiElementList


		for(i in 0..<multiElementList.size()){
			String temp=multiElementList[0]
			multiElementList.remove(temp)
			multiElementList.add(temp.replace("cs:",""))
		}
		println '去命名空间'+multiElementList

		//elementFlag.clear()
		//elementAttrFlag.clear()


		//标记是否重名
		//getmultiElementList(document.getDocumentElement())
		//添加父节点
	//	println "加父节点"+multiElementList

		for(i in 0..<multiElementList.size()){
			String temp=multiElementList[0]
			multiElementList.remove(temp)
			multiElementList.add('"'+temp.replace("cs:","")+'"')
		}
		println '加引号'+multiElementList
		String pmtSbString=pmtSb.toString().replace("1111111111111111111111111111111111------1111111111111111111111111111111111111111","\tpublic static final Set<String> MultiElementList = "+multiElementList)
		//pmtSb

		System.setOut(new PrintStream(new File("""src/cs/"""+FileName+""".groovy""")))
		println pmtSbString
		println commonSb.toString()




		//println '之后'+multiElementList

		//println AdapterFlag


	}


	public static void nodeTranserse(org.w3c.dom.Node node, StringBuffer pmtSb, StringBuffer commonSb){
		
		Map<String,Integer> map = new HashMap<String, Integer>();
		//childNode flag
		Set<Boolean> flag = new HashSet<Boolean>();

		//childNode attributes
	//	Set<String> attr_Set = new HashSet<String>();

		//boolean attr_flag=false
		String rNodeName=node.getNodeName();
		
		NodeList allNodes=node.getChildNodes();
		int size=allNodes.getLength();

		//如果不存在这个节点的名字 下面开始生成这个对于的classs了
		if(!elementFlag.contains(rNodeName)){
			elementFlag.add(rNodeName)
			//println 'node.getNodeType() '+node.getNodeType()
			//add class Name and attrbuit
			if(node.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
				if(rNodeName.contains("cs:")){
					commonSb.append("class " + rNodeName.replace("cs:","") + " implements Serializable {\n")
					if(NodeFlag==true){
						commonSb.append("1111111111111111111111111111111111------1111111111111111111111111111111111111111\n")
						FileName=rNodeName.replace("cs:","")
						NodeFlag=false
					}
					
					if(node.hasAttributes()){
						node.getAttributes().each { curNode ->
							for(i in 0..<curNode.getLength()){
								String attrName =  curNode.item(i).getNodeName()
								if(!['xsi:schemaLocation','xmlns','xmlns:cs','xmlns:xsi'].contains(attrName)){
									commonSb.append("\tString attr_"+attrName+";\n ")
								}
							}
						}
					}

				}
				else{
					pmtSb.append("class " + rNodeName + " implements Serializable {\n")
					if(NodeFlag==true){
						pmtSb.append("1111111111111111111111111111111111------1111111111111111111111111111111111111111\n")
						FileName=rNodeName
						NodeFlag=false
					}
					if(node.hasAttributes()){
						node.getAttributes().each { curNode ->
							for(i in 0..<curNode.getLength()){
								String attrName =  curNode.item(i).getNodeName()
								if(!['xsi:schemaLocation','xmlns','xmlns:cs','xmlns:xsi'].contains(attrName)){
									pmtSb.append("\tString attr_"+attrName+";\n ")
								}
							}
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

				
				StringBuffer tmpSB = new StringBuffer();
				//add childNode
				for(int j=0;j<size;j++){
					org.w3c.dom.Node childNode=allNodes.item(j);
					
					if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
						String childNodeName = childNode.getNodeName()

						//println 'childNodeName '+childNodeName
						//if childNode have ChildNode and do not have attributes
						if(childNode.getChildNodes().getLength() <=1  && !childNode.hasAttributes()){
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

									Set<String> attr_Set = new HashSet<String>();
									if(childNode.attributes.length>0){
										tmpSB.append("@JsonAdapter("+childNodeName+"Adapter.class)\n")
									}
									tmpSB.append("class " + childNodeName.replace("cs:","") + " implements Serializable {\n")
									if(NodeFlag==true){
										tmpSB.append("1111111111111111111111111111111111------1111111111111111111111111111111111111111\n")
										FileName= childNodeName.replace("cs:","")
										NodeFlag=false
									}
									childNode.getAttributes().each { curNode ->
										for(i in 0..<curNode.getLength()){
											String attrName =  "attr_" + curNode.item(i).getNodeName()
											tmpSB.append("\tString "+attrName+";\n ")
											attr_Set.add(attrName)
										}
									}
									if(attr_Set!=null){
										attr_Set.add(childNodeName)
										AdapterFlag.put(childNodeName,attr_Set)
									}

//									if(attr_Set!=null && !childNodeName.contains('cs:')){
//										attr_Set.add(childNodeName)
//										AdapterFlag.put(childNodeName,attr_Set)
//									}
									
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

		if(map.findAll{it.value > 0}){
			multiElementList.addAll(map.findAll{it.value > 0}.keySet())
		}
	}

	public static void nodeAddAdapter(Map<String,Set<String>> adapterFlag, StringBuffer pmtSb){
		pmtSb.append('\n')
		for(adapter in adapterFlag){
			pmtSb.append('\n')
			def adapterObj=adapter.getKey().substring(0,1).toLowerCase()+adapter.getKey().substring(1)
			//println adapterObj
			pmtSb.append("public class "+adapter.getKey()+"Adapter"+" extends TypeAdapter<"+adapter.getKey()+">{\n")
			pmtSb.append("\t@Override\n")
			pmtSb.append("\tpublic "+adapter.getKey()+" read(JsonReader jsonReader) throws IOException {\n")
			pmtSb.append("\t\t"+adapter.getKey()+" "+adapterObj+" = new "+adapter.getKey()+"();\n")
			pmtSb.append("\t\ttry{\n")
			pmtSb.append("\t\t\tjsonReader.beginObject();\n")
			pmtSb.append("\t\t\tjsonReader.setLenient(true)\n")
			pmtSb.append("\t\t\twhile (jsonReader.hasNext()) {\n")
			pmtSb.append("\t\t\t\tswitch (jsonReader.nextName()) {\n")
			for(attr in adapter.getValue()){
				//println "attr "+attr
				pmtSb.append("\t\t\t\t\tcase \'"+attr+"\':\n")
				pmtSb.append("\t\t\t\t\t\t"+adapterObj+"."+attr+" = jsonReader.nextString();"+"\n")
				pmtSb.append("\t\t\t\t\t\tbreak;\n")
			}
			pmtSb.append("\t\t\t\t}\n")
			pmtSb.append("\t\t\t}\n")
			pmtSb.append("\t\t\tjsonReader.endObject()\n")
			pmtSb.append("\t\t}catch(IllegalStateException e){\n")
			pmtSb.append("\t\t\ttry{\n")
			pmtSb.append("\t\t\t\t"+adapterObj+" = new "+adapter.getKey()+"();"+"\n")
			pmtSb.append("\t\t\t\t"+adapterObj+"."+adapter.getKey()+" = jsonReader.nextString();"+"\n")
			pmtSb.append("\t\t\t}catch(IllegalStateException e2){\n")
			pmtSb.append("\t\t\t\t"+adapterObj+" = new "+adapter.getKey()+"();"+"\n")
			pmtSb.append("\t\t\t\tjsonReader.nextNull();\n")
			pmtSb.append("\t\t\t}\n")
			pmtSb.append("\t\t}\n")
			pmtSb.append("\t\treturn "+adapterObj+"\n")
			pmtSb.append("\t}\n")
			pmtSb.append("\n")

			pmtSb.append("\t@Override\n")
			pmtSb.append("\tpublic void write(JsonWriter jsonWriter,"+adapter.getKey()+" "+adapterObj+") throws IOException {\n")
			pmtSb.append("\t\tif("+adapterObj+"?."+adapter.getKey()+" == null){"+"\n")
			pmtSb.append("\t\t\tjsonWriter.nullValue()\n")
			pmtSb.append("\t\t}else{\n")
			pmtSb.append("\t\t\t"+"if(")
			for(attr in adapter.getValue()){
				if(attr.startsWith("attr_")){
					pmtSb.append(adapterObj+"."+attr+" != null || ")
				}
			}
			pmtSb.deleteCharAt(pmtSb.length()-1)
			pmtSb.deleteCharAt(pmtSb.length()-1)
			pmtSb.deleteCharAt(pmtSb.length()-1)
			pmtSb.append("){\n")
			pmtSb.append("\t\t\t\tjsonWriter.beginObject()\n")
			for(String attr : adapter.getValue()){
				if(attr.startsWith("attr_")){
					pmtSb.append("\t\t\t\tif("+adapterObj+"."+attr+" != null){\n")
					pmtSb.append("\t\t\t\t\tjsonWriter.name(\""+attr+"\")\n")
					pmtSb.append("\t\t\t\t\tjsonWriter.value("+adapterObj+"."+attr+")\n")
					pmtSb.append("\t\t\t\t}\n")
				}
			}
			pmtSb.append("\t\t\t\tjsonWriter.name(\""+adapter.getKey()+"\")\n")
			pmtSb.append("\t\t\t\tjsonWriter.value("+adapterObj+"."+adapter.getKey()+")\n")
			pmtSb.append("\t\t\t\tjsonWriter.endObject()\n")
			pmtSb.append("\t\t\t}else{\n")
			pmtSb.append("\t\t\t\tjsonWriter.value("+adapterObj+"."+adapter.getKey()+")\n")
			pmtSb.append("\t\t\t}\n")
			pmtSb.append("\t\t}\n")
			pmtSb.append("\t}\n")
			pmtSb.append("}\n")
			}
		}


	public static void getmultiElementList(org.w3c.dom.Node node){

		Map<String,Integer> map = new HashMap<String, Integer>();
		Set<Boolean> flag = new HashSet<Boolean>();


		String rNodeName=node.getNodeName();

		NodeList allNodes=node.getChildNodes();
		int size=allNodes.getLength();

		if(!elementFlag.contains(rNodeName)){
			elementFlag.add(rNodeName)

			if(size>0){

//				StringBuffer tmpSB = new StringBuffer();
				for(int j=0;j<size;j++){
					org.w3c.dom.Node childNode=allNodes.item(j);

					if(childNode.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE){
						String childNodeName = childNode.getNodeName()
						//该节点没有子节点 而且没有属性
						if(childNode.getChildNodes().getLength() <=1 && !childNode.hasAttributes()){
							if(!flag.contains(childNodeName)){
								if(childNode.parentNode.getNodeName().contains("cs:")) {
									if (childNodeName.contains("cs:")) {
										if (multiElementList.contains(childNodeName.replace("cs:", ""))) {
											multiElementList.remove(childNodeName.replace("cs:", ""))
											multiElementList.add(childNode.parentNode.getNodeName().replace("cs:", "") + "." + childNodeName.replace("cs:", ""))
										}
									}else{
										if (multiElementList.contains(childNodeName)) {
											multiElementList.remove(childNodeName)
											multiElementList.add(childNode.parentNode.getNodeName() + "." + childNodeName)
										}
									}
								} else {
									if (multiElementList.contains(childNodeName)) {
										multiElementList.remove(childNodeName)
										multiElementList.add(childNode.parentNode.getNodeName() + "." + childNodeName)
									}
								}
							}
						} else{
							if(map.get(childNodeName) > 0 && !flag.contains(childNodeName)) {
								flag.add(childNodeName)
							} else if(!flag.contains(childNodeName)){
								if(childNode.parentNode.getNodeName().contains("cs:")) {
									if (childNodeName.contains("cs:")) {
										if (multiElementList.contains(childNodeName.replace("cs:", ""))) {
											multiElementList.remove(childNodeName.replace("cs:", ""))
											multiElementList.add(childNode.parentNode.getNodeName().replace("cs:", "") + "." + childNodeName.replace("cs:", ""))
										}
									}
								} else {
									if (multiElementList.contains(childNodeName)) {
										multiElementList.remove(childNodeName)
										multiElementList.add(childNode.parentNode.getNodeName() + "." + childNodeName)
									}
								}
							}
						}
					}
				}
			}

			flag = new HashSet<Boolean>();

			for(int j=0;j<size;j++){
				org.w3c.dom.Node childNode=allNodes.item(j);
				String childNodeName = childNode.getNodeName()
				if(childNode.getChildNodes().getLength() > 1 && !flag.contains(childNodeName)){
					flag.add(childNodeName);
					getmultiElementList(childNode);
				}

			}

		}
	}

	}
/**
public class TotalAmtInPmtCurrencyAdapter extends TypeAdapter<TotalAmtInPmtCurrency> {
	@Override
	public TotalAmtInPmtCurrency read(JsonReader jsonReader) throws IOException {

		TotalAmtInPmtCurrency totalAmtInPmtCurrency = new TotalAmtInPmtCurrency();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_Currency":
						totalAmtInPmtCurrency.attr_Currency = jsonReader.nextString();
						break;
					case "attr_ExchangeRate":
						totalAmtInPmtCurrency.attr_ExchangeRate = jsonReader.nextString();
						break;
					case "TotalAmtInPmtCurrency":
						totalAmtInPmtCurrency.TotalAmtInPmtCurrency = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				totalAmtInPmtCurrency = new TotalAmtInPmtCurrency();
				totalAmtInPmtCurrency.TotalAmtInPmtCurrency  = jsonReader.nextString();
			}catch(IllegalStateException e2){
				totalAmtInPmtCurrency = new TotalAmtInPmtCurrency();
				jsonReader.nextNull();
			}
		}
		return totalAmtInPmtCurrency;
	}
	@Override
	public void write(JsonWriter jsonWriter, TotalAmtInPmtCurrency totalAmtInPmtCurrency) throws IOException {
		if(totalAmtInPmtCurrency?.TotalAmtInPmtCurrency == null){
			jsonWriter.nullValue()
		}else{
			if(totalAmtInPmtCurrency.attr_Currency != null || totalAmtInPmtCurrency.attr_ExchangeRate != null){
				jsonWriter.beginObject()
				if(totalAmtInPmtCurrency.attr_Currency != null){
					jsonWriter.name("attr_Currency")
					jsonWriter.value(totalAmtInPmtCurrency.attr_Currency)
				}
				if(totalAmtInPmtCurrency.attr_ExchangeRate != null){
					jsonWriter.name("attr_ExchangeRate")
					jsonWriter.value(totalAmtInPmtCurrency.attr_ExchangeRate)
				}
				jsonWriter.name("TotalAmtInPmtCurrency")
				jsonWriter.value(totalAmtInPmtCurrency.TotalAmtInPmtCurrency)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(totalAmtInPmtCurrency.TotalAmtInPmtCurrency)
			}
		}
	}
}

public class TotalAmtInAdditionalPmtCurrencyAdapter extends TypeAdapter<TotalAmtInAdditionalPmtCurrency> {

	@Override
	public TotalAmtInAdditionalPmtCurrency read(JsonReader jsonReader) throws IOException {

		TotalAmtInAdditionalPmtCurrency totalAmtInAdditionalPmtCurrency = new TotalAmtInAdditionalPmtCurrency();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_Currency":
						totalAmtInAdditionalPmtCurrency.attr_Currency = jsonReader.nextString();
						break;
					case "attr_ExchangeRate":
						totalAmtInAdditionalPmtCurrency.attr_ExchangeRate = jsonReader.nextString();
						break;
					case "TotalAmtInAdditionalPmtCurrency":
						totalAmtInAdditionalPmtCurrency.TotalAmtInAdditionalPmtCurrency = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				totalAmtInAdditionalPmtCurrency = new TotalAmtInAdditionalPmtCurrency();
				totalAmtInAdditionalPmtCurrency.TotalAmtInAdditionalPmtCurrency  = jsonReader.nextString();
			}catch(IllegalStateException e2){
				totalAmtInAdditionalPmtCurrency = new TotalAmtInAdditionalPmtCurrency();
				jsonReader.nextNull();
			}
		}

		return totalAmtInAdditionalPmtCurrency;
	}

	@Override
	public void write(JsonWriter jsonWriter, TotalAmtInAdditionalPmtCurrency totalAmtInAdditionalPmtCurrency) throws IOException {
		if(totalAmtInAdditionalPmtCurrency?.TotalAmtInAdditionalPmtCurrency == null){
			jsonWriter.nullValue()
		}else{
			if(totalAmtInAdditionalPmtCurrency.attr_Currency != null || totalAmtInAdditionalPmtCurrency.attr_ExchangeRate != null){
				jsonWriter.beginObject()
				if(totalAmtInAdditionalPmtCurrency.attr_Currency != null){
					jsonWriter.name("attr_Currency")
					jsonWriter.value(totalAmtInAdditionalPmtCurrency.attr_Currency)
				}
				if(totalAmtInAdditionalPmtCurrency.attr_ExchangeRate != null){
					jsonWriter.name("attr_ExchangeRate")
					jsonWriter.value(totalAmtInAdditionalPmtCurrency.attr_ExchangeRate)
				}
				jsonWriter.name("TotalAmtInAdditionalPmtCurrency")
				jsonWriter.value(totalAmtInAdditionalPmtCurrency.TotalAmtInAdditionalPmtCurrency)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(totalAmtInAdditionalPmtCurrency.TotalAmtInAdditionalPmtCurrency)
			}
		}
	}

}
 **/


