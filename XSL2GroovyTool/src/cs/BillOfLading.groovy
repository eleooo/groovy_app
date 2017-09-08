package cs

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
;;

/**
 * @author RENGA
 * @cs2xmlVersion 67.3
 */
class BillOfLading implements Serializable {

    public static final Set<String> MultiElementList=["CarrierBookingNumber", "BookingOfficeUNLoc", "BookingOffice", "BookingOfficeName", "LabelActions", "AddressLine", "Reference", "EORINumber", "OceanLeg.DepartureDT", "OceanLeg.ArrivalDT", "ArrivalAtFND", "StopOff", "ArrivalAtFinalHub", "OceanLeg", "Container.Seal", "ContainerNumbers", "EmptyReturn", "EmergencyContact", "RFPackageUnit", "AWPackageUnit", "DGPackageUnit", "DGCargoSpec.Label", "GCPackageUnit", "HarmonizedTariffSchedule", "MarksAndNumbers", "AWCargoSpec", "ReeferCargoSpec", "ExternalReference", "DGCargoSpec", "TotalAmtInAdditionalPmtCurrency", "Appointment", "Cargo", "FreightChargeCNTR", "BLStatus", "BLCertClause", "FreightCharge", "Body.Container", "Party", "RailInformation", "CarrierRate", "DnD", "Body", "AssociatedExternalReference", "AssociatedParty"]

	cs.b2b.core.mapping.bean.bl.Header Header = new cs.b2b.core.mapping.bean.bl.Header();
	List<cs.b2b.core.mapping.bean.bl.Body> Body = new ArrayList<cs.b2b.core.mapping.bean.bl.Body>();
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
	cs.b2b.core.mapping.bean.bl.TransactionInformation TransactionInformation;
	cs.b2b.core.mapping.bean.bl.EventInformation EventInformation;
	cs.b2b.core.mapping.bean.bl.GeneralInformation GeneralInformation;
	List<cs.b2b.core.mapping.bean.bl.BLStatus> BLStatus = new ArrayList<cs.b2b.core.mapping.bean.bl.BLStatus>();
	List<cs.b2b.core.mapping.bean.bl.Party> Party = new ArrayList<cs.b2b.core.mapping.bean.bl.Party>();
	List<cs.b2b.core.mapping.bean.bl.CarrierRate> CarrierRate = new ArrayList<cs.b2b.core.mapping.bean.bl.CarrierRate>();
	List<cs.b2b.core.mapping.bean.bl.ExternalReference> ExternalReference = new ArrayList<cs.b2b.core.mapping.bean.bl.ExternalReference>();
	cs.b2b.core.mapping.bean.bl.Route Route;
	List<cs.b2b.core.mapping.bean.bl.Container> Container = new ArrayList<cs.b2b.core.mapping.bean.bl.Container>();
	cs.b2b.core.mapping.bean.bl.ContainerFlowInstruction ContainerFlowInstruction;
	List<cs.b2b.core.mapping.bean.bl.Cargo> Cargo = new ArrayList<cs.b2b.core.mapping.bean.bl.Cargo>();
	List<cs.b2b.core.mapping.bean.bl.Appointment> Appointment = new ArrayList<cs.b2b.core.mapping.bean.bl.Appointment>();
	List<cs.b2b.core.mapping.bean.bl.BLCertClause> BLCertClause = new ArrayList<cs.b2b.core.mapping.bean.bl.BLCertClause>();
	List<cs.b2b.core.mapping.bean.bl.DnD> DnD = new ArrayList<cs.b2b.core.mapping.bean.bl.DnD>();
	List<cs.b2b.core.mapping.bean.bl.FreightCharge> FreightCharge = new ArrayList<cs.b2b.core.mapping.bean.bl.FreightCharge>();
	List<cs.b2b.core.mapping.bean.bl.FreightChargeCNTR> FreightChargeCNTR = new ArrayList<cs.b2b.core.mapping.bean.bl.FreightChargeCNTR>();
	List<cs.b2b.core.mapping.bean.bl.RailInformation> RailInformation = new ArrayList<cs.b2b.core.mapping.bean.bl.RailInformation>();
	String Remarks;
	List<cs.b2b.core.mapping.bean.bl.AssociatedExternalReference> AssociatedExternalReference = new ArrayList<cs.b2b.core.mapping.bean.bl.AssociatedExternalReference>();
	List<cs.b2b.core.mapping.bean.bl.AssociatedParty> AssociatedParty = new ArrayList<cs.b2b.core.mapping.bean.bl.AssociatedParty>();
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
	cs.b2b.core.mapping.bean.bl.TrafficMode TrafficMode;
	String CustomsClearanceLoc;
	cs.b2b.core.mapping.bean.bl.CustomsClearanceDT CustomsClearanceDT;
	String CargoControlOffice;
	String ContactOfficeCode;
	cs.b2b.core.mapping.bean.bl.BLReceiptDT BLReceiptDT;
	cs.b2b.core.mapping.bean.bl.BLIssueDT BLIssueDT;
	cs.b2b.core.mapping.bean.bl.BLCreationDT BLCreationDT;
	cs.b2b.core.mapping.bean.bl.BLChangeDT BLChangeDT;
	cs.b2b.core.mapping.bean.bl.BLOnboardDT BLOnboardDT;
	String OriginalBLReleaseOffice;
	String BLPaymentOffice;
	String BLPaymentOfficeCode;
	String BLPaymentOfficeUNLoc;
	String CaptureOffice;
	String CaptureOfficeName;
	String CaptureOfficeUNLoc;
	String CaptureOfficePhoneNumber;
	cs.b2b.core.mapping.bean.bl.PaymentReceiptDT PaymentReceiptDT;
	String OriginOfGoods;
	List<String> CarrierBookingNumber = new ArrayList<String>();
	String Role;
	String SharedBy;
	cs.b2b.core.mapping.bean.bl.BLGrossWeight BLGrossWeight;
	cs.b2b.core.mapping.bean.bl.BLNetWeight BLNetWeight;
	cs.b2b.core.mapping.bean.bl.BLVolume BLVolume;
	cs.b2b.core.mapping.bean.bl.BLSVVD BLSVVD;
	cs.b2b.core.mapping.bean.bl.OutBoundROESVVD OutBoundROESVVD;
	cs.b2b.core.mapping.bean.bl.InBoundROESVVD InBoundROESVVD;
	cs.b2b.core.mapping.bean.bl.SIContactPerson SIContactPerson;
	String TradeCode;
	List<String> BookingOffice = new ArrayList<String>();
	List<String> BookingOfficeName = new ArrayList<String>();
	List<String> BookingOfficeUNLoc = new ArrayList<String>();
	String MovementRefNumber;
	String IsCSCustomer;
	List<cs.b2b.core.mapping.bean.bl.LabelActions> LabelActions = new ArrayList<cs.b2b.core.mapping.bean.bl.LabelActions>();
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
	cs.b2b.core.mapping.bean.bl.TimeOfIssue TimeOfIssue;
}

class TimeOfIssue implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class BLStatus implements Serializable {
	String attr_Type;
 	String Status;
	cs.b2b.core.mapping.bean.bl.StatusDT StatusDT;
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
	cs.b2b.core.mapping.bean.bl.Haulage Haulage;
	String IsOBDropAndPoll;
	List<cs.b2b.core.mapping.bean.bl.ArrivalAtFinalHub> ArrivalAtFinalHub = new ArrayList<cs.b2b.core.mapping.bean.bl.ArrivalAtFinalHub>();
	List<cs.b2b.core.mapping.bean.bl.ArrivalAtFND> ArrivalAtFND = new ArrayList<cs.b2b.core.mapping.bean.bl.ArrivalAtFND>();
	cs.b2b.core.mapping.bean.bl.FullReturnCutoffDT FullReturnCutoffDT;
	cs.b2b.core.mapping.bean.bl.DepartureDT DepartureDT;
	cs.b2b.core.mapping.bean.bl.ArrivalDT ArrivalDT;
	cs.b2b.core.mapping.bean.bl.POR POR;
	cs.b2b.core.mapping.bean.bl.FND FND;
	cs.b2b.core.mapping.bean.bl.FirstPOL FirstPOL;
	cs.b2b.core.mapping.bean.bl.LastPOD LastPOD;
	List<cs.b2b.core.mapping.bean.bl.OceanLeg> OceanLeg = new ArrayList<cs.b2b.core.mapping.bean.bl.OceanLeg>();
	List<cs.b2b.core.mapping.bean.bl.StopOff> StopOff = new ArrayList<cs.b2b.core.mapping.bean.bl.StopOff>();
	cs.b2b.core.mapping.bean.bl.ActualCargoReceiptDT ActualCargoReceiptDT;
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
	cs.b2b.core.mapping.bean.bl.OutboundSVVD OutboundSVVD;
	cs.b2b.core.mapping.bean.bl.DepartureDT DepartureDT;
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
	cs.b2b.core.mapping.bean.bl.InboundSVVD InboundSVVD;
	cs.b2b.core.mapping.bean.bl.ArrivalDT ArrivalDT;
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
	cs.b2b.core.mapping.bean.bl.CarrierExtractDT CarrierExtractDT;
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
	cs.b2b.core.mapping.bean.bl.CustomsClearanceDT CustomsClearanceDT;
	String CustomsClearanceLocCity;
	String CustomsClearanceLocCode;
	String CustomsClearanceLocType;
	String CustomsClearanceLocDesc;
	String CustomsRefType;
	String CustomsRefNumber;
	cs.b2b.core.mapping.bean.bl.CustomsRefDT CustomsRefDT;
	cs.b2b.core.mapping.bean.bl.PieceCount PieceCount;
	String InlandMoveIDPrefix;
	String SIContainerNumber;
	String SIContainerCheckDigit;
	cs.b2b.core.mapping.bean.bl.SIDeclaredGrossWeight SIDeclaredGrossWeight;
	String LastTransferFromBookingNo;
	String LastTransferToBookingNo;
	cs.b2b.core.mapping.bean.bl.BLSeal BLSeal;
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
	List<cs.b2b.core.mapping.bean.bl.EmptyReturn> EmptyReturn = new ArrayList<cs.b2b.core.mapping.bean.bl.EmptyReturn>();
}

class EmptyReturn implements Serializable {
	String NumberOfContainers;
	String ISOSizeType;
	String CSContainerSizeType;
	List<cs.b2b.core.mapping.bean.bl.ContainerNumbers> ContainerNumbers = new ArrayList<cs.b2b.core.mapping.bean.bl.ContainerNumbers>();
	cs.b2b.core.mapping.bean.bl.Facility Facility;
	cs.b2b.core.mapping.bean.bl.MvmtDT MvmtDT;
	cs.b2b.core.mapping.bean.bl.Address Address;
	cs.b2b.core.mapping.bean.bl.Contact Contact;
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
	cs.b2b.core.mapping.bean.bl.TrafficModel TrafficModel;
	String CgoPkgName;
	String CgoPkgCodeCount;
	String CustomsRefNumber;
	String CustomsRefType;
	cs.b2b.core.mapping.bean.bl.CustomsRefDT CustomsRefDT;
	String CustomsClearanceLocCode;
	String CustomsClearanceLocCity;
	String CustomsClearanceLocType;
	String CustomsClearanceLocDesc;
	cs.b2b.core.mapping.bean.bl.Seal Seal;
	String ArticleNumber;
	String CommodityDescription;
	String BLCargoDescription;
	List<cs.b2b.core.mapping.bean.bl.GCPackageUnit> GCPackageUnit = new ArrayList<cs.b2b.core.mapping.bean.bl.GCPackageUnit>();
	List<cs.b2b.core.mapping.bean.bl.ReeferCargoSpec> ReeferCargoSpec = new ArrayList<cs.b2b.core.mapping.bean.bl.ReeferCargoSpec>();
	List<cs.b2b.core.mapping.bean.bl.AWCargoSpec> AWCargoSpec = new ArrayList<cs.b2b.core.mapping.bean.bl.AWCargoSpec>();
	List<cs.b2b.core.mapping.bean.bl.DGCargoSpec> DGCargoSpec = new ArrayList<cs.b2b.core.mapping.bean.bl.DGCargoSpec>();
	List<cs.b2b.core.mapping.bean.bl.ExternalReference> ExternalReference = new ArrayList<cs.b2b.core.mapping.bean.bl.ExternalReference>();
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
	List<cs.b2b.core.mapping.bean.bl.RFPackageUnit> RFPackageUnit = new ArrayList<cs.b2b.core.mapping.bean.bl.RFPackageUnit>();
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
	List<cs.b2b.core.mapping.bean.bl.AWPackageUnit> AWPackageUnit = new ArrayList<cs.b2b.core.mapping.bean.bl.AWPackageUnit>();
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
	List<cs.b2b.core.mapping.bean.bl.DGPackageUnit> DGPackageUnit = new ArrayList<cs.b2b.core.mapping.bean.bl.DGPackageUnit>();
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
	cs.b2b.core.mapping.bean.bl.FreeTimeStartDT FreeTimeStartDT;
	cs.b2b.core.mapping.bean.bl.FreeTimeEndDT FreeTimeEndDT;
	String FreeTime;
	String FreeTimeType;
	String CSS_BL_CNTR_UUID;
	cs.b2b.core.mapping.bean.bl.ClockEndDT ClockEndDT;
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
	cs.b2b.core.mapping.bean.bl.ChargeAmount ChargeAmount;
	cs.b2b.core.mapping.bean.bl.BillingAmount BillingAmount;
	String ChargeNetAmount;
	String PaymentCurrency;
	String InvoiceNumber;
	String ControlOfficeCode;
	String CollectionOfficeCode;
	cs.b2b.core.mapping.bean.bl.TotalAmount TotalAmount;
	cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrency TotalAmtInPmtCurrency;
	cs.b2b.core.mapping.bean.bl.SVVD SVVD;
	String ExchRateToEurope;
	String OBCustomsUNVoyRef;
	String IBCustomsUNVoyRef;
	String OBVoyRef;
	String IBVoyRef;
	String IsApprovedForCustomer;
	String ChargeDesc;
	cs.b2b.core.mapping.bean.bl.PayByInformation PayByInformation;
	String RatePercentage;
	List<cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency> TotalAmtInAdditionalPmtCurrency = new ArrayList<cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency>();
}

@JsonAdapter(cs.b2b.core.mapping.bean.bl.ChargeAmountAdapter.class)
class ChargeAmount implements Serializable {
	String attr_Currency;
 	String ChargeAmount;

	@Override
	public String toString() {
		return this.ChargeAmount;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.bl.BillingAmountAdapter.class)
class BillingAmount implements Serializable {
	String attr_Currency;
 	String BillingAmount;

	@Override
	public String toString() {
		return this.BillingAmount;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.bl.TotalAmountAdapter.class)
class TotalAmount implements Serializable {
	String attr_Currency;
 	String TotalAmount;

	@Override
	public String toString() {
		return this.TotalAmount;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrencyAdapter.class)
class TotalAmtInPmtCurrency implements Serializable {
	String attr_Currency;
 	String attr_ExchangeRate;
 	String TotalAmtInPmtCurrency;

	@Override
	public String toString() {
		return this.TotalAmtInPmtCurrency;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrencyAdapter.class)
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
	cs.b2b.core.mapping.bean.bl.CityDetails CityDetails;
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
	cs.b2b.core.mapping.bean.bl.ChargeAmount ChargeAmount;
	cs.b2b.core.mapping.bean.bl.BillingAmount BillingAmount;
	String PaymentCurrency;
	String InvoiceNumber;
	String ControlOfficeCode;
	String CollectionOfficeCode;
	cs.b2b.core.mapping.bean.bl.TotalAmount TotalAmount;
	cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrency TotalAmtInPmtCurrency;
	cs.b2b.core.mapping.bean.bl.SVVD SVVD;
	String ExchRateToEurope;
	String OBCustomsUNVoyRef;
	String IBCustomsUNVoyRef;
	String OBVoyRef;
	String IBVoyRef;
	String IsApprovedForCustomer;
	String ChargeDesc;
	cs.b2b.core.mapping.bean.bl.PayByInformation PayByInformation;
	String RatePercentage;
	List<cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency> TotalAmtInAdditionalPmtCurrency = new ArrayList<cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency>();
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

public class ChargeAmountAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.bl.ChargeAmount> {
	
	@Override
	public cs.b2b.core.mapping.bean.bl.ChargeAmount read(JsonReader jsonReader) throws IOException {
		
		cs.b2b.core.mapping.bean.bl.ChargeAmount chargeAmount = new cs.b2b.core.mapping.bean.bl.ChargeAmount();

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
				chargeAmount = new cs.b2b.core.mapping.bean.bl.ChargeAmount();
				chargeAmount.ChargeAmount = jsonReader.nextString();
			}catch(IllegalStateException e2){
				chargeAmount = new cs.b2b.core.mapping.bean.bl.ChargeAmount();
				jsonReader.nextNull();
			}
		}
		
		return chargeAmount;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, cs.b2b.core.mapping.bean.bl.ChargeAmount chargeAmount) throws IOException {
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

public class BillingAmountAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.bl.BillingAmount> {
	
	@Override
	public cs.b2b.core.mapping.bean.bl.BillingAmount read(JsonReader jsonReader) throws IOException {
		
		cs.b2b.core.mapping.bean.bl.BillingAmount billingAmount = new cs.b2b.core.mapping.bean.bl.BillingAmount();

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
				billingAmount = new cs.b2b.core.mapping.bean.bl.BillingAmount();
				billingAmount.BillingAmount = jsonReader.nextString();
			}catch(IllegalStateException e2){
				billingAmount = new cs.b2b.core.mapping.bean.bl.BillingAmount();
				jsonReader.nextNull();
			}
		}
		
		return billingAmount;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, cs.b2b.core.mapping.bean.bl.BillingAmount billingAmount) throws IOException {
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

public class TotalAmountAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.bl.TotalAmount> {
	
	@Override
	public cs.b2b.core.mapping.bean.bl.TotalAmount read(JsonReader jsonReader) throws IOException {
		
		cs.b2b.core.mapping.bean.bl.TotalAmount totalAmount = new cs.b2b.core.mapping.bean.bl.TotalAmount();

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
				totalAmount = new cs.b2b.core.mapping.bean.bl.TotalAmount();
				totalAmount.TotalAmount = jsonReader.nextString();
			}catch(IllegalStateException e2){
				totalAmount = new cs.b2b.core.mapping.bean.bl.TotalAmount();
				jsonReader.nextNull();
			}
		}
		
		return totalAmount;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, cs.b2b.core.mapping.bean.bl.TotalAmount totalAmount) throws IOException {
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

public class TotalAmtInPmtCurrencyAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrency> {
	
	@Override
	public cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrency read(JsonReader jsonReader) throws IOException {
		
		cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrency totalAmtInPmtCurrency = new cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrency();

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
				totalAmtInPmtCurrency = new cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrency();
				totalAmtInPmtCurrency.TotalAmtInPmtCurrency  = jsonReader.nextString();
			}catch(IllegalStateException e2){
				totalAmtInPmtCurrency = new cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrency();
				jsonReader.nextNull();
			}
		}
		return totalAmtInPmtCurrency;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, cs.b2b.core.mapping.bean.bl.TotalAmtInPmtCurrency totalAmtInPmtCurrency) throws IOException {
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

public class TotalAmtInAdditionalPmtCurrencyAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency> {
	
	@Override
	public cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency read(JsonReader jsonReader) throws IOException {
		
		cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency totalAmtInAdditionalPmtCurrency = new cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency();

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
				totalAmtInAdditionalPmtCurrency = new cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency();
				totalAmtInAdditionalPmtCurrency.TotalAmtInAdditionalPmtCurrency  = jsonReader.nextString();
			}catch(IllegalStateException e2){
				totalAmtInAdditionalPmtCurrency = new cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency();
				jsonReader.nextNull();
			}
		}
		
		return totalAmtInAdditionalPmtCurrency;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, cs.b2b.core.mapping.bean.bl.TotalAmtInAdditionalPmtCurrency totalAmtInAdditionalPmtCurrency) throws IOException {
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

