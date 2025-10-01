package uni.space.finder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import com.google.gson.Gson;

@WebServlet("/api/signup")
public class SignupServlet extends HttpServlet {
    // Add CORS headers to all responses
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        BufferedReader reader = req.getReader();
        Gson gson = new Gson();
        SignupData data = gson.fromJson(reader, SignupData.class);
        reader.close();

        // Basic validation
        if (data == null || data.email == null || data.password == null || !data.email.endsWith("@student.uts.edu.au")) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Invalid input\"}");
            return;
        }

        boolean created = Account.createAccount(data.email, data.password, data.first, data.last, data.sid);
        if (created) {
            resp.getWriter().write("{\"success\":true}");
        } else {
            resp.getWriter().write("{\"success\":false,\"message\":\"Account already exists\"}");
        }
    }

    private static class SignupData {
        String first;
        String last;
        String email;
        String sid;
        String password;
    }
}
