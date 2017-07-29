package cs.b2b.mapping.scripts


import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.ContainerMovement

import cs.b2b.core.mapping.bean.ct.OceanLeg

import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection

import java.text.SimpleDateFormat

/**
 * Created by XIAOTR on 6/11/2017.
 */
class CUS_CS2CTXML_OLLUIF_OLLEDICARR {  cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
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
    def headerDateTime = null
    def headerReceiveID = null


    public void generateBody(Body current_Body, MarkupBuilder outXml) {


        def UnlocationConvertionMap = ['CNFUG':'CNFQG','CNTAG':'CNTAC']

        def eventDateTime = null

        outXml.'Record-Detail' {
            if(current_Body?.Event?.EventDT?.LocDT){
                eventDateTime = current_Body?.Event?.EventDT?.LocDT
            }

            OceanLeg lastOceanLeg = null
            OceanLeg firstOceanLeg = null
            if(current_Body?.Route?.OceanLeg){
                lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
                firstOceanLeg = current_Body?.Route?.OceanLeg[0]
            }

            if(current_Body?.GeneralInformation?.SCAC){
                'Field-Detail_Carrier'  util.substring(current_Body?.GeneralInformation?.SCAC,1,4)
            }

            'Field-Detail_Tab' '\t'
            def containerInfo = null

            if(util.isNotEmpty(current_Body?.Container?.ContainerNumber) || util.isNotEmpty(current_Body?.Container?.ContainerCheckDigit)){
                containerInfo = (current_Body?.Container?.ContainerNumber ? current_Body?.Container?.ContainerNumber : '')+ (current_Body?.Container?.ContainerCheckDigit ? current_Body?.Container?.ContainerCheckDigit : '' )

                'Field-Detail_Container' util.substring(containerInfo,1,15)
            }

            'Field-Detail_Tab1' '\t'

            def containerStatus = null
            if(current_Body?.Container?.ContainerStatus){
                containerStatus = current_Body?.Container?.ContainerStatus
            }
            if(containerStatus?.toUpperCase() =="EMPTY"){
                'Field-Detail_CT_Status' 'E'
            }else if(containerStatus?.toUpperCase() =="LADEN"){
                'Field-Detail_CT_Status' 'L'
            }else {
                'Field-Detail_CT_Status' util.substring(containerStatus,1,5)
            }

            'Field-Detail_Tab2' '\t'
            if(current_Body?.BLGeneralInfo[0]?.BLNumber){
                'Field-Detail_OBL'  util.substring(current_Body?.BLGeneralInfo[0]?.BLNumber,1,18)
            }

            'Field-Detail_Tab3' '\t'
            if(current_Body?.Event?.CarrEventCode){
                'Field-Detail_Status'  util.substring(current_Body?.Event?.CarrEventCode,1,15)
            }

            'Field-Detail_Tab4' '\t'
            if(eventDateTime){
                'Field-Detail_Status_Date' util.convertDateTime(eventDateTime, xmlDateTimeFormat, yyyyMMdd)
            }

            'Field-Detail_Tab5' '\t'
            def eventUnlocation = null
            def queryUnlocationEvent = ctUtil.getCS2MasterCitybySKDcodeandType(current_Body?.Event?.Location?.LocationCode?.SchedKDType,current_Body?.Event?.Location?.LocationCode?.SchedKDCode,conn)
            if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                eventUnlocation = current_Body?.Event?.Location?.LocationCode?.UNLocationCode
            }

            if(UnlocationConvertionMap.containsKey(eventUnlocation)){
                'Field-Detail_Status_Location'  UnlocationConvertionMap.get(eventUnlocation)
            }else if(eventUnlocation){
                'Field-Detail_Status_Location' util.substring(eventUnlocation,1,35)
            }else if(queryUnlocationEvent){
                'Field-Detail_Status_Location'  queryUnlocationEvent
            }else if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode){
                'Field-Detail_Status_Location' util.substring(current_Body?.Event?.Location?.LocationCode?.SchedKDCode,1,35)
            }

            'Field-Detail_Tab6' '\t'
            if(firstOceanLeg?.SVVD?.Loading?.VesselName){
                'Field-Detail_Vessel'   util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName?.trim(),1,30)
            }else if(firstOceanLeg?.SVVD?.Discharge?.VesselName){
                'Field-Detail_Vessel'   util.substring(firstOceanLeg?.SVVD?.Discharge?.VesselName?.trim(),1,30)
            }

            'Field-Detail_Tab7' '\t'
            if(firstOceanLeg?.SVVD?.Loading?.Voyage){
                'Field-Detail_Voyage'   util.substring(firstOceanLeg?.SVVD?.Loading?.Voyage,1,20)
            }else if(firstOceanLeg?.SVVD?.Discharge?.Voyage){
                'Field-Detail_Voyage'   util.substring(firstOceanLeg?.SVVD?.Discharge?.Voyage,1,20)
            }
            'Field-Detail_Tab8' '\t'
            if(current_Body?.Event?.Location?.CityDetails?.Country){
                'Field-Detail_Country' util.substring(current_Body?.Event?.Location?.CityDetails?.Country,1,5)
            }

            'Field-Detail_Tab9' '\t'
            def queryUnlocationCdeOri =  ctUtil.getCS2MasterCitybySKDcodeandType(current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDType,current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode,conn)
            def originInfo = null

            if(util.isNotEmpty(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) && UnlocationConvertionMap.containsKey(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode) ){
                originInfo =  UnlocationConvertionMap.get(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode)
            }else if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.MutuallyDefinedCode){
                originInfo = current_Body?.Route?.POR?.CityDetails?.LocationCode?.MutuallyDefinedCode
            }else if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode){
                originInfo = current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode
            }else if(queryUnlocationCdeOri){
                originInfo = queryUnlocationCdeOri
            }else if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode){
                originInfo = current_Body?.Route?.POR?.CityDetails?.LocationCode?.SchedKDCode
            }
            if(originInfo){
                'Field-Detail_Origin' util.substring(originInfo,1,5)
            }

            'Field-Detail_Tab10' '\t'

            if(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator =='E'}?.LocDT){
                'Field-Detail_Discharge' util.convertDateTime(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator =='E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
            }

            'Field-Detail_Tab11' '\t'

            if(firstOceanLeg?.POL?.DepartureDT[0]?.LocDT){
                    'Field-Detail_Sailing' util.convertDateTime(firstOceanLeg?.POL?.DepartureDT[0]?.LocDT, xmlDateTimeFormat, yyyyMMdd)

            }


            'Field-Detail_Tab12' '\t'
            def queryUnlocationPol = ctUtil.getCS2MasterCitybySKDcodeandType(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDType,current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode,conn)
            def polInfo = null
            if(util.isNotEmpty(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) && UnlocationConvertionMap.containsKey(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode) ){
                polInfo =  UnlocationConvertionMap.get(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode)
            }else if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.MutuallyDefinedCode){
                polInfo =  current_Body?.Route?.FirstPOL?.Port?.LocationCode?.MutuallyDefinedCode
            }else if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode){
                polInfo = current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode
            }else if(queryUnlocationPol){
                polInfo =  queryUnlocationPol
            }else if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode){
                polInfo = current_Body?.Route?.FirstPOL?.Port?.LocationCode?.SchedKDCode
            }

            if(polInfo){
                'Field-Detail_POL'  util.substring(polInfo,1,5)
            }

            'Field-Detail_Tab13' '\t'
            if(headerDateTime){
                'Field-Detail_ETA1' util.convertDateTime(headerDateTime,xmlDateTimeFormat, yyyyMMdd)
            }


            'Field-Detail_Tab14' '\t'
            'Field-Detail_POD1' ''
            'Field-Detail_Tab15' '\t'
            if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator =='A'}?.LocDT){
                'Field-Detail_ETD1' util.convertDateTime(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator =='A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
            }else if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator =='E'}?.LocDT){
                'Field-Detail_ETD1' util.convertDateTime(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator =='E'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
            }

            'Field-Detail_Tab16' '\t'

            if(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator =='A'}?.LocDT){
                'Field-Detail_ETA2' util.convertDateTime(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator =='A'}?.LocDT, xmlDateTimeFormat, yyyyMMdd)
            }
            'Field-Detail_Tab17' '\t'

            def queryUnlocationPod = ctUtil.getCS2MasterCitybySKDcodeandType(current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDType,current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode,conn)
            def podInfo = null

            if(util.isNotEmpty(current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) && UnlocationConvertionMap.containsKey(current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode) ){
                podInfo =  UnlocationConvertionMap.get(current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode)
            }else if(current_Body?.Route?.LastPOD?.Port?.LocationCode?.MutuallyDefinedCode){
                podInfo =  current_Body?.Route?.LastPOD?.Port?.LocationCode?.MutuallyDefinedCode
            }else if(current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode){
                podInfo =   current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode
            }else if(queryUnlocationPod){
                podInfo =  queryUnlocationPod
            }else if(current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode){
                podInfo = current_Body?.Route?.LastPOD?.Port?.LocationCode?.SchedKDCode
            }

            if(podInfo){
                'Field-Detail_POD2' util.substring(podInfo,1,5)
            }

            'Field-Detail_Tab18' '\t'
            'Field-Detail_ETD2' ''
            'Field-Detail_Tab19' '\t'
            if(current_Body?.Route?.FND?.ArrivalDT?.size()>1){
                if(current_Body?.Route?.FND?.ArrivalDT[-1]?.LocDT){
                    'Field-Detail_FND_ETA' util.convertDateTime(current_Body?.Route?.FND?.ArrivalDT[-1]?.LocDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                }
            }else{
                if(current_Body?.Route?.FND?.ArrivalDT?.LocDT){
                    'Field-Detail_FND_ETA' util.convertDateTime(current_Body?.Route?.FND?.ArrivalDT[0]?.LocDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)
                }
            }


            'Field-Detail_Tab20' '\t'
            if(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode){
                if( UnlocationConvertionMap.containsKey(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode)){
                    'Field-Detail_FND' UnlocationConvertionMap.get(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode)
                }else{
                    'Field-Detail_FND' util.substring(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode,1,8)
                }

            }

            'Field-Detail_Tab21' '\t'
            'Field-Detail_Customer' util.substring(headerReceiveID,1,30)
            'Field-Detail_Tab22'

            if(current_Body?.ExternalReference?.find{it?.CSReferenceType?.toUpperCase()=='TN'}?.ReferenceNumber){

                'Field-Detail_IT_No' util.substring(current_Body?.ExternalReference?.find{it?.CSReferenceType?.toUpperCase()=='TN'}?.ReferenceNumber,1,20)
            }

            'Field-Detail_Tab23' '\t'
            if(eventDateTime){
                'Field-Detail_Status_Time'util.convertDateTime(eventDateTime, xmlDateTimeFormat, HHmm)
            }else {
                'Field-Detail_Status_Time' '0000'
            }

            'Field-Detail_Tab24' '\t'
            if(headerDateTime){
                'Field-Detail_TETA1' util.convertDateTime(headerDateTime, xmlDateTimeFormat, HHmm)
            }

            'Field-Detail_Tab25' '\t'
            if(current_Body?.Container?.Seal[0]?.SealNumber){
                'Field-Detail_SN' util.substring(current_Body?.Container?.Seal[0]?.SealNumber?.replace("*",""),1,30)
            }

            'Field-Detail_Tab26' '\t'
            if(current_Body?.BookingGeneralInfo[0]?.CarrierBookingNumber){
                'Field-Detail_BKN' util.substring(current_Body?.BookingGeneralInfo[0]?.CarrierBookingNumber,1,30)
            }


            'Field-Detail_Tab27' '\t'
            'Field-Detail_Transship_event' ''
            'Field-Detail_Tab28' '\t'
            if(current_Body?.TransactionInformation?.InterchangeTransactionID){
                'Field-Detail_Control_Ref' util.substring(current_Body?.TransactionInformation?.InterchangeTransactionID?.padLeft(9,'0'),1,14)
            }

            'Field-Detail_Tab29' '\t'
            if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber){
                'Field-Detail_Lloyds_Code' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber,1,12)
            }else if(firstOceanLeg?.SVVD?.Discharge?.LloydsNumber){
                'Field-Detail_Lloyds_Code' util.substring(firstOceanLeg?.SVVD?.Discharge?.LloydsNumber,1,12)
            }

            'Field-Detail_Tab30' '\t'
            'Field-Detail_EDI_TZ_Diff' ''
            'Field-Detail_Tab31' '\t'
            'Field-Detail_Event_TZ_Diff' ''
            'Field-Detail_Tab32' '\t'
            def ExtReferenceList = ['CUS']

            current_Body?.ExternalReference?.find{ExtReferenceList.contains(it?.CSReferenceType)}?.each{current_Ext->
                'Field-Detail_ATB_No' util.substring(current_Ext?.ReferenceNumber,1,30)
            }

            'Field-Detail_Tab33' '\t'
            if(current_Body?.Container?.Seal[1]?.SealNumber){
                'Field-Detail_Seal2'  util.substring(current_Body?.Container?.Seal[1]?.SealNumber,1,30)
            }

            'Field-Detail_Tab34' '\t'
            if(current_Body?.Container?.Seal[2]?.SealNumber){
                'Field-Detail_Seal3'  util.substring(current_Body?.Container?.Seal[2]?.SealNumber,1,30)
            }
            'Field-Detail_Tab35' '\t'

            if(current_Body?.Container?.Seal[3]?.SealNumber){
                'Field-Detail_Seal4' util.substring(current_Body?.Container?.Seal[3]?.SealNumber,1,30)
            }
            'Field-Detail_Tab36' '\t'
            if(current_Body?.Container?.Seal[4]?.SealNumber){
                'Field-Detail_Seal5'  util.substring(current_Body?.Container?.Seal[4]?.SealNumber,1,30)
            }

            'Field-Detail_Tab37' '\t'
            if(current_Body?.Container?.Seal[5]?.SealNumber){
                'Field-Detail_Seal6' util.substring(current_Body?.Container?.Seal[5]?.SealNumber,1,30)
            }
            'Field-Detail_Tab38' '\t'
            if(current_Body?.Container?.Seal[6]?.SealNumber){
                'Field-Detail_Seal7'  util.substring(current_Body?.Container?.Seal[6]?.SealNumber,1,30)
            }
            'Field-Detail_Tab39' '\t'

            if(current_Body?.Container?.Seal[7]?.SealNumber){
                'Field-Detail_Seal8'  util.substring(current_Body?.Container?.Seal[7]?.SealNumber,1,30)
            }
            'Field-Detail_Tab40' '\t'
            if(current_Body?.Container?.Seal[8]?.SealNumber){
                'Field-Detail_Seal9'  util.substring(current_Body?.Container?.Seal[8]?.SealNumber,1,30)
            }
            'Field-Detail_Tab41' '\t'
            if(current_Body?.Container?.Seal[9]?.SealNumber){
                'Field-Detail_Seal10'  util.substring(current_Body?.Container?.Seal[9]?.SealNumber,1,30)
            }

            'Field-Detail_Tab42' '\t'
            if(current_Body?.ExternalReference?.find{it?.CSReferenceType =='RP'}?.ReferenceNumber){
                'Field-Detail_Rail_PickUp_No'   util.substring(current_Body?.ExternalReference?.find{it?.CSReferenceType =='RP'}?.ReferenceNumber,1,30)
            }

            'Field-Detail_Tab43' '\t'
            'Field-Detail_SI_Reference_Number' ''
            'Field-Detail_Tab44' '\t'
            def quertyCityName = ctUtil.getCS2MasterCitybyUnlocationCde(current_Body?.Event?.Location?.LocationCode?.UNLocationCode,conn)
            def eventInfo  = null

            if(current_Body?.Event?.Location?.LocationName){
                eventInfo = current_Body?.Event?.Location?.LocationName
            }else if(current_Body?.Event?.Location?.CityDetails?.City){
                eventInfo =  current_Body?.Event?.Location?.CityDetails?.City
            }else if (quertyCityName){
                eventInfo =  quertyCityName
            }
            if(eventInfo)
            {
                'Field-Detail_Event_Location_Name' util.substring(eventInfo,1,60)
            }


            'Field-Detail_Tab45' '\t'

            def containerSizeType = null
            if(current_Body?.Container?.CarrCntrSizeType){
                containerSizeType = util.getConversion('OLL','CT','O','ContainerSizeType',current_Body?.Container?.CarrCntrSizeType,conn)
                if(containerSizeType){
                    'Field-Detail_Container_Size_Type' util.substring(containerSizeType,1,5)
                }else {
                    'Field-Detail_Container_Size_Type' current_Body?.Container?.CarrCntrSizeType
                }
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

        def cs2xml = prepHandler(inputXmlBody)
        XmlBeanParser parser = new XmlBeanParser()
        ContainerMovement ct = parser.xmlParser(cs2xml, ContainerMovement.class)

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
        def T315 = outXml.createNode('root')
        def bizKeyRoot = bizKeyXml.createNode('root')
        def csuploadRoot = csuploadXml.createNode('root')
        //csupload root node name must be 'root', or will cause ORA error.

        //BeginWorkFlow
        TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
        def outputBodyCount = 0
        currentSystemDt = new Date()


        //def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
        def headerMsgDT = util.convertDateTime(ct.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
        headerDateTime = ct.Header.MsgDT.LocDT
        if(ct.Header.ReceiverID){
            headerReceiveID =ct.Header.ReceiverID
        }



        //long ediIsaCtrlNumber = ctrlNos[0]
        //long ediGroupCtrlNum = ctrlNos[1]
        def txnErrorKeys = []

        //duplication -- CT special logic
		//Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
        //checked the bizkey unique




		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		//List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)
        //start body looping
        def uniqueLKP =[] //insert unique bizkey
        blDupBodies.eachWithIndex { current_Body, current_BodyIndex ->

            def eventCS2Cde = current_Body.Event.CS1Event
            //def eventExtCde = util.getConversion('OLL-EDICARR', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

            //prep checking
            List<Map<String, String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde)
           // Map<String,String> pospVerifyFlags= new HashMap<String,String>()
            if (errorKeyList.size() == 0) {
                //pass validateBeforeExecution
                //main mapping
                generateBody(current_Body,outXml)
                outputBodyCount++
            }
            //posp checking
            //List<Map<String, String>> pospErrorKeyList = pospValidation(eventCS2Cde,errorKeyList,pospVerifyFlags)

            ctUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
           // ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, '', TP_ID, conn)

            if(!uniqueLKP.contains(current_Body?.TransactionInformation?.MessageID)){
                ctUtil.buildBizKeyforUIF(bizKeyXml,current_Body,current_BodyIndex,ct.Header,errorKeyList,headerMsgDT,TP_ID,conn)
                uniqueLKP.add(current_Body?.TransactionInformation?.MessageID)
            }


            //ctUtil.buildBizKeyforUIF(bizKeyXml,current_Body,current_BodyIndex,ct.Header,errorKeyList,headerMsgDT,TP_ID,conn)
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
            result = writer?.toString();
        }

        writer.close();
        csuploadWriter.close()
        bizKeyWriter.close()


        result = translateUIF(translateDetails(result),ouputHeader())

        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss")
        def filename = ct.Body[0]?.GeneralInformation?.SCAC+ f.format(new GregorianCalendar().time)+"_"+MSG_REQ_ID+'.txt'

        //1, set output file name
        cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, "PROMOTE_TransportOutputFileName",filename);

        //2, set transport property
        cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, "TRANSPORT_PROPERTY_KEY1", "StatusUpdateKey");
        cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, "TRANSPORT_PROPERTY_VAL1", "UIF");

        return result;
    }
    // check bizkey unque

    public String ouputHeader(){
        String header = null

        def title = ['Carrier','Container','CT_Status','OBL','Status','Status_Date','Status_Location','Vessel','Voyage','Country','Origin','Discharge','Sailing','POL','ETA1','POD1','ETD1','ETA2','POD2','ETD2','FND_ETA','FND','Customer','IT#','Status_Time','TETA1','SN','BKN','Transship_event','Control_Ref','Lloyds_Code','EDI_TZ_Diff','Event_TZ_Diff','ATB_No','Seal2','Seal3','Seal4','Seal5','Seal6','Seal7','Seal8','Seal9','Seal10','Rail_PickUp_No','SVR','Event_Location_Name','Container_Size_Type']
        title.each {
            header = (header==null ? '' : header)+it+'\t'
        }
        header = header.substring(0,header.lastIndexOf("\t"))


        return header

    }

    public String translateUIF(List details,String header){
        String txnDetails = null
        def outputuif = null


        details?.eachWithIndex {current_txn ,index->

            current_txn?.each{ current_detail->
                txnDetails = (txnDetails==null ? '' : txnDetails)+(current_detail ? current_detail : '')+'\t'
            }

            txnDetails = (txnDetails == null ? '' : txnDetails.substring(0,txnDetails.lastIndexOf("\t"))) + '\n'

        }
        outputuif= (outputuif==null ? '' : outputuif)+header+'\n'+txnDetails
        return outputuif
    }

    public List translateDetails(String XML){
        def root = new XmlParser().parseText(XML)
        def childNodeName = null
        def childvalue = null
        def detailcol = ['Carrier','Container','CT_Status','OBL','Status','Status_Date','Status_Location','Vessel','Voyage','Country','Origin','Discharge','Sailing','POL','ETA1','POD1','ETD1','ETA2','POD2','ETD2','FND_ETA','FND','Customer','IT_No','Status_Time','TETA1','SN','BKN','Transship_event','Control_Ref','Lloyds_Code','EDI_TZ_Diff','Event_TZ_Diff','ATB_No','Seal2','Seal3','Seal4','Seal5','Seal6','Seal7','Seal8','Seal9','Seal10','Rail_PickUp_No','SI_Reference_Number','Event_Location_Name','Container_Size_Type']

        List<List<String>>  outputDetailList = new ArrayList<>()


        root.eachWithIndex{ current_root, rootIndex ->
            List<String> details = new ArrayList<>()
            detailcol.each { current_title ->
                childNodeName = 'Field-Detail_' + current_title
                childvalue = root?.'Record-Detail'[rootIndex]?."${childNodeName}"?.text()
                details.add(childvalue)
            }
            outputDetailList.add(details)
        }

        return outputDetailList


    }

    //translate as cs2xml
    public String prepHandler(String inputXML){
        def cs2xml = inputXML
        def end =null
        def begin =  cs2xml.indexOf("<ns0:ContainerMovement")
        if(cs2xml.contains("</Reordered-Transaction>")){
            end = cs2xml.indexOf("</Reordered-Transaction>")

        }else {
            end = cs2xml.length()
        }
        cs2xml =  cs2xml.substring(begin,end)

        def replaceList =['ArrivalDT-FND']
        def replaceCharMap = ['ArrivalDT-FND':'ArrivalDT']
        replaceList.each {current_char->
            cs2xml=  cs2xml.replace(current_char,replaceCharMap.get(current_char))
        }

        return cs2xml

    }

    public List<Map<String, String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde) {
        List<Map<String, String>> errorKeyList = new ArrayList<Map<String, String>>();

        return errorKeyList;
    }

    public boolean pospValidation() {

    }



}






