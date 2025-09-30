package uni.space.finder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import com.google.gson.*;


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
