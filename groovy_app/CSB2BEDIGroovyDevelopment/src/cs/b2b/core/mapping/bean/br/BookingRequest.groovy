package cs.b2b.core.mapping.bean.br

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set;;

class BookingRequest implements Serializable {
	
	public static final Set<String> MultiElementList= ["AddressLine", "ChargePaymentInstruction", "Reference", "DoorAppointment", "IntendedDT", "OceanLeg", "Request.Remarks", "Container.Remarks", "CargoInfo.Remarks", "VerifiedGrossMass", "MarksAndNumbers", "EmergencyContact", "Label", "AWCargoSpec", "DGCargoSpec", "ReeferCargoSpec", "SpecialHandling", "NewStructure", "secondGuideName", "routingGuide.routingGuide", "BookingReminder", "Party", "CarrierRateReference", "Cargo", "UploadedDocument", "ExternalReference", "Memo", "Container", "PendingReasonCode", "Request"]
	
	Header Header;
	List<Request> Request = new ArrayList<Request>();
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
	DocumentationDate DocumentationDate;
}

class DocumentationDate implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class Request implements Serializable {
	TransactionInformation TransactionInformation;
	EventInformation EventInformation;
	GeneralInformation GeneralInformation;
	List<Party> Party = new ArrayList<Party>();
	List<CarrierRateReference> CarrierRateReference = new ArrayList<CarrierRateReference>();
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	List<Container> Container = new ArrayList<Container>();
	List<Cargo> Cargo = new ArrayList<Cargo>();
	SpecialInstruction SpecialInstruction;
	List<Remarks> Remarks = new ArrayList<Remarks>();
	List<String> PendingReasonCode = new ArrayList<String>();
	List<BookingReminder> BookingReminder = new ArrayList<BookingReminder>();
	List<Memo> Memo = new ArrayList<Memo>();
	EDIDynamicStructure EDIDynamicStructure;
	List<UploadedDocument> UploadedDocument = new ArrayList<UploadedDocument>();
	RoutingGuide routingGuide;
	String IRIS2UIFPlaceHolder;
}

class Remarks implements Serializable {
	String attr_RemarkType;
 	String Remarks;

	@Override
	public String toString() {
		return this.Remarks;
	}
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
	String CSBookingRefNumber;
	String CarrierBookingNumber;
	String ActionType;
	String SCAC;
	String SPCompanyID;
	BookingOffice BookingOffice;
	Requested Requested;
	Amended Amended;
	String ShipmentCargoType;
	ShipmentTrafficMode ShipmentTrafficMode;
	String Barcode;
	String NotificationEmailAddress;
	String isNeedReplyPartyEmail;
	String TermsOfPayment;
	String CustomerBookingReference;
	String BRVersionNumber;
	String CSBookingStatus;
	FollowingBooking FollowingBooking;
	TotalGrossWeight TotalGrossWeight;
	String TotalNumberOfPackage;
	TotalConsignment TotalConsignment;
	String TotalNumberOfEquipment;
	PlaceOfPayment PlaceOfPayment;
	String IsAcceptPartialShipment;
	String ServiceRequestLockID;
}

class BookingOffice implements Serializable {
	String BookingRegion;
	String UNLocCode;
	String BookingOffice;
	String BookingOfficeName;
	String BLIssuingOffice;
}

class Requested implements Serializable {
	String By;
	RequestedDT RequestedDT;
}

class RequestedDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class Amended implements Serializable {
	String By;
	AmendedDT AmendedDT;
}

class AmendedDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ShipmentTrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class FollowingBooking implements Serializable {
	String MasterBookingNumber;
}

class TotalGrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class TotalConsignment implements Serializable {
	String Volume;
	String VolumeUnit;
}

class PlaceOfPayment implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String LocationName;
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
	String SalesOfficeCode;
	String EDIPartyType;
	String CustomerCarrierCode;
	String CodeListQualifier;
	List<ChargePaymentInstruction> ChargePaymentInstruction = new ArrayList<ChargePaymentInstruction>();
}

class ChargePaymentInstruction implements Serializable {
	String ChargeCategoryCode;
	String PrepaidCollectIndicator;
	PaymentLocation PaymentLocation;
}

class PaymentLocation implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String LocationName;
}

class CarrierRateReference implements Serializable {
	String CSCarrierRateType;
	String CarrierRateNumber;
}

class ExternalReference implements Serializable {
	String CSReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
	String EDIReferenceType;
}

class Container implements Serializable {
	String CarrCntrSizeType;
	String CSContainerSizeType;
	String Quantity;
	GrossWeight GrossWeight;
	NetWeight NetWeight;
	String IsSOC;
	OBDoor OBDoor;
	IBDoor IBDoor;
	Haulage Haulage;
	Route Route;
	List<String> Remarks = new ArrayList<String>();
	Volume Volume;
	List<VerifiedGrossMass> VerifiedGrossMass = new ArrayList<VerifiedGrossMass>();
	BRHaulage BRHaulage;
}

class GrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class NetWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class OBDoor implements Serializable {
	EmptyPickupDT EmptyPickupDT;
	FullReturnDT FullReturnDT;
	List<DoorAppointment> DoorAppointment = new ArrayList<DoorAppointment>();
}

class EmptyPickupDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class FullReturnDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class DoorAppointment implements Serializable {
	cs.b2b.core.mapping.bean.AppointmentDT AppointmentDT;
	cs.b2b.core.mapping.bean.Address Address;
	String Company;
	cs.b2b.core.mapping.bean.Contact Contact;
	String Quantity;
	String Remarks;
	String PickupNumber;
}

class IBDoor implements Serializable {
	DestinationDT DestinationDT;
}

class DestinationDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class Haulage implements Serializable {
	String OutBound;
	String InBound;
}

class Route implements Serializable {
	String TradeLane;
	String TotalTransitTime;
	List<IntendedDT> IntendedDT = new ArrayList<IntendedDT>();
	POR POR;
	FND FND;
	SailingSchedule SailingSchedule;
	FirstPOL FirstPOL;
	LastPOD LastPOD;
	CargoNeededAtOriginDT CargoNeededAtOriginDT;
	CargoReadyForShippingDT CargoReadyForShippingDT;
	CargoNeededAtDestDT CargoNeededAtDestDT;
	CargoAvailableAtDestDT CargoAvailableAtDestDT;
	LoadingPortLatestDepDT LoadingPortLatestDepDT;
	DischargePortLatestArrDT DischargePortLatestArrDT;
	RequestDepartureDT RequestDepartureDT;
	LatestArrivalDT LatestArrivalDT;
	String IsFromSailingSchedule;
	List<OceanLeg> OceanLeg = new ArrayList<OceanLeg>();
}

class IntendedDT implements Serializable {
	From From;
	To To;
	String IntendedRangeIndicator;
}

class From implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class To implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
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
}

class SailingSchedule implements Serializable {
	Origin Origin;
	Destination Destination;
}

class Origin implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
}

class Destination implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
}

class FirstPOL implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
	String LoadingPortVoyage;
	String LoadingPortVesselOperator;
	String LoadingPortVesselName;
}

class LastPOD implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
}

class CargoNeededAtOriginDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class CargoReadyForShippingDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class CargoNeededAtDestDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class CargoAvailableAtDestDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class LoadingPortLatestDepDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class DischargePortLatestArrDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class RequestDepartureDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class LatestArrivalDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class OceanLeg implements Serializable {
	String LegSeq;
	POL POL;
	POD POD;
	SVVD SVVD;
	ETD ETD;
	ETA ETA;
}

class POL implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
}

class POD implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
}

class SVVD implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String ExternalVoyageNumber;
}

class ETD implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ETA implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class Volume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class VerifiedGrossMass implements Serializable {
	String ReferenceNumber;
	String VGMContainerNumber;
	Weight Weight;
	String WeightedMethod;
	WeightedDate WeightedDate;
	String ResponsibleParty;
	String AuthorizedOfficial;
	ReceivedDate ReceivedDate;
	String ReceivedFromId;
	WeightPartyContactInfo WeightPartyContactInfo;
}

class Weight implements Serializable {
	String Weight;
	String WeightUnit;
}

class WeightedDate implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ReceivedDate implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class WeightPartyContactInfo implements Serializable {
	String FirstName;
	String LastName;
	cs.b2b.core.mapping.bean.ContactPhone ContactPhone;
	cs.b2b.core.mapping.bean.ContactFax ContactFax;
	String ContactEmailAddress;
}

class BRHaulage implements Serializable {
	String OutBound;
	String InBound;
}

class Cargo implements Serializable {
	CargoInfo CargoInfo;
	List<ReeferCargoSpec> ReeferCargoSpec = new ArrayList<ReeferCargoSpec>();
	List<DGCargoSpec> DGCargoSpec = new ArrayList<DGCargoSpec>();
	List<AWCargoSpec> AWCargoSpec = new ArrayList<AWCargoSpec>();
	TrafficMode TrafficMode;
}

class CargoInfo implements Serializable {
	String CargoNature;
	String CargoDescription;
	cs.b2b.core.mapping.bean.Packaging Packaging;
	cs.b2b.core.mapping.bean.GrossWeight GrossWeight;
	cs.b2b.core.mapping.bean.NetWeight NetWeight;
	cs.b2b.core.mapping.bean.Volume Volume;
	List<cs.b2b.core.mapping.bean.MarksAndNumbers> MarksAndNumbers = new ArrayList<cs.b2b.core.mapping.bean.MarksAndNumbers>();
	List<String> Remarks = new ArrayList<String>();
	String HarmonizedTariffSchedule;
}

class ReeferCargoSpec implements Serializable {
	String AtmosphereType;
	cs.b2b.core.mapping.bean.Temperature Temperature;
	cs.b2b.core.mapping.bean.Ventilation Ventilation;
	String GensetType;
	String Remarks;
	String CO2;
	String O2;
	String VentSettingCode;
	String DehumidityPercentage;
	String SensitiveCargoDesc;
	String IsPreCoolingReq;
	String IsControlledAtmosphere;
	String IsReeferOperational;
	List<cs.b2b.core.mapping.bean.EmergencyContact> EmergencyContact = new ArrayList<cs.b2b.core.mapping.bean.EmergencyContact>();
	String RF_CGO_INFO_UUID;
	String ReeferUsage;
	String IsCargoObnoxious;
	String CanCargoTaintContainer;
	String ApprovalReference;
	AvgCargoGrossWeight AvgCargoGrossWeight;
}

class AvgCargoGrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class DGCargoSpec implements Serializable {
	String DGRegulator;
	String IMDGPage;
	String IMOClass;
	String UNNumber;
	String TechnicalName;
	String ChinaDGNumber;
	String ProperShippingName;
	cs.b2b.core.mapping.bean.PackageGroup PackageGroup;
	String MFAGNumber;
	String EMSNumber;
	String PSAClass;
	String ApprovalCode;
	cs.b2b.core.mapping.bean.GrossWeight GrossWeight;
	cs.b2b.core.mapping.bean.NetWeight NetWeight;
	cs.b2b.core.mapping.bean.NetExplosiveWeight NetExplosiveWeight;
	cs.b2b.core.mapping.bean.FlashPoint FlashPoint;
	cs.b2b.core.mapping.bean.ElevatedTemperature ElevatedTemperature;
	String isLimitedQuantity;
	String IsInhalationHazardous;
	String IsReportableQuantity;
	String IsEmptyUnclean;
	String isMarinePollutant;
	String State;
	List<String> Label = new ArrayList<String>();
	String Remarks;
	List<cs.b2b.core.mapping.bean.EmergencyContact> EmergencyContact = new ArrayList<cs.b2b.core.mapping.bean.EmergencyContact>();
	String DG_CGO_INFO_UUID;
	String SequenceNumber;
	String Measurement;
	String MeasurementUnit;
	String FinalProperShippingName;
	String Description;
	String ExternalApprovalParty;
	String ExternalApprovalReference;
	String EmergencyResponseCode;
	String ControlTemperatureValue;
	String ControlTemperatureUnit;
	String SadtValue;
	String SadtUnit;
	String EmergencyTemperatureValue;
	String EmergencyTemperatureUnit;
	String ConvertedEmergencyTemperatureValue;
	String ConvertedEmergencyTemperatureUnit;
}

class AWCargoSpec implements Serializable {
	cs.b2b.core.mapping.bean.Height Height;
	cs.b2b.core.mapping.bean.Length Length;
	cs.b2b.core.mapping.bean.Width Width;
	cs.b2b.core.mapping.bean.GrossWeight GrossWeight;
	String IsShipsideDelivery;
	String Remarks;
	List<cs.b2b.core.mapping.bean.EmergencyContact> EmergencyContact = new ArrayList<cs.b2b.core.mapping.bean.EmergencyContact>();
	String AW_CGO_INFO_UUID;
	String DeliveryType;
	String EstimatedTEU;
	String OnDeckIndicator;
	String ApprovalReference;
	String Description;
}

class TrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class SpecialInstruction implements Serializable {
	List<SpecialHandling> SpecialHandling = new ArrayList<SpecialHandling>();
	String ExportDeclarationRequired;
}

class SpecialHandling implements Serializable {
	String Code;
	String Description;
}

class BookingReminder implements Serializable {
	String PartyType;
	String Code;
	String Description;
}

class Memo implements Serializable {
	String UserID;
	String CreationDateTime;
	String MemoContent;
}

class EDIDynamicStructure implements Serializable {
	List<NewStructure> NewStructure = new ArrayList<NewStructure>();
}

class NewStructure implements Serializable {
	String ParentStructureName;
	String NewElementName;
	String NewElementValue;
}

class UploadedDocument implements Serializable {
	String Category;
	String DocumentTypeName;
	String DmUUID;
	String FileName;
	String Remarks;
	String CompanyID;
	String UploadedUserID;
	UploadedDT UploadedDT;
}

class UploadedDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class RoutingGuide implements Serializable {
	String isCompliance;
	List<cs.b2b.core.mapping.bean.RoutingGuide> routingGuide = new ArrayList<cs.b2b.core.mapping.bean.RoutingGuide>();
	String reasonType;
	String reason;
}