package uni.space.finder;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class ProfileAccountTest {
    @BeforeEach
    void setup() throws IOException {
        // Prepare a test accounts.txt file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("accounts.txt"))) {
            bw.write("0,testuser@example.com,pass123,Test,User,123456\n");
        }
    }

    @Test
    void testLoadAccountsAndRetrieveUser() {
        // Load accounts and check retrieval
        Account acc = null;
        // Load accounts
        try {
            java.lang.reflect.Method m = Account.class.getDeclaredMethod("loadAccounts");
            m.setAccessible(true);
            m.invoke(null);
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
        // Check if user exists
        boolean found = false;
        for (Account a : getAccountsList()) {
            if (a.email.equals("testuser@example.com") && a.first.equals("Test") && a.last.equals("User")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "User should be loaded from accounts.txt");
    }

    // Helper to access private static accounts list
    private static java.util.List<Account> getAccountsList() {
        try {
            java.lang.reflect.Field f = Account.class.getDeclaredField("accounts");
            f.setAccessible(true);
            return (java.util.List<Account>) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
