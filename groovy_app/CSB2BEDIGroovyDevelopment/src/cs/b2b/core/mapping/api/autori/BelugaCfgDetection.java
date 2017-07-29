package cs.b2b.core.mapping.api.autori;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import cs.b2b.beluga.common.datahandle.DataUtil;
import cs.b2b.core.common.util.LocalFileUtil;
import cs.b2b.mapping.e2e.util.ConnectionForTester;

public class BelugaCfgDetection {
	
	String subElementDelimiter = "";
	String elementDelimiter = "";
	String recordDelimiter = "";
	boolean isX12 = false;
	boolean isEdifact = false;
	
	public String generateBelugaConfigSql(String tpId, String messageType, String dirId, String msgFmtId, String ediOutputBody, String definitionName, Connection conn) throws Exception {
		println("tpId: "+tpId+", messageType: "+messageType+", dirId: "+dirId+", msgFmtId: "+msgFmtId+", definitionName: "+definitionName);
		println("ediOutputBody: "+(ediOutputBody.length()>100?ediOutputBody.substring(0, 100):ediOutputBody));
		if (ediOutputBody.startsWith("ISA")) {
			isX12 = true;
		} else if (ediOutputBody.startsWith("UNA")) {
			isEdifact = true;
		} else if (ediOutputBody.startsWith("UNB")) {
			isEdifact = true;
		}
		return autoDetectDelimiter(ediOutputBody, isX12, isEdifact, definitionName, tpId, messageType, dirId, msgFmtId, conn);
	}
	
	public static void println(String str) {
		System.out.println(str);
	}
	
	private String autoDetectDelimiter(String ediBody, boolean isX12, boolean isEdifact, String definitionName, String tpId, String msgType, String dirId, String msgFmtId, Connection conn) throws Exception {
		if (!isX12 && !isEdifact) {
			throw new Exception("Auto delimiter recognizer is not work as isX12 and isEdifact are false.");
		}
		String ret = "";
		if (isX12) {
			//X12, detect ISA / GS for recordDelimiter and elementDelimiter
			subElementDelimiter = "";
			if (ediBody==null || ediBody.length()<200 || !ediBody.startsWith("ISA")) {
				throw new Exception("Failed on delimiter recognize, X.12 data cannot recognized: '"+ediBody+"'. ");
			}
			String bodyStarter = ediBody.substring(0, 200);
			elementDelimiter = bodyStarter.substring(3, 4);
			
			String isa17 = "";
			String[] isas = bodyStarter.split(DataUtil.splitCharEncode(elementDelimiter));
			if (isas.length<17) {
				throw new Exception("Auto recognize, cannot get ISA 16 for analysis.");
			} else {
				//>~\r\nGS
				isa17 = isas[16];
				//20170516
				if (isa17.length()>0) {
					subElementDelimiter = isa17.substring(0, 1);
					
					isa17 = isa17.substring(1, isa17.length());
					isa17 = isa17.replace("GS", "");
					isa17 = isa17.replace("ST", "");
					recordDelimiter = isa17;
					if (!recordDelimiter.equals("\r\n") && !recordDelimiter.equals("\n")) {
						recordDelimiter = recordDelimiter.replace("\r\n", "");
						recordDelimiter = recordDelimiter.replace("\n", "");
					}
				}
			}
		} else {
			//EDIFACT, 
			//if start with UNA, then use UNA data for delimiter
			if (ediBody.startsWith("UNA")) {
				subElementDelimiter = ediBody.substring(3, 4);
				elementDelimiter = ediBody.substring(4, 5);
				recordDelimiter = ediBody.substring(8, 9);
				//check if record delimiter has \r\n or \n
				String temp1 = ediBody.substring(9, 10);
				//String temp2 = ediBody.substring(9, 11);
				if (recordDelimiter.equals("\r")) {
					//recordDelimiter is \r\n
					if (temp1.equals("\n")) {
						recordDelimiter = "\r\n";
					} else {
						throw new Exception("Unexpected \r as record delimiter for EDIFACT file.");
					}
				} else if (recordDelimiter.equals("\n")) {
					//recordDelimiter is \n
					//nothing todo, keep \n
				} else {
//					if (temp1.equals("\n")) {
//						//recordDelimiter += "\n";
//					} else if (temp2.equals("\r\n")) {
//						//recordDelimiter += "\r\n";
//					}
				}
			} else if (ediBody.startsWith("UNB") && ediBody.contains("UNH")) {
				//check UNB if not start with UNA
				elementDelimiter = ediBody.substring(3, 4);
				subElementDelimiter = ediBody.substring(8, 9);
				
				String starter = ediBody.substring(0, ediBody.indexOf("UNH"));
				String temp1 = starter.substring(starter.length()-3, starter.length()-2);
				String temp2 = starter.substring(starter.length()-2, starter.length()-1);
				String temp3 = starter.substring(starter.length()-1, starter.length());
				
				if (temp2.equals("\r") && temp3.equals("\n")) {
					if (checkDelimiterImpl(temp1)) {
						throw new Exception("Failed on delimiter checking, record delimiter cannot be space or linefeed only for edifact.");
					}
					//20170215 not following \r\n in record delimiter
					recordDelimiter = temp1;
				} else if (temp3.equals("\n")) {
					if (checkDelimiterImpl(temp2)) {
						throw new Exception("Failed on delimiter checking, record delimiter cannot be space or linefeed only for edifact.");
					}
					recordDelimiter = temp2;
				} else {
					if (checkDelimiterImpl(temp3)) {
						throw new Exception("Failed on delimiter checking, record delimiter cannot be space or linefeed only for edifact.");
					}
					recordDelimiter = temp3;
				}
			} else {
				throw new Exception("Failed on recognized as edifact is not start with UNA or UNB, you can close auto recognize feature in definition if the file is correct format, first 100 chars are: "+
						(ediBody!=null&&ediBody.length()>100?ediBody.substring(0, 100):ediBody)+".");
			}
		}
		
		checkDelimiter();
		
		String outJson = "";
		if (isX12) {
			outJson = LocalFileUtil.readBigFile("./sqls/template/BelugaCfgJsonTemplate_X12.txt");
			outJson = outJson.replace("$DEFINITION$", definitionName);
			outJson = outJson.replace("$TP_ID$", tpId);
			outJson = outJson.replace("$MSG_TYPE_ID$", msgType);
			outJson = outJson.replace("$DIR_ID$", dirId);
			outJson = outJson.replace("$MSG_FMT_ID$", msgFmtId);
			outJson = outJson.replace("$OPERATION$", "x2e");
			outJson = outJson.replace("$CTRL_SENDER$", "CARGOSMART");
			outJson = outJson.replace("$CTRL_RECEIVER$", tpId);
			outJson = outJson.replace("$MSG_TYPE_ID$", msgType);
			outJson = outJson.replace("$MSG_FMT_ID$", msgFmtId);
			
			
			outJson = outJson.replace("$RECORD_DELIMITER$", recordDelimiter);
			outJson = outJson.replace("$ELEMENT_DELIMITER$", elementDelimiter);
			outJson = outJson.replace("$SUBELE_DELIMITER$", ""); //subElementDelimiter
			
			String x12Replacer = getReplacerFromDb(tpId, msgType, dirId, conn);
			
			outJson = outJson.replace("$X12_REPLACER$", x12Replacer);
			outJson = outJson.replace("$ESCAPE_CHAR$", "");
			
			String header = ediBody.substring(0, ediBody.indexOf("ST"+elementDelimiter));
			String stLine = ediBody.substring(ediBody.indexOf("ST"+elementDelimiter));
			stLine = stLine.replace("ST"+elementDelimiter, "");
			
			String isas = header.substring(0, header.indexOf(recordDelimiter));
			String gses = header.substring(header.indexOf(recordDelimiter)+recordDelimiter.length());
			gses = gses.replace(recordDelimiter, "");
			
			println("subElementDelimiter = "+subElementDelimiter + ", elementDelimiter = "+elementDelimiter+", recordDelimiter = "+recordDelimiter);
			println("isas: "+isas);
			println("gses: "+gses);
			
			String[] isaeles = isas.split(DataUtil.splitCharEncode(elementDelimiter));
			String[] gseles = gses.split(DataUtil.splitCharEncode(elementDelimiter));
			
			outJson = outJson.replace("$ISA_01$", isaeles[1]);
			outJson = outJson.replace("$ISA_02$", isaeles[2]);
			outJson = outJson.replace("$ISA_03$", isaeles[3]);
			outJson = outJson.replace("$ISA_04$", isaeles[4]);
			outJson = outJson.replace("$ISA_05$", isaeles[5]);
			outJson = outJson.replace("$ISA_06$", isaeles[6]);
			outJson = outJson.replace("$ISA_07$", isaeles[7]);
			outJson = outJson.replace("$ISA_08$", isaeles[8]);
			if (isaeles[9].length()==6) {
				outJson = outJson.replace("$ISA_09$", "%yyMMdd%");
			} else if (isaeles[9].length()==8) {
				outJson = outJson.replace("$ISA_09$", "%yyyyMMdd%");
			} else {
				throw new Exception("Unknow ISA_09 format");
			}
			if (isaeles[10].length()==4) {
				outJson = outJson.replace("$ISA_10$", "%HHmm%");
			} else if (isaeles[10].length()==6) {
				outJson = outJson.replace("$ISA_10$", "%HHmmss%");
			} else {
				throw new Exception("Unknow ISA_10 format");
			}
			outJson = outJson.replace("$ISA_11$", isaeles[11]);
			outJson = outJson.replace("$ISA_12$", isaeles[12]);
			outJson = outJson.replace("$ISA_13$", "%EDI_CTRL_NUM%");
			outJson = outJson.replace("$ISA_14$", isaeles[14]);
			outJson = outJson.replace("$ISA_15$", isaeles[15]);
			outJson = outJson.replace("$ISA_16$", isaeles[16]);
			
			//GS part
			outJson = outJson.replace("$GS_01$", gseles[1]);
			outJson = outJson.replace("$GS_02$", gseles[2]);
			outJson = outJson.replace("$GS_03$", gseles[3]);
			if (gseles[4].length()==8) {
				outJson = outJson.replace("$GS_04$", "%yyyyMMdd%");
			} else if (gseles[4].length()==6) {
				outJson = outJson.replace("$GS_04$", "%yyMMdd%");
			} else {
				throw new Exception("Unknow GS_04 format");
			}
			if (gseles[5].length()==4) {
				outJson = outJson.replace("$GS_05$", "%HHmm%");
			} else if (gseles[5].length()==6) {
				outJson = outJson.replace("$GS_05$", "%HHmmss%");
			} else {
				throw new Exception("Unknow GS_05 format");
			}
			outJson = outJson.replace("$GS_06$", "%GROUP_CTRL_NUM%");
			outJson = outJson.replace("$GS_07$", gseles[7]);
			outJson = outJson.replace("$GS_08$", gseles[8]);
			
			String st01 = stLine.substring(0, stLine.indexOf(elementDelimiter));
			outJson = outJson.replace("$ST_01$", st01);
			ret = outJson;
			println(outJson);
		} else if (isEdifact) {
			throw new Exception("Unsupport EDIFACT format yet, find David.");
		}
		
		return ret;
	}
	
	private String getReplacerFromDb(String tpId, String msgType, String dirId, Connection conn) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String ret = " ";
		try {
			pst = conn.prepareStatement("select delimiter_type, delimiter_char, replace_type, replace_char from b2b_char_convert where tp_id=? and msg_type_id=? and dir_id=?");
			pst.setString(1, tpId);
			pst.setString(2, msgType);
			pst.setString(3, dirId);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getString("replace_char")!=null) {
					ret = rs.getString("replace_char");
				}
			} else {
				rs.close();
				pst.close();
				
				pst = conn.prepareStatement("SELECT TP_ID, PARTNER_DISPLAY_NAME, HOST_DISPLAY_NAME FROM B2B_TP_BIZ_AGREEMENTS WHERE TP_ID = ? AND MSG_TYPE_ID = ?");
				pst.setString(1, tpId);
				pst.setString(2, msgType);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rs.getString("PARTNER_DISPLAY_NAME")!=null) {
						String bizName = rs.getString("PARTNER_DISPLAY_NAME");
						if (bizName.equals("GTNEXUS") && msgType.equals("BC")) {
							ret = "^";
						}
					}
				}
			}
		} finally {
			if (rs!=null) {
				rs.close();
			}
			if (pst!=null) {
				pst.close();
			}
		}
		return ret;
	}
	
	private void checkDelimiter() throws Exception {
		if (isEdifact && checkDelimiterImpl(subElementDelimiter)) {
			throw new Exception("Auto recongize, invalid sub-element delimiter found: '"+subElementDelimiter+"' ("+((int)subElementDelimiter.charAt(0))+"). ");
		}
		if (checkDelimiterImpl(elementDelimiter)) {
			throw new Exception("Auto recongize, invalid element delimiter found: '"+elementDelimiter+"' ("+((int)elementDelimiter.charAt(0))+"). ");
		}
		if (recordDelimiter==null || recordDelimiter.equals(" ")) {
			throw new Exception("Auto recongize, invalid record delimiter found: '"+recordDelimiter+"'. ");
		}
		String _tmp = recordDelimiter;
		_tmp = _tmp.replace("\r\n", "");
		_tmp = _tmp.replace("\n", "");
		if (_tmp.length()>1) {
			throw new Exception("Auto recongize, invalid record delimiter found: '"+recordDelimiter+"'. ");
		} else if (_tmp.length()==1) {
			if (checkDelimiterImpl(_tmp)) {
				throw new Exception("Auto recongize, invalid record delimiter found: '"+_tmp+"' ("+((int)_tmp.charAt(0))+"). ");
			}
		}
	}
	
	private boolean checkDelimiterImpl(String str) {
		if (str==null || str.replace(" ", "").length()==0) {
			return true;
		}
		int iSubEle = (int)str.charAt(0);
		if (iSubEle>=48 && iSubEle<=57) {
			// 0 - 9
			return true;
		} else if (iSubEle>=65 && iSubEle<=90) {
			//A - Z
			return true;
		} else if (iSubEle>=97 && iSubEle<=122) {
			//a - z
			return true;
		}
		return false;
	}
	
	
	public static void main(String[] args) {
		Connection conn = null;
		try {
			ConnectionForTester util = new ConnectionForTester();
			conn = util.getB2BEDIQA1_DEV_DBConn();
			
			String file = "D:/_1_Beluga/edi_samples/edi/301_EDI2017012318420124-43.edi";
			
			String body = LocalFileUtil.readBigFile(file);
			
			BelugaCfgDetection det = new BelugaCfgDetection();
			
			String out = det.generateBelugaConfigSql("CARGILLGTN", "BC", "O", "X.12", body, "CUS_4010_301_CS.xml", conn);
			
			System.out.println(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn!=null) {
				try { conn.close(); } catch (Exception e) {}
			}
		}
	}
}
