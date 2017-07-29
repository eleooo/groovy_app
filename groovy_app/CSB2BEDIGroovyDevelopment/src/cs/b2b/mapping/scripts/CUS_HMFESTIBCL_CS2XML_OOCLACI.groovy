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
import java.util.List
import java.util.Map;
import java.util.TimeZone
import java.lang.String
import java.lang.Integer

public class CUS_HMFESTIBCL_CS2XML_OOCLACI {
	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	
	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	Connection conn = null

	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = 'HHmm'

	def currentSystemDt = null
	

	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		//inputXmlBody = util.removeBOM(inputXmlBody)

		/**
		 * Part II: get mapping runtime parameters
		 */
		
		this.conn = conn
		appSessionId = util.getRuntimeParameter("B2BSessionID", runtimeParams);
		sourceFileName = util.getRuntimeParameter("B2B_OriginalSourceFileName", runtimeParams);
		//pmt info
		TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
		MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
		DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
		MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);
		
		/**
		 * Part III: read xml and prepare output xml
		 */
		
		def HBLC = new XmlSlurper().parseText(inputXmlBody)

		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		
		def bizKeywriter = new StringWriter()
		def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeywriter), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		
		/**
		 * Part IV: mapping script start from here
		 */

		//create root node
		//def CS2XML = outXml.createNode('n1:HouseBillClose')('xmlns:n1' : 'http://www.example.com')
		def bizKeyRoot = bizKeyXml.createNode('root')

		//BeginWorkFlow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def outputBodyCount = 0
		def currentSystemDt = new Date()
		
		def msgFmtId = "XML"
		//def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, msgFmtId, conn)
		long ediIsaCtrlNumber = '1'
		long ediGroupCtrlNum = '1'

		outXml.'HouseBillClose' ('xmlns:cs':'http://www.cargosmart.com/common', 'xmlns':'http://www.cargosmart.com/HouseBillClose') {
			'Header' {
				'cs:ControlNumber' HBLC.InterchangeControlHeader.DateTime.text() //currentSystemDt
				/*
				'cs:MsgDT'	{
					'cs:GMT' currentSystemDt
					/*
					'LocDT' {
						'TimeZone'
						'CSTimeZone'
					}
					
				}
				*/
				'cs:MsgDirection' 'I'
				'cs:MsgType' 'HMFESTIBCL'
				'cs:SenderID' TP_ID
				'cs:ReceiverID' 'CSACI'
				'cs:Action' 'NEW'
				'cs:Version' '1.0'
				'cs:InterchangeMessageID' MSG_REQ_ID
				'cs:FileName'	HBLC.HouseBillClose.GeneralInformation.TransactionInformation.FileName.text()
				'cs:DataSource' 'B2B'
				'cs:TargetCustomCountryCode' 'CA'
			}
		
			'Body' {
					'TransactionInformation' {
						'cs:MessageID'
						'cs:GroupControlNumber' HBLC.InterchangeControlHeader.DateTime.text() //currentSystemDt
						'cs:InterchangeTransactionID' MSG_REQ_ID
					}
					'GeneralInformation' {
						'UMR' MSG_REQ_ID
						'ActionType' HBLC.HouseBillClose.GeneralInformation.ActionType.text()
						
						if (HBLC.ouseBillClose.GeneralInformation.AmendmentReasonCode.text()!="") {
							'AmendmentReasonCode' HBLC.ouseBillClose.GeneralInformation.AmendmentReasonCode.text()
						}
							
						'Version' HBLC.HouseBillClose.GeneralInformation.Version.text()
						'Issuer' {
							if (HBLC.HouseBillClose.GeneralInformation.Issuer.IssurID.text()!="") {
								'IssuerID' HBLC.HouseBillClose.GeneralInformation.Issuer.IssurID.text()
							}
								
							if (HBLC.HouseBillClose.GeneralInformation.Issuer.IssuerName.text()!="") {
								'IssuerName' HBLC.HouseBillClose.GeneralInformation.Issuer.IssuerName.text()
							}
							
							if (HBLC.HouseBillClose.GeneralInformation.Issuer.IssuerEmail.text()!="") {
								'n0:IssuerEmail' HBLC.HouseBillClose.GeneralInformation.Issuer.IssuerEmail.text()
							}
	
							'SCAC' HBLC.HouseBillClose.GeneralInformation.Issuer.SCAC.text()
						}
					}
					'ManifestInfo' {
						'PreviousCargoControlNumber' HBLC.HouseBillClose.ManifestInfo.PreviousCargoControlNumber.text()
						'FreightForwarderCode'	HBLC.HouseBillClose.ManifestInfo.FreightForwarderCode.text()
						
						HBLC.HouseBillClose.ManifestInfo.CargoControlNumber.eachWithIndex {curCTLNum, curIdx->
							'CargoControlNumber' curCTLNum
						}
						
						HBLC.HouseBillClose.ManifestInfo.ExternalReference.eachWithIndex { currExtRef, currRefIdx ->
							'ExternalReference' {
								'CSReferenceType' currExtRef.CSReferenceType.text()
								'ReferenceNumber' currExtRef.ReferenceNumber.text()
								'ReferenceDescription' currExtRef.ReferenceDescription.text()
						}
					}
				}
			}
		}
				
		//End root node
		//outXml.nodeCompleted(null,CS2XML)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		
		//promote bizkey to session
		//cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizKeywriter?.toString())
		
		
		String result = "";
		/*
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = writer.toString();
		}
		*/
		result = writer.toString();

		writer.close();
		bizKeywriter.close()

		return result
	}
	
}
