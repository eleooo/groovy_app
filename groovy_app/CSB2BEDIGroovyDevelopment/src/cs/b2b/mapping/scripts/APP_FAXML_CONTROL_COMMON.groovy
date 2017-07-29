package cs.b2b.mapping.scripts

import cs.b2b.core.mapping.bean.ack.fa.FunctionalAcknowlegment
import cs.b2b.core.mapping.util.XmlBeanParser

import java.sql.Connection
import groovy.xml.MarkupBuilder;

class APP_FAXML_CONTROL_COMMON {
	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_ACK_IRA_Common ackUtil = new cs.b2b.core.mapping.util.MappingUtil_ACK_IRA_Common(util);

	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	Connection conn = null

	public void generateBody(FunctionalAcknowlegment fa, MarkupBuilder outXml) {
		def firstTnx = null;
		if(fa?.Transactions?.size() > 0){
			firstTnx = fa?.Transactions?.first();
		} else {
			throw new Exception("Cannot find valid FA body")
		}
		outXml.'Group_UNH' {
			'UNH' {
				'E0062_01' '1'
				'S009_02' {
					'E0065_01' 'CONTRL'
					'E0052_02' firstTnx?.Origin?.Version
					'E0054_03' firstTnx?.Origin?.Release
					'E0051_04' firstTnx?.Origin?.Agency
				}
			}
			'UCI' {
				'E0020_01' fa?.Information?.Origin?.InterchangeControlNumber
				'S002_02' {
					'E0004_01' fa?.Information?.ReceiverId
					'E0007_02' fa?.Information?.ReceiverQualifier
				}
				'S003_03' {
					'E0010_01' fa?.Information?.SenderId
					'E0007_02' fa?.Information?.SenderQualifier
				}
				'E0083_04' '7'
			}
			fa?.Transactions?.each { txn ->
				'Group1_UCM' {
					'UCM' {
						'E0062_01' txn?.Origin?.ControlNumber
						'S009_02' {
							'E0065_01' txn?.Origin?.MessageType
							'E0052_02' txn?.Origin?.Version
							'E0054_03' txn?.Origin?.Release
							'E0051_04' txn?.Origin?.Agency
							'E0057_05' txn?.Origin?.AssociationAssignedCode
						}
						if(txn?.ActionCode && txn?.ActionCode == 'REJECTED') {
							'E0083_03' '4'
						}
						else
							'E0083_03' '7'
					}
					txn?.Errors?.each { error ->
							error?.Segment?.Field?.each { field ->
								'Group2_UCS' {
									def error_Code = ''
									if(field?.ErrorCode){
										def errorDesc = field?.ErrorCode.toLowerCase()
										if(errorDesc.contains("missing"))
											error_Code = '13'
										else if(errorDesc.contains("too short"))
											error_Code = '40'
										else if(errorDesc.contains("too long"))
											error_Code = '39'
										else if(errorDesc.contains("invalid value"))
											error_Code = '12'
										else
											error_Code = '12'
									}
									'UCS' {
										'E0096_01' error?.Segment?.Position
										'E0085_02' error_Code
									}
									'UCD' {
										'E0085_01' error_Code
										'S011_02' {
											'E0098_01' field?.Position
											'E0104_02' field?.CompositePosition
										}

									}
								}
						}
					}
				}
			}
			'UNT' {
				'E0074_01' '-1'
				'E0062_02' '1'
			}
		}
	}
	
	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */

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
		//		def parser = new XmlParser()
		//		parser.setNamespaceAware(false);
		//		def ContainerMovement = parser.parseText(inputXmlBody);

		//Parse the xmlBody to JavaBean
		XmlBeanParser parser = new XmlBeanParser()
		FunctionalAcknowlegment fa = parser.xmlParser(inputXmlBody, FunctionalAcknowlegment.class)

		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def bizKeyWriter = new StringWriter();
		def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeyWriter), "", false))
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")


		def CONTRL = outXml.createNode('CONTRL')
		def bizKeyRoot = bizKeyXml.createNode('root')

		generateBody(fa, outXml)
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
		ackUtil.buildBizKey(bizKeyXml, fa,1,errorKeyList, TP_ID, conn)

		outXml.nodeCompleted(null, CONTRL)
		bizKeyXml.nodeCompleted(null, bizKeyRoot)

		//println bizKeyWriter?.toString()
		ackUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);

		String result = '';
		result = writer?.toString();
		result = util.cleanXml(result)

	//	println '-----------'
	//	println result

		writer.close();
		return result
	}

	public boolean pospValidation() {

	}
}
