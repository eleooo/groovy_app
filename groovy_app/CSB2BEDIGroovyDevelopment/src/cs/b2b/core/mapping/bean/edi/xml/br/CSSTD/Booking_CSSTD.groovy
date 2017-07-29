package cs.b2b.core.mapping.bean.edi.xml.br.CSSTD

import java.io.IOException;

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class Booking_CSSTD implements Serializable {
	public static final Set<String> MultiElementList = ['References', 'AddressLines', 'Party', 'Intermodal.Location', 'SummaryDetails.Remarks','DangerousCargoInfo.EmergencyContact','RouteInformation.Location', 'Intermodal', 'Appointment', 'Weight', 'Containers', 'RemarksLines', 'DangerousCargoInfo', 'AwkwardCargoInfo', 'CargoDetails', 'SpecialHandling', 'Request'];
	String attr_noNamespaceSchemaLocation;
	InterchangeControlHeader InterchangeControlHeader;
	List<Request> Request = new ArrayList<Request>();
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

class Request implements Serializable {
	GeneralInfo GeneralInfo;
	ShipmentDetails ShipmentDetails;
	SummaryDetails SummaryDetails;
}

class GeneralInfo implements Serializable {
	TransactionInfo TransactionInfo;
	String ActionType;
	String CSReferenceNumber;
	String BookingNumber;
	String SCAC;
	Offices Offices;
	Amended Amended;
	Requested Requested;
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

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.DateCreatedAdapter.class)
class DateCreated implements Serializable {
	String attr_TimeZone;
	String DateCreated;

	@Override
	public String toString() {
		return this.DateCreated;
	}
}

class Offices implements Serializable {
	String BookingRegion;
	String BookingOffice;
	String BookingOfficeName;
	String BLIssuingOffice;
	String CargoOrigin;
}

class Amended implements Serializable {
	String NotificationEmailID;
	String By;
	Date Date;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.DateAdapter.class)
class Date implements Serializable {
	String attr_TimeZone;
	String Date;

	@Override
	public String toString() {
		return this.Date;
	}
}

class Requested implements Serializable {
	String NotificationEmailID;
	String By;
	Date Date;
}

class ShipmentDetails implements Serializable {
	UserReferences UserReferences;
	LegalParties LegalParties;
	RouteInformation RouteInformation;
	EquipmentInformation EquipmentInformation;
	CargoInformation CargoInformation;
}

class UserReferences implements Serializable {
	List<References> References = new ArrayList<References>();
}

class References implements Serializable {
	String ReferenceType;
	String ReferenceNumber;
	String ReferenceDescription;
}

class LegalParties implements Serializable {
	List<Party> Party = new ArrayList<Party>();
}

class Party implements Serializable {
	String PartyType;
	String PartyName;
	String CustomerUserID;
	String CarrierCustomerCode;
	PartyLocation PartyLocation;
	ContactPerson ContactPerson;
	String SalesOfficeCode;
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
	List<String> AddressLines = new ArrayList<String>();
}

class LocationCode implements Serializable {
	String MutuallyDefinedCode;
	String UNLocationCode;
	SchedKDCode SchedKDCode;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.SchedKDCodeAdapter.class)
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

class RouteInformation implements Serializable {
	IntendedDates IntendedDates;
	List<Location> Location = new ArrayList<Location>();
	List<Intermodal> Intermodal = new ArrayList<Intermodal>();
}

class IntendedDates implements Serializable {
	From From;
	To To;
	String Range;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.FromAdapter.class)
class From implements Serializable {
	String attr_TimeZone;
	String From;

	@Override
	public String toString() {
		return this.From;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.ToAdapter.class)
class To implements Serializable {
	String attr_TimeZone;
	String To;

	@Override
	public String toString() {
		return this.To;
	}
}

class Location implements Serializable {
	String FunctionCode;
	String LocationName;
	LocationDetails LocationDetails;
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

class Intermodal implements Serializable {
	VesselVoyageInformation VesselVoyageInformation;
	List<Location> Location = new ArrayList<Location>();
}


class VesselVoyageInformation implements Serializable {
	ServiceName ServiceName;
	String VoyageNumberDirection;
	VesselInformation VesselInformation;
	External External;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.ServiceNameAdapter.class)
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

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.VesselCodeAdapter.class)
class VesselCode implements Serializable {
	String attr_CallSign;
	String attr_LloydsCode;
	String VesselCode;

	@Override
	public String toString() {
		return this.VesselCode;
	}
}

class External implements Serializable {
	VesselCode VesselCode;
	String VoyageNumber;
}

class EquipmentInformation implements Serializable {
	List<Containers> Containers = new ArrayList<Containers>();
}

class Containers implements Serializable {
	String ContainerType;
	String Quantity;
	List<Weight> Weight = new ArrayList<Weight>();
	String SOCIndicator;
	OutBound OutBound;
	InBound InBound;
}

class Weight implements Serializable {
	String attr_Qualifier;
	String attr_Units;
	String Weight;
}

class OutBound implements Serializable {
	EmptyPickupDate EmptyPickupDate;
	FullReturnDate FullReturnDate;
	List<Appointment> Appointment = new ArrayList<Appointment>();
	String isMerchantHaulage;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.EmptyPickupDateAdapter.class)
class EmptyPickupDate implements Serializable {
	String attr_TimeZone;
	String EmptyPickupDate;

	@Override
	public String toString() {
		return this.EmptyPickupDate;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.FullReturnDateAdapter.class)
class FullReturnDate implements Serializable {
	String attr_TimeZone;
	String FullReturnDate;

	@Override
	public String toString() {
		return this.FullReturnDate;
	}
}

class Appointment implements Serializable {
	String Quantity;
	String Company;
	LocationDetails Location;
	ContactPerson ContactPerson;
	AppointmentDate AppointmentDate;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.AppointmentDateAdapter.class)
class AppointmentDate implements Serializable {
	String attr_TimeZone;
	String AppointmentDate;

	@Override
	public String toString() {
		return this.AppointmentDate;
	}
}

class InBound implements Serializable {
	EmptyReturnDate EmptyReturnDate;
	DestinationDateTime DestinationDateTime;
	String isMerchantHaulage;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.EmptyReturnDateAdapter.class)
class EmptyReturnDate implements Serializable {
	String attr_TimeZone;
	String EmptyReturnDate;

	@Override
	public String toString() {
		return this.EmptyReturnDate;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.DestinationDateTimeAdapter.class)
class DestinationDateTime implements Serializable {
	String attr_TimeZone;
	String DestinationDateTime;

	@Override
	public String toString() {
		return this.DestinationDateTime;
	}
}

class CargoInformation implements Serializable {
	List<CargoDetails> CargoDetails = new ArrayList<CargoDetails>();
}

class CargoDetails implements Serializable {
	String CargoNature;
	String CargoDescription;
	Commodity Commodity;
	Packaging Packaging;
	List<Weight> Weight = new ArrayList<Weight>();
	String HarmonizedTariffSchedule;
	Remarks Remarks;
	TrafficMode TrafficMode;
	ReeferSettings ReeferSettings;
	DangerousCargo DangerousCargo;
	AwkwardCargo AwkwardCargo;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.CommodityAdapter.class)
class Commodity implements Serializable {
	String attr_Group;
	String Commodity;

	@Override
	public String toString() {
		return this.Commodity;
	}
}

class Packaging implements Serializable {
	String PackageType;
	String Quantity;
	String Material;
}

class Remarks implements Serializable {
	List<String> RemarksLines = new ArrayList<String>();
}

class TrafficMode implements Serializable {
	String Description;
	String OutBound;
	String InBound;
}

class ReeferSettings implements Serializable {
	String attr_AtmosphereType;
	String attr_CO2;
	String attr_DehumidityInd;
	String attr_GenSetType;
	String attr_O2;
	String attr_PreCooling;
	String attr_SensitiveCargoInd;
	String attr_VentSettingCode;
	Temperature Temperature;
	Ventilation Ventilation;
	String DehumidityPercentage;
	String SensitiveCargoDesc;
	Remarks Remarks;
	EmergencyContact EmergencyContact;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.TemperatureAdapter.class)
class Temperature implements Serializable {
	String attr_Units;
	String Temperature;

	@Override
	public String toString() {
		return this.Temperature;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.VentilationAdapter.class)
class Ventilation implements Serializable {
	String attr_Units;
	String Ventilation;

	@Override
	public String toString() {
		return this.Ventilation;
	}
}

class EmergencyContact implements Serializable {
	String FirstName;
	String LastName;
	Phone Phone;
	Fax Fax;
	String Email;
	String Type;
}

class DangerousCargo implements Serializable {
	List<DangerousCargoInfo> DangerousCargoInfo = new ArrayList<DangerousCargoInfo>();
}

class DangerousCargoInfo implements Serializable {
	HazardousMaterial HazardousMaterial;
	List<EmergencyContact> EmergencyContact = new ArrayList<EmergencyContact>();
}

class HazardousMaterial implements Serializable {
	String IMCOClass;
	String IMCOPage;
	String UNNumber;
	FlashPoint FlashPoint;
	String MFAGTableNumber;
	String MaterialDescription;
	String EmergencyResponseCode;
	String EMSNumber;
	String ProperShippingName;
	String TechnicalShippingName;
	DGElevationTemperature DGElevationTemperature;
	List<Weight> Weight = new ArrayList<Weight>();
	Indicators Indicators;
	Package Package;
	Remarks Remarks;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.FlashPointAdapter.class)
class FlashPoint implements Serializable {
	String attr_Units;
	String FlashPoint;

	@Override
	public String toString() {
		return this.FlashPoint;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.DGElevationTemperatureAdapter.class)
class DGElevationTemperature implements Serializable {
	String attr_Units;
	String DGElevationTemperature;

	@Override
	public String toString() {
		return this.DGElevationTemperature;
	}
}

class Indicators implements Serializable {
	String IsReportableQuantity;
	String IsEmptyUnclean;
	String IsLimitedQuantity;
	String IsMarinePollutant;
	String IsInhalationHazardous;
}

class Package implements Serializable {
	String PackagingGroupCode;
	InnerPackaging InnerPackaging;
	OuterPackaging OuterPackaging;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.InnerPackagingAdapter.class)
class InnerPackaging implements Serializable {
	String attr_Units;
	String InnerPackaging;

	@Override
	public String toString() {
		return this.InnerPackaging;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.OuterPackagingAdapter.class)
class OuterPackaging implements Serializable {
	String attr_Units;
	String OuterPackaging;

	@Override
	public String toString() {
		return this.OuterPackaging;
	}
}

class AwkwardCargo implements Serializable {
	List<AwkwardCargoInfo> AwkwardCargoInfo = new ArrayList<AwkwardCargoInfo>();
}

class AwkwardCargoInfo implements Serializable {
	AwkwardCargoDetails AwkwardCargoDetails;
}

class AwkwardCargoDetails implements Serializable {
	Height Height;
	Width Width;
	Length Length;
	Remarks Remarks;
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.HeightAdapter.class)
class Height implements Serializable {
	String attr_Units;
	String Height;

	@Override
	public String toString() {
		return this.Height;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.WidthAdapter.class)
class Width implements Serializable {
	String attr_Units;
	String Width;

	@Override
	public String toString() {
		return this.Width;
	}
}

@JsonAdapter(cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.LengthAdapter.class)
class Length implements Serializable {
	String attr_Units;
	String Length;

	@Override
	public String toString() {
		return this.Length;
	}
}

class SummaryDetails implements Serializable {
	List<SummaryDetails_Remarks> Remarks = new ArrayList<Remarks>();
	List<SpecialHandling> SpecialHandling = new ArrayList<SpecialHandling>();
}

class SummaryDetails_Remarks implements Serializable {
	String Type;
	Remarks Content;
}
class SpecialHandling implements Serializable {
	String Code;
	String SpecialInstructions;
}


class DateCreatedAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.DateCreated> {
	
	@Override
	public DateCreated read(JsonReader jsonReader) throws IOException {
		
		DateCreated object = new DateCreated();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_TimeZone":
						object.attr_TimeZone = jsonReader.nextString();
					break;
					case "DateCreated":
						object.DateCreated = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new DateCreated();
				object.setDateCreated(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new DateCreated();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, DateCreated object) throws IOException {
		if(object?.DateCreated == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(object.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(object.attr_TimeZone)
				}
				jsonWriter.name("DateCreated")
				jsonWriter.value(object.DateCreated)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.DateCreated)
			}
		}
	}
}

class DateAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Date> {
	
	@Override
	public Date read(JsonReader jsonReader) throws IOException {
		
		Date object = new Date()

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_TimeZone":
						object.attr_TimeZone = jsonReader.nextString();
					break;
					case "Date":
						object.Date = jsonReader.nextString();
					break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Date();
				object.setDate(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Date();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Date object) throws IOException {
		if(object?.Date == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(object.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(object.attr_TimeZone)
				}
				jsonWriter.name("Date")
				jsonWriter.value(object.Date)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Date)
			}
		}
	}
}

class SchedKDCodeAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.SchedKDCode> {
	
	@Override
	public SchedKDCode read(JsonReader jsonReader) throws IOException {
		
		SchedKDCode object = new SchedKDCode();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;
						case "SchedKDCode":
						object.SchedKDCode = jsonReader.nextString();
					break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new SchedKDCode();
				object.setSchedKDCode(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new SchedKDCode();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, SchedKDCode object) throws IOException {
		if(object?.SchedKDCode == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()
				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}
				jsonWriter.name("SchedKDCode")
				jsonWriter.value(object.SchedKDCode)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.SchedKDCode)
			}
		}
	}
}


class FromAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.From> {
	
	@Override
	public From read(JsonReader jsonReader) throws IOException {
		
		From object = new From();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_TimeZone":
						object.attr_TimeZone = jsonReader.nextString();
						break;
						case "From":
						object.From = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new From();
				object.setFrom(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new From();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, From object) throws IOException {
		if(object?.From == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(object.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(object.attr_TimeZone)
				}
				jsonWriter.name("From")
				jsonWriter.value(object.From)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.From)
			}
		}
	}
}

class ToAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.To> {
	
	@Override
	public To read(JsonReader jsonReader) throws IOException {
		
		To object = new To();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_TimeZone":
						object.attr_TimeZone = jsonReader.nextString();
						break;
						case "To":
						object.To = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new To();
				object.setTo(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new To();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, To object) throws IOException {
		if(object?.To == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(object.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(object.attr_TimeZone)
				}
				jsonWriter.name("To")
				jsonWriter.value(object.To)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.To)
			}
		}
	}
}
class ServiceNameAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.ServiceName> {
	
	@Override
	public ServiceName read(JsonReader jsonReader) throws IOException {
		
		ServiceName object = new ServiceName();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Code":
						object.attr_Code = jsonReader.nextString();
						break;
						case "ServiceName":
						object.ServiceName = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ServiceName();
				object.setServiceName(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ServiceName();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, ServiceName object) throws IOException {
		if(object?.ServiceName == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Code != null ){
				jsonWriter.beginObject()
				if(object.attr_Code != null){
					jsonWriter.name("attr_Code")
					jsonWriter.value(object.attr_Code)
				}
				jsonWriter.name("ServiceName")
				jsonWriter.value(object.ServiceName)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ServiceName)
			}
		}
	}
}

class VesselCodeAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.VesselCode> {
	
	@Override
	public VesselCode read(JsonReader jsonReader) throws IOException {
		
		VesselCode object = new VesselCode();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_CallSign":
						object.attr_CallSign = jsonReader.nextString();
						break;

					case "attr_LloydsCode":
						object.attr_LloydsCode = jsonReader.nextString();
						break;
						case "VesselCode":
						object.VesselCode = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new VesselCode();
				object.setVesselCode(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new VesselCode();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, VesselCode object) throws IOException {
		if(object?.VesselCode == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_CallSign != null || object.attr_LloydsCode != null ){
				jsonWriter.beginObject()
				if(object.attr_CallSign != null){
					jsonWriter.name("attr_CallSign")
					jsonWriter.value(object.attr_CallSign)
				}
				if(object.attr_LloydsCode != null){
					jsonWriter.name("attr_LloydsCode")
					jsonWriter.value(object.attr_LloydsCode)
				}
				jsonWriter.name("VesselCode")
				jsonWriter.value(object.VesselCode)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.VesselCode)
			}
		}
	}
}
class EmptyPickupDateAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.EmptyPickupDate> {
	
	@Override
	public EmptyPickupDate read(JsonReader jsonReader) throws IOException {
		
		EmptyPickupDate object = new EmptyPickupDate();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_TimeZone":
						object.attr_TimeZone = jsonReader.nextString();
						break;
						case "EmptyPickupDate":
						object.EmptyPickupDate = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EmptyPickupDate();
				object.setEmptyPickupDate(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EmptyPickupDate();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, EmptyPickupDate object) throws IOException {
		if(object?.EmptyPickupDate == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TimeZone != null ){
				jsonWriter.beginObject()
				
				if(object.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(object.attr_TimeZone)
				}

				jsonWriter.name("EmptyPickupDate")
				jsonWriter.value(object.EmptyPickupDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EmptyPickupDate)
			}
		}
	}
}

class FullReturnDateAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.FullReturnDate> {
	
	@Override
	public FullReturnDate read(JsonReader jsonReader) throws IOException {
		
		FullReturnDate object = new FullReturnDate();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_TimeZone":
						object.attr_TimeZone = jsonReader.nextString();
						break;
						case "FullReturnDate":
						object.FullReturnDate = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new FullReturnDate();
				object.setFullReturnDate(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new FullReturnDate();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, FullReturnDate object) throws IOException {
		if(object?.FullReturnDate == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TimeZone != null ){
				jsonWriter.beginObject()
				
				if(object.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(object.attr_TimeZone)
				}

				jsonWriter.name("FullReturnDate")
				jsonWriter.value(object.FullReturnDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.FullReturnDate)
			}
		}
	}
}

class AppointmentDateAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.AppointmentDate> {
	
	@Override
	public AppointmentDate read(JsonReader jsonReader) throws IOException {
		
		AppointmentDate object = new AppointmentDate();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_TimeZone":
						object.attr_TimeZone = jsonReader.nextString();
						break;
						case "AppointmentDate":
						object.AppointmentDate = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new AppointmentDate();
				object.setAppointmentDate(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new AppointmentDate();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, AppointmentDate object) throws IOException {
		if(object?.AppointmentDate == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TimeZone != null ){
				jsonWriter.beginObject()
				
				if(object.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(object.attr_TimeZone)
				}

				jsonWriter.name("AppointmentDate")
				jsonWriter.value(object.AppointmentDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.AppointmentDate)
			}
		}
	}
}

class EmptyReturnDateAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.EmptyReturnDate> {
	
	@Override
	public EmptyReturnDate read(JsonReader jsonReader) throws IOException {
		
		EmptyReturnDate object = new EmptyReturnDate();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_TimeZone":
						object.attr_TimeZone = jsonReader.nextString();
						break;
						case "EmptyReturnDate":
						object.EmptyReturnDate = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EmptyReturnDate();
				object.setEmptyReturnDate(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EmptyReturnDate();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, EmptyReturnDate object) throws IOException {
		if(object?.EmptyReturnDate == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TimeZone != null ){
				jsonWriter.beginObject()
				
				if(object.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(object.attr_TimeZone)
				}

				jsonWriter.name("EmptyReturnDate")
				jsonWriter.value(object.EmptyReturnDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EmptyReturnDate)
			}
		}
	}
}

class DestinationDateTimeAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.DestinationDateTime> {
	
	@Override
	public DestinationDateTime read(JsonReader jsonReader) throws IOException {
		
		DestinationDateTime object = new DestinationDateTime();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_TimeZone":
						object.attr_TimeZone = jsonReader.nextString();
						break;
						case "DestinationDateTime":
						object.DestinationDateTime = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new DestinationDateTime();
				object.setDestinationDateTime(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new DestinationDateTime();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, DestinationDateTime object) throws IOException {
		if(object?.DestinationDateTime == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TimeZone != null ){
				jsonWriter.beginObject()
				
				if(object.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(object.attr_TimeZone)
				}

				jsonWriter.name("DestinationDateTime")
				jsonWriter.value(object.DestinationDateTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.DestinationDateTime)
			}
		}
	}
}
class CommodityAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Commodity> {
	
	@Override
	public Commodity read(JsonReader jsonReader) throws IOException {
		
		Commodity object = new Commodity();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Group":
						object.attr_Group = jsonReader.nextString();
						break;
						case "Commodity":
						object.Commodity = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Commodity();
				object.setCommodity(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Commodity();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Commodity object) throws IOException {
		if(object?.Commodity == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Group != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Group != null){
					jsonWriter.name("attr_Group")
					jsonWriter.value(object.attr_Group)
				}

				jsonWriter.name("Commodity")
				jsonWriter.value(object.Commodity)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Commodity)
			}
		}
	}
}

class TemperatureAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Temperature> {
	
	@Override
	public Temperature read(JsonReader jsonReader) throws IOException {
		
		Temperature object = new Temperature();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;
						case "Temperature":
						object.Temperature = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Temperature();
				object.setTemperature(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Temperature();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Temperature object) throws IOException {
		if(object?.Temperature == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("Temperature")
				jsonWriter.value(object.Temperature)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Temperature)
			}
		}
	}
}

class VentilationAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Ventilation> {
	
	@Override
	public Ventilation read(JsonReader jsonReader) throws IOException {
		
		Ventilation object = new Ventilation();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;
						case "Ventilation":
						object.Ventilation = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Ventilation();
				object.setVentilation(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Ventilation();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Ventilation object) throws IOException {
		if(object?.Ventilation == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("Ventilation")
				jsonWriter.value(object.Ventilation)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Ventilation)
			}
		}
	}
}

class FlashPointAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.FlashPoint> {
	
	@Override
	public FlashPoint read(JsonReader jsonReader) throws IOException {
		
		FlashPoint object = new FlashPoint();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;
						case "FlashPoint":
						object.FlashPoint = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new FlashPoint();
				object.setFlashPoint(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new FlashPoint();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, FlashPoint object) throws IOException {
		if(object?.FlashPoint == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()
				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}
				jsonWriter.name("FlashPoint")
				jsonWriter.value(object.FlashPoint)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.FlashPoint)
			}
		}
	}
}

class DGElevationTemperatureAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.DGElevationTemperature> {
	
	@Override
	public DGElevationTemperature read(JsonReader jsonReader) throws IOException {
		
		DGElevationTemperature object = new DGElevationTemperature();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;
						case "DGElevationTemperature":
						object.DGElevationTemperature = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new DGElevationTemperature();
				object.setDGElevationTemperature(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new DGElevationTemperature();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, DGElevationTemperature object) throws IOException {
		if(object?.DGElevationTemperature == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("DGElevationTemperature")
				jsonWriter.value(object.DGElevationTemperature)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.DGElevationTemperature)
			}
		}
	}
}

class InnerPackagingAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.InnerPackaging> {
	
	@Override
	public InnerPackaging read(JsonReader jsonReader) throws IOException {
		
		InnerPackaging object = new InnerPackaging();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;
						case "InnerPackaging":
						object.InnerPackaging = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new InnerPackaging();
				object.setInnerPackaging(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new InnerPackaging();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, InnerPackaging object) throws IOException {
		if(object?.InnerPackaging == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("InnerPackaging")
				jsonWriter.value(object.InnerPackaging)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.InnerPackaging)
			}
		}
	}
}

class OuterPackagingAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.OuterPackaging> {
	
	@Override
	public OuterPackaging read(JsonReader jsonReader) throws IOException {
		
		OuterPackaging object = new OuterPackaging();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;
						case "OuterPackaging":
						object.OuterPackaging = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new OuterPackaging();
				object.setOuterPackaging(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new OuterPackaging();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, OuterPackaging object) throws IOException {
		if(object?.OuterPackaging == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("OuterPackaging")
				jsonWriter.value(object.OuterPackaging)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.OuterPackaging)
			}
		}
	}
}
class HeightAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Height> {
	
	@Override
	public Height read(JsonReader jsonReader) throws IOException {
		
		Height object = new Height();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;
						case "Height":
						object.Height = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Height();
				object.setHeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Height();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Height object) throws IOException {
		if(object?.Height == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("Height")
				jsonWriter.value(object.Height)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Height)
			}
		}
	}
}
class WidthAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Width> {
	
	@Override
	public Width read(JsonReader jsonReader) throws IOException {
		
		Width object = new Width();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;
						case "Width":
						object.Width = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Width();
				object.setWidth(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Width();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Width object) throws IOException {
		if(object?.Width == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("Width")
				jsonWriter.value(object.Width)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Width)
			}
		}
	}
}
class LengthAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Length> {
	
	@Override
	public Length read(JsonReader jsonReader) throws IOException {
		
		Length object = new Length();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;
						case "Length":
						object.Length = jsonReader.nextString();
					break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Length();
				object.setLength(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Length();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Length object) throws IOException {
		if(object?.Length == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("Length")
				jsonWriter.value(object.Length)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Length)
			}
		}
	}
}

class WeightAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Weight> {
	
	@Override
	public Weight read(JsonReader jsonReader) throws IOException {
		
		Weight object = new Weight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Qualifier":
						object.attr_Qualifier = jsonReader.nextString();
						break;

					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;

					case "Weight":
						object.Weight = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Weight();
				object.setWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Weight();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Weight object) throws IOException {
		if(object?.Weight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Qualifier != null ||object.attr_Units != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Qualifier != null){
					jsonWriter.name("attr_Qualifier")
					jsonWriter.value(object.attr_Qualifier)
				}

				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("Weight")
				jsonWriter.value(object.Weight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Weight)
			}
		}
	}
}