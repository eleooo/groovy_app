package cs.b2b.core.mapping.bean.edi.x12.4010.T301

public class EDI_CS_T301 {

	public static final Set<String> MultiElementList = ["Loop_ST","G61","Y6","Loop_Y4","N9","R2A","Loop_N1","N3","Loop_R4","DTM","H3","EA","Loop_LX","K1","Loop_H1","H2","V1","V9"];

	public ISA1 ISA; 
	public GS2 GS; 
	public List<Loop_ST3> Loop_ST; 
	public GE43 GE; 
	public IEA44 IEA; 
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
class B15 {
	public String E140_01;
	public String E145_02;
	public String E373_03;
	public String E558_04;
}
class G616 {
	public String E366_01;
	public String E93_02;
	public String E365_03;
	public String E364_04;
	public String E443_05;
}
class Y67 {
	public String E313_01;
	public String E151_02;
	public String E275_03;
}
class Y38 {
	public String E13_01;
	public String E140_02;
	public String E373_03;
	public String E373_04;
	public String E154_05;
	public String E112_06;
	public String E373_07;
	public String E337_08;
	public String E91_09;
	public String E375_10;
	public String E623_11;
}
class Y410 {
	public String E13_01;
	public String E13_02;
	public String E373_03;
	public String E154_04;
	public String E95_05;
	public String E24_06;
	public String E140_07;
	public String E309_08;
	public String E310_09;
	public String E56_10;
}
class W0911 {
	public String E40_01;
	public String E408_02;
	public String E355_03;
	public String E355_04;
	public String E3_05;
	public String E1122_06;
	public String E488_07;
	public String E380_08;
}
class Loop_Y49 {
	public Y410 Y4; 
	public W0911 W09; 
}
class C040_0713 {
	public String E128_01;
	public String E127_02;
	public String E128_03;
	public String E127_04;
	public String E128_05;
	public String E127_06;
}
class N912 {
	public String E128_01;
	public String E127_02;
	public String E369_03;
	public String E373_04;
	public String E337_05;
	public String E623_06;
	public C040_0713 C040_07;
}
class R2A14 {
	public String E133_01;
	public String E1431_02;
	public String E91_03;
	public String E140_04;
	public String E309_05;
	public String E310_06;
	public String E56_07;
	public String E1_08;
	public String E742_09;
	public String E98_10;
}
class N116 {
	public String E98_01;
	public String E93_02;
	public String E66_03;
	public String E67_04;
	public String E706_05;
	public String E98_06;
}
class N217 {
	public String E93_01;
	public String E93_02;
}
class N318 {
	public String E166_01;
	public String E166_02;
}
class N419 {
	public String E19_01;
	public String E156_02;
	public String E116_03;
	public String E26_04;
	public String E309_05;
	public String E310_06;
}
class G6120 {
	public String E366_01;
	public String E93_02;
	public String E365_03;
	public String E364_04;
	public String E443_05;
}
class Loop_N115 {
	public N116 N1; 
	public N217 N2; 
	public List<N318> N3; 
	public N419 N4; 
	public List<G6120> G61; 
}
class R422 {
	public String E115_01;
	public String E309_02;
	public String E310_03;
	public String E114_04;
	public String E26_05;
	public String E174_06;
	public String E113_07;
	public String E156_08;
}
class DTM23 {
	public String E374_01;
	public String E373_02;
	public String E337_03;
	public String E623_04;
	public String E1250_05;
	public String E1251_06;
}
class Loop_R421 {
	public R422 R4; 
	public List<DTM23> DTM; 
}
class W0924 {
	public String E40_01;
	public String E408_02;
	public String E355_03;
	public String E408_04;
	public String E355_05;
	public String E3_06;
	public String E1122_07;
	public String E488_08;
	public String E380_09;
}
class H325 {
	public String E152_01;
	public String E153_02;
	public String E241_03;
	public String E242_04;
	public String E257_05;
}
class C001_0227 {
	public String E355_01;
	public String E1018_02;
	public String E649_03;
	public String E355_04;
	public String E1018_05;
	public String E649_06;
	public String E355_07;
	public String E1018_08;
	public String E649_09;
	public String E355_10;
	public String E1018_11;
	public String E649_12;
	public String E355_13;
	public String E1018_14;
	public String E649_15;
}
class EA26 {
	public String E1402_01;
	public C001_0227 C001_02;
	public String E380_03;
}
class LX29 {
	public String E554_01;
}
class N730 {
	public String E206_01;
	public String E207_02;
	public String E81_03;
	public String E187_04;
	public String E167_05;
	public String E232_06;
	public String E205_07;
	public String E183_08;
	public String E184_09;
	public String E102_10;
	public String E40_11;
	public String E140_12;
	public String E319_13;
	public String E219_14;
	public String E567_15;
	public String E571_16;
	public String E188_17;
	public String E761_18;
	public String E56_19;
	public String E65_20;
	public String E189_21;
	public String E24_22;
	public String E140_23;
	public String E301_24;
}
class W0931 {
	public String E40_01;
	public String E408_02;
	public String E355_03;
	public String E408_04;
	public String E355_05;
	public String E3_06;
	public String E1122_07;
	public String E488_08;
	public String E380_09;
}
class K132 {
	public String E61_01;
	public String E61_02;
}
class L033 {
	public String E213_01;
	public String E220_02;
	public String E221_03;
	public String E81_04;
	public String E187_05;
	public String E183_06;
	public String E184_07;
	public String E80_08;
	public String E211_09;
	public String E458_10;
	public String E188_11;
	public String E56_12;
	public String E380_13;
	public String E211_14;
	public String E1073_15;
}
class L534 {
	public String E213_01;
	public String E79_02;
	public String E22_03;
	public String E23_04;
	public String E103_05;
	public String E87_06;
	public String E88_07;
	public String E23_08;
	public String E22_09;
	public String E595_10;
}
class L435 {
	public String E82_01;
	public String E189_02;
	public String E65_03;
	public String E90_04;
	public String E380_05;
	public String E1271_06;
}
class L136 {
	public String E213_01;
	public String E60_02;
	public String E122_03;
	public String E58_04;
	public String E191_05;
	public String E117_06;
	public String E120_07;
	public String E150_08;
	public String E121_09;
	public String E39_10;
	public String E16_11;
	public String E276_12;
	public String E257_13;
	public String E74_14;
	public String E122_15;
	public String E372_16;
	public String E220_17;
	public String E221_18;
	public String E954_19;
	public String E100_20;
	public String E610_21;
}
class H138 {
	public String E62_01;
	public String E209_02;
	public String E208_03;
	public String E64_04;
	public String E63_05;
	public String E200_06;
	public String E77_07;
	public String E355_08;
	public String E254_09;
}
class H239 {
	public String E64_01;
	public String E274_02;
}
class Loop_H137 {
	public H138 H1; 
	public List<H239> H2; 
}
class Loop_LX28 {
	public LX29 LX; 
	public N730 N7; 
	public W0931 W09; 
	public List<K132> K1; 
	public L033 L0; 
	public L534 L5; 
	public L435 L4; 
	public L136 L1; 
	public List<Loop_H137> Loop_H1; 
}
class V140 {
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
class V941 {
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
class SE42 {
	public String E96_01;
	public String E329_02;
}
class Loop_ST3 {
	public ST4 ST; 
	public B15 B1; 
	public List<G616> G61; 
	public List<Y67> Y6; 
	public Y38 Y3; 
	public List<Loop_Y49> Loop_Y4; 
	public List<N912> N9; 
	public List<R2A14> R2A; 
	public List<Loop_N115> Loop_N1; 
	public List<Loop_R421> Loop_R4; 
	public W0924 W09; 
	public List<H325> H3; 
	public List<EA26> EA; 
	public List<Loop_LX28> Loop_LX; 
	public List<V140> V1; 
	public List<V941> V9; 
	public SE42 SE; 
}
class GE43 {
	public String E97_01;
	public String E28_02;
}
class IEA44 {
	public String I16_01;
	public String I12_02;
}
