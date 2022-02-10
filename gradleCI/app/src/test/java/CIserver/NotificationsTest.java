package CIserver;

import org.junit.Test;
import static org.junit.Assert.*;

public class NotificationsTest {
    @Test 
    public void testNotif_1() { // Working exemple
        assertTrue(Notifications.send(
            "dd2480group16@gmail.com",
            "6N9vpRzqZtY6rK9",
            "teltcou@gmail.com",
            "Server Statut",
            "Test 1"
          ));
    }
}
