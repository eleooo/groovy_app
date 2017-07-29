package cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTSTA_CS

public class EDI_CS_IFTSTA {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","Group_UNH.DTM","Group1_NAD","Group2_CTA","COM","Group3_RFF","Group_UNH.LOC","FTX","CNT","Group4_CNI","Group4_CNI.LOC","Group5_STS","Group5_STS.RFF","Group5_STS.DTM","Group5_STS.NAD","Group5_STS.PCI","Group6_TDT","Group6_TDT.DTM","Group6_TDT.RFF","Group7_LOC","Group7_LOC.DTM","Group8_EQD","Group8_EQD.MEA","Group8_EQD.DIM","SEL","Group8_EQD.RFF","TPL","Group9_EQA","Group10_GID","HAN","SGP","DGS","Group11_MEA","Group12_DIM","Group13_PCI","GIN"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE179 UNE; 
	public UNZ180 UNZ; 
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
class C819_0733 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD28 {
	public String E3035_01;
	public C082_0229 C082_02;
	public C058_0330 C058_03;
	public C080_0431 C080_04;
	public C059_0532 C059_05;
	public String E3164_06;
	public C819_0733 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C056_0236 {
	public String E3413_01;
	public String E3412_02;
}
class CTA35 {
	public String E3139_01;
	public C056_0236 C056_02;
}
class C076_0138 {
	public String E3148_01;
	public String E3155_02;
}
class COM37 {
	public C076_0138 C076_01;
}
class Group2_CTA34 {
	public CTA35 CTA; 
	public List<COM37> COM; 
}
class Group1_NAD27 {
	public NAD28 NAD; 
	public List<Group2_CTA34> Group2_CTA; 
}
class C506_0141 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF40 {
	public C506_0141 C506_01;
}
class C507_0143 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM42 {
	public C507_0143 C507_01;
}
class Group3_RFF39 {
	public RFF40 RFF; 
	public DTM42 DTM; 
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
class C107_0349 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_0450 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX48 {
	public String E4451_01;
	public String E4453_02;
	public C107_0349 C107_03;
	public C108_0450 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C270_0152 {
	public String E6069_01;
	public String E6066_02;
	public String E6411_03;
}
class CNT51 {
	public C270_0152 C270_01;
}
class C503_0255 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class CNI54 {
	public String E1490_01;
	public C503_0255 C503_02;
	public String E1312_03;
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
class C270_0161 {
	public String E6069_01;
	public String E6066_02;
	public String E6411_03;
}
class CNT60 {
	public C270_0161 C270_01;
}
class C601_0164 {
	public String E9015_01;
	public String E1131_02;
	public String E3055_03;
}
class C555_0265 {
	public String E4405_01;
	public String E1131_02;
	public String E3055_03;
	public String E4404_04;
}
class C556_0366 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class C556_0467 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class C556_0568 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class C556_0669 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class C556_0770 {
	public String E9013_01;
	public String E1131_02;
	public String E3055_03;
	public String E9012_04;
}
class STS63 {
	public C601_0164 C601_01;
	public C555_0265 C555_02;
	public C556_0366 C556_03;
	public C556_0467 C556_04;
	public C556_0568 C556_05;
	public C556_0669 C556_06;
	public C556_0770 C556_07;
}
class C506_0172 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF71 {
	public C506_0172 C506_01;
}
class C507_0174 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM73 {
	public C507_0174 C507_01;
}
class C002_0176 {
	public String E1001_01;
	public String E1131_02;
	public String E3055_03;
	public String E1000_04;
}
class C503_0277 {
	public String E1004_01;
	public String E1373_02;
	public String E1366_03;
	public String E3453_04;
	public String E1056_05;
	public String E1060_06;
}
class DOC75 {
	public C002_0176 C002_01;
	public C503_0277 C503_02;
	public String E3153_03;
	public String E1220_04;
	public String E1218_05;
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
class C082_0282 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_0383 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_0484 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_0585 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_0786 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD81 {
	public String E3035_01;
	public C082_0282 C082_02;
	public C058_0383 C058_03;
	public C080_0484 C080_04;
	public C059_0585 C059_05;
	public String E3164_06;
	public C819_0786 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C517_0288 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0389 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0490 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC87 {
	public String E3227_01;
	public C517_0288 C517_02;
	public C519_0389 C519_03;
	public C553_0490 C553_04;
	public String E5479_05;
}
class C210_0292 {
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
class C827_0493 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI91 {
	public String E4233_01;
	public C210_0292 C210_02;
	public String E8275_03;
	public C827_0493 C827_04;
}
class C220_0396 {
	public String E8067_01;
	public String E8066_02;
}
class C228_0497 {
	public String E8179_01;
	public String E8178_02;
}
class C040_0598 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3128_04;
}
class C401_0799 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_08100 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TDT95 {
	public String E8051_01;
	public String E8028_02;
	public C220_0396 C220_03;
	public C228_0497 C228_04;
	public C040_0598 C040_05;
	public String E8101_06;
	public C401_0799 C401_07;
	public C222_08100 C222_08;
	public String E8281_09;
}
class C507_01102 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM101 {
	public C507_01102 C507_01;
}
class C506_01104 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF103 {
	public C506_01104 C506_01;
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
class C507_01111 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM110 {
	public C507_01111 C507_01;
}
class Group7_LOC105 {
	public LOC106 LOC; 
	public List<DTM110> DTM; 
}
class Group6_TDT94 {
	public TDT95 TDT; 
	public List<DTM101> DTM; 
	public List<RFF103> RFF; 
	public List<Group7_LOC105> Group7_LOC; 
}
class C237_02114 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03115 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD113 {
	public String E8053_01;
	public C237_02114 C237_02;
	public C224_03115 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
}
class C502_02117 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03118 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA116 {
	public String E6311_01;
	public C502_02117 C502_02;
	public C174_03118 C174_03;
	public String E7383_04;
}
class C211_02120 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM119 {
	public String E6145_01;
	public C211_02120 C211_02;
}
class C215_02122 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_04123 {
	public String E7402_01;
	public String E7402_02;
}
class SEL121 {
	public String E9308_01;
	public C215_02122 C215_02;
	public String E4517_03;
	public C208_04123 C208_04;
}
class C506_01125 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E4000_04;
	public String E1060_05;
}
class RFF124 {
	public C506_01125 C506_01;
}
class C222_01127 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL126 {
	public C222_01127 C222_01;
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
class C237_02132 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class EQA131 {
	public String E8053_01;
	public C237_02132 C237_02;
}
class C215_02134 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_04135 {
	public String E7402_01;
	public String E7402_02;
}
class SEL133 {
	public String E9308_01;
	public C215_02134 C215_02;
	public String E4517_03;
	public C208_04135 C208_04;
}
class Group9_EQA130 {
	public EQA131 EQA; 
	public List<SEL133> SEL; 
}
class Group8_EQD112 {
	public EQD113 EQD; 
	public List<MEA116> MEA; 
	public List<DIM119> DIM; 
	public List<SEL121> SEL; 
	public List<RFF124> RFF; 
	public List<TPL126> TPL; 
	public TMD128 TMD; 
	public List<Group9_EQA130> Group9_EQA; 
}
class C213_02138 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_03139 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_04140 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_05141 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_06142 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class GID137 {
	public String E1496_01;
	public C213_02138 C213_02;
	public C213_03139 C213_03;
	public C213_04140 C213_04;
	public C213_05141 C213_05;
	public C213_06142 C213_06;
}
class C524_01144 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02145 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN143 {
	public C524_01144 C524_01;
	public C218_02145 C218_02;
}
class C237_01147 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP146 {
	public C237_01147 C237_01;
	public String E7224_02;
}
class C205_02149 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03150 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04151 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09152 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10153 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
}
class DGS148 {
	public String E8273_01;
	public C205_02149 C205_02;
	public C234_03150 C234_03;
	public C223_04151 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09152 C235_09;
	public C236_10153 C236_10;
	public String E8255_11;
	public String E8325_12;
	public String E8211_13;
}
class C107_03155 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04156 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX154 {
	public String E4451_01;
	public String E4453_02;
	public C107_03155 C107_03;
	public C108_04156 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C502_02159 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03160 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA158 {
	public String E6311_01;
	public C502_02159 C502_02;
	public C174_03160 C174_03;
	public String E7383_04;
}
class C523_01162 {
	public String E6350_01;
	public String E6353_02;
}
class EQN161 {
	public C523_01162 C523_01;
}
class Group11_MEA157 {
	public MEA158 MEA; 
	public EQN161 EQN; 
}
class C211_02165 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM164 {
	public String E6145_01;
	public C211_02165 C211_02;
}
class C523_01167 {
	public String E6350_01;
	public String E6353_02;
}
class EQN166 {
	public C523_01167 C523_01;
}
class Group12_DIM163 {
	public DIM164 DIM; 
	public EQN166 EQN; 
}
class C210_02170 {
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
class C827_04171 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI169 {
	public String E4233_01;
	public C210_02170 C210_02;
	public String E8275_03;
	public C827_04171 C827_04;
}
class C208_02173 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03174 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04175 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05176 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06177 {
	public String E7402_01;
	public String E7402_02;
}
class GIN172 {
	public String E7405_01;
	public C208_02173 C208_02;
	public C208_03174 C208_03;
	public C208_04175 C208_04;
	public C208_05176 C208_05;
	public C208_06177 C208_06;
}
class Group13_PCI168 {
	public PCI169 PCI; 
	public List<GIN172> GIN; 
}
class Group10_GID136 {
	public GID137 GID; 
	public List<HAN143> HAN; 
	public List<SGP146> SGP; 
	public List<DGS148> DGS; 
	public List<FTX154> FTX; 
	public List<Group11_MEA157> Group11_MEA; 
	public List<Group12_DIM163> Group12_DIM; 
	public List<Group13_PCI168> Group13_PCI; 
}
class Group5_STS62 {
	public STS63 STS; 
	public List<RFF71> RFF; 
	public List<DTM73> DTM; 
	public DOC75 DOC; 
	public List<FTX78> FTX; 
	public List<NAD81> NAD; 
	public LOC87 LOC; 
	public List<PCI91> PCI; 
	public List<Group6_TDT94> Group6_TDT; 
	public List<Group8_EQD112> Group8_EQD; 
	public List<Group10_GID136> Group10_GID; 
}
class Group4_CNI53 {
	public CNI54 CNI; 
	public List<LOC56> LOC; 
	public List<CNT60> CNT; 
	public List<Group5_STS62> Group5_STS; 
}
class UNT178 {
	public String E0074_01;
	public String E0062_02;
}
class Group_UNH13 {
	public UNH14 UNH; 
	public BGM17 BGM; 
	public List<DTM20> DTM; 
	public TSR22 TSR; 
	public List<Group1_NAD27> Group1_NAD; 
	public List<Group3_RFF39> Group3_RFF; 
	public List<LOC44> LOC; 
	public List<FTX48> FTX; 
	public List<CNT51> CNT; 
	public List<Group4_CNI53> Group4_CNI; 
	public UNT178 UNT; 
}
class UNE179 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ180 {
	public String E0036_01;
	public String E0020_02;
}
