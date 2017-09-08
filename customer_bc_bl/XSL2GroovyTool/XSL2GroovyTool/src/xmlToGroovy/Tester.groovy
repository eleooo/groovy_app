package xmlToGroovy

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import cs.Message

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
;;

class Tester {

	static void main(String[] args){
		
//				String xslName = "ct.xml"
//		String xslName = "bl.xml"
//		String xslName = "bc.xml"
//		String xslName = "DSGOODS_CS190_CS140_CS160.xml"
//		String xslName = "so_test.xml"
//		String xslName = "YOURID_IFTMSOD99B(General).txt_e2x_out2.xml"
//		String xslName = "1-1-baseLine.xml"
//		String xslName = "2-5-IG-Sample-VGM-Submission-for-Shipper-Owned-Containers.xml"
//		String xslName = "si_edi.xml"
//		String xslName = "big.xml"
//		String xslName = "OOCL_NS_9941248_20151008181626_New.xml"
//		String xslName = "EDI2017013112255236-95_2txn.in_e2x_out.xml"
//		String xslName = "1.baseline.xml"
//		String xslName = "EDI2017031700521478-15_1.in"
//		String xslName = "issue_xmlbeanparser-144-CNPORTSSHA-2-1-FTX-bookingstatus-remarklines.xml"
//		String xslName = "SSMXML_sample.xml"
//		String xslName = "siCS2xml.xml"
//		String xslName = "BR.xml"
//		String xslName = "ack_fa.xml"
//		String xslName = "edifact_functionalack_output.xml"
//		String xslName = "EDI2017071311190928-95_2.xml"
//		String xslName = "in.xml"
//		String xslName = "116-CARGOWISE-7-2.Declined_systemDatetime.xml"
		String xslName = "EXX.xml"

		File file = new File("""input/${xslName}""");
		String xml = file.text

		Gson gson = new GsonBuilder().create();
		XmlBeanParser xmlBeanParser = new XmlBeanParser();

		Message message = xmlBeanParser.xmlParser(xml, Message.class)
		println gson.toJson(message);

//		Booking br = xmlBeanParser.xmlParser(xml, Booking.class)
//		println gson.toJson(br);
		
//		BillOfLading bl = xmlBeanParser.xmlParser(xml, BillOfLading.class)
//		println gson.toJson(bl);
		
//		BookingConfirm bc = xmlBeanParser.xmlParser(xml, BookingConfirm.class)
//		println gson.toJson(bc);
		
//		ShippingInstruction si = xmlBeanParser.xmlParser(xml, ShippingInstruction.class)
//		println gson.toJson(si)
		
//		BookingRequest br = xmlBeanParser.xmlParser(xml, BookingRequest.class)
//		println gson.toJson(br)
		
//		FunctionalAcknowlegment fa = xmlBeanParser.xmlParser(xml, FunctionalAcknowlegment.class)
//		println gson.toJson(fa)
		
//		cs.b2b.core.mapping.bean.ss_pt2pt.Message message = xmlBeanParser.xmlParser(xml, cs.b2b.core.mapping.bean.ss_pt2pt.Message.class)
//		println gson.toJson(message) 
		
//		ContainerMovement ct = xmlBeanParser.xmlParser(xml, ContainerMovement.class)
//		println gson.toJson(ct)
		
//		ShippingOrder so = xmlBeanParser.xmlParser(xml,ShippingOrder.class)
//		println gson.toJson(so)
		
//		EDI_IFTMBF edi = xmlBeanParser.xmlParser(xml, EDI_IFTMBF.class)
//		println gson.toJson(edi)
		
//		SubmitVGM vgm = xmlBeanParser.xmlParser(xml, SubmitVGM.class)
//		println gson.toJson(vgm)
		
//		Message message = xmlBeanParser.xmlParser(xml, Message.class)
//		println gson.toJson(message);
			
//		EDI_IOCM ediIocm = xmlBeanParser.xmlParser(xml, EDI_IOCM.class)
//		println gson.toJson(ediIocm);
		
//		Message siInttra = xmlBeanParser.xmlParser(xml, Message.class)
//		println gson.toJson(siInttra)
		
//		Invoice iv = xmlBeanParser.xmlParser(xml, Invoice.class)
//		println gson.toJson(iv)
		
//		Acknowledgement ack = xmlBeanParser.xmlParser(xml, Acknowledgement.class)
//		println gson.toJson(ack)
		
//		long start = System.currentTimeMillis()
//		
//		EDI_IFTMIN edi = xmlBeanParser.xmlParser(xml,EDI_IFTMIN.class)
//		
//		long end = System.currentTimeMillis()
//		println "total " + (end-start) +" ms."
//		
//		start = System.currentTimeMillis()
//		String str =  gson.toJson(edi)
////		println str
//		end = System.currentTimeMillis()
		
//		println "Recover " + (end-start) +" ms."
		
//		Tester tester = new Tester();
//		
//		tester.getListFieldName(IFTMBF.class)
		
//		Date date = new Date();
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
//		println dateFormat.format(date)
//		println date.format("yyyy-MM-dd'T'HH:mm:ss",TimeZone.getTimeZone("GMT"))
		
//		String json = "{'value':'\\'}"
//		
//		Map<String, String> map = [:]
////		map.put("value","\\")
//		
//		map = gson.fromJson(json,Map.class)
//		
//		println gson.toJson(map)
		
		String str = null
		
		println str ==~ /[+-]?\d+/ || str ==~ /[+-]?\d+\.\d+/
			
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
