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
public class CUS_CS2SSXML_323_GTNEXUSPORT {
	
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
		
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' ' '//'323'
				'E329_02' ' '//ediTxnNum
				ediLineCount = ediLineCount+1
			}
			'V1' {
				if (current_Body.OceanComponent?.oceanleg[0]?.Vessel?.LloydsNumber.text()!='') {
					'E597_01' current_Body.OceanComponent?.oceanleg[0]?.Vessel?.LloydsNumber.text()
				} else 'E597_01' '99999996'
			
				'E182_02' util.substring(current_Body.OceanComponent?.oceanleg[0]?.Vessel?.Name.text(),1,28)
				'E26_03' 
				
				if (current_Body.OceanComponent?.oceanleg[0]?.Voyage?.ExternalVoyageNumber.text() !='') {
					'E55_04' current_Body.OceanComponent?.oceanleg[0]?.Voyage.ExternalVoyageNumber.text()
				} else {
					'E55_04' current_Body.OceanComponent?.oceanleg[0]?.Voyage?.InternalVoyageNumber.text() + current_Body.OceanComponent?.oceanleg[0]?.Direction.text()
					}
				'E140_05' current_Body.Carrier?.SCAC.text()
				'E249_06' 
				'E854_07' 
				'E897_08' 'L'
				'E91_09' 'O'
				
				ediLineCount = ediLineCount+1
			}
			
			'K1' {
				if (4>current_Body.OceanComponent?.oceanleg.size()) {
					def K1_01_UNLOCODE = ''
					def K1_01_ServiceCde = ''
					def K1_01_value=''
					
					current_Body.OceanComponent?.oceanleg?.each { currentLeg ->
						K1_01_UNLOCODE = K1_01_UNLOCODE + util.substring(currentLeg.POL?.UNLOCODE.text(),3,3) + "-"
						K1_01_ServiceCde = K1_01_ServiceCde + "-" + currentLeg.service?.ServiceCode.text()
					}
					K1_01_value =K1_01_UNLOCODE + util.substring(current_Body.OceanComponent?.oceanleg?.last()?.POD?.UNLOCODE.text(),3,3) + K1_01_ServiceCde
					
					'E61_01'  K1_01_value
					
				} else if (4==current_Body.OceanComponent?.oceanleg.size()) {
					def K1_01_UNLOCODE = ''
					def K1_01_ServiceCde = ''
					def K1_01_value=''
					
					current_Body.OceanComponent?.oceanleg?.each { currentLeg ->
						K1_01_UNLOCODE = K1_01_UNLOCODE + util.substring(currentLeg.POL?.UNLOCODE.text(),3,3) + "-"
						K1_01_ServiceCde = K1_01_ServiceCde + "-" + currentLeg.service?.ServiceCode.text()
					}
					K1_01_value =K1_01_UNLOCODE + util.substring(current_Body.OceanComponent?.oceanleg?.last()?.POD?.UNLOCODE.text(),3,3) + "-"+ current_Body.OceanComponent?.oceanleg[0]?.service?.ServiceCode.text() + "-TS-" +current_Body.OceanComponent?.oceanleg?.last()?.service?.ServiceCode.text()
					
					'E61_01'  K1_01_value
				} else {
					def K1_01_UNLOCODE = ''
					def K1_01_ServiceCde = ''
					def K1_01_value=''
					
					current_Body.OceanComponent?.oceanleg?.each { currentLeg ->
						K1_01_UNLOCODE = K1_01_UNLOCODE + util.substring(currentLeg.POL?.UNLOCODE.text(),3,3) + "-"
						K1_01_ServiceCde = K1_01_ServiceCde + "-" + currentLeg.service?.ServiceCode.text()
					}
					K1_01_value =K1_01_UNLOCODE + util.substring(current_Body.OceanComponent?.oceanleg?.last()?.POD?.UNLOCODE.text(),3,3) + K1_01_ServiceCde
					
					'E61_01'  K1_01_value
				}
				ediLineCount = ediLineCount+1
			}
			'Loop_R4' {
				'R4'{
					'E115_01' 'L'
					'E309_02' 'UN'
					
					def PORUNLocde = current_Body.Origin?.City?.UNLOCODE.text()
					def PORFaciltyCde = current_Body.OceanComponent?.oceanleg[0].POL?.FacilityCode.text()
					
					if (util.notEmpty(util.getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', PORUNLocde, PORFaciltyCde, conn))) {
						'E310_03' util.getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', PORUNLocde, PORFaciltyCde, conn)//code conversion

					} else {
						'E310_03' PORUNLocde
					}
					
					'E114_04' current_Body.Origin?.City?.City.text().take(24)
					'E26_05' PORUNLocde.take(2)
					ediLineCount = ediLineCount+1
				}
				
				current_Body.OceanComponent?.oceanleg[0]?.POL?.Event?.each { currentEvent ->
					if('Default_Cutoff' == currentEvent.Name.text()) {
						//Defaut_Cutoff
						'V9'{
							'E304_01' 'CRD'
							'E373_03' util.substring(currentEvent.LocalDateTime.text(),7,4)+util.substring(currentEvent.LocalDateTime.text(),4,2)+util.substring(currentEvent.LocalDateTime.text(),1,2)//yyyyMMdd
							'E337_04' util.substring(currentEvent.LocalDateTime.text(),12,2)+util.substring(currentEvent.LocalDateTime.text(),15,2)//HHmm
							ediLineCount = ediLineCount+1
						}
					} else if ('Estimate Berth Departure' == currentEvent.Name.text()) {
						//Estimate Berth Departure
						'V9'{
							'E304_01' 'EDD'
							'E373_03' util.substring(currentEvent.LocalDateTime.text(),7,4)+util.substring(currentEvent.LocalDateTime.text(),4,2)+util.substring(currentEvent.LocalDateTime.text(),1,2)//yyyyMMdd
							'E337_04' util.substring(currentEvent.LocalDateTime.text(),12,2)+util.substring(currentEvent.LocalDateTime.text(),15,2)//HHmm
							ediLineCount = ediLineCount+1
						}
					}
				}
				
			}
			
			//loop leg
			if (0<current_Body.OceanComponent?.oceanleg?.size()) {
				current_Body.OceanComponent?.oceanleg?.eachWithIndex { currentOceanLeg, curLegIdx ->
					if(current_Body.OceanComponent?.oceanleg?.size()-1>curLegIdx) {
						'Loop_R4' {
							'R4' {
								'E115_01' 'I'
								'E309_02' 'UN'
								
								def PODUNLocde = currentOceanLeg.POD?.UNLOCODE.text()
								def PODFaciltyCde = currentOceanLeg.POD?.FacilityCode.text()
								
								if (util.getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', PODUNLocde, PODFaciltyCde, conn)) {
									'E310_03' util.getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', PODUNLocde, PODFaciltyCde, conn) //code conversion
								} else {
									'E310_03' PODUNLocde
								}
								'E114_04' currentOceanLeg.POD?.PortName.text().take(24) 
								'E26_05' PODUNLocde.take(2)
								ediLineCount = ediLineCount+1
							}
							
							currentOceanLeg.POD?.Event?.each { currentEvent ->
								if('Estimate Berth Arrival' == currentEvent.Name.text()) {
									//Defaut_Cutoff
									'V9'{
										'E304_01' 'EAD'
										'E373_03' util.substring(currentEvent.LocalDateTime.text(),7,4)+util.substring(currentEvent.LocalDateTime.text(),4,2)+util.substring(currentEvent.LocalDateTime.text(),1,2)//yyyyMMdd
										'E337_04' util.substring(currentEvent.LocalDateTime.text(),12,2)+util.substring(currentEvent.LocalDateTime.text(),15,2)//HHmm
										ediLineCount = ediLineCount+1
									}
								}
							}
							current_Body.OceanComponent?.oceanleg[curLegIdx+1].POL?.Event?.each { currentEvent ->
								if ('Estimate Berth Departure' == currentEvent.Name.text()) {
									//Estimate Berth Departure
									'V9'{
										'E304_01' 'EDD'
										'E373_03' util.substring(currentEvent.LocalDateTime.text(),7,4)+util.substring(currentEvent.LocalDateTime.text(),4,2)+util.substring(currentEvent.LocalDateTime.text(),1,2)//yyyyMMdd
										'E337_04' util.substring(currentEvent.LocalDateTime.text(),12,2)+util.substring(currentEvent.LocalDateTime.text(),15,2)//HHmm
										ediLineCount = ediLineCount+1
									}
								}
								
							}

						}
					}
				}
			}
			
			//FND
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'
					'E309_02' 'UN'
					
					def FNDUNLocde = current_Body.Destination?.City?.UNLOCODE.text()
					def PODLastFaciltyCde = current_Body.OceanComponent?.oceanleg?.last()?.POD?.FacilityCode.text()
					
					if (util.getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', FNDUNLocde, PODLastFaciltyCde, conn)) {
						'E310_03' util.getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', FNDUNLocde, PODLastFaciltyCde, conn) //code conversion
					} else {
						'E310_03' FNDUNLocde
					}
					'E114_04' current_Body.Destination?.City?.City.text().take(24)
					'E26_05' FNDUNLocde.take(2)
					ediLineCount = ediLineCount+1
				}
				
				current_Body.OceanComponent?.oceanleg?.last()?.POD?.Event?.each { currentEvent ->
					if('Estimate Berth Arrival' == currentEvent.Name.text()) {
						'V9'{
							'E304_01' 'EAD'
							'E373_03' util.substring(currentEvent.LocalDateTime.text(),7,4)+util.substring(currentEvent.LocalDateTime.text(),4,2)+util.substring(currentEvent.LocalDateTime.text(),1,2)//yyyyMMdd
							'E337_04' util.substring(currentEvent.LocalDateTime.text(),12,2)+util.substring(currentEvent.LocalDateTime.text(),15,2)//HHmm
							ediLineCount = ediLineCount+1
						}
					}
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
			
			//generateBody(outXml, current_Body, current_BodyIndex, currentSystemDt, ediIsaCtrlNumber, ediGroupCtrlNum)
			
			//posp checking
			//pospValidation()
		
			
			buildBizKeyXML(bizKeyXml, MSG_REQ_ID, current_BodyIndex, errorKeyList)
			txnErrorKeys.add(errorKeyList);
		}
		
		//build GE, IEA
		//generateEDITail(outXml, outputBodyCount, ediIsaCtrlNumber, ediGroupCtrlNum)
		//EndWorkFlow

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

	private void generateEDIHeader(MarkupBuilder outXml, def currentSystemDt, long ediIsaCtrlNumber, long ediGroupCtrlNum) {
	
			outXml.'ISA' {
				'ISA_01' '00'
				'ISA_02' '          '
				'ISA_03' '00'
				'ISA_04' '          '
				'ISA_05' '01'
				'ISA_06' 'CARGOSMART     '
				'ISA_07' 'ZZ'
				'ISA_08' 'GTNEXUS        '
				'ISA_09' currentSystemDt.format("yyMMdd") //yymmdd
				'ISA_10' currentSystemDt.format("HHmm") //hhmm
				'ISA_11' 'U'
				'ISA_12' '00401'
				'ISA_13' ediIsaCtrlNumber.toString().padLeft(9,'0')
				'ISA_14' '0'
				'ISA_15' 'P'
				'ISA_16' '>'
			}
			
			outXml.'GS' {
				'GS_01' 'SO'
				'GS_02' 'CARGOSMART'
				'GS_03' 'GTNEXUS'
				'GS_04' currentSystemDt.format("yyyyMMdd") //yymmdd
				'GS_05' currentSystemDt.format("HHmm") //yymmdd
				'GS_06' ediGroupCtrlNum
				'GS_07' 'X'
				'GS_08' '004010'
			}

	}

	private List<Map<String,String>> prepValidation(def current_Body) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
		//check VoyageNumber
		if (current_Body.OceanComponent?.oceanleg[0]?.Voyage?.InternalVoyageNumber=="" && current_Body.OceanComponent?.oceanleg[0]?.Voyage?.ExternalVoyageNumber=="") {
			Map<String,String> errorKey = null
			errorKey = errorKey = [TYPE: ERROR , IS_ERROR: YES,  VALUE: 'Missing VoyageNumber']
			errorKeyList.add(errorKey);
		}
		//check oceanleg 
		if (current_Body.OceanComponent?.oceanleg.size() > 4) {
			Map<String,String> errorKey = null
			errorKey = errorKey = [TYPE: ERROR , IS_ERROR: YES,  VALUE: 'More than 4 OceanLeg']
			errorKeyList.add(errorKey);
		}
		
		//*/
		return errorKeyList;
	}

	private def pospValidation() {
	}

	private void generateEDITail(MarkupBuilder outXml, def outputBodyCount, long ediIsaCtrlNumber, long ediGroupCtrlNum) {
		outXml.'GE' {
			'GE_01' outputBodyCount
			'GE_02' ediGroupCtrlNum
		}
		outXml.'IEA' {
			'IEA_01' '1'
			'IEA_02' ediIsaCtrlNumber.toString().padLeft(9,'0')
		}
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






