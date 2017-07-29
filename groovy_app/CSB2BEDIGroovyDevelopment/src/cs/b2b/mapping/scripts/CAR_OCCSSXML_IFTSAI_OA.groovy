package cs.b2b.mapping.scripts

import cs.b2b.core.mapping.bean.edi.xml.oa.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import javax.xml.bind.DatatypeConverter
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat

class CAR_OCCSSXML_IFTSAI_OA {
	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();



	def mapIFTSAI(Message message, String TP_ID, Connection conn) {
		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		
		outXml.'IFTSAI' {
			message?.voyage?.Voyage.each { currVoyage ->
				'Group_UNH' {
					'UNH' {
						'E0062_01' ' '
						'S009_02' {
							'E0065_01' 'IFTSAI'
							'E0052_02' 'D'
							'E0054_03' '99B'
							'E0051_04' 'UN'
							'E0057_05' ''
							'E0110_06' ''
							'E0113_07' ''
						}
						'E0068_03' ''
						'S010_04' {
							'E0070_01' ''
							'E0073_02' ''
						}
						'S016_05' {
							'E0115_01' ''
							'E0116_02' ''
							'E0118_03' ''
							'E0051_04' ''
						}
						'S017_06' {
							'E0121_01' ''
							'E0122_02' ''
							'E0124_03' ''
							'E0051_04' ''
						}
						'S018_07' {
							'E0127_01' ''
							'E0128_02' ''
							'E0130_03' ''
							'E0051_04' ''
						}
					}
					'BGM' {
						'C002_01' {
							'E1001_01' '96'
							'E1131_02' ''
							'E3055_03' ''
							'E1000_04' 'IFTSAI'
						}
						'C106_02' {
							'E1004_01' ''
							'E1056_02' ''
							'E1060_03' ''
						}
						if (message.identification.Identification.ScheduleType == "NewSchedule")
							'E1225_03' '9'
						else if (message.identification.Identification.ScheduleType == "UpdatedSchedule") {
							if (message.voyage.Voyage.ScheduleType == "LongTerm")
								'E1225_03' '5'
							else
								'E1225_03' '4';
						}
						else if (message.identification.Identification.ScheduleType == "DeletedSchedule")
							'E1225_03' '1'

						'E4343_04' ''
					}
					'DTM' {
						'C507_01' {
							'E2005_01' ''
							'E2380_02' ''
							'E2379_03' ''
						}
					}
					'FTX' {
						'E4451_01' ''
						'E4453_02' ''
						'C107_03' {
							'E4441_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
						'C108_04' {
							'E4440_01' ''
							'E4440_02' ''
							'E4440_03' ''
							'E4440_04' ''
							'E4440_05' ''
						}
						'E3453_05' ''
						'E4447_06' ''
					}
					'GIS' {
						'C529_01' {
							'E7365_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E7187_04' ''
						}
					}
					'Group1_RFF' {
						'RFF' {
							'C506_01' {
								'E1153_01' ''
								'E1154_02' ''
								'E1156_03' ''
								'E4000_04' ''
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
					'Group2_LOC' {
						'LOC' {
							'E3227_01' ''
							'C517_02' {
								'E3225_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3224_04' ''
								'E3225_05' ''
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
						'DTM' {
							'C507_01' {
								'E2005_01' ''
								'E2380_02' ''
								'E2379_03' ''
							}
						}
					}
					'Group3_EQD' {
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
						}
						'EQN' {
							'C523_01' {
								'E6350_01' ''
								'E6353_02' ''
							}
						}
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
						'DIM' {
							'E6145_01' ''
							'C211_02' {
								'E6411_01' ''
								'E6168_02' ''
								'E6140_03' ''
								'E6008_04' ''
							}
						}
						'FTX' {
							'E4451_01' ''
							'E4453_02' ''
							'C107_03' {
								'E4441_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
							'C108_04' {
								'E4440_01' ''
								'E4440_02' ''
								'E4440_03' ''
								'E4440_04' ''
								'E4440_05' ''
							}
							'E3453_05' ''
							'E4447_06' ''
						}
						'RFF' {
							'C506_01' {
								'E1153_01' ''
								'E1154_02' ''
								'E1156_03' ''
								'E4000_04' ''
								'E1060_05' ''
							}
						}
						'TPL' {
							'C222_01' {
								'E8213_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E8212_04' ''
								'E8453_05' ''
							}
						}
					}
					'Group4_TDT' {
						'TDT' {
							'E8051_01' ''
							'E8028_02' currVoyage?.CurrentExternalReference
							'C220_03' {
								'E8067_01' ''
								'E8066_02' ''
							}
							'C228_04' {
								'E8179_01' ''
								'E8178_02' ''
							}
							'C040_05' {
								if (currVoyage?.carrier?.Carrier?.Name?.isEmpty() == false)
									if (currVoyage?.carrier?.Carrier?.Name?.length() >= 17)
										'E3127_01' currVoyage?.carrier?.Carrier?.Name?.substring(0,17)
									else
										'E3127_01' currVoyage?.carrier?.Carrier?.Name?.substring(0,currVoyage?.carrier?.Carrier?.Name?.length());

								'E1131_02' ''
								'E3055_03' ''
								'E3128_04' ''
							}
							'E8101_06' ''
							'C401_07' {
								'E8457_01' ''
								'E8459_02' ''
								'E7130_03' ''
							}
							'C222_08' {
								'E8213_01' currVoyage?.vessel?.Vessel?.LloydsNumber
								'E1131_02' ''
								'E3055_03' ''
								'E8212_04' currVoyage?.vessel?.Vessel?.Name
								'E8453_05' ''
							}
							'E8281_09' ''
						}
						'DTM' {
							'C507_01' {
								'E2005_01' ''
								'E2380_02' ''
								'E2379_03' ''
							}
						}
						'TSR' {
							'C536_01' {
								'E4065_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
							'C233_02' {
								'E7273_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E7273_04' ''
								'E1131_05' ''
								'E3055_06' ''
							}
							'C537_03' {
								'E4219_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
							'C703_04' {
								'E7085_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
						}
						if (currVoyage?.VoyageNumber!="") {
							'RFF' {
								'C506_01' {
									'E1153_01' 'VON'
									'E1154_02' currVoyage?.VoyageNumber
									'E1156_03' ''
									'E4000_04' ''
									'E1060_05' ''
								}
							}
						}
						if (currVoyage?.service?.Service?.Code.isEmpty()==false) {
							'FTX' {
								'E4451_01' 'TRA'
								'E4453_02' ''
								'C107_03' {
									'E4441_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
								'C108_04' {
									'E4440_01' currVoyage?.service?.Service?.Code
									'E4440_02' currVoyage?.service?.Service?.Name
									'E4440_03' ''
									'E4440_04' ''
									'E4440_05' ''
								}
								'E3453_05' ''
								'E4447_06' ''
							}
						}
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
						}
						'QTY' {
							'C186_01' {
								'E6063_01' ''
								'E6060_02' ''
								'E6411_03' ''
							}
						}
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
						currVoyage?.stops?.Stop.each { currStop ->
							'Group5_LOC' {
								'LOC' {
									if (currStop?.stopDescription?.StopDescription?.AllowLoading == "Y" && currStop?.stopDescription?.StopDescription?.AllowDischarge=="Y")
										'E3227_01' '153'
									else if (currStop?.stopDescription?.StopDescription?.AllowLoading == "Y" && currStop?.stopDescription?.StopDescription?.AllowDischarge=="N")
										'E3227_01' '9'
									if (currStop?.stopDescription?.StopDescription?.AllowLoading == "N" && currStop?.stopDescription?.StopDescription?.AllowDischarge=="Y")
										'E3227_01' '11'
									'C517_02' {
										'E3225_01' currStop?.terminal?.Terminal?.PortCode
										'E1131_02' ''
										'E3055_03' ''
										'E3224_04' currStop?.terminal?.Terminal?.PortName
										'E3225_05' ''
									}
									'C519_03' {
										'E3223_01' currStop?.terminal?.Terminal?.TerminalCode
										'E1131_02' ''
										'E3055_03' ''
										'E3222_04' currStop?.terminal?.Terminal?.TerminalName
									}
									'C553_04' {
										'E3233_01' ''
										'E1131_02' ''
										'E3055_03' ''
										'E3232_04' ''
									}
									'E5479_05' ''
								}
								String eventName = "";
								String scheduleType = "";
								String e2005 = "";
								currStop?.events?.Event?.each { currEvent ->
									eventName = currEvent?.Name?.isEmpty() ? "" : currEvent?.Name;
									scheduleType = currVoyage?.ScheduleType?.isEmpty() ? "" : currVoyage?.ScheduleType;
									e2005 = "";
									if (scheduleType=="LongTerm") {
										if (eventName =="Long Term Berth Arrival")
											e2005 ="232"
										else if (eventName =="Long Term Berth Departure")
											e2005 ="189"
										else if (eventName =="Long Term Pilot Arrival")
											e2005 ="231"
									}
									else if (scheduleType=="Coastal") {
										if (eventName =="Coastal Berth Arrival")
											e2005 ="132"
										else if (eventName =="Coastal Berth Departure")
											e2005 ="133"
										else if (eventName =="Actual Berth Arrival")
											e2005 ="178"
										else if (eventName =="Actual Berth Departure")
											e2005 ="186"
										else if (eventName =="Long Term Berth Departure")
											e2005 ="189"
										else if (eventName =="Long Term Pilot Arrival")
											e2005 ="231"
										else if (eventName =="Long Term Berth Arrival")
											e2005 ="232"
										else if (eventName =="Actual Pilot Arrival")
											e2005 ="233"
										else if (eventName =="Coastal Pilot Arrival")
											e2005 ="252"
									}
									'DTM' {
										'C507_01' {
											'E2005_01' e2005;
											'E2380_02' util.convertDateTime(currEvent?.DateTime, "dd/MM/yyyy HH:mm:ss", 'yyyyMMddHHmm')
											'E2379_03' ''
										}
									}

								} //end of currStop?.events?.Event
								'RFF' {
									'C506_01' {
										'E1153_01' ''
										'E1154_02' ''
										'E1156_03' ''
										'E4000_04' ''
										'E1060_05' ''
									}
								}
								if (currStop?.stopDescription?.StopDescription?.Action?.isEmpty()==false) {
									'FTX' {
										'E4451_01' 'ACD'
										'E4453_02' ''
										'C107_03' {
											switch (currStop?.stopDescription?.StopDescription?.Action) {
												case ["ADD"]:
													'E4441_01' '201'
													break;
												case ["OMIT"]:
													'E4441_01' '100'
													break;
												default:
													break;
											}
											'E1131_02' ''
											'E3055_03' ''
										}
										'C108_04' {
											'E4440_01' ''
											'E4440_02' ''
											'E4440_03' ''
											'E4440_04' ''
											'E4440_05' ''
										}
										'E3453_05' ''
										'E4447_06' ''
									}

								}

							}
						} //end of each Stop

					}
					'Group6_NAD' {
						'NAD' {
							'E3035_01' 'CA'
							'C082_02' {
								'E3039_01' currVoyage?.carrier?.Carrier?.Name
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
						'LOC' {
							'E3227_01' ''
							'C517_02' {
								'E3225_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3224_04' ''
								'E3225_05' ''
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
						'Group7_CTA' {
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
					'Group8_GID' {
						'GID' {
							'E1496_01' ''
							'C213_02' {
								'E7224_01' ''
								'E7065_02' ''
								'E1131_03' ''
								'E3055_04' ''
								'E7064_05' ''
								'E7233_06' ''
							}
							'C213_03' {
								'E7224_01' ''
								'E7065_02' ''
								'E1131_03' ''
								'E3055_04' ''
								'E7064_05' ''
								'E7233_06' ''
							}
							'C213_04' {
								'E7224_01' ''
								'E7065_02' ''
								'E1131_03' ''
								'E3055_04' ''
								'E7064_05' ''
								'E7233_06' ''
							}
							'C213_05' {
								'E7224_01' ''
								'E7065_02' ''
								'E1131_03' ''
								'E3055_04' ''
								'E7064_05' ''
								'E7233_06' ''
							}
							'C213_06' {
								'E7224_01' ''
								'E7065_02' ''
								'E1131_03' ''
								'E3055_04' ''
								'E7064_05' ''
								'E7233_06' ''
							}
						}
						'HAN' {
							'C524_01' {
								'E4079_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E4078_04' ''
							}
							'C218_02' {
								'E7419_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E7418_04' ''
							}
						}
						'FTX' {
							'E4451_01' ''
							'E4453_02' ''
							'C107_03' {
								'E4441_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
							'C108_04' {
								'E4440_01' ''
								'E4440_02' ''
								'E4440_03' ''
								'E4440_04' ''
								'E4440_05' ''
							}
							'E3453_05' ''
							'E4447_06' ''
						}
						'Group9_GDS' {
							'GDS' {
								'C703_01' {
									'E7085_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
							}
							'FTX' {
								'E4451_01' ''
								'E4453_02' ''
								'C107_03' {
									'E4441_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
								'C108_04' {
									'E4440_01' ''
									'E4440_02' ''
									'E4440_03' ''
									'E4440_04' ''
									'E4440_05' ''
								}
								'E3453_05' ''
								'E4447_06' ''
							}
						}
						'Group10_MEA' {
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
							'EQN' {
								'C523_01' {
									'E6350_01' ''
									'E6353_02' ''
								}
							}
						}
						'Group11_DIM' {
							'DIM' {
								'E6145_01' ''
								'C211_02' {
									'E6411_01' ''
									'E6168_02' ''
									'E6140_03' ''
									'E6008_04' ''
								}
							}
							'EQN' {
								'C523_01' {
									'E6350_01' ''
									'E6353_02' ''
								}
							}
						}
						'Group12_DGS' {
							'DGS' {
								'E8273_01' ''
								'C205_02' {
									'E8351_01' ''
									'E8078_02' ''
									'E8092_03' ''
								}
								'C234_03' {
									'E7124_01' ''
									'E7088_02' ''
								}
								'C223_04' {
									'E7106_01' ''
									'E6411_02' ''
								}
								'E8339_05' ''
								'E8364_06' ''
								'E8410_07' ''
								'E8126_08' ''
								'C235_09' {
									'E8158_01' ''
									'E8186_02' ''
								}
								'C236_10' {
									'E8246_01' ''
									'E8246_02' ''
									'E8246_03' ''
								}
								'E8255_11' ''
								'E8325_12' ''
								'E8211_13' ''
							}
							'FTX' {
								'E4451_01' ''
								'E4453_02' ''
								'C107_03' {
									'E4441_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
								'C108_04' {
									'E4440_01' ''
									'E4440_02' ''
									'E4440_03' ''
									'E4440_04' ''
									'E4440_05' ''
								}
								'E3453_05' ''
								'E4447_06' ''
							}
						}
					}
					'UNT' {
						'E0074_01' 'TEST'
						'E0062_02' ' '
					}
				}
			}

		}
		
		
		return writer.toString()
	}


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

		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		/**
		 * Part III: read xml and prepare output xml
		 */
		XmlBeanParser parser = new XmlBeanParser()
		Message message = parser.xmlParser(cleanXml(inputXmlBody), Message.class)
		//println(inputXmlBody)
//		def message = new XmlSlurper().parseText(cleanXml(inputXmlBody));
//		assert message instanceof groovy.util.slurpersupport.GPathResult;


		


		////below remarks lines is a jaxb demo, not use now
		//ContainerMovement containerMovement = JAXB.unmarshal(new StringReader(inputXmlBody), ContainerMovement.class)

		/**
		 * Part IV: mapping script start from here
		 */
//		List pre_result = pre_validation(message)
//		Node bizKey = mapBizKey(message)

//		def iftsai = cleanXml(mapIFTSAI(message, TP_ID, conn))

		def iftsai = cleanXml(mapIFTSAI(message, TP_ID, conn))
//		List pos_result = post_validation(iftsai)
//		println(iftsai.toString().replaceAll("><",">\n<"));
		return iftsai.toString()

	}

	String cleanXml(String xml) {
		Node root = new XmlParser().parseText(xml)
		cleanNode(root)
		XmlUtil.serialize(root)
	}

	boolean cleanNode(Node node) {
		node.attributes().with { a ->
			a.findAll { !it.value }.each { a.remove(it.key) }
		}
		node.children().with { kids ->
			kids.findAll { it instanceof Node ? !cleanNode(it) : false }
					.each { kids.remove(it) }
		}
		node.attributes() || node.children() || node.text()
	}

	/**
	 * split the inputstring into lines with the given maxCharPerLine
	 * @param inputString
	 * @param maxCharPerLine
	 * @return
	 */

	List splitString(def inputObject, int maxCharPerLine) {
		String inputString = inputObject.toString()
		if (inputString == '') {
			return null
		}

		if (maxCharPerLine < 1) {
			return null
		}
		List lines = new LinkedList()
		while (util.notEmpty(inputString)) {
			if (inputString.length() > maxCharPerLine) {
				int splitPosistion = inputString.take(maxCharPerLine).toString().lastIndexOf(" ")
				if (splitPosistion != -1) {
					lines.add(inputString.take(splitPosistion))
					inputString = inputString.substring(splitPosistion + 1)
				} else {
					lines.add(inputString.take(maxCharPerLine))
				}
			} else {
				lines.add(inputString)
				inputString = ''
			}
		}
		lines
	}

	/**
	 * return the external reference type  for a given TP_ID , message type and interal reference type or the interal reference type if no result found
	 * @param TP_ID
	 * @param msgType
	 * @param internalCode
	 * @param conn
	 * @return external code or the inputted internal Code if externalCode not found
	 */
	//TP Specific
	String getExternalReferenceType(String TP_ID, String internalCode, Connection conn) {
		String ret = util.getEDICdeRef(TP_ID, 'Group3_ReferenceType', 'O', 'CS2XML', 'RFF', '01_1154', internalCode, conn)
		if (ret == '') {
			return internalCode
		} else {
			return ret
		}
	}

	/**
	 * return the external package code for a given TP_ID and internal package code or the internal package code if no result found
	 * @param TP_ID
	 * @param internalCode
	 * @param conn
	 * @return the externalPackagCode or if not found the internalCode inputed
	 */
	String getExternalPackageCode(String TP_ID, String internalCode, Connection conn) {
		getConversionByExtCdeWithDefault(TP_ID, 'PackageUnit', internalCode, internalCode, conn)
	}

	/**
	 * return the external container size type  for a given TP_ID and internal container size type or the internal container size type if no result found
	 * @param TP_ID
	 * @param internalCode
	 * @param conn
	 * @return external container size code or if not found the internalCode provided
	 */
	String getExternalConatinerSize(String TP_ID, String internalCode, Connection conn) {
		getConversionByExtCdeWithDefault(TP_ID, 'ContainerType', internalCode, internalCode, conn)
	}

	/**
	 *  return the CustomerCarrierCode for a given TP_ID and CSCompanyID
	 * @param TP_ID
	 * @param CSCompanyID
	 * @param conn
	 * @return the CustomerCarrierCode if found
	 */
	String getCCC(String TP_ID, String CSCompanyID, Connection conn) {
		getConversionByExtCde(TP_ID, 'CarrierCustomerCode', CSCompanyID, conn)
	}

	/**
	 * return the external party type  for a given TP_ID , message type and interal party type or the interal party type if no result found
	 * @param TP_ID
	 * @param internalCode
	 * @param conn
	 * @return the external Party Type or if not found the internalCode provided
	 */
	//TP Specific
	String getExternalPartyType(String TP_ID, String internalCode, Connection conn) {
		String ret = util.getEDICdeRef(TP_ID, 'Group10_NameAndAddress', 'O', 'CS2XML', 'NAD', '01_3035', internalCode, conn)
		if (ret == '') {
			return internalCode
		} else {
			return ret
		}
	}

	/**
	 * return the internal reference type for a given TP_ID , message type and external reference type or the external reference type if no result found
	 * @param TP_ID
	 * @param externalCode
	 * @param conn
	 * @return the internal reference type or if not found the externalcode provided
	 */
	String getInternalReferenceType(String TP_ID, String externalCode, Connection conn) {
		String ret = util.getEDICdeReffromIntCde(TP_ID, 'Group3_ReferenceType', 'O', 'CS2XML', 'RFF', '01_1154', externalCode, conn)
		if (ret == '') {
			return internalCode
		} else {
			return ret
		}
	}

	/**
	 *
	 * @param TP_ID
	 * @param externalcode
	 * @param conn
	 * @return
	 */
	String getInternalPackageType(String TP_ID, String externalcode, Connection conn) {
		getConversionByIntCdeWithDefault(TP_ID, 'PackageUnit', externalcode, externalcode, conn)
	}

	//fork from mappingUtil with modification
	String getConversionByExtCdeWithDefault(String TP_ID, String convertTypeId, String fromValue, String defaultValue, Connection conn) throws Exception {
		String ret = getConversionByExtCde(TP_ID, convertTypeId, fromValue, conn);
		if (ret == null || ret.length() == 0) {
			ret = defaultValue;
		}
		return ret;
	}

	//fork from mappingUtil with modification
	String getConversionByExtCde(String TP_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select int_cde from b2b_cde_conversion where tp_id=? and convert_type_id=? and ext_cde=?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, convertTypeId);
			pre.setString(3, fromValue);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	//fork from mappingUtil.getConversionWithDefault with modification
	String getConversionByIntCdeWithDefault(String TP_ID, String convertTypeId, String fromValue, String defaultValue, Connection conn) throws Exception {
		String ret = getConversionByExtCde(TP_ID, convertTypeId, fromValue, conn);
		if (ret == null || ret.length() == 0) {
			ret = defaultValue;
		}
		return ret;
	}
	//fork from mappingUtil.getConversion with modification
	String getConversionByIntCde(String TP_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where tp_id=? and convert_type_id=? and int_cde=?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(getDBRowLimit());
			pre.setQueryTimeout(getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, convertTypeId);
			pre.setString(3, fromValue);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	/**
	 * convert the date time in the xml with the outFormat
	 * for edifact
	 * 103 = 'yyyyMMdd'
	 * 203 = 'yyyyMMddHHss'
	 * @param xmlDateTime
	 * @param outFormat the output format, please refer to https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
	 * @return date time with the outFormat
	 */
	String convertXMLDateTime(def xmlDateTime, String outFormat) {
		Calendar dateTime = DatatypeConverter.parseDateTime(xmlDateTime.toString())
		SimpleDateFormat sfmt = new SimpleDateFormat(outFormat);
		sfmt.format(dateTime.getTime())
	}

	String constructContactNumber(String countryCode, String areaCode, String number) {
		String ret
		ret = ret + countryCode
		if (areaCode != null && areaCode != '') {
			ret = ret + '-' + areaCode
		}
		if (number != null && number != '') {
			ret = ret + '-' + number
		}
		ret
	}
}
