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


@WebServlet(name = "ProfileServlet", urlPatterns = {"/ProfileServlet"})
public class ProfileServlet extends HttpServlet {
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
       response.setContentType("application/json");
       HttpSession session = request.getSession(false);
       String email = null;
       if (session == null) {
           System.out.println("[ProfileServlet] No session found.");
       } else {
           Object attr = session.getAttribute("email");
           if (attr == null) {
               System.out.println("[ProfileServlet] Session found, but no email attribute.");
           } else {
               email = (String) attr;
               System.out.println("[ProfileServlet] Session email: " + email);
           }
       }
   // Read accounts.txt from the project/app root
   File file = new File("accounts.txt");
       BufferedReader reader = new BufferedReader(new FileReader(file));
       String line;
       Map<String, Object> result = new HashMap<>();
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
       Gson gson = new Gson();
       response.getWriter().write(gson.toJson(result));
   }
}



