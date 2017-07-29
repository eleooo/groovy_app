package cs.b2b.core.mapping.api.autori;

public class Env {

	public static String QA1_jmsServerUrl = "csb2bediqa1ems01:9222";
	public static String QA1_jmsUserName = "b2bapp";
	public static String QA1_jmsPassword = "b2bapp";

	public static String QA2_jmsServerUrl = "hksudv38:7264";
	public static String QA2_jmsUserName = "b2bapp";
	public static String QA2_jmsPassword = "b2bapp";

	public static String SendToACK = "CS2.B2B.JOB_REQ.ACKNOWLEDGEMENT.OUT.QUE";
	public static String SendToBL = "CS2.B2B.JOB_REQ.BILLOFLADING.OUT.QUE";
	public static String SendToBC = "CS2.B2B.JOB_REQ.BOOKINGCONFIRMATION.OUT.QUE";
	public static String SendToBR = "CS2.B2B.JOB_REQ.BOOKINGREQUEST.OUT.QUE";
	public static String SendToCT = "CS2.B2B.JOB_REQ.CONTAINERMOVEMENT.OUT.QUE";
	public static String SendToSS = "CS2.B2B.JOB_REQ.SAILINGSCHEDULE.OUT.QUE";
	public static String SendToSI = "CS2.B2B.JOB_REQ.SHIPPINGINSTRUCTION.OUT.QUE";

	public static String ReplyToQueue = "B2B.CS2.INTEGRATION.MESSAGE_ACKNOWLEDGEMENT_OUT.QUE";
			
}
