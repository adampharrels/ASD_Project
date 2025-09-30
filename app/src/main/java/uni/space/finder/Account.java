package uni.space.finder;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Account {
    int id;
    String email;
    String password;
    String first;
    String last;
    String sid;

    private static List<Account> accounts = new ArrayList<>();
    private static int nextId = 0;
    private static final String FILE_PATH = "accounts.txt";

    public Account() {
        id = 0;
        email = "";
        password = "";
        first = "";
        last = "";
        sid = "";
    }

    public Account(int id, String email, String password, String first, String last, String sid) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.first = first;
        this.last = last;
        this.sid = sid;
    }

    // Create account if not exists, save to file, return true if created
    public static boolean createAccount(String email, String password, String first, String last, String sid) {
        loadAccounts();
        for (Account a : accounts) {
            if (a.email.equalsIgnoreCase(email)) {
                return false; // already exists
            }
        }
        Account acc = new Account(nextId++, email, password, first, last, sid);
        accounts.add(acc);
        saveAccounts();
        System.out.println("Account created for " + acc.email);
        return true;
    }

    // Load accounts from file
    private static void loadAccounts() {
        accounts.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 6) {
                    accounts.add(new Account(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }
            nextId = accounts.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save accounts to file
    private static void saveAccounts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Account a : accounts) {
                bw.write(a.id + "," + a.email + "," + a.password + "," + a.first + "," + a.last + "," + a.sid + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Login method
    public static boolean login(String email, String password) {
        loadAccounts();
        for (Account a : accounts) {
            if (a.email.equalsIgnoreCase(email) && a.password.equals(password)) {
                System.out.println("Welcome back " + email);
                return true;
            }
        }
        return false;
    }
}
