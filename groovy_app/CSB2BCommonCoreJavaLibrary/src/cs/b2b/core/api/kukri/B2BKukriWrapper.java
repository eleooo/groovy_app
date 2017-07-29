package cs.b2b.core.api.kukri;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.cargosmart.b2b.fwk2.schemas.msg.Msg;

import cs.b2b.core.common.classloader.GroovyScriptHelper;
import cs.b2b.core.common.session.B2BRuntimeSession;
import cs.b2b.core.common.util.Base64Util;
import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.common.xmlvalidation.ValidateXML;

public class B2BKukriWrapper {

	/**
	 * This method provide Kukri mapping script can be used in B2B2.0 alpaca
	 *     this method didn't cover Kukri common bizkey script part, need further check : select sequence, bizkey_type_id, query_position, query_xpath from b2b_edi_port_bizkey where tp_id=? and msg_type_id=? and dir_id=? order by query_position, sequence asc
	 * 
	 * @param inputBodyString - input body
	 * @param pmtScriptName - pmt mapping groovy script name
	 * @param pmtScript - pmt mapping groovy script body
	 * @param commonScripts - all kukri common script put to here, key is file name, value is groovy body
	 * @param msg - B2B2.0 Fwk library - B2B-Framework-SERVICE-1.1-20170418.061518-118.jar
	 *              msgRequestId: msg.msgIdentifier.msgRequestId
	 *              tpId: msg.msgIdentifier.tpId
	 *              msgType: msg.msgIdentifier.msgType
	 *              direction: msg.msgIdentifier.direction
	 *              msgFormat: msg.msgIdentifier.msgFormat
	 *              originalInput: crossProcessContext.originalInput
	 *              externalFilename: msg.inputFileName
	 *              inputFileName: msg.inputFileName
	 *              scac: msg.msgIdentifier.scac
	 *              correlationId: msg.msgIdentifier.correlationId
	 *              
	 * @param crossProcessContextMap - B2B2.0 Alpaca cross process context 
	 * @param conn - db connection by b2b_app
	 * @param schemaProperties - schema properties, name - value pair  
	 * @return
	 */
	public KukriResult kukriMapping(String inputBodyString, String pmtScriptName, String pmtScriptBody, HashMap<String, String> commonScripts, Msg msg, Map<String, Object> crossProcessContextMap, Connection conn, Properties schemaProperties) {
		KukriResult result = new KukriResult();

		String validateResult = validation(inputBodyString, pmtScriptName, pmtScriptBody, commonScripts, msg, conn);
		if (StringUtil.isNotEmpty(validateResult)) {
			result.fatalErrorDescription = validateResult;
			return result;
		}
		
		//1st, handle pmtGroovyScriptName
		//String packageName = "cs.b2b.mapping.scripts.";
		
		String tpId = msg.getMsgIdentifier().getTpId(); 
		String msgType = msg.getMsgIdentifier().getMsgType();
		String dirId = msg.getMsgIdentifier().getDirection();
		String msgFormat = msg.getMsgIdentifier().getMsgFormat();
		String msgReqId = msg.getMsgIdentifier().getMsgRequestId();
		String inputFileName = msg.getMsgIdentifier().getInputFilename();
		String correlationId = msg.getMsgIdentifier().getCorrelationId();
		
		String mappingScriptBody = pmtScriptBody;
		String mappingScriptName = pmtScriptName;
		if (StringUtil.isNotEmpty(mappingScriptName)) {
			if (mappingScriptName.indexOf(".")>0) {
				mappingScriptName = mappingScriptName.substring(mappingScriptName.indexOf(".")+1);
			}
		} else {
			//if script name is empty, then use default value
			mappingScriptName = tpId+"_"+msgType+"_"+dirId+"_Mapping";
		}
		
		if (schemaProperties!=null && schemaProperties.size()>0) {
			ValidateXML.setSchemaPathProperty(schemaProperties);
		}
		
//		//body is base64 encoding, decode it now
//		try {
//			mappingScriptBody = Base64Util.getFromBASE64(mappingScriptBody);
//		} catch (Exception ex) {
//			result.fatalErrorDescription = "Mapping Script Body base64 decode failed: "+ex.toString();
//			return result;
//		}
		
		//mapping scripts consolidation
		LinkedHashMap<String, String> invokingScripts = new LinkedHashMap<String, String>();
		String errDesc = consolidateMappingInvokingScripts(tpId, msgType, dirId, msgFormat, mappingScriptName, mappingScriptBody, commonScripts, invokingScripts);
		if (StringUtil.isNotEmpty(errDesc)) {
			result.fatalErrorDescription = errDesc;
			return result;
		}

		// Compiling
		groovy.lang.GroovyObject instance = null;
		cs.b2b.core.common.classloader.GroovyClassDefinition groovyClassDef = null;
		try {
			groovyClassDef = GroovyScriptHelper.getClassDef(invokingScripts);
			if (groovyClassDef==null) {
				result.fatalErrorDescription = "Load Groovy class definition is  failed, not available class to use. ";
				return result;
			}
			instance = (groovy.lang.GroovyObject) groovyClassDef.getInstance();
		} catch (Exception ex) {
			instance = null;
			String suppInfo = "";
			if (groovyClassDef!=null) {
				try {
					groovyClassDef.close();
				} catch (Exception exclose) {
					suppInfo = "script class def close has problem: "+exclose.toString();
				}
			}
			result.fatalErrorDescription = StringUtil.getErrRootCauseStr(ex, "Get script compiling meet exception");
			if (suppInfo!=null && suppInfo.length()>0) {
				result.fatalErrorDescription += "; \r\n" + suppInfo;
			}
			return result;
		}
		if (instance==null) {
			result.fatalErrorDescription = "Compiled instance is not available.";
			return result;
		}
		
		//start mapping
		String sessionId = String.valueOf(UUID.randomUUID());
		B2BRuntimeSession.addSessionCrossProcessContext(sessionId, crossProcessContextMap);
		
		//invoke mapping script
		try {
			//B2B xsl mapping use parameters, MSG_FORMAT & CORRELATION_ID are added for 2.0 script
			String[] runtimeParams = new String[]{"B2BSessionID="+sessionId, "B2B_OriginalSourceFileName="+inputFileName, "MSG_REQ_ID="+msgReqId, "TP_ID="+tpId, "MSG_TYPE_ID="+msgType, "DIR_ID="+dirId, "MSG_FORMAT="+msgFormat, "CORRELATION_ID"+correlationId};
			Object[] invokeParams = new Object[] {inputBodyString, runtimeParams, conn};
			result.outputMappingResult = (String) instance.invokeMethod("mapping", invokeParams);
			
			//should not one use 'APP_COMPLETE_WITH_ERROR_INFO' now, if still use, please review the groovy script, update to TransactionEnvelop mode, pmt don't need to discharge CE by itself, mapping only has Transaction concept, while CE is charge by application level (kukri / alpaca)
			String str = B2BRuntimeSession.getSessionValue(sessionId, "APP_COMPLETE_WITH_ERROR_INFO");
			if (StringUtil.isNotEmpty(str)) {
				result.fatalErrorDescription = "Found unexpected CE info from mapping script, need update to use transaction envelop mode, please review script; CE info in mapping: "+str+". ";
				return result;
			}
			
			// ---- after mapping ----
			//get the mapping Error / Obsolete / Warning / Information info, 'Warning' and 'Information' save into log only, Obsolete and Error will block the transaction
			str = B2BRuntimeSession.getSessionValue(sessionId, "InfoDescriptionInMapping");
			if (StringUtil.isNotEmpty(str)) {
				result.informationDescription = str;
			}
			str  = B2BRuntimeSession.getSessionValue(sessionId, "WarningDescriptionInMapping");
			if (StringUtil.isNotEmpty(str)) {
				result.warningDescription = str;
			}
			str = B2BRuntimeSession.getSessionValue(sessionId, "ObsoleteDescriptionInMapping");
			if (StringUtil.isNotEmpty(str)) {
				result.messageObsoleteDescription = str;
			}
			str = B2BRuntimeSession.getSessionValue(sessionId, "ErrorDescriptionInMapping");
			if (StringUtil.isNotEmpty(str)) {
				result.messageErrorDescription = str;
			}
			
			//get CSUpload Information
			str = B2BRuntimeSession.getSessionValue(sessionId, "PROMOTE_SESSION_CSUPLOAD");
			if (StringUtil.isNotEmpty(str)) {
				//output xml format: B2B1.0 CSUploadKeyTrack - http://www.tibco.com/schemas/message-processing/Schemas/System/CSUploadKeyTrack.xsd - CSUploadKeyTrack
				result.outputProcessAckXml = str;
			}
			
			//get ACK Upload (incoming ack upload)
			str = B2BRuntimeSession.getSessionValue(sessionId, "PROMOTE_SESSION_ACKUPLOAD");
			if (StringUtil.isNotEmpty(str)) {
				//ackupload output - output xml format: ProcessAckTrack - B2B1.0 http://www.tibco.com/schemas/APP_COMMON/Schemas/EDI/Schema.xsd
				result.outputAckUploadXml = str;
			}
			
			//Customs use only - /Processes/AppProcess/Util/WS/CreateReqUtil.process
			str = B2BRuntimeSession.getSessionValue(sessionId, "PROMOTE_SESSION_CreateReqUtil");
			if (StringUtil.isNotEmpty(str)) {
				//ackupload output - output xml format: ProcessAckTrack - B2B1.0 http://www.tibco.com/schemas/APP_COMMON/Schemas/EDI/Schema.xsd
				result.outputCreateReqUtilXml = str;
			}
			
			//Bizkey
			str = B2BRuntimeSession.getSessionValue(sessionId, "PROMOTE_SESSION_BIZKEY");
			if (StringUtil.isNotEmpty(str)) {
				//bizkey output - Transaction element in http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd
				result.outputTransactionXml = str;
			}
			
			//get Associate Types
			String assoScac = B2BRuntimeSession.getSessionValue(sessionId, "PROMOTE_CarrierSCAC");
			String assoInterchangeId = B2BRuntimeSession.getSessionValue(sessionId, "PROMOTE_InterchangeMessageID");
			if (StringUtil.isNotEmpty(assoScac)) {
				result.outputAssociateTypes.put("OSCAC", assoScac);
			}
			if (StringUtil.isNotEmpty(assoInterchangeId)) {
				result.outputAssociateTypes.put("OXMLMSGID", assoInterchangeId);
			}
			result.emails = B2BRuntimeSession.getEmails(sessionId);
		} catch (Exception eMapping) {
			result.fatalErrorDescription = StringUtil.getErrRootCauseStr(eMapping, "Mapping failed");
			return result;
		} finally {
			// ***** release GroovyMainClass, it's very important to release memory *****
			instance = null;
			if (groovyClassDef != null) {
				try { groovyClassDef.close(); } catch (Exception ex){}
			}
			groovyClassDef = null;
			
			// ***** clean up session values *****
			B2BRuntimeSession.cleanSessionById(sessionId);
			
			// *** check and close db connection ***
			if (conn==null) {
				String fatalErrorDesc = result.fatalErrorDescription;
				if (fatalErrorDesc==null) {
					fatalErrorDesc = "";
				}
				fatalErrorDesc = "Fatal issue, db connection is null, please check pmt script to avoid this behavior; " + fatalErrorDesc;
				result.fatalErrorDescription = fatalErrorDesc;
			} else {
				try {
					conn.close();
				} catch (Exception ex) {
					String warnInfo = result.warningDescription;
					if (warnInfo==null) {
						warnInfo = "";
					}
					warnInfo = "Warning, DB connection close in Kukri Wrapper meet exception: "+ex.toString()+"; " + warnInfo;
					result.warningDescription = warnInfo;
				}
			}
		}
		
		return result;
	}
	
	private String validation(String inputBodyString, String pmtScriptName, String pmtScriptBody, HashMap<String, String> commonScripts, Msg msg, Connection conn) {
		if (StringUtil.isEmpty(inputBodyString)) {
			return "Input content is empty, please check.";
		}
		if (StringUtil.isEmpty(pmtScriptBody)) {
			return "Mapping script is empty, KukiWrapper has not action.";
		}
		if (msg==null || msg.getMsgIdentifier()==null || StringUtil.isEmpty(msg.getMsgIdentifier().getTpId()) || StringUtil.isEmpty(msg.getMsgIdentifier().getDirection()) ||
				StringUtil.isEmpty(msg.getMsgIdentifier().getMsgType()) || StringUtil.isEmpty(msg.getMsgIdentifier().getMsgFormat()) || 
				StringUtil.isEmpty(msg.getMsgIdentifier().getMsgRequestId()) || StringUtil.isEmpty(msg.getMsgIdentifier().getInputFilename()) || StringUtil.isEmpty(msg.getMsgIdentifier().getCorrelationId())) {
			return "Msg is not available for tpId, dirId, msgType, msgFormat, msgRequestId, inputFileName, correlationId.";
		}
		if (conn==null) {
			return "DB Connection is not available, please check.";
		}
		boolean connStatus = false;
		try {
			if (conn.isClosed()) {
				connStatus = false;
			} else {
				connStatus = true;
			}
		} catch (Exception ex) {}
		if (! connStatus) {
			return "DB Connection is closed, please check.";
		}
		return "";
	}
	
	private String consolidateMappingInvokingScripts(String tpId, String msgType, String dirId, String msgFmtId, String mappingScriptName, String mappingScriptBody, HashMap<String, String> commonScripts, LinkedHashMap<String, String> invokingScripts) {
		//get common script usage from pmt's mapping script
		List<String> listEdiBean = new ArrayList<String>();
		List<String> listCS2XmlBeans = new ArrayList<String>();
		
		mappingScriptBody = getBase64Decode(mappingScriptBody);
		
		int lastImportPos = 0;
		for(int i=0; i<500; i++) {
			lastImportPos = mappingScriptBody.indexOf("import", lastImportPos);
			String str = "";
			if (lastImportPos<0) {
				break;
			} else {
				int retPos = mappingScriptBody.indexOf("\r\n", lastImportPos);
				if (retPos < 0) {
					retPos = mappingScriptBody.indexOf("\n", lastImportPos);
				}
				if (retPos>0 && retPos>lastImportPos) {
					str = mappingScriptBody.substring(lastImportPos, retPos);
					str = str.replace("import ", "");
					str = str.replace(";", "");
					if (str.startsWith("cs.b2b.core.mapping.bean.")) {
						if (str.startsWith("cs.b2b.core.mapping.bean.edi.")) {
							listEdiBean.add(str);
						} else {
							listCS2XmlBeans.add(str);
						}
					}
				} else {
					break;
				}
			}
			lastImportPos += "import".length();
		}
		
		//load common script - CS2XML javabeans
		if (listCS2XmlBeans.size()>0) {
			//if has cs2 bean, then load COMMON one first
			String commonCS2BeanName = "cs.b2b.core.mapping.bean.MessageBeanCS2XmlCommon";
			String commonCS2BeanBody = commonScripts.get(commonCS2BeanName);
			if (StringUtil.isEmpty(commonCS2BeanBody)) {
				return "Cannot find CS2 Common Bean script from commonScripts, please check.";
			} else {
				//add into invoking scripts
				if (commonCS2BeanName.indexOf(".")>=0) {
					commonCS2BeanName = commonCS2BeanName.substring(commonCS2BeanName.indexOf(".")+1);
				}
				invokingScripts.put(commonCS2BeanName, getBase64Decode(commonCS2BeanBody));
			}
		}
		
		List<String> allCommonScriptList = new ArrayList<String>();
		allCommonScriptList.addAll(listCS2XmlBeans);
		allCommonScriptList.addAll(listEdiBean);
		
		//loop every script name to load into invoking script
		for (String cmnScriptName : allCommonScriptList) {
			if (cmnScriptName.trim().endsWith(".*")) {
				cmnScriptName = cmnScriptName.replace(".*", "");
				for (String cmnscript : commonScripts.keySet()) {
					if (cmnscript.startsWith(cmnScriptName)) {
						String cmnbody = commonScripts.get(cmnscript);
						if (StringUtil.isNotEmpty(cmnbody)) {
							if (cmnscript.indexOf(".")>=0) {
								cmnscript = cmnscript.substring(cmnscript.indexOf(".")+1);
							}
							invokingScripts.put(cmnscript, getBase64Decode(cmnbody));
						}
					}
				}
			} else {
				String body = commonScripts.get(cmnScriptName);
				if (StringUtil.isNotEmpty(body)) {
					if (cmnScriptName.indexOf(".")>=0) {
						cmnScriptName = cmnScriptName.substring(cmnScriptName.indexOf(".")+1);
					}
					invokingScripts.put(cmnScriptName, getBase64Decode(body));
				}
			}
		}
		
		//put MappingUtil
		String mappingUtilName = "cs.b2b.core.mapping.util.MappingUtil";
		String mappingUtilBody = commonScripts.get(mappingUtilName);
		if (StringUtil.isNotEmpty(mappingUtilBody)) {
			invokingScripts.put("MappingUtil", getBase64Decode(mappingUtilBody));
		}
		
		//put MsgType_Direction_Common Util if exists
		String msgTypeCmnName = "MappingUtil_" + msgType + "_" + dirId + "_Common";
		String mappingMsgTypeDirCommonScriptName = "cs.b2b.core.mapping.util." + msgTypeCmnName;
		String mappingMsgTypeDirCommonScriptBody = commonScripts.get(mappingMsgTypeDirCommonScriptName);
		if (StringUtil.isNotEmpty(mappingMsgTypeDirCommonScriptBody)) {
			invokingScripts.put(msgTypeCmnName, getBase64Decode(mappingMsgTypeDirCommonScriptBody));
		}
		
		//put the pmt script to the last part
		invokingScripts.put(mappingScriptName, mappingScriptBody);
		
		//if has problem, then result is not empty
		return "";
	}
	
	private String getBase64Decode(String str) {
		String ret = "";
		try {
			ret = Base64Util.getFromBASE64(str);
		} catch (Exception ex) {
			ret = "";
		}
		return ret;
	}
}
