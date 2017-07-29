package cs.b2b.core.mapping.api.autori;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import cs.b2b.core.common.util.LocalFileUtil;
import cs.b2b.core.mapping.api.ApiSqlGenerator;
import cs.b2b.mapping.e2e.util.ConnectionForTester;
import cs.b2b.mapping.e2e.util.SQLGenerator_GroovyScript;

public class RIAutoGenerator {

	public static void main(String[] args) {
		try {
			String workingFolder = "D:/kukri_sql/auto_generate_ri/";
			String sendDataFolder = workingFolder+"/testing_ri_data";
			
			String configFile = workingFolder + "tp_list.txt";
			File fconfig = new File(configFile);
			if (! fconfig.exists()) {
				throw new Exception("No found testing config file : "+fconfig.getAbsolutePath());
			}
			
			String messageType = "";
			String dirId = "";
			String msgFmtId = "";
			
			String mappingClassName = "";
			String newGuideLineID = "";
			String definitionName = "";
			
			List<String> tps = new ArrayList<String>();
			
			String configBody = LocalFileUtil.readBigFile(fconfig.getAbsolutePath());
			String[] lines = configBody.split("\r\n");
			boolean isTpListStart = false;
			for(String str : lines) {
				if (str==null)
					continue;
				
				if (str.startsWith("MessageType=")) {
					messageType = str.substring("MessageType=".length()).trim();
				}
				if (str.startsWith("DirID=")) {
					dirId = str.substring("DirID=".length()).trim();
				}
				if (str.startsWith("MessageFormatID=")) {
					msgFmtId = str.substring("MessageFormatID=".length()).trim();
				}
				if (str.startsWith("mappingClassName=")) {
					mappingClassName = str.substring("mappingClassName=".length()).trim();
				}
				if (str.startsWith("newGuideLineID=")) {
					newGuideLineID = str.substring("newGuideLineID=".length()).trim();
				}
				if (str.startsWith("definitionName=")) {
					definitionName = str.substring("definitionName=".length()).trim();
				}
						
				if (str.startsWith("## TP list here")) {
					isTpListStart = true;
					continue;
				}
				if (str.startsWith("##")) {
					continue;
				}
				if (isTpListStart) {
					if (str.trim().length()>0) {
						tps.add(str.trim());
					}
				}
			}
			
			System.out.println("Message Type : "+messageType);
			System.out.println("Dir ID : "+dirId);
			System.out.println("Msg Format ID : "+msgFmtId);
			System.out.println("TP size: "+tps.size());
			System.out.println("-------<TP List Start>-----------");
			for(String s : tps) {
				System.out.println(s);
			}
			System.out.println("-------<TP List End>-----------");
			
			String tpFolder = "";
			
			String guidelineSwitchSql = LocalFileUtil.readBigFile("./sqls/template/template_ri_migration_swith_guideline.sql");
			
			ConnectionForTester connUtil = new ConnectionForTester();
			Connection conn = null;
			
			try {
				conn = connUtil.getB2BEDIQA1_DEV_DBConn();
				
				for(String tpId : tps) {
					//check if db has success record or not
					DBOperation dbo = new DBOperation();
					String ediOutputBody = dbo.getOutputFile(tpId, messageType, dirId, 1, conn);
					if (ediOutputBody==null) {
						ediOutputBody = "";
					} else if (ediOutputBody.trim().length()>0) {
						println("found completed msg in db already.");
					}
					//1, send file to qa env
					int sendCount = 0;
					if (ediOutputBody.trim().length()==0) {
						CS2ShootFile sender = new CS2ShootFile();
						sendCount = sender.shoot( tpId, messageType, dirId, "QA1", sendDataFolder);
						println("shoot file to EMS done.");
					}
					
					//2, check db and download output file
					// get from step "Multiple Channel Interface JMS sender"
					// MCI / E2P / MD2SD
					if (sendCount>0 || ediOutputBody.trim().length()>0) {
						if (ediOutputBody.trim().length()==0) {
							println("get msg body from db ...");
							ediOutputBody = dbo.getOutputFile(tpId, messageType, dirId, 5, conn);
						}
						if (ediOutputBody!=null && ediOutputBody.trim().length()>0) {
							
							println("start sql generation ...");
							
							//create sql folder
							tpFolder = workingFolder + "/" + tpId + "/";
							File tpFile = new File(tpFolder);
							if (! tpFile.exists()) {
								tpFile.mkdirs();
							}
							//2.1, generate IG sql
							BelugaCfgDetection belugaCfg = new BelugaCfgDetection();
							String jsonStr = belugaCfg.generateBelugaConfigSql(tpId, messageType, dirId, msgFmtId, ediOutputBody, definitionName, conn);
							if (jsonStr!=null && jsonStr.trim().length()>0) {
								File jsonFile = new File(tpFolder+"ig_"+tpId+"_"+messageType+"_"+dirId+".json");
								LocalFileUtil.writeToFile(jsonFile.getAbsolutePath(), jsonStr, false);
								ApiSqlGenerator.callDefinitionGenerator(jsonFile);
							}
							
							//2.2, generate mapping sql
							if (mappingClassName!=null && mappingClassName.trim().length()>0) {
								String mappingFile = mappingClassName;
								if (mappingFile.endsWith(".groovy")) {
									mappingFile = mappingFile.substring(0, mappingFile.lastIndexOf(".groovy"));
								}
								mappingFile = mappingFile.replace(".", "/");
								//real location
								mappingFile = "./src/" + mappingFile + ".groovy";
								File mf = new File(mappingFile);
								if (!mf.exists()) {
									throw new Exception("Cannot find mapping script: "+mf.getAbsolutePath());
								}
								
								SQLGenerator_GroovyScript pmtUtil = new SQLGenerator_GroovyScript();
								pmtUtil.groovyScriptFileFullPath = mappingFile;
								pmtUtil.tpId = tpId;
								pmtUtil.msgTypeId = messageType;
								pmtUtil.dirId = dirId;
								pmtUtil.msgFmtId = msgFmtId;
								pmtUtil.sqlOutputFilePath = tpFolder;
								pmtUtil.sqlFileStarter = "2_"+tpId+"_"+messageType+"";
								pmtUtil.generateSqlForScript();
							}
							//2.3, generate guideline switching sql
							String switchSql = guidelineSwitchSql.replace("@TP_ID", tpId);
							switchSql = switchSql.replace("@MSG_TYPE_ID", messageType);
							switchSql = switchSql.replace("@DIR_ID", dirId);
							switchSql = switchSql.replace("@NEW_GDL_ID", newGuideLineID);
							
							String fileSwitchGdl = tpFolder + "/3_"+tpId+"_"+messageType+"_switch_guideline.sql";
							LocalFileUtil.writeToFile(fileSwitchGdl, switchSql, false);
							
							println("---> switch guideline id sql done.");
							
						} else {
							System.err.println("No success msg sent, please check.....");
							System.err.println("No success msg sent, please check.....");
							System.err.println("No success msg sent, please check.....");
						}
						
					} else {
						System.err.println("No success msg sent, please check.....");
					}
				}
			} finally {
				if (conn!=null) 
					conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		println("done.");
	}
	
	public static void println(String str) {
		System.out.println(str);
	}
}
