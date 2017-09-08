package b2b

class DomToGroovyHelper {

	//replace xslt value with groovy value
	public static String replaceUselessChar(String str) {
		
		String result = str.replace("OLLMappingLib:", "util.")
				.replace("current-dateTime()", "current_dateTime")
				.replace("string-length", "string_length")
				.replace("[last()]", "[-1]")
				.replace("=","==");
				
		if(!result.contains("../")){
			result = result.replace("/", ".")
		}else{
			result = result.replace("..", "'..'").replace("/", ".")
		}

		if (result.startsWith("util.LogicalEq(")) {
			result = result.replaceFirst("util.LogicalEq", "").replace(",", "==");
		}
		else if (result.startsWith("util.LogicalNe")){
			result = result.replaceFirst("util.LogicalNe", "").replace(",", "!=");
		}
		else if (result.startsWith("util.LogicalAnd")){
			result = result.replaceFirst("util.LogicalAnd", "").replace(",", "&&");
		}
		else if (result.startsWith("util.CodeConversionWithDefaultValue(")) {
			result = result.replace("tib:render-xml(CdeConversionData)", "conn");
		}
		
		//replace xsl index with groovy index
		def matcher = result =~ /\[\d+\]/
		matcher.each{
			String replacementStr = '[' + ((it.substring(1,it.length() - 1) as Integer) - 1) + ']';
			result = result.replace(it,replacementStr);
		}
	

		return result;
	}
	
	
}
