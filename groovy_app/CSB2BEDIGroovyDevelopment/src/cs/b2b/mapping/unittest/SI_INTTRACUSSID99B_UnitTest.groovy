package cs.b2b.mapping.unittest

import cs.b2b.beluga.api.EDIProcessResult
import cs.b2b.beluga.common.fileparser.UIFFileParser
import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.BGM17
import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.Group_UNH13
import cs.b2b.core.mapping.bean.edi.xml.si.CS.ShippingInstructions
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.e2e.util.LocalFileUtil
import cs.b2b.mapping.scripts.CUS_IFTMIN_CS2SIXML_INTTRACUSSID99B
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import org.junit.Assert
import org.junit.Test
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.EDI_IFTMIN


import java.sql.Connection
import java.text.SimpleDateFormat

/**
 * Created by YOUAL on 6/8/2017.
 */
class SI_INTTRACUSSID99B_UnitTest {

	String BOFileNamePath = "./IG_Definition/CUS_D99B_IFTMIN_INTTRA.xml"
	String definitionBody = null;

    CUS_IFTMIN_CS2SIXML_INTTRACUSSID99B script = null;
    StringWriter writer = null

    MarkupBuilder markupBuilder = null

    Node node = null;
    Group_UNH13 UNH = null
    EDI_IFTMIN iftmin = null
    static Connection conn = null
    XmlBeanParser xmlBeanParser = new XmlBeanParser();
    BGM17 BGM = null

    cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil()

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
        script = new CUS_IFTMIN_CS2SIXML_INTTRACUSSID99B();

        script.conn = conn
    }

    private void initMarkupBuilder() {
        node = null
        UNH = new Group_UNH13()
        writer = new StringWriter()
        markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
    }

	private void initBelugaOcean(){
		File testBOFile = new File(BOFileNamePath);
		definitionBody = LocalFileUtil.readBigFile(testBOFile.getAbsolutePath());
		def p1 = ~/MinLength="(\d+)"/
		def p2 = ~/minOccurs="(\d+)"/
		definitionBody = definitionBody.replaceAll(p1, 'MinLength="0"')
		definitionBody = definitionBody.replaceAll(p2, 'minOccurs="0"')
	}

	private String xmlOutput(String edi){
		UIFFileParser parser = new UIFFileParser();
		EDIProcessResult ediResult = parser.edi2xml(edi, definitionBody);
		String testBOFileBody = ediResult.outputString==null?"":ediResult.outputString.toString();
		return testBOFileBody
	}

	@Test
	void test01() {
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
TSR+27+2'
CNT+7:123456789012345678:KGM'
LOC+57+AUMEL::86'
NAD+CN+FFFTO ORDER EADDD++DHL GLOTO ORDERBAL FORWARDING:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA35+O1+ORR'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		initMarkupBuilder()

		def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)

		node = xmlParserToNode(writer?.toString())

		//For GeneralInformation.IsToOrder -  "true" with NAD02_01 contains 'TO ORDER'
		Assert.assertEquals("true", node.Body.GeneralInformation.IsToOrder.text())

		//For GeneralInformation.ShipmentTrafficMode with TSR_02_01 = 2
		Assert.assertEquals("FCL", node.Body.GeneralInformation.ShipmentTrafficMode.OutBound.text())
		Assert.assertEquals("FCL", node.Body.GeneralInformation.ShipmentTrafficMode.InBound.text())
		Assert.assertEquals("FCL/FCL", node.Body.GeneralInformation.ShipmentTrafficMode.TrafficModeDescription.text())

		//For GeneralInformation.PlaceOfPayment.LocationCode.MutallyDefinedCode with Group1.LOC.C517.E3055_03="86"
		Assert.assertEquals("AUMEL", node.Body.GeneralInformation.PlaceOfPayment.LocationCode.MutuallyDefinedCode.text())

		//For GeneralInformation.TotalGrossWeight.WeightUnit with CNT.C270.E6411_03 = KGM
		Assert.assertEquals("KGS", node.Body.GeneralInformation.TotalGrossWeight.WeightUnit.text())

		//For City-case 1 : under party.Address with NAD01='CN' and 3039_01 container TO ORDER
		Assert.assertEquals("", node.Body.Party.Address.City.text())


	}

	@Test
	void test02() {
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
TSR+27+3'
CNT+7:123456789012345678:LBR'
LOC+57+AUMEL::87'
NAD+CN+O ORDER EADDD++DHL GLOTO ORDERBAL FORWARDING:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA35+O1+ORR'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)

		initMarkupBuilder()

		def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)
		node = xmlParserToNode(writer?.toString())

		//For IsToOrder "true" with NAD04_01 contains 'TO ORDER'
		Assert.assertEquals("true", node.Body.GeneralInformation.IsToOrder.text())

		//For ShipmentTrafficMode with TSR_02_01 = 3
		Assert.assertEquals("LCL", node.Body.GeneralInformation.ShipmentTrafficMode.OutBound.text())
		Assert.assertEquals("LCL", node.Body.GeneralInformation.ShipmentTrafficMode.InBound.text())
		Assert.assertEquals("LCL/LCL", node.Body.GeneralInformation.ShipmentTrafficMode.TrafficModeDescription.text())

		//For PlaceOfPayment.LocationCode.MutallyDefinedCode with Group1.LOC.C517.E3055_03="87"
		Assert.assertEquals("", node.Body.GeneralInformation.PlaceOfPayment.LocationCode.MutuallyDefinedCode.text())

		//MD line71 TotalGrossWeight.WeightUnit with CNT.C270.E6411_03 = LBR
		Assert.assertEquals("LBS", node.Body.GeneralInformation.TotalGrossWeight.WeightUnit.text())
	}

	@Test
	void test03() {
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
TSR+27+4'
CNT+15:123456789012345678:FTQ'
LOC+57+AUMEL::87'
NAD+CN+O ORDER EADDD++DHL GLOBAO ORDERL FORWARDING:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA35+O1+ORR'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		initMarkupBuilder()

		def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)
		node = xmlParserToNode(writer?.toString())

		//For GeneralInformation.IsToOrder  with NAD_3035 = “CN” and  no (NAD_3039 contains "TO ORDER" or NAD_C080_3036 contains “TO ORDER”) ,
		Assert.assertEquals("false", node.Body.GeneralInformation.IsToOrder.text())

		//For ShipmentTrafficMode with TSR_02_01 = 4
		Assert.assertEquals("", node.Body.GeneralInformation.ShipmentTrafficMode.OutBound.text())
		Assert.assertEquals("", node.Body.GeneralInformation.ShipmentTrafficMode.InBound.text())
		Assert.assertEquals("", node.Body.GeneralInformation.ShipmentTrafficMode.TrafficModeDescription.text())

		//MD line74 TotalConsignment.VolumeUnit with CNT.C270.E6411_03= FTQ and CNT[C270.E6069_01 = "15"][0]
		Assert.assertEquals("CBF", node.Body.GeneralInformation.TotalConsignment.VolumeUnit.text())
	}

	@Test
	void test_Party() {
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
CNT+15:123456789012345678:MTQ'
NAD+CA+++++OCA'
NAD+CN+++UNITERRA INTERNATIONAL CORfffffff35:dfasdfadfsafsdafsdffff CORfffffff35+6F NO.174 SEC.2aaaaaaaaaaaaaaaaaa35:TUN HUN SfffOUffffffffffffTH ROAD35:TAIPEIfffffff,TAIWAN,R.O.C.CTffff35:TfffffffffffssssfffffsffffOMMY WU35+CNN'
NAD+CS'
NAD+CZ'
CTA+IC+:LAST NAMEffFFFFFFFFFFffffffffffff35'
COM+hl.ops_shaaaaaaaaa60@1.com:EM'
COM+151515-1515-151551616:TE'
COM+212121-212121-21212212:FX'
NAD+EX'
CTA+NT+:First name ffffffffffffffffffffff35'
COM+hl.ofdsafdsaffffffdsa@2f.com:EM'
COM+123456789012345678901234567830:TE'
COM+111111111111111111111111111130:FX'
NAD+FP'
CTA+PJ+:First name'
COM+hl.ofdsafdsaffffffdsa@2f.com:EM'
COM+123456789012345678901234567830:TE'
COM+111111111111111111111111111130:FX'
NAD+FW'
NAD+GO'
NAD+HI'
NAD+IM'
NAD+NI'
NAD+N1+++ffffffff35:+aaaaaa ROAD35:::utntkutmtkMY WU35'
NAD+N2+++:ffffffff35+aaaaaa ROAD35:::utntkutmtkMY WU35'
NAD+ST+++UNITER+6F NO.174 SEC.2aaaaaaaaaaaaaaaaaa35::TAIPEIfffffC.CTffff35'
NAD+SU'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		println xmlOutput(edi)
		initMarkupBuilder()

		def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)
		node = xmlParserToNode(writer?.toString())
		println writer?.toString()
		//FOR TotalConsignment.VolumeUnit with CNT.C270.E6411_03= FTQ and CNT[C270.E6069_01 = "15"][0]
		Assert.assertEquals("CBM", node.Body.GeneralInformation.TotalConsignment.VolumeUnit.text())

		//FOR PartyType[Group11.NAD.E3035_01]
		Assert.assertEquals("CAR", node.Body.Party[0].PartyType.text())
		Assert.assertEquals("CGN", node.Body.Party[1].PartyType.text())
		Assert.assertEquals("SHP", node.Body.Party[2].PartyType.text())
		Assert.assertEquals("EXP", node.Body.Party[3].PartyType.text())
		Assert.assertEquals("PYR", node.Body.Party[4].PartyType.text())
		Assert.assertEquals("FWD", node.Body.Party[5].PartyType.text())
		Assert.assertEquals("GON", node.Body.Party[6].PartyType.text())
		Assert.assertEquals("SIR", node.Body.Party[7].PartyType.text())
		Assert.assertEquals("NPT", node.Body.Party[8].PartyType.text())
		Assert.assertEquals("ANP", node.Body.Party[9].PartyType.text())
		Assert.assertEquals("ANP", node.Body.Party[10].PartyType.text())
		Assert.assertEquals("STO", node.Body.Party[11].PartyType.text())


		//For partyName
		Assert.assertEquals("""UNITERRA INTERNATIONAL CORfffffff35
dfasdfadfsafsdafsdffff CORfffffff35""", node.Body.Party[1].PartyName.text())

        //For Contact
        Assert.assertEquals(1, node.Body.Party[2].Contact.size())
        //For Contact name under Party with Group12.CTA.E3139_01 = "IC"
		Assert.assertEquals("LAST NAMEffFFFFFFFFFFffffffffffff35", node.Body.Party[2].Contact.FirstName.text())
		Assert.assertEquals("",node.Body.Party[2].Contact.LastName.text())
		Assert.assertEquals("151515-1515-151551616",node.Body.Party[2].Contact.ContactPhone.Number.text())
		Assert.assertEquals("212121-212121-21212212",node.Body.Party[2].Contact.ContactFax.Number.text())
		Assert.assertEquals("hl.ops_shaaaaaaaaa60@1.com",node.Body.Party[2].Contact.ContactEmailAddress.text())

        //For Contact
        Assert.assertEquals(1, node.Body.Party[3].Contact.size())
        //For Contact name under Party with Group12.CTA.E3139_01 = "NT"
		Assert.assertEquals("First name ffffffffffffffffffffff35", node.Body.Party[3].Contact.FirstName.text())
		Assert.assertEquals("",node.Body.Party[3].Contact.LastName.text())
		Assert.assertEquals("123456789012345678901234567830",node.Body.Party[3].Contact.ContactPhone.Number.text())
		Assert.assertEquals("111111111111111111111111111130",node.Body.Party[3].Contact.ContactFax.Number.text())
		Assert.assertEquals("hl.ofdsafdsaffffffdsa@2f.com",node.Body.Party[3].Contact.ContactEmailAddress.text())

		//For Contact name under Party with Group12.CTA.E3139_01 = "PJ"---invaild case
		Assert.assertEquals("",node.Body.Party[4].Contact.FirstName.text())
		Assert.assertEquals("",node.Body.Party[4].Contact.LastName.text())
		Assert.assertEquals("",node.Body.Party[4].Contact.ContactPhone.Number.text())
		Assert.assertEquals("",node.Body.Party[4].Contact.ContactFax.Number.text())
		Assert.assertEquals("",node.Body.Party[4].Contact.ContactEmailAddress.text())

		//For PartyText concat Group11.NAD.C080.E3036_01, linefeed, Group11.NAD.C080.E3036_02, linefeed, oncat-sequence-format(Group11.NAD.C059.E3042, linefeed))
		Assert.assertEquals("""UNITERRA INTERNATIONAL CORfffffff35
dfasdfadfsafsdafsdffff CORfffffff35
6F NO.174 SEC.2aaaaaaaaaaaaaaaaaa35
TUN HUN SfffOUffffffffffffTH ROAD35
TAIPEIfffffff,TAIWAN,R.O.C.CTffff35
TfffffffffffssssfffffsffffOMMY WU35""",node.Body.Party[1].PartyText.text())

		//For PartyText concat-sequence-format(Group11.NAD.C059.E3042, linefeed))
		//NAD+N1+++ffffffff35:+aaaaaa ROAD35:::utntkutmtkMY WU35'
		Assert.assertEquals("""ffffffff35
aaaaaa ROAD35


utntkutmtkMY WU35""",node.Body.Party[9].PartyText.text())
		
		//NAD+N2+++:ffffffff35+aaaaaa ROAD35:::utntkutmtkMY WU35'
		Assert.assertEquals("""ffffffff35
aaaaaa ROAD35


utntkutmtkMY WU35""",node.Body.Party[10].PartyText.text())
		
		//NAD+ST+++UNITER+6F NO.174 SEC.2aaaaaaaaaaaaaaaaaa35::TAIPEIfffffC.CTffff35'
		//For PartyText concat Group11.NAD.C080.E3036_01, linefeed,  concat-sequence-format(Group11.NAD.C059.E3042, linefeed))
        Assert.assertEquals("""UNITER
6F NO.174 SEC.2aaaaaaaaaaaaaaaaaa35

TAIPEIfffffC.CTffff35""",node.Body.Party[11].PartyText.text())

		//For city-case 2 : NAD01 ！='CN'
		Assert.assertEquals("OCA", node.Body.Party[0].Address.City.text())
		//For city-case 3 : NAD01 ='CN' and NAD_3039 OR 3036 NOT contain 'TO ORDER'
		Assert.assertEquals("CNN", node.Body.Party[1].Address.City.text())
	}

    @Test
	void test_RFF() {
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
RFF+ADU:CNHU10111111'
RFF+BM:CNHU102'
RFF+BN:CNHU103'
RFF+CG:CNHU104'
RFF+CT:CNHU105'
RFF+ERN:CNHU106'
RFF+EX:CNHU101'
RFF+FF:CNHU101'
RFF+GN:CNHU101'
RFF+IV:CNHU101'
RFF+LC:CNHU101'
RFF+ON:CNHU101'
RFF+SI:CNHU101'
RFF+TN:CNHU101'
NAD+ST'
RFF+GN:CNHU15'
NAD+CG'
RFF+FF:CNHU16'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		
		initMarkupBuilder()

		def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)
		node = xmlParserToNode(writer?.toString())

		//FOR each Group3.RFF CSReferenceType
		Assert.assertEquals("BRF",node.Body.ExternalReference[0].CSReferenceType.text())
		//FOR each Group3.RFF ReferenceNumber
		Assert.assertEquals("CNHU10111111",node.Body.ExternalReference[0].ReferenceNumber.text())
		//FOR each Group3.RFF ReferenceDescription
		Assert.assertEquals("Broker reference 1",node.Body.ExternalReference[0].ReferenceDescription.text())
		//FOR each Group3.RFF EDIReferenceCode
		Assert.assertEquals("ADU",node.Body.ExternalReference[0].EDIReferenceCode.text())

		Assert.assertEquals("BL",node.Body.ExternalReference[1].CSReferenceType.text())
		Assert.assertEquals("Bill of lading number",node.Body.ExternalReference[1].ReferenceDescription.text())

		Assert.assertEquals("BKG",node.Body.ExternalReference[2].CSReferenceType.text())
		Assert.assertEquals("Booking reference number",node.Body.ExternalReference[2].ReferenceDescription.text())

		Assert.assertEquals("CGO",node.Body.ExternalReference[3].CSReferenceType.text())
		Assert.assertEquals("Consignee's order number",node.Body.ExternalReference[3].ReferenceDescription.text())

		Assert.assertEquals("CTR",node.Body.ExternalReference[4].CSReferenceType.text())
		Assert.assertEquals("Contract Number",node.Body.ExternalReference[4].ReferenceDescription.text())

		Assert.assertEquals("EXPR",node.Body.ExternalReference[5].CSReferenceType.text())
		Assert.assertEquals("Exporter's reference number",node.Body.ExternalReference[5].ReferenceDescription.text())

		Assert.assertEquals("EX",node.Body.ExternalReference[6].CSReferenceType.text())
		Assert.assertEquals("Export licence number",node.Body.ExternalReference[6].ReferenceDescription.text())

		Assert.assertEquals("FR",node.Body.ExternalReference[7].CSReferenceType.text())
		Assert.assertEquals("Freight forwarder's reference number",node.Body.ExternalReference[7].ReferenceDescription.text())

		Assert.assertEquals("GRN",node.Body.ExternalReference[8].CSReferenceType.text())
		Assert.assertEquals("Government reference number",node.Body.ExternalReference[8].ReferenceDescription.text())

		Assert.assertEquals("INV",node.Body.ExternalReference[9].CSReferenceType.text())
		Assert.assertEquals("Invoice number",node.Body.ExternalReference[9].ReferenceDescription.text())

		Assert.assertEquals("LEC",node.Body.ExternalReference[10].CSReferenceType.text())
		Assert.assertEquals("Letter of credit number",node.Body.ExternalReference[10].ReferenceDescription.text())

		Assert.assertEquals("PO",node.Body.ExternalReference[11].CSReferenceType.text())
		Assert.assertEquals("Order number (purchase)",node.Body.ExternalReference[11].ReferenceDescription.text())

		Assert.assertEquals("SID",node.Body.ExternalReference[12].CSReferenceType.text())
		Assert.assertEquals("SID (Shipper's identifying number for shipment)",node.Body.ExternalReference[12].ReferenceDescription.text())

		Assert.assertEquals("ITN",node.Body.ExternalReference[13].CSReferenceType.text())
		Assert.assertEquals("Transaction reference number",node.Body.ExternalReference[13].ReferenceDescription.text())

		//FOR GROUP15_RFF RFF+GN CSReferenceType
		Assert.assertEquals("GRN",node.Body.ExternalReference[14].CSReferenceType.text())
		//FOR GROUP15_RFF RFF+GN ReferenceNumber
		Assert.assertEquals("CNHU15",node.Body.ExternalReference[14].ReferenceNumber.text())
		//FOR GROUP15_RFF RFF+GN ReferenceDescription
		Assert.assertEquals("Government reference number",node.Body.ExternalReference[14].ReferenceDescription.text())
		//FOR GROUP15_RFF RFF+GN EDIReferenceCode
		Assert.assertEquals("GN",node.Body.ExternalReference[14].EDIReferenceCode.text())

		//FOR GROUP15_RFF RFF+FF
		Assert.assertEquals(null,node.Body.ExternalReference[15])
	}

	@Test
	void test_Container() {
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
EQD+++22U0'
MEA+AAE+WT+KGM:11111111115232.418'
MEA+AAE+WT+LBR:222225232.418'
MEA+AAE+T+KGM:555232.418'
MEA+AAE+T+LBR:11132.418'
MEA+AAE+AAW+FTQ:555232.418'
MEA+AAE+AAW+MTQ:11132.418'
TMP+2+025:CEL'
FTX+AGK++01+R60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80A'
FTX+AGK++01+R60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80A'
FTX+AGK++01+R60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80A'
EQD+++22U0'
MEA+AAE+WT+LBR:11111111115232.418'
MEA+AAE+T+LBR:66666132.418'
MEA+AAE+AAW+MTQ:11132.418'
SEL++CA'
SEL++AC'
SEL++CU'
SEL++SH'
SEL++TO'
TMP+2+-018:FAH'
FTX+AEB+++AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA175'
FTX+AEB+++AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA175AA'
EQD'
TMP+2+-55.5:FAH'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		initMarkupBuilder()

		def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)
		node = xmlParserToNode(writer?.toString())


		//For container.GrossWeight  with Group37.MEA.E6311_01 = "AAE" and Group37.MEA.C502_E6313_01 = "WT" [1] and unit as KGS
		Assert.assertEquals("KGS", node.Body.Container[0].GrossWeight.WeightUnit.text())
		//For container.TareWeight with Group37.MEA.E6311_01 = "AAE" and Group37.MEA.C502_E6313_01 = "T" [1] and unit as KGS
		Assert.assertEquals("KGS", node.Body.Container[0].TareWeight.WeightUnit.text())
		//For container.ContainerVolume with Group37.MEA.E6311_01 = "AAE" and Group37.MEA.C502_E6313_01 = "AAW" [1] and unit as FTQ
		Assert.assertEquals("CBF", node.Body.Container[0].ContainerVolume.VolumeUnit.text())

		//For container.GrossWeight  with Group37.MEA.E6311_01 = "AAE" and Group37.MEA.C502_E6313_01 = "WT" [1] and unit as LBS
		Assert.assertEquals("LBS", node.Body.Container[1].GrossWeight.WeightUnit.text())
		//For container.TareWeight  with Group37.MEA.E6311_01 = "AAE" and Group37.MEA.C502_E6313_01 = "T" [1] and unit as LBS
		Assert.assertEquals("LBS", node.Body.Container[1].TareWeight.WeightUnit.text())
		//For container.ContainerVolume with Group37.MEA.E6311_01 = "AAE" and Group37.MEA.C502_E6313_01 = "AAW" [1] and unit as MTQ
		Assert.assertEquals("CBM", node.Body.Container[1].ContainerVolume.VolumeUnit.text())

		//FOR Container.Remarks  WITH Group37.FTX.C108.4440 (1,240) when 4451=AGK
		Assert.assertEquals("R60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80AR60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80AR60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF ", node.Body.Container[0].Remarks.text())

		//FOR Container.Seal with Group37.SEL.C215.E9303_01 = "CA"
		Assert.assertEquals("Carrier", node.Body.Container[1].Seal[0].SealTypeName.text())
		//FOR Container.Seal with Group37.SEL.C215.E9303_01 = "AC"
		Assert.assertEquals("Quarantine agency", node.Body.Container[1].Seal[1].SealTypeName.text())
		//FOR Container.Seal with Group37.SEL.C215.E9303_01 = "CU"
		Assert.assertEquals("Customs", node.Body.Container[1].Seal[2].SealTypeName.text())
		//FOR Container.Seal with Group37.SEL.C215.E9303_01 = "SH"
		Assert.assertEquals("Shipper", node.Body.Container[1].Seal[3].SealTypeName.text())
		//FOR Container.Seal with Group37.SEL.C215.E9303_01 = "TO"
		Assert.assertEquals("Terminal operator", node.Body.Container[1].Seal[4].SealTypeName.text())


		///FOR Container.ReeferCargoSpec.Temperature with Group37.TMP.C239.E6246_01
		Assert.assertEquals("25", node.Body.Container[0].ReeferCargoSpec.Temperature.Temperature.text())
		Assert.assertEquals("-018", node.Body.Container[1].ReeferCargoSpec.Temperature.Temperature.text())
		Assert.assertEquals("-55.5", node.Body.Container[2].ReeferCargoSpec.Temperature.Temperature.text())
		//FOR Container.ReeferCargoSpec.Remarks with Group37.FTX.C108.4440 (1,350) when 4451=AEB
		Assert.assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA175AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA175", node.Body.Container[1].ReeferCargoSpec.Remarks.text())
	}

	@Test
	void test_Cargo_ContainerLoadPlan() {
        //FOR
//        GID+1 outer
//        SGP+
//        SGP+

        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+20:CR::6:CRATE'
SGP+UESU1111143416940+21111156'
MEA+AAE+WT+KGM:51.71115'
MEA+AAE+WT+LBR:52.71115'
MEA+AAE+AAW+FTQ:51.71115'
MEA+AAE+AAW+MTQ:52.71115'
SGP+UESU1111143416941+21111156'
MEA+AAE+WT+LBR:53.71115'
MEA+AAE+WT+KGM:54.71115'
MEA+AAE+AAW+MTQ:51.71115'
MEA+AAE+AAW+FTQ:52.71115'
EQD+CN+UESU1111143416940+22U0+1'
EQD+CN+UESU1111143416941+22U0+1'
"""
		//$linkingEQD = all Loop-Group37.EQD [ EQD.C237_02.E8260_01= currentGroup18_GID.Group29_SGP.C237_02.E8260_01]
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		initMarkupBuilder()

		def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)

		node = xmlParserToNode(writer?.toString())

		//For 1st Cargo.1st ContainerLoadPlan.GrossWeight with  Group30.MEA.E6311_01 = "AAE" and Group30.MEA.C502_E6313_01 = "WT" [1] and unit as KGM
		Assert.assertEquals("KGS", node.Body.Cargo.ContainerLoadPlan[0].GrossWeight.WeightUnit.text())

        //For 1stCargo.2ndContainerLoadPlan.GrossWeight with  Group30.MEA.E6311_01 = "AAE" and Group30.MEA.C502_E6313_01 = "WT" [1] and unit as LBR
        Assert.assertEquals("LBS", node.Body.Cargo.ContainerLoadPlan[1].GrossWeight.WeightUnit.text())

		//For 1stCargo.1stContainerLoadPlan.VolumeUnit with Group30.MEA.E6311_01 = "AAE" and Group30.MEA.C502_E6313_01 = "AAW" [1] and unit as FTQ
		Assert.assertEquals("CBF", node.Body.Cargo.ContainerLoadPlan[0].Volume.VolumeUnit.text())

		//For 1stCargo.2ndContainerLoadPlan.VolumeUnit with Group30.MEA.E6311_01 = "AAE" and Group30.MEA.C502_E6313_01 = "AAW" [1] and unit as MTQ
		Assert.assertEquals("CBM", node.Body.Cargo.ContainerLoadPlan[1].Volume.VolumeUnit.text())
	}

    @Test
    void test_Cargo_looping() {
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+12:CR::6:CRATE'
GID+2+34:CR::6:CRATE1'
UNH'
BGM+340++5'
GID+1+56:CR::6:CRATE'
GID+3+78:CR::6:CRATE1'
UNH'
BGM+340++5'
GID+1+90:CR::6:CRATE'
GID+1+91:CR::6:CRATE1'
UNH'
BGM+340++5'
GID++92:CR::6:CRATE'
GID++93:CR::6:CRATE1'
UNH'
BGM+340++5'
GID+1++94:CR::6:CRATE'
GID+2++95:CR::6:CRATE1'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
        initMarkupBuilder()


        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[1], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[2], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[3], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[4], 1, null, null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)
		node = xmlParserToNode(writer?.toString())

        //FOR Cargo looping with unique seq number as 1 and 2
        //GID+1 outer
        //GID+2 outer
		Assert.assertEquals(2, node.Body[0].Cargo.size())
		Assert.assertEquals("12", node.Body[0].Cargo[0].CargoInfo.Packaging.PackageQty.text())

		//FOR Cargo looping with unique seq number as 1 and 3
        //GID+1 outer
        //GID+3 outer
		Assert.assertEquals(2, node.Body[1].Cargo.size())
		Assert.assertEquals("56", node.Body[1].Cargo[0].CargoInfo.Packaging.PackageQty.text())

		//FOR Cargo looping with non-unique seq number as 1 and 1
        //GID+1 outer
        //GID+1 outer
		Assert.assertEquals(2, node.Body[2].Cargo.size())
		Assert.assertEquals("90", node.Body[2].Cargo[0].CargoInfo.Packaging.PackageQty.text())

		//FOR Cargo looping with no unique seq number e.g. GID++outer
        //GID+ outer
        //GID+ outer
		Assert.assertEquals(2, node.Body[3].Cargo.size())
		Assert.assertEquals("92", node.Body[3].Cargo[0].CargoInfo.Packaging.PackageQty.text())

		//FOR Cargo looping with only inner GID
        //GID+1 inner
        //GID+2 inner
		Assert.assertEquals(0, node.Body[4].Cargo.size())
    }

    @Test
    void test_Cargo_CargoNature() {
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+12:CR::6:CRATE'
SGP+UESU1111143416940+21111156'
DGS+IMD'
GID+2+34:CR::6:CRATE1'
SGP+UESU1111143416941+21111156'
DGS+IMD'
GID+3+34:CR::6:CRATE1'
SGP+UESU1111143416942+21111156'
GID+4+34:CR::6:CRATE1'
SGP+UESU1111143416943+21111156'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
EQD+CN+UESU1111143416941+22U0+1'
EQD+CN+UESU1111143416942+22U0+1'
TMP+2+-018:FAH'
EQD+CN+UESU1111143416943+22U0+1'
UNH'
BGM+340++5'
GID+1+56:CR::6:CRATE'
SGP+UESU1111143416940+21111156'
DGS+IMD'
GID+3+78:CR::6:CRATE1'
SGP+UESU1111143416941+21111156'
DGS+IMD'
GID+5+78:CR::6:CRATE1'
SGP+UESU1111143416942+21111156'
GID+7+78:CR::6:CRATE1'
SGP+UESU1111143416943+21111156'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
EQD+CN+UESU1111143416941+22U0+1'
EQD+CN+UESU1111143416942+22U0+1'
TMP+2+-018:FAH'
EQD+CN+UESU1111143416943+22U0+1'
UNH'
BGM+340++5'
GID+1+90:CR::6:CRATE'
SGP+UESU1111143416940+21111156'
DGS+IMD'
GID+1+91:CR::6:CRATE1'
SGP+UESU1111143416941+21111156'
DGS+IMD'
GID+1+92:CR::6:CRATE1'
SGP+UESU1111143416942+21111156'
GID+1+93:CR::6:CRATE1'
SGP+UESU1111143416943+21111156'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
EQD+CN+UESU1111143416941+22U0+1'
EQD+CN+UESU1111143416942+22U0+1'
TMP+2+-018:FAH'
EQD+CN+UESU1111143416943+22U0+1'
UNH'
BGM+340++5'
GID++92:CR::6:CRATE'
SGP+UESU1111143416940+21111156'
DGS+IMD'
GID++93:CR::6:CRATE1'
SGP+UESU1111143416941+21111156'
DGS+IMD'
GID++93:CR::6:CRATE1'
SGP+UESU1111143416942+21111156'
GID++93:CR::6:CRATE1'
SGP+UESU1111143416943+21111156'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
EQD+CN+UESU1111143416941+22U0+1'
EQD+CN+UESU1111143416942+22U0+1'
TMP+2+-018:FAH'
EQD+CN+UESU1111143416943+22U0+1'
UNH'
BGM+340++5'
GID+1++94:CR::6:CRATE'
GID+2++95:CR::6:CRATE1'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[1], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[2], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[3], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[4], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)
        node = xmlParserToNode(writer?.toString())


        //For CargoNature RD with $GID_Set Group32.DGS and $linkingEQD.TMP exists
        Assert.assertEquals("RD", node.Body[0].Cargo[0].CargoInfo.CargoNature.text())

        //FOR CargoNature DG with $GID_SetGroup32.DGS segment exists
        Assert.assertEquals("DG", node.Body[0].Cargo[1].CargoInfo.CargoNature.text())

        //FOR CargoNature RF with $linkingEQD.TMP segment exists
        Assert.assertEquals("RF", node.Body[0].Cargo[2].CargoInfo.CargoNature.text())

        //FOR CargoNature GC with no TMP and no DGS
        Assert.assertEquals("GC", node.Body[0].Cargo[3].CargoInfo.CargoNature.text())


        //Assert.assertEquals("GC", node.Body[0].GeneralInformation.ShipmentCargoType.text())

        //FOR Cargo looping with unique seq number as 1 and 3
        //GID+1 outer
        //GID+3 outer
        //For CargoNature RD with $GID_Set Group32.DGS and $linkingEQD.TMP exists
        Assert.assertEquals("RD", node.Body[1].Cargo[0].CargoInfo.CargoNature.text())

        //FOR CargoNature DG with $GID_SetGroup32.DGS segment exists
        Assert.assertEquals("DG", node.Body[1].Cargo[1].CargoInfo.CargoNature.text())

        //FOR CargoNature RF with $linkingEQD.TMP segment exists
        Assert.assertEquals("RF", node.Body[1].Cargo[2].CargoInfo.CargoNature.text())

        //FOR CargoNature GC with no TMP and no DGS
        Assert.assertEquals("GC", node.Body[1].Cargo[3].CargoInfo.CargoNature.text())

        //FOR Cargo looping with non-unique seq number as 1 and 1
        //GID+1 outer
        //GID+1 outer
        //For CargoNature RD with $GID_Set Group32.DGS and $linkingEQD.TMP exists
        Assert.assertEquals("RD", node.Body[2].Cargo[0].CargoInfo.CargoNature.text())

        //FOR CargoNature DG with $GID_SetGroup32.DGS segment exists
        Assert.assertEquals("DG", node.Body[2].Cargo[1].CargoInfo.CargoNature.text())

        //FOR CargoNature RF with $linkingEQD.TMP segment exists
        Assert.assertEquals("RF", node.Body[2].Cargo[2].CargoInfo.CargoNature.text())

        //FOR CargoNature GC with no TMP and no DGS
        Assert.assertEquals("GC", node.Body[2].Cargo[3].CargoInfo.CargoNature.text())

//        //FOR Cargo looping with no unique seq number e.g. GID++outer
//        //GID+ outer
//        //GID+ outer
        //For CargoNature RD with $GID_Set Group32.DGS and $linkingEQD.TMP exists
        Assert.assertEquals("RD", node.Body[3].Cargo[0].CargoInfo.CargoNature.text())

        //FOR CargoNature DG with $GID_SetGroup32.DGS segment exists
        Assert.assertEquals("DG", node.Body[3].Cargo[1].CargoInfo.CargoNature.text())

        //FOR CargoNature RF with $linkingEQD.TMP segment exists
        Assert.assertEquals("RF", node.Body[3].Cargo[2].CargoInfo.CargoNature.text())

        //FOR CargoNature GC with no TMP and no DGS
        Assert.assertEquals("GC", node.Body[3].Cargo[3].CargoInfo.CargoNature.text())

//        //FOR Cargo looping with only inner GID
//        //GID+1 inner
//        //GID+2 inner
        Assert.assertEquals(0, node.Body[4].Cargo.size())
        Assert.assertEquals("", node.Body[4].Cargo.CargoInfo.CargoNature.text())
    }

    @Test
    void test_ShipmentCargoType() {
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+12:CR::6:CRATE'
GID+1++12:CR::6:CRATE'
DGS+IMD'
GID+2+34:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
UNH'
BGM+340++5'
GID+1+12:CR::6:CRATE'
GID+1++12:CR::6:CRATE'
GID+2+34:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
UNH'
BGM+340++5'
GID+1+12:CR::6:CRATE'
GID+1++12:CR::6:CRATE'
DGS+IMD'
GID+2+34:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
UNH'
BGM+340++5'
GID+1+12:CR::6:CRATE'
GID+1++12:CR::6:CRATE'
GID+2+34:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
UNH'
BGM+340++5'
GID+1+90:CR::6:CRATE'
DGS+IMD'
GID+1+91:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
UNH'
BGM+340++5'
GID+1+90:CR::6:CRATE'
GID+1+91:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
UNH'
BGM+340++5'
GID+1+90:CR::6:CRATE'
DGS+IMD'
GID+1+91:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
UNH'
BGM+340++5'
GID+1+90:CR::6:CRATE'
GID+1+91:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
UNH'
BGM+340++5'
GID+1+91:CR::6:CRATE1'
GID+1++90:CR::6:CRATE'
DGS+IMD'
GID+1+91:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
UNH'
BGM+340++5'
GID++92:CR::6:CRATE'
DGS+IMD'
GID++93:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
UNH'
BGM+340++5'
GID++92:CR::6:CRATE'
GID++93:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
UNH'
BGM+340++5'
GID++92:CR::6:CRATE'
DGS+IMD'
GID++93:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
TMP+2+-018:FAH'
UNH'
BGM+340++5'
GID++92:CR::6:CRATE'
GID++93:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
UNH'
BGM+340++5'
GID++92:CR::6:CRATE'
GID+++92:CR::6:CRATE'
DGS+IMD'
GID++93:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
UNH'
BGM+340++5'
GID+1++94:CR::6:CRATE'
GID+2++95:CR::6:CRATE1'
EQD+CN+UESU1111143416940+22U0+1'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)

        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[1], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[2], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[3], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[4], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[5], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[6], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[7], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[8], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[9], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[10], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[11], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[12], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[13], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[14], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)

        node = xmlParserToNode(writer?.toString())

        //FOR unique seq number case ShipmentCargoType
        //GID+1 outer
        //GID+2 outer
        Assert.assertEquals("DG", node.Body[0].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("RF", node.Body[1].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("RD", node.Body[2].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("GC", node.Body[3].GeneralInformation.ShipmentCargoType.text())

        //FOR non-unique seq number case ShipmentCargoType
        //GID+1 outer
        //GID+1 outer
        Assert.assertEquals("DG", node.Body[4].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("RF", node.Body[5].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("RD", node.Body[6].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("GC", node.Body[7].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("GC", node.Body[8].GeneralInformation.ShipmentCargoType.text())

        //FOR non-unique seq number case ShipmentCargoType
        //GID+ outer
        //GID+ outer
        Assert.assertEquals("DG", node.Body[9].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("RF", node.Body[10].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("RD", node.Body[11].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("GC", node.Body[12].GeneralInformation.ShipmentCargoType.text())
        Assert.assertEquals("GC", node.Body[13].GeneralInformation.ShipmentCargoType.text())

        //FOR unique seq num with only inner GID
        //GID+1 inner
        //GID+2 inner
        Assert.assertEquals(0, node.Body[14].Cargo.size())
        Assert.assertEquals("GC", node.Body[14].GeneralInformation.ShipmentCargoType.text())
    }

    @Test
    void test_Cargo() {
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+12:CR::6:CRATE'
FTX+AAA+++A:B:C'
FTX+AAA+++A1:B1:C1'
MEA+AAE+WT+KGM:32.4'
MEA+AAE+WT+LBR:42.4'
MEA+AAE+AAW+FTQ:32.4'
MEA+AAE+AAW+MTQ:42.4'
RFF+ABT:1'
RFF+ABW:2'
RFF+ADU:3'
RFF+AFG:4'
RFF+AKG:5'
RFF+BH:6'
RFF+BN:7'
RFF+CG:8'
RFF+CT:9'
RFF+ED:10'
RFF+IV:11'
RFF+ON:12'
RFF+RF:13'
RFF+TN:14'
PCI++A1:A2:A3:A4:A5:A6:A7:A8:A9:A10'
PCI++A11:A12:A13:A14:A15:A16:A17:A18:A19:A20'
GID+1++12:CR::6:CRATE0'
GID+2+34:CR::6:CRATE1'
MEA+AAE+WT+LBR:2.4'
MEA+AAE+WT+KGM:3.4'
MEA+AAE+AAW+MTQ:32.4'
MEA+AAE+AAW+FTQ:42.4'
PCI++A1::A3:::::::A10'
PCI++A6'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
        initMarkupBuilder()


        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)
        node = xmlParserToNode(writer?.toString())


        Assert.assertEquals("A\nB\nC\nA1\nB1\nC1", node.Body[0].Cargo[0].CargoInfo.CargoDescription.text())
        Assert.assertEquals("CRATE", node.Body[0].Cargo[0].CargoInfo.Packaging.PackageDesc.text())

        //FOR GrossWeight with currentGroup18_GID.Group20.MEA.E6311_01 = "AAE" and Group20.MEA.C502.E6313_01 = "WT" [1] as KGM
        Assert.assertEquals("KGS", node.Body[0].Cargo[0].CargoInfo.GrossWeight.WeightUnit.text())
        //FOR GrossWeight with currentGroup18_GID.Group20.MEA.E6311_01 = "AAE" and Group20.MEA.C502.E6313_01 = "WT" [1] as LBR
        Assert.assertEquals("LBS", node.Body[0].Cargo[1].CargoInfo.GrossWeight.WeightUnit.text())

        //FOR GrossWeight with currentGroup18_GID.Group20.MEA.E6311_01 = "AAE" and Group20.MEA.C502.E6313_01 = "WT" [1] as KGM
        Assert.assertEquals("CBF", node.Body[0].Cargo[0].CargoInfo.Volume.VolumeUnit.text())
        //FOR GrossWeight with currentGroup18_GID.Group20.MEA.E6311_01 = "AAE" and Group20.MEA.C502.E6313_01 = "WT" [1] as LBR
        Assert.assertEquals("CBM", node.Body[0].Cargo[1].CargoInfo.Volume.VolumeUnit.text())

        //FOR MarksAndNumbers with Loop for each Group23.PCI under currentGroup18_GID and Loop for each C210.E7102
        Assert.assertEquals("1", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[0].SeqNumber.text())
        Assert.assertEquals("A1", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[0].MarksAndNumbersLine.text())

        Assert.assertEquals("2", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[1].SeqNumber.text())
        Assert.assertEquals("A2", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[1].MarksAndNumbersLine.text())

        Assert.assertEquals("3", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[2].SeqNumber.text())
        Assert.assertEquals("A3", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[2].MarksAndNumbersLine.text())

        Assert.assertEquals("4", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[3].SeqNumber.text())
        Assert.assertEquals("A4", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[3].MarksAndNumbersLine.text())

        Assert.assertEquals("5", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[4].SeqNumber.text())
        Assert.assertEquals("A5", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[4].MarksAndNumbersLine.text())

        Assert.assertEquals("6", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[5].SeqNumber.text())
        Assert.assertEquals("A6", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[5].MarksAndNumbersLine.text())

        Assert.assertEquals("7", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[6].SeqNumber.text())
        Assert.assertEquals("A7", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[6].MarksAndNumbersLine.text())

        Assert.assertEquals("8", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[7].SeqNumber.text())
        Assert.assertEquals("A8", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[7].MarksAndNumbersLine.text())

        Assert.assertEquals("9", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[8].SeqNumber.text())
        Assert.assertEquals("A9", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[8].MarksAndNumbersLine.text())

        Assert.assertEquals("10", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[9].SeqNumber.text())
        Assert.assertEquals("A10", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[9].MarksAndNumbersLine.text())

        Assert.assertEquals("11", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[10].SeqNumber.text())
        Assert.assertEquals("A11", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[10].MarksAndNumbersLine.text())

        Assert.assertEquals("12", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[11].SeqNumber.text())
        Assert.assertEquals("A12", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[11].MarksAndNumbersLine.text())

        Assert.assertEquals("13", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[12].SeqNumber.text())
        Assert.assertEquals("A13", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[12].MarksAndNumbersLine.text())

        Assert.assertEquals("14", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[13].SeqNumber.text())
        Assert.assertEquals("A14", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[13].MarksAndNumbersLine.text())

        Assert.assertEquals("15", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[14].SeqNumber.text())
        Assert.assertEquals("A15", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[14].MarksAndNumbersLine.text())

        Assert.assertEquals("16", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[15].SeqNumber.text())
        Assert.assertEquals("A16", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[15].MarksAndNumbersLine.text())

        Assert.assertEquals("17", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[16].SeqNumber.text())
        Assert.assertEquals("A17", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[16].MarksAndNumbersLine.text())

        Assert.assertEquals("18", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[17].SeqNumber.text())
        Assert.assertEquals("A18", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[17].MarksAndNumbersLine.text())

        Assert.assertEquals("19", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[18].SeqNumber.text())
        Assert.assertEquals("A19", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[18].MarksAndNumbersLine.text())

        Assert.assertEquals("20", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[19].SeqNumber.text())
        Assert.assertEquals("A20", node.Body[0].Cargo[0].CargoInfo.MarksAndNumbers[19].MarksAndNumbersLine.text())


        Assert.assertEquals("1", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[0].SeqNumber.text())
        Assert.assertEquals("A1", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[0].MarksAndNumbersLine.text())

        Assert.assertEquals("2", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[1].SeqNumber.text())
        Assert.assertEquals("", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[1].MarksAndNumbersLine.text())

        Assert.assertEquals("3", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[2].SeqNumber.text())
        Assert.assertEquals("A3", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[2].MarksAndNumbersLine.text())

        Assert.assertEquals("4", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[3].SeqNumber.text())
        Assert.assertEquals("", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[3].MarksAndNumbersLine.text())

        Assert.assertEquals("5", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[4].SeqNumber.text())
        Assert.assertEquals("", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[4].MarksAndNumbersLine.text())

        Assert.assertEquals("6", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[5].SeqNumber.text())
        Assert.assertEquals("", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[5].MarksAndNumbersLine.text())

        Assert.assertEquals("7", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[6].SeqNumber.text())
        Assert.assertEquals("", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[6].MarksAndNumbersLine.text())

        Assert.assertEquals("8", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[7].SeqNumber.text())
        Assert.assertEquals("", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[7].MarksAndNumbersLine.text())

        Assert.assertEquals("9", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[8].SeqNumber.text())
        Assert.assertEquals("", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[8].MarksAndNumbersLine.text())

        Assert.assertEquals("10", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[9].SeqNumber.text())
        Assert.assertEquals("A10", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[9].MarksAndNumbersLine.text())

        Assert.assertEquals("11", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[10].SeqNumber.text())
        Assert.assertEquals("A6", node.Body[0].Cargo[1].CargoInfo.MarksAndNumbers[10].MarksAndNumbersLine.text())

        //FOR Cargo.CargoInfo.ExternalReference with Loop Group22.RFF under currentGroup18_GID
        Assert.assertEquals("ABT", node.Body[0].Cargo[0].CargoInfo.ExternalReference[0].CSReferenceType.text())
        Assert.assertEquals("1", node.Body[0].Cargo[0].CargoInfo.ExternalReference[0].ReferenceNumber.text())
        Assert.assertEquals("Customs declaration number", node.Body[0].Cargo[0].CargoInfo.ExternalReference[0].ReferenceDescription.text())

        Assert.assertEquals("SKU", node.Body[0].Cargo[0].CargoInfo.ExternalReference[1].CSReferenceType.text())
        Assert.assertEquals("2", node.Body[0].Cargo[0].CargoInfo.ExternalReference[1].ReferenceNumber.text())
        Assert.assertEquals("Stock keeping unit number", node.Body[0].Cargo[0].CargoInfo.ExternalReference[1].ReferenceDescription.text())

        Assert.assertEquals("BRF", node.Body[0].Cargo[0].CargoInfo.ExternalReference[2].CSReferenceType.text())
        Assert.assertEquals("3", node.Body[0].Cargo[0].CargoInfo.ExternalReference[2].ReferenceNumber.text())
        Assert.assertEquals("Broker reference 1", node.Body[0].Cargo[0].CargoInfo.ExternalReference[2].ReferenceDescription.text())

        Assert.assertEquals("TARIF", node.Body[0].Cargo[0].CargoInfo.ExternalReference[3].CSReferenceType.text())
        Assert.assertEquals("4", node.Body[0].Cargo[0].CargoInfo.ExternalReference[3].ReferenceNumber.text())
        Assert.assertEquals("Tariff number", node.Body[0].Cargo[0].CargoInfo.ExternalReference[3].ReferenceDescription.text())

        Assert.assertEquals("MVID", node.Body[0].Cargo[0].CargoInfo.ExternalReference[4].CSReferenceType.text())
        Assert.assertEquals("5", node.Body[0].Cargo[0].CargoInfo.ExternalReference[4].ReferenceNumber.text())
        Assert.assertEquals("Vehicle Identification Number (VIN)", node.Body[0].Cargo[0].CargoInfo.ExternalReference[4].ReferenceDescription.text())

        Assert.assertEquals("BH", node.Body[0].Cargo[0].CargoInfo.ExternalReference[5].CSReferenceType.text())
        Assert.assertEquals("6", node.Body[0].Cargo[0].CargoInfo.ExternalReference[5].ReferenceNumber.text())
        Assert.assertEquals("House bill of lading number", node.Body[0].Cargo[0].CargoInfo.ExternalReference[5].ReferenceDescription.text())

        Assert.assertEquals("BKG", node.Body[0].Cargo[0].CargoInfo.ExternalReference[6].CSReferenceType.text())
        Assert.assertEquals("7", node.Body[0].Cargo[0].CargoInfo.ExternalReference[6].ReferenceNumber.text())
        Assert.assertEquals("Booking reference number", node.Body[0].Cargo[0].CargoInfo.ExternalReference[6].ReferenceDescription.text())

        Assert.assertEquals("CGO", node.Body[0].Cargo[0].CargoInfo.ExternalReference[7].CSReferenceType.text())
        Assert.assertEquals("8", node.Body[0].Cargo[0].CargoInfo.ExternalReference[7].ReferenceNumber.text())
        Assert.assertEquals("Consignee's order number", node.Body[0].Cargo[0].CargoInfo.ExternalReference[7].ReferenceDescription.text())

        Assert.assertEquals("SC", node.Body[0].Cargo[0].CargoInfo.ExternalReference[8].CSReferenceType.text())
        Assert.assertEquals("9", node.Body[0].Cargo[0].CargoInfo.ExternalReference[8].ReferenceNumber.text())
        Assert.assertEquals("Contract number", node.Body[0].Cargo[0].CargoInfo.ExternalReference[8].ReferenceDescription.text())

        Assert.assertEquals("ED", node.Body[0].Cargo[0].CargoInfo.ExternalReference[9].CSReferenceType.text())
        Assert.assertEquals("10", node.Body[0].Cargo[0].CargoInfo.ExternalReference[9].ReferenceNumber.text())
        Assert.assertEquals("Export declaration", node.Body[0].Cargo[0].CargoInfo.ExternalReference[9].ReferenceDescription.text())

        Assert.assertEquals("INV", node.Body[0].Cargo[0].CargoInfo.ExternalReference[10].CSReferenceType.text())
        Assert.assertEquals("11", node.Body[0].Cargo[0].CargoInfo.ExternalReference[10].ReferenceNumber.text())
        Assert.assertEquals("Export declaration", node.Body[0].Cargo[0].CargoInfo.ExternalReference[10].ReferenceDescription.text())

        Assert.assertEquals("PO", node.Body[0].Cargo[0].CargoInfo.ExternalReference[11].CSReferenceType.text())
        Assert.assertEquals("12", node.Body[0].Cargo[0].CargoInfo.ExternalReference[11].ReferenceNumber.text())
        Assert.assertEquals("Order number (purchase)", node.Body[0].Cargo[0].CargoInfo.ExternalReference[11].ReferenceDescription.text())

        Assert.assertEquals("EXPR", node.Body[0].Cargo[0].CargoInfo.ExternalReference[12].CSReferenceType.text())
        Assert.assertEquals("13", node.Body[0].Cargo[0].CargoInfo.ExternalReference[12].ReferenceNumber.text())
        Assert.assertEquals("Export reference number", node.Body[0].Cargo[0].CargoInfo.ExternalReference[12].ReferenceDescription.text())

        Assert.assertEquals("ITN", node.Body[0].Cargo[0].CargoInfo.ExternalReference[13].CSReferenceType.text())
        Assert.assertEquals("14", node.Body[0].Cargo[0].CargoInfo.ExternalReference[13].ReferenceNumber.text())
        Assert.assertEquals("Transaction reference number", node.Body[0].Cargo[0].CargoInfo.ExternalReference[13].ReferenceDescription.text())
    }

	@Test
    void test_DGCargoSpec__uniqueSeqNum_with1outeGID_DGD_under_outerGID(){
        //FOR unique seq number
//        GID+1 outer
//        DGS+
//                GID+1 inner
//                DGS+
//                GID+1 inner
//                DGS+
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+20:CR::6:CRATE'
DGS+IM1+++++454545'
CTA+HG+:CC,DD'
COM+111111111111111111111:TE'
GID+1++30:CR::6:CRATE1'
DGS+IM1+++++676767'
FTX+AAD+++A'
FTX+AAD+++B'
FTX+AAD+++C'
CTA+HG+:FF,AA'
COM+1234-11111-22222:TE'
GID+1++40:CR::6:CRATE2'
DGS+IM1+++++898989'
FTX+AAD+++E'
FTX+AAD+++F'
FTX+AAD+++G'
CTA+HG+:GG,BB'
COM+5678-222222-33333:TE'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        //script.generateBody(markupBuilder, iftmin.Group_UNH[1], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)
        node = xmlParserToNode(writer?.toString())


        Assert.assertEquals(1, node.Body.Cargo.size())
        Assert.assertEquals(2, node.Body.Cargo.DGCargoSpec.size())
        //Concat first three Group32.FTX.C108.E4440_01 by <Space>，which Group32.FTX.E4451_01 = "AAD
        Assert.assertEquals("A B C", node.Body.Cargo.DGCargoSpec[0].TechnicalName.text())
        Assert.assertEquals("E F G", node.Body.Cargo.DGCargoSpec[1].TechnicalName.text())

        //FOR 1ST InnerPackageDescription
        Assert.assertEquals(1, node.Body.Cargo.DGCargoSpec[0].PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("30", node.Body.Cargo.DGCargoSpec[0].PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE1", node.Body.Cargo.DGCargoSpec[0].PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR  2ND InnerPackageDescription
        Assert.assertEquals(1, node.Body.Cargo.DGCargoSpec[1].PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("40", node.Body.Cargo.DGCargoSpec[1].PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE2", node.Body.Cargo.DGCargoSpec[1].PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 1ST OuterPackageDescription
        Assert.assertEquals(1, node.Body.Cargo.DGCargoSpec[0].PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body.Cargo.DGCargoSpec[0].PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body.Cargo.DGCargoSpec[0].PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR S2ND OuterPackageDescription
        Assert.assertEquals(1, node.Body.Cargo.DGCargoSpec[1].PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body.Cargo.DGCargoSpec[1].PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body.Cargo.DGCargoSpec[1].PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR EMSNumber
        Assert.assertEquals("676767", node.Body.Cargo.DGCargoSpec[0].EMSNumber.text())
        Assert.assertEquals("898989", node.Body.Cargo.DGCargoSpec[1].EMSNumber.text())

        //FOR EmergencyContact.FirstName
        Assert.assertEquals("FF,AA", node.Body.Cargo.DGCargoSpec[0].EmergencyContact.FirstName.text())
        Assert.assertEquals("GG,BB", node.Body.Cargo.DGCargoSpec[1].EmergencyContact.FirstName.text())

        //FOR EmergencyContact.ContactPhone.Number
        Assert.assertEquals("1234-11111-22222", node.Body.Cargo.DGCargoSpec[0].EmergencyContact.ContactPhone.Number.text())
        Assert.assertEquals("5678-222222-33333", node.Body.Cargo.DGCargoSpec[1].EmergencyContact.ContactPhone.Number.text())
    }

    @Test
    void test_DGCargoSpec__uniqueSeqNum_with2outeGID(){
        //FOR unique seq number
//        GID+3 outer
//                GID+1 inner
//                DGS+
//                GID+1 inner
//                DGS+
//        GID+1 outer
//        DGS+
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+3+20:CR::6:CRATE'
GID+1++30:CR::6:CRATE1'
DGS+IM1'
GID+1++40:CR::6:CRATE2'
DGS+IM1'
GID+1+20:CR::6:CRATE'
DGS+IM1'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)
        node = xmlParserToNode(writer?.toString())

        Assert.assertEquals(2, node.Body.Cargo.size())
        Assert.assertEquals(0, node.Body.Cargo[0].DGCargoSpec.size())
        Assert.assertEquals(2, node.Body.Cargo[1].DGCargoSpec.size())

        //FOR 2nd Cargo.1stDGCargoSpec.1stInnerPackageDescription
        Assert.assertEquals(1, node.Body.Cargo[1].DGCargoSpec[0].PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("30", node.Body.Cargo[1].DGCargoSpec[0].PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE1", node.Body.Cargo[1].DGCargoSpec[0].PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 2nd Cargo.2nd DGCargoSpec.2ndInnerPackageDescription
        Assert.assertEquals(1, node.Body.Cargo[1].DGCargoSpec[1].PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("40", node.Body.Cargo[1].DGCargoSpec[1].PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE2", node.Body.Cargo[1].DGCargoSpec[1].PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 2nd Cargo.1stDGCargoSpec.1stInnerPackageDescription
        Assert.assertEquals(1, node.Body.Cargo[1].DGCargoSpec[0].PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body.Cargo[1].DGCargoSpec[0].PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body.Cargo[1].DGCargoSpec[0].PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR S2ND OuterPackageDescription
        //FOR 2nd Cargo.2ndDGCargoSpec.2ndInnerPackageDescription
        Assert.assertEquals(1, node.Body.Cargo[1].DGCargoSpec[1].PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body.Cargo[1].DGCargoSpec[1].PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body.Cargo[1].DGCargoSpec[1].PackageGroup.OuterPackageDescription.PackageDesc.text())
    }

    @Test
    void test_DGCargoSpec__uniqueSeqNum_with1outeGID(){
        //FOR 1st txn
//        GID+1 outer
//                GID+1 inner
//                DGS+
//                GID+1 inner
//                DGS++

        //FOR 2nd txn
//        GID+1 outer
//                GID+1 inner
//                DGS+
//                DGS+
        //FOR 3th txn
//        GID+1 outer
//        DGS+
//                GID+1 inner
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+20:CR::6:CRATE'
GID+1++30:CR::6:CRATE1'
DGS+IM1'
GID+1++40:CR::6:CRATE2'
DGS+IM1'
UNH'
BGM+340++5'
GID+1+20:CR::6:CRATE'
GID+1++30:CR::6:CRATE1'
DGS+IM1'
DGS+IM1'
UNH'
BGM+340++5'
GID+1+20:CR::6:CRATE'
DGS+IM1'
GID+1++30:CR::6:CRATE1'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[1], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[2], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)
        node = xmlParserToNode(writer?.toString())

        //FOR 1st txn
        Assert.assertEquals(1, node.Body[0].Cargo.size())
        Assert.assertEquals(2, node.Body[0].Cargo.DGCargoSpec.size())

        //FOR Cargo.1stDGCargoSpec.1stInnerPackageDescription
        Assert.assertEquals(1, node.Body[0].Cargo.DGCargoSpec[0].PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("30", node.Body[0].Cargo.DGCargoSpec[0].PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE1", node.Body[0].Cargo.DGCargoSpec[0].PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR Cargo.2nd DGCargoSpec.2ndInnerPackageDescription
        Assert.assertEquals(1, node.Body[0].Cargo.DGCargoSpec[1].PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("40", node.Body[0].Cargo.DGCargoSpec[1].PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE2", node.Body[0].Cargo.DGCargoSpec[1].PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR Cargo.1stDGCargoSpec.1stOuterPackageDescription
        Assert.assertEquals(1, node.Body[0].Cargo.DGCargoSpec[0].PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body[0].Cargo.DGCargoSpec[0].PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body[0].Cargo.DGCargoSpec[0].PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR Cargo.2ndDGCargoSpec.2ndIOuterPackageDescription
        Assert.assertEquals(1, node.Body[0].Cargo.DGCargoSpec[1].PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body[0].Cargo.DGCargoSpec[1].PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body[0].Cargo.DGCargoSpec[1].PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR 2nd txn
        Assert.assertEquals(1, node.Body[1].Cargo.size())
        Assert.assertEquals(2, node.Body[1].Cargo.DGCargoSpec.size())

        //FOR Cargo.1stDGCargoSpec.1stInnerPackageDescription
        Assert.assertEquals(1, node.Body[1].Cargo.DGCargoSpec[0].PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("30", node.Body[1].Cargo.DGCargoSpec[0].PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE1", node.Body[1].Cargo.DGCargoSpec[0].PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR Cargo.2ndDGCargoSpec.2ndInnerPackageDescription
        Assert.assertEquals(1, node.Body[1].Cargo.DGCargoSpec[1].PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("30", node.Body[1].Cargo.DGCargoSpec[1].PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE1", node.Body[1].Cargo.DGCargoSpec[1].PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR Cargo.1stDGCargoSpec.1stOuterPackageDescription
        Assert.assertEquals(1, node.Body[1].Cargo.DGCargoSpec[0].PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body[1].Cargo.DGCargoSpec[0].PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body[1].Cargo.DGCargoSpec[0].PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR Cargo.2ndDGCargoSpec.2ndIOuterPackageDescription
        Assert.assertEquals(1, node.Body[1].Cargo.DGCargoSpec[1].PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body[1].Cargo.DGCargoSpec[1].PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body[1].Cargo.DGCargoSpec[1].PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR 3th txn
        Assert.assertEquals(1, node.Body[2].Cargo.size())
        Assert.assertEquals(1, node.Body[2].Cargo.DGCargoSpec.size())

        //FOR Cargo.DGCargoSpec.InnerPackageDescription
        Assert.assertEquals(0, node.Body[2].Cargo.DGCargoSpec.PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("", node.Body[2].Cargo.DGCargoSpec.PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body[2].Cargo.DGCargoSpec.PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR Cargo.1stDGCargoSpec.1stOuterPackageDescription
        Assert.assertEquals(1, node.Body[2].Cargo.DGCargoSpec.PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body[2].Cargo.DGCargoSpec.PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body[2].Cargo.DGCargoSpec.PackageGroup.OuterPackageDescription.PackageDesc.text())
    }

    @Test
    void test_DGCargoSpec__uniqueSeqNum_with2MixedGID(){
        //FOR unique seq number
//        GID+1 mixed
//        DGS+
//        GID+2 mixed
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+20:CR::6:CRATE+30:CR::6:CRATE1'
DGS+IM1'
GID+2+21:CR::6:CRATE+40:CR::6:CRATE2'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)
        node = xmlParserToNode(writer?.toString())

        Assert.assertEquals(2, node.Body.Cargo.size())
        Assert.assertEquals(1, node.Body.Cargo[0].DGCargoSpec.size())
        Assert.assertEquals(0, node.Body.Cargo[1].DGCargoSpec.size())

        //FOR 1st Cargo.DGCargoSpec.InnerPackageDescription
        Assert.assertEquals(1, node.Body.Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("30", node.Body.Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE1", node.Body.Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 1st Cargo.DGCargoSpec.OuterPackageDescription
        Assert.assertEquals(1, node.Body.Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body.Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body.Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR 2nd Cargo.DGCargoSpec.InnerPackageDescription
        Assert.assertEquals(0, node.Body.Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("", node.Body.Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body.Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 2nd Cargo.DGCargoSpec.OuterPackageDescription
        Assert.assertEquals(0, node.Body.Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("", node.Body.Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body.Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageDesc.text())
    }

    @Test
    void test_DGCargoSpec__Non_uniqueSeqNum_with2outeGID(){
        //FOR non-unique seq number with 2 outer GID but no DGS under outer GID
//        GID+1 outer
//                GID+1 inner
//                DGS+
//                GID+1 inner
//                DGS+
//        GID+1 outer

        //For non-unique seq number with 2 outer GID and DGS under 1st outer GID
//        GID+1 outer
//        DGS+
//                GID+1 inner
//                DGS+
//                GID+1 inner
//                DGS+
//        GID+1 outer
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+20:CR::6:CRATE'
GID+1++30:CR::6:CRATE1'
DGS+IM1'
GID+1++40:CR::6:CRATE2'
DGS+IM1'
GID+1+21:CR::6:CRATE0'
UNH'
BGM+340++5'
GID+1+20:CR::6:CRATE'
DGS+IM1'
GID+1++30:CR::6:CRATE1'
DGS+IM1'
GID+1++40:CR::6:CRATE2'
DGS+IM1'
GID+1+21:CR::6:CRATE0'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        script.generateBody(markupBuilder, iftmin.Group_UNH[1], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)
        node = xmlParserToNode(writer?.toString())

        Assert.assertEquals(2, node.Body[0].Cargo.size())
        Assert.assertEquals(0, node.Body[0].Cargo[0].DGCargoSpec.size())
        Assert.assertEquals(0, node.Body[0].Cargo[1].DGCargoSpec.size())

        //FOR 1st Cargo.1stDGCargoSpec.InnerPackageDescription
        Assert.assertEquals(0, node.Body[0].Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("", node.Body[0].Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body[0].Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 1st Cargo.1stDGCargoSpec.OuterPackageDescription
        Assert.assertEquals(0, node.Body[0].Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("", node.Body[0].Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body[0].Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR 1st Cargo.2ndDGCargoSpec.InnerPackageDescription
        Assert.assertEquals(0, node.Body[0].Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("", node.Body[0].Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body[0].Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 1st Cargo.2ndDGCargoSpec.OuterPackageDescription
        Assert.assertEquals(0, node.Body[0].Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("", node.Body[0].Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body[0].Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR 2nd txn
        Assert.assertEquals(2, node.Body[1].Cargo.size())
        Assert.assertEquals(1, node.Body[1].Cargo[0].DGCargoSpec.size())
        Assert.assertEquals(0, node.Body[1].Cargo[1].DGCargoSpec.size())

        //FOR 1st Cargo.DGCargoSpec.InnerPackageDescription
        Assert.assertEquals(0, node.Body[1].Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("", node.Body[1].Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body[1].Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 1st Cargo.DGCargoSpec.OuterPackageDescription
        Assert.assertEquals(1, node.Body[1].Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body[1].Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body[1].Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR 2nd Cargo.DGCargoSpec.InnerPackageDescription
        Assert.assertEquals(0, node.Body[1].Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("", node.Body[1].Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body[1].Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 2nd Cargo.DGCargoSpec.OuterPackageDescription
        Assert.assertEquals(0, node.Body[1].Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("", node.Body[1].Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body[1].Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageDesc.text())
    }

    @Test
    void test_DGCargoSpec__Non_uniqueSeqNum_with2MixedGID(){
        //FOR non-unique seq number with 2 mixed GID
//        GID+1 mixed
//        DGS+
//        GID+1 mixed
        initBelugaOcean()
        String edi = """UNB'
UNH'
BGM+340++5'
GID+1+20:CR::6:CRATE+30:CR::6:CRATE1'
DGS+IM1'
GID+1+21:CR::6:CRATE+40:CR::6:CRATE2'
"""
        iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)
        node = xmlParserToNode(writer?.toString())

        Assert.assertEquals(2, node.Body.Cargo.size())
        Assert.assertEquals(1, node.Body.Cargo[0].DGCargoSpec.size())
        Assert.assertEquals(0, node.Body.Cargo[1].DGCargoSpec.size())

        //FOR 1st Cargo.DGCargoSpec.InnerPackageDescription
        Assert.assertEquals(1, node.Body.Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("30", node.Body.Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE1", node.Body.Cargo[0].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 1st Cargo.DGCargoSpec.OuterPackageDescription
        Assert.assertEquals(1, node.Body.Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("20", node.Body.Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("CRATE", node.Body.Cargo[0].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageDesc.text())

        //FOR 2nd Cargo.DGCargoSpec.InnerPackageDescription
        Assert.assertEquals(0, node.Body.Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.size())
        Assert.assertEquals("", node.Body.Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body.Cargo[1].DGCargoSpec.PackageGroup.InnerPackageDescription.PackageDesc.text())

        //FOR 2nd Cargo.DGCargoSpec.OuterPackageDescription
        Assert.assertEquals(0, node.Body.Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.size())
        Assert.assertEquals("", node.Body.Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageQty.text())
        Assert.assertEquals("", node.Body.Cargo[1].DGCargoSpec.PackageGroup.OuterPackageDescription.PackageDesc.text())
    }

    @Test
    void test_ChargeInformation_ChargeCategory() {
        String xml = """
<IFTMIN>
    <Group_UNH>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>1</E5237_01>
             </C229_01>
            </CPI>
        </Group6_CPI>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>2</E5237_01>
             </C229_01>
            </CPI>
        </Group6_CPI>
        <Group6_CPI>        
            <CPI>
             <C229_01>
                 <E5237_01>4</E5237_01>
             </C229_01>
            </CPI>
        </Group6_CPI>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>5</E5237_01>
             </C229_01>
            </CPI>
        </Group6_CPI>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>7</E5237_01>
             </C229_01>
            </CPI>
        </Group6_CPI>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>10</E5237_01>
             </C229_01>
            </CPI>
        </Group6_CPI>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>11</E5237_01>
             </C229_01>
            </CPI>
        </Group6_CPI>
    </Group_UNH>
</IFTMIN>
		"""
        iftmin = xmlBeanParser.xmlParser(xml, EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)

        node = xmlParserToNode(writer?.toString())

        //Create Segment for each Group6.CPI
        //For Body.ChargeInformation
        Assert.assertEquals("ALL", node.Body.ChargeInformation[0].ChargeDetails.ChargeCategory.text())
        Assert.assertEquals("ADDITIONAL", node.Body.ChargeInformation[1].ChargeDetails.ChargeCategory.text())
        Assert.assertEquals("BASIC", node.Body.ChargeInformation[2].ChargeDetails.ChargeCategory.text())
        Assert.assertEquals("DEST_HAULAGE", node.Body.ChargeInformation[3].ChargeDetails.ChargeCategory.text())
        Assert.assertEquals("DEST_PORT", node.Body.ChargeInformation[4].ChargeDetails.ChargeCategory.text())
        Assert.assertEquals("ORG_PORT", node.Body.ChargeInformation[5].ChargeDetails.ChargeCategory.text())
        Assert.assertEquals("ORG_HAULAGE", node.Body.ChargeInformation[6].ChargeDetails.ChargeCategory.text())
    }

    @Test
    void test_BLInfo() {
        String xml = """
<IFTMIN>
    <Group_UNH>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>4</E5237_01>
             </C229_01>
             <E4237_03>C</E4237_03>
            </CPI>
        </Group6_CPI> 
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>4</E5237_01>
             </C229_01>
             <E4237_03>P</E4237_03>
            </CPI>
        </Group6_CPI>     
        <Group11_NAD>
         <NAD>
            <E3035_01>CN</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>706</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>26</E1373_02>
                  </C503_02>
              </DOC>
         </Group13_DOC>
        </Group11_NAD>
        <Group11_NAD>
         <NAD>
            <E3035_01>FW</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>707</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>27</E1373_02>
                  </C503_02>
              </DOC>
          </Group13_DOC>
         </Group11_NAD>
        <Group11_NAD>
         <NAD>
            <E3035_01>NI</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>710</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>26</E1373_02>
                  </C503_02>
              </DOC>
           </Group13_DOC>
         </Group11_NAD>
        <Group11_NAD>
         <NAD>
            <E3035_01>N1</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>714</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>26</E1373_02>
                  </C503_02>
              </DOC>
         </Group13_DOC>
         </Group11_NAD>
        <Group11_NAD>
         <NAD>
            <E3035_01>N2</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>714</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>26</E1373_02>
                  </C503_02>
              </DOC>
         </Group13_DOC>
         </Group11_NAD>
        <Group11_NAD>
         <NAD>
            <E3035_01>CZ</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>714</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>26</E1373_02>
                  </C503_02>
              </DOC>
         </Group13_DOC>
         </Group11_NAD>
        <Group11_NAD>
         <NAD>
            <E3035_01>HI</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>714</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>26</E1373_02>
                  </C503_02>
              </DOC>
         </Group13_DOC>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>715</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>26</E1373_02>
                  </C503_02>
              </DOC>
         </Group13_DOC>
         </Group11_NAD>
    </Group_UNH>
</IFTMIN>
		"""
        iftmin = xmlBeanParser.xmlParser(xml, EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)

        node = xmlParserToNode(writer?.toString())

        //For DocumentType each Group13.DOC which Group13.DOC.C002.E1001_01 = 706 | 707 | 710| 714
        Assert.assertEquals("OBL", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[0].DocumentType.text())

        //FOR Body.BLInfo.BLType with First Group13.DOC.C002.E1001_01 as 706
        Assert.assertEquals("OBL", node.Body.BLInfo.BLType.text())

        Assert.assertEquals("CBL", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[1].DocumentType.text())
        Assert.assertEquals("SWB", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[2].DocumentType.text())
        Assert.assertEquals("HBL", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[3].DocumentType.text())
        Assert.assertEquals(null, node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[7])

        //FOR FreightType with Group13.DOC.C503.E1373_02 = "26" then "NF"
        Assert.assertEquals("NF", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[0].FreightType.text())

        //FOR BLFreightType withGroup13.DOC.C503.E1373_02 = "26" then "NF"
        Assert.assertEquals("NF", node.Body.BLInfo.BLFreightType.text())

        //FOR Group13.DOC.C503.E1373_02 = "27" and first Group6.CPI.C229.E5237_01 = "4" and Group6.CPI.E4237_03 = "C" then "FC"
        Assert.assertEquals("FC", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[1].FreightType.text())

        //FOR BLDistributedRole
        Assert.assertEquals("CGN", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[0].BLDistributedRole.text())
        Assert.assertEquals("FWD", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[1].BLDistributedRole.text())
        Assert.assertEquals("NPT", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[2].BLDistributedRole.text())
        Assert.assertEquals("ANP", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[3].BLDistributedRole.text())
        Assert.assertEquals("ANP", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[4].BLDistributedRole.text())
        Assert.assertEquals("SHP", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[5].BLDistributedRole.text())
        Assert.assertEquals("SIR", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[6].BLDistributedRole.text())
    }

    @Test
    void test_BLInfo_FreightType_FP() {
        String xml = """
<IFTMIN>
    <Group_UNH>
       <Group1_LOC>
           <LOC>
               <E3227_01>73</E3227_01>
               <C517_02>
                      <E3225_01>AUMEL</E3225_01>
                      <E3055_03>6</E3055_03>
                      <E3224_04>AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFFFFFFFFFAAAAAA60</E3224_04>
               </C517_02>
               <C519_03>
                      <E3223_01>CC</E3223_01>
               </C519_03>
           </LOC>
       </Group1_LOC>
       <Group1_LOC>
           <LOC>
               <E3227_01>73</E3227_01>
               <C517_02>
                      <E3225_01>AUMEL</E3225_01>
                      <E3055_03>6</E3055_03>
                      <E3224_04>AAAAAAA60</E3224_04>
               </C517_02>
               <C519_03>
                      <E3223_01>CCC</E3223_01>
               </C519_03>
           </LOC>
       </Group1_LOC>
       <MOA>
            <C516_01>
                <E5025_01>44</E5025_01>
                <E5004_02>3000</E5004_02>
            </C516_01>
       </MOA>
       <MOA>
            <C516_01>
                <E5025_01>44</E5025_01>
                <E5004_02>1200</E5004_02>
            </C516_01>
       </MOA>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>4</E5237_01>
             </C229_01>
             <E4237_03>P</E4237_03>
            </CPI>
        </Group6_CPI>     
        <Group11_NAD>
         <NAD>
            <E3035_01>CN</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>707</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>27</E1373_02>
                  </C503_02>
              </DOC>
          </Group13_DOC>
        </Group11_NAD>
    </Group_UNH>
</IFTMIN>
		"""
        iftmin = xmlBeanParser.xmlParser(xml, EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)

        node = xmlParserToNode(writer?.toString())

        //FOR FreightType with Group13.DOC.C503.E1373_02 = "27" and first Group6.CPI.C229.E5237_01 = "4" and Group6.CPI.E4237_03 = "P" then "FP"
        Assert.assertEquals("FP", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails[0].FreightType.text())

        //FOR BLFreightType with Group13.DOC.C503.E1373_02 = "27" and first Group6.CPI.C229.E5237_01 = "4" and Group6.CPI.E4237_03 = "P" then "FP"
        Assert.assertEquals("FP", node.Body.BLInfo.BLFreightType.text())

        //FOR Body.BLInfo.BLType with First Group13.DOC.C002.E1001_01 as 707
        Assert.assertEquals("CBL", node.Body.BLInfo.BLType.text())

        //FOR MOA[C516_E5025_01 = "44"][1]_C516_E5004_02
        Assert.assertEquals("3000", node.Body.BLInfo.DeclaredValueOfGoods.text())

        //FOR BLInfo.BLReleaseOffice with  Group1.LOC.E3227_01 = "73" and stringLength(Group1.LOC_C519_E3223_01) = 2
        Assert.assertEquals("CC", node.Body.BLInfo.BLReleaseOffice.CountryCode.text())
    }

    @Test
    void test_BLInfo_FreightType_FPC() {
        String xml = """
<IFTMIN>
    <Group_UNH>
       <Group1_LOC>
           <LOC>
               <E3227_01>73</E3227_01>
               <C517_02>
                      <E3225_01>AUMEL</E3225_01>
                      <E3055_03>6</E3055_03>
                      <E3224_04>AA60</E3224_04>
               </C517_02>
               <C519_03>
                      <E3223_01>CCC</E3223_01>
               </C519_03>
           </LOC>
       </Group1_LOC>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>4</E5237_01>
             </C229_01>
             <E4237_03>M</E4237_03>
            </CPI>
        </Group6_CPI>     
        <Group11_NAD>
         <NAD>
            <E3035_01>CN</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>710</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>27</E1373_02>
                  </C503_02>
              </DOC>
          </Group13_DOC>
        </Group11_NAD>
    </Group_UNH>
</IFTMIN>
		"""
        iftmin = xmlBeanParser.xmlParser(xml, EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)

        node = xmlParserToNode(writer?.toString())

        //FOR  Group13.DOC.C503.E1373_02 = "27" and first Group6.CPI.C229.E5237_01 = "4" and Group6.CPI.E4237_03 = "M" then "FPC"
        Assert.assertEquals("FPC", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails.FreightType.text())

        //FOR BLFreightType with Group13.DOC.C503.E1373_02 = "27" and first Group6.CPI.C229.E5237_01 = "4" and Group6.CPI.E4237_03 = "M" then "FPC"
        Assert.assertEquals("FPC", node.Body.BLInfo.BLFreightType.text())

        //FOR Body.BLInfo.BLType with First Group13.DOC.C002.E1001_01 as 710
        Assert.assertEquals("SWB", node.Body.BLInfo.BLType.text())

        //FOR BLInfo.BLReleaseOffice with  Group1.LOC.E3227_01 = "73" and stringLength(Group1.LOC_C519_E3223_01) > 2 and  CNTRY_CDE = 'AU'
        Assert.assertEquals("AU", node.Body.BLInfo.BLReleaseOffice.CountryCode.text())
    }

    @Test
    void test_BLInfo_FreightType_FC() {
        String xml = """
<IFTMIN>
    <Group_UNH>
       <Group1_LOC>
           <LOC>
               <E3227_01>73</E3227_01>
               <C517_02>
                      <E3225_01>AAAAA</E3225_01>
                      <E3055_03>6</E3055_03>
                      <E3224_04>AA60</E3224_04>
               </C517_02>
               <C519_03>
                      <E3223_01>CCCC</E3223_01>
               </C519_03>
           </LOC>
       </Group1_LOC>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>4</E5237_01>
             </C229_01>
             <E4237_03>F</E4237_03>
            </CPI>
        </Group6_CPI>     
        <Group11_NAD>
         <NAD>
            <E3035_01>CN</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>714</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>27</E1373_02>
                  </C503_02>
              </DOC>
          </Group13_DOC>
        </Group11_NAD>
    </Group_UNH>
</IFTMIN>
		"""
        iftmin = xmlBeanParser.xmlParser(xml, EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)

        node = xmlParserToNode(writer?.toString())

        //FOR  FreightType with Group13.DOC.C503.E1373_02 = "27" then "FC"
        Assert.assertEquals("FC", node.Body.BLInfo.FinalCopyBLDistribution.DistributionDetails.FreightType.text())

        //FOR BLFreightType with Group13.DOC.C503.E1373_02 = "27" and first Group6.CPI.C229.E5237_01 = "4" and Group6.CPI.E4237_03 = "F" then "FC"
        Assert.assertEquals("FC", node.Body.BLInfo.BLFreightType.text())

        //FOR Body.BLInfo.BLType with First Group13.DOC.C002.E1001_01 as 714
        Assert.assertEquals("HBL", node.Body.BLInfo.BLType.text())

        //FOR BLInfo.BLReleaseOffice with  Group1.LOC.E3227_01 = "73" and stringLength(Group1.LOC_C519_E3223_01) > 2 and  CNTRY_CDE = ''
        //println node.Body.BLInfo.BLReleaseOffice.CountryCode
        Assert.assertEquals("", node.Body.BLInfo.BLReleaseOffice.CountryCode.text())
    }

    @Test
    void test_SICertClause_CertificationClauseType() {
        String xml = """
<IFTMIN>
    <Group_UNH>
          <FTX>
               <E4451_01>BLC</E4451_01>
               <C107_03>
                    <E4441_01>01</E4441_01>
               </C107_03>
               <C108_04>
                    <E4440_01>123</E4440_01>
               </C108_04>
          </FTX>
          <FTX>
               <E4451_01>BLC</E4451_01>
               <C107_03>
                    <E4441_01>AA</E4441_01>
               </C107_03>
               <C108_04>
                    <E4440_01>Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Cou300a</E4440_01>
               </C108_04>
          </FTX>
          <FTX>
               <E4451_01>BLC</E4451_01>
               <C107_03>
                    <E4441_01>35</E4441_01>
               </C107_03>
          </FTX>
    </Group_UNH>
</IFTMIN>
		"""
        iftmin = xmlBeanParser.xmlParser(xml, EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)

        node = xmlParserToNode(writer?.toString())

        //FOR BLInfo.SICertClause.CertificationClauseType with extCertClauseTypeMap.containsKey (FTX.C107.E4441_01) as 01
        Assert.assertEquals("01", node.Body.BLInfo.SICertClause[0].CertificationClauseType.text())
        //FOR BLInfo.SICertClause.CertificationClauseType with ! extCertClauseTypeMap.containsKey (FTX.C107.E4441_01) as AA
        Assert.assertEquals("", node.Body.BLInfo.SICertClause[1].CertificationClauseType.text())

        //FOR BLInfo.SICertClause.CertificationClauseText with FTX.C108.E4440_01 != ""
        Assert.assertEquals("123", node.Body.BLInfo.SICertClause[0].CertificationClauseText.text())
        Assert.assertEquals("Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Count Shipper's Load and Cou300", node.Body.BLInfo.SICertClause[1].CertificationClauseText.text())
        //FOR BLInfo.SICertClause.CertificationClauseText with FTX.C108.E4440_01 = "" ,map from extCertClauseTypeMap["${FTX.C107.E4441_01}"]
        Assert.assertEquals("Age of Vessel", node.Body.BLInfo.SICertClause[2].CertificationClauseText.text())
    }

    @Test
    void test_BLInfo_BLFreightType_FC() {
        String xml = """
<IFTMIN>
    <Group_UNH>
        <Group6_CPI>
            <CPI>
             <C229_01>
                 <E5237_01>4</E5237_01>
             </C229_01>
             <E4237_03>C</E4237_03>
            </CPI>
        </Group6_CPI>     
        <Group11_NAD>
         <NAD>
            <E3035_01>CN</E3035_01>
         </NAD>
         <Group13_DOC>
              <DOC>
                  <C002_01>
                        <E1001_01>714</E1001_01>
                  </C002_01>
                  <C503_02>
                        <E1373_02>27</E1373_02>
                  </C503_02>
              </DOC>
          </Group13_DOC>
         </Group11_NAD>
    </Group_UNH>
</IFTMIN>
		"""
        iftmin = xmlBeanParser.xmlParser(xml, EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)

        node = xmlParserToNode(writer?.toString())

        //FOR BLFreightType with Group13.DOC.C503.E1373_02 = "27" and first Group6.CPI.C229.E5237_01 = "4" and Group6.CPI.E4237_03 = "C" then "FC"
        Assert.assertEquals("FC", node.Body.BLInfo.BLFreightType.text())
    }

    @Test
    void test_Route() {
        String xml = """
<IFTMIN>
    <Group_UNH>
        <Group8_TDT>
            <TDT>
                <E8051_01>20</E8051_01>
                <E8028_02>71Effffffffffff</E8028_02>
                <C222_08>
                       <E8213_01>aaaaaaaa9</E8213_01>
                      <E8212_04>CMA CGM BIANCAaaaaaaaaaaaaaaaaaaa35</E8212_04>
                </C222_08>
            </TDT>
            <Group9_LOC>
                <LOC>
                    <E3227_01>9</E3227_01>
                     <C517_02>
                        <E3225_01>SHASS</E3225_01>
                        <E3055_03>86</E3055_03>
                    </C517_02>
                </LOC>
            </Group9_LOC>
            <Group9_LOC>
                <LOC>
                    <E3227_01>7</E3227_01>
                     <C517_02>
                        <E3225_01>AAAAA</E3225_01>
                        <E3055_03>86</E3055_03>
                    </C517_02>
                </LOC>
            </Group9_LOC>
            <Group9_LOC>
                <LOC>
                    <E3227_01>11</E3227_01>
                     <C517_02>
                        <E3225_01>BBBBB</E3225_01>
                        <E3055_03>86</E3055_03>
                    </C517_02>
                </LOC>
            </Group9_LOC>
            <Group9_LOC>
                <LOC>
                    <E3227_01>88</E3227_01>
                     <C517_02>
                        <E3225_01>CNNNN</E3225_01>
                        <E3055_03>86</E3055_03>
                    </C517_02>
                </LOC>
            </Group9_LOC>
            <Group9_LOC>
                <LOC>
                    <E3227_01>198</E3227_01>
                     <C517_02>
                        <E3225_01>SHAAA</E3225_01>
                        <E3055_03>86</E3055_03>
                    </C517_02>
                </LOC>
            </Group9_LOC>
       </Group8_TDT>
    </Group_UNH>
</IFTMIN>
		"""
        iftmin = xmlBeanParser.xmlParser(xml, EDI_IFTMIN.class)
        initMarkupBuilder()

        def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
        script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
        markupBuilder.nodeCompleted(null, siRoot)

        node = xmlParserToNode(writer?.toString())

        //FOR Body.Route.POR.CityDetails.LocationCode.MutuallyDefinedCode with Group9.LOC.C517.E3055_03 = "86" where LOC.E3227_01 = '88'
        Assert.assertEquals("CNNNN", node.Body.Route.POR.CityDetails.LocationCode.MutuallyDefinedCode.text())

        //FOR Body.Route.FND.CityDetails.LocationCode.MutuallyDefinedCode with Group9.LOC.C517.E3055_03 = "86" where LOC.E3227_01 = '7'
        Assert.assertEquals("AAAAA", node.Body.Route.FND.CityDetails.LocationCode.MutuallyDefinedCode.text())

        //FOR Body.Route.FirstPOL.Port.LocationCode.MutuallyDefinedCode with Group9.LOC.C517.E3055_03 = "86" where LOC.E3227_01 = '88'
        Assert.assertEquals("SHASS", node.Body.Route.FirstPOL.Port.LocationCode.MutuallyDefinedCode.text())

        //FOR SVVD.LloydsNumber from Group8.TDT.C222.E8213_01
        Assert.assertEquals("aaaaaaaa9", node.Body.Route.SVVD.LloydsNumber.text())

        //FOR SVVD.ExternalVesselCode from Group8.TDT.C222.E8213_01
        Assert.assertEquals("aaaaaaaa9", node.Body.Route.SVVD.ExternalVesselCode.text())

        //FOR OriginOfGoods.LocationCode.MutuallyDefinedCode WITH Group9.LOC.C517.E3055_03 = "86" where LOC.E3227_01 = "198"
        Assert.assertEquals("SHAAA", node.Body.BLInfo.OriginOfGoods.LocationCode.MutuallyDefinedCode.text())
    }

    @Test
	void test_Body_Remarks() {
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
FTX+AAI++01+R60 FFFF PATIO FURNITURE E P R60 FFF'
FTX+AAI++01+R60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITUREAE P R60 FFFF'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		initMarkupBuilder()
		
		def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, null, null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)

		node = xmlParserToNode(writer?.toString())

		//FOR BODY.Remarks with two FTX.E4451_01 = "AAI"
		Assert.assertEquals("R60 FFFF PATIO FURNITURE E P R60 FFF", node.Body.Remarks.RemarkLine[0].text())
		Assert.assertEquals("R60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITURE E P R60 FFFF 80AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR60 FFFF PATIO FURNITURE E P R60 FFFF PAR60 FFFF PATIO FURNITUREAE P R60 FFFF", node.Body.Remarks.RemarkLine[1].text())

	}

	@Test
	void test_Prevalidation_BGM(){
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
UNH'
BGM+304++6'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		
		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)

		Assert.assertEquals(false, preErrors.contains('Invalid UNH[0].BGM.C002_01.E1001_01'))
		Assert.assertEquals(false, preErrors.contains('Invalid UNH[0].BGM.E1225_03'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[1], 0)
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].BGM.C002_01.E1001_01'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].BGM.E1225_03'))
	}
	
	@Test
	void test_Prevalidation_CNT(){
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
CNT+7:123456789012345678:KGM'
CNT+11:12345678912345679'
CNT+15:53.750:CBF'
CNT+16:1'
UNH'
BGM+340++5'
CNT+7:123456789012345678'
CNT+15:53.750'
UNH'
BGM+340++5'
CNT+7:123456789012345678:AAA'
CNT+15:53.750:BBB'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
	
		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(false, preErrors.contains('Missing mandatory UNH[0].CNT[0].C270_01.E6411_03 as E6069_01 is 7'))
		Assert.assertEquals(false, preErrors.contains('Missing mandatory UNH[0].CNT[1].C270_01.E6411_03 as E6069_01 is 15'))
		Assert.assertEquals(false, preErrors.contains('Invalid UNH[0].CNT[0].C270_01.E6411_03 for E6069_01 is 7'))
		Assert.assertEquals(false, preErrors.contains('Invalid UNH[0].CNT[1].C270_01.E6411_03 for E6069_01 is 15'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[1], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].CNT[0].C270_01.E6411_03 as E6069_01 is 7'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].CNT[1].C270_01.E6411_03 as E6069_01 is 15'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[2], 0)
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].CNT[0].C270_01.E6411_03 for E6069_01 is 7'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].CNT[1].C270_01.E6411_03 for E6069_01 is 15'))
	}
	
	@Test
	void test_Prevalidation_Group1_LOC(){
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
LOC+73+AUMEL::6:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFFFFFFFFFAAAAAA60'
DTM+95:20170426:102'
UNH'
BGM+340++5'
LOC+73+::6:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFFFFFFFFFAAAAAA60'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)

		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(false, preErrors.contains('Missing mandatory UNH[0].Group1_LOC.LOC.C507_02.E3225_01 for LOC.E3227_01 = "73"'))
		Assert.assertEquals(false, preErrors.contains('Missing mandatory UNH[0].Group1_LOC.DTM for LOC.C517_02.E3227_01 = "73"'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[1], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group1_LOC.LOC.C507_02.E3225_01 for LOC.E3227_01 = "73"'))
		//removed as script changed
		//Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group1_LOC.DTM for LOC.C517_02.E3227_01 = "73"'))
	}
	
	@Test
	void test_Prevalidation_Group8_TDT(){
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
TDT'
LOC+9+:139:6:SHANGHAI'
UNH'
BGM+340++5'
TDT'
LOC+9+CNSHA:139::SHANGHAI'
UNH'
BGM+340++5'
TDT'
LOC+11+:139:6:SHANGHAI'
UNH'
BGM+340++5'
TDT'
LOC+11+CNSHA:139::SHANGHAI'
UNH'
BGM+340++5'
TDT'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		
		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT[0].Group9_LOC[0].LOC.C517_02.E3225_01 as E3227_01 = "9" or "11"'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT[0].Group9_LOC[0].LOC.C517_02.E3225_01 as E3055_03 is present'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[1], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT[0].Group9_LOC[0].LOC.C517_02.E3055_03 as E3225_01 is present'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[2], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT[0].Group9_LOC[0].LOC.C517_02.E3225_01 as E3227_01 = "9" or "11"'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT[0].Group9_LOC[0].LOC.C517_02.E3225_01 as E3055_03 is present'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[3], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT[0].Group9_LOC[0].LOC.C517_02.E3055_03 as E3225_01 is present'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[4], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT.Group9_LOC.LOC.E3227_01 = "9"'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT.Group9_LOC.LOC.E3227_01 = "11"'))
	}

	@Test
	void test_Prevalidation_Group11_NAD(){
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
NAD'
UNH'
BGM+340++5'
NAD+CA'
UNH'
BGM+340++5'
NAD+CN'
UNH'
BGM+340++5'
NAD+HI'
DOC+706'
DOC+710'
DOC+714'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD for E3035_01 = "CA" (Carrier)'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD for E3035_01 = "CN" (Consignee)'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD for E3035_01 = "CZ" (Consignor)'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD for E3035_01 = "HI" (Requestor)'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[1], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD.NAD.C082_02.E3039_01 where NAD.E3035_01 = "CA"'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[2], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD.NAD.C080_04.E3036_01 where NAD.E3035_01 = "CN"'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[3], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD.NAD.C082_02.E3039_01 where NAD.E3035_01 = "HI"'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group13_DOC[2].DOC.C002_01.E1000_04 where DOC.C002_01.E1001_01 = "714"'))
		Assert.assertEquals(true, preErrors.contains('Multiple BL type involved - UNH[0].Group11_NAD[0]'))
		//Assert.assertEquals(true, preErrors.contains(''))
	}

	@Test
	void test_Prevalidation_Group18_GID(){
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
GID++12345678'
MEA+AAE+WT+AAA'
MEA+AAE+AAW+BBB'
SGP+UESU1111143416947'
MEA+AAE+WT+AAA'
MEA+AAE+AAW+BBB'
DGS'
CTA+HG'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(true, preErrors.contains('Either UNH[0].Group18_GID[0].GID.C213_02.E7065_02 or E7064_05 is mandatory as C213_02 present'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group18_GID[0].Group20_MEA[0].MEA.C174_03.E6411_01 as C502_02.E6313_01 = "WT"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group18_GID[0].Group20_MEA[1].MEA.C174_03.E6411_01 as C502_02.E6313_01 = "AAW"'))
		Assert.assertEquals(true, preErrors.contains('No matching EQD segment found for UNH[0].Group18_GID[0].Group29_SGP[0]'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group18_GID[0].Group29_SGP[0].Group30_MEA[0].MEA.C174_03.E6411_01 as C502_02.E6313_01 = "WT"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group18_GID[0].Group29_SGP[0].Group30_MEA[1].MEA.C174_03.E6411_01 as C502_02.E6313_01 = "AAW"'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group18_GID[0].Group32_DGS[0].Group33_CTA[0].C056_02.E3412_02'))

		//Assert.assertEquals(true, preErrors.contains(''))
	}
	
	@Test
	void test_Prevalidation_Group37_EQD(){
		initBelugaOcean()
		String edi = """UNB'
UNH'
BGM+340++5'
EQD'
MEA+AAE+WT+AAA'
MEA+AAE+AAW+BBB'
MEA+AAE+AAS+CCC'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		
		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group37_EQD[0].MEA[0].C174_03.E6411_01 as C502_02.E6313_01 = "WT"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group37_EQD[0].MEA[1].C174_03.E6411_01 as C502_02.E6313_01 = "AAW"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group37_EQD[0].MEA[2].C174_03.E6411_01 as C502_02.E6313_01 = "AAS"'))

		//Assert.assertEquals(true, preErrors.contains(''))
	}
	
    protected static Node xmlParserToNode(String testedXml){
        XmlParser xmlParser = new XmlParser();
		xmlParser.namespaceAware = false
        return xmlParser.parseText(testedXml)
    }

}
