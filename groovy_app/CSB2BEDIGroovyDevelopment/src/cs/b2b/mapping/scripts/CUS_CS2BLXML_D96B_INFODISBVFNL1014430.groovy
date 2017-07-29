package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.bean.bl.FreightCharge
import cs.b2b.core.mapping.bean.bl.FreightChargeCNTR
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection

public class CUS_CS2BLXML_D96B_INFODISBVFNL1014430 {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_BL_O_Common blUtil = new cs.b2b.core.mapping.util.MappingUtil_BL_O_Common(util);

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

	BigDecimal sum = null
	BigDecimal hundred = new BigDecimal ('100');
	BigDecimal thousand = new BigDecimal ('1000');

	BigDecimal rateLBS = new BigDecimal('0.4536')
	BigDecimal rateTon = new BigDecimal('1000')
	BigDecimal rateCBF = new BigDecimal('0.0283')

	public void generateBody(Body current_Body, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {
		outXml.'Group_UNH' {
			//GET
			'UNH' {
				'E0062_01' '-999'
				'S009_02' {
					'E0065_01' 'IFTMCS'
					'E0052_02' 'D'
					'E0054_03' '96B'
					'E0051_04' 'UN'
					'E0057_05' ''
				}
				'E0068_03' ''
				'S010_04' {
					'E0070_01' ''
					'E0073_02' ''
				}
			}
			'BGM' {
				Map<String, String> blTypeMap = ['Original': '705', 'Sea WayBill': '710']
				Map<String, String> transactionInformationActionMap = ['DEL': '1', 'NEW': '9', 'UPD': '5']
				'C002_01' {
					'E1001_01' blTypeMap[current_Body?.GeneralInformation?.BLType] ?: ""
					'E1131_02' ''
					'E3055_03' ''
					'E1000_04' ''
				}
				'C106_02' {
					'E1004_01' current_Body?.GeneralInformation?.BLNumber
					'E1056_02' ''
					'E1060_03' ''
				}
				//!!dont understant
				'E1225_03' transactionInformationActionMap[current_Body?.TransactionInformation?.Action]
				'E4343_04' ''
			}
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
			//!dont understant
			'DTM' {
				'C507_01' {
					'E2005_01' '182'
					if (StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.toString())) {
						'E2380_02' util.convertDateTime(current_Body.GeneralInformation.BLIssueDT.LocDT, xmlDateTimeFormat, yyyyMMdd)
					}
					'E2379_03' '203'
				}
			}
			//GET
			'TSR' {
				'C536_01' {
					Map haulageMap = ['C_C': '27', 'C_M': '29', 'M_C': '28', 'M_M': '30']
					'E4065_01' haulageMap.get(current_Body?.Route?.Haulage?.InBound + "_" + current_Body?.Route?.Haulage?.OutBound)
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
			'CUX' {
				'C504_01' {
					'E6347_01' ''
					'E6345_02' ''
					'E6343_03' ''
					'E6348_04' ''
				}
				'C504_02' {
					'E6347_01' ''
					'E6345_02' ''
					'E6343_03' ''
					'E6348_04' ''
				}
				'E5402_03' ''
				'E6341_04' ''
			}
			'MOA' {
				'C516_01' {
					'E5025_01' ''
					'E5004_02' ''
					'E6345_03' ''
					'E6343_04' ''
					'E4405_05' ''
				}
			}
			'FTX' {
				//-- cant find
				'E4451_01' 'AAS'
				'E4453_02' ''
				'C107_03' {
					'E4441_01' ''
					'E1131_02' ''
					'E3055_03' ''
				}
				'C108_04' {
					'E4440_01' util.substring(current_Body?.Remarks,1,70)?:''
					'E4440_02' util.substring(current_Body?.Remarks,71,70)?:''
					'E4440_03' util.substring(current_Body?.Remarks,141,70)?:''
					'E4440_04' util.substring(current_Body?.Remarks,211,70)?:''
					'E4440_05' util.substring(current_Body?.Remarks,281,70)?:''
				}
				'E3453_05' ''
			}
			'CNT' {
				'C270_01' {
					'E6069_01' ''
					'E6066_02' ''
					'E6411_03' ''
				}
			}
			'Group1_LOC' {
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
				'DTM' {
					'C507_01' {
						'E2005_01' ''
						'E2380_02' ''
						'E2379_03' ''
					}
				}
			}
			'Group2_TOD' {
				'TOD' {
					'E4055_01' ''
					'E4215_02' ''
					'C100_03' {
						'E4053_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E4052_04' ''
						'E4052_05' ''
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
			}
			current_Body?.CarrierRate?.each { current_CarrierRate ->
				'Group3_RFF' {
					//add by myself
					//-- 31
					'PreGrp3' ''
					'RFF' {
						//--
						'C506_01' {
							//--
							'E1153_01' ''
							//--
							'E1154_02' ''
							'E1156_03' ''
							'E4000_04' ''
						}
					}
					'DTM' {
						//--
						'C507_01' {
							//--
							'E2005_01' ''
							//--
							'E2380_02' ''
							//--
							'E2379_03' '102'
						}
					}
				}
			}
			'Group4_GOR' {
				'GOR' {
					'E8323_01' ''
					'C232_02' {
						'E9415_01' ''
						'E9411_02' ''
						'E9417_03' ''
						'E9353_04' ''
					}
					'C232_03' {
						'E9415_01' ''
						'E9411_02' ''
						'E9417_03' ''
						'E9353_04' ''
					}
					'C232_04' {
						'E9415_01' ''
						'E9411_02' ''
						'E9417_03' ''
						'E9353_04' ''
					}
					'C232_05' {
						'E9415_01' ''
						'E9411_02' ''
						'E9417_03' ''
						'E9353_04' ''
					}
				}
				'DTM' {
					'C507_01' {
						'E2005_01' ''
						'E2380_02' ''
						'E2379_03' ''
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
				}
				'Group5_DOC' {
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
				}
			}
			'Group6_CPI' {
				'CPI' {
					'C229_01' {
						//SegmentLoopList is add by myslfes
						'SegmentLoopList' current_Body?.FreightCharge[0]
						'E5237_01' '4'
						'E1131_02' ''
						'E3055_03' ''
					}
					'C231_02' {
						'E4215_01' ''
						'E1131_02' ''
						'E3055_03' ''
					}
					'E4237_03' ''
				}
				'RFF' {
					'C506_01' {
						'E1153_01' ''
						'E1154_02' ''
						'E1156_03' ''
						'E4000_04' ''
					}
				}
				'CUX' {
					'C504_01' {
						'E6347_01' ''
						'E6345_02' ''
						'E6343_03' ''
						'E6348_04' ''
					}
					'C504_02' {
						'E6347_01' ''
						'E6345_02' ''
						'E6343_03' ''
						'E6348_04' ''
					}
					'E5402_03' ''
					'E6341_04' ''
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
				'MOA' {
					'C516_01' {
						'E5025_01' ''
						'E5004_02' ''
						'E6345_03' ''
						'E6343_04' ''
						'E4405_05' ''
					}
				}
			}
			filteredFreightCharge?.eachWithIndex { current_FreightCharge, FreightChargeIndex ->
				'Group7_TCC' {
					'TCC' {
						'C200_01' {
							'E8023_01' ''
							'E1131_02' ''
							'E3055_03' ''
							//$Start/root/pfx4:Body-BillOfLading/pfx4:FreightCharge-Body-BillOfLading[number(../../index_TCC)]/pfx4:ChargeCode-FreightCharge-Body-BillOfLading
							//31
							'E8022_04' current_FreightCharge?.ChargeDesc
							Map<String, String> paymentMethodMap = [(blUtil.COLLECT): 'C', (blUtil.PREPAID): 'P']
							'E4237_05' paymentMethodMap.get(current_FreightCharge?.ChargeType)
							'E7140_06' ''
						}
						'C203_02' {
							'E5243_01' 'N'
							'E1131_02' ''
							'E3055_03' ''
							'E5242_04' current_FreightCharge?.FreightRate
							'E5275_05' ''
							'E1131_06' ''
							'E3055_07' ''
							'E5275_08' ''
							'E1131_09' ''
							'E3055_10' ''
						}
						'C528_03' {
							'E7357_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
						'C554_04' {
							'E5243_01' ''
							'E1131_02' ''
							'E3055_03' ''
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
					}
					'CUX' {
						'C504_01' {
							'E6347_01' ''
							'E6345_02' ''
							'E6343_03' ''
							'E6348_04' ''
						}
						'C504_02' {
							'E6347_01' ''
							'E6345_02' ''
							'E6343_03' ''
							'E6348_04' ''
						}
						'E5402_03' ''
						'E6341_04' ''
					}
					'PRI' {
						'C509_01' {
							'E5125_01' ''
							'E5118_02' ''
							'E5375_03' ''
							'E5387_04' ''
							'E5284_05' ''
							'E6411_06' ''
						}
						'E5213_02' ''
					}
					'EQN' {
						'C523_01' {
							'E6350_01' ''
							'E6353_02' ''
						}
					}
					'PCD' {
						'C501_01' {
							'E5245_01' ''
							'E5482_02' ''
							'E5249_03' ''
							'E1131_04' ''
							'E3055_05' ''
						}
					}
					'MOA' {
						'C516_01' {
							//!dont understant
							'E5025_01' PaymentMethod
							'E5004_02' current_FreightCharge?.TotalAmtInPmtCurrency!=''?current_FreightCharge?.TotalAmtInPmtCurrency?.toString().toBigDecimal()?.setScale(0, BigDecimal.ROUND_HALF_UP):''
							'E6345_03' PaymentMethod
							'E6343_04' ''
							'E4405_05' ''
						}
					}
					'QTY' {
						'C186_01' {
							'E6063_01' ''
							'E6060_02' ''
							'E6411_03' ''
						}
					}
				}
			}

			'Group8_TDT' {
				'TDT' {
					'E8051_01' '20'
					//--
					'E8028_02' ''
					'C220_03' {
						'E8067_01' '1'
						'E8066_02' ''
					}
					'C228_04' {
						'E8179_01' ''
						'E8178_02' ''
					}
					//--
					'C040_05' {
						//!
						'E3127_01' current_Body?.GeneralInformation?.SCACCode=="COSCCN36078928199562" ||current_Body?.GeneralInformation?.SCACCode=="COSU" ||current_Body?.GeneralInformation?.SCACCode=="COSCON" ?'COSU':''
						'E1131_02' '172'
						'E3055_03' '182'
						'E3128_04' ''
					}
					'E8101_06' ''
					'C401_07' {
						'E8457_01' ''
						'E8459_02' ''
						'E7130_03' ''
					}
					'C222_08' {
						//!
						'E8213_01' ''
						//!
						'E1131_02' ''
						'E3055_03' ''
						//!
						'E8212_04' current_Body?.Route?.OceanLeg[0]?.SVVD?.Loading?.VesselName?.trim()
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
				'Group9_LOC' {
					//Group8_Group9_LOC_5_02_02  cant find ??
					'LOC' {
						//--
						'E3227_01' ''
						//-- all
						'C517_02' {
							'E3225_01' ''
							'E1131_02' ''
							'E3055_03' '6'
							'E3224_04' ''
						}
						//-- all
						'C519_03' {
							'E3223_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E3222_04' ''
						}
						//-- all
						'C553_04' {
							'E3233_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E3232_04' ''
						}
						'E5479_05' ''
					}
					'DTM' {
						//--
						'C507_01' {
							//--
							'E2005_01' ''
							//--
							'E2380_02' ''
							//--
							'E2379_03' ''
						}
					}
				}
				'Group10_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' ''
							'E1154_02' ''
							'E1156_03' ''
							'E4000_04' ''
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
			}
			'Group11_NAD' {
				// NAD_1  have some difficult  ?
				'NAD' {
					//!
					'E3035_01' ''
					//--
					'C082_02' {
						//--
						'E3039_01' ''
						'E1131_02' ''
						'E3055_03' ''
					}
					//--
					'C058_03' {
						//--
						'E3124_01' ''
						//--
						'E3124_02' ''
						//--
						'E3124_03' ''
						//--
						'E3124_04' ''
						//--
						'E3124_05' ''
					}
					//--
					'C080_04' {
						//--
						'E3036_01' ''
						//--
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
					'E3229_07' ''
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
				'MOA' {
					'C516_01' {
						'E5025_01' ''
						'E5004_02' ''
						'E6345_03' ''
						'E6343_04' ''
						'E4405_05' ''
					}
				}
				'Group12_CTA' {
					//--
					'CTA' {
						//--
						'E3139_01' ''
						'C056_02' {
							'E3413_01' ''
							//-- 31
							'E3412_02' ''
						}
					}
					//--
					'COM' {
						//add by myselt
						//--  31
						'SegmentLoopList' ''
						'C076_01' {
							//--
							'E3148_01' ''
							//--
							'E3155_02' ''
						}
					}
				}
				'Group13_DOC' {
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
				}
				'Group14_TCC' {
					'TCC' {
						'C200_01' {
							'E8023_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E8022_04' ''
							'E4237_05' ''
							'E7140_06' ''
						}
						'C203_02' {
							'E5243_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E5242_04' ''
							'E5275_05' ''
							'E1131_06' ''
							'E3055_07' ''
							'E5275_08' ''
							'E1131_09' ''
							'E3055_10' ''
						}
						'C528_03' {
							'E7357_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
						'C554_04' {
							'E5243_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
					}
					'PRI' {
						'C509_01' {
							'E5125_01' ''
							'E5118_02' ''
							'E5375_03' ''
							'E5387_04' ''
							'E5284_05' ''
							'E6411_06' ''
						}
						'E5213_02' ''
					}
					'EQN' {
						'C523_01' {
							'E6350_01' ''
							'E6353_02' ''
						}
					}
					'PCD' {
						'C501_01' {
							'E5245_01' ''
							'E5482_02' ''
							'E5249_03' ''
							'E1131_04' ''
							'E3055_05' ''
						}
					}
					'MOA' {
						'C516_01' {
							'E5025_01' ''
							'E5004_02' ''
							'E6345_03' ''
							'E6343_04' ''
							'E4405_05' ''
						}
					}
					'QTY' {
						'C186_01' {
							'E6063_01' ''
							'E6060_02' ''
							'E6411_03' ''
						}
					}
				}
				'Group15_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' ''
							'E1154_02' ''
							'E1156_03' ''
							'E4000_04' ''
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
				'Group16_CPI' {
					'CPI' {
						'C229_01' {
							'E5237_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
						'C231_02' {
							'E4215_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
						'E4237_03' ''
					}
					'RFF' {
						'C506_01' {
							'E1153_01' ''
							'E1154_02' ''
							'E1156_03' ''
							'E4000_04' ''
						}
					}
					'CUX' {
						'C504_01' {
							'E6347_01' ''
							'E6345_02' ''
							'E6343_03' ''
							'E6348_04' ''
						}
						'C504_02' {
							'E6347_01' ''
							'E6345_02' ''
							'E6343_03' ''
							'E6348_04' ''
						}
						'E5402_03' ''
						'E6341_04' ''
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
					'MOA' {
						'C516_01' {
							'E5025_01' ''
							'E5004_02' ''
							'E6345_03' ''
							'E6343_04' ''
							'E4405_05' ''
						}
					}
				}
				'Group17_TSR' {
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
					'RFF' {
						'C506_01' {
							'E1153_01' ''
							'E1154_02' ''
							'E1156_03' ''
							'E4000_04' ''
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
					'TPL' {
						'C222_01' {
							'E8213_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E8212_04' ''
							'E8453_05' ''
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
					}
				}
			}
			'Group18_GID' {
				'GID' {
					//--
					'E1496_01' ''
					'C213_02' {
						'E7224_01' ''
						'E7065_02' ''
						'E1131_03' ''
						'E3055_04' ''
						'E7064_05' ''
					}
					//--
					'C213_03' {
						//--
						'E7224_01' ''
						//--
						'E7065_02' ''
						'E1131_03' ''
						'E3055_04' ''
						'E7064_05' ''
					}
					'C213_04' {
						'E7224_01' ''
						'E7065_02' ''
						'E1131_03' ''
						'E3055_04' ''
						'E7064_05' ''
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
				'TMP' {
					//--
					'E6245_01' ''
					//--
					'C239_02' {
						//--
						'E6246_01' ''
						//--
						'E6411_02' ''
					}
				}
				'RNG' {
					'E6167_01' ''
					'C280_02' {
						'E6411_01' ''
						'E6162_02' ''
						'E6152_03' ''
					}
				}
				'TMD' {
					'C219_01' {
						'E8335_01' ''
						'E8334_02' ''
					}
					'E8332_02' ''
					'E8341_03' ''
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
				'MOA' {
					'C516_01' {
						'E5025_01' ''
						'E5004_02' ''
						'E6345_03' ''
						'E6343_04' ''
						'E4405_05' ''
					}
				}
				'PIA' {
					'E4347_01' ''
					'C212_02' {
						'E7140_01' ''
						'E7143_02' ''
						'E1131_03' ''
						'E3055_04' ''
					}
					'C212_03' {
						'E7140_01' ''
						'E7143_02' ''
						'E1131_03' ''
						'E3055_04' ''
					}
					'C212_04' {
						'E7140_01' ''
						'E7143_02' ''
						'E1131_03' ''
						'E3055_04' ''
					}
					'C212_05' {
						'E7140_01' ''
						'E7143_02' ''
						'E1131_03' ''
						'E3055_04' ''
					}
					'C212_06' {
						'E7140_01' ''
						'E7143_02' ''
						'E1131_03' ''
						'E3055_04' ''
					}
				}
				'FTX' {
					//--
					'E4451_01' ''
					'E4453_02' ''
					//--
					'C107_03' {
						'E4441_01' ''
						//--
						'E1131_02' ''
						'E3055_03' ''
					}
					//--
					'C108_04' {
						//--
						'E4440_01' ''
						'E4440_02' ''
						'E4440_03' ''
						'E4440_04' ''
						'E4440_05' ''
					}
					'E3453_05' ''
				}
				'Group19_NAD' {
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
						'E3229_07' ''
						'E3251_08' ''
						'E3207_09' ''
					}
					'DTM' {
						'C507_01' {
							'E2005_01' ''
							'E2380_02' ''
							'E2379_03' ''
						}
					}
				}
				'GDS' {
					'C703_01' {
						'E7085_01' ''
						'E1131_02' ''
						'E3055_03' ''
					}
				}
				'Group20_MEA' {
					//add by muself
					//--
					'PreMEA' ''
					'MEA' {
						//--
						'E6311_01' ''
						//--
						'C502_02' {
							//--
							'E6313_01' ''
							'E6321_02' ''
							'E6155_03' ''
							'E6154_04' ''
						}
						//--
						'C174_03' {
							//--
							'E6411_01' ''
							//--
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
				'Group21_DIM' {
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
				'Group22_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' ''
							'E1154_02' ''
							'E1156_03' ''
							'E4000_04' ''
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
				'Group23_PCI' {
					'PCI' {
						//--
						'E4233_01' ''
						//--
						'C210_02' {
							//-- 01-08
							'E7102_01' ''
							'E7102_02' ''
							'E7102_03' ''
							'E7102_04' ''
							'E7102_05' ''
							'E7102_06' ''
							'E7102_07' ''
							'E7102_08' ''
							'E7102_09' ''
							'E7102_10' ''
						}
						'E8275_03' ''
						'C827_04' {
							'E7511_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
					}
					'RFF' {
						'C506_01' {
							'E1153_01' ''
							'E1154_02' ''
							'E1156_03' ''
							'E4000_04' ''
						}
					}
					'DTM' {
						'C507_01' {
							'E2005_01' ''
							'E2380_02' ''
							'E2379_03' ''
						}
					}
					'GIN' {
						'E7405_01' ''
						'C208_02' {
							'E7402_01' ''
							'E7402_02' ''
						}
						'C208_03' {
							'E7402_01' ''
							'E7402_02' ''
						}
						'C208_04' {
							'E7402_01' ''
							'E7402_02' ''
						}
						'C208_05' {
							'E7402_01' ''
							'E7402_02' ''
						}
						'C208_06' {
							'E7402_01' ''
							'E7402_02' ''
						}
					}
				}
				'Group24_DOC' {
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
				}
				'Group25_TPL' {
					'TPL' {
						'C222_01' {
							'E8213_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E8212_04' ''
							'E8453_05' ''
						}
					}
					'Group26_MEA' {
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
				}
				'Group27_SGP' {
					'SGP' {
						'C237_01' {
							'E8260_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E3207_04' ''
						}
						'E7224_02' ''
					}
					'SEQ' {
						'E1245_01' ''
						'C286_02' {
							'E1050_01' ''
							'E1159_02' ''
							'E1131_03' ''
							'E3055_04' ''
						}
					}
					'Group28_MEA' {
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
				}
				'Group29_TCC' {
					'TCC' {
						'C200_01' {
							'E8023_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E8022_04' ''
							'E4237_05' ''
							'E7140_06' ''
						}
						'C203_02' {
							'E5243_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E5242_04' ''
							'E5275_05' ''
							'E1131_06' ''
							'E3055_07' ''
							'E5275_08' ''
							'E1131_09' ''
							'E3055_10' ''
						}
						'C528_03' {
							'E7357_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
						'C554_04' {
							'E5243_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
					}
					'CUX' {
						'C504_01' {
							'E6347_01' ''
							'E6345_02' ''
							'E6343_03' ''
							'E6348_04' ''
						}
						'C504_02' {
							'E6347_01' ''
							'E6345_02' ''
							'E6343_03' ''
							'E6348_04' ''
						}
						'E5402_03' ''
						'E6341_04' ''
					}
					'PRI' {
						'C509_01' {
							'E5125_01' ''
							'E5118_02' ''
							'E5375_03' ''
							'E5387_04' ''
							'E5284_05' ''
							'E6411_06' ''
						}
						'E5213_02' ''
					}
					'EQN' {
						'C523_01' {
							'E6350_01' ''
							'E6353_02' ''
						}
					}
					'PCD' {
						'C501_01' {
							'E5245_01' ''
							'E5482_02' ''
							'E5249_03' ''
							'E1131_04' ''
							'E3055_05' ''
						}
					}
					'MOA' {
						'C516_01' {
							'E5025_01' ''
							'E5004_02' ''
							'E6345_03' ''
							'E6343_04' ''
							'E4405_05' ''
						}
					}
					'QTY' {
						'C186_01' {
							'E6063_01' ''
							'E6060_02' ''
							'E6411_03' ''
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
				}
				'Group30_DGS' {
					'DGS' {
						//--
						'E8273_01' ''
						//--
						'C205_02' {
							//--
							'E8351_01' ''
							//--
							'E8078_02' ''
							'E8092_03' ''
						}
						//--
						'C234_03' {
							//--
							'E7124_01' ''
							'E7088_02' ''
						}
						//--
						'C223_04' {
							//--
							'E7106_01' ''
							//--
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
					}
					'Group31_CTA' {
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
					'Group32_MEA' {
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
					'Group33_SGP' {
						'SGP' {
							'C237_01' {
								'E8260_01' ''
								'E1131_02' ''
								'E3055_03' ''
								'E3207_04' ''
							}
							'E7224_02' ''
						}
						'Group34_MEA' {
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
					}
				}
			}
			'Group35_EQD' {
				'EQD' {
					//--
					'E8053_01' ''
					//--
					'C237_02' {
						//--
						'E8260_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E3207_04' ''
					}
					//--
					'C224_03' {
						//-- 31
						'E8155_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E8154_04' ''
					}
					//--
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
				'TMD' {
					'C219_01' {
						'E8335_01' ''
						'E8334_02' ''
					}
					'E8332_02' ''
					'E8341_03' ''
				}
				'MEA' {
					//add by myself
					'PreMEA' ''
					//--
					'E6311_01' ''
					//--
					'C502_02' {
						//--
						'E6313_01' ''
						'E6321_02' ''
						'E6155_03' ''
						'E6154_04' ''
					}
					//--
					'C174_03' {
						//--
						'E6411_01' ''
						//--
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
				'SEL' {
					//--
					'E9308_01' ''
					//--
					'C215_02' {
						//--
						'E9303_01' ''
						'E1131_02' ''
						'E3055_03' ''
						'E9302_04' ''
					}
					'E4517_03' ''
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
				'TMP' {
					'E6245_01' ''
					'C239_02' {
						'E6246_01' ''
						'E6411_02' ''
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
				}
				'Group36_TCC' {
					'TCC' {
						'C200_01' {
							'E8023_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E8022_04' ''
							'E4237_05' ''
							'E7140_06' ''
						}
						'C203_02' {
							'E5243_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E5242_04' ''
							'E5275_05' ''
							'E1131_06' ''
							'E3055_07' ''
							'E5275_08' ''
							'E1131_09' ''
							'E3055_10' ''
						}
						'C528_03' {
							'E7357_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
						'C554_04' {
							'E5243_01' ''
							'E1131_02' ''
							'E3055_03' ''
						}
					}
					'PRI' {
						'C509_01' {
							'E5125_01' ''
							'E5118_02' ''
							'E5375_03' ''
							'E5387_04' ''
							'E5284_05' ''
							'E6411_06' ''
						}
						'E5213_02' ''
					}
					'EQN' {
						'C523_01' {
							'E6350_01' ''
							'E6353_02' ''
						}
					}
					'PCD' {
						'C501_01' {
							'E5245_01' ''
							'E5482_02' ''
							'E5249_03' ''
							'E1131_04' ''
							'E3055_05' ''
						}
					}
					'MOA' {
						'C516_01' {
							'E5025_01' ''
							'E5004_02' ''
							'E6345_03' ''
							'E6343_04' ''
							'E4405_05' ''
						}
					}
					'QTY' {
						'C186_01' {
							'E6063_01' ''
							'E6060_02' ''
							'E6411_03' ''
						}
					}
				}
				'Group37_NAD' {
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
						'E3229_07' ''
						'E3251_08' ''
						'E3207_09' ''
					}
					'DTM' {
						'C507_01' {
							'E2005_01' ''
							'E2380_02' ''
							'E2379_03' ''
						}
					}
				}
				'Group38_EQA' {
					'EQA' {
						'E8053_01' ''
						'C237_02' {
							'E8260_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E3207_04' ''
						}
					}
					'EQN' {
						'C523_01' {
							'E6350_01' ''
							'E6353_02' ''
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

		//Parse the xmlBody to JavaBean
		XmlBeanParser parser = new XmlBeanParser()
		BillOfLading bl = parser.xmlParser(inputXmlBody, BillOfLading.class)


		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
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
		def IFTMCS = outXml.createNode('IFTMCS')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.

		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()
		def txnErrorKeys = []

		//Start mapping

		bl?.Body?.eachWithIndex{current_Body, current_BodyIndex ->

			List<FreightCharge> filteredFreightCharge = current_Body.FreightCharge?.clone()
			List<FreightChargeCNTR> filteredFreightChargeCNTR = current_Body.FreightChargeCNTR?.clone()
			//OceanLegReorder
			blUtil.OceanLegReorder(current_Body.Route?.OceanLeg)

			//associate container and cargo
			Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo = blUtil.associateContainerAndCargo(current_Body)
			
			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex)

			//mapping
			if(errorKeyList.isEmpty()){
				generateBody(current_Body,  associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml)
			}
			//posp checking
			if(errorKeyList.isEmpty()){
				pospValidation(writer?.toString(), errorKeyList)
			}

			blUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%18s', current_Body?.TransactionInformation?.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			blUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, bl.Header?.MsgDT?.GMT, bl.Header.InterchangeMessageID, TP_ID, conn)

			txnErrorKeys.add(errorKeyList)
		}

		//End root node
		outXml.nodeCompleted(null,IFTMCS)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		blUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		blUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		blUtil.promoteHeaderIntChgMsgId(appSessionId, bl?.Body[0]);
		blUtil.promoteScacCode(appSessionId, bl?.Body[0]);
		
		String result = '';
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			result = util.cleanXml(writer?.toString());
		}

		writer.close();

		println result

		return result;
	}


	List<Map<String,String>> prepValidation(cs.b2b.core.mapping.bean.bl.Body current_Body, int current_BodyIndex){

		return [];
	}

	void pospValidation(String outputXml, List<Map<String,String>> errorKeyList){

		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(outputXml + "</IFTMCS>")

		blUtil.checkLocationcodeForPOLPOD(node, true, null, errorKeyList)

	}
}


