package cs.b2b.mapping.scripts

import cs.b2b.core.mapping.bean.ct.Header
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List;
import java.util.Map;

import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.Event
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * @author NICOLE
 * ABG CT initialize on 20161115 
 */
public class CUS_CS2CTXML_D08A_SOLVAYELM {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_CT_O_Common ctUtil = new cs.b2b.core.mapping.util.MappingUtil_CT_O_Common(util);

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
	def HHmmss = 'HHmmss'

	def currentSystemDt = null
	
	//Process Select SQL for main mapping and exception check
	public Map<String, String> sqlValue = new HashMap<String, String>();
	
	public void generateBody(Body current_Body, MarkupBuilder outXml , Header header) {

	def vCS1Event = current_Body.Event.CS1Event
		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
		def vCS1EventCodeConversion = util.getConversion('SOLVAYELM', 'CT', 'O', 'EventCdeCSIntVal', vCS1Event, conn)
		def shipDir =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
//		def shipDir=getShipDir('SONYDSG','Q2','O','ShipDirection',vCS1EventFirst5,'EventStatusCode',conn)
//		def currentSTCtrlNum = '###edi_Group_Ctrl_Number###' + String.format("%04d", current_BodyIndex+1)

		outXml.'Group_UNH' {
				'UNH' {
					'E0062_01' '-999999'
					'S009_02' {
						'E0065_01' 'IFTSTA'
						'E0052_02' 'D'
						'E0054_03' '96A'
						'E0051_04' 'UN'
//						'E0057_05' ''
					}
//					'E0068_03' ''
//					'S010_04' {
//						'E0070_01' ''
//						'E0073_02' ''
//					}
				}
				'BGM' {
					'C002_01' {
						'E1001_01' '23'
//						'E1131_02' ''
//						'E3055_03' ''
//						'E1000_04' ''
					}

					'C106_02' {
						'E1004_01' 'CS'+ new Date().format('yyyyMMddHHmmss')
//						'E1056_02' ''
//						'E1060_03' ''
					}
					'E1225_03' '9'
//					'E4343_04' ''
				}
			if(header?.MsgDT?.LocDT){
				'DTM' {
					'C507_01' {
						'E2005_01' '137'
						'E2380_02' util.convertXmlDateTime(header?.MsgDT?.LocDT,yyyyMMdd)
						'E2379_03' '102'
					}
				}
				'DTM' {
					'C507_01' {
						'E2005_01' '137'
						'E2380_02' util.convertXmlDateTime(header?.MsgDT?.LocDT,HHmmss)
						'E2379_03' '402'
					}
				}
			}

//				'TSR' {
//					'C536_01' {
//						'E4065_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//					}
//					'C233_02' {
//						'E7273_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//						'E7273_04' ''
//						'E1131_05' ''
//						'E3055_06' ''
//					}
//					'C537_03' {
//						'E4219_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//					}
//					'C703_04' {
//						'E7085_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//					}
//				}
				Map< String , String> PatyTypeMap = new HashMap < String , String>();
				PatyTypeMap.put('FWD','FW')
				PatyTypeMap.put('OTH','MS')
				PatyTypeMap.put('SHP','CZ')
			current_Body?.Party?.findAll {StringUtil.isEmpty(it?.CSCompanyID) && StringUtil.isNotEmpty(PatyTypeMap.get(it?.PartyType))}?.each { currentParty ->
				'Group1_NAD' {
						'NAD' {
							'E3035_01' PatyTypeMap.get(currentParty?.PartyType)
							'C082_02' {
								'E3039_01' currentParty?.CarrierCustomerCode
//								'E1131_02' ''
//								'E3055_03' ''
							}
//							'C058_03' {
//								'E3124_01' ''
//								'E3124_02' ''
//								'E3124_03' ''
//								'E3124_04' ''
//								'E3124_05' ''
//							}
							'C080_04' {
								if (StringUtil.isNotEmpty(currentParty?.PartyName)) {
									'E3036_01' util.substring(currentParty?.PartyName, 1, 35)
								}
								if (StringUtil.isNotEmpty(util.substring(currentParty?.PartyName, 36, 35))) {
									'E3036_02' util.substring(currentParty?.PartyName, 36, 35)
								}
//								'E3036_03' ''
//								'E3036_04' ''
//								'E3036_05' ''
//								'E3045_06' ''
							}
//							'C059_05' {
//								'E3042_01' ''
//								'E3042_02' ''
//								'E3042_03' ''
//								'E3042_04' ''
//							}
//							'E3164_06' ''
//							'C819_07' {
//								'E3229_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E3228_04' ''
//							}
//							'E3251_08' ''
//							'E3207_09' ''
						}
//					'Group2_CTA' {
//						'CTA' {
//							'E3139_01' 'IC'
////							'C056_02' {
////								'E3413_01' ''
////								'E3412_02' ''
////							}
//						}
////						'COM' {
////							'C076_01' {
////								'E3148_01' ''
////								'E3155_02' ''
////							}
////						}
//					}
					}
				}
			'Group1_NAD' {
				'NAD' {
					'E3035_01' 'MS'
					'C082_02' {
						'E3039_01' '300643'
//								'E1131_02' ''
//								'E3055_03' ''
					}
//							'C058_03' {
//								'E3124_01' ''
//								'E3124_02' ''
//								'E3124_03' ''
//								'E3124_04' ''
//								'E3124_05' ''
//							}
					'C080_04' {
						'E3036_01' 'ORIENT OVERSEAS CONTAINER LINES'
//								'E3036_02' ''
//								'E3036_03' ''
//								'E3036_04' ''
//								'E3036_05' ''
//								'E3045_06' ''
					}
//							'C059_05' {
//								'E3042_01' ''
//								'E3042_02' ''
//								'E3042_03' ''
//								'E3042_04' ''
//							}
//							'E3164_06' ''
//							'C819_07' {
//								'E3229_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E3228_04' ''
//							}
//							'E3251_08' ''
//							'E3207_09' ''
				}
//				'Group2_CTA' {
//					'CTA' {
//						'E3139_01' 'IC'
////							'C056_02' {
////								'E3413_01' ''
////								'E3412_02' ''
////							}
//					}
////						'COM' {
////							'C076_01' {
////								'E3148_01' ''
////								'E3155_02' ''
////							}
////						}
//				}
			}
			current_Body?.BookingGeneralInfo?.each { currentBGI ->
				'Group3_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' 'BN'
							if (currentBGI?.CarrierBookingNumber) {
								'E1154_02' currentBGI?.CarrierBookingNumber
							}
//								'E1156_03' ''
//								'E1056_04' ''
//								'E1060_05' ''
						}
					}

				}
			}

					current_Body?.ExternalReference?.findAll{['BN','SR'].contains(it?.CSReferenceType)}?.each { currentExtRef ->
						'Group3_RFF' {
							if(currentExtRef?.CSReferenceType=='SR'){
							'RFF'
									{
								'C506_01' {
									'E1153_01' 'SI'
									'E1154_02' currentExtRef?.ReferenceNumber
//								'E1156_03' ''
//								'E1056_04' ''
//								'E1060_05' ''
								}
							}
							}else{
							'RFF'
									{
								'C506_01' {
									'E1153_01' 'BN'
									'E1154_02' currentExtRef?.ReferenceNumber
//								'E1156_03' ''
//								'E1056_04' ''
//								'E1060_05' ''
								}
							}
						}
					}
						}

//					'DTM' {
//						'C507_01' {
//							'E2005_01' ''
//							'E2380_02' ''
//							'E2379_03' ''
//						}
//					}

//				'LOC' {
//					'E3227_01' ''
//					'C517_02' {
//						'E3225_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//						'E3224_04' ''
//					}
//					'C519_03' {
//						'E3223_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//						'E3222_04' ''
//					}
//					'C553_04' {
//						'E3233_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//						'E3232_04' ''
//					}
//					'E5479_05' ''
//				}
//				'FTX' {
//					'E4451_01' ''
//					'E4453_02' ''
//					'C107_03' {
//						'E4441_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//					}
//					'C108_04' {
//						'E4440_01' ''
//						'E4440_02' ''
//						'E4440_03' ''
//						'E4440_04' ''
//						'E4440_05' ''
//					}
//					'E3453_05' ''
//					'E4447_06' ''
//				}
//				'CNT' {
//					'C270_01' {
//						'E6069_01' ''
//						'E6066_02' ''
//						'E6411_03' ''
//					}
//				}
				'Group4_CNI' {
					'CNI' {
						'E1490_01' '1'
//						'C503_02' {
//							'E1004_01' ''
//							'E1373_02' ''
//							'E1366_03' ''
//							'E3453_04' ''
//							'E1056_05' ''
//							'E1060_06' ''
//						}
//						'E1312_03' ''
					}
//					'LOC' {
//						'E3227_01' ''
//						'C517_02' {
//							'E3225_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E3224_04' ''
//						}
//						'C519_03' {
//							'E3223_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E3222_04' ''
//						}
//						'C553_04' {
//							'E3233_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E3232_04' ''
//						}
//						'E5479_05' ''
//					}
//					'CNT' {
//						'C270_01' {
//							'E6069_01' ''
//							'E6066_02' ''
//							'E6411_03' ''
//						}
//					}
					'Group5_STS' {
						'STS' {
							'C601_01' {
								'E9015_01' '1'
//								'E1131_02' ''
//								'E3055_03' ''
							}
							'C555_02' {
								if(vCS1EventCodeConversion){
									'E4405_01' vCS1EventCodeConversion
								}
//								'E1131_02' ''
//								'E3055_03' ''
//								'E4404_04' ''
							}
//							'C556_03' {
//								'E9013_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E9012_04' ''
//							}
//							'C556_04' {
//								'E9013_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E9012_04' ''
//							}
//							'C556_05' {
//								'E9013_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E9012_04' ''
//							}
//							'C556_06' {
//								'E9013_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E9012_04' ''
//							}
//							'C556_07' {
//								'E9013_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E9012_04' ''
//							}
						}
						current_Body?.BLGeneralInfo?.findAll{it}?.each{ currentBLG ->
							'RFF' {
								'C506_01' {
									'E1153_01' 'BM'
									if(StringUtil.isNotEmpty(currentBLG?.BLNumber)){
										'E1154_02' currentBLG?.BLNumber
									}
//									'E1156_03' ''
//									'E1056_04' ''
//									'E1060_05' ''
								}
							}
						}
						current_Body?.BLGeneralInfo[0]?.findAll{it?.BLIssueDT?.LocDT}?.each { currentBLG ->
							'DTM' {
								'C507_01' {
									'E2005_01' '95'
									'E2380_02' util.convertDateTime(currentBLG,xmlDateTimeFormat,yyyyMMdd)
									'E2379_03' '102'
								}
							}
						}
						if(current_Body?.Event?.EventDT?.LocDT){
							'DTM' {
								'C507_01' {
									'E2005_01' '334'
									'E2380_02' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT,xmlDateTimeFormat,yyyyMMdd)
									'E2379_03' '102'
								}
							}
							'DTM' {
								'C507_01' {
									'E2005_01' '334'
									'E2380_02' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT,xmlDateTimeFormat,HHmmss)
									'E2379_03' '402'
								}
							}
						}

//						'DOC' {
//							'C002_01' {
//								'E1001_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E1000_04' ''
//							}
//							'C503_02' {
//								'E1004_01' ''
//								'E1373_02' ''
//								'E1366_03' ''
//								'E3453_04' ''
//								'E1056_05' ''
//								'E1060_06' ''
//							}
//							'E3153_03' ''
//							'E1220_04' ''
//							'E1218_05' ''
//						}
//						'FTX' {
//							'E4451_01' ''
//							'E4453_02' ''
//							'C107_03' {
//								'E4441_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//							}
//							'C108_04' {
//								'E4440_01' ''
//								'E4440_02' ''
//								'E4440_03' ''
//								'E4440_04' ''
//								'E4440_05' ''
//							}
//							'E3453_05' ''
//							'E4447_06' ''
//						}
//						'Group6_NAD' {
//							'NAD' {
//								'E3035_01' ''
//								'C082_02' {
//									'E3039_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//								'C058_03' {
//									'E3124_01' ''
//									'E3124_02' ''
//									'E3124_03' ''
//									'E3124_04' ''
//									'E3124_05' ''
//								}
//								'C080_04' {
//									'E3036_01' ''
//									'E3036_02' ''
//									'E3036_03' ''
//									'E3036_04' ''
//									'E3036_05' ''
//									'E3045_06' ''
//								}
//								'C059_05' {
//									'E3042_01' ''
//									'E3042_02' ''
//									'E3042_03' ''
//									'E3042_04' ''
//								}
//								'E3164_06' ''
//								'C819_07' {
//									'E3229_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E3228_04' ''
//								}
//								'E3251_08' ''
//								'E3207_09' ''
//							}
//							'Group7_CTA' {
//								'CTA' {
//									'E3139_01' ''
//									'C056_02' {
//										'E3413_01' ''
//										'E3412_02' ''
//									}
//								}
//								'COM' {
//									'C076_01' {
//										'E3148_01' ''
//										'E3155_02' ''
//									}
//								}
//							}
//						}
//						'LOC' {
//							'E3227_01' ''
//							'C517_02' {
//								'E3225_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E3224_04' ''
//							}
//							'C519_03' {
//								'E3223_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E3222_04' ''
//							}
//							'C553_04' {
//								'E3233_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E3232_04' ''
//							}
//							'E5479_05' ''
//						}
//						'PCI' {
//							'E4233_01' ''
//							'C210_02' {
//								'E7102_01' ''
//								'E7102_02' ''
//								'E7102_03' ''
//								'E7102_04' ''
//								'E7102_05' ''
//								'E7102_06' ''
//								'E7102_07' ''
//								'E7102_08' ''
//								'E7102_09' ''
//								'E7102_10' ''
//							}
//							'E8169_03' ''
//							'C827_04' {
//								'E7511_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//							}
//						}
//						'Group8_TDT' {
//							'TDT' {
//								'E8051_01' ''
//								'E8028_02' ''
//								'C220_03' {
//									'E8067_01' ''
//									'E8066_02' ''
//								}
//								'C001_04' {
//									'E8179_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E8178_04' ''
//								}
//								'C040_05' {
//									'E3127_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E3126_04' ''
//								}
//								'E8101_06' ''
//								'C401_07' {
//									'E8457_01' ''
//									'E8459_02' ''
//									'E7130_03' ''
//								}
//								'C222_08' {
//									'E8213_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E8212_04' ''
//									'E8453_05' ''
//								}
//								'E8281_09' ''
//							}
//							'DTM' {
//								'C507_01' {
//									'E2005_01' ''
//									'E2380_02' ''
//									'E2379_03' ''
//								}
//							}
//							'RFF' {
//								'C506_01' {
//									'E1153_01' ''
//									'E1154_02' ''
//									'E1156_03' ''
//									'E1056_04' ''
//									'E1060_05' ''
//								}
//							}
//							'Group9_LOC' {
//								'LOC' {
//									'E3227_01' ''
//									'C517_02' {
//										'E3225_01' ''
//										'E1131_02' ''
//										'E3055_03' ''
//										'E3224_04' ''
//									}
//									'C519_03' {
//										'E3223_01' ''
//										'E1131_02' ''
//										'E3055_03' ''
//										'E3222_04' ''
//									}
//									'C553_04' {
//										'E3233_01' ''
//										'E1131_02' ''
//										'E3055_03' ''
//										'E3232_04' ''
//									}
//									'E5479_05' ''
//								}
//								'DTM' {
//									'C507_01' {
//										'E2005_01' ''
//										'E2380_02' ''
//										'E2379_03' ''
//									}
//								}
//							}
//						}
//						'Group10_EQD' {
//							'EQD' {
//								'E8053_01' ''
//								'C237_02' {
//									'E8260_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E3207_04' ''
//								}
//								'C224_03' {
//									'E8155_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E8154_04' ''
//								}
//								'E8077_04' ''
//								'E8249_05' ''
//								'E8169_06' ''
//								'E4233_07' ''
//							}
//							'MEA' {
//								'E6311_01' ''
//								'C502_02' {
//									'E6313_01' ''
//									'E6321_02' ''
//									'E6155_03' ''
//									'E6154_04' ''
//								}
//								'C174_03' {
//									'E6411_01' ''
//									'E6314_02' ''
//									'E6162_03' ''
//									'E6152_04' ''
//									'E6432_05' ''
//								}
//								'E7383_04' ''
//							}
//							'DIM' {
//								'E6145_01' ''
//								'C211_02' {
//									'E6411_01' ''
//									'E6168_02' ''
//									'E6140_03' ''
//									'E6008_04' ''
//								}
//							}
//							'SEL' {
//								'E9308_01' ''
//								'C215_02' {
//									'E9303_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E9302_04' ''
//								}
//								'E4517_03' ''
//								'C208_04' {
//									'E7402_01' ''
//									'E7402_02' ''
//								}
//								'E4525_05' ''
//							}
//							'RFF' {
//								'C506_01' {
//									'E1153_01' ''
//									'E1154_02' ''
//									'E1156_03' ''
//									'E1056_04' ''
//									'E1060_05' ''
//								}
//							}
//							'TPL' {
//								'C222_01' {
//									'E8213_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E8212_04' ''
//									'E8453_05' ''
//								}
//							}
//							'TMD' {
//								'C219_01' {
//									'E8335_01' ''
//									'E8334_02' ''
//								}
//								'E8332_02' ''
//								'E8341_03' ''
//							}
//							'Group11_LOC' {
//								'LOC' {
//									'E3227_01' ''
//									'C517_02' {
//										'E3225_01' ''
//										'E1131_02' ''
//										'E3055_03' ''
//										'E3224_04' ''
//									}
//									'C519_03' {
//										'E3223_01' ''
//										'E1131_02' ''
//										'E3055_03' ''
//										'E3222_04' ''
//									}
//									'C553_04' {
//										'E3233_01' ''
//										'E1131_02' ''
//										'E3055_03' ''
//										'E3232_04' ''
//									}
//									'E5479_05' ''
//								}
//								'DTM' {
//									'C507_01' {
//										'E2005_01' ''
//										'E2380_02' ''
//										'E2379_03' ''
//									}
//								}
//							}
//							'Group12_EQA' {
//								'EQA' {
//									'E8053_01' ''
//									'C237_02' {
//										'E8260_01' ''
//										'E1131_02' ''
//										'E3055_03' ''
//										'E3207_04' ''
//									}
//								}
//								'SEL' {
//									'E9308_01' ''
//									'C215_02' {
//										'E9303_01' ''
//										'E1131_02' ''
//										'E3055_03' ''
//										'E9302_04' ''
//									}
//									'E4517_03' ''
//									'C208_04' {
//										'E7402_01' ''
//										'E7402_02' ''
//									}
//									'E4525_05' ''
//								}
//								'Group13_LOC' {
//									'LOC' {
//										'E3227_01' ''
//										'C517_02' {
//											'E3225_01' ''
//											'E1131_02' ''
//											'E3055_03' ''
//											'E3224_04' ''
//										}
//										'C519_03' {
//											'E3223_01' ''
//											'E1131_02' ''
//											'E3055_03' ''
//											'E3222_04' ''
//										}
//										'C553_04' {
//											'E3233_01' ''
//											'E1131_02' ''
//											'E3055_03' ''
//											'E3232_04' ''
//										}
//										'E5479_05' ''
//									}
//									'DTM' {
//										'C507_01' {
//											'E2005_01' ''
//											'E2380_02' ''
//											'E2379_03' ''
//										}
//									}
//								}
//							}
//						}
//						'Group14_GID' {
//							'GID' {
//								'E1496_01' ''
//								'C213_02' {
//									'E7224_01' ''
//									'E7065_02' ''
//									'E1131_03' ''
//									'E3055_04' ''
//									'E7064_05' ''
//									'E7233_06' ''
//								}
//								'C213_03' {
//									'E7224_01' ''
//									'E7065_02' ''
//									'E1131_03' ''
//									'E3055_04' ''
//									'E7064_05' ''
//									'E7233_06' ''
//								}
//								'C213_04' {
//									'E7224_01' ''
//									'E7065_02' ''
//									'E1131_03' ''
//									'E3055_04' ''
//									'E7064_05' ''
//									'E7233_06' ''
//								}
//								'C213_05' {
//									'E7224_01' ''
//									'E7065_02' ''
//									'E1131_03' ''
//									'E3055_04' ''
//									'E7064_05' ''
//									'E7233_06' ''
//								}
//								'C213_06' {
//									'E7224_01' ''
//									'E7065_02' ''
//									'E1131_03' ''
//									'E3055_04' ''
//									'E7064_05' ''
//									'E7233_06' ''
//								}
//							}
//							'HAN' {
//								'C524_01' {
//									'E4079_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E4078_04' ''
//								}
//								'C218_02' {
//									'E7419_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E7418_04' ''
//								}
//							}
//							'SGP' {
//								'C237_01' {
//									'E8260_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E3207_04' ''
//								}
//								'E7224_02' ''
//							}
//							'DGS' {
//								'E8273_01' ''
//								'C205_02' {
//									'E8351_01' ''
//									'E8078_02' ''
//									'E8092_03' ''
//								}
//								'C234_03' {
//									'E7124_01' ''
//									'E7088_02' ''
//								}
//								'C223_04' {
//									'E7106_01' ''
//									'E6411_02' ''
//								}
//								'E8339_05' ''
//								'E8364_06' ''
//								'E8410_07' ''
//								'E8126_08' ''
//								'C235_09' {
//									'E8158_01' ''
//									'E8186_02' ''
//								}
//								'C236_10' {
//									'E8246_01' ''
//									'E8246_02' ''
//									'E8246_03' ''
//									'E8246_04' ''
//								}
//								'E8255_11' ''
//								'E8179_12' ''
//								'E8211_13' ''
//							}
//							'FTX' {
//								'E4451_01' ''
//								'E4453_02' ''
//								'C107_03' {
//									'E4441_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//								'C108_04' {
//									'E4440_01' ''
//									'E4440_02' ''
//									'E4440_03' ''
//									'E4440_04' ''
//									'E4440_05' ''
//								}
//								'E3453_05' ''
//								'E4447_06' ''
//							}
//							'GDS' {
//								'C703_01' {
//									'E7085_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//							}
//							'LOC' {
//								'E3227_01' ''
//								'C517_02' {
//									'E3225_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E3224_04' ''
//								}
//								'C519_03' {
//									'E3223_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E3222_04' ''
//								}
//								'C553_04' {
//									'E3233_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E3232_04' ''
//								}
//								'E5479_05' ''
//							}
//							'QTY' {
//								'C186_01' {
//									'E6063_01' ''
//									'E6060_02' ''
//									'E6411_03' ''
//								}
//							}
//							'Group15_MEA' {
//								'MEA' {
//									'E6311_01' ''
//									'C502_02' {
//										'E6313_01' ''
//										'E6321_02' ''
//										'E6155_03' ''
//										'E6154_04' ''
//									}
//									'C174_03' {
//										'E6411_01' ''
//										'E6314_02' ''
//										'E6162_03' ''
//										'E6152_04' ''
//										'E6432_05' ''
//									}
//									'E7383_04' ''
//								}
//								'EQN' {
//									'C523_01' {
//										'E6350_01' ''
//										'E6353_02' ''
//									}
//								}
//							}
//							'Group16_DIM' {
//								'DIM' {
//									'E6145_01' ''
//									'C211_02' {
//										'E6411_01' ''
//										'E6168_02' ''
//										'E6140_03' ''
//										'E6008_04' ''
//									}
//								}
//								'EQN' {
//									'C523_01' {
//										'E6350_01' ''
//										'E6353_02' ''
//									}
//								}
//							}
//							'Group17_PCI' {
//								'PCI' {
//									'E4233_01' ''
//									'C210_02' {
//										'E7102_01' ''
//										'E7102_02' ''
//										'E7102_03' ''
//										'E7102_04' ''
//										'E7102_05' ''
//										'E7102_06' ''
//										'E7102_07' ''
//										'E7102_08' ''
//										'E7102_09' ''
//										'E7102_10' ''
//									}
//									'E8169_03' ''
//									'C827_04' {
//										'E7511_01' ''
//										'E1131_02' ''
//										'E3055_03' ''
//									}
//								}
//								'GIN' {
//									'E7405_01' ''
//									'C208_02' {
//										'E7402_01' ''
//										'E7402_02' ''
//									}
//									'C208_03' {
//										'E7402_01' ''
//										'E7402_02' ''
//									}
//									'C208_04' {
//										'E7402_01' ''
//										'E7402_02' ''
//									}
//									'C208_05' {
//										'E7402_01' ''
//										'E7402_02' ''
//									}
//									'C208_06' {
//										'E7402_01' ''
//										'E7402_02' ''
//									}
//								}
//							}
//							'Group18_RFF' {
//								'RFF' {
//									'C506_01' {
//										'E1153_01' ''
//										'E1154_02' ''
//										'E1156_03' ''
//										'E1056_04' ''
//										'E1060_05' ''
//									}
//								}
//								'DTM' {
//									'C507_01' {
//										'E2005_01' ''
//										'E2380_02' ''
//										'E2379_03' ''
//									}
//								}
//							}
//						}
					}
				}
			'UNT' {
				'E0074_01' '-99999'
				'E0062_02' '-99999'
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
//		def parser = new XmlParser()
//		parser.setNamespaceAware(false);
//		def ContainerMovement = parser.parseText(inputXmlBody);
		
		XmlBeanParser parser = new XmlBeanParser()
		ContainerMovement ct = parser.xmlParser(inputXmlBody, ContainerMovement.class)
	
		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def bizKeyWriter = new StringWriter();
		def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeyWriter), "", false));
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def csuploadWriter = new StringWriter();
		def csuploadXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(csuploadWriter), "", false));
		csuploadXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")


		/**
		 * Part IV: mapping script start from here
		 */

		//create root node
		def IFTSTA = outXml.createNode('IFTSTA')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.


		//BeginWorkFlow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def outputBodyCount = 0
		currentSystemDt = new Date()
		//def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
		def headerMsgDT = util.convertDateTime(ct.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		//long ediIsaCtrlNumber = ctrlNos[0]
		//long ediGroupCtrlNum = ctrlNos[1]
		def txnErrorKeys = []
		
		//build ISA, GS
//		generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)
		
		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
				
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

		//start body looping
		ct.Body.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('SOLVAYELM', 'CT', 'O', 'EventCdeCSIntVal', eventCS2Cde, conn)

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

			if (errorKeyList.size() == 0) {
				//pass validateBeforeExecution
				//main mapping
				generateBody(current_Body, outXml , ct.Header)
				outputBodyCount++
			}

			//posp checking
			pospValidation()

			ctUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, eventExtCde, TP_ID, conn)

			txnErrorKeys.add(errorKeyList);
		}

		//EndWorkFlow
		
		//build GE, IEA
//		generateEDITail(outXml, outputBodyCount)

		//End root node
		outXml.nodeCompleted(null,IFTSTA)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		println bizKeyWriter.toString();
//		println csuploadWriter.toString();

		//promote csupload and bizkey to session
		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID);
		if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);
		
		
		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = writer?.toString()
//			print('***********'+result);
		}
		
		
		
		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()
		
		  return result;

		
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
//		//				//R4 R Tibco R4/EventPOR/Type8
//		def flag
//		if (current_Body.Route?.POR?.CSStandardCity?.CSCountryCode?.length() >= 2){
//			flag=current_Body.Route?.POR?.CSStandardCity?.CSCountryCode
//		}else if(current_Body.Route?.POR?.CityDetails?.Country?.length()>0){
//			flag= sqlValue.get("R4R05")
//		}
//
//		// error cases
//		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, '- Event not subscribed by Partner', errorKeyList)
//		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event?.EventDT?.LocDT, true, null, errorKeyList)
//		//R4 5
//		ctUtil.missingEventCodeLoc(eventCS2Cde,current_Body.Event, true, null, errorKeyList)
//		ctUtil.missingEventFNDCodeLoc(eventCS2Cde, current_Body.Event,current_Body.Route?.FND, true, null, errorKeyList)
////				//B4 D
//		ctUtil.missingPODCountryCode(eventCS2Cde, current_Body.Route?.lastPOD, true, null, errorKeyList)
//		ctUtil.missingPODLocationCode(eventCS2Cde, current_Body.Route?.LastPOD?.Port?.LocationCode, true, null, errorKeyList)
//		ctUtil.missingPODLocationName(eventCS2Cde, current_Body.Route?.LastPOD, true, null, errorKeyList)
//		ctUtil.missingPODLocationQual(eventCS2Cde, current_Body.Route?.LastPOD, true, null, errorKeyList)
////				//B4 L
//		ctUtil.missingPOLCountryCode(eventCS2Cde, current_Body.Route?.FirstPOL, true, null, errorKeyList)
//		ctUtil.missingPOLLocationCode(eventCS2Cde, current_Body.Route?.FirstPOL?.Port?.LocationCode, true, null, errorKeyList)
//		ctUtil.missingPOLLocationName(eventCS2Cde, current_Body.Route?.FirstPOL, true, null, errorKeyList)
//		ctUtil.missingPOLLocationQual(eventCS2Cde, current_Body.Route?.FirstPOL, true, null, errorKeyList)
////				//R4 R
//		ctUtil.missingPORCountryCode(eventCS2Cde, current_Body.Route?.POR, flag, true, null, errorKeyList)
//		ctUtil.missingPORLocationName(eventCS2Cde, current_Body.Route?.POR,true, null, errorKeyList)
//		ctUtil.missingPORLocationQual(eventCS2Cde, current_Body.Route?.POR, true, null, errorKeyList)
//
//		// obsolete cases
//		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route?.Inbound_intermodal_indicator, false, null, errorKeyList)
//		ctUtil.EventNotSub(eventCS2Cde, eventExtCde, false, null, errorKeyList)

		return errorKeyList;
	}
	private static final String YES = 'YES'
	private static final String NO = 'NO'
	private static final String C = 'C'
	private static final String ERROR_SUPPORT = 'ES'
	private static final String ERROR_COMPLETE = 'EC'
	public void EventNotSub(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(eventExtCde)&&eventExtCde=="XX") {
			errorKey = [TYPE: isError? ERROR_SUPPORT : C, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg: eventCS2Cde + ' - EVT_NOT_SUB']
			errorKeyList.add(errorKey)
		}
	}

	public void missingEventCodeLoc(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Event.Location?.LocationName)&&StringUtil.isEmpty(Event.Location?.CityDetails?.City)&&eventCS2Cde!='CS180'&&StringUtil.isEmpty(Event.Location?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(Event.Location?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(Event.Location?.LocationCode?.SchedKDType))
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event Code and Location']
			errorKeyList.add(errorKey)
		}
	}
	public void missingEventFNDCodeLoc(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Event.Location?.LocationName)&&StringUtil.isEmpty(Event.Location?.CityDetails?.City)&&eventCS2Cde=='CS180'&&StringUtil.isEmpty(Event.Location?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(Event.Location?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.UNLocationCode)&&StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(FND?.CityDetails?.LocationCode?.SchedKDType)&&StringUtil.isEmpty(FND?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Event and FND Location Code and Name']
			errorKeyList.add(errorKey)
		}
	}
	void missingPODCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LastPOD?.Port?.CSCountryCode)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPODLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing POD Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingPODLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LastPOD?.Port?.PortName)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + ' -Missing POD Location Name']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingPODLocationQual(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.LastPOD LastPOD, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.UNLocationCode)&&StringUtil.isNotEmpty(LastPOD?.Port?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(LastPOD?.Port?.LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POD Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}
	void missingFNDCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CSStandardCity?.CSContinentCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingFNDLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode?.UNLocationCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingFNDLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FND FND, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FND?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing FND Location Name']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL , boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FirstPOL?.Port?.CSCountryCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPOLLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FirstPOL?.Port?.PortName)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POL Location Name']
			errorKeyList.add(errorKey)
		}
	}

	void missingPOLLocationQual(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.FirstPOL FirstPOL, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.UNLocationCode)&&StringUtil.isNotEmpty(FirstPOL?.Port?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(FirstPOL?.Port?.LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POL Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingPORCountryCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR,String flag, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(POR?.CSStandardCity?.CSCountryCode)&&StringUtil.isEmpty(flag)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Country Code']
			errorKeyList.add(errorKey)
		}
	}


	void missingPORLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.LocationCode LocationCode, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(LocationCode?.UNLocationCode) && StringUtil.isEmpty(LocationCode?.SchedKDCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Code for']
			errorKeyList.add(errorKey)
		}
	}


	void missingPORLocationName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(POR?.CityDetails?.City)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO,VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '- Missing POR Location Name']
			errorKeyList.add(errorKey)
		}
	}
	void missingPORLocationQual(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.POR POR, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.UNLocationCode)&&StringUtil.isNotEmpty(POR?.CityDetails?.LocationCode?.SchedKDCode)&&StringUtil.isEmpty(POR?.CityDetails?.LocationCode?.SchedKDType)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg !=null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing POR Location Qualifier']
			errorKeyList.add(errorKey)
		}
	}
	public boolean pospValidation() {

	}
	
	private void generateEDIHeader(MarkupBuilder outXml, def appSessionId, ContainerMovement ContainerMovement, def currentSystemDt) {
		//		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID.text());
				cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement?.Header?.InterchangeMessageID);
		//		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC?.text());
				cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC);
		
				outXml.'ISA' {
					'I01_01' '00'
					'I02_02' '          '
					'I03_03' '00'
					'I04_04' '          '
					'I05_05' 'ZZ'
					'I06_06' 'CARGOSMART     '
					'I05_07' 'ZZ'
					'I07_08' 'CARGOSMARTABG  '
					'I08_09' currentSystemDt.format("yyMMdd")
					'I09_10' currentSystemDt.format(HHmm)
					'I10_11' 'U'
					'I11_12' '00401'
					'I12_13' '1'
					'I13_14' '0'
					'I14_15' 'P'
					'I15_16' '>'
				}
				outXml.'GS' {
					'E479_01' 'QO'
					'E142_02' 'CARGOSMART'
					'E124_03' 'CARGOSMARTABG'
					'E373_04' currentSystemDt.format(yyyyMMdd)
					'E337_05' currentSystemDt.format(HHmm)
					'E28_06' '1'
					'E455_07' 'X'
					'E480_08' '004010'
				}
		
			}
	
	//add by Harry
	private void generateEDITail(MarkupBuilder outXml, def outputBodyCount) {
		outXml.'GE' {
			'E97_01' outputBodyCount
			'E28_02' '1'
		}
		outXml.'IEA' {
			'I16_01' '1'
			'I12_02' '1'
		}
	}
	
	public String getShipDir(String TP_ID, String Seg_ID, String DIR_ID, String convertTypeId, String Int_Cde,String Seg_Num,  Connection conn) throws Exception {
		if (conn == null)
		return "";
		
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_edi_cde_ref where tp_id=? and dir_id =? and  CONVERT_TYPE_ID =? and seg_id=? and seg_num=? and int_cde =?";
		
		try {
		pre = conn.prepareStatement(sql);
		pre.setMaxRows(util.getDBRowLimit());
		pre.setQueryTimeout(util.getDBTimeOutInSeconds());
		pre.setString(1, TP_ID);
		pre.setString(2, DIR_ID);
		pre.setString(3, convertTypeId);
		pre.setString(4, Seg_ID);
		pre.setString(5, Seg_Num);
		pre.setString(6, Int_Cde);
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
	
	public String getCS2MasterCity(String City_Name){
		if (conn == null)
			return "";
			
			String ret = ""; 
			PreparedStatement pre = null;
			ResultSet result = null;
			String sql = "select distinct cntry_cde  from cs2_master_city where  UPPER(CNTRY_NME) =UPPER(?) ";
			
			try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, City_Name);
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

}






