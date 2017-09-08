package b2b.groovyValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

public class B2BGroovyValidator {

	private static final String MAPPING_LOGIC_END = "//mapping logic end";
	private static final String MAPPING_LOGIC_START = "//mapping logic start";
	private static final String WARNING = "WARNING";
	private static final String ERROR = "ERROR";
	private static final String INFO = "INFO";
	private static final String SKIP = "SKIP";
	
	private static String[] logLevel = {SKIP,INFO,WARNING,ERROR};
//	private static String[] logLevel = {ERROR};
	
	private static Integer[] ignoreLines = {};
	private static String[] ignoreDef = {"TP_ID","MSG_TYPE_ID","DIR_ID","conn","uniqueId"};
	
	public boolean validGroorvy(String groovyScript){
		
		boolean result = true;
		
		if(groovyScript.indexOf(MAPPING_LOGIC_START) < 0){
			WriteLog(null, ERROR, "Missing mapping logic start flag");
			result = false;
			return result;
		}
		
		if(groovyScript.indexOf(MAPPING_LOGIC_END) < 0){
			WriteLog(null, ERROR, "Missing mapping logic end flag");
			result = false;
			return result;
		}
		
		String header = groovyScript.substring(0,groovyScript.indexOf(MAPPING_LOGIC_START));
		int beginLineNumber = header.split("\n").length;
		
		String mappingLogicPart = groovyScript.substring(groovyScript.indexOf(MAPPING_LOGIC_START) + 22, groovyScript.indexOf(MAPPING_LOGIC_END));
		
		String[] lines = mappingLogicPart.split("\n");
		
		Stack<String> stack = new Stack<String>();
		
		Map<String,Boolean> checkedDefCriteriaMap = new HashMap<String, Boolean>();
		Map<String,String> defCriteriaMap = new HashMap<String, String>();
		Map<String,String> defMap = new HashMap<String,String>();
		for(String globalDef : ignoreDef){
			defMap.put(globalDef, globalDef);
		}
		
		Map<String,String> loopMap = new HashMap<String, String>();
		
		List<Integer> ignoreList = Arrays.asList(ignoreLines);
		
		
		
			for(int i = 0; i < lines.length ;i++){
				
				Integer lineNumber = beginLineNumber + i;
				String line = lines[i].trim();
				try{
					//ignore line
					if("".equals(line) || line.startsWith("//")) continue;
					
					if(ignoreList.contains(lineNumber)){
						if(line.contains("{")){
							if(!line.contains("}")){
								stack.push("start");
							}
						}else if(line.contains("}")){
							stack.pop();
						}
						WriteLog(lineNumber, SKIP, line);
						continue;
					}
					
					if(line.contains("{")){//begin line
		
						if(line.contains(".each") || line.contains(".eachWithIndex")){//include each
							
							String[] loopParams = line.substring(line.indexOf("{") + 1, line.indexOf("->")).split(",");
							String loopElement = loopParams[0].trim();
							String loopIndex = null;
							if(loopParams.length > 1){
								loopIndex = loopParams[1].trim();
								if(!loopIndex.startsWith("index")){
									WriteLog(lineNumber, WARNING, "Loop index ["+loopIndex+"] should be started as 'index'.");
								}
								defMap.put(loopIndex, loopIndex);
							}
							
							String loopPath = line.substring(0,line.indexOf(".each")).trim().replace("inXml.","");
							while(loopPath.startsWith("current")){	//construct the map of loop's currentElement and absolute path
								String current = loopPath.substring(0,loopPath.indexOf("."));
								loopPath = loopPath.replace(current, loopMap.get(current));
							}
							
							stack.push(loopElement + "=" + loopPath);
							
							loopMap.put(loopElement, loopPath);
							
							if(!loopElement.startsWith("current")){
								WriteLog(lineNumber,WARNING, "Loop Element Name ["+loopElement+"] should be started as 'current'.");
							}
							
						}else if(line.startsWith("if") || line.startsWith("else if")){//include if or else if
							stack.push("start");
							String condition = line.substring(line.indexOf("(") + 1,line.lastIndexOf(")"));
							result = checkGpath(result, defMap,defCriteriaMap,checkedDefCriteriaMap, loopMap, lineNumber, line, "if", condition);
						}else if(line.contains(".find") && line.startsWith("def")){
							result = checkedCriteriaDef(result, defCriteriaMap, defMap,loopMap, lineNumber, line);
						}else{
						
							if(!line.contains("}")){	//end tag is in not same line with start tag
								stack.push("start");
							}else{
								if(line.indexOf("{") < line.indexOf("}")){//start tag and end tag in the same line and start tag is in front of end tag.
									WriteLog(lineNumber,WARNING, "["+line+"] End Tag '}' should not in the same line with start tag'{'.");
								}
							}
						}
					}else if(line.startsWith("if") || line.startsWith("else if")){//include if or else if
						String condition = line.substring(line.indexOf("(") + 1,line.lastIndexOf(")"));
						result = checkGpath(result, defMap,defCriteriaMap,checkedDefCriteriaMap, loopMap, lineNumber, line, "if", condition);
					}else if(line.length() == 1 && line.endsWith("}")){
						stack.pop();
					}else if(line.contains(".find") && line.startsWith("def")){
						result = checkedCriteriaDef(result, defCriteriaMap, defMap,loopMap, lineNumber, line);
					}else if(line.startsWith("def ")){//variable define
						line = line.substring(4);
						String[] varAndValue = line.split("(?<![=])=(?![=])");
						String varName = varAndValue[0].trim();
						String varValue = null;
						if(varAndValue.length > 1){
							varValue = varAndValue[1].trim();
						}else{
							WriteLog(lineNumber, WARNING, "Def [" + varName + "] should has initial value.");
						}
						defMap.put(varName, varValue);
						if(!varName.startsWith("v")){//standard variable name should be started as 'v'.
							WriteLog(lineNumber,WARNING, "Variable Name ["+varName+"] should be started as 'v'.");
						}
						
						//TODO check varValue
		//				System.out.println(varValue);
						
					}else if(line.startsWith("'")){//common field mapping
						String element = line.substring(0,line.indexOf("'", 1) + 1).trim();
						String gpath = line.substring(line.indexOf("'", 1)+1).trim();
						result = checkGpath(result, defMap,defCriteriaMap,checkedDefCriteriaMap, loopMap, lineNumber, line,element, gpath);
					}else{
						WriteLog(lineNumber,WARNING, "[" + line + "] " + "Cannot be recognised, please review.");
					}
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Error Line : " + lineNumber);
					result = false;
				}
			}
		
		if(!stack.isEmpty()){
			WriteLog(null,ERROR,"'{' and '}' are not totally matched, please review -- Not Match Count:" + stack.size());
		}
		
		return result;
	}

	private boolean checkedCriteriaDef(boolean result,
			Map<String, String> defCriteriaMap, Map<String, String> defMap,
			Map<String, String> loopMap, Integer lineNumber, String line) {
		line = line.substring(4);
		String[] varAndValue = line.split("(?<![=])=(?![=])");
		String varName = varAndValue[0].trim();
		String varValue = null;
		if(varAndValue.length > 1){
			varValue = varAndValue[1].trim();
		}else{
			WriteLog(lineNumber, WARNING, "Def [" + varName + "] should has initial value.");
		}
		if(!varValue.contains("}")){
			WriteLog(lineNumber, ERROR, "Definition(with criteria) [" + varName  +"]  must been defined in one line");
			result = false;
		}else if(!varValue.endsWith("}")){
			WriteLog(lineNumber, ERROR, "Definition(with criteria) [" + varName  +"] has potential NullPointerException issue");
			result = false;
		}
		
		if(checkCurrentElementGpath(result,defMap, loopMap, lineNumber, "def", varValue)){
			while(varValue.startsWith("current")){	//restore Loop's currentElment to absolute path
				String current = varValue.substring(0,varValue.indexOf("."));
				varValue = varValue.replace(current, loopMap.get(current));
			}
		}else{
			result = false;
		}
		
		defCriteriaMap.put(varName, varValue);
		
		loopMap.put(varName, varValue.substring(0,varValue.indexOf(".find")));
		return result;
	}

	private boolean checkGpath(boolean result, Map<String, String> defMap,Map<String, String> defCriMap,Map<String,Boolean> checkedDefCriteriaMap, Map<String, String> loopMap, Integer lineNumber, String line,String element, String gpath) {
		if(gpath.startsWith("'") || gpath.startsWith("\"")){//hard Code
			result = checkHardCode(result, lineNumber, element, gpath);
		}else if(gpath.startsWith("inXml.")){	//root element
			if(!gpath.contains(".text()")){
				WriteLog(lineNumber,WARNING, "["+ element +"]'s GPath ["+gpath+"] hasn't text(), please review.");
			}
			//TODO valid GPath with definition
		}else if(isMethod(gpath)){	//call method
			result = checkMethod(result, defMap,defCriMap,checkedDefCriteriaMap, loopMap, lineNumber, line,element, gpath);
		}else if(gpath.startsWith("current")){ //using currentElement in loop
			result = checkCurrentElementGpath(result, defMap, loopMap,lineNumber, element, gpath);
		}else if(isDef(gpath, defCriMap)){ //call criteria definition
//			String fullGpath = gpath;
//			String rootElement = fullGpath.substring(0, fullGpath.indexOf("."));
//			String childElement = fullGpath.substring(fullGpath.indexOf("."));
//			while(defCriMap.containsKey(rootElement)){
//				fullGpath = defCriMap.get(rootElement) + childElement;
//				rootElement = fullGpath.substring(0, fullGpath.indexOf("."));
//				childElement = fullGpath.substring(fullGpath.indexOf("."));
//			}
			//TODO valid GPath with definition
			
			if("if".equals(element)){
				if(!line.contains("notEmpty")){
					WriteLog(lineNumber, ERROR, "["+ element +"]'s GPath ["+gpath+"] has potential NullPointerException issue, must supplemnt notEmpty() checking, please review.");
					result = false;
				}else{
					checkedDefCriteriaMap.put(gpath, true);
				}
			}else if(!gpath.contains("]")){
				WriteLog(lineNumber, ERROR, "["+ element +"]'s GPath ["+gpath+"] is invaild, must supplemnt index [] after criteria def, please review.");
				result = false;
			}else {
				for(Entry<String, Boolean> entry : checkedDefCriteriaMap.entrySet()){
					if(gpath.contains(entry.getKey())){
						return result;
					}
				}
				WriteLog(lineNumber, ERROR, "["+ element +"]'s GPath ["+gpath+"] has potential NullPointerException issue, must supplemnt notEmpty() checking, please review.");
				result = false;
			}
			
		}else if(isDef(gpath, defMap)){ //call variable
			result = checkDef(result, defMap, lineNumber, element, gpath);
		}else{
			WriteLog(lineNumber,WARNING, "[" + line + "] " + "Cannot be recognised, please review.");
		}
		return result;
	}

	private boolean checkDef(boolean result, Map<String, String> defMap,
			Integer lineNumber, String element, String gpath) {
		if(!defMap.containsKey(gpath) && Pattern.compile("[\\D]+").matcher(gpath).matches()){
			for(Entry<String, String> entry : defMap.entrySet()){
				if(gpath.contains(entry.getKey())){
					return result;
				}
			}
			WriteLog(lineNumber,ERROR, "["+ element +"]'s GPath ["+gpath+"] is incorrect(variable is not defined), please review.");
			result = false;
		}
		return result;
	}

	private boolean checkCurrentElementGpath(boolean result,Map<String, String> defMap, Map<String, String> loopMap, Integer lineNumber, String element,String gpath) {
		String fullGpath = gpath;
		if(!fullGpath.startsWith("currentSystemDt")){
			if(fullGpath.indexOf(".") > 0){
				while(fullGpath.startsWith("current")){	//restore Loop's currentElment to absolute path
					String current = fullGpath.substring(0,fullGpath.indexOf("."));
					if(loopMap.containsKey(current)){
						fullGpath = fullGpath.replace(current, loopMap.get(current));
					}else{
						WriteLog(lineNumber,ERROR, "["+ element +"]'s GPath ["+gpath+"] is incorrect(["+ current +"] is not invalid), please review.");
						result = false;
					}
				}
			}else{
				result = checkDef(result, defMap, lineNumber, element, gpath);
			}
			
//			if(!fullGpath.contains(".text()")){
//				WriteLog(lineNumber,WARNING, "["+ element +"]'s GPath ["+gpath+"] hasn't text(), please review.");
//			}
		}
		
		//TODO valid GPath with definition
		return result;
	}

	private boolean checkHardCode(boolean result, Integer lineNumber,
			String element, String gpath) {
		if(!gpath.endsWith("'") && !gpath.endsWith("\"")){
			WriteLog(lineNumber,ERROR, "["+ element +"]'s GPath ["+gpath+"] is incorrect(missing ' at the end), please review.");
			result = false;
		}
		return result;
	}

	private boolean checkMethod(boolean result, Map<String, String> defMap,Map<String, String> defCriMap,Map<String,Boolean> checkedDefCriteriaMap, Map<String, String> loopMap, Integer lineNumber, String line,String element, String gpath) {
		String[] params = null;
		if(isMethod(gpath)){
			params = gpath.substring(gpath.indexOf("(") + 1, gpath.lastIndexOf(")")).split(",");
		}else{
			params = new String[]{gpath};
		}
		for(String param : params){
			if(isMethod(param)){
				checkMethod(result, defMap,defCriMap,checkedDefCriteriaMap, loopMap, lineNumber, line, element, param.trim());
			}else{
				result = checkGpath(result, defMap,defCriMap,checkedDefCriteriaMap, loopMap, lineNumber, line, element, param.trim());
			}
		}
		return result;
	}

	private boolean isMethod(String line) {
		return line.contains("(") && line.contains(")") && line.indexOf(")") - line.indexOf("(") > 1;
	}
	
	private boolean isDefCriteria(String gpath, Map<String, String> defCriMap){
		if(gpath.contains(".")){
			String rootElement = gpath.substring(0, gpath.indexOf("."));
			if(rootElement.endsWith("]")){
				return defCriMap.containsKey(rootElement.substring(0,rootElement.lastIndexOf("[")));
			}else{
				return defCriMap.containsKey(rootElement);
			}
		}else{
			if(gpath.contains("[")){
				return defCriMap.containsKey(gpath.substring(0,gpath.lastIndexOf("[")));
			}else{
				return defCriMap.containsKey(gpath);
			}
		}
	}
	
	private boolean isDef(String gpath, Map<String, String> defMap){
		if(Pattern.compile("[\\w\\d_]+").matcher(gpath).matches()){
			return defMap.containsKey(gpath);
		}else if(gpath.contains(".")){
			String rootElement = gpath.substring(0, gpath.indexOf("."));
			if(rootElement.endsWith("]")){
				return defMap.containsKey(rootElement.substring(0,rootElement.lastIndexOf("[")));
			}else{
				return defMap.containsKey(rootElement);
			}
		}else{
			if(gpath.contains("[")){
				return defMap.containsKey(gpath.substring(0,gpath.lastIndexOf("[")));
			}else{
				return defMap.containsKey(gpath);
			}
		}
	}
	
	private void WriteLog(Integer lineNumber, String type, String message) {
		if(Arrays.asList(logLevel).contains(type)){
			System.out.println("Line:"+ lineNumber +" " + type + ": " + message );
		}
	}
	
}
