package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.bean.SVVD
import cs.b2b.core.mapping.bean.bc.Body
import cs.b2b.core.mapping.bean.bc.OceanLeg
import cs.b2b.core.mapping.bean.bc.Route
import groovy.xml.MarkupBuilder
import org.junit.Assert

import static org.junit.Assert.*

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.LocationCode
import cs.b2b.core.mapping.bean.bc.Address
import cs.b2b.core.mapping.bean.bc.EmptyPickup
import cs.b2b.core.mapping.bean.bc.ExternalReference
import cs.b2b.core.mapping.bean.bc.Facility
import cs.b2b.core.mapping.bean.bc.MvmtDT
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.mapping.scripts.CUS_CS2BCXML_301_EASTMANCHEMICAL;

class EASTMANCHEMICAL_BC_UnitTest extends JunitBase_BC_O {
	
	@BeforeClass
	static void beforeClass(){
		init(CUS_CS2BCXML_301_EASTMANCHEMICAL.class, "EASTMANCHEMICAL", "BC", "O", "msg-id-12312312345")
	}
	
	@AfterClass
	static void afterClass(){
		close()
	}
	
	@Test
	void test00() {
		prepareCS2BCXmlByFile()
		println 'size: '+body.ContainerGroup.Container.size()
	}
	
	@Test
	void test01_B1() {
		prepareCS2BCXmlByManual()
		
		String result = currentMappingDetails()
		println 'case1: \r\n'+result
		assertTrue(result.contains("<E140_01>SCAC001</E140_01>"))
		assertTrue(result.contains("<E145_02>SID01</E145_02>"))
		assertTrue(result.contains("<E558_04>A</E558_04>"))
		
		result = currentMappingDetails()
		println 'case2: \r\n'+result
		assertTrue(result.contains("<E145_02>SID01</E145_02>"))
		
		body.ExternalReference.add(new ExternalReference())
		body.getExternalReference().get(2).setCSReferenceType("SID")
		body.getExternalReference().get(2).setReferenceNumber("SID0102")
		
		body.ExternalReference.add(new ExternalReference())
		body.getExternalReference().get(3).setCSReferenceType("SID")
		body.getExternalReference().get(3).setReferenceNumber("SID0103")
		
		//get the last SID
		result = currentMappingDetails()
		println 'case3: \r\n'+result
		assertTrue(result.contains("<E145_02>SID0103</E145_02>"))
		
		body.getExternalReference().get(0).setCSReferenceType("SID11")
		body.getExternalReference().get(2).setCSReferenceType("SID11")
		body.getExternalReference().get(3).setCSReferenceType("SID11")
		
		//if no SID, then use SR
		result = currentMappingDetails()
		println 'case4-SR: \r\n'+result
		assertTrue(result.contains("<E145_02>SR-01</E145_02>"))
		
		//if no SID and SR
		body.getExternalReference().get(1).setCSReferenceType("SR11")
		result = currentMappingDetails()
		println 'case5-no: \r\n'+result
		assertTrue(result.contains("<E145_02></E145_02>"))
		
		//check status mapping
		body.getGeneralInformation().setBookingStatus("Pending")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>B</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("Wait Listed")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>B</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("Cancelled")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>D</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("No Show")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>D</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("Declined")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>R</E558_04>"))
		
	}
	
	@Test
	void test02_Y3() {
		prepareCS2BCXmlByManual()
		String result = currentMappingDetails()
		println 'case.Y3.1: \r\n'+result
		assertTrue(result.contains("<E13_01>1234567</E13_01>"))
		assertTrue(result.contains("<E140_02>SCAC001</E140_02>"))
		assertTrue(result.contains("<E373_03>20170304</E373_03>"))
		assertTrue(result.contains("<E373_04>20170203</E373_04>"))
		assertTrue(result.contains("<E373_07>20170102</E373_07>"))
		assertTrue(result.contains("<E337_08>0304</E337_08>"))
		
		//
		body.getGeneralInformation().setCarrierBookingNumber("")
		body.getGeneralInformation().setBookingStatus("Declined")
		result = currentMappingDetails()
		println 'case.Y3.2: \r\n'+result
		assertTrue(result.contains("<E13_01>DECLINE</E13_01>"))
		
		body.getGeneralInformation().setCarrierBookingNumber(null)
		body.getGeneralInformation().setBookingStatus("Declined")
		result = currentMappingDetails()
		println 'case.Y3.3: \r\n'+result
		assertTrue(result.contains("<E13_01>DECLINE</E13_01>"))
		
		body.getGeneralInformation().setBookingStatus("Pending")
		result = currentMappingDetails()
		println 'case.Y3.4: \r\n'+result
		assertTrue(result.contains("<E13_01></E13_01>"))
		
		//E375_10
		assertTrue(result.contains("<E375_10>DD</E375_10>"))
		
		body.getRoute().Haulage.OutBound = "M"
		body.getRoute().Haulage.InBound = "C"
		result = currentMappingDetails()
		println 'case.Y3.5: \r\n'+result
		assertTrue(result.contains("<E375_10>PD</E375_10>"))
		
		body.getRoute().Haulage.OutBound = "C"
		body.getRoute().Haulage.InBound = "M"
		result = currentMappingDetails()
		println 'case.Y3.6: \r\n'+result
		assertTrue(result.contains("<E375_10>DP</E375_10>"))
		
		body.getRoute().Haulage.OutBound = "M"
		body.getRoute().Haulage.InBound = "M"
		result = currentMappingDetails()
		println 'case.Y3.7: \r\n'+result
		assertTrue(result.contains("<E375_10>PP</E375_10>"))
		
		body.getRoute().Haulage.OutBound = ""
		body.getRoute().Haulage.InBound = ""
		result = currentMappingDetails()
		println 'case.Y3.8: \r\n'+result
		assertTrue(!result.contains("<E375_10>"))
		
	}
	
	@Test
	void test03_Y4() {
		prepareCS2BCXmlByManual()
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.add(new EmptyPickup())
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).ISOSizeType = "4270"
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).MvmtDT = new MvmtDT()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).MvmtDT.LocDT = new LocDT()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).MvmtDT.LocDT.LocDT = "2017-01-06T16:17:18"
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).NumberOfContainers = "20"
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Address = new Address()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Address.LocationCode = new LocationCode()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Address.LocationCode.UNLocationCode = "SHSHA"
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Facility = new Facility()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Facility.FacilityCode = "FCODE-02"
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Facility.FacilityName = "FName-02"
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Address.City = "City002"
		
		println 'case.Y4.1: ----'
		String result = currentMappingDetails()
		if (result.indexOf("<Loop_Y4>")>0) {
			result = result.substring(result.indexOf("<Loop_Y4>"), result.indexOf("</Loop_Y4>")+10)
		}
		println 'output: \r\n'+result
		//assertTrue("E310_09 4.1", result.contains("<E310_09>ABCDE</E310_09>"))
		
		println 'case.Y4.2: ----'
		prepareCS2BCXmlByFile()
		//clean all empty data
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.clear()
		body.getContainerGroup().Container.add(body.getContainerGroup().Container.get(0))
		body.getContainerGroup().Container.add(body.getContainerGroup().Container.get(0))
		body.getContainerGroup().Container.add(body.getContainerGroup().Container.get(1))
		body.getContainerGroup().Container.add(body.getContainerGroup().Container.get(1))
		body.getContainerGroup().Container.add(body.getContainerGroup().Container.get(1))
		result = currentMappingDetails()
		if (result.indexOf("<Loop_Y4>")>0) {
			result = result.substring(result.indexOf("<Loop_Y4>"), result.lastIndexOf("</Loop_Y4>")+10)
		}
		println 'output: \r\n'+result
		assertTrue("4.2, E310_09", result.contains("<E13_01>3022202341</E13_01><E373_03></E373_03><E95_05>3</E95_05><E24_06>22G0</E24_06><E56_10>03</E56_10>"))
		
		assertTrue("4.3, W09.01", result.contains("<W09><E40_01>CZ</E40_01><E408_02>-19</E408_02><E355_03>CE</E355_03><E488_07>12</E488_07><E380_08>21</E380_08></W09>"))
		assertTrue("4.4, W09.02", result.contains("<W09><E40_01>CZ</E40_01><E408_02>-29</E408_02><E355_03>FA</E355_03><E488_07>32</E488_07><E380_08>22</E380_08></W09>"))
		assertTrue("4.5, W09.03", result.contains("<W09><E40_01>CZ</E40_01><E408_02>-39</E408_02><E355_03>FA</E355_03><E488_07>32</E488_07><E380_08>32</E380_08></W09>"))
		
	}
	
	@Test
	void test04_N9() {
		println 'case.N9.1: ----'
		prepareCS2BCXmlByFile()
		//clean all empty data
		
		String result = currentMappingDetails()
		if (result.indexOf("<N9>")>0) {
			result = result.substring(result.indexOf("<N9>"), result.lastIndexOf("</N9>")+5)
		}
		println 'output: \r\n'+result
		
		assertTrue("5.1, N9.01", result == "<N9><E128_01>BN</E128_01><E127_02>3022202341</E127_02></N9><N9><E128_01>BL</E128_01><E127_02>BL001</E127_02></N9><N9><E128_01>CT</E128_01><E127_02>CT0088</E127_02></N9><N9><E128_01>ZZ</E128_01><E127_02>FF0099</E127_02></N9><N9><E128_01>SVC</E128_01><E127_02>FirstPOL-Srv-PNX</E127_02></N9>")
		
	}

	@Test
	void test05_LoopN1() {
		println 'case.N1.1: ----'
		prepareCS2BCXmlByFile()
		//clean all empty data
		
		String result = currentMappingDetails()
		if (result.indexOf("<Loop_N1>")>0) {
			result = result.substring(result.indexOf("<Loop_N1>"), result.lastIndexOf("</Loop_N1>")+10)
		}
		println 'output: \r\n'+result
		
		assertTrue("6.1, N1.01", result != "")
		
	}

	@Test
	void test06_R4(){

		StringWriter writer = null
		MarkupBuilder markupBuilder = null
		Node node = null
		writer = new StringWriter()
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));

		//CSNAEDI6631 start
		CUS_CS2BCXML_301_EASTMANCHEMICAL script = new CUS_CS2BCXML_301_EASTMANCHEMICAL(conn: conn)

		OceanLeg oceanLeg1 = new OceanLeg(SVVD: new SVVD(CarriageType: 'PR'))
		OceanLeg oceanLeg2 = new OceanLeg(SVVD: new SVVD(CarriageType: 'MA'))

		Body body = new Body(Route: new Route(OceanLeg: [oceanLeg1, oceanLeg2]))

		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())

		Assert.assertEquals(1, node.Loop_R4.findAll{it.R4.E115_01.text() == 'L'}?.size())

		writer = new StringWriter()
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));

		oceanLeg1 = new OceanLeg(SVVD: new SVVD())
		oceanLeg2 = new OceanLeg(SVVD: new SVVD())

		body = new Body(Route: new Route(OceanLeg: [oceanLeg1, oceanLeg2]))

		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())

		Assert.assertEquals(2, node.Loop_R4.findAll{it.R4.E115_01.text() == 'L'}?.size())

		//CSNAEDI6631 end

	}

	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}
}

