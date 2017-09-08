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
	
outXml.'ns0:po_file_root'('xmlns:datatype':'http://www.oocllogistics.com/datatype', 'xmlns:header':'http://www.oocllogistics.com/header', 'xmlns:notelog':'http://www.oocllogistics.com/notelog', 'xmlns:ns0':'http://www.oocllogistics.com/po', 'xmlns:pfx8':'http://www.oocllogistics.com/EchoStar_PO_UIF_2dot0_Schema', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance', 'xmlns:xsl':'http://www.w3.org/1999/XSL/Transform'){
    'ns0:file_header'{
      'header:SENDER_ID' 'ECHOSTAR'
      'header:RECIPIENT_ID' 'POP'
      def var_v1 =  substring(current_dateTime,1,19)
      def var_v2 =  util.ConvertDatetimeFormat(string(var_v1) , "" , "yyyyMMdd" , "" , "")
      'header:MSG_CTRL_REF_NUM' var_v2
      def var_v3 =  substring(current_dateTime,1,19)
      'header:MSG_SENT_DATETIME' var_v3
      def var_v4 =  substring(current_dateTime,1,19)
      'header:MSG_REC_DATETIME' var_v4
      'header:MSG_STATUS' 'N'
      'header:MSG_TYPE' 'PO'
      'header:MSG_DEF_VER' '2.01'
    }
    inXml.Detail.each { current_Detail ->
      def var_v10 =  string(BEG.BEG03.text())
      def var_v11 =  string(BEG.BEG04.text())
      def var_v19 =  (string(FOB.FOB02.text()) == "CA")
      def var_v21 =  string(FOB.FOB02.text())
      def var_v22 =  (var_v21 == "CA")
      def var_v25 =  string(FOB.FOB04.text())
      def var_v26 =  string(FOB.FOB05.text())
      'ns0:po_msg_root'{
        'ns0:msg_header'{
          'header:SENDER_ID' 'ECHOSTAR'
          'header:RECIPIENT_ID' 'POP'
          def var_v5 =  substring(current_dateTime,1,19)
          def var_v6 =  util.ConvertDatetimeFormat(string(var_v5) , "" , "yyyyMMdd" , "" , "")
          'header:MSG_CTRL_REF_NUM' var_v6
          def var_v7 =  substring(current_dateTime,1,19)
          'header:MSG_SENT_DATETIME' var_v7
          'header:MSG_STATUS' 'N'
          'header:MSG_TYPE' 'PO'
          'header:MSG_DEF_VER' '2.01'
          'header:data_header'{
            'header:to' 'POP'
            'header:from' 'ECHOSTAR'
            def var_v8 =  substring(current_dateTime,1,19)
            'header:time_stamp' var_v8
            'header:header_msg_type' 'PO'
            'header:body_msg_type' 'PO'
            'header:msg_ver' '2.01'
          }
        }
        def var_v9 =  util.PO_Ext(string(BEG.BEG03.text()) , string(BEG.BEG04.text()))
        'ns0:ext_po_num' var_v9
        def var_v12 =  util.PO_Ext(var_v10 , var_v11)
        'ns0:po_num' var_v12
        'ns0:cust'{
          'datatype:PARTNER_EXT_REF_NUM' 'ECHOSTAR'
        }
        'ns0:csgn'{
          'datatype:PARTNER_EXT_REF_NUM' 'ECHOSTAR'
        }
        def pName =  current_Detail...PER[PER01 == 'IC'][0].PER02.text()
        def vEmail =  current_Detail...PER[PER05 == 'EM'][0].PER06.text()
        def vPhone =  current_Detail...PER[PER03 == 'TE'][0].PER04.text()
        'ns0:byr'{
          'datatype:PARTNER_NAME'{
            if( string_length(pName) !== 0 ) pName
            if( string_length(pName) == 0 ) 'N.A'
          }
          'datatype:PARTNER_EXT_REF_NUM'{
            if( string_length(pName) !== 0 ) pName
            if( string_length(pName) == 0 ) 'N.A'
          }
          'datatype:ADDR'{
            'datatype:CONTACT'{
              'datatype:CONTACT_TYPE' 'EM'
              'datatype:email' vEmail
            }
            'datatype:CONTACT'{
              'datatype:CONTACT_TYPE' 'TE'
              'datatype:CONTACT_NUM' vPhone
            }
          }
        }
        def pZZ =  current_Detail...REF[REF01 == 'ZZ'][0].REF02.text()
        'ns0:vdr'{
          'datatype:PARTNER_EXT_REF_NUM' pZZ
        }
        'ns0:po_attr'{
          'datatype:data_status_code' 'STATUS_ACTIVE'
          def var_v13 =  util.ConvertDatetimeFormat(string(BEG.BEG05.text()) , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'" , "" , "")
          def var_v14 =  (string(var_v13) != "")
          if( var_v14){
            def var_v15 =  string(BEG.BEG05.text())
            'datatype:crt_date'{
              def var_v16 =  util.ConvertDatetimeFormat(var_v15 , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'" , "" , "")
              'datatype:record_datetime' var_v16
            }
          }
        }
        'ns0:payment_method'{
          def var_v17 =  util.paymentmethod(string(FOB.FOB04.text()) , string(FOB.FOB05.text()))
          def var_v18 =  util.CodeConversionWithDefaultValue("EchoStar850FOB05-OLLPaymentMethod" , string(var_v17) , "C" ,tib:render-xml(CdeConversionData))
          'datatype:payment_method_code' var_v18
        }
        'ns0:ship_via'{
          'datatype:ship_via_code' 'S'
        }
        'ns0:por'{
          if( string(var_v19)=='true'){
            def var_v20 =  current_Detail.FOB.FOB03.text()
            'datatype:LOC_NAME' var_v20
          }
          if( string(var_v22)=='true'){
            def var_v23 =  current_Detail.FOB.FOB03.text()
            'datatype:COUNTRY_NAME' var_v23
          }
          if( string(var_v22)=='true'){
            def var_v24 =  current_Detail.FOB.FOB03.text()
            'datatype:cust_ref' var_v24
          }
        }
        def pST =  current_Detail...NameAndAddressLoop[N1.N101 == 'ST'][0].N4[0].N401.text()
        'ns0:fnd'{
          'datatype:LOC_NAME' pST
          'datatype:cust_ref' pST
        }
        'ns0:ship_window'{
          def PO1List =  current_Detail.PO1Loop.DTM[DTM01=='002']
          def PO109 =  util.ConvertYYYYMMDDtoXMLDateTime(PO1List[0].DTM02)
          if( PO109){
            'datatype:fnd_eta_date'{
              'datatype:record_datetime' PO109
            }
          }
        }
        'ns0:incoterm'{
          'datatype:intcoterm_code'('xsi:nil': 'true')
          def var_v27 =  util.paymentmethod_ext(var_v25 , var_v26)
          'datatype:intcoterm_name' var_v27
          def var_v28 =  util.paymentmethod_ext(var_v25 , var_v26)
          'datatype:intcoterm_ext_ref' var_v28
        }
        current_Detail.PO1Loop.each { current_PO1Loop ->
          def var_v29 =  (string(PO1.PO108.text()) == "BP")
          def var_v31 =  (string(PID.PID01.text()) == "F")
          def var_v33 =  ceiling(string(PO1.PO102.text()))
          def var_v36 =  (string(PO1.PO106.text()) == "VP")
          'ns0:po_item'{
            'datatype:item_num' current_PO1Loop.PO1.PO101.text()
            'datatype:sku'{
              if( string(var_v29)=='true'){
                def var_v30 =  current_PO1Loop.PO1.PO109.text()
                'datatype:STYLE_CODE' var_v30
              }
              if( var_v31){
                if( current_PO1Loop.PID.PID05){
                  'datatype:DESC' current_PO1Loop.PID.PID05.text()
                }
              }
            }
            'datatype:lowest_uom'{
              def var_v32 =  util.CodeConversionWithDefaultValue("EchoStar850PO103-OLLUOMcode" , string(PO1.PO103.text()) , "" ,tib:render-xml(CdeConversionData))
              'datatype:UOM_CODE' var_v32
              if( current_PO1Loop.PO1.PO103){
                'datatype:uom_ext_ref' current_PO1Loop.PO1.PO103.text()
              }
            }
            'datatype:lowest_uom_qty' var_v33
            'ns0:ship_windows'{
              current_PO1Loop.DTM.each { current_DTM ->
                def var_v34 =  (string(DTM01.text()) == "002")
                if( var_v34){
                  'datatype:fnd_eta_date'{
                    def var_v35 =  util.ConvertYYYYMMDDtoXMLDateTime(string(DTM02.text()))
                    'datatype:record_datetime' var_v35
                  }
                }
              }
            }
            'ns0:vdr_sku'{
              if( string(var_v36)=='true'){
                def var_v37 =  current_PO1Loop.PO1.PO107.text()
                'datatype:STYLE_CODE' var_v37
              }
            }
            def pZZ =  current_PO1Loop.'..'.REF[REF01 == 'ZZ'].REF02.text()
            'ns0:fnd'{
              'datatype:cust_ref' pZZ
            }
            current_PO1Loop.PO1.PO104.each { current_PO1_PO104 ->
              def var_v38 =  (string(..text()) != "")
              if( var_v38){
                'ns0:lowest_uom_cost'{
                  'datatype:cur_code'('xsi:nil': 'true')
                  'datatype:amt' current_PO1_PO104...text()
                }
              }
            }
            current_PO1Loop.PO1.PO104.each { current_PO1_PO104 ->
              def var_v39 =  string(..text())
              def var_v40 =  (var_v39 != "")
              if( var_v40){
                'ns0:lowest_uom_price'{
                  'datatype:cur_code'('xsi:nil': 'true')
                  'datatype:amt' current_PO1_PO104...text()
                }
              }
            }
            if( current_PO1Loop.PO1.PO110=='ON'){
              'ns0:customized_field'{
                'datatype:field_code' 'PO_DETAIL_GF_06'
                'datatype:field_value' current_PO1Loop.PO1.PO111
              }
            }
          }
        }
        def PO1List =  current_Detail.PO1Loop.PO1[PO110=='ON']
        def PO109 =  PO1List[0].PO111
        def REF02 =  current_Detail.REF[REF01=='P4'][0].REF02
        if( REF02!==''){
          'ns0:customized_field'{
            'datatype:field_code' 'PO_HEADER_GF_04'
            'datatype:field_value' REF02
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

