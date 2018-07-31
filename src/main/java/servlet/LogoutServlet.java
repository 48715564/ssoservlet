package servlet;

import filter.LoginFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Vector;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String clientId = req.getParameter("clientId");
        String username = req.getParameter("username");
        String key = clientId+"_"+username;
        Vector<HttpSession> sessions = (Vector<HttpSession>) LoginFilter.getTimedCache().get(key);
        for(HttpSession session:sessions){
            session.invalidate();
        }
    }
}
