

package uni.space.finder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;

public class ChangePasswordServlet extends HttpServlet {
    private static final String ACCOUNTS_FILE = "/Users/khuuthoainguyen/Documents/GitHub/ASD_Project/app/accounts.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject result = new JSONObject();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) sb.append(line);
            JSONObject body = new JSONObject(sb.toString());
            String email = body.optString("email");
            String current = body.optString("current");
            String newpw = body.optString("newpw");
            if (email.isEmpty() || current.isEmpty() || newpw.isEmpty()) {
                result.put("success", false);
                result.put("message", "Missing fields");
                response.getWriter().write(result.toString());
                return;
            }
            List<String> lines = Files.readAllLines(Paths.get(ACCOUNTS_FILE));
            boolean updated = false;
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length < 3) continue;
                // Debug log: print each line being checked
                System.out.println("Checking: " + Arrays.toString(parts));
                if (parts[1].equals(email) && parts[2].equals(current)) {
                    System.out.println("Match found for email: " + email);
                    parts[2] = newpw;
                    lines.set(i, String.join(",", parts));
                    updated = true;
                    break;
                }
            }
            if (updated) {
                try {
                    Files.write(Paths.get(ACCOUNTS_FILE), lines);
                    System.out.println("accounts.txt updated for " + email);
                    result.put("success", true);
                } catch (Exception writeEx) {
                    System.out.println("Error writing accounts.txt: " + writeEx.getMessage());
                    result.put("success", false);
                    result.put("message", "Failed to update file: " + writeEx.getMessage());
                }
            } else {
                System.out.println("No match for email/password. Email: " + email + ", Password: " + current);
                result.put("success", false);
                result.put("message", "Current password incorrect or email not found");
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            result.put("success", false);
            result.put("message", "Server error: " + ex.getMessage());
        }
        response.getWriter().write(result.toString());
    }
}

