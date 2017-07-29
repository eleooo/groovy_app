package cs.b2b.core.mapping.bean.bl;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.LocDTAdapter;;;

/**
 * @author RENGA
 * @cs2xmlVersion 67.3
 */
class BillOfLading implements Serializable {

    public static final Set<String> MultiElementList=["CarrierBookingNumber", "BookingOfficeUNLoc", "BookingOffice", "BookingOfficeName", "LabelActions", "AddressLine", "Reference", "EORINumber", "OceanLeg.DepartureDT", "OceanLeg.ArrivalDT", "ArrivalAtFND", "StopOff", "ArrivalAtFinalHub", "OceanLeg", "Container.Seal", "ContainerNumbers", "EmptyReturn", "EmergencyContact", "RFPackageUnit", "AWPackageUnit", "DGPackageUnit", "DGCargoSpec.Label", "GCPackageUnit", "HarmonizedTariffSchedule", "MarksAndNumbers", "AWCargoSpec", "ReeferCargoSpec", "ExternalReference", "DGCargoSpec", "TotalAmtInAdditionalPmtCurrency", "Appointment", "Cargo", "FreightChargeCNTR", "BLStatus", "BLCertClause", "FreightCharge", "Body.Container", "Party", "RailInformation", "CarrierRate", "DnD", "Body", "AssociatedExternalReference", "AssociatedParty"]

	Header Header = new Header();
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
	List<BLStatus> BLStatus = new ArrayList<BLStatus>();
	List<Party> Party = new ArrayList<Party>();
	List<CarrierRate> CarrierRate = new ArrayList<CarrierRate>();
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	Route Route;
	List<Container> Container = new ArrayList<Container>();
	ContainerFlowInstruction ContainerFlowInstruction;
	List<Cargo> Cargo = new ArrayList<Cargo>();
	List<Appointment> Appointment = new ArrayList<Appointment>();
	List<BLCertClause> BLCertClause = new ArrayList<BLCertClause>();
	List<DnD> DnD = new ArrayList<DnD>();
	List<FreightCharge> FreightCharge = new ArrayList<FreightCharge>();
	List<FreightChargeCNTR> FreightChargeCNTR = new ArrayList<FreightChargeCNTR>();
	List<RailInformation> RailInformation = new ArrayList<RailInformation>();
	String Remarks;
	List<AssociatedExternalReference> AssociatedExternalReference = new ArrayList<AssociatedExternalReference>();
	List<AssociatedParty> AssociatedParty = new ArrayList<AssociatedParty>();
}

class TransactionInformation implements Serializable {
	String MessageID;
	String GroupControlNumber;
	String InterchangeTransactionID;
	String Action;
}

class EventInformation implements Serializable {
	String EventCode;
	String EventDescription;
	cs.b2b.core.mapping.bean.EventDT EventDT;
}

class GeneralInformation implements Serializable {
	String SPCompanyID;
	String SCACCode;
	String BLNumber;
	String BLType;
	String ShipmentCargoType;
	TrafficMode TrafficMode;
	String CustomsClearanceLoc;
	CustomsClearanceDT CustomsClearanceDT;
	String CargoControlOffice;
	String ContactOfficeCode;
	BLReceiptDT BLReceiptDT;
	BLIssueDT BLIssueDT;
	BLCreationDT BLCreationDT;
	BLChangeDT BLChangeDT;
	BLOnboardDT BLOnboardDT;
	String OriginalBLReleaseOffice;
	String BLPaymentOffice;
	String BLPaymentOfficeCode;
	String BLPaymentOfficeUNLoc;
	String CaptureOffice;
	String CaptureOfficeName;
	String CaptureOfficeUNLoc;
	String CaptureOfficePhoneNumber;
	PaymentReceiptDT PaymentReceiptDT;
	String OriginOfGoods;
	List<String> CarrierBookingNumber = new ArrayList<String>();
	String Role;
	String SharedBy;
	BLGrossWeight BLGrossWeight;
	BLNetWeight BLNetWeight;
	BLVolume BLVolume;
	BLSVVD BLSVVD;
	OutBoundROESVVD OutBoundROESVVD;
	InBoundROESVVD InBoundROESVVD;
	SIContactPerson SIContactPerson;
	String TradeCode;
	List<String> BookingOffice = new ArrayList<String>();
	List<String> BookingOfficeName = new ArrayList<String>();
	List<String> BookingOfficeUNLoc = new ArrayList<String>();
	String MovementRefNumber;
	String IsCSCustomer;
	List<LabelActions> LabelActions = new ArrayList<LabelActions>();
	String PartnerID;
	String SubSCACCode;
}

class TrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class CustomsClearanceDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class BLReceiptDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class BLIssueDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class BLCreationDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class BLChangeDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class BLOnboardDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class PaymentReceiptDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class BLGrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class BLNetWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class BLVolume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class BLSVVD implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String ExtVoyage;
}

class OutBoundROESVVD implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String ROEPortOfLoad;
}

class InBoundROESVVD implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String ROEPortOfDischarge;
}

class SIContactPerson implements Serializable {
	String FirstName;
	String LastName;
	cs.b2b.core.mapping.bean.ContactPhone ContactPhone;
	cs.b2b.core.mapping.bean.ContactFax ContactFax;
	String ContactEmailAddress;
}

class LabelActions implements Serializable {
	String Seq;
	String Label;
	String ProposedAction;
	String ActionType;
	TimeOfIssue TimeOfIssue;
}

class TimeOfIssue implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
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
	String PartyText;
	List<String> EORINumber = new ArrayList<String>();
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

class Route implements Serializable {
	Haulage Haulage;
	String IsOBDropAndPoll;
	List<ArrivalAtFinalHub> ArrivalAtFinalHub = new ArrayList<ArrivalAtFinalHub>();
	List<ArrivalAtFND> ArrivalAtFND = new ArrayList<ArrivalAtFND>();
	FullReturnCutoffDT FullReturnCutoffDT;
	DepartureDT DepartureDT;
	ArrivalDT ArrivalDT;
	POR POR;
	FND FND;
	FirstPOL FirstPOL;
	LastPOD LastPOD;
	List<OceanLeg> OceanLeg = new ArrayList<OceanLeg>();
	List<StopOff> StopOff = new ArrayList<StopOff>();
	ActualCargoReceiptDT ActualCargoReceiptDT;
	String CSS_BL_RTE_INFO_UUID;
	String Inbound_intermodal_indicator;
	String Outbound_intermodal_indicator;
	String InboundFirmsCode;
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

class ArrivalAtFND implements Serializable {
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

class OceanLeg implements Serializable {
	String LegSeq;
	cs.b2b.core.mapping.bean.POL POL;
	cs.b2b.core.mapping.bean.POD POD;
	cs.b2b.core.mapping.bean.SVVD SVVD;
	List<cs.b2b.core.mapping.bean.DepartureDT> DepartureDT = new ArrayList<cs.b2b.core.mapping.bean.DepartureDT>();
	List<cs.b2b.core.mapping.bean.ArrivalDT> ArrivalDT = new ArrayList<cs.b2b.core.mapping.bean.ArrivalDT>();
	CarrierExtractDT CarrierExtractDT;
	String LoadingDirectionName;
	String LoadingVesselNationalityCode;
	String DischargeDirectionName;
	String DischargeVesselNationalityCode;
	String POLCSStateCode;
	String PODCSStateCode;
	String POLCSParentCityID;
	String PODCSParentCityID;
	String LoadingExtVoyage;
	String DischargeExtVoyage;
}

class CarrierExtractDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class StopOff implements Serializable {
	String SequenceNumber;
	cs.b2b.core.mapping.bean.PickupDetails PickupDetails;
	cs.b2b.core.mapping.bean.ReturnDetails ReturnDetails;
	String STOPOFF_LEG_UUID;
}

class ActualCargoReceiptDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
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
	String CSS_BL_CNTR_UUID;
	String OOCLCntrSizeType;
	String CPRSWayBillNumber;
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

class ContainerFlowInstruction implements Serializable {
	List<EmptyReturn> EmptyReturn = new ArrayList<EmptyReturn>();
}

class EmptyReturn implements Serializable {
	String NumberOfContainers;
	String ISOSizeType;
	String CSContainerSizeType;
	List<ContainerNumbers> ContainerNumbers = new ArrayList<ContainerNumbers>();
	Facility Facility;
	MvmtDT MvmtDT;
	Address Address;
	Contact Contact;
	String CSS_BL_RTE_INFO_UUID;
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
	List<String> HarmonizedTariffSchedule = new ArrayList<String>();
	String CSS_BL_CGO_UUID;
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

class Appointment implements Serializable {
	cs.b2b.core.mapping.bean.AppointmentDT AppointmentDT;
	cs.b2b.core.mapping.bean.Address Address;
	String Company;
	cs.b2b.core.mapping.bean.Contact Contact;
	String Sequence;
	String Type;
	cs.b2b.core.mapping.bean.ActualArrivalDT ActualArrivalDT;
	cs.b2b.core.mapping.bean.ActualDepartureDT ActualDepartureDT;
	String TransportLegCarrier;
	cs.b2b.core.mapping.bean.Container Container;
	String CSS_RTE_INFO_UUID;
}

class BLCertClause implements Serializable {
	String CertificationClauseType;
	String CertificationClauseText;
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

class FreightCharge implements Serializable {
	String DisplaySeqNumber;
	String ChargeType;
	String UnBill;
	String PayableElseWhere;
	String ChargeCode;
	String ChargePrintLable;
	String Basis;
	String FreightRate;
	String CalculateMethod;
	ChargeAmount ChargeAmount;
	BillingAmount BillingAmount;
	String ChargeNetAmount;
	String PaymentCurrency;
	String InvoiceNumber;
	String ControlOfficeCode;
	String CollectionOfficeCode;
	TotalAmount TotalAmount;
	TotalAmtInPmtCurrency TotalAmtInPmtCurrency;
	SVVD SVVD;
	String ExchRateToEurope;
	String OBCustomsUNVoyRef;
	String IBCustomsUNVoyRef;
	String OBVoyRef;
	String IBVoyRef;
	String IsApprovedForCustomer;
	String ChargeDesc;
	PayByInformation PayByInformation;
	String RatePercentage;
	List<TotalAmtInAdditionalPmtCurrency> TotalAmtInAdditionalPmtCurrency = new ArrayList<TotalAmtInAdditionalPmtCurrency>();
}

@JsonAdapter(ChargeAmountAdapter.class)
class ChargeAmount implements Serializable {
	String attr_Currency;
 	String ChargeAmount;

	@Override
	public String toString() {
		return this.ChargeAmount;
	}
}

@JsonAdapter(BillingAmountAdapter.class)
class BillingAmount implements Serializable {
	String attr_Currency;
 	String BillingAmount;

	@Override
	public String toString() {
		return this.BillingAmount;
	}
}

@JsonAdapter(TotalAmountAdapter.class)
class TotalAmount implements Serializable {
	String attr_Currency;
 	String TotalAmount;

	@Override
	public String toString() {
		return this.TotalAmount;
	}
}

@JsonAdapter(TotalAmtInPmtCurrencyAdapter.class)
class TotalAmtInPmtCurrency implements Serializable {
	String attr_Currency;
 	String attr_ExchangeRate;
 	String TotalAmtInPmtCurrency;

	@Override
	public String toString() {
		return this.TotalAmtInPmtCurrency;
	}
}

@JsonAdapter(TotalAmtInAdditionalPmtCurrencyAdapter.class)
class TotalAmtInAdditionalPmtCurrency implements Serializable {
	String attr_Currency;
 	String attr_ExchangeRate;
 	String TotalAmtInAdditionalPmtCurrency;

	@Override
	public String toString() {
		return this.TotalAmtInAdditionalPmtCurrency;
	}
}

class SVVD implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
}

class PayByInformation implements Serializable {
	String PayerName;
	String CarrierCustomerCode;
	CityDetails CityDetails;
}

class CityDetails implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
}

class FreightChargeCNTR implements Serializable {
	String DisplaySeqNumber;
	String ChargeType;
	String UnBill;
	String PayableElseWhere;
	String ChargeCode;
	String ChargePrintLable;
	String Basis;
	String FreightRate;
	String CalculateMethod;
	ChargeAmount ChargeAmount;
	BillingAmount BillingAmount;
	String PaymentCurrency;
	String InvoiceNumber;
	String ControlOfficeCode;
	String CollectionOfficeCode;
	TotalAmount TotalAmount;
	TotalAmtInPmtCurrency TotalAmtInPmtCurrency;
	SVVD SVVD;
	String ExchRateToEurope;
	String OBCustomsUNVoyRef;
	String IBCustomsUNVoyRef;
	String OBVoyRef;
	String IBVoyRef;
	String IsApprovedForCustomer;
	String ChargeDesc;
	PayByInformation PayByInformation;
	String RatePercentage;
	List<TotalAmtInAdditionalPmtCurrency> TotalAmtInAdditionalPmtCurrency = new ArrayList<TotalAmtInAdditionalPmtCurrency>();
	String ContainerNumber;
	String ContainerCheckDigit;
	String CSContainerSizeType;
	String CarrCntrSizeType;
	String OOCLCntrSizeType;
}

class RailInformation implements Serializable {
	String RailCarrier;
	String RailPickupNumber;
	String ContainerNumber;
	String ContainerCheckDigit;
}

class AssociatedExternalReference implements Serializable {
	String CSReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
	String EDIReferenceCode;
	String ReferenceNumberIndicator;
	String IsShowOnBL;
	String MessageType;
}

class AssociatedParty implements Serializable {
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
	String MessageType;
}

public class ChargeAmountAdapter extends TypeAdapter<ChargeAmount> {
	
	@Override
	public ChargeAmount read(JsonReader jsonReader) throws IOException {
		
		ChargeAmount chargeAmount = new ChargeAmount();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_Currency":
						chargeAmount.attr_Currency = jsonReader.nextString();
						break;
					case "ChargeAmount":
						chargeAmount.ChargeAmount = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				chargeAmount = new ChargeAmount();
				chargeAmount.ChargeAmount = jsonReader.nextString();
			}catch(IllegalStateException e2){
				chargeAmount = new ChargeAmount();
				jsonReader.nextNull();
			}
		}
		
		return chargeAmount;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, ChargeAmount chargeAmount) throws IOException {
		if(chargeAmount?.ChargeAmount == null){
			jsonWriter.nullValue()
		}else{
			if(chargeAmount.attr_Currency != null){
				jsonWriter.beginObject()
				jsonWriter.name("attr_Currency")
				jsonWriter.value(chargeAmount.attr_Currency)
				
				jsonWriter.name("ChargeAmount")
				jsonWriter.value(chargeAmount.ChargeAmount)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(chargeAmount.ChargeAmount)
			}
		}
	}
}

public class BillingAmountAdapter extends TypeAdapter<BillingAmount> {
	
	@Override
	public BillingAmount read(JsonReader jsonReader) throws IOException {
		
		BillingAmount billingAmount = new BillingAmount();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_Currency":
						billingAmount.attr_Currency = jsonReader.nextString();
						break;
					case "BillingAmount":
						billingAmount.BillingAmount = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				billingAmount = new BillingAmount();
				billingAmount.BillingAmount = jsonReader.nextString();
			}catch(IllegalStateException e2){
				billingAmount = new BillingAmount();
				jsonReader.nextNull();
			}
		}
		
		return billingAmount;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, BillingAmount billingAmount) throws IOException {
		if(billingAmount?.BillingAmount == null){
			jsonWriter.nullValue()
		}else{
			if(billingAmount.attr_Currency != null){
				jsonWriter.beginObject()
				jsonWriter.name("attr_Currency")
				jsonWriter.value(billingAmount.attr_Currency)
				
				jsonWriter.name("BillingAmount")
				jsonWriter.value(billingAmount.BillingAmount)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(billingAmount.BillingAmount)
			}
		}
	}
}

public class TotalAmountAdapter extends TypeAdapter<TotalAmount> {
	
	@Override
	public TotalAmount read(JsonReader jsonReader) throws IOException {
		
		TotalAmount totalAmount = new TotalAmount();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_Currency":
						totalAmount.attr_Currency = jsonReader.nextString();
						break;
					case "TotalAmount":
						totalAmount.TotalAmount = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				totalAmount = new TotalAmount();
				totalAmount.TotalAmount = jsonReader.nextString();
			}catch(IllegalStateException e2){
				totalAmount = new TotalAmount();
				jsonReader.nextNull();
			}
		}
		
		return totalAmount;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, TotalAmount totalAmount) throws IOException {
		if(totalAmount?.TotalAmount == null){
			jsonWriter.nullValue()
		}else{
			if(totalAmount.attr_Currency != null){
				jsonWriter.beginObject()
				jsonWriter.name("attr_Currency")
				jsonWriter.value(totalAmount.attr_Currency)
				
				jsonWriter.name("TotalAmount")
				jsonWriter.value(totalAmount.TotalAmount)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(totalAmount.TotalAmount)
			}
		}
	}
}

public class TotalAmtInPmtCurrencyAdapter extends TypeAdapter<TotalAmtInPmtCurrency> {
	
	@Override
	public TotalAmtInPmtCurrency read(JsonReader jsonReader) throws IOException {
		
		TotalAmtInPmtCurrency totalAmtInPmtCurrency = new TotalAmtInPmtCurrency();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_Currency":
						totalAmtInPmtCurrency.attr_Currency = jsonReader.nextString();
						break;
					case "attr_ExchangeRate":
						totalAmtInPmtCurrency.attr_ExchangeRate = jsonReader.nextString();
						break;
					case "TotalAmtInPmtCurrency":
						totalAmtInPmtCurrency.TotalAmtInPmtCurrency = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				totalAmtInPmtCurrency = new TotalAmtInPmtCurrency();
				totalAmtInPmtCurrency.TotalAmtInPmtCurrency  = jsonReader.nextString();
			}catch(IllegalStateException e2){
				totalAmtInPmtCurrency = new TotalAmtInPmtCurrency();
				jsonReader.nextNull();
			}
		}
		
		return totalAmtInPmtCurrency;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, TotalAmtInPmtCurrency totalAmtInPmtCurrency) throws IOException {
		if(totalAmtInPmtCurrency?.TotalAmtInPmtCurrency == null){
			jsonWriter.nullValue()
		}else{
			if(totalAmtInPmtCurrency.attr_Currency != null || totalAmtInPmtCurrency.attr_ExchangeRate != null){
				jsonWriter.beginObject()
				if(totalAmtInPmtCurrency.attr_Currency != null){
					jsonWriter.name("attr_Currency")
					jsonWriter.value(totalAmtInPmtCurrency.attr_Currency)
				}
				if(totalAmtInPmtCurrency.attr_ExchangeRate != null){
					jsonWriter.name("attr_ExchangeRate")
					jsonWriter.value(totalAmtInPmtCurrency.attr_ExchangeRate)
				}
				jsonWriter.name("TotalAmtInPmtCurrency")
				jsonWriter.value(totalAmtInPmtCurrency.TotalAmtInPmtCurrency)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(totalAmtInPmtCurrency.TotalAmtInPmtCurrency)
			}
		}
	}
}

public class TotalAmtInAdditionalPmtCurrencyAdapter extends TypeAdapter<TotalAmtInAdditionalPmtCurrency> {
	
	@Override
	public TotalAmtInAdditionalPmtCurrency read(JsonReader jsonReader) throws IOException {
		
		TotalAmtInAdditionalPmtCurrency totalAmtInAdditionalPmtCurrency = new TotalAmtInAdditionalPmtCurrency();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_Currency":
						totalAmtInAdditionalPmtCurrency.attr_Currency = jsonReader.nextString();
						break;
					case "attr_ExchangeRate":
						totalAmtInAdditionalPmtCurrency.attr_ExchangeRate = jsonReader.nextString();
						break;
					case "TotalAmtInAdditionalPmtCurrency":
						totalAmtInAdditionalPmtCurrency.TotalAmtInAdditionalPmtCurrency = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				totalAmtInAdditionalPmtCurrency = new TotalAmtInAdditionalPmtCurrency();
				totalAmtInAdditionalPmtCurrency.TotalAmtInAdditionalPmtCurrency  = jsonReader.nextString();
			}catch(IllegalStateException e2){
				totalAmtInAdditionalPmtCurrency = new TotalAmtInAdditionalPmtCurrency();
				jsonReader.nextNull();
			}
		}
		
		return totalAmtInAdditionalPmtCurrency;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, TotalAmtInAdditionalPmtCurrency totalAmtInAdditionalPmtCurrency) throws IOException {
		if(totalAmtInAdditionalPmtCurrency?.TotalAmtInAdditionalPmtCurrency == null){
			jsonWriter.nullValue()
		}else{
			if(totalAmtInAdditionalPmtCurrency.attr_Currency != null || totalAmtInAdditionalPmtCurrency.attr_ExchangeRate != null){
				jsonWriter.beginObject()
				if(totalAmtInAdditionalPmtCurrency.attr_Currency != null){
					jsonWriter.name("attr_Currency")
					jsonWriter.value(totalAmtInAdditionalPmtCurrency.attr_Currency)
				}
				if(totalAmtInAdditionalPmtCurrency.attr_ExchangeRate != null){
					jsonWriter.name("attr_ExchangeRate")
					jsonWriter.value(totalAmtInAdditionalPmtCurrency.attr_ExchangeRate)
				}
				jsonWriter.name("TotalAmtInAdditionalPmtCurrency")
				jsonWriter.value(totalAmtInAdditionalPmtCurrency.TotalAmtInAdditionalPmtCurrency)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(totalAmtInAdditionalPmtCurrency.TotalAmtInAdditionalPmtCurrency)
			}
		}
	}
}

