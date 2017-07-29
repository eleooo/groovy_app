package cs.b2b.core.mapping.bean.ss_pt2pt

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


class Message implements Serializable {
	
	public static final Set<String> MultiElementList = ['Event', 'AddressLines', 'oceanleg', 'Route', 'SearchCriteria']
	
 	TemplateDescription TemplateDescription;
	Identification Identification;
	RouteGroup RouteGroup;
}

class TemplateDescription implements Serializable {
	String TemplateName;
	String TemplateType;
	String Release;
	String CreationDateTime;
}

class Identification implements Serializable {
	String RefNo;
	String ScheduleType;
	String VersionNumber;
	String CreationDateTime;
	String TransmissionDateTime;
	Sender Sender;
	Receiver Receiver;
}

class Sender implements Serializable {
	String SenderName;
	String SenderCode;
}

class Receiver implements Serializable {
	String ReceiverName;
	String ReceiverCode;
}

class RouteGroup implements Serializable {
	List<SearchCriteria> SearchCriteria = new ArrayList<SearchCriteria>();
	List<Route> Route = new ArrayList<Route>();
	String TotalCounts;
	String FromCount;
	String ToCount;
	String Remarks;
}

class SearchCriteria implements Serializable {
	String OriginUnlocode;
	String DestinationUnlocode;
	String DateRangeType;
	String FromDate;
	String ToDate;
	String ExtractionDate;
}

class Route implements Serializable {
	Origin Origin;
	Destination Destination;
	Carrier Carrier;
	OceanComponent OceanComponent;
	String EstimatedTransitTimeInDays;
	String RouteStatus;
}

class Origin implements Serializable {
	City City;
	List<Event> Event = new ArrayList<Event>();
	String HaulageIndicator;
}

class City implements Serializable {
	String City;
	String County;
	String State;
	String Country;
	String UNLOCODE;
}

class Event implements Serializable {
	String Name;
	String LocalDateTime;
}

class Destination implements Serializable {
	City City;
	List<Event> Event = new ArrayList<Event>();
	String HaulageIndicator;
}

class Carrier implements Serializable {
	String SCAC;
	String Name;
}

class OceanComponent implements Serializable {
	List<Oceanleg> oceanleg = new ArrayList<Oceanleg>();
}

class Oceanleg implements Serializable {
	String LegSequence;
	POL POL;
	POD POD;
	Service service;
	Vessel Vessel;
	Voyage Voyage;
	String Direction;
}

class POL implements Serializable {
	String PortName;
	String UNLOCODE;
	String FacilityCode;
	String PhoneNumber;
	String FaxNumber;
	Address Address;
	List<Event> Event = new ArrayList<Event>();}

class Address implements Serializable {
	List<String> AddressLines = new ArrayList<String>();
}

class POD implements Serializable {
	String PortName;
	String UNLOCODE;
	String FacilityCode;
	String PhoneNumber;
	String FaxNumber;
	Address Address;
	List<Event> Event = new ArrayList<Event>();
}

class Service implements Serializable {
	String trade;
	String tradeLane;
	String ServiceCode;
	String ServiceName;
}

class Vessel implements Serializable {
	String LloydsNumber;
	String CallSign;
	String Code;
	String Name;
	String Owner;
	String Operator;
	String PortOfRegistry;
	String FlagCountryCode;
	String FlagCountry;
	String ClassSociety;
	String YearBuilt;
	String Age;
	String GrossWeight;
	String NetWeight;
}

class Voyage implements Serializable {
	String InternalVoyageNumber;
	String ExternalVoyageNumber;
}
