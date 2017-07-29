package cs.b2b.core.mapping.bean;

import java.io.IOException;
import java.lang.reflect.Type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter;;

/**
 * @author RENGA
 * @cs2xmlVersion 67.0
 */
class MsgDT implements Serializable {
	String GMT;
	LocDT LocDT;
}

@JsonAdapter(LocDTAdapter.class)
class LocDT implements Serializable {
	String attr_CSTimeZone;
 	String attr_TimeZone;
 	String LocDT;

	@Override
	public String toString() {
		return this.LocDT;
	}
}


class NVOProperty implements Serializable {
	String Name;
	String Value;
}

class CityDetails implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	LocationCode LocationCode;
}

class LocationCode implements Serializable {
	String MutuallyDefinedCode;
	String UNLocationCode;
	String SchedKDType;
	String SchedKDCode;
}

class Facility implements Serializable {
	String FacilityCode;
	String FacilityName;
}

class CSStandardCity implements Serializable {
	String CSParentCityID;
	String CSStateCode;
	String CSCountryCode;
	String CSContinentCode;
}

class Port implements Serializable {
	String PortName;
	String PortCode;
	String City;
	String County;
	String State;
	LocationCode LocationCode;
	String Country;
	String CSPortID;
	String CSCountryCode;
}

class Contact implements Serializable {
	String FirstName;
	String LastName;
	ContactPhone ContactPhone;
	ContactFax ContactFax;
	String ContactEmailAddress;
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

class Address implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	LocationCode LocationCode;
	String PostalCode;
	AddressLines AddressLines;
}

class AddressLines implements Serializable {
	List<String> AddressLine = new ArrayList<String>();
}

class Reference implements Serializable {
	String Type;
	String Number;
}

class Packaging implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class GrossWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class NetWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class Volume implements Serializable {
	String Volume;
	String VolumeUnit;
}

class MarksAndNumbers implements Serializable {
	String SeqNumber;
	String MarksAndNumbersLine;
	String CGO_MRK_NUM_ID;
}

class EventDT implements Serializable {
	String GMT;
	LocDT LocDT;
}

class Temperature implements Serializable {
	String Temperature;
	String TemperatureUnit;
}

class Ventilation implements Serializable {
	String Ventilation;
	String VentilationUnit;
}

class EmergencyContact implements Serializable {
	String FirstName;
	String LastName;
	ContactPhone ContactPhone;
	ContactFax ContactFax;
	String ContactEmailAddress;
	String Type;
	String EM_CTACT_UUID;
}

class PackageGroup implements Serializable {
	String Code;
	InnerPackageDescription InnerPackageDescription;
	OuterPackageDescription OuterPackageDescription;
}

class InnerPackageDescription implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class OuterPackageDescription implements Serializable {
	String PackageType;
	String PackageQty;
	String PackageDesc;
	String PackageMaterial;
}

class NetExplosiveWeight implements Serializable {
	String Weight;
	String WeightUnit;
}

class FlashPoint implements Serializable {
	String Temperature;
	String TemperatureUnit;
}

class ElevatedTemperature implements Serializable {
	String Temperature;
	String TemperatureUnit;
}

class Height implements Serializable {
	String Length;
	String LengthUnit;
}

class Length implements Serializable {
	String Length;
	String LengthUnit;
}

class Width implements Serializable {
	String Length;
	String LengthUnit;
}

class POL implements Serializable {
	Port Port;
	Facility Facility;
}

class POD implements Serializable {
	Port Port;
	Facility Facility;
}

class SVVD implements Serializable {
	Loading Loading;
	Discharge Discharge;
	String VesselVoyageType;
	String CarriageType;
	String TransportMode;
}

class Loading implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String CallNumber;
	String VesselNationality;
}

class Discharge implements Serializable {
	String Service;
	String Vessel;
	String VesselName;
	String Voyage;
	String Direction;
	String LloydsNumber;
	String CallSign;
	String CallNumber;
	String VesselNationality;
}

class DepartureDT implements Serializable {
	String attr_Indicator;
	 String GMT;
	LocDT LocDT;
}

class ArrivalDT implements Serializable {
	String attr_Indicator;
	 String GMT;
	LocDT LocDT;
}

class PickupDetails implements Serializable {
	City City;
	Facility Facility;
}

class City implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	LocationCode LocationCode;
}

class ReturnDetails implements Serializable {
	City City;
	Facility Facility;
}

class Seal implements Serializable {
	String SealType;
	String SealNumber;
}

class Haulage implements Serializable {
	String OutBound;
	String InBound;
}

class TrafficMode implements Serializable {
	String OutBound;
	String InBound;
}

class AppointmentDT implements Serializable {
	String GMT;
	LocDT LocDT;
}

class ActualArrivalDT implements Serializable {
	String GMT;
	LocDT LocDT;
}

class ActualDepartureDT implements Serializable {
	String GMT;
	LocDT LocDT;
}

class Container implements Serializable {
	String ContainerNumber;
	String ContainerCheckDigit;
	String CSContainerSizeType;
	String CarrCntrSizeType;
}

class CutOffTime implements Serializable {
	String GMT;
	LocDT LocDT;
}

class MasterBLFiling implements Serializable {
	String RegionCode;
	String FilingType;
	String IsHouseMasterShipment;
	String IsNeedShipToParty;
	String IsFilingShipmentPartyEORI;
	String TaxIDType;
	String CargoReceiverTaxID;
	String BtaFdaFlag;
	String IsHouseBL;
	String HarmonizedCode;
	CutOffTime CutOffTime;
}

class MasterBLParty implements Serializable {
	String PartyType;
	String PartyName;
	String CSCompanyID;
	String CarrierCustomerCode;
	String isNeedReplyPartyEmail;
	Contact Contact;
	Address Address;
	List<Reference> Reference = new ArrayList<cs.b2b.core.mapping.bean.Reference>();
}

class HouseBLFiling implements Serializable {
	List<Party> Party = new ArrayList<cs.b2b.core.mapping.bean.Party>();
	String HBLNumber;
	String InternalName;
	List<FilerInfo> FilerInfo = new ArrayList<cs.b2b.core.mapping.bean.FilerInfo>();
	String HBLSequenceId;
}

class Party implements Serializable {
	String PartyType;
	String PartyName;
	String CSCompanyID;
	String CarrierCustomerCode;
	String isNeedReplyPartyEmail;
	Contact Contact;
	Address Address;
	List<Reference> Reference = new ArrayList<cs.b2b.core.mapping.bean.Reference>();
}

class FilerInfo implements Serializable {
	String IsAuto;
	String CountryCode;
	List<Code> Code = new ArrayList<cs.b2b.core.mapping.bean.Code>();
}

class Code implements Serializable {
	String Type;
	String Value;
}

class Fax implements Serializable {
	String CountryCode;
	String AreaCode;
	String Number;
}

class RoutingGuide implements Serializable {
	String primaryCarrierCompanyID;
	String primaryGuideName;
	List<String> secondGuideName = new ArrayList<String>();
}


public class LocDTAdapter extends TypeAdapter<cs.b2b.core.mapping.bean.LocDT> {
	
	@Override
	public LocDT read(JsonReader jsonReader) throws IOException {
		
		LocDT locDT = new LocDT();

		try{
			jsonReader.beginObject();
			jsonReader.setLenient(true)
			while (jsonReader.hasNext()) {
				switch (jsonReader.nextName()) {
					case "attr_CSTimeZone":
						locDT.attr_CSTimeZone = jsonReader.nextString();
						break;
					case "attr_TimeZone":
						locDT.attr_TimeZone = jsonReader.nextString();
						break;
					case "LocDT":
						locDT.LocDT = jsonReader.nextString();
						break;
				}
			}
			jsonReader.endObject()
		}catch(IllegalStateException e){
			try{
				locDT = new LocDT();
				locDT.setLocDT(jsonReader.nextString());
			}catch(IllegalStateException e2){
				locDT = new LocDT();
				jsonReader.nextNull();
			}
		}
		
		return locDT;
	}
	
	@Override
	public void write(JsonWriter jsonWriter, LocDT locDT) throws IOException {
		if(locDT?.LocDT == null){
			jsonWriter.nullValue()
		}else{
			if(locDT.attr_CSTimeZone != null || locDT.attr_TimeZone != null){
				jsonWriter.beginObject()
				if(locDT.attr_CSTimeZone != null){
					jsonWriter.name("attr_CSTimeZone")
					jsonWriter.value(locDT.attr_CSTimeZone)
				}
				if(locDT.attr_TimeZone != null){
					jsonWriter.name("attr_TimeZone")
					jsonWriter.value(locDT.attr_TimeZone)
				}
				jsonWriter.name("LocDT")
				jsonWriter.value(locDT.LocDT)
				jsonWriter.endObject()
			}else{
				jsonWriter.value(locDT.LocDT)
			}
		}
	}
}
