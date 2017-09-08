package cs.b2b.core.mapping.bean.ct;


/**
 * @author RENGA
 * @cs2xmlVersion 67.3
 */
class ContainerMovement implements Serializable {
	public static final Set<String> MultiElementList = ["Seal", "ArrivalDT", "DepartureDT", "ArrivalAtFinalHub", "OceanLeg", "CargoReceiptDT", "FullReturnCutoffDT", "CargoDeliveryDT", "FullPickupDT", "AddressLine", "Reference", "MarksAndNumbers", "BLGeneralInfo", "MileStones", "ExternalReference", "BookingGeneralInfo", "Party", "AssociatedExternalReference", "Cargo", "DnD", "Body"];
	
	Header Header;
	List<Body> Body = new ArrayList<Body>();
	public static void main(String[] args) {
		ContainerMovement
	}
	
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
	Event Event;
	GeneralInformation GeneralInformation;
	Container Container;
	List<BookingGeneralInfo> BookingGeneralInfo = new ArrayList<BookingGeneralInfo>();
	List<BLGeneralInfo> BLGeneralInfo = new ArrayList<BLGeneralInfo>();
	Route Route;
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	List<Party> Party = new ArrayList<Party>();
	List<Cargo> Cargo = new ArrayList<Cargo>();
	List<MileStones> MileStones = new ArrayList<MileStones>();
	List<AssociatedExternalReference> AssociatedExternalReference = new ArrayList<AssociatedExternalReference>();
	List<DnD> DnD = new ArrayList<DnD>();
}

class TransactionInformation implements Serializable {
	String MessageID;
	String GroupControlNumber;
	String InterchangeTransactionID;
}

class Event implements Serializable {
	String CarrEventCode;
	CSEvent CSEvent;
	String EventDescription;
	Location Location;
	EventDT EventDT;
	String CurrentShipmentLeg;
	String CarrierUniqueMovementID;
	String ContraIndicator;
	String CS1Event;
	ResendInformation ResendInformation;
}

class CSEvent implements Serializable {
	String CSEventCode;
	String DimensionType;
	String DimensionValue;
	String EstActIndicator;
}

class Location implements Serializable {
	String LocationName;
	CityDetails CityDetails;
	LocationCode LocationCode;
	CSStandardCity CSStandardCity;
	String CSPortID;
}

class CityDetails implements Serializable {
	String City;
	String County;
	String State;
	String Country;
}

class LocationCode implements Serializable {
	String MutuallyDefinedCode;
	String UNLocationCode;
	String SchedKDType;
	String SchedKDCode;
}

class CSStandardCity implements Serializable {
	String CSParentCityID;
	String CSStateCode;
	String CSCountryCode;
	String CSContinentCode;
}

class EventDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ResendInformation implements Serializable {
	String Description;
	String EDIFileName;
}

class GeneralInformation implements Serializable {
	String SCAC;
	String SPCompanyID;
	String TransportMode;
	String TransportCarrier;
	String UpdateParty;
	String IsCopy;
	String Label;
	String ProposedAction;
	String ActionType;
	TimeOfIssue TimeOfIssue;
	String IsCSCustomer;
	String PartnerID;
	String SubSCACCode;
}

class TimeOfIssue implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class Container implements Serializable {
	String ContainerNumber;
	String ContainerCheckDigit;
	String ContainerStatus;
	String TransferFromBookingNumber;
	String TransferToBookingNumber;
	String RepackContainerNumber;
	String LadingQuantity;
	String CarrCntrSizeType;
	String CSContainerSizeType;
	List<Seal> Seal = new ArrayList<Seal>();
	Haulage Haulage;
	String CustomsDeclarationNumber;
	String CSS_CNTR_UUID;
	String RailPickupNumber;
	String CS1ContainerSizeType;
	String EmptyReturnFacilityCode;
}

class Seal implements Serializable {
	String SealType;
	String SealNumber;
}

class Haulage implements Serializable {
	String OutBound;
	String InBound;
}

class BookingGeneralInfo implements Serializable {
	String CarrierBookingNumber;
	Haulage Haulage;
	String BookingOffice;
	Packaging Packaging;
}

class Packaging implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class BLGeneralInfo implements Serializable {
	String BLNumber;
	BLIssueDT BLIssueDT;
	Haulage Haulage;
	String CustomsReferenceType;
	String CustomsReferenceNumber;
	String CustomsClearanceLocationCode;
	CustomsReferenceDT CustomsReferenceDT;
	BLSVVD BLSVVD;
	Packaging Packaging;
}

class BLIssueDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class CustomsReferenceDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class BLSVVD implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String ServiceName;
}

class Route implements Serializable {
	POR POR;
	FND FND;
	FirstPOL FirstPOL;
	LastPOD LastPOD;
	List<ArrivalAtFinalHub> ArrivalAtFinalHub = new ArrayList<ArrivalAtFinalHub>();
	List<FullPickupDT> FullPickupDT = new ArrayList<FullPickupDT>();
	List<FullReturnCutoffDT> FullReturnCutoffDT = new ArrayList<FullReturnCutoffDT>();
	List<CargoReceiptDT> CargoReceiptDT = new ArrayList<CargoReceiptDT>();
	List<CargoDeliveryDT> CargoDeliveryDT = new ArrayList<CargoDeliveryDT>();
	List<OceanLeg> OceanLeg = new ArrayList<OceanLeg>();
	String Inbound_intermodal_indicator;
	String Outbound_intermodal_indicator;
}

class POR implements Serializable {
	String LocationName;
	cs.b2b.core.mapping.bean.CityDetails CityDetails;
	cs.b2b.core.mapping.bean.Facility Facility;
	cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity;
}

class FND implements Serializable {
	String LocationName;
	cs.b2b.core.mapping.bean.CityDetails CityDetails;
	cs.b2b.core.mapping.bean.Facility Facility;
	cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity;
	List<ArrivalDT> ArrivalDT = new ArrayList<ArrivalDT>();
}

class ArrivalDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class FirstPOL implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
	String CSStateCode;
	String CSParentCityID;
}

class LastPOD implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
	String CSStateCode;
	String CSParentCityID;
}

class ArrivalAtFinalHub implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class FullPickupDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class FullReturnCutoffDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class CargoReceiptDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class CargoDeliveryDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class OceanLeg implements Serializable {
	String LegSeq;
	POL POL;
	POD POD;
	SVVD SVVD;
}

class POL implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
	List<DepartureDT> DepartureDT = new ArrayList<DepartureDT>();
	String CSStateCode;
	String CSParentCityID;
}

class DepartureDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class POD implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
	List<ArrivalDT> ArrivalDT = new ArrayList<ArrivalDT>();
	String CSStateCode;
	String CSParentCityID;
}

class SVVD implements Serializable {
	Loading Loading;
	Discharge Discharge;
}

class Loading implements Serializable {
	String Service;
	String ServiceName;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String RegistrationCountry;
	String RegistrationCountryCode;
	String ExternalVoyage;
}

class Discharge implements Serializable {
	String Service;
	String ServiceName;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String RegistrationCountry;
	String RegistrationCountryCode;
	String ExternalVoyage;
}

class ExternalReference implements Serializable {
	String CSReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
}

class Party implements Serializable {
	String PartyType;
	String PartyName;
	String CSCompanyID;
	String CarrierCustomerCode;
	String isNeedReplyPartyEmail;
	cs.b2b.core.mapping.bean.Contact Contact;
	cs.b2b.core.mapping.bean.Address Address;
	List<cs.b2b.core.mapping.bean.Reference> Reference = new ArrayList<cs.b2b.core.mapping.bean.Reference>();
	String PartyLevel;
}

class Cargo implements Serializable {
	String CargoNature;
	String CargoDescription;
	cs.b2b.core.mapping.bean.Packaging Packaging;
	cs.b2b.core.mapping.bean.GrossWeight GrossWeight;
	cs.b2b.core.mapping.bean.NetWeight NetWeight;
	cs.b2b.core.mapping.bean.Volume Volume;
	List<cs.b2b.core.mapping.bean.MarksAndNumbers> MarksAndNumbers = new ArrayList<cs.b2b.core.mapping.bean.MarksAndNumbers>();
	CustomsReference CustomsReference;
}

class CustomsReference implements Serializable {
	String ReferenceNumber;
	String ReferenceType;
	ReferenceDT ReferenceDT;
}

class ReferenceDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class MileStones implements Serializable {
	String CarrEventCode;
	CSEvent CSEvent;
	String EventDescription;
	Location Location;
	EventDT EventDT;
	String CurrentShipmentLeg;
	String CarrierUniqueMovementID;
	String SCAC;
	String SPCompanyID;
	String TransportMode;
	String TransportCarrier;
	String UpdateParty;
	String CS1Event;
}

class AssociatedExternalReference implements Serializable {
	String CSReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
	String EDIReferenceType;
	String EDIReferenceCode;
	String ReferenceNumberIndicator;
	String IsShowOnBL;
	String MsgType;
}

class DnD implements Serializable {
	String attr_Type;
 	String ContainerNumber;
	String ContainerCheckDigit;
	FreeTimeStartDT FreeTimeStartDT;
	FreeTimeEndDT FreeTimeEndDT;
	String FreeTime;
	String FreeTimeType;
	String CSS_BL_CNTR_UUID;
	ClockEndDT ClockEndDT;
	String IsCombo;
}

class FreeTimeStartDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class FreeTimeEndDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ClockEndDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}