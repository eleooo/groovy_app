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

	outXml.'VERMAS' {
		'Group_UNH' {
			'UNH' {
				'E0062_01' ''
				'S009_02' {
					'E0065_01' ''
					'E0052_02' ''
					'E0054_03' ''
					'E0051_04' ''
					'E0057_05' ''
				}
				'E0068_03' ''
				'S010_04' {
					'E0070_01' ''
					'E0073_02' ''
				}
			}
			'BGM' {
				'C002_01' {
					'E1001_01' ''
					'E1131_02' ''
					'E3055_03' ''
					'E1000_04' ''
				}
				'C106_02' {
					'E1004_01' ''
					'E1056_02' ''
					'E1060_03' ''
				}
				'E1225_03' ''
				'E4343_04' ''
				'E1373_05' ''
				'E3453_06' ''
			}
			'DTM' {
				'C507_01' {
					'E2005_01' ''
					'E2380_02' ''
					'E2379_03' ''
				}
			}
			'Group1_RFF' {
				'RFF' {
					'C506_01' {
						'E1153_01' ''
						'E1154_02' ''
						'E1156_03' ''
						'E1056_04' ''
						'E1060_05' ''
					}
				}
				'DTM' {
					'C507_01' {
						'E2005_01' ''
						'E2380_02' ''
						'E2379_03' ''
					}
				}
			}
			'Group2_NAD' {
				'NAD' {
					'E3035_01' ''
					'C082_02' {
						'E3039_01' ''
						'E1131_02' ''
						'E3055_03' ''
					}
					'C058_03' {
						'E3124_01' ''
						'E3124_02' ''
						'E3124_03' ''
						'E3124_04' ''
						'E3124_05' ''
					}
					'C080_04' {
						'E3036_01' ''
						'E3036_02' ''
						'E3036_03' ''
						'E3036_04' ''
						'E3036_05' ''
						'E3045_06' ''
					}
					'C059_05' {
						'E3042_01' ''
						'E3042_02' ''
						'E3042_03' ''
						'E3042_04' ''
					}
					'E3164_06' ''
					'C819_07' {
						'E3229_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E3228_04' ''
					}
					'E3251_08' ''
					'E3207_09' ''
				}
				'Group3_CTA' {
					'CTA' {
						'E3139_01' ''
						'C056_02' {
							'E3413_01' ''
							'E3412_02' ''
						}
					}
					'COM' {
						'C076_01' {
							'E3148_01' ''
							'E3155_02' ''
						}
					}
				}
			}
			'Group4_EQD' {
				'EQD' {
					'E8053_01' ''
					'C237_02' {
						'E8260_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E3207_04' ''
					}
					'C224_03' {
						'E8155_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E8154_04' ''
					}
					'E8077_04' ''
					'E8249_05' ''
					'E8169_06' ''
					'E4233_07' ''
				}
				'RFF' {
					'C506_01' {
						'E1153_01' ''
						'E1154_02' ''
						'E1156_03' ''
						'E1056_04' ''
						'E1060_05' ''
					}
				}
				'LOC' {
					'E3227_01' ''
					'C517_02' {
						'E3225_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E3224_04' ''
					}
					'C519_03' {
						'E3223_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E3222_04' ''
					}
					'C553_04' {
						'E3233_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E3232_04' ''
					}
					'E5479_05' ''
				}
				'SEL' {
					'E9308_01' ''
					'C215_02' {
						'E9303_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E9302_04' ''
					}
					'E4517_03' ''
					'C208_04' {
						'E7402_01' ''
						'E7402_02' ''
					}
					'E4525_05' ''
				}
				'Group5_MEA' {
					'MEA' {
						'E6311_01' ''
						'C502_02' {
							'E6313_01' ''
							'E6321_02' ''
							'E6155_03' ''
							'E6154_04' ''
						}
						'C174_03' {
							'E6411_01' ''
							'E6314_02' ''
							'E6162_03' ''
							'E6152_04' ''
							'E6432_05' ''
						}
						'E7383_04' ''
					}
					'DTM' {
						'C507_01' {
							'E2005_01' ''
							'E2380_02' ''
							'E2379_03' ''
						}
					}
				}
				'Group6_TDT' {
					'TDT' {
						'E8051_01' ''
						'E8028_02' ''
						'C220_03' {
							'E8067_01' ''
							'E8066_02' ''
						}
						'C001_04' {
							'E8179_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E8178_04' ''
						}
						'C040_05' {
							'E3127_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E3126_04' ''
						}
						'E8101_06' ''
						'C401_07' {
							'E8457_01' ''
							'E8459_02' ''
							'E7130_03' ''
						}
						'C222_08' {
							'E8213_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E8212_04' ''
							'E8453_05' ''
						}
						'E8281_09' ''
						'C003_10' {
							'E7041_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E7040_04' ''
						}
					}
					'RFF' {
						'C506_01' {
							'E1153_01' ''
							'E1154_02' ''
							'E1156_03' ''
							'E1056_04' ''
							'E1060_05' ''
						}
					}
				}
				'Group7_DOC' {
					'DOC' {
						'C002_01' {
							'E1001_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E1000_04' ''
						}
						'C503_02' {
							'E1004_01' ''
							'E1373_02' ''
							'E1366_03' ''
							'E3453_04' ''
							'E1056_05' ''
							'E1060_06' ''
						}
						'E3153_03' ''
						'E1220_04' ''
						'E1218_05' ''
					}
					'DTM' {
						'C507_01' {
							'E2005_01' ''
							'E2380_02' ''
							'E2379_03' ''
						}
					}
					'Group8_NAD' {
						'NAD' {
							'E3035_01' ''
							'C082_02' {
								'E3039_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
							'C058_03' {
								'E3124_01' ''
								'E3124_02' ''
								'E3124_03' ''
								'E3124_04' ''
								'E3124_05' ''
							}
							'C080_04' {
								'E3036_01' ''
								'E3036_02' ''
								'E3036_03' ''
								'E3036_04' ''
								'E3036_05' ''
								'E3045_06' ''
							}
							'C059_05' {
								'E3042_01' ''
								'E3042_02' ''
								'E3042_03' ''
								'E3042_04' ''
							}
							'E3164_06' ''
							'C819_07' {
								'E3229_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3228_04' ''
							}
							'E3251_08' ''
							'E3207_09' ''
						}
						'Group9_CTA' {
							'CTA' {
								'E3139_01' ''
								'C056_02' {
									'E3413_01' ''
									'E3412_02' ''
								}
							}
							'COM' {
								'C076_01' {
									'E3148_01' ''
									'E3155_02' ''
								}
							}
						}
					}
				}
			}
			'UNT' {
				'E0074_01' ''
				'E0062_02' ''
			}
		}
	}
	
}
