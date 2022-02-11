package CIserver;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Properties;  
import javax.mail.*;  

public class NotificationsTest {
    @Test 
    public void testNotif_1() { // Working exemple
        assertTrue(Notifications.send(
            "dd2480group16@gmail.com",
            "6N9vpRzqZtY6rK9",
            "dd2480group16@gmail.com",
            "Test 1",
            "Test 1"
          ));
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
            "6N9vpRzqZtY6rK9",
            "dd2480group16@gmail.com",
            "Test 3",
            "Test 3"
          ));
    }
}
