package cs.b2b.core.mapping.bean.edi.edifact.d96b.IFTMBF_ALL

public class EDI_ALL_IFTMBF {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","COM","Group_UNH.DTM","Group_UNH.TSR","MOA","Group_UNH.FTX","CNT","Group1_LOC","Group1_LOC.DTM","Group2_TOD","Group2_TOD.LOC","Group3_RFF","Group3_RFF.DTM","Group4_GOR","Group4_GOR.FTX","Group5_DOC","Group6_TCC","QTY","Group7_TDT","Group7_TDT.DTM","Group7_TDT.TSR","Group8_LOC","Group9_RFF","Group10_NAD","Group10_NAD.LOC","Group11_CTA","Group12_DOC","Group13_RFF","Group13_RFF.DTM","Group14_CPI","Group14_CPI.RFF","Group14_CPI.LOC","Group15_TSR","Group15_TSR.FTX","Group16_GID","Group16_GID.LOC","PIA","Group16_GID.FTX","Group17_NAD","GDS","Group18_MEA","Group19_DIM","Group20_RFF","Group20_RFF.DTM","Group21_PCI","GIN","Group22_DOC","Group22_DOC.DTM","Group23_TPL","Group24_MEA","Group25_SGP","Group26_MEA","Group27_DGS","Group27_DGS.FTX","Group28_CTA","Group29_MEA","Group30_SGP","Group31_MEA","Group32_EQD","Group32_EQD.MEA","Group32_EQD.DIM","Group32_EQD.TPL","Group32_EQD.FTX","Group33_NAD","Group34_CTA"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE345 UNE; 
	public UNZ346 UNZ; 
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
}
class C002_0137 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0238 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC36 {
	public C002_0137 C002_01;
	public C503_0238 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
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
	public List<DTM58> DTM; 
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
class C107_0367 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0468 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX66 {
	public String E4451_01;
	public String E4453_02;
	public C107_0367 C107_03;
	public C108_0468 C108_04;
	public String E3453_05;
}
class C002_0171 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0272 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC70 {
	public C002_0171 C002_01;
	public C503_0272 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_0174 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM73 {
	public C507_0174 C507_01;
}
class Group5_DOC69 {
	public DOC70 DOC; 
	public DTM73 DTM; 
}
class Group4_GOR60 {
	public GOR61 GOR; 
	public List<FTX66> FTX; 
	public List<Group5_DOC69> Group5_DOC; 
}
class C200_0177 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_0278 {
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
class C528_0379 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_0480 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC76 {
	public C200_0177 C200_01;
	public C203_0278 C203_02;
	public C528_0379 C528_03;
	public C554_0480 C554_04;
}
class C517_0282 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0383 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0484 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC81 {
	public String E3227_01;
	public C517_0282 C517_02;
	public C519_0383 C519_03;
	public C553_0484 C553_04;
	public String E5479_05;
}
class C107_0386 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0487 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX85 {
	public String E4451_01;
	public String E4453_02;
	public C107_0386 C107_03;
	public C108_0487 C108_04;
	public String E3453_05;
}
class C504_0189 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0290 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX88 {
	public C504_0189 C504_01;
	public C504_0290 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_0192 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI91 {
	public C509_0192 C509_01;
	public String E5213_02;
}
class C523_0194 {
	public String E6350_01;
	public String E6353_02;
}
class EQN93 {
	public C523_0194 C523_01;
}
class C501_0196 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD95 {
	public C501_0196 C501_01;
}
class C516_0198 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA97 {
	public C516_0198 C516_01;
}
class C186_01100 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY99 {
	public C186_01100 C186_01;
}
class Group6_TCC75 {
	public TCC76 TCC; 
	public LOC81 LOC; 
	public FTX85 FTX; 
	public CUX88 CUX; 
	public PRI91 PRI; 
	public EQN93 EQN; 
	public PCD95 PCD; 
	public List<MOA97> MOA; 
	public List<QTY99> QTY; 
}
class C220_03103 {
	public String E8067_01;
	public String E8066_02;
}
class C228_04104 {
	public String E8179_01;
	public String E8178_02;
}
class C040_05105 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_07106 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_08107 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT102 {
	public String E8051_01;
	public String E8028_02;
	public C220_03103 C220_03;
	public C228_04104 C228_04;
	public C040_05105 C040_05;
	public String E8101_06;
	public C401_07106 C401_07;
	public C222_08107 C222_08;
	public String E8281_09;
}
class C507_01109 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM108 {
	public C507_01109 C507_01;
}
class C536_01111 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02112 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03113 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04114 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR110 {
	public C536_01111 C536_01;
	public C233_02112 C233_02;
	public C537_03113 C537_03;
	public C703_04114 C703_04;
}
class C517_02117 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03118 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04119 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC116 {
	public String E3227_01;
	public C517_02117 C517_02;
	public C519_03118 C519_03;
	public C553_04119 C553_04;
	public String E5479_05;
}
class C507_01121 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM120 {
	public C507_01121 C507_01;
}
class Group8_LOC115 {
	public LOC116 LOC; 
	public DTM120 DTM; 
}
class C506_01124 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF123 {
	public C506_01124 C506_01;
}
class C507_01126 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM125 {
	public C507_01126 C507_01;
}
class Group9_RFF122 {
	public RFF123 RFF; 
	public DTM125 DTM; 
}
class Group7_TDT101 {
	public TDT102 TDT; 
	public List<DTM108> DTM; 
	public List<TSR110> TSR; 
	public List<Group8_LOC115> Group8_LOC; 
	public List<Group9_RFF122> Group9_RFF; 
}
class C082_02129 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03130 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04131 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05132 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD128 {
	public String E3035_01;
	public C082_02129 C082_02;
	public C058_03130 C058_03;
	public C080_04131 C080_04;
	public C059_05132 C059_05;
	public String E3164_06;
	public String E3229_07;
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
}
class Group15_TSR168 {
	public TSR169 TSR; 
	public RFF174 RFF; 
	public LOC176 LOC; 
	public TPL180 TPL; 
	public List<FTX182> FTX; 
}
class Group10_NAD127 {
	public NAD128 NAD; 
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
}
class C213_03188 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_04189 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class GID186 {
	public String E1496_01;
	public C213_02187 C213_02;
	public C213_03188 C213_03;
	public C213_04189 C213_04;
}
class C524_01191 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02192 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN190 {
	public C524_01191 C524_01;
	public C218_02192 C218_02;
}
class C239_02194 {
	public String E6246_01;
	public String E6411_02;
}
class TMP193 {
	public String E6245_01;
	public C239_02194 C239_02;
}
class C280_02196 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG195 {
	public String E6167_01;
	public C280_02196 C280_02;
}
class C219_01198 {
	public String E8335_01;
	public String E8334_02;
}
class TMD197 {
	public C219_01198 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02200 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03201 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04202 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC199 {
	public String E3227_01;
	public C517_02200 C517_02;
	public C519_03201 C519_03;
	public C553_04202 C553_04;
	public String E5479_05;
}
class C516_01204 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA203 {
	public C516_01204 C516_01;
}
class C212_02206 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_03207 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_04208 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_05209 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_06210 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class PIA205 {
	public String E4347_01;
	public C212_02206 C212_02;
	public C212_03207 C212_03;
	public C212_04208 C212_04;
	public C212_05209 C212_05;
	public C212_06210 C212_06;
}
class C107_03212 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04213 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX211 {
	public String E4451_01;
	public String E4453_02;
	public C107_03212 C107_03;
	public C108_04213 C108_04;
	public String E3453_05;
}
class C082_02216 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03217 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04218 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05219 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD215 {
	public String E3035_01;
	public C082_02216 C082_02;
	public C058_03217 C058_03;
	public C080_04218 C080_04;
	public C059_05219 C059_05;
	public String E3164_06;
	public String E3229_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01221 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM220 {
	public C507_01221 C507_01;
}
class Group17_NAD214 {
	public NAD215 NAD; 
	public DTM220 DTM; 
}
class C703_01223 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS222 {
	public C703_01223 C703_01;
}
class C502_02226 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03227 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA225 {
	public String E6311_01;
	public C502_02226 C502_02;
	public C174_03227 C174_03;
	public String E7383_04;
}
class C523_01229 {
	public String E6350_01;
	public String E6353_02;
}
class EQN228 {
	public C523_01229 C523_01;
}
class Group18_MEA224 {
	public MEA225 MEA; 
	public EQN228 EQN; 
}
class C211_02232 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM231 {
	public String E6145_01;
	public C211_02232 C211_02;
}
class C523_01234 {
	public String E6350_01;
	public String E6353_02;
}
class EQN233 {
	public C523_01234 C523_01;
}
class Group19_DIM230 {
	public DIM231 DIM; 
	public EQN233 EQN; 
}
class C506_01237 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF236 {
	public C506_01237 C506_01;
}
class C507_01239 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM238 {
	public C507_01239 C507_01;
}
class Group20_RFF235 {
	public RFF236 RFF; 
	public List<DTM238> DTM; 
}
class C210_02242 {
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
class C827_04243 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI241 {
	public String E4233_01;
	public C210_02242 C210_02;
	public String E8275_03;
	public C827_04243 C827_04;
}
class C506_01245 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF244 {
	public C506_01245 C506_01;
}
class C507_01247 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM246 {
	public C507_01247 C507_01;
}
class C208_02249 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03250 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04251 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05252 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06253 {
	public String E7402_01;
	public String E7402_02;
}
class GIN248 {
	public String E7405_01;
	public C208_02249 C208_02;
	public C208_03250 C208_03;
	public C208_04251 C208_04;
	public C208_05252 C208_05;
	public C208_06253 C208_06;
}
class Group21_PCI240 {
	public PCI241 PCI; 
	public RFF244 RFF; 
	public DTM246 DTM; 
	public List<GIN248> GIN; 
}
class C002_01256 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02257 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC255 {
	public C002_01256 C002_01;
	public C503_02257 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01259 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM258 {
	public C507_01259 C507_01;
}
class Group22_DOC254 {
	public DOC255 DOC; 
	public List<DTM258> DTM; 
}
class C222_01262 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL261 {
	public C222_01262 C222_01;
}
class C502_02265 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03266 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA264 {
	public String E6311_01;
	public C502_02265 C502_02;
	public C174_03266 C174_03;
	public String E7383_04;
}
class C523_01268 {
	public String E6350_01;
	public String E6353_02;
}
class EQN267 {
	public C523_01268 C523_01;
}
class Group24_MEA263 {
	public MEA264 MEA; 
	public EQN267 EQN; 
}
class Group23_TPL260 {
	public TPL261 TPL; 
	public List<Group24_MEA263> Group24_MEA; 
}
class C237_01271 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP270 {
	public C237_01271 C237_01;
	public String E7224_02;
}
class C502_02274 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03275 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA273 {
	public String E6311_01;
	public C502_02274 C502_02;
	public C174_03275 C174_03;
	public String E7383_04;
}
class C523_01277 {
	public String E6350_01;
	public String E6353_02;
}
class EQN276 {
	public C523_01277 C523_01;
}
class Group26_MEA272 {
	public MEA273 MEA; 
	public EQN276 EQN; 
}
class Group25_SGP269 {
	public SGP270 SGP; 
	public List<Group26_MEA272> Group26_MEA; 
}
class C205_02280 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03281 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04282 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09283 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10284 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS279 {
	public String E8273_01;
	public C205_02280 C205_02;
	public C234_03281 C234_03;
	public C223_04282 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09283 C235_09;
	public C236_10284 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03286 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04287 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX285 {
	public String E4451_01;
	public String E4453_02;
	public C107_03286 C107_03;
	public C108_04287 C108_04;
	public String E3453_05;
}
class C056_02290 {
	public String E3413_01;
	public String E3412_02;
}
class CTA289 {
	public String E3139_01;
	public C056_02290 C056_02;
}
class C076_01292 {
	public String E3148_01;
	public String E3155_02;
}
class COM291 {
	public C076_01292 C076_01;
}
class Group28_CTA288 {
	public CTA289 CTA; 
	public List<COM291> COM; 
}
class C502_02295 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03296 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA294 {
	public String E6311_01;
	public C502_02295 C502_02;
	public C174_03296 C174_03;
	public String E7383_04;
}
class C523_01298 {
	public String E6350_01;
	public String E6353_02;
}
class EQN297 {
	public C523_01298 C523_01;
}
class Group29_MEA293 {
	public MEA294 MEA; 
	public EQN297 EQN; 
}
class C237_01301 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP300 {
	public C237_01301 C237_01;
	public String E7224_02;
}
class C502_02304 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03305 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA303 {
	public String E6311_01;
	public C502_02304 C502_02;
	public C174_03305 C174_03;
	public String E7383_04;
}
class C523_01307 {
	public String E6350_01;
	public String E6353_02;
}
class EQN306 {
	public C523_01307 C523_01;
}
class Group31_MEA302 {
	public MEA303 MEA; 
	public EQN306 EQN; 
}
class Group30_SGP299 {
	public SGP300 SGP; 
	public List<Group31_MEA302> Group31_MEA; 
}
class Group27_DGS278 {
	public DGS279 DGS; 
	public List<FTX285> FTX; 
	public List<Group28_CTA288> Group28_CTA; 
	public List<Group29_MEA293> Group29_MEA; 
	public List<Group30_SGP299> Group30_SGP; 
}
class Group16_GID185 {
	public GID186 GID; 
	public HAN190 HAN; 
	public TMP193 TMP; 
	public RNG195 RNG; 
	public TMD197 TMD; 
	public List<LOC199> LOC; 
	public List<MOA203> MOA; 
	public List<PIA205> PIA; 
	public List<FTX211> FTX; 
	public List<Group17_NAD214> Group17_NAD; 
	public List<GDS222> GDS; 
	public List<Group18_MEA224> Group18_MEA; 
	public List<Group19_DIM230> Group19_DIM; 
	public List<Group20_RFF235> Group20_RFF; 
	public List<Group21_PCI240> Group21_PCI; 
	public List<Group22_DOC254> Group22_DOC; 
	public List<Group23_TPL260> Group23_TPL; 
	public List<Group25_SGP269> Group25_SGP; 
	public List<Group27_DGS278> Group27_DGS; 
}
class C237_02310 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03311 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD309 {
	public String E8053_01;
	public C237_02310 C237_02;
	public C224_03311 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C523_01313 {
	public String E6350_01;
	public String E6353_02;
}
class EQN312 {
	public C523_01313 C523_01;
}
class C219_01315 {
	public String E8335_01;
	public String E8334_02;
}
class TMD314 {
	public C219_01315 C219_01;
	public String E8332_02;
	public String E8341_03;
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
class C211_02320 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM319 {
	public String E6145_01;
	public C211_02320 C211_02;
}
class C222_01322 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL321 {
	public C222_01322 C222_01;
}
class C524_01324 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02325 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN323 {
	public C524_01324 C524_01;
	public C218_02325 C218_02;
}
class C239_02327 {
	public String E6246_01;
	public String E6411_02;
}
class TMP326 {
	public String E6245_01;
	public C239_02327 C239_02;
}
class C107_03329 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04330 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX328 {
	public String E4451_01;
	public String E4453_02;
	public C107_03329 C107_03;
	public C108_04330 C108_04;
	public String E3453_05;
}
class C082_02333 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03334 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04335 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05336 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD332 {
	public String E3035_01;
	public C082_02333 C082_02;
	public C058_03334 C058_03;
	public C080_04335 C080_04;
	public C059_05336 C059_05;
	public String E3164_06;
	public String E3229_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01338 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM337 {
	public C507_01338 C507_01;
}
class C056_02341 {
	public String E3413_01;
	public String E3412_02;
}
class CTA340 {
	public String E3139_01;
	public C056_02341 C056_02;
}
class C076_01343 {
	public String E3148_01;
	public String E3155_02;
}
class COM342 {
	public C076_01343 C076_01;
}
class Group34_CTA339 {
	public CTA340 CTA; 
	public List<COM342> COM; 
}
class Group33_NAD331 {
	public NAD332 NAD; 
	public DTM337 DTM; 
	public List<Group34_CTA339> Group34_CTA; 
}
class Group32_EQD308 {
	public EQD309 EQD; 
	public EQN312 EQN; 
	public TMD314 TMD; 
	public List<MEA316> MEA; 
	public List<DIM319> DIM; 
	public List<TPL321> TPL; 
	public HAN323 HAN; 
	public TMP326 TMP; 
	public List<FTX328> FTX; 
	public List<Group33_NAD331> Group33_NAD; 
}
class UNT344 {
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
	public DOC36 DOC; 
	public List<CNT39> CNT; 
	public List<Group1_LOC41> Group1_LOC; 
	public List<Group2_TOD48> Group2_TOD; 
	public List<Group3_RFF55> Group3_RFF; 
	public List<Group4_GOR60> Group4_GOR; 
	public List<Group6_TCC75> Group6_TCC; 
	public List<Group7_TDT101> Group7_TDT; 
	public List<Group10_NAD127> Group10_NAD; 
	public List<Group16_GID185> Group16_GID; 
	public List<Group32_EQD308> Group32_EQD; 
	public UNT344 UNT; 
}
class UNE345 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ346 {
	public String E0036_01;
	public String E0020_02;
}
