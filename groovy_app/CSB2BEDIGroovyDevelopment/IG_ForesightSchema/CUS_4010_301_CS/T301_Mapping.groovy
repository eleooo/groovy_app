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

	outXml.'T301' {
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
			'B1' {
				'E140_01' ''
				'E145_02' ''
				'E373_03' ''
				'E558_04' ''
			}
			'G61' {
				'E366_01' ''
				'E93_02' ''
				'E365_03' ''
				'E364_04' ''
				'E443_05' ''
			}
			'Y6' {
				'E313_01' ''
				'E151_02' ''
				'E275_03' ''
			}
			'Y3' {
				'E13_01' ''
				'E140_02' ''
				'E373_03' ''
				'E373_04' ''
				'E154_05' ''
				'E112_06' ''
				'E373_07' ''
				'E337_08' ''
				'E91_09' ''
				'E375_10' ''
				'E623_11' ''
			}
			'Loop_Y4' {
				'Y4' {
					'E13_01' ''
					'E13_02' ''
					'E373_03' ''
					'E154_04' ''
					'E95_05' ''
					'E24_06' ''
					'E140_07' ''
					'E309_08' ''
					'E310_09' ''
					'E56_10' ''
				}
				'W09' {
					'E40_01' ''
					'E408_02' ''
					'E355_03' ''
					'E355_04' ''
					'E3_05' ''
					'E1122_06' ''
					'E488_07' ''
					'E380_08' ''
				}
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
			'R2A' {
				'E133_01' ''
				'E1431_02' ''
				'E91_03' ''
				'E140_04' ''
				'E309_05' ''
				'E310_06' ''
				'E56_07' ''
				'E1_08' ''
				'E742_09' ''
				'E98_10' ''
			}
			'Loop_N1' {
				'N1' {
					'E98_01' ''
					'E93_02' ''
					'E66_03' ''
					'E67_04' ''
					'E706_05' ''
					'E98_06' ''
				}
				'N2' {
					'E93_01' ''
					'E93_02' ''
				}
				'N3' {
					'E166_01' ''
					'E166_02' ''
				}
				'N4' {
					'E19_01' ''
					'E156_02' ''
					'E116_03' ''
					'E26_04' ''
					'E309_05' ''
					'E310_06' ''
				}
				'G61' {
					'E366_01' ''
					'E93_02' ''
					'E365_03' ''
					'E364_04' ''
					'E443_05' ''
				}
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
			'W09' {
				'E40_01' ''
				'E408_02' ''
				'E355_03' ''
				'E408_04' ''
				'E355_05' ''
				'E3_06' ''
				'E1122_07' ''
				'E488_08' ''
				'E380_09' ''
			}
			'H3' {
				'E152_01' ''
				'E153_02' ''
				'E241_03' ''
				'E242_04' ''
				'E257_05' ''
			}
			'EA' {
				'E1402_01' ''
				'C001_02' {
					'E355_01' ''
					'E1018_02' ''
					'E649_03' ''
					'E355_04' ''
					'E1018_05' ''
					'E649_06' ''
					'E355_07' ''
					'E1018_08' ''
					'E649_09' ''
					'E355_10' ''
					'E1018_11' ''
					'E649_12' ''
					'E355_13' ''
					'E1018_14' ''
					'E649_15' ''
				}
				'E380_03' ''
			}
			'Loop_LX' {
				'LX' {
					'E554_01' ''
				}
				'N7' {
					'E206_01' ''
					'E207_02' ''
					'E81_03' ''
					'E187_04' ''
					'E167_05' ''
					'E232_06' ''
					'E205_07' ''
					'E183_08' ''
					'E184_09' ''
					'E102_10' ''
					'E40_11' ''
					'E140_12' ''
					'E319_13' ''
					'E219_14' ''
					'E567_15' ''
					'E571_16' ''
					'E188_17' ''
					'E761_18' ''
					'E56_19' ''
					'E65_20' ''
					'E189_21' ''
					'E24_22' ''
					'E140_23' ''
					'E301_24' ''
				}
				'W09' {
					'E40_01' ''
					'E408_02' ''
					'E355_03' ''
					'E408_04' ''
					'E355_05' ''
					'E3_06' ''
					'E1122_07' ''
					'E488_08' ''
					'E380_09' ''
				}
				'K1' {
					'E61_01' ''
					'E61_02' ''
				}
				'L0' {
					'E213_01' ''
					'E220_02' ''
					'E221_03' ''
					'E81_04' ''
					'E187_05' ''
					'E183_06' ''
					'E184_07' ''
					'E80_08' ''
					'E211_09' ''
					'E458_10' ''
					'E188_11' ''
					'E56_12' ''
					'E380_13' ''
					'E211_14' ''
					'E1073_15' ''
				}
				'L5' {
					'E213_01' ''
					'E79_02' ''
					'E22_03' ''
					'E23_04' ''
					'E103_05' ''
					'E87_06' ''
					'E88_07' ''
					'E23_08' ''
					'E22_09' ''
					'E595_10' ''
				}
				'L4' {
					'E82_01' ''
					'E189_02' ''
					'E65_03' ''
					'E90_04' ''
					'E380_05' ''
					'E1271_06' ''
				}
				'L1' {
					'E213_01' ''
					'E60_02' ''
					'E122_03' ''
					'E58_04' ''
					'E191_05' ''
					'E117_06' ''
					'E120_07' ''
					'E150_08' ''
					'E121_09' ''
					'E39_10' ''
					'E16_11' ''
					'E276_12' ''
					'E257_13' ''
					'E74_14' ''
					'E122_15' ''
					'E372_16' ''
					'E220_17' ''
					'E221_18' ''
					'E954_19' ''
					'E100_20' ''
					'E610_21' ''
				}
				'Loop_H1' {
					'H1' {
						'E62_01' ''
						'E209_02' ''
						'E208_03' ''
						'E64_04' ''
						'E63_05' ''
						'E200_06' ''
						'E77_07' ''
						'E355_08' ''
						'E254_09' ''
					}
					'H2' {
						'E64_01' ''
						'E274_02' ''
					}
				}
			}
			'V1' {
				'E597_01' ''
				'E182_02' ''
				'E26_03' ''
				'E55_04' ''
				'E140_05' ''
				'E249_06' ''
				'E854_07' ''
				'E897_08' ''
				'E91_09' ''
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
