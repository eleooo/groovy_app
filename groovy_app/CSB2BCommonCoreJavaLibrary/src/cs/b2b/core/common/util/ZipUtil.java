package cs.b2b.core.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

public class ZipUtil {

	public static String getZipInBase64StringFrom2Instream(String fileName1, String bodyInBase64_1, String fileName2, String bodyInBase64_2) throws Exception {
		InputStream is1 = Base64Util.getInputStreamFromBASE64(bodyInBase64_1);
		InputStream is2 = Base64Util.getInputStreamFromBASE64(bodyInBase64_2);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		
		ZipEntry entry1 = new ZipEntry(fileName1);
		zos.putNextEntry(entry1);
		
		byte[] buf = new byte[50 * 1025];
		int readCount = 0;
		do {
			readCount = is1.read(buf);
			if (readCount>0) {
				zos.write(buf, 0, readCount);
			}
		} while (readCount==buf.length);
		
		ZipEntry entry2 = new ZipEntry(fileName2);
		zos.putNextEntry(entry2);
		do {
			readCount = is2.read(buf);
			if (readCount>0) {
				zos.write(buf, 0, readCount);
			}
		} while (readCount==buf.length);
				
		zos.finish();
		
		//ByteArrayOutputStream to base64 String
		return Base64Util.getBASE64(baos.toByteArray());
	}
	
	/**
	 * Get all file details in zip base64 string
	 * 
	 * @param inputZipFileBodyBase64String
	 *            : zip file encode to base64 string
	 * @return : <fileName in zip, file body string>
	 */
	public static Hashtable<String, StringBuffer> getZipFileOut(String inputZipFileBodyBase64String) throws Exception {
		ZipArchiveInputStream zis = null;
		Hashtable<String, StringBuffer> retHash = null;
		try {
			zis = new ZipArchiveInputStream(new ByteArrayInputStream(Base64Util.getBytesFromBASE64(inputZipFileBodyBase64String)));
			retHash = new Hashtable<String, StringBuffer>();
			ZipArchiveEntry zae = null;
			byte[] buf = new byte[50*1024];
			while ((zae = zis.getNextZipEntry()) != null) {
				//ByteArrayOutputStream os = new ByteArrayOutputStream();
				StringBuffer fileSb = new StringBuffer();
				int readLen = zis.read(buf, 0, buf.length);
				while (readLen>0) {
					fileSb.append(new String(buf, 0, readLen));
					readLen = zis.read(buf, 0, buf.length);
				}
				retHash.put(zae.getName(), fileSb);
			}
		} finally {
			if (zis!=null) {
				try { zis.close(); } catch (Exception exx) {}
			}
		}
		return retHash;
	}
	
}
