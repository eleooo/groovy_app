package cs.b2b.mapping.unittest

//import cs.b2b.core.mapping.bean.ss_pt2pt.Message
import cs.b2b.core.mapping.bean.ss_pt2pt.*
import cs.b2b.core.mapping.bean.ss_pt2pt.Event
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2SSXML_323_LOWES
import groovy.xml.MarkupBuilder
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.sql.Connection

import static org.junit.Assert.*

class LOWES_SS_PT2PT_UnitTest {

	CUS_CS2SSXML_323_LOWES script = null;
	StringWriter writer = null
	MarkupBuilder markupBuilder = null
	Node node = null;
	Route route = null
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
		script = new CUS_CS2SSXML_323_LOWES();
		initMarkupBuilder()
		route = new Route();
		script.conn = conn
	}

	private void initMarkupBuilder() {
		node = null
		writer = new StringWriter()
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
	}

	@Test
	void testV1() {
	//
		Carrier carrier = new Carrier(SCAC: 'scac')
		Oceanleg oceanLeg1 = new Oceanleg(Vessel: new Vessel(LloydsNumber: '11111', Name: 'test1'), Voyage: new Voyage(ExternalVoyageNumber:'extVoyNum1'))
		Oceanleg oceanLeg2 = new Oceanleg(Vessel: new Vessel(LloydsNumber: '22222', Name: 'test2'), Voyage: new Voyage(ExternalVoyageNumber:'extVoyNum2'))

		//case1 one ocean leg
		route = new Route(Carrier: carrier, OceanComponent: new OceanComponent(oceanleg: [oceanLeg1]))
		script.generateBody(markupBuilder, route, 1, 1, 1, 1)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("11111",node.V1.E597_01.text())
		Assert.assertEquals("test1",node.V1.E182_02.text())
		Assert.assertEquals("extVoyNum1",node.V1.E55_04.text())
		Assert.assertEquals("scac",node.V1.E140_05.text())

		//case2 two ocean leg
		initMarkupBuilder()
		route = new Route(Carrier: carrier, OceanComponent: new OceanComponent(oceanleg: [oceanLeg1,oceanLeg2]))
		script.generateBody(markupBuilder, route, 1, 1, 1, 1)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("22222",node.V1.E597_01.text())
		Assert.assertEquals("test2",node.V1.E182_02.text())
		Assert.assertEquals("extVoyNum2",node.V1.E55_04.text())
		Assert.assertEquals("scac",node.V1.E140_05.text())

	}

	@Test
	void testK1(){
		Oceanleg oceanLeg1 = new Oceanleg(service: new Service(ServiceCode: "SC1"))
		Oceanleg oceanLeg2 = new Oceanleg(service: new Service(ServiceCode: "SC2"))
		
		//case1 one ocean leg
		route = new Route(OceanComponent: new OceanComponent(oceanleg: [oceanLeg1]))
		script.generateBody(markupBuilder, route, 1, 1, 1, 1)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("SC1",node.K1.E61_01.text())
		
		//case2 two ocean leg
		initMarkupBuilder()
		route = new Route(OceanComponent: new OceanComponent(oceanleg: [oceanLeg1,oceanLeg2]))
		script.generateBody(markupBuilder, route, 1, 1, 1, 1)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("SC2",node.K1.E61_01.text())

	}

	@Test
	void testR4(){
		
		POL pol1 = new POL(PortName: "POLN1", UNLOCODE: "POL1")
		POD pod1 = new POD(PortName: "PODN1", UNLOCODE: "POD1")
		Oceanleg oceanLeg1 = new Oceanleg(POL: pol1, POD: pod1)
		
		POL pol2 = new POL(PortName: "POLN2", UNLOCODE: "POL2")
		POD pod2 = new POD(PortName: "PODN2", UNLOCODE: "POD2")
		Oceanleg oceanLeg2 = new Oceanleg(POL: pol2, POD: pod2)
		
		//one oceanleg
		route = new Route(OceanComponent: new OceanComponent(oceanleg: [oceanLeg1]))
		script.generateBody(markupBuilder, route, 1, 1, 1, 1)
		node = xmlParserToNode(writer?.toString())
		
		//R4*R
		Assert.assertEquals("POL1",node.Loop_R4[0].R4.E310_03.text())
		Assert.assertEquals("POLN1",node.Loop_R4[0].R4.E114_04.text())
	
		//R4*D
		Assert.assertEquals("POD1",node.Loop_R4[1].R4.E310_03.text())
		Assert.assertEquals("PODN1",node.Loop_R4[1].R4.E114_04.text())
		
		
		//two oceanleg
		initMarkupBuilder()
		route = new Route(OceanComponent: new OceanComponent(oceanleg: [oceanLeg1,oceanLeg2]))
		script.generateBody(markupBuilder, route, 1, 1, 1, 1)
		node = xmlParserToNode(writer?.toString())
		
		//R4*R
		Assert.assertEquals("POL1",node.Loop_R4[0].R4.E310_03.text())
		Assert.assertEquals("POLN1",node.Loop_R4[0].R4.E114_04.text())
	
		//R4*D
		Assert.assertEquals("POD2",node.Loop_R4[1].R4.E310_03.text())
		Assert.assertEquals("PODN2",node.Loop_R4[1].R4.E114_04.text())
		
	}

	@Test
	void testDTM(){
		Event POLevent1 = new Event(Name: "Estimate Berth Departure", LocalDateTime:"11/11/1111 11:11:11")
		Event POLevent2 = new Event(Name: "Estimate Berth Departure", LocalDateTime:"22/22/2222 22:22:22")	
		Event PODevent1 = new Event(Name: "Estimate Berth Arrival", LocalDateTime:"33/33/3333 33:33:33")
		Event PODevent2 = new Event(Name: "Estimate Berth Arrival", LocalDateTime:"44/44/4444 44:44:44")
		
		Oceanleg oceanleg1 = new Oceanleg(POL: new POL(Event:[POLevent1]), POD: new POD(Event: [PODevent1]))
		Oceanleg oceanleg2 = new Oceanleg(POL: new POL(Event:[POLevent2]), POD: new POD(Event: [PODevent2]))
		
		// with match Event case
		route = new Route(OceanComponent: new OceanComponent(oceanleg: [oceanleg1, oceanleg2]))
		script.generateBody(markupBuilder, route, 1, 1, 1, 1)
		node = xmlParserToNode(writer?.toString())
		//R4*R
		Assert.assertEquals("11111111",node.Loop_R4[0].DTM.E373_02.text())
		Assert.assertEquals("44444444",node.Loop_R4[1].DTM.E373_02.text())
		
		
		//without match event case
		Event POLevent3 = new Event(Name: "Actual Berth Departure", LocalDateTime:"22/22/2222 22:22:22")
		Event PODevent3 = new Event(Name: "Actual Berth Arrival", LocalDateTime:"33/33/3333 33:33:33")
		
		initMarkupBuilder()
		route = new Route(OceanComponent: new OceanComponent(oceanleg: [new Oceanleg(POL: new POL(Event:[POLevent3]), POD: new POD(Event: [PODevent3]))]))
		script.generateBody(markupBuilder, route, 1, 1, 1, 1)
		node = xmlParserToNode(writer?.toString())
				
		if (node.Loop_R4[0].DTM.isEmpty() && node.Loop_R4[1].DTM.isEmpty()) {
			
		} else {
			fail("Expected no DTM in this case")
		}			
			


	}

	@Test
	void testPrepCheckCase(){
		Oceanleg oceanLeg1 = new Oceanleg(Vessel: new Vessel(LloydsNumber: '11111', Name: 'test1'), Voyage: new Voyage(ExternalVoyageNumber:''))
		Oceanleg oceanLeg2 = new Oceanleg(Vessel: new Vessel(LloydsNumber: '22222', Name: 'test2'), Voyage: new Voyage(ExternalVoyageNumber:''))
		
		List<Map<String,String>> errorKeyList
		
		//case1 one container
		route = new Route(OceanComponent: new OceanComponent(oceanleg: [oceanLeg1]))
		errorKeyList = script.prepValidation(route)
		try {
			errorKeyList.get(0)
			fail("Expected an IndexOutOfBoundsException to be thrown");
		} catch(IndexOutOfBoundsException anIndexOutOfBoundsException) {
			
		}
		
		//case2 two ocean leg
		errorKeyList.clear()
		initMarkupBuilder()
		route = new Route(OceanComponent: new OceanComponent(oceanleg: [oceanLeg1,oceanLeg2]))
		errorKeyList = script.prepValidation(route)
		try {
			errorKeyList.get(0)
		} catch(IndexOutOfBoundsException anIndexOutOfBoundsException) {
			fail("Expected errorKeList for last Oceanleg with no Voyage/ExternalVoyageNumber");
		}
	
	}
	
	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}
}










