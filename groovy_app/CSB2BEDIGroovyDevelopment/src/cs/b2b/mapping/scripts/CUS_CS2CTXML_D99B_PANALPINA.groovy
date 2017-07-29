package cs.b2b.mapping.scripts


import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder
import java.sql.Connection


/**
 * Created by XIAOTR on 6/26/2017.
 */
class CUS_CS2CTXML_D99B_PANALPINA {

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

    def headerAction = null
    def headerMsgDT = null



    public void generateBody(Body current_Body,int current_BodyIndex, MarkupBuilder outXml) {

        //CT special fields
        def vCS1Event = current_Body.Event.CS1Event
        def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
        def vCS1EventConverion = util.getConversion('PANALPINA','CT','O','EventCdeCSIntVal',vCS1Event,conn)

        def SCAC = current_Body?.GeneralInformation?.SCAC

        def shipDir = util.getConversionWithoutTP('CT','O','CSSTDQ2',vCS1EventFirst5,conn)
        outXml.'Group_UNH' {
            'UNH' {
                'E0062_01' '-999'
                'S009_02' {
                    'E0065_01' 'IFTSTA'
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
            }
            'BGM' {
                'C002_01' {
                    'E1001_01' '23'
                    'E1131_02' ''
                    'E3055_03' ''
                    'E1000_04' ''
                }
                'C106_02' {
                    // get control number
                    def ediCtrlNum = util.getEDIControlNumber('CARGOSMART',TP_ID, 'CT', 'EDIFACT',conn)
                    String tansationID = current_BodyIndex+1
                    if(ediCtrlNum){
                        'E1004_01' ediCtrlNum[0]+(tansationID.padLeft(5,'0'))
                    }

                    'E1056_02' ''
                    'E1060_03' ''
                }
                'E1225_03' '9'
                'E4343_04' ''
            }
            'DTM' {
                if(headerMsgDT){
                    'C507_01' {
                        'E2005_01' '137'
                        'E2380_02' util.substring(headerMsgDT,1,12)
                        'E2379_03' '203'
                    }
                }
            }
            'Group1_NAD' {
                'NAD' {
                    'E3035_01' 'CA'
                    'C082_02' {
                        'E3039_01' SCAC
                        'E1131_02' '160'
                        'E3055_03' '87'
                    }
                    'C058_03' {
                        'E3124_01' ''
                        'E3124_02' ''
                        'E3124_03' ''
                        'E3124_04' ''
                        'E3124_05' ''
                    }
                }
                'Group2_CTA' {
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


            List<Map<String,String>> allRFFList = []  // collect all RFF mapping for group3RFF and group5RFF

            /**
             * special enhance :  group3 RFF and group5RFF
             * #1 map RFF from first BookingGeneralInfo/CarrierBookingNumber
             * #2 map RFF from numberic BLGeneralInfo/BLNumber
             * #3 map RFF from first ExternalReference[CSReferenceType='FR']/ReferenceNumber   -->first RFF+FF
             * #4 map RFF from first ExternalReference[CSReferenceType='SID']/ReferenceNumber  -->first RFF+SI
             * #5 map RFF from ExternalReference[CSReferenceType='PO']/ReferenceNumbe -->RFF+ON in group3, RFF+PO in group5
             * all result will be collected into allRFFList
             * Group3 RFF will map the first nine RFF
             * Group5 RFF will map the after nine RFF
             * */
            if(current_Body?.BookingGeneralInfo[0]?.CarrierBookingNumber){
                Map<String,String> Grp3andGrp5RFF = [:]
                Grp3andGrp5RFF.put('BN',current_Body?.BookingGeneralInfo[0]?.CarrierBookingNumber)
                allRFFList.add(Grp3andGrp5RFF)
            }


            current_Body?.BLGeneralInfo?.findAll {util.isNotEmpty(it?.BLNumber)}?.each { current_blGeneralInfo ->
                if (current_blGeneralInfo.BLNumber.isNumber() ) {
                    Map<String,String> Grp3andGrp5RFF = [:]
                    Grp3andGrp5RFF.put('BM',util.substring(current_blGeneralInfo?.BLNumber, 1, 35))
                    allRFFList.add(Grp3andGrp5RFF)
                }
            }

            if(current_Body?.ExternalReference?.find{it?.CSReferenceType=='FR' && util.isNotEmpty(it?.ReferenceNumber)}?.ReferenceNumber){
                Map<String,String> Grp3andGrp5RFF = [:]
                Grp3andGrp5RFF.put('FR',util.substring(current_Body?.ExternalReference?.find{it?.CSReferenceType=='FR' && util.isNotEmpty(it?.ReferenceNumber)}?.ReferenceNumber, 1, 35))
                allRFFList.add(Grp3andGrp5RFF)
            }

            if(current_Body?.ExternalReference?.find{it?.CSReferenceType=='SID' && util.isNotEmpty(it?.ReferenceNumber)}?.ReferenceNumber){
                Map<String,String> Grp3andGrp5RFF = [:]
                Grp3andGrp5RFF.put('SID',util.substring(current_Body?.ExternalReference?.find{it?.CSReferenceType=='SID' && util.isNotEmpty(it?.ReferenceNumber)}?.ReferenceNumber, 1, 35))
                allRFFList.add(Grp3andGrp5RFF)
            }

            def extRefMap = ['PO']
            current_Body?.ExternalReference?.findAll {util.isNotEmpty(it?.ReferenceNumber) }?.each { current_extRef ->
                if(current_extRef?.CSReferenceType in extRefMap){
                    Map<String,String> Grp3andGrp5RFF = [:]
                    Grp3andGrp5RFF.put('PO',util.substring(current_extRef?.ReferenceNumber, 1, 35))
                    allRFFList.add(Grp3andGrp5RFF)
                }

            }

            def grp3ReferenceTypeMap  = ['BM':'BM','BN':'BN','FR':'FF','PO':'ON','SID':'SI']

            allRFFList.eachWithIndex{current_Grp3RFF,index->
                if(index<9){
                    'Group3_RFF' {
                        'RFF' {
                            'C506_01' {
                                'E1153_01' grp3ReferenceTypeMap.get(current_Grp3RFF.keySet()[0])
                                'E1154_02' current_Grp3RFF.get(current_Grp3RFF.keySet()[0])
                                'E1156_03' ''
                                'E4000_04' ''
                                'E1060_05' ''
                            }
                        }
                    }
                }
            }

            'Group4_CNI' {
                'CNI' {
                    'E1490_01' '1'
                    'C503_02' {
                        'E1004_01' ''
                        'E1373_02' ''
                        'E1366_03' ''
                        'E3453_04' ''
                        'E1056_05' ''
                        'E1060_06' ''
                    }
                    'E1312_03' ''
                }
                /*Group 5 mapping*/
                'Group5_STS' {
                    'STS' {
                        'C601_01' {
                            'E9015_01' '1'
                            'E1131_02'
                            'E3055_03' ''
                        }
                         'C555_02' {
                             if (vCS1EventConverion && vCS1EventConverion != 'XX') {
                                 'E4405_01'vCS1EventConverion
                                 'E1131_02' ''
                                 'E3055_03' ''
                                 'E4404_04' ''
                             }
                         }

                    }
                    def grp5ReferenceTypeMap  = ['BM':'BM','BN':'BN','FF':'FF','PO':'PO','SID':'SI']
                    allRFFList.eachWithIndex{current_Grp3RFF,index->
                        if(index>9 && index <18){

                            'RFF' {
                                'C506_01' {
                                    'E1153_01' grp5ReferenceTypeMap.get(current_Grp3RFF.keySet()[0])
                                    'E1154_02' current_Grp3RFF.get(current_Grp3RFF.keySet()[0])
                                    'E1156_03' ''
                                    'E4000_04' ''
                                    'E1060_05' ''
                                }
                            }
                        }
                    }

                    'DTM' {
                        'C507_01' {
                            'E2005_01' '334'
                            if(current_Body?.Event?.EventDT?.LocDT){
                                'E2380_02' util.convertXmlDateTime(current_Body?.Event?.EventDT?.LocDT,'yyyyMMddHHmm')
                            }
                            'E2379_03' '203'
                        }
                    }
                    /*map Event Location info to Group5 LOC*/
                    'LOC' {
                        'E3227_01' '175'
                        'C517_02' {
                            if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                                'E3225_01' current_Body?.Event?.Location?.LocationCode?.UNLocationCode

                            }else if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode){
                                'E3225_01' current_Body?.Event?.Location?.LocationCode?.SchedKDCode

                            }
                            'E1131_02' '139'
                            if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                                'E3055_03' '6'
                            }else if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode){
                                'E3055_03' '112'
                            }
                            if(current_Body?.Event?.Location?.CityDetails?.City){
                                'E3224_04' current_Body?.Event?.Location?.CityDetails?.City
                            }
                        }
                    }
                    /**map route info : from oceanleg
                     * shipment dir = I , get first oceanleg
                     * shipment dir = o, get last oceanleg
                   */
                    OceanLeg firstOceanLeg = null
                    OceanLeg lastOceanLeg = null
                    if(current_Body?.Route?.OceanLeg) {
                        firstOceanLeg = current_Body?.Route?.OceanLeg[0]
                        lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
                    }

                    POR por = current_Body?.Route?.POR
                    FirstPOL firstPOL = current_Body?.Route?.FirstPOL
                    LastPOD lastPOD = current_Body?.Route?.LastPOD
                    FND fnd = current_Body?.Route?.FND
                    'Group6_TDT' {
                        'TDT' {
                            'E8051_01' '20'
                            'E8028_02' ''
                            'C220_03' {
                                'E8067_01' '1'
                                'E8066_02' ''
                            }
                            'C228_04' {
                                'E8179_01' '13'
                                'E8178_02' ''
                            }
                            'C040_05' {
                                'E3127_01' SCAC
                                'E1131_02' '172'
                                'E3055_03' ''
                                'E3128_04' 'ORIENT OVERSEAS CONTAINER LINES'
                            }
                            'E8101_06' ''
                            'C401_07' {
                                'E8457_01' ''
                                'E8459_02' ''
                                'E7130_03' ''
                            }
                            'C222_08' {
                                if(shipDir =='I'){
                                    'E8213_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 9)
                                }else if(shipDir =='O'){
                                    'E8213_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 9)
                                }

                                'E1131_02' '146'
                                'E3055_03' ''
                                if(shipDir =='I'){
                                    'E8212_04' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName, 1, 35)
                                }else if(shipDir =='O'){
                                    'E8212_04' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName, 1, 35)
                                }
                                'E8453_05' ''
                            }
                            'E8281_09' ''
                        }

                        OceanLeg oceanLeg = null
                        if(shipDir =='I'){
                            oceanLeg = lastOceanLeg
                        }else if(shipDir == 'O'){
                            oceanLeg = firstOceanLeg
                        }
                        //map POR to LOC
                        'Group7_LOC' {
                            //map POR to LOC
                            if (por?.CityDetails?.LocationCode?.UNLocationCode || por?.CityDetails?.LocationCode?.SchedKDCode) {
                                'LOC' {
                                    'E3227_01' '88'
                                    'C517_02' {
                                        if (por?.CityDetails?.LocationCode?.UNLocationCode) {
                                            'E3225_01' por?.CityDetails?.LocationCode?.UNLocationCode
                                        } else if (por?.CityDetails?.LocationCode?.SchedKDCode) {
                                            'E3225_01' por?.CityDetails?.LocationCode?.SchedKDCode
                                        }
                                        'E1131_02' '139'
                                        if (por?.CityDetails?.LocationCode?.UNLocationCode) {
                                            'E3055_03' '6'
                                        } else {
                                            'E3055_03' '112'
                                        }
                                        if (por?.CityDetails?.City) {
                                            'E3224_04' por?.CityDetails?.City
                                        }
                                    }
                                }
                            }
                        }
                        // map FirstPOL and OceanLeg[0].POL to LOC
                        def polLOC0203 = null
                        'Group7_LOC' {
                            if (firstPOL?.Port?.LocationCode?.UNLocationCode || firstPOL?.Port?.LocationCode?.SchedKDCode
                                    || oceanLeg?.POL?.Port?.LocationCode?.UNLocationCode || oceanLeg?.POL?.Port?.LocationCode?.SchedKDCode) {
                                'LOC' {
                                    'E3227_01' '9'
                                    'C517_02' {
                                        if (oceanLeg?.POL?.Port?.LocationCode?.UNLocationCode) {
                                            'E3225_01' oceanLeg?.POL?.Port?.LocationCode?.UNLocationCode
                                            polLOC0203 = '6'
                                        } else if (oceanLeg?.POL?.Port?.LocationCode?.SchedKDCode) {
                                            'E3225_01' oceanLeg?.POL?.Port?.LocationCode?.SchedKDCode
                                            polLOC0203 = '112'
                                        } else if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
                                            polLOC0203 = '6'
                                        } else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
                                            'E3225_01' firstPOL?.Port?.LocationCode?.UNLocationCode
                                            polLOC0203 = '112'
                                        }
                                        'E1131_02' '139'

                                        if (polLOC0203) {
                                            'E3055_03' polLOC0203
                                        }
                                        if (oceanLeg?.POL?.Port?.PortName) {
                                            'E3224_04' oceanLeg?.POL?.Port?.PortName
                                        } else if (oceanLeg?.POL?.Port?.City) {
                                            'E3224_04' oceanLeg?.POL?.Port?.City
                                        } else if (firstPOL?.Port?.PortName) {
                                            'E3224_04' firstPOL?.Port?.PortName
                                        } else if (firstPOL?.Port?.City) {
                                            'E3224_04' firstPOL?.Port?.City
                                        }
                                    }
                                }
                                //map DepartureDT to LOC-DTM for POL
                                if (oceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'E' }?.LocDT) {
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '133'
                                            'E2380_02' util.convertXmlDateTime(oceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'E' }?.LocDT, 'yyyyMMddHHmm')
                                            'E2379_03' '203'
                                        }
                                    }
                                }
                                if (oceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '186'
                                            'E2380_02' util.convertXmlDateTime(oceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'A' }?.LocDT, 'yyyyMMddHHmm')
                                            'E2379_03' '203'
                                        }
                                    }
                                }
                            }
                        }
                        //map LastPOD and OceanLeg[0].POD to LOC
                        def podLOC0203 = null
                        'Group7_LOC' {
                            if (lastPOD?.Port?.LocationCode?.UNLocationCode || lastPOD?.Port?.LocationCode?.SchedKDCode
                                    || oceanLeg?.POD?.Port?.LocationCode?.UNLocationCode || lastPOD?.Port?.LocationCode?.SchedKDCode) {
                                'LOC' {
                                    'E3227_01' '11'
                                    'C517_02' {
                                        if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
                                            'E3225_01' lastPOD?.Port?.LocationCode?.UNLocationCode
                                            podLOC0203 = '6'
                                        } else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
                                            'E3225_01' lastPOD?.Port?.LocationCode?.SchedKDCode
                                            podLOC0203 = '112'
                                        } else if (oceanLeg?.POD?.Port?.LocationCode?.UNLocationCode) {
                                            'E3225_01' oceanLeg?.POD?.Port?.LocationCode?.UNLocationCode
                                            podLOC0203 = '6'
                                        } else if (oceanLeg?.POD?.Port?.LocationCode?.SchedKDCode) {
                                            'E3225_01' oceanLeg?.POD?.Port?.LocationCode?.SchedKDCode
                                            podLOC0203 = '112'
                                        }
                                        'E1131_02' '139'
                                        if (podLOC0203) {
                                            'E3055_03' podLOC0203
                                        }
                                        if (lastPOD?.Port?.PortName) {
                                            'E3224_04' lastPOD?.Port?.PortName
                                        } else if (lastPOD?.Port?.City) {
                                            'E3224_04' lastPOD?.Port?.City
                                        } else if (oceanLeg?.POD?.Port?.PortName) {
                                            'E3224_04' oceanLeg?.POD?.Port?.PortName
                                        } else if (oceanLeg?.POD?.Port?.City) {
                                            'E3224_04' oceanLeg?.POD?.Port?.City
                                        }
                                    }
                                }
                                if (oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT
                                        || fnd?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT) {
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '132'
                                            if (oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT) {
                                                'E2380_02' util.convertXmlDateTime(oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT,'yyyyMMddHHmm')
                                            } else if (fnd?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT) {
                                                'E2380_02'  util.convertXmlDateTime(fnd?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT,'yyyyMMddHHmm')
                                            }
                                            'E2379_03' '203'
                                        }
                                    }
                                }
                                if (oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '178'
                                            if (oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
                                                'E2380_02' util.convertXmlDateTime(oceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'A' }?.LocDT,'yyyyMMddHHmm')
                                            }
                                            'E2379_03' '203'
                                        }
                                    }
                                }
                            }
                        }
                        //map FND to LOC
                        def fndLoc0203 = null
                        'Group7_LOC' {
                            if (fnd?.CityDetails?.LocationCode?.UNLocationCode || fnd?.CityDetails?.LocationCode?.SchedKDCode) {
                                'LOC' {
                                    'E3227_01' '7'
                                    'C517_02' {
                                        if (fnd?.CityDetails?.LocationCode?.UNLocationCode) {
                                            'E3225_01' fnd?.CityDetails?.LocationCode?.UNLocationCode
                                            fndLoc0203 = '6'
                                        } else if (fnd?.CityDetails?.LocationCode?.SchedKDCode) {
                                            'E3225_01' fnd?.CityDetails?.LocationCode?.SchedKDCode
                                            fndLoc0203 = '112'
                                        }
                                        'E1131_02' '139'
                                        'E3055_03' fndLoc0203
                                        if (fnd?.CityDetails?.City)
                                            'E3224_04' fnd?.CityDetails?.City
                                    }
                                }
                            }
                        }
                    }
                    //map container info to Group8 EQD
                    'Group8_EQD' {
                        'EQD' {
                            if (current_Body?.Container?.ContainerNumber || current_Body?.Container?.ContainerCheckDigit) {
                                'E8053_01' 'CN'
                            }

                            'C237_02' {
                                if (current_Body?.Container?.ContainerNumber || current_Body?.Container?.ContainerCheckDigit) {
                                    'E8260_01' util.substring(ctUtil.emptyToString(current_Body?.Container?.ContainerNumber) + ctUtil.emptyToString(current_Body?.Container?.ContainerCheckDigit), 1, 17)
                                }
                                'E1131_02' ''
                                'E3055_03' ''
                                'E3207_04' ''
                            }
                            'C224_03' {
                                if (current_Body?.Container?.CarrCntrSizeType) {
                                    'E8155_01' current_Body?.Container?.CarrCntrSizeType
                                }
                                'E1131_02' '102'
                                'E3055_03' '5'
                                'E8154_04' ''
                            }
                            'E8077_04' ''
                            'E8249_05' ''
                            if (current_Body?.Container?.ContainerStatus == 'Empty' || current_Body?.Event?.CS1Event in ['CS010', 'CS210']) {
                                'E8169_06' '4'
                            } else {
                                'E8169_06' '5'
                            }
                        }
                    }
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

        headerMsgDT = util.convertXmlDateTime(ct.Header.MsgDT.LocDT, 'yyyyMMddHHmmss')
        //long ediIsaCtrlNumber = ctrlNos[0]
        //long ediGroupCtrlNum = ctrlNos[1]
        def txnErrorKeys = []

        headerAction = ct.Header?.Action

        //duplication -- CT special logic

		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairsbyConvertType(TP_ID,'EventCdeCSIntVal', conn)
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)
        //start body looping
        bodies.eachWithIndex { current_Body, current_BodyIndex ->


            def eventCS2Cde = current_Body.Event.CS1Event
            def eventExtCde = util.getConversion('PANALPINA','CT','O','EventCdeCSIntVal',eventCS2Cde,conn)
            //prep checking
            List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

            if (errorKeyList.size() == 0) {
                //pass validateBeforeExecution
                //main mapping
                generateBody(current_Body,current_BodyIndex, outXml)
                outputBodyCount++
            }
            //posp checking
            pospValidation()
            ctUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
            ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, eventExtCde, TP_ID, conn)
            txnErrorKeys.add(errorKeyList);
        }

        //EndWorkFlow

        //End root node
        outXml.nodeCompleted(null,IFTSTA)
        bizKeyXml.nodeCompleted(null,bizKeyRoot)
        csuploadXml.nodeCompleted(null,csuploadRoot)


        ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
        ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
        ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID);
        if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);

        String result = "";
        if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
            //if exists one txn without error, then return result
            result = writer?.toString();
            result = util.cleanXml(result)
        }

        writer.close();
        csuploadWriter.close()
        bizKeyWriter.close()

        return result;
    }

    public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
        List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
        //EVENT_NOT_SUBSCRIBED
        ctUtil.missingEventStatusCode(eventCS2Cde,eventExtCde,true,' - Event not subscribed by Partner',errorKeyList)
        //EventLocation type1 CSCountryCde , city and UNLocationcode are missed
        if(util.isEmpty(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode)){
            ctUtil.missingEventLocation(eventCS2Cde,current_Body?.Event,true,null,errorKeyList)
        }
        //CheckEventTriggeredTD
        ctUtil.checkEventTriggeredTD(eventCS2Cde,['CS067'],['CS277'],current_Body?.Route,false,null,errorKeyList)
        if(eventCS2Cde=='CS190' && current_Body?.Route?.Inbound_intermodal_indicator){
            ctUtil.checkWithIBIntermodal(eventCS2Cde,current_Body?.Route,'0',false,null,errorKeyList)
        }
        if(eventCS2Cde=='CS180'){
            ctUtil.checkWithIBHaulage(eventCS2Cde,current_Body?.BookingGeneralInfo[0],'C',false,null,errorKeyList)
        }

        if(eventCS2Cde in['CS170','CS200']){
            ctUtil.checkWithActivityLocation(eventCS2Cde,current_Body?.Route?.FND,'US',false,null,errorKeyList)
        }

        return errorKeyList;
    }

    public boolean pospValidation() {

    }
}
