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

public class CUS_CS2RCXML_CSRCXML_OOCLACI {
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
	
	public void generateBody(def currentBody, MarkupBuilder outXml) {
		
	}
	
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
		
		def CS2RCXML = new XmlSlurper().parseText(inputXmlBody)

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
		def CSRCXML = outXml.createNode('CUSTOMS_STATUS')
		def bizKeyRoot = bizKeyXml.createNode('root')

		//BeginWorkFlow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def outputBodyCount = 0
		def currentSystemDt = new Date()
		
		def msgFmtId = "XML"
		//def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, msgFmtId, conn)
		long ediIsaCtrlNumber = '1'
		long ediGroupCtrlNum = '1'

		
		
		CS2RCXML.Body.eachWithIndex { currentBody, currBodyIdx->
			outXml.'Status_Message' {
				'GeneralInfo' {
					'TransactionInfo' {
						'MessageSender' CS2RCXML.Header.SenderID.text()
						'MessageRecipient'CS2RCXML.Header.ReceiverID.text()
						'MessageReferenceNumber'
						'MessageIdentification'
						'CorrelationIdentifier'
						'DateCreated' currentSystemDt //TODO
						'TimeCreated' currentSystemDt
						'ReportDateCreated' CS2RCXML.Header.MsgDT.text() 
						'ReportTimeCreated' CS2RCXML.Header.MsgDT.text() 
						'BatchNumber' CS2RCXML.Header.InterchangeMessageID.text()
						'FileName'
						'Version'
					}
					'CSReferenceNumber' currentBody.GeneralInformation.UMR.text()
					'DataSource'
					'MessageType'
				}
				'StatusDetails' {
					'ManifestHeader' {
						'CarrierCode' currentBody.CarrierInformation.SCACCode.text()
						'ModeOfTransportationCode'
						'VesselCountryCode'
						'VesselName'
						'VoyageNumber'
						'ManifestSequenceNumber'
						'VesselCode'
						'ManifestTypeCode'
					}
					'Manifest' {
						'CarrierCode' currentBody.CarrierInformation.SCACCode.text()
						'CBCPort'
						'VesselName'
						'VoyageNumber'
						'ManifestSequenceNumber'
						'EstimatedDate'
					}
					'IssuerCode' {
						'IssuerCode' currentBody.GeneralInformation.Issuer.IssuerID.text()
					}
					'BillOfLadingStatusNotificationGrouping' {
						'BillOfLadingStatusNotification' {
							'BillOfLadingSequenceNumber' currentBody.StatusNotification.NotificationSeqence.text()
							'HouseBillCloseNumber' currentBody.ManifestInfo.BLRefNum.text()
							'DispositionCode'
							'Quantity'
							'EntryType'
							'EntryNumber'
							'ActionDateTime' currentBody.StatusNotification.StatusDT.GMT.text()
							'NegativeIndicator'
							'LineDeimiter'
							'ResendIndicator'
						}
						'BillOfLadingStatusNotificationContinuation' {
							'PortOfTransaction'
							'FIRMSCode'
							'USPortOfDestination'
							'ForeignDestination'
							'ContainerNumber'
							'ReferenceType'
							'ReferenceNumber'
						}
						'ReferenceIdentifier' {
							'ReferenceType' 'BLRefNum'
							'ReferenceNumber' currentBody.ManifestInfo.BLRefNum.text()
						}
						'ReferenceIdentifier' {
							'ReferenceType' 'StatusCode'
							'ReferenceNumber' currentBody.StatusNotification.StatusCode.text()
						}
						'ReferenceIdentifier' {
							'ReferenceType' 'StatusDescription'
							'ReferenceNumber' currentBody.StatusNotification.StatusDescription.text()
						}
						'StatusNotificationRemarks' {
							'Remarks'
						}
						'BillOfLadingContainer' {
							'ContainerNumber'
							'SealNumber1'
							'SealNumber2'
						}
					}
				}
			}
		}
		
		//End root node
		outXml.nodeCompleted(null,CSRCXML)
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
