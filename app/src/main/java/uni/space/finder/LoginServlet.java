package uni.space.finder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        BufferedReader reader = req.getReader();
        Gson gson = new Gson();
        Map<String, String> data = gson.fromJson(reader, Map.class);
        String email = data.get("email");
        String password = data.get("password");
        boolean success = Account.login(email, password);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (!success) {
            result.put("message", "Invalid email or password");
        }
            if (success) {
                req.getSession(true).setAttribute("email", email);
            }
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
