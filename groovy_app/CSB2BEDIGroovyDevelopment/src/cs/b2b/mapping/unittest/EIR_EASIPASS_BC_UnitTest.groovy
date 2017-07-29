package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.bean.bc.AssociatedExternalReference
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.ExternalReference
import cs.b2b.core.mapping.bean.bc.Header
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2BCXML_D96B_EIR_EASIPASS
import groovy.xml.MarkupBuilder
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.sql.Connection

class EIR_EASIPASS_BC_UnitTest {

	CUS_CS2BCXML_D96B_EIR_EASIPASS script = null;
	StringWriter writer = null
	MarkupBuilder markupBuilder = null
	Node node = null;
	Body body = null
	static Connection conn = null
	BookingConfirm bc = null
	XmlBeanParser xmlBeanParser = new XmlBeanParser()
	
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
		script = new CUS_CS2BCXML_D96B_EIR_EASIPASS();
		initMarkupBuilder()
		body = new Body();
		script.conn = conn
		script.TP_ID = 'EIR_EASIPASS'
		script.currentSystemDt = new Date()
	}

	private void initMarkupBuilder() {
		node = null
		writer = new StringWriter()
		body = new Body();
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
	}

	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}

	@Test
	public void testChecking(){

		//First POL location code is CNSHA but first non China POD location code is missing
		String xml = """
<BookingConfirm xmlns="http://www.cargosmart.com/bookingconfirm">
    <Body>
		<TransactionInformation>
			<ns0:MessageID xmlns:ns0="http://www.cargosmart.com/common">SDV_BC_3022202340_OOLU_20111024140252_121098</ns0:MessageID>
			<ns0:InterchangeTransactionID xmlns:ns0="http://www.cargosmart.com/common">999999999999981192</ns0:InterchangeTransactionID>
			<Action>NEW</Action>
		</TransactionInformation>
		<GeneralInformation>
            <SCACCode>OOLU</SCACCode>
            <CarrierBookingNumber>3022202341</CarrierBookingNumber>
		</GeneralInformation>
        <Route>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:LocationCode xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                        <ns1:SchedKDType>K</ns1:SchedKDType>
                        <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                    </ns1:LocationCode>
                </ns0:Port>
            </FirstPOL>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:City>Hong Kong</ns1:City>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns1:Country>Hong Kong</ns1:Country>
                        <ns1:CSPortID>189</ns1:CSPortID>
                        <ns1:CSCountryCode>HK</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>HKG05</ns1:FacilityCode>
                        <ns1:FacilityName>Singmas Terminal (HK) Ltd.</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Singapore</ns1:PortName>
                        <ns1:City>Singapore</ns1:City>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns1:Country>China</ns1:Country>
                        <ns1:CSPortID>355</ns1:CSPortID>
                        <ns1:CSCountryCode>SG</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>SIN01</ns1:FacilityCode>
                        <ns1:FacilityName>PSA Corporation Limited</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POD>
            </OceanLeg>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:City>Hong Kong</ns1:City>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns1:Country>Hong Kong</ns1:Country>
                        <ns1:CSPortID>189</ns1:CSPortID>
                        <ns1:CSCountryCode>HK</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>HKG05</ns1:FacilityCode>
                        <ns1:FacilityName>Singmas Terminal (HK) Ltd.</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Singapore</ns1:PortName>
                        <ns1:City>Singapore</ns1:City>
                        <ns1:Country>Singapore</ns1:Country>
                        <ns1:CSPortID>355</ns1:CSPortID>
                        <ns1:CSCountryCode>SG</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>SIN01</ns1:FacilityCode>
                        <ns1:FacilityName>PSA Corporation Limited</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POD>
            </OceanLeg>
        </Route>
    </Body>
</BookingConfirm>
"""
		bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
		initMarkupBuilder()
		String[] runtimeParameters = ["B2BSessionID=abcde","MSG_REQ_ID=TEST-MSG-REQ-ID-0011","TP_ID=EIR_EASIPASS","MSG_TYPE_ID=BC","DIR_ID=O"]
		script.mapping(xml, runtimeParameters, conn)
		String bizKey = cs.b2b.core.common.session.B2BRuntimeSession.getSessionValue("abcde","PROMOTE_SESSION_BIZKEY")
		Assert.assertTrue(bizKey.contains("Missing POD UN Location Code."))

		//First POL location code is CNSHA and POD country is still China
		xml = """
<BookingConfirm xmlns="http://www.cargosmart.com/bookingconfirm">
    <Body>
		<TransactionInformation>
			<ns0:MessageID xmlns:ns0="http://www.cargosmart.com/common">SDV_BC_3022202340_OOLU_20111024140252_121098</ns0:MessageID>
			<ns0:InterchangeTransactionID xmlns:ns0="http://www.cargosmart.com/common">999999999999981192</ns0:InterchangeTransactionID>
			<Action>NEW</Action>
		</TransactionInformation>
		<GeneralInformation>
            <SCACCode>OOLU</SCACCode>
            <CarrierBookingNumber>3022202341</CarrierBookingNumber>
		</GeneralInformation>
        <Route>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:LocationCode xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                        <ns1:SchedKDType>K</ns1:SchedKDType>
                        <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                    </ns1:LocationCode>
                </ns0:Port>
            </FirstPOL>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:City>Hong Kong</ns1:City>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns1:Country>Hong Kong</ns1:Country>
                        <ns1:CSPortID>189</ns1:CSPortID>
                        <ns1:CSCountryCode>HK</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>HKG05</ns1:FacilityCode>
                        <ns1:FacilityName>Singmas Terminal (HK) Ltd.</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Singapore</ns1:PortName>
                        <ns1:City>Singapore</ns1:City>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>SGSIN</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>55976</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns1:Country>China</ns1:Country>
                        <ns1:CSPortID>355</ns1:CSPortID>
                        <ns1:CSCountryCode>SG</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>SIN01</ns1:FacilityCode>
                        <ns1:FacilityName>PSA Corporation Limited</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POD>
            </OceanLeg>
        </Route>
    </Body>
</BookingConfirm>
"""
		bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
		initMarkupBuilder()
		script.mapping(xml, runtimeParameters, conn)
		bizKey = cs.b2b.core.common.session.B2BRuntimeSession.getSessionValue("abcde","PROMOTE_SESSION_BIZKEY")
		Assert.assertFalse(bizKey.contains("Missing POD UN Location Code."))

		//First POL location code is CNSHA and POD location code existed
		xml = """
<BookingConfirm xmlns="http://www.cargosmart.com/bookingconfirm">
    <Body>
		<TransactionInformation>
			<ns0:MessageID xmlns:ns0="http://www.cargosmart.com/common">SDV_BC_3022202340_OOLU_20111024140252_121098</ns0:MessageID>
			<ns0:InterchangeTransactionID xmlns:ns0="http://www.cargosmart.com/common">999999999999981192</ns0:InterchangeTransactionID>
			<Action>NEW</Action>
		</TransactionInformation>
		<GeneralInformation>
            <SCACCode>OOLU</SCACCode>
            <CarrierBookingNumber>3022202341</CarrierBookingNumber>
		</GeneralInformation>
        <Route>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:LocationCode xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                        <ns1:SchedKDType>K</ns1:SchedKDType>
                        <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                    </ns1:LocationCode>
                </ns0:Port>
            </FirstPOL>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:City>Hong Kong</ns1:City>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns1:Country>Hong Kong</ns1:Country>
                        <ns1:CSPortID>189</ns1:CSPortID>
                        <ns1:CSCountryCode>HK</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>HKG05</ns1:FacilityCode>
                        <ns1:FacilityName>Singmas Terminal (HK) Ltd.</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Singapore</ns1:PortName>
                        <ns1:City>Singapore</ns1:City>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>SGSIN</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>55976</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns1:Country>Singapore</ns1:Country>
                        <ns1:CSPortID>355</ns1:CSPortID>
                        <ns1:CSCountryCode>SG</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>SIN01</ns1:FacilityCode>
                        <ns1:FacilityName>PSA Corporation Limited</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POD>
            </OceanLeg>
        </Route>
    </Body>
</BookingConfirm>
"""
		bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
		initMarkupBuilder()
		script.mapping(xml, runtimeParameters, conn)
		bizKey = cs.b2b.core.common.session.B2BRuntimeSession.getSessionValue("abcde","PROMOTE_SESSION_BIZKEY")
		Assert.assertFalse(bizKey.contains("Missing POD UN Location Code."))

		//First POL location code is not CNSHA and POD location code is missing
		xml = """
<BookingConfirm xmlns="http://www.cargosmart.com/bookingconfirm">
    <Body>
		<TransactionInformation>
			<ns0:MessageID xmlns:ns0="http://www.cargosmart.com/common">SDV_BC_3022202340_OOLU_20111024140252_121098</ns0:MessageID>
			<ns0:InterchangeTransactionID xmlns:ns0="http://www.cargosmart.com/common">999999999999981192</ns0:InterchangeTransactionID>
			<Action>NEW</Action>
		</TransactionInformation>
		<GeneralInformation>
            <SCACCode>OOLU</SCACCode>
            <CarrierBookingNumber>3022202341</CarrierBookingNumber>
		</GeneralInformation>
        <Route>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:LocationCode xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:UNLocationCode>CNZHA</ns1:UNLocationCode>
                        <ns1:SchedKDType>K</ns1:SchedKDType>
                        <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                    </ns1:LocationCode>
                </ns0:Port>
            </FirstPOL>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:City>Hong Kong</ns1:City>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNZHA</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>58201</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns1:Country>Hong Kong</ns1:Country>
                        <ns1:CSPortID>189</ns1:CSPortID>
                        <ns1:CSCountryCode>HK</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>HKG05</ns1:FacilityCode>
                        <ns1:FacilityName>Singmas Terminal (HK) Ltd.</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:PortName>Singapore</ns1:PortName>
                        <ns1:City>Singapore</ns1:City>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>SGSIN</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>55976</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns1:Country>Singapore</ns1:Country>
                        <ns1:CSPortID>355</ns1:CSPortID>
                        <ns1:CSCountryCode>SG</ns1:CSCountryCode>
                    </ns1:Port>
                    <ns1:Facility xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:FacilityCode>SIN01</ns1:FacilityCode>
                        <ns1:FacilityName>PSA Corporation Limited</ns1:FacilityName>
                    </ns1:Facility>
                </ns0:POD>
            </OceanLeg>
        </Route>
    </Body>
</BookingConfirm>
"""
		bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
		initMarkupBuilder()
		script.mapping(xml, runtimeParameters, conn)
		bizKey = cs.b2b.core.common.session.B2BRuntimeSession.getSessionValue("abcde","PROMOTE_SESSION_BIZKEY")
		Assert.assertFalse(bizKey.contains("Missing POD UN Location Code."))
	}

	@Test
	public void testFTX(){

		//SHAEDI235
		//SHAEDI235-1
		//case1 first POL is CNSHA, and POD location code existed
		String xml = """
<BookingConfirm xmlns="http://www.cargosmart.com/bookingconfirm">
    <Body>
        <Route>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:LocationCode xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                    </ns1:LocationCode>
                </ns0:Port>
            </FirstPOL>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    	<ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                        </ns1:LocationCode>
                    </ns1:Port>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    	<ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNHKG</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>55976</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns0:Country>China</ns0:Country>
                    </ns1:Port>
                </ns0:POD>
                 <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                        </ns1:LocationCode>
                    </ns1:Port>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    <ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>SGSIN</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>55976</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns0:Country>Singapore</ns0:Country>
                    </ns1:Port>
                </ns0:POD>
            </OceanLeg>
        </Route>
        <RemarkLines RemarkType="Special">test</RemarkLines>
    </Body>
</BookingConfirm>
"""
		bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
		initMarkupBuilder()
		script.generateBody(bc.Body[0], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("SIN", node.FTX.find{it.E4451_01.text() == 'SIN'}.E4451_01.text())
		Assert.assertEquals("SGSIN", node.FTX.find{it.E4451_01.text() == 'SIN'}.C108_04.E4440_01.text())
		Assert.assertEquals("CNSHA", node.Group3_TDT.Group4_LOC.find{it.LOC.E3227_01.text() == '9'}.LOC.C517_02.E3225_01.text())
		Assert.assertEquals("SGSIN", node.Group3_TDT.Group4_LOC.find{it.LOC.E3227_01.text() == '11'}.LOC.C517_02.E3225_01.text())

		//case2 first POL is CNSHA, and POD location code existed and could be converted and Block Code is 7
		xml = """
<BookingConfirm xmlns="http://www.cargosmart.com/bookingconfirm">
    <Body>
        <Route>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:LocationCode xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                    </ns1:LocationCode>
                </ns0:Port>
            </FirstPOL>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    	<ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                        </ns1:LocationCode>
                    </ns1:Port>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    	<ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>SARUH</ns1:UNLocationCode>
                        </ns1:LocationCode>
                        <ns0:Country>SAUDI ARABIA</ns0:Country>
                    </ns1:Port>
                </ns0:POD>
                <BlockCode>
                	<UserDischargeBlock>7</UserDischargeBlock>
                </BlockCode>
            </OceanLeg>
        </Route>
    </Body>
</BookingConfirm>
"""
		bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
		initMarkupBuilder()
		script.generateBody(bc.Body[0], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("SIN", node.FTX.find{it.E4451_01.text() == 'SIN'}.E4451_01.text())
		Assert.assertEquals("SADM2", node.FTX.find{it.E4451_01.text() == 'SIN'}.C108_04.E4440_01.text())
		Assert.assertEquals("CNSHA", node.Group3_TDT.Group4_LOC.find{it.LOC.E3227_01.text() == '9'}.LOC.C517_02.E3225_01.text())
		Assert.assertEquals("SADM2", node.Group3_TDT.Group4_LOC.find{it.LOC.E3227_01.text() == '11'}.LOC.C517_02.E3225_01.text())

		//case3 first POL is CNSHA, and POD location code existed and could be converted and Block Code is 1
		xml = """
<BookingConfirm xmlns="http://www.cargosmart.com/bookingconfirm">
    <Body>
        <Route>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:LocationCode xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                    </ns1:LocationCode>
                </ns0:Port>
            </FirstPOL>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    	<ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                        </ns1:LocationCode>
                    </ns1:Port>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    	<ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>SARUH</ns1:UNLocationCode>
                        </ns1:LocationCode>
                        <ns0:Country>SAUDI ARABIA</ns0:Country>
                    </ns1:Port>
                </ns0:POD>
                <BlockCode>
                	<UserDischargeBlock>1</UserDischargeBlock>
                </BlockCode>
            </OceanLeg>
        </Route>
    </Body>
</BookingConfirm>
"""
		bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
		initMarkupBuilder()
		script.generateBody(bc.Body[0], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("SIN", node.FTX.find{it.E4451_01.text() == 'SIN'}.E4451_01.text())
		Assert.assertEquals("SADM1", node.FTX.find{it.E4451_01.text() == 'SIN'}.C108_04.E4440_01.text())
		Assert.assertEquals("CNSHA", node.Group3_TDT.Group4_LOC.find{it.LOC.E3227_01.text() == '9'}.LOC.C517_02.E3225_01.text())
		Assert.assertEquals("SADM2", node.Group3_TDT.Group4_LOC.find{it.LOC.E3227_01.text() == '11'}.LOC.C517_02.E3225_01.text())

		//case4 first POL is not CNSHA
		xml = """
<BookingConfirm xmlns="http://www.cargosmart.com/bookingconfirm">
    <Body>
        <Route>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                	<ns1:PortName>Hong Kong</ns1:PortName>
                    <ns1:LocationCode xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:UNLocationCode>CNZHA</ns1:UNLocationCode>
                    </ns1:LocationCode>
                </ns0:Port>
            </FirstPOL>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    	<ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNZHA</ns1:UNLocationCode>
                        </ns1:LocationCode>
                    </ns1:Port>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>SARUH</ns1:UNLocationCode>
                        </ns1:LocationCode>
                    </ns1:Port>
                </ns0:POD>
                <BlockCode>
                	<UserDischargeBlock>1</UserDischargeBlock>
                </BlockCode>
            </OceanLeg>
        </Route>
    </Body>
</BookingConfirm>
"""
		bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
		initMarkupBuilder()
		script.generateBody(bc.Body[0], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals(null, node?.FTX?.find{it.E4451_01.text() == 'SIN'}?.E4451_01?.text())

		//case5 all legs' POD are in China
		xml = """
<BookingConfirm xmlns="http://www.cargosmart.com/bookingconfirm">
    <Body>
        <Route>
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                	<ns1:PortName>Hong Kong</ns1:PortName>
                    <ns1:LocationCode xmlns:ns1="http://www.cargosmart.com/common">
                        <ns1:UNLocationCode>CNSHA</ns1:UNLocationCode>
                    </ns1:LocationCode>
                </ns0:Port>
            </FirstPOL>
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                    	<ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNZHA</ns1:UNLocationCode>
                        </ns1:LocationCode>
                    </ns1:Port>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns1:Port xmlns:ns1="http://www.cargosmart.com/common">
                 	   <ns1:PortName>Hong Kong</ns1:PortName>
                        <ns1:LocationCode>
                            <ns1:UNLocationCode>CNHKG</ns1:UNLocationCode>
                            <ns1:SchedKDType>K</ns1:SchedKDType>
                            <ns1:SchedKDCode>55976</ns1:SchedKDCode>
                        </ns1:LocationCode>
                        <ns0:Country>China</ns0:Country>
                    </ns1:Port>
                </ns0:POD>
                <BlockCode>
                	<UserDischargeBlock>1</UserDischargeBlock>
                </BlockCode>
            </OceanLeg>
        </Route>
    </Body>
</BookingConfirm>
"""
		bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
		initMarkupBuilder()
		script.generateBody(bc.Body[0], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals(null, node?.FTX?.find{it.E4451_01.text() == 'SIN'}?.E4451_01?.text())
	}

}










