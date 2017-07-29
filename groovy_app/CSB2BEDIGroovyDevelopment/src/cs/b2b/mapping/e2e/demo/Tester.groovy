package cs.b2b.mapping.e2e.demo

import org.codehaus.groovy.util.StringUtil

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.DateFormat;
import java.text.SimpleDateFormat

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMIN.EDI_IFTMIN;
import cs.b2b.core.mapping.bean.so.ShippingOrder
import cs.b2b.core.mapping.util.XmlBeanParser;

class Tester {

	static void main(String[] args){
		
//				String xslName = "ct.xml"
//		String xslName = "DSGOODS_CS190_CS140_CS160.xml"
//		String xslName = "so_test.xml"
//		String xslName = "YOURID_IFTMSOD99B(General).txt_e2x_out2.xml"
//		String xslName = "1-1-baseLine.xml"
//		String xslName = "2-5-IG-Sample-VGM-Submission-for-Shipper-Owned-Containers.xml"
//		String xslName = "xml(1).txt"
//
//		File file = new File("""demo/${xslName}""");
//		String xml = file.text
//
//		Gson gson = new GsonBuilder().create();
//		XmlBeanParser xmlBeanParser = new XmlBeanParser();
//		ContainerMovement ct = xmlBeanParser.xmlParser(xml, ContainerMovement.class)
//		println gson.toJson(ct)
//		
//		ShippingOrder so = xmlBeanParser.xmlParser(xml,ShippingOrder.class)
//		println gson.toJson(so)
		
//		EDI_IFTMBF edi = xmlBeanParser.xmlParser(xml, EDI_IFTMBF.class)
//		println gson.toJson(edi)
		
//		SubmitVGM vgm = xmlBeanParser.xmlParser(xml, SubmitVGM.class)
//		println gson.toJson(vgm)
			
		
//		EDI_IFTMIN edi = xmlBeanParser.xmlParser(xml,EDI_IFTMIN.class)
//		println gson.toJson(edi)
		
		
//		Tester tester = new Tester();
//		
//		tester.getListFieldName(IFTMBF.class)
		
//		Date date = new Date();
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
//		println dateFormat.format(date)
//		println date.format("yyyy-MM-dd'T'HH:mm:ss",TimeZone.getTimeZone("GMT"))


		String str = "1234567890123456789012345678901234567890";
		println str[0..<60]
	}
	
	public void getListFieldName(Class t){
		Field[] fields = t.getDeclaredFields()
		for(Field field : fields){
			if(field.getType().getName().equals("java.util.List")){
				println field.getName()
				Type fc = field.getGenericType();
				if(fc instanceof ParameterizedType){
				   ParameterizedType pt = (ParameterizedType) fc;
				   Class genericClazz = (Class)pt.getActualTypeArguments()[0];
				   this.getListFieldName(genericClazz)
				}
			}else if(!field.getType().getName().equals("java.lang.String") && field.isAccessible()){
				this.getListFieldName(field.getType())
			}
		}
	}
}
