/**
 *
 */
package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.common.xmlvalidation.ValidateXML
import cs.b2b.core.mapping.bean.edi.xml.br.inttra.Message
import cs.b2b.core.mapping.bean.edi.xml.br.inttra.MessageBody
import cs.b2b.core.mapping.util.XmlBeanParser

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat


import org.junit.Before;

/**
 * @author LAIKU
 * TP_List: 
 */
class CUS_BRXML_CS2BRXML_ELITE {
    cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil()
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

    Map<String, String> extWeightUnitMap = ["KGM": "KGS", "LBS": "LBS"]
    Map<String, String> extActionTypeMap = ["Original": "NEW", "Amendment": "UPD", "Cancellation": "CANCEL"]
    Map<String, String> extTemperatureUnitMap = ["CEL": "C", "FAH": "F"]
    Map<String, String> extPartyType = ["NotifyParty2": "ANP", "BookingParty": "BPT", "Carrier": "CAR", "Consignee": "CGN", "FreightForwarder": "FWD", "NotifyParty": "NPT", "Shipper": "SHP"]

    String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
        /**
         * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
         */
        inputXmlBody = util.removeBOM(inputXmlBody)

        /**
         * Part II: get app mapping runtime parameters
         */

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
        //Parse the xmlBody to JavaBean
        XmlBeanParser parser = new XmlBeanParser()
        //Use Inttra Schema (by Java bean)
        Message msg = parser.xmlParser(inputXmlBody, Message.class)

        def writer = new StringWriter()
        def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
        outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

        def bizKeyWriter = new StringWriter()
        def bizKeyXml = new MarkupBuilder(bizKeyWriter)
        bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

        /**
         * Part IV: mapping script start from here
         */
        //create Start root node to Xml
        def namespace = ['xmlns:ns0': 'http://www.cargosmart.com/bookingrequest',
                         'xmlns:ns1': 'http://www.cargosmart.com/common']
        def BookingRequest = outXml.createNode('ns0:BookingRequest', namespace)
        def bizKeyRoot = bizKeyXml.createNode('root')

        //Begin work flow
        TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
        currentSystemDt = new Date()

        def outputBodyCount = 0
        def headerGenerated = false
        def txnErrorKeys = []

        def bodies = msg.MessageBody
        def controlNum = msg.Header?.DocumentIdentifier

        bodies.eachWithIndex {
            _currentBody, _currentIndex ->

                List preErrors = prepValidation(_currentBody, _currentIndex, conn)

                // map Header once only
                if (!headerGenerated) {
                    //generateHeader BR XML Header
                    brUtil.generateHeader(outXml, _currentBody, outputBodyCount, currentSystemDt, DIR_ID, MSG_TYPE_ID, TP_ID, MSG_REQ_ID, controlNum)
                    headerGenerated = true
                }
                //Start mapping
                if (preErrors.isEmpty()) {
                    generateBody(msg, _currentBody,currentSystemDt, outXml)

                } else {
                    outXml.'Body' {}
                }

                outputBodyCount++

                String temp = ''
                temp = writer?.toString() + "</ns0:BookingRequest>"
                brUtil.buildBizKeyXML(bizKeyXml, temp, _currentIndex, MSG_REQ_ID, 'action', preErrors, TP_ID, msg.Header?.DocumentIdentifier.toString(), conn)
                txnErrorKeys.add(preErrors)
        }

        //End work flow
        outXml.nodeCompleted(null, BookingRequest)
        //End work flow of BizKey
        bizKeyXml.nodeCompleted(null, bizKeyRoot)

        //Generate BR CS2 XML Output
        String cs2brxmlResult = '';
        cs2brxmlResult = writer?.toString();
        cs2brxmlResult = util.cleanXml(cs2brxmlResult)

        //PospValidation
        List postErrors = postValidation(cs2brxmlResult, msg, txnErrorKeys)
        bodies.eachWithIndex { current_Body, current_BodyIndex ->
            if (postErrors[current_BodyIndex])
                txnErrorKeys[current_BodyIndex].add(postErrors[current_BodyIndex]);
        }


        writer.close();
        bizKeyWriter.close()

        brUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter)

        def BizKey = new XmlSlurper().parseText(bizKeyWriter?.toString())
        boolean hasGoodBR = false
        BizKey.children().each { currentTransaction ->
            if (!(util.notEmpty(currentTransaction.AppErrorReport))) {
                hasGoodBR = true
            }
        }

        def nodeOutput = new XmlParser().parseText(cs2brxmlResult)

        if (hasGoodBR) {
            List removeBodies = new LinkedList()
            BizKey.children().eachWithIndex { currentTransaction, currentTransactionIndex ->
                def currentBody = nodeOutput.children().findAll {
                    it.name().toString().contains('Request')
                }?.get(currentTransactionIndex)
                if ((util.notEmpty(currentTransaction.AppErrorReport))) {
                    removeBodies.add(currentBody)
                } else {
                    if (util.notEmpty(currentBody.Request.GeneralInformation.SCAC.text())) {
                        def insertSCAC = util.InsertSCAC(MSG_REQ_ID, DIR_ID, 'OSCAC', currentBody.Request.GeneralInformation.SCAC.text(), conn)
                        def carrierId = util.getCarrierID(currentBody.Request.GeneralInformation.SCAC.text(), conn)
                        def uuidId = currentBody.Request.TransactionInformation.'ns0:MessageID'.text()
                        def interchangeCtlNum = currentTransaction.ControlNumberInfo.Interchange.text()
                        def groupCtlNum = currentTransaction.ControlNumberInfo.Group.text()
                        def transactionCtlNum = currentTransaction.ControlNumberInfo.Transaction.text()
                        //def insertMonLog = util.SetMonEDIControlNo(TP_ID, carrierId, MSG_TYPE_ID, 'EDIFACT', interchangeCtlNum,groupCtlNum, transactionCtlNum, uuidId,MSG_REQ_ID, conn)
                    }
                }
            }

            removeBodies.each { currentBody ->
                nodeOutput.remove(currentBody)
            }

            String cleanedOutputXml = util.cleanXml(XmlUtil.serialize(nodeOutput))

            return cleanedOutputXml
        } else {
            // prepare Node version of ShippingInstruction
            Node emptyMsg = nodeOutput.clone()
            List kids = new LinkedList()
            //remove the <Header> and all <Body>
            emptyMsg.children().eachWithIndex { kid, index ->
                kids.add(kid)
            }
            kids.eachWithIndex { kid, index ->
                emptyMsg.remove(kid)
            }

            return XmlUtil.serialize(emptyMsg)
        }
    }

    private List prepValidation(def current_Body, def currentTransactionIndex, Connection conn) {
        List AppErrors = new ArrayList()
        currentTransactionIndex = currentTransactionIndex + 1

        //Starting of Check SCAC
        def scac = current_Body.MessageProperties?.Parties?.PartnerInformation?.find {
            it.attr_PartnerRole == 'Carrier' && it?.PartnerIdentifier?.attr_Agency == 'AssignedBySender'
        }?.PartnerIdentifier?.toString()
        String validateSCAC = brUtil.checkSCAC(scac, conn)

        if (!util.isNotEmpty(validateSCAC)) {
            AppErrors.add('Transaction ' + currentTransactionIndex + ' Invalid SCAC on the input.')
        }
        //End of Check SCAC

        //Starting of Checking for TransportationDetails
        def conveyanceName = current_Body.MessageProperties?.TransportationDetails?.ConveyanceInformation?.ConveyanceName
        def voyageTripNumber = current_Body.MessageProperties?.TransportationDetails?.ConveyanceInformation?.VoyageTripNumber

        def porEstimatedDepartureDtm = current_Body.MessageProperties?.TransportationDetails?.Location?.find {
            it.attr_LocationType == 'PlaceOfReceipt'
        }?.DateTime.find { it.attr_DateType == 'EstimatedDeparture' }?.DateTime
        def podEstimatedArrivalDtm = current_Body.MessageProperties?.TransportationDetails?.Location?.find {
            it.attr_LocationType == 'PlaceOfDelivery'
        }?.DateTime.find { it.attr_DateType == 'EstimatedArrival' }?.DateTime

        def porLocation = current_Body.MessageProperties?.TransportationDetails?.Location?.find {
            it.attr_LocationType == 'PlaceOfReceipt'
        }
        def podLocation = current_Body.MessageProperties?.TransportationDetails?.Location?.find {
            it.attr_LocationType == 'PlaceOfDelivery'
        }

        //1. Check "Missing Conveyance Name"

        if (!util.isNotEmpty(conveyanceName) && (!util.isNotEmpty(porEstimatedDepartureDtm) || !util.isNotEmpty(podEstimatedArrivalDtm))) {
            //if ((conveyanceName.isEmpty() && porEstimatedDepartureDtm.isEmpty())||(conveyanceName.isEmpty() && podEstimatedArrivalDtm.isEmpty())){
            AppErrors.add('Transaction ' + currentTransactionIndex + ' Missing Conveyance Name.')
        }

        //2. Check "Missing Voyage Trip Number"
        if (!util.isNotEmpty(voyageTripNumber) && (!util.isNotEmpty(porEstimatedDepartureDtm) || !util.isNotEmpty(podEstimatedArrivalDtm))) {
            //if ((voyageTripNumber.isEmpty() && porEstimatedDepartureDtm.isEmpty())||(voyageTripNumber.isEmpty() && podEstimatedArrivalDtm.isEmpty())){
            AppErrors.add('Transaction ' + currentTransactionIndex + ' Missing VoyageTrip Number.')
        }

        //3. Check POR and FND is mandatory
        if (!util.isNotEmpty(porLocation) || !util.isNotEmpty(voyageTripNumber)) {
            //if (!porLocation||!podLocation){
            AppErrors.add('Transaction ' + currentTransactionIndex + ' POR and POD is mandatory')
        }
        //End of Checking for TransportationDetails

        //Check PartnerInformation
        def bookingPartyPartnerId = current_Body.MessageProperties?.Parties?.PartnerInformation.find {
            it.attr_PartnerRole == 'BookingParty'
        }?.PartnerIdentifier
        def carrierPartnerId = current_Body.MessageProperties?.Parties?.PartnerInformation.find {
            it.attr_PartnerRole == 'Carrier'
        }?.PartnerIdentifier
        def shipFromContactInfo = current_Body.MessageProperties?.Parties?.PartnerInformation.find {
            it.attr_PartnerRole == 'ShipFrom'
        }?.ContactInformation
        def shipToContactInfo = current_Body.MessageProperties?.Parties?.PartnerInformation.find {
            it.attr_PartnerRole == 'ShipTo'
        }?.ContactInformation

        def shipFromAddrInfo = current_Body.MessageProperties?.Parties?.PartnerInformation.find {
            it.attr_PartnerRole == 'ShipFrom'
        }?.AddressInformation
        def shipToAddrInfo = current_Body.MessageProperties?.Parties?.PartnerInformation.find {
            it.attr_PartnerRole == 'ShipTo'
        }?.AddressInformation

        //1. PartnerIdentifier
        if (!bookingPartyPartnerId || !carrierPartnerId) {
            AppErrors.add('Transaction ' + currentTransactionIndex + ' PartnerIdentifier is mandatory for Booking Party and Carrier')
        }

        //2. ContactInformation
        if (!shipFromContactInfo || !shipToContactInfo) {
            AppErrors.add('Transaction ' + currentTransactionIndex + ' ContactInformation is mandatory for Ship From and Ship To Party')
        }

        //3. AddressInformation
        if (!shipFromAddrInfo || !shipToAddrInfo) {
            AppErrors.add('Transaction ' + currentTransactionIndex + ' AddressInformation is mandatory for Ship From and Ship To Party')
        }

        //End of check PartnerInformation

        //Check EquirementDetail
        current_Body.MessageDetails?.EquipmentDetails.each {
            currentEquipmentDetails ->
                //Check Temperature
                if (currentEquipmentDetails?.EquipmentTemperature) {
                    BigDecimal equipmentTemp = new BigDecimal(currentEquipmentDetails?.EquipmentTemperature?.toString().trim())
                    if (equipmentTemp < -99 || equipmentTemp > 998) {
                        AppErrors.add('Transaction ' + currentTransactionIndex + ' The range of Equirment Temperture should between -099~998')
                    }
                }

                //check container Size Type
                if (util.isEmpty(util.getConversionByExtCde(TP_ID, 'BR', 'I', 'ContainerType', currentEquipmentDetails.EquipmentType?.EquipmentTypeCode?.toString(), conn))) {
                    AppErrors.add('Transaction ' + currentTransactionIndex + ' ContainerSizeType Invalid')
                }

                //String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, Connection conn
        }
        //End of check EquirementDetail

        return AppErrors
    }

    public List postValidation(def output, def input, def txnErrorKeys) {
        List AppErrors = new ArrayList()

        def nodeBookingRequest = new XmlParser().parseText(output)

        // prepare schema validator
        cs.b2b.core.common.xmlvalidation.ValidateXML validator = new ValidateXML()
        // prepare Node version of ShippingInstruction
        Node msgWithHeader = nodeBookingRequest.clone()
        List kids = new LinkedList()
        //remove the <Body>
        msgWithHeader.children().eachWithIndex { kid, index ->
            if (index != 0) {
                kids.add(kid)
            }
        }
        kids.eachWithIndex { kid, index ->
            msgWithHeader.remove(kid)
        }

        input.MessageBody.eachWithIndex { current_Body, current_BodyIndex ->
            if (!(txnErrorKeys[current_BodyIndex])) {
                // do Post-validation on Body (
                Node currentBody = nodeBookingRequest.children().get(current_BodyIndex + 1)
                Node currentMsg = msgWithHeader.clone()
                // prepare a complete transaction with one header & one body
                currentMsg.append(currentBody)
                String validationResult = validator.xmlValidation('CS2-CUS-BRXML', XmlUtil.serialize(currentMsg))
                if (validationResult.contains('Validation Failure.')) {
                    AppErrors.add(validationResult)
                }
            }
            // for empty Body i.e. pre-validation fails
            else AppErrors.add("")
        }
        return AppErrors
    }

    private void generateBody(Message msg, MessageBody msg_Body, def currentSystemDt, MarkupBuilder outXml) {
        SimpleDateFormat sdf = new SimpleDateFormat(xmlDateTimeFormat)
        outXml.'ns0:Request' {
            'ns0:TransactionInformation' {
                UUID idOne = UUID.randomUUID();
                'ns1:MessageID' MSG_REQ_ID + ',' + idOne
                'ns1:InterchangeTransactionID' msg.Header?.DocumentIdentifier
            }
            'ns0:EventInformation' {
                'ns1:EventCode' 'bkg_req'
                'ns1:EventDT' {
                    'ns1:LocDT'('TimeZone': 'HKT', 'CSTimeZone': 'HKT', sdf.format(currentSystemDt) + '+08:00')
                }
            }
            'ns0:GeneralInformation' {
                'ns0:CSBookingRefNumber'
                'ns0:CarrierBookingNumber' msg_Body.MessageProperties?.ReferenceInformation?.find {
                    it.attr_ReferenceType == 'BookingNumber'
                }?.toString()?.replaceFirst('OOLU', '')

                if (util.isNotEmpty(extActionTypeMap[msg_Body.MessageProperties?.ShipmentID?.ShipmentIdentifier?.attr_MessageStatus])) {
                    'ns0:ActionType' extActionTypeMap[msg_Body.MessageProperties?.ShipmentID?.ShipmentIdentifier?.attr_MessageStatus]
                }

                'ns0:SCAC' msg_Body.MessageProperties?.Parties?.PartnerInformation?.find {
                    it.attr_PartnerRole == 'Carrier' && it?.PartnerIdentifier?.attr_Agency == 'AssignedBySender'
                }?.PartnerIdentifier
                'ns0:BookingOffice'{ 'ns0:BLIssuingOffice' 'SIN' }
                if (msg.Header?.Parties?.PartnerInformation?.find { it.attr_PartnerRole == 'Sender' }) {
                    'ns0:Requested' {
                        'ns0:By' msg.Header?.Parties?.PartnerInformation?.find {
                            it.attr_PartnerRole == 'Sender' && it?.PartnerIdentifier.attr_Agency == 'AssignedBySender'
                        }?.PartnerIdentifier
                        if (msg_Body.MessageProperties?.DateTime?.attr_DateType == 'Message') {
                            'ns0:RequestedDT' {
                                'ns0:LocDT'('TimeZone': 'GMT',
                                        util.convertDateTime(msg_Body.MessageProperties?.DateTime, yyyyMMddHHmm, xmlDateTimeFormat) + '+08:00')
                            }
                        }
                    }
                }

                def vShipmentCargoType = null
                if (msg_Body.MessageDetails?.GoodsDetails?.HazardousGoods) {
                    if (msg_Body.MessageProperties?.Instructions?.ShipmentComments?.find {
                        it.attr_CommentType == 'TemperatureControl'
                    }) {
                        vShipmentCargoType = 'RD'
                    } else {
                        vShipmentCargoType = 'DG'
                    }
                } else if (msg_Body.MessageProperties?.Instructions?.ShipmentComments?.find {
                    it.attr_CommentType == 'TemperatureControl'
                }) {
                    vShipmentCargoType = 'RF'
                } else {
                    vShipmentCargoType = 'GC'
                }
                'ns0:ShipmentCargoType' vShipmentCargoType
                'ns0:ShipmentTrafficMode' {
                    'ns1:OutBound' 'FCL'
                    'ns1:InBound' 'FCL'
                }
                if (msg_Body.MessageProperties?.ContactInformation?.CommunicationValue?.attr_CommunicationType == 'Email') {
                    'ns0:NotificationEmailAddress' msg_Body.MessageProperties?.ContactInformation?.CommunicationValue
                }
                if (msg_Body.MessageProperties?.ShipmentID?.ShipmentIdentifier) {
                    'ns0:CustomerBookingReference' msg_Body.MessageProperties?.ShipmentID?.ShipmentIdentifier
                }
            }
            //flag for FWDRecord
            def isExistFWD = false
            msg_Body.MessageProperties?.Parties?.PartnerInformation?.each { current_PartnerInformation ->
                def vPartyType = extPartyType[current_PartnerInformation.attr_PartnerRole]
                if (vPartyType == 'FWD') {
                    isExistFWD = true
                }
            }

            msg_Body.MessageProperties?.Parties?.PartnerInformation?.each {
                current_PartnerInformation ->
                    def vPartyType = extPartyType[current_PartnerInformation.attr_PartnerRole]
            }

            //party  ->begin
            msg_Body.MessageProperties?.Parties?.PartnerInformation?.each { current_PartnerInformation ->
                def vPartyType = extPartyType[current_PartnerInformation.attr_PartnerRole]
                if (vPartyType) {
                    'ns0:Party' {
                        'ns1:PartyType' vPartyType
                        'ns1:PartyName' current_PartnerInformation?.PartnerName

                        'ns1:Contact' {
                            if (current_PartnerInformation.attr_PartnerRole == 'BookingParty'){

                                if (msg_Body.MessageProperties?.Parties?.PartnerInformation?.find {
                                    it?.attr_PartnerRole == 'Sender' && it?.ContactInformation}?.ContactInformation?.ContactName){
                                    'ns1:FirstName' msg_Body.MessageProperties?.Parties?.PartnerInformation?.find {
                                        it?.attr_PartnerRole == 'Sender' && it?.ContactInformation}?.ContactInformation?.ContactName
                                } else if (current_PartnerInformation?.ContactInformation?.ContactName) {
                                    'ns1:FirstName' current_PartnerInformation?.ContactInformation?.ContactName
                                }

                                if (msg_Body.MessageProperties?.Parties?.PartnerInformation?.find {
                                    it?.attr_PartnerRole == 'Sender' && it?.ContactInformation}?.ContactInformation?.CommunicationValue){
                                    'ns1:ContactPhone' {
                                        'ns1:Number' msg_Body.MessageProperties?.Parties?.PartnerInformation?.find {
                                            it?.attr_PartnerRole == 'Sender' && it?.ContactInformation
                                        }?.ContactInformation?.CommunicationValue
                                    }
                                } else if (current_PartnerInformation?.ContactInformation?.CommunicationValue) {
                                    'ns1:ContactPhone' {
                                        'ns1:Number' current_PartnerInformation?.ContactInformation?.CommunicationValue
                                    }
                                }
                            }
                        }

                        if (current_PartnerInformation?.AddressInformation) {
                            'ns1:Address' {
                                'ns1:AddressLines' {
                                    'ns1:AddressLine' String.join(" ", current_PartnerInformation.AddressInformation.AddressLine)
                                }
                            }
                        }
                    }
                    //FWDRecord
                    if (!isExistFWD && vPartyType == 'BPT') {
                        'ns0:Party' {
                            'ns1:PartyType' 'FWD'
                            'ns1:PartyName' current_PartnerInformation.PartnerName
                            'ns1:Contact' {
                                if (current_PartnerInformation.attr_PartnerRole == 'BookingParty'){
                                    if (msg_Body.MessageProperties?.Parties?.PartnerInformation?.find {
                                        it?.attr_PartnerRole == 'Sender' && it?.ContactInformation}?.ContactInformation?.ContactName){
                                        'ns1:FirstName' msg_Body.MessageProperties?.Parties?.PartnerInformation?.find {
                                            it?.attr_PartnerRole == 'Sender' && it?.ContactInformation}?.ContactInformation?.ContactName
                                    } else if (current_PartnerInformation?.ContactInformation?.ContactName) {
                                        'ns1:FirstName' current_PartnerInformation?.ContactInformation?.ContactName
                                    }

                                    if (msg_Body.MessageProperties?.Parties?.PartnerInformation?.find {
                                        it?.attr_PartnerRole == 'Sender' && it.ContactInformation}?.ContactInformation?.CommunicationValue){
                                        'ns1:ContactPhone' {
                                            'ns1:Number' msg_Body.MessageProperties?.Parties?.PartnerInformation?.find {
                                                it?.attr_PartnerRole == 'Sender' && it?.ContactInformation
                                            }?.ContactInformation?.CommunicationValue
                                        }
                                    } else if (current_PartnerInformation?.ContactInformation?.CommunicationValue) {
                                        'ns1:ContactPhone' {
                                            'ns1:Number' current_PartnerInformation?.ContactInformation?.CommunicationValue
                                        }
                                    }
                                }
                            }
                            if (current_PartnerInformation.AddressInformation) {
                                'ns1:AddressLines' {
                                    'ns1:AddressLine' String.join(" ", current_PartnerInformation.AddressInformation.AddressLine)
                                }
                            }
                        }
                    }
                }
            }
            //party  ->end

            msg_Body.MessageProperties?.ReferenceInformation?.each {
                current_Refer ->
                    //def vType = this.getReferenceType(TP_ID,current_Refer?.attr_ReferenceType,conn)
                    def vType = util.getConversionByExtCde(TP_ID, "BR", "I", "ReferenceType", current_Refer?.attr_ReferenceType, conn)
                    if (vType) {
                        'ns0:ExternalReference' {
                            'ns1:CSReferenceType' vType
                            'ns1:ReferenceNumber' current_Refer
                            // MD's value, but TIBCO output no this field
                            if (vType == 'OTH') {
                                'ns0:EDIReferenceType' current_Refer?.attr_ReferenceType
                            }
                        }
                    }
            }

            //ExtraExternalReference FID
            if (msg_Body.MessageProperties?.ShipmentID?.ShipmentIdentifier) {
                'ns0:ExternalReference' {
                    'ns1:CSReferenceType' 'FID'
                    'ns1:ReferenceNumber' msg_Body.MessageProperties.ShipmentID.ShipmentIdentifier
                }
            }
            //ExtraExternalReference OTH & SR
            if (msg.Header?.Parties?.PartnerInformation?.find {
                it.attr_PartnerRole == 'Sender' && it?.PartnerIdentifier?.attr_Agency == 'AssignedBySender'
            }) {
                'ns0:ExternalReference' {
                    'ns1:CSReferenceType' 'SR'
                    'ns1:ReferenceNumber' msg.Header?.Parties?.PartnerInformation?.find {
                        it.attr_PartnerRole == 'Sender' && it?.PartnerIdentifier?.attr_Agency == 'AssignedBySender'
                    }?.PartnerIdentifier
                }
                'ns0:ExternalReference' {
                    'ns1:CSReferenceType' 'OTH'
                    'ns1:ReferenceNumber' 'EDI Booking_' + msg.Header?.Parties?.PartnerInformation.find {
                        it.attr_PartnerRole == 'Sender' && it?.PartnerIdentifier?.attr_Agency == 'AssignedBySender'
                    }?.PartnerIdentifier
                    'ns0:EDIReferenceType' '50'
                }
            }

            //container ->start


            def sum_count = 0
            msg_Body.MessageDetails?.EquipmentDetails?.each { current_cuont ->
                sum_count += current_cuont.EquipmentCount.toString() as Integer
            }

            msg_Body.MessageDetails?.EquipmentDetails?.each { current_eqDetail ->
                'ns0:Container' {
                    def scac = null
                    def ext_cde = null
                    def ccsType = null
                    msg_Body.MessageProperties?.Parties?.PartnerInformation?.each {
                        current_pInfo ->
                            if (current_pInfo.attr_PartnerRole == 'Carrier' && current_pInfo?.PartnerIdentifier.attr_Agency == 'AssignedBySender') {
                                scac = current_pInfo?.PartnerIdentifier.toString()
                            }
                    }
                    ext_cde = current_eqDetail.EquipmentType.EquipmentTypeCode
                    //ccsType = this.getCarrCntrSizeType(TP_ID, scac, ext_cde, conn)
                    'ns0:CarrCntrSizeType' ccsType
                    'ns0:Quantity' current_eqDetail.EquipmentCount

                    'ns0:GrossWeight' {
                        'ns1:Weight'((msg_Body.MessageDetails?.GoodsDetails?.PackageDetailWeight.toString() as Integer) / sum_count).toString()
                        if (util.notEmpty(extWeightUnitMap[msg_Body.MessageDetails?.GoodsDetails?.PackageDetailWeight.attr_UOM])) {
                            'ns1:WeightUnit' extWeightUnitMap[msg_Body.MessageDetails?.GoodsDetails?.PackageDetailWeight.attr_UOM]
                        }
                    }

                    'ns0:NetWeight' ''

                    if (msg_Body.MessageProperties.HaulageDetails?.attr_MovementType != '' || msg_Body.MessageProperties.HaulageDetails?.attr_MovementType == 'DoorToPort') {
                        'ns0:OBDoor' {

                            def loctime = msg_Body.MessageProperties.TransportationDetails.Location.find {
                                it.attr_LocationType == 'PlaceOfReceipt'
                            }.DateTime.find { it.attr_DateType == 'EquipmentPickup' }.DateTime
                            'ns0:EmptyPickupDT' {
                                'ns1:LocDT' util.convertDateTime(util.substring(loctime.toString(), 1, 8), yyyyMMdd, xmlDateTimeFormat)
                            }

                            msg_Body.MessageProperties?.Parties.PartnerInformation.each { current_paInfo ->
                                if (current_paInfo.attr_PartnerRole == 'ShipFrom') {
                                    'ns0:DoorAppointment' {
                                        if (current_paInfo.AddressInformation) {
                                            'ns1:Address' {
                                                if (current_paInfo.AddressInformation.AddressLine) {
                                                    'ns1:AddressLines' {
                                                        def addressLine
                                                        for (i in current_paInfo.AddressInformation.AddressLine) {
                                                            if (addressLine)
                                                                addressLine = addressLine + i
                                                            else addressLine = i
                                                        }
                                                        'ns1:AddressLine' addressLine
                                                    }
                                                }
                                            }
                                        }

                                        'ns1:Company' current_paInfo.PartnerName
                                        if (current_paInfo.ContactInformation) {
                                            'ns1:Contact' {
                                                'ns1:FirstName' current_paInfo.ContactInformation.ContactName
                                                if (current_paInfo.ContactInformation.CommunicationValue?.attr_CommunicationType == 'Telephone') {
                                                    'ns1:ContactPhone' {
                                                        'ns1:CountryCode' ''
                                                        'ns1:AreaCode' ''
                                                        'ns1:Number' current_paInfo.ContactInformation.CommunicationValue
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (msg_Body.MessageProperties?.HaulageDetails) {
                        'ns0:Haulage' {
                            def ty = msg_Body.MessageProperties?.HaulageDetails.attr_MovementType
                            def lenght = ty.length()
                            if (ty) {
                                if (ty) {
                                    'ns1:OutBound' util.substring(ty, 1, 4) == 'Port' ? 'M' : 'C'
                                    'ns1:InBound' util.substring(ty, lenght - 3, lenght) == 'Port' ? 'M' : 'C'
                                }
                            }
                        }
                    }

                    //route ->start
                    if (msg_Body.MessageProperties?.TransportationDetails?.Location) {
                        'ns0:Route' {
                            cs.b2b.core.mapping.bean.edi.xml.br.inttra.Location porLcocation = msg_Body.MessageProperties?.TransportationDetails?.Location?.find {
                                it.attr_LocationType == 'PlaceOfReceipt'
                            }
                            if (porLcocation) {
                                'ns0:POR' {
                                    'ns1:LocationName' porLcocation.LocationName
                                    'ns1:CityDetails' {
                                        'ns1:City' porLcocation.LocationName
                                        if (porLcocation.LocationCode) {
                                            'ns1:LocationCode' {
                                                if (porLcocation.LocationCode.attr_Agency == 'AssignedBySender') {
                                                    'ns1:MutuallyDefinedCode' porLcocation.LocationCode
                                                }
                                                if (porLcocation.LocationCode.attr_Agency == 'UN') {
                                                    'ns1:UNLocationCode' porLcocation.LocationCode
                                                }
                                                if (porLcocation.LocationCode.attr_Agency == 'ScheduleD') {
                                                    'ns1:SchedKDType' 'D'
                                                } else if (porLcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                    'ns1:SchedKDType' 'K'
                                                }
                                                if (porLcocation.LocationCode.attr_Agency == 'ScheduleD' || porLcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                    'ns1:SchedKDCode' porLcocation.LocationCode
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                            cs.b2b.core.mapping.bean.edi.xml.br.inttra.Location fndLcocation = msg_Body.MessageProperties?.TransportationDetails?.Location?.find {
                                it.attr_LocationType == 'PlaceOfDelivery'
                            }
                            if (fndLcocation) {
                                'ns0:FND' {
                                    'ns1:LocationName' fndLcocation.LocationName
                                    'ns1:CityDetails' {
                                        'ns1:City' fndLcocation.LocationName
                                        if (fndLcocation.LocationCode) {
                                            'ns1:LocationCode' {
                                                if (fndLcocation.LocationCode.attr_Agency == 'AssignedBySender') {
                                                    'ns1:MutuallyDefinedCode' fndLcocation.LocationCode
                                                }
                                                if (fndLcocation.LocationCode.attr_Agency == 'UN') {
                                                    'ns1:UNLocationCode' fndLcocation.LocationCode
                                                }
                                                if (fndLcocation.LocationCode.attr_Agency == 'ScheduleD') {
                                                    'ns1:SchedKDType' 'D'
                                                } else if (fndLcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                    'ns1:SchedKDType' 'K'
                                                }
                                                if (fndLcocation.LocationCode.attr_Agency == 'ScheduleD' || fndLcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                    'ns1:SchedKDCode' fndLcocation.LocationCode
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            cs.b2b.core.mapping.bean.edi.xml.br.inttra.Location fPolcocation = msg_Body.MessageProperties?.TransportationDetails?.Location?.find {
                                it.attr_LocationType == 'PortOfLoading'
                            }
                            if (fPolcocation) {
                                'ns0:FirstPOL' {
                                    'ns1:Port' {
                                        'ns1:PortName' fPolcocation.LocationName
                                        if (fPolcocation.LocationCode) {
                                            'ns1:LocationCode' {
                                                if (fPolcocation.LocationCode.attr_Agency == 'AssignedBySender') {
                                                    'ns1:MutuallyDefinedCode' fPolcocation.LocationCode
                                                }
                                                if (fPolcocation.LocationCode.attr_Agency == 'UN') {
                                                    'ns1:UNLocationCode' fPolcocation.LocationCode
                                                }
                                                if (fPolcocation.LocationCode.attr_Agency == 'ScheduleD') {
                                                    'ns1:SchedKDType' 'D'
                                                } else if (fPolcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                    'ns1:SchedKDType' 'K'
                                                }
                                                if (fPolcocation.LocationCode.attr_Agency == 'ScheduleD' || fPolcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                    'ns1:SchedKDCode' fPolcocation.LocationCode
                                                }
                                            }
                                        }

                                    }

                                    'ns0:LoadingPortVoyage' util.substring(msg_Body.MessageProperties?.TransportationDetails?.ConveyanceInformation?.VoyageTripNumber +
                                            msg_Body.MessageProperties?.TransportationDetails?.ConveyanceInformation?.ConveyanceName, 1, 17)
                                    'ns0:LoadingPortVesselName' msg_Body.MessageProperties?.TransportationDetails?.ConveyanceInformation?.ConveyanceName
                                }
                            }
                            cs.b2b.core.mapping.bean.edi.xml.br.inttra.Location lPolcocation = msg_Body.MessageProperties?.TransportationDetails?.Location?.find {
                                it.attr_LocationType == 'PortOfDischarge'
                            }
                            if (lPolcocation) {
                                'ns0:LastPOD' {
                                    'ns1:Port' {
                                        'ns1:PortName' lPolcocation.LocationName
                                        if (lPolcocation.LocationCode) {
                                            'ns1:LocationCode' {
                                                if (lPolcocation.LocationCode.attr_Agency == 'AssignedBySender') {
                                                    'ns1:MutuallyDefinedCode' lPolcocation.LocationCode
                                                }
                                                if (lPolcocation.LocationCode.attr_Agency == 'UN') {
                                                    'ns1:UNLocationCode' lPolcocation.LocationCode
                                                }
                                                if (lPolcocation.LocationCode.attr_Agency == 'ScheduleD') {
                                                    'ns1:SchedKDType' 'D'
                                                } else if (lPolcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                    'ns1:SchedKDType' 'K'
                                                }
                                                if (lPolcocation.LocationCode.attr_Agency == 'ScheduleD' || lPolcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                    'ns1:SchedKDCode' lPolcocation.LocationCode
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                            if (fPolcocation || lPolcocation) {
                                'ns0:OceanLeg' {
                                    def fsize = msg_Body.MessageProperties?.TransportationDetails?.Location.findAll {
                                        it.attr_LocationType == 'PortOfLoading'
                                    }.size()
                                    def lsize = msg_Body.MessageProperties?.TransportationDetails?.Location.findAll {
                                        it.attr_LocationType == 'PortOfDischarge'
                                    }.size()
                                    'ns0:LegSeq' fsize >= lsize ? fsize : lsize
                                    if (fPolcocation) {
                                        'ns0:POL' {
                                            'ns1:Port' {
                                                'ns1:PortName' fPolcocation.LocationName
                                                if (fPolcocation.LocationCode) {
                                                    'ns1:LocationCode' {
                                                        if (fPolcocation.LocationCode.attr_Agency == 'AssignedBySender') {
                                                            'ns1:MutuallyDefinedCode' fPolcocation.LocationCode
                                                        }
                                                        if (fPolcocation.LocationCode.attr_Agency == 'UN') {
                                                            'ns1:UNLocationCode' fPolcocation.LocationCode
                                                        }
                                                        if (fPolcocation.LocationCode.attr_Agency == 'ScheduleD') {
                                                            'ns1:SchedKDType' 'D'
                                                        } else if (fPolcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                            'ns1:SchedKDType' 'K'
                                                        }
                                                        if (fPolcocation.LocationCode.attr_Agency == 'ScheduleD' || fPolcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                            'ns1:SchedKDCode' fPolcocation.LocationCode
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (lPolcocation) {
                                        'ns0:POD' {
                                            'ns1:Port' {
                                                'ns1:PortName' lPolcocation.LocationName
                                                if (lPolcocation.LocationCode) {
                                                    'ns1:LocationCode' {
                                                        if (lPolcocation.LocationCode.attr_Agency == 'AssignedBySender') {
                                                            'ns1:MutuallyDefinedCode' lPolcocation.LocationCode
                                                        }
                                                        if (lPolcocation.LocationCode.attr_Agency == 'UN') {
                                                            'ns1:UNLocationCode' lPolcocation.LocationCode
                                                        }
                                                        if (lPolcocation.LocationCode.attr_Agency == 'ScheduleD') {
                                                            'ns1:SchedKDType' 'D'
                                                        } else if (lPolcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                            'ns1:SchedKDType' 'K'
                                                        }
                                                        if (lPolcocation.LocationCode.attr_Agency == 'ScheduleD' || lPolcocation.LocationCode.attr_Agency == 'ScheduleK') {
                                                            'ns1:SchedKDCode' lPolcocation.LocationCode
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (msg_Body.MessageProperties?.TransportationDetails?.ConveyanceInformation) {
                                        'ns0:SVVD' {
                                            'ns1:VesselName' msg_Body.MessageProperties?.TransportationDetails?.ConveyanceInformation.ConveyanceName
                                            'ns1:Voyage' util.substring(msg_Body.MessageProperties?.TransportationDetails?.ConveyanceInformation.VoyageTripNumber, 1, 17)
                                            'ns0:ExternalVoyageNumber' util.substring(msg_Body.MessageProperties?.TransportationDetails?.ConveyanceInformation.VoyageTripNumber, 1, 17)
                                        }
                                    }
                                    if (fPolcocation && fPolcocation.DateTime?.find {
                                        it.attr_DateType == 'EstimatedDeparture'
                                    }) {
                                        'ns0:ETD' {
                                            'ns1:LocDT' util.convertDateTime(fPolcocation.DateTime?.find {
                                                it.attr_DateType == 'EstimatedDeparture'
                                            }, "yyyyMMdd", xmlDateTimeFormat)
                                        }
                                    }
                                    if (lPolcocation && lPolcocation.DateTime?.find {
                                        it.attr_DateType == 'EstimatedArrival'
                                    }) {
                                        'ns0:ETA' {
                                            'ns1:LocDT' util.convertDateTime(lPolcocation.DateTime?.find {
                                                it.attr_DateType == 'EstimatedArrival'
                                            }, "yyyyMMdd", xmlDateTimeFormat)
                                        }
                                    }
                                }
                            }
                        }
                        //route ->end
                    }
                }
            }
            //container ->end

            /*
             * Cargo->start
             */


            'ns0:Cargo' {
                def flag = false
                msg_Body.MessageProperties?.Instructions?.ShipmentComments?.each { current_shimp ->
                    if (current_shimp.attr_CommentType == 'TemperatureControl') {
                        flag = true
                    }
                }

                'ns0:CargoInfo' {

                    if (msg_Body.messageDetails?.GoodsDetails?.HazardousGoods) {
                        if (flag) {
                            'ns1:CargoNature' 'RD'
                        } else {
                            'ns1:CargoNature' 'DG'
                        }
                    } else if (flag) {
                        'ns1:CargoNature' 'RF'
                    } else {
                        'ns1:CargoNature' 'GC'
                    }

                    if (msg_Body.messageDetails?.GoodsDetails?.PackageDetailComments?.attr_CommentType == 'GoodsDescription')
                        'ns1:CargoDescription' msg_Body.MessageDetails.GoodsDetails?.PackageDetailComments
                    if (msg_Body.messageDetails?.GoodsDetails?.PackageDetail) {
                        'ns1:Packaging' {
                            'ns1:PackageType' 'PC'
                            'ns1:PackageQty' msg_Body.messageDetails?.GoodsDetails?.PackageDetail?.NumberOfPackages
                        }

                        if (msg_Body.messageDetails?.GoodsDetails?.PackageDetailWeight) {
                            'ns1:GrossWeight' {
                                'ns1:Weight' msg_Body.messageDetails?.GoodsDetails?.PackageDetailWeight
                                if (util.notEmpty(extWeightUnitMap[msg_Body.MessageDetails?.GoodsDetails?.PackageDetailWeight.attr_UOM])) {
                                    'ns1:WeightUnit' extWeightUnitMap[msg_Body.MessageDetails?.GoodsDetails?.PackageDetailWeight.attr_UOM]
                                }
                            }
                        }
                    }
                }
                //		if(flag){
                msg_Body.MessageDetails?.EquipmentDetails?.each { current_eDatil ->
                    'ns0:ReeferCargoSpec' {
                        if (current_eDatil.EquipmentTemperature?.EquipmentTemperature && current_eDatil.EquipmentTemperature?.attr_UOM == 'CEL' || current_eDatil.EquipmentTemperature?.attr_UOM == 'FAH') {
                            'ns1:Temperature' {
                                'ns1:Temperature' current_eDatil?.EquipmentTemperature
                                if (util.isNotEmpty(extTemperatureUnitMap[current_eDatil.EquipmentTemperature.attr_UOM])) {
                                    'ns1:TemperatureUnit' extTemperatureUnitMap[current_eDatil.EquipmentTemperature.attr_UOM]
                                }

                            }
                            if (current_eDatil?.EquipmentAirFlow) {
                                'ns1:Ventilation' {
                                    'ns1:Ventilation' current_eDatil?.EquipmentAirFlow
                                    if (current_eDatil?.EquipmentAirFlow == 'CBM') {
                                        'ns1:VentilationUnit' 'cbmPerHour'
                                    }
                                }
                            }
                        }
                    }
                }
                msg_Body.messageDetails?.GoodsDetails?.HazardousGoods?.each { current_HazardousGood ->

                    'ns0:DGCargoSpec' {
                        'ns1:IMDGPage' current_HazardousGood?.IMDGPageNumber
                        'ns1:IMOClass' current_HazardousGood?.IMOClassCode
                        'ns1:UNNumber' current_HazardousGood?.UNDGNumber
                        def str = ''
                        current_HazardousGood.HazardousGoodsComments.findAll {
                            (it.attr_CommentType == 'ProperShippingName')
                        }.each { current_hazar ->
                            str += current_hazar.HazardousGoodsComments
                        }
                        'ns1:ProperShippingName' util.substring(str, 1, 80)
                        if (msg_Body.messageDetails?.GoodsDetails?.PackageDetail) {
                            'ns1:PackageGroup' {
                                'ns1:OuterPackageDescription' {
                                    'ns1:PackageType' msg_Body.messageDetails?.GoodsDetails?.PackageDetail?.PackageTypeCode
                                    'ns1:PackageQty' msg_Body.messageDetails?.GoodsDetails?.PackageDetail?.NumberOfPackages
                                }
                            }
                        }

                        'ns1:GrossWeight' ''

                        if (current_HazardousGood.FlashpointTemperature) {
                            'ns1:FlashPoint' {
                                'ns1:Temperature' current_HazardousGood.FlashpointTemperature
                                if (util.isNotEmpty(extTemperatureUnitMap[current_HazardousGood.FlashpointTemperature.attr_UOM])) {
                                    'ns1:TemperatureUnit' extTemperatureUnitMap[current_HazardousGood.FlashpointTemperature.attr_UOM]
                                }
                            }
                        }

                        'ns1:EmergencyContact' {
                            'ns1:FirstName' current_HazardousGood.EmergencyResponseContact?.ContactInformation?.ContactName
                            if (current_HazardousGood.EmergencyResponseContact?.ContactInformation?.CommunicationValue?.attr_CommunicationType == 'Telephone') {
                                'ns1:ContactPhone' {
                                    'ns1:Number' current_HazardousGood.EmergencyResponseContact?.ContactInformation?.CommunicationValue
                                }
                            }
                        }

                        def str_desc = ''
                        current_HazardousGood.HazardousGoodsComments.findAll {
                            (it.attr_CommentType == 'General')
                        }.each { current_hdgc ->
                            str_desc += current_hdgc.HazardousGoodsComments
                        }
                        'ns0:Description' util.substring(str_desc, 1, 80)
                    }
                }

                'ns0:TrafficMode' {
                    'ns1:OutBound' 'FCL'
                    'ns1:InBound' 'FCL'
                }

            }
            /*
             *Cargo->end
             */

            msg_Body.MessageProperties?.Instructions?.ShipmentComments.findAll {
                (it.attr_CommentType == 'General')
            }.each { current_ShipmentComment ->
                'ns0:Remarks '('RemarkType': '01', current_ShipmentComment.ShipmentComments)
            }

            if (msg_Body.MessageProperties?.ContactInformation?.CommunicationValue && msg_Body.MessageProperties?.ContactInformation?.CommunicationValue?.attr_CommunicationType == 'Email')
                'ns0:Memo' {
                    'ns0:MemoContent' msg_Body.MessageProperties.ContactInformation.CommunicationValue
                }
        }
    }
}
