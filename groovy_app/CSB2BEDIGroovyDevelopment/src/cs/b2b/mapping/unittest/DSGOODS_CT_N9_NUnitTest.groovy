package cs.b2b.mapping.unittest

import static org.junit.Assert.*
import groovy.xml.MarkupBuilder

import java.sql.Connection

import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import cs.b2b.core.mapping.bean.*;
import cs.b2b.core.mapping.bean.ct.*;
import cs.b2b.core.mapping.util.MappingUtil_CT_O_Common;
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.scripts.CUS_CS2CTXML_315_DSGOODS

class DSGOODS_CT_N9_NUnitTest {

	CUS_CS2CTXML_315_DSGOODS bean = null;
	StringWriter writer = null
	MarkupBuilder markupBuilder = null
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
		bean = new CUS_CS2CTXML_315_DSGOODS();
		writer = new StringWriter()
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
		body = new Body();
		bean.conn = conn
	}
	
	@Test
	void testBM() {
		//prepare data
		BLGeneralInfo bl = new BLGeneralInfo()
		body.BLGeneralInfo.add(bl)
		body.Event = new Event()
		body.Event.EventDT = new cs.b2b.core.mapping.bean.ct.EventDT()
		body.Container = new Container()
	
		
		//get result
		bean.generateBody(body, markupBuilder);
		String result = writer.toString();
		
		//Set Assert
		assertFalse(result.contains("<E128_01>BM</E128_01>"))
		
		//perpare data
		body.BLGeneralInfo[0].BLNumber = '123456'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>BM</E128_01><E127_02>123456</E127_02></N9>"))
		
		//perpare data
		body.BLGeneralInfo[0].BLNumber = '123456789012345678901234567890-aaaaaabbbbbccccc'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>BM</E128_01><E127_02>123456789012345678901234567890</E127_02></N9>"))
		
		
	}

	@Test
	void testIB(){
		
		//prepare data
		BLGeneralInfo bl = new BLGeneralInfo()
		body.BLGeneralInfo.add(bl)
		body.Event = new Event()
		body.Event.EventDT = new cs.b2b.core.mapping.bean.ct.EventDT()
		body.Container = new Container()
		
		//get result
		bean.generateBody(body, markupBuilder);
		String result = writer.toString();
		
		//Set Assert
		assertFalse(result.contains("<E128_01>IB</E128_01>"))
		
		//perpare data
		body.BLGeneralInfo[0].CustomsReferenceType = 'IT'
		body.BLGeneralInfo[0].CustomsReferenceNumber = '123456'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>IB</E128_01><E127_02>123456</E127_02></N9>"))
		
		//perpare data
		body.BLGeneralInfo[0].CustomsReferenceType = 'IT'
		body.BLGeneralInfo[0].CustomsReferenceNumber = '1234567890123456789012345678901234567890'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>IB</E128_01><E127_02>123456789012345678901234567890</E127_02></N9>"))
		
	}
	
	@Test
	void testBN() {
		
		//prepare data
		BookingGeneralInfo bn = new BookingGeneralInfo();
		body.BookingGeneralInfo.add(bn)
		body.Event = new Event()
		body.Event.EventDT = new cs.b2b.core.mapping.bean.ct.EventDT()
		body.Container = new Container()
		
		//get result
		bean.generateBody(body, markupBuilder);
		String result = writer.toString();
		
		//Set Assert
		assertFalse(result.contains("<E128_01>BN</E128_01>"))
		
		//perpare data
		body.BookingGeneralInfo[0].CarrierBookingNumber = '123456'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>BN</E128_01><E127_02>123456</E127_02></N9>"))
		
		//perpare data
		body.BookingGeneralInfo[0].CarrierBookingNumber = '1234567890123456789012345678901234567890'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>BN</E128_01><E127_02>123456789012345678901234567890</E127_02></N9>"))
		
	}
	
	@Test
	void testEQ() {
		
		//prepare data
		Container eq = new Container()
		body.Container = eq
		body.Event = new Event()
		body.Event.EventDT = new cs.b2b.core.mapping.bean.ct.EventDT()
		
		//get result
		bean.generateBody(body, markupBuilder);
		String result = writer.toString();
		
		//Set Assert
		assertFalse(result.contains("<E128_01>EQ</E128_01>"))
		
		//perpare data
		body.Container.ContainerNumber = '123456'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();

		//Set Assert
		assertTrue(result.contains("<N9><E128_01>EQ</E128_01><E127_02>123456</E127_02></N9>"))
		
		//perpare data
		body.Container.ContainerCheckDigit = 'X'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();

		//Set Assert
		assertTrue(result.contains("<N9><E128_01>EQ</E128_01><E127_02>123456X</E127_02></N9>"))
		
		//perpare data
		body.Container.ContainerNumber = '123456789012345678901234567890'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();

		//Set Assert
		assertTrue(result.contains("<N9><E128_01>EQ</E128_01><E127_02>1234567890X</E127_02></N9>"))
		
	}

	@Test
	void testSN() {
		//prepare data
		Seal sn = new Seal()
		Container cn = new Container();
		cn.Seal.add(sn)
		body.Container = cn
		body.Event = new Event()
		body.Event.EventDT = new cs.b2b.core.mapping.bean.ct.EventDT()
		
		body.Container.Seal[0].SealNumber = '123'
		
		//get result
		bean.generateBody(body, markupBuilder);
		String result = writer.toString();
		
		//Set Assert
		assertFalse(result.contains("<E128_01>SN</E128_01>"))
		
		body.Container.Seal[0].SealNumber = '123456'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>SN</E128_01><E127_02>123456</E127_02></N9>"))
		
		body.Container.Seal[0].SealNumber = '1234567890123456789012345678901234567890'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>SN</E128_01><E127_02>123456789012345678901234567890</E127_02></N9>"))
	}	
	
	@Test
	void testPO(){
		
		ExternalReference exf = new ExternalReference()
		body.ExternalReference.add(exf)
		body.Event = new Event()
		body.Event.EventDT = new cs.b2b.core.mapping.bean.ct.EventDT()
		body.Container = new Container()
		
		//get result
		bean.generateBody(body, markupBuilder);
		String result = writer.toString();
		
		//Set Assert
		assertFalse(result.contains("<E128_01>PO</E128_01>"))
		
		body.ExternalReference[0].CSReferenceType = 'PO'
		body.ExternalReference[0].ReferenceNumber = '1234567890123456789012345678901234567890'
		
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>PO</E128_01><E127_02>123456789012345678901234567890</E127_02></N9>"))
	}
	
	@Test
	void testIC(){
		
		Party party = new Party()
		body.Party.add(party)
		body.Event = new Event()
		body.Event.EventDT = new cs.b2b.core.mapping.bean.ct.EventDT()
		body.Container = new Container()
		
		//get result
		bean.generateBody(body, markupBuilder);
		String result = writer.toString();
		
		//Set Assert
		assertFalse(result.contains("<E128_01>IC</E128_01>"))
		
		
		body.Party[0].PartyType = 'CGN'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertFalse(result.contains("<E128_01>IC</E128_01>"))
		
		body.Party[0].CarrierCustomerCode = '1234567890123456789012345678901234567890'
		
		//get result
		bean.generateBody(body, markupBuilder);
		result = writer.toString();
		
		//Set Assert
		assertTrue(result.contains("<N9><E128_01>IC</E128_01><E127_02>123456789012345678901234567890</E127_02></N9>"))
		
	}
	
	@Test
	void testGetDuplicatedPairs(){
		
		MappingUtil_CT_O_Common ctUtils = new MappingUtil_CT_O_Common();
		
		Map<String,String> result = ctUtils.getDuplicatedPairs("DSGOODS", conn)

		assertNotNull(result)
		assertEquals("CS170RL", result.CS170)
		assertEquals("CS190D", result.CS190)
		assertEquals("CS277AG", result.CS277)
		
	}
}










