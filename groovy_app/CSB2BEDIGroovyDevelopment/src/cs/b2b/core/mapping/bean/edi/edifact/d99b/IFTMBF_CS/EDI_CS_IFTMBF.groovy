package cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMBF_CS

public class EDI_CS_IFTMBF {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","COM","Group_UNH.DTM","Group_UNH.TSR","MOA","Group_UNH.FTX","CNT","GDS","Group1_LOC","Group1_LOC.DTM","Group2_TOD","Group2_TOD.LOC","Group3_RFF","Group3_RFF.DTM","Group4_GOR","Group4_GOR.FTX","Group5_DOC","Group6_TCC","QTY","Group7_TDT","Group7_TDT.DTM","Group7_TDT.TSR","Group8_LOC","Group8_LOC.DTM","Group9_RFF","Group10_NAD","Group10_NAD.LOC","Group11_CTA","Group12_DOC","Group13_RFF","Group13_RFF.DTM","Group14_CPI","Group14_CPI.RFF","Group14_CPI.LOC","Group15_TSR","Group15_TSR.FTX","Group16_GID","Group16_GID.LOC","PIA","Group16_GID.FTX","Group16_GID.PCD","Group17_NAD","Group18_MEA","Group19_DIM","Group20_RFF","Group20_RFF.DTM","Group21_PCI","GIN","Group22_DOC","Group22_DOC.DTM","Group23_TPL","Group24_MEA","Group25_SGP","Group26_MEA","Group27_DGS","Group27_DGS.FTX","Group28_CTA","Group29_MEA","Group30_SGP","Group31_MEA","Group32_EQD","Group32_EQD.MEA","Group32_EQD.DIM","Group32_EQD.TPL","Group32_EQD.FTX","Group32_EQD.RFF","Group33_NAD","Group34_CTA","Group35_DGS","Group35_DGS.FTX","Group36_CTA"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE371 UNE; 
	public UNZ372 UNZ; 
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
	public String E0080_03;
	public String E0133_04;
	public String E0076_05;
}
class S002_024 {
	public String E0004_01;
	public String E0007_02;
	public String E0008_03;
	public String E0042_04;
}
class S003_035 {
	public String E0010_01;
	public String E0007_02;
	public String E0014_03;
	public String E0046_04;
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
class S016_0517 {
	public String E0115_01;
	public String E0116_02;
	public String E0118_03;
	public String E0051_04;
}
class S017_0618 {
	public String E0121_01;
	public String E0122_02;
	public String E0124_03;
	public String E0051_04;
}
class S018_0719 {
	public String E0127_01;
	public String E0128_02;
	public String E0130_03;
	public String E0051_04;
}
class UNH14 {
	public String E0062_01;
	public S009_0215 S009_02;
	public String E0068_03;
	public S010_0416 S010_04;
	public S016_0517 S016_05;
	public S017_0618 S017_06;
	public S018_0719 S018_07;
}
class C002_0121 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C106_0222 {
	public String E1004_01;
	public String E1056_02;
	public String E1060_03;
}
class BGM20 {
	public C002_0121 C002_01;
	public C106_0222 C106_02;
	public String E1225_03;
	public String E4343_04;
}
class C056_0224 {
	public String E3413_01;
	public String E3412_02;
}
class CTA23 {
	public String E3139_01;
	public C056_0224 C056_02;
}
class C076_0126 {
	public String E3148_01;
	public String E3155_02;
}
class COM25 {
	public C076_0126 C076_01;
}
class C507_0128 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM27 {
	public C507_0128 C507_01;
}
class C536_0130 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_0231 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_0332 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_0433 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR29 {
	public C536_0130 C536_01;
	public C233_0231 C233_02;
	public C537_0332 C537_03;
	public C703_0433 C703_04;
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
class C107_0369 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0470 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX68 {
	public String E4451_01;
	public String E4453_02;
	public C107_0369 C107_03;
	public C108_0470 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C002_0173 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0274 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC72 {
	public C002_0173 C002_01;
	public C503_0274 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_0176 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM75 {
	public C507_0176 C507_01;
}
class Group5_DOC71 {
	public DOC72 DOC; 
	public DTM75 DTM; 
}
class Group4_GOR62 {
	public GOR63 GOR; 
	public List<FTX68> FTX; 
	public List<Group5_DOC71> Group5_DOC; 
}
class C200_0179 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_0280 {
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
class C528_0381 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_0482 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC78 {
	public C200_0179 C200_01;
	public C203_0280 C203_02;
	public C528_0381 C528_03;
	public C554_0482 C554_04;
}
class C517_0284 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0385 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0486 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC83 {
	public String E3227_01;
	public C517_0284 C517_02;
	public C519_0385 C519_03;
	public C553_0486 C553_04;
	public String E5479_05;
}
class C107_0388 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0489 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX87 {
	public String E4451_01;
	public String E4453_02;
	public C107_0388 C107_03;
	public C108_0489 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C504_0191 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_0292 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX90 {
	public C504_0191 C504_01;
	public C504_0292 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C509_0194 {
	public String E5125_01;
	public String E5118_02;
	public String E5375_03;
	public String E5387_04;
	public String E5284_05;
	public String E6411_06;
}
class PRI93 {
	public C509_0194 C509_01;
	public String E5213_02;
}
class C523_0196 {
	public String E6350_01;
	public String E6353_02;
}
class EQN95 {
	public C523_0196 C523_01;
}
class C501_0198 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD97 {
	public C501_0198 C501_01;
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
class C186_01102 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY101 {
	public C186_01102 C186_01;
}
class Group6_TCC77 {
	public TCC78 TCC; 
	public LOC83 LOC; 
	public FTX87 FTX; 
	public CUX90 CUX; 
	public PRI93 PRI; 
	public EQN95 EQN; 
	public PCD97 PCD; 
	public List<MOA99> MOA; 
	public List<QTY101> QTY; 
}
class C220_03105 {
	public String E8067_01;
	public String E8066_02;
}
class C228_04106 {
	public String E8179_01;
	public String E8178_02;
}
class C040_05107 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_07108 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_08109 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT104 {
	public String E8051_01;
	public String E8028_02;
	public C220_03105 C220_03;
	public C228_04106 C228_04;
	public C040_05107 C040_05;
	public String E8101_06;
	public C401_07108 C401_07;
	public C222_08109 C222_08;
	public String E8281_09;
}
class C507_01111 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM110 {
	public C507_01111 C507_01;
}
class C536_01113 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02114 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03115 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04116 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR112 {
	public C536_01113 C536_01;
	public C233_02114 C233_02;
	public C537_03115 C537_03;
	public C703_04116 C703_04;
}
class C517_02119 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03120 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04121 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC118 {
	public String E3227_01;
	public C517_02119 C517_02;
	public C519_03120 C519_03;
	public C553_04121 C553_04;
	public String E5479_05;
}
class C507_01123 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM122 {
	public C507_01123 C507_01;
}
class Group8_LOC117 {
	public LOC118 LOC; 
	public List<DTM122> DTM; 
}
class C506_01126 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF125 {
	public C506_01126 C506_01;
}
class C507_01128 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM127 {
	public C507_01128 C507_01;
}
class Group9_RFF124 {
	public RFF125 RFF; 
	public DTM127 DTM; 
}
class Group7_TDT103 {
	public TDT104 TDT; 
	public List<DTM110> DTM; 
	public List<TSR112> TSR; 
	public List<Group8_LOC117> Group8_LOC; 
	public List<Group9_RFF124> Group9_RFF; 
}
class C082_02131 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03132 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04133 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05134 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07135 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD130 {
	public String E3035_01;
	public C082_02131 C082_02;
	public C058_03132 C058_03;
	public C080_04133 C080_04;
	public C059_05134 C059_05;
	public String E3164_06;
	public C819_07135 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C517_02137 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03138 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04139 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC136 {
	public String E3227_01;
	public C517_02137 C517_02;
	public C519_03138 C519_03;
	public C553_04139 C553_04;
	public String E5479_05;
}
class C056_02142 {
	public String E3413_01;
	public String E3412_02;
}
class CTA141 {
	public String E3139_01;
	public C056_02142 C056_02;
}
class C076_01144 {
	public String E3148_01;
	public String E3155_02;
}
class COM143 {
	public C076_01144 C076_01;
}
class Group11_CTA140 {
	public CTA141 CTA; 
	public List<COM143> COM; 
}
class C002_01147 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02148 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC146 {
	public C002_01147 C002_01;
	public C503_02148 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01150 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM149 {
	public C507_01150 C507_01;
}
class Group12_DOC145 {
	public DOC146 DOC; 
	public DTM149 DTM; 
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
class Group13_RFF151 {
	public RFF152 RFF; 
	public List<DTM154> DTM; 
}
class C229_01158 {
	public String E5237_01;
	public String E1131_02;
	public String E3055_03;
}
class C231_02159 {
	public String E4215_01;
	public String E1131_02;
	public String E3055_03;
}
class CPI157 {
	public C229_01158 C229_01;
	public C231_02159 C231_02;
	public String E4237_03;
}
class C506_01161 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF160 {
	public C506_01161 C506_01;
}
class C504_01163 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class C504_02164 {
	public String E6347_01;
	public String E6345_02;
	public String E6343_03;
	public String E6348_04;
}
class CUX162 {
	public C504_01163 C504_01;
	public C504_02164 C504_02;
	public String E5402_03;
	public String E6341_04;
}
class C517_02166 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03167 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04168 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC165 {
	public String E3227_01;
	public C517_02166 C517_02;
	public C519_03167 C519_03;
	public C553_04168 C553_04;
	public String E5479_05;
}
class C516_01170 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA169 {
	public C516_01170 C516_01;
}
class Group14_CPI156 {
	public CPI157 CPI; 
	public List<RFF160> RFF; 
	public CUX162 CUX; 
	public List<LOC165> LOC; 
	public List<MOA169> MOA; 
}
class C536_01173 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02174 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03175 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04176 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR172 {
	public C536_01173 C536_01;
	public C233_02174 C233_02;
	public C537_03175 C537_03;
	public C703_04176 C703_04;
}
class C506_01178 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF177 {
	public C506_01178 C506_01;
}
class C517_02180 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03181 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04182 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC179 {
	public String E3227_01;
	public C517_02180 C517_02;
	public C519_03181 C519_03;
	public C553_04182 C553_04;
	public String E5479_05;
}
class C222_01184 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL183 {
	public C222_01184 C222_01;
}
class C107_03186 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04187 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX185 {
	public String E4451_01;
	public String E4453_02;
	public C107_03186 C107_03;
	public C108_04187 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class Group15_TSR171 {
	public TSR172 TSR; 
	public RFF177 RFF; 
	public LOC179 LOC; 
	public TPL183 TPL; 
	public List<FTX185> FTX; 
}
class Group10_NAD129 {
	public NAD130 NAD; 
	public List<LOC136> LOC; 
	public List<Group11_CTA140> Group11_CTA; 
	public List<Group12_DOC145> Group12_DOC; 
	public List<Group13_RFF151> Group13_RFF; 
	public List<Group14_CPI156> Group14_CPI; 
	public List<Group15_TSR171> Group15_TSR; 
}
class C213_02190 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_03191 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_04192 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_05193 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_06194 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class GID189 {
	public String E1496_01;
	public C213_02190 C213_02;
	public C213_03191 C213_03;
	public C213_04192 C213_04;
	public C213_05193 C213_05;
	public C213_06194 C213_06;
}
class C524_01196 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02197 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN195 {
	public C524_01196 C524_01;
	public C218_02197 C218_02;
}
class C239_02199 {
	public String E6246_01;
	public String E6411_02;
}
class TMP198 {
	public String E6245_01;
	public C239_02199 C239_02;
}
class C280_02201 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG200 {
	public String E6167_01;
	public C280_02201 C280_02;
}
class C219_01203 {
	public String E8335_01;
	public String E8334_02;
}
class TMD202 {
	public C219_01203 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02205 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03206 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04207 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC204 {
	public String E3227_01;
	public C517_02205 C517_02;
	public C519_03206 C519_03;
	public C553_04207 C553_04;
	public String E5479_05;
}
class C516_01209 {
	public String E5025_01;
	public String E5004_02;
	public String E6345_03;
	public String E6343_04;
	public String E4405_05;
}
class MOA208 {
	public C516_01209 C516_01;
}
class C212_02211 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_03212 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_04213 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_05214 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class C212_06215 {
	public String E7140_01;
	public String E7143_02;
	public String E1131_03;
	public String E3055_04;
}
class PIA210 {
	public String E4347_01;
	public C212_02211 C212_02;
	public C212_03212 C212_03;
	public C212_04213 C212_04;
	public C212_05214 C212_05;
	public C212_06215 C212_06;
}
class C107_03217 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04218 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX216 {
	public String E4451_01;
	public String E4453_02;
	public C107_03217 C107_03;
	public C108_04218 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C501_01220 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD219 {
	public C501_01220 C501_01;
}
class C082_02223 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03224 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04225 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05226 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07227 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD222 {
	public String E3035_01;
	public C082_02223 C082_02;
	public C058_03224 C058_03;
	public C080_04225 C080_04;
	public C059_05226 C059_05;
	public String E3164_06;
	public C819_07227 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01229 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM228 {
	public C507_01229 C507_01;
}
class Group17_NAD221 {
	public NAD222 NAD; 
	public DTM228 DTM; 
}
class C703_01231 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS230 {
	public C703_01231 C703_01;
}
class C502_02234 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03235 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA233 {
	public String E6311_01;
	public C502_02234 C502_02;
	public C174_03235 C174_03;
	public String E7383_04;
}
class C523_01237 {
	public String E6350_01;
	public String E6353_02;
}
class EQN236 {
	public C523_01237 C523_01;
}
class Group18_MEA232 {
	public MEA233 MEA; 
	public EQN236 EQN; 
}
class C211_02240 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM239 {
	public String E6145_01;
	public C211_02240 C211_02;
}
class C523_01242 {
	public String E6350_01;
	public String E6353_02;
}
class EQN241 {
	public C523_01242 C523_01;
}
class Group19_DIM238 {
	public DIM239 DIM; 
	public EQN241 EQN; 
}
class C506_01245 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
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
class Group20_RFF243 {
	public RFF244 RFF; 
	public List<DTM246> DTM; 
}
class C210_02250 {
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
class C827_04251 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI249 {
	public String E4233_01;
	public C210_02250 C210_02;
	public String E8275_03;
	public C827_04251 C827_04;
}
class C506_01253 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF252 {
	public C506_01253 C506_01;
}
class C507_01255 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM254 {
	public C507_01255 C507_01;
}
class C208_02257 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03258 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04259 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05260 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06261 {
	public String E7402_01;
	public String E7402_02;
}
class GIN256 {
	public String E7405_01;
	public C208_02257 C208_02;
	public C208_03258 C208_03;
	public C208_04259 C208_04;
	public C208_05260 C208_05;
	public C208_06261 C208_06;
}
class Group21_PCI248 {
	public PCI249 PCI; 
	public RFF252 RFF; 
	public DTM254 DTM; 
	public List<GIN256> GIN; 
}
class C002_01264 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02265 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC263 {
	public C002_01264 C002_01;
	public C503_02265 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01267 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM266 {
	public C507_01267 C507_01;
}
class Group22_DOC262 {
	public DOC263 DOC; 
	public List<DTM266> DTM; 
}
class C222_01270 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL269 {
	public C222_01270 C222_01;
}
class C502_02273 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03274 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA272 {
	public String E6311_01;
	public C502_02273 C502_02;
	public C174_03274 C174_03;
	public String E7383_04;
}
class C523_01276 {
	public String E6350_01;
	public String E6353_02;
}
class EQN275 {
	public C523_01276 C523_01;
}
class Group24_MEA271 {
	public MEA272 MEA; 
	public EQN275 EQN; 
}
class Group23_TPL268 {
	public TPL269 TPL; 
	public List<Group24_MEA271> Group24_MEA; 
}
class C237_01279 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP278 {
	public C237_01279 C237_01;
	public String E7224_02;
}
class C502_02282 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03283 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA281 {
	public String E6311_01;
	public C502_02282 C502_02;
	public C174_03283 C174_03;
	public String E7383_04;
}
class C523_01285 {
	public String E6350_01;
	public String E6353_02;
}
class EQN284 {
	public C523_01285 C523_01;
}
class Group26_MEA280 {
	public MEA281 MEA; 
	public EQN284 EQN; 
}
class Group25_SGP277 {
	public SGP278 SGP; 
	public List<Group26_MEA280> Group26_MEA; 
}
class C205_02288 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03289 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04290 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09291 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10292 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS287 {
	public String E8273_01;
	public C205_02288 C205_02;
	public C234_03289 C234_03;
	public C223_04290 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09291 C235_09;
	public C236_10292 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03294 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04295 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX293 {
	public String E4451_01;
	public String E4453_02;
	public C107_03294 C107_03;
	public C108_04295 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02298 {
	public String E3413_01;
	public String E3412_02;
}
class CTA297 {
	public String E3139_01;
	public C056_02298 C056_02;
}
class C076_01300 {
	public String E3148_01;
	public String E3155_02;
}
class COM299 {
	public C076_01300 C076_01;
}
class Group28_CTA296 {
	public CTA297 CTA; 
	public List<COM299> COM; 
}
class C502_02303 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03304 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA302 {
	public String E6311_01;
	public C502_02303 C502_02;
	public C174_03304 C174_03;
	public String E7383_04;
}
class C523_01306 {
	public String E6350_01;
	public String E6353_02;
}
class EQN305 {
	public C523_01306 C523_01;
}
class Group29_MEA301 {
	public MEA302 MEA; 
	public EQN305 EQN; 
}
class C237_01309 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP308 {
	public C237_01309 C237_01;
	public String E7224_02;
}
class C502_02312 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03313 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA311 {
	public String E6311_01;
	public C502_02312 C502_02;
	public C174_03313 C174_03;
	public String E7383_04;
}
class C523_01315 {
	public String E6350_01;
	public String E6353_02;
}
class EQN314 {
	public C523_01315 C523_01;
}
class Group31_MEA310 {
	public MEA311 MEA; 
	public EQN314 EQN; 
}
class Group30_SGP307 {
	public SGP308 SGP; 
	public List<Group31_MEA310> Group31_MEA; 
}
class Group27_DGS286 {
	public DGS287 DGS; 
	public List<FTX293> FTX; 
	public List<Group28_CTA296> Group28_CTA; 
	public List<Group29_MEA301> Group29_MEA; 
	public List<Group30_SGP307> Group30_SGP; 
}
class Group16_GID188 {
	public GID189 GID; 
	public HAN195 HAN; 
	public TMP198 TMP; 
	public RNG200 RNG; 
	public TMD202 TMD; 
	public List<LOC204> LOC; 
	public List<MOA208> MOA; 
	public List<PIA210> PIA; 
	public List<FTX216> FTX; 
	public List<PCD219> PCD; 
	public List<Group17_NAD221> Group17_NAD; 
	public List<GDS230> GDS; 
	public List<Group18_MEA232> Group18_MEA; 
	public List<Group19_DIM238> Group19_DIM; 
	public List<Group20_RFF243> Group20_RFF; 
	public List<Group21_PCI248> Group21_PCI; 
	public List<Group22_DOC262> Group22_DOC; 
	public List<Group23_TPL268> Group23_TPL; 
	public List<Group25_SGP277> Group25_SGP; 
	public List<Group27_DGS286> Group27_DGS; 
}
class C237_02318 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03319 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD317 {
	public String E8053_01;
	public C237_02318 C237_02;
	public C224_03319 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C523_01321 {
	public String E6350_01;
	public String E6353_02;
}
class EQN320 {
	public C523_01321 C523_01;
}
class C219_01323 {
	public String E8335_01;
	public String E8334_02;
}
class TMD322 {
	public C219_01323 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C502_02325 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03326 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA324 {
	public String E6311_01;
	public C502_02325 C502_02;
	public C174_03326 C174_03;
	public String E7383_04;
}
class C211_02328 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM327 {
	public String E6145_01;
	public C211_02328 C211_02;
}
class C222_01330 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL329 {
	public C222_01330 C222_01;
}
class C524_01332 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02333 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN331 {
	public C524_01332 C524_01;
	public C218_02333 C218_02;
}
class C239_02335 {
	public String E6246_01;
	public String E6411_02;
}
class TMP334 {
	public String E6245_01;
	public C239_02335 C239_02;
}
class C107_03337 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04338 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX336 {
	public String E4451_01;
	public String E4453_02;
	public C107_03337 C107_03;
	public C108_04338 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C506_01340 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF339 {
	public C506_01340 C506_01;
}
class C082_02343 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03344 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04345 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05346 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07347 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD342 {
	public String E3035_01;
	public C082_02343 C082_02;
	public C058_03344 C058_03;
	public C080_04345 C080_04;
	public C059_05346 C059_05;
	public String E3164_06;
	public C819_07347 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01349 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM348 {
	public C507_01349 C507_01;
}
class C056_02352 {
	public String E3413_01;
	public String E3412_02;
}
class CTA351 {
	public String E3139_01;
	public C056_02352 C056_02;
}
class C076_01354 {
	public String E3148_01;
	public String E3155_02;
}
class COM353 {
	public C076_01354 C076_01;
}
class Group34_CTA350 {
	public CTA351 CTA; 
	public List<COM353> COM; 
}
class Group33_NAD341 {
	public NAD342 NAD; 
	public DTM348 DTM; 
	public List<Group34_CTA350> Group34_CTA; 
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
class Group36_CTA365 {
	public CTA366 CTA; 
	public List<COM368> COM; 
}
class Group35_DGS355 {
	public DGS356 DGS; 
	public List<FTX362> FTX; 
	public List<Group36_CTA365> Group36_CTA; 
}
class Group32_EQD316 {
	public EQD317 EQD; 
	public EQN320 EQN; 
	public TMD322 TMD; 
	public List<MEA324> MEA; 
	public List<DIM327> DIM; 
	public List<TPL329> TPL; 
	public HAN331 HAN; 
	public TMP334 TMP; 
	public List<FTX336> FTX; 
	public List<RFF339> RFF; 
	public List<Group33_NAD341> Group33_NAD; 
	public List<Group35_DGS355> Group35_DGS; 
}
class UNT370 {
	public String E0074_01;
	public String E0062_02;
}
class Group_UNH13 {
	public UNH14 UNH; 
	public BGM20 BGM; 
	public CTA23 CTA; 
	public List<COM25> COM; 
	public List<DTM27> DTM; 
	public List<TSR29> TSR; 
	public List<MOA34> MOA; 
	public List<FTX36> FTX; 
	public List<CNT39> CNT; 
	public List<GDS41> GDS; 
	public List<Group1_LOC43> Group1_LOC; 
	public List<Group2_TOD50> Group2_TOD; 
	public List<Group3_RFF57> Group3_RFF; 
	public List<Group4_GOR62> Group4_GOR; 
	public List<Group6_TCC77> Group6_TCC; 
	public List<Group7_TDT103> Group7_TDT; 
	public List<Group10_NAD129> Group10_NAD; 
	public List<Group16_GID188> Group16_GID; 
	public List<Group32_EQD316> Group32_EQD; 
	public UNT370 UNT; 
}
class UNE371 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ372 {
	public String E0036_01;
	public String E0020_02;
}
