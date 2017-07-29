package cs.b2b.core.common.util.mail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import cs.b2b.core.common.util.LocalFileUtil;

public class EmailReceiver {
	
	public static void main(String[] args) throws Exception {
		try {
			receive();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	 /** 
     * receive email
     */
    private static void receive() throws Exception {
        //session for connect email  
        Properties props = new Properties();  
//        props.setProperty("mail.store.protocol", "pop3");  
//        props.setProperty("mail.pop3.port", "110");  
//        props.setProperty("mail.pop3.host", "HKGOWA");  
        
        
      String mailServerAddress = "pending";
      String mailUser = "";
      String mailUserPsw = "";
      
      // create email session  
      Session session = Session.getInstance(props);  
      Store store = session.getStore("pop3");  
      store.connect(mailServerAddress, 110, mailUser, mailUserPsw);  
      
      
      // get inbox
      Folder folder = store.getFolder("INBOX");  
      /* Folder.READ_ONLY: read only 
       * Folder.READ_WRITE: read and write, can modify email status 
       */
      folder.open(Folder.READ_WRITE); //open mailbox  
        
      // pop3 cannot get mail status, getUnreadMessageCount is email total number  
      System.out.println("unread email count: " + folder.getUnreadMessageCount());  
        
      // pop3 cannot get email status, below is 0  
      System.out.println("del email count: " + folder.getDeletedMessageCount());  
      System.out.println("new email count: " + folder.getNewMessageCount());  
          
      System.out.println("email total count: " + folder.getMessageCount());  
        
      // get all email and parse  
      Message[] messages = folder.getMessages();
      
      parseMessage(messages);
        
      //release resource  
      folder.close(true);
      store.close();  
    }  
    
    /** 
     * parse email
     * @param messages email list 
     */  
    private static void parseMessage(Message[] messages) throws MessagingException, IOException {  
        if (messages == null || messages.length < 1) {
        	//no email found
            return;
        }
          
        //parse all email
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HHmmssSSS_");
        String localFolder = "C:\\emailTemp\\";
        
        for (int i = 0, count = messages.length; i < count; i++) {
        	String msgbusId = sdf.format(new Date());//+UniqueKeyGenerator.generateShortUUID();
        	
            MimeMessage msg = (MimeMessage) messages[i];  
            System.out.println("------------------reading " + msg.getMessageNumber() + " email-------------------- ");  
            System.out.println("subject : " + getSubject(msg));  
            System.out.println("sender  : " + getFrom(msg));  
            System.out.println("receiver: " + getReceiveAddress(msg, null));  
            System.out.println("Sent at : " + getSentDate(msg, null));  
            System.out.println("is read : " + isSeen(msg));  
            System.out.println("priority: " + getPriority(msg));  
            System.out.println("need ack: " + isReplySign(msg));  
            System.out.println("mailsize: " + msg.getSize() * 1024 + " kb");  
            boolean isContainerAttachment = isContainAttachment(msg);  
            System.out.println("has Atta: " + isContainerAttachment);  
            if (isContainerAttachment) {  
                saveAttachment(msg, localFolder + msgbusId+"__"); //save attachment  
            }
            StringBuffer content = new StringBuffer(30);  
            getMailTextContent(msg, content);  
            System.out.println("email body: " + (content.length() > 100 ? content.substring(0,100) + "..." : content));
            try {
            	LocalFileUtil.writeToFile(localFolder + msgbusId + ".txt", content.toString(), false);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
            System.out.println("------------------reading " + msg.getMessageNumber() + " finished-------------------- ");  
            System.out.println();
            
            msg.setFlag(Flags.Flag.DELETED, true);
        }
    }  
      
    /** 
     * get email subject
     * @param msg - email body 
     * @return email subject 
     */  
    private static String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {  
        return MimeUtility.decodeText(msg.getSubject());  
    }  
      
    /** 
     * get receiver 
     * @param msg - email body 
     * @return address <Email address> 
     * @throws MessagingException 
     * @throws UnsupportedEncodingException  
     */  
    private static String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {  
        String from = "";  
        Address[] froms = msg.getFrom();  
        if (froms.length < 1)  
            throw new MessagingException("no receiver!");  
          
        InternetAddress address = (InternetAddress) froms[0];  
        String person = address.getPersonal();  
        if (person != null) {  
            person = MimeUtility.decodeText(person) + " ";  
        } else {  
            person = "";  
        }  
        from = person + "<" + address.getAddress() + ">";  
          
        return from;  
    }  
      
    /** 
     * base on receiver type, get receiver address, if type is empty, get all receiver list 
     * <p>Message.RecipientType.TO</p> 
     * <p>Message.RecipientType.CC</p> 
     * <p>Message.RecipientType.BCC</p> 
     * @param msg - email body
     * @param type receiver number 
     * @return receiver 1 <email address 1>, receiver 2 <email address 2>, ... 
     * @throws MessagingException 
     */  
    private static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {  
        StringBuffer receiveAddress = new StringBuffer();  
        Address[] addresss = null;  
        if (type == null) {  
            addresss = msg.getAllRecipients();  
        } else {  
            addresss = msg.getRecipients(type);  
        }
          
        if (addresss == null || addresss.length < 1)  
            throw new MessagingException("no receiver!");  
        for (Address address : addresss) {  
            InternetAddress internetAddress = (InternetAddress)address;  
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");  
        }  
          
        receiveAddress.deleteCharAt(receiveAddress.length()-1); //delete the last comma  
          
        return receiveAddress.toString();  
    }  
      
    /** 
     * email sent time
     * @param msg - email body 
     * @return yyyy-MM-dd HH:mm:ss 
     * @throws MessagingException 
     */  
    private static String getSentDate(MimeMessage msg, String pattern) throws MessagingException {  
        Date receivedDate = msg.getSentDate();  
        if (receivedDate == null)  
            return "";  
          
        if (pattern == null || "".equals(pattern))  
            pattern = "yyyy-MM-dd HH:mm:ss";  
          
        return new SimpleDateFormat(pattern).format(receivedDate);  
    }  
      
    /** 
     * email contains attachment or not
     * @param msg - email body 
     * @return contains attachment: true, or else return: false 
     * @throws MessagingException 
     * @throws IOException 
     */  
    private static boolean isContainAttachment(Part part) throws MessagingException, IOException {  
        boolean flag = false;  
        if (part.isMimeType("multipart/*")) {  
            MimeMultipart multipart = (MimeMultipart) part.getContent();  
            int partCount = multipart.getCount();  
            for (int i = 0; i < partCount; i++) {  
                BodyPart bodyPart = multipart.getBodyPart(i);  
                String disp = bodyPart.getDisposition();  
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {  
                    flag = true;  
                } else if (bodyPart.isMimeType("multipart/*")) {  
                    flag = isContainAttachment(bodyPart);  
                } else {  
                    String contentType = bodyPart.getContentType();  
                    if (contentType.indexOf("application") != -1) {  
                        flag = true;  
                    }    
                      
                    if (contentType.indexOf("name") != -1) {  
                        flag = true;  
                    }   
                }  
                  
                if (flag) break;  
            }  
        } else if (part.isMimeType("message/rfc822")) {  
            flag = isContainAttachment((Part)part.getContent());  
        }  
        return flag;  
    }  
      
    /**  
     * check if email is read or unread
     * @param msg - email body  
     * @return if email is read then: true, else then: false  
     * @throws MessagingException   
     */  
    private static boolean isSeen(MimeMessage msg) throws MessagingException {  
        return msg.getFlags().contains(Flags.Flag.SEEN);  
    }  
      
    /** 
     * check if need email receipt ack 
     * @param msg - email body 
     * @return need ack: true, no need ack: false 
     * @throws MessagingException 
     */  
    private static boolean isReplySign(MimeMessage msg) throws MessagingException {  
        boolean replySign = false;  
        String[] headers = msg.getHeader("Disposition-Notification-To");  
        if (headers != null)  
            replySign = true;  
        return replySign;  
    }  
      
    /** 
     * email priority
     * @param msg - email body 
     * @return 1: High urgent, 3: Normal, 5: Low 
     * @throws MessagingException  
     */  
    private static String getPriority(MimeMessage msg) throws MessagingException {  
        String priority = "normal";  
        String[] headers = msg.getHeader("X-Priority");  
        if (headers != null) {  
            String headerPriority = headers[0];  
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)  
                priority = "urgent";  
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)  
                priority = "low";  
            else  
                priority = "normal";  
        }  
        return priority;  
    }   
      
    /** 
     * get mail context
     * @param part - email part 
     * @param content email body text 
     * @throws MessagingException 
     * @throws IOException 
     */  
    private static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {  
        //if body text is text attachment, then use getContent to get file content, but this is not we want, so need a charge here
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;   
        if (part.isMimeType("text/*") && !isContainTextAttach) {  
            content.append(part.getContent().toString());  
        } else if (part.isMimeType("message/rfc822")) {   
            getMailTextContent((Part)part.getContent(),content);  
        } else if (part.isMimeType("multipart/*")) {  
            Multipart multipart = (Multipart) part.getContent();  
            int partCount = multipart.getCount();  
            for (int i = 0; i < partCount; i++) {  
                BodyPart bodyPart = multipart.getBodyPart(i);  
                getMailTextContent(bodyPart,content);  
            }  
        }  
    }  
      
    /**  
     * save attachment
     * @param part - one of email part  
     * @param destDir  - save to local folder  
     * @throws UnsupportedEncodingException  
     * @throws MessagingException  
     * @throws FileNotFoundException  
     * @throws IOException  
     */  
    private static void saveAttachment(Part part, String destDir) throws UnsupportedEncodingException, MessagingException,  
            FileNotFoundException, IOException {  
        if (part.isMimeType("multipart/*")) {  
            Multipart multipart = (Multipart) part.getContent();    //complex email body  
            //complex email body contains multiple part
            int partCount = multipart.getCount();  
            for (int i = 0; i < partCount; i++) {  
                //get one of part  
                BodyPart bodyPart = multipart.getBodyPart(i);  
                //email part may be a complex part too  
                String disp = bodyPart.getDisposition();  
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {  
                    InputStream is = bodyPart.getInputStream();  
                    saveFile(is, destDir, decodeText(bodyPart.getFileName()));  
                } else if (bodyPart.isMimeType("multipart/*")) {  
                    saveAttachment(bodyPart, destDir);  
                } else {  
                    String contentType = bodyPart.getContentType();  
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {  
                        saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));  
                    }  
                }  
            }  
        } else if (part.isMimeType("message/rfc822")) {  
            saveAttachment((Part) part.getContent(), destDir);  
        }  
    }  
      
    /**  
     * save file to local   
     * @param is inputstream
     * @param fileName 
     * @param destDir - save folder  
     * @throws FileNotFoundException  
     * @throws IOException  
     */  
    private static void saveFile(InputStream is, String destDir, String fileName)  
            throws FileNotFoundException, IOException {  
        BufferedInputStream bis = new BufferedInputStream(is);  
        BufferedOutputStream bos = new BufferedOutputStream(  
                new FileOutputStream(new File(destDir + fileName)));  
        int len = -1;  
        while ((len = bis.read()) != -1) {  
            bos.write(len);  
            bos.flush();  
        }  
        bos.close();  
        bis.close();  
    }  
      
    /** 
     * content decode
     * @param encodeText get text from decode of MimeUtility.encodeText(String text) 
     * @return decode text 
     * @throws UnsupportedEncodingException 
     */  
    private static String decodeText(String encodeText) throws UnsupportedEncodingException {  
        if (encodeText == null || "".equals(encodeText)) {  
            return "";  
        } else {  
            return MimeUtility.decodeText(encodeText);  
        }  
    }  
}
