package cs.b2b.core.common.util;

import java.io.File;
import java.io.RandomAccessFile;

public class UTF8Util {

	public static byte[] gbk2utf8(String gbkstr) {
	    char c[] = gbkstr.toCharArray();
	    byte[] fullByte = new byte[3 * c.length];
	    for (int i = 0; i < c.length; i++) {
	        int m = (int) c[i];
	        String word = Integer.toBinaryString(m);
	 
	        StringBuffer sb = new StringBuffer();
	        int len = 16 - word.length();
	        for (int j = 0; j < len; j++) {
	            sb.append("0");
	        }
	        sb.append(word);
	        sb.insert(0, "1110");
	        sb.insert(8, "10");
	        sb.insert(16, "10");
	 
	        String s1 = sb.substring(0, 8);
	        String s2 = sb.substring(8, 16);
	        String s3 = sb.substring(16);
	 
	        byte b0 = Integer.valueOf(s1, 2).byteValue();
	        byte b1 = Integer.valueOf(s2, 2).byteValue();
	        byte b2 = Integer.valueOf(s3, 2).byteValue();
	        byte[] bf = new byte[3];
	        bf[0] = b0;
	        fullByte[i * 3] = bf[0];
	        bf[1] = b1;
	        fullByte[i * 3 + 1] = bf[1];
	        bf[2] = b2;
	        fullByte[i * 3 + 2] = bf[2];
	 
	    }
	    return fullByte;
	}
	
	public static String convertUtf16ToUtf8(String str) {
		String ret = str;
		
		return ret;
	}
	
	public static String removeBOM(String str) {
		byte[] bs = str.getBytes();
		String ret = null;
		if (-17 == bs[0] && -69 == bs[1] && -65 == bs[2]) {
			ret = new String(bs, 3, bs.length-3);
		} else if (-17 == bs[0] && -65 == bs[1] && -67 == bs[2]) {
			ret = new String(bs, 3, bs.length-3);
		} else if (-17 == bs[0] && -69 == bs[1] && 63 == bs[2]) {
			ret = new String(bs, 3, bs.length-3);
		} else if (63 == bs[0] && 63 == bs[1] && 63 == bs[2]) {
			ret = new String(bs, 3, bs.length-3);
		} else {
			ret = str;
		}
		return ret;
	}
	
	public static void replaceUtf8BOM(File file) {
		RandomAccessFile accessFile = null;
		byte[] utf8Bom = new byte[3];
		int readSize = 0;

		if (null == file) {
			return;
		}

		try {
			accessFile = new RandomAccessFile(file, "rw");
			readSize = accessFile.read(utf8Bom);

			if (0 >= readSize) {
				return;
			}

			if (-17 == utf8Bom[0] && -69 == utf8Bom[1] && -65 == utf8Bom[2]) {

				accessFile.seek(0);
				accessFile.write(new byte[] { ' ', ' ', ' ' });
			} else if (-17 == utf8Bom[0] && -69 == utf8Bom[1] && 63 == utf8Bom[2]) {

				accessFile.seek(0);
				accessFile.write(new byte[] { ' ', ' ', ' ' });
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (null != accessFile)
					accessFile.close();
			} catch (Exception ex) {

			}
		}
	}
	
	public static void main(String[] args) {
		try {
			String fileFullName = "";
			String body = LocalFileUtil.readBigFileContentDirectly(fileFullName);
			
			//body = "ISA*00*";
			
			System.out.println("->\r\n"+body);
			
			String ret = UTF8Util.removeBOM(body);
			System.out.println("->\r\n"+ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
