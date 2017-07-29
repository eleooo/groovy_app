package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.bean.CityDetails
import cs.b2b.core.mapping.bean.Discharge
import cs.b2b.core.mapping.bean.GrossWeight
import cs.b2b.core.mapping.bean.Loading
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.LocationCode
import cs.b2b.core.mapping.bean.Packaging
import cs.b2b.core.mapping.bean.SVVD
import cs.b2b.core.mapping.bean.Volume
import cs.b2b.core.mapping.bean.bc.*
import cs.b2b.core.mapping.util.MappingUtil_BC_O_Common
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2BCXML_301_LOWES
import groovy.xml.MarkupBuilder
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.sql.Connection

import static org.junit.Assert.*

class LOWES_BC_UnitTest {

	CUS_CS2BCXML_301_LOWES script = null;
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
		script = new CUS_CS2BCXML_301_LOWES();
		initMarkupBuilder()
		body = new Body();
		script.conn = conn
	}

	private void initMarkupBuilder() {
		node = null
		writer = new StringWriter()
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
	}

	@Test
	void testV1() {
	//2.	If there is more than 1 BK-VOYAGE, map first BK-VOYAGE as V107 = “SP”, last BK-VOYAGE as V107 = “GC” (totally 2 V1).If only 1 BK-VOYAGE, map V107 = “GC” (totally 1 V1 segment).

		OceanLeg oceanLeg1 = new OceanLeg(SVVD: new SVVD(Loading: new Loading(LloydsNumber: '12345', VesselName: "test1"), Discharge: new Discharge(VesselName: "test2")))
		OceanLeg oceanLeg2 = new OceanLeg(SVVD: new SVVD(Loading: new Loading(VesselName: "test1"), Discharge: new Discharge(VesselName: "test2")))
		OceanLeg oceanLeg3 = new OceanLeg(SVVD: new SVVD(Loading: new Loading(), Discharge: new Discharge()))

		//case1 one ocean leg
		body.Route = new Route(OceanLeg: [oceanLeg1])

		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("GC",node.V1.E854_07.text())


		//case2 two ocean leg
		initMarkupBuilder()
		body.Route = new Route(OceanLeg: [oceanLeg1, oceanLeg3, oceanLeg2])
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("SP",node.V1[0].E854_07.text())
		Assert.assertEquals("GC",node.V1[1].E854_07.text())

//		15.	Map V101=LloydsNumber, assign “99999996” as default.
		Assert.assertEquals("12345",node.V1[0].E597_01.text())
		Assert.assertEquals("99999996",node.V1[1].E597_01.text())

		//case3 no ocean leg
		initMarkupBuilder()
		body.Route = new Route()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals(0,node.V1.size())

		//13.	If V101 and V102 are missing, do not send out the V1.
		initMarkupBuilder()
		body.Route = new Route(OceanLeg: [oceanLeg3])
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals(0,node.V1.size())
	}

	@Test
	void testN9(){

		//14.	For N9*WY,GeneralInformation.CarrierBookingNumber

		CarrierRate carrierRate1 = new CarrierRate(CSCarrierRateType: "SC", CarrierRateNumber: "987654321")

		body.GeneralInformation = new GeneralInformation(CarrierBookingNumber: "123456789")
		body.CarrierRate = [carrierRate1]

		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("WY",node.N9[0].E128_01.text())
		Assert.assertEquals("123456789",node.N9[0].E127_02.text())
		Assert.assertEquals("E8",node.N9[1].E128_01.text())
		Assert.assertEquals("987654321",node.N9[1].E127_02.text())

	}

	@Test
	void testLX(){
//		7.	Send only 1 LX with add up all quantity/weight and volume, L008 for package quantity, L004 for weight in pounds, L006 for volume in cubic feet. Map separate/individual LX based from Cargo
//		•	Arrange conversion from KGS (*2.20462262 then round-off to 3-decimal); Map default L004=0, L005=G, L011=0
//		•	To convert CBM-to-CFT =*35.3146667215 then round-off to 3-decimal); Map L006=0, L007=E
//		•	Arrange conversion (no CL yet) assigning L009=CTN as default; Map L008=0, L009=CTN
//		•	L5 is mapped from the (first Cargo) CargoDescription (in case of multiple).
//		•	No need to map N7.


		Cargo cargo1 = new Cargo(CargoDescription: "desc1", Packaging: new Packaging(PackageType: "CT", PackageQty: 1), GrossWeight: new GrossWeight(Weight: 1, WeightUnit: "KGS"), Volume: new Volume(Volume: "1", VolumeUnit: "CBM"))
		Cargo cargo2 = new Cargo(CargoDescription: "desc2",Packaging: new Packaging(PackageType: "CT2", PackageQty: 1), GrossWeight: new GrossWeight(Weight: 1, WeightUnit: "LBS"), Volume: new Volume(Volume: "1", VolumeUnit: "CBF"))
		Cargo cargo3 = new Cargo(CargoDescription: "desc3",Packaging: new Packaging(PackageType: "CT3", PackageQty: 1), GrossWeight: new GrossWeight(Weight: 1, WeightUnit: "TON"), Volume: new Volume(Volume: "1", VolumeUnit: "CBM"))

		body.Cargo = [cargo1, cargo2, cargo3]
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals('2207.827',node.Loop_LX.L0.E81_04.text())
		Assert.assertEquals('G', node.Loop_LX.L0.E187_05.text())
		Assert.assertEquals('71.629', node.Loop_LX.L0.E183_06.text())
		Assert.assertEquals("E", node.Loop_LX.L0.E184_07.text())
		Assert.assertEquals('3', node.Loop_LX.L0.E80_08.text())
		Assert.assertEquals('CTN', node.Loop_LX.L0.E211_09.text())
		Assert.assertEquals('L', node.Loop_LX.L0.E188_11.text())

		Assert.assertEquals("desc1",node.Loop_LX.L5.E79_02.text())
		Assert.assertEquals(0, node.Loop_LX.N7.size())

		Cargo cargo4 = new Cargo(CargoDescription: "desc3",Packaging: new Packaging(PackageType: "CT3", PackageQty: 1), GrossWeight: new GrossWeight(Weight: 1, WeightUnit: "LBS"))

		body.Cargo = [cargo4]
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals('1.000',node.Loop_LX.L0.E81_04.text())
		Assert.assertEquals('G', node.Loop_LX.L0.E187_05.text())

		Cargo cargo5 = new Cargo()

		body.Cargo = [cargo5]
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals('0',node.Loop_LX.L0.E81_04.text())
		Assert.assertEquals('G', node.Loop_LX.L0.E187_05.text())
		Assert.assertEquals('0', node.Loop_LX.L0.E183_06.text())
		Assert.assertEquals("E", node.Loop_LX.L0.E184_07.text())
		Assert.assertEquals('0', node.Loop_LX.L0.E80_08.text())
		Assert.assertEquals('CTN', node.Loop_LX.L0.E211_09.text())
		Assert.assertEquals('L', node.Loop_LX.L0.E188_11.text())

	}

	@Test
	void testY3(){

		cs.b2b.core.mapping.bean.DepartureDT time1 = new cs.b2b.core.mapping.bean.DepartureDT(LocDT: new LocDT(LocDT: "2017-05-20T00:00:00"))
		cs.b2b.core.mapping.bean.DepartureDT time2 = new cs.b2b.core.mapping.bean.DepartureDT(LocDT: new LocDT(LocDT: "2017-05-21T01:00:00"))

		cs.b2b.core.mapping.bean.ArrivalDT arrivalDT1 = new cs.b2b.core.mapping.bean.ArrivalDT(attr_Indicator: "E", LocDT: new LocDT(LocDT: "2017-05-20T00:00:00"))
		ArrivalDT arrivalDT2 = new ArrivalDT(LocDT: new LocDT(LocDT: "2017-05-21T00:00:00"))

		OceanLeg oceanLeg1 = new OceanLeg(DepartureDT: [time1], ArrivalDT: [arrivalDT1], SVVD: new SVVD(Loading: new Loading(), Discharge: new Discharge()))
		OceanLeg oceanLeg2 = new OceanLeg(DepartureDT: [time2], SVVD: new SVVD(Loading: new Loading(), Discharge: new Discharge()))

		//8.	Map Y303 from first BK-VOYAGE only, if no departure date, leave it blank. Map only when multiple-OceanLeg –Y307 is enough for single OceanLeg
		body.Route = new Route(OceanLeg: [oceanLeg1])
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("",node.Y3.E373_03.text())
		Assert.assertEquals("20170520",node.Y3.E373_07.text())

		body.Route = new Route(OceanLeg: [new OceanLeg()])
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("",node.Y3.E373_03.text())
		Assert.assertEquals("",node.Y3.E373_07.text())

		//9.	Map Y307 from last BK-VOYAGE only, if no departure date, leave it blank.
		body.Route = new Route(OceanLeg: [oceanLeg1,oceanLeg2])
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("20170520",node.Y3.E373_03.text())
		Assert.assertEquals("20170521",node.Y3.E373_07.text())

		body.Route = new Route(OceanLeg: [new OceanLeg(), new OceanLeg()])
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("",node.Y3.E373_03.text())
		Assert.assertEquals("",node.Y3.E373_07.text())

		//10.	Map Y304 from Route.POD first, if Route.POD.ETA is empty, map it from last BK-VOYAGE.ETA.
		body.Route = new Route(OceanLeg: [oceanLeg1])
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("20170520",node.Y3.E373_04.text())

		body.Route = new Route(LastPOD: new LastPOD(ArrivalDT: arrivalDT2))
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("20170521",node.Y3.E373_04.text())

	}

	@Test
	void testR4(){

//		16.	For R4*E apply code conversion based from the UNLocationCode where R402=“DC” – see CL.
//				By default (if no match), map UNLocationCode where R402=“UN”.


		body.Route = new Route(FND:	new FND(
				CityDetails:new CityDetails(
							LocationCode:new LocationCode(
								UNLocationCode: "USMVX"
						)
					)
				)
			)

		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("DC",node.Loop_R4.find{it.R4.E115_01.text() == 'E'}.R4.E309_02.text())
		Assert.assertEquals("955",node.Loop_R4.find{it.R4.E115_01.text() == 'E'}.R4.E310_03.text())


		body.Route = new Route(FND:	new FND(
				CityDetails:new CityDetails(
						LocationCode:new LocationCode(
								UNLocationCode: "AAAAA"
						)
					)
				)
			)

		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("UN",node.Loop_R4.find{it.R4.E115_01.text() == 'E'}.R4.E309_02.text())
		Assert.assertEquals("AAAAA",node.Loop_R4.find{it.R4.E115_01.text() == 'E'}.R4.E310_03.text())

	}

	@Test
	void testV9(){

//		17.	Map V9 as follow:
//		V9*AAA with V903/V904 mapped from GeneralInformation.SICutOffDT.LocDT
//		V9*TRN with V903/V904 mapped from Route.FullReturnCutoff.LocDT

		body.GeneralInformation = new GeneralInformation(SICutOffDT: new SICutOffDT(LocDT: new LocDT(LocDT: '2017-05-20T00:00:00')))
		body.Route = new Route(FullReturnCutoff: new FullReturnCutoff(LocDT: new LocDT(LocDT: '2017-05-21T01:00:00')))

		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer.toString())

		Assert.assertEquals("AAA", node.V9[0].E304_01.text())
		Assert.assertEquals("20170520", node.V9[0].E373_03.text())
		Assert.assertEquals("0000", node.V9[0].E337_04.text())

		Assert.assertEquals("TRN", node.V9[1].E304_01.text())
		Assert.assertEquals("20170521", node.V9[1].E373_03.text())
		Assert.assertEquals("0100", node.V9[1].E337_04.text())
	}

	@Test
	void testDeclinedCase(){

		body.GeneralInformation = new GeneralInformation(BookingStatus: "Declined")
		script.generateBody4RejectCase(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		//4.	In booking decline, please add LX*1 for LX loop.
		Assert.assertEquals('1',node.Loop_LX.LX.E554_01.text())

		//6.	For booking decline, please send Y3*DECLINE and R4*L*UN*USNYC*NEW YORK USA by default.
		Assert.assertEquals("DECLINE", node.Y3.E13_01.text())

		//11.	For Booking Decline, if Route information is missing, please supplement with hardcode values R4*L*UN*USNYC*NEW YORK USA
		Assert.assertNotNull(node.Loop_R4.find{it.R4.E115_01.text() == 'L'})
		Assert.assertEquals("UN", node.Loop_R4.find{it.R4.E115_01.text() == 'L'}.R4.E309_02.text())
		Assert.assertEquals("USNYC", node.Loop_R4.find{it.R4.E115_01.text() == 'L'}.R4.E310_03.text())
		Assert.assertEquals("NEW YORK USA", node.Loop_R4.find{it.R4.E115_01.text() == 'L'}.R4.E114_04.text())

		body.Route = new Route(FND:	new FND(
				CityDetails:new CityDetails(
						LocationCode:new LocationCode(
								UNLocationCode: "USMVX"
						)
				)
		)
		)

		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("DC",node.Loop_R4.find{it.R4.E115_01.text() == 'E'}.R4.E309_02.text())
		Assert.assertEquals("955",node.Loop_R4.find{it.R4.E115_01.text() == 'E'}.R4.E310_03.text())

		body.Route = new Route(FND:	new FND(
				CityDetails:new CityDetails(
						LocationCode:new LocationCode(
								UNLocationCode: "AAAAA"
						)
				)
		)
		)

		initMarkupBuilder()
		script.generateBody4RejectCase(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("UN",node.Loop_R4.find{it.R4.E115_01.text() == 'E'}.R4.E309_02.text())
		Assert.assertEquals("AAAAA",node.Loop_R4.find{it.R4.E115_01.text() == 'E'}.R4.E310_03.text())


		//5.	For booking in pending status, please obsolete. If Cancelled or No Show, map “D”.
		body.GeneralInformation = new GeneralInformation(BookingStatus: "Pending")
		List list = script.prepValidation(body)
		Assert.assertEquals(1, list.size())

		body.GeneralInformation.BookingStatus = "Cancelled"
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals('D',node.B1.E558_04.text())

		body.GeneralInformation.BookingStatus = "No Show"
		initMarkupBuilder()
		script.generateBody(body, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals('D',node.B1.E558_04.text())
	}

	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}
}










