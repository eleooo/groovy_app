package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.bean.*
import cs.b2b.core.mapping.bean.bc.*
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2BCXML_301_IMERYS
import groovy.xml.MarkupBuilder
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.sql.Connection

class IMERYS_BC_UnitTest {

	CUS_CS2BCXML_301_IMERYS script = null;
	StringWriter writer = null
	MarkupBuilder markupBuilder = null
	Node node = null;
	Body body = null
	static Connection conn = null
	
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
		script = new CUS_CS2BCXML_301_IMERYS();
		initMarkupBuilder()
		script.conn = conn
	}

	private void initMarkupBuilder() {
		node = null
		body = new Body();
		writer = new StringWriter()
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
	}

	@Test
	void testB1() {
		//NATEDI1401c-1
		//2.	Adjust the B102 mapping to ensure it is the same with the 300
		initMarkupBuilder()
		body.GeneralInformation = new GeneralInformation(BookingStatus: "Confirmed")
		body.ExternalReference.add(new ExternalReference(ReferenceNumber: "12345", ReferenceDescription: "System Reference Number"))
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("12345",node.B1.E145_02.text())

		initMarkupBuilder()
		body.GeneralInformation = new GeneralInformation(BookingStatus: "Confirmed")
		body.AssociatedExternalReference.add(new AssociatedExternalReference(ReferenceNumber: "54321", CSReferenceType: 'OTH', EDIReferenceType: "50"))
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("54321",node.B1.E145_02.text())

		initMarkupBuilder()
		body.GeneralInformation = new GeneralInformation(BookingStatus: "Confirmed")
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("",node.B1.E145_02.text())

		//3.	Adjust the B103 mapping to support GMT
		initMarkupBuilder()
		body.GeneralInformation = new GeneralInformation(BookingStatusDT: new BookingStatusDT(LocDT:  new LocDT(LocDT: "2017-06-02T01:00:00")))
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("20170602",node.B1.E373_03.text())

		initMarkupBuilder()
		body.GeneralInformation = new GeneralInformation(BookingStatusDT: new BookingStatusDT(GMT: "2017-06-03T01:00:00"))
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("20170603",node.B1.E373_03.text())

		//4.	Adjust the B104 mapping to support "U"
		initMarkupBuilder()
		body.GeneralInformation = new GeneralInformation(BookingStatus: "Declined")
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("D",node.B1.E558_04.text())

		initMarkupBuilder()
		body.GeneralInformation = new GeneralInformation(BookingStatus: "Cancelled")
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("D",node.B1.E558_04.text())

		initMarkupBuilder()
		body.GeneralInformation = new GeneralInformation(BookingStatus: "Confirmed")
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("U",node.B1.E558_04.text())

		initMarkupBuilder()
		body.GeneralInformation = new GeneralInformation(BookingStatus: "Confirmed")
		body.EventInformation = new EventInformation(EventDescription: "NEW")
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("A",node.B1.E558_04.text())	// 	NATEDI1401c-2

		//5.	Send DnD DT to R4*R/DTM*635
		initMarkupBuilder()
		DnD dnd1 = new DnD(attr_Type: "Demurrage", DemFreeReceivedDT: new DemFreeReceivedDT(LocDT: new LocDT(LocDT: "2017-06-03T01:00:00")))
		DnD dnd2 = new DnD(attr_Type: "Demurrage", DemFreeReceivedDT: new DemFreeReceivedDT(LocDT: new LocDT(LocDT: "2017-06-01T01:00:00")))

		body.GeneralInformation = new GeneralInformation(BookingStatus: "Confirmed")
		body.DnD.addAll([dnd1,dnd2])
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("20170601",node.Loop_R4.find{it.R4.E115_01.text() == 'R'}.DTM.find{it.E374_01.text() == '635'}.E373_02.text())



	}


	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}
}










