package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_BRXML_CS2BRXML_ELITE
import cs.b2b.core.mapping.bean.edi.xml.br.inttra.*
import groovy.xml.MarkupBuilder
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.sql.Connection

class INTTRAv1_BR_UnitTest {
    CUS_BRXML_CS2BRXML_ELITE scripts = null
    StringWriter writer = null
    MarkupBuilder markupBuilder = null
    Node node = null;
    Message msg = null
    Header header = null
    MessageBody msgBody = null
    static Connection conn = null
    cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil()
    XmlBeanParser xmlBeanParser = new XmlBeanParser();

    @BeforeClass
    static void beforeClass() {
        ConnectionForTester testDBConn = new ConnectionForTester();
        conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
    }

    @AfterClass
    static void afterClass() {
        if (conn != null) {
            conn.close()
        }
    }

    @Before
    void before() {
        scripts = new CUS_BRXML_CS2BRXML_ELITE()
        initMarkupBuilder()
        msg = new Message()
        header = new Header()
        msgBody = new MessageBody()
        scripts.conn = conn
        scripts.currentSystemDt = new Date()
    }

    private void initMarkupBuilder() {
        def namespace = ['xmlns:ns0'    : 'http://www.cargosmart.com/bookingrequest',
                         'xmlns:ns1': 'http://www.cargosmart.com/common']
        node = null
        writer = new StringWriter()
        markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
        markupBuilder.createNode('ns0:BookingRequest', namespace)
        //outXml.nodeCompleted(null,BookingRequest)
    }

    @Test
    void checkPrep(){
        //Initial Setup
        def currentSystemDt = new Date()
        String xmlContent = new String()

        msgBody.MessageProperties = new MessageProperties()
        msgBody.MessageProperties.Parties = new Parties()

        Parties parties1 = new Parties()
        parties1.PartnerInformation = new ArrayList<PartnerInformation>()
        msgBody.MessageProperties.Parties = parties1

        //prepValidation(def current_Body, def currentTransactionIndex, Connection conn
        println scripts.prepValidation(msgBody,"1",conn)

    }

    @Test
    void testRequestTxnInfo() {
        //Initial Setup
        def currentSystemDt = new Date()
        //Case 1 - Check InterchangeTransactionID
        initMarkupBuilder()
        //2. Prepare input file content
        header.DocumentIdentifier = "1234567890"
        msg.Header = header
        //3. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //4. Compare Result
        String xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        def v_interchangeTransactionID = node.Request?.TransactionInformation?.InterchangeTransactionID.text()
        Assert.assertEquals("1234567890", v_interchangeTransactionID)
    }

    @Test
    void testRequestCSBookingRefNumber() {
        //Initial Setup
        def currentSystemDt = new Date()
        String xmlContent = new String()

        //Case 1 - Booking Number With OOLU initial
        MessageProperties msgProp = new MessageProperties()
        msgProp.ReferenceInformation.add(new ReferenceInformation(ReferenceInformation: "OOLU1234567890", attr_ReferenceType: "BookingNumber"))
        msgBody.MessageProperties = msgProp
        msg.MessageBody = msgBody
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("1234567890", node.Request?.GeneralInformation?.CarrierBookingNumber?.text())

        //Case 2 - Booking Number WithOut OOLU initial
        initMarkupBuilder()
        MessageProperties msgProp2 = new MessageProperties()
        msgProp2.ReferenceInformation.add(new ReferenceInformation(ReferenceInformation: "1234567890", attr_ReferenceType: "BookingNumber"))
        msgBody.MessageProperties = msgProp2
        msg.MessageBody = msgBody
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("1234567890", node.Request?.GeneralInformation?.CarrierBookingNumber?.text())

        //Case 3 - Booking Number With other SCAC Code initial
        initMarkupBuilder()
        MessageProperties msgProp3 = new MessageProperties()
        msgProp3.ReferenceInformation.add(new ReferenceInformation(ReferenceInformation: "MAEU1234567890", attr_ReferenceType: "BookingNumber"))
        msgBody.MessageProperties = msgProp3
        msg.MessageBody = msgBody
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("MAEU1234567890", node.Request?.GeneralInformation?.CarrierBookingNumber?.text())
    }

    @Test
    void testRequestActionType() {
        //Initial Setup
        def currentSystemDt = new Date()
        String xmlContent = new String()

        //When the Shipment Id.Msg Status is Original
        MessageProperties msgProp1 = new MessageProperties(ShipmentID: new ShipmentID(ShipmentIdentifier: new ShipmentIdentifier(attr_MessageStatus: "Original", ShipmentIdentifier: "Testing")))
        msgBody.MessageProperties = msgProp1
        msg.MessageBody = msgBody
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("NEW", node.Request?.GeneralInformation?.ActionType?.text())

//		//When the Shipment Id.Msg Status is Amendment
        initMarkupBuilder()
        MessageProperties msgProp2 = new MessageProperties(ShipmentID: new ShipmentID(ShipmentIdentifier: new ShipmentIdentifier(attr_MessageStatus: "Amendment", ShipmentIdentifier: "Testing")))
        msgBody.MessageProperties = msgProp2
        msg.MessageBody = msgBody
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("UPD", node.Request?.GeneralInformation?.ActionType?.text())
//
//		//When the Shipment Id.Msg Status is Cancellation
        initMarkupBuilder()
        MessageProperties msgProp3 = new MessageProperties(ShipmentID: new ShipmentID(ShipmentIdentifier: new ShipmentIdentifier(attr_MessageStatus: "Cancellation", ShipmentIdentifier: "Testing")))
        msgBody.MessageProperties = msgProp3
        msg.MessageBody = msgBody
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("CANCEL", node.Request?.GeneralInformation?.ActionType?.text())

//		//When the Shipment Id.Msg Status is null
        initMarkupBuilder()
        MessageProperties msgProp4 = new MessageProperties(ShipmentID: new ShipmentID(ShipmentIdentifier: new ShipmentIdentifier(attr_MessageStatus: "1", ShipmentIdentifier: "Testing")))
        msgBody.MessageProperties = msgProp4
        msg.MessageBody = msgBody
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("", node.Request?.GeneralInformation?.ActionType?.text())
    }

    @Test
    void testRequestSCAC() {
        //Initial Setup
        def currentSystemDt = new Date()
        String xmlContent = new String()

        //With scac
        MessageProperties msgProp1 = new MessageProperties()
        msgProp1.Parties = new Parties()
        msgProp1.Parties.PartnerInformation = new ArrayList<PartnerInformation>()
        msgProp1.Parties.PartnerInformation.add(new PartnerInformation(attr_PartnerRole: "Carrier", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "OOLU")))
        msgBody.MessageProperties = msgProp1
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("OOLU", node.Request?.GeneralInformation?.SCAC?.text())

        //WithOut scac
        initMarkupBuilder()
        MessageProperties msgProp2 = new MessageProperties()
        msgProp2.Parties = new Parties()
        msgProp2.Parties.PartnerInformation = new ArrayList<PartnerInformation>()
        msgProp2.Parties.PartnerInformation.add(new PartnerInformation(attr_PartnerRole: "Carrier", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "")))
        msgBody.MessageProperties = msgProp2
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("", node.Request?.GeneralInformation?.SCAC?.text())
    }

    @Test
    void testRequestRequestedInfo_By_RequestedDT() {
        //Initial Setup
        def currentSystemDt = new Date()
        String xmlContent = new String()

        header.Parties = new Parties()
        header.DateTime = new DateTime(attr_DateType: "Document", DateTime: "1703191208")
        header.Parties.PartnerInformation = new ArrayList<PartnerInformation>()
        header.Parties.PartnerInformation.add(new PartnerInformation(
                attr_PartnerRole: "Sender", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "CompanyName")))
        msg.Header = header

        MessageProperties msgProp = new MessageProperties()
        msgProp.DateTime = new DateTime(DateTime: "201703191207", attr_DateType: "Message")
        msgBody.MessageProperties = msgProp
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        //println xmlContent
        node = xmlParserToNode(xmlContent)
        //Test Requested By
        Assert.assertEquals("CompanyName",node.Request?.GeneralInformation?.Requested?.By?.text())
        //Test Requested DT
        Assert.assertEquals("2017-03-19T12:07:00+08:00",node.Request?.GeneralInformation?.Requested?.RequestedDT?.LocDT?.text())
    }

    @Test
    void testShipmentCargoType(){
        //Initial Setup
        def currentSystemDt = new Date()
        String xmlContent = new String()

        //Create msgDetails With HazardousGoods
        MessageDetails msgDetailsWithHazardousGoods = new MessageDetails()
        msgDetailsWithHazardousGoods.GoodsDetails  = new GoodsDetails()
        msgDetailsWithHazardousGoods.GoodsDetails.HazardousGoods = new ArrayList<HazardousGoods>()
        msgDetailsWithHazardousGoods.GoodsDetails.HazardousGoods.add(UNDGNumber: "Testing")

        //Create msgDetails WithOut HazardousGoods
        MessageDetails msgDetailsWithOutHazardousGoods = new MessageDetails()

        //Create MessageProperties with TemperatureControl
        MessageProperties msgPropWithTemperatureControl = new MessageProperties()
        msgPropWithTemperatureControl.Instructions = new Instructions()
        msgPropWithTemperatureControl.Instructions.ShipmentComments  = new ArrayList<ShipmentComments>()
        msgPropWithTemperatureControl.Instructions.ShipmentComments.add(attr_CommentType: "TemperatureControl")

        //Create msgDetails WithOut HazardousGoods
        MessageProperties msgPropWithOutTemperatureControl = new MessageProperties()

        //Case 1: Reefer Dangerous
        msgBody.MessageDetails = msgDetailsWithHazardousGoods
        msgBody.MessageProperties = msgPropWithTemperatureControl
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("RD", node.Request?.GeneralInformation?.ShipmentCargoType?.text())

        //Case 2: Dangerous
        initMarkupBuilder()
        msgBody.MessageDetails = msgDetailsWithHazardousGoods
        msgBody.MessageProperties = msgPropWithOutTemperatureControl
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("DG", node.Request?.GeneralInformation?.ShipmentCargoType?.text())

        //Case 3: Reefer
        initMarkupBuilder()
        msgBody.MessageDetails = msgDetailsWithOutHazardousGoods
        msgBody.MessageProperties = msgPropWithTemperatureControl
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("RF", node.Request?.GeneralInformation?.ShipmentCargoType?.text())

        //Case 4: General
        initMarkupBuilder()
        msgBody.MessageDetails = msgDetailsWithOutHazardousGoods
        msgBody.MessageProperties = msgPropWithOutTemperatureControl
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("GC", node.Request?.GeneralInformation?.ShipmentCargoType?.text())
    }

    @Test
    void testNotificationEmailAddress(){
        //Initial Setup
        def currentSystemDt = new Date()
        String xmlContent = new String()

        MessageProperties msgProp1 = new MessageProperties()
        msgProp1.ContactInformation = new ContactInformation(ContactName: "User1", CommunicationValue: new CommunicationValue(attr_CommunicationType: "Email",CommunicationValue: "abc@abc.com"))
        msgBody.MessageProperties = msgProp1
        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("abc@abc.com", node.Request?.GeneralInformation?.NotificationEmailAddress?.text())
    }

    @Test
    void testCustomerBookingReference(){
        //Initial Setup
        def currentSystemDt = new Date()
        String xmlContent = new String()

        MessageProperties msgProp1 = new MessageProperties(ShipmentID: new ShipmentID(ShipmentIdentifier: new ShipmentIdentifier(attr_MessageStatus: "Original", ShipmentIdentifier: "Testing")))
        msgBody.MessageProperties = msgProp1
        msg.MessageBody = msgBody

        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)
        Assert.assertEquals("Testing", node.Request?.GeneralInformation?.CustomerBookingReference?.text())
    }

    @Test
    void testPartyName(){
        //Initial Setup
        def currentSystemDt = new Date()
        String xmlContent = new String()

        msgBody.MessageProperties = new MessageProperties()
        msgBody.MessageProperties.Parties = new Parties()
        msgBody.MessageProperties.Parties = new Parties()

        Parties parties1 = new Parties()
        parties1.PartnerInformation = new ArrayList<PartnerInformation>()
        parties1.PartnerInformation.add(attr_PartnerRole: "Carrier", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "OOLU"), PartnerName: "IamCarrier")
        parties1.PartnerInformation.add(attr_PartnerRole: "BookingParty", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "OOLU"), PartnerName: "IamBookingParty")
        parties1.PartnerInformation.add(attr_PartnerRole: "FreightForwarder", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "OOLU"), PartnerName: "IamFreightForwarder")
        parties1.PartnerInformation.add(attr_PartnerRole: "Consignee", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "OOLU"), PartnerName: "IamConsignee")
        parties1.PartnerInformation.add(attr_PartnerRole: "Shipper", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "OOLU"), PartnerName: "IamShipper")
        parties1.PartnerInformation.add(attr_PartnerRole: "NotifyParty", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "OOLU"), PartnerName: "IamNotifyParty")
        parties1.PartnerInformation.add(attr_PartnerRole: "NotifyParty2", PartnerIdentifier: new PartnerIdentifier(attr_Agency: "AssignedBySender", PartnerIdentifier: "OOLU"), PartnerName: "IamNotifyParty2")
        msgBody.MessageProperties.Parties = parties1

        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        xmlContent = endNode(writer?.toString())
        node = xmlParserToNode(xmlContent)

        //Case 1: SCAC Code
        Assert.assertEquals("IamCarrier", node.Request?.Party?.find{it?.PartyType.text()=="CAR"}?.PartyName?.text())
        //Case 2: Booking Party
        Assert.assertEquals("IamBookingParty", node.Request?.Party?.find{it?.PartyType.text()=="BPT"}?.PartyName?.text())
        //Case 3: FreightForwarder
        Assert.assertEquals("IamFreightForwarder", node.Request?.Party?.find{it?.PartyType.text()=="FWD"}?.PartyName?.text())
        //Case 4: FreightForwarder
        Assert.assertEquals("IamNotifyParty2", node.Request?.Party?.find{it?.PartyType.text()=="ANP"}?.PartyName?.text())
        //Case 5: Consignee
        Assert.assertEquals("IamConsignee", node.Request?.Party?.find{it?.PartyType.text()=="CGN"}?.PartyName?.text())
        //Case 6: FreightForwarder
        Assert.assertEquals("IamNotifyParty", node.Request?.Party?.find{it?.PartyType.text()=="NPT"}?.PartyName?.text())
        //Case 7: Consignee
        Assert.assertEquals("IamShipper", node.Request?.Party?.find{it?.PartyType.text()=="SHP"}?.PartyName?.text())
    }

    //////////////////////After this line use XML directly
    @Test
    void testPartyContactBPT(){
        //Initial Setup
        def currentSystemDt = new Date()

        String xmlInputWithOutSender = """
<Message>
\t<Header/>
\t<MessageBody>
\t\t<MessageProperties>
\t\t\t<Parties>
\t\t\t\t<PartnerInformation PartnerRole="BookingParty">
\t\t\t\t\t<PartnerIdentifier Agency="AssignedBySender">TPId</PartnerIdentifier>
\t\t\t\t\t<PartnerName>IamBookingParty</PartnerName>
\t\t\t\t\t<ContactInformation>
\t\t\t\t\t\t<ContactName>BOOKING CONTACT</ContactName>
\t\t\t\t\t\t<CommunicationValue CommunicationType="Telephone">973-555-2000</CommunicationValue>
\t\t\t\t\t</ContactInformation>
\t\t\t\t</PartnerInformation>
\t\t\t</Parties>
\t\t</MessageProperties>
\t\t<MessageDetails/>
\t</MessageBody>
</Message>
		"""
        Message msg = xmlBeanParser.xmlParser(xmlInputWithOutSender, Message.class)
        MessageBody msgBody = msg.MessageBody

        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        node = xmlParserToNode(endNode(writer?.toString()))

        //Case 1: No PartnerRole ="Sender"
        Assert.assertEquals("BOOKING CONTACT", node.Request?.Party?.find{it?.PartyType.text()=="BPT"}?.Contact?.FirstName?.text())
        Assert.assertEquals("973-555-2000", node.Request?.Party?.find{it?.PartyType.text()=="BPT"}?.Contact?.ContactPhone?.Number?.text())



        String xmlInputWithSender = """
<Message>
\t<Header/>
\t<MessageBody>
\t\t<MessageProperties>
\t\t\t<Parties>
\t\t\t\t<PartnerInformation PartnerRole="BookingParty">
\t\t\t\t\t<PartnerIdentifier Agency="AssignedBySender">TPId</PartnerIdentifier>
\t\t\t\t\t<PartnerName>IamBookingParty</PartnerName>
\t\t\t\t\t<ContactInformation>
\t\t\t\t\t\t<ContactName>BOOKING CONTACT</ContactName>
\t\t\t\t\t\t<CommunicationValue CommunicationType="Telephone">973-555-2000BPT</CommunicationValue>
\t\t\t\t\t</ContactInformation>
\t\t\t\t</PartnerInformation>
\t\t\t\t<PartnerInformation PartnerRole="Sender">
\t\t\t\t\t<PartnerIdentifier Agency="AssignedBySender">TPId?</PartnerIdentifier>
\t\t\t\t\t<PartnerName>IamSender</PartnerName>
\t\t\t\t\t<ContactInformation>
\t\t\t\t\t\t<ContactName>BOOKING CONTACT UNDER Sender</ContactName>
\t\t\t\t\t\t<CommunicationValue CommunicationType="Telephone">973-555-2000Sender</CommunicationValue>
\t\t\t\t\t</ContactInformation>
\t\t\t\t</PartnerInformation>
\t\t\t</Parties>
\t\t</MessageProperties>
\t\t<MessageDetails/>
\t</MessageBody>
</Message>
		"""

        //Case 2: Have PartnerRole = Sender
        initMarkupBuilder()
        Message msg2 = xmlBeanParser.xmlParser(xmlInputWithSender, Message.class)
        MessageBody msgBody2  =msg2.MessageBody
        //1. Run mapping
        scripts.generateBody(msg2, msgBody2, currentSystemDt, markupBuilder)
        //2. Compare Result
        node = xmlParserToNode(endNode(writer?.toString()))
        Assert.assertEquals("BOOKING CONTACT UNDER Sender", node.Request?.Party?.find{it?.PartyType.text()=="BPT"}?.Contact?.FirstName?.text())
        Assert.assertEquals("973-555-2000Sender", node.Request?.Party?.find{it?.PartyType.text()=="BPT"}?.Contact?.ContactPhone?.Number?.text())
    }

    @Test
    void testPartyContactFWD(){
        //Initial Setup
        def currentSystemDt = new Date()

        String xmlInputWithOutSenderNoFwd = """
<Message>
\t<Header/>
\t<MessageBody>
\t\t<MessageProperties>
\t\t\t<Parties>
\t\t\t\t<PartnerInformation PartnerRole="BookingParty">
\t\t\t\t\t<PartnerIdentifier Agency="AssignedBySender">TPId</PartnerIdentifier>
\t\t\t\t\t<PartnerName>IamBookingParty</PartnerName>
\t\t\t\t\t<ContactInformation>
\t\t\t\t\t\t<ContactName>BOOKING CONTACT</ContactName>
\t\t\t\t\t\t<CommunicationValue CommunicationType="Telephone">973-555-2000</CommunicationValue>
\t\t\t\t\t</ContactInformation>
\t\t\t\t</PartnerInformation>
\t\t\t</Parties>
\t\t</MessageProperties>
\t\t<MessageDetails/>
\t</MessageBody>
</Message>
		"""
        Message msg = xmlBeanParser.xmlParser(xmlInputWithOutSenderNoFwd, Message.class)
        MessageBody msgBody = msg.MessageBody

        //1. Run mapping
        scripts.generateBody(msg, msgBody, currentSystemDt, markupBuilder)
        //2. Compare Result
        node = xmlParserToNode(endNode(writer?.toString()))

        //Case 1: No PartnerRole ="Sender"
        Assert.assertEquals("BOOKING CONTACT", node.Request?.Party?.find{it?.PartyType.text()=="FWD"}?.Contact?.FirstName?.text())
        Assert.assertEquals("973-555-2000", node.Request?.Party?.find{it?.PartyType.text()=="FWD"}?.Contact?.ContactPhone?.Number?.text())

        String xmlInputWithSenderNoFwd = """
<Message>
\t<Header/>
\t<MessageBody>
\t\t<MessageProperties>
\t\t\t<Parties>
\t\t\t\t<PartnerInformation PartnerRole="BookingParty">
\t\t\t\t\t<PartnerIdentifier Agency="AssignedBySender">TPId</PartnerIdentifier>
\t\t\t\t\t<PartnerName>IamBookingParty</PartnerName>
\t\t\t\t\t<ContactInformation>
\t\t\t\t\t\t<ContactName>BOOKING CONTACT</ContactName>
\t\t\t\t\t\t<CommunicationValue CommunicationType="Telephone">973-555-2000BPT</CommunicationValue>
\t\t\t\t\t</ContactInformation>
\t\t\t\t</PartnerInformation>
\t\t\t\t<PartnerInformation PartnerRole="Sender">
\t\t\t\t\t<PartnerIdentifier Agency="AssignedBySender">TPId?</PartnerIdentifier>
\t\t\t\t\t<PartnerName>IamSender</PartnerName>
\t\t\t\t\t<ContactInformation>
\t\t\t\t\t\t<ContactName>BOOKING CONTACT UNDER Sender</ContactName>
\t\t\t\t\t\t<CommunicationValue CommunicationType="Telephone">973-555-2000Sender</CommunicationValue>
\t\t\t\t\t</ContactInformation>
\t\t\t\t</PartnerInformation>
\t\t\t</Parties>
\t\t</MessageProperties>
\t\t<MessageDetails/>
\t</MessageBody>
</Message>
		"""

        //Case 2: Have PartnerRole = Sender
        initMarkupBuilder()
        Message msg2 = xmlBeanParser.xmlParser(xmlInputWithSenderNoFwd, Message.class)
        MessageBody msgBody2  =msg2.MessageBody
        //1. Run mapping
        scripts.generateBody(msg2, msgBody2, currentSystemDt, markupBuilder)
        //2. Compare Result
        node = xmlParserToNode(endNode(writer?.toString()))
        Assert.assertEquals("BOOKING CONTACT UNDER Sender", node.Request?.Party?.find{it?.PartyType.text()=="FWD"}?.Contact?.FirstName?.text())
        Assert.assertEquals("973-555-2000Sender", node.Request?.Party?.find{it?.PartyType.text()=="FWD"}?.Contact?.ContactPhone?.Number?.text())

        String xmlInputIncludeFWD = """
<Message>
\t<Header/>
\t<MessageBody>
\t\t<MessageProperties>
\t\t\t<Parties>
\t\t\t\t<PartnerInformation PartnerRole="BookingParty">
\t\t\t\t\t<PartnerIdentifier Agency="AssignedBySender">TPId</PartnerIdentifier>
\t\t\t\t\t<PartnerName>IamBookingParty</PartnerName>
\t\t\t\t\t<ContactInformation>
\t\t\t\t\t\t<ContactName>BOOKING CONTACT</ContactName>
\t\t\t\t\t\t<CommunicationValue CommunicationType="Telephone">973-555-2000BPT</CommunicationValue>
\t\t\t\t\t</ContactInformation>
\t\t\t\t</PartnerInformation>
\t\t\t\t<PartnerInformation PartnerRole="FreightForwarder">
\t\t\t\t\t<PartnerIdentifier Agency="AssignedBySender">TPId?</PartnerIdentifier>
\t\t\t\t\t<PartnerName>IamSender</PartnerName>
\t\t\t\t\t<ContactInformation>
\t\t\t\t\t\t<ContactName>BOOKING CONTACT UNDER FreightForwarder</ContactName>
\t\t\t\t\t\t<CommunicationValue CommunicationType="Telephone">973-555-2000FreightForwarder</CommunicationValue>
\t\t\t\t\t</ContactInformation>
\t\t\t\t</PartnerInformation>
\t\t\t</Parties>
\t\t</MessageProperties>
\t\t<MessageDetails/>
\t</MessageBody>
</Message>
		"""

        //Case 2: Have PartnerRole = Sender
        initMarkupBuilder()
        Message msg3 = xmlBeanParser.xmlParser(xmlInputWithSenderNoFwd, Message.class)
        MessageBody msgBody3  =msg2.MessageBody
        //1. Run mapping
        scripts.generateBody(msg3, msgBody3, currentSystemDt, markupBuilder)
        //2. Compare Result
        node = xmlParserToNode(endNode(writer?.toString()))
        Assert.assertEquals("BOOKING CONTACT UNDER Sender", node.Request?.Party?.find{it?.PartyType.text()=="FWD"}?.Contact?.FirstName?.text())
        Assert.assertEquals("973-555-2000Sender", node.Request?.Party?.find{it?.PartyType.text()=="FWD"}?.Contact?.ContactPhone?.Number?.text())
    }

    protected static Node xmlParserToNode(String testedXml) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.namespaceAware = false
        return xmlParser.parseText(testedXml)
    }

    private String endNode(String input) {
        input = input + "</ns0:BookingRequest>"
        return input
    }
}
