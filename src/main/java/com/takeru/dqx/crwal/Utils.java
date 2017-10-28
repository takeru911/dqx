package com.takeru.dqx.crwal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class Utils {

    public static String getRequest(String url){
        String html = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getMethod = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(getMethod)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    html = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    return html;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }
}
