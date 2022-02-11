package CIserver;

import javax.mail.internet.*; 
import java.util.Properties;  
import javax.mail.*;  

public class Notifications {
    public static boolean send(String from,String pwd,String to,String sub,String msg){
        boolean test = false;
        //Properties
        Properties p = new Properties(); // Initialize property
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.socketFactory.port", "465");
        p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.port", "465");
        //Create a Session
        Session s = Session.getDefaultInstance(p,
          new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {// check mail adress and password
             return new PasswordAuthentication(from, pwd);
          }
        });
        //Create message
        try {
          MimeMessage m = new MimeMessage(s);
          m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
          m.setSubject(sub);
          m.setText(msg);
          //send Message
          Transport.send(m);
          System.out.println("Mail sent");
          test = true;
        }catch (AuthenticationFailedException e) {
          e.printStackTrace();
          test = false;
        } catch (MessagingException e) {
          e.printStackTrace();
          test = false;
        } 
    p.clear();
    return test;
      }
}
