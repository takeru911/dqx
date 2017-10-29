package com.takeru.dqx.crwal;


import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;

import java.util.ArrayList;

public class Crawler {
    private CookieStore cookieStore;

    public static void main(String[] args){
        Crawler crawl = new Crawler();
        try {
            crawl.login(args[0], args[1]);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public Crawler(){
        cookieStore = new BasicCookieStore();
    }

    private boolean login(String userId, String userPassword) throws IOException {
        String storedValue = fetchStoredValue();
        String cisSessId = fetchCisSessid(userId, userPassword, storedValue);

        try(CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setDefaultCookieStore(cookieStore)
                .build()){

            ArrayList postParameter = new ArrayList();
            postParameter.add(new BasicNameValuePair("cis_sessid", cisSessId));
            postParameter.add(new BasicNameValuePair("_c", "1"));

            Utils.postRequest(httpClient,
                    "https://secure.dqx.jp/sc/login/exec?p=0",
                    new UrlEncodedFormEntity(postParameter)
            );
        }catch(IOException e){
            e.printStackTrace();
        }
        try(CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setDefaultCookieStore(cookieStore)
                .build()
        ){
            String html = Utils.getRequest(httpClient, "http://hiroba.dqx.jp/sc/login/characterselect");
            System.out.println(html);
        }catch (IOException e){
            e.printStackTrace();
        }

        return true;
    }

    private String fetchCisSessid(String userId, String password, String storedValue) {
        final String loginURL ="https://secure.square-enix.com/oauth/oa/oauthlogin.send?client_id=dq_comm&response_type=code&svcgrp=Service_SEJ&retu=http%3A%2F%2Fhiroba.dqx.jp%2Fsc%2F&retl=dqx_p&redirect_uri=https%3A%2F%2Fsecure.dqx.jp%2Fsc%2Flogin%2Fexec%3Fp%3D0&alar=1";
        try (CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setDefaultCookieStore(cookieStore)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build()) {
            ArrayList postParameter = new ArrayList();
            postParameter.add(new BasicNameValuePair("sqexid", userId));
            postParameter.add(new BasicNameValuePair("password", password));
            postParameter.add(new BasicNameValuePair("_STORED_", storedValue));

            HttpEntity postEntity = new UrlEncodedFormEntity(postParameter);
            String html = Utils.postRequest(httpClient, loginURL, postEntity);
            return parseCisSessid(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String fetchStoredValue(){
        final String url = "https://secure.square-enix.com/oauth/oa/oauthlogin?client_id=dq_comm&response_type=code&svcgrp=Service_SEJ&retu=http%3A%2F%2Fhiroba.dqx.jp%2Fsc%2F&retl=dqx_p&redirect_uri=https%3A%2F%2Fsecure.dqx.jp%2Fsc%2Flogin%2Fexec%3Fp%3D0&facflg=1";
        try(CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultCookieStore(cookieStore)
                .build()){
            String html = Utils.getRequest(client, url);

            return parseStoredValue(html);
        }catch (IOException e){
            e.printStackTrace();
        }

        return "";
    }

    private String parseCisSessid(String html){
        Document document = Jsoup.parse(html);
        return document.body().tagName("cis_sessid").child(0).child(0).val();
    }

    private String parseStoredValue(String html){
        Document document = Jsoup.parse(html);
        Element element = Jsoup.parse(
                document.getElementsByClass("login-content-width")
                        .html());
        String storedValue = element.getElementById("loginForm").child(0).val();
        return storedValue;
    }
}
