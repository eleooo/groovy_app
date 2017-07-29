package cs.b2b.core.mapping.bean.edi.edifact.d96b.IFTSTA_CS

public class EDI_CS_IFTSTA {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","Group_UNH.DTM","Group1_NAD","Group2_CTA","COM","Group3_RFF","Group_UNH.LOC","FTX","CNT","Group4_CNI","Group4_CNI.LOC","Group5_STS","Group5_STS.RFF","Group5_STS.DTM","Group5_STS.NAD","Group5_STS.PCI","Group6_TDT","Group6_TDT.RFF","Group6_TDT.LOC","Group6_TDT.DTM","Group7_EQD","Group7_EQD.MEA","Group7_EQD.DIM","SEL","TPL","Group8_EQA","Group9_GID","HAN","SGP","DGS","Group10_MEA","Group11_DIM","Group12_PCI","GIN"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE166 UNE; 
	public UNZ167 UNZ; 
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
class C507_0121 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM20 {
	public C507_0121 C507_01;
}
class C536_0123 {
	public String E4065_01;
	public String E1131_02;
	public String E3055_03;
}
class C233_0224 {
	public String E7273_01;
	public String E1131_02;
	public String E3055_03;
	public String E7273_04;
	public String E1131_05;
	public String E3055_06;
}
class C537_0325 {
	public String E4219_01;
	public String E1131_02;
	public String E3055_03;
}
class C703_0426 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class TSR22 {
	public C536_0123 C536_01;
	public C233_0224 C233_02;
	public C537_0325 C537_03;
	public C703_0426 C703_04;
}
class C082_0229 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_0330 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_0431 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_0532 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD28 {
	public String E3035_01;
	public C082_0229 C082_02;
	public C058_0330 C058_03;
	public C080_0431 C080_04;
	public C059_0532 C059_05;
	public String E3164_06;
	public String E3229_07;
	public String E3251_08;
	public String E3207_09;
}
class C056_0235 {
	public String E3413_01;
	public String E3412_02;
}
class CTA34 {
	public String E3139_01;
	public C056_0235 C056_02;
}
class C076_0137 {
	public String E3148_01;
	public String E3155_02;
}
class COM36 {
	public C076_0137 C076_01;
}
class Group2_CTA33 {
	public CTA34 CTA; 
	public List<COM36> COM; 
}
class Group1_NAD27 {
	public NAD28 NAD; 
	public List<Group2_CTA33> Group2_CTA; 
}
class C506_0140 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF39 {
	public C506_0140 C506_01;
}
class C507_0142 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM41 {
	public C507_0142 C507_01;
}
class Group3_RFF38 {
	public RFF39 RFF; 
	public DTM41 DTM; 
}
class C517_0244 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0345 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0446 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC43 {
	public String E3227_01;
	public C517_0244 C517_02;
	public C519_0345 C519_03;
	public C553_0446 C553_04;
	public String E5479_05;
}
class C107_0348 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0449 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX47 {
	public String E4451_01;
	public String E4453_02;
	public C107_0348 C107_03;
	public C108_0449 C108_04;
	public String E3453_05;
}
class C270_0151 {
	public String E6069_01;
	public String E6066_02;
	public String E6411_03;
}
class CNT50 {
	public C270_0151 C270_01;
}
class C503_0254 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class CNI53 {
	public String E1490_01;
	public C503_0254 C503_02;
	public String E1312_03;
}
class C517_0256 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0357 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0458 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC55 {
	public String E3227_01;
	public C517_0256 C517_02;
	public C519_0357 C519_03;
	public C553_0458 C553_04;
	public String E5479_05;
}
class C270_0160 {
	public String E6069_01;
	public String E6066_02;
	public String E6411_03;
}
class CNT59 {
	public C270_0160 C270_01;
}
class C601_0163 {
	public String E9015_01;
	public String E1131_02;
	public String E3055_03;
}
class C555_0264 {
	public String E9011_01;
	public String E1131_02;
	public String E3055_03;
	public String E9010_04;
}
class C556_0365 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class C556_0466 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class C556_0567 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class C556_0668 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class C556_0769 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class STS62 {
	public C601_0163 C601_01;
	public C555_0264 C555_02;
	public C556_0365 C556_03;
	public C556_0466 C556_04;
	public C556_0567 C556_05;
	public C556_0668 C556_06;
	public C556_0769 C556_07;
}
class C506_0171 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF70 {
	public C506_0171 C506_01;
}
class C507_0173 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM72 {
	public C507_0173 C507_01;
}
class C002_0175 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0276 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
}
class DOC74 {
	public C002_0175 C002_01;
	public C503_0276 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
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
class C082_0281 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_0382 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_0483 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_0584 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class NAD80 {
	public String E3035_01;
	public C082_0281 C082_02;
	public C058_0382 C058_03;
	public C080_0483 C080_04;
	public C059_0584 C059_05;
	public String E3164_06;
	public String E3229_07;
	public String E3251_08;
	public String E3207_09;
}
class C517_0286 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0387 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0488 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC85 {
	public String E3227_01;
	public C517_0286 C517_02;
	public C519_0387 C519_03;
	public C553_0488 C553_04;
	public String E5479_05;
}
class C210_0290 {
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
class C827_0491 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI89 {
	public String E4233_01;
	public C210_0290 C210_02;
	public String E8275_03;
	public C827_0491 C827_04;
}
class C220_0394 {
	public String E8067_01;
	public String E8066_02;
}
class C228_0495 {
	public String E8179_01;
	public String E8178_02;
}
class C040_0596 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_0797 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_0898 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT93 {
	public String E8051_01;
	public String E8028_02;
	public C220_0394 C220_03;
	public C228_0495 C228_04;
	public C040_0596 C040_05;
	public String E8101_06;
	public C401_0797 C401_07;
	public C222_0898 C222_08;
	public String E8281_09;
}
class C506_01100 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
}
class RFF99 {
	public C506_01100 C506_01;
}
class C517_02102 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03103 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04104 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC101 {
	public String E3227_01;
	public C517_02102 C517_02;
	public C519_03103 C519_03;
	public C553_04104 C553_04;
	public String E5479_05;
}
class C507_01106 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM105 {
	public C507_01106 C507_01;
}
class Group6_TDT92 {
	public TDT93 TDT; 
	public List<RFF99> RFF; 
	public List<LOC101> LOC; 
	public List<DTM105> DTM; 
}
class C237_02109 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03110 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD108 {
	public String E8053_01;
	public C237_02109 C237_02;
	public C224_03110 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C502_02112 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03113 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA111 {
	public String E6311_01;
	public C502_02112 C502_02;
	public C174_03113 C174_03;
	public String E7383_04;
}
class C211_02115 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM114 {
	public String E6145_01;
	public C211_02115 C211_02;
}
class C215_02117 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class SEL116 {
	public String E9308_01;
	public C215_02117 C215_02;
	public String E4517_03;
}
class C222_01119 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL118 {
	public C222_01119 C222_01;
}
class C237_02122 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class EQA121 {
	public String E8053_01;
	public C237_02122 C237_02;
}
class C215_02124 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class SEL123 {
	public String E9308_01;
	public C215_02124 C215_02;
	public String E4517_03;
}
class Group8_EQA120 {
	public EQA121 EQA; 
	public List<SEL123> SEL; 
}
class Group7_EQD107 {
	public EQD108 EQD; 
	public List<MEA111> MEA; 
	public List<DIM114> DIM; 
	public List<SEL116> SEL; 
	public List<TPL118> TPL; 
	public List<Group8_EQA120> Group8_EQA; 
}
class C213_02127 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_03128 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class C213_04129 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
}
class GID126 {
	public String E1496_01;
	public C213_02127 C213_02;
	public C213_03128 C213_03;
	public C213_04129 C213_04;
}
class C524_01131 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02132 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN130 {
	public C524_01131 C524_01;
	public C218_02132 C218_02;
}
class C237_01134 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP133 {
	public C237_01134 C237_01;
	public String E7224_02;
}
class C205_02136 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03137 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04138 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09139 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10140 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS135 {
	public String E8273_01;
	public C205_02136 C205_02;
	public C234_03137 C234_03;
	public C223_04138 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09139 C235_09;
	public C236_10140 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03142 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04143 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX141 {
	public String E4451_01;
	public String E4453_02;
	public C107_03142 C107_03;
	public C108_04143 C108_04;
	public String E3453_05;
}
class C502_02146 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03147 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA145 {
	public String E6311_01;
	public C502_02146 C502_02;
	public C174_03147 C174_03;
	public String E7383_04;
}
class C523_01149 {
	public String E6350_01;
	public String E6353_02;
}
class EQN148 {
	public C523_01149 C523_01;
}
class Group10_MEA144 {
	public MEA145 MEA; 
	public EQN148 EQN; 
}
class C211_02152 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM151 {
	public String E6145_01;
	public C211_02152 C211_02;
}
class C523_01154 {
	public String E6350_01;
	public String E6353_02;
}
class EQN153 {
	public C523_01154 C523_01;
}
class Group11_DIM150 {
	public DIM151 DIM; 
	public EQN153 EQN; 
}
class C210_02157 {
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
class C827_04158 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI156 {
	public String E4233_01;
	public C210_02157 C210_02;
	public String E8275_03;
	public C827_04158 C827_04;
}
class C208_02160 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03161 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04162 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05163 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06164 {
	public String E7402_01;
	public String E7402_02;
}
class GIN159 {
	public String E7405_01;
	public C208_02160 C208_02;
	public C208_03161 C208_03;
	public C208_04162 C208_04;
	public C208_05163 C208_05;
	public C208_06164 C208_06;
}
class Group12_PCI155 {
	public PCI156 PCI; 
	public List<GIN159> GIN; 
}
class Group9_GID125 {
	public GID126 GID; 
	public List<HAN130> HAN; 
	public List<SGP133> SGP; 
	public List<DGS135> DGS; 
	public List<FTX141> FTX; 
	public List<Group10_MEA144> Group10_MEA; 
	public List<Group11_DIM150> Group11_DIM; 
	public List<Group12_PCI155> Group12_PCI; 
}
class Group5_STS61 {
	public STS62 STS; 
	public List<RFF70> RFF; 
	public List<DTM72> DTM; 
	public DOC74 DOC; 
	public List<FTX77> FTX; 
	public List<NAD80> NAD; 
	public LOC85 LOC; 
	public List<PCI89> PCI; 
	public List<Group6_TDT92> Group6_TDT; 
	public List<Group7_EQD107> Group7_EQD; 
	public List<Group9_GID125> Group9_GID; 
}
class Group4_CNI52 {
	public CNI53 CNI; 
	public List<LOC55> LOC; 
	public List<CNT59> CNT; 
	public List<Group5_STS61> Group5_STS; 
}
class UNT165 {
	public String E0074_01;
	public String E0062_02;
}
class Group_UNH13 {
	public UNH14 UNH; 
	public BGM17 BGM; 
	public List<DTM20> DTM; 
	public TSR22 TSR; 
	public List<Group1_NAD27> Group1_NAD; 
	public List<Group3_RFF38> Group3_RFF; 
	public List<LOC43> LOC; 
	public List<FTX47> FTX; 
	public List<CNT50> CNT; 
	public List<Group4_CNI52> Group4_CNI; 
	public UNT165 UNT; 
}
class UNE166 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ167 {
	public String E0036_01;
	public String E0020_02;
}
