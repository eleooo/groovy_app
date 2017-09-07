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
	
def var_v1 =  MappingLibrary.StringConcat(string(Header.ISA.ISA09.text()) , string(Header.ISA.ISA10.text()))
outXml.'ns0:po_file_root'('xmlns:datatype':'http://www.oocllogistics.com/datatype', 'xmlns:header':'http://www.oocllogistics.com/header', 'xmlns:notelog':'http://www.oocllogistics.com/notelog', 'xmlns:ns0':'http://www.oocllogistics.com/po', 'xmlns:pfx8':'http://www.oocllogistics.com/AgeGroup_850_4dot10', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance', 'xmlns:xsl':'http://www.w3.org/1999/XSL/Transform'){
    'ns0:file_header'{
      'header:SENDER_ID' 'AGEGROUP'
      'header:RECIPIENT_ID' 'POP'
      'header:MSG_CTRL_REF_NUM' inXml.Header.ISA.ISA13.text()
      def var_v2 =  MappingLibrary.ConvertDatetimeFormat(string(var_v1) , "yyMMddHHmm" , "yyyy-MM-dd'T'HH:mm:ss'.'S" , "" , "exception")
      'header:MSG_SENT_DATETIME' var_v2
      def var_v3 =  substring(current_dateTime,1,19)
      def var_v4 =  MappingLibrary.ConvertDatetimeFormat(string(var_v3) , "" , "yyyy-MM-dd'T00:00:00.0'" , "" , "exception")
      'header:MSG_REC_DATETIME' var_v4
      'header:MSG_STATUS' 'N'
      'header:MSG_TYPE' 'PO'
      'header:MSG_DEF_VER' '2.01'
    }
    inXml.Detail.each { current_Detail ->
      def var_v10 =  substring(current_dateTime,1,19)
      def var_v19 =  string(BEG.BEG01.text())
      'ns0:po_msg_root'{
        'ns0:msg_header'{
          'header:SENDER_ID' 'AGEGROUP'
          'header:RECIPIENT_ID' 'POP'
          def var_v5 =  substring(current_dateTime,1,19)
          def var_v6 =  MappingLibrary.ConvertXMLDateTimetoYYYYMMDD(string(var_v5))
          'header:MSG_CTRL_REF_NUM' var_v6
          def var_v7 =  substring(current_dateTime,1,19)
          def var_v8 =  MappingLibrary.ConvertDatetimeFormat(string(var_v7) , "" , "yyyy-MM-dd'T00:00:00.0'" , "" , "exception")
          'header:MSG_SENT_DATETIME' var_v8
          def var_v9 =  getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID,"AgeGroupBEG01-OLLXMLMsgStatus" , string(BEG.BEG01.text()) , "NEW" ,tib:render-xml(CdeConversionData))
          'header:MSG_STATUS' var_v9
          'header:MSG_TYPE' 'PO'
          'header:MSG_DEF_VER' '2.01'
          'header:data_header'{
            'header:to' 'POP'
            'header:from' 'AGEGROUP'
            'header:time_stamp' var_v10
            'header:header_msg_type' 'PO'
            'header:body_msg_type' 'PO'
            'header:msg_ver' '2.01'
          }
        }
        'ns0:ext_po_num' current_Detail.BEG.BEG03.text()
        'ns0:po_num' current_Detail.BEG.BEG03.text()
        current_Detail.NameAndAddressLoop.each { current_NameAndAddressLoop ->
          def var_v11 =  ("BS" == string(N1.N101.text()))
          if( var_v11){
            'ns0:cust'{
              'datatype:PARTNER_NAME' current_NameAndAddressLoop.N1.N102.text()
              if( current_NameAndAddressLoop.N1.N104){
                'datatype:PARTNER_EXT_REF_NUM' current_NameAndAddressLoop.N1.N104.text()
              }
              'datatype:ADDR'{
                if( current_NameAndAddressLoop.N4.N404){
                  'datatype:country_name' current_NameAndAddressLoop.N4.N404.text()
                }
                if( current_NameAndAddressLoop.N4.N402){
                  'datatype:state_name' current_NameAndAddressLoop.N4.N402.text()
                }
                'datatype:city'{
                  if( current_NameAndAddressLoop.N4.N401){
                    'datatype:LOC_NAME' current_NameAndAddressLoop.N4.N401.text()
                  }
                }
                if( current_NameAndAddressLoop.N4.N403){
                  'datatype:postal_code' current_NameAndAddressLoop.N4.N403.text()
                }
                'datatype:ADDR_LINE' current_NameAndAddressLoop.N3.N301.text()
                if( current_NameAndAddressLoop.N3.N302){
                  'datatype:ADDR_LINE' current_NameAndAddressLoop.N3.N302.text()
                }
              }
            }
          }
        }
        current_Detail.NameAndAddressLoop.each { current_NameAndAddressLoop ->
          def var_v12 =  string(N1.N101.text())
          def var_v13 =  ("BS" == var_v12)
          if( var_v13){
            'ns0:csgn'{
              'datatype:PARTNER_NAME' current_NameAndAddressLoop.N1.N102.text()
              if( current_NameAndAddressLoop.N1.N104){
                'datatype:PARTNER_EXT_REF_NUM' current_NameAndAddressLoop.N1.N104.text()
              }
              'datatype:ADDR'{
                if( current_NameAndAddressLoop.N4.N404){
                  'datatype:country_code' current_NameAndAddressLoop.N4.N404.text()
                }
                if( current_NameAndAddressLoop.N4.N404){
                  'datatype:country_name' current_NameAndAddressLoop.N4.N404.text()
                }
                if( current_NameAndAddressLoop.N4.N402){
                  'datatype:state_code' current_NameAndAddressLoop.N4.N402.text()
                }
                if( current_NameAndAddressLoop.N4.N402){
                  'datatype:state_name' current_NameAndAddressLoop.N4.N402.text()
                }
                'datatype:city'{
                  if( current_NameAndAddressLoop.N4.N401){
                    'datatype:LOC_NAME' current_NameAndAddressLoop.N4.N401.text()
                  }
                }
                if( current_NameAndAddressLoop.N4.N403){
                  'datatype:postal_code' current_NameAndAddressLoop.N4.N403.text()
                }
                'datatype:ADDR_LINE' current_NameAndAddressLoop.N3.N301.text()
                if( current_NameAndAddressLoop.N3.N302){
                  'datatype:ADDR_LINE' current_NameAndAddressLoop.N3.N302.text()
                }
              }
              current_NameAndAddressLoop.'..'.REF.each { current_REF ->
                def var_v14 =  (string(REF01.text()) == "DP")
                if( var_v14){
                  if( current_REF.REF02){
                    'ns0:dept' current_REF.REF02.text()
                  }
                }
              }
            }
          }
        }
        current_Detail.NameAndAddressLoop.each { current_NameAndAddressLoop ->
          def var_v15 =  string(N1.N101.text())
          def var_v16 =  (var_v15 == "VN")
          if( var_v16){
            'ns0:vdr'{
              'datatype:PARTNER_NAME' current_NameAndAddressLoop.N1.N102.text()
              if( current_NameAndAddressLoop.N1.N104){
                'datatype:PARTNER_EXT_REF_NUM' current_NameAndAddressLoop.N1.N104.text()
              }
              'datatype:ADDR'{
                if( current_NameAndAddressLoop.N4.N404){
                  'datatype:country_code' current_NameAndAddressLoop.N4.N404.text()
                }
                if( current_NameAndAddressLoop.N4.N404){
                  'datatype:country_name' current_NameAndAddressLoop.N4.N404.text()
                }
                if( current_NameAndAddressLoop.N4.N402){
                  'datatype:state_name' current_NameAndAddressLoop.N4.N402.text()
                }
                'datatype:city'{
                  if( current_NameAndAddressLoop.N4.N401){
                    'datatype:LOC_NAME' current_NameAndAddressLoop.N4.N401.text()
                  }
                }
                if( current_NameAndAddressLoop.N4.N403){
                  'datatype:postal_code' current_NameAndAddressLoop.N4.N403.text()
                }
                'datatype:ADDR_LINE' current_NameAndAddressLoop.N3.N301.text()
                if( current_NameAndAddressLoop.N3.N302){
                  'datatype:ADDR_LINE' current_NameAndAddressLoop.N3.N302.text()
                }
              }
            }
          }
        }
        current_Detail.NameAndAddressLoop.each { current_NameAndAddressLoop ->
          def var_v17 =  string(N1.N101.text())
          def var_v18 =  (var_v17 == "BT")
          if( var_v18){
            'ns0:billto'{
              'datatype:PARTNER_NAME' current_NameAndAddressLoop.N1.N102.text()
              if( current_NameAndAddressLoop.N1.N104){
                'datatype:PARTNER_EXT_REF_NUM' current_NameAndAddressLoop.N1.N104.text()
              }
            }
          }
        }
        'ns0:po_attr'{
          def var_v20 =  getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID,"AgeGroupBEG01-OLLDataStatus" , var_v19 , "STATUS_ACTIVE" ,tib:render-xml(CdeConversionData))
          'datatype:data_status_code' var_v20
          'datatype:crt_date'{
            def var_v21 =  substring(current_dateTime,1,19)
            'datatype:record_datetime' var_v21
          }
          'datatype:issue_date'{
            def var_v22 =  MappingLibrary.ConvertYYYYMMDDtoXMLDateTime(string(BEG.BEG05.text()))
            'datatype:record_datetime' var_v22
          }
        }
        'ns0:payment_method'{
          def var_v23 =  MappingLibrary.fobvalue(string(FOB.FOB01.text()))
          'datatype:payment_method_code' var_v23
          def var_v24 =  current_Detail.string(FOB.FOB01.@xsi:nil) == 'true'
          if( string(var_v24)=='true'){
            'datatype:ext_ref'('xsi:nil': 'true')
          }
          if( string(var_v24)=='false'){
            'datatype:ext_ref' current_Detail.FOB.FOB01.text()
          }
        }
        'ns0:ship_via'{
          def var_v25 =  MappingLibrary.TD504(string(TD5.TD504.text()))
          'datatype:ship_via_code' var_v25
          if( current_Detail.TD5.TD504){
            'datatype:ship_via_ext_ref' current_Detail.TD5.TD504.text()
          }
        }
        current_Detail.REF.each { current_REF ->
          def var_v26 =  string(REF01.text())
          def var_v27 =  (var_v26 == "4B")
          if( var_v27){
            'ns0:por'{
              if( current_REF.REF02){
                'datatype:LOC_NAME' current_REF.REF02.text()
              }
              if( current_REF.'..'.FOB.FOB07){
                def var_v28 =  current_REF.string('..'.FOB.FOB07.@xsi:nil) == 'true'
                if( string(var_v28)=='true'){
                  'datatype:LOC_NAME'('xsi:nil': 'true')
                }
                if( string(var_v28)=='false'){
                  'datatype:LOC_NAME' current_REF.'..'.FOB.FOB07.text()
                }
              }
              if( current_REF.REF02){
                'datatype:cust_ref' current_REF.REF02.text()
              }
              if( current_REF.'..'.FOB.FOB07){
                def var_v29 =  current_REF.string('..'.FOB.FOB07.@xsi:nil) == 'true'
                if( string(var_v29)=='true'){
                  'datatype:cust_ref'('xsi:nil': 'true')
                }
                if( string(var_v29)=='false'){
                  'datatype:cust_ref' current_REF.'..'.FOB.FOB07.text()
                }
              }
            }
          }
        }
        current_Detail.NameAndAddressLoop.each { current_NameAndAddressLoop ->
          def var_v30 =  string(N1.N101.text())
          def var_v31 =  ("BS" == var_v30)
          if( var_v31){
            'ns0:fnd'{
              if( current_NameAndAddressLoop.N4.N401){
                'datatype:LOC_NAME' current_NameAndAddressLoop.N4.N401.text()
              }
              if( current_NameAndAddressLoop.N4.N401){
                'datatype:cust_ref' current_NameAndAddressLoop.N4.N401.text()
              }
            }
          }
        }
        current_Detail.PO1Loop.each { current_PO1Loop ->
          def var_v32 =  (string(PO1.PO102.text()) != "")
          def var_v48 =  (string('..'.FOB.FOB01.text()) == "CC")
          def var_v50 =  string('..'.FOB.FOB01.text())
          def var_v51 =  (var_v50 == "CC")
          def var_v53 =  string(TD5.TD504.text())
          'ns0:po_item'{
            'datatype:sku'{
              if( current_PO1Loop.PO1.PO107){
                'datatype:STYLE_CODE' current_PO1Loop.PO1.PO107.text()
              }
              if( current_PO1Loop.PO1.PO117){
                'datatype:COLOR_CODE' current_PO1Loop.PO1.PO117.text()
              }
              if( current_PO1Loop.PO1.PO119){
                'datatype:SIZE_CODE' current_PO1Loop.PO1.PO119.text()
              }
              if( current_PO1Loop.PO1.PO109){
                'datatype:DESC' current_PO1Loop.PO1.PO109.text()
              }
            }
            'datatype:lowest_uom'{
              if( current_PO1Loop.PO1.PO103){
                'datatype:UOM_CODE' current_PO1Loop.PO1.PO103.text()
              }
              if( current_PO1Loop.PO1.PO103){
                'datatype:uom_ext_ref' current_PO1Loop.PO1.PO103.text()
              }
            }
            if( string(var_v32)=='true'){
              def var_v33 =  current_PO1Loop.PO1.PO102.text()
              'datatype:lowest_uom_qty' var_v33
            }
            'ns0:ship_windows'{
              current_PO1Loop.DTM.each { current_DTM ->
                def var_v34 =  (string(DTM01.text()) == "037")
                if( var_v34){
                  'datatype:earliest_pol_etd_date'{
                    def var_v35 =  MappingLibrary.ConvertDatetimeFormat(string(DTM02.text()) , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00.0'" , "" , "exception")
                    'datatype:record_datetime' var_v35
                  }
                }
              }
              current_PO1Loop.DTM.each { current_DTM ->
                def var_v36 =  string(DTM01.text())
                def var_v37 =  (var_v36 == "037")
                if( var_v37){
                  def var_v38 =  string(DTM02.text())
                  'datatype:target_pol_etd_date'{
                    def var_v39 =  MappingLibrary.ConvertDatetimeFormat(var_v38 , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00.0'" , "" , "exception")
                    'datatype:record_datetime' var_v39
                  }
                }
              }
              current_PO1Loop.DTM.each { current_DTM ->
                def var_v40 =  string(DTM01.text())
                def var_v41 =  (var_v40 == "038")
                if( var_v41){
                  def var_v42 =  string(DTM02.text())
                  'datatype:latest_pol_etd_date'{
                    def var_v43 =  MappingLibrary.ConvertDatetimeFormat(var_v42 , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00.0'" , "" , "exception")
                    'datatype:record_datetime' var_v43
                  }
                }
              }
              current_PO1Loop.DTM.each { current_DTM ->
                def var_v44 =  string(DTM01.text())
                def var_v45 =  (var_v44 == "996")
                if( var_v45){
                  def var_v46 =  string(DTM02.text())
                  'datatype:fnd_eta_date'{
                    def var_v47 =  MappingLibrary.ConvertDatetimeFormat(var_v46 , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00.0'" , "" , "exception")
                    'datatype:record_datetime' var_v47
                  }
                }
              }
            }
            'ns0:origin'{
              if( string(var_v48)=='true'){
                def var_v49 =  current_PO1Loop.'..'.FOB.FOB07.text()
                'datatype:LOC_NAME' var_v49
              }
              if( string(var_v51)=='true'){
                def var_v52 =  current_PO1Loop.'..'.FOB.FOB07.text()
                'datatype:cust_ref' var_v52
              }
            }
            'ns0:ship_via'{
              def var_v54 =  MappingLibrary.CodeConversionWithOriginalValue("AGEGROUPShipVia-OLLShipVia" , var_v53 ,tib:render-xml(CdeConversionData))
              'datatype:ship_via_code' var_v54
              if( current_PO1Loop.TD5.TD504){
                'datatype:ship_via_ext_ref' current_PO1Loop.TD5.TD504.text()
              }
            }
            'ns0:lowest_uom_cost'{
              'datatype:cur_code'('xsi:nil': 'true')
              if( current_PO1Loop.PO1.PO104){
                'datatype:amt' current_PO1Loop.PO1.PO104.text()
              }
            }
            if( current_PO1Loop.PO1.PO111){
              'ns0:hts_ref' current_PO1Loop.PO1.PO111.text()
            }
            if( current_PO1Loop.SLN.SLN10){
              'ns0:upc_ref' current_PO1Loop.SLN.SLN10.text()
            }
            if( current_PO1Loop.PO1.PO101){
              'ns0:event_ref' current_PO1Loop.PO1.PO101.text()
            }
            def paramLineNum =  string(PO1.PO101.text())
            def paramColorCode =  string(PO1.PO121.text())
            def paramGroupDesc =  string(PO1.PO115.text())
            def paramGroupCode =  string(PO1.PO113.text())
            'ns0:customized_field'{
              'datatype:field_code' 'PO_DETAIL_GF_02'
              'datatype:field_value' paramGroupDesc
            }
            'ns0:customized_field'{
              'datatype:field_code' 'PO_DETAIL_GF_04'
              'datatype:field_value' paramColorCode
            }
            'ns0:customized_field'{
              'datatype:field_code' 'PO_DETAIL_GF_03'
              'datatype:field_value' paramLineNum
            }
            def N1VN =  current_PO1Loop.'..'.NameAndAddressLoop[N1.N101=='VN']
            def vn301 =  N1VN[0].N3.N301.text()
            def vn302 =  N1VN[0].N3.N302.text()
            def vn404 =  N1VN[0].N4.N404.text()
            if( string_length(vn404)>1){
              'ns0:loc_obj'{
                'datatype:LOC_TYPE' 'COUNTRY_OF_ORIGIN'
                'datatype:LOC_NAME' vn404
                'datatype:COUNTRY_NAME' vn404
                'datatype:cust_ref' vn404
              }
            }
          }
        }
        def sOriginalPONum =  string(BEG.BEG03.text())
        'ns0:customized_field'{
          'datatype:field_code' 'PO_HEADER_GF_01'
          'datatype:field_value' sOriginalPONum
        }
      }
    }
  }
	//mapping logic end
		
//	def outxml = XmlUtil.serialize(writer.toString());
//	return outxml;
	
	return writer.toString();
	
}

