package cs.b2b.core.mapping.bean.edi.xml.br.inttra

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.gson.annotations.JsonAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set;;

class Message implements Serializable {
	public static final Set<String> MultiElementList = ['AddressLine','Instructions.ShipmentComments', 'MessageProperties.ReferenceInformation', 'Location.DateTime', 'PushNotificationContactInformation.CommunicationValue', 'PartnerInformation', 'Location', 'HazardousGoodsComments', 'HazardousGoods', 'EquipmentDetails'];
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
	String PartnerName;
	ContactInformation ContactInformation;
	AddressInformation AddressInformation;
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

class ContactInformation implements Serializable {
	String ContactName;
	CommunicationValue CommunicationValue;
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
	List<String> AddressLine = new ArrayList<String>();
}

class MessageBody implements Serializable {
	MessageProperties MessageProperties;
	MessageDetails MessageDetails;
}

class MessageProperties implements Serializable {
	ShipmentID ShipmentID;
	DateTime DateTime;
	ContactInformation ContactInformation;
	PushNotificationContactInformation PushNotificationContactInformation;
	List<ReferenceInformation> ReferenceInformation = new ArrayList<ReferenceInformation>();
	Instructions Instructions;
	HaulageDetails HaulageDetails;
	TransportationDetails TransportationDetails;
	Parties Parties;
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

class ShipmentID implements Serializable {
	ShipmentIdentifier ShipmentIdentifier;
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

class PushNotificationContactInformation implements Serializable {
	List<CommunicationValue> CommunicationValue = new ArrayList<CommunicationValue>();
}

class Instructions implements Serializable {
	List<ShipmentComments> ShipmentComments = new ArrayList<ShipmentComments>(); 
}

@JsonAdapter(ShipmentCommentsAdapter.class)
class ShipmentComments implements Serializable {
	String attr_CommentType;
	String ShipmentComments;
	@Override
	public String toString() {
		return this.ShipmentComments;
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
}

class Location implements Serializable {
	String attr_LocationType;
	LocationCode LocationCode;
	String LocationName;
	List<DateTime> DateTime = new ArrayList<DateTime>();
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

class MessageDetails implements Serializable {
	List<EquipmentDetails> EquipmentDetails = new ArrayList<EquipmentDetails>();
	GoodsDetails GoodsDetails;
}

class EquipmentDetails implements Serializable {
	String LineNumber;
	EquipmentType EquipmentType;
	String EquipmentCount;
	EquipmentTemperature EquipmentTemperature;
	EquipmentAirFlow EquipmentAirFlow;
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

class EquipmentType implements Serializable {
	String EquipmentTypeCode;
}

class GoodsDetails implements Serializable {
	String LineNumber;
	PackageDetail PackageDetail;
	PackageDetailComments PackageDetailComments;
	String HumidityPercentage;
	PackageDetailWeight PackageDetailWeight;
	List<HazardousGoods> HazardousGoods = new ArrayList<HazardousGoods>();
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

@JsonAdapter(PackageDetailWeightAdapter.class)
class PackageDetailWeight implements Serializable {
	String attr_UOM;
	String PackageDetailWeight;

	@Override
	public String toString() {
		return this.PackageDetailWeight;
	}
}

class PackageDetail implements Serializable {
	String NumberOfPackages;
	String PackageTypeCode;
}

class HazardousGoods implements Serializable {
	String attr_PackingGroupCode;
	String IMOClassCode;
	String IMDGPageNumber;
	String UNDGNumber;
	FlashpointTemperature FlashpointTemperature;
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
	ContactInformation ContactInformation;
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
		if(object?.HaulageDetails == null){
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

				jsonWriter.name("HaulageDetails")
				jsonWriter.value(object.HaulageDetails)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.HaulageDetails)
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
class PackageDetailWeightAdapter extends TypeAdapter<PackageDetailWeight> {

	@Override
	public PackageDetailWeight read(JsonReader jsonReader) throws IOException {

		PackageDetailWeight object = new PackageDetailWeight();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {

					case "attr_UOM":
						object.attr_UOM = jsonReader.nextString();
						break;

					case "PackageDetailWeight":
						object.PackageDetailWeight = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				object = new PackageDetailWeight();
				object.setPackageDetailWeight(jsonReader.nextString());
			}catch(IllegalStateException e2){
				object = new PackageDetailWeight();
				jsonReader.nextNull();
			}
		}

		return object;
	}

	@Override
	public void write(JsonWriter jsonWriter, PackageDetailWeight object) throws IOException {
		if(object?.PackageDetailWeight == null){
			jsonWriter.nullValue()
		}else{
			if(object.attr_UOM != null ){
				jsonWriter.beginObject()

				if(object.attr_UOM != null){
					jsonWriter.name("attr_UOM")
					jsonWriter.value(object.attr_UOM)
				}

				jsonWriter.name("PackageDetailWeight")
				jsonWriter.value(object.PackageDetailWeight)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(object.PackageDetailWeight)
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
			if(object.attr_CommentType != null ){
				jsonWriter.beginObject()
				
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
