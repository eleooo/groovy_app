package cs.b2b.core.mapping.bean.bc

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


/**
 * @author RENGA
 * @cs2xmlVersion 67.3
 */
class BookingConfirm implements Serializable {
	
	public static final Set<String> MultiElementList= ["BLNumber", "DeclarantType", "LabelActions", "AddressLine", "Reference", "Seal", "ContainerNumbers", "FullReturn", "EmptyPickup", "EmptyReturn", "ContainerGroup.Container", "EmergencyContact", "DGCargoSpec.Label", "LicencePermit", "MarksAndNumbers", "AWCargoSpec", "ReeferCargoSpec", "DGCargoSpec", "OceanLeg.DepartureDT", "OceanLeg.ArrivalDT", "ArrivalAtFirstPOLDT", "ArrivalAtFND", "StopOff", "ArrivalAtFinalHub", "OceanLeg", "DocumentReceipt", "RequiredDocument", "ReceivedDocument", "FilingKey", "Appointment", "Cargo", "AssociatedExternalReference", "RemarkLines", "Party", "ExternalReference", "BLFiling", "CarrierRate", "DnD", "Body","AssociatedParty", "ChargePaymentInstruction"]
	
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
	List<CarrierRate> CarrierRate = new ArrayList<CarrierRate>();
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	List<Party> Party = new ArrayList<Party>();
	ContainerGroup ContainerGroup;
	List<Cargo> Cargo = new ArrayList<Cargo>();
	Route Route;
	List<Appointment> Appointment = new ArrayList<Appointment>();
	List<DnD> DnD = new ArrayList<DnD>();
	DocumentInformation DocumentInformation;
	List<BLFiling> BLFiling = new ArrayList<BLFiling>();
	String Remarks;
	List<RemarkLines> RemarkLines = new ArrayList<RemarkLines>();
	ServiceRequestInfo ServiceRequestInfo;
	List<AssociatedExternalReference> AssociatedExternalReference = new ArrayList<AssociatedExternalReference>();
	List<AssociatedParty> AssociatedParty = new ArrayList<AssociatedParty>();
}

@JsonAdapter(RemarkLinesAdapter.class)
class RemarkLines implements Serializable {
	String attr_RemarkCode;
 	String attr_RemarkType;
 	String RemarkLines;

	@Override
	public String toString() {
		return this.RemarkLines;
	}
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
	String CSBookingRefNumber;
	String CarrierBookingNumber;
	String BookingOffice;
	String BookingStatus;
	String BookingStatusRemarks;
	BookingStatusDT BookingStatusDT;
	String SIStatus;
	SIStatusDT SIStatusDT;
	String ShipmentCargoType;
	TrafficMode TrafficMode;
	String OriginOfGoods;
	String Broker;
	String BrokerName;
	String IsOBCustomsRequired;
	SICutOffDT SICutOffDT;
	String CntrAggregateStatus;
	CntrAggregateStatusDT CntrAggregateStatusDT;
	List<DeclarantType> DeclarantType = new ArrayList<DeclarantType>();
	List<String> BLNumber = new ArrayList<String>();
	String Role;
	String SharedBy;
	LastModifiedDT LastModifiedDT;
	FreezeDT FreezeDT;
	CreationDT CreationDT;
	ReceiptDT ReceiptDT;
	BookingContactPerson BookingContactPerson;
	String CarrierBookingStatus;
	String IsCSCustomer;
	String ReeferGPSIndicator;
	List<LabelActions> LabelActions = new ArrayList<LabelActions>();
	String PartnerID;
	String TradeCode;
	String PayableElsewhere;
	String TermsOfPayment;
	String ContainerPickupCheckcode;
	VGMCutOffDT VGMCutOffDT;
	EarliestReturnDate EarliestReturnDate;
	String SubSCACCode;
}

@JsonAdapter(DeclarantTypeAdapter.class)
class DeclarantType implements Serializable {
	String attr_CountryCode;
 	String DeclarantType;

	@Override
	public String toString() {
		return this.DeclarantType;
	}
}

class BookingStatusDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class SIStatusDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class TrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class SICutOffDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class CntrAggregateStatusDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class LastModifiedDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class FreezeDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class CreationDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ReceiptDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class BookingContactPerson implements Serializable {
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

class VGMCutOffDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class EarliestReturnDate implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
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

class ContainerGroup implements Serializable {
	List<Container> Container = new ArrayList<Container>();
	ContainerFlowInstruction ContainerFlowInstruction;
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
	String LastTransferFromBookingNo;
	String LastTransferToBookingNo;
	CustomsReference CustomsReference;
	String IsTriangulatedIndicator;
	String RepackContainerNumber;
	String CS1ContainerSizeType;
	String RuReferenceNumber;
	String PickupAuthorizationNumber;
	String CSS_CNTR_UUID;
	String OOCLCntrSizeType;
	String DraftContainerNumber;
	String SubstituteContainerSizeType;
	VerifiedGrossMass VerifiedGrossMass;
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
	String Status;
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

class ContainerFlowInstruction implements Serializable {
	List<EmptyPickup> EmptyPickup = new ArrayList<EmptyPickup>();
	List<EmptyReturn> EmptyReturn = new ArrayList<EmptyReturn>();
	List<FullReturn> FullReturn = new ArrayList<FullReturn>();
}

class EmptyPickup implements Serializable {
	String NumberOfContainers;
	String ISOSizeType;
	String CSContainerSizeType;
	List<ContainerNumbers> ContainerNumbers = new ArrayList<ContainerNumbers>();
	Facility Facility;
	MvmtDT MvmtDT;
	Address Address;
	Contact Contact;
	String CSS_RTE_INFO_UUID;
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

class EmptyReturn implements Serializable {
	String NumberOfContainers;
	String ISOSizeType;
	String CSContainerSizeType;
	List<ContainerNumbers> ContainerNumbers = new ArrayList<ContainerNumbers>();
	Facility Facility;
	MvmtDT MvmtDT;
	Address Address;
	Contact Contact;
	String CSS_RTE_INFO_UUID;
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
	String CSS_RTE_INFO_UUID;
}

class Cargo implements Serializable {
	String CargoNature;
	String CargoDescription;
	cs.b2b.core.mapping.bean.Packaging Packaging;
	cs.b2b.core.mapping.bean.GrossWeight GrossWeight;
	cs.b2b.core.mapping.bean.NetWeight NetWeight;
	cs.b2b.core.mapping.bean.Volume Volume;
	List<cs.b2b.core.mapping.bean.MarksAndNumbers> MarksAndNumbers = new ArrayList<cs.b2b.core.mapping.bean.MarksAndNumbers>();
	String CurrentContainerNumber;
	String CurrentContainerCheckDigit;
	List<LicencePermit> LicencePermit = new ArrayList<LicencePermit>();
	List<ReeferCargoSpec> ReeferCargoSpec = new ArrayList<ReeferCargoSpec>();
	List<DGCargoSpec> DGCargoSpec = new ArrayList<DGCargoSpec>();
	List<AWCargoSpec> AWCargoSpec = new ArrayList<AWCargoSpec>();
	CustomsReference CustomsReference;
	String CSS_CGO_UUID;
}

class LicencePermit implements Serializable {
	String attr_DutiableType;
 	String attr_Type;
 	String Number;
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

class Route implements Serializable {
	Haulage Haulage;
	String OBDropAndPull;
	FullReturnCutoff FullReturnCutoff;
	List<ArrivalAtFinalHub> ArrivalAtFinalHub = new ArrayList<ArrivalAtFinalHub>();
	List<ArrivalAtFND> ArrivalAtFND = new ArrayList<ArrivalAtFND>();
	ReqDeliveryDT ReqDeliveryDT;
	List<ArrivalAtFirstPOLDT> ArrivalAtFirstPOLDT = new ArrayList<ArrivalAtFirstPOLDT>();
	POR POR;
	FirstPOL FirstPOL;
	LastPOD LastPOD;
	FND FND;
	List<OceanLeg> OceanLeg = new ArrayList<OceanLeg>();
	List<StopOff> StopOff = new ArrayList<StopOff>();
	String CSS_RTE_INFO_UUID;
	String Inbound_intermodal_indicator;
	String Outbound_intermodal_indicator;
}

class Haulage implements Serializable {
	String OutBound;
	String InBound;
}

class FullReturnCutoff implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
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

class ReqDeliveryDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ArrivalAtFirstPOLDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class POR implements Serializable {
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

class DepartureDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
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

class ArrivalDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class FND implements Serializable {
	String LocationName;
	cs.b2b.core.mapping.bean.CityDetails CityDetails;
	cs.b2b.core.mapping.bean.Facility Facility;
	cs.b2b.core.mapping.bean.CSStandardCity CSStandardCity;
}

class OceanLeg implements Serializable {
	String LegSeq;
	cs.b2b.core.mapping.bean.POL POL;
	cs.b2b.core.mapping.bean.POD POD;
	cs.b2b.core.mapping.bean.SVVD SVVD;
	List<cs.b2b.core.mapping.bean.DepartureDT> DepartureDT = new ArrayList<cs.b2b.core.mapping.bean.DepartureDT>();
	List<cs.b2b.core.mapping.bean.ArrivalDT> ArrivalDT = new ArrayList<cs.b2b.core.mapping.bean.ArrivalDT>();
	CarrierExtractDT CarrierExtractDT;
	BlockCode BlockCode;
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

class BlockCode implements Serializable {
	String StandardDischargeBlock;
	String OverflowDischargeBlock;
	String UserDischargeBlock;
}

class StopOff implements Serializable {
	String SequenceNumber;
	cs.b2b.core.mapping.bean.PickupDetails PickupDetails;
	cs.b2b.core.mapping.bean.ReturnDetails ReturnDetails;
	String STOPOFF_LEG_UUID;
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

class DnD implements Serializable {
	String attr_Type;
 	String ContainerNumber;
	String ContainerCheckDigit;
	FreeTimeStartDT FreeTimeStartDT;
	FreeTimeEndDT FreeTimeEndDT;
	DemFreeReceivedDT DemFreeReceivedDT;
	EarliestEmptyPickupDT EarliestEmptyPickupDT;
	String FreeTime;
	String FreeTimeType;
	String CSS_CNTR_UUID;
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

class DemFreeReceivedDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class EarliestEmptyPickupDT implements Serializable {
	String attr_Indicator;
 	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class DocumentInformation implements Serializable {
	List<RequiredDocument> RequiredDocument = new ArrayList<RequiredDocument>();
	List<ReceivedDocument> ReceivedDocument = new ArrayList<ReceivedDocument>();
}

class RequiredDocument implements Serializable {
	String DocumentType;
	UpdatedDT UpdatedDT;
	DueDT DueDT;
	String IsForShipper;
	String IsForConsignee;
	String IsForBookingParty;
	String IsForForwarder;
	String IsForNotifyParty;
	String IsForAlsoNotify;
}

class UpdatedDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class DueDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ReceivedDocument implements Serializable {
	String attr_IsCompleted;
 	String DocumentUUID;
	String DocumentType;
	UpdatedDT UpdatedDT;
	List<DocumentReceipt> DocumentReceipt = new ArrayList<DocumentReceipt>();
}

class DocumentReceipt implements Serializable {
	String DocumentReceiptUUID;
	String DeliveryChannel;
	ReceiptDT ReceiptDT;
	UpdatedDT UpdatedDT;
	String ReceivedFromPartyType;
}

class BLFiling implements Serializable {
	String RegionCode;
	String FilingType;
	String IsHouseMasterShipment;
	String IsNeedShipToParty;
	String IsFilingShipmentPartyEORI;
	String TaxIDType;
	String CargoReceiverTaxID;
	String BtaFdaFlag;
	String IsHouseBL;
	String HarmonizedCode;
	cs.b2b.core.mapping.bean.CutOffTime CutOffTime;
	List<FilingKey> FilingKey = new ArrayList<FilingKey>();
}

class FilingKey implements Serializable {
	String key;
	String value;
}

class ServiceRequestInfo implements Serializable {
	String ServiceRequestLockID;
	String Status;
	String Remarks;
}

class AssociatedExternalReference implements Serializable {
	String CSReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
	String EDIReferenceType;
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
	String SalesOfficeCode;
	String EDIPartyType;
	String CustomerCarrierCode;
	String CodeListQualifier;
	List<ChargePaymentInstruction> ChargePaymentInstruction = new ArrayList<ChargePaymentInstruction>();
	String MessageType;
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

public class DeclarantTypeAdapter extends TypeAdapter<DeclarantType> {
	
	@Override
	public DeclarantType read(JsonReader jsonReader) throws IOException {
		
		DeclarantType declarantType = new DeclarantType();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_CountryCode":
						declarantType.attr_CountryCode = jsonReader.nextString();
						break;
					case "DeclarantType":
						declarantType.DeclarantType = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				declarantType = new DeclarantType();
				declarantType.DeclarantType = jsonReader.nextString();
			}catch(IllegalStateException e2){
				declarantType = new DeclarantType();
				jsonReader.nextNull();
			}
		}
		
		return declarantType;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, DeclarantType declarantType) throws IOException {
		if(declarantType?.DeclarantType == null){
			jsonWriter.nullValue()
		}else{
			if(declarantType.attr_CountryCode != null){
				jsonWriter.beginObject()
				jsonWriter.name("attr_CountryCode")
				jsonWriter.value(declarantType.attr_CountryCode)
				
				jsonWriter.name("DeclarantType")
				jsonWriter.value(declarantType.DeclarantType)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(declarantType.DeclarantType)
			}
		}
	}
}

public class RemarkLinesAdapter extends TypeAdapter<RemarkLines> {
	
	@Override
	public RemarkLines read(JsonReader jsonReader) throws IOException {
		
		RemarkLines remarkLines = new RemarkLines();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_RemarkCode":
						remarkLines.attr_RemarkCode = jsonReader.nextString();
						break;
					case "attr_RemarkType":
						remarkLines.attr_RemarkType = jsonReader.nextString();
						break;
					case "RemarkLines":
						remarkLines.RemarkLines = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				remarkLines = new RemarkLines();
				remarkLines.RemarkLines  = jsonReader.nextString();
			}catch(IllegalStateException e2){
				remarkLines = new RemarkLines();
				jsonReader.nextNull();
			}
		}
		
		return remarkLines;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, RemarkLines remarkLines) throws IOException {
		if(remarkLines?.RemarkLines == null){
			jsonWriter.nullValue()
		}else{
			if(remarkLines.attr_RemarkCode != null || remarkLines.attr_RemarkType != null){
				jsonWriter.beginObject()
				if(remarkLines.attr_RemarkCode != null){
					jsonWriter.name("attr_RemarkCode")
					jsonWriter.value(remarkLines.attr_RemarkCode)
				}
				if(remarkLines.attr_RemarkType != null){
					jsonWriter.name("attr_RemarkType")
					jsonWriter.value(remarkLines.attr_RemarkType)
				}
				jsonWriter.name("RemarkLines")
				jsonWriter.value(remarkLines.RemarkLines)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(remarkLines.RemarkLines)
			}
		}
	}
}


