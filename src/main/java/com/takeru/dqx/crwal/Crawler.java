package com.takeru.dqx.crwal;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Crawler {
    private final String logginURL ="https://secure.square-enix.com/oauth/oa/oauthlogin.send?client_id=dq_comm&response_type=code&svcgrp=Service_SEJ&retu=http%3A%2F%2Fhiroba.dqx.jp%2Fsc%2F&retl=dqx_p&redirect_uri=https%3A%2F%2Fsecure.dqx.jp%2Fsc%2Flogin%2Fexec%3Fp%3D0&facflg=1";

    public static void main(String[] args){
        Crawler crawl = new Crawler();
//        try {
            crawl.fetchStoredValue();
//        }catch(IOException e){
//            e.printStackTrace();
//        }
    }

    private boolean login(String userId, String userPassword) throws IOException {

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpClientContext httpContext = HttpClientContext.create();
            HttpPost httpPost = new HttpPost(logginURL);

            try(CloseableHttpResponse response = httpClient.execute(httpPost, httpContext)){
                HttpHost target = httpContext.getTargetHost();
                List<URI> redirectLocations = httpContext.getRedirectLocations();
                URI location = URIUtils.resolve(httpPost.getURI(), target, redirectLocations);
                System.out.println("Final HTTP location: " + location.toASCIIString());
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    System.out.println(response.getStatusLine().getStatusCode());
                    HttpEntity entity = response.getEntity();

                    System.out.println(EntityUtils.toString(entity, StandardCharsets.UTF_8));
                    System.out.println(response.getLocale());
                    System.out.println(Arrays.toString(response.getAllHeaders()));

                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return true;
    }

    private String fetchStoredValue(){
        final String url = "https://secure.square-enix.com/oauth/oa/oauthlogin?client_id=dq_comm&response_type=code&svcgrp=Service_SEJ&retu=http%3A%2F%2Fhiroba.dqx.jp%2Fsc%2F&retl=dqx_p&redirect_uri=https%3A%2F%2Fsecure.dqx.jp%2Fsc%2Flogin%2Fexec%3Fp%3D0&facflg=1";
        String html = Utils.getRequest(url);
        Document document = Jsoup.parse(html);
        Element element = Jsoup.parse(
                document.getElementsByClass("login-content-width")
                        .html()
        ).getElementById("loginForm");

        System.out.println(element.attr("_STORED_"));


        return html;
    }
}
