package cs.b2b.core.mapping.bean.edi.edifact.d96b.IFTMCS_CS

public class EDI_CS_IFTMCS {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","COM","Group_UNH.DTM","Group_UNH.TSR","Group_UNH.CUX","MOA","Group_UNH.FTX","CNT","Group1_LOC","Group1_LOC.DTM","Group2_TOD","Group2_TOD.LOC","Group3_RFF","Group4_GOR","Group4_GOR.DTM","Group4_GOR.LOC","SEL","Group4_GOR.FTX","Group5_DOC","Group6_CPI","Group6_CPI.RFF","Group6_CPI.LOC","Group7_TCC","QTY","Group8_TDT","Group8_TDT.DTM","Group8_TDT.TSR","Group9_LOC","Group10_RFF","Group11_NAD","Group11_NAD.LOC","Group12_CTA","Group13_DOC","Group14_TCC","Group15_RFF","Group15_RFF.DTM","Group16_CPI","Group16_CPI.RFF","Group16_CPI.LOC","Group17_TSR","Group17_TSR.FTX","Group18_GID","Group18_GID.LOC","PIA","Group18_GID.FTX","Group19_NAD","GDS","Group20_MEA","Group21_DIM","Group22_RFF","Group22_RFF.DTM","Group23_PCI","Group24_DOC","Group24_DOC.DTM","Group25_TPL","Group26_MEA","Group27_SGP","Group28_MEA","Group29_TCC","Group29_TCC.LOC","Group30_DGS","Group30_DGS.FTX","Group31_CTA","Group32_MEA","Group33_SGP","Group34_MEA","Group35_EQD","Group35_EQD.MEA","Group35_EQD.DIM","Group35_EQD.TPL","Group35_EQD.FTX","Group36_TCC","Group37_NAD","Group38_EQA"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE429 UNE; 
	public UNZ430 UNZ; 
}
class UNA1 {
	public String UNA_01;
	public String UNA_02;
	public String UNA_03;
	public String UNA_04;
	public String UNA_05;
	public String UNA_06;
}
class S001_013 {
	public String E0001_01;
	public String E0002_02;
}
class S002_024 {
	public String E0004_01;
	public String E0007_02;
	public String E0008_03;
}
class S003_035 {
	public String E0010_01;
	public String E0007_02;
	public String E0014_03;
}
class S004_046 {
	public String E0017_01;
	public String E0019_02;
}
class S005_067 {
	public String E0022_01;
	public String E0025_02;
}
class UNB2 {
	public S001_013 S001_01;
	public S002_024 S002_02;
	public S003_035 S003_03;
	public S004_046 S004_04;
	public String E0020_05;
	public S005_067 S005_06;
	public String E0026_07;
	public String E0029_08;
	public String E0031_09;
	public String E0032_10;
	public String E0035_11;
}
class S006_029 {
	public String E0040_01;
	public String E0007_02;
}
class S007_0310 {
	public String E0044_01;
	public String E0007_02;
}
class S004_0411 {
	public String E0017_01;
	public String E0019_02;
}
class S008_0712 {
	public String E0052_01;
	public String E0054_02;
	public String E0057_03;
}
class UNG8 {
	public String E0038_01;
	public S006_029 S006_02;
	public S007_0310 S007_03;
	public S004_0411 S004_04;
	public String E0048_05;
	public String E0051_06;
	public S008_0712 S008_07;
	public String E0058_08;
}
class S009_0215 {
	public String E0065_01;
	public String E0052_02;
	public String E0054_03;
	public String E0051_04;
	public String E0057_05;
}
class S010_0416 {
	public String E0070_01;
	public String E0073_02;
}
class UNH14 {
	public String E0062_01;
	public S009_0215 S009_02;
	public String E0068_03;
	public S010_0416 S010_04;
}
class C002_0118 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C106_0219 {
	public String E1004_01;
	public String E1056_02;
	public String E1060_03;
}
class BGM17 {
	public C002_0118 C002_01;
	public C106_0219 C106_02;
	public String E1225_03;
	public String E4343_04;
}
class C056_0221 {
	public String E3413_01;
	public String E3412_02;
}
class CTA20 {
	public String E3139_01;
	public C056_0221 C056_02;
}
class C076_0123 {
	public String E3148_01;
	public String E3155_02;
}
class COM22 {
	public C076_0123 C076_01;
}
class C507_0125 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM24 {
	public C507_0125 C507_01;
}
class C536_0127 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_0228 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_0329 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_0430 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR26 {
	public C536_0127 C536_01;
	public C233_0228 C233_02;
	public C537_0329 C537_03;
	public C703_0430 C703_04;
}
class C504_0132 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0233 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX31 {
	public C504_0132 C504_01;
	public C504_0233 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C516_0135 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA34 {
	public C516_0135 C516_01;
}
class C107_0337 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0438 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX36 {
	public String E4451_01;
	public String E4453_02;
	public C107_0337 C107_03;
	public C108_0438 C108_04;
	public String E3453_05;
}
class C270_0140 {
	public String E6069_01;
	public String E6066_02;
	public String E6411_03;
}
class CNT39 {
	public C270_0140 C270_01;
}
class C517_0243 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0344 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0445 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC42 {
	public String E3227_01;
	public C517_0243 C517_02;
	public C519_0344 C519_03;
	public C553_0445 C553_04;
	public String E5479_05;
}
class C507_0147 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM46 {
	public C507_0147 C507_01;
}
class Group1_LOC41 {
	public LOC42 LOC; 
	public List<DTM46> DTM; 
}
class C100_0350 {
	public String E4053_01;
	public String E1131_02;
	public String E3055_03;
	public String E4052_04;
	public String E4052_05;
}
class TOD49 {
	public String E4055_01;
	public String E4215_02;
	public C100_0350 C100_03;
}
class C517_0252 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0353 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0454 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC51 {
	public String E3227_01;
	public C517_0252 C517_02;
	public C519_0353 C519_03;
	public C553_0454 C553_04;
	public String E5479_05;
}
class Group2_TOD48 {
	public TOD49 TOD; 
	public List<LOC51> LOC; 
}
class C506_0157 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF56 {
	public C506_0157 C506_01;
}
class C507_0159 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM58 {
	public C507_0159 C507_01;
}
class Group3_RFF55 {
	public RFF56 RFF; 
	public DTM58 DTM; 
}
class C232_0262 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0363 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0464 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0565 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class GOR61 {
	public String E8323_01;
	public C232_0262 C232_02;
	public C232_0363 C232_03;
	public C232_0464 C232_04;
	public C232_0565 C232_05;
}
class C507_0167 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM66 {
	public C507_0167 C507_01;
}
class C517_0269 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0370 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0471 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC68 {
	public String E3227_01;
	public C517_0269 C517_02;
	public C519_0370 C519_03;
	public C553_0471 C553_04;
	public String E5479_05;
}
class C215_0273 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class SEL72 {
	public String E9308_01;
	public C215_0273 C215_02;
	public String E4517_03;
}
class C107_0375 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0476 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX74 {
	public String E4451_01;
	public String E4453_02;
	public C107_0375 C107_03;
	public C108_0476 C108_04;
	public String E3453_05;
}
class C002_0179 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0280 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC78 {
	public C002_0179 C002_01;
	public C503_0280 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_0182 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM81 {
	public C507_0182 C507_01;
}
class Group5_DOC77 {
	public DOC78 DOC; 
	public DTM81 DTM; 
}
class Group4_GOR60 {
	public GOR61 GOR; 
	public List<DTM66> DTM; 
	public List<LOC68> LOC; 
	public List<SEL72> SEL; 
	public List<FTX74> FTX; 
	public List<Group5_DOC77> Group5_DOC; 
}
class C229_0185 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_0286 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI84 {
	public C229_0185 C229_01;
	public C231_0286 C231_02;
	public String E4237_03;
}
class C506_0188 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF87 {
	public C506_0188 C506_01;
}
class C504_0190 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0291 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX89 {
	public C504_0190 C504_01;
	public C504_0291 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_0293 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0394 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0495 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC92 {
	public String E3227_01;
	public C517_0293 C517_02;
	public C519_0394 C519_03;
	public C553_0495 C553_04;
	public String E5479_05;
}
class C516_0197 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA96 {
	public C516_0197 C516_01;
}
class Group6_CPI83 {
	public CPI84 CPI; 
	public List<RFF87> RFF; 
	public CUX89 CUX; 
	public List<LOC92> LOC; 
	public List<MOA96> MOA; 
}
class C200_01100 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02101 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
	public String E5242_04;
	public String E5275_05;
	public String E1131_06;
	public String E3055_07;
	public String E5275_08;
	public String E1131_09;
	public String E3055_10;
}
class C528_03102 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04103 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC99 {
	public C200_01100 C200_01;
	public C203_02101 C203_02;
	public C528_03102 C528_03;
	public C554_04103 C554_04;
}
class C517_02105 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03106 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04107 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC104 {
	public String E3227_01;
	public C517_02105 C517_02;
	public C519_03106 C519_03;
	public C553_04107 C553_04;
	public String E5479_05;
}
class C107_03109 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04110 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX108 {
	public String E4451_01;
	public String E4453_02;
	public C107_03109 C107_03;
	public C108_04110 C108_04;
	public String E3453_05;
}
class C504_01112 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02113 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX111 {
	public C504_01112 C504_01;
	public C504_02113 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01115 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI114 {
	public C509_01115 C509_01;
	public String E5213_02;
}
class C523_01117 {
	public String E6350_01;
	public String E6353_02;
}
class EQN116 {
	public C523_01117 C523_01;
}
class C501_01119 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD118 {
	public C501_01119 C501_01;
}
class C516_01121 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA120 {
	public C516_01121 C516_01;
}
class C186_01123 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY122 {
	public C186_01123 C186_01;
}
class Group7_TCC98 {
	public TCC99 TCC; 
	public LOC104 LOC; 
	public FTX108 FTX; 
	public CUX111 CUX; 
	public PRI114 PRI; 
	public EQN116 EQN; 
	public PCD118 PCD; 
	public List<MOA120> MOA; 
	public List<QTY122> QTY; 
}
class C220_03126 {
	public String E8067_01;
	public String E8066_02;
}
class C228_04127 {
	public String E8179_01;
	public String E8178_02;
}
class C040_05128 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_07129 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_08130 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT125 {
	public String E8051_01;
	public String E8028_02;
	public C220_03126 C220_03;
	public C228_04127 C228_04;
	public C040_05128 C040_05;
	public String E8101_06;
	public C401_07129 C401_07;
	public C222_08130 C222_08;
	public String E8281_09;
}
class C507_01132 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM131 {
	public C507_01132 C507_01;
}
class C536_01134 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02135 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03136 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04137 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR133 {
	public C536_01134 C536_01;
	public C233_02135 C233_02;
	public C537_03136 C537_03;
	public C703_04137 C703_04;
}
class C517_02140 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03141 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04142 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC139 {
	public String E3227_01;
	public C517_02140 C517_02;
	public C519_03141 C519_03;
	public C553_04142 C553_04;
	public String E5479_05;
}
class C507_01144 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM143 {
	public C507_01144 C507_01;
}
class Group9_LOC138 {
	public LOC139 LOC; 
	public DTM143 DTM; 
}
class C506_01147 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF146 {
	public C506_01147 C506_01;
}
class C507_01149 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM148 {
	public C507_01149 C507_01;
}
class Group10_RFF145 {
	public RFF146 RFF; 
	public DTM148 DTM; 
}
class Group8_TDT124 {
	public TDT125 TDT; 
	public List<DTM131> DTM; 
	public List<TSR133> TSR; 
	public List<Group9_LOC138> Group9_LOC; 
	public List<Group10_RFF145> Group10_RFF; 
}
class C082_02152 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03153 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04154 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05155 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD151 {
	public String E3035_01;
	public C082_02152 C082_02;
	public C058_03153 C058_03;
	public C080_04154 C080_04;
	public C059_05155 C059_05;
	public String E3164_06;
	public String E3229_07;
	public String E3251_08;
	public String E3207_09;
}
class C517_02157 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03158 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04159 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC156 {
	public String E3227_01;
	public C517_02157 C517_02;
	public C519_03158 C519_03;
	public C553_04159 C553_04;
	public String E5479_05;
}
class C516_01161 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA160 {
	public C516_01161 C516_01;
}
class C056_02164 {
	public String E3413_01;
	public String E3412_02;
}
class CTA163 {
	public String E3139_01;
	public C056_02164 C056_02;
}
class C076_01166 {
	public String E3148_01;
	public String E3155_02;
}
class COM165 {
	public C076_01166 C076_01;
}
class Group12_CTA162 {
	public CTA163 CTA; 
	public List<COM165> COM; 
}
class C002_01169 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02170 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC168 {
	public C002_01169 C002_01;
	public C503_02170 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01172 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM171 {
	public C507_01172 C507_01;
}
class Group13_DOC167 {
	public DOC168 DOC; 
	public DTM171 DTM; 
}
class C200_01175 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02176 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
	public String E5242_04;
	public String E5275_05;
	public String E1131_06;
	public String E3055_07;
	public String E5275_08;
	public String E1131_09;
	public String E3055_10;
}
class C528_03177 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04178 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC174 {
	public C200_01175 C200_01;
	public C203_02176 C203_02;
	public C528_03177 C528_03;
	public C554_04178 C554_04;
}
class C509_01180 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI179 {
	public C509_01180 C509_01;
	public String E5213_02;
}
class C523_01182 {
	public String E6350_01;
	public String E6353_02;
}
class EQN181 {
	public C523_01182 C523_01;
}
class C501_01184 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD183 {
	public C501_01184 C501_01;
}
class C516_01186 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA185 {
	public C516_01186 C516_01;
}
class C186_01188 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY187 {
	public C186_01188 C186_01;
}
class Group14_TCC173 {
	public TCC174 TCC; 
	public PRI179 PRI; 
	public EQN181 EQN; 
	public PCD183 PCD; 
	public List<MOA185> MOA; 
	public List<QTY187> QTY; 
}
class C506_01191 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF190 {
	public C506_01191 C506_01;
}
class C507_01193 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM192 {
	public C507_01193 C507_01;
}
class Group15_RFF189 {
	public RFF190 RFF; 
	public List<DTM192> DTM; 
}
class C229_01196 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_02197 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI195 {
	public C229_01196 C229_01;
	public C231_02197 C231_02;
	public String E4237_03;
}
class C506_01199 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF198 {
	public C506_01199 C506_01;
}
class C504_01201 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02202 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX200 {
	public C504_01201 C504_01;
	public C504_02202 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_02204 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03205 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04206 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC203 {
	public String E3227_01;
	public C517_02204 C517_02;
	public C519_03205 C519_03;
	public C553_04206 C553_04;
	public String E5479_05;
}
class C516_01208 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA207 {
	public C516_01208 C516_01;
}
class Group16_CPI194 {
	public CPI195 CPI; 
	public List<RFF198> RFF; 
	public CUX200 CUX; 
	public List<LOC203> LOC; 
	public List<MOA207> MOA; 
}
class C536_01211 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02212 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03213 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04214 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR210 {
	public C536_01211 C536_01;
	public C233_02212 C233_02;
	public C537_03213 C537_03;
	public C703_04214 C703_04;
}
class C506_01216 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF215 {
	public C506_01216 C506_01;
}
class C517_02218 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03219 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04220 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC217 {
	public String E3227_01;
	public C517_02218 C517_02;
	public C519_03219 C519_03;
	public C553_04220 C553_04;
	public String E5479_05;
}
class C222_01222 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL221 {
	public C222_01222 C222_01;
}
class C107_03224 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04225 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX223 {
	public String E4451_01;
	public String E4453_02;
	public C107_03224 C107_03;
	public C108_04225 C108_04;
	public String E3453_05;
}
class Group17_TSR209 {
	public TSR210 TSR; 
	public RFF215 RFF; 
	public LOC217 LOC; 
	public TPL221 TPL; 
	public List<FTX223> FTX; 
}
class Group11_NAD150 {
	public NAD151 NAD; 
	public List<LOC156> LOC; 
	public List<MOA160> MOA; 
	public List<Group12_CTA162> Group12_CTA; 
	public List<Group13_DOC167> Group13_DOC; 
	public List<Group14_TCC173> Group14_TCC; 
	public List<Group15_RFF189> Group15_RFF; 
	public List<Group16_CPI194> Group16_CPI; 
	public List<Group17_TSR209> Group17_TSR; 
}
class C213_02228 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_03229 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_04230 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class GID227 {
	public String E1496_01;
	public C213_02228 C213_02;
	public C213_03229 C213_03;
	public C213_04230 C213_04;
}
class C524_01232 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02233 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN231 {
	public C524_01232 C524_01;
	public C218_02233 C218_02;
}
class C239_02235 {
	public String E6246_01;
	public String E6411_02;
}
class TMP234 {
	public String E6245_01;
	public C239_02235 C239_02;
}
class C280_02237 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG236 {
	public String E6167_01;
	public C280_02237 C280_02;
}
class C219_01239 {
	public String E8335_01;
	public String E8334_02;
}
class TMD238 {
	public C219_01239 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02241 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03242 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04243 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC240 {
	public String E3227_01;
	public C517_02241 C517_02;
	public C519_03242 C519_03;
	public C553_04243 C553_04;
	public String E5479_05;
}
class C516_01245 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA244 {
	public C516_01245 C516_01;
}
class C212_02247 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_03248 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_04249 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_05250 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_06251 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class PIA246 {
	public String E4347_01;
	public C212_02247 C212_02;
	public C212_03248 C212_03;
	public C212_04249 C212_04;
	public C212_05250 C212_05;
	public C212_06251 C212_06;
}
class C107_03253 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04254 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX252 {
	public String E4451_01;
	public String E4453_02;
	public C107_03253 C107_03;
	public C108_04254 C108_04;
	public String E3453_05;
}
class C082_02257 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03258 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04259 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05260 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD256 {
	public String E3035_01;
	public C082_02257 C082_02;
	public C058_03258 C058_03;
	public C080_04259 C080_04;
	public C059_05260 C059_05;
	public String E3164_06;
	public String E3229_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01262 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM261 {
	public C507_01262 C507_01;
}
class Group19_NAD255 {
	public NAD256 NAD; 
	public DTM261 DTM; 
}
class C703_01264 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS263 {
	public C703_01264 C703_01;
}
class C502_02267 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03268 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA266 {
	public String E6311_01;
	public C502_02267 C502_02;
	public C174_03268 C174_03;
	public String E7383_04;
}
class C523_01270 {
	public String E6350_01;
	public String E6353_02;
}
class EQN269 {
	public C523_01270 C523_01;
}
class Group20_MEA265 {
	public MEA266 MEA; 
	public EQN269 EQN; 
}
class C211_02273 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM272 {
	public String E6145_01;
	public C211_02273 C211_02;
}
class C523_01275 {
	public String E6350_01;
	public String E6353_02;
}
class EQN274 {
	public C523_01275 C523_01;
}
class Group21_DIM271 {
	public DIM272 DIM; 
	public EQN274 EQN; 
}
class C506_01278 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF277 {
	public C506_01278 C506_01;
}
class C507_01280 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM279 {
	public C507_01280 C507_01;
}
class Group22_RFF276 {
	public RFF277 RFF; 
	public List<DTM279> DTM; 
}
class C210_02283 {
	public String E7102_01;
	public String E7102_02;
	public String E7102_03;
	public String E7102_04;
	public String E7102_05;
	public String E7102_06;
	public String E7102_07;
	public String E7102_08;
	public String E7102_09;
	public String E7102_10;
}
class C827_04284 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI282 {
	public String E4233_01;
	public C210_02283 C210_02;
	public String E8275_03;
	public C827_04284 C827_04;
}
class C506_01286 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF285 {
	public C506_01286 C506_01;
}
class C507_01288 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM287 {
	public C507_01288 C507_01;
}
class C208_02290 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03291 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04292 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05293 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06294 {
	public String E7402_01;
	public String E7402_02;
}
class GIN289 {
	public String E7405_01;
	public C208_02290 C208_02;
	public C208_03291 C208_03;
	public C208_04292 C208_04;
	public C208_05293 C208_05;
	public C208_06294 C208_06;
}
class Group23_PCI281 {
	public PCI282 PCI; 
	public RFF285 RFF; 
	public DTM287 DTM; 
	public GIN289 GIN; 
}
class C002_01297 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02298 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC296 {
	public C002_01297 C002_01;
	public C503_02298 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01300 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM299 {
	public C507_01300 C507_01;
}
class Group24_DOC295 {
	public DOC296 DOC; 
	public List<DTM299> DTM; 
}
class C222_01303 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL302 {
	public C222_01303 C222_01;
}
class C502_02306 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03307 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA305 {
	public String E6311_01;
	public C502_02306 C502_02;
	public C174_03307 C174_03;
	public String E7383_04;
}
class C523_01309 {
	public String E6350_01;
	public String E6353_02;
}
class EQN308 {
	public C523_01309 C523_01;
}
class Group26_MEA304 {
	public MEA305 MEA; 
	public EQN308 EQN; 
}
class Group25_TPL301 {
	public TPL302 TPL; 
	public List<Group26_MEA304> Group26_MEA; 
}
class C237_01312 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP311 {
	public C237_01312 C237_01;
	public String E7224_02;
}
class C286_02314 {
	public String E1050_01;
	public String E1159_02;
	public String E1131_03;
	public String E3055_04;
}
class SEQ313 {
	public String E1245_01;
	public C286_02314 C286_02;
}
class C502_02317 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03318 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA316 {
	public String E6311_01;
	public C502_02317 C502_02;
	public C174_03318 C174_03;
	public String E7383_04;
}
class C523_01320 {
	public String E6350_01;
	public String E6353_02;
}
class EQN319 {
	public C523_01320 C523_01;
}
class Group28_MEA315 {
	public MEA316 MEA; 
	public EQN319 EQN; 
}
class Group27_SGP310 {
	public SGP311 SGP; 
	public SEQ313 SEQ; 
	public List<Group28_MEA315> Group28_MEA; 
}
class C200_01323 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02324 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
	public String E5242_04;
	public String E5275_05;
	public String E1131_06;
	public String E3055_07;
	public String E5275_08;
	public String E1131_09;
	public String E3055_10;
}
class C528_03325 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04326 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC322 {
	public C200_01323 C200_01;
	public C203_02324 C203_02;
	public C528_03325 C528_03;
	public C554_04326 C554_04;
}
class C504_01328 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02329 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX327 {
	public C504_01328 C504_01;
	public C504_02329 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01331 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI330 {
	public C509_01331 C509_01;
	public String E5213_02;
}
class C523_01333 {
	public String E6350_01;
	public String E6353_02;
}
class EQN332 {
	public C523_01333 C523_01;
}
class C501_01335 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD334 {
	public C501_01335 C501_01;
}
class C516_01337 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA336 {
	public C516_01337 C516_01;
}
class C186_01339 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY338 {
	public C186_01339 C186_01;
}
class C517_02341 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03342 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04343 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC340 {
	public String E3227_01;
	public C517_02341 C517_02;
	public C519_03342 C519_03;
	public C553_04343 C553_04;
	public String E5479_05;
}
class Group29_TCC321 {
	public TCC322 TCC; 
	public CUX327 CUX; 
	public PRI330 PRI; 
	public EQN332 EQN; 
	public PCD334 PCD; 
	public List<MOA336> MOA; 
	public List<QTY338> QTY; 
	public List<LOC340> LOC; 
}
class C205_02346 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03347 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04348 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09349 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10350 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS345 {
	public String E8273_01;
	public C205_02346 C205_02;
	public C234_03347 C234_03;
	public C223_04348 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09349 C235_09;
	public C236_10350 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03352 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04353 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX351 {
	public String E4451_01;
	public String E4453_02;
	public C107_03352 C107_03;
	public C108_04353 C108_04;
	public String E3453_05;
}
class C056_02356 {
	public String E3413_01;
	public String E3412_02;
}
class CTA355 {
	public String E3139_01;
	public C056_02356 C056_02;
}
class C076_01358 {
	public String E3148_01;
	public String E3155_02;
}
class COM357 {
	public C076_01358 C076_01;
}
class Group31_CTA354 {
	public CTA355 CTA; 
	public List<COM357> COM; 
}
class C502_02361 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03362 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA360 {
	public String E6311_01;
	public C502_02361 C502_02;
	public C174_03362 C174_03;
	public String E7383_04;
}
class C523_01364 {
	public String E6350_01;
	public String E6353_02;
}
class EQN363 {
	public C523_01364 C523_01;
}
class Group32_MEA359 {
	public MEA360 MEA; 
	public EQN363 EQN; 
}
class C237_01367 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP366 {
	public C237_01367 C237_01;
	public String E7224_02;
}
class C502_02370 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03371 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA369 {
	public String E6311_01;
	public C502_02370 C502_02;
	public C174_03371 C174_03;
	public String E7383_04;
}
class C523_01373 {
	public String E6350_01;
	public String E6353_02;
}
class EQN372 {
	public C523_01373 C523_01;
}
class Group34_MEA368 {
	public MEA369 MEA; 
	public EQN372 EQN; 
}
class Group33_SGP365 {
	public SGP366 SGP; 
	public List<Group34_MEA368> Group34_MEA; 
}
class Group30_DGS344 {
	public DGS345 DGS; 
	public List<FTX351> FTX; 
	public List<Group31_CTA354> Group31_CTA; 
	public List<Group32_MEA359> Group32_MEA; 
	public List<Group33_SGP365> Group33_SGP; 
}
class Group18_GID226 {
	public GID227 GID; 
	public HAN231 HAN; 
	public TMP234 TMP; 
	public RNG236 RNG; 
	public TMD238 TMD; 
	public List<LOC240> LOC; 
	public List<MOA244> MOA; 
	public List<PIA246> PIA; 
	public List<FTX252> FTX; 
	public List<Group19_NAD255> Group19_NAD; 
	public List<GDS263> GDS; 
	public List<Group20_MEA265> Group20_MEA; 
	public List<Group21_DIM271> Group21_DIM; 
	public List<Group22_RFF276> Group22_RFF; 
	public List<Group23_PCI281> Group23_PCI; 
	public List<Group24_DOC295> Group24_DOC; 
	public List<Group25_TPL301> Group25_TPL; 
	public List<Group27_SGP310> Group27_SGP; 
	public List<Group29_TCC321> Group29_TCC; 
	public List<Group30_DGS344> Group30_DGS; 
}
class C237_02376 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03377 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD375 {
	public String E8053_01;
	public C237_02376 C237_02;
	public C224_03377 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C523_01379 {
	public String E6350_01;
	public String E6353_02;
}
class EQN378 {
	public C523_01379 C523_01;
}
class C219_01381 {
	public String E8335_01;
	public String E8334_02;
}
class TMD380 {
	public C219_01381 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C502_02383 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03384 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA382 {
	public String E6311_01;
	public C502_02383 C502_02;
	public C174_03384 C174_03;
	public String E7383_04;
}
class C211_02386 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM385 {
	public String E6145_01;
	public C211_02386 C211_02;
}
class C215_02388 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class SEL387 {
	public String E9308_01;
	public C215_02388 C215_02;
	public String E4517_03;
}
class C222_01390 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL389 {
	public C222_01390 C222_01;
}
class C524_01392 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02393 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN391 {
	public C524_01392 C524_01;
	public C218_02393 C218_02;
}
class C239_02395 {
	public String E6246_01;
	public String E6411_02;
}
class TMP394 {
	public String E6245_01;
	public C239_02395 C239_02;
}
class C107_03397 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04398 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX396 {
	public String E4451_01;
	public String E4453_02;
	public C107_03397 C107_03;
	public C108_04398 C108_04;
	public String E3453_05;
}
class C200_01401 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02402 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
	public String E5242_04;
	public String E5275_05;
	public String E1131_06;
	public String E3055_07;
	public String E5275_08;
	public String E1131_09;
	public String E3055_10;
}
class C528_03403 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04404 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC400 {
	public C200_01401 C200_01;
	public C203_02402 C203_02;
	public C528_03403 C528_03;
	public C554_04404 C554_04;
}
class C509_01406 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI405 {
	public C509_01406 C509_01;
	public String E5213_02;
}
class C523_01408 {
	public String E6350_01;
	public String E6353_02;
}
class EQN407 {
	public C523_01408 C523_01;
}
class C501_01410 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD409 {
	public C501_01410 C501_01;
}
class C516_01412 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA411 {
	public C516_01412 C516_01;
}
class C186_01414 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY413 {
	public C186_01414 C186_01;
}
class Group36_TCC399 {
	public TCC400 TCC; 
	public PRI405 PRI; 
	public EQN407 EQN; 
	public PCD409 PCD; 
	public List<MOA411> MOA; 
	public List<QTY413> QTY; 
}
class C082_02417 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03418 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04419 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05420 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD416 {
	public String E3035_01;
	public C082_02417 C082_02;
	public C058_03418 C058_03;
	public C080_04419 C080_04;
	public C059_05420 C059_05;
	public String E3164_06;
	public String E3229_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01422 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM421 {
	public C507_01422 C507_01;
}
class Group37_NAD415 {
	public NAD416 NAD; 
	public DTM421 DTM; 
}
class C237_02425 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class EQA424 {
	public String E8053_01;
	public C237_02425 C237_02;
}
class C523_01427 {
	public String E6350_01;
	public String E6353_02;
}
class EQN426 {
	public C523_01427 C523_01;
}
class Group38_EQA423 {
	public EQA424 EQA; 
	public EQN426 EQN; 
}
class Group35_EQD374 {
	public EQD375 EQD; 
	public EQN378 EQN; 
	public TMD380 TMD; 
	public List<MEA382> MEA; 
	public List<DIM385> DIM; 
	public List<SEL387> SEL; 
	public List<TPL389> TPL; 
	public HAN391 HAN; 
	public TMP394 TMP; 
	public List<FTX396> FTX; 
	public List<Group36_TCC399> Group36_TCC; 
	public List<Group37_NAD415> Group37_NAD; 
	public List<Group38_EQA423> Group38_EQA; 
}
class UNT428 {
	public String E0074_01;
	public String E0062_02;
}
class Group_UNH13 {
	public UNH14 UNH; 
	public BGM17 BGM; 
	public CTA20 CTA; 
	public List<COM22> COM; 
	public List<DTM24> DTM; 
	public List<TSR26> TSR; 
	public List<CUX31> CUX; 
	public List<MOA34> MOA; 
	public List<FTX36> FTX; 
	public List<CNT39> CNT; 
	public List<Group1_LOC41> Group1_LOC; 
	public List<Group2_TOD48> Group2_TOD; 
	public List<Group3_RFF55> Group3_RFF; 
	public List<Group4_GOR60> Group4_GOR; 
	public List<Group6_CPI83> Group6_CPI; 
	public List<Group7_TCC98> Group7_TCC; 
	public List<Group8_TDT124> Group8_TDT; 
	public List<Group11_NAD150> Group11_NAD; 
	public List<Group18_GID226> Group18_GID; 
	public List<Group35_EQD374> Group35_EQD; 
	public UNT428 UNT; 
}
class UNE429 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ430 {
	public String E0036_01;
	public String E0020_02;
}
