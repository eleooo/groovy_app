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
	
outXml.'iv:ivMessageRoot'('xmlns:datatype':'http://www.oocllogistics.com/datatype', 'xmlns:header':'http://www.oocllogistics.com/header', 'xmlns:ns0':'http://www.oocllogistics.com/shipstatus', 'xmlns:pfx8':'PLLP_eCI_XLS', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance', 'xmlns:xsl':'http://www.w3.org/1999/XSL/Transform')'xmlns:iv':'http://www.oocllogistics.com/customer_iv_151', 'xsi:schemaLocation':'http://www.oocllogistics.com/customer_iv_151 oll_customer_iv_151.xsd'{
    'iv:TransactionHeader'{
      'iv:SenderID' 'OOCLLOGISTICS'
      'iv:RecipientID' 'HOBBYLOBBY'
      'iv:MessageType' 'IV'
      def sCurrentDT =  current_dateTime
      def sControlNum =  MappingLibrary.ConvertXMLDateTimetoYYYYMMDD(substring(current_dateTime,1,19))
      'iv:ControlNumber' sControlNum
      'iv:TransactionDateTime' sCurrentDT
      'iv:MessageInstruction' 'NEW'
    }
    'xsl:for-each-group''group-by':'m_inv_num', select:'Record' { current_Record ->
      def sCurrentInvNum =  current_Record.m_inv_num
      'iv:Invoice'{
        'iv:InvoiceNumber' current_Record.m_inv_num
        'iv:InvoiceIssueDate' current_Record.m_issue_date
        'iv:Issuer'{
          'iv:Name' current_Record.sp_prty_name
          'iv:AddressLine' current_Record.sp_addr_1
          'iv:AddressLine' current_Record.sp_addr_2
          'iv:AddressLine' current_Record.sp_addr_3
        }
        'iv:FreightPaymentMethod' current_Record.m_payment_term
        'iv:Consignee'{
          'iv:Name' current_Record.cp_prty_name
          'iv:AddressLine' current_Record.cp_addr_1
          'iv:AddressLine' current_Record.cp_addr_2
          'iv:AddressLine' current_Record.cp_addr_3
          'iv:AddressLine' current_Record.cp_addr_4
        }
        'iv:Shipper'{
          'iv:Name' current_Record.sp_prty_name
          'iv:AddressLine' current_Record.sp_addr_1
          'iv:AddressLine' current_Record.sp_addr_2
          'iv:AddressLine' current_Record.sp_addr_3
          'iv:AddressLine' current_Record.sp_addr_4
        }
        'iv:NotifyingParty'{
          'iv:Name' current_Record.np_prty_name
          'iv:AddressLine' current_Record.np_addr_1
          'iv:AddressLine' current_Record.np_addr_2
          'iv:AddressLine' current_Record.np_addr_3
          'iv:AddressLine' current_Record.np_addr_4
        }
        'iv:ForAccountAndRiskofMessers'{
          'iv:Name' current_Record.mp_prty_name
          'iv:AddressLine' current_Record.mp_addr_1
          'iv:AddressLine' current_Record.mp_addr_2
          'iv:AddressLine' current_Record.mp_addr_3
          'iv:AddressLine' current_Record.mp_addr_4
        }
        'iv:Transportation'{
          'iv:Leg'{
            'iv:Sequence' '1'
            'iv:ShipViaCode' 'S'
            'iv:VesselName' current_Record.m_vsl_name
            'iv:VoyageNumber' current_Record.m_voy_code
            'iv:LoadingLocation'{
              'iv:Name' current_Record.pol_loc_name
              'iv:ETD' current_Record.m_pol_etd
            }
          }
          'iv:FinalDestination'{
            'iv:Name' current_Record.fnd_loc_name
          }
        }
        current_Record.'..'.Record[m_inv_num==sCurrentInvNum].each { current_Record ->
          'xsl:sort'select:'h_disp_seq'
          'iv:ItemDetails'{
            'iv:PurchaseOrderNumber' current_Record.i_po_num
            'iv:Style' current_Record.i_buyer_item_num
            'iv:Color' current_Record.i_color
            'iv:Size' current_Record.i_item_size
            'iv:ItemDescription' current_Record.i_descr
            'iv:NumberOfPacks'('UOMCode': current_Record.ctn_abbr,'UOMCode2': current_Record.ctn_abbr2) current_Record.i_pack_qty
            'iv:NumberOfUnits'UOMCode:'PCS' current_Record.i_qty
            'iv:UnitPrice'{
              'iv:Amount' current_Record.i_unit_price
              'iv:CurrencyCode' 'USD'
            }
            'iv:SubtotalPrice'{
              'iv:Amount' current_Record.h_ttl_amount
              'iv:CurrencyCode' 'USD'
            }
            'iv:MarksNumbersAndGoodsDescription'{
              'iv:MarksAndNumbersText' current_Record.i_marks_no
              'iv:CargoDescriptions' current_Record.i_descr
            }
          }
        }
        'iv:Remarks' current_Record.m_remarks
      }
    }
  }
	//mapping logic end
		
//	def outxml = XmlUtil.serialize(writer.toString());
//	return outxml;
	
	return writer.toString();
	
}

