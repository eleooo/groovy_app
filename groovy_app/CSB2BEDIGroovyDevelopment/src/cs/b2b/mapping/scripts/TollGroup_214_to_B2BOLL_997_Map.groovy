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
	//not use this part
	
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
	def TollGroup_214_1dot0_MsgRoot = new XmlParser().parseText(inputXmlBody);

	def writer = new StringWriter()
	def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	
	
	/**
	 * Part IV: mapping script start from here
	 */

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()

	long ediControlNumberISA = util.GetSequenceNextValWithDefault('B2BOLL_Std_997_ISA_ID', 10, conn);
	
	outXml.'ns0:B2BOLL_Std_997_Root' ('xmlns:ns0':'http://www.oocllogistics.com/TollGroup_214_1dot0')
	{
		'ISA' {
			'AuthorizationInfoQualifier' '00'
			'SecurityInfoQualifier' '00'
			'InterchangeIDQualifier' TollGroup_214_1dot0_MsgRoot.ISA.InterchangeIDQualifier2.text()
			'InterchangeSenderID' TollGroup_214_1dot0_MsgRoot.ISA.InterchangeReceiverID.text()
			'InterchangeIDQualifier2' TollGroup_214_1dot0_MsgRoot.ISA.InterchangeIDQualifier.text()
			'InterchangeReceiverID' TollGroup_214_1dot0_MsgRoot.ISA.InterchangeSenderID.text()
			'InterchangeDate' currentSystemDt.format('yyMMdd')
			'InterchangeTime' currentSystemDt.format('HHmm')
			'RepetitionSeparatior' TollGroup_214_1dot0_MsgRoot.ISA.RepetitionSeparatior.text()
			'VersionNumber' TollGroup_214_1dot0_MsgRoot.ISA.VersionNumber.text()
			'ControlNumber' util.formatString(ediControlNumberISA, '%09d')
			'AcknowledgmentRequest' '0'
			'UsageIndicator' TollGroup_214_1dot0_MsgRoot.ISA.UsageIndicator.text()
			'ElementSeparator' TollGroup_214_1dot0_MsgRoot.ISA.ElementSeparator.text() //&gt;  >
		}
		'GS' {
			'FunctionalIdentifierCode' 'QM'
			'ApplicationSenderCode' TollGroup_214_1dot0_MsgRoot.GS.ApplicationReceiverCode.text()
			'ApplicationReceiverCode' TollGroup_214_1dot0_MsgRoot.GS.ApplicationSenderCode.text()
			'Date' currentSystemDt.format('yyyyMMdd')
			'Time' currentSystemDt.format('HHmm')
			'ControlNumber' ediControlNumberISA
			'AgencyCode' TollGroup_214_1dot0_MsgRoot.GS.AgencyCode.text()
			'IdentitierCode' TollGroup_214_1dot0_MsgRoot.GS.IdentitierCode.text()
		}
		def varCount = TollGroup_214_1dot0_MsgRoot.TransactionLoop.size()
		TollGroup_214_1dot0_MsgRoot.TransactionLoop.each { currentTxn ->
			
			long ediControlNumberSTSE = util.GetSequenceNextValWithDefault('B2BOLL_Std_997_STSE_ID', 10, conn);
			
			'MessageBody' {
				'ST' {
					'ST01' '997'
					'ST02' util.formatString(ediControlNumberSTSE, '%04d')
				}
				'AK1' {
					'AK101' TollGroup_214_1dot0_MsgRoot.GS.FunctionalIdentifierCode.text()
					'AK102' TollGroup_214_1dot0_MsgRoot.GS.ControlNumber.text()
				}
				'AK2Loop1' {
					'AK2' {
						'AK201' currentTxn.ST.IdentifierCode.text()
						'AK202' currentTxn.ST.ControlNumber.text()
					}
					'AK5' {
						'AK501' 'A'
					}
				}
				'AK9' {
					'AK901' 'A'
					'AK902' varCount
					'AK903' varCount
					'AK904' varCount
				}
				'SE' {
					'SE01' ''   //Beluga LBC will help to count it, pls refer definition: <Field Name="SE01" AutoFillEDISegmentCount="true" AutoFillEDISegmentCountStartTag="ST" AutoFillEDISegmentCountEndTag="SE"/>
					'SE02' util.formatString(ediControlNumberSTSE, '%04d')
				}
			}
		}
		'GE' {
			'GE01' '1'
			'GE02' ediControlNumberISA
		}
		'IEA' {
			'IEA01' '1'
			'IEA02' util.formatString(ediControlNumberISA, '%09d')
		}
	}

	return writer.toString();
}
