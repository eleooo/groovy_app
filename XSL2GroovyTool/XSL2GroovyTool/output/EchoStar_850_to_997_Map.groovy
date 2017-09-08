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
	
def ISAGSControl =
outXml.'##Control_Number#1#Call_Sequence_Method==GetSequenceNextValWithDefault;;EchoStar_997_ISA;;1##%09d##'
  def STSEControl =  '##Control_Number#1#Call_Sequence_Method==GetSequenceNextValWithDefault;;EchoStar_997_ISA;;1##%04d##'
  def var_v18 =  inXml.count(.pfx8:EchoStar_PO_MsgRoot.Detail)
  def var_v19 =  util.MathMultiply2("2" , string(var_v18))
  def var_v20 =  util.MathAdd(string(var_v19) , "4")
  'ns0:EchoStar_997_Root'('xmlns:ns0':'http://www.oocllogistics.com/Ethical977_POACK', 'xmlns:pfx8':'http://www.oocllogistics.com/EchoStar_PO_UIF_2dot0_Schema', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance', 'xmlns:xsl':'http://www.w3.org/1999/XSL/Transform'){
    'MSG'{
      'InterchangeControlHeader'{
        'ISA'{
          'ISA01' '00'
          'ISA02' ''
          'ISA03' '00'
          'ISA04' ''
          def var_v1 =  util.ReturnSenderQualifer(string(Header.ISA.ISA07.text()))
          'ISA05' var_v1
          def var_v2 =  util.EchoStar_850_to_997ReturnSenderID(string(Header.ISA.ISA08.text()))
          'ISA06' var_v2
          def var_v3 =  util.ReturnReceiverQualifer(string(Header.ISA.ISA05.text()))
          'ISA07' var_v3
          def var_v4 =  util.EchoStar_850_to_997ReturnReceiverID(string(Header.ISA.ISA06.text()))
          'ISA08' var_v4
          def var_v5 =  inXml.util.EchoStar_850_to_997MyConcat2()
          'ISA09' var_v5
          def var_v6 =  inXml.util.EchoStar_850_to_997_MapMyC3oncat()
          'ISA10' var_v6
          'ISA11' 'U'
          'ISA12' '00400'
          def var_v7 =  inXml.""
          def var_v8 =  inXml.""
          'ISA13' ISAGSControl
          'ISA14' '0'
          'ISA15' 'P'
          'ISA16' '>'
        }
        'GS'{
          'GS01' 'FA'
          def var_v9 =  inXml.string(Header.GS.GS03.@xsi:nil) == 'true'
          if( string(var_v9)=='true'){
            'GS02'('xsi:nil': 'true')
          }
          if( string(var_v9)=='false'){
            'GS02' inXml.Header.GS.GS03.text()
          }
          def var_v10 =  inXml.string(Header.GS.GS02.@xsi:nil) == 'true'
          if( string(var_v10)=='true'){
            'GS03'('xsi:nil': 'true')
          }
          if( string(var_v10)=='false'){
            'GS03' inXml.Header.GS.GS02.text()
          }
          def var_v11 =  inXml.util.EchoStar_850_to_997MyConcat()
          'GS04' var_v11
          def var_v12 =  inXml.util.EchoStar_850_to_997_MapMyC3oncat()
          'GS05' var_v12
          def var_v13 =  inXml.""
          'GS06' ISAGSControl
          'GS07' 'X'
          'GS08' '004010'
        }
      }
      'Header'{
        'ST'{
          'ST01' '997'
          def var_v14 =  inXml.""
          def var_v15 =  inXml.""
          'ST02' '##Control_Number#1#Call_Sequence_Method==GetSequenceNextValWithDefault;;EchoStar_997_ISA;;1##%04d##'
        }
        'AK1'{
          'AK101' 'PO'
          def var_v16 =  inXml.string(Header.GS.GS06.@xsi:nil) == 'true'
          if( string(var_v16)=='true'){
            'AK102'('xsi:nil': 'true')
          }
          if( string(var_v16)=='false'){
            'AK102' inXml.Header.GS.GS06.text()
          }
        }
      }
      'Detail'{
        inXml.Detail.each { current_Detail ->
          'LoopAK2'{
            'AK2'{
              'AK201' '850'
              def var_v17 =  util.MyC2so45ncat(string(ST.ST02.text()))
              'AK202' var_v17
            }
            'AK5'{
              'AK501' 'A'
            }
          }
        }
        'AK9'{
          'AK901' 'A'
          'AK902' var_v18
          'AK903' var_v18
          'AK904' var_v18
        }
      }
      'Summary'{
        'SE'{
          'SE01' floor(var_v20)
          def var_v21 =  inXml.""
          def var_v22 =  inXml.""
          'SE02' '##Control_Number#1#Call_Sequence_Method==GetSequenceNextValWithDefault;;EchoStar_997_ISA;;1##%04d##'
        }
      }
      'InterchangeControlTrailer'{
        'GE'{
          'GE01' '1'
          def var_v23 =  inXml.""
          'GE02' ISAGSControl
        }
        'IEA'{
          'IEA01' '1'
          def var_v24 =  inXml.""
          'IEA02' ISAGSControl
        }
      }
    }
  }
	//mapping logic end
		
//	def outxml = XmlUtil.serialize(writer.toString());
//	return outxml;
	
	return writer.toString();
	
}

