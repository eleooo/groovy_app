package cs.b2b.core.mapping.bean.si

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set;;

class ShippingInstruction implements Serializable {
	
	public static final Set<String> MultiElementList= ["BookingNumber", "AddressLine", "EORINumber", "Reference", "EmergencyContact", "ReeferCargoSpec", "Seal", "ExternalReference", "MarksAndNumbers", "HarmonizedTariffSchedule", "Label", "CLPInnerPack", "MarksAndNumber", "AWCargoSpec", "DGCargoSpec", "InnerPack", "ContainerLoadPlan", "FilerInfo.Code", "Party", "FilerInfo", "HouseBLFiling", "MasterBLParty", "MasterBLFiling", "DistributionDetails", "SICertClause", "RemarkLine", "NewStructure", "ChargeInformation", "Party", "Cargo", "FilingInfo", "SpecialHandling", "Container", "Body"]
	
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
	List<Party> Party = new ArrayList<Party>();
	CarrierRate CarrierRate;
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	List<Container> Container = new ArrayList<Container>();
	List<Cargo> Cargo = new ArrayList<Cargo>();
	List<FilingInfo> FilingInfo = new ArrayList<FilingInfo>();
	BLInfo BLInfo;
	Route Route;
	List<SpecialHandling> SpecialHandling = new ArrayList<SpecialHandling>();
	List<ChargeInformation> ChargeInformation = new ArrayList<ChargeInformation>();
	Remarks Remarks;
	EDIDynamicStructure EDIDynamicStructure;
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
	String CSSIReferenceNumber;
	String CustSIReferenceNumber;
	String ActionType;
	String BLNumber;
	String SCAC;
	String SPCompanyID;
	String Owner;
	String SIVersionNumber;
	String IsToOrder;
	String FMCNumber;
	List<String> BookingNumber = new ArrayList<String>();
	String ShipmentCargoType;
	ShipmentTrafficMode ShipmentTrafficMode;
	String IsOnlineBL;
	String SIRequestDescription;
	CustomerTransactionDT CustomerTransactionDT;
	String LastModifiedCSUserID;
	PlaceOfPayment PlaceOfPayment;
	TotalGrossWeight TotalGrossWeight;
	TotalConsignment TotalConsignment;
	String TotalNumberOfEquipment;
	TotalNumberOfPackage TotalNumberOfPackage;
	String ExportReferenceText;
	String DocUUID;
}

class ShipmentTrafficMode implements Serializable {
	String OutBound;
	String InBound;
	String TrafficModeDescription;
}

class CustomerTransactionDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class PlaceOfPayment implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String LocationName;
	String OfficeCode;
}

class TotalGrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class TotalConsignment implements Serializable {
	String Volume;
	String VolumeUnit;
}

class TotalNumberOfPackage implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
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
	String PartyText;
	String EDIPartyType;
	List<String> EORINumber = new ArrayList<String>();
	String AuthorizedPerson;
}

class CarrierRate implements Serializable {
	String CSCarrierRateType;
	String CarrierRateNumber;
}

class ExternalReference implements Serializable {
	String CSReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
	String EDIReferenceCode;
	String ReferenceNumberIndicator;
	String IsShowOnBL;
}

class Container implements Serializable {
	String AssociatedBookingNumber;
	String ContainerNumber;
	String DraftContainerNumber;
	String ContainerCheckDigit;
	String CarrCntrSizeType;
	String CSContainerSizeType;
	GrossWeight GrossWeight;
	NetWeight NetWeight;
	TareWeight TareWeight;
	ContainerVolume ContainerVolume;
	String Remarks;
	TrafficMode TrafficMode;
	List<Seal> Seal = new ArrayList<Seal>();
	Indicators Indicators;
	Haulage Haulage;
	String ContainerSequenceID;
	List<ReeferCargoSpec> ReeferCargoSpec = new ArrayList<ReeferCargoSpec>();
	VGMWeight VGMWeight;
	String VGMMethod;
	WeightedDate WeightedDate;
	String WeightReference;
	String WeightPartyName;
	WeightPartyContactInfo WeightPartyContactInfo;
	WeightPartyAddress WeightPartyAddress;
	String WeighingRequestIndicator;
	String ResponsiblePartyName;
	String VGMRemarks;
}

class GrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
	String WeightText;
}

class NetWeight implements Serializable {
	String Weight;
	String WeightUnit;
	String WeightText;
}

class TareWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class ContainerVolume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class TrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class Seal implements Serializable {
	String SealType;
	String SealNumber;
	String SealTypeName;
}

class Indicators implements Serializable {
	String SOCIndicator;
	String PerishableGoods;
	String DangerousGoods;
	String PersonalEffect;
	String Timber;
	String Flammable;
	String Fumigation;
	String MultipleBL;
	String LoadEmptyStatus;
	String IsPartial;
}

class Haulage implements Serializable {
	String OutBound;
	String InBound;
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
	MaxTemperature MaxTemperature;
}

class MaxTemperature implements Serializable {
	String Temperature;
	String TemperatureUnit;
}

class VGMWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class WeightedDate implements Serializable {
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

class WeightPartyAddress implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String PostalCode;
	cs.b2b.core.mapping.bean.AddressLines AddressLines;
}

class Cargo implements Serializable {
	CargoInfo CargoInfo;
	List<ReeferCargoSpec> ReeferCargoSpec = new ArrayList<ReeferCargoSpec>();
	List<DGCargoSpec> DGCargoSpec = new ArrayList<DGCargoSpec>();
	List<AWCargoSpec> AWCargoSpec = new ArrayList<AWCargoSpec>();
	List<ContainerLoadPlan> ContainerLoadPlan = new ArrayList<ContainerLoadPlan>();
	List<InnerPack> InnerPack = new ArrayList<InnerPack>();
	String InnerCommodityId;
}

class CargoInfo implements Serializable {
	String CargoNature;
	String CargoDescription;
	Packaging Packaging;
	GrossWeight GrossWeight;
	NetWeight NetWeight;
	Volume Volume;
	List<MarksAndNumbers> MarksAndNumbers = new ArrayList<MarksAndNumbers>();
	String CargoRemarks;
	List<HarmonizedTariffSchedule> HarmonizedTariffSchedule = new ArrayList<HarmonizedTariffSchedule>();
	String AssociatedBookingNumber;
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	String CommodityGrouping;
}

class Packaging implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class Volume implements Serializable {
	String Volume;
	String VolumeUnit;
	String VolumeText;
}

class MarksAndNumbers implements Serializable {
	String SeqNumber;
	String MarksAndNumbersLine;
	String CGO_MRK_NUM_ID;
}

class HarmonizedTariffSchedule implements Serializable {
	String SeqNumber;
	String HarmonizedCode;
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
	String HazardousClassificationCode;
	String HazardousReference;
	String MaterialDescription;
	String MaterialClassification;
	String EmergencyScheduleReference;
	String EmergencyResponseCode;
	String EmergencySchedulePageNumber;
	String LimitedQuantityDeclaration;
	String MarinePollutantInformation;
	String MedicalFirstAidGuidePageNumber;
	String IsResidue;
	String IsShipsideDelivery;
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
}

class ContainerLoadPlan implements Serializable {
	Package Package;
	GrossWeight GrossWeight;
	NetWeight NetWeight;
	Volume Volume;
	List<MarksAndNumber> MarksAndNumber = new ArrayList<MarksAndNumber>();
	String ContainerSequenceID;
	List<CLPInnerPack> CLPInnerPack = new ArrayList<CLPInnerPack>();
}

class Package implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class MarksAndNumber implements Serializable {
	String SeqNumber;
	String MarksAndNumbersLine;
	String CGO_MRK_NUM_ID;
}

class CLPInnerPack implements Serializable {
	InnerPackage InnerPackage;
	String InnerPackLevel;
	String InnerPackSequence;
	String AssociatedInnerCommodityId;
}

class InnerPackage implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class InnerPack implements Serializable {
	InnerPackage InnerPackage;
	String InnerPackLevel;
	String InnerPackSequence;
	String AssociatedInnerCommodityId;
}

class FilingInfo implements Serializable {
	List<cs.b2b.core.mapping.bean.MasterBLFiling> MasterBLFiling = new ArrayList<cs.b2b.core.mapping.bean.MasterBLFiling>();
	List<cs.b2b.core.mapping.bean.MasterBLParty> MasterBLParty = new ArrayList<cs.b2b.core.mapping.bean.MasterBLParty>();
	List<cs.b2b.core.mapping.bean.HouseBLFiling> HouseBLFiling = new ArrayList<cs.b2b.core.mapping.bean.HouseBLFiling>();
}

class BLInfo implements Serializable {
	String BLNumber;
	OnBoardDT OnBoardDT;
	IssuranceOfBLDT IssuranceOfBLDT;
	String PaymentOptionForOceanFreight;
	String IsDraftRequired;
	DraftBLDistribution DraftBLDistribution;
	FinalCopyBLDistribution FinalCopyBLDistribution;
	String SummaryBLCargoDesc;
	String BLDefaultCurrencyCode;
	String DeclaredValueOfGoods;
	BLReleaseOffice BLReleaseOffice;
	List<SICertClause> SICertClause = new ArrayList<SICertClause>();
	String RequestedBLDateType;
	OriginOfGoods OriginOfGoods;
	String BLRemarks;
	String OtherInformation;
	String BLCaptureOffice;
	String ThroughRateInd;
	String LoadThroughRateInd;
	String DischargingThroughRateInd;
	String MeasurementInd;
	String PLCIssue;
	String TotalPrepaidAmount;
	String TotalCollectAmount;
	String BLType;
	String BLFreightType;
}

class OnBoardDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
	String OnBoardDTType;
}

class IssuranceOfBLDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class DraftBLDistribution implements Serializable {
	String BLDistributedRole;
	String TransmissionMode;
	String TransmissionModeName;
	Fax Fax;
}

class Fax implements Serializable {
	String CountryCode;
	String AreaCode;
	String Number;
}

class FinalCopyBLDistribution implements Serializable {
	List<DistributionDetails> DistributionDetails = new ArrayList<DistributionDetails>();
	String OtherHandlingInstruction;
	String BLTransmissionMode;
	String BLOtherTransmissionInfo;
}

class DistributionDetails implements Serializable {
	String DocumentType;
	String FreightType;
	String NumberOfCopies;
	cs.b2b.core.mapping.bean.Fax Fax;
	String TransmissionMode;
	String OtherTransmissionInfo;
	String BLDistributedRole;
}

class BLReleaseOffice implements Serializable {
	String CountryCode;
	String City;
	String County;
	String State;
	String Country;
	String LocationName;
	LocationCode LocationCode;
	String OfficeCode;
}

class LocationCode implements Serializable {
	String MutuallyDefinedCode;
	String UNLocationCode;
	String SchedKDType;
	String SchedKDCode;
}

class SICertClause implements Serializable {
	String CertificationClauseType;
	String CertificationClauseText;
}

class OriginOfGoods implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String LocationName;
	String OriginText;
}

class Route implements Serializable {
	POR POR;
	FND FND;
	FirstPOL FirstPOL;
	LastPOD LastPOD;
	SVVD SVVD;
	String PreCarriage;
}

class POR implements Serializable {
	String LocationName;
	cs.b2b.core.mapping.bean.CityDetails CityDetails;
	cs.b2b.core.mapping.bean.Facility Facility;
	cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity;
	String BLText;
}

class FND implements Serializable {
	String LocationName;
	cs.b2b.core.mapping.bean.CityDetails CityDetails;
	cs.b2b.core.mapping.bean.Facility Facility;
	cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity;
	String BLText;
}

class FirstPOL implements Serializable {
	Port Port;
	Facility Facility;
	String BLText;
}

class Port implements Serializable {
	String PortName;
	String PortCode;
	String City;
	String County;
	String State;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String Country;
	String CSPortID;
	String CSCountryCode;
}

class Facility implements Serializable {
	String FacilityCode;
	String FacilityName;
}

class LastPOD implements Serializable {
	Port Port;
	Facility Facility;
	String BLText;
}

class SVVD implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String CallNumber;
	String VesselNationality;
	String ExternalVesselType;
	String ExternalVesselCode;
	String ExternalVesselNumber;
	String VesselVoyageText;
	String VoyageReference;
	String VesselFlag;
	String VesselOperator;
}

class SpecialHandling implements Serializable {
	String Code;
	String Description;
}

class ChargeInformation implements Serializable {
	ChargeDetails ChargeDetails;
	PayableBy PayableBy;
}

class ChargeDetails implements Serializable {
	String ChargeCode;
	String ChargeCodeDescription;
	String ChargeType;
	String ChargeCategory;
	String ChargeItem;
	String ChargeItemDescription;
	String PayableAt;
	String PrepaidIndicator;
	String PrepaidBy;
	String PrepaidAt;
	String PrepaidCurrency;
	String PrepaidAmount;
	String CollectIndicator;
	String CollectBy;
	String CollectAt;
	String CollectCurrency;
	String CollectAmount;
	String FreightIndicator;
	String MautifactorRateDescription;
	String I_ECharge;
	String ChargeRatingMethod;
	String ChargeBasisIndicator;
	String ChargeBasis;
	String ChargeRate;
	String CurrencyCode;
	String CalculationMethod;
	String ExchangeRate;
	String ExchangeCurrency;
}

class PayableBy implements Serializable {
	String PartyType;
	String PartyName;
	String CSCompanyID;
	String CarrierCustomerCode;
	String isNeedReplyPartyEmail;
	cs.b2b.core.mapping.bean.Contact Contact;
	cs.b2b.core.mapping.bean.Address Address;
	List<cs.b2b.core.mapping.bean.Reference> Reference = new ArrayList<cs.b2b.core.mapping.bean.Reference>();
}

class Remarks implements Serializable {
	List<String> RemarkLine = new ArrayList<String>();
}

class EDIDynamicStructure implements Serializable {
	List<NewStructure> NewStructure = new ArrayList<NewStructure>();
}

class NewStructure implements Serializable {
	String ParentStructureName;
	String NewElementName;
	String NewElementValue;
}

