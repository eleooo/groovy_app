package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.bl.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.DecimalFormat

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

	public void generateBody(Body current_Body,Header header, Map<cs.b2b.core.mapping.bean.bl.Container, List<cs.b2b.core.mapping.bean.bl.Cargo>> associateContainerAndCargo,List<FreightCharge> filteredFreightCharge, List<FreightChargeCNTR> filteredFreightChargeCNTR, MarkupBuilder outXml) {

		//println getPort_list("Waihai","CNWAI",conn)
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
//				'E0068_03' ''
//				'S010_04' {
//					'E0070_01' ''
//					'E0073_02' ''
//				}
			}
			'BGM' {
				Map<String, String> blTypeMap = ['Original': '705', 'Sea WayBill': '710']
				Map<String, String> transactionInformationActionMap = ['DEL': '1', 'NEW': '9', 'UPD': '5']
				'C002_01' {
					'E1001_01' blTypeMap[current_Body?.GeneralInformation?.BLType] ?: ""
//					'E1131_02' ''
//					'E3055_03' ''
//					'E1000_04' ''
				}
				'C106_02' {
					'E1004_01' current_Body?.GeneralInformation?.BLNumber
//					'E1056_02' ''
//					'E1060_03' '
				}
				'E1225_03' transactionInformationActionMap[header?.Action]
//				'E4343_04' ''
			}
//			'CTA' {
//				'E3139_01' ''
//				'C056_02' {
//					'E3413_01' ''
//					'E3412_02' ''
//				}
//			}
//			'COM' {
//				'C076_01' {
//					'E3148_01' ''
//					'E3155_02' ''
//				}
//			}
			//!dont understant
			'DTM' {
				'C507_01' {
					'E2005_01' '182'
					if (StringUtil.isNotEmpty(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.toString())) {
						'E2380_02' util.convertDateTime(current_Body.GeneralInformation.BLIssueDT.LocDT, xmlDateTimeFormat, "yyyyMMddHHmm")
					}
					'E2379_03' '203'
				}
			}
			//GET
			'TSR' {
				'C536_01' {
					Map haulageMap = ['C_C': '27', 'C_M': '29', 'M_C': '28', 'M_M': '30']
					'E4065_01' haulageMap.get(current_Body?.Route?.Haulage?.InBound + "_" + current_Body?.Route?.Haulage?.OutBound)
//					'E1131_02' ''
//					'E3055_03' ''
				}
//				'C233_02' {
//					'E7273_01' ''
//					'E1131_02' ''
//					'E3055_03' ''
//					'E7273_04' ''
//					'E1131_05' ''
//					'E3055_06' ''
//				}
//				'C537_03' {
//					'E4219_01' ''
//					'E1131_02' ''
//					'E3055_03' ''
//				}
//				'C703_04' {
//					'E7085_01' ''
//					'E1131_02' ''
//					'E3055_03' ''
//				}
			}
//			'CUX' {
//				'C504_01' {
//					'E6347_01' ''
//					'E6345_02' ''
//					'E6343_03' ''
//					'E6348_04' ''
//				}
//				'C504_02' {
//					'E6347_01' ''
//					'E6345_02' ''
//					'E6343_03' ''
//					'E6348_04' ''
//				}
//				'E5402_03' ''
//				'E6341_04' ''
//			}
//			'MOA' {
//				'C516_01' {
//					'E5025_01' ''
//					'E5004_02' ''
//					'E6345_03' ''
//					'E6343_04' ''
//					'E4405_05' ''
//				}
//			}
			if(current_Body?.Remarks){
				'FTX' {
					'E4451_01' 'AAS'
//				'E4453_02' ''
//				'C107_03' {
//					'E4441_01' ''
//					'E1131_02' ''
//					'E3055_03' ''
//				}
					'C108_04' {
						'E4440_01' util.substring(current_Body?.Remarks,1,70)?:''
						'E4440_02' util.substring(current_Body?.Remarks,71,70)?:''
						'E4440_03' util.substring(current_Body?.Remarks,141,70)?:''
						'E4440_04' util.substring(current_Body?.Remarks,211,70)?:''
						'E4440_05' util.substring(current_Body?.Remarks,281,70)?:''
					}
//				'E3453_05' ''
				}
			}

//			'CNT' {
//				'C270_01' {
//					'E6069_01' ''
//					'E6066_02' ''
//					'E6411_03' ''
//				}
//			}
//			'Group1_LOC' {
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
//				'DTM' {
//					'C507_01' {
//						'E2005_01' ''
//						'E2380_02' ''
//						'E2379_03' ''
//					}
//				}
//			}
//			'Group2_TOD' {
//				'TOD' {
//					'E4055_01' ''
//					'E4215_02' ''
//					'C100_03' {
//						'E4053_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//						'E4052_04' ''
//						'E4052_05' ''
//					}
//				}
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
//			}



			def BLIssueDTFlag   = false;
			def BLReceiptDTFlag = false;
			def BLOnboardDTFlag = false;
			if(current_Body?.GeneralInformation?.BLIssueDT?.LocDT) BLIssueDTFlag = true

			//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:BLReceiptDT-BLInformationType/ns1:LocDT!=''
			if(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT) BLReceiptDTFlag = true

			//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:BLOnboardDT-BLInformationType/ns1:GMT!=''
			if(current_Body?.GeneralInformation?.BLOnboardDT?.GMT) BLOnboardDTFlag = true


			'Group3_RFF' {
				'RFF' {
					'C506_01' {
						//RFF_01_01
						'E1153_01' 'BM'
						///RFF_01_02
						'E1154_02' current_Body?.GeneralInformation?.BLNumber
//						'E1156_03' ''
//						'E4000_04' ''
					}
				}
//				'DTM' {
//					'C507_01' {
//						//value1  $PreDTM/root/preDTM[number($Start/root/GRP-INDEX)]/value1
//						'E2005_01' ''
//						//substring(tib:format-dateTime('yyyyMMddHHmmss', tib:trim($Start/root/value2)), 1,8)  $PreDTM/root/preDTM[number($Start/root/GRP-INDEX)]/value2
//						'E2380_02' ''
//						'E2379_03' '102'
//					}
//				}
				//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:BLIssueDT-BLInformationType/ns1:LocDT!=''
				//falg means the DTM can be add
				def flag = true
				if(BLIssueDTFlag && flag){
					'DTM' {
						'C507_01' {
							'E2005_01' '182'
							'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.toString(),xmlDateTimeFormat,yyyyMMdd)
							'E2379_03' '102'
						}
					}
					flag=false
					BLIssueDTFlag=false
				}
				if(BLReceiptDTFlag && flag){
					'DTM' {
						'C507_01' {
							'E2005_01' '310'
							'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT,xmlDateTimeFormat,yyyyMMdd)
							'E2379_03' '102'
						}
					}
					BLReceiptDTFlag=false
					flag=false
				}
				if(BLOnboardDTFlag && flag){
					'DTM' {
						'C507_01' {
							'E2005_01' '342'
							'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT,xmlDateTimeFormat,yyyyMMdd)
							'E2379_03' '102'
						}
					}
					BLOnboardDTFlag=false
					flag=false
				}
			}

			//cant understant the three if
			current_Body?.GeneralInformation?.CarrierBookingNumber?.each { current_CarrierBookingNumber ->
				'Group3_RFF' {
					'RFF' {
						'C506_01' {
							'E1153_01' 'BN'
							'E1154_02' current_CarrierBookingNumber
//							'E1156_03' ''
//							'E4000_04' ''
						}
					}
					//falg means the DTM can be add
					def flag = true
					if(BLIssueDTFlag && flag){
						'DTM' {
							'C507_01' {
								'E2005_01' '182'
								'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.toString(),xmlDateTimeFormat,yyyyMMdd)
								'E2379_03' '102'
							}
						}
						flag=false
						BLIssueDTFlag=false
					}
					//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:BLReceiptDT-BLInformationType/ns1:LocDT!=''
					if(BLReceiptDTFlag && flag){
						'DTM' {
							'C507_01' {
								'E2005_01' '310'
								'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT,xmlDateTimeFormat,yyyyMMdd)
								'E2379_03' '102'
							}
						}
						BLReceiptDTFlag=false
						flag=false
					}
					//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:BLOnboardDT-BLInformationType/ns1:GMT!=''
					if(BLOnboardDTFlag && flag){
						'DTM' {
							'C507_01' {
								'E2005_01' '342'
								'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT,xmlDateTimeFormat,yyyyMMdd)
								'E2379_03' '102'
							}
						}
						BLOnboardDTFlag=false
						flag=false
					}

				}
			}
			def conversionMap = ['CR':'CR','INV':'IV','PO':'PO','SC':'CT','SID':'SI']
			current_Body?.ExternalReference?.each { current_ExternalReference ->
				'Group3_RFF' {
					'RFF' {
						'C506_01' {
							if(conversionMap.get(current_ExternalReference?.CSReferenceType)){
								'E1153_01' conversionMap.get(current_ExternalReference?.CSReferenceType)
								'E1154_02' current_ExternalReference?.ReferenceNumber
							}
//							'E1156_03' ''
//							'E4000_04' '
						}
					}
					//falg means the DTM can be add
					def flag = true
					if(BLIssueDTFlag && flag){
						'DTM' {
							'C507_01' {
								'E2005_01' '182'
								'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.toString(),xmlDateTimeFormat,yyyyMMdd)
								'E2379_03' '102'
							}
						}
						flag=false
						BLIssueDTFlag=false
					}
					//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:BLReceiptDT-BLInformationType/ns1:LocDT!=''
					if(BLReceiptDTFlag && flag){
						'DTM' {
							'C507_01' {
								'E2005_01' '310'
								'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT,xmlDateTimeFormat,yyyyMMdd)
								'E2379_03' '102'
							}
						}
						BLReceiptDTFlag=false
						flag=false
					}
					//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:BLOnboardDT-BLInformationType/ns1:GMT!=''
					if(BLOnboardDTFlag && flag){
						'DTM' {
							'C507_01' {
								'E2005_01' '342'
								'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT,xmlDateTimeFormat,yyyyMMdd)
								'E2379_03' '102'
							}
						}
						BLOnboardDTFlag=false
						flag=false
					}
				}
			}
			current_Body?.CarrierRate?.each { current_CarrierRate ->

				'Group3_RFF' {
					'RFF' {
						'C506_01' {
							if(conversionMap.get(current_CarrierRate?.CSCarrierRateType)){
								'E1153_01' conversionMap.get(current_CarrierRate?.CSCarrierRateType)
								'E1154_02' current_CarrierRate?.CarrierRateNumber
							}
//							'E1156_03' ''
//							'E4000_04' '
						}
					}
					//falg means the DTM can be add
					def flag = true
					if(current_Body?.GeneralInformation?.BLIssueDT?.LocDT!=''){
						if(BLIssueDTFlag && flag){
							'DTM' {
								'C507_01' {
									'E2005_01' '182'
									'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLIssueDT?.LocDT?.toString(),xmlDateTimeFormat,yyyyMMdd)
									'E2379_03' '102'
								}
							}
							flag=false
							BLIssueDTFlag=false
						}
					}
					//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:BLReceiptDT-BLInformationType/ns1:LocDT!=''
					if(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT!=''){
						if(BLReceiptDTFlag && flag){
							'DTM' {
								'C507_01' {
									'E2005_01' '310'
									'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLReceiptDT?.LocDT,xmlDateTimeFormat,yyyyMMdd)
									'E2379_03' '102'
								}
							}
							BLReceiptDTFlag=false
							flag=false
						}
					}
					//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:BLOnboardDT-BLInformationType/ns1:GMT!=''
					if(current_Body?.GeneralInformation?.BLOnboardDT?.GMT!=''){
						if(BLOnboardDTFlag && flag){
							'DTM' {
								'C507_01' {
									'E2005_01' '342'
									'E2380_02' util.convertDateTime(current_Body?.GeneralInformation?.BLOnboardDT?.GMT,xmlDateTimeFormat,yyyyMMdd)
									'E2379_03' '102'
								}
							}
							BLOnboardDTFlag=false
							flag=false
						}
					}
				}
			}

//			'Group4_GOR' {
//				'GOR' {
//					'E8323_01' ''
//					'C232_02' {
//						'E9415_01' ''
//						'E9411_02' ''
//						'E9417_03' ''
//						'E9353_04' ''
//					}
//					'C232_03' {
//						'E9415_01' ''
//						'E9411_02' ''
//						'E9417_03' ''
//						'E9353_04' ''
//					}
//					'C232_04' {
//						'E9415_01' ''
//						'E9411_02' ''
//						'E9417_03' ''
//						'E9353_04' ''
//					}
//					'C232_05' {
//						'E9415_01' ''
//						'E9411_02' ''
//						'E9417_03' ''
//						'E9353_04' ''
//					}
//				}
//				'DTM' {
//					'C507_01' {
//						'E2005_01' ''
//						'E2380_02' ''
//						'E2379_03' ''
//					}
//				}
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
//				'SEL' {
//					'E9308_01' ''
//					'C215_02' {
//						'E9303_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//						'E9302_04' ''
//					}
//					'E4517_03' ''
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
//				}
//				'Group5_DOC' {
//					'DOC' {
//						'C002_01' {
//							'E1001_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E1000_04' ''
//						}
//						'C503_02' {
//							'E1004_01' ''
//							'E1373_02' ''
//							'E1366_03' ''
//							'E3453_04' ''
//						}
//						'E3153_03' ''
//						'E1220_04' ''
//						'E1218_05' ''
//					}
//					'DTM' {
//						'C507_01' {
//							'E2005_01' ''
//							'E2380_02' ''
//							'E2379_03' ''
//						}
//					}
//				}
//			}
			'Group6_CPI' {
				'CPI' {
					'C229_01' {
						//SegmentLoopList is add by myslfes
						//'SegmentLoopList' current_Body?.FreightCharge[0]
						'E5237_01' '4'
//						'E1131_02' ''
//						'E3055_03' ''
					}
//					'C231_02' {
//						'E4215_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//					}
//					'E4237_03' ''
				}
//				'RFF' {
//					'C506_01' {
//						'E1153_01' ''
//						'E1154_02' ''
//						'E1156_03' ''
//						'E4000_04' ''
//					}
//				}
//				'CUX' {
//					'C504_01' {
//						'E6347_01' ''
//						'E6345_02' ''
//						'E6343_03' ''
//						'E6348_04' ''
//					}
//					'C504_02' {
//						'E6347_01' ''
//						'E6345_02' ''
//						'E6343_03' ''
//						'E6348_04' ''
//					}
//					'E5402_03' ''
//					'E6341_04' ''
//				}
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
//				'MOA' {
//					'C516_01' {
//						'E5025_01' ''
//						'E5004_02' ''
//						'E6345_03' ''
//						'E6343_04' ''
//						'E4405_05' ''
//					}
//				}
			}
			current_Body?.FreightCharge?.eachWithIndex { current_FreightCharge, FreightChargeIndex ->
				'Group7_TCC' {
					'TCC' {
						'C200_01' {
//							'E8023_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
							//$Start/root/pfx4:Body-BillOfLading/pfx4:FreightCharge-Body-BillOfLading[number(../../index_TCC)]/pfx4:ChargeCode-FreightCharge-Body-BillOfLading
							//31
							'E8022_04' current_FreightCharge?.ChargeDesc
							Map<String, String> paymentMethodMap = [(blUtil.COLLECT): 'C', (blUtil.PREPAID): 'P']
							'E4237_05' paymentMethodMap.get(current_FreightCharge?.ChargeType)
//							'E7140_06' ''
						}
						'C203_02' {
							'E5243_01' 'N'
//							'E1131_02' ''
//							'E3055_03' ''
//							'E5275_05' ''
//							'E1131_06' ''
//							'E3055_07' ''
//							'E5275_08' ''
//							'E1131_09' ''
//							'E3055_10' '
							'E5242_04' Double.parseDouble(current_FreightCharge?.FreightRate)<0?new DecimalFormat("0000000000.000000").format(Double.parseDouble(current_FreightCharge?.FreightRate)):'+'+new DecimalFormat("0000000000.000000").format(Double.parseDouble(current_FreightCharge?.FreightRate))
//							'E5275_05' ''
//							'E1131_06' ''
//							'E3055_07' ''
//							'E5275_08' ''
//							'E1131_09' ''
//							'E3055_10' ''
						}
//						'C528_03' {
//							'E7357_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//						}
//						'C554_04' {
//							'E5243_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//						}
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
//					'FTX' {
//						'E4451_01' ''
//						'E4453_02' ''
//						'C107_03' {
//							'E4441_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//						}
//						'C108_04' {
//							'E4440_01' ''
//							'E4440_02' ''
//							'E4440_03' ''
//							'E4440_04' ''
//							'E4440_05' ''
//						}
//						'E3453_05' ''
//					}
//					'CUX' {
//						'C504_01' {
//							'E6347_01' ''
//							'E6345_02' ''
//							'E6343_03' ''
//							'E6348_04' ''
//						}
//						'C504_02' {
//							'E6347_01' ''
//							'E6345_02' ''
//							'E6343_03' ''
//							'E6348_04' ''
//						}
//						'E5402_03' ''
//						'E6341_04' ''
//					}
//					'PRI' {
//						'C509_01' {
//							'E5125_01' ''
//							'E5118_02' ''
//							'E5375_03' ''
//							'E5387_04' ''
//							'E5284_05' ''
//							'E6411_06' ''
//						}
//						'E5213_02' ''
//					}
//					'EQN' {
//						'C523_01' {
//							'E6350_01' ''
//							'E6353_02' ''
//						}
//					}
//					'PCD' {
//						'C501_01' {
//							'E5245_01' ''
//							'E5482_02' ''
//							'E5249_03' ''
//							'E1131_04' ''
//							'E3055_05' ''
//						}
//					}
					'MOA' {
						'C516_01' {
							//--
							if(current_FreightCharge?.ChargeType=='1'){
								'E5025_01' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency?.trim()=='USD'?'36':'23'
							}else if(current_FreightCharge?.ChargeType=='0'){
								'E5025_01' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency?.trim()=='USD'?'36':'23'
							}
							'E5004_02' Double.parseDouble(current_FreightCharge?.TotalAmtInPmtCurrency?.toString())<0? new DecimalFormat("0000000000.00").format(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()?.toBigDecimal()):'+'+ new DecimalFormat("0000000000.00").format(current_FreightCharge?.TotalAmtInPmtCurrency?.toString()?.toBigDecimal())
							//--
							if(current_FreightCharge?.ChargeType=='0'){
								'E6345_03' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency?.trim()
							}else if(current_FreightCharge?.ChargeType=='1'){
								'E6345_03' current_FreightCharge?.TotalAmtInPmtCurrency?.attr_Currency?.trim()
							}

//							'E6343_04' ''
//							'E4405_05' ''
						}
					}
//					'QTY' {
//						'C186_01' {
//							'E6063_01' ''
//							'E6060_02' ''
//							'E6411_03' ''
//						}
//					}
				}
			}

			'Group8_TDT' {
				'TDT' {
					'E8051_01' '20'
					//--
					//$Start/root/pfx:Body-BillOfLading/pfx:GeneralInformation-Body-BillOfLading/pfx:SPCompanyID-BLInformationType
					'E8028_02' current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.Direction?"${current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.Voyage?:''}${current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.Direction?:''}":"${current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.Voyage?:''}"
					'C220_03' {
						'E8067_01' '1'
						'E8066_02' ''
					}
//					'C228_04' {
//						'E8179_01' ''
//						'E8178_02' ''
//					}
					//--
					'C040_05' {
						//!
						'E3127_01' current_Body?.GeneralInformation?.SPCompanyID=="COSCCN36078928199562" ||current_Body?.GeneralInformation?.SPCompanyID=="COSU" ||current_Body?.GeneralInformation?.SPCompanyID=="COSCON" ?'COSU':''
						'E1131_02' '172'
						'E3055_03' '182'
//						'E3128_04' ''
					}
//					'E8101_06' ''
//					'C401_07' {
//						'E8457_01' ''
//						'E8459_02' ''
//						'E7130_03' ''
//					}
					'C222_08' {
						//dont true
						if(current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.CallSign){
							'E8213_01'  current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.CallSign
						}else if(current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.LloydsNumber){
							'E8213_01'  current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.LloydsNumber
						}
						'E1131_02' current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.CallSign?'103':(current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.LloydsNumber?'ZZZ':'')
//						'E3055_03' ''
						'E8212_04' current_Body?.Route?.OceanLeg[-1]?.SVVD?.Loading?.VesselName?.trim()
//						'E8453_05' ''
					}
//					'E8281_09' ''
				}
//				'DTM' {
//					'C507_01' {
//						'E2005_01' ''
//						'E2380_02' ''
//						'E2379_03' ''
//					}
//				}
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
				if(current_Body?.Route?.POR?.CityDetails?.City || current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode){
					'Group9_LOC' {
						//Group8_Group9_LOC_5_02_02  cant find ??
						'LOC' {
							//--cant find
							'E3227_01' '88'
							//-- all
							'C517_02' {
								'E3225_01' current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
								//gesst
//								tib:trim($Start/root/RouteInfomation/OrgCity)!='' or
//								tib:trim($Start/root/RouteInfomation/OrgUNLOCCode)!=''
//								and
//								$FindPortList/resultSet/Record[1]/COUNT>0
								if(current_Body?.Route?.POR?.CityDetails?.City  || (current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode && Integer.parseInt(getPort_list(current_Body?.Route?.POR?.CityDetails?.City,current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode,conn).toString())) ){
									'E1131_02' '139'
								}else{
									'E1131_02' '229'
								}
								'E3055_03' '6'
								'E3224_04' current_Body?.Route?.POR?.CityDetails?.City
							}
							//-- all
							'C519_03' {
								'E3223_01' current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
								'E1131_02' '145'
								'E3055_03' '6'
								'E3222_04' current_Body?.Route?.POR?.CityDetails?.City
							}
							//-- all
							'C553_04' {
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:POR-Route-Body-BillOfLading/ns1:CSStandardCity/ns1:CSCountryCode)
								'E3233_01' current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode
								'E1131_02' '162'
								'E3055_03' '5'
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:POR-Route-Body-BillOfLading/ns1:CSStandardCity/ns1:CSCountryCode)
								println getCntry_nme(current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode,conn)
								'E3232_04' current_Body?.Route?.POR?.CityDetails?.Country?:getCntry_nme(current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode,conn)
							}
							'E5479_05' ''
						}
						'DTM' {
							//--
							'C507_01' {
								//--
								//tib:trim($Start/root/pfx3:Body-BillOfLading/pfx3:Route-Body-BillOfLading/pfx3:POR-Route-Body-BillOfLading/ns:CityDetails/ns:City)!='' or
								//tib:trim($Start/root/pfx3:Body-BillOfLading/pfx3:Route-Body-BillOfLading/pfx3:POR-Route-Body-BillOfLading/ns:CityDetails/ns:LocationCode/ns:UNLocationCode)!=''
								'E2005_01' ''
								//--
								'E2380_02' ''
								//--
//								'E2379_03' '102'
							}
						}
					}
				}
				if(current_Body?.Route?.FirstPOL?.Port?.City || current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode){
					'Group9_LOC' {
						//Group8_Group9_LOC_5_02_02  cant find ??
						'LOC' {
							//--cant find
							'E3227_01' '9'
							//-- all
							'C517_02' {
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:FirstPOL-Route-Body-BillOfLading/ns1:Port/ns1:LocationCode/ns1:UNLocationCode)
								'E3225_01' current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode
								//tib:trim($Start/root/RouteInfomation/FirstPOLCity)!='' or
								//tib:trim($Start/root/RouteInfomation/FPolUNLOCCode)!=''
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:FirstPOL-Route-Body-BillOfLading/ns1:Port/ns1:City)
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:FirstPOL-Route-Body-BillOfLading/ns1:Port/ns1:LocationCode/ns1:UNLocationCode)
								if(current_Body?.Route?.FirstPOL?.Port?.City || current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode ){
									'E1131_02'  '139'
								}
								'E3055_03' '6'
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:FirstPOL-Route-Body-BillOfLading/ns1:Port/ns1:PortName)
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:FirstPOL-Route-Body-BillOfLading/ns1:Port/ns1:City)
								'E3224_04' current_Body?.Route?.FirstPOL?.Port?.PortName?:current_Body?.Route?.FirstPOL?.Port?.City
							}
							//-- all
							'C519_03' {
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:FirstPOL-Route-Body-BillOfLading/ns1:Port/ns1:LocationCode/ns1:UNLocationCode)
								'E3223_01' current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode
								'E1131_02' '145'
								'E3055_03' '6'
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:FirstPOL-Route-Body-BillOfLading/ns1:Port/ns1:City)
								'E3222_04' current_Body?.Route?.FirstPOL?.Port?.City
							}
							//-- all
							'C553_04' {
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:FirstPOL-Route-Body-BillOfLading/ns1:Port/ns1:CSCountryCode)
								'E3233_01' current_Body?.Route?.FirstPOL?.Port?.CSCountryCode
								'E1131_02' '162'
								'E3055_03' '5'
								//println getCntry_nme(current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode,conn)
								'E3232_04' current_Body?.Route?.FirstPOL?.Port?.Country?:getCntry_nme(current_Body?.Route?.FirstPOL?.Port?.CSCountryCode,conn)
							}
//							'E5479_05' ''
						}
						'DTM' {
							'C507_01' {
								//--
								//tib:trim($Start/root/pfx3:Body-BillOfLading/pfx3:Route-Body-BillOfLading/pfx3:FirstPOL-Route-Body-BillOfLading/ns:Port/ns:City)!='' or
								//tib:trim($Start/root/pfx3:Body-BillOfLading/pfx3:Route-Body-BillOfLading/pfx3:FirstPOL-Route-Body-BillOfLading/ns:Port/ns:LocationCode/ns:UNLocationCode)!=''
								if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode || current_Body?.Route?.FirstPOL?.Port?.City){
									if(current_Body?.Route?.DepartureDT?.LocDT){
										//--
										'E2005_01' '133'
										//tib:trim($Start/root/pfx3:Body-BillOfLading/pfx3:Route-Body-BillOfLading/pfx3:DepartureDT-Route-Body-BillOfLading/ns:LocDT)!=""
										//substring(tib:format-dateTime('yyyyMMddHHmmss', $Start/root/pfx3:Body-BillOfLading/pfx3:Route-Body-BillOfLading/pfx3:DepartureDT-Route-Body-BillOfLading/ns:LocDT), 1,12)
										'E2380_02' current_Body?.Route?.DepartureDT?.LocDT ? util.substring(util.convertDateTime(current_Body?.Route?.DepartureDT?.LocDT,xmlDateTimeFormat,yyyyMMdd),1,12):''
										'E2379_03' '102'
									}
								}
							}
						}
					}
				}
				if(current_Body?.Route?.LastPOD?.Port?.City || current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode){
					'Group9_LOC' {
						//Group8_Group9_LOC_5_02_02  cant find ??
						'LOC' {
							//--cant find
							'E3227_01' '11'
							//-- all
							'C517_02' {
								'E3225_01' current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode
								//tib:trim($Start/root/RouteInfomation/LastPODCity)!='' or tib:trim($Start/root/RouteInfomation/LPodUNLOCCode)!=''
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:LastPOD-Route-Body-BillOfLading/ns1:Port/ns1:City)
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:LastPOD-Route-Body-BillOfLading/ns1:Port/ns1:LocationCode/ns1:UNLocationCode)
								if(current_Body?.Route?.LastPOD?.Port?.City && current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode ){
									'E1131_02' '139'
								}
								'E3055_03' '6'
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:LastPOD-Route-Body-BillOfLading/ns1:Port/ns1:PortName)
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:LastPOD-Route-Body-BillOfLading/ns1:Port/ns1:City)
								'E3224_04' current_Body?.Route?.LastPOD?.Port ?. PortName?:current_Body?.Route?.LastPOD?.Port?.City
							}
							//-- all
							'C519_03' {
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:LastPOD-Route-Body-BillOfLading/ns1:Port/ns1:LocationCode/ns1:UNLocationCode)
								'E3223_01' current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode
								'E1131_02' '145'
								'E3055_03' '6'
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:LastPOD-Route-Body-BillOfLading/ns1:Port/ns1:City)
								'E3222_04' current_Body?.Route?.LastPOD?.Port?.City
							}
							//-- all
							'C553_04' {
								//tib:trim(/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:LastPOD-Route-Body-BillOfLading/ns1:Port/ns1:CSCountryCode)
								'E3233_01' current_Body?.Route?.LastPOD?.Port?.CSCountryCode
								'E1131_02' '162'
								'E3055_03' '5'
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:LastPOD-Route-Body-BillOfLading/ns1:Port/ns1:Country)
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:LastPOD-Route-Body-BillOfLading/ns1:Port/ns1:CSCountryCode)
								'E3232_04' current_Body?.Route?.LastPOD?.Port?.Country?: getCntry_nme(current_Body?.Route?.LastPOD?.Port?.CSCountryCode,conn)
							}
//							'E5479_05' ''
						}
						'DTM' {
							'C507_01' {
								//tib:trim($Start/root/pfx3:Body-BillOfLading/pfx3:Route-Body-BillOfLading/pfx3:ArrivalDT-Route-Body-BillOfLading/ns:LocDT)!=""
								//substring(tib:format-dateTime('yyyyMMddHHmmss', $Start/root/pfx3:Body-BillOfLading/pfx3:Route-Body-BillOfLading/pfx3:ArrivalDT-Route-Body-BillOfLading/ns:LocDT), 1,12)
								if(current_Body?.Route?.ArrivalDT?.LocDT){
									'E2005_01' '132'
									'E2380_02' util.substring(util.convertDateTime(current_Body?.Route?.ArrivalDT?.LocDT,xmlDateTimeFormat,yyyyMMdd),1,8)
									'E2379_03' '102'
								}
							}
						}
					}
				}
				if(current_Body?.Route?.FND?.CityDetails?.City || current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode){
					'Group9_LOC' {
						'LOC' {
							'E3227_01' '65'
							'C517_02' {
								'E3225_01' current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
								//dont true
								//tib:trim($Start/root/pfx:Body-BillOfLading/pfx:Route-Body-BillOfLading/pfx:FND-Route-Body-BillOfLading/ns1:CityDetails/ns1:LocationCode/ns1:UNLocationCode)
								if(current_Body?.Route?.FND?.CityDetails?.City && current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode){
									'E1131_02' '139'
								}else{
									'E1131_02' '229'
								}
								'E3055_03' '6'
								'E3224_04' current_Body?.Route?.FND?.CityDetails?.City
							}
							'C519_03' {
								'E3223_01' current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
								'E1131_02' '145'
								'E3055_03' '6'
								'E3222_04' current_Body?.Route?.FND?.CityDetails?.City
							}
							'C553_04' {
								'E3233_01' current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode
								'E1131_02' '162'
								'E3055_03' '5'
								println getCntry_nme(current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode,conn)
								'E3232_04' current_Body?.Route?.FND?.CityDetails?.Country?:getCntry_nme(current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode,conn)
							}
							'E5479_05' ''
						}
						'DTM' {
							'C507_01' {
								//dont ride
								'E2005_01' ''
								//--
								'E2380_02' ''
//								'E2379_03' '102'
							}
						}
					}
				}
//				'Group9_LOC' {
//					//Group8_Group9_LOC_5_02_02  cant find ??
//					'LOC' {
//						//--cant find
//						'E3227_01' ''
//						//-- all
//						'C517_02' {
//							'E3225_01' ''
//							'E1131_02' ''
//							'E3055_03' '6'
//							'E3224_04' ''
//						}
//						//-- all
//						'C519_03' {
//							'E3223_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E3222_04' ''
//						}
//						//-- all
//						'C553_04' {
//							'E3233_01' ''
//							'E1131_02' '145'
//							'E3055_03' '6'
//							'E3232_04' ''
//						}
//						'E5479_05' ''
//					}
//					'DTM' {
//						//--cant find
//						'C507_01' {
//							//--
//							'E2005_01' ''
//							//--
//							'E2380_02' ''
//							//--
//							'E2379_03' ''
//						}
//					}
//				}
//				'Group10_RFF' {
//					'RFF' {
//						'C506_01' {
//							'E1153_01' ''
//							'E1154_02' ''
//							'E1156_03' ''
//							'E4000_04' ''
//						}
//					}
//					'DTM' {
//						'C507_01' {
//							'E2005_01' ''
//							'E2380_02' ''
//							'E2379_03' ''
//						}
//					}
//				}
			}
			Map<String, String> partyTypeMap = ['ANP':'N2','CGN':'CN','FWD':'FW','SHP':'CZ','NPT':'N1']
			current_Body?.Party?.each {current_Party ->
				if(partyTypeMap.get(current_Party?.PartyType)) {
					'Group11_NAD' {
						'NAD' {
							//--
							'E3035_01' partyTypeMap.get(current_Party?.PartyType)
							'C082_02' {
								'E3039_01' current_Party?.CarrierCustomerCode
//								'E1131_02' ''
//								'E3055_03' ''
							}
							'C058_03' {
								//tib:concat-sequence-format($AccAddressLine/AccumulatedOutput/root/addressline, ' ')
								'E3124_01' util.substring("${current_Party?.Address?.AddressLines?.AddressLine[0] ?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[1]?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[2]?: ''}".trim(), 1, 35)
								'E3124_02' util.substring("${current_Party?.Address?.AddressLines?.AddressLine[0] ?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[1] ?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[2] ?: ''}".trim(),36,35)
								'E3124_03' util.substring("${current_Party?.Address?.AddressLines?.AddressLine[0] ?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[1] ?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[2] ?: ''}".trim(),71,35)
								'E3124_04' util.substring("${current_Party?.Address?.AddressLines?.AddressLine[0] ?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[1] ?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[2] ?: ''}".trim(),106,35)
								'E3124_05' util.substring("${current_Party?.Address?.AddressLines?.AddressLine[0] ?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[1] ?: ''}${" "}${current_Party?.Address?.AddressLines?.AddressLine[2] ?: ''}".trim(),141,35)
							}
							'C080_04' {
								'E3036_01' util.substring(current_Party?.PartyName,1,35)
								'E3036_02' util.substring(current_Party?.PartyName,36,35)
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
//							'E3229_07' ''
//							'E3251_08' ''
//							'E3207_09' ''
						}
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
//						'MOA' {
//							'C516_01' {
//								'E5025_01' ''
//								'E5004_02' ''
//								'E6345_03' ''
//								'E6343_04' ''
//								'E4405_05' ''
//							}
//						}
						'Group12_CTA' {
							'CTA' {
								if(current_Party?.Contact?.FirstName || current_Party?.Contact?.LastName ){
									'E3139_01' partyTypeMap.get(current_Party?.PartyType)=='CZ' ? 'SH' : partyTypeMap.get(current_Party?.PartyType)
									'C056_02' {
										'E3413_01' ''
										//tib:trim(substring(concat(tib:trim($Start/root/pfx4:Party-Body-BillOfLading/ns1:Contact/ns1:FirstName), " ",tib:trim($Start/root/pfx4:Party-Body-BillOfLading/ns1:Contact/ns1:LastName)), 1,35))
										//-- 31
										'E3412_02' util.substring("${current_Party?.Contact?.FirstName?:''}${current_Party?.Contact?.LastName?:''}".trim(),1,35)
									}
								}
							}
							//--
							'COM' {
								//add by myselt
								//--  31
								//'SegmentLoopList' ''
								//tib:trim($CountryCode/root/CountryCode)="" and tib:trim($AreaCode/root/AreaCode)="" and tib:trim($Number/root/Number)!=""
								if(current_Party?.Contact?.ContactPhone?.CountryCode || current_Party?.Contact?.ContactPhone?.AreaCode || current_Party?.Contact?.ContactPhone?.Number){
									'C076_01' {
										//tib:trim($CountryCode/root/CountryCode)="" and tib:trim($AreaCode/root/AreaCode)="" and tib:trim($Number/root/Number)!=""
										if(current_Party?.Contact?.ContactPhone?.CountryCode=="" && current_Party?.Contact?.ContactPhone?.AreaCode=="" && current_Party?.Contact?.ContactPhone?.Number) {
											'E3148_01' current_Party?.Contact?.ContactPhone?.Number
										}
										//tib:trim($CountryCode/root/CountryCode)!="" and tib:trim($AreaCode/root/AreaCode)="" and tib:trim($Number/root/Number)!=""
										else if(current_Party?.Contact?.ContactPhone?.CountryCode && current_Party?.Contact?.ContactPhone?.AreaCode=="" && current_Party?.Contact?.ContactPhone?.Number){
											//concat($CountryCode/root/CountryCode,"-",$AreaCode/root/AreaCode,"-",$Number/root/Number)
											'E3148_01' "${current_Party?.Contact?.ContactPhone?.CountryCode?:''}${"- -"}${current_Party?.Contact?.ContactPhone?.Number?:''}"
										}
										//starts-with(concat($CountryCode/root/CountryCode,"-",$AreaCode/root/AreaCode,"-",$Number/root/Number), "-")
										else if(current_Party?.Contact?.ContactPhone?.CountryCode==""){
											//substring-after(concat($CountryCode/root/CountryCode,"-",$AreaCode/root/AreaCode,"-",$Number/root/Number), "-")
											'E3148_01' "${current_Party?.Contact?.ContactPhone?.CountryCode?:''}${'-'}${current_Party?.Contact?.ContactPhone?.Number?:''}"
										}else{
											'E3148_01' "${current_Party?.Contact?.ContactPhone?.CountryCode?:''}${'-'}${current_Party?.Contact?.ContactPhone?.AreaCode?:''}${'-'}${current_Party?.Contact?.ContactPhone?.Number?:''}"
										}
										'E3155_02' 'TE'
									}
								}
							}
						}
//						'Group13_DOC' {
//							'DOC' {
//								'C002_01' {
//									'E1001_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E1000_04' ''
//								}
//								'C503_02' {
//									'E1004_01' ''
//									'E1373_02' ''
//									'E1366_03' ''
//									'E3453_04' ''
//								}
//								'E3153_03' ''
//								'E1220_04' ''
//								'E1218_05' ''
//							}
//							'DTM' {
//								'C507_01' {
//									'E2005_01' ''
//									'E2380_02' ''
//									'E2379_03' ''
//								}
//							}
//						}
//						'Group14_TCC' {
//							'TCC' {
//								'C200_01' {
//									'E8023_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E8022_04' ''
//									'E4237_05' ''
//									'E7140_06' ''
//								}
//								'C203_02' {
//									'E5243_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E5242_04' ''
//									'E5275_05' ''
//									'E1131_06' ''
//									'E3055_07' ''
//									'E5275_08' ''
//									'E1131_09' ''
//									'E3055_10' ''
//								}
//								'C528_03' {
//									'E7357_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//								'C554_04' {
//									'E5243_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//							}
//							'PRI' {
//								'C509_01' {
//									'E5125_01' ''
//									'E5118_02' ''
//									'E5375_03' ''
//									'E5387_04' ''
//									'E5284_05' ''
//									'E6411_06' ''
//								}
//								'E5213_02' ''
//							}
//							'EQN' {
//								'C523_01' {
//									'E6350_01' ''
//									'E6353_02' ''
//								}
//							}
//							'PCD' {
//								'C501_01' {
//									'E5245_01' ''
//									'E5482_02' ''
//									'E5249_03' ''
//									'E1131_04' ''
//									'E3055_05' ''
//								}
//							}
//							'MOA' {
//								'C516_01' {
//									'E5025_01' ''
//									'E5004_02' ''
//									'E6345_03' ''
//									'E6343_04' ''
//									'E4405_05' ''
//								}
//							}
//							'QTY' {
//								'C186_01' {
//									'E6063_01' ''
//									'E6060_02' ''
//									'E6411_03' ''
//								}
//							}
//						}
//						'Group15_RFF' {
//							'RFF' {
//								'C506_01' {
//									'E1153_01' ''
//									'E1154_02' ''
//									'E1156_03' ''
//									'E4000_04' ''
//								}
//							}
//							'DTM' {
//								'C507_01' {
//									'E2005_01' ''
//									'E2380_02' ''
//									'E2379_03' ''
//								}
//							}
//						}
//						'Group16_CPI' {
//							'CPI' {
//								'C229_01' {
//									'E5237_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//								'C231_02' {
//									'E4215_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//								'E4237_03' ''
//							}
//							'RFF' {
//								'C506_01' {
//									'E1153_01' ''
//									'E1154_02' ''
//									'E1156_03' ''
//									'E4000_04' ''
//								}
//							}
//							'CUX' {
//								'C504_01' {
//									'E6347_01' ''
//									'E6345_02' ''
//									'E6343_03' ''
//									'E6348_04' ''
//								}
//								'C504_02' {
//									'E6347_01' ''
//									'E6345_02' ''
//									'E6343_03' ''
//									'E6348_04' ''
//								}
//								'E5402_03' ''
//								'E6341_04' ''
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
//							'MOA' {
//								'C516_01' {
//									'E5025_01' ''
//									'E5004_02' ''
//									'E6345_03' ''
//									'E6343_04' ''
//									'E4405_05' ''
//								}
//							}
//						}
//						'Group17_TSR' {
//							'TSR' {
//								'C536_01' {
//									'E4065_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//								'C233_02' {
//									'E7273_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E7273_04' ''
//									'E1131_05' ''
//									'E3055_06' ''
//								}
//								'C537_03' {
//									'E4219_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//								'C703_04' {
//									'E7085_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//								}
//							}
//							'RFF' {
//								'C506_01' {
//									'E1153_01' ''
//									'E1154_02' ''
//									'E1156_03' ''
//									'E4000_04' ''
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
//							'TPL' {
//								'C222_01' {
//									'E8213_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E8212_04' ''
//									'E8453_05' ''
//								}
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
//							}
//						}
					}
				}
			}

			current_Body?.Cargo?.eachWithIndex {current_Cargo , cargoIndex->

				'Group18_GID' {
					'GID' {
						'E1496_01' cargoIndex+1
//						'C213_02' {
//							'E7224_01' ''
//							'E7065_02' ''
//							'E1131_03' ''
//							'E3055_04' ''
//							'E7064_05' ''
//						}
						//
						//TIBCO and db are C213_1
						'C213_03' {
							//$Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../GID_cnt)]/ns1:Packaging/ns1:PackageQty
							'E7224_01' current_Cargo?.Packaging?.PackageQty
							//$Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../GID_cnt)]/ns1:Packaging/ns1:PackageType
							'E7065_02' current_Cargo?.Packaging?.PackageType
//							'E1131_03' ''
//							'E3055_04' ''
//							'E7064_05' ''
						}
//						'C213_04' {
//							'E7224_01' ''
//							'E7065_02' ''
//							'E1131_03' ''
//							'E3055_04' ''
//							'E7064_05' ''
//						}
					}
//					'HAN' {
//						'C524_01' {
//							'E4079_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E4078_04' ''
//						}
//						'C218_02' {
//							'E7419_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E7418_04' ''
//						}
//					}
					def BlDGFlashPt
					def BlDGFlashPtUnit
					def BlRFTemp
					def BlRFTempUnit

					//count($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:DGCargoSpec-BLCargoType)!=0 and
					//tib:trim($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/ns1:CargoNature)="RD" and
					//$Map-Data/root/BlDGFlashPt!=""
					//count($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:DGCargoSpec-BLCargoType)!=0 and
					//tib:trim($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:DGCargoSpec-BLCargoType[1]/ns1:FlashPoint/ns1:TemperatureUnit)='C'
					if(current_Cargo?.DGCargoSpec?.size()>0 && current_Cargo?.DGCargoSpec[0]?.FlashPoint?.TemperatureUnit=='C'){
						//tib:trim(translate(($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number($Start/root/CGO_cnt)]/pfx4:DGCargoSpec-BLCargoType[1]/ns1:FlashPoint/ns1:Temperature), '+', ''))
						BlDGFlashPt=current_Cargo?.DGCargoSpec[0]?.FlashPoint?.Temperature?.replace('+','')
						BlDGFlashPtUnit='CEL'
					}
					//count($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:DGCargoSpec-BLCargoType)!=0 and
					//tib:trim($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:DGCargoSpec-BLCargoType[1]/ns1:FlashPoint/ns1:TemperatureUnit)='F'
					else if(current_Cargo?.DGCargoSpec?.size()>0 && current_Cargo?.DGCargoSpec[0].FlashPoint?.TemperatureUnit=='F'){
						BlDGFlashPt=current_Cargo?.DGCargoSpec[0]?.FlashPoint?.Temperature?.replace('+','')
						BlDGFlashPtUnit='FAH'
					}
					//count($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:ReeferCargoSpec-BLCargoType)!=0 and
					//tib:trim($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number($Start/root/CGO_cnt)]/pfx4:ReeferCargoSpec-BLCargoType[1]/ns1:Temperature/ns1:TemperatureUnit)='C'
					else if(current_Cargo?.ReeferCargoSpec?.size()>0 && current_Cargo?.ReeferCargoSpec[0]?.Temperature?.TemperatureUnit=='C'){
						//$Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:ReeferCargoSpec-BLCargoType[1]/ns1:Temperature/ns1:Temperature
						BlRFTemp=current_Cargo?.ReeferCargoSpec[0]?.Temperature?.Temperature
						BlRFTempUnit='CEL'
					}
					else if(current_Cargo?.ReeferCargoSpec?.size()>0 && current_Cargo?.ReeferCargoSpec[0]?.Temperature?.TemperatureUnit=='F'){
						//$Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:ReeferCargoSpec-BLCargoType[1]/ns1:Temperature/ns1:Temperature
						BlRFTemp=current_Cargo?.ReeferCargoSpec[0]?.Temperature?.Temperature
						BlRFTempUnit='FAH'
					}

					'TMP' {
						if(BlDGFlashPt || BlRFTemp){
							'E6245_01' '1'
							//--
							'C239_02' {
								//count($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:DGCargoSpec-BLCargoType)!=0 and
								//tib:trim($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/ns1:CargoNature)="RD" and
								//$Map-Data/root/BlDGFlashPt!=""
								if(current_Cargo?.DGCargoSpec?.size()>0 && current_Cargo?.CargoNature=='RD' && BlDGFlashPt){
									'E6246_01' BlDGFlashPt
									'E6411_02' BlDGFlashPtUnit
								}else if(current_Cargo?.DGCargoSpec?.size()>0 && current_Cargo?.CargoNature=='RD' && BlDGFlashPt==""){
									'E6246_01' BlRFTemp
									'E6411_02' BlRFTempUnit
								}
								//count($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:DGCargoSpec-BLCargoType)=0 and
								//count($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/pfx4:ReeferCargoSpec-BLCargoType)!=0
								else if(current_Cargo?.DGCargoSpec?.size()==0 && current_Cargo?.ReeferCargoSpec?.size()>0){
									'E6246_01' BlDGFlashPt
									'E6411_02' BlDGFlashPtUnit
								}
							}
						}
					}
//					'RNG' {
//						'E6167_01' ''
//						'C280_02' {
//							'E6411_01' ''
//							'E6162_02' ''
//							'E6152_03' ''
//						}
//					}
//					'TMD' {
//						'C219_01' {
//							'E8335_01' ''
//							'E8334_02' ''
//						}
//						'E8332_02' ''
//						'E8341_03' ''
//					}
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
//					'MOA' {
//						'C516_01' {
//							'E5025_01' ''
//							'E5004_02' ''
//							'E6345_03' ''
//							'E6343_04' ''
//							'E4405_05' ''
//						}
//					}
//					'PIA' {
//						'E4347_01' ''
//						'C212_02' {
//							'E7140_01' ''
//							'E7143_02' ''
//							'E1131_03' ''
//							'E3055_04' ''
//						}
//						'C212_03' {
//							'E7140_01' ''
//							'E7143_02' ''
//							'E1131_03' ''
//							'E3055_04' ''
//						}
//						'C212_04' {
//							'E7140_01' ''
//							'E7143_02' ''
//							'E1131_03' ''
//							'E3055_04' ''
//						}
//						'C212_05' {
//							'E7140_01' ''
//							'E7143_02' ''
//							'E1131_03' ''
//							'E3055_04' ''
//						}
//						'C212_06' {
//							'E7140_01' ''
//							'E7143_02' ''
//							'E1131_03' ''
//							'E3055_04' ''
//						}
//					}
					'FTX' {
						//--
						'E4451_01' 'AAA'
						'E4453_02' ''
						'C107_03' {
							'E4441_01' ''
							//--is ""
							'E1131_02' ''
							'E3055_03' ''
						}
						//--
						'C108_04' {
							//substring(tib:trim($Start/root/pfx4:Body-BillOfLading/pfx4:Cargo-Body-BillOfLading[number(../../CGO_cnt)]/ns1:CargoDescription), 1,70)
							'E4440_01' util.substring(current_Cargo?.CargoDescription,1,70)
//							'E4440_02' ''
//							'E4440_03' ''
//							'E4440_04' ''
//							'E4440_05' ''
						}
//						'E3453_05' ''
					}
//					'Group19_NAD' {
//						'NAD' {
//							'E3035_01' ''
//							'C082_02' {
//								'E3039_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//							}
//							'C058_03' {
//								'E3124_01' ''
//								'E3124_02' ''
//								'E3124_03' ''
//								'E3124_04' ''
//								'E3124_05' ''
//							}
//							'C080_04' {
//								'E3036_01' ''
//								'E3036_02' ''
//								'E3036_03' ''
//								'E3036_04' ''
//								'E3036_05' ''
//								'E3045_06' ''
//							}
//							'C059_05' {
//								'E3042_01' ''
//								'E3042_02' ''
//								'E3042_03' ''
//								'E3042_04' ''
//							}
//							'E3164_06' ''
//							'E3229_07' ''
//							'E3251_08' ''
//							'E3207_09' ''
//						}
//						'DTM' {
//							'C507_01' {
//								'E2005_01' ''
//								'E2380_02' ''
//								'E2379_03' ''
//							}
//						}
//					}
//					'GDS' {
//						'C703_01' {
//							'E7085_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//						}
//					}
					'Group20_MEA' {
						'MEA' {
							//$Start/root/pfx3:Cargo-Body-BillOfLading/ns:GrossWeight!='' and
							//($Start/root/pfx3:Cargo-Body-BillOfLading/ns:GrossWeight/ns:WeightUnit='KGS' or
							//$Start/root/pfx3:Cargo-Body-BillOfLading/ns:GrossWeight/ns:WeightUnit='LBS' or
							//$Start/root/pfx3:Cargo-Body-BillOfLading/ns:GrossWeight/ns:WeightUnit='TON')
							if (current_Cargo?.GrossWeight &&(current_Cargo?.GrossWeight?.WeightUnit=='KGS' ||current_Cargo?.GrossWeight?.WeightUnit=='LBS' ||current_Cargo?.GrossWeight?.WeightUnit=='TON')){
								'E6311_01' 'WT'
								'C502_02' {
									'E6313_01' 'G'
//								'E6321_02' ''
//								'E6155_03' ''
//								'E6154_04' ''
								}
								'C174_03' {
									'E6411_01' 'KGM'
									if(current_Cargo?.GrossWeight?.WeightUnit=='KGS' ){
										'E6314_02' current_Cargo?.GrossWeight?.Weight
									}else if(current_Cargo?.GrossWeight?.WeightUnit=='LBS'){
										'E6314_02' new BigDecimal(current_Cargo?.GrossWeight?.Weight)*0.4536
									}else{
										'E6314_02' new BigDecimal(current_Cargo?.GrossWeight?.Weight)*1000
									}
//								'E6162_03' ''
//								'E6152_04' ''
//								'E6432_05' ''
								}
							}
							//$Start/root/pfx3:Cargo-Body-BillOfLading/ns:Volume/ns:Volume!='' and
							//($Start/root/pfx3:Cargo-Body-BillOfLading/ns:Volume/ns:VolumeUnit='CBM' or
							//$Start/root/pfx3:Cargo-Body-BillOfLading/ns:Volume/ns:VolumeUnit='CBF')
							else if(current_Cargo?.Volume && (current_Cargo?.Volume?.VolumeUnit=='CBM' || current_Cargo?.Volume?.VolumeUnit=='CBF')){
								'E6311_01' 'VOL'
								'C502_02' {
									'E6313_01' 'AAW'
//								'E6321_02' ''
//								'E6155_03' ''
//								'E6154_04' '
								}
								'C174_03' {
									'E6411_01' 'MTQ'
									if(current_Cargo?.Volume?.VolumeUnit=='CBM'){
										'E6314_02' current_Cargo?.Volume?.Volume
									}else{
										'E6314_02' new BigDecimal(current_Cargo?.Volume?.Volume)*0.0283
									}
//								'E6162_03' ''
//								'E6152_04' ''
//								'E6432_05'
								}
							}

							//--
//							'C174_03' {
//								//--
//								'E6411_01' ''
//								//--
//								'E6314_02' ''
////								'E6162_03' ''
////								'E6152_04' ''
////								'E6432_05' ''
//							}
//							'E7383_04' ''
						}
//						'EQN' {
//							'C523_01' {
//								'E6350_01' ''
//								'E6353_02' ''
//							}
//						}
					}
//					'Group21_DIM' {
//						'DIM' {
//							'E6145_01' ''
//							'C211_02' {
//								'E6411_01' ''
//								'E6168_02' ''
//								'E6140_03' ''
//								'E6008_04' ''
//							}
//						}
//						'EQN' {
//							'C523_01' {
//								'E6350_01' ''
//								'E6353_02' ''
//							}
//						}
//					}
//					'Group22_RFF' {
//						'RFF' {
//							'C506_01' {
//								'E1153_01' ''
//								'E1154_02' ''
//								'E1156_03' ''
//								'E4000_04' ''
//							}
//						}
//						'DTM' {
//							'C507_01' {
//								'E2005_01' ''
//								'E2380_02' ''
//								'E2379_03' ''
//							}
//						}
//					}
					current_Cargo?.MarksAndNumbers?.each {current_MarksAndNumbers->
						'Group23_PCI' {
							'PCI' {
								'E4233_01' '24'
								'C210_02' {
									//-- 01-08
									'E7102_01' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,1,35)
									'E7102_02' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,36,35)
									'E7102_03' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,71,35)
									'E7102_04' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,106,35)
									'E7102_05' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,141,35)
									'E7102_06' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,176,35)
									'E7102_07' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,211,35)
									'E7102_08' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,246,35)
									'E7102_09' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,281,35)
									'E7102_10' util.substring(current_MarksAndNumbers?.MarksAndNumbersLine,316,35)
								}
//							'E8275_03' ''
//							'C827_04' {
//								'E7511_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//							}
							}
//						'RFF' {
//							'C506_01' {
//								'E1153_01' ''
//								'E1154_02' ''
//								'E1156_03' ''
//								'E4000_04' ''
//							}
//						}
//						'DTM' {
//							'C507_01' {
//								'E2005_01' ''
//								'E2380_02' ''
//								'E2379_03' ''
//							}
//						}
//						'GIN' {
//							'E7405_01' ''
//							'C208_02' {
//								'E7402_01' ''
//								'E7402_02' ''
//							}
//							'C208_03' {
//								'E7402_01' ''
//								'E7402_02' ''
//							}
//							'C208_04' {
//								'E7402_01' ''
//								'E7402_02' ''
//							}
//							'C208_05' {
//								'E7402_01' ''
//								'E7402_02' ''
//							}
//							'C208_06' {
//								'E7402_01' ''
//								'E7402_02' ''
//							}
//						}
						}
					}


//					'Group24_DOC' {
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
//							}
//							'E3153_03' ''
//							'E1220_04' ''
//							'E1218_05' ''
//						}
//						'DTM' {
//							'C507_01' {
//								'E2005_01' ''
//								'E2380_02' ''
//								'E2379_03' ''
//							}
//						}
//					}
//					'Group25_TPL' {
//						'TPL' {
//							'C222_01' {
//								'E8213_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E8212_04' ''
//								'E8453_05' ''
//							}
//						}
//						'Group26_MEA' {
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
//							'EQN' {
//								'C523_01' {
//									'E6350_01' ''
//									'E6353_02' ''
//								}
//							}
//						}
//					}
//					'Group27_SGP' {
//						'SGP' {
//							'C237_01' {
//								'E8260_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E3207_04' ''
//							}
//							'E7224_02' ''
//						}
//						'SEQ' {
//							'E1245_01' ''
//							'C286_02' {
//								'E1050_01' ''
//								'E1159_02' ''
//								'E1131_03' ''
//								'E3055_04' ''
//							}
//						}
////						'Group28_MEA' {
////							'MEA' {
////								'E6311_01' ''
////								'C502_02' {
////									'E6313_01' ''
////									'E6321_02' ''
////									'E6155_03' ''
////									'E6154_04' ''
////								}
////								'C174_03' {
////									'E6411_01' ''
////									'E6314_02' ''
////									'E6162_03' ''
////									'E6152_04' ''
////									'E6432_05' ''
////								}
////								'E7383_04' ''
////							}
////							'EQN' {
////								'C523_01' {
////									'E6350_01' ''
////									'E6353_02' ''
////								}
////							}
////						}
//					}
//					'Group29_TCC' {
//						'TCC' {
//							'C200_01' {
//								'E8023_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E8022_04' ''
//								'E4237_05' ''
//								'E7140_06' ''
//							}
//							'C203_02' {
//								'E5243_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//								'E5242_04' ''
//								'E5275_05' ''
//								'E1131_06' ''
//								'E3055_07' ''
//								'E5275_08' ''
//								'E1131_09' ''
//								'E3055_10' ''
//							}
//							'C528_03' {
//								'E7357_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//							}
//							'C554_04' {
//								'E5243_01' ''
//								'E1131_02' ''
//								'E3055_03' ''
//							}
//						}
//						'CUX' {
//							'C504_01' {
//								'E6347_01' ''
//								'E6345_02' ''
//								'E6343_03' ''
//								'E6348_04' ''
//							}
//							'C504_02' {
//								'E6347_01' ''
//								'E6345_02' ''
//								'E6343_03' ''
//								'E6348_04' ''
//							}
//							'E5402_03' ''
//							'E6341_04' ''
//						}
//						'PRI' {
//							'C509_01' {
//								'E5125_01' ''
//								'E5118_02' ''
//								'E5375_03' ''
//								'E5387_04' ''
//								'E5284_05' ''
//								'E6411_06' ''
//							}
//							'E5213_02' ''
//						}
//						'EQN' {
//							'C523_01' {
//								'E6350_01' ''
//								'E6353_02' ''
//							}
//						}
//						'PCD' {
//							'C501_01' {
//								'E5245_01' ''
//								'E5482_02' ''
//								'E5249_03' ''
//								'E1131_04' ''
//								'E3055_05' ''
//							}
//						}
//						'MOA' {
//							'C516_01' {
//								'E5025_01' ''
//								'E5004_02' ''
//								'E6345_03' ''
//								'E6343_04' ''
//								'E4405_05' ''
//							}
//						}
//						'QTY' {
//							'C186_01' {
//								'E6063_01' ''
//								'E6060_02' ''
//								'E6411_03' ''
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
//					}
					current_Cargo?.DGCargoSpec?.each {current_DGCargoSpec ->
						'Group30_DGS' {
							'DGS' {
								'E8273_01' 'IMD'
								'C205_02' {
									//substring(tib:trim($Start/root/pfx4:DGCargoSpec-BLCargoType/ns1:IMOClass), 1,7)
									'E8351_01' util.substring(current_DGCargoSpec?.IMOClass,1,7)
									//substring(tib:trim($Start/root/pfx4:DGCargoSpec-BLCargoType/ns1:IMDGPage), 1,7)
									'E8078_02' util.substring(current_DGCargoSpec?.IMDGPage,1,7)
//								'E8092_03' ''
								}
								//--
								'C234_03' {
									//substring(tib:trim($Start/root/pfx4:DGCargoSpec-BLCargoType/ns1:UNNumber), 1,7)
									'E7124_01' util.substring(current_DGCargoSpec?.UNNumber,1,7)
//								'E7088_02' ''
								}
								//--
								'C223_04' {
									//-- dont guan ta
									'E7106_01' ''
									//--
									'E6411_02' ''
								}
//							'E8339_05' ''
//							'E8364_06' ''
//							'E8410_07' ''
//							'E8126_08' ''
//							'C235_09' {
//								'E8158_01' ''
//								'E8186_02' ''
//							}
//							'C236_10' {
//								'E8246_01' ''
//								'E8246_02' ''
//								'E8246_03' ''
//							}
//							'E8255_11' ''
//							'E8325_12' ''
//							'E8211_13' ''
							}
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
//						}
//						'Group31_CTA' {
//							'CTA' {
//								'E3139_01' ''
//								'C056_02' {
//									'E3413_01' ''
//									'E3412_02' ''
//								}
//							}
//							'COM' {
//								'C076_01' {
//									'E3148_01' ''
//									'E3155_02' ''
//								}
//							}
//						}
//						'Group32_MEA' {
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
//							'EQN' {
//								'C523_01' {
//									'E6350_01' ''
//									'E6353_02' ''
//								}
//							}
//						}
//						'Group33_SGP' {
//							'SGP' {
//								'C237_01' {
//									'E8260_01' ''
//									'E1131_02' ''
//									'E3055_03' ''
//									'E3207_04' ''
//								}
//								'E7224_02' ''
//							}
//							'Group34_MEA' {
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
//						}
						}
					}
				}
			}

			current_Body?.Container?.each {current_Container ->
				'Group35_EQD' {
					'EQD' {
						//--
						'E8053_01' 'CN'
						//--
						'C237_02' {
							//concat(tib:trim($Start/root/pfx4:Body-BillOfLading/pfx4:Container-Body-BillOfLading[number(../../CNTR_Cnt)]/ns1:ContainerNumber),
							// tib:trim($Start/root/pfx4:Body-BillOfLading/pfx4:Container-Body-BillOfLading[number(../../CNTR_Cnt)]/ns1:ContainerCheckDigit))
							'E8260_01' "${current_Container?.ContainerNumber?:''}${current_Container?.ContainerCheckDigit?:''}"
//						'E1131_02' ''
//						'E3055_03' ''
//						'E3207_04' ''
						}
						'C224_03' {
							//tib:trim($Start/root/pfx4:Body-BillOfLading/pfx4:Container-Body-BillOfLading[number(../../CNTR_Cnt)]/pfx4:CS1ContainerSizeType-BLContainerType)
							'E8155_01' current_Container?.CS1ContainerSizeType
//						'E1131_02' ''
//						'E3055_03' ''
//						'E8154_04' ''
						}
						//$Start/root/pfx4:Body-BillOfLading/pfx4:Container-Body-BillOfLading[number(../../CNTR_Cnt)]/ns1:IsSOC='true'
						if(current_Container?.IsSOC=='true')
							'E8077_04' '1'
//					'E8249_05' ''
//					'E8169_06' ''
					}
//				'EQN' {
//					'C523_01' {
//						'E6350_01' ''
//						'E6353_02' ''
//					}
//				}
//				'TMD' {
//					'C219_01' {
//						'E8335_01' ''
//						'E8334_02' ''
//					}
//					'E8332_02' ''
//					'E8341_03' ''
//				}
					'MEA' {
						//tib:trim($Start/root/pfx3:Body-BillOfLading/pfx3:Container-Body-BillOfLading[number(../../CNTR_cnt)]/ns:GrossWeight/ns:Weight)!="" and
						//tib:trim($Start/root/pfx3:Body-BillOfLading/pfx3:Container-Body-BillOfLading[number(../../CNTR_cnt)]/ns:GrossWeight/ns:WeightUnit)!=""
						if(current_Container?.GrossWeight?.Weight && current_Container?.GrossWeight?.WeightUnit){
							'E6311_01' 'WT'
							//--
							'C502_02' {
								'E6313_01' 'G'
//						'E6321_02' ''
//						'E6155_03' ''
//						'E6154_04' ''
							}
							//--
							'C174_03' {
								//--
								'E6411_01' 'KGM'
								//tib:trim($Start/root/pfx3:Body-BillOfLading/pfx3:Container-Body-BillOfLading[number(../../CNTR_cnt)]/ns:GrossWeight/ns:WeightUnit)='KGS'
								if(current_Container?.GrossWeight?.WeightUnit=='KGS' ){
									'E6314_02' current_Container?.GrossWeight?.Weight
								}else if(current_Container?.GrossWeight?.WeightUnit=='LBS'){
									'E6314_02' new BigDecimal(current_Container?.GrossWeight?.Weight)*0.4536
								}else if(current_Container?.GrossWeight?.WeightUnit=='TON'){
									'E6314_02' new BigDecimal(current_Container?.GrossWeight?.Weight)*1000
								}
//						'E6162_03' ''
//						'E6152_04' ''
//						'E6432_05' ''
							}
						}
//						'E6311_01' 'WT'
//						//--
//						'C502_02' {
//							'E6313_01' 'G'
////						'E6321_02' ''
////						'E6155_03' ''
////						'E6154_04' ''
//						}
//						//--
//						'C174_03' {
//							//--
//							'E6411_01' ''
//							//--
//							'E6314_02' ''
////						'E6162_03' ''
////						'E6152_04' ''
////						'E6432_05' ''
//						}
//					'E7383_04' ''
					}
//				'DIM' {
//					'E6145_01' ''
//					'C211_02' {
//						'E6411_01' ''
//						'E6168_02' ''
//						'E6140_03' ''
//						'E6008_04' ''
//					}
//				}
					current_Container?.Seal?.each {current_Seal ->
						'SEL' {
							//substring($Start/root/sealNumber, 1,12)
							'E9308_01' util.substring(current_Seal?.SealNumber,1,12)
							'C215_02' {
								//tib:trim($Start/root/sealType)= 'CA' or
								//tib:trim($Start/root/sealType)= 'CU' or
								//tib:trim($Start/root/sealType)= 'SH'
								if(current_Seal?.SealType=='CA'||current_Seal?.SealType=='CU'||current_Seal?.SealType=='SH')
									'E9303_01' current_Seal?.SealType
//						'E1131_02' ''
//						'E3055_03' ''
//						'E9302_04' ''
							}
//					'E4517_03' ''
						}
					}
//				'TPL' {
//					'C222_01' {
//						'E8213_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//						'E8212_04' ''
//						'E8453_05' ''
//					}
//				}
//				'HAN' {
//					'C524_01' {
//						'E4079_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//						'E4078_04' ''
//					}
//					'C218_02' {
//						'E7419_01' ''
//						'E1131_02' ''
//						'E3055_03' ''
//						'E7418_04' ''
//					}
//				}
//				'TMP' {
//					'E6245_01' ''
//					'C239_02' {
//						'E6246_01' ''
//						'E6411_02' ''
//					}
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
//				}
//				'Group36_TCC' {
//					'TCC' {
//						'C200_01' {
//							'E8023_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E8022_04' ''
//							'E4237_05' ''
//							'E7140_06' ''
//						}
//						'C203_02' {
//							'E5243_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E5242_04' ''
//							'E5275_05' ''
//							'E1131_06' ''
//							'E3055_07' ''
//							'E5275_08' ''
//							'E1131_09' ''
//							'E3055_10' ''
//						}
//						'C528_03' {
//							'E7357_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//						}
//						'C554_04' {
//							'E5243_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//						}
//					}
//					'PRI' {
//						'C509_01' {
//							'E5125_01' ''
//							'E5118_02' ''
//							'E5375_03' ''
//							'E5387_04' ''
//							'E5284_05' ''
//							'E6411_06' ''
//						}
//						'E5213_02' ''
//					}
//					'EQN' {
//						'C523_01' {
//							'E6350_01' ''
//							'E6353_02' ''
//						}
//					}
//					'PCD' {
//						'C501_01' {
//							'E5245_01' ''
//							'E5482_02' ''
//							'E5249_03' ''
//							'E1131_04' ''
//							'E3055_05' ''
//						}
//					}
//					'MOA' {
//						'C516_01' {
//							'E5025_01' ''
//							'E5004_02' ''
//							'E6345_03' ''
//							'E6343_04' ''
//							'E4405_05' ''
//						}
//					}
//					'QTY' {
//						'C186_01' {
//							'E6063_01' ''
//							'E6060_02' ''
//							'E6411_03' ''
//						}
//					}
//				}
//				'Group37_NAD' {
//					'NAD' {
//						'E3035_01' ''
//						'C082_02' {
//							'E3039_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//						}
//						'C058_03' {
//							'E3124_01' ''
//							'E3124_02' ''
//							'E3124_03' ''
//							'E3124_04' ''
//							'E3124_05' ''
//						}
//						'C080_04' {
//							'E3036_01' ''
//							'E3036_02' ''
//							'E3036_03' ''
//							'E3036_04' ''
//							'E3036_05' ''
//							'E3045_06' ''
//						}
//						'C059_05' {
//							'E3042_01' ''
//							'E3042_02' ''
//							'E3042_03' ''
//							'E3042_04' ''
//						}
//						'E3164_06' ''
//						'E3229_07' ''
//						'E3251_08' ''
//						'E3207_09' ''
//					}
//					'DTM' {
//						'C507_01' {
//							'E2005_01' ''
//							'E2380_02' ''
//							'E2379_03' ''
//						}
//					}
//				}
//				'Group38_EQA' {
//					'EQA' {
//						'E8053_01' ''
//						'C237_02' {
//							'E8260_01' ''
//							'E1131_02' ''
//							'E3055_03' ''
//							'E3207_04' ''
//						}
//					}
//					'EQN' {
//						'C523_01' {
//							'E6350_01' ''
//							'E6353_02' ''
//						}
//					}
//
				}
			}
			'UNT' {
				'E0074_01' '-999'
				'E0062_02' '-999'
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
				generateBody(current_Body, bl.Header,  associateContainerAndCargo, filteredFreightCharge, filteredFreightChargeCNTR, outXml)
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

	//	blUtil.checkLocationcodeForPOLPOD(node, true, null, errorKeyList)

	}

	public String getCntry_nme(String cntry_cde, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}

		if (cntry_cde==null) {
			return ""
		}

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select cs.cntry_nme from cs2_continent_country cs where cs.cntry_cde=?";

		try {
			pre = conn.prepareStatement(sql);
			// only need get the 1st one
			pre.setMaxRows(1);
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, cntry_cde);
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

	public String getPort_list(String port_name, String un_location_code, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}

		if (port_name==null || un_location_code==null) {
			return ""
		}

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select count(*) from cs2_port_list pl where pl.port_name=? and pl.un_location_code=?";

		try {
			pre = conn.prepareStatement(sql);
			// only need get the 1st one
			pre.setMaxRows(1);
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, port_name);
			pre.setString(2, un_location_code);
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


