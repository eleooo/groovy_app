package cs.test2
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
class BillOfLading implements Serializable {
	public static final Set<String> MultiElementList = ["BookingNumber", "Party", "Containers", "DescriptionLine", "CargoItems", "BLBillOfLading"]
 	InterchangeControlHeader InterchangeControlHeader;
	List<BLBillOfLading> BLBillOfLading = new ArrayList<BLBillOfLading>();
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

class BLBillOfLading implements Serializable {
	GeneralInfo GeneralInfo;
	BLDetails BLDetails;
	SummaryDetails SummaryDetails;
}

class GeneralInfo implements Serializable {
	TransactionInfo TransactionInfo;
	String ActionType;
	String BLNumber;
	String SCAC;
	String PaymentStatus;
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
	LegalParties LegalParties;
	RouteInformation RouteInformation;
	EquipmentInformation EquipmentInformation;
	CargoInformation CargoInformation;
}

class BookingInfo implements Serializable {
	List<String> BookingNumber = new ArrayList<String>();
}

class UserReferences implements Serializable {
	References References;
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
	FullReturnCutoff FullReturnCutoff;
	ArrivalAtFinalHub ArrivalAtFinalHub;
	Haulage Haulage;
	Location Location;
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

class Haulage implements Serializable {
	String Outbound;
	String Inbound;
}

class Location implements Serializable {
	String FunctionCode;
	String FacilityCode;
	String FacilityName;
	String LocationName;
	LocationDetails LocationDetails;
	EventDate EventDate;
	VesselVoyageInformation VesselVoyageInformation;
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

class EventDate implements Serializable {
	Arrival Arrival;
	Departure Departure;
}

class EventDate2 implements Serializable {
	FreeDemurrageStartDate FreeDemurrageStartDate;
	FreeDetentionStartDate FreeDetentionStartDate;
}

@JsonAdapter(FreeDemurrageStartDateAdapter.class)
class FreeDemurrageStartDate implements Serializable {
	String attr_TimeZone;
	String FreeDemurrageStartDate;

	@Override
	public String toString() {
		return this.FreeDemurrageStartDate;
	}
}

@JsonAdapter(FreeDetentionStartDateAdapter.class)
class FreeDetentionStartDate implements Serializable {
	String attr_TimeZone;
	String FreeDetentionStartDate;

	@Override
	public String toString() {
		return this.FreeDetentionStartDate;
	}
}

@JsonAdapter(ArrivalAdapter.class)
class Arrival implements Serializable {
	String attr_EstActIndicator;
 	String attr_TimeZone;
 	String Arrival;

	@Override
	public String toString() {
		return this.Arrival;
	}
}

@JsonAdapter(DepartureAdapter.class)
class Departure implements Serializable {
	String attr_EstActIndicator;
 	String attr_TimeZone;
 	String Departure;

	@Override
	public String toString() {
		return this.Departure;
	}
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

class EquipmentInformation implements Serializable {
	List<Containers> Containers = new ArrayList<Containers>();
}

class Containers implements Serializable {
	String SOCIndicator;
	String ContainerType;
	ContainerNumber ContainerNumber;
	SealNumber SealNumber;
	Weight Weight;
	Volume Volume;
	PieceCount PieceCount;
	TrafficMode TrafficMode;
	RateAndCharges RateAndCharges;
	ReeferSetting ReeferSetting;
	DetentionDemurrage DetentionDemurrage;
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

@JsonAdapter(PieceCountAdapter.class)
class PieceCount implements Serializable {
	String attr_Units;
 	String PieceCount;

	@Override
	public String toString() {
		return this.PieceCount;
	}
}

class TrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class RateAndCharges implements Serializable {
	Charges Charges;
}

class Charges implements Serializable {
	String ChargeTypeCode;
	FreightRate FreightRate;
	PrepaidAmount PrepaidAmount;
	CollectAmount CollectAmount;
	String PayableAt;
	PayableBy PayableBy;
}

@JsonAdapter(FreightRateAdapter.class)
class FreightRate implements Serializable {
	String attr_Qualifier;
 	String FreightRate;

	@Override
	public String toString() {
		return this.FreightRate;
	}
}

@JsonAdapter(PrepaidAmountAdapter.class)
class PrepaidAmount implements Serializable {
	String attr_Currency;
 	String attr_ExchangeRate;
 	String PrepaidAmount;

	@Override
	public String toString() {
		return this.PrepaidAmount;
	}
}

@JsonAdapter(CollectAmountAdapter.class)
class CollectAmount implements Serializable {
	String attr_Currency;
 	String attr_ExchangeRate;
 	String CollectAmount;

	@Override
	public String toString() {
		return this.CollectAmount;
	}
}

class PayableBy implements Serializable {
	String attr_Type;
 	String CarrierCustomerCode;
	ContactPerson ContactPerson;
	Address Address;
	String CityName;
	String County;
	String StateProvinceCode;
	String StateProvince;
	String CountryCode;
	String CountryName;
	String PostalCode;
}

class ReeferSetting implements Serializable {
	ReeferCargoInfo ReeferCargoInfo;
}

class ReeferCargoInfo implements Serializable {
	ReeferSettings ReeferSettings;
	EmergencyContactDetails EmergencyContactDetails;
}

class ReeferSettings implements Serializable {
	String attr_AtmosphereType;
 	String attr_CAIndicator;
 	String attr_CO2;
 	String attr_GenSetType;
 	String attr_Humidity;
 	String attr_O2;
 	String attr_PreCooling;
 	String attr_ROIndicator;
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

class Remarks implements Serializable {
	String RemarksLines;
}

class EmergencyContactDetails implements Serializable {
	String FirstName;
	String LastName;
	Phone Phone;
	Fax Fax;
	String Email;
}

class DetentionDemurrage implements Serializable {
	String Type;
	EventDate EventDate;
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
	List<CargoItems> CargoItems = new ArrayList<CargoItems>();
}

class CargoItems implements Serializable {
	String CargoNature;
	String CommodityCode;
	Weight Weight;
	Volume Volume;
	Package Package;
	ContainerNumber ContainerNumber;
	CargoDescription CargoDescription;
	MarksAndNumbers MarksAndNumbers;
	DangerousCargo DangerousCargo;
	AwkwardCargo AwkwardCargo;
}

class Package2 implements Serializable {
	String PackagingGroupCode;
	InnerPackageDescription InnerPackageDescription;
	OuterPackageDescription OuterPackageDescription;
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
	EmergencyContactDetails EmergencyContactDetails;
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

class AwkwardCargo implements Serializable {
	AwkwardCargoInfo AwkwardCargoInfo;
}

class AwkwardCargoInfo implements Serializable {
	AwkwardCargoDetails AwkwardCargoDetails;
	EmergencyContactDetails EmergencyContactDetails;
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

class SummaryDetails implements Serializable {
	BLInformation BLInformation;
	Certifications Certifications;
	Charges Charges;
	Remarks Remarks;
}

class BLInformation implements Serializable {
	String attr_BillType;
 	String BLNumber;
	String BLStatus;
	BLIssuanceDateTime BLIssuanceDateTime;
	BLReceiptDateTime BLReceiptDateTime;
	BLCreationDateTime BLCreationDateTime;
	BLUpdateDateTime BLUpdateDateTime;
	BLOnBoardDateTime BLOnBoardDateTime;
	BLCapturingOffice BLCapturingOffice;
	PaymentStatus PaymentStatus;
	String CargoType;
	String CargoControlOffice;
	TrafficMode TrafficMode;
	Weight Weight;
	Volume Volume;
}

@JsonAdapter(BLIssuanceDateTimeAdapter.class)
class BLIssuanceDateTime implements Serializable {
	String attr_TimeZone;
 	String BLIssuanceDateTime;

	@Override
	public String toString() {
		return this.BLIssuanceDateTime;
	}
}

@JsonAdapter(BLReceiptDateTimeAdapter.class)
class BLReceiptDateTime implements Serializable {
	String attr_TimeZone;
 	String BLReceiptDateTime;

	@Override
	public String toString() {
		return this.BLReceiptDateTime;
	}
}

@JsonAdapter(BLCreationDateTimeAdapter.class)
class BLCreationDateTime implements Serializable {
	String attr_TimeZone;
 	String BLCreationDateTime;

	@Override
	public String toString() {
		return this.BLCreationDateTime;
	}
}

@JsonAdapter(BLUpdateDateTimeAdapter.class)
class BLUpdateDateTime implements Serializable {
	String attr_TimeZone;
 	String BLUpdateDateTime;

	@Override
	public String toString() {
		return this.BLUpdateDateTime;
	}
}

@JsonAdapter(BLOnBoardDateTimeAdapter.class)
class BLOnBoardDateTime implements Serializable {
	String attr_TimeZone;
 	String BLOnBoardDateTime;

	@Override
	public String toString() {
		return this.BLOnBoardDateTime;
	}
}

@JsonAdapter(BLCapturingOfficeAdapter.class)
class BLCapturingOffice implements Serializable {
	String attr_PhoneNumber;
 	String BLCapturingOffice;

	@Override
	public String toString() {
		return this.BLCapturingOffice;
	}
}

class PaymentStatus implements Serializable {
	BLPaymentOffice BLPaymentOffice;
	PaymentReceiptDateTime PaymentReceiptDateTime;
}

@JsonAdapter(BLPaymentOfficeAdapter.class)
class BLPaymentOffice implements Serializable {
	String attr_PhoneNumber;
 	String BLPaymentOffice;

	@Override
	public String toString() {
		return this.BLPaymentOffice;
	}
}

@JsonAdapter(PaymentReceiptDateTimeAdapter.class)
class PaymentReceiptDateTime implements Serializable {
	String attr_TimeZone;
 	String PaymentReceiptDateTime;

	@Override
	public String toString() {
		return this.PaymentReceiptDateTime;
	}
}

class Certifications implements Serializable {
	String Code;
	String ClauseText;
}



public class BLPaymentOfficeAdapter extends TypeAdapter<BLPaymentOffice>{
	@Override
	public BLPaymentOffice read(JsonReader jsonReader) throws IOException {
		BLPaymentOffice bLPaymentOffice = new BLPaymentOffice();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'BLPaymentOffice':
						bLPaymentOffice.BLPaymentOffice = jsonReader.nextString();
						break;
					case 'attr_PhoneNumber':
						bLPaymentOffice.attr_PhoneNumber = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				bLPaymentOffice = new BLPaymentOffice();
				bLPaymentOffice.BLPaymentOffice = jsonReader.nextString();
			}catch(IllegalStateException e2){
				bLPaymentOffice = new BLPaymentOffice();
				jsonReader.nextNull();
			}
		}
		return bLPaymentOffice
	}

	@Override
	public void write(JsonWriter jsonWriter,BLPaymentOffice bLPaymentOffice) throws IOException {
		if(bLPaymentOffice?.BLPaymentOffice == null){
			jsonWriter.nullValue()
		}else{
			if(bLPaymentOffice.attr_PhoneNumber != null ){
				jsonWriter.beginObject()
				if(bLPaymentOffice.attr_PhoneNumber != null){
					jsonWriter.name("attr_PhoneNumber")
					jsonWriter.value(bLPaymentOffice.attr_PhoneNumber)
				}
				jsonWriter.name("BLPaymentOffice")
				jsonWriter.value(bLPaymentOffice.BLPaymentOffice)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(bLPaymentOffice.BLPaymentOffice)
			}
		}
	}
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

public class PieceCountAdapter extends TypeAdapter<PieceCount>{
	@Override
	public PieceCount read(JsonReader jsonReader) throws IOException {
		PieceCount pieceCount = new PieceCount();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Units':
						pieceCount.attr_Units = jsonReader.nextString();
						break;
					case 'PieceCount':
						pieceCount.PieceCount = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				pieceCount = new PieceCount();
				pieceCount.PieceCount = jsonReader.nextString();
			}catch(IllegalStateException e2){
				pieceCount = new PieceCount();
				jsonReader.nextNull();
			}
		}
		return pieceCount
	}

	@Override
	public void write(JsonWriter jsonWriter,PieceCount pieceCount) throws IOException {
		if(pieceCount?.PieceCount == null){
			jsonWriter.nullValue()
		}else{
			if(pieceCount.attr_Units != null ){
				jsonWriter.beginObject()
				if(pieceCount.attr_Units != null){
					jsonWriter.name("attr_Units")
					jsonWriter.value(pieceCount.attr_Units)
				}
				jsonWriter.name("PieceCount")
				jsonWriter.value(pieceCount.PieceCount)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(pieceCount.PieceCount)
			}
		}
	}
}

public class BLReceiptDateTimeAdapter extends TypeAdapter<BLReceiptDateTime>{
	@Override
	public BLReceiptDateTime read(JsonReader jsonReader) throws IOException {
		BLReceiptDateTime bLReceiptDateTime = new BLReceiptDateTime();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'BLReceiptDateTime':
						bLReceiptDateTime.BLReceiptDateTime = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						bLReceiptDateTime.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				bLReceiptDateTime = new BLReceiptDateTime();
				bLReceiptDateTime.BLReceiptDateTime = jsonReader.nextString();
			}catch(IllegalStateException e2){
				bLReceiptDateTime = new BLReceiptDateTime();
				jsonReader.nextNull();
			}
		}
		return bLReceiptDateTime
	}

	@Override
	public void write(JsonWriter jsonWriter,BLReceiptDateTime bLReceiptDateTime) throws IOException {
		if(bLReceiptDateTime?.BLReceiptDateTime == null){
			jsonWriter.nullValue()
		}else{
			if(bLReceiptDateTime.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(bLReceiptDateTime.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(bLReceiptDateTime.attr_TimeZone)
				}
				jsonWriter.name("BLReceiptDateTime")
				jsonWriter.value(bLReceiptDateTime.BLReceiptDateTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(bLReceiptDateTime.BLReceiptDateTime)
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

public class PrepaidAmountAdapter extends TypeAdapter<PrepaidAmount>{
	@Override
	public PrepaidAmount read(JsonReader jsonReader) throws IOException {
		PrepaidAmount prepaidAmount = new PrepaidAmount();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'PrepaidAmount':
						prepaidAmount.PrepaidAmount = jsonReader.nextString();
						break;
					case 'attr_ExchangeRate':
						prepaidAmount.attr_ExchangeRate = jsonReader.nextString();
						break;
					case 'attr_Currency':
						prepaidAmount.attr_Currency = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				prepaidAmount = new PrepaidAmount();
				prepaidAmount.PrepaidAmount = jsonReader.nextString();
			}catch(IllegalStateException e2){
				prepaidAmount = new PrepaidAmount();
				jsonReader.nextNull();
			}
		}
		return prepaidAmount
	}

	@Override
	public void write(JsonWriter jsonWriter,PrepaidAmount prepaidAmount) throws IOException {
		if(prepaidAmount?.PrepaidAmount == null){
			jsonWriter.nullValue()
		}else{
			if(prepaidAmount.attr_ExchangeRate != null || prepaidAmount.attr_Currency != null ){
				jsonWriter.beginObject()
				if(prepaidAmount.attr_ExchangeRate != null){
					jsonWriter.name("attr_ExchangeRate")
					jsonWriter.value(prepaidAmount.attr_ExchangeRate)
				}
				if(prepaidAmount.attr_Currency != null){
					jsonWriter.name("attr_Currency")
					jsonWriter.value(prepaidAmount.attr_Currency)
				}
				jsonWriter.name("PrepaidAmount")
				jsonWriter.value(prepaidAmount.PrepaidAmount)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(prepaidAmount.PrepaidAmount)
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
					case 'attr_LloydsCode':
						vesselCode.attr_LloydsCode = jsonReader.nextString();
						break;
					case 'attr_CallSign':
						vesselCode.attr_CallSign = jsonReader.nextString();
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

public class CollectAmountAdapter extends TypeAdapter<CollectAmount>{
	@Override
	public CollectAmount read(JsonReader jsonReader) throws IOException {
		CollectAmount collectAmount = new CollectAmount();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_ExchangeRate':
						collectAmount.attr_ExchangeRate = jsonReader.nextString();
						break;
					case 'attr_Currency':
						collectAmount.attr_Currency = jsonReader.nextString();
						break;
					case 'CollectAmount':
						collectAmount.CollectAmount = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				collectAmount = new CollectAmount();
				collectAmount.CollectAmount = jsonReader.nextString();
			}catch(IllegalStateException e2){
				collectAmount = new CollectAmount();
				jsonReader.nextNull();
			}
		}
		return collectAmount
	}

	@Override
	public void write(JsonWriter jsonWriter,CollectAmount collectAmount) throws IOException {
		if(collectAmount?.CollectAmount == null){
			jsonWriter.nullValue()
		}else{
			if(collectAmount.attr_ExchangeRate != null || collectAmount.attr_Currency != null ){
				jsonWriter.beginObject()
				if(collectAmount.attr_ExchangeRate != null){
					jsonWriter.name("attr_ExchangeRate")
					jsonWriter.value(collectAmount.attr_ExchangeRate)
				}
				if(collectAmount.attr_Currency != null){
					jsonWriter.name("attr_Currency")
					jsonWriter.value(collectAmount.attr_Currency)
				}
				jsonWriter.name("CollectAmount")
				jsonWriter.value(collectAmount.CollectAmount)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(collectAmount.CollectAmount)
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

public class BLCreationDateTimeAdapter extends TypeAdapter<BLCreationDateTime>{
	@Override
	public BLCreationDateTime read(JsonReader jsonReader) throws IOException {
		BLCreationDateTime bLCreationDateTime = new BLCreationDateTime();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'BLCreationDateTime':
						bLCreationDateTime.BLCreationDateTime = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						bLCreationDateTime.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				bLCreationDateTime = new BLCreationDateTime();
				bLCreationDateTime.BLCreationDateTime = jsonReader.nextString();
			}catch(IllegalStateException e2){
				bLCreationDateTime = new BLCreationDateTime();
				jsonReader.nextNull();
			}
		}
		return bLCreationDateTime
	}

	@Override
	public void write(JsonWriter jsonWriter,BLCreationDateTime bLCreationDateTime) throws IOException {
		if(bLCreationDateTime?.BLCreationDateTime == null){
			jsonWriter.nullValue()
		}else{
			if(bLCreationDateTime.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(bLCreationDateTime.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(bLCreationDateTime.attr_TimeZone)
				}
				jsonWriter.name("BLCreationDateTime")
				jsonWriter.value(bLCreationDateTime.BLCreationDateTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(bLCreationDateTime.BLCreationDateTime)
			}
		}
	}
}

public class BLUpdateDateTimeAdapter extends TypeAdapter<BLUpdateDateTime>{
	@Override
	public BLUpdateDateTime read(JsonReader jsonReader) throws IOException {
		BLUpdateDateTime bLUpdateDateTime = new BLUpdateDateTime();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'BLUpdateDateTime':
						bLUpdateDateTime.BLUpdateDateTime = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						bLUpdateDateTime.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				bLUpdateDateTime = new BLUpdateDateTime();
				bLUpdateDateTime.BLUpdateDateTime = jsonReader.nextString();
			}catch(IllegalStateException e2){
				bLUpdateDateTime = new BLUpdateDateTime();
				jsonReader.nextNull();
			}
		}
		return bLUpdateDateTime
	}

	@Override
	public void write(JsonWriter jsonWriter,BLUpdateDateTime bLUpdateDateTime) throws IOException {
		if(bLUpdateDateTime?.BLUpdateDateTime == null){
			jsonWriter.nullValue()
		}else{
			if(bLUpdateDateTime.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(bLUpdateDateTime.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(bLUpdateDateTime.attr_TimeZone)
				}
				jsonWriter.name("BLUpdateDateTime")
				jsonWriter.value(bLUpdateDateTime.BLUpdateDateTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(bLUpdateDateTime.BLUpdateDateTime)
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
					case 'attr_EstActIndicator':
						departure.attr_EstActIndicator = jsonReader.nextString();
						break;
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
			if(departure.attr_EstActIndicator != null || departure.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(departure.attr_EstActIndicator != null){
					jsonWriter.name("attr_EstActIndicator")
					jsonWriter.value(departure.attr_EstActIndicator)
				}
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

public class FreightRateAdapter extends TypeAdapter<FreightRate>{
	@Override
	public FreightRate read(JsonReader jsonReader) throws IOException {
		FreightRate freightRate = new FreightRate();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_Qualifier':
						freightRate.attr_Qualifier = jsonReader.nextString();
						break;
					case 'FreightRate':
						freightRate.FreightRate = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				freightRate = new FreightRate();
				freightRate.FreightRate = jsonReader.nextString();
			}catch(IllegalStateException e2){
				freightRate = new FreightRate();
				jsonReader.nextNull();
			}
		}
		return freightRate
	}

	@Override
	public void write(JsonWriter jsonWriter,FreightRate freightRate) throws IOException {
		if(freightRate?.FreightRate == null){
			jsonWriter.nullValue()
		}else{
			if(freightRate.attr_Qualifier != null ){
				jsonWriter.beginObject()
				if(freightRate.attr_Qualifier != null){
					jsonWriter.name("attr_Qualifier")
					jsonWriter.value(freightRate.attr_Qualifier)
				}
				jsonWriter.name("FreightRate")
				jsonWriter.value(freightRate.FreightRate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(freightRate.FreightRate)
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

public class BLCapturingOfficeAdapter extends TypeAdapter<BLCapturingOffice>{
	@Override
	public BLCapturingOffice read(JsonReader jsonReader) throws IOException {
		BLCapturingOffice bLCapturingOffice = new BLCapturingOffice();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_PhoneNumber':
						bLCapturingOffice.attr_PhoneNumber = jsonReader.nextString();
						break;
					case 'BLCapturingOffice':
						bLCapturingOffice.BLCapturingOffice = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				bLCapturingOffice = new BLCapturingOffice();
				bLCapturingOffice.BLCapturingOffice = jsonReader.nextString();
			}catch(IllegalStateException e2){
				bLCapturingOffice = new BLCapturingOffice();
				jsonReader.nextNull();
			}
		}
		return bLCapturingOffice
	}

	@Override
	public void write(JsonWriter jsonWriter,BLCapturingOffice bLCapturingOffice) throws IOException {
		if(bLCapturingOffice?.BLCapturingOffice == null){
			jsonWriter.nullValue()
		}else{
			if(bLCapturingOffice.attr_PhoneNumber != null ){
				jsonWriter.beginObject()
				if(bLCapturingOffice.attr_PhoneNumber != null){
					jsonWriter.name("attr_PhoneNumber")
					jsonWriter.value(bLCapturingOffice.attr_PhoneNumber)
				}
				jsonWriter.name("BLCapturingOffice")
				jsonWriter.value(bLCapturingOffice.BLCapturingOffice)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(bLCapturingOffice.BLCapturingOffice)
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

public class BLIssuanceDateTimeAdapter extends TypeAdapter<BLIssuanceDateTime>{
	@Override
	public BLIssuanceDateTime read(JsonReader jsonReader) throws IOException {
		BLIssuanceDateTime bLIssuanceDateTime = new BLIssuanceDateTime();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'BLIssuanceDateTime':
						bLIssuanceDateTime.BLIssuanceDateTime = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						bLIssuanceDateTime.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				bLIssuanceDateTime = new BLIssuanceDateTime();
				bLIssuanceDateTime.BLIssuanceDateTime = jsonReader.nextString();
			}catch(IllegalStateException e2){
				bLIssuanceDateTime = new BLIssuanceDateTime();
				jsonReader.nextNull();
			}
		}
		return bLIssuanceDateTime
	}

	@Override
	public void write(JsonWriter jsonWriter,BLIssuanceDateTime bLIssuanceDateTime) throws IOException {
		if(bLIssuanceDateTime?.BLIssuanceDateTime == null){
			jsonWriter.nullValue()
		}else{
			if(bLIssuanceDateTime.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(bLIssuanceDateTime.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(bLIssuanceDateTime.attr_TimeZone)
				}
				jsonWriter.name("BLIssuanceDateTime")
				jsonWriter.value(bLIssuanceDateTime.BLIssuanceDateTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(bLIssuanceDateTime.BLIssuanceDateTime)
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

public class ArrivalAdapter extends TypeAdapter<Arrival>{
	@Override
	public Arrival read(JsonReader jsonReader) throws IOException {
		Arrival arrival = new Arrival();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_EstActIndicator':
						arrival.attr_EstActIndicator = jsonReader.nextString();
						break;
					case 'Arrival':
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
			if(arrival.attr_EstActIndicator != null || arrival.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(arrival.attr_EstActIndicator != null){
					jsonWriter.name("attr_EstActIndicator")
					jsonWriter.value(arrival.attr_EstActIndicator)
				}
				if(arrival.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(arrival.attr_TimeZone)
				}
				jsonWriter.name("Arrival")
				jsonWriter.value(arrival.Arrival)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(arrival.Arrival)
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

public class BLOnBoardDateTimeAdapter extends TypeAdapter<BLOnBoardDateTime>{
	@Override
	public BLOnBoardDateTime read(JsonReader jsonReader) throws IOException {
		BLOnBoardDateTime bLOnBoardDateTime = new BLOnBoardDateTime();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'attr_TimeZone':
						bLOnBoardDateTime.attr_TimeZone = jsonReader.nextString();
						break;
					case 'BLOnBoardDateTime':
						bLOnBoardDateTime.BLOnBoardDateTime = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				bLOnBoardDateTime = new BLOnBoardDateTime();
				bLOnBoardDateTime.BLOnBoardDateTime = jsonReader.nextString();
			}catch(IllegalStateException e2){
				bLOnBoardDateTime = new BLOnBoardDateTime();
				jsonReader.nextNull();
			}
		}
		return bLOnBoardDateTime
	}

	@Override
	public void write(JsonWriter jsonWriter,BLOnBoardDateTime bLOnBoardDateTime) throws IOException {
		if(bLOnBoardDateTime?.BLOnBoardDateTime == null){
			jsonWriter.nullValue()
		}else{
			if(bLOnBoardDateTime.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(bLOnBoardDateTime.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(bLOnBoardDateTime.attr_TimeZone)
				}
				jsonWriter.name("BLOnBoardDateTime")
				jsonWriter.value(bLOnBoardDateTime.BLOnBoardDateTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(bLOnBoardDateTime.BLOnBoardDateTime)
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

public class PaymentReceiptDateTimeAdapter extends TypeAdapter<PaymentReceiptDateTime>{
	@Override
	public PaymentReceiptDateTime read(JsonReader jsonReader) throws IOException {
		PaymentReceiptDateTime paymentReceiptDateTime = new PaymentReceiptDateTime();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'PaymentReceiptDateTime':
						paymentReceiptDateTime.PaymentReceiptDateTime = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						paymentReceiptDateTime.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				paymentReceiptDateTime = new PaymentReceiptDateTime();
				paymentReceiptDateTime.PaymentReceiptDateTime = jsonReader.nextString();
			}catch(IllegalStateException e2){
				paymentReceiptDateTime = new PaymentReceiptDateTime();
				jsonReader.nextNull();
			}
		}
		return paymentReceiptDateTime
	}

	@Override
	public void write(JsonWriter jsonWriter,PaymentReceiptDateTime paymentReceiptDateTime) throws IOException {
		if(paymentReceiptDateTime?.PaymentReceiptDateTime == null){
			jsonWriter.nullValue()
		}else{
			if(paymentReceiptDateTime.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(paymentReceiptDateTime.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(paymentReceiptDateTime.attr_TimeZone)
				}
				jsonWriter.name("PaymentReceiptDateTime")
				jsonWriter.value(paymentReceiptDateTime.PaymentReceiptDateTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(paymentReceiptDateTime.PaymentReceiptDateTime)
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

public class FreeDemurrageStartDateAdapter extends TypeAdapter<FreeDemurrageStartDate>{
	@Override
	public FreeDemurrageStartDate read(JsonReader jsonReader) throws IOException {
		FreeDemurrageStartDate freeDemurrageStartDate = new FreeDemurrageStartDate();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'FreeDemurrageStartDate':
						freeDemurrageStartDate.FreeDemurrageStartDate = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						freeDemurrageStartDate.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				freeDemurrageStartDate = new FreeDemurrageStartDate();
				freeDemurrageStartDate.FreeDemurrageStartDate = jsonReader.nextString();
			}catch(IllegalStateException e2){
				freeDemurrageStartDate = new FreeDemurrageStartDate();
				jsonReader.nextNull();
			}
		}
		return freeDemurrageStartDate
	}

	@Override
	public void write(JsonWriter jsonWriter,FreeDemurrageStartDate freeDemurrageStartDate) throws IOException {
		if(freeDemurrageStartDate?.FreeDemurrageStartDate == null){
			jsonWriter.nullValue()
		}else{
			if(freeDemurrageStartDate.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(freeDemurrageStartDate.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(freeDemurrageStartDate.attr_TimeZone)
				}
				jsonWriter.name("FreeDemurrageStartDate")
				jsonWriter.value(freeDemurrageStartDate.FreeDemurrageStartDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(freeDemurrageStartDate.FreeDemurrageStartDate)
			}
		}
	}
}

public class FreeDetentionStartDateAdapter extends TypeAdapter<FreeDetentionStartDate>{
	@Override
	public FreeDetentionStartDate read(JsonReader jsonReader) throws IOException {
		FreeDetentionStartDate freeDetentionStartDate = new FreeDetentionStartDate();
		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case 'FreeDetentionStartDate':
						freeDetentionStartDate.FreeDetentionStartDate = jsonReader.nextString();
						break;
					case 'attr_TimeZone':
						freeDetentionStartDate.attr_TimeZone = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				freeDetentionStartDate = new FreeDetentionStartDate();
				freeDetentionStartDate.FreeDetentionStartDate = jsonReader.nextString();
			}catch(IllegalStateException e2){
				freeDetentionStartDate = new FreeDetentionStartDate();
				jsonReader.nextNull();
			}
		}
		return freeDetentionStartDate
	}

	@Override
	public void write(JsonWriter jsonWriter,FreeDetentionStartDate freeDetentionStartDate) throws IOException {
		if(freeDetentionStartDate?.FreeDetentionStartDate == null){
			jsonWriter.nullValue()
		}else{
			if(freeDetentionStartDate.attr_TimeZone != null ){
				jsonWriter.beginObject()
				if(freeDetentionStartDate.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(freeDetentionStartDate.attr_TimeZone)
				}
				jsonWriter.name("FreeDetentionStartDate")
				jsonWriter.value(freeDetentionStartDate.FreeDetentionStartDate)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(freeDetentionStartDate.FreeDetentionStartDate)
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


