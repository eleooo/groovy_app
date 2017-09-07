import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import java.io.StringWriter
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date
import java.util.TimeZone

String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
	
	oll.b2b.core.mapping.util.MappingUtil util = new oll.b2b.core.mapping.util.MappingUtil();
	 
	/**
	 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
	 */
	inputXmlBody = util.removeBOM(inputXmlBody)
	
	/**
	 * Part II: get OLL mapping runtime parameters
	 */
	def ollSessionId = util.getRuntimeParameter("OLLSessionID", runtimeParams);
	def sourceFileName = util.getRuntimeParameter("OLL_OriginalSourceFileName", runtimeParams);
	//	def portId = getRuntimeParameter("OLL_SendPortID", runtimeParams);
	//	def portProperty = getRuntimeParameter("PortProperty", runtimeParams);
	//pmt info
	def TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
	def MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
	def DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
	def MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

	/**
	 * Part III: read xml and prepare output xml
	 */
	//Important: the inputXml is xml root element
	def ZVSHPFL1 = new XmlParser().parseText(inputXmlBody);

	def writer = new StringWriter()
	def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	
	
	/**
	 * Part IV: mapping script start from here
	 */

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()
	
outXml.'ns0:hts_file_root'('xmlns:datatype':'http://www.oocllogistics.com/datatype', 'xmlns:header':'http://www.oocllogistics.com/header', 'xmlns:notelog':'http://www.oocllogistics.com/notelog', 'xmlns:ns0':'http://www.oocllogistics.com/hts', 'xmlns:pfx8':'http://www.oocllogistics.com/EchoStar_PO_UIF_2dot0_Schema', 'xmlns:xsl':'http://www.w3.org/1999/XSL/Transform'){
    'ns0:file_header'{
      'header:SENDER_ID' 'ECHOSTAR'
      'header:RECIPIENT_ID' 'PLLP'
      def var_v1 =  substring(current_dateTime,1,19)
      def var_v2 =  util.ConvertDatetimeFormat(string(var_v1) , "" , "yyyyMMdd" , "" , "")
      def var_v9 =  inXml.position()
      'header:MSG_CTRL_REF_NUM' var_v2
      def var_v3 =  substring(current_dateTime,1,19)
      'header:MSG_SENT_DATETIME' var_v3
      def var_v4 =  substring(current_dateTime,1,19)
      'header:MSG_REC_DATETIME' var_v4
      'header:MSG_STATUS' 'N'
      'header:MSG_TYPE' 'HTS'
      'header:MSG_DEF_VER' '2.01'
    }
    inXml.Detail[TC2[TC01=='J'][0].TC02 !== ''].each { current_Detail ->
      'ns0:hts_msg_root'{
        'ns0:msg_header'{
          'header:SENDER_ID' 'ECHOSTAR'
          'header:RECIPIENT_ID' 'PLLP'
          def var_v5 =  substring(current_dateTime,1,19)
          def var_v6 =  util.ConvertDatetimeFormat(string(var_v5) , "" , "yyyyMMdd" , "" , "")
          'header:MSG_CTRL_REF_NUM' var_v6
          def var_v7 =  substring(current_dateTime,1,19)
          'header:MSG_SENT_DATETIME' var_v7
          'header:MSG_STATUS' 'N'
          'header:MSG_TYPE' 'HTS'
          'header:MSG_DEF_VER' '2.01'
          'header:data_header'{
            'header:to' 'PLLP'
            'header:from' 'ECHOSTAR'
            def var_v8 =  substring(current_dateTime,1,19)
            'header:time_stamp' var_v8
            'header:header_msg_type' 'HTS'
            'header:body_msg_type' 'HTS'
            'header:msg_ver' var_v9
          }
        }
        def var_v12 =  string(PO1.PO109.text())
        'ns0:hts_record'{
          'xsl:for-each-group''group-by':'string(PO1/PO109/text())', select:'PO1Loop[string(PO1/PO109/text())!=\'\']' { current_PO1Loop ->
            'ns0:cust'{
              'datatype:PARTNER_NAME' 'ECHOSTAR'
              'datatype:PARTNER_EXT_REF_NUM' 'ECHOSTAR'
            }
          }
          'xsl:for-each-group''group-by':'string(PO1/PO108/text())', select:'../PO1Loop[string(PO1/PO108/text())!=\'\']' { current_PO1Loop ->
            def var_v15 =  string(PO1.PO108.text())
            'ns0:sku'{
              def var_v16 =  string(var_v12)
              'datatype:SKU_CODE'{
                if( var_v9!==0 and var_v15=='BP') var_v16
              }
              def var_v17 =  string(var_v12)
              'datatype:STYLE_CODE'{
                if( var_v9!==0 and var_v15=='BP') var_v17
              }
            }
          }
          current_Detail.'..'.TC2.each { current_TC2 ->
            def var_v18 =  (string(TC01.text()) == "J")
            if( var_v18){
              'ns0:hts'{
                def var_v19 =  current_TC2.position()
                'ns0:seq' var_v19
                if( current_TC2.TC02){
                  'ns0:hts_num' current_TC2.TC02.text()
                }
              }
            }
          }
        }
      }
    }
  }
	//mapping logic end
		
//	def outxml = XmlUtil.serialize(writer.toString());
//	return outxml;
	
	return writer.toString();
	
}

