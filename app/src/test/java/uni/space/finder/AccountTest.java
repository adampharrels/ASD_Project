package uni.space.finder;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    @Test
    void testCreateAndLoginAccount() {
        // Use a unique email to avoid collision
        String email = "testuser" + System.currentTimeMillis() + "@student.uts.edu.au";
        String password = "TestPass123!";
        String first = "Test";
        String last = "User";
        String sid = "999999";

        // Should create account successfully
        assertTrue(Account.createAccount(email, password, first, last, sid));
        // Should not create duplicate
        assertFalse(Account.createAccount(email, password, first, last, sid));
        // Should login with correct credentials
        assertTrue(Account.login(email, password));
        // Should not login with wrong password
        assertFalse(Account.login(email, "wrongpass"));
    }
}
