package cs.b2b.core.mapping.bean.oa

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set;;

class Message implements Serializable {
	
	public static final Set<String> MultiElementList = ["Event", "Cargo", "Remark", "Stop"];
	
 	TemplateDescription templateDescription;
	Identification identification;
	Voyage voyage;
}

class TemplateDescription implements Serializable {
	TemplateDescriptionType TemplateDescription;
}

class TemplateDescriptionType implements Serializable {
	String TemplateName;
	String TemplateType;
	String Release;
	String DateTime;
}

class Identification implements Serializable {
	IdentificationType Identification;
}

class IdentificationType implements Serializable {
	String RefNo;
	String VersionNumber;
	String ScheduleType;
	String FunctionReference;
	String CreationDate;
	Sender sender;
	Receiver receiver;
}

class Sender implements Serializable {
	SenderType Sender;
}

class SenderType implements Serializable {
	String SenderName;
	String Source;
}

class Receiver implements Serializable {
	ReceiverType Receiver;
}

class ReceiverType implements Serializable {
	String ReceiverName;
	String ReceiverCode;
}

class Voyage implements Serializable {
	VoyageType Voyage;
}

class VoyageType implements Serializable {
	String Type;
	String VoyageNumber;
	String Direction;
	String PreviousService;
	String NextService;
	String CurrentVoyageKey;
	String OwnerVoyageKey;
	String CurrentVoyageWeekNumber;
	String PreviousVoyageReference;
	String CurrentVoyageReference;
	String NextVoyageReference;
	String PreviousExternalReference;
	String CurrentExternalReference;
	String NextExternalReference;
	String OldInternalVoyageReference;
	Carrier carrier;
	Vessel vessel;
	Service service;
	Stops stops;
	Remarks remarks;
	Proforma proforma;
	String LastUpdatedBy;
	String LastUpdated;
	Vendor Vendor;
	String ScheduleType;
}

class Carrier implements Serializable {
	CarrierType Carrier;
}

class CarrierType implements Serializable {
	String ReferenceNumber;
	String Code;
	String Name;
}

class Vessel implements Serializable {
	VesselType Vessel;
}

class VesselType implements Serializable {
	String LloydsNumber;
	String IMONumber;
	String Code;
	String Name;
	String CallLetters;
	String VesselId;
}

class Service implements Serializable {
	ServiceType Service;
}

class ServiceType implements Serializable {
	String Code;
	String Name;
	String ServiceLoopId;
}

class Stops implements Serializable {
	List<Stop> Stop = new ArrayList<Stop>();
}

class Stop implements Serializable {
	StopDescription stopDescription;
	Terminal terminal;
	Events events;
	String AnchorDropDate;
	String AnchorRaiseDate;
	String VesselDischargeDate;
	String IsScheduleTentative;
	String IsPhaseInoutStop;
	Cargos cargos;
	Remarks remarks;
	String ArrivalExtVoyRef;
	String DepExtVoyRef;
	String StopKey;
}

class StopDescription implements Serializable {
	StopDescriptionType StopDescription;
}

class StopDescriptionType implements Serializable {
	String Action;
	String TargetVoyageReference;
	String Type;
	String AllowLoading;
	String AllowDischarge;
	String DryDock;
	String ReasonDescription;
}

class Terminal implements Serializable {
	TerminalType Terminal;
}

class TerminalType implements Serializable {
	String PortName;
	String PortCode;
	String PortUnlocode;
	String TerminalName;
	String TerminalCode;
	String CallNumber;
	String OldTerminalName;
	String OldTerminalCode;
	String OldCallNumber;
}

class Events implements Serializable {
	List<Event> Event = new ArrayList<Event>();
}

class Event implements Serializable {
	String Name;
	String DateTime;
}

class Cargos implements Serializable {
	List<Cargo> Cargo = new ArrayList<Cargo>();
}

class Cargo implements Serializable {
	String Name;
	String DateTime;
}

class Remarks implements Serializable {
	List<Remark> Remark = new ArrayList<Remark>();
}

class Remark implements Serializable {
	String Text;
	String Creator;
	String DateTime;
	String Standard;
}

class Proforma implements Serializable {
	String ProformaId;
	String ProformaName;
}

class Vendor implements Serializable {
	String Carrier;
	String VendorCode;
	String VendorName;
	String TerritoryCode;
}

