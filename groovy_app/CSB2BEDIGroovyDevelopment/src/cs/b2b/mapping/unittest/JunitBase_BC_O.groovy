package cs.b2b.mapping.unittest

import groovy.xml.MarkupBuilder

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.sql.Connection

import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.LocationCode
import cs.b2b.core.mapping.bean.bc.*
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.mapping.e2e.util.ConnectionForTester
import cs.b2b.mapping.e2e.util.LocalFileUtil


abstract class JunitBase_BC_O {

	static String TP_ID = null
	static String MSG_TYPE_ID = null
	static String DIR_ID = null
	static String MSG_REQ_ID = null
	
	static Object mappingObj = null
	static Body body = null
	static Connection conn = null
	static StringWriter writer = null
	static MarkupBuilder markupBuilder = null

	static String basicTestingFileName = "./demo/BC_EASTMANCHEMICAL/testing_files/487-EASTMANCHEMICAL-baseline.xml"
		
	static void init(Class cls, String tpId, String msgTypeId, String dirId, String msgReqId) {
		mappingObj = cls.newInstance()
		ConnectionForTester testDBConn = new ConnectionForTester();
		conn = testDBConn.getB2BEDIQA1_DEV_DBConn();
		
		Field field = mappingObj.getClass().getDeclaredField("conn");
		field.setAccessible(true);
		field.set(mappingObj, conn)
		
		TP_ID = tpId
		MSG_TYPE_ID = msgTypeId
		DIR_ID = dirId
		MSG_REQ_ID = msgReqId
		
	}
	
	static void close() {
		if(conn != null){
			try { conn.close() } catch (Exception e) {e.printStackTrace()}
			println 'DB Connection is closed.'
		} else {
			println 'DB Connection was closed already.'
		}
	}
	
	void prepareCS2BCXmlByFile() {
		XmlBeanParser parser = new XmlBeanParser()
		String inputXmlBody = LocalFileUtil.readBigFileContentDirectly(basicTestingFileName)
		BookingConfirm bcfile = parser.xmlParser(inputXmlBody, BookingConfirm.class)
		body = bcfile.Body.get(0)
	}
	
	void prepareCS2BCXmlByManual() {
		body = new Body()
		//prepare data
		body.setGeneralInformation(new GeneralInformation())
		body.getGeneralInformation().setSCACCode("SCAC001")
		body.getGeneralInformation().setCarrierBookingNumber("1234567")
		body.getGeneralInformation().setBookingStatus("Confirmed")
		
		body.getGeneralInformation().setBookingStatusDT(new BookingStatusDT())
		body.getGeneralInformation().getBookingStatusDT().setLocDT(new LocDT())
		body.getGeneralInformation().getBookingStatusDT().getLocDT().LocDT = "2017-04-12T13:45:50"
		
		body.ExternalReference.add(new ExternalReference())
		body.getExternalReference().get(0).setCSReferenceType("SID")
		body.getExternalReference().get(0).setReferenceNumber("SID01")
		
		body.ExternalReference.add(new ExternalReference())
		body.getExternalReference().get(1).setCSReferenceType("SR")
		body.getExternalReference().get(1).setReferenceNumber("SR-01")
		
		body.setRoute(new Route())
		body.getRoute().setFirstPOL(new FirstPOL())
		body.getRoute().getFirstPOL().setDepartureDT(new DepartureDT())
		body.getRoute().getFirstPOL().getDepartureDT().setLocDT(new LocDT())
		body.getRoute().getFirstPOL().getDepartureDT().getLocDT().setLocDT("2017-03-04T05:06:07")
		body.getRoute().setLastPOD(new LastPOD())
		body.getRoute().getLastPOD().setArrivalDT(new ArrivalDT())
		body.getRoute().getLastPOD().getArrivalDT().setLocDT(new LocDT())
		body.getRoute().getLastPOD().getArrivalDT().getLocDT().setLocDT("2017-02-03T04:05:06")
		body.getRoute().setFullReturnCutoff(new FullReturnCutoff())
		body.getRoute().getFullReturnCutoff().setLocDT(new LocDT())
		body.getRoute().getFullReturnCutoff().getLocDT().setLocDT("2017-01-02T03:04:05")
		
		body.getRoute().Haulage = new Haulage()
		body.getRoute().Haulage.InBound = "C"
		body.getRoute().Haulage.OutBound = "C"
		
		body.setContainerGroup(new ContainerGroup())
		body.getContainerGroup().setContainerFlowInstruction(new ContainerFlowInstruction())
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.add(new EmptyPickup())
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).ISOSizeType = "20GF"
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).MvmtDT = new MvmtDT()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).MvmtDT.LocDT = new LocDT()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).MvmtDT.LocDT.LocDT = "2017-02-05T15:16:17"
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).NumberOfContainers = "123"
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).Address = new Address()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).Address.LocationCode = new LocationCode()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).Address.LocationCode.UNLocationCode = "ABCDE"
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).Facility = new Facility()
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).Facility.FacilityCode = "FCODE-01"
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).Facility.FacilityName = "FName-01"
		
		body.getContainerGroup().getContainerFlowInstruction().EmptyPickup.get(0).Address.City = "City001-1234567890-1234567890-1234567890"
		
	}
	
	String currentMappingDetails() {
		if (mappingObj==null)
			throw new Exception("Please initial mapping bean class first. ")
		
		writer = new StringWriter()
		markupBuilder = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false))
		
		Field field = mappingObj.getClass().getDeclaredField("TP_ID");
		field.setAccessible(true);
		field.set(mappingObj, TP_ID)
		
		field = mappingObj.getClass().getDeclaredField("MSG_TYPE_ID");
		field.setAccessible(true);
		field.set(mappingObj, MSG_TYPE_ID)
		
		field = mappingObj.getClass().getDeclaredField("DIR_ID");
		field.setAccessible(true);
		field.set(mappingObj, DIR_ID)
		
		field = mappingObj.getClass().getDeclaredField("MSG_REQ_ID");
		field.setAccessible(true);
		field.set(mappingObj, MSG_REQ_ID)
		
		String ret = ""
		
		Method method = mappingObj.getClass().getMethod("generateBody", Body.class, MarkupBuilder.class);
		method.invoke(mappingObj, body, markupBuilder)
		ret = writer.toString()
		
		return ret
	}
	
}
