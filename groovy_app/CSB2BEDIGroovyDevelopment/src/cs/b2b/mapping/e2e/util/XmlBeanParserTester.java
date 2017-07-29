package cs.b2b.mapping.e2e.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cs.b2b.core.mapping.bean.edi.custom.iocm.EDI_ICOM;
import cs.b2b.core.mapping.util.XmlBeanParser;

public class XmlBeanParserTester {

	public static void main(String[] args) {
		try {
			String xml = LocalFileUtil.readBigFile("./demo/IOCM/EDI2017013112255236-95_2txn.in_e2x_out.xml");
			
			XmlBeanParser util = new XmlBeanParser();
			EDI_ICOM iocm = util.xmlParser(xml, EDI_ICOM.class);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String str = gson.toJson(iocm);
			
			System.out.println(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
