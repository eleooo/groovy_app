package cs

/**
 * Created by LINJE2 on 9/1/2017.
 */
class Message implements Serializable {
    public static final Set<String> MultiElementList= ["CommunicationValue", "AddressLine", "PartnerInformation", "Location", "Marks", "PackageDetailComments", "GoodsDetails", "EquipmentDetails"]
    Header Header;
    MessageBody MessageBody;
}

class Header implements Serializable {
    MessageType MessageType;
    String DocumentIdentifier;
    DateTime DateTime;
    Parties Parties;
}

class MessageType implements Serializable {
    String attr_MessageVersion;
    String MessageType;

    @Override
    public String toString() {
        return this.MessageType;
    }
}

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
    ChargeDetails ChargeDetails;
}

class PartnerIdentifier implements Serializable {
    String attr_Agency;
    String PartnerIdentifier;

    @Override
    public String toString() {
        return this.PartnerIdentifier;
    }
}

class ContactInformation implements Serializable {
    ContactName ContactName;
    List<CommunicationValue> CommunicationValue = new ArrayList<CommunicationValue>();
}

class ContactName implements Serializable {
    String attr_ContactType;
    String ContactName;

    @Override
    public String toString() {
        return this.ContactName;
    }
}

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

class ChargeCategory implements Serializable {
    String attr_ChargeType;
    String attr_PrepaidorCollectIndicator;
    String ChargeCategory;

    @Override
    public String toString() {
        return this.ChargeCategory;
    }
}

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

class LocationCode implements Serializable {
    String attr_Agency;
    String LocationCode;

    @Override
    public String toString() {
        return this.LocationCode;
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
    ExportLicenseDetails ExportLicenseDetails;
    BlLocations BlLocations;
    ReferenceInformation ReferenceInformation;
    Instructions Instructions;
    ControlTotal ControlTotal;
    HaulageDetails HaulageDetails;
    TransportationDetails TransportationDetails;
    Parties Parties;
}

class ShipmentDeclaredValue implements Serializable {
    String attr_Currency;
    String ShipmentDeclaredValue;

    @Override
    public String toString() {
        return this.ShipmentDeclaredValue;
    }
}

class ReferenceInformation implements Serializable {
    String attr_ReferenceType;
    String ReferenceInformation;

    @Override
    public String toString() {
        return this.ReferenceInformation;
    }
}

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

class GrossWeight implements Serializable {
    String attr_UOM;
    String GrossWeight;

    @Override
    public String toString() {
        return this.GrossWeight;
    }
}

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
    ConveyanceInformation ConveyanceInformation;
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

class TransportIdentification implements Serializable {
    String attr_TransportIdentificationType;
    String TransportIdentification;

    @Override
    public String toString() {
        return this.TransportIdentification;
    }
}

class TransportMeans implements Serializable {
    String attr_TransportMeansType;
    String TransportMeans;

    @Override
    public String toString() {
        return this.TransportMeans;
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
    EquipmentGrossVolume EquipmentGrossVolume;
    EquipmentTemperature EquipmentTemperature;
    String EquipmentNonActive;
    EquipmentAirFlow EquipmentAirFlow;
    EquipmentSeal EquipmentSeal;
    EquipmentComments EquipmentComments;
    EquipmentReferenceInformation EquipmentReferenceInformation;
    EquipmentHazardousGoods EquipmentHazardousGoods;
}

class EquipmentIdentifier implements Serializable {
    String attr_EquipmentSupplier;
    String EquipmentIdentifier;

    @Override
    public String toString() {
        return this.EquipmentIdentifier;
    }
}

class EquipmentGrossWeight implements Serializable {
    String attr_UOM;
    String EquipmentGrossWeight;

    @Override
    public String toString() {
        return this.EquipmentGrossWeight;
    }
}

class EquipmentGrossVolume implements Serializable {
    String attr_UOM;
    String EquipmentGrossVolume;

    @Override
    public String toString() {
        return this.EquipmentGrossVolume;
    }
}

class EquipmentTemperature implements Serializable {
    String attr_UOM;
    String EquipmentTemperature;

    @Override
    public String toString() {
        return this.EquipmentTemperature;
    }
}

class EquipmentAirFlow implements Serializable {
    String attr_UOM;
    String EquipmentAirFlow;

    @Override
    public String toString() {
        return this.EquipmentAirFlow;
    }
}

class EquipmentSeal implements Serializable {
    String attr_SealingParty;
    String EquipmentSeal;

    @Override
    public String toString() {
        return this.EquipmentSeal;
    }
}

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

    @Override
    public String toString() {
        return this.EquipmentReferenceInformation;
    }
}

class EquipmentType implements Serializable {
    EquipmentTypeCode EquipmentTypeCode;
    String EquipmentDescription;
}

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
    EmergencyResponseContact EmergencyResponseContact;
}

class FlashpointTemperature implements Serializable {
    String attr_UOM;
    String FlashpointTemperature;

    @Override
    public String toString() {
        return this.FlashpointTemperature;
    }
}

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

class GoodsDetails implements Serializable {
    String LineNumber;
    PackageDetail PackageDetail;
    List<PackageDetailComments> PackageDetailComments = new ArrayList<PackageDetailComments>();
    ProductId ProductId;
    PackageDetailGrossVolume PackageDetailGrossVolume;
    PackageDetailGrossWeight PackageDetailGrossWeight;
    DetailsReferenceInformation DetailsReferenceInformation;
    ExportLicenseDetails ExportLicenseDetails;
    PackageMarks PackageMarks;
    SplitGoodsDetails SplitGoodsDetails;
    HazardousGoods HazardousGoods;
}

class PackageDetailComments implements Serializable {
    String attr_CommentType;
    String PackageDetailComments;

    @Override
    public String toString() {
        return this.PackageDetailComments;
    }
}

class ProductId implements Serializable {
    String attr_ItemTypeIdCode;
    String ProductId;

    @Override
    public String toString() {
        return this.ProductId;
    }
}

class PackageDetailGrossVolume implements Serializable {
    String attr_UOM;
    String PackageDetailGrossVolume;

    @Override
    public String toString() {
        return this.PackageDetailGrossVolume;
    }
}

class PackageDetailGrossWeight implements Serializable {
    String attr_UOM;
    String PackageDetailGrossWeight;

    @Override
    public String toString() {
        return this.PackageDetailGrossWeight;
    }
}

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

class SplitGoodsGrossVolume implements Serializable {
    String attr_UOM;
    String SplitGoodsGrossVolume;

    @Override
    public String toString() {
        return this.SplitGoodsGrossVolume;
    }
}

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
    EmergencyResponseContact EmergencyResponseContact;
}


