package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.bl.*
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2BLXML_310_AMAZON
import groovy.xml.MarkupBuilder
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.sql.Connection

class AMAZON_BL_UnitTest {

	CUS_CS2BLXML_310_AMAZON script = null;
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
		script = new CUS_CS2BLXML_310_AMAZON();
		initMarkupBuilder()
		script.conn = conn
		script.TP_ID = 'AMAZON'
		script.currentSystemDt = new Date()
	}

	private void initMarkupBuilder() {
		node = null
		body = new Body();
		writer = new StringWriter()
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
	}

	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}

	@Test
	void testB3() {
		String xml = """
<ns0:BillOfLading xmlns:ns0="http://www.cargosmart.com/billoflading">
  <ns0:Body>
     <ns0:GeneralInformation>
      <ns0:SCACCode>OOLU</ns0:SCACCode>
      <ns0:BLNumber>3037112420</ns0:BLNumber>
    </ns0:GeneralInformation>
  </ns0:Body>
</ns0:BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		//•	Map B302=GeneralInformation.BLNumber + N incrementing per container/transaction
		script.generateBody(bl.Body[0], 0, null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("30371124201",node.B3.E76_02.text())

		initMarkupBuilder()
		script.generateBody(bl.Body[0], 1, null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("30371124202",node.B3.E76_02.text())

		initMarkupBuilder()
		script.generateBody(bl.Body[0], 2, null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("30371124203",node.B3.E76_02.text())
	}

	@Test
	void testV1(){


		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <Route>             
            <OceanLeg>
                <ns0:LegSeq xmlns:ns0="http://www.cargosmart.com/common">1</ns0:LegSeq>
                <ns0:POL xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:Port>
                        <ns0:PortName>Xiamen</ns0:PortName>
                        <ns0:PortCode>ZIA</ns0:PortCode>
                        <ns0:City>Xiamen</ns0:City>
                        <ns0:County>Xiamen</ns0:County>
                        <ns0:State>Fujian</ns0:State>
                        <ns0:LocationCode>
                            <ns0:UNLocationCode>CNXMN</ns0:UNLocationCode>
                            <ns0:SchedKDType>K</ns0:SchedKDType>
                            <ns0:SchedKDCode>57069</ns0:SchedKDCode>
                        </ns0:LocationCode>
                        <ns0:Country>China</ns0:Country>
                        <ns0:CSPortID>123</ns0:CSPortID>
                        <ns0:CSCountryCode>CN</ns0:CSCountryCode>
                    </ns0:Port>
                    <ns0:Facility>
                        <ns0:FacilityCode>ZIA13</ns0:FacilityCode>
                        <ns0:FacilityName>Xiamen Ocean Gate Container Terminal Co., Ltd. (Haicang)</ns0:FacilityName>
                    </ns0:Facility>
                </ns0:POL>
                <ns0:POD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:Port>
                        <ns0:PortName>Los Angeles</ns0:PortName>
                        <ns0:PortCode>LAS</ns0:PortCode>
                        <ns0:City>Los Angeles</ns0:City>
                        <ns0:County>Los Angeles</ns0:County>
                        <ns0:State>California</ns0:State>
                        <ns0:LocationCode>
                            <ns0:UNLocationCode>USLAX</ns0:UNLocationCode>
                            <ns0:SchedKDType>D</ns0:SchedKDType>
                            <ns0:SchedKDCode>2704</ns0:SchedKDCode>
                        </ns0:LocationCode>
                        <ns0:Country>United States</ns0:Country>
                        <ns0:CSPortID>426</ns0:CSPortID>
                        <ns0:CSCountryCode>US</ns0:CSCountryCode>
                    </ns0:Port>
                    <ns0:Facility>
                        <ns0:FacilityCode>LAS02</ns0:FacilityCode>
                        <ns0:FacilityName>Eagle Marine Services Los Angeles</ns0:FacilityName>
                    </ns0:Facility>
                </ns0:POD>
                <ns0:SVVD xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:Loading>
                        <ns0:Service>PCS1</ns0:Service>
                        <ns0:Vessel>AFE</ns0:Vessel>
                        <ns0:VesselName>APL FULLERTON</ns0:VesselName>
                        <ns0:Voyage>205</ns0:Voyage>
                        <ns0:Direction>E</ns0:Direction>
                        <ns0:LloydsNumber>9632026</ns0:LloydsNumber>
                        <ns0:CallSign>S6NQ</ns0:CallSign>
                        <ns0:CallNumber>1</ns0:CallNumber>
                        <ns0:VesselNationality>Singapore</ns0:VesselNationality>
                    </ns0:Loading>
                    <ns0:Discharge>
                        <ns0:Service>PCS1</ns0:Service>
                        <ns0:Vessel>AFE</ns0:Vessel>
                        <ns0:VesselName>APL FULLERTON</ns0:VesselName>
                        <ns0:Voyage>205</ns0:Voyage>
                        <ns0:Direction>E</ns0:Direction>
                        <ns0:LloydsNumber>9632026</ns0:LloydsNumber>
                        <ns0:CallSign>S6NQ</ns0:CallSign>
                        <ns0:CallNumber>1</ns0:CallNumber>
                        <ns0:VesselNationality>Singapore</ns0:VesselNationality>
                    </ns0:Discharge>
                    <ns0:VesselVoyageType>OUTBOUND</ns0:VesselVoyageType>
                </ns0:SVVD>
                <cs:DepartureDT xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading" Indicator="A">
                    <cs:GMT>2017-05-21T08:50:00</cs:GMT>
                    <cs:LocDT CSTimeZone="CCT" TimeZone="CST">2017-05-21T16:50:00</cs:LocDT>
                </cs:DepartureDT>
                <cs:DepartureDT xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading" Indicator="E">
                    <cs:GMT>2017-05-21T07:00:00</cs:GMT>
                    <cs:LocDT CSTimeZone="CCT" TimeZone="CST">2017-05-21T15:00:00</cs:LocDT>
                </cs:DepartureDT>
                <cs:ArrivalDT xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading" Indicator="A">
                    <cs:GMT>2017-06-03T21:53:00</cs:GMT>
                    <cs:LocDT CSTimeZone="PDT" TimeZone="PDT">2017-06-03T14:53:00</cs:LocDT>
                </cs:ArrivalDT>
                <cs:ArrivalDT xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading" Indicator="E">
                    <cs:GMT>2017-06-03T23:00:00</cs:GMT>
                    <cs:LocDT CSTimeZone="PDT" TimeZone="PDT">2017-06-03T16:00:00</cs:LocDT>
                </cs:ArrivalDT>
                <CarrierExtractDT>
                    <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="HKT">2017-06-04T18:51:36</ns0:LocDT>
                </CarrierExtractDT>
                <LoadingDirectionName>East</LoadingDirectionName>
                <LoadingVesselNationalityCode>SG</LoadingVesselNationalityCode>
                <DischargeDirectionName>East</DischargeDirectionName>
                <DischargeVesselNationalityCode>SG</DischargeVesselNationalityCode>
                <POLCSStateCode>FJ</POLCSStateCode>
                <PODCSStateCode>CA</PODCSStateCode>
                <POLCSParentCityID>2533037</POLCSParentCityID>
                <PODCSParentCityID>2532344</PODCSParentCityID>
            </OceanLeg>
        </Route>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)

		initMarkupBuilder()
		script.generateBody(bl.Body[0], 0, null, [], bl.Body[0].FreightChargeCNTR, markupBuilder)
		node = xmlParserToNode(writer?.toString())

		//1.	V104 – use first char of the Direction
		Assert.assertEquals("205E",node.V1.E55_04.text())
	}

	@Test
	void testLX() {
		String xml = """
<ns0:BillOfLading xmlns:ns0="http://www.cargosmart.com/billoflading">
  <ns0:Body>
    <ns0:Container>
      <ns1:ContainerNumber xmlns:ns1="http://www.cargosmart.com/common">OOLU579055</ns1:ContainerNumber>
      <ns1:ContainerCheckDigit xmlns:ns1="http://www.cargosmart.com/common">9</ns1:ContainerCheckDigit>
      <ns0:CSContainerSizeType xmlns:ns0="http://www.cargosmart.com/common">45RE</ns0:CSContainerSizeType>
      <ns1:IsSOC xmlns:ns1="http://www.cargosmart.com/common">false</ns1:IsSOC>
      <ns1:GrossWeight xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:Weight>12</ns1:Weight>
        <ns1:WeightUnit>TON</ns1:WeightUnit>
      </ns1:GrossWeight>
      <ns1:Seal xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:SealType>SH</ns1:SealType>
        <ns1:SealNumber>OOLQ169636</ns1:SealNumber>
      </ns1:Seal>
      <ns1:TrafficMode xmlns:ns1="http://www.cargosmart.com/common"/>
      <ns0:PieceCount>
        <ns1:PieceCount xmlns:ns1="http://www.cargosmart.com/common">958</ns1:PieceCount>
        <ns1:PieceCountUnit xmlns:ns1="http://www.cargosmart.com/common">CT</ns1:PieceCountUnit>
      </ns0:PieceCount>
      <ns0:BLSeal>
        <ns1:SealType xmlns:ns1="http://www.cargosmart.com/common">SH</ns1:SealType>
        <ns1:SealNumber xmlns:ns1="http://www.cargosmart.com/common">OOLQ169636</ns1:SealNumber>
      </ns0:BLSeal>
      <ns0:CS1ContainerSizeType>40HQ</ns0:CS1ContainerSizeType>
    </ns0:Container>
<ns0:Container>
      <ns1:ContainerNumber xmlns:ns1="http://www.cargosmart.com/common">OOLU579056</ns1:ContainerNumber>
      <ns1:ContainerCheckDigit xmlns:ns1="http://www.cargosmart.com/common">9</ns1:ContainerCheckDigit>
      <ns0:CSContainerSizeType xmlns:ns0="http://www.cargosmart.com/common">20GP</ns0:CSContainerSizeType>
      <ns1:IsSOC xmlns:ns1="http://www.cargosmart.com/common">false</ns1:IsSOC>
      <ns1:GrossWeight xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:Weight>12</ns1:Weight>
        <ns1:WeightUnit>TON</ns1:WeightUnit>
      </ns1:GrossWeight>
      <ns1:Seal xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:SealType>SH</ns1:SealType>
        <ns1:SealNumber>OOLQ169636</ns1:SealNumber>
      </ns1:Seal>
      <ns1:TrafficMode xmlns:ns1="http://www.cargosmart.com/common"/>
      <ns0:PieceCount>
        <ns1:PieceCount xmlns:ns1="http://www.cargosmart.com/common">958</ns1:PieceCount>
        <ns1:PieceCountUnit xmlns:ns1="http://www.cargosmart.com/common">CT</ns1:PieceCountUnit>
      </ns0:PieceCount>
      <ns0:BLSeal>
        <ns1:SealType xmlns:ns1="http://www.cargosmart.com/common">SH</ns1:SealType>
        <ns1:SealNumber xmlns:ns1="http://www.cargosmart.com/common">OOLQ169636</ns1:SealNumber>
      </ns0:BLSeal>
      <ns0:CS1ContainerSizeType>40HQ</ns0:CS1ContainerSizeType>
    </ns0:Container>
\t <Cargo>
            <ns0:CargoNature xmlns:ns0="http://www.cargosmart.com/common">GC</ns0:CargoNature>
            <ns0:CargoDescription xmlns:ns0="http://www.cargosmart.com/common">Innova ITX9600 Heavy  Duty Inversion  Therapy Table </ns0:CargoDescription>
            <cs:Packaging xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                <cs:PackageType>CT</cs:PackageType>
                <cs:PackageQty>685</cs:PackageQty>
                <cs:PackageDesc>PO 566IZX8G Item B003QCI4GG ; Innova ITX9600 Heavy Duty Inversion Therapy Table HTS #: 9402.90.0020 SHIPPER STATES THAT THIS SHIPMENT CONTAINS NO SOLID WOOD PACKINGMATERIAL.S/C:MT176053 PO No#:566IZX8G</cs:PackageDesc>
            </cs:Packaging>
            <cs:GrossWeight xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                <cs:Weight>18495</cs:Weight>
                <cs:WeightUnit>KGS</cs:WeightUnit>
            </cs:GrossWeight>
            <cs:Volume xmlns:cs="http://www.cargosmart.com/common" xmlns="http://www.cargosmart.com/shipment/billoflading">
                <cs:Volume>65.16</cs:Volume>
                <cs:VolumeUnit>CBM</cs:VolumeUnit>
            </cs:Volume>
            <ns0:MarksAndNumbers xmlns:ns0="http://www.cargosmart.com/common">
                <ns0:SeqNumber>1</ns0:SeqNumber>
                <ns0:MarksAndNumbersLine>G.W:  LBS:  KGS</ns0:MarksAndNumbersLine>
            </ns0:MarksAndNumbers>
            <ns0:MarksAndNumbers xmlns:ns0="http://www.cargosmart.com/common">
                <ns0:SeqNumber>2</ns0:SeqNumber>
                <ns0:MarksAndNumbersLine>N.W: LBS:  KGS:</ns0:MarksAndNumbersLine>
            </ns0:MarksAndNumbers>
            <ns0:MarksAndNumbers xmlns:ns0="http://www.cargosmart.com/common">
                <ns0:SeqNumber>3</ns0:SeqNumber>
                <ns0:MarksAndNumbersLine>MEASURE: CBFT:</ns0:MarksAndNumbersLine>
            </ns0:MarksAndNumbers>
            <ns0:MarksAndNumbers xmlns:ns0="http://www.cargosmart.com/common">
                <ns0:SeqNumber>4</ns0:SeqNumber>
                <ns0:MarksAndNumbersLine>CBM:</ns0:MarksAndNumbersLine>
            </ns0:MarksAndNumbers>
            <ContainerNumber>OOLU579055</ContainerNumber>
            <ContainerCheckDigit>9</ContainerCheckDigit>
            <CurrentContainerNumber>OOLU579055</CurrentContainerNumber>
            <CurrentContainerCheckDigit>9</CurrentContainerCheckDigit>
            <TrafficModel>
                <ns0:OutBound xmlns:ns0="http://www.cargosmart.com/common">FCL</ns0:OutBound>
                <ns0:InBound xmlns:ns0="http://www.cargosmart.com/common">FCL</ns0:InBound>
            </TrafficModel>
            <CgoPkgName>Carton</CgoPkgName>
            <CgoPkgCodeCount>1</CgoPkgCodeCount>
            <CustomsRefDT/>
            <CustomsClearanceLocCode>2704</CustomsClearanceLocCode>
            <CustomsClearanceLocCity>Los Angeles</CustomsClearanceLocCity>
            <CustomsClearanceLocType>IB</CustomsClearanceLocType>
            <CustomsClearanceLocDesc>LOS ANGELES, CA - 7 PACIFIC REGION</CustomsClearanceLocDesc>
            <Seal>
                <ns0:SealType xmlns:ns0="http://www.cargosmart.com/common">CA</ns0:SealType>
                <ns0:SealNumber xmlns:ns0="http://www.cargosmart.com/common">OOLEEM3265</ns0:SealNumber>
            </Seal>
            <GCPackageUnit>
                <ns0:PackageSeqNumber xmlns:ns0="http://www.cargosmart.com/common">1</ns0:PackageSeqNumber>
                <ns0:PackageUnitQuantity xmlns:ns0="http://www.cargosmart.com/common">685</ns0:PackageUnitQuantity>
            </GCPackageUnit>
        </Cargo>
    <ns0:FreightChargeCNTR>
      <ns0:DisplaySeqNumber>1</ns0:DisplaySeqNumber>
      <ns0:ChargeType>0</ns0:ChargeType>
      <ns0:PayableElseWhere>false</ns0:PayableElseWhere>
      <ns0:ChargeCode>BUC</ns0:ChargeCode>
      <ns0:ChargePrintLable>BUNKER CHARGE</ns0:ChargePrintLable>
      <ns0:FreightRate>362</ns0:FreightRate>
      <ns0:CalculateMethod>40HQ</ns0:CalculateMethod>
      <ns0:ChargeAmount Currency="USD">362</ns0:ChargeAmount>
      <ns0:TotalAmount Currency="USD">724</ns0:TotalAmount>
      <ns0:TotalAmtInPmtCurrency Currency="USD" ExchangeRate="1">362</ns0:TotalAmtInPmtCurrency>
      <ns0:ChargeDesc>BUC</ns0:ChargeDesc>
      <ns0:PayByInformation>
        <ns0:PayerName>Closet Maid</ns0:PayerName>
        <ns0:CarrierCustomerCode>6418741000</ns0:CarrierCustomerCode>
        <ns0:CityDetails>
          <ns1:City xmlns:ns1="http://www.cargosmart.com/common">Ocala</ns1:City>
          <ns1:County xmlns:ns1="http://www.cargosmart.com/common">Marion</ns1:County>
          <ns1:State xmlns:ns1="http://www.cargosmart.com/common">Florida</ns1:State>
          <ns1:Country xmlns:ns1="http://www.cargosmart.com/common">United States</ns1:Country>
        </ns0:CityDetails>
      </ns0:PayByInformation>
      <ns0:ContainerNumber>OOLU579055</ns0:ContainerNumber>
      <ns0:ContainerCheckDigit>9</ns0:ContainerCheckDigit>
      <ns0:OOCLCntrSizeType>40HQ</ns0:OOCLCntrSizeType>
    </ns0:FreightChargeCNTR>
    <ns0:FreightChargeCNTR>
      <ns0:DisplaySeqNumber>1</ns0:DisplaySeqNumber>
      <ns0:ChargeType>0</ns0:ChargeType>
      <ns0:PayableElseWhere>false</ns0:PayableElseWhere>
      <ns0:ChargeCode>SED</ns0:ChargeCode>
      <ns0:FreightRate>4</ns0:FreightRate>
      <ns0:CalculateMethod>40HQ</ns0:CalculateMethod>
      <ns0:ChargeAmount Currency="USD">4</ns0:ChargeAmount>
      <ns0:TotalAmount Currency="USD">4</ns0:TotalAmount>
      <ns0:TotalAmtInPmtCurrency Currency="USD" ExchangeRate="1">4</ns0:TotalAmtInPmtCurrency>
      <ns0:ChargeDesc>SED</ns0:ChargeDesc>
      <ns0:PayByInformation>
        <ns0:PayerName>Closet Maid</ns0:PayerName>
        <ns0:CarrierCustomerCode>6418741000</ns0:CarrierCustomerCode>
        <ns0:CityDetails>
          <ns1:City xmlns:ns1="http://www.cargosmart.com/common">Ocala</ns1:City>
          <ns1:County xmlns:ns1="http://www.cargosmart.com/common">Marion</ns1:County>
          <ns1:State xmlns:ns1="http://www.cargosmart.com/common">Florida</ns1:State>
          <ns1:Country xmlns:ns1="http://www.cargosmart.com/common">United States</ns1:Country>
        </ns0:CityDetails>
      </ns0:PayByInformation>
      <ns0:ContainerNumber>OOLU579055</ns0:ContainerNumber>
      <ns0:ContainerCheckDigit>9</ns0:ContainerCheckDigit>
      <ns0:OOCLCntrSizeType>40HQ</ns0:OOCLCntrSizeType>
    </ns0:FreightChargeCNTR>
    <ns0:FreightChargeCNTR>
      <ns0:DisplaySeqNumber>1</ns0:DisplaySeqNumber>
      <ns0:ChargeType>0</ns0:ChargeType>
      <ns0:PayableElseWhere>false</ns0:PayableElseWhere>
      <ns0:ChargeCode>Ocean Freight</ns0:ChargeCode>
      <ns0:FreightRate>4300</ns0:FreightRate>
      <ns0:CalculateMethod>40HQ</ns0:CalculateMethod>
      <ns0:ChargeAmount Currency="USD">4300</ns0:ChargeAmount>
      <ns0:TotalAmount Currency="USD">4300</ns0:TotalAmount>
      <ns0:TotalAmtInPmtCurrency Currency="USD" ExchangeRate="1">4300</ns0:TotalAmtInPmtCurrency>
      <ns0:ChargeDesc>Ocean Freight</ns0:ChargeDesc>
      <ns0:PayByInformation>
        <ns0:PayerName>Closet Maid</ns0:PayerName>
        <ns0:CarrierCustomerCode>6418741000</ns0:CarrierCustomerCode>
        <ns0:CityDetails>
          <ns1:City xmlns:ns1="http://www.cargosmart.com/common">Ocala</ns1:City>
          <ns1:County xmlns:ns1="http://www.cargosmart.com/common">Marion</ns1:County>
          <ns1:State xmlns:ns1="http://www.cargosmart.com/common">Florida</ns1:State>
          <ns1:Country xmlns:ns1="http://www.cargosmart.com/common">United States</ns1:Country>
        </ns0:CityDetails>
      </ns0:PayByInformation>
      <ns0:ContainerNumber>OOLU579056</ns0:ContainerNumber>
      <ns0:ContainerCheckDigit>9</ns0:ContainerCheckDigit>
      <ns0:OOCLCntrSizeType>40HQ</ns0:OOCLCntrSizeType>
    </ns0:FreightChargeCNTR>
  </ns0:Body>
</ns0:BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)

		initMarkupBuilder()
		script.generateBody(bl.Body[0], 0, null, [], bl.Body[0].FreightChargeCNTR, markupBuilder)
		node = xmlParserToNode(writer?.toString())

//		Map equipment initial (N701= Container/ContainerNumber char1-4) and equipment number (N702= Container/ContainerNumber char5-10)
		Assert.assertEquals("OOLU",node.Loop_LX.Loop_N7.N7.E206_01.text())
		Assert.assertEquals("579055",node.Loop_LX.Loop_N7.N7.E207_02.text())
		Assert.assertEquals("12000",node.Loop_LX.Loop_N7.N7.E81_03.text())

		//•	Set L301=Container/GrossWeight/Weight and L302=G
		Assert.assertEquals(node.Loop_LX.Loop_N7.N7.E81_03.text(),node.L3.E81_01.text())

		//4.	Please associate Container with its Cargo to get Volume and map N708/N709.Consequenty the same value has to be declared under L309/L310
		Assert.assertEquals("G",node.L3.E187_02.text())
		Assert.assertEquals("65.16",node.L3.E183_09.text())
		Assert.assertEquals("X",node.L3.E184_10.text())

		Assert.assertEquals(node.L3.E183_09.text(),node.Loop_LX.Loop_N7.N7.E183_08.text())
		Assert.assertEquals(node.L3.E184_10.text(),node.Loop_LX.Loop_N7.N7.E184_09.text())

		//5.	Check the missing unit L312
		Assert.assertEquals("K",node.L3.E188_12.text())


		//with the ContainerCheckDigit in N718
		Assert.assertEquals("9",node.Loop_LX.Loop_N7.N7.E761_18.text())
		//converted size type in N722 (use original as default)
		Assert.assertEquals("45RE",node.Loop_LX.Loop_N7.N7.E24_22.text())

		//		Mapping only 1 LX for the specific ContainerNumber, L1 will be mapped from FreightChargeCNTR corresponding to the ContainerNumber
		Assert.assertEquals("362.00",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E60_02.text())
		Assert.assertEquals("36200",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E58_04.text())
		Assert.assertEquals("0",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E117_06.text())

		//      L108 conversion, mapping original input code as default.
		Assert.assertEquals("BNK",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E150_08.text())
		Assert.assertEquals("E",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E16_11.text())
		//7.	Map L112=ChargePrintLable
		Assert.assertEquals("BUNKER CHARGE",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E276_12.text())

		Assert.assertEquals("USD",node.Loop_LX.Loop_N7.Loop_L1[0].C3.E100_01.text())
		Assert.assertEquals("1.0000",node.Loop_LX.Loop_N7.Loop_L1[0].C3.E280_02.text())

		Assert.assertEquals("4.00",node.Loop_LX.Loop_N7.Loop_L1[1].L1.E60_02.text())
		Assert.assertEquals("400",node.Loop_LX.Loop_N7.Loop_L1[1].L1.E58_04.text())
		Assert.assertEquals("0",node.Loop_LX.Loop_N7.Loop_L1[1].L1.E117_06.text())

		//      L108 conversion, mapping original input code as default.
		Assert.assertEquals("SED",node.Loop_LX.Loop_N7.Loop_L1[1].L1.E150_08.text())
		Assert.assertEquals("E",node.Loop_LX.Loop_N7.Loop_L1[1].L1.E16_11.text())

		//Map shipment-level C3, with C301=FreightChargeCNTR/TotalAmount/Currency @ ChargeType=0
		Assert.assertEquals("USD",node.Loop_LX.Loop_N7.Loop_L1[1].C3.E100_01.text())
		Assert.assertEquals("1.0000",node.Loop_LX.Loop_N7.Loop_L1[1].C3.E280_02.text())

//		Sum up the TotalAmount only for ChargeType=0, mapping this value in B307 and L305 >> Again only for the associated FreightChargeCNTR
		Assert.assertEquals("36600",node.B3.E193_07.text())
		Assert.assertEquals(node.B3.E193_07.text(),node.L3.E58_05.text())

		initMarkupBuilder()
		script.generateBody(bl.Body[0], 1, null, [], bl.Body[0].FreightChargeCNTR, markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("OOLU",node.Loop_LX.Loop_N7.N7.E206_01.text())
		Assert.assertEquals("579056",node.Loop_LX.Loop_N7.N7.E207_02.text())
		//with the ContainerCheckDigit in N718
		Assert.assertEquals("9",node.Loop_LX.Loop_N7.N7.E761_18.text())
		//converted size type in N722 (use original as default)
		Assert.assertEquals("2200",node.Loop_LX.Loop_N7.N7.E24_22.text())

		//		Mapping only 1 LX for the specific ContainerNumber, L1 will be mapped from FreightChargeCNTR corresponding to the ContainerNumber
		Assert.assertEquals("4300.00",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E60_02.text())
		Assert.assertEquals("430000",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E58_04.text())
		Assert.assertEquals("0",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E117_06.text())
		Assert.assertEquals("BAS",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E150_08.text())
		Assert.assertEquals("E",node.Loop_LX.Loop_N7.Loop_L1[0].L1.E16_11.text())

		//Map shipment-level C3, with C301=FreightChargeCNTR/TotalAmount/Currency @ ChargeType=0
		Assert.assertEquals("USD",node.Loop_LX.Loop_N7.Loop_L1[0].C3.E100_01.text())
		Assert.assertEquals("1.0000",node.Loop_LX.Loop_N7.Loop_L1[0].C3.E280_02.text())

		//		Sum up the TotalAmount only for ChargeType=0, mapping this value in B307 and L305 >> Again only for the associated FreightChargeCNTR
		Assert.assertEquals("430000",node.B3.E193_07.text())
		Assert.assertEquals(node.B3.E193_07.text(),node.L3.E58_05.text())

		//•	Map Y2 with Y201=1, Y204=N722, convert the Y204 value based from CL mapping the original as default use Container/CSContainerSizeType
		Assert.assertEquals("1",node.Y2.E95_01.text())
		Assert.assertEquals(node.Loop_LX.Loop_N7.N7.E24_22.text(),node.Y2.E24_04.text())
	}

	@Test
	void testN9() {
		String xml = """
<ns0:BillOfLading xmlns:ns0="http://www.cargosmart.com/billoflading">
  <ns0:Body>
  <ns0:GeneralInformation>
      <ns0:SCACCode>OOLU</ns0:SCACCode>
      <ns0:BLNumber>3037112420</ns0:BLNumber>
      <ns0:CarrierBookingNumber>3037112420</ns0:CarrierBookingNumber>
    </ns0:GeneralInformation>
    <ns0:CarrierRate>
      <ns1:CSCarrierRateType xmlns:ns1="http://www.cargosmart.com/common">SC</ns1:CSCarrierRateType>
      <ns1:CarrierRateNumber xmlns:ns1="http://www.cargosmart.com/common">MT08608299MT08608299MT08608299MT08608299</ns1:CarrierRateNumber>
    </ns0:CarrierRate>
    <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
     <ns0:ExternalReference>
      <ns1:CSReferenceType xmlns:ns1="http://www.cargosmart.com/common">PO</ns1:CSReferenceType>
      <ns1:ReferenceNumber xmlns:ns1="http://www.cargosmart.com/common">PO12345678PO12345678PO12345678PO12345678</ns1:ReferenceNumber>
      <ns1:ReferenceDescription xmlns:ns1="http://www.cargosmart.com/common">Contract Number</ns1:ReferenceDescription>
    </ns0:ExternalReference>
  </ns0:Body>
</ns0:BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		//•	Follow the CL for the N9 references to map.
		script.generateBody(bl.Body[0], 0, null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("3037112420",node.N9.find{it.E128_01.text() == 'BN'}.E127_02.text())
		Assert.assertEquals("OOLU3037112420",node.N9.find{it.E128_01.text() == 'BM'}.E127_02.text())
		//N902 max length is 30
		Assert.assertEquals("MT08608299MT08608299MT08608299",node.N9.find{it.E128_01.text() == 'CT'}.E127_02.text())
		Assert.assertEquals("PO12345678PO12345678PO12345678",node.N9.find{it.E128_01.text() == 'PO'}.E127_02.text())
		//N9 max occur is 15
		Assert.assertEquals(15,node.N9.size())

	}

	@Test
	void testN1() {
		String xml = """
<ns0:BillOfLading xmlns:ns0="http://www.cargosmart.com/billoflading">
  <ns0:Body>
    <ns0:Party>
      <ns1:PartyType xmlns:ns1="http://www.cargosmart.com/common">SHP</ns1:PartyType>
      <ns1:PartyName xmlns:ns1="http://www.cargosmart.com/common">ClosetMaid (Jiangmen) Storage Ltd.</ns1:PartyName>
      <ns1:CarrierCustomerCode xmlns:ns1="http://www.cargosmart.com/common">6537492000</ns1:CarrierCustomerCode>
      <ns1:Contact xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:FirstName>Lin Arris</ns1:FirstName>
        <ns1:ContactPhone>
          <ns1:CountryCode>86</ns1:CountryCode>
          <ns1:AreaCode>750</ns1:AreaCode>
          <ns1:Number>3808120 202</ns1:Number>
        </ns1:ContactPhone>
        <ns1:ContactEmailAddress/>
      </ns1:Contact>
      <ns1:Address xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:City>Jiangmen</ns1:City>
        <ns1:County>Jiangmen</ns1:County>
        <ns1:State>Guangdong</ns1:State>
        <ns1:Country>CN</ns1:Country>
        <ns1:LocationCode>
          <ns1:UNLocationCode>CNJMN</ns1:UNLocationCode>
          <ns1:SchedKDType>K</ns1:SchedKDType>
          <ns1:SchedKDCode>57000</ns1:SchedKDCode>
        </ns1:LocationCode>
        <ns1:PostalCode/>
        <ns1:AddressLines>
          <ns1:AddressLine>Ma Yi Industry Development Dictrict</ns1:AddressLine>
        </ns1:AddressLines>
      </ns1:Address>
    </ns0:Party>
    <ns0:Party>
      <ns1:PartyType xmlns:ns1="http://www.cargosmart.com/common">CGN</ns1:PartyType>
      <ns1:PartyName xmlns:ns1="http://www.cargosmart.com/common">Closet Maid</ns1:PartyName>
      <ns1:CarrierCustomerCode xmlns:ns1="http://www.cargosmart.com/common">6418741000</ns1:CarrierCustomerCode>
      <ns1:Contact xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:FirstName>Mike Williams</ns1:FirstName>
        <ns1:ContactPhone>
          <ns1:CountryCode>1</ns1:CountryCode>
          <ns1:AreaCode>352</ns1:AreaCode>
          <ns1:Number>4016000</ns1:Number>
        </ns1:ContactPhone>
        <ns1:ContactEmailAddress/>
      </ns1:Contact>
      <ns1:Address xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:City>Ocala</ns1:City>
        <ns1:County>Marion</ns1:County>
        <ns1:State>Florida</ns1:State>
        <ns1:Country>US</ns1:Country>
        <ns1:LocationCode>
          <ns1:UNLocationCode>USOCF</ns1:UNLocationCode>
        </ns1:LocationCode>
        <ns1:PostalCode>34478</ns1:PostalCode>
        <ns1:AddressLines>
          <ns1:AddressLine>650 South West 27th Avenue</ns1:AddressLine>
          <ns1:AddressLine>4400</ns1:AddressLine>
        </ns1:AddressLines>
      </ns1:Address>
    </ns0:Party>
<ns0:Container>
      <ns1:ContainerNumber xmlns:ns1="http://www.cargosmart.com/common">OOLU579055</ns1:ContainerNumber>
      <ns1:ContainerCheckDigit xmlns:ns1="http://www.cargosmart.com/common">9</ns1:ContainerCheckDigit>
      <ns1:IsSOC xmlns:ns1="http://www.cargosmart.com/common">false</ns1:IsSOC>
      <ns1:GrossWeight xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:Weight>12</ns1:Weight>
        <ns1:WeightUnit>TON</ns1:WeightUnit>
      </ns1:GrossWeight>
      <ns1:Seal xmlns:ns1="http://www.cargosmart.com/common">
        <ns1:SealType>SH</ns1:SealType>
        <ns1:SealNumber>OOLQ169636</ns1:SealNumber>
      </ns1:Seal>
      <ns1:TrafficMode xmlns:ns1="http://www.cargosmart.com/common"/>
      <ns0:PieceCount>
        <ns1:PieceCount xmlns:ns1="http://www.cargosmart.com/common">958</ns1:PieceCount>
        <ns1:PieceCountUnit xmlns:ns1="http://www.cargosmart.com/common">CT</ns1:PieceCountUnit>
      </ns0:PieceCount>
      <ns0:BLSeal>
        <ns1:SealType xmlns:ns1="http://www.cargosmart.com/common">SH</ns1:SealType>
        <ns1:SealNumber xmlns:ns1="http://www.cargosmart.com/common">OOLQ169636</ns1:SealNumber>
      </ns0:BLSeal>
      <ns0:CS1ContainerSizeType>40HQ</ns0:CS1ContainerSizeType>
    </ns0:Container>
<ns0:FreightChargeCNTR>
      <ns0:DisplaySeqNumber>1</ns0:DisplaySeqNumber>
      <ns0:ChargeType>0</ns0:ChargeType>
      <ns0:PayableElseWhere>false</ns0:PayableElseWhere>
      <ns0:ChargeCode>BUC</ns0:ChargeCode>
      <ns0:FreightRate>362</ns0:FreightRate>
      <ns0:CalculateMethod>40HQ</ns0:CalculateMethod>
      <ns0:ChargeAmount Currency="USD">362</ns0:ChargeAmount>
      <ns0:TotalAmount Currency="USD">724</ns0:TotalAmount>
      <ns0:TotalAmtInPmtCurrency Currency="USD" ExchangeRate="1">362</ns0:TotalAmtInPmtCurrency>
      <ns0:ChargeDesc>BUC</ns0:ChargeDesc>
      <ns0:PayByInformation>
        <ns0:PayerName>Closet Maid</ns0:PayerName>
        <ns0:CarrierCustomerCode>6418741000</ns0:CarrierCustomerCode>
        <ns0:CityDetails>
          <ns1:City xmlns:ns1="http://www.cargosmart.com/common">Ocala</ns1:City>
          <ns1:County xmlns:ns1="http://www.cargosmart.com/common">Marion</ns1:County>
          <ns1:State xmlns:ns1="http://www.cargosmart.com/common">Florida</ns1:State>
          <ns1:Country xmlns:ns1="http://www.cargosmart.com/common">United States</ns1:Country>
        </ns0:CityDetails>
      </ns0:PayByInformation>
      <ns0:ContainerNumber>OOLU579055</ns0:ContainerNumber>
      <ns0:ContainerCheckDigit>9</ns0:ContainerCheckDigit>
      <ns0:OOCLCntrSizeType>40HQ</ns0:OOCLCntrSizeType>
    </ns0:FreightChargeCNTR>
  </ns0:Body>
</ns0:BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		//•	Map N101=SH, CN and with the BT from FreightChargeCNTR/PayByInformation
		script.generateBody(bl.Body[0], 0, null, [], bl.Body[0].FreightChargeCNTR, markupBuilder)
		node = xmlParserToNode(writer?.toString())

		Assert.assertEquals("SH",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N1.E98_01.text())
		Assert.assertEquals("OOLUOCEAN",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N1.E67_04.text())
		Assert.assertEquals("CN",node.Loop_N1.find{it.N1.E98_01.text() == 'CN'}.N1.E98_01.text())
		Assert.assertEquals("6418741000",node.Loop_N1.find{it.N1.E98_01.text() == 'CN'}.N1.E67_04.text())

		Assert.assertEquals("BT",node.Loop_N1.find{it.N1.E98_01.text() == 'BT'}.N1.E98_01.text())
		Assert.assertEquals("Closet Maid",node.Loop_N1.find{it.N1.E98_01.text() == 'BT'}.N1.E93_02.text())
		Assert.assertEquals("25",node.Loop_N1.find{it.N1.E98_01.text() == 'BT'}.N1.E66_03.text())
		Assert.assertEquals("6418741000",node.Loop_N1.find{it.N1.E98_01.text() == 'BT'}.N1.E67_04.text())
		Assert.assertEquals("Ocala",node.Loop_N1.find{it.N1.E98_01.text() == 'BT'}.N4.E19_01.text())
		Assert.assertEquals("FL",node.Loop_N1.find{it.N1.E98_01.text() == 'BT'}.N4.E156_02.text())
		Assert.assertEquals("US",node.Loop_N1.find{it.N1.E98_01.text() == 'BT'}.N4.E26_04.text())
		Assert.assertEquals("",node.Loop_N1.find{it.N1.E98_01.text() == 'BT'}.N4.E310_06.text())
	}
}










