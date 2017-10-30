package com.takeru.dqx.crwal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


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

    public static String postRequest(HttpClient client, String url, HttpEntity postEntity) throws IOException{
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(postEntity);
        try(CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httpPost)){
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        }
    }
}
