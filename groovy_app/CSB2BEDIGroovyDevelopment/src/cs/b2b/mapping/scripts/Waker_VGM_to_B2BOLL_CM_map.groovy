package cs.b2b.mapping.scripts

import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


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
	def ZVSHPFL1 = new XmlParser().parseText(inputXmlBody);

	def writer = new StringWriter()
	def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	
	
	/**
	 * Part IV: mapping script start from here
	 */

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()

	outXml.'cm_file_root' ('xmlns':'http://www.oocllogistics.com/cm',
		'xmlns:datatype':'http://www.oocllogistics.com/datatype',
		'xmlns:notelog':'http://www.oocllogistics.com/notelog',
		'xmlns:header':'http://www.oocllogistics.com/header',
		'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance')
	{
		'file_header' {
			'header:SENDER_ID' 'WACKER'
			'header:RECIPIENT_ID' 'PLLP'
			'header:MSG_CTRL_REF_NUM' currentSystemDt.format("yyyyMMdd")
			'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
			'header:MSG_REC_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
			'header:MSG_STATUS' 'N'
			'header:MSG_TYPE' 'VERMAS'
			'header:MSG_DEF_VER' '2.01'
		}
		'cm_msg_root' {
			'msg_header' {
				'header:SENDER_ID' 'WACKER'
				'header:RECIPIENT_ID' 'PLLP'
				'header:MSG_CTRL_REF_NUM' currentSystemDt.format("yyyyMMdd")
				'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
				'header:MSG_STATUS' 'N'
				'header:MSG_TYPE' 'VERMAS'
				'header:MSG_DEF_VER' '2.01'
				'header:data_header' {
					'header:to' 'PLLP'
					'header:from' 'WACKER'
					'header:time_stamp' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
					'header:header_msg_type' 'VERMAS'
					'header:body_msg_type' 'VERMAS'
					'header:msg_ver' '2.01'
				}
			}
			//cm_cntr
			
			'cm_cntr' {
				'cntr_seq' '1'
				def varE1EDT37_B = ZVSHPFL1.IDOC.E1EDT20.E1EDT37.find{it.VELTP.text() == 'B'}?.EXIDV?.text()
				'cntr_ref_num' varE1EDT37_B
				'cntr' {
					'datatype:CNTR_NUM' varE1EDT37_B
				}
				'cntr_measurement' {}
				def varE1EDT37_WM = ZVSHPFL1.IDOC.E1EDT20.E1EDT37.find{it.EXIDV.text() == '#WEIGHINGMETH#'}?.INHALT?.text()
				'vgm_method' varE1EDT37_WM
				//AM
				'partner_obj' {
					'datatype:type_code' 'AM'
					'datatype:PARTNER_NAME' 'Wacker Chemie AG'
					'datatype:ADDR' {
						'datatype:CONTACT' {
							'datatype:CONTACT_TYPE' 'PERSON'
							def varAMPIC_ZIP = ZVSHPFL1.IDOC.E1EDT20.E1ADRM4.find{it.PARTNER_Q.text() == 'ZP'}?.NAME1?.text()
							if (varAMPIC_ZIP!=null && varAMPIC_ZIP.indexOf("-")>0) {
								'datatype:first_name' varAMPIC_ZIP.substring(varAMPIC_ZIP.indexOf("-")+1).trim()
							} else {
								'datatype:first_name' varAMPIC_ZIP
							}
						}
					}
				}
				
				//SPC
				'partner_obj' {
					'datatype:type_code' 'SPC'
					'datatype:PARTNER_NAME' 'Wacker Chemie AG'
				}
				
				//WPC
				'partner_obj' {
					'datatype:type_code' 'WPC'
					'datatype:PARTNER_NAME' 'Wacker Chemie AG'
					'datatype:ADDR' {
						'datatype:CONTACT' {
							'datatype:CONTACT_TYPE' 'PERSON'
							def varAMPIC_ZIP = ZVSHPFL1.IDOC.E1EDT20.E1ADRM4.find{ it.PARTNER_Q.text() == 'ZP'}?.NAME1?.text()
							if (varAMPIC_ZIP!=null && varAMPIC_ZIP.indexOf("-")>0) {
								//output the string after  "-" from vAMPIC
								'datatype:first_name' varAMPIC_ZIP.substring(varAMPIC_ZIP.indexOf("-")+1).trim()
							} else {
								'datatype:first_name' varAMPIC_ZIP
							}
						}
					}
				}
				'carrier_bkg_num' ZVSHPFL1.IDOC.E1EDT20.E1EDK33.E1EDT01.VBELN[0]?.text()
			}
		}
	}

	return writer.toString();
}