package cs.b2b.core.mapping.bean.edi.edifact.d16a.VERMAS

public class EDI_VERMAS {

	public static final Set<String> MultiElementList = ["UNA","Group_UNH","Group1_RFF","Group2_NAD","Group3_CTA","COM","Group4_EQD","Group4_EQD.RFF","LOC","SEL","Group5_MEA","Group5_MEA.DTM","Group6_TDT","Group6_TDT.RFF","Group7_DOC","Group7_DOC.DTM","Group8_NAD","Group9_CTA"];

	public List<UNA1> UNA; 
	public UNB2 UNB; 
	public UNG8 UNG; 
	public List<Group_UNH13> Group_UNH; 
	public UNE87 UNE; 
	public UNZ88 UNZ; 
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
	public String E1373_05;
	public String E3453_06;
}
class C507_0121 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM20 {
	public C507_0121 C507_01;
}
class C506_0124 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF23 {
	public C506_0124 C506_01;
}
class C507_0126 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM25 {
	public C507_0126 C507_01;
}
class Group1_RFF22 {
	public RFF23 RFF; 
	public DTM25 DTM; 
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
class Group3_CTA34 {
	public CTA35 CTA; 
	public List<COM37> COM; 
}
class Group2_NAD27 {
	public NAD28 NAD; 
	public List<Group3_CTA34> Group3_CTA; 
}
class C237_0241 {
	public String E8260_01;
	public String E1131_02;
	public String E3055_03;
	public String E3207_04;
}
class C224_0342 {
	public String E8155_01;
	public String E1131_02;
	public String E3055_03;
	public String E8154_04;
}
class EQD40 {
	public String E8053_01;
	public C237_0241 C237_02;
	public C224_0342 C224_03;
	public String E8077_04;
	public String E8249_05;
	public String E8169_06;
	public String E4233_07;
}
class C506_0144 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF43 {
	public C506_0144 C506_01;
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
class C215_0250 {
	public String E9303_01;
	public String E1131_02;
	public String E3055_03;
	public String E9302_04;
}
class C208_0451 {
	public String E7402_01;
	public String E7402_02;
}
class SEL49 {
	public String E9308_01;
	public C215_0250 C215_02;
	public String E4517_03;
	public C208_0451 C208_04;
	public String E4525_05;
}
class C502_0254 {
	public String E6313_01;
	public String E6321_02;
	public String E6155_03;
	public String E6154_04;
}
class C174_0355 {
	public String E6411_01;
	public String E6314_02;
	public String E6162_03;
	public String E6152_04;
	public String E6432_05;
}
class MEA53 {
	public String E6311_01;
	public C502_0254 C502_02;
	public C174_0355 C174_03;
	public String E7383_04;
}
class C507_0157 {
	public String E2005_01;
	public String E2380_02;
	public String E2379_03;
}
class DTM56 {
	public C507_0157 C507_01;
}
class Group5_MEA52 {
	public MEA53 MEA; 
	public List<DTM56> DTM; 
}
class C220_0360 {
	public String E8067_01;
	public String E8066_02;
}
class C001_0461 {
	public String E8179_01;
	public String E1131_02;
	public String E3055_03;
	public String E8178_04;
}
class C040_0562 {
	public String E3127_01;
	public String E1131_02;
	public String E3055_03;
	public String E3126_04;
}
class C401_0763 {
	public String E8457_01;
	public String E8459_02;
	public String E7130_03;
}
class C222_0864 {
	public String E8213_01;
	public String E1131_02;
	public String E3055_03;
	public String E8212_04;
	public String E8453_05;
}
class C003_1065 {
	public String E7041_01;
	public String E1131_02;
	public String E3055_03;
	public String E7040_04;
}
class TDT59 {
	public String E8051_01;
	public String E8028_02;
	public C220_0360 C220_03;
	public C001_0461 C001_04;
	public C040_0562 C040_05;
	public String E8101_06;
	public C401_0763 C401_07;
	public C222_0864 C222_08;
	public String E8281_09;
	public C003_1065 C003_10;
}
class C506_0167 {
	public String E1153_01;
	public String E1154_02;
	public String E1156_03;
	public String E1056_04;
	public String E1060_05;
}
class RFF66 {
	public C506_0167 C506_01;
}
class Group6_TDT58 {
	public TDT59 TDT; 
	public List<RFF66> RFF; 
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
class C082_0276 {
	public String E3039_01;
	public String E1131_02;
	public String E3055_03;
}
class C058_0377 {
	public String E3124_01;
	public String E3124_02;
	public String E3124_03;
	public String E3124_04;
	public String E3124_05;
}
class C080_0478 {
	public String E3036_01;
	public String E3036_02;
	public String E3036_03;
	public String E3036_04;
	public String E3036_05;
	public String E3045_06;
}
class C059_0579 {
	public String E3042_01;
	public String E3042_02;
	public String E3042_03;
	public String E3042_04;
}
class C819_0780 {
	public String E3229_01;
	public String E1131_02;
	public String E3055_03;
	public String E3228_04;
}
class NAD75 {
	public String E3035_01;
	public C082_0276 C082_02;
	public C058_0377 C058_03;
	public C080_0478 C080_04;
	public C059_0579 C059_05;
	public String E3164_06;
	public C819_0780 C819_07;
	public String E3251_08;
	public String E3207_09;
}
class C056_0283 {
	public String E3413_01;
	public String E3412_02;
}
class CTA82 {
	public String E3139_01;
	public C056_0283 C056_02;
}
class C076_0185 {
	public String E3148_01;
	public String E3155_02;
}
class COM84 {
	public C076_0185 C076_01;
}
class Group9_CTA81 {
	public CTA82 CTA; 
	public List<COM84> COM; 
}
class Group8_NAD74 {
	public NAD75 NAD; 
	public List<Group9_CTA81> Group9_CTA; 
}
class Group7_DOC68 {
	public DOC69 DOC; 
	public List<DTM72> DTM; 
	public List<Group8_NAD74> Group8_NAD; 
}
class Group4_EQD39 {
	public EQD40 EQD; 
	public List<RFF43> RFF; 
	public List<LOC45> LOC; 
	public List<SEL49> SEL; 
	public List<Group5_MEA52> Group5_MEA; 
	public List<Group6_TDT58> Group6_TDT; 
	public List<Group7_DOC68> Group7_DOC; 
}
class UNT86 {
	public String E0074_01;
	public String E0062_02;
}
class Group_UNH13 {
	public UNH14 UNH; 
	public BGM17 BGM; 
	public DTM20 DTM; 
	public List<Group1_RFF22> Group1_RFF; 
	public List<Group2_NAD27> Group2_NAD; 
	public List<Group4_EQD39> Group4_EQD; 
	public UNT86 UNT; 
}
class UNE87 {
	public String E0060_01;
	public String E0048_02;
}
class UNZ88 {
	public String E0036_01;
	public String E0020_02;
}
