package cs.b2b.core.mapping.bean.ack.cs2

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set;;

class Acknowledgement implements Serializable {
	public static final Set<String> MultiElementList = ['BookingNumber', 'Notes', 'ExternalReference', 'Body']
	Header Header;
	List<Body> Body = new ArrayList<Body>();
}

class Header implements Serializable {
	String ControlNumber;
	cs.b2b.core.mapping.bean.MsgDT MsgDT;
	String MsgDirection;
	String MsgType;
	String SenderID;
	String ReceiverID;
	String Action;
	String Version;
	String InterchangeMessageID;
	String FileName;
	String DataSource;
	String TargetCustomCountryCode;
	cs.b2b.core.mapping.bean.NVOProperty NVOProperty;
}

class Body implements Serializable {
	TransactionInformation TransactionInformation;
	EventInformation EventInformation;
	GeneralInformation GeneralInformation;
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	List<Notes> Notes = new ArrayList<Notes>();
}

class TransactionInformation implements Serializable {
	String MessageID;
	String GroupControlNumber;
	String InterchangeTransactionID;
}

class EventInformation implements Serializable {
	String EventCode;
	String EventDescription;
	cs.b2b.core.mapping.bean.EventDT EventDT;
}

class GeneralInformation implements Serializable {
	String CustomerTradingPartnerID;
	String MessageID;
	String ControlNumber;
	String InterchangeMessageID;
	String GroupControlNumber;
	String InterchangeTransactionID;
	String CSReferenceNumber;
	String MessageStatusCode;
	String MessageStatusDescription;
	List<String> BookingNumber = new ArrayList<String>();
	String SCAC;
	String SPCompanyID;
}

class ExternalReference implements Serializable {
	String CSReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
	String EDIReferenceType;
}

class Notes implements Serializable {
	String Code;
	String Description;
	String IsOpenToCustomer;
}