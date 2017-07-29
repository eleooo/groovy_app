package cs.b2b.mapping.scripts

import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.text.SimpleDateFormat

/**
 * Created by XIAOTR on 6/26/2017.
 * pattern after DUMMYCTD99B
 */
class CUS_CS2CTXML_D99B_CGOSVCS {

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



    public void generateBody(Body current_Body, MarkupBuilder outXml) {

        //CT special fields
        def vCS1Event = current_Body.Event.CS1Event
        def vCS1EventFirst5 = util.substring(vCS1Event,1,5)

        def SCAC = current_Body?.GeneralInformation?.SCAC

        def shipDir = util.getConversionWithoutTP('CT','O','EventDirection',vCS1EventFirst5,conn)
        def contianerTypeConvertion = util.getConversionWithScac('CGOSVCS','CT','O','ContainerType',current_Body?.Container?.CarrCntrSizeType,SCAC,conn)
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
                    // get current datetime
                    'E1004_01' new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
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
                            'E1131_02' ''
                            'E3055_03' ''
                        }
                        def queryEventConversion = util.getCdeConversionFromIntCde('CGOSVCS','CT','O','<QUERY-NULL-SCAC>','EDIFACT','EventStatusCode',vCS1Event,conn)
                        'C555_02' {
                            if(queryEventConversion.get("EXT_CDE")){
                                'E4405_01' queryEventConversion.get("EXT_CDE")
                            }
                            'E1131_02' ''
                            'E3055_03' ''
                            'E4404_04' ''
                        }
                    }
                    //get BL Numer
                    current_Body?.BLGeneralInfo?.findAll{util.isNotEmpty(it?.BLNumber)}?.each {current_BLGeneralInfo ->
                        'RFF' {
                            'C506_01' {
                                'E1153_01' 'BM'
                                'E1154_02' util.substring(current_BLGeneralInfo.BLNumber,1,35)
                                'E1156_03' ''
                                'E4000_04' ''
                            }

                        }
                    }
                    //get booking bumber
                    current_Body?.BookingGeneralInfo?.findAll{util.isNotEmpty(it?.CarrierBookingNumber)}?.each { current_bkGeneralInfo ->
                        'RFF' {
                            'C506_01' {
                                'E1153_01' 'BN'
                                'E1154_02' util.substring(current_bkGeneralInfo.CarrierBookingNumber, 1, 35)
                                'E1156_03' ''
                                'E4000_04' ''

                            }
                        }
                    }

                    //get External Reference
                    def externalReferenceMap = ['CTR':'CT','EQ':'EQ','EXPR':'RF','FR':'FF','INV':'IV','MVID':'VT','PO':'ON','SID':'SI']
                    current_Body?.ExternalReference?.each {current_ExtRef ->
                        if(externalReferenceMap.containsKey(current_ExtRef?.CSReferenceType)){
                            'RFF' {
                                'C506_01' {
                                    'E1153_01' externalReferenceMap.get(current_ExtRef?.CSReferenceType)
                                    'E1154_02' util.substring(current_ExtRef?.ReferenceNumber,1,35)
                                    'E1156_03' ''
                                    'E4000_04' ''
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
                    /*map party info to Group5 NAD*/
                    //party type conversion
                    def group5NADLimitaion = 9
                    def partyTypeMap = ['ANP':'N2','CGN':'CN','FWD':'FW','NPT':'N1','SHP':'CZ']
                    def individualParty = []
                    current_Body?.Party?.findAll {util.isNotEmpty(it?.CarrierCustomerCode)}?.each{current_Party ->
                        if(partyTypeMap?.containsKey(current_Party?.PartyType) && current_Party?.CarrierCustomerCode && group5NADLimitaion>0 && !individualParty.contains(util.substring(current_Party?.CarrierCustomerCode,1,35)+current_Party?.PartyType) ){
                            'NAD' {
                                'E3035_01' partyTypeMap.get(current_Party?.PartyType)
                                'C082_02' {
                                    'E3039_01' util.substring(current_Party?.CarrierCustomerCode,1,35)
                                    'E1131_02' ''
                                    'E3055_03' ''
                                }
                                individualParty.add(util.substring(current_Party?.CarrierCustomerCode,1,35)+current_Party?.PartyType)
                                group5NADLimitaion--
                            }
                        }
                    }
                    if(SCAC && group5NADLimitaion>0){
                        'NAD' {
                            'E3035_01' 'CA'
                            'C082_02' {
                                'E3039_01' SCAC
                                'E1131_02' ''
                                'E3055_03' ''
                            }

                            group5NADLimitaion--
                        }
                    }
                    /*map Event Location info to Group5 LOC*/
                    'LOC' {
                        'E3227_01' '175'
                        'C517_02' {
                            if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                                'E3225_01' current_Body?.Event?.Location?.LocationCode?.UNLocationCode
                            }
                            'E1131_02' '139'
                            if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                                'E3055_03' '6'
                            }else {
                                'E3055_03' 'ZZZ'
                            }
                            if(current_Body?.Event?.Location?.LocationName){
                                'E3224_04' current_Body?.Event?.Location?.LocationName
                            }

                        }
                    }
                    /*map route info : loop by oceanLeg  ---Group6 TDT*/
                    current_Body?.Route?.OceanLeg?.each {current_OceanLeg ->
                        'Group6_TDT' {
                            'TDT' {
                                'E8051_01' '20'
                                if(shipDir =='I' || shipDir =='T/S-I'){
                                    if(current_OceanLeg?.SVVD?.Discharge?.Voyage){
                                        'E8028_02' util.substring(ctUtil.emptyToString(current_OceanLeg?.SVVD?.Discharge?.Voyage) + ctUtil.emptyToString(current_OceanLeg?.SVVD?.Discharge?.Direction),1,17)
                                    }
                                }else if(shipDir =='O' || shipDir =='T/S-O'){
                                    if(current_OceanLeg?.SVVD?.Loading?.Voyage){
                                        'E8028_02'  util.substring(ctUtil.emptyToString(current_OceanLeg?.SVVD?.Loading?.Voyage) + ctUtil.emptyToString(current_OceanLeg?.SVVD?.Loading?.Direction),1,17)
                                    }
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
                                    if(SCAC){
                                        'E3127_01' SCAC
                                    }
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
                                    //Tibco : when shipDir = I, there is a bug in app, awalys get the below logic, but no mathlength setup ---> causedfailed to get the LloydsNumber
                                    /*tib:trim($Start/pfx13:group/pfx6:Body-ContainerMovement/pfx6:Route-CntrMvmtCusBodyType/pfx6:OceanLeg-CntrMvmtCusRouteType[1]/pfx6:SVVD-CntrMvmtOceanLegType/pfx6:Discharge-SVVD-CntrMvmtOceanLegType/pfx6:LloydsNumber-SimpleSVVDCusType) !="" and
                                    tib:trim($Start/pfx13:group/pfx11:RuleName/pfx11:Attribute[pfx11:Key="MaxLength"]/pfx11:Value) !=""*/
                                    if(shipDir =='I' || shipDir =='T/S-I'){
                                        if(current_OceanLeg?.SVVD?.Discharge?.LloydsNumber){
                                            'E8213_01' util.substring(current_OceanLeg?.SVVD?.Discharge?.LloydsNumber,1,9)
                                            'E1131_02' 'L'
                                        }else if(current_OceanLeg?.SVVD?.Discharge?.CallSign){
                                            'E8213_01' util.substring(current_OceanLeg?.SVVD?.Discharge?.CallSign,1,9)
                                            'E1131_02' '103'
                                        }
                                        'E3055_03' ''
                                        if(current_OceanLeg?.SVVD?.Discharge?.VesselName){
                                            'E8212_04' util.substring(current_OceanLeg?.SVVD?.Discharge?.VesselName,1,35)
                                        }
                                    }else if(shipDir =='O' || shipDir =='T/S-O'){
                                        if(current_OceanLeg?.SVVD?.Loading?.LloydsNumber){
                                            'E8213_01' util.substring(current_OceanLeg?.SVVD?.Loading?.LloydsNumber,1,9)
                                            'E1131_02' 'L'
                                        }else if(current_OceanLeg?.SVVD?.Loading?.CallSign){
                                            'E8213_01' util.substring(current_OceanLeg?.SVVD?.Loading?.CallSign,1,9)
                                            'E1131_02' '103'
                                        }
                                        'E3055_03' ''
                                        if(current_OceanLeg?.SVVD?.Loading?.VesselName){
                                            'E8212_04' util.substring(current_OceanLeg?.SVVD?.Loading?.VesselName,1,35)
                                        }
                                    }
                                    'E8453_05' ''
                                }
                                'E8281_09' ''
                            }

                            /*route info : loop LOC */
                            //#1 FND info
                            'Group7_LOC' {

                                'LOC' {
                                    'E3227_01' '7'
                                    'C517_02' {
                                        if(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode){
                                            'E3225_01' current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode
                                        }
                                        'E1131_02' '229'
                                        'E3055_03' '6'
                                        if(current_Body?.Route?.FND?.CityDetails?.City){
                                            'E3224_04' current_Body?.Route?.FND?.CityDetails?.City
                                        }
                                    }
                                }
                            }
                            //#2 POL info
                            'Group7_LOC' {

                                'LOC' {
                                    'E3227_01' '9'
                                    'C517_02' {
                                        if (current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode) {
                                            'E3225_01' current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode
                                        }

                                        'E1131_02' '139'
                                        'E3055_03' '6'
                                        if (current_OceanLeg?.POL?.Port?.City) {
                                            'E3224_04' current_OceanLeg?.POL?.Port?.City
                                        }
                                    }
                                }
                                //POL-DTM map from POL_Est
                                if (current_OceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'E' }?.LocDT){
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '133'
                                            'E2380_02' util.convertXmlDateTime(current_OceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'E' }?.LocDT, 'yyyyMMddHHmm')
                                            'E2379_03' '203'
                                        }
                                    }
                                }
                                //POL-DTM map from POL_Act
                                if (current_OceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '186'
                                            'E2380_02' util.convertXmlDateTime(current_OceanLeg?.POL?.DepartureDT?.find { it?.attr_Indicator == 'A' }?.LocDT, 'yyyyMMddHHmm')
                                            'E2379_03' '203'
                                        }
                                    }
                                }
                            }
                            //#3 POD info
                            'Group7_LOC' {
                                'LOC' {
                                    'E3227_01' '11'
                                    'C517_02' {
                                        if (current_OceanLeg?.POD?.Port?.LocationCode?.UNLocationCode) {
                                            'E3225_01' current_OceanLeg?.POD?.Port?.LocationCode?.UNLocationCode
                                        }
                                        'E1131_02' '139'
                                        'E3055_03' '6'
                                        if (current_OceanLeg?.POD?.Port?.City) {
                                            'E3224_04' current_OceanLeg?.POD?.Port?.City
                                        }
                                    }
                                }
                                //POD-DTM map from POD_Est
                                if (current_OceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT) {
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '132'
                                            'E2380_02' util.convertXmlDateTime(current_OceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT, 'yyyyMMddHHmm')
                                            'E2379_03' '203'
                                        }
                                    }
                                }
                                //POD-DTM map from POD_Act
                                if (current_OceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'A' }?.LocDT) {
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '178'
                                            'E2380_02' util.convertXmlDateTime(current_OceanLeg?.POD?.ArrivalDT?.find { it?.attr_Indicator == 'A' }?.LocDT, 'yyyyMMddHHmm')
                                            'E2379_03' '203'
                                        }
                                    }
                                }
                            }
                            //#4 POR info
                            'Group7_LOC' {
                                'LOC' {
                                    'E3227_01' '88'
                                    'C517_02' {
                                        if (current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) {
                                            'E3225_01' current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
                                        }

                                        'E1131_02' '229'
                                        'E3055_03' '6'
                                        if (current_Body?.Route?.POR?.CityDetails?.City) {
                                            'E3224_04' current_Body?.Route?.POR?.CityDetails?.City
                                        }
                                    }
                                }
                            }

                        }
                    }
                    //map container info to Group8 EQD
                    'Group8_EQD' {
                        'EQD' {
                            'E8053_01' 'CN'
                            'C237_02' {
                                if(current_Body?.Container?.ContainerNumber || current_Body?.Container?.ContainerCheckDigit){
                                    'E8260_01' util.substring(ctUtil.emptyToString(current_Body?.Container?.ContainerNumber) + ctUtil.emptyToString(current_Body?.Container?.ContainerCheckDigit),1,17)
                                }
                                'E1131_02' ''
                                'E3055_03' ''
                                'E3207_04' ''
                            }
                            'C224_03' {
                                if(contianerTypeConvertion){
                                    'E8155_01' contianerTypeConvertion
                                }else if(current_Body?.Container?.CarrCntrSizeType){
                                    'E8155_01' current_Body?.Container?.CarrCntrSizeType
                                }
                                'E1131_02' ''
                                'E3055_03' ''
                                'E8154_04' ''
                            }
                            'E8077_04' ''
                            'E8249_05' ''
                            if(current_Body?.Container?.ContainerStatus == 'Empty'){
                                'E8169_06' '4'
                            }else if(current_Body?.Container?.ContainerStatus == 'Laden'){
                                'E8169_06' '5'
                            }else  if(current_Body?.Event?.CS1Event in ['CS010','CS210']){
                                'E8169_06' '4'
                            }
                        }
                        //map CA and SH : seal Number
                        current_Body?.Container?.Seal?.each {current_Seal ->
                            if(current_Seal?.SealType in ['CA','SH']){
                                'SEL' {
                                    if(current_Seal?.SealNumber){
                                        'E9308_01' util.substring(current_Seal?.SealNumber,1,35)
                                    }
                                    'C215_02' {
                                        'E9303_01' current_Seal?.SealType
                                        'E1131_02' ''
                                        'E3055_03' ''
                                        'E9302_04' ''
                                    }
                                    'E4517_03' ''
                                    'C208_04' {
                                        'E7402_01' ''
                                        'E7402_02' ''
                                    }
                                }
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
//		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
//
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

        //start body looping
        ct.Body.eachWithIndex { current_Body, current_BodyIndex ->


            def eventCS2Cde = current_Body.Event.CS1Event
            def eventExtCde = util.getCdeConversionFromIntCde('CGOSVCS', 'CT', 'O','<QUERY-NULL-SCAC>','EDIFACT', 'EventStatusCode', eventCS2Cde, conn).get('EXT_CDE')

            //prep checking
            List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

            if (errorKeyList.size() == 0) {
                //pass validateBeforeExecution
                //main mapping

                generateBody(current_Body, outXml)
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


        return errorKeyList;
    }

    public boolean pospValidation() {

    }
}
