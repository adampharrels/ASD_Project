import Java.util.ArrayList;
import Java.util.List;

public class Account {
    int id;
    String username;
    String password;

    private static List<Account> accounts = new ArrayList<>();
    private static int nextId = 0;

    public Account() {
        id = 0;
        username = "";
        password = "";
    }

    public void createAccount(String username, String password) {
        Account acc = new Account(nextId, username, password);
        nextId++;
        accounts.add(acc);
        System.out.println("Account created for " + acc.username);
    }

    public

    public Account login(String username, String password) {
        for (Account a : accounts) {
            if (a.username.equalsIgnoreCase(username) && a.password.equalsIgnoreCase(password)) {
                System.out.println("Welcome back " + username);
                return true;
            }
        }
        return false;
    }
}
