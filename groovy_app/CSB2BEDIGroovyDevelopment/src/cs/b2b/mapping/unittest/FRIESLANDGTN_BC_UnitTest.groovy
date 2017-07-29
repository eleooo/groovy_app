package cs.b2b.mapping.unittest

import static org.junit.Assert.*

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.LocationCode
import cs.b2b.core.mapping.bean.Temperature
import cs.b2b.core.mapping.bean.bc.*
import cs.b2b.mapping.scripts.CUS_CS2BCXML_301_GTN_STD1;

class FRIESLANDGTN_BC_UnitTest extends JunitBase_BC_O {

	@BeforeClass
	static void beforeClass(){
		init(CUS_CS2BCXML_301_GTN_STD1.class, "FRIESLANDGTN", "BC", "O", "msg-id-123456789")
	}
	
	@AfterClass
	static void afterClass(){
		close()
	}
	
//	@Before
//	void before() {
//		init(CUS_CS2BCXML_301_FRIESLANDGTN.class, "FRIESLANDGTN", "BC", "O", "msg-id-123456789")
//	}
//	
//	@After
//	void after() {
//		close()
//	}
	
	@Test
	void test01_B1() {
		//prepare data
		prepareCS2BCXmlByManual()
		body.getExternalReference().clear()
		
		body.ExternalReference.add(new ExternalReference())
		body.getExternalReference().get(0).setCSReferenceType("SID")
		body.getExternalReference().get(0).setReferenceNumber("SID-sys-01")
		body.getExternalReference().get(0).setReferenceDescription("System Reference Number")
		
		body.ExternalReference.add(new ExternalReference())
		body.getExternalReference().get(1).setCSReferenceType("SID")
		body.getExternalReference().get(1).setReferenceNumber("SID-sys-022-123456789-123456789-123456789")
		body.getExternalReference().get(1).setReferenceDescription("System Reference Number")
		
		String result = currentMappingDetails()
		println 'case1: \r\n'+result
		assertTrue(result.contains("<E140_01>SCAC001</E140_01>"))
		assertTrue(result.contains("<E373_03>20170412</E373_03>"))
		assertTrue(result.contains("<E145_02>BLANK</E145_02>"))
		assertTrue(result.contains("<E558_04>A</E558_04>"))
		
		body.getGeneralInformation().setCarrierBookingNumber("01234567")
		result = currentMappingDetails()
		println 'case2: \r\n'+result
		assertTrue(result.contains("<E145_02>SID-sys-022-123456789-12345678</E145_02>"))
		
		body.getExternalReference().get(0).setReferenceNumber("EDI Booking Confirmation")
		body.getExternalReference().get(1).setReferenceNumber("EDI Booking Confirmation")
		
		result = currentMappingDetails()
		println 'case3: \r\n'+result
		assertTrue(result.contains("<E145_02>BLANK</E145_02>"))
		
		//check status mapping
		body.getGeneralInformation().setBookingStatus("Cancelled")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>D</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("Rejected")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>X</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("Pending")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>X</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("PreBooking")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>X</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("Wait Listed")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>X</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("No Show")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>X</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("Declined")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>X</E558_04>"))
		
		body.getGeneralInformation().setBookingStatus("Terminated")
		result = currentMappingDetails()
		assertTrue(result.contains("<E558_04>X</E558_04>"))		
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
		body.getGeneralInformation().setSCACCode("APLU")
		
		String result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.lastIndexOf("</Loop_Y4>"))
		println 'case.Y4.1: \r\n'+result
		assertTrue(result.contains("<E13_01>1234567</E13_01>"))
		assertTrue(result.contains("<E373_03>20170205</E373_03>"))
		assertTrue(result.contains("<E95_05>123</E95_05>"))
		assertTrue("Container sizetype conversion", result.contains("<E24_06>29PO</E24_06>"))
		
		body.getGeneralInformation().setSCACCode("COSU")
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).ISOSizeType = "4464"
		result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.lastIndexOf("</Loop_Y4>"))
		println 'case.Y4.2: \r\n'+result
		assertTrue("Container sizetype conversion", result.contains("<E24_06>42PL</E24_06>"))
		
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.add(new EmptyPickup())
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).ISOSizeType = "4270"
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).MvmtDT = new MvmtDT()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).MvmtDT.LocDT = new LocDT()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).MvmtDT.LocDT.LocDT = "2017-02-05T15:16:17"
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).NumberOfContainers = "123"
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Address = new Address()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Address.LocationCode = new LocationCode()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Address.LocationCode.UNLocationCode = "ABCDE"
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Facility = new Facility()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Facility.FacilityCode = "FCODE-01"
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(1).Address.City = "City001"
		
		body.getGeneralInformation().setSCACCode("OOLU")
		result = currentMappingDetails()
		println 'case.Y4.3.1: \r\n'+result
		String result11 = result.substring(result.indexOf("<Loop_Y4>"), result.indexOf("</Loop_Y4>")+10)
		String result22 = result.substring(result.lastIndexOf("<Loop_Y4>"), result.lastIndexOf("</Loop_Y4>")+10)
		println 'case.Y4.3.2: \r\n'+result22
		assertTrue("Container sizetype conversion 4.3", result22.contains("<E24_06>42T0</E24_06>"))
		assertTrue("E310_09 4.3", result22.contains("<E310_09>ABCDE</E310_09>"))
		
		//UN
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.remove(1)
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).Address.LocationCode.UNLocationCode = ""
		result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.indexOf("</Loop_Y4>")+10)
		println 'case.Y4.4: \r\n'+result
		assertTrue("E310_09 4.4", result.contains("<E310_09>FCODE</E310_09>"))
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).Facility.FacilityCode = ""
		result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.indexOf("</Loop_Y4>")+10)
		println 'case.Y4.5: \r\n'+result
		assertTrue("E310_09 4.5", result.contains("<E310_09>City001-1234567890-1234567890-</E310_09>"))
		
		//use testing file for W09
		prepareCS2BCXmlByFile()
		result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.lastIndexOf("</Loop_Y4>")+10)
		println 'case.Y4.6: \r\n'+result
		assertTrue("4.6, W09.01", result.contains("<W09><E40_01>CZ</E40_01><E408_02>-18.5</E408_02><E355_03>CE</E355_03><E488_07>12</E488_07><E380_08>21</E380_08></W09>"))
		assertTrue("4.7, W09.02", result.contains("<W09><E40_01>CZ</E40_01><E408_02>-28.5</E408_02><E355_03>FA</E355_03><E488_07>32</E488_07><E380_08>22</E380_08></W09>"))
		assertTrue("4.8, W09.03", result.contains("<W09><E40_01>CZ</E40_01><E408_02>-38.5</E408_02><E355_03>FA</E355_03><E488_07>32</E488_07><E380_08>32</E380_08></W09>"))
		
		body.getCargo().get(0).getReeferCargoSpec().get(0).getTemperature().setTemperature("1234")
		result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.indexOf("</Loop_Y4>")+10)
		println 'case.Y4.9: \r\n'+result
		assertTrue("W09 4.9", result.contains("<E408_02>1234</E408_02>"))
		
		body.getCargo().get(0).getReeferCargoSpec().get(0).getTemperature().setTemperature("1235.12")
		result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.indexOf("</Loop_Y4>")+10)
		println 'case.Y4.10: \r\n'+result
		assertTrue("W09 4.10", result.contains("<E408_02>1235</E408_02>"))
		
		body.getCargo().get(0).getReeferCargoSpec().get(0).getTemperature().setTemperature("-1236.23")
		result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.indexOf("</Loop_Y4>")+10)
		println 'case.Y4.11: \r\n'+result
		assertTrue("W09 4.11", result.contains("<E408_02>-1236</E408_02>"))
		
		body.getCargo().get(0).getReeferCargoSpec().get(0).getTemperature().setTemperature("234.567")
		result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.indexOf("</Loop_Y4>")+10)
		println 'case.Y4.12: \r\n'+result
		assertTrue("W09 4.12", result.contains("<E408_02>234.5</E408_02>"))
		
		body.getCargo().get(0).getReeferCargoSpec().get(0).getTemperature().setTemperature("-112.567")
		result = currentMappingDetails()
		result = result.substring(result.indexOf("<Loop_Y4>"), result.indexOf("</Loop_Y4>")+10)
		println 'case.Y4.13: \r\n'+result
		assertTrue("W09 4.13", result.contains("<E408_02>-112.5</E408_02>"))
		
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
		assertTrue("4.1, N9.01", result.contains("<N9><E128_01>BN</E128_01><E127_02>3022202341</E127_02></N9><N9><E128_01>BM</E128_01><E127_02>3022202341</E127_02></N9><N9><E128_01>BL</E128_01><E127_02>BL001</E127_02></N9><N9><E128_01>CR</E128_01><E127_02>CR002</E127_02></N9><N9><E128_01>CR</E128_01><E127_02>CR002-1</E127_02></N9><N9><E128_01>FN</E128_01><E127_02>FR003</E127_02></N9><N9><E128_01>CR</E128_01><E127_02>ICR004</E127_02></N9><N9><E128_01>SO</E128_01><E127_02>INV005</E127_02></N9><N9><E128_01>CG</E128_01><E127_02>PO006</E127_02></N9><N9><E128_01>PO</E128_01><E127_02>PO006</E127_02></N9><N9><E128_01>SI</E128_01><E127_02>SR007</E127_02></N9><N9><E128_01>KL</E128_01><E127_02>CarrierRateNumber01</E127_02></N9><N9><E128_01>OU</E128_01><E127_02>CarrierRateNumber02</E127_02></N9><N9><E128_01>4F</E128_01><E127_02>SHP-CCC-6645553000</E127_02></N9><N9><E128_01>RU</E128_01><E127_02>OL-SVD-PNX</E127_02></N9><N9><E128_01>SS</E128_01><E127_02>CUS009</E127_02></N9>"))
	}
	
	
}

