package common;

import cn.hutool.http.HttpRequest;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * oauthapi
 */
public class OAuthApi {
    private static final String AUTHORIZATION = "Authorization";

    private static String getFromBASE64(String s) {
        if (s == null) {
            return null;
        }
        BASE64Encoder encoder = new BASE64Encoder();
        try {
            String b = encoder.encode(s.getBytes());
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, String> getAuthorization(String clientId,String clientSecret) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(AUTHORIZATION, "Basic "+getFromBASE64(clientId+":"+clientSecret));
        return map;
    }

    public static String apiAccessToken(String clientId,String clientSecret,String accessTokenUri){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("grant_type", "client_credentials");
        return HttpRequest.post(accessTokenUri).addHeaders(getAuthorization(clientId,clientSecret)).form(paramMap).execute().body();
    }


    public static String userAccessToken(String clientId,String clientSecret,String accessTokenUri,String code,String redirect_uri){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("code", code);
        paramMap.put("redirect_uri", redirect_uri);
        return HttpRequest.post(accessTokenUri).addHeaders(getAuthorization(clientId,clientSecret)).form(paramMap).execute().body();
    }

    public static String userInfo(String url,String token){
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put(AUTHORIZATION, "Bearer "+token);
        return HttpRequest.get(url).addHeaders(headMap).execute().body();
    }
}
