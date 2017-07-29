package cs.b2b.mapping.e2e.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

public class BelugaOceanHelper {

	private String operation = null;
	
	private String definitionName = null;
	private String definitionBodyInBase64 = null;
	private String md5 = null;
	
	private String definitionConfigSettings = null;
	private String ctrlnumSenderId = null;
	private String ctrlnumReceiverId = null;
	private String ctrlnumMsgType = null;
	private String ctrlnumFormat = null;
	
	private String tpId = null;
	private String msgTypeId = null;
	private String dirId = null;
	private String msgFmtId = null;
			
	public String getBelugaOceanConfigSettingStr() {
		return this.definitionConfigSettings;
	}
	
	public String getDefinitionName() {
		return definitionName;
	}
	
	public String getMd5() {
		return md5;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public String getDefinitionBody() throws Exception {
		if (definitionBodyInBase64==null) {
			throw new Exception("Cannot find definition body, please check.");
		} else {
			String body = Base64Util.getFromBASE64(definitionBodyInBase64);
			if (CommonUtil.isEmpty(body)) {
				throw new Exception("Invalid definition body in b2b_edi_beluga_body_script, md5: "+md5+". ");
			}
			return body;
		}
	}
	
	public void getBelugaOceanDefinitionSettingStr(String[] runtimeParams, Connection conn) throws Exception {
		cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
		
		tpId = util.getRuntimeParameter("TP_ID", runtimeParams);
		msgTypeId = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
		dirId = util.getRuntimeParameter("DIR_ID", runtimeParams);
		msgFmtId = util.getRuntimeParameter("MSG_FMT_ID", runtimeParams);
		//String MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);
		
		if (CommonUtil.isEmpty(tpId)) {
			throw new Exception("Empty TP_ID in parameters, please chceck.");
		}
		if (CommonUtil.isEmpty(msgTypeId)) {
			throw new Exception("Empty MSG_TYPE_ID in parameters, please chceck.");
		}
		if (CommonUtil.isEmpty(dirId)) {
			throw new Exception("Empty DIR_ID in parameters, please chceck.");
		}
		if (CommonUtil.isEmpty(msgFmtId)) {
			throw new Exception("Empty MSG_FMT_ID in parameters, please chceck.");
		}
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			pst = conn.prepareStatement("select c.md5, script_name, script, operation, ctrlnum_sender, ctrlnum_receiver, ctrlnum_msgtype, ctrlnum_format, transform_settings from b2b_edi_beluga_cfg c, b2b_edi_beluga_script_body b where c.md5=b.md5 and tp_id=? and dir_id=? and msg_type_id=? and msg_fmt_id=? ");
			pst.setString(1, tpId);
			pst.setString(2, dirId);
			pst.setString(3, msgTypeId);
			pst.setString(4, msgFmtId);
			rst = pst.executeQuery();
			if (rst.next()) {
				md5 = rst.getString("md5");
				definitionName = rst.getString("SCRIPT_NAME");
				definitionBodyInBase64 = rst.getString("SCRIPT");
				operation = rst.getString("OPERATION");
				
				ctrlnumSenderId = rst.getString("CTRLNUM_SENDER");
				ctrlnumReceiverId = rst.getString("CTRLNUM_RECEIVER");
				ctrlnumMsgType = rst.getString("CTRLNUM_MSGTYPE");
				ctrlnumFormat = rst.getString("CTRLNUM_FORMAT");
				definitionConfigSettings = rst.getString("TRANSFORM_SETTINGS");
			} else {
				throw new Exception("No found b2b_edi_beluga_cfg setting by TP_ID: "+tpId+", MSG_TYPE_ID: "+msgTypeId+", DIR_ID: "+dirId+", MSG_FMT_ID: "+msgFmtId+", please check.");
			}
		} finally {
			if (rst!=null) {
				try { rst.close(); } catch (Exception e) {}
				try { pst.close(); } catch (Exception e) {}
			}
		}
	}
	
	public String setEDICtrlNumber(String ediBody, Connection conn) throws Exception {
		String outEdiBody = ediBody;
		if (isNotEmpty(ctrlnumSenderId) && isNotEmpty(ctrlnumReceiverId) && isNotEmpty(ctrlnumMsgType) && isNotEmpty(ctrlnumFormat) && isNotEmpty(definitionConfigSettings)) {
			long[] ctrls = getEDIControlNumber(ctrlnumSenderId, ctrlnumReceiverId, ctrlnumMsgType, ctrlnumFormat, conn);
			
			String ediCtrlNumber = ctrls[0]+"";
			String ediGroupNumber = ctrls[1]+"";
			
			//%EDI_CTRL_NUM%
			outEdiBody = outEdiBody.replace("%EDI_CTRL_NUM%", String.format("%09d", Long.parseLong(ediCtrlNumber)));

			//%GROUP_CTRL_NUM%
			outEdiBody = outEdiBody.replace("%GROUP_CTRL_NUM%", ediGroupNumber);

			//%GROUP_CTRL_NUM_IN_TXN% - if over 5 digital, only get the right 5
			long txnGroupNum = Long.parseLong(ediGroupNumber);
			if (txnGroupNum > 99999) {
				txnGroupNum = txnGroupNum % 100000;
			}
			outEdiBody = outEdiBody.replace("%GROUP_CTRL_NUM_IN_TXN%", txnGroupNum+"");
		} else {
			throw new Exception("Cannot find out BelugaOcean PMT setting in table B2B_EDI_TP_LBC_SETTING for ["+tpId+"/"+msgTypeId+"/"+dirId+"], please check. ");
		}
		return outEdiBody;
	}
	
	private boolean isNotEmpty(String str) {
		return str!=null && str.trim().length()>0;
	}
	
	private long[] getEDIControlNumber(String vSenderID, String vPartnerId, String vMessageType, String vEDIOutMsgFormat, Connection conn) throws Exception {
		CallableStatement cStmt = null;
		long vEdiCtlNo = -1;
		long vEdiGroupCtlNo = -1;
		try {
			cStmt = conn.prepareCall("{call B2B_PLSQL.B2BEDI_CTLNO_PKG.GET_EDI_CTLNO (?, ?, ?, ?, ?, ?)}");
			cStmt.setString(1, vSenderID);
			cStmt.setString(2, vPartnerId);
			cStmt.setString(3, vMessageType);
			cStmt.setString(4, vEDIOutMsgFormat);
			
			cStmt.registerOutParameter(5, Types.BIGINT);
			cStmt.registerOutParameter(6, Types.BIGINT);
			
			cStmt.execute();
			vEdiCtlNo = cStmt.getLong(5);
			vEdiGroupCtlNo = cStmt.getLong(6);
		} finally {
			try { cStmt.close(); } catch (Exception ex) {}
		}
		return new long[]{vEdiCtlNo, vEdiGroupCtlNo};
	}
}
