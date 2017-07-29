package cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMCS

public class EDI_CS_IFTMCS {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","COM","Group_UNH.DTM","Group_UNH.TSR","Group_UNH.CUX","MOA","Group_UNH.FTX","CNT","GDS","Group1_LOC","Group1_LOC.DTM","Group2_TOD","Group2_TOD.LOC","Group3_RFF","Group3_RFF.DTM","Group4_GOR","Group4_GOR.DTM","Group4_GOR.LOC","SEL","Group4_GOR.FTX","Group5_DOC","Group6_CPI","Group6_CPI.RFF","Group6_CPI.LOC","Group7_TCC","QTY","Group8_TDT","Group8_TDT.DTM","Group8_TDT.TSR","Group9_LOC","Group10_RFF","Group11_NAD","Group11_NAD.LOC","Group12_CTA","Group13_DOC","Group14_TCC","Group15_RFF","Group15_RFF.DTM","Group16_CPI","Group16_CPI.RFF","Group16_CPI.LOC","Group17_TSR","Group17_TSR.FTX","Group18_GID","Group18_GID.LOC","PIA","Group18_GID.FTX","Group18_GID.PCD","Group19_NAD","Group20_MEA","Group21_DIM","Group22_RFF","Group22_RFF.DTM","Group23_PCI","GIN","Group24_DOC","Group24_DOC.DTM","Group25_TPL","Group26_MEA","Group27_SGP","Group28_MEA","Group29_TCC","Group29_TCC.LOC","Group30_DGS","Group30_DGS.FTX","Group31_CTA","Group32_MEA","Group33_SGP","Group34_MEA","Group35_EQD","Group35_EQD.MEA","Group35_EQD.DIM","Group35_EQD.TPL","Group35_EQD.FTX","Group35_EQD.RFF","Group36_TCC","Group37_NAD","Group38_EQA","Group39_DGS","Group39_DGS.FTX","Group40_CTA"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE462 UNE; 
	public UNZ463 UNZ; 
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
	public String E6343_03;
	public String E4405_04;
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
	public String E4447_06;
}
class C270_0140 {
	public String E6069_01;
	public String E6066_02;
	public String E6411_03;
}
class CNT39 {
	public C270_0140 C270_01;
}
class C703_0142 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS41 {
	public C703_0142 C703_01;
}
class C517_0245 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0346 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0447 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC44 {
	public String E3227_01;
	public C517_0245 C517_02;
	public C519_0346 C519_03;
	public C553_0447 C553_04;
	public String E5479_05;
}
class C507_0149 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM48 {
	public C507_0149 C507_01;
}
class Group1_LOC43 {
	public LOC44 LOC; 
	public List<DTM48> DTM; 
}
class C100_0352 {
	public String E4053_01;
	public String E1131_02;
	public String E3055_03;
	public String E4052_04;
	public String E4052_05;
}
class TOD51 {
	public String E4055_01;
	public String E4215_02;
	public C100_0352 C100_03;
}
class C517_0254 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0355 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0456 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC53 {
	public String E3227_01;
	public C517_0254 C517_02;
	public C519_0355 C519_03;
	public C553_0456 C553_04;
	public String E5479_05;
}
class Group2_TOD50 {
	public TOD51 TOD; 
	public List<LOC53> LOC; 
}
class C506_0159 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF58 {
	public C506_0159 C506_01;
}
class C507_0161 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM60 {
	public C507_0161 C507_01;
}
class Group3_RFF57 {
	public RFF58 RFF; 
	public List<DTM60> DTM; 
}
class C232_0264 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0365 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0466 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0567 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class GOR63 {
	public String E8323_01;
	public C232_0264 C232_02;
	public C232_0365 C232_03;
	public C232_0466 C232_04;
	public C232_0567 C232_05;
}
class C507_0169 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM68 {
	public C507_0169 C507_01;
}
class C517_0271 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0372 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0473 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC70 {
	public String E3227_01;
	public C517_0271 C517_02;
	public C519_0372 C519_03;
	public C553_0473 C553_04;
	public String E5479_05;
}
class C215_0275 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class SEL74 {
	public String E9308_01;
	public C215_0275 C215_02;
}
class C107_0377 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0478 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX76 {
	public String E4451_01;
	public String E4453_02;
	public C107_0377 C107_03;
	public C108_0478 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C002_0181 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0282 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC80 {
	public C002_0181 C002_01;
	public C503_0282 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_0184 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM83 {
	public C507_0184 C507_01;
}
class Group5_DOC79 {
	public DOC80 DOC; 
	public DTM83 DTM; 
}
class Group4_GOR62 {
	public GOR63 GOR; 
	public List<DTM68> DTM; 
	public List<LOC70> LOC; 
	public List<SEL74> SEL; 
	public List<FTX76> FTX; 
	public List<Group5_DOC79> Group5_DOC; 
}
class C229_0187 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_0288 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI86 {
	public C229_0187 C229_01;
	public C231_0288 C231_02;
	public String E4237_03;
}
class C506_0190 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF89 {
	public C506_0190 C506_01;
}
class C504_0192 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0293 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX91 {
	public C504_0192 C504_01;
	public C504_0293 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_0295 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0396 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0497 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC94 {
	public String E3227_01;
	public C517_0295 C517_02;
	public C519_0396 C519_03;
	public C553_0497 C553_04;
	public String E5479_05;
}
class C516_0199 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
}
class MOA98 {
	public C516_0199 C516_01;
}
class Group6_CPI85 {
	public CPI86 CPI; 
	public List<RFF89> RFF; 
	public CUX91 CUX; 
	public List<LOC94> LOC; 
	public List<MOA98> MOA; 
}
class C200_01102 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02103 {
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
class C528_03104 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04105 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC101 {
	public C200_01102 C200_01;
	public C203_02103 C203_02;
	public C528_03104 C528_03;
	public C554_04105 C554_04;
}
class C517_02107 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03108 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04109 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC106 {
	public String E3227_01;
	public C517_02107 C517_02;
	public C519_03108 C519_03;
	public C553_04109 C553_04;
	public String E5479_05;
}
class C107_03111 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04112 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX110 {
	public String E4451_01;
	public String E4453_02;
	public C107_03111 C107_03;
	public C108_04112 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C504_01114 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02115 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX113 {
	public C504_01114 C504_01;
	public C504_02115 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01117 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI116 {
	public C509_01117 C509_01;
	public String E5213_02;
}
class C523_01119 {
	public String E6350_01;
	public String E6353_02;
}
class EQN118 {
	public C523_01119 C523_01;
}
class C501_01121 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD120 {
	public C501_01121 C501_01;
}
class C516_01123 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA122 {
	public C516_01123 C516_01;
}
class C186_01125 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY124 {
	public C186_01125 C186_01;
}
class Group7_TCC100 {
	public TCC101 TCC; 
	public LOC106 LOC; 
	public FTX110 FTX; 
	public CUX113 CUX; 
	public PRI116 PRI; 
	public EQN118 EQN; 
	public PCD120 PCD; 
	public List<MOA122> MOA; 
	public List<QTY124> QTY; 
}
class C220_03128 {
	public String E8067_01;
	public String E8066_02;
}
class C228_04129 {
	public String E8179_01;
	public String E8178_02;
}
class C040_05130 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_07131 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_08132 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT127 {
	public String E8051_01;
	public String E8028_02;
	public C220_03128 C220_03;
	public C228_04129 C228_04;
	public C040_05130 C040_05;
	public String E8101_06;
	public C401_07131 C401_07;
	public C222_08132 C222_08;
	public String E8281_09;
}
class C507_01134 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM133 {
	public C507_01134 C507_01;
}
class C536_01136 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02137 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03138 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04139 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR135 {
	public C536_01136 C536_01;
	public C233_02137 C233_02;
	public C537_03138 C537_03;
	public C703_04139 C703_04;
}
class C517_02142 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03143 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04144 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC141 {
	public String E3227_01;
	public C517_02142 C517_02;
	public C519_03143 C519_03;
	public C553_04144 C553_04;
	public String E5479_05;
}
class C507_01146 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM145 {
	public C507_01146 C507_01;
}
class Group9_LOC140 {
	public LOC141 LOC; 
	public DTM145 DTM; 
}
class C506_01149 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF148 {
	public C506_01149 C506_01;
}
class C507_01151 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM150 {
	public C507_01151 C507_01;
}
class Group10_RFF147 {
	public RFF148 RFF; 
	public DTM150 DTM; 
}
class Group8_TDT126 {
	public TDT127 TDT; 
	public List<DTM133> DTM; 
	public List<TSR135> TSR; 
	public List<Group9_LOC140> Group9_LOC; 
	public List<Group10_RFF147> Group10_RFF; 
}
class C082_02154 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03155 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04156 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05157 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07158 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD153 {
	public String E3035_01;
	public C082_02154 C082_02;
	public C058_03155 C058_03;
	public C080_04156 C080_04;
	public C059_05157 C059_05;
	public String E3164_06;
	public C819_07158 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C517_02160 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03161 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04162 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC159 {
	public String E3227_01;
	public C517_02160 C517_02;
	public C519_03161 C519_03;
	public C553_04162 C553_04;
	public String E5479_05;
}
class C516_01164 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA163 {
	public C516_01164 C516_01;
}
class C056_02167 {
	public String E3413_01;
	public String E3412_02;
}
class CTA166 {
	public String E3139_01;
	public C056_02167 C056_02;
}
class C076_01169 {
	public String E3148_01;
	public String E3155_02;
}
class COM168 {
	public C076_01169 C076_01;
}
class Group12_CTA165 {
	public CTA166 CTA; 
	public List<COM168> COM; 
}
class C002_01172 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02173 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC171 {
	public C002_01172 C002_01;
	public C503_02173 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01175 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM174 {
	public C507_01175 C507_01;
}
class Group13_DOC170 {
	public DOC171 DOC; 
	public DTM174 DTM; 
}
class C200_01178 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02179 {
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
class C528_03180 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04181 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC177 {
	public C200_01178 C200_01;
	public C203_02179 C203_02;
	public C528_03180 C528_03;
	public C554_04181 C554_04;
}
class C504_01183 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02184 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX182 {
	public C504_01183 C504_01;
	public C504_02184 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01186 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI185 {
	public C509_01186 C509_01;
	public String E5213_02;
}
class C523_01188 {
	public String E6350_01;
	public String E6353_02;
}
class EQN187 {
	public C523_01188 C523_01;
}
class C501_01190 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD189 {
	public C501_01190 C501_01;
}
class C516_01192 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA191 {
	public C516_01192 C516_01;
}
class C186_01194 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY193 {
	public C186_01194 C186_01;
}
class Group14_TCC176 {
	public TCC177 TCC; 
	public CUX182 CUX; 
	public PRI185 PRI; 
	public EQN187 EQN; 
	public PCD189 PCD; 
	public List<MOA191> MOA; 
	public List<QTY193> QTY; 
}
class C506_01197 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF196 {
	public C506_01197 C506_01;
}
class C507_01199 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM198 {
	public C507_01199 C507_01;
}
class Group15_RFF195 {
	public RFF196 RFF; 
	public List<DTM198> DTM; 
}
class C229_01202 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_02203 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI201 {
	public C229_01202 C229_01;
	public C231_02203 C231_02;
	public String E4237_03;
}
class C506_01205 {
	public String E1153_01;
	public String E1154_02;
}
class RFF204 {
	public C506_01205 C506_01;
}
class C504_01207 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02208 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX206 {
	public C504_01207 C504_01;
	public C504_02208 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_02210 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03211 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04212 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC209 {
	public String E3227_01;
	public C517_02210 C517_02;
	public C519_03211 C519_03;
	public C553_04212 C553_04;
	public String E5479_05;
}
class C516_01214 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
}
class MOA213 {
	public C516_01214 C516_01;
}
class Group16_CPI200 {
	public CPI201 CPI; 
	public List<RFF204> RFF; 
	public CUX206 CUX; 
	public List<LOC209> LOC; 
	public List<MOA213> MOA; 
}
class C536_01217 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02218 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03219 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04220 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR216 {
	public C536_01217 C536_01;
	public C233_02218 C233_02;
	public C537_03219 C537_03;
	public C703_04220 C703_04;
}
class C506_01222 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF221 {
	public C506_01222 C506_01;
}
class C517_02224 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03225 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04226 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC223 {
	public String E3227_01;
	public C517_02224 C517_02;
	public C519_03225 C519_03;
	public C553_04226 C553_04;
	public String E5479_05;
}
class C222_01228 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL227 {
	public C222_01228 C222_01;
}
class C107_03230 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04231 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX229 {
	public String E4451_01;
	public String E4453_02;
	public C107_03230 C107_03;
	public C108_04231 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class Group17_TSR215 {
	public TSR216 TSR; 
	public RFF221 RFF; 
	public LOC223 LOC; 
	public TPL227 TPL; 
	public List<FTX229> FTX; 
}
class Group11_NAD152 {
	public NAD153 NAD; 
	public List<LOC159> LOC; 
	public List<MOA163> MOA; 
	public List<Group12_CTA165> Group12_CTA; 
	public List<Group13_DOC170> Group13_DOC; 
	public List<Group14_TCC176> Group14_TCC; 
	public List<Group15_RFF195> Group15_RFF; 
	public List<Group16_CPI200> Group16_CPI; 
	public List<Group17_TSR215> Group17_TSR; 
}
class C213_02234 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_03235 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_04236 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_05237 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_06238 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class GID233 {
	public String E1496_01;
	public C213_02234 C213_02;
	public C213_03235 C213_03;
	public C213_04236 C213_04;
	public C213_05237 C213_05;
	public C213_06238 C213_06;
}
class C524_01240 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02241 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN239 {
	public C524_01240 C524_01;
	public C218_02241 C218_02;
}
class C239_02243 {
	public String E6246_01;
	public String E6411_02;
}
class TMP242 {
	public String E6245_01;
	public C239_02243 C239_02;
}
class C280_02245 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG244 {
	public String E6167_01;
	public C280_02245 C280_02;
}
class C219_01247 {
	public String E8335_01;
	public String E8334_02;
}
class TMD246 {
	public C219_01247 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02249 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03250 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04251 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC248 {
	public String E3227_01;
	public C517_02249 C517_02;
	public C519_03250 C519_03;
	public C553_04251 C553_04;
	public String E5479_05;
}
class C516_01253 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA252 {
	public C516_01253 C516_01;
}
class C212_02255 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_03256 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_04257 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_05258 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_06259 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class PIA254 {
	public String E4347_01;
	public C212_02255 C212_02;
	public C212_03256 C212_03;
	public C212_04257 C212_04;
	public C212_05258 C212_05;
	public C212_06259 C212_06;
}
class C107_03261 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04262 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX260 {
	public String E4451_01;
	public String E4453_02;
	public C107_03261 C107_03;
	public C108_04262 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C501_01264 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD263 {
	public C501_01264 C501_01;
}
class C082_02267 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03268 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04269 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05270 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07271 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD266 {
	public String E3035_01;
	public C082_02267 C082_02;
	public C058_03268 C058_03;
	public C080_04269 C080_04;
	public C059_05270 C059_05;
	public String E3164_06;
	public C819_07271 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01273 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM272 {
	public C507_01273 C507_01;
}
class Group19_NAD265 {
	public NAD266 NAD; 
	public DTM272 DTM; 
}
class C703_01275 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS274 {
	public C703_01275 C703_01;
}
class C502_02278 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03279 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA277 {
	public String E6311_01;
	public C502_02278 C502_02;
	public C174_03279 C174_03;
	public String E7383_04;
}
class C523_01281 {
	public String E6350_01;
	public String E6353_02;
}
class EQN280 {
	public C523_01281 C523_01;
}
class Group20_MEA276 {
	public MEA277 MEA; 
	public EQN280 EQN; 
}
class C211_02284 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM283 {
	public String E6145_01;
	public C211_02284 C211_02;
}
class C523_01286 {
	public String E6350_01;
	public String E6353_02;
}
class EQN285 {
	public C523_01286 C523_01;
}
class Group21_DIM282 {
	public DIM283 DIM; 
	public EQN285 EQN; 
}
class C506_01289 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF288 {
	public C506_01289 C506_01;
}
class C507_01291 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM290 {
	public C507_01291 C507_01;
}
class Group22_RFF287 {
	public RFF288 RFF; 
	public List<DTM290> DTM; 
}
class C210_02294 {
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
class C827_04295 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI293 {
	public String E4233_01;
	public C210_02294 C210_02;
	public String E8275_03;
	public C827_04295 C827_04;
}
class C506_01297 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF296 {
	public C506_01297 C506_01;
}
class C507_01299 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM298 {
	public C507_01299 C507_01;
}
class C208_02301 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03302 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04303 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05304 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06305 {
	public String E7402_01;
	public String E7402_02;
}
class GIN300 {
	public String E7405_01;
	public C208_02301 C208_02;
	public C208_03302 C208_03;
	public C208_04303 C208_04;
	public C208_05304 C208_05;
	public C208_06305 C208_06;
}
class Group23_PCI292 {
	public PCI293 PCI; 
	public RFF296 RFF; 
	public DTM298 DTM; 
	public List<GIN300> GIN; 
}
class C002_01308 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02309 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC307 {
	public C002_01308 C002_01;
	public C503_02309 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01311 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM310 {
	public C507_01311 C507_01;
}
class Group24_DOC306 {
	public DOC307 DOC; 
	public List<DTM310> DTM; 
}
class C222_01314 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL313 {
	public C222_01314 C222_01;
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
class Group26_MEA315 {
	public MEA316 MEA; 
	public EQN319 EQN; 
}
class Group25_TPL312 {
	public TPL313 TPL; 
	public List<Group26_MEA315> Group26_MEA; 
}
class C237_01323 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP322 {
	public C237_01323 C237_01;
	public String E7224_02;
}
class C286_02325 {
	public String E1050_01;
	public String E1159_02;
	public String E1131_03;
	public String E3055_04;
}
class SEQ324 {
	public String E1245_01;
	public C286_02325 C286_02;
}
class C502_02328 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03329 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA327 {
	public String E6311_01;
	public C502_02328 C502_02;
	public C174_03329 C174_03;
	public String E7383_04;
}
class C523_01331 {
	public String E6350_01;
	public String E6353_02;
}
class EQN330 {
	public C523_01331 C523_01;
}
class Group28_MEA326 {
	public MEA327 MEA; 
	public EQN330 EQN; 
}
class Group27_SGP321 {
	public SGP322 SGP; 
	public SEQ324 SEQ; 
	public List<Group28_MEA326> Group28_MEA; 
}
class C200_01334 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02335 {
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
class C528_03336 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04337 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC333 {
	public C200_01334 C200_01;
	public C203_02335 C203_02;
	public C528_03336 C528_03;
	public C554_04337 C554_04;
}
class C504_01339 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02340 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX338 {
	public C504_01339 C504_01;
	public C504_02340 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01342 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI341 {
	public C509_01342 C509_01;
	public String E5213_02;
}
class C523_01344 {
	public String E6350_01;
	public String E6353_02;
}
class EQN343 {
	public C523_01344 C523_01;
}
class C501_01346 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD345 {
	public C501_01346 C501_01;
}
class C516_01348 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA347 {
	public C516_01348 C516_01;
}
class C186_01350 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY349 {
	public C186_01350 C186_01;
}
class C517_02352 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03353 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04354 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC351 {
	public String E3227_01;
	public C517_02352 C517_02;
	public C519_03353 C519_03;
	public C553_04354 C553_04;
	public String E5479_05;
}
class Group29_TCC332 {
	public TCC333 TCC; 
	public CUX338 CUX; 
	public PRI341 PRI; 
	public EQN343 EQN; 
	public PCD345 PCD; 
	public List<MOA347> MOA; 
	public List<QTY349> QTY; 
	public List<LOC351> LOC; 
}
class C205_02357 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03358 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04359 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09360 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10361 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS356 {
	public String E8273_01;
	public C205_02357 C205_02;
	public C234_03358 C234_03;
	public C223_04359 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09360 C235_09;
	public C236_10361 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03363 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04364 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX362 {
	public String E4451_01;
	public String E4453_02;
	public C107_03363 C107_03;
	public C108_04364 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02367 {
	public String E3413_01;
	public String E3412_02;
}
class CTA366 {
	public String E3139_01;
	public C056_02367 C056_02;
}
class C076_01369 {
	public String E3148_01;
	public String E3155_02;
}
class COM368 {
	public C076_01369 C076_01;
}
class Group31_CTA365 {
	public CTA366 CTA; 
	public List<COM368> COM; 
}
class C502_02372 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03373 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA371 {
	public String E6311_01;
	public C502_02372 C502_02;
	public C174_03373 C174_03;
	public String E7383_04;
}
class C523_01375 {
	public String E6350_01;
	public String E6353_02;
}
class EQN374 {
	public C523_01375 C523_01;
}
class Group32_MEA370 {
	public MEA371 MEA; 
	public EQN374 EQN; 
}
class C237_01378 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP377 {
	public C237_01378 C237_01;
	public String E7224_02;
}
class C502_02381 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03382 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA380 {
	public String E6311_01;
	public C502_02381 C502_02;
	public C174_03382 C174_03;
	public String E7383_04;
}
class C523_01384 {
	public String E6350_01;
	public String E6353_02;
}
class EQN383 {
	public C523_01384 C523_01;
}
class Group34_MEA379 {
	public MEA380 MEA; 
	public EQN383 EQN; 
}
class Group33_SGP376 {
	public SGP377 SGP; 
	public List<Group34_MEA379> Group34_MEA; 
}
class Group30_DGS355 {
	public DGS356 DGS; 
	public List<FTX362> FTX; 
	public List<Group31_CTA365> Group31_CTA; 
	public List<Group32_MEA370> Group32_MEA; 
	public List<Group33_SGP376> Group33_SGP; 
}
class Group18_GID232 {
	public GID233 GID; 
	public HAN239 HAN; 
	public TMP242 TMP; 
	public RNG244 RNG; 
	public TMD246 TMD; 
	public List<LOC248> LOC; 
	public List<MOA252> MOA; 
	public List<PIA254> PIA; 
	public List<FTX260> FTX; 
	public List<PCD263> PCD; 
	public List<Group19_NAD265> Group19_NAD; 
	public List<GDS274> GDS; 
	public List<Group20_MEA276> Group20_MEA; 
	public List<Group21_DIM282> Group21_DIM; 
	public List<Group22_RFF287> Group22_RFF; 
	public List<Group23_PCI292> Group23_PCI; 
	public List<Group24_DOC306> Group24_DOC; 
	public List<Group25_TPL312> Group25_TPL; 
	public List<Group27_SGP321> Group27_SGP; 
	public List<Group29_TCC332> Group29_TCC; 
	public List<Group30_DGS355> Group30_DGS; 
}
class C237_02387 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03388 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD386 {
	public String E8053_01;
	public C237_02387 C237_02;
	public C224_03388 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C523_01390 {
	public String E6350_01;
	public String E6353_02;
}
class EQN389 {
	public C523_01390 C523_01;
}
class C219_01392 {
	public String E8335_01;
	public String E8334_02;
}
class TMD391 {
	public C219_01392 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C502_02394 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03395 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA393 {
	public String E6311_01;
	public C502_02394 C502_02;
	public C174_03395 C174_03;
	public String E7383_04;
}
class C211_02397 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM396 {
	public String E6145_01;
	public C211_02397 C211_02;
}
class C215_02399 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class SEL398 {
	public String E9308_01;
	public C215_02399 C215_02;
}
class C222_01401 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL400 {
	public C222_01401 C222_01;
}
class C524_01403 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02404 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN402 {
	public C524_01403 C524_01;
	public C218_02404 C218_02;
}
class C239_02406 {
	public String E6246_01;
	public String E6411_02;
}
class TMP405 {
	public String E6245_01;
	public C239_02406 C239_02;
}
class C107_03408 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04409 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX407 {
	public String E4451_01;
	public String E4453_02;
	public C107_03408 C107_03;
	public C108_04409 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C506_01411 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF410 {
	public C506_01411 C506_01;
}
class C200_01414 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02415 {
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
class C528_03416 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04417 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC413 {
	public C200_01414 C200_01;
	public C203_02415 C203_02;
	public C528_03416 C528_03;
	public C554_04417 C554_04;
}
class C504_01419 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02420 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX418 {
	public C504_01419 C504_01;
	public C504_02420 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01422 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI421 {
	public C509_01422 C509_01;
	public String E5213_02;
}
class C523_01424 {
	public String E6350_01;
	public String E6353_02;
}
class EQN423 {
	public C523_01424 C523_01;
}
class C501_01426 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD425 {
	public C501_01426 C501_01;
}
class C516_01428 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA427 {
	public C516_01428 C516_01;
}
class C186_01430 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY429 {
	public C186_01430 C186_01;
}
class Group36_TCC412 {
	public TCC413 TCC; 
	public CUX418 CUX; 
	public PRI421 PRI; 
	public EQN423 EQN; 
	public PCD425 PCD; 
	public List<MOA427> MOA; 
	public List<QTY429> QTY; 
}
class C082_02433 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03434 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04435 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05436 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07437 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD432 {
	public String E3035_01;
	public C082_02433 C082_02;
	public C058_03434 C058_03;
	public C080_04435 C080_04;
	public C059_05436 C059_05;
	public String E3164_06;
	public C819_07437 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01439 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM438 {
	public C507_01439 C507_01;
}
class Group37_NAD431 {
	public NAD432 NAD; 
	public DTM438 DTM; 
}
class C237_02442 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class EQA441 {
	public String E8053_01;
	public C237_02442 C237_02;
}
class C523_01444 {
	public String E6350_01;
	public String E6353_02;
}
class EQN443 {
	public C523_01444 C523_01;
}
class Group38_EQA440 {
	public EQA441 EQA; 
	public EQN443 EQN; 
}
class C205_02447 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03448 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04449 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09450 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10451 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS446 {
	public String E8273_01;
	public C205_02447 C205_02;
	public C234_03448 C234_03;
	public C223_04449 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09450 C235_09;
	public C236_10451 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03453 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04454 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX452 {
	public String E4451_01;
	public String E4453_02;
	public C107_03453 C107_03;
	public C108_04454 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02457 {
	public String E3413_01;
	public String E3412_02;
}
class CTA456 {
	public String E3139_01;
	public C056_02457 C056_02;
}
class C076_01459 {
	public String E3148_01;
	public String E3155_02;
}
class COM458 {
	public C076_01459 C076_01;
}
class Group40_CTA455 {
	public CTA456 CTA; 
	public List<COM458> COM; 
}
class Group39_DGS445 {
	public DGS446 DGS; 
	public List<FTX452> FTX; 
	public List<Group40_CTA455> Group40_CTA; 
}
class Group35_EQD385 {
	public EQD386 EQD; 
	public EQN389 EQN; 
	public TMD391 TMD; 
	public List<MEA393> MEA; 
	public List<DIM396> DIM; 
	public List<SEL398> SEL; 
	public List<TPL400> TPL; 
	public HAN402 HAN; 
	public TMP405 TMP; 
	public List<FTX407> FTX; 
	public List<RFF410> RFF; 
	public List<Group36_TCC412> Group36_TCC; 
	public List<Group37_NAD431> Group37_NAD; 
	public List<Group38_EQA440> Group38_EQA; 
	public List<Group39_DGS445> Group39_DGS; 
}
class UNT460 {
	public String E0074_01;
	public String E0062_02;
}
class UNE461 {
	public String E0060_01;
	public String E0048_02;
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
	public List<GDS41> GDS; 
	public List<Group1_LOC43> Group1_LOC; 
	public List<Group2_TOD50> Group2_TOD; 
	public List<Group3_RFF57> Group3_RFF; 
	public List<Group4_GOR62> Group4_GOR; 
	public List<Group6_CPI85> Group6_CPI; 
	public List<Group7_TCC100> Group7_TCC; 
	public List<Group8_TDT126> Group8_TDT; 
	public List<Group11_NAD152> Group11_NAD; 
	public List<Group18_GID232> Group18_GID; 
	public List<Group35_EQD385> Group35_EQD; 
	public UNT460 UNT; 
	public UNE461 UNE; 
}
class UNE462 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ463 {
	public String E0036_01;
	public String E0020_02;
}
