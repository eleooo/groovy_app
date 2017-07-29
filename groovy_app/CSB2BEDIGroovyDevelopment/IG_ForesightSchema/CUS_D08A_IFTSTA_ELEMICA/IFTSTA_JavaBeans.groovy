package cs.b2b.core.mapping.bean.edi.edifact.d08a.IFTSTA

public class EDI_ELEMICA_IFTSTA {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","Group_UNH.DTM","Group1_NAD","Group2_CTA","COM","Group3_RFF","Group_UNH.LOC","FTX","CNT","Group4_CNI","Group4_CNI.LOC","Group5_STS","Group5_STS.RFF","Group5_STS.DTM","Group6_NAD","Group7_CTA","Group5_STS.PCI","Group8_TDT","Group8_TDT.DTM","Group8_TDT.RFF","Group9_LOC","Group9_LOC.DTM","Group10_EQD","Group10_EQD.MEA","Group10_EQD.DIM","SEL","Group10_EQD.RFF","TPL","Group11_LOC","Group11_LOC.DTM","Group12_EQA","Group13_LOC","Group13_LOC.DTM","Group14_GID","HAN","SGP","DGS","GDS","Group14_GID.LOC","QTY","Group15_MEA","Group16_DIM","Group17_PCI","GIN","Group18_RFF","Group18_RFF.DTM"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE212 UNE; 
	public UNZ213 UNZ; 
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
	public String E1056_04;
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
	public String E1056_04;
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
class C056_0290 {
	public String E3413_01;
	public String E3412_02;
}
class CTA89 {
	public String E3139_01;
	public C056_0290 C056_02;
}
class C076_0192 {
	public String E3148_01;
	public String E3155_02;
}
class COM91 {
	public C076_0192 C076_01;
}
class Group7_CTA88 {
	public CTA89 CTA; 
	public List<COM91> COM; 
}
class Group6_NAD81 {
	public NAD82 NAD; 
	public List<Group7_CTA88> Group7_CTA; 
}
class C517_0294 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_0395 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_0496 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC93 {
	public String E3227_01;
	public C517_0294 C517_02;
	public C519_0395 C519_03;
	public C553_0496 C553_04;
	public String E5479_05;
}
class C210_0298 {
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
class C827_0499 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI97 {
	public String E4233_01;
	public C210_0298 C210_02;
	public String E8169_03;
	public C827_0499 C827_04;
}
class C220_03102 {
	public String E8067_01;
	public String E8066_02;
}
class C001_04103 {
	public String E8179_01;
	public String E1131_02;
	public String E3055_03;
	public String E8178_04;
}
class C040_05104 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3126_04;
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
	public C001_04103 C001_04;
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
class C506_01110 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF109 {
	public C506_01110 C506_01;
}
class C517_02113 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03114 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04115 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC112 {
	public String E3227_01;
	public C517_02113 C517_02;
	public C519_03114 C519_03;
	public C553_04115 C553_04;
	public String E5479_05;
}
class C507_01117 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM116 {
	public C507_01117 C507_01;
}
class Group9_LOC111 {
	public LOC112 LOC; 
	public List<DTM116> DTM; 
}
class Group8_TDT100 {
	public TDT101 TDT; 
	public List<DTM107> DTM; 
	public List<RFF109> RFF; 
	public List<Group9_LOC111> Group9_LOC; 
}
class C237_02120 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_03121 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD119 {
	public String E8053_01;
	public C237_02120 C237_02;
	public C224_03121 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
	public String E4233_07;
}
class C502_02123 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03124 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA122 {
	public String E6311_01;
	public C502_02123 C502_02;
	public C174_03124 C174_03;
	public String E7383_04;
}
class C211_02126 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM125 {
	public String E6145_01;
	public C211_02126 C211_02;
}
class C215_02128 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_04129 {
	public String E7402_01;
	public String E7402_02;
}
class SEL127 {
	public String E9308_01;
	public C215_02128 C215_02;
	public String E4517_03;
	public C208_04129 C208_04;
	public String E4525_05;
}
class C506_01131 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF130 {
	public C506_01131 C506_01;
}
class C222_01133 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class TPL132 {
	public C222_01133 C222_01;
}
class C219_01135 {
	public String E8335_01;
	public String E8334_02;
}
class TMD134 {
	public C219_01135 C219_01;
	public String E8332_02;
	public String E8341_03;
}
class C517_02138 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03139 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04140 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC137 {
	public String E3227_01;
	public C517_02138 C517_02;
	public C519_03139 C519_03;
	public C553_04140 C553_04;
	public String E5479_05;
}
class C507_01142 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM141 {
	public C507_01142 C507_01;
}
class Group11_LOC136 {
	public LOC137 LOC; 
	public List<DTM141> DTM; 
}
class C237_02145 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class EQA144 {
	public String E8053_01;
	public C237_02145 C237_02;
}
class C215_02147 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_04148 {
	public String E7402_01;
	public String E7402_02;
}
class SEL146 {
	public String E9308_01;
	public C215_02147 C215_02;
	public String E4517_03;
	public C208_04148 C208_04;
	public String E4525_05;
}
class C517_02151 {
	public String E3225_01;
	public String E1131_02;
	public String E3055_03;
	public String E3224_04;
}
class C519_03152 {
	public String E3223_01;
	public String E1131_02;
	public String E3055_03;
	public String E3222_04;
}
class C553_04153 {
	public String E3233_01;
	public String E1131_02;
	public String E3055_03;
	public String E3232_04;
}
class LOC150 {
	public String E3227_01;
	public C517_02151 C517_02;
	public C519_03152 C519_03;
	public C553_04153 C553_04;
	public String E5479_05;
}
class C507_01155 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM154 {
	public C507_01155 C507_01;
}
class Group13_LOC149 {
	public LOC150 LOC; 
	public List<DTM154> DTM; 
}
class Group12_EQA143 {
	public EQA144 EQA; 
	public List<SEL146> SEL; 
	public List<Group13_LOC149> Group13_LOC; 
}
class Group10_EQD118 {
	public EQD119 EQD; 
	public List<MEA122> MEA; 
	public List<DIM125> DIM; 
	public List<SEL127> SEL; 
	public List<RFF130> RFF; 
	public List<TPL132> TPL; 
	public TMD134 TMD; 
	public List<Group11_LOC136> Group11_LOC; 
	public List<Group12_EQA143> Group12_EQA; 
}
class C213_02158 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_03159 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_04160 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_05161 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class C213_06162 {
	public String E7224_01;
	public String E7065_02;
	public String E1131_03;
	public String E3055_04;
	public String E7064_05;
	public String E7233_06;
}
class GID157 {
	public String E1496_01;
	public C213_02158 C213_02;
	public C213_03159 C213_03;
	public C213_04160 C213_04;
	public C213_05161 C213_05;
	public C213_06162 C213_06;
}
class C524_01164 {
	public String E4079_01;
	public String E1131_02;
	public String E3055_03;
	public String E4078_04;
}
class C218_02165 {
	public String E7419_01;
	public String E1131_02;
	public String E3055_03;
	public String E7418_04;
}
class HAN163 {
	public C524_01164 C524_01;
	public C218_02165 C218_02;
}
class C237_01167 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class SGP166 {
	public C237_01167 C237_01;
	public String E7224_02;
}
class C205_02169 {
	public String E8351_01;
	public String E8078_02;
	public String E8092_03;
}
class C234_03170 {
	public String E7124_01;
	public String E7088_02;
}
class C223_04171 {
	public String E7106_01;
	public String E6411_02;
}
class C235_09172 {
	public String E8158_01;
	public String E8186_02;
}
class C236_10173 {
	public String E8246_01;
	public String E8246_02;
	public String E8246_03;
	public String E8246_04;
}
class DGS168 {
	public String E8273_01;
	public C205_02169 C205_02;
	public C234_03170 C234_03;
	public C223_04171 C223_04;
	public String E8339_05;
	public String E8364_06;
	public String E8410_07;
	public String E8126_08;
	public C235_09172 C235_09;
	public C236_10173 C236_10;
	public String E8255_11;
	public String E8179_12;
	public String E8211_13;
}
class C107_03175 {
	public String E4441_01;
	public String E1131_02;
	public String E3055_03;
}
class C108_04176 {
	public String E4440_01;
	public String E4440_02;
	public String E4440_03;
	public String E4440_04;
	public String E4440_05;
}
class FTX174 {
	public String E4451_01;
	public String E4453_02;
	public C107_03175 C107_03;
	public C108_04176 C108_04;
	public String E3453_05;
	public String E4447_06;
}
class C703_01178 {
	public String E7085_01;
	public String E1131_02;
	public String E3055_03;
}
class GDS177 {
	public C703_01178 C703_01;
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
class C186_01184 {
	public String E6063_01;
	public String E6060_02;
	public String E6411_03;
}
class QTY183 {
	public C186_01184 C186_01;
}
class C502_02187 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_03188 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA186 {
	public String E6311_01;
	public C502_02187 C502_02;
	public C174_03188 C174_03;
	public String E7383_04;
}
class C523_01190 {
	public String E6350_01;
	public String E6353_02;
}
class EQN189 {
	public C523_01190 C523_01;
}
class Group15_MEA185 {
	public MEA186 MEA; 
	public EQN189 EQN; 
}
class C211_02193 {
	public String E6411_01;
	public String E6168_02;
	public String E6140_03;
	public String E6008_04;
}
class DIM192 {
	public String E6145_01;
	public C211_02193 C211_02;
}
class C523_01195 {
	public String E6350_01;
	public String E6353_02;
}
class EQN194 {
	public C523_01195 C523_01;
}
class Group16_DIM191 {
	public DIM192 DIM; 
	public EQN194 EQN; 
}
class C210_02198 {
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
class C827_04199 {
	public String E7511_01;
	public String E1131_02;
	public String E3055_03;
}
class PCI197 {
	public String E4233_01;
	public C210_02198 C210_02;
	public String E8169_03;
	public C827_04199 C827_04;
}
class C208_02201 {
	public String E7402_01;
	public String E7402_02;
}
class C208_03202 {
	public String E7402_01;
	public String E7402_02;
}
class C208_04203 {
	public String E7402_01;
	public String E7402_02;
}
class C208_05204 {
	public String E7402_01;
	public String E7402_02;
}
class C208_06205 {
	public String E7402_01;
	public String E7402_02;
}
class GIN200 {
	public String E7405_01;
	public C208_02201 C208_02;
	public C208_03202 C208_03;
	public C208_04203 C208_04;
	public C208_05204 C208_05;
	public C208_06205 C208_06;
}
class Group17_PCI196 {
	public PCI197 PCI; 
	public List<GIN200> GIN; 
}
class C506_01208 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF207 {
	public C506_01208 C506_01;
}
class C507_01210 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM209 {
	public C507_01210 C507_01;
}
class Group18_RFF206 {
	public RFF207 RFF; 
	public List<DTM209> DTM; 
}
class Group14_GID156 {
	public GID157 GID; 
	public List<HAN163> HAN; 
	public List<SGP166> SGP; 
	public List<DGS168> DGS; 
	public List<FTX174> FTX; 
	public List<GDS177> GDS; 
	public List<LOC179> LOC; 
	public List<QTY183> QTY; 
	public List<Group15_MEA185> Group15_MEA; 
	public List<Group16_DIM191> Group16_DIM; 
	public List<Group17_PCI196> Group17_PCI; 
	public List<Group18_RFF206> Group18_RFF; 
}
class Group5_STS62 {
	public STS63 STS; 
	public List<RFF71> RFF; 
	public List<DTM73> DTM; 
	public DOC75 DOC; 
	public List<FTX78> FTX; 
	public List<Group6_NAD81> Group6_NAD; 
	public LOC93 LOC; 
	public List<PCI97> PCI; 
	public List<Group8_TDT100> Group8_TDT; 
	public List<Group10_EQD118> Group10_EQD; 
	public List<Group14_GID156> Group14_GID; 
}
class Group4_CNI53 {
	public CNI54 CNI; 
	public List<LOC56> LOC; 
	public List<CNT60> CNT; 
	public List<Group5_STS62> Group5_STS; 
}
class UNT211 {
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
	public UNT211 UNT; 
}
class UNE212 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ213 {
	public String E0036_01;
	public String E0020_02;
}
