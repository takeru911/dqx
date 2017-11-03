package com.takeru.dqx.crawl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class Utils {

    public static String getRequest(HttpClient httpClient, String url) throws IOException{
        String html = "";
        HttpGet getMethod = new HttpGet(url);

        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(getMethod)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                html = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return html;
            }
        }
        return html;
    }

    static String postRequest(HttpClient client, String url, HttpEntity postEntity) throws IOException{
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(postEntity);
        try(CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httpPost)){
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        }
    }

    static CookieStore getLoggedInCookie() throws IOException{
        UserAuth userAuth = UserAuth.getInstance();
        Map<String, Map> userInfo = loadUserAuthInfo();
        Map<String, String> user = userInfo.get("auth");
        return userAuth.getLoggedinCookie(user.get("userId"), user.get("userPassword"), user.get("characterName"));
    }

    static Map<String, Map> loadUserAuthInfo() throws IOException{
        final String confFilePath = Utils.class.getClassLoader().getResource("conf/Config.json").getPath();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(confFilePath),Map.class);
    }

    public static Document convertUrl2JsoupDocument(String url, CookieStore cookie) throws IOException, InterruptedException{
        try(CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setDefaultCookieStore(cookie)
                .build()
        ){
            HttpGet get = new HttpGet(url);
            try(CloseableHttpResponse response = httpClient.execute(get)){
                if(response.getStatusLine().getStatusCode() == 403){
                    Thread.sleep(200);
                    return convertUrl2JsoupDocument(url, cookie);
                }
                String html = EntityUtils.toString(response.getEntity());

                return Jsoup.parse(html);
            }

        }
    }
}
