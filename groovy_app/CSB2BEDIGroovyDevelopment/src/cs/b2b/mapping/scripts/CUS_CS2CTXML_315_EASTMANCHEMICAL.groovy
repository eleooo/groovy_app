package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * @author Tracy
 * EASTMANCHEMICAL CT initialize on 20170611
 */
public class CUS_CS2CTXML_315_EASTMANCHEMICAL {

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

    def currentSystemDt = null


    public void generateBody(Body current_Body, MarkupBuilder outXml) {

        //CT special fields
        def vCS1Event = current_Body?.Event?.CS1Event?.trim()
        def vCS1EventFirst5 = util.substring(vCS1Event, 1, 5)
        def vCS1EventCodeConversion = util.getConversion('EASTMANCHEMICAL', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)

        def ContainerSizeType = util.getConversion('EASTMANCHEMICAL','CT','O','ContainerType',current_Body?.Container?.CarrCntrSizeType,conn)

        def SCAC = current_Body?.GeneralInformation?.SCAC
        //Customization
        def queryNonMovementDT = ctUtil.getConversionByIntCdeandScacCde('EventNonMovementDT',vCS1Event,SCAC,conn)

        def shipDir = ""

        if(util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)){
            shipDir = util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)
        }else{
            shipDir = util.getConversion('EASTMANCHEMICAL','CT', 'O', 'EventDirection', vCS1Event, conn)
        }




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        outXml.'Loop_ST' {
            'ST' {
                'E143_01' '315'
                'E329_02' '-999'
            }
            'B4' {
                'E157_03' vCS1EventCodeConversion

                if(queryNonMovementDT.get('EXT_CDE')=='GMT'){
                    //Customization -  NonMovementEventDtInGmt

                    'E373_04' util.convertDateTime(current_Body?.Event?.EventDT?.GMT, xmlDateTimeFormat, yyyyMMdd)

                    'E161_05' util.convertDateTime(current_Body?.Event?.EventDT?.GMT, xmlDateTimeFormat, HHmm)
                }else{

                    'E373_04' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

                    'E161_05' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)
                }



                if (current_Body.Container?.ContainerNumber) {
                    'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
                }

                if (current_Body.Container?.ContainerNumber) {
                    'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, 10)
                }


                if (util.isEmpty(current_Body?.Container?.ContainerStatus)&& current_Body?.Event?.CS1Event=='CS210') {

                    'E578_09'  'E'
                }else if (util.isEmpty(current_Body?.Container?.ContainerStatus)&& current_Body?.Event?.CS1Event !='CS210'){
                    'E578_09'  'L'
                }else if(util.isNotEmpty(current_Body?.Container?.ContainerStatus)){
                    'E578_09' util.substring(current_Body?.Container?.ContainerStatus,1,1)
                }else {
                    'E578_09' 'L'
                }

                if (util.isNotEmpty(ContainerSizeType)) {
                    'E24_10' ContainerSizeType
                } else {
                    'E24_10' current_Body?.Container?.CarrCntrSizeType?.trim()
                }

                def B411
                def B412
                if(util.isNotEmpty(current_Body?.Event?.Location?.LocationCode?.UNLocationCode)){
                    B411 = current_Body?.Event?.Location?.LocationCode?.UNLocationCode
                    B412 ='UN'
                }else if(util.isNotEmpty(current_Body?.Event?.Location?.CityDetails?.City)){
                    B411 = util.substring(current_Body?.Event?.Location?.CityDetails?.City, 1, 30)
                    B412 = 'CI'
                }else if(util.isNotEmpty(current_Body?.Event?.Location?.LocationName)){
                    B411 = util.substring(current_Body?.Event?.Location?.LocationName, 1, 30)
                    B412 = 'CI'
                }else if(util.isNotEmpty(current_Body?.Event?.Location?.LocationCode?.SchedKDCode)){
                    B411 = util.substring(current_Body?.Event?.Location?.LocationCode?.SchedKDCode, 1, 30)
                    if(util.isNotEmpty(current_Body?.Event?.Location?.LocationCode?.SchedKDType)){
                        B412 = current_Body?.Event?.Location?.LocationCode?.SchedKDType
                    }
                }

                if (B411) {
                    'E310_11' B411
                }

                if (B412) {
                    'E309_12' B412
                }

                if (util.isNotEmpty(current_Body?.Container?.ContainerCheckDigit))
                    'E761_13' current_Body?.Container?.ContainerCheckDigit
            }
            //Loop N9
            //for avoid duplicated N9
            //for N9 SegmentsLimitation

            //BookingNumber
//            current_Body?.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}?.groupBy{it?.CarrierBookingNumber?.trim()}?.each { BookingNumber, current_BookingGeneralInfoGroup ->
			current_Body?.BookingGeneralInfo?.each {current_BookingGeneralInfo ->
				//1.0 allow duplicated BKnumber
				if(util.isNotEmpty(current_BookingGeneralInfo?.CarrierBookingNumber?.trim())){
                'N9' {
                    'E128_01' 'BN'
                    'E127_02' util.substring(current_BookingGeneralInfo.CarrierBookingNumber,1,30)
                }
			  }	
            }

            //BLNumber
//            current_Body?.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it.BLNumber)}?.groupBy{it?.BLNumber?.trim()}?.each { BLNumber, current_BLGeneralInfo ->
			//1.0 allow duplicated BLnumber
			current_Body?.BLGeneralInfo?.each {current_BLGeneralInfo ->
				if(util.isNotEmpty(current_BLGeneralInfo?.BLNumber?.trim())){
                'N9' {
                    'E128_01' 'BM'
                    'E127_02' util.substring(current_BLGeneralInfo.BLNumber,1,30)
                }
              }
			}

            //SCAC
            if (util.isNotEmpty(SCAC)) {
                'N9' {
                    'E128_01' 'SCA'
                    'E127_02' util.substring(SCAC?.trim(), 1, 30)
                }

            }

            //Party 4F and 4E
            def  N9PartyTypeMap = ['CGN': '4E','SHP':'4F']

            current_Body?.Party?.each {current_Party ->   
				//LoopByPartyNoLevelRtnM: when Party has no ParyLevel then do mapping
				if(current_Party?.PartyLevel?.trim()=='' || util.isEmpty(current_Party?.PartyLevel)){
                if(N9PartyTypeMap.containsKey(current_Party?.PartyType)&& util.isNotEmpty(current_Party?.PartyName?.trim())){  
                    'N9' {
                        'E128_01' N9PartyTypeMap.get(current_Party?.PartyType)
                        'E127_02' util.substring(current_Party?.PartyName?.trim(),1,30).trim()
                    }
                }
				}
            }
            //ExternalReference

            def externalReferenceTypeMap = ['CIN':'IN','CR':'CR','CTR':'CT','FM':'FM','FR':'FN','PO':'PO','PR':'Q1',
                                            'RP':'RP','SC':'E8','SID':'SI','SO':'SO','SR':'SR','TARIF':'TS']
            // filter duplicate


            current_Body?.ExternalReference?.each {current_ExtReference ->
                if(externalReferenceTypeMap.containsKey(current_ExtReference?.CSReferenceType)){
                    'N9' {
                        'E128_01' externalReferenceTypeMap.get(current_ExtReference?.CSReferenceType)
                        'E127_02' util.substring(current_ExtReference?.ReferenceNumber,1,50)  //1.0 did substring first 50 chars
                    }

                }
            }




//===============================================N9 OVER=======================================================

//===============================================Q2 START=======================================================
            OceanLeg firstOceanLeg = null
            OceanLeg lastOceanLeg = null
            if (current_Body.Route?.OceanLeg) {
                firstOceanLeg = current_Body.Route.OceanLeg[0]
                lastOceanLeg = current_Body.Route.OceanLeg[-1]
            }
            if (shipDir == 'I') {
                'Q2' {
                    if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
                        'E597_01' util?.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 8) //1.0: if DB has no config max length, then get default length 8
                    }

                    if (lastOceanLeg?.SVVD?.Discharge?.Voyage) {
                        'E55_09' util.substring(lastOceanLeg?.SVVD?.Discharge?.Voyage?.trim() + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction?.trim() : ""),1,10)
                    } else if (lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage) {
                        'E55_09' util.substring(lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage?.trim(),1,10)
                    }
					
					if (lastOceanLeg?.SVVD?.Discharge?.LloydsNumber) {
					'E897_12' 'L'
					}
                    if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
                        'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName?.trim(), 1, 28)
                    }
                }
            } else if (shipDir == 'O') {
                'Q2' {
                    if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
                        'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 8) //1.0: if DB has no config max length, then get default length 8
                    }

                    if (firstOceanLeg?.SVVD?.Loading?.Voyage) {
                        'E55_09' util.substring(firstOceanLeg?.SVVD?.Loading?.Voyage + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction : ""),1,10)
                    } else if (firstOceanLeg?.SVVD?.Loading?.ExternalVoyage) {
                        'E55_09' util.substring(firstOceanLeg?.SVVD?.Loading?.ExternalVoyage?.trim(),1,10)
                    }
					
					if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
					'E897_12' 'L'
					}
                    if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
                        'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName?.trim(), 1, 28)
                    }
                }
            }

//===============================================Q2 OVER========================================================

//===============================================R4 START=======================================================
//1.0 actual sequence:  1.POR; 2.POL; 3.POD; 4.FND which is not same with DB configuration, it's updated by Tina Zhao in previous SCR			
            //POR
            POR por = current_Body?.Route?.POR
            def R402R =null
            def R403R =null
            def R404R =null

            if (por?.CityDetails?.LocationCode?.UNLocationCode) {
                R402R =  'UN'
                R403R =  por?.CityDetails?.LocationCode?.UNLocationCode?.trim()
//            }else if(util.isNotEmpty(por?.CityDetails?.LocationCode?.SchedKDCode)){ //1.0 only check the SKDCode!='', then do mapping
			  }else if(util.isNotEmpty(por?.CityDetails?.LocationCode?.SchedKDCode) && util.isNotEmpty(por?.CityDetails?.LocationCode?.SchedKDType)){
                R402R =  por?.CityDetails?.LocationCode?.SchedKDType?.trim()
                R403R =  por?.CityDetails?.LocationCode?.SchedKDCode?.trim()
            }

            if (por?.CityDetails?.City) {
//                R404R =  util.substring(por?.CityDetails?.City?.trim(),1,24)?.toUpperCase()   //Tracy
				R404R =  util.substring(por?.CityDetails?.City?.trim(),1,24)
            }
            if(util.isNotEmpty(R403R) || util.isNotEmpty(R404R)) {
                'Loop_R4' {
                    'R4' {
                        'E115_01' 'R'
                        if (R402R) {
                            'E309_02' R402R
                        }

                        if (R403R) {
                            'E310_03' R403R
                        }

                        if (R404R) {
                            'E114_04' R404R
                        }

                        if (util.isNotEmpty(por?.CSStandardCity?.CSCountryCode) && por?.CSStandardCity?.CSCountryCode?.length() <= 2) {
                            'E26_05' por?.CSStandardCity?.CSCountryCode?.trim()
                        }
                        if (por?.CSStandardCity?.CSStateCode) {
                            'E156_08' util.substring(por?.CSStandardCity?.CSStateCode, 1, 2).padRight(2)
                        }
                    }

                    LocDT porDTM = null
                    def isAct = false
                    if (current_Body?.Route?.CargoReceiptDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
                        porDTM = current_Body?.Route?.CargoReceiptDT?.find { it?.attr_Indicator == 'A' }?.LocDT
                        isAct = true
                    } else if (current_Body?.Route?.FullPickupDT?.find {
                        it?.attr_Indicator == 'E'
                    }?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C') {
                        porDTM = current_Body?.Route?.FullPickupDT?.find { it?.attr_Indicator == 'E' }?.LocDT
                    } else if (current_Body?.Route?.FullReturnCutoffDT?.find {
                        it?.attr_Indicator == 'A'
                    }?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M') {
                        porDTM = current_Body?.Route?.FullReturnCutoffDT?.find { it?.attr_Indicator == 'A' }?.LocDT
                    }

                    if (porDTM) {
                        'DTM' {
                            if (isAct) {
                                'E374_01' '140'
                            } else {
                                'E374_01' '139'
                            }
                            'E373_02' util.convertDateTime(porDTM, xmlDateTimeFormat, yyyyMMdd)
                            'E337_03' util.convertDateTime(porDTM, xmlDateTimeFormat, HHmm)

                        }
                    }

                }
            }
			
			//POL
			//FirstPOL firstPOL=current_Body?.Route?.FirstPOL
//			=======================POL and POD LoopByOceanleg  start==============================
			current_Body?.Route?.OceanLeg?.each {current_oceanLeg ->
				def R402L =null
				def R403L =null
				def R404L =null
	//			if(current_Body?){}
				if (current_oceanLeg?.POL?.Port?.LocationCode?.UNLocationCode) {
					R402L =  'UN'
					R403L = current_oceanLeg?.POL?.Port?.LocationCode?.UNLocationCode?.trim()
//				}else if(util.isNotEmpty(current_oceanLeg?.POL?.Port?.LocationCode?.SchedKDCode)){ //1.0 only check SKDcode!='', then do mapping
				}else if(util.isNotEmpty(current_oceanLeg?.POL?.Port?.LocationCode?.SchedKDCode) &&util.isNotEmpty(current_oceanLeg?.POL?.Port?.LocationCode?.SchedKDType)){
					R402L = current_oceanLeg?.POL?.Port?.LocationCode?.SchedKDType?.trim()
					R403L = current_oceanLeg?.POL?.Port?.LocationCode?.SchedKDCode?.trim()
				}
	
				if (current_oceanLeg?.POL?.Port?.PortName) {
					R404L = util.substring(current_oceanLeg?.POL?.Port?.PortName?.trim(),1,24)
				}else if(firstOceanLeg?.POL?.Port?.City){
					R404L = util.substring(current_oceanLeg?.POL?.Port?.City?.trim(),1,24)
				}
				if(util.isNotEmpty(R403L) || util.isNotEmpty(R404L)) {
					'Loop_R4' {
						'R4' {
							'E115_01' 'L'
							if (R402L) {
								'E309_02' R402L
							}
	
							if (R403L) {
								'E310_03' R403L
							}
	
							if (R404L) {
								'E114_04' R404L
							}
	
	
							if (util.isNotEmpty(current_oceanLeg?.POL?.Port?.CSCountryCode) && current_oceanLeg?.POL?.Port?.CSCountryCode?.length() <= 2) {
								'E26_05' current_oceanLeg?.POL?.Port?.CSCountryCode?.trim()
							}
	
							if (util.isNotEmpty(current_oceanLeg?.POL?.CSStateCode)) {
								'E156_08' util.substring(current_oceanLeg?.POL?.CSStateCode?.trim(), 1, 2).padRight(2)
							}
						}
						LocDT polDTM = null
						def isAct = false
						if (current_oceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
							isAct = true
							polDTM = current_oceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'A' }?.LocDT
						} else if (current_oceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'E' }?.LocDT) {
							polDTM = current_oceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'E' }?.LocDT
						}
	
						if (polDTM) {
							'DTM' {
								if (isAct) {
									'E374_01' '140'
								} else {
									'E374_01' '139'
								}
	
								'E373_02' util.convertDateTime(polDTM, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(polDTM, xmlDateTimeFormat, HHmm)
							}
						}
	
					}
				}
				
				//POD
			   //LastPOD lastPod=current_Body?.Route?.LastPOD
				def R402D =null
				def R403D =null
				def R404D =null
	
				if(current_oceanLeg?.POD?.Port?.LocationCode?.UNLocationCode){
					R402D =  'UN'
					R403D =  current_oceanLeg?.POD?.Port?.LocationCode?.UNLocationCode
//				}else if(util.isNotEmpty(current_oceanLeg?.POD?.Port?.LocationCode?.SchedKDCode)){  //1.0 only check SKDcode!='', then do mapping
				}else if(util.isNotEmpty(current_oceanLeg?.POD?.Port?.LocationCode?.SchedKDCode) && util.isNotEmpty(current_oceanLeg?.POD?.Port?.LocationCode?.SchedKDType)){
					R402D =  current_oceanLeg?.POD?.Port?.LocationCode?.SchedKDType
					R403D = current_oceanLeg?.POD?.Port?.LocationCode?.SchedKDCode

				}
	
				if(current_oceanLeg?.POD?.Port?.PortName){
					R404D =  util.substring(current_oceanLeg?.POD?.Port?.PortName,1,24)
				}else if(firstOceanLeg?.POD?.Port?.City){
					R404D =  util.substring(current_oceanLeg?.POD?.Port?.City,1,24)
				}
				if(util.isNotEmpty(R403D) || util.isNotEmpty(R404D)) {
	
					'Loop_R4' {
						'R4' {
							'E115_01' 'D'
	
							if (R402D) {
								'E309_02' R402D
							}
	
							if (R403D) {
								'E310_03' R403D
							}
	
							if (R404R) {
								'E114_04' R404D
							}
	
							if (util.isNotEmpty(current_oceanLeg?.POD?.Port?.CSCountryCode) && current_oceanLeg?.POD?.Port?.CSCountryCode?.length() <= 2) {
								'E26_05' current_oceanLeg?.POD?.Port?.CSCountryCode
							}
	
							if (util.isNotEmpty(current_oceanLeg?.POD?.CSStateCode) && current_oceanLeg?.POD?.CSStateCode?.length() <= 2) {
								'E156_08' current_oceanLeg?.POD?.CSStateCode?.trim()?.padRight(2)
							}
						}
	
						LocDT podDTM = null
						def isAct = false
						if (current_oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
							isAct = true
							podDTM = current_oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'A' }?.LocDT
						} else if (current_oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT) {
							podDTM = current_oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT
						}
	
						if (podDTM) {
							'DTM' {
								if (isAct) {
									'E374_01' '140'
								} else {
									'E374_01' '139'
								}
	
								'E373_02' util.convertDateTime(podDTM, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(podDTM, xmlDateTimeFormat, HHmm)
							}
						}
					}
				}
				
			}
//			=======================POL and POD LoopByOceanleg  over==============================

            //FND

            FND fnd=current_Body?.Route?.FND

            def R402E =null
            def R403E =null
            def R404E =null

            if (fnd?.CityDetails?.LocationCode?.UNLocationCode) {
                R402E = 'UN'
                R403E =  fnd?.CityDetails?.LocationCode?.UNLocationCode?.trim()
//            } else if (util.isNotEmpty(fnd?.CityDetails?.LocationCode?.SchedKDCode)) { //1.0 only check SKDcode!='', then do mapping
			} else if (util.isNotEmpty(fnd?.CityDetails?.LocationCode?.SchedKDCode) && util.isNotEmpty(fnd?.CityDetails?.LocationCode?.SchedKDType)) {
                R402E = fnd?.CityDetails?.LocationCode?.SchedKDType?.trim()
                R403E =  fnd?.CityDetails?.LocationCode?.SchedKDCode?.trim()
            }


            if (fnd?.CityDetails?.City) {
//                R404E =  util.substring(fnd?.CityDetails?.City?.trim(), 1, 24).toUpperCase() //Tracy
				R404E =  util.substring(fnd?.CityDetails?.City?.trim(), 1, 24)
            }
            if(util.isNotEmpty(R403E) || util.isNotEmpty(R404E)) {
                'Loop_R4' {
                    'R4' {
                        'E115_01' 'E'
                        if (R402E) {
                            'E309_02' R402E
                        }

                        if (R403E) {
                            'E310_03' R403E
                        }

                        if (R404E) {
                            'E114_04' R404E
                        }


                        if (util.isNotEmpty(fnd?.CSStandardCity?.CSCountryCode) && fnd?.CSStandardCity?.CSCountryCode?.length() <= 2) {
                            'E26_05' fnd?.CSStandardCity?.CSCountryCode?.trim()
                        }
                        if (fnd?.CSStandardCity?.CSStateCode) {
                            'E156_08' util.substring(fnd?.CSStandardCity?.CSStateCode?.trim(), 1, 2).padRight(2)
                        }
                    }

                    LocDT fndDTM = null
                    def isAct = false

                    if (current_Body?.Route?.CargoDeliveryDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
                        fndDTM = current_Body?.Route?.CargoDeliveryDT?.find { it?.attr_Indicator == 'A' }?.LocDT
                        isAct = true
                    } else if (fnd?.ArrivalDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
                        fndDTM = fnd?.ArrivalDT?.find { it?.attr_Indicator == 'A' }?.LocDT
                        isAct = true
                    } else if (current_Body?.Route?.CargoDeliveryDT?.find {
                        it?.attr_Indicator == 'E'
                    }?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.InBound == 'C') {
                        fndDTM = current_Body?.Route?.CargoDeliveryDT?.find { it?.attr_Indicator == 'E' }?.LocDT
                    } else if (fnd?.ArrivalDT?.find {
                        it?.attr_Indicator == 'E'
                    }?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.InBound == 'M') {
                        fndDTM = fnd?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT
                    }

                    if (fndDTM) {
                        'DTM' {
                            if (isAct) {
                                'E374_01' '140'
                            } else {
                                'E374_01' '139'
                            }

                            'E373_02' util.convertDateTime(fndDTM, xmlDateTimeFormat, yyyyMMdd)
                            'E337_03' util.convertDateTime(fndDTM, xmlDateTimeFormat, HHmm)
                        }

                    }
                }
            }

//===============================================R4 OVER========================================================

            'SE' {
                //SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
                'E96_01' '-999'
                'E329_02' '-999'
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
        def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
        //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
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
        def T315 = outXml.createNode('T315')
        def bizKeyRoot = bizKeyXml.createNode('root')
        def csuploadRoot = csuploadXml.createNode('root')
        //csupload root node name must be 'root', or will cause ORA error.

        //BeginWorkFlow
        TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
        def outputBodyCount = 0
        currentSystemDt = new Date()
        //def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
        def headerMsgDT = util.convertDateTime(ct.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
        //long ediIsaCtrlNumber = ctrlNos[0]
        //long ediGroupCtrlNum = ctrlNos[1]
        def txnErrorKeys = []

        //duplication -- CT special logic
    	Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
//
	//	List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)

		List<Body> bodies = ctUtil.CTEventDuplicationbyBodByConditions(ct.Body, duplicatedPairs)

        //start body looping
        bodies.eachWithIndex { current_Body, current_BodyIndex ->

            def eventCS2Cde = current_Body?.Event?.CS1Event?.trim()
            def eventExtCde = util.getConversion('EASTMANCHEMICAL', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

            //prep checking
            List<Map<String, String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)
            Map<String,String> pospVerifyFlags= new HashMap<String,String>()
            if (errorKeyList.size() == 0) {
                //pass validateBeforeExecution
                //main mapping
                generateBody(current_Body,outXml)
                outputBodyCount++
            }

            //posp checking
            //List<Map<String, String>> pospErrorKeyList = pospValidation(eventCS2Cde,errorKeyList,pospVerifyFlags)


            ctUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
            ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, eventExtCde, TP_ID, conn)

            txnErrorKeys.add(errorKeyList);
        }

        //EndWorkFlow

        //End root node
        outXml.nodeCompleted(null, T315)
        bizKeyXml.nodeCompleted(null, bizKeyRoot)
        csuploadXml.nodeCompleted(null, csuploadRoot)

        //		println bizKeyWriter.toString();
        //		println csuploadWriter.toString();

        //promote csupload and bizkey to session
        ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
        ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
        ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId, ct.Header.InterchangeMessageID);
        if (ct.Body[0]?.GeneralInformation?.SCAC) ctUtil.promoteAssoCarrierSCACToSession(appSessionId, ct.Body[0]?.GeneralInformation?.SCAC);

        String result = "";
        if (txnErrorKeys.findAll { it.size == 0 }.size != 0) {
            //if exists one txn without error, then return result
            result = util.cleanXml(writer?.toString());
        }

        writer.close();
        csuploadWriter.close()
        bizKeyWriter.close()

        return result;
    }

    public List<Map<String, String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
        List<Map<String, String>> errorKeyList = new ArrayList<Map<String, String>>();

        //MissingStatusCode, sequence=1
        ctUtil.missingEventStatusCode(eventCS2Cde,eventExtCde,true,null,errorKeyList)

        //MissingCarrierBookingNumber, sequence=1
        ctUtil.missingBookingNumber(eventCS2Cde,current_Body?.BookingGeneralInfo,true,null,errorKeyList)

        //MissingContainerNumber, sequence=1
        ctUtil.missingContainerNumber(eventCS2Cde,current_Body?.Container,true,null,errorKeyList)

        //FilterCS260WhenIBIntermodal, sequence=1
        ctUtil.filterIBIntermodal(eventCS2Cde,'CS260',current_Body?.Route?.Inbound_intermodal_indicator,false," - Filter CS260 because contains I/B intermodal.",errorKeyList)

		//MissingEventLocationNameAndCity, sequence=2
		ctUtil.missingEventLocationNameandCity(eventCS2Cde,current_Body?.Event,true,null,errorKeyList)
		
        //MissingEventCountryCode, sequence=3
        String missingEventCountryCodemsg = "- Missing event country code."+(current_Body?.Event?.Location?.CityDetails?.City ? current_Body?.Event?.Location?.CityDetails?.City :'')+" "+(current_Body?.Event?.Location?.CityDetails?.Country ? current_Body?.Event?.Location?.CityDetails?.Country :'')

        ctUtil.missingEventCountryCode(eventCS2Cde,current_Body?.Event,true,missingEventCountryCodemsg,errorKeyList)

		//MissingPorCity, sequence=5
		ctUtil.missingCityDetailsCity(eventCS2Cde,current_Body?.Route?.POR?.CityDetails?.City,true,'- Missing POR City.',errorKeyList)
		
		//MissingFndCity, , Sequence=6
		ctUtil.missingCityDetailsCity(eventCS2Cde,current_Body?.Route?.FND?.CityDetails?.City,true,'- Missing FND City.',errorKeyList)
		//MissingPorCSCountryCode, sequence=9
        ctUtil.missingPORCountryCode(eventCS2Cde,current_Body?.Route?.POR,null,true,null,errorKeyList)

        //MissingPodCSCountryCode, sequnce=10
        ctUtil.missingPODCountryCode(eventCS2Cde,current_Body?.Route?.LastPOD,true,null,errorKeyList)
        //MissingPolCSCountryCode, sequnce=11
        ctUtil.missingPOLCountryCode(eventCS2Cde,current_Body?.Route?.FirstPOL,true,null,errorKeyList)

		//MissingFndCSCountryCode, Sequence=12
		ctUtil.missingFNDCountryCode(eventCS2Cde,current_Body?.Route?.FND,true,null,errorKeyList)

        return errorKeyList;
    }

    public boolean pospValidation() {

    }




}






