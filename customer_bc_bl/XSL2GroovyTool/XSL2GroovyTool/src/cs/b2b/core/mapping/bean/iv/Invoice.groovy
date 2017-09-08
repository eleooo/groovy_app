package cs.b2b.core.mapping.bean.iv

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set;;

class Invoice implements Serializable {
	
	public static final Set<String> MultiElementList = ['BookingInformation', 'AddressLine', 'MailAddressLines', 'BankRemittanceLines', 'ReferenceInformation.CargoDescription', 'ContainerInfo', 'InvoiceDetail.BLNumber', 'BLInformation', 'Label', 'ChargeRemarks', 'ContainerInformation', 'Reference', 'Container.Seal', 'RFPackageUnit', 'EmergencyContact', 'AWPackageUnit', 'Label', 'DGPackageUnit', 'ReeferCargoSpec', 'GCPackageUnit', 'DGCargoSpec', 'AWCargoSpec', 'ExternalReference', 'MarksAndNumbers', 'Leg.DepartureDT', 'Leg.ArrivalDT', 'Leg', 'ArrivalAtFinalHub', 'ContainerNumbers', 'FullReturn', 'Party', 'BLStatus', 'CarrierRate', 'Cargo', 'Container', 'DnD', 'InvoiceEntry', 'BillOfLadingInfo', 'AdjustmentEntry', 'Body']
	
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
	EventInformation EventInformation;
	TransactionInformation TransactionInformation;
	GeneralInformation GeneralInformation;
	InvoiceDetail InvoiceDetail;
	List<InvoiceEntry> InvoiceEntry = new ArrayList<InvoiceEntry>();
	List<AdjustmentEntry> AdjustmentEntry = new ArrayList<AdjustmentEntry>();
	List<BillOfLadingInfo> BillOfLadingInfo = new ArrayList<BillOfLadingInfo>();
}

class EventInformation implements Serializable {
	String EventCode;
	String EventDescription;
	cs.b2b.core.mapping.bean.EventDT EventDT;
}

class TransactionInformation implements Serializable {
	String MessageID;
	String GroupControlNumber;
	String InterchangeTransactionID;
}

class GeneralInformation implements Serializable {
	String SPCompanyID;
	String SCAC;
	String ActionType;
}

class InvoiceDetail implements Serializable {
	String InvoiceReferenceCode;
	List<String> BLNumber = new ArrayList<String>();
	List<BLInformation> BLInformation = new ArrayList<BLInformation>();
	List<Label> Label = new ArrayList<Label>();
	String CollectionOfficeCode;
	String ControlOfficeCode;
	String IssueOffice;
	String OriginalInvoiceUploadIndicator;
	String CopyInvoiceUploadIndicator;
	String IsNonCertifiedCopyUploaded;
	String ShowIndicator;
	String PaperSize;
	String IsCombinedInvoice;
	PDFUploadDate PDFUploadDate;
	String CarrierPDFUploadRemarks;
	NumberOfPrint NumberOfPrint;
	String CargoSmartPrintStatus;
	String HasChangeRequest;
	String InvoiceExternalNumber;
	String invoiceType;
	String InvoicePayerName;
	InvoiceDate InvoiceDate;
	TotalAmount TotalAmount;
	String CurrentStatus;
	PayerAddress PayerAddress;
	PayeeAddress PayeeAddress;
	String PayerVATID;
	String PayeeVATID;
	String PayerCarrierCustomerCode;
	String IsAdjustmentNote;
	String PaymentDueDate;
	String CurrentDueReason;
	String LastPaymentDate;
	SVVD SVVD;
	String ShipmentBound;
	String Remarks;
	String ShipperConsigneeReference;
	String InvoiceCustomerRefNumber;
	PaymentMailAddress PaymentMailAddress;
	BankRemittance BankRemittance;
	String ArrivedDeparted;
	ReferenceInformation ReferenceInformation;
	String IsSupplemental;
	String SpecialRemark;
}

class BLInformation implements Serializable {
	String BLNumber;
	List<BookingInformation> BookingInformation = new ArrayList<BookingInformation>();
}

class BookingInformation implements Serializable {
	String BookingNumber;
	String BookingPartyEmail;
}

class Label implements Serializable {
	String LabelName;
	LabelDate LabelDate;
}

class LabelDate implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class PDFUploadDate implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class NumberOfPrint implements Serializable {
	String AllowOriginalCount;
	String AllowCopyCount;
	String OriginalPrintedCount;
	String CopyPrintedCount;
}

class InvoiceDate implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class TotalAmount implements Serializable {
	String attr_ExchangeRate;
 	String attr_InvoiceCurrency;
 	String attr_LocalCurrency;
 	String TotalLocalAmount;
	String OutstandingLocalAmount;
	String TotalInvoiceAmount;
	String OutstandingInvoiceAmount;
	String TotalLocalPaidAmount;
	String TotalInvoicePaidAmount;
	String TotalLocalAdjustmentAmount;
	String TotalInvoiceAdjustmentAmount;
	String TotalLocalVATAmount;
	String TotalInvoiceVATAmount;
	String TotalLocalBeforeVATAmount;
	String TotalInvoiceBeforeVATAmount;
}

class PayerAddress implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String PostalCode;
	cs.b2b.core.mapping.bean.AddressLines AddressLines;
}

class PayeeAddress implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String PostalCode;
	cs.b2b.core.mapping.bean.AddressLines AddressLines;
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
	String VesselNationality;
}

class PaymentMailAddress implements Serializable {
	List<String> MailAddressLines = new ArrayList<String>();
}

class BankRemittance implements Serializable {
	List<String> BankRemittanceLines = new ArrayList<String>();
}

class ReferenceInformation implements Serializable {
	List<ContainerInfo> ContainerInfo = new ArrayList<ContainerInfo>();
	List<String> CargoDescription = new ArrayList<String>();
	TotalWeight TotalWeight;
	TotalVolume TotalVolume;
}

class ContainerInfo implements Serializable {
	String ContainerNumber;
	String CheckDigit;
	String CarrierContainerSizeType;
}

class TotalWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class TotalVolume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class InvoiceEntry implements Serializable {
	String ChargeCode;
	String ChargeDescription;
	String ChargePrintLabel;
	ItemAmount_InvoiceEntry ItemAmount;
	String IsPrepaid;
	String BLNumber;
	String CalculationMethod;
	SVVD SVVD;
	POR_InvoiceEntry POR;
	FND_InvoiceEntry FND;
	String POL;
	String POD;
	String ShipperConsigneeReference;
	String ChargeFromDate;
	String ChargeToDate;
	List<String> ChargeRemarks = new ArrayList<String>();
	String ShipmentBound;
	List<ContainerInformation> ContainerInformation = new ArrayList<ContainerInformation>();
	String ExtendedValue;
}

class ItemAmount_AdjustmentEntry implements Serializable {
	String attr_AdjustmentCurrency;
	String attr_ChargeCurrency;
 	String attr_ExchangeRate;
 	String attr_VoyageExchangeCurrency;
 	String attr_VoyageExchangeRate;
 	String ChargeBasis;
	String ChargeRate;
	String AdjustmentAmount;
	String LocalAdjustmentAmount;
	String InvoiceAdjustmentAmount;
	String TotalLocalAmount;
	String TotalInvoiceAmount;
	String VATRate;
	String VATAmount;
	String VATLocalAmount;
	String VATInvoiceAmount;
}


class ItemAmount_InvoiceEntry implements Serializable {
	String attr_ChargeCurrency;
	String attr_ExchangeRate;
	String attr_VoyageExchangeCurrency;
	String attr_VoyageExchangeRate;
	String ChargeBasis;
	String ChargeRate;
	String ChargeAmount;
	String LocalChargeAmount;
	String InvoiceChargeAmount;
	String TotalLocalAmount;
	String TotalInvoiceAmount;
	String VATRate;
	String VATAmount;
	String VATLocalAmount;
	String VATInvoiceAmount;
}
class POR_Route implements Serializable {
	String LocationName;
	CityDetails CityDetails;
	cs.b2b.core.mapping.bean.Facility Facility;
	cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity;
}

class POR_InvoiceEntry implements Serializable {
	String LocationName;
	CityDetails CityDetails;
}

class CityDetails implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
}

class FND_Route implements Serializable {
	String LocationName;
	CityDetails CityDetails;
	cs.b2b.core.mapping.bean.Facility Facility;
	cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity;
}

class FND_InvoiceEntry implements Serializable {
	String LocationName;
	CityDetails CityDetails;
}

class ContainerInformation implements Serializable {
	String ContainerNumber;
	String ContainerCheckDigit;
}

class AdjustmentEntry implements Serializable {
	String ChargeCode;
	String ChargeDescription;
	String ChargePrintLabel;
	ItemAmount_AdjustmentEntry ItemAmount;
	String IsUnbill;
	String IsPrepaid;
	String BLNumber;
	List<ContainerInformation> ContainerInformation = new ArrayList<ContainerInformation>();
	String PayerName;
	PayerAddress PayerAddress;
	String CollectionOfficeCode;
	String ControlOfficeCode;
	String InvoiceReferenceCode;
	String CalculationMethod;
	SVVD SVVD;
	POR_InvoiceEntry POR;
	FND_InvoiceEntry FND;
	String POL;
	String POD;
	String ShipperConsigneeReference;
	String ShipmentBound;
	String ChargeCreationDatetime;
	String ExtendedValue;
}

class BillOfLadingInfo implements Serializable {
	String BillOfLadingNumber;
	List<CarrierRate> CarrierRate = new ArrayList<CarrierRate>();
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	List<BLStatus> BLStatus = new ArrayList<BLStatus>();
	List<Party> Party = new ArrayList<Party>();
	List<Container> Container = new ArrayList<Container>();
	List<Cargo> Cargo = new ArrayList<Cargo>();
	Route Route;
	List<DnD> DnD = new ArrayList<DnD>();
	ContainerFlowInstruction ContainerFlowInstruction;
	BLOnboardDT BLOnboardDT;
}

class CarrierRate implements Serializable {
	String CSCarrierRateType;
	String CarrierRateNumber;
}

class ExternalReference implements Serializable {
	String CSReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
}

class BLStatus implements Serializable {
	String attr_Type;
 	String Status;
	StatusDT StatusDT;
}

class StatusDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
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
}

class Container implements Serializable {
	String ContainerNumber;
	String ContainerCheckDigit;
	String CSContainerSizeType;
	String CarrCntrSizeType;
	String IsSOC;
	cs.b2b.core.mapping.bean.GrossWeight GrossWeight;
	List<cs.b2b.core.mapping.bean.Seal> Seal = new ArrayList<cs.b2b.core.mapping.bean.Seal>();
	cs.b2b.core.mapping.bean.Haulage Haulage;
	cs.b2b.core.mapping.bean.TrafficMode TrafficMode;
	String DisplaySequenceNumber;
	CustomsClearanceDT CustomsClearanceDT;
	String CustomsClearanceLocCity;
	String CustomsClearanceLocCode;
	String CustomsClearanceLocType;
	String CustomsClearanceLocDesc;
	String CustomsRefType;
	String CustomsRefNumber;
	CustomsRefDT CustomsRefDT;
	PieceCount PieceCount;
	String InlandMoveIDPrefix;
	String SIContainerNumber;
	String SIContainerCheckDigit;
	SIDeclaredGrossWeight SIDeclaredGrossWeight;
	String LastTransferFromBookingNo;
	String LastTransferToBookingNo;
	BLSeal BLSeal;
	String RepackContainerNumber;
	String CS1ContainerSizeType;
	Volume Volume;
}

class CustomsClearanceDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class CustomsRefDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class PieceCount implements Serializable {
	String PieceCount;
	String PieceCountUnit;
}

class SIDeclaredGrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class BLSeal implements Serializable {
	String SealType;
	String SealNumber;
}

class Volume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class Cargo implements Serializable {
	String CargoNature;
	String CargoDescription;
	cs.b2b.core.mapping.bean.Packaging Packaging;
	cs.b2b.core.mapping.bean.GrossWeight GrossWeight;
	cs.b2b.core.mapping.bean.NetWeight NetWeight;
	cs.b2b.core.mapping.bean.Volume Volume;
	List<cs.b2b.core.mapping.bean.MarksAndNumbers> MarksAndNumbers = new ArrayList<cs.b2b.core.mapping.bean.MarksAndNumbers>();
	String DisplaySequenceNumber;
	String ContainerNumber;
	String ContainerCheckDigit;
	String CurrentContainerNumber;
	String CurrentContainerCheckDigit;
	TrafficModel TrafficModel;
	String CgoPkgName;
	String CgoPkgCodeCount;
	String CustomsRefNumber;
	String CustomsRefType;
	CustomsRefDT CustomsRefDT;
	String CustomsClearanceLocCode;
	String CustomsClearanceLocCity;
	String CustomsClearanceLocType;
	String CustomsClearanceLocDesc;
	Seal Seal;
	String ArticleNumber;
	String CommodityDescription;
	String BLCargoDescription;
	List<GCPackageUnit> GCPackageUnit = new ArrayList<GCPackageUnit>();
	List<ReeferCargoSpec> ReeferCargoSpec = new ArrayList<ReeferCargoSpec>();
	List<AWCargoSpec> AWCargoSpec = new ArrayList<AWCargoSpec>();
	List<DGCargoSpec> DGCargoSpec = new ArrayList<DGCargoSpec>();
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
}

class TrafficModel implements Serializable {
	String OutBound;
	String InBound;
}

class Seal implements Serializable {
	String SealType;
	String SealNumber;
}

class GCPackageUnit implements Serializable {
	String PackageSeqNumber;
	String PackageUnitQuantity;
	String PackageUnitDescription;
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
	List<RFPackageUnit> RFPackageUnit = new ArrayList<RFPackageUnit>();
}

class RFPackageUnit implements Serializable {
	String PackageSeqNumber;
	String PackageUnitQuantity;
	String PackageUnitDescription;
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
	List<AWPackageUnit> AWPackageUnit = new ArrayList<AWPackageUnit>();
}

class AWPackageUnit implements Serializable {
	String PackageSeqNumber;
	String PackageUnitQuantity;
	String PackageUnitDescription;
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
	List<DGPackageUnit> DGPackageUnit = new ArrayList<DGPackageUnit>();
}

class DGPackageUnit implements Serializable {
	String PackageSeqNumber;
	String PackageUnitQuantity;
	String PackageUnitDescription;
}

class Route implements Serializable {
	Haulage Haulage;
	String IsOBDropAndPoll;
	List<ArrivalAtFinalHub> ArrivalAtFinalHub = new ArrayList<ArrivalAtFinalHub>();
	FullReturnCutoffDT FullReturnCutoffDT;
	DepartureDT DepartureDT;
	ArrivalDT ArrivalDT;
	POR_Route POR;
	FND_Route FND;
	FirstPOL FirstPOL;
	LastPOD LastPOD;
	List<Leg> Leg = new ArrayList<Leg>();
}

class Haulage implements Serializable {
	String OutBound;
	String InBound;
}

class ArrivalAtFinalHub implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class FullReturnCutoffDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class DepartureDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ArrivalDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class FirstPOL implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
	OutboundSVVD OutboundSVVD;
	DepartureDT DepartureDT;
	String CSStateCode;
	String CSParentCityID;
}

class OutboundSVVD implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String CallNumber;
	String VesselNationality;
	String DirectionName;
	String VesselNationalityCode;
}

class LastPOD implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
	InboundSVVD InboundSVVD;
	ArrivalDT ArrivalDT;
	String CSStateCode;
	String CSParentCityID;
}

class InboundSVVD implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String CallNumber;
	String VesselNationality;
	String DirectionName;
	String VesselNationalityCode;
}

class Leg implements Serializable {
	String LegSeq;
	cs.b2b.core.mapping.bean.POL POL;
	cs.b2b.core.mapping.bean.POD POD;
	cs.b2b.core.mapping.bean.SVVD SVVD;
	List<cs.b2b.core.mapping.bean.DepartureDT> DepartureDT = new ArrayList<cs.b2b.core.mapping.bean.DepartureDT>();
	List<cs.b2b.core.mapping.bean.ArrivalDT> ArrivalDT = new ArrayList<cs.b2b.core.mapping.bean.ArrivalDT>();
}

class DnD implements Serializable {
	String attr_Type;
 	String ContainerNumber;
	String ContainerCheckDigit;
	FreeTimeStartDT FreeTimeStartDT;
	FreeTimeEndDT FreeTimeEndDT;
	String FreeTime;
	String FreeTimeType;
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

class ContainerFlowInstruction implements Serializable {
	List<FullReturn> FullReturn = new ArrayList<FullReturn>();
}

class FullReturn implements Serializable {
	String NumberOfContainers;
	String ISOSizeType;
	String CSContainerSizeType;
	List<ContainerNumbers> ContainerNumbers = new ArrayList<ContainerNumbers>();
	Facility Facility;
	MvmtDT MvmtDT;
	Address Address;
	Contact Contact;
}

class ContainerNumbers implements Serializable {
	String ContainerNumber;
	String CheckDigit;
}

class Facility implements Serializable {
	String FacilityCode;
	String FacilityName;
}

class MvmtDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class Address implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String PostalCode;
	cs.b2b.core.mapping.bean.AddressLines AddressLines;
}

class Contact implements Serializable {
	String FirstName;
	String LastName;
	cs.b2b.core.mapping.bean.ContactPhone ContactPhone;
	cs.b2b.core.mapping.bean.ContactFax ContactFax;
	String ContactEmailAddress;
}

class BLOnboardDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

