package cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMBF

public class EDI_IFTMBF {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","COM","Group_UNH.DTM","Group_UNH.TSR","MOA","Group_UNH.FTX","CNT","GDS","Group1_LOC","Group1_LOC.DTM","Group2_TOD","Group2_TOD.LOC","Group3_RFF","Group3_RFF.DTM","Group4_GOR","Group4_GOR.FTX","Group5_DOC","Group6_TCC","QTY","Group7_TDT","Group7_TDT.DTM","Group7_TDT.TSR","Group8_LOC","Group8_LOC.DTM","Group9_RFF","Group10_NAD","Group10_NAD.LOC","Group11_CTA","Group12_DOC","Group13_RFF","Group13_RFF.DTM","Group14_CPI","Group14_CPI.RFF","Group14_CPI.LOC","Group15_TSR","Group15_TSR.FTX","Group16_GID","Group16_GID.LOC","PIA","Group16_GID.FTX","Group16_GID.PCD","Group17_NAD","Group18_MEA","Group19_DIM","Group20_RFF","Group20_RFF.DTM","Group21_PCI","GIN","Group22_DOC","Group22_DOC.DTM","Group23_TPL","Group24_MEA","Group25_SGP","Group26_MEA","Group27_DGS","Group27_DGS.FTX","Group28_CTA","Group29_MEA","Group30_SGP","Group31_MEA","Group32_EQD","Group32_EQD.MEA","Group32_EQD.DIM","Group32_EQD.TPL","Group32_EQD.FTX","Group32_EQD.RFF","Group33_NAD","Group34_CTA","Group35_DGS","Group35_DGS.FTX","Group36_CTA"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE368 UNE; 
	public UNZ369 UNZ; 
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
class C516_0132 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA31 {
	public C516_0132 C516_01;
}
class C107_0334 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0435 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX33 {
	public String E4451_01;
	public String E4453_02;
	public C107_0334 C107_03;
	public C108_0435 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C270_0137 {
	public String E6069_01;
	public String E6066_02;
	public String E6411_03;
}
class CNT36 {
	public C270_0137 C270_01;
}
class C703_0139 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS38 {
	public C703_0139 C703_01;
}
class C517_0242 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0343 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0444 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC41 {
	public String E3227_01;
	public C517_0242 C517_02;
	public C519_0343 C519_03;
	public C553_0444 C553_04;
	public String E5479_05;
}
class C507_0146 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM45 {
	public C507_0146 C507_01;
}
class Group1_LOC40 {
	public LOC41 LOC; 
	public List<DTM45> DTM; 
}
class C100_0349 {
	public String E4053_01;
	public String E1131_02;
	public String E3055_03;
	public String E4052_04;
	public String E4052_05;
}
class TOD48 {
	public String E4055_01;
	public String E4215_02;
	public C100_0349 C100_03;
}
class C517_0251 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0352 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0453 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC50 {
	public String E3227_01;
	public C517_0251 C517_02;
	public C519_0352 C519_03;
	public C553_0453 C553_04;
	public String E5479_05;
}
class Group2_TOD47 {
	public TOD48 TOD; 
	public List<LOC50> LOC; 
}
class C506_0156 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF55 {
	public C506_0156 C506_01;
}
class C507_0158 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM57 {
	public C507_0158 C507_01;
}
class Group3_RFF54 {
	public RFF55 RFF; 
	public List<DTM57> DTM; 
}
class C232_0261 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0362 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0463 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class C232_0564 {
	public String E9415_01;
	public String E9411_02;
	public String E9417_03;
	public String E9353_04;
}
class GOR60 {
	public String E8323_01;
	public C232_0261 C232_02;
	public C232_0362 C232_03;
	public C232_0463 C232_04;
	public C232_0564 C232_05;
}
class C107_0366 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0467 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX65 {
	public String E4451_01;
	public String E4453_02;
	public C107_0366 C107_03;
	public C108_0467 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C002_0170 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0271 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC69 {
	public C002_0170 C002_01;
	public C503_0271 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_0173 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM72 {
	public C507_0173 C507_01;
}
class Group5_DOC68 {
	public DOC69 DOC; 
	public DTM72 DTM; 
}
class Group4_GOR59 {
	public GOR60 GOR; 
	public List<FTX65> FTX; 
	public List<Group5_DOC68> Group5_DOC; 
}
class C200_0176 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_0277 {
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
class C528_0378 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_0479 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC75 {
	public C200_0176 C200_01;
	public C203_0277 C203_02;
	public C528_0378 C528_03;
	public C554_0479 C554_04;
}
class C517_0281 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0382 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0483 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC80 {
	public String E3227_01;
	public C517_0281 C517_02;
	public C519_0382 C519_03;
	public C553_0483 C553_04;
	public String E5479_05;
}
class C107_0385 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0486 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX84 {
	public String E4451_01;
	public String E4453_02;
	public C107_0385 C107_03;
	public C108_0486 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C504_0188 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0289 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX87 {
	public C504_0188 C504_01;
	public C504_0289 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_0191 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI90 {
	public C509_0191 C509_01;
	public String E5213_02;
}
class C523_0193 {
	public String E6350_01;
	public String E6353_02;
}
class EQN92 {
	public C523_0193 C523_01;
}
class C501_0195 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD94 {
	public C501_0195 C501_01;
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
class C186_0199 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY98 {
	public C186_0199 C186_01;
}
class Group6_TCC74 {
	public TCC75 TCC; 
	public LOC80 LOC; 
	public FTX84 FTX; 
	public CUX87 CUX; 
	public PRI90 PRI; 
	public EQN92 EQN; 
	public PCD94 PCD; 
	public List<MOA96> MOA; 
	public List<QTY98> QTY; 
}
class C220_03102 {
	public String E8067_01;
	public String E8066_02;
}
class C228_04103 {
	public String E8179_01;
	public String E8178_02;
}
class C040_05104 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_07105 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_08106 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT101 {
	public String E8051_01;
	public String E8028_02;
	public C220_03102 C220_03;
	public C228_04103 C228_04;
	public C040_05104 C040_05;
	public String E8101_06;
	public C401_07105 C401_07;
	public C222_08106 C222_08;
	public String E8281_09;
}
class C507_01108 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM107 {
	public C507_01108 C507_01;
}
class C536_01110 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02111 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03112 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04113 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR109 {
	public C536_01110 C536_01;
	public C233_02111 C233_02;
	public C537_03112 C537_03;
	public C703_04113 C703_04;
}
class C517_02116 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03117 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04118 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC115 {
	public String E3227_01;
	public C517_02116 C517_02;
	public C519_03117 C519_03;
	public C553_04118 C553_04;
	public String E5479_05;
}
class C507_01120 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM119 {
	public C507_01120 C507_01;
}
class Group8_LOC114 {
	public LOC115 LOC; 
	public List<DTM119> DTM; 
}
class C506_01123 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF122 {
	public C506_01123 C506_01;
}
class C507_01125 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM124 {
	public C507_01125 C507_01;
}
class Group9_RFF121 {
	public RFF122 RFF; 
	public DTM124 DTM; 
}
class Group7_TDT100 {
	public TDT101 TDT; 
	public List<DTM107> DTM; 
	public List<TSR109> TSR; 
	public List<Group8_LOC114> Group8_LOC; 
	public List<Group9_RFF121> Group9_RFF; 
}
class C082_02128 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03129 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04130 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05131 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07132 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD127 {
	public String E3035_01;
	public C082_02128 C082_02;
	public C058_03129 C058_03;
	public C080_04130 C080_04;
	public C059_05131 C059_05;
	public String E3164_06;
	public C819_07132 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C517_02134 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03135 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04136 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC133 {
	public String E3227_01;
	public C517_02134 C517_02;
	public C519_03135 C519_03;
	public C553_04136 C553_04;
	public String E5479_05;
}
class C056_02139 {
	public String E3413_01;
	public String E3412_02;
}
class CTA138 {
	public String E3139_01;
	public C056_02139 C056_02;
}
class C076_01141 {
	public String E3148_01;
	public String E3155_02;
}
class COM140 {
	public C076_01141 C076_01;
}
class Group11_CTA137 {
	public CTA138 CTA; 
	public List<COM140> COM; 
}
class C002_01144 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02145 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC143 {
	public C002_01144 C002_01;
	public C503_02145 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01147 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM146 {
	public C507_01147 C507_01;
}
class Group12_DOC142 {
	public DOC143 DOC; 
	public DTM146 DTM; 
}
class C506_01150 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
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
class Group13_RFF148 {
	public RFF149 RFF; 
	public List<DTM151> DTM; 
}
class C229_01155 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_02156 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI154 {
	public C229_01155 C229_01;
	public C231_02156 C231_02;
	public String E4237_03;
}
class C506_01158 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF157 {
	public C506_01158 C506_01;
}
class C504_01160 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02161 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX159 {
	public C504_01160 C504_01;
	public C504_02161 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_02163 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03164 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04165 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC162 {
	public String E3227_01;
	public C517_02163 C517_02;
	public C519_03164 C519_03;
	public C553_04165 C553_04;
	public String E5479_05;
}
class C516_01167 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA166 {
	public C516_01167 C516_01;
}
class Group14_CPI153 {
	public CPI154 CPI; 
	public List<RFF157> RFF; 
	public CUX159 CUX; 
	public List<LOC162> LOC; 
	public List<MOA166> MOA; 
}
class C536_01170 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02171 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03172 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04173 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR169 {
	public C536_01170 C536_01;
	public C233_02171 C233_02;
	public C537_03172 C537_03;
	public C703_04173 C703_04;
}
class C506_01175 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF174 {
	public C506_01175 C506_01;
}
class C517_02177 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03178 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04179 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC176 {
	public String E3227_01;
	public C517_02177 C517_02;
	public C519_03178 C519_03;
	public C553_04179 C553_04;
	public String E5479_05;
}
class C222_01181 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL180 {
	public C222_01181 C222_01;
}
class C107_03183 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04184 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX182 {
	public String E4451_01;
	public String E4453_02;
	public C107_03183 C107_03;
	public C108_04184 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class Group15_TSR168 {
	public TSR169 TSR; 
	public RFF174 RFF; 
	public LOC176 LOC; 
	public TPL180 TPL; 
	public List<FTX182> FTX; 
}
class Group10_NAD126 {
	public NAD127 NAD; 
	public List<LOC133> LOC; 
	public List<Group11_CTA137> Group11_CTA; 
	public List<Group12_DOC142> Group12_DOC; 
	public List<Group13_RFF148> Group13_RFF; 
	public List<Group14_CPI153> Group14_CPI; 
	public List<Group15_TSR168> Group15_TSR; 
}
class C213_02187 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_03188 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_04189 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_05190 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_06191 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class GID186 {
	public String E1496_01;
	public C213_02187 C213_02;
	public C213_03188 C213_03;
	public C213_04189 C213_04;
	public C213_05190 C213_05;
	public C213_06191 C213_06;
}
class C524_01193 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02194 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN192 {
	public C524_01193 C524_01;
	public C218_02194 C218_02;
}
class C239_02196 {
	public String E6246_01;
	public String E6411_02;
}
class TMP195 {
	public String E6245_01;
	public C239_02196 C239_02;
}
class C280_02198 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG197 {
	public String E6167_01;
	public C280_02198 C280_02;
}
class C219_01200 {
	public String E8335_01;
	public String E8334_02;
}
class TMD199 {
	public C219_01200 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02202 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03203 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04204 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC201 {
	public String E3227_01;
	public C517_02202 C517_02;
	public C519_03203 C519_03;
	public C553_04204 C553_04;
	public String E5479_05;
}
class C516_01206 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA205 {
	public C516_01206 C516_01;
}
class C212_02208 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_03209 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_04210 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_05211 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_06212 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class PIA207 {
	public String E4347_01;
	public C212_02208 C212_02;
	public C212_03209 C212_03;
	public C212_04210 C212_04;
	public C212_05211 C212_05;
	public C212_06212 C212_06;
}
class C107_03214 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04215 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX213 {
	public String E4451_01;
	public String E4453_02;
	public C107_03214 C107_03;
	public C108_04215 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C501_01217 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD216 {
	public C501_01217 C501_01;
}
class C082_02220 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03221 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04222 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05223 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07224 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD219 {
	public String E3035_01;
	public C082_02220 C082_02;
	public C058_03221 C058_03;
	public C080_04222 C080_04;
	public C059_05223 C059_05;
	public String E3164_06;
	public C819_07224 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01226 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM225 {
	public C507_01226 C507_01;
}
class Group17_NAD218 {
	public NAD219 NAD; 
	public DTM225 DTM; 
}
class C703_01228 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS227 {
	public C703_01228 C703_01;
}
class C502_02231 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03232 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA230 {
	public String E6311_01;
	public C502_02231 C502_02;
	public C174_03232 C174_03;
	public String E7383_04;
}
class C523_01234 {
	public String E6350_01;
	public String E6353_02;
}
class EQN233 {
	public C523_01234 C523_01;
}
class Group18_MEA229 {
	public MEA230 MEA; 
	public EQN233 EQN; 
}
class C211_02237 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM236 {
	public String E6145_01;
	public C211_02237 C211_02;
}
class C523_01239 {
	public String E6350_01;
	public String E6353_02;
}
class EQN238 {
	public C523_01239 C523_01;
}
class Group19_DIM235 {
	public DIM236 DIM; 
	public EQN238 EQN; 
}
class C506_01242 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF241 {
	public C506_01242 C506_01;
}
class C507_01244 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM243 {
	public C507_01244 C507_01;
}
class Group20_RFF240 {
	public RFF241 RFF; 
	public List<DTM243> DTM; 
}
class C210_02247 {
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
class C827_04248 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI246 {
	public String E4233_01;
	public C210_02247 C210_02;
	public String E8275_03;
	public C827_04248 C827_04;
}
class C506_01250 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF249 {
	public C506_01250 C506_01;
}
class C507_01252 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM251 {
	public C507_01252 C507_01;
}
class C208_02254 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03255 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04256 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05257 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06258 {
	public String E7402_01;
	public String E7402_02;
}
class GIN253 {
	public String E7405_01;
	public C208_02254 C208_02;
	public C208_03255 C208_03;
	public C208_04256 C208_04;
	public C208_05257 C208_05;
	public C208_06258 C208_06;
}
class Group21_PCI245 {
	public PCI246 PCI; 
	public RFF249 RFF; 
	public DTM251 DTM; 
	public List<GIN253> GIN; 
}
class C002_01261 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02262 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC260 {
	public C002_01261 C002_01;
	public C503_02262 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01264 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM263 {
	public C507_01264 C507_01;
}
class Group22_DOC259 {
	public DOC260 DOC; 
	public List<DTM263> DTM; 
}
class C222_01267 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL266 {
	public C222_01267 C222_01;
}
class C502_02270 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03271 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA269 {
	public String E6311_01;
	public C502_02270 C502_02;
	public C174_03271 C174_03;
	public String E7383_04;
}
class C523_01273 {
	public String E6350_01;
	public String E6353_02;
}
class EQN272 {
	public C523_01273 C523_01;
}
class Group24_MEA268 {
	public MEA269 MEA; 
	public EQN272 EQN; 
}
class Group23_TPL265 {
	public TPL266 TPL; 
	public List<Group24_MEA268> Group24_MEA; 
}
class C237_01276 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP275 {
	public C237_01276 C237_01;
	public String E7224_02;
}
class C502_02279 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03280 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA278 {
	public String E6311_01;
	public C502_02279 C502_02;
	public C174_03280 C174_03;
	public String E7383_04;
}
class C523_01282 {
	public String E6350_01;
	public String E6353_02;
}
class EQN281 {
	public C523_01282 C523_01;
}
class Group26_MEA277 {
	public MEA278 MEA; 
	public EQN281 EQN; 
}
class Group25_SGP274 {
	public SGP275 SGP; 
	public List<Group26_MEA277> Group26_MEA; 
}
class C205_02285 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03286 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04287 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09288 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10289 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS284 {
	public String E8273_01;
	public C205_02285 C205_02;
	public C234_03286 C234_03;
	public C223_04287 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09288 C235_09;
	public C236_10289 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03291 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04292 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX290 {
	public String E4451_01;
	public String E4453_02;
	public C107_03291 C107_03;
	public C108_04292 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02295 {
	public String E3413_01;
	public String E3412_02;
}
class CTA294 {
	public String E3139_01;
	public C056_02295 C056_02;
}
class C076_01297 {
	public String E3148_01;
	public String E3155_02;
}
class COM296 {
	public C076_01297 C076_01;
}
class Group28_CTA293 {
	public CTA294 CTA; 
	public List<COM296> COM; 
}
class C502_02300 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03301 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA299 {
	public String E6311_01;
	public C502_02300 C502_02;
	public C174_03301 C174_03;
	public String E7383_04;
}
class C523_01303 {
	public String E6350_01;
	public String E6353_02;
}
class EQN302 {
	public C523_01303 C523_01;
}
class Group29_MEA298 {
	public MEA299 MEA; 
	public EQN302 EQN; 
}
class C237_01306 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP305 {
	public C237_01306 C237_01;
	public String E7224_02;
}
class C502_02309 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03310 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA308 {
	public String E6311_01;
	public C502_02309 C502_02;
	public C174_03310 C174_03;
	public String E7383_04;
}
class C523_01312 {
	public String E6350_01;
	public String E6353_02;
}
class EQN311 {
	public C523_01312 C523_01;
}
class Group31_MEA307 {
	public MEA308 MEA; 
	public EQN311 EQN; 
}
class Group30_SGP304 {
	public SGP305 SGP; 
	public List<Group31_MEA307> Group31_MEA; 
}
class Group27_DGS283 {
	public DGS284 DGS; 
	public List<FTX290> FTX; 
	public List<Group28_CTA293> Group28_CTA; 
	public List<Group29_MEA298> Group29_MEA; 
	public List<Group30_SGP304> Group30_SGP; 
}
class Group16_GID185 {
	public GID186 GID; 
	public HAN192 HAN; 
	public TMP195 TMP; 
	public RNG197 RNG; 
	public TMD199 TMD; 
	public List<LOC201> LOC; 
	public List<MOA205> MOA; 
	public List<PIA207> PIA; 
	public List<FTX213> FTX; 
	public List<PCD216> PCD; 
	public List<Group17_NAD218> Group17_NAD; 
	public List<GDS227> GDS; 
	public List<Group18_MEA229> Group18_MEA; 
	public List<Group19_DIM235> Group19_DIM; 
	public List<Group20_RFF240> Group20_RFF; 
	public List<Group21_PCI245> Group21_PCI; 
	public List<Group22_DOC259> Group22_DOC; 
	public List<Group23_TPL265> Group23_TPL; 
	public List<Group25_SGP274> Group25_SGP; 
	public List<Group27_DGS283> Group27_DGS; 
}
class C237_02315 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03316 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD314 {
	public String E8053_01;
	public C237_02315 C237_02;
	public C224_03316 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C523_01318 {
	public String E6350_01;
	public String E6353_02;
}
class EQN317 {
	public C523_01318 C523_01;
}
class C219_01320 {
	public String E8335_01;
	public String E8334_02;
}
class TMD319 {
	public C219_01320 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C502_02322 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03323 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA321 {
	public String E6311_01;
	public C502_02322 C502_02;
	public C174_03323 C174_03;
	public String E7383_04;
}
class C211_02325 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM324 {
	public String E6145_01;
	public C211_02325 C211_02;
}
class C222_01327 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL326 {
	public C222_01327 C222_01;
}
class C524_01329 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02330 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN328 {
	public C524_01329 C524_01;
	public C218_02330 C218_02;
}
class C239_02332 {
	public String E6246_01;
	public String E6411_02;
}
class TMP331 {
	public String E6245_01;
	public C239_02332 C239_02;
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
class C506_01337 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF336 {
	public C506_01337 C506_01;
}
class C082_02340 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03341 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04342 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05343 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07344 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD339 {
	public String E3035_01;
	public C082_02340 C082_02;
	public C058_03341 C058_03;
	public C080_04342 C080_04;
	public C059_05343 C059_05;
	public String E3164_06;
	public C819_07344 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01346 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM345 {
	public C507_01346 C507_01;
}
class C056_02349 {
	public String E3413_01;
	public String E3412_02;
}
class CTA348 {
	public String E3139_01;
	public C056_02349 C056_02;
}
class C076_01351 {
	public String E3148_01;
	public String E3155_02;
}
class COM350 {
	public C076_01351 C076_01;
}
class Group34_CTA347 {
	public CTA348 CTA; 
	public List<COM350> COM; 
}
class Group33_NAD338 {
	public NAD339 NAD; 
	public DTM345 DTM; 
	public List<Group34_CTA347> Group34_CTA; 
}
class C205_02354 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03355 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04356 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09357 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10358 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS353 {
	public String E8273_01;
	public C205_02354 C205_02;
	public C234_03355 C234_03;
	public C223_04356 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09357 C235_09;
	public C236_10358 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03360 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04361 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX359 {
	public String E4451_01;
	public String E4453_02;
	public C107_03360 C107_03;
	public C108_04361 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02364 {
	public String E3413_01;
	public String E3412_02;
}
class CTA363 {
	public String E3139_01;
	public C056_02364 C056_02;
}
class C076_01366 {
	public String E3148_01;
	public String E3155_02;
}
class COM365 {
	public C076_01366 C076_01;
}
class Group36_CTA362 {
	public CTA363 CTA; 
	public List<COM365> COM; 
}
class Group35_DGS352 {
	public DGS353 DGS; 
	public List<FTX359> FTX; 
	public List<Group36_CTA362> Group36_CTA; 
}
class Group32_EQD313 {
	public EQD314 EQD; 
	public EQN317 EQN; 
	public TMD319 TMD; 
	public List<MEA321> MEA; 
	public List<DIM324> DIM; 
	public List<TPL326> TPL; 
	public HAN328 HAN; 
	public TMP331 TMP; 
	public List<FTX333> FTX; 
	public List<RFF336> RFF; 
	public List<Group33_NAD338> Group33_NAD; 
	public List<Group35_DGS352> Group35_DGS; 
}
class UNT367 {
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
	public List<MOA31> MOA; 
	public List<FTX33> FTX; 
	public List<CNT36> CNT; 
	public List<GDS38> GDS; 
	public List<Group1_LOC40> Group1_LOC; 
	public List<Group2_TOD47> Group2_TOD; 
	public List<Group3_RFF54> Group3_RFF; 
	public List<Group4_GOR59> Group4_GOR; 
	public List<Group6_TCC74> Group6_TCC; 
	public List<Group7_TDT100> Group7_TDT; 
	public List<Group10_NAD126> Group10_NAD; 
	public List<Group16_GID185> Group16_GID; 
	public List<Group32_EQD313> Group32_EQD; 
	public UNT367 UNT; 
}
class UNE368 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ369 {
	public String E0036_01;
	public String E0020_02;
}
