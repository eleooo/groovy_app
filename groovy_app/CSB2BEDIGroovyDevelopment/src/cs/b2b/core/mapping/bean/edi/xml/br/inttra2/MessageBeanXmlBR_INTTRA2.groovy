package cs.b2b.core.mapping.bean.edi.xml.br.inttra2

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.gson.annotations.JsonAdapter

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set;;

class Message implements Serializable {
	public static final Set<String> MultiElementList = ['EquipmentDetails.SpecialServiceRequest', 'HazardousGoods.IMOClassCode', 'EquipmentParty.DateTime', 'ConveyanceInformation.Identifier', 'ExportLicense.DateTime', 'Location', 'ChargeCategory', 'Contacts', 'CustomsDeclarationInformation', 'ReferenceInformation', 'Party', 'TransportationDetails', 'InnerInnerPack', 'InnerPack', 'HazardousGoodsComments', 'HazardousGoodsSplitDetails', 'HazardousGoods', 'SplitGoodsDetails', 'DetailsReferenceInformation', 'CommodityClassification', 'EquipmentParty', 'EquipmentComments', 'EquipmentReferenceInformation', 'EquipmentGasLevel', 'EquipmentDetails', 'GoodsDetails'];
	Header Header;
	MessageBody MessageBody;
}

class Header implements Serializable {
	String SenderId;
	String ReceiverId;
	String RequestDateTimeStamp;
	String RequestMessageVersion;
	String TransactionType;
	String TransactionVersion;
	String DocumentIdentifier;
	String TransactionStatus;
	String TransactionSplitIndicator;
}

class MessageBody implements Serializable {
	MessageProperties MessageProperties;
	MessageDetails MessageDetails;
}

class MessageProperties implements Serializable {
	String ShipmentID;
	ContactInformation ContactInformation;
	DateTime DateTime;
	String MovementType;
	String PerContainerReleaseNbrReqst;
	GeneralInformation GeneralInformation;
	AmendmentJustification AmendmentJustification;
	CustomsClearanceInstructions CustomsClearanceInstructions;
	List<CustomsDeclarationInformation> CustomsDeclarationInformation = new ArrayList<CustomsDeclarationInformation>();
	String NatureOfCargo ;
	List<Location> Location = new ArrayList<Location>();
	List<ReferenceInformation> ReferenceInformation = new ArrayList<ReferenceInformation>();
	ExportLicense ExportLicense;
	List<TransportationDetails> TransportationDetails = new ArrayList<TransportationDetails>();
	List<Party> Party = new ArrayList<Party>();
}

@JsonAdapter(DateTimeAdapter.class)
class DateTime implements Serializable {
	String attr_DateType;
	String attr_Type;
	String DateTime;

	@Override
	public String toString() {
		return this.DateTime;
	}
}

class ContactInformation implements Serializable {
	String Type;
	String Name;
	CommunicationDetails CommunicationDetails;
}

class CommunicationDetails implements Serializable {
	String Phone ;
	String Fax ;
	String Email ;
}

class GeneralInformation implements Serializable {
	String Text;
}

class AmendmentJustification implements Serializable {
	String Text;
}

class CustomsClearanceInstructions implements Serializable {
	String Filer;
	String FilingProgram;
	FilerID FilerID;
}

@JsonAdapter(FilerIDAdapter.class)
class FilerID implements Serializable {
	String attr_Type;
	String FilerID;

	@Override
	public String toString() {
		return this.FilerID;
	}
}

class CustomsDeclarationInformation implements Serializable {
	String attr_Type;
	String CustomsDeclarationInfoValue;
}

class Location implements Serializable {
	String Type;
	Identifier Identifier;
	String Name;
	String City;
	String Subdivision;
	String CountryName;
	String CountryCode;
	DateTime DateTime;
}

@JsonAdapter(IdentifierAdapter.class)
class Identifier implements Serializable {
	String attr_Type;
	String Identifier;

	@Override
	public String toString() {
		return this.Identifier;
	}
}

class ReferenceInformation implements Serializable {
	String attr_Type;
	String Value;
}

class ExportLicense implements Serializable {
	String Value;
	List<DateTime> DateTime = new ArrayList<DateTime>();
}

class TransportationDetails implements Serializable {
	String attr_TransportMode;
	String attr_TransportStage;
	ConveyanceInformation ConveyanceInformation;
	List<Location> Location = new ArrayList<Location>();
}

class ConveyanceInformation implements Serializable {
	String Type;
	List<Identifier> Identifier = new ArrayList<Identifier>();
	String RegistrationCountryCode;
	OperatorIdentifier OperatorIdentifier;
}

@JsonAdapter(OperatorIdentifierAdapter.class)
class OperatorIdentifier implements Serializable {
	String attr_Type;
	String OperatorIdentifier;

	@Override
	public String toString() {
		return this.OperatorIdentifier;
	}
}

class Party implements Serializable {
	String Role;
	String Name;
	Identifier Identifier;
	Address Address;
	List<Contacts> Contacts = new ArrayList<Contacts>();
	List<ChargeCategory> ChargeCategory = new ArrayList<ChargeCategory>();
}

class Address implements Serializable {
	String StreetAddress;
	String CityName;
	String Subdivision;
	String PostalCode;
	String CountryCode;
	String CountryName;
}

class Contacts implements Serializable {
	String Type;
	String Name;
	CommunicationDetails CommunicationDetails;
}

class ChargeCategory implements Serializable {
	String attr_ChargeType;
	PrepaidCollector PrepaidCollector;
	ChargeLocation ChargeLocation;
}

@JsonAdapter(PrepaidCollectorAdapter.class)
class PrepaidCollector implements Serializable {
	String attr_PrepaidorCollectIndicator;
	String PrepaidCollector;

	@Override
	public String toString() {
		return this.PrepaidCollector;
	}
}

class ChargeLocation implements Serializable {
	String Type;
	Identifier Identifier;
	String Name;
	String City;
	String Subdivision;
	String CountryName;
	String CountryCode;
}

class MessageDetails implements Serializable {
	List<GoodsDetails> GoodsDetails = new ArrayList<GoodsDetails>();
	List<EquipmentDetails> EquipmentDetails = new ArrayList<EquipmentDetails>();
}

class GoodsDetails implements Serializable {
	String LineNumber;
	PackageDetail PackageDetail;
	List<CommodityClassification> CommodityClassification = new ArrayList<CommodityClassification>();
	String GoodDescription;
	List<CustomsDeclarationInformation> CustomsDeclarationInformation = new ArrayList<CustomsDeclarationInformation>();
	OutOfGaugeDimensions OutOfGaugeDimensions;
	List<DetailsReferenceInformation> DetailsReferenceInformation = new ArrayList<DetailsReferenceInformation>();
	ExportLicense ExportLicense;
	List<SplitGoodsDetails> SplitGoodsDetails = new ArrayList<SplitGoodsDetails>();
	List<HazardousGoods> HazardousGoods = new ArrayList<HazardousGoods>();
}

@JsonAdapter(CommodityClassificationAdapter.class)
class CommodityClassification implements Serializable {
	String attr_Type;
	String CommodityClassification;

	@Override
	public String toString() {
		return this.CommodityClassification;
	}
}

class PackageDetail implements Serializable {
	OuterPack OuterPack;
}

class OuterPack implements Serializable {
	String NumberOfPackages;
	String PackageTypeCode;
	String PackageTypeDescription;
	GoodGrossVolume GoodGrossVolume;
	GoodGrossWeight GoodGrossWeight;
	List<InnerPack> InnerPack = new ArrayList<InnerPack>();
}

@JsonAdapter(GoodGrossVolumeAdapter.class)
class GoodGrossVolume implements Serializable {
	String attr_UOM;
	String GoodGrossVolume;

	@Override
	public String toString() {
		return this.GoodGrossVolume;
	}
}

@JsonAdapter(GoodGrossWeightAdapter.class)
class GoodGrossWeight implements Serializable {
	String attr_UOM;
	String GoodGrossWeight;

	@Override
	public String toString() {
		return this.GoodGrossWeight;
	}
}

class InnerPack implements Serializable {
	String NumberOfPackages;
	String PackageTypeCode;
	String PackageTypeDescription;
	GoodGrossVolume GoodGrossVolume;
	GoodGrossWeight GoodGrossWeight;
	List<InnerInnerPack> InnerInnerPack = new ArrayList<InnerInnerPack>();
}

class InnerInnerPack implements Serializable {
	String NumberOfPackages;
	String PackageTypeCode;
	String PackageTypeDescription;
	GoodGrossVolume GoodGrossVolume;
	GoodGrossWeight GoodGrossWeight;
}

class OutOfGaugeDimensions implements Serializable {
	String attr_UOM;
	String Length;
	String Width;
	String Height;
}

class DetailsReferenceInformation implements Serializable {
	String attr_Type;
	String Value;
}

class SplitGoodsDetails implements Serializable {
	String EquipmentIdentifier;
	String NumberOfPackages;
	GrossVolume GrossVolume;
	GrossWeight GrossWeight;
}

@JsonAdapter(GrossVolumeAdapter.class)
class GrossVolume implements Serializable {
	String attr_UOM;
	String GrossVolume;

	@Override
	public String toString() {
		return this.GrossVolume;
	}
}

@JsonAdapter(GrossWeightAdapter.class)
class GrossWeight implements Serializable {
	String attr_UOM;
	String GrossWeight;

	@Override
	public String toString() {
		return this.GrossWeight;
	}
}

class HazardousGoods implements Serializable {
	List<String> IMOClassCode = new ArrayList<String>();
	String IMDGPageNumber;
	String HazardCodeVersionNumber;
	String PackingGroupCode;
	String UNDGNumber;
	FlashpointTemperature FlashpointTemperature;
	String EMSNumber;
	String TremCardNumber;
	String ProperShippingName;
	String TransportInLimitedQuantities;
	String TechnicalName;
	String EmptyUncleanedReceptacle;
	String MarinePollutantStatus;
	String InhalantHazard;
	String AggregationState;
	List<HazardousGoodsComments> HazardousGoodsComments = new ArrayList<HazardousGoodsComments>();
	String IntermedBulkContainerCode;
	EmergencyResponseContact EmergencyResponseContact;
	HazardousGoodsWeight HazardousGoodsWeight;
	HazardousGoodsVolume HazardousGoodsVolume;
	HazardousGoodsRadioactivity HazardousGoodsRadioactivity;
	HazardousGoodsAcidConcentrtn HazardousGoodsAcidConcentrtn;
	List<HazardousGoodsSplitDetails> HazardousGoodsSplitDetails = new ArrayList<HazardousGoodsSplitDetails>();
}

@JsonAdapter(FlashpointTemperatureAdapter.class)
class FlashpointTemperature implements Serializable {
	String attr_UOM;
	String FlashpointTemperature;

	@Override
	public String toString() {
		return this.FlashpointTemperature;
	}
}

@JsonAdapter(HazardousGoodsWeightAdapter.class)
class HazardousGoodsWeight implements Serializable {
	String attr_UOM;
	String HazardousGoodsWeight;

	@Override
	public String toString() {
		return this.HazardousGoodsWeight;
	}
}

@JsonAdapter(HazardousGoodsVolumeAdapter.class)
class HazardousGoodsVolume implements Serializable {
	String attr_UOM;
	String HazardousGoodsVolume;

	@Override
	public String toString() {
		return this.HazardousGoodsVolume;
	}
}

@JsonAdapter(HazardousGoodsRadioactivityAdapter.class)
class HazardousGoodsRadioactivity implements Serializable {
	String attr_UOM;
	String HazardousGoodsRadioactivity;

	@Override
	public String toString() {
		return this.HazardousGoodsRadioactivity;
	}
}

@JsonAdapter(HazardousGoodsAcidConcentrtnAdapter.class)
class HazardousGoodsAcidConcentrtn implements Serializable {
	String attr_UOM;
	String HazardousGoodsAcidConcentrtn;

	@Override
	public String toString() {
		return this.HazardousGoodsAcidConcentrtn;
	}
}

class HazardousGoodsComments implements Serializable {
	String Category;
	String Text;
}

class EmergencyResponseContact implements Serializable {
	String Type;
	String Name;
	CommunicationDetails CommunicationDetails;
}

class HazardousGoodsSplitDetails implements Serializable {
	String EquipmentIdentifier;
	String NumberOfPackages;
	GrossVolume GrossVolume;
	GrossWeight GrossWeight;
}

class EquipmentDetails implements Serializable {
	String attr_EquipmentSupplier;
	String attr_FullEmptyIndicator;
	EquipmentIdentifier EquipmentIdentifier;
	EquipmentType EquipmentType;
	String NumberOfEquipment;
	ImportExportHaulage ImportExportHaulage;
	EquipmentGrossWeight EquipmentGrossWeight;
	EquipmentGrossVolume EquipmentGrossVolume;
	EquipmentHumidity EquipmentHumidity;
	EquipmentAirFlow EquipmentAirFlow;
	List<EquipmentGasLevel> EquipmentGasLevel = new ArrayList<EquipmentGasLevel>();
	EquipmentDetailsDimensions EquipmentDetailsDimensions;
	String NonActiveReefer;
	EquipmentTemperature EquipmentTemperature;
	SpecialHandling SpecialHandling;
	List<EquipmentComments> EquipmentComments = new ArrayList<EquipmentComments>();
	List<CustomsDeclarationInformation> CustomsDeclarationInformation = new ArrayList<CustomsDeclarationInformation>();
	String StowageInstructions;
	List<String> SpecialServiceRequest = new ArrayList<String>();
	List<EquipmentReferenceInformation> EquipmentReferenceInformation = new ArrayList<EquipmentReferenceInformation>();
	List<EquipmentParty> EquipmentParty = new ArrayList<EquipmentParty>();
}

@JsonAdapter(EquipmentIdentifierAdapter.class)
class EquipmentIdentifier implements Serializable {
	String attr_EquipmentIdentifierType;
	String EquipmentIdentifier;

	@Override
	public String toString() {
		return this.EquipmentIdentifier;
	}
}

@JsonAdapter(EquipmentGrossWeightAdapter.class)
class EquipmentGrossWeight implements Serializable {
	String attr_UOM;
	String EquipmentGrossWeight;

	@Override
	public String toString() {
		return this.EquipmentGrossWeight;
	}
}

@JsonAdapter(EquipmentGrossVolumeAdapter.class)
class EquipmentGrossVolume implements Serializable {
	String attr_UOM;
	String EquipmentGrossVolume;

	@Override
	public String toString() {
		return this.EquipmentGrossVolume;
	}
}

@JsonAdapter(EquipmentHumidityAdapter.class)
class EquipmentHumidity implements Serializable {
	String attr_UOM;
	String EquipmentHumidity;

	@Override
	public String toString() {
		return this.EquipmentHumidity;
	}
}

@JsonAdapter(EquipmentAirFlowAdapter.class)
class EquipmentAirFlow implements Serializable {
	String attr_UOM;
	String EquipmentAirFlow;

	@Override
	public String toString() {
		return this.EquipmentAirFlow;
	}
}

@JsonAdapter(EquipmentGasLevelAdapter.class)
class EquipmentGasLevel implements Serializable {
	String attr_Gas;
	String attr_UOM;
	String EquipmentGasLevel;

	@Override
	public String toString() {
		return this.EquipmentGasLevel;
	}
}

@JsonAdapter(EquipmentTemperatureAdapter.class)
class EquipmentTemperature implements Serializable {
	String attr_UOM;
	String EquipmentTemperature;

	@Override
	public String toString() {
		return this.EquipmentTemperature;
	}
}

class EquipmentType implements Serializable {
	String EquipmentTypeCode;
	String EquipmentDescription;
}

class ImportExportHaulage implements Serializable {
	String CargoMovementType;
	String HaulageArrangements;
}

class EquipmentDetailsDimensions implements Serializable {
	String attr_UOM;
	String Length;
	String Width;
	String Height;
}

class SpecialHandling implements Serializable {
	String EquipmentControlledAtmosphere;
	String SuperFreezerService;
	String GensetRequired;
	String Humidity;
	String InTransitColdSterilization;
	String NumberOfTemperatureProbes;
	String NumberOfUSDProbes;
	String TemperatureVariance;
	String VentSetting;
}

class EquipmentComments implements Serializable {
	String Category;
	String Text;
}

class EquipmentReferenceInformation implements Serializable {
	String attr_Type;
	String Value;
}

class EquipmentParty implements Serializable {
	String Role;
	String Name;
	Identifier Identifier;
	Address Address;
	List<DateTime> DateTime = new ArrayList<DateTime>();
	List<Contacts> Contacts = new ArrayList<Contacts>();
}
class DateTimeAdapter extends TypeAdapter<DateTime> {

	@Override
	public DateTime read(JsonReader jsonReader) throws IOException {

		DateTime object = new DateTime();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_DateType":
						object.attr_DateType = jsonReader.nextString();
						break;

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "DateTime":
						object.DateTime = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new DateTime();
				object.setDateTime(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new DateTime();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, DateTime object) throws IOException {
		if(object?.DateTime == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_DateType != null ||object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_DateType != null){
					jsonWriter.name("attr_DateType")
					jsonWriter.value(object.attr_DateType)
				}

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("DateTime")
				jsonWriter.value(object.DateTime)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.DateTime)
			}
		}
	}
}
class FilerIDAdapter extends TypeAdapter<FilerID> {

	@Override
	public FilerID read(JsonReader jsonReader) throws IOException {

		FilerID object = new FilerID();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "FilerID":
						object.FilerID = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new FilerID();
				object.setFilerID(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new FilerID();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, FilerID object) throws IOException {
		if(object?.FilerID == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("FilerID")
				jsonWriter.value(object.FilerID)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.FilerID)
			}
		}
	}
}
class IdentifierAdapter extends TypeAdapter<Identifier> {

	@Override
	public Identifier read(JsonReader jsonReader) throws IOException {

		Identifier object = new Identifier();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "Identifier":
						object.Identifier = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Identifier();
				object.setIdentifier(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Identifier();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, Identifier object) throws IOException {
		if(object?.Identifier == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("Identifier")
				jsonWriter.value(object.Identifier)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Identifier)
			}
		}
	}
}
class OperatorIdentifierAdapter extends TypeAdapter<OperatorIdentifier> {

	@Override
	public OperatorIdentifier read(JsonReader jsonReader) throws IOException {

		OperatorIdentifier object = new OperatorIdentifier();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "OperatorIdentifier":
						object.OperatorIdentifier = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new OperatorIdentifier();
				object.setOperatorIdentifier(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new OperatorIdentifier();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, OperatorIdentifier object) throws IOException {
		if(object?.OperatorIdentifier == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("OperatorIdentifier")
				jsonWriter.value(object.OperatorIdentifier)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.OperatorIdentifier)
			}
		}
	}
}
class PrepaidCollectorAdapter extends TypeAdapter<PrepaidCollector> {

	@Override
	public PrepaidCollector read(JsonReader jsonReader) throws IOException {

		PrepaidCollector object = new PrepaidCollector();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_PrepaidorCollectIndicator":
						object.attr_PrepaidorCollectIndicator = jsonReader.nextString();
						break;

					case "PrepaidCollector":
						object.PrepaidCollector = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new PrepaidCollector();
				object.setPrepaidCollector(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new PrepaidCollector();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, PrepaidCollector object) throws IOException {
		if(object?.PrepaidCollector == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_PrepaidorCollectIndicator != null ){
				jsonWriter.beginObject()

				if(object.attr_PrepaidorCollectIndicator != null){
					jsonWriter.name("attr_PrepaidorCollectIndicator")
					jsonWriter.value(object.attr_PrepaidorCollectIndicator)
				}

				jsonWriter.name("PrepaidCollector")
				jsonWriter.value(object.PrepaidCollector)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.PrepaidCollector)
			}
		}
	}
}
class GoodGrossVolumeAdapter extends TypeAdapter<GoodGrossVolume> {

	@Override
	public GoodGrossVolume read(JsonReader jsonReader) throws IOException {

		GoodGrossVolume object = new GoodGrossVolume();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "GoodGrossVolume":
						object.GoodGrossVolume = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new GoodGrossVolume();
				object.setGoodGrossVolume(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new GoodGrossVolume();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, GoodGrossVolume object) throws IOException {
		if(object?.GoodGrossVolume == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("GoodGrossVolume")
				jsonWriter.value(object.GoodGrossVolume)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.GoodGrossVolume)
			}
		}
	}
}
class GoodGrossWeightAdapter extends TypeAdapter<GoodGrossWeight> {

	@Override
	public GoodGrossWeight read(JsonReader jsonReader) throws IOException {

		GoodGrossWeight object = new GoodGrossWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "GoodGrossWeight":
						object.GoodGrossWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new GoodGrossWeight();
				object.setGoodGrossWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new GoodGrossWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, GoodGrossWeight object) throws IOException {
		if(object?.GoodGrossWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("GoodGrossWeight")
				jsonWriter.value(object.GoodGrossWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.GoodGrossWeight)
			}
		}
	}
}
class GrossVolumeAdapter extends TypeAdapter<GrossVolume> {

	@Override
	public GrossVolume read(JsonReader jsonReader) throws IOException {

		GrossVolume object = new GrossVolume();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "GrossVolume":
						object.GrossVolume = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new GrossVolume();
				object.setGrossVolume(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new GrossVolume();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, GrossVolume object) throws IOException {
		if(object?.GrossVolume == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("GrossVolume")
				jsonWriter.value(object.GrossVolume)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.GrossVolume)
			}
		}
	}
}
class GrossWeightAdapter extends TypeAdapter<GrossWeight> {

	@Override
	public GrossWeight read(JsonReader jsonReader) throws IOException {

		GrossWeight object = new GrossWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "GrossWeight":
						object.GrossWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new GrossWeight();
				object.setGrossWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new GrossWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, GrossWeight object) throws IOException {
		if(object?.GrossWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("GrossWeight")
				jsonWriter.value(object.GrossWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.GrossWeight)
			}
		}
	}
}
class FlashpointTemperatureAdapter extends TypeAdapter<FlashpointTemperature> {

	@Override
	public FlashpointTemperature read(JsonReader jsonReader) throws IOException {

		FlashpointTemperature object = new FlashpointTemperature();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "FlashpointTemperature":
						object.FlashpointTemperature = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new FlashpointTemperature();
				object.setFlashpointTemperature(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new FlashpointTemperature();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, FlashpointTemperature object) throws IOException {
		if(object?.FlashpointTemperature == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("FlashpointTemperature")
				jsonWriter.value(object.FlashpointTemperature)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.FlashpointTemperature)
			}
		}
	}
}
class HazardousGoodsWeightAdapter extends TypeAdapter<HazardousGoodsWeight> {

	@Override
	public HazardousGoodsWeight read(JsonReader jsonReader) throws IOException {

		HazardousGoodsWeight object = new HazardousGoodsWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "HazardousGoodsWeight":
						object.HazardousGoodsWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new HazardousGoodsWeight();
				object.setHazardousGoodsWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new HazardousGoodsWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, HazardousGoodsWeight object) throws IOException {
		if(object?.HazardousGoodsWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("HazardousGoodsWeight")
				jsonWriter.value(object.HazardousGoodsWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.HazardousGoodsWeight)
			}
		}
	}
}
class HazardousGoodsVolumeAdapter extends TypeAdapter<HazardousGoodsVolume> {

	@Override
	public HazardousGoodsVolume read(JsonReader jsonReader) throws IOException {

		HazardousGoodsVolume object = new HazardousGoodsVolume();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "HazardousGoodsVolume":
						object.HazardousGoodsVolume = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new HazardousGoodsVolume();
				object.setHazardousGoodsVolume(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new HazardousGoodsVolume();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, HazardousGoodsVolume object) throws IOException {
		if(object?.HazardousGoodsVolume == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("HazardousGoodsVolume")
				jsonWriter.value(object.HazardousGoodsVolume)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.HazardousGoodsVolume)
			}
		}
	}
}
class HazardousGoodsRadioactivityAdapter extends TypeAdapter<HazardousGoodsRadioactivity> {

	@Override
	public HazardousGoodsRadioactivity read(JsonReader jsonReader) throws IOException {

		HazardousGoodsRadioactivity object = new HazardousGoodsRadioactivity();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "HazardousGoodsRadioactivity":
						object.HazardousGoodsRadioactivity = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new HazardousGoodsRadioactivity();
				object.setHazardousGoodsRadioactivity(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new HazardousGoodsRadioactivity();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, HazardousGoodsRadioactivity object) throws IOException {
		if(object?.HazardousGoodsRadioactivity == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("HazardousGoodsRadioactivity")
				jsonWriter.value(object.HazardousGoodsRadioactivity)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.HazardousGoodsRadioactivity)
			}
		}
	}
}
class HazardousGoodsAcidConcentrtnAdapter extends TypeAdapter<HazardousGoodsAcidConcentrtn> {

	@Override
	public HazardousGoodsAcidConcentrtn read(JsonReader jsonReader) throws IOException {

		HazardousGoodsAcidConcentrtn object = new HazardousGoodsAcidConcentrtn();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "HazardousGoodsAcidConcentrtn":
						object.HazardousGoodsAcidConcentrtn = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new HazardousGoodsAcidConcentrtn();
				object.setHazardousGoodsAcidConcentrtn(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new HazardousGoodsAcidConcentrtn();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, HazardousGoodsAcidConcentrtn object) throws IOException {
		if(object?.HazardousGoodsAcidConcentrtn == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("HazardousGoodsAcidConcentrtn")
				jsonWriter.value(object.HazardousGoodsAcidConcentrtn)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.HazardousGoodsAcidConcentrtn)
			}
		}
	}
}
class EquipmentIdentifierAdapter extends TypeAdapter<EquipmentIdentifier> {

	@Override
	public EquipmentIdentifier read(JsonReader jsonReader) throws IOException {

		EquipmentIdentifier object = new EquipmentIdentifier();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_EquipmentIdentifierType":
						object.attr_EquipmentIdentifierType = jsonReader.nextString();
						break;

					case "EquipmentIdentifier":
						object.EquipmentIdentifier = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentIdentifier();
				object.setEquipmentIdentifier(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentIdentifier();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentIdentifier object) throws IOException {
		if(object?.EquipmentIdentifier == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_EquipmentIdentifierType != null ){
				jsonWriter.beginObject()

				if(object.attr_EquipmentIdentifierType != null){
					jsonWriter.name("attr_EquipmentIdentifierType")
					jsonWriter.value(object.attr_EquipmentIdentifierType)
				}

				jsonWriter.name("EquipmentIdentifier")
				jsonWriter.value(object.EquipmentIdentifier)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentIdentifier)
			}
		}
	}
}
class EquipmentGrossWeightAdapter extends TypeAdapter<EquipmentGrossWeight> {

	@Override
	public EquipmentGrossWeight read(JsonReader jsonReader) throws IOException {

		EquipmentGrossWeight object = new EquipmentGrossWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "EquipmentGrossWeight":
						object.EquipmentGrossWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentGrossWeight();
				object.setEquipmentGrossWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentGrossWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentGrossWeight object) throws IOException {
		if(object?.EquipmentGrossWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("EquipmentGrossWeight")
				jsonWriter.value(object.EquipmentGrossWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentGrossWeight)
			}
		}
	}
}
class EquipmentGrossVolumeAdapter extends TypeAdapter<EquipmentGrossVolume> {

	@Override
	public EquipmentGrossVolume read(JsonReader jsonReader) throws IOException {

		EquipmentGrossVolume object = new EquipmentGrossVolume();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "EquipmentGrossVolume":
						object.EquipmentGrossVolume = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentGrossVolume();
				object.setEquipmentGrossVolume(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentGrossVolume();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentGrossVolume object) throws IOException {
		if(object?.EquipmentGrossVolume == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("EquipmentGrossVolume")
				jsonWriter.value(object.EquipmentGrossVolume)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentGrossVolume)
			}
		}
	}
}
class EquipmentHumidityAdapter extends TypeAdapter<EquipmentHumidity> {

	@Override
	public EquipmentHumidity read(JsonReader jsonReader) throws IOException {

		EquipmentHumidity object = new EquipmentHumidity();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "EquipmentHumidity":
						object.EquipmentHumidity = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentHumidity();
				object.setEquipmentHumidity(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentHumidity();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentHumidity object) throws IOException {
		if(object?.EquipmentHumidity == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("EquipmentHumidity")
				jsonWriter.value(object.EquipmentHumidity)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentHumidity)
			}
		}
	}
}
class EquipmentAirFlowAdapter extends TypeAdapter<EquipmentAirFlow> {

	@Override
	public EquipmentAirFlow read(JsonReader jsonReader) throws IOException {

		EquipmentAirFlow object = new EquipmentAirFlow();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "EquipmentAirFlow":
						object.EquipmentAirFlow = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentAirFlow();
				object.setEquipmentAirFlow(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentAirFlow();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentAirFlow object) throws IOException {
		if(object?.EquipmentAirFlow == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("EquipmentAirFlow")
				jsonWriter.value(object.EquipmentAirFlow)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentAirFlow)
			}
		}
	}
}
class EquipmentTemperatureAdapter extends TypeAdapter<EquipmentTemperature> {

	@Override
	public EquipmentTemperature read(JsonReader jsonReader) throws IOException {

		EquipmentTemperature object = new EquipmentTemperature();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "EquipmentTemperature":
						object.EquipmentTemperature = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentTemperature();
				object.setEquipmentTemperature(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentTemperature();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentTemperature object) throws IOException {
		if(object?.EquipmentTemperature == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("EquipmentTemperature")
				jsonWriter.value(object.EquipmentTemperature)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentTemperature)
			}
		}
	}
}
class CommodityClassificationAdapter extends TypeAdapter<CommodityClassification> {

	@Override
	public CommodityClassification read(JsonReader jsonReader) throws IOException {

		CommodityClassification object = new CommodityClassification();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Type":
						object.attr_Type = jsonReader.nextString();
						break;

					case "CommodityClassification":
						object.CommodityClassification = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new CommodityClassification();
				object.setCommodityClassification(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new CommodityClassification();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, CommodityClassification object) throws IOException {
		if(object?.CommodityClassification == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Type != null ){
				jsonWriter.beginObject()

				if(object.attr_Type != null){
					jsonWriter.name("attr_Type")
					jsonWriter.value(object.attr_Type)
				}

				jsonWriter.name("CommodityClassification")
				jsonWriter.value(object.CommodityClassification)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.CommodityClassification)
			}
		}
	}
}
class EquipmentGasLevelAdapter extends TypeAdapter<EquipmentGasLevel> {

	@Override
	public EquipmentGasLevel read(JsonReader jsonReader) throws IOException {

		EquipmentGasLevel object = new EquipmentGasLevel();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Gas":
						object.attr_Gas = jsonReader.nextString();
						break;

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "EquipmentGasLevel":
						object.EquipmentGasLevel = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentGasLevel();
				object.setEquipmentGasLevel(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentGasLevel();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentGasLevel object) throws IOException {
		if(object?.EquipmentGasLevel == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Gas != null ||object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_Gas != null){
					jsonWriter.name("attr_Gas")
					jsonWriter.value(object.attr_Gas)
				}

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("EquipmentGasLevel")
				jsonWriter.value(object.EquipmentGasLevel)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentGasLevel)
			}
		}
	}
}