package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.bean.bl.FreightCharge
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2BLXML_XML_OOCLLOGISTICS
import groovy.xml.MarkupBuilder
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.sql.Connection

class OOCLLOGISTICS_BL_UnitTest {

	CUS_CS2BLXML_XML_OOCLLOGISTICS script = null;
	StringWriter writer = null
	MarkupBuilder markupBuilder = null
	Node node = null;
	Body body = null
	static Connection conn = null
	XmlBeanParser xmlBeanParser = new XmlBeanParser()
	BillOfLading bl = null
	
	@BeforeClass
	static void beforeClass(){
		ConnectionForTester testDBConn = new ConnectionForTester();
		conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
	}	
	
	@AfterClass
	static void afterClass(){
		
		if(conn != null){
			conn.close()
		}
	}
	
	@Before
	void before() {
		script = new CUS_CS2BLXML_XML_OOCLLOGISTICS();
		initMarkupBuilder()
		script.conn = conn
		script.TP_ID = 'OOCLLOGISTICS'
		script.currentSystemDt = new Date()
	}

	private void initMarkupBuilder() {
		node = null
		body = new Body();
		writer = new StringWriter()
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
	}

	@Test
	void testGeneralInfo() {
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
 <Body>
 <TransactionInformation>
   <Action>UPD</Action>
  </TransactionInformation>
  <GeneralInformation>
   <SCACCode>OOLU</SCACCode>
   <BLNumber>2528612841</BLNumber>
   <BLType>Sea WayBill</BLType>
  </GeneralInformation>
 </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("UPD",node.GeneralInfo.ActionType.text())
		Assert.assertEquals("2528612841",node.GeneralInfo.BLNumber.text())
		Assert.assertEquals("OOLU",node.GeneralInfo.SCAC.text())
		Assert.assertEquals("MIXED",node.GeneralInfo.PaymentStatus.text())

		initMarkupBuilder()
		script.generateBody(bl.Body[0], null, [new FreightCharge(ChargeType: "0")], null, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("COLLECT",node.GeneralInfo.PaymentStatus.text())

		initMarkupBuilder()
		script.generateBody(bl.Body[0], null, [new FreightCharge(ChargeType: "1")], null, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("PREPAID",node.GeneralInfo.PaymentStatus.text())

		initMarkupBuilder()
		script.generateBody(bl.Body[0], null, [new FreightCharge(ChargeType: "1"), new FreightCharge(ChargeType: "0")], null, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("MIXED",node.GeneralInfo.PaymentStatus.text())
	}

	@Test
	void testBLDetails_BookingInfo() {
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <GeneralInformation>
            <CarrierBookingNumber>2528612841</CarrierBookingNumber>
        </GeneralInformation>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("2528612841",node.BLDetails.BookingInfo.BookingNumber.text())
	}

	@Test
	void testBLDetails_UserReferences(){
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <GeneralInformation>
        	<BLNumber>2528612842</BLNumber>
            <CarrierBookingNumber>2528612841</CarrierBookingNumber>
        </GeneralInformation>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("2528612841",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'BN'}.ReferenceNumber.text())
		Assert.assertEquals("2528612842",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'BM'}.ReferenceNumber.text())


		xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <ExternalReference>
            <ns0:CSReferenceType xmlns:ns0="http://www.cargosmart.com/common">CTR</ns0:CSReferenceType>
            <ns0:ReferenceNumber xmlns:ns0="http://www.cargosmart.com/common">CTR</ns0:ReferenceNumber>
            <ns0:ReferenceDescription xmlns:ns0="http://www.cargosmart.com/common">In Transit Export Number</ns0:ReferenceDescription>
        </ExternalReference>
        <ExternalReference>
            <ns0:CSReferenceType xmlns:ns0="http://www.cargosmart.com/common">CR</ns0:CSReferenceType>
            <ns0:ReferenceNumber xmlns:ns0="http://www.cargosmart.com/common">CR</ns0:ReferenceNumber>
            <ns0:ReferenceDescription xmlns:ns0="http://www.cargosmart.com/common">Shipping Instruction Reference</ns0:ReferenceDescription>
        </ExternalReference>
        <ExternalReference>
            <ns0:CSReferenceType xmlns:ns0="http://www.cargosmart.com/common">EXPR</ns0:CSReferenceType>
            <ns0:ReferenceNumber xmlns:ns0="http://www.cargosmart.com/common">EXPR</ns0:ReferenceNumber>
            <ns0:ReferenceDescription xmlns:ns0="http://www.cargosmart.com/common">Forwarder Reference</ns0:ReferenceDescription>
        </ExternalReference>
        <ExternalReference>
            <ns0:CSReferenceType xmlns:ns0="http://www.cargosmart.com/common">FID</ns0:CSReferenceType>
            <ns0:ReferenceNumber xmlns:ns0="http://www.cargosmart.com/common">FID</ns0:ReferenceNumber>
            <ns0:ReferenceDescription xmlns:ns0="http://www.cargosmart.com/common">Forwarder Reference</ns0:ReferenceDescription>
        </ExternalReference>
        <ExternalReference>
            <ns0:CSReferenceType xmlns:ns0="http://www.cargosmart.com/common">FR</ns0:CSReferenceType>
            <ns0:ReferenceNumber xmlns:ns0="http://www.cargosmart.com/common">FR</ns0:ReferenceNumber>
            <ns0:ReferenceDescription xmlns:ns0="http://www.cargosmart.com/common">Forwarder Reference</ns0:ReferenceDescription>
        </ExternalReference>
        <ExternalReference>
            <ns0:CSReferenceType xmlns:ns0="http://www.cargosmart.com/common">INV</ns0:CSReferenceType>
            <ns0:ReferenceNumber xmlns:ns0="http://www.cargosmart.com/common">INV</ns0:ReferenceNumber>
            <ns0:ReferenceDescription xmlns:ns0="http://www.cargosmart.com/common">Forwarder Reference</ns0:ReferenceDescription>
        </ExternalReference>
        <ExternalReference>
            <ns0:CSReferenceType xmlns:ns0="http://www.cargosmart.com/common">PO</ns0:CSReferenceType>
            <ns0:ReferenceNumber xmlns:ns0="http://www.cargosmart.com/common">PO</ns0:ReferenceNumber>
            <ns0:ReferenceDescription xmlns:ns0="http://www.cargosmart.com/common">Forwarder Reference</ns0:ReferenceDescription>
        </ExternalReference>
        <ExternalReference>
            <ns0:CSReferenceType xmlns:ns0="http://www.cargosmart.com/common">SID</ns0:CSReferenceType>
            <ns0:ReferenceNumber xmlns:ns0="http://www.cargosmart.com/common">SID</ns0:ReferenceNumber>
            <ns0:ReferenceDescription xmlns:ns0="http://www.cargosmart.com/common">Forwarder Reference</ns0:ReferenceDescription>
        </ExternalReference>
    </Body>
</BillOfLading>
"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("CTR",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'CT'}.ReferenceNumber.text())
		Assert.assertEquals("CR",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'CR'}.ReferenceNumber.text())
		Assert.assertEquals("EXPR",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'RF'}.ReferenceNumber.text())
		Assert.assertEquals("FID",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'FI'}.ReferenceNumber.text())
		Assert.assertEquals("FR",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'FN'}.ReferenceNumber.text())
		Assert.assertEquals("INV",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'IK'}.ReferenceNumber.text())
		Assert.assertEquals("PO",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'PO'}.ReferenceNumber.text())
		Assert.assertEquals("SID",node.BLDetails.UserReferences.References.find{it.ReferenceType.text() == 'SI'}.ReferenceNumber.text())

	}

	@Test
	void testBLDetails_LegalParties(){
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <Party>
            <ns0:PartyType xmlns:ns0="http://www.cargosmart.com/common">SHP</ns0:PartyType>
            <ns0:PartyName xmlns:ns0="http://www.cargosmart.com/common">SDV Logistiques ( Canada) Inc.</ns0:PartyName>
            <ns0:CarrierCustomerCode xmlns:ns0="http://www.cargosmart.com/common">3126812007</ns0:CarrierCustomerCode>
            <ns0:Contact xmlns:ns0="http://www.cargosmart.com/common">
                <ns0:FirstName>Malek</ns0:FirstName>
                <ns0:LastName>Boucenna</ns0:LastName>
                <ns0:ContactPhone>
                    <ns0:CountryCode>1</ns0:CountryCode>
                    <ns0:AreaCode>514</ns0:AreaCode>
                    <ns0:Number>3384699</ns0:Number>
                </ns0:ContactPhone>
            </ns0:Contact>
            <ns0:Address xmlns:ns0="http://www.cargosmart.com/common">
                <ns0:City>Saint Laurent</ns0:City>
                <ns0:State>Quebec</ns0:State>
                <ns0:Country>CA</ns0:Country>
                <ns0:LocationCode>
                    <ns0:UNLocationCode>CASLA</ns0:UNLocationCode>
                </ns0:LocationCode>
                <ns0:PostalCode>H4S 1Y6</ns0:PostalCode>
                <ns0:AddressLines>
                    <ns0:AddressLine>3333 Douglas B Floreani</ns0:AddressLine>
                </ns0:AddressLines>
            </ns0:Address>
        </Party>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("SH",node.BLDetails.LegalParties.Party.PartyType.text())
		Assert.assertEquals("SDV Logistiques ( Canada) Inc.",node.BLDetails.LegalParties.Party.PartyName.text())
		Assert.assertEquals("3126812007",node.BLDetails.LegalParties.Party.CarrierCustomerCode.text())
		Assert.assertEquals("3333 Douglas B Floreani",node.BLDetails.LegalParties.Party.PartyLocation.Address.AddressLines.text())
		Assert.assertEquals("Saint Laurent",node.BLDetails.LegalParties.Party.PartyLocation.City.text())
		Assert.assertEquals("",node.BLDetails.LegalParties.Party.PartyLocation.County.text())
		Assert.assertEquals("Quebec",node.BLDetails.LegalParties.Party.PartyLocation.StateProvince.text())
		Assert.assertEquals("CA",node.BLDetails.LegalParties.Party.PartyLocation.CountryCode.text())
		Assert.assertEquals("",node.BLDetails.LegalParties.Party.PartyLocation.CountryName.text())
		Assert.assertEquals("CASLA",node.BLDetails.LegalParties.Party.PartyLocation.LocationCode.UNLocationCode.text())
		Assert.assertEquals("H4S 1Y6",node.BLDetails.LegalParties.Party.PartyLocation.PostalCode.text())
		Assert.assertEquals("Malek",node.BLDetails.LegalParties.Party.ContactPerson.FirstName.text())
		Assert.assertEquals("Boucenna",node.BLDetails.LegalParties.Party.ContactPerson.LastName.text())
		Assert.assertEquals("1",node.BLDetails.LegalParties.Party.ContactPerson.Phone.CountryCode.text())
		Assert.assertEquals("514",node.BLDetails.LegalParties.Party.ContactPerson.Phone.AreaCode.text())
		Assert.assertEquals("3384699",node.BLDetails.LegalParties.Party.ContactPerson.Phone.Number.text())


	}

	@Test
	void testBLDetails_RouteInformation(){
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <Route>
            <Haulage>
                <ns0:OutBound xmlns:ns0="http://www.cargosmart.com/common">C</ns0:OutBound>
                <ns0:InBound xmlns:ns0="http://www.cargosmart.com/common">M</ns0:InBound>
            </Haulage>
            <IsOBDropAndPoll>false</IsOBDropAndPoll>
            <ArrivalAtFinalHub Indicator="E">
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-25T14:02:00</ns0:GMT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="CET" CSTimeZone="CET">2013-01-25T15:02:00</ns0:LocDT>
            </ArrivalAtFinalHub>
            <FullReturnCutoffDT>
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-12T19:00:00</ns0:GMT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="EST" CSTimeZone="EST">2013-01-12T14:00:00</ns0:LocDT>
            </FullReturnCutoffDT>
            <DepartureDT>
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-17T11:50:00</ns0:GMT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="EST" CSTimeZone="EST">2013-01-17T06:50:00</ns0:LocDT>
            </DepartureDT>
            <ArrivalDT>
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-24T11:00:00</ns0:GMT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="CET" CSTimeZone="CET">2013-01-24T12:00:00</ns0:LocDT>
            </ArrivalDT>
            <POR>
                <ns0:CityDetails xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:City>Antwerp</ns0:City>
                    <ns0:County>Antwerpen</ns0:County>
                    <ns0:State>Vlaanderen</ns0:State>
                    <ns0:Country>Belgium</ns0:Country>
                    <ns0:LocationCode>
                        <ns0:UNLocationCode>BEANR</ns0:UNLocationCode>
                        <ns0:SchedKDType>K</ns0:SchedKDType>
                        <ns0:SchedKDCode>42305</ns0:SchedKDCode>
                    </ns0:LocationCode>
                </ns0:CityDetails>
                <cs:Facility xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                    <cs:FacilityCode>ANR03</cs:FacilityCode>
                    <cs:FacilityName>Europaterminal Kaai 869</cs:FacilityName>
                </cs:Facility>
                <cs:CSStandardCity xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                    <cs:CSParentCityID>2532764</cs:CSParentCityID>
                    <cs:CSCountryCode>BE</cs:CSCountryCode>
                    <cs:CSContinentCode>EU</cs:CSContinentCode>
                </cs:CSStandardCity>
            </POR>
            <FND>
                <ns0:CityDetails xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:City>Columbus</ns0:City>
                    <ns0:County>Franklin</ns0:County>
                    <ns0:State>Ohio</ns0:State>
                    <ns0:Country>United States</ns0:Country>
                    <ns0:LocationCode>
                        <ns0:UNLocationCode>USCMH</ns0:UNLocationCode>
                        <ns0:SchedKDType>D</ns0:SchedKDType>
                        <ns0:SchedKDCode>4103</ns0:SchedKDCode>
                    </ns0:LocationCode>
                </ns0:CityDetails>
                <cs:CSStandardCity xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                    <cs:CSParentCityID>2534264</cs:CSParentCityID>
                    <cs:CSStateCode>OH</cs:CSStateCode>
                    <cs:CSCountryCode>US</cs:CSCountryCode>
                    <cs:CSContinentCode>NA</cs:CSContinentCode>
                </cs:CSStandardCity>
            </FND>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:PortName>Montreal</ns0:PortName>
                    <ns0:PortCode>MTR</ns0:PortCode>
                    <ns0:City>Montreal</ns0:City>
                    <ns0:State>Quebec</ns0:State>
                    <ns0:LocationCode>
                        <ns0:UNLocationCode>CAMTR</ns0:UNLocationCode>
                        <ns0:SchedKDType>K</ns0:SchedKDType>
                        <ns0:SchedKDCode>1822</ns0:SchedKDCode>
                    </ns0:LocationCode>
                    <ns0:Country>Canada</ns0:Country>
                    <ns0:CSPortID>38</ns0:CSPortID>
                    <ns0:CSCountryCode>CA</ns0:CSCountryCode>
                </ns0:Port>
                <cs:Facility xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                    <cs:FacilityCode>MTR02</cs:FacilityCode>
                    <cs:FacilityName>Cast Terminal</cs:FacilityName>
                </cs:Facility>
                <OutboundSVVD>
                    <ns0:Service xmlns:ns0="http://www.cargosmart.com/common">GEX2</ns0:Service>
                    <ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">MOX</ns0:Vessel>
                    <ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">MONTREAL EXPRESS</ns0:VesselName>
                    <ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">214</ns0:Voyage>
                    <ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
                    <ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">9253741</ns0:LloydsNumber>
                    <ns0:CallSign xmlns:ns0="http://www.cargosmart.com/common">MAHG5</ns0:CallSign>
                    <ns0:VesselNationality xmlns:ns0="http://www.cargosmart.com/common">United Kingdom</ns0:VesselNationality>
                    <DirectionName>East</DirectionName>
                    <VesselNationalityCode>GB</VesselNationalityCode>
                </OutboundSVVD>
                <DepartureDT>
                    <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="EST">2013-01-17T06:50:00</ns0:LocDT>
                </DepartureDT>
                <CSStateCode>QC</CSStateCode>
                <CSParentCityID>2536840</CSParentCityID>
            </FirstPOL>
            <LastPOD>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:PortName>Montreal</ns0:PortName>
                    <ns0:PortCode>MTR</ns0:PortCode>
                    <ns0:City>Montreal</ns0:City>
                    <ns0:State>Quebec</ns0:State>
                    <ns0:LocationCode>
                        <ns0:UNLocationCode>CAMTR</ns0:UNLocationCode>
                        <ns0:SchedKDType>K</ns0:SchedKDType>
                        <ns0:SchedKDCode>1822</ns0:SchedKDCode>
                    </ns0:LocationCode>
                    <ns0:Country>Canada</ns0:Country>
                    <ns0:CSPortID>38</ns0:CSPortID>
                    <ns0:CSCountryCode>CA</ns0:CSCountryCode>
                </ns0:Port>
                <cs:Facility xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                    <cs:FacilityCode>MTR02</cs:FacilityCode>
                    <cs:FacilityName>Cast Terminal</cs:FacilityName>
                </cs:Facility>
                <InboundSVVD>
                    <ns0:Service xmlns:ns0="http://www.cargosmart.com/common">GEX2</ns0:Service>
                    <ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">MOX</ns0:Vessel>
                    <ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">MONTREAL EXPRESS</ns0:VesselName>
                    <ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">214</ns0:Voyage>
                    <ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
                    <ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">9253741</ns0:LloydsNumber>
                    <ns0:CallSign xmlns:ns0="http://www.cargosmart.com/common">MAHG5</ns0:CallSign>
                    <ns0:VesselNationality xmlns:ns0="http://www.cargosmart.com/common">United Kingdom</ns0:VesselNationality>
                    <DirectionName>East</DirectionName>
                    <VesselNationalityCode>GB</VesselNationalityCode>
                </InboundSVVD>
                <ArrivalDT>
                    <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="CET">2013-01-24T12:00:00</ns0:LocDT>
                </ArrivalDT>
                <CSStateCode>QC</CSStateCode>
                <CSParentCityID>2532764</CSParentCityID>
            </LastPOD>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:Port>
                        <ns0:PortName>Montreal</ns0:PortName>
                        <ns0:PortCode>MTR</ns0:PortCode>
                        <ns0:City>Montreal</ns0:City>
                        <ns0:State>Quebec</ns0:State>
                        <ns0:LocationCode>
                            <ns0:UNLocationCode>CAMTR</ns0:UNLocationCode>
                            <ns0:SchedKDType>K</ns0:SchedKDType>
                            <ns0:SchedKDCode>1822</ns0:SchedKDCode>
                        </ns0:LocationCode>
                        <ns0:Country>Canada</ns0:Country>
                        <ns0:CSPortID>38</ns0:CSPortID>
                        <ns0:CSCountryCode>CA</ns0:CSCountryCode>
                    </ns0:Port>
                    <ns0:Facility>
                        <ns0:FacilityCode>MTR02</ns0:FacilityCode>
                        <ns0:FacilityName>Cast Terminal</ns0:FacilityName>
                    </ns0:Facility>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:Port>
                        <ns0:PortName>Antwerp</ns0:PortName>
                        <ns0:PortCode>ANR</ns0:PortCode>
                        <ns0:City>Antwerpen</ns0:City>
                        <ns0:County>Antwerpen</ns0:County>
                        <ns0:State>Vlaanderen</ns0:State>
                        <ns0:LocationCode>
                            <ns0:UNLocationCode>BEANR</ns0:UNLocationCode>
                            <ns0:SchedKDType>K</ns0:SchedKDType>
                            <ns0:SchedKDCode>42305</ns0:SchedKDCode>
                        </ns0:LocationCode>
                        <ns0:Country>Belgium</ns0:Country>
                        <ns0:CSPortID>20</ns0:CSPortID>
                        <ns0:CSCountryCode>BE</ns0:CSCountryCode>
                    </ns0:Port>
                    <ns0:Facility>
                        <ns0:FacilityCode>ANR03</ns0:FacilityCode>
                        <ns0:FacilityName>Europaterminal Kaai 869</ns0:FacilityName>
                    </ns0:Facility>
                </ns0:POD>
                <ns0:SVVD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:Loading>
                        <ns0:Service>GEX2</ns0:Service>
                        <ns0:Vessel>MOX</ns0:Vessel>
                        <ns0:VesselName>MONTREAL EXPRESS</ns0:VesselName>
                        <ns0:Voyage>214</ns0:Voyage>
                        <ns0:Direction>E</ns0:Direction>
                        <ns0:LloydsNumber>9253741</ns0:LloydsNumber>
                        <ns0:CallSign>MAHG5</ns0:CallSign>
                        <ns0:CallNumber>1</ns0:CallNumber>
                        <ns0:VesselNationality>United Kingdom</ns0:VesselNationality>
                    </ns0:Loading>
                    <ns0:Discharge>
                        <ns0:Service>GEX2</ns0:Service>
                        <ns0:Vessel>MOX</ns0:Vessel>
                        <ns0:VesselName>MONTREAL EXPRESS</ns0:VesselName>
                        <ns0:Voyage>214</ns0:Voyage>
                        <ns0:Direction>E</ns0:Direction>
                        <ns0:LloydsNumber>9253741</ns0:LloydsNumber>
                        <ns0:CallSign>MAHG5</ns0:CallSign>
                        <ns0:CallNumber>1</ns0:CallNumber>
                        <ns0:VesselNationality>United Kingdom</ns0:VesselNationality>
                    </ns0:Discharge>
                    <ns0:VesselVoyageType>OUTBOUND</ns0:VesselVoyageType>
                </ns0:SVVD>
                <cs:DepartureDT xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading" Indicator="A">
                    <cs:GMT>2013-01-17T11:50:00</cs:GMT>
                    <cs:LocDT CSTimeZone="EST" TimeZone="EST">2013-01-17T06:50:00</cs:LocDT>
                </cs:DepartureDT>
                <cs:DepartureDT xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading" Indicator="E">
                    <cs:GMT>2013-01-17T13:00:00</cs:GMT>
                    <cs:LocDT CSTimeZone="EST" TimeZone="EST">2013-01-17T08:00:00</cs:LocDT>
                </cs:DepartureDT>
                <cs:ArrivalDT xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading" Indicator="E">
                    <cs:GMT>2013-01-24T11:00:00</cs:GMT>
                    <cs:LocDT CSTimeZone="CET" TimeZone="CET">2013-01-24T12:00:00</cs:LocDT>
                </cs:ArrivalDT>
                <CarrierExtractDT>
                    <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="HKT">2013-01-21T09:59:45</ns0:LocDT>
                </CarrierExtractDT>
                <LoadingDirectionName>East</LoadingDirectionName>
                <LoadingVesselNationalityCode>GB</LoadingVesselNationalityCode>
                <DischargeDirectionName>East</DischargeDirectionName>
                <DischargeVesselNationalityCode>GB</DischargeVesselNationalityCode>
                <POLCSStateCode>QC</POLCSStateCode>
                <POLCSParentCityID>2536840</POLCSParentCityID>
                <PODCSParentCityID>2532764</PODCSParentCityID>
            </OceanLeg>
            <ActualCargoReceiptDT>
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-11T19:56:00+08:00</ns0:GMT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="" CSTimeZone="EST">2013-01-11T14:56:00+08:00</ns0:LocDT>
            </ActualCargoReceiptDT>
        </Route>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("20130112140000",node.BLDetails.RouteInformation.FullReturnCutoff.text())
		Assert.assertEquals("20130125150200",node.BLDetails.RouteInformation.ArrivalAtFinalHub.text())
		Assert.assertEquals("C",node.BLDetails.RouteInformation.Haulage.Outbound.text())
		Assert.assertEquals("M",node.BLDetails.RouteInformation.Haulage.Inbound.text())
		//POR
		Assert.assertEquals("POR",node.BLDetails.RouteInformation.Location[0].FunctionCode.text())
		Assert.assertEquals("ANR03",node.BLDetails.RouteInformation.Location[0].FacilityCode.text())
		Assert.assertEquals("Europaterminal Kaai 869",node.BLDetails.RouteInformation.Location[0].FacilityName.text())
		Assert.assertEquals("Antwerp",node.BLDetails.RouteInformation.Location[0].LocationDetails.City.text())
		Assert.assertEquals("Antwerpen",node.BLDetails.RouteInformation.Location[0].LocationDetails.County.text())
		Assert.assertEquals("Vlaanderen",node.BLDetails.RouteInformation.Location[0].LocationDetails.StateProvince.text())
		Assert.assertEquals("BE",node.BLDetails.RouteInformation.Location[0].LocationDetails.CountryCode.text())
		Assert.assertEquals("Belgium",node.BLDetails.RouteInformation.Location[0].LocationDetails.CountryName.text())
		Assert.assertEquals("BEANR",node.BLDetails.RouteInformation.Location[0].LocationDetails.LocationCode.UNLocationCode.text())
		//FND
		Assert.assertEquals("FND",node.BLDetails.RouteInformation.Location[1].FunctionCode.text())
		Assert.assertEquals("",node.BLDetails.RouteInformation.Location[1].FacilityCode.text())
		Assert.assertEquals("",node.BLDetails.RouteInformation.Location[1].FacilityName.text())
		Assert.assertEquals("Columbus",node.BLDetails.RouteInformation.Location[1].LocationDetails.City.text())
		Assert.assertEquals("Franklin",node.BLDetails.RouteInformation.Location[1].LocationDetails.County.text())
		Assert.assertEquals("Ohio",node.BLDetails.RouteInformation.Location[1].LocationDetails.StateProvince.text())
		Assert.assertEquals("US",node.BLDetails.RouteInformation.Location[1].LocationDetails.CountryCode.text())
		Assert.assertEquals("United States",node.BLDetails.RouteInformation.Location[1].LocationDetails.CountryName.text())
		Assert.assertEquals("USCMH",node.BLDetails.RouteInformation.Location[1].LocationDetails.LocationCode.UNLocationCode.text())
		//POL
		Assert.assertEquals("POL",node.BLDetails.RouteInformation.Location[2].FunctionCode.text())
		Assert.assertEquals("MTR02",node.BLDetails.RouteInformation.Location[2].FacilityCode.text())
		Assert.assertEquals("Cast Terminal",node.BLDetails.RouteInformation.Location[2].FacilityName.text())
		Assert.assertEquals("Montreal",node.BLDetails.RouteInformation.Location[2].LocationDetails.City.text())
		Assert.assertEquals("",node.BLDetails.RouteInformation.Location[2].LocationDetails.County.text())
		Assert.assertEquals("Quebec",node.BLDetails.RouteInformation.Location[2].LocationDetails.StateProvince.text())
		Assert.assertEquals("CA",node.BLDetails.RouteInformation.Location[2].LocationDetails.CountryCode.text())
		Assert.assertEquals("Canada",node.BLDetails.RouteInformation.Location[2].LocationDetails.CountryName.text())
		Assert.assertEquals("CAMTR",node.BLDetails.RouteInformation.Location[2].LocationDetails.LocationCode.UNLocationCode.text())
		Assert.assertEquals("1822",node.BLDetails.RouteInformation.Location[2].LocationDetails.LocationCode.SchedKDCode.text())
		Assert.assertEquals("K", node.BLDetails.RouteInformation.Location[2].LocationDetails.LocationCode.SchedKDCode.@Type[0])
		Assert.assertEquals("20130117065000", node.BLDetails.RouteInformation.Location[2].EventDate.Departure.text())
		Assert.assertEquals("EST", node.BLDetails.RouteInformation.Location[2].EventDate.Departure.@TimeZone[0])
		Assert.assertEquals("0", node.BLDetails.RouteInformation.Location[2].EventDate.Departure.@EstActIndicator[0])
		Assert.assertEquals("Loading", node.BLDetails.RouteInformation.Location[2].VesselVoyageInformation.VoyageEvent.text())
		Assert.assertEquals("GEX2", node.BLDetails.RouteInformation.Location[2].VesselVoyageInformation.ServiceName.@Code[0])
		Assert.assertEquals("214E", node.BLDetails.RouteInformation.Location[2].VesselVoyageInformation.VoyageNumberDirection.text())
		Assert.assertEquals("MOX", node.BLDetails.RouteInformation.Location[2].VesselVoyageInformation.VesselInformation.VesselCode.text())
		Assert.assertEquals("MONTREAL EXPRESS", node.BLDetails.RouteInformation.Location[2].VesselVoyageInformation.VesselInformation.VesselName.text())
		Assert.assertEquals("United Kingdom", node.BLDetails.RouteInformation.Location[2].VesselVoyageInformation.VesselInformation.VesselRegistrationCountry.text())
		//POD
		Assert.assertEquals("POD",node.BLDetails.RouteInformation.Location[3].FunctionCode.text())
		Assert.assertEquals("MTR02",node.BLDetails.RouteInformation.Location[3].FacilityCode.text())
		Assert.assertEquals("Cast Terminal",node.BLDetails.RouteInformation.Location[3].FacilityName.text())
		Assert.assertEquals("Montreal",node.BLDetails.RouteInformation.Location[3].LocationDetails.City.text())
		Assert.assertEquals("",node.BLDetails.RouteInformation.Location[3].LocationDetails.County.text())
		Assert.assertEquals("Quebec",node.BLDetails.RouteInformation.Location[3].LocationDetails.StateProvince.text())
		Assert.assertEquals("CA",node.BLDetails.RouteInformation.Location[3].LocationDetails.CountryCode.text())
		Assert.assertEquals("Canada",node.BLDetails.RouteInformation.Location[3].LocationDetails.CountryName.text())
		Assert.assertEquals("CAMTR",node.BLDetails.RouteInformation.Location[3].LocationDetails.LocationCode.UNLocationCode.text())
		Assert.assertEquals("1822",node.BLDetails.RouteInformation.Location[3].LocationDetails.LocationCode.SchedKDCode.text())
		Assert.assertEquals("K", node.BLDetails.RouteInformation.Location[3].LocationDetails.LocationCode.SchedKDCode.@Type[0])
		Assert.assertEquals("20130124120000", node.BLDetails.RouteInformation.Location[3].EventDate.Arrival.text())
		Assert.assertEquals("CET", node.BLDetails.RouteInformation.Location[3].EventDate.Arrival.@TimeZone[0])
		Assert.assertEquals("0", node.BLDetails.RouteInformation.Location[3].EventDate.Arrival.@EstActIndicator[0])
		Assert.assertEquals("Discharge", node.BLDetails.RouteInformation.Location[3].VesselVoyageInformation.VoyageEvent.text())
		Assert.assertEquals("GEX2", node.BLDetails.RouteInformation.Location[3].VesselVoyageInformation.ServiceName.@Code[0])
		Assert.assertEquals("214E", node.BLDetails.RouteInformation.Location[3].VesselVoyageInformation.VoyageNumberDirection.text())
		Assert.assertEquals("MOX", node.BLDetails.RouteInformation.Location[3].VesselVoyageInformation.VesselInformation.VesselCode.text())
		Assert.assertEquals("MONTREAL EXPRESS", node.BLDetails.RouteInformation.Location[3].VesselVoyageInformation.VesselInformation.VesselName.text())
		Assert.assertEquals("United Kingdom", node.BLDetails.RouteInformation.Location[3].VesselVoyageInformation.VesselInformation.VesselRegistrationCountry.text())
	}

	@Test
	void testBLDetails_EquipmentInformation(){
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
    	<GeneralInformation>
   			<SCACCode>OOLU</SCACCode>
   			<BLNumber>2528612841</BLNumber>
   			<BLType>Sea WayBill</BLType>
  		</GeneralInformation>
        <Container>
            <ns0:ContainerNumber xmlns:ns0="http://www.cargosmart.com/common">OOLU617238</ns0:ContainerNumber>
            <ns0:ContainerCheckDigit xmlns:ns0="http://www.cargosmart.com/common">6</ns0:ContainerCheckDigit>
            <ns0:CSContainerSizeType xmlns:ns0="http://www.cargosmart.com/common">45RE</ns0:CSContainerSizeType>
            <ns0:CarrCntrSizeType xmlns:ns0="http://www.cargosmart.com/common">45R1</ns0:CarrCntrSizeType>
            <ns0:IsSOC xmlns:ns0="http://www.cargosmart.com/common">false</ns0:IsSOC>
            <cs:GrossWeight xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                <cs:Weight>15988.95</cs:Weight>
                <cs:WeightUnit>KGS</cs:WeightUnit>
            </cs:GrossWeight>
            <cs:Seal xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                <cs:SealType>SH</cs:SealType>
                <cs:SealNumber>ul4255573</cs:SealNumber>
            </cs:Seal>
            <cs:Haulage xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                <cs:OutBound>C</cs:OutBound>
                <cs:InBound>M</cs:InBound>
            </cs:Haulage>
            <TrafficMode>
                <ns0:OutBound xmlns:ns0="http://www.cargosmart.com/common">FCL</ns0:OutBound>
                <ns0:InBound xmlns:ns0="http://www.cargosmart.com/common">FCL</ns0:InBound>
            </TrafficMode>
            <DisplaySequenceNumber>1</DisplaySequenceNumber>
            <PieceCount>
                <ns0:PieceCount xmlns:ns0="http://www.cargosmart.com/common">77</ns0:PieceCount>
                <ns0:PieceCountUnit xmlns:ns0="http://www.cargosmart.com/common">RL</ns0:PieceCountUnit>
            </PieceCount>
            <InlandMoveIDPrefix>V16</InlandMoveIDPrefix>
            <SIDeclaredGrossWeight>
                <ns0:Weight xmlns:ns0="http://www.cargosmart.com/common">15988.95</ns0:Weight>
                <ns0:WeightUnit xmlns:ns0="http://www.cargosmart.com/common">KGS</ns0:WeightUnit>
            </SIDeclaredGrossWeight>
            <LastTransferToBookingNo>2528612841</LastTransferToBookingNo>
            <BLSeal>
                <ns0:SealType xmlns:ns0="http://www.cargosmart.com/common">SH</ns0:SealType>
                <ns0:SealNumber xmlns:ns0="http://www.cargosmart.com/common">ul4255573</ns0:SealNumber>
            </BLSeal>
            <CS1ContainerSizeType>40RQ</CS1ContainerSizeType>
            <OOCLCntrSizeType>40RQ</OOCLCntrSizeType>
            <CPRSWayBillNumber> </CPRSWayBillNumber>
        </Container>
        <Cargo>
            <ns0:CargoNature xmlns:ns0="http://www.cargosmart.com/common">RF</ns0:CargoNature>
            <ns0:CargoDescription xmlns:ns0="http://www.cargosmart.com/common">PLASTIC FILMS</ns0:CargoDescription>
            <cs:Packaging xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                <cs:PackageType>RL</cs:PackageType>
                <cs:PackageQty>77</cs:PackageQty>
                <cs:PackageDesc>1X40' RQ SLAC 77 ROLLS ON 24 PALLETS PLASTIC FILMS HS CODE; 392030 Temperature set at 20.0 c (68.0 f)Fresh Air Exchange rate set at 0 CubicMeterPerHour (0.0 CubicFeetPerMinute)</cs:PackageDesc>
            </cs:Packaging>
            <cs:GrossWeight xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                <cs:Weight>15988.95</cs:Weight>
                <cs:WeightUnit>KGS</cs:WeightUnit>
            </cs:GrossWeight>
            <ns0:MarksAndNumbers xmlns:ns0="http://www.cargosmart.com/common">
                <ns0:SeqNumber>1</ns0:SeqNumber>
                <ns0:MarksAndNumbersLine>AES NO;</ns0:MarksAndNumbersLine>
            </ns0:MarksAndNumbers>
            <ns0:MarksAndNumbers xmlns:ns0="http://www.cargosmart.com/common">
                <ns0:SeqNumber>2</ns0:SeqNumber>
                <ns0:MarksAndNumbersLine>X20130111020928</ns0:MarksAndNumbersLine>
            </ns0:MarksAndNumbers>
            <DisplaySequenceNumber>1</DisplaySequenceNumber>
            <CurrentContainerNumber>OOLU617238</CurrentContainerNumber>
            <CurrentContainerCheckDigit>6</CurrentContainerCheckDigit>
            <TrafficModel>
                <ns0:OutBound xmlns:ns0="http://www.cargosmart.com/common">FCL</ns0:OutBound>
                <ns0:InBound xmlns:ns0="http://www.cargosmart.com/common">FCL</ns0:InBound>
            </TrafficModel>
            <CgoPkgName>Roll</CgoPkgName>
            <CgoPkgCodeCount>1</CgoPkgCodeCount>
            <Seal>
                <ns0:SealType xmlns:ns0="http://www.cargosmart.com/common">SH</ns0:SealType>
                <ns0:SealNumber xmlns:ns0="http://www.cargosmart.com/common">ul4255573</ns0:SealNumber>
            </Seal>
            <ArticleNumber>-1</ArticleNumber>
            <ReeferCargoSpec>
                <ns0:AtmosphereType xmlns:ns0="http://www.cargosmart.com/common">NP</ns0:AtmosphereType>
                <cs:Temperature xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                    <cs:Temperature>20</cs:Temperature>
                    <cs:TemperatureUnit>C</cs:TemperatureUnit>
                </cs:Temperature>
                <cs:Ventilation xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                    <cs:Ventilation>0</cs:Ventilation>
                    <cs:VentilationUnit>cbmPerHour</cs:VentilationUnit>
                </cs:Ventilation>
                <ns0:GensetType xmlns:ns0="http://www.cargosmart.com/common">CO</ns0:GensetType>
                <ns0:Remarks xmlns:ns0="http://www.cargosmart.com/common">genset: yes</ns0:Remarks>
                <ns0:CO2 xmlns:ns0="http://www.cargosmart.com/common">0</ns0:CO2>
                <ns0:O2 xmlns:ns0="http://www.cargosmart.com/common">0</ns0:O2>
                <ns0:IsPreCoolingReq xmlns:ns0="http://www.cargosmart.com/common">false</ns0:IsPreCoolingReq>
                <ns0:SensitiveCargoDesc xmlns:ns0="http://www.cargosmart.com/common">123456789012345678901</ns0:SensitiveCargoDesc>
                <ns0:IsControlledAtmosphere xmlns:ns0="http://www.cargosmart.com/common">false</ns0:IsControlledAtmosphere>
                <ns0:IsReeferOperational xmlns:ns0="http://www.cargosmart.com/common">true</ns0:IsReeferOperational>
                <RFPackageUnit>
                    <ns0:PackageSeqNumber xmlns:ns0="http://www.cargosmart.com/common">1</ns0:PackageSeqNumber>
                    <ns0:PackageUnitQuantity xmlns:ns0="http://www.cargosmart.com/common">77</ns0:PackageUnitQuantity>
                    <ns0:PackageUnitDescription xmlns:ns0="http://www.cargosmart.com/common">1X40' RQ SLAC 77 ROLLS ON 24 PALLETS PLASTIC FILMS HS CODE; 392030 Temperature set at 20.0 c (68.0 f) Fresh Air Exchange rate set at 0 CubicMeterPerHour (0.0 CubicFeetPerMinute)</ns0:PackageUnitDescription>
                </RFPackageUnit>
            </ReeferCargoSpec>
        </Cargo>
        <DnD Type="Det/Dem">
            <ContainerNumber>OOLU617238</ContainerNumber>
            <ContainerCheckDigit>6</ContainerCheckDigit>
            <FreeTimeStartDT>
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2014-12-19T16:00:00</ns0:GMT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="PHT">2014-12-20T00:00:00</ns0:LocDT>
            </FreeTimeStartDT>
            <FreeTimeEndDT Indicator="A">
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2014-12-29T15:59:00</ns0:GMT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="PHT">2014-12-29T23:59:00</ns0:LocDT>
            </FreeTimeEndDT>
            <FreeTime>3</FreeTime>
            <FreeTimeType>W</FreeTimeType>
        </DnD>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("0",node.BLDetails.EquipmentInformation.Containers.SOCIndicator.text())
		Assert.assertEquals("45R0",node.BLDetails.EquipmentInformation.Containers.ContainerType.text())
		Assert.assertEquals("OOLU617238",node.BLDetails.EquipmentInformation.Containers.ContainerNumber.text())
		Assert.assertEquals("6",node.BLDetails.EquipmentInformation.Containers.ContainerNumber.@CheckDigit[0])
		Assert.assertEquals("ul4255573",node.BLDetails.EquipmentInformation.Containers.SealNumber.text())
		Assert.assertEquals("SH",node.BLDetails.EquipmentInformation.Containers.SealNumber.@Type[0])
		Assert.assertEquals("15988.95",node.BLDetails.EquipmentInformation.Containers.Weight.text())
		Assert.assertEquals("Gross",node.BLDetails.EquipmentInformation.Containers.Weight.@Qualifier[0])
		Assert.assertEquals("KGS",node.BLDetails.EquipmentInformation.Containers.Weight.@Units[0])
		Assert.assertEquals("77",node.BLDetails.EquipmentInformation.Containers.PieceCount.text())
		Assert.assertEquals("RL",node.BLDetails.EquipmentInformation.Containers.PieceCount.@Units[0])
		Assert.assertEquals("",node.BLDetails.EquipmentInformation.Containers.TrafficMode.OutBound.text())
		Assert.assertEquals("",node.BLDetails.EquipmentInformation.Containers.TrafficMode.InBound.text())

		Assert.assertEquals("NP",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.@AtmosphereType[0])
		Assert.assertEquals("CO",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.@GenSetType[0])
		Assert.assertEquals("",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.@VentSettingCode[0])
		Assert.assertEquals("",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.@CO2[0])
		Assert.assertEquals("",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.@O2[0])
		Assert.assertEquals("",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.@Humidity[0])
		Assert.assertEquals("0",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.@PreCooling[0])
		Assert.assertEquals("0",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.@CAIndicator[0])
		Assert.assertEquals("1",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.@ROIndicator[0])

		Assert.assertEquals("20",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.Temperature.text())
		Assert.assertEquals("C",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.Temperature.@Units[0])
		Assert.assertEquals("0",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.Ventilation.text())
		Assert.assertEquals("cbmPerHour",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.Ventilation.@Units[0])
		Assert.assertEquals("123456789012345678901",node.BLDetails.EquipmentInformation.Containers.ReeferSetting.ReeferCargoInfo.ReeferSettings.SensitiveCargoDesc.text())

		Assert.assertEquals("COMBINED",node.BLDetails.EquipmentInformation.Containers.DetentionDemurrage.Type.text())
		Assert.assertEquals("20141220000000",node.BLDetails.EquipmentInformation.Containers.DetentionDemurrage.EventDate.FreeDetentionStartDate.text())
		Assert.assertEquals("PHT",node.BLDetails.EquipmentInformation.Containers.DetentionDemurrage.EventDate.FreeDetentionStartDate.@TimeZone[0])
		Assert.assertEquals("3",node.BLDetails.EquipmentInformation.Containers.DetentionDemurrage.FreeTime.text())
		Assert.assertEquals("W",node.BLDetails.EquipmentInformation.Containers.DetentionDemurrage.FreeTime.@Type[0])
	}

	@Test
	void testBLDetails_CargoInformation(){
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <ns0:Cargo>
            <ns1:CargoNature xmlns:ns1="http://www.cargosmart.com/common">GC</ns1:CargoNature>
            <ns1:CargoDescription xmlns:ns1="http://www.cargosmart.com/common">aaathis is the testing for descdescdesc2222222douknowxxxxxxx</ns1:CargoDescription>
            <ns1:Packaging xmlns:ns1="http://www.cargosmart.com/common">
                <ns1:PackageType>BX</ns1:PackageType>
            </ns1:Packaging>
            <ns1:GrossWeight xmlns:ns1="http://www.cargosmart.com/common">
                <ns1:Weight>100</ns1:Weight>
                <ns1:WeightUnit>KGS</ns1:WeightUnit>
            </ns1:GrossWeight>
            <ns1:NetWeight xmlns:ns1="http://www.cargosmart.com/common">
                <ns1:Weight>90.111</ns1:Weight>
                <ns1:WeightUnit>KGS</ns1:WeightUnit>
            </ns1:NetWeight>
            <ns1:Volume xmlns:ns1="http://www.cargosmart.com/common">
                <ns1:Volume>200.222</ns1:Volume>
                <ns1:VolumeUnit>CBF</ns1:VolumeUnit>
            </ns1:Volume>
            <ns1:MarksAndNumbers xmlns:ns1="http://www.cargosmart.com/common">
                <ns1:SeqNumber>1</ns1:SeqNumber>
                <ns1:MarksAndNumbersLine>this is the emma1testing for cargo</ns1:MarksAndNumbersLine>
            </ns1:MarksAndNumbers>
            <ns0:CurrentContainerNumber>ELLE386162</ns0:CurrentContainerNumber>
            <ns0:CurrentContainerCheckDigit>9</ns0:CurrentContainerCheckDigit>
            <ns0:GCPackageUnit>
                <ns1:PackageSeqNumber xmlns:ns1="http://www.cargosmart.com/common">1</ns1:PackageSeqNumber>
                <ns1:PackageUnitQuantity xmlns:ns1="http://www.cargosmart.com/common">12</ns1:PackageUnitQuantity>
                <ns1:PackageUnitDescription xmlns:ns1="http://www.cargosmart.com/common">S.T.C. mixed commodities as listed below:  aaa  Packed in</ns1:PackageUnitDescription>
            </ns0:GCPackageUnit>
        </ns0:Cargo>
        <ns0:Cargo>
            <ns1:CargoNature xmlns:ns1="http://www.cargosmart.com/common">AD</ns1:CargoNature>
            <ns1:CargoDescription xmlns:ns1="http://www.cargosmart.com/common">sss</ns1:CargoDescription>
            <ns1:Packaging xmlns:ns1="http://www.cargosmart.com/common">
                <ns1:PackageType>BG</ns1:PackageType>
            </ns1:Packaging>
            <ns1:GrossWeight xmlns:ns1="http://www.cargosmart.com/common">
                <ns1:Weight>200</ns1:Weight>
                <ns1:WeightUnit>TON</ns1:WeightUnit>
            </ns1:GrossWeight>
            <ns1:NetWeight xmlns:ns1="http://www.cargosmart.com/common">
                <ns1:Weight>180.555</ns1:Weight>
                <ns1:WeightUnit>TON</ns1:WeightUnit>
            </ns1:NetWeight>
            <ns1:Volume xmlns:ns1="http://www.cargosmart.com/common">
                <ns1:Volume>100.111</ns1:Volume>
                <ns1:VolumeUnit>CBM</ns1:VolumeUnit>
            </ns1:Volume>
            <ns0:CurrentContainerNumber>ELLE386151</ns0:CurrentContainerNumber>
            <ns0:CurrentContainerCheckDigit>8</ns0:CurrentContainerCheckDigit>
            <ns0:GCPackageUnit>
                <ns1:PackageSeqNumber xmlns:ns1="http://www.cargosmart.com/common">1</ns1:PackageSeqNumber>
                <ns1:PackageUnitQuantity xmlns:ns1="http://www.cargosmart.com/common">23</ns1:PackageUnitQuantity>
                <ns1:PackageUnitDescription xmlns:ns1="http://www.cargosmart.com/common">S.T.C. freight all kinds as listed below:  sss</ns1:PackageUnitDescription>
            </ns0:GCPackageUnit>
            <ns0:DGCargoSpec>
                <ns1:DGRegulator xmlns:ns1="http://www.cargosmart.com/common">IMD</ns1:DGRegulator>
                <ns1:IMDGPage xmlns:ns1="http://www.cargosmart.com/common">page0008</ns1:IMDGPage>
                <ns1:IMOClass xmlns:ns1="http://www.cargosmart.com/common">333333classIMO3</ns1:IMOClass>
                <ns1:UNNumber xmlns:ns1="http://www.cargosmart.com/common">1170117010</ns1:UNNumber>
                <ns1:TechnicalName xmlns:ns1="http://www.cargosmart.com/common">TechnicalName180aaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbcccccccccccccccccc</ns1:TechnicalName>
                <ns1:ProperShippingName xmlns:ns1="http://www.cargosmart.com/common">ETHANOL SOLUTION properShippingName80pppppppppppppppppppp</ns1:ProperShippingName>
                <ns1:PackageGroup xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:Code>III</ns1:Code>
                    <ns1:InnerPackageDescription>
                        <ns1:PackageType>FX</ns1:PackageType>
                    </ns1:InnerPackageDescription>
                    <ns1:OuterPackageDescription>
                        <ns1:PackageType>GR</ns1:PackageType>
                    </ns1:OuterPackageDescription>
                </ns1:PackageGroup>
                <ns1:MFAGNumber xmlns:ns1="http://www.cargosmart.com/common">MFAGNumber</ns1:MFAGNumber>
                <ns1:EMSNumber xmlns:ns1="http://www.cargosmart.com/common">F-E, S-D</ns1:EMSNumber>
                <ns1:PSAClass xmlns:ns1="http://www.cargosmart.com/common">PSAClass10</ns1:PSAClass>
                <ns1:ApprovalCode xmlns:ns1="http://www.cargosmart.com/common">olsou020</ns1:ApprovalCode>
                <ns1:GrossWeight xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:Weight>649</ns1:Weight>
                    <ns1:WeightUnit>KGS</ns1:WeightUnit>
                </ns1:GrossWeight>
                <ns1:NetWeight xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:Weight>45.45</ns1:Weight>
                    <ns1:WeightUnit>KGS</ns1:WeightUnit>
                </ns1:NetWeight>
                <ns1:NetExplosiveWeight xmlns:ns1="http://www.cargosmart.com/common"/>
                <ns1:FlashPoint xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:Temperature>27</ns1:Temperature>
                    <ns1:TemperatureUnit>C</ns1:TemperatureUnit>
                </ns1:FlashPoint>
                <ns1:ElevatedTemperature xmlns:ns1="http://www.cargosmart.com/common"/>
                <ns1:isLimitedQuantity xmlns:ns1="http://www.cargosmart.com/common">true</ns1:isLimitedQuantity>
                <ns1:IsInhalationHazardous xmlns:ns1="http://www.cargosmart.com/common">false</ns1:IsInhalationHazardous>
                <ns1:IsReportableQuantity xmlns:ns1="http://www.cargosmart.com/common">false</ns1:IsReportableQuantity>
                <ns1:IsEmptyUnclean xmlns:ns1="http://www.cargosmart.com/common">false</ns1:IsEmptyUnclean>
                <ns1:isMarinePollutant xmlns:ns1="http://www.cargosmart.com/common">false</ns1:isMarinePollutant>
                <ns1:Label xmlns:ns1="http://www.cargosmart.com/common">Label1with120xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxaaaaa</ns1:Label>
                <ns1:Label xmlns:ns1="http://www.cargosmart.com/common">Labe2yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyybbb</ns1:Label>
                <ns1:Label xmlns:ns1="http://www.cargosmart.com/common">Label3zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzccc</ns1:Label>
                <ns1:Label xmlns:ns1="http://www.cargosmart.com/common">Label4qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq</ns1:Label>
                <ns1:Remarks xmlns:ns1="http://www.cargosmart.com/common"/>
                <ns1:EmergencyContact xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:FirstName>duty officer with 35 chars contact1</ns1:FirstName>
                    <ns1:ContactPhone>
                        <ns1:Number>9194832700-22-1942-552</ns1:Number>
                    </ns1:ContactPhone>
                    <ns1:Type>INBOUND</ns1:Type>
                </ns1:EmergencyContact>
                <ns1:EmergencyContact xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:FirstName>duty officer</ns1:FirstName>
                    <ns1:ContactPhone>
                        <ns1:Number>9194832700</ns1:Number>
                    </ns1:ContactPhone>
                    <ns1:Type>OUTBOUND</ns1:Type>
                </ns1:EmergencyContact>
                <ns0:DGPackageUnit>
                    <ns1:PackageSeqNumber xmlns:ns1="http://www.cargosmart.com/common">1</ns1:PackageSeqNumber>
                    <ns1:PackageUnitQuantity xmlns:ns1="http://www.cargosmart.com/common">23</ns1:PackageUnitQuantity>
                    <ns1:PackageUnitDescription xmlns:ns1="http://www.cargosmart.com/common">S.T.C. freight all kinds as listed below:  sss</ns1:PackageUnitDescription>
                </ns0:DGPackageUnit>
            </ns0:DGCargoSpec>
            <ns0:AWCargoSpec>
                <ns1:Height xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:Length>80</ns1:Length>
                    <ns1:LengthUnit>M</ns1:LengthUnit>
                </ns1:Height>
                <ns1:Length xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:Length>100</ns1:Length>
                    <ns1:LengthUnit>M</ns1:LengthUnit>
                </ns1:Length>
                <ns1:Width xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:Length>120</ns1:Length>
                    <ns1:LengthUnit>M</ns1:LengthUnit>
                </ns1:Width>
                <ns1:EmergencyContact xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:FirstName>bbb</ns1:FirstName>
                    <ns1:ContactPhone>
                        <ns1:Number>111</ns1:Number>
                    </ns1:ContactPhone>
                    <ns1:Type>INBOUND</ns1:Type>
                </ns1:EmergencyContact>
                <ns1:EmergencyContact xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:FirstName>aaa</ns1:FirstName>
                    <ns1:ContactPhone>
                        <ns1:Number>222</ns1:Number>
                    </ns1:ContactPhone>
                    <ns1:Type>OUTBOUND</ns1:Type>
                </ns1:EmergencyContact>
                <ns0:AWPackageUnit>
                    <ns1:PackageSeqNumber xmlns:ns1="http://www.cargosmart.com/common">1</ns1:PackageSeqNumber>
                    <ns1:PackageUnitQuantity xmlns:ns1="http://www.cargosmart.com/common">34</ns1:PackageUnitQuantity>
                    <ns1:PackageUnitDescription xmlns:ns1="http://www.cargosmart.com/common">x20 foot container said to contain:  xxx  =</ns1:PackageUnitDescription>
                </ns0:AWPackageUnit>
            </ns0:AWCargoSpec>
        </ns0:Cargo>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("GC",node.BLDetails.CargoInformation.CargoItems[0].CargoNature.text())
		Assert.assertEquals("100",node.BLDetails.CargoInformation.CargoItems[0].Weight.text())
		Assert.assertEquals("KGS",node.BLDetails.CargoInformation.CargoItems[0].Weight.@Units[0])
		Assert.assertEquals("200.222",node.BLDetails.CargoInformation.CargoItems[0].Volume.text())
		Assert.assertEquals("CBF",node.BLDetails.CargoInformation.CargoItems[0].Volume.@Units[0])
		Assert.assertEquals("12",node.BLDetails.CargoInformation.CargoItems[0].Package.text())
		Assert.assertEquals("ELLE386162",node.BLDetails.CargoInformation.CargoItems[0].ContainerNumber.text())
		Assert.assertEquals("9",node.BLDetails.CargoInformation.CargoItems[0].ContainerNumber.@CheckDigit[0])
		Assert.assertEquals("aaathis is the testing for descdescdesc2222222douknowxxxxxxx",node.BLDetails.CargoInformation.CargoItems[0].CargoDescription.DescriptionLine.text())
		Assert.assertEquals("this is the emma1testing for cargo",node.BLDetails.CargoInformation.CargoItems[0].MarksAndNumbers.MarksAndNumbersLine.text())
		Assert.assertEquals(0, node.BLDetails.CargoInformation.CargoItems[0].DangerousCargo.DangerousCargoInfo.size())
		Assert.assertEquals(0, node.BLDetails.CargoInformation.CargoItems[0].AwkwardCargo.AwkwardCargoInfo.size())

		Assert.assertEquals("AD",node.BLDetails.CargoInformation.CargoItems[1].CargoNature.text())
		Assert.assertEquals("IMD",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.DGRegulator.text())
		Assert.assertEquals("33333",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.IMCOClass.text())
		Assert.assertEquals("page0",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.IMDGPage.text())
		Assert.assertEquals("1170117010",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.UNNumber.text())
		Assert.assertEquals("TechnicalName180aaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbcccccccccccccccccc",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.TechnicalShippingName.text())
		Assert.assertEquals("ETHANOL SOLUTION properShippingName80pppppppppppppppppppp",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.ProperShippingName.text())
		Assert.assertEquals("F-E, S-D",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.EMSNumber.text())
		Assert.assertEquals("PSAClass10",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.PSAClass.text())
		Assert.assertEquals("MFAGNumber",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.MFAGPageNumber.text())
		Assert.assertEquals("27",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.FlashPoint.text())
		Assert.assertEquals("C",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.FlashPoint.@Units[0])
		Assert.assertEquals("",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.DGElevationTemperature.text())
		Assert.assertEquals(null,node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.DGElevationTemperature.@Units[0])
		Assert.assertEquals("",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.State.text())
		Assert.assertEquals("olsou020",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.ApprovalCode.text())
		Assert.assertEquals("Label1with120xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxaaaaa",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Label[0].text())
		Assert.assertEquals("649",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Weight.text())
		Assert.assertEquals("KGS",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Weight.@Units[0])
		Assert.assertEquals("",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.NetExplosiveWeight.text())
		Assert.assertEquals(null,node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.NetExplosiveWeight.@Units[0])
		Assert.assertEquals("0",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Indicators.isMarinePollutant.text())
		Assert.assertEquals("0",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Indicators.isInhalationHazardous.text())
		Assert.assertEquals("1",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Indicators.isLimitedQuantity.text())
		Assert.assertEquals("0",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Indicators.isReportableQuantity.text())
		Assert.assertEquals("0",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Indicators.isEmptyUnclean.text())
		Assert.assertEquals("III",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Package.PackagingGroupCode.text())
		Assert.assertEquals("FX",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Package.InnerPackageDescription.@Type[0])
		Assert.assertEquals("GR",node.BLDetails.CargoInformation.CargoItems[1].DangerousCargo.DangerousCargoInfo.HazardousMaterial.Package.OuterPackageDescription.@Type[0])

		Assert.assertEquals("80",node.BLDetails.CargoInformation.CargoItems[1].AwkwardCargo.AwkwardCargoInfo.AwkwardCargoDetails.Height.text())
		Assert.assertEquals("M",node.BLDetails.CargoInformation.CargoItems[1].AwkwardCargo.AwkwardCargoInfo.AwkwardCargoDetails.Height.@Units[0])
		Assert.assertEquals("120",node.BLDetails.CargoInformation.CargoItems[1].AwkwardCargo.AwkwardCargoInfo.AwkwardCargoDetails.Width.text())
		Assert.assertEquals("M",node.BLDetails.CargoInformation.CargoItems[1].AwkwardCargo.AwkwardCargoInfo.AwkwardCargoDetails.Width.@Units[0])
		Assert.assertEquals("100",node.BLDetails.CargoInformation.CargoItems[1].AwkwardCargo.AwkwardCargoInfo.AwkwardCargoDetails.Length.text())
		Assert.assertEquals("M",node.BLDetails.CargoInformation.CargoItems[1].AwkwardCargo.AwkwardCargoInfo.AwkwardCargoDetails.Length.@Units[0])
		Assert.assertEquals("34",node.BLDetails.CargoInformation.CargoItems[1].Package.text())
	}

	@Test
	void testSummaryDetails_BLInformation(){
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <GeneralInformation>
            <SPCompanyID>ORIEHK41514112426815</SPCompanyID>
            <SCACCode>OOLU</SCACCode>
            <BLNumber>2528612841</BLNumber>
            <BLType>Sea WayBill</BLType>
            <ShipmentCargoType>RF</ShipmentCargoType>
            <TrafficMode>
                <ns0:OutBound xmlns:ns0="http://www.cargosmart.com/common">FCL</ns0:OutBound>
                <ns0:InBound xmlns:ns0="http://www.cargosmart.com/common">FCL</ns0:InBound>
            </TrafficMode>
            <ContactOfficeCode>ANR</ContactOfficeCode>
            <BLReceiptDT>
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-12T06:45:00</ns0:GMT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="CST">2013-01-12T14:45:00</ns0:LocDT>
            </BLReceiptDT>
            <BLIssueDT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common">2013-01-17T00:00:00</ns0:LocDT>
            </BLIssueDT>
            <BLCreationDT>
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-12T06:48:00</ns0:GMT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="GMT">2013-01-12T06:48:00</ns0:LocDT>
            </BLCreationDT>
            <BLChangeDT>
                <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="GMT">2013-01-21T01:57:00</ns0:LocDT>
            </BLChangeDT>
            <BLOnboardDT>
                <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-17T00:00:00</ns0:GMT>
            </BLOnboardDT>
            <OriginalBLReleaseOffice>CHS</OriginalBLReleaseOffice>
            <BLPaymentOffice>Antwerp</BLPaymentOffice>
            <BLPaymentOfficeCode>ANR</BLPaymentOfficeCode>
            <CaptureOffice>ANR</CaptureOffice>
            <CaptureOfficeName>Antwerp</CaptureOfficeName>
            <CaptureOfficePhoneNumber>(32)-3-2348888</CaptureOfficePhoneNumber>
            <CarrierBookingNumber>2528612841</CarrierBookingNumber>
            <BLGrossWeight>
                <ns0:Weight xmlns:ns0="http://www.cargosmart.com/common">0</ns0:Weight>
            </BLGrossWeight>
            <BLNetWeight>
                <ns0:Weight xmlns:ns0="http://www.cargosmart.com/common">0</ns0:Weight>
            </BLNetWeight>
            <BLVolume>
                <ns0:Volume xmlns:ns0="http://www.cargosmart.com/common">0</ns0:Volume>
            </BLVolume>
            <BLSVVD>
                <ns0:Service xmlns:ns0="http://www.cargosmart.com/common">GEX2</ns0:Service>
                <ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">MOX</ns0:Vessel>
                <ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">MONTREAL EXPRESS</ns0:VesselName>
                <ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">214</ns0:Voyage>
                <ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
                <ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">9253741</ns0:LloydsNumber>
                <ns0:CallSign xmlns:ns0="http://www.cargosmart.com/common">MAHG5</ns0:CallSign>
            </BLSVVD>
            <OutBoundROESVVD>
                <ns0:Service xmlns:ns0="http://www.cargosmart.com/common">GEX2</ns0:Service>
                <ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">MOX</ns0:Vessel>
                <ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">MONTREAL EXPRESS</ns0:VesselName>
                <ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">214</ns0:Voyage>
                <ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
                <ROEPortOfLoad>Montreal</ROEPortOfLoad>
            </OutBoundROESVVD>
            <TradeCode>TAT</TradeCode>
            <BookingOffice>CHI</BookingOffice>
            <BookingOfficeName>Chicago</BookingOfficeName>
            <BookingOfficeUNLoc>USCHI</BookingOfficeUNLoc>
            <LabelActions>
                <Seq>1</Seq>
                <Label>B/L Ready</Label>
                <ProposedAction>UPDATE_BL_SHIPMENT</ProposedAction>
                <ActionType>U</ActionType>
                <TimeOfIssue>
                    <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-21T01:57:00</ns0:GMT>
                </TimeOfIssue>
            </LabelActions>
            <LabelActions>
                <Seq>2</Seq>
                <Label>Carrier Released</Label>
                <ProposedAction>UPDATE_SHMT_ATTRIBUTE</ProposedAction>
                <TimeOfIssue>
                    <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-21T01:57:00</ns0:GMT>
                </TimeOfIssue>
            </LabelActions>
            <LabelActions>
                <Seq>3</Seq>
                <Label>Freight Release</Label>
                <ProposedAction>UPDATE_BL_SHIPMENT</ProposedAction>
                <TimeOfIssue>
                    <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-21T01:57:00</ns0:GMT>
                </TimeOfIssue>
            </LabelActions>
            <LabelActions>
                <Seq>4</Seq>
                <Label>Cargo Released</Label>
                <ProposedAction>UPDATE_SHMT_ATTRIBUTE</ProposedAction>
                <TimeOfIssue>
                    <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-21T01:57:00</ns0:GMT>
                </TimeOfIssue>
            </LabelActions>
            <LabelActions>
                <Seq>5</Seq>
                <Label>Carrier Released</Label>
                <ProposedAction>UPDATE_SHMT_ATTRIBUTE</ProposedAction>
                <TimeOfIssue>
                    <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-21T01:57:00</ns0:GMT>
                </TimeOfIssue>
            </LabelActions>
            <LabelActions>
                <Seq>6</Seq>
                <Label>Freight Release</Label>
                <ProposedAction>UPDATE_BL_SHIPMENT</ProposedAction>
                <TimeOfIssue>
                    <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-21T01:57:00</ns0:GMT>
                </TimeOfIssue>
            </LabelActions>
        </GeneralInformation>
        <BLStatus Type="BL_STATUS">
   		    <Status>BL Ready</Status>
            <StatusDT>
           	    <ns0:GMT xmlns:ns0="http://www.cargosmart.com/common">2013-01-21T01:57:00</ns0:GMT>
               	<ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="GMT" CSTimeZone="EST">2013-01-20T20:57:00</ns0:LocDT>
           	</StatusDT>
	   	</BLStatus>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("Sea WayBill",node.SummaryDetails.BLInformation.@BillType[0])
		Assert.assertEquals("BL Ready",node.SummaryDetails.BLInformation.BLStatus.text())
		Assert.assertEquals("20130117000000",node.SummaryDetails.BLInformation.BLIssuanceDateTime.text())
		Assert.assertEquals("",node.SummaryDetails.BLInformation.BLIssuanceDateTime.@TimeZone[0])
		Assert.assertEquals("20130112144500",node.SummaryDetails.BLInformation.BLReceiptDateTime.text())
		Assert.assertEquals("CST",node.SummaryDetails.BLInformation.BLReceiptDateTime.@TimeZone[0])
		Assert.assertEquals("20130112064800",node.SummaryDetails.BLInformation.BLCreationDateTime.text())
		Assert.assertEquals("GMT",node.SummaryDetails.BLInformation.BLCreationDateTime.@TimeZone[0])
		Assert.assertEquals("20130121015700",node.SummaryDetails.BLInformation.BLUpdateDateTime.text())
		Assert.assertEquals("GMT",node.SummaryDetails.BLInformation.BLUpdateDateTime.@TimeZone[0])
		Assert.assertEquals("",node.SummaryDetails.BLInformation.BLOnBoardDateTime.text())
		Assert.assertEquals("",node.SummaryDetails.BLInformation.BLOnBoardDateTime.@TimeZone[0])
		Assert.assertEquals("ANR",node.SummaryDetails.BLInformation.BLCapturingOffice.text())
		Assert.assertEquals("(32)-3-2348888",node.SummaryDetails.BLInformation.BLCapturingOffice.@PhoneNumber[0])
		Assert.assertEquals("Antwerp",node.SummaryDetails.BLInformation.PaymentStatus.BLPaymentOffice.text())
		Assert.assertEquals("ANR",node.SummaryDetails.BLInformation.PaymentStatus.BLPaymentOffice.@PhoneNumber[0])
		Assert.assertEquals("",node.SummaryDetails.BLInformation.PaymentStatus.PaymentReceiptDateTime.text())
		Assert.assertEquals("",node.SummaryDetails.BLInformation.PaymentStatus.PaymentReceiptDateTime.@TimeZone[0])
		Assert.assertEquals("RF",node.SummaryDetails.BLInformation.CargoType.text())
		Assert.assertEquals("",node.SummaryDetails.BLInformation.CargoControlOffice.text())
		Assert.assertEquals("FCL",node.SummaryDetails.BLInformation.TrafficMode.OutBound.text())
		Assert.assertEquals("FCL",node.SummaryDetails.BLInformation.TrafficMode.InBound.text())
		Assert.assertEquals("",node.SummaryDetails.BLInformation.Weight.text())
		Assert.assertEquals(null,node.SummaryDetails.BLInformation.Weight.@Units[0])
		Assert.assertEquals("",node.SummaryDetails.BLInformation.Volume.text())
		Assert.assertEquals(null,node.SummaryDetails.BLInformation.Volume.@Units[0])
		Assert.assertEquals("",node.SummaryDetails.BLInformation.Certifications.Code.text())
		Assert.assertEquals("",node.SummaryDetails.BLInformation.Certifications.ClauseText.text())
	}

	@Test
	void testSummaryDetails_Charges(){
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <FreightCharge>
            <DisplaySeqNumber>1</DisplaySeqNumber>
            <ChargeType>0</ChargeType>
            <UnBill>true</UnBill>
            <PayableElseWhere>false</PayableElseWhere>
            <ChargeCode>DCF</ChargeCode>
            <ChargePrintLable>I/B DOC FEE</ChargePrintLable>
            <Basis>1</Basis>
            <FreightRate>25</FreightRate>
            <CalculateMethod>Lump Sum</CalculateMethod>
            <ChargeAmount Currency="EUR">25</ChargeAmount>
            <PaymentCurrency>EUR</PaymentCurrency>
            <ControlOfficeCode>512</ControlOfficeCode>
            <CollectionOfficeCode>ANR</CollectionOfficeCode>
            <TotalAmount Currency="EUR">25</TotalAmount>
            <TotalAmtInPmtCurrency Currency="EUR" ExchangeRate="1">25.265</TotalAmtInPmtCurrency>
            <ExchRateToEurope>0</ExchRateToEurope>
            <IsApprovedForCustomer>true</IsApprovedForCustomer>
            <ChargeDesc>Inbound Documentation Fee</ChargeDesc>
            <PayByInformation>
                <PayerName>Sdv Belgium</PayerName>
                <CarrierCustomerCode>4069006000</CarrierCustomerCode>
                <CityDetails>
                    <ns0:City xmlns:ns0="http://www.cargosmart.com/common">Antwerpen</ns0:City>
                    <ns0:County xmlns:ns0="http://www.cargosmart.com/common">Antwerpen</ns0:County>
                    <ns0:State xmlns:ns0="http://www.cargosmart.com/common">Vlaanderen</ns0:State>
                    <ns0:Country xmlns:ns0="http://www.cargosmart.com/common">Belgium</ns0:Country>
                </CityDetails>
            </PayByInformation>
            <RatePercentage>100</RatePercentage>
        </FreightCharge>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		script.generateBody(bl.Body[0], null, bl.Body[0].FreightCharge, [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("DCF",node.SummaryDetails.Charges.Charges.ChargeTypeCode.text())
		Assert.assertEquals("25",node.SummaryDetails.Charges.Charges.FreightRate.text())
		Assert.assertEquals("",node.SummaryDetails.Charges.Charges.PrepaidAmount.text())
		Assert.assertEquals(null,node.SummaryDetails.Charges.Charges.PrepaidAmount.@Currency[0])
		Assert.assertEquals(null,node.SummaryDetails.Charges.Charges.PrepaidAmount.@ExchangeRate[0])
		Assert.assertEquals("25.265",node.SummaryDetails.Charges.Charges.CollectAmount.text())
		Assert.assertEquals("EUR",node.SummaryDetails.Charges.Charges.CollectAmount.@Currency[0])
		Assert.assertEquals("1",node.SummaryDetails.Charges.Charges.CollectAmount.@ExchangeRate[0])
		Assert.assertEquals("1",node.SummaryDetails.Charges.Charges.PayableAt.text())
		Assert.assertEquals("4069006000",node.SummaryDetails.Charges.Charges.PayableBy.CarrierCustomerCode.text())
		Assert.assertEquals("Sdv Belgium",node.SummaryDetails.Charges.Charges.PayableBy.ContactPerson.FirstName.text())
		Assert.assertEquals("Antwerpen",node.SummaryDetails.Charges.Charges.PayableBy.CityName.text())
		Assert.assertEquals("Antwerpen",node.SummaryDetails.Charges.Charges.PayableBy.County.text())
		Assert.assertEquals("Vlaanderen",node.SummaryDetails.Charges.Charges.PayableBy.StateProvince.text())
		Assert.assertEquals("Belgium",node.SummaryDetails.Charges.Charges.PayableBy.CountryName.text())

	}
	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}
}










