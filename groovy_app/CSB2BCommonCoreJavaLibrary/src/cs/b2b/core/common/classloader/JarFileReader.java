package cs.b2b.core.common.classloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarFileReader {

	private JarInputStream jarInput;
	private Hashtable<String, byte[]> hashEntriesStream;
	
	public JarFileReader(InputStream in) throws IOException {
		jarInput = new JarInputStream(in);
		hashEntriesStream = new Hashtable<String, byte[]>();
	}
	
	public void readEntries() throws IOException {
		JarEntry entry = jarInput.getNextJarEntry();
		while(entry != null) {
			if(entry.getName().endsWith(".class")) {
				String ename = entry.getName();
				ename = ename.replaceAll("\\/", ".");
				ename = ename.substring(0, ename.length()-6);
				copyInputStream(jarInput, ename);		
			}
			entry = jarInput.getNextJarEntry();
		}
	}
	
	public void copyInputStream(InputStream in, String entryName) throws IOException {
		ByteArrayOutputStream _copy = new ByteArrayOutputStream();
		int chunk = 0;
		byte[] data = new byte[52*1024];
		while(-1 != (chunk = in.read(data)))
		{
			_copy.write(data, 0, chunk);
		}
		hashEntriesStream.put(entryName, _copy.toByteArray());
	}
	
	public byte[] getCopy(String className) {
		return hashEntriesStream.get(className);
	}
	
	public boolean containsCopy(String className) {
		return hashEntriesStream.containsKey(className);
	}
	
	public static void main(String[] args) {
		File jarFile = new File("xxx.jar");
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(jarFile));
			JarFileReader reader = new JarFileReader(in);
			reader.readEntries();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
