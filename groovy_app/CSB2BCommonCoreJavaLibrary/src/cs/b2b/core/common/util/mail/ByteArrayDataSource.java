package cs.b2b.core.common.util.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * to send email, attachment source
 * @author LIANGDA
 *
 */
public class ByteArrayDataSource implements DataSource {

	private byte[] data;// data
	private String type;// content-type

	/* Create a DataSource from an input stream */
	public ByteArrayDataSource(InputStream is, String type) {
		this.type = type;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int ch;
			byte[] buffer = new byte[512];
			while ((ch = is.read(buffer)) > 0) {
				os.write(buffer, 0, ch);
			}
			data = os.toByteArray();
		} catch (IOException ioex) {

		}
	}

	/* Create a DataSource from a byte array */
	public ByteArrayDataSource(byte[] data, String type) {
		this.data = data;
		this.type = type;
	}

	/* Create a DataSource from a String */
	public ByteArrayDataSource(String data, String type) {
		// Assumption that the string contains only ASCII
		// characters! Otherwise just pass a charset into this
		// constructor and use it in getBytes()
		// "iso-8859-1"
		this.data = data.getBytes();
		this.type = type;
	}

	/**
	 * Return an InputStream for the data. Note - a new stream must be returned
	 * each time.
	 */
	public InputStream getInputStream() throws IOException {
		if (data == null)
			throw new IOException("no data");
		return new ByteArrayInputStream(data);

	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("cannot do this");
	}

	public String getContentType() {
		return type;
	}

	public String getName() {
		return "ByteArrayDataSource";
	}

}
