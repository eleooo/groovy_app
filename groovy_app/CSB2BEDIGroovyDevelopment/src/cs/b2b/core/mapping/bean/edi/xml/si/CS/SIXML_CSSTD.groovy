package cs.b2b.core.mapping.bean.edi.xml.si.CS

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.gson.annotations.JsonAdapter
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set;;

class ShippingInstructions implements Serializable {
	public static final Set<String> MultiElementList = [
            'SIBillOfLading',
			'BookingNumber',
			'References',
			'AddressLines',
			'Party',
			'Party.ContainerNumber',
			'Location',
			'CargoItems.Weight',
			'CargoDetails.Weight',
			'SealNumber',
			'Containers',
			'Containers.Weight',
			'EmergencyContact',
			'DangerousCargoInfo',
			'AwkwardCargoInfo',
			'CargoDetails',
			'CargoItems',
			'SpecialHandling',
			'SIBillOfLadingDistribution',
			'CertificationClauseText',
			'RequestedDocuments',
			'DescriptionLine',
			'MarksAndNumbersLine',
			'HarmonizedTariffSchedule',
			'RemarksLines'
		];
	InterchangeControlHeader InterchangeControlHeader;
	List<SIBillOfLading> SIBillOfLading = new ArrayList<SIBillOfLading>();
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

class SIBillOfLading implements Serializable {
	GeneralInfo GeneralInfo;
	BLDetails BLDetails;
	SummaryDetails SummaryDetails;
}

class GeneralInfo implements Serializable {
	TransactionInfo TransactionInfo;
	String ActionType;
	String BLNumber;
	String SIReferenceNumber;
	String SCAC;
	String OwnedBy;
	String BLReleaseOffice;
	String SIVersionNumber;
}

class TransactionInfo implements Serializable {
	String BatchNumber;
	String MessageSender;
	String MessageRecipient;
	String MessageID;
	DateCreated DateCreated;
	String FileName;
	String DataSource;
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

class BLDetails implements Serializable {
	BookingInfo BookingInfo;
	UserReferences UserReferences;
	CarrierRateReferences CarrierRateReferences;
	LegalParties LegalParties;
	RouteInformation RouteInformation;
	EquipmentInformation EquipmentInformation;
	CargoInformation CargoInformation;
}

class BookingInfo implements Serializable {
	List<String> BookingNumber = new ArrayList<String>();
}

class UserReferences implements Serializable {
	List<References> References = new ArrayList<References>();
}

class References implements Serializable {
	String ReferenceNumber;
	String ReferenceType;
	String ReferenceDescription;
}

class CarrierRateReferences implements Serializable {
	String ReferenceNumber;
	String ReferenceType;
	String ReferenceDescription;
}

class LegalParties implements Serializable {
	List<Party> Party = new ArrayList<Party>();
}

class Party implements Serializable {
	String PartyType;
	String PartyName;
	String CompanyID;
	String CarrierCustomerCode;
	PartyLocation PartyLocation;
	ContactPerson ContactPerson;
	String PartyText;
	String FMCNumber;
	String EORINumber;
	String Signature;
	List<ContainerNumber> ContainerNumber = new ArrayList<ContainerNumber>();
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

class PartyLocation implements Serializable {
	Address Address;
	PartyAddress PartyAddress;
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

class PartyAddress implements Serializable {
	String AddressLines ;
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

class RouteInformation implements Serializable {
	VesselVoyageInformation VesselVoyageInformation;
	List<Location> Location = new ArrayList<Location>();
}

class VesselVoyageInformation implements Serializable {
	ServiceName ServiceName;
	String VoyageNumberDirection;
	VesselInformation VesselInformation;
	External External;
	String PreCarriage;
	String VesselVoyageText;
	TrafficMode TrafficMode;
	String Haulage;
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
	String attr_Type;
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

class TrafficMode implements Serializable {
	String Description;
	String OutBound;
	String InBound;
}

class Location implements Serializable {
	String FunctionCode;
	String LocationName;
	LocationDetails LocationDetails;
}

class LocationDetails implements Serializable {
	String FunctionCode;
	String LocationName;
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
	String State;
	String Text;
}

class EquipmentInformation implements Serializable {
	List<Containers> Containers = new ArrayList<Containers>();
}

class Containers implements Serializable {
	String AssociatedBookingNumber;
	String ContainerType;
	ContainerNumber ContainerNumber;
	List<SealNumber> SealNumber = new ArrayList<SealNumber>();
	List<Weight> Weight = new ArrayList<Weight>();
	Package Package;
	Volume Volume;
	String Remarks;
	Indicators Indicators;
	TrafficMode TrafficMode;
	ReeferSettings ReeferSettings;
}

@JsonAdapter(SealNumberAdapter.class)
class SealNumber implements Serializable {
	String attr_Name;
	String attr_Type;
	String SealNumber;
	
	@Override
	public String toString() {
		return this.SealNumber;
	}
}
@JsonAdapter(PackageAdapter.class)
class Package implements Serializable {
	String attr_Type;
	String Package;

	@Override
	public String toString() {
		return this.Package;
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
	String VGMMethod;
}

class Indicators_hazar implements Serializable {
	String ReportableQuantityIndicator;
	String isEmptyUnclean;
	String limitedQuantityDeclaration;
	String marinePollutantInformation;
	String isResidue;
	String isInhalationHazardous;
	String isShipsideDelivery;
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

//TODO
class Remarks implements Serializable {
	List<String> RemarksLines = new ArrayList<String>();
}

class EmergencyContact implements Serializable {
	String FirstName;
	String LastName;
	Phone Phone;
	Fax Fax;
	String Email;
	String Type;
}

class CargoInformation implements Serializable {
	//List<CargoDetails> CargoDetails = new ArrayList<CargoDetails>();
	List<CargoItems> CargoItems = new ArrayList<CargoItems>();
}

class CargoDetails implements Serializable {
	String AssociatedBookingNumber;
	ContainerNumber ContainerNumber;
	String CargoNature;
	String HarmonizedTariffSchedule ;
	Package Package;
	String PackageDescription;
	List<Weight> Weight = new ArrayList<Weight>();
	Volume Volume;
	CargoDescription CargoDescription;
	MarksAndNumbers MarksAndNumbers;
	Remarks Remarks;
	String PackageMaterial;
	DangerousCargo DangerousCargo;
	AwkwardCargo AwkwardCargo;
}
//TODO
class CargoDescription implements Serializable {
	List<String> DescriptionLine = new ArrayList<String>();
}
//TODO
class MarksAndNumbers implements Serializable {
	List<String> MarksAndNumbersLine = new ArrayList<String>();
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
	String DGRegulationString;
	String UNNumber;
	FlashPoint FlashPoint;
	String HazardousReference;
	String MFAGTableNumber;
	String MFAGPageNumber;
	String MaterialDescription;
	String MaterialClassification;
	String EmergencyScheduleReference;
	String EmergencyResponseCode;
	String EmergencySchedulePageNumber;
	String ProperShippingName;
	String TechnicalShippingName;
	String hazardousClassificationCode;
	DGElevationTemperature DGElevationTemperature;
	List<Weight> Weight = new ArrayList<Weight>();
	Indicators_hazar Indicators_hazar;
	HazardousMaterial_Package Package;
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

//Special for HazardousMaterial
class HazardousMaterial_Package implements Serializable {
	String PackagingGroupCode;
	InnerPackage InnerPackage;
	OuterPackage OuterPackage;
}

@JsonAdapter(InnerPackageAdapter.class)
class InnerPackage implements Serializable {
	String attr_Type;
	String InnerPackage;

	@Override
	public String toString() {
		return this.InnerPackage;
	}
}

@JsonAdapter(OuterPackageAdapter.class)
class OuterPackage implements Serializable {
	String attr_Type;
	String OuterPackage;

	@Override
	public String toString() {
		return this.OuterPackage;
	}
}

class AwkwardCargo implements Serializable {
	List<AwkwardCargoInfo> AwkwardCargoInfo = new ArrayList<AwkwardCargoInfo>();
}

class AwkwardCargoInfo implements Serializable {
	AwkwardCargoDetails AwkwardCargoDetails;
	List<EmergencyContact> EmergencyContact = new ArrayList<EmergencyContact>();
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

class CargoItems implements Serializable {
	String CargoNature;
	Package Package;
	String PackageDescription;
	String PackageMaterial;
	List<Weight> Weight = new ArrayList<Weight>();
	Volume Volume;
	CargoDescription CargoDescription;
	MarksAndNumbers MarksAndNumbers;
	List<String> HarmonizedTariffSchedule = new ArrayList<String>();
	String Remarks;
	DangerousCargo DangerousCargo;
	AwkwardCargo AwkwardCargo;
	List<CargoDetails> CargoDetails = new ArrayList<CargoDetails>();
}

class SummaryDetails implements Serializable {
	BLInformation BLInformation;
	Certifications Certifications;
	Charges Charges;
	PaperWork PaperWork;
}

class BLInformation implements Serializable {
	String attr_BLType;
	String attr_FreightType;
	String BLNumber;
	OnBoardBLDate OnBoardBLDate;
	DraftInformation DraftInformation;
	OriginalBL OriginalBL;
	String PaymentStatus;
	String BLCargoDescription;
	Remarks Remarks;
	SpecialInstructions SpecialInstructions;
	SIDistribution SIDistribution;
}

@JsonAdapter(OnBoardBLDateAdapter.class)
class OnBoardBLDate implements Serializable {
	String attr_Type;
	String OnBoardBLDate;

	@Override
	public String toString() {
		return this.OnBoardBLDate;
	}
}

class DraftInformation implements Serializable {
	String ModeOfTransmission;
	Recipient Recipient;
	FaxNumber FaxNumber;
}

@JsonAdapter(RecipientAdapter.class)
class Recipient implements Serializable {
	String attr_Type;
	String Recipient;

	@Override
	public String toString() {
		return this.Recipient;
	}
}

class FaxNumber implements Serializable {
	String CountryCode;
	String AreaCode;
	String Number;
}

class OriginalBL implements Serializable {
	String ModeOfTransmission;
	String OtherInformation;
}

class SpecialInstructions implements Serializable {
	List<SpecialHandling> SpecialHandling = new ArrayList<SpecialHandling>();
}

@JsonAdapter(SpecialHandlingAdapter.class)
class SpecialHandling implements Serializable {
	String attr_Code;
	String SpecialHandling;
	
	@Override
	public String toString() {
		return this.SpecialHandling;
	}
}

class SIDistribution implements Serializable {
	List<SIBillOfLadingDistribution> SIBillOfLadingDistribution = new ArrayList<SIBillOfLadingDistribution>();
}

class SIBillOfLadingDistribution implements Serializable {
	String attr_DocType;
	String attr_FreightType;
	String PartyRole;
	String NumberOfCopies;
}

class Certifications implements Serializable {
	List<CertificationClauseText> CertificationClauseText = new ArrayList<CertificationClauseText>();
}

@JsonAdapter(CertificationClauseTextAdapter.class)
class CertificationClauseText implements Serializable {
	String attr_Code;
	String CertificationClauseText;

	@Override
	public String toString() {
		return this.CertificationClauseText;
	}
}

class Charges implements Serializable {
	String attr_Category;
	String attr_Code;
	String attr_SubCategory;
	String attr_Type;
	String PayableAt_charge;
	PaidBy PaidBy;
}

class PaidBy implements Serializable {
	String PartyType;
	String PartyName;
	String CompanyID;
	String CarrierCustomerCode;
	PartyLocation PartyLocation;
	ContactPerson ContactPerson;
}

class PaperWork implements Serializable {
	List<RequestedDocuments> RequestedDocuments = new ArrayList<RequestedDocuments>();
}

class RequestedDocuments implements Serializable {
	String attr_DocumentType;
	String attr_FreightType;
	String NoOfCopies;
	String ModeOfTransmission;
	FaxNumber FaxNumber;
	String OtherInformation;
}

class DateCreatedAdapter extends TypeAdapter<DateCreated> {

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
class SchedKDCodeAdapter extends TypeAdapter<SchedKDCode> {

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
class ServiceNameAdapter extends TypeAdapter<ServiceName> {

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

class PackageAdapter extends TypeAdapter<Package> {

	@Override
	public Package read(JsonReader jsonReader) throws IOException {

		Package object = new Package();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "Package":
						object.Package = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Package();
				object.setPackage(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Package();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, Package object) throws IOException {
		if(object?.Package == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("Package")
				jsonWriter.value(object.Package)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Package)
			}
		}
	}
}
class VolumeAdapter extends TypeAdapter<Volume> {

	@Override
	public Volume read(JsonReader jsonReader) throws IOException {

		Volume object = new Volume();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Units":
						object.attr_Units = jsonReader.nextString();
						break;

					case "Volume":
						object.Volume = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Volume();
				object.setVolume(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Volume();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, Volume object) throws IOException {
		if(object?.Volume == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Units != null ){
				jsonWriter.beginObject()

				if(object.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(object.attr_Units)
				}

				jsonWriter.name("Volume")
				jsonWriter.value(object.Volume)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Volume)
			}
		}
	}
}
class TemperatureAdapter extends TypeAdapter<Temperature> {

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
class VentilationAdapter extends TypeAdapter<Ventilation> {

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

class FlashPointAdapter extends TypeAdapter<FlashPoint> {

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

class DGElevationTemperatureAdapter extends TypeAdapter<DGElevationTemperature> {

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


class InnerPackageAdapter extends TypeAdapter<InnerPackage> {

	@Override
	public InnerPackage read(JsonReader jsonReader) throws IOException {

		InnerPackage object = new InnerPackage();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "InnerPackage":
						object.InnerPackage = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new InnerPackage();
				object.setInnerPackage(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new InnerPackage();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, InnerPackage object) throws IOException {
		if(object?.InnerPackage == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("InnerPackage")
				jsonWriter.value(object.InnerPackage)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.InnerPackage)
			}
		}
	}
}


class OuterPackageAdapter extends TypeAdapter<OuterPackage> {

	@Override
	public OuterPackage read(JsonReader jsonReader) throws IOException {

		OuterPackage object = new OuterPackage();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "OuterPackage":
						object.OuterPackage = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new OuterPackage();
				object.setOuterPackage(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new OuterPackage();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, OuterPackage object) throws IOException {
		if(object?.OuterPackage == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("OuterPackage")
				jsonWriter.value(object.OuterPackage)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.OuterPackage)
			}
		}
	}
}


class HeightAdapter extends TypeAdapter<Height> {

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


class WidthAdapter extends TypeAdapter<Width> {

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


class LengthAdapter extends TypeAdapter<Length> {

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


class OnBoardBLDateAdapter extends TypeAdapter<OnBoardBLDate> {

	@Override
	public OnBoardBLDate read(JsonReader jsonReader) throws IOException {

		OnBoardBLDate object = new OnBoardBLDate();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "OnBoardBLDate":
						object.OnBoardBLDate = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new OnBoardBLDate();
				object.setOnBoardBLDate(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new OnBoardBLDate();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, OnBoardBLDate object) throws IOException {
		if(object?.OnBoardBLDate == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("OnBoardBLDate")
				jsonWriter.value(object.OnBoardBLDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.OnBoardBLDate)
			}
		}
	}
}


class RecipientAdapter extends TypeAdapter<Recipient> {

	@Override
	public Recipient read(JsonReader jsonReader) throws IOException {

		Recipient object = new Recipient();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "Recipient":
						object.Recipient = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Recipient();
				object.setRecipient(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Recipient();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, Recipient object) throws IOException {
		if(object?.Recipient == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("Recipient")
				jsonWriter.value(object.Recipient)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Recipient)
			}
		}
	}
}


class CertificationClauseTextAdapter extends TypeAdapter<CertificationClauseText> {

	@Override
	public CertificationClauseText read(JsonReader jsonReader) throws IOException {

		CertificationClauseText object = new CertificationClauseText();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Code":
						object.attr_Code = jsonReader.nextString();
						break;

					case "CertificationClauseText":
						object.CertificationClauseText = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new CertificationClauseText();
				object.setCertificationClauseText(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new CertificationClauseText();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, CertificationClauseText object) throws IOException {
		if(object?.CertificationClauseText == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Code != null ){
				jsonWriter.beginObject()

				if(object.attr_Code != null){
					jsonWriter.name("attr_Code")
					jsonWriter.value(object.attr_Code)
				}

				jsonWriter.name("CertificationClauseText")
				jsonWriter.value(object.CertificationClauseText)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.CertificationClauseText)
			}
		}
	}
}



class VesselCodeAdapter extends TypeAdapter<VesselCode> {
	
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

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
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
			if(object.attr_CallSign != null ||object.attr_LloydsCode != null ||object.attr_Type != null ){
				jsonWriter.beginObject()
				
				if(object.attr_CallSign != null){
					jsonWriter.name("attr_CallSign")
					jsonWriter.value(object.attr_CallSign)
				}

				if(object.attr_LloydsCode != null){
					jsonWriter.name("attr_LloydsCode")
					jsonWriter.value(object.attr_LloydsCode)
				}

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
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


class ContainerNumberAdapter extends TypeAdapter<ContainerNumber> {
	
	@Override
	public ContainerNumber read(JsonReader jsonReader) throws IOException {
		
		ContainerNumber object = new ContainerNumber();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_CheckDigit":
						object.attr_CheckDigit = jsonReader.nextString();
						break;

					case "ContainerNumber":
						object.ContainerNumber = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ContainerNumber();
				object.setContainerNumber(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ContainerNumber();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, ContainerNumber object) throws IOException {
		if(object?.ContainerNumber == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_CheckDigit != null ){
				jsonWriter.beginObject()
				
				if(object.attr_CheckDigit != null){
					jsonWriter.name("attr_CheckDigit")
					jsonWriter.value(object.attr_CheckDigit)
				}

				jsonWriter.name("ContainerNumber")
				jsonWriter.value(object.ContainerNumber)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ContainerNumber)
			}
		}
	}
}

class WeightAdapter extends TypeAdapter<Weight> {
	
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

class SealNumberAdapter extends TypeAdapter<SealNumber> {
	
	@Override
	public SealNumber read(JsonReader jsonReader) throws IOException {
		
		SealNumber object = new SealNumber();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Name":
						object.attr_Name = jsonReader.nextString();
						break;

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "SealNumber":
						object.SealNumber = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new SealNumber();
				object.setSealNumber(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new SealNumber();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, SealNumber object) throws IOException {
		if(object?.SealNumber == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Name != null ||object.attr_Type != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Name != null){
					jsonWriter.name("attr_Name")
					jsonWriter.value(object.attr_Name)
				}

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("SealNumber")
				jsonWriter.value(object.SealNumber)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.SealNumber)
			}
		}
	}
}

class SpecialHandlingAdapter extends TypeAdapter<SpecialHandling> {
	
	@Override
	public SpecialHandling read(JsonReader jsonReader) throws IOException {
		
		SpecialHandling object = new SpecialHandling();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_Code":
						object.attr_Code = jsonReader.nextString();
						break;

					case "SpecialHandling":
						object.SpecialHandling = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new SpecialHandling();
				object.setSpecialHandling(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new SpecialHandling();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, SpecialHandling object) throws IOException {
		if(object?.SpecialHandling == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Code != null ){
				jsonWriter.beginObject()
				
				if(object.attr_Code != null){
					jsonWriter.name("attr_Code")
					jsonWriter.value(object.attr_Code)
				}

				jsonWriter.name("SpecialHandling")
				jsonWriter.value(object.SpecialHandling)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.SpecialHandling)
			}
		}
	}
}
