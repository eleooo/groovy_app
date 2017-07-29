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
 * @author Neptune
 * FRIESLANDGTN CT initialize on 20170528
 */
public class CUS_CS2CTXML_315_FRIESLANDGTN {

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


    public void generateBody(Body current_Body, MarkupBuilder outXml, Map<String,String> pospVerifyFlags) {

        //CT special fields
        def vCS1Event = current_Body.Event.CS1Event
        def vCS1EventFirst5 = util.substring(vCS1Event, 1, 5)
        def vCS1EventCodeConversion = util.getConversion('FRIESLANDGTN', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)
        def shipDir = util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)
        def ContainerSizeType = ctUtil.ConvertContainerSizeType('ContainerType', 'GTNEXUS', current_Body?.GeneralInformation?.SCAC, 'X.12', current_Body?.Container?.CarrCntrSizeType, 'O',conn)
        def CSContainerSizeType = ctUtil.ConvertContainerSizeType('ContainerType_CSType', 'GTNEXUS', current_Body?.GeneralInformation?.SCAC, 'X.12', current_Body?.Container?.CSContainerSizeType, 'O',conn)

        def SCAC = current_Body?.GeneralInformation?.SCAC

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //for CustomizationRule(GtnexusCustomization), when CS1Event in following events, B406 will be replaced by R403

        def GtnexusCustomizationFirstPolEvent = ['CS040', 'CS060', 'CS070']
        def GtnexusCustomizationLastPodEvent = ['CS120', 'CS130', 'CS160']
        def replacedOrigingR403L
        def replacedOrigingR403D
        def replacedB406
        def replacedB411ByB406EqualsVNCLI


        if (GtnexusCustomizationFirstPolEvent.contains(vCS1Event)) {
            if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode?.toUpperCase() == "VNCLI" || current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode?.toUpperCase() == "VNCLI") {
                replacedOrigingR403L = "VNSGN"
                replacedB406 = replacedOrigingR403L
            } else if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) {
                replacedOrigingR403L = current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode?.trim()
                replacedB406 = replacedOrigingR403L
            } else if (current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode) {
                replacedOrigingR403L = current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode?.trim()
                replacedB406 = replacedOrigingR403L
            }else{
                replacedOrigingR403L = "9999"
                replacedB406 = replacedOrigingR403L
            }

        } else if (GtnexusCustomizationLastPodEvent.contains(vCS1Event)) {
            if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode?.toUpperCase() == "VNCLI" || current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode?.toUpperCase() == "VNCLI") {
                replacedOrigingR403D = "VNSGN"
                replacedB406 = replacedOrigingR403D
            } else if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) {
                replacedOrigingR403D = current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode?.trim()
                replacedB406 = replacedOrigingR403D
            } else if (current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode) {
                replacedOrigingR403D = current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode?.trim()
                replacedB406 = replacedOrigingR403D
            }else{
                replacedOrigingR403D = "9999"
                replacedB406 = replacedOrigingR403D
            }
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //pospVerifyFlags
//        def R4AtLeastOneDTMCheckingFlags=true
        def N90203BothMissingFlag=true
        def B409MissingFlag=true
        def N94FMissingFlag=true

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        outXml.'Loop_ST' {
            'ST' {
                'E143_01' '315'
                'E329_02' '    '
            }
            'B4' {
                'E157_03' vCS1EventCodeConversion

                'E373_04' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                //
                'E161_05' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, HHmm)

                def B406 = "9999"

                if (util.isNotEmpty(current_Body?.Event?.Location?.LocationCode?.SchedKDCode)) B406 = current_Body?.Event?.Location?.LocationCode?.SchedKDCode
                if (util.isNotEmpty(current_Body?.Event?.Location?.LocationCode?.UNLocationCode)) B406 = current_Body?.Event?.Location?.LocationCode?.UNLocationCode

                ////Customization replace for GtnexusCustomization
                if(B406.toUpperCase()=="VNCLI"){
                    replacedB411ByB406EqualsVNCLI="Ho Chi Minh"
                    B406="VNSGN"
                }

                if(replacedB406) B406=replacedB406

                    'E159_06' B406


                if (current_Body.Container?.ContainerNumber) {
                    'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
                }

                if (current_Body.Container?.ContainerNumber) {
                    'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, 10)
                }

                def B409 = util.substring(current_Body?.Container?.ContainerStatus,1,1)
                if (util.isEmpty(B409)) {
                    if (current_Body?.Event?.CS1Event != 'CS210') {
                        B409 = 'L'
                    } else B409 = 'E'
                }
                if (current_Body?.Event?.CS1Event == 'CS010') {
                    B409 = 'E'
                } else if (current_Body?.Event?.CS1Event == 'CS180') {
                    B409 = 'L'
                }

                if (util.isNotEmpty(B409)) {
                    B409MissingFlag=false
                    'E578_09' B409
                }

                if (util.isNotEmpty(ContainerSizeType)) {
                    'E24_10' ContainerSizeType
                } else if (util.isNotEmpty(CSContainerSizeType)) {
                    'E24_10' CSContainerSizeType
                }

                def B411
                def B412
                if (util.isNotEmpty(current_Body?.Event?.Location?.LocationName)) {
                    B411 = util.substring(current_Body?.Event?.Location?.LocationName, 1, 30)
                    B412 = 'CI'
                } else if (util.isNotEmpty(current_Body?.Event?.Location?.CityDetails?.City)) {
                    B411 = util.substring(current_Body?.Event?.Location?.CityDetails?.City, 1, 30)
                    B412 = 'CI'
                } else if (util.isNotEmpty(current_Body?.Event?.Location?.LocationCode?.UNLocationCode)) {
                    B411 = current_Body?.Event?.Location?.LocationCode?.UNLocationCode
                    B412 = 'UN'
                }

                if(replacedB411ByB406EqualsVNCLI) B411="Ho Chi Minh"
                if (B411) {
                    'E310_11' util.substring(B411.trim(), 1, 30)
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
            def N9SegmentsLimitation=30
            current_Body?.Route?.OceanLeg[0]?.POL?.DepartureDT?.find { it.attr_Indicator == 'A' }?.each {
                def N902=getFacilityCodeQuery(current_Body?.Route?.LastPOD?.Facility?.FacilityCode, 'FacilityCode', 'O', 'CT', 'GTNEXUS', 'X.12')?.trim()
                if(N9SegmentsLimitation>0 && N902){
                    'N9' {
                        'E128_01' 'TT'
                        'E127_02' N902
                    }
                    N9SegmentsLimitation--
                }

            }

            def legalPartyType = ['SHP', 'FWD', 'CGN', 'NPT', 'ANP', 'CCP']
            def individualN94F = []
            current_Body?.Party?.each { current_Party ->
                if (legalPartyType.contains(current_Party?.PartyType?.trim()) && !individualN94F.contains(util.substring(current_Party?.CarrierCustomerCode?.trim(), 1, 30)) && N9SegmentsLimitation>0 && current_Party?.CarrierCustomerCode?.trim()) {
                    N90203BothMissingFlag=false
                    N94FMissingFlag=false
                    'N9' {
                        'E128_01' '4F'
                        'E127_02' util.substring(current_Party?.CarrierCustomerCode?.trim(), 1, 30)
                        individualN94F.add(util.substring(current_Party?.CarrierCustomerCode?.trim(), 1, 30))
                    }
                    N9SegmentsLimitation--
                }
            }

            def individualN94EQ = []
            current_Body?.Container?.each { current_Container ->
                def N902 = ctUtil.emptyToString(current_Container?.ContainerNumber) + ctUtil.emptyToString(current_Container?.ContainerCheckDigit)
                if (util.isNotEmpty(N902) && !individualN94EQ.contains(util.substring(N902.trim(), 1, 30)) && N9SegmentsLimitation>0) {
                    N90203BothMissingFlag=false
                    'N9' {
                        'E128_01' 'EQ'
                        'E127_02' util.substring(N902.trim(), 1, 30)
                        individualN94EQ.add(util.substring(N902.trim(), 1, 30))
                    }
                    N9SegmentsLimitation--
                }
            }

            def individualN94BN = []
            current_Body?.BookingGeneralInfo?.each { current_BookingGeneralInfo ->
                def N902 = current_BookingGeneralInfo?.CarrierBookingNumber
                if (util.isNotEmpty(N902) && current_Body?.GeneralInformation?.SCAC == 'MISC') {
                    N902 = 'MISC' + N902
                }
                if (util.isNotEmpty(N902) && !individualN94BN.contains(util.substring(N902.trim(), 1, 30)) && N9SegmentsLimitation>0) {
                    N90203BothMissingFlag=false
                    'N9' {
                        'E128_01' 'BN'
                        'E127_02' util.substring(N902.trim(), 1, 30)
                        individualN94BN.add(util.substring(N902.trim(), 1, 30))
                    }
                    N9SegmentsLimitation--
                }

            }

            def individualN94BM = []
            current_Body?.BLGeneralInfo?.each { current_BLGeneralInfo ->
                def N902 = current_BLGeneralInfo?.BLNumber
                if (util.isNotEmpty(N902) && current_Body?.GeneralInformation?.SCAC == 'MISC') {
                    N902 = 'MISC' + N902
                }
                if (util.isNotEmpty(N902) && !individualN94BM.contains(util.substring(N902.trim(), 1, 30)) && N9SegmentsLimitation>0) {
                    N90203BothMissingFlag=false
                    'N9' {
                        'E128_01' 'BM'
                        'E127_02' util.substring(N902.trim(), 1, 30)
                        individualN94BM.add(util.substring(N902.trim(), 1, 30))
                    }
                    N9SegmentsLimitation--
                }
            }

            def individualN94SN = []
            current_Body?.Container?.Seal?.each { current_Seal ->
                if (util.isNotEmpty(current_Seal?.SealNumber) && !individualN94SN.contains(util.substring(current_Seal?.SealNumber?.trim(), 1, 30)) && N9SegmentsLimitation>0) {
                    N90203BothMissingFlag=false
                    'N9' {
                        'E128_01' 'SN'
                        'E127_02' util.substring(current_Seal?.SealNumber?.trim(), 1, 30)
                        individualN94SN.add(util.substring(current_Seal?.SealNumber?.trim(), 1, 30))
                    }
                    N9SegmentsLimitation--
                }
            }

            if (util.isNotEmpty(SCAC)&& N9SegmentsLimitation>0) {
                N90203BothMissingFlag=false
                'N9' {
                    'E128_01' 'SCA'
                    'E127_02' util.substring(SCAC?.trim(), 1, 30)
                }
                N9SegmentsLimitation--
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
                        'E597_01' util?.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)
                    }
                    if (lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode) {
                        'E26_02' lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode
                    }
                    if (lastOceanLeg?.SVVD?.Discharge?.Voyage) {
                        'E55_09' util.substring(lastOceanLeg?.SVVD?.Discharge?.Voyage?.trim() + (lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction?.trim() : ""),1,10)
                    } else if (lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage) {
                        'E55_09' util.substring(lastOceanLeg?.SVVD?.Discharge?.ExternalVoyage?.trim(),1,10)
                    }
                    'E897_12' 'L'
                    if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
                        'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName?.trim(), 1, 28)
                    }
                }
            } else if (shipDir == 'O') {
                'Q2' {
                    if (firstOceanLeg?.SVVD?.Loading?.LloydsNumber) {
                        'E597_01' util?.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)
                    }
                    if (firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode) {
                        'E26_02' firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode
                    }

                    if (firstOceanLeg?.SVVD?.Loading?.Voyage) {
                        'E55_09' util.substring(firstOceanLeg?.SVVD?.Loading?.Voyage + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction : ""),1,10)
                    } else if (firstOceanLeg?.SVVD?.Loading?.ExternalVoyage) {
                        'E55_09' util.substring(firstOceanLeg?.SVVD?.Loading?.ExternalVoyage?.trim(),1,10)
                    }

                    'E897_12' 'L'
                    if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
                        'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName?.trim(), 1, 28)
                    }
                }
            }

//===============================================Q2 OVER========================================================

//===============================================R4 START=======================================================
            //special logic for R403/04---------rule name: GtnexusCustomization
            def specialR403 = "VNSGN"
            def specialR404 = "Ho Chi Minh"


            POR por = current_Body?.Route?.POR
            'Loop_R4' {
                def R404flag = false
                'R4' {
                    'E115_01' 'R'
                    if (por?.CityDetails?.LocationCode?.UNLocationCode) {
                        'E309_02' "UN"
                        if (por?.CityDetails?.LocationCode?.UNLocationCode?.trim()?.toUpperCase() == "VNCLI") {
                            'E310_03' specialR403
                            R404flag = true
                        } else {
                            'E310_03' por?.CityDetails?.LocationCode?.UNLocationCode?.trim()
                        }

                    } else if (por?.CityDetails?.LocationCode?.SchedKDCode && por?.CityDetails?.LocationCode?.SchedKDType) {
                        'E309_02' por?.CityDetails?.LocationCode?.SchedKDType?.trim()
                        if (por?.CityDetails?.LocationCode?.SchedKDCode?.trim()?.toUpperCase() == "VNCLI") {
                            'E310_03' specialR403
                            R404flag = true
                        } else {
                            'E310_03' por?.CityDetails?.LocationCode?.SchedKDCode?.trim()
                        }

                    } else {
                        'E309_02' "ZZ"
                        'E310_03' "XXXX"
                    }

                    if (R404flag) {
                        'E114_04' specialR404
                    } else if (por?.CityDetails?.City) {
                        'E114_04' util.substring(por?.CityDetails?.City?.trim(),1,24)
                    }
                    if (por?.CSStandardCity?.CSCountryCode) {
                        'E26_05' por?.CSStandardCity?.CSCountryCode?.trim()
                    }
                    if (por?.CSStandardCity?.CSStateCode) {
                        'E156_08' util.substring(por?.CSStandardCity?.CSStateCode?.trim(),1,2)
                    }
                }
                if (current_Body?.Route?.CargoReceiptDT?.find { it?.attr_Indicator == "A" }?.LocDT) {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '140'
                        'E373_02' util.convertDateTime(current_Body?.Route?.CargoReceiptDT?.find { it?.attr_Indicator == "A" }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(current_Body?.Route?.CargoReceiptDT?.find { it?.attr_Indicator == "A" }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                } else if (current_Body?.Route?.FullPickupDT?.find { it?.attr_Indicator == "E" }?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound == "C") {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '139'
                        'E373_02' util.convertDateTime(current_Body?.Route?.FullPickupDT?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(current_Body?.Route?.FullPickupDT?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                } else if (current_Body?.Route?.FullReturnCutoffDT?.find {
                    it?.attr_Indicator == "A"
                }?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound == "M") {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '139'
                        'E373_02' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(current_Body?.Route?.FullReturnCutoffDT?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                }
                R404flag = false
            }


            FirstPOL firstPOL = current_Body?.Route?.FirstPOL
            'Loop_R4' {
                def R404flag = false
                'R4' {
                    'E115_01' 'L'
                    if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
                        'E309_02' "UN"
                        if (firstPOL?.Port?.LocationCode?.UNLocationCode?.trim()?.toUpperCase() == "VNCLI") {
                            'E310_03' specialR403
                            R404flag = true
                        } else {
                            'E310_03' firstPOL?.Port?.LocationCode?.UNLocationCode?.trim()
                        }

                    } else if (firstPOL?.Port?.LocationCode?.SchedKDCode && firstPOL?.Port?.LocationCode?.SchedKDType) {
                        'E309_02' firstPOL?.Port?.LocationCode?.SchedKDType?.trim()
                        if (firstPOL?.Port?.LocationCode?.SchedKDCode?.trim()?.toUpperCase() == "VNCLI") {
                            'E310_03' specialR403
                            R404flag = true
                        } else {
                            'E310_03' firstPOL?.Port?.LocationCode?.SchedKDCode?.trim()
                        }

                    } else {
                        'E309_02' "ZZ"

                        // tibco 1.0 app Customization Logic--------ruleName: GtnexusDelmonteCustomization
                        def legalEvent = ['CS040', 'CS060', 'CS070', 'CS110', 'CS030', 'CS047']
                        if (legalEvent.contains(current_Body?.Event?.CS1Event?.trim())) {
                            'E310_03' "9999"
                        } else {
                            'E310_03' "XXXX"
                        }

                    }
                    if (R404flag) {
                        'E114_04' specialR404
                    } else if (firstPOL?.Port?.PortName) {
                        'E114_04' util.substring(firstPOL?.Port?.PortName?.trim(), 1, 24)
                    }
                    if (firstPOL?.Port?.CSCountryCode) {
                        'E26_05' firstPOL?.Port?.CSCountryCode?.trim()
                    }

                    if (firstPOL?.CSStateCode) {
                        'E156_08' util.substring(firstPOL?.CSStateCode?.trim(), 1, 2)
                    }
                }

                if (firstOceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == "A" }?.LocDT) {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '140'
                        'E373_02' util.convertDateTime(firstOceanLeg?.POL?.DepartureDT?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(firstOceanLeg?.POL?.DepartureDT?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                } else if (firstOceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == "E" }?.LocDT) {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '139'
                        'E373_02' util.convertDateTime(firstOceanLeg?.POL?.DepartureDT?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(firstOceanLeg?.POL?.DepartureDT?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                }
                R404flag = false
            }

            LastPOD lastPod = current_Body?.Route?.LastPOD

////////////Customization logic (GtnexusCustomization) for CS180 remove R4D when R4D03=R4E03
            if (current_Body?.Event?.CS1Event == "CS180" && (current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode == current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode || (!current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode && current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode ==current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) || (!current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode && current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode==current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode) || (!current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode && !current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode && current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode == current_Body?.Route?.FND?.CityDetails?.LocationCode?.SchedKDCode))) {
                ///////no R4D output
            } else {
                'Loop_R4' {
                    def R404flag = false
                    'R4' {
                        'E115_01' 'D'
                        if (lastPod?.Port?.LocationCode?.UNLocationCode) {
                            'E309_02' "UN"
                            if (lastPod?.Port?.LocationCode?.UNLocationCode?.trim()?.toUpperCase() == "VNCLI") {
                                'E310_03' specialR403
                                R404flag = true
                            } else {
                                'E310_03' lastPod?.Port?.LocationCode?.UNLocationCode?.trim()
                            }

                        } else if (lastPod?.Port?.LocationCode?.SchedKDCode && lastPod?.Port?.LocationCode?.SchedKDType) {
                            'E309_02' lastPod?.Port?.LocationCode?.SchedKDType?.trim()

                            if (lastPod?.Port?.LocationCode?.SchedKDCode?.trim()?.toUpperCase() == "VNCLI") {
                                'E310_03' specialR403
                                R404flag = true
                            } else {
                                'E310_03' lastPod?.Port?.LocationCode?.SchedKDCode?.trim()
                            }

                        } else {
                            'E309_02' "ZZ"

                            // tibco 1.0 app Customization Logic--------ruleName: GtnexusDelmonteCustomization

                            def legalEvent = ['CS120', 'CS130', 'CS160', 'CS190', 'CS080']
                            if (legalEvent.contains(current_Body?.Event?.CS1Event?.trim()) || (['CS140', 'CS220', 'CS260'].contains(current_Body?.Event?.CS1Event?.trim()) && ((lastPod?.Port?.PortName && lastPod?.Port?.PortName?.trim() == current_Body?.Event?.Location?.LocationName?.trim()) || (lastPod?.Port?.City && lastPod?.Port?.City?.trim() == current_Body?.Event?.Location?.CityDetails?.City?.trim())))) {
                                'E310_03' "9999"
                            } else {
                                'E310_03' "XXXX"
                            }
                        }

                        if (lastPod?.Port?.PortName) {

                            if (R404flag) {
                                'E114_04' specialR404
                            } else {
                                'E114_04' util.substring(lastPod?.Port?.PortName?.trim(), 1, 24)
                            }

                        }
                        if (lastPod?.Port?.CSCountryCode) {
                            'E26_05' lastPod?.Port?.CSCountryCode?.trim()
                        }
                        if (lastPod?.CSStateCode) {
                            'E156_08' util.substring(lastPod?.CSStateCode?.trim(), 1, 2)
                        }
                    }
                    if (lastOceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == "A" }?.LocDT) {
//                        R4AtLeastOneDTMCheckingFlags=false
                        'DTM' {
                            'E374_01' '140'
                            'E373_02' util.convertDateTime(lastOceanLeg?.POD?.ArrivalDT?.find {
                                it?.attr_Indicator == "A"
                            }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                            'E337_03' util.convertDateTime(lastOceanLeg?.POD?.ArrivalDT?.find {
                                it?.attr_Indicator == "A"
                            }?.LocDT, xmlDateTimeFormat, HHmm)
                        }
                    } else if (lastOceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == "E" }?.LocDT) {
//                        R4AtLeastOneDTMCheckingFlags=false
                        'DTM' {
                            'E374_01' '139'
                            'E373_02' util.convertDateTime(lastOceanLeg?.POD?.ArrivalDT?.find {
                                it?.attr_Indicator == "E"
                            }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                            'E337_03' util.convertDateTime(lastOceanLeg?.POD?.ArrivalDT?.find {
                                it?.attr_Indicator == "E"
                            }?.LocDT, xmlDateTimeFormat, HHmm)
                        }
                    }
                    R404flag = false
                }
            }




            FND fnd = current_Body?.Route?.FND
            'Loop_R4' {
                def R404flag = false
                'R4' {
                    'E115_01' 'E'
                    if (fnd?.CityDetails?.LocationCode?.UNLocationCode) {
                        'E309_02' "UN"
                        if (fnd?.CityDetails?.LocationCode?.UNLocationCode?.trim()?.toUpperCase() == "VNCLI") {
                            'E310_03' specialR403
                            R404flag = true
                        } else {
                            'E310_03' fnd?.CityDetails?.LocationCode?.UNLocationCode?.trim()
                        }

                    } else if (fnd?.CityDetails?.LocationCode?.SchedKDCode && fnd?.CityDetails?.LocationCode?.SchedKDType) {
                        'E309_02' fnd?.CityDetails?.LocationCode?.SchedKDType?.trim()

                        if (fnd?.CityDetails?.LocationCode?.SchedKDCode?.trim()?.toUpperCase() == "VNCLI") {
                            'E310_03' specialR403
                            R404flag = true
                        } else {
                            'E310_03' fnd?.CityDetails?.LocationCode?.SchedKDCode?.trim()
                        }

                    } else {
                        'E309_02' "ZZ"

                        // tibco 1.0 app Customization Logic--------ruleName: GtnexusDelmonteCustomization

                        def legalEvent = ['CS200', 'CS140', 'CS220', 'CS260']
                        if (legalEvent.contains(current_Body?.Event?.CS1Event?.trim()) && fnd?.CityDetails?.City && fnd?.CityDetails?.City?.trim() == current_Body?.Event?.Location?.CityDetails?.City?.trim()) {
                            'E310_03' "9999"
                        } else {
                            'E310_03' "XXXX"
                        }
                    }

                    if (R404flag) {
                        'E114_04' specialR404
                    } else if (fnd?.CityDetails?.City) {
                        'E114_04' util.substring(fnd?.CityDetails?.City?.trim(), 1, 24)
                    } else if (current_Body?.Route?.LastPOD?.Port?.PortName) {
                        'E114_04' util.substring(current_Body?.Route?.LastPOD?.Port?.PortName?.trim(), 1, 24)
                    }
                    if (fnd?.CSStandardCity?.CSCountryCode) {
                        'E26_05' fnd?.CSStandardCity?.CSCountryCode?.trim()
                    }
                    if (fnd?.CSStandardCity?.CSStateCode) {
                        'E156_08' util.substring(fnd?.CSStandardCity?.CSStateCode?.trim(),1,2)
                    }
                }
                if (fnd?.ArrivalDT?.find { it?.attr_Indicator == "A" }?.LocDT) {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '140'
                        'E373_02' util.convertDateTime(fnd?.ArrivalDT?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(fnd?.ArrivalDT?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                } else if (current_Body?.Route?.CargoDeliveryDT?.find { it?.attr_Indicator == "A" }?.LocDT) {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '140'
                        'E373_02' util.convertDateTime(current_Body?.Route?.CargoDeliveryDT?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(current_Body?.Route?.CargoDeliveryDT?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                } else if (current_Body?.Route?.ArrivalAtFinalHub?.find { it?.attr_Indicator == "A" }?.LocDT) {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '140'
                        'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find {
                            it?.attr_Indicator == "A"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                } else if (fnd?.ArrivalDT?.find { it?.attr_Indicator == "E" }?.LocDT) {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '139'
                        'E373_02' util.convertDateTime(fnd?.ArrivalDT?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(fnd?.ArrivalDT?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                } else if (current_Body?.Route?.CargoDeliveryDT?.find { it?.attr_Indicator == "E" }?.LocDT) {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '139'
                        'E373_02' util.convertDateTime(current_Body?.Route?.CargoDeliveryDT?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(current_Body?.Route?.CargoDeliveryDT?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                } else if (current_Body?.Route?.ArrivalAtFinalHub?.find { it?.attr_Indicator == "E" }?.LocDT) {
//                    R4AtLeastOneDTMCheckingFlags=false
                    'DTM' {
                        'E374_01' '139'
                        'E373_02' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                        'E337_03' util.convertDateTime(current_Body?.Route?.ArrivalAtFinalHub?.find {
                            it?.attr_Indicator == "E"
                        }?.LocDT, xmlDateTimeFormat, HHmm)
                    }
                }

                R404flag = false
            }

//===============================================R4 OVER========================================================

            'SE' {
                //SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
                'E96_01' ' '
                'E329_02' '    '
            }
        }

//        if(R4AtLeastOneDTMCheckingFlags){
//            pospVerifyFlags.put("ErrorR4AtLeastOneDTMChecking","- Both DTM02 DTM03 & DTM05 are missing.")
//        }
        if(N90203BothMissingFlag){
            pospVerifyFlags.put("ErrorN90203BothMissing","- Both N902 N903 are missing.")
        }
        if(B409MissingFlag){
            pospVerifyFlags.put("ErrorB409Missing","- B4_09 is missing.")
        }
        if(N94FMissingFlag){
            pospVerifyFlags.put("ErrorN94FMissingFlag","- N9*4F is missing.")
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
//		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
//
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

        //start body looping
        ct.Body.eachWithIndex { current_Body, current_BodyIndex ->

            def eventCS2Cde = current_Body.Event.CS1Event
            def eventExtCde = util.getConversion('FRIESLANDGTN', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

            //prep checking
            List<Map<String, String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)
            Map<String,String> pospVerifyFlags= new HashMap<String,String>()
            if (errorKeyList.size() == 0) {
                //pass validateBeforeExecution
                //main mapping
                generateBody(current_Body, outXml,pospVerifyFlags)
                outputBodyCount++
            }

            //posp checking
            List<Map<String, String>> pospErrorKeyList = pospValidation(eventCS2Cde,errorKeyList,pospVerifyFlags)


            ctUtil.buildCsupload(csuploadXml, pospErrorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
            ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, pospErrorKeyList, headerMsgDT, eventExtCde, TP_ID, conn)

            txnErrorKeys.add(pospErrorKeyList);
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
            result = writer?.toString()
        }

        writer.close();
        csuploadWriter.close()
        bizKeyWriter.close()

        return result;
    }

    public List<Map<String, String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
        List<Map<String, String>> errorKeyList = new ArrayList<Map<String, String>>();
        def ContainerSizeType = ctUtil.ConvertContainerSizeType('ContainerType', 'GTNEXUS', current_Body?.GeneralInformation?.SCAC, 'X.12', current_Body?.Container?.CarrCntrSizeType, 'O',conn)
        def CSContainerSizeType = ctUtil.ConvertContainerSizeType('ContainerType_CSType', 'GTNEXUS', current_Body?.GeneralInformation?.SCAC, 'X.12', current_Body?.Container?.CSContainerSizeType, 'O',conn)

        //preException Case
        if(!(ContainerSizeType || CSContainerSizeType) || (CSContainerSizeType && current_Body?.Container?.CarrCntrSizeType && !ContainerSizeType)){
            ctUtil.FailToConvertContainerSizeType(eventCS2Cde,current_Body?.Container,true,null,errorKeyList)
        }
        ctUtil.filterInboundintermodalindicatorByEvent("CS260",current_Body,"1",false,null,errorKeyList)
        ctUtil.filterPartySentToCustomerByEvent(eventCS2Cde,['CS090'],current_Body?.Party,['COST PLUS','WORLD MARKET'],false,null,errorKeyList)
        ctUtil.filterContainerNumberMaxLength(eventCS2Cde,current_Body?.Container,10,true,null,errorKeyList)
        ctUtil.missingBookingNumber(eventCS2Cde,current_Body?.BookingGeneralInfo,true,null,errorKeyList)
        ctUtil.MissingCarrierCustomerCodeByPartyLevel(eventCS2Cde,current_Body?.Party,"BK",['SHP','FWD','CGN'],true,null,errorKeyList)
        ctUtil.missingContainerNumber(eventCS2Cde,current_Body?.Container,true,null,errorKeyList)
        ctUtil.missingEventStatusDate(eventCS2Cde,current_Body?.Event?.EventDT?.LocDT,true,null,errorKeyList)
        ctUtil.missingEventLocationCode(eventCS2Cde,current_Body?.Event,true,"- Missing Event Location",errorKeyList)
        ctUtil.missingEventStatusCode(eventCS2Cde,eventExtCde,true,null,errorKeyList)
        ctUtil.filterContainerNumberMinLengthForB40708Pair(eventCS2Cde,current_Body?.Container,5,true,null,errorKeyList)

        return errorKeyList;
    }

    public List<Map<String, String>> pospValidation(def eventCS2Cde,List<Map<String, String>> errorKeyList, Map<String,String> pospVerifyFlags) {
        Map<String, String> errorKey = null
        for(String errorMessage : pospVerifyFlags.keySet()){
            if(errorMessage.startsWith("Error")){
                errorKey = [TYPE: "ES", IS_ERROR: "YES", VALUE: eventCS2Cde+pospVerifyFlags.get(errorMessage)]
                errorKeyList.add(errorKey)
            }else{
                errorKey = [TYPE: "EC", IS_ERROR: "NO", VALUE: eventCS2Cde+pospVerifyFlags.get(errorMessage)]
                errorKeyList.add(errorKey)
            }
        }
        return errorKeyList;
    }

    public String postProcess(String mappingString, String orignStr, String finalStr, String operation) {
        if (operation.equals("ReplaceAll")) return mappingString.replaceAll(orignStr, finalStr);
        else if (operation.equals("ReplaceFirst")) return mappingString.replaceFirst(orignStr, finalStr);
        else if (operation.equals("") || operation == "" || operation == null) return mappingString;

    }

    public String ConvertContainerSizeType(String convertTypeID, String tp_id, String scac_cde, String msg_fmt_id, String int_cde, String dir_id) {
        if (conn == null)
            return "";

        String ret = "";
        PreparedStatement pre = null;
        ResultSet result = null;
        String sql = "SELECT ext_cde FROM B2B_CDE_CONVERSION  WHERE convert_type_id = ?  AND tp_id = ? AND scac_cde = ? AND msg_fmt_id = ? AND Int_Cde = ? AND dir_id = ? ";

        try {
            pre = conn.prepareStatement(sql);
            pre.setMaxRows(1);
            pre.setQueryTimeout(10);
            pre.setString(1, convertTypeID);
            pre.setString(2, tp_id);
            pre.setString(3, scac_cde);
            pre.setString(4, msg_fmt_id);
            pre.setString(5, int_cde);
            pre.setString(6, dir_id);

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

    public String getFacilityCodeQuery(String int_cde, String convertType, String dirID, String msg_type_id, String interchangeID, String msg_fmt_id) {
        if (conn == null)
            return "";

        String ret = "";
        PreparedStatement pre = null;
        ResultSet result = null;
        String sql = "select ext_cde from b2b_cde_conversion where b2b_cde_conversion.int_cde = ? and b2b_cde_conversion.convert_type_id = ? and b2b_cde_conversion.dir_id = ? and b2b_cde_conversion.msg_type_id = ? and b2b_cde_conversion.tp_id = ? and b2b_cde_conversion.msg_fmt_id = ?";

        try {
            pre = conn.prepareStatement(sql);
            pre.setMaxRows(1);
            pre.setQueryTimeout(10);
            pre.setString(1, int_cde);
            pre.setString(2, convertType);
            pre.setString(3, dirID);
            pre.setString(4, msg_type_id);
            pre.setString(5, interchangeID);
            pre.setString(6, msg_fmt_id);

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






