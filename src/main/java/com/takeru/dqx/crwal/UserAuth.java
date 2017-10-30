package com.takeru.dqx.crwal;


import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserAuth {
    private CookieStore cookieStore;
    private boolean isLoggedin = false;
    private boolean isCharacterSelected = false;

    public static void main(String[] args){
        UserAuth auth = new UserAuth();
        try {
            auth.login(args[0], args[1]);
            auth.characterSelect(args[2]);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public UserAuth(){
        cookieStore = new BasicCookieStore();
    }

    CookieStore getLoggedinCookie(String userId, String userPassword, String userName) throws IOException{
        if(!isLoggedin){
            login(userId, userPassword);
        }
        if(!isCharacterSelected){
            characterSelect(userName);
        }

        return cookieStore;
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
            isLoggedin = true;
        }

        return isLoggedin;
    }

    private boolean characterSelect(String selectCharacterName) throws IOException{
        final String characterSelectUrl = "http://hiroba.dqx.jp/sc/login/characterselect/";
        final String execSelectUrl = "http://hiroba.dqx.jp/sc/login/characterexec";
        try (CloseableHttpClient httpClient = HttpClientBuilder
                .create()
             .setDefaultCookieStore(cookieStore)
             .build()
        ){
            String html = Utils.getRequest(httpClient, characterSelectUrl);
            String selectedCharacterRel = parseCharacterSelectRel(html, selectCharacterName);
            ArrayList postParameters = new ArrayList();
            postParameters.add(new BasicNameValuePair("cid", selectedCharacterRel));
            HttpEntity postEntity = new UrlEncodedFormEntity(postParameters);
            Utils.postRequest(httpClient, execSelectUrl, postEntity);
            isCharacterSelected = true;
        }
        return isCharacterSelected;
    }

    @org.jetbrains.annotations.NotNull
    private String fetchCisSessid(String userId, String password, String storedValue) throws IOException{
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
        }
    }

    @org.jetbrains.annotations.NotNull
    private String fetchStoredValue() throws IOException {
        final String url = "https://secure.square-enix.com/oauth/oa/oauthlogin?client_id=dq_comm&response_type=code&svcgrp=Service_SEJ&retu=http%3A%2F%2Fhiroba.dqx.jp%2Fsc%2F&retl=dqx_p&redirect_uri=https%3A%2F%2Fsecure.dqx.jp%2Fsc%2Flogin%2Fexec%3Fp%3D0&facflg=1";
        try (CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultCookieStore(cookieStore)
                .build()) {
            String html = Utils.getRequest(client, url);

            return parseStoredValue(html);
        }
    }

    @org.jetbrains.annotations.NotNull
    private String parseCisSessid(String html){
        Document document = Jsoup.parse(html);
        return document.body().tagName("cis_sessid").child(0).child(0).val();
    }

    @org.jetbrains.annotations.NotNull
    private String parseStoredValue(String html){
        Document document = Jsoup.parse(html);
        Element element = Jsoup.parse(
                document.getElementsByClass("login-content-width")
                        .html());
        String storedValue = element.getElementById("loginForm").child(0).val();
        return storedValue;
    }

    @org.jetbrains.annotations.NotNull
    private String parseCharacterSelectRel(String html, String selectCharacterName){
        Document document = Jsoup.parse(html);

        Elements selectedCharacterElements = document.getElementsByTag("tr");
        List<Element> selectedCharacterElementList = selectedCharacterElements
                .stream()
                .map(element -> element.getElementsMatchingText(Pattern.compile("^" + selectCharacterName + " ")))
                .filter(elements -> elements.size() > 0)
                .map(element    -> element.get(0))
                .collect(Collectors.toList());
        if(selectedCharacterElementList.size() == 0){
            throw new IllegalStateException("キャラクター名が誤っているっぽいです。");
        }
        Element selectedCharacterElement = selectedCharacterElementList.get(0);
        return selectedCharacterElement
                .select("td.btn_cselect")
                .select("a")
                .attr("rel");
    }
}
