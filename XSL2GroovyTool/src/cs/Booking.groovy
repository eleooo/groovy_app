package cs

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
class Booking implements Serializable {
	public static final Set<String> MultiElementList = ["Party", "DescriptionLine", "CargoGroup", "Confirmation"]
 	InterchangeControlHeader InterchangeControlHeader;
	List<Confirmation> Confirmation = new ArrayList<Confirmation>();
}

class InterchangeControlHeader implements Serializable {
	String ControlNumber;
	String SenderId;
	String ReceiverId;
	String DateTime;
	String ControlVersion;
	String UsageIndicator;
	String MessageSessionId;
}

class Confirmation implements Serializable {
	MessageHeader MessageHeader;
	MessageBody MessageBody;
	SummaryDetails SummaryDetails;
}

class MessageHeader implements Serializable {
	TransactionInfo TransactionInfo;
	String MessageFunction;
	String BookingNumber;
	String BookingOffice;
	String SCAC;
	StatusDate StatusDate;
	TrafficMode TrafficMode;
}

@JsonAdapter(StatusDateAdapter.class)
class StatusDate implements Serializable {
	String attr_TimeZone;
 	String StatusDate;

	@Override
	public String toString() {
		return this.StatusDate;
	}
}

class TransactionInfo implements Serializable {
	String BatchNumber;
	String MessageSender;
	String MessageRecipient;
	String MessageID;
	DateCreated DateCreated;
	String FileName;
	String Version;
}

@JsonAdapter(DateCreatedAdapter.class)
class DateCreated implements Serializable {
	String attr_TimeZone;
 	String DateCreated;

	@Override
	public String toString() {
		return this.DateCreated;
	}
}

class TrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class MessageBody implements Serializable {
	PartyInformation PartyInformation;
	ShipmentDetails ShipmentDetails;
}

class PartyInformation implements Serializable {
	List<Party> Party = new ArrayList<Party>();
}

class Party implements Serializable {
	String PartyType;
	String PartyName;
	String CustomerUserID;
	String CarrierCustomerCode;
	PartyLocation PartyLocation;
	ContactPerson ContactPerson;
}

class PartyLocation implements Serializable {
	Address Address;
	String Street;
	String City;
	String County;
	String StateProvinceCode;
	String StateProvince;
	String CountryCode;
	String CountryName;
	LocationCode LocationCode;
	String PostalCode;
}

class Address implements Serializable {
	String AddressLines;
}

class LocationCode implements Serializable {
	String MutuallyDefinedCode;
	String UNLocationCode;
	SchedKDCode SchedKDCode;
}

@JsonAdapter(SchedKDCodeAdapter.class)
class SchedKDCode implements Serializable {
	String attr_Type;
 	String SchedKDCode;

	@Override
	public String toString() {
		return this.SchedKDCode;
	}
}

class ContactPerson implements Serializable {
	String FirstName;
	String LastName;
	Phone Phone;
	Fax Fax;
	String Email;
	String Type;
}

class Phone implements Serializable {
	String CountryCode;
	String AreaCode;
	String Number;
}

class Fax implements Serializable {
	String CountryCode;
	String AreaCode;
	String Number;
}

class ShipmentDetails implements Serializable {
	RouteInformation RouteInformation;
	EquipmentInformation EquipmentInformation;
	CargoInformation CargoInformation;
	ExternalReferences ExternalReferences;
	AppointmentInformation AppointmentInformation;
	DocumentInformation DocumentInformation;
}

class RouteInformation implements Serializable {
	EventDate3 EventDate;
	Haulage Haulage;
	LocationDetails2 LocationDetails
	PortDetails PortDetails;
	StopOff StopOff;
}



@JsonAdapter(EventDateAdapter.class)
class EventDate implements Serializable {
	String attr_TimeZone;
	String EventDate;

	@Override
	public String toString() {
		return this.EventDate;
	}
}

class EventDate2 implements Serializable {
	FreeDemurrageStartDate FreeDemurrageStartDate;
	FreeDetentionStartDate FreeDetentionStartDate;
}

class EventDate3 implements Serializable {
	FullPickup FullPickup;
	FullReturnCutoff FullReturnCutoff;
	ArrivalAtFinalHub ArrivalAtFinalHub;
	DeliveryDate DeliveryDate;
	ReqDeliveryDate ReqDeliveryDate;
	VGMCutOffDate VGMCutOffDate;
}

class EventDate4 implements Serializable {
	Departure Departure;
	Arrival Arrival;
}

@JsonAdapter(ArrivalAdapter.class)
class Arrival implements Serializable {
	String attr_TimeZone;
	String Arrival;

	@Override
	public String toString() {
		return this.Arrival;
	}
}

@JsonAdapter(DepartureAdapter.class)
class Departure implements Serializable {
	String attr_TimeZone;
	String Departure;

	@Override
	public String toString() {
		return this.Departure;
	}
}



@JsonAdapter(EventDateAdapter.class)
class FreeDemurrageStartDate implements Serializable {
	String attr_TimeZone;
	String FreeDemurrageStartDate;

	@Override
	public String toString() {
		return this.FreeDemurrageStartDate;
	}
}

@JsonAdapter(EventDateAdapter.class)
class FreeDetentionStartDate implements Serializable {
	String attr_TimeZone;
	String FreeDetentionStartDate;

	@Override
	public String toString() {
		return this.FreeDetentionStartDate;
	}
}





@JsonAdapter(FullReturnCutoffAdapter.class)
class FullReturnCutoff implements Serializable {
	String attr_TimeZone;
 	String FullReturnCutoff;

	@Override
	public String toString() {
		return this.FullReturnCutoff;
	}
}

@JsonAdapter(ArrivalAtFinalHubAdapter.class)
class ArrivalAtFinalHub implements Serializable {
	String attr_TimeZone;
 	String ArrivalAtFinalHub;

	@Override
	public String toString() {
		return this.ArrivalAtFinalHub;
	}
}

@JsonAdapter(DeliveryDateAdapter.class)
class DeliveryDate implements Serializable {
	String attr_TimeZone;
 	String DeliveryDate;

	@Override
	public String toString() {
		return this.DeliveryDate;
	}
}

@JsonAdapter(ReqDeliveryDateAdapter.class)
class ReqDeliveryDate implements Serializable {
	String attr_TimeZone;
 	String ReqDeliveryDate;

	@Override
	public String toString() {
		return this.ReqDeliveryDate;
	}
}

@JsonAdapter(VGMCutOffDateAdapter.class)
class VGMCutOffDate implements Serializable {
	String attr_TimeZone;
 	String VGMCutOffDate;

	@Override
	public String toString() {
		return this.VGMCutOffDate;
	}
}

class Haulage implements Serializable {
	String Outbound;
	String Inbound;
}

class LocationDetails3 implements Serializable {
	String FunctionCode;
	String FacilityName;
	String Location;
	LocationCode LocationCode;
	Address Address;
	String CityName;
	String StateProvinceCode;
	String StateProvince;
	String County;
	String CountryCode;
	String CountryName;
	String PostalCode;
}

class LocationDetails2 implements Serializable {
	String FunctionCode;
	String LocationName;
	LocationDetails LocationDetails;
	EventDate4 EventDate;
}

class LocationDetails implements Serializable {
	Address Address;
	String Street;
	String City;
	String County;
	String StateProvinceCode;
	String StateProvince;
	String CountryCode;
	String CountryName;
	LocationCode LocationCode;
	String PostalCode;
}


class PortDetails implements Serializable {
	String FunctionCode;
	String LocationName;
	LocationDetails LocationDetails;
	EventDate4 EventDate;
	String SequenceNumber;
	VesselVoyageInformation VesselVoyageInformation;
}

class VesselVoyageInformation implements Serializable {
	String VoyageEvent;
	ServiceName ServiceName;
	String VoyageNumberDirection;
	VesselInformation VesselInformation;
}

@JsonAdapter(ServiceNameAdapter.class)
class ServiceName implements Serializable {
	String attr_Code;
 	String ServiceName;

	@Override
	public String toString() {
		return this.ServiceName;
	}
}

class VesselInformation implements Serializable {
	VesselCode VesselCode;
	String VesselName;
	String VesselRegistrationCountry;
}

@JsonAdapter(VesselCodeAdapter.class)
class VesselCode implements Serializable {
	String attr_CallSign;
 	String attr_LloydsCode;
 	String VesselCode;

	@Override
	public String toString() {
		return this.VesselCode;
	}
}

class StopOff implements Serializable {
	String SequenceNumber;
	PickupDetails PickupDetails;
	ReturnDetails ReturnDetails;
}

class PickupDetails implements Serializable {
	String FacilityCode;
	String FacilityName;
	String Location;
	LocationCode LocationCode;
	Address Address;
	String CityName;
	String StateProvinceCode;
	String StateProvince;
	String County;
	String CountryCode;
	String CountryName;
	String PostalCode;
}

class ReturnDetails implements Serializable {
	String FacilityCode;
	String FacilityName;
	String Location;
	LocationCode LocationCode;
	Address Address;
	String CityName;
	String StateProvinceCode;
	String StateProvince;
	String County;
	String CountryCode;
	String CountryName;
	String PostalCode;
}

class EquipmentInformation implements Serializable {
	Containers Containers;
	ContainerMovement ContainerMovement;
	DetentionDemurrage DetentionDemurrage;
}

class Containers implements Serializable {
	String IsSOC;
	String ContainerType;
	ContainerNumber ContainerNumber;
	SealNumber SealNumber;
	Weight Weight;
	Volume Volume;
	TrafficMode TrafficMode;
}

@JsonAdapter(ContainerNumberAdapter.class)
class ContainerNumber implements Serializable {
	String attr_CheckDigit;
 	String ContainerNumber;

	@Override
	public String toString() {
		return this.ContainerNumber;
	}
}

@JsonAdapter(SealNumberAdapter.class)
class SealNumber implements Serializable {
	String attr_Type;
 	String SealNumber;

	@Override
	public String toString() {
		return this.SealNumber;
	}
}

@JsonAdapter(WeightAdapter.class)
class Weight implements Serializable {
	String attr_Qualifier;
 	String attr_Units;
 	String Weight;

	@Override
	public String toString() {
		return this.Weight;
	}
}

@JsonAdapter(VolumeAdapter.class)
class Volume implements Serializable {
	String attr_Units;
 	String Volume;

	@Override
	public String toString() {
		return this.Volume;
	}
}

class ContainerMovement implements Serializable {
	EmptyPickup EmptyPickup;
	EmptyReturn EmptyReturn;
	FullPickup2 FullPickup;
	FullReturn FullReturn;
}

class EmptyPickup implements Serializable {
	String attr_Number;
 	String attr_Type;
	LocationDetails3 LocationDetails ;
	EventDate EventDate;
}

class EmptyReturn implements Serializable {
	String attr_Number;
 	String attr_Type;
	LocationDetails3 LocationDetails ;
	EventDate EventDate;
}

class FullPickup2 implements Serializable {
	String attr_Number;
 	String attr_Type;
	EventDate EventDate;
}

@JsonAdapter(FullPickupAdapter.class)
class FullPickup implements Serializable {
	String attr_TimeZone;
	String FullPickup;

	@Override
	public String toString() {
		return this.FullPickup;
	}
}

class FullReturn implements Serializable {
	String attr_Number;
 	String attr_Type;
	EventDate EventDate;
}

class DetentionDemurrage implements Serializable {
	String Type;
	ContainerNumber ContainerNumber;
	EventDate2 EventDate;
	FreeTime FreeTime;
}

@JsonAdapter(FreeTimeAdapter.class)
class FreeTime implements Serializable {
	String attr_Type;
 	String FreeTime;

	@Override
	public String toString() {
		return this.FreeTime;
	}
}

class CargoInformation implements Serializable {
	List<CargoGroup> CargoGroup = new ArrayList<CargoGroup>();
}

class CargoGroup implements Serializable {
	String CargoNature;
	String CommodityCode;
	Weight Weight;
	Volume Volume;
	Package Package;
	ContainerNumber ContainerNumber;
	CargoDescription CargoDescription;
	MarksAndNumbers MarksAndNumbers;
	DangerousCargo DangerousCargo;
	ReeferCargo ReeferCargo;
	AwkwardCargo AwkwardCargo;
}



class CargoDescription implements Serializable {
	List<String> DescriptionLine = new ArrayList<String>();
}

class MarksAndNumbers implements Serializable {
	String MarksAndNumbersLine;
}

class DangerousCargo implements Serializable {
	DangerousCargoInfo DangerousCargoInfo;
}

class DangerousCargoInfo implements Serializable {
	HazardousMaterial HazardousMaterial;
	ContactDetails ContactDetails;
}

class HazardousMaterial implements Serializable {
	String DGRegulator;
	String IMCOClass;
	String IMDGPage;
	String UNNumber;
	String TechnicalShippingName;
	String ProperShippingName;
	String EMSNumber;
	String PSAClass;
	String MFAGPageNumber;
	FlashPoint FlashPoint;
	DGElevationTemperature DGElevationTemperature;
	String State;
	String ApprovalCode;
	String Label;
	Weight Weight;
	NetExplosiveWeight NetExplosiveWeight;
	Indicators Indicators;
	Package2 Package;
	Remarks Remarks;
}

@JsonAdapter(FlashPointAdapter.class)
class FlashPoint implements Serializable {
	String attr_Units;
 	String FlashPoint;

	@Override
	public String toString() {
		return this.FlashPoint;
	}
}

@JsonAdapter(DGElevationTemperatureAdapter.class)
class DGElevationTemperature implements Serializable {
	String attr_Units;
 	String DGElevationTemperature;

	@Override
	public String toString() {
		return this.DGElevationTemperature;
	}
}

@JsonAdapter(NetExplosiveWeightAdapter.class)
class NetExplosiveWeight implements Serializable {
	String attr_Units;
 	String NetExplosiveWeight;

	@Override
	public String toString() {
		return this.NetExplosiveWeight;
	}
}

class Indicators implements Serializable {
	String isMarinePollutant;
	String isInhalationHazardous;
	String isLimitedQuantity;
	String isReportableQuantity;
	String isEmptyUnclean;
}

class Package2 implements Serializable {
	String PackagingGroupCode;
	InnerPackageDescription InnerPackageDescription;
	OuterPackageDescription OuterPackageDescription;
}

@JsonAdapter(PackageAdapter.class)
class Packag implements Serializable {
	String attr_Type;
	String Package;

	@Override
	public String toString() {
		return this.Package;
	}
}


@JsonAdapter(InnerPackageDescriptionAdapter.class)
class InnerPackageDescription implements Serializable {
	String attr_Type;
 	String InnerPackageDescription;

	@Override
	public String toString() {
		return this.InnerPackageDescription;
	}
}

@JsonAdapter(OuterPackageDescriptionAdapter.class)
class OuterPackageDescription implements Serializable {
	String attr_Type;
 	String OuterPackageDescription;

	@Override
	public String toString() {
		return this.OuterPackageDescription;
	}
}

class Remarks implements Serializable {
	String RemarksLines;
}

class ContactDetails implements Serializable {
	String FirstName;
	String LastName;
	Phone Phone;
	Fax Fax;
	String Email;
	String Type;
}

class ReeferCargo implements Serializable {
	ReeferCargoInfo ReeferCargoInfo;
}

class ReeferCargoInfo implements Serializable {
	ReeferSettings ReeferSettings;
	ContactDetails ContactDetails;
}

class ReeferSettings implements Serializable {
	String attr_AtmosphereType;
 	String attr_GenSetType;
 	String attr_VentSettingCode;
 	Temperature Temperature;
	Ventilation Ventilation;
	String SensitiveCargoDesc;
	Remarks Remarks;
}

@JsonAdapter(TemperatureAdapter.class)
class Temperature implements Serializable {
	String attr_Units;
 	String Temperature;

	@Override
	public String toString() {
		return this.Temperature;
	}
}

@JsonAdapter(VentilationAdapter.class)
class Ventilation implements Serializable {
	String attr_Units;
 	String Ventilation;

	@Override
	public String toString() {
		return this.Ventilation;
	}
}

class AwkwardCargo implements Serializable {
	AwkwardCargoInfo AwkwardCargoInfo;
}

class AwkwardCargoInfo implements Serializable {
	AwkwardCargoDetails AwkwardCargoDetails;
	ContactDetails ContactDetails;
}

class AwkwardCargoDetails implements Serializable {
	Height Height;
	Width Width;
	Length Length;
	Remarks Remarks;
}

@JsonAdapter(HeightAdapter.class)
class Height implements Serializable {
	String attr_Units;
 	String Height;

	@Override
	public String toString() {
		return this.Height;
	}
}

@JsonAdapter(WidthAdapter.class)
class Width implements Serializable {
	String attr_Units;
 	String Width;

	@Override
	public String toString() {
		return this.Width;
	}
}

@JsonAdapter(LengthAdapter.class)
class Length implements Serializable {
	String attr_Units;
 	String Length;

	@Override
	public String toString() {
		return this.Length;
	}
}

class ExternalReferences implements Serializable {
	References References;
}

class References implements Serializable {
	String ReferenceType;
	String ReferenceName;
	String ReferenceNumber;
}

class AppointmentInformation implements Serializable {
	AppointmentInfo AppointmentInfo;
}

class AppointmentInfo implements Serializable {
	String SequenceNumber;
	String Type;
	String CustomerSiteName;
	LocationDetails LocationDetails;
	String CargoSmartCityID;
	String ContactName;
	String ContactPhoneNumber;
	AppointmentDateTime AppointmentDateTime;
}

@JsonAdapter(AppointmentDateTimeAdapter.class)
class AppointmentDateTime implements Serializable {
	String attr_TimeZone;
 	String AppointmentDateTime;

	@Override
	public String toString() {
		return this.AppointmentDateTime;
	}
}

class DocumentInformation implements Serializable {
	RequiredDocuments RequiredDocuments;
}

class RequiredDocuments implements Serializable {
	String DocumentType;
	DueDate DueDate;
}

@JsonAdapter(DueDateAdapter.class)
class DueDate implements Serializable {
	String attr_TimeZone;
 	String DueDate;

	@Override
	public String toString() {
		return this.DueDate;
	}
}

class SummaryDetails implements Serializable {
	OtherRemarks OtherRemarks;
}

class OtherRemarks implements Serializable {
	String RemarksLines;
}



public class TemperatureAdapter extends TypeAdapter<Temperature>{
	@Override
	public Temperature read(JsonReader jsonReader) throws IOException {
		Temperature temperature = new Temperature();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'Temperature':
						temperature.Temperature = jsonReader.nextString();
						break;
					case 'attr_Units':
						temperature.attr_Units = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				temperature = new Temperature();
				temperature.Temperature = jsonReader.nextString();
			}catch(IllegalStateException e2){
				temperature = new Temperature();
				jsonReader.nextNull();
			}
		}
		return temperature
	}

	@Override
	public void write(JsonWriter jsonWriter,Temperature temperature) throws IOException {
		if(temperature?.Temperature == null){
			jsonWriter.nullValue()
		}else{
			if(temperature.attr_Units != null ){
				jsonWriter.beginObject()
				if(temperature.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(temperature.attr_Units)
				}
				jsonWriter.name("Temperature")
				jsonWriter.value(temperature.Temperature)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(temperature.Temperature)
			}
		}
	}
}

public class SchedKDCodeAdapter extends TypeAdapter<SchedKDCode>{
	@Override
	public SchedKDCode read(JsonReader jsonReader) throws IOException {
		SchedKDCode schedKDCode = new SchedKDCode();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'SchedKDCode':
						schedKDCode.SchedKDCode = jsonReader.nextString();
						break;
					case 'attr_Type':
						schedKDCode.attr_Type = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				schedKDCode = new SchedKDCode();
				schedKDCode.SchedKDCode = jsonReader.nextString();
			}catch(IllegalStateException e2){
				schedKDCode = new SchedKDCode();
				jsonReader.nextNull();
			}
		}
		return schedKDCode
	}

	@Override
	public void write(JsonWriter jsonWriter,SchedKDCode schedKDCode) throws IOException {
		if(schedKDCode?.SchedKDCode == null){
			jsonWriter.nullValue()
		}else{
			if(schedKDCode.attr_Type != null ){
				jsonWriter.beginObject()
				if(schedKDCode.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(schedKDCode.attr_Type)
				}
				jsonWriter.name("SchedKDCode")
				jsonWriter.value(schedKDCode.SchedKDCode)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(schedKDCode.SchedKDCode)
			}
		}
	}
}

public class VGMCutOffDateAdapter extends TypeAdapter<VGMCutOffDate>{
	@Override
	public VGMCutOffDate read(JsonReader jsonReader) throws IOException {
		VGMCutOffDate vGMCutOffDate = new VGMCutOffDate();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'VGMCutOffDate':
						vGMCutOffDate.VGMCutOffDate = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						vGMCutOffDate.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				vGMCutOffDate = new VGMCutOffDate();
				vGMCutOffDate.VGMCutOffDate = jsonReader.nextString();
			}catch(IllegalStateException e2){
				vGMCutOffDate = new VGMCutOffDate();
				jsonReader.nextNull();
			}
		}
		return vGMCutOffDate
	}

	@Override
	public void write(JsonWriter jsonWriter,VGMCutOffDate vGMCutOffDate) throws IOException {
		if(vGMCutOffDate?.VGMCutOffDate == null){
			jsonWriter.nullValue()
		}else{
			if(vGMCutOffDate.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(vGMCutOffDate.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(vGMCutOffDate.attr_TimeZone)
				}
				jsonWriter.name("VGMCutOffDate")
				jsonWriter.value(vGMCutOffDate.VGMCutOffDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(vGMCutOffDate.VGMCutOffDate)
			}
		}
	}
}

public class SealNumberAdapter extends TypeAdapter<SealNumber>{
	@Override
	public SealNumber read(JsonReader jsonReader) throws IOException {
		SealNumber sealNumber = new SealNumber();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'SealNumber':
						sealNumber.SealNumber = jsonReader.nextString();
						break;
					case 'attr_Type':
						sealNumber.attr_Type = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				sealNumber = new SealNumber();
				sealNumber.SealNumber = jsonReader.nextString();
			}catch(IllegalStateException e2){
				sealNumber = new SealNumber();
				jsonReader.nextNull();
			}
		}
		return sealNumber
	}

	@Override
	public void write(JsonWriter jsonWriter,SealNumber sealNumber) throws IOException {
		if(sealNumber?.SealNumber == null){
			jsonWriter.nullValue()
		}else{
			if(sealNumber.attr_Type != null ){
				jsonWriter.beginObject()
				if(sealNumber.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(sealNumber.attr_Type)
				}
				jsonWriter.name("SealNumber")
				jsonWriter.value(sealNumber.SealNumber)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(sealNumber.SealNumber)
			}
		}
	}
}

public class VesselCodeAdapter extends TypeAdapter<VesselCode>{
	@Override
	public VesselCode read(JsonReader jsonReader) throws IOException {
		VesselCode vesselCode = new VesselCode();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_CallSign':
						vesselCode.attr_CallSign = jsonReader.nextString();
						break;
					case 'attr_LloydsCode':
						vesselCode.attr_LloydsCode = jsonReader.nextString();
						break;
					case 'VesselCode':
						vesselCode.VesselCode = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				vesselCode = new VesselCode();
				vesselCode.VesselCode = jsonReader.nextString();
			}catch(IllegalStateException e2){
				vesselCode = new VesselCode();
				jsonReader.nextNull();
			}
		}
		return vesselCode
	}

	@Override
	public void write(JsonWriter jsonWriter,VesselCode vesselCode) throws IOException {
		if(vesselCode?.VesselCode == null){
			jsonWriter.nullValue()
		}else{
			if(vesselCode.attr_LloydsCode != null || vesselCode.attr_CallSign != null ){
				jsonWriter.beginObject()
				if(vesselCode.attr_LloydsCode != null){
					jsonWriter.name("attr_LloydsCode")
					jsonWriter.value(vesselCode.attr_LloydsCode)
				}
				if(vesselCode.attr_CallSign != null){
					jsonWriter.name("attr_CallSign")
					jsonWriter.value(vesselCode.attr_CallSign)
				}
				jsonWriter.name("VesselCode")
				jsonWriter.value(vesselCode.VesselCode)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(vesselCode.VesselCode)
			}
		}
	}
}

public class DateCreatedAdapter extends TypeAdapter<DateCreated>{
	@Override
	public DateCreated read(JsonReader jsonReader) throws IOException {
		DateCreated dateCreated = new DateCreated();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'DateCreated':
						dateCreated.DateCreated = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						dateCreated.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				dateCreated = new DateCreated();
				dateCreated.DateCreated = jsonReader.nextString();
			}catch(IllegalStateException e2){
				dateCreated = new DateCreated();
				jsonReader.nextNull();
			}
		}
		return dateCreated
	}

	@Override
	public void write(JsonWriter jsonWriter,DateCreated dateCreated) throws IOException {
		if(dateCreated?.DateCreated == null){
			jsonWriter.nullValue()
		}else{
			if(dateCreated.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(dateCreated.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(dateCreated.attr_TimeZone)
				}
				jsonWriter.name("DateCreated")
				jsonWriter.value(dateCreated.DateCreated)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(dateCreated.DateCreated)
			}
		}
	}
}

public class DGElevationTemperatureAdapter extends TypeAdapter<DGElevationTemperature>{
	@Override
	public DGElevationTemperature read(JsonReader jsonReader) throws IOException {
		DGElevationTemperature dGElevationTemperature = new DGElevationTemperature();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Units':
						dGElevationTemperature.attr_Units = jsonReader.nextString();
						break;
					case 'DGElevationTemperature':
						dGElevationTemperature.DGElevationTemperature = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				dGElevationTemperature = new DGElevationTemperature();
				dGElevationTemperature.DGElevationTemperature = jsonReader.nextString();
			}catch(IllegalStateException e2){
				dGElevationTemperature = new DGElevationTemperature();
				jsonReader.nextNull();
			}
		}
		return dGElevationTemperature
	}

	@Override
	public void write(JsonWriter jsonWriter,DGElevationTemperature dGElevationTemperature) throws IOException {
		if(dGElevationTemperature?.DGElevationTemperature == null){
			jsonWriter.nullValue()
		}else{
			if(dGElevationTemperature.attr_Units != null ){
				jsonWriter.beginObject()
				if(dGElevationTemperature.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(dGElevationTemperature.attr_Units)
				}
				jsonWriter.name("DGElevationTemperature")
				jsonWriter.value(dGElevationTemperature.DGElevationTemperature)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(dGElevationTemperature.DGElevationTemperature)
			}
		}
	}
}

public class DeliveryDateAdapter extends TypeAdapter<DeliveryDate>{
	@Override
	public DeliveryDate read(JsonReader jsonReader) throws IOException {
		DeliveryDate deliveryDate = new DeliveryDate();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'DeliveryDate':
						deliveryDate.DeliveryDate = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						deliveryDate.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				deliveryDate = new DeliveryDate();
				deliveryDate.DeliveryDate = jsonReader.nextString();
			}catch(IllegalStateException e2){
				deliveryDate = new DeliveryDate();
				jsonReader.nextNull();
			}
		}
		return deliveryDate
	}

	@Override
	public void write(JsonWriter jsonWriter,DeliveryDate deliveryDate) throws IOException {
		if(deliveryDate?.DeliveryDate == null){
			jsonWriter.nullValue()
		}else{
			if(deliveryDate.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(deliveryDate.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(deliveryDate.attr_TimeZone)
				}
				jsonWriter.name("DeliveryDate")
				jsonWriter.value(deliveryDate.DeliveryDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(deliveryDate.DeliveryDate)
			}
		}
	}
}

public class AppointmentDateTimeAdapter extends TypeAdapter<AppointmentDateTime>{
	@Override
	public AppointmentDateTime read(JsonReader jsonReader) throws IOException {
		AppointmentDateTime appointmentDateTime = new AppointmentDateTime();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_TimeZone':
						appointmentDateTime.attr_TimeZone = jsonReader.nextString();
						break;
					case 'AppointmentDateTime':
						appointmentDateTime.AppointmentDateTime = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				appointmentDateTime = new AppointmentDateTime();
				appointmentDateTime.AppointmentDateTime = jsonReader.nextString();
			}catch(IllegalStateException e2){
				appointmentDateTime = new AppointmentDateTime();
				jsonReader.nextNull();
			}
		}
		return appointmentDateTime
	}

	@Override
	public void write(JsonWriter jsonWriter,AppointmentDateTime appointmentDateTime) throws IOException {
		if(appointmentDateTime?.AppointmentDateTime == null){
			jsonWriter.nullValue()
		}else{
			if(appointmentDateTime.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(appointmentDateTime.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(appointmentDateTime.attr_TimeZone)
				}
				jsonWriter.name("AppointmentDateTime")
				jsonWriter.value(appointmentDateTime.AppointmentDateTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(appointmentDateTime.AppointmentDateTime)
			}
		}
	}
}

public class FreeTimeAdapter extends TypeAdapter<FreeTime>{
	@Override
	public FreeTime read(JsonReader jsonReader) throws IOException {
		FreeTime freeTime = new FreeTime();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'FreeTime':
						freeTime.FreeTime = jsonReader.nextString();
						break;
					case 'attr_Type':
						freeTime.attr_Type = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				freeTime = new FreeTime();
				freeTime.FreeTime = jsonReader.nextString();
			}catch(IllegalStateException e2){
				freeTime = new FreeTime();
				jsonReader.nextNull();
			}
		}
		return freeTime
	}

	@Override
	public void write(JsonWriter jsonWriter,FreeTime freeTime) throws IOException {
		if(freeTime?.FreeTime == null){
			jsonWriter.nullValue()
		}else{
			if(freeTime.attr_Type != null ){
				jsonWriter.beginObject()
				if(freeTime.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(freeTime.attr_Type)
				}
				jsonWriter.name("FreeTime")
				jsonWriter.value(freeTime.FreeTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(freeTime.FreeTime)
			}
		}
	}
}

public class HeightAdapter extends TypeAdapter<Height>{
	@Override
	public Height read(JsonReader jsonReader) throws IOException {
		Height height = new Height();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Units':
						height.attr_Units = jsonReader.nextString();
						break;
					case 'Height':
						height.Height = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				height = new Height();
				height.Height = jsonReader.nextString();
			}catch(IllegalStateException e2){
				height = new Height();
				jsonReader.nextNull();
			}
		}
		return height
	}

	@Override
	public void write(JsonWriter jsonWriter,Height height) throws IOException {
		if(height?.Height == null){
			jsonWriter.nullValue()
		}else{
			if(height.attr_Units != null ){
				jsonWriter.beginObject()
				if(height.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(height.attr_Units)
				}
				jsonWriter.name("Height")
				jsonWriter.value(height.Height)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(height.Height)
			}
		}
	}
}

public class DueDateAdapter extends TypeAdapter<DueDate>{
	@Override
	public DueDate read(JsonReader jsonReader) throws IOException {
		DueDate dueDate = new DueDate();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'DueDate':
						dueDate.DueDate = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						dueDate.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				dueDate = new DueDate();
				dueDate.DueDate = jsonReader.nextString();
			}catch(IllegalStateException e2){
				dueDate = new DueDate();
				jsonReader.nextNull();
			}
		}
		return dueDate
	}

	@Override
	public void write(JsonWriter jsonWriter,DueDate dueDate) throws IOException {
		if(dueDate?.DueDate == null){
			jsonWriter.nullValue()
		}else{
			if(dueDate.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(dueDate.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(dueDate.attr_TimeZone)
				}
				jsonWriter.name("DueDate")
				jsonWriter.value(dueDate.DueDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(dueDate.DueDate)
			}
		}
	}
}

public class WidthAdapter extends TypeAdapter<Width>{
	@Override
	public Width read(JsonReader jsonReader) throws IOException {
		Width width = new Width();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Units':
						width.attr_Units = jsonReader.nextString();
						break;
					case 'Width':
						width.Width = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				width = new Width();
				width.Width = jsonReader.nextString();
			}catch(IllegalStateException e2){
				width = new Width();
				jsonReader.nextNull();
			}
		}
		return width
	}

	@Override
	public void write(JsonWriter jsonWriter,Width width) throws IOException {
		if(width?.Width == null){
			jsonWriter.nullValue()
		}else{
			if(width.attr_Units != null ){
				jsonWriter.beginObject()
				if(width.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(width.attr_Units)
				}
				jsonWriter.name("Width")
				jsonWriter.value(width.Width)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(width.Width)
			}
		}
	}
}

public class FlashPointAdapter extends TypeAdapter<FlashPoint>{
	@Override
	public FlashPoint read(JsonReader jsonReader) throws IOException {
		FlashPoint flashPoint = new FlashPoint();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Units':
						flashPoint.attr_Units = jsonReader.nextString();
						break;
					case 'FlashPoint':
						flashPoint.FlashPoint = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				flashPoint = new FlashPoint();
				flashPoint.FlashPoint = jsonReader.nextString();
			}catch(IllegalStateException e2){
				flashPoint = new FlashPoint();
				jsonReader.nextNull();
			}
		}
		return flashPoint
	}

	@Override
	public void write(JsonWriter jsonWriter,FlashPoint flashPoint) throws IOException {
		if(flashPoint?.FlashPoint == null){
			jsonWriter.nullValue()
		}else{
			if(flashPoint.attr_Units != null ){
				jsonWriter.beginObject()
				if(flashPoint.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(flashPoint.attr_Units)
				}
				jsonWriter.name("FlashPoint")
				jsonWriter.value(flashPoint.FlashPoint)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(flashPoint.FlashPoint)
			}
		}
	}
}

public class ContainerNumberAdapter extends TypeAdapter<ContainerNumber>{
	@Override
	public ContainerNumber read(JsonReader jsonReader) throws IOException {
		ContainerNumber containerNumber = new ContainerNumber();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_CheckDigit':
						containerNumber.attr_CheckDigit = jsonReader.nextString();
						break;
					case 'ContainerNumber':
						containerNumber.ContainerNumber = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				containerNumber = new ContainerNumber();
				containerNumber.ContainerNumber = jsonReader.nextString();
			}catch(IllegalStateException e2){
				containerNumber = new ContainerNumber();
				jsonReader.nextNull();
			}
		}
		return containerNumber
	}

	@Override
	public void write(JsonWriter jsonWriter,ContainerNumber containerNumber) throws IOException {
		if(containerNumber?.ContainerNumber == null){
			jsonWriter.nullValue()
		}else{
			if(containerNumber.attr_CheckDigit != null ){
				jsonWriter.beginObject()
				if(containerNumber.attr_CheckDigit != null){
					jsonWriter.name("attr_CheckDigit")
					jsonWriter.value(containerNumber.attr_CheckDigit)
				}
				jsonWriter.name("ContainerNumber")
				jsonWriter.value(containerNumber.ContainerNumber)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(containerNumber.ContainerNumber)
			}
		}
	}
}

public class ReqDeliveryDateAdapter extends TypeAdapter<ReqDeliveryDate>{
	@Override
	public ReqDeliveryDate read(JsonReader jsonReader) throws IOException {
		ReqDeliveryDate reqDeliveryDate = new ReqDeliveryDate();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'ReqDeliveryDate':
						reqDeliveryDate.ReqDeliveryDate = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						reqDeliveryDate.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				reqDeliveryDate = new ReqDeliveryDate();
				reqDeliveryDate.ReqDeliveryDate = jsonReader.nextString();
			}catch(IllegalStateException e2){
				reqDeliveryDate = new ReqDeliveryDate();
				jsonReader.nextNull();
			}
		}
		return reqDeliveryDate
	}

	@Override
	public void write(JsonWriter jsonWriter,ReqDeliveryDate reqDeliveryDate) throws IOException {
		if(reqDeliveryDate?.ReqDeliveryDate == null){
			jsonWriter.nullValue()
		}else{
			if(reqDeliveryDate.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(reqDeliveryDate.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(reqDeliveryDate.attr_TimeZone)
				}
				jsonWriter.name("ReqDeliveryDate")
				jsonWriter.value(reqDeliveryDate.ReqDeliveryDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(reqDeliveryDate.ReqDeliveryDate)
			}
		}
	}
}

public class EventDateAdapter extends TypeAdapter<EventDate>{
	@Override
	public EventDate read(JsonReader jsonReader) throws IOException {
		EventDate eventDate = new EventDate();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'EventDate':
						eventDate.EventDate = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						eventDate.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				eventDate = new EventDate();
				eventDate.EventDate = jsonReader.nextString();
			}catch(IllegalStateException e2){
				eventDate = new EventDate();
				jsonReader.nextNull();
			}
		}
		return eventDate
	}

	@Override
	public void write(JsonWriter jsonWriter,EventDate eventDate) throws IOException {
		if(eventDate?.EventDate == null){
			jsonWriter.nullValue()
		}else{
			if(eventDate.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(eventDate.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(eventDate.attr_TimeZone)
				}
				jsonWriter.name("EventDate")
				jsonWriter.value(eventDate.EventDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(eventDate.EventDate)
			}
		}
	}
}

public class ArrivalAdapter extends TypeAdapter<Arrival>{
	@Override
	public Arrival read(JsonReader jsonReader) throws IOException {
		Arrival arrival = new Arrival();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'EventDate':
						arrival.Arrival = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						arrival.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				arrival = new Arrival();
				arrival.Arrival = jsonReader.nextString();
			}catch(IllegalStateException e2){
				arrival = new Arrival();
				jsonReader.nextNull();
			}
		}
		return arrival
	}

	@Override
	public void write(JsonWriter jsonWriter,Arrival arrival) throws IOException {
		if(arrival?.Arrival == null){
			jsonWriter.nullValue()
		}else{
			if(arrival.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(arrival.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(arrival.attr_TimeZone)
				}
				jsonWriter.name("EventDate")
				jsonWriter.value(arrival.Arrival)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(arrival.Arrival)
			}
		}
	}
}


public class DepartureAdapter extends TypeAdapter<Departure>{
	@Override
	public Departure read(JsonReader jsonReader) throws IOException {
		Departure departure = new Departure();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'Departure':
						departure.Departure = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						departure.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				departure = new Departure();
				departure.Departure = jsonReader.nextString();
			}catch(IllegalStateException e2){
				departure = new Departure();
				jsonReader.nextNull();
			}
		}
		return departure
	}

	@Override
	public void write(JsonWriter jsonWriter,Departure departure) throws IOException {
		if(departure?.Departure == null){
			jsonWriter.nullValue()
		}else{
			if(departure.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(departure.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(departure.attr_TimeZone)
				}
				jsonWriter.name("Departure")
				jsonWriter.value(departure.Departure)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(departure.Departure)
			}
		}
	}
}





public class FullReturnCutoffAdapter extends TypeAdapter<FullReturnCutoff>{
	@Override
	public FullReturnCutoff read(JsonReader jsonReader) throws IOException {
		FullReturnCutoff fullReturnCutoff = new FullReturnCutoff();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'FullReturnCutoff':
						fullReturnCutoff.FullReturnCutoff = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						fullReturnCutoff.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				fullReturnCutoff = new FullReturnCutoff();
				fullReturnCutoff.FullReturnCutoff = jsonReader.nextString();
			}catch(IllegalStateException e2){
				fullReturnCutoff = new FullReturnCutoff();
				jsonReader.nextNull();
			}
		}
		return fullReturnCutoff
	}

	@Override
	public void write(JsonWriter jsonWriter,FullReturnCutoff fullReturnCutoff) throws IOException {
		if(fullReturnCutoff?.FullReturnCutoff == null){
			jsonWriter.nullValue()
		}else{
			if(fullReturnCutoff.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(fullReturnCutoff.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(fullReturnCutoff.attr_TimeZone)
				}
				jsonWriter.name("FullReturnCutoff")
				jsonWriter.value(fullReturnCutoff.FullReturnCutoff)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(fullReturnCutoff.FullReturnCutoff)
			}
		}
	}
}

public class OuterPackageDescriptionAdapter extends TypeAdapter<OuterPackageDescription>{
	@Override
	public OuterPackageDescription read(JsonReader jsonReader) throws IOException {
		OuterPackageDescription outerPackageDescription = new OuterPackageDescription();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Type':
						outerPackageDescription.attr_Type = jsonReader.nextString();
						break;
					case 'OuterPackageDescription':
						outerPackageDescription.OuterPackageDescription = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				outerPackageDescription = new OuterPackageDescription();
				outerPackageDescription.OuterPackageDescription = jsonReader.nextString();
			}catch(IllegalStateException e2){
				outerPackageDescription = new OuterPackageDescription();
				jsonReader.nextNull();
			}
		}
		return outerPackageDescription
	}

	@Override
	public void write(JsonWriter jsonWriter,OuterPackageDescription outerPackageDescription) throws IOException {
		if(outerPackageDescription?.OuterPackageDescription == null){
			jsonWriter.nullValue()
		}else{
			if(outerPackageDescription.attr_Type != null ){
				jsonWriter.beginObject()
				if(outerPackageDescription.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(outerPackageDescription.attr_Type)
				}
				jsonWriter.name("OuterPackageDescription")
				jsonWriter.value(outerPackageDescription.OuterPackageDescription)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(outerPackageDescription.OuterPackageDescription)
			}
		}
	}
}

public class WeightAdapter extends TypeAdapter<Weight>{
	@Override
	public Weight read(JsonReader jsonReader) throws IOException {
		Weight weight = new Weight();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Qualifier':
						weight.attr_Qualifier = jsonReader.nextString();
						break;
					case 'attr_Units':
						weight.attr_Units = jsonReader.nextString();
						break;
					case 'Weight':
						weight.Weight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				weight = new Weight();
				weight.Weight = jsonReader.nextString();
			}catch(IllegalStateException e2){
				weight = new Weight();
				jsonReader.nextNull();
			}
		}
		return weight
	}

	@Override
	public void write(JsonWriter jsonWriter,Weight weight) throws IOException {
		if(weight?.Weight == null){
			jsonWriter.nullValue()
		}else{
			if(weight.attr_Qualifier != null || weight.attr_Units != null ){
				jsonWriter.beginObject()
				if(weight.attr_Qualifier != null){
					jsonWriter.name("attr_Qualifier")
					jsonWriter.value(weight.attr_Qualifier)
				}
				if(weight.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(weight.attr_Units)
				}
				jsonWriter.name("Weight")
				jsonWriter.value(weight.Weight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(weight.Weight)
			}
		}
	}
}

public class ArrivalAtFinalHubAdapter extends TypeAdapter<ArrivalAtFinalHub>{
	@Override
	public ArrivalAtFinalHub read(JsonReader jsonReader) throws IOException {
		ArrivalAtFinalHub arrivalAtFinalHub = new ArrivalAtFinalHub();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'ArrivalAtFinalHub':
						arrivalAtFinalHub.ArrivalAtFinalHub = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						arrivalAtFinalHub.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				arrivalAtFinalHub = new ArrivalAtFinalHub();
				arrivalAtFinalHub.ArrivalAtFinalHub = jsonReader.nextString();
			}catch(IllegalStateException e2){
				arrivalAtFinalHub = new ArrivalAtFinalHub();
				jsonReader.nextNull();
			}
		}
		return arrivalAtFinalHub
	}

	@Override
	public void write(JsonWriter jsonWriter,ArrivalAtFinalHub arrivalAtFinalHub) throws IOException {
		if(arrivalAtFinalHub?.ArrivalAtFinalHub == null){
			jsonWriter.nullValue()
		}else{
			if(arrivalAtFinalHub.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(arrivalAtFinalHub.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(arrivalAtFinalHub.attr_TimeZone)
				}
				jsonWriter.name("ArrivalAtFinalHub")
				jsonWriter.value(arrivalAtFinalHub.ArrivalAtFinalHub)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(arrivalAtFinalHub.ArrivalAtFinalHub)
			}
		}
	}
}

public class StatusDateAdapter extends TypeAdapter<StatusDate>{
	@Override
	public StatusDate read(JsonReader jsonReader) throws IOException {
		StatusDate statusDate = new StatusDate();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'StatusDate':
						statusDate.StatusDate = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						statusDate.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				statusDate = new StatusDate();
				statusDate.StatusDate = jsonReader.nextString();
			}catch(IllegalStateException e2){
				statusDate = new StatusDate();
				jsonReader.nextNull();
			}
		}
		return statusDate
	}

	@Override
	public void write(JsonWriter jsonWriter,StatusDate statusDate) throws IOException {
		if(statusDate?.StatusDate == null){
			jsonWriter.nullValue()
		}else{
			if(statusDate.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(statusDate.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(statusDate.attr_TimeZone)
				}
				jsonWriter.name("StatusDate")
				jsonWriter.value(statusDate.StatusDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(statusDate.StatusDate)
			}
		}
	}
}

public class ServiceNameAdapter extends TypeAdapter<ServiceName>{
	@Override
	public ServiceName read(JsonReader jsonReader) throws IOException {
		ServiceName serviceName = new ServiceName();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'ServiceName':
						serviceName.ServiceName = jsonReader.nextString();
						break;
					case 'attr_Code':
						serviceName.attr_Code = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				serviceName = new ServiceName();
				serviceName.ServiceName = jsonReader.nextString();
			}catch(IllegalStateException e2){
				serviceName = new ServiceName();
				jsonReader.nextNull();
			}
		}
		return serviceName
	}

	@Override
	public void write(JsonWriter jsonWriter,ServiceName serviceName) throws IOException {
		if(serviceName?.ServiceName == null){
			jsonWriter.nullValue()
		}else{
			if(serviceName.attr_Code != null ){
				jsonWriter.beginObject()
				if(serviceName.attr_Code != null){
					jsonWriter.name("attr_Code")
					jsonWriter.value(serviceName.attr_Code)
				}
				jsonWriter.name("ServiceName")
				jsonWriter.value(serviceName.ServiceName)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(serviceName.ServiceName)
			}
		}
	}
}

public class VolumeAdapter extends TypeAdapter<Volume>{
	@Override
	public Volume read(JsonReader jsonReader) throws IOException {
		Volume volume = new Volume();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Units':
						volume.attr_Units = jsonReader.nextString();
						break;
					case 'Volume':
						volume.Volume = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				volume = new Volume();
				volume.Volume = jsonReader.nextString();
			}catch(IllegalStateException e2){
				volume = new Volume();
				jsonReader.nextNull();
			}
		}
		return volume
	}

	@Override
	public void write(JsonWriter jsonWriter,Volume volume) throws IOException {
		if(volume?.Volume == null){
			jsonWriter.nullValue()
		}else{
			if(volume.attr_Units != null ){
				jsonWriter.beginObject()
				if(volume.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(volume.attr_Units)
				}
				jsonWriter.name("Volume")
				jsonWriter.value(volume.Volume)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(volume.Volume)
			}
		}
	}
}

public class LengthAdapter extends TypeAdapter<Length>{
	@Override
	public Length read(JsonReader jsonReader) throws IOException {
		Length length = new Length();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Units':
						length.attr_Units = jsonReader.nextString();
						break;
					case 'Length':
						length.Length = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				length = new Length();
				length.Length = jsonReader.nextString();
			}catch(IllegalStateException e2){
				length = new Length();
				jsonReader.nextNull();
			}
		}
		return length
	}

	@Override
	public void write(JsonWriter jsonWriter,Length length) throws IOException {
		if(length?.Length == null){
			jsonWriter.nullValue()
		}else{
			if(length.attr_Units != null ){
				jsonWriter.beginObject()
				if(length.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(length.attr_Units)
				}
				jsonWriter.name("Length")
				jsonWriter.value(length.Length)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(length.Length)
			}
		}
	}
}

public class InnerPackageDescriptionAdapter extends TypeAdapter<InnerPackageDescription>{
	@Override
	public InnerPackageDescription read(JsonReader jsonReader) throws IOException {
		InnerPackageDescription innerPackageDescription = new InnerPackageDescription();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'InnerPackageDescription':
						innerPackageDescription.InnerPackageDescription = jsonReader.nextString();
						break;
					case 'attr_Type':
						innerPackageDescription.attr_Type = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				innerPackageDescription = new InnerPackageDescription();
				innerPackageDescription.InnerPackageDescription = jsonReader.nextString();
			}catch(IllegalStateException e2){
				innerPackageDescription = new InnerPackageDescription();
				jsonReader.nextNull();
			}
		}
		return innerPackageDescription
	}

	@Override
	public void write(JsonWriter jsonWriter,InnerPackageDescription innerPackageDescription) throws IOException {
		if(innerPackageDescription?.InnerPackageDescription == null){
			jsonWriter.nullValue()
		}else{
			if(innerPackageDescription.attr_Type != null ){
				jsonWriter.beginObject()
				if(innerPackageDescription.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(innerPackageDescription.attr_Type)
				}
				jsonWriter.name("InnerPackageDescription")
				jsonWriter.value(innerPackageDescription.InnerPackageDescription)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(innerPackageDescription.InnerPackageDescription)
			}
		}
	}
}

public class VentilationAdapter extends TypeAdapter<Ventilation>{
	@Override
	public Ventilation read(JsonReader jsonReader) throws IOException {
		Ventilation ventilation = new Ventilation();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Units':
						ventilation.attr_Units = jsonReader.nextString();
						break;
					case 'Ventilation':
						ventilation.Ventilation = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				ventilation = new Ventilation();
				ventilation.Ventilation = jsonReader.nextString();
			}catch(IllegalStateException e2){
				ventilation = new Ventilation();
				jsonReader.nextNull();
			}
		}
		return ventilation
	}

	@Override
	public void write(JsonWriter jsonWriter,Ventilation ventilation) throws IOException {
		if(ventilation?.Ventilation == null){
			jsonWriter.nullValue()
		}else{
			if(ventilation.attr_Units != null ){
				jsonWriter.beginObject()
				if(ventilation.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(ventilation.attr_Units)
				}
				jsonWriter.name("Ventilation")
				jsonWriter.value(ventilation.Ventilation)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(ventilation.Ventilation)
			}
		}
	}
}

public class PackageAdapter extends TypeAdapter<Package>{
	@Override
	public Package read(JsonReader jsonReader) throws IOException {
		Package package1 = new Package();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'Package':
						package1.Package = jsonReader.nextString();
						break;
					case 'attr_Type':
						package1.attr_Type = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				package1 = new Package();
				package1.Package = jsonReader.nextString();
			}catch(IllegalStateException e2){
				package1 = new Package();
				jsonReader.nextNull();
			}
		}
		return package1
	}

	@Override
	public void write(JsonWriter jsonWriter,Package package1) throws IOException {
		if(package1?.Package == null){
			jsonWriter.nullValue()
		}else{
			if(package1.attr_Type != null ){
				jsonWriter.beginObject()
				if(package1.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(package1.attr_Type)
				}
				jsonWriter.name("Package")
				jsonWriter.value(package1.Package)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(package1.Package)
			}
		}
	}
}

public class NetExplosiveWeightAdapter extends TypeAdapter<NetExplosiveWeight>{
	@Override
	public NetExplosiveWeight read(JsonReader jsonReader) throws IOException {
		NetExplosiveWeight netExplosiveWeight = new NetExplosiveWeight();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Units':
						netExplosiveWeight.attr_Units = jsonReader.nextString();
						break;
					case 'NetExplosiveWeight':
						netExplosiveWeight.NetExplosiveWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				netExplosiveWeight = new NetExplosiveWeight();
				netExplosiveWeight.NetExplosiveWeight = jsonReader.nextString();
			}catch(IllegalStateException e2){
				netExplosiveWeight = new NetExplosiveWeight();
				jsonReader.nextNull();
			}
		}
		return netExplosiveWeight
	}

	@Override
	public void write(JsonWriter jsonWriter,NetExplosiveWeight netExplosiveWeight) throws IOException {
		if(netExplosiveWeight?.NetExplosiveWeight == null){
			jsonWriter.nullValue()
		}else{
			if(netExplosiveWeight.attr_Units != null ){
				jsonWriter.beginObject()
				if(netExplosiveWeight.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(netExplosiveWeight.attr_Units)
				}
				jsonWriter.name("NetExplosiveWeight")
				jsonWriter.value(netExplosiveWeight.NetExplosiveWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(netExplosiveWeight.NetExplosiveWeight)
			}
		}
	}
}

public class FullPickupAdapter extends TypeAdapter<FullPickup>{
	@Override
	public FullPickup read(JsonReader jsonReader) throws IOException {
		FullPickup fullPickup = new FullPickup();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_TimeZone':
						fullPickup.attr_TimeZone = jsonReader.nextString();
						break;
					case 'FullPickup':
						fullPickup.FullPickup = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				fullPickup = new FullPickup();
				fullPickup.FullPickup = jsonReader.nextString();
			}catch(IllegalStateException e2){
				fullPickup = new FullPickup();
				jsonReader.nextNull();
			}
		}
		return fullPickup
	}

	@Override
	public void write(JsonWriter jsonWriter,FullPickup fullPickup) throws IOException {
		if(fullPickup?.FullPickup == null){
			jsonWriter.nullValue()
		}else{
			if(fullPickup.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(fullPickup.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(fullPickup.attr_TimeZone)
				}
				jsonWriter.name("FullPickup")
				jsonWriter.value(fullPickup.FullPickup)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(fullPickup.FullPickup)
			}
		}
	}
}

