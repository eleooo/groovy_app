package cs.b2b.core.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import sun.misc.BASE64Decoder;

public class Base64Util {

	public static String getBASE64(byte[] bs) {
		if (bs == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(bs);
	}
	
	public static String getBASE64(String s) {
		if (s == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}

	public static String getFromBASE64(String s) throws Exception {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static String getFromBASE64WithEncoding(String s, String encoding) throws Exception {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			if (encoding!=null && encoding.trim().length()>0)
				return new String(b, encoding);
			else
				return new String(b);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static InputStream getInputStreamFromBASE64(String s) throws Exception {
		if (s == null)
			return null;
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			return new ByteArrayInputStream(decoder.decodeBuffer(s));
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static byte[] getBytesFromBASE64(String s) throws Exception {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return b;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static String getZipBodyFromBase64(String s, String encoding) throws Exception {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] content = decoder.decodeBuffer(s);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		ZipInputStream zis = new ZipInputStream(bais);
		zis.getNextEntry();
        byte[] b = new byte[1024];
        for (int c = zis.read(b, 0, 1024); c != -1; c = zis.read(b, 0, 1024)) {
        	baos.write(b, 0, c);                    
        }
		String unzippedContent = new String(baos.toByteArray(), encoding);
		return unzippedContent;
	}
	
	public static void main(String[] args) {
		String str = "\"-abcdefghiJKLMNopq-\"";
		try {
			String encodeStr = Base64Util.getBASE64(str);
			System.out.println("encode: "+encodeStr);
		
			String decodeStr = Base64Util.getFromBASE64(encodeStr);
			System.out.println("decode: "+decodeStr);
			
			String encoding = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("file.encoding"));
			
			System.out.println("encoding: "+encoding);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
