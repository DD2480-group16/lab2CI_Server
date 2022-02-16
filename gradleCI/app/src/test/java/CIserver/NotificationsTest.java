package CIserver;

import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;
import java.util.Properties;
import javax.mail.*;
import com.sun.mail.imap.protocol.FLAGS;
import java.util.Arrays;

public class NotificationsTest {
    @Test
    public void testNotif_1() throws MessagingException, IOException{ // Working exemple
        //Sends a test email and then removes it.
        Notifications sender = new Notifications();

        Properties p = new Properties();
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.socketFactory.port", "465");
        p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(p);

        Store store = session.getStore("imaps");
        store.connect("smtp.gmail.com", "dd2480group16@gmail.com","6N9vpRzqZtY6rK9");

        Folder inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_WRITE);

        Message[] mails = inbox.getMessages();
        int before = inbox.getMessageCount();

        sender.send("dd2480group16@gmail.com",
        "6N9vpRzqZtY6rK8",
        "dd2480group16@gmail.com",
        "Test 1",
        "Test 1");


        mails = inbox.getMessages();
        int after = inbox.getMessageCount();

        assertTrue(after-before >= 1);
        if(after-before == 1){
          assertTrue(mails[after-1].getSubject().equals("Test 1"));
          mails[after-1].setFlag(FLAGS.Flag.DELETED, true); //Removes the test email.

        }else{
          Message[] potentialmails = Arrays.copyOfRange(mails, before-1, after);
          int index = after-before-1;
          while(index > 0){
            if(potentialmails[index].getSubject().equals("Test 1")) break;
            index--;
          }
          assertTrue(index >= 0); //if index less than 0, the value doesn't exist.
          mails[before-1+index].setFlag(FLAGS.Flag.DELETED, true);
        }

        inbox.close(true);
        store.close();


    }
    @Test
    public void testNotif_2() {// With the wrong password
        assertFalse(Notifications.send(
            "dd2480group16@gmail.com",
            "6N9vpRzqZtY6rK",
            "dd2480group16@gmail.com",
            "Test 2",
            "Test 2"
          ));
    }

    @Test
    public void testNotif_3() {// With the wrong email adress
        assertFalse(Notifications.send(
            "dd2480group@gmail.com",
            "6N9vpRzqZtY6rK8",
            "dd2480group16@gmail.com",
            "Test 3",
            "Test 3"
          ));
    }
}
