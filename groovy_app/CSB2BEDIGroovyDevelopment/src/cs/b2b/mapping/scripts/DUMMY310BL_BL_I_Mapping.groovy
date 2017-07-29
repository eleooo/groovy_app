package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection

/**
 * IG		: BL CS2 310
 * Version	: 0.8
 */

String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {


    cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

    /**
     * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
     */
    inputXmlBody = util.removeBOM(inputXmlBody)

    /**
     * Part II: get app mapping runtime parameters
     */
    def appSessionId = util.getRuntimeParameter("AppSessionID", runtimeParams);
    def sourceFileName = util.getRuntimeParameter("OriginalSourceFileName", runtimeParams);
    //pmt info
    def TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
    def MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
    def DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
    def MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

    /**
     * Part III: read xml and prepare output xml
     */
    //Important: the inputXml is xml root element
    def inputBLCS2XML = new XmlSlurper().parseText(inputXmlBody);

    def writer = new StringWriter()
    def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
    //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
    outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

    /**
     * Part IV: mapping script start from here
     */

	//prod stage
	def IFTMCS = new XmlSlurper().parseText(inputXmlBody);
	
	//coding stage
	//cs.b2b.mapping.scripts.IFTMCS IFTMCS = new cs.b2b.mapping.scripts.IFTMCS();
	
	
	IFTMCS.Group_UNH.each { currentTxn ->
		currentTxn.Group11_NAD.each  {
			it.NAD.C058_03.E3124_02
		}
	}
	

    return writer.toString();
}

public String commonSubstring(String srcString, int startIndex, int substringLength) {
    if (startIndex > srcString.length()) {
        return ""
    } else {
        if (startIndex + substringLength >= srcString.length()) {
            return srcString.substring(startIndex)
        } else {
            return srcString.substring(startIndex, startIndex + substringLength)
        }
    }
}

public String ConcatphoneNumber(String CountryCode, String AreaCode, String Number) {
    String result;
    if (CountryCode != "" && AreaCode != "" && Number != "") {
        result = CountryCode.concat("-").concat(AreaCode).concat("-").concat(Number)
    } else if (CountryCode == "" && AreaCode != "" && Number != "") {
        result = AreaCode.concat("-").concat(Number);
    } else if (CountryCode == "" && AreaCode == "" && Number != "") {
        result = Number;
    }

    return result;
}