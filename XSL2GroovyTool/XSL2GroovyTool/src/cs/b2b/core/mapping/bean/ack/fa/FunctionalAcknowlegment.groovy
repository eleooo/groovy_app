package cs.b2b.core.mapping.bean.ack.fa

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List
import java.util.Set;;

class FunctionalAcknowlegment implements Serializable {
	public static final Set<String> MultiElementList = ['Field', 'Errors', 'Transactions']
 	Information Information;
	List<Transactions> Transactions = new ArrayList<Transactions>();
}

class Information implements Serializable {
	String SenderId;
	String SenderQualifier;
	String ReceiverId;
	String ReceiverQualifier;
	Origin Origin;
	String AcitonCode;
	String ErrorCode;
	String TotalNumberOfTxn;
	String NumberOfReceivedTxn;
	String NumberOfAcceptedTxn;
	String Remark;
}

class Origin implements Serializable {
	String InterchangeControlNumber;
	String GroupControlNumber;
	String FunctionalIdentifierCode;
}

class Transactions implements Serializable {
	Origin_Transactions Origin;
	String ActionCode;
	String ErrorCode;
	List<Errors> Errors = new ArrayList<Errors>();
	String Remark;
}

class Origin_Transactions implements Serializable {
	String Sequence;
	String ControlNumber;
	String MessageType;
	String Version;
	String Release;
	String Agency;
	String AssociationAssignedCode;
}

class Errors implements Serializable {
	Segment Segment;
}

class Segment implements Serializable {
	String ID;
	String Position;
	String ErrorCode;
	List<Field> Field = new ArrayList<Field>();
	String Remark;
}

class Field implements Serializable {
	String Position;
	String CompositePosition;
	String ErrorCode;
	String OrigData;
	String Remark;
}