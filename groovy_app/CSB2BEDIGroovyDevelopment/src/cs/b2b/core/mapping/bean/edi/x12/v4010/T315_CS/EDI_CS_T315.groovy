package cs.b2b.core.mapping.bean.edi.x12.v4010.T315_CS

public class EDI_CS_T315 {

	public static final Set<String> MultiElementList = ["Loop_ST","N9","SG","Loop_R4","DTM","V9"];

	public ISA1 ISA; 
	public GS2 GS; 
	public List<Loop_ST3> Loop_ST; 
	public GE15 GE; 
	public IEA16 IEA; 
}
class ISA1 {
	public String I01_01;
	public String I02_02;
	public String I03_03;
	public String I04_04;
	public String I05_05;
	public String I06_06;
	public String I05_07;
	public String I07_08;
	public String I08_09;
	public String I09_10;
	public String I10_11;
	public String I11_12;
	public String I12_13;
	public String I13_14;
	public String I14_15;
	public String I15_16;
}
class GS2 {
	public String E479_01;
	public String E142_02;
	public String E124_03;
	public String E373_04;
	public String E337_05;
	public String E28_06;
	public String E455_07;
	public String E480_08;
}
class ST4 {
	public String E143_01;
	public String E329_02;
}
class B45 {
	public String E152_01;
	public String E71_02;
	public String E157_03;
	public String E373_04;
	public String E161_05;
	public String E159_06;
	public String E206_07;
	public String E207_08;
	public String E578_09;
	public String E24_10;
	public String E310_11;
	public String E309_12;
	public String E761_13;
}
class C040_077 {
	public String E128_01;
	public String E127_02;
	public String E128_03;
	public String E127_04;
	public String E128_05;
	public String E127_06;
}
class N96 {
	public String E128_01;
	public String E127_02;
	public String E369_03;
	public String E373_04;
	public String E337_05;
	public String E623_06;
	public C040_077 C040_07;
}
class Q28 {
	public String E597_01;
	public String E26_02;
	public String E373_03;
	public String E373_04;
	public String E373_05;
	public String E80_06;
	public String E81_07;
	public String E187_08;
	public String E55_09;
	public String E128_10;
	public String E127_11;
	public String E897_12;
	public String E182_13;
	public String E183_14;
	public String E184_15;
	public String E188_16;
}
class SG9 {
	public String E157_01;
	public String E641_02;
	public String E35_03;
	public String E373_04;
	public String E337_05;
	public String E623_06;
}
class R411 {
	public String E115_01;
	public String E309_02;
	public String E310_03;
	public String E114_04;
	public String E26_05;
	public String E174_06;
	public String E113_07;
	public String E156_08;
}
class DTM12 {
	public String E374_01;
	public String E373_02;
	public String E337_03;
	public String E623_04;
	public String E1250_05;
	public String E1251_06;
}
class Loop_R410 {
	public R411 R4; 
	public List<DTM12> DTM; 
}
class V913 {
	public String E304_01;
	public String E106_02;
	public String E373_03;
	public String E337_04;
	public String E19_05;
	public String E156_06;
	public String E26_07;
	public String E641_08;
	public String E154_09;
	public String E380_10;
	public String E1274_11;
	public String E61_12;
	public String E623_13;
	public String E380_14;
	public String E154_15;
	public String E86_16;
	public String E86_17;
	public String E86_18;
	public String E81_19;
	public String E82_20;
}
class SE14 {
	public String E96_01;
	public String E329_02;
}
class Loop_ST3 {
	public ST4 ST; 
	public B45 B4; 
	public List<N96> N9; 
	public Q28 Q2; 
	public List<SG9> SG; 
	public List<Loop_R410> Loop_R4; 
	public List<V913> V9; 
	public SE14 SE; 
}
class GE15 {
	public String E97_01;
	public String E28_02;
}
class IEA16 {
	public String I16_01;
	public String I12_02;
}
