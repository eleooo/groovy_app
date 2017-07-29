package cs.b2b.mapping.unittest

import static org.junit.Assert.*

import java.text.DecimalFormat
import java.text.SimpleDateFormat;

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import cs.b2b.core.mapping.bean.bc.BookingConfirm
import cs.b2b.core.mapping.bean.bc.ReeferCargoSpec
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.mapping.e2e.util.LocalFileUtil

class BC_UnitTest extends JunitBase_BC_O {
	
	@BeforeClass
	static void beforeClass(){
		
	}
	
	@AfterClass
	static void afterClass(){
		
	}
	
	@Test
	void test00() {
		prepareCS2BCXmlByFile()
		
		def defReeferCargos = body.Cargo?.ReeferCargoSpec
		
		def defReeferCargo = body.Cargo.find(){it.ReeferCargoSpec}?.ReeferCargoSpec?.first()
//		defReeferCargos.each { refer ->
//			println refer.Temperature.Temperature + ", "+refer.Temperature.TemperatureUnit
//		}
		
		println 'datas: '+defReeferCargos
		println 'data: '+defReeferCargo
		
		//println 'size: '+defReeferCargos.size()
		
		//println 'first: '+defReeferCargos.find(){it.Temperature}?.first()
		
		body.Cargo.each { cargo ->
			cargo.ReeferCargoSpec.each { refer ->
				println refer.Temperature.Temperature + ", "+refer.Temperature.TemperatureUnit
			}
		}
	}
	
	String xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	SimpleDateFormat sdfXmlFmt = new SimpleDateFormat(xmlDateTimeFormat);
	
	@Test
	void test01() {
		String len = "-18.5"
		BigDecimal  lenth = new BigDecimal(len)
		def out = lenth.setScale(0, BigDecimal.ROUND_HALF_UP)
		println 'out: '+out
		
		DecimalFormat formatter = new DecimalFormat("#.####")
		println 'out2: ' + formatter.format(out)
		println 'out3: '+ formatter.format(Double.parseDouble(len))
		
		java.util.Date currentDate = new java.util.Date()
		java.util.Date date = sdfXmlFmt.parse('2017-04-23T23:00:00');
		
		println 'current: '+currentDate
		println 'fmt dat: '+date
		
		println 'ts compare: '+(date.minus(currentDate)>0)
		println 'ts compare: '+(date.getTime() > currentDate.getTime())
		println 'ts compare: '+(date.after(currentDate))
	}
	
	String strEdiXml = '''
		<T301>
			<Loop_ST>
				<Loop_N1>
					<N1>
						<E98_01>BKs</E98_01>
					</N1>
				</Loop_N1>
			</Loop_ST>
		</T301>
	'''
	@Test
	void test02() {
		println '--- test02 ----'
		XmlParser xmlParser = new XmlParser();
		Node node = xmlParser.parseText(strEdiXml)
		
		String bcXmlFile = "D:\\1_B2BEDI_Revamp\\BC\\OUT_301\\DUMMYBC301\\ActualComplete\\1015-JBSAMBER-baseline_filterParty_partyName_miss.xml"
		XmlBeanParser parser = new XmlBeanParser()
		String inputXmlBody = LocalFileUtil.readBigFileContentDirectly(basicTestingFileName)
		BookingConfirm bcfile = parser.xmlParser(inputXmlBody, BookingConfirm.class)
		def current_Body = bcfile.Body.get(0)
		
		if (node?.Loop_ST?.Loop_N1.find{it.N1.E98_01.text() == 'BK'|| it.N1.E98_01.text() == 'CA'} == null && !(current_Body.GeneralInformation?.BookingStatus in ["Rejected", "Declined"])) {
			println 'true'
		} else {
			println 'false'
		}
		
	}
	
}

