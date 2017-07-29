package cs.b2b.core.mapping.api.autori;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.tibco.tibjms.TibjmsQueueConnectionFactory;

import cs.b2b.core.common.util.LocalFileUtil;

public class CS2ShootFile {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss");
	static int sendCount = 0;
	
	public int shoot(String tpId, String messageType, String dirId, String envName, String testFileFolder) throws Exception {
		
		File ff = new File(testFileFolder);
		if (! ff.exists()) {
			throw new Exception("Test folder: "+testFileFolder+" is not found.");
		}
		File[] sendFiles = ff.listFiles();
		if (sendFiles==null || sendFiles.length==0) {
			throw new Exception("Send file is not available in folder: "+testFileFolder+".");
		}
		
		String serverUrl = Env.QA1_jmsServerUrl;
		String userName = Env.QA1_jmsUserName;
		String password = Env.QA1_jmsPassword;
		
		String sendToQueue = "";
		String replyToQueue = Env.ReplyToQueue;
		
		if (envName!=null && envName.equalsIgnoreCase("qa2")) {
			serverUrl = Env.QA2_jmsServerUrl;
			userName = Env.QA2_jmsUserName;
			password = Env.QA2_jmsPassword;
		}
		if (messageType==null || messageType.trim().length()==0) {
			throw new Exception("message type is invalid.");
		}
		if (dirId.equals("O")) {
			// message type assign QUEUE
			if (messageType.equalsIgnoreCase("bc")) {
				sendToQueue = Env.SendToBC;
			} else if (messageType.equalsIgnoreCase("bl")) {
				sendToQueue = Env.SendToBL;
			} else if (messageType.equalsIgnoreCase("br")) {
				sendToQueue = Env.SendToBR;
			} else if (messageType.equalsIgnoreCase("ct")) {
				sendToQueue = Env.SendToCT;
			} else if (messageType.equalsIgnoreCase("ss")) {
				sendToQueue = Env.SendToSS;
			} else if (messageType.equalsIgnoreCase("si")) {
				sendToQueue = Env.SendToSI;
			} else if (messageType.equalsIgnoreCase("ack")) {
				sendToQueue = Env.SendToACK;
			}
		} else {
			throw new Exception("only support outgoing edi now.");
		}
		
		QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(serverUrl);
		
		QueueConnection connection;
		
		connection = factory.createQueueConnection(userName, password);

		QueueSession session = connection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

		Queue queue = session.createQueue(sendToQueue);

		MessageProducer sender = session.createProducer(queue);

		int successSentCount = 0;
		
		for(File f : sendFiles) {
			String body = LocalFileUtil.readBigFile(f.getAbsolutePath());
			if (body!=null && body.trim().length()>0) {
				
				TextMessage message = session.createTextMessage();
				String countStr = sendCount + "";
				if (countStr.length()<2) {
					countStr = "0" + countStr;
				}
				String assId = "ARI-"+sdf.format(new Date())+"-"+sendCount;
				
				if (replyToQueue!=null && replyToQueue.trim().length()>0) {
					Queue rqueue = session.createQueue(replyToQueue);
					message.setJMSReplyTo(rqueue);
				}
				
				message.setText(body.trim());

				message.setStringProperty("TpId", tpId);
				message.setStringProperty("MsgType", messageType.toUpperCase());
				message.setBooleanProperty("JMS_TIBCO_PRESERVE_UNDELIVERED", true);
				message.setStringProperty("MsgRequestId", assId);
				
				sender.setDeliveryMode(DeliveryMode.PERSISTENT);
				message.setJMSType(messageType.toUpperCase());
				
				//sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				sender.setPriority(4);
				
				sender.send(message);
				session.commit();
				
				successSentCount++;
				
				println("-> "+successSentCount+", "+f.getName()+", sent: "+assId);
				
			}
		}
		
		session.close();
		connection.close();
		
		return successSentCount;
	}
	
	public static void println(String s) {
		System.out.println(s);
	}
}
