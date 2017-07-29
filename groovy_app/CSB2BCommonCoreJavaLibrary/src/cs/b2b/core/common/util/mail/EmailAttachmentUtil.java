package cs.b2b.core.common.util.mail;

import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import cs.b2b.core.common.util.HandlerUtil;

public class EmailAttachmentUtil {
	
	public String SendEmail(String[] runtimeParams, String subject, String body, List<byte[]> attachImages) throws Exception {
		String serverUrl = HandlerUtil.getHandlerParameter("emailServer", runtimeParams);
		String emailPort = HandlerUtil.getHandlerParameter("emailPort", runtimeParams);
		String emailUser = HandlerUtil.getHandlerParameter("emailUser", runtimeParams);
		String emailPsw = HandlerUtil.getHandlerParameter("emailPsw", runtimeParams);
		String emailFrom = HandlerUtil.getHandlerParameter("emailFrom", runtimeParams);
		String emailTo = HandlerUtil.getHandlerParameter("emailTo", runtimeParams);
		String emailCC = HandlerUtil.getHandlerParameter("emailCC", runtimeParams);
		String envName = HandlerUtil.getHandlerParameter("envName", runtimeParams);
		
		Properties prop = new Properties();
		prop.setProperty("mail.host", serverUrl);
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.auth", "true");
		if (emailPort!=null && emailPort.trim().length()>0) {
			prop.setProperty("mail.smtp.port", String.valueOf(emailPort));
		}
		
		Session session = Session.getInstance(prop);
		session.setDebug(false);
		Transport ts = session.getTransport();
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(emailFrom));
		
		//set CC receiver
		if (emailCC.contains(",")) {
			emailCC = emailCC.replace(",", ";");
		}
		if (emailCC.contains(";")) {
			String[] ccRecvs = emailCC.split(";");
			for(int ir=0; ccRecvs!=null && ir<ccRecvs.length; ir++) {
				String recv = ccRecvs[ir];
				if (recv.trim().length()>0) {
					message.setRecipient(Message.RecipientType.CC, new InternetAddress(recv));
				}
			}
		} else if (emailCC!=null && emailCC.trim().length()>0) {
			message.setRecipient(Message.RecipientType.CC, new InternetAddress(emailCC));
		}
		
		if (emailTo.contains(",")) {
			emailTo = emailTo.replace(",", ";");
		}
		if (emailTo.contains(";")) {
			String[] toRecvs = emailTo.split(";");
			for(int ir=0; toRecvs!=null && ir<toRecvs.length; ir++) {
				String recv = toRecvs[ir];
				if (recv.trim().length()>0) {
					message.setRecipient(Message.RecipientType.TO, new InternetAddress(recv));
				}
			}
		} else if (emailTo!=null && emailTo.trim().length()>0) {
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
		}
		
		//checking recvers
		Address[] toAdds = message.getRecipients(Message.RecipientType.TO);
		Address[] ccAdds = message.getRecipients(Message.RecipientType.CC);
		if ((toAdds==null || toAdds.length==0) && (ccAdds==null || ccAdds.length==0)) {
			throw new Exception("TO and CC email address is empty.");
		}
		
		//message.setContent(body, "text/html;charset=UTF-8");
		
		//
        // This HTML mail have to 2 part, the BODY and the embedded image
        //
        MimeMultipart multipart = new MimeMultipart("related");

        // first part  (the html)
        BodyPart messageBodyPart = new MimeBodyPart();
//        String htmlText = "<H1>"+body+"</H1><br><br>File Volume: <br>"
//        		+ "<img src='cid:IMG1'><br><br>File Size:<br>"
//        		+ "<img src='cid:IMG2'><br><br>Total file summary:<br>testing.<br>";
        messageBodyPart.setContent(body, "text/html");
		
        multipart.addBodyPart(messageBodyPart);
        
        for(int ia=0; attachImages!=null && ia<attachImages.size(); ia++) {
        	if (attachImages.get(ia) != null) {
        		BodyPart attach = new MimeBodyPart();
            	ByteArrayDataSource bas1 = new ByteArrayDataSource(attachImages.get(ia), "application/octet-stream");
            	attach.setDataHandler(new DataHandler(bas1));
//            	attach.setFileName("1.jpg");
            	attach.setHeader("Content-ID","IMG"+(ia+1));
            	multipart.addBodyPart(attach);
            }
        }
        
        message.setSubject("("+envName+") "+subject);
        message.setContent(multipart);
        
        ts.connect(serverUrl, emailUser, emailPsw);
		ts.sendMessage(message, message.getAllRecipients());
		ts.close();
		
		return "email sent.";
	}

}
