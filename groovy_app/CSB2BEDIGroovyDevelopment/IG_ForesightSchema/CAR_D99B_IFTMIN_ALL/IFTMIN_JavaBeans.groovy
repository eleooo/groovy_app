package cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN_ALL

public class EDI_ALL_IFTMIN {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","COM","Group_UNH.DTM","Group_UNH.TSR","Group_UNH.CUX","MOA","Group_UNH.FTX","CNT","Group_UNH.DOC","GDS","Group1_LOC","Group1_LOC.DTM","Group2_TOD","Group2_TOD.LOC","Group3_RFF","Group3_RFF.DTM","Group4_GOR","Group4_GOR.DTM","Group4_GOR.LOC","SEL","Group4_GOR.FTX","Group5_DOC","Group6_CPI","Group6_CPI.RFF","Group6_CPI.LOC","Group7_TCC","QTY","Group8_TDT","Group8_TDT.DTM","Group8_TDT.TSR","Group9_LOC","Group9_LOC.DTM","Group10_RFF","Group11_NAD","Group11_NAD.LOC","Group12_CTA","Group13_DOC","Group14_TCC","Group15_RFF","Group15_RFF.DTM","Group16_CPI","Group16_CPI.RFF","Group16_CPI.LOC","Group17_TSR","Group17_TSR.FTX","Group18_GID","Group18_GID.HAN","Group18_GID.LOC","PIA","Group18_GID.FTX","Group18_GID.PCD","Group19_NAD","Group19_NAD.LOC","Group20_MEA","Group21_DIM","Group22_RFF","Group22_RFF.DTM","Group23_PCI","GIN","Group24_DOC","Group24_DOC.DTM","Group25_GOR","Group25_GOR.DTM","Group25_GOR.LOC","Group25_GOR.FTX","Group26_DOC","Group27_TPL","Group28_MEA","Group29_SGP","Group30_MEA","Group31_TCC","Group31_TCC.LOC","Group32_DGS","Group32_DGS.FTX","Group33_CTA","Group34_MEA","Group35_SGP","Group36_MEA","Group37_EQD","Group37_EQD.MEA","Group37_EQD.DIM","Group37_EQD.TPL","Group37_EQD.FTX","Group37_EQD.RFF","Group38_TCC","Group39_NAD","Group40_CTA","Group41_EQA","Group42_DGS","Group42_DGS.FTX","Group43_CTA"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE494 UNE; 
	public UNZ495 UNZ; 
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
	public String E0110_06;
	public String E0113_07;
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
}
class C233_0228 {
	public String E7273_01;
}
class TSR26 {
	public C536_0127 C536_01;
	public C233_0228 C233_02;
}
class C504_0130 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0231 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX29 {
	public C504_0130 C504_01;
	public C504_0231 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C516_0133 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA32 {
	public C516_0133 C516_01;
}
class C107_0335 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0436 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX34 {
	public String E4451_01;
	public String E4453_02;
	public C107_0335 C107_03;
	public C108_0436 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C270_0138 {
	public String E6069_01;
	public String E6066_02;
	public String E6411_03;
}
class CNT37 {
	public C270_0138 C270_01;
}
class C002_0140 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0241 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC39 {
	public C002_0140 C002_01;
	public C503_0241 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C703_0143 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS42 {
	public C703_0143 C703_01;
}
class C517_0246 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0347 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
}
class C553_0448 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
}
class LOC45 {
	public String E3227_01;
	public C517_0246 C517_02;
	public C519_0347 C519_03;
	public C553_0448 C553_04;
}
class C507_0150 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM49 {
	public C507_0150 C507_01;
}
class Group1_LOC44 {
	public LOC45 LOC; 
	public List<DTM49> DTM; 
}
class C100_0353 {
	public String E4053_01;
	public String E1131_02;
	public String E3055_03;
	public String E4052_04;
	public String E4052_05;
}
class TOD52 {
	public String E4055_01;
	public String E4215_02;
	public C100_0353 C100_03;
}
class C517_0255 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0356 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0457 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC54 {
	public String E3227_01;
	public C517_0255 C517_02;
	public C519_0356 C519_03;
	public C553_0457 C553_04;
	public String E5479_05;
}
class Group2_TOD51 {
	public TOD52 TOD; 
	public List<LOC54> LOC; 
}
class C506_0160 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF59 {
	public C506_0160 C506_01;
}
class C507_0162 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM61 {
	public C507_0162 C507_01;
}
class Group3_RFF58 {
	public RFF59 RFF; 
	public List<DTM61> DTM; 
}
class C232_0265 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0366 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0467 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0568 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class GOR64 {
	public String E8323_01;
	public C232_0265 C232_02;
	public C232_0366 C232_03;
	public C232_0467 C232_04;
	public C232_0568 C232_05;
}
class C507_0170 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM69 {
	public C507_0170 C507_01;
}
class C517_0272 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0373 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0474 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC71 {
	public String E3227_01;
	public C517_0272 C517_02;
	public C519_0373 C519_03;
	public C553_0474 C553_04;
	public String E5479_05;
}
class C215_0276 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_0477 {
	public String E7402_01;
	public String E7402_02;
}
class SEL75 {
	public String E9308_01;
	public C215_0276 C215_02;
	public String E4517_03;
	public C208_0477 C208_04;
}
class C107_0379 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0480 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX78 {
	public String E4451_01;
	public String E4453_02;
	public C107_0379 C107_03;
	public C108_0480 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C002_0183 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0284 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC82 {
	public C002_0183 C002_01;
	public C503_0284 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_0186 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM85 {
	public C507_0186 C507_01;
}
class Group5_DOC81 {
	public DOC82 DOC; 
	public DTM85 DTM; 
}
class Group4_GOR63 {
	public GOR64 GOR; 
	public List<DTM69> DTM; 
	public List<LOC71> LOC; 
	public List<SEL75> SEL; 
	public List<FTX78> FTX; 
	public List<Group5_DOC81> Group5_DOC; 
}
class C229_0189 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_0290 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI88 {
	public C229_0189 C229_01;
	public C231_0290 C231_02;
	public String E4237_03;
}
class C506_0192 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF91 {
	public C506_0192 C506_01;
}
class C504_0194 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0295 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX93 {
	public C504_0194 C504_01;
	public C504_0295 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_0297 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0398 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0499 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC96 {
	public String E3227_01;
	public C517_0297 C517_02;
	public C519_0398 C519_03;
	public C553_0499 C553_04;
	public String E5479_05;
}
class C516_01101 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA100 {
	public C516_01101 C516_01;
}
class Group6_CPI87 {
	public CPI88 CPI; 
	public List<RFF91> RFF; 
	public CUX93 CUX; 
	public List<LOC96> LOC; 
	public List<MOA100> MOA; 
}
class C200_01104 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02105 {
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
class C528_03106 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04107 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC103 {
	public C200_01104 C200_01;
	public C203_02105 C203_02;
	public C528_03106 C528_03;
	public C554_04107 C554_04;
}
class C517_02109 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03110 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04111 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC108 {
	public String E3227_01;
	public C517_02109 C517_02;
	public C519_03110 C519_03;
	public C553_04111 C553_04;
	public String E5479_05;
}
class C107_03113 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04114 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX112 {
	public String E4451_01;
	public String E4453_02;
	public C107_03113 C107_03;
	public C108_04114 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C504_01116 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02117 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX115 {
	public C504_01116 C504_01;
	public C504_02117 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01119 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI118 {
	public C509_01119 C509_01;
	public String E5213_02;
}
class C523_01121 {
	public String E6350_01;
	public String E6353_02;
}
class EQN120 {
	public C523_01121 C523_01;
}
class C501_01123 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD122 {
	public C501_01123 C501_01;
}
class C516_01125 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA124 {
	public C516_01125 C516_01;
}
class C186_01127 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY126 {
	public C186_01127 C186_01;
}
class Group7_TCC102 {
	public TCC103 TCC; 
	public LOC108 LOC; 
	public FTX112 FTX; 
	public CUX115 CUX; 
	public PRI118 PRI; 
	public EQN120 EQN; 
	public PCD122 PCD; 
	public List<MOA124> MOA; 
	public List<QTY126> QTY; 
}
class C220_03130 {
	public String E8067_01;
	public String E8066_02;
}
class C228_04131 {
	public String E8179_01;
	public String E8178_02;
}
class C040_05132 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_07133 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_08134 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT129 {
	public String E8051_01;
	public String E8028_02;
	public C220_03130 C220_03;
	public C228_04131 C228_04;
	public C040_05132 C040_05;
	public String E8101_06;
	public C401_07133 C401_07;
	public C222_08134 C222_08;
	public String E8281_09;
}
class C507_01136 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM135 {
	public C507_01136 C507_01;
}
class C536_01138 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02139 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03140 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04141 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR137 {
	public C536_01138 C536_01;
	public C233_02139 C233_02;
	public C537_03140 C537_03;
	public C703_04141 C703_04;
}
class C517_02144 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03145 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04146 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC143 {
	public String E3227_01;
	public C517_02144 C517_02;
	public C519_03145 C519_03;
	public C553_04146 C553_04;
	public String E5479_05;
}
class C507_01148 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM147 {
	public C507_01148 C507_01;
}
class Group9_LOC142 {
	public LOC143 LOC; 
	public List<DTM147> DTM; 
}
class C506_01151 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF150 {
	public C506_01151 C506_01;
}
class C507_01153 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM152 {
	public C507_01153 C507_01;
}
class Group10_RFF149 {
	public RFF150 RFF; 
	public DTM152 DTM; 
}
class Group8_TDT128 {
	public TDT129 TDT; 
	public List<DTM135> DTM; 
	public List<TSR137> TSR; 
	public List<Group9_LOC142> Group9_LOC; 
	public List<Group10_RFF149> Group10_RFF; 
}
class C082_02156 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03157 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04158 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05159 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07160 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD155 {
	public String E3035_01;
	public C082_02156 C082_02;
	public C058_03157 C058_03;
	public C080_04158 C080_04;
	public C059_05159 C059_05;
	public String E3164_06;
	public C819_07160 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C517_02162 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03163 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04164 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC161 {
	public String E3227_01;
	public C517_02162 C517_02;
	public C519_03163 C519_03;
	public C553_04164 C553_04;
	public String E5479_05;
}
class C516_01166 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA165 {
	public C516_01166 C516_01;
}
class C056_02169 {
	public String E3413_01;
	public String E3412_02;
}
class CTA168 {
	public String E3139_01;
	public C056_02169 C056_02;
}
class C076_01171 {
	public String E3148_01;
	public String E3155_02;
}
class COM170 {
	public C076_01171 C076_01;
}
class Group12_CTA167 {
	public CTA168 CTA; 
	public List<COM170> COM; 
}
class C002_01174 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02175 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC173 {
	public C002_01174 C002_01;
	public C503_02175 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01177 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM176 {
	public C507_01177 C507_01;
}
class Group13_DOC172 {
	public DOC173 DOC; 
	public DTM176 DTM; 
}
class C200_01180 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02181 {
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
class C528_03182 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04183 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC179 {
	public C200_01180 C200_01;
	public C203_02181 C203_02;
	public C528_03182 C528_03;
	public C554_04183 C554_04;
}
class C504_01185 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02186 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX184 {
	public C504_01185 C504_01;
	public C504_02186 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01188 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI187 {
	public C509_01188 C509_01;
	public String E5213_02;
}
class C523_01190 {
	public String E6350_01;
	public String E6353_02;
}
class EQN189 {
	public C523_01190 C523_01;
}
class C501_01192 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD191 {
	public C501_01192 C501_01;
}
class C516_01194 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA193 {
	public C516_01194 C516_01;
}
class C186_01196 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY195 {
	public C186_01196 C186_01;
}
class Group14_TCC178 {
	public TCC179 TCC; 
	public CUX184 CUX; 
	public PRI187 PRI; 
	public EQN189 EQN; 
	public PCD191 PCD; 
	public List<MOA193> MOA; 
	public List<QTY195> QTY; 
}
class C506_01199 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF198 {
	public C506_01199 C506_01;
}
class C507_01201 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM200 {
	public C507_01201 C507_01;
}
class Group15_RFF197 {
	public RFF198 RFF; 
	public List<DTM200> DTM; 
}
class C229_01204 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_02205 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI203 {
	public C229_01204 C229_01;
	public C231_02205 C231_02;
	public String E4237_03;
}
class C506_01207 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF206 {
	public C506_01207 C506_01;
}
class C504_01209 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02210 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX208 {
	public C504_01209 C504_01;
	public C504_02210 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_02212 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03213 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04214 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC211 {
	public String E3227_01;
	public C517_02212 C517_02;
	public C519_03213 C519_03;
	public C553_04214 C553_04;
	public String E5479_05;
}
class C516_01216 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA215 {
	public C516_01216 C516_01;
}
class Group16_CPI202 {
	public CPI203 CPI; 
	public List<RFF206> RFF; 
	public CUX208 CUX; 
	public List<LOC211> LOC; 
	public List<MOA215> MOA; 
}
class C536_01219 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02220 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03221 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04222 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR218 {
	public C536_01219 C536_01;
	public C233_02220 C233_02;
	public C537_03221 C537_03;
	public C703_04222 C703_04;
}
class C506_01224 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF223 {
	public C506_01224 C506_01;
}
class C517_02226 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03227 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04228 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC225 {
	public String E3227_01;
	public C517_02226 C517_02;
	public C519_03227 C519_03;
	public C553_04228 C553_04;
	public String E5479_05;
}
class C222_01230 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL229 {
	public C222_01230 C222_01;
}
class C107_03232 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04233 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX231 {
	public String E4451_01;
	public String E4453_02;
	public C107_03232 C107_03;
	public C108_04233 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class Group17_TSR217 {
	public TSR218 TSR; 
	public RFF223 RFF; 
	public LOC225 LOC; 
	public TPL229 TPL; 
	public List<FTX231> FTX; 
}
class Group11_NAD154 {
	public NAD155 NAD; 
	public List<LOC161> LOC; 
	public List<MOA165> MOA; 
	public List<Group12_CTA167> Group12_CTA; 
	public List<Group13_DOC172> Group13_DOC; 
	public List<Group14_TCC178> Group14_TCC; 
	public List<Group15_RFF197> Group15_RFF; 
	public List<Group16_CPI202> Group16_CPI; 
	public List<Group17_TSR217> Group17_TSR; 
}
class C213_02236 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_03237 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_04238 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_05239 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_06240 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class GID235 {
	public String E1496_01;
	public C213_02236 C213_02;
	public C213_03237 C213_03;
	public C213_04238 C213_04;
	public C213_05239 C213_05;
	public C213_06240 C213_06;
}
class C524_01242 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02243 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN241 {
	public C524_01242 C524_01;
	public C218_02243 C218_02;
}
class C239_02245 {
	public String E6246_01;
	public String E6411_02;
}
class TMP244 {
	public String E6245_01;
	public C239_02245 C239_02;
}
class C280_02247 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG246 {
	public String E6167_01;
	public C280_02247 C280_02;
}
class C219_01249 {
	public String E8335_01;
	public String E8334_02;
}
class TMD248 {
	public C219_01249 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02251 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03252 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04253 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC250 {
	public String E3227_01;
	public C517_02251 C517_02;
	public C519_03252 C519_03;
	public C553_04253 C553_04;
	public String E5479_05;
}
class C516_01255 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA254 {
	public C516_01255 C516_01;
}
class C212_02257 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_03258 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_04259 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_05260 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_06261 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class PIA256 {
	public String E4347_01;
	public C212_02257 C212_02;
	public C212_03258 C212_03;
	public C212_04259 C212_04;
	public C212_05260 C212_05;
	public C212_06261 C212_06;
}
class C107_03263 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04264 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX262 {
	public String E4451_01;
	public String E4453_02;
	public C107_03263 C107_03;
	public C108_04264 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C501_01266 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD265 {
	public C501_01266 C501_01;
}
class C082_02269 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03270 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04271 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05272 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07273 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD268 {
	public String E3035_01;
	public C082_02269 C082_02;
	public C058_03270 C058_03;
	public C080_04271 C080_04;
	public C059_05272 C059_05;
	public String E3164_06;
	public C819_07273 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01275 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM274 {
	public C507_01275 C507_01;
}
class C517_02277 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03278 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04279 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC276 {
	public String E3227_01;
	public C517_02277 C517_02;
	public C519_03278 C519_03;
	public C553_04279 C553_04;
	public String E5479_05;
}
class Group19_NAD267 {
	public NAD268 NAD; 
	public DTM274 DTM; 
	public List<LOC276> LOC; 
}
class C703_01281 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS280 {
	public C703_01281 C703_01;
}
class C502_02284 {
	public String E6313_01;
}
class C174_03285 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
}
class MEA283 {
	public String E6311_01;
	public C502_02284 C502_02;
	public C174_03285 C174_03;
	public String E7383_04;
}
class C523_01287 {
	public String E6350_01;
	public String E6353_02;
}
class EQN286 {
	public C523_01287 C523_01;
}
class Group20_MEA282 {
	public MEA283 MEA; 
	public EQN286 EQN; 
}
class C211_02290 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM289 {
	public String E6145_01;
	public C211_02290 C211_02;
}
class C523_01292 {
	public String E6350_01;
	public String E6353_02;
}
class EQN291 {
	public C523_01292 C523_01;
}
class Group21_DIM288 {
	public DIM289 DIM; 
	public EQN291 EQN; 
}
class C506_01295 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF294 {
	public C506_01295 C506_01;
}
class C507_01297 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM296 {
	public C507_01297 C507_01;
}
class Group22_RFF293 {
	public RFF294 RFF; 
	public List<DTM296> DTM; 
}
class C210_02300 {
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
class C827_04301 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI299 {
	public String E4233_01;
	public C210_02300 C210_02;
	public String E8275_03;
	public C827_04301 C827_04;
}
class C506_01303 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF302 {
	public C506_01303 C506_01;
}
class C507_01305 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM304 {
	public C507_01305 C507_01;
}
class C208_02307 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03308 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04309 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05310 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06311 {
	public String E7402_01;
	public String E7402_02;
}
class GIN306 {
	public String E7405_01;
	public C208_02307 C208_02;
	public C208_03308 C208_03;
	public C208_04309 C208_04;
	public C208_05310 C208_05;
	public C208_06311 C208_06;
}
class Group23_PCI298 {
	public PCI299 PCI; 
	public RFF302 RFF; 
	public DTM304 DTM; 
	public List<GIN306> GIN; 
}
class C002_01314 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02315 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC313 {
	public C002_01314 C002_01;
	public C503_02315 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01317 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM316 {
	public C507_01317 C507_01;
}
class Group24_DOC312 {
	public DOC313 DOC; 
	public List<DTM316> DTM; 
}
class C232_02320 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_03321 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_04322 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_05323 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class GOR319 {
	public String E8323_01;
	public C232_02320 C232_02;
	public C232_03321 C232_03;
	public C232_04322 C232_04;
	public C232_05323 C232_05;
}
class C507_01325 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM324 {
	public C507_01325 C507_01;
}
class C517_02327 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03328 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04329 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC326 {
	public String E3227_01;
	public C517_02327 C517_02;
	public C519_03328 C519_03;
	public C553_04329 C553_04;
	public String E5479_05;
}
class C215_02331 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_04332 {
	public String E7402_01;
	public String E7402_02;
}
class SEL330 {
	public String E9308_01;
	public C215_02331 C215_02;
	public String E4517_03;
	public C208_04332 C208_04;
}
class C107_03334 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04335 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX333 {
	public String E4451_01;
	public String E4453_02;
	public C107_03334 C107_03;
	public C108_04335 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C002_01338 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02339 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC337 {
	public C002_01338 C002_01;
	public C503_02339 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01341 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM340 {
	public C507_01341 C507_01;
}
class Group26_DOC336 {
	public DOC337 DOC; 
	public DTM340 DTM; 
}
class Group25_GOR318 {
	public GOR319 GOR; 
	public List<DTM324> DTM; 
	public List<LOC326> LOC; 
	public List<SEL330> SEL; 
	public List<FTX333> FTX; 
	public List<Group26_DOC336> Group26_DOC; 
}
class C222_01344 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL343 {
	public C222_01344 C222_01;
}
class C502_02347 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03348 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA346 {
	public String E6311_01;
	public C502_02347 C502_02;
	public C174_03348 C174_03;
	public String E7383_04;
}
class C523_01350 {
	public String E6350_01;
	public String E6353_02;
}
class EQN349 {
	public C523_01350 C523_01;
}
class Group28_MEA345 {
	public MEA346 MEA; 
	public EQN349 EQN; 
}
class Group27_TPL342 {
	public TPL343 TPL; 
	public List<Group28_MEA345> Group28_MEA; 
}
class C237_01353 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP352 {
	public C237_01353 C237_01;
	public String E7224_02;
}
class C502_02356 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03357 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA355 {
	public String E6311_01;
	public C502_02356 C502_02;
	public C174_03357 C174_03;
	public String E7383_04;
}
class C523_01359 {
	public String E6350_01;
	public String E6353_02;
}
class EQN358 {
	public C523_01359 C523_01;
}
class Group30_MEA354 {
	public MEA355 MEA; 
	public EQN358 EQN; 
}
class Group29_SGP351 {
	public SGP352 SGP; 
	public List<Group30_MEA354> Group30_MEA; 
}
class C200_01362 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02363 {
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
class C528_03364 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04365 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC361 {
	public C200_01362 C200_01;
	public C203_02363 C203_02;
	public C528_03364 C528_03;
	public C554_04365 C554_04;
}
class C504_01367 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02368 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX366 {
	public C504_01367 C504_01;
	public C504_02368 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01370 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI369 {
	public C509_01370 C509_01;
	public String E5213_02;
}
class C523_01372 {
	public String E6350_01;
	public String E6353_02;
}
class EQN371 {
	public C523_01372 C523_01;
}
class C501_01374 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD373 {
	public C501_01374 C501_01;
}
class C516_01376 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA375 {
	public C516_01376 C516_01;
}
class C186_01378 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY377 {
	public C186_01378 C186_01;
}
class C517_02380 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03381 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04382 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC379 {
	public String E3227_01;
	public C517_02380 C517_02;
	public C519_03381 C519_03;
	public C553_04382 C553_04;
	public String E5479_05;
}
class Group31_TCC360 {
	public TCC361 TCC; 
	public CUX366 CUX; 
	public PRI369 PRI; 
	public EQN371 EQN; 
	public PCD373 PCD; 
	public List<MOA375> MOA; 
	public List<QTY377> QTY; 
	public List<LOC379> LOC; 
}
class C205_02385 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03386 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04387 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09388 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10389 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS384 {
	public String E8273_01;
	public C205_02385 C205_02;
	public C234_03386 C234_03;
	public C223_04387 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09388 C235_09;
	public C236_10389 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03391 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04392 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX390 {
	public String E4451_01;
	public String E4453_02;
	public C107_03391 C107_03;
	public C108_04392 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02395 {
	public String E3413_01;
	public String E3412_02;
}
class CTA394 {
	public String E3139_01;
	public C056_02395 C056_02;
}
class C076_01397 {
	public String E3148_01;
	public String E3155_02;
}
class COM396 {
	public C076_01397 C076_01;
}
class Group33_CTA393 {
	public CTA394 CTA; 
	public List<COM396> COM; 
}
class C502_02400 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03401 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA399 {
	public String E6311_01;
	public C502_02400 C502_02;
	public C174_03401 C174_03;
	public String E7383_04;
}
class C523_01403 {
	public String E6350_01;
	public String E6353_02;
}
class EQN402 {
	public C523_01403 C523_01;
}
class Group34_MEA398 {
	public MEA399 MEA; 
	public EQN402 EQN; 
}
class C237_01406 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP405 {
	public C237_01406 C237_01;
	public String E7224_02;
}
class C502_02409 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03410 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA408 {
	public String E6311_01;
	public C502_02409 C502_02;
	public C174_03410 C174_03;
	public String E7383_04;
}
class C523_01412 {
	public String E6350_01;
	public String E6353_02;
}
class EQN411 {
	public C523_01412 C523_01;
}
class Group36_MEA407 {
	public MEA408 MEA; 
	public EQN411 EQN; 
}
class Group35_SGP404 {
	public SGP405 SGP; 
	public List<Group36_MEA407> Group36_MEA; 
}
class Group32_DGS383 {
	public DGS384 DGS; 
	public List<FTX390> FTX; 
	public List<Group33_CTA393> Group33_CTA; 
	public List<Group34_MEA398> Group34_MEA; 
	public List<Group35_SGP404> Group35_SGP; 
}
class Group18_GID234 {
	public GID235 GID; 
	public List<HAN241> HAN; 
	public TMP244 TMP; 
	public RNG246 RNG; 
	public TMD248 TMD; 
	public List<LOC250> LOC; 
	public List<MOA254> MOA; 
	public List<PIA256> PIA; 
	public List<FTX262> FTX; 
	public List<PCD265> PCD; 
	public List<Group19_NAD267> Group19_NAD; 
	public List<GDS280> GDS; 
	public List<Group20_MEA282> Group20_MEA; 
	public List<Group21_DIM288> Group21_DIM; 
	public List<Group22_RFF293> Group22_RFF; 
	public List<Group23_PCI298> Group23_PCI; 
	public List<Group24_DOC312> Group24_DOC; 
	public List<Group25_GOR318> Group25_GOR; 
	public List<Group27_TPL342> Group27_TPL; 
	public List<Group29_SGP351> Group29_SGP; 
	public List<Group31_TCC360> Group31_TCC; 
	public List<Group32_DGS383> Group32_DGS; 
}
class C237_02415 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03416 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD414 {
	public String E8053_01;
	public C237_02415 C237_02;
	public C224_03416 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C523_01418 {
	public String E6350_01;
	public String E6353_02;
}
class EQN417 {
	public C523_01418 C523_01;
}
class C219_01420 {
	public String E8335_01;
	public String E8334_02;
}
class TMD419 {
	public C219_01420 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C502_02422 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03423 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA421 {
	public String E6311_01;
	public C502_02422 C502_02;
	public C174_03423 C174_03;
	public String E7383_04;
}
class C211_02425 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM424 {
	public String E6145_01;
	public C211_02425 C211_02;
}
class C215_02427 {
	public String E9303_01;
}
class SEL426 {
	public String E9308_01;
	public C215_02427 C215_02;
}
class C222_01429 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL428 {
	public C222_01429 C222_01;
}
class C524_01431 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02432 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN430 {
	public C524_01431 C524_01;
	public C218_02432 C218_02;
}
class C239_02434 {
	public String E6246_01;
	public String E6411_02;
}
class TMP433 {
	public String E6245_01;
	public C239_02434 C239_02;
}
class C107_03436 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04437 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX435 {
	public String E4451_01;
	public String E4453_02;
	public C107_03436 C107_03;
	public C108_04437 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C506_01439 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF438 {
	public C506_01439 C506_01;
}
class C200_01442 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02443 {
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
class C528_03444 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04445 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC441 {
	public C200_01442 C200_01;
	public C203_02443 C203_02;
	public C528_03444 C528_03;
	public C554_04445 C554_04;
}
class C504_01447 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02448 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX446 {
	public C504_01447 C504_01;
	public C504_02448 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01450 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI449 {
	public C509_01450 C509_01;
	public String E5213_02;
}
class C523_01452 {
	public String E6350_01;
	public String E6353_02;
}
class EQN451 {
	public C523_01452 C523_01;
}
class C501_01454 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD453 {
	public C501_01454 C501_01;
}
class C516_01456 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA455 {
	public C516_01456 C516_01;
}
class C186_01458 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY457 {
	public C186_01458 C186_01;
}
class Group38_TCC440 {
	public TCC441 TCC; 
	public CUX446 CUX; 
	public PRI449 PRI; 
	public EQN451 EQN; 
	public PCD453 PCD; 
	public List<MOA455> MOA; 
	public List<QTY457> QTY; 
}
class C082_02461 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03462 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04463 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05464 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07465 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD460 {
	public String E3035_01;
	public C082_02461 C082_02;
	public C058_03462 C058_03;
	public C080_04463 C080_04;
	public C059_05464 C059_05;
	public String E3164_06;
	public C819_07465 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01467 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM466 {
	public C507_01467 C507_01;
}
class C056_02470 {
	public String E3413_01;
	public String E3412_02;
}
class CTA469 {
	public String E3139_01;
	public C056_02470 C056_02;
}
class C076_01472 {
	public String E3148_01;
	public String E3155_02;
}
class COM471 {
	public C076_01472 C076_01;
}
class Group40_CTA468 {
	public CTA469 CTA; 
	public List<COM471> COM; 
}
class Group39_NAD459 {
	public NAD460 NAD; 
	public DTM466 DTM; 
	public List<Group40_CTA468> Group40_CTA; 
}
class C237_02475 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class EQA474 {
	public String E8053_01;
	public C237_02475 C237_02;
}
class C523_01477 {
	public String E6350_01;
	public String E6353_02;
}
class EQN476 {
	public C523_01477 C523_01;
}
class Group41_EQA473 {
	public EQA474 EQA; 
	public EQN476 EQN; 
}
class C205_02480 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03481 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04482 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09483 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10484 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS479 {
	public String E8273_01;
	public C205_02480 C205_02;
	public C234_03481 C234_03;
	public C223_04482 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09483 C235_09;
	public C236_10484 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03486 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04487 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX485 {
	public String E4451_01;
	public String E4453_02;
	public C107_03486 C107_03;
	public C108_04487 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02490 {
	public String E3413_01;
	public String E3412_02;
}
class CTA489 {
	public String E3139_01;
	public C056_02490 C056_02;
}
class C076_01492 {
	public String E3148_01;
	public String E3155_02;
}
class COM491 {
	public C076_01492 C076_01;
}
class Group43_CTA488 {
	public CTA489 CTA; 
	public List<COM491> COM; 
}
class Group42_DGS478 {
	public DGS479 DGS; 
	public List<FTX485> FTX; 
	public List<Group43_CTA488> Group43_CTA; 
}
class Group37_EQD413 {
	public EQD414 EQD; 
	public EQN417 EQN; 
	public TMD419 TMD; 
	public List<MEA421> MEA; 
	public List<DIM424> DIM; 
	public List<SEL426> SEL; 
	public List<TPL428> TPL; 
	public HAN430 HAN; 
	public TMP433 TMP; 
	public List<FTX435> FTX; 
	public List<RFF438> RFF; 
	public List<Group38_TCC440> Group38_TCC; 
	public List<Group39_NAD459> Group39_NAD; 
	public List<Group41_EQA473> Group41_EQA; 
	public List<Group42_DGS478> Group42_DGS; 
}
class UNT493 {
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
	public List<CUX29> CUX; 
	public List<MOA32> MOA; 
	public List<FTX34> FTX; 
	public List<CNT37> CNT; 
	public List<DOC39> DOC; 
	public List<GDS42> GDS; 
	public List<Group1_LOC44> Group1_LOC; 
	public List<Group2_TOD51> Group2_TOD; 
	public List<Group3_RFF58> Group3_RFF; 
	public List<Group4_GOR63> Group4_GOR; 
	public List<Group6_CPI87> Group6_CPI; 
	public List<Group7_TCC102> Group7_TCC; 
	public List<Group8_TDT128> Group8_TDT; 
	public List<Group11_NAD154> Group11_NAD; 
	public List<Group18_GID234> Group18_GID; 
	public List<Group37_EQD413> Group37_EQD; 
	public UNT493 UNT; 
}
class UNE494 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ495 {
	public String E0036_01;
	public String E0020_02;
}
