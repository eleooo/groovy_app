package cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN

public class EDI_IFTMIN {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","COM","Group_UNH.DTM","Group_UNH.TSR","Group_UNH.CUX","MOA","Group_UNH.FTX","CNT","Group_UNH.DOC","GDS","Group1_LOC","Group1_LOC.DTM","Group2_TOD","Group2_TOD.LOC","Group3_RFF","Group3_RFF.DTM","Group4_GOR","Group4_GOR.DTM","Group4_GOR.LOC","SEL","Group4_GOR.FTX","Group5_DOC","Group6_CPI","Group6_CPI.RFF","Group6_CPI.LOC","Group7_TCC","QTY","Group8_TDT","Group8_TDT.DTM","Group8_TDT.TSR","Group9_LOC","Group9_LOC.DTM","Group10_RFF","Group11_NAD","Group11_NAD.LOC","Group12_CTA","Group13_DOC","Group14_TCC","Group15_RFF","Group15_RFF.DTM","Group16_CPI","Group16_CPI.RFF","Group16_CPI.LOC","Group17_TSR","Group17_TSR.FTX","Group18_GID","Group18_GID.HAN","Group18_GID.LOC","PIA","Group18_GID.FTX","Group18_GID.PCD","Group19_NAD","Group19_NAD.LOC","Group20_MEA","Group21_DIM","Group22_RFF","Group22_RFF.DTM","Group23_PCI","GIN","Group24_DOC","Group24_DOC.DTM","Group25_GOR","Group25_GOR.DTM","Group25_GOR.LOC","Group25_GOR.FTX","Group26_DOC","Group27_TPL","Group28_MEA","Group29_SGP","Group30_MEA","Group31_TCC","Group31_TCC.LOC","Group32_DGS","Group32_DGS.FTX","Group33_CTA","Group34_MEA","Group35_SGP","Group36_MEA","Group37_EQD","Group37_EQD.MEA","Group37_EQD.DIM","Group37_EQD.TPL","Group37_EQD.FTX","Group37_EQD.RFF","Group38_TCC","Group39_NAD","Group40_CTA","Group41_EQA","Group42_DGS","Group42_DGS.FTX","Group43_CTA"];

	public List<UNA1> UNA;
	public UNB2 UNB;
	public UNG8 UNG;
	public List<Group_UNH13> Group_UNH;
	public UNE499 UNE;
	public UNZ500 UNZ;
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
class C002_0142 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0243 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC41 {
	public C002_0142 C002_01;
	public C503_0243 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C703_0145 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS44 {
	public C703_0145 C703_01;
}
class C517_0248 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0349 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0450 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC47 {
	public String E3227_01;
	public C517_0248 C517_02;
	public C519_0349 C519_03;
	public C553_0450 C553_04;
	public String E5479_05;
}
class C507_0152 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM51 {
	public C507_0152 C507_01;
}
class Group1_LOC46 {
	public LOC47 LOC;
	public List<DTM51> DTM;
}
class C100_0355 {
	public String E4053_01;
	public String E1131_02;
	public String E3055_03;
	public String E4052_04;
	public String E4052_05;
}
class TOD54 {
	public String E4055_01;
	public String E4215_02;
	public C100_0355 C100_03;
}
class C517_0257 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0358 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0459 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC56 {
	public String E3227_01;
	public C517_0257 C517_02;
	public C519_0358 C519_03;
	public C553_0459 C553_04;
	public String E5479_05;
}
class Group2_TOD53 {
	public TOD54 TOD;
	public List<LOC56> LOC;
}
class C506_0162 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF61 {
	public C506_0162 C506_01;
}
class C507_0164 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM63 {
	public C507_0164 C507_01;
}
class Group3_RFF60 {
	public RFF61 RFF;
	public List<DTM63> DTM;
}
class C232_0267 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0368 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0469 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0570 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class GOR66 {
	public String E8323_01;
	public C232_0267 C232_02;
	public C232_0368 C232_03;
	public C232_0469 C232_04;
	public C232_0570 C232_05;
}
class C507_0172 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM71 {
	public C507_0172 C507_01;
}
class C517_0274 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0375 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0476 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC73 {
	public String E3227_01;
	public C517_0274 C517_02;
	public C519_0375 C519_03;
	public C553_0476 C553_04;
	public String E5479_05;
}
class C215_0278 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_0479 {
	public String E7402_01;
	public String E7402_02;
}
class SEL77 {
	public String E9308_01;
	public C215_0278 C215_02;
	public String E4517_03;
	public C208_0479 C208_04;
}
class C107_0381 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0482 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX80 {
	public String E4451_01;
	public String E4453_02;
	public C107_0381 C107_03;
	public C108_0482 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C002_0185 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0286 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC84 {
	public C002_0185 C002_01;
	public C503_0286 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_0188 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM87 {
	public C507_0188 C507_01;
}
class Group5_DOC83 {
	public DOC84 DOC;
	public DTM87 DTM;
}
class Group4_GOR65 {
	public GOR66 GOR;
	public List<DTM71> DTM;
	public List<LOC73> LOC;
	public List<SEL77> SEL;
	public List<FTX80> FTX;
	public List<Group5_DOC83> Group5_DOC;
}
class C229_0191 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_0292 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI90 {
	public C229_0191 C229_01;
	public C231_0292 C231_02;
	public String E4237_03;
}
class C506_0194 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF93 {
	public C506_0194 C506_01;
}
class C504_0196 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0297 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX95 {
	public C504_0196 C504_01;
	public C504_0297 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_0299 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03100 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04101 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC98 {
	public String E3227_01;
	public C517_0299 C517_02;
	public C519_03100 C519_03;
	public C553_04101 C553_04;
	public String E5479_05;
}
class C516_01103 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA102 {
	public C516_01103 C516_01;
}
class Group6_CPI89 {
	public CPI90 CPI;
	public List<RFF93> RFF;
	public CUX95 CUX;
	public List<LOC98> LOC;
	public List<MOA102> MOA;
}
class C200_01106 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02107 {
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
class C528_03108 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04109 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC105 {
	public C200_01106 C200_01;
	public C203_02107 C203_02;
	public C528_03108 C528_03;
	public C554_04109 C554_04;
}
class C517_02111 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03112 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04113 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC110 {
	public String E3227_01;
	public C517_02111 C517_02;
	public C519_03112 C519_03;
	public C553_04113 C553_04;
	public String E5479_05;
}
class C107_03115 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04116 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX114 {
	public String E4451_01;
	public String E4453_02;
	public C107_03115 C107_03;
	public C108_04116 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C504_01118 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02119 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX117 {
	public C504_01118 C504_01;
	public C504_02119 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01121 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI120 {
	public C509_01121 C509_01;
	public String E5213_02;
}
class C523_01123 {
	public String E6350_01;
	public String E6353_02;
}
class EQN122 {
	public C523_01123 C523_01;
}
class C501_01125 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD124 {
	public C501_01125 C501_01;
}
class C516_01127 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA126 {
	public C516_01127 C516_01;
}
class C186_01129 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY128 {
	public C186_01129 C186_01;
}
class Group7_TCC104 {
	public TCC105 TCC;
	public LOC110 LOC;
	public FTX114 FTX;
	public CUX117 CUX;
	public PRI120 PRI;
	public EQN122 EQN;
	public PCD124 PCD;
	public List<MOA126> MOA;
	public List<QTY128> QTY;
}
class C220_03132 {
	public String E8067_01;
	public String E8066_02;
}
class C228_04133 {
	public String E8179_01;
	public String E8178_02;
}
class C040_05134 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_07135 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_08136 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT131 {
	public String E8051_01;
	public String E8028_02;
	public C220_03132 C220_03;
	public C228_04133 C228_04;
	public C040_05134 C040_05;
	public String E8101_06;
	public C401_07135 C401_07;
	public C222_08136 C222_08;
	public String E8281_09;
}
class C507_01138 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM137 {
	public C507_01138 C507_01;
}
class C536_01140 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02141 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03142 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04143 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR139 {
	public C536_01140 C536_01;
	public C233_02141 C233_02;
	public C537_03142 C537_03;
	public C703_04143 C703_04;
}
class C517_02146 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03147 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04148 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC145 {
	public String E3227_01;
	public C517_02146 C517_02;
	public C519_03147 C519_03;
	public C553_04148 C553_04;
	public String E5479_05;
}
class C507_01150 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM149 {
	public C507_01150 C507_01;
}
class Group9_LOC144 {
	public LOC145 LOC;
	public List<DTM149> DTM;
}
class C506_01153 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF152 {
	public C506_01153 C506_01;
}
class C507_01155 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM154 {
	public C507_01155 C507_01;
}
class Group10_RFF151 {
	public RFF152 RFF;
	public DTM154 DTM;
}
class Group8_TDT130 {
	public TDT131 TDT;
	public List<DTM137> DTM;
	public List<TSR139> TSR;
	public List<Group9_LOC144> Group9_LOC;
	public List<Group10_RFF151> Group10_RFF;
}
class C082_02158 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03159 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04160 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05161 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07162 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD157 {
	public String E3035_01;
	public C082_02158 C082_02;
	public C058_03159 C058_03;
	public C080_04160 C080_04;
	public C059_05161 C059_05;
	public String E3164_06;
	public C819_07162 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C517_02164 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03165 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04166 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC163 {
	public String E3227_01;
	public C517_02164 C517_02;
	public C519_03165 C519_03;
	public C553_04166 C553_04;
	public String E5479_05;
}
class C516_01168 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA167 {
	public C516_01168 C516_01;
}
class C076_01170 {
	public String E3148_01;
	public String E3155_02;
}
class COM169 {
	public C076_01170 C076_01;
}
class C056_02173 {
	public String E3413_01;
	public String E3412_02;
}
class CTA172 {
	public String E3139_01;
	public C056_02173 C056_02;
}
class C076_01175 {
	public String E3148_01;
	public String E3155_02;
}
class COM174 {
	public C076_01175 C076_01;
}
class Group12_CTA171 {
	public CTA172 CTA;
	public List<COM174> COM;
}
class C002_01178 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02179 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC177 {
	public C002_01178 C002_01;
	public C503_02179 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01181 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM180 {
	public C507_01181 C507_01;
}
class Group13_DOC176 {
	public DOC177 DOC;
	public DTM180 DTM;
}
class C200_01184 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02185 {
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
class C528_03186 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04187 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC183 {
	public C200_01184 C200_01;
	public C203_02185 C203_02;
	public C528_03186 C528_03;
	public C554_04187 C554_04;
}
class C504_01189 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02190 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX188 {
	public C504_01189 C504_01;
	public C504_02190 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01192 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI191 {
	public C509_01192 C509_01;
	public String E5213_02;
}
class C523_01194 {
	public String E6350_01;
	public String E6353_02;
}
class EQN193 {
	public C523_01194 C523_01;
}
class C501_01196 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD195 {
	public C501_01196 C501_01;
}
class C516_01198 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA197 {
	public C516_01198 C516_01;
}
class C186_01200 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY199 {
	public C186_01200 C186_01;
}
class Group14_TCC182 {
	public TCC183 TCC;
	public CUX188 CUX;
	public PRI191 PRI;
	public EQN193 EQN;
	public PCD195 PCD;
	public List<MOA197> MOA;
	public List<QTY199> QTY;
}
class C506_01203 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF202 {
	public C506_01203 C506_01;
}
class C507_01205 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM204 {
	public C507_01205 C507_01;
}
class Group15_RFF201 {
	public RFF202 RFF;
	public List<DTM204> DTM;
}
class C229_01208 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_02209 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI207 {
	public C229_01208 C229_01;
	public C231_02209 C231_02;
	public String E4237_03;
}
class C506_01211 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF210 {
	public C506_01211 C506_01;
}
class C504_01213 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02214 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX212 {
	public C504_01213 C504_01;
	public C504_02214 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_02216 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03217 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04218 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC215 {
	public String E3227_01;
	public C517_02216 C517_02;
	public C519_03217 C519_03;
	public C553_04218 C553_04;
	public String E5479_05;
}
class C516_01220 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA219 {
	public C516_01220 C516_01;
}
class Group16_CPI206 {
	public CPI207 CPI;
	public List<RFF210> RFF;
	public CUX212 CUX;
	public List<LOC215> LOC;
	public List<MOA219> MOA;
}
class C536_01223 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02224 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03225 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04226 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR222 {
	public C536_01223 C536_01;
	public C233_02224 C233_02;
	public C537_03225 C537_03;
	public C703_04226 C703_04;
}
class C506_01228 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF227 {
	public C506_01228 C506_01;
}
class C517_02230 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03231 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04232 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC229 {
	public String E3227_01;
	public C517_02230 C517_02;
	public C519_03231 C519_03;
	public C553_04232 C553_04;
	public String E5479_05;
}
class C222_01234 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL233 {
	public C222_01234 C222_01;
}
class C107_03236 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04237 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX235 {
	public String E4451_01;
	public String E4453_02;
	public C107_03236 C107_03;
	public C108_04237 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class Group17_TSR221 {
	public TSR222 TSR;
	public RFF227 RFF;
	public LOC229 LOC;
	public TPL233 TPL;
	public List<FTX235> FTX;
}
class Group11_NAD156 {
	public NAD157 NAD;
	public List<LOC163> LOC;
	public List<MOA167> MOA;
	public List<COM169> COM;
	public List<Group12_CTA171> Group12_CTA;
	public List<Group13_DOC176> Group13_DOC;
	public List<Group14_TCC182> Group14_TCC;
	public List<Group15_RFF201> Group15_RFF;
	public List<Group16_CPI206> Group16_CPI;
	public List<Group17_TSR221> Group17_TSR;
}
class C213_02240 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_03241 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_04242 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_05243 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_06244 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class GID239 {
	public String E1496_01;
	public C213_02240 C213_02;
	public C213_03241 C213_03;
	public C213_04242 C213_04;
	public C213_05243 C213_05;
	public C213_06244 C213_06;
}
class C524_01246 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02247 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN245 {
	public C524_01246 C524_01;
	public C218_02247 C218_02;
}
class C239_02249 {
	public String E6246_01;
	public String E6411_02;
}
class TMP248 {
	public String E6245_01;
	public C239_02249 C239_02;
}
class C280_02251 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG250 {
	public String E6167_01;
	public C280_02251 C280_02;
}
class C219_01253 {
	public String E8335_01;
	public String E8334_02;
}
class TMD252 {
	public C219_01253 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02255 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03256 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04257 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC254 {
	public String E3227_01;
	public C517_02255 C517_02;
	public C519_03256 C519_03;
	public C553_04257 C553_04;
	public String E5479_05;
}
class C516_01259 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA258 {
	public C516_01259 C516_01;
}
class C212_02261 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_03262 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_04263 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_05264 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_06265 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class PIA260 {
	public String E4347_01;
	public C212_02261 C212_02;
	public C212_03262 C212_03;
	public C212_04263 C212_04;
	public C212_05264 C212_05;
	public C212_06265 C212_06;
}
class C107_03267 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04268 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX266 {
	public String E4451_01;
	public String E4453_02;
	public C107_03267 C107_03;
	public C108_04268 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C501_01270 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD269 {
	public C501_01270 C501_01;
}
class C082_02273 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03274 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04275 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05276 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07277 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD272 {
	public String E3035_01;
	public C082_02273 C082_02;
	public C058_03274 C058_03;
	public C080_04275 C080_04;
	public C059_05276 C059_05;
	public String E3164_06;
	public C819_07277 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01279 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM278 {
	public C507_01279 C507_01;
}
class C517_02281 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03282 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04283 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC280 {
	public String E3227_01;
	public C517_02281 C517_02;
	public C519_03282 C519_03;
	public C553_04283 C553_04;
	public String E5479_05;
}
class Group19_NAD271 {
	public NAD272 NAD;
	public DTM278 DTM;
	public List<LOC280> LOC;
}
class C703_01285 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS284 {
	public C703_01285 C703_01;
}
class C502_02288 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03289 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA287 {
	public String E6311_01;
	public C502_02288 C502_02;
	public C174_03289 C174_03;
	public String E7383_04;
}
class C523_01291 {
	public String E6350_01;
	public String E6353_02;
}
class EQN290 {
	public C523_01291 C523_01;
}
class Group20_MEA286 {
	public MEA287 MEA;
	public EQN290 EQN;
}
class C211_02294 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM293 {
	public String E6145_01;
	public C211_02294 C211_02;
}
class C523_01296 {
	public String E6350_01;
	public String E6353_02;
}
class EQN295 {
	public C523_01296 C523_01;
}
class Group21_DIM292 {
	public DIM293 DIM;
	public EQN295 EQN;
}
class C506_01299 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF298 {
	public C506_01299 C506_01;
}
class C507_01301 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM300 {
	public C507_01301 C507_01;
}
class Group22_RFF297 {
	public RFF298 RFF;
	public List<DTM300> DTM;
}
class C210_02304 {
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
class C827_04305 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI303 {
	public String E4233_01;
	public C210_02304 C210_02;
	public String E8275_03;
	public C827_04305 C827_04;
}
class C506_01307 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF306 {
	public C506_01307 C506_01;
}
class C507_01309 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM308 {
	public C507_01309 C507_01;
}
class C208_02311 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03312 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04313 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05314 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06315 {
	public String E7402_01;
	public String E7402_02;
}
class GIN310 {
	public String E7405_01;
	public C208_02311 C208_02;
	public C208_03312 C208_03;
	public C208_04313 C208_04;
	public C208_05314 C208_05;
	public C208_06315 C208_06;
}
class Group23_PCI302 {
	public PCI303 PCI;
	public RFF306 RFF;
	public DTM308 DTM;
	public List<GIN310> GIN;
}
class C002_01318 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02319 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC317 {
	public C002_01318 C002_01;
	public C503_02319 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01321 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM320 {
	public C507_01321 C507_01;
}
class Group24_DOC316 {
	public DOC317 DOC;
	public List<DTM320> DTM;
}
class C232_02324 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_03325 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_04326 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_05327 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class GOR323 {
	public String E8323_01;
	public C232_02324 C232_02;
	public C232_03325 C232_03;
	public C232_04326 C232_04;
	public C232_05327 C232_05;
}
class C507_01329 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM328 {
	public C507_01329 C507_01;
}
class C517_02331 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03332 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04333 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC330 {
	public String E3227_01;
	public C517_02331 C517_02;
	public C519_03332 C519_03;
	public C553_04333 C553_04;
	public String E5479_05;
}
class C215_02335 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_04336 {
	public String E7402_01;
	public String E7402_02;
}
class SEL334 {
	public String E9308_01;
	public C215_02335 C215_02;
	public String E4517_03;
	public C208_04336 C208_04;
}
class C107_03338 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04339 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX337 {
	public String E4451_01;
	public String E4453_02;
	public C107_03338 C107_03;
	public C108_04339 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C002_01342 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02343 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC341 {
	public C002_01342 C002_01;
	public C503_02343 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01345 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM344 {
	public C507_01345 C507_01;
}
class Group26_DOC340 {
	public DOC341 DOC;
	public DTM344 DTM;
}
class Group25_GOR322 {
	public GOR323 GOR;
	public List<DTM328> DTM;
	public List<LOC330> LOC;
	public List<SEL334> SEL;
	public List<FTX337> FTX;
	public List<Group26_DOC340> Group26_DOC;
}
class C222_01348 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL347 {
	public C222_01348 C222_01;
}
class C502_02351 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03352 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA350 {
	public String E6311_01;
	public C502_02351 C502_02;
	public C174_03352 C174_03;
	public String E7383_04;
}
class C523_01354 {
	public String E6350_01;
	public String E6353_02;
}
class EQN353 {
	public C523_01354 C523_01;
}
class Group28_MEA349 {
	public MEA350 MEA;
	public EQN353 EQN;
}
class Group27_TPL346 {
	public TPL347 TPL;
	public List<Group28_MEA349> Group28_MEA;
}
class C237_01357 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP356 {
	public C237_01357 C237_01;
	public String E7224_02;
}
class C502_02360 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03361 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA359 {
	public String E6311_01;
	public C502_02360 C502_02;
	public C174_03361 C174_03;
	public String E7383_04;
}
class C523_01363 {
	public String E6350_01;
	public String E6353_02;
}
class EQN362 {
	public C523_01363 C523_01;
}
class Group30_MEA358 {
	public MEA359 MEA;
	public EQN362 EQN;
}
class Group29_SGP355 {
	public SGP356 SGP;
	public List<Group30_MEA358> Group30_MEA;
}
class C200_01366 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02367 {
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
class C528_03368 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04369 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC365 {
	public C200_01366 C200_01;
	public C203_02367 C203_02;
	public C528_03368 C528_03;
	public C554_04369 C554_04;
}
class C504_01371 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02372 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX370 {
	public C504_01371 C504_01;
	public C504_02372 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01374 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI373 {
	public C509_01374 C509_01;
	public String E5213_02;
}
class C523_01376 {
	public String E6350_01;
	public String E6353_02;
}
class EQN375 {
	public C523_01376 C523_01;
}
class C501_01378 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD377 {
	public C501_01378 C501_01;
}
class C516_01380 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA379 {
	public C516_01380 C516_01;
}
class C186_01382 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY381 {
	public C186_01382 C186_01;
}
class C517_02384 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03385 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04386 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC383 {
	public String E3227_01;
	public C517_02384 C517_02;
	public C519_03385 C519_03;
	public C553_04386 C553_04;
	public String E5479_05;
}
class Group31_TCC364 {
	public TCC365 TCC;
	public CUX370 CUX;
	public PRI373 PRI;
	public EQN375 EQN;
	public PCD377 PCD;
	public List<MOA379> MOA;
	public List<QTY381> QTY;
	public List<LOC383> LOC;
}
class C205_02389 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03390 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04391 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09392 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10393 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS388 {
	public String E8273_01;
	public C205_02389 C205_02;
	public C234_03390 C234_03;
	public C223_04391 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09392 C235_09;
	public C236_10393 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03395 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04396 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX394 {
	public String E4451_01;
	public String E4453_02;
	public C107_03395 C107_03;
	public C108_04396 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02399 {
	public String E3413_01;
	public String E3412_02;
}
class CTA398 {
	public String E3139_01;
	public C056_02399 C056_02;
}
class C076_01401 {
	public String E3148_01;
	public String E3155_02;
}
class COM400 {
	public C076_01401 C076_01;
}
class Group33_CTA397 {
	public CTA398 CTA;
	public List<COM400> COM;
}
class C502_02404 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03405 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA403 {
	public String E6311_01;
	public C502_02404 C502_02;
	public C174_03405 C174_03;
	public String E7383_04;
}
class C523_01407 {
	public String E6350_01;
	public String E6353_02;
}
class EQN406 {
	public C523_01407 C523_01;
}
class Group34_MEA402 {
	public MEA403 MEA;
	public EQN406 EQN;
}
class C237_01410 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP409 {
	public C237_01410 C237_01;
	public String E7224_02;
}
class C502_02413 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03414 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA412 {
	public String E6311_01;
	public C502_02413 C502_02;
	public C174_03414 C174_03;
	public String E7383_04;
}
class C523_01416 {
	public String E6350_01;
	public String E6353_02;
}
class EQN415 {
	public C523_01416 C523_01;
}
class Group36_MEA411 {
	public MEA412 MEA;
	public EQN415 EQN;
}
class Group35_SGP408 {
	public SGP409 SGP;
	public List<Group36_MEA411> Group36_MEA;
}
class Group32_DGS387 {
	public DGS388 DGS;
	public List<FTX394> FTX;
	public List<Group33_CTA397> Group33_CTA;
	public List<Group34_MEA402> Group34_MEA;
	public List<Group35_SGP408> Group35_SGP;
}
class Group18_GID238 {
	public GID239 GID;
	public List<HAN245> HAN;
	public TMP248 TMP;
	public RNG250 RNG;
	public TMD252 TMD;
	public List<LOC254> LOC;
	public List<MOA258> MOA;
	public List<PIA260> PIA;
	public List<FTX266> FTX;
	public List<PCD269> PCD;
	public List<Group19_NAD271> Group19_NAD;
	public List<GDS284> GDS;
	public List<Group20_MEA286> Group20_MEA;
	public List<Group21_DIM292> Group21_DIM;
	public List<Group22_RFF297> Group22_RFF;
	public List<Group23_PCI302> Group23_PCI;
	public List<Group24_DOC316> Group24_DOC;
	public List<Group25_GOR322> Group25_GOR;
	public List<Group27_TPL346> Group27_TPL;
	public List<Group29_SGP355> Group29_SGP;
	public List<Group31_TCC364> Group31_TCC;
	public List<Group32_DGS387> Group32_DGS;
}
class C237_02419 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03420 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD418 {
	public String E8053_01;
	public C237_02419 C237_02;
	public C224_03420 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C523_01422 {
	public String E6350_01;
	public String E6353_02;
}
class EQN421 {
	public C523_01422 C523_01;
}
class C219_01424 {
	public String E8335_01;
	public String E8334_02;
}
class TMD423 {
	public C219_01424 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C502_02426 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03427 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA425 {
	public String E6311_01;
	public C502_02426 C502_02;
	public C174_03427 C174_03;
	public String E7383_04;
}
class C211_02429 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM428 {
	public String E6145_01;
	public C211_02429 C211_02;
}
class C215_02431 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_04432 {
	public String E7402_01;
	public String E7402_02;
}
class SEL430 {
	public String E9308_01;
	public C215_02431 C215_02;
	public String E4517_03;
	public C208_04432 C208_04;
}
class C222_01434 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL433 {
	public C222_01434 C222_01;
}
class C524_01436 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02437 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN435 {
	public C524_01436 C524_01;
	public C218_02437 C218_02;
}
class C239_02439 {
	public String E6246_01;
	public String E6411_02;
}
class TMP438 {
	public String E6245_01;
	public C239_02439 C239_02;
}
class C107_03441 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04442 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX440 {
	public String E4451_01;
	public String E4453_02;
	public C107_03441 C107_03;
	public C108_04442 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C506_01444 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF443 {
	public C506_01444 C506_01;
}
class C200_01447 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02448 {
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
class C528_03449 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04450 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC446 {
	public C200_01447 C200_01;
	public C203_02448 C203_02;
	public C528_03449 C528_03;
	public C554_04450 C554_04;
}
class C504_01452 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02453 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX451 {
	public C504_01452 C504_01;
	public C504_02453 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01455 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI454 {
	public C509_01455 C509_01;
	public String E5213_02;
}
class C523_01457 {
	public String E6350_01;
	public String E6353_02;
}
class EQN456 {
	public C523_01457 C523_01;
}
class C501_01459 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD458 {
	public C501_01459 C501_01;
}
class C516_01461 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA460 {
	public C516_01461 C516_01;
}
class C186_01463 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY462 {
	public C186_01463 C186_01;
}
class Group38_TCC445 {
	public TCC446 TCC;
	public CUX451 CUX;
	public PRI454 PRI;
	public EQN456 EQN;
	public PCD458 PCD;
	public List<MOA460> MOA;
	public List<QTY462> QTY;
}
class C082_02466 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03467 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04468 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05469 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07470 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD465 {
	public String E3035_01;
	public C082_02466 C082_02;
	public C058_03467 C058_03;
	public C080_04468 C080_04;
	public C059_05469 C059_05;
	public String E3164_06;
	public C819_07470 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01472 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM471 {
	public C507_01472 C507_01;
}
class C056_02475 {
	public String E3413_01;
	public String E3412_02;
}
class CTA474 {
	public String E3139_01;
	public C056_02475 C056_02;
}
class C076_01477 {
	public String E3148_01;
	public String E3155_02;
}
class COM476 {
	public C076_01477 C076_01;
}
class Group40_CTA473 {
	public CTA474 CTA;
	public List<COM476> COM;
}
class Group39_NAD464 {
	public NAD465 NAD;
	public DTM471 DTM;
	public List<Group40_CTA473> Group40_CTA;
}
class C237_02480 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class EQA479 {
	public String E8053_01;
	public C237_02480 C237_02;
}
class C523_01482 {
	public String E6350_01;
	public String E6353_02;
}
class EQN481 {
	public C523_01482 C523_01;
}
class Group41_EQA478 {
	public EQA479 EQA;
	public EQN481 EQN;
}
class C205_02485 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03486 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04487 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09488 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10489 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS484 {
	public String E8273_01;
	public C205_02485 C205_02;
	public C234_03486 C234_03;
	public C223_04487 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09488 C235_09;
	public C236_10489 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03491 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04492 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX490 {
	public String E4451_01;
	public String E4453_02;
	public C107_03491 C107_03;
	public C108_04492 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02495 {
	public String E3413_01;
	public String E3412_02;
}
class CTA494 {
	public String E3139_01;
	public C056_02495 C056_02;
}
class C076_01497 {
	public String E3148_01;
	public String E3155_02;
}
class COM496 {
	public C076_01497 C076_01;
}
class Group43_CTA493 {
	public CTA494 CTA;
	public List<COM496> COM;
}
class Group42_DGS483 {
	public DGS484 DGS;
	public List<FTX490> FTX;
	public List<Group43_CTA493> Group43_CTA;
}
class Group37_EQD417 {
	public EQD418 EQD;
	public EQN421 EQN;
	public TMD423 TMD;
	public List<MEA425> MEA;
	public List<DIM428> DIM;
	public List<SEL430> SEL;
	public List<TPL433> TPL;
	public HAN435 HAN;
	public TMP438 TMP;
	public List<FTX440> FTX;
	public List<RFF443> RFF;
	public List<Group38_TCC445> Group38_TCC;
	public List<Group39_NAD464> Group39_NAD;
	public List<Group41_EQA478> Group41_EQA;
	public List<Group42_DGS483> Group42_DGS;
}
class UNT498 {
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
	public List<DOC41> DOC;
	public List<GDS44> GDS;
	public List<Group1_LOC46> Group1_LOC;
	public List<Group2_TOD53> Group2_TOD;
	public List<Group3_RFF60> Group3_RFF;
	public List<Group4_GOR65> Group4_GOR;
	public List<Group6_CPI89> Group6_CPI;
	public List<Group7_TCC104> Group7_TCC;
	public List<Group8_TDT130> Group8_TDT;
	public List<Group11_NAD156> Group11_NAD;
	public List<Group18_GID238> Group18_GID;
	public List<Group37_EQD417> Group37_EQD;
	public UNT498 UNT;
}
class UNE499 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ500 {
	public String E0036_01;
	public String E0020_02;
}
