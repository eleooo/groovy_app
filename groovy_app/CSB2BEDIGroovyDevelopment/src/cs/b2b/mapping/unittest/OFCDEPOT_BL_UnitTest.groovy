package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.bean.bl.BillOfLading
import cs.b2b.core.mapping.bean.bl.Body
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2BLXML_310_OFCDEPOT
import groovy.xml.MarkupBuilder
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.sql.Connection

class OFCDEPOT_BL_UnitTest {

	CUS_CS2BLXML_310_OFCDEPOT script = null;
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
		script = new CUS_CS2BLXML_310_OFCDEPOT();
		initMarkupBuilder()
		script.conn = conn
		script.TP_ID = 'OFCDEPOT'
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

		//Map B302=B303=BLNumber
		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("3037112420",node.B3.E76_02.text())
		Assert.assertEquals("3037112420",node.B3.E145_03.text())
		Assert.assertEquals("CC",node.B3.E146_04.text())
	}

	@Test
	void testV1(){


		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
    	<ns0:GeneralInformation>
      		<ns0:SCACCode>OOLU</ns0:SCACCode>
      		<ns0:BLNumber>3037112420</ns0:BLNumber>
    	</ns0:GeneralInformation>
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
		script.generateBody(bl.Body[0], null, [], bl.Body[0].FreightChargeCNTR, markupBuilder)
		node = xmlParserToNode(writer?.toString())

		Assert.assertEquals("OOLU",node.V1.E597_01.text())
		Assert.assertEquals("APL FULLERTON",node.V1.E182_02.text())
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
      <ns0:CS1ContainerSizeType>22HQ</ns0:CS1ContainerSizeType>
    </ns0:Container>
	<Cargo>
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
	<Cargo>
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

//		Map only 1 LX containing
//		All N7 containers
//		With only 1 L0 having total weight (L004=L301), volume (L006=L309)
//		Set L008=count (N7), L009=CNT
//		Include/append the ContainerCheckDigit into N702
//		Convert N711 (map first 2-char as default), N722 (use original as default)

		initMarkupBuilder()
		script.generateBody(bl.Body[0], null, [], bl.Body[0].FreightChargeCNTR, markupBuilder)
		node = xmlParserToNode(writer?.toString())

		Assert.assertEquals(1,node.Loop_LX.size())

		Assert.assertEquals(2,node.Loop_LX.Loop_N7.size())
		Assert.assertEquals('OOLU',node.Loop_LX.Loop_N7[0].N7.E206_01.text())
		Assert.assertEquals('5790559',node.Loop_LX.Loop_N7[0].N7.E207_02.text())
		Assert.assertEquals('40',node.Loop_LX.Loop_N7[0].N7.E40_11.text())
		Assert.assertEquals('40HC',node.Loop_LX.Loop_N7[0].N7.E24_22.text())
		Assert.assertEquals('OOLU',node.Loop_LX.Loop_N7[1].N7.E206_01.text())
		Assert.assertEquals('5790569',node.Loop_LX.Loop_N7[1].N7.E207_02.text())
		Assert.assertEquals('22',node.Loop_LX.Loop_N7[1].N7.E40_11.text())
		Assert.assertEquals('22HQ',node.Loop_LX.Loop_N7[1].N7.E24_22.text())

		Assert.assertEquals(1,node.Loop_LX.Loop_L0.size())
		Assert.assertEquals('24',node.Loop_LX.Loop_L0.L0.E81_04.text())
		Assert.assertEquals('G',node.Loop_LX.Loop_L0.L0.E187_05.text())
		Assert.assertEquals('130.32',node.Loop_LX.Loop_L0.L0.E183_06.text())
		Assert.assertEquals('X',node.Loop_LX.Loop_L0.L0.E184_07.text())
		Assert.assertEquals('2',node.Loop_LX.Loop_L0.L0.E80_08.text())
		Assert.assertEquals('CNT',node.Loop_LX.Loop_L0.L0.E211_09.text())
		Assert.assertEquals('K',node.Loop_LX.Loop_L0.L0.E188_11.text())
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

		//Map N9 as per CL - N9*BM
		script.generateBody(bl.Body[0], null, [], [], markupBuilder)
		node = xmlParserToNode(writer?.toString())
		Assert.assertEquals("3037112420",node.N9.find{it.E128_01.text() == 'BM'}.E127_02.text())
		Assert.assertEquals(1,node.N9.size())
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
        <ns1:Country>China</ns1:Country>
        <ns1:LocationCode>
          <ns1:UNLocationCode>CNJMN</ns1:UNLocationCode>
          <ns1:SchedKDType>K</ns1:SchedKDType>
          <ns1:SchedKDCode>57000</ns1:SchedKDCode>
        </ns1:LocationCode>
        <ns1:PostalCode>123456123456123456123456</ns1:PostalCode>
        <ns1:AddressLines>
          <ns1:AddressLine>Ma Yi Industry Development Dictrict</ns1:AddressLine>
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

		//Map only 1 N1 for N1*SH (PartyType=SHP)
		script.generateBody(bl.Body[0],  null, [], bl.Body[0].FreightChargeCNTR, markupBuilder)
		node = xmlParserToNode(writer?.toString())

		Assert.assertEquals(1, node.Loop_N1.size())
		Assert.assertEquals("SH",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N1.E98_01.text())
		Assert.assertEquals("ClosetMaid (Jiangmen) Storage Ltd.",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N1.E93_02.text())
		Assert.assertEquals("Ma Yi Industry Development Dictrict",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N3.E166_01.text())
		Assert.assertEquals("Ma Yi Industry Development Dictric",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N3.E166_02.text())
		Assert.assertEquals("Jiangmen",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N4.E19_01.text())
		Assert.assertEquals("GD",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N4.E156_02.text())
		Assert.assertEquals("123456123456",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N4.E116_03.text())
		Assert.assertEquals("CN",node.Loop_N1.find{it.N1.E98_01.text() == 'SH'}.N4.E26_04.text())

	}

	@Test
	void testR4(){
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
        <Route>             
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
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
                <cs:Facility xmlns="http://www.cargosmart.com/shipment/billoflading" xmlns:cs="http://www.cargosmart.com/common">
                    <cs:FacilityName>Antwerp Gateway N.V. Quay 1700</cs:FacilityName>
                </cs:Facility>
                <OutboundSVVD>
                    <ns0:Service xmlns:ns0="http://www.cargosmart.com/common">NE3</ns0:Service>
                    <ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">CCU</ns0:Vessel>
                    <ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">COSCO DENMARK</ns0:VesselName>
                    <ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">015</ns0:Voyage>
                    <ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
                    <ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">9516478</ns0:LloydsNumber>
                    <ns0:CallSign xmlns:ns0="http://www.cargosmart.com/common">VRNP8</ns0:CallSign>
                    <ns0:VesselNationality xmlns:ns0="http://www.cargosmart.com/common">Hong Kong</ns0:VesselNationality>
                    <DirectionName>East</DirectionName>
                    <VesselNationalityCode>HK</VesselNationalityCode>
                </OutboundSVVD>
                <DepartureDT>
                    <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="MET">2016-11-30T23:59:00</ns0:LocDT>
                </DepartureDT>
                <CSStateCode/>
                <CSParentCityID>2532764</CSParentCityID>
            </FirstPOL>
            <LastPOD>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:PortName>Weihai</ns0:PortName>
                    <ns0:PortCode>WEI</ns0:PortCode>
                    <ns0:City>Weihai</ns0:City>
                    <ns0:County>Weihai</ns0:County>
                    <ns0:State>Shandong</ns0:State>
                    <ns0:LocationCode>
                        <ns0:UNLocationCode>CNWEI</ns0:UNLocationCode>
                        <ns0:SchedKDType>K</ns0:SchedKDType>
                        <ns0:SchedKDCode>57000</ns0:SchedKDCode>
                    </ns0:LocationCode>
                    <ns0:Country>China</ns0:Country>
                    <ns0:CSPortID>116</ns0:CSPortID>
                    <ns0:CSCountryCode>CN</ns0:CSCountryCode>
                </ns0:Port>
                <cs:Facility xmlns="http://www.cargosmart.com/shipment/billoflading" xmlns:cs="http://www.cargosmart.com/common">
                    <cs:FacilityName>Weihai South Port</cs:FacilityName>
                </cs:Facility>
                <InboundSVVD>
                    <ns0:Service xmlns:ns0="http://www.cargosmart.com/common">CF8</ns0:Service>
                    <ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">Q4F</ns0:Vessel>
                    <ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">JINFUXING11</ns0:VesselName>
                    <ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">401</ns0:Voyage>
                    <ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">N</ns0:Direction>
                    <ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">0</ns0:LloydsNumber>
                    <DirectionName>North</DirectionName>
                </InboundSVVD>
                <ArrivalDT>
                    <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="CST">2017-01-11T21:00:00</ns0:LocDT>
                </ArrivalDT>
                <CSStateCode>SD</CSStateCode>
                <CSParentCityID>2531318</CSParentCityID>
            </LastPOD>
        </Route>
    </Body>
</BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		initMarkupBuilder()

		//Map only 1 R4 for R4*L (FirstPOL) having
		//R402=OR
		//R403=LocationCode.ScheKDCode
		//R404=LocationCode.UNLocationCode
		script.generateBody(bl.Body[0],  null, [], bl.Body[0].FreightChargeCNTR, markupBuilder)
		node = xmlParserToNode(writer?.toString())

		Assert.assertEquals('L', node.Loop_R4.R4.E115_01.text())
		Assert.assertEquals('OR', node.Loop_R4.R4.E309_02.text())
		Assert.assertEquals('42305', node.Loop_R4.R4.E310_03.text())
		Assert.assertEquals('BEANR', node.Loop_R4.R4.E114_04.text())

	}

	@Test
	void testL3(){
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
      <ns0:CS1ContainerSizeType>22HQ</ns0:CS1ContainerSizeType>
    </ns0:Container>
	<Cargo>
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
	<Cargo>
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
	<FreightCharge>
		<DisplaySeqNumber>2</DisplaySeqNumber>
		<ChargeType>0</ChargeType>
		<UnBill>true</UnBill>
		<PayableElseWhere>true</PayableElseWhere>
		<ChargeCode>BAF</ChargeCode>
		<ChargePrintLable>BUNKER ADJUST</ChargePrintLable>
		<Basis>1</Basis>
		<FreightRate>510</FreightRate>
		<CalculateMethod>40RQ</CalculateMethod>
		<ChargeAmount Currency="USD">510</ChargeAmount>
		<PaymentCurrency>USD</PaymentCurrency>
		<ControlOfficeCode>381</ControlOfficeCode>
		<CollectionOfficeCode>CAQ</CollectionOfficeCode>
		<TotalAmount Currency="USD">510</TotalAmount>
		<TotalAmtInPmtCurrency Currency="USD" ExchangeRate="1">510</TotalAmtInPmtCurrency>
		<SVVD>
			<ns0:Service xmlns:ns0="http://www.cargosmart.com/common">GEX2</ns0:Service>
			<ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">MOX</ns0:Vessel>
			<ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">MONTREAL EXPRESS</ns0:VesselName>
			<ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">214</ns0:Voyage>
			<ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
			<ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">9253741</ns0:LloydsNumber>
			<ns0:CallSign xmlns:ns0="http://www.cargosmart.com/common">MAHG5</ns0:CallSign>
		</SVVD>
		<ExchRateToEurope>0</ExchRateToEurope>
		<OBVoyRef>214 e</OBVoyRef>
		<IsApprovedForCustomer>true</IsApprovedForCustomer>
		<ChargeDesc>Bunker Adjustment Factor</ChargeDesc>
		<PayByInformation>
			<PayerName>SDV Logistiques ( Canada) Inc.</PayerName>
			<CarrierCustomerCode>3126812007</CarrierCustomerCode>
			<CityDetails>
				<ns0:City xmlns:ns0="http://www.cargosmart.com/common">Saint Laurent</ns0:City>
				<ns0:State xmlns:ns0="http://www.cargosmart.com/common">Quebec</ns0:State>
				<ns0:Country xmlns:ns0="http://www.cargosmart.com/common">Canada</ns0:Country>
			</CityDetails>
		</PayByInformation>
		<RatePercentage>100</RatePercentage>
	</FreightCharge>
	<FreightCharge>
		<DisplaySeqNumber>3</DisplaySeqNumber>
		<ChargeType>0</ChargeType>
		<UnBill>true</UnBill>
		<PayableElseWhere>true</PayableElseWhere>
		<ChargeCode>Ocean Freight</ChargeCode>
		<ChargePrintLable>Ocean Freight</ChargePrintLable>
		<Basis>1</Basis>
		<FreightRate>3993</FreightRate>
		<CalculateMethod>40RQ</CalculateMethod>
		<ChargeAmount Currency="USD">3993</ChargeAmount>
		<PaymentCurrency>USD</PaymentCurrency>
		<ControlOfficeCode>381</ControlOfficeCode>
		<CollectionOfficeCode>CAQ</CollectionOfficeCode>
		<TotalAmount Currency="USD">3993</TotalAmount>
		<TotalAmtInPmtCurrency Currency="USD" ExchangeRate="1">3993</TotalAmtInPmtCurrency>
		<SVVD>
			<ns0:Service xmlns:ns0="http://www.cargosmart.com/common">GEX2</ns0:Service>
			<ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">MOX</ns0:Vessel>
			<ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">MONTREAL EXPRESS</ns0:VesselName>
			<ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">214</ns0:Voyage>
			<ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
			<ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">9253741</ns0:LloydsNumber>
			<ns0:CallSign xmlns:ns0="http://www.cargosmart.com/common">MAHG5</ns0:CallSign>
		</SVVD>
		<ExchRateToEurope>0</ExchRateToEurope>
		<OBVoyRef>214 e</OBVoyRef>
		<IsApprovedForCustomer>true</IsApprovedForCustomer>
		<ChargeDesc>Ocean Freight</ChargeDesc>
		<PayByInformation>
			<PayerName>SDV Logistiques ( Canada) Inc.</PayerName>
			<CarrierCustomerCode>3126812007</CarrierCustomerCode>
			<CityDetails>
				<ns0:City xmlns:ns0="http://www.cargosmart.com/common">Saint Laurent</ns0:City>
				<ns0:State xmlns:ns0="http://www.cargosmart.com/common">Quebec</ns0:State>
				<ns0:Country xmlns:ns0="http://www.cargosmart.com/common">Canada</ns0:Country>
			</CityDetails>
		</PayByInformation>
		<RatePercentage>100</RatePercentage>
	</FreightCharge>
  </ns0:Body>
</ns0:BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)

		initMarkupBuilder()
		script.generateBody(bl.Body[0], null, bl.Body[0].FreightCharge, [], markupBuilder)
		node = xmlParserToNode(writer?.toString())

		Assert.assertEquals('24',node.L3.E81_01.text())
		Assert.assertEquals('G',node.L3.E187_02.text())
		Assert.assertEquals('452827',node.L3.E58_05.text())
		Assert.assertEquals('130.32',node.L3.E183_09.text())
		Assert.assertEquals('X',node.L3.E184_10.text())
		Assert.assertEquals('K',node.L3.E188_12.text())
	}

	@Test
	void testL1(){
		String xml = """
<ns0:BillOfLading xmlns:ns0="http://www.cargosmart.com/billoflading">
  <ns0:Body>
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
	<FreightCharge>
		<DisplaySeqNumber>2</DisplaySeqNumber>
		<ChargeType>0</ChargeType>
		<UnBill>true</UnBill>
		<PayableElseWhere>true</PayableElseWhere>
		<ChargeCode>BAF</ChargeCode>
		<ChargePrintLable>BUNKER ADJUST</ChargePrintLable>
		<Basis>1</Basis>
		<FreightRate>510</FreightRate>
		<CalculateMethod>40RQ</CalculateMethod>
		<ChargeAmount Currency="USD">510</ChargeAmount>
		<PaymentCurrency>USD</PaymentCurrency>
		<ControlOfficeCode>381</ControlOfficeCode>
		<CollectionOfficeCode>CAQ</CollectionOfficeCode>
		<TotalAmount Currency="USD">510</TotalAmount>
		<TotalAmtInPmtCurrency Currency="USD" ExchangeRate="1">510</TotalAmtInPmtCurrency>
		<SVVD>
			<ns0:Service xmlns:ns0="http://www.cargosmart.com/common">GEX2</ns0:Service>
			<ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">MOX</ns0:Vessel>
			<ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">MONTREAL EXPRESS</ns0:VesselName>
			<ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">214</ns0:Voyage>
			<ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
			<ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">9253741</ns0:LloydsNumber>
			<ns0:CallSign xmlns:ns0="http://www.cargosmart.com/common">MAHG5</ns0:CallSign>
		</SVVD>
		<ExchRateToEurope>0</ExchRateToEurope>
		<OBVoyRef>214 e</OBVoyRef>
		<IsApprovedForCustomer>true</IsApprovedForCustomer>
		<ChargeDesc>Bunker Adjustment Factor</ChargeDesc>
		<PayByInformation>
			<PayerName>SDV Logistiques ( Canada) Inc.</PayerName>
			<CarrierCustomerCode>3126812007</CarrierCustomerCode>
			<CityDetails>
				<ns0:City xmlns:ns0="http://www.cargosmart.com/common">Saint Laurent</ns0:City>
				<ns0:State xmlns:ns0="http://www.cargosmart.com/common">Quebec</ns0:State>
				<ns0:Country xmlns:ns0="http://www.cargosmart.com/common">Canada</ns0:Country>
			</CityDetails>
		</PayByInformation>
		<RatePercentage>100</RatePercentage>
	</FreightCharge>
	<FreightCharge>
		<DisplaySeqNumber>3</DisplaySeqNumber>
		<ChargeType>0</ChargeType>
		<UnBill>true</UnBill>
		<PayableElseWhere>true</PayableElseWhere>
		<ChargeCode>Ocean Freight</ChargeCode>
		<ChargePrintLable>Ocean Freight</ChargePrintLable>
		<Basis>1</Basis>
		<FreightRate>3993</FreightRate>
		<CalculateMethod>40RQ</CalculateMethod>
		<ChargeAmount Currency="USD">3993</ChargeAmount>
		<PaymentCurrency>USD</PaymentCurrency>
		<ControlOfficeCode>381</ControlOfficeCode>
		<CollectionOfficeCode>CAQ</CollectionOfficeCode>
		<TotalAmount Currency="USD">3993</TotalAmount>
		<TotalAmtInPmtCurrency Currency="USD" ExchangeRate="1">3993</TotalAmtInPmtCurrency>
		<SVVD>
			<ns0:Service xmlns:ns0="http://www.cargosmart.com/common">GEX2</ns0:Service>
			<ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">MOX</ns0:Vessel>
			<ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">MONTREAL EXPRESS</ns0:VesselName>
			<ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">214</ns0:Voyage>
			<ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
			<ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">9253741</ns0:LloydsNumber>
			<ns0:CallSign xmlns:ns0="http://www.cargosmart.com/common">MAHG5</ns0:CallSign>
		</SVVD>
		<ExchRateToEurope>0</ExchRateToEurope>
		<OBVoyRef>214 e</OBVoyRef>
		<IsApprovedForCustomer>true</IsApprovedForCustomer>
		<ChargeDesc>Ocean Freight</ChargeDesc>
		<PayByInformation>
			<PayerName>SDV Logistiques ( Canada) Inc.</PayerName>
			<CarrierCustomerCode>3126812007</CarrierCustomerCode>
			<CityDetails>
				<ns0:City xmlns:ns0="http://www.cargosmart.com/common">Saint Laurent</ns0:City>
				<ns0:State xmlns:ns0="http://www.cargosmart.com/common">Quebec</ns0:State>
				<ns0:Country xmlns:ns0="http://www.cargosmart.com/common">Canada</ns0:Country>
			</CityDetails>
		</PayByInformation>
		<RatePercentage>100</RatePercentage>
	</FreightCharge>
  </ns0:Body>
</ns0:BillOfLading>
		"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)

		initMarkupBuilder()
		script.generateBody(bl.Body[0], null, bl.Body[0].FreightCharge, [], markupBuilder)
		node = xmlParserToNode(writer?.toString())

		Assert.assertEquals(3,node.Loop_L1.size())
		Assert.assertEquals('2526.5',node.Loop_L1[0].L1.E58_04.text())
		Assert.assertEquals('DCF',node.Loop_L1[0].L1.E150_08.text())
		Assert.assertEquals('I/B DOC FEE',node.Loop_L1[0].L1.E276_12.text())
		Assert.assertEquals('51000',node.Loop_L1[1].L1.E58_04.text())
		Assert.assertEquals('BAF',node.Loop_L1[1].L1.E150_08.text())
		Assert.assertEquals('BUNKER ADJUST',node.Loop_L1[1].L1.E276_12.text())
		Assert.assertEquals('399300',node.Loop_L1[2].L1.E58_04.text())
		Assert.assertEquals('OCE',node.Loop_L1[2].L1.E150_08.text())
		Assert.assertEquals('Ocean Freight',node.Loop_L1[2].L1.E276_12.text())
	}

	@Test
	public void testChecking(){

		//Send only US-bound shipments. Obsolete the transaction if Last POD Country is not US.
		String xml = """
<BillOfLading xmlns="http://www.cargosmart.com/billoflading">
    <Body>
    <TransactionInformation>
            <ns0:MessageID xmlns:ns0="http://www.cargosmart.com/common">SDV_BL_2528612841_OOLU_20130121100602_16102050_999999999989835475</ns0:MessageID>
            <ns0:InterchangeTransactionID xmlns:ns0="http://www.cargosmart.com/common">999999999989835475</ns0:InterchangeTransactionID>
            <Action>UPD</Action>
        </TransactionInformation>
     <ns0:GeneralInformation>
      <ns0:SCACCode>OOLU</ns0:SCACCode>
      <ns0:BLNumber>3037112420</ns0:BLNumber>
    </ns0:GeneralInformation>
        <Route>             
            <FirstPOL>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
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
                <cs:Facility xmlns="http://www.cargosmart.com/shipment/billoflading" xmlns:cs="http://www.cargosmart.com/common">
                    <cs:FacilityName>Antwerp Gateway N.V. Quay 1700</cs:FacilityName>
                </cs:Facility>
                <OutboundSVVD>
                    <ns0:Service xmlns:ns0="http://www.cargosmart.com/common">NE3</ns0:Service>
                    <ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">CCU</ns0:Vessel>
                    <ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">COSCO DENMARK</ns0:VesselName>
                    <ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">015</ns0:Voyage>
                    <ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">E</ns0:Direction>
                    <ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">9516478</ns0:LloydsNumber>
                    <ns0:CallSign xmlns:ns0="http://www.cargosmart.com/common">VRNP8</ns0:CallSign>
                    <ns0:VesselNationality xmlns:ns0="http://www.cargosmart.com/common">Hong Kong</ns0:VesselNationality>
                    <DirectionName>East</DirectionName>
                    <VesselNationalityCode>HK</VesselNationalityCode>
                </OutboundSVVD>
                <DepartureDT>
                    <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="MET">2016-11-30T23:59:00</ns0:LocDT>
                </DepartureDT>
                <CSStateCode/>
                <CSParentCityID>2532764</CSParentCityID>
            </FirstPOL>
            <LastPOD>
                <ns0:Port xmlns:ns0="http://www.cargosmart.com/common">
                    <ns0:PortName>Weihai</ns0:PortName>
                    <ns0:PortCode>WEI</ns0:PortCode>
                    <ns0:City>Weihai</ns0:City>
                    <ns0:County>Weihai</ns0:County>
                    <ns0:State>Shandong</ns0:State>
                    <ns0:LocationCode>
                        <ns0:UNLocationCode>CNWEI</ns0:UNLocationCode>
                        <ns0:SchedKDType>K</ns0:SchedKDType>
                        <ns0:SchedKDCode>57000</ns0:SchedKDCode>
                    </ns0:LocationCode>
                    <ns0:Country>China</ns0:Country>
                    <ns0:CSPortID>116</ns0:CSPortID>
                    <ns0:CSCountryCode>CN</ns0:CSCountryCode>
                </ns0:Port>
                <cs:Facility xmlns="http://www.cargosmart.com/shipment/billoflading" xmlns:cs="http://www.cargosmart.com/common">
                    <cs:FacilityName>Weihai South Port</cs:FacilityName>
                </cs:Facility>
                <InboundSVVD>
                    <ns0:Service xmlns:ns0="http://www.cargosmart.com/common">CF8</ns0:Service>
                    <ns0:Vessel xmlns:ns0="http://www.cargosmart.com/common">Q4F</ns0:Vessel>
                    <ns0:VesselName xmlns:ns0="http://www.cargosmart.com/common">JINFUXING11</ns0:VesselName>
                    <ns0:Voyage xmlns:ns0="http://www.cargosmart.com/common">401</ns0:Voyage>
                    <ns0:Direction xmlns:ns0="http://www.cargosmart.com/common">N</ns0:Direction>
                    <ns0:LloydsNumber xmlns:ns0="http://www.cargosmart.com/common">0</ns0:LloydsNumber>
                    <DirectionName>North</DirectionName>
                </InboundSVVD>
                <ArrivalDT>
                    <ns0:LocDT xmlns:ns0="http://www.cargosmart.com/common" TimeZone="CST">2017-01-11T21:00:00</ns0:LocDT>
                </ArrivalDT>
                <CSStateCode>SD</CSStateCode>
                <CSParentCityID>2531318</CSParentCityID>
            </LastPOD>
        </Route>
    </Body>
</BillOfLading>
"""
		bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
		String[] runtimeParameters = ["B2BSessionID=abcde","MSG_REQ_ID=TEST-MSG-REQ-ID-0011","TP_ID=OFCDEPOT","MSG_TYPE_ID=BL","DIR_ID=O"]
		initMarkupBuilder()
		script.mapping(xml, runtimeParameters, conn)
		String bizKey = cs.b2b.core.common.session.B2BRuntimeSession.getSessionValue("abcde","PROMOTE_SESSION_BIZKEY")
		Assert.assertTrue(bizKey.contains("POD country is not US"))

	}
}










