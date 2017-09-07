import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

class Message implements Serializable {
    public static final Set<String> MultiElementList=["CommunicationValue", "AddressLine", "PartnerInformation", "Location", "Marks", "PackageDetailComments", "GoodsDetails", "EquipmentDetails"]
    String Header;
    String MessageBody;
}

class Header implements Serializable {
    MessageType MessageType;
    String DocumentIdentifier;
    DateTime DateTime;
    String Parties;
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
    String ContactInformation;
    String AddressInformation;
    String ChargeDetails;
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
    List<CommunicationValue> CommunicationValue = new ArrayList<CommunicationValue>();
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

class ChargeDetails implements Serializable {
    ChargeCategory ChargeCategory;
    String ChargeInvoice;
    ChargeLocation ChargeLocation;
    ChargeAmount ChargeAmount;
}

@JsonAdapter(ChargeCategoryAdapter.class)
class ChargeCategory implements Serializable {
    String attr_ChargeType;
    String attr_PrepaidorCollectIndicator;
    String ChargeCategory;

    @Override
    public String toString() {
        return this.ChargeCategory;
    }
}

@JsonAdapter(ChargeAmountAdapter.class)
class ChargeAmount implements Serializable {
    String attr_Currency;
    String ChargeAmount;

    @Override
    public String toString() {
        return this.ChargeAmount;
    }
}

class ChargeLocation implements Serializable {
    String attr_LocationType;
    LocationCode LocationCode;
    String LocationName;
    String LocationState;
    String LocationCountry;
    String LocationCountryCode;
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

class MessageBody implements Serializable {
    String MessageProperties;
    String MessageDetails;
}

class MessageProperties implements Serializable {
    String ShipmentID;
    DateTime DateTime;
    ShipmentDeclaredValue ShipmentDeclaredValue;
    String LetterOfCreditDetails;
    String ExportLicenseDetails;
    String BlLocations;
    ReferenceInformation ReferenceInformation;
    String Instructions;
    String ControlTotal;
    HaulageDetails HaulageDetails;
    TransportationDetails TransportationDetails;
    String Parties;
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

class ShipmentID implements Serializable {
    ShipmentIdentifier ShipmentIdentifier;
    String DocumentVersion;
}

@JsonAdapter(ShipmentIdentifierAdapter.class)
class ShipmentIdentifier implements Serializable {
    String attr_MessageIdentifier;
    String attr_MessageStatus;
    String ShipmentIdentifier;

    @Override
    public String toString() {
        return this.ShipmentIdentifier;
    }
}

class LetterOfCreditDetails implements Serializable {
    String LetterOfCreditNumber;
    DateTime DateTime;
    String LCIssueDateFreeText;
    String LCExpiryDateFreeText;
}

class ExportLicenseDetails implements Serializable {
    String ExportLicenseNumber;
    DateTime DateTime;
}

class BlLocations implements Serializable {
    List<Location> Location = new ArrayList<Location>();
}

class Location implements Serializable {
    String attr_LocationType;
    LocationCode LocationCode;
    String LocationName;
    String LocationState;
    String LocationCountry;
    String LocationCountryCode;
    DateTime DateTime;
}

class Instructions implements Serializable {
    ShipmentComments ShipmentComments;
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
    String NumberOfPackages;
    GrossWeight GrossWeight;
    GrossVolume GrossVolume;
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

class TransportationDetails implements Serializable {
    String attr_TransportMode;
    String attr_TransportStage;
    String ConveyanceInformation;
    List<Location> Location = new ArrayList<Location>();
}

class ConveyanceInformation implements Serializable {
    String ConveyanceName;
    String VoyageTripNumber;
    String CarrierSCAC;
    TransportIdentification TransportIdentification;
    TransportMeans TransportMeans;
    String TransportNationality;
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

@JsonAdapter(TransportMeansAdapter.class)
class TransportMeans implements Serializable {
    String attr_TransportMeansType;
    String TransportMeans;

    @Override
    public String toString() {
        return this.TransportMeans;
    }
}

class MessageDetails implements Serializable {
    List<String> EquipmentDetails = new ArrayList<String>();
    List<String> GoodsDetails = new ArrayList<String>();
}

class EquipmentDetails implements Serializable {
    String LineNumber;
    EquipmentIdentifier EquipmentIdentifier;
    String EquipmentType;
    EquipmentGrossWeight EquipmentGrossWeight;
    EquipmentGrossVolume EquipmentGrossVolume;
    EquipmentTemperature EquipmentTemperature;
    String EquipmentNonActive;
    EquipmentAirFlow EquipmentAirFlow;
    EquipmentSeal EquipmentSeal;
    EquipmentComments EquipmentComments;
    EquipmentReferenceInformation EquipmentReferenceInformation;
    EquipmentHazardousGoods EquipmentHazardousGoods;
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

@JsonAdapter(EquipmentReferenceInformationAdapter.class)
class EquipmentReferenceInformation implements Serializable {
    String attr_ReferenceType;
    String EquipmentReferenceInformation;

    @Override
    public String toString() {
        return this.EquipmentReferenceInformation;
    }
}

class EquipmentType implements Serializable {
    EquipmentTypeCode EquipmentTypeCode;
    String EquipmentDescription;
}

@JsonAdapter(EquipmentTypeCodeAdapter.class)
class EquipmentTypeCode implements Serializable {
    String attr_NonActiveReefer;
    String EquipmentTypeCode;

    @Override
    public String toString() {
        return this.EquipmentTypeCode;
    }
}

class EquipmentHazardousGoods implements Serializable {
    String attr_PackingGroupCode;
    String IMOClassCode;
    String IMDGPageNumber;
    String UNDGNumber;
    FlashpointTemperature FlashpointTemperature;
    String EMSNumber;
    HazardousGoodsComments HazardousGoodsComments;
    String EmergencyResponseContact;
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
    String ContactInformation;
}

class GoodsDetails implements Serializable {
    String LineNumber;
    PackageDetail PackageDetail;
    List<PackageDetailComments> PackageDetailComments = new ArrayList<PackageDetailComments>();
    ProductId ProductId;
    PackageDetailGrossVolume PackageDetailGrossVolume;
    PackageDetailGrossWeight PackageDetailGrossWeight;
    DetailsReferenceInformation DetailsReferenceInformation;
    String ExportLicenseDetails;
    String PackageMarks;
    String SplitGoodsDetails;
    HazardousGoods HazardousGoods;
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
    HazardousGoodsComments HazardousGoodsComments;
    String EmergencyResponseContact;
}



public class EquipmentIdentifierAdapter extends TypeAdapter<EquipmentIdentifier>{
    @Override
    public EquipmentIdentifier read(JsonReader jsonReader) throws IOException {
        EquipmentIdentifier equipmentIdentifier = new EquipmentIdentifier();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'EquipmentIdentifier':
                        equipmentIdentifier.EquipmentIdentifier = jsonReader.nextString();
                        break;
                    case 'attr_EquipmentSupplier':
                        equipmentIdentifier.attr_EquipmentSupplier = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                equipmentIdentifier = new EquipmentIdentifier();
                equipmentIdentifier.EquipmentIdentifier = jsonReader.nextString();
            }catch(IllegalStateException e2){
                equipmentIdentifier = new EquipmentIdentifier();
                jsonReader.nextNull();
            }
        }
        return equipmentIdentifier
    }

    @Override
    public void write(JsonWriter jsonWriter,EquipmentIdentifier equipmentIdentifier) throws IOException {
        if(equipmentIdentifier?.EquipmentIdentifier == null){
            jsonWriter.nullValue()
        }else{
            if(equipmentIdentifier.attr_EquipmentSupplier != null ){
                jsonWriter.beginObject()
                if(equipmentIdentifier.attr_EquipmentSupplier != null){
                    jsonWriter.name("attr_EquipmentSupplier")
                    jsonWriter.value(equipmentIdentifier.attr_EquipmentSupplier)
                }
                jsonWriter.name("EquipmentIdentifier")
                jsonWriter.value(equipmentIdentifier.EquipmentIdentifier)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(equipmentIdentifier.EquipmentIdentifier)
            }
        }
    }
}

public class EquipmentGrossWeightAdapter extends TypeAdapter<EquipmentGrossWeight>{
    @Override
    public EquipmentGrossWeight read(JsonReader jsonReader) throws IOException {
        EquipmentGrossWeight equipmentGrossWeight = new EquipmentGrossWeight();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'EquipmentGrossWeight':
                        equipmentGrossWeight.EquipmentGrossWeight = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        equipmentGrossWeight.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                equipmentGrossWeight = new EquipmentGrossWeight();
                equipmentGrossWeight.EquipmentGrossWeight = jsonReader.nextString();
            }catch(IllegalStateException e2){
                equipmentGrossWeight = new EquipmentGrossWeight();
                jsonReader.nextNull();
            }
        }
        return equipmentGrossWeight
    }

    @Override
    public void write(JsonWriter jsonWriter,EquipmentGrossWeight equipmentGrossWeight) throws IOException {
        if(equipmentGrossWeight?.EquipmentGrossWeight == null){
            jsonWriter.nullValue()
        }else{
            if(equipmentGrossWeight.attr_UOM != null ){
                jsonWriter.beginObject()
                if(equipmentGrossWeight.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(equipmentGrossWeight.attr_UOM)
                }
                jsonWriter.name("EquipmentGrossWeight")
                jsonWriter.value(equipmentGrossWeight.EquipmentGrossWeight)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(equipmentGrossWeight.EquipmentGrossWeight)
            }
        }
    }
}

public class HaulageDetailsAdapter extends TypeAdapter<HaulageDetails>{
    @Override
    public HaulageDetails read(JsonReader jsonReader) throws IOException {
        HaulageDetails haulageDetails = new HaulageDetails();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'HaulageDetails':
                        haulageDetails.HaulageDetails = jsonReader.nextString();
                        break;
                    case 'attr_MovementType':
                        haulageDetails.attr_MovementType = jsonReader.nextString();
                        break;
                    case 'attr_ServiceType':
                        haulageDetails.attr_ServiceType = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                haulageDetails = new HaulageDetails();
                haulageDetails.HaulageDetails = jsonReader.nextString();
            }catch(IllegalStateException e2){
                haulageDetails = new HaulageDetails();
                jsonReader.nextNull();
            }
        }
        return haulageDetails
    }

    @Override
    public void write(JsonWriter jsonWriter,HaulageDetails haulageDetails) throws IOException {
        if(haulageDetails?.HaulageDetails == null){
            jsonWriter.nullValue()
        }else{
            if(haulageDetails.attr_MovementType != null || haulageDetails.attr_ServiceType != null ){
                jsonWriter.beginObject()
                if(haulageDetails.attr_MovementType != null){
                    jsonWriter.name("attr_MovementType")
                    jsonWriter.value(haulageDetails.attr_MovementType)
                }
                if(haulageDetails.attr_ServiceType != null){
                    jsonWriter.name("attr_ServiceType")
                    jsonWriter.value(haulageDetails.attr_ServiceType)
                }
                jsonWriter.name("HaulageDetails")
                jsonWriter.value(haulageDetails.HaulageDetails)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(haulageDetails.HaulageDetails)
            }
        }
    }
}

public class PackageDetailGrossWeightAdapter extends TypeAdapter<PackageDetailGrossWeight>{
    @Override
    public PackageDetailGrossWeight read(JsonReader jsonReader) throws IOException {
        PackageDetailGrossWeight packageDetailGrossWeight = new PackageDetailGrossWeight();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'PackageDetailGrossWeight':
                        packageDetailGrossWeight.PackageDetailGrossWeight = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        packageDetailGrossWeight.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                packageDetailGrossWeight = new PackageDetailGrossWeight();
                packageDetailGrossWeight.PackageDetailGrossWeight = jsonReader.nextString();
            }catch(IllegalStateException e2){
                packageDetailGrossWeight = new PackageDetailGrossWeight();
                jsonReader.nextNull();
            }
        }
        return packageDetailGrossWeight
    }

    @Override
    public void write(JsonWriter jsonWriter,PackageDetailGrossWeight packageDetailGrossWeight) throws IOException {
        if(packageDetailGrossWeight?.PackageDetailGrossWeight == null){
            jsonWriter.nullValue()
        }else{
            if(packageDetailGrossWeight.attr_UOM != null ){
                jsonWriter.beginObject()
                if(packageDetailGrossWeight.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(packageDetailGrossWeight.attr_UOM)
                }
                jsonWriter.name("PackageDetailGrossWeight")
                jsonWriter.value(packageDetailGrossWeight.PackageDetailGrossWeight)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(packageDetailGrossWeight.PackageDetailGrossWeight)
            }
        }
    }
}

public class EquipmentReferenceInformationAdapter extends TypeAdapter<EquipmentReferenceInformation>{
    @Override
    public EquipmentReferenceInformation read(JsonReader jsonReader) throws IOException {
        EquipmentReferenceInformation equipmentReferenceInformation = new EquipmentReferenceInformation();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_ReferenceType':
                        equipmentReferenceInformation.attr_ReferenceType = jsonReader.nextString();
                        break;
                    case 'EquipmentReferenceInformation':
                        equipmentReferenceInformation.EquipmentReferenceInformation = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                equipmentReferenceInformation = new EquipmentReferenceInformation();
                equipmentReferenceInformation.EquipmentReferenceInformation = jsonReader.nextString();
            }catch(IllegalStateException e2){
                equipmentReferenceInformation = new EquipmentReferenceInformation();
                jsonReader.nextNull();
            }
        }
        return equipmentReferenceInformation
    }

    @Override
    public void write(JsonWriter jsonWriter,EquipmentReferenceInformation equipmentReferenceInformation) throws IOException {
        if(equipmentReferenceInformation?.EquipmentReferenceInformation == null){
            jsonWriter.nullValue()
        }else{
            if(equipmentReferenceInformation.attr_ReferenceType != null ){
                jsonWriter.beginObject()
                if(equipmentReferenceInformation.attr_ReferenceType != null){
                    jsonWriter.name("attr_ReferenceType")
                    jsonWriter.value(equipmentReferenceInformation.attr_ReferenceType)
                }
                jsonWriter.name("EquipmentReferenceInformation")
                jsonWriter.value(equipmentReferenceInformation.EquipmentReferenceInformation)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(equipmentReferenceInformation.EquipmentReferenceInformation)
            }
        }
    }
}

public class ReferenceInformationAdapter extends TypeAdapter<ReferenceInformation>{
    @Override
    public ReferenceInformation read(JsonReader jsonReader) throws IOException {
        ReferenceInformation referenceInformation = new ReferenceInformation();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_ReferenceType':
                        referenceInformation.attr_ReferenceType = jsonReader.nextString();
                        break;
                    case 'ReferenceInformation':
                        referenceInformation.ReferenceInformation = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                referenceInformation = new ReferenceInformation();
                referenceInformation.ReferenceInformation = jsonReader.nextString();
            }catch(IllegalStateException e2){
                referenceInformation = new ReferenceInformation();
                jsonReader.nextNull();
            }
        }
        return referenceInformation
    }

    @Override
    public void write(JsonWriter jsonWriter,ReferenceInformation referenceInformation) throws IOException {
        if(referenceInformation?.ReferenceInformation == null){
            jsonWriter.nullValue()
        }else{
            if(referenceInformation.attr_ReferenceType != null ){
                jsonWriter.beginObject()
                if(referenceInformation.attr_ReferenceType != null){
                    jsonWriter.name("attr_ReferenceType")
                    jsonWriter.value(referenceInformation.attr_ReferenceType)
                }
                jsonWriter.name("ReferenceInformation")
                jsonWriter.value(referenceInformation.ReferenceInformation)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(referenceInformation.ReferenceInformation)
            }
        }
    }
}

public class ProductIdAdapter extends TypeAdapter<ProductId>{
    @Override
    public ProductId read(JsonReader jsonReader) throws IOException {
        ProductId productId = new ProductId();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_ItemTypeIdCode':
                        productId.attr_ItemTypeIdCode = jsonReader.nextString();
                        break;
                    case 'ProductId':
                        productId.ProductId = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                productId = new ProductId();
                productId.ProductId = jsonReader.nextString();
            }catch(IllegalStateException e2){
                productId = new ProductId();
                jsonReader.nextNull();
            }
        }
        return productId
    }

    @Override
    public void write(JsonWriter jsonWriter,ProductId productId) throws IOException {
        if(productId?.ProductId == null){
            jsonWriter.nullValue()
        }else{
            if(productId.attr_ItemTypeIdCode != null ){
                jsonWriter.beginObject()
                if(productId.attr_ItemTypeIdCode != null){
                    jsonWriter.name("attr_ItemTypeIdCode")
                    jsonWriter.value(productId.attr_ItemTypeIdCode)
                }
                jsonWriter.name("ProductId")
                jsonWriter.value(productId.ProductId)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(productId.ProductId)
            }
        }
    }
}

public class ChargeAmountAdapter extends TypeAdapter<ChargeAmount>{
    @Override
    public ChargeAmount read(JsonReader jsonReader) throws IOException {
        ChargeAmount chargeAmount = new ChargeAmount();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_Currency':
                        chargeAmount.attr_Currency = jsonReader.nextString();
                        break;
                    case 'ChargeAmount':
                        chargeAmount.ChargeAmount = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                chargeAmount = new ChargeAmount();
                chargeAmount.ChargeAmount = jsonReader.nextString();
            }catch(IllegalStateException e2){
                chargeAmount = new ChargeAmount();
                jsonReader.nextNull();
            }
        }
        return chargeAmount
    }

    @Override
    public void write(JsonWriter jsonWriter,ChargeAmount chargeAmount) throws IOException {
        if(chargeAmount?.ChargeAmount == null){
            jsonWriter.nullValue()
        }else{
            if(chargeAmount.attr_Currency != null ){
                jsonWriter.beginObject()
                if(chargeAmount.attr_Currency != null){
                    jsonWriter.name("attr_Currency")
                    jsonWriter.value(chargeAmount.attr_Currency)
                }
                jsonWriter.name("ChargeAmount")
                jsonWriter.value(chargeAmount.ChargeAmount)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(chargeAmount.ChargeAmount)
            }
        }
    }
}

public class DateTimeAdapter extends TypeAdapter<DateTime>{
    @Override
    public DateTime read(JsonReader jsonReader) throws IOException {
        DateTime dateTime = new DateTime();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_DateType':
                        dateTime.attr_DateType = jsonReader.nextString();
                        break;
                    case 'DateTime':
                        dateTime.DateTime = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                dateTime = new DateTime();
                dateTime.DateTime = jsonReader.nextString();
            }catch(IllegalStateException e2){
                dateTime = new DateTime();
                jsonReader.nextNull();
            }
        }
        return dateTime
    }

    @Override
    public void write(JsonWriter jsonWriter,DateTime dateTime) throws IOException {
        if(dateTime?.DateTime == null){
            jsonWriter.nullValue()
        }else{
            if(dateTime.attr_DateType != null ){
                jsonWriter.beginObject()
                if(dateTime.attr_DateType != null){
                    jsonWriter.name("attr_DateType")
                    jsonWriter.value(dateTime.attr_DateType)
                }
                jsonWriter.name("DateTime")
                jsonWriter.value(dateTime.DateTime)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(dateTime.DateTime)
            }
        }
    }
}

public class EquipmentSealAdapter extends TypeAdapter<EquipmentSeal>{
    @Override
    public EquipmentSeal read(JsonReader jsonReader) throws IOException {
        EquipmentSeal equipmentSeal = new EquipmentSeal();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_SealingParty':
                        equipmentSeal.attr_SealingParty = jsonReader.nextString();
                        break;
                    case 'EquipmentSeal':
                        equipmentSeal.EquipmentSeal = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                equipmentSeal = new EquipmentSeal();
                equipmentSeal.EquipmentSeal = jsonReader.nextString();
            }catch(IllegalStateException e2){
                equipmentSeal = new EquipmentSeal();
                jsonReader.nextNull();
            }
        }
        return equipmentSeal
    }

    @Override
    public void write(JsonWriter jsonWriter,EquipmentSeal equipmentSeal) throws IOException {
        if(equipmentSeal?.EquipmentSeal == null){
            jsonWriter.nullValue()
        }else{
            if(equipmentSeal.attr_SealingParty != null ){
                jsonWriter.beginObject()
                if(equipmentSeal.attr_SealingParty != null){
                    jsonWriter.name("attr_SealingParty")
                    jsonWriter.value(equipmentSeal.attr_SealingParty)
                }
                jsonWriter.name("EquipmentSeal")
                jsonWriter.value(equipmentSeal.EquipmentSeal)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(equipmentSeal.EquipmentSeal)
            }
        }
    }
}

public class ShipmentIdentifierAdapter extends TypeAdapter<ShipmentIdentifier>{
    @Override
    public ShipmentIdentifier read(JsonReader jsonReader) throws IOException {
        ShipmentIdentifier shipmentIdentifier = new ShipmentIdentifier();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'ShipmentIdentifier':
                        shipmentIdentifier.ShipmentIdentifier = jsonReader.nextString();
                        break;
                    case 'attr_MessageStatus':
                        shipmentIdentifier.attr_MessageStatus = jsonReader.nextString();
                        break;
                    case 'attr_MessageIdentifier':
                        shipmentIdentifier.attr_MessageIdentifier = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                shipmentIdentifier = new ShipmentIdentifier();
                shipmentIdentifier.ShipmentIdentifier = jsonReader.nextString();
            }catch(IllegalStateException e2){
                shipmentIdentifier = new ShipmentIdentifier();
                jsonReader.nextNull();
            }
        }
        return shipmentIdentifier
    }

    @Override
    public void write(JsonWriter jsonWriter,ShipmentIdentifier shipmentIdentifier) throws IOException {
        if(shipmentIdentifier?.ShipmentIdentifier == null){
            jsonWriter.nullValue()
        }else{
            if(shipmentIdentifier.attr_MessageStatus != null || shipmentIdentifier.attr_MessageIdentifier != null ){
                jsonWriter.beginObject()
                if(shipmentIdentifier.attr_MessageStatus != null){
                    jsonWriter.name("attr_MessageStatus")
                    jsonWriter.value(shipmentIdentifier.attr_MessageStatus)
                }
                if(shipmentIdentifier.attr_MessageIdentifier != null){
                    jsonWriter.name("attr_MessageIdentifier")
                    jsonWriter.value(shipmentIdentifier.attr_MessageIdentifier)
                }
                jsonWriter.name("ShipmentIdentifier")
                jsonWriter.value(shipmentIdentifier.ShipmentIdentifier)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(shipmentIdentifier.ShipmentIdentifier)
            }
        }
    }
}

public class PartnerIdentifierAdapter extends TypeAdapter<PartnerIdentifier>{
    @Override
    public PartnerIdentifier read(JsonReader jsonReader) throws IOException {
        PartnerIdentifier partnerIdentifier = new PartnerIdentifier();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'PartnerIdentifier':
                        partnerIdentifier.PartnerIdentifier = jsonReader.nextString();
                        break;
                    case 'attr_Agency':
                        partnerIdentifier.attr_Agency = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                partnerIdentifier = new PartnerIdentifier();
                partnerIdentifier.PartnerIdentifier = jsonReader.nextString();
            }catch(IllegalStateException e2){
                partnerIdentifier = new PartnerIdentifier();
                jsonReader.nextNull();
            }
        }
        return partnerIdentifier
    }

    @Override
    public void write(JsonWriter jsonWriter,PartnerIdentifier partnerIdentifier) throws IOException {
        if(partnerIdentifier?.PartnerIdentifier == null){
            jsonWriter.nullValue()
        }else{
            if(partnerIdentifier.attr_Agency != null ){
                jsonWriter.beginObject()
                if(partnerIdentifier.attr_Agency != null){
                    jsonWriter.name("attr_Agency")
                    jsonWriter.value(partnerIdentifier.attr_Agency)
                }
                jsonWriter.name("PartnerIdentifier")
                jsonWriter.value(partnerIdentifier.PartnerIdentifier)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(partnerIdentifier.PartnerIdentifier)
            }
        }
    }
}

public class FlashpointTemperatureAdapter extends TypeAdapter<FlashpointTemperature>{
    @Override
    public FlashpointTemperature read(JsonReader jsonReader) throws IOException {
        FlashpointTemperature flashpointTemperature = new FlashpointTemperature();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'FlashpointTemperature':
                        flashpointTemperature.FlashpointTemperature = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        flashpointTemperature.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                flashpointTemperature = new FlashpointTemperature();
                flashpointTemperature.FlashpointTemperature = jsonReader.nextString();
            }catch(IllegalStateException e2){
                flashpointTemperature = new FlashpointTemperature();
                jsonReader.nextNull();
            }
        }
        return flashpointTemperature
    }

    @Override
    public void write(JsonWriter jsonWriter,FlashpointTemperature flashpointTemperature) throws IOException {
        if(flashpointTemperature?.FlashpointTemperature == null){
            jsonWriter.nullValue()
        }else{
            if(flashpointTemperature.attr_UOM != null ){
                jsonWriter.beginObject()
                if(flashpointTemperature.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(flashpointTemperature.attr_UOM)
                }
                jsonWriter.name("FlashpointTemperature")
                jsonWriter.value(flashpointTemperature.FlashpointTemperature)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(flashpointTemperature.FlashpointTemperature)
            }
        }
    }
}

public class ShipmentCommentsAdapter extends TypeAdapter<ShipmentComments>{
    @Override
    public ShipmentComments read(JsonReader jsonReader) throws IOException {
        ShipmentComments shipmentComments = new ShipmentComments();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_ClauseType':
                        shipmentComments.attr_ClauseType = jsonReader.nextString();
                        break;
                    case 'ShipmentComments':
                        shipmentComments.ShipmentComments = jsonReader.nextString();
                        break;
                    case 'attr_CommentType':
                        shipmentComments.attr_CommentType = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                shipmentComments = new ShipmentComments();
                shipmentComments.ShipmentComments = jsonReader.nextString();
            }catch(IllegalStateException e2){
                shipmentComments = new ShipmentComments();
                jsonReader.nextNull();
            }
        }
        return shipmentComments
    }

    @Override
    public void write(JsonWriter jsonWriter,ShipmentComments shipmentComments) throws IOException {
        if(shipmentComments?.ShipmentComments == null){
            jsonWriter.nullValue()
        }else{
            if(shipmentComments.attr_ClauseType != null || shipmentComments.attr_CommentType != null ){
                jsonWriter.beginObject()
                if(shipmentComments.attr_ClauseType != null){
                    jsonWriter.name("attr_ClauseType")
                    jsonWriter.value(shipmentComments.attr_ClauseType)
                }
                if(shipmentComments.attr_CommentType != null){
                    jsonWriter.name("attr_CommentType")
                    jsonWriter.value(shipmentComments.attr_CommentType)
                }
                jsonWriter.name("ShipmentComments")
                jsonWriter.value(shipmentComments.ShipmentComments)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(shipmentComments.ShipmentComments)
            }
        }
    }
}

public class EquipmentGrossVolumeAdapter extends TypeAdapter<EquipmentGrossVolume>{
    @Override
    public EquipmentGrossVolume read(JsonReader jsonReader) throws IOException {
        EquipmentGrossVolume equipmentGrossVolume = new EquipmentGrossVolume();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'EquipmentGrossVolume':
                        equipmentGrossVolume.EquipmentGrossVolume = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        equipmentGrossVolume.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                equipmentGrossVolume = new EquipmentGrossVolume();
                equipmentGrossVolume.EquipmentGrossVolume = jsonReader.nextString();
            }catch(IllegalStateException e2){
                equipmentGrossVolume = new EquipmentGrossVolume();
                jsonReader.nextNull();
            }
        }
        return equipmentGrossVolume
    }

    @Override
    public void write(JsonWriter jsonWriter,EquipmentGrossVolume equipmentGrossVolume) throws IOException {
        if(equipmentGrossVolume?.EquipmentGrossVolume == null){
            jsonWriter.nullValue()
        }else{
            if(equipmentGrossVolume.attr_UOM != null ){
                jsonWriter.beginObject()
                if(equipmentGrossVolume.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(equipmentGrossVolume.attr_UOM)
                }
                jsonWriter.name("EquipmentGrossVolume")
                jsonWriter.value(equipmentGrossVolume.EquipmentGrossVolume)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(equipmentGrossVolume.EquipmentGrossVolume)
            }
        }
    }
}

public class EquipmentAirFlowAdapter extends TypeAdapter<EquipmentAirFlow>{
    @Override
    public EquipmentAirFlow read(JsonReader jsonReader) throws IOException {
        EquipmentAirFlow equipmentAirFlow = new EquipmentAirFlow();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'EquipmentAirFlow':
                        equipmentAirFlow.EquipmentAirFlow = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        equipmentAirFlow.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                equipmentAirFlow = new EquipmentAirFlow();
                equipmentAirFlow.EquipmentAirFlow = jsonReader.nextString();
            }catch(IllegalStateException e2){
                equipmentAirFlow = new EquipmentAirFlow();
                jsonReader.nextNull();
            }
        }
        return equipmentAirFlow
    }

    @Override
    public void write(JsonWriter jsonWriter,EquipmentAirFlow equipmentAirFlow) throws IOException {
        if(equipmentAirFlow?.EquipmentAirFlow == null){
            jsonWriter.nullValue()
        }else{
            if(equipmentAirFlow.attr_UOM != null ){
                jsonWriter.beginObject()
                if(equipmentAirFlow.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(equipmentAirFlow.attr_UOM)
                }
                jsonWriter.name("EquipmentAirFlow")
                jsonWriter.value(equipmentAirFlow.EquipmentAirFlow)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(equipmentAirFlow.EquipmentAirFlow)
            }
        }
    }
}

public class EquipmentCommentsAdapter extends TypeAdapter<EquipmentComments>{
    @Override
    public EquipmentComments read(JsonReader jsonReader) throws IOException {
        EquipmentComments equipmentComments = new EquipmentComments();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_CommentType':
                        equipmentComments.attr_CommentType = jsonReader.nextString();
                        break;
                    case 'EquipmentComments':
                        equipmentComments.EquipmentComments = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                equipmentComments = new EquipmentComments();
                equipmentComments.EquipmentComments = jsonReader.nextString();
            }catch(IllegalStateException e2){
                equipmentComments = new EquipmentComments();
                jsonReader.nextNull();
            }
        }
        return equipmentComments
    }

    @Override
    public void write(JsonWriter jsonWriter,EquipmentComments equipmentComments) throws IOException {
        if(equipmentComments?.EquipmentComments == null){
            jsonWriter.nullValue()
        }else{
            if(equipmentComments.attr_CommentType != null ){
                jsonWriter.beginObject()
                if(equipmentComments.attr_CommentType != null){
                    jsonWriter.name("attr_CommentType")
                    jsonWriter.value(equipmentComments.attr_CommentType)
                }
                jsonWriter.name("EquipmentComments")
                jsonWriter.value(equipmentComments.EquipmentComments)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(equipmentComments.EquipmentComments)
            }
        }
    }
}

public class MessageTypeAdapter extends TypeAdapter<MessageType>{
    @Override
    public MessageType read(JsonReader jsonReader) throws IOException {
        MessageType messageType = new MessageType();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_MessageVersion':
                        messageType.attr_MessageVersion = jsonReader.nextString();
                        break;
                    case 'MessageType':
                        messageType.MessageType = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                messageType = new MessageType();
                messageType.MessageType = jsonReader.nextString();
            }catch(IllegalStateException e2){
                messageType = new MessageType();
                jsonReader.nextNull();
            }
        }
        return messageType
    }

    @Override
    public void write(JsonWriter jsonWriter,MessageType messageType) throws IOException {
        if(messageType?.MessageType == null){
            jsonWriter.nullValue()
        }else{
            if(messageType.attr_MessageVersion != null ){
                jsonWriter.beginObject()
                if(messageType.attr_MessageVersion != null){
                    jsonWriter.name("attr_MessageVersion")
                    jsonWriter.value(messageType.attr_MessageVersion)
                }
                jsonWriter.name("MessageType")
                jsonWriter.value(messageType.MessageType)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(messageType.MessageType)
            }
        }
    }
}

public class CommunicationValueAdapter extends TypeAdapter<CommunicationValue>{
    @Override
    public CommunicationValue read(JsonReader jsonReader) throws IOException {
        CommunicationValue communicationValue = new CommunicationValue();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'CommunicationValue':
                        communicationValue.CommunicationValue = jsonReader.nextString();
                        break;
                    case 'attr_CommunicationType':
                        communicationValue.attr_CommunicationType = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                communicationValue = new CommunicationValue();
                communicationValue.CommunicationValue = jsonReader.nextString();
            }catch(IllegalStateException e2){
                communicationValue = new CommunicationValue();
                jsonReader.nextNull();
            }
        }
        return communicationValue
    }

    @Override
    public void write(JsonWriter jsonWriter,CommunicationValue communicationValue) throws IOException {
        if(communicationValue?.CommunicationValue == null){
            jsonWriter.nullValue()
        }else{
            if(communicationValue.attr_CommunicationType != null ){
                jsonWriter.beginObject()
                if(communicationValue.attr_CommunicationType != null){
                    jsonWriter.name("attr_CommunicationType")
                    jsonWriter.value(communicationValue.attr_CommunicationType)
                }
                jsonWriter.name("CommunicationValue")
                jsonWriter.value(communicationValue.CommunicationValue)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(communicationValue.CommunicationValue)
            }
        }
    }
}

public class DetailsReferenceInformationAdapter extends TypeAdapter<DetailsReferenceInformation>{
    @Override
    public DetailsReferenceInformation read(JsonReader jsonReader) throws IOException {
        DetailsReferenceInformation detailsReferenceInformation = new DetailsReferenceInformation();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_ReferenceType':
                        detailsReferenceInformation.attr_ReferenceType = jsonReader.nextString();
                        break;
                    case 'DetailsReferenceInformation':
                        detailsReferenceInformation.DetailsReferenceInformation = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                detailsReferenceInformation = new DetailsReferenceInformation();
                detailsReferenceInformation.DetailsReferenceInformation = jsonReader.nextString();
            }catch(IllegalStateException e2){
                detailsReferenceInformation = new DetailsReferenceInformation();
                jsonReader.nextNull();
            }
        }
        return detailsReferenceInformation
    }

    @Override
    public void write(JsonWriter jsonWriter,DetailsReferenceInformation detailsReferenceInformation) throws IOException {
        if(detailsReferenceInformation?.DetailsReferenceInformation == null){
            jsonWriter.nullValue()
        }else{
            if(detailsReferenceInformation.attr_ReferenceType != null ){
                jsonWriter.beginObject()
                if(detailsReferenceInformation.attr_ReferenceType != null){
                    jsonWriter.name("attr_ReferenceType")
                    jsonWriter.value(detailsReferenceInformation.attr_ReferenceType)
                }
                jsonWriter.name("DetailsReferenceInformation")
                jsonWriter.value(detailsReferenceInformation.DetailsReferenceInformation)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(detailsReferenceInformation.DetailsReferenceInformation)
            }
        }
    }
}

public class GrossWeightAdapter extends TypeAdapter<GrossWeight>{
    @Override
    public GrossWeight read(JsonReader jsonReader) throws IOException {
        GrossWeight grossWeight = new GrossWeight();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'GrossWeight':
                        grossWeight.GrossWeight = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        grossWeight.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                grossWeight = new GrossWeight();
                grossWeight.GrossWeight = jsonReader.nextString();
            }catch(IllegalStateException e2){
                grossWeight = new GrossWeight();
                jsonReader.nextNull();
            }
        }
        return grossWeight
    }

    @Override
    public void write(JsonWriter jsonWriter,GrossWeight grossWeight) throws IOException {
        if(grossWeight?.GrossWeight == null){
            jsonWriter.nullValue()
        }else{
            if(grossWeight.attr_UOM != null ){
                jsonWriter.beginObject()
                if(grossWeight.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(grossWeight.attr_UOM)
                }
                jsonWriter.name("GrossWeight")
                jsonWriter.value(grossWeight.GrossWeight)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(grossWeight.GrossWeight)
            }
        }
    }
}

public class ChargeCategoryAdapter extends TypeAdapter<ChargeCategory>{
    @Override
    public ChargeCategory read(JsonReader jsonReader) throws IOException {
        ChargeCategory chargeCategory = new ChargeCategory();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'ChargeCategory':
                        chargeCategory.ChargeCategory = jsonReader.nextString();
                        break;
                    case 'attr_ChargeType':
                        chargeCategory.attr_ChargeType = jsonReader.nextString();
                        break;
                    case 'attr_PrepaidorCollectIndicator':
                        chargeCategory.attr_PrepaidorCollectIndicator = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                chargeCategory = new ChargeCategory();
                chargeCategory.ChargeCategory = jsonReader.nextString();
            }catch(IllegalStateException e2){
                chargeCategory = new ChargeCategory();
                jsonReader.nextNull();
            }
        }
        return chargeCategory
    }

    @Override
    public void write(JsonWriter jsonWriter,ChargeCategory chargeCategory) throws IOException {
        if(chargeCategory?.ChargeCategory == null){
            jsonWriter.nullValue()
        }else{
            if(chargeCategory.attr_ChargeType != null || chargeCategory.attr_PrepaidorCollectIndicator != null ){
                jsonWriter.beginObject()
                if(chargeCategory.attr_ChargeType != null){
                    jsonWriter.name("attr_ChargeType")
                    jsonWriter.value(chargeCategory.attr_ChargeType)
                }
                if(chargeCategory.attr_PrepaidorCollectIndicator != null){
                    jsonWriter.name("attr_PrepaidorCollectIndicator")
                    jsonWriter.value(chargeCategory.attr_PrepaidorCollectIndicator)
                }
                jsonWriter.name("ChargeCategory")
                jsonWriter.value(chargeCategory.ChargeCategory)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(chargeCategory.ChargeCategory)
            }
        }
    }
}

public class ShipmentDeclaredValueAdapter extends TypeAdapter<ShipmentDeclaredValue>{
    @Override
    public ShipmentDeclaredValue read(JsonReader jsonReader) throws IOException {
        ShipmentDeclaredValue shipmentDeclaredValue = new ShipmentDeclaredValue();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'ShipmentDeclaredValue':
                        shipmentDeclaredValue.ShipmentDeclaredValue = jsonReader.nextString();
                        break;
                    case 'attr_Currency':
                        shipmentDeclaredValue.attr_Currency = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                shipmentDeclaredValue = new ShipmentDeclaredValue();
                shipmentDeclaredValue.ShipmentDeclaredValue = jsonReader.nextString();
            }catch(IllegalStateException e2){
                shipmentDeclaredValue = new ShipmentDeclaredValue();
                jsonReader.nextNull();
            }
        }
        return shipmentDeclaredValue
    }

    @Override
    public void write(JsonWriter jsonWriter,ShipmentDeclaredValue shipmentDeclaredValue) throws IOException {
        if(shipmentDeclaredValue?.ShipmentDeclaredValue == null){
            jsonWriter.nullValue()
        }else{
            if(shipmentDeclaredValue.attr_Currency != null ){
                jsonWriter.beginObject()
                if(shipmentDeclaredValue.attr_Currency != null){
                    jsonWriter.name("attr_Currency")
                    jsonWriter.value(shipmentDeclaredValue.attr_Currency)
                }
                jsonWriter.name("ShipmentDeclaredValue")
                jsonWriter.value(shipmentDeclaredValue.ShipmentDeclaredValue)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(shipmentDeclaredValue.ShipmentDeclaredValue)
            }
        }
    }
}

public class SplitGoodsGrossVolumeAdapter extends TypeAdapter<SplitGoodsGrossVolume>{
    @Override
    public SplitGoodsGrossVolume read(JsonReader jsonReader) throws IOException {
        SplitGoodsGrossVolume splitGoodsGrossVolume = new SplitGoodsGrossVolume();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'SplitGoodsGrossVolume':
                        splitGoodsGrossVolume.SplitGoodsGrossVolume = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        splitGoodsGrossVolume.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                splitGoodsGrossVolume = new SplitGoodsGrossVolume();
                splitGoodsGrossVolume.SplitGoodsGrossVolume = jsonReader.nextString();
            }catch(IllegalStateException e2){
                splitGoodsGrossVolume = new SplitGoodsGrossVolume();
                jsonReader.nextNull();
            }
        }
        return splitGoodsGrossVolume
    }

    @Override
    public void write(JsonWriter jsonWriter,SplitGoodsGrossVolume splitGoodsGrossVolume) throws IOException {
        if(splitGoodsGrossVolume?.SplitGoodsGrossVolume == null){
            jsonWriter.nullValue()
        }else{
            if(splitGoodsGrossVolume.attr_UOM != null ){
                jsonWriter.beginObject()
                if(splitGoodsGrossVolume.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(splitGoodsGrossVolume.attr_UOM)
                }
                jsonWriter.name("SplitGoodsGrossVolume")
                jsonWriter.value(splitGoodsGrossVolume.SplitGoodsGrossVolume)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(splitGoodsGrossVolume.SplitGoodsGrossVolume)
            }
        }
    }
}

public class PackageDetailCommentsAdapter extends TypeAdapter<PackageDetailComments>{
    @Override
    public PackageDetailComments read(JsonReader jsonReader) throws IOException {
        PackageDetailComments packageDetailComments = new PackageDetailComments();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'PackageDetailComments':
                        packageDetailComments.PackageDetailComments = jsonReader.nextString();
                        break;
                    case 'attr_CommentType':
                        packageDetailComments.attr_CommentType = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                packageDetailComments = new PackageDetailComments();
                packageDetailComments.PackageDetailComments = jsonReader.nextString();
            }catch(IllegalStateException e2){
                packageDetailComments = new PackageDetailComments();
                jsonReader.nextNull();
            }
        }
        return packageDetailComments
    }

    @Override
    public void write(JsonWriter jsonWriter,PackageDetailComments packageDetailComments) throws IOException {
        if(packageDetailComments?.PackageDetailComments == null){
            jsonWriter.nullValue()
        }else{
            if(packageDetailComments.attr_CommentType != null ){
                jsonWriter.beginObject()
                if(packageDetailComments.attr_CommentType != null){
                    jsonWriter.name("attr_CommentType")
                    jsonWriter.value(packageDetailComments.attr_CommentType)
                }
                jsonWriter.name("PackageDetailComments")
                jsonWriter.value(packageDetailComments.PackageDetailComments)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(packageDetailComments.PackageDetailComments)
            }
        }
    }
}

public class LocationCodeAdapter extends TypeAdapter<LocationCode>{
    @Override
    public LocationCode read(JsonReader jsonReader) throws IOException {
        LocationCode locationCode = new LocationCode();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'LocationCode':
                        locationCode.LocationCode = jsonReader.nextString();
                        break;
                    case 'attr_Agency':
                        locationCode.attr_Agency = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                locationCode = new LocationCode();
                locationCode.LocationCode = jsonReader.nextString();
            }catch(IllegalStateException e2){
                locationCode = new LocationCode();
                jsonReader.nextNull();
            }
        }
        return locationCode
    }

    @Override
    public void write(JsonWriter jsonWriter,LocationCode locationCode) throws IOException {
        if(locationCode?.LocationCode == null){
            jsonWriter.nullValue()
        }else{
            if(locationCode.attr_Agency != null ){
                jsonWriter.beginObject()
                if(locationCode.attr_Agency != null){
                    jsonWriter.name("attr_Agency")
                    jsonWriter.value(locationCode.attr_Agency)
                }
                jsonWriter.name("LocationCode")
                jsonWriter.value(locationCode.LocationCode)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(locationCode.LocationCode)
            }
        }
    }
}

public class EquipmentTemperatureAdapter extends TypeAdapter<EquipmentTemperature>{
    @Override
    public EquipmentTemperature read(JsonReader jsonReader) throws IOException {
        EquipmentTemperature equipmentTemperature = new EquipmentTemperature();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'EquipmentTemperature':
                        equipmentTemperature.EquipmentTemperature = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        equipmentTemperature.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                equipmentTemperature = new EquipmentTemperature();
                equipmentTemperature.EquipmentTemperature = jsonReader.nextString();
            }catch(IllegalStateException e2){
                equipmentTemperature = new EquipmentTemperature();
                jsonReader.nextNull();
            }
        }
        return equipmentTemperature
    }

    @Override
    public void write(JsonWriter jsonWriter,EquipmentTemperature equipmentTemperature) throws IOException {
        if(equipmentTemperature?.EquipmentTemperature == null){
            jsonWriter.nullValue()
        }else{
            if(equipmentTemperature.attr_UOM != null ){
                jsonWriter.beginObject()
                if(equipmentTemperature.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(equipmentTemperature.attr_UOM)
                }
                jsonWriter.name("EquipmentTemperature")
                jsonWriter.value(equipmentTemperature.EquipmentTemperature)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(equipmentTemperature.EquipmentTemperature)
            }
        }
    }
}

public class SplitGoodsGrossWeightAdapter extends TypeAdapter<SplitGoodsGrossWeight>{
    @Override
    public SplitGoodsGrossWeight read(JsonReader jsonReader) throws IOException {
        SplitGoodsGrossWeight splitGoodsGrossWeight = new SplitGoodsGrossWeight();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'SplitGoodsGrossWeight':
                        splitGoodsGrossWeight.SplitGoodsGrossWeight = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        splitGoodsGrossWeight.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                splitGoodsGrossWeight = new SplitGoodsGrossWeight();
                splitGoodsGrossWeight.SplitGoodsGrossWeight = jsonReader.nextString();
            }catch(IllegalStateException e2){
                splitGoodsGrossWeight = new SplitGoodsGrossWeight();
                jsonReader.nextNull();
            }
        }
        return splitGoodsGrossWeight
    }

    @Override
    public void write(JsonWriter jsonWriter,SplitGoodsGrossWeight splitGoodsGrossWeight) throws IOException {
        if(splitGoodsGrossWeight?.SplitGoodsGrossWeight == null){
            jsonWriter.nullValue()
        }else{
            if(splitGoodsGrossWeight.attr_UOM != null ){
                jsonWriter.beginObject()
                if(splitGoodsGrossWeight.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(splitGoodsGrossWeight.attr_UOM)
                }
                jsonWriter.name("SplitGoodsGrossWeight")
                jsonWriter.value(splitGoodsGrossWeight.SplitGoodsGrossWeight)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(splitGoodsGrossWeight.SplitGoodsGrossWeight)
            }
        }
    }
}

public class TransportIdentificationAdapter extends TypeAdapter<TransportIdentification>{
    @Override
    public TransportIdentification read(JsonReader jsonReader) throws IOException {
        TransportIdentification transportIdentification = new TransportIdentification();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_TransportIdentificationType':
                        transportIdentification.attr_TransportIdentificationType = jsonReader.nextString();
                        break;
                    case 'TransportIdentification':
                        transportIdentification.TransportIdentification = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                transportIdentification = new TransportIdentification();
                transportIdentification.TransportIdentification = jsonReader.nextString();
            }catch(IllegalStateException e2){
                transportIdentification = new TransportIdentification();
                jsonReader.nextNull();
            }
        }
        return transportIdentification
    }

    @Override
    public void write(JsonWriter jsonWriter,TransportIdentification transportIdentification) throws IOException {
        if(transportIdentification?.TransportIdentification == null){
            jsonWriter.nullValue()
        }else{
            if(transportIdentification.attr_TransportIdentificationType != null ){
                jsonWriter.beginObject()
                if(transportIdentification.attr_TransportIdentificationType != null){
                    jsonWriter.name("attr_TransportIdentificationType")
                    jsonWriter.value(transportIdentification.attr_TransportIdentificationType)
                }
                jsonWriter.name("TransportIdentification")
                jsonWriter.value(transportIdentification.TransportIdentification)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(transportIdentification.TransportIdentification)
            }
        }
    }
}

public class TransportMeansAdapter extends TypeAdapter<TransportMeans>{
    @Override
    public TransportMeans read(JsonReader jsonReader) throws IOException {
        TransportMeans transportMeans = new TransportMeans();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'TransportMeans':
                        transportMeans.TransportMeans = jsonReader.nextString();
                        break;
                    case 'attr_TransportMeansType':
                        transportMeans.attr_TransportMeansType = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                transportMeans = new TransportMeans();
                transportMeans.TransportMeans = jsonReader.nextString();
            }catch(IllegalStateException e2){
                transportMeans = new TransportMeans();
                jsonReader.nextNull();
            }
        }
        return transportMeans
    }

    @Override
    public void write(JsonWriter jsonWriter,TransportMeans transportMeans) throws IOException {
        if(transportMeans?.TransportMeans == null){
            jsonWriter.nullValue()
        }else{
            if(transportMeans.attr_TransportMeansType != null ){
                jsonWriter.beginObject()
                if(transportMeans.attr_TransportMeansType != null){
                    jsonWriter.name("attr_TransportMeansType")
                    jsonWriter.value(transportMeans.attr_TransportMeansType)
                }
                jsonWriter.name("TransportMeans")
                jsonWriter.value(transportMeans.TransportMeans)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(transportMeans.TransportMeans)
            }
        }
    }
}

public class HazardousGoodsCommentsAdapter extends TypeAdapter<HazardousGoodsComments>{
    @Override
    public HazardousGoodsComments read(JsonReader jsonReader) throws IOException {
        HazardousGoodsComments hazardousGoodsComments = new HazardousGoodsComments();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_CommentType':
                        hazardousGoodsComments.attr_CommentType = jsonReader.nextString();
                        break;
                    case 'HazardousGoodsComments':
                        hazardousGoodsComments.HazardousGoodsComments = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                hazardousGoodsComments = new HazardousGoodsComments();
                hazardousGoodsComments.HazardousGoodsComments = jsonReader.nextString();
            }catch(IllegalStateException e2){
                hazardousGoodsComments = new HazardousGoodsComments();
                jsonReader.nextNull();
            }
        }
        return hazardousGoodsComments
    }

    @Override
    public void write(JsonWriter jsonWriter,HazardousGoodsComments hazardousGoodsComments) throws IOException {
        if(hazardousGoodsComments?.HazardousGoodsComments == null){
            jsonWriter.nullValue()
        }else{
            if(hazardousGoodsComments.attr_CommentType != null ){
                jsonWriter.beginObject()
                if(hazardousGoodsComments.attr_CommentType != null){
                    jsonWriter.name("attr_CommentType")
                    jsonWriter.value(hazardousGoodsComments.attr_CommentType)
                }
                jsonWriter.name("HazardousGoodsComments")
                jsonWriter.value(hazardousGoodsComments.HazardousGoodsComments)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(hazardousGoodsComments.HazardousGoodsComments)
            }
        }
    }
}

public class GrossVolumeAdapter extends TypeAdapter<GrossVolume>{
    @Override
    public GrossVolume read(JsonReader jsonReader) throws IOException {
        GrossVolume grossVolume = new GrossVolume();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'attr_UOM':
                        grossVolume.attr_UOM = jsonReader.nextString();
                        break;
                    case 'GrossVolume':
                        grossVolume.GrossVolume = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                grossVolume = new GrossVolume();
                grossVolume.GrossVolume = jsonReader.nextString();
            }catch(IllegalStateException e2){
                grossVolume = new GrossVolume();
                jsonReader.nextNull();
            }
        }
        return grossVolume
    }

    @Override
    public void write(JsonWriter jsonWriter,GrossVolume grossVolume) throws IOException {
        if(grossVolume?.GrossVolume == null){
            jsonWriter.nullValue()
        }else{
            if(grossVolume.attr_UOM != null ){
                jsonWriter.beginObject()
                if(grossVolume.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(grossVolume.attr_UOM)
                }
                jsonWriter.name("GrossVolume")
                jsonWriter.value(grossVolume.GrossVolume)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(grossVolume.GrossVolume)
            }
        }
    }
}

public class EquipmentTypeCodeAdapter extends TypeAdapter<EquipmentTypeCode>{
    @Override
    public EquipmentTypeCode read(JsonReader jsonReader) throws IOException {
        EquipmentTypeCode equipmentTypeCode = new EquipmentTypeCode();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'EquipmentTypeCode':
                        equipmentTypeCode.EquipmentTypeCode = jsonReader.nextString();
                        break;
                    case 'attr_NonActiveReefer':
                        equipmentTypeCode.attr_NonActiveReefer = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                equipmentTypeCode = new EquipmentTypeCode();
                equipmentTypeCode.EquipmentTypeCode = jsonReader.nextString();
            }catch(IllegalStateException e2){
                equipmentTypeCode = new EquipmentTypeCode();
                jsonReader.nextNull();
            }
        }
        return equipmentTypeCode
    }

    @Override
    public void write(JsonWriter jsonWriter,EquipmentTypeCode equipmentTypeCode) throws IOException {
        if(equipmentTypeCode?.EquipmentTypeCode == null){
            jsonWriter.nullValue()
        }else{
            if(equipmentTypeCode.attr_NonActiveReefer != null ){
                jsonWriter.beginObject()
                if(equipmentTypeCode.attr_NonActiveReefer != null){
                    jsonWriter.name("attr_NonActiveReefer")
                    jsonWriter.value(equipmentTypeCode.attr_NonActiveReefer)
                }
                jsonWriter.name("EquipmentTypeCode")
                jsonWriter.value(equipmentTypeCode.EquipmentTypeCode)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(equipmentTypeCode.EquipmentTypeCode)
            }
        }
    }
}

public class PackageDetailGrossVolumeAdapter extends TypeAdapter<PackageDetailGrossVolume>{
    @Override
    public PackageDetailGrossVolume read(JsonReader jsonReader) throws IOException {
        PackageDetailGrossVolume packageDetailGrossVolume = new PackageDetailGrossVolume();
        try{
            jsonReader.beginObject();
            jsonReader.setLenient(true)
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case 'PackageDetailGrossVolume':
                        packageDetailGrossVolume.PackageDetailGrossVolume = jsonReader.nextString();
                        break;
                    case 'attr_UOM':
                        packageDetailGrossVolume.attr_UOM = jsonReader.nextString();
                        break;
                }
            }
            jsonReader.endObject()
        }catch(IllegalStateException e){
            try{
                packageDetailGrossVolume = new PackageDetailGrossVolume();
                packageDetailGrossVolume.PackageDetailGrossVolume = jsonReader.nextString();
            }catch(IllegalStateException e2){
                packageDetailGrossVolume = new PackageDetailGrossVolume();
                jsonReader.nextNull();
            }
        }
        return packageDetailGrossVolume
    }

    @Override
    public void write(JsonWriter jsonWriter,PackageDetailGrossVolume packageDetailGrossVolume) throws IOException {
        if(packageDetailGrossVolume?.PackageDetailGrossVolume == null){
            jsonWriter.nullValue()
        }else{
            if(packageDetailGrossVolume.attr_UOM != null ){
                jsonWriter.beginObject()
                if(packageDetailGrossVolume.attr_UOM != null){
                    jsonWriter.name("attr_UOM")
                    jsonWriter.value(packageDetailGrossVolume.attr_UOM)
                }
                jsonWriter.name("PackageDetailGrossVolume")
                jsonWriter.value(packageDetailGrossVolume.PackageDetailGrossVolume)
                jsonWriter.endObject()
            }else{
                jsonWriter.value(packageDetailGrossVolume.PackageDetailGrossVolume)
            }
        }
    }
}