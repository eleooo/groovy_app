package so

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShippingOrder implements Serializable {
	Header Header = new Header();
	List<Order> OrderLoop = new ArrayList<Order>();
}

class Header implements Serializable {
	String ControlNumber;
	MsgDT MsgDT = new MsgDT();
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
	NVOProperty NVOProperty = new NVOProperty();
}

class MsgDT implements Serializable {
	String GMT;
	LocDT LocDT = new LocDT();
}

class LocDT implements Serializable {
	String attr_CSTimeZone;
 	String attr_TimeZone;
 	String LocDT;

	@Override
	public String toString() {
		return this.LocDT;
	}
}

class NVOProperty implements Serializable {
	String Name;
	String Value;
}

class Order implements Serializable {
	TransactionInformation TransactionInformation = new TransactionInformation();
	EventInformation EventInformation = new EventInformation();
	GeneralInformation GeneralInformation = new GeneralInformation();
	List<Party> PartyLoop = new ArrayList<Party>();
	List<ExternalReference> ExternalReferenceLoop = new ArrayList<ExternalReference>();
	List<Container> ContainerLoop = new ArrayList<Container>();
	List<Cargo> CargoLoop = new ArrayList<Cargo>();
	Route Route = new Route();
	EDIDynamicStructure EDIDynamicStructure = new EDIDynamicStructure();
}

class Remarks implements Serializable {
	String attr_RemarkCode;
 	String attr_RemarkType;
 	List<String> Remarks = new ArrayList<String>();
}

class TransactionInformation implements Serializable {
	String MessageID;
	String GroupControlNumber;
	String InterchangeTransactionID;
}

class EventInformation implements Serializable {
	String EventCode;
	String EventDescription;
	EventDT EventDT = new EventDT();
}

class EventDT implements Serializable {
	String GMT;
	LocDT LocDT = new LocDT();
}

class GeneralInformation implements Serializable {
	String DocumentNameCode;
	String CSSORefNumber;
	String CarrierBookingNumber;
	String ActionType;
	String SCAC;
	String SPCompanyID;
	String SOVersionNumber;
	Requested Requested = new Requested();
	String ShipmentCargoType;
	Haulage Haulage = new Haulage();
	ShipmentTrafficMode ShipmentTrafficMode = new ShipmentTrafficMode();
	String CustomerBookingReference;
	List<MonetaryAmount> MonetaryAmountLoop = new ArrayList<MonetaryAmount>();
	List<ChargePaymentInstruction> ChargePaymentInstructionLoop = new ArrayList<ChargePaymentInstruction>();
	TotalGrossWeight TotalGrossWeight = new TotalGrossWeight();
	String TotalNumberOfPackage;
	TotalConsignment TotalConsignment = new TotalConsignment();
	String TotalNumberOfEquipment;
	PlaceOfPayment PlaceOfPayment = new PlaceOfPayment();
	BillOfLadingReleaseOffice BillOfLadingReleaseOffice = new BillOfLadingReleaseOffice();
	IssuranceOfBLDT IssuranceOfBLDT = new IssuranceOfBLDT();
	ExpiryDateOfLetterOfCredit ExpiryDateOfLetterOfCredit = new ExpiryDateOfLetterOfCredit();
	IssuanceDateOfLetterOfCredit IssuanceDateOfLetterOfCredit = new IssuanceDateOfLetterOfCredit();
	OriginalLocation OriginalLocation = new OriginalLocation();
	String TransportDocumentCurrency;
}

class Requested implements Serializable {
	String By;
	RequestedDT RequestedDT = new RequestedDT();
}

class RequestedDT implements Serializable {
	String GMT;
	LocDT LocDT = new LocDT();
}

class Haulage implements Serializable {
	String OutBound;
	String InBound;
}

class ShipmentTrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class MonetaryAmount implements Serializable {
	String MonetaryAmountCode;
	String MonetaryAmountValue;
}

class ChargePaymentInstruction implements Serializable {
	String ChargeCategoryCode;
	String PrepaidCollectIndicator;
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
	LocationCode LocationCode = new LocationCode();
	String LocationName;
}

class LocationCode implements Serializable {
	String MutuallyDefinedCode;
	String UNLocationCode;
	String SchedKDType;
	String SchedKDCode;
}

class BillOfLadingReleaseOffice implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	LocationCode LocationCode = new LocationCode();
	String LocationName;
}

class IssuranceOfBLDT implements Serializable {
	String GMT;
	LocDT LocDT = new LocDT();
}

class ExpiryDateOfLetterOfCredit implements Serializable {
	String GMT;
	LocDT LocDT = new LocDT();
}

class IssuanceDateOfLetterOfCredit implements Serializable {
	String GMT;
	LocDT LocDT = new LocDT();
}

class OriginalLocation implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	LocationCode LocationCode = new LocationCode();
	String LocationName;
}

class Party implements Serializable {
	String PartyType;
	String PartyName;
	String CSCompanyID;
	String CarrierCustomerCode;
	String isNeedReplyPartyEmail;
	Contact Contact = new Contact();
	Address Address = new Address();
	List<Reference> ReferenceLoop = new ArrayList<Reference>();
	String EDIPartyType;
	String CustomerCarrierCode;
	String CodeListQualifier;
	List<DocumentMessageDetails> DocumentMessageDetailsLoop = new ArrayList<DocumentMessageDetails>();
	List<SOPartyContact> SOPartyContactLoop = new ArrayList<SOPartyContact>();
}

class Contact implements Serializable {
	String FirstName;
	String LastName;
	ContactPhone ContactPhone = new ContactPhone();
	ContactFax ContactFax = new ContactFax();
	String ContactEmailAddress;
}

class ContactPhone implements Serializable {
	String CountryCode;
	String AreaCode;
	String Number;
}

class ContactFax implements Serializable {
	String CountryCode;
	String AreaCode;
	String Number;
}

class Address implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	LocationCode LocationCode = new LocationCode();
	String PostalCode;
	AddressLines AddressLines = new AddressLines();
}

class AddressLines implements Serializable {
	List<String> AddressLine = new ArrayList<String>();
}

class Reference implements Serializable {
	String Type;
	String Number;
}

class DocumentMessageDetails implements Serializable {
	String DocumentNameCode;
	String DocumentName;
	String DocumentStatusCode;
	String NumberOfCopiesOfDocumentRequired;
}

class SOPartyContact implements Serializable {
	String FirstName;
	String LastName;
	List<ContactPhone> ContactPhoneLoop = new ArrayList<ContactPhone>();
	List<ContactFax> ContactFaxLoop = new ArrayList<ContactFax>();
	List<String> ContactEmailAddress = new ArrayList<String>();
}

class ExternalReference implements Serializable {
	String CSReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
	String EDIReferenceType;
}

class Container implements Serializable {
	String ContainerSequenceID;
	String ContainerNumber;
	String CarrCntrSizeType;
	String CSContainerSizeType;
	String Quantity;
	GrossWeight GrossWeight = new GrossWeight();
	String IsSOC;
	GrossVolume GrossVolume = new GrossVolume();
	ReeferCargoSpec ReeferCargoSpec = new ReeferCargoSpec();
	List<Seal> SealLoop = new ArrayList<Seal>();
	List<FreeText> FreeTextLoop = new ArrayList<FreeText>();
}

class GrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class GrossVolume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class ReeferCargoSpec implements Serializable {
	String AtmosphereType;
	Temperature Temperature = new Temperature();
	Ventilation Ventilation = new Ventilation();
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
	List<EmergencyContact> EmergencyContactLoop = new ArrayList<EmergencyContact>();
	String RF_CGO_INFO_UUID;
}

class Temperature implements Serializable {
	String Temperature;
	String TemperatureUnit;
}

class Ventilation implements Serializable {
	String Ventilation;
	String VentilationUnit;
}

class EmergencyContact implements Serializable {
	String FirstName;
	String LastName;
	ContactPhone ContactPhone = new ContactPhone();
	ContactFax ContactFax = new ContactFax();
	String ContactEmailAddress;
	String Type;
	String EM_CTACT_UUID;
}

class Seal implements Serializable {
	String SealType;
	String SealNumber;
	String SealTypeName;
}

class FreeText implements Serializable {
	String TextSubjectCode;
	List<String> TextLiteral = new ArrayList<String>();
}

class Cargo implements Serializable {
	CargoInfo CargoInfo = new CargoInfo();
	List<ReeferCargoSpec> ReeferCargoSpecLoop = new ArrayList<ReeferCargoSpec>();
	List<DGCargoSpec> DGCargoSpecLoop = new ArrayList<DGCargoSpec>();
	List<AWCargoSpec> AWCargoSpecLoop = new ArrayList<AWCargoSpec>();
	List<ExternalReference> ExternalReferenceLoop = new ArrayList<ExternalReference>();
	List<String> USCensusBureauScheduleBNumber = new ArrayList<String>();
	List<ContainerLoadPlan> ContainerLoadPlanLoop = new ArrayList<ContainerLoadPlan>();
	TrafficMode TrafficMode = new TrafficMode();
}

class CargoInfo implements Serializable {
	String CargoNature;
	String CargoDescription;
	Packaging Packaging = new Packaging();
	GrossWeight GrossWeight = new GrossWeight();
	NetWeight NetWeight = new NetWeight();
	Volume Volume = new Volume();
	List<MarksAndNumbers> MarksAndNumbersLoop = new ArrayList<MarksAndNumbers>();
	List<String> HarmonizedTariffSchedule = new ArrayList<String>();
	List<FreeText> FreeTextLoop = new ArrayList<FreeText>();
	List<SOMarksAndNumbers> SOMarksAndNumbersLoop = new ArrayList<SOMarksAndNumbers>();
	String ItemNumber;
	InnerPackaging InnerPackaging = new InnerPackaging();
	InnerGrossWeight InnerGrossWeight = new InnerGrossWeight();
	InnerNetWeight InnerNetWeight = new InnerNetWeight();
	InnerVolume InnerVolume = new InnerVolume();
	InnerInnerPackaging InnerInnerPackaging = new InnerInnerPackaging();
	InnerInnerGrossWeight InnerInnerGrossWeight = new InnerInnerGrossWeight();
	InnerInnerNetWeight InnerInnerNetWeight = new InnerInnerNetWeight();
	InnerInnerVolume InnerInnerVolume = new InnerInnerVolume();
}

class Packaging implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class NetWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class Volume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class MarksAndNumbers implements Serializable {
	String SeqNumber;
	String MarksAndNumbersLine;
	String CGO_MRK_NUM_ID;
}

class SOMarksAndNumbers implements Serializable {
	String SeqNumber;
	List<String> MarksAndNumbersLine = new ArrayList<String>();
	String CGO_MRK_NUM_ID;
}

class InnerPackaging implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class InnerGrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class InnerNetWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class InnerVolume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class InnerInnerPackaging implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class InnerInnerGrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class InnerInnerNetWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class InnerInnerVolume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class DGCargoSpec implements Serializable {
	String DGRegulator;
	String IMDGPage;
	String IMOClass;
	String UNNumber;
	String TechnicalName;
	String ChinaDGNumber;
	String ProperShippingName;
	PackageGroup PackageGroup = new PackageGroup();
	String MFAGNumber;
	String EMSNumber;
	String PSAClass;
	String ApprovalCode;
	GrossWeight GrossWeight = new GrossWeight();
	NetWeight NetWeight = new NetWeight();
	NetExplosiveWeight NetExplosiveWeight = new NetExplosiveWeight();
	FlashPoint FlashPoint = new FlashPoint();
	ElevatedTemperature ElevatedTemperature = new ElevatedTemperature();
	String isLimitedQuantity;
	String IsInhalationHazardous;
	String IsReportableQuantity;
	String IsEmptyUnclean;
	String isMarinePollutant;
	String State;
	List<String> Label = new ArrayList<String>();
	String Remarks;
	List<EmergencyContact> EmergencyContactLoop = new ArrayList<EmergencyContact>();
	String DG_CGO_INFO_UUID;
	String FinalProperShippingName;
	List<FreeText> FreeTextLoop = new ArrayList<FreeText>();
	List<SOEmergencyContact> SOEmergencyContactLoop = new ArrayList<SOEmergencyContact>();
}

class PackageGroup implements Serializable {
	String Code;
	InnerPackageDescription InnerPackageDescription = new InnerPackageDescription();
	OuterPackageDescription OuterPackageDescription = new OuterPackageDescription();
}

class InnerPackageDescription implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class OuterPackageDescription implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class NetExplosiveWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class FlashPoint implements Serializable {
	String Temperature;
	String TemperatureUnit;
}

class ElevatedTemperature implements Serializable {
	String Temperature;
	String TemperatureUnit;
}

class SOEmergencyContact implements Serializable {
	String FirstName;
	String LastName;
	List<ContactPhone> ContactPhoneLoop = new ArrayList<ContactPhone>();
	List<ContactFax> ContactFaxLoop = new ArrayList<ContactFax>();
	List<String> ContactEmailAddress = new ArrayList<String>();
	String Type;
	String EM_CTACT_UUID;
}

class AWCargoSpec implements Serializable {
	Height Height = new Height();
	Length Length = new Length();
	Width Width = new Width();
	GrossWeight GrossWeight = new GrossWeight();
	String IsShipsideDelivery;
	String Remarks;
	List<EmergencyContact> EmergencyContactLoop = new ArrayList<EmergencyContact>();
	String AW_CGO_INFO_UUID;
}

class Height implements Serializable {
	String Length;
	String LengthUnit;
}

class Length implements Serializable {
	String Length;
	String LengthUnit;
}

class Width implements Serializable {
	String Length;
	String LengthUnit;
}

class ContainerLoadPlan implements Serializable {
	Package Package = new Package();
	GrossWeight GrossWeight = new GrossWeight();
	Volume Volume = new Volume();
	String ContainerSequenceID;
}

class Package implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class TrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class Route implements Serializable {
	POR POR = new POR();
	FND FND = new FND();
	FirstPOL FirstPOL = new FirstPOL();
	LastPOD LastPOD = new LastPOD();
	List<OceanLeg> OceanLegLoop = new ArrayList<OceanLeg>();
}

class POR implements Serializable {
	String LocationName;
	CityDetails CityDetails = new CityDetails();
	Facility Facility = new Facility();
	CSStandardCity CSStandardCity = new CSStandardCity();
}

class CityDetails implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	LocationCode LocationCode = new LocationCode();
}

class Facility implements Serializable {
	String FacilityCode;
	String FacilityName;
}

class CSStandardCity implements Serializable {
	String CSParentCityID;
	String CSStateCode;
	String CSCountryCode;
	String CSContinentCode;
}

class FND implements Serializable {
	String LocationName;
	CityDetails CityDetails = new CityDetails();
	Facility Facility = new Facility();
	CSStandardCity CSStandardCity = new CSStandardCity();
}

class FirstPOL implements Serializable {
	Port Port = new Port();
	Facility Facility = new Facility();
	String LoadingPortVoyage;
	String LoadingPortVesselOperator;
	String LoadingPortVesselName;
}

class Port implements Serializable {
	String PortName;
	String PortCode;
	String City;
	String County;
	String State;
	LocationCode LocationCode = new LocationCode();
	String Country;
	String CSPortID;
	String CSCountryCode;
}

class LastPOD implements Serializable {
	Port Port = new Port();
	Facility Facility = new Facility();
}

class OceanLeg implements Serializable {
	String LegSeq;
	POL POL = new POL();
	POD POD = new POD();
	SVVD SVVD = new SVVD();
	ETD ETD = new ETD();
	ETA ETA = new ETA();
}

class POL implements Serializable {
	Port Port = new Port();
	Facility Facility = new Facility();
}

class POD implements Serializable {
	Port Port = new Port();
	Facility Facility = new Facility();
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
	LocDT LocDT = new LocDT();
}

class ETA implements Serializable {
	String GMT;
	LocDT LocDT = new LocDT();
}

class EDIDynamicStructure implements Serializable {
	List<NewStructure> NewStructureLoop = new ArrayList<NewStructure>();
}

class NewStructure implements Serializable {
	String ParentStructureName;
	String NewElementName;
	String NewElementValue;
}


