package uni.space.finder;
import java.util.ArrayList;
import java.util.List;

public class Account {
    int id;
    String username;
    String password;

    private static List<Account> accounts = new ArrayList<>();
    private static int nextId = 0;

    public Account(int id, String username, String password) {
        this.id = 0;
        this.username = "";
        this.password = "";
    }

    public static void createAccount(String username, String password) {
        Account acc = new Account(nextId, username, password);
        nextId++;
        accounts.add(acc);
        System.out.println("Account created for " + acc.username);
    }

    public static boolean verifyAccount(String username, String password) {
        for (Account a : accounts) {
            if (a.username.equalsIgnoreCase(username) && a.password.equalsIgnoreCase(password)) {
                System.out.println("Welcome back " + username);
                return true;
            }
        }
        return false;
    }

    public static Account login(String username, String password) {
        for (Account a : accounts) {
            if (a.username.equalsIgnoreCase(username) && a.password.equalsIgnoreCase(password)) {
                System.out.println("Welcome back " + username);
                return a;
            }
        }
        return null;
    }
}
