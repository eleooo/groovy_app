package cs.b2b.core.mapping.bean.edi.x12.5040.T323_LOWES

public class EDI_LOWES_T323 {

	public static final Set<String> MultiElementList = ["Loop_ST","K1","Loop_R4","DTM","V9"];

	public ISA1 ISA; 
	public GS2 GS; 
	public List<Loop_ST3> Loop_ST; 
	public GE12 GE; 
	public IEA13 IEA; 
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
	public String I65_11;
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
	public String E1705_03;
}
class V15 {
	public String E597_01;
	public String E182_02;
	public String E26_03;
	public String E55_04;
	public String E140_05;
	public String E249_06;
	public String E854_07;
	public String E897_08;
	public String E91_09;
}
class K16 {
	public String E61_01;
	public String E61_02;
}
class R48 {
	public String E115_01;
	public String E309_02;
	public String E310_03;
	public String E114_04;
	public String E26_05;
	public String E174_06;
	public String E113_07;
	public String E156_08;
}
class DTM9 {
	public String E374_01;
	public String E373_02;
	public String E337_03;
	public String E623_04;
	public String E1250_05;
	public String E1251_06;
}
class V910 {
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
class Loop_R47 {
	public R48 R4; 
	public List<DTM9> DTM; 
	public List<V910> V9; 
}
class SE11 {
	public String E96_01;
	public String E329_02;
}
class Loop_ST3 {
	public ST4 ST; 
	public V15 V1; 
	public List<K16> K1; 
	public List<Loop_R47> Loop_R4; 
	public SE11 SE; 
}
class GE12 {
	public String E97_01;
	public String E28_02;
}
class IEA13 {
	public String I16_01;
	public String I12_02;
}
