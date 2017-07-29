package cs.b2b.mapping.unittest

import org.junit.Assert
import java.io.StringWriter;
import java.sql.Connection
import java.util.List;
import java.util.Map;

import org.junit.AfterClass
import org.junit.Before;
import org.junit.BeforeClass
import org.junit.Test

import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.LocationCode
import cs.b2b.core.mapping.bean.Temperature
import cs.b2b.core.mapping.bean.bc.AssociatedExternalReference
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.BookingStatusDT
import cs.b2b.core.mapping.bean.bc.ExternalReference
import cs.b2b.core.mapping.bean.bc.GeneralInformation
import cs.b2b.core.mapping.util.XmlBeanParser;
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2BCXML_301_GTN_STD2
import groovy.util.Node;
import groovy.xml.MarkupBuilder;;

class LIFUNGGTN_BC_UnitTest {

	CUS_CS2BCXML_301_GTN_STD2 script = null;
	StringWriter writer = null
	MarkupBuilder markupBuilder = null
	Node node = null;
	Body body = null
	static Connection conn = null
	BookingConfirm bc = null
	XmlBeanParser xmlBeanParser = new XmlBeanParser()
//	@BeforeClass
//	static void beforeClass(){	
//		init(CUS_CS2BCXML_301_GTN_STD2.class, "LIFUNGGTN", "BC", "O", "msg-id-123456789")
//	}
	
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
		script = new CUS_CS2BCXML_301_GTN_STD2();
		initMarkupBuilder()
		body = new Body();
		script.conn = conn
		script.TP_ID = 'LIFUNGGTN'
		script.currentSystemDt = new Date()
	}
	
	private void initMarkupBuilder() {
//		node = null
		writer = new StringWriter()
//		body = new Body();
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
	}

	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}
	
	
	@Test
	void test01_Declined() {
		initMarkupBuilder()
//		String[] runtimeParameters = ["B2BSessionID=abcde","MSG_REQ_ID=TEST-MSG-REQ-ID-0011","TP_ID=LIFUNGGTN","MSG_TYPE_ID=BC","DIR_ID=O"]
		//prepare data
		body.ExternalReference.add(new ExternalReference())
		body.getExternalReference().get(0).setCSReferenceType("OTH")
		body.getExternalReference().get(0).setReferenceNumber("test-1111111111111111111111130xxx")
		body.getExternalReference().get(0).setReferenceDescription("50")
		
		body.ExternalReference.add(new ExternalReference())
		body.getExternalReference().get(1).setCSReferenceType("OTH")
		body.getExternalReference().get(1).setReferenceNumber("test-222")
		body.getExternalReference().get(1).setReferenceDescription("60")
		
		body.AssociatedExternalReference.add(new AssociatedExternalReference())
		body.getAssociatedExternalReference().get(0).setCSReferenceType("OTH")
		body.getAssociatedExternalReference().get(0).setReferenceNumber("test-3333333333333333333333330yyy")
		body.getAssociatedExternalReference().get(0).setEDIReferenceType("50")
		
		body.AssociatedExternalReference.add(new AssociatedExternalReference())
		body.getAssociatedExternalReference().get(1).setCSReferenceType("OTH")
		body.getAssociatedExternalReference().get(1).setReferenceNumber("test-444")
		body.getAssociatedExternalReference().get(1).setEDIReferenceType("60")
		
		body.GeneralInformation = new GeneralInformation()
		body.getGeneralInformation().setSCACCode("OOLU")
		body.getGeneralInformation().setBookingStatusRemarks("10240838|This vessel is fully booked+unable to accommodate additional containers|21/01/2013 22:13:00")
		
		body.getGeneralInformation().setCarrierBookingNumber("Booking-1234 XXXXXXX yyyyyyy30abc")
		
		body.getGeneralInformation().setBookingStatusDT(new BookingStatusDT(LocDT: new LocDT(LocDT: "2017-01-13T02:15:32")))
		
		body.getGeneralInformation().setBookingStatus("Declined")		
		
		script.generateBody4RejectCase(body, markupBuilder)
		String result = writer.toString()
		println 'case1: \r\n'+result
		//B1
		Assert.assertTrue(result.contains("<E140_01>OOLU</E140_01>"))
		//a.	ExternalReference/ReferenceNumber where CSReferenceType="OTH" and EDIReferenceType="50"
		Assert.assertTrue(result.contains("<E145_02>test-1111111111111111111111130</E145_02>"))
		Assert.assertTrue(result.contains("<E373_03>20170113</E373_03>"))		
		Assert.assertTrue(result.contains("<E558_04>D</E558_04>"))
		//Y3
		Assert.assertTrue(result.contains("<E13_01>DECLINE</E13_01>"))
		//N9
		Assert.assertTrue(result.contains("<E128_01>BN</E128_01>"))
		Assert.assertTrue(result.contains("<E127_02>Booking-1234 XXXXXXX yyyyyyy30</E127_02>"))
		//R4
		Assert.assertTrue(result.contains("<E115_01>L</E115_01>"))
		//LX
		Assert.assertTrue(result.contains("<E554_01>1</E554_01>"))
		//K1
		Assert.assertTrue(result.contains("<E61_01>This vessel is fully booked+un</E61_01>"))
		Assert.assertTrue(result.contains("<E61_02>able to accommodate additional</E61_02>"))
		
		//b.	AssociatedExternalReference/ReferenceNumber where CSReferenceType="OTH" and EDIReferenceType="50"
		body.getExternalReference().remove(0)
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case2: \r\n'+result
		Assert.assertTrue(result.contains("<E145_02>test-3333333333333333333333330</E145_02>"))
		
		//c.	GeneralInformation/BookingStatusRemarks only if | exists  use the first element
		body.getAssociatedExternalReference().remove(0)		
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case3: \r\n'+result
		Assert.assertTrue(result.contains("<E145_02>10240838</E145_02>"))
		
		//d.	GeneralInformation/CarrierBookingNumber
		body.getGeneralInformation().setBookingStatusRemarks("|This vessel is fully booked+unable to accommodate additional containers|21/01/2013 22:13:00")
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case4: \r\n'+result
		Assert.assertTrue(result.contains("<E145_02>Booking-1234 XXXXXXX yyyyyyy30</E145_02>"))
		
		body.getGeneralInformation().setBookingStatusRemarks("   |This vessel is fully booked+unable to accommodate additional containers|21/01/2013 22:13:00")
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case5: \r\n'+result
		Assert.assertTrue(result.contains("<E145_02>Booking-1234 XXXXXXX yyyyyyy30</E145_02>"))
		
		body.getGeneralInformation().setBookingStatusRemarks("abc")
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case6: \r\n'+result
		Assert.assertTrue(result.contains("<E145_02>Booking-1234 XXXXXXX yyyyyyy30</E145_02>"))
		
		//b.	GeneralInformation/BookingStatusRemarks  full text if | does not exist
		//Ronnel: ~ no need to cater the spaces
		body.getGeneralInformation().setBookingStatusRemarks("testK1 xxxxxxxxxxxxxxxxxxxx    wgaigjealgjealgjelw wgiojeai  gelk")
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case7: \r\n'+result
		Assert.assertTrue(result.contains("<E61_01>testK1 xxxxxxxxxxxxxxxxxxxx   </E61_01>")) 
		Assert.assertTrue(result.contains("<E61_02> wgaigjealgjealgjelw wgiojeai </E61_02>"))
		
		body.getGeneralInformation().setBookingStatusRemarks("testK1")
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case8: \r\n'+result
		Assert.assertTrue(result.contains("<E61_01>testK1</E61_01>"))
		Assert.assertTrue(result.contains("<E61_02></E61_02>"))
		
		//a.	GeneralInformation/BookingStatusRemarks  only the 2ND element if | exists
		body.getGeneralInformation().setBookingStatusRemarks("10240838| This vessel is fully booked+ |21/01/2013 22:13:00")
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case9: \r\n'+result
		Assert.assertTrue(result.contains("<E61_01>This vessel is fully booked+</E61_01>"))
		Assert.assertTrue(result.contains("<E61_02></E61_02>"))
		
		//Ronnel:~ no need to cater the null Booking Status Remarks as well - it will be provided by OOCL as system should have a default Rejection reason
		body.getGeneralInformation().setBookingStatusRemarks("10240838|  |21/01/2013 22:13:00")
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case10: \r\n'+result
		Assert.assertTrue(result.contains("<E61_01></E61_01>"))
		Assert.assertTrue(result.contains("<E61_02></E61_02>"))
		
		//c.	Hard-code "BOOKING REQUEST CANCELLED"
		body.GeneralInformation.BookingStatusRemarks = null
		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		result = writer.toString()
		println 'case11: \r\n'+result
		Assert.assertTrue(result.contains("<E61_01>BOOKING REQUEST CANCELLED</E61_01>"))
		Assert.assertFalse(result.contains("<E61_02>"))
		
	}
	
	@Test
	void test02_checkB104Status() {
		initMarkupBuilder()
		//check status mapping
		body.GeneralInformation = new GeneralInformation()
		body.GeneralInformation.BookingStatus="Confirmed"
		script.generateBody(body, markupBuilder)
		String result = writer.toString()
		println 'case12: \r\n'+result
		Assert.assertTrue(result.contains("<E558_04>A</E558_04>"))
		
		body.GeneralInformation.BookingStatus="Cancelled"
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		result = writer.toString()
		println 'case13: \r\n'+result
		Assert.assertTrue(result.contains("<E558_04>D</E558_04>"))
		
		body.GeneralInformation.BookingStatus="Pending"
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		result = writer.toString()
		println 'case14: \r\n'+result
		Assert.assertTrue(result.contains("<E558_04>B</E558_04>"))
		
		body.GeneralInformation.BookingStatus="Rejected"
		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		def T301 = outXml.createNode('T301')
		script.generateBody(body, outXml)		
		result = writer.toString()
		println 'case15: \r\n'+result
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
		script.pospValidation(body,result,errorKeyList)
		println errorKeyList.toString()
		Assert.assertTrue(errorKeyList.toString().contains("MessageFunction are not Confirmed or Cancelled or Pending or Declined"))
		
	}	
	
}

