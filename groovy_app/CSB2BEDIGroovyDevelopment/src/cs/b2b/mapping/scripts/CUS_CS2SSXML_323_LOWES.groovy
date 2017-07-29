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




/**
 * @author LAMJA	
 * GTNEXUSPORT SS_PT2PT initialize on 20161201 
 */
public class CUS_CS2SSXML_323_LOWES {
	
	private static final String COMPLETE = 'C'
	private static final String OBSOLETE = 'O'
	private static final String ERROR = 'E'
	private static final String YES = 'YES'
	private static final String NO = 'NO'
	private static final String TYPE = 'Type'
	private static final String ERROR_SUPPORT = 'ES'
	private static final String IS_ERROR = 'IsError'
	private static final String VALUE = 'Value'
	private static final String ERROR_COMPLETE = 'EC'

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	def conn = null
	
	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = 'HHmm'

	private void generateBody(MarkupBuilder outXml, def current_Body, def current_BodyIndex, def currentSystemDt, long ediIsaCtrlNumber, long ediGroupCtrlNum) {
		def ediLineCount = 0
		def ediTxnNum = ediGroupCtrlNum.toString().padRight(9,'0').toInteger() + current_BodyIndex+1
		def curLeg
		
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' ' '//'323'
				'E329_02' ' '//ediTxnNum
				ediLineCount = ediLineCount+1
			}
			
			if (current_Body.OceanComponent?.oceanleg.size() > 1) {
				curLeg = current_Body.OceanComponent?.oceanleg.last()
			} else {
				curLeg = current_Body.OceanComponent?.oceanleg.first()
			}

			'V1' {
				'E597_01' curLeg.Vessel?.LloydsNumber
				'E182_02' curLeg.Vessel?.Name
				'E55_04' curLeg.Voyage?.ExternalVoyageNumber
				'E140_05' current_Body.Carrier?.SCAC
				'E897_08' 'L'
				ediLineCount = ediLineCount+1
			}
		
			'K1' {
				'E61_01'  curLeg.service?.ServiceCode
				ediLineCount = ediLineCount+1
			}
			
			//R4*R
			'Loop_R4' {
				curLeg = current_Body.OceanComponent?.oceanleg.first()
				'R4'{
					'E115_01' 'R'
					'E309_02' 'UN'
					'E310_03' curLeg.POL?.UNLOCODE
					'E114_04' curLeg.POL?.PortName	
				}
				
				curLeg.POL?.Event.eachWithIndex { curEvent, curEvtIdx->
					if (curEvent.Name=='Estimate Berth Departure') {
						'DTM' {
							'E374_01' '369'
							'E373_02' util.substring(curEvent.LocalDateTime.text(),7,4)+util.substring(curEvent.LocalDateTime.text(),4,2)+util.substring(curEvent.LocalDateTime.text(),1,2)//yyyyMMdd
							ediLineCount = ediLineCount+1
						}
					}
				}
				
				'V9'{
					'E304_01' 'ZZZ'
					'E106_02' 'EVENT'
					ediLineCount = ediLineCount+1
				}
			}
			
			//R4*R
			'Loop_R4' {
				curLeg = current_Body.OceanComponent?.oceanleg.last()
				'R4'{
					'E115_01' 'D'
					'E309_02' 'UN'
					'E310_03' curLeg.POD?.UNLOCODE
					'E114_04' curLeg.POD?.PortName	
				}
				
				curLeg.POD?.Event.eachWithIndex { curEvent, curEvtIdx->
						if (curEvent.Name == 'Estimate Berth Arrival') {
						'DTM' {
							'E374_01' '371'
							'E373_02' util.substring(curEvent.LocalDateTime.text(),7,4)+util.substring(curEvent.LocalDateTime.text(),4,2)+util.substring(curEvent.LocalDateTime.text(),1,2)//yyyyMMdd
							ediLineCount = ediLineCount+1
						}
					}
				}
				
				'V9'{
					'E304_01' 'ZZZ'
					'E106_02' 'EVENT'
					ediLineCount = ediLineCount+1
				}
			}

			'SE' {
				ediLineCount = ediLineCount+1
										
				'E96_01' ' '//ediLineCount
				'E329_02' ' '//ediTxnNum

			}
		}
	}


	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		//inputXmlBody = util.removeBOM(inputXmlBody)

		/**
		 * Part II: get OLL mapping runtime parameters
		 */
		//[]{"B2BSessionID="+B2BSessionID, "B2B_OriginalSourceFileName="+B2B_OriginalSourceFileName, "B2B_SendPortID="+B2B_SendPortID, "PortProperty="+PortProperty, "MSG_REQ_ID="+MSG_REQ_ID, "TP_ID="+TP_ID, "MSG_TYPE_ID="+MSG_TYPE_ID, "DIR_ID="+DIR_ID};
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
		//Important: the inputXml is xml root element
		def SSMXML = new XmlSlurper().parseText(inputXmlBody)

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
		def T323 = outXml.createNode('T323')
		def bizKeyRoot = bizKeyXml.createNode('root')

		//BeginWorkFlow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def outputBodyCount = 0
		def currentSystemDt = new Date()
		
		def msgFmtId = "X.12"
		//def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, msgFmtId, conn)
		long ediIsaCtrlNumber = '1'
		long ediGroupCtrlNum = '1'
		

		//build ISA, GS
		//generateEDIHeader(outXml, currentSystemDt, ediIsaCtrlNumber, ediGroupCtrlNum)
		def txnErrorKeys = []
		
		//start body looping
		SSMXML.RouteGroup.Route.eachWithIndex { current_Body, current_BodyIndex ->

			//prep checking
			
			List<Map<String,String>> errorKeyList = prepValidation(current_Body)
			
			if (errorKeyList.size()== 0) {
				//pass validateBeforeExecution
				//main mapping
				generateBody(outXml, current_Body, current_BodyIndex, currentSystemDt, ediIsaCtrlNumber, ediGroupCtrlNum)
				outputBodyCount++
			}

			
			//posp checking
			//pospValidation()
			
			buildBizKeyXML(bizKeyXml, MSG_REQ_ID, current_BodyIndex, errorKeyList)
			txnErrorKeys.add(errorKeyList);
		}
		
		//build BizKey
		
				
		//End root node
		outXml.nodeCompleted(null,T323)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)

//*
		//promote bizkey to session
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizKeywriter?.toString())
		
		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = writer.toString();
		}
//*/
		writer.close();
		bizKeywriter.close()

		return result
	}


	private List<Map<String,String>> prepValidation(def current_Body) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
		//check oceanleg 
		if (current_Body.OceanComponent?.oceanleg.size() > 1 && current_Body.OceanComponent?.oceanleg?.last().Voyage?.ExternalVoyageNumber=="" ) {
			Map<String,String> errorKey = null
			errorKey = errorKey = [TYPE: OBSOLETE , IS_ERROR: NO,  VALUE: 'OceanLeg > 1 and Last OceanLeg without ExternalVoyageNumber']
			errorKeyList.add(errorKey);
		}
		
		//*/
		return errorKeyList;
	}

	private def pospValidation() {
	}

	
	private void buildBizKeyXML(MarkupBuilder bizKeyXml, def MSG_REQ_ID, def current_Bodyindex, def errorKeyList) {
		
		def appErrorReportError = errorKeyList.find{it?.IS_ERROR == YES}
		def appErrorReportObsolete = errorKeyList.find{it?.IS_ERROR == NO}
		
		bizKeyXml.'ns0:Transaction'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
			'ns0:ControlNumberInfo' {
				'ns0:Interchange' MSG_REQ_ID
				'ns0:Group' current_Bodyindex + 1
				'ns0:Transaction' current_Bodyindex + 1
			}
			
			'ns0:BizKey' {
				'ns0:Type' 'REF_NUM'
				'ns0:Value' MSG_REQ_ID+'_'+ (current_Bodyindex+1)
			}

			//'ns0:CarrierId'  

			if (errorKeyList.size != 0) {
				'ns1:AppErrorReport'('xmlns:ns1':'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
					if (appErrorReportError != null) {
						'ns1:Status' ERROR
					} else {
						'ns1:Status' OBSOLETE
					}
					'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'	//max length is 20, exceed will missing error bizkey
					if (appErrorReportError != null) {
						'ns1:Msg' appErrorReportError?.VALUE
					} else {
						'ns1:Msg' appErrorReportObsolete?.VALUE
					}
					'ns1:Severity' '5'
				}
			}
		}
	}
}






