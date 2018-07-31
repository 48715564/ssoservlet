package servlet;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import common.Constant;
import common.OAuthApi;
import filter.LoginFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Vector;

public class LoginServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //通过oauth2.0授权码模式获得access_token
        String code = request.getParameter("code");
        String jsonStr = OAuthApi.userAccessToken(Constant.clientId, Constant.clientSecret, Constant.accessTokenUri, code, Constant.redirectUri);
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        String access_token = jsonObject.getStr("access_token");
        //通过access_token获取用户信息，保存到session
        String userJson = OAuthApi.userInfo(Constant.userInfoUri, access_token);
        JSONObject userinfo = JSONUtil.parseObj(userJson);
        request.getSession().setAttribute("userinfo", userinfo);
        //保存登录信息
        String key = Constant.clientId + "_" + userinfo.get("username");
        if (LoginFilter.getTimedCache().containsKey(key)) {
            Vector<HttpSession> sessions = (Vector<HttpSession>) LoginFilter.getTimedCache().get(key);
            if (!sessions.contains(request.getSession())) {
                sessions.add(request.getSession());
                LoginFilter.getTimedCache().put(key, sessions);
            }
        } else {
            Vector<HttpSession> sessions = new Vector<HttpSession>();
            sessions.add(request.getSession());
            LoginFilter.getTimedCache().put(key, sessions);
        }
        if (request.getSession().getAttribute("url") != null) {
            HttpServletResponse rs = (HttpServletResponse) request.getSession().getAttribute("response");
            String url = (String) request.getSession().getAttribute("url");
            rs.sendRedirect(url);
            request.getSession().removeAttribute("url");
            request.getSession().removeAttribute("request");
            request.getSession().removeAttribute("response");
        } else {
            response.getWriter().print("登录成功！");
        }
    }
}
