package cs.b2b.core.mapping.bean.edi.x12.v4050.T310_CS

public class EDI_CS_T310 {

	public static final Set<String> MultiElementList = ["Loop_ST","Y6","N9","V1","M1","Y2","Loop_N1","N3","G61","Loop_R4","DTM","R2A","R2","K1","H3","Loop_C8","C8C","Loop_LX","Loop_N7","M7","Loop_L1","Loop_H1","H2","Loop_LH1","LH2","LH3","LFH","LEP","LHT","LHR","PER","Loop_L0","Loop_L0.L5","PWK","V9","Loop_ST.C8"];

	public ISA1 ISA; 
	public GS2 GS; 
	public List<Loop_ST3> Loop_ST; 
	public GE102 GE; 
	public IEA103 IEA; 
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
class B35 {
	public String E147_01;
	public String E76_02;
	public String E145_03;
	public String E146_04;
	public String E188_05;
	public String E373_06;
	public String E193_07;
	public String E202_08;
	public String E32_09;
	public String E374_10;
	public String E140_11;
	public String E373_12;
	public String E375_13;
	public String E335_14;
}
class B2A6 {
	public String E353_01;
	public String E346_02;
}
class Y67 {
	public String E313_01;
	public String E151_02;
	public String E275_03;
}
class G38 {
	public String E315_01;
	public String E317_02;
	public String E93_03;
	public String E201_04;
	public String E782_05;
	public String E73_06;
}
class C040_0710 {
	public String E128_01;
	public String E127_02;
	public String E128_03;
	public String E127_04;
	public String E128_05;
	public String E127_06;
}
class N99 {
	public String E128_01;
	public String E127_02;
	public String E369_03;
	public String E373_04;
	public String E337_05;
	public String E623_06;
	public C040_0710 C040_07;
}
class V111 {
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
class M012 {
	public String E250_01;
	public String E373_02;
	public String E373_03;
	public String E373_04;
}
class M113 {
	public String E26_01;
	public String E14_02;
	public String E74_03;
	public String E122_04;
	public String E98_05;
	public String E61_06;
	public String E122_07;
	public String E782_08;
	public String E1004_09;
	public String E954_10;
	public String E1004_11;
	public String E954_12;
}
class C214 {
	public String E8_01;
	public String E66_02;
	public String E67_03;
	public String E20_04;
	public String E7_05;
	public String E107_06;
	public String E373_07;
}
class C315 {
	public String E100_01;
	public String E280_02;
	public String E100_03;
	public String E100_04;
}
class Y216 {
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
class N118 {
	public String E98_01;
	public String E93_02;
	public String E66_03;
	public String E67_04;
	public String E706_05;
	public String E98_06;
}
class N219 {
	public String E93_01;
	public String E93_02;
}
class N320 {
	public String E166_01;
	public String E166_02;
}
class N421 {
	public String E19_01;
	public String E156_02;
	public String E116_03;
	public String E26_04;
	public String E309_05;
	public String E310_06;
	public String E1715_07;
}
class Loop_N117 {
	public N118 N1; 
	public N219 N2; 
	public List<N320> N3; 
	public N421 N4; 
}
class G6122 {
	public String E366_01;
	public String E93_02;
	public String E365_03;
	public String E364_04;
	public String E443_05;
}
class R424 {
	public String E115_01;
	public String E309_02;
	public String E310_03;
	public String E114_04;
	public String E26_05;
	public String E174_06;
	public String E113_07;
	public String E156_08;
}
class DTM25 {
	public String E374_01;
	public String E373_02;
	public String E337_03;
	public String E623_04;
	public String E1250_05;
	public String E1251_06;
}
class Loop_R423 {
	public R424 R4; 
	public List<DTM25> DTM; 
}
class R2A26 {
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
class R227 {
	public String E140_01;
	public String E133_02;
	public String E19_03;
	public String E154_04;
	public String E177_05;
	public String E91_06;
	public String E296_07;
	public String E296_08;
	public String E76_09;
	public String E373_10;
	public String E369_11;
	public String E56_12;
	public String E742_13;
}
class K128 {
	public String E61_01;
	public String E61_02;
}
class H329 {
	public String E152_01;
	public String E153_02;
	public String E241_03;
	public String E242_04;
	public String E257_05;
}
class L530 {
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
class C832 {
	public String E213_01;
	public String E246_02;
	public String E247_03;
	public String E1302_04;
}
class C8C33 {
	public String E247_01;
	public String E247_02;
	public String E247_03;
}
class Loop_C831 {
	public C832 C8; 
	public List<C8C33> C8C; 
}
class LX35 {
	public String E554_01;
}
class N737 {
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
class C001_0339 {
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
class QTY38 {
	public String E673_01;
	public String E380_02;
	public C001_0339 C001_03;
	public String E61_04;
}
class V440 {
	public String E877_01;
}
class C001_0242 {
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
class N1241 {
	public String E829_01;
	public C001_0242 C001_02;
}
class M743 {
	public String E225_01;
	public String E225_02;
	public String E225_03;
	public String E225_04;
	public String E98_05;
}
class W0944 {
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
class L146 {
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
class C347 {
	public String E100_01;
	public String E280_02;
	public String E100_03;
	public String E100_04;
}
class Loop_L145 {
	public L146 L1; 
	public C347 C3; 
}
class L748 {
	public String E213_01;
	public String E168_02;
	public String E171_03;
	public String E172_04;
	public String E169_05;
	public String E170_06;
	public String E59_07;
	public String E173_08;
	public String E46_09;
	public String E373_10;
	public String E119_11;
	public String E227_12;
	public String E294_13;
	public String E295_14;
	public String E19_15;
	public String E156_16;
}
class X149 {
	public String E83_01;
	public String E50_02;
	public String E51_03;
	public String E373_04;
	public String E52_05;
	public String E48_06;
	public String E26_07;
	public String E141_08;
	public String E210_09;
	public String E80_10;
	public String E148_11;
	public String E47_12;
	public String E355_13;
	public String E212_14;
	public String E1306_15;
	public String E67_16;
	public String E310_17;
}
class X250 {
	public String E70_01;
	public String E373_02;
	public String E373_03;
	public String E70_04;
	public String E373_05;
	public String E373_06;
}
class C040_0752 {
	public String E128_01;
	public String E127_02;
	public String E128_03;
	public String E127_04;
	public String E128_05;
	public String E127_06;
}
class N951 {
	public String E128_01;
	public String E127_02;
	public String E369_03;
	public String E373_04;
	public String E337_05;
	public String E623_06;
	public C040_0752 C040_07;
}
class H154 {
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
class H255 {
	public String E64_01;
	public String E274_02;
}
class Loop_H153 {
	public H154 H1; 
	public List<H255> H2; 
}
class LH157 {
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
}
class LH258 {
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
class LH359 {
	public String E224_01;
	public String E984_02;
	public String E985_03;
	public String E1073_04;
}
class LFH60 {
	public String E808_01;
	public String E809_02;
	public String E809_03;
	public String E1023_04;
	public String E355_05;
	public String E380_06;
	public String E380_07;
}
class LEP61 {
	public String E806_01;
	public String E807_02;
	public String E156_03;
	public String E127_04;
}
class LH462 {
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
class LHT63 {
	public String E215_01;
	public String E218_02;
	public String E222_03;
}
class LHR64 {
	public String E128_01;
	public String E127_02;
	public String E373_03;
}
class PER65 {
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
class Loop_LH156 {
	public LH157 LH1; 
	public List<LH258> LH2; 
	public List<LH359> LH3; 
	public List<LFH60> LFH; 
	public List<LEP61> LEP; 
	public LH462 LH4; 
	public List<LHT63> LHT; 
	public List<LHR64> LHR; 
	public List<PER65> PER; 
}
class Loop_N736 {
	public N737 N7; 
	public QTY38 QTY; 
	public V440 V4; 
	public N1241 N12; 
	public List<M743> M7; 
	public W0944 W09; 
	public List<Loop_L145> Loop_L1; 
	public L748 L7; 
	public X149 X1; 
	public X250 X2; 
	public List<N951> N9; 
	public List<Loop_H153> Loop_H1; 
	public List<Loop_LH156> Loop_LH1; 
}
class L067 {
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
class L568 {
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
class L170 {
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
class C371 {
	public String E100_01;
	public String E280_02;
	public String E100_03;
	public String E100_04;
}
class Loop_L169 {
	public L170 L1; 
	public C371 C3; 
}
class L772 {
	public String E213_01;
	public String E168_02;
	public String E171_03;
	public String E172_04;
	public String E169_05;
	public String E170_06;
	public String E59_07;
	public String E173_08;
	public String E46_09;
	public String E373_10;
	public String E119_11;
	public String E227_12;
	public String E294_13;
	public String E295_14;
	public String E19_15;
	public String E156_16;
}
class X173 {
	public String E83_01;
	public String E50_02;
	public String E51_03;
	public String E373_04;
	public String E52_05;
	public String E48_06;
	public String E26_07;
	public String E141_08;
	public String E210_09;
	public String E80_10;
	public String E148_11;
	public String E47_12;
	public String E355_13;
	public String E212_14;
	public String E1306_15;
	public String E67_16;
	public String E310_17;
}
class X274 {
	public String E70_01;
	public String E373_02;
	public String E373_03;
	public String E70_04;
	public String E373_05;
	public String E373_06;
}
class C876 {
	public String E213_01;
	public String E246_02;
	public String E247_03;
	public String E1302_04;
}
class C8C77 {
	public String E247_01;
	public String E247_02;
	public String E247_03;
}
class Loop_C875 {
	public C876 C8; 
	public List<C8C77> C8C; 
}
class H179 {
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
class H280 {
	public String E64_01;
	public String E274_02;
}
class Loop_H178 {
	public H179 H1; 
	public List<H280> H2; 
}
class LH182 {
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
}
class LH283 {
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
class LH384 {
	public String E224_01;
	public String E984_02;
	public String E985_03;
	public String E1073_04;
}
class LFH85 {
	public String E808_01;
	public String E809_02;
	public String E809_03;
	public String E1023_04;
	public String E355_05;
	public String E380_06;
	public String E380_07;
}
class LEP86 {
	public String E806_01;
	public String E807_02;
	public String E156_03;
	public String E127_04;
}
class LH487 {
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
class LHT88 {
	public String E215_01;
	public String E218_02;
	public String E222_03;
}
class LHR89 {
	public String E128_01;
	public String E127_02;
	public String E373_03;
}
class PER90 {
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
class Loop_LH181 {
	public LH182 LH1; 
	public List<LH283> LH2; 
	public List<LH384> LH3; 
	public List<LFH85> LFH; 
	public List<LEP86> LEP; 
	public LH487 LH4; 
	public List<LHT88> LHT; 
	public List<LHR89> LHR; 
	public List<PER90> PER; 
}
class Loop_L066 {
	public L067 L0; 
	public List<L568> L5; 
	public List<Loop_L169> Loop_L1; 
	public L772 L7; 
	public X173 X1; 
	public X274 X2; 
	public List<Loop_C875> Loop_C8; 
	public List<Loop_H178> Loop_H1; 
	public List<Loop_LH181> Loop_LH1; 
}
class Loop_LX34 {
	public LX35 LX; 
	public List<Loop_N736> Loop_N7; 
	public List<Loop_L066> Loop_L0; 
}
class L391 {
	public String E81_01;
	public String E187_02;
	public String E60_03;
	public String E122_04;
	public String E58_05;
	public String E191_06;
	public String E117_07;
	public String E150_08;
	public String E183_09;
	public String E184_10;
	public String E80_11;
	public String E188_12;
	public String E171_13;
	public String E74_14;
	public String E122_15;
}
class C002_0893 {
	public String E704_01;
	public String E704_02;
	public String E704_03;
	public String E704_04;
	public String E704_05;
}
class PWK92 {
	public String E755_01;
	public String E756_02;
	public String E757_03;
	public String E98_04;
	public String E66_05;
	public String E67_06;
	public String E352_07;
	public C002_0893 C002_08;
	public String E1525_09;
}
class L195 {
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
class C396 {
	public String E100_01;
	public String E280_02;
	public String E100_03;
	public String E100_04;
}
class Loop_L194 {
	public L195 L1; 
	public C396 C3; 
}
class V997 {
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
class C898 {
	public String E213_01;
	public String E246_02;
	public String E247_03;
	public String E1302_04;
}
class K199 {
	public String E61_01;
	public String E61_02;
}
class L11100 {
	public String E127_01;
	public String E128_02;
	public String E352_03;
	public String E373_04;
	public String E1073_05;
}
class SE101 {
	public String E96_01;
	public String E329_02;
}
class Loop_ST3 {
	public ST4 ST; 
	public B35 B3; 
	public B2A6 B2A; 
	public List<Y67> Y6; 
	public G38 G3; 
	public List<N99> N9; 
	public List<V111> V1; 
	public M012 M0; 
	public List<M113> M1; 
	public C214 C2; 
	public C315 C3; 
	public List<Y216> Y2; 
	public List<Loop_N117> Loop_N1; 
	public List<G6122> G61; 
	public List<Loop_R423> Loop_R4; 
	public List<R2A26> R2A; 
	public List<R227> R2; 
	public List<K128> K1; 
	public List<H329> H3; 
	public L530 L5; 
	public List<Loop_C831> Loop_C8; 
	public List<Loop_LX34> Loop_LX; 
	public L391 L3; 
	public List<PWK92> PWK; 
	public List<Loop_L194> Loop_L1; 
	public List<V997> V9; 
	public List<C898> C8; 
	public List<K199> K1; 
	public L11100 L11; 
	public SE101 SE; 
}
class GE102 {
	public String E97_01;
	public String E28_02;
}
class IEA103 {
	public String I16_01;
	public String I12_02;
}
