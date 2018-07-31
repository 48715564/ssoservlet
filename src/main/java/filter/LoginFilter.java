package filter;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import common.Constant;
import common.UrlUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LoginFilter implements Filter {
    private static TimedCache<String, Object> timedCache = CacheUtil.newTimedCache(1000 * 60 * 60 * 24 * 2);

    public static TimedCache<String, Object> getTimedCache() {
        return timedCache;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String filePath = filterConfig.getInitParameter("oauthConfigLocation");
        if (filePath == null || "".equals(filePath)) {
            filePath = "oauthConfig.properties";
        }
        String basePath = this.getClass().getResource("/").getPath() + "/" + filePath;
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(basePath))) {
            properties.load(bufferedReader);
            Constant.clientId = properties.getProperty("clientId");
            Constant.clientSecret = properties.getProperty("clientSecret");
            Constant.redirectUri = properties.getProperty("redirectUri");
            Constant.userAuthorizationUri = properties.getProperty("userAuthorizationUri");
            Constant.accessTokenUri = properties.getProperty("accessTokenUri");
            Constant.userInfoUri = properties.getProperty("userInfoUri");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }


    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (request.getSession().getAttribute("userinfo") == null) {
            request.getSession().setAttribute("request", request);
            request.getSession().setAttribute("response", response);
            request.getSession().setAttribute("url", UrlUtils.buildFullRequestUrl(request));
            response.sendRedirect(Constant.userAuthorizationUri + "?response_type=code&client_id=" + Constant.clientId + "&scope=all&redirect_uri=" + Constant.redirectUri);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
