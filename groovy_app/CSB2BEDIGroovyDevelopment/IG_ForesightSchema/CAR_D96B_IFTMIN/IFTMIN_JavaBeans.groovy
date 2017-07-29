package cs.b2b.core.mapping.bean.edi.edifact.d96b.IFTMIN_ALL

public class EDI_ALL_IFTMIN {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","COM","Group_UNH.DTM","Group_UNH.TSR","Group_UNH.CUX","MOA","Group_UNH.FTX","CNT","Group_UNH.DOC","Group1_LOC","Group1_LOC.DTM","Group2_TOD","Group2_TOD.LOC","Group3_RFF","Group3_RFF.DTM","Group4_GOR","Group4_GOR.DTM","Group4_GOR.LOC","SEL","Group4_GOR.FTX","Group5_DOC","Group6_CPI","Group6_CPI.RFF","Group6_CPI.LOC","Group7_TCC","QTY","Group8_TDT","Group8_TDT.DTM","Group8_TDT.TSR","Group9_LOC","Group10_RFF","Group11_NAD","Group11_NAD.LOC","Group12_CTA","Group13_DOC","Group14_TCC","Group15_RFF","Group15_RFF.DTM","Group16_CPI","Group16_CPI.RFF","Group16_CPI.LOC","Group17_TSR","Group17_TSR.FTX","Group18_GID","Group18_GID.HAN","Group18_GID.LOC","PIA","Group18_GID.FTX","Group19_NAD","GDS","Group20_MEA","Group21_DIM","Group22_RFF","Group22_RFF.DTM","Group23_PCI","GIN","Group24_DOC","Group24_DOC.DTM","Group25_GOR","Group25_GOR.DTM","Group25_GOR.LOC","Group25_GOR.FTX","Group26_DOC","Group27_TPL","Group28_MEA","Group29_SGP","Group30_MEA","Group31_TCC","Group31_TCC.LOC","Group32_DGS","Group32_DGS.FTX","Group33_CTA","Group34_MEA","Group35_SGP","Group36_MEA","Group37_EQD","Group37_EQD.MEA","Group37_EQD.DIM","Group37_EQD.TPL","Group37_EQD.FTX","Group38_TCC","Group39_NAD","Group40_CTA","Group41_EQA"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE460 UNE; 
	public UNZ461 UNZ; 
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
}
class DOC41 {
	public C002_0142 C002_01;
	public C503_0243 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
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
	public String E3222_04;
}
class C553_0448 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC45 {
	public String E3227_01;
	public C517_0246 C517_02;
	public C519_0347 C519_03;
	public C553_0448 C553_04;
	public String E5479_05;
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
class SEL75 {
	public String E9308_01;
	public C215_0276 C215_02;
	public String E4517_03;
}
class C107_0378 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0479 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX77 {
	public String E4451_01;
	public String E4453_02;
	public C107_0378 C107_03;
	public C108_0479 C108_04;
	public String E3453_05;
}
class C002_0182 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0283 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC81 {
	public C002_0182 C002_01;
	public C503_0283 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_0185 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM84 {
	public C507_0185 C507_01;
}
class Group5_DOC80 {
	public DOC81 DOC; 
	public DTM84 DTM; 
}
class Group4_GOR63 {
	public GOR64 GOR; 
	public List<DTM69> DTM; 
	public List<LOC71> LOC; 
	public List<SEL75> SEL; 
	public List<FTX77> FTX; 
	public List<Group5_DOC80> Group5_DOC; 
}
class C229_0188 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_0289 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI87 {
	public C229_0188 C229_01;
	public C231_0289 C231_02;
	public String E4237_03;
}
class C506_0191 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF90 {
	public C506_0191 C506_01;
}
class C504_0193 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0294 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX92 {
	public C504_0193 C504_01;
	public C504_0294 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_0296 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0397 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0498 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC95 {
	public String E3227_01;
	public C517_0296 C517_02;
	public C519_0397 C519_03;
	public C553_0498 C553_04;
	public String E5479_05;
}
class C516_01100 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA99 {
	public C516_01100 C516_01;
}
class Group6_CPI86 {
	public CPI87 CPI; 
	public List<RFF90> RFF; 
	public CUX92 CUX; 
	public List<LOC95> LOC; 
	public List<MOA99> MOA; 
}
class C200_01103 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02104 {
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
class C528_03105 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04106 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC102 {
	public C200_01103 C200_01;
	public C203_02104 C203_02;
	public C528_03105 C528_03;
	public C554_04106 C554_04;
}
class C517_02108 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03109 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04110 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC107 {
	public String E3227_01;
	public C517_02108 C517_02;
	public C519_03109 C519_03;
	public C553_04110 C553_04;
	public String E5479_05;
}
class C107_03112 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04113 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX111 {
	public String E4451_01;
	public String E4453_02;
	public C107_03112 C107_03;
	public C108_04113 C108_04;
	public String E3453_05;
}
class C504_01115 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02116 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX114 {
	public C504_01115 C504_01;
	public C504_02116 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01118 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI117 {
	public C509_01118 C509_01;
	public String E5213_02;
}
class C523_01120 {
	public String E6350_01;
	public String E6353_02;
}
class EQN119 {
	public C523_01120 C523_01;
}
class C501_01122 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD121 {
	public C501_01122 C501_01;
}
class C516_01124 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA123 {
	public C516_01124 C516_01;
}
class C186_01126 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY125 {
	public C186_01126 C186_01;
}
class Group7_TCC101 {
	public TCC102 TCC; 
	public LOC107 LOC; 
	public FTX111 FTX; 
	public CUX114 CUX; 
	public PRI117 PRI; 
	public EQN119 EQN; 
	public PCD121 PCD; 
	public List<MOA123> MOA; 
	public List<QTY125> QTY; 
}
class C220_03129 {
	public String E8067_01;
	public String E8066_02;
}
class C228_04130 {
	public String E8179_01;
	public String E8178_02;
}
class C040_05131 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_07132 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_08133 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT128 {
	public String E8051_01;
	public String E8028_02;
	public C220_03129 C220_03;
	public C228_04130 C228_04;
	public C040_05131 C040_05;
	public String E8101_06;
	public C401_07132 C401_07;
	public C222_08133 C222_08;
	public String E8281_09;
}
class C507_01135 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM134 {
	public C507_01135 C507_01;
}
class C536_01137 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02138 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03139 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04140 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR136 {
	public C536_01137 C536_01;
	public C233_02138 C233_02;
	public C537_03139 C537_03;
	public C703_04140 C703_04;
}
class C517_02143 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03144 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04145 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC142 {
	public String E3227_01;
	public C517_02143 C517_02;
	public C519_03144 C519_03;
	public C553_04145 C553_04;
	public String E5479_05;
}
class C507_01147 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM146 {
	public C507_01147 C507_01;
}
class Group9_LOC141 {
	public LOC142 LOC; 
	public DTM146 DTM; 
}
class C506_01150 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF149 {
	public C506_01150 C506_01;
}
class C507_01152 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM151 {
	public C507_01152 C507_01;
}
class Group10_RFF148 {
	public RFF149 RFF; 
	public DTM151 DTM; 
}
class Group8_TDT127 {
	public TDT128 TDT; 
	public List<DTM134> DTM; 
	public List<TSR136> TSR; 
	public List<Group9_LOC141> Group9_LOC; 
	public List<Group10_RFF148> Group10_RFF; 
}
class C082_02155 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03156 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04157 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05158 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD154 {
	public String E3035_01;
	public C082_02155 C082_02;
	public C058_03156 C058_03;
	public C080_04157 C080_04;
	public C059_05158 C059_05;
	public String E3164_06;
	public String E3229_07;
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
class C509_01183 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI182 {
	public C509_01183 C509_01;
	public String E5213_02;
}
class C523_01185 {
	public String E6350_01;
	public String E6353_02;
}
class EQN184 {
	public C523_01185 C523_01;
}
class C501_01187 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD186 {
	public C501_01187 C501_01;
}
class C516_01189 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA188 {
	public C516_01189 C516_01;
}
class C186_01191 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY190 {
	public C186_01191 C186_01;
}
class Group14_TCC176 {
	public TCC177 TCC; 
	public PRI182 PRI; 
	public EQN184 EQN; 
	public PCD186 PCD; 
	public List<MOA188> MOA; 
	public List<QTY190> QTY; 
}
class C506_01194 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF193 {
	public C506_01194 C506_01;
}
class C507_01196 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM195 {
	public C507_01196 C507_01;
}
class Group15_RFF192 {
	public RFF193 RFF; 
	public List<DTM195> DTM; 
}
class C229_01199 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_02200 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI198 {
	public C229_01199 C229_01;
	public C231_02200 C231_02;
	public String E4237_03;
}
class C506_01202 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF201 {
	public C506_01202 C506_01;
}
class C504_01204 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02205 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX203 {
	public C504_01204 C504_01;
	public C504_02205 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_02207 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03208 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04209 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC206 {
	public String E3227_01;
	public C517_02207 C517_02;
	public C519_03208 C519_03;
	public C553_04209 C553_04;
	public String E5479_05;
}
class C516_01211 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA210 {
	public C516_01211 C516_01;
}
class Group16_CPI197 {
	public CPI198 CPI; 
	public List<RFF201> RFF; 
	public CUX203 CUX; 
	public List<LOC206> LOC; 
	public List<MOA210> MOA; 
}
class C536_01214 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02215 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03216 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04217 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR213 {
	public C536_01214 C536_01;
	public C233_02215 C233_02;
	public C537_03216 C537_03;
	public C703_04217 C703_04;
}
class C506_01219 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF218 {
	public C506_01219 C506_01;
}
class C517_02221 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03222 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04223 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC220 {
	public String E3227_01;
	public C517_02221 C517_02;
	public C519_03222 C519_03;
	public C553_04223 C553_04;
	public String E5479_05;
}
class C222_01225 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL224 {
	public C222_01225 C222_01;
}
class C107_03227 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04228 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX226 {
	public String E4451_01;
	public String E4453_02;
	public C107_03227 C107_03;
	public C108_04228 C108_04;
	public String E3453_05;
}
class Group17_TSR212 {
	public TSR213 TSR; 
	public RFF218 RFF; 
	public LOC220 LOC; 
	public TPL224 TPL; 
	public List<FTX226> FTX; 
}
class Group11_NAD153 {
	public NAD154 NAD; 
	public List<LOC159> LOC; 
	public List<MOA163> MOA; 
	public List<Group12_CTA165> Group12_CTA; 
	public List<Group13_DOC170> Group13_DOC; 
	public List<Group14_TCC176> Group14_TCC; 
	public List<Group15_RFF192> Group15_RFF; 
	public List<Group16_CPI197> Group16_CPI; 
	public List<Group17_TSR212> Group17_TSR; 
}
class C213_02231 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_03232 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_04233 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class GID230 {
	public String E1496_01;
	public C213_02231 C213_02;
	public C213_03232 C213_03;
	public C213_04233 C213_04;
}
class C524_01235 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02236 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN234 {
	public C524_01235 C524_01;
	public C218_02236 C218_02;
}
class C239_02238 {
	public String E6246_01;
	public String E6411_02;
}
class TMP237 {
	public String E6245_01;
	public C239_02238 C239_02;
}
class C280_02240 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG239 {
	public String E6167_01;
	public C280_02240 C280_02;
}
class C219_01242 {
	public String E8335_01;
	public String E8334_02;
}
class TMD241 {
	public C219_01242 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02244 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03245 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04246 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC243 {
	public String E3227_01;
	public C517_02244 C517_02;
	public C519_03245 C519_03;
	public C553_04246 C553_04;
	public String E5479_05;
}
class C516_01248 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA247 {
	public C516_01248 C516_01;
}
class C212_02250 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_03251 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_04252 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_05253 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_06254 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class PIA249 {
	public String E4347_01;
	public C212_02250 C212_02;
	public C212_03251 C212_03;
	public C212_04252 C212_04;
	public C212_05253 C212_05;
	public C212_06254 C212_06;
}
class C107_03256 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04257 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX255 {
	public String E4451_01;
	public String E4453_02;
	public C107_03256 C107_03;
	public C108_04257 C108_04;
	public String E3453_05;
}
class C280_01259 {
	public String E6411_01;
	public String E6162_02;
}
class PCD258 {
	public C280_01259 C280_01;
}
class C082_02262 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03263 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04264 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05265 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD261 {
	public String E3035_01;
	public C082_02262 C082_02;
	public C058_03263 C058_03;
	public C080_04264 C080_04;
	public C059_05265 C059_05;
	public String E3164_06;
	public String E3229_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01267 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM266 {
	public C507_01267 C507_01;
}
class Group19_NAD260 {
	public NAD261 NAD; 
	public DTM266 DTM; 
}
class C703_01269 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS268 {
	public C703_01269 C703_01;
}
class C502_02272 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03273 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA271 {
	public String E6311_01;
	public C502_02272 C502_02;
	public C174_03273 C174_03;
	public String E7383_04;
}
class C523_01275 {
	public String E6350_01;
	public String E6353_02;
}
class EQN274 {
	public C523_01275 C523_01;
}
class Group20_MEA270 {
	public MEA271 MEA; 
	public EQN274 EQN; 
}
class C211_02278 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM277 {
	public String E6145_01;
	public C211_02278 C211_02;
}
class C523_01280 {
	public String E6350_01;
	public String E6353_02;
}
class EQN279 {
	public C523_01280 C523_01;
}
class Group21_DIM276 {
	public DIM277 DIM; 
	public EQN279 EQN; 
}
class C506_01283 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF282 {
	public C506_01283 C506_01;
}
class C507_01285 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM284 {
	public C507_01285 C507_01;
}
class Group22_RFF281 {
	public RFF282 RFF; 
	public List<DTM284> DTM; 
}
class C210_02288 {
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
class C827_04289 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI287 {
	public String E4233_01;
	public C210_02288 C210_02;
	public String E8275_03;
	public C827_04289 C827_04;
}
class C506_01291 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF290 {
	public C506_01291 C506_01;
}
class C507_01293 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM292 {
	public C507_01293 C507_01;
}
class C208_02295 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03296 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04297 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05298 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06299 {
	public String E7402_01;
	public String E7402_02;
}
class GIN294 {
	public String E7405_01;
	public C208_02295 C208_02;
	public C208_03296 C208_03;
	public C208_04297 C208_04;
	public C208_05298 C208_05;
	public C208_06299 C208_06;
}
class Group23_PCI286 {
	public PCI287 PCI; 
	public RFF290 RFF; 
	public DTM292 DTM; 
	public List<GIN294> GIN; 
}
class C002_01302 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02303 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC301 {
	public C002_01302 C002_01;
	public C503_02303 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01305 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM304 {
	public C507_01305 C507_01;
}
class Group24_DOC300 {
	public DOC301 DOC; 
	public List<DTM304> DTM; 
}
class C232_02308 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_03309 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_04310 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_05311 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class GOR307 {
	public String E8323_01;
	public C232_02308 C232_02;
	public C232_03309 C232_03;
	public C232_04310 C232_04;
	public C232_05311 C232_05;
}
class C507_01313 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM312 {
	public C507_01313 C507_01;
}
class C517_02315 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03316 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04317 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC314 {
	public String E3227_01;
	public C517_02315 C517_02;
	public C519_03316 C519_03;
	public C553_04317 C553_04;
	public String E5479_05;
}
class C215_02319 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class SEL318 {
	public String E9308_01;
	public C215_02319 C215_02;
	public String E4517_03;
}
class C107_03321 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04322 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX320 {
	public String E4451_01;
	public String E4453_02;
	public C107_03321 C107_03;
	public C108_04322 C108_04;
	public String E3453_05;
}
class C002_01325 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02326 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC324 {
	public C002_01325 C002_01;
	public C503_02326 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01328 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM327 {
	public C507_01328 C507_01;
}
class Group26_DOC323 {
	public DOC324 DOC; 
	public DTM327 DTM; 
}
class Group25_GOR306 {
	public GOR307 GOR; 
	public List<DTM312> DTM; 
	public List<LOC314> LOC; 
	public List<SEL318> SEL; 
	public List<FTX320> FTX; 
	public List<Group26_DOC323> Group26_DOC; 
}
class C222_01331 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL330 {
	public C222_01331 C222_01;
}
class C502_02334 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03335 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA333 {
	public String E6311_01;
	public C502_02334 C502_02;
	public C174_03335 C174_03;
	public String E7383_04;
}
class C523_01337 {
	public String E6350_01;
	public String E6353_02;
}
class EQN336 {
	public C523_01337 C523_01;
}
class Group28_MEA332 {
	public MEA333 MEA; 
	public EQN336 EQN; 
}
class Group27_TPL329 {
	public TPL330 TPL; 
	public List<Group28_MEA332> Group28_MEA; 
}
class C237_01340 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP339 {
	public C237_01340 C237_01;
	public String E7224_02;
}
class C502_02343 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03344 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA342 {
	public String E6311_01;
	public C502_02343 C502_02;
	public C174_03344 C174_03;
	public String E7383_04;
}
class C523_01346 {
	public String E6350_01;
	public String E6353_02;
}
class EQN345 {
	public C523_01346 C523_01;
}
class Group30_MEA341 {
	public MEA342 MEA; 
	public EQN345 EQN; 
}
class Group29_SGP338 {
	public SGP339 SGP; 
	public List<Group30_MEA341> Group30_MEA; 
}
class C200_01349 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02350 {
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
class C528_03351 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04352 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC348 {
	public C200_01349 C200_01;
	public C203_02350 C203_02;
	public C528_03351 C528_03;
	public C554_04352 C554_04;
}
class C504_01354 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02355 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX353 {
	public C504_01354 C504_01;
	public C504_02355 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_01357 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI356 {
	public C509_01357 C509_01;
	public String E5213_02;
}
class C523_01359 {
	public String E6350_01;
	public String E6353_02;
}
class EQN358 {
	public C523_01359 C523_01;
}
class C501_01361 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD360 {
	public C501_01361 C501_01;
}
class C516_01363 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA362 {
	public C516_01363 C516_01;
}
class C186_01365 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY364 {
	public C186_01365 C186_01;
}
class C517_02367 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03368 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04369 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC366 {
	public String E3227_01;
	public C517_02367 C517_02;
	public C519_03368 C519_03;
	public C553_04369 C553_04;
	public String E5479_05;
}
class Group31_TCC347 {
	public TCC348 TCC; 
	public CUX353 CUX; 
	public PRI356 PRI; 
	public EQN358 EQN; 
	public PCD360 PCD; 
	public List<MOA362> MOA; 
	public List<QTY364> QTY; 
	public List<LOC366> LOC; 
}
class C205_02372 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03373 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04374 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09375 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10376 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS371 {
	public String E8273_01;
	public C205_02372 C205_02;
	public C234_03373 C234_03;
	public C223_04374 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09375 C235_09;
	public C236_10376 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03378 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04379 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX377 {
	public String E4451_01;
	public String E4453_02;
	public C107_03378 C107_03;
	public C108_04379 C108_04;
	public String E3453_05;
}
class C056_02382 {
	public String E3413_01;
	public String E3412_02;
}
class CTA381 {
	public String E3139_01;
	public C056_02382 C056_02;
}
class C076_01384 {
	public String E3148_01;
	public String E3155_02;
}
class COM383 {
	public C076_01384 C076_01;
}
class Group33_CTA380 {
	public CTA381 CTA; 
	public List<COM383> COM; 
}
class C502_02387 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03388 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA386 {
	public String E6311_01;
	public C502_02387 C502_02;
	public C174_03388 C174_03;
	public String E7383_04;
}
class C523_01390 {
	public String E6350_01;
	public String E6353_02;
}
class EQN389 {
	public C523_01390 C523_01;
}
class Group34_MEA385 {
	public MEA386 MEA; 
	public EQN389 EQN; 
}
class C237_01393 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP392 {
	public C237_01393 C237_01;
	public String E7224_02;
}
class C502_02396 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03397 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA395 {
	public String E6311_01;
	public C502_02396 C502_02;
	public C174_03397 C174_03;
	public String E7383_04;
}
class C523_01399 {
	public String E6350_01;
	public String E6353_02;
}
class EQN398 {
	public C523_01399 C523_01;
}
class Group36_MEA394 {
	public MEA395 MEA; 
	public EQN398 EQN; 
}
class Group35_SGP391 {
	public SGP392 SGP; 
	public List<Group36_MEA394> Group36_MEA; 
}
class Group32_DGS370 {
	public DGS371 DGS; 
	public List<FTX377> FTX; 
	public List<Group33_CTA380> Group33_CTA; 
	public List<Group34_MEA385> Group34_MEA; 
	public List<Group35_SGP391> Group35_SGP; 
}
class Group18_GID229 {
	public GID230 GID; 
	public List<HAN234> HAN; 
	public TMP237 TMP; 
	public RNG239 RNG; 
	public TMD241 TMD; 
	public List<LOC243> LOC; 
	public List<MOA247> MOA; 
	public List<PIA249> PIA; 
	public List<FTX255> FTX; 
	public PCD258 PCD; 
	public List<Group19_NAD260> Group19_NAD; 
	public List<GDS268> GDS; 
	public List<Group20_MEA270> Group20_MEA; 
	public List<Group21_DIM276> Group21_DIM; 
	public List<Group22_RFF281> Group22_RFF; 
	public List<Group23_PCI286> Group23_PCI; 
	public List<Group24_DOC300> Group24_DOC; 
	public List<Group25_GOR306> Group25_GOR; 
	public List<Group27_TPL329> Group27_TPL; 
	public List<Group29_SGP338> Group29_SGP; 
	public List<Group31_TCC347> Group31_TCC; 
	public List<Group32_DGS370> Group32_DGS; 
}
class C237_02402 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03403 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD401 {
	public String E8053_01;
	public C237_02402 C237_02;
	public C224_03403 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C523_01405 {
	public String E6350_01;
	public String E6353_02;
}
class EQN404 {
	public C523_01405 C523_01;
}
class C219_01407 {
	public String E8335_01;
	public String E8334_02;
}
class TMD406 {
	public C219_01407 C219_01;
	public String E8332_02;
	public String E8341_03;
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
class C211_02412 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM411 {
	public String E6145_01;
	public C211_02412 C211_02;
}
class C215_02414 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class SEL413 {
	public String E9308_01;
	public C215_02414 C215_02;
	public String E4517_03;
}
class C222_01416 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL415 {
	public C222_01416 C222_01;
}
class C524_01418 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02419 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN417 {
	public C524_01418 C524_01;
	public C218_02419 C218_02;
}
class C239_02421 {
	public String E6246_01;
	public String E6411_02;
}
class TMP420 {
	public String E6245_01;
	public C239_02421 C239_02;
}
class C107_03423 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04424 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX422 {
	public String E4451_01;
	public String E4453_02;
	public C107_03423 C107_03;
	public C108_04424 C108_04;
	public String E3453_05;
}
class C200_01427 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_02428 {
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
class C528_03429 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_04430 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC426 {
	public C200_01427 C200_01;
	public C203_02428 C203_02;
	public C528_03429 C528_03;
	public C554_04430 C554_04;
}
class C509_01432 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI431 {
	public C509_01432 C509_01;
	public String E5213_02;
}
class C523_01434 {
	public String E6350_01;
	public String E6353_02;
}
class EQN433 {
	public C523_01434 C523_01;
}
class C501_01436 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD435 {
	public C501_01436 C501_01;
}
class C516_01438 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA437 {
	public C516_01438 C516_01;
}
class C186_01440 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY439 {
	public C186_01440 C186_01;
}
class Group38_TCC425 {
	public TCC426 TCC; 
	public PRI431 PRI; 
	public EQN433 EQN; 
	public PCD435 PCD; 
	public List<MOA437> MOA; 
	public List<QTY439> QTY; 
}
class C082_02443 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03444 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04445 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05446 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD442 {
	public String E3035_01;
	public C082_02443 C082_02;
	public C058_03444 C058_03;
	public C080_04445 C080_04;
	public C059_05446 C059_05;
	public String E3164_06;
	public String E3229_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01448 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM447 {
	public C507_01448 C507_01;
}
class C056_02451 {
	public String E3413_01;
	public String E3412_02;
}
class CTA450 {
	public String E3139_01;
	public C056_02451 C056_02;
}
class C076_01453 {
	public String E3148_01;
	public String E3155_02;
}
class COM452 {
	public C076_01453 C076_01;
}
class Group40_CTA449 {
	public CTA450 CTA; 
	public List<COM452> COM; 
}
class Group39_NAD441 {
	public NAD442 NAD; 
	public DTM447 DTM; 
	public List<Group40_CTA449> Group40_CTA; 
}
class C237_02456 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class EQA455 {
	public String E8053_01;
	public C237_02456 C237_02;
}
class C523_01458 {
	public String E6350_01;
	public String E6353_02;
}
class EQN457 {
	public C523_01458 C523_01;
}
class Group41_EQA454 {
	public EQA455 EQA; 
	public EQN457 EQN; 
}
class Group37_EQD400 {
	public EQD401 EQD; 
	public EQN404 EQN; 
	public TMD406 TMD; 
	public List<MEA408> MEA; 
	public List<DIM411> DIM; 
	public List<SEL413> SEL; 
	public List<TPL415> TPL; 
	public HAN417 HAN; 
	public TMP420 TMP; 
	public List<FTX422> FTX; 
	public List<Group38_TCC425> Group38_TCC; 
	public List<Group39_NAD441> Group39_NAD; 
	public List<Group41_EQA454> Group41_EQA; 
}
class UNT459 {
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
	public List<Group1_LOC44> Group1_LOC; 
	public List<Group2_TOD51> Group2_TOD; 
	public List<Group3_RFF58> Group3_RFF; 
	public List<Group4_GOR63> Group4_GOR; 
	public List<Group6_CPI86> Group6_CPI; 
	public List<Group7_TCC101> Group7_TCC; 
	public List<Group8_TDT127> Group8_TDT; 
	public List<Group11_NAD153> Group11_NAD; 
	public List<Group18_GID229> Group18_GID; 
	public List<Group37_EQD400> Group37_EQD; 
	public UNT459 UNT; 
}
class UNE460 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ461 {
	public String E0036_01;
	public String E0020_02;
}
