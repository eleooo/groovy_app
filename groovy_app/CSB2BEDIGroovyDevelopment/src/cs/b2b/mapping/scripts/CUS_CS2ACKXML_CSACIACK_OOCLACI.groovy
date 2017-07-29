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

public class CUS_CS2ACKXML_CSACIACK_OOCLACI {
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
		
		def CS2ACKXML = new XmlSlurper().parseText(inputXmlBody)

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
		def CSACIACK = outXml.createNode('CustomsResponses')
		def bizKeyRoot = bizKeyXml.createNode('root')

		//BeginWorkFlow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def outputBodyCount = 0
		def currentSystemDt = new Date()
		
		def msgFmtId = "XML"
		//def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, msgFmtId, conn)
		long ediIsaCtrlNumber = '1'
		long ediGroupCtrlNum = '1'

		
		
		CS2ACKXML.Body.eachWithIndex { currentBody, currBodyIdx->
			outXml.'Response' {
				'Header' {
					'OrganizationID' CS2ACKXML.Header.ReceiverID.text()
					'TestingInd' 'F' 
					'EdiMessageID' CS2ACKXML.Header.InterchangeMessageID.text()
					'EdiCustomsID'  CS2ACKXML.Header.SenderID.text()
					'IssuerScac' currentBody.GeneralInformation.Issuer.SCAC.text()
					'FileCreationDatetime' {
						'dateStr' currentSystemDt //TODO time format//
					}
					'EdiPartnerName' 
					'EdiPartnerDatetime' {
						'dateStr' CS2ACKXML.Header.MsgDT.GMT.text()
					}
					currentBody.ManifestInfo.ExternalReference.eachWithIndex  { currentRef, currRefIdx ->
						'Reference'{
							'Qualifier' currentRef.CSReferenceType.text()
							'ReferenceNum' currentRef.ReferenceNumber.text()
							'VersionNum' 
						}
					}
					'MessageErrorCode'
					'MessageFunction'
					'CustomsControlNum'
					
					//OverallStatusCode for message
					if (currentBody.Report.TotalBillsAccepted.text()>0 && currentBody.Report.TotalBills.text() == currentBody.Report.TotalBillsAccepted.text()) {
						'OverallStatusCode' 'AC'
					} else if (currentBody.Report.TotalBillsRejected.text() >0 && currentBody.Report.TotalBills.text() == currentBody.Report.TotalBillsRejected.text()) {
						'OverallStatusCode' 'RE'
					} else 'OverallStatusCode' 'PR' 
					
					'MiscInfo' {
						'Qualifier' 'UMR'
						'Value' currentBody.GeneralInformation.UMR.text()
					}
				}
				'General' {
					'Counts' {
						'Qualifier'
						'Count'
					}
					'Datetimes' {
						'Qualifier'
						'Date' {
							'dateStr' currentBody.Report.TransmissionDT.text()
						}
					}
					'Refs' {
						'Qualifier'
						'Value'
					}
					'Statuses' {
						'Qualifier'
						'Code'
					}
					currentBody.Error.eachWithIndex { currentErr, curErrIdx ->
						'Errors' {
							'SeqNum' curErrIdx
							'Location'
							'Info' {
								'SeqNum'
								'ID' currentErr.EntityNumber.text()
								'Type' 'E'
								'Description' currentErr.Description.text()
								
							}
						}
					}
						
					'SuppInfo' {
						'SeqNum'
						'ReturnCode'
						'Remark'
						'PositionNum'
					}
				}
			}
		}
		
		//End root node
		outXml.nodeCompleted(null,CSACIACK)
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
