package cs.b2b.core.mapping.bean.edi.edifact.d08a.IFTMBC

public class EDI_ELEMICA_IFTMBC {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","COM","Group_UNH.DTM","Group_UNH.TSR","FTX","CNT","GDS","Group1_LOC","Group1_LOC.DTM","Group2_RFF","Group2_RFF.DTM","TCC","Group3_TDT","Group3_TDT.DTM","Group3_TDT.TSR","Group4_LOC","Group4_LOC.DTM","Group5_RFF","Group6_NAD","Group6_NAD.LOC","Group7_CTA","Group8_TSR","Group9_GID","Group9_GID.LOC","PCD","Group10_NAD","Group11_MEA","Group12_DIM","Group13_RFF","Group13_RFF.DTM","Group14_DOC","Group14_DOC.DTM","Group15_DGS","Group16_CTA","Group17_MEA","Group18_EQD","Group18_EQD.MEA","Group18_EQD.DIM","Group18_EQD.RNG","Group18_EQD.RFF","Group19_NAD","Group20_DGS","Group21_CTA"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE243 UNE; 
	public UNZ244 UNZ; 
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
class C107_0332 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0433 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX31 {
	public String E4451_01;
	public String E4453_02;
	public C107_0332 C107_03;
	public C108_0433 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C270_0135 {
	public String E6069_01;
	public String E6066_02;
	public String E6411_03;
}
class CNT34 {
	public C270_0135 C270_01;
}
class C703_0137 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS36 {
	public C703_0137 C703_01;
}
class C517_0240 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0341 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0442 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC39 {
	public String E3227_01;
	public C517_0240 C517_02;
	public C519_0341 C519_03;
	public C553_0442 C553_04;
	public String E5479_05;
}
class C507_0144 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM43 {
	public C507_0144 C507_01;
}
class Group1_LOC38 {
	public LOC39 LOC; 
	public List<DTM43> DTM; 
}
class C506_0147 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF46 {
	public C506_0147 C506_01;
}
class C507_0149 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM48 {
	public C507_0149 C507_01;
}
class Group2_RFF45 {
	public RFF46 RFF; 
	public List<DTM48> DTM; 
}
class C200_0151 {
	public String E8023_01;
	public String E1131_02;
	public String E3055_03;
	public String E8022_04;
	public String E4237_05;
	public String E7140_06;
}
class C203_0252 {
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
class C528_0353 {
	public String E7357_01;
	public String E1131_02;
	public String E3055_03;
}
class C554_0454 {
	public String E5243_01;
	public String E1131_02;
	public String E3055_03;
}
class TCC50 {
	public C200_0151 C200_01;
	public C203_0252 C203_02;
	public C528_0353 C528_03;
	public C554_0454 C554_04;
}
class C220_0357 {
	public String E8067_01;
	public String E8066_02;
}
class C001_0458 {
	public String E8179_01;
	public String E1131_02;
	public String E3055_03;
	public String E8178_04;
}
class C040_0559 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3126_04;
}
class C401_0760 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_0861 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT56 {
	public String E8051_01;
	public String E8028_02;
	public C220_0357 C220_03;
	public C001_0458 C001_04;
	public C040_0559 C040_05;
	public String E8101_06;
	public C401_0760 C401_07;
	public C222_0861 C222_08;
	public String E8281_09;
}
class C507_0163 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM62 {
	public C507_0163 C507_01;
}
class C536_0165 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_0266 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_0367 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_0468 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR64 {
	public C536_0165 C536_01;
	public C233_0266 C233_02;
	public C537_0367 C537_03;
	public C703_0468 C703_04;
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
class C507_0175 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM74 {
	public C507_0175 C507_01;
}
class Group4_LOC69 {
	public LOC70 LOC; 
	public List<DTM74> DTM; 
}
class C506_0178 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF77 {
	public C506_0178 C506_01;
}
class C507_0180 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM79 {
	public C507_0180 C507_01;
}
class Group5_RFF76 {
	public RFF77 RFF; 
	public DTM79 DTM; 
}
class Group3_TDT55 {
	public TDT56 TDT; 
	public List<DTM62> DTM; 
	public List<TSR64> TSR; 
	public List<Group4_LOC69> Group4_LOC; 
	public List<Group5_RFF76> Group5_RFF; 
}
class C082_0283 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_0384 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_0485 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_0586 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_0787 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD82 {
	public String E3035_01;
	public C082_0283 C082_02;
	public C058_0384 C058_03;
	public C080_0485 C080_04;
	public C059_0586 C059_05;
	public String E3164_06;
	public C819_0787 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C517_0289 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0390 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0491 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC88 {
	public String E3227_01;
	public C517_0289 C517_02;
	public C519_0390 C519_03;
	public C553_0491 C553_04;
	public String E5479_05;
}
class C056_0294 {
	public String E3413_01;
	public String E3412_02;
}
class CTA93 {
	public String E3139_01;
	public C056_0294 C056_02;
}
class C076_0196 {
	public String E3148_01;
	public String E3155_02;
}
class COM95 {
	public C076_0196 C076_01;
}
class Group7_CTA92 {
	public CTA93 CTA; 
	public List<COM95> COM; 
}
class C536_0199 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_02100 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_03101 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_04102 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR98 {
	public C536_0199 C536_01;
	public C233_02100 C233_02;
	public C537_03101 C537_03;
	public C703_04102 C703_04;
}
class C506_01104 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF103 {
	public C506_01104 C506_01;
}
class C517_02106 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03107 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04108 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC105 {
	public String E3227_01;
	public C517_02106 C517_02;
	public C519_03107 C519_03;
	public C553_04108 C553_04;
	public String E5479_05;
}
class C222_01110 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL109 {
	public C222_01110 C222_01;
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
	public String E4447_06;
}
class Group8_TSR97 {
	public TSR98 TSR; 
	public RFF103 RFF; 
	public LOC105 LOC; 
	public TPL109 TPL; 
	public List<FTX111> FTX; 
}
class Group6_NAD81 {
	public NAD82 NAD; 
	public List<LOC88> LOC; 
	public List<Group7_CTA92> Group7_CTA; 
	public List<Group8_TSR97> Group8_TSR; 
}
class C213_02116 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_03117 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_04118 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_05119 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_06120 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class GID115 {
	public String E1496_01;
	public C213_02116 C213_02;
	public C213_03117 C213_03;
	public C213_04118 C213_04;
	public C213_05119 C213_05;
	public C213_06120 C213_06;
}
class C524_01122 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02123 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN121 {
	public C524_01122 C524_01;
	public C218_02123 C218_02;
}
class C239_02125 {
	public String E6246_01;
	public String E6411_02;
}
class TMP124 {
	public String E6245_01;
	public C239_02125 C239_02;
}
class C280_02127 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG126 {
	public String E6167_01;
	public C280_02127 C280_02;
}
class C219_01129 {
	public String E8335_01;
	public String E8334_02;
}
class TMD128 {
	public C219_01129 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02131 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03132 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04133 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC130 {
	public String E3227_01;
	public C517_02131 C517_02;
	public C519_03132 C519_03;
	public C553_04133 C553_04;
	public String E5479_05;
}
class C107_03135 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04136 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX134 {
	public String E4451_01;
	public String E4453_02;
	public C107_03135 C107_03;
	public C108_04136 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C501_01138 {
	public String E5245_01;
	public String E5482_02;
	public String E5249_03;
	public String E1131_04;
	public String E3055_05;
}
class PCD137 {
	public C501_01138 C501_01;
	public String E4405_02;
}
class C082_02141 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_03142 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_04143 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_05144 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_07145 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD140 {
	public String E3035_01;
	public C082_02141 C082_02;
	public C058_03142 C058_03;
	public C080_04143 C080_04;
	public C059_05144 C059_05;
	public String E3164_06;
	public C819_07145 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C507_01147 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM146 {
	public C507_01147 C507_01;
}
class Group10_NAD139 {
	public NAD140 NAD; 
	public DTM146 DTM; 
}
class C703_01149 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS148 {
	public C703_01149 C703_01;
}
class C502_02152 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03153 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA151 {
	public String E6311_01;
	public C502_02152 C502_02;
	public C174_03153 C174_03;
	public String E7383_04;
}
class C523_01155 {
	public String E6350_01;
	public String E6353_02;
}
class EQN154 {
	public C523_01155 C523_01;
}
class Group11_MEA150 {
	public MEA151 MEA; 
	public EQN154 EQN; 
}
class C211_02158 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM157 {
	public String E6145_01;
	public C211_02158 C211_02;
}
class C523_01160 {
	public String E6350_01;
	public String E6353_02;
}
class EQN159 {
	public C523_01160 C523_01;
}
class Group12_DIM156 {
	public DIM157 DIM; 
	public EQN159 EQN; 
}
class C506_01163 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF162 {
	public C506_01163 C506_01;
}
class C507_01165 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM164 {
	public C507_01165 C507_01;
}
class Group13_RFF161 {
	public RFF162 RFF; 
	public List<DTM164> DTM; 
}
class C002_01168 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_02169 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC167 {
	public C002_01168 C002_01;
	public C503_02169 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
}
class C507_01171 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM170 {
	public C507_01171 C507_01;
}
class Group14_DOC166 {
	public DOC167 DOC; 
	public List<DTM170> DTM; 
}
class C205_02174 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03175 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04176 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09177 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10178 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
	public String E8246_04;
}
class DGS173 {
	public String E8273_01;
	public C205_02174 C205_02;
	public C234_03175 C234_03;
	public C223_04176 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09177 C235_09;
	public C236_10178 C236_10;
	public String E8255_11;
	public String E8179_12;
	public String E8211_13;
}
class C107_03180 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04181 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX179 {
	public String E4451_01;
	public String E4453_02;
	public C107_03180 C107_03;
	public C108_04181 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02184 {
	public String E3413_01;
	public String E3412_02;
}
class CTA183 {
	public String E3139_01;
	public C056_02184 C056_02;
}
class C076_01186 {
	public String E3148_01;
	public String E3155_02;
}
class COM185 {
	public C076_01186 C076_01;
}
class Group16_CTA182 {
	public CTA183 CTA; 
	public List<COM185> COM; 
}
class C502_02189 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03190 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA188 {
	public String E6311_01;
	public C502_02189 C502_02;
	public C174_03190 C174_03;
	public String E7383_04;
}
class C523_01192 {
	public String E6350_01;
	public String E6353_02;
}
class EQN191 {
	public C523_01192 C523_01;
}
class Group17_MEA187 {
	public MEA188 MEA; 
	public EQN191 EQN; 
}
class Group15_DGS172 {
	public DGS173 DGS; 
	public List<FTX179> FTX; 
	public List<Group16_CTA182> Group16_CTA; 
	public List<Group17_MEA187> Group17_MEA; 
}
class Group9_GID114 {
	public GID115 GID; 
	public HAN121 HAN; 
	public TMP124 TMP; 
	public RNG126 RNG; 
	public TMD128 TMD; 
	public List<LOC130> LOC; 
	public List<FTX134> FTX; 
	public List<PCD137> PCD; 
	public List<Group10_NAD139> Group10_NAD; 
	public List<GDS148> GDS; 
	public List<Group11_MEA150> Group11_MEA; 
	public List<Group12_DIM156> Group12_DIM; 
	public List<Group13_RFF161> Group13_RFF; 
	public List<Group14_DOC166> Group14_DOC; 
	public List<Group15_DGS172> Group15_DGS; 
}
class C237_02195 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03196 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD194 {
	public String E8053_01;
	public C237_02195 C237_02;
	public C224_03196 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
	public String E4233_07;
}
class C523_01198 {
	public String E6350_01;
	public String E6353_02;
}
class EQN197 {
	public C523_01198 C523_01;
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
class C502_02202 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03203 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA201 {
	public String E6311_01;
	public C502_02202 C502_02;
	public C174_03203 C174_03;
	public String E7383_04;
}
class C211_02205 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM204 {
	public String E6145_01;
	public C211_02205 C211_02;
}
class C524_01207 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02208 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN206 {
	public C524_01207 C524_01;
	public C218_02208 C218_02;
}
class C239_02210 {
	public String E6246_01;
	public String E6411_02;
}
class TMP209 {
	public String E6245_01;
	public C239_02210 C239_02;
}
class C280_02212 {
	public String E6411_01;
	public String E6162_02;
	public String E6152_03;
}
class RNG211 {
	public String E6167_01;
	public C280_02212 C280_02;
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
class C506_01217 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF216 {
	public C506_01217 C506_01;
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
class Group19_NAD218 {
	public NAD219 NAD; 
	public DTM225 DTM; 
}
class C205_02229 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03230 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04231 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09232 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10233 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
	public String E8246_04;
}
class DGS228 {
	public String E8273_01;
	public C205_02229 C205_02;
	public C234_03230 C234_03;
	public C223_04231 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09232 C235_09;
	public C236_10233 C236_10;
	public String E8255_11;
	public String E8179_12;
	public String E8211_13;
}
class C107_03235 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04236 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX234 {
	public String E4451_01;
	public String E4453_02;
	public C107_03235 C107_03;
	public C108_04236 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C056_02239 {
	public String E3413_01;
	public String E3412_02;
}
class CTA238 {
	public String E3139_01;
	public C056_02239 C056_02;
}
class C076_01241 {
	public String E3148_01;
	public String E3155_02;
}
class COM240 {
	public C076_01241 C076_01;
}
class Group21_CTA237 {
	public CTA238 CTA; 
	public List<COM240> COM; 
}
class Group20_DGS227 {
	public DGS228 DGS; 
	public List<FTX234> FTX; 
	public List<Group21_CTA237> Group21_CTA; 
}
class Group18_EQD193 {
	public EQD194 EQD; 
	public EQN197 EQN; 
	public TMD199 TMD; 
	public List<MEA201> MEA; 
	public List<DIM204> DIM; 
	public HAN206 HAN; 
	public TMP209 TMP; 
	public List<RNG211> RNG; 
	public List<FTX213> FTX; 
	public List<RFF216> RFF; 
	public List<Group19_NAD218> Group19_NAD; 
	public List<Group20_DGS227> Group20_DGS; 
}
class UNT242 {
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
	public List<FTX31> FTX; 
	public List<CNT34> CNT; 
	public List<GDS36> GDS; 
	public List<Group1_LOC38> Group1_LOC; 
	public List<Group2_RFF45> Group2_RFF; 
	public List<TCC50> TCC; 
	public List<Group3_TDT55> Group3_TDT; 
	public List<Group6_NAD81> Group6_NAD; 
	public List<Group9_GID114> Group9_GID; 
	public List<Group18_EQD193> Group18_EQD; 
	public UNT242 UNT; 
}
class UNE243 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ244 {
	public String E0036_01;
	public String E0020_02;
}
