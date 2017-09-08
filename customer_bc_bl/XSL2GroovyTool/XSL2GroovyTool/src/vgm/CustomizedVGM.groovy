package vgm

import java.util.Set;

class SubmitVGM implements Serializable {
	
	public static final Set<String> MultiElementList = ["ShipperSeal", "TerminalSeal", "CarrierSeal", "VeterinarySeal", "CustomsSeal", "Certificates", "Method2Weights"];
	
 	String MessageGuid;
	String MessageDateTime;
	String MessageVersion;
	String TenantID;
	String SponsorID;
	String INTTRAeVGMID;
	String State;
	Submitter Submitter = new Submitter();
	String SubmitterReference;
	String ContainerNumber;
	VerifiedGrossMass VerifiedGrossMass = new VerifiedGrossMass();
	VerificationDetails VerificationDetails = new VerificationDetails();
	String VGMDeterminationDateTime;
	VGMDeterminationLocation VGMDeterminationLocation = new VGMDeterminationLocation();
	String VGMDeterminationMethod;
	List<Certificates> CertificatesLoop = new ArrayList<Certificates>();
	List<Method2Weights> Method2WeightsLoop = new ArrayList<Method2Weights>();
	ResponsibleParty ResponsibleParty = new ResponsibleParty();
	AuthorizedParty AuthorizedParty = new AuthorizedParty();
	WeighingParty WeighingParty = new WeighingParty();
	Carrier Carrier = new Carrier();
	Shipper Shipper = new Shipper();
	Forwarder Forwarder = new Forwarder();
	Terminal Terminal = new Terminal();
	String BookingNumber;
	String BillOfLadingNumber;
	String ForwardersReferenceNumber;
	String ShippersReferenceNumber;
	ContainerDetails ContainerDetails = new ContainerDetails();
}

class Submitter implements Serializable {
	PartyID PartyID = new PartyID();
	String PartyName1;
	String PartyName2;
	String StreetAddress1;
	String StreetAddress2;
	String StreetAddress3;
	String StreetAddress4;
	String City;
	String SubdivisionCode;
	String PostalCode;
	String ISOCountryCode;
	InformationContact InformationContact = new InformationContact();
}

class PartyID implements Serializable {
	String IDType;
	String ID;
}

class InformationContact implements Serializable {
	String Name;
	String Phone;
	String Fax;
	String Email;
}

class VerifiedGrossMass implements Serializable {
	String Mass;
	String UOM;
}

class VerificationDetails implements Serializable {
	String VerificationSignature;
	String VerificationDateTime;
	String Delegated;
}

class VGMDeterminationLocation implements Serializable {
	LocationID LocationID = new LocationID();
	String City;
	String ISOCountryCode;
}

class LocationID implements Serializable {
	String IDType;
	String ID;
}

class Certificates implements Serializable {
	String Type;
	String Number;
	String IssueDateTime;
	String ExpirationDateTime;
}

class Method2Weights implements Serializable {
	String WeightType;
	String Weight;
	String UOM;
}

class ResponsibleParty implements Serializable {
	PartyID PartyID = new PartyID();
	String PartyName1;
	String PartyName2;
	String StreetAddress1;
	String StreetAddress2;
	String StreetAddress3;
	String StreetAddress4;
	String City;
	String SubdivisionCode;
	String PostalCode;
	String ISOCountryCode;
	InformationContact InformationContact = new InformationContact();
}

class AuthorizedParty implements Serializable {
	PartyID PartyID = new PartyID();
	String PartyName1;
	String PartyName2;
	String StreetAddress1;
	String StreetAddress2;
	String StreetAddress3;
	String StreetAddress4;
	String City;
	String SubdivisionCode;
	String PostalCode;
	String ISOCountryCode;
	InformationContact InformationContact = new InformationContact();
}

class WeighingParty implements Serializable {
	PartyID PartyID = new PartyID();
	String PartyName1;
	String PartyName2;
	String StreetAddress1;
	String StreetAddress2;
	String StreetAddress3;
	String StreetAddress4;
	String City;
	String SubdivisionCode;
	String PostalCode;
	String ISOCountryCode;
	InformationContact InformationContact = new InformationContact();
}

class Carrier implements Serializable {
	PartyID PartyID = new PartyID();
	String PartyName1;
	String PartyName2;
	String StreetAddress1;
	String StreetAddress2;
	String StreetAddress3;
	String StreetAddress4;
	String City;
	String SubdivisionCode;
	String PostalCode;
	String ISOCountryCode;
	InformationContact InformationContact = new InformationContact();
}

class Shipper implements Serializable {
	PartyID PartyID = new PartyID();
	String PartyName1;
	String PartyName2;
	String StreetAddress1;
	String StreetAddress2;
	String StreetAddress3;
	String StreetAddress4;
	String City;
	String SubdivisionCode;
	String PostalCode;
	String ISOCountryCode;
	InformationContact InformationContact = new InformationContact();
}

class Forwarder implements Serializable {
	PartyID PartyID = new PartyID();
	String PartyName1;
	String PartyName2;
	String StreetAddress1;
	String StreetAddress2;
	String StreetAddress3;
	String StreetAddress4;
	String City;
	String SubdivisionCode;
	String PostalCode;
	String ISOCountryCode;
	InformationContact InformationContact = new InformationContact();
}

class Terminal implements Serializable {
	PartyID PartyID = new PartyID();
	String PartyName1;
	String PartyName2;
	String StreetAddress1;
	String StreetAddress2;
	String StreetAddress3;
	String StreetAddress4;
	String City;
	String SubdivisionCode;
	String PostalCode;
	String ISOCountryCode;
	InformationContact InformationContact = new InformationContact();
}

class ContainerDetails implements Serializable {
	String ShipperOwned;
	String ISOContainerType;
	List<String> CarrierSeal = new ArrayList<String>();
	List<String> ShipperSeal = new ArrayList<String>();
	List<String> CustomsSeal = new ArrayList<String>();
	List<String> VeterinarySeal = new ArrayList<String>();
	List<String> TerminalSeal = new ArrayList<String>();
}


