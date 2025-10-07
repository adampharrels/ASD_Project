package uni.space.finder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.*;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "EditUserInfoServlet", urlPatterns = {"/EditUserInfoServlet"})
public class EditUserInfoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.getWriter().write("{\"success\":false,\"error\":\"Not logged in\"}");
            return;
        }
        String email = (String) session.getAttribute("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        // Read all accounts
        File file = new File("accounts.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        boolean updated = false;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 6 && parts[1].equals(email)) {
                if (firstName != null && !firstName.isEmpty()) parts[3] = firstName;
                if (lastName != null && !lastName.isEmpty()) parts[4] = lastName;
                updated = true;
                line = String.join(",", parts);
            }
            sb.append(line).append("\n");
        }
        reader.close();
        // Write back updated accounts
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(sb.toString());
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", updated);
        if (!updated) resp.put("error", "User not found");
        response.getWriter().write(new Gson().toJson(resp));
    }
}
