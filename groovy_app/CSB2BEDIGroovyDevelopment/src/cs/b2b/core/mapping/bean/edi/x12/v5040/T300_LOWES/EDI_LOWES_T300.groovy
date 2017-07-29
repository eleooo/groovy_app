package cs.b2b.core.mapping.bean.edi.x12.v5040.T300_LOWES

public class EDI_LOWES_T300 {

	public static final Set<String> MultiElementList = ["Loop_ST","G61","Y6","Loop_Y2","N9","R2A","Loop_N1","N3","Loop_R4","Loop_R4.DTM","H3","EA","Loop_LX","Loop_H1","H2","Loop_LH1","LH2","LH3","LFH","LEP","LHT","LHR","PER","V1","V9","K1"];

	public ISA1 ISA; 
	public GS2 GS; 
	public List<Loop_ST3> Loop_ST; 
	public GE55 GE; 
	public IEA56 IEA; 
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
	public String E1705_03;
}
class B15 {
	public String E140_01;
	public String E145_02;
	public String E373_03;
	public String E558_04;
	public String E1073_05;
	public String E1658_06;
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
class Y78 {
	public String E467_01;
	public String E470_02;
	public String E471_03;
	public String E468_04;
	public String E373_05;
}
class Y19 {
	public String E135_01;
	public String E373_02;
	public String E140_03;
	public String E91_04;
	public String E98_05;
	public String E19_06;
	public String E156_07;
	public String E375_08;
	public String E374_09;
}
class Y211 {
	public String E95_01;
	public String E78_02;
	public String E56_03;
	public String E24_04;
	public String E91_05;
	public String E177_06;
	public String E140_07;
	public String E464_08;
	public String E465_09;
	public String E466_10;
}
class W0912 {
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
class Loop_Y210 {
	public Y211 Y2; 
	public W0912 W09; 
}
class C040_0714 {
	public String E128_01;
	public String E127_02;
	public String E128_03;
	public String E127_04;
	public String E128_05;
	public String E127_06;
}
class N913 {
	public String E128_01;
	public String E127_02;
	public String E369_03;
	public String E373_04;
	public String E337_05;
	public String E623_06;
	public C040_0714 C040_07;
}
class R2A15 {
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
class N117 {
	public String E98_01;
	public String E93_02;
	public String E66_03;
	public String E67_04;
	public String E706_05;
	public String E98_06;
}
class N218 {
	public String E93_01;
	public String E93_02;
}
class N319 {
	public String E166_01;
	public String E166_02;
}
class N420 {
	public String E19_01;
	public String E156_02;
	public String E116_03;
	public String E26_04;
	public String E309_05;
	public String E310_06;
	public String E1715_07;
}
class G6121 {
	public String E366_01;
	public String E93_02;
	public String E365_03;
	public String E364_04;
	public String E443_05;
}
class Loop_N116 {
	public N117 N1; 
	public N218 N2; 
	public List<N319> N3; 
	public N420 N4; 
	public List<G6121> G61; 
}
class R423 {
	public String E115_01;
	public String E309_02;
	public String E310_03;
	public String E114_04;
	public String E26_05;
	public String E174_06;
	public String E113_07;
	public String E156_08;
}
class DTM24 {
	public String E374_01;
	public String E373_02;
	public String E337_03;
	public String E623_04;
	public String E1250_05;
	public String E1251_06;
}
class Loop_R422 {
	public R423 R4; 
	public List<DTM24> DTM; 
}
class W0925 {
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
class H326 {
	public String E152_01;
	public String E153_02;
	public String E241_03;
	public String E242_04;
	public String E257_05;
}
class C001_0228 {
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
class EA27 {
	public String E1402_01;
	public C001_0228 C001_02;
	public String E380_03;
}
class LX30 {
	public String E554_01;
}
class N731 {
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
class W0932 {
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
class DTM33 {
	public String E374_01;
	public String E373_02;
	public String E337_03;
	public String E623_04;
	public String E1250_05;
	public String E1251_06;
}
class L034 {
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
class L535 {
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
class L436 {
	public String E82_01;
	public String E189_02;
	public String E65_03;
	public String E90_04;
	public String E380_05;
	public String E1271_06;
}
class L137 {
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
	public String E148_22;
}
class H139 {
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
class H240 {
	public String E64_01;
	public String E274_02;
}
class Loop_H138 {
	public H139 H1; 
	public List<H240> H2; 
}
class LH142 {
	public String E355_01;
	public String E80_02;
	public String E277_03;
	public String E200_04;
	public String E22_05;
	public String E355_06;
	public String E380_07;
	public String E595_08;
	public String E665_09;
	public String E254_10;
	public String E1375_11;
	public String E1271_12;
}
class LH243 {
	public String E215_01;
	public String E983_02;
	public String E218_03;
	public String E222_04;
	public String E759_05;
	public String E355_06;
	public String E408_07;
	public String E355_08;
	public String E408_09;
	public String E355_10;
	public String E408_11;
	public String E188_12;
	public String E267_13;
}
class LH344 {
	public String E224_01;
	public String E984_02;
	public String E985_03;
	public String E1073_04;
}
class LFH45 {
	public String E808_01;
	public String E809_02;
	public String E809_03;
	public String E1023_04;
	public String E355_05;
	public String E380_06;
	public String E380_07;
	public String E373_08;
}
class LEP46 {
	public String E806_01;
	public String E807_02;
	public String E156_03;
	public String E127_04;
}
class LH447 {
	public String E238_01;
	public String E364_02;
	public String E254_03;
	public String E230_04;
	public String E230_05;
	public String E230_06;
	public String E271_07;
	public String E267_08;
	public String E805_09;
	public String E986_10;
	public String E364_11;
	public String E355_12;
}
class LHT48 {
	public String E215_01;
	public String E218_02;
	public String E222_03;
}
class LHR49 {
	public String E128_01;
	public String E127_02;
	public String E373_03;
}
class PER50 {
	public String E366_01;
	public String E93_02;
	public String E365_03;
	public String E364_04;
	public String E365_05;
	public String E364_06;
	public String E365_07;
	public String E364_08;
	public String E443_09;
}
class Loop_LH141 {
	public LH142 LH1; 
	public List<LH243> LH2; 
	public List<LH344> LH3; 
	public List<LFH45> LFH; 
	public List<LEP46> LEP; 
	public LH447 LH4; 
	public List<LHT48> LHT; 
	public List<LHR49> LHR; 
	public List<PER50> PER; 
}
class Loop_LX29 {
	public LX30 LX; 
	public N731 N7; 
	public W0932 W09; 
	public DTM33 DTM; 
	public L034 L0; 
	public L535 L5; 
	public L436 L4; 
	public L137 L1; 
	public List<Loop_H138> Loop_H1; 
	public List<Loop_LH141> Loop_LH1; 
}
class V151 {
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
class V952 {
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
class K153 {
	public String E61_01;
	public String E61_02;
}
class SE54 {
	public String E96_01;
	public String E329_02;
}
class Loop_ST3 {
	public ST4 ST; 
	public B15 B1; 
	public List<G616> G61; 
	public List<Y67> Y6; 
	public Y78 Y7; 
	public Y19 Y1; 
	public List<Loop_Y210> Loop_Y2; 
	public List<N913> N9; 
	public List<R2A15> R2A; 
	public List<Loop_N116> Loop_N1; 
	public List<Loop_R422> Loop_R4; 
	public W0925 W09; 
	public List<H326> H3; 
	public List<EA27> EA; 
	public List<Loop_LX29> Loop_LX; 
	public List<V151> V1; 
	public List<V952> V9; 
	public List<K153> K1; 
	public SE54 SE; 
}
class GE55 {
	public String E97_01;
	public String E28_02;
}
class IEA56 {
	public String I16_01;
	public String I12_02;
}
