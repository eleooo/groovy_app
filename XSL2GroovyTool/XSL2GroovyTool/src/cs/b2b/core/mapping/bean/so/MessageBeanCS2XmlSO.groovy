package cs.b2b.core.mapping.bean.so;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import cs.b2b.core.mapping.bean.LocDT;
import cs.b2b.core.mapping.bean.LocDTAdapter;

class ShippingOrder implements Serializable {
	public static final Set<String> MultiElementList = ["ChargePaymentInstruction", "MonetaryAmount", "SOPartyContact.ContactEmailAddress", "SOPartyContact.ContactFax", "SOPartyContact.ContactPhone","SOPartyContact.ContactEmailAddress","SOEmergencyContact.ContactFax", "SOEmergencyContact.ContactPhone", "SOPartyContact", "DocumentMessageDetails", "Reference", "EmergencyContact", "TextLiteral", "FreeText", "Seal", "MarksAndNumbersLine", "SOMarksAndNumbers", "MarksAndNumbers", "HarmonizedTariffSchedule", "SOEmergencyContact", "Label", "ExternalReference", "AWCargoSpec", "DGCargoSpec", "USCensusBureauScheduleBNumber", "Cargo.ReeferCargoSpec", "ContainerLoadPlan", "OceanLeg", "NewStructure", "Party", "Order.Remarks", "Cargo", "Container", "Order", "AddressLine"]
	
	Header Header;
	List<Order> Order = new ArrayList<Order>();
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

class Order implements Serializable {
	TransactionInformation TransactionInformation;
	EventInformation EventInformation;
	GeneralInformation GeneralInformation;
	List<Party> Party = new ArrayList<Party>();
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	List<Container> Container = new ArrayList<Container>();
	List<Cargo> Cargo = new ArrayList<Cargo>();
	Route Route;
	EDIDynamicStructure EDIDynamicStructure;
	List<Remarks> Remarks = new ArrayList<Remarks>();
}

@JsonAdapter(RemarksAdapter.class)
class Remarks implements Serializable {
	String attr_RemarkCode;
 	String attr_RemarkType;
 	String Remarks;
	 
	 @Override
	public String toString() {
		return Remarks;
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
	String DocumentNameCode;
	String CSSORefNumber;
	String CarrierBookingNumber;
	String ActionType;
	String SCAC;
	String SPCompanyID;
	String SOVersionNumber;
	Requested Requested;
	String ShipmentCargoType;
	Haulage Haulage;
	ShipmentTrafficMode ShipmentTrafficMode;
	String CustomerBookingReference;
	List<MonetaryAmount> MonetaryAmount = new ArrayList<MonetaryAmount>();
	List<ChargePaymentInstruction> ChargePaymentInstruction = new ArrayList<ChargePaymentInstruction>();
	TotalGrossWeight TotalGrossWeight;
	String TotalNumberOfPackage;
	TotalConsignment TotalConsignment;
	String TotalNumberOfEquipment;
	PlaceOfPayment PlaceOfPayment;
	BillOfLadingReleaseOffice BillOfLadingReleaseOffice;
	IssuranceOfBLDT IssuranceOfBLDT;
	ExpiryDateOfLetterOfCredit ExpiryDateOfLetterOfCredit;
	IssuanceDateOfLetterOfCredit IssuanceDateOfLetterOfCredit;
	OriginalLocation OriginalLocation;
	String TransportDocumentCurrency;
}

class Requested implements Serializable {
	String By;
	RequestedDT RequestedDT;
}

class RequestedDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
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
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String LocationName;
}

class BillOfLadingReleaseOffice implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	cs.b2b.core.mapping.bean.LocationCode LocationCode;
	String LocationName;
}

class IssuranceOfBLDT implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class ExpiryDateOfLetterOfCredit implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class IssuanceDateOfLetterOfCredit implements Serializable {
	String GMT;
	cs.b2b.core.mapping.bean.LocDT LocDT;
}

class OriginalLocation implements Serializable {
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
	String EDIPartyType;
	String CustomerCarrierCode;
	String CodeListQualifier;
	List<DocumentMessageDetails> DocumentMessageDetails = new ArrayList<DocumentMessageDetails>();
	List<SOPartyContact> SOPartyContact = new ArrayList<SOPartyContact>();
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
	List<ContactPhone> ContactPhone = new ArrayList<ContactPhone>();
	List<ContactFax> ContactFax = new ArrayList<ContactFax>();
	List<String> ContactEmailAddress = new ArrayList<String>();
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
	GrossWeight GrossWeight;
	String IsSOC;
	GrossVolume GrossVolume;
	ReeferCargoSpec ReeferCargoSpec;
	List<Seal> Seal = new ArrayList<Seal>();
	List<FreeText> FreeText = new ArrayList<FreeText>();
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
	CargoInfo CargoInfo;
	List<ReeferCargoSpec> ReeferCargoSpec = new ArrayList<ReeferCargoSpec>();
	List<DGCargoSpec> DGCargoSpec = new ArrayList<DGCargoSpec>();
	List<AWCargoSpec> AWCargoSpec = new ArrayList<AWCargoSpec>();
	List<ExternalReference> ExternalReference = new ArrayList<ExternalReference>();
	List<String> USCensusBureauScheduleBNumber = new ArrayList<String>();
	List<ContainerLoadPlan> ContainerLoadPlan = new ArrayList<ContainerLoadPlan>();
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
	List<String> HarmonizedTariffSchedule = new ArrayList<String>();
	List<FreeText> FreeText = new ArrayList<FreeText>();
	List<SOMarksAndNumbers> SOMarksAndNumbers = new ArrayList<SOMarksAndNumbers>();
	String ItemNumber;
	InnerPackaging InnerPackaging;
	InnerGrossWeight InnerGrossWeight;
	InnerNetWeight InnerNetWeight;
	InnerVolume InnerVolume;
	InnerInnerPackaging InnerInnerPackaging;
	InnerInnerGrossWeight InnerInnerGrossWeight;
	InnerInnerNetWeight InnerInnerNetWeight;
	InnerInnerVolume InnerInnerVolume;
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
	String FinalProperShippingName;
	List<FreeText> FreeText = new ArrayList<FreeText>();
	List<SOEmergencyContact> SOEmergencyContact = new ArrayList<SOEmergencyContact>();
}

class SOEmergencyContact implements Serializable {
	String FirstName;
	String LastName;
	List<ContactPhone> ContactPhone = new ArrayList<ContactPhone>();
	List<ContactFax> ContactFax = new ArrayList<ContactFax>();
	List<String> ContactEmailAddress = new ArrayList<String>();
	String Type;
	String EM_CTACT_UUID;
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
	Volume Volume;
	String ContainerSequenceID;
}

class Package implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class Volume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class TrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class Route implements Serializable {
	POR POR;
	FND FND;
	FirstPOL FirstPOL;
	LastPOD LastPOD;
	List<OceanLeg> OceanLeg = new ArrayList<OceanLeg>();
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
	String LoadingPortVoyage;
	String LoadingPortVesselOperator;
	String LoadingPortVesselName;
}

class LastPOD implements Serializable {
	cs.b2b.core.mapping.bean.Port Port;
	cs.b2b.core.mapping.bean.Facility Facility;
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

class EDIDynamicStructure implements Serializable {
	List<NewStructure> NewStructure = new ArrayList<NewStructure>();
}

class NewStructure implements Serializable {
	String ParentStructureName;
	String NewElementName;
	String NewElementValue;
}

public class RemarksAdapter extends TypeAdapter<Remarks> {
	
	@Override
	public Remarks read(JsonReader jsonReader) throws IOException {
		
		Remarks remarks = new Remarks();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_RemarkCode":
						remarks.attr_RemarkCode = jsonReader.nextString();
						break;
					case "attr_RemarkType":
						remarks.attr_RemarkType = jsonReader.nextString();
						break;
					case "Remarks":
						remarks.Remarks = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				remarks = new Remarks();
				remarks.setRemarks(jsonReader.nextString());
			}catch(IllegalStateException e2){
				remarks = new Remarks();
				jsonReader.nextNull();
			}
		}
		
		return remarks;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Remarks remarks) throws IOException {
		if(remarks?.Remarks == null){
			jsonWriter.nullValue()
		}else{
			if(remarks.attr_RemarkCode != null || remarks.attr_RemarkType != null){
				jsonWriter.beginObject()
				if(remarks.attr_RemarkCode != null){
					jsonWriter.name("attr_RemarkCode")
					jsonWriter.value(remarks.attr_RemarkCode)
				}
				if(remarks.attr_RemarkType != null){
					jsonWriter.name("attr_RemarkType")
					jsonWriter.value(remarks.attr_RemarkType)
				}
				jsonWriter.name("Remarks")
				jsonWriter.value(remarks.Remarks)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(remarks.Remarks)
			}
		}
	}
}