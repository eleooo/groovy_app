package cs.b2b.core.api.kukri;

import java.util.HashMap;
import java.util.List;

import org.simplejavamail.email.Email;

import cs.b2b.core.common.util.StringUtil;

/**
 * this class is result for : kukriMapping(String inputBodyString, String pmtScriptName, String pmtScriptBody, HashMap<String, String> commonScripts, Msg msg, Connection conn)
 * 
 * @author LIANGDA
 *
 */

public class KukriResult {
	
	//Exception happen in mapping invoking
	public String fatalErrorDescription = null;
	
	//message need to Error
	public String messageErrorDescription = null;
	
	//message need to Obsolete
	public String messageObsoleteDescription = null;
	
	//write log with Info severity
	public String informationDescription = null;
	
	//write log with Warning severity
	public String warningDescription = null;
	
	
	// *** all mapping output ***
	
	//mapping output
	public String outputMappingResult = null;
	
	//bizkey output - need cater CE info here - Transaction element in http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd , reference : /Processes/AppProcess/GenericProcess/InsertBusinessKey.process
	public String outputTransactionXml = null;
	
	//csupload output - output xml format: B2B1.0 CSUploadKeyTrack - http://www.tibco.com/schemas/message-processing/Schemas/System/CSUploadKeyTrack.xsd - CSUploadKeyTrack , reference: /Processes/Util/PIT/SetAppProcessInfo.process
	public String outputProcessAckXml = null;
	
	//ackupload output - output xml format: ProcessAckTrack - B2B1.0 http://www.tibco.com/schemas/APP_COMMON/Schemas/EDI/Schema.xsd , reference : /Processes/AppProcess/GenericProcess/AckUpload.process
	public String outputAckUploadXml = null;
	
	//use for Customs only - reference : /Processes/AppProcess/Util/WS/CreateReqUtil.process
	public String outputCreateReqUtilXml = null;
	
	//associate key values, use dir_id and msg_req_id and pair data to insert into associate table, reference: /Processes/AppProcess/GenericProcess/insertAssoType.process
	public HashMap<String, String> outputAssociateTypes = new HashMap<String, String>();

	//org.simplejavamail.email.Email, simple-java-mail-4.2.3-java6-release.jar, Alpaca will help to send this email out, http://www.simplejavamail.org/#/about
	public List<Email> emails;
	
	public String getAllResultInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("--------------------------------------\r\n");
		if (StringUtil.isNotEmpty(fatalErrorDescription)) {
			sb.append("fatalErrorDescription: ").append(fatalErrorDescription).append("; \r\n");
		}
		if (StringUtil.isNotEmpty(messageErrorDescription)) {
			sb.append("messageErrorDescription: ").append(messageErrorDescription).append("; \r\n");
		}
		if (StringUtil.isNotEmpty(messageObsoleteDescription)) {
			sb.append("messageObsoleteDescription: ").append(messageObsoleteDescription).append("; \r\n");
		}
		if (StringUtil.isNotEmpty(informationDescription)) {
			sb.append("informationDescription: ").append(informationDescription).append("; \r\n");
		}
		if (StringUtil.isNotEmpty(warningDescription)) {
			sb.append("warningDescription: ").append(warningDescription).append("; \r\n");
		}
		
		if (StringUtil.isNotEmpty(outputTransactionXml)) {
			sb.append("outputTransactionXml: \r\n    ").append(outputTransactionXml).append("\r\n");
		}
		if (StringUtil.isNotEmpty(outputProcessAckXml)) {
			sb.append("outputProcessAckXml: \r\n    ").append(outputProcessAckXml).append("\r\n");
		}
		if (StringUtil.isNotEmpty(outputAckUploadXml)) {
			sb.append("outputAckUploadXml: \r\n    ").append(outputAckUploadXml).append("\r\n");
		}
		if (StringUtil.isNotEmpty(outputCreateReqUtilXml)) {
			sb.append("outputCreateReqUtilXml: \r\n    ").append(outputCreateReqUtilXml).append("\r\n");
		}
		
		if (outputAssociateTypes!=null && outputAssociateTypes.size()>0) {
			sb.append("outputAssociateTypes: \r\n");
			int count = 1;
			for(String key : outputAssociateTypes.keySet()) {
				String val = outputAssociateTypes.get(key);
				sb.append("    |-").append(count++).append(", ").append(key).append(" = ").append(val).append("\r\n");
			}
		}
		
		if (emails!=null && emails.size()>0) {
			sb.append("emails: \r\n");
			for(Email email : emails) {
				if (email!=null) {
					sb.append("    |-").append(email.toString());
				}
			}
		}
		
		if (StringUtil.isNotEmpty(outputMappingResult)) {
			sb.append("outputMappingResult: \r\n").append(outputMappingResult).append("\r\n");
		}
		sb.append("--------------------------");
		
		return sb.toString();
	}
}

