package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.bean.bc.AssociatedExternalReference
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.CarrierRate
import cs.b2b.core.mapping.bean.bc.ExternalReference
import cs.b2b.core.mapping.bean.bc.GeneralInformation
import cs.b2b.core.mapping.bean.bc.Header
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2BCXML_301_LOWES
import cs.b2b.mapping.scripts.CUS_CS2BCXML_XML_OOCLLOGISTICS
import groovy.xml.MarkupBuilder
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.sql.Connection

class OOCLLOGISTICS_BC_UnitTest {

	CUS_CS2BCXML_XML_OOCLLOGISTICS script = null;
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
		script = new CUS_CS2BCXML_XML_OOCLLOGISTICS();
		initMarkupBuilder()
		body = new Body();
		script.conn = conn
	}

	private void initMarkupBuilder() {
		node = null
		writer = new StringWriter()
		body = new Body();
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
	}

	@Test
	void testReference(){

		//CSEDI6637 OOCLLOGISTICS BCXML – Enhancement for PO number
		AssociatedExternalReference associatedExternalReference = new AssociatedExternalReference(CSReferenceType: 'PO', ReferenceNumber: '123456')
		ExternalReference externalReference = new ExternalReference(CSReferenceType: 'PO',ReferenceDescription: 'Purchase Order Number', ReferenceNumber: '654321')

		Header header = new Header(ControlNumber: "aaaa")

		//case1, both exists
		initMarkupBuilder()
		body.ExternalReference.add(externalReference)
		body.AssociatedExternalReference.add(associatedExternalReference)

		script.generateBody(body, header, markupBuilder)

		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("123456",node.MessageBody.ShipmentDetails.ExternalReferences.References.find{it.ReferenceType.text() == 'PO'}.ReferenceNumber.text())

		//case1, AssociatedExternalReference only
		initMarkupBuilder()

		body.AssociatedExternalReference.add(associatedExternalReference)

		script.generateBody(body, header, markupBuilder)

		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("123456",node.MessageBody.ShipmentDetails.ExternalReferences.References.find{it.ReferenceType.text() == 'PO'}.ReferenceNumber.text())


		//case3 ExternalReference only
		initMarkupBuilder()

		body.ExternalReference.add(externalReference)

		script.generateBody(body, header, markupBuilder)

		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("654321",node.MessageBody.ShipmentDetails.ExternalReferences.References.find{it.ReferenceType.text() == 'PO'}.ReferenceNumber.text())

		//case4 both not exists
		initMarkupBuilder()

		script.generateBody(body, header, markupBuilder)

		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals(null,node?.MessageBody?.ShipmentDetails?.ExternalReferences?.References?.find{it.ReferenceType.text() == 'PO'}?.ReferenceNumber?.text())

	}


	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}
}










