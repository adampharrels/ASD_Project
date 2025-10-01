package uni.space.finder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import org.json.JSONObject;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/ProfileServlet"})
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.getWriter().write("{\"success\":false,\"error\":\"Not logged in\"}");
            return;
        }
        String email = (String) session.getAttribute("email");
    // Read accounts.txt from the correct location
    File file = new File("app/accounts.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        JSONObject result = new JSONObject();
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 6 && parts[1].equals(email)) {
                result.put("success", true);
                result.put("id", parts[0]);
                result.put("email", parts[1]);
                result.put("firstName", parts[3]);
                result.put("lastName", parts[4]);
                result.put("studentNumber", parts[5]);
                found = true;
                break;
            }
        }
        reader.close();
        if (!found) {
            result.put("success", false);
            result.put("error", "User not found");
        }
        response.getWriter().write(result.toString());
    }
}
