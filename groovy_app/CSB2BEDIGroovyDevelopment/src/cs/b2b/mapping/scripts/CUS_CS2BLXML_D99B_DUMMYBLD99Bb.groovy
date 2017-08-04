import com.sun.javafx.binding.StringFormatter
import cs.b2b.core.mapping.bean.bl.Cargo
import groovy.xml.MarkupBuilder

import java.sql.Connection

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.bean.bl.FreightCharge
import cs.b2b.core.mapping.bean.bl.FreightChargeCNTR
import cs.b2b.core.mapping.bean.bl.OceanLeg
import cs.b2b.core.mapping.util.XmlBeanParser

public class CUS_CS2BLXML_D99B_DUMMYBLD99Bb {

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
			'UNH' {
			'E0062_01' '-999'
			'S009_02' {
				'E0065_01' 'IFTMCS'
				'E0052_02' 'D'
				'E0054_03' '99B'
				'E0051_04' 'UN'
				'E0057_05' '2.0'
			}
			'E0068_03' ''
			'S010_04' {
				'E0070_01' ''
				'E0073_02' ''
			}
		}
		'BGM' {
			Map<String,String> blTypeMap = ['Original':'705', 'Sea WayBill':'710']
			Map<String,String> transactionInformationActionMap = ['DEL':'1', 'NEW':'9', 'UPD':'5']
			'C002_01' {
				'E1001_01' blTypeMap[current_Body.GeneralInformation.BLType] ? blTypeMap[current_Body.GeneralInformation.BLType] : ""
				'E1131_02' ''
				'E3055_03' ''
				'E1000_04' current_Body.GeneralInformation.BLType
			}
			'C106_02' {
				'E1004_01' current_Body.GeneralInformation.BLNumber?.trim()
				'E1056_02' ''
				'E1060_03' ''
			}
			'E1225_03' transactionInformationActionMap[current_Body.TransactionInformation.Action]
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
		if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.toString())){
			'DTM' {
				'C507_01' {
					'E2005_01' '182'
					'E2380_02' util.convertDateTime(current_Body.GeneralInformation.BLIssueDT.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E2379_03' '102'
				}
			}
		}
		if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLOnboardDT?.LocDT?.toString())) {
			'DTM' {
				'C507_01' {
					'E2005_01' '342'
					'E2380_02' util.convertDateTime(current_Body.GeneralInformation.BLOnboardDT.LocDT, xmlDateTimeFormat, yyyyMMdd)
					'E2379_03' '102'
				}
			}
		}
		'TSR' {
			'C536_01' {
				Map haulageMap = ['C_C':'27', 'C_M':'29', 'M_C':'28', 'M_M':'30']
				'E4065_01' haulageMap.get(current_Body?.Route?.Haulage?.InBound + "_" + current_Body?.Route?.Haulage?.OutBound)
				'E1131_02' ''
				'E3055_03' ''
			}
			'C233_02' {
				Map trafficModeMap = ['FCL':'2','LCL':'3']
				'E7273_01' trafficModeMap.get(current_Body?.GeneralInformation?.TrafficMode?.OutBound)
				'E1131_02' ''
				'E3055_03' ''
				'E7273_04' trafficModeMap.get(current_Body?.GeneralInformation?.TrafficMode?.InBound)
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
				'E6343_03' ''
				'E4405_04' ''
			}
		}
		Map<String, String> blCertClauseTypeMap = ['01':'01','02':'02','03':'03','04':'04','11':'11','20':'20','22':'22','24':'24','25':'25','34':'34','CC':'CC']
		current_Body?.BLCertClause?.each {current_BLCertClause ->
			'FTX' {
				'E4451_01' 'BLC'
				'E4453_02' ''
				'C107_03' {
					'E4441_01' blCertClauseTypeMap.get(current_BLCertClause?.CertificationClauseType)
					'E1131_02' ''
					'E3055_03' ''
				}
				'C108_04' {
					'E4440_01' util.substring(current_BLCertClause?.CertificationClauseText,1,60)?.trim()
					'E4440_02' util.substring(current_BLCertClause?.CertificationClauseText,61,60)?.trim()
					'E4440_03' util.substring(current_BLCertClause?.CertificationClauseText,121,60)?.trim()
					'E4440_04' util.substring(current_BLCertClause?.CertificationClauseText,181,60)?.trim()
					'E4440_05' util.substring(current_BLCertClause?.CertificationClauseText,241,60)?.trim()
				}
				'E3453_05' ''
				'E4447_06' ''
			}
		}
		if(current_Body?.Remarks){
			'FTX' {
				'E4451_01' 'AAS'
				'E4453_02' ''
				'C107_03' {
					'E4441_01' ''
					'E1131_02' ''
					'E3055_03' ''
				}
				'C108_04' {
					'E4440_01' util.substring(current_Body?.Remarks,1,60)?.trim()
					'E4440_02' util.substring(current_Body?.Remarks,61,60)?.trim()
					'E4440_03' util.substring(current_Body?.Remarks,121,60)?.trim()
					'E4440_04' util.substring(current_Body?.Remarks,181,60)?.trim()
					'E4440_05' util.substring(current_Body?.Remarks,241,60)?.trim()
				}
				'E3453_05' ''
				'E4447_06' ''
			}
		}
		if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLGrossWeight?.Weight)){
			Map grossWeightUnitMap = ['KGM':'KGM','LBS':'LBS','TON':'TON']
			'CNT' {
				'C270_01' {
					'E6069_01' '7'
					'E6066_02' current_Body?.GeneralInformation?.BLGrossWeight?.Weight?.toBigDecimal()?.setScale(0, BigDecimal.ROUND_CEILING)
					'E6411_03' grossWeightUnitMap.get(current_Body?.GeneralInformation?.BLGrossWeight?.WeightUnit)
				}
			}
		}
		if(StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLVolume?.Volume)){
			Map consignmentUnitMap = ['CBF':'CBF','CBM':'CBM']
			'CNT' {
				'C270_01' {
					'E6069_01' '15'
					'E6066_02' current_Body?.GeneralInformation?.BLVolume?.Volume?.toBigDecimal()?.setScale(0, BigDecimal.ROUND_CEILING)
					'E6411_03' consignmentUnitMap.get(current_Body?.GeneralInformation?.BLVolume?.VolumeUnit)
				}
			}
		}
		'CNT' {
			'C270_01' {
				'E6069_01' '11'
				'E6066_02' current_Body?.Container ? current_Body?.Container?.size() : '0'
			}
		}
		'GDS' {
			'C703_01' {
				'E7085_01' ''
				'E1131_02' ''
				'E3055_03' ''
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
		current_Body?.GeneralInformation?.CarrierBookingNumber?.each{current_CarrierBookingNumber ->
			'Group3_RFF' {
				'RFF' {
					'C506_01' {
						'E1153_01' 'BN'
						'E1154_02' util.substring(current_CarrierBookingNumber, 1, 35)
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
		}

		if(current_Body?.GeneralInformation?.BLNumber){
			'Group3_RFF' {
				'RFF' {
					'C506_01' {
						'E1153_01' 'BM'
						'E1154_02' util.substring(current_Body?.GeneralInformation?.BLNumber, 1, 35)
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
		}
		Map<String, String> refTypeMap = ['CR':'CR','CTR':'CT','EXPR':'RF','FID':'FI','FR':'FF','INV':'IV','PO':'ON','SID':'SI']

		current_Body?.CarrierRate?.each { current_CarrierRate ->
			if (refTypeMap.get(current_CarrierRate?.CSCarrierRateType) && StringUtil.isNotEmpty(current_CarrierRate?.CarrierRateNumber)){
				'Group3_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' refTypeMap.get(current_CarrierRate?.CSCarrierRateType)
							'E1154_02' util.substring(current_CarrierRate?.CarrierRateNumber, 1, 35)
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
			}
		}

		current_Body?.ExternalReference?.each { current_ExternalReference ->
			if (refTypeMap.get(current_ExternalReference?.CSReferenceType) && StringUtil.isNotEmpty(current_ExternalReference?.ReferenceNumber)){
				'Group3_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' refTypeMap.get(current_ExternalReference?.CSReferenceType)
							'E1154_02' util.substring(current_ExternalReference?.ReferenceNumber, 1, 35)
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
					'E1060_05' ''
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
				}
			}
		}
		filteredFreightCharge?.eachWithIndex{current_FreightCharge, FreightChargeIndex->
			'Group7_TCC' {
				'TCC' {
					'C200_01' {
						String chargeCode = blUtil.getB2BCdeConversion(current_FreightCharge?.ChargeCode,TP_ID,"ChargeType",conn);
						if(chargeCode){
							chargeCode = current_FreightCharge?.ChargeCode
						}
						'E8023_01' chargeCode
						'E1131_02' ''
						'E3055_03' ''
						'E8022_04' util.substring(current_FreightCharge.ChargeDesc,1,26)
						Map<String, String> paymentMethodMap = [(blUtil.COLLECT):'C', (blUtil.PREPAID):'P']
						'E4237_05' paymentMethodMap.get(current_FreightCharge?.ChargeType)
						'E7140_06' ''
					}
					'C203_02' {
						'E5243_01' 'N'
						'E1131_02' ''
						'E3055_03' ''
						'E5242_04' util.substring(current_FreightCharge?.FreightRate,1,35)
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
					'E4447_06' ''
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
						'E5025_01' '23'
						if(current_FreightCharge?.ChargeAmount?.ChargeAmount){
							'E5004_02' current_FreightCharge?.ChargeAmount?.ChargeAmount?.toBigDecimal()?.setScale(0, BigDecimal.ROUND_HALF_UP)
						}else{
							'E5004_02' '0'
						}
						'E6345_03' current_FreightCharge?.ChargeAmount?.attr_Currency
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

		OceanLeg firstOceanLeg = null
		OceanLeg lastOceanLeg = null
		if(current_Body?.Route?.OceanLeg?.size() > 0){
			firstOceanLeg = current_Body?.Route?.OceanLeg[0]
			lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
		}

		'Group8_TDT' {
			'TDT' {
				'E8051_01' '20'
				if(lastOceanLeg) {
					'E8028_02' "${StringUtil.isNotEmpty(lastOceanLeg?.SVVD?.Loading?.Voyage) ? lastOceanLeg?.SVVD?.Loading?.Voyage?.trim() : ''}${StringUtil.isNotEmpty(lastOceanLeg?.SVVD?.Loading?.Direction) ? lastOceanLeg?.SVVD?.Loading?.Direction?.trim() : ''}"
				}
				'C220_03' {
					'E8067_01' '1'
					'E8066_02' ''
				}
				'C228_04' {
					'E8179_01' ''
					'E8178_02' ''
				}
				'C040_05' {
					'E3127_01' current_Body?.GeneralInformation?.SCACCode
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
					if(StringUtil.isNotEmpty(lastOceanLeg?.SVVD?.Loading?.LloydsNumber)){
						'E8213_01' lastOceanLeg?.SVVD?.Loading?.LloydsNumber?.trim()
						'E1131_02' 'L'
					}else if(StringUtil.isNotEmpty(lastOceanLeg?.SVVD?.Loading?.CallSign)){
						'E8213_01' lastOceanLeg?.SVVD?.Loading?.CallSign?.trim()
						'E1131_02' '103'
					}

					'E3055_03' ''
					'E8212_04' lastOceanLeg?.SVVD?.Loading?.VesselName?.trim()
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
			//FND
			'Group9_LOC' {
				'LOC' {
					'E3227_01' '7'
					'C517_02' {
						if(StringUtil.isNotEmpty(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode)){
							'E3225_01' current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
						}else if (StringUtil.isNotEmpty(current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode)){
							'E3225_01' current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode
						}
						'E1131_02' ''
						'E3055_03' '6'
						'E3224_04' current_Body?.Route?.FND?.CityDetails?.City?.trim()
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
				if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT) {
					'DTM' {
						'C507_01' {
							'E2005_01' '178'
							'E2380_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd + HHmm)
							'E2379_03' '203'
						}
					}
				}else if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT){
					'DTM' {
						'C507_01' {
							'E2005_01' '132'
							'E2380_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd + HHmm)
							'E2379_03' '203'
						}
					}
				}
			}
			//POL
			'Group9_LOC' {
				'LOC' {
					'E3227_01' '9'
					'C517_02' {
						if(StringUtil.isNotEmpty(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode)){
							'E3225_01' current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode
						}else if(StringUtil.isNotEmpty(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode)){
							'E3225_01' current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode
						}
						'E1131_02' ''
						'E3055_03' '6'
						'E3224_04' current_Body?.Route?.FirstPOL?.Port?.PortName?.trim()
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
				if(firstOceanLeg?.DepartureDT?.find{it?.attr_Indicator == "A"}?.LocDT){
					'DTM' {
						'C507_01' {
							'E2005_01' '186'
							'E2380_02' util.convertDateTime(firstOceanLeg?.DepartureDT?.find{it?.attr_Indicator == "A"}?.LocDT, xmlDateTimeFormat, yyyyMMdd + HHmm)
							'E2379_03' '203'
						}
					}
				}else if(firstOceanLeg?.DepartureDT?.find{it?.attr_Indicator == "E"}?.LocDT){
					'DTM' {
						'C507_01' {
							'E2005_01' '133'
							'E2380_02' util.convertDateTime(firstOceanLeg?.DepartureDT?.find{it?.attr_Indicator == "E"}?.LocDT, xmlDateTimeFormat, yyyyMMdd + HHmm)
							'E2379_03' '203'
						}
					}
				}
			}
			//POD
			'Group9_LOC' {
				'LOC' {
					'E3227_01' '11'
					'C517_02' {
						if(StringUtil.isNotEmpty(current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode)){
							'E3225_01' current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode
						}else if(StringUtil.isNotEmpty(current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode)){
							'E3225_01' current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode
						}
						'E1131_02' ''
						'E3055_03' '6'
						'E3224_04' current_Body?.Route?.LastPOD?.Port?.PortName?.trim()
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
				if(lastOceanLeg?.ArrivalDT?.find{it?.attr_Indicator == "A"}?.LocDT){
					'DTM' {
						'C507_01' {
							'E2005_01' '178'
							'E2380_02' util.convertDateTime(lastOceanLeg?.ArrivalDT?.find{it?.attr_Indicator == "A"}?.LocDT, xmlDateTimeFormat, yyyyMMdd + HHmm)
							'E2379_03' '203'
						}
					}
				}else if(lastOceanLeg?.ArrivalDT?.find{it?.attr_Indicator == "E"}?.LocDT){
					'DTM' {
						'C507_01' {
							'E2005_01' '132'
							'E2380_02' util.convertDateTime(lastOceanLeg?.ArrivalDT?.find{it?.attr_Indicator == "E"}?.LocDT, xmlDateTimeFormat, yyyyMMdd + HHmm)
							'E2379_03' '203'
						}
					}
				}
			}
			//POR
			'Group9_LOC' {
				'LOC' {
					'E3227_01' '88'
					'C517_02' {
						if(StringUtil.isNotEmpty(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode)){
							'E3225_01' current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
						}else if (StringUtil.isNotEmpty(current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode)){
							'E3225_01' current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode
						}
						'E1131_02' ''
						'E3055_03' '6'
						'E3224_04' current_Body?.Route?.POR?.CityDetails?.City?.trim()
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
				if(current_Body?.Route?.FullReturnCutoffDT?.LocDT) {
					'DTM' {
						'C507_01' {
							Date fullReturnCutoffDate = util.sdfXmlFmt.parse(current_Body?.Route?.FullReturnCutoffDT?.LocDT?.LocDT);
							'E2005_01' fullReturnCutoffDate.after(currentSystemDt) ? '132' : '178'
							'E2380_02' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.LocDT, xmlDateTimeFormat, yyyyMMdd + HHmm)
							'E2379_03' '203'
						}
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
		}
		Map<String, String> partyTypeMap = ['ANP':'N2','CAR':'CA','CGN':'CN','FWD':'FW','NPT':'N1','SHP':'CZ']
		current_Body?.Party?.each {current_Party ->
			if(partyTypeMap.get(current_Party?.PartyType)) {
				'Group11_NAD' {
					'NAD' {
						'E3035_01' partyTypeMap.get(current_Party?.PartyType)
						'C082_02' {
							if (current_Party?.PartyType == 'CAR') {
								'E3039_01' current_Body?.GeneralInformation?.SCACCode
							} else {
								'E3039_01' current_Party?.CarrierCustomerCode
							}
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
							'E3036_01' util.substring(current_Party?.PartyName, 1, 35)
							'E3036_02' util.substring(current_Party?.PartyName, 36, 35)
							'E3036_03' ''
							'E3036_04' ''
							'E3036_05' ''
							'E3045_06' ''
						}
						String vAddressLines = current_Party?.Address?.AddressLines?.AddressLine?.join("")
						'C059_05' {
							'E3042_01' util.substring(vAddressLines, 1, 35)
							'E3042_02' util.substring(vAddressLines, 36, 35)
							'E3042_03' util.substring(vAddressLines, 71, 35)
							'E3042_04' util.substring(vAddressLines, 106, 35)
						}
						'E3164_06' current_Party?.Address?.City
						'C819_07' {
							'E3229_01' util.substring(current_Party?.Address?.State, 1, 9)
							'E1131_02' ''
							'E3055_03' ''
							'E3228_04' ''
						}
						'E3251_08' current_Party?.Address?.PostalCode
						'E3207_09' util.substring(current_Party?.Address?.Country, 1, 3)
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
					String FirstName = current_Party?.Contact?.FirstName
					String LastName = current_Party?.Contact?.LastName
					String vname = util.substring((StringUtil.isNotEmpty(FirstName) ? FirstName : '') + ' ' + (StringUtil.isNotEmpty(LastName) ? LastName : ''), 1, 60)
					if (StringUtil.isNotEmpty(vname)) {
						'Group12_CTA' {
							'CTA' {
								'E3139_01' 'IC'
								'C056_02' {
									'E3413_01' ''
									'E3412_02' util.substring(vname?.trim(), 1, 35)
								}
							}
							if (StringUtil.isNotEmpty(current_Party?.Contact?.ContactEmailAddress)) {
								'COM' {
									'C076_01' {
										'E3148_01' current_Party?.Contact?.ContactEmailAddress
										'E3155_02' 'EM'
									}
								}
							}
							if (StringUtil.isNotEmpty(current_Party?.Contact?.ContactPhone?.Number)) {
								'COM' {
									'C076_01' {
										'E3148_01' current_Party?.Contact?.ContactPhone?.Number
										'E3155_02' 'TE'
									}
								}
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
					}
					'Group15_RFF' {
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
							'E4447_06' ''
						}
					}
				}
			}
		}
		current_Body?.Cargo?.eachWithIndex {current_Cargo, cargoIndex ->
			'Group18_GID' {
				'GID' {
					'E1496_01' cargoIndex + 1
					'C213_02' {
						'E7224_01' current_Cargo?.Packaging?.PackageQty
						String packageUtil = util.getConversion(TP_ID, "BL", "O", "PackageUnit", current_Cargo?.Packaging?.PackageType, conn)
						'E7065_02' packageUtil ? packageUtil : current_Cargo?.Packaging?.PackageType
						'E1131_03' ''
						'E3055_04' ''
						'E7064_05' ''
					}
					'C213_03' {
						'E7224_01' ''
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
					'C213_05' {
						'E7224_01' ''
						'E7065_02' ''
						'E1131_03' ''
						'E3055_04' ''
						'E7064_05' ''
					}
					'C213_06' {
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
					'E6245_01' ''
					'C239_02' {
						'E6246_01' ''
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

				if(current_Cargo?.CargoDescription) {
					for (int i = 0; i * 350 < current_Cargo?.CargoDescription?.length() && i < 9; i++) {
						'FTX' {
							'E4451_01' 'AAA'
							'E4453_02' ''
							'C107_03' {
								'E4441_01' ''
								'E1131_02' ''
								'E3055_03' ''
							}
							'C108_04' {
								'E4440_01' util.substring(current_Cargo?.CargoDescription, i * 350 + 1, 70)
								'E4440_02' util.substring(current_Cargo?.CargoDescription, i * 350 + 71, 70)
								'E4440_03' util.substring(current_Cargo?.CargoDescription, i * 350 + 141, 70)
								'E4440_04' util.substring(current_Cargo?.CargoDescription, i * 350 + 211, 70)
								'E4440_05' util.substring(current_Cargo?.CargoDescription, i * 350 + 281, 70)
							}
							'E3453_05' ''
							'E4447_06' ''
						}
					}
				}else{
					'FTX' {
						'E4451_01' 'AAA'
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
				'PCD' {
					'C501_01' {
						'E5245_01' ''
						'E5482_02' ''
						'E5249_03' ''
						'E1131_04' ''
						'E3055_05' ''
					}
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
						'C819_07' {
							'E3229_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E3228_04' ''
						}
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
				if(['KGS','KGM','LBS','TON'].contains(current_Cargo?.GrossWeight?.WeightUnit)){
					'Group20_MEA' {
						'MEA' {
							'E6311_01' 'WT'
							'C502_02' {
								'E6313_01' 'G'
								'E6321_02' ''
								'E6155_03' ''
								'E6154_04' ''
							}
							'C174_03' {
								'E6411_01' 'KGM'
								if(current_Cargo?.GrossWeight?.WeightUnit == "KGS" || current_Cargo?.GrossWeight?.WeightUnit == "KGM" ){
									'E6314_02' current_Cargo?.GrossWeight?.Weight?.toBigDecimal()?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString()
								}else if(current_Cargo?.GrossWeight?.WeightUnit == "LBS"){
									'E6314_02' blUtil.replaceZeroAfterPoint(current_Cargo?.GrossWeight?.Weight?.toBigDecimal()?.multiply(rateLBS)?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString())
								}else if(current_Cargo?.GrossWeight?.WeightUnit == "TON"){
									'E6314_02' current_Cargo?.GrossWeight?.Weight?.toBigDecimal()?.multiply(rateTon)?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString()
								}
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

				if(['CBM','CBF'].contains(current_Cargo?.Volume?.VolumeUnit) && StringUtil.isNotEmpty(current_Cargo?.Volume?.Volume)){
					'Group20_MEA' {
						'MEA' {
							'E6311_01' 'VOL'
							'C502_02' {
								'E6313_01' 'AAW'
								'E6321_02' ''
								'E6155_03' ''
								'E6154_04' ''
							}
							'C174_03' {
								'E6411_01' 'CBM'
								if(current_Cargo?.Volume?.VolumeUnit == "CBM"){
									'E6314_02' current_Cargo?.Volume?.Volume?.toBigDecimal()?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString()
								}else if(current_Cargo?.Volume?.VolumeUnit == "CBF"){
									'E6314_02' current_Cargo?.Volume?.Volume?.toBigDecimal()?.multiply(rateCBF)?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString()
								}
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
				List<List<String>> markAndNumberListGroup = []
				List<String> markAndNumberList = new ArrayList<String>()
				current_Cargo?.MarksAndNumbers?.eachWithIndex{ current_MarksAndNumbers, marksAndNumbersIndex ->
					if(markAndNumberList.size() >= 10){
						markAndNumberListGroup.add(markAndNumberList);
						markAndNumberList = new ArrayList<String>()
					}
					if(current_MarksAndNumbers?.MarksAndNumbersLine?.length() > 35){
						for(int i = 0; i < current_MarksAndNumbers?.MarksAndNumbersLine?.length() ; i++){
							String tmpMarkAndNumber = util.substring(current_MarksAndNumbers?.MarksAndNumbersLine, i*35 + 1, 35)
							if(StringUtil.isNotEmpty(tmpMarkAndNumber)){
								if(markAndNumberList.size() >= 10){
									markAndNumberListGroup.add(markAndNumberList);
									markAndNumberList = new ArrayList<String>();
								}
								markAndNumberList.add(tmpMarkAndNumber)
							}
						}
					}else{
						markAndNumberList.add(current_MarksAndNumbers?.MarksAndNumbersLine)
					}
				}
				markAndNumberListGroup.add(markAndNumberList);
				markAndNumberListGroup.eachWithIndex { current_markAndNumberList, markAndNumberIndex ->
					if(markAndNumberIndex < 9) {
						'Group23_PCI' {
							'PCI' {
								'E4233_01' ''
								'C210_02' {
									'E7102_01' current_markAndNumberList.size() > 0 ? current_markAndNumberList[0]?.trim() : ''
									'E7102_02' current_markAndNumberList.size() > 1 ? current_markAndNumberList[1]?.trim() : ''
									'E7102_03' current_markAndNumberList.size() > 2 ? current_markAndNumberList[2]?.trim() : ''
									'E7102_04' current_markAndNumberList.size() > 3 ? current_markAndNumberList[3]?.trim() : ''
									'E7102_05' current_markAndNumberList.size() > 4 ? current_markAndNumberList[4]?.trim() : ''
									'E7102_06' current_markAndNumberList.size() > 5 ? current_markAndNumberList[5]?.trim() : ''
									'E7102_07' current_markAndNumberList.size() > 6 ? current_markAndNumberList[6]?.trim() : ''
									'E7102_08' current_markAndNumberList.size() > 7 ? current_markAndNumberList[7]?.trim() : ''
									'E7102_09' current_markAndNumberList.size() > 8 ? current_markAndNumberList[8]?.trim() : ''
									'E7102_10' current_markAndNumberList.size() > 9 ? current_markAndNumberList[9]?.trim() : ''
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
							'E8260_01' "${StringUtil.isNotEmpty(current_Cargo?.CurrentContainerNumber) ? current_Cargo?.CurrentContainerNumber : ''}${StringUtil.isNotEmpty(current_Cargo?.CurrentContainerCheckDigit) ? current_Cargo?.CurrentContainerCheckDigit : ''}"
							'E1131_02' ''
							'E3055_03' ''
							'E3207_04' ''
						}
						'E7224_02' current_Cargo?.Packaging?.PackageQty
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
				current_Cargo?.DGCargoSpec?.eachWithIndex{ current_DGCargoSpec, DGGargoIndex ->
					'Group30_DGS' {
						'DGS' {
							'E8273_01' 'IMD'
							'C205_02' {
								'E8351_01' util.substring(current_DGCargoSpec?.IMOClass?.trim(),1,7)
								'E8078_02' util.substring(current_DGCargoSpec?.IMDGPage?.trim(),1,7)
								'E8092_03' ''
							}
							//BizConn Special Handle
							String unnnumber = current_DGCargoSpec?.UNNumber
							if(unnnumber){
								unnnumber = String.format("%4s",current_DGCargoSpec?.UNNumber)?.replace(" ","0")
							}
							'C234_03' {
								'E7124_01' util.substring(unnnumber,1,4)
								'E7088_02' ''
							}
							//BizConn Special Handle
							String dgTemperature = current_DGCargoSpec?.FlashPoint?.Temperature
							if(dgTemperature){
								dgTemperature = "${dgTemperature?.toBigDecimal() < 0 ? '-':''}${util.substring(String.format("%3s",dgTemperature?.toBigDecimal()?.abs()?.setScale(0, BigDecimal.ROUND_DOWN)?.toString())?.replace(" ","0"),1,3)}"
								if(dgTemperature == "000"){
									dgTemperature = "0"
								}
							}

							Map<String, String> measureUnitQualifierMap = ['C':'CEL','F':'FAH']
							'C223_04' {
								'E7106_01' dgTemperature
								'E6411_02' measureUnitQualifierMap.get(current_DGCargoSpec?.FlashPoint?.TemperatureUnit)
							}
							Map<String, String> dgCargoCodeMap = ['I':'1','II':'2','III':'3']
							'E8339_05' dgCargoCodeMap.get(current_DGCargoSpec?.PackageGroup?.Code)
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
						if(StringUtil.isNotEmpty(current_DGCargoSpec.Remarks)){
							'FTX' {
								'E4451_01' 'AAC'
								'E4453_02' ''
								'C107_03' {
									'E4441_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
								'C108_04' {
									'E4440_01' current_DGCargoSpec.Remarks
									'E4440_02' ''
									'E4440_03' ''
									'E4440_04' ''
									'E4440_05' ''
								}
								'E3453_05' ''
								'E4447_06' ''
							}
						}
						if(StringUtil.isNotEmpty(current_DGCargoSpec?.TechnicalName)){
							'FTX' {
								'E4451_01' 'AAD'
								'E4453_02' ''
								'C107_03' {
									'E4441_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
								'C108_04' {
									'E4440_01' current_DGCargoSpec?.TechnicalName
									'E4440_02' ''
									'E4440_03' ''
									'E4440_04' ''
									'E4440_05' ''
								}
								'E3453_05' ''
								'E4447_06' ''
							}
						}
						if(StringUtil.isNotEmpty(current_DGCargoSpec?.ProperShippingName)){
							'FTX' {
								'E4451_01' 'ACB'
								'E4453_02' ''
								'C107_03' {
									'E4441_01' ''
									'E1131_02' ''
									'E3055_03' ''
								}
								'C108_04' {
									'E4440_01' current_DGCargoSpec?.ProperShippingName
									'E4440_02' ''
									'E4440_03' ''
									'E4440_04' ''
									'E4440_05' ''
								}
								'E3453_05' ''
								'E4447_06' ''
							}
						}
						current_DGCargoSpec?.EmergencyContact?.each { current_EmergencyContact ->
							String FirstName = current_EmergencyContact?.FirstName
							String LastName = current_EmergencyContact?.LastName
							String vname = (StringUtil.isNotEmpty(FirstName)? FirstName  :'' ) + ' ' + (StringUtil.isNotEmpty(LastName) ? LastName :'' )
							'Group31_CTA' {
								'CTA' {
									'E3139_01' 'HG'
									'C056_02' {
										'E3413_01' ''
										'E3412_02' util.substring(vname,1,35)
									}
								}
								if(StringUtil.isNotEmpty(current_EmergencyContact?.ContactPhone?.Number)){
									'COM' {
										'C076_01' {
											'E3148_01' current_EmergencyContact?.ContactPhone?.Number
											'E3155_02' 'TE'
										}
									}
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
			}
		}
		current_Body?.Container?.each { current_Container ->
			'Group35_EQD' {
				'EQD' {
					'E8053_01' 'CN'
					'C237_02' {
						'E8260_01' "${StringUtil.isNotEmpty(current_Container?.ContainerNumber) ? current_Container?.ContainerNumber : ''}${StringUtil.isNotEmpty(current_Container?.ContainerCheckDigit) ? current_Container?.ContainerCheckDigit : ''}"
						'E1131_02' ''
						'E3055_03' ''
						'E3207_04' ''
					}
					String containerSizeType = util.getConversion(TP_ID, "BL", "O", "ContainerType", current_Container?.CS1ContainerSizeType, conn)
					'C224_03' {
						'E8155_01' StringUtil.isNotEmpty(containerSizeType) ? containerSizeType : current_Container?.CS1ContainerSizeType
						'E1131_02' ''
						'E3055_03' ''
						'E8154_04' ''
					}
					Map<String, String> equipmentSupplierMap = ['true':'1','false':'2']
					'E8077_04' equipmentSupplierMap.get(current_Container?.IsSOC)
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
				if(StringUtil.isNotEmpty(current_Container?.GrossWeight?.Weight) && StringUtil.isNotEmpty(current_Container?.GrossWeight?.WeightUnit)) {
					'MEA' {
						'E6311_01' 'WT'
						'C502_02' {
							'E6313_01' 'G'
							'E6321_02' ''
							'E6155_03' ''
							'E6154_04' ''
						}
						'C174_03' {
							'E6411_01' 'KGM'
							if(current_Container?.GrossWeight?.WeightUnit == "KGS" || current_Container?.GrossWeight?.WeightUnit == "KGM" ){
								'E6314_02' blUtil.replaceZeroAfterPoint(current_Container?.GrossWeight?.Weight?.toBigDecimal()?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString())
							}else if(current_Container?.GrossWeight?.WeightUnit == "LBS"){
								'E6314_02' blUtil.replaceZeroAfterPoint(current_Container?.GrossWeight?.Weight?.toBigDecimal()?.multiply(rateLBS)?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString())
							}else if(current_Container?.GrossWeight?.WeightUnit == "TON"){
								'E6314_02' blUtil.replaceZeroAfterPoint(current_Container?.GrossWeight?.Weight?.toBigDecimal()?.multiply(rateTon)?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString())
							}
							'E6162_03' ''
							'E6152_04' ''
							'E6432_05' ''
						}
						'E7383_04' ''
					}
				}
				List<Cargo> assoCargo = current_Body?.Cargo?.findAll{it?.CurrentContainerNumber == current_Container?.ContainerNumber}
				if(assoCargo && assoCargo[0]?.ReeferCargoSpec?.findAll{StringUtil.isNotEmpty(it?.Ventilation?.VentilationUnit) && StringUtil.isNotEmpty(it?.Ventilation?.Ventilation)}?.size() > 0) {
					'MEA' {
						'E6311_01' 'AAE'
						'C502_02' {
							if(assoCargo?.find{StringUtil.isNotEmpty(it?.Volume?.Volume) && StringUtil.isNotEmpty(it?.Volume?.VolumeUnit)}){
								'E6313_01' 'AAS'
							}
							'E6321_02' ''
							'E6155_03' ''
							'E6154_04' ''
						}
						'C174_03' {
							if(assoCargo[0]?.ReeferCargoSpec?.findAll{it?.Ventilation?.VentilationUnit == 'cbfPerMin' && StringUtil.isNotEmpty(it?.Ventilation?.Ventilation)}?.size() > 0){
								'E6411_01' 'CBM'
								'E6314_02' blUtil.replaceZeroAfterPoint(assoCargo[0]?.ReeferCargoSpec?.find{it?.Ventilation?.VentilationUnit == 'cbfPerMin'}?.Ventilation?.Ventilation?.toBigDecimal()?.multiply(rateCBF)?.add(new BigDecimal(0.000000001))?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString())
							}else if(assoCargo[0]?.ReeferCargoSpec?.findAll{it?.Ventilation?.VentilationUnit == 'cbmPerHour' && StringUtil.isNotEmpty(it?.Ventilation?.Ventilation) }?.size() > 0){
								'E6411_01' 'CBM'
								'E6314_02' blUtil.replaceZeroAfterPoint(assoCargo[0]?.ReeferCargoSpec?.find{it?.Ventilation?.VentilationUnit == 'cbmPerHour'}?.Ventilation?.Ventilation?.toBigDecimal()?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString())
							}else if(assoCargo[0]?.ReeferCargoSpec?.findAll{it?.Ventilation?.VentilationUnit == 'percentage' && StringUtil.isNotEmpty(it?.Ventilation?.Ventilation)}?.size() > 0){
								'E6411_01' 'PER'
								'E6314_02' blUtil.replaceZeroAfterPoint(assoCargo[0]?.ReeferCargoSpec?.find{it?.Ventilation?.VentilationUnit == 'percentage'}?.Ventilation?.Ventilation?.toBigDecimal()?.setScale(3, BigDecimal.ROUND_HALF_UP)?.toString())
							}
							'E6162_03' ''
							'E6152_04' ''
							'E6432_05' ''
						}
						'E7383_04' ''
					}
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
				current_Container?.Seal?.each { current_Seal ->
					'SEL' {
						'E9308_01' util.substring(current_Seal?.SealNumber, 1, 35)
						'C215_02' {
							'E9303_01' (['SH','CA'].contains(current_Seal?.SealType) ? current_Seal?.SealType : '')
							'E1131_02' ''
							'E3055_03' ''
							'E9302_04' ''
						}
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
				Map<String, String> temperatureUnitMap = ['C':'CEL','F':'FAH']
				if(assoCargo?.find{it?.ReeferCargoSpec?.size() != 0}) {
					'TMP' {
						'E6245_01' '2'
						'C239_02' {
							String temperature = assoCargo?.find{it?.ReeferCargoSpec != null}?.ReeferCargoSpec[0]?.Temperature?.Temperature
							if(temperature){
								temperature = "${ temperature?.toBigDecimal() < 0 ? '-' : ''}${String.format("%3s", temperature?.toBigDecimal()?.abs()?.setScale(0, BigDecimal.ROUND_FLOOR)?.toString()).replace(" ","0")}"
								if(temperature == "000"){
									temperature = "0"
								}
							}else{
								temperature = '0'
							}
							'E6246_01' temperature
							'E6411_02' temperatureUnitMap.get(assoCargo?.find{it?.ReeferCargoSpec?.find{StringUtil.isNotEmpty(it?.Temperature?.TemperatureUnit)}}?.ReeferCargoSpec?.get(0)?.Temperature?.TemperatureUnit)
						}
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
						'C819_07' {
							'E3229_01' ''
							'E1131_02' ''
							'E3055_03' ''
							'E3228_04' ''
						}
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
				'Group39_DGS' {
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
					'Group40_CTA' {
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
			'E0074_01' '1'
			'E0062_02' '1'
		}
		'UNE' {
			'E0060_01' ''
			'E0048_02' ''
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


