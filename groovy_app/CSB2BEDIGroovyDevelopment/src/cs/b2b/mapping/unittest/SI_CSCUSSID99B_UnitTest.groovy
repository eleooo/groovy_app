package cs.b2b.mapping.unittest

import cs.b2b.beluga.api.EDIProcessResult;
import cs.b2b.beluga.common.fileparser.UIFFileParser;
import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.BGM17
import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.Group_UNH13
import cs.b2b.core.mapping.bean.edi.xml.si.CS.ShippingInstructions
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.mapping.e2e.util.BelugaOceanHelper
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.e2e.util.LocalFileUtil;
import cs.b2b.mapping.scripts.CUS_IFTMIN_CS2SIXML_CSCUSSID99B
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
//import groovy.util
import org.junit.Assert
import org.junit.Test
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.EDI_IFTMIN

import java.io.File;
import java.sql.Connection
import java.text.SimpleDateFormat

/**
 * Created by YOUAL on 6/8/2017.
 */
class SI_CSCUSSID99B_UnitTest {
	
	String BOFileNamePath = "./IG_Definition/CUS_D99B_IFTMIN_CS.xml"
	String definitionBody = null;
	
    CUS_IFTMIN_CS2SIXML_CSCUSSID99B script = null;
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
        script = new CUS_IFTMIN_CS2SIXML_CSCUSSID99B();

        script.conn = conn
    }

    private void initMarkupBuilder() {
        node = null
        UNH = new Group_UNH13()
        writer = new StringWriter()
        //markupBuilder = new MarkupBuilder(writer)

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
	void test_Prevalidation_BGM(){
		initBelugaOcean()
		String edi = """UNH'
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
		String edi = """UNH'
CNT+7:1:KGM'
CNT+11:1'
CNT+15:53.75:CBF'
CNT+16:1'
UNH'
CNT+7:1'
CNT+15:53.75'
UNH'
CNT+7:1:AAA'
CNT+15:53.75:BBB'
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
		String edi = """UNH'
LOC+73'
DTM+95'
UNH'
LOC+73'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		
		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(false, preErrors.contains('Missing mandatory UNH[0].Group1_LOC[0].DTM for LOC.E3227_01 = "73"'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[1], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group1_LOC[0].DTM for LOC.E3227_01 = "73"'))
	}
	
	@Test
	void test_Prevalidation_Group8_TDT(){
		initBelugaOcean()
		String edi = """UNH'
TDT'
UNH'
TDT'
LOC+9'
UNH'
TDT'
LOC+11'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		
		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT.Group9_LOC.LOC.E3227_01 = "9"'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT.Group9_LOC.LOC.E3227_01 = "11"'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[1], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT[0].Group9_LOC[0].LOC.C517_02.E3225_01 as E3227_01 = "9"|"11"'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[2], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group8_TDT[0].Group9_LOC[0].LOC.C517_02.E3225_01 as E3227_01 = "9"|"11"'))
	}

	@Test
	void test_Prevalidation_Group11_NAD(){
		initBelugaOcean()
		String edi = """UNH'
NAD'
UNH'
NAD+CA'
UNH'
NAD+WPA'
UNH'
NAD+WPA'
CTA+IC'
UNH'
NAD'
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
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD for E3035_01 = "SH" (Shipper)'))
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD for E3035_01 = "SI" (SI Requestor)'))
		Assert.assertEquals(true, preErrors.contains('UNH[0].Group11_NAD[0] - At least one of NAD020 or (NAD040~NAD090) is required'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[1], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD.NAD.C082_02.E3039_01 where NAD.E3035_01 = "CA"'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[2], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD[NAD_01 = "WPA"].Group12_CTA'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[3], 0)
		Assert.assertEquals(true, preErrors.contains('Missing mandatory UNH[0].Group11_NAD[NAD_01 = "WPA"].Group12_CTA[0].CTA where CTA_01 = "RP"'))
		
		preErrors = script.prepValidation(iftmin.Group_UNH[4], 0)
		Assert.assertEquals(true, preErrors.contains('Multiple BL type involved - UNH[0].Group11_NAD[0]'))
	}

	@Test
	void test_Prevalidation_Group18_GID(){
		initBelugaOcean()
		String edi = """UNH'
GID'
MEA+VOL++AAA'
MEA+WT++AAA'
SGP'
MEA+VOL++AAA'
MEA+WT++AAA'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)
		
		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group18_GID[0].Group20_MEA[0].MEA.C174_03.E6411_01 as E6311_01 = "VOL"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group18_GID[0].Group20_MEA[1].MEA.C174_03.E6411_01 as E6311_01 = "WT"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group18_GID[0].Group29_SGP[0].Group30_MEA[0].MEA.C174_03.E6411_01 as E6311_01 = "VOL"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group18_GID[0].Group29_SGP[0].Group30_MEA[1].MEA.C174_03.E6411_01 as E6311_01 = "WT"'))

		//Assert.assertEquals(true, preErrors.contains(''))
	}
	
	@Test
	void test_Prevalidation_Group37_EQD(){
		initBelugaOcean()
		String edi = """UNH'
EQD'
MEA+WT++AAA'
MEA+VOL++BBB'
MEA+AAE+AAS+CCC'
MEA+VOL+VGM'
MEA+WT+VGM+TON'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)

		initMarkupBuilder()

		List preErrors = new ArrayList()
		preErrors = script.prepValidation(iftmin.Group_UNH[0], 0)
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group37_EQD[0].MEA[0].C174_03.E6411_01 as E6311_01 = "WT"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group37_EQD[0].MEA[1].C174_03.E6411_01 as E6311_01 = "VOL"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group37_EQD[0].MEA[2].C174_03.E6411_01 as E6311_01 = "AAE" and C502_02.E6313_01 == "AAS"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group37_EQD[0].MEA[3].C174_03.E6411_01 as E6311_01 = "VOL"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group37_EQD[0].MEA[3].E6311_01 as C502_02.E6313_01 = "VGM"'))
		Assert.assertEquals(true, preErrors.contains('Invalid UNH[0].Group37_EQD[0].MEA[4].C174_03.E6411_01 = "TON" as E6311_01 = "WT" & C502_02.E6313_01 = "VGM"'))
		
		//Assert.assertEquals(true, preErrors.contains(''))
	}
	
	@Test
	void test_PartyText(){
		initBelugaOcean()
		String edi = """UNH'
NAD+CN+++NAD04_01:NAD04_02+NAD05_01:NAD05_02:NAD05_03:NAD05_04'
UNH'
NAD+CN+++NAD04_01+:::NAD05_04'
UNH'
NAD+CN+++NAD04_01+NAD05_01::NAD05_03:'
"""
		iftmin = xmlBeanParser.xmlParser(xmlOutput(edi), EDI_IFTMIN.class)

		initMarkupBuilder()
		
		def siRoot = markupBuilder.createNode('ShippingInstruction', ['xmlns': "http://www.cargosmart.com/shipmentinstruction", 'xmlns:ns0': "http://www.cargosmart.com/common"])
		script.generateBody(markupBuilder, iftmin.Group_UNH[0], 1, new Date(), null, null, null, null)
		script.generateBody(markupBuilder, iftmin.Group_UNH[1], 1, new Date(), null, null, null, null)
		script.generateBody(markupBuilder, iftmin.Group_UNH[2], 1, new Date(), null, null, null, null)
		markupBuilder.nodeCompleted(null, siRoot)
		node = xmlParserToNode(writer?.toString())
		
		Assert.assertEquals("""NAD04_01
NAD05_01
NAD05_02
NAD05_03
NAD05_04""",node.Body[0].Party[0].PartyText.text())
		
		Assert.assertEquals("""NAD04_01



NAD05_04""",node.Body[1].Party[0].PartyText.text())
		
		Assert.assertEquals("""NAD04_01
NAD05_01

NAD05_03""",node.Body[2].Party[0].PartyText.text())
	}
	
    protected static Node xmlParserToNode(String testedXml){
        XmlParser xmlParser = new XmlParser();
        return xmlParser.parseText(testedXml)
    }

}
