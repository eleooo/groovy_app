package cs.b2b.core.mapping.api.autori;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.zip.ZipInputStream;

import cs.b2b.beluga.common.datahandle.XMLUtil;
import cs.b2b.mapping.e2e.util.ConnectionForTester;

public class DBOperation {

	String sqlQueryOutput = "select p.msg_req_id, p.proc_seq, p.proc_type_id, p.proc_sts_id, p.in_msg from b2b_msg_req_detail d, b2b_msg_req_process p where d.msg_req_id=p.msg_req_id and d.tp_id=? and d.msg_type_id=? and d.dir_id=? order by p.msg_req_id desc, proc_seq asc";
	
	public String getOutputFile(String tpId, String msgType, String dirId, int waitLoopCount, Connection conn) throws Exception {
		String ret = "";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sqlQueryOutput);
			pst.setQueryTimeout(30);
			pst.setMaxRows(100);
			
			pst.setString(1, tpId);
			pst.setString(2, msgType);
			pst.setString(3, dirId);
			
			//MCI / E2P / MD2SD
			String outputProcTypeId = "";
			
			for (int i=0; i<waitLoopCount && outputProcTypeId.trim().length()==0; i++) {
				if (i>0) {
					println("Sleep 6s to wait txn completed...");
					Thread.sleep(6000);
				}
				rs = pst.executeQuery();
				while (rs.next()) {
					String ptype = rs.getString("proc_type_id");
					String psts = rs.getString("proc_sts_id");
					Blob pinmsg = rs.getBlob("in_msg");
					
					if (ptype.equals("MCI")) {
						outputProcTypeId = "MCI";
					} else if (ptype.equals("E2P")) {
						outputProcTypeId = "E2P";
					} else if (ptype.equals("MD2SD")) {
						outputProcTypeId = "MD2SD";
					}
					if (outputProcTypeId.length()>0) {
						if (psts!=null && (psts.equals("C") || psts.equals("P"))) {

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ZipInputStream zis = new ZipInputStream(pinmsg.getBinaryStream());
							zis.getNextEntry();
							byte[] b = new byte[1024];
							for (int c = zis.read(b, 0, 1024); c != -1; c = zis.read(b, 0, 1024)) {
								baos.write(b, 0, c);
							}
							String unzippedContent = new String(baos.toByteArray(), "UTF-8");
							
							if (unzippedContent!=null && unzippedContent.length()>0) {
								ret = unzippedContent.substring(unzippedContent.indexOf("<ns0:Body>")+"<ns0:Body>".length());
								ret = ret.substring(0, ret.indexOf("</ns0:Body>"));
								
								ret = XMLUtil.decodeAllXmlChar(ret);
								
								System.out.println("data: \r\n"+ret);
								//ret = unzippedContent;
								break;
							}
						} else {
							println("No completed mapping result, wait...");
						}
					}
				}
			}
		} finally {
			close(pst, rs, null);
		}
		return ret;
	}
	
	private static void println(String s) {
		System.out.println(s);
	}
	
	private void close(PreparedStatement pst, ResultSet rs, Connection conn) throws Exception {
		if (rs!=null)
			rs.close();
		
		if (pst!=null)
			pst.close();
		
		if (conn!=null)
			conn.close();
	}
	
	public String unzip(byte[] content) throws Exception {
		String encoding = "UTF-8";
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ByteArrayInputStream bais = new ByteArrayInputStream(content);
		final ZipInputStream zis = new ZipInputStream(bais);
		zis.getNextEntry();
                final byte[] b = new byte[1024];
                for (int c = zis.read(b, 0, 1024); c != -1; c = zis.read(b, 0, 1024)) {
                    baos.write(b, 0, c);                    
                }
		String unzippedContent = new String(baos.toByteArray(), encoding);
		return unzippedContent;
	}
}
