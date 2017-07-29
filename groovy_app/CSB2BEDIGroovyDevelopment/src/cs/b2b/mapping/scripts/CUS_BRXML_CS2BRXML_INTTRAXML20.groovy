package cs.b2b.mapping.scripts

import cs.b2b.core.common.util.StringUtil
import cs.b2b.core.common.xmlvalidation.ValidateXML
import cs.b2b.core.mapping.bean.edi.xml.br.inttra2.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import java.sql.Connection;
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat

import com.google.gson.Gson;

public class CUS_BRXML_CS2BRXML_INTTRAXML20 {

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

    Map<String, String> extPartyType = ["FirstAdditionalNotifyParty": "ANP",
                                        "Booker": "BPT",
                                        "Carrier": "CAR",
                                        "Consignee": "CGN",
                                        "Forwarder": "FWD",
                                        "MainNotifyParty": "NPT",
                                        "Shipper": "SHP"]

    String mapping(String inputXmlBody, String[] runtimeParams, Connection conn){
/**
 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
 */
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
        Message inputbr = xmlBeanParser.xmlParser(inputXmlBody,Message.class)

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


        def bodies = inputbr.MessageBody
        def transactionDT = ""

        transactionDT = util.substring(currentSystemDt.format("yyyy"), 1, 2)

        def action = ""
        if (inputbr.Header.TransactionStatus == "Original") {
            action = "NEW"
        } else if (inputbr.Header.TransactionStatus == "Change") {
            action = "UPD"
        } else if (inputbr.Header.TransactionStatus == "Cancel") {
            action = "CANCEL"
        }

        def InterchangeTransactionID = inputbr.Header?.DocumentIdentifier

        bodies.eachWithIndex { current_Body, current_BodyIndex ->
            // prep checking
            List<Map<String,String>> AppErrors = prepValidation(TP_ID, inputbr, current_BodyIndex, conn)

            // map Header once only
            if (!headerGenerated){

                brUtil.generateHeader(outXml, current_Body, outputBodyCount, currentSystemDt, DIR_ID, MSG_TYPE_ID, TP_ID, MSG_REQ_ID, InterchangeTransactionID)
                headerGenerated = true
            }
            // map Body
            if (AppErrors.size()==0) {
                generateBody(outXml, current_Body, outputBodyCount, currentSystemDt, transactionDT, AppErrors, action, InterchangeTransactionID)
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
        buildBizKey(bizKeyXml, brxml, inputbr, txnErrorKeys)
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

    public void generateBody(MarkupBuilder outXml, def current_Body, def current_BodyIndex, def currentSystemDt, def transactionDT, def preErrors, def action, def InterchangeTransactionID) {

        MessageBody currentTransaction = current_Body
        SimpleDateFormat sdf=new SimpleDateFormat(xmlDateTimeFormat)
        SimpleDateFormat sdf1=new SimpleDateFormat(yyyyMMddHHmmss)

        def SCAC_CDE = currentTransaction?.MessageProperties?.Party?.find { it.Name == 'Orient Shipping Services (OOCL)' && it.Identifier?.attr_Type == 'PartnerAlias' }?.Identifier?.toString()

        outXml.'ns0:Request' {
            'ns0:TransactionInformation' {
                UUID idOne = UUID.randomUUID();
                'ns1:MessageID' MSG_REQ_ID + ',' + idOne
                'ns1:InterchangeTransactionID' InterchangeTransactionID
            }
            'ns0:EventInformation' {
                'ns1:EventCode' 'bkg_req'
                'ns1:EventDT' {
                    //println
                    'ns1:LocDT'( 'TimeZone' : 'HKT', 'CSTimeZone' : 'HKT', currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")+'+08:00')
                }
            }
            'ns0:GeneralInformation' {
                'ns0:CSBookingRefNumber' ''
                'ns0:CarrierBookingNumber' currentTransaction?.MessageProperties?.ReferenceInformation?.find { it.attr_Type == 'BookingNumber' }?.Value

                if (util.notEmpty(action)){
                    'ns0:ActionType' action
                }

                'ns0:SCAC' SCAC_CDE
                //Check CargoNature is RF
                def isRFCargoNature = false
                currentTransaction.MessageDetails?.GoodsDetails?.each { current_GoodsDetail ->
                    if (currentTransaction?.MessageProperties?.NatureOfCargo?.find { it == 'TemperatureControlled' }
                            && (current_GoodsDetail.HazardousGoods == null || current_GoodsDetail.HazardousGoods.empty)) {
                        isRFCargoNature = true
                    }
                }
                if (isRFCargoNature) {
                    currentTransaction.MessageProperties?.TransportationDetails?.each { current_TransportationDetail ->
                        current_TransportationDetail.Location?.findAll {
                            it.Type == 'PortOfLoad' && it.Identifier?.attr_Type == 'UNLOC'
                        }?.each { current_Location ->
                            if (util.substring(current_Location.Identifier.toString(), 1, 2) == 'AU') {
                                'ns0:BookingOffice' { 'ns0:BookingOffice' 'SYD' }
                            }
                        }
                    }
                }
                'ns0:Requested' {
                    if (currentTransaction.MessageProperties?.Party?.find {
                        it.Role == 'Shipper' && it.Identifier?.attr_Type == 'PartnerAlias'
                    }) {
                        'ns0:By' util.substring(currentTransaction?.MessageProperties?.Party?.find { it.Role == 'Shipper' && it.Identifier?.attr_Type == 'PartnerAlias' }?.Identifier?.toString(), 1, 20)
                    }
                    'ns0:RequestedDT' {
                        'ns1:LocDT'('TimeZone': 'GMT',
                                currentTransaction.MessageProperties?.DateTime)
                    }
                }
                def vShipmentCargoType = null
                if (currentTransaction.MessageDetails?.GoodsDetails?.find {
                    it.HazardousGoods != null && !it.HazardousGoods.empty
                }) {
                    if (currentTransaction?.MessageProperties?.NatureOfCargo?.find { it == 'TemperatureControlled' }) {
                        vShipmentCargoType = 'RD'
                    } else {
                        vShipmentCargoType = 'DG'
                    }
                } else if (currentTransaction?.MessageProperties?.NatureOfCargo?.find { it == 'TemperatureControlled' }) {
                    vShipmentCargoType = 'RF'
                } else {
                    vShipmentCargoType = 'GC'
                }
                'ns0:ShipmentCargoType' vShipmentCargoType
                'ns0:ShipmentTrafficMode' {
                    'ns1:OutBound' 'FCL'
                    'ns1:InBound' 'FCL'
                }
                'ns0:NotificationEmailAddress' util.substring(currentTransaction?.MessageProperties?.ContactInformation?.CommunicationDetails?.Email[0], 1, 60)
                'ns0:CustomerBookingReference' currentTransaction.MessageProperties?.ReferenceInformation?.find {
                    it.attr_Type == 'ShipperReferenceNumber'
                }?.Value
            }

            // Party ->
            currentTransaction.MessageProperties.Party.each { current_Party ->
                def vPartyType = extPartyType[current_Party.Role]

                if (util.notEmpty(vPartyType)) {
                    'ns0:Party'{
                        'ns1:PartyType' vPartyType
                        'ns1:PartyName' current_Party.Name
                        if(util.notEmpty(current_Party.Identifier?.toString())){
                        def vCarrierCustomerCode = util.getConversionWithScacByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, 'CarrierCustCode', current_Party.Identifier?.toString(), SCAC_CDE, conn)

                        if (!vCarrierCustomerCode) {
                            vCarrierCustomerCode = current_Party.Identifier
                        }
                        if (vCarrierCustomerCode) {
                            'ns1:CarrierCustomerCode' vCarrierCustomerCode
                        }
                        }
                        //						'ns1:isNeedReplyPartyEmail' this.isNeedReplyPartyEmail('ONESTEELBSM',vPartyType,conn)
                        //Sam ->Start
                        'ns1:Contact' {
                            'ns1:FirstName' current_Party?.Contacts[0]?.Name
                            if (current_Party?.Contacts[0]?.CommunicationDetails?.Phone[0]) {
                                'ns1:ContactPhone' {
                                    'ns1:Number' util.substring(current_Party?.Contacts[0]?.CommunicationDetails?.Phone[0], 1, 30)
                                }
                            }

                            'ns1:ContactFax' {
//                                if (current_Party?.Contacts[0]?.CommunicationDetails?.Fax[0]) {
//                                    'ns1:Number' util.substring(current_Party?.Contacts[0]?.CommunicationDetails?.Fax[0], 1, 30)
//                                }

                            }
                            'ns1:ContactEmailAddress' current_Party?.Contacts[0]?.CommunicationDetails?.Email[0]
                        }
                        if (current_Party.Address) {
                            'ns1:Address' {
                                'ns1:City' current_Party.Address?.CityName
                                if (current_Party.Address?.CountryName) {
                                    'ns1:Country' current_Party.Address?.CountryName
                                }
                                'ns1:LocationCode' {
                                    'ns1:UNLocationCode' current_Party.Address.CountryCode

                                }
                                //								if(current_Party.Address.CountryName){
                                //									'ns1:Country' current_Party.Address.CountryName
                                //								}

                                if (util.substring(current_Party.Address.PostalCode, 1, 12)) {
                                    'ns1:PostalCode' util.substring(current_Party.Address.PostalCode, 1, 12)
                                }
                                def partyAddressline = ""
                                def partyStreet = ""
                                def partyCityName = ""
                                def partySubdivision = ""
                                def partyPostalCode = ""
                                def partyCountryCode = ""
                                if (current_Party.Address?.StreetAddress) {
                                    partyStreet = current_Party?.Address?.StreetAddress + ", "
                                }
                                if (current_Party.Address?.CityName) {
                                    partyCityName = current_Party.Address?.CityName + ", "
                                }
                                if (current_Party.Address?.Subdivision) {
                                    partySubdivision = current_Party.Address?.Subdivision + ", "
                                }
                                if (current_Party.Address?.PostalCode) {
                                    partyPostalCode = current_Party.Address?.PostalCode + ", "
                                }
                                if (current_Party.Address?.CountryCode) {
                                    if (current_Party.Address?.CountryName) {
                                        partyCountryCode = current_Party.Address?.CountryCode + ", "
                                    } else {
                                        partyCountryCode = current_Party.Address?.CountryCode
                                    }
                                }


                                if (current_Party.Address?.CountryName) {
                                    partyAddressline = partyStreet + partyCityName + partySubdivision + partyPostalCode + partyCountryCode + current_Party.Address?.CountryName
                                } else {
                                    partyAddressline = (partyStreet + partyCityName + partySubdivision + partyPostalCode + partyCountryCode).trim()
                                }
                                'ns1:AddressLines' { 'ns1:AddressLine' partyAddressline }
                            }
                        }
                    }
                }

            }//End-party looping
            currentTransaction?.MessageProperties?.ReferenceInformation?.each { current_ReferenceInformation ->
                def vCSCarrierRateType = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, 'CarrierRateType', current_ReferenceInformation.attr_Type, conn)
                if (vCSCarrierRateType) {
                    'ns0:CarrierRateReference' {
                        'ns1:CSCarrierRateType' vCSCarrierRateType
                        'ns1:CarrierRateNumber' current_ReferenceInformation.value
                    }

                }
                def vExternalReferenceType = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, "ReferenceType", current_ReferenceInformation.attr_Type, conn)
                if (vExternalReferenceType) {
                    'ns0:ExternalReference' {
                        'ns1:CSReferenceType' vExternalReferenceType
                        'ns1:ReferenceNumber' current_ReferenceInformation.Value
                        'ns0:EDIReferenceType' vExternalReferenceType
                    }
                }
            }
            if (currentTransaction?.MessageProperties?.ShipmentID) {
                'ns0:ExternalReference' {
                    'ns1:CSReferenceType' 'OTH'
                    'ns1:ReferenceNumber' currentTransaction.MessageProperties.ShipmentID
                    'ns0:EDIReferenceType' '50'
                }
            }
            if (currentTransaction?.MessageProperties?.ReferenceInformation?.find { it.attr_Type == 'ShipperReferenceNumber' }) {
                'ns0:ExternalReference' {
                    'ns1:CSReferenceType' 'SID'
                    'ns1:ReferenceNumber' currentTransaction?.MessageProperties?.ReferenceInformation?.find { it.attr_Type == 'ShipperReferenceNumber' }?.Value
                }
            }

            //container ->start
            currentTransaction.MessageDetails.EquipmentDetails.each { current_container ->
                'ns0:Container' {
                    String ccsType = util.getConversionWithScacByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, 'ContainerType', current_container.EquipmentType?.EquipmentTypeCode, SCAC_CDE, conn)
                    'ns0:CarrCntrSizeType' ccsType
                    'ns0:Quantity' current_container.NumberOfEquipment
                    if (current_container.EquipmentGrossWeight) {
                        'ns0:GrossWeight' {
                            'ns1:Weight' current_container.EquipmentGrossWeight
                            if (current_container.EquipmentGrossWeight?.attr_UOM == "KGM") {
                                'ns1:WeightUnit' "KGS"
                            } else if (current_container.EquipmentGrossWeight?.attr_UOM == "LBS") {
                                'ns1:WeightUnit' "LBS"
                            }
                        }
                    }
                    //					'ns0:Quantity' current_EquipmentDetails.NumberOfEquipment
                    'ns0:NetWeight' ''
                    def current_Location = currentTransaction?.MessageProperties?.Location?.find { it.Type == 'PlaceOfReceipt' && it.DateTime.attr_DateType == 'EarliestDeparture' }
                    def vDateTime = current_Location.DateTime
                    if (currentTransaction?.MessageProperties?.MovementType == 'DoorToDoor' || currentTransaction.MessageProperties.MovementType == 'DoorToPort') {
//todo
                        'ns0:OBDoor' {
                            'ns0:EmptyPickupDT' { 'ns1:LocDT' vDateTime }
                            if (currentTransaction.MessageProperties.Party.find { it.Role == "ShipFromDoor" }) {
                                'ns0:DoorAppointment' {
                                    if (currentTransaction.MessageProperties.Party.find { it.Role == "Booker" }.Address) {
                                        'ns1:Address' {
                                            'ns1:City' currentTransaction.MessageProperties.Party.find {
                                                it.Role == "Booker"
                                            }.Address.CityName
                                            'ns1:Country' currentTransaction.MessageProperties.Party.find {
                                                it.Role == "Booker"
                                            }.Address.CountryName
                                            'ns1:PostalCode' currentTransaction.MessageProperties.Party.find {
                                                it.Role == "Booker"
                                            }.Address.PostalCode
                                            if (currentTransaction.MessageProperties.Party.find {
                                                it.Role == "Booker"
                                            }.Address.StreetAddress) {
                                                def partyAddressline = ""
                                                def partyStreet = ""
                                                def partyCityName = ""
                                                def partySubdivision = ""
                                                def partyPostalCode = ""
                                                def partyCountryCode = ""
                                                if (currentTransaction.MessageProperties.Party.find {
                                                    it.Role == "Booker"
                                                }.Address.StreetAddress) {
                                                    partyStreet = currentTransaction.MessageProperties.Party.find {
                                                        it.Role == "Booker"
                                                    }.Address.StreetAddress + ", "
                                                }
                                                if (currentTransaction.MessageProperties.Party.find {
                                                    it.Role == "Booker"
                                                }.Address.CityName) {
                                                    partyCityName = currentTransaction.MessageProperties.Party.find {
                                                        it.Role == "Booker"
                                                    }.Address.CityName + ", "
                                                }
                                                if (currentTransaction.MessageProperties.Party.find {
                                                    it.Role == "Booker"
                                                }.Address.Subdivision) {
                                                    partySubdivision = currentTransaction.MessageProperties.Party.find {
                                                        it.Role == "Booker"
                                                    }.Address.Subdivision + ", "
                                                }
                                                if (currentTransaction.MessageProperties.Party.find {
                                                    it.Role == "Booker"
                                                }.Address.PostalCode) {
                                                    partyPostalCode = currentTransaction.MessageProperties.Party.find {
                                                        it.Role == "Booker"
                                                    }.Address.PostalCode + ", "
                                                }
                                                if (currentTransaction.MessageProperties.Party.find {
                                                    it.Role == "Booker"
                                                }.Address.CountryCode) {
                                                    partyCountryCode = currentTransaction.MessageProperties.Party.find {
                                                        it.Role == "Booker"
                                                    }.Address.CountryCode + ", "
                                                }
                                                partyAddressline = partyStreet + partyCityName + partySubdivision + partyPostalCode + partyCountryCode + msg_Body.MessageProperties.Party.find {
                                                    it.Role == "Booker"
                                                }.Address.CountryName
                                                'ns1:AddressLines' { 'ns1:AddressLine' partyAddressline }
                                            }
                                        }

                                    }
                                    'ns1:Company' currentTransaction.MessageProperties.Party.find { it.Role == "Booker" }.Name
                                    currentTransaction.MessageProperties.Party.find {
                                        it.Role == "Booker"
                                    }.Contacts.each { current_Contact ->
                                        'ns1:Contact' {
                                            'ns1:FirstName' current_Contact.Name[0]
                                            if (current_Contact.CommunicationDetails.Phone[0]) {
                                                'ns1:ContactPhone' {
                                                    'ns1:Number' current_Contact.CommunicationDetails.Phone[0]
                                                }
                                            }
                                            'ns1:ContactEmailAddress' current_Contact.CommunicationDetails.Email[0]
                                        }
                                    }
                                }
                            }

                        }
                    }
                    if (currentTransaction?.MessageProperties?.MovementType) {
                        'ns0:Haulage' {
                            def ty = currentTransaction?.MessageProperties?.MovementType
                            def lenght = ty.length()
                            if (ty) {
                                if (ty) {
                                    'ns1:OutBound' util.substring(ty, 1, 4) == 'Port' ? 'M' : 'C'
                                    'ns1:InBound' util.substring(ty, lenght - 3, lenght) == 'Port' ? 'M' : 'C'
                                }
                            }


                        }
                    }

                    //sam ->end


                    'ns0:Route' {
                        'ns0:IntendedDT' {
                            if (currentTransaction.MessageProperties.Location.find {
                                it.Type == 'PlaceOfReceipt' && it.DateTime.attr_DateType == 'EarliestDeparture'
                            }) {
                                'ns0:From' {
                                    'ns1:LocDT' currentTransaction.MessageProperties.Location.find {
                                        it.Type == 'PlaceOfReceipt' && it.DateTime.attr_DateType == 'EarliestDeparture'
                                    }.DateTime
                                }
                                'ns0:IntendedRangeIndicator' 'S'
                            }

                        }
                        //POR

                        cs.b2b.core.mapping.bean.edi.xml.br.inttra2.Location porLocation = currentTransaction.MessageProperties?.Location?.find{it.Type == 'PlaceOfReceipt'}
                        if (porLocation) {
                            'ns0:POR' {
                                'ns1:CityDetails' {
                                    'ns1:City' porLocation.City
                                    if (porLocation.Identifier) {
                                        'ns1:LocationCode' {
                                            if (porLocation.Identifier?.attr_Type == 'UNLOC') {
                                                'ns1:UNLocationCode' util.substring(porLocation.Identifier.toString(), 1, 5)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //FND
                        cs.b2b.core.mapping.bean.edi.xml.br.inttra2.Location fndLocation = currentTransaction.MessageProperties?.Location?.find{it.Type == 'PlaceOfDelivery'}
                        //as Class<Location>
                        if (fndLocation) {
                            'ns0:FND' {
                                'ns1:CityDetails' {
                                    'ns1:City' fndLocation.City
                                    if (fndLocation.Identifier) {
                                        'ns1:LocationCode' {
                                            if (fndLocation.Identifier.attr_Type == 'UNLOC')
                                                'ns1:UNLocationCode' util.substring(fndLocation.Identifier.toString(), 1, 5)
                                        }
                                    }
                                }
                            }
                        }
                        cs.b2b.core.mapping.bean.edi.xml.br.inttra2.TransportationDetails tsDetails1= currentTransaction.MessageProperties?.TransportationDetails?.find{it.Location.find{it.Type == 'PortOfLoad'}}
                        cs.b2b.core.mapping.bean.edi.xml.br.inttra2.Location fPOLLocation = tsDetails1.Location.find{it.Type == 'PortOfLoad'}
                        if (fPOLLocation) {
                            'ns0:FirstPOL' {
                                'ns1:Port' {
                                    'ns1:PortName' fPOLLocation.Name
                                    'ns1:City' fPOLLocation.City
                                    if (fPOLLocation.Identifier) {
                                        'ns1:LocationCode' {
                                            if (fPOLLocation.Identifier.attr_Type == 'UNLOC') {
                                                'ns1:UNLocationCode' util.substring(fPOLLocation.Identifier.toString(), 1, 5)
                                            }
                                        }
                                    }
                                    'ns1:Country' fPOLLocation.CountryName
                                }
                                def VoyageNumber = currentTransaction?.MessageProperties?.TransportationDetails?.find { it.ConveyanceInformation?.Identifier?.find { it.attr_Type == 'VoyageNumber' }
                                }?.ConveyanceInformation?.Identifier?.find { it.attr_Type == 'VoyageNumber' }?.toString()
                                def VesselName = currentTransaction?.MessageProperties?.TransportationDetails?.find {
                                    it.ConveyanceInformation?.Identifier?.find { it.attr_Type == 'VesselName' }
                                }?.ConveyanceInformation?.Identifier?.find { it.attr_Type == 'VesselName' }?.toString()
                                'ns0:LoadingPortVoyage' util.substring(VoyageNumber + VesselName, 1, 17)
                                'ns0:LoadingPortVesselName' VesselName
                            }
                        }
                        cs.b2b.core.mapping.bean.edi.xml.br.inttra2.TransportationDetails tsDetails2= currentTransaction.MessageProperties?.TransportationDetails?.find{it.Location.find{it.Type == 'PortOfLoad'}}
                        cs.b2b.core.mapping.bean.edi.xml.br.inttra2.Location lPODLocation = tsDetails2.Location.find{it.Type == 'PortOfDischarge'}
                        if (lPODLocation) {
                            'ns0:LastPOD' {
                                'ns1:Port' {
                                    'ns1:PortName' lPODLocation.Name
                                    'ns1:City' lPODLocation.City
                                    if (lPODLocation.Identifier) {
                                        'ns1:LocationCode' {
                                            if (lPODLocation.Identifier.attr_Type == 'UNLOC') {
                                                'ns1:UNLocationCode' util.substring(lPODLocation.Identifier.toString(), 1, 5)
                                            }
                                        }
                                    }
                                    'ns1:Country' lPODLocation.CountryName
                                }

                            }
                        }

                        /*'ns0:LoadingPortLatestDepDT'{
                         if(msg_Body.MessageProperties?.Location.find{it.DateTime.attr_DateType == 'EarliestDeparture'}){
                         'ns1:LocDT' msg_Body.MessageProperties?.Location.find{it.DateTime.attr_DateType == 'EarliestDeparture'}.DateTime
                         }
                         }
                         'ns0:DischargePortLatestArrDT'{
                         if(msg_Body.MessageProperties?.Location.find{it.DateTime.attr_DateType == 'EstimatedArrival'}){
                         'ns1:LocDT' msg_Body.MessageProperties?.Location.find{it.DateTime.attr_DateType == 'EstimatedArrival'}.DateTime
                         }
                         }*/
                        if (fPOLLocation || lPODLocation) {
                            def fsize = 0
                            def lsize = 0
                            currentTransaction.MessageProperties?.TransportationDetails?.each {
                                fsize = fsize + it.Location.findAll { it.Type == 'PortOfLoad' }.size()
                            }
                            currentTransaction.MessageProperties?.TransportationDetails?.each {
                                lsize = lsize + it.Location.findAll { it.Type == 'PortOfDischarge' }.size()
                            }
                            'ns0:OceanLeg' {
                                'ns0:LegSeq' fsize >= lsize ? fsize : lsize
                                if (fPOLLocation) {
                                    'ns0:POL' {
                                        'ns1:Port' {
                                            'ns1:PortName' fPOLLocation.Name
                                            'ns1:City' fPOLLocation.City
                                            if (fPOLLocation.Identifier) {
                                                'ns1:LocationCode' {
                                                    if (fPOLLocation.Identifier.attr_Type == 'UNLOC') {
                                                        'ns1:UNLocationCode' fPOLLocation.Identifier.toString()
                                                    }
                                                }
                                            }
                                            'ns1:Country' fPOLLocation.CountryName
                                        }
                                    }
                                }
                                if (lPODLocation) {
                                    'ns0:POD' {
                                        'ns1:Port' {
                                            'ns1:PortName' lPODLocation.Name
                                            'ns1:City' lPODLocation.City
                                            if (lPODLocation.Identifier) {
                                                'ns1:LocationCode' {
                                                    if (lPODLocation.Identifier.attr_Type == 'UNLOC') {
                                                        'ns1:UNLocationCode' lPODLocation.Identifier.toString()
                                                    }
                                                }
                                            }
                                            'ns1:Country' lPODLocation.CountryName
                                        }
                                    }
                                }
                                if (currentTransaction.MessageProperties?.TransportationDetails?.find {
                                    it.ConveyanceInformation
                                }) {
                                    'ns0:SVVD' {
                                        for (current_details in currentTransaction.MessageProperties?.TransportationDetails) {
                                            if (current_details.ConveyanceInformation.Identifier.find {
                                                it.attr_Type == 'VesselName'
                                            }) {
                                                'ns1:VesselName' current_details.ConveyanceInformation.Identifier.find {
                                                    it.attr_Type == 'VesselName'
                                                }.toString()
                                                break
                                            }
                                        }
                                        for (current_details in currentTransaction.MessageProperties?.TransportationDetails) {
                                            if (current_details.ConveyanceInformation.Identifier.find {
                                                it.attr_Type == 'VoyageNumber'
                                            }) {
                                                'ns1:Voyage' util.substring(current_details.ConveyanceInformation.Identifier.find {
                                                    it.attr_Type == 'VoyageNumber'
                                                }.toString(), 1, 17)
                                                break
                                            }

                                        }
                                    }
                                }
                                if (fPOLLocation && fPOLLocation.DateTime?.attr_DateType == 'EarliestDeparture') {
                                    'ns0:ETD' { 'ns1:LocDT' fPOLLocation.DateTime }
                                }
                                if (lPODLocation && lPODLocation.DateTime?.attr_DateType == 'EstimatedArrival') {
                                    'ns0:ETA' { 'ns1:LocDT' lPODLocation.DateTime }
                                }
                            }
                        }
                    }
                }
            }

            //container ->end

            /*
             * Cargo->start
             */
            if (currentTransaction?.MessageDetails?.GoodsDetails) {
                currentTransaction?.MessageDetails?.GoodsDetails?.each { current_goodsDetail ->
                    'ns0:Cargo' {
                        //CargoInfo  ->
                        'ns0:CargoInfo' {
                            if (current_goodsDetail?.HazardousGoods || !current_goodsDetail?.HazardousGoods?.empty) {
                                if (currentTransaction.MessageProperties.NatureOfCargo.find { it == 'TemperatureControlled' }) {
                                    'ns1:CargoNature' 'RD'
                                } else {
                                    'ns1:CargoNature' 'DG'
                                }
                            } else {
                                if (currentTransaction?.MessageProperties?.NatureOfCargo?.find { it == 'TemperatureControlled' }) {
                                    'ns1:CargoNature' 'RF'
                                } else {
                                    'ns1:CargoNature' 'GC'
                                }
                            }

                            'ns1:CargoDescription' util.substring(current_goodsDetail.GoodDescription, 1, 1000)

                            if (current_goodsDetail.PackageDetail) {
                                'ns1:Packaging' {
                                    'ns1:PackageType' 'PC'
                                    'ns1:PackageQty' current_goodsDetail.PackageDetail?.OuterPack?.NumberOfPackages
                                }

                                if (current_goodsDetail.PackageDetail?.OuterPack?.GoodGrossWeight) {
                                    'ns1:GrossWeight' {


                                        if (current_goodsDetail.PackageDetail?.OuterPack?.GoodGrossWeight?.attr_UOM == 'KGM') {
                                            'ns1:Weight'(((current_goodsDetail?.PackageDetail?.OuterPack?.GoodGrossWeight?.toString()) as double) / 1000).toString()
                                        } else {
                                            'ns1:Weight' current_goodsDetail.PackageDetail?.OuterPack?.GoodGrossWeight
                                        }

                                        if (current_goodsDetail?.PackageDetail?.OuterPack?.GoodGrossWeight?.attr_UOM == 'KGM') {
                                            'ns1:WeightUnit' 'TON'
                                        } else if (current_goodsDetail.PackageDetail?.OuterPack?.GoodGrossWeight?.attr_UOM == 'LBS') {
                                            'ns1:WeightUnit' 'LBS'
                                        }
                                    }
                                }

                            }
                            if (current_goodsDetail.CommodityClassification) {
                                'ns0:HarmonizedTariffSchedule' current_goodsDetail.CommodityClassification[0]
                            }

                        }
                        //ReeferCargoSpec	->
                        if (currentTransaction?.MessageProperties?.NatureOfCargo?.find { it == 'TemperatureControlled' }) {
                            currentTransaction.MessageDetails?.EquipmentDetails?.each { current_equipmentDetail ->

                                'ns0:ReeferCargoSpec' {
                                    if (current_equipmentDetail.EquipmentTemperature) {
                                        'ns1:Temperature' {
                                            'ns1:Temperature' current_equipmentDetail.EquipmentTemperature
                                            if (current_equipmentDetail.EquipmentTemperature?.attr_UOM == 'CEL') {
                                                'ns1:TemperatureUnit' 'C'
                                            } else if (current_equipmentDetail.EquipmentTemperature.attr_UOM == 'FAH') {
                                                'ns1:TemperatureUnit' 'F'
                                            }
                                        }
                                        if (current_equipmentDetail?.EquipmentAirFlow) {
                                            'ns1:Ventilation' {
                                                'ns1:Ventilation' current_equipmentDetail?.EquipmentAirFlow
                                                if (current_equipmentDetail?.EquipmentAirFlow?.attr_UOM == 'CBM') {
                                                    'ns1:VentilationUnit' 'cbmPerHour'
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //DGCargoSpec 	->
                        if (current_goodsDetail.HazardousGoods) {
                            currentTransaction?.messageDetails?.GoodsDetails?.each { current_gd ->

                                def psname = ''
                                current_gd.HazardousGoods?.each { current_hazar ->
                                    psname += current_hazar.ProperShippingName
                                }
                                current_gd.HazardousGoods?.each { current_HazardousGood ->
                                    'ns0:DGCargoSpec' {
                                        'ns1:IMDGPage' util.substring(current_HazardousGood?.IMDGPageNumber, 1, 5)
                                        'ns1:IMOClass' util.substring(current_HazardousGood?.IMOClassCode[0], 1, 5)
                                        'ns1:UNNumber' current_HazardousGood?.UNDGNumber

                                        'ns1:ProperShippingName' util.substring(psname, 1, 80)

                                        if (current_gd.PackageDetail) {
                                            'ns1:PackageGroup' {
                                                'ns1:OuterPackageDescription' {
                                                    'ns1:PackageType' current_gd.PackageDetail?.OuterPack?.PackageTypeCode
                                                    'ns1:PackageQty' current_gd.PackageDetail?.OuterPack?.NumberOfPackages
                                                }
                                            }
                                        }

                                        'ns1:GrossWeight' ''

                                        if (current_HazardousGood.FlashpointTemperature) {
                                            'ns1:FlashPoint' {
                                                'ns1:Temperature' current_HazardousGood.FlashpointTemperature
                                                if (current_HazardousGood.FlashpointTemperature.attr_UOM == 'CEL') {
                                                    'ns1:TemperatureUnit' 'C'
                                                } else if (current_HazardousGood.FlashpointTemperature.attr_UOM == 'FAH') {
                                                    'ns1:TemperatureUnit' 'F'
                                                }
                                            }
                                        }

                                        current_HazardousGood.EmergencyResponseContact.each { current_erc ->
                                            'ns1:EmergencyContact' {
                                                'ns1:FirstName' current_erc.Name
                                                if (current_erc.CommunicationDetails?.Phone[0]) {
                                                    'ns1:ContactPhone' {
                                                        'ns1:Number' util.substring(current_erc.CommunicationDetails?.Phone[0], 1, 30)
                                                    }
                                                }
                                            }
                                        }

                                        def str_desc = current_HazardousGood?.HazardousGoodsComments?.find {
                                            it.Category == 'GeneralHazmatComments'
                                        }?.Text

                                        'ns0:Description' util.substring(str_desc, 1, 70)
                                    }
                                }
                            }
                        }
                        //TrafficMode	->
                        'ns0:TrafficMode' {
                            'ns1:OutBound' 'FCL'
                            'ns1:InBound' 'FCL'
                        }
                    }
                }
            } else if (action == 'Cancel') {
                def dummyGoodsDetails = null
                def lineNumber = 1
                def goodDescription = 'dummy cargo'
            }
            /*
             * Cargo  ->end
             */

            //Remarks	->

            'ns0:Remarks '('RemarkType': '01', currentTransaction?.MessageProperties?.GeneralInformation?.Text)
            def vn_remark = 'Vessel Voyage details: '
            currentTransaction?.MessageProperties?.TransportationDetails?.each {
                it.ConveyanceInformation?.Identifier?.each { current_identifier ->
                    if (current_identifier.attr_Type == 'VesselName' || current_identifier.attr_Type == 'VoyageNumber') {
                        vn_remark = vn_remark + current_identifier + ' '
                    }
                }
                'ns0:Remarks'('RemarkType': '01', vn_remark.trim())
            }
            def ed_remark = 'Earliest Depature: '
            currentTransaction.MessageProperties?.each { a ->

                a.Location.each { b ->
                    if (b.Type == 'PlaceOfReceipt') {
                        if (b.DateTime.attr_DateType == 'EarliestDeparture')
                            ed_remark = ed_remark + b.DateTime.DateTime
                    } else {
                    }
                }
            }
            'ns0:Remarks'('RemarkType': '03', ed_remark)
            if (currentTransaction.MessageProperties?.AmendmentJustification?.Text) {
                'ns0:Remarks' currentTransaction.MessageProperties?.AmendmentJustification?.Text
            }

            //Memo	->
            if (currentTransaction.MessageProperties?.ContactInformation?.CommunicationDetails?.Email[0])
                'ns0:Memo' {
                    'ns0:MemoContent' currentTransaction.MessageProperties?.ContactInformation?.CommunicationDetails?.Email[0]
                }

        }
    }

    public void buildBizKey(MarkupBuilder bizKeyXml, def outputxml, def inputXml, def txnErrorKeys){
    def bookingRequest = new XmlSlurper().parseText(outputxml)
    Message input = inputXml

    input.MessageBody?.eachWithIndex { current_Body, current_BodyIndex ->
        bizKeyXml.'ns0:Transaction' ('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
            'ns0:ControlNumberInfo' {
                'ns0:Interchange'
                'ns0:Group'
                'ns0:Transaction' input.Header?.DocumentIdentifier
            }

            // If no error in pre-validation
            if (txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.size() == 0) {
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

                'ns0:CarrierId' //util.getCarrierID(bookingRequest.Request[current_BodyIndex].GeneralInformation?.SCAC.text(), conn)

            }// End-if-no-error-in-prevalidation

            'ns0:CTEventTypeId'

            // map validation error
            String Status = ''
            String errMsg = ''

            if (txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.size()>0) {
                Status = 'E'
                txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.eachWithIndex{ stringError, errorIndex ->
                    errMsg = errMsg +  ' [Error ' + (errorIndex + 1) + '] : ' + stringError
                    if (errorIndex != 0)
                        errMsg = errMsg + ', '
                }

            }



            if (txnErrorKeys[current_BodyIndex]?.findAll{it != ""}?.size() > 0) {
                'ns1:AppErrorReport'('xmlns:ns1': 'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
                    'ns1:Status' Status
                    'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'    //max length is 20, exceed will missing error bizkey
                    'ns1:Msg' errMsg.take(1000)
                    'ns1:Severity' '5'
                }
            }
        }// End-Transaction
    }
}

    private List<Map<String,String>> prepValidation(def TP_ID, def inputBR, def currentTransactionIndex, Connection conn) {
        Message currentTransaction = inputBR
        List AppErrors = new ArrayList()
        currentTransactionIndex  = currentTransactionIndex + 1

        //Check vaild SCAC
//        $mapLocationConstant/root/Config[SegmentId="PartnerInformation.PartnerIdentitifier" and SegmentField="SCAC"]/ProcessId = "1" and
//        tib:trim($checkSCAC/resultSet/Record[1]/EXT_CDE)=""
        def scac = currentTransaction?.MessageBody?.MessageProperties?.Party?.find{it.Role ='Carrier' && it.Identifier.attr_Type =='PartnerAlias'}?.Identifier?.toString()
        String validateSCAC = brUtil.checkSCAC(scac,conn)
        if (util.isEmpty(validateSCAC)){
            Map<String,String> errorKey = null
            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' Invalid SCAC: '+ scac +' on the input.']
        }

//        //check tp_integration_asso
//        def ReceiverID = util.getCarrierTpId(TP_ID, 'BR', scac, conn)
//        if (util.isEmpty(ReceiverID)) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Customer is not integrate with SCAC : '+ scac]
//            AppErrors.add(errorKey)
//        }
//
//        //$mapLocationConstant/root/Config[SegmentId="TransportationDetails.ConveyanceInformation" and SegmentField="ConveyanceName"]/ProcessId = "1"
//
//        def ConveyanceName = currentTransaction?.MessageBody?.MessageProperties?.TransportationDetails?.ConveyanceInformation?.find {it.Type == 'VesselName'}?.Identifier
//        def VoyageTripNumber = currentTransaction?.MessageBody?.MessageProperties?.TransportationDetails?.ConveyanceInformation?.find {it.Type == 'VoyageNumber'}?.Identifier
//        def EarliestDeparture = currentTransaction?.MessageBody?.MessageProperties?.Location?.find {it.Type == 'PlaceOfReceipt'}?.DateTime?.attr_Type =='EarliestDeparture'
//        def EstimatedArrival = currentTransaction?.MessageBody?.MessageProperties?.Location?.find {it.Type == 'PlaceOfDelivery'}?.DateTime?.attr_Type =='EstimatedArrival'
//        if ((util.isEmpty(ConveyanceName) || util.isEmpty(VoyageTripNumber)) && util.isEmpty(EarliestDeparture) && util.isEmpty(EstimatedArrival)){
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' Missing Conveyance Name + VoyageTrip Number or POR departure Date or FND Arrival Date.']
//            AppErrors.add(errorKey)
//        }
//
//        //$mapLocationConstant/root/Config[SegmentId="TransportationDetails.ConveyanceInformation" and SegmentField="ConveyanceName"]/ProcessId = "2" and
//
//        def ConveyanceName2 = currentTransaction?.MessageBody?.MessageProperties?.TransportationDetails?.ConveyanceInformation?.find {it.Type == 'VesselName'}?.Identifier
//        def VoyageTripNumber2 = currentTransaction?.MessageBody?.MessageProperties?.TransportationDetails?.ConveyanceInformation?.find {it.Type == 'VoyageNumber'}?.Identifier
//        def EarliestDeparture1 = currentTransaction?.MessageBody?.MessageProperties?.Location?.find {it.Type == 'PlaceOfReceipt'}?.DateTime?.attr_Type =='EarliestDeparture'
//        def EstimatedArrival1 = currentTransaction?.MessageBody?.MessageProperties?.Location?.find {it.Type == 'PlaceOfDelivery'}?.DateTime?.attr_Type =='EstimatedArrival'
//        if (currentTransaction?.Header?.TransactionStatus != 'Cancel' && (util.isEmpty(ConveyanceName2) || util.isEmpty(VoyageTripNumber2)) && util.isEmpty(EarliestDeparture1) && util.isEmpty(EstimatedArrival1)){
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' Missing Conveyance Name + VoyageTrip Number or POR departure Date or FND Arrival Date For Orginal and Change Booking.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="TransportationDetails.Location" and SegmentField="Location"]/ProcessId = "1" and
//
//        def POL = currentTransaction?.MessageBody?.MessageProperties?.TransportationDetails?.Location[0]?.find { it.Type == 'PortOfLoad'}
//        def POD = currentTransaction?.MessageBody?.MessageProperties?.TransportationDetails?.Location[0]?.find { it.Type == 'PlaceOfDelivery'}
//        if (util.isEmpty(POL) || util.isEmpty(POD)){
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' POL and POD is mandatory.']
//            AppErrors.add(errorKey)
//        }
//
//        //$mapLocationConstant/root/Config[SegmentId="TransportationDetails.Location" and SegmentField="Location"]/ProcessId = "2" and
//
//        def POL2 = currentTransaction?.MessageBody?.MessageProperties?.TransportationDetails?.Location[0]?.find { it.Type == 'PortOfLoad'}
//        def POD2 = currentTransaction?.MessageBody?.MessageProperties?.TransportationDetails?.Location[0]?.find { it.Type == 'PlaceOfDelivery'}
//        if (currentTransaction?.Header?.TransactionStatus != 'Cancel' && (util.isEmpty(POL) || util.isEmpty(POD))){
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' POL and POD is mandatory For Orginal and Change Booking.']
//            AppErrors.add(errorKey)
//        }
//
//
////        $mapLocationConstant/root/Config[SegmentId="MessageProperties.Location" and SegmentField="PlaceOfReceipt"]/ProcessId = "1" and
//
//        def POR = currentTransaction?.MessageBody?.MessageProperties?.Location?.find {it.Type == 'PlaceOfReceipt'}
//        if (util.isEmpty(POR)){
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' Missing PlaceOfReceipt.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="MessageProperties.Location" and SegmentField="PlaceOfReceipt"]/ProcessId = "2" and
//
//        def POR2 = currentTransaction?.MessageBody?.MessageProperties?.Location?.find {it.Type == 'PlaceOfReceipt'}
//        if ( currentTransaction?.Header?.TransactionStatus != 'Cancel' && util.isEmpty(POR) ) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' PlaceOfReceipt is mandatory For Orginal and Change Booking.']
//            AppErrors.add(errorKey)
//        }
//
//        //$mapLocationConstant/root/Config[SegmentId="MessageProperties.Location" and SegmentField="PlaceOfDelivery"]/ProcessId = "1" and
//
//        def POD1 = currentTransaction?.MessageBody?.MessageProperties?.Location?.find {it.Type == 'PlaceOfDelivery'}
//        if ( util.isEmpty(POD) ){
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' Missing PlaceOfDelivery.']
//            AppErrors.add(errorKey)
//        }
//
//        //$mapLocationConstant/root/Config[SegmentId="MessageProperties.Location" and SegmentField="PlaceOfDelivery"]/ProcessId = "2" and
//
//        def POD22 = currentTransaction?.MessageBody?.MessageProperties?.Location?.find {it.Type == 'PlaceOfDelivery'}
//        if ( currentTransaction?.Header?.TransactionStatus != 'Cancel' && util.isEmpty(POD2) ) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' PlaceOfDelivery is mandatory For Orginal and Change Booking.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="Parties.PartnerInformation" and SegmentField="PartnerRole"]/ProcessId = "1" and
//
//        def PartnerRole = currentTransaction?.MessageBody?.MessageProperties?.Party?.find {it.Role == 'Booker'}?.Identifier
//        def Carrier = currentTransaction?.MessageBody?.MessageProperties?.Party?.find {it.Role == 'Carrier'}?.Identifier
//        if ( util.isEmpty(PartnerRole) || util.isEmpty(Carrier) ) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' PartnerIdentifier is mandatory for Booking Party and Carrier.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="Parties.PartnerInformation" and SegmentField="PartnerRole"]/ProcessId = "2" and
//
//        def Booker2 = currentTransaction?.MessageBody?.MessageProperties?.Party?.find {it.Role == 'Booker'}?.Identifier
//        def Carrier2 = currentTransaction?.MessageBody?.MessageProperties?.Party?.find {it.Role == 'Carrier'}?.Identifier
//        if ( currentTransaction?.Header?.TransactionStatus != 'Cancel' && util.isEmpty(Booker2) || util.isEmpty(Carrier2) ) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' PartnerIdentifier is mandatory for Booking Party and Carrier.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="Parties.PartnerInformation" and SegmentField="PartnerRole_Carrier"]/ProcessId = "1" and
//
//        def Carrier1 = currentTransaction?.MessageBody?.MessageProperties?.Party?.find {it.Role == 'Carrier'}?.Identifier
//        if ( util.isEmpty(Carrier1) ) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' PartnerIdentifier is mandatory for Carrier.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="Parties.PartnerInformation" and SegmentField="ContactInformation"]/ProcessId = "1" and
//
//        def Carrier11 = currentTransaction?.MessageBody?.MessageProperties?.Party?.find {it.Role == 'Carrier'}
//        def Booker11 = currentTransaction?.MessageBody?.MessageProperties?.Party?.find {it.Role == 'Booker'}
//        if ( (util.notEmpty(Carrier11) && util.isEmpty(Carrier11.Contacts)) || (util.notEmpty(Booker11) && util.isEmpty(Booker11.Contacts))) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' ContactInformation is mandatory for Ship From and Ship To Party.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="Parties.PartnerInformation" and SegmentField="AddressInformation"]/ProcessId = "1" and
//        def Carrier111 = currentTransaction?.MessageBody?.MessageProperties?.Party?.find {it.Role == 'Carrier'}
//        def Booker111 = currentTransaction?.MessageBody?.MessageProperties?.Party?.find {it.Role == 'Booker'}
//        if ( (util.notEmpty(Carrier111) && util.isEmpty(Carrier111.Address)) || (util.notEmpty(Booker111) && util.isEmpty(Booker111.Address))) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' AddressInformation is mandatory for Ship From and Ship To Party.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="MessageDetails.GoodsDetails" and SegmentField="GoodsDetails"]/ProcessId = "1" and
//        def GoodsDetails = currentTransaction?.MessageBody?.MessageDetails?.GoodsDetails
//        if ( util.isEmpty(GoodsDetails) && currentTransaction?.Header?.TransactionStatus != 'Cancel' ) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' No GoodsDetails(Cargo), should have at least one.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="MessageDetails.GoodsDetails" and SegmentField="NumberOfPacakges"]/ProcessId = "1" and
//
//
////        $mapLocationConstant/root/Config[SegmentId="MessageDetails" and SegmentField="EquipmentDetails"]/ProcessId = "1" and
//        def EquipmentDetails = currentTransaction?.MessageBody?.MessageDetails?.EquipmentDetails
//        if ( util.isEmpty(EquipmentDetails) && currentTransaction?.Header?.TransactionStatus != 'Cancel' ) {
//            Map<String,String> errorKey = null
//            errorKey = errorKey = [CAT: 'PREP', TYPE: 'E' , VALUE: 'Transaction '+ currentTransactionIndex + ' EquipmentDetails is mandatory for new and update booking.']
//            AppErrors.add(errorKey)
//        }
//
////        $mapLocationConstant/root/Config[SegmentId="EquipmentDetails" and SegmentField="EquipmentTemperture"]/ProcessId = "1" and
//        currentTransaction?.MessageBody?.MessageDetails?.EquipmentDetails?.each { currentEquipmentDetails ->
//                //Check Temperature
//                if (currentEquipmentDetails?.EquipmentTemperature) {
//                    BigDecimal equipmentTemp = new BigDecimal(currentEquipmentDetails?.EquipmentTemperature?.toString().trim())
//                    if (equipmentTemp < -99 || equipmentTemp > 998) {
//                        AppErrors.add('Transaction ' + currentTransactionIndex + ' The range of Equirment Temperture should between -099~998')
//                    }
//                }
//
//                //check container Size Type
//                if (util.isEmpty(util.getConversionByExtCde(TP_ID, 'BR', 'I', 'ContainerType', currentEquipmentDetails.EquipmentType?.EquipmentTypeCode?.toString(), conn))) {
//                    AppErrors.add('Transaction ' + currentTransactionIndex + ' ContainerSizeType Invalid')
//                }
//
//                //String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, Connection conn
//        }


    return AppErrors
}

}

