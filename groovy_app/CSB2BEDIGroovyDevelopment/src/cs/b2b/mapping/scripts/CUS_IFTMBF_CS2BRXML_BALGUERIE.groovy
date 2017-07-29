package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import cs.b2b.core.mapping.util.XmlBeanParser
import java.sql.Connection
import java.text.SimpleDateFormat
import cs.b2b.core.mapping.bean.edi.edifact.d96b.IFTMBF_CS.EDI_CS_IFTMBF
import cs.b2b.core.mapping.bean.edi.edifact.d96b.IFTMBF_CS.Group_UNH13

/**
 * Created by YOUAL on 7/7/2017.
 */
class CUS_IFTMBF_CS2BRXML_BALGUERIE {
    cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
    cs.b2b.core.mapping.util.MappingUtil_BR_I_Common brUtil = new cs.b2b.core.mapping.util.MappingUtil_BR_I_Common(util)

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
    def yyyyMMddHHmmss = 'yyyyMMddHHmmss'
    def yyyyMMddHHmm = 'yyyyMMddHHmm'

    def currentSystemDt = null

    Map<String, String> extPartyType = ["BK": "BPT",
                                        "CA": "CAR",
                                        "CN": "CGN",
                                        "FW": "FWD",
                                        "CZ": "SHP"]
    Map<String, String> extWeightUnitMap = ["KGM" : "KGS", "LBR" : "LBS", "TON" : "TON"]

    String mapping(String inputXmlBody, String[] runtimeParams, Connection conn){
/**
 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
 */
        //println inputXmlBody
        inputXmlBody = util.removeBOM(inputXmlBody)
        /**
         * Part II: get app mapping runtime parameters
         */
        this.conn = conn
        appSessionId = util.getRuntimeParameter("B2BSessionID", runtimeParams)
        sourceFileName = util.getRuntimeParameter("OriginalSourceFileName", runtimeParams)
        //pmt info
        TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams)
        MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams)
        DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams)
        MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams)

        /**
         * Part III: read xml and prepare output xml
         */

        XmlBeanParser xmlBeanParser = new XmlBeanParser()
        EDI_CS_IFTMBF iftmbf = xmlBeanParser.xmlParser(inputXmlBody,EDI_CS_IFTMBF.class)

        def writer = new StringWriter()
        def outXml = new MarkupBuilder(writer) //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
        outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

        def bizKeyWriter = new StringWriter()
        def bizKeyXml = new MarkupBuilder(bizKeyWriter)
        bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

        TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
        def currentSystemDt = new Date()

        /**
         * Part IV: mapping script start from here
         */
        //create root node

        def brRoot = outXml.createNode('ns0:BookingRequest', ['xmlns:ns0':'http://www.cargosmart.com/bookingrequest', 'xmlns:ns1':'http://www.cargosmart.com/common'])
        def bizKeyRoot = bizKeyXml.createNode('root')

        def outputBodyCount = 0
        def headerGenerated = false
        List<Map<String,String>> txnErrorKeys = new ArrayList<Map<String,String>>()

        def bodies = iftmbf.Group_UNH
        def transactionDT = ""
        if (util.notEmpty(iftmbf.UNB?.S004_04)){
            transactionDT = util.substring(currentSystemDt.format("yyyy"), 1, 2) + iftmbf.UNB?.S004_04?.E0017_01 + iftmbf.UNB?.S004_04?.E0019_02
        }

        def actionType = ""
        if (iftmbf.Group_UNH[0].BGM?.E1225_03 == '9') {
            actionType = "NEW"
        } else if (iftmbf.Group_UNH[0].BGM?.E1225_03 == '5' || iftmbf.Group_UNH[0].BGM?.E1225_03 == '4') {
            actionType = "UPD"
        } else if (iftmbf.Group_UNH[0].BGM?.E1225_03 == '1') {
            actionType = "CANCEL"
        }
        //map from EDIInformation/InterchangeControlNumber
        def InterchangeTransactionID = iftmbf.Group_UNH?.UNH?.E0062_01

        def senderID =iftmbf.UNB?.S002_02?.E0004_01

        bodies.eachWithIndex { current_Body, current_BodyIndex ->
            def SCAC_CDE = ""
            def SCAC_frmNAD = current_Body?.Group10_NAD?.find { it.NAD?.E3035_01 == "CA" }?.NAD?.C082_02?.E3039_01
            def SCAC_frmTDT = current_Body?.Group7_TDT?.TDT?.C040_05?.E3127_01

            if (util.notEmpty(SCAC_frmNAD)) {
                SCAC_CDE = SCAC_frmNAD
            }else if (util.notEmpty(SCAC_frmTDT)) {
                SCAC_CDE = SCAC_frmTDT
            }

            // prep checking
            List<Map<String,String>> AppErrors = prepValidation(current_Body, current_BodyIndex, SCAC_CDE, conn)

            // map Header once only
            if (!headerGenerated){

                brUtil.generateHeader(outXml, current_Body, outputBodyCount, currentSystemDt, DIR_ID, MSG_TYPE_ID, TP_ID, MSG_REQ_ID, InterchangeTransactionID)
                headerGenerated = true
            }
            // map Body
            if (AppErrors.size()==0) {
                generateBody(outXml, current_Body, outputBodyCount, currentSystemDt, transactionDT, SCAC_CDE, senderID, AppErrors, actionType, InterchangeTransactionID)
            } else {
                outXml.'Request' {}
            }

            // add pre-validation error to error set
            txnErrorKeys.add(AppErrors)
        }

        // close mapping of CS2XML
        outXml.nodeCompleted(null,brRoot)
        String brxml = writer?.toString()

        //println(inputbr)
        // do post-validation
        List<Map<String,String>> postErrors = brUtil.postValidation1(brxml, txnErrorKeys)
        // add post-validation error to error set
        bodies.eachWithIndex { current_Body, current_BodyIndex ->
            if (postErrors[current_BodyIndex])
                txnErrorKeys[current_BodyIndex].add(postErrors[current_BodyIndex])
        }

        // map Bizkey
        buildBizKey(bizKeyXml, brxml, iftmbf, txnErrorKeys)
        bizKeyXml.nodeCompleted(null,bizKeyRoot)

        String bizkeyXML = bizKeyWriter?.toString()
        //println(bizkeyXML)


        writer.close()
        bizKeyWriter.close()

        println brxml
        println bizKeyWriter.toString()

        // promote bizkey
        brUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter)
        def BizKey = new XmlSlurper().parseText(bizKeyWriter?.toString())

        boolean hasGoodBR = false
        BizKey.children().each { currentTransaction ->
            if (!(util.notEmpty(currentTransaction.AppErrorReport))) {
                hasGoodBR = true
            }
        }

        def nodeOutput = new XmlParser().parseText(brxml)

        if (hasGoodBR) {
            List removeBodies = new LinkedList()

            BizKey.children().eachWithIndex { currentTransaction, currentTransactionIndex ->
                def currentBody = nodeOutput.children().findAll {
                    it.name().toString().contains('Request')
                }?.get(currentTransactionIndex)
                if ((util.notEmpty(currentTransaction.AppErrorReport))) {
                    removeBodies.add(currentBody)
                }
            }

            removeBodies.each { currentBody ->
                nodeOutput.remove(currentBody)
            }

            String cleanedOutputXml = util.cleanXml(XmlUtil.serialize(nodeOutput))

            return cleanedOutputXml
        }
        else{
            // prepare Node version of BookingRequest
            Node emptyMsg = nodeOutput.clone()
            List kids= new LinkedList()
            //remove the <Header> and all <Body>
            emptyMsg.children().eachWithIndex {kid , index->
                kids.add(kid)
            }
            kids.eachWithIndex {kid , index->
                emptyMsg.remove(kid)
            }

            return XmlUtil.serialize(emptyMsg)
        }
    }

    private List<Map<String,String>> prepValidation(def body, def currentTransactionIndex, def SCAC, Connection conn) {
        Group_UNH13 currentTransaction = body
        List AppErrors = new ArrayList()
        currentTransactionIndex  = currentTransactionIndex + 1

//        $mapExceptionFlow/root/Config[SegmentId="DTM" and SegmentField="C507_2379"]/ProcessID = "2" and
//        (exists($Start/root/pfx:T-IFTMBF/pfx:S-DTM_5[tib:trim(pfx:C-C507_5_01/pfx:E-2379_5_01.03)=""] )or
//                exists($Start/root/pfx:T-IFTMBF/pfx:S-DTM_5[tib:trim(pfx:C-C507_5_01/pfx:E-2379_5_01.03)!="204"]))

        //Check DTM C507_2379  process 2
        def DTM01 = currentTransaction?.DTM?.C507_01?.E2379_03
        if (DTM01 =="" || DTM01 != "204"){
            Map<String,String> errorKey = null
            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' DTM C507-2379 invalid..']
        }

//        $mapExceptionFlow/root/Config[SegmentId="Group3_RFF" and SegmentField="C506_1154"]/ProcessID = "1" and
//        exists($Start/root/pfx:T-IFTMBF/pfx:L-RFF_15) and
//        $Start/root/pfx:T-IFTMBF/pfx:L-RFF_15/pfx:S-RFF_15/pfx:C-C506_15_01/pfx:E-1154_15_01.02=""

        //Check Group3_RFF C506_1154 process 1
        def RFF01_02 = currentTransaction?.Group3_RFF?.RFF?.C506_01?.E1154_02
        if (util.isEmpty(util.trim(RFF01_02))) {
            Map<String,String> errorKey = null
            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' Missing Mandatory Field Group3_RFF_C506_1154.']
        }

//        $mapExceptionFlow/root/Config[SegmentId="Group16_FTX" and SegmentField="C108_4440"]/ProcessID = "1" and
//        exists($Start/root/pfx:T-IFTMBF/pfx:L-GID_55) and
//        exists($Start/root/pfx:T-IFTMBF/pfx:L-GID_55/pfx:S-FTX_63) and
//        normalize-space($Start/root/pfx:T-IFTMBF/pfx:L-GID_55[exists(pfx:S-FTX_63/pfx:C-C108_63_04)]/pfx:S-FTX_63/pfx:C-C108_63_04)!="" and
//        $Start/root/pfx:T-IFTMBF/pfx:L-GID_55/pfx:S-FTX_63[normalize-space(pfx:C-C108_63_04)!=""]/pfx:C-C108_63_04/pfx:E-4440_63_04.01=""

        //Check Group16GID_FTX C108_4440 process 1
        def FTX04_01 = currentTransaction?.Group16_GID?.FTX?.C108_04?.E4440_01
        if (util.isEmpty(util.trim(FTX04_01))) {
            Map<String,String> errorKey = null
            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' Missing Mandatory Field Group16 FTX C108-4440.']
        }

//        $mapExceptionFlow/root/Config[SegmentId="Group10_NAD" and SegmentField="SCAC"]/ProcessID = "1" and
//        tib:trim($checkSCAC/resultSet/Record[1]/EXT_CDE)=""
        //Check Group10_NAD SCAC process 1
        String validateSCAC = brUtil.checkSCAC(SCAC,conn)
        if (util.isEmpty(validateSCAC)){
            Map<String,String> errorKey = null
            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' Invalid SCAC on the input.']
        }

        return AppErrors
    }

    public void generateBody(MarkupBuilder outXml, def current_Body, def current_BodyIndex, def currentSystemDt, def transactionDT, def SCAC, def senderID, def preErrors, def actionType, def InterchangeTransactionID) {

        Group_UNH13 currentTransaction = current_Body
        SimpleDateFormat sdf = new SimpleDateFormat(xmlDateTimeFormat)
        SimpleDateFormat sdf1 = new SimpleDateFormat(yyyyMMddHHmmss)

        outXml.'ns0:Request' {
            'ns0:TransactionInformation' {
                UUID idOne = UUID.randomUUID();
                'ns1:MessageID' MSG_REQ_ID + ',' + idOne
                'ns1:InterchangeTransactionID' InterchangeTransactionID
            }
            'ns0:EventInformation' {
                'ns1:EventCode' 'bkg_req'
                'ns1:EventDT' {
                    'ns1:LocDT'('TimeZone': 'HKT', 'CSTimeZone': 'HKT', currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss") + '+08:00')
                }
            }
            'ns0:GeneralInformation' {
                'ns0:CSBookingRefNumber' ''

                def vRFF_BN_Number = currentTransaction?.Group3_RFF?.RFF?.C506_01?.find {
                    it.E1153_01 == "BN"
                }?.E1154_02
                if (vRFF_BN_Number) {
                    if (util.notEmpty(SCAC) && (SCAC = util.substring(vRFF_BN_Number, 1, 4))) {
                        'ns0:CarrierBookingNumber' util.substring(vRFF_BN_Number, 5, vRFF_BN_Number.size() - 4)
                    } else {
                        'ns0:CarrierBookingNumber' vRFF_BN_Number
                    }
                }

                if (actionType) {
                    'ns0:ActionType' actionType
                }
                'ns0:SCAC' SCAC

                def vBGM03 = currentTransaction?.BGM?.E1225_03
                if (vBGM03 == "1") {
                    'ns0:BookingOffice' util.getConversionByExtCde(TP_ID, 'BR', 'I', 'BookingOffice', '', conn)
                } else if (vBGM03 != "1") {
                    'ns0:BookingOffice' util.getConversionByExtCde(TP_ID, 'BR', 'I', 'CancelOffice', '', conn)
                } else {
                    'ns0:BookingOffice'
                }
                'ns0:BLIssuingOffice' util.getConversionWithScacByExtCde(TP_ID, 'BR', 'I', 'IssuingOffice', '', SCAC, conn)

                'ns0:Requested' {
                    'ns0:By' senderID
                    'ns0:RequestedDT' {
                        def vDTM01_02 = currentTransaction?.DTM?.C507_01?.find { it.E2005_01 == "318" }?.E2380_02
                        if (vDTM01_02) {
                            'ns1:LocDT'('TimeZone': 'GMT', vDTM01_02)
                        } else {
                            'ns1:LocDT'('TimeZone': 'GMT', currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss"))
                        }
                    }
                }

                def vDGS01_01 = currentTransaction?.Group16_GID[0]?.GDS[0]?.C703_01?.E7085_01

                def vGID_TMP_exists = currentTransaction?.Group16_GID?.findAll { it.TMP }?.size() > 0
                def vGID_DGS_exists = currentTransaction?.Group16_GID?.Group27_DGS?.findAll { it.DGS }?.size() > 0
                def vGID_DIM_exists = currentTransaction?.Group16_GID?.Group19_DIM?.findAll { it.DIM }?.size() > 0

                if (vDGS01_01 == "10") {
                    'ns0:ShipmentCargoType' "AW"
                } else if (vDGS01_01 == "11") {
                    'ns0:ShipmentCargoType' "DG"
                } else if (vDGS01_01 == "12") {
                    'ns0:ShipmentCargoType' "GC"
                } else if (vDGS01_01 == "14") {
                    'ns0:ShipmentCargoType' "RF"
                } else if (vGID_TMP_exists && !vGID_DGS_exists) {
                    'ns0:ShipmentCargoType' "RF"
                } else if (vGID_DGS_exists && currentTransaction?.Group16_GID?.Group27_DGS?.DGS?.C223_04?.E6411_02 == "") {
                    'ns0:ShipmentCargoType' "DG"
                } else if (vGID_DGS_exists && util.notEmpty(currentTransaction?.Group16_GID?.Group27_DGS?.DGS?.C223_04?.E6411_02)) {
                    'ns0:ShipmentCargoType' "RD"
                } else if (vGID_DIM_exists) {
                    'ns0:ShipmentCargoType' "AW"
                } else {
                    'ns0:ShipmentCargoType' "GC"
                }


                def vTMD01_01 = currentTransaction?.Group16_GID[0]?.TMD?.C219_01?.E8335_01
                if (vTMD01_01) {
                    'ns0:ShipmentTrafficMode' {
                        switch (vTMD01_01) {
                            case "2":
                                'ns1:OutBound' 'LCL'
                                'ns1:InBound' 'LCL'
                                break
                            case "3":
                                'ns1:OutBound' 'FCL'
                                'ns1:InBound' 'FCL'
                                break
                            case "4":
                                'ns1:OutBound' 'FCL'
                                'ns1:InBound' 'LCL'
                                break
                            case "5":
                                'ns1:OutBound' 'LCL'
                                'ns1:InBound' 'FCL'
                                break
                            default:
                                'ns1:OutBound' 'FCL'
                                'ns1:InBound' 'FCL'
                                break
                        }
                    }
                }

                def vCOM01_01 = currentTransaction?.COM?.C076_01?.find { it.E3148_01 == "EM" }?.E3155_02
                'ns0:NotificationEmailAddress' util.substring(vCOM01_01, 1, 60)

                def vRFF01_01 = currentTransaction?.Group3_RFF?.RFF?.C506_01?.find { it.E1153_01 == "SI" }?.E1154_02
                if (vRFF01_01) {
                    'ns0:CustomerBookingReference' vRFF01_01
                } else {
                    'ns0:CustomerBookingReference' currentTransaction?.BGM?.C106_02?.E1004_01
                }

                currentTransaction?.Group10_NAD?.each { current_NAD->
                    def vNAD01 = extPartyType[current_NAD?.NAD?.E3035_01]
                    if (util.notEmpty(vNAD01)) {
                        'ns0:Party'{
                            'ns1:PartyType' vNAD01
                                if (current_NAD?.NAD?.C080_04?.E3036_01) {
                                    'ns1:PartyName' current_NAD?.NAD?.C080_04?.E3036_01?.trim() + " " + current_NAD?.NAD?.C080_04?.E3036_02?.trim()
                                }

                                def vNAD02_01 = current_NAD?.NAD?.C082_02?.E3039_01
                                def vNAD04_01 = current_NAD?.NAD?.C080_04?.E3036_01
                                def vCCC_CDE = null

                                if (util.notEmpty(vNAD02_01) && util.getConversionWithScacByExtCde(TP_ID, 'BR', 'I', 'CarrierCustCode', vNAD02_01, SCAC, conn)) {
                                    vCCC_CDE = util.getConversionWithScacByExtCde(TP_ID, 'BR', 'I', 'CarrierCustCode', vNAD02_01, SCAC, conn)
                                } else if (util.notEmpty(vNAD04_01) && util.getConversionWithScacByExtCde(TP_ID, 'BR', 'I', 'CarrierCustCode', vNAD04_01, SCAC, conn)) {
                                    vCCC_CDE = util.getConversionWithScacByExtCde(TP_ID, 'BR', 'I', 'CarrierCustCode', vNAD04_01, SCAC, conn)
                                } else if (util.notEmpty(vNAD02_01)) {
                                    vCCC_CDE = vNAD02_01
                                }

                                if (vCCC_CDE) {
                                    'ns1:CarrierCustomerCode' vCCC_CDE
                                }

                                if (current_NAD?.Group11_CTA) {
                                    'ns1:Contact' {
                                        int name_Cut = current_NAD?.Group11_CTA[0]?.CTA?.C056_02?.E3412_02?.indexOf(",")
                                        if (name_Cut != -1) {
                                            'ns0:FirstName' util.substring(current_NAD?.Group11_CTA[0]?.CTA?.C056_02?.E3412_02, 1, name_Cut)
                                            'ns0:LastName' util.substring(current_NAD?.Group11_CTA[0]?.CTA?.C056_02?.E3412_02, name_Cut + 2, current_NAD?.Group11_CTA[0]?.CTA?.C056_02?.E3412_02?.length())
                                        } else {
                                            'ns0:FirstName' current_NAD?.Group11_CTA[0]?.CTA?.C056_02?.E3412_02
                                        }
                                        def currentNAD_COM_TE = current_NAD.Group11_CTA[0]?.COM?.findAll {
                                            it.C076_01.E3155_02 == "TE"
                                        }
                                        def currentNAD_COM_FX = current_NAD.Group11_CTA[0]?.COM?.findAll {
                                            it.C076_01.E3155_02 == "FX"
                                        }
                                        def currentNAD_COM_EM = current_NAD.Group11_CTA[0]?.COM?.findAll {
                                            it.C076_01.E3155_02 == "EM"
                                        }

                                        if (currentNAD_COM_TE.size() > 0) {
                                            'ns1:ContactPhone' {
                                                'ns1:Number' util.substring(currentNAD_COM_TE[0].C076_01?.E3148_01, 1, 30)
                                            }
                                        }
                                        if (currentNAD_COM_FX.size() > 0) {
                                            'ns1:ContactFax' {
                                                'ns1:Number' util.substring(currentNAD_COM_FX[0].C076_01?.E3148_01, 1, 30)
                                            }
                                        }
                                        if (currentNAD_COM_EM.size() > 0) {
                                            'ns1:ContactEmailAddress' util.substring(currentNAD_COM_EM[0].C076_01?.E3148_01, 1, 60)
                                        }
                                    }
                                }

                                'ns1:Address'{
                                    'ns1:City' current_NAD?.NAD?.E3164_06
                                    'ns1:State' current_NAD?.NAD?.E3229_07
                                     def UN_LOCN_CDE = ""
                                     if (util.notEmpty(current_NAD?.NAD?.E3207_09)){
                                        UN_LOCN_CDE = current_NAD?.NAD?.E3207_09
                                     }
                                     Map<String, String> cs2MasterCity = util.getCS2MasterCityByUNLocCityType(UN_LOCN_CDE, 'S', conn)
                                    'ns1:Country' cs2MasterCity["COUNTY_NME"]
                                    'ns1:PostalCode' util.substring(current_NAD?.NAD?.E3251_08, 1, 12)
                                    if (util.notEmpty(current_NAD?.NAD?.C059_05)) {
                                        'ns1:AddressLines'{
                                            'ns1:AddressLine' current_NAD?.NAD?.C059_05?.E3042_01 + " " + current_NAD?.NAD?.C059_05?.E3042_03
                                        }
                                    }
                                }
                                'ns1:CodeListQualifier' current_NAD?.NAD?.C082_02?.E1131_02
                        }//End-Party
                    }//END-vNAD01
                }//END-Group10_NAD-looping

                currentTransaction?.Group3_RFF?.RFF?.each { current_RFF ->
                    def vCSCarrierRateType = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, 'CarrierRateType', current_RFF?.C506_01?.E1153_01, conn)
                    if (vCSCarrierRateType) {
                        'ns0:CarrierRateReference'{
                            'ns1:CSCarrierRateType' vCSCarrierRateType
                            if (current_RFF?.C506_01?.E1153_01 == "52" && util.isEmpty(current_RFF?.C506_01?.E1154_02)) {
                                'ns1:CarrierRateNumber' "52"
                            } else {
                                'ns1:CarrierRateNumber' current_RFF?.C506_01?.E1154_02
                            }
                        }
                    }

                    def vCSReferenceType = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, 'ReferenceType', current_RFF?.C506_01?.E1153_01, conn)
                    if (vCSReferenceType) {
                        'ns0:ExternalReference'{
                            'ns1:CSReferenceType' vCSReferenceType
                            'ns1:ReferenceNumber' current_RFF?.C506_01?.E1154_02
                            def vEDIRefenceType= util.getConversionByExtCde(TP_ID, 'CS2BR', DIR_ID, 'ReferenceType', current_RFF?.C506_01?.E1153_01, conn)
                            if (vCSReferenceType == "OTH" && vEDIRefenceType == "OTH" ) {
                                'ns0:EDIReferenceType' current_RFF?.C506_01?.E1153_01
                            } else {
                                'ns0:EDIReferenceType' ''
                            }
                        }
                    }
                }//ENG-Group3_RFF-looping

                currentTransaction?.Group32_EQD?.eachWithIndex { currentEQD, currentEQD_index ->
                    'ns0:Container'{
                        def vContainerType_IntCde = util.getConversionWithScacByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, 'ContainerType', currentEQD?.EQD?.C224_03?.E8155_01, SCAC, conn)
                        if (vContainerType_IntCde) {
                            'ns0:CarrCntrSizeType' vContainerType_IntCde
                        } else {
                            'ns0:CarrCntrSizeType' currentEQD?.EQD?.C224_03?.E8155_01
                        }
                        'ns0:Quantity' currentEQD?.EQN?.C523_01?.E6350_01

                        def currentEQD_MEA_GWT = currentEQD.MEA?.findAll{it.E6311_01 == "WT" && it.C502_02?.E6313_01 == "G"}
                        if (currentEQD_MEA_GWT?.size() > 0) {
                            'ns0:GrossWeight'{
                                'ns1:Weight' currentEQD_MEA_GWT[0]?.C174_03?.E6314_02
                                'ns1:WeightUnit' extWeightUnitMap[currentEQD_MEA_GWT[0]?.C174_03?.E6411_01]
                            }
                        }

                        if (currentEQD?.EQD?.E8077_04 == "1") {
                            'ns0:IsSOC' "1"
                        } else {
                            'ns0:IsSOC' "0"
                        }

                        'ns0:OBDoor'{
                            'ns0:EmptyPickupDT'{
                                'ns1:GMT' ''
                                def vEmptyPickupDT_LocDT = currentEQD?.Group33_NAD?.DTM?.C507_01?.findAll {it.E2005_01 == "201"}?.E2380_02
                                if (vEmptyPickupDT_LocDT?.size() > 0) {
                                    'ns1:LocDT' util.convertXmlDateTime(vEmptyPickupDT_LocDT[0], 'YYYYMMDDhhmmss')
                                }
                            }

                            'ns0:DoorAppointment'{
                                'ns0:AppointmentDT'{
                                    def vCurrentEDQ_NAD_DTM03_204 = currentEQD?.Group33_NAD?.DTM?.C507_01?.findAll {it.E2005_01 == "201" && it.E2379_03 == "204"}?.E2380_02
                                    def vCurrentEDQ_NAD_DTM03_102 = currentEQD?.Group33_NAD?.DTM?.C507_01?.findAll {it.E2005_01 == "201" && it.E2379_03 == "102"}?.E2380_02
                                    if (vCurrentEDQ_NAD_DTM03_204?.size() > 0 && currentEQD?.Group33_NAD?.NAD?.E3035_01 == "SF") {
                                        'ns1:LocDT' util.convertXmlDateTime(vCurrentEDQ_NAD_DTM03_204[0], 'YYYYMMDDhhmmss')
                                    } else if (vCurrentEDQ_NAD_DTM03_102?.size() > 0 && currentEQD?.Group33_NAD?.NAD?.E3035_01 == "SF") {
                                        'ns1:LocDT' util.convertXmlDateTime(vCurrentEDQ_NAD_DTM03_102[0], 'YYYYMMDDhhmmss')
                                    }
                                }

                                if (currentEQD?.Group33_NAD?.Group34_CTA) {
                                    'ns1:Contact'{
                                        def vCurrentEQD_NAD_CTA = currentEQD?.Group33_NAD?.findAll {it.NAD?.E3035_01 == "SF"}?.Group34_CTA
                                        if (vCurrentEQD_NAD_CTA?.size() > 0) {
                                            int name_Cut = vCurrentEQD_NAD_CTA[0]?.CTA[0]?.C056_02?.E3412_02?.indexOf(",")
                                            if (name_Cut != -1) {
                                                'ns1:FirstName' util.substring(vCurrentEQD_NAD_CTA[0]?.CTA[0]?.C056_02?.E3412_02, 1, name_Cut)
                                                'ns1:LastName' util.substring(vCurrentEQD_NAD_CTA[0]?.CTA[0]?.C056_02?.E3412_02, name_Cut + 2, vCurrentEQD_NAD_CTA[0]?.CTA[0]?.C056_02?.E3412_02?.length())
                                            } else {
                                                'ns1:FirstName' vCurrentEQD_NAD_CTA[0]?.CTA[0]?.C056_02?.E3412_02
                                            }
                                        }
                                        def vCurrentEQD = vCurrentEQD_NAD_CTA[0]?.COM[0]?.C076_01?.find {it.E3155_02 == "TE"}?.E3148_01
                                        'ns1:ContactPhone'{
                                            if (vCurrentEQD) {
                                                'ns1:Number' util.substring(vCurrentEQD, 1, 30)
                                            }
                                        }
                                    }
                                }
                                 if (currentEQD?.EQN?.C523_01?.E6350_01) {
                                    'ns1:Quantity' currentEQD?.EQN?.C523_01?.E6350_01
                                }
                            }
                        }

                        if (currentTransaction?.TSR?.size() > 0) {
                            'ns0:Haulage'{
                                if (currentTransaction?.TSR[0]?.C536_01?.E4065_01 == "27" || currentTransaction?.TSR[0]?.C536_01?.E4065_01 == "28") {
                                    'ns1:OutBound' "C"
                                } else if (currentTransaction?.TSR[0]?.C536_01?.E4065_01 == "29" || currentTransaction?.TSR[0]?.C536_01?.E4065_01 == "30") {
                                    'ns1:OutBound' "M"
                                } else {
                                    'ns1:OutBound' ""
                                }
                                if (currentTransaction?.TSR[0]?.C536_01?.E4065_01 == "27" || currentTransaction?.TSR[0]?.C536_01?.E4065_01 == "29") {
                                    'ns1:InBound' "C"
                                } else if (currentTransaction?.TSR[0]?.C536_01?.E4065_01 == "28" || currentTransaction?.TSR[0]?.C536_01?.E4065_01 == "30") {
                                    'ns1:InBound' "M"
                                } else {
                                    'ns1:InBound' ""
                                }
                            }
                        }
                        'ns0:Route'{
                            'ns1:IntendedDT'{
                                def vCurrentDTM01_01_196 = currentTransaction?.DTM?.C507_01?.findAll {it.E2005_01 == "196"}?.E2380_02
                                def vCurrentDTM01_01_231 = currentTransaction?.DTM?.C507_01?.findAll {it.E2005_01 == "231"}?.E2380_02
                                def vCurrentDTM01_01_208 = currentTransaction?.DTM?.C507_01?.findAll {it.E2005_01 == "208"}?.E2380_02
                                def vCurrentDTM01_01_233 = currentTransaction?.DTM?.C507_01?.findAll {it.E2005_01 == "233"}?.E2380_02
                                'ns1:From'{
                                    if (vCurrentDTM01_01_196) {
                                        'ns1::LocDT' util.convertXmlDateTime(vCurrentDTM01_01_196, 'YYYYMMDDhhmmss')
                                    } else if (vCurrentDTM01_01_231) {
                                        'ns1::LocDT' util.convertXmlDateTime(vCurrentDTM01_01_231, 'YYYYMMDDhhmmss')
                                    }
                                }
                                'ns1:To'{
                                    if (vCurrentDTM01_01_208) {
                                        'ns1::LocDT' util.convertXmlDateTime(vCurrentDTM01_01_208, 'YYYYMMDDhhmmss')
                                    } else if (vCurrentDTM01_01_233) {
                                        'ns1::LocDT' util.convertXmlDateTime(vCurrentDTM01_01_233, 'YYYYMMDDhhmmss')
                                    }
                                }
                                if (vCurrentDTM01_01_196 || vCurrentDTM01_01_208) {
                                    'ns1:IntendedRangeIndicator' "S"
                                } else if (vCurrentDTM01_01_231 || vCurrentDTM01_01_233) {
                                    'ns1:IntendedRangeIndicator' "A"
                                }
                            }

                            def vLocationName_POR = currentTransaction?.Group7_TDT?.Group8_LOC?.findAll {it.LOC?.E3227_01 == "88"}[0]?.LOC
                            def vLocationName_FND = currentTransaction?.Group7_TDT?.Group8_LOC?.findAll {it.LOC?.E3227_01 == "7"}[0]?.LOC
                            def vLocationName_POL = currentTransaction?.Group7_TDT?.Group8_LOC?.findAll {it.LOC?.E3227_01 == "9"}[0]?.LOC
                            def vLocationName_POD = currentTransaction?.Group7_TDT?.Group8_LOC?.findAll {it.LOC?.E3227_01 == "11"}[0]?.LOC

                            'ns1:POR'{
                                if (vLocationName_POR?.size() > 0) {
                                    'ns1:LocationName' vLocationName_POR?.C517_02?.E3224_04
                                    'ns1:CityDetails'{
                                        'ns1:City' vLocationName_POR?.C517_02?.E3224_04
                                        if (vLocationName_POR?.C553_04?.find {it.E1131_02 == "162"}?.E3233_01) {
                                            'ns1:Country' vLocationName_POR?.C553_04?.find {it.E1131_02 == "162"}?.E3233_01
                                        }
                                        'ns1:LocationCode'{
                                            if (vLocationName_POR?.C517_02?.find {it.E3055_03 == "6"}?.E3225_01) {
                                                'ns1:UNLocationCode' util.substring(vLocationName_POR?.C517_02?.find {it.E3055_03 == "6"}?.E3225_01, 1, 5)
                                            }
                                        }
                                    }
                                }
                            }
                            'ns1:FND'{
                                if (vLocationName_FND?.size() > 0) {
                                    'ns1:LocationName' vLocationName_FND?.C517_02?.E3224_04
                                    'ns1:CityDetails'{
                                        'ns1:City' vLocationName_FND?.C517_02?.E3224_04
                                            'ns1:Country' vLocationName_FND?.C553_04?.find {it.E1131_02 == "162"}?.E3233_01
                                        'ns1:LocationCode'{
                                            if (vLocationName_FND?.C517_02?.find {it.E3055_03 == "6"}?.E3225_01) {
                                                'ns1:UNLocationCode' util.substring(vLocationName_FND?.C517_02?.find {it.E3055_03 == "6"}?.E3225_01, 1, 5)
                                            }
                                        }
                                    }
                                }
                            }
                            'ns1:FirstPOL'{
                                'ns1:Port'{
                                    if (vLocationName_POL?.size() > 0) {
                                        'ns1:PortName' vLocationName_POL[0]?.C517_02?.E3224_04
                                        'ns1:City' vLocationName_POL[0]?.C517_02?.E3224_04
                                        'ns1:LocationCode'{
                                            'ns1:UNLocationCode' util.substring(vLocationName_POL[0]?.C517_02?.E3225_01, 1, 5)
                                        }
                                        'ns1:Country' vLocationName_POL?.find {it.C517_02?.E3224_04 == "162"}?.C553_04?.E3233_01
                                    }

                                    def vTDT08_01 = currentTransaction?.Group7_TDT[0]?.TDT?.C222_08?.E8213_01
                                    def vTDT08_04 = currentTransaction?.Group7_TDT[0]?.TDT?.C222_08?.E8212_04
                                    def vVoyDir = currentTransaction?.Group7_TDT[0]?.TDT?.E8028_02
                                    def vVslCde = util.getConversionWithoutTP(MSG_TYPE_ID, DIR_ID, "VslCall", vTDT08_01, conn)
                                    def vInternalVslVoy = util.getConversionWithoutTP(MSG_TYPE_ID, DIR_ID,"VslCall", vTDT08_01?.padRight(20-vTDT08_01?.size(), " ") + vVoyDir, conn)
                                    def vVslName = ""

                                    if (util.getConversionWithoutTP(MSG_TYPE_ID, DIR_ID, "VesselCodeQualifier", vTDT08_04, conn)) {
                                        vVslName = util.getConversionWithoutTP(MSG_TYPE_ID, DIR_ID, "VesselCodeQualifier", vTDT08_04, conn)
                                    } else {
                                        vVslName = util?.substring(vTDT08_04, 1, 3)
                                    }

                                    if (vVslCde && vInternalVslVoy) {
                                        'ns1:LoadingPortVoyage' vVslCde + vInternalVslVoy
                                    } else if (vVslCde && vVoyDir) {
                                        'ns1:LoadingPortVoyage' vVslCde + vVoyDir
                                    } else {
                                        'ns1:LoadingPortVoyage' vVslName + vVoyDir
                                    }

                                    if (currentTransaction?.Group7_TDT?.last()?.TDT?.C222_08?.E8212_04) {
                                        'ns1:LoadingPortVesselName' currentTransaction?.Group7_TDT?.last()?.TDT?.C222_08?.E8212_04
                                    } else {
                                        'ns1:LoadingPortVesselName' 'TBA'
                                    }
                                }

                            }
                            'ns1:LastPOD'{
                                'ns1:Port'{
                                    'ns1:PortName' util.substring(vLocationName_POD[0]?.C517_02?.E3224_04, 1, 70)
                                    'ns1:City' util.substring(vLocationName_POD[0]?.C517_02?.E3224_04, 1,60)
                                    'ns1:LocationCode'{
                                        'ns1:UNLocationCode' util.substring(vLocationName_POD[0]?.C517_02?.E3225_01, 1, 5)
                                    }
                                    'ns1:Country' vLocationName_POD?.find {it.C517_02?.E3224_04 == "162"}?.C553_04?.E3233_01
                                }
                            }
                            'ns1:CargoNeededAtOriginDT'{
                                def vCargoNeededAtOriginDT_Loc = currentTransaction?.Group7_TDT?.Group8_LOC?.findAll {it.LOC?.E3227_01 == "88"}?.DTM?.C507_01?.E2380_02
                                if (vCargoNeededAtOriginDT_Loc?.size() > 0) {
                                    'ns1:LocDT' util.convertXmlDateTime(vCargoNeededAtOriginDT_Loc[0], 'YYYYMMDDhhmm00')
                                } else {
                                    'ns1:LocDT' util.convertXmlDateTime(vCargoNeededAtOriginDT_Loc[0], 'YYYYHHDD000000')
                                }
                            }
                            'ns1:CargoAvailableAtDestDT'{
                                def vCargoAvailableAtDestDT_Loc = currentTransaction?.Group7_TDT?.Group8_LOC?.findAll {it.LOC?.E3227_01 == "7"}?.DTM
                                if (vCargoAvailableAtDestDT_Loc.size() > 0) {
                                    'ns1:LocDT' util.convertXmlDateTime(vCargoAvailableAtDestDT_Loc[0]?.C507_01?.find {it.E2005_01 == "17"}?.E2380_02, 'YYYYHHDD000000')
                                }
                            }
                            'ns1:LoadingPortLatestDepDT'{
                                def vLoadingPortLatestDepDT_Loc = currentTransaction?.Group7_TDT?.Group8_LOC?.findAll {it.LOC?.E3227_01 == "9"}?.DTM
                                if (vLoadingPortLatestDepDT_Loc?.size() > 0) {
                                    def vDTM01_02 = vLoadingPortLatestDepDT_Loc[0]?.C507_01?.find {it?.E2005_01 == "133"}?.E2380_02
                                    if ( vDTM01_02?.length() == 12) {
                                        'ns1:LocDT' util.convertXmlDateTime(vDTM01_02, 'YYYYMMDDhhmm00')
                                    } else if (vDTM01_02?.length() == 8) {
                                        'ns1:LocDT' util.convertXmlDateTime(vDTM01_02, 'YYYYMMDD000000')
                                    } else if(vDTM01_02?.length() == 14) {
                                        'ns1:LocDT' util.convertXmlDateTime(vDTM01_02, 'YYYYMMDDhhmm00')
                                    }
                                }
                                'ns1:LocDT' ''
                            }
                            'ns1:DischargePortLatestArrDT'{
                                'ns1:LocDT' ''
                            }
                            'RequestDepartureDT'{
                                'cs:GMT' ''
                                'cs:LocDT' ''
                            }
                            'LatestArrivalDT'{
                                'cs:GMT' ''
                                'cs:LocDT' ''
                            }
                            'IsFromSailingSchedule' ''
                            'OceanLeg'{
                                'LegSeq' ''
                                'POL'{
                                    'cs:Port'{
                                        'cs:PortName' ''
                                        'cs:PortCode' ''
                                        'cs:City' ''
                                        'cs:County' ''
                                        'cs:State' ''
                                        'cs:LocationCode'{
                                            'cs:MutuallyDefinedCode' ''
                                            'cs:UNLocationCode' ''
                                            'cs:SchedKDType' ''
                                            'cs:SchedKDCode' ''
                                        }
                                        'cs:Country' ''
                                        'cs:CSPortID' ''
                                        'cs:CSCountryCode' ''
                                    }
                                    'cs:Facility'{
                                        'cs:FacilityCode' ''
                                        'cs:FacilityName' ''
                                    }
                                }
                                'POD'{
                                    'cs:Port'{
                                        'cs:PortName' ''
                                        'cs:PortCode' ''
                                        'cs:City' ''
                                        'cs:County' ''
                                        'cs:State' ''
                                        'cs:LocationCode'{
                                            'cs:MutuallyDefinedCode' ''
                                            'cs:UNLocationCode' ''
                                            'cs:SchedKDType' ''
                                            'cs:SchedKDCode' ''
                                        }
                                        'cs:Country' ''
                                        'cs:CSPortID' ''
                                        'cs:CSCountryCode' ''
                                    }
                                    'cs:Facility'{
                                        'cs:FacilityCode' ''
                                        'cs:FacilityName' ''
                                    }
                                }
                                'SVVD'{
                                    'cs:Service' ''
                                    'cs:Vessel' ''
                                    'cs:VesselName' ''
                                    'cs:Voyage' ''
                                    'cs:Direction' ''
                                    'cs:LloydsNumber' ''
                                    'cs:CallSign' ''
                                    'ExternalVoyageNumber' ''
                                }
                                'ETD'{
                                    'cs:GMT' ''
                                    'cs:LocDT' ''
                                }
                                'ETA'{
                                    'cs:GMT' ''
                                    'cs:LocDT' ''
                                }
                            }
                        }
                        'Remarks' ''
                        'Volume'{
                            'cs:Volume' ''
                            'cs:VolumeUnit' ''
                        }
                        'VerifiedGrossMass'{
                            'ReferenceNumber' ''
                            'VGMContainerNumber' ''
                            'Weight'{
                                'cs:Weight' ''
                                'cs:WeightUnit' ''
                            }
                            'WeightedMethod' ''
                            'WeightedDate'{
                                'cs:GMT' ''
                                'cs:LocDT' ''
                            }
                            'ResponsibleParty' ''
                            'AuthorizedOfficial' ''
                            'ReceivedDate'{
                                'cs:GMT' ''
                                'cs:LocDT' ''
                            }
                            'ReceivedFromId' ''
                            'WeightPartyContactInfo'{
                                'cs:FirstName' ''
                                'cs:LastName' ''
                                'cs:ContactPhone'{
                                    'cs:CountryCode' ''
                                    'cs:AreaCode' ''
                                    'cs:Number' ''
                                }
                                'cs:ContactFax'{
                                    'cs:CountryCode' ''
                                    'cs:AreaCode' ''
                                    'cs:Number' ''
                                }
                                'cs:ContactEmailAddress' ''
                            }
                        }
                        'BRHaulage'{
                            'OutBound' ''
                            'InBound' ''
                        }
                    }

                }
            }
        }
    }

    public void buildBizKey(MarkupBuilder bizKeyXml, def output, def inputEDI, def txnErrorKeys){
        def bookingRequest = new XmlSlurper().parseText(output)
        EDI_CS_IFTMBF input = inputEDI

        input.Group_UNH?.eachWithIndex { current_Body, current_BodyIndex ->
            bizKeyXml.'ns0:Transaction'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
                'ns0:ControlNumberInfo' {
                    'ns0:Interchange' input.UNB?.E0020_05
                    'ns0:Group' input.UNG?.E0048_05
                    'ns0:Transaction' current_Body.UNH?.E0062_01
                }
                // TransactionRefNumber
                if (util.notEmpty(current_Body.BGM?.C106_02?.E1004_01)){
                    'ns0:BizKey' {
                        'ns0:Type' 'TransactionRefNumber'
                        'ns0:Value' current_Body.BGM?.C106_02?.E1004_01	}
                }
                // Message Reference Number (Reference Number)
                'ns0:BizKey' {
                    'ns0:Type' 'MSRN'
                    'ns0:Value' MSG_REQ_ID	}

                // If no error in pre-validation
                if (txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.size() == 0) {
                    // insert Biz-key by generated CS2XML which is common across different BR msg format
                    def bizKey = brUtil.buildBizKey(bizKeyXml, bookingRequest.Request[current_BodyIndex], current_BodyIndex, txnErrorKeys[current_BodyIndex], TP_ID, conn)
                    bizKey.each{ currentBizKeyMap ->
                        currentBizKeyMap.each{ key, value ->
                            String val = value
                            if ( val==null || val.trim()=='') {
                                //loop next item
                                return
                            }
                            'ns0:BizKey'{
                                'ns0:Type' key
                                'ns0:Value' value
                            }
                        }
                    }
                    if (util.notEmpty(bookingRequest.Request[current_BodyIndex].GeneralInformation?.SCAC)){
                        'ns0:CarrierId' util.getCarrierID(bookingRequest.Request[current_BodyIndex].GeneralInformation?.SCAC.text(), conn)
                    }
                }// End-if-no-error-in-prevalidation

                'ns0:CTEventTypeId'

                // map pre-validation error
                String errMsg = ''
                txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.eachWithIndex{ stringError, errorIndex ->
                    errMsg = errMsg +  ' [Error ' + (errorIndex + 1) + '] : ' + stringError
                    if (errorIndex != 0)
                        errMsg = errMsg + '\n'
                }

                if (txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.size() > 0) {
                    'ns1:AppErrorReport'('xmlns:ns1': 'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
                        'ns1:Status' 'E'
                        'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'    //max length is 20, exceed will missing error bizkey
                        'ns1:Msg' errMsg.take(1000)
                        'ns1:Severity' '5'
                    }
                }
            }// End-Transaction
        }// End-Loop-input-Group_UNH
    }

}
