package cs.b2b.mapping.e2e.util;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class DemoBase {

	public static void println(String str) {
		System.out.println(str);
	}
	
	public static void println(Exception e) {
		println((Throwable)e);
	}
	
	public static void println(Throwable e) {
		StringBuffer sberror = new StringBuffer();
		StringWriter sw = new StringWriter();
		Throwable loge = e;
		for (int li=0; li<10; li++) {
			if (loge!=null && loge.getCause()!=null) {
				loge = loge.getCause();
			}
			if (loge.getCause()==null)
				break;
		}
		try {
			loge.printStackTrace(new PrintWriter(sw));
			sberror.append(sw.toString()).append(";; ");
			sw.close();
		} catch (Exception exx) {
			sberror.append("\r\n // Error //: ").append(e.toString());
		}
		println("-------------\r\n"+sberror+"\r\n------------------");
	}
	
	public String formatXml(String str) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(str));
        Document doc = db.parse(is);
		
		OutputFormat format = new OutputFormat(doc);
        //format.setLineWidth(65);
        format.setIndenting(true);
       	format.setLineSeparator("\r\n");
        //format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(doc);

        return out.toString();
	}
}
