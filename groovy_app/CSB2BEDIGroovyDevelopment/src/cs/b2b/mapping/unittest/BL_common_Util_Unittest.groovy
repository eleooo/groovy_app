package cs.b2b.mapping.unittest

import cs.b2b.core.mapping.bean.Contact
import cs.b2b.core.mapping.bean.ContactPhone
import cs.b2b.core.mapping.bean.bl.Cargo
import cs.b2b.core.mapping.bean.bl.Container
import cs.b2b.core.mapping.bean.bl.ExternalReference
import cs.b2b.core.mapping.util.MappingUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import cs.b2b.core.mapping.util.MappingUtil_BL_O_Common

class BL_common_Util_Unittest {

	//	 * @Tested True tested by BL_common_Util_Unittest method:
	
	MappingUtil_BL_O_Common blUtil = new MappingUtil_BL_O_Common(new MappingUtil());
	List<Map<String,String>> errorKeyList = new ArrayList<String,String>();

	@Before
	public void before(){
		errorKeyList = new ArrayList<String,String>();
	}
	
	@Test
	public void checkR403_M(){
		//normal case
		String normal1 = "<Loop_R4><R4><E310_03>test</E310_03></R4></Loop_R4>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkR403_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_R4><R4></R4></Loop_R4>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkR403_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkN709_M(){
		//normal case
		String normal1 = "<Loop_LX><Loop_N7><N7><E184_09>test</E184_09></N7></Loop_N7></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkN709_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_LX><Loop_N7><N7></N7></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkN709_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkN708_M(){
		//normal case
		String normal1 = "<Loop_LX><Loop_N7><N7><E183_08>test</E183_08></N7></Loop_N7></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkN708_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_LX><Loop_N7><N7></N7></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkN708_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkN701_M(){
		//normal case
		String normal1 = "<Loop_LX><Loop_N7><N7><E206_01>test</E206_01></N7></Loop_N7></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkN701_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_LX><Loop_N7><N7></N7></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkN701_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkN101_M(){
		//normal case
		String normal1 = "<Loop_N1><N1><E98_01>test</E98_01></N1></Loop_N1>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkN101_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_N1><N1></N1></Loop_N1>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkN101_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkL301_M(){
		//normal case
		String normal1 = "<L3><E81_01>test</E81_01></L3>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkL301_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<L3></L3>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkL301_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkL004_L005_L011_P(){
		//normal case
		String normal1 = "<Loop_LX><Loop_L0><L0><E81_04>test</E81_04><E187_05>test</E187_05><E188_11>test</E188_11></L0></Loop_L0></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkL004_L005_L011_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//normal case
		errorKeyList = new ArrayList<String,String>();
		String normal2 = "<Loop_LX><Loop_L0><L0><E187_05>test</E187_05><E188_11>test</E188_11></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(normal2));
		blUtil.checkL004_L005_L011_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//normal case
		errorKeyList = new ArrayList<String,String>();
		String normal3 = "<Loop_LX><Loop_L0><L0><E81_04>test</E81_04><E188_11>test</E188_11></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(normal3));
		blUtil.checkL004_L005_L011_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//normal case
		errorKeyList = new ArrayList<String,String>();
		String normal4 = "<Loop_LX><Loop_L0><L0><E81_04>test</E81_04><E187_05>test</E187_05></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(normal4));
		blUtil.checkL004_L005_L011_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_LX><Loop_L0><L0></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkL004_L005_L011_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkVesselInfo(){
		//normal case
		String normal1 = "<V1>test</V1><V1><E182_02>test</E182_02><E55_04>test</E55_04><E140_05>test</E140_05></V1>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkVesselInfo(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//normal case
		errorKeyList = new ArrayList<String,String>();
		String normal2 = "<V1>test</V1><V1><E182_02>test</E182_02><E140_05>test</E140_05></V1>";
		node = xmlParserToNode(testXml(normal2));
		blUtil.checkVesselInfo(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//normal case
		errorKeyList = new ArrayList<String,String>();
		String normal3 = "<V1>test</V1><V1><E55_04>test</E55_04><E140_05>test</E140_05></V1>";
		node = xmlParserToNode(testXml(normal3));
		blUtil.checkVesselInfo(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//normal case
		errorKeyList = new ArrayList<String,String>();
		String normal4 = "<V1>test</V1><V1><E182_02>test</E182_02><E55_04>test</E55_04></V1>";
		node = xmlParserToNode(testXml(normal4));
		blUtil.checkVesselInfo(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_LX><LoopL0><L5>test</L5></LoopL0></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkVesselInfo(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )
		
		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase2XML = "<V1>test</V1>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkVesselInfo(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )
	}
	
	@Test
	public void checkMissLoopLXLoopL0L5(){
		//normal case
		String normal1 = "<Loop_LX><LoopL0><L5>test</L5></LoopL0></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissLoopLXLoopL0L5(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_LX><LoopL0></LoopL0></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissLoopLXLoopL0L5(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkMissingR4DTM(){
		//normal case
		String normal1 = "<Loop_R4><DTM>CR</DTM></Loop_R4>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissingR4DTM(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_R4></Loop_R4>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissingR4DTM(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkMissingN9CR_Honeywell(){
		//normal case
		String normal1 = "<N9><E128_01>CR</E128_01></N9>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissingN9CR_Honeywell(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<N9><E128_01>NotCR</E128_01></N9>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissingN9CR_Honeywell(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )
	
		
		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase2XML = "<N9><E128_01>notCR</E128_01></N9><N9><E128_01>NotCR</E128_01></N9>";
		node = xmlParserToNode(testXml(errorcase2XML));
		blUtil.checkMissingN9CR_Honeywell(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkMissingN7(){
		//normal case
		String normal1 = "<Loop_LX><Loop_N7><N7>test</N7></Loop_N7></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissingN7(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_LX><Loop_N7></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissingN7(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )
	
		
		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase2XML = "<Loop_LX></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase2XML));
		blUtil.checkMissingN7(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	
	@Test
	public void checkMissingLX(){
		//normal case
		String normal1 = "<Loop_LX><N1>test</N1></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissingLX(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_N1></Loop_N1>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissingLX(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkMissingLoopN1N1(){
		//normal case
		String normal1 = "<Loop_N1><N1>test</N1></Loop_N1>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissingLoopN1N1(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_N1></Loop_N1>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissingLoopN1N1(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkMissingLoopLXLoopL0L0(){
		//normal case
		String normal1 = "<Loop_LX><Loop_L0><L0>test</L0></Loop_L0></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissingLoopLXLoopL0L0(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_LX><Loop_L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissingLoopLXLoopL0L0(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkMissingL3_11_80(){
		//normal case
		String normal1 = "<L3><E80_11>test</E80_11></L3>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissingL3_11_80(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<L3></L3>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissingL3_11_80(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkMissingL1andL1_1(){
		//normal case
		String normal1 = "<Loop_L1>test</Loop_L1><Loop_LX><Loop_L0><Loop_L1>test</Loop_L1></Loop_L0></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissingL1andL1_1(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		// normal case
		errorKeyList = new ArrayList<String,String>();
		String normal2 = "<Loop_L1>test</Loop_L1><Loop_LX><Loop_L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(normal2));
		blUtil.checkMissingL1andL1_1(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		// normal case
		errorKeyList = new ArrayList<String,String>();
		String normal3 = "<Loop_LX><Loop_L0><Loop_L1>test</Loop_L1></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(normal3));
		blUtil.checkMissingL1andL1_1(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_LX><Loop_L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissingL1andL1_1(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0 )

	}
	
	@Test
	public void checkChargeCode999(){
		//error case
		String normal1 = "<Loop_L1><L1><E150_08>999</E150_08></L1></Loop_L1>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkChargeCode999(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		
		// normal case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<Loop_L1><L1><E150_08>999test</E150_08></L1></Loop_L1>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkChargeCode999(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
	}
	
	@Test
	public void test_checkB3(){
		//error case
		String normal1 = "<B3></B3>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkB3(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		// normal case
		errorKeyList = new ArrayList<String,String>();
		String errorcase1XML = "<B3><E145_03>test</E145_03></B3>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkB3(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
	}
	
	
	@Test
	public void test_replaceZeroAfterPoint(){//zero
		String input_str;
		String result_str;
		//case 01
		input_str = '0.1230'
		result_str = blUtil.replaceZeroAfterPoint(input_str)
		Assert.assertTrue(result_str == '0.123')
		
		//case 02
		input_str = '0.1203'
		result_str = blUtil.replaceZeroAfterPoint(input_str)
		Assert.assertTrue(result_str == '0.1203')
		
		//case 03
		input_str = '.1203'
		result_str = blUtil.replaceZeroAfterPoint(input_str)
		Assert.assertTrue(result_str == '.1203')
		
		//case 04
		input_str = '.120'
		result_str = blUtil.replaceZeroAfterPoint(input_str)
		Assert.assertTrue(result_str == '.12')
	}
	
	@Test
	public void test_PartyTypeMissing(){//zero
		//		exists($Start/group/pfx12:Body-BillOfLading/pfx12:Party-Body-BillOfLading[ns:PartyType=""])
		String normal;
		Node node;
		cs.b2b.core.mapping.bean.bl.Body current_Body;
		List<cs.b2b.core.mapping.bean.bl.Party> tmp_party_list;
		cs.b2b.core.mapping.bean.bl.Party tmp_party;
		//case 01
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()

		tmp_party_list = null
		current_Body.setParty(tmp_party_list)
		
		blUtil.partyTypeMissing(current_Body, true, 'Party name qualifier is missing', errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------01'
		
		//case 02
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		
		tmp_party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>()
		current_Body.setParty(tmp_party_list)
		
		blUtil.partyTypeMissing(current_Body, true, 'Party name qualifier is missing', errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------02'
		
		//case 03
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		
		tmp_party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>()
		
		tmp_party = new cs.b2b.core.mapping.bean.bl.Party();
		tmp_party.setPartyName(null);
		tmp_party_list.add(tmp_party)
		
		current_Body.setParty(tmp_party_list)
		
		blUtil.partyTypeMissing(current_Body, true, 'Party name qualifier is missing', errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------03'
				
		//case 04
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		
		tmp_party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>()
		
		tmp_party = new cs.b2b.core.mapping.bean.bl.Party()
		tmp_party.setPartyName("")
		tmp_party_list.add(tmp_party)
		
		current_Body.setParty(tmp_party_list)
		
		blUtil.partyTypeMissing(current_Body, true, 'Party name qualifier is missing', errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
		println 'case ----------end----------04'
		
		//case 05
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>()
		
		tmp_party = new cs.b2b.core.mapping.bean.bl.Party()
		tmp_party.setPartyName('123')
		tmp_party_list.add(tmp_party)
		
		current_Body.setParty(tmp_party_list)
		blUtil.partyTypeMissing(current_Body, true, 'Party name qualifier is missing', errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------05'
	}
	@Test
	public void test_ChargeCodeNotConverted(){//zero
		String normal;
		Node node;
		
		//case 01
		errorKeyList = new ArrayList<String,String>();
		normal = "<Loop_L1><L1><E150_08>NaN</E150_08></L1></Loop_L1>";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkChargeCodeNotConverted(node, true, 'Charge Code unconverted', errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
		
		//case 02
		errorKeyList = new ArrayList<String,String>();
		normal = "<Loop_L1><L1><E150_08>123</E150_08></L1></Loop_L1>";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkChargeCodeNotConverted(node, true, 'Charge Code unconverted', errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//case 03
		errorKeyList = new ArrayList<String,String>();
		normal = "<Loop_L1><L1></L1></Loop_L1>";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkChargeCodeNotConverted(node, true, 'Charge Code unconverted', errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		
	}
	
	@Test
	public void testNonTotalAmtCollectSum(){

		//normal case
		String normal1 = "<B3><E193_07>test</E193_07></B3>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkNonTotalAmtCollectSum(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)


		// error case
		String errorcase1XML = "<B3></B3>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkNonTotalAmtCollectSum(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		
	}
	
	
	
	@Test
	public void testV101_V102_R(){
		//normal case
		String normal1 = "<V1><E597_01>test</E597_01></V1>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkV101_V102_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<V1><E597_01>test</E597_01><E182_02>test</E182_02></V1>";
		node = xmlParserToNode(testXml(normal2));
		blUtil.checkV101_V102_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<V1></V1>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkV101_V102_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		
	}

	@Test
	public void testV102_M(){
		//normal case
		String normal = "<V1><E182_02>test</E182_02></V1>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkV102_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
	
		// exception case
		
		String testcase1XML = "<V1></V1>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkV102_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

	}
	@Test
	public void testL3_M(){
		//normal case
		String normal = "<L3><E81_01>test</E81_01></L3>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkL3_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
	
		// exception case
		
		String testcase1XML = "";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL3_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

	}
	
	@Test
	public void testDTM02_DTM03_DTM_05_R(){
		//normal case
		String normal = "<Loop_R4><DTM><E373_02>test</E373_02></DTM></Loop_R4>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkDTM02_DTM03_DTM_05_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		
		errorKeyList = new ArrayList<String,String>();
		String testcase4XML = "<Loop_R4><DTM></DTM></Loop_R4>";
		node = xmlParserToNode(testXml(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkDTM02_DTM03_DTM_05_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		// exception case
		String testcase2XML = "<Loop_R4><DTM><E373_02></E373_02></DTM></Loop_R4>";
		node = xmlParserToNode(testXml(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkDTM02_DTM03_DTM_05_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size()  > 0)

	}
	@Test
	public void testDTM04_DTM03_C(){
		//normal case
		String normal = "<Loop_R4><DTM><E337_03>test</E337_03><E623_04>test</E623_04></DTM></Loop_R4>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkDTM04_DTM03_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String testcase3XML = "<Loop_R4><DTM><E337_03>test</E337_03></DTM></Loop_R4>";
		node = xmlParserToNode(testXml(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkDTM04_DTM03_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		String testcase2XML = "<Loop_R4><DTM></DTM></Loop_R4>";
		node = xmlParserToNode(testXml(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkDTM04_DTM03_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size()  == 0)
		
		// exception case
		String testcase1XML = "<Loop_R4><DTM><E623_04>test</E623_04></DTM></Loop_R4>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkDTM04_DTM03_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)


	}
	@Test
	public void testL507_L506_C(){
		//normal case
		String normal = "<Loop_LX><Loop_L0><L5><E87_06>test</E87_06><E88_07>test</E88_07></L5></Loop_L0></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkL507_L506_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		
		// exception case
		String testcase1XML = "<Loop_LX><Loop_L0><L5><E88_07>test</E88_07></L5></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL507_L506_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

	}

	@Test
	public void testN103_N104_P(){
		//normal case
		String normal = "<Loop_N1><N1><E66_03>test</E66_03><E67_04>test</E67_04></N1></Loop_N1>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkN103_N104_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		
		// exception case
		String testcase1XML = "<Loop_N1><N1><E67_04>test</E67_04></N1></Loop_N1>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN103_N104_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		
		errorKeyList == new ArrayList<String,String>();
		String testcase2XML = "<Loop_N1><N1><E66_03>test</E66_03></N1></Loop_N1>";
		node = xmlParserToNode(testXml(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN103_N104_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

	}
	@Test
	public void testN702_M(){
		//normal case
		String normal = "<Loop_LX><Loop_N7><N7><E207_02>test</E207_02></N7></Loop_N7></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkN702_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// exception case
		String testcase1XML = "<Loop_LX><Loop_N7><N7></N7></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN702_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)


	}

	@Test
	public void testG6103_G6104_P(){
		//normal case
		String normal = "<G61><E365_03>test</E365_03><E364_04>test</E364_04></G61>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkG6103_G6104_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// two case
		String testcase1XML = "<G61><E365_03>test</E365_03><E364_04></E364_04></G61>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkG6103_G6104_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String testcase2XML = "<G61><E365_03></E365_03><E364_04>test</E364_04></G61>";
		node = xmlParserToNode(testXml(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkG6103_G6104_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testL004_L005_P(){
		//normal case
		String normal = "<Loop_LX><Loop_L0><L0><E81_04>test</E81_04><E187_05>Test</E187_05></L0></Loop_L0></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkL004_L005_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// two case
		String testcase1XML = "<Loop_LX><Loop_L0><L0><E81_04></E81_04><E187_05>Test</E187_05></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL004_L005_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String testcase2XML = "<Loop_LX><Loop_L0><L0><E81_04>test</E81_04><E187_05></E187_05></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL004_L005_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testL006_L007_P(){
		//normal case
		String normal = "<B3><E32_09>test</E32_09><E374_10>Test</E374_10></B3>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkL006_L007_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// two case
		String testcase1XML = "<B3><E374_10>Test</E374_10></B3>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL006_L007_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String testcase2XML = "<B3><E32_09>Test</E32_09></B3>";
		node = xmlParserToNode(testXml(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL006_L007_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testL008_L009_P(){
		//normal case
		String normal = "<Loop_LX><Loop_L0><L0><E80_08>test</E80_08><E211_09>test</E211_09></L0></Loop_L0></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkL008_L009_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// two case
		String testcase1XML = "<Loop_LX><Loop_L0><L0><E211_09>test</E211_09></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL008_L009_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String testcase2XML = "<Loop_LX><Loop_L0><L0><E80_08>test</E80_08></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL008_L009_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testL011_L004_C(){
		//normal case
		String normal = "<Loop_LX><Loop_L0><L0><E81_04>test</E81_04><E188_11>test</E188_11></L0></Loop_L0></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkL011_L004_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// two case
		String testcase1XML = "<Loop_LX><Loop_L0><L0><E81_04>test</E81_04></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL011_L004_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String testcase2XML = "<Loop_LX><Loop_L0><L0><E188_11>test</E188_11></L0></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL011_L004_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testL104_L105_L106_R(){
		//normal case
		String normal1 = "<Loop_LX><Loop_N7><Loop_L1><L1><E58_04>test</E58_04><E191_05>test</E191_05><E117_06>test</E117_06></L1></Loop_L1></Loop_N7></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkL104_L105_L106_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<Loop_L1><L1><E58_04>test</E58_04><E191_05>test</E191_05><E117_06>test</E117_06></L1></Loop_L1>";
		node = xmlParserToNode(testXml(normal2));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL104_L105_L106_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String testcase1XML = "<Loop_LX><Loop_N7><Loop_L1><L1></L1></Loop_L1></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL104_L105_L106_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String testcase2XML = "<Loop_L1><L1></L1></Loop_L1>";
		node = xmlParserToNode(testXml(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL104_L105_L106_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testL301_L302_P(){
		//normal case
		String normal = "<L3><E81_01>test</E81_01><E187_02>test</E187_02></L3>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkL301_L302_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<L3><E81_01>test</E81_01></L3>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL301_L302_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String errorcase2XML = "<L3><E187_02>test</E187_02></L3>";
		node = xmlParserToNode(testXml(errorcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL301_L302_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testL312_L301_C(){
		//normal case
		String normal1 = "<L3><E81_01>test</E81_01><E188_12>test</E188_12></L3>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkL312_L301_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<L3></L3>";
		node = xmlParserToNode(testXml(normal2));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL312_L301_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<L3><E188_12>test</E188_12></L3>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkL312_L301_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testN406_N405_C(){
		//normal case
		String normal1 = "<Loop_N1><N4><E309_05></E309_05><E310_06></E310_06></N4></Loop_N1>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkN406_N405_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<Loop_N1><N4><E309_05>test</E309_05><E310_06>test</E310_06></N4></Loop_N1>";
		node = xmlParserToNode(testXml(normal2));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN406_N405_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<Loop_N1><N4><E309_05></E309_05><E310_06>test</E310_06></N4></Loop_N1>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN406_N405_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testR402_R403_P(){
		//normal case
		String normal = "<Loop_R4><R4><E309_02>test</E309_02><E310_03>test</E310_03></R4></Loop_R4>";
		Node node = xmlParserToNode(testXml(normal));
		blUtil.checkR402_R403_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<Loop_N1><N4></N4></Loop_N1>";
		node = xmlParserToNode(testXml(normal2));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkR402_R403_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<Loop_R4><R4><E309_02>test</E309_02></R4></Loop_R4>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkR402_R403_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String errorcase2XML = "<Loop_R4><R4><E310_03>test</E310_03></R4></Loop_R4>";
		node = xmlParserToNode(testXml(errorcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkR402_R403_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testV108_V101_C(){
		//normal case
		String normal1 = "<V1></V1>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkV108_V101_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<V1><E597_01>test</E597_01><E897_08>test</E897_08></V1>";
		node = xmlParserToNode(testXml(normal2));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkV108_V101_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<V1><E897_08>test</E897_08></V1>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkV108_V101_C(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testB309_B310_P(){
		//normal case
		String normal1 = "<B3><E32_09>test</E32_09><E374_10>test</E374_10></B3>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkB309_B310_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<B3></B3>";
		node = xmlParserToNode(testXml(normal2));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkB309_B310_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<B3><E374_10>test</E374_10></B3>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkB309_B310_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String errorcase2XML = "<B3><E32_09>test</E32_09></B3>";
		node = xmlParserToNode(testXml(errorcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkB309_B310_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testH101_M(){
		//normal case
		String normal1 = "<Loop_LX><Loop_N7><Loop_H1><H1><E62_01>test</E62_01></H1></Loop_H1></Loop_N7></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkH101_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<Loop_LX><Loop_N7><Loop_H1><H1><E62_01></E62_01></H1></Loop_H1></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkH101_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testN102_N103_R(){  //already tested by file
		//normal case
		String normal1 = "<Loop_N1><N1><E93_02>test</E93_02><E66_03></E66_03></N1></Loop_N1>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkN102_N103_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<Loop_N1><N1><E93_02></E93_02><E66_03>test</E66_03></N1></Loop_N1>";
		node = xmlParserToNode(testXml(normal2));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN102_N103_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<Loop_N1><N1><E93_02></E93_02><E66_03></E66_03></N1></Loop_N1>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN102_N103_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testN703_N704_P(){
		//normal case
		String normal1 = "<Loop_LX><Loop_N7><N7></N7></Loop_N7></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkN703_N704_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<Loop_LX><Loop_N7><N7><E81_03>test</E81_03><E187_04>test</E187_04></N7></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(normal2));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN703_N704_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<Loop_LX><Loop_N7><N7><E187_04>test</E187_04></N7></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN703_N704_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String errorcase2XML = "<Loop_LX><Loop_N7><N7><E81_03>test</E81_03></N7></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN703_N704_P(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testN901_N902_M(){
		//normal case
		String normal1 = "<N9><E369_03>test</E369_03></N9>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkN901_N902_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<N9><E128_01>test</E128_01><E127_02>test</E127_02><E369_03>test</E369_03></N9>";
		node = xmlParserToNode(testXml(normal2));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN901_N902_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<N9>><E127_02>test</E127_02></N9>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN901_N902_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String errorcase2XML = "<N9><E128_01>test</E128_01></N9>";
		node = xmlParserToNode(testXml(errorcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN901_N902_M(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testN902_N903_R(){
		//normal case
		String normal1 = "<N9><E127_02>test</E127_02><E369_03>test</E369_03></N9>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkN902_N903_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<N9><E373_04>test</E373_04></N9>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkN902_N903_R(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testH1_FlashPoint(){
		//normal case
		String normal1 = "<Loop_LX><Loop_L0><Loop_H1><H1><E77_07>Hi</E77_07></H1></Loop_H1></Loop_L0></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkH1_FlashPoint(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<Loop_LX><Loop_L0><Loop_H1><H1><E77_07>test+test-test</E77_07></H1></Loop_H1></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkH1_FlashPoint(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test  //already tested by file
	public void testL1max(){
		//normal case
		String normal1 = "<Loop_LX><Loop_N7><Loop_L1></Loop_L1></Loop_N7></Loop_LX>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkL1max(node,1, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		String normal2 = "<Loop_LX><Loop_L0><Loop_L1></Loop_L1></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(normal2));
		blUtil.checkL1max(node,1, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<Loop_LX><Loop_N7><Loop_L1></Loop_L1><Loop_L1></Loop_L1></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkL1max(node,1,true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		String errorcase2XML = "<Loop_LX><Loop_L0><Loop_L1></Loop_L1><Loop_L1></Loop_L1></Loop_L0></Loop_LX>";
		node = xmlParserToNode(testXml(errorcase2XML));
		blUtil.checkL1max(node,1,true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test //already tested by file
	public void testMissingL1_2(){
		//normal case
		String normal1 = "<Loop_L1></Loop_L1>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkMissingL1_2(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<Loop_L2></Loop_L2>";
		node = xmlParserToNode(testXml(errorcase1XML));
		blUtil.checkMissingL1_2(node,true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}
	
	@Test
	public void test_checkQTY01_M(){//zero
		String normal;
		Node node;
		//case 01
		errorKeyList = new ArrayList<String,String>();
		normal = "<Loop_LX><Loop_N7><QTY><E673_01>test</E673_01></QTY></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkQTY01_M(node, true, "QTY_01 is mandatory.", errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//case 02
		errorKeyList = new ArrayList<String,String>();
		normal = "<Loop_LX><Loop_N7><QTY></QTY></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkQTY01_M(node, true, "QTY_01 is mandatory.", errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//case 03
		errorKeyList = new ArrayList<String,String>();
		normal = "<Loop_LX><Loop_N7></Loop_N7></Loop_LX>";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkQTY01_M(node, true, "QTY_01 is mandatory.", errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//case 04
		errorKeyList = new ArrayList<String,String>();
		normal = "<Loop_LX></Loop_LX>";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkQTY01_M(node, true, "QTY_01 is mandatory.", errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//case 05
		errorKeyList = new ArrayList<String,String>();
		normal = "";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkQTY01_M(node, true, "QTY_01 is mandatory.", errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
	}
	@Test
	public void test_checkB2A_M(){//zero
		String normal;
		Node node;
		//case 01
		errorKeyList = new ArrayList<String,String>();
		normal = "<B2A><E353_01>test</E353_01></B2A>";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkB2A_M(node, true,"B2A.01.353 is mandatory.",errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		
		//case 02  (E353_01's content is '' or not exist E353_01 node) (due to prud.. data)
		errorKeyList = new ArrayList<String,String>();
		normal = "<B2A></B2A>";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkB2A_M(node, true,"B2A.01.353 is mandatory.",errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
		
		//case 03
		errorKeyList = new ArrayList<String,String>();
		normal = "";
		node = xmlParserToNode(testXml(normal));
		blUtil.checkB2A_M(node, true,"B2A.01.353 is mandatory.",errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	public void testcheckNonUSInbound(){

		String normal1 = "<Loop_R4><R4><E115_01>E</E115_01><E26_05>US</E26_05></R4></Loop_R4>";
		Node node = xmlParserToNode(testXml(normal1));
		blUtil.checkNonUSInbound(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		// error case
		String errorcase1XML = "<Loop_R4><R4><E115_01>E</E115_01><E26_05>test</E26_05></R4></Loop_R4>";
		node = xmlParserToNode(testXml(errorcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkNonUSInbound(node,true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}
	@Test
	//author LINJA
	//status success
	public void testMissingBGM_C002_1001(){
		errorKeyList.clear();
		String normal = "<BGM><C002_01><E1001_01></E1001_01></C002_01></BGM>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.missingBGM_C002_1001(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//error case
		errorKeyList.clear();
		String errorcaseXML= "<BGM><C002_01></C002_01></BGM>";
		node = xmlParserToNode(testGroup_UNH(errorcaseXML));
		blUtil.missingBGM_C002_1001(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//error case
		errorKeyList.clear();
		String errorcaseXML1= "<BGM></BGM>";
		node = xmlParserToNode(testGroup_UNH(errorcaseXML1));
		blUtil.missingBGM_C002_1001(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	//author LINJA
	//status success
	public void testCheckBLTypeIsMemo(){
		String normal;
		Node node;
		cs.b2b.core.mapping.bean.bl.Body curent_body;
		List<cs.b2b.core.mapping.bean.bl.GeneralInformation> tem_GeneralInformation_list;
		cs.b2b.core.mapping.bean.bl.GeneralInformation tem_GeneralInformation;

		errorKeyList.clear();
		curent_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_GeneralInformation_list = null;
		curent_body.setGeneralInformation(tem_GeneralInformation_list);
		blUtil.checkBLTypeIsMemo(curent_body,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		println "--------------case01 end----------------"


		errorKeyList.clear();
		curent_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_GeneralInformation = new cs.b2b.core.mapping.bean.bl.GeneralInformation();
		tem_GeneralInformation_list = new ArrayList<cs.b2b.core.mapping.bean.bl.GeneralInformation>();
		tem_GeneralInformation_list.add(tem_GeneralInformation);
		curent_body.setGeneralInformation(tem_GeneralInformation_list);
		blUtil.checkBLTypeIsMemo(curent_body,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println "--------------case02 end----------------"

		errorKeyList.clear();
		curent_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_GeneralInformation = new cs.b2b.core.mapping.bean.bl.GeneralInformation();
		tem_GeneralInformation_list = new ArrayList<cs.b2b.core.mapping.bean.bl.GeneralInformation>();
		tem_GeneralInformation.setBLType("");
		tem_GeneralInformation_list.add(tem_GeneralInformation);
		curent_body.setGeneralInformation(tem_GeneralInformation_list);
		blUtil.checkBLTypeIsMemo(curent_body,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println "--------------case03 end----------------"

		errorKeyList.clear();
		curent_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_GeneralInformation = new cs.b2b.core.mapping.bean.bl.GeneralInformation();
		tem_GeneralInformation_list = new ArrayList<cs.b2b.core.mapping.bean.bl.GeneralInformation>();
		tem_GeneralInformation.setBLType("mem");
		tem_GeneralInformation_list.add(tem_GeneralInformation);
		curent_body.setGeneralInformation(tem_GeneralInformation_list);
		blUtil.checkBLTypeIsMemo(curent_body,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println "--------------case04 end----------------"

		errorKeyList.clear();
		curent_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_GeneralInformation = new cs.b2b.core.mapping.bean.bl.GeneralInformation();
		tem_GeneralInformation_list = new ArrayList<cs.b2b.core.mapping.bean.bl.GeneralInformation>();
		tem_GeneralInformation.setBLType("Memo");
		tem_GeneralInformation_list.add(tem_GeneralInformation);
		curent_body.setGeneralInformation(tem_GeneralInformation_list);
		blUtil.checkBLTypeIsMemo(curent_body,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);
		println "--------------case05 end----------------"
	}

	@Test
	//author LINJA
	//status success
	public void testMissingBGM_C106_1004(){
		errorKeyList.clear();
		String normal = "<BGM><C106_02><E1004_01></E1004_01></C106_02></BGM>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.missingBGM_C106_1004(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//error case
		errorKeyList.clear();
		String errorcaseXML= "<BGM><C106_02></C106_02></BGM>";
		node = xmlParserToNode(testGroup_UNH(errorcaseXML));
		blUtil.missingBGM_C106_1004(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		errorKeyList.clear();
		String errorcaseXML1= "<BGM></BGM>";
		node = xmlParserToNode(testGroup_UNH(errorcaseXML1));
		blUtil.missingBGM_C106_1004(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}

	@Test
	//author LINJA
	//status success
	public void testCheckCCP(){
		cs.b2b.core.mapping.bean.bl.Body current_body;
		List<cs.b2b.core.mapping.bean.bl.Party> tem_Party_list;
		cs.b2b.core.mapping.bean.bl.Party tem_Party;
		String Ext_Cde;

		errorKeyList.clear()
		Ext_Cde = null;
		current_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_Party = new cs.b2b.core.mapping.bean.bl.Party();
		tem_Party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>();
		tem_Party.setPartyType();
		tem_Party.setCarrierCustomerCode('')
		tem_Party_list.add(tem_Party);
		current_body.setParty(tem_Party_list);
		blUtil.checkCCP(current_body,Ext_Cde,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		println "--------------case01 end----------------"

		errorKeyList.clear()
		Ext_Cde = '123456'
		current_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_Party = new cs.b2b.core.mapping.bean.bl.Party();
		tem_Party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>();
		tem_Party.setPartyType('CCP');
		tem_Party.setCarrierCustomerCode('')
		tem_Party_list.add(tem_Party);
		current_body.setParty(tem_Party_list);
		blUtil.checkCCP(current_body,Ext_Cde,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		println "--------------case02 end----------------"

		errorKeyList.clear()
		Ext_Cde = '123456';
		current_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_Party = new cs.b2b.core.mapping.bean.bl.Party();
		tem_Party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>();
		tem_Party.setPartyType('CCP');
		tem_Party.setCarrierCustomerCode('123456')
		tem_Party_list.add(tem_Party);
		current_body.setParty(tem_Party_list);
		blUtil.checkCCP(current_body,Ext_Cde,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		println "--------------case03 end----------------"

		errorKeyList.clear()
		Ext_Cde = '123456';
		current_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_Party = new cs.b2b.core.mapping.bean.bl.Party();
		tem_Party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>();
		tem_Party.setPartyType('CCP');
		tem_Party.setCarrierCustomerCode('123458')
		tem_Party_list.add(tem_Party);
		current_body.setParty(tem_Party_list);
		blUtil.checkCCP(current_body,Ext_Cde,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		println "--------------case04 end----------------"

		errorKeyList.clear()
		Ext_Cde = '123456';
		current_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_Party = new cs.b2b.core.mapping.bean.bl.Party();
		tem_Party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>();
		tem_Party.setPartyType('CBP');
		tem_Party.setCarrierCustomerCode('123456')
		tem_Party_list.add(tem_Party);
		current_body.setParty(tem_Party_list);
		blUtil.checkCCP(current_body,Ext_Cde,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		println "--------------case05 end----------------"
	}

	@Test
	//author LINJA
	//status success
	public void testCheckChargeTypeNotExists(){

		errorKeyList.clear()
		String normal = "<Group7_TCC><TCC><C200_01><E8022_04></E8022_04></C200_01></TCC></Group7_TCC>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.checkChargeTypeNotExists(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);

		//error case1
		errorKeyList.clear();
		String errorcaseXML1 = "<Group7_TCC><TCC><C200_01><E8022_04>1</E8022_04></C200_01></TCC></Group7_TCC>";
		node = xmlParserToNode(testGroup_UNH(errorcaseXML1));
		blUtil.checkChargeTypeNotExists(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
	}


	@Test
	//author LINJA
	//status success
	public void testCheckNoCollectCharge(){

		cs.b2b.core.mapping.bean.bl.Body current_body;
		List<cs.b2b.core.mapping.bean.bl.FreightCharge> tem_FreightCharge_list;
		cs.b2b.core.mapping.bean.bl.FreightCharge tem_FreightCharge;

		errorKeyList.clear()
		current_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_FreightCharge = new cs.b2b.core.mapping.bean.bl.FreightCharge();
		tem_FreightCharge_list = new ArrayList<cs.b2b.core.mapping.bean.bl.FreightCharge>();
		tem_FreightCharge.setChargeType("1");
		tem_FreightCharge_list.add(tem_FreightCharge);
		current_body.setFreightCharge(tem_FreightCharge_list);
		blUtil.checkNoCollectCharge(current_body,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		println "--------------case01 end----------------"

		errorKeyList.clear();
		current_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_FreightCharge = new cs.b2b.core.mapping.bean.bl.FreightCharge();
		tem_FreightCharge_list = new ArrayList<cs.b2b.core.mapping.bean.bl.FreightCharge>();

		tem_FreightCharge.setChargeType('0');
		tem_FreightCharge_list.add(tem_FreightCharge);
		current_body.setFreightCharge(tem_FreightCharge_list);
		blUtil.checkNoCollectCharge(current_body,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		println "--------------case02 end----------------"

	}

	@Test
	//author LINJA
	//status success
	public void testCheckGroup18FTXC108IsMissing(){

		errorKeyList.clear()
		String normal = "<Group18_GID><FTX><C108_04></C108_04></FTX></Group18_GID>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.checkGroup18FTXC108IsMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case01 end----------------'

		errorKeyList.clear()
		String errorXML = "<Group18_GID><FTX></FTX></Group18_GID>";
		node = xmlParserToNode(testGroup_UNH(errorXML));
		blUtil.checkGroup18FTXC108IsMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);
		println '------------case02 end----------------'
	}

	@Test
	//author LINJA
	//status success
	public void testCheckGroup18FTXC108_4440Chars(){

		errorKeyList.clear()
		String normal = "<Group18_GID><FTX><C108_04><E4440_01>1234567890123456789012345678901234567890123456789012345678901234567890</E4440_01></C108_04></FTX></Group18_GID>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.checkGroup18FTXC108_4440Chars(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);
		println '------------case01 end----------------'

		errorKeyList.clear()
		String errorXML = "<Group18_GID><FTX><C108_04><E4440_01>123456789012345678901234567890123456789012345678901234567890</E4440_01></C108_04></FTX></Group18_GID>";
		node = xmlParserToNode(testGroup_UNH(errorXML));
		blUtil.checkGroup18FTXC108_4440Chars(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case02 end----------------'
	}

	@Test
	//author LINJA
	//status success
	public void testCheckCargoMarksAndNumbersLine(){
		cs.b2b.core.mapping.bean.bl.Body current_body;
		List<cs.b2b.core.mapping.bean.bl.Cargo> tem_Cargo_list;
		List<cs.b2b.core.mapping.bean.MarksAndNumbers> tem_MarksAndNumbers_list;
		cs.b2b.core.mapping.bean.bl.Cargo tem_Cargo;
		cs.b2b.core.mapping.bean.MarksAndNumbers tem_MarksAndNumbers

		errorKeyList.clear()
		current_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_Cargo = new cs.b2b.core.mapping.bean.bl.Cargo();
		tem_Cargo_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Cargo>();
		tem_MarksAndNumbers = new cs.b2b.core.mapping.bean.MarksAndNumbers();
		tem_MarksAndNumbers_list = new ArrayList<cs.b2b.core.mapping.bean.MarksAndNumbers>();

		tem_MarksAndNumbers.setMarksAndNumbersLine('123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890')
		tem_MarksAndNumbers_list.add(tem_MarksAndNumbers);
		tem_Cargo.setMarksAndNumbers(tem_MarksAndNumbers_list);
		tem_Cargo_list.add(tem_Cargo);
		current_body.setCargo(tem_Cargo_list);
		blUtil.checkCargoMarksAndNumbersLine(current_body,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		println "--------------case01 end----------------"

		errorKeyList.clear()
		current_body = new cs.b2b.core.mapping.bean.bl.Body();
		tem_Cargo = new cs.b2b.core.mapping.bean.bl.Cargo();
		tem_Cargo_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Cargo>();
		tem_MarksAndNumbers = new cs.b2b.core.mapping.bean.MarksAndNumbers();
		tem_MarksAndNumbers_list = new ArrayList<cs.b2b.core.mapping.bean.MarksAndNumbers>();

		tem_MarksAndNumbers.setMarksAndNumbersLine('123')
		tem_MarksAndNumbers_list.add(tem_MarksAndNumbers);
		tem_Cargo.setMarksAndNumbers(tem_MarksAndNumbers_list);
		tem_Cargo_list.add(tem_Cargo);
		current_body.setCargo(tem_Cargo_list);
		blUtil.checkCargoMarksAndNumbersLine(current_body,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		println "--------------case02 end----------------"
	}

	@Test
	//author LINJA
	//status success
	public void testCheckGroup30DGSC205_8351IsMissing(){
		errorKeyList.clear()
		String normal = "<Group30_DGS><DGS><C205_02><E8351_01></E8351_01></C205_02></DGS></Group30_DGS>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.checkGroup30DGSC205_8351IsMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);
		println '------------case01 end----------------'

		errorKeyList.clear()
		String errorXML = "<Group30_DGS><DGS><C205_02><E8351_01>123</E8351_01></C205_02></DGS></Group30_DGS>";
		node = xmlParserToNode(testGroup_UNH(errorXML));
		blUtil.checkGroup30DGSC205_8351IsMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case02 end----------------'
	}

	@Test
	//author LINJA
	//status success
	public void testCheckGroup18TMDC219IsMissing(){
		errorKeyList.clear()
		String normal = "<Group18_GID><TMD><C219_01></C219_01></TMD></Group18_GID>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.checkGroup18TMDC219IsMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case01 end----------------'

		errorKeyList.clear()
		String errorXML = "<Group18_GID><TMD></TMD></Group18_GID>";
		node = xmlParserToNode(testGroup_UNH(errorXML));
		blUtil.checkGroup18TMDC219IsMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);
		println '------------case02 end----------------'
	}

	@Test
	//author LINJA
	//status success
	public void testCheckGroup18TMDC219_8335IsMissing(){
		errorKeyList.clear()
		String normal = "<Group18_GID><TMD><C219_01><E8335_01></E8335_01></C219_01></TMD></Group18_GID>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.checkGroup18TMDC219_8335IsMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);
		println '------------case01 end----------------'

		errorKeyList.clear()
		String errorXML = "<Group18_GID><TMD><C219_01><E8335_01>123</E8335_01></C219_01></TMD></Group18_GID>";
		node = xmlParserToNode(testGroup_UNH(errorXML));
		blUtil.checkGroup18TMDC219_8335IsMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case02 end----------------'
	}

	@Test
	//author LINJA
	//status success
	public void testCheckGroup9LOCC517_3225And3224AreMissing(){
		errorKeyList.clear()
		String normal = "<Group9_LOC><LOC><C517_02><E1131_02></E1131_02><E3224_04></E3224_04></C517_02></LOC></Group9_LOC>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.checkGroup9LOCC517_3225And3224AreMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);
		println '------------case01 end----------------'

		errorKeyList.clear()
		String errorXML = "<Group9_LOC><LOC><C517_02><E1131_02>123</E1131_02><E3224_04>123</E3224_04></C517_02></LOC></Group9_LOC>";
		node = xmlParserToNode(testGroup_UNH(errorXML));
		blUtil.checkGroup9LOCC517_3225And3224AreMissing(node,true,null,errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case02 end----------------'

	}

	@Test
	//author LINJA
	//status success
	public void testCheckGrp8TDTC222_8212IsMissing() {
		errorKeyList.clear()
		String normal = "<Group8_TDT><TDT><C222_08><E8212_04></E8212_04></C222_08></TDT></Group8_TDT>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.checkGrp8TDTC222_8212IsMissing(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);
		println '------------case01 end----------------'

		errorKeyList.clear()
		String errorXML = "<Group8_TDT><TDT><C222_08><E8212_04>123</E8212_04></C222_08></TDT></Group8_TDT>";
		node = xmlParserToNode(testGroup_UNH(errorXML));
		blUtil.checkGrp8TDTC222_8212IsMissing(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case02 end----------------'
	}

	@Test
	//author LINJA
	//status success
	public void testCheckNotInboundToUS() {
		errorKeyList.clear()
		String normal = "<Group9_LOC><LOC><E3227_01>7</E3227_01><C553_04><E3233_01>UX</E3233_01></C553_04></LOC></Group9_LOC>";
		Node node = xmlParserToNode(testGroup_UNH(normal));
		blUtil.checkNotInboundToUS(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0);
		println '------------case01 end----------------'

		errorKeyList.clear()
		String errorXML = "<Group9_LOC><LOC><E3227_01>7</E3227_01><C553_04><E3233_01>US</E3233_01></C553_04></LOC></Group9_LOC>";
		node = xmlParserToNode(testGroup_UNH(errorXML));
		blUtil.checkNotInboundToUS(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case02 end----------------'

		errorKeyList.clear()
		String errorXML1 = "<Group9_LOC><LOC><E3227_01>8</E3227_01><C553_04><E3233_01>US</E3233_01></C553_04></LOC></Group9_LOC>";
		node = xmlParserToNode(testGroup_UNH(errorXML1));
		blUtil.checkNotInboundToUS(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case03 end----------------'

		errorKeyList.clear()
		String errorXML2 = "<Group9_LOC><LOC><E3227_01>8</E3227_01><C553_04><E3233_01>UX</E3233_01></C553_04></LOC></Group9_LOC>";
		node = xmlParserToNode(testGroup_UNH(errorXML2));
		blUtil.checkNotInboundToUS(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0);
		println '------------case04 end----------------'
	}

	/**
	 * @author XIEZE
	 */
	@Test
	public void testGroup_11(){
		//case 1
		String testcase1XML = "<Group11_NAD>test</Group11_NAD>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGroup_11(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2

		String testcase2XML = "<Group11_NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup_11(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 3

		String testcase3XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup_11(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)


	}
	@Test
	public void testGroup11_NAD_C080(){
		//case 1
		String testcase1XML = "<Group11_NAD><NAD></NAD></Group11_NAD>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C080(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
		//case 2
		String testcase2XML = "<Group11_NAD><NAD><C080_04>test</C080_04></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C080(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 3
		String testcase3XML = "<Group11_NAD><NAD><E3035_01>CA</E3035_01></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C080(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 4
		String testcase4XML = "<Group11_NAD><NAD><C080_04>test</C080_04><E3035_01>CA</E3035_01></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C080(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 5
		String testcase5XML = "<Group11_NAD><NAD><C080_04>test</C080_04><E3035_01>test</E3035_01></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase5XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C080(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 6
		String testcase6XML = "<Group11_NAD><NAD><E3035_01>test</E3035_01></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase6XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C080(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

	}
	@Test
	public void testGroup11_NAD_C082(){
		//case 1
		String testcase1XML = "<Group11_NAD><NAD><E3035_01>test</E3035_01></NAD></Group11_NAD>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C082(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
        //case 2
		String testcase2XML = "<Group11_NAD><NAD><E3035_01>test</E3035_01><C082_02>test</C082_02></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C082(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 3
		String testcase3XML = "<Group11_NAD><NAD><E3035_01>CA</E3035_01><C082_02>test</C082_02></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C082(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		// case 4
		String testcase4XML = "<Group11_NAD><NAD></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C082(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		// case 5
		String testcase5XML = "<Group11_NAD><NAD><C082_02>test</C082_02></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase5XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C082(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		// case 6
		String testcase6XML = "<Group11_NAD><NAD><E3035_01>CA</E3035_01></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase6XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup11_NAD_C082(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)


	}
	@Test
	public void testGroup11_NAD3039(){
		//case 1
		String testcase1XML = "<Group11_NAD><NAD><E3035_01>test</E3035_01></NAD></Group11_NAD>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkGroup11_NAD3039(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 2
		String testcase2XML = "<Group11_NAD><NAD></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkGroup11_NAD3039(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 3
		String testcase3XML = "<Group11_NAD><NAD><E3035_01>CA</E3035_01></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkGroup11_NAD3039(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 4
		String testcase4XML = "<Group11_NAD><NAD><E3035_01>test</E3035_01><C082_02><E3039_01>t</E3039_01></C082_02></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkGroup11_NAD3039(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 5
		String testcase5XML = "<Group11_NAD><NAD><E3035_01>CA</E3035_01><C082_02><E3039_01>t</E3039_01></C082_02></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase5XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkGroup11_NAD3039(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
		//case 6
		String testcase6XML = "<Group11_NAD><NAD><E3035_01>test</E3035_01><C082_02><E3039_01>testtesttest</E3039_01></C082_02></NAD></Group11_NAD>";
		node = xmlParserToNode(testGroup_UNH(testcase6XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.checkGroup11_NAD3039(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}
	@Test
	public void testGrp11_Grp12_COM(){

		String normal;
		Node node;
		cs.b2b.core.mapping.bean.bl.Body current_Body;
		List<cs.b2b.core.mapping.bean.bl.Party> tmp_party_list;
		cs.b2b.core.mapping.bean.bl.Party tmp_party;
		//case 01  party is null
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_party_list = null
		current_Body.setParty(tmp_party_list)
		blUtil.checkGrp11_Grp12_COM(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------01'

		//case 02 Contact is not exist
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>()
		current_Body.setParty(tmp_party_list)
		blUtil.checkGrp11_Grp12_COM(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------02'

		//case 03 Contact is null
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>()
		tmp_party = new cs.b2b.core.mapping.bean.bl.Party();
		tmp_party.setContact(null);
		tmp_party_list.add(tmp_party)
		current_Body.setParty(tmp_party_list)
		blUtil.checkGrp11_Grp12_COM(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------03'


		//case 04  Number.size >1
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>()
		tmp_party = new cs.b2b.core.mapping.bean.bl.Party()
		Contact contact=new Contact()
		ContactPhone contactPhone =new ContactPhone()
		contactPhone.setNumber("test")
		contact.setContactPhone(contactPhone)
		tmp_party.setContact(contact)
		tmp_party_list.add(tmp_party)
		current_Body.setParty(tmp_party_list)
		blUtil.checkGrp11_Grp12_COM(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
		println 'case ----------end----------04'


		//case 05 ContactEmailAddress.size >0
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_party_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>()
		tmp_party = new cs.b2b.core.mapping.bean.bl.Party()
		contact=new Contact()
		contact.setContactEmailAddress("test")
		tmp_party.setContact(contact)
		tmp_party_list.add(tmp_party)
		current_Body.setParty(tmp_party_list)
		blUtil.checkGrp11_Grp12_COM(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
		println 'case ----------end----------05'
	}
	@Test
	public void testGrp19_FTX(){

		String normal;
		Node node;
		cs.b2b.core.mapping.bean.bl.Body current_Body;
		List<cs.b2b.core.mapping.bean.bl.Cargo> tmp_cargo_list;
		//normal
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_cargo_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Cargo>()
        Cargo tmp_cargo = new Cargo()
		tmp_cargo.setCargoDescription("test")
		tmp_cargo_list.add(tmp_cargo)
		current_Body.setCargo(tmp_cargo_list)
		blUtil.checkGrp19_FTX(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------01'

		//exception
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_cargo_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Cargo>()
		tmp_cargo = new Cargo()
		tmp_cargo.setCargoDescription("")
		tmp_cargo_list.add(tmp_cargo)
		current_Body.setCargo(tmp_cargo_list)

		blUtil.checkGrp19_FTX(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
		println 'case ----------end----------02'

	}
	@Test
	public void testGrp1_LOC_C517_3225(){
		//case 1
		String testcase1XML = "<Group1_LOC><LOC><C517_02><E3225_01>test</E3225_01></C517_02></LOC></Group1_LOC>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGrp1_LOC_C517_3225(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2

		String testcase2XML = "<Group1_LOC><LOC><C517_02><E3225_01></E3225_01></C517_02></LOC></Group1_LOC>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp1_LOC_C517_3225(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 3

		String testcase3XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp1_LOC_C517_3225(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)
	}
	@Test
	public void testGrp1_LOC_C517_91_3224(){
		//case 1
		String testcase1XML = "<Group1_LOC><LOC><E3227_01>test</E3227_01><C517_02><E3224_04>test</E3224_04></C517_02></LOC></Group1_LOC>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGrp1_LOC_C517_91_3224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2

		String testcase2XML = "<Group1_LOC><LOC><E3227_01>91</E3227_01><C517_02><E3224_04></E3224_04></C517_02></LOC></Group1_LOC>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp1_LOC_C517_91_3224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 3

		String testcase3XML = "<Group1_LOC><LOC><E3227_01>91</E3227_01></LOC></Group1_LOC>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp1_LOC_C517_91_3224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 4

		String testcase4XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp1_LOC_C517_91_3224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
	}
	@Test
	public void testGrp1_LOC_C517_Non91_3224(){
		//case 1
		String testcase1XML = "<Group1_LOC><LOC><E3227_01>test</E3227_01><C517_02><E3224_04>test</E3224_04></C517_02></LOC></Group1_LOC>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGrp1_LOC_C517_Non91_3224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2

		String testcase2XML = "<Group1_LOC><LOC><E3227_01>test</E3227_01><C517_02><E3224_04></E3224_04></C517_02></LOC></Group1_LOC>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp1_LOC_C517_Non91_3224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 3

		String testcase3XML = "<Group1_LOC><LOC><E3227_01>test</E3227_01></LOC></Group1_LOC>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp1_LOC_C517_Non91_3224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 4

		String testcase4XML = "<Group1_LOC><LOC><E3227_01>91</E3227_01></LOC></Group1_LOC>";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp1_LOC_C517_Non91_3224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 5

		String testcase5XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase5XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp1_LOC_C517_Non91_3224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)
	}
	@Test
	public void testGroup35_EQD_C224(){
		//case 1
		String testcase1XML = "<Group35_EQD><EQD><C224_03>test</C224_03></EQD></Group35_EQD>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGroup35_EQD_C224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2
		String testcase2XML = "<Group35_EQD><EQD><C224_03></C224_03></EQD></Group35_EQD>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup35_EQD_C224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 3
		String testcase3XML = "<Group35_EQD></Group35_EQD>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup35_EQD_C224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 4
		String testcase4XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup35_EQD_C224(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

	}
	@Test
	public void testGroup35_EQD_C224_8155(){
		//case 1
		String testcase1XML = "<Group35_EQD><EQD><C224_03><E8155_01>test</E8155_01></C224_03></EQD></Group35_EQD>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGroup35_EQD_C224_8155(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2
		String testcase2XML = "<Group35_EQD><EQD><C224_03><E8155_01></E8155_01></C224_03></EQD></Group35_EQD>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup35_EQD_C224_8155(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 3
		String testcase3XML = "<Group35_EQD></Group35_EQD>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup35_EQD_C224_8155(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 4
		String testcase4XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGroup35_EQD_C224_8155(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

	}
	@Test
	public void testGrp35_MEA_C174_6314(){
		//case 1
		String testcase1XML = "<Group35_EQD><MEA><C174_03><E6314_02>test</E6314_02></C174_03></MEA></Group35_EQD>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGrp35_MEA_C174_6314(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2
		String testcase2XML = "<Group35_EQD><MEA><C174_03><E6314_02></E6314_02></C174_03></MEA></Group35_EQD>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp35_MEA_C174_6314(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 3
		String testcase3XML = "<Group35_EQD></Group35_EQD>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp35_MEA_C174_6314(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 4
		String testcase4XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp35_MEA_C174_6314(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

	}
	@Test
	public void testcheckGrp36_EQD(){

		String normal;
		Node node;
		cs.b2b.core.mapping.bean.bl.Body current_Body;
		List<cs.b2b.core.mapping.bean.bl.Container> tmp_container_list;
		//normal
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_container_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Container>()
		Container tmp_container = new Container()
		tmp_container.setContainerNumber("test")
		tmp_container_list.add(tmp_container)
		current_Body.setContainer(tmp_container_list)
		blUtil.checkGrp36_EQD(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------01'

		//exception
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_container_list = new ArrayList<cs.b2b.core.mapping.bean.bl.Container>()
		tmp_container = new Container()
		tmp_container.setContainerNumber("")
		tmp_container_list.add(tmp_container)
		current_Body.setContainer(tmp_container_list)

		blUtil.checkGrp36_EQD(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
		println 'case ----------end----------02'

	}
	@Test
	public void testGrp3_REF_C506_1154(){
		//case 1
		String testcase1XML = "<Group3_RFF><RFF><C506_01><E1154_02>test</E1154_02></C506_01></RFF></Group3_RFF>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGrp3_REF_C506_1154(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2
		String testcase2XML = "<Group3_RFF><RFF><C506_01><E1154_02></E1154_02></C506_01></RFF></Group3_RFF>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp3_REF_C506_1154(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 3
		String testcase3XML = "<Group3_RFF></Group3_RFF>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp3_REF_C506_1154(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 4
		String testcase4XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp3_REF_C506_1154(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

	}
	@Test
	public void testGrp8(){
		//case 1
		String testcase1XML = "<Group8_TDT><TDT>test</TDT></Group8_TDT>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGrp8(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2
		String testcase2XML = "<Group8_TDT><TDT></TDT></Group8_TDT>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp8(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 3
		String testcase3XML = "<Group8_TDT></Group8_TDT>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp8(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 4
		String testcase4XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp8(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

	}
	@Test
	public void testGrp9(){
		//case 1
		String testcase1XML = "<Group8_TDT><Group9_LOC>test</Group9_LOC></Group8_TDT>";
		Node node = xmlParserToNode(testGroup_UNH(testcase1XML));
		blUtil.missingGrp9(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 2
		String testcase2XML = "<Group8_TDT><Group9_LOC></Group9_LOC></Group8_TDT>";
		node = xmlParserToNode(testGroup_UNH(testcase2XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp9(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() == 0)

		//case 3
		String testcase3XML = "<Group8_TDT></Group8_TDT>";
		node = xmlParserToNode(testGroup_UNH(testcase3XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp9(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

		//case 4
		String testcase4XML = "";
		node = xmlParserToNode(testGroup_UNH(testcase4XML));
		errorKeyList = new ArrayList<String,String>();
		blUtil.missingGrp9(node, true, null, errorKeyList);
		Assert.assertTrue(errorKeyList.size() > 0)

	}
	@Test
	public void testRFF_GROUP4(){

		String normal;
		Node node;
		cs.b2b.core.mapping.bean.bl.Body current_Body;
		List<cs.b2b.core.mapping.bean.bl.ExternalReference> tmp_externalReference_list;
		//normal
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_externalReference_list = new ArrayList<cs.b2b.core.mapping.bean.bl.ExternalReference>()
		ExternalReference tmp_externalReference = new ExternalReference()
		tmp_externalReference.setReferenceDescription("test")
		tmp_externalReference_list.add(tmp_externalReference)
		current_Body.setExternalReference(tmp_externalReference_list)
		blUtil.missingRFF_GROUP4(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------01'

		//exception
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_externalReference_list = new ArrayList<cs.b2b.core.mapping.bean.bl.ExternalReference>()
		tmp_externalReference = new ExternalReference()
		tmp_externalReference.setReferenceDescription("")
		tmp_externalReference_list.add(tmp_externalReference)
		current_Body.setExternalReference(tmp_externalReference_list)
		blUtil.missingRFF_GROUP4(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
		println 'case ----------end----------02'

	}
	@Test
	public void testRFF_GROUP4_typeorname(){

		String normal;
		Node node;
		cs.b2b.core.mapping.bean.bl.Body current_Body;
		List<cs.b2b.core.mapping.bean.bl.ExternalReference> tmp_externalReference_list;
		//normal
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_externalReference_list = new ArrayList<cs.b2b.core.mapping.bean.bl.ExternalReference>()
		ExternalReference tmp_externalReference = new ExternalReference()
		tmp_externalReference.setReferenceDescription("test")
		tmp_externalReference_list.add(tmp_externalReference)
		current_Body.setExternalReference(tmp_externalReference_list)
		blUtil.missingRFF_GROUP4_typeorname(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() == 0)
		println 'case ----------end----------01'

		//exception
		errorKeyList = new ArrayList<String,String>();
		current_Body = new cs.b2b.core.mapping.bean.bl.Body()
		tmp_externalReference_list = new ArrayList<cs.b2b.core.mapping.bean.bl.ExternalReference>()
		tmp_externalReference = new ExternalReference()
		tmp_externalReference.setReferenceDescription("")
		tmp_externalReference_list.add(tmp_externalReference)
		current_Body.setExternalReference(tmp_externalReference_list)
		blUtil.missingRFF_GROUP4_typeorname(current_Body, true, null, errorKeyList)
		Assert.assertTrue(errorKeyList.size() > 0)
		println 'case ----------end----------02'

	}
	protected static String testGroup_UNH(String testFieldBGM){
		return "<root><Group_UNH>" + testFieldBGM + "</Group_UNH></root>"
	}
	protected static String testXml(String testFieldXml){
		return "<root><Loop_ST>"+ testFieldXml +"</Loop_ST></root>";
	}
	protected static Node xmlParserToNode(String testedXml){
		XmlParser xmlParser = new XmlParser();
		return xmlParser.parseText(testedXml)
	}

	protected static void printErrorKey(List<Map<String,String>> errorKeyList){
		if(errorKeyList.iterator()?.next()){
			println errorKeyList.iterator()?.next()
		}
	}
}
