import groovy.xml.MarkupBuilder
import java.sql.Connection


String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

	/**
	 * Part I: prep handling here, 
	 */
	//inputXmlBody = util.removeBOM(inputXmlBody)

	/**
	 * Part II: get OLL mapping runtime parameters
	 */
	def appSessionId = util.getRuntimeParameter("AppSessionID", runtimeParams);
	def sourceFileName = util.getRuntimeParameter("OriginalSourceFileName", runtimeParams);
	def TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
	def MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
	def DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
	def MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

	/**
	 * Part III: read xml and prepare output xml
	 */
	def parser = new XmlParser()
	parser.setNamespaceAware(false)
	//Important: the inputXml is xml root element
	def ContainerMovement = parser.parseText(inputXmlBody);

	
	def writer = new StringWriter()
	def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

	/**
	 * Part IV: mapping script start from here
	 */

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()
	//please update foramt here
	def msgFmtId = "X.12"
	def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, msgFmtId, conn)
	long ediIsaCtrlNumber = ctrlNos[0]
	long ediGroupCtrlNum = ctrlNos[1]

	outXml.'T315' {
		'ISA' {
			'I01_01' ''
			'I02_02' ''
			'I03_03' ''
			'I04_04' ''
			'I05_05' ''
			'I06_06' ''
			'I05_07' ''
			'I07_08' ''
			'I08_09' ''
			'I09_10' ''
			'I10_11' ''
			'I11_12' ''
			'I12_13' ''
			'I13_14' ''
			'I14_15' ''
			'I15_16' ''
		}
		'GS' {
			'E479_01' ''
			'E142_02' ''
			'E124_03' ''
			'E373_04' ''
			'E337_05' ''
			'E28_06' ''
			'E455_07' ''
			'E480_08' ''
		}
		'Loop_ST' {
			'ST' {
				'E143_01' ''
				'E329_02' ''
			}
			'B4' {
				'E152_01' ''
				'E71_02' ''
				'E157_03' ''
				'E373_04' ''
				'E161_05' ''
				'E159_06' ''
				'E206_07' ''
				'E207_08' ''
				'E578_09' ''
				'E24_10' ''
				'E310_11' ''
				'E309_12' ''
				'E761_13' ''
			}
			'N9' {
				'E128_01' ''
				'E127_02' ''
				'E369_03' ''
				'E373_04' ''
				'E337_05' ''
				'E623_06' ''
				'C040_07' {
					'E128_01' ''
					'E127_02' ''
					'E128_03' ''
					'E127_04' ''
					'E128_05' ''
					'E127_06' ''
				}
			}
			'Q2' {
				'E597_01' ''
				'E26_02' ''
				'E373_03' ''
				'E373_04' ''
				'E373_05' ''
				'E80_06' ''
				'E81_07' ''
				'E187_08' ''
				'E55_09' ''
				'E128_10' ''
				'E127_11' ''
				'E897_12' ''
				'E182_13' ''
				'E183_14' ''
				'E184_15' ''
				'E188_16' ''
			}
			'SG' {
				'E157_01' ''
				'E641_02' ''
				'E35_03' ''
				'E373_04' ''
				'E337_05' ''
				'E623_06' ''
			}
			'Loop_R4' {
				'R4' {
					'E115_01' ''
					'E309_02' ''
					'E310_03' ''
					'E114_04' ''
					'E26_05' ''
					'E174_06' ''
					'E113_07' ''
					'E156_08' ''
				}
				'DTM' {
					'E374_01' ''
					'E373_02' ''
					'E337_03' ''
					'E623_04' ''
					'E1250_05' ''
					'E1251_06' ''
				}
			}
			'V9' {
				'E304_01' ''
				'E106_02' ''
				'E373_03' ''
				'E337_04' ''
				'E19_05' ''
				'E156_06' ''
				'E26_07' ''
				'E641_08' ''
				'E154_09' ''
				'E380_10' ''
				'E1274_11' ''
				'E61_12' ''
				'E623_13' ''
				'E380_14' ''
				'E154_15' ''
				'E86_16' ''
				'E86_17' ''
				'E86_18' ''
				'E81_19' ''
				'E82_20' ''
			}
			'SE' {
				'E96_01' ''
				'E329_02' ''
			}
		}
		'GE' {
			'E97_01' ''
			'E28_02' ''
		}
		'IEA' {
			'I16_01' ''
			'I12_02' ''
		}
	}
	
}
