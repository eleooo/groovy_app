package b2b

class PospHandler {

	public static String postHandler(String str){
		
		if(str.startsWith("def")){
			//for the case the first child node of body is variable, then need to put prefix "outXml." to the correct node.
			str = str.replaceFirst("[\\s]+'", "\noutXml.'");
		}else{
			//default, the first child node is not a variable.
			str = "outXml." + str;
		}
		
		//replace xsl variable name with groovy variable name
		str =  str.replace('$', '');
		
		
		return str;
	}
	
}
