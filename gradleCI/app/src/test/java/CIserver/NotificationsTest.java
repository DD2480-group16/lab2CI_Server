package CIserver;

import org.junit.Test;
import static org.junit.Assert.*;

public class NotificationsTest {
    @Test 
    public void testNotif_1() { // Working exemple
        assertTrue(Notifications.send(
            "dd2480group16@gmail.com",
            "6N9vpRzqZtY6rK9",
            "dd2480group16@gmail.com",
            "Server Statut",
            "Test 1"
          ));
    }
    @Test 
    public void testNotif_2() {// With the wrong password
        assertFalse(Notifications.send(
            "dd2480group16@gmail.com",
            "6N9vpRzqZtY6rK",
            "dd2480group16@gmail.com",
            "Server Statut",
            "Test 2"
          ));
    }

    @Test 
    public void testNotif_3() {// With the wrong email adress
        assertFalse(Notifications.send(
            "dd2480group@gmail.com",
            "6N9vpRzqZtY6rK9",
            "dd2480group16@gmail.com",
            "Server Statut",
            "Test 3"
          ));
    }
}
