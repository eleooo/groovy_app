package cs.b2b.core.mapping.bean.edi.xml.si.INTTRA

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class Message implements Serializable {
	public static final Set<String> MultiElementList = [
		'ContactInformation.CommunicationValue',
		'Street',
		'AddressLine',
		'Do',
		'mentationRequirements, ',
		'rtnerInformation',
		'Location',
		'ShipmentComments',
		'HeaderCustomsFilerInstruction',
		'ReferenceInformation',
		'ExportLicenseDetails',
		'EquipmentSeal.EquipmentSeal',
		'EquipmentComments',
		'HazardousGoodsComments',
		'DetailsCustomsFilerInstruction',
		'PackageMarks',
		'PackageDetailComments',
		'DetailsReferenceInformation',
		'ProductId',
		'HazardousGoods',
		'SplitGoodsDetails',
		'EquipmentDetails',
		'GoodsDetails',
		'Parties.PartnerInformation',
		'EmergencyResponseContact.ContactInformation',
		'PartnerInformation.DocumentationRequirements',
		'MessageProperties.ExportLicenseDetails',
		'LetterOfCreditDetails.DateTime',
		'ExportLicenseDetails.DateTime',
		'BlLocations.Location',
		'TransportationDetails.Location',
		'HeaderCustomsInformation.HeaderCustomsFilerInstruction',
		'GoodsDetails.PackageMarks',
		'MessageDetails.EquipmentDetails',
		'MessageDetails.GoodsDetails',
		'GoodsDetails.HazardousGoods',
		'GoodsDetails.SplitGoodsDetails',
		'DetailsCustomsInformation.DetailsCustomsFilerInstruction',
		'HouseParties.PartnerInformation',
		'MessageProperties.ReferenceInformation',
		'Instructions.ShipmentComments',
		'EquipmentDetails.EquipmentSeal',
		'EquipmentDetails.EquipmentComments',
		'EquipmentDetails.EquipmentReferenceInformation',
		'GoodsDetails.ProductId',
		'GoodsDetails.DetailsReferenceInformation',
		'HazardousGoods.HazardousGoodsComments',
		'Marks'	
	]

	String attr_noNamespaceSchemaLocation;
	Header Header;
	MessageBody MessageBody;
}

class Header implements Serializable {
	MessageType MessageType;
	String DocumentIdentifier;
	DateTime DateTime;
	Parties Parties;
}

@JsonAdapter(MessageTypeAdapter.class)
class MessageType implements Serializable {
	String attr_MessageVersion;
	String MessageType;

	@Override
	public String toString() {
		return this.MessageType;
	}
}

@JsonAdapter(DateTimeAdapter.class)
class DateTime implements Serializable {
	String attr_DateType;
	String DateTime;

	@Override
	public String toString() {
		return this.DateTime;
	}
}

class Parties implements Serializable {
	List<PartnerInformation> PartnerInformation = new ArrayList<PartnerInformation>();
}

class PartnerInformation implements Serializable {
	String attr_PartnerRole;
	PartnerIdentifier PartnerIdentifier;
	String PartnerName ;
	ContactInformation ContactInformation;
	AddressInformation AddressInformation;
	PartyReferenceInformation PartyReferenceInformation;
	List<DocumentationRequirements> DocumentationRequirements = new ArrayList<DocumentationRequirements>();
	
}

@JsonAdapter(PartnerIdentifierAdapter.class)
class PartnerIdentifier implements Serializable {
	String attr_Agency;
	String PartnerIdentifier;

	@Override
	public String toString() {
		return this.PartnerIdentifier;
	}
}

@JsonAdapter(PartyReferenceInformationAdapter.class)
class PartyReferenceInformation implements Serializable {
	String attr_ReferenceType;
	String PartyReferenceInformation;

	@Override
	public String toString() {
		return this.PartyReferenceInformation;
	}
}

class ContactInformation implements Serializable {
	ContactName ContactName;
	WebBLDocuments WebBLDocuments;
	List<CommunicationValue> CommunicationValue = new ArrayList<String>();
}

@JsonAdapter(ContactNameAdapter.class)
class ContactName implements Serializable {
	String attr_ContactType;
	String ContactName;

	@Override
	public String toString() {
		return this.ContactName;
	}
}
@JsonAdapter(WebBLDocumentsAdapter.class)
class WebBLDocuments implements Serializable {
	String attr_BLDocType;
	String WebBLDocuments;

	@Override
	public String toString() {
		return this.WebBLDocuments;
	}
}

@JsonAdapter(CommunicationValueAdapter.class)
class CommunicationValue implements Serializable {
	String attr_CommunicationType;
	String CommunicationValue;
	@Override
	public String toString() {
		return this.CommunicationValue;
	}
}

class AddressInformation implements Serializable {
	List<String> AddressLine ;
	String City;
	String StateProvince;
	String PostalCode;
	String CountryCode;
	//String Street ;
	List<String> Street ;
}

class DocumentationRequirements implements Serializable {
	Documents Documents;
	String Quantity;
}
@JsonAdapter(DocumentsAdapter.class)
class Documents implements Serializable {
	String attr_DocumentType;
	String attr_Freighted;
	String attr_Parent;
	String Documents;

	@Override
	public String toString() {
		return this.Documents;
	}
}

class MessageBody implements Serializable {
	MessageProperties MessageProperties;
	MessageDetails MessageDetails;
}

class MessageProperties implements Serializable {
	ShipmentID ShipmentID;
	DateTime DateTime;
	ShipmentDeclaredValue ShipmentDeclaredValue;
	LetterOfCreditDetails LetterOfCreditDetails;
	List<ExportLicenseDetails> ExportLicenseDetails = new ArrayList<ExportLicenseDetails>();
	BlLocations BlLocations;
	List<ReferenceInformation> ReferenceInformation = new ArrayList<ReferenceInformation>();
	Instructions Instructions;
	ControlTotal ControlTotal;
	HaulageDetails HaulageDetails;
	TransportationDetails TransportationDetails;
	Parties Parties;
	ShipmentIndicator ShipmentIndicator;
	HeaderCustomsInformation HeaderCustomsInformation;
	ChargeCategory ChargeCategory;
}

@JsonAdapter(ChargeCategoryAdapter.class)
class ChargeCategory implements Serializable {
	String attr_ChargeType;
	String attr_PrepaidorCollectIndicator;
	String ChargeCategory;
	
	public String toString() {
		return this.ChargeCategory;
	}
}
@JsonAdapter(ShipmentDeclaredValueAdapter.class)
class ShipmentDeclaredValue implements Serializable {
	String attr_Currency;
	String ShipmentDeclaredValue;

	@Override
	public String toString() {
		return this.ShipmentDeclaredValue;
	}
}
@JsonAdapter(ReferenceInformationAdapter.class)
class ReferenceInformation implements Serializable {
	String attr_ReferenceType;
	String ReferenceInformation;

	@Override
	public String toString() {
		return this.ReferenceInformation;
	}
}
@JsonAdapter(HaulageDetailsAdapter.class)
class HaulageDetails implements Serializable {
	String attr_MovementType;
	String attr_ServiceType;
	String HaulageDetails;

	@Override
	public String toString() {
		return this.HaulageDetails;
	}
}
@JsonAdapter(ShipmentIndicatorAdapter.class)
class ShipmentIndicator implements Serializable {
	String attr_IndicatorType;
	String ShipmentIndicator;

	@Override
	public String toString() {
		return this.ShipmentIndicator;
	}
}

class ShipmentID implements Serializable {
	ShipmentIdentifier ShipmentIdentifier;
	String DocumentVersion;
}
@JsonAdapter(ShipmentIdentifierAdapter.class)
class ShipmentIdentifier implements Serializable {
	String attr_MessageStatus;
	String ShipmentIdentifier;

	@Override
	public String toString() {
		return this.ShipmentIdentifier;
	}
}

class LetterOfCreditDetails implements Serializable {
	String LetterOfCreditNumber;
	List<DateTime> DateTime = new ArrayList<DateTime>();
}

class ExportLicenseDetails implements Serializable {
	String ExportLicenseNumber;
	List<DateTime> DateTime = new ArrayList<DateTime>();
}

class BlLocations implements Serializable {
	List<Location> Location = new ArrayList<Location>();
}

class Location implements Serializable {
	String attr_LocationType;
	LocationCode LocationCode;
	String LocationName;
	String LocationCountry;
	DateTime DateTime;
}

@JsonAdapter(LocationCodeAdapter.class)
class LocationCode implements Serializable {
	String attr_Agency;
	String LocationCode;

	@Override
	public String toString() {
		return this.LocationCode;
	}
}

class Instructions implements Serializable {
	List<ShipmentComments> ShipmentComments =new ArrayList<ShipmentComments>();
}
@JsonAdapter(ShipmentCommentsAdapter.class)
class ShipmentComments implements Serializable {
	String attr_ClauseType;
	String attr_CommentType;
	String ShipmentComments;
	@Override
	public String toString() {
		return this.ShipmentComments;
	}
}

class ControlTotal implements Serializable {
	String NumberOfEquipment;
	String NumberOfPackages ;
	GrossWeight GrossWeight;
	GrossVolume GrossVolume;
	NetWeight NetWeight;
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
@JsonAdapter(GrossVolumeAdapter.class)
class GrossVolume implements Serializable {
	String attr_UOM;
	String GrossVolume;

	@Override
	public String toString() {
		return this.GrossVolume;
	}
}
@JsonAdapter(NetWeightAdapter.class)
class NetWeight implements Serializable {
	String attr_UOM;
	String NetWeight;

	@Override
	public String toString() {
		return this.NetWeight;
	}
}

class TransportationDetails implements Serializable {
	String attr_TransportMode;
	String attr_TransportStage;
	ConveyanceInformation ConveyanceInformation;
	List<Location> Location = new ArrayList<Location>();
}

class ConveyanceInformation implements Serializable {
	String ConveyanceName;
	String VoyageTripNumber;
	String CarrierSCAC;
	TransportIdentification TransportIdentification;
}
@JsonAdapter(TransportIdentificationAdapter.class)
class TransportIdentification implements Serializable {
	String attr_TransportIdentificationType;
	String TransportIdentification;

	@Override
	public String toString() {
		return this.TransportIdentification;
	}
}

class HeaderCustomsInformation implements Serializable {
	List<HeaderCustomsFilerInstruction> HeaderCustomsFilerInstruction = new ArrayList<HeaderCustomsFilerInstruction>();
}

class HeaderCustomsFilerInstruction implements Serializable {
	String attr_ManifestFilerStatus;
	ManifestFilingCountryCode ManifestFilingCountryCode;
	String ManifestFilingIdentification;
}
@JsonAdapter(ManifestFilingCountryCodeAdapter.class)
class ManifestFilingCountryCode implements Serializable {
	String attr_Agency;
	String ManifestFilingCountryCode;

	@Override
	public String toString() {
		return this.ManifestFilingCountryCode;
	}
}

class MessageDetails implements Serializable {
	List<EquipmentDetails> EquipmentDetails = new ArrayList<EquipmentDetails>();
	List<GoodsDetails> GoodsDetails = new ArrayList<GoodsDetails>();
}

class EquipmentDetails implements Serializable {
	String LineNumber;
	EquipmentIdentifier EquipmentIdentifier;
	EquipmentType EquipmentType;
	EquipmentGrossWeight EquipmentGrossWeight;
	EquipmentTareWeight EquipmentTareWeight;
	EquipmentGrossVolume EquipmentGrossVolume;
	EquipmentTemperature EquipmentTemperature;
	EquipmentAirFlow EquipmentAirFlow;
	EquipmentLocation EquipmentLocation;

	List<EquipmentSeal> EquipmentSeal =  new ArrayList<EquipmentSeal>();
	List<EquipmentComments> EquipmentComments =  new ArrayList<EquipmentComments>();
	List<EquipmentReferenceInformation> EquipmentReferenceInformation =  new ArrayList<EquipmentReferenceInformation>();
}
@JsonAdapter(EquipmentIdentifierAdapter.class)
class EquipmentIdentifier implements Serializable {
	String attr_EquipmentSupplier;
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
@JsonAdapter(EquipmentTareWeightAdapter.class)
class EquipmentTareWeight implements Serializable {
	String attr_UOM;
	String EquipmentTareWeight;

	@Override
	public String toString() {
		return this.EquipmentTareWeight;
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
@JsonAdapter(EquipmentTemperatureAdapter.class)
class EquipmentTemperature implements Serializable {
	String attr_UOM;
	String EquipmentTemperature;

	@Override
	public String toString() {
		return this.EquipmentTemperature;
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
@JsonAdapter(EquipmentSealAdapter.class)
class EquipmentSeal implements Serializable {
	String attr_SealingParty;
	String EquipmentSeal;
	@Override
	public String toString() {
		return this.EquipmentSeal;
	}
}
@JsonAdapter(EquipmentCommentsAdapter.class)
class EquipmentComments implements Serializable {
	String attr_CommentType;
	String EquipmentComments;
	@Override
	public String toString() {
		return this.EquipmentComments;
	}
}

class EquipmentReferenceInformation implements Serializable {
	String attr_ReferenceType;
	String EquipmentReferenceInformation;
}

class EquipmentType implements Serializable {
	String EquipmentTypeCode;
	String EquipmentDescription;
}

class EquipmentLocation implements Serializable {
	String attr_LocationType;
	LocationCode LocationCode;
	String LocationName;
	String LocationCountry;
}

class GoodsDetails implements Serializable {
	String LineNumber;
	PackageDetail PackageDetail;
	List<PackageDetailComments> PackageDetailComments = new ArrayList<PackageDetailComments>();
	List<ProductId> ProductId = new ArrayList<ProductId>();
	PackageDetailGrossVolume PackageDetailGrossVolume;
	PackageDetailGrossWeight PackageDetailGrossWeight;
	List<DetailsReferenceInformation> DetailsReferenceInformation = new ArrayList<DetailsReferenceInformation>();
	List<PackageMarks> PackageMarks = new ArrayList<PackageMarks>();
	List<SplitGoodsDetails> SplitGoodsDetails = new ArrayList<SplitGoodsDetails>();
	List<HazardousGoods> HazardousGoods = new ArrayList<HazardousGoods>();
	DetailsCustomsInformation DetailsCustomsInformation;
	HouseParties HouseParties;
	PackageDetailNetWeight PackageDetailNetWeight;
}
@JsonAdapter(PackageDetailCommentsAdapter.class)
class PackageDetailComments implements Serializable {
	String attr_CommentType;
	String PackageDetailComments;
	@Override
	public String toString() {
		return this.PackageDetailComments;
	}
}
@JsonAdapter(ProductIdAdapter.class)
class ProductId implements Serializable {
	String attr_ItemTypeIdCode;
	String ProductId;
	@Override
	public String toString() {
		return this.ProductId;
	}
}
@JsonAdapter(PackageDetailGrossVolumeAdapter.class)
class PackageDetailGrossVolume implements Serializable {
	String attr_UOM;
	String PackageDetailGrossVolume;

	@Override
	public String toString() {
		return this.PackageDetailGrossVolume;
	}
}
@JsonAdapter(PackageDetailGrossWeightAdapter.class)
class PackageDetailGrossWeight implements Serializable {
	String attr_UOM;
	String PackageDetailGrossWeight;

	@Override
	public String toString() {
		return this.PackageDetailGrossWeight;
	}
}
@JsonAdapter(DetailsReferenceInformationAdapter.class)
class DetailsReferenceInformation implements Serializable {
	String attr_ReferenceType;
	String DetailsReferenceInformation;
	@Override
	public String toString() {
		return this.DetailsReferenceInformation;
	}
}
@JsonAdapter(PackageDetailNetWeightAdapter.class)
class PackageDetailNetWeight implements Serializable {
	String attr_UOM;
	String PackageDetailNetWeight;

	@Override
	public String toString() {
		return this.PackageDetailNetWeight;
	}
}

class PackageDetail implements Serializable {
	String attr_Level;
	String NumberOfPackages;
	String PackageTypeCode;
	String PackageTypeDescription;
}

class PackageMarks implements Serializable {
	List<String> Marks = new ArrayList<String>();
}

class SplitGoodsDetails implements Serializable {
	EquipmentIdentifier EquipmentIdentifier;
	String SplitGoodsNumberOfPackages;
	SplitGoodsGrossVolume SplitGoodsGrossVolume;
	SplitGoodsGrossWeight SplitGoodsGrossWeight;
}
@JsonAdapter(SplitGoodsGrossVolumeAdapter.class)
class SplitGoodsGrossVolume implements Serializable {
	String attr_UOM;
	String SplitGoodsGrossVolume;

	@Override
	public String toString() {
		return this.SplitGoodsGrossVolume;
	}
}
@JsonAdapter(SplitGoodsGrossWeightAdapter.class)
class SplitGoodsGrossWeight implements Serializable {
	String attr_UOM;
	String SplitGoodsGrossWeight;

	@Override
	public String toString() {
		return this.SplitGoodsGrossWeight;
	}
}

class HazardousGoods implements Serializable {
	String attr_PackingGroupCode;
	String IMOClassCode;
	String IMDGPageNumber;
	String UNDGNumber;
	FlashpointTemperature FlashpointTemperature;
	String EMSNumber;
	List<HazardousGoodsComments> HazardousGoodsComments = new ArrayList<HazardousGoodsComments>();
	EmergencyResponseContact EmergencyResponseContact;
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
@JsonAdapter(HazardousGoodsCommentsAdapter.class)
class HazardousGoodsComments implements Serializable {
	String attr_CommentType;
	String HazardousGoodsComments;
	@Override
	public String toString() {
		return this.HazardousGoodsComments;
	}
}

class EmergencyResponseContact implements Serializable {
	List<ContactInformation> ContactInformation = new ArrayList<ContactInformation>();
}

class DetailsCustomsInformation implements Serializable {
	List<DetailsCustomsFilerInstruction> DetailsCustomsFilerInstruction = new ArrayList<DetailsCustomsFilerInstruction>();
}

class DetailsCustomsFilerInstruction implements Serializable {
	String attr_ManifestFilerStatus;
	ManifestFilingCountryCode ManifestFilingCountryCode;
	String ManifestFilingIdentification;
}

class HouseParties implements Serializable {
	List<PartnerInformation> PartnerInformation = new ArrayList<PartnerInformation>();
}





class MessageTypeAdapter extends TypeAdapter<MessageType> {

	@Override
	public MessageType read(JsonReader jsonReader) throws IOException {

		MessageType object = new MessageType();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_MessageVersion":
						object.attr_MessageVersion = jsonReader.nextString();
						break;

					case "MessageType":
						object.MessageType = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new MessageType();
				object.setMessageType(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new MessageType();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, MessageType object) throws IOException {
		if(object?.MessageType == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_MessageVersion != null ){
				jsonWriter.beginObject()

				if(object.attr_MessageVersion != null){
					jsonWriter.name("attr_MessageVersion")
					jsonWriter.value(object.attr_MessageVersion)
				}

				jsonWriter.name("MessageType")
				jsonWriter.value(object.MessageType)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.MessageType)
			}
		}
	}
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
			if(object.attr_DateType != null ){
				jsonWriter.beginObject()

				if(object.attr_DateType != null){
					jsonWriter.name("attr_DateType")
					jsonWriter.value(object.attr_DateType)
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


class PartnerIdentifierAdapter extends TypeAdapter<PartnerIdentifier> {

	@Override
	public PartnerIdentifier read(JsonReader jsonReader) throws IOException {

		PartnerIdentifier object = new PartnerIdentifier();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Agency":
						object.attr_Agency = jsonReader.nextString();
						break;

					case "PartnerIdentifier":
						object.PartnerIdentifier = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new PartnerIdentifier();
				object.setPartnerIdentifier(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new PartnerIdentifier();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, PartnerIdentifier object) throws IOException {
		if(object?.PartnerIdentifier == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Agency != null ){
				jsonWriter.beginObject()

				if(object.attr_Agency != null){
					jsonWriter.name("attr_Agency")
					jsonWriter.value(object.attr_Agency)
				}

				jsonWriter.name("PartnerIdentifier")
				jsonWriter.value(object.PartnerIdentifier)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.PartnerIdentifier)
			}
		}
	}
}


class PartyReferenceInformationAdapter extends TypeAdapter<PartyReferenceInformation> {

	@Override
	public PartyReferenceInformation read(JsonReader jsonReader) throws IOException {

		PartyReferenceInformation object = new PartyReferenceInformation();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_ReferenceType":
						object.attr_ReferenceType = jsonReader.nextString();
						break;

					case "PartyReferenceInformation":
						object.PartyReferenceInformation = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new PartyReferenceInformation();
				object.setPartyReferenceInformation(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new PartyReferenceInformation();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, PartyReferenceInformation object) throws IOException {
		if(object?.PartyReferenceInformation == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_ReferenceType != null ){
				jsonWriter.beginObject()

				if(object.attr_ReferenceType != null){
					jsonWriter.name("attr_ReferenceType")
					jsonWriter.value(object.attr_ReferenceType)
				}

				jsonWriter.name("PartyReferenceInformation")
				jsonWriter.value(object.PartyReferenceInformation)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.PartyReferenceInformation)
			}
		}
	}
}


class ContactNameAdapter extends TypeAdapter<ContactName> {

	@Override
	public ContactName read(JsonReader jsonReader) throws IOException {

		ContactName object = new ContactName();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_ContactType":
						object.attr_ContactType = jsonReader.nextString();
						break;

					case "ContactName":
						object.ContactName = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ContactName();
				object.setContactName(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ContactName();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, ContactName object) throws IOException {
		if(object?.ContactName == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_ContactType != null ){
				jsonWriter.beginObject()

				if(object.attr_ContactType != null){
					jsonWriter.name("attr_ContactType")
					jsonWriter.value(object.attr_ContactType)
				}

				jsonWriter.name("ContactName")
				jsonWriter.value(object.ContactName)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ContactName)
			}
		}
	}
}


class WebBLDocumentsAdapter extends TypeAdapter<WebBLDocuments> {

	@Override
	public WebBLDocuments read(JsonReader jsonReader) throws IOException {

		WebBLDocuments object = new WebBLDocuments();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_BLDocType":
						object.attr_BLDocType = jsonReader.nextString();
						break;

					case "WebBLDocuments":
						object.WebBLDocuments = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new WebBLDocuments();
				object.setWebBLDocuments(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new WebBLDocuments();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, WebBLDocuments object) throws IOException {
		if(object?.WebBLDocuments == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_BLDocType != null ){
				jsonWriter.beginObject()

				if(object.attr_BLDocType != null){
					jsonWriter.name("attr_BLDocType")
					jsonWriter.value(object.attr_BLDocType)
				}

				jsonWriter.name("WebBLDocuments")
				jsonWriter.value(object.WebBLDocuments)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.WebBLDocuments)
			}
		}
	}
}


class DocumentsAdapter extends TypeAdapter<Documents> {

	@Override
	public Documents read(JsonReader jsonReader) throws IOException {

		Documents object = new Documents();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_DocumentType":
						object.attr_DocumentType = jsonReader.nextString();
						break;

					case "attr_Freighted":
						object.attr_Freighted = jsonReader.nextString();
						break;

					case "attr_Parent":
						object.attr_Parent = jsonReader.nextString();
						break;

					case "Documents":
						object.Documents = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new Documents();
				object.setDocuments(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new Documents();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, Documents object) throws IOException {
		if(object?.Documents == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_DocumentType != null ||object.attr_Freighted != null ||object.attr_Parent != null ){
				jsonWriter.beginObject()

				if(object.attr_DocumentType != null){
					jsonWriter.name("attr_DocumentType")
					jsonWriter.value(object.attr_DocumentType)
				}

				if(object.attr_Freighted != null){
					jsonWriter.name("attr_Freighted")
					jsonWriter.value(object.attr_Freighted)
				}

				if(object.attr_Parent != null){
					jsonWriter.name("attr_Parent")
					jsonWriter.value(object.attr_Parent)
				}

				jsonWriter.name("Documents")
				jsonWriter.value(object.Documents)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.Documents)
			}
		}
	}
}


class ShipmentDeclaredValueAdapter extends TypeAdapter<ShipmentDeclaredValue> {

	@Override
	public ShipmentDeclaredValue read(JsonReader jsonReader) throws IOException {

		ShipmentDeclaredValue object = new ShipmentDeclaredValue();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Currency":
						object.attr_Currency = jsonReader.nextString();
						break;

					case "ShipmentDeclaredValue":
						object.ShipmentDeclaredValue = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ShipmentDeclaredValue();
				object.setShipmentDeclaredValue(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ShipmentDeclaredValue();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, ShipmentDeclaredValue object) throws IOException {
		if(object?.ShipmentDeclaredValue == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Currency != null ){
				jsonWriter.beginObject()

				if(object.attr_Currency != null){
					jsonWriter.name("attr_Currency")
					jsonWriter.value(object.attr_Currency)
				}

				jsonWriter.name("ShipmentDeclaredValue")
				jsonWriter.value(object.ShipmentDeclaredValue)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ShipmentDeclaredValue)
			}
		}
	}
}


class HaulageDetailsAdapter extends TypeAdapter<HaulageDetails> {

	@Override
	public HaulageDetails read(JsonReader jsonReader) throws IOException {

		HaulageDetails object = new HaulageDetails();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_MovementType":
						object.attr_MovementType = jsonReader.nextString();
						break;

					case "attr_ServiceType":
						object.attr_ServiceType = jsonReader.nextString();
						break;

					case "HaulageDetails":
						object.HaulageDetails = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new HaulageDetails();
				object.setHaulageDetails(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new HaulageDetails();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, HaulageDetails object) throws IOException {
		if(object == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_MovementType != null ||object.attr_ServiceType != null ){
				jsonWriter.beginObject()

				if(object.attr_MovementType != null){
					jsonWriter.name("attr_MovementType")
					jsonWriter.value(object.attr_MovementType)
				}

				if(object.attr_ServiceType != null){
					jsonWriter.name("attr_ServiceType")
					jsonWriter.value(object.attr_ServiceType)
				}

				if(object.HaulageDetails){
					jsonWriter.name("HaulageDetails")
					jsonWriter.value(object.HaulageDetails)
				}
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.HaulageDetails)
			}
		}
	}
}


class ShipmentIndicatorAdapter extends TypeAdapter<ShipmentIndicator> {

	@Override
	public ShipmentIndicator read(JsonReader jsonReader) throws IOException {

		ShipmentIndicator object = new ShipmentIndicator();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_IndicatorType":
						object.attr_IndicatorType = jsonReader.nextString();
						break;

					case "ShipmentIndicator":
						object.ShipmentIndicator = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ShipmentIndicator();
				object.setShipmentIndicator(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ShipmentIndicator();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, ShipmentIndicator object) throws IOException {
		if(object?.ShipmentIndicator == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_IndicatorType != null ){
				jsonWriter.beginObject()

				if(object.attr_IndicatorType != null){
					jsonWriter.name("attr_IndicatorType")
					jsonWriter.value(object.attr_IndicatorType)
				}

				jsonWriter.name("ShipmentIndicator")
				jsonWriter.value(object.ShipmentIndicator)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ShipmentIndicator)
			}
		}
	}
}


class ShipmentIdentifierAdapter extends TypeAdapter<ShipmentIdentifier> {

	@Override
	public ShipmentIdentifier read(JsonReader jsonReader) throws IOException {

		ShipmentIdentifier object = new ShipmentIdentifier();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_MessageStatus":
						object.attr_MessageStatus = jsonReader.nextString();
						break;

					case "ShipmentIdentifier":
						object.ShipmentIdentifier = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ShipmentIdentifier();
				object.setShipmentIdentifier(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ShipmentIdentifier();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, ShipmentIdentifier object) throws IOException {
		if(object?.ShipmentIdentifier == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_MessageStatus != null ){
				jsonWriter.beginObject()

				if(object.attr_MessageStatus != null){
					jsonWriter.name("attr_MessageStatus")
					jsonWriter.value(object.attr_MessageStatus)
				}

				jsonWriter.name("ShipmentIdentifier")
				jsonWriter.value(object.ShipmentIdentifier)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ShipmentIdentifier)
			}
		}
	}
}


class LocationCodeAdapter extends TypeAdapter<LocationCode> {

	@Override
	public LocationCode read(JsonReader jsonReader) throws IOException {

		LocationCode object = new LocationCode();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Agency":
						object.attr_Agency = jsonReader.nextString();
						break;

					case "LocationCode":
						object.LocationCode = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new LocationCode();
				object.setLocationCode(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new LocationCode();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, LocationCode object) throws IOException {
		if(object?.LocationCode == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Agency != null ){
				jsonWriter.beginObject()

				if(object.attr_Agency != null){
					jsonWriter.name("attr_Agency")
					jsonWriter.value(object.attr_Agency)
				}

				jsonWriter.name("LocationCode")
				jsonWriter.value(object.LocationCode)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.LocationCode)
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


class NetWeightAdapter extends TypeAdapter<NetWeight> {

	@Override
	public NetWeight read(JsonReader jsonReader) throws IOException {

		NetWeight object = new NetWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "NetWeight":
						object.NetWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new NetWeight();
				object.setNetWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new NetWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, NetWeight object) throws IOException {
		if(object?.NetWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("NetWeight")
				jsonWriter.value(object.NetWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.NetWeight)
			}
		}
	}
}


class TransportIdentificationAdapter extends TypeAdapter<TransportIdentification> {

	@Override
	public TransportIdentification read(JsonReader jsonReader) throws IOException {

		TransportIdentification object = new TransportIdentification();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_TransportIdentificationType":
						object.attr_TransportIdentificationType = jsonReader.nextString();
						break;

					case "TransportIdentification":
						object.TransportIdentification = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new TransportIdentification();
				object.setTransportIdentification(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new TransportIdentification();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, TransportIdentification object) throws IOException {
		if(object?.TransportIdentification == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_TransportIdentificationType != null ){
				jsonWriter.beginObject()

				if(object.attr_TransportIdentificationType != null){
					jsonWriter.name("attr_TransportIdentificationType")
					jsonWriter.value(object.attr_TransportIdentificationType)
				}

				jsonWriter.name("TransportIdentification")
				jsonWriter.value(object.TransportIdentification)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.TransportIdentification)
			}
		}
	}
}


class ManifestFilingCountryCodeAdapter extends TypeAdapter<ManifestFilingCountryCode> {

	@Override
	public ManifestFilingCountryCode read(JsonReader jsonReader) throws IOException {

		ManifestFilingCountryCode object = new ManifestFilingCountryCode();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_Agency":
						object.attr_Agency = jsonReader.nextString();
						break;

					case "ManifestFilingCountryCode":
						object.ManifestFilingCountryCode = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ManifestFilingCountryCode();
				object.setManifestFilingCountryCode(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ManifestFilingCountryCode();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, ManifestFilingCountryCode object) throws IOException {
		if(object?.ManifestFilingCountryCode == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_Agency != null ){
				jsonWriter.beginObject()

				if(object.attr_Agency != null){
					jsonWriter.name("attr_Agency")
					jsonWriter.value(object.attr_Agency)
				}

				jsonWriter.name("ManifestFilingCountryCode")
				jsonWriter.value(object.ManifestFilingCountryCode)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ManifestFilingCountryCode)
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

					case "attr_EquipmentSupplier":
						object.attr_EquipmentSupplier = jsonReader.nextString();
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
			if(object.attr_EquipmentSupplier != null ){
				jsonWriter.beginObject()

				if(object.attr_EquipmentSupplier != null){
					jsonWriter.name("attr_EquipmentSupplier")
					jsonWriter.value(object.attr_EquipmentSupplier)
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


class EquipmentTareWeightAdapter extends TypeAdapter<EquipmentTareWeight> {

	@Override
	public EquipmentTareWeight read(JsonReader jsonReader) throws IOException {

		EquipmentTareWeight object = new EquipmentTareWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "EquipmentTareWeight":
						object.EquipmentTareWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentTareWeight();
				object.setEquipmentTareWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentTareWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentTareWeight object) throws IOException {
		if(object?.EquipmentTareWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("EquipmentTareWeight")
				jsonWriter.value(object.EquipmentTareWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentTareWeight)
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


class PackageDetailGrossVolumeAdapter extends TypeAdapter<PackageDetailGrossVolume> {

	@Override
	public PackageDetailGrossVolume read(JsonReader jsonReader) throws IOException {

		PackageDetailGrossVolume object = new PackageDetailGrossVolume();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "PackageDetailGrossVolume":
						object.PackageDetailGrossVolume = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new PackageDetailGrossVolume();
				object.setPackageDetailGrossVolume(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new PackageDetailGrossVolume();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, PackageDetailGrossVolume object) throws IOException {
		if(object?.PackageDetailGrossVolume == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("PackageDetailGrossVolume")
				jsonWriter.value(object.PackageDetailGrossVolume)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.PackageDetailGrossVolume)
			}
		}
	}
}


class PackageDetailGrossWeightAdapter extends TypeAdapter<PackageDetailGrossWeight> {

	@Override
	public PackageDetailGrossWeight read(JsonReader jsonReader) throws IOException {

		PackageDetailGrossWeight object = new PackageDetailGrossWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "PackageDetailGrossWeight":
						object.PackageDetailGrossWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new PackageDetailGrossWeight();
				object.setPackageDetailGrossWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new PackageDetailGrossWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, PackageDetailGrossWeight object) throws IOException {
		if(object?.PackageDetailGrossWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("PackageDetailGrossWeight")
				jsonWriter.value(object.PackageDetailGrossWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.PackageDetailGrossWeight)
			}
		}
	}
}


class PackageDetailNetWeightAdapter extends TypeAdapter<PackageDetailNetWeight> {

	@Override
	public PackageDetailNetWeight read(JsonReader jsonReader) throws IOException {

		PackageDetailNetWeight object = new PackageDetailNetWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "PackageDetailNetWeight":
						object.PackageDetailNetWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new PackageDetailNetWeight();
				object.setPackageDetailNetWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new PackageDetailNetWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, PackageDetailNetWeight object) throws IOException {
		if(object?.PackageDetailNetWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("PackageDetailNetWeight")
				jsonWriter.value(object.PackageDetailNetWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.PackageDetailNetWeight)
			}
		}
	}
}


class SplitGoodsGrossVolumeAdapter extends TypeAdapter<SplitGoodsGrossVolume> {

	@Override
	public SplitGoodsGrossVolume read(JsonReader jsonReader) throws IOException {

		SplitGoodsGrossVolume object = new SplitGoodsGrossVolume();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "SplitGoodsGrossVolume":
						object.SplitGoodsGrossVolume = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new SplitGoodsGrossVolume();
				object.setSplitGoodsGrossVolume(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new SplitGoodsGrossVolume();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, SplitGoodsGrossVolume object) throws IOException {
		if(object?.SplitGoodsGrossVolume == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("SplitGoodsGrossVolume")
				jsonWriter.value(object.SplitGoodsGrossVolume)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.SplitGoodsGrossVolume)
			}
		}
	}
}


class SplitGoodsGrossWeightAdapter extends TypeAdapter<SplitGoodsGrossWeight> {

	@Override
	public SplitGoodsGrossWeight read(JsonReader jsonReader) throws IOException {

		SplitGoodsGrossWeight object = new SplitGoodsGrossWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "SplitGoodsGrossWeight":
						object.SplitGoodsGrossWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new SplitGoodsGrossWeight();
				object.setSplitGoodsGrossWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new SplitGoodsGrossWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, SplitGoodsGrossWeight object) throws IOException {
		if(object?.SplitGoodsGrossWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("SplitGoodsGrossWeight")
				jsonWriter.value(object.SplitGoodsGrossWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.SplitGoodsGrossWeight)
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
class CommunicationValueAdapter extends TypeAdapter<CommunicationValue> {

	@Override
	public CommunicationValue read(JsonReader jsonReader) throws IOException {

		CommunicationValue object = new CommunicationValue();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_CommunicationType":
						object.attr_CommunicationType = jsonReader.nextString();
						break;

					case "CommunicationValue":
						object.CommunicationValue = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new CommunicationValue();
				object.setCommunicationValue(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new CommunicationValue();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, CommunicationValue object) throws IOException {
		if(object?.CommunicationValue == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_CommunicationType != null ){
				jsonWriter.beginObject()

				if(object.attr_CommunicationType != null){
					jsonWriter.name("attr_CommunicationType")
					jsonWriter.value(object.attr_CommunicationType)
				}

				jsonWriter.name("CommunicationValue")
				jsonWriter.value(object.CommunicationValue)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.CommunicationValue)
			}
		}
	}
}
class ReferenceInformationAdapter extends TypeAdapter<ReferenceInformation> {

	@Override
	public ReferenceInformation read(JsonReader jsonReader) throws IOException {

		ReferenceInformation object = new ReferenceInformation();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_ReferenceType":
						object.attr_ReferenceType = jsonReader.nextString();
						break;

					case "ReferenceInformation":
						object.ReferenceInformation = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ReferenceInformation();
				object.setReferenceInformation(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ReferenceInformation();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, ReferenceInformation object) throws IOException {
		if(object?.ReferenceInformation == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_ReferenceType != null ){
				jsonWriter.beginObject()

				if(object.attr_ReferenceType != null){
					jsonWriter.name("attr_ReferenceType")
					jsonWriter.value(object.attr_ReferenceType)
				}

				jsonWriter.name("ReferenceInformation")
				jsonWriter.value(object.ReferenceInformation)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ReferenceInformation)
			}
		}
	}
}
class ShipmentCommentsAdapter extends TypeAdapter<ShipmentComments> {

	@Override
	public ShipmentComments read(JsonReader jsonReader) throws IOException {

		ShipmentComments object = new ShipmentComments();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_ClauseType":
						object.attr_ClauseType = jsonReader.nextString();
						break;

					case "attr_CommentType":
						object.attr_CommentType = jsonReader.nextString();
						break;

					case "ShipmentComments":
						object.ShipmentComments = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ShipmentComments();
				object.setShipmentComments(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ShipmentComments();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, ShipmentComments object) throws IOException {
		if(object?.ShipmentComments == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_ClauseType != null ||object.attr_CommentType != null ){
				jsonWriter.beginObject()

				if(object.attr_ClauseType != null){
					jsonWriter.name("attr_ClauseType")
					jsonWriter.value(object.attr_ClauseType)
				}

				if(object.attr_CommentType != null){
					jsonWriter.name("attr_CommentType")
					jsonWriter.value(object.attr_CommentType)
				}

				jsonWriter.name("ShipmentComments")
				jsonWriter.value(object.ShipmentComments)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ShipmentComments)
			}
		}
	}
}

class EquipmentSealAdapter extends TypeAdapter<EquipmentSeal> {

	@Override
	public EquipmentSeal read(JsonReader jsonReader) throws IOException {

		EquipmentSeal object = new EquipmentSeal();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_SealingParty":
						object.attr_SealingParty = jsonReader.nextString();
						break;

					case "EquipmentSeal":
						object.EquipmentSeal = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentSeal();
				object.setEquipmentSeal(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentSeal();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentSeal object) throws IOException {
		if(object?.EquipmentSeal == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_SealingParty != null ){
				jsonWriter.beginObject()

				if(object.attr_SealingParty != null){
					jsonWriter.name("attr_SealingParty")
					jsonWriter.value(object.attr_SealingParty)
				}

				jsonWriter.name("EquipmentSeal")
				jsonWriter.value(object.EquipmentSeal)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentSeal)
			}
		}
	}
}


class EquipmentCommentsAdapter extends TypeAdapter<EquipmentComments> {

	@Override
	public EquipmentComments read(JsonReader jsonReader) throws IOException {

		EquipmentComments object = new EquipmentComments();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_CommentType":
						object.attr_CommentType = jsonReader.nextString();
						break;

					case "EquipmentComments":
						object.EquipmentComments = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new EquipmentComments();
				object.setEquipmentComments(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new EquipmentComments();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, EquipmentComments object) throws IOException {
		if(object?.EquipmentComments == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_CommentType != null ){
				jsonWriter.beginObject()

				if(object.attr_CommentType != null){
					jsonWriter.name("attr_CommentType")
					jsonWriter.value(object.attr_CommentType)
				}

				jsonWriter.name("EquipmentComments")
				jsonWriter.value(object.EquipmentComments)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.EquipmentComments)
			}
		}
	}
}

class PackageDetailCommentsAdapter extends TypeAdapter<PackageDetailComments> {

	@Override
	public PackageDetailComments read(JsonReader jsonReader) throws IOException {

		PackageDetailComments object = new PackageDetailComments();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_CommentType":
						object.attr_CommentType = jsonReader.nextString();
						break;

					case "PackageDetailComments":
						object.PackageDetailComments = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new PackageDetailComments();
				object.setPackageDetailComments(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new PackageDetailComments();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, PackageDetailComments object) throws IOException {
		if(object?.PackageDetailComments == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_CommentType != null ){
				jsonWriter.beginObject()

				if(object.attr_CommentType != null){
					jsonWriter.name("attr_CommentType")
					jsonWriter.value(object.attr_CommentType)
				}

				jsonWriter.name("PackageDetailComments")
				jsonWriter.value(object.PackageDetailComments)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.PackageDetailComments)
			}
		}
	}
}


class ProductIdAdapter extends TypeAdapter<ProductId> {

	@Override
	public ProductId read(JsonReader jsonReader) throws IOException {

		ProductId object = new ProductId();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_ItemTypeIdCode":
						object.attr_ItemTypeIdCode = jsonReader.nextString();
						break;

					case "ProductId":
						object.ProductId = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ProductId();
				object.setProductId(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ProductId();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, ProductId object) throws IOException {
		if(object?.ProductId == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_ItemTypeIdCode != null ){
				jsonWriter.beginObject()

				if(object.attr_ItemTypeIdCode != null){
					jsonWriter.name("attr_ItemTypeIdCode")
					jsonWriter.value(object.attr_ItemTypeIdCode)
				}

				jsonWriter.name("ProductId")
				jsonWriter.value(object.ProductId)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ProductId)
			}
		}
	}
}

class DetailsReferenceInformationAdapter extends TypeAdapter<DetailsReferenceInformation> {

	@Override
	public DetailsReferenceInformation read(JsonReader jsonReader) throws IOException {

		DetailsReferenceInformation object = new DetailsReferenceInformation();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_ReferenceType":
						object.attr_ReferenceType = jsonReader.nextString();
						break;

					case "DetailsReferenceInformation":
						object.DetailsReferenceInformation = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new DetailsReferenceInformation();
				object.setDetailsReferenceInformation(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new DetailsReferenceInformation();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, DetailsReferenceInformation object) throws IOException {
		if(object?.DetailsReferenceInformation == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_ReferenceType != null ){
				jsonWriter.beginObject()

				if(object.attr_ReferenceType != null){
					jsonWriter.name("attr_ReferenceType")
					jsonWriter.value(object.attr_ReferenceType)
				}

				jsonWriter.name("DetailsReferenceInformation")
				jsonWriter.value(object.DetailsReferenceInformation)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.DetailsReferenceInformation)
			}
		}
	}
}


class ChargeCategoryAdapter extends TypeAdapter<ChargeCategory> {
	
	@Override
	public ChargeCategory read(JsonReader jsonReader) throws IOException {
		
		ChargeCategory object = new ChargeCategory();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					
					case "attr_ChargeType":
						object.attr_ChargeType = jsonReader.nextString();
						break;

					case "attr_PrepaidorCollectIndicator":
						object.attr_PrepaidorCollectIndicator = jsonReader.nextString();
						break;

					case "ChargeCategory":
						object.ChargeCategory = jsonReader.nextString();
						break;

				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new ChargeCategory();
				object.setChargeCategory(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new ChargeCategory();
				jsonReader.nextNull();
			}
		}
		
		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, ChargeCategory object) throws IOException {
		if(object?.ChargeCategory == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_ChargeType != null ||object.attr_PrepaidorCollectIndicator != null ){
				jsonWriter.beginObject()
				
				if(object.attr_ChargeType != null){
					jsonWriter.name("attr_ChargeType")
					jsonWriter.value(object.attr_ChargeType)
				}

				if(object.attr_PrepaidorCollectIndicator != null){
					jsonWriter.name("attr_PrepaidorCollectIndicator")
					jsonWriter.value(object.attr_PrepaidorCollectIndicator)
				}

				jsonWriter.name("ChargeCategory")
				jsonWriter.value(object.ChargeCategory)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.ChargeCategory)
			}
		}
	}
}



class HazardousGoodsCommentsAdapter extends TypeAdapter<HazardousGoodsComments> {

	@Override
	public HazardousGoodsComments read(JsonReader jsonReader) throws IOException {

		HazardousGoodsComments object = new HazardousGoodsComments();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_CommentType":
						object.attr_CommentType = jsonReader.nextString();
						break;

					case "HazardousGoodsComments":
						object.HazardousGoodsComments = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new HazardousGoodsComments();
				object.setHazardousGoodsComments(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new HazardousGoodsComments();
				jsonReader.nextNull();
			}
		}

		return object;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, HazardousGoodsComments object) throws IOException {
		if(object?.HazardousGoodsComments == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_CommentType != null ){
				jsonWriter.beginObject()

				if(object.attr_CommentType != null){
					jsonWriter.name("attr_CommentType")
					jsonWriter.value(object.attr_CommentType)
				}

				jsonWriter.name("HazardousGoodsComments")
				jsonWriter.value(object.HazardousGoodsComments)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.HazardousGoodsComments)
			}
		}
	}
}